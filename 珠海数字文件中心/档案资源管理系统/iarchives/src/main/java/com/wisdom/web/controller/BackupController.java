package com.wisdom.web.controller;

import com.wisdom.web.entity.BackupFile;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.ExtTree;
import com.wisdom.web.entity.Tb_backup_strategy;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.BackupService;
import com.wisdom.web.service.BackupStrategyService;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by RonJiang on 2018/1/22 0022.
 */

@Controller
@RequestMapping(value = "/backupRestore")
public class BackupController {

	@Autowired
	BackupService backupService;

	@Autowired
	BackupStrategyService backupStrategyService;

	public static final String[] BACKUP_SETTING = { "userRole,用户及用户组设置", "organ,机构设置", "class,分类设置", "dataNode,数据节点设置",
			"template,模板设置", "code,档号设置" };

	public static final String[] BACKUP_DATA = { "acquisitionAudit,数据采集及审核", "management,数据管理" };

	@Value("${system.document.rootpath}")
	private String rootpath;

	@RequestMapping("/backuphtml")
	public String backuphtml(Model model) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		model.addAttribute("userid", userDetails.getUserid());
		return "/inlet/backup";
	}

	@RequestMapping("/restorehtml")
	public String restorehtml(Model model) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		model.addAttribute("userid", userDetails.getUserid());
		return "/inlet/restore";
	}

	/**
	 * 获取设置备份列表
	 * 
	 * @return
	 */
	@RequestMapping("/getSettingBackups")
	@ResponseBody
	public List<ExtTree> getSettingBackups() {
		List<ExtTree> trees = new ArrayList<>();
		for (String str : BACKUP_SETTING) {
			String[] arr = str.split(",");
			ExtTree tree = new ExtTree();
			tree.setFnid(arr[0]);
			tree.setText(arr[1]);
			tree.setLeaf(true);
			tree.setChecked(true);
			trees.add(tree);
		}
		return trees;
	}

	/**
	 * 获取业务数据备份列表
	 * 
	 * @return
	 */
	@RequestMapping("/getDataBackups")
	@ResponseBody
	public List<ExtTree> getDataBackups() {
		List<ExtTree> trees = new ArrayList<>();
		for (String str : BACKUP_DATA) {
			String[] arr = str.split(",");
			ExtTree tree = new ExtTree();
			tree.setFnid(arr[0]);
			tree.setText(arr[1]);
			tree.setLeaf(true);
			tree.setChecked(true);
			trees.add(tree);
		}
		return trees;
	}

	/**
	 * 备份
	 * 
	 * @param fnidarr
	 * @param backupContent
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/backup")
	@ResponseBody
	public ExtMsg backup(String[] fnidarr, String backupContent) throws Exception {
		backupService.backup(fnidarr, backupContent);
		String msg = "设置备份成功";
		if ("data".equals(backupContent)) {
			msg = "数据备份成功";
		}
		return new ExtMsg(true, msg, null);
	}

	@RequestMapping("/getBackupList")
	@ResponseBody
	public List<BackupFile> getBackupList(String tab) {
		return backupService.getBackupList(tab);
	}

	@RequestMapping("/deletebackup")
	@ResponseBody
	public ExtMsg deletebackup(String[] filenames) {
		backupService.deletebackup(filenames);
		return new ExtMsg(true, "数据删除成功", null);
	}

	@RequestMapping(value = "/downloadbackup/{filename}", method = RequestMethod.GET)
	@ResponseBody
	public void downloadbackup(@PathVariable String filename, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		filename = filename.replace(".zip", "");// 确保有后缀
		String targetPath = rootpath + "/backupRestore/documents/" + filename + ".zip";
		File file = new File(targetPath);
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + getOutName(request, file.getName()) + "\"");
		response.setContentType("application/zip");
		FileInputStream inputStream = new FileInputStream(file);
		ServletOutputStream out = response.getOutputStream();
		int b;
		byte[] buffer = new byte[1024];
		while ((b = inputStream.read(buffer)) != -1) {
			out.write(buffer, 0, b);
		}
		inputStream.close();
		out.flush();
		out.close();
	}

	private String getOutName(HttpServletRequest request, String name) throws Exception {
		String outName = "";
		String agent = request.getHeader("User-Agent");
		if (null != agent && -1 != agent.indexOf("MSIE")) {
			outName = URLEncoder.encode(name, "UTF8");
		} else if (null != agent && -1 != agent.indexOf("Mozilla")) {
			outName = MimeUtility.encodeText(name, "UTF8", "B");
		}
		return outName;
	}

	@RequestMapping("/analyzeByZip")
	@ResponseBody
	public List<ExtTree> analyzeByZip(String filename) {
		List<ExtTree> trees = new ArrayList<>();
		if (filename != null) {
			Set<String> backUpSet = backupService.analyzeByZip(filename);
			String[] backUpClass = ArrayUtils.addAll(BACKUP_SETTING, BACKUP_DATA);
			for (String str : backUpClass) {
				String[] arr = str.split(",");
				for (Iterator iterator = backUpSet.iterator(); iterator.hasNext();) {
					String key = (String) iterator.next();
					if (key.equals(arr[1])) {
						ExtTree tree = new ExtTree();
						tree.setFnid(arr[0]);
						tree.setText(arr[1]);
						tree.setLeaf(true);
						tree.setChecked(true);
						trees.add(tree);
						break;
					}
				}
			}
		}
		return trees;
	}

	@RequestMapping(value = "/uploadZipFiles", method = RequestMethod.POST)
	@ResponseBody
	public void uploadFileZips(HttpServletRequest request) throws Exception {
		Map<String, Object> params = parse(request);
		if ((boolean) params.get("mutipart")) {
			if (params.get("chunk") != null) { // 文件分片上传
				backupService.uploadchunkZips(params);
			} else { // 文件单片上传
				backupService.uploadFileZips(params);
			}
		}
	}

	private Map<String, Object> parse(HttpServletRequest request) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
		result.put("mutipart", isMultiPart);
		if (isMultiPart) {
			StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
			result.put("id", req.getParameter("id"));
			result.put("filename", req.getParameter("name"));
			result.put("chunk", req.getParameter("chunk"));
			result.put("chunks", req.getParameter("chunks"));

			Iterator iterator = req.getFileNames();
			while (iterator.hasNext()) {
				MultipartFile file = req.getFile((String) iterator.next());
				result.put("size", file.getSize());
				result.put("content", file.getBytes());
			}
		}
		return result;
	}

	@RequestMapping(value = "/validateZip", method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg validateZip(String[] fileName) throws Exception {
		return backupService.validateZip(fileName);
	}

	/**
	 * 数据恢复
	 * 
	 * @param fnidarr
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/restore")
	@ResponseBody
	public ExtMsg restore(String[] fnidarr, String filename, String userid) throws Exception {
		try {
			if (filename.contains("数据库备份")){
				backupService.recoverDatabase(filename, userid);
				return new ExtMsg(true, "数据正在后台恢复中，请耐心等待", null);
			}else {
				backupService.restore(fnidarr, filename);
			}
		}catch (Exception e){
			e.printStackTrace();
			return new ExtMsg(false, "数据恢复失败", null);
		}
		return new ExtMsg(true, "数据恢复成功", null);
	}

	/**
	 * 保存备份策略
	 * 
	 * @param backupFrequency
	 *            备份频率
	 * @param backupTime
	 *            备份时间
	 * @param backupType
	 *            备份类型(fullbackup)
	 * @param backupContent
	 *            备份内容（setting data）
	 * @return
	 */
	@RequestMapping(value = "/saveBackupStrategy")
	@ResponseBody
	public ExtMsg saveBackupStrategy(String backupFrequency, String backupTime, String backupType,
			String backupContent) {
		backupStrategyService.clearOriginalBackupStrategy(backupContent);// 删除数据或设置的原备份策略
		Tb_backup_strategy backupStrategy = new Tb_backup_strategy();
		backupStrategy.setBackupfrequency(backupFrequency);
		backupStrategy.setBackuptime(backupTime);
		backupStrategy.setBackuptype(backupType);
		backupStrategy.setBackupcontent(backupContent);
		String cron = "0 0 " + backupTime;
		if ("everyday".equals(backupFrequency)) {// 备份频率为每天备份（每日：everyday）
			cron += " * * ?";
		} else {// 备份频率为具体每周几（MON,TUE,WED,THU,FRI,SAT,SUN）
			cron += " ? * " + backupFrequency;
		}
		backupStrategy.setCron(cron);
		backupStrategyService.saveBackupStrategy(backupStrategy);
		String msg = "setting".equals(backupContent) ? "保存设置数据备份策略成功" : "保存业务数据备份策略成功";
		return new ExtMsg(true, msg, null);
	}

	/**
	 * 获取备份策略
	 * 
	 * @param backupContent
	 * @return
	 */
	@RequestMapping(value = "/getBackupStrategy")
	@ResponseBody
	public ExtMsg getBackupStrategy(String backupContent) {
		Tb_backup_strategy backupStrategy = backupStrategyService.getBackupStrategy(backupContent);
		if (backupStrategy != null) {
			return new ExtMsg(true, "", backupStrategy);
		}
		return new ExtMsg(false, "", null);
	}

	/**
	 * 数据库全量备份
	 *
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/backupdatabasefull")
	@ResponseBody
	public ExtMsg backupAll(String userid) throws Exception {
		backupService.backupDataBase(BackupService.BACKUP_TYPE_FULL, userid);
		String msg = "数据在后台备份中,请耐心等待...";
		return new ExtMsg(true, msg, null);
	}

}
