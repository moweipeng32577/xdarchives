package com.xdtech.project.lot.device.service;

import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.project.lot.device.entity.DeviceWork;
import com.xdtech.project.lot.device.repository.DeviceRepository;
import com.xdtech.project.lot.device.repository.DeviceWorkRepository;
import com.xdtech.project.lot.util.CornUtil;
import com.xdtech.project.lot.util.QuartzJob;
import com.xdtech.project.lot.util.QuartzUtil;
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
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * 设备作业管理业务类
 * Created by wangmh on 2019-09-18.
 */
@Service
@Transactional
public class DeviceWorkService {

    @Autowired
    DeviceWorkRepository deviceWorkRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @PersistenceContext
    EntityManager entityManager;

    public Page<DeviceWork> getDeviceWorks(int page,int limit, String deviceType, String deviceId) {
        PageRequest pageRequest = new PageRequest(page-1, limit);
        String jql = "select dw from DeviceWork dw left join fetch dw.device where 1=1";
        if(StringUtils.isNotBlank(deviceId)){
            jql += " and dw.device.id = '"+deviceId+"'";
        }else if(StringUtils.isNotBlank(deviceType)){
            jql += " and dw.device.type = '"+deviceType+"'";
        }
        jql += " order by dw.createTime desc";
        Query query = entityManager.createQuery(jql);
        query.setFirstResult((page-1)*limit);
        query.setMaxResults(limit);
        List<DeviceWork> resultList = query.getResultList();
        int count = entityManager.createQuery(jql).getResultList().size();
        return new PageImpl<>(resultList,pageRequest,count);
    }

    public ExtMsg saveDeviceWork(DeviceWork deviceWork, String type) {

        if(type.equals("modify")){
            DeviceWork work = deviceWorkRepository.findOne(deviceWork.getWorkId());
            deviceWork.setCreateTime(work.getCreateTime());
            deviceWork.setStatus(work.getStatus());
            System.out.println("[JobName]："+ deviceWork.getDevice().getName()+deviceWork.getPeriod()+deviceWork.getMode()+"---------------修改定时任务!---------------"+ deviceWork.getWorkTime());
            try {
                String cronTime = CornUtil.getCron(deviceWork.getPeriod(),deviceWork.getWorkTime());
                QuartzUtil.modifyJobTime(deviceWork.getMode(),deviceWork.getDevice().getId(),deviceWork.getDevice().getName()+deviceWork.getPeriod()+deviceWork.getMode(), cronTime);
            } catch (ParseException e) {
                e.printStackTrace();
                return new ExtMsg(true,"添加定时任务失败！ 时间格式转化corn格式失败！",null);
            }catch (Exception e){
                e.printStackTrace();
                return new ExtMsg(true,"添加定时任务失败！ ",null);
            }
        }
        else if(type.equals("add")){
            System.out.println("[JobName]："+ deviceWork.getDevice().getName()+deviceWork.getPeriod()+deviceWork.getMode()+"---------------添加定时任务!---------------"+ deviceWork.getWorkTime());

            try {
                String cronTime = CornUtil.getCron(deviceWork.getPeriod(),deviceWork.getWorkTime());
                QuartzUtil.addJob(deviceWork.getMode(),deviceWork.getDevice().getId(),deviceWork.getDevice().getName()+deviceWork.getPeriod()+deviceWork.getMode(), QuartzJob.class, cronTime);
            } catch (ParseException e) {
                e.printStackTrace();
                return new ExtMsg(true,"添加定时任务失败！ 时间格式转化corn格式失败！",null);
            }catch (Exception e){
                e.printStackTrace();
                return new ExtMsg(true,"添加定时任务失败！",null);
            }

            deviceWork.setCreateTime(new Date());
            deviceWork.setStatus(1);
        }

        deviceWorkRepository.save(deviceWork);
        return new ExtMsg(true,type.equals("modify")?"修改定时任务成功！":"添加定时任务成功！",null);
    }

    public void deleteDeviceWork(String[] ids) {

        List<DeviceWork> list = deviceWorkRepository.findByids(ids);
        for(DeviceWork deviceWork : list){
            QuartzUtil.removeJob(deviceWork.getDevice().getName()+deviceWork.getPeriod()+deviceWork.getMode());
        }
        deviceWorkRepository.deleteByIds(ids);
    }
}
