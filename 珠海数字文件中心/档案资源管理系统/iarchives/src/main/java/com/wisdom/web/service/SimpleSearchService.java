package com.wisdom.web.service;

import com.wisdom.secondaryDataSource.entity.Tb_entry_index_sx;
import com.wisdom.util.DBCompatible;
import com.wisdom.util.GainField;
import com.wisdom.web.controller.ClassifySearchController;
import com.wisdom.web.controller.ClassifySearchDirectoryController;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by RonJiang on 2017/10/30 0030.
 */
@Service
@Transactional
public class SimpleSearchService {
    @Autowired
    ThumbnailRepository thumbnailRepository;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    EntryBookmarksRepository entryBookmarksRepository;

    @Autowired
    ClassifySearchController classifySearchController;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RightOrganRepository rightOrganRepository;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    MissionUserRepository missionUserRepository;

    @Autowired
    ClassifySearchService classifySearchService;

    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    ElectronicRepository electronicRepository;

    @Autowired
    ElectronicPrintRepository electronicPrintRepository;

    @Autowired
    ElectronicSolidRepository electronicSolidRepository;

    @Autowired
    ThematicDetailRepository thematicDetailRepository;

    @Autowired
    EntryBookmarksRepository bookmarksRepository;

    @Autowired
    ClassifySearchDirectoryController classifySearchDirectoryController;

    @Autowired
    SimpleSearchDirectoryService simpleSearchDirectoryService;

    @Autowired
    NodesettingService nodesettingService;

    @Value("${sql.fullsearch}")
    private String fullSearchFlag;//是否使用简单检索的sqlserver全文检索

