package com.wisdom.web.service;

import com.wisdom.secondaryDataSource.entity.Tb_entry_index_sx;
import com.wisdom.secondaryDataSource.entity.Tb_index_detail_sx;
import com.wisdom.secondaryDataSource.repository.SecondaryEntryIndexRepository;
import com.wisdom.util.DBCompatible;
import com.wisdom.web.controller.ClassifySearchController;
import com.wisdom.web.controller.ClassifySearchDirectoryController;
import com.wisdom.web.entity.Tb_index_detail;
import com.wisdom.web.entity.Tb_index_detail_manage;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/6/26.
 */
@Service
@Transactional
public class SimpleSearchDirectoryService {

    @Autowired
    UserService userService;

    @Autowired
    SimpleSearchService simpleSearchService;

    @Autowired
    ClassifySearchService classifySearchService;

    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    ClassifySearchDirectoryController classifySearchDirectoryController;

    @Autowired
    SecondaryEntryIndexRepository secondaryEntryIndexRepository;

    @Autowired
    ClassifySearchController classifySearchController;

    @PersistenceContext(unitName="entityManagerFactorySecondary")
    EntityManager entityManagerSecondary;

    /**
     * @param page      第几页
     * @param limit     一页获取多少行
     * @param condition 字段
     * @param operator  操作符
     * @param content   查询条件内容
     * @return
     */
    public String createSearchSql(int page, int limit, String isCollection, String condition,
                                                     String operator, String content, Sort sort,String userid,String datasoure) {
        List<String> nodeidList = userService.findDataAuths(userid);
        String nodeidStr = "";//权限，代理授权
        if (nodeidList.size() > 0) {
            if("directory".equals(datasoure)){
                nodeidStr = " and nodeid in('" + String.join("','", nodeidList) + "') ";
            }else{
                nodeidStr = " and flagopen in('原文开放','条目开放') and nodeid in('" + String.join("','", nodeidList) + "') ";
            }
        }
        String isCollectionStr = "";//收藏条件
        if (isCollection != null && !"".equals(isCollection)) {
            isCollectionStr = simpleSearchService.getIsCollection(userid,datasoure);
        }
        String searchCondition = "";
        if (content != null && !"".equals(content.trim())) {//输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        //PageRequest pageRequest = new PageRequest(page - 1, limit);
//        String sortStr = "";//排序
//        int sortInt = 0;//判断是否副表表排序
//        if (sort != null && sort.iterator().hasNext()) {
//            Sort.Order order = sort.iterator().next();
//            if ("eleid".equals(order.getProperty())) {
//                sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
//            } else {
//                sortStr = " order by " + order.getProperty() + " " + order.getDirection();
//            }
//            sortInt = entryIndexService.checkFilecode(order.getProperty());
//        } else {
//            sortStr = " order by archivecode desc, descriptiondate desc ";
//        }

//        String table = "v_index_detail_manage";
//        String countTable = "v_index_detail_manage";
//        if (condition == null || entryIndexService.checkFilecode(condition) == 0) {//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
//            countTable = "tb_entry_index_manage";
//            if (sortInt == 0) {//非副表表字段排序
//                table = "tb_entry_index_manage";
//            }
//        }
//        //合并
//        Page<Tb_index_detail_manage> pageList=mergePage(managePage,pageRequest,condition,operator,content,sort,userid);
        return searchCondition + isCollectionStr + nodeidStr;
    }

    //目录管理条目
    public Page<Tb_index_detail_manage> findDetailManage(int page, int limit, Sort sort,String searchSql){
        PageRequest pageRequest = new PageRequest(page-1, limit);
        String sortStr="";
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            if ("eleid".equals(order.getProperty())) {
                sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
            } else {
                sortStr = " order by " + order.getProperty() + " " + order.getDirection();
            }
        } else {
            sortStr = " order by archivecode desc, descriptiondate desc ";
        }
        String sql = "select sid.entryid from tb_entry_index_manage sid where 1=1 " + searchSql;
        String countSql = "select count(nodeid) from tb_entry_index_manage sid where 1=1 " + searchSql;
        Page<Tb_index_detail_manage> managePage = entryIndexService.getPageListDetailManage(sql, sortStr, page, limit, pageRequest,countSql);
        return managePage;
    }

