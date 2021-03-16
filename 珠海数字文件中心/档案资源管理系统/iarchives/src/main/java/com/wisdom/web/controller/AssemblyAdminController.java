package com.wisdom.web.controller;

import com.wisdom.util.GainField;
import com.wisdom.util.LogAnnotation;
import com.wisdom.web.entity.*;
import com.wisdom.web.service.AssemblyAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


/**
 * 流水线管理
 */
@Controller
@RequestMapping(value = "/assemblyAdmin")
public class AssemblyAdminController {

	@Autowired
    AssemblyAdminService assemblyAdminService;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping("/main")
	public String main(Model model, String isp) {
		return "/inlet/assemblyAdmin";
	}

	/**
	 * 分页获取流水线数据
	 * @param page 页码
	 * @param limit 每页数
	 * @param condition 查询字段
	 * @param operator  条件
	 * @param content 查询字段值
	 * @return
	 */
	@RequestMapping("/getAssemblyBySearch")
	@ResponseBody
	public Page<Szh_assembly> getAssemblyBySearch(int page, int limit, String sort,String condition, String operator, String content){		
	return assemblyAdminService.getAssemblyBySearch_1(page,limit,sort,condition,operator,content);
	}

	/**
	 * 根据id获取流水线环节
	 * @param id
	 * @return
	 */
	@RequestMapping("/getLinkByid")
	@ResponseBody
	public ExtMsg getLinkByid(String id, String type){
		List<Szh_assembly_flows> assembly_flows = assemblyAdminService.getLinkByid(id,type);
		return new ExtMsg(true, "", GainField.getFieldValues(assembly_flows,"id"));
	}

	/**
	 * 设置流水线环节
	 * @param assemblyid 流水线id
	 * @param ids 环节id
	 * @return
	 */
	@RequestMapping("/setLinkByid")
	@ResponseBody
	public ExtMsg setLinkByid(String assemblyid, String[] ids){
		List<Szh_assembly_node> assembly_nodes = assemblyAdminService.setLinkByid(assemblyid,ids);
		if(assembly_nodes.size()>0){
			return new ExtMsg(true, "", null);
		}else{
			return new ExtMsg(false, "", null);
		}
	}

	/**
	 * 获取流水线环节
	 * @return
	 */
	@RequestMapping("/getLinkAll")
	@ResponseBody
	public List<Szh_assembly_flows> getLinkAll(){
		return assemblyAdminService.getLinkAll();
	}

	/**
	 * 根据id获取流水线人员
	 * @param id
	 * @return
	 */
	@RequestMapping("/getAssemblyUserByid")
	@ResponseBody
	public ExtMsg getAssemblyUserByid(String id,String assemblyflowid){
		List<Tb_user> users = assemblyAdminService.getAssemblyUserByid(id,assemblyflowid);
		return new ExtMsg(true, "", GainField.getFieldValues(users,"userid"));

	}

	/**
	 * 设置流水线用户
	 * @param assemblyid 流水线id
	 * @param ids 用户id
	 * @return
	 */
	@RequestMapping("/setAssemblyUser")
	@ResponseBody
	public ExtMsg setAssemblyUser(String assemblyid, String[] ids,String assemblyflowid){
		List<Szh_assembly_user> assembly_users = assemblyAdminService.setAssemblyUser(assemblyid,ids,assemblyflowid);
		if(assembly_users.size()>0){
			return new ExtMsg(true, "", null);
		}else{
			return new ExtMsg(false, "", null);
		}
	}

	/**
	 * 根据id获取流水线环节
	 * @param id
	 * @return
	 */
	@RequestMapping("/getAssemblyflows")
	@ResponseBody
	public List<Szh_assembly_flows> getAssemblyflows(String id,String type){
		return assemblyAdminService.getLinkByid(id,type);
	}

	/**
	 * 获取流水线管理员
	 * @return
	 */
	@RequestMapping("/getAssemblyAdminUser")
	@ResponseBody
	public Page<Tb_user> getAssemblyAdminUser(int page, int limit, String condition, String operator, String content,String sort){
		return assemblyAdminService.getAssemblyAdminUser(page,limit,condition,operator,content,sort);
	}

	/**
	 * 获取流水线管理员
	 * @return
	 */
	@RequestMapping("/getAdminUser")
	@ResponseBody
	public ExtMsg getAdminUser(){
		List<Szh_admin_user> users = assemblyAdminService.getAdminUser();
		return new ExtMsg(true, "", GainField.getFieldValues(users,"userid"));

	}

