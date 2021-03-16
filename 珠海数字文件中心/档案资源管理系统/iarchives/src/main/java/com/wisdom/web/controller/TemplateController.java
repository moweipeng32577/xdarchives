package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.secondaryDataSource.entity.*;
import com.wisdom.secondaryDataSource.entity.Tb_codeset_sx;
import com.wisdom.secondaryDataSource.entity.Tb_data_node_sx;
import com.wisdom.secondaryDataSource.entity.Tb_data_template_sx;
import com.wisdom.secondaryDataSource.repository.SecondaryDataNodeRepository;
import com.wisdom.secondaryDataSource.repository.SxCodesetRepository;
import com.wisdom.secondaryDataSource.repository.SxTemplateRepository;
import com.wisdom.util.*;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.CodesetRepository;
import com.wisdom.web.repository.DataNodeRepository;
import com.wisdom.web.repository.EntryDetailRepository;
import com.wisdom.web.repository.EntryIndexRepository;
import com.wisdom.web.repository.EventEntryRepository;
import com.wisdom.web.repository.TemplateRepository;
import com.wisdom.web.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * 数据模板控制器，用于检索模板、表单模板、数据表格模板的维护 Created by Rong on 2017/10/30.
 */
@Controller
@RequestMapping(value = "/template")
public class TemplateController {

    @Value("${find.sx.data}")
    private Boolean openSxData;//是否可检索声像系统的声像数据

    @Autowired
    TemplateService templateService;

    @Autowired
    CodesettingService codesettingService;

    @Autowired
    CodesetRepository codesetRepository;

    @Autowired
    DataNodeRepository dataNodeRepository;
    
    @Autowired
    EntryDetailRepository entryDetailRepository;
	
	@Autowired
	EventEntryRepository eventEntryRepository;
	
	@Autowired
	EntryIndexRepository entryIndexRepository;

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    OrganService organService;

    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    FundsService fundsService;

    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    Environment env;

    @Autowired
    ExportExcelService exportExcelService;

    @Autowired
    SecondaryDataNodeRepository secondaryDataNodeRepository;
    @Autowired
    SxTemplateRepository sxTemplateRepository;

    @Autowired
    SxCodesetRepository sxCodesetRepository;

    @RequestMapping("/main")
    public String template(Model model) {
        model.addAttribute("openSxData",openSxData);
        return "/inlet/template";
    }

	@RequestMapping("/grid")
	@ResponseBody
	public List gridtemplate(String nodeid, String type, String info, String eventid, String entryid, String xtType,String table) {
        if("声像系统".equals(xtType)){
            return templateService.gridSxTemplate(nodeid, type, info,table);
        }else{
            List<Tb_data_template> list = new ArrayList<>();
            if (eventid != null && !"".equals(eventid)) {
                // 因为档案关联里的条目一般都由不同的节点数据组成
                List<String> entryids = eventEntryRepository.findEntryidByEventId(eventid);
                if (entryids.size() > 0) {
                    String nodeInfo = entryIndexService.findNodeidByEntryid(entryids.get(0));
                    list = templateService.gridTemplate(nodeInfo, type, info);
                    return list;
                }
            } else if (entryid != null && !"".equals(entryid)) {
                Tb_entry_index entry_index = entryIndexRepository.findByEntryid(entryid);
                if (entry_index != null) {
                    nodeid = entry_index.getNodeid();
                }
            }
            list = templateService.gridTemplate(nodeid, type, info);
            return list;
        }
	}
	
	@RequestMapping("/getInactiveformfield")
    @ResponseBody
    public ExtMsg getInactiveformfield(String nodeid, String field) {
		boolean value = templateRepository.findByNodeidAndFieldcode(nodeid, field).getFenumsedit();
		if (value) {
			return new ExtMsg(true, "显示", null);
		}
		return new ExtMsg(true, "隐藏", null);
    }

    @RequestMapping("/getTableField")
    @ResponseBody
    public Page<Tb_data_template> getTableField(int page, int limit, String condition, String operator,
                              String content, String nodeid, String sort){
        Sort sortobj=WebSort.getSortByJson(sort);
        Page<Tb_data_template> paget=templateService.findByNodeidAndffieldequels(page, limit, condition, operator, content, nodeid, sortobj);
        return paget;
    }

    @RequestMapping("/getSxTableField")
    @ResponseBody
    public Page<Tb_data_template_sx> getSxTableField(int page, int limit, String condition, String operator,
                                                     String content, String nodeid, String sort){
        Sort sortobj=WebSort.getSortByJson(sort);
        Page<Tb_data_template_sx> paget=templateService.findByNodeidAndffieldequelsSx(page, limit, condition, operator, content, nodeid, sortobj);
        return paget;
    }

