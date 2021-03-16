package com.wisdom.web.service;

import com.wisdom.secondaryDataSource.entity.Tb_entry_index_sx;
import com.wisdom.util.DBCompatible;
import com.wisdom.web.controller.ClassifySearchController;
import com.wisdom.web.controller.ClassifySearchDirectoryController;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.EntryBookmarksRepository;
import com.wisdom.web.repository.EntryIndexManageRepository;
import com.wisdom.web.repository.EntryIndexRepository;

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
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by nick on 2018/3/9.
 * 条目收藏夹service
 */
@Service
@Transactional
public class EntryBookmarksService {

    @Autowired
    EntryBookmarksRepository bookmarksRepository;

    @Autowired
    EntryIndexRepository entryIndexRepository;
    
    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    EntryIndexManageRepository entryIndexManageRepository;

    @Autowired
    ClassifySearchController classifySearchController;

    @Autowired
    SimpleSearchDirectoryService simpleSearchDirectoryService;

    @Autowired
    ClassifySearchService classifySearchService;

    @Autowired
    ClassifySearchDirectoryController classifySearchDirectoryController;

    @Autowired
    EntryIndexService entryIndexService;

    @PersistenceContext
    EntityManager entityManager;

    public ExtMsg addBookmarks(String[] entryids, String userid,String type){
        List<String> bmList = new ArrayList<>();
        String addtate="0";//简单检索收藏
        if(type!=null&&"directory".equals(type)){ //判断是否目录检索收藏
            addtate="3";//目录检索收藏
        }else if (type!=null&&"pavilion".equals(type)) {//判断是否是馆库查询收藏
            addtate = "4";
        }else if (type!=null&&"soundimage".equals(type)) {//判断是否目录中心声像收藏
            addtate = "5";
        }else if (type!=null&&"pavilionSoundimage".equals(type)) {//判断是否编研管理声像收藏
            addtate = "6";
        }
        bmList = bookmarksRepository.findEntryidByUseridandAddstate(userid,addtate);
        List<String> noexists = new ArrayList<>();
        for (String entryid : entryids){
            if(!bmList.contains(entryid.trim())){
                noexists.add(entryid);
            }
        }
        //已选数据不存在收藏关联,数据添加收藏
        if(noexists.size()>0){
            addBookmark(userid, noexists,addtate);
        }
        return new ExtMsg(true,"收藏成功",noexists);
    }

    public List<Tb_entry_bookmarks> addBookmark(String userid,List<String> entryidList,String addstate){
        List<Tb_entry_bookmarks> bookmarksList=new ArrayList<>();
        for(String entryid:entryidList){
            Tb_entry_bookmarks bmObj=new Tb_entry_bookmarks();
            bmObj.setUserid(userid);
            bmObj.setEntryid(entryid);
            bmObj.setAddstate(addstate);//标记收藏
            Date day=new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            bmObj.setModify(df.format(day));
            bookmarksList.add(bmObj);
        }
        return bookmarksRepository.save(bookmarksList);
    }

    public ExtMsg cancelBookmarks(String[] entryids, String userid,String type){
        if(type!=null&&"directory".equals(type)) { //判断是否目录检索收藏
            bookmarksRepository.deleteByEntryidInAndUseridAndAddstate(entryids, userid,"3");
        }else if(type!=null&&"management".equals(type)){
            bookmarksRepository.deleteByEntryidInAndUseridAndAddstate(entryids, userid,"0");
        }else if(type!=null&&"pavilion".equals(type)){
            bookmarksRepository.deleteByEntryidInAndUseridAndAddstate(entryids, userid,"4");
        }else if(type!=null&&"soundimage".equals(type)){  //目录中心声像取消收藏
            bookmarksRepository.deleteByEntryidInAndUseridAndAddstate(entryids, userid,"5");
        }else if(type!=null&&"pavilionSoundimage".equals(type)){  //编研管理声像取消收藏
            bookmarksRepository.deleteByEntryidInAndUseridAndAddstate(entryids, userid,"6");
        }
        return new ExtMsg(true,"取消收藏成功",null);
    }

