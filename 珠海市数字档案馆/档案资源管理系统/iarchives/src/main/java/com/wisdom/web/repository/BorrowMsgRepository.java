package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_borrowdoc;
import com.wisdom.web.entity.Tb_borrowmsg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

/**
 * Created by Administrator on 2017/10/25 0025.
 */
public interface BorrowMsgRepository
		extends JpaRepository<Tb_borrowmsg, Integer>, JpaSpecificationExecutor<Tb_borrowmsg> {

	Tb_borrowmsg findByEntryid(String entryid);

	//@Query(value = "select entryid from Tb_borrowmsg where borrowdate =?1 ")
	List<Tb_borrowmsg> findAllByBorrowdate(String date);

	@Query(value = "select b from Tb_borrowmsg b where b.borrowcode in (select msgid from Tb_flows where taskid=?1)")
	List<Tb_borrowmsg> getBorrowmsgs(String taskid);

	// List<Tb_borrowmsg> findByBorrowcodeAndEntryid();
	List<Tb_borrowmsg> findByBorrowcodeInAndEntryidIn(String[] borrowcodes, String[] entryids);

	List<Tb_borrowmsg> findByBorrowcodeIn(String[] borrowCodes);

	@Query(value = "select b from Tb_borrowmsg b where b.borrowcode in (select borrowcode from Tb_borrowdoc where docid=?1)")
	List<Tb_borrowmsg> getBorrowmsgsByBorrowdocid(String borrowdocid);

	Page<Tb_borrowmsg> findByStateAndLyqx(Pageable pageable, String state, String lyqx);

	List<Tb_borrowmsg> findByMsgidIn(String[] ids);

	@Query(value = "select count(b) from Tb_borrowmsg b where b.entryid = ?1 and state = '未归还'")
	String findCountByEntryid(String entryid);

	Integer deleteByEntryidIn(String[] entryids);

	List<Tb_borrowmsg> findByApproverInAndState(String[] approvers, String state);

	Integer deleteByBorrowcodeAndEntryidIn(String borrowcode, String[] entryids);

	Integer deleteByBorrowcodeIn(String[] borrowcode);

	@Query(value = "select b from Tb_borrowmsg b where b.borrowcode = ?1 and state = '未归还'")
	List<Tb_borrowmsg> findByBorrowcodeAndState(String borrowcode);

	Tb_borrowmsg findByBorrowcodeAndEntryid(String borrowcode,String entryid);
	

	@Query(value = "select b from Tb_borrowmsg b where b.borrowcode = ?1 and state = '已归还'")
	List<Tb_borrowmsg> findYGByBorrowcodeAndState(String borrowcode);

	@Query(value = "select b from Tb_borrowmsg b where b.borrowcode = ?1 and b.entryid =?2")
	List<Tb_borrowmsg> findBycodeAndEntryid(String borrowCodes,String entryid);

	@Query(value = "select b.entryid from Tb_borrowmsg b where b.borrowcode in (?1) and (b.type='实体查档' or b.type='电子、实体查档' or b.type='调档') and b.lyqx='查看'")
	String[] getBorrowMsgEntryid(String[] borrowcodes);

	@Query(value = "select distinct b.borrowcode from Tb_borrowmsg b where  b.type='实体查档' or b.type='电子、实体查档' or b.type='调档'")
	List<String> findBorrowcodes();

	@Query(value = "select b from Tb_borrowmsg b where b.borrowcode in (select borrowcode from Tb_borrowdoc where docid=?1) and (b.type='实体查档' or b.type='电子、实体查档' or b.type='调档') and b.lyqx='查看'")
	List<Tb_borrowmsg> getBorrowmsgsByOutware(String borrowdocid);

	List<Tb_borrowmsg> findByEntryidInAndLyqxAndState(String[] entryids,String lyqx,String state);

    @Query(value = "select b.entryid from Tb_borrowmsg b where b.lyqx=?1")
    List<String> findByLyqxAndState(String lyqx);

    @Query(value = "select b from Tb_borrowmsg b where b.entryid in (?1) and (b.type='实体查档' or b.type='电子、实体查档') and (b.lyqx='查看' or b.lyqx='借出') and b.state = ?2")
    List<Tb_borrowmsg> findByEntryidAndTypeAndLyqxAndState(String[] entryids,String state);

    List<Tb_borrowmsg> findByBorrowcodeInAndType(String[] borrowdocs, String type);

    @Query(value = "select b from Tb_borrowmsg b where b.type='调档' and b.borrowcode in (select borrowcode from Tb_borrowdoc where docid in (?1))")
    List<Tb_borrowmsg> getBorrowmsgsByBorrowdocidAndType(String[] borrowdocid);
}