package com.wisdom.DataInterface;

import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.controller.AcquisitionController;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.repository.DataNodeRepository;
import com.wisdom.web.repository.EntryIndexRepository;
import com.wisdom.web.repository.RightOrganRepository;
import com.wisdom.web.repository.UserRepository;
import com.wisdom.web.service.ClassificationService;
import com.wisdom.web.service.OrganService;
import com.wisdom.web.service.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 数据共享接口
 * Created by wjh
 */
@RestController
@RequestMapping("/shared")
public class DataShared {

    private static Logger LOGGER = LoggerFactory.getLogger(DataShared.class);

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RightOrganRepository rightOrganRepository;

    @Autowired
    OrganService organService;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    TemplateService templateservice;

    @Autowired
    ClassificationService classificationService;

    @Autowired
    AcquisitionController acquisitionController;
//    /**
//     * 根据条件获取共享业务数据
//     * @param condition 条件字段
//     * @param operator 匹配方式
//     * @param content 查询内容
//     * @param page 页面
//     * @param limit 每页多少
//     * @return
//     */
//    @GetMapping("/getSharedEntryData")
//    public ExtMsg getSharedEntryData(String condition, String operator, String content, Integer page, Integer limit){
//        try {
//            List list= getSharedData(condition,operator,content,page,limit);
//            Specifications sp = (Specifications)list.get(0);
//            PageRequest pageRequest = (PageRequest)list.get(1);
//            if(sp==null){
//                return new ExtMsg(true,"",entryIndexRepository.findAll(pageRequest));
//            }else{
//                return new ExtMsg(true,"",entryIndexRepository.findAll(sp,pageRequest));
//            }
//        }catch (Exception e){
//            LOGGER.error("获取共享业务数据出错",e);
//            return new ExtMsg(false,"获取共享数据出错",null);
//        }
//    }
//
//    /**
//     * 根据条件获取共享用户数据
//     * @param condition 条件字段
//     * @param operator 匹配方式
//     * @param content 查询内容
//     * @param page 页面
//     * @param limit 每页多少
//     * @return
//     */
//    @GetMapping("/getSharedUserData")
//    public ExtMsg getSharedUserData(String condition, String operator, String content, Integer page, Integer limit){
//        try {
//            List list= getSharedData(condition,operator,content,page,limit);
//            Specifications sp = (Specifications)list.get(0);
//            PageRequest pageRequest = (PageRequest)list.get(1);
//            if(sp==null){
//                return new ExtMsg(true,"",userRepository.findAll(pageRequest));
//            }else{
//                return new ExtMsg(true,"",userRepository.findAll(sp,pageRequest));
//            }
//        }catch (Exception e){
//            LOGGER.error("获取共享用户数据出错",e);
//            return new ExtMsg(false,"获取共享用户数据出错",null);
//        }
//    }
//
//    /**
//     * 根据条件获取共享机构数据
//     * @param condition 条件字段
//     * @param operator 匹配方式
//     * @param content 查询内容
//     * @param page 页面
//     * @param limit 每页多少
//     * @return
//     */
//    @GetMapping("/getSharedOrganData")
//    public ExtMsg getSharedOrganData(String condition, String operator, String content, Integer page, Integer limit){
//        try {
//            List list= getSharedData(condition,operator,content,page,limit);
//            Specifications sp = (Specifications)list.get(0);
//            PageRequest pageRequest = (PageRequest)list.get(1);
//            if(sp==null){
//                return new ExtMsg(true,"",rightOrganRepository.findAll(pageRequest));
//            }else{
//                return new ExtMsg(true,"",rightOrganRepository.findAll(sp,pageRequest));
//            }
//        }catch (Exception e){
//            LOGGER.error("获取共享机构数据出错",e);
//            return new ExtMsg(false,"获取共享机构数据出错",null);
//        }
//    }
//
//
//    public List getSharedData(String condition, String operator, String content, Integer page, Integer limit)throws Exception{
//            Specifications sp = null;
//            if(condition != null&&operator != null&&content != null){
//                String[] conditions = condition.split(",");
//                String[] operators = operator.split(",");
//                String[] contents = content.split(",");
//                for (int i = 0; i < contents.length; i++) {
//                    sp = sp==null?Specifications.where(new SpecificationUtil(conditions[i],operators[i],contents[i])):sp.and(new SpecificationUtil(conditions[i],operators[i],contents[i]));
//                }
//            }
//            page = page==null?1:page;
//            limit = limit==null?20:limit;
//            PageRequest pageRequest = new PageRequest(page-1,limit);
//            return Arrays.asList(sp,pageRequest);
//    }

    // 更新机构等级001.001.002
    @RequestMapping("/updateOrganlevel")
    public void updateOrganlevel(){
        organService.updateOrganlevel();
    }

    @RequestMapping("/updateClassid")
    public void updateClassid(){//设置富滇的tb_data_node的classid
        List<String> nodecodes=dataNodeRepository.findAllFl();
        for (String  nodecode:nodecodes){
            List<String> sunNodecodes=dataNodeRepository.findAllFlNext(nodecode);
            if(sunNodecodes.size()>0){//该节点下边是子级分类节点，跳过此节点继续往下
            }else{
                //更新所有子节点的classid
                templateservice.updateAllNodeNext(nodecode);
                /*String classid=dataNodeRepository.findClassid(nodecode);//此方法内不允许直接更新数据库操作
                dataNodeRepository.updateAllNodeNext(nodecode,classid);*/
            }

        }
    }

    // 更新分类等级001.001.002
    @RequestMapping("/updateClassCodelevel")
    public void updateCodelevel(){
        classificationService.updateCodelevel();
        //classificationService.updateAjCodelevel();
    }

    //更新卷内文件总数模板字段
    @RequestMapping("/updateSumInnerFiles")
    public void updateSumInnerFiles(){
        templateservice.updateSumInnerFiles();
    }

    //更新初始化案卷nodecode异常
    @RequestMapping("/updateAjFirstNode")
    public void updateAjFirstNode() {
        templateservice.updateAjFirstNode();
    }

    //测试pdf签章
    @RequestMapping("/testPdfSign")
    public void testPdfSign() {
        try{
            acquisitionController.PDFVerifySign("");
        }catch(Exception e){
           e.printStackTrace();
        }
    }
}
