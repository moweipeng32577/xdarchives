package com.xdtech.component.storeroom.service;

import com.xdtech.component.DateTime.DateTimeUtils;
import com.xdtech.component.storeroom.entity.MoveWare;
import com.xdtech.component.storeroom.entity.ZoneShelves;
import com.xdtech.component.storeroom.entity.Storage;
import com.xdtech.component.storeroom.repository.MoveWareRepository;
import com.xdtech.component.storeroom.repository.StorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 移库业务类
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/24.
 */
@Service
@Transactional
public class MoveWareService {

    @Autowired
    private MoveWareRepository moveWareRepository;

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 获取移库编号
     * 格式为：199001010001，即年月日八位数字加四位数字序号
     * 当天没有移库记录，则序号为1
     * 如有移库记录，则获取最大序号加1为新序号
     * @return  移库编号
     */
    private synchronized String generateWarenum(){
        //获取最大的移库编号
        String max = moveWareRepository.findMaxWarenum();
        //获取当前日期字符串
        String datestr = DateTimeUtils.getDateStr(new Date());
        int num = 0;
        //如果有当天移库记录，获取最大序号
        if(max != null && datestr.equals(max.substring(0,8))){
            num = Integer.parseInt(max.substring(8));
        }
        //生成移库编号字符串
        String result = datestr + String.format("%04d",num+1);
        return result;
    }

    /**
     * 实体档案移库
     * 将某一批实体档案从一个单元格移动到另一个单元格
     * @param moveWare  移库对象
     * @param target    目的单元格
     * @return 保存后的移库对象
     */
    public MoveWare save(MoveWare moveWare, ZoneShelves target){
        Set<Storage> storageList = moveWare.getStorages();
        ZoneShelves source = null;
        //1.更新库存实体档案到目的单元格
        for(Storage storage : storageList){
            //获取源单元格，所有实体档案都是属于同一单元格，故取一个即可
            if(source == null){
                source = storage.getZoneShelves();
            }
            storage.setZoneShelves(target);
            entityManager.merge(storage);
        }
        //2.减少源单元格使用量
        //计算移库后源单元格的使用量
        Integer downnum = source.getUsecapacity() - storageList.size();
        //正常情况不会进入此逻辑，避免出现负使用量情况
        if(downnum < 0){
            downnum = 0;
        }
        source.setUsecapacity(downnum);
        entityManager.merge(source);
        //3.增加目的单元格使用量
        target.setUsecapacity(target.getUsecapacity() + storageList.size());
        entityManager.merge(target);
        //4.保存移库对象
        //生成移库编号
        if(moveWare.getWarenum() == null || "".equals(moveWare.getWarenum())){
            moveWare.setWarenum(generateWarenum());
        }
        //生成移库时间
        moveWare.setWaretime(DateTimeUtils.getDateTimeStr(new Date()));
        return moveWareRepository.save(moveWare);
    }

    /**
     * 实体档案移库-單元格
     * 将某一批单元格整体移动到另一批单元格
     * @param moveWare  移库对象
     * @param sources   源单元格
     * @param targets   目的单元格
     * @return  保存后的移库对象
     */
    public MoveWare save(MoveWare moveWare, List<ZoneShelves> sources, List<ZoneShelves> targets,int sourceSize,int targetSize){

        Integer totalcount = 0;
        for (int i = 0; i < sources.size(); i++) {
            ZoneShelves source = sources.get(i);
            ZoneShelves target = targets.get(i);

            Integer num = storageRepository.changeShelvesBatch(target, source);
            totalcount += num;
            /*Integer sourcenum = source.getUsecapacity() - num;
            Integer targetnum = target.getUsecapacity() + num;*/
            Integer sourcenum = sourceSize - num;
            Integer targetnum = targetSize+ num;
            source.setUsecapacity(sourcenum > 0 ? sourcenum : 0);
            //根据shid获取其中的entryid数（有出现entryid重复）
            List<String> entryidList=storageRepository.findEntryByShid(target.getShid());
            target.setUsecapacity(entryidList.size());

            entityManager.merge(source);
            entityManager.merge(target);
        }

        //生成移库编号
        if(moveWare.getWarenum() == null || "".equals(moveWare.getWarenum())){
            moveWare.setWarenum(generateWarenum());
        }
        //生成移库时间
        moveWare.setWaretime(DateTimeUtils.getDateTimeStr(new Date()));
        return moveWareRepository.save(moveWare);
    }


    /**
     * 实体档案移库-電子檔案
     * 将某一批電子文件移动到另一批单元格
     * @param moveWare  移库对象
     * @param ids   源单元格
     * @param targets   目的单元格
     * @return  保存后的移库对象
     */
    public MoveWare entrySave(MoveWare moveWare, String[] ids, List<ZoneShelves> targets){

        Integer totalcount = 0;
        for (int i = 0; i < ids.length; i++) {
            //ZoneShelves source = targets.get(i);
            ZoneShelves target = targets.get(0);
            String entryid=ids[i];
            List<Storage> sts=storageRepository.getShelves(entryid);
            ZoneShelves source=sts.get(0).getZoneShelves();
            Integer num = storageRepository.changeShelves(target, source,entryid);
            totalcount += num;
            Integer sourcenum = source.getUsecapacity() - num;
            Integer targetnum = target.getUsecapacity() + num;
            source.setUsecapacity(sourcenum > 0 ? sourcenum : 0);
            target.setUsecapacity(targetnum);

            entityManager.merge(source);
            entityManager.merge(target);
        }

        //生成移库编号
        if(moveWare.getWarenum() == null || "".equals(moveWare.getWarenum())){
            moveWare.setWarenum(generateWarenum());
        }
        //生成移库时间
        moveWare.setWaretime(DateTimeUtils.getDateTimeStr(new Date()));
        return moveWareRepository.save(moveWare);
    }

}
