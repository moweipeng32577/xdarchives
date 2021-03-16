package com.wisdom.web.controller;

import com.wisdom.util.GainField;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.EntryIndexCaptureRepository;
import com.wisdom.web.repository.EntryIndexRepository;
import com.wisdom.web.repository.UserDataNodeRepository;
import com.wisdom.web.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by RonJiang on 2018/1/22 0022.
 */

@Controller
@RequestMapping(value = "/summarization")
public class SummarizationController {
	
    @Autowired
    TemplateService templateService;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    NodesettingService nodesettingService;
    
    @Autowired
    UserDataNodeRepository userDataNodeRepository;
    
    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    EntryIndexCaptureRepository entryIndexCaptureRepository;

    @RequestMapping("/main")
    public String index() {
        return "/inlet/summarization";
    }

	@RequestMapping("/getSelectedByNodeId")
	@ResponseBody
	public List getSelectedByNodeId(String nodeid) {
		List<Tb_data_template> list = templateService.findByNodeid(nodeid);
		for (Tb_data_template data_template : list) {
			data_template.setFieldname(data_template.getFieldcode() + "_" + data_template.getFieldname());
		}
		return list;
	}

    @RequestMapping("/summary")
    @ResponseBody
    public ExtMsg summary(String[] multiValue, String comboValue, String nodeId, Tb_entry_index conditions, ExtOperators operators,
                          ExtDateRangeData daterangedata, String logic,boolean ifSearchLeafNode,boolean ifContainSelfNode,String datasoure) {
    	String result = "";

        if("capture".equals(datasoure)) {
            Tb_entry_index_capture capture=new Tb_entry_index_capture();
            BeanUtils.copyProperties(conditions,capture);
            List<Tb_entry_index_capture> entry_indexList = findByClassifySearchCapture(capture, operators, daterangedata, logic, nodeId, ifSearchLeafNode, ifContainSelfNode);
            if (entry_indexList.size() == 0 || "count".equals(comboValue)) {
                result = "总记录数：" + entry_indexList.size() + "";
            } else {
                for (String field : multiValue) {
                    String[] fieldArr = field.split("_");
                    String[] fieldValue = GainField.getFieldValues(entry_indexList, fieldArr[0]);
                    String resultValue = calculate(fieldValue, comboValue);
                    result += fieldArr[1] + "：" + resultValue + "\n";
                }
            }
        }else {
            List<Tb_entry_index> entry_indexList = findByClassifySearch(conditions, operators, daterangedata, logic, nodeId, ifSearchLeafNode, ifContainSelfNode);
            if (entry_indexList.size() == 0 || "count".equals(comboValue)) {
                result = "总记录数：" + entry_indexList.size() + "";
            } else {
                for (String field : multiValue) {
                    String[] fieldArr = field.split("_");
                    String[] fieldValue = GainField.getFieldValues(entry_indexList, fieldArr[0]);
                    String resultValue = calculate(fieldValue, comboValue);
                    result += fieldArr[1] + "：" + resultValue + "\n";
                }
            }
        }
        return new ExtMsg(true, "", result);
    }

