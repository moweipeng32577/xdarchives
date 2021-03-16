package com.wisdom.web.controller;

import com.wisdom.util.GainField;
import com.wisdom.web.entity.*;
import com.wisdom.web.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by Administrator on 2018/11/30.
 */
@Controller
@RequestMapping(value = "/checkGroup")
public class CheckGroupController {

    @Autowired
    CheckGroupService checkGroupService;


    
    @RequestMapping("/main")
    public String main() {
        return "/inlet/checkGroup";
    }

    @RequestMapping("/getCheckGroup")
    @ResponseBody
    public List<Szh_check_group> getCheckGroup(String type) {
        return checkGroupService.getCheckGroup(type);
    }

    @RequestMapping("/checkGroupAddSubmit")
    @ResponseBody
    public ExtMsg userGroupAddSubmit(Szh_check_group check_group) {
        Szh_check_group check_groupnew = checkGroupService.userGroupAddSubmit(check_group);
        if (check_groupnew != null) {
            return new ExtMsg(true, "操作完成", null);
        }
        return new ExtMsg(true, "操作失败", null);
    }

    @RequestMapping("/getCheckGroupform")
    @ResponseBody
    public ExtMsg getCheckGroupForm(String checkgroupid) {
        return new ExtMsg(true, "", checkGroupService.getCheckGroupForm(checkgroupid));
    }

    @RequestMapping("/delCheckGroup")
    @ResponseBody
    public ExtMsg deleteCheckGroup(String[] checkgroupids) {
        int count = checkGroupService.deletetCheckGroup(checkgroupids);
        if(count>0){
            return new ExtMsg(true, "",count);
        }else{
            return new ExtMsg(false, "",null);
        }

    }

    @RequestMapping("/getCheckUser")
    @ResponseBody
    public List<BackCheckUser> getCheckUser(String checkgroupid) {
        List<BackCheckUser> backCheckUsers = checkGroupService.getCheckUser(checkgroupid);
            return backCheckUsers;

    }

    @RequestMapping("/getUser")
    @ResponseBody
    public ExtMsg getUser(String checkgroupid) {
        List<Tb_user> Users = checkGroupService.getUser(checkgroupid);
        return new ExtMsg(true,"成功", GainField.getFieldValues(Users,"userid"));
    }

    @RequestMapping("/setCheckUser")
    @ResponseBody
    public ExtMsg setCheckUser(String checkgroupid, String[] userids) {
        List<Szh_check_user> Users = checkGroupService.setCheckUser(checkgroupid,userids);
        if(Users.size()>0){
            return new ExtMsg(true,"成功",null);
        }else {
            return new ExtMsg(false,"失败",null);
        }
    }

    @RequestMapping("/delCheckUser")
    @ResponseBody
    public ExtMsg deleteCheckUser(String[] checkuserids) {
        int count = checkGroupService.deletetCheckUser(checkuserids);
        if(count>0){
            return new ExtMsg(true, "",count);
        }else{
            return new ExtMsg(false, "",null);
        }
    }

    @RequestMapping("/getCheckGroupAll")
    @ResponseBody
    public List<Szh_check_group> getCheckGroupAll() {
        return checkGroupService.getCheckGroupAll();
    }
}
