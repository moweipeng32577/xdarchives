package com.wisdom.web.service;

import com.wisdom.util.*;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 数字化质检模块服务层
 */
@Service
@Transactional
public class DigitalInspectionService {

    @Autowired
    SzhBatchBillRepository szhBatchBillRepository;

    @Autowired
    SzhBatchEntryRepository szhBatchEntryRepository;

    @Autowired
    SzhBatchMediaRepository szhBatchMediaRepository;

    @Autowired
    SzhBatchErrRepository szhBatchErrRepository;

    @Autowired
    SzhAssemblyRepository assemblyRepository;

    @Autowired
    EntryIndexCaptureRepository entryIndexCaptureRepository;

    @Autowired
    EntryDetailCaptureRepository entryDetailCaptureRepository;

    @Autowired
    ElectronicCaptureRepository electronicCaptureRepository;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    SzhMediaMetadataRepository szhMediaMetadataRepository;

    @Autowired
    SzhCheckUserRepository szhCheckUserRepository;

    @Autowired
    SzhBatchDealRepository szhBatchDealRepository;

    @Autowired
    SzhCheckEntryRepository szhCheckEntryRepository;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    EntryDetailRepository entryDetailRepository;

    @Autowired
    ElectronicRepository electronicRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ExportExcelService exportExcelService;

    @Autowired
    NodesettingService nodesettingService;

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录

    /**
     * 分页获取质检批次单据信息
     * @param type 类型(质检OR验收)
     * @param status 单据状态
     * @param page 页码
     * @param limit 每页数
     * @param condition 查询字段
     * @param operator  条件
     * @param content 查询字段值
     * @return
     */
    public Page<Szh_batch_bill> getBatchBillBySearch(String type, String status, int page, int limit, String sort,String condition, String operator, String content){
        Sort sortobj=WebSort.getSortByJson(sort);
        Specifications sp = null;
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (status!=null) {
            sp = Specifications.where(new SpecificationUtil("status","equal",status)).and(new SpecificationUtil("inspector","equal",userDetails.getRealname()));
        }

        sp = ClassifySearchService.addSearchbarCondition(sp, "type", "equal", type);

        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        return szhBatchBillRepository.findAll(sp, new PageRequest(page - 1, limit,sortobj));
    }

    public Page<Szh_batch_bill> getBatchBillBySearchCheckUser(String type, String status, int page, int limit, String sort,String condition, String operator, String content){
        Sort sortobj=WebSort.getSortByJson(sort);
        Specifications sp = null;
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Szh_check_user>  check_userList = szhCheckUserRepository.findByUserid(userDetails.getUserid());
        List<String> batchids = new ArrayList<>();
        for(Szh_check_user check_user :check_userList){
            List<Szh_batch_deal>  batch_deals = szhBatchDealRepository.findByCheckgroupid(check_user.getCheckgroupid());
            for (Szh_batch_deal batch_deal : batch_deals){
                batchids.add(batch_deal.getBatchid());
            }
        }
        Specification<Szh_batch_bill> CheckUserCondition = new Specification<Szh_batch_bill>() {
            @Override
            public Predicate toPredicate(Root<Szh_batch_bill> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                CriteriaBuilder.In in = criteriaBuilder.in(root.get("id"));
                for (String id : batchids) {
                    in.value(id);
                }
                return criteriaBuilder.or(in);
            }
        };
        if (status!=null) {
            sp = Specifications.where(new SpecificationUtil("status","equal",status)).and(CheckUserCondition);
        }

        sp = ClassifySearchService.addSearchbarCondition(sp, "type", "equal", type);

        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        return szhBatchBillRepository.findAll(sp, new PageRequest(page - 1, limit,sortobj));
    }

    /**
     * 分页获取质检批次条目信息
     * @param batchcode 批次号
     * @param page 页码
     * @param limit 每页数
     * @param condition 查询字段
     * @param operator  条件
     * @param content 查询字段值
     * @return
     */
    public Page<Szh_batch_entry> getBatchEntryBySearch(String batchcode, String isCheck, int page, int limit, String condition, String operator, String content){
        Specifications sp = null;
        sp = Specifications.where(new SpecificationUtil("batchcode","equal",batchcode));
        sp = ClassifySearchService.addSearchbarCondition(sp, "ischeck","equal", isCheck);
        if (content != null&&!"".equals(content)) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        return szhBatchEntryRepository.findAll(sp, new PageRequest(page - 1, limit));
    }

