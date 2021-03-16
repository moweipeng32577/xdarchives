package com.xdtech.component.storeroom.service;

import com.xdtech.component.storeroom.entity.Storage;
import com.xdtech.component.storeroom.entity.ZoneShelves;
import com.xdtech.component.storeroom.entity.Zones;
import com.xdtech.component.storeroom.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实体档案业务类
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/20.
 */
@Service
@Transactional
public class StorageService {

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private InventoryResultRepository inventoryResultRepository;

    @Autowired
    ZoneShelvesRepository zoneShelvesRepository;

    @Autowired
    BorrowRepository borrowRepository;

    @Autowired
    InWareRepository inWareRepository;
    @Autowired
    OutWareRepository outWareRepository;

    /**
     * 实体档案新增
     * @param storage   新增数据参数
     * @return  保存后的实体档案对象
     */
    public Storage save(Storage storage){
        return storageRepository.save(storage);
    }

    /**
     * 修正实体档案的库存状态
     * 用于在档案盘点后，盘点到的实体档案，状态却为已出库
     * 通过此方法修改未已入库
     * @param storage   实体档案对象
     * @return  修改结果，true为修改成功,false为修改失败
     */
    public boolean changeStatusIn(Storage storage){
        return storageRepository.changeStatus(storage.getStid(), Storage.STATUS_IN) > 0;
    }

    /**
     * 修正实体档案的库存状态
     * 用于在档案盘点后，状态却为已入库的实体档案，未盘点到
     * 通过此方法修改未已入库
     * @param storage   实体档案对象
     * @return  修改结果，true为修改成功,false为修改失败
     */
    public boolean changeStatusOut(Storage storage){
        return storageRepository.changeStatus(storage.getStid(), Storage.STATUS_OUT) > 0;
    }

    /**
     * 修改盘点状态异常的库房记录
     * @param chipcodes
     */
    public void changeStatus(String chipcodes){
        String[] chips=chipcodes.split(",");
        String inStr=Storage.STATUS_IN;
        storageRepository.changeInventoryStatus(chips,inStr);
        //修改成功后，设置inventory_result的resulttype的3改为7
        String newRt="7";
        Integer num= inventoryResultRepository.changeResulttype(chips,newRt);

    }

    /**
     * 按档号查询存档位置
     * @param dhCode
     * @return
     */
    public String findshid(String dhCode){
        List<Storage> list=new ArrayList<>();
        if(dhCode.startsWith("id-")){//传的是entryid
            list=storageRepository.getByEntry(dhCode.substring(3));
        }else{
            list=storageRepository.findByArchivecode(dhCode);
        }
        String zsMsg="";
        String zoneMsg="";
        if(list.size()>0){
            ZoneShelves zs=list.get(0).getZoneShelves();
            zsMsg=zs.getColdisplay()+"列_"+zs.getSectiondisplay()+"节_"+zs.getLayerdisplay()+"层_"+zs.getSidedisplay();
            Zones zone=zs.getZone();
            zoneMsg=zone.getCitydisplay()+"_"+zone.getUnitdisplay()+"_"+zone.getRoomdisplay()+"_"+zone.getZonedisplay()+"_";
        }
        return zoneMsg+zsMsg;
    }


    /**
     * 修改盘点位置异常的记录
     * @param chipshs
     */
    public void changeShel(String[] chipshs){

    }

    /**
     * 销毁
     * @param entryids
     */
    public Integer delEntry(String[] entryids){
        //更新库存
        for(String entryid:entryids){
            Storage storage=storageRepository.findByEntry(entryid);
            ZoneShelves zs=storage.getZoneShelves();
            int usecapacity=zs.getUsecapacity();
            if(usecapacity>0){
                usecapacity--;
            }
            zs.setUsecapacity(usecapacity);
            zoneShelvesRepository.save(zs);
        }
        //清空相关条目库存位置
        storageRepository.clearPosition(entryids);
        return storageRepository.deleteInEntryid(entryids);
    }

    /**
     * 销毁
     * @param storages
     */
    public Integer delStorages(List<Storage> storages){
        String[] stidArr=new String[storages.size()];//库存id集合
        for(int i=0;i<storages.size();i++){
            Storage storage=storages.get(i);
            stidArr[i]=storage.getStid();
            //更新单元格库存数
            ZoneShelves zs=storage.getZoneShelves();
            int usecapacity=zs.getUsecapacity();
            if(usecapacity>0){
                usecapacity--;
            }
            zs.setUsecapacity(usecapacity);
            zoneShelvesRepository.save(zs);
        }

        //删除出入库_库存关联表记录
        inWareRepository.deleteAllByStid(stidArr);
        outWareRepository.deleteAllByStid(stidArr);

        //删除库存盘点详细记录
        inventoryResultRepository.deleteAllByStid(stidArr);

        //删除库存记录
        return storageRepository.deleteAllByStid(stidArr);
    }

    /**
     * 更新大屏借阅信息查看状态
     * @param docid
     */
    public Integer updateMessageStatue(String docid){
        return borrowRepository.updateMessageStatus(docid);
    }


    public Map findByEntryid(String entryid){
        //Storage st = storageRepository.findByEntry(entryid);
        Map map = new HashMap();
        List<Storage> storages= storageRepository.getByEntry(entryid);
        Storage st=new Storage();
        if(storages.size()>0){
            st=storages.get(0);
        }else{
            map.put("zone", "false");//返回查找失败
            return map;
        }
        String col = st.getZoneShelves().getCol();
        Zones zone = st.getZoneShelves().getZone();
        map.put("col", col);
        map.put("zone", zone);
        return map;
    }
}
