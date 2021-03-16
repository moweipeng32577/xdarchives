package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_supervision_electronic;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Administrator on 2020/10/13.
 */
public interface SupervisionElectronicRepository extends JpaRepository<Tb_supervision_electronic,String> {


    List<Tb_supervision_electronic> findByOrganidAndSelectyearAndSavetype(String organid, String selectyear, String savetype, Sort sort);

    Tb_supervision_electronic findByEleid(String eleid);

    List<Tb_supervision_electronic> findByEleidInOrderBySortsequence(String[] eleids);

    Integer deleteByEleidIn(String[] ids);

    List<Tb_supervision_electronic> findByOrganidAndSelectyear(String organid, String selectyear);
}
