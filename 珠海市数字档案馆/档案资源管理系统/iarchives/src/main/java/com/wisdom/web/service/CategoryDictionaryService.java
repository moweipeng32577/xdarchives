package com.wisdom.web.service;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wisdom.web.entity.Tb_category_dictionary;
import com.wisdom.web.repository.CategoryDictionaryRepository;

@Service
@Transactional
public class CategoryDictionaryService {
	
	@Autowired
	CategoryDictionaryRepository categoryDictionaryRepository;
	
	public List<String> getFilingyear() {
		return categoryDictionaryRepository.getName("年度");
	}
	
	public List<String> getEntryretention() {
		return categoryDictionaryRepository.getName("保管期限");
	}
	
	public List<String> getOrgan() {
		return categoryDictionaryRepository.getName("机构问题");
	}
	
	public List<Tb_category_dictionary> getCategoryDictionary(){
        return categoryDictionaryRepository.findByParentidIsNull();
    }
	
	public Page<Tb_category_dictionary> findBySearch(String categoryid,String condition, String operator, String content, int page, int limit){
        Specifications sp = null;
        Specification<Tb_category_dictionary> searchCategoryidCondition = null;
        if (categoryid != null && !"".equals(categoryid)) {
        	searchCategoryidCondition = getSearchCategoryidCondition(categoryid);
            sp = Specifications.where(searchCategoryidCondition);
        } else {
        	searchCategoryidCondition = getSearchParentidIsNull();
            sp = Specifications.where(searchCategoryidCondition);
        }
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        return categoryDictionaryRepository.findAll(sp,pageRequest);
    }
	
	public static Specification<Tb_category_dictionary> getSearchParentidIsNull(){
        Specification<Tb_category_dictionary> searchCategoryidCondition = new Specification<Tb_category_dictionary>() {
            @Override
            public Predicate toPredicate(Root<Tb_category_dictionary> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate[] p = new Predicate[1];
        	    p[0] = criteriaBuilder.isNull(root.get("parentid"));//名字形同
                return criteriaBuilder.and(p);
            }
        };
        return searchCategoryidCondition;
    }
	
	public static Specification<Tb_category_dictionary> getSearchCategoryidCondition(String categoryid){
        Specification<Tb_category_dictionary> searchCategoryidCondition = new Specification<Tb_category_dictionary>() {
            @Override
            public Predicate toPredicate(Root<Tb_category_dictionary> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate[] p = new Predicate[1];
        	    p[0] = criteriaBuilder.equal(root.get("parentid"), categoryid);//名字形同
                return criteriaBuilder.and(p);
            }
        };
        return searchCategoryidCondition;
    }
	
	public Tb_category_dictionary saveCategory(Tb_category_dictionary category) {
		Tb_category_dictionary info = categoryDictionaryRepository.findByCategoryid(category.getParentid());
		if (info != null) {
			// 如果字词已经存在
			if (info.getCategoryid().equals(category.getParentid()) && info.getName().equals(category.getName())) {
				return null;
			}
		}
		return categoryDictionaryRepository.save(category);
	}
	
	public int updateCategoryName(String categoryid,String name,String remark) {
		return categoryDictionaryRepository.updateCategoryName(categoryid, name, remark);
	}
	
	public Integer delCategory(String[] categoryidData){
        return categoryDictionaryRepository.deleteByCategoryidIn(categoryidData);
	}
}