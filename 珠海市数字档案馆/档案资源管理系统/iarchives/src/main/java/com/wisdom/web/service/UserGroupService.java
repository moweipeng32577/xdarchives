package com.wisdom.web.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wisdom.secondaryDataSource.entity.*;
import com.wisdom.secondaryDataSource.entity.Tb_role_data_node_sx;
import com.wisdom.secondaryDataSource.entity.Tb_role_function_sx;
import com.wisdom.secondaryDataSource.repository.*;
import com.wisdom.util.GainField;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SlmRuntimeEasy;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2017/10/24 0024.
 */
@Service
@Transactional
public class UserGroupService {

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    FunctionRepository functionRepository;

    @Autowired
    RoleFunctionRepository roleFunctionRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    RoleDataNodeRepository roleDataNodeRepository;

    @Autowired
    RoleDataNodeSxRepository roleDataNodeSxRepository;

    @Autowired
    RoleOrganRepository roleOrganRepository;

    @Autowired
    RightOrganRepository rightOrganRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SlmRuntimeEasy slmRuntimeEasy;

    @Autowired
    RoleFunctionSxRepository roleFunctionSxRepository;

    @Autowired
    RoleEleFunctionRepository roleEleFunctionRepository;

    @Autowired
    SxRoleRepository sxroleRepository;

    @Autowired
    SxFunctionRepository sxFunctionRepository;
    @Autowired
    SxRoleFunctionRepository sxRoleFunctionRepository;
    @Autowired
    SxRoleDataNodeRepository sxRoleDataNodeRepository;
    @Autowired
    SxUserRoleRepository sxUserRoleRepository;
    @Autowired
    SxRoleRepository  sxRoleRepository;


    /*
     * 获取全部用户组
     */
    public Page getUserGroup(int page, int limit) {
        PageRequest pageRequest = new PageRequest(page - 1, limit, new Sort("sortsequence"));
        return roleRepository.findAll(pageRequest);
    }

    /*
     * 获取全部声像用户组
     */
    public Page getSxUserGroup(int page, int limit) {
        PageRequest pageRequest = new PageRequest(page - 1, limit, new Sort("sortsequence"));
        return sxroleRepository.findAll(pageRequest);
    }

    public List<Tb_role> getUserGroupString() {
        List<Tb_role> allRoles = roleRepository.findAll(new Sort("sortsequence"));
        List<String> roleList = new ArrayList<String>();
        if (allRoles.size() > 0) {
        	for (int i = 0; i < allRoles.size(); i++) {
				Tb_role role = allRoles.get(i);
				roleList.add(role.getRolename() + "-" + role.getRoleid());
			}
        }
        String[] strings = new String[roleList.size()];
        String[] arrStrings = roleList.toArray(strings);
        // Collator 类是用来执行区分语言环境的 String 比较的，这里选择使用CHINA
		Comparator comparator = Collator.getInstance(java.util.Locale.CHINA);
		// 使根据指定比较器产生的顺序对指定对象数组进行排序。
		Arrays.sort(arrStrings, comparator);
		List<Tb_role> returnList = new ArrayList<>();
		for (int i = 0; i < arrStrings.length; i++) {
            String[] info = arrStrings[i].split("-");
            if (!info[0].equals("安全保密管理员") && !info[0].equals("系统管理员") && !info[0].equals("安全审计员")) {
            	Tb_role role = new Tb_role();
            	role.setRoleid(info[1]);
            	role.setRolename(info[0]);
                returnList.add(role);
            }
        }
        return returnList;
    }

    public List<Tb_role_sx> getSxUserGroupString() {
        List<Tb_role_sx> allRoles = sxRoleRepository.findAll(new Sort("sortsequence"));
        List<String> roleList = new ArrayList<String>();
        if (allRoles.size() > 0) {
            for (int i = 0; i < allRoles.size(); i++) {
                Tb_role_sx role = allRoles.get(i);
                roleList.add(role.getRolename() + "-" + role.getRoleid());
            }
        }
        String[] strings = new String[roleList.size()];
        String[] arrStrings = roleList.toArray(strings);
        // Collator 类是用来执行区分语言环境的 String 比较的，这里选择使用CHINA
        Comparator comparator = Collator.getInstance(java.util.Locale.CHINA);
        // 使根据指定比较器产生的顺序对指定对象数组进行排序。
        Arrays.sort(arrStrings, comparator);
        List<Tb_role_sx> returnList = new ArrayList<>();
        for (int i = 0; i < arrStrings.length; i++) {
            String[] info = arrStrings[i].split("-");
            if (!info[0].equals("安全保密管理员") && !info[0].equals("系统管理员") && !info[0].equals("安全审计员")) {
                Tb_role_sx role = new Tb_role_sx();
                role.setRoleid(info[1]);
                role.setRolename(info[0]);
                returnList.add(role);
            }
        }
        return returnList;
    }

