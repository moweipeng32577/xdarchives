package com.wisdom.web.controller;

/**
 * 回收管理控制器
 * Created by RonJiang on 2018/4/23 0023.
 */

import com.wisdom.util.LogAop;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_electronic_recyclebin;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.service.RecyclebinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
@RequestMapping(value = "/recyclebin")
public class RecyclebinController {

	@Value("${system.document.rootpath}")
	private String rootpath;// 系统文件根目录

	@Autowired
	LogAop logAop;

	@Autowired
	RecyclebinService recyclebinService;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping("/main")
	public String index() {
		return "/inlet/recyclebin";
	}

	/**
	 * 检索
	 * 
	 * @param page
	 * @param start
	 * @param limit
	 * @param condition
	 * @param operator
	 * @param content
	 * @return
	 */
	@RequestMapping("/getRecyclebin")
	@ResponseBody
	public Page<Tb_electronic_recyclebin> getRecyclebin(int page, int start, int limit, String condition,
			String operator, String content, String sort) {
		Sort sortobj = WebSort.getSortByJson(sort);
		logger.info("page:" + page + ";start:" + start + ";limt:" + limit);
		return recyclebinService.findBySearch(condition, operator, content, page, limit, sortobj);
	}

	/**
	 * 根据回收id获取回收记录
	 * 
	 * @param recycleid
	 * @return
	 */
	@RequestMapping(value = "/recyclebins/{recycleid}", method = RequestMethod.GET)
	@ResponseBody
	public Tb_electronic_recyclebin getRecyclebin(@PathVariable String recycleid) {
		return recyclebinService.getRecyclebin(recycleid);
	}

	/**
	 * 还原已删除文件，支持多选
	 * 
	 * @param recycleids
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/restore/{recycleids}", method = RequestMethod.GET)
	@ResponseBody
	public ExtMsg restore(@PathVariable String recycleids) {
		String[] recycleidData = recycleids.split(",");
		return recyclebinService.restore(recycleidData);
	}

	/**
	 * 下载回收站中的电子文件，支持多个文件下载（自动打包压缩文件）
	 * 
	 * @param recycleids
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/download/{recycleids}", method = RequestMethod.GET)
	public void download(@PathVariable String recycleids, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String zipPath = recyclebinService.transFiles(recycleids);
		FileInputStream inputStream = null;
		try {
			File html_file = new File(zipPath);
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + ElectronicController.getOutName(request, html_file.getName()) + "\"");
			response.setContentType("application/zip");
			inputStream = new FileInputStream(html_file);
			ServletOutputStream out = response.getOutputStream();
			int b = 0;
			byte[] buffer = new byte[1024];
			while ((b = inputStream.read(buffer)) != -1) {
				out.write(buffer, 0, b);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	/**
	 * 彻底删除回收站中文件，支持多选删除
	 * 
	 * @param recycleids
	 * @return
	 */
	@RequestMapping(value = "/thoroughDelete")
	@ResponseBody
	public ExtMsg delRecyclebins(String recycleids) {
		String startTime = LogAop.getCurrentSystemTime();// 开始时间
		long startMillis = System.currentTimeMillis();// 开始毫秒数
		String[] recycleidData = recycleids.split(",");
		Integer del = recyclebinService.delRecyclebin(recycleidData);
		for (String recycleid : recycleidData) {
			logAop.generateManualLog(startTime, LogAop.getCurrentSystemTime(), System.currentTimeMillis() - startMillis,
					"回收管理", "删除回收文件操作，回收id为：" + recycleid);
		}
		if (del > 0) {
			return new ExtMsg(true, "删除成功", del);
		}
		return new ExtMsg(false, "删除失败", null);
	}
}
