package com.wisdom.web.controller;

import com.wisdom.web.entity.*;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.DigitalInspectionService;
import com.wisdom.web.service.ElectronicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 质检验收
 */
@Controller
@RequestMapping(value = "/inspectionAccept")
public class InspectionAcceptController {

	@Value("${system.document.rootpath}")
	private String rootpath;// 系统文件根目录

	@Autowired
    ElectronicService electronicService;

	@Autowired
	private DigitalInspectionService digitalInspectionService;


	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping("/main")
	public String main(Model model, String isp) {
		return "/inlet/inspectionAccept";
	}

	/**
	 * 分页获取质检批次单据信息
	 * @param status 单据状态
	 * @param page 页码
	 * @param limit 每页数
	 * @param condition 查询字段
	 * @param operator  条件
	 * @param content 查询字段值
	 * @return
	 */
	@RequestMapping("/getBatchBillBySearch")
	@ResponseBody
	public Page<Szh_batch_bill> getBatchBillBySearch(String status, int page, int limit, String sort,String condition, String operator, String content){
		if("未抽检".equals(status)){
			return digitalInspectionService.getBatchBillBySearch("验收",status,page,limit,sort,condition,operator,content);
		}else{
			return digitalInspectionService.getBatchBillBySearchCheckUser("验收",status,page,limit,sort,condition,operator,content);
		}
	}

	/**
	 * 分页获取质检批次条目信息
	 * @param batchcode 批次号
	 * @param isCheck 是否选中抽检
	 * @param page 页码
	 * @param limit 每页数
	 * @param condition 查询字段
	 * @param operator  条件
	 * @param content 查询字段值
	 * @return
	 */
	@RequestMapping("/getBatchEntryBySearch")
	@ResponseBody
	public Page<Szh_batch_entry> getBatchEntryBySearch(String notMe, String batchcode, String isCheck, String flag, int page, int limit, String condition, String operator, String content){
		if("未抽检".equals(flag)||"否".equals(isCheck)){
			return digitalInspectionService.getBatchEntryBySearch(batchcode,isCheck,page,limit,condition,operator,content);
		}else {
			return digitalInspectionService.getBatchEntryBySearchCheckUser(notMe,batchcode,isCheck,page,limit,condition,operator,content);
		}
	}

	/**
	 * 分页获取完成抽检质检批次条目信息
	 * @param batchcode 批次号
	 * @param status 状态
	 * @param page 页码
	 * @param limit 每页数
	 * @param condition 查询字段
	 * @param operator  条件
	 * @param content 查询字段值
	 * @return
	 */
	@RequestMapping("/getWcBatchEntryBySearch")
	@ResponseBody
	public Page<Szh_batch_entry> getWcBatchEntryBySearch(String batchcode, String status, int page, int limit, String condition, String operator, String content){
		return digitalInspectionService.getWcBatchEntryBySearch(batchcode,status,page,limit,condition,operator,content);
	}

