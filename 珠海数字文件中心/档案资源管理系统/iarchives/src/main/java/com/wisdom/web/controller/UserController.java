package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wisdom.secondaryDataSource.entity.Tb_user_sx;
import com.wisdom.util.*;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.*;
import com.xdtech.project.lot.device.entity.DeviceArea;
import com.xdtech.project.lot.device.entity.Tb_user_area;
import com.xdtech.project.lot.device.entity.Tb_user_device;
import com.xdtech.project.lot.device.repository.DeviceAreaRepository;
import com.xdtech.project.lot.device.repository.DeviceRepository;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.util.ListHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 用户管理控制器
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RightOrganRepository rightOrganRepository;

    @Autowired
    UserNodeTempRepository userNodeTempRepository;

    @Autowired
    ElectronBorrowService electronBorrowService;

    @Autowired
    InformService informService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    DeviceAreaRepository deviceAreaRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    InFormRepository inFormRepository;

    @Autowired
    TaskService taskService;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    UserFillSortRepository userFillSortRepository;

    @Value("${system.document.rootpath}")
    private String rootpath;

    @Value("${system.iarchivesx.syncpath}")
    private String iarchivesxSyncPath;//数据同步请求地址

    @Value("${system.showChildUser.opened}")
    private boolean showChildUser;//是否显示子机构节点的用户

    @Value("${find.sx.data}")
    private Boolean openSxData;//是否可检索声像系统的声像数据

    @Value("${CA.netcat.use}")
    private String netcatUse;//是否使用网证通电子签章  1使用  0禁用

    @RequestMapping("/main")
    public String index(Model model, String isp) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Tb_role> roles = roleRepository.findBygroups(userDetails.getUserid());
        String userType = "";
        for (Tb_role role : roles) {
            String name = role.getRolename();
            if (name.equals("安全保密管理员")) {
                userType = "bm";
            } else if (name.equals("系统管理员")) {
                userType = "xt";
            }
        }
        Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
        model.addAttribute("functionButton", functionButton);
        model.addAttribute("userType", userType);
        model.addAttribute("showChildUser", showChildUser);
        model.addAttribute("openSxData", openSxData);
        model.addAttribute("netcatUse",netcatUse);
        return "/inlet/user";
    }

    @RequestMapping("/addUserNodeTemp")
    @ResponseBody
    public Integer addUserNodeTemp(String userid, String nodeid) {
        if (userid != null && !"".equals("userid")) {
            List<Tb_user_node_temp> userNodes = userNodeTempRepository.findUserByNodeidAndUniquetag(nodeid, BatchModifyService.getUniquetag());
            String[] id = userid.split("-");
            List<Tb_user_node_temp> user_node_temps = new ArrayList<>();
            for (int i = 0; i < id.length; i++) {
                Tb_user_node_temp node_temp = new Tb_user_node_temp();
                node_temp.setNodeid(nodeid);
                node_temp.setUserid(id[i]);
                node_temp.setUniquetag(BatchModifyService.getUniquetag());
                node_temp.setSortsquence(userNodes.size() + i + 1); //设置序号
                user_node_temps.add(node_temp);
            }
            return userNodeTempRepository.save(user_node_temps).size();
        }
        return 0;
    }

    @RequestMapping("/deleteUserNodeTemp")
    @ResponseBody
    public Integer deleteUserNodeTemp(String userid, String nodeid) {
        if (userid != null && !"".equals("userid")) {
            String[] id = userid.split("-");
            int count = userNodeTempRepository.deleteByUseridInAndUniquetag(id, BatchModifyService.getUniquetag());
            List<Tb_user_node_temp> userNodes = userNodeTempRepository.findUserByNodeidAndUniquetag(nodeid, BatchModifyService.getUniquetag());
            for (int i = 0; i < userNodes.size(); i++) { //重新排序
                userNodes.get(i).setSortsquence(i + 1);
            }
            userNodeTempRepository.save(userNodes);
            return count;
        }
        return 0;
    }

    //更新用户排序
    @RequestMapping("/updateUserNodeTemp")
    @ResponseBody
    public ExtMsg updateNodeTemp(String userid, String nodeid, String type) {
        userService.updateNodeTemp(userid, nodeid, type);
        return new ExtMsg(true, "", null);
    }

    @RequestMapping("/inform")
    public String inform(Model model, String msgid) {
        Tb_inform inform = userService.getInform(msgid);
        if (inform != null) {
            List<Tb_electronic> electronics = informService.getInformFile(inform.getId());
            model.addAttribute("electronics", electronics);
        }
        model.addAttribute("inform", inform);
        model.addAttribute("text", inform == null ? "" : inform.getText());
        return "/inlet/htmledit";
    }

    @RequestMapping("/getborrowinform")
    public String getBorrowinform(Model model, String docid) {
        Tb_borrowdoc borrowdoc = userService.getBorrowInform(docid);
        model.addAttribute("borrwman", borrowdoc.getBorrowman());
        model.addAttribute("borroworgan", borrowdoc.getBorroworgan());
        model.addAttribute("state", borrowdoc.getState());
        model.addAttribute("type", borrowdoc.getType());
        model.addAttribute("borrowts", borrowdoc.getBorrowts());
        model.addAttribute("borrowmd", borrowdoc.getBorrowmd());
        model.addAttribute("desci", borrowdoc.getDesci());
        model.addAttribute("borrowdate", borrowdoc.getBorrowdate());
        return "/inlet/borrowinformation";
    }
    @RequestMapping("/zt")
    public String zt() {
        return "/setting/zt";
    }

    @RequestMapping("/ztsize")
    public String ztsize() {
        return "/setting/ztsize";
    }

    @RequestMapping("/anim")
    public String anim() {
        return "/setting/anim";
    }

    @RequestMapping("/userbg")
    public String userbg() {
        return "/setting/userbg";
    }

    @RequestMapping("/userimg")
    public String userimg() {
        return "/inlet/userimg";
    }

    @RequestMapping("/userMsg")
    public String userMsg(Model model) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("loginname", userDetails.getLoginname());
        model.addAttribute("realname", userDetails.getRealname());
        model.addAttribute("organid", userDetails.getOrganid());
        model.addAttribute("sex", userDetails.getSex());
        return "/setting/userMsg";
    }

    @RequestMapping("/saveicon")
    @ResponseBody
    public String saveicon(@RequestBody List<Tb_Icon> icons, String sysType) {
        return userService.saveicon(icons, sysType);
    }

    @RequestMapping("/delicon")
    @ResponseBody
    public String delicon(String orders, String flag, String sysType) {
        return userService.delicon(orders, flag, sysType);
    }

    /**
     * 删除机构的三员用户
     *
     * @param organId
     * @return
     */
    @RequestMapping("/deleteAdmin")
    @ResponseBody
    public ExtMsg deleteAdmin(String organId) {
        if (!userService.deleteAdmin(organId)) {
            return new ExtMsg(false, "请勿删除该机构的三员用户", null);
        }
        Tb_user userMsg = new Tb_user();
        userMsg.setLogin_ip(iarchivesxSyncPath);//设置跳转地址
        return new ExtMsg(true, "删除成功", userMsg);
    }

    @RequestMapping("/upPersonalized")
    @ResponseBody
    public String upPersonalized(Tb_Personalized personalized) {
        String result = userService.upPersonalized(personalized);
        return result;
    }

    @RequestMapping("/getUnitUser")
    @ResponseBody
    public void getUnitUser(String organName, String organID, boolean ifSearchLeafNode, boolean ifContainSelfNode, int page, int start, int limit, String condition, String operator, String content, HttpServletResponse httpServletResponse, String sort, String xtType) {
        if ("".equals(sort) || sort == null) {
            sort = "[{'property':'createtime','direction':'ASC'}]";
        }
        Sort sortobj = WebSort.getSortByJson(sort);
        organID = organID == null ? "" : organID;
        if ("声像系统".equals(xtType)) {
            Page<Tb_user_sx> list = null;
            if (condition != null) {
                list = userService.findSxBySearch(page, limit, condition, operator, content, organID, ifSearchLeafNode, ifContainSelfNode, sortobj, xtType);
            } else {
                list = userService.getSxUnitUser(organID, ifSearchLeafNode, ifContainSelfNode, page, limit, sortobj, xtType);
            }
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("utf-8");
            String json = JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect);
            try {
                httpServletResponse.getWriter().write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Page<Tb_user> list = null;
            if (condition != null) {
                list = userService.findBySearch(page, limit, condition, operator, content, organID, ifSearchLeafNode, ifContainSelfNode, sortobj, xtType);
            } else {
                list = userService.getUnitUser(organID, ifSearchLeafNode, ifContainSelfNode, page, limit, sortobj, xtType);
            }
            //判断时间是否到期，到期之后把启用改成禁用
            List<Tb_user> listContent = list.getContent();
            for (Tb_user tb_user : listContent) {
                if ("外来人员".equals(tb_user.getOutuserstate()) && (long) 1 == tb_user.getStatus() && new Date().after(tb_user.getExdate())) {
                    tb_user.setStatus((long) 0);
                }
            }
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("utf-8");
            String json = JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect);
            try {
                httpServletResponse.getWriter().write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @RequestMapping("/getUnitOutuser")
    @ResponseBody
    public void getUnitOutuser(int page, int start, int limit, String condition, String operator, String content, String sort, HttpServletResponse httpServletResponse) {
        Sort sortobj = WebSort.getSortByJson(sort);
        Page<Tb_user> list = userService.findBySearchOutUsers(page, limit, condition, operator, content, sortobj);
//        Page<Tb_user> list = userRepository.findByOutuserstateIsNotNull(pageRequest);
        //判断时间是否到期，到期之后把启用改成禁用
        List<Tb_user> listContent = list.getContent();
        for (Tb_user tb_user : listContent) {
            if ((long) 1 == tb_user.getStatus() && new Date().after(tb_user.getExdate())) {
                tb_user.setStatus((long) 0);
            }
        }
        //不使用框架自带的json转换，避免循环引用
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("utf-8");
        String json = JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect);
        try {
            httpServletResponse.getWriter().write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/userids")
    @ResponseBody
    public Page<Tb_user> findUserByUserids(int page, int limit, String userid, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        return userService.findByUserids(page, limit, userid, sortobj);
    }

    @RequestMapping("/usersequence")
    @ResponseBody
    public void findUserBySortsequence(String[] userid, int currentcount, String operate) {
        userService.findBySortquence(userid, currentcount, operate);
    }

    @RequestMapping("/getUserRealname")
    @ResponseBody
    public ExtMsg getUserRealname() {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ExtMsg(true, "获取用户真实姓名成功", userDetails.getRealname());
    }

    @RequestMapping("/editPwd")
    @ResponseBody
    public ExtMsg editPwd(String oldpwd, String pwd) {
        try {
            if(oldpwd.equals(MD5.MD5(pwd))){//跟旧密码一致
                return new ExtMsg(true,"samePwd",oldpwd);
            }
            String msg = userService.editPwd(oldpwd, pwd);
            return new ExtMsg("修改成功，需重新登录才可以切换系统！".equals(msg)||"修改成功".equals(msg) ? true : false, msg, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ExtMsg(false, "修改失败", null);
    }

    @LogAnnotation(module = "用户管理", startDesc = "删除了", endDesc = "用户", sites = "1")
    @RequestMapping("/userDel")
    @ResponseBody
    public ExtMsg userDel(String[] logins) {
        for (int i = 0; i < logins.length; i++) {
            if (logins[i].equals("402880e962d25fc10162d26109fa0005")//系统管理员id
                    || logins[i].equals("402880e962d22b7b0162d23261110004")//安全保密管理员id
                    || logins[i].equals("402880e962d22b7b0162d232ceaf0006")) {//安全审计员id
                return new ExtMsg(true, "无法删除管理员用户", null);
            }
        }
        int i = userService.userDel(logins);
        if (i > 0) {
            //删除声像用户
            userService.userSxDel(logins);
            Tb_user userMsg = new Tb_user();
            userMsg.setLogin_ip(iarchivesxSyncPath);//设置跳转地址
            return new ExtMsg(true, "删除成功", userMsg);
        } else {
            return new ExtMsg(false, "删除失败", null);
        }
    }

    @LogAnnotation(module = "用户管理", startDesc = "修改了", endDesc = "用户", sites = "1", fields = "loginname")
    @RequestMapping("/userEdit/userEditSubmit")
    @ResponseBody
    public ExtMsg userEditSubmit(Tb_user user) {
        if (user.getLoginname().equals("xitong") || user.getLoginname().equals("aqbm")
                || user.getLoginname().equals("aqsj")) {
            return new ExtMsg(true, "无法修改管理员用户", null);
        }
        Tb_user userExist = userService.findByLoginname(user.getLoginname());
        if (userExist == null || userRepository.findByUserid(user.getUserid()).getLoginname().equals(userExist.getLoginname())) {
            if (userService.userEditSubmit(user) == 1) {
                //更新声像用户
                userService.userSxEditSubmit(user);
                Tb_user userMsg = new Tb_user();
                userMsg.setLogin_ip(iarchivesxSyncPath);//设置跳转地址
                userMsg.setUserid(user.getUserid());
                return new ExtMsg(true, "修改成功", userMsg);
            } else {
                return new ExtMsg(false, "修改失败", null);
            }
        } else {
            return new ExtMsg(false, "账号已存在", null);
        }
    }

    @LogAnnotation(module = "用户管理", startDesc = "绑定了", endDesc = "用户", sites = "1", fields = "loginname")
    @RequestMapping("/userBind/userBindSubmit")
    @ResponseBody
    public ExtMsg userBindSubmit(Tb_user user, String cacode, String signcode) {
        //int i = userService.userDel(logins);
        if (user.getLoginname().equals("xitong") || user.getLoginname().equals("aqbm")
                || user.getLoginname().equals("aqsj")) {
            return new ExtMsg(true, "无法修改管理员用户", null);
        }
        if (userService.userBindSubmit(user, cacode, signcode) == 1) {
            return new ExtMsg(true, "绑定成功", null);
        } else {
            return new ExtMsg(false, "修改失败", null);
        }
    }

    @LogAnnotation(module = "用户管理", startDesc = "增加了", endDesc = "用户", sites = "1", fields = "loginname")
    @RequestMapping("/userAdd/userAddSubmit")
    @ResponseBody
    public ExtMsg addUser(Tb_user user, String treetext) {
        ExtMsg msg = new ExtMsg();
        if (userService.findByLoginname(user.getLoginname()) == null) {
            user.setCreatetime(new Date());
            user.setLoginpassword(MD5.MD5("555"));
            user.setOrganid(treetext);
            user.setStatus(1L);
            Integer orders = userRepository.findOrdersByOrganid(treetext);
            orders = ((orders == null || orders < 0) ? 0 : orders) + 1;//若同级机构用户的orders最大值为空或负数，则转化为0，再+1
            user.setSortsequence(orders);
            user = userService.addUser(user);

            //增加声像用户
            userService.addSxUser(user);

            msg.setSuccess(true);
            Tb_user userMsg = new Tb_user();
            userMsg.setLogin_ip(iarchivesxSyncPath);//设置跳转地址
            userMsg.setUserid(user.getUserid());
            msg.setData(userMsg);
            msg.setMsg("添加用户成功");
        } else {
            msg.setSuccess(false);
            msg.setMsg("帐号已存在！");
        }
        return msg;
    }

    @RequestMapping("/getmsg")
    @ResponseBody
    public List getmsg() {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Tb_inform> informs = userService.findInforms(userDetails);
        List<Tb_Msg> msgs = electronBorrowService.findMsg();
        for (Tb_inform inform : informs) {
            Tb_Msg msg = new Tb_Msg();
            msg.setMsgid(inform.getId());
            msg.setMsgtype("1");
            msg.setMsgtypetext(inform.getTitle());
            msg.setMsgtext(inform.getText());
            if (inform.getStick() != null) {
                msg.setBorrowmsgid("*");
            }
            msgs.add(msg);
        }
        return msgs;
    }


    @RequestMapping("/deletetask")
    @ResponseBody
    public ExtMsg deletetask(String tasktype,String taskid){
        return taskService.deletetask(tasktype,taskid);
    }

    @RequestMapping("/deleteTask")
    @ResponseBody
    public ExtMsg deleteTask(String msgId) {
        return taskService.deleteTask(msgId);
    }

    @RequestMapping("/myInform")
    @ResponseBody
    public List myInform() {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.findMyInforms(userDetails);
    }

    @RequestMapping("/getMsg")
    @ResponseBody
    public IndexMsg getMsg() {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Tb_inform> informs = userService.findInforms(userDetails);
        List<Tb_Msg> msgs = electronBorrowService.findMsg();
        for (Tb_inform inform : informs) {
            Tb_Msg msg = new Tb_Msg();
            msg.setMsgid(inform.getId());
            msg.setMsgtype("1");
            msg.setMsgtypetext(inform.getTitle());
            msg.setMsgtext(inform.getText());
            if (inform.getStick() != null) {
                msg.setBorrowmsgid("*");
            }
            msgs.add(msg);
        }
        return new IndexMsg(true, "0", "成功", msgs);
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @RequestMapping("/findBySearch")
    @ResponseBody
    public Page<Tb_user> findBySearch(int page, int limit, String condition, String operator, String content, String organID, boolean ifSearchLeafNode, boolean ifContainSelfNode, String sort, String xtType) {
        Sort sortobj = WebSort.getSortByJson(sort);
        Page<Tb_user> list = userService.findBySearch(page, limit, condition, operator, content, organID, ifSearchLeafNode, ifContainSelfNode, sortobj, xtType);
        return list;
    }

    /**
     * 获取全部功能权限
     *
     * @param fnid
     * @param userId
     * @return
     */
    @RequestMapping("/getAllGn")
    @ResponseBody
    public List<ExtTree> getAllGn(String fnid, String userId, String xtType) {
        if ("声像系统".equals(xtType)) {
            return userService.getAllSxGn(fnid, userId, xtType);
        } else {
            return userService.getAllGn(fnid, userId, xtType);
        }
    }

    /**
     * 获取已设置用户组相关功能权限
     *
     * @param userId
     * @return
     */
    @RequestMapping("/getRoleGn")
    @ResponseBody
    public ExtMsg getRoleGn(String userId) {
        return userService.getRoleGn(userId);
    }

    //    @LogAnnotation(module="用户管理",startDesc = "设置用户功能权限，功能权限id为：",sites="1")
    @RequestMapping("/UserSetGnSubmit")
    @ResponseBody
    public ExtMsg UserSetGnSubmit(String[] gnList, String userId, String xtType) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userId.contains(userDetails.getUserid())) {
            return new ExtMsg(true, "设置功能权限失败，不允许设置自身权限", null);
        }
        if ("声像系统".equals(xtType)) {
            List<Tb_user_function_sx> list = userService.UserSetSxGnSubmit(gnList, userId, xtType);
            if (list != null && list.size() > 0) {
                return new ExtMsg(true, "设置功能权限成功", null);
            }
        } else {
            List<Tb_user_function> list = userService.UserSetGnSubmit(gnList, userId, xtType);
            if (list != null && list.size() > 0) {
                return new ExtMsg(true, "设置功能权限成功", null);
            }
        }
        return new ExtMsg(false, "设置功能权限失败", null);
    }

    // @LogAnnotation(type="用户管理",desc="设置数据权限")
    @RequestMapping("/UserSetSjSubmit")
    @ResponseBody
    public ExtMsg UserSetSjSubmit(String nodeStr, String userId, String xtType) {
        String[] nodeList = nodeStr.split(",");//解决：传参数受限10000个的问题
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userId.contains(userDetails.getUserid())) {
            return new ExtMsg(true, "设置数据权限失败，不允许设置自身权限", null);
        }
        List list = userService.UserSetSjSubmit(nodeList, userId, xtType);
        if (list != null && list.size() > 0) {
            if ("声像系统".equals(xtType)) {
                Tb_user user = new Tb_user();
                user.setLogin_ip(iarchivesxSyncPath);//设置跳转地址
                return new ExtMsg(true, "设置数据权限成功", user);
            } else {
                //清除个人权限数据缓存
                GuavaCache.removeValueByKey(userId + GuavaUsedKeys.NODE_USER_LIST_SUFFIX);
                GuavaCache.removeValueByKey(userId + GuavaUsedKeys.NODE_ROLE_LIST_SUFFIX);
                return new ExtMsg(true, "设置数据权限成功", null);
            }
        }
        return new ExtMsg(false, "设置数据权限失败", null);
    }

    @RequestMapping("/userSetOrganSubmit")
    @ResponseBody
    public ExtMsg userSetOrganSubmit(String[] organList, String userId) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userId.contains(userDetails.getUserid())) {
            return new ExtMsg(true, "设置机构权限失败，不允许设置自身权限", null);
        }
        List<Tb_user_organ> list = userService.userSetOrganSubmit(organList, userId);
        if (list != null && list.size() > 0) {
            return new ExtMsg(true, "设置机构权限成功", null);
        }
        return new ExtMsg(false, "设置机构权限失败", null);
    }

    @RequestMapping("/resetUserPW")
    @ResponseBody
    public ExtMsg resetUserPW(String[] userIds, String loginpassword) {
        int count = userService.resetUserPW(userIds, loginpassword);
        if (count > 0) {
            Tb_user userMsg = new Tb_user();
            userMsg.setLogin_ip(iarchivesxSyncPath);//设置跳转地址
            userMsg.setUserid(String.join(",", userIds));
            return new ExtMsg(true, "初始密码成功", userMsg);
        } else {
            return new ExtMsg(false, "初始密码失败", null);
        }
    }

    @RequestMapping("/order/{userid}/{targetorder}")
    @ResponseBody
    public ExtMsg modifyorder(@PathVariable String userid, @PathVariable String targetorder) {
        Tb_user user = userService.findUser(userid);
        userService.modifyUserOrder(user, Integer.parseInt(targetorder));
        return null;
    }

    @RequestMapping("/endisableUser")
    @ResponseBody
    public ExtMsg endisableUser(String userid) {
        return userService.endiableUser(userid);
    }

    @RequestMapping("/setNewTime")
    @ResponseBody
    public ExtMsg setNewTime(String userid, String expiryDate) {
        return userService.setNewTime(userid, expiryDate);
    }


    @RequestMapping("/changeOrgan")
    @ResponseBody
    public ExtMsg changeOrgan(String[] userIds, String refid) {
        return userService.changeOrgan(userIds, refid);
    }

    @RequestMapping("/userAdd/addAdminValidation")
    @ResponseBody
    public ExtMsg addAdminValidation(String organId) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (organId.equals(userDetails.getLogin_ip()) && "2".equals(userDetails.getUsertype())) {
            return new ExtMsg(false, "不允许删除当前机构的三员账号。", null);
        }
        return userService.addAdminValidation(organId);
    }

    /**
     * 新增三员
     *
     * @param secretAdmin
     * @param systemAdmin
     * @param auditor
     * @param xitongName
     * @param aqbmName
     * @param aqsjName
     * @param organId
     * @return
     */
    @LogAnnotation(module = "用户管理", startDesc = "在机构ID为", endDesc = "上，增加了三员用户", sites = "4")
    @RequestMapping("/userAdd/addAdmin")
    @ResponseBody
    public ExtMsg addAdmin(String secretAdmin, String systemAdmin, String auditor, String xitongName, String aqbmName,
                           String aqsjName, String organId) {
        String msg = "";
        if (userService.findByLoginname(secretAdmin) != null) {
            msg = secretAdmin;
        }
        if (userService.findByLoginname(systemAdmin) != null) {
            msg += msg.length() == 0 ? systemAdmin : "、" + systemAdmin;
        }
        if (userService.findByLoginname(auditor) != null) {
            msg += msg.length() == 0 ? auditor : "、" + auditor;
        }
        if (msg.length() != 0) {
            return new ExtMsg(false, "帐号 " + msg + " 已经被使用，请更换帐号", null);
        }
        return userService.addAmin(secretAdmin, systemAdmin, auditor, xitongName, aqbmName, aqsjName, organId);
    }


    /**
     * 切换平台
     *
     * @param userid
     * @return
     */
    @RequestMapping("/platformchange")
    @ResponseBody
    public void PlatformChange(String userid, String changetype) {
        userService.PlatformChange(userid, changetype);
    }

    /**
     * 添加外来人员
     *
     * @param user
     * @param treetext
     * @return
     */
    @LogAnnotation(module = "用户管理", startDesc = "增加了", endDesc = "临时用户", sites = "1", fields = "loginname")
    @RequestMapping("/userOutAddSubmit")
    @ResponseBody
    public ExtMsg addOutUser(Tb_user user, String treetext) {
        ExtMsg msg = new ExtMsg();
        Date date = new Date();
        if (userService.findByLoginname(user.getLoginname()) == null) {
            user.setCreatetime(new Date());
            String password = "555";
            if (user.getLoginname().length() < 6) {
                //新增临时账号 提供证件非身份证，且证件号码小于6位时，默认密码555
            } else {
                //密码默认为证件号码的后6位
                password = user.getLoginname().substring(user.getLoginname().length() - 6, user.getLoginname().length());
            }
            user.setLoginpassword(MD5.MD5(password));
            if (treetext != null && !"".equals(treetext)) {
                user.setOrganid(treetext);
            } else {
                //用户部门节点：默认选中“外来人员部门”
                Tb_right_organ organ = rightOrganRepository.findByOrganname("外来人员部门");
                if (organ != null) {
                    user.setOrganid(organ.getOrganid());
                }
            }
            user.setStatus(1L);
            Integer orders = userRepository.findOrdersByOrganid(user.getOrganid());
            orders = ((orders == null || orders < 0) ? 0 : orders) + 1;//若同级机构用户的orders最大值为空或负数，则转化为0，再+1
            user.setSortsequence(orders);
            user.setOutuserstate("外来人员");
            user.setOutuserstarttime(date);
            user.setInfodate(user.getInfodate());

            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(date);
            if (user.getInfodate().equals("一天")) {
                rightNow.add(Calendar.DAY_OF_MONTH, 1);
            } else if (user.getInfodate().equals("一周")) {
                rightNow.add(Calendar.WEEK_OF_MONTH, 1);
            } else {// 一月
                rightNow.add(Calendar.MONTH, 1);
            }
            Date dt1 = rightNow.getTime();
            user.setExdate(dt1);
            user = userService.addUser(user);
            msg.setSuccess(true);
            Tb_user userMsg = new Tb_user();
            userMsg.setLogin_ip(iarchivesxSyncPath);//设置跳转地址
            userMsg.setUserid(user.getUserid());
            msg.setData(userMsg);
            msg.setMsg("添加临时用户成功");
        } else {
            msg.setSuccess(false);
            msg.setMsg("帐号已存在！");
        }
        return msg;
    }

    //修改外来人员的到期时间
    @RequestMapping("/UpdateExpireDate")
    @ResponseBody
    public ExtMsg UpdateExpireDate(String userid, String exdate) throws ParseException {
        Tb_user tbUser = userRepository.findByUserid(userid);
        Date nowadays = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = format.parse(exdate + " 23:59:59");
        boolean b = date.after(nowadays);
        if (b) {
            tbUser.setStatus(1L);
        } else {
            tbUser.setStatus(0L);
        }
        tbUser.setExdate(date);


        userRepository.save(tbUser);
        Tb_user tbUser2 = userRepository.findByUserid(userid);
        return new ExtMsg(true, "修改成功", null);
    }


    @LogAnnotation(module = "用户管理", startDesc = "修改了", endDesc = "临时用户", sites = "1", fields = "loginname")
    @RequestMapping("/userOutEditSubmit")
    @ResponseBody
    public ExtMsg userOotEditSubmit(Tb_user user) {
        //int i = userService.userDel(logins);
        if (user.getLoginname().equals("xitong") || user.getLoginname().equals("aqbm")
                || user.getLoginname().equals("aqsj")) {
            return new ExtMsg(true, "无法修改管理员用户", null);
        }
        Tb_user userExist = userService.findByLoginname(user.getLoginname());
        if (userExist == null || userRepository.findByUserid(user.getUserid()).getLoginname().equals(userExist.getLoginname())) {
            if (userService.userOutEditSubmit(user) == 1) {
                Tb_user userMsg = new Tb_user();
                userMsg.setLogin_ip(iarchivesxSyncPath);//设置跳转地址
                userMsg.setUserid(user.getUserid());
                return new ExtMsg(true, "修改成功", userMsg);
            } else {
                return new ExtMsg(false, "修改失败", null);
            }
        } else {
            return new ExtMsg(false, "账号已存在", null);
        }
    }

    @RequestMapping("/getCopyUser")
    @ResponseBody
    public List getCopyUser(String organid, String sourceId, String username, String xtType) {
        if ("声像系统".equals(xtType)) {
            return userService.getSxCopyUser(organid, sourceId, username);
        } else {
            return userService.getCopyUser(organid, sourceId, username);
        }
    }

    @RequestMapping("/copyUser")
    @ResponseBody
    public ExtMsg copyUser(String sourceId, String[] copys, boolean dataCheck, boolean organCheck, boolean fnCheck,
                           boolean roleCheck, boolean nodeCheck, boolean fileCheck, String xtType) {
        if ("声像系统".equals(xtType)) {
            for (int i = 0; i < copys.length; i++) {
                //更新个人数据节点缓存
                GuavaCache.removeValueByKey(copys[i] + "    " + GuavaUsedKeys.NODE_USER_LIST_SUFFIX);
                //更新个人角色数据节点缓存
                GuavaCache.removeValueByKey(copys[i] + "    " + GuavaUsedKeys.NODE_ROLE_LIST_SUFFIX);
            }
            ExtMsg extMsg = userService.copySxUser(sourceId, copys, dataCheck, organCheck, fnCheck, roleCheck, nodeCheck, fileCheck);
            return extMsg;
        }
        for (int i = 0; i < copys.length; i++) {
            //更新个人数据节点缓存
            GuavaCache.removeValueByKey(copys[i] + "    " + GuavaUsedKeys.NODE_USER_LIST_SUFFIX);
            //更新个人角色数据节点缓存
            GuavaCache.removeValueByKey(copys[i] + "    " + GuavaUsedKeys.NODE_ROLE_LIST_SUFFIX);
        }
        ExtMsg extMsg = userService.copyUser(sourceId, copys, dataCheck, organCheck, fnCheck, roleCheck, nodeCheck, fileCheck);
        return extMsg;
    }

    @RequestMapping("/copyCheck")
    @ResponseBody
    public ExtMsg copyCheck(String sourceId, String[] copys, String xtType) {
        if ("声像系统".equals(xtType)) {
            return userService.copySxCheck(sourceId, copys);
        }
        return userService.copyCheck(sourceId, copys);
    }

    @RequestMapping("/countMax")
    @ResponseBody
    public ExtMsg countMax(String sourceId) {
        return userService.countMax(sourceId);
    }

    @RequestMapping(value = "/importUser", method = RequestMethod.POST)
    @ResponseBody
    public String importUser(MultipartFile fileImport, String parentid) throws Exception {
        //		MultipartFile转File
        ObjectMapper json = new ObjectMapper();
        String fileName = fileImport.getOriginalFilename();
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        String tempName = UUID.randomUUID().toString().replace("-", "");
        File tempDir = new File(rootpath + "/importUser");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        File tempFile = File.createTempFile(tempName, prefix, tempDir);
        fileImport.transferTo(tempFile);

        Map<String, String> map = new HashMap();
        map.put("登录名", "loginname");
        map.put("真实名字", "realname");
        map.put("电话号码", "phone");
        map.put("地址", "address");
        map.put("性别", "sex");
        map.put("用户状态", "usertype");
        map.put("机构人员类型", "organusertype");
        map.put("人员职位", "duty");
        map.put("备注", "remark");


        // String names[] ={"登录名","真实名字","电话号码","地址","性别","用户类型"};//表头说明值
        // String keys[] = {"loginname","realname","phone","address","sex","usertype"};//表头字段值

//        //根据map把列头 （中文切换英文）
//        List changeExcelHeadList= ReadExcel.changeExcelHead(map,tempFile.toString());
//        //将转换格式的列头List 转化成数据
//        String[] changeHeads = new String[changeExcelHeadList.size()];
//        changeExcelHeadList.toArray(changeHeads);
//
//        //获取excel表列头
//        List ExcelHeadList = ReadExcel.getHeadField(tempFile);
//        //将转换格式的列头List 转化成数据
//        String[] Heads = new String[ExcelHeadList.size()];
//        ExcelHeadList.toArray(Heads);


        //判断模板字段是否匹配
//        if(!Arrays.equals(changeHeads, Heads)){
//            Map<String, Object> resMap = new ListHashMap<>();
//            resMap.put("success",false);
//            resMap.put("msg","模板格式不匹配，导入失败");
//            String jsonString = json.writeValueAsString(resMap);
//            return  jsonString;
//        }

        //excel转实体（根据列头字段与数据表字段对应）
        List<Object> list = ReadExcel.readExcelToEntity(tempFile, map, "Tb_user");
        tempFile.delete();

        List<Tb_user> userList = new ArrayList<>();
        for (Object o : list) {
            userList.add((Tb_user) o);
        }
        String jsonString = json.writeValueAsString(userService.importUser(userList, parentid));
        return jsonString;
    }

    //根据用户id获取用户信息
    @RequestMapping("/getUser")
    @ResponseBody
    public ExtMsg getUser(String userid) {
        return new ExtMsg(true, "", userService.getUser(userid));
    }

    //设备接入权限列表
    @RequestMapping(value = "/deviceJoinList", method = RequestMethod.GET)
    @ResponseBody
    public List<ExtTree> deviceJoinList(String type, String[] userids) {
        return userService.deviceJoinList(type, userids);
    }

    //设备接入权限提交
    @RequestMapping(value = "/saveDeviceJoinAuthority", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg saveDeviceJoinAuthority(String[] deviceList, String[] userids) {
        userService.saveDeviceJoinAuthority(deviceList, userids);
        return new ExtMsg(true, "设置设备接入权限成功!", null);
    }

    //判断当前用户是否拥有设备接入权限
    @RequestMapping(value = "/isHasDevicePremissions")
    @ResponseBody
    public boolean isHasDevicePremissions(String deviceid) {
        return userService.isHasDevicePremissions(deviceid);
    }

    @RequestMapping(value = "/deviceList", method = RequestMethod.GET)
    @ResponseBody
    public List<ExtTree> deviceExtList(String type, String[] userId) {
        return userService.deviceExtList(type, userId);
    }

    @RequestMapping(value = "/saveDeviceAuthority", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg saveDeviceAuthority(String[] deviceList, String userId) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userId.contains(userDetails.getUserid())) {
            return new ExtMsg(true, "设置设备权限失败，不允许设置自身权限", null);
        }
        List<Tb_user_device> userDevices = userService.saveDeviceAuthority(deviceList, userId);
        if (userDevices != null && userDevices.size() > 0) {
            return new ExtMsg(true, "设置设备权限成功!", null);
        }
        return new ExtMsg(false, "设置设备权限失败!", null);
    }

    //根据用户设备权限查询楼层维护设备
    @RequestMapping(value = "/devicePanel")
    @ResponseBody
    public List devicePanel(String areaid) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List list;
        if (areaid == null) {
            list = deviceRepository.findByUserdevice(userDetails.getUserid());
        } else {
            list = deviceRepository.findByAreaAndUserdevice(areaid, userDetails.getUserid());
        }
        return list;
    }

    //根据用户设备权限查询启用状态的设备，总览
    @RequestMapping(value = "/areaDevice")
    @ResponseBody
    public List areaDevice(String areaid) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List list;
        if (areaid == null) {
            list = deviceRepository.findByEnabledAndUserdevice(userDetails.getUserid());
        } else {
            list = deviceRepository.findByAreaAndEnabledAndUserdevice(areaid, userDetails.getUserid());
        }
        return list;
    }

    //文件权限tree
    @RequestMapping(value = "/getWjList", method = RequestMethod.GET)
    @ResponseBody
    public List<ExtTree> getWjList(String userid) {
        return userService.getWjList(userid);
    }

    //获取文件权限
    @RequestMapping(value = "/getWJQXbtn")
    @ResponseBody
    public ExtMsg getWJQXbtn() {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Tb_user user = userRepository.findByUserid(userDetails.getUserid());
        String sysType;
        if (userDetails.getPlatformchange() != null && !"".equals(userDetails.getPlatformchange())) {
            sysType = user.getPlatformchange();
        } else {
            sysType = userDetails.getType();
        }
        String userid = userDetails.getUserid();
        List userFunction = userService.getWJQXbtn(sysType, userid);
        return new ExtMsg(true, "获取文件权限成功", userFunction);
    }

    //提交文件权限
    @RequestMapping(value = "/setWJQXbtn")
    @ResponseBody
    public ExtMsg setWJQXbtn(String userid, String[] lylist, String[] gllist) {
        userService.setWJQXbtn(lylist, gllist, userid);
        return new ExtMsg(true, "设置文件权限成功", "");
    }

    @RequestMapping(value = "/areaList", method = RequestMethod.GET)
    @ResponseBody
    public List<ExtTree> findAreaList(String[] userId) {
        return userService.findAreaList(userId);
    }

    @RequestMapping(value = "saveAreaAuthority", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg saveAreaAuthority(String[] areaList, String userId) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userId.contains(userDetails.getUserid())) {
            return new ExtMsg(true, "设置区域权限失败，不允许设置自身权限", null);
        }
        List<Tb_user_area> userAreas = userService.saveAreaAuthority(areaList, userId);
        if (userAreas != null && userAreas.size() > 0) {
            return new ExtMsg(true, "设置区域权限成功", null);
        }
        return new ExtMsg(true, "设置区域权限失败", null);
    }

    //根据用户权限过滤区域
    @RequestMapping(value = "/devicearea", method = RequestMethod.GET)
    @ResponseBody
    public List<DeviceArea> deviceAreas(String floorid, String areaid) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!StringUtils.isEmpty(floorid) && StringUtils.isEmpty(areaid)) {
            return deviceAreaRepository.findByFlooridOnUser(floorid, userDetails.getUserid());
        } else if (!StringUtils.isEmpty(floorid) && !StringUtils.isEmpty(areaid)) {
            List<DeviceArea> list = new ArrayList<>();
            list.add(deviceAreaRepository.findByIdOrderByName(areaid));
            return list;
        } else {
            List<DeviceArea> list = new ArrayList<>();
            list = deviceAreaRepository.findByUser(userDetails.getUserid());
            return list;
        }
    }

    @RequestMapping("/getIntergatedServiceNum")
    @ResponseBody
    public Map getIntergatedServiceNum() {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String[] tasktypeStr = new String[5];
        tasktypeStr[0] = "公车预约";
        tasktypeStr[1] = "场地预约";
        tasktypeStr[2] = "部门审核";
        tasktypeStr[3] = "副馆长审阅";
        tasktypeStr[4] = "馆长审阅";
        List<Tb_task> tasks = taskRepository.findTasks(tasktypeStr, userDetails.getUserid(), Tb_task.STATE_WAIT_HANDLE);// 待处理
        List<Tb_inform> informs = inFormRepository.getOrderInForms(userDetails.getUserid());
        Map<String, String> map = new HashMap<>();
        map.put("taskNum", tasks.size() + "");
        map.put("informNum", informs.size() + "");
        return map;
    }

    @RequestMapping("/getOutWareMsg")
    @ResponseBody
    public List getOutWareMsg() {
        return electronBorrowService.getOutWareMsg();
    }


    /**
     * 获取归档排序用户
     *
     * @return
     */
    @RequestMapping("/getFillSortUser")
    @ResponseBody
    public ExtMsg getFillSortUser() {
        List<Tb_user> users = userService.getFillSortUser();
        return new ExtMsg(true, "成功", GainField.getFieldValues(users, "userid"));
    }

    /**
     * 设置归档排序用户
     *
     * @return
     */
    @RequestMapping("/setFillSortUser")
    @ResponseBody
    public ExtMsg setFillSortUser(String[] userids) {
        userService.setFillSortUser(userids);
        return new ExtMsg(true, "设置归档排序用户成功！", null);
    }

    @RequestMapping("/updateSelectedUser")
    @ResponseBody
    public List<String> updateSelectedUser(String organid, String userid) {
        List<Tb_user> users = workflowService.getWorkUser(organid, null);
        List<String> userids = new ArrayList<>();
        if (userid != null && !userid.equals("")) {
            String[] ids = userid.split(",");
            for (int i = 0; i < users.size(); i++) {
                userids.add(users.get(i).getUserid());
            }
            List<String> idsInfo = new ArrayList<>(Arrays.asList(ids));
            idsInfo.retainAll(userids);
            List<String> seletedUser = userFillSortRepository.getUserids();
            seletedUser.contains(idsInfo);
            List<Tb_user> returnUserInfo = new ArrayList<>();
            for (int i = 0; i < seletedUser.size(); i++) {  //排序返回
                Tb_user user = userRepository.findByUserid(seletedUser.get(i));
                returnUserInfo.add(user);
            }
            return workflowService.getUseridCollatorList(returnUserInfo);
        }
        return null;
    }

    @RequestMapping("/getCurrentusr")
    @ResponseBody
    public ExtMsg getCurrentusr() {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = userDetails.getRealname();
        return new ExtMsg(true, null, name);
    }
}