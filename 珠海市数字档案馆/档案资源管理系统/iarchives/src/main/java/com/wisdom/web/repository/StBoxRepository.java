package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_st_box;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Administrator on 2017/10/25 0025.
 */
public interface StBoxRepository extends JpaRepository<Tb_st_box, String> {

    /**
     * 根据用户id查找查档添加项条目
     * @param userid
     * @return
     */
    List<Tb_st_box> findByUseridAndBorrowtype(String userid,String borrowType);

    /**
     * 根据用户id与数据id判断是否存在
     * @param userid
     * @param entryid
     * @return
     */
    List<Tb_st_box> findByUseridAndEntryidAndBorrowtype(String userid,String entryid,String borrowType);

    Integer deleteByEntryidInAndUserid(String[] entryids,String userid);

    Integer deleteByEntryidInAndUseridAndBorrowtype(String[] entryids,String userid,String borrowType);

    List<Tb_st_box> findByUseridAndEntryidInAndBorrowtype(String userid,String[] entryids,String borrowType);
}
