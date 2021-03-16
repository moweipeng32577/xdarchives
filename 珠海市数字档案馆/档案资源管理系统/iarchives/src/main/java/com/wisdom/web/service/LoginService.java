package com.wisdom.web.service;

import com.wisdom.util.FileUtil;
import com.wisdom.util.GainField;
import com.wisdom.util.MD5;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2017/8/18.
 */
@Service
@Transactional
public class LoginService {


    @Autowired
    GroupRepository groupRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    FunctionRepository functionRepository;

    @Autowired
    ResourcesRepository resourcesRepository;

    @Autowired
    PersonalizedRepository personalizedRepository;

    @Autowired
    RightOrganRepository rightOrganRepository;

    @Autowired
    FundsRepository fundsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleFunctionRepository roleFunctionRepository;

    @Autowired
    UserFunctionRepository userFunctionRepository;

    @Autowired
    TransdocRepository transdocRepository;

    @Autowired
    AuditService auditService;

    @Autowired
    EntryIndexCaptureRepository entryIndexCaptureRepository;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Value("${system.audit.opened}")
    private String auditOpened;// 是否打开数据审核

    public Tb_Personalized index(SecurityUser user1){

        //组
        List<Tb_group> groups = groupRepository.findBygroups(user1.getUserid());
        user1.setGroups(groups);

        List<Tb_role> roles = null;
        //角色
        if(groups!=null){
            String[] groupids = GainField.getFieldValues(groups,"groupid").length==0?new String[]{""}:GainField.getFieldValues(groups,"groupid");
            if(groupids.length > 0){
                roles = roleRepository.findByroles(groupids,user1.getUserid());
            }else{
                roles = roleRepository.findBygroups(user1.getUserid());
            }

            user1.setRoles(roles);
        }

        //功能
        if(roles!=null){
            String[] roleids = GainField.getFieldValues(roles,"roleid").length==0?new String[]{""}:GainField.getFieldValues(roles,"roleid");
            List<Tb_right_function> functions = functionRepository.findByfunctions(roleids,user1.getUserid());
            user1.setFunctions(functions);
        }


        //资源
        if(user1.getFunctions()!=null){
            List<String> urls = new ArrayList<>();
            for(Tb_right_function fn:user1.getFunctions()){
                if(fn.getUrl()!=null){
                    urls.add(fn.getUrl().split("\\?")[0]);
                }
            }
            user1.setResources(urls);
        }

        Tb_Personalized personalizeds = personalizedRepository.findByUserid(user1.getUserid());
        user1.setPersonalized(personalizeds);
        return personalizeds;
    }

    public boolean initOrgan(){
        Tb_right_organ organ = rightOrganRepository.findByOrgannameAndIsinit("全宗单位","0");
        return organ==null?true:false;
    }

    public boolean initFunds(){
        Tb_funds funds = fundsRepository.findByFundsnameAndIsinit("全宗单位","0");
        return funds==null?true:false;
    }

    public boolean initUserPwd(String username){
        Tb_user user = userRepository.findByLoginname(username);
        return MD5.MD5("555").equals(user.getPassword())?false:true;
    }

    public int updataFunctionStatus(String status,String functionname){
        int count=0;
        if(status!=null&&functionname!=null){
            count=functionRepository.updataStatusByFunctionname(status,functionname);
        }
        return count;
    }

    public int updataFunctionStatus(String status,String[] functionname){
        int count=0;
        if(status!=null&&functionname!=null){
            count=functionRepository.updateStatusByFunctionnames(status,functionname);
        }
        return count;
    }

    public int updataFunctionStatusWithChilds(String status,String functionname){
        int count=0;
        if(status!=null&&functionname!=null){
            count=functionRepository.updateStatusByFunctionnameWithChilds(status,functionname);
        }
        return count;
    }

    /**
     * 控制审核模块是否要显示
     */
    public void controlAudit(){
        if("true".equals(auditOpened)){
            updataFunctionStatus("1","数据审核");
        }else{
            updataFunctionStatus("0","数据审核");
            //删除用户权限记录
            userFunctionRepository.deleteByFnid("数据审核");
            //删除用户组权限记录
            roleFunctionRepository.deleteByFnid("数据审核");
            //要将已有（移交状态=已移交）的移交单据，都进行退回操作
            List<Tb_transdoc> transdocs = transdocRepository.findByState(Tb_transdoc.STATE_TRANSFOR);//获取已移交的移交单据
            for(Tb_transdoc tb_transdoc:transdocs){
                auditService.sendback(tb_transdoc.getDocid(),"切换到无数据审批模式，未审核的移交数据自动退回采集中可重新进行移交",null);
            }
        }
    }

