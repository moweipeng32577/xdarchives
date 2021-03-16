package com.wisdom.web.service;

import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 数字化加工服务层
 */
@Service
@Transactional
public class DigitalProcessService {

    @Autowired
    SzhAssemblyFlowsRepository szhAssemblyFlowsRepository;

    @Autowired
    SzhArchivesCalloutRepository szhArchivesCalloutRepository;

    @Autowired
    SzhCalloutEntryRepository szhCalloutEntryRepository;

    @Autowired
    SzhEntryIndexCaptureRepository shzEntryIndexCaptureRepository;

    @Autowired
    SzhEntryDetailCaptureRepository szhEntryDetailCaptureRepository;

    @Autowired
    EntryIndexCaptureRepository entryIndexCaptureRepository;

    @Autowired
    EntryDetailCaptureRepository entryDetailCaptureRepository;

    @Autowired
    SzhCalloutCaptureRepository szhCalloutCaptureRepository;

    @Autowired
    CodesetRepository codesetRepository;

    @Autowired
    SzhFlowsRecordRepository szhFlowsRecordRepository;

    @Autowired
    SzhElectronicCaptureRepository szhElectronicCaptureRepository;

    @Autowired
    ElectronicCaptureRepository electronicCaptureRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    ReportDataRepository reportDataRepository;

    @Autowired
    AssemblyAdminService assemblyAdminService;

    @Autowired
    SzhAdminUserRepository szhAdminUserRepository;

    @Autowired
    SzhAssemblyUserRepository szhAssemblyUserRepository;

    @Autowired
    SzhEntryTrackRepository szhEntryTrackRepository;

    @Autowired
    SzhLinkBackRepository szhLinkBackRepository;

    @Autowired
    SzhAssemblyRepository szhAssemblyRepository;

    @Autowired
    SzhAssemblyPreflowRepository szhAssemblyPreflowRepository;

    @Autowired
    SzhLinkSignRepository szhLinkSignRepository;

    @Autowired
    SzhAuditStatusRepository szhAuditStatusRepository;

    //用于根据节点拼接查询条件
    public static  Map<String,String> flowsMap = new HashMap<>();
    static {
        flowsMap.put("整理","tidy");
        flowsMap.put("扫描","scan");
        flowsMap.put("图像处理","pictureprocess");
        flowsMap.put("审核","audit");
        flowsMap.put("著录","record");
        flowsMap.put("属性定义","definition");
        flowsMap.put("装订","bind");
    }

    public Page<Szh_callout_entry> getCalloutEntryBySearch(String type, String status, String batchcode,
                                                           String flownodeid,String assemblyid, int page, int limit,
                                                           String condition, String operator, String content,
                                                           String sort){
        Sort sortobj=WebSort.getSortByJson(sort);
        PageRequest pageRequest=new PageRequest(page-1,limit,sortobj);
        Specifications sp = null;
//        if (status!=null&&!"".equals(status)) {
//            sp = Specifications.where(new SpecificationUtil(flowsMap.get(status),"equal",type));
//        }
        /*PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort(Sort.Direction
                .ASC, "archivecode") : sort);*/
        if (status!=null&&!"".equals(status)) {
            if("未签收".equals(type)){
                Specification<Szh_callout_entry> wqsCondition = new Specification<Szh_callout_entry>() {
                    @Override
                    public Predicate toPredicate(Root<Szh_callout_entry> root, CriteriaQuery<?> criteriaQuery,
                                                 CriteriaBuilder criteriaBuilder) {

                        CriteriaBuilder.In in = criteriaBuilder.in(root.get(flowsMap.get(status).toString()));
                        in.value("未签收");
                        in.value("审核退回");
                        in.value("质检退回");
                        in.value("完成退回");
                        in.value("");
                        in.value("-");
                        Predicate aNull = criteriaBuilder.isNull(root.get(flowsMap.get(status).toString()));
                        //因为直接导入数据的状态为Null,所以把Null状态也一起显示了。

                        return criteriaBuilder.or(in,aNull);
                    }
                };
                sp = sp.where(wqsCondition);
                //判断环节的前置环节
                if(!"*".equals(batchcode)&&!"".equals(batchcode)){
                    Szh_archives_callout archives_callout = szhArchivesCalloutRepository.findByBatchcode(batchcode);
                    Szh_assembly assembly = szhAssemblyRepository.findByCode(archives_callout.getAssemblycode());
                    List<String> preflowids = szhAssemblyPreflowRepository.getPreflowid(assembly.getId(),flownodeid);
                    if(preflowids.size()>0){
                        Szh_assembly_flows assembly_flow = szhAssemblyFlowsRepository.findById(preflowids.get(0));
                        String flow = flowsMap.get(assembly_flow.getNodename());
                        Specification<Szh_callout_entry> getwqsCondition = new Specification<Szh_callout_entry>() {
                            @Override
                            public Predicate toPredicate(Root<Szh_callout_entry> root, CriteriaQuery<?> criteriaQuery,
                                                         CriteriaBuilder criteriaBuilder) {
                                Predicate[] predicates = new Predicate[2];
                                predicates[0] = criteriaBuilder.equal(root.get("batchcode"), batchcode);
                                predicates[1] = criteriaBuilder.equal(root.get(flow), "已处理");
                                return criteriaBuilder.and(predicates);
                            }
                        };
                        sp = sp.and(getwqsCondition);
                    }
                }else{
                    List<String> preflowids = szhAssemblyPreflowRepository.getPreflowid(assemblyid,flownodeid);
                    if(preflowids.size()>0){
                        Szh_assembly_flows assembly_flow = szhAssemblyFlowsRepository.findById(preflowids.get(0));
                        String flow = flowsMap.get(assembly_flow.getNodename());
                        Specification<Szh_callout_entry> getwqsCondition = new Specification<Szh_callout_entry>() {
                            @Override
                            public Predicate toPredicate(Root<Szh_callout_entry> root, CriteriaQuery<?> criteriaQuery,
                                                         CriteriaBuilder criteriaBuilder) {
                                Predicate[] predicates = new Predicate[1];
                                predicates[0] = criteriaBuilder.equal(root.get(flow), "已处理");
                                return criteriaBuilder.and(predicates);
                            }
                        };
                        sp = sp.and(getwqsCondition);
                    }
                }
            }else{
                SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                List<Szh_admin_user> admin_users = szhAdminUserRepository.findByUserid(userDetails.getUserid());
                if(admin_users.size()>0){  //判断是否数字化加工管理员
                    sp = Specifications.where(new SpecificationUtil(flowsMap.get(status),"equal",type));
                }else {
                    List<String> calloutids = new ArrayList<>();
                    if("".equals(batchcode)||batchcode==null){
                        calloutids = szhLinkSignRepository.getByUseridAndAndLink(userDetails.getUserid(),status,assemblyid);
                    }else{
                        calloutids = szhLinkSignRepository.getByUseridAndAndBatchcodeAndAndLink(userDetails.getUserid(),batchcode,status);
                    }
                    List<String> ids = calloutids;
                    if(calloutids.size()>0){  //判断当前用户此环节是否有签收的条目
                        Specification<Szh_callout_entry> getidCondition = new Specification<Szh_callout_entry>() {
                            @Override
                            public Predicate toPredicate(Root<Szh_callout_entry> root, CriteriaQuery<?> criteriaQuery,
                                                         CriteriaBuilder criteriaBuilder) {

                                CriteriaBuilder.In in = criteriaBuilder.in(root.get("id"));
                                for(String id : ids){
                                    in.value(id);
                                }
                                return criteriaBuilder.or(in);
                            }
                        };
                        sp = Specifications.where(new SpecificationUtil(flowsMap.get(status),"equal",type)).and(getidCondition);
                    }else{
                        return null;
                    }
                }
            }
        }else{
            return null;
        }

        if (batchcode!=null&&!"".equals(batchcode)) {
            sp = ClassifySearchService.addSearchbarCondition(sp,"batchcode","equal",batchcode);
        }

        if (content != null&&!"".equals(content)) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        return szhCalloutEntryRepository.findAll(sp, new PageRequest(page - 1, limit,sortobj));

    }

