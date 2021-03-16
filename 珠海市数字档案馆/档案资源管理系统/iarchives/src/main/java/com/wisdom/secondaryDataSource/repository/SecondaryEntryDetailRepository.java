package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_entry_detail_sx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by Leo on 2020/5/22 0022.
 */
public interface SecondaryEntryDetailRepository extends JpaRepository<Tb_entry_detail_sx, String>, JpaSpecificationExecutor<Tb_entry_detail_sx> {

    Tb_entry_detail_sx findByEntryid(String entryids);

    List<Tb_entry_detail_sx> findByEntryidIn(String[] entryids);
}
