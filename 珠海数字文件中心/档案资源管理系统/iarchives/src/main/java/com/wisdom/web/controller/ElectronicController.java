package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.secondaryDataSource.entity.Tb_electronic_browse_sx;
import com.wisdom.secondaryDataSource.repository.SxElectronicBrowseRepository;
import com.wisdom.util.*;
import com.wisdom.web.entity.*;
import com.wisdom.web.entity.ExtTree;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.*;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 电子原文管理控制器 Created by Rong on 2017/11/15.
 */
@Controller
@RequestMapping(value = "/electronic")
public class ElectronicController {

	private static String type;

	@Value("${system.document.rootpath}")
	private String rootpath;// 系统文件根目录

	@Value("${system.nginx.browse.path}") // 浏览文件路径
	private String browsepath;

	@Value("${system.document.watermarkpath}")
	private String watermarkpath;// 水印文件根路径

	@Value("${system.document.watermarktext}")
	private String watermarktext;// 水印文件文本

	@Value("${system.ImageProcessor.path}")
	private  String ImageProcessorPath;//大图像压缩外部程序

	@Value("${system.print.watermark}")
	private  String printWatermark;//电子文件打印水印开关

	@Value("${itemclickDownload}")
	private String itemclickDownload;//是否点击下载无法查看的文件

	@Value("${system.document.mediaServerPath}")
	private String mediaServerPath;// 文件系统服务器地址

	@Autowired
	ElectronicService electronicService;

	@Autowired
	ElectronicRepository electronicRepository;

	@Autowired
	ElectronicCaptureRepository electronicCaptureRepository;

	@Autowired
	PersonalizedRepository personalizedRepository;

	@Autowired
	ElectronicSolidRepository electronicSolidRepository;

	@Autowired
	UserService userService;

	@Autowired
	TextOpenRepository textOpenRepository;

	@Autowired
	BorrowDocRepository borrowDocRepository;

	@Autowired
	EntryIndexService entryIndexService;

	@Autowired
	EntryIndexCaptureService entryIndexCaptureService;

	@Autowired
	WatermarkService watermarkService;

	@Autowired
	WatermarkUtil watermarkUtil;

	@Autowired
	DataNodeExtRepository dataNodeExtRepository;

	@Autowired
	ElectronicAccessRepository electronicAccessRepository;

	@Autowired
	SxElectronicBrowseRepository sxElectronicBrowseRepository;

