package com.wisdom.web.controller;

import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.LogService;
import com.wisdom.web.service.NodesettingService;
import com.wisdom.web.service.ProjectRateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2020/5/9.
 */
@Controller
@RequestMapping("projectRate")
public class ProjectRateController {


    @Autowired
    ProjectRateService projectRateService;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    LogService logService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    LogMsgRepository logMsgRepository;

    @Autowired
    NodeRepository nodeRepository;

    @Autowired
    WorkRepository workRepository;


    @Value("${system.report.server}")
    private String reportServer;//报表服务

    @RequestMapping("/main")
    public String index(Model model){
        Tb_data_node nodeProject = dataNodeRepository.findByNodename("项目管理");
        model.addAttribute("nodeId",nodeProject.getNodeid());
        model.addAttribute("reportServer",reportServer);
        return "/inlet/projectRate";
    }

    @RequestMapping("/getProjectOpenEntries")
    @ResponseBody
    public Page<Tb_index_detail> getProjectOpenEntries(String nodeid, String condition, String operator, String content,
                                           ExtDateRangeData daterangedata,int page, int limit, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        return projectRateService.getProjectOpenEntries(nodeid, condition, operator, content, daterangedata, page, limit, sortobj);
    }

    @RequestMapping("/getProjectOpenEntriesIndex")
    @ResponseBody
    public IndexMsg getProjectOpenEntriesIndex(String condition, String operator, String content,
                                               ExtDateRangeData daterangedata, int page, int limit, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        Tb_data_node nodeProject = dataNodeRepository.findByNodename("项目管理");
        Page<Tb_index_detail> detailPage = projectRateService.getProjectOpenEntries(nodeProject.getNodeid(), condition, operator, content, daterangedata, page, limit, sortobj);
        List<Tb_index_detail> detailList = detailPage.getContent();
        return new IndexMsg(true,"0","成功",detailList);
    }

    @RequestMapping("/getProjectOpenNodeId")
    @ResponseBody
    public ExtMsg getProjectOpenNodeId() {
        Tb_data_node nodeProject = dataNodeRepository.findByNodename("项目管理");
        if(nodeProject==null){
            return  new ExtMsg(true,"", new String[]{""});
        }
        return new ExtMsg(true,"", nodesettingService.getNodeidLoop(nodeProject.getNodeid(), true, null));
    }

    @RequestMapping("/addmain")//新增项目模块
    public String addmain(Model model) {
        model.addAttribute("reportServer", reportServer);
        return "/inlet/projectAdd";
    }

    @RequestMapping("/auditmain")//部门审核模块
    public String unitauditmain(Model model) {
        model.addAttribute("reportServer", reportServer);
        return "/inlet/departmentAudit";
    }

    @RequestMapping("/affairmain")//综合事务部整理模块
    public String affairmain(Model model) {
        model.addAttribute("reportServer", reportServer);
        return "/inlet/affair";
    }

    @RequestMapping("/deputyCuratormain")//副馆长审阅模块
    public String deputyCuratormain(Model model) {
        model.addAttribute("reportServer", reportServer);
        return "/inlet/deputyCurator";
    }

    @RequestMapping("/curatormain")//馆长审阅模块
    public String curatormain(Model model) {
        model.addAttribute("reportServer", reportServer);
        return "/inlet/curator";
    }

    //登录者的项目记录
    @RequestMapping("/getProjectManages")
    @ResponseBody
    public Page<Tb_project_manage> getProjectManages(int page, int limit, String sort, String condition, String operator, String content, String projectstatus) {
        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String[] userids = user.getUserid().split(",");
        return projectRateService.getProjectManages(page, limit, sort, condition, operator, content, projectstatus,userids);
    }

    @RequestMapping("/projectManageSubmit")
    @ResponseBody
    public ExtMsg projectManageSubmit(Tb_project_manage project_manage) {
        Tb_project_manage tb_project_manage = projectRateService.projectManageSubmit(project_manage);
        String msg = "";
        if(null!=project_manage.getId() && !"".equals(project_manage.getId())){//修改
            msg = "修改" + project_manage.getTitle() + "项目记录;记录id:"+project_manage.getId();
        }else {
            msg = "新增" + project_manage.getTitle() + "项目记录;记录id:"+tb_project_manage.getId();
        }
        logService.recordTextLog("项目管理-新增项目",msg);
        return new ExtMsg(true, "", null);
    }

