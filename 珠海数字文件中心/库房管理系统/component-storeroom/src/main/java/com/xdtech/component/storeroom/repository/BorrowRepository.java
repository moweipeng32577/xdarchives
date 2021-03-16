package com.xdtech.component.storeroom.repository;

import com.xdtech.component.storeroom.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 实体借阅档案关联库房位置
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/26.
 */
public interface BorrowRepository extends JpaRepository<Borrow, String> {

    @Query(value = "select doc.docid,doc.type as type,doc.borrowman as borrowman from st_borrow sb  left join tb_borrowdoc doc on sb.docid = doc.docid where sb.status = '0' group by sb.docid ",nativeQuery = true)
    List  findDocGroupByDocid();

    @Query(value = "select tei.archivecode,tei.title, sz.citydisplay,sz.zonedisplay,szs.coldisplay,szs.layerdisplay,szs.sectiondisplay,szs.sidedisplay from st_borrow sb left join  st_storage ss on sb.entryid = ss.entry left join st_zone_shelves szs on  ss.zone_shelves_shid = szs.shid left join st_zones sz on sz.zoneid = szs.zoneid  left join tb_entry_index tei on tei.entryid = sb.entryid where sb.docid=?1",nativeQuery = true)
    List findZoneByDocid(String docid);

    @Modifying
    @Transactional
    @Query(value = "update st_borrow set status = '1' where docid = ?1",nativeQuery=true)
    Integer updateMessageStatus(String docid);
}
