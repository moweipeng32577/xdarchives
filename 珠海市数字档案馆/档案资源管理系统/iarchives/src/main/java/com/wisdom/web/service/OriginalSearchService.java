package com.wisdom.web.service;

import com.wisdom.web.entity.ExtTree;
import com.wisdom.web.entity.Tb_electronic;
import com.wisdom.web.repository.ElectronicRepository;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RonJiang on 2017/11/2 0002.
 */
@Service
@Transactional
public class OriginalSearchService {
    @Autowired
    ElectronicRepository electronicRepository;
    
    @Autowired
    EntryIndexService entryIndexService;

    @PersistenceContext
    EntityManager entityManager;

    public Page<Tb_electronic> findBySearch(int page, int limit, String condition, String operator, String content, Sort sort){
        SecurityUser userDetails=(SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId=userDetails.getUserid();
        String searchCondition = entryIndexService.getSqlByConditions(condition, content, "te");
        List<Tb_electronic> resultList = new ArrayList<>();
        String sql = "select * from tb_electronic te inner join (select entryid from tb_entry_index tei inner join (select nodeid from tb_user_data_node where userid='"+userId+"' union select nodeid from  tb_role_data_node where roleid in (select roleid from tb_user_role where userid='"+userId+"'"+"))a  on tei.nodeid=a.nodeid )b  on te.entryid=b.entryid "+
                " where "+searchCondition;
        String countSql = "select count(*) from tb_electronic te inner join (select entryid from tb_entry_index tei inner join (select nodeid from tb_user_data_node where userid='"+userId+"' union select nodeid from  tb_role_data_node where roleid in (select roleid from tb_user_role where userid='"+userId+"'"+"))a  on tei.nodeid=a.nodeid )b  on te.entryid=b.entryid "+
                " where "+searchCondition;
        if (content == null||"".equals(content.trim())) {
            sql = "select * from tb_electronic te inner join (select entryid from tb_entry_index tei inner join (select nodeid from tb_user_data_node where userid='"+userId+"' union select nodeid from  tb_role_data_node where roleid in (select roleid from tb_user_role where userid='"+userId+"'"+"))a  on tei.nodeid=a.nodeid )b  on te.entryid=b.entryid ";
            countSql = "select count(*) from tb_electronic te inner join (select entryid from tb_entry_index tei inner join (select nodeid from tb_user_data_node where userid='"+userId+"' union select nodeid from  tb_role_data_node where roleid in (select roleid from tb_user_role where userid='"+userId+"'"+"))a  on tei.nodeid=a.nodeid )b  on te.entryid=b.entryid ";
        }
        Query query = entityManager.createNativeQuery(sql,Tb_electronic.class);
        query.setFirstResult((page-1)*limit);
        query.setMaxResults(limit);
        resultList = query.getResultList();

        query = entityManager.createNativeQuery(countSql);
        int count = Integer.parseInt(query.getResultList().get(0) + "");
        PageRequest pageRequest = new PageRequest(page-1,limit,sort==null?new Sort("filename"):sort);
        return new PageImpl((List<Tb_electronic>)resultList,pageRequest,count);
    }

    public List<ExtTree> getAllEleName(){
        List<ExtTree> extTrees = new ArrayList<ExtTree>();
        List<Tb_electronic> electronics = electronicRepository.findAll();
        ExtTree tree = null;
        if(electronics!=null){
            for(Tb_electronic electronic:electronics){
                ExtTree extTree = new ExtTree();
                extTree.setCls("folder");
                extTree.setFnid(electronic.getEleid());
                extTree.setText(electronic.getFilename());
                extTree.setLeaf(true);
                extTree.setChecked(false);
                extTrees.add(extTree);
            }
        }
        return extTrees;
    }

    public static Specification<Tb_electronic> getEntryidIsNotNullCondition(){
        Specification<Tb_electronic> entryidIsNotNull = new Specification<Tb_electronic>() {
            @Override
            public Predicate toPredicate(Root<Tb_electronic> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate isNotNull = criteriaBuilder.isNotNull(root.get("entryid"));
                return criteriaBuilder.and(isNotNull);
                //Predicate notEqual = criteriaBuilder.notEqual(root.get("entryid"), "");
                //return criteriaBuilder.and(isNotNull, notEqual);
            }
        };
        return entryidIsNotNull;
    }
}