	public Map<String, Object> parse(HttpServletRequest request) throws Exception {
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

	@RequestMapping(value = "/serelectronics/{entrytype}", method = RequestMethod.POST)
	@ResponseBody
	public void uploadfiles(HttpServletRequest request, @PathVariable String entrytype) throws Exception {
		Map<String, Object> params = parse(request);
		if ((boolean) params.get("mutipart")) {
			if (params.get("chunk") != null) { // 文件分片上传
				electronicService.uploadchunk(params, entrytype, "");
			} else { // 文件单片上传
				electronicService.uploadfile(params, entrytype, "");
			}
		}
	}

	@RequestMapping(value = "/serelectronics/{entrytype}/{entryid}", method = RequestMethod.POST)
	@ResponseBody
	public void uploadfiles(HttpServletRequest request, @PathVariable String entrytype, @PathVariable String entryid)
			throws Exception {
		Map<String, Object> params = parse(request);
		if ((boolean) params.get("mutipart")) {
			if (params.get("chunk") != null) { // 文件分片上传
				electronicService.uploadchunk(params, entrytype, entryid);
			} else { // 文件单片上传
				electronicService.uploadfile(params, entrytype, entryid);
			}
		}
	}

	@RequestMapping(value = "/serelectronics/version", method = RequestMethod.POST)
	@ResponseBody
	public void uploadfilesVersion(HttpServletRequest request)
			throws Exception {
		Map<String, Object> params = parse(request);
		if ((boolean) params.get("mutipart")) {
			if (params.get("chunk") != null) { // 文件分片上传
				electronicService.uploadchunkVersion(params);
			} else { // 文件单片上传
				electronicService.uploadfileVersion(params);
			}
		}
	}

	@RequestMapping(value = "/offlineAccession", method = RequestMethod.POST)
	@ResponseBody
	public void offlineAccession(HttpServletRequest request) throws Exception {
		Map<String, Object> params = parse(request);
		if ((boolean) params.get("mutipart")) {
			if (params.get("chunk") != null) { // 文件分片上传
				electronicService.uploadOfflineChunk(params);
			} else { // 文件单片上传
				electronicService.uploadOfflineFile(params);
			}
		}
	}

	@RequestMapping(value = "/electronicsUserimg", method = RequestMethod.POST)
	@ResponseBody
	public void uploadUserimg(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> params = parse(request);
		if ((boolean) params.get("mutipart")) {
			if (params.get("chunk") != null) { // 文件分片上传
				electronicService.uploadchunkUserimg(params);
			} else { // 文件单片上传
				electronicService.uploadUserimg(params);
			}
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
	@RequestMapping(value = "/electronicsFocus", method = RequestMethod.POST)
	@ResponseBody
	public void uploadfilesFocus(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> params = parse(request);
		if ((boolean) params.get("mutipart")) {
			if (params.get("chunk") != null) { // 文件分片上传
				electronicService.uploadchunkFocus(params);
			} else { // 文件单片上传
				electronicService.uploadfileFocus(params);
			}
		}
	}

	/**
	 * 专题制作
	 *
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @throws Exception
	 */
	@RequestMapping(value = "/electronicsThematic", method = RequestMethod.POST)
	@ResponseBody
	public void electronicsThematic(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> params = parse(request);
		if ((boolean) params.get("mutipart")) {
			if (params.get("chunk") != null) { // 文件分片上传
				electronicService.uploadchunkThematic(params);
			} else { // 文件单片上传
				electronicService.uploadfileThematic(params);
			}
		}
	}

	@RequestMapping(value = "/electronics/{entrytype}/{filename}", method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg uploadadd(@PathVariable String entrytype, @PathVariable String filename) {
		Map<String, Object> map = electronicService.saveElectronic(entrytype, null, filename);
		ExtMsg msg = new ExtMsg(true, "", map);
		return msg;
	}

	@RequestMapping(value = "/electronics/{entrytype}/{entryid}/{filename}", method = RequestMethod.POST)
	@ResponseBody
	public synchronized ExtMsg uploadmodify(@PathVariable String entrytype, @PathVariable String entryid,
			@PathVariable String filename) {
		Map<String, Object> map = electronicService.saveElectronic(entrytype, entryid, filename);
		ExtMsg msg = new ExtMsg(true, "", map);
		return msg;
	}

	@RequestMapping(value = "/ztelectronics/{entrytype}/{filename}", method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg uploadztadd(@PathVariable String entrytype, @PathVariable String filename) {
		Map<String, Object> map = electronicService.saveZtElectronic(entrytype, null, filename,null,"");
		ExtMsg msg = new ExtMsg(true, "", map);
		return msg;
	}

	@RequestMapping(value = "/ztelectronics/{entrytype}/{entryid}/{filename}", method = RequestMethod.POST)
	@ResponseBody
	public synchronized ExtMsg ztuploadmodify(@PathVariable String entrytype, @PathVariable String entryid,
			@PathVariable String filename,String isImportThematicZip,String fileClassId) {
		Map<String, Object> map=new HashMap<>();
		if("true".equals(isImportThematicZip)){//上传专题压缩包
			String path=electronicService.getTemporaryThematic();
			try{
				//解压包
				ZipUtils.unzip(path+File.separator+filename,path+File.separator+filename.substring(0,filename.lastIndexOf(".")));
				//保存文件夹和文件
				electronicService.traverse(path+File.separator+filename.substring(0,filename.lastIndexOf(".")),fileClassId,entryid);
			}catch (Exception e){
				e.printStackTrace();
			}finally {
				FileUtil.delAllFile(path);
			}
		}else {
			map = electronicService.saveZtElectronic(entrytype, entryid, filename,fileClassId,"");
		}
		return new ExtMsg(true, "", map);
	}

	@RequestMapping(value = "/chunk/{filename}/{chunks}/{chunk}", method = RequestMethod.GET)
	@ResponseBody
	public boolean checkchunk(@PathVariable String filename, @PathVariable int chunks, @PathVariable int chunk)
			throws Exception {
		return electronicService.checkchunk(filename, chunks, chunk);
	}

	@RequestMapping(value = "/chunk/{filename}", method = RequestMethod.DELETE)
	@ResponseBody
	public void delchunk(@PathVariable String filename) {
		electronicService.delchunk(filename);
	}

	/**
	 * 处理文件下载时的中文名
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param name
	 *            文件名
	 * @return 转码过的文件名
	 * @throws Exception
	 */
	public static String getOutName(HttpServletRequest request, String name) throws IOException {
		String outName = MimeUtility.encodeText(name, "UTF8", "B");
		UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
		Browser browser = userAgent.getBrowser();
		String browseName = browser.getName()!=null?browser.getName().toLowerCase():"";
		if (browseName.indexOf("internet explorer")>-1) {
			outName = URLEncoder.encode(name, "UTF8");
		}
		return outName;
	}

	/**
	 * 根据eleid判断文件是否存在于相应目录中，若不存在，给出相应提示
	 *
	 * @param eleids
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/ifFileExist/{entrytype}/{eleids}", method = RequestMethod.GET)
	@ResponseBody
	public ExtMsg ifFileExist(@PathVariable String entrytype, @PathVariable String eleids,String xtType) throws Exception {
		String[] eleidStrArr = eleids.split(",");
		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> mapList = new ArrayList<>();
		String path="";
		for (String eleid : eleidStrArr) {
			if("声像系统".equals(xtType)){
				map=electronicService.findSxElectronic(entrytype,eleid);
				path=browsepath;
			}else {
				map = electronicService.findElectronic(entrytype, eleid);
				path=rootpath;
			}
			mapList.add(map);
		}
		boolean ifFileExists = true;
		String notExistsFilesStr = "";
		for (Map<String, Object> resultMap : mapList) {
			String filename = (String) resultMap.get("filename");
			File file = new File(path + resultMap.get("filepath") + "/" + filename);
			if (!file.exists()) {
				if (notExistsFilesStr.length() > 0) {
					notExistsFilesStr += "、";
				}
				notExistsFilesStr += "“" + filename + "”";
				ifFileExists = false;
			}
		}
		if (ifFileExists) {
			return new ExtMsg(true, "", null);
		} else {
			return new ExtMsg(false, "本地文件" + notExistsFilesStr + "不存在！", null);
		}
	}

	@RequestMapping(value = "/electronics/download/{entrytype}/{eleid}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<byte[]> downfile(@PathVariable String entrytype, @PathVariable String eleid,
			HttpServletRequest request, HttpServletResponse response,String mType,String xtType) throws Exception {
		Map<String, Object> map=null;
		String path="";
		if("声像系统".equals(xtType)){
			map= electronicService.findSxElectronic(entrytype, eleid);
			path=browsepath;
		}else {
			map= electronicService.findElectronic(entrytype, eleid);
			path=rootpath;
		}
		String filename = (String) map.get("filename");
        String file_type = (String)map.get("filetype");
        String mediaPath = path + map.get("filepath") + "/" + filename;
		//pdf文件添加水印
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if("pdf".equals(file_type)&&("lyjylook".equals(mType)||"gljylook".equals(mType))){   //是否pdf文件
            Tb_watermark watermark = watermarkService.getWatermarkByOrgan(userDetails.getReplaceOrganid());//根据机构id获取水印配置信息
            if (watermark == null) {
                watermark = watermarkService.getWatermarkByOrgan("0");//若所属机构没有设置水印则使用全局水印
            }
            String waterFilePath = watermarkUtil.getWatermarkPdf(watermark,mediaPath,userDetails.getLoginname(),2052,request);
            mediaPath = waterFilePath;
        }
		File file = new File(mediaPath);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentLength(Long.parseLong((String) map.get("filesize")));
		headers.setContentDispositionFormData("attachment", getOutName(request, filename));
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/electronics/downloads/{entrytype}/{idStr}", method = RequestMethod.GET)
	public void downfiles(@PathVariable String entrytype, @PathVariable String idStr, HttpServletRequest request,
			HttpServletResponse response,String mType,String xtType) throws Exception {
		String zipPath = electronicService.transFiles(entrytype, idStr,mType);
		try {
			File html_file = new File(zipPath);
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + getOutName(request, html_file.getName()) + "\"");
			response.setContentType("application/zip");
			FileInputStream inputStream = new FileInputStream(html_file);
			ServletOutputStream out = response.getOutputStream();
			int b = 0;
			byte[] buffer = new byte[1024];
			while ((b = inputStream.read(buffer)) != -1) {
				out.write(buffer, 0, b);
			}
			inputStream.close();
			out.flush();
			out.close();
		} catch (Exception e) {
		}
	}

    @RequestMapping(value = "/downloadsExist/{entrytype}/{idStr}")
    @ResponseBody
    public Boolean downloadsExist(@PathVariable String entrytype,@PathVariable String idStr){
        return electronicService.downloadsExist(entrytype,idStr);
    }

    @RequestMapping("/mediaNative")
	public void mediaNative(String entrytype, String eleid, HttpServletResponse response) {
//        response.setHeader("content-type", "text/html; charset=gbk");

		response.setCharacterEncoding("GBK");
//        response.setHeader("content-type", "text/html; charset=gbk");
		response.setContentType("text/txt;charset=gbk");

		Map<String, Object> map = electronicService.findElectronic(entrytype,eleid);
		String filename = (String)map.get("filename");
		String imgPath = rootpath + map.get("filepath") + "/" + filename;
		File file = new File(imgPath);
		try {
			FileInputStream inputStream = new FileInputStream(file);
			ServletOutputStream out = response.getOutputStream();

			int b = 0;
			byte[] buffer = new byte[1024];
			while ((b = inputStream.read(buffer)) != -1) {
				out.write(buffer, 0, b);
			}
			out.flush();
			out.close();
			inputStream.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/electronics/{entrytype}/{entryid}/{eleids}", method = RequestMethod.DELETE)
	@ResponseBody
	public ExtMsg deletefile(@PathVariable String entrytype, @PathVariable String entryid, @PathVariable String eleids)
			throws IOException {
		if (eleids.startsWith(",")) {
			eleids = eleids.substring(1, eleids.length());
		}
		Integer num = electronicService.deleteElectronic(entrytype, entryid, eleids);
		if (num > 0) {
			electronicSolidRepository.deleteByEntryidAndElectronicidIn(entryid, eleids.split(","));
			return new ExtMsg(true, "删除成功", eleids);
		}
		return new ExtMsg(false, "删除失败", null);
	}

	@RequestMapping(value = "/ztelectronics/{entrytype}/{entryid}/{eleids}", method = RequestMethod.DELETE)
	@ResponseBody
	public ExtMsg deleteZtfile(@PathVariable String entrytype, @PathVariable String entryid,
			@PathVariable String eleids) {
		Integer num = electronicService.deleteZtElectronic(entrytype, entryid, eleids);
		if (num > 0) {
			return new ExtMsg(true, "删除成功", eleids);
		}
		return new ExtMsg(false, "删除失败", null);
	}

	@RequestMapping(value = "/electronics/tree/{entrytype}/{entryid}/{remainEleids}", method = RequestMethod.GET)
	@ResponseBody
	public List<ExtTree> findElectronicTree(@PathVariable String entrytype, @PathVariable String entryid,
											@PathVariable String[] remainEleids,String fileClassId) {
		List<Map<String, Object>> list;
		if ("undefined".equals(remainEleids[0]) && "undefined".equals(entryid)) {
			return null;
		} else {
			if("thematic".equals(entrytype)){
				return electronicService.findElectronics(entryid,fileClassId);
			}else if("thematicUtilize".equals(entrytype)){
				return electronicService.findElectronics(entryid);
			}
			list = electronicService.findElectronics(entrytype, entryid, remainEleids);
		}
		return createExtTree(list);
	}

	public List<ExtTree>  createExtTree(List<Map<String, Object>> list){
		ExtTree returnTree = new ExtTree();
		for (Map<String, Object> map : list) {
			if (map.get("folder") != null && !"".equals(map.get("folder").toString().trim())) {
				String path = map.get("folder").toString().substring(1);
				String[] folder = path.split("/");
				ExtTree tempTree = newTemp(folder, map);
				returnTree = insertTree(returnTree, tempTree);
			} else {
				ExtTree node = new ExtTree();
				node.setFnid((String) map.get("eleid"));
				node.setText((String) map.get("filename"));
				node.setLeaf(true);

				ExtTree[] extTrees;
				if (returnTree.getChildren() != null) {// 添加子节点
					extTrees = Arrays.copyOf(returnTree.getChildren(), returnTree.getChildren().length + 1);
					extTrees[returnTree.getChildren().length] = node;
				} else {
					extTrees = new ExtTree[1];
					extTrees[0] = node;
				}
				returnTree.setChildren(extTrees);
			}
		}
		List<ExtTree> extTreeList = new ArrayList<>();
		if (returnTree.getChildren() != null) {
			for (ExtTree extTree : returnTree.getChildren()) {
				extTreeList.add(extTree);
			}
		}
		return extTreeList;
	}

	/**
	 * 利用平台-审批通过-查看原文
	 *
	 * @param entryid
	 * @return
	 */
	@RequestMapping(value = "/solidUnion/{entryid}/{remainEleids}", method = RequestMethod.GET)
	@ResponseBody
	public List<ExtTree> findSolidAndOriginal(@PathVariable String entryid,@PathVariable String remainEleids) {
		String[] eleids = remainEleids.split(",");
		List<Map<String, Object>> list = electronicService.findElectronics("solid", entryid,
				eleids);
		List<Map<String, Object>> listmap = new ArrayList<>();
		for (Map<String, Object> map : list) {
			for(int j=0;j<eleids.length;j++){
				if (map.get("electronicid").equals(eleids[j])) {
					listmap.add(map);
					break;
				}
			}
		}
		ExtTree returnTree = new ExtTree();
		for (Map<String, Object> map : listmap) {
			if (map.get("folder") != null && !"".equals(map.get("folder").toString().trim())) {
				String path = map.get("folder").toString().substring(1);
				String[] folder = path.split("/");
				ExtTree tempTree = newTemp(folder, map);
				returnTree = insertTree(returnTree, tempTree);
			} else {
				ExtTree node = new ExtTree();
				node.setFnid((String) map.get("eleid"));
				node.setText((String) map.get("filename"));
				node.setLeaf(true);

				ExtTree[] extTrees;
				if (returnTree.getChildren() != null) {// 添加子节点
					extTrees = Arrays.copyOf(returnTree.getChildren(), returnTree.getChildren().length + 1);
					extTrees[returnTree.getChildren().length] = node;
				} else {
					extTrees = new ExtTree[1];
					extTrees[0] = node;
				}
				returnTree.setChildren(extTrees);
			}
		}
		List<ExtTree> extTreeList = new ArrayList<>();
		if (returnTree.getChildren() != null) {
			for (ExtTree extTree : returnTree.getChildren()) {
				extTreeList.add(extTree);
			}
		}
		return electronicService.findSolidAndOriginal(entryid, extTreeList, listmap,eleids);// 补上
																					// 没有被固化的文件
	}

	private ExtTree insertTree(ExtTree returnTree, ExtTree temptree) {
		Boolean flag = false;
		if (returnTree.getChildren() != null) {
			for (int i = 0; i < returnTree.getChildren().length; i++) {
				ExtTree extTree = returnTree.getChildren()[i];
				if (extTree.getText().equals(temptree.getText())) {
					if (temptree.getChildren() != null && temptree.getChildren().length != 0) {
						insertTree(extTree, temptree.getChildren()[0]);// only
																		// one
																		// child
					}
					// searchtree无子，代表已有叶子节点存在
					flag = true;
					break;
				}
			}
			if (!flag) {// 本级中没有找到相同节点——添加
				ExtTree[] extTrees = Arrays.copyOf(returnTree.getChildren(), returnTree.getChildren().length + 1);
				extTrees[returnTree.getChildren().length] = temptree;
				returnTree.setChildren(extTrees);
			}
		} else {
			ExtTree[] extTrees = new ExtTree[1];
			extTrees[0] = temptree;
			returnTree.setChildren(extTrees);
		}
		return returnTree;
	}

	private ExtTree newTemp(String[] folder, Map<String, Object> map) {
		ExtTree temptree = new ExtTree();
		temptree.setLeaf(true);
		temptree.setFnid((String) map.get("eleid"));
		temptree.setText((String) map.get("filename"));
		for (int i = folder.length; i > 0; i--) {
			ExtTree node = new ExtTree();
			node.setFnid("");
			node.setCls("folder");
			node.setText(folder[i - 1]);
			node.setLeaf(false);
			ExtTree[] extfile = new ExtTree[1];
			extfile[0] = temptree;
			node.setChildren(extfile);
			temptree = node;
		}
		return temptree;
	}

	/**
	 * 获取焦点图树节点
	 *
	 * @return
	 */
	@RequestMapping(value = "/electronicsFocusTree", method = RequestMethod.GET)
	@ResponseBody
	public List<ExtTree> findElectronicTreeFocus() {
		return electronicService.findElectronicTreeFocus();
	}

	@RequestMapping("/media")
	public String media(Model model, String entrytype, String eleid, String filetype,String xtType) {
		// Map<String, Object> map =
		// electronicService.findElectronic(entrytype,eleid);
		// String filename = (String)map.get("filename");
		// String imgPath = rootpath + map.get("filepath") + "/" + filename;
		List<String> typeValidation = Arrays.asList("doc","docx","xls","xlsx","ppt","pptx","dwg");
		String showPage = "/inlet/media";
		if(typeValidation.contains(filetype)){
			showPage = "/inlet/officeMedia1";
		}
		model.addAttribute("filetype", filetype);
		if("txt".equals(filetype)){
			model.addAttribute("imgsrc", "/electronic/mediaNative?entrytype=" + entrytype + "&eleid=" + eleid+"&xtType="+xtType);
			model.addAttribute("itemclickDownload",itemclickDownload);
			return showPage;
		}
		else if (entrytype != null) {
			model.addAttribute("imgsrc", "/electronic/loadMedia?entrytype=" + entrytype + "&eleid=" + eleid+"&xtType="+xtType);
			model.addAttribute("itemclickDownload",itemclickDownload);
			return showPage;
		} else {
			model.addAttribute("imgsrc", "/electronic/loadMediaFocus?eleid=" + eleid+"&xtType="+xtType);
			return showPage;
		}
	}

	@RequestMapping("/jyMedia")
	public String jyMedia(Model model, String entrytype, String eleid, String filetype,String xtType) {
		model.addAttribute("isJy", true);
		model.addAttribute("filetype", filetype);
		model.addAttribute("imgsrc", "/electronic/loadJyMedia?entrytype=" + entrytype + "&eleid=" + eleid+"&xtType="+xtType);
		Map<String, Object> map = null;
		if("声像系统".equals(xtType)){
			map=electronicService.findSxElectronic(entrytype, eleid);
		}else {
			map=electronicService.findElectronic(entrytype, eleid);
		}
		if (map != null) {
			model.addAttribute("entryid", map.get("entryid"));
		}
		return "/inlet/media";
	}

	@RequestMapping("/jyMediaPrint")
	public String jyMediaPrint(Model model, String entrytype, String eleid, String filetype,String type) {
		model.addAttribute("isJy", true);
		model.addAttribute("filetype", filetype);
		if("hasWm".equals(type)){
			model.addAttribute("imgsrc", "/electronic/loadJyMedia?entrytype=" + entrytype + "&eleid=" + eleid);
		}else{
			model.addAttribute("imgsrc", "/electronic/loadJyMediaPrint?entrytype=" + entrytype + "&eleid=" + eleid);
		}
		return "/inlet/media";
	}

	@RequestMapping("/loadJyMediaPrint")
	public void loadJyMediaPrint(HttpServletRequest request, HttpServletResponse response, String entrytype, String eleid)
			throws Exception {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Map<String, Object> map = electronicService.findElectronic(entrytype, eleid);
		String file_name = (String) map.get("filename");
		String file_type = (String) map.get("filetype");
		response.setCharacterEncoding("UTF-8");
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

	@RequestMapping("/loadJyMedia")
	public void loadJyMedia(HttpServletRequest request, HttpServletResponse response, String entrytype, String eleid,String xtType)
			throws Exception {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Map<String, Object> map=null;
		String mediaPath="";
		ServletOutputStream out;
		if("声像系统".equals(xtType)){
			map = electronicService.findSxElectronic(entrytype, eleid);
			String file_name = (String) map.get("filename");
			mediaPath = browsepath + map.get("filepath") + "/" + file_name;//.substring(0,file_name.lastIndexOf("."))+"_WM.jpg";
			System.out.println("----------------图片地址："+mediaPath);
		}else {
			map = electronicService.findElectronic(entrytype, eleid);
			String file_name = (String) map.get("filename");
			mediaPath = rootpath + map.get("filepath") + "/" + file_name;

			String file_type = (String) map.get("filetype");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/" + file_type);

			//先判断文件是否存在再进行下一步，没有文件就直接返回异常信息
			File mediaFile = new File(mediaPath);
			if (!mediaFile.exists()) {
				return;
			}
			String organId = userService.getUserOrgan(userDetails.getLoginname());
			Tb_watermark watermark = watermarkService.getWatermarkByOrgan(organId);//根据机构id获取水印配置信息
			if (watermark == null || "0".equals(watermark.getIsuse())) {//没有机构水印或者机构水印不是可使用状态
				watermark = watermarkService.getWatermarkByOrgan("0");//若所属机构没有设置水印则使用全局水印
			}
			if (!"0".equals(watermark.getIsuse()))
				mediaPath = watermarkUtil.getWatermarkPdf(watermark, mediaPath, userDetails.getLoginname(), 2052, request);
		}
		File html_file = new File(mediaPath);
		if (!html_file.exists()) {
			return;
		}
		FileInputStream inputStream = new FileInputStream(html_file);
		out = response.getOutputStream();

		int b = 0;
		byte[] buffer = new byte[1024*8];
		while ((b = inputStream.read(buffer)) != -1) {
			out.write(buffer, 0, b);
		}
		inputStream.close();
		out.flush();
		out.close();
	}

	@RequestMapping("/loadMedia")
	public void loadMedia(HttpServletRequest request, HttpServletResponse response, String entrytype, String eleid,String xtType)
		throws InterruptedException {
		Map<String, Object> map=null;
		String path="";
		if("声像系统".equals(xtType)){
			map= electronicService.findSxElectronic(entrytype, eleid);
			path=browsepath;
		}else {
			map= electronicService.findElectronic(entrytype, eleid);
			path=rootpath;
		}
		String PicPath = "";
		String file_name = (String) map.get("filename");
		type = (String) map.get("filename");
		String file_type = (String) map.get("filetype");
		Integer file_size = Integer.parseInt((String) map.get("filesize"));
		String mediaPath = path + map.get("filepath") + "/" + file_name;//图片文件路径
		response.setCharacterEncoding("UTF-8");

		if ("jpg".equals(file_type)) {
			if (file_size > 30000000) { //判断图片文件是否大于30M
				PicPath = path + map.get("filepath") + "/" + file_name.replace(".", "_compression.");//压缩图片文件路径
				File file = new File(PicPath);
				if (!file.exists()) { //判断该大图片是否已经压缩（若已经压缩了就不用再次压缩）
					Boolean flag = true;
					for (Thread t : Thread.getAllStackTraces().keySet()) {
						if (t.getName().equals(file_name.replace(".", "_compression."))) {
							flag = false;
							t.join();
							break;
						}
					}
					if (flag) {
						String dir = ImageProcessorPath + "/ImageProcessor_X64.exe" + " " + mediaPath + " " + PicPath + " 50 20 ";
						RuntimeUntil runtime = new RuntimeUntil(file_name.replace(".", "_compression."), dir, "压缩");
						runtime.start();
						runtime.join();
					}
				}
			}
		}
		InputStream inputStream = null;
		BufferedInputStream bis = null;
		OutputStream out = null;
		BufferedOutputStream bos = null;
		try {
			File html_file = new File(PicPath == "" ? mediaPath : PicPath);
			inputStream = new FileInputStream(html_file);
			bis = new BufferedInputStream(inputStream);
			out = response.getOutputStream();
			bos = new BufferedOutputStream(out);
			// 下载的字节范围
			int startByte,endByte,totalByte;
			if (request != null && request.getHeader("range") != null) {
				//分片下载
				String[] range = request.getHeader("range").replaceAll("[^0-9\\-]", "").split("-");
				// 文件总大小
				totalByte = inputStream.available();
				// 下载起始位置
				startByte = Integer.parseInt(range[0]);
				// 下载结束位置
				if (range.length > 1) {
					endByte = Integer.parseInt(range[1]);
				} else {
					endByte = totalByte - 1;
				}
				// 返回http状态
				response.setStatus(206);
			} else {
				// 正常下载
				// 文件总大小
				totalByte = inputStream.available();
				// 下载起始位置
				startByte = 0;
				// 下载结束位置
				endByte = totalByte - 1;
				// 返回http状态
				response.setHeader("Accept-Ranges","bytes");
				response.setStatus(200);
			}
			// 需要下载字节数
			int length = endByte - startByte + 1;
			// 响应头
			if (file_type != null && !"pdf".equals(file_type.toLowerCase())) {
				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + getOutName(request, file_name) + "\"");
			}
			response.setHeader("Accept-Ranges", "bytes");
			response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + totalByte);
			response.setContentLength(length);
			response.setContentType("Content-Type: application/octet-stream");
			// 响应内容
			bis.skip(startByte);
			int len = 0;
			byte[] buff = new byte[1024*8];
			while ((len = bis.read(buff, 0, buff.length)) != -1) {
				if (length <= len) {
					bos.write(buff, 0, length);
					break;
				} else {
					length -= len;
					bos.write(buff, 0, len);
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(bos != null){
					bos.flush();
					bos.close();
				}
				if(out!=null){
					out.flush();
					out.close();
				}
				if(bis != null){
					bis.close();
				}
				if(inputStream!=null){
					inputStream.close();
				}
			}catch (IOException e){

			}
		}

	}

	@RequestMapping("/loadMediaFocus")
	public void loadMediaFocus(HttpServletRequest request, HttpServletResponse response, String eleid)
			throws Exception {
		Tb_focus focus = electronicService.getFocus(eleid);
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=1.pdf");
		response.setContentType("application/" + focus.getPath().substring(focus.getPath().lastIndexOf(".") + 1));
		// response.setHeader("Content-disposition","attachment;filename=t1.doc"
		// );
		ServletOutputStream out;
		String mediaPath = rootpath + focus.getPath();
		File html_file = new File(mediaPath);
		if (!html_file.exists()) {
			String relativelyPath = System.getProperty("user.dir");
			String newPath = relativelyPath + "/src/main/resources/static/img/focus/no.jpg";
			File html_file_new = new File(newPath);
			FileInputStream inputStream = new FileInputStream(html_file_new);
			out = response.getOutputStream();

			int b = 0;
			byte[] buffer = new byte[1024];
			while ((b = inputStream.read(buffer)) != -1) {
				out.write(buffer, 0, b);
			}

			inputStream.close();
			out.flush();
			out.close();
		} else {
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
	}

	@RequestMapping("/showUserimg")
	public String mediaUserimg(Model model) {
		model.addAttribute("imgsrc", "/electronic/outputUserimg");
		return "/inlet/mediaUserimg";
	}

	@RequestMapping("/outputUserimg")
	public void loadMediaUserimg(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Tb_Personalized personalized = personalizedRepository.findByUserid(userDetails.getUserid());
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=1.png");
		// response.setContentType("application/" +
		// personalized.getPath().substring(personalized.getPath().lastIndexOf(".")
		// + 1));
		// response.setHeader("Content-disposition","attachment;filename=t1.doc"
		// );
		ServletOutputStream out;
//        账号头像缺失时，用默认图标。
        String relativePath = System.getProperty("user.dir");
        String mediaPath = relativePath + "/src/main/resources/static/img/user_default.png";
		if(personalized!=null && personalized.getPath()!=null && !"".equals(personalized.getPath())){
            mediaPath = rootpath + "/" +personalized.getPath() + "/" + personalized.getTitle();
        }
		File html_file = new File(mediaPath);
		if(html_file.exists()){}
		else {
            mediaPath = relativePath + "/src/main/resources/static/img/user_default.png";
            html_file = new File(mediaPath);
        }
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

	/**
	 * 删除焦点图
	 *
	 * @param eleids
	 *            节点元素id
	 * @return
	 */
	@RequestMapping(value = "/electronicsFocusDel")
	@ResponseBody
	public ExtMsg electronicsFocusDel(String[] eleids) {
		int i = electronicService.electronicsFocusDel(eleids);
		ExtMsg msg = null;
		if (i > 0)
			msg = new ExtMsg(true, "删除成功", null);
		else
			msg = new ExtMsg(false, "删除失败", null);
		return msg;
	}

	@RequestMapping(value = "/getFocus")
	@ResponseBody
	public List<Tb_focus> getFocus() {
		return electronicService.getFocus();
	}

	@RequestMapping("/saveUserimgInfo")
	@ResponseBody
	public void saveUserimgInfo(String filename) {
		electronicService.saveUserimgInfo(filename);
	}

	/**
	 * 原始文件排序
	 * 
	 * @param eleids
	 *            需要排序的电子文件
	 * @param entryType
	 *            数据类型（采集、管理、利用）
	 * @return 排序状态信息
	 */
	@RequestMapping(value = "/mediaFileSort")
	@ResponseBody
	public ExtMsg mediaFileSort(String[] eleids, String entryType) {
		try {
			electronicService.mediaFileSort(eleids, entryType);
			return new ExtMsg(true, "操作成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			return new ExtMsg(false, "操作失败", null);
		}
	}

	@RequestMapping(value = "/getSxElectronicByEntryid")
	@ResponseBody
	public ExtMsg mediaFileSort(String entryid) {
		Tb_electronic_browse_sx ecList = sxElectronicBrowseRepository.findByEntryid(entryid);
		return new ExtMsg(true, "", ecList);
	}

	@RequestMapping(value="/loadSpecialMedia")
	public void loadSpecialMedia(HttpServletRequest request, HttpServletResponse response,
								 String eleid, String entryid, String fileType) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String defaultImg = "";
		if ("photo".equals(fileType)) {
			defaultImg = "/static/img/defaultMedia/default_img.jpg";
		} else if ("video".equals(fileType)) {
			defaultImg = "/static/img/defaultMedia/default_video.jpg";
		} else if ("audio".equals(fileType)) {
			defaultImg = "/static/img/defaultMedia/default_audio.png";
		}
		FileInputStream inputStream = null;
		ByteArrayOutputStream output = null;
		OutputStream outStream = null;
		BufferedOutputStream bufferOut = null;
		Tb_electronic_browse_sx eb = null;
		File file=null;
		try {
			if (entryid != null && !"".equals(entryid)) {
				eb = sxElectronicBrowseRepository.findByEntryid(entryid);
			} else if (eleid != null && !"".equals(eleid)) {
				eb = sxElectronicBrowseRepository.findByEleid(eleid);
			}
			if (eb != null) {//打开了nginx服务 还是用流传给前台
				if("".equals(fileType)||fileType==null)fileType="jpg,jpeg,png,bmp".indexOf(eb.getFiletype().toLowerCase())>-1?"photo":fileType;
				if ("video".equals(fileType)) {
					try {
						String path = mediaServerPath + eb.getFilepath() + "/" + eb.getFilename();
						response.sendRedirect(new String(path.getBytes("utf-8"), "iso-8859-1"));
						file = new File(browsepath + eb.getFilepath() + "/" + eb.getFilename());
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if("photo".equals(fileType)){//若是照片的话先看看是否要求添加水印
//					String orginid=userService.getUserOrgan(userDetails.getLoginname());
//					Tb_watermark tb_watermark=watermarkService.getWatermarkByOrgan(orginid);
//					if(tb_watermark==null)tb_watermark=watermarkService.getWatermarkByOrgan("0");
//					if(tb_watermark!=null){//有记录 需要在照片中添加水印
//						if("0".equals(tb_watermark.getIspicture())){//文字水印
//							if(eb.getFilename().endsWith("_WM.jpg")){//已有水印图片
								file = new File(browsepath + eb.getFilepath() + "/" + eb.getFilename());
							//}
						//}
//					}else{
//						file = new File(browsepath + eb.getFilepath() + "/" + eb.getFilename());
//					}
				}else{
					file = new File(browsepath + eb.getFilepath() + "/" + eb.getFilename());
				}

			} else {
				Resource resource = new ClassPathResource(defaultImg);
				file = resource.getFile();
			}
			System.out.println("----------------图片地址："+file);
			if (!file.exists()) {
				Resource resource = new ClassPathResource(defaultImg);
				file = resource.getFile();
				System.out.println("<<<<<<" + file.getPath());
			}
			inputStream = new FileInputStream(file);
			output = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];
			int rc;
			while (-1 != (rc = inputStream.read(buff))) {
				output.write(buff, 0, rc);
			}
			output.flush();
			byte[] downByte = output.toByteArray();
			outStream = response.getOutputStream();
			bufferOut = new BufferedOutputStream(outStream);
			bufferOut.write(downByte);
			bufferOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
				if (output != null)
					output.close();
				if (outStream != null)
					outStream.close();
				if (bufferOut != null)
					bufferOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@RequestMapping(value = "/getBrowseByEntryid")
	@ResponseBody
	public ExtMsg getBrowseByEntryid(String entryid, String eleid) {
		Tb_electronic_browse_sx eb = new Tb_electronic_browse_sx();
		if (entryid != null && !"".equals(entryid)) {
			eb = sxElectronicBrowseRepository.findByEntryid(entryid);
		} else if (eleid != null && !"".equals(eleid)) {
			eb = sxElectronicBrowseRepository.findByEleid(eleid);
		}
		eb.setFilepath(mediaServerPath + eb.getFilepath());
		return new ExtMsg(true, "", eb);
	}

	/**
	 * 移动焦点图
	 *
	 * @param eleids
	 *            节点元素id
	 * @return
	 */
	@RequestMapping(value = "/electronicsFocusChange")
	@ResponseBody
	public ExtMsg electronicsFocusChange(String[] eleids) {
		try {
			electronicService.electronicsFocusChange(eleids);
			return new ExtMsg(true, "操作成功", null);
		} catch (Exception e) {
			e.printStackTrace();
			return new ExtMsg(false, "操作失败", null);
		}
	}
	@RequestMapping(value = "/geteleids")
	@ResponseBody
	public List<String> getEleids(String borrowcodeid, String entryid) {
		Tb_borrowdoc borrowdoc = borrowDocRepository.findByDocid(borrowcodeid);
		List<String> eleids = textOpenRepository.findEleidsByborrowcode(borrowdoc.getBorrowcode(),entryid);
		return eleids;
	}
	@RequestMapping("/loadMediaThematic")
	public void loadMediaThematic(HttpServletRequest request, HttpServletResponse response, String eleid,String type)
			throws Exception {
		String backgroundpath="";
		if("ly".equals(type)){
			Tb_thematic_make thematic = electronicService.getThematicMake(eleid);
			if(thematic==null){
				Tb_thematic thematict = electronicService.getThematic(eleid);
				if(thematict==null){
					return;
				}else{
					backgroundpath= thematict.getBackgroundpath();
				}
			}else{
				backgroundpath= thematic.getBackgroundpath();
			}
		}else{
			Tb_thematic thematic = electronicService.getThematic(eleid);
			if(thematic==null){
				return;
			}
			backgroundpath= thematic.getBackgroundpath();
		}

		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=1.pdf");
		response.setContentType("application/" + backgroundpath.substring(backgroundpath.lastIndexOf(".") + 1));
		// response.setHeader("Content-disposition","attachment;filename=t1.doc"
		// );
		ServletOutputStream out;
		String mediaPath = rootpath + backgroundpath;
		File html_file = new File(mediaPath);
		if (!html_file.exists()) {
			String relativelyPath =  request.getSession().getServletContext().getRealPath("/"); ;
			String newPath = relativelyPath + "/WEB-INF/classes/static/img/focus/no.jpg";
			File html_file_new = new File(newPath);
			FileInputStream inputStream = new FileInputStream(html_file_new);
			out = response.getOutputStream();

			int b = 0;
			byte[] buffer = new byte[1024];
			while ((b = inputStream.read(buffer)) != -1) {
				out.write(buffer, 0, b);
			}

			inputStream.close();
			out.flush();
			out.close();
		} else {
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
	}

	/**
	 * 公告富文本图
	 *
	 * @param eleid
	 *            图片id
	 * @return
	 */
	@RequestMapping("/loadMediaInform")
	public void loadMediaInform(HttpServletRequest request, HttpServletResponse response, String eleid)
			throws Exception {
		Tb_electronic_access tea = electronicAccessRepository.findByEleid(eleid);
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=1.pdf");
		response.setContentType("application/" + tea.getFilename().substring(tea.getFilename().lastIndexOf(".") + 1));
		ServletOutputStream out;
		String mediaPath = rootpath + tea.getFilepath()+ File.separator +tea.getFilename();
		File html_file = new File(mediaPath);
		if (!html_file.exists()) {
			String relativelyPath =  request.getSession().getServletContext().getRealPath("/"); ;
			String newPath = relativelyPath + "/WEB-INF/classes/static/img/focus/no.jpg";
			File html_file_new = new File(newPath);
			FileInputStream inputStream = new FileInputStream(html_file_new);
			out = response.getOutputStream();

			int b = 0;
			byte[] buffer = new byte[1024];
			while ((b = inputStream.read(buffer)) != -1) {
				out.write(buffer, 0, b);
			}

			inputStream.close();
			out.flush();
			out.close();
		} else {
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
	}
	
	/**
	 * 查档
	 *
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @throws Exception
	 */
	@RequestMapping(value = "/electronicsBorrow", method = RequestMethod.POST)
	@ResponseBody
	public void electronicsBorrow(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> params = parse(request);
		if ((boolean) params.get("mutipart")) {
			if (params.get("chunk") != null) { // 文件分片上传
				electronicService.uploadchunkBorrow(params);
			} else { // 文件单片上传
				electronicService.uploadfileBorrow(params);
			}
		}
	}

	@RequestMapping(value = "/ztelectronics/borrow/{entrytype}/{entryid}/{filename}", method = RequestMethod.POST)
	@ResponseBody
	public synchronized ExtMsg ztuploadmodifyBorrow(@PathVariable String entrytype, @PathVariable String entryid,
													@PathVariable String filename) {
		Map<String, Object> map = electronicService.saveZtElectronicBorrow(entrytype, entryid, filename);
		ExtMsg msg = new ExtMsg(true, "", map);
		return msg;
	}

	@RequestMapping(value = "/ztelectronics/borrow/{entrytype}/{filename}", method = RequestMethod.POST)
	@ResponseBody
	public synchronized ExtMsg ztuploadBorrow(@PathVariable String entrytype, @PathVariable String filename) {
		Map<String, Object> map = electronicService.saveZtElectronicBorrow(entrytype,null, filename);
		ExtMsg msg = new ExtMsg(true, "", map);
		return msg;
	}

	@RequestMapping("/getByeleid")
	@ResponseBody
	public ExtMsg getByeleid(String[] eleids){
		if(eleids != null){
			List<Tb_electronic> eleList = electronicRepository.findByEleidIn(eleids);
			return  new ExtMsg(true,"",eleList);
		}
		else{
			return  new ExtMsg(true, "", "");
		}
	}

    //打印
    @RequestMapping("/printYWMedia")
    public void printYWMedia(HttpServletRequest request, HttpServletResponse response, String entrytype,
                             String eleid,String lyqx,String btnType,String path,String mType) throws Exception {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> map = electronicService.findElectronic(entrytype, eleid);
        String file_name = (String) map.get("filename");
        String file_type = (String) map.get("filetype");
        response.setCharacterEncoding("UTF-8");

        response.setContentType("application/" + file_type);
        ServletOutputStream out;
        String mediaPath = rootpath + map.get("filepath") + "/" + file_name;
        if("pdf".equals(file_type)&&("lyjylook".equals(mType)||"gljylook".equals(mType))){   //是否pdf文件
            String organname = userDetails.getOrganid();
            String unitName = userDetails.getAddress();
            String ip = LogAop.getIpAddress(); //获取访客ip
            watermarktext = unitName + "-" + organname + "-" + ip;

            Tb_watermark watermark = watermarkService.getWatermarkByOrgan(userDetails.getReplaceOrganid());//根据机构id获取水印配置信息
            if (watermark == null) {
                watermark = watermarkService.getWatermarkByOrgan("0");//若所属机构没有设置水印则使用全局水印
            }
            String waterFilePath = watermarkUtil.getWatermarkPdf(watermark,mediaPath,userDetails.getLoginname(),2052,request);
            mediaPath = waterFilePath;
        }
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

    //批量打印
    @RequestMapping("/batchprint")
    public void batchprint(HttpServletRequest request, HttpServletResponse response,Model model, String[] eleid,String datatype,String mType,String xtType) throws Exception{
        ImageUtil imageUtil = new ImageUtil();
        String[] files = new String[eleid.length];
        List filespath = new ArrayList();
		String fileRootPath=rootpath;
        if("capture".equals(datatype)){
            List<Tb_electronic_capture> eleCapList=electronicCaptureRepository.findByEleidInOrderByFilename(eleid);
            for(int i=0;i<eleCapList.size();i++){
                String filepath =  fileRootPath + eleCapList.get(i).getFilepath() + "/" + eleCapList.get(i).getFilename();
                files[i]=filepath;
            }
        }
        else if("solid".equals(datatype)){
        	if("声像系统".equals(xtType)){
				fileRootPath=browsepath;
        		List<Sort.Order> sorts = new ArrayList<>();
				sorts.add(new Sort.Order(Sort.Direction.ASC,"sequence"));//默认字段排序
				sorts.add(new Sort.Order(Sort.Direction.ASC,"filename"));//文件名排序
				List<Tb_electronic_browse_sx> eleSolidList = sxElectronicBrowseRepository.findByEleidIn(eleid,new Sort(sorts));
				for (int i = 0; i < eleSolidList.size(); i++) {
					String filepath = fileRootPath + eleSolidList.get(i).getFilepath() + "/" + eleSolidList.get(i).getFilename();
					files[i] = filepath;
				}

			}else {
				List<Tb_electronic_solid> eleSolidList = electronicSolidRepository.findByEleidInOrderByFilename(eleid);
				if (eleSolidList.size() == 0) {//借阅-已通过-查看电子原文
					List<Tb_electronic> eleList = electronicRepository.findByEleidInOrderByFilename(eleid);
					for (int i = 0; i < eleList.size(); i++) {
						String filepath = fileRootPath + eleList.get(i).getFilepath() + "/" + eleList.get(i).getFilename();
						files[i] = filepath;
					}
				} else {
					for (int i = 0; i < eleSolidList.size(); i++) {
						String filepath = fileRootPath + eleSolidList.get(i).getFilepath() + "/" + eleSolidList.get(i).getFilename();
						files[i] = filepath;
					}
				}
			}
        }
        else {
            List<Tb_electronic> eleList=electronicRepository.findByEleidInOrderByFilename(eleid);
            for(int i=0;i<eleList.size();i++){
                String filepath =  fileRootPath + eleList.get(i).getFilepath() + "/" + eleList.get(i).getFilename();
                files[i]=filepath;
            }
        }

        String folder = fileRootPath+"/electronics/batchPrint";
        String mergeFileName = "final.pdf";
        PdfUtil pdfUtil = new PdfUtil();
        pdfUtil.deleteFile(folder);

        ExecutorService exe = Executors.newFixedThreadPool(files.length);//创建线程池
        for (int i = 0; i < files.length; i++) {
            String file = files[i];
            if (file.toLowerCase().endsWith(".png") || file.toLowerCase().endsWith(".jpg") || file.toLowerCase().endsWith(".gif") || file.toLowerCase().endsWith(".jpeg") || file.toLowerCase().endsWith(".gif")) {
                BufferedImage bi = imageUtil.rotateImage(file);
                if (bi == null)
                    continue;

                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String pdffile = pdfUtil.Image2PDF(file, bi, folder);
                            filespath.add(pdffile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                Thread thread=new Thread(task);
                exe.execute(thread);
            }
            else if(file.toLowerCase().endsWith(".pdf")){
                filespath.add(file);
            }
        }
        exe.shutdown();
        while (true) {
            if (exe.isTerminated()) {
                System.out.println("图片转PDF----------线程池结束了！");
                break;
            }
            Thread.sleep(200);
        }
        String[] filesPathArray = new String[filespath.size()];
        filespath.toArray(filesPathArray);
        Arrays.sort(filesPathArray);
        pdfUtil.mergePDF(filesPathArray, folder, mergeFileName);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/" + "pdf");
        ServletOutputStream out;
        String mediaPath = folder + "/" + "final.pdf";//文件路径


        //只在利用平台的-借阅-已通过-查看电子原文加水印
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if("lyjylook".equals(mType)||"gljylook".equals(mType)){
            Tb_watermark watermark = watermarkService.getWatermarkByOrgan(userDetails.getReplaceOrganid());//根据机构id获取水印配置信息
            if (watermark == null) {
                watermark = watermarkService.getWatermarkByOrgan("0");//若所属机构没有设置水印则使用全局水印
            }
            String waterFilePath = watermarkUtil.getWatermarkPdf(watermark,mediaPath,userDetails.getLoginname(),2052,request);
            mediaPath = waterFilePath;
        }
//        String organname = userDetails.getOrganid();
//        String unitName = userDetails.getAddress();
//        String ip = LogAop.getIpAddress(); //获取访客ip
//        watermarktext = unitName + "-" + organname + "-" + ip;
//
//        String waterFilePath = WatermarkUtil.setWatermarkText(2052, mediaPath,
//                watermarkpath + "\\" + userDetails.getUsername() + "\\1.pdf", watermarktext, null, "5", 0.5f, 50,"black", true);
//        mediaPath = waterFilePath;


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

	/**
	 * 水印上传
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/watermarkElectronics", method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg watermarkElectronics(HttpServletRequest request, HttpServletResponse response) throws
			Exception {
		Map<String, Object> params = parse(request);
		if ((boolean) params.get("mutipart")) {
			if (params.get("chunk") != null) {  //文件分片上传
				electronicService.uploadchunk(params);
			} else {      //文件单片上传
				return electronicService.watermarkElectronics(params);
			}
		}
		//Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
		return new ExtMsg(false, "操作失败", null);
	}

	@RequestMapping("/watermarkloadMedia")
	public void watermarkloadMedia(HttpServletRequest request, HttpServletResponse response, String watermarkPath) throws Exception {

		response.setCharacterEncoding("UTF-8");
		ServletOutputStream out;
		String mediaPath = rootpath + watermarkPath;
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

	@RequestMapping("/getPages")
	@ResponseBody
	public ExtMsg getPages(HttpServletRequest request,HttpServletResponse response,String[] filefnids,String entryid , String entrytype) {
		int pages = 0;
		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> mapList = new ArrayList<>();
		for (String eleid : filefnids) {
			map = electronicService.findElectronic(entrytype, eleid);
			mapList.add(map);
		}
		boolean ifFileExists = true;
		String notExistsFilesStr = "";
		for (Map<String, Object> resultMap : mapList) {
			String filename = (String) resultMap.get("filename");
			String filetype = filename.substring(filename.lastIndexOf(".") + 1);
			String filepath = rootpath + resultMap.get("filepath") + "/" + filename;
			File file = new File(filepath);
			String eleid =(String) resultMap.get("eleid");
			if (!file.exists()) {
				if (notExistsFilesStr.length() > 0) {
					notExistsFilesStr += "、";
				}
				notExistsFilesStr += "“" + filename + "”";
				ifFileExists = false;
			}
			else {
			  int page = electronicService.readPages(filetype,file,filepath);
			  electronicService.setPages(eleid, String.valueOf(page),entrytype); //设置电子原文表的页数
              pages += page;
			}
		}
		if (ifFileExists) {
			return new ExtMsg(true, "获取成功", pages);
		} else {
			return  new ExtMsg(false, "本地文件" + notExistsFilesStr + "不存在！", pages);
		}
	}

	@RequestMapping("/setPages")
	@ResponseBody
	public void setPages(String entryid,String pages,String entrytype){//设置条目表的总页数

		if(entrytype.equals("management")){
			entryIndexService.setPagesbyEntryid(entryid,pages);
		}
		else if(entrytype.equals("capture")){
			entryIndexCaptureService.setPagesbyEntryid(entryid,pages);
		}
	}


	@RequestMapping(value = "/electronics/management/Version/tree", method = RequestMethod.GET)
	@ResponseBody
	public List<ExtTree> findElectronicVersionManageTree(String eleversionid,String eletype) {
		ExtTree returnTree = new ExtTree();
		ExtTree node = new ExtTree();
		if("capture".equals(eletype)){
			Tb_electronic_version_capture electronic_version = electronicService.findEleCaptureVersion(eleversionid);
			node.setFnid(electronic_version.getId());
			node.setText(electronic_version.getFilename());
			node.setLeaf(true);
		}else {
			Tb_electronic_version electronic_version = electronicService.findElectronicVersion(eleversionid);
			node.setFnid(electronic_version.getId());
			node.setText(electronic_version.getFilename());
			node.setLeaf(true);
		}
		ExtTree[] extTrees;
		if (returnTree.getChildren() != null) {// 添加子节点
			extTrees = Arrays.copyOf(returnTree.getChildren(), returnTree.getChildren().length + 1);
			extTrees[returnTree.getChildren().length] = node;
		} else {
			extTrees = new ExtTree[1];
			extTrees[0] = node;
		}
		returnTree.setChildren(extTrees);
		List<ExtTree> extTreeList = new ArrayList<>();
		if (returnTree.getChildren() != null) {
			for (ExtTree extTree : returnTree.getChildren()) {
				extTreeList.add(extTree);
			}
		}
		return extTreeList;
	}

	@RequestMapping("/verMedia")
	public String mediaVersion(Model model,String eleid, String filetype,String entrytype) {
		List<String> typeValidation = Arrays.asList("doc","docx","xls","xlsx","ppt","pptx","dwg");
		String showPage = "/inlet/media";
		if(typeValidation.contains(filetype)){
			showPage = "/inlet/officeMedia1";
		}
		model.addAttribute("filetype", filetype);
		if("management".equals(entrytype)){
			model.addAttribute("imgsrc", "/electronic/versionMedia?eleid=" + eleid);
		}else{
			model.addAttribute("imgsrc", "/electronic/versionMediaCapture?eleid=" + eleid);
		}
		return showPage;
	}

	@RequestMapping("/versionMedia")
	public void loadMediaVersion(HttpServletRequest request, HttpServletResponse response,String eleid)
			throws Exception {
		Tb_electronic_version electronic_version = electronicService.findElectronicVersion(eleid);
		String file_name = electronic_version.getFilename();
		String file_type = electronic_version.getFiletype();
		// response.setContentType("multipart/form-data");
		response.setCharacterEncoding("UTF-8");
		// response.setContentType("text/html");
		// response.setHeader("Content-Type", "image/jpg");
		if (file_type != null && !"pdf".equals(file_type.toLowerCase())) {
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + getOutName(request, file_name) + "\"");
		}
		// response.setHeader("Content-Disposition","attachment;
		// filename=1.pdf");
		response.setContentType("application/" + file_type);
		// response.setHeader("Content-disposition","attachment;filename=t1.doc"
		// );
		ServletOutputStream out;
		String mediaPath = rootpath + electronic_version.getFilepath() + "/" + file_name;
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

	@RequestMapping("/versionMediaCapture")
	public void loadMediaVersionCapture(HttpServletRequest request, HttpServletResponse response,String eleid)
			throws Exception {
		Tb_electronic_version_capture electronic_version = electronicService.findEleCaptureVersion(eleid);
		String file_name = electronic_version.getFilename();
		String file_type = electronic_version.getFiletype();
		// response.setContentType("multipart/form-data");
		response.setCharacterEncoding("UTF-8");
		// response.setContentType("text/html");
		// response.setHeader("Content-Type", "image/jpg");
		if (file_type != null && !"pdf".equals(file_type.toLowerCase())) {
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + getOutName(request, file_name) + "\"");
		}
		// response.setHeader("Content-Disposition","attachment;
		// filename=1.pdf");
		response.setContentType("application/" + file_type);
		// response.setHeader("Content-disposition","attachment;filename=t1.doc"
		// );
		ServletOutputStream out;
		String mediaPath = rootpath + electronic_version.getFilepath() + "/" + file_name;
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

	//初始化页面
	@RequestMapping("/fileCheckerMain")
	public String fileCheckerMain(Model model, String isp){
		Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
		model.addAttribute("functionButton", functionButton);
		return "/inlet/fileChecker";
	}
	//获取所有文件的存储记录
	@RequestMapping("/getElectronics")
	@ResponseBody
	public Page<Tb_electronic> getElectronics(int page, int limit) {
		Page<Tb_electronic> list = electronicService.getElectronics(page, limit);
		return list;
	}

	@RequestMapping("/checkfile")
	@ResponseBody
	public Integer checkFile()  throws FileNotFoundException, IOException {
		Integer isChecking = electronicService.checkFile();
		return isChecking;
	}

	@RequestMapping("/isChecking")
	@ResponseBody
	public Integer isChecking(){
		Integer isChecking = electronicService.isChecking();
		return isChecking;
	}

	@RequestMapping("/checkfileResult")
	@ResponseBody
	public Map<String,Object> checkfileResult()  throws FileNotFoundException, IOException {

		return electronicService.getFileCheckResult();
	}
	@RequestMapping("/getDataMap")
	@ResponseBody
	public Map<String,Object> getDataMap(int currentPage, int PageSize)  throws FileNotFoundException, IOException {

		return electronicService.getDataMap(currentPage, PageSize);
	}

	@RequestMapping(value = "/ztelectronics/borrowApprove/{borrowcode}/{filename}", method = RequestMethod.POST)
	@ResponseBody
	public synchronized ExtMsg ztuploadmodifyBorrowApprove(@PathVariable String borrowcode,
													@PathVariable String filename) {
		Map<String, Object> map = electronicService.saveZtElectronicBorrowApprove(borrowcode, filename);
		ExtMsg msg = new ExtMsg(true, "", map);
		return msg;
	}

	@RequestMapping(value = "/ztelectronics/deleteApproveEle/{eleids}", method = RequestMethod.DELETE)
	@ResponseBody
	public ExtMsg deleteApproveEle(@PathVariable String eleids) {
		Integer num = electronicService.deleteApproveEle(eleids);
		if (num > 0) {
			return new ExtMsg(true, "删除成功", eleids);
		}
		return new ExtMsg(false, "删除失败", null);
	}

	//申请管理-查看原文打印
	@RequestMapping("/mediaPrint")
	public void mediaPrint(Model model, String entrytype, String eleid, String mType,HttpServletResponse response,HttpServletRequest request) throws IOException{
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Map<String, Object> map = electronicService.findElectronic(entrytype, eleid);
		String file_name = (String) map.get("filename");
		String file_type = (String) map.get("filetype");
		response.setCharacterEncoding("UTF-8");

		response.setContentType("application/" + file_type);
		ServletOutputStream out;
		String mediaPath = rootpath + map.get("filepath") + "/" + file_name;
		//if("pdf".equals(file_type)&&("lyjylook".equals(mType)||"gljylook".equals(mType))){   //是否pdf文件
			String organname = userDetails.getOrganid();
			String unitName = userDetails.getAddress();
			String ip = LogAop.getIpAddress(); //获取访客ip
			watermarktext = unitName + "-" + organname + "-" + ip;

			Tb_watermark watermark = watermarkService.getWatermarkByOrgan(userDetails.getReplaceOrganid());//根据机构id获取水印配置信息
			if (watermark == null) {
				watermark = watermarkService.getWatermarkByOrgan("0");//若所属机构没有设置水印则使用全局水印
			}
			String waterFilePath = watermarkUtil.getWatermarkPdf(watermark,mediaPath,userDetails.getLoginname(),2052,request);
			mediaPath = waterFilePath;
		//}
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

	@RequestMapping("/loadMediaPrint")
	public void loadMediaPrint(HttpServletRequest request, HttpServletResponse response, String entrytype, String eleid)
			throws InterruptedException {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Map<String, Object> map = electronicService.findElectronic(entrytype, eleid);
		String PicPath = "";
		String file_name = (String) map.get("filename");
		type = (String) map.get("filename");
		String file_type = (String) map.get("filetype");
		Integer file_size = Integer.parseInt((String) map.get("filesize"));
		String mediaPath = rootpath + map.get("filepath") + "/" + file_name;//图片文件路径
		response.setCharacterEncoding("UTF-8");
		FileInputStream inputStream = null;
		ServletOutputStream out = null;
		try {
			if (file_type != null && !"pdf".equals(file_type.toLowerCase())) {
				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + getOutName(request, file_name) + "\"");
			}
			response.setContentType("application/" + file_type);
			if("true".equals(printWatermark)){
				String organId = userService.getUserOrgan(userDetails.getLoginname());
				Tb_watermark watermark = watermarkService.getWatermarkByOrgan(organId);//根据机构id获取水印配置信息
				if(watermark==null){
					watermark = watermarkService.getWatermarkByOrgan("0");//若所属机构没有设置水印则使用全局水印
				}
				if("1".equals(watermark.getIsmanage())){
				mediaPath = watermarkUtil.getWatermarkPdf(watermark,mediaPath,userDetails.getLoginname(),2052,request);
				}
			}
			File html_file = new File(mediaPath);
			inputStream = new FileInputStream(html_file);
			out = response.getOutputStream();

			int b = 0;
			byte[] buffer = new byte[1024];
			while ((b = inputStream.read(buffer)) != -1) {
				out.write(buffer, 0, b);
			}
			inputStream.close();
			out.flush();
			out.close();
		}catch (IOException e) {

		}finally {
			try {
				inputStream.close();
				out.flush();
				out.close();
			}catch (IOException e){

			}
		}
	}

	@RequestMapping(value = "/szhelectronics/tree/{entryid}", method = RequestMethod.GET)
	@ResponseBody
	public List<ExtTree> findSzhElectronicTree( @PathVariable String entryid) {
		List<Map<String, Object>> list;
		list = electronicService.findSzhElectronics(entryid);
		ExtTree returnTree = new ExtTree();
		for (Map<String, Object> map : list) {
			if (map.get("folder") != null && !"".equals(map.get("folder").toString().trim())) {
				String path = map.get("folder").toString().substring(1);
				String[] folder = path.split("/");
				ExtTree tempTree = newTemp(folder, map);
				returnTree = insertTree(returnTree, tempTree);
			} else {
				ExtTree node = new ExtTree();
				node.setFnid((String) map.get("eleid"));
				node.setText((String) map.get("filename"));
				node.setLeaf(true);

				ExtTree[] extTrees;
				if (returnTree.getChildren() != null) {// 添加子节点
					extTrees = Arrays.copyOf(returnTree.getChildren(), returnTree.getChildren().length + 1);
					extTrees[returnTree.getChildren().length] = node;
				} else {
					extTrees = new ExtTree[1];
					extTrees[0] = node;
				}
				returnTree.setChildren(extTrees);
			}
		}
		List<ExtTree> extTreeList = new ArrayList<>();
		if (returnTree.getChildren() != null) {
			for (ExtTree extTree : returnTree.getChildren()) {
				extTreeList.add(extTree);
			}
		}
		return extTreeList;
	}

	//判断是否档案节点
	@RequestMapping(value = "/checkMedia",method = RequestMethod.GET)
	@ResponseBody
	public ExtMsg checkMedia(String nodeid){
		Tb_data_node_mdaflag dataNode  = dataNodeExtRepository.findNodeid(nodeid);
		String msg="1";
		if(dataNode == null ) {
			msg="0";//0表示不是声像档案
		}
		return new ExtMsg(true,msg,null);
	}

	@RequestMapping("/saveSetPages")
	@ResponseBody
	public ExtMsg saveSetPages(String eleids,String entrytype) {
		int pages = 0;
		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> mapList = new ArrayList<>();
		if(!"".equals(eleids)&&eleids!=null){
			String[] filefnids = eleids.split(",");
			for (String eleid : filefnids) {
				map = electronicService.findElectronic(entrytype, eleid);
				mapList.add(map);
			}
		}
		for (Map<String, Object> resultMap : mapList) {
			String filename = (String) resultMap.get("filename");
			String filetype = filename.substring(filename.lastIndexOf(".") + 1);
			String filepath = rootpath + resultMap.get("filepath") + "/" + filename;
			File file = new File(filepath);
			String eleid =(String) resultMap.get("eleid");
			if (file.exists()) {
				int page = electronicService.readPages(filetype,file,filepath);
				electronicService.setPages(eleid, String.valueOf(page),entrytype); //设置电子原文表的页数
				pages += page;
			}
		}
		return new ExtMsg(true, "获取成功", pages);
	}


	@RequestMapping("/findByEleid")
	@ResponseBody
	public Tb_electronic findByEleid(String eleid){
		Tb_electronic electronic = electronicRepository.findByEleid(eleid);
		return electronic;
	}

	/**
	 * 工作监督管理
	 *
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @throws Exception
	 */
	@RequestMapping(value = "/electronicsSupervisionWork", method = RequestMethod.POST)
	@ResponseBody
	public void electronicsSupervisionWork(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> params = parse(request);
		if ((boolean) params.get("mutipart")) {
			if (params.get("chunk") != null) { // 文件分片上传
				electronicService.uploadchunkSupervisionWork(params);
			} else { // 文件单片上传
				electronicService.uploadfileSupervisionWork(params);
			}
		}
	}

	@RequestMapping(value = "/electronics/supervisionWork/{organid}/{selectyear}/{savetype}/{filename}", method = RequestMethod.POST)
	@ResponseBody
	public synchronized ExtMsg uploadmodifySupervisionWork(@PathVariable String organid, @PathVariable String selectyear,@PathVariable String savetype,
														   @PathVariable String filename) {
		Map<String, Object> map = electronicService.saveElectronicSupervisionWork(organid,selectyear,savetype,filename);
		ExtMsg msg = new ExtMsg(true, "", map);
		return msg;
	}

	@RequestMapping(value = "/supervisionWork/tree/{organid}/{selectyear}/{savetype}", method = RequestMethod.GET)
	@ResponseBody
	public List<ExtTree> findElectronicTree(@PathVariable String organid, @PathVariable String selectyear,
											@PathVariable String savetype) {
		List<Map<String, Object>> list = electronicService.findElectronicSupervisionWorks(organid, selectyear, savetype);
		ExtTree returnTree = new ExtTree();
		for (Map<String, Object> map : list) {
			if (map.get("folder") != null && !"".equals(map.get("folder").toString().trim())) {
				String path = map.get("folder").toString().substring(1);
				String[] folder = path.split("/");
				ExtTree tempTree = newTemp(folder, map);
				returnTree = insertTree(returnTree, tempTree);
			} else {
				ExtTree node = new ExtTree();
				node.setFnid((String) map.get("eleid"));
				node.setText((String) map.get("filename"));
				node.setLeaf(true);

				ExtTree[] extTrees;
				if (returnTree.getChildren() != null) {// 添加子节点
					extTrees = Arrays.copyOf(returnTree.getChildren(), returnTree.getChildren().length + 1);
					extTrees[returnTree.getChildren().length] = node;
				} else {
					extTrees = new ExtTree[1];
					extTrees[0] = node;
				}
				returnTree.setChildren(extTrees);
			}
		}
		List<ExtTree> extTreeList = new ArrayList<>();
		if (returnTree.getChildren() != null) {
			for (ExtTree extTree : returnTree.getChildren()) {
				extTreeList.add(extTree);
			}
		}
		return extTreeList;
	}

	@RequestMapping("/supervisionMedia")
	public String supervisionMedia(Model model, String entrytype, String eleid, String filetype) {
		List<String> typeValidation = Arrays.asList("doc","docx","xls","xlsx","ppt","pptx","dwg");
		String showPage = "/inlet/media";
		if(typeValidation.contains(filetype)){
			showPage = "/inlet/officeMedia1";
		}
		model.addAttribute("filetype", filetype);
		if("txt".equals(filetype)){
			model.addAttribute("imgsrc", "/electronic/mediaNative?entrytype=" + entrytype + "&eleid=" + eleid);
			return showPage;
		}
		else if (entrytype != null) {
			model.addAttribute("imgsrc", "/electronic/loadMedia?entrytype=" + entrytype + "&eleid=" + eleid);
			model.addAttribute("itemclickDownload",itemclickDownload);
			return showPage;
		} else {
			model.addAttribute("imgsrc", "/electronic/loadMediaFocus?eleid=" + eleid);
			return showPage;
		}
	}

	@RequestMapping(value = "/supervisionWork/{eleids}", method = RequestMethod.DELETE)
	@ResponseBody
	public ExtMsg deleteElectronicSupervisionWork(@PathVariable String eleids) {
		Integer num = electronicService.deleteElectronicSupervisionWork( eleids);
		if (num > 0) {
			return new ExtMsg(true, "删除成功", eleids);
		}
		return new ExtMsg(false, "删除失败", null);
	}

	//上传完修改电子文件顺序号
	@RequestMapping(value = "/updateSortElectronics")
	@ResponseBody
	public ExtMsg updateSortElectronics(String entrytype,String entryid, String[] remainEleids) {
		electronicService.updateSortElectronics(entryid,entrytype, remainEleids);
		return new ExtMsg(true,"",null);
	}

	/**
	 * 要把实体id获取其所有电子文件
	 * @param entrytype 类型（采集、管理或固化等）
	 * @param entryid 实体id
	 * @return
	 */
	@RequestMapping(value = "/getEles",method = RequestMethod.GET)
	@ResponseBody
	public ExtMsg getEleId(String entrytype,String entryid){
		List eles = electronicService.findEle(entrytype, entryid);
		return new ExtMsg(true,"OK",eles);
	}

	//重命名文件夹
	@RequestMapping(value = "/updateFolderName",method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg updateFolderName(String eleId,String name){
		if(electronicService.updateFolderName(eleId, name)>0){
			return new ExtMsg(true,"重命名成功",null);
		}else {
			return new ExtMsg(false,"重命名失败",null);
		}
	}
}