    /**
     *
     * @param page      第几页
     * @param limit     一页获取多少行
     * @param condition 字段
     * @param operator  操作符
     * @param content   查询条件内容
     * @return
     */
    public Page<Tb_entry_index> findBySearch(int page, int limit, String condition, String operator, String content,String userid,Sort sort,String searchtype,String datasoure){
        PageRequest pageRequest = new PageRequest(page-1,limit, sort);
        List<String> entryids = new ArrayList<>();
        if("2".equals(searchtype)){//判断是否是馆库查询
            if("soundimage".equals(datasoure)){//声像系统
                return findBySearchCompilationSx(page,limit,sort);
            }
            entryids = bookmarksRepository.findEntryidByUseridandAddstate(userid,"4");//馆库查询-编研系统-查看收藏
        }else {
            entryids = bookmarksRepository.findEntryidByUserid(userid);
        }
        Specifications specifications = null;
        if (entryids.size() > 0) {
        	String[] entryidArr = new String[entryids.size()];
            entryids.toArray(entryidArr);
            Specification<Tb_entry_index> searchEntryidsCondition = EntryIndexService.getSearchEntryidsCondition(entryidArr);
            specifications = Specifications.where(searchEntryidsCondition);
            if (content != null && !content.equals("")) {
                specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
            }
        }
        if(specifications == null){
            return null;
        }
        Page<Tb_entry_index> entryIndex =  entryIndexRepository.findAll(specifications,pageRequest);
        //return getFullName(entryIndex, new PageRequest(page-1,limit,new Sort("archivecode")));
        return classifySearchController.getNodefullnameAll(entryIndex, new PageRequest(page-1,limit,new Sort("archivecode")));
    }



    public Page<Tb_entry_index> findBySearchSimple(int page, int limit, String condition, String operator,
                                                  String content,String userid,String sort,String searchtype,
                                                   String datasoure){
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        if("2".equals(searchtype)){//判断是否是馆库查询
            if("soundimage".equals(datasoure)){//声像系统
                return findBySearchCompilationSx(page,limit, WebSort.getSortByJson(sort));
            }
        }
        String contentSql = "";
        if (content != null && !content.equals("")) {
             contentSql = "and " + condition + " "+operator + " '%"+ content +"%'";
        }
        String sortSql = "";
        if(sort == null){
            sortSql = " order by modify desc";
        }else{
            String[] sortArray = sort.split("[{:,}]");
            String cond = sortArray[2].substring(1, sortArray[2].length()-1);
            String c = sortArray[4].substring(1,sortArray[4].length()-1);
            sortSql =
                    " order by tei." + cond + " "+ c ;
        }

        String sql = "select tei.* from tb_entry_index tei inner join tb_entry_bookmarks teb on tei.entryid = teb.entryid" +
                " where teb.userid='"+userid+"' and teb.addstate= '0'" + contentSql;
        String countSql =  "select count(nodeid) from tb_entry_index tei inner join tb_entry_bookmarks teb on tei.entryid = teb.entryid" +
                " where teb.userid='"+userid+"' and teb.addstate= '0'" + contentSql;
        Query couuntQuery = entityManager.createNativeQuery(countSql);
        int count = Integer.parseInt(couuntQuery.getResultList().get(0) + "");
        List<Tb_entry_index> resultList;
        if(count>1000 ||"sqlserver".equals(DBCompatible.getDBVersion())){
            sql = DBCompatible.getInstance().sqlPages(sql+sortSql, page - 1, limit);
            Query query = entityManager.createNativeQuery(sql, Tb_entry_index.class);
            resultList = query.getResultList();
        }else {
            sql = sql +  sortSql;
            Query query = entityManager.createNativeQuery(sql, Tb_entry_index.class);
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            resultList = query.getResultList();
        }
        return classifySearchController.getNodefullnameAll(new PageImpl(resultList, pageRequest, count), pageRequest);
    }

    //编研管理系统-馆库查询-声像系统-查看收藏
    public Page findBySearchCompilationSx(int page, int limit,Sort sort){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userid = userDetails.getUserid();
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        List<String> entryidlist = bookmarksRepository.findEntryidByUseridandAddstate(userid,"6");
        if(entryidlist.size()>0){
            String searchSql = " and entryid in('" + String.join("','", entryidlist) + "') ";
            Page<Tb_entry_index_sx> indexPage = simpleSearchDirectoryService.findIndexSxBook(page, limit, sort, false,searchSql);
            List<Tb_entry_index_sx> sxList = indexPage.getContent();
            List<Tb_entry_index_sx> returnList = classifySearchDirectoryController.convertSxNodefullnameAll(sxList);
            return new PageImpl(returnList,pageRequest,indexPage.getTotalElements());
        }else{
            return null;
        }
    }



