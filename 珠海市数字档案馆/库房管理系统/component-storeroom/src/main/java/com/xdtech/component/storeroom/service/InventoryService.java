package com.xdtech.component.storeroom.service;

import com.xdtech.component.DateTime.DateTimeUtils;
import com.xdtech.component.storeroom.entity.*;
import com.xdtech.component.storeroom.repository.InventoryRepository;
import com.xdtech.component.storeroom.repository.InventoryResultRepository;
import com.xdtech.component.storeroom.repository.StorageRepository;
import com.xdtech.component.storeroom.repository.ZoneShelvesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

/**
 * 实体档案盘点业务类
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/26.
 */
@Service
@Transactional
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryResultRepository inventoryResultRepository;

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private ZoneShelvesRepository zoneShelvesRepository;

    @Autowired
    ShelvesService shelvesService;

    /**
     * 获取盘点编号
     * 格式为：199001010001，即年月日八位数字加四位数字序号
     * 当天没有盘点记录，则序号为1
     * 如有盘点记录，则获取最大序号加1为新序号
     * @return  盘点编号
     */
    private synchronized String generateWarenum(){
        //获取最大的入库编号
        String max = inventoryRepository.findMaxWarenum();
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
     * 从文件中读取数据
     * 文件内容为一行一条数据
     * @param file  上传文件
     * @return  数据集合
     */
    private Set<String> getDataByFile(File file){
        Set<String> codeSet = new HashSet<String>();
        try {
            //BufferedReader reader = new BufferedReader(new FileReader(file));
            //WINDOWS创建的txt默认gbk格式
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "GBK");
            BufferedReader reader = new BufferedReader(isr);
            String tempstr = "";
            while ((tempstr = reader.readLine()) != null) {//每一行是一个档案编码,再加个位置具体信息,暂时是只有编码信息(类档号)
                codeSet.add(tempstr);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File Not Found");
        } catch (IOException e) {
            throw new RuntimeException("IO Exception");
        }
        return codeSet;
    }

    /**
     * 新增档案盘点批次
     * @param inventory 盘点对象
     * @param inventoryFile 上传的盘点文件
     * @return  保存后的盘点对象
     */
    //public Inventory save(Inventory inventory, File inventoryFile,String shid){
    public String save(Inventory inventory, File inventoryFile,String shid,String shidMsg){
        //1.生成盘点编号
        if(inventory.getChecknum() == null || "".equals(inventory.getChecknum())){
            inventory.setChecknum(generateWarenum());
        }
        //2.设置盘点时间
        inventory.setChecktime(DateTimeUtils.getDateTimeStr(new Date()));

        //3.设置盘点范围
        List<Storage> storagelist = new ArrayList<Storage>();
        String rangetype="";
        String zoneid="";
        if(shid.startsWith("room")){
            rangetype=Inventory.RANGE_TYPE_ROOM;
            String roommsg=shid.substring(shid.indexOf(":")+1);
            String room=roommsg.substring(roommsg.lastIndexOf(",")+1);
            String unit=roommsg.substring(roommsg.indexOf(",")+1,roommsg.lastIndexOf(","));
            String city=roommsg.substring(0,roommsg.indexOf(","));
            //按照库房查找下边所有的zoneshelves的shid，再去关联对应的st_storage的stid
            storagelist = storageRepository.findByShelves_Room(city,unit,room);
            if(storagelist.size()<1){//需要先获取zoneid
                List<Zones> zones=shelvesService.findZonesByRoom(city,unit,room);
                zoneid=zones.get(0).getZoneid();
            }
        }else if(shid.startsWith("zone")){
            rangetype=Inventory.RANGE_TYPE_ZONE;
            zoneid=shid.substring(shid.indexOf(":")+1);
            //按照zoneid查找下边所有的zoneshelves的shid，再去关联对应的st_storage的stid
            storagelist = storageRepository.findByShelves_zone(zoneid);
        }else if(shid.startsWith("col")){
            rangetype=Inventory.RANGE_TYPE_COL;
            zoneid=shid.substring(shid.indexOf(":")+1,shid.indexOf(","));
            String col=shid.substring(shid.indexOf(",")+1);
            //按照zoneid和列名查找下边所有的zoneshelves的shid，再去关联对应的st_storage的stid
            storagelist = storageRepository.findByShelves_zoneAndCol(zoneid,col);
        }
        inventory.setRangetype(rangetype);
        String des=inventory.getDescription();

        //4.读取上传的数据
        Set<String> codeSet = getDataByFile(inventoryFile);

        if(storagelist.size()<1){//盘点范围没有库存档案
            //保存保存盘点记录
            des=des+"-"+"盘点范围:"+shidMsg+"没有库存档案";
            inventory.setDescription(des);
            List<ZoneShelves> zsList=zoneShelvesRepository.findByZoneid(zoneid);
            inventory.setRange(zsList.get(0));//没有库存记录随意记一个shid作为位置权限标记
            inventory =  inventoryRepository.save(inventory);
            //return "0";
        }else{//盘点范围有库存档案
            String range=storagelist.get(0).getZoneShelves().getShid();
            ZoneShelves zs=zoneShelvesRepository.findOne(range);
            inventory.setRange(zs);

            if(codeSet.size()<1){//盘点清单没有数据
                //保存保存盘点记录
                des=des+"-"+"盘点清单没有数据";
                inventory.setDescription(des);
                inventory =  inventoryRepository.save(inventory);
                return "1";
            }else{//盘点清单有数据
                //保存盘点记录
                des=des+"-"+shidMsg;
                inventory.setDescription(des);
                inventory =  inventoryRepository.save(inventory);
            }
        }

        //5.保存盘点清单
        Map<String, Storage> storageMap = new HashMap<String, Storage>();
        if(storagelist.size()>0){//盘点范围有库存档案
            for (Storage storage : storagelist){
                String chipCode=storageRepository.findArchivecode(storage.getEntry());//档号
                storageMap.put(chipCode, storage);
                //数据库标记在库的数据未盘点到
                if(Storage.STATUS_IN.equals(storage.getStorestatus()) && !codeSet.contains(chipCode)){
                    System.out.println("在库数据未盘点到"+storage.getChipcode());
                    InventoryResult ir = new InventoryResult();
                    ir.setCheck(inventory);
                    ir.setResulttype(InventoryResult.RESULT_TYPE_LESS);
                    ir.setStorage(storage);
                    ir.setChipcode(chipCode);
                    inventoryResultRepository.save(ir); //保存盘点结果
                }else if(Storage.STATUS_IN.equals(storage.getStorestatus()) && codeSet.contains(chipCode)){
                    //}else if(Storage.STATUS_IN.equals(storage.getStorestatus()) && storage.getZoneShelves().getShid().equals(同codeset同行)  && codeSet.contains(storage.getChipcode())){
                    System.out.println("在库一致"+storage.getChipcode());//应该再比对一下具体位置
                }
            }
            for (String code : codeSet){
                //盘点结果在盘点范围中有，但库存状态有误
                if(storageMap.containsKey(code) && !Storage.STATUS_IN.equals(storageMap.get(code).getStorestatus())){
                    System.out.println("此档案状态异常"+code);
                    InventoryResult ir = new InventoryResult();
                    ir.setCheck(inventory);
                    ir.setChipcode(code);
                    ir.setResulttype(InventoryResult.RESULT_TYPE_DIFF_STATUS);
                    ir.setStorage(storageMap.get(code));
                    inventoryResultRepository.save(ir); //保存盘点结果
                }else if(!storageMap.containsKey(code)){
                    recordNotInRange(code,inventory);
                }
            }
        }else{//盘点范围没有库存档案
            for (String code : codeSet){
                recordNotInRange(code,inventory);
            }
            return "0";
        }

        return "2";
    }

    public void recordNotInRange(String code,Inventory inventory){
        Storage storage = storageRepository.findByChipcode(code);
        InventoryResult ir = new InventoryResult();
        ir.setCheck(inventory);
        ir.setChipcode(code);
        if(storage == null){
            System.out.println("没有此档案"+code);
            //盘点结果在盘点范围中没有，但数据库中也没有
            ir.setResulttype(InventoryResult.RESULT_TYPE_MORE);
            ir.setStorage(null);
        }else{
            System.out.println("此档案在别处"+code);
            //盘点结果在盘点范围中没有，但数据库中有
            ir.setResulttype(InventoryResult.RESULT_TYPE_DIFF_SHELVES);
            ir.setStorage(storage);
        }
        inventoryResultRepository.save(ir); //保存盘点结果
    }

    /**
     * 检索盘点结果
     * @param page      页号
     * @param limit     页大小
     * @return  盘点记录的分页结果
     */
    public Page<Inventory> findAll(int page, int limit){
        //判断排序类型及排序字段
        Sort sort = new Sort(Sort.Direction.DESC, "checktime");
        //获取pageable
        Pageable pageRequest = new PageRequest(page,limit);
        /*PageRequest pageRequest = new PageRequest(page, limit);*/
        //从session获取用户名，只能查自己所在单位的库房
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session=request.getSession();
        String nickname=(String)session.getAttribute("nickname");
        String codeunit=(String)session.getAttribute("codeunit");
        if(nickname==null){//非韶关
            return inventoryRepository.findAllByOrderByChecktimeDesc(pageRequest);
        }
        String code=nickname.substring(0,nickname.indexOf("|"));
        String cCode=codeunit.substring(0,codeunit.indexOf("|"));
        String unitcode="";
        if(nickname.startsWith("444")){
            unitcode=cCode;
        }else{
            unitcode="444"+code;
        }
        return inventoryRepository.findAllByUnitOrderByChecktimeDesc(pageRequest,unitcode);
    }

    /**
     * 检索盘点结果不在数据库中的数据
     * @param page      页号
     * @param limit     页大小
     * @return  盘点结果的分页结果
     */
    public Page<InventoryResult> findAllResultMore(int page, int limit){
        PageRequest pageRequest = new PageRequest(page, limit);
        return inventoryResultRepository.findAllByResulttype(pageRequest, InventoryResult.RESULT_TYPE_MORE);
    }

    /**
     * 检索盘点范围内没有盘点到的数据
     * @param page      页号
     * @param limit     页大小
     * @return  盘点结果的分页结果
     */
    public Page<InventoryResult> findAllResultLess(int page, int limit){
        PageRequest pageRequest = new PageRequest(page, limit);
        return inventoryResultRepository.findAllByResulttype(pageRequest, InventoryResult.RESULT_TYPE_LESS);
    }

    /**
     * 检索盘点状态信息有误的数据
     * @param page      页号
     * @param limit     页大小
     * @return  盘点结果的分页结果
     */
    public Page<InventoryResult> findAllResultStatus(int page, int limit){
        PageRequest pageRequest = new PageRequest(page, limit);
        return inventoryResultRepository.findAllByResulttype(pageRequest, InventoryResult.RESULT_TYPE_DIFF_STATUS);
    }

    /**
     * 检索盘点位置信息有误的数据
     * @param page      页号
     * @param limit     页大小
     * @return  盘点结果的分页结果
     */
    public Page<InventoryResult> findAllResultShelves(int page, int limit){
        PageRequest pageRequest = new PageRequest(page, limit);
        return inventoryResultRepository.findAllByResulttype(pageRequest, InventoryResult.RESULT_TYPE_DIFF_SHELVES);
    }

    /**
     * 检索盘点记录
     * @return
     */
    public Page<Inventory> findInventory(Pageable pageable){
        return inventoryRepository.findAll(pageable);
    }

    /**
     * 检索盘点结果异常记录
     * @return
     */
    public Page<InventoryResult> findInventoryResult(Pageable pageable, String resulttype, String checkid){
        Inventory check=inventoryRepository.findOne(checkid);
        return inventoryResultRepository.findAllByResulttypeAndCheck(pageable,resulttype,check);
    }

    /**
     * 检索盘点结果异常记录
     * @return
     */
    public List<InventoryResult> findInventoryResults(String resulttype, String checkid){
        Inventory check=inventoryRepository.findOne(checkid);
        return inventoryResultRepository.findAllByResulttypeAndCheck(resulttype,check);
    }

}