    /**
     * 通过用户id获取用户组
     *
     * @param userId
     * @return
     */
    public List<Tb_role> myUserGroup(String userId) {
    	List<Tb_role> allRoles = roleRepository.findBygroups(userId);
        List<String> roleList = new ArrayList<String>();
        if (allRoles.size() > 0) {
        	for (int i = 0; i < allRoles.size(); i++) {
				Tb_role role = allRoles.get(i);
				roleList.add(role.getRolename() + "-" + role.getRoleid());
			}
        }
        String[] strings = new String[roleList.size()];
        String[] arrStrings = roleList.toArray(strings);
        // Collator 类是用来执行区分语言环境的 String 比较的，这里选择使用CHINA
		Comparator comparator = Collator.getInstance(java.util.Locale.CHINA);
		// 使根据指定比较器产生的顺序对指定对象数组进行排序。
		Arrays.sort(arrStrings, comparator);
		List<Tb_role> returnList = new ArrayList<>();
		for (int i = 0; i < arrStrings.length; i++) {
            String[] info = arrStrings[i].split("-");
        	Tb_role role = new Tb_role();
        	role.setRoleid(info[1]);
        	role.setRolename(info[0]);
            returnList.add(role);
        }
        return returnList;
    }

    /**
     * 通过用户id获取用户组  声像
     *
     * @param userId
     * @return
     */
    public List<Tb_role_sx> mySxUserGroup(String userId) {
        List<Tb_role_sx> allRoles = sxRoleRepository.findBygroups(userId);
        List<String> roleList = new ArrayList<String>();
        if (allRoles.size() > 0) {
            for (int i = 0; i < allRoles.size(); i++) {
                Tb_role_sx role = allRoles.get(i);
                roleList.add(role.getRolename() + "-" + role.getRoleid());
            }
        }
        String[] strings = new String[roleList.size()];
        String[] arrStrings = roleList.toArray(strings);
        // Collator 类是用来执行区分语言环境的 String 比较的，这里选择使用CHINA
        Comparator comparator = Collator.getInstance(java.util.Locale.CHINA);
        // 使根据指定比较器产生的顺序对指定对象数组进行排序。
        Arrays.sort(arrStrings, comparator);
        List<Tb_role_sx> returnList = new ArrayList<>();
        for (int i = 0; i < arrStrings.length; i++) {
            String[] info = arrStrings[i].split("-");
            Tb_role_sx role = new Tb_role_sx();
            role.setRoleid(info[1]);
            role.setRolename(info[0]);
            returnList.add(role);
        }
        return returnList;
    }

    /**
     * 设置用户组
     *
     * @param groupids
     * @param userid
     * @return
     */
    public List userGroupSeting(String[] groupids, String userid) {
        String[] userArray = userid.split(",");
        userRoleRepository.deleteAllByUseridIn(userArray);//根据用户id删除已有的用户组
        if (groupids == null || groupids.length == 0) {//判断是否为清除用户组
            return new ArrayList();
        }
        List<Tb_user_role> users = new ArrayList<>();
        for (String userId : userArray) {
            for (String group : groupids) {
                Tb_user_role ur = new Tb_user_role();
                ur.setRoleid(group);
                ur.setUserid(userId);
                users.add(ur);
            }
        }
        return userRoleRepository.save(users);
    }

    /**
     * 设置用户组 声像
     *
     * @param groupids
     * @param userid
     * @return
     */
    public List userSxGroupSeting(String[] groupids, String userid) {
        String[] userArray = userid.split(",");
        sxUserRoleRepository.deleteAllByUseridIn(userArray);//根据用户id删除已有的用户组
        if (groupids == null || groupids.length == 0) {//判断是否为清除用户组
            return new ArrayList();
        }
        List<Tb_user_role_sx> users = new ArrayList<>();
        for (String userId : userArray) {
            for (String group : groupids) {
                Tb_user_role_sx ur = new Tb_user_role_sx();
                ur.setRoleid(group);
                ur.setUserid(userId);
                users.add(ur);
            }
        }
        return sxUserRoleRepository.save(users);
    }

