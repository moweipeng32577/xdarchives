package com.wisdom.web.controller;

import com.wisdom.web.entity.*;
import com.wisdom.web.repository.SzhAssemblyUserRepository;
import com.wisdom.web.repository.SzhEntryIndexCaptureRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.DigitalProcessService;
import com.wisdom.web.service.EntryIndexService;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 数字化加工
 */
@Controller
@RequestMapping(value = "/digitalProcess")
public class DigitalProcessController {

	@Autowired
    DigitalProcessService digitalProcessService;

	@Autowired
    EntryIndexService entryIndexService;

	@Autowired
	SzhEntryIndexCaptureRepository entryIndexCaptureRepository;

	@Value("${system.document.rootpath}")
	private String rootpath;// 系统文件根目录

	@RequestMapping("/main")
	public String main(Model model, String isp) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 系统绑定对象(全局)
		model.addAttribute("realname",userDetails.getRealname());
		return "/inlet/digitalProcess";
	}

	/**
	 * 分页获取批次条目单据信息
	 * @param type 类型
	 * @param status 单据状态
	 * @param batchcode 批次号
	 * @param page 页码
	 * @param limit 每页数
	 * @param condition 查询字段
	 * @param operator  条件
	 * @param content 查询字段值
	 * @return
	 */
	@RequestMapping("/getCalloutEntryBySearch")
	@ResponseBody
	public Page<Szh_callout_entry> getCalloutEntryBySearch(String type, String status, String batchcode,
														   String flownodeid,String assemblyid, int page, int limit,
														   String condition, String operator, String content,
														   String sort){
		return digitalProcessService.getCalloutEntryBySearch(type,status,batchcode,flownodeid,assemblyid,page,limit,condition,operator,content,sort);
	}

	/**
	 * 分页获取完成环节批次条目单据信息
	 * @param batchcode 批次号
	 * @param page 页码
	 * @param limit 每页数
	 * @param condition 查询字段
	 * @param operator  条件
	 * @param content 查询字段值
	 * @return
	 */
	@RequestMapping("/getFinishCalloutEntryBySearch")
	@ResponseBody
	public Page<Szh_callout_entry> getFinishCalloutEntryBySearch(String batchcode, int page, int limit, String condition, String operator, String content){
		return digitalProcessService.getFinishCalloutEntryBySearch(batchcode,page,limit,condition,operator,content);
	}


	/**
	 * 获取流程节点树
	 * @return
	 */
	@RequestMapping("/getFlowsTree")
	@ResponseBody
	public List<ExtNcTree> getFlowsTree(String assemblyid){
		return digitalProcessService.getFlowsTree(assemblyid);
	}

	/**
	 * 获取调档批次节点树
	 * @param batchcode 批次号
	 * @return
	 */
	@RequestMapping("/getBatchTree")
	@ResponseBody
	public List<ExtNcTree> getBatchTree(String batchcode){
		return digitalProcessService.getBatchTree(batchcode);
	}

	/**
	 * 签收条目
	 * @param ids 条目数组
	 * @param node 节点
	 * @param status 状态
	 * @return
	 */
	@RequestMapping("/calloutSign")
	@ResponseBody
	public synchronized ExtMsg calloutSign(String[] ids, String node, String status,String assemblyid) {
		boolean state = false;
		try {
			String checkResult = digitalProcessService.checkSign(ids, node, status, true);
			if (checkResult != null) {
				return new ExtMsg(false, "条目：" + checkResult + "已被他人处理", "重复操作");
			}
			digitalProcessService.calloutSign(ids, node, status,assemblyid);
			state = true;
		} catch (Exception e) {
			e.printStackTrace();
			return new ExtMsg(state, "", null);
		}
		return new ExtMsg(state, "", null);
	}

	/**
	 * 完成环节入库
	 * @param ids 条目数组
	 * @return
	 */
	@RequestMapping("/putStorage")
	@ResponseBody
	public ExtMsg putStorage(String[] ids){
		boolean state = digitalProcessService.putStorage(ids);
		return new ExtMsg(state,"",null);
	}

	/**
	 * 获取办理详情
	 * @param id 批次条目id
	 * @param page 页码
	 * @param limit 每页数
	 * @param condition 查询字段
	 * @param operator  条件
	 * @param content 查询字段值
	 * @return
	 */
	@RequestMapping("/getDealDetails")
	@ResponseBody
	public Page<Szh_flows_record> getDealDetails(String id, int page, int limit, String condition, String operator, String content){
		return digitalProcessService.getDealDetails(id,page,limit,condition,operator,content);
	}

	@RequestMapping(value = "/entries", method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg saveEntry(@ModelAttribute("form") SzhEntryCapture entry, String type, String nodeid) {
		entry.setEntryIndex(entry.getRawEntryIndex());
		entry.setEntryDetial(entry.getRawEntryDetail());
		entry.setNodeid(nodeid);
		Tb_data_node node = entryIndexService.getNodeLevel(entry.getNodeid());
		String code = entry.getArchivecode();
		if (!code.isEmpty()) {// 如果档号不为空
			// 查询当前节点所有数据的档号,判断档号的唯一性
			List<String> archivecode = entryIndexCaptureRepository.findCodeByNodeid(entry.getNodeid());
			if (archivecode.size() > 0) {
				if (type.equals("modify")) {
					Szh_entry_index_capture entryIndex = entryIndexCaptureRepository.findByEntryid(entry.getEntryid());
					// 如果修改了档号
					if (entryIndex.getArchivecode() != null && !code.equals(entryIndex.getArchivecode())
							&& isExist(code, archivecode)) {
						return new ExtMsg(false, "保存失败，档号重复！", null);
					}
				}
			}
		} else {
			// 如果档号为空,且非未归管理
			if (!node.getNodename().equals("未归管理") && node.getNodename().equals("文件管理") && node.getNodename().equals("资料管理")) {
				return new ExtMsg(false, "保存失败，档号为空", null);
			}
		}
		SzhEntryCapture result = digitalProcessService.saveEntry(entry, type);
		return new ExtMsg(result != null ? true : false, result != null ? "保存成功" : "保存失败", result);
	}

	// 判断档号是否存在
	private boolean isExist(String entryCode, List<String> archivecode) {
		for (int i = 0; i < archivecode.size(); i++) {
			String code = archivecode.get(i);
			// 如果档号存在(传过来的档号在节点当中已经存在)
			if (code != null && entryCode.equals(code)) {
				return true;
			}
		}
		return false;
	}

	@RequestMapping("/getNodeid")
	@ResponseBody
	public String getNodeid(String calloutId){
		return digitalProcessService.getNodeid(calloutId);
	}

	@RequestMapping("/getEntryid")
	@ResponseBody
	public String getEntryid(String calloutId){
		return digitalProcessService.getEntryid(calloutId);
	}

	/**
	 * 获取调出条目
	 * @param entryid 条目id
	 * @return
	 */
	@RequestMapping("/getEntryIndex")
	@ResponseBody
	public SzhEntryCapture getEntryIndex(String entryid){
		return digitalProcessService.getEntryIndex(entryid);
	}

	/**
	 * 获取合并后的表单内容
	 * @param nodeid    节点
	 * @param calloutId 调出单据id
	 * @param userId 用户id(用于指定合并蓝本)
	 * @return
	 */
	@RequestMapping("/mergeEntry")
	@ResponseBody
	public SzhEntryCapture mergeEntry(String nodeid,String calloutId,String userId){
		return digitalProcessService.mergeEntry(nodeid,calloutId,userId);
	}

	/**
	 * 获取节点与条目id
	 * @param calloutId
	 * @return
	 */
	@RequestMapping("/getEntryidNodeid")
	@ResponseBody
	public ExtMsg getEntryidNodeid(String calloutId){
		List<String> list = digitalProcessService.getEntryidNodeid(calloutId);
		if(list.size()==1){
			return new ExtMsg(false,"已入库",null);
		}else{
			return new ExtMsg(true,"",list);
		}
	}

	/**
	 * 获取表头(根据对比差异字段获取)
	 * @param nodeid     节点
	 * @param calloutId  调出单据id
	 * @return
	 */
	@RequestMapping("/gridHeader")
	@ResponseBody
	public List gridHeader(String nodeid,String calloutId) {
		return digitalProcessService.gridHeader(nodeid,calloutId);
	}


	/**
	 * 获取同档号条目
	 * @param nodeid     节点
	 * @param calloutId  调出单据id
	 * @param condition  查询字段
	 * @param operator   查询条件
	 * @param content    查询内容
	 * @param page       页码
	 * @param limit      分页数
	 * @return
	 */
	@RequestMapping("/szhEntries")
	@ResponseBody
	public Page<SzhEntryCapture> szhEntries(String nodeid,String calloutId, String condition,String operator, String content,int page, int limit) {
		return digitalProcessService.szhEntries(nodeid,calloutId,condition,operator,content,page,limit);
	}

	/**
	 * 获取条目录入人
	 * @param nodeid     节点
	 * @param calloutId  调出单据id
	 * @return
	 */
	@RequestMapping("/getOperateUsers")
	@ResponseBody
	public List getOperateUsers(String nodeid,String calloutId) {
		return digitalProcessService.getOperateUsers(nodeid,calloutId);
	}

	/**
	 * 保存合并后的数据
	 * @param entry   表单数据
	 * @param type    操作类型
	 * @param nodeid  节点
	 * @return
	 */
	@RequestMapping(value = "/mergeEntries", method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg mergeEntries(@ModelAttribute("form") SzhEntryCapture entry, String type, String nodeid) {
		boolean isRk = digitalProcessService.hasEntryCapture(entry.getArchivecode(),nodeid);//判断是否已经入库
		ExtMsg result;
		if(!isRk){
			result = saveEntry(entry,type,nodeid);
			if(result.isSuccess()&&"保存成功".equals(result.getMsg())){
				entry = (SzhEntryCapture)result.getData();
				boolean state = digitalProcessService.mergeEntries(entry,nodeid);
				if(state){
					result.setMsg("整合成功!");
				}else{
					result.setMsg("整合失败!");
				}
			}
		}else{
			result = new ExtMsg(true,"已经入库",null);
		}
		return result;
	}

	@RequestMapping("/toShowMedia")
	public String szhMedia(Model model,String eleid,String filetype){
		model.addAttribute("filetype", filetype);
		model.addAttribute("imgsrc", "/digitalProcess/showMedia?&eleid=" + eleid+"&filetype="+filetype);
		return "/inlet/media";
	}

	@RequestMapping("/showMedia")
	public void loadJyMedia(HttpServletRequest request, HttpServletResponse response, String eleid)
			throws Exception {
		Map<String, Object> map = digitalProcessService.findSzhElectronic(eleid);
		String file_name = (String) map.get("filename");
		String file_type = (String) map.get("filetype");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + ElectronicController.getOutName(request, file_name) + "\"");
		response.setContentType("application/" + file_type);
		ServletOutputStream out;
		String mediaPath = rootpath + map.get("filepath") + "/" + file_name;
		File html_file = new File(mediaPath);
		FileInputStream inputStream = new FileInputStream(html_file);
		out = response.getOutputStream();
		int b = 0;
		byte[] buffer = new byte[1024];
		while ((b = inputStream.read(buffer)) != -1) {
			out.write(buffer, 0, b);
		}
		inputStream.close();
		out.flush();
		out.close();
	}

	/**
	 * 卷内打印：1、根据页次分组排序
	 * @return
	 */
	@RequestMapping("/reportData")
	@ResponseBody
	public void reportData(String[] entryids){
		digitalProcessService.delectData(); //删除数据
		digitalProcessService.reportData(entryids);
	}

	/*
     条目日志相关信息
 */
	@RequestMapping("/getSzhFlowsRecordMessage")
	@ResponseBody
	public List getSzhFlowsRecordMessage(String calloutId){
		return digitalProcessService.findSzhFlowsRecordMessage(calloutId);
	}


	//查看电子文件
	@RequestMapping("/szhShowMedia")
	public void szhShowMedia(HttpServletRequest request, HttpServletResponse response, String eleid)
			throws Exception {
		Szh_electronic_capture electronic = digitalProcessService.getElectronic(eleid);
		String file_name = electronic.getFilename();
		String file_type = electronic.getFiletype();
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + getOutName(request, file_name) + "\"");
		response.setContentType("application/" + file_type);
		ServletOutputStream out;
		String mediaPath = rootpath + electronic.getFilepath() + "/" + file_name;
		File html_file = new File(mediaPath);
		FileInputStream inputStream = new FileInputStream(html_file);
		out = response.getOutputStream();
		int b = 0;
		byte[] buffer = new byte[1024];
		while ((b = inputStream.read(buffer)) != -1) {
			out.write(buffer, 0, b);
		}
		inputStream.close();
		out.flush();
		out.close();
	}

	public static String getOutName(HttpServletRequest request, String name) throws IOException {
		String outName = "";
		String agent = request.getHeader("User-Agent");
		if (null != agent && -1 != agent.indexOf("MSIE")) {
			outName = URLEncoder.encode(name, "UTF8");
		} else if (null != agent && -1 != agent.indexOf("Mozilla")) {
			outName = MimeUtility.encodeText(name, "UTF8", "B");
		}
		return outName;
	}

	@RequestMapping("/getCalloutid")
	@ResponseBody
	public String getCalloutid(String entryId){
		return digitalProcessService.getCalloutid(entryId);
	}

	/**
	 * 获取环节信息
	 * @param assemblyid 流水线号
	 * @return
	 */
	@RequestMapping("/getLinkByassembly")
	@ResponseBody
	public List<Szh_assembly_flows> getLinkByassembly(String assemblyid){
		List<Szh_assembly_flows> flows = digitalProcessService.getLinkByassembly(assemblyid);
		for(int i=0;i<flows.size();i++){
			if("审核".equals(flows.get(i).getNodename())||"完成环节".equals(flows.get(i).getNodename())||"装订".equals(flows.get(i).getNodename()) ){
				flows.remove(i);
				i=i-1;
			}
		}
		return flows;
	}

	/**
	 * 获取关联环节
	 * @param assemblyid 流水线id
	 * @param linkId 环节id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getRelevancyLinks")
	@ResponseBody
	public ExtMsg getRelevancyLinks(String assemblyid,String linkId) {
		return new ExtMsg(true,null,digitalProcessService.getRelevancyLinks(assemblyid,linkId));
	}

	/**
	 * 退回其他环节
	 * @param relateLink 关联环节
	 * @param linkName 环节名
	 * @param assemblyid 流水线id
	 * @param ids 调出条目id
	 * @param linkBack 退回信息
	 * @param backText 哪个环节退回（审核/完成）
	 * @return
	 */
	@RequestMapping("/linkback")
	@ResponseBody
	public ExtMsg linkback(String relateLink,String linkName,String assemblyid,String ids[],Szh_link_back linkBack,String backText){
		boolean state = false;
		try {
			state = digitalProcessService.linkback(relateLink,linkName,assemblyid,ids,linkBack,backText);
		}catch (Exception e){
			e.printStackTrace();
		}
		return new ExtMsg(state,"",null);
	}

	/**
	 * 审核获取调档条目
	 * @param ids 调出文件ids
	 * @return
	 */
	@RequestMapping("/getcalloutEntrys")
	@ResponseBody
	public List<Szh_callout_entry> getcalloutEntrys(String[] ids){
		return digitalProcessService.getcalloutEntrys(ids);
	}

	//获取审核条目原文
	@RequestMapping("/getSzhAuditEle")
	@ResponseBody
	public List getSzhAuditEle(String entryid){
		List<Szh_electronic_capture> szhEles = digitalProcessService.getSzhEleCaptures(entryid);
		String[] mediaArr = new String[szhEles.size()];
		List<BackSzhEle> backSzhEles = new ArrayList<>();
		Map<String,String> mediaIdStatusMap = new HashMap<>();
		for(int i=0;i<szhEles.size();i++){
			Szh_electronic_capture eleCapture =  szhEles.get(i);
			mediaArr[i] = eleCapture.getEleid();
		}

		List<Szh_audit_status> auditStatus = digitalProcessService.getAuditStatuss(mediaArr);
		for(Szh_audit_status as:auditStatus){
			mediaIdStatusMap.put(as.getMediaid(),as.getStatus());
		}
		//返回审核列表
		for(int i=0;i<szhEles.size();i++){
			Szh_electronic_capture eleCapture =  szhEles.get(i);
			BackSzhEle shzEle = new BackSzhEle();
			shzEle.setId(eleCapture.getEleid());
			shzEle.setFilename(eleCapture.getFilename());
			String status = mediaIdStatusMap.get(eleCapture.getEleid());
			if(status!=null){  //判断是否审核
				shzEle.setStatus(status);
			}else{
				shzEle.setStatus("未检");
			}
			backSzhEles.add(shzEle);
		}
		return backSzhEles;
	}
}