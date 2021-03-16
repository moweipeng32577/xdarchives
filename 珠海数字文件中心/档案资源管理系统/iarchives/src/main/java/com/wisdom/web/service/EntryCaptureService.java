package com.wisdom.web.service;

import com.wisdom.util.DBCompatible;
import com.wisdom.util.GainField;
import com.wisdom.util.ZipUtil;
import com.wisdom.web.controller.ManagementController;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.apache.commons.io.FileUtils;
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
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static com.wisdom.web.service.ThematicService.delFolder;

/**
 * 档案条目采集业务类(包含跨表业务)
 * Created by Rong on 2017/11/13.
 */
@Service
@Transactional
public class EntryCaptureService {

    @PersistenceContext
    EntityManager entityManager;
    
    @Autowired
    CodesetRepository codesetRepository;

    @Autowired
    EntryIndexCaptureRepository entryIndexCaptureRepository;

    @Autowired
    EntryDetailCaptureRepository entryDetailCaptureRepository;

    @Autowired
    ElectronicCaptureRepository electronicCaptureRepository;

    @Autowired
    TransdocEntryRepository transdocEntryRepository;

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    OrganService organService;

    @Autowired
    FundsService fundsService;

    @Autowired
    ElectronicService electronicService;

    @Autowired
    RecyclebinService recyclebinService;

    @Autowired
    ElectronicRecyclebinRepository electronicRecyclebinRepository;

    @Autowired
    ClassifySearchService classifySearchService;

    @Autowired
    ElectronicVersionCaptureRepository electronicVersionCaptureRepository;

    @Autowired
    ThumbnailRepository thumbnailRepository;

    @Autowired
    DataNodeExtRepository dataNodeExtRepository;

    @Autowired
    EntryIndexTempRepository entryIndexTempRepository;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    CodesettingService codesettingService;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    RightOrganRepository rightOrganRepository;

    @Autowired
    SystemConfigRepository systemConfigRepository;

    @Autowired
    TemplateService templateService;

    @Autowired
    EntryCaptureService entryCaptureService;

    @Autowired
    EntryIndexCaptureService entryIndexCaptureService;

    @Autowired
    ManagementController managementController;

    @Autowired
    UserNodeSortRepository userNodeSortRepository;

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录
    
