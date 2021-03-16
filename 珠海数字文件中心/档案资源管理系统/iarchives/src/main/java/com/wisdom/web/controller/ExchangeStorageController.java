package com.wisdom.web.controller;

import com.google.gson.Gson;
import com.wisdom.util.AnalysisSipUntil;
import com.wisdom.util.ExcelUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.entity.sip.Sip;
import com.wisdom.web.service.ExchangeService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 数据存储控制器 Created by yl on 2017/11/3.
 */
@Controller
@RequestMapping(value = "/exchangeStorage")
public class ExchangeStorageController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private List<Object> exchangeDatas = new ArrayList<>();

	private List<FileTree> fileTrees = new ArrayList<FileTree>();

	private Tb_exchange_reception exchangeReception;

	@Autowired
	ExchangeService exchangeService;

	@RequestMapping("/main")
	public String main() {
		return "/inlet/exchangeStorage";
	}

	@RequestMapping("/clearSip")
	@ResponseBody
	public boolean clearSip() {
		// 重新加载页面时，清空列表
		exchangeDatas.clear();
		fileTrees.clear();
		exchangeReception = null;
		return true;
	}

	@RequestMapping("/getExchangeStorage")
	@ResponseBody
	public void getExchangeStorage(int page, int start, int limit, HttpServletResponse response) {
		String jsonStr = "";
		try {
			jsonStr = getGson(start, limit, exchangeDatas);
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

	@RequestMapping("/verifyExchangeStorage")
	@ResponseBody
	public ExtMsg verifyExchangeStorage(String exchangeid, HttpServletResponse response)
			throws IOException, ParserConfigurationException, SAXException {
		ExtMsg extMsg = null;
		exchangeDatas.clear();
		fileTrees.clear();
		exchangeReception = exchangeService.findByExchangeid(exchangeid);
		String fileName;
		if (exchangeReception != null) {
			fileName = exchangeReception.getFilename();
			logger.info("截取的后缀名：" + fileName.substring(fileName.lastIndexOf(".") + 1));
			if ("sip".equals(fileName.substring(fileName.lastIndexOf(".") + 1))) {

				Charset gbk = Charset.forName("GBK");
				ZipInputStream zipInputStream = new ZipInputStream(
						new ByteArrayInputStream(exchangeReception.getFiledata()), gbk);
				ZipEntry zipEntry;

				FileTree file = new FileTree();
				file.setFnid(UUID.randomUUID().toString().replace("-", ""));
				file.setLeaf(false);
				file.setText(fileName);
				file.setExpanded(false);
				fileTrees.add(file);

				FileTree document = new FileTree();
				InputStream sipInputStream = null;
				try {
					while ((zipEntry = zipInputStream.getNextEntry()) != null) {
						FileTree fileTree = new FileTree();
						String zeName = zipEntry.getName().substring(zipEntry.getName().lastIndexOf("/") + 1);
						if ("".equals(zeName)) {
							document.setFnid(UUID.randomUUID().toString().replace("-", ""));
							document.setLeaf(true);
							document.setParentid(file.getFnid());
							document.setText(StringUtils.substringBefore(zipEntry.getName(), "/"));
							document.setExpanded(false);
							fileTrees.add(document);
						} else if (zipEntry.getName().contains("document")) {
							document.setLeaf(false);

							fileTree.setFnid(UUID.randomUUID().toString().replace("-", ""));
							fileTree.setParentid(document.getFnid());
							fileTree.setLeaf(true);
							fileTree.setText(zeName);
							fileTree.setExpanded(false);
							fileTrees.add(fileTree);
						} else if ("sip.xml".equals(zeName)) {
							byte[] data = getByte(zipInputStream); // 获取当前条目的字节数组
							sipInputStream = new ByteArrayInputStream(data); // 把当前条目的字节数据转换成Inputstream流
							fileTree.setFnid(UUID.randomUUID().toString().replace("-", ""));
							fileTree.setParentid(file.getFnid());
							fileTree.setLeaf(true);
							fileTree.setText(zeName);
							fileTree.setExpanded(false);
							fileTrees.add(fileTree);
							break;
						}
					}
					zipInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				String xsdPath = "../xsd/电子公文档案接收文件格式.xsd";
				AnalysisSipUntil analysisSipUntil = new AnalysisSipUntil(xsdPath, sipInputStream);
				Sip sip = analysisSipUntil.getSipData();
				if (sip != null) {
					exchangeDatas.add(sip);
				}
			} else {
				InputStream inputStream = new ByteArrayInputStream(exchangeReception.getFiledata());
				ExcelUtil<EntryBase> excelUtil = new ExcelUtil<EntryBase>(EntryBase.class);
				List<EntryBase> entryBases = excelUtil.getExcelToList(fileName.substring(0, fileName.indexOf(".")),
						inputStream);
				exchangeDatas.addAll(entryBases);
				FileTree fileTree = new FileTree();
				fileTree.setLeaf(true);
				fileTree.setText(fileName);
				fileTrees.add(fileTree);
			}
		}
		if (exchangeDatas.size() > 0) {
			extMsg = new ExtMsg(true, "解析成功", null);
		} else {
			extMsg = new ExtMsg(false, "解析失败，请检查文件数据格式是否正确！", null);
		}
		return extMsg;
	}

	@RequestMapping("/showExchangeStorage")
	@ResponseBody
	public List<FileTree> showExchangeStorage(String pcid, HttpServletResponse response) {
		if (fileTrees.size() <= 1) {
			return fileTrees;
		} else {
			List<FileTree> trees = new ArrayList<FileTree>();
			if (!"".equals(pcid)) {
				for (FileTree fileTree : fileTrees) {
					if (pcid.equals(fileTree.getParentid())) {
						trees.add(fileTree);
					}
				}
			} else {
				for (FileTree fileTree : fileTrees) {
					if (fileTree.getParentid() == null) {
						trees.add(fileTree);
					}
				}
			}
			return trees;
		}
	}

	@RequestMapping("/getDataView")
	@ResponseBody
	public void getDataView(String parentid, HttpServletResponse response) {
		String jsonStr = "";
		try {
			List<SipDataView> sipDataViews = new ArrayList<SipDataView>();
			if (!"".equals(parentid)) {
				for (FileTree fileTree : fileTrees) {
					if (parentid.equals(fileTree.getParentid())) {
						SipDataView sipDataView = new SipDataView();
						sipDataView.setName(fileTree.getText());
						if (fileTree.getText().equals("document")) {
							sipDataView.setUrl("/img/folder.png");
						} else if (fileTree.getText().toLowerCase().substring(fileTree.getText().lastIndexOf(".") + 1)
								.equals("jpg")) {
							sipDataView.setUrl("/img/jpg.ico");
						} else if (fileTree.getText().toLowerCase().substring(fileTree.getText().lastIndexOf(".") + 1)
								.equals("png")) {
							sipDataView.setUrl("/img/png.ico");
						} else if (fileTree.getText().toLowerCase().substring(fileTree.getText().lastIndexOf(".") + 1)
								.equals("xml")) {
							sipDataView.setUrl("/img/xml.ico");
						} else if (fileTree.getText().toLowerCase().substring(fileTree.getText().lastIndexOf(".") + 1)
								.equals("pdf")) {
							sipDataView.setUrl("/img/pdf.ico");
						} else if (fileTree.getText().toLowerCase().substring(fileTree.getText().lastIndexOf(".") + 1)
								.equals("ppt")) {
							sipDataView.setUrl("/img/ppt.ico");
						} else if (fileTree.getText().toLowerCase().substring(fileTree.getText().lastIndexOf(".") + 1)
								.equals("xls")
								|| fileTree.getText().toLowerCase().substring(fileTree.getText().lastIndexOf(".") + 1)
										.equals("xlsx")) {
							sipDataView.setUrl("/img/xls.ico");
						} else if (fileTree.getText().toLowerCase().substring(fileTree.getText().lastIndexOf(".") + 1)
								.equals("docx")
								|| fileTree.getText().toLowerCase().substring(fileTree.getText().lastIndexOf(".") + 1)
										.equals("doc")) {
							sipDataView.setUrl("/img/doc.ico");
						}
						sipDataViews.add(sipDataView);
					}
				}
			}
			Map<String, Object> jsonObj = new HashMap<String, Object>();
			jsonObj.put("content", sipDataViews);
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

	@RequestMapping("/openSipFile")
	@ResponseBody
	public void openSipFile(String fileName, HttpServletResponse response) {
		if (!"document".equals(fileName)) {
			try {
				Charset gbk = Charset.forName("GBK");
				ZipInputStream zipInputStream = new ZipInputStream(
						new ByteArrayInputStream(exchangeReception.getFiledata()), gbk);
				ZipEntry zipEntry;
				while ((zipEntry = zipInputStream.getNextEntry()) != null) {
					String zeName = zipEntry.getName().substring(zipEntry.getName().lastIndexOf("/") + 1);
					if (fileName.equals(zeName)) {
						byte[] data = getByte(zipInputStream); // 获取当前条目的字节数组
						InputStream is = new ByteArrayInputStream(data); // 把当前条目的字节数据转换成Inputstream流
						toWrite(response, fileName, is, (int) zipEntry.getSize());
						break;
					}
				}
				zipInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public byte[] getByte(InflaterInputStream zis) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[] temp = new byte[1024];
			byte[] buf = null;
			int length = 0;

			while ((length = zis.read(temp, 0, 1024)) != -1) {
				bout.write(temp, 0, length);
			}

			buf = bout.toByteArray();
			bout.close();
			return buf;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void toWrite(HttpServletResponse response, String fileName, InputStream ins, int size) {
		try {
			if (fileName.contains(".xml")) {
				response.setContentType("application/xml");
			} else {
				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + new String(fileName.getBytes("GBK"), "ISO-8859-1") + "\"");
				response.setContentType("application/octet-stream");
			}
			OutputStream os = response.getOutputStream();
			int bytesRead = 0;
			byte[] buffer = new byte[size];
			while ((bytesRead = ins.read(buffer, 0, size)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.flush();
			os.close();
			ins.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getGson(int start, int limit, List<Object> datas) {
		logger.info("start:" + start + "limit:" + limit);
		String jsonStr = null;
		Map<String, Object> jsonObj = new HashMap<String, Object>();
		List<Object> mdatas = new ArrayList<Object>();
		if (datas.size() > 0) {
			mdatas = datas.subList(start, datas.size() < limit + start ? datas.size() : limit + start);
		}
		jsonObj.put("content", mdatas);
		jsonObj.put("totalElements", datas.size());
		Gson gson = new Gson();
		jsonStr = gson.toJson(jsonObj);
		return jsonStr;
	}
}
