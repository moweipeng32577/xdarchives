package com.wisdom.web.controller;

import com.wisdom.util.LogAnnotation;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.ExtNcTree;
import com.wisdom.web.entity.Tb_thematic_detail;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.service.ThematicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 信息编研控制器
 * Created by yl on 2017/10/27.
 */
@Controller
@RequestMapping(value = "/infoCompilation")
public class InfoCompilationController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ThematicService thematicService;

    @Value("${system.report.server}")
    private String reportServer;//报表服务

    @RequestMapping("/main")
    public String main() {
        return "/inlet/infoCompilation";
    }

    @RequestMapping("pavilionSearch")
    public String PavilionSearch(){return "/inlet/pavilionSearch";}

    //编研系统-查档申请管理
    @RequestMapping("/whthinManage")
    public String whthinManage(Model model){
        model.addAttribute("iflag","1");
        model.addAttribute("reportServer",reportServer);
        return "/inlet/whthinManage";
    }

    @RequestMapping("/getThematic")
    @ResponseBody
    public List<ExtNcTree> getThematic() {
        return thematicService.getThematic();
    }

    @RequestMapping("/getThematicDetail")
    @ResponseBody
    public Page<Tb_thematic_detail> getThematicDetail(int page, int start, int limit,String condition, String operator, String content, String thematicId, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        return thematicService.findTDetailByThematicid(page, limit,condition,operator, content,thematicId,sortobj);
    }

    @LogAnnotation(module="编研系统管理-专题制作-信息编研",sites = "1",fields = "title",connect = "##标题",startDesc = "增加信息，条目详细：")
    @RequestMapping("/saveThematicDetail")
    @ResponseBody
    public ExtMsg saveThematicDetail(@ModelAttribute("form") Tb_thematic_detail tb_thematic, String[] mediaids) {
        Tb_thematic_detail mThematicDetail = thematicService.saveThematicDetail(tb_thematic,mediaids);
        ExtMsg extMsg=null;
        if(mThematicDetail!=null){
            extMsg=new ExtMsg(true,"保存成功",mThematicDetail.getThematicdetilid());
        }else{
            extMsg=new ExtMsg(false,"保存失败",null);
        }
        return extMsg;
    }

    @LogAnnotation(module="编研系统管理-专题制作-信息编研",sites = "1",fields = "title",connect = "##标题",startDesc = "修改信息，条目详细：")
    @RequestMapping("/updateThematicDetail")
    @ResponseBody
    public ExtMsg updateThematicDetail(@ModelAttribute("form") Tb_thematic_detail tb_thematic_detail) {
        Tb_thematic_detail save = thematicService.updateThematicDetail(tb_thematic_detail);
        ExtMsg extMsg = null;
        if (save!=null) {
            extMsg = new ExtMsg(true, "修改成功", null);
        } else {
            extMsg = new ExtMsg(false, "修改失败", null);
        }
        return extMsg;
    }

    @LogAnnotation(module="编研系统管理-专题制作-信息编研",sites = "1",startDesc = "删除信息，条目编号：")
    @RequestMapping("/deleteThematicDetail")
    @ResponseBody
    public ExtMsg deleteThematicDetail(String[] thematicdetilids){
        int count = thematicService.deleteThematicDetail(thematicdetilids);
        ExtMsg extMsg=null;
        if(count>0){
            thematicService.delElectronic(thematicdetilids);//删除关联电子文件和电子文件条目
            extMsg=new ExtMsg(true,"删除成功",null);
        }else{
            extMsg=new ExtMsg(false,"删除失败",null);
        }
        return extMsg;
    }


    @RequestMapping("/searchleadin")
    @ResponseBody
    public ExtMsg searchleadin(String[] dataids,String treeid){
        return thematicService.searchleadin(dataids,treeid);
    }

    @RequestMapping("/thematicele")
    public String Thematicele(Model model, String thematicid) {
        model.addAttribute("thematicid",thematicid);
        return "/inlet/thematicelectronic";
    }
}