    public Page<Szh_batch_entry> getWcBatchEntryBySearch(String batchcode, String status, int page, int limit, String condition, String operator, String content){
        Specifications sp = null;
        sp = ClassifySearchService.addSearchbarCondition(sp, "batchcode", "equal", batchcode);
        sp = ClassifySearchService.addSearchbarCondition(sp, "status", "equal", status);
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        return szhBatchEntryRepository.findAll(sp, new PageRequest(page - 1, limit));
    }

    public Page<Szh_batch_entry> getBatchEntryBySearchCheckUser(String notMe, String batchcode, String isCheck, int page, int limit, String condition, String operator, String content){
        Specifications sp = null;
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        List<Szh_check_user>  check_userList = szhCheckUserRepository.findByUserid(userDetails.getUserid());
//        List<String> batchentryids = new ArrayList<>();
//        for(Szh_check_user check_user :check_userList){
//            List<Szh_check_entry>  check_entrys = szhCheckEntryRepository.findByCheckuserid(check_user.getCheckuserid());
//            for (Szh_check_entry check_entry : check_entrys){
//                batchentryids.add(check_entry.getBatchentryid());
//            }
//        }
//        Specification<Szh_batch_entry> CheckUserCondition = new Specification<Szh_batch_entry>() {
//            @Override
//            public Predicate toPredicate(Root<Szh_batch_entry> root, CriteriaQuery<?> criteriaQuery,
//                                         CriteriaBuilder criteriaBuilder) {
//                CriteriaBuilder.In in = criteriaBuilder.in(root.get("id"));
//                for (String id : batchentryids) {
//                    in.value(id);
//                }
//                return criteriaBuilder.or(in);
//            }
//        };
        String notMeOperator = "equal";
        sp = Specifications.where(new SpecificationUtil("batchcode","equal",batchcode));
        if("是".equals(notMe)){
            notMeOperator = "notEqual";
        }
        sp = Specifications.where(new SpecificationUtil("batchcode","equal",batchcode));
        sp = ClassifySearchService.addSearchbarCondition(sp,"checker",notMeOperator,userDetails.getLoginname());
        sp = ClassifySearchService.addSearchbarCondition(sp,"ischeck","equal",isCheck);
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        return szhBatchEntryRepository.findAll(sp, new PageRequest(page - 1, limit));
    }
    /**
     * @param page 第几页
     * @param limit 一页获取多少行
     * @param condition 字段
     * @param operator 操作符
     * @param content 查询条件内容
     * @return
     */
    public Page<Tb_entry_index_capture> findByCaptureSearch(String type, String archivesType, int page, int limit, String condition, String operator, String content, Sort sort) {
        Specification<Tb_entry_index_capture> simpleSearchCondition = getSimpleSearchCondition(condition, operator, content);
        Sort sorts = new Sort(new Sort.Order(Sort.Direction.DESC, "archivecode"),
                new Sort.Order(Sort.Direction.DESC, "descriptiondate"));
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? sorts : sort);
//        List<String> nodeids = dataNodeRepository.findAjNodeId();//获取案卷数据节点id(用于根据档案类型过滤数据)
//        Specification<Tb_entry_index_capture> nodeidCondition = new Specification<Tb_entry_index_capture>() {
//            @Override
//            public Predicate toPredicate(Root<Tb_entry_index_capture> root, CriteriaQuery<?> criteriaQuery,
//                                         CriteriaBuilder criteriaBuilder) {
//                if("案卷".equals(archivesType)){
//                    CriteriaBuilder.In in = criteriaBuilder.in(root.get("nodeid"));
//                    for (String nodeid : nodeids) {
//                        in.value(nodeid);
//                    }
//                    return criteriaBuilder.or(in);
//                }else{
//                    Predicate[] predicates = new Predicate[nodeids.size()];
//                    for(int i=0;i<nodeids.size();i++){
//                        predicates[i] = criteriaBuilder.notEqual(root.get("nodeid"), nodeids.get(i));
//                    }
//                    return criteriaBuilder.and(predicates);
//                }
//            }
//        };

