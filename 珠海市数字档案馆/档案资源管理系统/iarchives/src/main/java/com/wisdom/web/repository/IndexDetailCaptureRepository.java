package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_index_detail_capture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2020/6/17.
 */
public interface IndexDetailCaptureRepository extends JpaRepository<Tb_index_detail_capture, String>, JpaSpecificationExecutor<Tb_index_detail_capture> {

    @Query(value = "select d from Tb_index_detail_capture d where entryid in (?1)")
    List<Tb_index_detail_capture> findByEntryidIn(String[] entryids);

    Tb_index_detail_capture findByEntryid(String entryid);
}