    @RequestMapping("/getProjectManageByid")
    @ResponseBody
    public ExtMsg getProjectManageByid(String id) {
        Tb_project_manage project_manage = projectRateService.getProjectManageByid(id);
        if (project_manage != null) {
            return new ExtMsg(true, "", project_manage);
        } else {
            return new ExtMsg(false, "", null);
        }
    }

    @RequestMapping("/deleteProjectManageByid")
    @ResponseBody
    public ExtMsg deleteProjectManageByid(String[] ids) {
        int count = 0;
        count = projectRateService.deleteProjectManageByid(ids);
        if (count > 0) {
            return new ExtMsg(true, "", null);
        } else {
            return new ExtMsg(false, "", null);
        }
    }

    @RequestMapping("/getProjectLogs")
    @ResponseBody
    public Page<Tb_log_msg> getProjectLogs(String id) {
        return projectRateService.getProjectLogs(id);
    }

    @RequestMapping("/getHomeProjectLogs")
    @ResponseBody
    public IndexMsg getHomeProjectLogs(String id,int page,int limit) {
        Sort sortobj = WebSort.getSortByJson(null);
        PageRequest pageRequest = new PageRequest(page - 1, limit, sortobj);
        String msg=logMsgRepository.findBydesciCount(id);
        return  new IndexMsg(true, "200",msg, logMsgRepository.findBydesci(pageRequest,id));
    }

    //新增项目-提交审核
    @RequestMapping("/updateProjectStatusByid")
    @ResponseBody
    public ExtMsg updateProjectStatusByid(String[] ids,String spnodeid, String spmanid) {
        int count = 0;
        Tb_node node = nodeRepository.findByNodeid(spnodeid);
        count = projectRateService.updateProjectStatusByid(ids,"新增项目","提交"+node.getText(),spmanid,spnodeid);
        if (count > 0) {
            //审核提醒通知
            projectRateService.task(count,ids,node.getText(),spmanid);//【新增项目】-提交
            return new ExtMsg(true, "", null);
        } else {
            return new ExtMsg(false, "", null);
        }
    }

    @RequestMapping("/updateProjectAffairStatusByid")
    @ResponseBody
    public ExtMsg updateProjectAffairStatusByid(String[] ids,String spnodeid, String spmanid) {
        int count = 0;
        count = projectRateService.updateProjectStatusByid(ids,"综合事务部整理","提交副领导审阅",spmanid,null);
        if (count > 0) {
            //审核提醒通知
            projectRateService.affairtask(ids,"副馆长审阅",spmanid);//【综合事务部整理】-提交审阅（副馆长审阅）
            return new ExtMsg(true, "", null);
        } else {
            return new ExtMsg(false, "", null);
        }
    }

    //本部门用户的项目记录
    @RequestMapping("/getProjectByDepart")
    @ResponseBody
    public Page<Tb_project_manage> getProjectByDepart(int page, int limit, String sort, String condition, String operator, String content, String projectstatus) {
        Tb_work work = workRepository.findByWorktext("项目管理审批");
        String nodeId = "";
        if (work != null && work.getText() != null) {
            Tb_node node = nodeRepository.findByWorkidAndSortsequence(work.getId(), 2);
            nodeId = node.getId();
        }
        if(StringUtils.isEmpty(sort)){
            sort="bmshtime,createtime";
        }
        return projectRateService.getProjectByDeputyCurator(page, limit, sort, condition, operator, content, projectstatus,nodeId);
    }

    //部门审核
    @RequestMapping("/updateProjectStatusByAudit")
    @ResponseBody
    public ExtMsg updateProjectStatusByAudit(String[] ids,String approveresult,String areaText,String spmanid, String spnodeid,String allapprove) {
        int count = 0;
        String status;
        if("通过".equals(approveresult)){
            status = "部门审核通过";
        }else {
            status = "部门审核不通过";
        }
        count = projectRateService.updateProjectStatusBy(ids,"部门审核",status,areaText,"部门审核",spmanid,spnodeid,allapprove);
        if (count > 0) {
            //审核提醒通知
            Tb_node node = nodeRepository.findByNodeid(spnodeid);
            projectRateService.task(count,ids,node.getText(),spmanid);//【部门审核】-提交
            return new ExtMsg(true, "", null);
        } else {
            return new ExtMsg(false, "", null);
        }
    }

