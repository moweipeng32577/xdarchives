package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_openbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

/**
 * Created by tanly on 2017/12/4 0004.
 */
public interface OpenboxRepository  extends JpaRepository<Tb_openbox, Integer> {
    /**
     * 根据用户id与数据id判断是否存在
     * @param userid
     * @param entryid
     * @return
     */
    List<Tb_openbox> findByUseridAndAndEntryid(String userid, String entryid);

    @Query(value = "select t.entryid from Tb_openbox t where userid = ?1 and entryid in ?2")
    Set<String> findByUseridAndAndEntryidIn(String userid, String[] entryid);

    List<Tb_openbox> findByUserid(String userid);

    Integer deleteByEntryidInAndUserid(String[] entryids,String userid);
}
