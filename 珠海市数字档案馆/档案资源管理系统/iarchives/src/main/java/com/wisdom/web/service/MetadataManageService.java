package com.wisdom.web.service;

import com.wisdom.util.DBCompatible;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.ServiceMetadataRepositort;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SunK on 2020/5/22 0022.
 */
@Service
@Transactional
public class MetadataManageService {


    @Autowired
    ClassifySearchService classifySearchService;
    @Autowired
    EntryIndexService entryIndexService;
    @Autowired
    MetadataSearchService metadataSearchService;
    @Autowired
    ServiceMetadataRepositort serviceMetadataRepositort;
    @Autowired
    NodesettingService nodesettingService;

    public List<ExtNcTree> getMetadataTree() {
        List<ExtNcTree> extNcTrees = new ArrayList<>();
        ExtNcTree e1 = new ExtNcTree();
        e1.setText("实体元数据");
        e1.setFnid("2");
        e1.setLeaf(true);
        e1.setExpanded(false);
        e1.setCls("file");
        ExtNcTree e2 = new ExtNcTree();
        e2.setText("业务元数据");
        e2.setFnid("2");
        e2.setLeaf(true);
        e2.setExpanded(false);
        e2.setCls("file");
        ExtNcTree e3 = new ExtNcTree();
        e3.setText("机构元数据");
        e3.setFnid("3");
        e3.setLeaf(true);
        e3.setExpanded(false);
        e3.setCls("file");
        ExtNcTree e4 = new ExtNcTree();
        e4.setText("授权元数据");
        e4.setFnid("4");
        e4.setLeaf(true);
        e4.setExpanded(false);
        e4.setCls("file");
        extNcTrees.add(e1);
        extNcTrees.add(e2);
        extNcTrees.add(e3);
        extNcTrees.add(e4);
        return extNcTrees;
    }


    public Page<Tb_index_detail> getEntrys(String nodeid, String condition, String operator, String content,
                                                   Tb_index_detail formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,
                                                   boolean ifSearchLeafNode, boolean ifContainSelfNode, int page, int limit, Sort sort) {

        PageRequest pageRequest = new PageRequest(page - 1, limit);
        String searchCondition = "";
        String nodeidStr = " and sid.nodeid='" + nodeid + "' ";
        if (ifSearchLeafNode) {//点击非叶子节点时，查询出其包含的所有叶子节点数据
            List<String> nodeidList = nodesettingService.getNodeidLoop(nodeid, ifContainSelfNode, new ArrayList<String>());
            if (nodeidList.size() > 0) {
                nodeidStr = " and sid.nodeid in('" + String.join("','", nodeidList) + "') ";
            }
        }
        if (content != null && !"".equals(content)) {//输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        String dataStr = "";
        if (daterangedata.getFiledateendday() != null && daterangedata.getFiledatestartday() != null) {//检索条件为日期范围
            dataStr = classifySearchService.getDateRangeCondition(daterangedata);
        }
        String sortStr = "";//排序
        int sortInt = 0;//判断是否副表表排序
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            if ("eleid".equals(order.getProperty())) {
                sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
            } else {
                sortStr = " order by " + order.getProperty() + " " + order.getDirection();
            }
            sortInt = entryIndexService.checkFilecode(order.getProperty());
        } else {
            sortStr = " order by archivecode desc, descriptiondate desc ";
        }

        String table = "v_index_detail";
        String countTable = "v_index_detail";
        if (condition == null || entryIndexService.checkFilecode(condition) == 0) {//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
            countTable = "tb_entry_index";
            if (sortInt == 0) {//非副表表字段排序
                table = "tb_entry_index";
            }
        }
        String sql = "select sid.entryid from " + table + " sid where 1=1 " + searchCondition + dataStr+nodeidStr;
        String countSql = "select count(nodeid) from " + countTable + " sid where 1=1 " + searchCondition + dataStr+nodeidStr;
        Page<Tb_index_detail> result = metadataSearchService.getPageListTwo(sql, sortStr, countSql, page, limit, pageRequest);
        return result;
    }



    public Page<Tb_service_metadata> getMetadataByEntryid(String entryid,PageRequest page){
        Page<Tb_service_metadata> metadataPage = serviceMetadataRepositort.findByEntryids(entryid,page);
        return metadataPage;
    }

    public List<Tb_data_template> serviceMetadataTemp(String nodeid){
        List<Tb_data_template> tb_data_templates = new ArrayList<>();
        Map<Integer,String> m = new HashMap<>();
        m.put(0,"operation");
        m.put(1,"mstatus");
        m.put(2,"servicetime");
        m.put(3,"operationmsg");
        m.put(4,"loginname");
        m.put(5,"realname");
        m.put(6,"duty");
        m.put(7,"organusertype");
        List<String> list = new ArrayList<>();
        list.add("业务行为");
        list.add("状态");
        list.add("时间");
        list.add("描述");
        list.add("操作人账号");
        list.add("操作人姓名");
        list.add("操作人职务");
        list.add("操作人机构人员类型");
        for(int i = 0;i<m.size();i++){
            Tb_data_template template = new Tb_data_template();
            template.setFdefault("");
            template.setFfield(false);
            template.setFieldcode(m.get(i));
            template.setFieldname(list.get(i));
            template.setFieldtable("tb_service_metadata");
            template.setFreadonly(false);
            template.setFrequired(false);
            template.setFsequence(new Long((long) 0));
            template.setFtip("");
            template.setFtype("String");
            template.setFvalidate("");
            template.setGfield(true);
            template.setGhidden(false);
            template.setGsequence(new Long((long) 0));
            template.setGwidth(new Long((long) 260));
            template.setInactiveformfield(false);
            template.setNodeid(nodeid);
            template.setQfield(false);
            template.setQsequence(new Long((long) 0));
            template.setTemplateid("");
            tb_data_templates.add(template);
        }
        return  tb_data_templates;
    }



    public Integer DelMeatadata(String[] ids){
        Integer l = serviceMetadataRepositort.deleteBySidIn(ids);
        return l;
    }

    public Integer addMetadata(String entryid,Tb_service_metadata service_metadata){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        if(null!=service_metadata){
            if(null!=entryid) {
                service_metadata.setEntryids(entryid);
            }
            if(null==service_metadata.getUserid()||"".equals(service_metadata.getUserid())) {
                service_metadata.setUserid(userDetails.getUserid());
            }
            serviceMetadataRepositort.save(service_metadata);
            return 1;
        }else {
            return 0;
        }
    }

    public Tb_service_metadata getServiceMetadataById(String sid){
        Tb_service_metadata service_metadata = serviceMetadataRepositort.findAllBySid(sid);
        return service_metadata;
    }

    public Tb_service_metadata addServiceMetadata(Tb_service_metadata service_metadata){
        if(null!=service_metadata){
//            if(null!=service_metadata.getSid()&&!"".equals(service_metadata.getSid())){//修改
//
//            }
            serviceMetadataRepositort.save(service_metadata);
            return service_metadata;
        }
        return null;
    }
}