    @RequestMapping("/queryName")
    @ResponseBody
    public List<ExtSearchData> queryConditionTemplate(String nodeid, String xtType) {
        if("声像系统".equals(xtType)){
            return  templateService.queryConditionSxTemplate(nodeid);
        }else{
            List<ExtSearchData> list = templateService.queryConditionTemplate(nodeid);
            return list;
        }
    }

    @RequestMapping("/excludedQueryName")
    @ResponseBody
    public List deleteGrid(String nodeid, String[] excludeValues) {
        return templateService.excludedQueryConditionTemplate(nodeid, excludeValues);
    }

    @RequestMapping("/form")
    @ResponseBody
    public List formtemplate(String nodeid) {
        List<Tb_data_template> list = templateService.formTemplate(nodeid);
        return list;
    }

    @RequestMapping("/sxform")
    @ResponseBody
    public List sxformTemplate(String nodeid,String tableType,String nodeName) {
        if(nodeName!=null && nodeName.contains("组")){
            tableType="group";
        }else {
            tableType="";
        }
        List<Tb_data_template_sx> list = templateService.sxformTemplate(nodeid,tableType);
        return list;
    }



    @RequestMapping("/formEdit")
    @ResponseBody
    public List formtemplateEdit(String nodeid) {//获取档号编辑字段
        List<Tb_data_template> list = templateService.formTemplateEdit(nodeid);
        return list;
    }

    @RequestMapping("/firstFormField")
    @ResponseBody
    public ExtMsg firstFormField(String nodeid) {
        String firstFormField = templateService.firstFormField(nodeid);
        if (firstFormField == null || "".equals(firstFormField)) {
            return new ExtMsg(false, "获取第一个表单字段失败", null);
        }
        return new ExtMsg(true, "获取第一个表单字段成功", firstFormField);
    }

    @RequestMapping("/changeGrid")
    @ResponseBody
    public List changeGrid(String nodeid, String[] multiValue, String type) {
        long columnWidth = 150;// 列宽
        List<Tb_data_template> list = templateService.findByNodeidOrderByGsequence(nodeid);
        List<Tb_data_template> list_copy = new ArrayList<>();
        long maxSequence = 0;// 取最大序号

        List<String> kfList=new ArrayList<>();//库房位置详细字段集合
        kfList.add("citydisplay");
        kfList.add("unitdisplay");
        kfList.add("roomdisplay");
        kfList.add("zonedisplay");
        kfList.add("sectiondisplay");
        kfList.add("coldisplay");
        kfList.add("layerdisplay");
        kfList.add("sidedisplay");

        for (Tb_data_template data_template : list) {
            Tb_data_template data_template_copy = new Tb_data_template();
            BeanUtils.copyProperties(data_template, data_template_copy);

            if(!kfList.contains(data_template.getFieldcode())) {//不加进库房位置详细字段
                list_copy.add(data_template_copy);
            }

            if (data_template_copy.getGfield()) {
                long sequence = data_template_copy.getGsequence() != null ? data_template_copy.getGsequence() : 0;
                maxSequence = sequence > maxSequence ? sequence : maxSequence;
            }
        }
        if (multiValue == null || multiValue.length == 0) {
            multiValue = codesettingService.getCodesetFieldcodeByNodeid(nodeid);
        }
        if (multiValue != null && multiValue.length > 0) {
            for (String fieldStr : multiValue) {
                for (Tb_data_template data_template : list_copy) {
                    if (data_template.getFieldcode().equals("entryretention")) {
                        if (type != null && type.equals("保管期限调整")) {
                            data_template.setGfield(true);// 设为列表字段
                            data_template.setGsequence(Long.valueOf("1"));// 往前排列
                            if (data_template.getGwidth() == null || data_template.getGwidth() == 0) {
                                data_template.setGwidth(columnWidth);
                            }
                        }
                    }
                    if (fieldStr.equals(data_template.getFieldcode())) {
                        if (!data_template.getGfield()) {
                            data_template.setGfield(true);// 设为列表字段
                            data_template.setGsequence(maxSequence + 1);// 往后排列
                            if (data_template.getGwidth() == null || data_template.getGwidth() == 0) {
                                data_template.setGwidth(columnWidth);
                            }
                        }
                        break;
                    }
                }
                maxSequence++;
            }
        }
        /*List<Tb_data_template> returnList = new ArrayList<>();
        for (Tb_data_template data_template : list_copy) {
            if (data_template.getGfield()) {
                returnList.add(data_template);// 过滤数据
            }
        }*/
        Collections.sort(list_copy, new Comparator<Tb_data_template>() {// 根据序号排序
            @Override
            public int compare(Tb_data_template temp1, Tb_data_template temp2) {
                long sequence1 = temp1.getGsequence() != null ? temp1.getGsequence() : temp1.getFsequence()+100;//没有列表序号就按照表单序号来排
                long sequence2 = temp2.getGsequence() != null ? temp2.getGsequence() : temp1.getFsequence()+100;
                return (int) (sequence1 - sequence2);
            }
        });
        return list_copy;
    }

