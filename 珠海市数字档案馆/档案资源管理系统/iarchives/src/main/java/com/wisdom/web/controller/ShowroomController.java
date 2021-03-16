package com.wisdom.web.controller;

import com.wisdom.util.LogAnnotation;
import com.wisdom.util.LogAop;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.ElectronicAccessRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.ElectronicService;
import com.wisdom.web.service.ShowroomService;
import com.wisdom.web.service.ShowroomService;
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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 展厅管理控制器
 * Created by zdw on 2020/03/20
 */
@Controller
@RequestMapping(value = "/showroom")
public class ShowroomController {

    @Autowired
    LogAop logAop;

    @Autowired
    ShowroomService showroomService;

    @Autowired
    ElectronicService electronicService;

    @Autowired
    ElectronicAccessRepository electronicAccessRepository;

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/main")
    public String showroom(Model model, String flag) {
        model.addAttribute("buttonflag",flag);
        return "/inlet/showroom";
    }

    @RequestMapping("/mainly")
    public String showroomly(Model model, String flag) {
        model.addAttribute("buttonflag",flag);
        return "/inlet/showroom";
    }

    @RequestMapping("/getShowroom")
    @ResponseBody
    public Page<Tb_showroom> getShowroom(String type,int page, int start, int limit, String condition,String operator,String content,String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        logger.info("page:" + page + ";start:" + start + ";limt:" + limit);
        return showroomService.findBySearch(type,condition,operator,content,page,limit,sortobj);
    }

    @RequestMapping("/getDateShowroom")
    @ResponseBody
    public Page<Tb_showroom> getDateShowroom(String date,int page, int start, int limit){
        logger.info("page:" + page + ";start:" + start + ";limt:" + limit);
        return showroomService.findByDateSearch(date,page,limit);
    }

    @RequestMapping(value = "/showrooms/{showroomid}", method = RequestMethod.GET)
    @ResponseBody
    public ExtMsg getShowroom(@PathVariable String showroomid){
        return new ExtMsg(true, "", showroomService.getShowroom(showroomid));
    }

    @RequestMapping(value = "/showrooms/{showroomids}", method = RequestMethod.DELETE)
    @ResponseBody
    public ExtMsg delShowroom(@PathVariable String showroomids) {
        String startTime = LogAop.getCurrentSystemTime();//开始时间
        long startMillis = System.currentTimeMillis();//开始毫秒数
        String[] showroomidData = showroomids.split(",");
        Integer del = showroomService.delShowroom(showroomidData);
        for(String showroomid:showroomidData){
            logAop.generateManualLog(startTime,LogAop.getCurrentSystemTime(),System.currentTimeMillis()-startMillis,"展厅管理","删除展厅记录操作，展厅id为："+showroomid);
        }
        if (del > 0) {
            return new ExtMsg(true, "删除成功", del);
        }
        return new ExtMsg(false, "删除失败", null);
    }

    @RequestMapping("/htmledit")
    public String htmledit() {
        return "/inlet/htmledit";
    }

    //@LogAnnotation(module = "展厅管理",sites = "1",fields = "title,askman",connect = "##标题；,##投件人；",startDesc = "增加展厅操作，展厅信息详情：")
    @RequestMapping(value = "/showrooms", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg saveShowroom(Tb_showroom showroom, String[] eleids){
        Tb_showroom result = showroomService.saveShowroom(showroom, eleids);
        if(result != null){
            return new ExtMsg(true,"保存成功",result);
        }
        return new ExtMsg(false,"保存失败",null);
    }

    /**
     * 展厅修改
     *
     * @param eleids
     * @return
     */
    @RequestMapping("/editShowroom")
    @ResponseBody
    public ExtMsg editInform(Tb_showroom showroom, String[] eleids) {
        showroom =  showroomService.editShowroom(showroom, eleids);
        if (showroom != null) {
            return new ExtMsg(true, "展厅修改成功", null);
        }
        return new ExtMsg(false, "展厅修改失败", null);
    }

    @RequestMapping(value = "/electronicsFile/{showroomid}", method = RequestMethod.POST)
    @ResponseBody
    public List<Tb_electronic> electronicsFile(@PathVariable String showroomid) {
        return showroomService.getShowroomFile(showroomid);
    }

    //上传图片
    @RequestMapping("/upload")
    @ResponseBody
    public Map<String, String> upload(@RequestParam("fileToUpload") MultipartFile[] multfiles) {
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
        String filePath = electronicService.getTemporaryShowroom();//展厅富文本相对路径文件夹
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