    //副馆长审阅
    @RequestMapping("/updateProjectStatusByDeputycurator")
    @ResponseBody
    public ExtMsg updateProjectStatusByDeputycurator(String[] ids,String approveresult,String areaText,String spmanid, String spnodeid,String allapprove) {
        int count = 0;
        String status;
        if("通过".equals(approveresult)){
            status = "副领导审阅通过";
        }else {
            status = "副领导审阅不通过";
        }
        count = projectRateService.updateProjectStatusBy(ids,"副馆长审阅",status,areaText,"副馆长审阅",spmanid,spnodeid,allapprove);
        if (count > 0) {
            //审核提醒通知
            Tb_node node = nodeRepository.findByNodeid(spnodeid);
            projectRateService.task(count,ids,node.getText(),null);//【副馆长审阅】-提交
            return new ExtMsg(true, "", null);
        } else {
            return new ExtMsg(false, "", null);
        }
    }

    //馆长审阅
    @RequestMapping("/updateProjectStatusByCurator")
    @ResponseBody
    public ExtMsg updateProjectStatusByCurator(String[] ids,String approveresult,String areaText,String allapprove) {
        int count = 0;
        String status;
        if("通过".equals(approveresult)){
            status = "领导审阅通过发布";
        }else {
            status = "领导审阅不发布";
        }
        count = projectRateService.updateProjectStatusBy(ids,"馆长审阅",status,areaText,"馆长审阅",null,null,allapprove);
        if (count > 0) {
            return new ExtMsg(true, "", null);
        } else {
            return new ExtMsg(false, "", null);
        }
    }

    //综合事务部整理模块的项目记录
    @RequestMapping("/getProjectByAffair")
    @ResponseBody
    public Page<Tb_project_manage> getProjectByAffair(int page, int limit, String sort, String condition, String operator, String content, String projectstatus) {
        return projectRateService.getProjectBy(page, limit, sort, condition, operator, content, projectstatus);
    }

    //馆长审阅模块的项目记录
    @RequestMapping("/getProjectByCurator")
    @ResponseBody
    public Page<Tb_project_manage> getProjectByCurator(int page, int limit, String sort, String condition, String operator, String content, String projectstatus) {
//        return projectRateService.getProjectBy(page, limit, sort, condition, operator, content, projectstatus);
        Tb_work work = workRepository.findByWorktext("项目管理审批");
        String nodeId = "";
        if (work != null && work.getText() != null) {
            Tb_node node = nodeRepository.findByWorkidAndSortsequence(work.getId(), 4);
            nodeId = node.getId();
        }
        if(StringUtils.isEmpty(sort)){
            sort="gzsytime,createtime";
        }
        return projectRateService.getProjectByDeputyCurator(page, limit, sort, condition, operator, content, projectstatus,nodeId);
    }

    //副馆长审阅模块的项目记录
    @RequestMapping("/getProjectByDeputyCurator")
    @ResponseBody
    public Page<Tb_project_manage> getProjectByDeputyCurator(int page, int limit, String sort, String condition, String operator, String content, String projectstatus) {
        Tb_work work = workRepository.findByWorktext("项目管理审批");
        String nodeId = "";
        if (work != null && work.getText() != null) {
            Tb_node node = nodeRepository.findByWorkidAndSortsequence(work.getId(), 3);
            nodeId = node.getId();
        }
        if(StringUtils.isEmpty(sort)){
            sort="fgzsytime,createtime";
        }
        return projectRateService.getProjectByDeputyCurator(page, limit, sort, condition, operator, content, projectstatus,nodeId);
    }

    //领导审阅通过发布 项目
    @RequestMapping("/getProjectBypublish")
    @ResponseBody
    public IndexMsg getProjectBypublish(String condition, String operator, String content, int page, int limit, String sort) {
        Page<Tb_project_manage> detailPage = projectRateService.getProjectBy(page, limit, sort, condition, operator, content, "领导审阅通过发布");
        List<Tb_project_manage> detailList = detailPage.getContent();
        List returnList = new ArrayList(detailList);
        return new IndexMsg(true,"200","成功",returnList);
    }

    @RequestMapping("/getNextNode")
    @ResponseBody
    public List<Tb_node> getNextNode(String spnodeid) {
        return projectRateService.getNodes(spnodeid);
    }
}