    @RequestMapping("/codesettingTemplate")
    @ResponseBody
    public ExtMsg getCodesettingTemplate(String nodeid) {
        ExtMsg result = templateService.getCodesettingTemplate(nodeid);
        if (!result.isSuccess()) {
            return result;
        }
        List<Tb_data_template> resultTemplateList = (List<Tb_data_template>) result.getData();
        for (int i = 0; i < resultTemplateList.size(); i++) {
            String fieldcode = resultTemplateList.get(i).getFieldcode();
            long order = codesetRepository.findOrdernumByFieldcodeAndDatanodeid(fieldcode, nodeid);
            resultTemplateList.get(i).setFsequence(order);
        }
        Collections.sort(resultTemplateList, new Comparator<Tb_data_template>() {
            @Override
            public int compare(Tb_data_template temp1, Tb_data_template temp2) {
                long sequence1 = temp1.getFsequence() != null ? temp1.getFsequence() : 0;
                long sequence2 = temp2.getFsequence() != null ? temp2.getFsequence() : 0;
                return (int) (sequence1 - sequence2);
            }
        });
        return new ExtMsg(true, "", resultTemplateList);
    }

    @LogAnnotation(module="系统设置-模板维护",sites = "1",fields = "fieldname",connect = "##字段名称",startDesc = "修改模板，条目详细：")
    @RequestMapping("/UpdateTemplate")
    @ResponseBody
    public ExtMsg UpdateTemplate(Tb_data_template data_template, String xtType,String table) {
        if("声像系统".equals(xtType)){
            Tb_data_template_sx template = templateService.UpdateSxTemplate(data_template, xtType,table);
            if(template==null) {
                return new ExtMsg(true, "字段描述或元数据字段重复", "false");
            }
        }else{
            Tb_data_template template = templateService.UpdateTemplate(data_template, xtType);
            if(template==null) {
                return new ExtMsg(true, "字段描述或元数据字段重复", "false");
            }
        }

        return new ExtMsg(true, "更新成功", "true");
    }
    @RequestMapping("/updateTemplateField")
    @ResponseBody
    public ExtMsg updateTemplateField(String str1,Tb_data_template obj,String xtType){
        Tb_data_template tb_data_template=null;
//        Tb_data_template tb_data_template1=null;
        if(!StringUtils.isEmpty(str1)){
            tb_data_template=JSON.parseObject(str1,Tb_data_template.class);
        }
//        if(!StringUtils.isEmpty(str1)){
//            tb_data_template1=JSON.parseObject(str2,Tb_data_template.class);
//        }
        templateService.updateTemplateByType(tb_data_template,obj,xtType);
        return null;
    }

    @RequestMapping("/updateTemplistField")
    @ResponseBody
    public ExtMsg updateTemplistField(String str1,Tb_data_template obj,String xtType){
        Tb_data_template tb_data_template=null;
        if(!StringUtils.isEmpty(str1)){
            tb_data_template=JSON.parseObject(str1,Tb_data_template.class);
        }
        templateService.updateTemplistByType(tb_data_template,obj,xtType);
        return null;
    }

    @RequestMapping("/copyTemplate")
    @ResponseBody
    public ExtMsg copyTemplate(String sourceid, String withCode, String xtType,String[] targetids) {
        if("声像系统".equals(xtType)){
            return templateService.copySxTemplate(sourceid, withCode,targetids);
        }
        return templateService.copyTemplate(sourceid, withCode,targetids);
    }

    @LogAnnotation(module="系统设置-模板维护",sites = "1",startDesc = "删除模板，条目编号：")
    @RequestMapping("/deleteTemplateByNodeid")
    @ResponseBody
    public ExtMsg deleteTemplateByNodeid(String nodeid,String xtType,String tableType) {
        if("声像系统".equals(xtType)){
            templateService.deleteSxTemplateByNodeid(nodeid,tableType);
        }else{
            templateService.deleteTemplateByNodeid(nodeid);
        }
        return new ExtMsg(true, "模板删除成功", null);
    }

    @RequestMapping("/templates")
    @ResponseBody
    public Page<Object> findTemplateDetailBySearch(int page, int limit, String condition, String operator,
                                                             String content, String nodeid, String sort, String xtType,String tableType) {
        Sort sortobj = WebSort.getSortByJson(sort);
        return templateService.findBySearch(page, limit, condition, operator, content, nodeid, sortobj, xtType,tableType);
    }

