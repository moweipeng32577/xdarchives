package com.wisdom.web.service;

import com.wisdom.web.repository.TaskRepository;
import com.wisdom.web.repository.ThematicRepository;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2020/9/18.
 */
@Service
@Transactional
public class BranchAuditServiec {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ThematicRepository thematicRepository;

    public int updateTask(String ids,String tasktype){
        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String[] thematics=ids.split(",");
        int count = 0;
        for(String id: thematics){
            //更新任务状态为“完成”
            taskRepository.updateByBorrowmsgidAndUserid(id,tasktype,user.getUserid());
            //删除其他人任务审核提醒
            taskRepository.deleteByBorrowmsgid(id,tasktype,"待处理");
            count++;
        }
        return count;
    }

    public int updateThematicPublishstate(String ids,String state,String approvetext){
        String[] thematics=ids.split(",");
        int count = 0;
        for(String id: thematics){
            count+=thematicRepository.updateThematicPublishstate(state,id,approvetext);
        }
        return count;
    }
}
