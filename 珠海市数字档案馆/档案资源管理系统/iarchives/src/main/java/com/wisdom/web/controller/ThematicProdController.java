package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.util.FunctionUtil;
import com.wisdom.util.LogAnnotation;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_thematic;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.repository.ThematicRepository;
import com.wisdom.web.service.ElectronicService;
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
import java.util.*;

/**
 * 专题制作控制器
 * Created by yl on 2017/10/27.
 */
@Controller
@RequestMapping(value = "/thematicProd")
public class ThematicProdController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserController userController;

    @Autowired
    ThematicService thematicService;

    @Autowired
    ThematicRepository thematicRepository;

    @Autowired
    ElectronicService electronicService;

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录

    @Value("${system.nginx.browse.path}")//浏览文件路径
    private String browsepath;
    @RequestMapping("/main")
    public String main(Model model) {
        Object wjqxFunctionButton = JSON.toJSON(userController.getWJQXbtn());//文件权限
        model.addAttribute("wjqxFunctionButton", wjqxFunctionButton);
        return "/inlet/thematicProd";
    }

    @RequestMapping("/getThematic")
    @ResponseBody
    public Page<Tb_thematic> getThematic(int page, int start, int limit,String condition, String operator, String content, String sort,String state) {
        logger.info("page:" + page + "start:" + start + "limt:" + limit);
        Sort sortobj = null;
        if(sort==null) {//默认按章节排序
            List<Sort.Order> sorts = new ArrayList<>();
            if(state!=null && !"".equals(state)){
                sorts.add(new Sort.Order(Sort.Direction.DESC, "submitedtime"));
            }else {
                sorts.add(new Sort.Order(Sort.Direction.DESC, "createtime"));
            }
            sortobj=new Sort(sorts);
        }else {
            sortobj= WebSort.getSortByJson(sort);;
        }
        Page<Tb_thematic> thematics;
        if(state!=null && !"".equals(state)){
            thematics = thematicService.findBySearchByState(page,limit, condition, operator, content, sortobj,state);
        }else {
            thematics = thematicService.findBySearch(page, limit, condition, operator, content, sortobj);
        }
        logger.info(thematics.toString());
        return thematics;
    }
    @LogAnnotation(module="编研系统管理-专题制作",sites = "1",fields = "title",connect = "##专题名称",startDesc = "增加专题，条目详细：")
    @RequestMapping("/saveThematic")
    @ResponseBody
    public ExtMsg saveThematic(@ModelAttribute("form") Tb_thematic tb_thematic,String backgroundpath) {
        ExtMsg extMsg=null;
        //专题名称不能重复
        if(thematicService.findByTitle(tb_thematic.getTitle())!=null){
            extMsg=new ExtMsg(false,"保存失败,专题名称不能重复",null);
        }else{
            Tb_thematic save = thematicService.saveThematic(tb_thematic,backgroundpath);
            if(save!=null){
                extMsg=new ExtMsg(true,"保存成功",null);
            }else{
                extMsg=new ExtMsg(false,"保存失败",null);
            }
        }
        return extMsg;
    }

    @LogAnnotation(module="编研系统管理-专题制作",sites = "1",startDesc = "删除专题，条目编号：")
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
    @LogAnnotation(module="编研系统管理-专题制作",sites = "1",fields = "title",connect = "##专题名称",startDesc = "修改专题:条目详细")
    @RequestMapping("/updateThematic")
    @ResponseBody
    public ExtMsg updateThematic(@RequestParam("title") String title,@RequestParam("thematiccontent") String thematiccontent,
                                 @RequestParam("thematictypes") String thematictypes, String thematicid,String backgroundpath){
        ExtMsg extMsg=null;
        Tb_thematic tb_thematic = thematicService.findByThematicid(thematicid);
        //判断专题名称是否被修改过、专题名称是否重复
        if(!title.equals(tb_thematic.getTitle())&&thematicService.findByTitle(title)!=null){
            extMsg=new ExtMsg(false,"保存失败,专题名称不能重复",null);
        }else{
            int count=thematicService.updataThematic(title,thematiccontent,thematictypes,backgroundpath,thematicid);
            if(count>0){
                extMsg=new ExtMsg(true,"修改成功",null);
            }else{
                extMsg=new ExtMsg(false,"修改失败",null);
            }
        }
        return extMsg;
    }

    public void downLoad(HttpServletResponse response, String zipPath) {
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
        } finally {
            html_file.delete();
        }
    }

    /**
    * 发布专题
    *
    * @param thematicids
    * @return {@link ExtMsg}
    * @throws
    **/
    @RequestMapping(value = "/releaseThematicStuats",method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg releaseThematicById(String thematicids){
       if(thematicService.releaseThmatic(thematicids,"已发布")&&thematicService.updateThematicForPublish("已发布", thematicids)>0){
           return new ExtMsg(true, "发布成功", null);
       }else{
           return new ExtMsg(false,"发布失败",null);
       }
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

    //发布数字资源zip包
    @RequestMapping("/releaseThematic")
    @ResponseBody
    public void releaseThematic(String thematicids,HttpServletResponse response) {
        String zipPath = thematicService.releasenetwork(thematicids);
        downLoad(response,zipPath);
    }

    //发布数字资源zip包
    @RequestMapping("/releasenetworkMake")
    @ResponseBody
    public void releasenetworkMake(String thematicids,HttpServletResponse response) {
        String zipPath = thematicService.releasenetworkMake(thematicids);
        downLoad(response,zipPath);
    }

    @RequestMapping(value = "/electronics/{filename}", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg uploadztadd( @PathVariable String filename) {
        return new ExtMsg(true, "", thematicService.getBackgroundPath(filename));
    }

    @RequestMapping(value = "/getBackground")
    public void getBackground(HttpServletRequest request, HttpServletResponse response, String url, String address,String isCombined,String type) throws IOException {
        FileInputStream inputStream = null;
        try {
            File file = null;
            if (url.indexOf("thematic_def.png") > -1) {
                Resource resource = new ClassPathResource(url);
                file = resource.getFile();
            } else if (url.indexOf("/static/img/defaultMedia") > -1) {
                Resource resource = new ClassPathResource(url);
                file = resource.getFile();
            } else {
                String mediaPath = "";
                if("upThematic".equals(type)){  //判断是不是专题利用背景图
                    mediaPath = rootpath + url;//todo//待处理
                }else{
                    mediaPath = browsepath + url;//todo//待处理
                    //如果未找到文件
                    if(!new File(mediaPath).exists()){
                        mediaPath = rootpath + url;
                    }
                }
                if (isCombined != null && isCombined.equals("1")) {
                    mediaPath = mediaPath.substring(0, mediaPath.lastIndexOf(".")) + "_combined.jpg";
                    System.out.println(mediaPath);
                }
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
                String defaultBg = "/static/img/icon/thematic_def.png";
                if ("photo".equals(address)) {
                    defaultBg = "/static/img/defaultMedia/default_img.jpg";
                }else if ("video".equals(address)) {
                    defaultBg = "/static/img/defaultMedia/default_video.jpg";
                }else if ("audio".equals(address)) {
                    defaultBg = "/static/img/defaultMedia/default_audio.png";
                }else if("unit".equals(address)){
                    defaultBg = "/static/img/defaultMedia/default_unit.png";
                }
                Resource resource = new ClassPathResource(defaultBg);
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

    @RequestMapping(value = "/resultSubmit")
    @ResponseBody
    public ExtMsg resultSubmit(String thematicids) {
        if(thematicService.updateThmaticStatus(thematicids)){
            //审核提醒通知
            String[] ids=thematicids.split(",");
            thematicService.task(ids,"分管领导审核");
            return new ExtMsg(true, "提交成功", null);
        }else{
            return new ExtMsg(false,"提交失败",null);
        }
    }
}