    /**
     * 新增用户组
     *
     * @param role 用户组实例
     * @return
     */
    public Tb_role userGroupAddSubmit(Tb_role role) {
        role.setParentroleid("0");
        role.setRoletype("普通");
        role.setStatus(0);
        return roleRepository.save(role);
    }

    /**
     * 新增声像用户组
     *
     * @param role 用户组实例
     * @return
     */
    public Tb_role_sx addSxUserGroup(Tb_role role) {
        Tb_role_sx roleSx=new Tb_role_sx();
        BeanUtils.copyProperties(role,roleSx);
        return sxroleRepository.save(roleSx);
    }

    /**
     * 获取用户组对象
     *
     * @param roleid 用户组id
     * @return
     */
    public Tb_role getUserGroupByid(String roleid) {
        return roleRepository.findByRoleid(roleid);
    }

    /**
     * 获取功能权限
     *
     * @param fnid   功能权限id
     * @param roleId 用户组id
     * @return
     */
    public List<ExtTree> getAllGn(String fnid, String roleId,String xtType) {
        if ("09454816393442aba7e95f16a826353f".equals(fnid.trim()) || "8adae20e224b43bf80c362ca9b59fc73".equals(fnid.trim())) {//过滤掉 安全维护\系统设置 的子功能
            return null;
        }

        String isp = "1";
        if (!"1".equals(fnid)) {//判断是否为根节点
            Tb_right_function function;
            function = functionRepository.findByFnid(fnid);
            if (function != null) {
                isp = function.getTkey();
            }
        }
        List<Tb_right_function> list = new ArrayList<>();
        if (slmRuntimeEasy.hasPlatform()) {
            list = functionRepository.findByIspAndStatusOrderBySortsequence(isp, "1");
        } else {
            list = functionRepository.findByIspAndStatusOrderBySortsequenceFalse(isp, "1");
        }

        List<Tb_role_function> role_functions;
        role_functions = roleFunctionRepository.findByRoleid(roleId);
        List<Tb_right_function> functions = new ArrayList<>();
        if (role_functions != null) {
            String[] fnids = GainField.getFieldValues(role_functions, "fnid").length == 0 ? new String[]{""} : GainField.getFieldValues(role_functions, "fnid");
            functions = functionRepository.findByFnidIn(fnids);
        }
        String[] funids = GainField.getFieldValues(functions, "fnid").length == 0 ? new String[]{""} : GainField.getFieldValues(functions, "fnid");
        List<String> funclists = java.util.Arrays.asList(funids);
        ExtMsg result = getRoleGn(roleId);

        //获取已设置用户组的相关权限，字体灰色显示
        List<Tb_right_function> resultfunctions = (List<Tb_right_function>) result.getData();
        String[] resultfunids = GainField.getFieldValues(resultfunctions, "fnid").length == 0 ? new String[]{""}
                : GainField.getFieldValues(resultfunctions, "fnid");
        List<String> resultfunclists = java.util.Arrays.asList(resultfunids);
        List<Tb_right_function> allGnList = functionRepository.findByStatusOrderBySortsequence("1");
        return getCheckGnChildren(resultfunclists, funclists, list, allGnList);
    }

    /**
     * 获取已设置用户组相关功能权限
     * @param userId
     * @return
     */
    public ExtMsg getRoleGn(String userId){
        List<Tb_right_function> functions = new ArrayList<>();// 批量设置
        List<Tb_role> roles = roleRepository.findBygroups(userId);
        Boolean flag = false;
        if(roles!=null && roles.size() > 0){
            String[] roleids = GainField.getFieldValues(roles,"roleid").length==0?new String[]{""}:GainField.getFieldValues(roles,"roleid");
            functions = functionRepository.findByfunctions(roleids, "");
            flag = true;
        }
        return new ExtMsg(flag,"",functions);
    }

