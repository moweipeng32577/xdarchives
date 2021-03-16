package com.wisdom.web.controller;

import com.wisdom.util.ExportUtil;
import com.wisdom.util.GainField;
import com.wisdom.util.LogAnnotation;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.EntryDetailRepository;
import com.wisdom.web.repository.EntryIndexRepository;
import com.wisdom.web.repository.IndexDetailCaptureRepository;
import com.wisdom.web.service.CodesettingAlignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by RonJiang on 2018/1/22 0022.
 */
@Controller
@RequestMapping(value = "/codesettingAlign")
public class CodesettingAlignController {

    @Autowired
    CodesettingAlignService codesettingAlignService;
    
    @Autowired
    EntryDetailRepository entryDetailRepository;
    
    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    IndexDetailCaptureRepository indexDetailCaptureRepository;

    @RequestMapping("/main")
    public String index() {
        return "/inlet/codesettingAlign";
    }

    /**
     *  档号对齐操作
     */
    @LogAnnotation(module = "档号对齐",startDesc = "对齐操作，节点id为：",sites = "8")
    @RequestMapping("/align")
    @ResponseBody
    public ExtMsg alignArchivecode(String condition, String operator, String content, Tb_index_detail formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic, String nodeid, boolean ifSearchLeafNode, boolean ifContainSelfNode,String datasoure){
        ExtMsg result = codesettingAlignService.alignArchivecode(condition,operator,content,formConditions,formOperators,daterangedata,logic,nodeid,ifSearchLeafNode,ifContainSelfNode,datasoure);
        if(result!=null){
            if(!result.isSuccess()){
                return result;
            }
        }
        return new ExtMsg(true,"档号对齐操作成功",null);
    }

	@RequestMapping("/export")
	@ResponseBody
	public void export(HttpServletResponse response, String[] columnArray, String condition, String operator,
			String content, Tb_entry_index formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata,
			String logic, String nodeid, boolean ifSearchLeafNode, boolean ifContainSelfNode) {
		List<Tb_entry_index> entry_indexList = new ArrayList<>();
        if (condition != null && formConditions.toFieldnameString().contains(condition)) {
            entry_indexList = codesettingAlignService.findAlldataBySearch(null, condition, operator, content,
					formConditions, formOperators, daterangedata, logic, nodeid, ifSearchLeafNode, ifContainSelfNode);// 检索全部数据（不分页）
		} else {
			entry_indexList = codesettingAlignService.findAlldataBySearch("副表", condition, operator, content,
					formConditions, formOperators, daterangedata, logic, nodeid, ifSearchLeafNode, ifContainSelfNode);// 检索全部数据（不分页）
		}

		String[] keys = new String[columnArray.length];
		String[] names = new String[columnArray.length];
		for (int i = 0; i < columnArray.length; i++) {
			String[] split = columnArray[i].split("-");
			keys[i] = split[0];
			names[i] = split[1];
		}
		List<Map<String, Object>> list = createExcelRecord(entry_indexList, keys);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
		String fileName = "档号对齐前备份" + sdf.format(new Date());
		ExportUtil exportUtil = new ExportUtil(fileName, response, list, keys, names);
		exportUtil.exportExcel();
	}

    private List<Map<String, Object>> createExcelRecord(List<Tb_entry_index> entry_indexList, String[] keys) {
        List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
        Tb_entry_index entry_index;
        for (int j = 0; j < entry_indexList.size(); j++) {
            entry_index = entry_indexList.get(j);
            Map<String, Object> mapValue = new HashMap<>();
            Tb_entry_detail details = entryDetailRepository.findByEntryid(entry_indexList.get(j).getEntryid());
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

    /**
     *  数据采集-档号对齐操作
     */
    // @LogAnnotation(module = "档号对齐",startDesc = "对齐操作，节点id为：",sites = "8")
    @RequestMapping("/collectAlign")
    @ResponseBody
    public ExtMsg alignArchivecode(String condition, String operator, String content, Tb_entry_index_capture formConditions,
                                   ExtOperators formOperators, ExtDateRangeData daterangedata, String logic, String nodeid, boolean ifSearchLeafNode, boolean ifContainSelfNode){
        ExtMsg result = codesettingAlignService.alignArchivecode(condition,operator,content,formConditions,formOperators,daterangedata,logic,nodeid,ifSearchLeafNode,ifContainSelfNode);
        if(result!=null){
            if(!result.isSuccess()){
                return result;
            }
        }
        return new ExtMsg(true,"档号对齐操作成功",null);
    }

    @RequestMapping("/auditExport")
    @ResponseBody
    public void export(HttpServletResponse response, String[] columnArray, String condition, String operator,
                       String content, Tb_index_detail_capture formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata,
                       String logic, String nodeid, boolean ifSearchLeafNode, boolean ifContainSelfNode,String docid) {
        List<Tb_index_detail_capture> entry_indexList = new ArrayList<>();
        if (condition != null && formConditions.toFieldnameString().contains(condition)) {
            entry_indexList = codesettingAlignService.findAlldataBySearch(null, condition, operator, content,
                    formConditions, formOperators, daterangedata, logic, nodeid, ifSearchLeafNode, ifContainSelfNode,docid);// 检索全部数据（不分页）
        } else {
            entry_indexList = codesettingAlignService.findAlldataBySearch("副表", condition, operator, content,
                    formConditions, formOperators, daterangedata, logic, nodeid, ifSearchLeafNode, ifContainSelfNode,docid
            );// 检索全部数据（不分页）
        }

        String[] keys = new String[columnArray.length];
        String[] names = new String[columnArray.length];
        for (int i = 0; i < columnArray.length; i++) {
            String[] split = columnArray[i].split("-");
            keys[i] = split[0];
            names[i] = split[1];
        }
        List<Map<String, Object>> list = createAuditExcelRecord(entry_indexList, keys);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String fileName = "档号对齐前备份" + sdf.format(new Date());
        ExportUtil exportUtil = new ExportUtil(fileName, response, list, keys, names);
        exportUtil.exportExcel();
    }

    //数据审核用的导出档号
    private List<Map<String, Object>> createAuditExcelRecord(List<Tb_index_detail_capture> entry_indexList, String[] keys) {
        List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
        Tb_index_detail_capture entry_index;
        for (int j = 0; j < entry_indexList.size(); j++) {
            entry_index = entry_indexList.get(j);
            Map<String, Object> mapValue = new HashMap<>();
            Tb_index_detail_capture details = indexDetailCaptureRepository.findByEntryid(entry_indexList.get(j).getEntryid());
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
}