	/**
	 * 设置流水线管理员
	 * @param ids 用户id
	 * @return
	 */
	@RequestMapping("/setAdminUser")
	@ResponseBody
	public ExtMsg setAdminUser(String[] ids){
		List<Szh_admin_user> adminusers = assemblyAdminService.setAdminUser(ids);
		if(adminusers.size()>0){
			return new ExtMsg(true, "", null);
		}else{
			return new ExtMsg(false, "", null);
		}
	}

	/**
	 * 删除流水线管理员
	 * @param userids 用户id
	 * @return
	 */
	@RequestMapping("/delAdminUser")
	@ResponseBody
	public ExtMsg delAdminUser(String[] userids){
		int count = assemblyAdminService.delAdminUser(userids);
		if(count>0){
			return new ExtMsg(true, "", count);
		}else{
			return new ExtMsg(false, "", null);
		}
	}

	/**
	 * 根据id获取流水线人员
	 * @param id
	 * @return
	 */
	@RequestMapping("/getAssemblyUser")
	@ResponseBody
	public List<Tb_user> getAssemblyUser(String id,String assemblyflowid){
		return assemblyAdminService.getAssemblyUser(id,assemblyflowid);
	}

	/**
	 * 根据id获取流水线人员
	 * @param assemblyid
	 * @return
	 */
	@RequestMapping("/getAssemblyFlowUser")
	@ResponseBody
	public List<Tb_user> getAssemblyFlowUser(String assemblyid,String organid){
		return assemblyAdminService.getAssemblyFlowUser(assemblyid,organid);
	}

	/**
	 * 根据id获取流水线前置环节
	 * @param id
	 * @return
	 */
	@RequestMapping("/getAssemblyPreflowByid")
	@ResponseBody
	public ExtMsg getAssemblyPreflowByid(String id,String assemblyflowid){
		List<Szh_assembly_flows> flows = assemblyAdminService.getAssemblyPreflowByid(id,assemblyflowid);
		return new ExtMsg(true, "", GainField.getFieldValues(flows,"id"));
	}

	/**
	 * 根据id获取流水线可选择的前置环节
	 * @param id
	 * @return
	 */
	@RequestMapping("/getAssemblyPreflow")
	@ResponseBody
	public List<Szh_assembly_flows> getAssemblyPreflow(String id,String assemblyflowid){
		return assemblyAdminService.getAssemblyPreflow(id,assemblyflowid);
	}

	/**
	 * 设置前置环节
	 * @param assemblyid 流水线id
	 * @param preflowids 环节ids
	 * @return
	 */
	@RequestMapping("/setPreLink")
	@ResponseBody
	public ExtMsg setPreLink(String assemblyid, String[] preflowids,String assemblyflowid){
		List<Szh_assembly_preflow> assembly_preflows = assemblyAdminService.setPreLink(assemblyid,preflowids,assemblyflowid);
		if(assembly_preflows.size()>0){
			return new ExtMsg(true, "", null);
		}else{
			return new ExtMsg(false, "", null);
		}
	}

	/**
	 * 新增流水线
	 * @param title 流水线名
	 * @param remark 备注
	 * @return
	 */
	@LogAnnotation(module = "流水线设置",fields = "title",sites = "1",connect = "##流水线名",startDesc = "增加流水线")
	@RequestMapping("/setAssembly")
	@ResponseBody
	public ExtMsg setAssembly(String title,String remark,String subtype,String code){
		boolean flag = assemblyAdminService.setAssembly(title,remark,subtype,code);
		if(flag){
			return new ExtMsg(true, "操作成功", null);
		}else{
			return new ExtMsg(false, "流水线名已存在", null);
		}
	}

	/**
	 * 获取流水线
	 * @param code 流水线号
	 * @return
	 */
	@RequestMapping("/getAssembly")
	@ResponseBody
	public ExtMsg setAssembly(String code){
		return new ExtMsg(true, "", assemblyAdminService.getAssembly(code));
	}

	/**
	 * 删除流水线
	 * @param codes 流水线号
	 * @return
	 */
	@RequestMapping("/delAssembly")
	@ResponseBody
	public ExtMsg delAssembly(String[] codes){
		boolean flag = assemblyAdminService.delAssembly(codes);
		return new ExtMsg(flag, "", null);
	}
}