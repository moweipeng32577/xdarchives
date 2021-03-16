package com.wisdom.web.controller;

import com.wisdom.web.entity.*;
import com.wisdom.web.repository.MissionUserRepository;
import com.wisdom.web.repository.UserRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 *  待办事项控制器
 * Created by xd on 2017/9/28.
 */
@Controller
@RequestMapping(value = "/mission")
public class MissionAdminsController {
    
    @Autowired
    MissionService missionService;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    MissionUserRepository missionUserRepository;

    @Value("${system.report.server}")
    private String reportServer;//报表服务

    @Value("${CA.netcat.use}")
    private String netcatUse;//是否使用网证通电子签章  1使用  0禁用

    @RequestMapping("/main")
    public String userGroup(Model model) {
        model.addAttribute("reportServer",reportServer);
        model.addAttribute("netcatUse",netcatUse);
        return "/inlet/missionAdmins";
    }

    @RequestMapping("/getSpState")
    @ResponseBody
    public List<ExtNcTree> getSpState(){
        List<ExtNcTree> trees = new ArrayList<>();
        ExtNcTree tree = new ExtNcTree();
        tree.setFnid("1");
        tree.setLeaf(true);
        tree.setText("待处理");
        trees.add(tree);

        tree = new ExtNcTree();
        tree.setFnid("2");
        tree.setLeaf(true);
        tree.setText("完成");
        trees.add(tree);

//        tree = new ExtNcTree();
//        tree.setFnid("3");
//        tree.setLeaf(true);
//        tree.setText("代理");
//        trees.add(tree);

        return trees;
    }

    @RequestMapping("/getTask")
    @ResponseBody
    public Page getTask(String condition,String operator,String content,String state,String type,int page, int limit, String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        return missionService.getTask(condition,operator,content,state,type,page,limit,sortobj);
    }

    @RequestMapping("/setQxUserid")
    @ResponseBody
    public ExtMsg setQxUserid(String useridList){
    	if (useridList != null) {
    		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    		String userid = userRepository.findByRealname(userDetails.getRealname()).getUserid();
    		String[] idList = useridList.split("∪");
    		for (int i = 0; i < idList.length; i++) {
    			Tb_mission_user missionUser = new Tb_mission_user();
    			missionUser.setAgentuserid(userid);
    			missionUser.setUserid(idList[i]);
    			missionUserRepository.save(missionUser);
    		}
            return new ExtMsg(true, "设置用户权限成功", null);
    	}
    	return null;
    }
}