    @RequestMapping("/templateDescs")
    @ResponseBody
    public Page<Tb_template_desc> findTemplateDescBySearch(int page, int limit, String condition, String operator,
                                                           String content, String nodeid, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        return templateService.findDescBySearch(page, limit, condition, operator, content, nodeid, sortobj);
    }

    @RequestMapping("/templateSxDescs")
    @ResponseBody
    public Page<Tb_template_desc_sx> findTemplateSxDescBySearch(int page, int limit, String condition, String operator,
                                                           String content, String nodeid, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        return templateService.findSxDescBySearch(page, limit, condition, operator, content, nodeid, sortobj);
    }

    @Transactional
    @RequestMapping("/updateAllDesc")
    @ResponseBody
    public ExtMsg updateAllDesc(){
        //查找所有模板字段
        List<String> fieldcodeList=templateRepository.getAllFilecode();
        for(String fieldcode:fieldcodeList){
            templateService.updateTemplateDesc(fieldcode);//更新每个字段的字段描述信息
        }
        return new ExtMsg(true, "更新成功", null);
    }

    @Transactional
    @RequestMapping("/updateSxAllDesc")
    @ResponseBody
    public ExtMsg updateAllSxDesc(){
        //查找所有模板字段
        List<String> fieldcodeList=sxTemplateRepository.getAllFilecode();
        for(String fieldcode:fieldcodeList){
            templateService.updateTemplateSxDesc(fieldcode);//更新每个字段的字段描述信息
        }
        return new ExtMsg(true, "更新成功", null);
    }

    @RequestMapping("/getAllField")
    @ResponseBody
    public List getAllField(String nodeid, String xtType) {
        return templateService.getAllByField(nodeid, xtType);
    }

    @RequestMapping("/getGroupField")
    @ResponseBody
    public List getGroupField(String nodeid,String nodeType){
        return templateService.getAllField(nodeid,nodeType);
    }

    @RequestMapping("/submitGroupfields")
    @ResponseBody
    public ExtMsg submitGroupfields(String nodeid, String[] fieldnames,String nodeType) {
        templateService.submitGroupfields(nodeid, fieldnames,nodeType);
        return new ExtMsg(true, "成功", null);
    }


    @RequestMapping("/getUnselectForm")
    @ResponseBody
    public List getUnselectForm(String nodeid, String xtType){
        if("声像系统".equals(xtType)){
            List<Tb_data_template_sx> list=sxTemplateRepository.findByNodeid(nodeid);
            return templateService.getSxIndexField(list);
        }else{
            List<Tb_data_template> list=templateRepository.findByNodeid(nodeid);
            return templateService.getIndexField(list);
        }
    }

    /**
     * 设置字段 - 获取模板字段（左）
     * @param nodeid
     * @return
     */
    @RequestMapping("/getSelectedList")
    @ResponseBody
    public List getSelectedList(String nodeid, String xtType) {
        if("声像系统".equals(xtType)){
            List<Tb_data_template_sx> templates = sxTemplateRepository.findByNodeidOrderByFsequence(nodeid);
            return templateService.getSxIndexField(templates);
        }else{
            List<Tb_data_template> templates = templateRepository.findByNodeidOrderByFsequence(nodeid);
            return templateService.getIndexField(templates);
        }
    }
    
    /**
     * 设置检索字段 - 获取检索字段（右）
     * @param nodeid
     * @return
     */
    @RequestMapping("/getSearchField")
    @ResponseBody
    public ExtMsg getSearchField(String nodeid, String xtType) {
        if("声像系统".equals(xtType)){
            List<Tb_data_template_sx> templates = sxTemplateRepository.findQueryByNode(nodeid);// 相应节点下的查询字段的模板
            return new ExtMsg(true, "操作成功", GainField.getFieldValues(templates, "fieldcode","fieldtable"));
        }else{
            List<Tb_data_template> templates = templateRepository.findQueryByNode(nodeid);// 相应节点下的查询字段的模板
            return new ExtMsg(true, "操作成功", GainField.getFieldValues(templates, "fieldcode"));
        }
    }
    
    /**
     * 设置列表字段 - 获取列表字段（右）
     * @param nodeid
     * @return
     */
    @RequestMapping("/getSelectedField")
    @ResponseBody
    public ExtMsg getSelectedField(String nodeid, String xtType) {
        if("声像系统".equals(xtType)){
            List<Tb_data_template_sx> templates = sxTemplateRepository.findGridByNode(nodeid);
            return new ExtMsg(true, "操作成功", GainField.getFieldValues(templates, "fieldcode","fieldtable"));
        }else{
            List<Tb_data_template> templates = templateRepository.findGridByNode(nodeid);
            return new ExtMsg(true, "操作成功", GainField.getFieldValues(templates, "fieldcode"));
        }
    }
    