    //获取数据节点全名
    private Page<Tb_entry_index> getFullName(Page<Tb_entry_index> result,PageRequest pageRequest) {
        List<Tb_entry_index> content = result.getContent();
        long totalElements = result.getTotalElements();
        List<Tb_entry_index> returnResult = new ArrayList<>();
        for(Tb_entry_index entryIndex:content){
            Tb_entry_index entry_index = new Tb_entry_index();
            BeanUtils.copyProperties(entryIndex,entry_index);
            if(entry_index.getTdn()!=null){
                String nodeid = entry_index.getTdn().getNodeid();
                String nodefullname = nodesettingService.getNodefullnameLoop(nodeid,"_","");
                entry_index.setNodefullname(nodefullname);
                returnResult.add(entry_index);
            }
        }
        return new PageImpl(returnResult,pageRequest,totalElements);
    }

    /**目录检索
     *
     * @param page      第几页
     * @param limit     一页获取多少行
     * @param condition 字段
     * @param operator  操作符
     * @param content   查询条件内容
     * @return
     */
    public Page findBySearchDirectory(String datasoure,int page, int limit, String condition, String operator, String content, String userid, Sort sort){
        PageRequest pageRequest = new PageRequest(page-1,limit, sort);
        String searchSql = "";
        if (content != null && !"".equals(content.trim())) {//输入框检索
            searchSql = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        if("directory".equals(datasoure)){  //数据源为目录管理
            List<String> entryids = bookmarksRepository.findEntryidByUseridandAddstate(userid,"3");
            if(entryids.size()>0){
                searchSql = " and entryid in('" + String.join("','", entryids) + "') ";
            }else {
                return null;
            }
            Page<Tb_index_detail_manage> resultAll = simpleSearchDirectoryService.findDetailManage(page,limit,sort,searchSql);
            return classifySearchDirectoryController.convertManageNodefullnameAll(resultAll, pageRequest);
        }else if("management".equals(datasoure)){  //数据源为档案系统
            List<String> entryids = bookmarksRepository.findEntryidByUseridandAddstate(userid,"0");
            if(entryids.size()>0){
                searchSql = " and entryid in('" + String.join("','", entryids) + "') ";
                Page<Tb_index_detail> detailPage = simpleSearchDirectoryService.findIndexDeatail(page, limit, sort, searchSql);
                return classifySearchController.convertNodefullnames(detailPage, pageRequest);
            }else{
                return null;
            }
        }else{  //数据源为声像系统
            List<String> entryids = bookmarksRepository.findEntryidByUseridandAddstate(userid,"5");
            if(entryids.size()>0){
                searchSql = " and entryid in('" + String.join("','", entryids) + "') ";
                Page<Tb_entry_index_sx> indexPage = simpleSearchDirectoryService.findIndexSxBook(page, limit, sort, false,searchSql);
                List<Tb_entry_index_sx> sxList = indexPage.getContent();
                List<Tb_entry_index_sx> returnList = classifySearchDirectoryController.convertSxNodefullnameAll(sxList);
                return new PageImpl(returnList,pageRequest,indexPage.getTotalElements());
            }else{
                return null;
            }
        }
    }

    private Page<Tb_entry_index_manage> getFullNameDirectory(Page<Tb_entry_index_manage> result,PageRequest pageRequest) {
        List<Tb_entry_index_manage> content = result.getContent();
        long totalElements = result.getTotalElements();
        List<Tb_entry_index_manage> returnResult = new ArrayList<>();
        for(Tb_entry_index_manage entryIndex:content){
            Tb_entry_index_manage entry_index = new Tb_entry_index_manage();
            BeanUtils.copyProperties(entryIndex,entry_index);
            String nodeid = entry_index.getNodeid();
            String nodefullname = nodesettingService.getNodefullnameLoop(nodeid,"_","");
            entry_index.setNodefullname(nodefullname);
            returnResult.add(entry_index);
        }
        return new PageImpl(returnResult,pageRequest,totalElements);
    }
}