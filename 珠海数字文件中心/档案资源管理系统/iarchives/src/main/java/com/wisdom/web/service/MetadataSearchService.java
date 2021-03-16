package com.wisdom.web.service;

import com.wisdom.util.DBCompatible;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SunK on 2020/4/20 0020.
 */
@Service
public class MetadataSearchService {


    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    ClassifySearchService classifySearchService;

    @Autowired
    TemplateRepository templateRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    MetadataTemplateRepository metadataTemplateRepository;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    RightOrganRepository rightOrganRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntryIndexCaptureService entryIndexCaptureService;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    EntryDetailRepository entryDetailRepository;

    @Autowired
    UserDataNodeRepository userDataNodeRepository;

    @Autowired
    SimpleSearchService simpleSearchService;

    //利用平台-开放档案
    public Page<Tb_index_detail> findBySearchPlatformOpen(int page, int limit, String openType, String isCollection,
                                                                  String condition, String operator, String content, Sort sort,
                                                                  String entryids,String metadataType) {

        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userid = userDetails.getUserid();

        String searchCondition = "";
        if (content != null&& !"".equals(condition)&&condition!=null) {// 输入框检索
            //查询出元数据字段对应的模板字段
            condition = templateRepository.findFieldcodeByMetadataid(condition);
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
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
            openStr = simpleSearchService.getSearchOpenStr(openType);
        }

        String entryidStr = "";
        if (entryids != null && !"".equals(entryids)) {//过滤信息编研中已选中的数据管理条目
            String[] entryidArr = entryids.split(",");
            entryidStr = " and entryid not in ('" + String.join("','", entryidArr) + "') ";
        }

        String table = "v_index_detail";
        String countTable = "v_index_detail";
        if (condition == null || entryIndexService.checkFilecode(condition) == 0) {//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
            countTable = "tb_entry_index";
            if (sortInt == 0) {//非副表表字段排序
                table = "tb_entry_index";
            }
        }
        String sql = "select sid.entryid from " + table + " sid where 1=1 " + searchCondition + openStr + entryidStr;
        String countSql = "select count(nodeid) from " + countTable + " sid where 1=1 " + searchCondition + openStr + entryidStr;
        Page<Tb_index_detail> result = getPageListTwo(sql, sortStr, countSql, page, limit, pageRequest);
//        return classifySearchController.convertNodefullnameAll(result, pageRequest);
        return result;
    }


    //Tb_index_detail sql原生语句分页查询
    public Page<Tb_index_detail> getPageListTwo(String sql, String sortStr, String countSql, int page, int limit, PageRequest pageRequest) {
        Query couuntQuery = entityManager.createNativeQuery(countSql);
        int count = Integer.parseInt(couuntQuery.getResultList().get(0) + "");
        List<Tb_index_detail> resultList;
        if (count > 1000) {
            sql = "select tt.* from v_index_detail tt  inner join (" + DBCompatible.getInstance().sqlPages(sql + sortStr, page - 1, limit) + ")t on t.entryid = tt.entryid ";
            Query query = entityManager.createNativeQuery(sql, Tb_index_detail.class);
            resultList = query.getResultList();
        } else {
            sql = "select tt.* from v_index_detail tt  inner join (" + sql + ")t on t.entryid = tt.entryid " + sortStr;
            Query query = entityManager.createNativeQuery(sql, Tb_index_detail.class);
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            resultList = query.getResultList();
        }
        return new PageImpl(resultList, pageRequest, count);
    }


    /**
     * 根据节点id和qfield查找检索字段
     *
     * @param metadataType
     * @return
     */
    public List<Tb_metadata_temp> queryConditionTemplate(String metadataType) {
        //获取模板维护中关联的字段来进行设置检索字段（必须是同分类）
//        List<ExtSearchData> queryConditionList = new ArrayList<ExtSearchData>();// 存放指定节点所有查询字段
//        List<String> queryList = templateRepository.findMetadataQueryByNode(nodeid);// 相应节点下的查询字段的模板
//        if(queryList==null||queryList.size()==0){
//            return queryConditionList;
//        }
//        List<Tb_metadata_temp> metadata_temps = metadataTemplateRepository.findAll(queryList);
//
//        for (Tb_metadata_temp template : metadata_temps) {
//            ExtSearchData extSearchData = new ExtSearchData();
//            extSearchData.setItem(template.getFieldcode());
//            extSearchData.setName(template.getFieldname());
//            queryConditionList.add(extSearchData);
//        }
//        return queryConditionList;
//        List<ExtSearchData> queryConditionList = new ArrayList<ExtSearchData>();
//        if (null == metadataType || "".equals(metadataType)) {
//            return queryConditionList;
//        }
        if (null == metadataType || "".equals(metadataType)) {
            return new ArrayList<>();
        }
        List<Tb_metadata_temp> metadata_temps = metadataTemplateRepository.findByClassify(metadataType);

        return metadata_temps;
    }

