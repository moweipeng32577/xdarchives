package com.wisdom.web.service;

import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2020/10/14.
 */
@Service
@Transactional
public class BusinessYearlyCheckService {



    @Autowired
    YearlyCheckReportRepository yearlyCheckReportRepository;

    @Autowired
    YearlyCheckElectronicRepository yearlyCheckElectronicRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    YearlyCheckApproveMsgRepository yearlyCheckApproveMsgRepository;

    @Autowired
    NodeRepository nodeRepository;

    @Autowired
    FlowsRepository flowsRepository;

    @Autowired
    YearlyCheckApproveDocRepository yearlyCheckApproveDocRepository;

    @Autowired
    UserRepository userRepository;

    @Value("${system.document.rootpath}")
    private String rootpath;// 系统文件根目录

    public Tb_yearlycheck_report addSubmit(Tb_yearlycheck_report yearlycheckReport, MultipartFile source) {
        yearlycheckReport.setState("未提交");
        yearlycheckReport = yearlyCheckReportRepository.save(yearlycheckReport);
        String[] subFileName = source.getOriginalFilename().split("\\\\");
        String fileName = source.getOriginalFilename();
        if (subFileName.length > 1) {
            fileName = subFileName[subFileName.length - 1];
        }
        File saveFile = new File(getStorageBaseDir(yearlycheckReport.getId()),fileName);
        if (saveFile.exists()) {//检查文件或目录是否存在  存在就删除
            saveFile.delete();
        }
        try {
            source.transferTo(saveFile);
            Tb_yearlycheck_electronic ele = new Tb_yearlycheck_electronic();
            ele.setFilename(fileName);
            ele.setReportid(yearlycheckReport.getId());
            ele.setFilesize(String.valueOf(saveFile.length()));
            ele.setFilepath(getStorageBaseDir(yearlycheckReport.getId()).replace(rootpath, ""));
            ele.setFiletype(fileName.substring(fileName.lastIndexOf('.') + 1));
            yearlyCheckElectronicRepository.save(ele);//保存电子文件
        } catch (Exception e) {
            e.printStackTrace();
        }
        return yearlycheckReport;
    }

    public String getStorageBaseDir(String reportid) {
        String dir = "";
        Calendar cal = Calendar.getInstance();
        dir = rootpath +"/electronics/yearlycheck/"  + cal.get(Calendar.YEAR) + "/" +
                (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE) +"/"+reportid.trim();
        File upDir = new File(dir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }
        return dir;
    }

    public Page<Tb_yearlycheck_report> getYearlyCheckReports(int page, int limit, String condition, String operator, String content, Sort sort) {
        Specifications specifications = null;
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        return yearlyCheckReportRepository.findAll(specifications, pageRequest);
    }

    public Page<Tb_yearlycheck_report> getYearlyCheckReportsByState(String state, int page, int limit, String condition, String operator, String content, Sort sort) {
        Specifications specifications = null;
        specifications = Specifications.where(new SpecificationUtil("state","equal",state));
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        return yearlyCheckReportRepository.findAll(specifications, pageRequest);
    }

    public Integer deleteYearlyCheckElectronic(String[] reportids) {
        Integer num = 0;
        List<Tb_yearlycheck_electronic> electronics = yearlyCheckElectronicRepository.findByReportidIn(reportids);//获取删除电子文件
        for (Tb_yearlycheck_electronic electronic : electronics) {
            File file = new File(rootpath + electronic.getFilepath() + "/" + electronic.getFilename());
            file.delete();//删除电子文件
        }
        yearlyCheckElectronicRepository.deleteByReportidIn(reportids);
        num = yearlyCheckReportRepository.deleteByIdIn(reportids);
        return num;
    }

    public void saveSubmit(Tb_yearlycheck_report yearlycheckReport) {
        yearlyCheckReportRepository.save(yearlycheckReport);
    }

    public Page<Tb_yearlycheck_report> getYearlyCheckApproveReports(String[] ids, int page, int limit, Sort sort) {
        Specifications specifications = null;
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

    public Tb_yearlycheck_approvedoc approveDocSubmit(Tb_yearlycheck_approvedoc approvedoc, String spman, String[] ids) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        Tb_task task = new Tb_task();
        task.setLoginname(spman);
        task.setState(Tb_task.STATE_WAIT_HANDLE);// 待处理
        task.setText(approvedoc.getSubmiter() + " " + new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss").format(new Date())
                + " 年检审核");
        task.setType("年检");
        task.setTime(new Date());
        task = taskRepository.save(task);// 添加任务

        String approvecode = UUID.randomUUID().toString().replace("-", "");// 表单号用uuid生成
        approvedoc.setState("已送审");
        approvedoc.setApprovecode(approvecode);
        approvedoc.setSubmiterid(userDetails.getUserid());

        List<Tb_yearlycheck_approvemsg> approvemsgs = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            Tb_yearlycheck_approvemsg approvemsg = new Tb_yearlycheck_approvemsg();
            approvemsg.setApprovecode(approvecode);
            approvemsg.setReportid(ids[i]);
            Tb_yearlycheck_report yearlycheckReport = yearlyCheckReportRepository.findById(ids[i]);
            yearlycheckReport.setState("已提交");
            approvemsgs.add(approvemsg);
        }
        yearlyCheckApproveMsgRepository.save(approvemsgs);
        Tb_node node = nodeRepository.getNode("年检审核");
        Tb_flows flows = new Tb_flows();
        flows.setText("启动");
        flows.setState(Tb_flows.STATE_FINISHED);// 完成
        flows.setTaskid(task.getId());
        flows.setMsgid(approvecode);
        flows.setDate(dateInt);
        flows.setNodeid(node.getId());
        flowsRepository.save(flows);// 添加启动流程实例

        Tb_flows flows1 = new Tb_flows();
        flows1.setText(node.getText());
        flows1.setState(Tb_flows.STATE_HANDLE);// 处理中
        flows1.setDate(dateInt);
        flows1.setTaskid(task.getId());
        flows1.setMsgid(approvecode);
        flows1.setSpman(spman);
        flows1.setNodeid(node.getId());
        flowsRepository.save(flows1);// 添加下一流程实例
        Tb_user user = userRepository.findByUserid(spman);
        approvedoc.setApprovetext(node.getText());
        approvedoc.setApproveman(user.getRealname());
        return yearlyCheckApproveDocRepository.save(approvedoc);// 添加年检单据
    }
}
