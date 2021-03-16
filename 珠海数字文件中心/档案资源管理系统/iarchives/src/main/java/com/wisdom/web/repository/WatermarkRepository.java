package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_watermark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WatermarkRepository extends JpaRepository<Tb_watermark, String> ,JpaSpecificationExecutor<Tb_watermark> {

    Integer deleteByIdIn(String[] ids);

    List<Tb_watermark> findByOrganid(String organid);

    @Query(value = "select b from Tb_watermark b where b.title=?1")
    List<Tb_watermark> findByTitle(String title);

    Tb_watermark findByOrganidAndIsdefault(String organid, String isdefault);
}
