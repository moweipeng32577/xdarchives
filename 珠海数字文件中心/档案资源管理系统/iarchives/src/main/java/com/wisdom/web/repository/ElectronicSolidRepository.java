package com.wisdom.web.repository;

import com.wisdom.web.entity.Tb_electronic_solid;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Rong on 2017/11/21.
 */
public interface ElectronicSolidRepository extends JpaRepository<Tb_electronic_solid, String> {

	Tb_electronic_solid findByEleid(String eleid);

	List<Tb_electronic_solid> findByEntryidOrderBySortsequence(String entryid);

	List<Tb_electronic_solid> findByEleidInOrderBySortsequence(String[] eleids);

	@Modifying
    @Transactional
	int deleteByEntryid(String entryId);
	
	@Modifying
    @Transactional
	int deleteByEntryidAndElectronicidIn(String entryId, String[] eleid);

	Integer deleteByEntryidIn(String[] entryidData);

	List<Tb_electronic_solid> findByEleidIn(String[] eleids, Sort sort);

	List<Tb_electronic_solid> findByEntryid(String entryid, Sort sort);

	Tb_electronic_solid findByElectronicid(String electronicid);

	List<Tb_electronic_solid> findByEntryid(String entryid);

    List<Tb_electronic_solid> findByEleidInOrderByFilename(String[] eleids);

	@Query(value = "select count(*) as count,sum(cast(FileSize as bigint)) as size  from tb_entry_index tei  left join  tb_electronic_solid te on tei.entryid = te.entryid where tei.eleid is not null and  te.filepath like '%/electronics/solidFile/%'",nativeQuery = true)
	String getCapacity();

	@Query(value = "select count(*) from tb_entry_index tei  left join  tb_electronic_solid te on tei.entryid = te.entryid where tei.eleid is not null and  te.filepath like '%/electronics/solidFile/%' and substring(tei.descriptiondate,0,10)<= ?1",nativeQuery = true)
	String getSumCapacity(String date);

	@Query(value = "select sum(cast(FileSize as bigint)) from tb_entry_index tei  left join  tb_electronic_solid te on tei.entryid = te.entryid where tei.eleid is not null and  te.filepath like '%/electronics/solidFile/%' and substring(tei.descriptiondate,0,10)<= ?1",nativeQuery = true)
	String getTotalCapacity(String date);

	List<Tb_electronic_solid> findByEntryidOrderByFilename(String entryid);
}