    public Page<Szh_callout_entry> getFinishCalloutEntryBySearch(String batchcode, int page, int limit, String condition, String operator, String content){
        Specifications sp = null;
        Set<String> keySet = flowsMap.keySet();
//        for(String key:keySet){
//            sp = ClassifySearchService.addSearchbarCondition(sp,flowsMap.get(key),"equal","已处理");
//        }
        if (batchcode!=null&&!"".equals(batchcode)) {
            sp = ClassifySearchService.addSearchbarCondition(sp,"batchcode","equal",batchcode);
        }
        return szhCalloutEntryRepository.findAll(sp, new PageRequest(page - 1, limit));
    }

    public Page<Szh_flows_record> getDealDetails(String id,int page, int limit, String condition, String operator, String content){
        Specifications sp = null;
        if (id!=null&&!"".equals(id)) {
            sp = Specifications.where(new SpecificationUtil("calloutid","equal",id));
        }
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(new Sort.Order(Sort.Direction.ASC,"operatetime"));
        return szhFlowsRecordRepository.findAll(sp, new PageRequest(page - 1, limit,new Sort(sorts)));
    }

    public List<ExtNcTree> getFlowsTree(String assemblyid){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Szh_assembly_flows> flows = assemblyAdminService.getLinkByid(assemblyid,"preflow");
        List<Szh_admin_user> admin_users = szhAdminUserRepository.findByUserid(userDetails.getUserid());
        List<ExtNcTree> trees = new ArrayList<>();
        if(admin_users.size()>0){
            for(Szh_assembly_flows flow:flows){//构造树结构
                ExtNcTree tree = new ExtNcTree();
                tree.setFnid(flow.getId());
                tree.setLeaf(true);
                tree.setText(flow.getNodename());
                trees.add(tree);
            }
        }else{
            List<String> assemblyflowids = szhAssemblyUserRepository.getAssemblyflowidByUserid(assemblyid,userDetails.getUserid());
            List<Szh_assembly_flows> newflows = new ArrayList<>();
            for(int i=0;i<flows.size();i++){
                for(int j=0;j<assemblyflowids.size();j++){
                    if(flows.get(i).getId().equals(assemblyflowids.get(j))){
                        newflows.add(flows.get(i));
                        break;
                    }
                }
            }
            for(Szh_assembly_flows flow:newflows){//构造树结构
                ExtNcTree tree = new ExtNcTree();
                tree.setFnid(flow.getId());
                tree.setLeaf(true);
                tree.setText(flow.getNodename());
                trees.add(tree);
            }
        }
        return trees;
    }

    public List<ExtNcTree> getBatchTree(String batchcode){
        List<Szh_archives_callout> callouts = szhArchivesCalloutRepository.findByAssemblycode(batchcode);
        List<ExtNcTree> trees = new ArrayList<>();
        for(Szh_archives_callout callout:callouts){//构造树结构
            ExtNcTree tree = new ExtNcTree();
            tree.setFnid(callout.getBatchcode());
            tree.setLeaf(true);
            tree.setText(callout.getBatchname());
            trees.add(tree);
        }
        return trees;
    }

