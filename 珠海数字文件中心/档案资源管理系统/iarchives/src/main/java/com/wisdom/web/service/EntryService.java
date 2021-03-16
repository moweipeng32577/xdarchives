package com.wisdom.web.service;

import com.wisdom.secondaryDataSource.entity.SxEntry;
import com.wisdom.secondaryDataSource.entity.Tb_entry_detail_sx;
import com.wisdom.secondaryDataSource.entity.Tb_entry_index_sx;
import com.wisdom.secondaryDataSource.repository.SecondaryEntryDetailRepository;
import com.wisdom.secondaryDataSource.repository.SecondaryEntryIndexRepository;
import com.wisdom.util.DateUtil;
import com.wisdom.util.GainField;
import com.wisdom.web.controller.FullSearchController;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceContext;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Created by Rong on 2017/11/13.
 */
@Service
@Transactional
public class EntryService {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    EntryDetailRepository entryDetailRepository;

    @Autowired
    ElectronicRepository electronicRepository;

    @Autowired
    ElectronicService electronicService;

    @Autowired
    BorrowMsgRepository borrowMsgRepository;

    @Autowired
    RecyclebinService recyclebinService;

    @Autowired
    EntryCaptureService entryCaptureService;

    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    ElectronicRecyclebinRepository electronicRecyclebinRepository;

    @Autowired
    ElectronicVersionRepository electronicVersionRepository;

    @Autowired
    ElectronicBrowseRepository electronicBrowseRepository;

    @Autowired
    ThumbnailRepository thumbnailRepository;

    @Autowired
    DataNodeExtRepository dataNodeExtRepository;

    @Autowired
    EntryIndexTempRepository entryIndexTempRepository;

    @Autowired
    FundsService fundsService;

    @Autowired
    CodesetRepository codesetRepository;

    @Autowired
    ClassifySearchService classifySearchService;

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    SecondaryEntryDetailRepository secondaryEntryDetailRepository;

    @Autowired
    SecondaryEntryIndexRepository secondaryEntryIndexRepository;

    @Autowired
    FullSearchController fullSearchController;

    @Autowired
    FullTextRepository fullTextRepository;


    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录

    public Entry getEntry(String entryid){
        Tb_entry_index index = entryIndexRepository.findByEntryid(entryid);
        Tb_entry_detail detail = entryDetailRepository.findByEntryid(entryid);

        Entry entry = new Entry();
        entry.setEntryIndex(index);
        entry.setEntryDetial(detail);
        return entry;
    }

    public SxEntry getSxEntry(String entryid){
        Tb_entry_index_sx index = secondaryEntryIndexRepository.findAllByEntryid(entryid);
        Tb_entry_detail_sx detail = secondaryEntryDetailRepository.findByEntryid(entryid);

        SxEntry entry = new SxEntry();
        entry.setEntryIndex(index);
        entry.setEntryDetial(detail);
        return entry;
    }
	
    public List<Entry> getEntrys(List<Tb_entry_index> list){
        List<Entry> eList=new ArrayList<Entry>();
        for(Tb_entry_index index:list){
            Entry entry = new Entry();
            Tb_entry_detail detail = entryDetailRepository.findByEntryid(index.getEntryid());
            entry.setEntryIndex(index);
            entry.setEntryDetial(detail);
            eList.add(entry);
        }
        return eList;
    }

    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
    
