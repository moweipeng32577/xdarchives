package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_long_retention_setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by yl on 2020/5/21.
 */
public interface LongRetentionSettingRepository extends JpaRepository<Tb_long_retention_setting, String>,
        JpaSpecificationExecutor<Tb_long_retention_setting> {
    Tb_long_retention_setting findByNodeid(String nodeid);
}
