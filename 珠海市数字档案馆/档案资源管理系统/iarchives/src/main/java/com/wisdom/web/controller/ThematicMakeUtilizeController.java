package com.wisdom.web.controller;

import com.wisdom.web.entity.*;
import com.wisdom.web.service.ThematicMakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * 专题利用控制器 Created by yl on 2017/10/27.
 */
@Controller
@RequestMapping(value = "/thematicMakeUtilize")
public class ThematicMakeUtilizeController {

	@Autowired
	ThematicMakeService thematicService;

	@RequestMapping("/main")
	public String main() {
		return "/inlet/thematicMakeUtilize";
	}

	//解决利用平台与管理平台公用页面权限控制问题
	@RequestMapping("/mainly")
	public String indexly(Model model, String flag){
		model.addAttribute("buttonflag",flag);
		return "/inlet/thematicUtilize";
	}

	/**
	 * 获取已发布专题
	 *
	 * @param page
	 *            页码
	 * @param start
	 *            开始
	 * @param limit
	 *            页数
	 * @param condition
	 * @param operator
	 * @param content
	 * @return
	 */
	@RequestMapping("/getThematicDetailFb")
	@ResponseBody
	public Page<Tb_thematic_make> getThematicDetailFb(int page, int start, int limit, String condition, String operator,
			String content, String sort,String thematictypes) {
		Sort sortobj = WebSort.getSortByJson(sort);
		if (thematictypes!=null && !"专题类型".equals(thematictypes)){
			return thematicService.findTbThematicPage(page, limit, condition, operator, content, sortobj,thematictypes);
		}else {
			return thematicService.findTbThematicPage(page, limit, condition, operator, content, sortobj,null);
		}
	}

	@RequestMapping("/getThematicDetailFbList")
	@ResponseBody
	public IndexMsg getThematicDetailFbList(int page, int limit, String condition, String operator, String content,
			String sort) {
		Sort sortobj = WebSort.getSortByJson(sort);
		Page<Tb_thematic_make> thematicPage = thematicService.findTbThematicPage(page, limit, condition, operator, content,
				sortobj,null);
		return new IndexMsg(true, "0", "成功", thematicPage.getContent());
	}

	@RequestMapping("/releaseThematicUtilize")
	@ResponseBody
	public ExtMsg releaseThematicUtilize(String thematicid) {
		ExtMsg extMsg = null;
		// if (thematicService.updateThematicForPublishstate("已发布", thematicid)
		// > 0) {
		// extMsg = new ExtMsg(true, "发布成功", null);
		// } else {
		// extMsg = new ExtMsg(false, "发布失败", null);
		// }
		return extMsg;
	}

	@RequestMapping(value = "/downloadZt/{thematicid}", method = RequestMethod.GET)
	public void downloadZt(HttpServletResponse response, @PathVariable String thematicid) throws IOException {
		String zipPath = thematicService.findFilePathByThematicid(thematicid);
		FileInputStream inputStream = null;
		ServletOutputStream out = null;
		try {
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ new String(zipPath.substring(zipPath.lastIndexOf("\\") + 1).getBytes("gbk"), "iso8859-1") + "\"");
			response.setContentType("application/zip");

			File html_file = new File(zipPath);
			inputStream = new FileInputStream(html_file);
			out = response.getOutputStream();
			int b = 0;
			byte[] buffer = new byte[1024];
			while ((b = inputStream.read(buffer)) != -1) {
				out.write(buffer, 0, b);
			}
			out.flush();
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	@RequestMapping("/getThematic")
	@ResponseBody
	public List<Tb_thematic_make> getThematic() {
		List<Tb_thematic_make> thematics = thematicService.getThematicbyState();
		return thematics;
	}

	@RequestMapping("/getThematicbyid")
	@ResponseBody
	public List<Tb_thematic_detail_make> getThematicByid(String thematicid) {
		List<Tb_thematic_detail_make> thematicdetails = thematicService.getThematicdetail(thematicid);
		return thematicdetails;
	}

	//判断专题利用文件是否存在
	@RequestMapping("/getThematicFile")
	@ResponseBody
	public ExtMsg getThematicFile(String thematicid) {
		String zipPath = thematicService.findFilePathByThematicid(thematicid);
		if(null==zipPath){
			return new ExtMsg(false,"获取文件路径失败",null);
		}
		File html_file = new File(zipPath);
		if(html_file.exists()){
			return new ExtMsg(true,"",null);
		}else{
			return new ExtMsg(false,"",null);
		}
	}
}