    public Entry saveEntry(Entry entry, String type, boolean isCompilationManageSystem,Boolean isMedia) {
        Tb_entry_index index = entry.getEntryIndex();
        String currentEntryid = index.getEntryid();
        String eleids = index.getEleid();
        Tb_entry_index entryInfo = entryIndexRepository.findByEntryid(entry.getEntryid());
        if (type.equals("modify") && entryInfo != null) {
            Integer count = Integer.valueOf(borrowMsgRepository.findCountByEntryid(entry.getEntryid()));// 查找到当前条目未归还的份数
            if (entry.getFscount() != null && !entry.getFscount().equals("")) {
                if (isNumeric(entry.getFscount())) {// 份数有值时且为正整数,库存份数是否有值都为(库存份数=份数-【实体查档中未归还的当前记录的条数】)
                    Integer fscount = Integer.valueOf(entry.getFscount());
                    Integer value = fscount - count;
                    // 如果库存份数的值大于0,保存结果值,如果小于等于0,就保存0(不存在负数)
                    if (value > 0) {
                        index.setKccount(String.valueOf(value));
                    } else {
                        index.setKccount("0");
                    }
                }
            } else {
                index.setKccount(index.getFscount());
            }
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
//            if (index.getEntryretention() == null) {// 保管期限
//                index.setEntryretention(entryInfo.getEntryretention());
//            }
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

            if(!"永久".equals(entryInfo.getEntryretention()) && (entryInfo.getDuetime()!=null || "".equals(entryInfo.getDuetime()))){
                index.setEntryretention(entryInfo.getEntryretention());
            }

        } else {
            index.setTitle(index.getTitle().trim());
            if(isCompilationManageSystem){
                index.setFlagopen("编研开放");//当是编码管理系统保存目录时把开放状态默认设为编研开放
            }else {
                index.setFlagopen(null);//新增的条目开放状态都是空的
            }
        }
        //著录份数增加-库存数相应增加
        if (type.equals("add") ){
            Integer count = Integer.valueOf(borrowMsgRepository.findCountByEntryid(entry.getEntryid()));// 查找到当前条目未归还的份数
            if (entry.getFscount() != null && !entry.getFscount().equals("")) {
                if (isNumeric(entry.getFscount())) {// 份数有值时且为正整数,库存份数是否有值都为(库存份数=份数-【实体查档中未归还的当前记录的条数】)
                    Integer fscount = Integer.valueOf(entry.getFscount());
                    Integer value = fscount - count;
                    // 如果库存份数的值大于0,保存结果值,如果小于等于0,就保存0(不存在负数)
                    if (value > 0) {
                        index.setKccount(String.valueOf(value));
                    } else {
                        index.setKccount("0");
                    }
                }
            } else {
                index.setKccount(index.getFscount());
            }

        }

        Tb_entry_detail detail1 = null;
        if ("".equals(eleids.trim())) {
            index.setEleid(null);
            index = entryIndexRepository.save(index);
        } else {
            String[] eleidArr = eleids.split(",");
            List<String> savedEntryid = electronicRepository.findEntryidByEleidIn(eleidArr);
            if (currentEntryid != null&&!"".equals(currentEntryid.trim())) {// 修改操作，当前需保存的条目的entryid不为空
                index.setEleid(String.valueOf(eleidArr.length));
            } else {// 增加(著录)操作，当前需保存的条目的条目id为空
                /*if (index.getFscount() != null && !"".equals(index.getFscount())) {
                    if (Integer.valueOf(index.getFscount()) >= 0) {// 份数
                        index.setKccount(index.getFscount());// 如果是新增的话,份数需要同步到库存份数中
                    }
                }*/
                if (savedEntryid.get(0) == null) {// 连续著录第一次操作，当前电子文件未曾保存，无对应entryid
                    index.setEleid(String.valueOf(eleidArr.length));
                    index = entryIndexRepository.save(index);
                    electronicRepository.updateEntryid(index.getEntryid(), eleidArr);
                    for(String eleid : eleidArr){
                        //转存原来的电子文件到新的文件夹
//                        electronicService.renameToIndex("management",index.getEntryid(),eleid);
                        detail1 = electronicService.renameToIndex("management",index.getEntryid(),eleid, entry.getNodeid());
                    }
                } else {// 连续著录第二次及后续操作，当前电子文件曾保存过，有对应entryid
                    index.setEleid(null);
                }
            }
        }
        String inde_node = entry.getNodeid();
        index.setKccount(index.getFscount());
        index = entryIndexRepository.save(index);
        index.setNodeid(inde_node);
        //保存到期时间
        if((index.getFiledate()!=null&&index.getFiledate()!="") && (index.getEntryretention()!=null&&index.getEntryretention()!="")){
            if(index.getDuetime() == null || "".equals(index.getDuetime())){
                switch (index.getEntryretention()){
                    case "长期":case "30年" :{
                        String duetime =  DateUtil.getAddYearDate(index.getFiledate(),30);
                        index.setDuetime(duetime);
                    }
                    break;
                    case "短期":case "10年" :{
                        String duetime =  DateUtil.getAddYearDate(index.getFiledate(),10);
                        index.setDuetime(duetime);
                    }
                    break;
                    default:{
                        index.setDuetime("");
                    }
                    break;
                }
            }

        }
        Tb_entry_detail detail = entry.getEntryDetail();
        if(detail1 != null) {
            BeanUtils.copyProperties(detail1, detail);
        }
        detail.setEntryid(index.getEntryid());
        Tb_data_node_mdaflag dataNode  = dataNodeExtRepository.findNodeid(entry.getNodeid());
        if(!(dataNode != null && "modify".equals(type)) ) {
            detail = entryDetailRepository.save(detail);
        }

        Entry result = new Entry();
        result.setEntryIndex(index);
        result.setEntryDetial(detail);
        return result;
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
        //复制数据到临时表
        String entryidStr ="";
        if(entryids!=null&&!"".equals(entryids.trim())){//非选择所有页
            entryidStr = " and sid.entryid in('" + String.join("','", entryids.split(",")) + "') ";
        }
        String searchCondition = "";
        if (content != null && !"".equals(content.trim())&&!"0".equals(selectAll)) {//输入框检索   选择所有页
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        String nodeidStr="";
        if(nodeid != null && !"".equals(nodeid.trim())&&!"0".equals(selectAll)){//选择所有页
            nodeidStr=" and sid.nodeid='"+nodeid+"' ";
            String tempStr=" and sid.entryid not in (select entryid from tb_entry_index_temp where uniquetag='"+uniquetag+"') ";//避免重复插入数据
            nodeidStr=nodeidStr+tempStr;
        }
        if("".equals(entryidStr+searchCondition+nodeidStr)){//没有被选择的条目时直接返回
            return tempList.size();
        }
        String sql = "select sid.* from v_index_detail sid where 1=1 " + entryidStr+searchCondition+nodeidStr;
        Query query = entityManager.createNativeQuery(sql, Tb_index_detail.class);
        List<Tb_index_detail> resultList = query.getResultList();

        for (Tb_index_detail entry_index: resultList) {
            Tb_entry_index_temp entry_index_temp = new Tb_entry_index_temp();
            BeanUtils.copyProperties(entry_index, entry_index_temp);
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
        String uniquetag=BatchModifyService.getUniquetagByType("glgd");
        //添加主表数据
        String count = entryIndexTempRepository.findByUniquetag(uniquetag);//预归档最大的归档顺序号
        List<Tb_entry_index> result = entryIndexRepository.findByEntryidIn(entryids.split(","));
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
        for (Tb_entry_index entry_index : result) {
            String nodeid = entry_index.getNodeid();
            String defaultFunds = templateRepository.findFdefaultByFieldcodeAndNodeid("funds", nodeid);
            String organid = entryIndexService.getOrganidByNodeidLoop(nodeid);// 机构id
            if (defaultFunds != null && !"".equals(defaultFunds)) {
                entry_index.setFunds(defaultFunds);
            } else {
                String funds = fundsService.getOrganFunds(organid);
                if (funds != null) {// 如果是单位机构的话,直接填充获取到的全宗号
                    entry_index.setFunds(funds);
                } else {// 如果是部门机构的话,需要获取到所属单位的全宗号
                    String unitOrganid = entryIndexService.getOrganInfo(organid);
                    String unitFunds = fundsService.getOrganFunds(unitOrganid);
                    entry_index.setFunds(unitFunds == null ? "" : unitFunds);
                }
            }
            Tb_entry_index_temp entry_index_temp = new Tb_entry_index_temp();
            BeanUtils.copyProperties(entry_index, entry_index_temp,new String[]{"sortsequence"});
            //添加副表数据
            Tb_entry_detail detail = entryDetailRepository.findByEntryid(entry_index.getEntryid());
            if (detail != null) {
                BeanUtils.copyProperties(detail, entry_index_temp);
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

    public ExtMsg entryTempEdit(Entry entry,String dataNodeid){
        //重新设置档号
        Tb_entry_index index=new  Tb_entry_index();
        BeanUtils.copyProperties(entry,index,new String[]{"entryid","nodeid"});
        index.setNodeid(dataNodeid);
        int state=2;
        // 把表单数据保存到临时表
        String entryid=entry.getEntryid();
        List<Tb_entry_index_temp> temps=entryIndexTempRepository.findByEntryidIn(new String[]{entryid});
        Tb_entry_index_temp temp=temps.get(0);
        List<Tb_data_template> editTemplates=templateRepository.findEditFormByNode(dataNodeid);
        if(editTemplates.size()>0){//只修改了编辑字段，只修改编辑字段到临时表
            List<Tb_codeset> codesets=codesetRepository.findEditCodeset(dataNodeid);//档编辑字段含档号字段
            if(codesets.size()>0){//预归档编辑字段含档号字段，获取档号设置字段和预归档编辑字段和archivecode字段的并集
                state=entryCaptureService.getArchvieCode(index,dataNodeid, temp.getArchivecode(),"数据管理");
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
            state=entryCaptureService.getArchvieCode(index,dataNodeid, temp.getArchivecode(),"数据管理");
            if(state==-1){//档号重复
                return  new ExtMsg(true, "0", null);
            }
            //BeanUtils.copyProperties(entry,temp.get(0));//nodeid会清除，非mysql会entryid冲突,
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

    public Integer delEntry(String[] entryidData){
        //删除关联的详细表
        entryDetailRepository.deleteByEntryidIn(entryidData);
        //删除条目关联的电子文件
        delElectronic(entryidData);
        //删除回收站关联的电子文件记录和电子文件
        deleteRecyclebin(entryidData);
        //删除电子文件历史版本
        electronicVersionRepository.deleteByEntryidIn(entryidData);
        //删除条目关联的电子文件记录
        electronicRepository.deleteByEntryidIn(entryidData);
        //删除条目
        return entryIndexRepository.deleteByEntryidIn(entryidData);
    }

    public Integer delEntryOnly(String[] entryidData){
        //删除条目
        return entryIndexRepository.deleteByEntryids(entryidData);
    }

    public void delEntryRef(String[] entryidData){
        //删除关联的详细表
        entryDetailRepository.deleteByEntryidIn(entryidData);
        //删除条目关联的电子文件
        delElectronic(entryidData);
        //删除回收站关联的电子文件记录和电子文件
        deleteRecyclebin(entryidData);
        //删除电子文件历史版本
        electronicVersionRepository.deleteByEntryidIn(entryidData);
        //删除条目关联的电子文件记录
        electronicRepository.deleteByEntryidIn(entryidData);
        //删除关联的全文表记录
        fullTextRepository.deleteByEntryidIn(entryidData);
        //删除关联的solr全文检索记录
        fullSearchController.delSolrRecord(entryidData,"entryid");
    }

    /**
     * 删除电子文件
     * @param entryidData
     */
    public void delElectronic(String[] entryidData){
        //富滇由于之前旧的数据是年月日文件夹下，有可能存在多条目关联一个电子文件
        //需要保护以前的旧数据，需要一条条判断改文件是否被多个条目关联，如果被多个条目关联，不能删除
//        for(String entryid:entryidData){
            List<Tb_electronic> electronics = electronicRepository.findByEntryidIn(entryidData);
            for(Tb_electronic electronic : electronics){
                //当只有一条电子文件记录才允许删除
                if(electronicRepository.findByFilepathAndFilename(electronic.getFilepath(),electronic
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
                    if(folder.listFiles()!=null && folder.listFiles().length==0){
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

    /**
     * 上传或删除电子文件时间，更新条目中的电子文件数量
     * @param entryid
     * @param operate  更新操作，增加：add，删除：remove
     * @param count    更新数量
     */
    public void updateEleNum(String entryid, String operate, int count) {
        Tb_entry_index index = entryIndexRepository.findByEntryid(entryid);
        int num = count;
        switch (operate){
            case "add" :
                if(index.getEleid() == null){
                    index.setEleid(String.valueOf(num));
                }else{
                    num = Integer.parseInt(index.getEleid().trim());
                    index.setEleid(String.valueOf(num + count));
                }
                entryIndexRepository.save(index);
                break;
            case "remove" :
                if(index.getEleid() == null){
                    return;
                }
                num = Integer.parseInt(index.getEleid().trim());
                if(num == count){
                    index.setEleid(null);
                }else{
                    index.setEleid(String.valueOf(num - count));
                }
                entryIndexRepository.save(index);
                break;
            default:
                break;
        }
    }


    //删除电子文件及相关信息
    public Integer delElectronicByEntryid(String[] entryidData) {
//        //删除条目关联的电子文件
//        delElectronicCapture(entryidData);
        //删除回收站关联的电子文件记录和电子文件
        deleteRecyclebin(entryidData);
        //删除关联的压缩文件和缩略图电子文件
        deleteCompressoionThumbFile(entryidData);
        electronicBrowseRepository.deleteByEntryidIn(entryidData);//删除压缩文件记录
        thumbnailRepository.deleteByEntryidIn(entryidData);//删除缩略图
        //删除条目关联的电子文件记录
        return electronicRepository.deleteByEntryidIn(entryidData);
    }
    public void deleteCompressoionThumbFile(String[] entryids) {
        List<Tb_electronic_browse> ebList = electronicBrowseRepository.findByEntryidIn(entryids);
        for (Tb_electronic_browse eb : ebList) {
            File deleteFile = new File(rootpath + eb.getFilepath() + "/" + eb.getFilename());
            deleteFile.delete();
            deleteFile.getParentFile().delete();//自带【是否有子文件判断】
            deleteFile.getParentFile().getParentFile().delete();
        }

        List<Tb_thumbnail> tbList = thumbnailRepository.findByEntryidIn(entryids);
        for (Tb_thumbnail tb : tbList) {
            if(!tb.getUrl().contains("/static/img/defaultMedia")){
                File deleteFile = new File(rootpath + tb.getUrl());
                deleteFile.delete();
                deleteFile.getParentFile().delete();//自带【是否有子文件判断】
                deleteFile.getParentFile().getParentFile().delete();
            }
        }
    }

    public List<MediaEntry> getMediaEntry(List<Tb_index_detail> list){
        String[] entryidAry = GainField.getFieldValues(list, "entryid").length == 0 ? new String[]{""} : GainField.getFieldValues(list, "entryid");
        List<Tb_thumbnail> thumList = thumbnailRepository.findByEntryidIn(entryidAry);
        List<MediaEntry> eList = new ArrayList<>();
        for (Tb_index_detail index : list) {
            Tb_thumbnail thumbnail = null;
            for (Tb_thumbnail thum : thumList) {
                if (thum.getEntryid().trim().equals(index.getEntryid().trim())) {
                    thumbnail = thum;
                    break;
                }
            }
            MediaEntry entry = new MediaEntry();
            BeanUtils.copyProperties(index, entry);
            if(index.getF12()!=null){//判断帧率是否为视频
                entry.setBackground(thumbnail == null ? "" : thumbnail.getUrl());
            }else {
                entry.setBackground("");
            }
            eList.add(entry);
        }
        return eList;
    }
}
