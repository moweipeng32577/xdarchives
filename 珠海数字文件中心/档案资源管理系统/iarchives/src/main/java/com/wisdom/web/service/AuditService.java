package com.wisdom.web.service;

import com.wisdom.util.DBCompatible;
import com.wisdom.util.DateUtil;
import com.wisdom.util.GainField;
import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Rong on 2017/10/31.
 */
@Service
@Transactional
public class AuditService {

    @Autowired
    NodesettingService nodesettingService;
    @Autowired
    NodeRepository nodeRepository;

    @Autowired
    EntryCaptureService entryCaptureService;

    @Autowired
    TransdocRepository transdocRepository;
    
    @Autowired
    EntryIndexCaptureRepository entryIndexCaptureRepository;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    EntryDetailRepository entryDetailRepository;

    @Autowired
    ElectronicRepository electronicRepository;

    @Autowired
    TransdocEntryRepository transdocEntryRepository;

    @Autowired
    ElectronicCaptureRepository electronicCaptureRepository;

    @Autowired
    ElectronicService electronicService;

    @Autowired
    ElectronicVersionRepository electronicVersionRepository;

    @Autowired
    ElectronicVersionCaptureRepository electronicVersionCaptureRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    WorkRepository workRepository;

    @Autowired
    UserNodeRepository userNodeRepository;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RightOrganRepository rightOrganRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserDataNodeRepository userDataNodeRepository;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    FlowsRepository flowsRepository;

    @Autowired
    ElectronApproveService electronApproveService;

    @Autowired
    ClassifySearchService classifySearchService;

    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    CaTransforRepository caTransforRepository;

    @Autowired
    TransdocPreviewRepository transdocPreviewRepository;

    @PersistenceContext
    EntityManager entityManager;


    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录

    public int[] move(String[] entryidData, String docid,String taskid){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String transDate= DateUtil.getCurrentTime();
        long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        int insertIndexes = entryIndexRepository.moveindexes(entryidData);
        String[] insertIds = new String[entryidData.length];
        for (int i = 0; i < entryidData.length; i++){
            insertIds[i] = String.format("%1$-36s",(String) entryidData[i]);
        }
        int insertDetails = entryDetailRepository.movedetails(insertIds);
        electronicRepository.moveeletronics(insertIds);
        //更新电子文件历史版本
        electronicVersionRepository.moveEletronicVersions(insertIds);
        //电子文件历史版本存储位置转移
        moveEleCaptureVersionToIndex(insertIds);

        moveEleCaptureToIndex(insertIds);
        transdocEntryRepository.changeStatusByDocid(docid,Tb_transdoc_entry.STATUS_MOVE);//根据docid设置条目状态为“已入库”
        //删除采集表中的数据,先删除主表条目，然后开线程删除关联数据
        entryCaptureService.delEntryOnly(entryidData);
        Tb_transdoc transdoc = transdocRepository.findOne(docid);
        //审核审批
        if(taskid!=null&&!"".equals(taskid)){
            Tb_flows flows = flowsRepository.findByTaskidAndSpman(taskid, userDetails.getUserid());// 获取当前任务流程设置状态
            if(flows==null){   //不是审核人
                flows = flowsRepository.findByMsgidAndState(transdoc.getTransfercode(),"处理中");
                // 更新上一环节工作流信息
                flows.setState(Tb_flows.STATE_FINISHED);// 完成
                flows.setDate(dateInt);
                flows.setSpman(userDetails.getUserid());  //更新审核人

            }else{
                // 更新上一环节工作流信息
                flows.setState(Tb_flows.STATE_FINISHED);// 完成
                flows.setDate(dateInt);
            }
            Tb_node node = nodeRepository.getEndNode("采集移交审核");// 拿到当前节点

            List<Tb_node> nodes = nodeRepository.findByWorkidOrderBySortsequence(node.getWorkid());
            Tb_node node1 = nodes.get(nodes.size() - 1);
            // 完成单据审批
            Tb_flows flows1 = new Tb_flows();
            flows1.setNodeid(node1.getId());
            flows1.setText(node1.getText());
            flows1.setDate(dateInt);
            flows1.setTaskid(taskid);
            flows1.setMsgid(flows.getMsgid());
            flows1.setState(Tb_flows.STATE_FINISHED);// 完成
            flowsRepository.save(flows1);
            electronApproveService.updateElectroInfo(node.getText(), userDetails.getRealname(), taskid);
            transdoc.setApproveman(userDetails.getRealname());
            transdoc.setApprovetime(transDate);
        }
        transdoc.setState(Tb_transdoc.STATE_AUDIT); //根据docid设置单据状态为“已审核”
        transdocEntryRepository.changeStatusByDocid(docid,Tb_transdoc_entry.STATUS_MOVE);//根据docid设置条目状态为“已入库”
        Tb_transdoc transdoc1 = transdocRepository.save(transdoc);
        int delflag = 0 ;
        if(transdoc1!=null){
            delflag = 1;
        }
        return new int[]{insertIndexes, insertDetails, delflag};
    }

