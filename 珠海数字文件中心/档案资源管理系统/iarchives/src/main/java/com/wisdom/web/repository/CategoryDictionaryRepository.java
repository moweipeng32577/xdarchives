package com.wisdom.web.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.wisdom.web.entity.Tb_category_dictionary;

public interface CategoryDictionaryRepository extends JpaRepository<Tb_category_dictionary, Integer> ,
	JpaSpecificationExecutor<Tb_category_dictionary>{
		
	Tb_category_dictionary findByCategoryid(String categoryid);
	
	@Query(value = "select c.name from Tb_category_dictionary c where c.parentid in"
			+ "(select categoryid from Tb_category_dictionary where name = ?1)")
	List<String> getName(String name);

	List<Tb_category_dictionary> findByParentidIsNull();
	
	/**
	 * 批量删除
	 * @param categoryid
	 * @return
	 */
	Integer deleteByCategoryidIn(String[] categoryid);
	
	@Modifying
    @Transactional
    @Query(value = "update Tb_category_dictionary set name = ?2, remark= ?3 where categoryid = ?1")
    int updateCategoryName(String categoryid,String name,String remark);
}