    public Page<Tb_entry_detail_capture> getEntriesByPower(String[] nodeids, int page, int limit,
                                                           String condition, String operator, String content, Sort sort) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if ("1".equals(userDetails.getUsertype())) {
            Tb_data_node node = dataNodeRepository.findByNodeid(nodeids[0]);
            Tb_right_organ organnode = rightOrganRepository.findByOrganid(node.getOrganid());
            String organid = userRepository.findOrganidByUserid(userDetails.getUserid());// 获取当前用户机构id
            Tb_right_organ organuser = rightOrganRepository.findOne(organid);
            if ("unit".equals(organuser.getOrgantype())) {
                while (organnode.getOrgantype() != null && organnode.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {// 获取单位对象
                    organnode = rightOrganRepository.findOne(organnode.getParentid());
                }
                if (organuser.getOrganid().equals(organnode.getOrganid())) {
                    Page<Tb_entry_detail_capture> index_details = entryIndexCaptureService.getEntrybaseto(nodeids, condition, operator, content, page, limit, sort);
                    return index_details;
                    //Page<Tb_entry_index> entryIndexPage = getEntriesByOpenCode(nodeids,opentype,page,limit,condition,operator,content,sort);

                } else {
                    List<Tb_user_data_node> userDataNodeList = userDataNodeRepository.findByUseridAndNodeid(userDetails.getUserid(), nodeids[0]);
                    if (userDataNodeList.size() > 0) {
                        Page<Tb_entry_detail_capture> index_details = entryIndexCaptureService.getEntrybaseto(nodeids, condition, operator, content, page, limit, sort);
                        return index_details;
                    }
                }
            } else if ("department".equals(organuser.getOrgantype())) {
                boolean flag = false;
                while (organnode.getOrgantype() != null && organnode.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {// 获取单位对象
                    if (organuser.getOrganid().equals(organnode.getOrganid())) {
                        flag = true;
                        break;
                    }
                    organnode = rightOrganRepository.findOne(organnode.getParentid());
                }
                if (flag) {
                    Page<Tb_entry_detail_capture> index_details = entryIndexCaptureService.getEntrybaseto(nodeids, condition, operator, content, page, limit, sort);
                    return index_details;
                } else {
                    List<Tb_user_data_node> userDataNodeList = userDataNodeRepository.findByUseridAndNodeid(userDetails.getUserid(), nodeids[0]);
                    if (userDataNodeList.size() > 0) {
                        Page<Tb_entry_detail_capture> index_details = entryIndexCaptureService.getEntrybaseto(nodeids, condition, operator, content, page, limit, sort);
                        return index_details;
                    }
                }
            }

        } else {
            Tb_data_node node = dataNodeRepository.findByNodeid(nodeids[0]);
            Tb_right_organ organnode = rightOrganRepository.findByOrganid(node.getOrganid());
            String organid = userRepository.findOrganidByUserid(userDetails.getUserid());// 获取当前用户机构id
            Tb_right_organ organuser = rightOrganRepository.findOne(organid);
            if ("unit".equals(organuser.getOrgantype())) {
                while (organnode.getOrgantype() != null && organnode.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {// 获取单位对象
                    organnode = rightOrganRepository.findOne(organnode.getParentid());
                }
                if (organuser.getOrganid().equals(organnode.getOrganid())) {
                    Page<Tb_entry_detail_capture> index_details = entryIndexCaptureService.getEntrybaseto(nodeids, condition, operator, content, page, limit, sort);
                    return index_details;
                }
            } else if ("department".equals(organuser.getOrgantype())) {
                boolean flag = false;
                while (organnode.getOrgantype() != null && organnode.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {// 获取单位对象
                    if (organuser.getOrganid().equals(organnode.getOrganid())) {
                        flag = true;
                        break;
                    }
                    organnode = rightOrganRepository.findOne(organnode.getParentid());
                }
                if (flag) {
                    Page<Tb_entry_detail_capture> index_details = entryIndexCaptureService.getEntrybaseto(nodeids, condition, operator, content, page, limit, sort);
                    return index_details;
                }
            }
        }
        return null;
    }

    public Entry getEntry(String entryid) {
        Tb_entry_index index = entryIndexRepository.findByEntryid(entryid);
        Tb_entry_detail detail = entryDetailRepository.findByEntryid(entryid);

        Entry entry = new Entry();
        entry.setEntryIndex(index);
        entry.setEntryDetial(detail);
        return entry;
    }
}