    /**
     * 更新字段
     * @param nodeid
     * @param
     * @return
     */
    @RequestMapping("/updateQuence")
    @ResponseBody
    public ExtMsg updateQuence(String nodeid, String fieldCode, String rightFieldCode, String type, String xtType) {
        if("声像系统".equals(xtType)){
            templateService.updateSxQuence(nodeid, fieldCode, rightFieldCode, type);
            return new ExtMsg(true, "字段更新成功！", null);
        }else{
            templateService.updateQuence(nodeid, fieldCode, rightFieldCode, type);
            return new ExtMsg(true, "字段更新成功！", null);
        }
    }

    @RequestMapping("/getSelectedByNodeid")
    @ResponseBody
    public ExtMsg getSelectedByNodeid(String nodeid, String xtType,String nodeType) {
        if("声像系统".equals(xtType)){
            List<Tb_data_template_sx> list = templateService.findByNodeidOrderByFsequence(nodeid,nodeType);
            return new ExtMsg(true, "操作成功", GainField.getFieldValues(list, "fieldcode"));
        }else{
            List<Tb_data_template> list = templateRepository.findByNodeidOrderByFsequence(nodeid);
            return new ExtMsg(true, "操作成功", GainField.getFieldValues(list, "fieldcode"));
        }
    }

