package com.wisdom.web.service;

import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.controller.ClassifySearchController;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Rong on 2017/10/31.
 */
@Service
@Transactional
public class IndexLyService {

    @Autowired
    InformService informService;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    InFormRepository inFormRepository;

    @Autowired
    SimpleSearchService simpleSearchService;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    BorrowDocRepository borrowDocRepository;

    @Autowired
    FlowsRepository flowsRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    QuestionnaireService questionnaireService;

    public Page<Tb_inform> getinform(int page, int limit){
        Specifications sp = null;
        List<Tb_inform_user> inform_users = informService.getInformUsers();
        Specification<Tb_inform> searchIdDate = InformService.getSearchInformidAndDateCondition(inform_users);
        sp = Specifications.where(searchIdDate);
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(new Sort.Order(Sort.Direction.ASC,"stick"));//发布时间降序
        sorts.add(new Sort.Order(Sort.Direction.DESC,"informdate"));//发布时间降序
        PageRequest pageRequest = new PageRequest(page-1,limit,new Sort(sorts));
        return inFormRepository.findAll(sp,pageRequest);
    }

    public List<Tb_entry_index> findOpenEntries(String openType){
        Set<String> organidSet = simpleSearchService.getOrganNodeByUser();//获取机构数据节点
        String[] organArr = new String[organidSet.size()];
        organidSet.toArray(organArr);
        List<Tb_data_node> nodeList = dataNodeRepository.findByRefidIn(organArr);
        List<String> nodeidList = new ArrayList<>();
        for (Tb_data_node node:nodeList){
            nodeidList.add(node.getNodeid());
        }

        return entryIndexRepository.findAll(Specifications.where(simpleSearchService.getSearchOpenCondition(openType)).and(simpleSearchService.getSearchNodeidCondition(nodeidList)));
    }

    public Page<Tb_borrowdoc> getBorrowFinishMsg(int page,int limit){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Sort sort = new Sort(Sort.Direction.DESC, "finishtime");
        PageRequest pageRequest=new PageRequest(page-1,limit,sort);
        Page<Tb_borrowdoc> borrowdocs = borrowDocRepository.getByStateAndClearstate(userDetails.getUserid(),pageRequest);
        return borrowdocs;
    }

    public List<Tb_borrowdoc> getBorrowMsg(){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Sort sort = new Sort(Sort.Direction.DESC, "borrowdate");
        PageRequest pageRequest=new PageRequest(0,10,sort);
        Specifications  sp = Specifications.where(new SpecificationUtil("borrowman","equal",userDetails.getRealname()));
        Page<Tb_borrowdoc> borrowdocs = borrowDocRepository.findAll(sp,pageRequest);
        return borrowdocs.getContent();
    }

    public int getBorrowFinishMsgCount(){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageRequest pageRequest=null;
        Page<Tb_borrowdoc> borrowdocs = borrowDocRepository.getByStateAndClearstate(userDetails.getUserid(),pageRequest);
        List<Tb_borrowdoc> borrowdocList = borrowdocs.getContent();
        return borrowdocList.size();
    }

    public void setBorrowFinish(String borrowdocid){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Tb_borrowdoc borrowdoc = borrowDocRepository.findByDocid(borrowdocid);
        borrowdoc.setClearstate("0");
        webSocketService.noticeRefresh(userDetails.getUserid());
    }

    public List<Tb_questionnaire> getQuestionnaire() {
        return questionnaireService.getPublishQuestionnaire("1");
    }
}