    /**
     * @param page      第几页
     * @param limit     一页获取多少行
     * @param condition 字段
     * @param operator  操作符
     * @param content   查询条件内容
     * @return
     */
    public Page<Tb_index_detail> findBySearch(int page, int limit, String isCollection, String condition,
                                              String operator, String content, Sort sort,String datasoure) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userid = userDetails.getUserid();
        String nodeidStr = userService.getAuthNode(userDetails.getUserid());//使用联表查询节点权限
        String isCollectionStr = "";//收藏条件
        if (isCollection != null && !"".equals(isCollection)) {
            isCollectionStr = getIsCollection(userid,null);
        }

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
            sortInt = entryIndexService.checkFilecode(order.getProperty());
        } else {
            //sortStr = " order by archivecode desc, descriptiondate desc ";
            sortStr = " order by entryid ";//先用这个，排序速度快
        }

        int searchInt=0;//主表字段的条件检索0  有副表字段的条件检索 1
        int searchContentInt=0;//单个数字或者字母的条件检索1  其他 0
        int lengthInt=0;//长度小于8纯数字字符串检索1  其他 0
        String searchCondition = "";
        if (content != null && !"".equals(content.trim())) {//输入框检索
            searchInt=entryIndexService.checkFilecode(condition);
            searchContentInt=checkContent(content.trim());
            lengthInt=checkIntContent(content.trim());
            if("true".equals(fullSearchFlag) && searchContentInt == 0 && lengthInt==0) {//启用sqlserver的全文检索 长度小于8纯数字字符串或者单个字母不走全文检索
                searchCondition = classifySearchService.getFullSearchSqlConditions(condition, content, "sid", operator);

                Page<Tb_index_detail> result = returenTbIndexDetail(datasoure,searchInt,sortInt,
                        searchCondition,isCollectionStr,nodeidStr,sortStr,pageRequest,page,limit);
                if(result.getContent().size() == 0){
                    searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
                    result = returenTbIndexDetail(datasoure,searchInt,sortInt,
                            searchCondition,isCollectionStr,nodeidStr,sortStr,pageRequest,page,limit);
                    return classifySearchController.convertNodefullnameAll(result, pageRequest);
                }
                return classifySearchController.convertNodefullnameAll(result, pageRequest);
            }else{
                searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
            }
        }

        Page<Tb_index_detail> result = returenTbIndexDetail(datasoure,searchInt,sortInt,
                searchCondition,isCollectionStr,nodeidStr,sortStr,pageRequest,page,limit);
        return classifySearchController.convertNodefullnameAll(result, pageRequest);
    }

    public Page<Tb_index_detail> returenTbIndexDetail(String datasoure,int searchInt,int sortInt,
                                                      String searchCondition,String isCollectionStr,String nodeidStr,
                                                      String sortStr,PageRequest pageRequest,int page,int limit){
        String table = "v_index_detail";
        String countTable = "v_index_detail";
        if("capture".equals(datasoure)){
            datasoure="v_index_detail_capture";
            table="v_index_detail_capture";
            countTable="v_index_detail_capture";
            if (searchInt == 0) {//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
                countTable = "tb_entry_index_capture";
            }
            if (sortInt == 0 && searchInt == 0) {//非副表表字段排序和非副表字段的检索
                table = "tb_entry_index_capture";
            }
        }else {
            datasoure="v_index_detail";
            if (searchInt == 0) {//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
                countTable = "tb_entry_index";
            }
            if (sortInt == 0 && searchInt == 0) {//非副表表字段排序和非副表字段的检索
                table = "tb_entry_index";
            }
        }
        String sql = "select sid.entryid from " + table + " sid where 1=1 " + searchCondition + isCollectionStr + nodeidStr;
        String countSql = "select count(nodeid) from " + countTable + " sid where 1=1 " + searchCondition + isCollectionStr + nodeidStr;
        Page<Tb_index_detail> result = entryIndexService.getPageListTwo(sql, sortStr, countSql, page, limit, pageRequest,datasoure);
        return result;
    }
    //判断是否单个数字或者字母
    public int checkContent(String content) {
        String fc_pattern = "[0-9]|[a-zA-Z]";//单个数字或者字母
        String[] conditions = content.split(",");
        for (int i = 0; i < conditions.length; i++) {
            if (conditions[i].matches(fc_pattern)) {
                return 1;
            }
        }
        return 0;
    }

    //判断是否长度小于8纯数字字符串
    public int checkIntContent(String content) {
        String fc_pattern = "[0-9]*";//单个数字或者字母
        String[] conditions = content.split(",");
        for (int i = 0; i < conditions.length; i++) {
            if (conditions[i].matches(fc_pattern)) {
                if(conditions[i].length()<8){
                    return 1;
                }
            }
        }
        return 0;
    }

    /**
     * @param page      第几页
     * @param limit     一页获取多少行
     * @param condition 字段
     * @param operator  操作符
     * @param content   查询条件内容
     * @return
     */
    public Page<Tb_entry_index> findBySearchPlatform(int page, int limit, String openType, String isCollection,
                                                     String condition, String operator, String content, Sort sort) {
        Set<String> organidSet = getOrganNodeByUser();// 获取机构数据节点
        String[] organArr = new String[organidSet.size()];
        organidSet.toArray(organArr);
        List<Tb_data_node> nodeList = dataNodeRepository.findByRefidIn(organArr);
        List<String> nodeidList = new ArrayList<>();
        for (Tb_data_node node : nodeList) {
            nodeidList.add(node.getNodeid());
        }
        Specification<Tb_entry_index> simpleSearchCondition = getSimpleSearchCondition(isCollection, condition,
                operator, content);
        Sort sorts = new Sort(new Sort.Order(Sort.Direction.DESC, "archivecode"),
                new Sort.Order(Sort.Direction.DESC, "descriptiondate"));
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? sorts : sort);
        Page<Tb_entry_index> result = entryIndexRepository.findAll(Specifications.where(simpleSearchCondition)
                .and(getSearchOpenCondition(openType)).and(getSearchNodeidCondition(nodeidList)), pageRequest);
        return classifySearchController.convertNodefullname(result, pageRequest);
    }

    //利用平台-开放档案
    public Page<Tb_index_detail> findBySearchPlatformOpen(int page, int limit, String openType, String isCollection,
                                                          String condition, String operator, String content, Sort sort, String entryids) {
		/*Specification<Tb_entry_index> simpleSearchCondition = getSimpleSearchCondition(isCollection, condition,
				operator, content);
		Sort sorts = new Sort(new Sort.Order(Sort.Direction.DESC, "opendate"));
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? sorts : sort);
		Page<Tb_entry_index> result = entryIndexRepository.findAll(Specifications.where(simpleSearchCondition)
				.and(getSearchOpenCondition(openType)), pageRequest);
		return classifySearchController.convertNodefullname(result, pageRequest);*/
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userid = userDetails.getUserid();
        if("a".equals(userDetails.getType())){
            openType="条目开放";
        }
        /*List<String> nodeidList = userService.findDataAuths(userid);
        String nodeidStr = "";
        if (nodeidList.size() > 0) {
            nodeidStr = " and nodeid in ('" + String.join("','", nodeidList) + "') ";
        }*/
        String searchCondition = "";
        if (content != null) {// 输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }

        String isCollectionStr = "";//收藏条件
        if (isCollection != null && !"".equals(isCollection)) {
            isCollectionStr = getIsCollection(userid,null);
        }

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
            sortInt = entryIndexService.checkFilecode(order.getProperty());
        } else {
            sortStr = " order by archivecode desc, descriptiondate desc ";
        }

        String openStr = "";
        if (openType != null) {//开放条件
            openStr = getSearchOpenStr(openType);
        }

        String entryidStr="";
//        if(entryids!=null&&!"".equals(entryids)){//过滤信息编研中已选中的数据管理条目
//            String[] entryidArr=entryids.split(",");
//            entryidStr=" and entryid not in ('"+String.join("','",entryidArr)+"') ";
//        }

        String table = "v_index_detail";
        String countTable = "v_index_detail";
        if (condition == null || entryIndexService.checkFilecode(condition) == 0) {//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
            countTable = "tb_entry_index";
            if (sortInt == 0) {//非副表表字段排序
                table = "tb_entry_index";
            }
        }
        String sql = "select sid.entryid from " + table + " sid where 1=1 " + searchCondition + isCollectionStr +  openStr+entryidStr;
        String countSql = "select count(nodeid) from " + countTable + " sid where 1=1 " + searchCondition + isCollectionStr +  openStr+entryidStr;
        Page<Tb_index_detail> result = entryIndexService.getPageListTwo(sql, sortStr, countSql, page, limit, pageRequest);
        return classifySearchController.convertNodefullnameAll(result, pageRequest);
    }

    public Page<MediaEntry> findMediaSearchOpen(int page, int limit, String openType, String isCollection,
                                                String condition, String operator, String content, Sort sort) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userid = userDetails.getUserid();
        String nodeidStr = "";
        /*if ("true".equals(openArchies)) {//只显示本单位+下属机构的条目
            nodeidStr=" and nodeid in (select nodeid from tb_data_node where organid in (select organid from tb_user_organ where userid ='"+userDetails.getUserid()+"')) ";
        }*/
        String searchCondition = "";
        if (content != null && !"".equals(content)) {// 输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }

        String isCollectionStr = "";//收藏条件
        if (isCollection != null && !"".equals(isCollection)) {
            isCollectionStr = getIsCollection(userid,null);
        }

        String sortStr = " order by archivecode desc, descriptiondate desc ";

        String openStr = "";
        if (openType != null) {//开放条件
            openStr = getSearchOpenStr(openType);
        }

        String table = "v_index_detail";
        String countTable = "v_index_detail";
        if (condition == null || entryIndexService.checkFilecode(condition) == 0) {//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
            countTable = "tb_entry_index";
            table = "tb_entry_index";
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        String nodeidLimit= "and sid.nodeid in (select dn.nodeid from tb_data_node_mdaflag dn where dn.is_media = 1)";
        String sql = "select sid.entryid from " + table + " sid where 1=1 " + searchCondition + isCollectionStr + nodeidStr + openStr + nodeidLimit;
        String countSql = "select count(*) from " + countTable + " sid where 1=1 " + searchCondition + isCollectionStr + nodeidStr + openStr;
        Page<Tb_index_detail> result = entryIndexService.getPageListTwo(sql, sortStr, countSql, page, limit,
                pageRequest);
        if(result.getContent() == null || result.getContent().size() == 0){
            return null;
        }
        List<MediaEntry> mediaEntries = new ArrayList<>();
        String[] entryidAry = GainField.getFieldValues(result.getContent(), "entryid").length == 0 ? new String[]{""} : GainField.getFieldValues(result.getContent(), "entryid");
        List<Tb_thumbnail> thumList = thumbnailRepository.findByEntryidIn(entryidAry);
        for (Tb_index_detail entry_index : result) {// 将基础表数据与扩展表数据合并到EntryBase表,并添加到集合
            MediaEntry mediaEntry = new MediaEntry();
            Tb_thumbnail thumbnail = null;
            for (Tb_thumbnail thum : thumList) {
                if (thum.getEntryid().equals(entry_index.getEntryid())) {
                    thumbnail = thum;
                    break;
                }
            }
            BeanUtils.copyProperties(entry_index, mediaEntry);
            mediaEntry.setBackground(thumbnail == null ? "" : thumbnail.getUrl());
            mediaEntries.add(mediaEntry);
        }

        return new PageImpl<MediaEntry>(mediaEntries,pageRequest,result.getTotalPages());
    }

    public Page<MediaEntry> findMediaSearchOpen(int page, int limit, String isCollection, String condition,
                          String operator, String content, Sort sort){
        PageRequest pageRequest = new PageRequest(page-1,limit);
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> nodeidList = userService.findSecondaryDataAuths(userDetails.getUserid());
        String nodeidStr = "";//权限，代理授权
        if (nodeidList.size() > 0) {
            nodeidStr = " and nodeid in('" + String.join("','", nodeidList) + "') ";
        }
        String searchCondition = "";
        if (content != null && !"".equals(content.trim())) {//输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
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
        Page<Tb_entry_index_sx> resultEntry = entryIndexService.getPageListEntry(indexSql,sortStr, indexCountSql, page, limit, pageRequest,false);
        List<Tb_entry_index_sx> sxList = resultEntry.getContent();
        List<Tb_entry_index_sx> returnList = classifySearchDirectoryController.convertSxNodefullnameAll(sxList);
        List<MediaEntry> eList = entryIndexService.getMediaEntry(returnList);
        return new PageImpl<MediaEntry>(eList, pageRequest, resultEntry.getTotalElements());
    }

    //编研管理系统-馆库查询数据源-声像系统
    public Page findBySearchCompilationSx(int page, int limit,String condition,
                                          String operator, String content, Sort sort){
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userDetails.getUserid();
        Page<Tb_entry_index_sx> indexPage = simpleSearchDirectoryService.findIndexSx(page, limit, condition, operator, content, sort, userId, false);
        List<Tb_entry_index_sx> sxList = indexPage.getContent();
        List<Tb_entry_index_sx> returnList = classifySearchDirectoryController.convertSxNodefullnameAll(sxList);
        return new PageImpl(returnList,pageRequest,indexPage.getTotalElements());
    }

    /**
     * 获取：当前用户所属的单位及单位下的部门，返回机构id集合
     *
     * @return Set<String>
     */
    public Set<String> getOrganNodeByUser() {
        // 找到他的所属单位
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String organid = userRepository.findOrganidByUserid(userDetails.getUserid());
        Tb_right_organ organ = rightOrganRepository.findOne(organid);
        while (organ.getOrgantype() != null && organ.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {
            organ = rightOrganRepository.findOne(organ.getParentid());// 该用户所属单位
        }
        Set<String> organidSet = new HashSet<>();// 允许显示的机构节点ID
        organidSet.add(organ.getOrganid());
        // 所属单位的所有子节点
        List<Tb_right_organ> childOrganList = rightOrganRepository.findByParentid(organ.getOrganid());
        List<Tb_right_organ> filterList = childOrganList.stream().filter(org -> org.getOrgantype().equals("department"))
                .collect(Collectors.toList());
        while (filterList.size() != 0) {
            String[] childids = GainField.getFieldValues(filterList, "organid").length == 0 ? new String[]{""}
                    : GainField.getFieldValues(filterList, "organid");
            organidSet.addAll(Arrays.asList(childids));
            childOrganList = rightOrganRepository.findByParentidIn(childids);
            filterList = childOrganList.stream().filter(org -> org.getOrgantype().equals("department"))
                    .collect(Collectors.toList());
        }
        return organidSet;
    }

    public Page<Tb_entry_index> getAllEntry(int page, int limit) {
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        return entryIndexRepository.findAll(pageRequest);
    }

    public Tb_entry_index getEntryIndexByEntryid(String entryid) {
        return entryIndexRepository.findByEntryid(entryid);
    }

    public Specification<Tb_entry_index> getSimpleSearchCondition(String isCollection, String condition,
                                                                  String operator, String content) {
        Specification<Tb_entry_index> simpleSearchCondition = new Specification<Tb_entry_index>() {
            @Override
            public Predicate toPredicate(Root<Tb_entry_index> root, CriteriaQuery<?> query,
                                         CriteriaBuilder criteriaBuilder) {
                if (condition != null) {
                    String[] conditions = condition.split(",");
                    String[] operators = operator.split(",");
                    String[] contents = content.split(",");// 存放前一次与本次查询内容
                    Predicate[] predicates = null;
                    List<String> entryid = null;
                    if (isCollection != null && !"".equals(isCollection)) {
                        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal();
                        // 查找到收藏的所有数据id
                        entryid = entryBookmarksRepository.findEntryidByUserid(userDetails.getUserid());
                        predicates = new Predicate[contents.length + 1];
                    } else {
                        predicates = new Predicate[contents.length];
                    }
                    for (int i = 0; i < contents.length; i++) {
                        String[] contentsData = new String[]{};// 存放两次查询的每一次查询内容中，以空格分隔开的内容
                        if (contents[i] != null) {
                            contentsData = contents[i].split(" ");// 切割以空格隔开的多个关键词
                        }
                        Predicate[] predicatesData = new Predicate[contentsData.length];
                        for (int j = 0; j < contentsData.length; j++) {
                            if ("like".equals(operators[i])) { // 类似于
                                predicatesData[j] = criteriaBuilder.like(root.get(conditions[i]),
                                        "%" + contentsData[j] + "%");
                            }
                        }
                        predicates[i] = criteriaBuilder.or(predicatesData);
                    }
                    if (isCollection != null && isCollection != "" && entryid.size() > 0) {
                        predicates[conditions.length] = criteriaBuilder.and(root.get("entryid").in(entryid));
                    }
                    return criteriaBuilder.and(predicates);
                }
                return null;
            }
        };
        return simpleSearchCondition;
    }

    public String getIsCollection(String userid,String type) {// 查找到收藏的所有数据id
        List<String> entryid = entryBookmarksRepository.findEntryidByUserid(userid);
        String isCollectionStr = "";
        if (entryid.size() > 0) {
            if(type!=null&&"directory".equals(type)){
                isCollectionStr = " and entryid in(select entryid from tb_entry_bookmarks where userid='" + userid + "' and addstate = '3') ";
            }else if(type!=null&&"management".equals(type)){
                isCollectionStr = " and entryid in(select entryid from tb_entry_bookmarks where userid='" + userid + "' and addstate = '0') ";
            }else{
                isCollectionStr = " and entryid in(select entryid from tb_entry_bookmarks where userid='" + userid + "' and addstate = '5') ";
            }
        }
        return isCollectionStr;
    }

    public static Specification<Tb_entry_index> getSearchOpenCondition(String opentype) {
        Specification<Tb_entry_index> searchOpenCondition = new Specification<Tb_entry_index>() {
            @Override
            public Predicate toPredicate(Root<Tb_entry_index> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                CriteriaBuilder.In in = criteriaBuilder.in(root.get("flagopen"));
                String[] openarr = opentype.split(",");
                for (String ele : openarr) {
                    in.value(ele);
                }
                return criteriaBuilder.or(in);
            }
        };
        return searchOpenCondition;
    }

    /**
     * 实现：生成了查询条件——查询在节点集合nodeidList中的条目数据；
     * 分组处理：处理oracle的in的个数不能超过1000的问题，例如:nodeid in（1,2...）or nodeid in (3,4...)；
     *
     * @param nodeidList
     * @return
     */
    public static Specification<Tb_entry_index> getSearchNodeidCondition(List<String> nodeidList) {
        Specification<Tb_entry_index> SearchNodeidCondition = new Specification<Tb_entry_index>() {
            @Override
            public Predicate toPredicate(Root<Tb_entry_index> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                int num = 900;
                int group = (nodeidList.size() / num) + 1;
                Predicate[] predicates = new Predicate[group];
                CriteriaBuilder.In in = criteriaBuilder.in(root.get("tdn").get("nodeid"));
                for (int q = 0; q < group; q++) {
                    if (q == group - 1) {
                        for (int i = q * num; i < nodeidList.size(); i++) {
                            in.value(nodeidList.get(i));
                        }
                    } else {
                        for (int i = q * num; i < q * num + num; i++) {
                            in.value(nodeidList.get(i));
                        }
                    }
                    predicates[q] = in;
                    in = criteriaBuilder.in(root.get("tdn").get("nodeid"));
                }

                return criteriaBuilder.or(predicates);
            }
        };
        return SearchNodeidCondition;
    }

    public String getSearchOpenStr(String opentype) {
        String[] openarr = opentype.split(",");
        String openStr = " and flagopen in('" + String.join("','", openarr) + "') ";
        return openStr;
    }

    public List<Tb_electronic_print> getApplySetPrint(String entryid) {
        List<Tb_electronic_print> electronic_prints = electronicPrintRepository.findByEntryidAndBorrowcode(entryid,null);
        return electronic_prints;
    }

    public void setApplySetPrint(String[] applyprintids,Tb_electronic_print electronic_print) {
        List<Tb_electronic_print> electronic_prints = electronicPrintRepository.findByIdIn(applyprintids);
        for(Tb_electronic_print electronicPrint : electronic_prints){
            electronicPrint.setPrintstate(electronic_print.getPrintstate());
            electronicPrint.setScopepage(electronic_print.getScopepage());
            electronicPrint.setCopies(electronic_print.getCopies());
        }
    }

    public void cleanScope(String[] applyprintids) {
        List<Tb_electronic_print> electronic_prints = electronicPrintRepository.findByIdIn(applyprintids);
        for(Tb_electronic_print electronicPrint : electronic_prints){
            electronicPrint.setCopies(0);
            electronicPrint.setPrintstate(null);
            electronicPrint.setScopepage(null);
        }
    }
}