package com.xdtech.project.lot.speed.service;

import com.xdtech.project.lot.speed.entity.Record;
import com.xdtech.project.lot.speed.entity.TbDevicePropHistories;
import com.xdtech.project.lot.speed.repository.DevicePropHistoryRepository;
import com.xdtech.project.lot.speed.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * 斯必得业务类
 * 主要提供获取设备实时状态、获取温湿度历史数据的方法
 *
 * Created by Rong on 2019-11-15.
 */
@Service
public class SensorsService {

    @Autowired
    RecordRepository recordRepository;

    @Autowired
    DevicePropHistoryRepository devicePropHistoryRepository;

    public Page<Record> getDeviceHistories(Integer sensorIndex, String startTime, String endTime,
                                                  Integer page, Integer limit) {
        Pageable pageable = new PageRequest(page-1, limit, new Sort(Sort.Direction.DESC,"time"));
        Specification<Record> specification = new Specification<Record>() {
            @Override
            public Predicate toPredicate(Root<Record> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate sensor = cb.equal(root.get("sensorIndex"), sensorIndex);
                if(startTime != null && endTime != null){
                    Predicate time = cb.between(root.get("time"), startTime, endTime);
                    return cb.and(sensor, time);
                }
                return sensor;
            }
        };
        Page<Record> list = recordRepository.findAll(specification, pageable);
        //Page<Record> list = recordRepository.findAllBySensorIndexOrderByTimeDesc(sensorIndex, pageable);
        return list;
    }


    public Page<TbDevicePropHistories> getDeviceHistoriesByDeviceId(String propId, String deviceId, String startTime, String endTime,
                                                                    Integer page, Integer limit) {
        Pageable pageable = new PageRequest(page-1, limit, new Sort(Sort.Direction.DESC,"valueTime"));
        Specification<TbDevicePropHistories> specification = new Specification<TbDevicePropHistories>() {
            @Override
            public Predicate toPredicate(Root<TbDevicePropHistories> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate deviceCodeCate = cb.equal(root.get("deviceId"), deviceId);
                Predicate propIdCate = cb.equal(root.get("propId"),propId);
                Predicate p = cb.and(deviceCodeCate,propIdCate);

                if(startTime != null && endTime != null){
                    Predicate time = cb.between(root.get("valueTime"), startTime, endTime);
                    return cb.and(p, time);
                }
                return p;
            }
        };
        Page<TbDevicePropHistories> list = devicePropHistoryRepository.findAll(specification, pageable);
        return list;
    }


    public List<TbDevicePropHistories> getDeviceHistoriesByDeviceId(String propId, String deviceId) {
        List<TbDevicePropHistories> list = devicePropHistoryRepository.findDeviceHistoriesByDeviceId (deviceId,propId);
        return list;
    }
}
