package com.wisdom.web.controller;

import com.wisdom.util.GuavaCache;
import com.wisdom.util.GuavaUsedKeys;
import com.wisdom.util.LogAnnotation;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.NodesettingTree;
import com.wisdom.web.entity.Tb_classification;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.repository.ClassificationRepository;
import com.wisdom.web.repository.DataNodeRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.ClassificationService;
import com.wisdom.web.service.NodesettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 分类设置控制器 Created by tanly on 2017/11/1 0024.
 */
@Controller
@RequestMapping(value = "/classificationsetting")
public class ClassificationsettingController {

	@Value("${find.sx.data}")
	private Boolean openSxData;//是否可检索声像系统的声像数据

	@Autowired
	ClassificationService classificationService;

	@Autowired
	ClassificationRepository classificationRepository;

	@Autowired
	DataNodeRepository dataNodeRepository;

	@Autowired
	NodesettingService nodesettingService;

	@RequestMapping("/main")
	public String classificationsetting(Model model) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
		model.addAttribute("userid",userDetails.getUserid());
		model.addAttribute("loginname", userDetails.getLoginname());
		model.addAttribute("openSxData",openSxData);
		return "/inlet/classificationsetting";
	}

	@LogAnnotation(module="系统设置-分类设置",sites = "1",fields = "classname",connect = "##分类名称",startDesc = "修改分类，条目详细：")
	@RequestMapping("/updateClass")
	@ResponseBody
	public ExtMsg updateClass(Tb_classification classification, String parentclassid_real, String nodename, String xtType) {
		Tb_classification tb_classification = classificationService.updateClass(classification, parentclassid_real,
				nodename, xtType);
		if (tb_classification != null) {
			GuavaCache.removeValueByKey(GuavaUsedKeys.NODE_ALL_LIST);   //清空记录在变量中的所有节点数据
			//更新数据节点更新时间
			nodesettingService.updateNodeChangeTime();
			return new ExtMsg(true, "修改成功", tb_classification);
		}
		return new ExtMsg(false, "请重新命名，该名称的数据节点已存在！", null);
	}

	@RequestMapping("/classValid")
	@ResponseBody
	public ExtMsg classValid(String formname, String parentid) {
		List<Tb_classification> classificationList = classificationRepository.findByClassnameAndParentclassid(formname,
				parentid);
		boolean isExist = classificationList.size() > 0;
		return new ExtMsg(isExist, "", null);
	}


	@LogAnnotation(module="系统设置-分类设置",sites = "1",fields = "classname",connect = "##分类名称",startDesc = "新增分类，条目详细：")
	@RequestMapping("/addClass")
	@ResponseBody
	public ExtMsg addClass(Tb_classification classification, String parentclassid_real, String xtType) {
		classification=classificationService.addClass(classification, parentclassid_real,xtType);
		GuavaCache.removeValueByKey(GuavaUsedKeys.NODE_ALL_LIST);   //清空记录在变量中的所有节点数据
		//更新数据节点更新时间
		nodesettingService.updateNodeChangeTime();
		return new ExtMsg(true, "增加成功",classification);
	}

	@LogAnnotation(module="系统设置-分类设置",sites = "1",startDesc = "删除分类，条目编号：")
	@RequestMapping("/deleteClass")
	@ResponseBody
	public void deleteClass(String[] ids, String xtType) {
		classificationService.deleteClassifications(ids,xtType);
		GuavaCache.removeValueByKey(GuavaUsedKeys.NODE_ALL_LIST);   //清空记录在变量中的所有节点数据
		//更新数据节点更新时间
		nodesettingService.updateNodeChangeTime();
	}

	@RequestMapping("/classifications")
	@ResponseBody
	public Page<Tb_classification> findClassificationDetailBySearch(int page, int limit, String condition,
			String operator, String content, String classificationid, String xtType, String sort) {
		Sort sortobj = WebSort.getSortByJson(sort);
		return classificationService.findBySearch(page, limit, condition, operator, content, classificationid, xtType, sortobj);
	}

	@RequestMapping("/classids")
	@ResponseBody
	public Page<Tb_classification> findClassificationByClassids(int page, int limit, String classid, String sort) {
		Sort sortobj = WebSort.getSortByJson(sort);
		return classificationService.findByClassids(page, limit, classid, sortobj);
	}

	@RequestMapping("/classsortsequence")
	@ResponseBody
	public void findOrganBySortsequence(String[] classid, int currentcount, String operate) {
		classificationService.findBySortquence(classid, currentcount, operate);
	}

	@RequestMapping("/classification/{classid}/{overclassid}/{targetorder}")
	@ResponseBody
	public ExtMsg modifyorder(@PathVariable String classid, @PathVariable String targetorder,
			@PathVariable String overclassid) {
		Tb_classification classification = classificationService.findClassification(classid);
		classificationService.modifyClassOrder(classification, Integer.parseInt(targetorder), overclassid);
		return null;
	}

	@RequestMapping("/preview")
	@ResponseBody
	public List<NodesettingTree> preview(Tb_classification classification, String parentclassid_real, String previewType,String xtType) {
		if (previewType == null) {
			return null;
		} else if (previewType.equals("add")) {
			return classificationService.addClassPreview(classification, parentclassid_real,xtType);
		} else {
			return classificationService.updatePreview(classification.getClassname(), classification.getClassid(),xtType);
		}
	}

	//@LogAnnotation(module="系统设置-机构管理",sites = "1",startDesc = "删除机构，条目编号：")
	@RequestMapping("/deletePreview")
	@ResponseBody
	public List<NodesettingTree> deletePreview(String[] ids,String xtType) {
		if (ids == null || ids.length == 0) {
			return null;
		}
		if("声像系统".equals(xtType)){
			return classificationService.deleteSxPreview(ids,xtType);
		}else{
			return classificationService.deletePreview(ids,xtType);
		}
	}

	@RequestMapping("/deleteValidate")
	@ResponseBody
	public ExtMsg deleteValidate(String[] ids) throws InterruptedException, ExecutionException {
		if (ids == null || ids.length == 0) {
			return new ExtMsg(true, "失败", null);
		}
		return new ExtMsg(classificationService.deleteValidate(ids), "增加成功", null);
	}
}
