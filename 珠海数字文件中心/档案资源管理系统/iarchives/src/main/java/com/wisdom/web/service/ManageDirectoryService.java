package com.wisdom.web.service;

import com.wisdom.util.DBCompatible;
import com.wisdom.util.GainField;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2019/6/25.
 */
@Service
@Transactional
public class ManageDirectoryService {



    @Autowired
    EntryIndexManageRepository entryIndexManageRepository;

    @Autowired
    EntryDetailManageRepository entryDetailManageRepository;

    @Autowired
    CodesettingService codesettingService;

    @Autowired
    EntryIndexService entryIndexService;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    CodesetRepository codesetRepository;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    RightOrganRepository rightOrganRepository;

    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    EntryIndexAcceptRepository entryIndexAcceptRepository;

    @Autowired
    EntryDetailAcceptRepository entryDetailAcceptRepository;

    public Page<Tb_entry_index_manage> getEntries(String nodeid, String condition, String operator, String content, int page, int limit, Sort sort) {
        Specifications sp = Specifications.where(getSearchNodeidCondition(nodeid));
        PageRequest pageRequest = new PageRequest(page - 1, limit,
                sort == null ? new Sort(Sort.Direction.DESC, "archivecode", "descriptiondate") : sort);
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        return entryIndexManageRepository.findAll(sp, pageRequest);
    }

    public Page<Tb_index_detail_manage> getIndexDetailManage(String nodeid, String condition, String operator,String content, int page, int limit, Sort sort) {
        Specifications sp = Specifications.where(getSearchNodeidCondition(nodeid));
        PageRequest pageRequest = new PageRequest(page - 1, limit,
                sort == null ? new Sort(Sort.Direction.DESC, "archivecode", "descriptiondate") : sort);
        String contentSql = "";
        if (content != null) {
            contentSql = "and "+ condition + " " + operator + " '%" + content + "%'";
        }
        String sql="select * from v_index_detail_manage where nodeid = '"+ nodeid + "' " + contentSql;
        String countSql = "select count(*) from v_index_detail_manage where nodeid = '"+ nodeid + "' " + contentSql;
        Query query = entityManager.createNativeQuery(sql, Tb_index_detail_manage.class);
        List<Tb_index_detail_manage> resultList = query.getResultList();
        Query couuntQuery = entityManager.createNativeQuery(countSql);
        int count = Integer.parseInt(couuntQuery.getResultList().get(0) + "");
        return new PageImpl(resultList, pageRequest, count);
    }

    public List<ManageEntry> getEntrys(List<Tb_entry_index_manage> list){
        List<ManageEntry> eList=new ArrayList<ManageEntry>();
        for(Tb_entry_index_manage index:list){
            ManageEntry entry = new ManageEntry();
            Tb_entry_detail_manage detail = entryDetailManageRepository.findByEntryid(index.getEntryid());
            entry.setEntryIndex(index);
            entry.setEntryDetial(detail);
            eList.add(entry);
        }
        return eList;
    }

