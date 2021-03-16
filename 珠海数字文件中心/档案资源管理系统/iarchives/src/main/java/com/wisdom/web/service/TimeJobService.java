package com.wisdom.web.service;

import com.wisdom.web.entity.Tb_time_job;
import com.wisdom.web.repository.TimeJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yl on 2020/1/3.
 * 动态定时任务
 */
@Service
@Transactional
public class TimeJobService {
    @Autowired
    TimeJobRepository timeJobRepository;

    public List<Tb_time_job> findALLTimeJob() {
        return timeJobRepository.findAll();
    }

    public Tb_time_job findJobById(String id) {
        return timeJobRepository.findById(id);
    }

    public Tb_time_job findByJobname(String jobname) {
        return timeJobRepository.findByJobname(jobname);
    }

    public Tb_time_job saveJob(Tb_time_job timeJob) {
        return timeJobRepository.save(timeJob);
    }
}