    private List<ExtTree> getCheckGnChildren(List<String> groupFunctions,List<String> userFunctions, List<Tb_right_function> functions, List<Tb_right_function> allGnList) {
        List<ExtTree> gnTrees = new ArrayList<>();
        ExtTree tree;
        for (Tb_right_function function : functions) {
            if ("09454816393442aba7e95f16a826353f".equals(function.getFnid().trim())||"8adae20e224b43bf80c362ca9b59fc73".equals(function.getFnid().trim())) {//过滤掉 安全维护\系统设置 功能
                continue;
            }
            tree = new ExtTree();
            if (!"true".equals(function.getHaschilds())) {
                tree.setLeaf(true);
            }

            if (userFunctions.contains(function.getFnid())) {
                tree.setChecked(true);
            }
            if ( groupFunctions !=  null &&  groupFunctions.size() >0 ){
                if(groupFunctions.contains(function.getFnid()) && !"true".equals(function.getHaschilds())){
                    //比较是否是用户组设置的权限，是则灰色显示
                    tree.setText("<span style = 'color:gray;editable:false'>"+function.getName()+"</span>");
                } else {
                    tree.setText(function.getName());
                }
            } else {
                tree.setText(function.getName());
            }
            tree.setFnid(function.getFnid());
            //List<Tb_user_organ_parents> childUserOrgans = findTopOrganOfPcid(useRightOrgan.getOrganid(), parents);// 判断是否有子节点
            if ("true".equals(function.getHaschilds())) {
                tree.setCls("folder");
                tree.setLeaf(false);
                String isp = "1";
                if (!"1".equals(function.getFnid())) {// 判断是否为根节点
                    isp = function.getTkey();
                }
                List<Tb_right_function> list = new ArrayList<>();
				/*if(slmRuntimeEasy.hasPlatform()){
					list = functionRepository.findByIspAndStatusOrderBySortsequence(isp, "1");
				}else{
					list = functionRepository.findByIspAndStatusOrderBySortsequenceFalse(isp, "1");
				}*/
                for(Tb_right_function subFun:allGnList){
                    if(isp.equals(subFun.getIsp())){
                        list.add(subFun);
                    }
                }

                List<ExtTree> extTrees = getCheckGnChildren(groupFunctions, userFunctions, list, allGnList);
                ExtTree[] gnTreeList = new ExtTree[extTrees.size()];
                for(int j=0;j<extTrees.size();j++){
                    gnTreeList[j]=extTrees.get(j);
                }
                tree.setChildren(gnTreeList);
            } else {
                tree.setCls("file");
                tree.setLeaf(true);
            }
            gnTrees.add(tree);
        }
        return gnTrees;
    }

    /**
     * 获取功能权限  声像
     *
     * @param fnid   功能权限id
     * @param roleId 用户组id
     * @return
     */
    public List<ExtTree> getSxAllGn(String fnid, String roleId,String xtType) {
        if ("09454816393442aba7e95f16a826353f".equals(fnid.trim()) || "8adae20e224b43bf80c362ca9b59fc73".equals(fnid.trim())) {//过滤掉 安全维护\系统设置 的子功能
            return null;
        }

        String isp = "1";
        if (!"1".equals(fnid)) {//判断是否为根节点
            Tb_right_function_sx function;
            function = sxFunctionRepository.findByFnid(fnid);
            if (function != null) {
                isp = function.getTkey();
            }
        }
        List<Tb_right_function_sx> list = new ArrayList<>();
        if (slmRuntimeEasy.hasPlatform()) {
            list = sxFunctionRepository.findByIspAndStatusOrderBySortsequence(isp, "1");
        } else {
            list = sxFunctionRepository.findByIspAndStatusOrderBySortsequenceFalse(isp, "1");
        }

        List<com.wisdom.secondaryDataSource.entity.Tb_role_function_sx> role_functions;
        role_functions = sxRoleFunctionRepository.findByRoleid(roleId);
        List<Tb_right_function_sx> functions = new ArrayList<>();
        if (role_functions != null) {
            String[] fnids = GainField.getFieldValues(role_functions, "fnid").length == 0 ? new String[]{""} : GainField.getFieldValues(role_functions, "fnid");
            functions = sxFunctionRepository.findByFnidIn(fnids);
        }
        String[] funids = GainField.getFieldValues(functions, "fnid").length == 0 ? new String[]{""} : GainField.getFieldValues(functions, "fnid");
        List<String> funclists = java.util.Arrays.asList(funids);
        ExtMsg result = getSxRoleGn(roleId);

        //获取已设置用户组的相关权限，字体灰色显示
        List<Tb_right_function_sx> resultfunctions = (List<Tb_right_function_sx>) result.getData();
        String[] resultfunids = GainField.getFieldValues(resultfunctions, "fnid").length == 0 ? new String[]{""}
                : GainField.getFieldValues(resultfunctions, "fnid");
        List<String> resultfunclists = java.util.Arrays.asList(resultfunids);
        List<Tb_right_function_sx> allGnList = sxFunctionRepository.findByStatusOrderBySortsequence("1");
        return getCheckGnChildrenSx(resultfunclists, funclists, list, allGnList);
    }

