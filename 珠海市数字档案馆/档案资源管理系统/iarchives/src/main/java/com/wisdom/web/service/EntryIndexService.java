package com.wisdom.web.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wisdom.secondaryDataSource.entity.Tb_data_node_sx;
import com.wisdom.secondaryDataSource.entity.Tb_entry_index_sx;
import com.wisdom.secondaryDataSource.repository.SxDataNodeRepository;
import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.*;
import com.wisdom.web.controller.ClassifySearchController;
import com.wisdom.web.controller.ClassifySearchDirectoryController;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.xdtech.component.storeroom.entity.InWare;
import com.xdtech.component.storeroom.entity.Storage;
import com.xdtech.component.storeroom.repository.StorageRepository;
import com.xdtech.component.storeroom.service.InWareService;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.hibernate.jpa.HibernateEntityManager;
import org.slf4j.LoggerFactory;
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
import sun.misc.BASE64Decoder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.io.IOException;
import java.io.*;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static com.wisdom.web.service.ThematicService.delFolder;

/**
 * Created by yl on 2017/10/26. 条目管理索引service
 */
@Service
@Transactional
public class EntryIndexService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    public static String dir = "";

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录

    @Value("${webservice.filearchive.receive.nodeid}")
    private String receiveId;//收文ID

    @Value("${webservice.filearchive.send.nodeid}")
    private String sendId;//发文ID

    @Value("${webservice.filearchive.signReport.nodeid}")
    private String signReportId;//签报ID

    @PersistenceContext
    EntityManager entityManager;

    @PersistenceContext(unitName="entityManagerFactorySecondary")
    EntityManager entityManagerSecondary;

    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    EntryIndexCaptureRepository entryIndexCaptureRepository;

    @Autowired
    CodesettingService codesettingService;

    @Autowired
    CodesetRepository codesetRepository;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    RightOrganRepository rightOrganRepository;

    @Autowired
    PublicUtilService publicUtilService;

    @Autowired
    EntryDetailRepository entryDetailRepository;

    @Autowired
    EntryDetailCaptureRepository entryDetailCaptureRepository;

    @Autowired
    ElectronicRepository electronicRepository;

    @Autowired
    ElectronicCaptureRepository electronicCaptureRepository;

    @Autowired
    EntryIndexTempRepository entryIndexTempRepository;

    @Autowired
    EntryIndexSqTempRepository entryIndexSqTempRepository;

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    OrganService organService;

    @Autowired
    FundsService fundsService;

    @Autowired
    EntryCaptureService entryCaptureService;

    @Autowired
    SystemConfigRepository systemConfigRepository;

    @Autowired
    ClassifySearchService classifySearchService;

    @Autowired
    ElectronicService electronicService;

    @Autowired
    ElectronicVersionRepository electronicVersionRepository;

    @Autowired
    EntryBookmarksRepository bookmarksRepository;

    @Autowired
    SimpleSearchService simpleSearchService;

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    InWareService inWareService;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TransdocRepository transdocRepository;

    @Autowired
    OrdersetRepository ordersetRepository;

    @Autowired
    BatchModifyService batchModifyService;


    @Autowired
    EntryIndexCaptureService entryIndexCaptureService;

    @Autowired
    ClassifySearchController classifySearchController;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    UserNodeSortRepository userNodeSortRepository;

    @Autowired
    ElectronicVersionCaptureRepository electronicVersionCaptureRepository;

    @Autowired
    EntryService entryService;

    @Autowired
    BackCapturedocRepository backCapturedocRepository;

    @Autowired
    BackCapturedocEntryRepository backCapturedocEntryRepository;

    @Autowired
    AuditService auditService;

    @Autowired
    SimpleSearchDirectoryService simpleSearchDirectoryService;

    @Autowired
    ClassifySearchDirectoryController classifySearchDirectoryController;

    @Autowired
    ThumbnailRepository thumbnailRepository;

    @Autowired
    SxDataNodeRepository sxDataNodeRepository;

    public Page<Tb_index_detail> getEntries(String nodeid, String condition, String operator, String content,
                                            Tb_index_detail formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,
                                            boolean ifSearchLeafNode, boolean ifContainSelfNode, int page, int limit, Sort sort) {
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        String nodeidStr = " and sid.nodeid='" + nodeid + "' ";
        if (ifSearchLeafNode) {//点击非叶子节点时，查询出其包含的所有叶子节点数据
            List<String> nodeidList = nodesettingService.getNodeidLoop(nodeid, ifContainSelfNode, new ArrayList<String>());
            if (nodeidList.size() > 0) {
                nodeidStr = " and sid.nodeid in('" + String.join("','", nodeidList) + "') ";
            }
        }

        String sortStr = "";//排序
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        List<Tb_user_node_sort> userNodeSorts = userNodeSortRepository.findByNodeidAndUseridOrderBySortsequence(nodeid,userDetails.getUserid());
        int sortInt = 0;//判断是否副表表排序
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            if ("eleid".equals(order.getProperty())) {
                sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
            } else {
                sortStr = " order by " + order.getProperty() + " " + order.getDirection();
            }
            sortInt = checkFilecode(order.getProperty());
        } else {
            if(userNodeSorts.size()>0){  //设置了节点排序
                for(int i=0;i<userNodeSorts.size();i++){
                    if(i==0){
                        sortStr = " order by " + userNodeSorts.get(i).getFieldcode()+ " "+ userNodeSorts.get(i).getSorttype();
                    }else {
                        sortStr = sortStr + "," + userNodeSorts.get(i).getFieldcode()+ " "+ userNodeSorts.get(i).getSorttype();
                    }
                    sortInt = checkFilecode(userNodeSorts.get(i).getFieldcode());
                }
            }else {
                sortStr = " order by archivecode desc, descriptiondate desc ";
            }
        }

        String searchCondition = "";
        if (content != null && !"".equals(content)) {//输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        String dataStr = "";
        if (daterangedata.getFiledateendday() != null && daterangedata.getFiledatestartday() != null) {//检索条件为日期范围
            dataStr = classifySearchService.getDateRangeCondition(daterangedata);
        }
        String formAdvancedSearch = "";
        String tempStr="";
        if("预归档未归".equals(logic)){//"预归档未归页面检索
            String uniqueTag=BatchModifyService.getUniquetagByType("glgd");
            tempStr=" and entryid not in(select entryid from tb_entry_index_temp where uniquetag='"+uniqueTag+"')";
        }else if (logic != null) {//高级检索的表单检索
            formAdvancedSearch = classifySearchService.getFormAdvancedIndexDetailSearch(formConditions, formOperators, logic);
        }
        String formDetail = "";//标记表单检索是否有副表字段
        if (!"".equals(formAdvancedSearch)) {
            formDetail = formAdvancedSearch.substring(0, 1);
            formAdvancedSearch = formAdvancedSearch.substring(1);
        }
        String table = "v_index_detail";
        String countTable = "v_index_detail";
        if ((condition == null || checkFilecode(condition) == 0) && ("".equals(formDetail) || "0".equals(formDetail))) {//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
            countTable = "tb_entry_index";
            if (sortInt == 0) {//非副表表字段排序
                table = "tb_entry_index";
            }
        }
        String sql = "select sid.entryid from " + table + " sid where 1=1 " + searchCondition + formAdvancedSearch + dataStr + nodeidStr+tempStr;
        String countSql = "select count(nodeid) from " + countTable + " sid where 1=1 " + searchCondition + formAdvancedSearch + dataStr + nodeidStr+tempStr;
        return getPageListTwo(sql, sortStr, countSql, page, limit, pageRequest);
    }

    //Tb_index_detail sql原生语句分页查询
    public Page<Tb_index_detail> getPageList(String sql, String countSql, int page, int limit, PageRequest pageRequest) {
        Query query = entityManager.createNativeQuery(sql, Tb_index_detail.class);
        query.setFirstResult((page - 1) * limit);
        query.setMaxResults(limit);
        List<Tb_index_detail> resultList = query.getResultList();
        Query couuntQuery = entityManager.createNativeQuery(countSql);
        int count = Integer.parseInt(couuntQuery.getResultList().get(0) + "");
        return new PageImpl(resultList, pageRequest, count);
    }

    //Tb_index_detail sql原生语句分页查询
    public Page<Tb_index_detail> getPageListTwo(String sql, String sortStr, String countSql, int page, int limit, PageRequest pageRequest) {
        Query couuntQuery = entityManager.createNativeQuery(countSql);
        System.out.println("分页统计数量开始:"+new Date());
        int count = Integer.parseInt(couuntQuery.getResultList().get(0) + "");
        System.out.println("分页统计数量结束:"+new Date());
        List<Tb_index_detail> resultList;
        if (count > 1000||"sqlserver".equals(DBCompatible.getDBVersion())) {
            sql = "select tt.* from v_index_detail tt  right join (" + DBCompatible.getInstance().sqlPages(sql+sortStr, page - 1, limit) + ")t on t.entryid = tt.entryid "+sortStr;
            Query query = entityManager.createNativeQuery(sql, Tb_index_detail.class);
            resultList = query.getResultList();
        } else {
            sql = "select tt.* from v_index_detail tt  right join (" + sql + ")t on t.entryid = tt.entryid " + sortStr;
            Query query = entityManager.createNativeQuery(sql, Tb_index_detail.class);
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            resultList = query.getResultList();
        }
        System.out.println("分页条目查询结束:"+new Date());
        return new PageImpl(resultList, pageRequest, count);
    }

    //Tb_index_detail sql原生语句选择数据源分页查询
    public Page<Tb_index_detail> getPageListTwo(String sql, String sortStr, String countSql, int page, int limit, PageRequest pageRequest,String dataSoure) {
        Query couuntQuery = entityManager.createNativeQuery(countSql);
        int count = Integer.parseInt(couuntQuery.getResultList().get(0) + "");
        List<Tb_index_detail> resultList;
        if (count > 1000||"sqlserver".equals(DBCompatible.getDBVersion())) {
            sql = "select tt.* from "+dataSoure+" tt  right join (" + DBCompatible.getInstance().sqlPages(sql+sortStr, page - 1, limit) + ")t on t.entryid = tt.entryid "+sortStr;
            Query query = entityManager.createNativeQuery(sql, Tb_index_detail.class);
            resultList = query.getResultList();
        } else {
            sql = "select tt.* from "+dataSoure+" tt  right join (" + sql + ")t on t.entryid = tt.entryid " + sortStr;
            Query query = entityManager.createNativeQuery(sql, Tb_index_detail.class);
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            resultList = query.getResultList();
        }
        System.out.println("分页条目查询结束:"+new Date());
        return new PageImpl(resultList, pageRequest, count);
    }

    //Tb_index_detail_manage sql原生语句分页查询
    public Page<Tb_index_detail_manage> getPageListDetailManage(String sql, String sortStr, int page, int limit, PageRequest pageRequest,String countSql) {
        Query couuntQuery = entityManager.createNativeQuery(countSql);
        int count = Integer.parseInt(couuntQuery.getResultList().get(0) + "");
        List<Tb_index_detail_manage> resultList=new ArrayList<>();
        if (count > 1000 ||"sqlserver".equals(DBCompatible.getDBVersion())) {
            sql = "select tt.* from v_index_detail_manage tt  inner join (" + DBCompatible.getInstance().sqlPages(sql+sortStr, page-1, limit) + ")t on t.entryid = tt.entryid";
            Query query = entityManager.createNativeQuery(sql, Tb_index_detail_manage.class);
            resultList = query.getResultList();
        } else {
            sql = "select tt.* from v_index_detail_manage tt  inner join (" + sql + " )t on t.entryid = tt.entryid " + sortStr;
            Query query = entityManager.createNativeQuery(sql, Tb_index_detail_manage.class);
            query.setFirstResult((page-1) * limit);
            query.setMaxResults(limit);
            resultList = query.getResultList();
        }
        return new PageImpl(resultList, pageRequest, count);
    }


    //tb_entry_index 声像库 sql原生语句分页查询
    public Page<Tb_entry_index_sx> getPageListEntry(String sql , String sortStr, String countSql, int page, int limit, PageRequest pageRequest,boolean isGetCount) {
        Query couuntQuery = entityManagerSecondary.createNativeQuery(countSql+" and sid.flagopen in('公开')");
        int count = Integer.parseInt(couuntQuery.getResultList().get(0) + "");
        List<Tb_entry_index_sx> resultList = new ArrayList<>();
        if(!isGetCount){
            if (count > 1000 ||"sqlserver".equals(DBCompatible.getDBVersion())) {
                sql = "select tt.* from v_index_detail tt  inner join (" +
                        DBCompatible.getInstance().sqlPages(sql+" and sid.flagopen in('公开') " +sortStr, page - 1 , limit) + " )t on t.entryid = tt.entryid ";
                Query query = entityManagerSecondary.createNativeQuery(sql, Tb_entry_index_sx.class);
                resultList = query.getResultList();
            } else {
                sql = "select tt.* from v_index_detail tt  inner join (" + sql + " and sid.flagopen in('公开'))t on t.entryid = tt.entryid " +sortStr;
                Query query = entityManagerSecondary.createNativeQuery(sql, Tb_entry_index_sx.class);
                query.setFirstResult((page - 1) * limit);
                query.setMaxResults(limit);
                resultList = query.getResultList();
            }
        }
        return new PageImpl<Tb_entry_index_sx>(resultList, pageRequest, count);
    }


    public int checkFilecode(String condition) {
        String fc_pattern = "^[f][0-5][0-9]";//副表字段
        String[] conditions = condition.split(",");
        for (int i = 0; i < conditions.length; i++) {
            if (conditions[i].matches(fc_pattern)) {
                return 1;
            }
        }
        return 0;
    }

    public Page<Tb_index_detail> getEntrybase(String nodeid, String condition, String operator, String content,
                                              Tb_index_detail formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,
                                              boolean ifSearchLeafNode, boolean ifContainSelfNode, int page, int limit, Sort sort,String datasoure) {
        String nodeidStr = " and sid.nodeid='" + nodeid + "' ";
        if (ifSearchLeafNode) {//点击非叶子节点时，查询出其包含的所有叶子节点数据
            List<String> nodeidList = nodesettingService.getNodeidLoop(nodeid, ifContainSelfNode, new ArrayList<String>());
            if (nodeidList.size() > 0) {
                nodeidStr = " and sid.nodeid in('" + String.join("','", nodeidList) + "') ";
            }
        }

        //高级检索用
        /*Specification<Tb_entry_index> formAdvancedSearch = ClassifySearchService.getFormAdvancedIndexSearch(formConditions,formOperators,logic);
        Specification<Tb_entry_index> dateRangeCondition = ClassifySearchService.getDateRangeIndexCondition(daterangedata);*/
        String dataStr = ClassifySearchService.getDateRangeCondition(daterangedata);
        String formStr = ClassifySearchService.getFormAdvancedSearch(formConditions, formOperators, logic,null,null,"management");
        String formDetail = "";//标记表单检索是否有副表字段
        if (!"".equals(formStr)) {
            formDetail = formStr.substring(0, 1);
            formStr = formStr.substring(1);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        String sortStr = "";//排序
        int sortInt = 0;//判断是否副表表排序
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            if("eleid".equals(order.getProperty())){
                sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
            }else {
                sortStr = " order by " + order.getProperty() + " " + order.getDirection();
            }
            sortInt = checkFilecode(order.getProperty());
        } else {
            sortStr = " order by archivecode desc, descriptiondate desc ";
        }
        String searchCondition = "";
        if (content != null && !"".equals(content.trim())) {//输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }

        String table = "v_index_detail";
        String countTable = "v_index_detail";
        if("capture".equals(datasoure)){
            datasoure="v_index_detail_capture";
            table="v_index_detail_capture";
            countTable="v_index_detail_capture";
            if ((condition == null || checkFilecode(condition) == 0) && ("".equals(formDetail) || "0".equals(formDetail))) {//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
                countTable = "tb_entry_index_capture";
                if (sortInt == 0) {//非副表表字段排序
                    table = "tb_entry_index_capture";
                }
            }
        }else {
            datasoure="v_index_detail";
            if ((condition == null || checkFilecode(condition) == 0) && ("".equals(formDetail) || "0".equals(formDetail))) {//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
                countTable = "tb_entry_index";
                if (sortInt == 0) {//非副表表字段排序
                    table = "tb_entry_index";
                }
            }
        }
        String sql = "select sid.entryid from " + table + " sid where 1=1 " + nodeidStr + searchCondition + dataStr + formStr;
        String countSql = "select count(nodeid) from " + countTable + " sid where 1=1 " + nodeidStr + searchCondition + dataStr + formStr;
        return getPageListTwo(sql, sortStr, countSql, page, limit, pageRequest,datasoure);
    }

    public Page<Tb_index_detail> getEntrybaseto(String[] nodeids, String condition, String operator, String content,
                                                int page, int limit, Sort sort) {
        String nodeidStr = "";
        if (nodeids.length > 0) {
            nodeidStr = " and nodeid in ('" + String.join("','", nodeids) + "') ";
        }

        PageRequest pageRequest = new PageRequest(page - 1, limit);
        String sortStr = "";//排序
        int sortInt = 0;//判断是否副表表排序
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            if("eleid".equals(order.getProperty())){
                sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
            }else {
                sortStr = " order by " + order.getProperty() + " " + order.getDirection();
            }
            sortInt = checkFilecode(order.getProperty());
        } else {
            sortStr = " order by archivecode desc, descriptiondate desc ";
        }

        String searchCondition = "";
        if (content != null) {// 输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }

        String table = "v_index_detail";
        String countTable = "v_index_detail";
        if (condition == null || checkFilecode(condition) == 0) {//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
            countTable = "tb_entry_index";
            if (sortInt == 0) {//非副表表字段排序
                table = "tb_entry_index";
            }
        }
        String openStr = simpleSearchService.getSearchOpenStr("原文开放,条目开放");//利用平台-权限档案显示开放的档案数据
        String sql = "select sid.entryid from " + table + " sid where 1=1 " + nodeidStr + searchCondition + openStr;
        String countSql = "select count(nodeid) from " + countTable + " sid where 1=1 " + nodeidStr + searchCondition+ openStr;
        return getPageListTwo(sql, sortStr, countSql, page, limit, pageRequest);
    }

    public List<Tb_index_detail> getEntrybaseNew(String nodeid, String condition, String operator, String content,String datasoure,
                                                 Tb_index_detail formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,
                                                 boolean ifSearchLeafNode, boolean ifContainSelfNode) {
        String nodeidStr = " and sid.nodeid='" + nodeid + "' ";
        if (ifSearchLeafNode) {//点击非叶子节点时，查询出其包含的所有叶子节点数据
            List<String> nodeidList = nodesettingService.getNodeidLoop(nodeid, ifContainSelfNode, new ArrayList<String>());
            if (nodeidList.size() > 0) {
                nodeidStr = " and sid.nodeid in('" + String.join("','", nodeidList) + "') ";
            }
        }

        //高级检索用
        /*Specification<Tb_entry_index> formAdvancedSearch = ClassifySearchService.getFormAdvancedIndexSearch(formConditions,formOperators,logic);
        Specification<Tb_entry_index> dateRangeCondition = ClassifySearchService.getDateRangeIndexCondition(daterangedata);*/
        String dataStr = ClassifySearchService.getDateRangeCondition(daterangedata);
        String formStr = ClassifySearchService.getFormAdvancedSearch(formConditions, formOperators, logic,null,null,"management");
        if (!"".equals(formStr)) {
            formStr = formStr.substring(1);
        }
        String searchCondition = "";
        if (content != null && !"".equals(content.trim())) {//输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        List<Tb_index_detail> resultList =new ArrayList<>();
        if("capture".equals(datasoure)){
            String sql = "select sid.* from v_index_detail_capture sid where 1=1 " + nodeidStr + searchCondition + dataStr + formStr;
            Query query = entityManager.createNativeQuery(sql, Tb_index_detail.class);
            resultList = query.getResultList();
        }else{
            String sql = "select sid.* from v_index_detail sid where 1=1 " + nodeidStr + searchCondition + dataStr + formStr;
            Query query = entityManager.createNativeQuery(sql, Tb_index_detail.class);
            resultList = query.getResultList();
        }
        return resultList;
    }

    public Page<Tb_entry_index_sqtemp> getSqtempEntryIndex(String entryids, String dataSource, String nodeid, int page,
                                                           int limit, Sort sort) {
        List<String> entryidList = new ArrayList<>();
        if (entryids != null && !entryids.equals("")) {
            for (int i = 0; i < entryids.split("∪").length; i++) {
                entryidList.add(entryids.split("∪")[i]);
            }
        }
        List<String> codeSet = codesetRepository.findFieldcodeByDatanodeid(nodeid);
        String calvalue = "";
        if (codeSet.size() > 0) {
        	calvalue = codeSet.get(codeSet.size() - 1);
        }
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(new Sort.Order(Sort.Direction.ASC, "archivecode"));// 原档号升序
        if ("capture".equals(dataSource)) {// 初次加载调序列表时
            List<Tb_entry_index> result = entryIndexRepository.findByEntryidIn(entryids.split("∪"));
            List<Tb_entry_index_sqtemp> tempList = new ArrayList<>();
            String uniquetag=BatchModifyService.getUniquetag();
            for (Tb_entry_index entry_index : result) {
                Tb_entry_index_sqtemp entry_index_sqtemp = new Tb_entry_index_sqtemp();
                //BeanUtils.copyProperties(entry_index, entry_index_sqtemp);
                entry_index_sqtemp.setArchivecode(entry_index.getArchivecode());
                entry_index_sqtemp.setEntryid(entry_index.getEntryid());
                entry_index_sqtemp.setNodeid(entry_index.getNodeid());
                entry_index_sqtemp.setPageno(entry_index.getPageno());
                entry_index_sqtemp.setTitle(entry_index.getTitle());
                GainField.setFieldValueByName("calvalue", entry_index_sqtemp, (String)GainField.getFieldValueByName(calvalue, entry_index));
                entry_index_sqtemp.setNewarchivecode(entry_index.getArchivecode());// 未作修改时,新档号默认为初始档号
                entry_index_sqtemp.setUniquetag(uniquetag);
                tempList.add(entry_index_sqtemp);
            }
            entryIndexSqTempRepository.save(tempList);
        } else {
            if (entryids == null || entryids.equals("")) {
                String uniquetag = BatchModifyService.getUniquetag();
                List<String> sqtemps = entryIndexSqTempRepository.findEntryidByNodeidAndUniquetag(nodeid, uniquetag);
                entryidList.addAll(sqtemps);
            }
        }
        return entryIndexSqTempRepository.findByEntryidInAndUniquetag(entryidList, BatchModifyService.getUniquetag(),
                new PageRequest(page - 1, limit, sort == null ? new Sort(sorts) : sort));
    }

    public List<Tb_entry_index> getEntryList(String nodeid, String condition, String operator, String content,
                                             Tb_entry_index formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,
                                             boolean ifSearchLeafNode, boolean ifContainSelfNode) {
        String[] nodeids;
        if (ifSearchLeafNode) {
            List<String> nodeidList = nodesettingService.getNodeidLoop(nodeid, ifContainSelfNode,
                    new ArrayList<String>());
            nodeids = new String[nodeidList.size()];
            nodeidList.toArray(nodeids);
        } else {
            nodeids = new String[]{nodeid};
        }
        Specification<Tb_entry_index> searchNodeidCondition = ClassifySearchService.getSearchNodeidIndex(nodeids);
        Specifications specifications = Specifications.where(searchNodeidCondition);
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        Specification<Tb_entry_index> formAdvancedSearch = ClassifySearchService
                .getFormAdvancedIndexSearch(formConditions, formOperators, logic);
        Specification<Tb_entry_index> dateRangeCondition = ClassifySearchService
                .getDateRangeIndexCondition(daterangedata);
        return entryIndexRepository.findAll(specifications.and(formAdvancedSearch).and(dateRangeCondition),
                new Sort("archivecode"));
    }

    public List<String> getIndexIds(String nodeid, String condition, String operator, String content,String logic,Tb_index_detail formConditions,
                                    ExtOperators formOperators) {
        List<String> list = new ArrayList<>();
        String sql = "";
        String formAdvancedSearch="";
        if (logic != null) {//高级检索的表单检索
            formAdvancedSearch = classifySearchService.getFormAdvancedIndexDetailSearch(formConditions, formOperators, logic);
        }
        if (!"".equals(formAdvancedSearch)) {
            formAdvancedSearch = formAdvancedSearch.substring(1);
        }
        if (content != null&&!"".equals(content)) {
            String contentSql = "";
            String[] conditions = condition.split(",");
            String[] operators = operator.split(",");
            String[] contents = content.split(",");
            for (int i = 0; i < contents.length; i++) {
                contentSql += classifySearchService.getSqlByConditionsto(conditions[i],contents[i],"",operators[i]);
            }
            //创建sql语句
            sql = "select entryid from tb_entry_index where nodeid ='" + nodeid + "'" + contentSql + formAdvancedSearch;
            Query query = entityManager.createNativeQuery(sql);
            list = query.getResultList();
        } else {
            if("sqlserver".equals(DBCompatible.getDBVersion())){
                sql = "select cast(entryid as char(36)) as entryid from tb_entry_index where nodeid ='" + nodeid + "'" + formAdvancedSearch;
            }else {
                sql = "select entryid from tb_entry_index where nodeid ='" + nodeid + "'" + formAdvancedSearch;
            }
            Query query = entityManager.createNativeQuery(sql);
            list = query.getResultList();
        }
        return list;
    }

    public List<Tb_entry_index> getLongEntryIndex(String nodeid, String condition, String operator, String content,String logic,Tb_index_detail formConditions,
                                    ExtOperators formOperators) {
        List<Tb_entry_index> list = new ArrayList<>();
        String sql = "";
        String formAdvancedSearch="";
        if (logic != null) {//高级检索的表单检索
            formAdvancedSearch = classifySearchService.getFormAdvancedIndexDetailSearch(formConditions, formOperators, logic);
        }
        if (!"".equals(formAdvancedSearch)) {
            formAdvancedSearch = formAdvancedSearch.substring(1);
        }
        if (content != null) {
            String contentSql = "";
            String[] conditions = condition.split(",");
            String[] operators = operator.split(",");
            String[] contents = content.split(",");
            for (int i = 0; i < contents.length; i++) {
                contentSql += " and " + operatorContent(conditions[i], operators[i], contents[i]);
            }
            //创建sql语句
            sql = "select * from tb_entry_index where nodeid ='" + nodeid + "'" + contentSql + formAdvancedSearch;
            Query query = entityManager.createNativeQuery(sql,Tb_entry_index.class);
            list = query.getResultList();
        } else {
            if("sqlserver".equals(DBCompatible.getDBVersion())){
                sql = "select * from tb_entry_index where nodeid ='" + nodeid + "'" + formAdvancedSearch;
            }else {
                sql = "select * from tb_entry_index where nodeid ='" + nodeid + "'" + formAdvancedSearch;
            }
            Query query = entityManager.createNativeQuery(sql,Tb_entry_index.class);
            list = query.getResultList();
        }
        return list;
    }

    public List<Tb_entry_index> getLognIndexCapture(String nodeid, String condition, String operator, String content) {
        List<Tb_entry_index> list = new ArrayList<>();
        String sql = "";
        String tbstr = "tb_entry_index_capture";
        String shCondition=" and entryid not in(select entryid from tb_transdoc_entry where status='"+ Tb_transdoc_entry.STATUS_AUDIT +"')";
        shCondition+=" and entryid not in(select entryid from tb_transdoc_preview where nodeid ='"+nodeid+"')";
        if (content != null&&!"".equals(content)) {
            String contentSql = "";
            String[] conditions = condition.split(",");
            String[] operators = operator.split(",");
            String[] contents = content.split(",");
            for (int i = 0; i < contents.length; i++) {
                contentSql += classifySearchService.getSqlByConditionsto(conditions[i],contents[i],"",operators[i]);
            }
            //创建sql语句
            if("sqlserver".equals(DBCompatible.getDBVersion())) {
                sql = "select * from "+ tbstr + " where nodeid ='" + nodeid + "'" + contentSql +shCondition;
            }else {
                    sql = "select * from "+ tbstr + " where nodeid ='" + nodeid + "'" + contentSql+shCondition;
            }
            Query query = entityManager.createNativeQuery(sql,Tb_entry_index.class);
            list = query.getResultList();
        } else {
            if("sqlserver".equals(DBCompatible.getDBVersion())){
                sql = "select * from "+ tbstr + " where nodeid ='" + nodeid + "'"+shCondition;
            }else {
                sql = "select * from "+ tbstr + " where nodeid ='" + nodeid + "'"+shCondition;
            }
            Query query = entityManager.createNativeQuery(sql,Tb_entry_index.class);
            list = query.getResultList();
        }
        return list;
    }

    //根据加入移交中间表查询
    public List<Tb_entry_index> getDocPreviewEntry(String nodeid, String condition, String operator, String content) {
        List<Tb_entry_index> list = new ArrayList<>();
        String sql = "";
        String tbstr = "tb_entry_index_capture";
        String shCondition=" and entryid in(select entryid from tb_transdoc_preview where nodeid ='"+nodeid+"')";
        if (content != null&&!"".equals(content)) {
            String contentSql = "";
            String[] conditions = condition.split(",");
            String[] operators = operator.split(",");
            String[] contents = content.split(",");
            for (int i = 0; i < contents.length; i++) {
                contentSql += classifySearchService.getSqlByConditionsto(conditions[i],contents[i],"",operators[i]);
            }
            //创建sql语句
            if("sqlserver".equals(DBCompatible.getDBVersion())) {
                sql = "select * from "+ tbstr + " where nodeid ='" + nodeid + "'" + contentSql +shCondition;
            }else {
                sql = "select * from "+ tbstr + " where nodeid ='" + nodeid + "'" + contentSql+shCondition;
            }
            Query query = entityManager.createNativeQuery(sql,Tb_entry_index.class);
            list = query.getResultList();
        } else {
            if("sqlserver".equals(DBCompatible.getDBVersion())){
                sql = "select * from "+ tbstr + " where nodeid ='" + nodeid + "'"+shCondition;
            }else {
                sql = "select * from "+ tbstr + " where nodeid ='" + nodeid + "'"+shCondition;
            }
            Query query = entityManager.createNativeQuery(sql,Tb_entry_index.class);
            list = query.getResultList();
        }
        return list;
    }

    public List<String> getIndexCaptureIds(String nodeid, String condition, String operator, String content,String exporttype) {
        List<String> list = new ArrayList<>();
        String sql = "";
        String tbstr = "tb_entry_index_capture";
        if(exporttype!=null&&"accept".equals(exporttype)) { //判断是否目录接收导出
            tbstr = "tb_entry_index_accept";
        }else if(exporttype!=null&&"manage".equals(exporttype)){  //判断是否目录管理导出
            tbstr = "tb_entry_index_manage";
        }
        if (content != null&&!"".equals(content)) {
            String contentSql = "";
            String[] conditions = condition.split(",");
            String[] operators = operator.split(",");
            String[] contents = content.split(",");
            for (int i = 0; i < contents.length; i++) {
                contentSql += classifySearchService.getSqlByConditionsto(conditions[i],contents[i],"",operators[i]);
            }
            //创建sql语句
            if("sqlserver".equals(DBCompatible.getDBVersion())) {
                sql = "select cast(entryid as char(36)) as entryid from "+ tbstr + " where nodeid ='" + nodeid + "'" + contentSql;
            }else {
                sql = "select entryid from "+ tbstr + " where nodeid ='" + nodeid + "'" + contentSql;
            }
            Query query = entityManager.createNativeQuery(sql);
            list = query.getResultList();
        } else {
            if("sqlserver".equals(DBCompatible.getDBVersion())){
                sql = "select cast(entryid as char(36)) as entryid from "+ tbstr + " where nodeid ='" + nodeid + "'";
            }else {
                sql = "select entryid from "+ tbstr + " where nodeid ='" + nodeid + "'";
            }
            Query query = entityManager.createNativeQuery(sql);
            list = query.getResultList();
        }
        return list;
    }

    public List<Tb_entry_index_capture> getEntryCaptureList(String nodeid, String condition, String operator,
                                                            String content, Tb_entry_index_capture formConditions, ExtOperators formOperators,
                                                            ExtDateRangeData daterangedata, String logic, boolean ifSearchLeafNode, boolean ifContainSelfNode) {
        String[] nodeids;
        if (ifSearchLeafNode) {
            List<String> nodeidList = nodesettingService.getNodeidLoop(nodeid, ifContainSelfNode,
                    new ArrayList<String>());
            nodeids = new String[nodeidList.size()];
            nodeidList.toArray(nodeids);
        } else {
            nodeids = new String[]{nodeid};
        }
        Specification<Tb_entry_index_capture> searchNodeidCondition = ClassifySearchService
                .getSearchNodeidIndexCapture(nodeids);
        Specifications specifications = Specifications.where(searchNodeidCondition);
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        Specification<Tb_entry_index_capture> formAdvancedSearch = ClassifySearchService
                .getFormAdvancedIndexCaptureSearch(formConditions, formOperators, logic);
        Specification<Tb_entry_index_capture> dateRangeCondition = ClassifySearchService
                .getDateRangeIndexCaptureCondition(daterangedata);
        return entryIndexRepository.findAll(specifications.and(formAdvancedSearch).and(dateRangeCondition),
                new Sort("archivecode"));
    }

    public Page<Tb_entry_index_temp> getEntryIndex(String[] entryids, String dataSource, int page, int limit, Sort sort,String ygType,String dataNodeid) {
        String uniquetag=BatchModifyService.getUniquetagByType("glgd");
        List<String[]> subAry = new InformService().subArray(entryids, 1000);
        if ("capture".equals(dataSource)&&!"ygd".equals(ygType)) {// 初次加载归档预览列表时
            //addEntriesToTemp(entryids,subAry,ygType,uniquetag,dataNodeid);
        }
        List<Sort.Order> sorts = new ArrayList<>();
        if ("".equals(dataSource)||"del".equals(dataSource)){//排序状态  ,del是取消预归档标记
            sorts.add(new Sort.Order(Sort.Direction.ASC, "sortsequence"));// 归档顺序号升序
        }else {//进入预归档
            sorts=entryIndexCaptureService.getSort(sorts,dataNodeid);
        }

        List<Tb_entry_index_temp> entry_index_temps = new ArrayList<>();
        if("ygd".equals(ygType)){//归档nodeid不变，预归档页面直接查所有个人用户添加的预归档数据
            return entryIndexTempRepository.findByUniquetag(uniquetag,
                    new PageRequest(page - 1, limit, sort == null ? new Sort(sorts) : sort));
        }else if("ygdChange".equals(ygType)){//归档nodeid改变，预归档页面直接查所有个人用户添加的未修改数据和为生成档号前的预归档数据
            List<String> stringList=entryIndexTempRepository.findEntryidByUniquetag(uniquetag);//获取临时表个人数据
            if(stringList.size()>0){//获取到entryids后重新加载采集表数据到临时表
                //List转String
                String[] entries=stringList.toArray(new String[stringList.size()]);
                batchModifyService.deleteEntryIndexTempByUniquetag();//先清空个人数据
                addEntriesToTemp(entries,subAry,ygType,uniquetag,dataNodeid);//重新加载采集表数据
            }
            return entryIndexTempRepository.findByUniquetag(uniquetag,
                    new PageRequest(page - 1, limit, sort == null ? new Sort(sorts) : sort));
        }else{
            for (String[] sub : subAry) {
                entry_index_temps.addAll(entryIndexTempRepository.findByEntryidInAndUniquetag(sub, uniquetag));
            }
        }
        List<Tb_entry_index_temp> ls = new ArrayList();
        ls=entry_index_temps.subList((page-1)*limit,page*limit+limit>entry_index_temps.size()?entry_index_temps.size():page*limit+limit);
        Page<Tb_entry_index_temp> page1 = new PageImpl<Tb_entry_index_temp>(ls,new PageRequest(page - 1, limit, sort == null ? new Sort(sorts) : sort),entry_index_temps.size());
        return  page1;
    }

    //根据entryid加载管理表的数据到临时表
    public void addEntriesToTemp(String[] entryids,List<String[]> subAry,String ygType,String uniquetag,String nodeid){
        Map<String,Integer> map = new HashMap<>();
        List<Tb_index_detail> result = new ArrayList<>();
        if("ygdChange".equals(ygType)){//归档nodeid改变，subAry重新生成
            subAry = new InformService().subArray(entryids, 1000);
        }
        List<Tb_index_detail> resultList;
        for (String[] sub : subAry) {
            String entryidStr ="";
            if(sub.length>0){
                entryidStr = " and sid.entryid in('" + String.join("','", sub)+ "') ";
            }
            String sql = "select sid.* from v_index_detail sid where 1=1 " + entryidStr;
            Query query = entityManager.createNativeQuery(sql, Tb_index_detail.class);
            resultList = query.getResultList();
            result.addAll(resultList);
        }
        for (Tb_index_detail entry_index : result) {
            Tb_entry_index_temp entry_index_temp = new Tb_entry_index_temp();
            BeanUtils.copyProperties(entry_index, entry_index_temp,new String[]{"sortsequence"});
            entry_index_temp.setUniquetag(uniquetag);
            entry_index_temp.setArchivecode("");
            entry_index_temp.setNodeid(nodeid);
            //entry_index_temp.setSortsequence(map.get(entry_index_temp.getEntryid()));//排序字段
            entityManager.merge(entry_index_temp);
        }
        entityManager.flush();//批量处理
        entityManager.clear();
        //增加完后排序
        entryIndexCaptureService.setSortsequence(nodeid,uniquetag);
    }

    public List<Tb_entry_index> getEntryIndexList(String nodeid, String condition, String operator, String content) {
        Specification<Tb_entry_index> searchNodeidCondition = ClassifySearchService
                .getSearchNodeidIndex(new String[]{nodeid});
        Specifications specifications = Specifications.where(searchNodeidCondition);
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        return entryIndexRepository.findAll(specifications);
    }

    public Page<Tb_index_detail> findByNodeid(int page,int start, int limit, String condition, String operator, String content, String nodeid,Sort sort) {
        String[] name = new String[2];
        name[0] = "起止年月";
        name[1] = "filedate";
        // 此处报错的话为模板配置错误引起的问题
        Tb_data_template template = templateRepository.findFieldCode(nodeid, name);
        if (template != null) {
            String value = "";
            if (template.getFieldcode().equals("filedate")) {
                value = "FileDate";
            }
            String contentSql = "";
            if (content != null) {
                String[] conditions = condition.split(",");
                String[] operators = operator.split(",");
                String[] contents = content.split(",");
                for (int i = 0; i < contents.length; i++) {
                    contentSql += " and " + operatorContent(conditions[i], operators[i], contents[i]);
                }
            }
            String sortstr = " order by entryid asc";
            if (sort != null && sort.iterator().hasNext()) {
                Sort.Order order = sort.iterator().next();
                sortstr = " order by " + order.getProperty() + " " + order.getDirection();
            }
            String dataSql =
                    "select * from v_index_detail where " + DBCompatible.getInstance().findAppraisalOverdueData(value
                            ,nodeid,limit,"entry",contentSql) + contentSql + sortstr;
            String countSql =
                    "select count(entryid) from v_index_detail where "+ DBCompatible.getInstance().findAppraisalOverdueData(value,nodeid,limit,"count",contentSql) + contentSql;

            Query query = entityManager.createNativeQuery(dataSql, Tb_index_detail.class);
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            Query couuntQuery = entityManager.createNativeQuery(countSql);
            int count =0;
            if(couuntQuery.getResultList().size()>1){
                count=Integer.parseInt(couuntQuery.getResultList().get(0) +"")+Integer.parseInt(couuntQuery.getResultList().get(1)+"");
            }
            PageRequest pageRequest = new PageRequest(page - 1, limit);
            return new PageImpl(query.getResultList(),pageRequest,count);
        }
        return null;
    }

    public String operatorContent(String condition, String operator, String content) {
        String operatorContent = "";
        if ("equal".equals(operator)) {// 等于
            operatorContent = condition + " ='" + content + "'";
        } else if ("beginAt".equals(operator)) {// 开始于
            operatorContent = condition + " like '" + content + "%'";
        } else if ("isNull".equals(operator)) {
            operatorContent = "(" + condition + " is null or " + condition + " = '' )";
        } else if ("isNotNull".equals(operator)) {
            operatorContent = "(" + condition + " not null or " + condition + " != '' )";
        } else if ("notLike".equals(operator)) {
            operatorContent = condition + " not like '%" + content + "%'";
        } else if ("notEqual".equals(operator)) {
            operatorContent = condition + " !='" + content + "'";
        } else if ("greaterThanOrEqualTo".equals(operator)) {
            operatorContent = condition + " >= " + content;
        } else if ("lessThanOrEqualTo".equals(operator)) {
            operatorContent = condition + " <= " + content;
        } else if ("greaterThan".equals(operator)) {// 大于
            operatorContent = condition + " > " + content;
        } else if ("lessThan".equals(operator)) {// 小于
            operatorContent = condition + " < " + content;
        } else {// 默认类似与
            operatorContent = condition + " like '%" + content + "%'";
        }
        return operatorContent;
    }

    public Object generateArchivecode(String entryids, String nodeid, String[] filingValuesStrArr,
                                      Map<String, String> entryidEntryretentionMap,String uniquetag) {
        List<String[]> subAry = new InformService().subArray(entryids.split(","), 1000);
        String[] entryidsData = entryids.split(",");
        List<Tb_entry_index_temp> entryIndexes = new ArrayList<>();
        List<Tb_entry_index_temp> temps=entryIndexTempRepository.findByEntryidInAndUniquetagOrderBySortsequence(entryidsData,uniquetag);
        Object info = regenerateCodesettingFieldValue(temps, nodeid, filingValuesStrArr, entryidEntryretentionMap);
        if (!info.getClass().toString().equals("class java.lang.String")) {
            entryIndexes = (List<Tb_entry_index_temp>) info;
        } else {// 档号设置分隔符找不到时regenerateCodesettingFieldValue方法返回null
            return info.toString();
        }
        entityManager.flush();
        entityManager.close();
        return entryIndexTempRepository.save(entryIndexes);
    }

    /**
     * 数据归档归档界面点击“生成档号”时,设置档号构成字段值（包括档号）
     *
     * @param entryIndexes             需更新的条目数据
     * @param nodeid                   归档目标节点的节点ID
     * @param filingValuesStrArr       档号设置字段的值（表单中的输入值）
     * @param entryidEntryretentionMap 保管期限自动鉴定结果map
     * @return 更新后的条目数据
     */
    private Object regenerateCodesettingFieldValue(List<Tb_entry_index_temp> entryIndexes, String nodeid,
                                                   String[] filingValuesStrArr, Map<String, String> entryidEntryretentionMap) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Tb_codeset> codeSettingList = codesetRepository.findByDatanodeidOrderByOrdernum(nodeid);// 获取档号设置集合
        //List<Object> fieldLength = codesetRepository.findFieldlengthByDatanodeid(nodeid);
        // 遍历表单字段，设置除计算项以外的档号字段对应的属性值
        //List<String> codeSettingFieldList = codesettingService.getCodeSettingFields(nodeid);
        //List<String> codeSettingSplitCodes = codesettingService.getCodeSettingSplitCodes(nodeid);
        List<String> codeSettingSplitCodes = new ArrayList<>();//档号分隔符集合
        List<String> fieldLength=new ArrayList<>();//字段长度集合
        List<String> codeSettingFieldList=new ArrayList<>();//字段集合
        for(Tb_codeset codeset:codeSettingList){
            codeSettingSplitCodes.add(codeset.getSplitcode());
            fieldLength.add(String.valueOf(codeset.getFieldlength()));
            codeSettingFieldList.add(codeset.getFieldcode());
        }
        Map<String, Map<String, String>> mapFiled = getConfigMap();//获取参数设置的MAP
        List<Tb_data_template> enumList = templateRepository.getByNodeidFtype("enum", nodeid);//获取某节点的模板中属于enum的字段
        for (int i = 0; i < entryIndexes.size(); i++) {// 遍历每条需归档的记录
            // 设置所有记录统一的节点ID--------第一步
            entryIndexes.get(i).setNodeid(nodeid);
            List<String> codeSettingFieldValues = new ArrayList<>();
            if (filingValuesStrArr != null && filingValuesStrArr.length > 0) {
                Integer num = 0;
                // 如果页面没有传入计算值
                if (filingValuesStrArr.length < codeSettingFieldList.size()) {
                    num = filingValuesStrArr.length;
                } else {
                    num = filingValuesStrArr.length - 1;
                }

                for (int j = 0; j < num; j++) {
                    Integer length = Integer.valueOf(fieldLength.get(j));
                    String[] fieldNameAndValue = filingValuesStrArr[j].split("∪");
                    String fieldName = fieldNameAndValue[0];// 属性名
                    String value = fieldNameAndValue[1];// 属性值
                    if (isNumeric(value)) {
                        value = alignValue(length, Integer.valueOf(value));
                    }
                    GainField.setFieldValueByName(fieldName, entryIndexes.get(i), value);
                    value = getConfigByName(fieldName, value, enumList, mapFiled);
                    if (isNumeric(value)) {
                        value = alignValue(length, Integer.valueOf(value));
                    }
                    codeSettingFieldValues.add(value);
                }
            }
            if (!entryidEntryretentionMap.isEmpty()) {// 保管期限值通过自动鉴定获得
                entryIndexes.get(i).setEntryretention(entryidEntryretentionMap.get(entryIndexes.get(i).getEntryid()));
            }

            // 设置计算项字段对应的属性值--------第三步
            String calValue = "";
            Integer size = codeSettingList.size() - 1;
            String calculation = codeSettingFieldList.get(size);
            Integer number = (int)codeSettingList.get(size).getFieldlength();;// 获取计算项单位长度
            if (filingValuesStrArr.length == codeSettingFieldList.size()) {// 如果页面传入了计算值
                String info = filingValuesStrArr[filingValuesStrArr.length - 1].split("∪")[1];
                if (i == 0) {
                    calValue = info;
                } else {
                    calValue = String.valueOf(Integer.parseInt(info) + i);
                }
            } else {
                calValue = String.valueOf(getCalValue(entryIndexes.get(i), nodeid, codeSettingList, null));
            }
            calValue = alignValue(number, Integer.valueOf(calValue));
            GainField.setFieldValueByName(calculation, entryIndexes.get(i), calValue);
            codeSettingFieldValues.add(calValue);

            // 设置档号--------第四步
            if (codeSettingSplitCodes.size() == 0) {
                return null;
            }
            if (!entryidEntryretentionMap.isEmpty()) {
                // 若保管期限为自动鉴定生成，则档号构成字段值中的保管期限不从前端页面读取，而是间接获取，以下代码将保管期限值插入到list的指定位置
                codeSettingFieldValues.add(
                        codesetRepository.findOrdernumByFieldcodeAndDatanodeid("entryretention", nodeid) - 1,
                        entryIndexes.get(i).getEntryretention());
            }
            String archivecode = produceArchivecode(codeSettingFieldValues, codeSettingSplitCodes, calValue, nodeid);// 档号
            // 查询当前节点所有数据的档号,判断档号的唯一性
            List<String> archivecodes = entryIndexRepository.findArchivecodeByNodeid(archivecode, nodeid);
            if (archivecodes.size() > 0) {
                return archivecode;
            }
            entryIndexes.get(i).setArchivecode(archivecode);
            webSocketService.refreshGenerateArchivecodeBar(userDetails.getUserid(),i+1+"&"+entryIndexes.size());// 通知刷新
        }
        return entryIndexes;
    }


    /**
     * 根据位数补齐；位数为0则使用原值
     */
    public String alignValue(Object no, Object vo) {
        Integer n = Integer.parseInt(no.toString());
        return n == 0 ? vo.toString().trim() : String.format("%0" + n + "d", vo).trim();
    }

    public List<Tb_entry_detail> filingEntryIndex(String entryids,String userid) {
        String uniquetag=BatchModifyService.getUniquetagByType("glgd");
        List<Tb_entry_index_temp> entryIndexTemps =new ArrayList<>();
        String[] idArr = entryids.split(",");
        if("".equals(entryids)){//全部条目归档
            List<String> entryidList=entryIndexTempRepository.findEntryidByUniquetag(uniquetag);
            entryIndexTemps = entryIndexTempRepository.findByUniquetagOrderBySortsequence(uniquetag);
            idArr=entryidList.toArray(new String[entryidList.size()]);
        }else{//选择条目归档
            entryIndexTemps = entryIndexTempRepository.findByEntryidInAndUniquetag(idArr, uniquetag);
        }
        List<Tb_entry_index> entryIndexes = new ArrayList<>();
        List<Tb_entry_detail> entryDetails = new ArrayList<>();
        for (int i=0;i< entryIndexTemps.size();i++) {
            Tb_entry_index_temp entryIndexTemp=entryIndexTemps.get(i);
            Tb_entry_index entryIndex = new Tb_entry_index();
            Tb_entry_detail entryDetail = new Tb_entry_detail();
            BeanUtils.copyProperties(entryIndexTemp, entryIndex);
            if (entryIndexTemp.getFscount() != null && !"0".equals(entryIndexTemp.getFscount())) {
                entryIndex.setKccount(entryIndexTemp.getFscount());
            } else if (entryIndexTemp.getKccount() != null && !"0".equals(entryIndexTemp.getKccount())) {
                entryIndex.setFscount(entryIndexTemp.getKccount());
            } else {// 如果库存份数与份数都为null，那么归档后数量就默认为1
                entryIndex.setFscount("1");
                entryIndex.setKccount("1");
            }
            entryIndex=entryIndexRepository.save(entryIndex);
            BeanUtils.copyProperties(entryIndexTemp, entryDetail);
            entryDetail.setEntryid(entryIndex.getEntryid());
            entryDetailRepository.save(entryDetail);
            webSocketService.refreshArchiveProgressBar(userid,i+1+"&"+entryIndexTemps.size());// 通知刷新
            entryDetails.add(entryDetail);
            /*List<Tb_electronic> elecList = electronicRepository.findByEntryid(entryIndex.getEntryid());
            for (Tb_electronic elec : elecList) {
                if(elec.getFileclassid() != null && !"".equals(elec.getFileclassid())){
                    String className = fileClassificationRepository.findClassNameByFileclassid(elec.getFileclassid());
                    String fileclassid = fileClassificationRepository.findByClassnameAndNodeid(className,entryIndexTemp.getNodeid());
                    elec.setFileclassid(fileclassid);
                }
            }
            electronicRepository.save(elecList);*/
        }
        entryIndexTempRepository.deleteByUniquetagAndEntryids(uniquetag,idArr);
        return entryDetails;
    }

    public Page<Tb_entry_index> getEntryIndexes(String entryids, String nodeid, String[] filingValuesStrArr, int page,
                                                int limit) {
        String[] entryidsData = entryids.split(",");
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        Page<Tb_entry_index> entryIndexes = entryIndexRepository.findByEntryidIn(entryidsData, pageRequest);
        Long totalElements = entryIndexes.getTotalElements();
        List<Tb_entry_index> resultList = entryIndexes.getContent();
        List<Tb_entry_index> previewResultList = new ArrayList<>();
        int calIndex = (page - 1) * limit;// 每遍历一条记录，该值增加1
        for (Tb_entry_index entryIndex : resultList) {
            Tb_entry_index entry_index = new Tb_entry_index();
            BeanUtils.copyProperties(entryIndex, entry_index);

            List<String> codeSettingSplitCodes = codesettingService.getCodeSettingSplitCodes(nodeid);
            List<String> codeSettingFieldValues = new ArrayList<>();
            for (String filingValuesStr : filingValuesStrArr) {
                String[] filingValueStrData = filingValuesStr.split("∪");
                if (filingValueStrData.length > 1) {
                    codeSettingFieldValues.add(filingValueStrData[1]);
                }
            }
            String[] calFieldnameAndValue = filingValuesStrArr[filingValuesStrArr.length - 1].split("∪");
            String calFieldName = calFieldnameAndValue[0];
            String calValue = calFieldnameAndValue[1];
            // 归档的条数不止一条时，每set一条记录的计算项数值，计算项数值+1
            Integer number = codesettingService.getCalFieldLength(nodeid);// 获取计算项单位长度
            calValue = alignValue(number, Integer.valueOf(calValue) + calIndex);
            GainField.setFieldValueByName(calFieldName, entry_index, calValue);
            calIndex++;
            String archivecode = produceArchivecode(codeSettingFieldValues, codeSettingSplitCodes, calValue, nodeid);// 档号
            entry_index.setArchivecode(archivecode);

            previewResultList.add(entry_index);
        }
        return new PageImpl(previewResultList, pageRequest, totalElements);
    }

    public List filingEntryIndexes(String entryids, String nodeid, String[] filingValuesStrArr) {
        String[] entryidsData = entryids.split(",");
        List<Tb_entry_index> entryIndexes = entryIndexRepository.findByEntryidIn(entryidsData);
        HibernateEntityManager hEntityManager = (HibernateEntityManager) entityManager;
        Session session = hEntityManager.getSession();
        for (Tb_entry_index entryIndex : entryIndexes) {
            session.evict(entryIndex);// 将对象清除出缓存session，使对象由持久态变为游离态
        }
        int calIndex = 0;// 每遍历一条记录，该值增加1
        for (int i = 0; i < entryIndexes.size(); i++) {// 遍历每条需归档的记录
            // 设置所有记录统一的节点ID--------第一步
            entryIndexes.get(i).setNodeid(nodeid);

            // 遍历表单字段，设置除计算项以外的档号字段对应的属性值--------第二步
            for (int j = 0; j < filingValuesStrArr.length - 1; j++) {
                String[] fieldNameAndValue = filingValuesStrArr[j].split("∪");
                String fieldName = fieldNameAndValue[0];// 属性名
                String value = fieldNameAndValue[1];// 属性值
                GainField.setFieldValueByName(fieldName, entryIndexes.get(i), value);
            }

            // 设置计算项字段对应的属性值--------第三步
            String[] calFieldnameAndValue = filingValuesStrArr[filingValuesStrArr.length - 1].split("∪");
            String calFieldName = calFieldnameAndValue[0];
            String calValue = calFieldnameAndValue[1];
            // 归档的条数不止一条时，每set一条记录的计算项数值，计算项数值+1
            Integer number = codesettingService.getCalFieldLength(nodeid);// 获取计算项单位长度
            calValue = alignValue(number, Integer.valueOf(calValue) + calIndex);
            GainField.setFieldValueByName(calFieldName, entryIndexes.get(i), calValue);
            calIndex++;

            // 设置档号--------第四步
            List<String> codeSettingSplitCodes = codesettingService.getCodeSettingSplitCodes(nodeid);
            if (codeSettingSplitCodes.size() == 0) {
                return null;
            }
            List<String> codeSettingFieldValues = new ArrayList<>();
            for (String filingValuesStr : filingValuesStrArr) {
                codeSettingFieldValues.add(filingValuesStr.split("∪")[1]);
            }
            String archivecode = produceArchivecode(codeSettingFieldValues, codeSettingSplitCodes, calValue, nodeid);// 档号
            entryIndexes.get(i).setArchivecode(archivecode);
        }
        SolidifyThread solidifyThread = new SolidifyThread(entryidsData, "management","");// 开启固化线程
        solidifyThread.start();
        return entryIndexRepository.save(entryIndexes);
    }

    public ExtMsg againAppraisal(String approvaldate, String entryid) {
        Tb_entry_index entryIndex = entryIndexRepository.findByEntryid(entryid);
        List<String> codeSettingFieldList = codesettingService.getCodeSettingFields(entryIndex.getNodeid());// 获取档号设置字段集合
        if (codeSettingFieldList.size() == 0) {// 档号字段未设置
            return new ExtMsg(false, "鉴定失败，无法重新生成档号，请检查档号设置信息是否正确", null);
        }
        // 获取保管期限，如果档号获取失败，需要还原保管期限
        String oldApprovaldate = entryIndex.getEntryretention();
        entryIndex.setEntryretention(approvaldate);
        String archivecode = getArchivecodeValue(entryIndex, entryIndex.getNodeid(), codeSettingFieldList);
        if (archivecode != null && !("".equals(archivecode))) {
            entryIndex.setArchivecode(archivecode);
            return new ExtMsg(true, "鉴定成功", null);
        } else {
            entryIndex.setEntryretention(oldApprovaldate);
            return new ExtMsg(false, "鉴定失败，无法重新生成档号，请检查档号设置信息是否正确", null);
        }
    }

    public Integer getCalValue(Object entryIndex, String nodeid, List<Tb_codeset> codeSettingList, String type)
            throws NumberFormatException {
        Integer calValue = null;
        if (codeSettingList.size() == 1) {// 档号设置只有一个计算项字段，无其它字段
            String sql = "select max("
                    + DBCompatible.getInstance().findExpressionOfToNumber(codeSettingList.get(0).getFieldcode())
                    + ") from tb_entry_index where nodeid='" + nodeid + "'";
            Query query = entityManager.createNativeQuery(sql);
            int maxCalValue = query.getSingleResult() == null ? 0 : Integer.valueOf(query.getSingleResult().toString());
            if (maxCalValue == 0) {
                return 1;
            }
            calValue = maxCalValue + 1;

            return calValue;
        }
        String codeSettingFieldValues = "";
        //List<String> spList = codesetRepository.findSplitcodeByDatanodeid(nodeid);
        List<String> spList = new ArrayList<>();//档号分隔符集合
        List<String> fieldlengthList=new ArrayList<>();//字段长度集合
        for(Tb_codeset codeset:codeSettingList){
            spList.add(codeset.getSplitcode());
            fieldlengthList.add(String.valueOf(codeset.getFieldlength()));
        }
        for (int i = 0; i < codeSettingList.size() - 1; i++) {
            String value = "";
            // 通过反射获得档号字段的页面输入值，不含最后一个（计算项）
            String codeSettingFieldValue = GainField.getFieldValueByName(codeSettingList.get(i).getFieldcode(), entryIndex) + "";
            if (entryCaptureService.isNumeric(codeSettingFieldValue) && !codeSettingFieldValue.equals("")) {
                Integer length = Integer.parseInt(fieldlengthList.get(i));
                value = alignValue(length, Integer.valueOf(codeSettingFieldValue));
            } else {
                if (codeSettingList.get(i).getFieldcode().equals("organ")) {
                    Tb_right_organ right_organ = rightOrganRepository.getWithNodeid(nodeid);
                    if (right_organ.getCode() != null && !right_organ.getCode().equals("")) {
                        value = right_organ.getCode();
                    } else {
                        value = codeSettingFieldValue;
                    }
                } else if (codeSettingList.get(i).getFieldcode().equals("entryretention")) {
                    List<String> list = systemConfigRepository.findConfigvalueByConfigcode(codeSettingFieldValue);
                    if (list.size() == 0) {
                        value += codeSettingFieldValue;
                    } else {
                        value += list.get(0);
                    }
                } else {
                    value = codeSettingFieldValue;
                }
            }
            if (!"null".equals(codeSettingFieldValue) && !"".equals(codeSettingFieldValue)) {
                if (i < codeSettingList.size() - 2) {
                    codeSettingFieldValues += value + spList.get(i);
                }
                if (i == codeSettingList.size() - 2) {
                    codeSettingFieldValues += value;
                }
            } else {// 页面中档号设置字段无输入值
                return null;
            }
        }
        GainField.setFieldValueByName("archivecode", entryIndex, codeSettingFieldValues);//设置临时档号
        String calValueFieldCode = codeSettingList.get(codeSettingList.size() - 1).getFieldcode();
        String sql = "select max(" + DBCompatible.getInstance().findExpressionOfToNumber(calValueFieldCode)
                + ") from tb_entry_index where archivecode like '" + codeSettingFieldValues + "%' and nodeid='" + nodeid
                + "'";
        Query query = entityManager.createNativeQuery(sql);
        int maxCalValue = query.getSingleResult() == null ? 0 : Integer.valueOf(query.getSingleResult().toString());
        // 如果是数据审核
        if ("数据审核".equals(type)) {
            String captureSql = "select max(" + DBCompatible.getInstance().findExpressionOfToNumber(calValueFieldCode)
                    + ") from tb_entry_index_capture where archivecode like '" + codeSettingFieldValues + "%' and nodeid='" + nodeid
                    + "'";
            Query capturequery = entityManager.createNativeQuery(captureSql);
            int cmaxCalValue = capturequery.getSingleResult() == null ? 0 : Integer.valueOf(capturequery.getSingleResult().toString());
            if (cmaxCalValue > maxCalValue) {
                return cmaxCalValue;
            } else if (maxCalValue > cmaxCalValue) {
                return maxCalValue;
            } else {
                if (maxCalValue == 0) {
                    return 1;
                }
            }
        }else if ("预归档".equals(type)) {//预归档需要去看临时表中的档号的计算项最大值
            String uniqueTag=BatchModifyService.getUniquetagByType("glgd");
            String tempSql = "select max(" + DBCompatible.getInstance().findExpressionOfToNumber(calValueFieldCode)
                    + ") from tb_entry_index_temp where archivecode like '" + codeSettingFieldValues + "%' and uniquetag='"+uniqueTag+"' ";
            Query capturequery = entityManager.createNativeQuery(tempSql);
            int tmaxCalValue = capturequery.getSingleResult() == null ? 0 : Integer.valueOf(capturequery.getSingleResult().toString());
            if (tmaxCalValue > maxCalValue) {//临时表的计算项大于数据管理，返回临时表的计算项+1
                // 通过反射获得档号字段的页面输入计算项的值
                String oldCalValue = GainField.getFieldValueByName(calValueFieldCode, entryIndex)+"";
                if((tmaxCalValue+"").equals(oldCalValue)){//临时表的计算项最大值等于当前条目，则计算项保持不变
                    return tmaxCalValue;
                }
                return tmaxCalValue+1;
            }
        } else {
            if (maxCalValue == 0) {
                return 1;
            }
        }
        calValue = maxCalValue + 1;
        return calValue;
    }

    public String getArchivecodeValue(Tb_entry_index entryIndex, String nodeid, List<String> codeSettingFieldList) {
        Map<String, Map<String, String>> mapFiled = getConfigMap();//获取参数设置的MAP
        List<Tb_data_template> enumList = templateRepository.getByNodeidFtype("enum", nodeid);//获取某节点的模板中属于enum的字段

        List<String> codeSettingFieldValues = new ArrayList<>();// 档号字段页面输入值的集合
        for (int i = 0; i < codeSettingFieldList.size(); i++) {
            // 通过反射获得档号字段的页面输入值
            String codeSettingFieldValue = GainField.getFieldValueByName(codeSettingFieldList.get(i), entryIndex) + "";
            if (!("null".equals(codeSettingFieldValue)) && !("".equals(codeSettingFieldValue))) {
                codeSettingFieldValue = getConfigByName(codeSettingFieldList.get(i), codeSettingFieldValue, enumList, mapFiled);
                codeSettingFieldValues.add(codeSettingFieldValue);
            } else {// 页面中档号设置字段无输入值
                return null;
            }
        }
        List<String> codeSettingSplitCodes = codesettingService.getCodeSettingSplitCodes(nodeid);
        return produceArchivecode(codeSettingFieldValues, codeSettingSplitCodes,
                codeSettingFieldValues.get(codeSettingFieldValues.size() - 1), nodeid);
    }

    /**
     * 获取参数设置的所有值（枚举值），返回Map<String, Map<String, String>>集合
     *
     * @return
     */
    public Map<String, Map<String, String>> getConfigMap() {
        Map<String, Map<String, String>> mapField = new HashMap<>();//返回的集合
        Map<String, String> classMap = new HashMap<>();//枚举类
        Map<String, List<Tb_system_config>> groupMap = new HashMap<>();//分组后的集合

        List<Tb_system_config> scList = systemConfigRepository.findAll();
        for (Tb_system_config sc : scList) {
            String value = sc.getParentconfigid() != null ? sc.getParentconfigid().trim() : null;
            groupMap.computeIfAbsent(value, k -> new ArrayList<>()).add(sc);

            if (sc.getParentconfigid() == null || "".equals(sc.getParentconfigid().trim())) {
                classMap.put(sc.getConfigid().trim(), sc.getValue());//收集枚举类
            }
        }

        Iterator<Map.Entry<String, List<Tb_system_config>>> it = groupMap.entrySet().iterator();
        while (it.hasNext()) {
            Map<String, String> mapValue = new HashMap<>();//枚举值
            Map.Entry<String, List<Tb_system_config>> entry = it.next();
            List<Tb_system_config> tmpList = entry.getValue();
            for (Tb_system_config sc : tmpList) {
                mapValue.put(sc.getCode(), sc.getValue());//tmpList转成mapValue
            }
            if (classMap.get(entry.getKey()) != null && !"".equals(classMap.get(entry.getKey()))) {//去掉parentconfigid=null的组（枚举类组）
                mapField.put(classMap.get(entry.getKey()), mapValue);
            }
        }
        return mapField;
    }

    /**
     * 获取参数值，如：短期 转成 DQ
     */
    public String getConfigByName(String fieldName, String value, List<Tb_data_template> enumList, Map<String, Map<String, String>> mapFiled) {
        String returnValue = null;
        for (Tb_data_template template : enumList) {
            if (template.getFieldcode().equals(fieldName)) {
                returnValue = mapFiled.get(template.getFenums()).get(value);
                break;
            }
        }
        return returnValue == null ? value : returnValue;
    }

    public Tb_entry_index findEntryIndex(String entryid) {
        return entryIndexRepository.findByEntryid(entryid);
    }

    public List findAllByNodeidAndArchivecodeLike(Integer start, Integer limit, String nodeid, String entryid,
                                                  Sort sort) {
        // 根据档号获取实体
        Tb_entry_index tb_entry_index = entryIndexRepository.findByEntryid(entryid);
        // 获取案卷档号设置字段集合
        List<String> ajCodeSettingFieldList = codesettingService.getCodeSettingFields(tb_entry_index.getNodeid());
        // 档号设置字段值集合
        List<String> codeSettingFieldValues = new ArrayList<>();
        for (int i = 0; i < ajCodeSettingFieldList.size(); i++) {
            String codeSettingFieldValue = GainField.getFieldValueByName(ajCodeSettingFieldList.get(i), tb_entry_index)
                    + "";
            if (!"null".equals(codeSettingFieldValue) && !"".equals(codeSettingFieldValue)) {
                codeSettingFieldValues.add(codeSettingFieldValue);
            } else {
                codeSettingFieldValues.add("");
            }
        }
        // 获取卷内档号设置字段集合
        List<String> jnCodeSettingFieldList = codesettingService.getCodeSettingFields(nodeid);
        String searchCondition = getJNSearchCondition(ajCodeSettingFieldList, codeSettingFieldValues, nodeid,
                jnCodeSettingFieldList.size() > 0 ? jnCodeSettingFieldList.get(jnCodeSettingFieldList.size() - 1) : "");
        List list = new ArrayList();
        // 返回的条件语句如果是空字符串，则返回空数据回前端
        if ("".equals(searchCondition)) {
            list.add(0);
            list.add(new ArrayList<Tb_entry_index>());
            return list;
        }
        String countSql = "select count(nodeid) from tb_entry_index where " + searchCondition;
        String sql = "select * from tb_entry_index where " + searchCondition;
        Query qCount = entityManager.createNativeQuery(countSql);
        int count = Integer.valueOf(qCount.getSingleResult().toString());
        String sortstr = " order by archivecode desc";
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            sortstr = " order by " + order.getProperty() + " " + order.getDirection();
        }
        sql = sql + sortstr;
        Query query = entityManager.createNativeQuery(sql, Tb_entry_index.class);
        query.setFirstResult(start);
        query.setMaxResults(limit);
        list.add(count);
        list.add(query.getResultList());
        return list;
        // Specification<Tb_entry_index> searchNodeidCondition =
        // ClassifySearchService.getSearchNodeidCondition(new String[]{nodeid});
        // Specifications specifications =
        // Specifications.where(searchNodeidCondition);
        //// specifications = specifications.and(new
        // SpecificationUtil("archivecode","like",archivecode + "-%"));
        // specifications = specifications.and(new
        // SpecificationUtil("archivecode","beginAt",archivecode + "-"));
        // PageRequest pageRequest = new PageRequest(page-1,limit,new
        // Sort("archivecode"));
        // return entryIndexRepository.findAll(specifications, pageRequest);
    }

    public String findNodeidByEntryid(String entryid) {
        if (entryid != null && !("".equals(entryid))) {
            return entryIndexRepository.findNodeidByEntryid(entryid);
        }
        return null;
    }

    public Page<Tb_entry_index> findByEntryids(String[] entryidArr, PageRequest pageRequest) {
        return entryIndexRepository.findByEntryidIn(entryidArr, pageRequest);
    }

    /**
     * 修改拖动卷内文件后，修改卷内顺序号、同时修改相应档号，卷内文件重新排序
     *
     * @param entryIndex      需移动的条目
     * @param target          移动目标位置
     * @param filearchivecode 卷内文件所属案卷的档号
     * @param nodeid          卷内文件数据节点ID
     * @return
     */
    public ExtMsg modifyJnEntryindexOrder(Tb_entry_index entryIndex, int target, String filearchivecode,
                                          String nodeid) {
        Integer number = codesettingService.getCalFieldLength(nodeid);// 获取计算项单位长度
        List<Tb_entry_index> entryIndexes = entryIndexRepository.findAllByNodeidAndArchivecodeLike(nodeid,
                filearchivecode + "-%");
        int[] innerFileValues = new int[entryIndexes.size()];
        ExtMsg illegalCharErrorMsg = new ExtMsg(false, "卷内顺序号包含非法字符，无法排序", null);
        for (int i = 0; i < entryIndexes.size(); i++) {
            try {
                int innerFileValue = Integer.parseInt(entryIndexes.get(i).getInnerfile());
                innerFileValues[i] = innerFileValue;
            } catch (NumberFormatException e) {
                return illegalCharErrorMsg;
            }
        }
        int innerFile = 0;
        try {
            innerFile = Integer.parseInt(entryIndex.getInnerfile());
        } catch (NumberFormatException e) {
            return illegalCharErrorMsg;
        }
        if (innerFile < target) {
            // 后移。1.将目标位置包括后面的所有数据后移一个位置；
            for (int i = 0; i < innerFileValues.length; i++) {
                if (innerFileValues[i] >= target) {
                    innerFileValues[i]++;
                }
            }
        } else {
            // 前移。1.将目标位置及以后，当前数据以前的数据后移一个位置；
            for (int i = 0; i < innerFileValues.length; i++) {
                if (innerFileValues[i] >= target && innerFileValues[i] < innerFile) {
                    innerFileValues[i]++;
                }
            }
        }
        // 2.重新设置卷内顺序号及档号
        ExtMsg illegalCodesetErrorMsg = new ExtMsg(false, "档号设置异常，无法排序", null);
        List<String> codeSettingSplitCodes = codesettingService.getCodeSettingSplitCodes(nodeid);
        if (codeSettingSplitCodes.size() == 0) {
            return illegalCodesetErrorMsg;
        }
        List<String> codesetFieldCodes = codesetRepository.findFieldcodeByDatanodeid(nodeid);
        if (codesetFieldCodes.size() == 0) {
            return illegalCodesetErrorMsg;
        }

        Map<String, Map<String, String>> mapFiled = getConfigMap();//获取参数设置的MAP
        List<Tb_data_template> enumList = templateRepository.getByNodeidFtype("enum", nodeid);//获取某节点的模板中属于enum的字段
        List<String> codesetFieldValues = new ArrayList<>();
        for (int i = 0; i < codesetFieldCodes.size(); i++) {
            String codesetFieldValue = (String) GainField.getFieldValueByName(codesetFieldCodes.get(i),
                    entryIndexes.get(i));
            codesetFieldValue = getConfigByName(codesetFieldCodes.get(i), codesetFieldValue, enumList, mapFiled);
            codesetFieldValues.add(codesetFieldValue);
        }
        for (int i = 0; i < entryIndexes.size(); i++) {
            if (entryIndexes.get(i).getEntryid() == entryIndex.getEntryid()) {
                entryIndexes.get(i).setInnerfile(alignValue(number, target));
            } else {
                entryIndexes.get(i).setInnerfile(alignValue(number, innerFileValues[i]));
            }
            String archivecode = produceArchivecode(codesetFieldValues, codeSettingSplitCodes,
                    entryIndexes.get(i).getInnerfile(), nodeid);// 档号
            entryIndexes.get(i).setArchivecode(archivecode);
        }
        entryIndexRepository.save(entryIndexes);
        return null;
    }

    public void updateSubsequentData(Tb_entry_index entryIndex, List<String> codeSettingFieldList, String flag,
                                     String pages) {
        // 子件插件获取参数
        String innerfile = entryIndex.getInnerfile();
        String archivecode = entryIndex.getArchivecode();

        /* １、参数处理 */
        String nodeid = entryIndex.getNodeid();
        String entryid = entryIndex.getEntryid();
        String filecode = entryIndex.getFilecode();// 案卷号
        String recordcode = entryIndex.getRecordcode();// 件号
        Integer number = codesettingService.getCalFieldLength(nodeid);// 获取计算项单位长度
        String calFieldcode = codeSettingFieldList.get(codeSettingFieldList.size() - 1);
        String calValue = (String) (GainField.getFieldValueByName(calFieldcode, entryIndex) != null
                ? GainField.getFieldValueByName(calFieldcode, entryIndex) : "0");
        List<String> codeSettingFieldValues = new ArrayList<>();
        for (int i = 0; i < codeSettingFieldList.size() - 1; i++) {
            // 通过反射获得档号字段的页面输入值，不含最后一个（计算项）
            String codeSettingFieldValue = GainField.getFieldValueByName(codeSettingFieldList.get(i), entryIndex) + "";
            if (!"null".equals(codeSettingFieldValue) && !"".equals(codeSettingFieldValue)) {
                codeSettingFieldValues.add(codeSettingFieldValue);
            } else {
                codeSettingFieldValues.add("");
            }
        }
        List<String> codeSettingSplitCodes = codesettingService.getCodeSettingSplitCodes(nodeid);
        /* ２、数据查询及处理 */
        List<Tb_entry_index> resultList = new ArrayList<>();
        if (codeSettingFieldList.size() == 1) {
            Specifications sp = null;
            // 由于数据库计算项字段类型为varchar,此处计算项大小比较采取的是字符串比较,并不十分合理
            Specification<Tb_entry_index> searchNodeidCondition = ClassifySearchService
                    .getSearchNodeidIndex(new String[]{nodeid});
            Specification<Tb_entry_index> searchCalvalueCondition = getSearchCalvalueCondition(calFieldcode, calValue);
            Specification<Tb_entry_index> searchEntryid = getSearchEntryidCondition(entryid);
            sp = Specifications.where(searchNodeidCondition).and(searchCalvalueCondition).or(searchEntryid);
            resultList = entryIndexRepository.findAll(sp);
        } else {
            String searchCondition = getJointSearchCondition(codeSettingFieldList, codeSettingFieldValues, nodeid);
            searchCondition += " and " + calFieldcode + ">'" + calValue + "' or (entryid='" + entryid + "')";
            String sql = "select * from tb_entry_index where " + searchCondition;
            Query query = entityManager.createNativeQuery(sql, Tb_entry_index.class);
            resultList = query.getResultList();
        }

        // 先判断有无重复，无重复的话不做排序处理
        List<Tb_entry_index> copyList = entryIndexRepository.findDoubleArchivecodes(archivecode, nodeid);
        if (copyList.size() > 1) {
            if (flag.equals("jnchj")) {// 卷内插件
                List<Tb_entry_index> saveList = getModifiedList(entryIndex, resultList, calFieldcode, number,
                        codeSettingFieldList, codeSettingSplitCodes, "insertion", pages);
                entryIndexRepository.save(saveList);
                // 子件插件处理母卷的总页数和总文件数
                int jnadd = 1;
                String zjPages = pages;

                updateAjuFile(archivecode, innerfile, jnadd, zjPages, nodeid, entryid);

            } else if (flag.equals("chju") || flag.equals("syncJn")) {// 案卷排序
                // 先截取档号前端
                String parentArchivecode = archivecode.substring(0, archivecode.lastIndexOf(filecode));
                // 根据案卷号获取后边的相应案卷号的案卷(排除卷内文件)
                List<Tb_entry_index> ajNextList = entryIndexRepository
                        .findAllByArchivecodeLikeAndNext(parentArchivecode, filecode, nodeid);

                // 处理之后的案卷编号filecode各+1
                if (ajNextList.size() > 0) {
                    for (int i = ajNextList.size() - 1; i >= 0; i--) {// 反序，从最大那个开始
                        // 插卷后，后边的案卷号全部加1
                        String filecode0 = ajNextList.get(i).getFilecode();
                        String ajArch = ajNextList.get(i).getArchivecode();
                        String filecode1 = "";
                        int num = filecode0.length();
                        try {
                            filecode1 = Integer.parseInt(filecode0) + 1 + "";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        int newNum = filecode1.length();
                        // 重新拼接filecode
                        if (filecode1.length() < num) {
                            for (int j = 0; j < num - newNum; j++) {
                                filecode1 = 0 + filecode1;
                            }
                        }

                        int pNum = parentArchivecode.length();
                        if (flag.equals("syncJn")) {// 同步卷内
                            // 更新案卷和相关卷内的filecode
                            String jnNodeid = publicUtilService.getJnNodeid(nodeid);
                            // 现获取该filecode的案卷和卷内,然后按新的案卷号filecode和新的档号archivecode一一更新
                            List<Tb_entry_index> ajOneList = entryIndexRepository
                                    .findAllByArchivecodeAndFilecode(parentArchivecode, filecode0, nodeid, jnNodeid);
                            for (int k = 0; k < ajOneList.size(); k++) {
                                // 重新拼接archivecode
                                String archivecodeOne = ajOneList.get(k).getArchivecode();
                                String subArch = archivecodeOne.substring(pNum, archivecodeOne.length());// 获取案卷号和卷内号
                                String newSunArch = subArch.replaceFirst(filecode0, filecode1);// 新案卷号
                                String newArchivecode = parentArchivecode + newSunArch;
                                entryIndexRepository.updateFilecodeAndArchivecode(filecode1, newArchivecode,
                                        archivecodeOne, nodeid, jnNodeid);
                            }
                        } else {// 只处理案卷
                            // 重新拼接archivecode
                            String subArch = ajArch.substring(pNum, ajArch.length());// 获取案卷号和卷内号
                            String newSunArch = subArch.replaceFirst(filecode0, filecode1);// 新案卷号
                            String newArchivecode = parentArchivecode + newSunArch;
                            entryIndexRepository.updateFilecodeAndArchivecode(filecode1, newArchivecode, ajArch,
                                    nodeid);
                        }
                    }
                }
                // 最后处理重复的插入数据,先获取filecode，filecode+1，再更新archivecode
                String filecodeY = copyList.get(0).getFilecode();
                String filecodeY1 = "";
                int num = filecodeY.length();
                try {
                    filecodeY1 = Integer.parseInt(filecodeY) + 1 + "";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int newNum = filecodeY1.length();
                // 重新拼接filecode
                if (filecodeY1.length() < num) {
                    for (int j = 0; j < num - newNum; j++) {
                        filecodeY1 = 0 + filecodeY1;
                    }
                }
                int pNum = parentArchivecode.length();
                if (flag.equals("syncJn")) {// 同步卷内
                    // 更新案卷和相关卷内的filecode
                    // 现获取该filecode的卷内
                    String jnNodeid = publicUtilService.getJnNodeid(nodeid);
                    List<Tb_entry_index> ajCopyList = entryIndexRepository.findAllByCopyArchivecode(parentArchivecode,
                            filecodeY, jnNodeid);
                    // 增加案卷
                    ajCopyList.add(copyList.get(0));
                    // 然后按新的案卷号filecode和新的档号archivecode一一更新
                    for (int k = 0; k < ajCopyList.size(); k++) {
                        // 重新拼接archivecode
                        String archivecodeOne = ajCopyList.get(k).getArchivecode();
                        String subArch = archivecodeOne.substring(pNum, archivecodeOne.length());// 获取案卷号和卷内号
                        String newSunArch = subArch.replaceFirst(filecodeY, filecodeY1);// 新案卷号
                        String newArchivecode = parentArchivecode + newSunArch;

                        if (archivecodeOne.equals(archivecode)) {// 原案卷的更新档号
                            entryIndexRepository.updateCopyArchivecode(filecodeY1, newArchivecode, archivecodeOne,
                                    entryid);
                        } else {// 子件更新档号
                            entryIndexRepository.updateFilecodeAndArchivecode(filecodeY1, newArchivecode,
                                    archivecodeOne, jnNodeid);
                        }
                    }
                } else {// 只更新案卷
                    String subArch = archivecode.substring(pNum, archivecode.length());// 获取案卷号和卷内号
                    String newSunArch = subArch.replaceFirst(filecodeY, filecodeY1);// 新案卷号
                    String newArchivecode = parentArchivecode + newSunArch;
                    entryIndexRepository.updateCopyArchivecode(filecodeY1, newArchivecode, archivecode, entryid);
                }
            } else if (flag.equals("chji")) {// 插件排序
                String recordcodeY = "";
                String recordcodeY1 = "";
                String parentArchivecode = "";
                // 先判断档号的统计项
                String code = codeSettingFieldList.get(codeSettingFieldList.size() - 1);
                if (code.equals("recordcode")) {
                    // 先处理插件之后的档号排序
                    updateAjiSquChji(archivecode, recordcode, nodeid);
                    parentArchivecode = archivecode.substring(0, archivecode.lastIndexOf(recordcode));
                    recordcodeY = copyList.get(0).getRecordcode();
                } else if (code.equals("innerfile")) {
                    updateJnAjiSquChji(archivecode, innerfile, nodeid, pages);
                    parentArchivecode = archivecode.substring(0, archivecode.lastIndexOf(innerfile));
                    recordcodeY = copyList.get(0).getInnerfile();
                    // 更新案卷文件总数和总页数
                    int jnadd = 1;
                    String zjPages = pages;

                    updateAjuFile(archivecode, innerfile, jnadd, zjPages, nodeid, entryid);
                }

                // 再处理重复的插入数据,先获取filecode，filecode+1，再更新archivecode

                int num = recordcodeY.length();
                try {
                    recordcodeY1 = Integer.parseInt(recordcodeY) + 1 + "";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int newNum = recordcodeY1.length();
                // 重新拼接filecode
                if (recordcodeY1.length() < num) {
                    for (int j = 0; j < num - newNum; j++) {
                        recordcodeY1 = 0 + recordcodeY1;
                    }
                }

                int pNum = parentArchivecode.length();
                String subArch = archivecode.substring(pNum, archivecode.length());// 获取案卷号和卷内号
                String newSunArch = subArch.replaceFirst(recordcodeY, recordcodeY1);// 新案卷号
                String newArchivecode = parentArchivecode + newSunArch;
                if (code.equals("recordcode")) {
                    entryIndexRepository.updateCopyArchivecodeAndRecordcode(recordcodeY1, newArchivecode, archivecode,
                            entryid);
                } else if (code.equals("innerfile")) {
                    entryIndexRepository.updateCopyArchivecodeAndInnerfile(recordcodeY1, newArchivecode, archivecode,
                            entryid);
                }
            }
        }
    }

    private Integer getCalVal(List<String> codeSettingFieldList, String nodeid) {
        String sql = "select max(" + DBCompatible.getInstance().findExpressionOfToNumber(codeSettingFieldList.get(0))
                + ") from tb_entry_index where nodeid='" + nodeid + "'";
        Query query = entityManager.createNativeQuery(sql);
        Integer maxCalValueResult=0;
        if(query.getSingleResult()!=null){
            maxCalValueResult =Integer.parseInt(query.getSingleResult().toString());
            if (maxCalValueResult == null) {
                return 1;
            }
        }
        Integer maxCalValue = maxCalValueResult.intValue();
        Integer calValue = maxCalValue + 1;
        return calValue;
    }

    private Integer getCalVals(List<String> codeSettingFieldList, Tb_entry_index entry_index, String nodeid) {
        String codeSettingFieldValues = "";
        List<String> spList = codesetRepository.findSplitcodeByDatanodeid(nodeid);
        for (int i = 0; i < codeSettingFieldList.size() - 1; i++) {
            // 通过反射获得档号字段的页面输入值，不含最后一个（计算项）
            String codeSettingFieldValue = GainField.getFieldValueByName(codeSettingFieldList.get(i), entry_index) + "";
            if (!"null".equals(codeSettingFieldValue) && !"".equals(codeSettingFieldValue)) {
                if (i < codeSettingFieldList.size() - 2) {
                    codeSettingFieldValues += codeSettingFieldValue + spList.get(i);
                }
                if (i == codeSettingFieldList.size() - 2) {
                    codeSettingFieldValues += codeSettingFieldValue;
                }
            } else {// 页面中档号设置字段无输入值
                return null;
            }
        }
        String calValueFieldCode = codeSettingFieldList.get(codeSettingFieldList.size() - 1);
        String sql = "select max(" + DBCompatible.getInstance().findExpressionOfToNumber(calValueFieldCode)
                + ") from tb_entry_index where archivecode like '" + codeSettingFieldValues + "%' and nodeid='" + nodeid
                + "'";
        Query query = entityManager.createNativeQuery(sql);
        BigInteger maxCalValueResult = (BigInteger) query.getSingleResult();
        if (maxCalValueResult == null) {
            return 1;
        }
        Integer maxCalValue = maxCalValueResult.intValue();
        Integer calValue = maxCalValue + 1;

        return calValue;
    }

    public String dismantle(String entryid, String targetNodeid, String title, String syncType) {
        Tb_entry_index entryIndex = entryIndexRepository.findByEntryid(entryid);
        String archivecode = entryIndex.getArchivecode();
        String innerfile = entryIndex.getInnerfile();// 卷内文件件号
        String filecode = entryIndex.getFilecode();// 案卷号
        String recordcode = entryIndex.getRecordcode();// 件号
        String zjPages = entryIndex.getPages();
        String nodeid = entryIndex.getNodeid();

        if (archivecode == null) {
            return "档号不能为空";
        }

        List<Tb_codeset> codeFieldList = codesetRepository.findByDatanodeidOrderByOrdernum(nodeid); // 获取档号设置字段集合
        if (codeFieldList.size() != 0) {
            Tb_codeset codeset = codeFieldList.get(codeFieldList.size() - 1); // 获取最后一个档号组成字段
            Object lastFieldValue = GainField.getFieldValueByName(codeset.getFieldcode(), entryIndex);// 获取字段值
            if (lastFieldValue == null) {
                return codeset.getFieldname() + "不能为空";
            }
        } else {
            return "档号组成有误";
        }

        Tb_entry_index entry_index = new Tb_entry_index();
        BeanUtils.copyProperties(entryIndex, entry_index);
        List<String> codeSettingFieldList = codesettingService.getCodeSettingFields(targetNodeid);// 获取档号设置字段集合
        Integer number = codesettingService.getCalFieldLength(targetNodeid);// 获取计算项单位长度
        String restoreCalVal = "";
        Integer calValue = 0;
        if (codeSettingFieldList.size() == 0) {// 没有档号设置直接放置过去
            entry_index.setArchivecode(null);
            entry_index.setNodeid(targetNodeid);
            entryIndexRepository.save(entry_index);
        } else {
            if (codeSettingFieldList.size() == 1) {// 档号设置只有一个计算项字段，无其它字段
                calValue = getCalVal(codeSettingFieldList, targetNodeid);
            } else {
                calValue = getCalVals(codeSettingFieldList, entry_index, targetNodeid);
            }
            if (calValue != null) {
                restoreCalVal = alignValue(number, calValue);
            }
            String calFieldcode = codeSettingFieldList.get(codeSettingFieldList.size() - 1);
            GainField.setFieldValueByName(calFieldcode, entry_index, restoreCalVal);// 还原统计项的值，确保拆到其它节点后其数值保持不变
            entry_index.setArchivecode(null);
            entry_index.setNodeid(targetNodeid);
            entryIndexRepository.save(entry_index);
        }

        // 拆卷并且要求同步卷内文件，把卷内文件也拆到那个节点
        String jnNodeid = "";
        if (syncType.equals("syncInnerFile") && title.equals("1")) {
            jnNodeid = publicUtilService.getJnNodeid(nodeid);
            // 根据档号去查询所有卷内文件包括自身案卷
            List<Tb_entry_index> ajList = entryIndexRepository
                    .findAllByArchivecodeLikeAndNodeidOrderByArchivecode(archivecode, jnNodeid);
            if (ajList.size() > 0) {// 案卷本身已更新
                // 把所有相关档号的案卷和子件都更新节点和档号
                entryIndexRepository.updateNodeidAndArchivecode(targetNodeid, archivecode, jnNodeid);
            }
        }

        // 拆卷后，更新案卷件号顺序，更新相应案卷相应卷内文件的档号（即选中记录的后边的记录的档号中的案卷件号减一）
        if (title.equals("1")) {
            // 先截取档号前端
            String parentArchivecode = archivecode.substring(0, archivecode.lastIndexOf(filecode));
            // 根据案卷号获取后边的相应案卷号的案卷(排除卷内文件)
            List<Tb_entry_index> ajNextList = entryIndexRepository.findAllByArchivecodeLikeAndNext(parentArchivecode,
                    filecode, nodeid);
            if (ajNextList.size() > 0) {
                for (int i = 0; i < ajNextList.size(); i++) {
                    // 拆件后，后边的案卷号全部减1
                    String filecode0 = ajNextList.get(i).getFilecode();
                    String archivecode0 = ajNextList.get(i).getArchivecode();
                    String filecode1 = "";
                    int num = filecode0.length();
                    try {
                        filecode1 = Integer.parseInt(filecode0) - 1 + "";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int newNum = filecode1.length();
                    // 重新拼接filecode
                    if (filecode1.length() < num) {
                        for (int j = 0; j < num - newNum; j++) {
                            filecode1 = 0 + filecode1;
                        }
                    }
                    int pNum = parentArchivecode.length();
                    if (syncType.equals("syncInnerFile")) {// 同步更新卷内
                        // 更新案卷和相关卷内的filecode
                        // 现获取该filecode的案卷和卷内,然后按新的案卷号filecode和新的档号archivecode一一更新
                        List<Tb_entry_index> ajOneList = entryIndexRepository
                                .findAllByArchivecodeAndFilecode(parentArchivecode, filecode0, nodeid, jnNodeid);
                        for (int k = 0; k < ajOneList.size(); k++) {
                            // 重新拼接archivecode
                            String archivecodeOne = ajOneList.get(k).getArchivecode();
                            String subArch = archivecodeOne.substring(pNum, archivecodeOne.length());// 获取案卷号和卷内号
                            String newSunArch = subArch.replaceFirst(filecode0, filecode1);// 新案卷号
                            String newArchivecode = parentArchivecode + newSunArch;
                            entryIndexRepository.updateFilecodeAndArchivecode(filecode1, newArchivecode, archivecodeOne,
                                    nodeid, jnNodeid);
                        }

                    } else {// 只更新案卷顺序
                        String subArch = archivecode0.substring(pNum, archivecode0.length());// 获取案卷号和卷内号
                        String newSunArch = subArch.replaceFirst(filecode0, filecode1);// 新案卷号
                        String newArchivecode = parentArchivecode + newSunArch;
                        entryIndexRepository.updateFilecodeAndArchivecode(filecode1, newArchivecode, archivecode0,
                                nodeid);
                    }
                }
            }
        }
        // 卷内文件拆件，更新该卷条目的文件数和总页数，title: 1拆卷 2拆件
        if (title.equals("2") || innerfile != null) {
            // 更新案卷文件总数和总页数
            int jnadd = 0;
            updateAjuFile(archivecode, innerfile, jnadd, zjPages, nodeid, entryid);
            // 2排序
            updateJnSqu(archivecode, innerfile, nodeid);
        }
        // 拆件后，更新案卷件号顺序，（即选中记录的后边的记录的档号中的案件号减一）
        if (title.equals("3") && innerfile == null) {// 3 拆件
            updateAjiSqu(archivecode, recordcode, nodeid);// 案件排序
        }

        return "拆件成功";
    }

    public static Specification<Tb_entry_index> getSearchEntryidCondition(String[] entryidArr) {
        Specification<Tb_entry_index> searchEntryID = new Specification<Tb_entry_index>() {
            @Override
            public Predicate toPredicate(Root<Tb_entry_index> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                Predicate[] predicates = new Predicate[entryidArr.length];
                for (int i = 0; i < entryidArr.length; i++) {
                    predicates[i] = criteriaBuilder.equal(root.get("entryid"), entryidArr[i]);
                }
                return criteriaBuilder.or(predicates);
            }
        };
        return searchEntryID;
    }

    /**
     * 根据档号构成字段值、档号设置分隔符、计算项值、节点id
     *
     * @param codeSettingFieldValues 档号构成字段值（可包含计算项值，也可不包含，若包含计算项值，该值不会被此方法使用， 此处设计是为了便于传入参数处理方便——
     *                               1>归档的档号生成功能，不传入计算项值， 2>著录等其它地方调用此方法，默认包含了计算项值，省去传入参数处理步骤）
     * @param codeSettingSplitCodes  档号设置分隔符（不包含计算项字段对应的分隔符）
     * @param calValue               计算项值
     * @param nodeid                 节点id
     * @return
     */
    public String produceArchivecode(List<String> codeSettingFieldValues, List<String> codeSettingSplitCodes,
                                     String calValue, String nodeid) {
        List<String> codesetFieldCodes = codesetRepository.findFieldcodeByDatanodeid(nodeid);
        String archivecode = "";// 档号
        String organcode = rightOrganRepository.findCodeByOrganid(getOrganidByNodeidLoop(nodeid));
        String type = templateRepository.findOrganFtypeByNodeid(nodeid);
        if (codeSettingFieldValues.size() > 0) {
            for (int i = 0; i <codeSettingFieldValues.size() - 1; i++) {
                // 档号构成包括机构字段且机构对应编码已设置，则档号生成时使用编码（否则使用机构名）
                if (type != null && type.equals("string") && "organ".equals(codesetFieldCodes.get(i))
                        && organcode != null && !"".equals(organcode)) {
                    archivecode += organcode + codeSettingSplitCodes.get(i);
                } else {
                    archivecode += codeSettingFieldValues.get(i) + codeSettingSplitCodes.get(i);
                }
            }
        }
        archivecode += calValue;
        return archivecode;
    }

    public static Specification<Tb_entry_index> getSearchCalvalueCondition(String calFieldcode, String calValue) {
        Specification<Tb_entry_index> searchCalvalueCondition = new Specification<Tb_entry_index>() {
            @Override
            public Predicate toPredicate(Root<Tb_entry_index> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.greaterThan(root.get(calFieldcode), calValue);
                return criteriaBuilder.and(p);
            }
        };
        return searchCalvalueCondition;
    }

    public static Specification<Tb_entry_index> getSearchEntryidCondition(String entryid) {
        Specification<Tb_entry_index> searchEntryid = new Specification<Tb_entry_index>() {
            @Override
            public Predicate toPredicate(Root<Tb_entry_index> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get("entryid"), entryid);
                return criteriaBuilder.and(p);
            }
        };
        return searchEntryid;
    }

    public static Specification<Tb_entry_index> getSearchEntryidsCondition(String[] entryidArr) {
        Specification<Tb_entry_index> searchEntryidsCondition = null;
        searchEntryidsCondition = new Specification<Tb_entry_index>() {
            @Override
            public Predicate toPredicate(Root<Tb_entry_index> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                CriteriaBuilder.In in = criteriaBuilder.in(root.get("entryid"));
                for (String entryid : entryidArr) {
                    in.value(entryid);
                }
                return criteriaBuilder.or(in);
            }
        };
        return searchEntryidsCondition;
    }

    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static String getJointSearchCondition(List<String> codeSettingFieldList, List<String> codeSettingFieldValues,
                                                 String nodeid) {
        String searchCondition = "";
        for (int i = 0; i < codeSettingFieldList.size() - 1; i++) {
            if (i == 0) {
                searchCondition += codeSettingFieldList.get(i) + "='" + codeSettingFieldValues.get(i) + "' ";
            } else {
                searchCondition += "and " + codeSettingFieldList.get(i) + "='" + codeSettingFieldValues.get(i) + "' ";
            }
        }
        searchCondition += "and nodeid='" + nodeid + "'";
        return searchCondition;
    }

    // 拼接案卷的档号构成字段和卷内的档号构成字段的最后一个
    public String getJNSearchCondition(List<String> codeSettingFieldList, List<String> codeSettingFieldValues,
                                       String nodeid, String lastJnField) {
        String searchCondition = "";
        for (int i = 0; i < codeSettingFieldList.size(); i++) {
            String field = codeSettingFieldList.get(i);
            String value = codeSettingFieldValues.get(i);
            if (i == 0) {
                searchCondition += field + "='" + value + "' ";
            } else {
                searchCondition += "and " + field + "='" + value + "' ";
            }
        }
        if (!"".equals(searchCondition)) {
            if (!"".equals(lastJnField)) {
                searchCondition += "and nodeid='" + nodeid + "' and " + lastJnField + " is not null";
            } else {
                searchCondition += "and nodeid='" + nodeid+"'";
            }
        }
        return searchCondition;
    }

    public List<Tb_entry_index> getModifiedList(Tb_entry_index entryIndex, List<Tb_entry_index> resultList,
                                                String calFieldcode, Integer number, List<String> codeSettingFieldList, List<String> codeSettingSplitCodes,
                                                String flag, String pages) {
        List<Tb_entry_index> saveList = new ArrayList<>();
        for (Tb_entry_index tb_entry_index : resultList) {
            Tb_entry_index entry_index = new Tb_entry_index();
            BeanUtils.copyProperties(tb_entry_index, entry_index);
            String calVal = (String) (GainField.getFieldValueByName(calFieldcode, entry_index) != null
                    && !"".equals(GainField.getFieldValueByName(calFieldcode, entry_index))
                    ? GainField.getFieldValueByName(calFieldcode, entry_index) : "0");
            String pageno = entry_index.getPageno() != null && !"".equals(entry_index.getPageno())
                    ? entry_index.getPageno() : "0";
            String modifiedCalVal = "";
            String modifiedPageno = "";
            Integer calIntVal = Integer.parseInt(calVal);
            Integer pagenoIntVal = Integer.parseInt(pageno);
            if ("insertion".equals(flag)) {// 插件
                modifiedCalVal = alignValue(number, calIntVal + 1);
                // modifiedPageno = pagenoIntVal + Integer.parseInt(pages !=
                // null && !"".equals(pages) ? pages : "0") + "";
                modifiedPageno = pagenoIntVal + "";
            }
            if ("dismantle".equals(flag)) {// 拆件
                modifiedCalVal = alignValue(number, calIntVal - 1);
                modifiedPageno = pagenoIntVal
                        - Integer.parseInt(entryIndex.getPages() != null && !"".equals(entryIndex.getPages())
                        ? entryIndex.getPages() : "0")
                        + "";
            }
            GainField.setFieldValueByName(calFieldcode, entry_index, modifiedCalVal);
            entry_index.setPageno(modifiedPageno);
            if (codeSettingFieldList.size() == 1) {
                entry_index.setArchivecode(modifiedCalVal);
            } else {
                Map<String, Map<String, String>> mapFiled = getConfigMap();//获取参数设置的MAP
                List<Tb_data_template> enumList = templateRepository.getByNodeidFtype("enum", entryIndex.getNodeid());//获取某节点的模板中属于enum的字段
                List<String> codeSettingFieldValues = new ArrayList<>();
                for (String codeSettingFieldcode : codeSettingFieldList) {
                    String codeSettingFieldValue = (String) (GainField.getFieldValueByName(codeSettingFieldcode,
                            entry_index) != null ? GainField.getFieldValueByName(codeSettingFieldcode, entry_index)
                            : "");
                    codeSettingFieldValue = getConfigByName(codeSettingFieldcode, codeSettingFieldValue, enumList, mapFiled);
                    codeSettingFieldValues.add(codeSettingFieldValue);
                }
                String archivecode = produceArchivecode(codeSettingFieldValues, codeSettingSplitCodes, modifiedCalVal,
                        entryIndex.getNodeid());
                entry_index.setArchivecode(archivecode);
            }
            saveList.add(entry_index);
        }
        return saveList;
    }

    public Map<String, Object> getFileNodeidAndEntryidByInnerfileEntryid(String entryid) {
        Map<String, Object> result = new HashMap<>();
        Tb_entry_index index = entryIndexRepository.findByEntryid(entryid);
        String innerfileArchivecode = index.getArchivecode();
        String innerfileNodeid = index.getNodeid();
        String innerfileParentnodeid = dataNodeRepository.findParentnodeidByNodeid(innerfileNodeid);// 卷内文件节点的父节点nodeid
        List<String> childrenNodeids = getNodeidByWithAs(innerfileParentnodeid);// 卷内文件节点并列的节点的nodeid集合
        String[] childrenNodeidArr = new String[childrenNodeids.size()];
        childrenNodeids.toArray(childrenNodeidArr);
        List<String> codeSettingSplitCodes = codesetRepository.findSplitcodeByDatanodeid(innerfileNodeid);
        String fileArchivecode = "";
        if (codeSettingSplitCodes.size() > 1) {
            String codeSettingSplitCode = codeSettingSplitCodes.get(codeSettingSplitCodes.size() - 2);// 档号设置中倒数第二个字段的分隔符
            fileArchivecode = innerfileArchivecode.substring(0, innerfileArchivecode.lastIndexOf(codeSettingSplitCode));
        }
        List<String> fileEntryids = entryIndexRepository.findEntryidByArchivecodeAndNodeidIn(fileArchivecode,
                childrenNodeidArr);
        if (fileEntryids.size() == 0) {
            String filenodeid = publicUtilService.getFileNodeid(innerfileNodeid);
            fileEntryids = entryIndexRepository.findEntryidByArchivecodeAndNodeidIn(fileArchivecode,
                    new String[]{filenodeid});
        }
        String fileEntryid;
        if (fileEntryids.size() == 0) {
            fileEntryid = "";
        } else {
            fileEntryid = fileEntryids.get(0);
        }
        String fileNodeid = entryIndexRepository.findNodeidByEntryid(fileEntryid);
        result.put("entryid", fileEntryid);
        result.put("nodeid", fileNodeid);
        return result;
    }

    public Map<String, Object> getFileNodeidAndEntryid(String nodeid, String entryid) {
        Map<String, Object> result = new HashMap<>();
        Tb_entry_index index = entryIndexRepository.findByEntryid(entryid);
        String innerfileArchivecode = index.getArchivecode();
        String innerfileNodeid = index.getNodeid();
        String innerfileParentnodeid = dataNodeRepository.findParentnodeidByNodeid(innerfileNodeid);// 卷内文件节点的父节点nodeid
        List<String> childrenNodeids = getNodeidByWithAs(innerfileParentnodeid);// 卷内文件节点并列的节点的nodeid集合
        String[] childrenNodeidArr = new String[childrenNodeids.size()];
        childrenNodeids.toArray(childrenNodeidArr);
        List<String> codeSettingSplitCodes = codesetRepository.findSplitcodeByDatanodeid(innerfileNodeid);
        String fileArchivecode = "";
        if (codeSettingSplitCodes.size() > 1) {
            String codeSettingSplitCode = codeSettingSplitCodes.get(codeSettingSplitCodes.size() - 2);// 档号设置中倒数第二个字段的分隔符
            fileArchivecode = innerfileArchivecode.substring(0, innerfileArchivecode.lastIndexOf(codeSettingSplitCode));
        }
        // 根据档号查找是否存在案卷的条目
        List<String> fileEntryids = entryIndexRepository.findEntryidByArchivecodeAndNodeidIn(fileArchivecode,
                childrenNodeidArr);
        if (fileEntryids.size() == 0) {
            String filenodeid = publicUtilService.getFileNodeid(innerfileNodeid);
            fileEntryids = entryIndexRepository.findEntryidByArchivecodeAndNodeidIn(fileArchivecode,
                    new String[]{filenodeid});
        }
        String fileEntryid;
        if (fileEntryids.size() == 0) {
            fileEntryid = "";
        } else {
            fileEntryid = fileEntryids.get(0);
        }
        String fileNodeid = publicUtilService.getFileNodeid(nodeid);
        result.put("entryid", fileEntryid);
        result.put("nodeid", fileNodeid);
        result.put("archivecode", fileArchivecode);
        return result;
    }

    //寻找到节点下的所有子节点
    public void getChildNodeid(String nodeid, List<String> allChildNodes) {
        List<String> nodeList = getNodeidByWithAs(nodeid);
        allChildNodes.addAll(nodeList);
//        if (nodeList != null && nodeList.size() > 0) {
//            allChildNodes.addAll(nodeList);
//            for (int i = 0; i < nodeList.size(); i++) {
//                getChildNodeid(nodeList.get(i), allChildNodes);
//            }
//        }
    }

    public Integer getFilingNum(String nodeid, String funds) {
        List<String> allChildNodes = new ArrayList<>();
        getChildNodeid(nodeid, allChildNodes);
        if (allChildNodes.size() > 0) {
            List<String[]> subAry = new InformService()
                    .subArray(allChildNodes.toArray(new String[allChildNodes.size()]), 1000);
            Integer num = 0;
            for (String[] ary : subAry) {
                num += Integer.valueOf(entryIndexRepository.findByFundsIn(ary, funds));
            }
            return num;
        }
        return 0;
    }

    public String getFileds(List<Tb_data_node> nodes, String funds,List<String> nodeListReturn) {
        int value = 0;
        for (int i = 0; i < nodes.size(); i++) {
            String nodeid = nodes.get(i).getNodeid();
            if (nodeListReturn.contains(nodeid)) {
                value += Integer.valueOf(entryIndexRepository.findByFunds(nodeid, funds));
                List<String> nodeidList = new ArrayList<>();
                getChildNodeid(nodeid, nodeidList);
                if (nodeidList.size() > 0) {
                    //将子节点的条目数也计算出来
                    value += Integer.valueOf(entryIndexRepository.findByFundsIn(nodeidList.toArray(new String[nodeidList.size()]), funds));
                }
            }
        }
        return String.valueOf(value);
    }

    public String getAllFileds(List<Tb_data_node> nodes, List<String> filesid, String funds,List<String> nodeListget) {
        int value = 0;
        List<String> nodeList = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {// refid所对应的节点信息
            String nodeid = nodes.get(i).getNodeid();
            if (nodeListget.contains(nodeid)) {
                for (int j = 0; j < filesid.size(); j++) {
                    if (getNodeInfo(nodeid, filesid.get(j)) != null) {
                        nodeList.add(nodeid);
                        value += Integer.valueOf(entryIndexRepository.findByFunds(nodeid, funds));
                    }
                }
            }
        }
        for (int i = 0; i < nodeList.size(); i++) {//查找出符合条件的节点下所有子节点具体的值
            List<String> childList = new ArrayList<>();
            getChildNodeid(nodeList.get(i), childList);
            if (childList.size() > 0) {
                value += Integer.valueOf(entryIndexRepository.findByFundsIn(childList.toArray(new String[childList.size()]), funds));
            }
        }
        return String.valueOf(value);
    }

    //如果有子机构,需要将子机构的数据也统计出来
    public String getTerm(List<Tb_data_node> nodes, String dcnodeid, String[] entryretention, String funds) {//已归管理-文书档案
        Integer value = 0;
        List<String> nodeList = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            Tb_data_node node = nodes.get(i);
            if (getNodeInfo(node.getNodeid(), dcnodeid) != null) {
                nodeList.add(node.getNodeid());//查找到当前节点为已归管理 - 文书档案下的子节点
            }
        }
        if (nodeList.size() > 0) {//查找节点中,保管期限与全宗号符合条件的数据
            String count = entryIndexRepository.findByEntryretention(nodeList.toArray(new String[nodeList.size()]), entryretention, funds);
            value += Integer.valueOf(count);
        }
        for (int i = 0; i < nodeList.size(); i++) {
            //查找到当前节点的所有子机构
            List<String> childNode = new ArrayList<>();
            getChildNodeid(nodeList.get(i), childNode);
            if (childNode.size() > 0) {//查找节点中,保管期限与全宗号符合条件的数据
                String count = entryIndexRepository.findByEntryretention(childNode.toArray(new String[childNode.size()]), entryretention, funds);
                value += Integer.valueOf(count);
            }
        }
        return String.valueOf(value);
    }

    public Tb_data_node getNodeLevel(String nodeid) {
        String str = DBCompatible.getInstance().findWithAs();  //对于with as 做不同数据库兼容
        String sql = "With "+str+" t_findNameByNode As( select * from tb_data_node where nodeid='"+nodeid+"' Union ALL SELECT t.* from tb_data_node t INNER JOIN t_findNameByNode f ON t.nodeid=f.parentnodeid )select DISTINCT* from t_findNameByNode  where nodelevel='1'";
        //     Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
        Tb_data_node node=getDataNodeByWithAs(sql);
        if (node == null) {
            return null;
        }
        return node;
    }

    public Tb_data_node getNodeValue(String nodeid, String name) {
        String str = DBCompatible.getInstance().findWithAs();  //对于with as 做不同数据库兼容
        String sql = "With "+str+" t_findNameByNode As( select * from tb_data_node where nodeid='"+nodeid+"' Union ALL SELECT t.* from tb_data_node t INNER JOIN t_findNameByNode f ON t.nodeid=f.parentnodeid )select DISTINCT * from t_findNameByNode where nodename='"+name+"'";
        //   Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
        Tb_data_node node=getDataNodeByWithAs(sql);
        if (node == null) {
            return null;
        }
        return node;
    }

    public Tb_data_node getNodeInfo(String nodeid, String id) {
        String str = DBCompatible.getInstance().findWithAs();  //对于with as 做不同数据库兼容
        String sql = "With "+str+" t_findParentidBynodeLoop AS(SELECT * FROM tb_data_node WHERE nodeid='"+nodeid+"' UNION ALL SELECT t.* FROM tb_data_node t INNER JOIN t_findParentidBynodeLoop f ON t.nodeid = f.parentnodeid )select DISTINCT * from t_findParentidBynodeLoop where parentnodeid='"+id+"'";
        //Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
        Tb_data_node node=getDataNodeByWithAs(sql);

        if (node == null) {
            return null;
        }
        return node;
    }

    /**
     * 通过当前部门机构的信息获取所属单位的机构id
     *
     * @param organid
     * @return
     */
    public String getOrganInfo(String organid) {
        Tb_right_organ organ = organService.findOrgan(organid);// 获取到部门的机构信息
        if (organ == null) {
            return null;
        } else if (organ.getOrgantype() == null) {
            return null;
        } else if ("unit".equals(organ.getOrgantype()) || "单位".equals(organ.getOrgantype())) {
            return organid;
        } else {
            Tb_right_organ parentOrgan = organService.findOrgan(organ.getParentid());
            if (parentOrgan == null) {
                return null;
            } else if ("unit".equals(parentOrgan.getOrgantype()) || "单位".equals(organ.getOrgantype())) {
                return getOrganInfo(parentOrgan.getOrganid());
            } else {
                return getOrganInfo(parentOrgan.getParentid());
            }
        }
    }

    public String getOrganidByNodeidLoop(String nodeid) {
        nodeid = String.format("%1$-36s", (String) nodeid);
        int nodetype = dataNodeRepository.findNodetypeByNodeid(nodeid);
        if (nodetype == 1) {
            return dataNodeRepository.findRefidByNodeid(nodeid);
        } else {
            return getOrganidByNodeidLoop(dataNodeRepository.findParentnodeidByNodeid(nodeid));
        }
    }

    //查找storage中不存在的entry
    public Page<Tb_index_detail> getStorageNoEntries(String nodeid, String condition, String operator, String content,Tb_index_detail formConditions,
                                                     ExtOperators formOperators, ExtDateRangeData daterangedata, String logic, int page, int limit, Sort sort) {
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        String sortStr = "";//排序
        int sortInt = 0;//判断是否副表表排序
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            if ("eleid".equals(order.getProperty())) {
                sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
            } else {
                sortStr = " order by " + order.getProperty() + " " + order.getDirection();
            }
            sortInt = checkFilecode(order.getProperty());
        } else {
            sortStr = " order by archivecode desc, descriptiondate desc ";
        }

        String searchCondition = "";
        if (content != null && !"".equals(content)) {//输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        String dataStr = "";
        if (daterangedata.getFiledateendday() != null && daterangedata.getFiledatestartday() != null) {//检索条件为日期范围
            dataStr = classifySearchService.getDateRangeCondition(daterangedata);
        }
        String formAdvancedSearch = "";
        if (logic != null) {//高级检索的表单检索
            formAdvancedSearch = classifySearchService.getFormAdvancedIndexDetailSearch(formConditions, formOperators, logic);
        }
        String formDetail = "";//标记表单检索是否有副表字段
        if (!"".equals(formAdvancedSearch)) {
            formDetail = formAdvancedSearch.substring(0, 1);
            formAdvancedSearch = formAdvancedSearch.substring(1);
        }
        String table = "v_index_detail";
        String countTable = "v_index_detail";
        if ((condition == null || checkFilecode(condition) == 0) && ("".equals(formDetail) || "0".equals(formDetail))) {//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
            countTable = "tb_entry_index";
            if (sortInt == 0) {//非副表表字段排序
                table = "tb_entry_index";
            }
        }
        String sql = "select sid.* from " + table + " sid where entryid not in (select entry from st_storage where entry is not null) and nodeid='" + nodeid + "' " + searchCondition+ formAdvancedSearch + dataStr ;
        String countSql = "select count(nodeid) from " + countTable + " sid where entryid not in (select entry from st_storage where entry is not null) and nodeid='" + nodeid + "' " + searchCondition+ formAdvancedSearch + dataStr ;

        return getPageListTwo(sql, sortStr, countSql, page, limit, pageRequest);
    }

    public Page<Tb_index_detail> getaddStorages(String condition, String operator, String content, int page, int limit, Sort sort,String addstate){
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort(Sort.Direction
                .DESC, "descriptiondate") : sort);
        String searchCondition = "";
        if (content != null) {//输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        SecurityUser userDetails=(SecurityUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId=userDetails.getUserid();
        List<String> entryids = bookmarksRepository.findEntryidByUseridandAddstate(userId,addstate);
        String sql = "select sid.* from v_index_detail sid where entryid in ('" + String.join("','", entryids) + "') "+ searchCondition;
        String countSql = "select count(nodeid) from v_index_detail sid where entryid in ('" + String.join("','", entryids) + "') "+ searchCondition;
        return getPageList(sql, countSql, page, limit, pageRequest);
    }

    //查找storage中存在的已入库entry
    public Page<Tb_index_detail> findStorageEntries(String staIn,String nodeid,String condition, String operator, String content,Tb_index_detail formConditions,
                                                    ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,int page, int limit, Sort sort) {
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        String sortStr = "";//排序
        int sortInt = 0;//判断是否副表表排序
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            if ("eleid".equals(order.getProperty())) {
                sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
            } else {
                sortStr = " order by " + order.getProperty() + " " + order.getDirection();
            }
            sortInt = checkFilecode(order.getProperty());
        } else {
            sortStr = " order by archivecode desc, descriptiondate desc ";
        }

        String searchCondition = "";
        if (content != null && !"".equals(content)) {//输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        String dataStr = "";
        if (daterangedata.getFiledateendday() != null && daterangedata.getFiledatestartday() != null) {//检索条件为日期范围
            dataStr = classifySearchService.getDateRangeCondition(daterangedata);
        }
        String formAdvancedSearch = "";
        if (logic != null) {//高级检索的表单检索
            formAdvancedSearch = classifySearchService.getFormAdvancedIndexDetailSearch(formConditions, formOperators, logic);
        }
        String formDetail = "";//标记表单检索是否有副表字段
        if (!"".equals(formAdvancedSearch)) {
            formDetail = formAdvancedSearch.substring(0, 1);
            formAdvancedSearch = formAdvancedSearch.substring(1);
        }
        String table = "v_index_detail";
        String countTable = "v_index_detail";
        if ((condition == null || checkFilecode(condition) == 0) && ("".equals(formDetail) || "0".equals(formDetail))) {//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
            countTable = "tb_entry_index";
            if (sortInt == 0) {//非副表表字段排序
                table = "tb_entry_index";
            }
        }
        String sql = "select sid.* from " + table + " sid where entryid in (select entry from st_storage where entry is not null and storestatus='" + staIn + "') and nodeid='" + nodeid + "' " + searchCondition+ formAdvancedSearch + dataStr ;
        String countSql = "select count(nodeid) from " + countTable + " sid where entryid in (select entry from st_storage where entry is not null and storestatus='" + staIn + "') and nodeid='" + nodeid + "' " + searchCondition+ formAdvancedSearch + dataStr ;
        return getPageListTwo(sql, sortStr, countSql, page, limit, pageRequest);
    }

    //查找storage中存在的出库条目staOut
    public List<Tb_entry_index> getOutwares(String staOut) {
        return entryIndexRepository.getOutwares(staOut);
    }

    public Page<Tb_entry_index> getOutwares(String staOut, int page,int limit, String condition, String operator, String content,Sort sort) {
        PageRequest pageRequest = new PageRequest(page - 1, limit,
                sort == null ? new Sort(Sort.Direction.DESC, "archivecode", "descriptiondate") : sort);
        String searchCondition = "";
        if (content != null && !"".equals(content)){//输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "", operator);
        }
        String sql = "select * from tb_entry_index where  entryid  in (select entry from st_storage where storestatus= '"+staOut+"' and  entry is not null)"+searchCondition;
        Query query = entityManager.createNativeQuery(sql, Tb_entry_index.class);
        query.setFirstResult((page - 1) * limit);
        query.setMaxResults(limit);
        List<Tb_entry_index> resultList = query.getResultList();
        int count = entryIndexRepository.getOutwaresNums(staOut);
        return new PageImpl(resultList, pageRequest,count);
    }

    //查找storage中存在的入库条目
    public Page<Tb_index_detail> getInwares(String staIn, String nodeid, String condition, String operator, String content, int page, int limit, Sort sort) {
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort(Sort.Direction
                .DESC, "descriptiondate") : sort);
        String searchCondition = "";
        if (content != null) {//输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        String sql = "select sid.* from v_index_detail sid where entryid in (select entry from st_storage where entry is not null and storestatus='" + staIn + "') and nodeid='" + nodeid + "' " + searchCondition;
        String countSql = "select count(nodeid) from v_index_detail sid where entryid in (select entry from st_storage where entry is not null and storestatus='" + staIn + "') and nodeid='" + nodeid + "' " + searchCondition;
        return getPageList(sql, countSql, page, limit, pageRequest);
    }

    public ExtMsg findEntryByBookmarks(String userid,String addstate){
        List<String> entryids = bookmarksRepository.findEntryidByUseridandAddstate(userid,addstate);
        SplitListUtil splitListUtil = new SplitListUtil();
        List<List<String>> splitEntryidsList = splitListUtil.splitList(entryids,2000);//对entryids按照2000分组，因为sqlserver查询时候In参数不得超过2000
        List<Tb_entry_index> list = new ArrayList<>();
        for(int i=0;i<splitEntryidsList.size();i++){
            String[] splitEntryids = new String[splitEntryidsList.get(i).size()];
            splitEntryidsList.get(i).toArray(splitEntryids);
            List<Tb_entry_index> entrylist = entryIndexRepository.findAllByAddstateIn(splitEntryids);
            if(entrylist != null){
                list.addAll(entrylist);
            }
        }
        return new ExtMsg(true,"",list);
    }

    public ExtMsg addBookmarks(String[] entryids, String userid,String addstate){
        List<String> bmList = bookmarksRepository.findWareEntryid(userid,addstate);
        List<String> noexists = new ArrayList<>();
        for (String entryid : entryids){
            if(!bmList.contains(entryid)){
                noexists.add(entryid);
            }
        }
        //已选数据不存在添加关联,数据则添加
        for(int i=0;i<noexists.size();i++){
            addBookmark(userid, noexists.get(i),addstate);
        }
        return new ExtMsg(true,"添加成功",null);
    }

    public ExtMsg deleteBookmarks(String[] entryids, String userid,String addstate){
        bookmarksRepository.deleteEntryByUseridandAndEntryid(entryids,userid,addstate);
        return new ExtMsg(true,"删除成功",null);
    }

    public Tb_entry_bookmarks addBookmark(String userid,String entryid,String addstate){
        Tb_entry_bookmarks bmObj=new Tb_entry_bookmarks();
        bmObj.setUserid(userid);
        bmObj.setEntryid(entryid);
        bmObj.setAddstate(addstate);//区分收藏和添加功能，1表示实体档案入库添加操作
        return bookmarksRepository.save(bmObj);
    }

    public Page<Map<String, Object>> findBorrows(int page, int limit){
        StringBuffer sb = new StringBuffer();
        sb.append("select sb.id,sb.entryid,tb.borrowdate,tb.borrowman,ei.archivecode,ei.title");
        sb.append(",concat(stz.roomdisplay,'-',sts.coldisplay,'-',sts.layerdisplay,'-',sts.sectiondisplay,'-',sts.sidedisplay) ");
        sb.append(" from st_borrow sb ");
        sb.append(" left join tb_borrowdoc tb on sb.docid = tb.docid");
        sb.append(" left join tb_entry_index ei on sb.entryid = ei.entryid");
        sb.append(" left join st_storage st on sb.stid = st.stid");
        sb.append(" left join st_zone_shelves sts on st.zone_shelves_shid = sts.shid");
        sb.append(" left join st_zones stz on sts.zoneid = stz.zoneid");
        sb.append(" where type = '实体借阅' and sb.status = '0'");
        sb.append(" order by borrowdate desc,archivecode desc");
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setFirstResult((page-1)*limit);
        query.setMaxResults(limit);
        List<Map<String, Object>> resultList = new LinkedList<>();
        List<Object[]> queryList = query.getResultList();
        queryList.stream().forEach(objects -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", objects[0]);
            map.put("entryid", objects[1]);
            map.put("borrowdate", objects[2]);
            map.put("borrowman", objects[3]);
            map.put("archivecode", objects[4]);
            map.put("title", objects[5]);
            map.put("position", objects[6]);
            map.put("nodefullname",classifySearchController.convertNodefullnameByEntryid(objects[1].toString()));
            resultList.add(map);
        });
        int count = resultList.size();
        return new PageImpl<>(resultList,new PageRequest(page-1, limit),count);
    }

    /**
     * 按档号找entryid和nodeid
     *
     * @param dhCode
     * @return
     */
    public String findIds(String dhCode) {
        List<Tb_entry_index> list = entryIndexRepository.findByArchivecode(dhCode);
        String msg = "";
        if (list.size() > 0) {
            msg = list.get(0).getEntryid() + "-" + list.get(0).getNodeid();
        }
        return msg;
    }

    /**
     * 按档号找entryid和nodeid
     *
     * @param dhCode
     * @return
     */
    public List<Tb_entry_index> findIdsAll(String[] dhCode) {
        List<Tb_entry_index> a = new ArrayList<>();
        List<Tb_entry_index> b = new ArrayList<>();
        List<String> id = new ArrayList<>();
        List<String> archivescode = new ArrayList<>();
        for(String str : dhCode){
            if(str.indexOf("id-")>-1) {
                String newStr = str.replaceAll("id-", "");
                id.add(newStr);
            }else if(str.indexOf("iniId")>-1){
                String iniID = str.replaceAll("iniId-", "");
                InWare iw = inWareService.findOne(iniID);
                Set<Storage> stSet = iw.getStorages();
                String entryMsg = "";
                if (stSet.size() > 0) {
                    for (Storage st : stSet) {
                        String entry = st.getEntry();
                        entryMsg += entry + ",";
                    }
                    entryMsg = entryMsg.substring(0, entryMsg.lastIndexOf(","));
                    String[] entrys = entryMsg.split(",");
                    return entryIndexRepository.findByEntryidIn(entrys);
                }
            }else {
                archivescode.add(str);
            }
        }
        if(id.size()>0) {
            a = entryIndexRepository.findByEntryidIn(id.toArray(new String[id.size()]));
        }
        b = entryIndexRepository.findByArchivecodeIn(dhCode);
        a.addAll(b);
        return a;
    }

    /**
     * 拆卷、件的删除
     *
     * @param entryid
     * @param nodeid
     * @param title
     * @param syncType
     */
    public String delEntry(String entryid, String nodeid, String title, String syncType) {
        Tb_entry_index tei = entryIndexRepository.findByEntryid(entryid);
        String archivecode = tei.getArchivecode();
        String innerfile = tei.getInnerfile();
        String filecode = tei.getFilecode();
        String recordcode = tei.getRecordcode();
        String zjPages = tei.getPages();
        String zjNodeid = tei.getNodeid();

        if (archivecode == null) {
            return "档号不能为空";
        }

        List<Tb_codeset> codeFieldList = codesetRepository.findByDatanodeidOrderByOrdernum(zjNodeid); // 获取档号设置字段集合
        if (codeFieldList.size() != 0) {
            Tb_codeset codeset = codeFieldList.get(codeFieldList.size() - 1); // 获取最后一个档号组成字段
            Object lastFieldValue = GainField.getFieldValueByName(codeset.getFieldcode(), tei);// 获取字段值
            if (lastFieldValue == null) {
                return codeset.getFieldname() + "不能为空";
            }
        } else {
            return "档号组成有误";
        }

        // 删除案卷或卷内文件
        entryDetailRepository.deleteByEntryid(entryid);
        electronicRepository.deleteByEntryid(entryid);
        entryIndexRepository.deleteByEntryid(entryid);

        String jnNodeid = "";
        if (syncType.equals("syncInnerFile") && title.equals("1")) {// 同步删除案卷的卷内文件
            // 先查找要删除的entyid
            jnNodeid = publicUtilService.getJnNodeid(zjNodeid);// 获取卷内nodeid
            List<String> entryids = entryIndexRepository.findAllByNodeid(archivecode, jnNodeid);
            String[] ids = entryids.toArray(new String[entryids.size()]);
            entryIndexRepository.deleteByEntryidIn(ids);
            entryDetailRepository.deleteByEntryidIn(ids);
            electronicRepository.deleteByEntryidIn(ids);
        }

        if (title.equals("1")) {// 案卷删除后排序
            // 先截取档号前端
            String parentArchivecode = archivecode.substring(0, archivecode.lastIndexOf(filecode));
            // 根据案卷号获取后边的相应案卷号的案卷(排除卷内文件)
            List<Tb_entry_index> ajNextList = entryIndexRepository.findAllByArchivecodeLikeAndNext(parentArchivecode,
                    filecode, zjNodeid);
            if (ajNextList.size() > 0) {
                for (int i = 0; i < ajNextList.size(); i++) {
                    // 拆件后，后边的案卷号全部减1
                    String filecode0 = ajNextList.get(i).getFilecode();
                    String archivecode0 = ajNextList.get(i).getArchivecode();
                    String filecode1 = "";
                    int num = filecode0.length();
                    try {
                        filecode1 = Integer.parseInt(filecode0) - 1 + "";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int newNum = filecode1.length();
                    // 重新拼接filecode
                    if (filecode1.length() < num) {
                        for (int j = 0; j < num - newNum; j++) {
                            filecode1 = 0 + filecode1;
                        }
                    }
                    int pNum = parentArchivecode.length();
                    if (syncType.equals("syncInnerFile")) {// 同步更新卷内
                        // 更新案卷和相关卷内的filecode
                        // 现获取该filecode的案卷和卷内,然后按新的案卷号filecode和新的档号archivecode一一更新
                        List<Tb_entry_index> ajOneList = entryIndexRepository
                                .findAllByArchivecodeAndFilecode(parentArchivecode, filecode0, zjNodeid, jnNodeid);
                        for (int k = 0; k < ajOneList.size(); k++) {
                            // 重新拼接archivecode
                            String archivecodeOne = ajOneList.get(k).getArchivecode();
                            String subArch = archivecodeOne.substring(pNum, archivecodeOne.length());// 获取案卷号和卷内号
                            String newSunArch = subArch.replaceFirst(filecode0, filecode1);// 新案卷号
                            String newArchivecode = parentArchivecode + newSunArch;
                            entryIndexRepository.updateFilecodeAndArchivecode(filecode1, newArchivecode, archivecodeOne,
                                    zjNodeid, jnNodeid);
                        }
                    } else {// 只更新案卷
                        String subArch = archivecode0.substring(pNum, archivecode0.length());// 获取案卷号和卷内号
                        String newSunArch = subArch.replaceFirst(filecode0, filecode1);// 新案卷号
                        String newArchivecode = parentArchivecode + newSunArch;
                        entryIndexRepository.updateFilecodeAndArchivecode(filecode1, newArchivecode, archivecode0,
                                zjNodeid);
                    }
                }
            }
        } else if (title.equals("2") || innerfile != null) {// 卷内文件删除
            // 更新案卷文件总数和总页数
            int jnadd = 0;
            updateAjuFile(archivecode, innerfile, jnadd, zjPages, zjNodeid, entryid);
            // 排序
            updateJnSqu(archivecode, innerfile, zjNodeid);
        } else if (title.equals("3") && innerfile == null) {
            updateAjiSqu(archivecode, recordcode, zjNodeid);// 案件排序 recordcode
        }
        return "拆件成功";
    }

    /**
     * 案件拆件排序
     */
    public void updateAjiSqu(String archivecode, String recordcode, String nodeid) {
        // 先截取档号前端
        String parentArchivecode = archivecode.substring(0, archivecode.lastIndexOf(recordcode));
        // 根据案卷号获取后边的相应件号的案卷(排除卷内文件)
        List<Tb_entry_index> ajNextList = entryIndexRepository.findAllByArchivecodeLikeAndNextRecord(parentArchivecode,
                recordcode, nodeid);
        if (ajNextList.size() > 0) {
            for (int i = 0; i < ajNextList.size(); i++) {
                // 拆件后，后边的案卷号全部减1
                String recordcode0 = ajNextList.get(i).getRecordcode();
                String archivecode0 = ajNextList.get(i).getArchivecode();
                String recordcode1 = "";
                int num = recordcode0.length();
                try {
                    recordcode1 = Integer.parseInt(recordcode0) - 1 + "";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int newNum = recordcode1.length();
                // 重新拼接recordcode
                if (recordcode1.length() < num) {
                    for (int j = 0; j < num - newNum; j++) {
                        recordcode1 = 0 + recordcode1;
                    }
                }
                int pNum = parentArchivecode.length();
                String subArch = archivecode0.substring(pNum, archivecode0.length());// 获取件号
                String newSunArch = subArch.replaceFirst(recordcode0, recordcode1);// 新件号
                String newArchivecode = parentArchivecode + newSunArch;
                entryIndexRepository.updateRecordcodeAndArchivecode(recordcode1, newArchivecode, archivecode0, nodeid);
            }
        }
    }

    /**
     * 卷内文件拆件排序
     */
    public void updateJnSqu(String archivecode, String innerfile, String nodeid) {
        // 先截取档号前端
        String parentArchivecode = archivecode.substring(0, archivecode.lastIndexOf(innerfile));

        // 获取案件之后的其他同一卷内的卷内文件，然后给他们的innerfile和档号-1
        List<Tb_entry_index> jnNextList = entryIndexRepository.findInnerByArchivecodeLikeAndNext(parentArchivecode,
                innerfile, nodeid);
        if (jnNextList.size() > 0) {
            for (int i = 0; i < jnNextList.size(); i++) {
                // 拆件后，后边的卷内号全部减1
                String innerfile0 = jnNextList.get(i).getInnerfile();
                String archivecodeOne = jnNextList.get(i).getArchivecode();
                String innerfile1 = "";
                int num = innerfile0.length();
                try {
                    innerfile1 = Integer.parseInt(innerfile0) - 1 + "";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int newNum = innerfile1.length();
                // 重新拼接filecode
                if (innerfile1.length() < num) {
                    for (int j = 0; j < num - newNum; j++) {
                        innerfile1 = 0 + innerfile1;
                    }
                }
                int pNum = parentArchivecode.length();
                String subArch = archivecodeOne.substring(pNum, archivecodeOne.length());// 获取案卷号和卷内号
                String newSunArch = subArch.replaceFirst(innerfile0, innerfile1);// 新案卷号
                String newArchivecode = parentArchivecode + newSunArch;
                entryIndexRepository.updateInnerfileAndArchivecode(innerfile1, newArchivecode, archivecodeOne, nodeid);
            }
        }
    }

    /**
     * 更新案卷的文件总数和总页数
     */
    public void updateAjuFile(String archivecode, String innerfile, int jnadd, String zjPages, String nodeid,
                              String entryid) {
        // 根据archivecode和innerfile去截取母卷的archivecode
        String archiveTemp = archivecode.substring(0, archivecode.lastIndexOf(innerfile));// 母卷加分割符（分割符长度1）
        // 获取对应案卷的nodeid
        String filenodeid = publicUtilService.getFileNodeid(nodeid);
        // 先判断有没有母卷，没有就直接退出
        List<Tb_entry_index> teis = entryIndexRepository.findByArchivecodeAndNodeid(
                archivecode.substring(0, archivecode.lastIndexOf(innerfile) - 1), filenodeid);
        if (teis.size() < 1) {
            return;
        }

        String archivecodeMj = archivecode.substring(0, archiveTemp.length() - 1);
        // 根据档号去查找所有的子件集合，然后再去修改母卷的文件数和总页数
        List<Tb_entry_index> zjList = entryIndexRepository
                .findAllByArchivecodeLikeAndNodeidOrderByArchivecode(archiveTemp, nodeid);
        // Tb_entry_detail f02文件总数 Tb_entry_index pages 总页数
        String fileSize = zjList.size() + "";
        String pages = "0";
        int pageNum = 0;
        int pageNo = 0;
        if (zjList.size() > 0) {
            for (int i = 0; i < zjList.size(); i++) {
                String pageS = zjList.get(i).getPages();// 页数
                String pageN = zjList.get(i).getPageno();// 页号
                String zjEntryid = zjList.get(i).getEntryid();
                String zjInnerfile = zjList.get(i).getInnerfile();
                // 顺便修改子件的页号
                int pno = 0;
                try {
                    if (!(zjPages == null || "".equals(zjPages.trim()))) {
                        pno = Integer.valueOf(zjPages);
                    }
                    if (pno > 0) {// 拆插件页数>0才执行
                        if (zjInnerfile.compareTo(innerfile) > 0 || entryid.equals(zjEntryid)) {
                            int zjpno = Integer.valueOf(pageN);
                            // 1.插件，后边的页号都加一个插件页数
                            if (jnadd == 1) {
                                pageN = zjpno + pno + "";
                            }
                            // 2.拆件，后边的页号都减一个拆件页数
                            if (jnadd == 0) {
                                if (zjpno > pno) {
                                    pageN = zjpno - pno + "";
                                } else {
                                    pageN = "";// 页号小于拆插件页数，设置页号为空
                                }
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // String zjPageNo=pageNum+1+"";
                entryIndexRepository.updatePagenoByEntryid(zjEntryid, pageN);
                try {
                    pageNum += Integer.parseInt(pageS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            pages = pageNum + "";
        }

        // 更新总页数
        entryIndexRepository.updatePagesByArchivecode(archivecodeMj, pages, filenodeid);
        // 更新文件总数
        entryDetailRepository.updatePagesByArchivecode(archivecodeMj, fileSize, filenodeid);
    }

    /**
     * 案件插件排序
     */
    public void updateAjiSquChji(String archivecode, String recordcode, String nodeid) {
        // 先截取档号前端
        String parentArchivecode = archivecode.substring(0, archivecode.lastIndexOf(recordcode));
        // 根据案卷号获取后边的相应件号的案卷(排除卷内文件)
        List<Tb_entry_index> ajNextList = entryIndexRepository.findAllByArchivecodeLikeAndNextRecord(parentArchivecode,
                recordcode, nodeid);
        if (ajNextList.size() > 0) {
            for (int i = ajNextList.size() - 1; i >= 0; i--) {
                // 插件后，后边的案卷号全部+1
                String recordcode0 = ajNextList.get(i).getRecordcode();
                String archivecode0 = ajNextList.get(i).getArchivecode();
                String recordcode1 = "";
                int num = recordcode0.length();
                try {
                    recordcode1 = Integer.parseInt(recordcode0) + 1 + "";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int newNum = recordcode1.length();
                // 重新拼接recordcode
                if (recordcode1.length() < num) {
                    for (int j = 0; j < num - newNum; j++) {
                        recordcode1 = 0 + recordcode1;
                    }
                }
                int pNum = parentArchivecode.length();
                String subArch = archivecode0.substring(pNum, archivecode0.length());// 获取件号
                String newSunArch = subArch.replaceFirst(recordcode0, recordcode1);// 新件号
                String newArchivecode = parentArchivecode + newSunArch;
                entryIndexRepository.updateRecordcodeAndArchivecode(recordcode1, newArchivecode, archivecode0, nodeid);
            }
        }
    }

    /**
     * 卷内文件案件插件排序
     */
    public void updateJnAjiSquChji(String archivecode, String innerfile, String nodeid, String pages) {
        // 先截取档号前端
        String parentArchivecode = archivecode.substring(0, archivecode.lastIndexOf(innerfile));
        // 根据案卷号获取后边的相应件号的案卷(排除卷内文件)
        List<Tb_entry_index> ajNextList = entryIndexRepository.findInnerByArchivecodeLikeAndNext(parentArchivecode,
                innerfile, nodeid);
        if (ajNextList.size() > 0) {
            for (int i = ajNextList.size() - 1; i >= 0; i--) {
                // 插件后，后边的案卷号全部+1,页号全部加个页数
                String innerfile0 = ajNextList.get(i).getInnerfile();
                String archivecode0 = ajNextList.get(i).getArchivecode();
                String pageNo = ajNextList.get(i).getPageno();
                String innerfile1 = "";
                int num = innerfile0.length();
                try {
                    innerfile1 = Integer.parseInt(innerfile0) + 1 + "";
                    // pageNo=Integer.parseInt(pages)+Integer.parseInt(pageNo)+"";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int newNum = innerfile1.length();
                // 重新拼接recordcode
                if (innerfile1.length() < num) {
                    for (int j = 0; j < num - newNum; j++) {
                        innerfile1 = 0 + innerfile1;
                    }
                }
                int pNum = parentArchivecode.length();
                String subArch = archivecode0.substring(pNum, archivecode0.length());// 获取件号
                String newSunArch = subArch.replaceFirst(innerfile0, innerfile1);// 新件号
                String newArchivecode = parentArchivecode + newSunArch;
                entryIndexRepository.updateInnerfileAndArchivecode(innerfile1, newArchivecode, archivecode0, nodeid);
                // entryIndexRepository.updateInnerfileAndArchivecodeAndPageNo(innerfile1,
                // newArchivecode, archivecode0, nodeid,pageNo);
            }
        }
    }

    /**
     * 档号更新
     */
    public String getArchivecode(String oldArch, Integer pNum, String oldCode, String newCode, String parentArch) {
        String subArch = oldArch.substring(pNum, oldArch.length());// 获取案卷号和卷内号
        String newSunArch = subArch.replaceFirst(oldCode, newCode);// 新案卷号
        return parentArch + newSunArch;
    }

    /**
     * 页数矫正,需求：传入的文件级记录集执行页数矫正(例如 2条记录，顺序号分别为001，002，页号分别为5，10,则第1条执行矫正后页数赋值为5)
     * 处理的条件：档号和页号字段有值；顺序号/件号相连的记录的页号、页数有值；
     * <p>
     * 根据档号设置组成字段，匹配去查找当前比较条目的下一条目，即查找卷内顺序号+1的条目，页数=匹配到的条目页号-当前条目的页号
     * 不予处理的条件，并提示信息“矫正失败，请检查档号、页号数据是否规范正确!”：
     * 档号设置不存在、查找失败，即找不到下一条、档号重复(查找的条目存在重复的档号)、没有页号、页号比原来的小(计算结果为负数)
     *
     * @param entryids 选择的记录ID
     * @return
     */
    public String pgNumCorrect(String entryids) {
        StringBuffer msg = new StringBuffer();
        String[] entryidData = entryids.split("、");
        // 根据entryid获取条目列表
        List<Tb_entry_index> entryIndexs = entryIndexRepository.findByEntryidIn(entryidData);
        String nodeid = "";
        Integer number = 0;
        List<String> codesetFieldCodes = new ArrayList<>();
        List<String> codeSettingSplitCodes = new ArrayList<>();
        if (entryIndexs.size() > 0) {
            nodeid = entryIndexs.get(0).getNodeid();
            // 获取计算项单位长度
            number = codesettingService.getLastCalFieldLength(nodeid);
            // 获取档号组成字段
            codesetFieldCodes = codesetRepository.findFieldcodeByDatanodeid(nodeid);
            // 获取档号设置分割符号
            codeSettingSplitCodes = codesettingService.getCodeSettingSplitCodes(nodeid);
        }
        if (codesetFieldCodes.size() == 0 || codeSettingSplitCodes.size() == 0) {
            msg.append("矫正失败，请检查档号、页号数据是否规范正确!<br/>");
            msg.append("失败记录：" + entryids);
            return msg.toString();
        }
        //正规纯数字组成
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        //正规范围型(036/037)
        Pattern backslash = Pattern.compile("^(\\d+)[//](\\d+)$");
        //正规范围型(036-037)
        Pattern bars = Pattern.compile("^(\\d+)[-](\\d+)$");
        for (int i = 0; i < entryIndexs.size(); i++) {
            String pageno = entryIndexs.get(i).getPageno();
            ////判断页号是否为空
            if ("".equals(pageno) || pageno == null) {
                pgNumMsg(msg, entryIndexs, i);
                continue;
            }
            //不符合036/037或036-037规则且不是数字
            if (!backslash.matcher(pageno).matches() && !bars.matcher(pageno).matches() && !pattern.matcher(pageno).matches()) {
                pgNumMsg(msg, entryIndexs, i);
                continue;
            }
            String[] pagenos = null;
            //如果符合036/037
            if (backslash.matcher(pageno).matches()) {
                pagenos = pageno.split("/");
            } else if (bars.matcher(pageno).matches()) {//如果符合036-037
                pagenos = pageno.split("-");
            }
            if (pagenos != null && pagenos.length > 1) {
                //判断是否属于错误范围型 ，如 037/036，就当异常信息处理
                if (Integer.parseInt(pagenos[0]) > Integer.parseInt(pagenos[1])) {
                    pgNumMsg(msg, entryIndexs, i);
                    continue;
                }
                //若符合036/0037,则页数为（037-036）+1=2
                String pages = String.valueOf(Integer.parseInt(pagenos[1]) - Integer.parseInt(pagenos[0]) + 1);
                entryIndexs.get(i).setPages(pages);
                continue;
            }
            // 获取每个构成字段的值
            List<String> codesetFieldValues = new ArrayList<>();
            // 获取最后一项值
            String calValue = "";
            Map<String, Map<String, String>> mapFiled = getConfigMap();//获取参数设置的MAP
            List<Tb_data_template> enumList = templateRepository.getByNodeidFtype("enum", nodeid);//获取某节点的模板中属于enum的字段
            for (int j = 0; j < codesetFieldCodes.size(); j++) {
                String codesetFieldValue = (String) GainField.getFieldValueByName(codesetFieldCodes.get(j),
                        entryIndexs.get(i));
                codesetFieldValue = getConfigByName(codesetFieldCodes.get(j), codesetFieldValue, enumList, mapFiled);
                codesetFieldValues.add(codesetFieldValue);
                if (j == codesetFieldCodes.size() - 1) {
                    calValue = codesetFieldValue;
                }
            }
            // 判断最后一项是否是数字
            if (calValue == null || "".equals(calValue) || !pattern.matcher(calValue).matches()) {
                pgNumMsg(msg, entryIndexs, i);
                continue;
            } else {
                // 最后一个计算项+1
                calValue = alignValue(number, Integer.valueOf(calValue) + 1);
                // 生成新的档号
                String archivecode = produceArchivecode(codesetFieldValues, codeSettingSplitCodes, calValue, nodeid);
                List<Tb_entry_index> entryIndexList = entryIndexRepository.findByArchivecode(archivecode);
                // 判断是否档号重复，即查找是否有多条相同档号的条目
                if (entryIndexList.size() == 0 || entryIndexList.size() > 1) {
                    pgNumMsg(msg, entryIndexs, i);
                    continue;
                } else {
                    Tb_entry_index find = entryIndexList.get(0);
                    //判断有没有页号
                    if ("".equals(find.getPageno()) || find.getPageno() == null) {
                        pgNumMsg(msg, entryIndexs, i);
                        continue;
                    }
                    String[] findPagenos = null;
                    //如果符合036/037
                    if (backslash.matcher(find.getPageno()).matches()) {
                        findPagenos = find.getPageno().split("/");
                    } else if (bars.matcher(find.getPageno()).matches()) {//如果符合036-037
                        findPagenos = find.getPageno().split("-");
                    }
                    if (findPagenos == null) {
                        //页号比原来条目还要小
                        if (!pattern.matcher(find.getPageno()).matches
                                () || Integer.parseInt(find.getPageno()) < Integer.parseInt(pageno)) {
                            pgNumMsg(msg, entryIndexs, i);
                            continue;
                        }

                        String pages = String.valueOf(Integer.parseInt(find.getPageno()) - Integer.parseInt(pageno));
                        entryIndexs.get(i).setPages(pages);
                    } else {
                        // 判断是否属于错误范围型 ，如 037/036，就当异常信息处理
                        //判断当前条目的页号是否小于（如036/037）036
                        if (Integer.parseInt(findPagenos[0]) > Integer.parseInt
                                (findPagenos[1]) || Integer.parseInt(findPagenos[0]) < Integer.parseInt(pageno)) {
                            pgNumMsg(msg, entryIndexs, i);
                            continue;
                        }
                        //若符合036/0037,则页数为036-当前条目的页号
                        String pages = String.valueOf(Integer.parseInt(findPagenos[0]) - Integer.parseInt(pageno));
                        entryIndexs.get(i).setPages(pages);
                    }
                }
            }
        }
        return msg.toString();
    }

    private void pgNumMsg(StringBuffer msg, List<Tb_entry_index> entryIndexs, int i) {
        if ("".equals(msg.toString())) {
            msg.append("矫正失败，请检查档号、页号数据是否规范正确!<br/>");
            msg.append("失败记录：" + entryIndexs.get(i).getArchivecode());
        } else {
            msg.append("、" + entryIndexs.get(i).getArchivecode());
        }
    }

    /**
     * 统计项更新
     *
     * @param entryidData
     * @return
     */
    public String statisticUpdate(String[] entryidData) {
        String info = "操作完成!";
        List<Tb_entry_index> list = entryIndexRepository.findByEntryidIn(entryidData);
        // 数据节点ID和档号分隔符的值对,例:"4028e681636cd73101636d3e80e41b69","-"
        Map<String, String> nodeid_splitcode_map = new Hashtable<>();
        String splitCode = "-";

        for (Tb_entry_index entry : list) {
            String archivecode = entry.getArchivecode();
            String nodeid = entry.getNodeid();
            // 未缓存档号分隔符时去获取
            if (!nodeid_splitcode_map.containsKey(nodeid)) {
                // 获取当前记录的档号设置
                List<Tb_codeset> codesetList = codesettingService.findCodesetByDatanodeid(nodeid);
                if (codesetList.size() > 0) {
                    nodeid_splitcode_map.put(nodeid, ((Tb_codeset) codesetList.get(0)).getSplitcode());
                }
            }
            splitCode = nodeid_splitcode_map.get(nodeid).toString();
            if ("".equals(splitCode)) {
                info += "档号设置未找到!" + entry.getArchivecode() + "\r";
                splitCode = "-";
            }
            int innerfileNumber = entryIndexRepository.countInnerfileNumberByArchivecode(archivecode + splitCode);
            //Object object = entryIndexRepository.sumInnerfilePagesByArchivecode(archivecode + splitCode);
            Query query = entityManager.createNativeQuery("select sum("
                    + DBCompatible.getInstance().findExpressionOfToNumber("pages")
                    + ") from tb_entry_index  where archivecode like '" + (archivecode + splitCode) + "%'");
            Object object = query.getSingleResult();
            int innerfilePageNumber = object == null ? 0 : Double.valueOf(String.valueOf(object)).intValue();
            info += "档号：" + entry.getArchivecode() + "\r" + "卷内文件数修改为：" + innerfileNumber + " 条\t" + "卷内总页数修改为："
                    + innerfilePageNumber;
            logger.info("===============" + info);
            // 更新案卷记录
            entry.setPages(String.valueOf(innerfilePageNumber));
            Tb_entry_detail entryDetail = entryDetailRepository.findByEntryid(entry.getEntryid());
            entryDetail.setF02(String.valueOf(innerfileNumber));
            entryIndexRepository.save(entry);
            entryDetailRepository.save(entryDetail);
        }
        return info;
    }

    private String convertSeqCode(String seqCode, String patchStr, int strLength) {
        int seqCodeLength = seqCode.length();
        for (int i = seqCodeLength; i < strLength; i++) {
            seqCode = patchStr + seqCode;
        }
        return seqCode;
    }

    public EntryCapture getNewFileFormData(String entryid, String archivecode, String nodeid) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Tb_entry_index index = entryIndexRepository.findByEntryid(entryid);
        // 获取同档号的条目集合
        List<Tb_entry_index> entryIndices = entryIndexRepository.findAllByNodeidAndArchivecodeLike(index.getNodeid(),
                archivecode);
        String organid = getOrganidByNodeidLoop(nodeid);// 机构id
        String organ = organService.findOrganByOrganid(organid);// 机构名称
        String funds = fundsService.getOrganFunds(organid);// 全宗号
        EntryCapture entry = new EntryCapture();
        entry.setNodeid(nodeid);
        // 获取真实姓名
        entry.setDescriptionuser(userDetails.getRealname());
        // 设置起止时间，提取同案卷档号的卷内记录的最早和最晚的文件日期，作为案卷记录的起止时间
        String fieldate = templateRepository.findFieldCodeByNodeidAndFieldName(nodeid, "起止年月");
        if(fieldate!=null){
            GainField.setFieldValueByName(fieldate, entry, getMaxMinDate(entryIndices));
        }
        // 设置卷内文件数，即同案卷的卷内文件记录数
        String num = templateRepository.findFieldCodeByNodeidAndFieldName(nodeid, "卷内文件数");
        if(num!=null){
            GainField.setFieldValueByName(num, entry, String.valueOf(entryIndices.size()));
        }
        // 设置机构名称
        entry.setOrgan(organ);
        // 设置全宗号
        entry.setFunds(funds);

        // 获取当前节点的档号设置字段
        List<String> codeSet = codesetRepository.findFieldcodeByDatanodeid(nodeid);
        List<String> codeInfo = new ArrayList<>();
        codeInfo.add("catalog");
        codeInfo.add("filecode");
        codeInfo.add("entryretention");
        codeInfo.removeAll(codeSet);
        codeSet.addAll(codeInfo);

        for (int i = 0; i < codeSet.size(); i++) {
        	String value = (String) GainField.getFieldValueByName(codeSet.get(i), index);
        	GainField.setFieldValueByName(codeSet.get(i), entry, value);
        }

        int pages = 0;
        // 设置页数，合计同案卷的卷内文件的页数
        for (Tb_entry_index mIndex : entryIndices) {
            int page;
            try {
                page = Integer.valueOf(mIndex.getPages());
                pages = pages + page;
            } catch (Exception e) {
            }
        }
        entry.setPages(String.valueOf(pages));
        return entry;
    }

    private String getMaxMinDate(List<Tb_entry_index> entryIndices) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String date = "";

        List<Date> dates = new ArrayList<>();
        for (int i = 0; i < entryIndices.size(); i++) {
            try {
                // 能转换成日期格式才进行日期大小判断
                dates.add(df.parse(entryIndices.get(i).getFiledate()));
            } catch (Exception e) {

            }
        }
        if (dates.size() == 0) {
            return date;
        }
        Date max = dates.get(0);// 默认第一个最大
        Date min = dates.get(0);// 默认第一个最小
        for (int i = 1; i < dates.size(); i++) {
            if (dates.get(i).getTime() > max.getTime()) {
                max = dates.get(i);
            }
            if (dates.get(i).getTime() < min.getTime()) {
                min = dates.get(i);
            }
        }
        date = df.format(min) + "-" + df.format(max);
        return date;
    }

    public Tb_entry_index saveIndex(Tb_entry_index tb_entry_index) {
        return entryIndexRepository.save(tb_entry_index);
    }

    public Tb_entry_detail saveEntryDetail(Tb_entry_detail tb_entry_detail) {
        return entryDetailRepository.save(tb_entry_detail);
    }

    /**
     * 根据condition、content拼接成where条件sql(仅限LIKE)
     *
     * @param condition 字段
     * @param content   内容
     * @param alias     表的别名 如：te
     * @return
     */
    public String getSqlByConditions(String condition, String content, String alias) {
        String str = "";
        if (condition != null && content != null) {
            String[] conditions = condition.split(",");
            String[] contents = content.split(",");
            for (int i = 0; i < contents.length; i++) {
                if (i != 0) {
                    str += " and ";
                }
                if (alias != null && !alias.equals("")) {
                    str += (alias + ".");
                }
                str += conditions[i] + " like '%" + contents[i] + "%'";
            }
        }
        return str;
    }

    /**
     * 根据condition、content拼接成where条件sql(仅限LIKE,=,>,<)
     *
     * @param condition 字段
     * @param content   内容
     * @param alias     表的别名 如：te
     * @param op        条件符号字符串（equal，like，greaterThan，lessThan）
     * @return
     */
    public String getSqlByConditionsto(String condition, String content, String alias, String op) {
        String str = "";
        if (condition != null && content != null) {
            String[] conditions = condition.split(",");
            String[] contents = content.split(",");
            for (int i = 0; i < contents.length; i++) {
                if (i != 0) {
                    str += " and ";
                }
                if (alias != null && !alias.equals("")) {
                    str += (alias + ".");
                }
                if ("equal".equals(op)) {
                    str += conditions[i] + " = '" + contents[i] + "'";
                } else if ("like".equals(op)) {
                    str += conditions[i] + " like '%" + contents[i] + "%'";
                } else if ("greaterThan".equals(op)) {
                    str += conditions[i] + " > '" + contents[i] + "'";
                } else if ("lessThan".equals(op)) {
                    str += conditions[i] + " < '" + contents[i] + "'";
                }
            }
        }
        return str;
    }

    public int setPagesbyEntryid(String entryid, String pages) {
        return entryIndexRepository.updatePagesByEntryid(pages, entryid);
    }

    /**
     * 不在同一个类使用@Transactional？参考第三点：
     * https://blog.csdn.net/kinseygeek/article/details/54931710
     *
     * @param oaEntry
     * @param fileAry
     * @return
     */
    public String receiveImpl(OaEntry oaEntry, JSONArray fileAry) {
        String successTip = "成功：";
        Tb_entry_index entry_index = new Tb_entry_index();

        //1.保存条目index对象=========================================
        String nodeid = "";
        if ("receive".equals(oaEntry.getArchives_type())) {
            nodeid = receiveId;//收文
        } else if ("send".equals(oaEntry.getArchives_type())) {
            nodeid = sendId;//发文
        } else if ("signReport".equals(oaEntry.getArchives_type())) {
            nodeid = signReportId;//签报
        }
        entry_index.setNodeid(nodeid);
        entry_index.setEleid(fileAry.size() == 0 ? null : fileAry.size() + "");
        setIndexValue(entry_index, oaEntry);//字段值匹配
        entry_index.setTitle(entry_index.getTitle() + "OA_RECEIVE_FLAG_FILE");//临时待删
        entry_index = entryIndexRepository.save(entry_index);

        //2.保存条目detail对象=========================================
        Tb_entry_detail detail = new Tb_entry_detail();
        setDetailValue(detail, oaEntry);//字段值匹配
        detail.setEntryid(entry_index.getEntryid());
        detail.setF02(detail.getF02() + "OA_RECEIVE_FLAG_FILE");//临时待删
        entryDetailRepository.save(detail);
        LogOAReceive(successTip + "保存条目数据 （ARCHIVES_NO：" + oaEntry.getArchives_no() + "，题名:" + oaEntry.getTitle() + " ，ENTRY_INDEX表ID:" + entry_index.getEntryid() + "）", true, false);

        //3.复制附件；保存文件electronic对象============================
        if (fileAry.size() == 0) {
            return entry_index.getEntryid();
        }
        String dir = getStorageDir("management", entry_index.getEntryid());//电子文件存放目录
        EntryIndexService.dir = dir;
        for (int i = 0; i < fileAry.size(); i++) {
            JSONObject job = fileAry.getJSONObject(i);

            //复制文件
            decoderBase64File(job.get("FILE_CONTENT").toString(), rootpath + dir + "/" + job.get("FILE_NAME").toString());
            File srcFile = new File(rootpath + dir + "/" + job.get("FILE_NAME").toString());
            LogOAReceive(successTip + "复制文件 （ARCHIVES_NO：" + job.get("ARCHIVES_NO").toString() + "，FILE_NAME：" + job.get("FILE_NAME").toString() + "）", true, false);

            //插入记录
            Tb_electronic ele = new Tb_electronic();
            ele.setEntryid(entry_index.getEntryid());
            ele.setFilename(job.get("FILE_NAME").toString());
            ele.setFilepath(dir);
            ele.setFilefolder("/OARECEIVE" + getEletronicType(job.get("FILE_TYPE").toString()));//临时待改
            ele.setFilesize(String.valueOf(srcFile.length()));
            ele.setFiletype(srcFile.getName().substring(srcFile.getName().lastIndexOf('.') + 1));
            ele.setSortsequence(Integer.parseInt(job.get("FILE_NO").toString()));
            electronicRepository.save(ele);//为了详细打印日志，不用save(list)
            LogOAReceive(successTip + "插入电子文件记录 （ELEID：" + ele.getEleid() + "）", true, false);
        }
        return entry_index.getEntryid();
    }

    /**
     * 不在同一个类使用@Transactional？参考第三点：
     * https://blog.csdn.net/kinseygeek/article/details/54931710
     *
     * @param oaEntry
     * @param fileAry
     * @return
     */
    public String receiveCaptureImpl(OaEntry oaEntry, JSONArray fileAry) {
        String successTip = "成功：";
        Tb_entry_index_capture eic = new Tb_entry_index_capture();

        //1.保存条目index对象=========================================
        String nodeid = "";
        if ("receive".equals(oaEntry.getArchives_type())) {
            nodeid = receiveId;//收文
        } else if ("send".equals(oaEntry.getArchives_type())) {
            nodeid = sendId;//发文
        } else if ("signReport".equals(oaEntry.getArchives_type())) {
            nodeid = signReportId;//签报
        }
        eic.setNodeid(nodeid);
        eic.setEleid(fileAry.size() == 0 ? null : fileAry.size() + "");
        setIndexCaptureValue(eic, oaEntry);//字段值匹配
        eic.setTitle(eic.getTitle() + "OA_RECEIVE_FLAG_FILE");//临时待删
        eic = entryIndexCaptureRepository.save(eic);

        //2.保存条目detail对象=========================================
        Tb_entry_detail_capture detail_capture = new Tb_entry_detail_capture();
        setDetailCaptureValue(detail_capture, oaEntry);//字段值匹配
        detail_capture.setEntryid(eic.getEntryid());
        detail_capture.setF02(detail_capture.getF02() + "OA_RECEIVE_FLAG_FILE");//临时待删
        entryDetailCaptureRepository.save(detail_capture);
        LogOAReceive(successTip + "保存条目数据 （ARCHIVES_NO：" + oaEntry.getArchives_no() + "，题名:" + oaEntry.getTitle() + " ，ENTRY_INDEX_CAPTURE表ID:" + eic.getEntryid() + "）", true, false);

        //3.复制附件；保存文件electronic对象============================
        if (fileAry.size() == 0) {
            return eic.getEntryid();
        }
        String dir = getStorageDir("capture", eic.getEntryid());//电子文件存放目录
        EntryIndexService.dir = dir;
        for (int i = 0; i < fileAry.size(); i++) {
            JSONObject job = fileAry.getJSONObject(i);

            //复制文件
            decoderBase64File(job.get("FILE_CONTENT").toString(), rootpath + dir + "/" + job.get("FILE_NAME").toString());
            File srcFile = new File(rootpath + dir + "/" + job.get("FILE_NAME").toString());
            LogOAReceive(successTip + "复制文件 （ARCHIVES_NO：" + job.get("ARCHIVES_NO").toString() + "，FILE_NAME：" + job.get("FILE_NAME").toString() + "）", true, false);

            //插入记录
            Tb_electronic_capture elec = new Tb_electronic_capture();
            elec.setEntryid(eic.getEntryid());
            elec.setFilename(job.get("FILE_NAME").toString());
            elec.setFilepath(dir);
            elec.setFilefolder("/OARECEIVE" + getEletronicType(job.get("FILE_TYPE").toString()));//临时待改
            elec.setFilesize(String.valueOf(srcFile.length()));
            elec.setFiletype(srcFile.getName().substring(srcFile.getName().lastIndexOf('.') + 1));
            elec.setSortsequence(Integer.parseInt(job.get("FILE_NO").toString()));
            electronicCaptureRepository.save(elec);//为了详细打印日志，不用save(list)
            LogOAReceive(successTip + "插入电子文件记录 （ELEID：" + elec.getEleid() + "）", true, false);
        }
        return eic.getEntryid();
    }

    /**
     * 匹配设置字段值(entry_index)
     *
     * @param entry_index
     * @param oaEntry
     * @return
     */
    private Tb_entry_index setIndexValue(Tb_entry_index entry_index, OaEntry oaEntry) {
        Map<String, String> mapping = new HashMap<>();
        if (oaEntry == null) {
            return entry_index;
        }
        String type = oaEntry.getArchives_type();
        if ("receive".equals(type)) {// 收文
            mapping.put("title", "title");
            mapping.put("filedate", "description_date");
            mapping.put("filenumber", "file_number");
            mapping.put("catalog", "archive_class");
            mapping.put("entryretention", "entry_retention");
            mapping.put("pages", "page_count");
            mapping.put("fscount", "archives_count");
        } else if ("send".equals(type)) {//发文
            mapping.put("title", "title");
            mapping.put("filedate", "description_date");
            mapping.put("catalog", "archive_class");
            mapping.put("entryretention", "entry_retention");
            mapping.put("pages", "page_count");
            mapping.put("filenumber", "file_number");
            mapping.put("fscount", "archives_count");
        } else if ("signReport".equals(type)) {//签报
            mapping.put("title", "title");
            mapping.put("filedate", "description_date");
            mapping.put("filenumber", "file_number");
        }

        for (String key : mapping.keySet()) {
            String value = "";
            if (GainField.getFieldValueByName(mapping.get(key), oaEntry) != null) {
                value = GainField.getFieldValueByName(mapping.get(key), oaEntry) + "";
            }
            GainField.setFieldValueByName(key, entry_index, value);
        }
        return entry_index;
    }

    /**
     * 匹配设置字段值(entry_index)
     *
     * @param entry_index_capture
     * @param oaEntry
     * @return
     */
    private Tb_entry_index_capture setIndexCaptureValue(Tb_entry_index_capture entry_index_capture, OaEntry oaEntry) {
        Map<String, String> mapping = new HashMap<>();
        if (oaEntry == null) {
            return entry_index_capture;
        }
        String type = oaEntry.getArchives_type();
        if ("receive".equals(type)) {// 收文
            mapping.put("title", "title");
            mapping.put("filedate", "description_date");
            mapping.put("filenumber", "file_number");
            mapping.put("catalog", "archive_class");
            mapping.put("entryretention", "entry_retention");
            mapping.put("pages", "page_count");
            mapping.put("fscount", "archives_count");
        } else if ("send".equals(type)) {//发文
            mapping.put("title", "title");
            mapping.put("filedate", "description_date");
            mapping.put("catalog", "archive_class");
            mapping.put("entryretention", "entry_retention");
            mapping.put("pages", "page_count");
            mapping.put("filenumber", "file_number");
            mapping.put("fscount", "archives_count");
        } else if ("signReport".equals(type)) {//签报
            mapping.put("title", "title");
            mapping.put("filedate", "description_date");
            mapping.put("filenumber", "file_number");
        }

        for (String key : mapping.keySet()) {
            String value = "";
            if (GainField.getFieldValueByName(mapping.get(key), oaEntry) != null) {
                value = GainField.getFieldValueByName(mapping.get(key), oaEntry) + "";
            }
            GainField.setFieldValueByName(key, entry_index_capture, value);
        }
        return entry_index_capture;
    }

    /**
     * 匹配设置字段值(entry_detail)
     *
     * @param entry_detail
     * @param oaEntry
     * @return
     */
    private Tb_entry_detail setDetailValue(Tb_entry_detail entry_detail, OaEntry oaEntry) {
        Map<String, String> mapping = new HashMap<>();
        if (oaEntry == null) {
            return entry_detail;
        }
        mapping.put("f01", "archives_no");//固定
        String type = oaEntry.getArchives_type();
        if ("receive".equals(type)) {// 收文
            mapping.put("f12", "archives_type");
            mapping.put("f11", "department");
            mapping.put("f03", "lw_number");
            mapping.put("f04", "lw_unit");
            mapping.put("f06", "child_class");
            mapping.put("f08", "flag_notice");
            mapping.put("f09", "emergency");
        } else if ("send".equals(type)) {//发文
            mapping.put("f41", "sub_title");
            mapping.put("f15", "archives_type");
            mapping.put("f14", "department");
            mapping.put("f04", "description_user");
            mapping.put("f09", "child_class");
            mapping.put("f10", "main_sender");
            mapping.put("f11", "copy_sender");
            mapping.put("f12", "emergency");
            mapping.put("f13", "is_open");
            mapping.put("f07", "open_scope");
            mapping.put("f08", "print_count");
        } else if ("signReport".equals(type)) {//签报
            mapping.put("f11", "archives_type");
            mapping.put("f12", "department");
            mapping.put("f03", "description_user");
            mapping.put("f07", "emergency");
            mapping.put("f06", "main_sender");
        }

        for (String key : mapping.keySet()) {
            String value = "";
            if (GainField.getFieldValueByName(mapping.get(key), oaEntry) != null) {
                value = GainField.getFieldValueByName(mapping.get(key), oaEntry) + "";
            }
            GainField.setFieldValueByName(key, entry_detail, value);
        }
        return entry_detail;
    }

    /**
     * 匹配设置字段值(entry_detail)
     *
     * @param entry_detail_capture
     * @param oaEntry
     * @return
     */
    private Tb_entry_detail_capture setDetailCaptureValue(Tb_entry_detail_capture entry_detail_capture, OaEntry oaEntry) {
        Map<String, String> mapping = new HashMap<>();
        if (oaEntry == null) {
            return entry_detail_capture;
        }
        mapping.put("f01", "archives_no");//固定
        String type = oaEntry.getArchives_type();
        if ("receive".equals(type)) {// 收文
            mapping.put("f12", "archives_type");
            mapping.put("f11", "department");
            mapping.put("f03", "lw_number");
            mapping.put("f04", "lw_unit");
            mapping.put("f06", "child_class");
            mapping.put("f08", "flag_notice");
            mapping.put("f09", "emergency");
        } else if ("send".equals(type)) {//发文
            mapping.put("f41", "sub_title");
            mapping.put("f15", "archives_type");
            mapping.put("f14", "department");
            mapping.put("f04", "description_user");
            mapping.put("f09", "child_class");
            mapping.put("f10", "main_sender");
            mapping.put("f11", "copy_sender");
            mapping.put("f12", "emergency");
            mapping.put("f13", "is_open");
            mapping.put("f07", "open_scope");
            mapping.put("f08", "print_count");
        } else if ("signReport".equals(type)) {//签报
            mapping.put("f11", "archives_type");
            mapping.put("f12", "department");
            mapping.put("f03", "description_user");
            mapping.put("f07", "emergency");
            mapping.put("f06", "main_sender");
        }

        for (String key : mapping.keySet()) {
            String value = "";
            if (GainField.getFieldValueByName(mapping.get(key), oaEntry) != null) {
                value = GainField.getFieldValueByName(mapping.get(key), oaEntry) + "";
            }
            GainField.setFieldValueByName(key, entry_detail_capture, value);
        }
        return entry_detail_capture;
    }

    /**
     * 打印日志信息
     *
     * @param info       打印信息
     * @param printTime  是否打印时间
     * @param printSplit 是否分割线
     */
    private void LogOAReceive(String info, boolean printTime, boolean printSplit) {
        File dir = new File(rootpath + "/ReceiveOAFile/ReceiveLog");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String curMonth = new SimpleDateFormat("yyyyMM").format(new Date());
        File file = new File(rootpath + "/ReceiveOAFile/ReceiveLog/OAReceive_" + curMonth + ".log");//每月一个日志文件
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(file, true), "GBK");
            String time = "";
            if (printTime) {
                time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "：";
            }
            writer.write(time + info + "\n");
            if (printSplit) {
                writer.write("--------------------------------------------------------------------------------------------------------------\n");
            }
            writer.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解码还原文件
     *
     * @param base64Code
     * @param targetPath
     * @throws Exception
     */
    public void decoderBase64File(String base64Code, String targetPath) {
        try {
            byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
            FileOutputStream out = new FileOutputStream(targetPath);
            out.write(buffer);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取附件类型
    public String getEletronicType(String folder) {
        switch (folder) {
            case "receive_content":
            case "signReport_content":
                folder = "正文";
                break;
            case "receive_file":
            case "send_file":
            case "signReport_file":
                folder = "附件";
                break;
            case "receive_approval":
            case "send_approval":
            case "signReport_approval":
                folder = "审批表";
                break;
            case "send_content":
                folder = "定稿";
                break;
            case "send_content_pdf":
                folder = "发文稿";
                break;
        }
        return folder;
    }

    /**
     * 获取目录，entryid区分,不存在则创建
     *
     * @param entrytype
     * @param entryid
     * @return
     */
    private String getStorageDir(String entrytype, String entryid) {
        Calendar cal = Calendar.getInstance();
        String path = "/electronics/storages/" + cal.get(Calendar.YEAR) + "/" +
                (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE) + "/" + entrytype + "/" + entryid.trim();
        File eleDir = new File(rootpath + path);
        if (!eleDir.exists()) {
            eleDir.mkdirs();
        }
        return path;
    }

    public List<RebackMissPageCheck> getMissPageCheck(String[] ids) {
        List<Tb_entry_index> index_captures = entryIndexRepository.findByEntryidIn(ids);
        List<RebackMissPageCheck> rebackMissPageChecks = new ArrayList<>();
        for (Tb_entry_index index_capture : index_captures) {
            RebackMissPageCheck missPageCheck = new RebackMissPageCheck();
            String eleid = index_capture.getEleid();
            if (eleid != null && index_capture.getPages() != null && !"".equals(index_capture.getPages()) &&
                    !"".equals(eleid.trim())) {
                eleid=eleid.replaceAll(" ","");//清除所有空格
                if (index_capture.getPages().equals(eleid)) {
                    missPageCheck.setResult("<span style ='color:green'>通过</span>");
                } else if (Integer.parseInt(index_capture.getPages()) < Integer.parseInt(eleid)) {
                    int count = Integer.parseInt(eleid) - Integer.parseInt(index_capture.getPages());
                    missPageCheck.setResult("多" + count + "页");
                } else {
                    int count = Integer.parseInt(index_capture.getPages()) - Integer.parseInt(eleid);
                    missPageCheck.setResult("<span style ='color:red'>漏" + count + "页</span>");
                }
            }
            missPageCheck.setArchivecode(index_capture.getArchivecode());
            missPageCheck.setPage(index_capture.getPages());
            missPageCheck.setElenumber(eleid);
            missPageCheck.setId(index_capture.getEntryid());
            rebackMissPageChecks.add(missPageCheck);
        }
        return rebackMissPageChecks;
    }

    public int[] getMissPageCheckTotal(String[] ids) {
        int total = 0;
        int pagetotal = 0;
        int eletotal = 0;
        List<String[]> subAry = new InformService().subArray(ids, 1000);
        for (String[] ary : subAry) {
            List<Tb_entry_index> index_captures = entryIndexRepository.findByEntryidIn(ary);
            for (Tb_entry_index index_capture : index_captures) {
                if (index_capture.getPages() != null && !"".equals(index_capture.getPages())) {
                    pagetotal = pagetotal + Integer.parseInt(index_capture.getPages());
                }
                String eleid = index_capture.getEleid();
                if (eleid != null && !"".equals(eleid.trim())) {
                    eleid=eleid.replaceAll(" ","");//清除所有空格
                    eletotal = eletotal + Integer.parseInt(eleid);
                }
            }
            total = total + index_captures.size();
        }
        int[] number = new int[3];
        number[0] = total;
        number[1] = pagetotal;
        number[2] = eletotal;
        return number;
    }

    /**
     * 保存电子文件版本
     */
    public Tb_electronic_version saveElectronicVersion(Tb_electronic_version eleversion,String eleid) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Tb_electronic electronic = electronicRepository.findByEleid(eleid);
        // 获取原来电子文件
        File targetFile = new File(rootpath + electronic.getFilepath(), electronic.getFilename());
        // 获取新的存储电子文件路径
        String filepath = electronicService.getUploadDirSaveVersion(eleversion.getVersion(),eleid,"management")
                .replace(rootpath, "");
        // 把之前原来电子文件复制到存储路径
        File newFile = new File(rootpath + filepath, eleversion.getVersion()+"-"+electronic.getFilename());
        try {
            FileUtils.copyFile(targetFile,newFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        long filesize = Long.parseLong(electronic.getFilesize());
//        float size = (float) filesize/1024;
//        DecimalFormat df = new DecimalFormat("0.0");
//        String newsize = df.format(size);
        eleversion.setFilesize(electronic.getFilesize());
        eleversion.setFilepath(filepath);
        eleversion.setCreatename(userDetails.getLoginname());
        eleversion.setCreatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        eleversion.setEleid(eleid);
        eleversion.setFilename(eleversion.getVersion()+"-"+electronic.getFilename());
        eleversion.setFiletype(electronic.getFiletype());
        eleversion.setEntryid(electronic.getEntryid());
        Tb_electronic_version electronic_version = electronicVersionRepository.save(eleversion);
        return electronic_version;
    }

    /**
     * 根据电子文件id获取电子文件历史版本
     */
    public Page<Tb_electronic_version> getElectronicVersion(String eleid,int page,int limit,Sort sort) {
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort(Sort.Direction
                .DESC, "createtime") : sort);
        return electronicVersionRepository.findByEleid(eleid,pageRequest);
    }

    /**
     * 根据电子文件版本id删除电子文件历史版本
     */
    public int delElectronicVersion(String[] eleVersions) {
        List<Tb_electronic_version> electronic_versions = electronicVersionRepository.findByIdIn(eleVersions);
        for(int i=0;i<electronic_versions.size();i++){
            File targetFile = new File(rootpath + electronic_versions.get(i).getFilepath(), electronic_versions.get(i).getFilename());
            targetFile.delete();
        }
        return electronicVersionRepository.deleteByIdIn(eleVersions);
    }

    /**
     * 根据电子文件版本id回滚到此版本
     */
    public void rebackElectronicVersion(String eleVersionid) {
        Tb_electronic_version electronic_version = electronicVersionRepository.findById(eleVersionid);
        Tb_electronic electronic = electronicRepository.findByEleid(electronic_version.getEleid());
        try {
            // 获取原来电子文件
            File oldFile = new File(rootpath + electronic.getFilepath(), electronic.getFilename());
            oldFile.delete();

            // 目标电子文件
            File targetFile = new File(rootpath + electronic.getFilepath(), electronic_version.getFilename());
            // 获取回滚版本的电子文件
            File newFile = new File(rootpath + electronic_version.getFilepath(), electronic_version.getFilename());

            FileUtils.copyFile(newFile, targetFile);

            electronic.setFilename(electronic_version.getFilename());
            electronic.setFilesize(String.valueOf(newFile.length()));
            electronic.setFiletype(electronic_version.getFiletype());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Tb_electronic_version> getEleVersionByids(String[] eleVersionids) {
        return electronicVersionRepository.findByIdIn(eleVersionids);
    }

    public Tb_electronic_version getEleVersionByid(String eleVersionid) {
        return electronicVersionRepository.findById(eleVersionid);
    }

    /**
     * 压缩文件
     *
     * @return
     */
    public String transFiles(String[] eleVersionids) throws IOException {
        //定义下载压缩包名称
        String zipname = "EleVersion" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

        //文件复制
        List<Tb_electronic_version> electronic_versions = getEleVersionByids(eleVersionids);
        String desPath = null;
        for (Tb_electronic_version ele_Version : electronic_versions) {
            String selectionFilename = ele_Version.getFilename();
            String selectionFilepath = rootpath + ele_Version.getFilepath();
            desPath = selectionFilepath.split(ele_Version.getVersion())[0] + "originalElectronic/" + new SimpleDateFormat("yyyy/M/d").format(new Date());
            desPath += File.separator + zipname;
            File srcFile = new File(selectionFilepath + File.separator + selectionFilename);
            File desFile = new File(desPath + File.separator + selectionFilename);
            FileUtils.copyFile(srcFile, desFile);
        }

        //文件压缩
        String transFilepath = desPath;//.substring(0,desPath.lastIndexOf(File.separator));//创建中转文件夹
        ZipUtil.zip(transFilepath.replaceAll("/", "\\\\"), transFilepath.replaceAll("/", "\\\\") + ".zip", "");//压缩
        String zipPath = transFilepath.replace("/", "\\") + ".zip";
        delFolder(transFilepath);
        return zipPath;
    }

    public static Specification<Tb_entry_index_manage> getSearchManageCondition(String[] entryidArr) {
        Specification<Tb_entry_index_manage> searchEntryidsCondition = null;
        searchEntryidsCondition = new Specification<Tb_entry_index_manage>() {
            @Override
            public Predicate toPredicate(Root<Tb_entry_index_manage> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                CriteriaBuilder.In in = criteriaBuilder.in(root.get("entryid"));
                for (String entryid : entryidArr) {
                    in.value(entryid);
                }
                return criteriaBuilder.or(in);
            }
        };
        return searchEntryidsCondition;
    }

    public Page getEntrybaseManage(String datasoure,String nodeids, String condition, String operator, String content,
                                              Tb_index_detail_manage formConditions,Tb_index_detail detailformConditions,Tb_entry_index_sx sxformConditions,
                                                           ExtOperators formOperators, ExtDateRangeData daterangedata, String logic, int page,
                                                           int limit, Sort sort) {
        String nodeidStr = " and sid.nodeid ='" + nodeids + "' ";
        //高级检索用
        String dataStr = ClassifySearchService.getDateRangeCondition(daterangedata);
        String formStr = ClassifySearchService.getFormAdvancedSearch(detailformConditions, formOperators, logic,formConditions,sxformConditions,datasoure);
        if (!"".equals(formStr)) {
            formStr = formStr.substring(1);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        String sortStr = "";//排序
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            if("eleid".equals(order.getProperty())){
                sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
            }else {
                sortStr = " order by " + order.getProperty() + " " + order.getDirection();
            }
        } else {
            sortStr = " order by archivecode desc, descriptiondate desc ";
        }
        String searchCondition = "";
        if (content != null && !"".equals(content.trim())) {//输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }

        String searchSql = nodeidStr + searchCondition + dataStr + formStr;
        if("directory".equals(datasoure)){  //数据源为目录管理
            Page<Tb_index_detail_manage> resultAll = simpleSearchDirectoryService.findDetailManage(page,limit,sort,searchSql);
            return classifySearchDirectoryController.convertManageNodefullnameAll(resultAll, pageRequest);
        }else if("management".equals(datasoure)){  //数据源为档案系统
            String sql = "select sid.entryid from tb_entry_index sid where 1=1 and flagopen in('原文开放','条目开放') " + searchSql;
            String countSql = "select count(nodeid) from tb_entry_index sid where 1=1 and flagopen in('原文开放','条目开放') " + searchSql;
            Page<Tb_index_detail> detailPage = getPageListTwo(sql, sortStr, countSql, page, limit, pageRequest);
            return classifySearchController.convertNodefullnames(detailPage, pageRequest);
        }else{  //数据源为声像系统
            String indexSql = "select sid.entryid from tb_entry_index sid where 1=1 " + searchSql;
            String indexCountSql = "select count(entryid) from tb_entry_index sid where 1=1 " + searchSql;
            Page<Tb_entry_index_sx> resultEntry = getPageListEntry(indexSql,sortStr, indexCountSql, page, limit, pageRequest,false);
            List<Tb_entry_index_sx> sxList = resultEntry.getContent();
            List<Tb_entry_index_sx> returnList = classifySearchDirectoryController.convertSxNodefullnameAll(sxList);
            return new PageImpl(returnList,pageRequest,resultEntry.getTotalElements());
        }
    }

    public Page<Tb_entry_index> getreturnOutwares(String staOut, int page,int limit, String condition, String operator, String content,Sort sort) {
        PageRequest pageRequest = new PageRequest(page - 1, limit,
                sort == null ? new Sort(Sort.Direction.DESC, "archivecode", "descriptiondate") : sort);
        String searchCondition = "";
        if (content != null && !"".equals(content)){//输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "", operator);
        }
        String sql = "select * from tb_entry_index where  entryid  in(select t1.entry from (SELECT o.waretime,os.storages_stid,o.waretype,s.storestatus,s.entry FROM st_outware_storages os  " +
                "  left join st_outware o on os.outwares_outid = o.outid left join st_storage s on os.storages_stid = s.stid where  " +
                "  storestatus = '"+staOut+"' and s.entry is not null ) t1,(SELECT max(o.waretime) waretime,os.storages_stid FROM st_outware_storages os  " +
                "  left join st_outware o on os.outwares_outid = o.outid left join st_storage s on os.storages_stid = s.stid where  " +
                "  storestatus = '"+staOut+"' and s.entry is not null  GROUP BY os.storages_stid )t2 where t1.waretime =t2.waretime " +
                "  and t1.storages_stid = t2.storages_stid  and waretype != '转递出库')"+searchCondition;

        Query query = entityManager.createNativeQuery(sql, Tb_entry_index.class);
        query.setFirstResult((page - 1) * limit);
        query.setMaxResults(limit);
        List<Tb_entry_index> resultList = query.getResultList();

        int count = entryIndexRepository.getreturnOutwaresNums(staOut);
        return new PageImpl(resultList, pageRequest,count);
    }

    /**
     * 查找库区管理中单击的单元格存入的档案信息
     * @param shid
     * @param pageRequest
     * @return
     */
    public Page<Tb_entry_index> getCellEntry(String shid, PageRequest pageRequest) {
        String[] entryArr = storageRepository.findByShidInwares(shid);
        if(entryArr.length == 0){
            return  null;
        }
        Page<Tb_entry_index> entryList = entryIndexRepository.findByEntryidIn(entryArr, pageRequest);
        return entryList;
    }

    public List<String> getNodeidByTaskid(String taskid){
        Tb_task task = taskRepository.findByTaskid(taskid);
        task.setState(Tb_task.STATE_FINISHED);
        taskRepository.save(task);
        Tb_transdoc transdoc = transdocRepository.findOne(task.getBorrowmsgid());
        String nodeid = transdoc.getNodeid();
        List<String> parentNodeids = new ArrayList<>();
        getParentNodeids(nodeid,parentNodeids);
        return parentNodeids;
    }

    //递归获取所有父节点
    public List<String> getParentNodeids(String nodeid,List<String> parentNodeids) {
        Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
        if (node == null) {
            return null;
        }
        else if (!"0".equals(node.getParentnodeid())) {
            parentNodeids.add(node.getNodeid());
            getParentNodeids(node.getParentnodeid(),parentNodeids);
        }else{
            parentNodeids.add(node.getNodeid());
        }
        return parentNodeids;
    }

    public Page<Tb_index_detail> getMediaEntries(String nodeid, String condition, String operator, String content,
                                                 Tb_index_detail formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,
                                                 boolean ifSearchLeafNode, boolean ifContainSelfNode, int page, int limit, Sort sort, String[] labels, String groupid) {
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        String nodeidStr = "";
        if (nodeid != null) {
            nodeidStr = " and sid.nodeid='" + nodeid + "' ";
            if (ifSearchLeafNode) {//点击非叶子节点时，查询出其包含的所有叶子节点数据
                List<String> nodeidList = nodesettingService.getNodeidLoop(nodeid, ifContainSelfNode, new ArrayList<String>());
                if (nodeidList.size() > 0) {
                    nodeidStr = " and sid.nodeid in('" + String.join("','", nodeidList) + "') ";
                }
            }
        }
        String sortStr = "";//排序
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            sortStr = " order by " + order.getProperty() + " " + order.getDirection();
        } else {
            sortStr = " order by archivecode desc, descriptiondate desc ";
        }

        String labelsql = "";
        if (labels != null && labels.length != 0) {
            labelsql = " and entryid in(select entryid from tb_label_entry where labelid in (";
            for (int i = 0; i < labels.length; i++) {
                String label = labels[i];
                labelsql += ("'" + label + "'");
                if (i < labels.length - 1) {
                    labelsql += ",";
                } else {
                    labelsql += "))";
                }
            }
        }

        String groupsql = "";
        if (groupid != null) {
            groupsql = " and docgroupid in('" + groupid + "')";
        }

        String searchCondition = "";
        if (content != null && !"".equals(content)) {//输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        String dataStr = "";
        if (daterangedata.getFiledateendday() != null && daterangedata.getFiledatestartday() != null) {//检索条件为日期范围
            dataStr = classifySearchService.getDateRangeCondition(daterangedata);
        }
        String formAdvancedSearch = "";
        if (logic != null) {//高级检索的表单检索
            formAdvancedSearch = classifySearchService.getFormAdvancedIndexDetailSearch(formConditions, formOperators, logic);
        }
        String sql = "select sid.* from v_index_detail sid where 1=1 " + searchCondition + formAdvancedSearch + dataStr + nodeidStr + labelsql + groupsql + sortStr;
        String countSql = "select count(*) from v_index_detail sid where 1=1 " + searchCondition + formAdvancedSearch + dataStr + nodeidStr + labelsql + groupsql;
        return getPageList(sql, countSql, page, limit, pageRequest);
    }

    public Page<Tb_index_detail> getMediaEntries(String nodeid, String condition, String operator, String content,
                                                 Tb_index_detail formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,
                                                 boolean ifSearchLeafNode, boolean ifContainSelfNode, int page, int limit, Sort sort, String[] labels, String groupid
            , String[] filingyear, String[] entryretention) {
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        String nodeidStr = "";
        if (nodeid != null) {
            nodeidStr = " and sid.nodeid='" + nodeid + "' ";
            if (ifSearchLeafNode) {//点击非叶子节点时，查询出其包含的所有叶子节点数据
                List<String> nodeidList = nodesettingService.getNodeidLoop(nodeid, ifContainSelfNode, new ArrayList<String>());
                if (nodeidList.size() > 0) {
                    nodeidStr = " and sid.nodeid in('" + String.join("','", nodeidList) + "') ";
                }
            }
        }
        String sortStr = "";//排序
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            sortStr = " order by " + order.getProperty() + " " + order.getDirection();
        } else {
            sortStr = " order by archivecode desc, descriptiondate desc ";
        }

        String labelsql = "";
        if (labels != null && labels.length != 0) {
            labelsql = " and entryid in(select entryid from tb_label_entry where labelid in (";
            for (int i = 0; i < labels.length; i++) {
                String label = labels[i];
                labelsql += ("'" + label + "'");
                if (i < labels.length - 1) {
                    labelsql += ",";
                } else {
                    labelsql += "))";
                }
            }
        }

        //过滤归档年度sql
        String filingyearsql = "";
        if (filingyear != null && filingyear.length != 0) {
            filingyearsql = " and filingyear in(";
            for (int i = 0; i < filingyear.length; i++) {
                String label = filingyear[i];
                filingyearsql += ("'" + label + "'");
                if (i < filingyear.length - 1) {
                    filingyearsql += ",";
                } else {
                    filingyearsql += ")";
                }
            }
        }
        //过滤保管期限sql
        String entryretentionsql = "";
        if (entryretention != null && entryretention.length != 0) {
            entryretentionsql = " and entryretention in(";
            for (int i = 0; i < entryretention.length; i++) {
                String label = entryretention[i];
                entryretentionsql += ("'" + label + "'");
                if (i < entryretention.length - 1) {
                    entryretentionsql += ",";
                } else {
                    entryretentionsql += ")";
                }
            }
        }

        String groupsql = "";
        if (groupid != null) {
            groupsql = " and docgroupid in('" + groupid + "')";
        }

        String searchCondition = "";
        if (content != null && !"".equals(content)) {//输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        String dataStr = "";
        if (daterangedata.getFiledateendday() != null && daterangedata.getFiledatestartday() != null) {//检索条件为日期范围
            dataStr = classifySearchService.getDateRangeCondition(daterangedata);
        }
        String formAdvancedSearch = "";
        if (logic != null) {//高级检索的表单检索
            formAdvancedSearch = classifySearchService.getFormAdvancedIndexDetailSearch(formConditions, formOperators, logic);
        }
        String sql = "select sid.* from v_index_detail sid where 1=1 " + searchCondition + formAdvancedSearch + dataStr + nodeidStr + labelsql + filingyearsql + entryretentionsql + groupsql + sortStr;
        String countSql = "select count(*) from v_index_detail sid where 1=1 " + searchCondition + formAdvancedSearch + dataStr + nodeidStr + labelsql + filingyearsql + entryretentionsql + groupsql;
        return getPageList(sql, countSql, page, limit, pageRequest);
    }

    public List<String> getNodeidByWithAs(String parentnodeid){
        String str = DBCompatible.getInstance().findWithAs();  //对于with as 做不同数据库兼容
        String sql = "WITH " + str +"  t_children AS(SELECT nodeid,parentnodeid from tb_data_node WHERE parentnodeid='"+parentnodeid+"' UNION ALL SELECT t.nodeid,t.parentnodeid FROM tb_data_node t INNER JOIN t_children c on t.parentnodeid=c.nodeid )SELECT distinct nodeid from t_children";
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }

    public Tb_data_node getDataNodeByWithAs(String sql){
        Query query = entityManager.createNativeQuery(sql,Tb_data_node.class);
        if(query.getResultList().size()==0){
            return null;
        }else{
            return (Tb_data_node)query.getResultList().get(0);
        }
    }

    public Tb_data_node_sx getSxNodeLevel(String nodeid) {
        Tb_data_node_sx node = sxDataNodeRepository.findByNodeid(nodeid);
        if (node == null) {
            return null;
        } else if (node.getNodelevel() == 1) {
            return node;
        } else {
            return getSxNodeLevel(node.getParentnodeid());
        }
    }

    //执行退回采集
    public int[] backCaptureSubmit(String[] entryidData,String nodeid,String backreason,String backer,String backcount,String backorgan,String backtime){
        int insertIndexes = 0;
        int insertDetails = 0;
        Tb_backcapturedoc backcapturedoc = new Tb_backcapturedoc();
        backcapturedoc.setNodeid(nodeid);
        backcapturedoc.setBacktime(backtime);
        backcapturedoc.setBackcount(backcount);
        backcapturedoc.setBackorgan(backorgan);
        backcapturedoc.setBacker(backer);
        backcapturedoc.setBackreason(backreason);
        backcapturedoc = backCapturedocRepository.save(backcapturedoc);
        List<Tb_backcapturedoc_entry> backEntrys = new ArrayList<>();
        List<String[]> subAry = new InformService().subArray(entryidData, 1000);//处理ORACLE1000参数问题
        for (String[] ary : subAry) {
            //数据管理条目退回数据采集
            insertIndexes += entryIndexCaptureRepository.moveCaptures(ary);
            String[] insertIds = new String[ary.length];
            for (int i = 0; i < ary.length; i++){
                insertIds[i] = String.format("%1$-36s",(String) ary[i]);
                Tb_backcapturedoc_entry backcapturedocEntry = new Tb_backcapturedoc_entry();
                backcapturedocEntry.setEntryid(insertIds[i]);
                backcapturedocEntry.setBackdocid(backcapturedoc.getId());
                backEntrys.add(backcapturedocEntry);
            }
            //数据管理条目详情退回数据采集
            insertDetails += entryDetailCaptureRepository.moveCapturedetails(insertIds);
            //数据管理条目文件退回数据采集
            electronicCaptureRepository.moveCaptureEletronics(insertIds);
            //更新电子文件历史版本
            electronicVersionCaptureRepository.moveCaptureVersions(insertIds);
            //电子文件历史版本存储位置转移
            moveEleIndexVersionToCapture(insertIds);
            //电子文件存储位置转移
            moveEleIndexToCapture(insertIds);
            //删除管理表中的数据
            entryService.delEntry(insertIds);
        }
        //保存退回采集单据条目
        backCapturedocEntryRepository.save(backEntrys);
        int delflag = 1 ;
        return new int[]{insertIndexes, insertDetails, delflag};
    }

    /**
     * 退回采集后，将电子文件转存到capture命名的文件夹
     */
    private void moveEleIndexToCapture(String[] insertIds) {
        for (String id : insertIds) {
            //查找数据管理关联的电子文件
            List<Tb_electronic> indexEles = electronicRepository.findByEntryid(id);
            for (Tb_electronic electronic : indexEles) {
                if(electronic!=null){
                    //获取电子原文文件地址
                    File targetFile = new File( rootpath + electronic.getFilepath(), electronic.getFilename());
                    //把电子文件转存到以entryid为最后一层文件夹名称新的文件夹
                    targetFile.renameTo(new File(auditService.getStorageDir("capture",id),  electronic.getFilename()));
                    //查找退回采集后的电子文件，并修改电子文件路径
                    Tb_electronic_capture capture = electronicCaptureRepository.findByEleid(electronic.getEleid());
                    capture.setFilepath(electronicService.getStorageBaseDir("capture",id));
                }
            }
        }
    }

    /**
     * 退回采集后，将电子文件历史版本转存到capture命名的文件夹
     */
    private void moveEleIndexVersionToCapture(String[] insertIds) {
        List<Tb_electronic_version> electronicVersions = electronicVersionRepository.findByEntryidIn(insertIds);
        for(Tb_electronic_version eleVersion:electronicVersions){
            if(eleVersion!=null) {
                //获取电子原文文件地址
                File targetFile = new File(rootpath + eleVersion.getFilepath(), eleVersion.getFilename());
                //把电子文件转存到以新的路径下
                String filepath = electronicService.getUploadDirSaveVersion(eleVersion.getVersion(), eleVersion.getEleid().trim(), "capture")
                        .replace(rootpath, "");
                targetFile.renameTo(new File(rootpath + filepath, eleVersion.getFilename()));
                //查找退回采集后的电子文件，并修改电子文件路径
                Tb_electronic_version_capture electronic = electronicVersionCaptureRepository.findById(eleVersion.getId());
                electronic.setFilepath(filepath);
            }
        }
    }

    //获取节点所有退回采集单据
    public Page<Tb_backcapturedoc> getNodeBackCaptureDoc(int page, int limit, String condition, String operator, String content, String nodeid, Sort sort){
        Specifications sp = null;
        sp = Specifications.where(new SpecificationUtil("nodeid","equal",nodeid));
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        PageRequest pageRequest = new PageRequest(page-1,limit,sort);
        return backCapturedocRepository.findAll(sp,pageRequest);
    }

    public List<MediaEntry> getMediaEntry(List<Tb_entry_index_sx> list){
        String[] entryidAry = GainField.getFieldValues(list, "entryid").length == 0 ? new String[]{""} : GainField.getFieldValues(list, "entryid");
        List<Tb_thumbnail> thumList = thumbnailRepository.findByEntryidIn(entryidAry);
        List<MediaEntry> eList = new ArrayList<>();
        for (Tb_entry_index_sx index : list) {
            Tb_thumbnail thumbnail = null;
            for (Tb_thumbnail thum : thumList) {
                if (thum.getEntryid().trim().equals(index.getEntryid().trim())) {
                    thumbnail = thum;
                    break;
                }
            }
            MediaEntry entry = new MediaEntry();
            BeanUtils.copyProperties(index, entry);
            entry.setBackground(thumbnail == null ? "" : thumbnail.getUrl());
            eList.add(entry);
        }
        return eList;
    }
}