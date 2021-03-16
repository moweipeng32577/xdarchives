package com.wisdom.web.service;

import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.AcceptDocBatchRepository;
import com.wisdom.web.repository.AcceptDocRepository;
import com.wisdom.web.repository.UserRepository;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 库房管理-接收管理服务层
 * Created by Administrator on 2019/6/17.
 */
@Service
@Transactional
public class AcceptService {

    @Autowired
    AcceptDocRepository acceptDocRepository;

    @Autowired
    AcceptDocBatchRepository acceptDocBatchRepository;

    @Autowired
    UserRepository userRepository;
    public Page<Tb_acceptdoc> getAcceptDocByState(int page, int limit, String condition, String operator, String content, String sort){
        Specifications sp = null;
        Sort sortobj = WebSort.getSortByJson(sort);
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }

        PageRequest pageRequest = new PageRequest(page-1, limit, sort == null ?
                new Sort(Sort.Direction.DESC, "accepdate") : sortobj);
        return acceptDocRepository.findAll(sp,pageRequest);
    }

    public String delDoc(String[] acceptdocid){
        String msg = "";
        acceptDocBatchRepository.deleteByAcceptdocidIn(acceptdocid);
        Integer count = acceptDocRepository.deleteByAcceptdocidIn(acceptdocid);
        if(count>0){
            msg = "删除成功";
        }
        return msg;
    }

    public Page<Tb_acceptdoc_batch> getBatchByAcceptdocid(String acceptdocid,String state,int page, int limit, String condition, String operator, String content){
        Specifications sp = null;
        if(state != null && !"".equals(state)){
            sp = Specifications.where(new SpecificationUtil("state","equal",state));
        }
        if(acceptdocid !=null && !"".equals(acceptdocid)){
            sp = ClassifySearchService.addSearchbarCondition(sp, "acceptdocid", "equal", acceptdocid);
        }
        if (content != null&&!"".equals(content)) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        return acceptDocBatchRepository.findAll(sp, new PageRequest(page - 1, limit));
    }

    public ExtMsg sterilizeBatch(String[] batchid,String acceptdocid){
        Integer count = acceptDocBatchRepository.updateBatchByBatchidIn(batchid);
        if(count>0){
            List<Tb_acceptdoc_batch> batches = acceptDocBatchRepository.findByBatchidIn(batchid);
            int allCount = 0;
            for(Tb_acceptdoc_batch batch : batches){
                String scope = batch.getArchivescope();
                String[] scopestr = scope.split("-");
                allCount = allCount + (Integer.parseInt(scopestr[1]) - Integer.parseInt(scopestr[0]) +1);
            }
            Tb_acceptdoc doc = acceptDocRepository.findByAcceptdocid(acceptdocid);
            String sterilizing = doc.getSterilizing();
            String sterilizings = sterilizing == null ? allCount+"" : Integer.parseInt(sterilizing)+allCount+"";
            acceptDocRepository.updateSterilizingByAcceptdocid(acceptdocid,sterilizings);
            return new ExtMsg(true,"操作成功",null);
        }
        return new ExtMsg(true,"操作失败",null);
    }

    public ExtMsg saveBatch(Tb_acceptdoc_batch tb_acceptdoc_batch){
        Tb_acceptdoc doc = acceptDocRepository.findByAcceptdocid(tb_acceptdoc_batch.getAcceptdocid());
        String scope = tb_acceptdoc_batch.getArchivescope().split("-")[1];
        if(Integer.parseInt(scope) > doc.getArchivenum()){
            return new ExtMsg(true,"失败",null);
        }
        acceptDocBatchRepository.save(tb_acceptdoc_batch);
        return new ExtMsg(true,"",null);
    }

    public ExtMsg finishsterilizeBatch(String[] batchid){
        Integer count = acceptDocBatchRepository.updateStateByBatchidIn(batchid,"已消毒");
        if(count>0){
            List<Tb_acceptdoc_batch> list = acceptDocBatchRepository.findByBatchidIn(batchid);
            int allCount = 0;
            for(Tb_acceptdoc_batch batch : list){
                String scope = batch.getArchivescope();
                String[] scopestr = scope.split("-");
                allCount = allCount + (Integer.parseInt(scopestr[1]) - Integer.parseInt(scopestr[0]) +1);
            }
            Tb_acceptdoc doc = acceptDocRepository.findByAcceptdocid(list.get(0).getAcceptdocid());
            String sterilized = doc.getSterilized();
            String sterilizeds = sterilized == null ? allCount+"" : Integer.parseInt(sterilized)+allCount+"";
            String sterilizing = Integer.parseInt(doc.getSterilizing())-allCount+"";
            acceptDocRepository.updateSterilizedByAcceptdocid(list.get(0).getAcceptdocid(),sterilizeds,sterilizing);////更新单据已消毒、正在消毒批次数量

            return new ExtMsg(true,"操作成功",null);
        }
        return new ExtMsg(false,"操作失败",null);
    }

    public ExtMsg putStorageBatch(String[] batchid){
        Integer count = acceptDocBatchRepository.updateStateByBatchidIn(batchid,"已入库");
        if(count>0){//更新单据正在消毒、已入库批次数量
            List<Tb_acceptdoc_batch> list = acceptDocBatchRepository.findByBatchidIn(batchid);
            int allCount = 0;
            for(Tb_acceptdoc_batch batch : list){
                String scope = batch.getArchivescope();
                String[] scopestr = scope.split("-");
                allCount = allCount + (Integer.parseInt(scopestr[1]) - Integer.parseInt(scopestr[0]) +1);
            }
            Tb_acceptdoc doc = acceptDocRepository.findByAcceptdocid(list.get(0).getAcceptdocid());
            String finishstore = doc.getFinishstore();
            String finishstores = finishstore == null ? allCount+"" : Integer.parseInt(finishstore)+allCount+"";
            String sterilized = Integer.parseInt(doc.getSterilized())-allCount+"";
            acceptDocRepository.updateFinishstoreByAcceptdocid(list.get(0).getAcceptdocid(),sterilized,finishstores);
            return new ExtMsg(true,"操作成功",null);
        }
        return new ExtMsg(false,"操作失败",null);
    }

    public String getOrganId(String userid) {
        return userRepository.getOrganidByUserid(userid);
    }

    public Tb_acceptdoc getAcceptDoc(String acceptdocid) {
        return acceptDocRepository.findByAcceptdocid(acceptdocid);
    }
}