        if("验收".equals(type)){
            List<String> captureIds = szhBatchEntryRepository.findStatusAndType("通过","质检");
            Specification<Tb_entry_index_capture> passTypeCondition = new Specification<Tb_entry_index_capture>() {
                @Override
                public Predicate toPredicate(Root<Tb_entry_index_capture> root, CriteriaQuery<?> criteriaQuery,
                                             CriteriaBuilder criteriaBuilder) {

                    CriteriaBuilder.In in = criteriaBuilder.in(root.get("entryid"));
                    for (String captureId : captureIds) {
                        in.value(captureId);
                    }
                    return criteriaBuilder.or(in);
                }
            };
            simpleSearchCondition = Specifications.where(passTypeCondition);
        }

        return convertNodefullname(entryIndexCaptureRepository.findAll(Specifications.where(simpleSearchCondition),pageRequest),pageRequest);
    }

    public boolean batchAddFormSubmit(Szh_batch_bill bill, String type){
            String[] entryIds = bill.getId().split(",");//获取导入条目id
            List<Tb_entry_index_capture> captures = entryIndexCaptureRepository.findByEntryidIn(entryIds);//采集条目信息
            List<Tb_electronic_capture> electronic_captures = electronicCaptureRepository.findByEntryidIn(entryIds);//采集原文信息
            List<Szh_batch_entry> entries = new ArrayList<>();//批次条目信息集合
            List<Szh_batch_media> medias = new ArrayList<>();//批次条目信息集合
            List<Szh_media_metadata> metadatas = new ArrayList<>();//元数据条目信息集合
            Map<String,String> dhIdMap = new HashMap<>();//ID档案号对应Map(初始化元数据用)
            int pagenum = 0;//页数
            for(Tb_entry_index_capture capture:captures){//生成批次条目信息
                dhIdMap.put(capture.getEntryid(),capture.getArchivecode());
                int pagenumTemp = capture.getPages()!=null&&!"".equals(capture.getPages())?Integer.parseInt(capture.getPages()):0;
                entries.add(new Szh_batch_entry(
                        bill.getBatchcode(),
                        capture.getFilecode(),
                        capture.getArchivecode(),
                        pagenumTemp,
                        capture.getEntryid(),
                        capture.getNodeid(),
                        "否",
                        "未检查",
                        type
                ));
                int pages = capture.getEleid()!=null&&!"".equals(capture.getEleid())?Integer.parseInt(capture.getEleid().trim()):0;
                pagenum += pages;
            }

            for(Tb_electronic_capture capture:electronic_captures){
                String id = UUID.randomUUID().toString().replace("-", "");
                medias.add(new Szh_batch_media(
                        id,
                        capture.getFilename(),
                        bill.getBatchcode(),
                        capture.getEntryid(),
                        capture.getEleid(),
                        capture.getFilepath(),
                        capture.getFiletype(),
                        "未检查"
                ));

                metadatas.add(new Szh_media_metadata(
                        bill.getBatchcode(),
                        id,
                        dhIdMap.get(capture.getEntryid()),
                        capture.getFilename(),
                        rootpath+"/electronics/storages/" +capture.getFilepath(),
                        Integer.parseInt(capture.getFilesize())
                ));
            }
            bill.setCopies(captures.size());
            bill.setPagenum(pagenum);
            bill.setType(type);
            if("*".equals(bill.getInspector())){//判断是否连续导入
                szhBatchEntryRepository.deleteByBatchcodeAndCaptureentryidIn(bill.getBatchcode(),entryIds);
            }else{
                bill.setStatus("未抽检");//设置状态
                szhBatchBillRepository.save(bill);
            }
            szhBatchEntryRepository.save(entries);
            szhBatchMediaRepository.save(medias);
            szhMediaMetadataRepository.save(metadatas);
            return true;
    }

    public boolean batchDel(String[] batchcodes) {
        try{
            Integer billCount = szhBatchBillRepository.deleteByBatchcodeIn(batchcodes);
            szhBatchEntryRepository.deleteByBatchcodeIn(batchcodes);
            szhBatchMediaRepository.deleteByBatchcodeIn(batchcodes);
            szhBatchErrRepository.deleteByBatchcodeIn(batchcodes);
            if(billCount>0){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean cancelSampling(String[] batchcodes) {
        try{
            List<Szh_batch_bill> bills = szhBatchBillRepository.findByBatchcodeIn(batchcodes);
            List<Szh_batch_entry> entries = szhBatchEntryRepository.findByBatchcodeIn(batchcodes);
            for(Szh_batch_bill bill:bills){
                bill.setStatus("未抽检");
                bill.setCheckcount(null);
            }
            for(Szh_batch_entry entry:entries){
                entry.setIscheck("否");
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean samplingSubmit(String[] batchcodes,String checkcount,String samplingtype,String checkgroupid) {
        try{
            for(String batchcode:batchcodes){
                Szh_batch_bill bill = szhBatchBillRepository.findByBatchcode(batchcode);
                List<Szh_batch_entry> entries = szhBatchEntryRepository.findByBatchcode(batchcode);
                List<Szh_check_user>  check_userList = szhCheckUserRepository.findByCheckgroupid(checkgroupid);//检查组
                String[] userArryId = new String[check_userList.size()];
                for(int i=0;i<check_userList.size();i++){
                    userArryId[i] = check_userList.get(i).getUserid();//获取用户id数组
                }
                List<Tb_user> users = userRepository.findByUseridIn(userArryId);
                Map<String,String> userIdMap = new HashMap<>();//id用户名对应map
                for(Tb_user user:users){
                    userIdMap.put(user.getUserid(),user.getLoginname());
                }
                Szh_batch_deal batch_deal = new Szh_batch_deal();
                batch_deal.setBatchid(bill.getId());
                batch_deal.setCheckgroupid(checkgroupid);
                batch_deal.setState("抽检中");
                batch_deal = szhBatchDealRepository.save(batch_deal);
                double temp = Double.parseDouble(checkcount)/100*entries.size();
                Integer num = temp>1?(int)Math.floor(temp):1;//获取抽检数量
                int[] randomIndex = randomNumber(0,entries.size(),num);//获取抽检随机数
                double dlecount = randomIndex.length/check_userList.size();
                Integer count = dlecount>1?(int)Math.floor(dlecount):1;
                int number = 0;
                List<Szh_check_entry> check_entries = new ArrayList<>();
                for(int i=0;i<num;i++){//设置抽检条目
                        if(i!=0&&i%count==0){
                            if(number<check_userList.size()-1){
                                number++;
                            }
                        }
                    entries.get(randomIndex[i]).setIscheck("是");//设置是否参与抽检
                    entries.get(randomIndex[i]).setChecker(userIdMap.get(check_userList.get(number).getUserid()));//设置抽检人
                    Szh_check_entry check_entry = new Szh_check_entry();
                    check_entry.setBatchentryid(entries.get(randomIndex[i]).getId());
                    check_entry.setBatchid(bill.getId());
                    check_entry.setBatchdealid(batch_deal.getId());
                    check_entry.setCheckuserid(check_userList.get(number).getCheckuserid());
                    check_entry.setState("未检查");
                    check_entries.add(check_entry);
                }
                szhCheckEntryRepository.save(check_entries);
                bill.setStatus("正在抽检");//设置批次单据状态
                bill.setSamplingtype(samplingtype);//设置抽检类型
                bill.setCheckcount("抽查"+checkcount+"%,共"+num+"份");//设置抽检比例描述
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public Szh_batch_bill getBill(String batchcode){
        Szh_batch_bill bill = null;
        try{
            bill = szhBatchBillRepository.findByBatchcode(batchcode);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bill;
    }

    public EntryBase getFormEntry(String entryid){
        EntryBase entryBase = new EntryBase();
        try{
            Tb_entry_index_capture capture = entryIndexCaptureRepository.findByEntryid(entryid);
            Tb_entry_detail_capture detail_capture = entryDetailCaptureRepository.findByEntryid(entryid);
            BeanUtils.copyProperties(capture,entryBase);
            BeanUtils.copyProperties(detail_capture,entryBase);
        }catch (Exception e){
            e.printStackTrace();
        }
        return entryBase;
    }

    public List<Szh_batch_media> getEntryMedias(String batchcode, String entryid){
        Specifications sp = null;
        if (batchcode!=null) {
            sp = Specifications.where(new SpecificationUtil("batchcode","equal",batchcode));
        }
        if (entryid != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, "entryid","equal",entryid);
        }
        return szhBatchMediaRepository.findAll(sp);
    }

    public List<Szh_batch_err> getMediaErrors(String batchcode, String mediaid){
        Specifications sp = null;
        sp = Specifications.where(new SpecificationUtil("batchcode","equal",batchcode));
        sp = ClassifySearchService.addSearchbarCondition(sp, "mediaid","equal",mediaid);
        return szhBatchErrRepository.findAll(sp);
    }

    public boolean errSubmit(Szh_batch_err err){
        boolean state = false;
        try {
            Tb_electronic_capture capture = electronicCaptureRepository.findByEleid(err.getMediaid());
            err.setCaptureentryid(capture.getEntryid());//添加条目名(用于导出错误报告用)
            err.setFilename(capture.getFilename());//添加文件名
            err.setStatus("未处理");//添加状态
            szhBatchErrRepository.save(err);
            state = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return state;
    }

    public int delMediaErrs(String[] errids){
        int delCount = 0;
        try {
            delCount = szhBatchErrRepository.deleteByIdIn(errids);
        }catch (Exception e){
            e.printStackTrace();
        }
        return delCount;
    }

    public boolean errRepair(String[] errids){
        try{
            List<Szh_batch_err> errs = szhBatchErrRepository.findByIdIn(errids);
            for(Szh_batch_err err:errs){
                err.setStatus("已处理");
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean changeMediaStatus(String id){
        try{
            Szh_batch_media media = szhBatchMediaRepository.findOne(id);
            media.setStatus("已检");
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void exportErrReport(String batchcode, HttpServletResponse response){
        try {
            List<Szh_batch_err> errs = szhBatchErrRepository.findByBatchcode(batchcode);
            Map<String,List<Szh_batch_err>> fileNameMap = new HashMap<>();//存放文件名(用于条目与文件名对应用)
            List<String> captureidLists = new ArrayList<>();
            for(Szh_batch_err err:errs){
                captureidLists.add(err.getCaptureentryid());
                if(!fileNameMap.containsKey(err.getCaptureentryid())){
                    List<Szh_batch_err> tempList = new ArrayList<>();
                    tempList.add(err);
                    fileNameMap.put(err.getCaptureentryid(), tempList);
                }else{
                    fileNameMap.get(err.getCaptureentryid()).add(err);
                }
            }

            String[] captureids = captureidLists.toArray(new String[captureidLists.size()]);//获取条目id
            List<Tb_entry_index_capture> captures = entryIndexCaptureRepository.findByEntryidIn(captureids);
            List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
            int count = 1;
            for (Tb_entry_index_capture capture:captures) {
                List<Szh_batch_err> scErrs = fileNameMap.get(capture.getEntryid());
                if(scErrs!=null){
                    for(Szh_batch_err err:scErrs){
                        Map<String, Object> mapValue = new HashMap<>();
                        mapValue.put("id",count);
                        mapValue.put("funds", capture.getFunds());
                        mapValue.put("filecode", capture.getFilecode());
                        mapValue.put("recordcode", capture.getRecordcode());
                        mapValue.put("filename", err.getFilename());
                        mapValue.put("errtype",err.getErrtype() );
                        mapValue.put("depict",err.getDepict());
                        mapValue.put("status",err.getStatus());
                        listmap.add(mapValue);
                        count++;
                    }
                }
            }
            String exportName = batchcode+"批次的错误报告_"+new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            String names[] = {"序号", "全宗号", "案卷号", "顺序号/件号", "文件名", "错误类型", "描述","状态"};// 列名
            String keys[] = {"id", "funds", "filecode", "recordcode", "filename", "errtype", "depict","status"};// map中的key
            ExportUtil exportUtil = new ExportUtil(exportName, response, listmap, keys, names);
            exportUtil.exportExcel();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public String exitda(String id,String batchcode){
        String result = "1";//1:单条通过,2:全部通过,3:操作失败
        try{
            Szh_batch_entry entry = szhBatchEntryRepository.findOne(id);
            entry.setStatus("退回");
            boolean state = checkEntryStatus(batchcode);
            if(state){
                Szh_batch_bill bill = szhBatchBillRepository.findByBatchcode(batchcode);
                bill.setStatus("完成抽检");
                Szh_batch_deal batch_deal = szhBatchDealRepository.findByBatchidAndAndState(bill.getId(),"抽检中");
                batch_deal.setState("完成抽检");
                result = "2";
            }
        }catch (Exception e){
            e.printStackTrace();
            result = "3";
        }
        return result;
    }

    public String passEntry(String id,String batchcode){
        String result = "1";//1:单条通过,2:全部通过,3:操作失败
        try {
            Szh_batch_entry batchEntry = szhBatchEntryRepository.findOne(id);
            batchEntry.setStatus("通过");
            Szh_check_entry check_entry = szhCheckEntryRepository.findByBatchentryidAndAndState(id,"未检查");
            if(check_entry!=null){
                check_entry.setState("通过");
                boolean state = checkEntryStatus(batchcode);
                if(state){
                    Szh_batch_bill bill = szhBatchBillRepository.findByBatchcode(batchcode);
                    bill.setStatus("完成抽检");
                    Szh_batch_deal batch_deal = szhBatchDealRepository.findByBatchidAndAndState(bill.getId(),"抽检中");
                    batch_deal.setState("完成抽检");
                    result = "2";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            result = "3";
        }
        return result;
    }

    public boolean checkEntryStatus(String batchcode){
        boolean state = true;
        List<Szh_batch_entry> entrys = szhBatchEntryRepository.findByBatchcodeAndIscheck(batchcode,"是");
        for(Szh_batch_entry entry:entrys){//判断同批次中是否还有未检查条目(用于判断修改批次状态用)
            if("未检查".equals(entry.getStatus())){
                state = false;
                break;
            }
        }
        return state;
    }

    public boolean changeBatchEntry(String batchcode, String entryId){
        boolean state = false;
        try {
            Tb_entry_index_capture capture = entryIndexCaptureRepository.findByEntryid(entryId);
            if(capture!=null){
                Szh_batch_entry entry = szhBatchEntryRepository.findByBatchcodeAndCaptureentryid(batchcode,entryId);
                if(entry!=null){
                    entry.setFilecode(capture.getFilecode());//更新案卷号
                    entry.setArchivecode(capture.getArchivecode());//更新档号
                }
            }
            state = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return state;
    }

    public List<Szh_assembly> getAssemblys(){
        return assemblyRepository.findAll();
    }

    public Szh_media_metadata getMetadata(String mediaid){
        return szhMediaMetadataRepository.findByMediaid(mediaid);
    }

    public boolean metadataSubmit(Szh_media_metadata metadata){
        boolean state = false;
        try {
            szhMediaMetadataRepository.save(metadata);
            state = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return state;
    }

    public boolean acceptSubmit(String[] batchcodes){
        boolean state = false;
        try {
            List<Szh_batch_bill> batchBills = szhBatchBillRepository.findByBatchcodeIn(batchcodes);
            List<Szh_batch_entry> batchEntries = szhBatchEntryRepository.findByBatchcodeInAndStatus(batchcodes,"通过");
            for(Szh_batch_bill bill:batchBills){//设置页数
//                bill.setPagenum(Integer.parseInt(pages));
                bill.setStatus("完成验收");
            }
            String[] captureIdArrays = new String[batchEntries.size()];
            for(int i=0;i<batchEntries.size();i++){
                batchEntries.get(i).setStatus("已验收");
                captureIdArrays[i] = batchEntries.get(i).getCaptureentryid();//获取采集表id
            }

            List<Tb_entry_index_capture> entryCaptures = entryIndexCaptureRepository.findByEntryidIn(captureIdArrays);
            List<Tb_entry_detail_capture> detailCaptures = entryDetailCaptureRepository.findByEntryidIn(captureIdArrays);
            List<Tb_electronic_capture> electronicCaptures = electronicCaptureRepository.findByEntryidIn(captureIdArrays);
            List<Tb_entry_index> entryIndices = new ArrayList<>();
            List<Tb_entry_detail> entryDetails = new ArrayList<>();
            List<Tb_electronic> electronics = new ArrayList<>();
            Map<String,String> tempScMap = new HashMap<>();//临时文件与新增文件档号，id对应map(用于将转移电子文件与转移条目对应起来)
            Map<String,String> tempCeMap = new HashMap<>();//新增文件id，移动原文entryid对应map(用于将转移电子文件与转移条目对应起来)
            for(Tb_entry_index_capture capture:entryCaptures){//复制基础表数据
                Tb_entry_index index = new Tb_entry_index();
                BeanUtils.copyProperties(capture,index);
                entryIndices.add(index);
                tempScMap.put(capture.getArchivecode(),capture.getEntryid());
            }

            for(Tb_entry_detail_capture capture:detailCaptures){//复制扩展表数据
                Tb_entry_detail index = new Tb_entry_detail();
                BeanUtils.copyProperties(capture,index);
                entryDetails.add(index);
            }

            for(Tb_electronic_capture capture:electronicCaptures){
                Tb_electronic electronic = new Tb_electronic();
                BeanUtils.copyProperties(capture,electronic);
                electronics.add(electronic);
            }

            entryIndices = entryIndexRepository.save(entryIndices);//导入采集数据
            for(Tb_entry_index capture:entryIndices){//设置档号与移动条目间对应关系
                if(tempScMap.containsKey(capture.getArchivecode())){
                    tempCeMap.put(tempScMap.get(capture.getArchivecode()),capture.getEntryid());
                }
            }
            entryDetailRepository.save(entryDetails);
//            electronicRepository.moveeletronics(captureIdArrays);//执行出错
            electronics = electronicRepository.save(electronics);
            for(Tb_electronic capture:electronics){//将新条目id设置到移动后的原文entryid中
                if(tempCeMap.containsKey(capture.getEntryid())){
                    capture.setEntryid(tempCeMap.get(capture.getEntryid()));
                }
            }

            entryDetailCaptureRepository.deleteByEntryidIn(captureIdArrays);//删除采集数据
            electronicCaptureRepository.deleteByEntryidIn(captureIdArrays);
            entryIndexCaptureRepository.deleteByEntryidIn(captureIdArrays);
            state = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return state;
    }

    public String chooseFieldExport(String fileName, String zipPassword, String[] batchcodes) {
        String[] fieldcod = {"title","archivecode","funds","catalog","filecode","innerfile","entryretention","filingyear"};//字段号
        List<String> userFieldNameList = Arrays.asList("题名","档号","全宗号","目录号","案卷号","卷内顺序号","保管期限","归档年度");//字段名
        String[] fieldname = new String[userFieldNameList.size()];
        userFieldNameList.toArray(fieldname);
        String[] strcode = new String[fieldcod.length + 1];
        String[] strname = new String[fieldname.length + 1];
        strcode[strcode.length - 1] = "entryid";
        strname[strname.length - 1] = "条目ID";
        System.arraycopy(fieldcod, 0, strcode, 0, fieldcod.length);
        System.arraycopy(fieldname, 0, strname, 0, fieldname.length);
        String zippath = null;
        SXSSFWorkbook workbook =null;
        try {
            workbook = SXSSFCreatExcelAndCopyFile(batchcodes, fileName, strcode, strname);
            //创建字段模板
            String dir = ConfigValue.getPath("system.document.rootpath");
            String path = dir + "/OAFile" + "/Excel导出/临时目录/" + fileName;//
//            createTemp(nodeId, strcode, strname, path + "/", ids.length);
            CreateExcel.ExportExcel(workbook, fileName);

            // zip 完整路径
            zippath = dir + "/OAFile" + "/Excel导出/" + fileName + ".zip";
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, zipPassword);
            //wirteFile(response,zippath,usefileName,zipReturn,succ);//读取压缩包，发送页面
            ZipUtils.del(path);
            //new File(zippath).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            workbook.dispose();
        }
        return zippath;
    }

    /**
     * 生成excel 并复制原文
     * @param fileName 文件名
     * @param strcode  code集合
     * @param strname  字段名集合
     * @return 返回workbook对象
     * @throws Exception
     */
    public SXSSFWorkbook SXSSFCreatExcelAndCopyFile(String[] batchcodes,String fileName, String[] strcode, String[] strname) throws Exception {
        //1.创建excel文件---用在保存循环的数据--循环写入
        //2.创建工作簿  SXSSFWorkbook 支持最大行1048576
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        boolean b = Arrays.asList(strcode).contains("archivecode");
            List<Tb_entry_index> list = entryIndexRepository.findBatchcodesByAll(batchcodes);
            List<Tb_entry_detail> list2 = new ArrayList<>();
            List<Entry> entryList = createEntrtList(list, list2);
            //每900条写入1次
            sheet = CreateExcel.SXSSFWorkbookCreateExcle(sheet, entryList, strcode, strname);
            //拷贝电子文件
            List<Tb_electronic> electronics = electronicRepository.findBatchcodesByAll(batchcodes);
            //存放档号
            String archivecode = "";
            String dir = ConfigValue.getPath("system.document.rootpath");
            for (int j = 0; j < electronics.size(); j++) {//循环电子原文--记录对应原文，条目的档号
                for (int k = 0; k < entryList.size(); k++) {
                    String eleEntryId = electronics.get(j).getEntryid().trim();
                    String entryid = entryList.get(k).getEntryid().trim();
                    if (entryid.equals(eleEntryId)) {
                        archivecode = entryList.get(k).getArchivecode();
                    }
                }
                String filepath = electronics.get(j).getFilepath();//路径
                String filename = electronics.get(j).getFilename();//文件名
                String file = dir + filepath + "/" + filename;//完整路径
                String newUserFileName = "";
                if (!b || (archivecode == null) || "".equals(archivecode)) {//-当自选字段中没有档号时/档号为空/未归管理-使用条目id为原文目录名
                    newUserFileName = fileName + "/" + "document/" + electronics.get(j).getEntryid().trim();//拷贝路径
                } else {
                    String str = archivecode.replaceAll("\\·", "-");
                    newUserFileName = fileName + "/" + "document/" + str;//拷贝路径
                }
                try {
                    FileUtil.CopyFile(file, newUserFileName, filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //清空档号字符串
                archivecode = "";
            }
        return workbook;
    }

    public List<Entry> createEntrtList(List<Tb_entry_index> indexList, List<Tb_entry_detail> details) {
        List<Entry> entryList = new ArrayList<>();
        if (indexList.size() > 0 || details.size() > 0) {
            for (int i = 0; i < indexList.size(); i++) {
                Entry entry = new Entry();//生成entry对象 调用里面的方法
                entry.setEntryIndex(indexList.get(i));
                if(details.size() > 0&&details.size()==indexList.size()) {//防止数据库无副表对应条目,导致导出空白文件
                    entry.setEntryDetial(details.get(i));
                }else {
                    Tb_entry_detail detail=new Tb_entry_detail();
                    detail.setEntryid(indexList.get(i).getEntryid());
                    entry.setEntryDetial(detail);
                }
                entryList.add(entry);
            }
        }
        return entryList;
    }


    public Specification<Tb_entry_index_capture> getSimpleSearchCondition(String condition, String operator, String content) {
        Specification<Tb_entry_index_capture> simpleSearchCondition = new Specification<Tb_entry_index_capture>() {
            @Override
            public Predicate toPredicate(Root<Tb_entry_index_capture> root, CriteriaQuery<?> query,
                                         CriteriaBuilder criteriaBuilder) {
                if (condition != null) {
                    String[] conditions = condition.split(",");
                    String[] operators = operator.split(",");
                    String[] contents = content.split(",");// 存放前一次与本次查询内容
                    Predicate[] predicates = null;
                    List<String> entryid = null;
                     predicates = new Predicate[contents.length];
                    for (int i = 0; i < contents.length; i++) {
                        String[] contentsData = new String[] {};// 存放两次查询的每一次查询内容中，以空格分隔开的内容
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

                    return criteriaBuilder.and(predicates);
                }
                return null;
            }
        };
        return simpleSearchCondition;
    }

    /**
     * 功能：产生min-max中的n个不重复的随机数
     * min:产生随机数的开始位置
     * mab：产生随机数的最大位置
     * n: 所要产生多少个随机数
     *
     */
    public static int[] randomNumber(int min,int max,int n){

        //判断是否已经达到索要输出随机数的个数
        if(n>(max-min+1) || max <min){
            return null;
        }

        int[] result = new int[n]; //用于存放结果的数组

        int count = 0;
        while(count <n){
            int num = (int)(Math.random()*(max-min))+min;
            boolean flag = true;
            for(int j=0;j<count;j++){
                if(num == result[j]){
                    flag = false;
                    break;
                }
            }
            if(flag){
                result[count] = num;
                count++;
            }
        }
        return result;
    }

    /**
     * 生成数据节点全名，转换分页结果
     *
     * @param result
     * @param pageRequest
     * @return
     */
    public Page<Tb_entry_index_capture> convertNodefullname(Page<Tb_entry_index_capture> result, PageRequest pageRequest) {
        List<Tb_entry_index_capture> content = result.getContent();
        long totalElements = result.getTotalElements();
        List<Tb_entry_index_capture> returnResult = new ArrayList<>();

        Map<String, Object[]> parentmap = nodesettingService.findAllParentOfNode();
        for (Tb_entry_index_capture entryIndex : content) {
            Tb_entry_index_capture entry_index = new Tb_entry_index_capture();
            BeanUtils.copyProperties(entryIndex, entry_index);
            String nodeid = entry_index.getNodeid().trim();
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
            entry_index.setNodefullname(nodefullname.toString());

            returnResult.add(entry_index);
        }
        return new PageImpl(returnResult, pageRequest, totalElements);
    }
}