	/**
	 * 获取批次号以及抽检人
	 * @return
	 */
	@RequestMapping("/getBatchAddForm")
	@ResponseBody
	public ExtMsg getBatchAddForm(){
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 系统绑定对象(全局)
		String code = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());//获取时间
		String random = (int)(Math.random()*(9999-1000+1))+1000+"";//获取4位随机数
		Szh_batch_bill bill = new Szh_batch_bill();
		bill.setBatchcode(code+random);								//设置批次号
		bill.setInspector(userDetails.getRealname());				//设置抽检人
		return  new ExtMsg(true,"成功",bill);
	}

	/**
	 *
	 * @param archivesType 档案类型
	 * @param page 页码
	 * @param limit 每页数
	 * @param condition 查询字段
	 * @param operator  条件
	 * @param content 查询字段值
	 * @param sort 排序
	 * @return
	 */
	@RequestMapping("/findByCaptureSearch")
	@ResponseBody
	public Page<Tb_entry_index_capture> findByCaptureSearch(String archivesType, int page, int limit, String condition, String operator, String content, String sort) {
		Sort sortobj = WebSort.getSortByJson(sort);
		return digitalInspectionService.findByCaptureSearch("验收",archivesType,page, limit, condition, operator, content, sortobj);
	}

	/**
	 * 添加批次表单提交
	 * @param bill 表单对象
	 * @return
	 */
	@RequestMapping("/batchAddFormSubmit")
	@ResponseBody
	public ExtMsg batchAddFormSubmit(Szh_batch_bill bill) {
		boolean status = digitalInspectionService.batchAddFormSubmit(bill,"验收");
		return new ExtMsg(status,null,null);
	}

	/**
	 * 删除批次
	 * @param batchcodes 批次号数组
	 * @return
	 */
	@RequestMapping("/batchDel")
	@ResponseBody
	public ExtMsg batchDel(String[] batchcodes) {
		boolean status = digitalInspectionService.batchDel(batchcodes);
		return new ExtMsg(status,"删除成功","");
	}

	/**
	 * 设置抽检率
	 * @param batchcodes 批次号数组
	 * @param checkcount 抽检率
	 * @param samplingtype 抽检类型(保留)
	 * @return
	 */
	@RequestMapping("/samplingSubmit")
	@ResponseBody
	public ExtMsg samplingSubmit(String[] batchcodes, String checkcount, String samplingtype, String checkgroupid) {
		boolean status = digitalInspectionService.samplingSubmit(batchcodes,checkcount,samplingtype,checkgroupid);
		return new ExtMsg(status,"","");
	}

	/**
	 * 退回批次
	 * @param batchcodes 批次号数组
	 * @return
	 */
	@RequestMapping("/cancelSampling")
	@ResponseBody
	public ExtMsg cancelSampling(String[] batchcodes) {
		boolean status = digitalInspectionService.cancelSampling(batchcodes);
		return new ExtMsg(status,"操作完成","");
	}

	/**
	 * 获取批次数据
	 * @param batchcode 批次号
	 * @return
	 */
	@RequestMapping("/getBill")
	@ResponseBody
	public ExtMsg getBill(String batchcode){
		Szh_batch_bill bill = digitalInspectionService.getBill(batchcode);
		return  new ExtMsg(true,"成功",bill);
	}

	@RequestMapping("/getEntryMedias")
	@ResponseBody
	public List<Szh_batch_media> getEntryMedias(String batchcode, String entryid) {
		return digitalInspectionService.getEntryMedias(batchcode,entryid );
	}

	/**
	 * 获取批次数据
	 * @param entryid 条目id
	 * @return
	 */
	@RequestMapping("/getFormEntry")
	@ResponseBody
	public ExtMsg getFormEntry(String entryid){
		EntryBase capture = digitalInspectionService.getFormEntry(entryid);
		return  new ExtMsg(true,"成功",capture);
	}

	/**
	 * 获取图片错误信息
	 * @param batchcode 批次编号
	 * @param mediaid 原文id
	 * @return
	 */
	@RequestMapping("/getMediaErrors")
	@ResponseBody
	public List<Szh_batch_err> getMediaErrors(String batchcode, String mediaid){
		return  digitalInspectionService.getMediaErrors(batchcode,mediaid);
	}

	/**
	 * 新增原文错误信息
	 * @param err 表单数据
	 * @return
	 */
	@RequestMapping("/errSubmit")
	@ResponseBody
	public ExtMsg errSubmit(Szh_batch_err err){
		boolean state = digitalInspectionService.errSubmit(err);
		return  new ExtMsg(state,state?"操作成功":"操作失败",null);
	}

	/**
	 * 删除原文错误信息
	 * @param errids 错误信息id数组
	 * @return
	 */
	@RequestMapping("/delMediaErrs")
	@ResponseBody
	public ExtMsg delMediaErrs(String[] errids){
		int delCount = digitalInspectionService.delMediaErrs(errids);
		return  new ExtMsg(true,"删除"+delCount+"条数据",null);
	}

	/**
	 * 修复原文错误信息
	 * @param errids 错误信息id数组
	 * @return
	 */
	@RequestMapping("/errRepair")
	@ResponseBody
	public ExtMsg errRepair(String[] errids){
		boolean state = digitalInspectionService.errRepair(errids);
		return  new ExtMsg(state,"",null);
	}

	/**
	 * 原文质检通过
	 * @param id 主键
	 * @return
	 */
	@RequestMapping("/changeMediaStatus")
	@ResponseBody
	public ExtMsg changeMediaStatus(String id){
		boolean state = digitalInspectionService.changeMediaStatus(id);
		return  new ExtMsg(state,"",null);
	}

	/**
	 * 退档(当全部条目状态不为"未检查"时,整个批次状态改为"完成抽检")
	 * @param id 主键
	 * @param batchcode 批次号
	 * @return
	 */
	@RequestMapping("/exitda")
	@ResponseBody
	public ExtMsg exitda(String id, String batchcode){
		String result = digitalInspectionService.exitda(id,batchcode);
		boolean state = "1".equals(result)||"2".equals(result)?true:false;
		return  new ExtMsg(state,result,null);
	}

	/**
	 * 通过(当全部条目状态不为"未检查"时,整个批次状态改为"完成抽检")
	 * @param id 主键
	 * @param batchcode 批次号
	 * @return
	 */
	@RequestMapping("/passEntry")
	@ResponseBody
	public ExtMsg passEntry(String id, String batchcode){
		String result = digitalInspectionService.passEntry(id,batchcode);
		boolean state = "1".equals(result)||"2".equals(result)?true:false;
		return  new ExtMsg(state,result,null);
	}

	/**
	 * 错误报告导出
	 * @param batchcode 批次号
	 * @param response 响应对象
	 * @return
	 */
	@RequestMapping("/exportErrReport")
	@ResponseBody
	public void exportErrReport(String batchcode, HttpServletResponse response){
		digitalInspectionService.exportErrReport(batchcode,response);
	}

	/**
	 * 更新批次条目信息
	 * @param batchcode 批次号
	 * @param entryId 条目id
	 * @return
	 */
	@RequestMapping("/changeBatchEntry")
	@ResponseBody
	public ExtMsg changeBatchEntry(String batchcode, String entryId){
		boolean state = digitalInspectionService.changeBatchEntry(batchcode,entryId);
		return new ExtMsg(state,"",null);
	}

	/**
	 *获取流水线数据
	 * @return
	 */
	@RequestMapping("/getAssemblys")
	@ResponseBody
	public List<Szh_assembly> getAssemblys(){
		return digitalInspectionService.getAssemblys();
	}


	/**
	 * 获取元数据数据
	 * @param mediaid 原文id
	 * @return
	 */
	@RequestMapping("/getMetadata")
	@ResponseBody
	public ExtMsg getMetadata(String mediaid){
		Szh_media_metadata metadata = digitalInspectionService.getMetadata(mediaid);
		return  new ExtMsg(true,"成功",metadata);
	}


	/**
	 * 修改元数据
	 * @param metadata 元数据
	 * @return
	 */
	@RequestMapping("/metadataSubmit")
	@ResponseBody
	public ExtMsg metadataSubmit(Szh_media_metadata metadata){
		boolean state = digitalInspectionService.metadataSubmit(metadata);
		return  new ExtMsg(state,"",null);
	}

	/**
	 * 修改元数据
	 * @param batchcodes 批次号数组
	 * @return
	 */
	@RequestMapping("/acceptSubmit")
	@ResponseBody
	public ExtMsg acceptSubmit(String[] batchcodes){
		boolean state = digitalInspectionService.acceptSubmit(batchcodes);
		return  new ExtMsg(state,"",null);
	}

	/**
	 * 成果导出
	 * @param fileName 文件名
	 * @param zipPassword 压缩包密码
	 * @param batchcodes  批次号数组
	 * @return
	 */
	@RequestMapping("/chooseFieldExport")
	@ResponseBody
	public synchronized ExtMsg chooseFieldExport(String fileName, String zipPassword, String[] batchcodes) {
		String zipPath = digitalInspectionService.chooseFieldExport(fileName,zipPassword,batchcodes);
		return new ExtMsg(zipPath!=null?true:false, "message", zipPath);
	}

	@RequestMapping("/showMedia")
	public void loadJyMedia(HttpServletRequest request, HttpServletResponse response, String eleid)
			throws Exception {
		Map<String, Object> map = electronicService.findElectronic("capture", eleid);
		String file_name = (String) map.get("filename");
		String file_type = (String) map.get("filetype");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + getOutName(request, file_name) + "\"");
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
}