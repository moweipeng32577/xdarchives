package com.wisdom.web.repository;

import com.wisdom.web.entity.ReportData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by RonJiang on 2018/2/27 0027.
 */
public interface ReportDataRepository extends JpaRepository<ReportData, String>,JpaSpecificationExecutor<ReportData> {

    @Query(value = "select sad.id, sei.entryid,sad.pagename,sad.count,sad.filepahenum,sad.bz,sed.f49,sed.f50,sad.biscopyed,sad.czjd from szh_entry_index_capture  sei LEFT JOIN szh_entry_detail_capture sed on sei.entryid = sed.entryid LEFT JOIN szh_electronic_capture  sec on sei.entryid = sec.entryid LEFT JOIN szh_attr_define sad on sad.mediaid = sec.eleid  where sad.filepahenum is not null  and sad.filepahenum != '' and sei.entryid =?1  order by sad.filepahenum+0" ,nativeQuery = true)
    List<ReportData> findReportData(String entryid);

    List<ReportData> findByPagenameAndEntryid(String pagename, String entryid);

    @Modifying
    @Query(value = "insert into report_data values(?1,?2,?3,?4,?5,?6,?7,?8,?9,?10)" ,nativeQuery = true)
    Integer insertFristData(String id, String entryid, String pagename, String count, String filepahenum, String bz, String f49, String f50, String biscopyed, String czjd);

//    @Modifying
//    @Query(value = "insert into report_data('id','entryid','pagename','count','filepahenum','bz','f49','f50') values(?1,?2,?3,?4,?5,?6,?7,?8)" ,nativeQuery = true)
//    Integer insertData(String id, String entryid, String pagename,String count,String filepahenum,String bz, String f49,String f50);

    @Modifying
    @Query(value = "delete from report_data",nativeQuery = true)
    Integer delectData();
}
