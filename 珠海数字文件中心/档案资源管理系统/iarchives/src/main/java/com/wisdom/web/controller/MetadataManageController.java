package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.util.FunctionUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.DataNodeExtRepository;
import com.wisdom.web.repository.UserRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.CaptureMetadataService;
import com.wisdom.web.service.MetadataManageService;
import com.wisdom.web.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by SunK on 2020/5/21 0021.
 */
//元数据管理控制器
@Controller
@RequestMapping(value = "/metadataManagement")
public class MetadataManageController {

    @Autowired
    DataNodeExtRepository dataNodeExtRepository;

    @Autowired
    MetadataManageService metadataManageService;

    @Autowired
    TemplateService templateService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CaptureMetadataService captureMetadataService;

    @RequestMapping(value = "/main")
    public String metadataManage(Model model, String isp) {
        Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
        model.addAttribute("functionButton", functionButton);
        return "/inlet/metadataManagement";
    }

//    @RequestMapping(value = "")
//    @ResponseBody
//    public void findByType(String type){
//
//    }

    @RequestMapping(value = "/getMetadataTree")
    @ResponseBody
    public List<ExtNcTree> getMetadataTree() {
        return metadataManageService.getMetadataTree();
    }

    @RequestMapping(value = "/entries")
    @ResponseBody
    private Page<Tb_index_detail> getEntrys(String nodeid, String type, String basicCondition, String basicOperator, String basicContent,
                                            String condition, String operator, String content, String info, Tb_index_detail formConditions,
                                            ExtOperators formOperators, ExtDateRangeData daterangedata, String logic, boolean ifSearchLeafNode,
                                            boolean ifContainSelfNode, int page, int limit, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        return metadataManageService.getEntrys(nodeid, condition, operator, content, formConditions, formOperators,
                daterangedata, logic, ifSearchLeafNode, ifContainSelfNode, page, limit, sortobj);
    }


    @RequestMapping(value = "/entries/innerfile/{entryid}/")
    @ResponseBody
    public Page<Tb_service_metadata> getEntryInnerFile(@PathVariable String entryid, String nodeid, Integer page,
                                                       Integer start, Integer limit, String sort) {

        Sort sortobj = WebSort.getSortByJson(sort);
        PageRequest pageRequest = new PageRequest(page - 1, limit,
                sortobj == null ? new Sort(Sort.Direction.DESC, "servicetime") : sortobj);
        Page<Tb_service_metadata> metadataPage = metadataManageService.getMetadataByEntryid(entryid, pageRequest);
        metadataPage.getContent().forEach(tb_service_metadata ->{
            Tb_user user = userRepository.findByUserid(tb_service_metadata.getUserid());
            if(null!=user){
                tb_service_metadata.setLoginname(user.getLoginname());
                tb_service_metadata.setRealname(user.getRealname());
                tb_service_metadata.setDuty(user.getDuty());
                tb_service_metadata.setOrganusertype(user.getOrganusertype());
            }
        });
        return metadataPage;
    }


    @RequestMapping(value = "/getServiceMetadataTemp")
    @ResponseBody
    public List<Tb_data_template> getServiceMetadataTemp(String nodeid) {

        return metadataManageService.serviceMetadataTemp(nodeid);
    }


    @RequestMapping(value = "/getMetadataTemp")
    @ResponseBody
    public List<Tb_data_template> getMetadataTemp(String nodeid, String type, String info, String eventid, String entryid) {
        List<Tb_data_template> list = new ArrayList<>();
        list = templateService.gridTemplate(nodeid, type, info);
        for (Tb_data_template template : list) {
            String metadatafieldname = template.getMetadatafieldname();
            if (!"".equals(metadatafieldname) && null != metadatafieldname) {
                template.setFieldname(metadatafieldname);
            }
        }
        return list;
    }


    //追溯元数据--删除
    @RequestMapping(value = "/deleteServiceMetadata")
    @ResponseBody
    public ExtMsg deleteServiceMetadata(String[] ids) {
        if (null != ids && ids.length > 0) {
            int del_count = metadataManageService.DelMeatadata(ids);
            return new ExtMsg(true, "删除成功", del_count);
        } else {
            return new ExtMsg(false, "删除失败", null);
        }
    }

    //追溯元数据--修改
    @RequestMapping(value = "/modifyServiceMetadata")
    @ResponseBody
    public ExtMsg modifyServiceMetadata(String entryids, String isSelectAll, String nodeid, String condition, String operator,
                                        String content, Tb_entry_index formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata,
                                        String logic, boolean ifSearchLeafNode, boolean ifContainSelfNode) {
        String[] entryInfo = entryids.split(",");
        if (null != entryInfo && entryInfo.length > 0) {
            int del_count = metadataManageService.DelMeatadata(entryInfo);
            return new ExtMsg(true, "删除成功", del_count);
        } else {
            return new ExtMsg(false, "删除失败", null);
        }
    }

    //追溯元数据--增加
    @RequestMapping(value = "/addMetadata")
    @ResponseBody
    public ExtMsg addMetadata(String entryid, Tb_service_metadata service_metadata) {
        int add_count = metadataManageService.addMetadata(entryid, service_metadata);
        if (add_count == 1) {
            return new ExtMsg(true, "成功增加一条数据", add_count);
        }
        return new ExtMsg(false, "新增失败！", null);
    }

    @RequestMapping(value = "/getServiceMetadataByid")
    @ResponseBody
    public ExtMsg getServiceMetadataByid(String sid) {
        Tb_service_metadata service_metadata = metadataManageService.getServiceMetadataById(sid);
        if (null != service_metadata) {
            Tb_user user = userRepository.findByUserid(service_metadata.getUserid());
            service_metadata.setDuty(user.getDuty());
            service_metadata.setOrganusertype(user.getOrganusertype());
            service_metadata.setRealname(user.getRealname());
            service_metadata.setLoginname(user.getLoginname());
            return new ExtMsg(true, "请求成功", service_metadata);
        }
        return new ExtMsg(false, "请求失败！", null);
    }

    @RequestMapping(value = "/getAddServiceMetadata")
    @ResponseBody
    public ExtMsg getAddServiceMetadata(String operation) {
        Tb_service_config service_config = captureMetadataService.findByOperationAndParentidIsNull(operation);
        Tb_service_metadata service_metadata = new Tb_service_metadata();
        if (null != service_config) {
            SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Tb_user user = userRepository.findByUserid(userDetails.getUserid());
            service_metadata.setOperation(operation);
            service_metadata.setMstatus(service_config.getMstatus());
            service_metadata.setOperationmsg(service_config.getOperationmsg());
            service_metadata.setAid(service_config.getAid());
            service_metadata.setUserid(userDetails.getUserid());
            service_metadata.setDuty(user.getDuty());
            service_metadata.setOrganusertype(user.getOrganusertype());
            service_metadata.setRealname(user.getRealname());
            service_metadata.setLoginname(user.getLoginname());
            service_metadata.setServicetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            return new ExtMsg(true, "请求成功", service_metadata);
        }
        return new ExtMsg(false, "请求失败！", null);
    }

    @RequestMapping(value = "/getServiceMetadataByEntryid")
    @ResponseBody
    public Page<Tb_service_metadata> getServiceMetadataByEntryid(String entryid,String sort,int page, int limit){
        Sort sortobj = WebSort.getSortByJson(sort);
        PageRequest pageRequest = new PageRequest(page - 1, limit,
                sortobj == null ? new Sort(Sort.Direction.DESC, "servicetime") : sortobj);
       return metadataManageService.getMetadataByEntryid(entryid, pageRequest);
    }
}
