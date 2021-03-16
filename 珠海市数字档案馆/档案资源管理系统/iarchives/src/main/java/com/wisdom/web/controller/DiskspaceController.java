package com.wisdom.web.controller;

import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.web.entity.Diskspace;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.repository.TaskRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.DiskspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;

/**
 * Created by RonJiang on 2018/5/5 0005.
 */
@Controller
@RequestMapping(value = "/diskspace")
public class DiskspaceController {

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录

    @Autowired
    DiskspaceService diskspaceService;
    
    @Autowired
    TaskRepository taskRepository;
    
    @Autowired
    private WebSocketService webSocketService;

    @RequestMapping("/main")
    public String main(String taskid) {
    	if (taskid != null && !taskid.equals("")) {
    		taskRepository.updateByTaskid(taskid);
    	}
        return "/inlet/diskspace";
    }

    /**
     * 获取电子文件存储磁盘的详细信息
     * @return
     */
    @RequestMapping("/getDiskspace")
    @ResponseBody
    public ExtMsg getDiskspace(){
        boolean driverExists = false;
        String drivernumber = rootpath.substring(0,1);
        File[] roots = File.listRoots();// 获取磁盘分区列表
        for (File file : roots) {
            if(file.toString().startsWith(drivernumber)){
                driverExists = true;
                Diskspace diskspace = diskspaceService.getDiskspaceDetail(file,file.toString().substring(0,1));
                SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                webSocketService.noticeRefresh(userDetails.getUserid());//刷新系统管理员用户消息
                return new ExtMsg(true,"获取磁盘空间数据成功",diskspace);
            }
        }
        if(!driverExists){
            return new ExtMsg(false,"获取磁盘空间数据失败",null);
        }
        return null;
    }
}