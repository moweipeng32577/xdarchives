package com.wisdom.web.controller;

import com.wisdom.util.LogAnnotation;
import com.wisdom.util.LogAop;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.EntryIndexRepository;
import com.wisdom.web.service.AlgorithmRetentionService;
import com.wisdom.web.service.AppraisalStandardService;
import com.wisdom.web.service.EntryIndexService;
import com.wisdom.web.service.RetentionInitProbabilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RonJiang on 2018/5/9 0009.
 */
@Controller
@RequestMapping(value = "/appraisalStandard")
public class AppraisalStandardController {

    @Autowired
    LogAop logAop;

    @Autowired
    AppraisalStandardService appraisalStandardService;
    @Autowired
    AlgorithmRetentionService algorithmRetentionService;
    @Autowired
    EntryIndexRepository entryIndexRepository;
    @Autowired
    RetentionInitProbabilityService retentionInitProbabilityService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/main")
    public String index(){
        return "/inlet/appraisalStandard";
    }

    @RequestMapping("/getAppraisalStandardBySearch")
    @ResponseBody
    public Page<Tb_appraisal_standard> getAppraisalStandardBySearch(String appraisaltypevalue, int page, int start, int limit, String condition,String operator,String content,String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        logger.info("appraisaltypevalue:" + appraisaltypevalue +";page:" + page + ";start:" + start + ";limt:" + limit);
        if(appraisaltypevalue==null || "".equals(appraisaltypevalue)){//点击根节点“鉴定标准管理”时，appraisaltypevalue为空串
            return null;
        }
        return appraisalStandardService.findBySearch(appraisaltypevalue,condition,operator,content,page,limit,sortobj);
    }

    @RequestMapping("/getAutoRetentionWordsBySearch")
    @ResponseBody
    public Page<AlgorithmRetention> getAutoRetentionWordsBySearch( int page, int start, int limit, String condition,String operator,String content,String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        return algorithmRetentionService.findBySearch(condition,operator,content,page,limit,sortobj);
    }

    /**
     * 重置预测表
     * @return
     */
    @RequestMapping("/resetAlgRetentionTable")
    @ResponseBody
    public ExtMsg resetAlgRetention(){
        List<Object []> list = entryIndexRepository.findTitleNotNullByAll();
        if(list == null)
            return new ExtMsg(false,"读取标题失败",null);
        boolean isSuccess = algorithmRetentionService.resetRetentionTable(list);
        if(!isSuccess)
            return new ExtMsg(false,"未知错误，操作失败",null);

        return new ExtMsg(true,"初始化成功",null);
    }

    /**
     * 保存初始概率
     * @return
     */
    @RequestMapping("/saveInitProbabilityTable")
    @ResponseBody
    public ExtMsg saveInitProbabilityTable(RetentionInitProbability retentionInitProbability){
        try {
            if (retentionInitProbability.getY() + retentionInitProbability.getCQ() + retentionInitProbability.getDQ() != 1) {
                return new ExtMsg(false, "概率和不为 1 ！", null);
            }
            retentionInitProbability.setId(null);
            //调用service保存概率
            retentionInitProbabilityService.updateRetentionInitProbability(retentionInitProbability);
            return new ExtMsg(true, "概率设置成功", null);
        }catch(Exception e){
            e.printStackTrace();
            return new ExtMsg(false, "未知错误，概率设置失败", null);
        }
    }

    /**
     *  根据鉴定标准id获取鉴定标准对象
     * @param appraisalstandardid
     * @return
     */
    @RequestMapping("/getAppraisalStandard")
    @ResponseBody
    public Tb_appraisal_standard getAppraisalStandard(String appraisalstandardid){
        return appraisalStandardService.getAppraisalStandard(appraisalstandardid);
    }

    /**
     *  获取鉴定标准管理模块左侧节点树
     * @return
     */
    @RequestMapping("/getAppraisalStandardTree")
    @ResponseBody
    public List<ExtNcTree> getAppraisalStandardTree(){
        List<Tb_appraisal_type> appraisalTypeList = appraisalStandardService.getAllAppraisalStandard();
        List<ExtNcTree> trees = new ArrayList<>();
        for(Tb_appraisal_type appraisalType:appraisalTypeList){
            ExtNcTree tree = new ExtNcTree();
            tree.setFnid(appraisalType.getAppraisaltypeid());
            tree.setLeaf(true);
            tree.setText(appraisalType.getAppraisaltypevalue());
            trees.add(tree);
        }
        return trees;
    }

