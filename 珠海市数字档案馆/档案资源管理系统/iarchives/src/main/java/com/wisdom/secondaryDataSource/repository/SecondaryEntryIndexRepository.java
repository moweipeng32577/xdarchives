package com.wisdom.secondaryDataSource.repository;

import com.wisdom.secondaryDataSource.entity.Tb_entry_index_sx;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by Leo on 2020/5/22 0022.
 */
public interface SecondaryEntryIndexRepository extends JpaRepository<Tb_entry_index_sx,String>, JpaSpecificationExecutor<Tb_entry_index_sx> {
    Tb_entry_index_sx findAllByEntryid(String entryid);

    @Query(value = "select sid.entryid from tb_entry_index sid where 1=1 ?1",nativeQuery = true)
    List<Tb_entry_index_sx> findAllByEntryidIn(String sql);

    List<Tb_entry_index_sx> findByEntryidIn(String[] entryids);

    Page<Tb_entry_index_sx> findByEntryidIn(String[] entryids, Pageable pageable);
}
