package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wisdom.secondaryDataSource.entity.Tb_entry_detail_sx;
import com.wisdom.secondaryDataSource.entity.Tb_entry_index_sx;
import com.wisdom.secondaryDataSource.repository.SecondaryEntryDetailRepository;
import com.wisdom.secondaryDataSource.repository.SecondaryEntryIndexRepository;
import com.wisdom.util.ExportUtil;
import com.wisdom.util.GainField;
import com.wisdom.util.LogAop;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.ClassifySearchService;
import com.wisdom.web.service.EntryIndexService;
import com.wisdom.web.service.EntryService;
import com.wisdom.web.service.NodesettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类检索控制器
 * Created by RonJiang on 2017/10/26 0026.
 */
@Controller
@RequestMapping(value = "/classifySearch")
public class ClassifySearchController {

    @Autowired
    LogAop logAop;

    @Autowired
    ClassifySearchService classifySearchService;

    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    EntryService entryService;

    @Autowired
    EntryIndexService entryIndexService;
    
    @Autowired
    EntrySearchTempRepository entrySearchTempRepository;

    @Autowired
    EntryIndexManageRepository entryIndexManageRepository;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    EntryDetailRepository entryDetailRepository;

    @Autowired
    EntryDetailManageRepository entryDetailManageRepository;

    @Autowired
    SecondaryEntryIndexRepository secondaryEntryIndexRepository;

    @Autowired
    SecondaryEntryDetailRepository secondaryEntryDetailRepository;

    @Autowired
    SimpleSearchController simpleSearchController;
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${system.report.server}")
    private String reportServer;//报表服务

    @RequestMapping("/main")
    public String index(Model model, String flag) {
        model.addAttribute("buttonflag", flag);
        model.addAttribute("reportServer",reportServer);
        return "/inlet/classifySearch";
    }

    //解决利用平台与管理平台公用页面权限控制问题
    @RequestMapping("/mainly")
    public String indexly(Model model, String flag) {
        model.addAttribute("buttonflag", flag);
        model.addAttribute("reportServer",reportServer);
        return "/inlet/classifySearch";
    }

    /**
     * 高级检索
     */
    @RequestMapping("/findBySearch")
    @ResponseBody
    public Page<Tb_index_detail> findBySearch(int page, int start, int limit, String condition, String operator, String content, Tb_index_detail formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic, String nodeid, boolean ifSearchLeafNode, boolean ifContainSelfNode, String sort,String datasoure) {
        Sort sortobj = WebSort.getSortByJson(sort);
        Page<Tb_index_detail> result = entryIndexService.getEntrybase(nodeid, condition, operator, content, formConditions, formOperators, daterangedata, logic, ifSearchLeafNode, ifContainSelfNode, page, limit, sortobj,datasoure);
        result = convertNodefullnames(result, new PageRequest(page - 1, limit));
        return result;
    }