    //查询声像库的条目
    public Page<Tb_entry_index_sx> findIndexSx(int page, int limit, String condition, String operator, String content, Sort sort, String userid,boolean isGetCount){
        List<String> nodeidList = userService.findSecondaryDataAuths(userid);
        String nodeidStr = "";//权限，代理授权
        if (nodeidList.size() > 0) {
            nodeidStr = " and nodeid in('" + String.join("','", nodeidList) + "') ";
        }
        String searchCondition = "";
        if (content != null && !"".equals(content.trim())) {//输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        PageRequest pageRequest = new PageRequest(page-1, limit);
        String sortStr = ""; //排序
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            if ("eleid".equals(order.getProperty())) {
                sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
            } else {
                sortStr = " order by " + order.getProperty() + " " + order.getDirection();
            }
        } else {
            sortStr = " order by archivecode desc, descriptiondate desc ";
        }
        String indexSql = "select sid.entryid from tb_entry_index sid where 1=1 " + searchCondition  + nodeidStr;
        String indexCountSql = "select count(entryid) from tb_entry_index sid where 1=1 " + searchCondition + nodeidStr;
        Page<Tb_entry_index_sx> resultEntry = entryIndexService.getPageListEntry(indexSql,sortStr, indexCountSql, page, limit, pageRequest,isGetCount);
        return resultEntry;
    }

    //查询声像库的收藏条目
    public Page<Tb_entry_index_sx> findIndexSxBook(int page, int limit,Sort sort,boolean isGetCount,String searchSql){
        PageRequest pageRequest = new PageRequest(page-1, limit);
        String sortStr = ""; //排序
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            if ("eleid".equals(order.getProperty())) {
                sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
            } else {
                sortStr = " order by " + order.getProperty() + " " + order.getDirection();
            }
        } else {
            sortStr = " order by archivecode desc, descriptiondate desc ";
        }
        String indexSql = "select sid.entryid from tb_entry_index sid where 1=1 " + searchSql;
        String indexCountSql = "select count(nodeid) from tb_entry_index sid where 1=1 " + searchSql;
        Page<Tb_entry_index_sx> resultEntry = entryIndexService.getPageListEntry(indexSql,sortStr, indexCountSql, page, limit, pageRequest,isGetCount);
        return resultEntry;
    }

    /**
     *查询档案开放条目
     * @param page
     * @param limit
     * @param sort
     * @param searchSql
     * @return
     */
    public Page<Tb_index_detail> findIndexDeatail(int page, int limit, Sort sort,String searchSql){
        PageRequest pageRequest = new PageRequest(page-1, limit);
        String sortStr="";
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            if ("eleid".equals(order.getProperty())) {
                sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
            } else {
                sortStr = " order by " + order.getProperty() + " " + order.getDirection();
            }
        } else {
            sortStr = " order by archivecode desc, descriptiondate desc ";
        }
        String sql = "select sid.entryid from tb_entry_index sid where 1=1 " + searchSql;
        String countSql = "select count(entryid) from tb_entry_index sid where 1=1 " + searchSql;
        Page<Tb_index_detail> detailPage = entryIndexService.getPageListTwo(sql, sortStr,countSql, page, limit, pageRequest);
        return detailPage;
    }

    //查询合并所有page数据
    public Page mergePage(String datasoure,int page, int limit, String isCollection, String condition,
                                                  String operator, String content, Sort sort){
        PageRequest pageRequest = new PageRequest(page-1,limit);
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userDetails.getUserid();
        String searchSql = createSearchSql(page,limit,isCollection,condition,operator,content,sort,userId,datasoure);
        if("directory".equals(datasoure)){  //数据源为目录管理
            Page<Tb_index_detail_manage> resultAll = findDetailManage(page,limit,sort,searchSql);
            return classifySearchDirectoryController.convertManageNodefullnameAll(resultAll, pageRequest);
        }else if("management".equals(datasoure)){  //数据源为档案系统
            Page<Tb_index_detail> detailPage = findIndexDeatail(page, limit, sort, searchSql);
            return classifySearchController.convertNodefullnameAll(detailPage, pageRequest);
        }else{  //数据源为声像系统
            Page<Tb_entry_index_sx> indexPage = findIndexSx(page, limit, condition, operator, content, sort, userId, false);
            List<Tb_entry_index_sx> sxList = indexPage.getContent();
            List<Tb_entry_index_sx> returnList = classifySearchDirectoryController.convertSxNodefullnameAll(sxList);
            return new PageImpl(returnList,pageRequest,indexPage.getTotalElements());
        }
    }

    public Tb_index_detail_sx getSxEntry(String entryid){
        String sql = "select tt.* from v_index_detail tt  where tt.entryid='"+ entryid +"'";
        Query query = entityManagerSecondary.createNativeQuery(sql, Tb_index_detail_sx.class);
        return (Tb_index_detail_sx)query.getResultList().get(0);
    }
}