    //1 移交签章 ； 2审核签章 ; 0档案员接收
    public Tb_transdoc updateTrandoc(String docid,String type,String pwdno){
        String tranDate=DateUtil.getCurrentTime();
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Tb_transdoc transdoc=transdocRepository.findOne(docid);
        if("1".equals(type)){
            if("1".equals(pwdno)){
                transdoc.setTransforcasigndate("");
                transdoc.setEstablishleader("");
                transdoc.setEstablishleaderdate("");
                transdoc.setTransforcasign("");//标记移交签章完成
            }else {
                transdoc.setTransforcasigndate(tranDate);
                transdoc.setEstablishleader(userDetails.getRealname());
                transdoc.setEstablishleaderdate(tranDate);
                transdoc.setTransforcasign("Y");//标记移交签章完成
            }
        }else if("2".equals(type)){
            if("1".equals(pwdno)){
                transdoc.setEditcasigndate("");
                transdoc.setArchivesleader("");
                transdoc.setArchivesleaderdate("");
                transdoc.setEditcasign("");//标记审核签章完成
            }else {
                transdoc.setEditcasigndate(tranDate);
                transdoc.setArchivesleader(userDetails.getRealname());
                transdoc.setArchivesleaderdate(tranDate);
                transdoc.setEditcasign("Y");//标记审核签章完成
            }
        }else {
            if("1".equals(pwdno)){
                transdoc.setArchivesuser("");
                transdoc.setArchivesuserdate("");
            }else {
                transdoc.setArchivesuser(userDetails.getRealname());
                transdoc.setArchivesuserdate(tranDate);
            }
        }
        return transdocRepository.save(transdoc);
    }

    /**
     * 更新移交审核证书关联表的审核证书编号信息
     * @param editcasign  审核证书是否存在  0不存在 1存在
     * @param caid  证书编号
     * @param docid  移交记录id
     */
    public void updateTransforEditCa(String editcasign,String caid,String docid){
        if("1".equals(editcasign)){//有证书
            //存储用户个人信息和移交条目id关联
            if(caid!=null&&caid.length()>20){//证书编号存在
                Tb_ca_transfor caTransfor=new Tb_ca_transfor();
                List<Tb_ca_transfor> caTransforList=caTransforRepository.findByDocid(docid);
                if(caTransforList.size()==1){//移交时已记录信息
                    caTransfor=caTransforList.get(0);
                }else{
                    caTransfor.setDocid(docid);
                }
                caTransfor.setEditcaid(caid);//记录审核证书编号
                caTransforRepository.save(caTransfor);
            }
        }
    }

    /**
     * 更新移交审核证书关联表的移交证书编号信息
     * @param transforcasign  移交证书是否存在 0不存在 1存在
     * @param caid  证书编号
     * @param docid  移交记录id
     */
    public void updateTransforCa(String transforcasign,String caid,String docid){
        if("1".equals(transforcasign)){//有证书
            //存储用户个人信息和移交条目id关联
            if(caid!=null&&caid.length()>20){//证书编号存在
                Tb_ca_transfor caTransfor=new Tb_ca_transfor();
                caTransfor.setDocid(docid);
                caTransfor.setTransforcaid(caid);//记录移交证书编号
                caTransforRepository.save(caTransfor);
            }
        }
    }

