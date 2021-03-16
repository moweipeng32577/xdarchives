package com.wisdom.web.controller;

import com.wisdom.secondaryDataSource.entity.Tb_report_sx;
import com.wisdom.secondaryDataSource.repository.SxReportRepository;
import com.wisdom.util.LogAnnotation;
import com.wisdom.util.LogAop;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.ElectronicRepository;
import com.wisdom.web.repository.ReportRepository;
import com.wisdom.web.service.ElectronicService;
import com.wisdom.web.service.NodesettingService;
import com.wisdom.web.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 报表管理控制器 Created by RonJiang on 2018/2/27 0027.
 */
@Controller
@RequestMapping(value = "/report")
public class ReportController {

	@Autowired
	LogAop logAop;

	@Autowired
	ReportService reportService;

	@Autowired
	NodesettingService nodesettingService;

	@Autowired
	ElectronicController electronicController;

	@Autowired
	ElectronicService electronicService;

	@Autowired
	ReportRepository reportRepository;

	@Autowired
	SxReportRepository sxReportRepository;

    @Autowired
    ElectronicRepository electronicRepository;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Value("${system.document.rootpath}")
	private String rootpath;// 系统文件根目录
	@Value("${system.document.reportserver}")
	private String reportserver;// 报表服务地址

	@Value("${find.sx.data}")
	private Boolean openSxData;//是否可检索声像系统的声像数据

	private static final String PUBLIC_REPORT_FNID = "publicreportfnid";

	@RequestMapping("/main")
	public String index(Model model) {
		model.addAttribute("openSxData",openSxData);
		return "/inlet/report";
	}

	//档案分类数量统计表
//	@RequestMapping("/classifyTotal")
//	public String fileClassify() {
//		return "/inlet/classifyTotal";
//	}

	//电子查档档案统计表
	@RequestMapping("/classifyeleTotal")
	public String eleClassify() {
		return "/inlet/classifyeleTotal";
	}

	//实体查档档案统计表
	@RequestMapping("/classifyentryTotal")
	public String entryClassify() {
		return "/inlet/classifyentryTotal";
	}

	/**
	 * 本模块独立的获取数据节点方法
	 * 
	 * @param pcid
	 * @return
	 */
	@RequestMapping("/getReportDatanode")
	@ResponseBody
	public List<NodesettingTree> getReportDatanode(String pcid,String xtType) {
		List<NodesettingTree> nodeTrees = nodesettingService.getNodeByParentId(pcid, true,xtType);
		List<NodesettingTree> returnNodeTrees = new ArrayList<>();
		returnNodeTrees.addAll(nodeTrees);
		if ("".equals(pcid)) {// 仅最外层级别的节点加载时，追加一个公共报表节点
			NodesettingTree publicReportNode = new NodesettingTree();
			publicReportNode.setText("公共报表");
			publicReportNode.setLeaf(true);
			publicReportNode.setFnid(PUBLIC_REPORT_FNID);
			publicReportNode.setCls("file");
			publicReportNode.setRoottype("classification");
			publicReportNode.setChildren(null);
			returnNodeTrees.add(publicReportNode);
		}
		return returnNodeTrees;
	}

	/**
	 * 获取相应节点的报表
	 * 
	 * @param nodeid
	 *            所选数据节点的节点id（若在数据采集和数据管理界面调用，则另外包含“公共报表”节点id）
	 * @param flag
	 *            是否显示所有报表
	 */
	@RequestMapping("/getNodeReport")
	@ResponseBody
	public Page getNodeReport(String nodeid, String flag, int page, int start, int limit, String condition,
			String operator, String content, String sort,String xtType) {
		Sort sortobj = WebSort.getSortByJson(sort);
		nodeid = nodeid == null ? "" : nodeid;
		String[] nodeids = nodeid.split(",");
		if("声像系统".equals(xtType)){
			Page<Tb_report_sx> list = null;
			if (condition != null) {
				list = reportService.findSxBySearch(page, limit, condition, operator, content, nodeids, sortobj);
			} else {
				if ("all".equals(flag)) {
					list = reportService.getSxAllReport(page, limit, sortobj, nodeids);
				} else {
					list = reportService.getSxNodeReport(nodeids, page, limit, sortobj);
				}
			}
			return list;
		}else{
			Page<Tb_report> list = null;
            if ("all".equals(flag)) {
                list = reportService.getAllReport(page, limit, sortobj, nodeids,condition, operator, content);
            } else {
                list = reportService.getNodeReport(nodeids, page, limit, sortobj,condition, operator, content);
            }
			return list;
		}

	}

