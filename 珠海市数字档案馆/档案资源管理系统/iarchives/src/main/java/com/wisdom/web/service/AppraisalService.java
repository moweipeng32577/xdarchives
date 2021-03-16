package com.wisdom.web.service;

import com.wisdom.util.GainField;
import com.wisdom.util.LogAop;
import com.wisdom.web.entity.Tb_bill;
import com.wisdom.web.entity.Tb_bill_entry;
import com.wisdom.web.entity.Tb_entry_index;
import com.wisdom.web.repository.BillEntryIndexRepository;
import com.wisdom.web.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by RonJiang on 2018/4/20 0020.
 */
@Service
@Transactional
public class AppraisalService {

    @Autowired
    BillRepository billRepository;

    @Autowired
    BillEntryIndexRepository billEntryIndexRepository;

    public Page<Tb_bill> findBillBySearch(int page, int limit, String condition, String operator, String content, String nodeid){
        Specification<Tb_bill> searchNodeidCondition = getSearchNodeidCondition(new String[]{nodeid});
        Specifications specifications = Specifications.where(searchNodeidCondition);
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        Specification<Tb_bill> searchSubmitterCondition = getSearchSubmitterCondition(LogAop.getCurrentOperateuserRealname());
        PageRequest pageRequest = new PageRequest(page-1,limit);
        return billRepository.findAll(specifications.and(searchSubmitterCondition),pageRequest);
    }

    public Page<Tb_bill_entry> findBillEntryBySearch(int page, int limit, String condition, String operator, String content, String[] entryidData){
        Specification<Tb_bill_entry> searchEntryidsCondition = getSearchEntryidsCondition(entryidData);
        Specifications sp = Specifications.where(searchEntryidsCondition);
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit, new Sort("archivecode"));
        return billEntryIndexRepository.findAll(sp, pageRequest);
    }

    public String[] getEntryidsByBillid(String billid){
        List<Tb_bill_entry> billEntryList = billEntryIndexRepository.findByBillid(billid);
        return GainField.getFieldValues(billEntryList, "entryid").length == 0 ? new String[]{""} : GainField.getFieldValues(billEntryList, "entryid");
    }

    public static Specification<Tb_bill> getSearchNodeidCondition(String[] nodeids){
        Specification<Tb_bill> searchNodeID = null;
        if(nodeids!=null){
            if(nodeids.length>0){
                searchNodeID = new Specification<Tb_bill>() {
                    @Override
                    public Predicate toPredicate(Root<Tb_bill> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                        Predicate[] predicates = new Predicate[nodeids.length];
                        for(int i=0;i<nodeids.length;i++){
                            predicates[i] = criteriaBuilder.equal(root.get("nodeid"),nodeids[i]);
                        }
                        return criteriaBuilder.or(predicates);
                    }
                };
            }
        }
        return searchNodeID;
    }

    public static Specification<Tb_bill> getSearchSubmitterCondition(String submitter){
        Specification<Tb_bill> searchSubmitterCondition = null;
        searchSubmitterCondition = new Specification<Tb_bill>() {
            @Override
            public Predicate toPredicate(Root<Tb_bill> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get("submitter"),submitter);
                return criteriaBuilder.or(p);
            }
        };
        return searchSubmitterCondition;
    }

    public static Specification<Tb_bill_entry> getSearchEntryidsCondition(String[] entryidArr){
        Specification<Tb_bill_entry> searchEntryidsCondition = null;
        searchEntryidsCondition = new Specification<Tb_bill_entry>() {
            @Override
            public Predicate toPredicate(Root<Tb_bill_entry> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                CriteriaBuilder.In in = criteriaBuilder.in(root.get("entryid"));
                for (String entryid:entryidArr) {
                    in.value(entryid);
                }
                return criteriaBuilder.or(in);
            }
        };
        return searchEntryidsCondition;
    }
}
