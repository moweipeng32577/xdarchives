package com.wisdom.web.service;

import com.wisdom.web.entity.ExtNcTree;
import com.wisdom.web.entity.Tb_accredit;
import com.wisdom.web.repository.AccreditRepository;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SunK on 2020/5/6 0006.
 */
@Service
@Transactional
public class AccreditMetadataService {


    @Autowired
    AccreditRepository accreditRepository;

    public List<ExtNcTree> getMetadataTree(){
        List<ExtNcTree> extNcTrees = new ArrayList<>();
        ExtNcTree e1 = new ExtNcTree();
        e1.setText("业务元数据");
        e1.setFnid("1");
        e1.setLeaf(true);
        e1.setExpanded(false);
        e1.setCls("file");
        ExtNcTree e2 = new ExtNcTree();
        e2.setText("机构元数据");
        e2.setFnid("2");
        e2.setLeaf(true);
        e2.setExpanded(false);
        e2.setCls("file");
        extNcTrees.add(e1);
        extNcTrees.add(e2);
        return extNcTrees;
    }



    public List<ExtNcTree> findByParentconfigid(String parentconfigid) {
        List<Tb_accredit> accredits;
        if ("".equals(parentconfigid)) {
            accredits = accreditRepository.findByParentidIsNullOrderBySortsequence();
        } else {
            accredits = accreditRepository.findByParentidOrderBySortsequence(parentconfigid);
        }
        List<ExtNcTree> extNcTrees = new ArrayList<>();

        for (int i = 0; i < accredits.size(); i++) {
            ExtNcTree tree = new ExtNcTree();
            tree.setFnid(accredits.get(i).getAid());
            List<Tb_accredit> lists = accreditRepository
                    .findByParentidOrderBySortsequence(accredits.get(i).getAid());
            // if (!lists.isEmpty()) {//有子节点
            // tree.setCls("folder");
            // tree.setLeaf(false);
            // } else {
            tree.setCls("file");
            tree.setLeaf(true);
            // }
            tree.setText(accredits.get(i).getShortname());
            extNcTrees.add(tree);
        }
        return extNcTrees;
    }

    public Tb_accredit findByConfigid(String configid) {
        return accreditRepository.findByAid(configid);
    }

    public Tb_accredit findByParentconfigidAndConfigvalue(String parentconfigid, String value) {
        return accreditRepository.findByParentidAndText(parentconfigid, value);
    }

    public Tb_accredit saveSystemConfig(Tb_accredit tb_accredit) {
        return accreditRepository.save(tb_accredit);
    }

    public Tb_accredit findByParentconfigidAndConfigcode(String parentconfigid, String code) {
        return accreditRepository.findByParentidAndShortname(parentconfigid, code);
    }

    public Tb_accredit findByConfigvalue(String value) {
        return accreditRepository.findByTextAndParentidIsNull(value);
    }

    public Tb_accredit findByConfigcode(String code) {
        return accreditRepository.findByShortnameAndParentidIsNull(code);
    }


    public Page<Tb_accredit> findBySearch(int page, int limit, String condition, String operator, String content,
                                               String configid, Sort sort) {
        PageRequest pageRequest = new PageRequest(page - 1, limit,
                sort == null ? new Sort(Sort.Direction.ASC, "sortsequence") : sort);
        Specification<Tb_accredit> searchid;
        if ("".equals(configid)) {
            searchid = getSearchParentconfigidIsnullCondition();
        } else {
            searchid = getSearchParentconfigidEqualCondition(configid);
        }
        Specifications specifications = Specifications.where(searchid);
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        return accreditRepository.findAll(specifications, pageRequest);
    }

    public static Specification<Tb_accredit> getSearchParentconfigidIsnullCondition() {
        Specification<Tb_accredit> searchParentidIsnullCondition = new Specification<Tb_accredit>() {
            @Override
            public Predicate toPredicate(Root<Tb_accredit> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.isNull(root.get("parentid"));
                return criteriaBuilder.or(p);
            }
        };
        return searchParentidIsnullCondition;
    }

    public static Specification<Tb_accredit> getSearchParentconfigidEqualCondition(String parentid) {
        Specification<Tb_accredit> searchParentidEqualCondition = new Specification<Tb_accredit>() {
            @Override
            public Predicate toPredicate(Root<Tb_accredit> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get("parentid"), parentid);
                return criteriaBuilder.or(p);
            }
        };
        return searchParentidEqualCondition;
    }


    public Integer deleteByAidIn(String[] configids) {
        return accreditRepository.deleteByAidIn(configids);
    }

    public Integer deleteByAid(String configid) {
        if (accreditRepository.findByParentid(configid).size() > 0) {
            accreditRepository.deleteByParentid(configid);
        }
        return accreditRepository.deleteByAid(configid);
    }
}
