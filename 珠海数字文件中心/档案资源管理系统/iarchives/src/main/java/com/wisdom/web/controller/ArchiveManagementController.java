package com.wisdom.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.wisdom.util.FunctionUtil;

/**
 * 档案管理控制器
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = "/archiveManagement")
public class ArchiveManagementController {

	@RequestMapping("/main")
	public String acquisition(Model model, String isp) {
//		Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
//		model.addAttribute("functionButton", functionButton);
		return "/inlet/archiveManagement";
	}
	
	
}