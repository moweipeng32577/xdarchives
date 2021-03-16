package com.wisdom.web.controller;

import com.wisdom.util.LogAnnotation;
import com.wisdom.util.WatermarkUtil;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_watermark;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.WatermarkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xd on 2017/9/27.
 */
@Controller
@RequestMapping(value = "/watermark")
public class WatermarkController {

    private static Map<String,Tb_watermark> userWatermarkMap = new HashMap<>();//预览用

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${system.document.rootpath}")
    private String rootpath;// 系统文件根目录

    @Autowired
    WatermarkService watermarkService;

    @Autowired
    WatermarkUtil watermarkUtil;

    @RequestMapping("/main")
    public String organ() {
        return "inlet/watermark";
    }

    /**
     * 按机构获取水印数据
     * @param page 页码
     * @param limit 分页数
     * @param condition 查询字段
     * @param operator  条件
     * @param content 查询内容
     * @param organid 机构id
     * @return
     */
    @RequestMapping("/findWatermarkBySearch")
    @ResponseBody
    public Page<Tb_watermark> findWatermarkBySearch(int page, int limit, String condition, String operator, String content, String organid) {
        return watermarkService.findBySearch(page, limit, condition, operator, content, organid);
    }

    @RequestMapping("/getWatermark")
    @ResponseBody
    public ExtMsg getWatermark(String id) {
        return watermarkService.getWatermark(id);
    }

    /**
     * 保存水印条目数据
     * @param watermark 水印条目数据
     * @return
     */
    @LogAnnotation(module="系统设置-水印管理",sites = "1",fields = "title",connect = "##名称",startDesc = "操作水印，条目详细：")
    @RequestMapping("/saveWatermark")
    @ResponseBody
    public ExtMsg saveWatermark(Tb_watermark watermark) {
        return watermarkService.saveWatermark(watermark);
    }


    /**
     * 删除水印数据
     * @param ids 需要删除的水印数据id
     * @param paths 需要删除的水印数据id
     * @return
     */
    @LogAnnotation(module="系统设置-水印管理",sites = "1",startDesc = "删除水印，条目编号：")
    @RequestMapping("/delWatermarks")
    @ResponseBody
    public ExtMsg delWatermarks(String[] ids, String[] paths) {
        return watermarkService.delWatermarks(ids,paths);
    }

    @RequestMapping("/previewWatermark")
    @ResponseBody
    public ExtMsg previewWatermark(Tb_watermark watermark){
        boolean state = false;
        try{
            SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 系统绑定对象(全局)
            userWatermarkMap.put(userDetails.getLoginname(),watermark);
            state = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ExtMsg(state,null,null);
    }

    @RequestMapping("/media")
    public String media(Model model,String type,String number) {
        model.addAttribute("filetype", "pdf");
        model.addAttribute("imgsrc", "/watermark/preview?type="+type+"&number="+number);
        return "/inlet/media";
    }

    @RequestMapping("/preview")
    public void preview(HttpServletRequest request, HttpServletResponse response,String type) throws Exception {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 系统绑定对象(全局)
        ServletOutputStream out;
        response.setCharacterEncoding("UTF-8");
        Resource resource = new ClassPathResource("/static/doc/用户手册-利用平台.pdf");
        String mediaPath = resource.getFile().getPath();
        String waterFilePath = "";
        if("nowatermark".equals(type)){  //新增没有水印
            waterFilePath = mediaPath;
        }else{
            Tb_watermark watermark = userWatermarkMap.get(userDetails.getLoginname());
            waterFilePath = watermarkUtil.getWatermarkPdf(watermark,mediaPath,userDetails.getLoginname(),2052,request);
        }
        File html_file = new File(waterFilePath);
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
