package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_electronic_browse_sx;
import com.wisdom.secondaryDataSource.entity.Tb_electronic_sx;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by Leo on 2020/8/11 0011.
 */
public interface SxElectronicBrowseRepository extends JpaRepository<Tb_electronic_browse_sx,String>, JpaSpecificationExecutor<Tb_electronic_browse_sx> {

    Tb_electronic_browse_sx findByEleid(String eleid);

    List<Tb_electronic_browse_sx> findByEleidIn(String[] eleid, Sort sort);

    List<Tb_electronic_browse_sx> findByEntryid(String entryid, Sort sort);

    Tb_electronic_browse_sx findByEntryid(String entryid);
}
