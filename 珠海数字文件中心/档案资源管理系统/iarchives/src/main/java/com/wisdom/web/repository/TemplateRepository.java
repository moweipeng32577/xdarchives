package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_data_template;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

/**
 * Created by Rong on 2017/10/30.
 */
public interface TemplateRepository extends JpaRepository<Tb_data_template, String>,
        JpaSpecificationExecutor<Tb_data_template> {

    @Query(value = "select t from Tb_data_template t where qfield = 1 and nodeid = ?1 order by qsequence")
    List<Tb_data_template> findQueryByNode(String nodeid);

    @Query(value = "select t from Tb_data_template t where ffield = 1 and nodeid = ?1 order by fsequence")
    List<Tb_data_template> findFormByNode(String nodeid);

    @Query(value = "select * from tb_data_template where nodeid = ?1 and (fieldcode in (select fieldcode from  tb_codeset where datanodeid=?1) or fieldcode='archivecode') union  select * from tb_data_template t where archivecodeedit = 1 and nodeid = ?1",nativeQuery = true)
    List<Tb_data_template> findEditFormAndCodesetByNode(String nodeid);

    @Query(value = "select t from Tb_data_template t where archivecodeedit = 1 and nodeid = ?1 order by fsequence")
    List<Tb_data_template> findEditFormByNode(String nodeid);

    @Query(value = "select t from Tb_data_template t where gfield = 1 and nodeid = ?1 order by gsequence")
    List<Tb_data_template> findGridByNode(String nodeid);

    @Query(value = "select fieldcode from Tb_data_template where ffield = 1 and ftype='daterange' and nodeid = ?1")
    String findFieldCodeByNodeid(String nodeid);
    
    @Query(value = "select fenums from Tb_data_template where fieldcode='entryretention' and nodeid = ?1")
    String findFEnumsByNodeid(String nodeid);
    
    @Query(value = "select ftype from Tb_data_template where fieldcode = 'organ' and nodeid = ?1")
    String findOrganFtypeByNodeid(String nodeid);
    
    @Query(value = "select fdefault from Tb_data_template where fieldcode = ?1 and nodeid = ?2")
    String findFdefaultByFieldcodeAndNodeid(String field, String nodeid);

    @Query(value = "select fieldname from Tb_data_template where fieldcode = ?1 and nodeid = ?2")
    String findFieldNameByFieldcodeAndNodeid(String fieldcode, String nodeid);
    
    Page<Tb_data_template> findByNodeid(Pageable pageable, String nodeid);

    @Query(value = "select t from Tb_data_template t where t.nodeid = ?1 order by fsequence")
    List<Tb_data_template> findByNodeid(String nodeid);

    @Query(value = "select * from tb_data_template_sx where nodeid = ?1",nativeQuery = true)
    List<Tb_data_template> findSxByNodeid(String nodeid);

    List<Tb_data_template> findByNodeidOrderByFsequence(String nodeid);
    
    List<Tb_data_template> findByNodeidAndFfieldOrderByFsequence(String nodeid, boolean ffield);

    List<Tb_data_template> findByNodeidAndFieldtableAndFieldcodeNotIn(String datanodeid,String fieldtable, String[] excludes);

    Tb_data_template findByTemplateid(String templateid);
    
    @Query(value = "select ftype from Tb_data_template where fieldcode = ?1 and nodeid=?2")
    String findFtypeByFieldcodeAndNodeid(String fieldcode, String nodeid);

    List<Tb_data_template> findByFenums(String fenums);

    List<Tb_data_template> findByNodeidOrderByGsequence(String nodeid);

    List<Tb_data_template> findByNodeidIn(String[] nodeId);
    
    @Query(value = "select fieldcode from Tb_data_template where ftype = 'enum' and nodeid = ?1")
    List<String> findFieldcodeByNodeid(String nodeid);
    
    @Query(value = "select fieldcode from Tb_data_template where freadonly = '1' and nodeid = ?1")
    List<String> findFieldonly(String nodeid);
    
    @Query(value = "select fieldcode from Tb_data_template where nodeid = ?1 and fieldtable != 'tb_entry_detail'")
    List<String> findCodeByNodeid(String nodeid);
    
    @Query(value = "select fieldcode from Tb_data_template where nodeid = ?1 and fieldtable = ?2")
    List<String> findNameByFieldtable(String nodeid, String fieldtable);

    Integer deleteByNodeidIn(String[] nodeids);

    Integer deleteByNodeid(String nodeid);
    
    //跟新卷内文件总数字段设置
    @Query(value = "select t from Tb_data_template t where t.fieldname='卷内文件数' and t.fieldcode not like 'f02'")
    List<Tb_data_template> findNodeids();

    @Query(value = "select t from Tb_data_template t where t.nodeid=?1 and t.fieldcode = 'f02'")
    List<Tb_data_template> findByNodeidAndFieldcode(String nodeid);
    
    Tb_data_template findByNodeidAndFieldcode(String nodeid, String fieldcode);

    @Modifying
    @Query(value = "update Tb_data_template t set t.fieldname='卷内文件数',t.fieldcode='f02',t.fieldtable='tb_entry_detail' where t.templateid=?1")
    Integer updateFieldF02(String templateid);

    @Modifying
    @Query(value = "update Tb_data_template t set t.fieldcode=?2,t.fieldtable='tb_entry_detail' where t.templateid=?1")
    Integer updateFieldCbFjn(String templateid,String fieldcode);

    @Modifying
    @Query(value = "update Tb_data_template t set t.fieldcode=?2,t.fieldtable='tb_entry_index' where t.templateid=?1")
    Integer updateFieldZbFjn(String templateid,String fieldcode);

    /**
     * create by lihj on 2018/02/28 快速调整字段
     */
    @Modifying
    @Query("update Tb_data_template t set t.gfield=?2 where t.templateid in ?1")
    Integer updateGfieldToSet(String[] templateid,boolean setParam);

    @Modifying
    @Query("update Tb_data_template t set t.qfield=?2 where t.templateid in ?1")
    Integer updateQfieldToSet(String[] templateid,boolean setParam);

    @Modifying
    @Query("update Tb_data_template t set t.ffield=?2 where t.templateid in ?1")
    Integer updateFfieldToSet(String[] templateid,boolean setParam);

    @Modifying
    @Query("update Tb_data_template t set t.gsequence=t.gsequence+?2 where t.templateid in ?1")
    Integer updateGfieldSequence(String[] templateid,long setParam);

    @Modifying
    @Query("update Tb_data_template t set t.qsequence=t.qsequence+?2 where t.templateid in ?1")
    Integer updateQfieldSequence(String[] templateid,long setParam);

    @Modifying
    @Query("update Tb_data_template t set t.fsequence=t.fsequence+?2 where t.templateid in ?1")
    Integer updateFfieldSequence(String[] templateid,long setParam);
    
    @Modifying
    @Query("update Tb_data_template t set t.ffield = ?1,t.gfield = ?2 where nodeid = ?3 and fieldcode = ?4")
    Integer updateFsquenceAndGfield(boolean ffield, boolean gfield, String nodeid, String fieldcode);
    
    @Modifying
    @Query("update Tb_data_template t set t.ffield = ?1,t.qfield = ?2 where nodeid = ?3 and fieldcode = ?4")
    Integer updateFsquenceAndQfield(boolean ffield, boolean qfield, String nodeid, String fieldcode);
    
    @Modifying
    @Query("update Tb_data_template t set t.gfield = ?1,t.gsequence = ?2 where nodeid = ?3 and fieldcode = ?4")
    Integer updateGequence(boolean gfield, long gsequence, String nodeid, String fieldcode);
    
    @Modifying
    @Query("update Tb_data_template t set t.qfield = ?1,t.qsequence = ?2 where nodeid = ?3 and fieldcode = ?4")
    Integer updateQequence(boolean qfield, long qsequence, String nodeid, String fieldcode);

    @Modifying
    @Query(value = "update tb_data_template set fdefault=?1 where nodeid in (select nodeid from tb_data_node where organid in ?2) and fieldcode='funds'",nativeQuery = true)
    Integer updateFunds(String funds, String[] organidArr);

    @Query(value = "select t from Tb_data_template t where nodeid = ?1 order by fsequence ASC")
    List<Tb_data_template> findFieldCodesByNodeid(String nodeid);
    
    @Query(value = "select t from Tb_data_template t where t.nodeid = ?1 and t.fieldname in (?2) order by t.fieldcode,t.fieldname")
    Tb_data_template findFieldCodeByNodeidAndFieldNameIn(String nodeid, String[] fieldname);

    @Query(value = "select t from Tb_data_template t where t.nodeid = ?1 and (t.fieldname in (?2) or t.fieldcode in (?2))")
    Tb_data_template findFieldCode(String nodeid, String[] fieldname);
    
    @Query(value = "select fieldcode from Tb_data_template where nodeid = ?1 and fieldname = ?2 order by fieldcode,fieldname")
    String findFieldCodeByNodeidAndFieldName(String nodeid, String fieldname);

    @Query(value = "select t from Tb_data_template t where nodeid = ?1 order by fsequence ASC")
    List<Tb_data_template> findFieldNamesByNodeid(String nodeid);

    @Query(value = "select t from Tb_data_template t where t.ftype=?1 and t.nodeid=?2 and t.fieldcode in (select c.fieldcode from Tb_codeset c where c.datanodeid=?2)")
    List<Tb_data_template> getByNodeidFtype(String ftype,String nodeid);

    @Query(value = "select distinct nodeid from Tb_data_template where nodeid in ?1")
    Set<String> getNodeidByNodeidIn(String[] nodeid);

    @Query(value = "select distinct nodeid from tb_data_template_sx where nodeid in ?1",nativeQuery = true)
    Set<String> getSxNodeidByNodeidIn(String[] nodeid);

    @Query(value = "select t from Tb_data_template t where nodeid = ?1 order by fsequence")
    List<Tb_data_template> findByNodeOrderByFs(String nodeid);

    @Query(value = "select count(*) from Tb_data_template where nodeid = ?1 and  fieldname = ?2 and templateid != ?3 ")
    Integer findCount(String nodeid,String fieldname,String templateid);

    @Query(value = "select fieldcode from Tb_data_template where nodeid = ?1 order by fsequence")
    String[] findFieldCodesByNodeidOrderfs(String nodeid);

    @Query(value = "select t.metadataid from Tb_data_template t where t.qfield = 1 and t.metadataid is not null and t.nodeid = ?1 order by t.qsequence")
    List<String> findMetadataQueryByNode(String nodeid);

    @Query(value = "select t.fieldcode from Tb_data_template t where t.metadataid = ?1")
    String findFieldcodeByMetadataid(String metadataid);

    @Query(value = "select count(t) from Tb_data_template t where t.metadataid = ?1 and t.nodeid = ?2")
    Integer findCountByMetadataidAndNodeid(String metadataid,String nodeid);

    @Query(value = "select distinct t.fieldname from Tb_data_template t where t.fieldcode = ?1")
    String[]  getOrderFileName(String fieldcode);

    @Query(value = "select distinct (t.fieldcode) from Tb_data_template t ")
    List<String>  getAllFilecode();

}