    /**
     * 获取已设置用户组相关功能权限
     * @param userId
     * @return
     */
    public ExtMsg getSxRoleGn(String userId){
        List<Tb_right_function_sx> functions = new ArrayList<>();// 批量设置
        List<Tb_role_sx> roles = sxRoleRepository.findBygroups(userId);
        Boolean flag = false;
        if(roles!=null && roles.size() > 0){
            String[] roleids = GainField.getFieldValues(roles,"roleid").length==0?new String[]{""}:GainField.getFieldValues(roles,"roleid");
            functions = sxFunctionRepository.findByfunctions(roleids, "");
            flag = true;
        }
        return new ExtMsg(flag,"",functions);
    }

    private List<ExtTree> getCheckGnChildrenSx(List<String> resultfunclists,List<String> funclists,
                                               List<Tb_right_function_sx> list, List<Tb_right_function_sx> allGnList) {
        List<ExtTree> extTrees = new ArrayList<>();
        ExtTree tree;
        for (Tb_right_function_sx function : list) {
            if ("09454816393442aba7e95f16a826353f".equals(function.getFnid().trim())||"8adae20e224b43bf80c362ca9b59fc73".equals(function.getFnid().trim())) {//过滤掉 安全维护\系统设置 功能
                continue;
            }
            tree = new ExtTree();
            if (!"true".equals(function.getHaschilds())) {
                tree.setLeaf(true);
            }

            if (funclists.contains(function.getFnid())) {
                tree.setChecked(true);
            }

            if ( resultfunclists !=  null &&  resultfunclists.size() >0 ){
                if(resultfunclists.contains(function.getFnid()) && !"true".equals(function.getHaschilds())){
                    //比较是否是用户组设置的权限，是则灰色显示
                    tree.setText("<span style = 'color:gray;editable:false'>"+function.getName()+"</span>");
                } else {
                    tree.setText(function.getName());
                }
            } else {
                tree.setText(function.getName());
            }
            tree.setFnid(function.getFnid());
            if ("true".equals(function.getHaschilds())) {
                tree.setCls("folder");
                tree.setLeaf(false);
                String isp = "1";
                if (!"1".equals(function.getFnid())) {// 判断是否为根节点
                    isp = function.getTkey();
                }
                List<Tb_right_function_sx> listSx = new ArrayList<>();
				/*if(slmRuntimeEasy.hasPlatform()){
					list = functionRepository.findByIspAndStatusOrderBySortsequence(isp, "1");
				}else{
					list = functionRepository.findByIspAndStatusOrderBySortsequenceFalse(isp, "1");
				}*/
                for(Tb_right_function_sx subFun:allGnList){
                    if(isp.equals(subFun.getIsp())){
                        listSx.add(subFun);
                    }
                }

                List<ExtTree> extTreeSx = getCheckGnChildrenSx(resultfunclists, funclists, listSx, allGnList);
                ExtTree[] gnTreeList = new ExtTree[extTreeSx.size()];
                for(int j=0;j<extTreeSx.size();j++){
                    gnTreeList[j]=extTreeSx.get(j);
                }
                tree.setChildren(gnTreeList);
            } else {
                tree.setCls("file");
                tree.setLeaf(true);
            }
            extTrees.add(tree);
        }
        return extTrees;
    }

    /**
     * 设置功能权限
     *
     * @param fnid   功能权限id数组
     * @param roleId 用户组id
     * @return
     */
    public List UserGroupSetGnSubmit(String[] fnid, String roleId,String xtType) {
        roleFunctionRepository.deleteAllByRoleidIn(new String[]{roleId});//根据id删除已有的用户组
        if (fnid == null) {
            List<Tb_role_function> returnList = new ArrayList<>();
            returnList.add(new Tb_role_function());
            return returnList;
        }

        List<Tb_role_function> ufs = new ArrayList<>();
        for (String fid : fnid) {
            Tb_role_function tuf = new Tb_role_function();
            tuf.setFnid(fid);
            tuf.setRoleid(roleId);
            ufs.add(tuf);
        }
        return roleFunctionRepository.save(ufs);
    }

