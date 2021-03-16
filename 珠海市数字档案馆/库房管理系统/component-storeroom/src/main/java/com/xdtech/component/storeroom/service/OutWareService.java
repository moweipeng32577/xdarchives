package com.xdtech.component.storeroom.service;

import com.xdtech.component.DateTime.DateTimeUtils;
import com.xdtech.component.storeroom.entity.InWare_History;
import com.xdtech.component.storeroom.entity.OutWare;
import com.xdtech.component.storeroom.entity.OutWare_History;
import com.xdtech.component.storeroom.entity.Storage;
import com.xdtech.component.storeroom.repository.OutWareHistoryRepository;
import com.xdtech.component.storeroom.repository.OutWareRepository;
import com.xdtech.component.storeroom.repository.StorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 出库业务类
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/23.
 */
@Service
@Transactional
public class OutWareService {

    @Autowired
    private OutWareRepository outWareRepository;

    @Autowired
    private OutWareHistoryRepository outWareHistoryRepository;

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private StorageService storageService;
    /**
     * 获取出库编号
     * 格式为：199001010001，即年月日八位数字加四位数字序号
     * 当天没有出库记录，则序号为1
     * 如有出库记录，则获取最大序号加1为新序号
     * @return  出库编号
     */
    private synchronized String generateWarenum(){
        //获取最大的出库编号
        String max = outWareRepository.findMaxWarenum();
        //获取当前日期字符串
        String datestr = DateTimeUtils.getDateStr(new Date());
        int num = 0;
        //如果有当天出库记录，获取最大序号
        if(max != null && datestr.equals(max.substring(0,8))){
            num = Integer.parseInt(max.substring(8));
        }
        //生成出库编号字符串
        String result = datestr + String.format("%04d",num+1);
        return result;
    }

    /**
     * 出库记录检索
     * @param page      页号
     * @param limit     页大小
     * @return  出库记录的分页结果
     */
    public Page<OutWare_History> findAll(int page, int limit){
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
            List<OutWare_History> outWaresList = outWareHistoryRepository.findAll();
            return new PageImpl<OutWare_History>(outWaresList, pr, outWaresList.size());
        }
        String code=nickname.substring(0,nickname.indexOf("|"));
        String condition ="";
        if(code.startsWith("444")){//局档案室，需要过滤掉派出所节点
            code=code.substring(3);
            condition="派出所";
            return outWareHistoryRepository.findAllByJuPermision(code,condition,pr);
        }else{//派出所档案员或者部门档案员
            return outWareHistoryRepository.findAllByPermision(code,pr);
        }
    }

    /**
     * 出库
     * 包含调档出库
     * 出库同时会更新实体档案库存状态
     * @param outware 出库对象，包含关联的实体档案
     * @return  保存后的出库对象
     */
    public OutWare save(OutWare outware){
        //生成出库编号
        if(outware.getWarenum() == null || "".equals(outware.getWarenum())){
            outware.setWarenum(generateWarenum());
        }
        //设置出库时间
        outware.setWaretime(DateTimeUtils.getDateTimeStr(new Date()));
        //保存出库数据
        return entityManager.merge(outware);
    }

    /**
     * 批量删除出库记录
     * @param outids    主键ID字符串，多ID用','分隔
     */
    public void delete(String outids){
        outWareRepository.deleteAllByOutidIn(outids.split(","));
    }



    /**
     * 根据出库记录主键ID检索实体档案清单
     * @param outid 出库记录主键ID
     * @return  出库记录关联的实体档案集合
     */
    public Set<Storage> findStorageByOutware(String outid){
        OutWare outWare = outWareRepository.findWithStorageByOutid(outid);
        return outWare.getStorages();
    }

    /**
     * 根据档号获取相应出库记录
     * @param dhCode
     * @param pageRequest
     * @return
     */
    public  Page<OutWare_History> findOutWares(String dhCode, PageRequest pageRequest){
        List<OutWare_History> result=new ArrayList<>();
        if(dhCode.startsWith("id-")){//传的是entryid
            result = outWareHistoryRepository.findByEntryid(dhCode.substring(3));
        }else{
            result = outWareHistoryRepository.findByArchivecode(dhCode);
        }
        return new PageImpl(result, pageRequest,result.size());
    }

    public OutWare findOne(String outid){
        return outWareRepository.findOne(outid);
    }

    public Map save(String waretype, String ids, String description, String user,String borrowcodes) {
        OutWare outWare = new OutWare();
        outWare.setWaretype(waretype);
        outWare.setWareuser(user);
        outWare.setDescription(description);
        outWare.setBorrowcodes(borrowcodes);
        if(outWare.getStorages() == null){
            outWare.setStorages(new HashSet<Storage>());
        }
        for(String id : ids.split(",")){
            List<Storage> storages= storageRepository.getByEntry(id);
            Storage st=new Storage();
            if(storages.size()>0){
                st=storages.get(0);
            }else{
                Map map = new HashMap();
                map.put("zone", "false");//返回查找失败
                return map;
            }
            //Storage st = storageRepository.findByEntry(id);
            st.setStorestatus(Storage.STATUS_OUT);
            if("转递出库".equals(waretype)){//转递出库不用再归还，要把密集架的使用数去掉
                int useCapacity = st.getZoneShelves().getUsecapacity();
                st.getZoneShelves().setUsecapacity(--useCapacity);
                String borrowMsSql = "update tb_borrowmsg set state = '已归还' where lyqx = '查看' and state != '已归还' and entryid = :entryid";
                Query borrowMsgQuery = entityManager.createNativeQuery(borrowMsSql);
                borrowMsgQuery.setParameter("entryid", st.getEntry());
                borrowMsgQuery.executeUpdate();
                String entrySql = "update tb_entry_index set fscount = 0,kccount = 0 where entryid = :entryid";
                Query entryQuery = entityManager.createNativeQuery(entrySql);
                entryQuery.setParameter("entryid", st.getEntry());
                entryQuery.executeUpdate();
            }
            outWare.getStorages().add(st);
        }
        OutWare o =  this.save(outWare);

        //根据条目获取密集架存储信息
        Map<String ,String > map = storageService.findByEntryid(ids.split(",")[0]);
        return map;
    }

    /**
     * 根据出库单据获取条目ID
     * @param inid
     * @return
     */
    public  String[] findEntryIdsByOutid(String inid) {
        String[] entryids =outWareRepository.findEntryIdsByOutid(inid);
        return entryids;
    }

}
