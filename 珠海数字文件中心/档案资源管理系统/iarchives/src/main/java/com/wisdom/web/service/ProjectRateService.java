package com.wisdom.web.service;

import com.wisdom.util.DBCompatible;
import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2020/5/9.
 */
@Service
@Transactional
public class ProjectRateService {

    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    ClassifySearchService classifySearchService;

    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ProjectManageRepository projectManageRepository;

    @Autowired
    ProjectDocRepository projectDocRepository;

    @Autowired
    LogService logService;

    @Autowired
    LogMsgRepository logMsgRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserFunctionRepository userFunctionRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    NodeRepository nodeRepository;

    public Page<Tb_index_detail> getProjectOpenEntries(String nodeid, String condition, String operator, String content,
                                            ExtDateRangeData daterangedata,int page, int limit, Sort sort) {
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        //项目管理下的所有子节点
        List<String> nodeListProject = nodesettingService.getNodeidLoop(nodeid, true, null);
        String nodeidStr = "";
        if (nodeListProject.size() > 0) {
            nodeidStr = " and sid.nodeid in('" + String.join("','", nodeListProject) + "') ";
        }

        String sortStr = "";//排序
        int sortInt = 0;//判断是否副表表排序
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            if ("eleid".equals(order.getProperty())) {
                sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
            } else {
                sortStr = " order by " + order.getProperty() + " " + order.getDirection();
            }
            sortInt = entryIndexService.checkFilecode(order.getProperty());
        } else {
            sortStr = " order by archivecode desc, descriptiondate desc ";
        }

        String searchCondition = "";
        if (content != null && !"".equals(content)) {//输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        String dataStr = "";
        if (daterangedata.getFiledateendday() != null && daterangedata.getFiledatestartday() != null) {//检索条件为日期范围
            dataStr = classifySearchService.getDateRangeCondition(daterangedata);
        }

        String table = "v_index_detail";
        String countTable = "v_index_detail";
        if ((condition == null || entryIndexService.checkFilecode(condition) == 0)) {//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
            countTable = "tb_entry_index";
            if (sortInt == 0) {//非副表表字段排序
                table = "tb_entry_index";
            }
        }

        String flagOpenStr = " and (sid.flagopen='条目开放'or sid.flagopen='原文开放')";
        String sql = "select sid.entryid from " + table + " sid where 1=1 " + searchCondition +  dataStr + nodeidStr + flagOpenStr;
        String countSql = "select count(nodeid) from " + countTable + " sid where 1=1 " + searchCondition +  dataStr + nodeidStr + flagOpenStr;
        return entryIndexService.getPageListTwo(sql, sortStr, countSql, page, limit, pageRequest);
    }

    public Tb_project_manage projectManageSubmit(Tb_project_manage project_manage){
        Tb_project_manage project_manage1 = null;
        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try{
            if(null!=project_manage.getId() && !"".equals(project_manage.getId())){//修改
                Tb_project_manage project_manage2 = getProjectManageByid(project_manage.getId());
                project_manage.setProjectstatus(project_manage2.getProjectstatus());
                project_manage.setOperator(user.getUserid());
            }else {
                project_manage.setProjectstatus("新增项目");
                project_manage.setOperator(user.getUserid());
                project_manage.setCreatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }
            project_manage1 =  projectManageRepository.save(project_manage);
        }catch (Exception e){
            e.printStackTrace();
        }
        return project_manage1;
    }


    public Page<Tb_project_manage> getProjectManages(int page, int limit, String sort, String condition, String operator, String content, String projectstatus,String[] userids){
        Sort sortobj = null;
        if(sort==null||sort==""){
            sortobj = new Sort(Sort.Direction.DESC, "createtime");
        }

        PageRequest pageRequest = new PageRequest(page-1,limit,sortobj);
        Specification<Tb_project_manage> searchUseridCondition = getSearchByOperator(userids);
        Specifications sp = Specifications.where(searchUseridCondition);
        if(content!=null){
            sp = sp.and(ClassifySearchService.addSearchbarCondition(sp, condition, operator, content));
        }
        if(projectstatus!=null){
            String[] status = projectstatus.split(",");
            Specification<Tb_project_manage> searchstatusCondition = getSearchStatusByProjectManages(status);
            Specifications specifications = Specifications.where(searchstatusCondition);
            sp = sp.and(specifications);
        }
        return projectManageRepository.findAll(sp,pageRequest);
    }

    public Tb_project_manage getProjectManageByid(String id){
        return projectManageRepository.findOne(id);
    }

    public int deleteProjectManageByid(String[] ids){
        if(ids!=null){
            for(String str : ids){
                logService.recordTextLog("项目管理","删除项目信息;记录id:"+str);
            }
        }
        return projectManageRepository.deleteByIdIn(ids);
    }

