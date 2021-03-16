package com.wisdom.web.service;

import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2020/10/15.
 */
@Service
@Transactional
public class YearlyCheckAuditService {



    @Autowired
    TaskRepository taskRepository;

    @Autowired
    YearlyCheckApproveDocRepository yearlyCheckApproveDocRepository;

    @Autowired
    YearlyCheckApproveMsgRepository yearlyCheckApproveMsgRepository;

    @Autowired
    YearlyCheckReportRepository yearlyCheckReportRepository;

    @Autowired
    FlowsRepository flowsRepository;

    @Autowired
    NodeRepository nodeRepository;

    @Autowired
    UserNodeRepository userNodeRepository;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ElectronApproveService electronApproveService;


    public Page<Tb_task> getYearlyCheckAuditTasks(int page, int limit, String sort){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Sort sortobj = WebSort.getSortByJson(sort);
        Sort sorts = new Sort(new Sort.Order(Sort.Direction.DESC, "tasktime"),
                new Sort.Order(Sort.Direction.DESC, "taskid"));
        PageRequest pageRequest = new PageRequest(page-1,limit,sortobj==null?sorts:sortobj);
        return taskRepository.findByLoginnameAndStateAndTasktype(pageRequest,userDetails.getUserid(), Tb_task.STATE_WAIT_HANDLE,"年检");
    }

    public Page<Tb_yearlycheck_report> getYearlyCheckApproveReportsByTaskid(String taskid, int page, int limit, Sort sort) {
        Specifications specifications = null;
        String[] ids = yearlyCheckApproveMsgRepository.getReportIdsByTaskid(taskid);
        Specification<Tb_yearlycheck_report> searchIds = new Specification<Tb_yearlycheck_report>() {
            @Override
            public Predicate toPredicate(Root<Tb_yearlycheck_report> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                CriteriaBuilder.In p = criteriaBuilder.in(root.get("id"));
                for (String id : ids) {
                    p.value(id);
                }
                return criteriaBuilder.or(p);
            }
        };
        specifications = specifications.where(searchIds);
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        return yearlyCheckReportRepository.findAll(specifications, pageRequest);
    }