    /**
     * 分类检索，不使用框架自带的json转换，避免循环引用
     */
    @RequestMapping("/findByClassifySearch")
    public void findByClassifySearch(int page, int start, int limit, String condition, String operator, String content, Tb_index_detail formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic, String nodeids, boolean ifSearchLeafNode, boolean ifContainSelfNode, HttpServletResponse httpServletResponse, String sort,String datasoure) {
        Sort sortobj = WebSort.getSortByJson(sort);
        //Page<Tb_entry_index> result =  classifySearchService.findByClassifySearch(page, limit, condition, operator, content, nodeids,formConditions,formOperators,daterangedata,logic,ifSearchLeafNode,ifContainSelfNode,sortobj);
        //Page<Entry> result = classifySearchService.getEntrys(nodeids,condition,operator,content,formConditions,formOperators,daterangedata,logic,ifSearchLeafNode,ifContainSelfNode,page,limit,sort);
        Sort sortobj1 = WebSort.getSortByJson(sort);
        Page<Tb_index_detail> result = entryIndexService.getEntrybase(nodeids, condition, operator, content, formConditions, formOperators, daterangedata, logic, ifSearchLeafNode, ifContainSelfNode, page, limit, sortobj1,datasoure);
        result = convertNodefullnames(result, new PageRequest(page - 1, limit));
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("utf-8");
        String json = JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
        try {
            httpServletResponse.getWriter().write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分类检索，不使用框架自带的json转换，避免循环引用(利用平台)
     */
    @RequestMapping("/findByClassifySearchPlatform")
    public void findByClassifySearchPlatform(int page, int start, int limit, String condition, String operator, String content, Tb_entry_index formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic, String nodeids, boolean ifSearchLeafNode, boolean ifContainSelfNode, HttpServletResponse httpServletResponse, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        logger.info("page:" + page + ";start:" + start + ";limt:" + limit + ";nodeids:" + nodeids);
        Page<Tb_entry_index> result = classifySearchService.findByClassifySearchPlatform(page, limit, condition, operator, content, nodeids, formConditions, formOperators, daterangedata, "原文开放,条目开放", logic, ifSearchLeafNode, ifContainSelfNode, sortobj);
        result = convertNodefullname(result, new PageRequest(page - 1, limit));
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("utf-8");
        String json = JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
        try {
            httpServletResponse.getWriter().write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @RequestMapping("/getAllEntry")
    @ResponseBody
    public Page<Tb_entry_index> getAllEntry(int page, int start, int limit) {
        logger.info("page:" + page + ";start:" + start + ";limt:" + limit);
        Page<Tb_entry_index> list = classifySearchService.getAllEntry(page, limit);
        logger.info(list.toString());
        return list;
    }

    //    @LogAnnotation(module = "分类检索",startDesc = "数据导出操作，导出文件名为：",sites = "1,2",connect = "，导出条目id为：")
    @RequestMapping("/exportData")
    @ResponseBody
    public void exportData(String fileName, String[] entryids, String[] names,String[] keys,HttpServletResponse response,String type) {
        String startTime = LogAop.getCurrentSystemTime();//开始时间
        long startMillis = System.currentTimeMillis();//开始毫秒数
        logger.info("fileName:" + fileName + ";entryids:" + entryids);
        List<Tb_entry_index> entryIndexes = new ArrayList<Tb_entry_index>();
        List<Tb_entry_index_manage> manageIndexes = new ArrayList<>();
        List<Tb_entry_detail>entryDetails = new ArrayList<Tb_entry_detail>();
        List<Tb_entry_detail_manage> manageDetails = new ArrayList<Tb_entry_detail_manage>();
        List<Tb_entry_detail_sx> detailSxes = new ArrayList();
        List<Tb_entry_index_sx> indexSxes = new ArrayList();
        if(type!=null&&"directory".equals(type)){  //判断是否目录简单检索
            manageIndexes = entryIndexManageRepository.findByEntryidIn(entryids);
            manageDetails = entryDetailManageRepository.findByEntryidIn(entryids);
        }else if(type!=null&&"management".equals(type)){
            entryIndexes = entryIndexRepository.findByEntryidIn(entryids);
            entryDetails = entryDetailRepository.findByEntryidIn(entryids);
        }else if(type!=null&&"soundimage".equals(type)){   //声像系统
            indexSxes = secondaryEntryIndexRepository.findByEntryidIn(entryids);
            detailSxes = secondaryEntryDetailRepository.findByEntryidIn(entryids);
        }
//        for (String entryid : entryids) {
//            entryIndexes.add(classifySearchService.getEntryIndexByEntryid(entryid));
//        }
        logger.info(entryIndexes.toString());
        List<Map<String, Object>> list = simpleSearchController.createExcelRecord(entryIndexes,manageIndexes,entryDetails,manageDetails,indexSxes,detailSxes,keys,type);

        ExportUtil exportUtil = new ExportUtil(fileName, response, list, keys, names);
        exportUtil.exportExcel();
        for (String entryid : entryids) {
            logAop.generateManualLog(startTime, LogAop.getCurrentSystemTime(), System.currentTimeMillis() - startMillis, "分类检索", "数据导出操作，导出文件名为：" + fileName + ",导出条目id为：" + entryid);
        }
    }

    private List<Map<String, Object>> createExcelRecord(List<Tb_entry_index> entryIndexes,List<Tb_entry_index_manage> manageIndexes,
                                        List<Tb_entry_detail>entryDetails, List<Tb_entry_detail_manage> manageDetails, String keys[], String type) {
        List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
        if(type!=null&&"directory".equals(type)){
            Tb_entry_index_manage entryIndex;
            Tb_entry_detail_manage entryDetail;
            for (int i = 0; i < manageIndexes.size(); i++) {
                Map<String,Object> entryMap = new HashMap<String,Object>();
                // entryIndex实体对象转换成map对象
                entryIndex = manageIndexes.get(i);
                Field[] entryFields = entryIndex.getClass().getDeclaredFields();
                for(Field entryfield  :entryFields){
                    entryfield.setAccessible(true); // //在反射时能访问私有变量
                    try {
                        entryMap.put(entryfield.getName(), entryfield.get(entryIndex));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                // entryDetail实体对象转换成map对象
                entryDetail = manageDetails.get(i);
                Field[] entryCapturefields = entryDetail.getClass().getDeclaredFields();
                for(Field entryCapturefield  :entryCapturefields){
                    entryCapturefield.setAccessible(true); // //在反射时能访问私有变量
                    try {
                        entryMap.put(entryCapturefield.getName(), entryCapturefield.get(entryDetail));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                Map<String, Object> mapValue = new HashMap<String, Object>();
                for(int j=0;j<keys.length;j++){
                    if("nodefullname".equals(keys[j])){
                        String nodefullname = nodesettingService.getNodefullnameLoop(entryIndex.getNodeid(),"_",""); //数据节点
                        mapValue.put("nodefullname",nodefullname);
                    }
                    else {
                        mapValue.put(keys[j],entryMap.get(keys[j]));
                    }
                }
                listmap.add(mapValue);
            }
        }else if(type!=null&&"management".equals(type)){
            Tb_entry_index entryIndex;
            Tb_entry_detail entryDetail;
            for (int i = 0; i < entryIndexes.size(); i++) {
                Map<String,Object> entryMap = new HashMap<String,Object>();
                // entryIndex实体对象转换成map对象
                entryIndex = entryIndexes.get(i);
                Field[] entryFields = entryIndex.getClass().getDeclaredFields();
                for(Field entryfield  :entryFields){
                    entryfield.setAccessible(true); // //在反射时能访问私有变量
                    try {
                        entryMap.put(entryfield.getName(), entryfield.get(entryIndex));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                // entryDetail实体对象转换成map对象
                entryDetail = entryDetails.get(i);
                Field[] entryCapturefields = entryDetail.getClass().getDeclaredFields();
                for(Field entryCapturefield  :entryCapturefields){
                    entryCapturefield.setAccessible(true); // //在反射时能访问私有变量
                    try {
                        entryMap.put(entryCapturefield.getName(), entryCapturefield.get(entryDetail));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                Map<String, Object> mapValue = new HashMap<String, Object>();
                for(int j=0;j<keys.length;j++){
                    if("nodefullname".equals(keys[j])){
                        String nodefullname = nodesettingService.getNodefullnameLoop(entryIndex.getNodeid(),"_",""); //数据节点
                        mapValue.put("nodefullname",nodefullname);
                    }
                    else {
                        mapValue.put(keys[j],entryMap.get(keys[j]));
                    }
                }
                listmap.add(mapValue);
            }
        }
        return listmap;
    }
    
    /**
     * 存储分类检索&高级检索的每个节点的上一次检索信息
     */
    @RequestMapping("/setLastSearchInfo")
    @ResponseBody
    public void setLastSearchInfo(String nodeid, String fieldColumn, String fieldValue, String type) {
    	SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	String userid = userDetails.getUserid();
    	Tb_entry_search_temp entry_search_temp = entrySearchTempRepository.findByNodeidAndTypeAndUserid(nodeid, type, userid);
    	if (entry_search_temp != null) {
    		entrySearchTempRepository.deleteByNodeidAndTypeAndUserid(nodeid, type, userid);
    	}
    	if (fieldColumn != null && fieldValue != null) {
    		String[] field = fieldColumn.split(",");
        	String[] value = fieldValue.split(",");
        	Tb_entry_search_temp search_temp = new Tb_entry_search_temp();
        	// 存储检索的用户信息
        	GainField.setFieldValueByName("userid", search_temp, userDetails.getUserid());
        	// 存储检索的节点信息
        	GainField.setFieldValueByName("nodeid", search_temp, nodeid);
        	// 存储检索的类型
        	GainField.setFieldValueByName("type", search_temp, type);
        	for (int i = 0; i < field.length; i++) {
                if(!field[i].contains("OperatorCombo")){
                    GainField.setFieldValueByName(field[i], search_temp, value[i]);
                }
        	}
        	entrySearchTempRepository.save(search_temp);
    	}
    }
    
    @RequestMapping("/getLastSearchInfo")
    @ResponseBody
    public ExtMsg getLastSearchInfo(String nodeid, String type, String field, String inactiveField) {
    	SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Tb_entry_search_temp entry_search_temp = entrySearchTempRepository.findByNodeidAndTypeAndUserid(nodeid, type, userDetails.getUserid());
    	if (entry_search_temp != null) {
    		String returnValue = "";
    		if (field != null && !"".equals(field)) {
    			String[] fieldValue = field.split(",");
        		for (int i = 0; i < fieldValue.length; i++) {
        			String value = (String) GainField.getFieldValueByName(fieldValue[i], entry_search_temp);
    				returnValue += value + "∩";
        		}
        		returnValue = returnValue.substring(0, returnValue.length() - 1);
    		}
    		String returnInactiveValue = "";
    		if (inactiveField != null && !"".equals(inactiveField)) {
    			String[] inactiveFieldValue = inactiveField.split(",");
        		for (int i = 0; i < inactiveFieldValue.length; i++) {
        			String value = (String) GainField.getFieldValueByName(inactiveFieldValue[i], entry_search_temp);
        			returnInactiveValue += value + "∩";
        		}
        		returnInactiveValue = returnInactiveValue.substring(0, returnInactiveValue.length() - 1);
    		}
    		returnValue = returnValue + "∪" + returnInactiveValue;
    		return new ExtMsg(true, "获取历史检索数据成功！",returnValue);
    	}
    	return new ExtMsg(false, "获取历史检索数据失败！", null);
    }
    
    @RequestMapping("/clearSearchInfo")
    @ResponseBody
    public ExtMsg clearSearchInfo(String nodeid, String type) {
    	SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Integer value = entrySearchTempRepository.deleteByNodeidAndTypeAndUserid(nodeid, type, userDetails.getUserid());
    	if (value > 0) {
    		return new ExtMsg(true, "清除历史检索数据成功！",null);
    	}
    	return new ExtMsg(false, "清除历史检索数据失败！",null);
    }

    /**
     * 生成数据节点全名，转换分页结果
     *
     * @param result
     * @param pageRequest
     * @return
     */
    public Page<Tb_entry_index> convertNodefullname(Page<Tb_entry_index> result, PageRequest pageRequest) {
        List<Tb_entry_index> content = result.getContent();
        long totalElements = result.getTotalElements();
        List<Tb_entry_index> returnResult = new ArrayList<>();

        Map<String, Object[]> parentmap = nodesettingService.findAllParentOfNode();
        for (Tb_entry_index entryIndex : content) {
            Tb_entry_index entry_index = new Tb_entry_index();
            BeanUtils.copyProperties(entryIndex, entry_index);
            String nodeid = entry_index.getNodeid();
            Tb_data_node node = (Tb_data_node) parentmap.get(nodeid)[0];
            List<Tb_data_node> parents = (List<Tb_data_node>) parentmap.get(nodeid)[1];
            StringBuffer nodefullname = new StringBuffer(node.getNodename());
            for (Tb_data_node parent : parents) {
                if (parent == null) {
                    continue;
                }
                nodefullname.insert(0, "_");
                nodefullname.insert(0, parent.getNodename());
            }
            entry_index.setNodefullname(nodefullname.toString());

            returnResult.add(entry_index);
        }
        return new PageImpl(returnResult, pageRequest, totalElements);
    }

    /**
     *   根据Tb_index_detail生成数据节点全名，转换分页结果
     * @param result
     * @param pageRequest
     * @return
     */
    public Page<Tb_index_detail> convertNodefullnameAll(Page<Tb_index_detail> result,PageRequest pageRequest){
        List<Tb_index_detail> content = result.getContent();
        long totalElements = result.getTotalElements();
        List<Tb_index_detail> returnResult = new ArrayList<>();

        Map<String,Object[]> parentmap = nodesettingService.findAllParentOfNode();
        for(Tb_index_detail entryIndex:content){
            if(entryIndex!=null) {
                Tb_index_detail entry_index = new Tb_index_detail();
                BeanUtils.copyProperties(entryIndex, entry_index);
                String fullname = getFullnameByNodeid(parentmap, entry_index.getNodeid());
                entry_index.setNodefullname(fullname);
                returnResult.add(entry_index);
            }
        }
        return new PageImpl(returnResult,pageRequest,totalElements);
    }

    /**
     *   根据Tb_entry_index际生成数据节点全名，转换分页结果
     * @param result
     * @param pageRequest
     * @return
     */
    public Page<Tb_entry_index> getNodefullnameAll(Page<Tb_entry_index> result,PageRequest pageRequest){
        List<Tb_entry_index> content = result.getContent();
        long totalElements = result.getTotalElements();
        List<Tb_entry_index> returnResult = new ArrayList<>();

        Map<String,Object[]> parentmap = nodesettingService.findAllParentOfNode();
        for(Tb_entry_index entryIndex:content){
            Tb_entry_index entry_index = new Tb_entry_index();
            BeanUtils.copyProperties(entryIndex,entry_index);
            String fullname=getFullnameByNodeid(parentmap,entry_index.getNodeid());
            entry_index.setNodefullname(fullname);
            returnResult.add(entry_index);
        }
        return new PageImpl(returnResult,pageRequest,totalElements);
    }

    /**
     * 根据nodeid获取相应的节点全名
     * @param parentmap
     * @param nodeid
     * @return
     */
    public String getFullnameByNodeid(Map<String,Object[]> parentmap,String nodeid){
        if(parentmap.get(nodeid)==null){
            return "";
        }
        Tb_data_node node = (Tb_data_node)parentmap.get(nodeid)[0];
        List<Tb_data_node> parents = (List<Tb_data_node>)parentmap.get(nodeid)[1];
        if (node.getNodename() != null && !"".equals(node.getNodename())) {
            StringBuffer nodefullname = new StringBuffer(node.getNodename());
            for(Tb_data_node parent : parents){
                if(parent == null){
                    continue;
                }
                nodefullname.insert(0, "_");
                nodefullname.insert(0, parent.getNodename());
            }
            return nodefullname.toString();
        }
        return "";
    }

    public Page<Tb_index_detail> convertNodefullnames(Page<Tb_index_detail> result, PageRequest pageRequest) {
        List<Tb_index_detail> content = result.getContent();
        long totalElements = result.getTotalElements();
        List<Tb_index_detail> returnResult = new ArrayList<>();
        Map<String, Object[]> parentmap = nodesettingService.findAllParentOfNode();
        for (Tb_index_detail entryIndex : content) {
            if(entryIndex==null){
                continue;
            }
            String nodeid = entryIndex.getNodeid();
            Tb_data_node node = (Tb_data_node) parentmap.get(nodeid)[0];
            entryIndex.setTdn(node);
            List<Tb_data_node> parents = (List<Tb_data_node>) parentmap.get(nodeid)[1];
            StringBuffer nodefullname = new StringBuffer(node.getNodename());
            for (Tb_data_node parent : parents) {
                if (parent == null) {
                    continue;
                }
                nodefullname.insert(0, "_");
                nodefullname.insert(0, parent.getNodename());
            }
            entryIndex.setNodefullname(nodefullname.toString());
            returnResult.add(entryIndex);
        }
        //List<Entry> entries = entryService.getEntrys(returnResult);
        return new PageImpl(returnResult, pageRequest, totalElements);
    }

    /**
     *   根据entryid生成数据节点全名
     * @return
     */
    public String convertNodefullnameByEntryid(String entryid) {
        Tb_entry_index index = entryIndexRepository.findByEntryid(entryid);

        Map<String, Object[]> parentmap = nodesettingService.findAllParentOfNode();

        StringBuffer nodefullname =new StringBuffer();

        String nodeid = index.getNodeid();
        if (parentmap.get(nodeid) != null) {
            Tb_data_node node = (Tb_data_node) parentmap.get(nodeid)[0];
            List<Tb_data_node> parents = (List<Tb_data_node>) parentmap.get(nodeid)[1];
            if (node.getNodename() != null) {
                nodefullname = new StringBuffer(node.getNodename());
                for (Tb_data_node parent : parents) {
                    if (parent == null) {
                        continue;
                    }
                    nodefullname.insert(0, "_");
                    nodefullname.insert(0, parent.getNodename());
                }
            }
        }

        return nodefullname.toString();
    }
}