    public String getStorageDir(String entrytype,String entryid) {
        String eledir = rootpath + electronicService.getStorageBaseDir(entrytype,entryid);
        File eleDir = new File(eledir);
        if (!eleDir.exists()) {
            eleDir.mkdirs();
        }
        return eledir;
    }

    /**
     * 入库后，将电子文件转存到management命名的文件夹
     */
    private void moveEleCaptureToIndex(String[] insertIds) {
        for (String id : insertIds) {
            //查找数据采集关联的电子文件
            List<Tb_electronic_capture> captures = electronicCaptureRepository.findByEntryid(id);
            for (Tb_electronic_capture capture : captures) {
                if(capture!=null){
                    //获取电子原文文件地址
                    File targetFile = new File( rootpath + capture.getFilepath(), capture.getFilename());
                    //把电子文件转存到以entryid为最后一层文件夹名称新的文件夹
                    targetFile.renameTo(new File(getStorageDir("management",id),  capture.getFilename()));
                    //查找入库后的电子文件，并修改电子文件路径
                    Tb_electronic electronic = electronicRepository.findByEleid(capture.getEleid());
                    electronic.setFilepath(electronicService.getStorageBaseDir("management",id));
                }
            }
        }
    }

    /**
     * 入库后，将电子文件历史版本转存到management命名的文件夹
     */
    private void moveEleCaptureVersionToIndex(String[] insertIds) {
                    List<Tb_electronic_version_capture> version_captures = electronicVersionCaptureRepository.findByEntryidIn(insertIds);
                    for(Tb_electronic_version_capture version_capture:version_captures){
                        //获取电子原文文件地址
                        File targetFile = new File( rootpath + version_capture.getFilepath(), version_capture.getFilename());
                        //把电子文件转存到以新的路径下
                        String filepath = electronicService.getUploadDirSaveVersion(version_capture.getVersion(),version_capture.getEleid(),"management")
                                .replace(rootpath, "");
                        targetFile.renameTo(new File(rootpath+filepath,version_capture.getFilename()));
                        //查找入库后的电子文件，并修改电子文件路径
                        Tb_electronic_version electronic = electronicVersionRepository.findById(version_capture.getId());
                        electronic.setFilepath(filepath);
                    }
    }

    public int[] sendback(String docid,String sendbackreason,String taskid){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        Tb_transdoc transdoc = transdocRepository.findOne(docid);
        transdoc.setSendbackreason(sendbackreason); //根据docid设置退回原因
        transdoc.setState(Tb_transdoc.STATE_SENDBACK);  //根据docid设置单据状态为“已退回”
        transdoc.setApproveman(userDetails.getRealname());
        transdoc.setApprovetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        //无数据审核模块，自动退回
        if(taskid==null){
            Tb_flows flows = flowsRepository.findByMsgidAndState(transdoc.getTransfercode(),Tb_flows.STATE_HANDLE);
            taskid = flows.getTaskid();
        }
        Tb_task task = taskRepository.findByTaskid(taskid);// 获取任务修改任务状态
        task.setState(Tb_task.STATE_FINISHED);// 完成
        Tb_flows flow = flowsRepository.findByTaskidAndSpman(taskid, userDetails.getUserid());// 获取当前任务流程设置状态
        if(flow==null){   //不是审核人
            flow = flowsRepository.findByMsgidAndState(transdoc.getTransfercode(),"处理中");
            // 更新上一环节工作流信息
            flow.setState(Tb_flows.STATE_FINISHED);// 完成
            flow.setDate(dateInt);
            flow.setSpman(userDetails.getUserid());  //更新审核人

        }else{
            // 更新上一环节工作流信息
            flow.setState(Tb_flows.STATE_FINISHED);// 完成
            flow.setDate(dateInt);
        }

        Tb_node node = nodeRepository.getEndNode("采集移交审核");// 拿到当前节点
        List<Tb_node> nodes = nodeRepository.findByWorkidOrderBySortsequence(node.getWorkid());
        Tb_node node1 = nodes.get(nodes.size() - 1);
        Tb_flows flows1 = new Tb_flows();
        flows1.setNodeid(node1.getId());
        flows1.setText(node1.getText());
        flows1.setDate(dateInt);
        flows1.setTaskid(taskid);
        flows1.setMsgid(flow.getMsgid());
        flows1.setState(Tb_flows.STATE_FINISHED);// 完成
        flowsRepository.save(flows1);// 结束整个流程
        int num=0;
        String[] entryidData = getEntryidsByDocid(docid);
        List<String[]> subChoiceAry = new InformService().subArray(entryidData, 1000);// 处理参数超出问题
        for (String[] strings : subChoiceAry) {
            num = transdocEntryRepository.changeStatusByEntryid(strings,Tb_transdoc_entry.STATUS_TRANSFOR);//根据entryid设置条目状态为“待移交”
            transdocPreviewRepository.deleteByEntryidIn(strings);//移除加入移交案卷的条目
        }
        Tb_transdoc transdoc1 = transdocRepository.save(transdoc);
        int delflag = 0;
        if(transdoc1!=null){
            delflag = 1;
        }
        return new int[]{num, delflag};
    }

