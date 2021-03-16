package com.wisdom.web.service;

import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.Tb_borrowdoc;
import com.wisdom.web.entity.Tb_task;
import com.wisdom.web.entity.Tb_user;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2017/10/24 0024.
 */
@Service
@Transactional
public class MissionService {

    @Autowired
    TaskRepository taskRepository;

	@Autowired
	BorrowDocRepository borrowDocRepository;

	@Autowired
	UserRepository userRepository;

    /**
     * 获取全部用户组
     * @param state
     * @param type
     * @param page
     * @param limit
     * @param sort
     * @return
     */
    public Page getTask(String condition,String operator,String content,String state, String type, int page, int limit, Sort sort){
    	if (condition!=null && condition.equals("type")){
    		condition = "tasktype";
		}
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        PageRequest pageRequest = new PageRequest(page-1, limit, sort == null ?
				new Sort(Sort.Direction.DESC, "tasktime","taskid") : sort);
    	if (state.equals("完成")) {
			Page<Tb_task> taskList;
			if (condition!=null && !condition.equals("") && operator!=null && !operator.equals("") && content!=null && !content.equals("")){
				Specifications specifications = Specifications.where(new SpecificationUtil("loginname", "equal", userDetails.getUserid()))
						.and(new SpecificationUtil("state", "equal", state))
						.and(new SpecificationUtil("tasktype", "equal", type))
						.and(new SpecificationUtil(condition, operator, content));// 过滤
				taskList = taskRepository.findAll(specifications ,pageRequest);
			}else {
				taskList = taskRepository.findByLoginnameAndStateAndTasktype(pageRequest,userDetails.getUserid(),state,type);
			}
			if(condition!=null && condition.equals("state") && !content.equals("完成")){
				taskList = taskRepository.findByLoginnameAndStateAndTasktype(pageRequest,userDetails.getUserid(),state,type);
			}
    		List<Tb_task> taskTemp = new ArrayList<>();
    		for (int i = 0; i < taskList.getContent().size(); i++) {
    			Tb_task task = new Tb_task();
    			BeanUtils.copyProperties(taskList.getContent().get(i),task);
    			taskTemp.add(task);
    		}
    		for (int i = 0; i < taskTemp.size(); i++) {
    			Tb_task task = taskTemp.get(i);
    			if (task.getApprovetext() != null) {
    				if (!task.getApprovetext().equals("完成") && !task.getApprovetext().equals("结束")) {
        				task.setState("待处理");
        			}
    			}
    		}
			if (condition!=null && !condition.equals("") && condition.equals("state") && !content.equals("")){
				for (int i = 0; i < taskTemp.size(); i++) {
					Tb_task task = taskTemp.get(i);
					if (!task.getState().equals(content)) {
						taskTemp.remove(i);
						i=0;
					}
				}
			}
			if (condition!=null && !condition.equals("") && condition.equals("state") && !content.equals("")){
				for (int i = 0; i < taskTemp.size(); i++) {
					Tb_task task = taskTemp.get(i);
					if (!content.equals("完成") && task.getState().equals("完成")) {
						taskTemp.remove(i);
						i=0;
					}
				}
			}
    		for (int i = 0; i < taskTemp.size() - 1; i++) {
                Tb_task task = taskTemp.get(i);
                for (int j = i + 1; j < taskTemp.size(); j++) {
                    if (task.getText().equals(taskTemp.get(j).getText())) {
                    	taskTemp.remove(i);
                    }
                }
            }
            //代替临时账户实体查档或电子查档时，单据在完成后只有临时账户才能查看
			if("实体查档".equals(type) || "电子查档".equals(type)){
				for(int i=0;i<taskTemp.size();i++){
					Tb_task task = taskTemp.get(i);
					if("完成".equals(task.getState())){
						Tb_borrowdoc tb_borrowdoc = borrowDocRepository.getBorrowDocByTaskid(task.getId());//获取单据查档人
						if(tb_borrowdoc==null){
							continue;
						}
						Tb_user tb_user = userRepository.findByUserid(tb_borrowdoc.getBorrowmanid());
						if("外来人员".equals(tb_user.getOutuserstate())){
							taskTemp.remove(i);
						}
					}
				}
			}
    		return new PageImpl<Tb_task>(taskTemp, pageRequest, taskList.getTotalElements());
    	}
		if (condition!=null && !condition.equals("") && operator!=null && !operator.equals("") && content!=null && !content.equals("")){
			Specifications specifications = Specifications.where(new SpecificationUtil("loginname", "equal", userDetails.getUserid()))
					.and(new SpecificationUtil("state", "equal", state)).and(new SpecificationUtil("tasktype", "equal", type))
					.and(new SpecificationUtil(condition, operator, content));// 过滤为当前机构
			return taskRepository.findAll(specifications ,pageRequest);
		}
		return taskRepository.findByLoginnameAndStateAndTasktype(pageRequest,userDetails.getUserid(),state,type);
    }
}