package com.xdtech.project.lot.device.service;

import com.xdtech.component.storeroom.service.ShelvesService;
import com.xdtech.project.lot.device.entity.DeviceHistory;
import com.xdtech.project.lot.device.repository.DeviceHistoryRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author wujy  2019/09/11
 */
@Service
@Transactional
public class DeviceHistoryService {

    @Autowired
    DeviceHistoryRepository deviceHistoryRepository;

    @Autowired
    ShelvesService shelvesService;

    @PersistenceContext
    EntityManager entityManager;

    /**
     * 获取温湿度历史数据
     */
    public Page<DeviceHistory> getDeviceHistories(String deviceid, String deviceType,String ip,Integer port, Integer code,String startTime, String endTime, Integer page, Integer limit) {
        PageRequest pageRequest;
        String jql = "from DeviceHistory his left join fetch his.device  where 1=1 ";
        if(StringUtils.isNotBlank(ip) && port != null && code != null){
            deviceid = shelvesService.getMJJDeviceId(ip, port, code);
        }
        if(StringUtils.isNotBlank(deviceid)){
            jql += " and his.device.id='" + deviceid+"'";
        }
        if(StringUtils.isNotBlank(deviceType)){
            jql += " and his.type='" + deviceType+"'";
        }
        if(startTime != null){
            jql += " and his.captureTime >='"+startTime+"'";
        }
        if(endTime != null){
            jql += " and his.captureTime <='"+endTime+"'";
        }
        jql += " order by his.captureTime desc";
        Query query = entityManager.createQuery(jql);
        if(page != null && limit != null){
            query.setFirstResult((page-1)*limit);
            query.setMaxResults(limit);
            pageRequest = new PageRequest(page-1, limit);
        }else{
            pageRequest = new PageRequest(0, 100);//由于不分页会造成数据量查询非常大，所以限定100条
        }
        List<DeviceHistory> deviceHistoryList = query.getResultList();
        int count = entityManager.createQuery(jql).getResultList().size();
        return new PageImpl<>(deviceHistoryList,pageRequest,count);
    }

    public Page<DeviceHistory> getHThistory(String type,int page, int limit){
        PageRequest pageRequest = new PageRequest(page-1, limit);
        return deviceHistoryRepository.findAllByType(type,pageRequest);
    }
}