    private String calculate(String[] fieldValue, String comboValue) {
        Long[] fieldNum = new Long[fieldValue.length];
        for (int i = 0; i < fieldValue.length; i++) {
            try {
                boolean isNum = Pattern.matches("^\\d+$", fieldValue[i]);
                if (isNum) {
                    String value = "".equals(fieldValue[i]) ? "0" : fieldValue[i];
                    //value=value.length()>11?"0":value;//判断是否超出int最大值
                    fieldNum[i] = Long.parseLong(value);
                } else {
                    fieldNum[i] = 0L;
                }
            } catch (java.lang.NumberFormatException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < fieldNum.length - 1; i++) {   //冒泡
            for (int j = 0; j < fieldNum.length - i - 1; j++) {
                if (fieldNum[j] > fieldNum[j + 1]) {    //把大的值交换到后面
                    Long temp = fieldNum[j];
                    fieldNum[j] = fieldNum[j + 1];
                    fieldNum[j + 1] = temp;

                    String str = fieldValue[j];//原值跟着换
                    fieldValue[j] = fieldValue[j + 1];
                    fieldValue[j + 1] = str;
                }
            }
        }

        String returnStr = "";
        if ("max".equals(comboValue)) {
            returnStr = fieldValue[fieldValue.length - 1] + "";
        } else if ("min".equals(comboValue)) {
            returnStr = fieldValue[0] + "";
        } else if ("ave".equals(comboValue)) {
            int sum = 0;
            for (Long num : fieldNum) {
                sum += num;
            }
            returnStr = String.format("%.2f", sum / (double) fieldNum.length);
        } else if ("sum".equals(comboValue)) {
            int sum = 0;
            for (Long num : fieldNum) {
                sum += num;
            }
            returnStr = sum + "";
        }
        return returnStr;
    }

	private List<Tb_entry_index> findByClassifySearch(Tb_entry_index conditions, ExtOperators operators,
			ExtDateRangeData daterangedata, String logic, String nodeId, boolean ifSearchLeafNode,
			boolean ifContainSelfNode) {
        Specification<Tb_entry_index> formAdvancedSearch = ClassifySearchService.getFormAdvancedIndexSearch(conditions,
                operators, logic);
        String[] nodeIds = null;
        if (ifSearchLeafNode) {
            List<String> nodeIdList = new ArrayList<>(
                    nodesettingService.getNodeidLoop(nodeId, ifContainSelfNode, new ArrayList<String>()));
            nodeIds = new String[nodeIdList.size() + 1];
            nodeIdList.add(nodeId);
            nodeIdList.toArray(nodeIds);
        } else {
            nodeIds = new String[]{nodeId};
        }
        Specification<Tb_entry_index> searchNodeIdCondition = ClassifySearchService.getSearchNodeidIndex(nodeIds);
        Specification<Tb_entry_index> dateRangeCondition = ClassifySearchService
                .getDateRangeIndexCondition(daterangedata);
        return entryIndexRepository
                .findAll(Specifications.where(formAdvancedSearch).and(searchNodeIdCondition).and(dateRangeCondition));

	}

    private List<Tb_entry_index_capture> findByClassifySearchCapture(Tb_entry_index_capture conditions, ExtOperators operators,
                                                      ExtDateRangeData daterangedata, String logic, String nodeId, boolean ifSearchLeafNode,
                                                      boolean ifContainSelfNode) {
        Specification<Tb_entry_index_capture> formAdvancedSearch = ClassifySearchService.getFormAdvancedIndexCaptureSearch(conditions,
                operators, logic);
        String[] nodeIds = null;
        if (ifSearchLeafNode) {
            List<String> nodeIdList = new ArrayList<>(
                    nodesettingService.getNodeidLoop(nodeId, ifContainSelfNode, new ArrayList<String>()));
            nodeIds = new String[nodeIdList.size() + 1];
            nodeIdList.add(nodeId);
            nodeIdList.toArray(nodeIds);
        } else {
            nodeIds = new String[]{nodeId};
        }
        Specification<Tb_entry_index_capture> searchNodeIdCondition = ClassifySearchService.getSearchNodeidIndexCapture(nodeIds);
        Specification<Tb_entry_index_capture> dateRangeCondition = ClassifySearchService
                .getDateRangeIndexCaptureCondition(daterangedata);
        return entryIndexCaptureRepository
                .findAll(Specifications.where(formAdvancedSearch).and(searchNodeIdCondition).and(dateRangeCondition));

    }

    @RequestMapping("/datasoure")
    @ResponseBody
    public boolean getAllEntryIndex(Tb_entry_index conditions, ExtOperators operators, ExtDateRangeData daterangedata,
                                    String logic, String nodeId, boolean ifSearchLeafNode, boolean ifContainSelfNode,String datasoure) {
        Integer number=0;
        if("capture".equals(datasoure)) {
            Tb_entry_index_capture capture=new Tb_entry_index_capture();
            BeanUtils.copyProperties(conditions,capture);
            number= findByClassifySearchCapture(capture, operators, daterangedata, logic, nodeId, ifSearchLeafNode, ifContainSelfNode).size();
        }else {
            number= findByClassifySearch(conditions, operators, daterangedata, logic, nodeId, ifSearchLeafNode, ifContainSelfNode).size();
        }
        if(number==0){
           return false;
        }else {
            return true;
        }
    }
}