    public Page<Tb_transdoc> findBySearch(int page, int limit, String condition, String operator, String content, String nodeID, Sort sort) {
        Specifications sp = null;
        Specification<Tb_transdoc> searchID = getSearchNodeidStateCondition(nodeID,Tb_transdoc.STATE_TRANSFOR);//查找已移交的单据
        sp = Specifications.where(searchID);
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        return transdocRepository.findAll(sp, pageRequest);
    }

    public Page<TransdocVO> findTransdocVoBySearch(String state,String taskid,int page, int limit, String condition, String operator, String content, Sort sort) {
        Specifications sp = null;
        //s：查询谓词(默认时间DESC排序)
        String s="order by transdate DESC";
        if(sort != null){
            String[] str=sort.toString().split(":");
            s="order by "+str[0]+" "+str[1];
        }
        sort = sort==null?new Sort(Sort.Direction.ASC,"transdate"):sort;
        if(!StringUtils.isEmpty(taskid)){//有taskid就是点击了task条目 所以这里绕开权限设置
            Tb_transdoc transdoc = transdocRepository.getByTaskid(taskid);
            sp = Specifications.where(new SpecificationUtil("docid","equal",transdoc.getDocid()));
            PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
            Page<Tb_transdoc> transdocPage = transdocRepository.findAll(sp, pageRequest);
            List<Tb_transdoc> transdocs=transdocPage.getContent();
            List<TransdocVO> returnTransdocs = new ArrayList<>();
            Map<String, Object[]> parentmap = nodesettingService.findAllParentOfNode();
            for(Tb_transdoc tb_transdoc:transdocs){
                TransdocVO returnTransdoc=new TransdocVO();
                String nodeid = transdoc.getNodeid();
                BeanUtils.copyProperties(tb_transdoc,returnTransdoc);
                Tb_data_node node = (Tb_data_node) parentmap.get(nodeid.trim())[0];
                List<Tb_data_node> parents = (List<Tb_data_node>) parentmap.get(nodeid.trim())[1];
                StringBuffer nodefullname = new StringBuffer(node.getNodename());
                for (Tb_data_node parent : parents) {
                    if (parent == null) {
                        continue;
                    }
                    nodefullname.insert(0, "_");
                    nodefullname.insert(0, parent.getNodename());
                }
                returnTransdoc.setNodefullname(nodefullname.toString());
                returnTransdocs.add(returnTransdoc);
            }
            return new PageImpl<TransdocVO>(returnTransdocs,pageRequest,transdocPage.getTotalElements());
        }else{
            sp = Specifications.where(new SpecificationUtil("state","equal",state));
        }
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        //查询用户的所有节点权限
       /* String[] roleids = new String[]{};
        List<String> nodeids = new ArrayList<>();
        List<Tb_role> roles = roleRepository.findBygroups(userDetails.getUserid());
        String nodeidStr = "";
        if(roles.size()>0){
            roleids = GainField.getFieldValues(roles,"roleid").length==0?new String[]{""}:GainField.getFieldValues(roles,"roleid");
            nodeids = userDataNodeRepository.findBynodes(roleids,userDetails.getUserid());// 选中用户所拥有的权限
            //过滤显示数据节点权限的单据
            if(nodeids.size()>0){
                nodeidStr = " and sid.nodeid in('" + String.join("','", nodeids) + "') ";
            }

        }*/
        String nodeidStr = userService.getAuthNodeids(userDetails.getUserid());
        String searchCondition = "";//检索框
        if (content != null && !"".equals(content)) {// 输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        String sql = "select sid.* from tb_transdoc sid where state='"+ state +"'"+ searchCondition + nodeidStr+" "+s;
        String countSql = "select count(*) from tb_transdoc sid where state='"+ state +"'"+ searchCondition + nodeidStr;
        Query query = entityManager.createNativeQuery(sql, Tb_transdoc.class);
        query.setFirstResult((page - 1) * limit);
        query.setMaxResults(limit);
        
        List<Tb_transdoc> transdocs = query.getResultList();
        Query couuntQuery = entityManager.createNativeQuery(countSql);
        int count = Integer.parseInt(couuntQuery.getResultList().get(0) + "");
        List<TransdocVO> returnTransdocs = new ArrayList<>();
        Map<String, Object[]> parentmap = nodesettingService.findAllParentOfNode();
        for(Tb_transdoc transdoc : transdocs){
            TransdocVO returnTransdoc = new TransdocVO();
            String nodeid = transdoc.getNodeid().trim();
            BeanUtils.copyProperties(transdoc,returnTransdoc);
            Tb_data_node node = (Tb_data_node) parentmap.get(nodeid)[0];
            List<Tb_data_node> parents = (List<Tb_data_node>) parentmap.get(nodeid)[1];
            StringBuffer nodefullname = new StringBuffer(node.getNodename());
            for (Tb_data_node parent : parents) {
                if (parent == null) {
                    continue;
                }
                nodefullname.insert(0, "_");
                nodefullname.insert(0, parent.getNodename());
            }
            returnTransdoc.setNodefullname(nodefullname.toString());
            returnTransdocs.add(returnTransdoc);
        }
        return new PageImpl<TransdocVO>(returnTransdocs,pageRequest,count);
    }


    public static Specification<Tb_transdoc> getSearchNodeidStateCondition(String nodeid,String state){
        Specification<Tb_transdoc> searchNodeidStateCondition = new Specification<Tb_transdoc>() {
            @Override
            public Predicate toPredicate(Root<Tb_transdoc> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate[] predicates=new Predicate[2];
                predicates[0] = criteriaBuilder.equal(root.get("nodeid"), nodeid);
                predicates[1] = criteriaBuilder.equal(root.get("state"), state);
                return criteriaBuilder.and(predicates);
            }
        };
        return searchNodeidStateCondition;
    }

    public String[] getEntryidsByDocid(String docid){
        List<Tb_transdoc_entry> transdocEntryList = transdocEntryRepository.findByDocid(docid);
        return GainField.getFieldValues(transdocEntryList, "entryid").length == 0 ? new String[]{""} : GainField.getFieldValues(transdocEntryList, "entryid");
    }

    //根据节点ID获取待审核的条目ID
    public String[] getAuditEntryidsByNodeid(String nodeid){
        List<Tb_transdoc> transdocs = transdocRepository.findByNodeidIn(new String[]{nodeid});
        List<Tb_transdoc_entry> transdocEntryList = transdocEntryRepository.findByStatusAndDocidIn(Tb_transdoc_entry
                .STATUS_AUDIT,GainField.getFieldValues(transdocs, "docid").length == 0 ? new String[]{""} :
                GainField.getFieldValues(transdocs, "docid"));
        return GainField.getFieldValues(transdocEntryList, "entryid").length == 0 ? new String[]{""} : GainField.getFieldValues(transdocEntryList, "entryid");
    }

    /**
    * 获得数据采集移交审批人
    *
    * @param worktext
    * @param nodeid
    * @return {@link List< Tb_user>}
    * @throws
    */
    public List<Tb_user> getApproveMan(String worktext,String nodeid) {
        // 通过worktext获取工作流ID
        Tb_work work = workRepository.findByWorktext(worktext);
        String nodeId = "";
        if (work != null && !StringUtils.isEmpty(work.getText())) {
            Tb_node node = nodeRepository.findByWorkidAndSortsequence(work.getId(), 1);
            nodeId = node.getNextid().split(",")[0];
        }
        List<Tb_user_node> user_nodes = userNodeRepository.findByNodeid(nodeId);
        String[] userids = GainField.getFieldValues(user_nodes, "userid").length == 0 ? new String[] { "" }
                : GainField.getFieldValues(user_nodes, "userid");
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //获取当前节点的机构
        Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
        String organid = node.getOrganid();
        Tb_right_organ organnow = rightOrganRepository.findOne(organid);
        while (organnow.getOrgantype() != null && organnow.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT) && !"0".equals(organnow.getParentid())) {// 获取单位对象
            organnow = rightOrganRepository.findOne(organnow.getParentid());
        }
        List<String> idStrings = new ArrayList<>();
        //过滤其他单位的审批人
        for (int i = 0; i < userids.length; i++) {
            if (!userids[i].equals(userDetails.getUserid())) {  //去除当前用户的审批人
                String userorganid = userRepository.findOrganidByUserid(userids[i]);// 获取审批用户机构id
                Tb_right_organ userorgan = rightOrganRepository.findOne(userorganid);  //获取当前审批人的机构
                //获取当前审批人所属的单位
                while (userorgan.getOrgantype() != null && userorgan.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT) && !"0".equals(userorgan.getParentid())) {// 获取单位对象
                    userorgan = rightOrganRepository.findOne(userorgan.getParentid());
                }
                if(userorgan.getOrganid().equals(organnow.getOrganid())){  //判断所在的机构是否一致
                    idStrings.add(userids[i]);
                }
            }
        }