    public static Specification<Tb_project_manage> getSearchStatusByProjectManages(String[] status){
        Specification<Tb_project_manage> searchStatus = null;
        if(status!=null && status.length > 0){
            searchStatus = new Specification<Tb_project_manage>() {
                @Override
                public Predicate toPredicate(Root<Tb_project_manage> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    CriteriaBuilder.In<String> inValue = criteriaBuilder.in(root.get("projectstatus"));
                    for(String s : status){
                        inValue.value(s);
                    }
                    return inValue;
                }
            };
        }
        return searchStatus;
    }

    public static Specification<Tb_project_manage> getSearchByOperator(String[] userids){
        Specification<Tb_project_manage> users = null;
        if(userids!=null && userids.length > 0){
            users = new Specification<Tb_project_manage>() {
                @Override
                public Predicate toPredicate(Root<Tb_project_manage> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    CriteriaBuilder.In<String> inValue = criteriaBuilder.in(root.get("operator"));
                    for(String s : userids){
                        inValue.value(s);
                    }
                    return inValue;
                }
            };
        }
        return users;
    }

    public static Specification<Tb_project_manage> getSearchIdsByProjectManages(String[] ids){
        Specification<Tb_project_manage> searchIds = null;
        if(ids!=null && ids.length > 0){
            searchIds = new Specification<Tb_project_manage>() {
                @Override
                public Predicate toPredicate(Root<Tb_project_manage> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    CriteriaBuilder.In<String> inValue = criteriaBuilder.in(root.get("id"));
                    for(String s : ids){
                        inValue.value(s);
                    }
                    return inValue;
                }
            };
        }
        return searchIds;
    }

    public Page<Tb_log_msg> getProjectLogs(String id){
        List<Tb_log_msg> list = logMsgRepository.findBydesci(id.trim());
        return new PageImpl(list,null,0);
    }

    /**
     *
     * @param ids
     * @param modul 子模块
     * @param status 项目记录状态
     * @return
     */
    public int updateProjectStatusByid(String[] ids,String modul,String status,String spmanid,String spnodeid){
        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Tb_project_manage> list = projectManageRepository.findByIdIn(ids);
        if(list==null){
            return 0;
        }
        int count = 0;
        Tb_node node = nodeRepository.findByNodeid(spnodeid);
        for(Tb_project_manage project_manage : list){
            project_manage.setProjectstatus(status);
            project_manage.setSpnodeid(spnodeid);
            Tb_project_doc project_doc = new Tb_project_doc();
            project_doc.setFpspmanid(spmanid);
            project_doc.setSpnodeid(spnodeid);
            project_doc.setProjectid(project_manage.getId());
            projectDocRepository.save(project_doc);
            if("部门审核".equals(node.getText())){
                project_manage.setBmshtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }else if("副馆长审阅".equals(node.getText())){
                project_manage.setFgzsytime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }else if("馆长审阅".equals(node.getText())){
                project_manage.setGzsytime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }
            projectManageRepository.save(project_manage);
            logService.recordTextLog("项目管理-"+modul,status+";记录id:"+project_manage.getId());
            count++;
        }
        return count;
    }

    /**
     *
     * @param ids
     * @param modul 子模块
     * @param status 项目记录状态
     * @param areaText 批示语
     * @return
     */
    public int updateProjectStatusBy(String[] ids,String modul,String status,String areaText,String tasktype,String spmanid, String spnodeid,String allapprove){
        List<Tb_project_manage> list = projectManageRepository.findByIdIn(ids);
        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(list==null){return 0;}
        int count = 0;
        for(Tb_project_manage project_manage : list){
            project_manage.setProjectstatus(status);
            project_manage.setApprove(allapprove);
            if(spmanid!=null && spnodeid!=null){
                project_manage.setSpnodeid(spnodeid);
                Tb_project_doc project_doc = new Tb_project_doc();
                project_doc.setFpspmanid(spmanid);
                project_doc.setSpnodeid(spnodeid);
                project_doc.setProjectid(project_manage.getId());
                projectDocRepository.save(project_doc);
            }
            if("部门审核".equals(modul)){
                project_manage.setFgzsytime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }else if("副馆长审阅".equals(modul)){
                project_manage.setGzsytime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }
            projectManageRepository.save(project_manage);
            logService.recordTextLog("项目管理-"+modul,status+";批示："+areaText+"记录id:"+project_manage.getId());
            count++;
            //更新任务状态为“完成”
            taskRepository.updateByBorrowmsgidAndUserid(project_manage.getId(),tasktype,user.getUserid());
            //删除其他人任务审核提醒
            taskRepository.deleteByBorrowmsgid(project_manage.getId(),tasktype,"待处理");
        }
        return count;
    }

