package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_borrowdoc;
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
public interface BorrowDocRepository
		extends JpaRepository<Tb_borrowdoc, Integer>, JpaSpecificationExecutor<Tb_borrowdoc> {

	@Query(value = "select b from Tb_borrowdoc b where b.borrowcode in (select msgid from Tb_flows where taskid=?1)")
	Tb_borrowdoc getBorrowDocByTaskid(String taskid);

	Page<Tb_borrowdoc> findByBorrowmanAndStateAndType(Pageable pageable, String borrowman, String state, String type);

	Page<Tb_borrowdoc> findByStateAndTypeOrderByBorrowdate(Pageable pageable, String state, String type);

	Tb_borrowdoc findByDocid(String id);

	List<Tb_borrowdoc> findAllByBorrowdate(String date);

	List<Tb_borrowdoc> findByBorrowcodeIn(String[] borrowdocs);

	Tb_borrowdoc findByBorrowcode(String borrowdoc);

	Integer deleteByBorrowcodeIn(String[] borrowCodes);

	@Query(value = "select b from Tb_borrowdoc b where b.clearstate = '1' and b.borrowmanid = ?1 and (b.state ='已通过' or b.state ='不通过' or b.state ='退回')")
	Page<Tb_borrowdoc> getByStateAndClearstate(String borrowmanid, Pageable pageable);

	@Modifying
	@Query(value = "update Tb_borrowdoc set returnstate=?1 where borrowcode=?2")
	Integer setBorrowdocState(String flagopen, String borrowcode);

	Page<Tb_borrowdoc> findByBorrowmanidAndStateAndType(Pageable pageable, String borrowmanid, String state,
			String type);

    Page<Tb_borrowdoc> findByBorrowcodeInAndStateAndType(Pageable pageable, String[] borrowcode, String state,
                                                        String type);

	List<Tb_borrowdoc> findByDocidIn(String[] id);

    @Query(value = "select b from Tb_borrowdoc b where b.id =(select borrowmsgid from Tb_task  where taskid = ?1)")
    Page<Tb_borrowdoc> findByBorrowmig(Pageable pageable,String taskid);

    @Query(value = "select b from Tb_borrowdoc b where b.borrowcode in(select borrowmsgid from Tb_task  where taskid = ?1)")
    Tb_borrowdoc findByBorrowmig(String taskid);

	List<Tb_borrowdoc> findByBorrowmanid(String userid);

	@Query(value = "select b from Tb_borrowdoc b where b.borrowcode in (select borrowcode from Tb_borrowmsg  where entryid in (?1) )")
	List<Tb_borrowdoc> findByEntryids(String[] entryids);

    @Query(value = "select b from Tb_borrowdoc b where b.borrowcode in (select borrowcode from Tb_borrowmsg where msgid in (select borrowmsgid from Tb_task where taskid = ?1))")
    List<Tb_borrowdoc> findByTaskid(String taskid);
}