    /**
     * 设置功能权限 声像
     *
     * @param fnid   功能权限id数组
     * @param roleId 用户组id
     * @return
     */
    public List UserGroupSetSxGnSubmit(String[] fnid, String roleId,String xtType) {
        sxRoleFunctionRepository.deleteAllByRoleidIn(new String[]{roleId});//根据id删除已有的用户组
        if (fnid == null) {
            List<Tb_role_function> returnList = new ArrayList<>();
            returnList.add(new Tb_role_function());
            return returnList;
        }

        List<Tb_role_function_sx> ufs = new ArrayList<>();
        for (String fid : fnid) {
            Tb_role_function_sx tuf = new Tb_role_function_sx();
            tuf.setFnid(fid);
            tuf.setRoleid(roleId);
            ufs.add(tuf);
        }
        return sxRoleFunctionRepository.save(ufs);
    }

    /**
     * 设置数据权限
     *
     * @param dnid   数据权限id数组
     * @param roleId 用户组id
     * @return
     */
    public List UserGroupSetSjSubmit(String[] dnid, String roleId,String xtType) {
        if("声像系统".equals(xtType)){
            sxRoleDataNodeRepository.deleteAllByRoleidIn(new String[]{roleId});//根据用户组删除数据权限
            List<Tb_role_data_node_sx> ufs = new ArrayList<>();
            for (String nid : dnid) {
                Tb_role_data_node_sx tuf = new Tb_role_data_node_sx();
                tuf.setNodeid(nid);
                tuf.setRoleid(roleId);
                ufs.add(tuf);
            }
            return sxRoleDataNodeRepository.save(ufs);
        }else{
            roleDataNodeRepository.deleteAllByRoleidIn(new String[]{roleId});//根据用户组删除数据权限
            List<Tb_role_data_node> ufs = new ArrayList<>();
            for (String nid : dnid) {
                Tb_role_data_node tuf = new Tb_role_data_node();
                tuf.setNodeid(nid);
                tuf.setRoleid(roleId);
                ufs.add(tuf);
            }
            return roleDataNodeRepository.save(ufs);
        }
    }

    /**
     * 设置机构权限
     *
     * @param organid 机构权限id数组
     * @param roleId  用户组id
     * @return
     */
    public List<Tb_role_organ> userGroupSetOrganSubmit(String[] organid, String roleId) {
        roleOrganRepository.deleteAllByRoleidIn(new String[]{roleId});//根据用户组删除权限
        List<Tb_right_organ> rightOrganList = rightOrganRepository.getParentOrgans(organid);
        String[] pnid = GainField.getFieldValues(rightOrganList, "organid");

        String[] nids = ArrayUtils.addAll(organid, pnid);
        List<Tb_role_organ> ro = new ArrayList<>();
        for (String nid : nids) {
            Tb_role_organ role_organ = new Tb_role_organ();
            role_organ.setOrganid(nid);
            role_organ.setRoleid(roleId);
            ro.add(role_organ);
        }
        return roleOrganRepository.save(ro);
    }

    /**
     * 删除用户组
     *
     * @param groupIds 用户组id数组
     */
    public int userGroupDel(String[] groupIds) {
        userRoleRepository.deleteAllByRoleidIn(groupIds);//删除用户用户组关联数据
        roleFunctionRepository.deleteAllByRoleidIn(groupIds);//删除用户组功能表关联数据
        roleDataNodeRepository.deleteAllByRoleidIn(groupIds);//删除用户组数据权限表关联数据
        return roleRepository.deleteAllByRoleidIn(groupIds);//删除用户组
    }

    /**
     * 删除声像用户组
     *
     * @param groupIds 用户组id数组
     */
    public int userGroupSxDel(String[] groupIds) {
        sxUserRoleRepository.deleteAllByRoleidIn(groupIds);//删除用户用户组关联数据
        sxRoleFunctionRepository.deleteAllByRoleidIn(groupIds);//删除用户组功能表关联数据
        sxRoleDataNodeRepository.deleteAllByRoleidIn(groupIds);//删除用户组数据权限表关联数据
        return sxroleRepository.deleteAllByRoleidIn(groupIds);//删除用户组
    }

    public void modifyUsergroupOrder(Tb_role userGroup, int target) {
        if (userGroup.getSortsequence() == null || userGroup.getSortsequence() < target) {
            //后移。1.将目标位置包括后面的所有数据后移一个位置；
            roleRepository.modifyUsergroupOrder(target, Integer.MAX_VALUE);
        } else {
            //前移。1.将目标位置及以后，当前数据以前的数据后移一个位置；
            roleRepository.modifyUsergroupOrder(target, userGroup.getSortsequence());
        }
        //2.将当前数据移到目标位置
        userGroup.setSortsequence(target);
        roleRepository.save(userGroup);
    }

