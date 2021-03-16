package com.wisdom.web.controller;

import com.wisdom.util.TimeScheduled;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_node;
import com.wisdom.web.entity.Tb_role;
import com.wisdom.web.entity.Tb_work;
import com.wisdom.web.repository.NodeRepository;
import com.wisdom.web.repository.RoleRepository;
import com.wisdom.web.repository.WorkRepository;
import com.wisdom.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanly on 2018/3/9 0009.
 */
@Controller
@RequestMapping(value = "/oaimport")
public class OAImportController {

    @Autowired
    TimeScheduled timeScheduled;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserService userService;

    @Autowired
    WorkRepository workRepository;

    @Autowired
    NodeRepository nodeRepository;

    @RequestMapping("/main")
    public String oaImport() {
        return "/oaimport";
    }

    @RequestMapping("/importEntries")
    @ResponseBody
    public ExtMsg execImport() {
        return new ExtMsg(true,timeScheduled.importOAEntries(),null);
    }

    @RequestMapping("/importOrgan")
    @ResponseBody
    public ExtMsg importOrgan() {
        return new ExtMsg(true,timeScheduled.importOAOrgUnit(),null);
    }

    @RequestMapping("/importUser")
    @ResponseBody
    public ExtMsg importUser() {
        return new ExtMsg(true,timeScheduled.importOAUser(),null);
    }

    @RequestMapping("/getAuthorizedRole")
    @ResponseBody
    public List<Tb_role> getAuthorizedRole() {
        List<Tb_role> all = roleRepository.findAll();
        List<Tb_role> returnlist =new ArrayList<>();
        for (Tb_role role:all){
            String name = role.getRolename();
            if(!name.equals("安全保密管理员") && !name.equals("系统管理员") && !name.equals("安全审计员")) {
                returnlist.add(role);
            }
        }
        return  returnlist;
    }

    @RequestMapping("/getNode")
    @ResponseBody
    public List<Tb_node> getNode(String workid) {
        List<Tb_node> all = nodeRepository.findByWorkidOrderBySortsequence(workid);
        List<Tb_node> returnlist =new ArrayList<>();
        for (Tb_node obj:all){
            String name = obj.getText();
            if(!name.endsWith("启动") && !name.endsWith("结束")) {
                returnlist.add(obj);
            }
        }
        return  returnlist;
    }

    @RequestMapping("/getWork")
    @ResponseBody
    public List<Tb_work> getWork() {
        return  workRepository.findAll();
    }

    @RequestMapping("/authorizeByRole")
    @ResponseBody
    public ExtMsg authorizeByRole(String roleid,boolean isIncluded) {
        userService.authorizeByRole(roleid,isIncluded);
        return new ExtMsg(true,null,null);
    }

    @RequestMapping("/authorizeByNode")
    @ResponseBody
    public ExtMsg authorizeByNode(String nodeid,boolean isIncluded) {
        userService.authorizeByNode(nodeid,isIncluded);
        return new ExtMsg(true,null,null);
    }
}