    public static Specification<Tb_entry_index_manage> getSearchNodeidCondition(String nodeid){
        Specification<Tb_entry_index_manage> searchNodeidCondition = new Specification<Tb_entry_index_manage>() {
            @Override
            public Predicate toPredicate(Root<Tb_entry_index_manage> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get("tdn").get("nodeid"), nodeid);
                return criteriaBuilder.and(p);
            }
        };
        return searchNodeidCondition;
    }

    // 根据案卷、卷内的档号组成字段做匹配
    public List findAllByNodeidAndArchivecodeLike(Integer start, Integer limit, String nodeid, String entryid,
                                                  Sort sort) {
        // 根据档号获取实体
        Tb_entry_index_manage tb_entry_index_capture = entryIndexManageRepository.findByEntryid(entryid);
        // 获取案卷档号设置字段集合
        List<String> ajCodeSettingFieldList = codesettingService
                .getCodeSettingFields(tb_entry_index_capture.getNodeid());
        // 档号设置字段值集合
        List<String> codeSettingFieldValues = new ArrayList<>();
        for (int i = 0; i < ajCodeSettingFieldList.size(); i++) {
            String codeSettingFieldValue = GainField.getFieldValueByName(ajCodeSettingFieldList.get(i),
                    tb_entry_index_capture) + "";
            if (!"null".equals(codeSettingFieldValue) && !"".equals(codeSettingFieldValue)) {
                codeSettingFieldValues.add(codeSettingFieldValue);
            } else {
                codeSettingFieldValues.add("");
            }
        }
        // 获取卷内档号设置字段集合
        List<String> jnCodeSettingFieldList = codesettingService.getCodeSettingFields(nodeid);
        String searchCondition = entryIndexService.getJNSearchCondition(ajCodeSettingFieldList, codeSettingFieldValues,
                nodeid,
                jnCodeSettingFieldList.size() > 0 ? jnCodeSettingFieldList.get(jnCodeSettingFieldList.size() - 1) : "");
        List list = new ArrayList();
        // 返回的条件语句如果是空字符串，则返回空数据回前端
        if ("".equals(searchCondition)) {
            list.add(0);
            list.add(new ArrayList<Tb_entry_index_manage>());
            return list;
        }
        String countSql = "select count(nodeid) from tb_entry_index_manage where " + searchCondition;
        String sql = "select * from tb_entry_index_manage where " + searchCondition;
        Query qCount = entityManager.createNativeQuery(countSql);
        int count = Integer.valueOf(qCount.getSingleResult().toString());
        String sortstr = " order by archivecode desc";
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            sortstr = " order by " + order.getProperty() + " " + order.getDirection();
        }
        sql = sql + sortstr;
        Query query = entityManager.createNativeQuery(sql, Tb_entry_index_manage.class);
        query.setFirstResult(start);
        query.setMaxResults(limit);
        list.add(count);
        list.add(query.getResultList());
        return list;
    }

    public Integer getCalValue(Tb_entry_index_manage entryIndexCapture, String nodeid,
                               List<String> codeSettingFieldList) {
        Integer calValue = null;
        if (codeSettingFieldList.size() == 1) {// 档号设置只有一个计算项字段，无其它字段
            String sql = "select max("
                    + DBCompatible.getInstance().findExpressionOfToNumber(codeSettingFieldList.get(0))
                    + ") from tb_entry_index_manage where nodeid='" + nodeid + "'";
            Query query = entityManager.createNativeQuery(sql);
            int maxCalValue = query.getSingleResult() == null ? 0 : Integer.valueOf(query.getSingleResult().toString());
            if (maxCalValue == 0) {
                return 1;
            }
            calValue = maxCalValue + 1;

            return calValue;
        }
        String codeSettingFieldValues = "";
        Map<String, Map<String, String>> mapFiled = entryIndexService.getConfigMap();//获取参数设置的MAP
        List<Tb_data_template> enumList = templateRepository.getByNodeidFtype("enum", nodeid);//获取某节点的模板中属于enum的字段
        List<String> spList = codesetRepository.findSplitcodeByDatanodeid(nodeid);
        for (int i = 0; i < codeSettingFieldList.size() - 1; i++) {
            String value = "";
            // 通过反射获得档号字段的页面输入值，不含最后一个（计算项）
            String codeSettingFieldValue = GainField.getFieldValueByName(codeSettingFieldList.get(i), entryIndexCapture)
                    + "";
            codeSettingFieldValue = entryIndexService.getConfigByName(codeSettingFieldList.get(i), codeSettingFieldValue, enumList, mapFiled);
            if (isNumeric(codeSettingFieldValue)) {
                Integer length = Integer
                        .parseInt(codesetRepository.findFieldlengthByDatanodeid(nodeid).get(i).toString());
                value = entryIndexService.alignValue(length,Integer.valueOf(codeSettingFieldValue));
            } else {
                if (codeSettingFieldList.get(i).equals("organ")) {
                    Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
                    Tb_right_organ right_organ = rightOrganRepository.findByOrganid(node.getRefid());
                    if (right_organ.getCode() != null && !right_organ.getCode().equals("")) {
                        value = right_organ.getCode();
                    } else {
                        value = codeSettingFieldValue;
                    }
                } else {
                    value = codeSettingFieldValue;
                }
            }
            if (!"null".equals(codeSettingFieldValue) && !"".equals(codeSettingFieldValue)) {
                if (i < codeSettingFieldList.size() - 2) {
                    codeSettingFieldValues += value + spList.get(i);
                }
                if (i == codeSettingFieldList.size() - 2) {
                    codeSettingFieldValues += value;
                }
            } else {// 页面中档号设置字段无输入值
                return null;
            }
        }
        String calValueFieldCode = codeSettingFieldList.get(codeSettingFieldList.size() - 1);
        String sql = "select max(" + DBCompatible.getInstance().findExpressionOfToNumber(calValueFieldCode)
                + ") from tb_entry_index_manage where archivecode like '" + codeSettingFieldValues + "%' and nodeid='"
                + nodeid + "'";
        Query query = entityManager.createNativeQuery(sql);
        int maxCalValue = query.getSingleResult() == null ? 0 : Integer.valueOf(query.getSingleResult().toString());
        if (maxCalValue == 0) {
            return 1;
        }
        calValue = maxCalValue + 1;
        return calValue;
    }

    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public ManageEntry getEntry(String entryid) {
        Tb_entry_index_manage index = entryIndexManageRepository.findByEntryid(entryid);
        Tb_entry_detail_manage detail = entryDetailManageRepository.findByEntryid(entryid);

        ManageEntry entry = new ManageEntry();
        entry.setEntryIndex(index);
        entry.setEntryDetial(detail);
        return entry;
    }

    public ManageEntry saveEntry(ManageEntry entry, String type) {
        Tb_entry_index_manage index = entry.getEntryIndex();
        Tb_data_node dataNodeid = new Tb_data_node();
        dataNodeid.setNodeid(index.getNodeid());
        index.setTdn(dataNodeid);
        String currentEntryid = index.getEntryid();
        String eleids = index.getEleid();
        if (type.equals("modify")) {
            Tb_entry_index_manage entryInfo = entryIndexManageRepository.findByEntryid(entry.getEntryid());
            if (entryInfo != null) {
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
            }
        } else {
            index.setFlagopen(null);//新增的条目开放状态都是空的
        }
        index.setEleid(null);

        index.setKccount(index.getFscount());// 新增还是修改,库存份数都等于份数
        index = entryIndexManageRepository.save(index);
        Tb_entry_detail_manage detail = entry.getEntryDetail();
        detail.setEntryid(index.getEntryid());
        detail = entryDetailManageRepository.save(detail);

        ManageEntry result = new ManageEntry();
        result.setEntryIndex(index);
        result.setEntryDetial(detail);
        return result;
    }

    public Integer delEntry(String[] entryidData) {
        //删除关联的详细表
        entryDetailManageRepository.deleteByEntryidIn(entryidData);
        //删除条目
        return entryIndexManageRepository.deleteByEntryidIn(entryidData);
    }

    public List<Tb_entry_index_manage> getManageEntryList(String nodeid, String condition, String operator, String content,
                                             Tb_entry_index_manage formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,
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
        Specification<Tb_entry_index_manage> searchNodeidCondition = ClassifySearchService.getSearchNodeidManage(nodeids);
        Specifications specifications = Specifications.where(searchNodeidCondition);
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        Specification<Tb_entry_index_manage> formAdvancedSearch = ClassifySearchService
                .getFormAdvancedManageSearch(formConditions, formOperators, logic);
        Specification<Tb_entry_index_manage> dateRangeCondition = ClassifySearchService
                .getDateRangeManageCondition(daterangedata);
        return entryIndexManageRepository.findAll(specifications.and(formAdvancedSearch).and(dateRangeCondition),
                new Sort("archivecode"));
    }

    public String[] findEntryids(String nodeid) {
        return entryIndexManageRepository.findEntryidsByNodeid(nodeid);
    }

    //执行退回目录传输
    public int[] backAcceptSubmit(String[] entryidData){
        int insertIndexes = 0;
        int insertDetails = 0;
        List<String[]> subAry = new InformService().subArray(entryidData, 1000);//处理ORACLE1000参数问题
        for (String[] ary : subAry) {
            //目录管理条目退回目录传输
            insertIndexes += entryIndexAcceptRepository.moveAccepts(ary);
            String[] insertIds = new String[ary.length];
            for (int i = 0; i < ary.length; i++){
                insertIds[i] = String.format("%1$-36s",(String) ary[i]);
            }
            //目录管理条目详情退回目录传输
            insertDetails += entryDetailAcceptRepository.moveAcceptdetails(insertIds);
            //删除目录管理表中的数据
            delEntry(insertIds);
        }
        return new int[]{insertIndexes, insertDetails};
    }
}
