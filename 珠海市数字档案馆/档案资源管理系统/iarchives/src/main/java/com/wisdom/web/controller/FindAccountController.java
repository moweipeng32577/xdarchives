package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.util.FunctionUtil;
import com.wisdom.web.entity.Tb_work;
import com.wisdom.web.repository.WorkRepository;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wisdom.web.entity.ExtMsg;

@Controller
@RequestMapping(value = "/findAccount")
public class FindAccountController {

	@Autowired
	WorkRepository workRepository;

	@RequestMapping("/main")
	public String findAccount(Model model) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		model.addAttribute("userid", userDetails.getUserid());
		Tb_work workBorrrow = workRepository.findByWorktext("查档审批");
		if(workBorrrow!=null){
			model.addAttribute("borrrowSendmsg","1".equals(workBorrrow.getSendmsgstate())?true:false);  //查档是否短信通知
		}else{
			model.addAttribute("borrrowSendmsg",false);
		}
		return "/inlet/findAccount";
	}
	
	@RequestMapping("/getIdcardInfo")
    @ResponseBody
    public ExtMsg getIdcardInfo(String idcard) {
		if (idcard != null) {
			
		}
		return null;
	}
	
	
}