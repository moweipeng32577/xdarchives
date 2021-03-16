package com.wisdom.web.controller;

import com.wisdom.util.AnalysisSipUntil;
import com.wisdom.util.ExcelUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.entity.sip.Sip;
import com.wisdom.web.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;

/**
 * 数据移交控制器 Created by yl on 2017/11/3.
 */
@Controller
@RequestMapping(value = "/exchangeTransfer")
public class ExchangeTransferController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	EntryIndexService entryIndexService;

	@Autowired
	ExchangeService exchangeService;

	@Autowired
	EntryIndexCaptureService entryIndexCaptureService;

	@Autowired
	ElectronicService electronicService;

	@RequestMapping("/main")
	public String main() {
		return "/inlet/exchangeTransfer";
	}

	@RequestMapping("/exchangeTransferData")
	@ResponseBody
	public ExtMsg getExchangeStorage(String[] exchangeids, String nodeId)
			throws IOException, ParserConfigurationException, SAXException {
		ExtMsg extMsg;
		String[] msgs = new String[exchangeids.length];
		for (int i = 0; i < exchangeids.length; i++) {
			Tb_exchange_reception exchangeReception = exchangeService.findByExchangeid(exchangeids[i]);
			String fileName = null;
			if (exchangeReception != null) {
				fileName = exchangeReception.getFilename();
				String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
				logger.info("截取的后缀名：" + fileName.substring(fileName.lastIndexOf(".") + 1));
				if ("sip".equals(extension)) {
					Charset gbk = Charset.forName("GBK");
					ZipInputStream zipInputStream = new ZipInputStream(
							new ByteArrayInputStream(exchangeReception.getFiledata()), gbk);
					ZipEntry zipEntry;
					InputStream sipInputStream = null;
					try {
						while ((zipEntry = zipInputStream.getNextEntry()) != null) {
							String zeName = zipEntry.getName().substring(zipEntry.getName().lastIndexOf("/") + 1);
							if (!"".equals(zeName)) {
								logger.info("dd:" + zeName);
								if ("sip.xml".equals(zeName)) {
									byte[] data = getByte(zipInputStream); // 获取当前条目的字节数组
									sipInputStream = new ByteArrayInputStream(data); // 把当前条目的字节数据转换成Inputstream流
									break;
								}
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
						Tb_entry_index_capture entryIndexCapture = new Tb_entry_index_capture();
						entryIndexCapture.setTitle(sip.getTitle());
						entryIndexCapture.setFunds(sip.getFunds());
						entryIndexCapture.setCatalog(sip.getCatalog());
						entryIndexCapture.setFilenumber(sip.getFilenumber());
						entryIndexCapture.setEntryretention(sip.getEntryretention());
						entryIndexCapture.setFiledate(sip.getFiledate());
						entryIndexCapture.setFilingyear(sip.getFilingyear());
						entryIndexCapture.setNodeid(nodeId);
						// entryIndexCapture.setTransfor("待移交");

						Tb_entry_index_capture save = entryIndexCaptureService.saveEntryIndexCapture(entryIndexCapture);

						if (save != null) {
							electronicService.uploadSipFile(save.getEntryid(), sip,
									new ZipInputStream(new ByteArrayInputStream(exchangeReception.getFiledata()), gbk));
						} else {
							msgs[i] = "'" + fileName + "'移交失败,请检查文件格式是否正确;";
						}
					} else {
						msgs[i] = "'" + fileName + "'移交失败,请检查文件格式是否正确;";
					}
				} else if ("xls".equals(extension) || "xlsx".equals(extension)) {
					try {
						InputStream inputStream = new ByteArrayInputStream(exchangeReception.getFiledata());
						ExcelUtil<EntryBase> util1 = new ExcelUtil<EntryBase>(EntryBase.class);
						List<EntryBase> entryBases = util1.getExcelToList(fileName.substring(0, fileName.indexOf(".")),
								inputStream);
						if (entryBases.size() > 0) {
							for (EntryBase entryBase : entryBases) {
								Tb_entry_index_capture entryIndexCapture = new Tb_entry_index_capture();
								BeanUtils.copyProperties(entryBase, entryIndexCapture);
								entryIndexCapture.setNodeid(nodeId);
								String entryid = entryIndexCaptureService.saveEntryIndexCapture(entryIndexCapture)
										.getEntryid();
								Tb_entry_detail_capture entryDetailCapture = new Tb_entry_detail_capture();
								BeanUtils.copyProperties(entryBase, entryDetailCapture);
								entryDetailCapture.setEntryid(entryid);
								entryIndexCaptureService.saveEntryDetailCapture(entryDetailCapture);
							}
						} else {
							msgs[i] = "'" + fileName + "'移交失败,请检查文件格式是否正确;";
						}
					} catch (Exception e) {
						msgs[i] = "'" + fileName + "'移交失败,请检查文件格式是否正确;";
					}
				}

			} else {
				msgs[i] = "移交失败,请检查文件格式是否正确;";
			}
		}
		String msg = "";
		for (String m : msgs) {
			logger.info("msg:" + msg + "m:" + m);
			if (m != null) {
				msg += m + "<br />";
			}
		}
		if ("".equals(msg)) {
			extMsg = new ExtMsg(true, "移交成功", null);
		} else {
			extMsg = new ExtMsg(false, msg, null);
		}
		return extMsg;
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
}