        List<Tb_user> users = userRepository.findByUseridIn(idStrings);// 获取本单位下的全部用户
        List<Tb_user> backUsers = new ArrayList<>(); // 返回的排序用户
        List<Tb_user> unitUsers = new ArrayList<>(); // 单位非排序用户
        List<Tb_user> allDepartmentUsers = new ArrayList<>();// 全部部门非排序用户
        for (Tb_user user : users) {
            if (organid != null && organid.trim().equals(user.getOrganid().trim())) {
                backUsers.add(user);// 如果是本部门审批人则放在一级集合
            } else {
                if (organnow.getOrganid() != null && organnow.getOrganid().trim().contains(user.getOrganid().trim())) {
                    unitUsers.add(user);// 如果是本单位审批人则放在二级级集合
                } else {
                    allDepartmentUsers.add(user);// 剩余放在全部部门集合
                }
            }
        }
        backUsers.addAll(unitUsers);// 合并
        backUsers.addAll(allDepartmentUsers);
        List<String> lusers = new ArrayList<String>();
        if (backUsers.size() > 0) {
            for (int i = 0; i < backUsers.size(); i++) {
                Tb_user user = backUsers.get(i);
                lusers.add(user.getRealname() + "-" + user.getUserid());
            }
        }
        List<Tb_user> returnList = new ArrayList<>();
        for (int i = 0; i < lusers.size(); i++) {
            String[] info = lusers.get(i).split("-");
            Tb_user userinfo = new Tb_user();
            userinfo.setUserid(info[1]);
            userinfo.setRealname(info[0]);
            returnList.add(userinfo);
        }
        return returnList;
    }

    //根据单据ID获取待审核的条目ID
    public String getTaskid(String docid){
        Tb_transdoc transdoc = transdocRepository.findOne(docid);
        //List<Tb_flows> flows = flowsRepository.findByMsgid(transdoc.getTransfercode());
        List<Tb_flows> flows = flowsRepository.findWithMsgidAndState(transdoc.getTransfercode(),Tb_flows.STATE_HANDLE);
        String taskid = flows.get(0).getTaskid();
        return taskid;
    }

    public Page<Tb_index_detail> getEntries(String nodeid,String docid, String condition, String operator, String content, int page, int limit, Sort sort,String parententryid) {
        PageRequest pageRequest = new PageRequest(page - 1, limit);

        String sortStr="";//排序
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            if("eleid".equals(order.getProperty())){
                sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
            }else {
                sortStr = " order by " + order.getProperty() + " " + order.getDirection();
            }
        }else{
            sortStr = " order by archivecode desc, descriptiondate desc,entryid desc  ";
        }

        String shCondition="";//审核筛选
        if(docid!=null && !"".equals(docid)){//数据审核模块查看单据的条目详细信息
            shCondition=" and entryid in(select entryid from tb_transdoc_entry where docid ='"+ docid +"'";
        }
        String nodeIdSql=" nodeid ='"+nodeid+"' ";
        if(parententryid!=null||"".equals(parententryid)){
            if("true".equals(parententryid)){
                shCondition+=")";
                //shCondition+=" and ( parententryid!='' or parententryid is not null))";
            }else {
                shCondition+=") and archivecode like '"+parententryid+"%' ";
            }
        }else{
            shCondition+=")";
        }

        String searchCondition = "";//检索框
        if (content != null && !"".equals(content)) {// 输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }

        String sql = "select sid.* from v_index_detail sid where "+ nodeIdSql+searchCondition +shCondition;
        String countSql = "select count(nodeid) from v_index_detail sid where "+nodeIdSql+ searchCondition +shCondition;
        return entryIndexService.getPageListTwo(sql, sortStr, countSql, page, limit, pageRequest);
    }

    //采集移交审核完成，暂时未入库
    public void approveTransDoc(String taskid,String nodeid,String nextNodeId, String nextSpman,String docid){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String tranDate=DateUtil.getCurrentTime();
        long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        Tb_task task1 = taskRepository.findByTaskid(taskid);
        //Tb_flows flows = flowsRepository.findByTaskidAndSpman(taskid, userDetails.getUserid());// 获取当前任务流程设置状态
        // 获取当前任务流程中处理中的flows,改为有办理权限的用户可以审核，不能按当前用户id去获取流程
        List<Tb_flows> flowses= flowsRepository.findAuditFlows(taskid, Tb_flows.STATE_HANDLE);
        Tb_flows flows =flowses.get(0);
        //List<Tb_node> node = nodeRepository.findByNextidIn(new String[]{nextNodeId});// 拿到当前节点
        Tb_node nextNode = nodeRepository.findByNodeid(nextNodeId);// 拿到下一节点
        String nextSpmanRealname = userRepository.findByUserid(nextSpman).getRealname();
        // 更新上一个环节的任务信息
        electronApproveService.updateElectroInfo(nextNode.getText(), nextSpmanRealname, taskid);
        // 创建下一环节的任务信息
        Tb_task task = new Tb_task();
        task.setState(Tb_task.STATE_WAIT_HANDLE);// 处理中
        task.setTime(new Date());
        task.setLoginname(nextSpman);
        task.setText(task1.getText());
        task.setType(task1.getType());
        task.setApprovetext(nextNode.getText());
        task.setApproveman(nextSpmanRealname);
        task.setLastid(taskid);
        Tb_task task2 = taskRepository.save(task);// 下一审批人任务
        // 更新上一环节的工作流信息
        flows.setState(Tb_flows.STATE_FINISHED);
        flows.setDate(dateInt);
        // 创建下一环节的工作流信息
        Tb_flows flows1 = new Tb_flows();
        flows1.setNodeid(nextNode.getId());
        flows1.setText(nextNode.getText());
        flows1.setSpman(nextSpman);
        flows1.setTaskid(task2.getId());
        flows1.setMsgid(flows.getMsgid());
        flows1.setState(Tb_flows.STATE_HANDLE);// 处理中
        flows1.setDate(dateInt);
        flowsRepository.save(flows1);// 下一流程
    }
}