    public void calloutSign(String[] ids, String node, String status, String assemblyid) throws Exception {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 系统绑定对象(全局)
        List<Szh_callout_entry> entries = szhCalloutEntryRepository.findByIdIn(ids);
        boolean isWqs = "未签收".equals(status);
        boolean isYqs = "已签收".equals(status);
        status = "未签收".equals(status) ? "已签收" : "已处理";
        String signTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//HH时间24小时制
        List<Szh_flows_record> flowsRecords = new ArrayList<>();
        List<Szh_link_sign> linkSigns = new ArrayList<>();
        for (Szh_callout_entry entry : entries) {
            switch (node) {
                case "整理":
                    entry.setTidy(status);
                    break;
                case "扫描":
                    entry.setScan(status);
                    entry.setScanstate(status);
                    break;
                case "图像处理":
                    entry.setPictureprocess(status);
                    entry.setPicturestate(status);
                    break;
                case "属性定义":
                    entry.setDefinition(status);
                    break;
                case "审核":
                    entry.setAudit(status);
                    entry.setCheckstate(status);
                    break;
                case "装订":
                    entry.setBind(status);
                    break;
                case "著录":
                    entry.setRecord(status);
                    break;
                default:
                    System.out.println("***");
            }
            entry.setBusinesssigner(userDetails.getRealname());//设置业务签收人
            entry.setBusinesssigncode(userDetails.getLoginname());//设置业务签收号
            entry.setSigntime(signTime);//设置业务签收时间
            linkSigns.add(new Szh_link_sign(userDetails.getUserid(), entry.getId(), node, signTime, entry.getBatchcode(), assemblyid));//生成签收人条目对应信息
            if (isWqs) {
                flowsRecords.add(new Szh_flows_record(entry.getArchivecode(), entry.getBatchcode(), entry.getId(), node, userDetails.getLoginname(), signTime, status));
                szhFlowsRecordRepository.save(flowsRecords);//添加已签收环节记录日志
            }
            if (isYqs) {
                flowsRecords.add(new Szh_flows_record(entry.getArchivecode(), entry.getBatchcode(), entry.getId(), node, userDetails.getLoginname(), signTime, status));
                szhFlowsRecordRepository.save(flowsRecords);//添加已处理环节记录日志
            }
            if ("已签收".equals(status)) {
                if (entry.getWorkstate().equals("未处理")) {
                    entry.setWorkstate("加工中");
                }
                if (entry.getWorkstate().equals("(质检退回)加工完成")) {
                    entry.setWorkstate("(质检退回)加工中");
                }
                if (entry.getWorkstate().equals("加工完成")) {
                    entry.setWorkstate("加工中");
                }
            }
        }
        if ("已签收".equals(status)) {
            szhLinkSignRepository.save(linkSigns);
        }
    }