	/**
	 * 根据报表id获取报表对象
	 * 
	 * @param reportid
	 * @return
	 */
	@RequestMapping(value = "/reports/{reportid}", method = RequestMethod.GET)
	@ResponseBody
	public Tb_report getReport(@PathVariable String reportid,String xtType) {
		if("声像系统".equals(xtType)){
			Tb_report_sx report_sx= reportService.getSxReport(reportid);
			Tb_report report=new Tb_report();
			BeanUtils.copyProperties(report_sx,report);
			return report;
		}else{
			return reportService.getReport(reportid);
		}
	}

	/**
	 * 获取报表样式管理中的文件列表（每个报表只允许存在至多一个样式文件）
	 * 
	 * @param reportid
	 *            报表id
	 * @return
	 */
	@RequestMapping(value = "/reports/tree")
	@ResponseBody
	public List<ExtNcTree> findReportTree( String reportid, String xtType, String eleid) {
        List<ExtNcTree> list = new ArrayList<>();
        if(eleid !=null && !"".equals(eleid)){
            Tb_electronic electronic = electronicRepository.findByEleid(eleid);
            ExtNcTree node = new ExtNcTree();
            node.setFnid(electronic.getEleid());
            node.setText(electronic.getFilename());
            node.setLeaf(true);
            list.add(node);
            return list;
        }
		if (reportid == null || "".equals(reportid)) {
			return null;
		}
		Tb_report report =new Tb_report();
		if("声像系统".equals(xtType)){
			Tb_report_sx report_sx= reportService.getSxReport(reportid);
			BeanUtils.copyProperties(report_sx,report);
		}else{
			report = reportService.getReport(reportid);
		}
		if (report.getFilename() == null || "".equals(report.getFilename())) {// 报表记录中filename属性为空则表明该报表无对应样式文件上传
			return null;
		}
		ExtNcTree node = new ExtNcTree();
		node.setFnid(report.getReportid());
		node.setText(report.getFilename());
		node.setLeaf(true);
		list.add(node);
		return list;
	}

