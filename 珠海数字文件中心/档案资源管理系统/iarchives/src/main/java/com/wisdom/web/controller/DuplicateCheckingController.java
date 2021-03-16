package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.util.ExportUtil;
import com.wisdom.util.FunctionUtil;
import com.wisdom.util.GainField;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.EntryDetailRepository;
import com.wisdom.web.repository.EntryIndexRepository;
import com.wisdom.web.service.ClassifySearchService;
import com.wisdom.web.service.DuplicationCheckingService;
import com.wisdom.web.service.EntryIndexService;
import com.wisdom.web.service.NodesettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by RonJiang on 2018/1/22 0022.
 */

@Controller
@RequestMapping(value = "/duplicateChecking")
public class DuplicateCheckingController {
    @Autowired
    DuplicationCheckingService duplicationCheckingService;

    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    EntryIndexRepository entryIndexRepository;
    
    @Autowired
    EntryDetailRepository entryDetailRepository;
    
    @Autowired
    ClassifySearchService classifySearchService;
    @Autowired
    EntryIndexService entryIndexService;

    @RequestMapping("/main")
    public String index(Model model, String isp) {
        Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
        model.addAttribute("functionButton", functionButton);
        return "/inlet/duplicateChecking";
    }

    @RequestMapping("/findBySearch")
    @ResponseBody
    public Page<Tb_index_detail> findBySearch(int start, int page, int limit, String condition, String operator, String content,String datasoure,
                                             Tb_index_detail formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata,
                                             String logic, String nodeid, String[] multiValue, boolean ifSearchLeafNode, boolean ifContainSelfNode,String sort) {
        List<Tb_index_detail> tb_index_details = entryIndexService.getEntrybaseNew(nodeid, condition, operator, content,datasoure, formConditions, formOperators, daterangedata, logic, ifSearchLeafNode, ifContainSelfNode);
        //获得检索数据
        //List<Tb_entry_index> entry_indexAddList = findBySearch(condition, operator, content, nodeid, formConditions, formOperators, daterangedata, logic, ifSearchLeafNode, ifContainSelfNode);
        //分组
        Map<String, List<Tb_index_detail>> resultMap = new HashMap<>();
        for (Tb_index_detail entry_index : tb_index_details) {
            String subField = getFieldValues(entry_index, multiValue);
            resultMap.computeIfAbsent(subField,k -> new ArrayList<>()).add(entry_index);//java8新特性：构建本地缓存，效率更高
        }
        //过滤、合并集合
        List<Tb_index_detail> returnList = new ArrayList<>();
        Iterator<Map.Entry<String, List<Tb_index_detail>>> it = resultMap.entrySet().iterator();
        while (it.hasNext()) {
            List<Tb_index_detail> entry_indexIterator = it.next().getValue();
            if (entry_indexIterator.size() != 1||condition!=null) {
                returnList.addAll(entry_indexIterator);
            }
        }
        //分页返回
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        List<Tb_index_detail> returnPage = new ArrayList<>();
        limit = start + limit > returnList.size() ? returnList.size() : start + limit;
        for (int i = start; i < limit; i++) {
            returnPage.add(returnList.get(i));
        }
        return new PageImpl(returnPage, pageRequest, returnList.size());
    }

    public static String getFieldValues(Tb_index_detail entry_index, String[] multiValue) {
        String subField = "";
        if(multiValue!=null){
            for (String fieldStr : multiValue) {
                String value = ",";
                if (GainField.getFieldValueByName(fieldStr, entry_index) != null) {
                    value = GainField.getFieldValueByName(fieldStr, entry_index) + ",";
                }
                subField += value;
            }
        }
        return subField;
    }

    public List<Tb_entry_index> findBySearch(String condition, String operator, String content, String nodeid, Tb_entry_index formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic, boolean ifSearchLeafNode, boolean ifContainSelfNode) {
        String[] nodeids;
        if (ifSearchLeafNode) {
            List<String> nodeidList = nodesettingService.getNodeidLoop(nodeid, ifContainSelfNode, new ArrayList<String>());
            nodeids = new String[nodeidList.size()];
            nodeidList.toArray(nodeids);
        } else {
            nodeids = new String[]{nodeid};
        }
        Specification<Tb_entry_index> searchNodeidCondition = ClassifySearchService.getSearchNodeidIndex(nodeids);
        Specifications specifications = Specifications.where(searchNodeidCondition);
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        Specification<Tb_entry_index> formAdvancedSearch = ClassifySearchService.getFormAdvancedIndexSearch(formConditions, formOperators, logic);
        Specification<Tb_entry_index> dateRangeCondition = ClassifySearchService.getDateRangeIndexCondition(daterangedata);
        return entryIndexRepository.findAll(specifications.and(formAdvancedSearch).and(dateRangeCondition));
    }

    @RequestMapping(value = "/export")
    @ResponseBody
    public void export(String exportFileName,String[] idArray,String[] columnArray, HttpServletResponse response, HttpServletRequest request) {
        List<Tb_entry_index> entry_indexList = entryIndexRepository.findByEntryidIn(idArray);

        String[] keys = new String[columnArray.length];
        String[] names = new String[columnArray.length];
        for (int i = 0; i < columnArray.length; i++) {
            String[] split = columnArray[i].split("-");
            keys[i] = split[0];
            names[i] = split[1];
        }
        List<Map<String, Object>> list = createExcelRecord(entry_indexList, keys);
        ExportUtil exportUtil = new ExportUtil(exportFileName, response, list, keys, names);
        exportUtil.exportExcel();
    }

    private List<Map<String, Object>> createExcelRecord(List<Tb_entry_index> entry_indexList, String[] keys) {
        List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
        Tb_entry_index entry_index;
        for (int j = 0; j < entry_indexList.size(); j++) {
            entry_index = entry_indexList.get(j);
            Tb_entry_detail details = entryDetailRepository.findByEntryid(entry_indexList.get(j).getEntryid());
            Map<String, Object> mapValue = new HashMap<>();
            for (String key : keys) {
            	if (details != null && details.toString().contains(key)) {
					mapValue.put(key, GainField.getFieldValueByName(key, details));
				} else {
            		mapValue.put(key, GainField.getFieldValueByName(key, entry_index));
            	}
            }
            listmap.add(mapValue);
        }
        return listmap;
    }

    public static void main(String[] age){}{
        Tb_index_detail tb_index_detail = new Tb_index_detail();
        tb_index_detail.setResponsible("1");
        String str=getFieldValues(tb_index_detail,new String[]{"responsible"});
        System.out.println(str);
    }
}
