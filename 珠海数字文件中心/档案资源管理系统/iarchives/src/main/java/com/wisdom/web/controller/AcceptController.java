package com.wisdom.web.controller;

import com.wisdom.util.DateUtil;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_acceptdoc;
import com.wisdom.web.entity.Tb_acceptdoc_batch;
import com.wisdom.web.repository.AcceptDocBatchRepository;
import com.wisdom.web.repository.AcceptDocRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.AcceptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 库房管理-接收管理控制器
 * Created by Administrator on 2019/6/13.
 */
@Controller
@RequestMapping(value = "/accept")
public class AcceptController {

    @Autowired
    AcceptDocRepository acceptDocRepository;

    @Autowired
    AcceptService acceptService;

    @Autowired
    AcceptDocBatchRepository acceptDocBatchRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${system.report.server}")
    private String reportServer;//报表服务

    @RequestMapping("/main")
    public String accept(Model model){
        model.addAttribute("reportServer",reportServer);
        return "/inlet/storeroom/accept";
    }

    /**
     * 保存新增单据
     * @return
     */
    @RequestMapping("/saveDoc")
    @ResponseBody
    public ExtMsg saveDoc(Tb_acceptdoc tb_acceptdoc){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        tb_acceptdoc.setAccepter(userDetails.getRealname());
        String organId = acceptService.getOrganId(userDetails.getUserid());
        tb_acceptdoc.setOrganid(organId);
        tb_acceptdoc.setOrgan(userDetails.getOrgan().getOrganid());
        tb_acceptdoc.setAccepdate(DateUtil.getCurrentTime());
        Tb_acceptdoc saveResult = acceptDocRepository.save(tb_acceptdoc);
        if(saveResult != null){
            return new ExtMsg(true,"保存成功",null);
        }
        return new ExtMsg(false,"保存失败",null);
    };

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg updateAcceptDoc(Tb_acceptdoc acceptdoc){
        Tb_acceptdoc accept = acceptService.getAcceptDoc(acceptdoc.getAcceptdocid());
        accept.setSubmitter(acceptdoc.getSubmitter());
        accept.setSubmitdate(acceptdoc.getSubmitdate());
        accept.setDocremark(acceptdoc.getDocremark());
        accept.setSubmitorgan(acceptdoc.getSubmitorgan());
        accept.setArchivenum(acceptdoc.getArchivenum());
        Tb_acceptdoc updateResult = acceptDocRepository.save(accept);
        if(updateResult != null){
            return new ExtMsg(true,"修改成功",null);
        }
        return new ExtMsg(false,"修改失败",null);
    }

    @RequestMapping("/getAcceptDocByState")
    @ResponseBody
    public Page<Tb_acceptdoc> getAcceptDocByState(int page, int limit, String condition, String operator, String content,String sort){
        return acceptService.getAcceptDocByState(page,limit,condition,operator,content,sort);
    }

    /**
     * 接收单据数据删除
     * @param acceptdocid
     * @return
     */
    @RequestMapping("/delDoc")
    @ResponseBody
    public ExtMsg delDoc(String[] acceptdocid) {
        try {
            String msg = acceptService.delDoc(acceptdocid);
            return new ExtMsg(true, msg, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ExtMsg(false, "操作失败", null);
    }

    /**
     * 获取批次号
     * @return
     */
    @RequestMapping("/getBatchAddForm")
    @ResponseBody
    public ExtMsg getBatchAddForm(String acceptdocid){
        if(acceptdocid!=null && !"".equals(acceptdocid)){
            String code = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());//获取时间
            String random = (int)(Math.random()*(9999-1000+1))+1000+"";//获取4位随机数
            Tb_acceptdoc_batch tb_acceptdoc_batch = new Tb_acceptdoc_batch();
            tb_acceptdoc_batch.setBatchcode(code+random);
            tb_acceptdoc_batch.setAcceptdocid(acceptdocid);
            List<Tb_acceptdoc_batch> acceptdocs = acceptDocBatchRepository.findByAcceptdocid(acceptdocid);
            if(acceptdocs.size()>0){
                Tb_acceptdoc tbAcceptdoc = acceptDocRepository.findByAcceptdocid(acceptdocid);
                int[] scopes = new int[acceptdocs.size()];
                for(int i=0;i<acceptdocs.size();i++){
                    String scopeStr = acceptdocs.get(i).getArchivescope();
                    scopeStr = scopeStr.split("-")[1];
                    scopes[i] = Integer.parseInt(scopeStr);
                }
                int n = scopes.length;
                for(int i=0;i<n;i++){
                    boolean flag = false;
                    for(int j=0;j<n-i-1;j++){
                        int temp = scopes[j];
                        if(scopes[j]>scopes[j+1]){
                            scopes[j] = scopes[j+1];
                            scopes[j+1] = temp;
                            flag = true;
                        }
                    }
                    if(!flag){   //没有数据交换，数组已经有序，退出排序
                        break;
                    }
                }
                int count = scopes[n-1]+1;
                if(count >= tbAcceptdoc.getArchivenum()){
                    return new ExtMsg(true,"失败",tb_acceptdoc_batch);
                }else{
                    return new ExtMsg(true,""+count,tb_acceptdoc_batch);
                }
            }
            return new ExtMsg(true,""+1,tb_acceptdoc_batch);
        }
        return new ExtMsg(false,"失败",null);
    }

    /**
     * 保存新增批次
     * @return
     */
    @RequestMapping("/saveBatch")
    @ResponseBody
    public ExtMsg saveBatch(Tb_acceptdoc_batch tb_acceptdoc_batch){
        return acceptService.saveBatch(tb_acceptdoc_batch);
    };

    /**
     * 获取接收单据批次信息
     * @param acceptdocid
     * @return
     */
    @RequestMapping("/getBatchBySearch")
    @ResponseBody
    public Page<Tb_acceptdoc_batch> getBatchByAcceptdocid(String acceptdocid,String state,int page, int limit, String condition, String operator, String content){
        return acceptService.getBatchByAcceptdocid(acceptdocid, state, page,  limit,  condition,  operator,  content);
    }

    /**
     * 消毒批次
     * @return
     */
    @RequestMapping("/sterilizeBatch")
    @ResponseBody
    public ExtMsg sterilizeBatch(String[] batchid,String acceptdocid){
        return acceptService.sterilizeBatch(batchid,acceptdocid);
    }

    /**
     * 完成正在消毒批次
     * @return
     */
    @RequestMapping("/finishsterilizeBatch")
    @ResponseBody
    public ExtMsg finishsterilizeBatch(String[] batchid){
        return acceptService.finishsterilizeBatch(batchid);
    }

    /**
     * 入库已消毒批次
     * @return
     */
    @RequestMapping("/putStorageBatch")
    @ResponseBody
    public ExtMsg putStorageBatch(String[] batchid){
        return acceptService.putStorageBatch(batchid);
    }

}