	/**
	 * 上传报表样式文件（实际内部逻辑为——设置filename属性，filename属性不为空，则页面可成功读取样式文件列表）
	 * 
	 * @param reportid
	 * @param filename
	 * @return
	 */
	@RequestMapping(value = "/uploadReport/{reportid}/{filename}", method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg uploadReport(@PathVariable String reportid, @PathVariable String filename) {
    		Map<String, Object> map = reportService.uploadReport(reportid, filename);
		ExtMsg msg = new ExtMsg(true, "", map);
		return msg;
	}

	/**
	 * 报表样式管理上传(报表样式物理文件上传)
	 * 
	 * @param request
	 *            请求对象
	 * @throws Exception
	 */
	@RequestMapping(value = "/electronicsReport", method = RequestMethod.POST)
	@ResponseBody
	public void electronicsReport(HttpServletRequest request) throws Exception {
		Map<String, Object> params = electronicController.parse(request);
		if ((boolean) params.get("mutipart")) {
			if (params.get("chunk") != null) { // 文件分片上传
				electronicService.uploadchunkReport(params);
			} else { // 文件单片上传
				electronicService.uploadfileReport(params);
			}
		}
	}

	/**
	 * 保存报表
	 * 
	 * @param report
	 * @param realnodename
	 * @return
	 */
	@LogAnnotation(module = "报表管理", sites = "1", fields = "reportname,modul", connect = "##报表名称；,##节点名称；", startDesc = "保存操作，报表详情：")
	@RequestMapping(value = "/reports", method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg saveReport(@ModelAttribute("form") Tb_report report, String realnodename,String xtType,String eleid) {
		report.setNodename(realnodename);
		report.setModul(realnodename);
		Tb_report result=new Tb_report();
		if("声像系统".equals(xtType)){
//			Tb_report_sx reportData = sxReportRepository.findByReportid(report.getReportid());
//			if(reportData != null){
//				if( reportData.getFilename() != null || "".equals(reportData.getFilename())) {
//					report.setFilename(reportData.getFilename());  //修改报表时候，假如改报表样式文件已存在则不改变其样式文件
//				}
//			}
			Tb_report_sx sxResult = reportService.saveSxReport(report);
			BeanUtils.copyProperties(sxResult,result);
		}else{
//			Tb_report reportData = reportRepository.findByReportid(report.getReportid());
//			if(reportData != null){
//				if( reportData.getFilename() != null || "".equals(reportData.getFilename())) {
//					report.setFilename(reportData.getFilename());  //修改报表时候，假如改报表样式文件已存在则不改变其样式文件
//				}
//			}
			result = reportService.saveReport(report);
		}

		if (result != null) {
            if(eleid!=null && !"".equals(eleid)){
                Tb_electronic electronic = electronicRepository.findByEleid(eleid);
                if(electronic!=null){
                    electronic.setEntryid(result.getReportid());
                    electronicRepository.save(electronic);
                }
            }
			return new ExtMsg(true, "保存成功", result);
		}
		return new ExtMsg(false, "保存失败", null);
	}

	/**
	 * 删除报表（包括删除报表样式文件）
	 * 
	 * @param reportids
	 * @return
	 */
	// @LogAnnotation(module = "报表管理",startDesc = "删除报表记录操作，报表id为：",sites = "1")
	@RequestMapping(value = "/reports/{reportids}", method = RequestMethod.DELETE)
	@ResponseBody
	public ExtMsg delReport(@PathVariable String reportids,String xtType) {
		String startTime = LogAop.getCurrentSystemTime();// 开始时间
		long startMillis = System.currentTimeMillis();// 开始毫秒数
		String[] reportidData = reportids.split(",");
		Integer del=0;
		if("声像系统".equals(xtType)){
			del = reportService.delSxReport(reportidData);
		}else{
			del = reportService.delReport(reportidData);
		}
		for (String reportid : reportidData) {
			logAop.generateManualLog(startTime, LogAop.getCurrentSystemTime(), System.currentTimeMillis() - startMillis,
					"报表管理", "删除报表记录操作，报表id为：" + reportid);
		}
		if (del > 0) {
			return new ExtMsg(true, "删除成功", del);
		}
		return new ExtMsg(false, "删除失败", null);
	}

	/**
	 * 删除报表样式文件
	 * 
	 * @param reportid
	 * @return
	 */
	@LogAnnotation(module = "报表管理", startDesc = "删除报表文件操作，报表id为：", sites = "1")
	@RequestMapping(value = "/deleteRepElectronic/{reportid}", method = RequestMethod.DELETE)
	@ResponseBody
	public ExtMsg deleteRepElectronic(@PathVariable String reportid) {
		Integer num = reportService.deleteRepElectronic(reportid);
		if (num > 0) {
			return new ExtMsg(true, "删除成功", reportid);
		}
		return new ExtMsg(false, "删除失败", null);
	}

	/**
	 * 下载报表样式文件时，根据报表ID判断文件是否存在于相应目录中，若不存在，给出相应提示
	 * 
	 * @param reportid
	 * @param isNotReportTemplate 表示模板信息无需存在数据库，直接打印去核对有没有对应的模板文件即可。
	 * @param fileName  由于isReportTemplate等于false时表示无需存在数据库，那么直接从前端把模板文件名传过来。
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/ifFileExist/{reportid}", method = RequestMethod.GET)
	@ResponseBody
	public ExtMsg ifFileExist(@PathVariable String reportid,boolean isNotReportTemplate,String fileName) throws Exception {
		String dir;
		if(isNotReportTemplate == true){
			dir = reportService.getReportStorageDir(fileName);
		}else {
			Map<String, Object> map  = reportService.findReport(reportid);
			String	filename =  (String) map.get("filename");
			dir = reportService.getReportStorageDir(filename);
		}
		File file = new File(dir);
		if (file.exists()) {
			return new ExtMsg(true, "", null);
		} else {
			return new ExtMsg(false, dir + "（ 报表样式文件不存在！）", null);
		}
	}

	/**
	 * 根据上传列表中的待上传文件名，判断文件是否已存在，若已存在，则禁止重复上传
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/ifFileExistAt/{filename}", method = RequestMethod.GET)
	@ResponseBody
	public ExtMsg ifFileExistAt(@PathVariable String filename) throws Exception {
		Integer count = reportRepository.findCountByFilename(filename);
		if (count > 0) {
			String reportName = reportRepository.findReportnameByFilename(filename);
			String dual = reportRepository.findModulByFilename(filename);
			String msg = "文件已存在，该文件对应的报表所处节点为：" + dual + ",报表名称为：" + reportName + ",不能重复上传！";
			return new ExtMsg(true, msg, null);
		} else {
			return new ExtMsg(false, "", null);
		}
	}

	/**
	 * 报表样式文件下载
	 * 
	 * @reportid 需要下载文件的reportid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/reports/download/{reportid}", method = RequestMethod.GET)
	public void downloadWzFile(@PathVariable String reportid, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String, Object> map = reportService.findReport(reportid);
		String filename =  (String) map.get("filename");
		String dir = reportService.getReportStorageDir(filename);
		downLoad(response, dir);
	}

	public void downLoad(HttpServletResponse response, String dir) {
		try {
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ new String(dir.substring(dir.lastIndexOf("/") + 1).getBytes("gbk"), "iso8859-1") + "\"");
			response.setContentType("application/cpt");
			ServletOutputStream out;
			File html_file = new File(dir);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调用报表打印html
	 * 
	 * @param reportlet
	 *            json格式
	 * @return
	 */
	@RequestMapping(value = "/finereport", method = RequestMethod.POST)
	public String getReport(Model model, String reportlet, HttpServletRequest request) {
		if (reportserver != null && !"".equals(reportserver)) {
			model.addAttribute("reportserver", reportserver);
		} else {
			String rooturl = "http://" + request.getServerName() + ":" + request.getServerPort();
			model.addAttribute("reportserver", rooturl);
		}
		model.addAttribute("reportlet", reportlet);
		return "/inlet/fineReport";
	}

	@RequestMapping(value = "/finereportly", method = RequestMethod.POST)
	public String getReportly(Model model, String reportlet, HttpServletRequest request) {
		if (reportserver != null && !"".equals(reportserver)) {
			model.addAttribute("reportserver", reportserver);
		} else {
			String rooturl = "http://" + request.getServerName() + ":" + request.getServerPort();
			model.addAttribute("reportserver", rooturl);
		}
		model.addAttribute("reportlet", reportlet);
		return "/inlet/fineReport";
	}

	/**
	 * 数据库地址调用报表打印html
	 * 
	 * @param reportlet
	 *            json格式
	 * @return
	 */
	@RequestMapping(value = "/finereport", method = RequestMethod.GET)
	public String getReportUrl(Model model, String reportlet) {
		// String codereportlet = URLDecoder.decode(reportlet);
		model.addAttribute("reportlet", reportlet);
		model.addAttribute("reportserver", reportserver);
		return "/inlet/fineReport";
	}
}