    /**
     * 文件权限授权
     * @param usergroupid
     * @return
     */
    public List<ExtTree> getWjList(String usergroupid) {
        List<Tb_role_ele_function> eleFunction = roleEleFunctionRepository.findByUsergroupid(usergroupid);
        if(eleFunction==null||eleFunction.size() == 0){
            Tb_role_ele_function ele1= new Tb_role_ele_function(usergroupid,"管理平台");
            Tb_role_ele_function ele2= new Tb_role_ele_function(usergroupid,"利用平台");
            roleEleFunctionRepository.save(ele1);
            roleEleFunctionRepository.save(ele2);
            return getEleTree(usergroupid);
        }
        else{
            return getEleTree(usergroupid);
        }
    }

    public List<ExtTree> getEleTree(String usergroupid){
        List<Tb_role_ele_function> eleFunction = roleEleFunctionRepository.findByUsergroupid(usergroupid);
        ExtTree[] trees = new ExtTree[eleFunction.size()];
        for (int i =0;i<eleFunction.size();i++) {
            ExtTree tree = new ExtTree();
            tree.setCls("folder");
            tree.setRoottype("root");
            tree.setText(eleFunction.get(i).getPlatform());
            tree.setExpanded(true);

            ExtTree[] childTree;
            if(eleFunction.get(i).getPlatform().equals("管理平台")){
                childTree=new ExtTree[6];
            }
            else{
                childTree=new ExtTree[4];
            }

            for(int j = 0;j<childTree.length;j++){
                ExtTree ctree = new ExtTree();
                switch (j){
                    case 0:
                        ctree.setText("下载");
                        ctree.setLeaf(true);
                        ctree.setFnid(i==0?"管理平台":"利用平台");
                        ctree.setChecked(eleFunction.get(i).getDownload().equals("1"));
                        childTree[j] =ctree;
                        break;
                    case 1:
                        ctree.setText("全部下载");
                        ctree.setLeaf(true);
                        ctree.setFnid(i==0?"管理平台":"利用平台");
                        ctree.setChecked(eleFunction.get(i).getDownloadAll().equals("1"));
                        childTree[j] =ctree;
                        break;
                    case 2:
                        ctree.setText("打印");
                        ctree.setLeaf(true);
                        ctree.setFnid(i==0?"管理平台":"利用平台");
                        ctree.setChecked(eleFunction.get(i).getPrint().equals("1"));
                        childTree[j] =ctree;
                        break;
                    case 3:
                        ctree.setText("批量打印");
                        ctree.setLeaf(true);
                        ctree.setFnid(i==0?"管理平台":"利用平台");
                        ctree.setChecked(eleFunction.get(i).getPrintBatch().equals("1"));
                        childTree[j] =ctree;
                        break;
                    case 4:
                        if(eleFunction.get(i).getPlatform().equals("管理平台")) {
                            ctree.setText("删除");
                            ctree.setLeaf(true);
                            ctree.setFnid("管理平台");
                            ctree.setChecked(eleFunction.get(i).getDel().equals("1"));
                        }
                        childTree[j] = ctree;
                        break;
                    case 5:
                        if(eleFunction.get(i).getPlatform().equals("管理平台")) {
                            ctree.setText("管理按钮");
                            ctree.setExpanded(true);
                            ctree.setLeaf(false);
                            ExtTree[] childcTree = new ExtTree[4];
                            boolean flag = false;
                            for (int k = 0; k < childcTree.length; k++) {
                                ExtTree cctree = new ExtTree();
                                switch (k) {
                                    case 0:
                                        cctree.setText("上传");
                                        cctree.setFnid("管理平台");
                                        cctree.setLeaf(true);
                                        if(eleFunction.get(i).getUpload().equals("1")){
                                            cctree.setChecked(true);
                                            flag=true;
                                        }
                                        else{
                                            cctree.setChecked(false);
                                        }
                                        childcTree[k] = cctree;
                                        break;
                                    case 1:
                                        cctree.setText("上移");
                                        cctree.setFnid("管理平台");
                                        cctree.setLeaf(true);
                                        if(eleFunction.get(i).getUp().equals("1")){
                                            cctree.setChecked(true);
                                            flag=true;
                                        }
                                        else{
                                            cctree.setChecked(false);
                                        }
                                        childcTree[k] = cctree;
                                        break;
                                    case 2:
                                        cctree.setText("下移");
                                        cctree.setFnid("管理平台");
                                        cctree.setLeaf(true);
                                        if(eleFunction.get(i).getDown().equals("1")){
                                            cctree.setChecked(true);
                                            flag=true;
                                        }
                                        else{
                                            cctree.setChecked(false);
                                        }
                                        childcTree[k] = cctree;
                                        break;
                                    case 3:
                                        cctree.setText("查看历史版本");
                                        cctree.setFnid("管理平台");
                                        cctree.setLeaf(true);
                                        if(eleFunction.get(i).getLookhistory().equals("1")){
                                            cctree.setChecked(true);
                                            flag=true;
                                        }
                                        else{
                                            cctree.setChecked(false);
                                        }
                                        childcTree[k] = cctree;
                                        break;
                                }
                            }

                            ctree.setChecked(flag);
                            ctree.setChildren(childcTree);
                        }
                        childTree[j] = ctree;
                        break;
                    default:
                        break;
                }
            }
            if(childTree[0].isChecked() && childTree[1].isChecked() && childTree[2].isChecked()){
                tree.setChecked(true);
            }
            tree.setChildren(childTree);
            trees[i] = tree;
        }
        return Arrays.asList(trees);
    }

