package com.xdtech.component.storeroom.service;

import com.xdtech.component.DateTime.DateTimeUtils;
import com.xdtech.component.storeroom.entity.*;
import com.xdtech.component.storeroom.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 入库业务类
 * 包含实体档案入库、入库记录删除、历史记录查看操作
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/20.
 */
@Service
@Transactional
public class InWareService {

    @Autowired
    private InWareRepository inWareRepository;

    @Autowired
    private InWareHistoryRepository inWareHistoryRepository;

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private ZoneShelvesRepository zoneShelvesRepository;

    @Autowired
    ZonesRepository zonesRepository;

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 获取入库编号
     * 格式为：199001010001，即年月日八位数字加四位数字序号
     * 当天没有入库记录，则序号为1
     * 如有入库记录，则获取最大序号加1为新序号
     * @return  入库编号
     */
    private synchronized String generateWarenum(){
        //获取最大的入库编号
        String max = inWareRepository.findMaxWarenum();
        //获取当前日期字符串
        String datestr = DateTimeUtils.getDateStr(new Date());
        int num = 0;
        //如果有当天入库记录，获取最大序号
        if(max != null && datestr.equals(max.substring(0,8))){
            num = Integer.parseInt(max.substring(8));
        }
        //生成入库编号字符串
        String result = datestr + String.format("%04d",num+1);
        return result;
    }

    /**
     * 入库记录生成
     * 包含新增入库及归还入库
     * 入库同时会更新实体档案库存状态并更新架体存储量
     * @param inware 入库对象，包含关联的实体档案
     * @return  保存后的入库对象
     */
    public InWare save(InWare inware){
        //1.生成入库编号
        if(inware.getWarenum() == null || "".equals(inware.getWarenum())){
            inware.setWarenum(generateWarenum());
        }
        //2.设置入库时间
        inware.setWaretime(DateTimeUtils.getDateTimeStr(new Date()));
        //3.保存入库记录、包括插入实体档案数据
        //4.更新存储位置容量
        Map<String, Integer> map = new HashMap<String, Integer>();
        //先便利所有的实体档案的存储位置
        //计算每个单元格的使用增加量
        for(Storage storage : inware.getStorages()){
            if(storage.getStid() == null){
                if(map.containsKey(storage.getZoneShelves().getShid())){
                    map.put(storage.getZoneShelves().getShid(), map.get(storage.getZoneShelves().getShid()) + 1);
                }else{
                    map.put(storage.getZoneShelves().getShid(), 1);
                }
            }
        }
        //更新单元格使用量
        if(map.size() > 0){
            for (String shid : map.keySet()){
                ZoneShelves zoneShelves = zoneShelvesRepository.findOne(shid);
                zoneShelves.setUsecapacity(zoneShelves.getUsecapacity() + map.get(shid));
                entityManager.merge(zoneShelves);
            }
        }
        //5.保存入库记录
        return entityManager.merge(inware);
    }

    /**
     * 批量删除入库记录
     * @param inids 主键ID字符串，多ID用','分隔
     */
    public void delete(String inids){
        inWareRepository.deleteAllByInidIn(inids.split(","));
    }

    //更新条目存储位置
    public void updateStore(String shid, String ids){
        //重新标记档案系统的对应条目的库存位置,直接写了tb_intry_index
        String zsMsg="";
        String zoneMsg="";
        ZoneShelves target=zoneShelvesRepository.findOne(shid);
        zsMsg=target.getColdisplay()+"列-"+target.getSectiondisplay()+"节-"+target.getLayerdisplay()+"层-"+target.getSidedisplay();
        Zones zone=zonesRepository.findByShid(shid);
        zoneMsg=zone.getCitydisplay()+"-"+zone.getUnitdisplay()+"-"+zone.getRoomdisplay()+"-"+zone.getZonedisplay()+"-";
        String savePosition=zoneMsg+zsMsg;//详细移库位置
        //获取原单元格的所有的电子条目的entryid
        String[] entryids = ids.split(",");
        if(entryids.length>0){
            storageRepository.savePosition(entryids,savePosition);
        }
    }

    /**
     * 入库记录检索
     * @param page      页号
     * @param limit     页大小
     * @return  入库记录的分页结果
     */
    public Page<InWare_History> findAll(int page, int limit){
        //PageRequest pr = new PageRequest(page-1, limit);
        //判断排序类型及排序字段
        Sort sort = new Sort(Sort.Direction.DESC, "waretime");
        //获取pageable
        Pageable pr = new PageRequest(page-1,limit,sort);
        //从session获取用户名
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session=request.getSession();
        String nickname=(String)session.getAttribute("nickname");
        if(nickname==null){//非韶关
           List<InWare_History> inWaresList = inWareHistoryRepository.findAll();
           return new PageImpl<InWare_History>(inWaresList, pr, inWaresList.size());
        }
        String code=nickname.substring(0,nickname.indexOf("|"));
        String condition ="";
        if(code.startsWith("444")){//局档案室，需要过滤掉派出所节点
            code=code.substring(3);
            condition="派出所";
            return inWareHistoryRepository.findAllByJuPermision(code,condition,pr);
        }else{//派出所档案员或者部门档案员
            return inWareHistoryRepository.findAllByPermision(code,pr);
        }
    }

    /**
     * 根据入库记录主键ID检索实体档案清单
     * @param inid  入库记录主键ID
     * @return  入库记录关联的实体档案集合
     */
    public Set<Storage> findStorageByInware(String inid){
        InWare inWare = inWareRepository.findWithStorageByInid(inid);
        return inWare.getStorages();
    }

    /**
     * 根据档号获取相应入库记录
     * @param dhCode
     * @param
     * @return
     */
    public  /*Page<InWare>*/ Set<InWare> findInWares(String dhCode/*, PageRequest pageRequest*/){
        List<Storage> list=new ArrayList<>();
        if(dhCode.startsWith("id-")){//传的是entryid
            list=storageRepository.getByEntry(dhCode.substring(3));
        }else{
            list=storageRepository.findByArchivecode(dhCode);
        }

        Set<InWare> inwares=new HashSet<InWare>();
        if(list.size()>0){
            inwares=list.get(0).getInwares();
        }
        return inwares;
        //return inWareRepository.findByArchivecode(dhCode,pageRequest);
    }

    /**
     * 根据档号获取相应入库记录
     * @param dhCode
     * @param pageRequest
     * @return
     */
    public  Page<InWare_History> findInWares(String dhCode, PageRequest pageRequest){
        List<InWare_History> result=new ArrayList<>();
        if(dhCode.startsWith("id-")){//传的是entryid
            result = inWareHistoryRepository.findByEntryid(dhCode.substring(3));
        }else{
            result = inWareHistoryRepository.findByArchivecode(dhCode);
        }
        return new PageImpl(result, pageRequest,result.size());
    }

    public InWare findOne(String inid){
        return inWareRepository.findOne(inid);
    }

    /**
     * 根据入库单据获取条目ID
     * @param inid
     * @return
     */
    public  String[] findEntryIdsByinid(String inid) {
        String[] entryids =inWareRepository.findEntryIdsByinid(inid);
        return entryids;
    }

}