    public SzhEntryCapture saveEntry(SzhEntryCapture entry, String type) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 系统绑定对象(全局)
        SzhEntryCapture result = new SzhEntryCapture();
        Szh_entry_index_capture index = entry.getEntryIndex();
        if (type.equals("modify")) {
            Szh_entry_index_capture entryInfo = shzEntryIndexCaptureRepository.findByEntryid(entry.getEntryid());
            if (entryInfo != null) {
                if(entryInfo.getEleid()!=null){
                    index.setEleid(entryInfo.getEleid());
                }
                if (index.getTitle() == null) {// 题名
                    index.setTitle(entryInfo.getTitle());
                }
                if (index.getFilenumber() == null) {// 文件编号
                    index.setFilenumber(entryInfo.getFilenumber());
                }
                if (index.getArchivecode() == null) {// 档号
                    index.setArchivecode(entryInfo.getArchivecode());
                }
                if (index.getFunds() == null) {// 全宗号
                    index.setFunds(entryInfo.getFunds());
                }
                if (index.getCatalog() == null) {// 目录号
                    index.setCatalog(entryInfo.getCatalog());
                }
                if (index.getFilecode() == null) {// 案卷号
                    index.setFilecode(entryInfo.getFilecode());
                }
                if (index.getInnerfile() == null) {// 卷内顺序号
                    index.setInnerfile(entryInfo.getInnerfile());
                }
                if (index.getFilingyear() == null) {// 归档年度
                    index.setFilingyear(entryInfo.getFilingyear());
                }
                if (index.getKeyword() == null) {// 主题词
                    index.setKeyword(entryInfo.getKeyword());
                }
                if (index.getEntryretention() == null) {// 保管期限
                    index.setEntryretention(entryInfo.getEntryretention());
                }
                if (index.getOrgan() == null) {// 机构问题
                    index.setOrgan(entryInfo.getOrgan());
                }
                if (index.getRecordcode() == null) {// 件号
                    index.setRecordcode(entryInfo.getRecordcode());
                }
                if (index.getEntrysecurity() == null) {// 密级
                    index.setEntrysecurity(entryInfo.getEntrysecurity());
                }
                if (index.getPages() == null) {// 页数
                    index.setPages(entryInfo.getPages());
                }
                if (index.getPageno() == null) {// 页号
                    index.setPageno(entryInfo.getPageno());
                }
                if (index.getFiledate() == null) {// 文件日期
                    index.setFiledate(entryInfo.getFiledate());
                }
                if (index.getResponsible() == null) {// 责任者
                    index.setResponsible(entryInfo.getResponsible());
                }
                if (index.getSerial() == null) {// 文件流水号
                    index.setSerial(entryInfo.getSerial());
                }
                if (index.getFlagopen() == null) {// 开放状态
                    index.setFlagopen(entryInfo.getFlagopen());
                }
                if (index.getOpendate() == null) {// 开放时间
                    index.setOpendate(entryInfo.getOpendate());
                }
                if (index.getEntrystorage() == null) {// 存储位置
                    index.setEntrystorage(entryInfo.getEntrystorage());
                }
                if (index.getDescriptiondate() == null) {// 著录时间
                    index.setDescriptiondate(entryInfo.getDescriptiondate());
                }
                if (index.getDescriptionuser() == null) {// 著录用户
                    index.setDescriptionuser(entryInfo.getDescriptionuser());
                }
                index.setNodeid(entryInfo.getNodeid());
            }
        } else {
            index.setFlagopen(null);//新增的条目开放状态都是空的
        }
        index.setKccount(index.getFscount());// 新增还是修改,库存份数都等于份数
        index = shzEntryIndexCaptureRepository.save(index);
        Szh_entry_detail_capture detail = entry.getEntryDetail();
        index.setUserid(userDetails.getUserid());
        detail.setUserid(userDetails.getUserid());
        detail.setEntryid(index.getEntryid());
        detail = szhEntryDetailCaptureRepository.save(detail);
        result.setEntryIndex(index);
        result.setEntryDetial(detail);
        return result;
    }

    public boolean putStorage(String[] ids){
        boolean state = false;
        try {
            List<Szh_callout_entry> callout_entrys = szhCalloutEntryRepository.findByIdIn(ids);
            for(Szh_callout_entry callout_entry:callout_entrys){
                callout_entry.setWorkstate("质检/验收中");
            }
            String[] captureids =  szhCalloutCaptureRepository.findEntryids(ids);

            //根据entryid查找出eleid
            String[] updateEleids = szhElectronicCaptureRepository.findEleids(captureids);
            //删除质检库中著录信息
            entryIndexCaptureRepository.deleteIndexes(captureids);
             entryDetailCaptureRepository.deleteDetails(captureids);
             if(updateEleids!=null&&updateEleids.length>0){
                 //删除采集电子文件
                 electronicCaptureRepository.delectEles(updateEleids);
             }

            //重新插入质检库中著录信息
            entryIndexCaptureRepository.moveindexes(captureids);
            String[] insertIds = new String[captureids.length];
            for (int i = 0; i < captureids.length; i++){
                insertIds[i] = String.format("%1$-36s",(String) captureids[i]);
            }
            entryDetailCaptureRepository.movedetails(insertIds);
            //重新插入采集电子文件
            electronicCaptureRepository.moveeletronics(insertIds);

            state = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return state;
    }

    public String getNodeid(String calloutId){
        String enyrtid =  szhCalloutCaptureRepository.findEntryid(calloutId);
        Szh_entry_index_capture entry_index_capture = shzEntryIndexCaptureRepository.findByEntryid(enyrtid);
        return entry_index_capture.getNodeid();
    }

    public String getEntryid(String calloutId){
        String enyrtid =  szhCalloutCaptureRepository.findEntryid(calloutId);
        return enyrtid;
    }

    public SzhEntryCapture mergeEntry(String nodeid,String calloutId,String userId){
        Page<SzhEntryCapture> page = szhEntries(null,calloutId,null,null,null,1,100);
        List<SzhEntryCapture> entryList = page.getContent();//获取多用户录入数据
        List<Tb_data_template> iTemplateList = templateRepository.findByNodeid(nodeid);//获取模板
        SzhEntryCapture szhEntryCapture = new SzhEntryCapture();
        try {
            if(entryList!=null&&entryList.size()>0){//获取第一条数据,生成备份避开代理
                SzhEntryCapture captureTemp = null;
                if(userId==null){//用户id为空时默认以第一条为蓝本
                    captureTemp = entryList.get(0);
                }else{
                    for(SzhEntryCapture sc:entryList){//以指定用户录入数据为蓝本
                        if(userId.equals(sc.getEntryIndex().getUserid())){
                            captureTemp = sc;
                        }
                    }
                }
                Szh_entry_index_capture captureIndex = new Szh_entry_index_capture();
                Szh_entry_detail_capture detailCapture = new Szh_entry_detail_capture();
                BeanUtils.copyProperties(captureTemp.getEntryIndex(),captureIndex);
                BeanUtils.copyProperties(captureTemp.getEntryDetail(),detailCapture);
                captureIndex.setEntryid(null);//清空记录id
                detailCapture.setEntryid(null);
                szhEntryCapture.setEntryIndex(captureIndex);
                szhEntryCapture.setEntryDetial(detailCapture);
                szhEntryCapture.setUsername(captureTemp.getUsername());
            }
            for(Tb_data_template template:iTemplateList){
                String fieldCode = template.getFieldcode().substring(0,1).toUpperCase()+template.getFieldcode().substring(1);
                String than1 = getFieldVal(template.getFieldcode(),szhEntryCapture);//比对字符串
                for(SzhEntryCapture captures:entryList){
                    if(captures.getEntryIndex().getUserid().equals(szhEntryCapture.getEntryIndex().getUserid())){
                        continue;
                    }
                    String than2 = getFieldVal(template.getFieldcode(),captures);//比对字符串
                    if((than1==null||"".equals(than1))&&(than2!=null&&!"".equals(than2))){
                        PropertyDescriptor pd = new PropertyDescriptor(template.getFieldcode(), szhEntryCapture.getClass());
                        Method setMethod = pd.getWriteMethod();
                        setMethod.invoke(szhEntryCapture,than2);//重新赋值
                        break;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return szhEntryCapture;
    }

    public String getFieldVal(String fieldName,SzhEntryCapture szhEntryCapture){
        String result = null;
        try {
            fieldName = fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
            if(fieldName.startsWith("F")&&fieldName.length()==3){
                Class<Szh_entry_detail_capture> cls = Szh_entry_detail_capture.class;
                Method me = cls.getDeclaredMethod("get"+fieldName);
                result = (String)me.invoke(szhEntryCapture.getEntryDetail());
            }else{
                Class<Szh_entry_index_capture> cls = Szh_entry_index_capture.class;
                Method me = cls.getDeclaredMethod("get"+fieldName);
                result = (String)me.invoke(szhEntryCapture.getEntryIndex());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public List<String> getEntryidNodeid(String calloutId){
        List<String> list = new ArrayList<>();
        String entryid =  szhCalloutCaptureRepository.findEntryid(calloutId);
        Szh_entry_index_capture entryCapture = shzEntryIndexCaptureRepository.findByEntryid(entryid);
        List<Tb_entry_index_capture> captures = entryIndexCaptureRepository.findByArchivecode(entryCapture.getArchivecode());
        if(captures==null||captures.size()==0){
            if(entryCapture!=null&&entryCapture.getNodeid()!=null){
                list.add(entryCapture.getNodeid());
            }
            if(entryid!=null){
                list.add(entryid);
                List<Szh_electronic_capture> eles =  szhElectronicCaptureRepository.findByEntryid(entryid);
                if(eles!=null&&eles.size()>0){
                    list.add(eles.get(0).getEleid());//获取id
                    list.add(eles.get(0).getFilename());//获取文件名
                }
            }
        }else{
            list.add("已入库");
        }
        return list;
    }

    public SzhEntryCapture getEntryIndex(String entryid){
        SzhEntryCapture capture = new SzhEntryCapture();
        Szh_entry_index_capture szhEntryIndex = shzEntryIndexCaptureRepository.findByEntryid(entryid);
        Szh_entry_detail_capture szhDetailIndex = szhEntryDetailCaptureRepository.findByEntryid(entryid);

            /////////////////////////////档号字段自动补全////////////////////////////////////
            try {
                String archivesCode = szhEntryIndex.getArchivecode();//获取档号
                Map<String,String> fieldMap = new HashMap<>();//字段与值对应Map
                if(archivesCode!=null&&!"".equals(archivesCode)){
                    List<Tb_codeset> codesets = codesetRepository.findByDatanodeidOrderByOrdernum(szhEntryIndex.getNodeid());//获取档号组成字段
                    for(int i=0;i<codesets.size();i++){
                        Tb_codeset codeset = codesets.get(i);
                        int index = archivesCode.indexOf(codeset.getSplitcode());
                        boolean state = i==codesets.size()-1;
                        if(index>-1 || state){
                            String val = state?archivesCode:archivesCode.substring(0,index);//截取匹配分隔符字符
                            fieldMap.put(codeset.getFieldcode(),val);
                            archivesCode = archivesCode.substring(index+1);//获取剩余字符
                        }
                    }
                    if(codesets.size()==fieldMap.size()){//判断截取的档号信息是否完整
                        Set<String> fieldCodes = fieldMap.keySet();
                        for(String fieldCode:fieldCodes){//设置档号字段
                            PropertyDescriptor pd = new PropertyDescriptor(fieldCode, szhEntryIndex.getClass());//获取访问器实例
                            Method setMethod = pd.getWriteMethod();//获取写方法
                            setMethod.invoke(szhEntryIndex,fieldMap.get(fieldCode));//重新赋值
                        }
                    }
                }

                capture.setEntryIndex(szhEntryIndex);
                capture.setEntryDetial(szhDetailIndex);
            }catch (Exception e){
                e.printStackTrace();
            }

            return capture;
    }

    public Map<String, Object> findSzhElectronic(String eleid) {
        Szh_electronic_capture eleCapture = szhElectronicCaptureRepository.findOne(eleid);
        return eleCapture.getMap();
    }

    public Page<SzhEntryCapture> szhEntries(String nodeid,String calloutId, String condition, String operator, String content, int page, int limit) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 系统绑定对象(全局)
        Szh_callout_entry calloutEntry = szhCalloutEntryRepository.findOne(calloutId);//获取档号作为条件
        Specifications sp = null;
        sp = ClassifySearchService.addSearchbarCondition(sp, "archivecode", "equal",calloutEntry!=null?calloutEntry.getArchivecode():"");
        sp = ClassifySearchService.addSearchbarCondition(sp, "userid", "notEqual","*");
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        Page<Szh_entry_index_capture> szhCaptures = shzEntryIndexCaptureRepository.findAll(sp,pageRequest);
        List<Szh_entry_index_capture> szhCaptureLists = szhCaptures.getContent();
        Map<String,Szh_entry_index_capture> idEntryMap = new HashMap<>();//id实例对应Map,优化查询速度
        String[] entryIds = new String[szhCaptureLists.size()];//存放entryid
        String[] userids = new String[szhCaptureLists.size()];//存放userid
        for(int i=0;i<szhCaptureLists.size();i++){
            Szh_entry_index_capture capture = szhCaptureLists.get(i);
            idEntryMap.put(capture.getEntryid(),capture);
            entryIds[i] = capture.getEntryid();
            userids[i] = capture.getUserid();
        }

        List<Szh_entry_detail_capture> detailCaptures = szhEntryDetailCaptureRepository.findByEntryidIn(entryIds);
        List<SzhEntryCapture> entryCaptures = new ArrayList<>();
        Map<String,SzhEntryCapture> idEntryMap1 = new HashMap<>();//用户id数据对应Map
        for(Szh_entry_detail_capture detailCapture:detailCaptures){//组合记录
            SzhEntryCapture entryCapture = new SzhEntryCapture();
            entryCapture.setEntryDetial(detailCapture);
            entryCapture.setEntryIndex(idEntryMap.get(detailCapture.getEntryid()));
            entryCaptures.add(entryCapture);
            idEntryMap1.put(detailCapture.getUserid(),entryCapture);
        }

        List<Tb_user> users = userRepository.findByUseridIn(userids);
        for(Tb_user user:users){
            idEntryMap1.get(user.getUserid()).setUsername(user.getRealname());
        }
        return new PageImpl<SzhEntryCapture>(entryCaptures, pageRequest, szhCaptures.getTotalElements());
    }

    public List getOperateUsers(String nodeid,String calloutId){
        Page<SzhEntryCapture> page = szhEntries(null,calloutId,null,null,null,1,100);
        return page.getContent();
    }

    public List<Tb_data_template> gridHeader(String nodeid,String calloutId){
        Page<SzhEntryCapture> page = szhEntries(null,calloutId,null,null,null,1,100);
        List<SzhEntryCapture> entryList = page.getContent();//获取多用户录入数据
        List<Tb_data_template> iTemplateList = templateRepository.findByNodeid(nodeid);//获取模板
        List<Tb_data_template> templateList = new ArrayList<>();
        try {
            for(Tb_data_template template:iTemplateList){
                String fieldCode = template.getFieldcode().substring(0,1).toUpperCase()+template.getFieldcode().substring(1);
                String than1 = null;//比对字符串
                for(SzhEntryCapture captures:entryList){
                    String than2 = null;
                    if(fieldCode.startsWith("F")&&fieldCode.length()==3){
                        Class<Szh_entry_detail_capture> cls = Szh_entry_detail_capture.class;
                        Method me = cls.getDeclaredMethod("get"+fieldCode);
                        than2 = (String)me.invoke(captures.getEntryDetail());
                    }else{
                        Class<Szh_entry_index_capture> cls = Szh_entry_index_capture.class;
                        Method me = cls.getDeclaredMethod("get"+fieldCode);
                        than2 = (String)me.invoke(captures.getEntryIndex());
                    }

                    if(than1!=null){
                        if(than2.equals(than1)){
                            continue;
                        }else{
                            templateList.add(template);
                            break;
                        }
                    }else{
                        than1 = than2;
                    }
                }
            }

            Tb_data_template template = new Tb_data_template();
            template.setFieldcode("username");
            template.setFieldname("操作人");
            templateList.add(template);
        }catch (Exception e){
            e.printStackTrace();
        }
        return templateList;
    }

    public boolean hasEntryCapture(String archivecode,String nodeid){
        try {
            Szh_entry_index_capture capture =  shzEntryIndexCaptureRepository.findByArchivecodeAndNodeidAndUserid(archivecode,nodeid,"#");
            if(capture!=null){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean mergeEntries(SzhEntryCapture entry,String nodeid){
        boolean state = false;
        String entryid = entry.getEntryIndex().getEntryid();//获取合并后条目主键
        try{
            Szh_entry_index_capture captureIndex = shzEntryIndexCaptureRepository.findByEntryid(entryid);
            Szh_entry_detail_capture detailIndex = szhEntryDetailCaptureRepository.findByEntryid(entryid);
            Tb_entry_index_capture tbCapture = new Tb_entry_index_capture();
            BeanUtils.copyProperties(captureIndex,tbCapture);
            tbCapture = entryIndexCaptureRepository.save(tbCapture);//保存基础表数据

            Tb_entry_detail_capture tbDetailCapture = new Tb_entry_detail_capture();
            detailIndex.setEntryid(tbCapture.getEntryid());
            BeanUtils.copyProperties(detailIndex,tbDetailCapture);
            entryDetailCaptureRepository.save(tbDetailCapture);//保存扩展表数据

            Szh_entry_index_capture eleCap = shzEntryIndexCaptureRepository.findByArchivecodeAndNodeidAndUserid(entry.getEntryIndex().getArchivecode(),nodeid,"*");
            List<Szh_electronic_capture> electronicCaptures = szhElectronicCaptureRepository.findByEntryid(eleCap.getEntryid());

            List<Tb_electronic_capture> tbElectronicCaptures = new ArrayList<>();
            for(Szh_electronic_capture capture:electronicCaptures){
                Tb_electronic_capture tbElectronicCapture = new Tb_electronic_capture();
                BeanUtils.copyProperties(capture,tbElectronicCapture);
                tbElectronicCaptures.add(tbElectronicCapture);
            }
            tbElectronicCaptures = electronicCaptureRepository.save(tbElectronicCaptures);//保存电子文件数据
            for(Tb_electronic_capture ec:tbElectronicCaptures){//设置新条目与原文对应关系
                ec.setEntryid(tbCapture.getEntryid());
            }
            captureIndex.setEleid(tbElectronicCaptures.size()+"");//设置条目电子原文数目

            //删除缓存数据(暂缓...)

            state = true;
        }catch (Exception e){
            e.printStackTrace();
        }finally {//删除合并后的条目
            shzEntryIndexCaptureRepository.delete(entryid);
            entryDetailCaptureRepository.delete(entryid);
        }
        return state;
    }


    //删除reportdata表的数据
    public void delectData(){
        reportDataRepository.delectData();
    }


    //重新插入数据
    public void reportData(String[] entryids){
        for(int i = 0 ; i < entryids.length ;i++) {
            List<ReportData> allreportList =  reportDataRepository.findReportData(entryids[i]);
            for(ReportData reportData: allreportList){
                List <ReportData> reportList = reportDataRepository.findByPagenameAndEntryid(reportData.getPagename(),reportData.getEntryid());
                if(reportList.size() == 0) {
                    int a= reportDataRepository.insertFristData(reportData.getId(),reportData.getEntryid(),reportData.getPagename(),reportData.getCount(),reportData.getFilepahenum(),reportData.getBz(),reportData.getF49(),reportData.getF50(),reportData.getBiscopyed(),reportData.getCzjd());
                }
                else if(reportList.size() != 0){
                    Boolean state = false;
                    for(ReportData reportExist:reportList) {
                        // 页次相邻的相同的文件名名称合并
                        String [] filepahenumlist = reportExist.getFilepahenum().split(",");
                        for(int j =0; j< filepahenumlist.length;j++) {
                            if (Math.abs(Integer.parseInt(filepahenumlist[j]) - Integer.parseInt(reportData.getFilepahenum())) == 1) {
                                reportExist.setFilepahenum(reportExist.getFilepahenum() + "," + reportData.getFilepahenum());
                                //获取最大的值的件数
                                if(!reportData.getCount().equals("") && !reportExist.getCount().equals("")) {
                                    if (Integer.parseInt(reportData.getCount()) > Integer.parseInt(reportExist.getCount())) {
                                        reportExist.setCount(reportData.getCount());
                                    }
                                }
                                state = true;
                                break;
                            }
                        }

                    }
                    //页次不相邻的插入
                    if(state == false){
                        reportDataRepository.insertFristData(reportData.getId(),reportData.getEntryid(),reportData.getPagename(),reportData.getCount(),reportData.getFilepahenum(),reportData.getBz(),reportData.getF49(),reportData.getF50(),reportData.getBiscopyed(),reportData.getCzjd());
                    }
                }
            }
        }
        List<ReportData> allreportListPX = reportDataRepository.findAll();
        // 页次排序并规则化（最小页次  -  最大页次）
        for(ReportData reportDataPX:allreportListPX){
            String filenumNew = new String();
            String[] reportDataS = reportDataPX.getFilepahenum().split(",");
            int [] reportDataI = new int[reportDataS.length];
            for(int i =0 ; i<reportDataS.length; i++){
                reportDataI[i] = Integer.parseInt(reportDataS[i]);
            }
            Arrays.sort(reportDataI);
            filenumNew = filenumNew + reportDataI[0];
            if(reportDataS.length > 1){
                filenumNew = filenumNew + "-" + reportDataI[reportDataS.length - 1];
            }
            reportDataPX.setFilepahenum(filenumNew);
        }
    }

    /**
     *获得条目日志表相关信息
     */
    public List findSzhFlowsRecordMessage(String calloutId){
        List messages=new ArrayList();
        //数字化加工环节日志
        List<Szh_flows_record> sfrs=szhFlowsRecordRepository.findByCalloutid(calloutId);
        //实体流转日志
        List<Szh_entry_track> sets=szhEntryTrackRepository.findByEntryidGroupByStatus(calloutId);
        //退回日志
        List<Szh_link_back> slbs=szhLinkBackRepository.findByEnriyid(calloutId);
        messages.add(sets);
        messages.add(sfrs);
        messages.add(slbs);
        return messages;
    }

    /**
     *检查签收条目是否已经签收
     * @param ids 条目数组
     * @param node 节点
     * @param status 状态
     * @param isSign 操作类型(签收OR退回)
     * @return 已处理文件名
     */
    public String checkSign(String[] ids, String node, String status,boolean isSign) throws Exception{
        List<Szh_callout_entry> entries = szhCalloutEntryRepository.findByIdIn(ids);
        List<String> statusList = new ArrayList<>();//存在的状态类型
        if(isSign){//签收
            if("未签收".equals(status)){
                statusList =  Arrays.asList("已签收","已处理");
            }else{
                statusList =  Arrays.asList("未签收","已处理");
            }
        }else{//退回
            if("已处理".equals(status)){
                statusList =  Arrays.asList("未签收","已签收");
            }else{
                statusList =  Arrays.asList("未签收","已处理");
            }
        }

        String result = "";//返回信息
        for(Szh_callout_entry entry:entries){
            String srcStatus = entry.getTidy();
            switch (node) {
                case "扫描":
                    srcStatus = entry.getScan();
                    break;
                case "图像处理":
                    srcStatus = entry.getPictureprocess();
                    break;
                case "属性定义":
                    srcStatus = entry.getDefinition();
                    break;
                case "审核":
                    srcStatus = entry.getAudit();
                    break;
                case "装订":
                    srcStatus = entry.getBind();
                    break;
                case "著录":
                    srcStatus = entry.getRecord();
                    break;
            }
            if(statusList.contains(srcStatus)){//判断当前条目是否处于待修改状态
                result += ","+entry.getArchivecode();
            }
        }
        return "".equals(result)?null:result.substring(1);
    }

    public Szh_electronic_capture getElectronic(String eleid) {
        return szhElectronicCaptureRepository.findByEleid(eleid);
    }

    public String getCalloutid(String entryId){
        return  szhCalloutCaptureRepository.findCallout(entryId);
    }

    public List<Szh_assembly_flows> getLinkByassembly(String assemblyid){
        return szhAssemblyFlowsRepository.getFlowsByassemblyid(assemblyid);
    }

    public String getRelevancyLinks(String assemblyid,String linkId) {
        Set<String> flowsIdSet = new HashSet<>();
        flowsIdSet.add(linkId);
        getFlowsIdList(flowsIdSet,assemblyid,linkId);
        String[] linkidArr = flowsIdSet.toArray(new String[flowsIdSet.size()]);
        List<Szh_assembly_flows> flowsList = szhAssemblyFlowsRepository.findByIdInOrderBySorting(linkidArr);
        String linkConcat = "";
        for(Szh_assembly_flows flows:flowsList){
            linkConcat += "-"+flows.getNodename();
        }

        if(!"".equals(linkConcat)){
            linkConcat = linkConcat.substring(1);
        }
        if(!linkConcat.contains("审核"))
        {
            linkConcat += "-"+ "审核";
        }
        return linkConcat;
    }

    public Set<String> getFlowsIdList(Set<String> flowsIdSet,String assemblyid,String linkid){
        List<Szh_assembly_preflow> preflows = szhAssemblyPreflowRepository.findByAssemblyidAndPreflowid(assemblyid,linkid);
        if(preflows.size()>0){
            for(Szh_assembly_preflow preflow:preflows){
                flowsIdSet.add(preflow.getAssemblyflowid());
                getFlowsIdList(flowsIdSet,assemblyid,preflow.getAssemblyflowid());
            }
        }else{
            flowsIdSet.add(linkid);
        }
        return flowsIdSet;
    }

    public boolean linkback(String relateLink,String linkName,String assemblyid,String ids[],Szh_link_back linkBack,String backText){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 系统绑定对象(全局)
        Set<String> flowsIdSet = new HashSet<>();
        flowsIdSet.add(linkBack.getLink());
        getFlowsIdList(flowsIdSet,assemblyid,linkBack.getLink());
        String[] linkidArr = flowsIdSet.toArray(new String[flowsIdSet.size()]);
        List<Szh_assembly_flows> flowsList = szhAssemblyFlowsRepository.findByIdInOrderBySorting(linkidArr);
        Szh_assembly_flows f = new Szh_assembly_flows();
        f.setNodename(backText==null?"":backText);
        flowsList.add(f);//添加审核环节
        List<Szh_callout_entry> entries = szhCalloutEntryRepository.findByIdIn(ids);
        //String status = "审核退回";
        String status = "审核".equals(backText)?"审核退回":"完成退回";
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        List<Szh_link_back> linkBacks = new ArrayList<>();
        List<Szh_flows_record> flowsRecords = new ArrayList<>();
        String[] linkNameArr = new String[flowsList.size()];
        relateLink = relateLink.replace("关联环节：","");
        for(int i=0;i<entries.size();i++){
            Szh_callout_entry entry = entries.get(i);
            if(entry.getWorkstate().equals("加工完成")) {
                entry.setWorkstate("加工中");
            }
            if(entry.getWorkstate().equals("(质检退回)加工完成")){
                flowsRecords.add(new Szh_flows_record(entry.getArchivecode(), entry.getBatchcode(), entry.getId(), "完成环节", userDetails.getLoginname(), time, "((质检退回)加工中)"));
                entry.setWorkstate("(质检退回)加工中");
            }
            boolean hasSh = false;
            for(int j=0;j<flowsList.size();j++){
                Szh_assembly_flows flows = flowsList.get(j);
                if(i==0){
                    linkNameArr[j] = flows.getNodename();
                }
                if(hasSh){//排除多次审核的情况
                    continue;
                }
                switch (flows.getNodename()) {
                    case "整理":
                        entry.setTidy(status);
                        break;
                    case "扫描":
                        entry.setScan(status);
                        break;
                    case "图像处理":
                        entry.setPictureprocess(status);
                        break;
                    case "属性定义":
                        entry.setDefinition(status);
                        break;
                    case "审核":
                        entry.setAudit(status);
                        hasSh = true;
                        break;
                    case "装订":
                        entry.setBind(status);
                        break;
                    case "著录":
                        entry.setRecord(status);
                        break;
                    default:
                        System.out.println("**");
                }
                if("完成环节".equals(backText)) {
                    flowsRecords.add(new Szh_flows_record(entry.getArchivecode(), entry.getBatchcode(), entry.getId(), flows.getNodename(), userDetails.getLoginname(), time, "完成退回"));
                }
                if("审核".equals(backText)) {
                    flowsRecords.add(new Szh_flows_record(entry.getArchivecode(), entry.getBatchcode(), entry.getId(), flows.getNodename(), userDetails.getLoginname(), time, "审核退回"));
                }
            }
            linkBacks.add(new Szh_link_back("完成".equals(backText)?backText:linkName, entry.getId(), userDetails.getRealname(),userDetails.getLoginname(), time, "【"+relateLink+"】"+linkBack.getDepict(),"退回"));
        }
        szhLinkSignRepository.deleteByCalloutidInAndLinkIn(ids,linkNameArr);//清除签收信息
        szhFlowsRecordRepository.save(flowsRecords);//保存签收退回信息
        szhLinkBackRepository.save(linkBacks);//保存环节退回信息
        return true;
    }

    public List<Szh_callout_entry> getcalloutEntrys(String[] ids){
        return szhCalloutEntryRepository.findByIdInAndAudit(ids,"已签收");
    }

    public List getSzhEleCaptures(String entryid){
        return szhElectronicCaptureRepository.findByEntryid(entryid);
    }

    public List getAuditStatuss(String[] mediaArr){
        return szhAuditStatusRepository.findByMediaidIn(mediaArr);
    }
}
