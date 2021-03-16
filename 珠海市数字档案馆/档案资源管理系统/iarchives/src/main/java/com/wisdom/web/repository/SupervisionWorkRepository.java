package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_supervision_work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2020/10/12.
 */
public interface SupervisionWorkRepository extends JpaRepository<Tb_supervision_work,String> {


    @Query(value = "select distinct t.selectyear from Tb_supervision_work t")
    List<String> getSelectYear();

    Tb_supervision_work findByOrganidAndSelectyear(String organid, String selectyear);
}