    /**
     *  判断静态文件是否存在
     * @param filepath 静态文件路径
     * @return
     */
    public boolean isPathExist(String filepath){
        boolean b = false;
        try {
            Resource resource = new ClassPathResource(filepath);
            if(resource.exists()){
                return b=true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return b;
    }

    public ExtMsg isLDPermis(Tb_user user, String logintype){
        // 该用户所属机构根节点
        String organid = userRepository.findOrganidByUserid(user.getUserid());
        Tb_right_organ organ = rightOrganRepository.findOne(organid);
        while (organ.getParentid() !=null && !"0".equals(organ.getParentid())) {
            organ = rightOrganRepository.findOne(organ.getParentid());// 找到根节点
        }
        //判断用户所属单位-最上层的单位id，是否是等于4028e681636168e601636180baac0002，是-那就是馆用户；不是-就是立档用户。
        String organid1 = "4028e681636168e601636180baac0002";
        if(organid1.equals(organ.getOrganid().trim()) && "11".equals(logintype)){
            //馆用户选择了立档归档系统
            return new ExtMsg(true,"用户无该系统权限，请登录档案资源管理系统！",null);
        }else if(!organid1.equals(organ.getOrganid().trim()) && "1".equals(logintype)){
            //立档用户选择了档案资源管理系统
            return new ExtMsg(true,"用户无该系统权限，请登录立档单位在线归档系统！",null);
        }
        return new ExtMsg(false,"",null);
    }

    public String getLDPermis(Tb_user user){
        // 该用户所属机构根节点
        Tb_right_organ organ = rightOrganRepository.findOne(user.getOrganid());
        while (organ.getParentid() !=null && !"0".equals(organ.getParentid())) {
            organ = rightOrganRepository.findOne(organ.getParentid());// 找到根节点
        }
        //判断用户所属单位-最上层的单位id，是否是等于4028e681636168e601636180baac0002，是-那就是馆用户；不是-就是立档用户。
        String organid1 = "4028e681636168e601636180baac0002";
        if(!organid1.equals(organ.getOrganid().trim())){//立档用户
            return "11";
        }
        return "1";
    }

    public boolean isPermiss(Tb_user user, String logintype) {
        boolean flag=false;
        String[] isp = new String[]{"k57"};
        List<String> ufSourceList = userFunctionRepository.findByUserid(user.getUserid());
        List<String> roleFuids = roleFunctionRepository.findFnids(user.getUserid());
        ufSourceList.removeAll(roleFuids);
        ufSourceList.addAll(roleFuids);
        String realname=user.getRealname();
        List<String> fnids=new ArrayList<String>();
        switch (logintype){
            case "1":
                //用户没有管理平台权限
                isp = new String[]{"daxt"};//档案资源管理系统
                fnids=functionRepository.findFnidsByTkeyIn(isp);
                if(!ufSourceList.containsAll(fnids)) {
                    flag=true;
                }
                break;
            case "2":
                //用户没有声像系统权限
                if("0".equals(user.getUsertype())){
                    flag=true;
                }
                break;
            case "3":
                isp = new String[]{"k180"};//数字化系统
                fnids=functionRepository.findFnidsByTkeyIn(isp);
                if(!ufSourceList.containsAll(fnids)) {
                    flag=true;
                }
                break;
            case "4":
                flag=true;
                List<String> roleids=userRoleRepository.findByUserid(user.getUserid());
                if(roleids.size()>0){
                    String syRoleids="402880e962d238700162d23abfcb0003,402880e962d238700162d23ed30c0007,402880e962d238700162d23ef69a0008";//三员角色id
                    for(String roleid:roleids){
                       if(syRoleids.contains(roleid.trim())){//有三员角色
                           flag=false;
                           break;
                       }
                    }
                }
                break;
            case "5":
                isp = new String[]{"k203"};//目录中心
                fnids=functionRepository.findFnidsByTkeyIn(isp);
                if(!ufSourceList.containsAll(fnids)) {
                    flag=true;
                }
                break;
            case "6":
                isp = new String[]{"k202"};//新闻影像采集系统
                fnids=functionRepository.findFnidsByTkeyIn(isp);
                if(!ufSourceList.containsAll(fnids)) {
                    flag=true;
                }
                break;
            case "7":
                isp = new String[]{"k53"};//编研管理系统
                fnids=functionRepository.findFnidsByTkeyIn(isp);
                if(!ufSourceList.containsAll(fnids)) {
                    flag=true;
                }
                break;
            case "8":
                isp = new String[]{"k170"};//库房系统
                fnids=functionRepository.findFnidsByTkeyIn(isp);
                if(!ufSourceList.containsAll(fnids)) {
                    flag=true;
                }
                break;
            case "14":
                isp = new String[]{"k211"};//业务指导系统
                fnids=functionRepository.findFnidsByTkeyIn(isp);
                if(!ufSourceList.containsAll(fnids)) {
                    flag=true;
                }
                break;
            case "10":
                isp = new String[]{"k213"};//综合事务管理系统
                fnids=functionRepository.findFnidsByTkeyIn(isp);
                if(!ufSourceList.containsAll(fnids)) {
                    flag=true;
                }
                break;
            case "11":
                isp = new String[]{"k216"};//电子文档系统
                fnids=functionRepository.findFnidsByTkeyIn(isp);
                if(!ufSourceList.containsAll(fnids)) {
                    flag=true;
                }
                break;
            case "12":
                isp = new String[]{"ysjxt"};//电子档案元数据登记
                fnids=functionRepository.findFnidsByTkeyIn(isp);
                if(!ufSourceList.containsAll(fnids)) {
                    flag=true;
                }
                break;
            case "13":
                isp = new String[]{"jhxt"};//安全离线数据交换系统
                fnids=functionRepository.findFnidsByTkeyIn(isp);
                if(!ufSourceList.containsAll(fnids)) {
                    flag=true;
                }
                break;
            default:
                flag=false;
                break;
        }
        return flag;
    }
}