    /**
     * 设置文件权限
     * @param usergroupid
     * @param lylist 利用平台
     * @param gllist 管理平台
     * @return
     */
    public void setWJQXbtn(String[] lylist,String[] gllist,String usergroupid) {
        List<Tb_role_ele_function> eleFunction = roleEleFunctionRepository.findByUsergroupid(usergroupid);
        for (Tb_role_ele_function ele : eleFunction) {
            if (ele.getPlatform().equals("利用平台")) {
                ele.setDownload(ArrayUtils.contains(lylist,"下载")?"1":"0");
                ele.setDownloadAll(ArrayUtils.contains(lylist,"全部下载")?"1":"0");
                ele.setPrint(ArrayUtils.contains(lylist,"打印")?"1":"0");
                ele.setPrintBatch(ArrayUtils.contains(lylist,"批量打印")?"1":"0");
            }
            else if (ele.getPlatform().equals("管理平台")) {
                ele.setDownload(ArrayUtils.contains(gllist,"下载")?"1":"0");
                ele.setDownloadAll(ArrayUtils.contains(gllist,"全部下载")?"1":"0");
                ele.setPrint(ArrayUtils.contains(gllist,"打印")?"1":"0");
                ele.setPrintBatch(ArrayUtils.contains(gllist,"批量打印")?"1":"0");
                ele.setUpload(ArrayUtils.contains(gllist,"上传")?"1":"0");
                ele.setUp(ArrayUtils.contains(gllist,"上移")?"1":"0");
                ele.setDown(ArrayUtils.contains(gllist,"下移")?"1":"0");
                ele.setDel(ArrayUtils.contains(gllist,"删除")?"1":"0");
                ele.setLookhistory(ArrayUtils.contains(gllist,"查看历史版本")?"1":"0");
            }
        }
    }

    public void getUsersOnUserGroup(String roleid,int page,int limit,HttpServletResponse httpServletResponse){
        //按创建时间排序
        PageRequest pageRequest = new PageRequest(page-1,limit,new Sort(Sort.Direction.ASC,"createtime"));
        Page<Tb_user> list = userRepository.findUsersByRoleid(roleid,pageRequest);

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

    public List<Tb_user> getAllUsers(String organid){
        if(organid!=null&&!"".equals(organid)){
            return userRepository.findByOrganid(organid);
        }else{
            return userRepository.findAll();
        }
    }

    public List<Tb_user_role> addUsers(String roleid,String[] userids){
        //删除原来组内用户
        userRoleRepository.deleteByRoleid(roleid);
        List<Tb_user_role> userRoles = new ArrayList<>();
        for(String userid : userids){
            Tb_user_role  user_role = new Tb_user_role();
            user_role.setRoleid(roleid);
            user_role.setUserid(userid);
            userRoles.add(user_role);
        }
        return userRoleRepository.save(userRoles);
    }

    public int delUserOnGroup(String roleid,String[] userids){
        return userRoleRepository.deleteByRoleidAndUseridIn(roleid,userids);
    }
}