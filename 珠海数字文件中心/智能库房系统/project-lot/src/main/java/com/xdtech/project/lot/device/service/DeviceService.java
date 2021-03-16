package com.xdtech.project.lot.device.service;

import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.project.lot.device.entity.Device;
import com.xdtech.project.lot.device.entity.DeviceHistory;
import com.xdtech.project.lot.device.entity.DeviceWarning;
import com.xdtech.project.lot.device.repository.DeviceHistoryRepository;
import com.xdtech.project.lot.device.repository.DeviceRepository;
import com.xdtech.project.lot.device.repository.DeviceWarningRepository;
import com.xdtech.project.lot.device.repository.DeviceWorkRepository;
import com.xdtech.project.lot.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;

/**
 * 设备管理业务类
 * Created by Rong on 2019-01-17.
 */
@Service
@Transactional
public class DeviceService {

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    DeviceWorkRepository deviceWorkRepository;

    @Autowired
    DeviceWarningRepository deviceWarningRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    DeviceHistoryRepository deviceHistoryRepository;
    /**
     * 设备删除
     *
     * @param ids
     */
    public ExtMsg delDevice(String[] ids) {
        List list = deviceWorkRepository.findBydeviceid(ids);
        if(list.size() > 0){
            return  new ExtMsg(true,"该设备正在工作，无法删除","");
        }
        else {
            int index = deviceRepository.deleteByIdIn(ids);
            return  new ExtMsg(true,"删除成功","");
        }
    }

    public List<Device> getDeviceByArea(String areaid, String type) {
        return deviceRepository.findByAreaAndType(areaid,type);
    }

    public Page<DeviceWarning> getDeviceWarning(PageRequest pageRequest) {
        return deviceWarningRepository.findAll(pageRequest);

    }

    /**
     * 找出当前未经处理的告警信息
     * @return
     */
    public List<DeviceWarning> getDeviceWarningAll() {
        int[] effect = {1,2};//未处理，生效
        return deviceWarningRepository.findAllByStatusInOrderByCreateTimeDesc(effect);
    }

    /**
     * 找出当前未经处理的告警信息的数量
     * @return
     */
    public List<Map<String,Object>> getDeviceWarningCount() {
        List countList = deviceWarningRepository.DeviceWarningCount();
        List<Map<String,Object>> maps = new ArrayList<>();
        for (Object count : countList) {
            Map<String,Object> map = new HashMap<>();
            Object[] obj = (Object[])count;
            map.put("warningType",(String) obj[0]);
            map.put("count", (long) obj[1]);
            maps.add(map);
        }
        return maps;
    }

    public Integer enabledOrDisableDevice(String[] ids,String type){
        Integer count = 0;
        if (null != ids) {
            for (int i = 0; i < ids.length; i++) {
                Device device = deviceRepository.findOne(ids[i]);
                if(null!=type||"".equals(type)) {
                    device.setEnabled(type);
                    count++;
                }
            }
        }
        return count;
    }

    public Page<Device> findDevice(String deviceType,Integer page,Integer limit) {
        String jql = "from Device de where 1=1 ";
        if(StringUtils.isNotBlank(deviceType)){
            jql += "and de.type = '"+deviceType+"'";
        }
        jql += " order by de.sort+"+0+",de.name ";
        Query query = entityManager.createQuery(jql);
        query.setFirstResult((page-1)*limit);
        query.setMaxResults(limit);
        List<Device> devices = query.getResultList();
        int count = entityManager.createQuery(jql).getResultList().size();
        return new PageImpl<>(devices, new PageRequest(page-1, limit), count);
    }

    public List<Device> findDevice(String deviceType) {
        String jql = "from Device de where de.prop like ?1 ";
        if(StringUtils.isNotBlank(deviceType)){
            jql += "and de.type.typeCode = ?2";
        }
        jql += " order by de.name ";
        Query query = entityManager.createQuery(jql);
        query.setParameter(1, "%version:\"new\"%");
        query.setParameter(2, deviceType);
        return query.getResultList();
    }

    /**
     * 保存告警信息
     * @param deviceId
     * @param description
     */
    public void saveWarning(String deviceId, String description) {
        DeviceWarning warning = new DeviceWarning();
        warning.setWarningType(DeviceWarning.HT_WARNING);
        Device device = new Device();
        device.setId(deviceId);
        warning.setDevice(device);
        warning.setDescription(description);
        warning.setStatus(1);//默认为未处理
        warning.setCreateTime(DateUtil.getCurrentTime());
        deviceWarningRepository.save(warning);
    }

    /**
     * 保存历史记录数据
     */
    public void saveHistory(String deviceId, String captureValue,String hisType) {
        DeviceHistory deviceHistory = new DeviceHistory();
        deviceHistory.setCaptureTime (DateUtil.getCurrentTime());
        deviceHistory.setCaptureValue(captureValue);
        deviceHistory.setType(hisType);
        Device device = new Device();
        device.setId(deviceId);
        deviceHistory.setDevice(device);
        deviceHistoryRepository.save(deviceHistory);
    }

    public Device updateDeviceStatus(String deviceId,int status) {
        Device device = deviceRepository.findOne(deviceId);
        if(device != null){
            device.setStatus(status);
            return deviceRepository.save(device);
        }
        return null;
    }

    public Device findDeviceByIdOrType(String deviceId,String deviceType,String sort) {
        if(StringUtils.isNotBlank(deviceId)){
            return deviceRepository.findOneById(deviceId);
        }
        else{
            if(StringUtils.isNotBlank(sort)){
                return deviceRepository.findOneByTypeAndSort(deviceType,sort);
            }
           else{
                return deviceRepository.findOneByType(deviceType);
            }
        }
    }

    //设备接入
    public ExtMsg enabledDevice(String deviceid,String state,String type){
        Device device = deviceRepository.findById(deviceid);
        device.setEnabled(state);
        //当前用户没有接入设备权限，发出警告
        if("noPremissions".equals(type)){
            DeviceWarning deviceWarning = new DeviceWarning();
            deviceWarning.setDevice(device);
            deviceWarning.setWarningType("非法接入告警");
            if(device.getArea()!=null){
                deviceWarning.setDescription(device.getName()+"设备非法接入"+ device.getArea().getName()+"库房！");
            }else{
                deviceWarning.setDescription(device.getName()+"设备非法接入库房！");
            }
            deviceWarning.setCreateTime(DateUtil.getCurrentTime());
            deviceWarning.setWarningTime(DateUtil.getCurrentTime());
            deviceWarning.setStatus(2);
            deviceWarningRepository.save(deviceWarning);
        }
        deviceRepository.save(device);
        return new ExtMsg(true,"",null);
    }
}
