package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.secondaryDataSource.entity.*;
import com.wisdom.util.*;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.RoleRepository;
import com.wisdom.web.repository.UserRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.NodesettingService;
import com.wisdom.web.service.UserGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户组管理控制器
 * Created by xd on 2017/9/28.
 */
@Controller
@RequestMapping(value = "/userGroup")
public class UserGroupController {
	
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${system.iarchivesx.syncpath}")
    private String iarchivesxSyncPath;//声像数据同步请求地址

    @Value("${find.sx.data}")
    private Boolean openSxData;//是否可检索声像系统的声像数据

    @Autowired
    UserGroupService userGroupService;

    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @RequestMapping("/main")
    public String index(Model model, String isp) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        List<Tb_role> roles= roleRepository.findBygroups(userDetails.getUserid());
        String userType="";
        for (Tb_role role:roles){
            String name = role.getRolename();
            if(name.equals("安全保密管理员")) {
                userType="bm";
            }else if(name.equals("系统管理员")){
                userType="xt";
            }
        }
        Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
        model.addAttribute("loginname", userDetails.getLoginname());
        model.addAttribute("functionButton", functionButton);
        model.addAttribute("userType",userType);
        model.addAttribute("openSxData",openSxData);
        return "/inlet/userGroup";
    }

    @RequestMapping("/getUserGroup")
    @ResponseBody
    public Page<Object> getUserGroup(int page, int limit,String xtType) {
        Page list=null;
        if("声像系统".equals(xtType))
            list = userGroupService.getSxUserGroup(page, limit);
        else
            list = userGroupService.getUserGroup(page, limit);
        logger.info(list.toString());
        return list;
    }

    @RequestMapping("/getUserGroupString")
    @ResponseBody
    public List getUserGroupString(String xtType) {
        if("声像系统".equals(xtType)){
            return userGroupService.getSxUserGroupString();
        }else{
            return userGroupService.getUserGroupString();
        }
    }

    @RequestMapping("/userGroupSeting")
    @ResponseBody
    public ExtMsg userGroupSeting(String[] groupids, String userid, String xtType) {
        List list = new ArrayList<>();
        if("声像系统".equals(xtType)){
            userGroupService.userSxGroupSeting(groupids, userid);
        }else{
            userGroupService.userGroupSeting(groupids, userid);
        }
        if (list != null) {
            //更新个人数据节点缓存
            GuavaCache.removeValueByKey(userid + GuavaUsedKeys.NODE_ROLE_LIST_SUFFIX);
            return new ExtMsg(true, "设置成功", null);
        }
        return new ExtMsg(false, "设置失败", null);
    }

    @RequestMapping("/myUserGroup")
    @ResponseBody
    public ExtMsg myUserGroup(String userId,String xtType) {
        String[] userArray = userId.split(",");
        if (userArray.length > 1 || userArray.length == 0) {
            return new ExtMsg(true, "", null);
        }
        List list = new ArrayList<>();
        if("声像系统".equals(xtType)){
            list = userGroupService.mySxUserGroup(userId);
        }else{
            list = userGroupService.myUserGroup(userId);
        }
        if (list != null) {
            return new ExtMsg(true, "", GainField.getFieldValues(list, "roleid"));
        }
        return new ExtMsg(false, "", null);
    }

    @RequestMapping("/myUserGrouped")
    @ResponseBody
    public ExtMsg myUserGrouped(String userId) {
        String[] userArray = userId.split(",");
        List<Tb_role> list = userGroupService.myUserGroup(userId);
        if (list != null) {
            return new ExtMsg(true, "", list);
        }
        return new ExtMsg(false, "", null);
    }

    @LogAnnotation(module="安全维护-用户组管理",sites = "1",fields = "rolename",connect = "##用户组名",startDesc = "操作用户组，条目详细：")
    @RequestMapping("/userGroupAddSubmit")
    @ResponseBody
    public ExtMsg userGroupAddSubmit(Tb_role role) {
        Tb_role role1 = userGroupService.userGroupAddSubmit(role);
        //增加声像用户组
        userGroupService.addSxUserGroup(role1);
        if (role1 != null) {
            /*Tb_user user=new Tb_user();
            user.setLogin_ip(iarchivesxSyncPath);//设置跳转地址
            user.setRemark(role1.getRoleid());*/
            return new ExtMsg(true, "操作完成", null);
        }
        return new ExtMsg(true, "操作失败", null);
    }

    /**
     * 获取用户组对象,用于回显
     *
     * @param roleid
     * @return
     */
    @RequestMapping("/getUserGroupByid")
    @ResponseBody
    public ExtMsg getUserGroupByid(String roleid) {
        return new ExtMsg(true, "成功", userGroupService.getUserGroupByid(roleid));
    }

    @RequestMapping("/getAllGn")
    @ResponseBody
    public List<ExtTree> getAllGn(String fnid, String roleId,String xtType) {
        if("声像系统".equals(xtType)){
            return userGroupService.getSxAllGn(fnid, roleId,xtType);
        }
        return userGroupService.getAllGn(fnid, roleId,xtType);
    }

    @RequestMapping("/getAllSjQx")
    @ResponseBody
    public List<ExtTree> getAllSjQx(String pcid, String roleId,String xtType) {
        return nodesettingService.getUserGroupCheckNodeByParentId(pcid, roleId,xtType);
    }

    @RequestMapping("/getAllOrganAuth")
    @ResponseBody
    public List<ExtTree> getAllOrganAuth(String pcid, String roleId) {
        return nodesettingService.getUserGroupCheckOrganByParentId(pcid, roleId);
    }

    /**
     * 设置功能权限
     *
     * @param gnList 功能权限id数组
     * @param roleId 用户组id
     * @return
     */
