package com.wisdom.web.service;

import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 流水线管理服务层
 */
@Service
@Transactional
public class AssemblyAdminService {

    @Autowired
    SzhAssemblyRepository szhAssemblyRepository;

    @Autowired
    SzhAssemblyNodeRepository szhAssemblyNodeRepository;

    @Autowired
    SzhAssemblyFlowsRepository szhAssemblyFlowsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SzhAssemblyUserRepository szhAssemblyUserRepository;

    @Autowired
    SzhAdminUserRepository szhAdminUserRepository;

    @Autowired
    UserService userService;

    @Autowired
    OrganService organService;

    @Autowired
    SzhAssemblyPreflowRepository szhAssemblyPreflowRepository;

    @Autowired
    SzhArchivesCalloutRepository szhArchivesCalloutRepository;

    @Autowired
    SzhCalloutEntryRepository szhCalloutEntryRepository;

    @Autowired
    ArchivesCalloutService archivesCalloutService;

    public Page<Szh_assembly> getAssemblyBySearch(int page, int limit, String condition, String operator, String content){
        Specifications sp = null;
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        return szhAssemblyRepository.findAll(sp, new PageRequest(page - 1, limit));
    }

    public Page<Szh_assembly> getAssemblyBySearch_1(int page, int limit, String sort,String condition, String operator, String content){
        Sort sortobj=WebSort.getSortByJson(sort);
        PageRequest pageRequest=new PageRequest(page-1,limit,sortobj);
        Specifications sp = null;
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        return szhAssemblyRepository.findAll(sp,pageRequest);
    }

    public List<Szh_assembly_flows> getLinkByid(String id, String type) {
        List<Szh_assembly_node> assemblyNodes =  szhAssemblyNodeRepository.findByAssemblyidOrderBySorting(id);
        List<Szh_assembly_flows> allFlows = szhAssemblyFlowsRepository.getAllFlows();
        if (type != null && "preflow".equals(type)) {
//            return szhAssemblyFlowsRepository.getFlowsByassemblyidnew(id);
            return getSortFlows(assemblyNodes,allFlows,true);
        }else {
//            return szhAssemblyFlowsRepository.getFlowsByassemblyid(id);
            return getSortFlows(assemblyNodes,allFlows,false);
        }
    }

    public List<Szh_assembly_flows> getSortFlows(List<Szh_assembly_node> assemblyNodes,List<Szh_assembly_flows> allFlows,boolean hasFinish){
        Map<String,Szh_assembly_flows> idAfMap = new HashMap<>();//流水线id与实例对于map,用于改善性能
        List<Szh_assembly_flows> afs = new ArrayList<>();
        for(Szh_assembly_flows af:allFlows){
            idAfMap.put(af.getId(),af);
        }

        for(Szh_assembly_node an:assemblyNodes){
            if("完成环节".equals(idAfMap.get(an.getNodeid()).getNodename())){
                if(!hasFinish){
                    continue;
                }
            }
            afs.add(idAfMap.get(an.getNodeid()));
        }
        return afs;
    }

    public List<Szh_assembly_node> setLinkByid(String assemblyid,String[] ids){
        szhAssemblyNodeRepository.deleteByAssemblyid(assemblyid);   //删之前的环节设置
        szhAssemblyPreflowRepository.deleteByAssemblyid(assemblyid);  //删除之前的前置环节
        List<Szh_assembly_node> assembly_nodes = new ArrayList<>();
        for(int i=0;i<ids.length;i++){
            Szh_assembly_node assembly_node =new Szh_assembly_node();
            assembly_node.setAssemblyid(assemblyid);
            assembly_node.setNodeid(ids[i]);
            assembly_node.setSorting(i+1);
            assembly_nodes.add(assembly_node);
        }
        return szhAssemblyNodeRepository.save(assembly_nodes);
    }

    public List<Szh_assembly_flows> getLinkAll(){
        return szhAssemblyFlowsRepository.getAllFlows();
    }

    public List<Tb_user> getAssemblyUserByid(String id, String assemblyflowid){
        return userRepository.getUserByAssemblyid(id,assemblyflowid);
    }

