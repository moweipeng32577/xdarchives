package com.wisdom.web.repository;

import com.wisdom.web.entity.Szh_batch_media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SzhBatchMediaRepository extends JpaRepository<Szh_batch_media,String>,JpaSpecificationExecutor<Szh_batch_media> {
    Integer deleteByBatchcodeIn(String[] batchcodes);
}