//    @LogAnnotation(module="用户组管理",startDesc = "设置用户组功能权限，功能权限id为：",sites="1")
    @RequestMapping("/UserGroupSetGnSubmit")
    @ResponseBody
    public ExtMsg UserGroupSetGnSubmit(String[] gnList, String roleId,String xtType) {
        if("声像系统".equals(xtType)){
            List list = userGroupService.UserGroupSetSxGnSubmit(gnList, roleId,xtType);
            if (list != null && list.size() > 0) {
                return new ExtMsg(true, "设置功能权限成功", null);
            }
            return new ExtMsg(false, "设置功能权限失败", null);
        }else{
            List<Tb_role_function> list = userGroupService.UserGroupSetGnSubmit(gnList, roleId,xtType);
            if (list != null && list.size() > 0) {
                return new ExtMsg(true, "设置功能权限成功", null);
            }
            return new ExtMsg(false, "设置功能权限失败", null);
        }

    }

    /**
     * 设置数据权限
     *
     * @param nodeStr 数据权限id
     * @param roleId  用户组id
     * @return
     */
    @RequestMapping("/UserGroupSetSjSubmit")
    @ResponseBody
    public ExtMsg UserGroupSetSjSubmit(String nodeStr, String roleId,String xtType) {
        String[] nodeList = nodeStr.split(",");//解决：传参数受限10000个的问题
        List<Tb_role_data_node> list = new ArrayList<>();
        try {
            list = userGroupService.UserGroupSetSjSubmit(nodeList, roleId,xtType);
        } catch (Exception e) {
            return new ExtMsg(false, "设置数据权限失败", null);
        }
        if (list != null) {
            if("声像系统".equals(xtType)){
                Tb_user user=new Tb_user();
                user.setLogin_ip(iarchivesxSyncPath);//设置跳转地址
                return new ExtMsg(true,"设置数据权限成功",user);
            }else{
                GuavaCache.removeValueByKey(GuavaUsedKeys.NODE_ALL_LIST);//清空记录在变量中的所有节点数据,所有用户重新获取节点权限缓存
                //更新数据节点更新时间
                nodesettingService.updateNodeChangeTime();
                return new ExtMsg(true, "设置数据权限成功", null);
            }
        }
        return new ExtMsg(false, "设置数据权限失败", null);
    }

    /**
     * 设置机构权限
     *
     * @param organList 机构权限id数组
     * @param roleId    用户组id
     * @return
     */
    @RequestMapping("/userGroupSetOrganSubmit")
    @ResponseBody
    public ExtMsg userGroupSetOrganSubmit(String[] organList, String roleId) {
        List<Tb_role_organ> list;
        try {
            list = userGroupService.userGroupSetOrganSubmit(organList, roleId);
        } catch (Exception e) {
            return new ExtMsg(false, "设置机构权限失败", null);
        }
        if (list != null) {
            return new ExtMsg(true, "设置机构权限成功", null);
        }
        return new ExtMsg(false, "设置机构权限失败", null);
    }

    @LogAnnotation(module="安全维护-用户组管理",sites = "1",startDesc = "删除用户组，条目编号：")
    @RequestMapping("/userGroupDel")
    @ResponseBody
    public ExtMsg userGroupDel(String[] groupIds) {
        int i = userGroupService.userGroupDel(groupIds);
        if (i != 0) {
            userGroupService.userGroupSxDel(groupIds);
            /*Tb_user user=new Tb_user();
            user.setLogin_ip(iarchivesxSyncPath);//设置跳转地址
            user.setRemark(String.join(",",groupIds));*/
            return new ExtMsg(true, "删除成功", null);
        }
        return new ExtMsg(false, "删除失败", null);
    }

    @RequestMapping("/order/{roleid}/{targetorder}")
    @ResponseBody
    public ExtMsg modifyorder(@PathVariable String roleid, @PathVariable String targetorder) {
        Tb_role userGroup = userGroupService.getUserGroupByid(roleid);
        userGroupService.modifyUsergroupOrder(userGroup, Integer.parseInt(targetorder));
        return null;
    }

    //文件权限tree
    @RequestMapping(value = "/getWjList", method = RequestMethod.GET)
    @ResponseBody
    public List<ExtTree> getWjList(String usergroupid) {
        return userGroupService.getWjList(usergroupid);
    }

    //提交文件权限
    @RequestMapping(value="/setWJQXbtn")
    @ResponseBody
    public ExtMsg setWJQXbtn(String userid,String[] lylist,String[] gllist){
        userGroupService.setWJQXbtn(lylist,gllist,userid);
        return new ExtMsg(true,"设置文件权限成功","");
    }

    //获取组内所有用户
    @RequestMapping("/getUsersOnUserGroup")
    @ResponseBody
    public void getUsersOnUserGroup(String roleid,int page,int limit,HttpServletResponse httpServletResponse){
        userGroupService.getUsersOnUserGroup(roleid,page,limit, httpServletResponse);
    }

    /**
     * 获取用户组内用户
     * @param roleid 用户组id
     * @return
     */
    @RequestMapping("/getUsers")
    @ResponseBody
    public ExtMsg getUsers(String roleid) {
        List<Tb_user> users = userRepository.findUsersByRoleid(roleid);
        return new ExtMsg(true, "成功", GainField.getFieldValues(users,"userid"));
    }

    /**
     * 获取所有用户
     * @return
     */
    @RequestMapping("/getAllUsers")
    @ResponseBody
    public List<Tb_user> getAllUsers(String organid) {
        return userGroupService.getAllUsers(organid);
    }

    /**
     *设置组内用户
     * @return
     */
    @RequestMapping("/addUsers")
    @ResponseBody
    public ExtMsg addUsers(String roleid,String[] userids) {
        List<Tb_user_role> userRoles = userGroupService.addUsers(roleid,userids);
        if(userRoles.size()>0){
            return new ExtMsg(true,"",null);
        }
        return new ExtMsg(false,"",null);
    }

    /**
     *删除组内用户
     * @return
     */
    @RequestMapping("/delUserOnGroup")
    @ResponseBody
    public ExtMsg delUserOnGroup(String roleid,String[] userids) {
        int count = 0;
        count = userGroupService.delUserOnGroup(roleid,userids);
        if(count > 0){
            return new ExtMsg(true,"",null);
        }
        return new ExtMsg(false,"",null);
    }
}