    public List<Szh_assembly_user> setAssemblyUser(String assemblyid, String[] ids,String assemblyflowid){
        szhAssemblyUserRepository.deleteByAssemblyidAndAssemblyflowid(assemblyid,assemblyflowid);   //删之前的流水线用户
        List<Szh_assembly_user> assembly_users = new ArrayList<>();
        for(int i=0;i<ids.length;i++){
            Szh_assembly_user assembly_user =new Szh_assembly_user();
            assembly_user.setAssemblyid(assemblyid);
            assembly_user.setAssemblyflowid(assemblyflowid);
            assembly_user.setUserid(ids[i]);
            assembly_users.add(assembly_user);
        }
        return szhAssemblyUserRepository.save(assembly_users);
    }

    public Page<Tb_user> getAssemblyAdminUser(int page, int limit, String condition, String operator, String content,String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort(Sort.Direction
                .ASC, "loginname") : sortobj);
        List<Szh_admin_user> adminusers = szhAdminUserRepository.findAll();
        Specifications sp = null;
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        if(adminusers.size()>0) {
            Specification<Tb_user> getCondition = new Specification<Tb_user>() {
                @Override
                public Predicate toPredicate(Root<Tb_user> root, CriteriaQuery<?> criteriaQuery,
                                             CriteriaBuilder criteriaBuilder) {

                    CriteriaBuilder.In in = criteriaBuilder.in(root.get("userid"));
                    for (Szh_admin_user user : adminusers) {
                        in.value(user.getUserid());
                    }
                    return criteriaBuilder.or(in);
                }
            };
            sp = sp.where(getCondition);
            return userRepository.findAll(sp, pageRequest);
        }else{
            return null;
        }
    }

    public List<Szh_admin_user> getAdminUser(){
        return szhAdminUserRepository.findAll();
    }

    public List<Szh_admin_user> setAdminUser(String[] ids){
        szhAdminUserRepository.deleteAll();  //删之前的流水线管理员
        List<Szh_admin_user> adminusers = new ArrayList<>();
        for(int i=0;i<ids.length;i++){
            Szh_admin_user aduser =new Szh_admin_user();
            aduser.setUserid(ids[i]);
            adminusers.add(aduser);
        }
        return szhAdminUserRepository.save(adminusers);
    }

    public int delAdminUser(String[] ids){
        return szhAdminUserRepository.deleteByUseridIn(ids);
    }

    public List<Tb_user> getAssemblyUser(String id,String assemblyflowid){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 系统绑定对象(全局)
        return userRepository.getUserByAssemblyidAndUserid(id,assemblyflowid,userDetails.getUserid());
    }

    public List<Tb_user> getAssemblyFlowUser(String assemblyid,String organid){
        List<Tb_user> userList = new ArrayList<>();
        if(organid == null || "0".equals(organid)){
            userList = userRepository.getUserByAssemblyid(assemblyid);
        }else{
            List<String> organidList = organService.getOrganidLoop(organid, true, new ArrayList<String>());
            organidList.add(organid);
            String[] organs = new String[organidList.size()];
            Specification<Tb_user> specification = userService.getUsers(organidList.toArray(organs));
            List<String> userids = szhAssemblyUserRepository.getByUseridByAssemblyid(assemblyid);
            Specification<Tb_user> specificationAssemblyid = getUsersByAssemblyid(userids);
            userList = userRepository.findAll(Specifications.where(specification).and(specificationAssemblyid));
        }
        List<String> users = new ArrayList<String>();
        if (userList.size() > 0) {
            for (int i = 0; i < userList.size(); i++) {
                Tb_user user = userList.get(i);
                users.add(user.getRealname() + "-" + user.getUserid());
            }
        }
        String[] strings = new String[users.size()];
        String[] arrStrings = users.toArray(strings);
        // Collator 类是用来执行区分语言环境的 String 比较的，这里选择使用CHINA
        Comparator comparator = Collator.getInstance(java.util.Locale.CHINA);
        // 使根据指定比较器产生的顺序对指定对象数组进行排序。
        Arrays.sort(arrStrings, comparator);
        List<Tb_user> returnList = new ArrayList<>();
        for (int i = 0; i < arrStrings.length; i++) {
            String[] info = arrStrings[i].split("-");
            if (!info[0].equals("安全保密管理员") && !info[0].equals("系统管理员") && !info[0].equals("安全审计员")) {
                Tb_user userinfo = new Tb_user();
                userinfo.setUserid(info[1]);
                userinfo.setRealname(info[0]);
                returnList.add(userinfo);
            }
        }
        return userList;
    }

    public static Specification<Tb_user> getUsersByAssemblyid(List<String> userids) {
        Specification<Tb_user> specification = new Specification<Tb_user>() {
            @Override
            public Predicate toPredicate(Root<Tb_user> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                CriteriaBuilder.In in = criteriaBuilder.in(root.get("userid"));
                for (String userid : userids) {
                    in.value(userid);
                }
                return criteriaBuilder.or(in);
            }
        };
        return specification;
    }

    public List<Szh_assembly_flows> getAssemblyPreflowByid(String id,String assemblyflowid){
        return szhAssemblyFlowsRepository.findFlowsByAssemblyidAndFlowid(id,assemblyflowid);
    }

    public List<Szh_assembly_flows> getAssemblyPreflow(String id,String assemblyflowid){
        List<Szh_assembly_node> nodeflows = szhAssemblyNodeRepository.findByAssemblyid(id);
        Szh_assembly_node nodeflow = szhAssemblyNodeRepository.findByAssemblyidAndNodeid(id,assemblyflowid);
        List<String> flowids = new ArrayList<>();
        for(int i=0;i<nodeflows.size();i++){
            if(nodeflows.get(i).getSorting()<nodeflow.getSorting()){
                flowids.add(nodeflows.get(i).getNodeid());
            }
        }
        String[] ids = new String[flowids.size()];
        flowids.toArray(ids);
        List<Szh_assembly_flows> returnflows = szhAssemblyFlowsRepository.findByIdIn(ids);
        return returnflows;
    }

    public List<Szh_assembly_preflow> setPreLink(String assemblyid,String[] preflowids,String assemblyflowid){
        szhAssemblyPreflowRepository.deleteByAssemblyidAndAssemblyflowid(assemblyid,assemblyflowid);   //删除此环节之前设置的前置环节
        List<Szh_assembly_preflow> assembly_preflows = new ArrayList<>();
        for(int i=0;i<preflowids.length;i++){
            Szh_assembly_preflow assembly_preflow =new Szh_assembly_preflow();
            assembly_preflow.setAssemblyid(assemblyid);
            assembly_preflow.setAssemblyflowid(assemblyflowid);
            assembly_preflow.setPreflowid(preflowids[i]);
            assembly_preflows.add(assembly_preflow);
        }
        return szhAssemblyPreflowRepository.save(assembly_preflows);
    }

    public boolean setAssembly(String title,String remark,String subtype,String code){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 系统绑定对象(全局)
        List<Szh_assembly> assemblies = szhAssemblyRepository.findByTitle(title);
        switch (assemblies.size()){
            case 0:
                if("edit".equals(subtype)){
                    Szh_assembly assemblyEdit = szhAssemblyRepository.findByCode(code);
                    assemblyEdit.setTitle(title);
                    assemblyEdit.setRemark(remark);
                }else {
                    String ordernum = "";
                    String code8 = new SimpleDateFormat("yyyyMMdd").format(new Date());//获取八位时间
                    String num = szhAssemblyRepository.getOrderNum(code8);
                    if (num == null) {
                        ordernum = "0001";
                    } else {
                        ordernum = String.format("%04d", Integer.parseInt(num) + 1);//字符串格式化
                    }
                    Szh_assembly assembly = new Szh_assembly();
                    assembly.setCode(code8 + ordernum);
                    assembly.setTitle(title);
                    assembly.setRemark(remark);
                    assembly.setCreatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    assembly.setCreator(userDetails.getRealname());
                    szhAssemblyRepository.save(assembly);
                }
                return true;
            case 1:
                Szh_assembly assemblyEdit = szhAssemblyRepository.findByCode(code);
                if("edit".equals(subtype)&&assemblyEdit.getId().equals(assemblies.get(0).getId())) {
                    assemblyEdit.setTitle(title);
                    assemblyEdit.setRemark(remark);
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    public Szh_assembly getAssembly(String code){
        return szhAssemblyRepository.findByCode(code);
    }

    public boolean delAssembly(String[] codes){
        boolean flag = false;
        try {
            szhAssemblyRepository.deleteByCodeIn(codes);
            String[] batchcodes = szhArchivesCalloutRepository.getBatchcodes(codes);
            if (batchcodes != null && batchcodes.length > 0) {
                String[] entryids = szhCalloutEntryRepository.findIdByBatchcodes(batchcodes);
                szhArchivesCalloutRepository.deleteByBatchcodeIn(batchcodes);
                if (entryids != null && entryids.length > 0) {
                    archivesCalloutService.entryDel(entryids);
                }
            }
            flag = true;
        }catch (Exception e){
            e.getStackTrace();
        }
        return flag;
    }
}
