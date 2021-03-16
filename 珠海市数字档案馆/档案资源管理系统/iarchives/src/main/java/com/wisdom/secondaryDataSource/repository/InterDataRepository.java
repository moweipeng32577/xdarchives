package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.InterData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by tanly on 2018/4/12 0012.
 */
public interface InterDataRepository extends JpaRepository<InterData, String> {
    @Query(value = "select * from t_bankoa_ygd where archive_flag='0' and nd>2016 and regexp_like(wjlx,'(发文|收文|签报)')", nativeQuery = true)
    List<InterData> getEntryByFilter();

    @Modifying
    @Transactional
    @Query(value = "update t_bankoa_ygd set archive_flag = ?2,oa_clsj = ?3  where id = ?1", nativeQuery = true)
    int updateInterData(String id, String flag,java.sql.Date oaclsj);
}
