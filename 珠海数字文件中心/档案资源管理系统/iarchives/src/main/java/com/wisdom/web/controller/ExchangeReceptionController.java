package com.wisdom.web.controller;

import com.google.gson.Gson;
import com.wisdom.util.DateUtil;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_exchange_reception;
import com.wisdom.web.service.ElectronicService;
import com.wisdom.web.service.ExchangeService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 数据接收控制器 Created by yl on 2017/11/2.
 */
// @ControllerAdvice
@Controller
@RequestMapping(value = "/exchangeReception")
public class ExchangeReceptionController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ExchangeService exchangeService;

	@Autowired
	ElectronicService electronicService;

	@RequestMapping("/main")
	public String main() {
		return "/inlet/exchangeReception";
	}

	@RequestMapping("/importExchange")
	@ResponseBody
	public ExtMsg importSip(@RequestParam("importExchange") MultipartFile file, HttpServletRequest request) {
		ExtMsg extMsg = null;
		if (!file.isEmpty()) {
			try {
				logger.info("文件名称：" + file.getOriginalFilename() + "MD5校验值：" + DigestUtils.md5Hex(file.getInputStream())
						+ "文件大小：" + file.getSize());
				Tb_exchange_reception exchangeReception = null;
				// IE 需要截取"\"
				String fileName = file.getOriginalFilename()
						.substring(file.getOriginalFilename().lastIndexOf("\\") + 1);
				logger.info("fileName：" + fileName);
				String name = fileName.substring(fileName.lastIndexOf(".") + 1);
				logger.info("name：" + name);
				if ("sip".equals(name)) {
					Charset gbk = Charset.forName("GBK");
					ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream(), gbk);
					ZipEntry zipEntry;
					boolean document = false, sip = false;
					while ((zipEntry = zipInputStream.getNextEntry()) != null) {
						logger.info("getName:" + zipEntry.getName());
						// 判断是否包含document
						if (zipEntry.getName().indexOf("/") != -1) {
							// logger.info("截取/前的字符串:" +
							// ze.getName().substring(0,
							// ze.getName().indexOf("/")));
							if ("document".equals(zipEntry.getName().substring(0, zipEntry.getName().indexOf("/")))) {
								document = true;
							}
						} else if ("sip.xml".equals(zipEntry.getName())) {
							sip = true;
						}
					}
					zipInputStream.close();
					if (!document) {
						extMsg = new ExtMsg(false, "上传失败，压缩文件没有document文件夹或者存放目录格式不对", null);
					} else if (!sip) {
						extMsg = new ExtMsg(false, "上传失败，压缩文件没有sip.xml文件或者存放目录格式不对", null);
					} else if (document && sip) {
						exchangeReception = exchangeService.saveExchange(new Tb_exchange_reception(fileName,
								DigestUtils.md5Hex(file.getInputStream()), IOUtils.toByteArray(file.getInputStream()),
								file.getSize(), DateUtil.getCurrentTime()));
						if (exchangeReception != null) {
							extMsg = new ExtMsg(true, "上传成功", null);
						} else {
							extMsg = new ExtMsg(false, "上传失败", null);
						}
					} else {
						extMsg = new ExtMsg(false, "上传失败,请检查压缩文件是否存在document文件夹和sip.xml文件", null);
					}
				} else if ("xls".equals(name)) {
					exchangeReception = exchangeService.saveExchange(new Tb_exchange_reception(fileName,
							DigestUtils.md5Hex(file.getInputStream()), IOUtils.toByteArray(file.getInputStream()),
							file.getSize(), DateUtil.getCurrentTime()));
					if (exchangeReception != null) {
						extMsg = new ExtMsg(true, "上传成功", null);
					} else {
						extMsg = new ExtMsg(false, "上传失败", null);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				extMsg = new ExtMsg(false, "上传失败", null);
			}
		} else {
			extMsg = new ExtMsg(false, "上传失败", null);
		}
		return extMsg;
	}

	@RequestMapping("/getExchange")
	@ResponseBody
	public void getExchange(int page, int start, int limit, String condition, String operator, String content,
			HttpServletResponse response) {
		List list = exchangeService.getExchange(start, limit, condition, operator, content);
		String jsonStr = "";
		try {
			jsonStr = getGson(list);
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

	@RequestMapping("/extportExchange")
	@ResponseBody
	public void extportExchange(String fileName, HttpServletResponse response) {
		String filePath = "/static/xsd/模版.xls";
		try {
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ new String(filePath.substring(filePath.lastIndexOf("/") + 1).getBytes("GBK"), "ISO-8859-1")
					+ "\"");
			response.setContentType("application/octet-stream");
			OutputStream out;
			Resource resource = new ClassPathResource(filePath);
			InputStream inputStream = resource.getInputStream();
			// InputStream inputStream = new
			// ByteArrayInputStream(QRCodeUtil.encode("W1-30-302-10",
			// "d:/180.png",
			// "d:/MyWorkDoc",true, true).toByteArray());
			out = response.getOutputStream();
			int b = 0;
			byte[] buffer = new byte[1024];
			while ((b = inputStream.read(buffer)) != -1) {
				out.write(buffer, 0, b);
			}
			out.flush();
			out.close();
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 焦点图上传
	 *
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @throws Exception
	 */
	@RequestMapping(value = "/uploadSipfiles", method = RequestMethod.POST)
	@ResponseBody
	public void uploadSipfiles(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> params = parse(request);
		if ((boolean) params.get("mutipart")) {
			if (params.get("chunk") != null) { // 文件分片上传
				electronicService.uploadchunkSips(params);
			} else { // 文件单片上传
				electronicService.uploadfileSips(params);
			}
		}
	}

	@RequestMapping(value = "/analysisSipfile", method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg analysisSipfile(String fileName, String fileSize) throws Exception {
		return electronicService.analysisSipfile(fileName, fileSize);
	}

	@RequestMapping("/deleteExchange")
	@ResponseBody
	public ExtMsg deleteExchange(String[] exchangeids) {
		ExtMsg extMsg = null;
		int count = exchangeService.deleteExchange(exchangeids);
		if (count > 0) {
			extMsg = new ExtMsg(true, "删除成功", null);
		} else {
			extMsg = new ExtMsg(false, "删除失败", null);
		}
		return extMsg;
	}

	private Map<String, Object> parse(HttpServletRequest request) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		boolean isMutipart = ServletFileUpload.isMultipartContent(request);
		result.put("mutipart", isMutipart);
		if (isMutipart) {
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

	private String getGson(List list) {
		String jsonStr = null;
		Map<String, Object> jsonObj = new HashMap<String, Object>();
		jsonObj.put("content", (List<Tb_exchange_reception>) list.get(1));
		jsonObj.put("totalElements", list.get(0));
		Gson gson = new Gson();
		jsonStr = gson.toJson(jsonObj);
		return jsonStr;
	}
}
