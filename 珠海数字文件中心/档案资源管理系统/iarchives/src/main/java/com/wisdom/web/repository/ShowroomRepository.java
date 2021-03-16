package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_showroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by zdw on 2020/03/20
 */
public interface ShowroomRepository extends JpaRepository<Tb_showroom,String>,JpaSpecificationExecutor<Tb_showroom> {

    Tb_showroom findByShowroomid(String showroomid);

    Integer deleteByShowroomidIn(String[] showroomidData);
}
