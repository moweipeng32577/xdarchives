package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_borrowdoc;
import com.wisdom.web.entity.Tb_showroom_date_person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2017/10/25 0025.
 */
public interface ShowroomDatePersonRepository
		extends JpaRepository<Tb_showroom_date_person, Integer>, JpaSpecificationExecutor<Tb_showroom_date_person> {

	@Query(value = "select b from Tb_showroom_date_person b where b.showroomid=?1 and b.visitingdate=?2")
	Tb_showroom_date_person findByShoeroomidAndVisitingdate(String showroomid, String date);

}