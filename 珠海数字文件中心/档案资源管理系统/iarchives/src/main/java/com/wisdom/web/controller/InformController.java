package com.wisdom.web.controller;

import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.DateUtil;
import com.wisdom.util.LogAnnotation;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.ElectronicAccessRepository;
import com.wisdom.web.repository.InFormUserRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.ElectronicService;
import com.wisdom.web.service.InformService;
import org.apache.catalina.core.ApplicationPart;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 公告管理控制器 Created by Administrator on 2017/11/4 0004.
 */
@Controller
@RequestMapping(value = "/inform")
public class InformController {
	
	@Autowired
	InFormUserRepository inFormUserRepository;

	@Autowired
	InformService informService;

	@Autowired
	WebSocketService webSocketService;

	@Autowired
	ElectronicService electronicService;

	@Autowired
	ElectronicAccessRepository electronicAccessRepository;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${system.document.rootpath}")
	private String rootpath;//系统文件根目录

	@RequestMapping("/main")
	public String inform(Model model, String flag) {
		model.addAttribute("buttonflag", flag);
		return "/inlet/inform";
	}

	// 解决利用平台与管理平台公用页面权限控制问题
	@RequestMapping("/mainly")
	public String informly(Model model, String flag) {
		model.addAttribute("buttonflag", flag);
		return "/inlet/inform";
	}

	@RequestMapping("/htmledit")
	public String htmledit() {
		return "/inlet/htmledit";
	}

	@RequestMapping("/getInform")
	@ResponseBody
	public ExtMsg getInform(String id) {
		return new ExtMsg(true, "", informService.getInform(id));
	}

	@RequestMapping("/getInforms")
	@ResponseBody
	public Page<Tb_inform> getInforms(String flag, int page, int start, int limit, String condition, String operator,
			String content, String sort) {
		Sort sortobj = WebSort.getSortByJson(sort);
		logger.info("flag:" + flag + ";page:" + page + ";start:" + start + ";limt:" + limit);
		return informService.findBySearchInforom(condition, operator, content, flag, page, limit, sortobj);
	}

	/**
	 *
	 * @param title
	 *            标题
	 * @param limitdate
	 *            到期时间
	 * @param html
	 *            公告内容
	 * @param postedUser
	 *            是否推送至用户
	 * @param postedUsergroup
	 *            是否推送至用户组
	 * @return
	 */
	@LogAnnotation(module="档案系统-公告管理",sites = "1",fields = "title",connect = "##标题",startDesc = "添加公告，条目详细：")
	@RequestMapping("/addInform")
	@ResponseBody
	public ExtMsg addInform(String title, String limitdate, String html, String[] eleids, String postedUser,
			String postedUserids, String postedUsergroup, String postedUsergroupids) {
		Tb_inform tb_inform = informService.addInform(title, limitdate, html, eleids, postedUser, postedUserids,
				postedUsergroup, postedUsergroupids);
		if (tb_inform != null) {
			return new ExtMsg(true, "公告添加成功", tb_inform.getId());
		}
		return new ExtMsg(false, "公告添加失败", null);
	}

	/**
	 * 公告修改
	 * 
	 * @param id
	 * @param title
	 * @param limitdate
	 * @param eleids
	 * @param html
	 * @return
	 */
	@LogAnnotation(module="档案系统-公告管理",sites = "2",fields = "title",connect = "##标题",startDesc = "修改公告，条目详细：")
	@RequestMapping("/editInform")
	@ResponseBody
	public ExtMsg editInform(String id, String title, String limitdate, String[] eleids, String html) {
		Tb_inform inform = new Tb_inform();
		inform.setId(id);
		try {
			inform.setLimitdate(new SimpleDateFormat("yyyyMMddHHmmss").parse(limitdate + "235959"));
		} catch (ParseException px) {
			px.printStackTrace();
		}
		inform.setTitle(title);
		inform.setText(html);
		Tb_inform inform_full = informService.editInform(inform, eleids);
		if (inform_full != null) {
			webSocketService.noticeRefresh();
			return new ExtMsg(true, "公告修改成功", null);
		}
		return new ExtMsg(false, "公告修改失败", null);
	}
	
