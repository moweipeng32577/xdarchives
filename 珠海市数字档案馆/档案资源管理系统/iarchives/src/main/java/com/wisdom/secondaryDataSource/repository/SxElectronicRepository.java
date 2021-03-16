package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_electronic_sx;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Leo on 2020/8/5 0005.
 */
public interface SxElectronicRepository extends JpaRepository<Tb_electronic_sx, String> {

    List<Tb_electronic_sx> findByEleidIn(String[] eleids, Sort sort);

    List<Tb_electronic_sx> findByEntryid(String entryid, Sort sort);

    Tb_electronic_sx findByEleid(String eleid);
}
