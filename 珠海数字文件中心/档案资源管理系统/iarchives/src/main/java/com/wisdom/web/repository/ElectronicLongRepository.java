package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_electronic_long;
import com.wisdom.web.entity.Tb_electronic_solid;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Rong on 2017/11/21.
 */
public interface ElectronicLongRepository extends JpaRepository<Tb_electronic_long, String> {

    Tb_electronic_long findByEleid(String eleid);

    List<Tb_electronic_long> findByEntryidOrderBySortsequence(String entryid);

    List<Tb_electronic_long> findByEleidInOrderBySortsequence(String[] eleids);

    Integer deleteByEntryidIn(String[] entryidData);

    List<Tb_electronic_long> findByEleidIn(String[] eleids, Sort sort);

    List<Tb_electronic_long> findByEntryid(String entryid,Sort sort);

    List<Tb_electronic_long> findByEntryid(String entryid);
}