    public Page<Tb_project_manage> getProjectBy(int page, int limit, String sort, String condition, String operator, String content, String projectstatus){
        Sort sortobj = null;
        if(sort==null||sort==""){
            sortobj = new Sort(Sort.Direction.DESC, "finishtime");
        }else{
            sortobj = new Sort(Sort.Direction.DESC, sort);
        }
        PageRequest pageRequest = new PageRequest(page-1,limit,sortobj);
        Specifications sp = null;
        if(projectstatus!=null){
            String[] status = projectstatus.split(",");
            Specification<Tb_project_manage> searchstatusCondition = getSearchStatusByProjectManages(status);
            sp = Specifications.where(searchstatusCondition);
        }
        if(content!=null){
            sp = sp.and(ClassifySearchService.addSearchbarCondition(sp, condition, operator, content));
        }
        return projectManageRepository.findAll(sp,pageRequest);
    }

    public void task(int count,String[] ids,String type,String spmanid){
        List<Tb_project_manage> list = projectManageRepository.findByIdIn(ids);
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Tb_task> taskProjects = new ArrayList<>();
            for(Tb_project_manage project_manage : list){
                Tb_task taskProject = new Tb_task();
                taskProject.setState(Tb_task.STATE_WAIT_HANDLE);// 处理中
                taskProject.setTime(new Date());
                taskProject.setLoginname(spmanid);//审核人
                if("部门审核".equals(type)){
                    taskProject.setText(userDetails.getRealname() + " 提交一条新项目审批！");
                }else if("综合事务部整理".equals(type)){
                    taskProject.setText(userDetails.getRealname() + " 审批通过一条新项目！");
                }else if("馆长审阅".equals(type) || "副馆长审阅".equals(type)){
                    taskProject.setText(userDetails.getRealname() + " 提交一条新项目审阅！");
                }
                taskProject.setType(type);
                taskProject.setBorrowmsgid(project_manage.getId());
                taskProjects.add(taskProject);
            }
        taskRepository.save(taskProjects);
    }

    public void affairtask(String[] ids,String type, String spmanid){
        List<Tb_project_manage> list = projectManageRepository.findByIdIn(ids);
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Tb_task> taskProjects = new ArrayList<>();
        for(Tb_project_manage project_manage : list){
            Tb_task taskProject = new Tb_task();
            taskProject.setState(Tb_task.STATE_WAIT_HANDLE);// 处理中
            taskProject.setTime(new Date());
            taskProject.setLoginname(spmanid);//审核人
            taskProject.setText(userDetails.getRealname() + " 审批通过一条新项目！");
            taskProject.setType(type);
            taskProject.setBorrowmsgid(project_manage.getId());
            taskProjects.add(taskProject);
        }
        taskRepository.save(taskProjects);
    }

    public Page<Tb_project_manage> getProjectByDeputyCurator(int page, int limit, String sort, String condition, String operator, String content, String projectstatus,String nodeId){
        Sort sortobj = null;
        if(sort==null||sort==""){
            sortobj = new Sort(Sort.Direction.DESC, "createtime");

        }else{
            String[] sorts=sort.split(",");
            if(StringUtils.isNotEmpty(sorts[0])){
                sortobj = new Sort(Sort.Direction.DESC, sorts[0]);
                for(int i=1;i<sorts.length;i++){
                    sortobj.and(new Sort(Sort.Direction.DESC, sorts[i]));
                }
            }

        }
        PageRequest pageRequest = new PageRequest(page-1,limit,sortobj);
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> projectids = projectDocRepository.findByFpspmanidAndSpnodeid(userDetails.getUserid(),nodeId);
        if(projectids.size()==0){return null;}
        String[] projects = new String[projectids.size()];
        projectids.toArray(projects);
        Specifications sp = Specifications.where(getSearchIdsByProjectManages(projects));
        if(projectstatus!=null){
            String[] status = projectstatus.split(",");
            Specification<Tb_project_manage> searchstatusCondition = getSearchStatusByProjectManages(status);
            sp = sp.and(Specifications.where(searchstatusCondition));
        }
        if(content!=null){
            sp = sp.and(ClassifySearchService.addSearchbarCondition(sp, condition, operator, content));
        }
        return projectManageRepository.findAll(sp,pageRequest);
    }

    public List<Tb_node> getNodes(String spnodeid) {
        Tb_node node = nodeRepository.findByNodeid(spnodeid);
        String[] nextids = node.getNextid().split(",");
        return nodeRepository.getNodes(nextids);
    }
}
