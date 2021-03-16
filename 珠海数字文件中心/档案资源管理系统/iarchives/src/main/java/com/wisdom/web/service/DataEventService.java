package com.wisdom.web.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wisdom.web.entity.Tb_data_event;
import com.wisdom.web.repository.DataEventRepository;
import com.wisdom.web.repository.EventEntryRepository;

@Service
@Transactional
public class DataEventService {

	@Autowired
	DataEventRepository dataEventRepository;
	
	@Autowired
	EventEntryRepository eventEntryRepository;

	public Page<Tb_data_event> findBySearch(String eventid, String condition, String operator, String content,
			int page, int limit, Sort sort) {
		Specifications sp = null;
		Specification<Tb_data_event> searchDataEventCondition = null;
		if (eventid != null && !"".equals(eventid)) {
			searchDataEventCondition = getSearchDataEventCondition(eventid);
			sp = Specifications.where(searchDataEventCondition);
		}
		if (content != null) {
			sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
		}
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(new Sort.Order(Sort.Direction.DESC, "createdate"));//置顶
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort(sorts) : sort);
		return dataEventRepository.findAll(sp, pageRequest);
	}

	public static Specification<Tb_data_event> getSearchDataEventCondition(String eventid) {
		Specification<Tb_data_event> searchDataEventCondition = new Specification<Tb_data_event>() {
			@Override
			public Predicate toPredicate(Root<Tb_data_event> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				Predicate p = criteriaBuilder.equal(root.get("eventid"), eventid);
				return criteriaBuilder.and(p);
			}
		};
		return searchDataEventCondition;
	}
}