    @RequestMapping(value = "/getSelectedTableFieldByNode",method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg getSelectedTableFieldByNode(String nodeid, String xtType){
        if("声像系统".equals(xtType)){
            List<Tb_data_template_sx> list =sxTemplateRepository.findFormByNode(nodeid);
            return new ExtMsg(true, "操作成功", GainField.getFieldValues(list, "fieldcode","fieldtable"));
        }else{
            List<Tb_data_template> list =templateRepository.findFormByNode(nodeid);
            return new ExtMsg(true, "操作成功", GainField.getFieldValues(list, "fieldcode"));
        }
    }

    @RequestMapping(value = "/updateFormQueue",method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg updateFormQueue(String nodeid,String[] leftfieldnamelist,String[] rightfieldnamelist, String xtType){
        if("声像系统".equals(xtType)){
            return templateService.updateSxQueue(nodeid,leftfieldnamelist,rightfieldnamelist)?new ExtMsg(true, "字段更新成功！", null):
                    new ExtMsg(false, "字段更新失败", null);
        }else{
            return templateService.updateQueue(nodeid,leftfieldnamelist,rightfieldnamelist)?new ExtMsg(true, "字段更新成功！", null):
                    new ExtMsg(false, "字段更新失败", null);
        }
    }
	@RequestMapping("/getFieldInfo")
	@ResponseBody
	public List<Tb_field> getFieldInfo(String nodeid, String targetNodeid) {
		Tb_data_node node = dataNodeRepository.findByNodeid(targetNodeid);
		if (node.getNodetype() == 2) {
			return null;
		} else {
			// 普通模板字段
			List<String> code = templateRepository.findCodeByNodeid(nodeid);
			List<String> targetCode = templateRepository.findCodeByNodeid(targetNodeid);
			// 字段描述
			List<String> fieldname = templateRepository.findNameByFieldtable(nodeid, "tb_entry_detail");
			List<String> targetFieldName = templateRepository.findNameByFieldtable(targetNodeid, "tb_entry_detail");
			
			List<Tb_field> fields = new ArrayList<>();
			fields = templateService.getFieldInfo(nodeid, targetNodeid, code, targetCode);
			fields.addAll(templateService.getFieldInfo(nodeid, targetNodeid, fieldname, targetFieldName));
			
			return fields;
		}
	}

    @RequestMapping("/submitfields")
    @ResponseBody
    public ExtMsg submitfields(String nodeid, String[] fieldnames, String xtType,String table) {
        if("声像系统".equals(xtType)){
            templateService.submitSxfields(nodeid, fieldnames, xtType,table);
        }else{
            templateService.submitfields(nodeid, fieldnames, xtType,table);
        }
        return new ExtMsg(true, "成功", null);
    }

    @RequestMapping("/synctemplate")
    @ResponseBody
    public ExtMsg synctemplate(String nodeid, String copyType, String syncCodeset, String xtType,String tableType) {
        return new ExtMsg(templateService.synctemplate(nodeid, copyType, syncCodeset, xtType,tableType), "", null);
    }

    @RequestMapping("/getDefault")
    @ResponseBody
    public ExtMsg getDefault(String nodeid, String type, String field) {
        if ("organ".equals(field)) {
            if (type.equals("string")) {// 如果机构/问题是字符类型
                Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
                Tb_right_organ organ = organService.findOrgan(node.getRefid());
                if (organ.getOrganname() != null) {
                    return new ExtMsg(true, "success", organ.getOrganname());
                }
            }
        } else if ("filingyear".equals(field)) {//归档年度
            String year = templateRepository.findFdefaultByFieldcodeAndNodeid("filingyear", nodeid);
            if (year != null && !"".equals(year)) {
                return new ExtMsg(true, "success", year);
            } else {
                Calendar cal = Calendar.getInstance();
                return new ExtMsg(true, "success", String.valueOf(cal.get(Calendar.YEAR)));
            }
        } else {
            String fundsDefult = templateRepository.findFdefaultByFieldcodeAndNodeid("funds", nodeid);
            if (!"".equals(fundsDefult) && fundsDefult != null) {//如果默认值不为空
                return new ExtMsg(true, "success", fundsDefult);
            } else {
                Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
                String funds = fundsService.getOrganFunds(node.getOrganid());
                if (funds != null) {//如果是单位机构的话,直接填充获取到的全宗号
                    return new ExtMsg(true, "success", funds);
                } else {//如果是部门机构的话,需要获取到所属单位的全宗号
                    String unitOrganid = entryIndexService.getOrganInfo(node.getOrganid());
                    String unitFunds = fundsService.getOrganFunds(unitOrganid);
                    return new ExtMsg(true, "success", unitFunds == null ? "" : unitFunds);
                }
            }
        }
        return new ExtMsg(false, "获取数据失败", null);
    }

    @RequestMapping("/getEnumValue")
    @ResponseBody
    public String getEnumValue(String nodeid) {
        return templateService.findFEnumsByNodeid(nodeid);
    }

    @RequestMapping("/isActionable/{nodeid}")
    @ResponseBody
    public ExtMsg isActionable(@PathVariable String nodeid, String xtType) {
        if("声像系统".equals(xtType)){
            List<Tb_data_node_sx> nodeids = new ArrayList<>();
            List<Tb_data_node_sx> ids = nodesettingService.getSxParentidLoop(nodeid, nodeids);
            for (int i = 0; i < ids.size(); i++) {
                if (ids.get(i).getLuckstate() != null && !ids.get(i).getLuckstate().equals("0")) {
                    return new ExtMsg(false, "false", null);
                }
            }
        }else{
            List<Tb_data_node> nodeids = new ArrayList<>();
            List<Tb_data_node> ids = nodesettingService.getParentidLoop(nodeid, nodeids);
            for (int i = 0; i < ids.size(); i++) {
                if (ids.get(i).getLuckstate() != null && !ids.get(i).getLuckstate().equals("0")) {
                    return new ExtMsg(false, "false", null);
                }
            }
        }
        return new ExtMsg(true, "true", null);
    }

    @RequestMapping("/getLuckState/{nodeid}")
    @ResponseBody
    public ExtMsg getLuckState(@PathVariable String nodeid, String xtType) {
        if("声像系统".equals(xtType)){
            Tb_data_node_sx node = secondaryDataNodeRepository.findByNodeid(nodeid);
            if (!node.getLeaf()) {// 如果是父节点
                if (node.getLuckstate() != null && node.getLuckstate().equals("0")) {// 代表模板没有被锁定
                    return new ExtMsg(true, "luck", null);
                }
            } else {
                return new ExtMsg(true, "child", null);
            }
        }else{
            Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
            if (!node.getLeaf()) {// 如果是父节点
                if (node.getLuckstate() != null && node.getLuckstate().equals("0")) {// 代表模板没有被锁定
                    return new ExtMsg(true, "luck", null);
                }
            } else {
                return new ExtMsg(true, "child", null);
            }
        }
        return new ExtMsg(true, "unluck", null);
    }

    @RequestMapping("/updateNodeLuckState/{nodeid}/{state}")
    @ResponseBody
    public ExtMsg updateNodeLuckState(@PathVariable String nodeid, @PathVariable String state, String xtType,String tableType) {
        if (templateService.updateNodeLuckState(nodeid, state, xtType,tableType) > 0) {
            return new ExtMsg(true, "操作模板成功", null);
        }
        return new ExtMsg(false, "操作模板失败", null);
    }

    @RequestMapping("/findArchivecode")
    @ResponseBody
    public ExtMsg findArchivecode(String nodeid) {
        return templateService.findArchivecode(nodeid);
    }

    @RequestMapping("/export")
    @ResponseBody
    public void export(HttpServletResponse response, HttpServletRequest request, String nodeid, String xtType,String tableType) {
        String excelPath = templateService.export(nodeid, xtType,tableType);
        try {
            File zipFile = new File(excelPath + ".zip");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + getOutName(request, zipFile.getName()) + "\"");
            response.setContentType("application/zip");
            FileInputStream inputStream = new FileInputStream(zipFile);
            ServletOutputStream out = response.getOutputStream();
            int b;
            byte[] buffer = new byte[1024];
            while ((b = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, b);
            }
            inputStream.close();
            out.flush();
            out.close();
            if (zipFile.exists()) {// 输出后删除zip
                zipFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getOutName(HttpServletRequest request, String name) throws Exception {
        String agent = request.getHeader("User-Agent");
        if (agent != null && (agent.contains("Firefox") || agent.contains("Safari") || agent.contains("Chrome"))) {
            name = new String((name).getBytes(), "ISO8859-1");
        } else {
            name = URLEncoder.encode(name, "UTF8"); // 其他浏览器
        }
        return name;
    }

    @RequestMapping("/getfdlength")
    @ResponseBody
    public ExtMsg getFieldlength(String tablename, String fieldcode, String xtType) {
        String fdlength = templateService.getFieldlengthByTable(tablename, fieldcode, xtType);
        return new ExtMsg(true, "操作成功", fdlength);
    }

    @RequestMapping("/exportFieldModel")
    @ResponseBody
    public ExtMsg exportFieldModel(String nodeid,String xtType,String fieldTable) {
        //1.根据节点查询出当前节点的字段模板
        List<Tb_codeset> codesetlists;
        List<Tb_data_template> templates;
        if("声像系统".equals(xtType)) {
            return exportSxFieldModel(nodeid,fieldTable);
        }else {
            templates = templateService.formTemplate(nodeid);
            String nodename = exportExcelService.getParentNodeName(nodeid, templates.size());
            //2查询出档号组成设置
            codesetlists = codesettingService.findCodesetByDatanodeid(nodeid);
            String[] codeserFields = templateService.getFiledName(new Tb_codeset());
            //3.调用生成excel方法
            String[] fields = templateService.getFiledName(new Tb_data_template());
            CreateExcel.createFieldModel(templates, nodename, fields, codesetlists, codeserFields);
            //4.执行文件导出
            Map<String, String> map = new HashMap();
            map.put("nodename", nodename);
            return new ExtMsg(true, "导出成功", map);
        }
    }

    public ExtMsg exportSxFieldModel(String nodeid,String fieldTable) {
        //1.根据节点查询出当前节点的字段模板
        List<Tb_codeset_sx> codesetlists;
        List<Tb_data_template_sx> templates;
        templates = sxTemplateRepository.findAllByNodeidAndFieldtableInOrderByFsequence(nodeid,templateService.getTableNameByTableType(fieldTable));
        String nodename = exportExcelService.getSxParentNodeName(nodeid, templates.size());
        //2查询出档号组成设置
        codesetlists = sxCodesetRepository.findByDatanodeidAndFiledtableInOrderByOrdernum(nodeid,templateService.getTableNameByTableType(fieldTable));
        String[] codeserFields = templateService.getFiledName(new Tb_codeset_sx());
        //3.调用生成excel方法
        String[] fields = templateService.getFiledName(new Tb_data_template_sx());
        CreateExcel.createSxFieldModel(templates, nodename, fields, codesetlists, codeserFields);
        //4.执行文件导出
        Map<String, String> map = new HashMap();
        map.put("nodename", nodename);
        return new ExtMsg(true, "导出成功", map);
    }

    @RequestMapping("/downLoadModel")
    @ResponseBody
    public void downLoadModel(HttpServletRequest request, HttpServletResponse response, String nodename) {
        //String sr=java.net.URLDecoder.decode(nodename);
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + "/OAFile" + "/Excel导出/节点字段模板/" + nodename;
        String filepath = path + "/" + nodename + ".xlsx";
        String agent = request.getHeader("User-Agent");
        String filename = nodename + ".xlsx";
        boolean isIE = ValueUtil.isMSBrowser(request);
        try {
            //根据不同浏览器进行不同的编码
            String filenameEncoder = "";
            if (isIE) {
                // IE浏览器
                filenameEncoder = URLEncoder.encode(filename, "utf-8");
                //filenameEncoder = filename.replace("+", " ");
            } else if (agent.contains("Firefox")) {
                /*// 火狐浏览器
                BASE64Encoder base64Encoder = new BASE64Encoder();
                filenameEncoder = "=?utf-8?B?"
                        + base64Encoder.encode(filename.getBytes("utf-8")) + "?=";*/
                filenameEncoder = new String(filename.getBytes("UTF-8"), "ISO8859-1");
            } else {
                // 其它浏览器
                filenameEncoder = URLEncoder.encode(filename, "utf-8");
            }
            InputStream inputStream = new FileInputStream(new File(filepath));
            OutputStream out = response.getOutputStream();
            response.setContentType("multipart/form-data;charset=utf-8");
            response.setHeader("Content-disposition",
                    "attachment;filename=" + filenameEncoder);
            byte[] b = new byte[1024 * 1024 * 10];
            int leng = 0;
            while ((leng = inputStream.read(b)) != -1) {
                out.write(b, 0, leng);
            }
            out.flush();
            inputStream.close();
            out.close();
            if ( new File(path).isDirectory()) {
                FileUtil.delFolder(path);
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }


    @RequestMapping("/delModelFile")
    @ResponseBody
    public void delModelFile(String filename) {
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + "/OAFile" + "/Excel导出/节点字段模板" ;
        if (new File(path).isDirectory()) {
            FileUtil.delFolder(path);
        }
    }

    //解析接收的模板文件
    @RequestMapping("/importFieldModel")
    @ResponseBody
    public ExtMsg importFieldModel(HttpServletResponse response, HttpServletRequest request, String filename, String NodeIdf,String xtType) {
        //1.文件使用组件上传 存放目录 document/OAfile/upload
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + File.separator + "OAFile" + File.separator + "upload" + File.separator + filename;
        //File file = new File(path);
        Map<Integer, Integer> map = new HashMap();
        if("声像系统".equals(xtType)){
            map = templateService.importSxFieldModel(path, NodeIdf);
        }else {
            map = templateService.importFieldModel(path, NodeIdf);
        }
        return new ExtMsg(true, "导入完成", map);

    }

    //删除上传的模板字段文件
    @RequestMapping("/deleteUploadFieldModel")
    @ResponseBody
    public void deleteUploadFieldModel(String filename) {
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + File.separator + "OAFile" + File.separator + "upload" + File.separator + filename;
        File file = new File(path);
        if (file.isFile()) {
            file.delete();
        }
    }

    /**
     *create by lihj on 2018/02/28
     * 快速调整字段，设置列表字段全为是
     */
    @Transactional
    @RequestMapping("/updateGFieldToSet")
    @ResponseBody
    public ExtMsg updateGFieldToSet(String[] templateid,boolean setParam){
        System.err.println("测试ids："+templateid);
        Integer count=templateRepository.updateGfieldToSet(templateid,setParam);
        if(count>0){
            return new ExtMsg(true, "列表字段设置成功"+setParam, null);
        }
        return new ExtMsg(false, "列表字段设置失败", null);
    }

    @Transactional
    @RequestMapping("/updateQFieldToSet")
    @ResponseBody
    public ExtMsg updateQFieldToSet(String[] templateid,boolean setParam){
        Integer count=templateRepository.updateQfieldToSet(templateid,setParam);
        if(count>0){
            return new ExtMsg(true, "检索字段设置成功"+setParam, null);
        }
        return new ExtMsg(false, "检索字段设置失败", null);
    }

    @Transactional
    @RequestMapping("/updateGfieldSequence")
    @ResponseBody
    public ExtMsg updateGfieldSequence(String[] templateid,long setParam){
        Integer count=templateRepository.updateGfieldSequence(templateid,setParam);
        if(count>0){
            return new ExtMsg(true, "列表字段顺序调整成功"+setParam, null);
        }
        return new ExtMsg(false, "列表字段顺序调整失败", null);
    }

    @Transactional
    @RequestMapping("/updateQfieldSequence")
    @ResponseBody
    public ExtMsg updateQfieldSequence(String[] templateid,long setParam){
        Integer count=templateRepository.updateQfieldSequence(templateid,setParam);
        if(count>0){
            return new ExtMsg(true, "检索字段顺序调整成功"+setParam, null);
        }
        return new ExtMsg(false, "检索字段顺序调整失败", null);
    }

    @Transactional
    @RequestMapping("/updateFFieldToSet")
    @ResponseBody
    public ExtMsg updateFFieldToSet(String[] templateid,boolean setParam){
        Integer count=templateRepository.updateFfieldToSet(templateid,setParam);
        if(count>0){
            return new ExtMsg(true, "表单字段设置成功"+setParam, null);
        }
        return new ExtMsg(false, "表单字段设置失败", null);
    }

    @Transactional
    @RequestMapping("/updateFfieldSequence")
    @ResponseBody
    public ExtMsg updateFfieldSequence(String[] templateid,long setParam){
        Integer count=templateRepository.updateFfieldSequence(templateid,setParam);
        if(count>0){
            return new ExtMsg(true, "表单字段顺序调整成功"+setParam, null);
        }
        return new ExtMsg(false, "表单字段顺序调整失败", null);
    }

    @RequestMapping("/getFileRemark")
    @ResponseBody
    public String getFileRemark(String fieldcode,String fieldname) {
        return templateService.getFileRemark(fieldcode,fieldname);
    }

    @RequestMapping("/setMetadata")
    @ResponseBody
    public ExtMsg setMetadata(String[] selectMetadata,String[] selectMetadataIds) {
        List<Tb_data_template> dataTemplates = templateService.setMetadata(selectMetadata,selectMetadataIds);
        if(dataTemplates!=null){
            return new ExtMsg(true,"",null);
        }else{
            return new ExtMsg(false,"",null);
        }
    }
}