    public boolean isNumeric(String str){
    	Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public Page<Tb_index_detail_capture> getEntries(String moduleName,String nodeid, String status, String docid, String condition, String operator,
                                                    String content, int page, int limit, Sort sort,String parententryid) {
        Specifications sp = Specifications.where(getSearchNodeidCondition(nodeid));
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        String sortStr="";//排序
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        List<Tb_user_node_sort> userNodeSorts = userNodeSortRepository.findByNodeidAndUseridOrderBySortsequence(nodeid,userDetails.getUserid());
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            if("eleid".equals(order.getProperty())){
                sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
            }else {
                sortStr = " order by " + order.getProperty() + " " + order.getDirection();
            }
        }else{
            if(userNodeSorts.size()>0){  //设置了节点排序
                for(int i=0;i<userNodeSorts.size();i++){
                    if(i==0){
                        sortStr = " order by " + userNodeSorts.get(i).getFieldcode()+ " "+ userNodeSorts.get(i).getSorttype();
                    }else {
                        sortStr = sortStr + "," + userNodeSorts.get(i).getFieldcode()+ " "+ userNodeSorts.get(i).getSorttype();
                    }
                }
            }else{
                sortStr = " order by archivecode desc, descriptiondate desc ";
            }
        }
        String nodeIdSql="";
        String shCondition="";//审核筛选
        if(docid!=null && !"".equals(docid)){//数据审核模块查看单据的条目详细信息
            shCondition=" and entryid in(select entryid from tb_transdoc_entry where docid ='"+ docid +"' ";
            nodeIdSql+=" nodeid ='"+nodeid+"' ";
            if(parententryid!=null||"".equals(parententryid)){
                if("true".equals(parententryid)){
                    shCondition+=")";
                }else {
                    Tb_entry_index_capture tb_entry_index_capture = entryIndexCaptureRepository.findByEntryid(parententryid);
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
                    shCondition +=") and (("+ entryIndexService.getJNSearchCondition(ajCodeSettingFieldList, codeSettingFieldValues,
                            nodeid, jnCodeSettingFieldList.size() > 0 ? jnCodeSettingFieldList.get(jnCodeSettingFieldList.size() - 1) : "");
                    shCondition+=") or archivecode like '"+tb_entry_index_capture.getArchivecode()+"%' ) ";
                }
            }else{
                shCondition+=")";
            }
        }else {//数据采集模块查看单据的条目详细信息
            shCondition=" and entryid not in(select entryid from tb_transdoc_entry where status='"+status+"')";
            shCondition+="and entryid not in(select entryid from tb_transdoc_preview where nodeid ='"+nodeid+"')";//加入移交筛选
            nodeIdSql+="nodeid ='"+nodeid+"'";
        }
        String tempStr="";
        if("预归档未归".equals(moduleName)){//预归档未归查看的节点数据要过滤掉临时表的个人用户数据
            String uniqueTag=BatchModifyService.getUniquetagByType("cjgd");
            tempStr=" and entryid not in(select entryid from tb_entry_index_temp where uniquetag='"+uniqueTag+"')";
        }
        String searchCondition = "";//检索框
        if (content != null && !"".equals(content)) {// 输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        String sql = "select sid.* from v_index_detail_capture sid where "+nodeIdSql+ searchCondition +shCondition + tempStr + sortStr;
        String countSql = "select count(*) from v_index_detail_capture sid where "+nodeIdSql+ searchCondition +shCondition + tempStr;
        Query query = entityManager.createNativeQuery(sql, Tb_index_detail_capture.class);
        query.setFirstResult((page - 1) * limit);
        query.setMaxResults(limit);
        List<Tb_index_detail_capture> resultList = query.getResultList();
        Query couuntQuery = entityManager.createNativeQuery(countSql);
        int count = Integer.parseInt(couuntQuery.getResultList().get(0) + "");
        return new PageImpl(resultList, pageRequest, count);
    }

    public Page<Tb_index_detail_capture> findPreviewEntryindexcaptureBySearch(String nodeid, String condition, String operator,
                                                    String content, int page, int limit, Sort sort) {
        String sortStr="";//排序
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            if ("eleid".equals(order.getProperty())) {
                sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
            } else {
                sortStr = " order by " + order.getProperty() + " " + order.getDirection();
            }
        }
       String searchCondition="";
        if (content != null && !"".equals(content)) {// 输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        String sql = "select sid.* from v_index_detail_capture sid inner join tb_transdoc_preview tp on sid.entryid=tp.entryid" +
                " where tp.nodeid='"+nodeid+"'"+ searchCondition  + sortStr;
        String countSql = "select count(*) from v_index_detail_capture sid inner join tb_transdoc_preview tp on sid.entryid=tp.entryid" +
                " where tp.nodeid='"+nodeid+"'"+ searchCondition;
        Query query = entityManager.createNativeQuery(sql, Tb_index_detail_capture.class);
        query.setFirstResult((page - 1) * limit);
        query.setMaxResults(limit);
        List<Tb_index_detail_capture> resultList = query.getResultList();
        Query couuntQuery = entityManager.createNativeQuery(countSql);
        int count = Integer.parseInt(couuntQuery.getResultList().get(0) + "");
        return new PageImpl(resultList, pageRequest, count);
    }

    /**
     * 加入预归档
     * @param entryids  条目id
     * @param nodeid  未归节点
     * @param condition  条件字段
     * @param operator  like、equals等
     * @param content   搜索框
     * @param uniquetag  临时表个人标记
     * @param selectAll   全选
     * @param targetNodeid  归档节点
     * @return
     */
    public int entryIndexYgd(String entryids,String nodeid,String condition,String operator,String content,String uniquetag,String selectAll,String targetNodeid){//添加条目到预归档临时表
        List<Tb_entry_index_temp> tempList = new ArrayList<>();
        String entryidStr ="";
        if(entryids!=null&&!"".equals(entryids.trim())){//非选择所有页
            entryidStr = " and sid.entryid in('" + String.join("','", entryids.split(",")) + "') ";
        }
        String searchCondition = "";
        if (content != null && !"".equals(content.trim())&&"1".equals(selectAll)) {//输入框检索   选择所有页
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        String nodeidStr="";
        if(nodeid != null && !"".equals(nodeid.trim())&&"1".equals(selectAll)){//选择所有页
            nodeidStr=" and sid.nodeid='"+nodeid+"' ";
            String tempStr=" and sid.entryid not in (select entryid from tb_entry_index_temp where uniquetag='"+uniquetag+"') ";//避免重复插入数据
            nodeidStr=nodeidStr+tempStr;
        }
        if("".equals(entryidStr+searchCondition+nodeidStr)){//没有被选择的条目时直接返回
            return tempList.size();
        }
        String sql = "select sid.* from v_index_detail_capture sid where 1=1 " + entryidStr+searchCondition+nodeidStr;
        Query query = entityManager.createNativeQuery(sql, Tb_index_detail_capture.class);
        List<Tb_index_detail_capture> resultList = query.getResultList();

        Map<String,Integer> map = new HashMap<>();
        for (Tb_index_detail_capture entry_index_capture : resultList) {
            Tb_entry_index_temp entry_index_temp = new Tb_entry_index_temp();
            BeanUtils.copyProperties(entry_index_capture, entry_index_temp,new String[]{"sortsequence"});
            entry_index_temp.setUniquetag(uniquetag);
            entry_index_temp.setArchivecode("");
            entry_index_temp.setNodeid(targetNodeid);
            tempList.add(entry_index_temp);
        }
        entryIndexTempRepository.save(tempList);
        return tempList.size();
    }

    /**插入预归档指定位置
     * 之前的预归档数据指定位置往下的条目（包括指定位置的条目）的都要往后移
     * @param entryids   选中的未归条目
     * @param insertLine  指定的行
     * @param targetNodeid  归档节点
     * @return
     */
    public int entryIndexInsertYgd(String entryids,String insertLine,String targetNodeid){//添加条目到预归档临时表
        //添加主表数据
        String uniquetag=BatchModifyService.getUniquetagByType("cjgd");
        String count = entryIndexTempRepository.findByUniquetag(uniquetag);//预归档最大的归档顺序号
        List<Tb_entry_index_capture> result = entryIndexCaptureRepository.findByEntryidIn(entryids.split(","));
        List<Tb_entry_index_temp> tempList = new ArrayList<>();
        Map<String,Integer> map = new HashMap<>();
        for (int i = 0; i < entryids.split(",").length; i++) {//插入预归档记录的【归档顺序】指定行数值insertLine
            try{
                if(Integer.valueOf(count)-(Integer.valueOf(insertLine))>-1){//预归档行数大于等于指定行数
                    map.put(entryids.split(",")[i],i+Integer.valueOf(insertLine));////将id与顺序对应
                }else{//加到最后边
                    map.put(entryids.split(",")[i],i+Integer.valueOf(count)+1);////将id与顺序对应
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        for (Tb_entry_index_capture entry_index_capture : result) {
            String nodeid = entry_index_capture.getNodeid();
            String defaultFunds = templateRepository.findFdefaultByFieldcodeAndNodeid("funds", nodeid);
            String organid = entryIndexService.getOrganidByNodeidLoop(nodeid);// 机构id
            if (defaultFunds != null && !"".equals(defaultFunds)) {
                entry_index_capture.setFunds(defaultFunds);
            } else {
                String funds = fundsService.getOrganFunds(organid);
                if (funds != null) {// 如果是单位机构的话,直接填充获取到的全宗号
                    entry_index_capture.setFunds(funds);
                } else {// 如果是部门机构的话,需要获取到所属单位的全宗号
                    String unitOrganid = entryIndexService.getOrganInfo(organid);
                    String unitFunds = fundsService.getOrganFunds(unitOrganid);
                    entry_index_capture.setFunds(unitFunds == null ? "" : unitFunds);
                }
            }
            Tb_entry_index_temp entry_index_temp = new Tb_entry_index_temp();
            BeanUtils.copyProperties(entry_index_capture, entry_index_temp,new String[]{"sortsequence"});
            //添加副表数据
            Tb_entry_detail_capture detail_capture = entryDetailCaptureRepository.findByEntryid(entry_index_capture.getEntryid());
            if (detail_capture != null) {
                BeanUtils.copyProperties(detail_capture, entry_index_temp);
            }
            entry_index_temp.setUniquetag(uniquetag);
            entry_index_temp.setArchivecode("");
            entry_index_temp.setNodeid(targetNodeid);
            if(map.get(entry_index_temp.getEntryid())==null){
                entry_index_temp.setSortsequence(0);
            }else{
                entry_index_temp.setSortsequence(map.get(entry_index_temp.getEntryid()));
            }
            tempList.add(entry_index_temp);
        }

        //查找指定位置之后的预归档条目
        List<Tb_entry_index_temp> editList=new ArrayList<>();
        int line=0;
        try{
            line=Integer.valueOf(insertLine);
            if(line>0){
                editList=entryIndexTempRepository.findByUniquetagAndSortsequence(uniquetag,line);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        entryIndexTempRepository.save(tempList);
        //指定位置之后的预归档条目，顺序号加插入条目数
        if(Integer.valueOf(count)-(Integer.valueOf(insertLine))>-1&&tempList.size()>0){//预归档行数大于等于指定行数
            try{
                for(Tb_entry_index_temp temp:editList){
                    temp.setSortsequence(Integer.valueOf(temp.getSortsequence())+tempList.size());//顺序号加插入条目数
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return tempList.size();
    }

    public int entryIndexYgdDel(String entryids,String uniquetag) {//取消预归档临时表
        String[] entryArr=entryids.split(",");
        int i=entryIndexTempRepository.deleteByEntryidInAndUniquetag(entryArr,uniquetag);
        return i;
    }

    public ExtMsg entryTempEdit(EntryCapture entry,String dataNodeid){
        //重新设置档号
        Tb_entry_index index=new  Tb_entry_index();
        BeanUtils.copyProperties(entry,index,new String[]{"entryid","nodeid"});
        index.setNodeid(dataNodeid);
        int state=2;
        /*int state=getArchvieCode(index,dataNodeid);
        if(state==-1){
            return  new ExtMsg(true, "0", null);
        }*/
        // 把表单数据保存到临时表
        String entryid=entry.getEntryid();
        List<Tb_entry_index_temp> temps=entryIndexTempRepository.findByEntryidIn(new String[]{entryid});
        Tb_entry_index_temp temp=temps.get(0);
        List<Tb_data_template> editTemplates=templateRepository.findEditFormByNode(dataNodeid);
        if(editTemplates.size()>0){//只修改了编辑字段，只修改编辑字段到临时表
            List<Tb_codeset> codesets=codesetRepository.findEditCodeset(dataNodeid);//档编辑字段含档号字段
            if(codesets.size()>0){//预归档编辑字段含档号字段，获取档号设置字段和预归档编辑字段和archivecode字段的并集
                state=getArchvieCode(index,dataNodeid, temp.getArchivecode(),"数据采集");
                if(state==-1){//档号重复
                    return  new ExtMsg(true, "0", null);
                }
                editTemplates=templateRepository.findEditFormAndCodesetByNode(dataNodeid);
            }
            for(Tb_data_template template:editTemplates){//修改编辑字段到临时表
                String fieldcode=template.getFieldcode();
                String fieldValue = GainField.getFieldValueByName(fieldcode, entry) != null
                        ? (String) GainField.getFieldValueByName(fieldcode, entry) : "";
                GainField.setFieldValueByName(fieldcode, temp, fieldValue);
            }
        }else{//提交整个表单字段，可以复制有值的字段到临时表，entryid和nodeid不复制
            state=getArchvieCode(index,dataNodeid, temp.getArchivecode(),"数据采集");
            if(state==-1){//档号重复
                return  new ExtMsg(true, "0", null);
            }
            //BeanUtils.copyProperties(entry,temp,new String[]{"entryid","nodeid"});//nodeid会清除，非mysql会entryid冲突
            List<Tb_data_template> templates=templateRepository.findFormByNode(dataNodeid);
            for(Tb_data_template template:templates){//修改表单字段
                String fieldcode=template.getFieldcode();
                if(!"entryid".equals(fieldcode)){//entryid不处理
                    String fieldValue = GainField.getFieldValueByName(fieldcode, entry) != null
                            ? (String) GainField.getFieldValueByName(fieldcode, entry) : "";
                    GainField.setFieldValueByName(fieldcode, temp, fieldValue);
                }
            }
        }

        if(state==0){
            temp.setArchivecode("");
        }else if(state==1){//档号不重复
            temp.setArchivecode(index.getArchivecode());
        }

        return new ExtMsg(true, "1", null);
    }

    /**
     *
     * @param entryIndex  提交条目
     * @param nodeid   目标节点
     * @param archivecodeOld   临时表该条目的档号
     * @return
     */
    public int getArchvieCode(Tb_entry_index entryIndex,String nodeid,String archivecodeOld,String type){
        List<String> codeSettingFieldList = codesettingService.getCodeSettingFields(nodeid);// 获取档号设置字段集合
        if (codeSettingFieldList.size() == 0) {// 档号字段未设置
            return 0;//档号没设置
        }
        String code = managementController.alignArchivecode(entryIndex, "").getArchivecode();
        if (code!=null&&!"".equals(code)) {// 如果档号不为空
            // 查询当前节点所有数据的档号,判断档号的唯一性
            List<String> archivecode;
            String uniquetag="_cjgd-";
            if("数据采集".equals(type)){
                archivecode = entryIndexCaptureRepository.findCodeByNodeidAndCode(nodeid,code);
            }else{//数据管理
                archivecode = entryIndexRepository.findCodeByNodeidAndCode(nodeid,code);
                uniquetag="_glgd-";
            }

            if (archivecode.size() > 0) { // 判断采集表或者数据管理表的重复
                return -1;//档号重复
            }
            List<String> tempArchivecode=entryIndexTempRepository.findArchivecodeByNodeidAndCode(nodeid,code,uniquetag);
            if (tempArchivecode.size() > 0) {// 判断临时表的重复
                if (!code.equals(archivecodeOld)) {//新档号等于临时表同entryid的档号不算重复
                    return -1;//档号重复
                }
            }
            return 1;//档号不重复
        }else{
            return 0;//档号没设置
        }
    }

    public List<Tb_entry_index_capture> getEntries(String moduleName,String nodeid, String status, String docid, String
            condition, String operator, String content) {
        Specifications sp = Specifications.where(getSearchNodeidCondition(nodeid));
        List<Tb_transdoc_entry> transdocEntries = new ArrayList<>();
        String searchCondition="";
        if (docid != null && !"".equals(docid)) {// 数据审核模块查看单据的条目详细信息
            transdocEntries = transdocEntryRepository.findByStatusAndDocid(status, docid);
            searchCondition = entryIndexService.getSqlByConditions(condition, content, "ted") + " and tei.entryid  in(select entryid from tb_transdoc_entry where status='"+status+"')";
        } else {// 数据采集模块查看单据的条目详细信息
            transdocEntries = transdocEntryRepository.findByStatus(status);
            searchCondition = entryIndexService.getSqlByConditions(condition, content, "ted") + " and tei.entryid not in(select entryid from tb_transdoc_entry where status='" + status + "')";
        }
        if(content != null&&condition.length()==3&&condition.startsWith("f")){//搜索备用字段
            List<Tb_entry_index_capture> resultList = new ArrayList<>();
            String sql = "select tei.* from (select * from tb_entry_index_capture where nodeid='"+nodeid+"') tei inner join tb_entry_detail_capture ted on tei.entryid=ted.entryid "+
                    " where "+searchCondition;
            String countSql = "select count(nodeid) from (select * from tb_entry_index_capture where nodeid='"+nodeid+"') tei inner join tb_entry_detail_capture ted on tei.entryid=ted.entryid "+
                    " where "+searchCondition;
            Query query = entityManager.createNativeQuery(sql,Tb_entry_index_capture.class);
            resultList = query.getResultList();
            return resultList;
        }
        String[] entryidData = GainField.getFieldValues(transdocEntries, "entryid").length == 0 ? new String[] { "" }
                : GainField.getFieldValues(transdocEntries, "entryid");
        Specification<Tb_entry_index_capture> searchEntryidsCondition = getSearchEntryidsCondition(entryidData);
        Specification<Tb_entry_index_capture> searchEntryidsExcludeCondition = getSearchEntryidsExcludeCondition(
                entryidData);
        if ("AuditController".equals(moduleName)) {// 数据审核界面查看条目，获得条目状态为“待审核”的记录
            sp = sp.and(searchEntryidsCondition);
        }
        if ("AcquisitionController".equals(moduleName)) {// 数据采集界面查看条目，排除条目状态为“待审核”的记录
            sp = sp.and(searchEntryidsExcludeCondition);
        }
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        return entryIndexCaptureRepository.findAll(sp);
    }

    public EntryCapture getEntry(String entryid) {
        Tb_entry_index_capture index = entryIndexCaptureRepository.findByEntryid(entryid);
        Tb_entry_detail_capture detail = entryDetailCaptureRepository.findByEntryid(entryid);

        EntryCapture entry = new EntryCapture();
        entry.setEntryIndex(index);
        entry.setEntryDetial(detail);
        return entry;
    }

    public List<EntryCapture> getEntrys(List<Tb_entry_index_capture> list){
        List<EntryCapture> eList=new ArrayList<EntryCapture>();
        for(Tb_entry_index_capture index:list){
            EntryCapture entry = new EntryCapture();
            Tb_entry_detail_capture detail = entryDetailCaptureRepository.findByEntryid(index.getEntryid());
            entry.setEntryIndex(index);
            entry.setEntryDetial(detail);
            eList.add(entry);
        }
        return eList;
    }
    

    public EntryCapture saveEntry(EntryCapture entry, String type) {
        Tb_entry_index_capture index = entry.getEntryIndex();
        String currentEntryid = index.getEntryid();
        String eleids = index.getEleid();
        if (type.equals("modify")) {
            Tb_entry_index_capture entryInfo = entryIndexCaptureRepository.findByEntryid(entry.getEntryid());
            if (entryInfo != null) {
                if (index.getTitle() == null) {// 题名
                    index.setTitle(entryInfo.getTitle().trim());
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
            index.setTitle(index.getTitle().trim());
        	index.setFlagopen(null);//新增的条目开放状态都是空的
        }
        Tb_entry_detail_capture detail1 = null;
        if ("".equals(eleids)) {// 如果没有电子文件数据的话,就直接保存
            index.setEleid(null);
        } else {
            String[] eleidArr = eleids.split(",");
            List<String> savedEntryid = electronicCaptureRepository.findEntryidByEleidIn(eleidArr);
            if (currentEntryid != null && !"".equals(currentEntryid)) {// 修改操作，当前需保存的条目的entryid不为空
                index.setEleid(String.valueOf(eleidArr.length));
            } else {// 增加(著录)操作，当前需保存的条目的条目id为空
                if (savedEntryid.get(0) == null) {// 连续著录第一次操作，当前电子文件未曾保存，无对应entryid
                    index.setEleid(String.valueOf(eleidArr.length));
                    index = entryIndexCaptureRepository.save(index);
                    electronicCaptureRepository.updateEntryid(index.getEntryid(), eleidArr);
                    for(String eleid : eleidArr){
                        //转存原来的电子文件到新的文件夹
//                        electronicService.renameToCapture("capture",index.getEntryid(),eleid);
                        detail1 = electronicService.renameToCapture("capture",index.getEntryid(),eleid, entry.getNodeid());
                    }
                } else {// 连续著录第二次及后续操作，当前电子文件曾保存过，有对应entryid
                    index.setEleid(null);
                }
            }
        }
        index.setKccount(index.getFscount());// 新增还是修改,库存份数都等于份数
        index = entryIndexCaptureRepository.save(index);
        Tb_entry_detail_capture detail = entry.getEntryDetail();
        if(detail1 != null) {
            BeanUtils.copyProperties(detail1,detail);
        }
        detail.setEntryid(index.getEntryid());
        Tb_data_node_mdaflag dataNode  = dataNodeExtRepository.findNodeid(entry.getNodeid());
        if(!(dataNode != null && "modify".equals(type)) ) {
            detail = entryDetailCaptureRepository.save(detail);
        }

        EntryCapture result = new EntryCapture();
        result.setEntryIndex(index);
        result.setEntryDetial(detail);
        return result;
    }

    public Integer delEntry(String[] entryidData) {
        //删除关联的详细表
        entryDetailCaptureRepository.deleteByEntryidIn(entryidData);
        //删除条目关联的电子文件
        delElectronicCapture(entryidData);
        //删除回收站关联的电子文件记录和电子文件
        deleteRecyclebin(entryidData);
        //删除条目关联的电子文件记录
        electronicCaptureRepository.deleteByEntryidIn(entryidData);
        //删除电子文件历史版本表数据
        electronicVersionCaptureRepository.deleteByEntryidIn(entryidData);
        //删除条目
        return entryIndexCaptureRepository.deleteByEntryidIn(entryidData);
    }

    public Integer delEntryOnly(String[] entryidData) {
        //删除关联的详细表
        entryDetailCaptureRepository.deleteDetails(entryidData);
        //删除条目
        return entryIndexCaptureRepository.deleteIndexes(entryidData);
    }

    public void delEntryRef(String[] entryidData) {
        //删除条目关联的电子文件
        delElectronicCapture(entryidData);
        //删除回收站关联的电子文件记录和电子文件
        deleteRecyclebin(entryidData);
        //删除条目关联的电子文件记录
        electronicCaptureRepository.deleteByEntryidIn(entryidData);
        //删除电子文件历史版本表数据
        electronicVersionCaptureRepository.deleteByEntryidIn(entryidData);
    }

    /**
     * 删除电子文件
     * @param entryidData
     */
    public void delElectronicCapture(String[] entryidData){
        //富滇由于之前旧的数据是年月日文件夹下，有可能存在多条目关联一个电子文件
        //需要保护以前的旧数据，需要一条条判断改文件是否被多个条目关联，如果被多个条目关联，不能删除
//        for(String entryid:entryidData){
            List<Tb_electronic_capture> electronics = electronicCaptureRepository.findByEntryidIn(entryidData);
            for(Tb_electronic_capture electronic : electronics){
                //当只有一条电子文件记录才允许删除
                if(electronicCaptureRepository.findByFilepathAndFilename(electronic.getFilepath(),electronic
                        .getFilename()).size()==1){
                    File srcFile = new File(rootpath + electronic.getFilepath() + "/" + electronic
                            .getFilename());
                    srcFile.delete();//删除电子文件

                    String yasuo_filepath =rootpath + electronic.getFilepath() + "/" + electronic.getFilename().replace(".", "_compression.");//如果存在該圖像文件的壓縮文件，則把壓縮文件也一同刪除
                    File file =  new File(yasuo_filepath);//压缩图片文件路径
                    if(file.exists()){
                        file.delete();
                    }
                    //判断文件夹是否还有文件，没有则把文件夹删除
                    File folder =new File(rootpath + electronic.getFilepath());
                    if(folder.listFiles()!=null&&folder.listFiles().length==0){
                        //删除文件夹
                        folder.delete();
                    }
                }
            }
//        }
    }

    /**
     * 删除回收站关联的电子文件记录和电子文件
     * @param entryidData
     */
    public void deleteRecyclebin(String[] entryidData){
        List<String> recycleids = electronicRecyclebinRepository.findRecycleidByEntryidIn(entryidData);
        String[] recycleidsArr = new String[recycleids.size()];
        recycleids.toArray(recycleidsArr);
        recyclebinService.delRecyclebin(recycleidsArr);
    }

    public void updateEleNum(String entryid, String operate, int count) {
        Tb_entry_index_capture index = entryIndexCaptureRepository.findByEntryid(entryid);
        int num = count;
        switch (operate) {
            case "add":
                if (index.getEleid() == null) {
                    index.setEleid(String.valueOf(num));
                } else {
                    num = Integer.parseInt(index.getEleid().trim());
                    index.setEleid(String.valueOf(num + count));
                }
                entryIndexCaptureRepository.save(index);
                break;
            case "remove":
                if (index.getEleid() == null) {
                    return;
                }
                num = Integer.parseInt(index.getEleid().trim());
                if (num == count) {
                    index.setEleid(null);
                } else {
                    index.setEleid(String.valueOf(num - count));
                }
                entryIndexCaptureRepository.save(index);
                break;
            default:
                break;
        }
    }

    public List<Tb_entry_index_capture> getEntryCaptureList(String nodeid, String condition, String operator, String content) {
        Specifications specifications = Specifications.where(getSearchNodeidCondition(nodeid));
        //过滤在移交审核的条目
        List<Tb_transdoc_entry> transdocEntries = transdocEntryRepository.findByStatus("待审核");
        String[] entryidData = GainField.getFieldValues(transdocEntries, "entryid");
        if(entryidData==null||entryidData.length==0){
            entryidData=new String[]{""};
        }
        Specification<Tb_entry_index_capture> searchEntryidsExcludeCondition = getSearchEntryidsExcludeCondition(entryidData);
        specifications = specifications.and(searchEntryidsExcludeCondition);
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        return entryIndexCaptureRepository.findAll(specifications);
    }

    public static Specification<Tb_entry_index_capture> getSearchNodeidCondition(String nodeid){
        Specification<Tb_entry_index_capture> searchNodeidCondition = new Specification<Tb_entry_index_capture>() {
            @Override
            public Predicate toPredicate(Root<Tb_entry_index_capture> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get("nodeid"), nodeid);
                return criteriaBuilder.and(p);
            }
        };
        return searchNodeidCondition;
    }

    public static Specification<Tb_entry_index_capture> getSearchEntryidsCondition(String[] entryidData){
        Specification<Tb_entry_index_capture> searchEntryidsCondition = null;
        searchEntryidsCondition = new Specification<Tb_entry_index_capture>() {
            @Override
            public Predicate toPredicate(Root<Tb_entry_index_capture> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                CriteriaBuilder.In in = criteriaBuilder.in(root.get("entryid"));
                for (String entryid:entryidData) {
                    in.value(entryid);
                }
                return criteriaBuilder.or(in);
            }
        };
        return searchEntryidsCondition;
    }

    public static Specification<Tb_entry_index_capture> getSearchEntryidsExcludeCondition(String[] entryidData){
        Specification<Tb_entry_index_capture> searchEntryidsExcludeCondition = null;
        searchEntryidsExcludeCondition = new Specification<Tb_entry_index_capture>() {
            @Override
            public Predicate toPredicate(Root<Tb_entry_index_capture> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate[] predicates = new Predicate[entryidData.length];
                for(int i=0;i<entryidData.length;i++){
                    predicates[i] = criteriaBuilder.notEqual(root.get("entryid"), entryidData[i]);
                }
                return criteriaBuilder.and(predicates);
            }
        };
        return searchEntryidsExcludeCondition;
    }

    public EntryCapture getNewFileFormData(String entryid, String archivecode, String nodeid) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Tb_entry_index_capture index = entryIndexCaptureRepository.findByEntryid(entryid);
        //获取同档号的条目集合
        List<Tb_entry_index_capture> entryIndexCaptures = entryIndexCaptureRepository
                .findAllByNodeidAndArchivecodeLike(index.getNodeid(), archivecode);
        String organid = entryIndexService.getOrganidByNodeidLoop(nodeid);// 机构id
        String organ = organService.findOrganByOrganid(organid);// 机构名称
        String funds = fundsService.getOrganFunds(organid);// 全宗号
        EntryCapture entry = new EntryCapture();
        entry.setNodeid(nodeid);
        //获取真实姓名
        entry.setDescriptionuser(userDetails.getRealname());
        
        // 设置起止时间，提取同案卷档号的卷内记录的最早和最晚的文件日期，作为案卷记录的起止时间
        String fieldate = templateRepository.findFieldCodeByNodeidAndFieldName(nodeid, "起止年月");
        GainField.setFieldValueByName(fieldate, entry, getMaxMinDate(entryIndexCaptures));
        // 设置卷内文件数，即同案卷的卷内文件记录数
        String num = templateRepository.findFieldCodeByNodeidAndFieldName(nodeid, "卷内文件数");
        GainField.setFieldValueByName(num, entry, String.valueOf(entryIndexCaptures.size()));
        
        //设置机构名称
        entry.setOrgan(organ);
        //设置全宗号
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
        //设置页数，合计同案卷的卷内文件的页数
        for (Tb_entry_index_capture capture : entryIndexCaptures) {
            int page;
            try {
                page = Integer.valueOf(capture.getPages());
                pages = pages + page;
            } catch (Exception e) {
            }
        }
        entry.setPages(String.valueOf(pages));
        return entry;
    }

    private String getMaxMinDate(List<Tb_entry_index_capture> entryIndexCaptures) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String date = "";

        List<Date> dates = new ArrayList<>();
        for (int i = 0; i < entryIndexCaptures.size(); i++) {
            try {
                //能转换成日期格式才进行日期大小判断
                dates.add(df.parse(entryIndexCaptures.get(i).getFiledate()));
            } catch (Exception e) {

            }
        }
        if (dates.size() == 0) {
            return date;
        }
        Date max = dates.get(0);//默认第一个最大
        Date min = dates.get(0);//默认第一个最小
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

    public List<RebackMissPageCheck> getMissPageCheck(String[] ids) {
        List<Tb_entry_index_capture> index_captures = entryIndexCaptureRepository.findByEntryidIn(ids);
        List<RebackMissPageCheck> rebackMissPageChecks = new ArrayList<>();
        for(Tb_entry_index_capture index_capture : index_captures){
            RebackMissPageCheck missPageCheck = new RebackMissPageCheck();
            String eleid = index_capture.getEleid();
            if(eleid!=null&&index_capture.getPages()!=null&&!"".equals(index_capture.getPages())&&
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
        int pagetotal =0;
        int eletotal = 0;
        List<String[]> subAry = new InformService().subArray(ids, 1000);
        for (String[] ary : subAry) {
            List<Tb_entry_index_capture> index_captures = entryIndexCaptureRepository.findByEntryidIn(ary);
            for(Tb_entry_index_capture index_capture : index_captures){
                if(index_capture.getPages()!=null&&!"".equals(index_capture.getPages())){
                    pagetotal = pagetotal + Integer.parseInt(index_capture.getPages());
                }
                String eleid = index_capture.getEleid();
                if(eleid!=null&&!"".equals(eleid.trim())) {
                    eleid=eleid.replaceAll(" ","");//清除所有空格
                    eletotal = eletotal + Integer.parseInt(eleid);
                }
            }
            total = total+index_captures.size();
        }
        int[] number = new int[3];
        number[0] = total;
        number[1] = pagetotal;
        number[2] = eletotal;
        return number;
    }


    public Page<Tb_index_detail_capture> getMediaEntries(String nodeid, String status, String docid, String condition,
                                                         String operator, String content, int page, int limit, Sort sort,
                                                         String[] labels,String[] filingyear,String[] entryretention,String groupid,String parententryid) {
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        String sortStr="";//排序
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            sortStr = " order by " + order.getProperty() + " " + order.getDirection();
        }else{
            sortStr = " order by archivecode desc, descriptiondate desc ";
        }

        String nodeIdSql="";
        String shCondition="";//审核筛选
        if(docid!=null && !"".equals(docid)){//数据审核模块查看单据的条目详细信息
            shCondition=" and entryid in(select entryid from tb_transdoc_entry where docid ='"+ docid +"' ";
            nodeIdSql+="1=1";
            if(parententryid!=null){
                if("".equals(parententryid)){
                    shCondition+=" and ( parententryid='"+parententryid+"' or parententryid is null))";
                }else {
                    shCondition+=" and parententryid='"+parententryid+"')";
                }
            }else{
                shCondition+=")";
            }
        }else {//数据采集模块查看单据的条目详细信息
            shCondition=" and entryid not in(select entryid from tb_transdoc_entry where status='"+status+"')";
            nodeIdSql+="nodeid ='"+nodeid+"'";
        }
        String labelsql ="";
        if(labels!=null&&labels.length!=0){
            labelsql=" and entryid in(select entryid from tb_label_entry where labelid in (";
            for (int i = 0; i < labels.length; i++) {
                String label = labels[i];
                labelsql+=("'"+label+"'");
                if(i<labels.length-1){
                    labelsql+=",";
                }else{
                    labelsql+="))";
                }
            }
        }
        //过滤归档年度sql
        String filingyearsql ="";
        if(filingyear!=null&&filingyear.length!=0){
            filingyearsql=" and filingyear in(";
            for (int i = 0; i < filingyear.length; i++) {
                String label = filingyear[i];
                filingyearsql+=("'"+label+"'");
                if(i<filingyear.length-1){
                    filingyearsql+=",";
                }else{
                    filingyearsql+=")";
                }
            }
        }
        //过滤保管期限sql
        String entryretentionsql ="";
        if(entryretention!=null&&entryretention.length!=0){
            entryretentionsql=" and entryretention in(";
            for (int i = 0; i < entryretention.length; i++) {
                String label = entryretention[i];
                entryretentionsql+=("'"+label+"'");
                if(i<entryretention.length-1){
                    entryretentionsql+=",";
                }else{
                    entryretentionsql+=")";
                }
            }
        }
        String groupsql ="";
        if(groupid!=null){
            groupsql=" and docgroupid in('"+groupid+"')";
        }

        String searchCondition = "";//检索框
        if (content != null && !"".equals(content)) {// 输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
//        String nodeidStr="";
//        if(nodeid!=null){
//            nodeidStr = " and nodeid='" +nodeid+"'";
//        }
        String sql = "select sid.* from v_index_detail_capture sid where "+nodeIdSql+ searchCondition +shCondition+labelsql+filingyearsql+entryretentionsql+groupsql+sortStr;
        String countSql = "select count(*) from v_index_detail_capture sid where "+nodeIdSql+ searchCondition +shCondition+labelsql+filingyearsql+entryretentionsql+groupsql;
        Query query = entityManager.createNativeQuery(sql, Tb_index_detail_capture.class);
        query.setFirstResult((page - 1) * limit);
        query.setMaxResults(limit);
        List<Tb_index_detail_capture> resultList = query.getResultList();
        Query couuntQuery = entityManager.createNativeQuery(countSql);
        int count = Integer.parseInt(couuntQuery.getResultList().get(0) + "");
        return new PageImpl(resultList, pageRequest, count);
    }

    public List<MediaEntry> getMediaEntryCaptures(List<Tb_index_detail_capture> list){
        String[] entryidAry = GainField.getFieldValues(list, "entryid").length == 0 ? new String[]{""} : GainField.getFieldValues(list, "entryid");
        List<Tb_thumbnail> thumList = thumbnailRepository.findByEntryidIn(entryidAry);
        List<MediaEntry> eList = new ArrayList<>();
        for (Tb_index_detail_capture index : list) {
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