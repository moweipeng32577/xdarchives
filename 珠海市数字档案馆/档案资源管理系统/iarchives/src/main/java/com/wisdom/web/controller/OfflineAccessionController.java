package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.wisdom.util.MD5;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.TbofflineAccessionRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.ClassifySearchService;
import com.wisdom.web.service.OfflineAccessionService;
import com.xdtech.project.foursexverify.entity.DataView;
import org.apdplat.word.vector.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 离线接收控制器 Created by yl on 2017/11/2.
 */
// @ControllerAdvice
@Controller
@RequestMapping(value = "/offlineAccession")
public class OfflineAccessionController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	OfflineAccessionService offlineAccessionService;


	@Value("${system.report.server}")
	private String reportServer;//报表服务

	@RequestMapping("/main")
	public String main(Model model) {
		model.addAttribute("reportServer",reportServer);
		return "/inlet/offlineAccession";
	}

	/**
	 * 获取批次表单内容
	 * @return
	 */
	@RequestMapping("/getBatchAddForm")
	@ResponseBody
	public ExtMsg getBatchAddForm() {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 系统绑定对象(全局)
		Tb_offline_accession_batch callout;
		String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		callout = new Tb_offline_accession_batch();
		callout.setBatchcode(date.replaceAll("-", "").substring(0, 6) + "0001");
		return new ExtMsg(true, "成功", callout);
	}

	/**
	 * 删除批次
	 * @param batchids 批次ID数组
	 * @return
	 */
	@RequestMapping("/batchDel")
	@ResponseBody
	public ExtMsg batchDel(String[] batchids) {
		boolean status = offlineAccessionService.batchDel(batchids);
		return new ExtMsg(status,"删除成功","");
	}

	/**
	 * 新增批次表单内容
	 * @return
	 */
	@RequestMapping("/addBatch")
	@ResponseBody
	public ExtMsg addOutUser(Tb_offline_accession_batch batch) {
		return offlineAccessionService.addBatch(batch);
	}


	@RequestMapping("/getBatch")
	@ResponseBody
	public Page<Tb_offline_accession_batch> getBatch(int page, int start, int limit, String condition, String operator, String content, String sort) {
		Sort sortobj = WebSort.getSortByJson(sort);
		PageRequest pageRequest = new PageRequest(page-1, limit, sortobj == null ? new Sort(Sort.Direction
				.DESC,"batchname") : sortobj);
        Specifications sp = null;
        if(content != null){
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
		Page<Tb_offline_accession_batch> list = offlineAccessionService.getBatch(sp,pageRequest);
		return list;
	}

	@RequestMapping("/addBatchdoc")
	@ResponseBody
	public List<Tb_offline_accession_batchdoc> addBatchdoc(String  batchdocListJson) {
		List<Tb_offline_accession_batchdoc> batchdocList = JSON.parseArray(batchdocListJson,Tb_offline_accession_batchdoc.class);
		offlineAccessionService.addBatchDoc(batchdocList);
		return batchdocList;
	}

	@RequestMapping("/getBatchdoc")
	@ResponseBody
	public Page<Tb_offline_accession_batchdoc> getBatch(int page, int start, int limit, String condition, String operator, String content, String sort,String batchid) {
		Sort sortobj = WebSort.getSortByJson(sort);
		PageRequest pageRequest = new PageRequest(page-1, limit, sortobj == null ? new Sort(Sort.Direction
				.DESC,"filename") : sortobj);
		Page<Tb_offline_accession_batchdoc> list = offlineAccessionService.getBatchdoc(pageRequest,batchid);
		return list;
	}

	@RequestMapping("/foursexverifys")
	@ResponseBody
	public List<Map<String, String>> foursexverifys(String fileNames,String insertFileNames) {
		return offlineAccessionService.getFourSexVerify(fileNames,insertFileNames);
	}

	@RequestMapping("/showVerifyPackage")
	@ResponseBody
	public List<com.xdtech.project.foursexverify.entity.FileTree> showVerifyPackage(String filename) {
		return offlineAccessionService.getVerifyPackage(filename);
	}

	@RequestMapping("/showOAVerifyPackage")
	@ResponseBody
	public List<com.xdtech.project.foursexverify.entity.FileTree> showOAVerifyPackage(String filename) {
		return offlineAccessionService.getOAVerifyPackage(filename);
	}

	@RequestMapping("/getDataView")
	@ResponseBody
	public void getDataView(String[] childrens, HttpServletResponse response) {
		String jsonStr = "";
		try {
			List<DataView> dataViews = new ArrayList<DataView>();
			if (childrens!=null &&childrens.length>0) {
				for (String  children : childrens) {
					DataView dataView = new DataView();
					dataView.setName(children);
					if (children.indexOf(".")==-1) {
						dataView.setUrl("/img/folder.png");
					} else if (children.toLowerCase().substring(children.lastIndexOf(".") + 1)
							.equals("jpg")) {
						dataView.setUrl("/img/jpg.ico");
					} else if (children.toLowerCase().substring(children.lastIndexOf(".") + 1)
							.equals("png")) {
						dataView.setUrl("/img/png.ico");
					} else if (children.toLowerCase().substring(children.lastIndexOf(".") + 1)
							.equals("xml")) {
						dataView.setUrl("/img/xml.ico");
					} else if (children.toLowerCase().substring(children.lastIndexOf(".") + 1)
							.equals("pdf")) {
						dataView.setUrl("/img/pdf.ico");
					} else if (children.toLowerCase().substring(children.lastIndexOf(".") + 1)
							.equals("ppt")) {
						dataView.setUrl("/img/ppt.ico");
					} else if (children.toLowerCase().substring(children.lastIndexOf(".") + 1)
							.equals("xls")
							|| children.toLowerCase().substring(children.lastIndexOf(".") + 1)
							.equals("xlsx")) {
						dataView.setUrl("/img/xls.ico");
					} else if (children.toLowerCase().substring(children.lastIndexOf(".") + 1)
							.equals("docx")
							|| children.toLowerCase().substring(children.lastIndexOf(".") + 1)
							.equals("doc")) {
						dataView.setUrl("/img/doc.ico");
					}else{
						dataView.setUrl("/img/focus/no.jpg");
					}
					dataViews.add(dataView);
				}
			}
			Map<String, Object> jsonObj = new HashMap<String, Object>();
			jsonObj.put("content", dataViews);
			Gson gson = new Gson();
			jsonStr = gson.toJson(jsonObj);
			response.setContentType("application/x-json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = null;
			out = response.getWriter();
			out.print(jsonStr);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("/getMetadata")
	@ResponseBody
	public Map<String, String> getMetadata(String fileName,String xmlName) {
		return offlineAccessionService.getMetadata(fileName,xmlName);
	}

	@RequestMapping("/insertCapture")
	@ResponseBody
	public ExtMsg insertCapture(String nodeid,String fileNames,String docid) {
		offlineAccessionService.insertCapture(nodeid,fileNames);

		String[] ids = docid.split(",");
		offlineAccessionService.updateDoc(ids);

		return new ExtMsg(true, "接入成功", null);
	}
}


