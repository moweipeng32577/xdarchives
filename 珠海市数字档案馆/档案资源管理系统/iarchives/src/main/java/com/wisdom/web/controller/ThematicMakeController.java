package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.util.FunctionUtil;
import com.wisdom.util.LogAnnotation;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_datareceive;
import com.wisdom.web.entity.Tb_thematic_make;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.service.ThematicMakeService;
import com.wisdom.web.service.ThematicService;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 专题制作控制器
 * Created by yl on 2017/10/27.
 */
@Controller
@RequestMapping(value = "/thematicMake")
public class ThematicMakeController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ThematicMakeService thematicService;

    @Autowired
    ThematicMakeService thematicMakeService;

    @Autowired
    ThematicProdController thematicProdController;

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录


    /**
    * 返回专题制作主页并带上该用户权限下可以使用的功能Btn
    *
    * @param model
    * @param isp 功能父节点
    * @return {@link java.lang.String}
    * @throws
    */
    @RequestMapping("/main")
    public String thematicManagementMain(Model model, String isp) {
        Object functionBtn= JSON.toJSON(FunctionUtil.getQxFunction(isp));
        model.addAttribute("functionBtn",functionBtn);
        return "/inlet/thematicManagement";
    }


    @RequestMapping("/getThematic")
    @ResponseBody
    public Page<Tb_thematic_make> getThematic(int page, int start, int limit,String condition, String operator, String content, String sort) {
        logger.info("page:" + page + "start:" + start + "limt:" + limit);
        Sort sortobj = WebSort.getSortByJson(sort);
        Page<Tb_thematic_make> thematics = thematicService.findBySearch(page, limit, condition, operator, content, sortobj);
        logger.info(thematics.toString());
        return thematics;
    }


    /**
    * 利用系统获取已发布专题
    *
    * @param
    * @return {@link List< Tb_thematic_make>}
    * @throws
    */
    @RequestMapping(value = "/getThematicByStatus",method = RequestMethod.GET)
    @ResponseBody
    public List<Tb_thematic_make> getThematicByStatus(){
        return thematicMakeService.getThematicbyState();
    }

    @LogAnnotation(module = "档案系统-专题制作", sites = "1", fields = "title", connect = "##题名；", startDesc = "新增操作，条目详情：")
    @RequestMapping("/saveThematic")
    @ResponseBody
    public ExtMsg saveThematic(@ModelAttribute("form") Tb_thematic_make Tb_thematic_make,String backgroundpath) {
        ExtMsg extMsg=null;
        //专题名称不能重复
        if(thematicService.findByTitle(Tb_thematic_make.getTitle())!=null){
            extMsg=new ExtMsg(false,"保存失败,专题名称不能重复",null);
        }else{
            Tb_thematic_make save = thematicService.saveThematic(Tb_thematic_make,backgroundpath);
            if(save!=null){
                extMsg=new ExtMsg(true,"保存成功",null);
            }else{
                extMsg=new ExtMsg(false,"保存失败",null);
            }
        }
        return extMsg;
    }

    @LogAnnotation(module = "档案系统-专题制作", sites = "1",startDesc = "删除操作，条目编号：")
    @RequestMapping("/deleteThematic")
    @ResponseBody
    public ExtMsg deleteThematic(String[] thematicids){
        int count = thematicService.deleteThematic(thematicids);
        ExtMsg extMsg=null;
        if(count>0){
            extMsg=new ExtMsg(true,"删除成功",null);
        }else{
            extMsg=new ExtMsg(false,"删除失败",null);
        }
        return extMsg;
    }


    @LogAnnotation(module = "档案系统-专题制作", sites = "1", fields = "title", connect = "##题名；", startDesc = "修改操作，条目详情：")
    @RequestMapping("/updateThematic")
    @ResponseBody
    public ExtMsg updateThematic(@ModelAttribute("form") Tb_thematic_make thematic_make, String thematicid,String backgroundpath){
        ExtMsg extMsg=null;
        Tb_thematic_make Tb_thematic_make = thematicService.findByThematicid(thematicid);
        //判断专题名称是否被修改过、专题名称是否重复
        if(!thematic_make.getTitle().equals(Tb_thematic_make.getTitle())&&thematicService.findByTitle(thematic_make.getTitle())!=null){
            extMsg=new ExtMsg(false,"保存失败,专题名称不能重复",null);
        }else{
            int count=thematicService.updataThematic(thematic_make.getTitle(),thematic_make.getThematiccontent(),thematic_make.getThematictypes(),backgroundpath,thematicid);
            if(count>0){
                extMsg=new ExtMsg(true,"修改成功",null);
            }else{
                extMsg=new ExtMsg(false,"修改失败",null);
            }
        }
        return extMsg;
    }

    @RequestMapping("/releaseThematic")
    @ResponseBody
    public ExtMsg releaseThematic(String thematicids) {
        ExtMsg extMsg = null;
        if (thematicService.updateThematicForPublishstate("已发布", thematicids) > 0) {
            extMsg = new ExtMsg(true, "发布成功", null);
        } else {
            extMsg = new ExtMsg(false, "发布失败", null);
        }
        return extMsg;
    }

    @RequestMapping("/cancleReleaseThematic")
    @ResponseBody
    public ExtMsg cancleReleaseThematic(String thematicids) {
        ExtMsg extMsg = null;
        if (thematicService.updateThematicForPublishstate("", thematicids) > 0) {
            extMsg = new ExtMsg(true, "取消发布成功", null);
        } else {
            extMsg = new ExtMsg(false, "取消发布失败", null);
        }
        return extMsg;
    }

    @RequestMapping(value = "/electronicsBackground", method = RequestMethod.POST)
    @ResponseBody
    public void electronicsBackground(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> params = parse(request);
        if ((boolean) params.get("mutipart")) {
            if (params.get("chunk") != null) {  //文件分片上传
                thematicService.uploadchunk(params);
            } else {      //文件单片上传
                thematicService.uploadfileInform(params);
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
    public ExtMsg uploadztadd( @PathVariable String filename) {
        return new ExtMsg(true, "", thematicService.getBackgroundPath(filename));
    }

    @RequestMapping(value = "/getBackground")
	public void getBackground(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
		FileInputStream inputStream = null;
		try {
			File file = null;
			if (url.indexOf("thematic_def.png") > -1) {
				Resource resource = new ClassPathResource(url);
				file = resource.getFile();
			} else {
				String mediaPath = rootpath + url;
				file = new File(mediaPath);
			}

			inputStream = new FileInputStream(file);
			ServletOutputStream out = response.getOutputStream();

			int b = 0;
			byte[] buffer = new byte[1024];
			while ((b = inputStream.read(buffer)) != -1) {
				out.write(buffer, 0, b);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			FileInputStream is = null;
			try {
				Resource resource = new ClassPathResource("/static/img/icon/thematic_def.png");
				File file = resource.getFile();
				is = new FileInputStream(file);
				ServletOutputStream out = response.getOutputStream();

				int b = 0;
				byte[] buffer = new byte[1024];
				while ((b = is.read(buffer)) != -1) {
					out.write(buffer, 0, b);
				}
				out.flush();
				out.close();
			} catch (IOException e1) {
				logger.error(e1.getMessage());
			} finally {
				if (is != null) {
					is.close();
				}
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

    /**
     * 发布到政务网
     *
     * @param thematicIds
     * @return
     */
    @RequestMapping("/releasenetwork")
    @ResponseBody
    public ExtMsg releasenetwork(String thematicIds,Tb_datareceive datareceive,HttpServletRequest request) {
        String path=thematicService.releasenetwork(thematicIds,datareceive);
        request.getSession().setAttribute("releasenetZipPath",path);
        return new ExtMsg(true, "发布成功", path);
    }

    @RequestMapping("/downLoadReleasenetwork")
    @ResponseBody
    public void downLoad(HttpServletResponse response, HttpServletRequest request) {
        String zipPath=(String)request.getSession().getAttribute("releasenetZipPath");
        File html_file = new File(zipPath);
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename="
                    + new String(zipPath.substring(zipPath.lastIndexOf("\\") + 1).getBytes("gbk"), "iso8859-1"));
            response.setContentType("application/zip");
            ServletOutputStream out;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发布数据包单据
     *
     * @param thematicIds
     * @return
     */
    @RequestMapping("/getThematicDoc")
    @ResponseBody
    public ExtMsg getThematicDoc(String thematicIds) {
        Tb_datareceive datareceive = thematicMakeService.getThematicDoc(thematicIds);
        return new ExtMsg(true, "成功", datareceive);
    }
}