    /**
     *  获取所有鉴定类型至下拉框
     * @return
     */
    @RequestMapping("/enums")
    @ResponseBody
    public List<ExtSearchData> getEnums() {
        List<Tb_appraisal_type> appraisalTypeList = appraisalStandardService.getAllAppraisalStandard();
        List<ExtSearchData> result = new ArrayList<>();
        for (Tb_appraisal_type appraisalType : appraisalTypeList) {
            ExtSearchData extSearchData = new ExtSearchData();
            extSearchData.setItem(appraisalType.getAppraisaltypevalue());
            extSearchData.setName(appraisalType.getAppraisaltypevalue());
            result.add(extSearchData);
        }
        return result;
    }

    @RequestMapping("/ifAppraisaltypeExists")
    @ResponseBody
    public ExtMsg ifAppraisaltypeExists(String appraisalTypeValue){
        List<String> appraisalTypeValueList = appraisalStandardService.getAllAppraisaltypevalue();//查找所有鉴定类型值
        if(appraisalTypeValueList.contains(appraisalTypeValue)){
            return new ExtMsg(true,"鉴定类型存在",null);
        }else{
            return new ExtMsg(false,"鉴定类型不存在",null);
        }
    }

    /**
     *  保存表单中鉴定标准信息
     * @param appraisalStandard
     * @return
     */
    @LogAnnotation(module = "鉴定标准管理",sites = "1",fields = "appraisalstandardvalue,appraisaldesc",connect = "##鉴定标准值；,##描述；",startDesc = "增加鉴定标准操作，鉴定标准信息详情：")
    @RequestMapping(value = "/appraisalStandards", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg saveAppraisalStandard(@ModelAttribute("form") Tb_appraisal_standard appraisalStandard){
        String appraisaltypevalue = appraisalStandard.getAppraisaltypevalue();
        List<String> appraisalTypeValueList = appraisalStandardService.getAllAppraisaltypevalue();//查找所有鉴定类型值
        if(!appraisalTypeValueList.contains(appraisaltypevalue)){//鉴定类型在系统中不存在
            Tb_appraisal_type appraisalType = new Tb_appraisal_type();
            appraisalType.setAppraisaltypevalue(appraisaltypevalue);
            Tb_appraisal_type savedAppraisalType = appraisalStandardService.saveAppraisalType(appraisalType);//新增鉴定类型
            appraisalStandard.setAppraisaltypeid(savedAppraisalType.getAppraisaltypeid());
        }else{
            appraisalStandard.setAppraisaltypeid(appraisalStandardService.findAppraisaltypeidByAppraisaltypevalue(appraisaltypevalue));
        }
        Tb_appraisal_standard result = appraisalStandardService.saveAppraisalStandard(appraisalStandard);
        if(result != null){
            return new ExtMsg(true,"保存成功",result);
        }
        return new ExtMsg(false,"保存失败",null);
    }

    /**
     *  删除鉴定标准
     * @param appraisalstandardids
     * @return
     */
    @RequestMapping(value = "/appraisalStandards/{appraisalstandardids}", method = RequestMethod.DELETE)
    @ResponseBody
    public ExtMsg delAppraisalStandard(@PathVariable String appraisalstandardids) {
        String startTime = LogAop.getCurrentSystemTime();//开始时间
        long startMillis = System.currentTimeMillis();//开始毫秒数
        String[] appraisalstandardidData = appraisalstandardids.split(",");
        Integer del = appraisalStandardService.delAppraisalStandard(appraisalstandardidData);
        for(String appraisalstandardid:appraisalstandardidData){
            logAop.generateManualLog(startTime,LogAop.getCurrentSystemTime(),System.currentTimeMillis()-startMillis,"鉴定标准管理","删除鉴定标准操作，鉴定标准id为："+appraisalstandardid);
        }
        if (del > 0) {
            return new ExtMsg(true, "删除成功", del);
        }
        return new ExtMsg(false, "删除失败", null);
    }

    /**
     *  删除鉴定类型
     * @param appraisaltypevalue
     * @return
     */
    @LogAnnotation(module = "鉴定标准管理",startDesc = "删除鉴定标准操作，鉴定类型为：",sites = "1")
    @RequestMapping(value = "/delAppraisalType/{appraisaltypevalue}", method = RequestMethod.DELETE)
    @ResponseBody
    public ExtMsg delAppraisalType(@PathVariable String appraisaltypevalue) {
        Integer del = appraisalStandardService.delAppraisalType(appraisaltypevalue);
        if (del > 0) {
            return new ExtMsg(true, "删除成功", del);
        }
        return new ExtMsg(false, "删除失败", null);
    }
}
