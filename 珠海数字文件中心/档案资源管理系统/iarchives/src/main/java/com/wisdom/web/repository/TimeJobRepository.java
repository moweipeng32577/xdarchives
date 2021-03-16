package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_time_job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by yl on 2020/1/3.
 */
public interface TimeJobRepository extends JpaRepository<Tb_time_job, String>,
        JpaSpecificationExecutor<Tb_time_job> {
    Tb_time_job findById(String id);

    Tb_time_job findByJobname(String jobname);
}