    public List<Tb_node> getNodes(String taskid) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Tb_flows> flows = flowsRepository.findFlows(taskid, userDetails.getUserid(), "处理中");
        Tb_node node = nodeRepository.findByNodeid(flows.get(0).getNodeid());
        String[] nextids = node.getNextid().split(",");
        return nodeRepository.getNodes(nextids);
    }

    public Tb_yearlycheck_approvedoc approveFormSubmit(String textArea, String nextNode, String nextSpman, String taskid) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        Tb_task task1 = taskRepository.findByTaskid(taskid);
        Tb_flows flows = flowsRepository.findByTaskidAndSpman(taskid, userDetails.getUserid());// 获取当前任务流程设置状态
        Tb_node node = nodeRepository.findByNodeid(nextNode);// 拿到当前节点
        Tb_yearlycheck_approvedoc approvedoc = yearlyCheckApproveDocRepository.getApproveDocByTaskid(taskid);
        approvedoc.setApprove(textArea);
        if (!"".equals(nextSpman)) {// 存在下一审批环节
            String nextSpmanRealname = userRepository.findByUserid(nextSpman).getRealname();
            // 更新上一个环节的任务信息
            electronApproveService.updateElectroInfo(node.getText(), nextSpmanRealname, taskid);

            // 创建下一环节的任务信息
            Tb_task task = new Tb_task();
            task.setState(Tb_task.STATE_WAIT_HANDLE);// 处理中
            task.setTime(new Date());
            task.setLoginname(nextSpman);
            task.setText(task1.getText());
            task.setType(task1.getType());
            task.setApprovetext(node.getText());
            task.setApproveman(nextSpmanRealname);
            task.setLastid(taskid);
            Tb_task task2 = taskRepository.save(task);// 下一审批人任务
            // 更新上一环节的工作流信息
            flows.setState(Tb_flows.STATE_FINISHED);
            flows.setDate(dateInt);
            // 创建下一环节的工作流信息
            Tb_flows flows1 = new Tb_flows();
            flows1.setNodeid(node.getId());
            flows1.setText(node.getText());
            flows1.setSpman(nextSpman);
            flows1.setTaskid(task2.getId());
            flows1.setMsgid(flows.getMsgid());
            flows1.setState(Tb_flows.STATE_HANDLE);// 处理中
            flows1.setDate(dateInt);
            flowsRepository.save(flows1);// 下一流程
            // 更新电子查档信息
            approvedoc.setState("已送审");
            approvedoc.setApprovetext(node.getText());// 更新下一审批环节
            approvedoc.setApproveman(nextSpmanRealname);// 更新当前审批人
        } else { // 单据审批完毕
            // 更新上一环节工作流信息
            flows.setState(Tb_flows.STATE_FINISHED);// 完成
            flows.setDate(dateInt);

            List<Tb_node> nodes = nodeRepository.findByWorkidOrderBySortsequence(node.getWorkid());
            Tb_node node1 = nodes.get(nodes.size() - 1);
            // 完成单据审批
            Tb_flows flows1 = new Tb_flows();
            flows1.setNodeid(node1.getId());
            flows1.setText(node1.getText());
            flows1.setDate(dateInt);
            flows1.setTaskid(taskid);
            flows1.setMsgid(flows.getMsgid());
            flows1.setState(Tb_flows.STATE_FINISHED);// 完成
            flowsRepository.save(flows1);
            approvedoc.setState("已通过");
            approvedoc.setApprovetext(node1.getText());
            approvedoc.setApproveman(userDetails.getRealname());
            electronApproveService.updateElectroInfo(node.getText(), userDetails.getRealname(), taskid);
            String[] ids = yearlyCheckApproveMsgRepository.getReportIdsByApprovecode(approvedoc.getApprovecode());
            List<Tb_yearlycheck_report> yearlycheckReports = yearlyCheckReportRepository.findByIdIn(ids);
            for(Tb_yearlycheck_report report : yearlycheckReports){
                report.setState("已审批");
            }
            yearlyCheckReportRepository.save(yearlycheckReports);
        }
        return yearlyCheckApproveDocRepository.save(approvedoc);
    }

    public void pproveFormBack(String textarea, String taskid) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        Tb_task task = taskRepository.findByTaskid(taskid);// 获取任务修改任务状态
        task.setState(Tb_task.STATE_FINISHED);// 完成

        Tb_yearlycheck_approvedoc approvedoc = yearlyCheckApproveDocRepository.getApproveDocByTaskid(taskid);
        approvedoc.setState("退回");
        approvedoc.setApprove(textarea);

        String[] ids = yearlyCheckApproveMsgRepository.getReportIdsByApprovecode(approvedoc.getApprovecode());
        List<Tb_yearlycheck_report> yearlycheckReports = yearlyCheckReportRepository.findByIdIn(ids);
        for(Tb_yearlycheck_report report : yearlycheckReports){
            report.setState("已退回");
        }
        yearlyCheckReportRepository.save(yearlycheckReports);
        List<Tb_flows> flowGet = flowsRepository.findFlows(taskid, userDetails.getUserid(), "处理中");
        Tb_node node = nodeRepository.findByNodeid(flowGet.get(0).getNodeid());

        List<Tb_flows> flows = flowsRepository.findByTaskid(taskid);// 获取本流程修改状态
        for (Tb_flows flow : flows) {
            flow.setState(Tb_flows.STATE_FINISHED);// 完成
        }

        List<Tb_node> nodes = nodeRepository.findByWorkidOrderBySortsequence(node.getWorkid());
        Tb_node node1 = nodes.get(nodes.size() - 1);
        Tb_flows flows1 = new Tb_flows();
        flows1.setNodeid(node1.getId());
        flows1.setText(node1.getText());
        flows1.setDate(dateInt);
        flows1.setTaskid(taskid);
        flows1.setMsgid(flows.get(0).getMsgid());
        flows1.setState(Tb_flows.STATE_FINISHED);// 完成
        flowsRepository.save(flows1);// 结束整个流程
    }
}
