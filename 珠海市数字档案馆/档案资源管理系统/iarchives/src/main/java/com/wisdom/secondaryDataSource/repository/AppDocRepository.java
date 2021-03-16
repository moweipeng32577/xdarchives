package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.AppDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by tanly on 2018/4/12 0012.
 */
public interface AppDocRepository extends JpaRepository<AppDoc, String> {
    @Query(value = "select * from t_csgappdoc_info where fileid=?1 and isdeleted='0' and regexp_like(url,'(fw/|sw/|qb/|null/)')", nativeQuery = true)
    List<AppDoc> getByFileidAndIsdeletedAndUrl(String fileid);

    @Modifying
    @Transactional
    @Query(value = "update t_csgappdoc_info set archive_ele_flag = ?2 where id = ?1", nativeQuery = true)
    int updateAppdoc(String id, String eleFlag);
}
