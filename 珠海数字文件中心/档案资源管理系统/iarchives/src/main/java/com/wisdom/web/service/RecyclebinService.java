package com.wisdom.web.service;

import com.wisdom.util.FileUtil;
import com.wisdom.util.LogAop;
import com.wisdom.util.ZipUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.wisdom.web.service.ThematicService.delFolder;

/**
 * Created by RonJiang on 2018/4/23 0023.
 */
@Service
@Transactional
public class RecyclebinService {

	@Value("${system.document.rootpath}")
	private String rootpath;// 系统文件根目录

	@Autowired
	ElectronicRecyclebinRepository electronicRecyclebinRepository;

	@Autowired
	ElectronicRepository electronicRepository;

	@Autowired
	ElectronicCaptureRepository electronicCaptureRepository;

	@Autowired
	EntryService entryService;

	@Autowired
	EntryCaptureService entryCaptureService;

	@Autowired
	LogMsgRepository logMsgRepository;

	@Autowired
	EntryIndexRepository entryIndexRepository;

	@Autowired
	EntryIndexCaptureRepository entryIndexCaptureRepository;

	@Autowired
	ElectronicService electronicService;

	@Autowired
	OrganService organService;

	public Page<Tb_electronic_recyclebin> findBySearch(String condition, String operator, String content, int page,
			int limit, Sort sort) {
		Specifications sp = null;
		if (content != null) {
			sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
		}
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ?
				new Sort(Sort.Direction.DESC, "deletetime") : sort);
		return electronicRecyclebinRepository.findAll(sp, pageRequest);
	}

	public Tb_electronic_recyclebin getRecyclebin(String recycleid) {
		return electronicRecyclebinRepository.findByRecycleid(recycleid);
	}

	public ExtMsg restore(String[] recycleidData) {
		StringBuffer msg = new StringBuffer();
		List<Tb_log_msg> logs = new ArrayList<>();// 存放用于写入的日志
		SecurityUser userDetiles = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal());
		for (String recycleid : recycleidData) {
			Tb_electronic_recyclebin electronicRecyclebin = electronicRecyclebinRepository.findByRecycleid(recycleid);
			String table = electronicRecyclebin.getOriginaltable();
			String filename = electronicRecyclebin.getFilename();
			try {
				if ("tb_electronic".equals(table)) {// 删除回收表中数据，保存电子文件管理表数据
					// 判断电子文件关联的条目是否还存在
					if (entryIndexRepository.findByEntryid(electronicRecyclebin.getEntryid()) != null) {
						saveEle("management", electronicRecyclebin);
					} else {
						msg.append("'" + filename + "'文件所属条目已经被删除，无法进行还原操作<br/>");
						continue;
					}
				}
				if ("tb_electronic_capture".equals(table)) {// 删除回收表中数据，保存电子文件采集表数据
					// 判断电子文件关联的条目是否还存在
					if (entryIndexCaptureRepository.findByEntryid(electronicRecyclebin.getEntryid()) != null) {
						saveEle("capture", electronicRecyclebin);
					} else {
						msg.append("'" + filename + "'文件所属条目已经被删除，无法进行还原操作<br/>");
						continue;
					}
				}
				// 还原成功后，删除数据
				electronicRecyclebinRepository.deleteByRecycleid(recycleid);
				String organ = organService.findFullOrgan("", userDetiles.getOrganid());
				msg.append("'" + filename + "'文件还原成功<br/>");
				Tb_log_msg logMsg = new Tb_log_msg(LogAop.getIpAddress(), userDetiles.getLoginname(),
						userDetiles.getRealname(), organ, LogAop.getCurrentSystemTime(), LogAop.getCurrentSystemTime(),
						"0ms", "回收管理", "还原表:" + table + "中ID为:" + recycleid + "名为:" + filename + "的文件");
				logs.add(logMsg);
			} catch (IOException e) {
				msg.append("'" + filename + "'文件已不存在！<br/>");
			}
		}

		logMsgRepository.save(logs);// 批量存入日志
		return new ExtMsg(true, msg.toString(), null);
	}

	public Integer delRecyclebin(String[] recycleidData) {
		for (String recycleid : recycleidData) {// 删除回收站中的源文件
			Tb_electronic_recyclebin electronicRecyclebin = electronicRecyclebinRepository.findByRecycleid(recycleid);
			String fileFullpath = rootpath + electronicRecyclebin.getFilepath() + "/"
					+ electronicRecyclebin.getFilename();
			File file = new File(fileFullpath);
			file.delete();
			// 判断时间戳的文件夹是否还有文件，没有则把文件夹删除
			File timeFolder = new File(rootpath + electronicRecyclebin.getFilepath());
			if (timeFolder.listFiles() != null && timeFolder.listFiles().length == 0) {
				// 删除文件夹
				timeFolder.delete();
			}
			// 还需要判断entryid的文件里面是否还有文件，没有则把文件夹删除
			int lastIndexOf = electronicRecyclebin.getFilepath().lastIndexOf("/");
			String entryidFilepate = electronicRecyclebin.getFilepath().substring(0, lastIndexOf);
			File entryiFolder = new File(rootpath + entryidFilepate);
			if (entryiFolder.listFiles() != null && entryiFolder.listFiles().length == 0) {
				// 删除文件夹
				entryiFolder.delete();
			}
		}
		return electronicRecyclebinRepository.deleteByRecycleidIn(recycleidData);
	}

	public String transFiles(String idStr) {
		// 定义下载压缩包名称
		Calendar cal = Calendar.getInstance();
		String zipname = "E" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(cal.getTime());

		// 文件复制
		List<Map<String, Object>> selectionEntitys = getSelectionEntity(idStr);
		String desPath = "";
		for (Map<String, Object> selectionEntity : selectionEntitys) {
			String selectionFilename = (String) selectionEntity.get("filename");
			String selectionFilepath = rootpath + selectionEntity.get("filepath");
			desPath = selectionFilepath.split("recyclebinElectronic")[0] + "downRecElectronic/"
					+ new SimpleDateFormat("yyyy/M/d").format(cal.getTime());
			desPath += File.separator + zipname;
			File srcFile = new File(selectionFilepath + File.separator + selectionFilename);
			File desFile = new File(desPath + File.separator + selectionFilename);
			try {
				FileUtils.copyFile(srcFile, desFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// 文件压缩
		String transFilepath = desPath;// .substring(0,desPath.lastIndexOf(File.separator));//创建中转文件夹
		ZipUtil.zip(transFilepath.replaceAll("/", "\\\\"), transFilepath.replaceAll("/", "\\\\") + ".zip", "");// 压缩
		String zipPath = transFilepath.replace("/", "\\") + ".zip";
		delFolder(transFilepath);
		return zipPath;
	}

	/**
	 * 下载多个文件到指定文件夹
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getSelectionEntity(String idStr) {
		String[] ids = idStr.split(",");
		SecurityUser userDetiles = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal());
		List<Map<String, Object>> selectList = new ArrayList<Map<String, Object>>();
		List<Tb_log_msg> logs = new ArrayList<>();
		for (String id : ids) {
			Tb_electronic_recyclebin electronicRecyclebin = electronicRecyclebinRepository.findByRecycleid(id);
			Map<String, Object> selectionMap = electronicRecyclebin.getMap();
			selectList.add(selectionMap);
			String organ = organService.findFullOrgan("", userDetiles.getOrganid());
			Tb_log_msg logMsg = new Tb_log_msg(LogAop.getIpAddress(), userDetiles.getLoginname(),
					userDetiles.getRealname(), organ, LogAop.getCurrentSystemTime(), LogAop.getCurrentSystemTime(),
					"0ms", "回收管理", "下载表:" + electronicRecyclebin.getOriginaltable() + "中ID为:" + id + " 名为:"
							+ electronicRecyclebin.getFilename() + "的文件");
			logs.add(logMsg);
		}
		logMsgRepository.save(logs);
		return selectList;
	}

	public void restoreFile(String entryFilePath, String entryid, String srcFilePath, String fileName)
			throws IOException {
		// 回收站电子文件
		String srcFileFullPath = rootpath + srcFilePath + "/" + fileName;
		File srcFile = new File(srcFileFullPath);
		// 还原的条目电子文件路径
		File entryFile = new File(entryFilePath);
		FileUtils.copyFile(srcFile, entryFile);
		// 判断是否存在多条同路径的回收站文件(主要兼容富滇旧的文件路径（日期文件夹），存在多条则删除文件，不删除文件夹)
		if (electronicRecyclebinRepository.findByEntryidAndFilepathAndFilename(entryid, srcFilePath, fileName)
				.size() > 1) {
			// 多条只删除电子文件
			srcFile.delete();
		} else {
			// 文件夹连同文件删除
			FileUtil.delFolder(rootpath + srcFilePath);
			// 回收站的电子文件最后一层时间戳的文件删除后，也需要判断上一层的entryid文件夹还没有其它文件，如果没有，也需要删除
			int lastIndexOf = srcFilePath.lastIndexOf("/");
			// 时间戳上一层的文件夹
			String entryidFilepate = srcFilePath.substring(0, lastIndexOf);
			File entryiFolder = new File(rootpath + entryidFilepate);
			// 判断文件夹还存不存在其它文件，没有则删除
			if (entryiFolder.listFiles() != null && entryiFolder.listFiles().length == 0) {
				// 删除文件夹
				entryiFolder.delete();
			}
		}
	}

	public void saveEle(String entrytype, Tb_electronic_recyclebin electronicRecyclebin) throws IOException {
		switch (entrytype) {
		case "management":
			String electronicFilePath;
			List<Tb_electronic> electronics = electronicRepository
					.findByEntryidAndFilename(electronicRecyclebin.getEntryid(), electronicRecyclebin.getFilename());
			// 查找条目里面是否也有同样的文件，若存在，就不新增记录
			if (electronics.size() > 0) {
				// 获取条目的电子文件地址
				electronicFilePath = rootpath + electronics.get(0).getFilepath() + "/"
						+ electronics.get(0).getFilename();
			} else {
				Tb_electronic electronic = new Tb_electronic();
				BeanUtils.copyProperties(electronicRecyclebin, electronic);
				electronic.setEleid(null);
				electronic.setFilepath(
						electronicService.getStorageBaseDir("management", electronicRecyclebin.getEntryid()));
				electronicRepository.save(electronic);
				entryService.updateEleNum(electronicRecyclebin.getEntryid(), "add", 1);
				electronicFilePath = rootpath
						+ electronicService.getStorageBaseDir("management", electronicRecyclebin.getEntryid()) + "/"
						+ electronicRecyclebin.getFilename();
			}
			// 复制文件（从回收站路径剪切文件至文件原存储路径）
			restoreFile(electronicFilePath, electronicRecyclebin.getEntryid(), electronicRecyclebin.getFilepath(),
					electronicRecyclebin.getFilename());
			break;
		case "capture":
			String electronicCapturePath;
			List<Tb_electronic_capture> electronicCaptures = electronicCaptureRepository
					.findByEntryidAndFilename(electronicRecyclebin.getEntryid(), electronicRecyclebin.getFilename());
			// 查找条目里面是否也有同样的文件，若存在，就不新增记录
			if (electronicCaptures.size() > 0) {
				// 获取条目的电子文件地址
				electronicCapturePath = rootpath + electronicCaptures.get(0).getFilepath() + "/"
						+ electronicCaptures.get(0).getFilename();
			} else {
				Tb_electronic_capture electronicCapture = new Tb_electronic_capture();
				BeanUtils.copyProperties(electronicRecyclebin, electronicCapture);
				electronicCapture.setEleid(null);
				electronicCapture
						.setFilepath(electronicService.getStorageBaseDir("capture", electronicRecyclebin.getEntryid()));
				electronicCaptureRepository.save(electronicCapture);
				entryCaptureService.updateEleNum(electronicRecyclebin.getEntryid(), "add", 1);
				electronicCapturePath = rootpath
						+ electronicService.getStorageBaseDir("capture", electronicRecyclebin.getEntryid()) + "/"
						+ electronicRecyclebin.getFilename();
			}
			// 复制文件（从回收站路径剪切文件至文件原存储路径）
			restoreFile(electronicCapturePath, electronicRecyclebin.getEntryid(), electronicRecyclebin.getFilepath(),
					electronicRecyclebin.getFilename());
			break;
		}
	}
}