	@RequestMapping("/clearInform")
    @ResponseBody
    public ExtMsg clearInform(String id) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Integer value = inFormUserRepository.updateStateByInfomidAndUserroleid(id, userDetails.getUserid());
		if(value > 0){
            return new ExtMsg(true,"清除成功",null);
        }
        return new ExtMsg(false,"清除失败",null);
	}
	
	@RequestMapping("/getPosteds")
	@ResponseBody
	public List getPosteds(String flag, String organid) {
		return informService.getPostedUser(flag, organid);
	}
	
	@RequestMapping("/updatePosteds")
	@ResponseBody
	public List<String> updatePosteds(String flag, String organid, String userid) {
		// 获取树节点的所有用户
		List<ExtNcTree> users = informService.getPostedUser(flag, organid);
		List<String> ids = new ArrayList<>();
		for (int i = 0; i < users.size(); i++) {
			ids.add(users.get(i).getFnid());
		}
		String[] idInfo = userid.split(",");
		ids.retainAll(Arrays.asList(idInfo));
        return ids;
	}

	/**
	 * 获取公告已推送用户或用户组
	 * 
	 * @param id
	 *            公告id
	 * @param flag
	 *            用户还是用户组标识
	 * @return
	 */
	@RequestMapping("/getHasPosteds")
	@ResponseBody
	public ExtMsg getHasPosteds(String id, String flag) {
		String[] result = informService.getHasPosteds(id, flag);
		if (result != null) {
			return new ExtMsg(true, "获取公告已推送用户或用户组成功", result);
		}
		return new ExtMsg(false, "获取公告已推送用户或用户组失败", new String[] { "" });
	}

	/**
	 * 发布公告到用户或用户组
	 * 
	 * @param id
	 *            公告id
	 * @param posteds
	 *            用户或用户组id
	 * @param flag
	 *            用户或用户组标识
	 * @return
	 */
	@RequestMapping("/postedInform")
	@ResponseBody
	public ExtMsg postedInform(String id, String[] posteds, String flag) {
		String postedStrs = "";
		if (posteds != null) {
			for (int i = 0; i < posteds.length; i++) {
				if (i == 0) {
					postedStrs += posteds[i];
				} else {
					postedStrs += "∪" + posteds[i];
				}
			}
		}
		Map<String, Object> resultMap = informService.postedInform(id, posteds, flag);
		if (resultMap == null) {// 已选为空
			webSocketService.noticeRefresh();
			return new ExtMsg(false, "取消发布", null);
		} else {// 已选不为空
			if (resultMap.get("list") != null) {// 列表按钮发布（直接发布）
				webSocketService.noticeRefresh();
				return new ExtMsg(true, "发布成功", null);
			} else {// 表单按钮发布（未直接发布，先将发布信息返回）
				if ("usergroupselect".equals(flag)) {// 用户组
					return new ExtMsg(true, "设置用户组推送成功，请点击提交按钮完成发布", resultMap.get("role") + "," + postedStrs);
				}
				if ("userselect".equals(flag)) {// 用户
					return new ExtMsg(true, "设置用户推送成功，请点击提交按钮完成发布", resultMap.get("user") + "," + postedStrs);
				}
			}
		}
		return null;
	}

	/**
	 * 删除公告
	 * 
	 * @param ids
	 *            公告id数组
	 * @return
	 */
	@LogAnnotation(module="档案系统-公告管理",sites = "1",startDesc = "删除公告，条目编号：")
	@RequestMapping("/informDel")
	@ResponseBody
	public ExtMsg informDel(String[] ids) {
		Integer del = informService.informDel(ids);
		if (del != 0) {
			// 删除成功后，通知页面刷新信息。删除的公告可能是发布中
			webSocketService.noticeRefresh();
			return new ExtMsg(true, "删除成功", null);
		}
		return new ExtMsg(false, "删除失败", null);
	}

	/**
	 *
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @throws Exception
	 */
	@RequestMapping(value = "/electronicsInform", method = RequestMethod.POST)
	@ResponseBody
	public void electronicsInform(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> params = parse(request);
		if ((boolean) params.get("mutipart")) {
			if (params.get("chunk") != null) { // 文件分片上传
				informService.uploadchunk(params);
			} else { // 文件单片上传
				informService.uploadfileInform(params);
			}
		}
	}

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

	@RequestMapping(value = "/electronics/{filename}", method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg uploadztadd(@PathVariable String filename) {
		Map<String, Object> map = informService.saveElectronic(null, filename);
		ExtMsg msg = new ExtMsg(true, "", map);
		return msg;
	}

	@RequestMapping(value = "/electronics/{informid}/{filename}", method = RequestMethod.POST)
	@ResponseBody
	public synchronized ExtMsg ztuploadmodify(@PathVariable String informid, @PathVariable String filename) {
		Map<String, Object> map = informService.saveElectronic(informid, filename);
		ExtMsg msg = new ExtMsg(true, "", map);
		return msg;
	}

	@RequestMapping(value = "/electronics/{eleids}", method = RequestMethod.DELETE)
	@ResponseBody
	public ExtMsg deletefile(@PathVariable String eleids) {
		Integer num = informService.deleteElectronic(eleids);
		if (num > 0) {
			return new ExtMsg(true, "删除成功", num);
		}
		return new ExtMsg(false, "删除失败", null);
	}

	@RequestMapping(value = "/electronicsFile/{informid}", method = RequestMethod.POST)
	@ResponseBody
	public List<Tb_electronic> electronicsFile(@PathVariable String informid) {
		return informService.getInformFile(informid);
	}

	@RequestMapping("/openFile")
	@ResponseBody
	public void openSipFile(String eleid, String fileName, HttpServletRequest request, HttpServletResponse response) {
		String path = informService.getInformFilePath(eleid);
		try {
			File html_file = new File(path);
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + new String(fileName.getBytes("GBK"), "ISO-8859-1") + "\"");
			response.setContentType("application/octet-stream");
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

	/**
	 * 公告置顶
	 * @param ids    公告ids
	 * @param level 公告等级
	 * @return      返回信息
	 */
	@RequestMapping("/informStick")
	@ResponseBody
	public ExtMsg informStick(String[] ids,String level) {
		ExtMsg msg = new ExtMsg(true,"置顶成功",null);
		try {
			informService.informStick(ids,level);
			webSocketService.noticeRefresh();
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg("置顶失败");
		}
		return msg;
	}

	/**
	 * 取消公告置顶
	 * @param ids    公告ids
	 * @return      返回信息
	 */
	@RequestMapping("/cancelStick")
	@ResponseBody
	public ExtMsg cancelStick(String[] ids) {
		ExtMsg msg = new ExtMsg(true,"取消置顶成功",null);
		try {
			boolean state = informService.cancelStick(ids);
			if(state){
				webSocketService.noticeRefresh();
			}else{
				msg.setMsg("非置顶公告");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg.setMsg("取消置顶失败");
		}
		return msg;
	}

	//上传图片
	@RequestMapping("/upload")
	@ResponseBody
	public Map<String, String> upload(@RequestParam("fileToUpload") MultipartFile[] multfiles) {
		return uploadImage(multfiles);
	}

	public Map<String,String> uploadImage(MultipartFile[] multfiles){
		Map<String, String> result = new HashMap<>();
		if (multfiles.length == 0) {
			result.put("message", "请选择图片！");
			return result;
		}

		// 源文件名称
		final String originalFileName = multfiles[0].getOriginalFilename();
		if (StringUtils.isBlank(originalFileName)) {
			result.put("message", "请选择图片！");
			return result;
		}

		// 文件后缀[.jpg]
		final String suffix = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
		/*if (!FileUtil.IMAGE_EXTENSIONS.contains(suffix)) {
			result.put("message", "图片格式错误！");
			return result;
		}*/

		String lastFilePath;
		String newFileName = originalFileName;
		String folderName = File.separator + "temp" + File.separator;
		String relativePath = folderName;
		String filePath = electronicService.getTemporaryInform();//公告富文本相对路径文件夹
		String fileUrl = null;
		File targetFile = new File(rootpath+filePath);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		FileOutputStream out = null;
		try {
			lastFilePath =rootpath+filePath + File.separator + newFileName;
			out = new FileOutputStream(lastFilePath);
			out.write(multfiles[0].getBytes());
			//更新Tb_electronic_access作为富文本电子文件记录表
			Tb_electronic_access tea=new Tb_electronic_access();
			tea.setFilename(newFileName);
			tea.setFilepath(filePath);
			tea=electronicAccessRepository.save(tea);
			fileUrl ="/electronic/loadMediaInform?eleid="+tea.getEleid()+"";
			//fileUrl ="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589950959856&di=604c85f8a855fe13c44c1b68c1d65ccb&imgtype=0&src=http%3A%2F%2Fa0.att.hudong.com%2F27%2F10%2F01300000324235124757108108752.jpg";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		if (fileUrl == null) {
			result.put("message", "图片上传失败！");
			return result;
		}

		result.put("message", "uploadSuccess");
		result.put("file", fileUrl);
		return result;
	}
}
