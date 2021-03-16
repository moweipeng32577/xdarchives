package com.wisdom.web.service;

import com.wisdom.util.GainField;
import com.wisdom.web.entity.Tb_right_function;
import com.wisdom.web.entity.Tb_role;
import com.wisdom.web.entity.Tb_role_function;
import com.wisdom.web.entity.Tb_user;
import com.wisdom.web.repository.FunctionRepository;
import com.wisdom.web.repository.RoleFunctionRepository;
import com.wisdom.web.repository.UserFunctionRepository;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

/**
 * 统一平台服务处理器
 * Created by wjh
 */
@Service
public class UnifyService {

    @Autowired
    FunctionRepository functionRepository;
    @Autowired
    UserService userService;
    @Autowired
    UserFunctionRepository userFunctionRepository;
    @Autowired
    RoleFunctionRepository roleFunctionRepository;

    public  List<Tb_right_function>  getList(String sysType,String realname,String loginname){
        try {
            realname= URLDecoder.decode(realname, "UTF-8");//解码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[] isp = new String[]{"k57"};
        Tb_user user=userService.findByLoginname(loginname);
        List<String> ufSourceList = userFunctionRepository.findByUserid(user.getUserid());
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 系统绑定对象(全局)
        List<Tb_role> roles = userDetails.getRoles();
        String[] roleids = GainField.getFieldValues(roles, "roleid").length == 0 ? new String[] { "" }
                : GainField.getFieldValues(roles, "roleid");
        List<Tb_role_function> roleSourceList = roleFunctionRepository.findByRoleidIn(roleids);
        String[] fnids = GainField.getFieldValues(roleSourceList, "fnid").length == 0 ? new String[] { "" }
                : GainField.getFieldValues(roleSourceList, "fnid");
        ufSourceList.removeAll(Arrays.asList(fnids));
        ufSourceList.addAll(Arrays.asList(fnids));
        switch (sysType){
            case "3":
                isp = new String[]{"k180"};//数字化系统
                break;
            case "4":
                isp = new String[]{"k39","k134"};//基础平台系统
                String[] tkey=new String[]{"k134","k138"};
                /*if(realname.equals("系统管理员")){
                    return functionRepository.findByFunctiontypeAndIspInOrderBySortsequence("desktop",isp);
                }else*/
                if(realname.equals("安全保密管理员")||loginname.contains("aqbm")){//没有机构管理权限
                    isp = new String[]{"1","k134"};
                    tkey=new String[]{"k134","k136","k137","k138"};
                    return functionRepository.findByFunctiontypeAndIspInAndTkeyInOrderBySortsequence("desktop",isp,tkey);
                }else if(realname.equals("安全审计员")||loginname.contains("aqsj")){//只有日志管理权限  tkey  k138
                    isp = new String[]{"1","k134"};
                    return functionRepository.findByFunctiontypeAndIspInAndTkeyInOrderBySortsequence("desktop",isp,tkey);
                }
                List<Tb_right_function> rolefunctions=functionRepository.findByFunctiontypeAndIspInOrTkeyInAndStatusOrderBySortsequence("desktop",isp,isp,"1");
                List<Tb_right_function> returnfunctions=functionRepository.findByFnidIn(ufSourceList.toArray(new String[ufSourceList.size()]));
                rolefunctions.retainAll(returnfunctions);//过滤没有权限的功能
                return rolefunctions;

            case "5":
                isp = new String[]{"k203"};//目录中心
                break;
            case "6":
                isp = new String[]{"k202"};//新闻影像采集系统
                break;
            case "7":
                isp = new String[]{"k53"};//编研管理系统
                break;
            case "8":
                List<String> functionCode = functionRepository.findByIsp("k170");//k170表示库房的菜单功能
                List<String> ispList = functionRepository.findByFunctioncodeIn(functionCode.toArray(new String[functionCode.size()]));
                ispList.remove("1");
                isp = ispList.toArray(new String[ispList.size()]);
                List<Tb_right_function> functions=functionRepository.findByFunctiontypeAndIspInAndStatusOrderBySortsequence("desktop",isp,"1");
                List<Tb_right_function> userfunctions=functionRepository.findByFnidIn(ufSourceList.toArray(new String[ufSourceList.size()]));
                functions.retainAll(userfunctions);//过滤没有权限的功能
                for (Tb_right_function function : functions) {
                    if(function.getIsp().equals("k170")){
                        function.setIsp("1");
                    }
                }
                return functions;
            case "14":
                isp = new String[]{"k211"};//业务指导系统
                break;
            case "10":
                isp = new String[]{"k213","k208","k210","k342"};//综合事务管理系统
//                ,"k208","k210"
                break;
            case "11":
                List<String> functionCodeK = functionRepository.findByIsp("k216");//k216表示电子文档系统的菜单功能
                List<String> ispListK = functionRepository.findByFunctioncodeIn(functionCodeK.toArray(new String[functionCodeK.size()]));
                ispListK.remove("1");
                isp = ispListK.toArray(new String[ispListK.size()]);
                List<Tb_right_function> functionsK = functionRepository.findByFunctiontypeAndIspInAndStatusOrderBySortsequence("desktop",isp,"1");
                List<Tb_right_function> userfunctionsK = functionRepository.findByFnidIn(ufSourceList.toArray(new String[ufSourceList.size()]));
                functionsK.retainAll(userfunctionsK);//过滤没有权限的功能
                for (Tb_right_function function : functionsK) {
                    if(function.getIsp().equals("k216")){
                        function.setIsp("1");
                    }
                }
                return functionsK;
            case "12":
                isp = new String[]{"ysjxt"};//元数据系统
                break;
            case "13":
                isp = new String[]{"jhxt"};//安全离线数据交换系统
                break;
            default:
                break;
        }
        List<Tb_right_function> functions=functionRepository.findByFunctiontypeAndIspInAndStatusOrderBySortsequence("desktop",isp,"1");
        List<Tb_right_function> userfunctions=functionRepository.findByFnidIn(ufSourceList.toArray(new String[ufSourceList.size()]));
        functions.retainAll(userfunctions);//过滤没有权限的功能
        return functions;
    }
}
