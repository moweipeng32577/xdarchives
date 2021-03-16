package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_entry_bookmarks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by nick on 2018/3/9.
 */
public interface EntryBookmarksRepository
		extends JpaRepository<Tb_entry_bookmarks, String>, JpaSpecificationExecutor<Tb_entry_bookmarks> {

	List<Tb_entry_bookmarks> findAllByUserid(String userid);

	int deleteAllByBookmarkid(String bookmarkid);

	Tb_entry_bookmarks findByUseridAndEntryid(String userid, String entryid);

	@Query(value = "select entryid from Tb_entry_bookmarks where userid=?1 and addstate='0'")
	List<String> findEntryidByUserid(String userid);

	Integer deleteByEntryidIn(String[] entryidData);

	Integer deleteByEntryidInAndUseridAndAddstate(String[] entryids, String userid,String addstate);

	@Query(value = "select trim(entryid) from Tb_entry_bookmarks where userid=?1 and addstate=?2")
	List<String> findEntryidByUseridandAddstate(String userid,String addstate);

	@Modifying
	@Query(value = "delete from Tb_entry_bookmarks  where entryid in (?1) and userid = ?2 and addstate=?3")
	Integer deleteEntryByUseridandAndEntryid(String[] entryid,String userid,String addstate);

	@Query(value = "select entryid from Tb_entry_bookmarks where userid=?1 and addstate=?2")
	List<String> findWareEntryid(String userid,String addstate);
}