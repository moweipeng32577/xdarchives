package com.wisdom.web.service;

import com.monitorjbl.xlsx.StreamingReader;
import com.wisdom.util.*;
import com.wisdom.web.controller.ManagementController;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.xdtech.component.storeroom.entity.InWare;
import com.xdtech.component.storeroom.entity.Storage;
import com.xdtech.component.storeroom.entity.ZoneShelves;
import com.xdtech.component.storeroom.repository.StorageRepository;
import com.xdtech.component.storeroom.repository.ZoneShelvesRepository;
import com.xdtech.component.storeroom.repository.ZonesRepository;
import com.xdtech.component.storeroom.service.InWareService;
import com.xdtech.component.storeroom.service.StorageService;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by SunK on 2018/8/7 0007.
 */
@Service
//@Transactional
public class ImportService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static long chunkSize = 5242880;//文件分片大小5M
    private List<String> RepetitionIndex = new ArrayList<>();// 存放重复的档号
    private List<String> RepetitionEntryid = new ArrayList<>();// 存放重复entryid
    private List<Entry> entryList = new ArrayList<>();// 存放重复的档案
    private List<EntryCapture> captureEntryList = new ArrayList<>();// 存放重复的档案
    private static final String IMPORT_STYPE_XML = "Xml";
    private static final String IMPORT_STYPE_EXCEL = "Excel";
    private static final String IMPORT_STYPE_ZIP_XML = "ZipXml";
    private static final String IMPORT_STYPE_ZIP_EXCEL = "ZipExcel";
    private List<Szh_RebackImport> rebackImportList = new ArrayList<>();// 存放重复的档案
    private List<AcceptEntryCapture> acceptEntryList = new ArrayList<>();// 存放重复的档案
    private static int ENTRY_STORAGE_LENGTH = 8;//实体档案存储位置分割长度
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    EntryIndexRepository entryIndexRepository;
    @Autowired
    EntryDetailRepository entryDetailRepository;
    @Autowired
    ElectronicRepository electronicRepository;
    @Autowired
    CodesetRepository codesetRepository;
    @Autowired
    EntryIndexCaptureRepository entryIndexCaptureRepository;
    @Autowired
    EntryDetailCaptureRepository entryDetailCaptureRepository;
    @Autowired
    ElectronicCaptureRepository electronicCaptureRepository;
    @Autowired
    DataNodeRepository dataNodeRepository;
    @Autowired
    RightOrganRepository rightOrganRepository;
    @Autowired
    TemplateRepository templateRepository;
    @Autowired
    NodesettingService nodesettingService;
    @Autowired
    OrganService organService;
    @Autowired
    SzhCalloutEntryRepository szhCalloutEntryRepository;
    @Autowired
    SzhEntryIndexCaptureRepository szhEntryIndexCaptureRepository;
    @Autowired
    SzhArchivesCalloutRepository szhArchivesCalloutRepository;
    @Autowired
    SzhAssemblyRepository szhAssemblyRepository;
    @Autowired
    SzhAssemblyFlowsRepository szhAssemblyFlowsRepository;
    @Autowired
    SzhMediaMetadataRepository szhMediaMetadataRepository;
    @Autowired
    SzhEntryDetailCaptureRepository szhEntryDetailCaptureRepository;
    @Autowired
    SzhEntryTrackRepository szhEntryTrackRepository;
    @Autowired
    SzhCalloutCaptureRepository szhCalloutCaptureRepository;
    @Autowired
    ImpRecord impRecord;

    @Autowired
    EntryIndexAcceptRepository entryIndexAcceptRepository;

    @Autowired
    EntryDetailAcceptRepository entryDetailAcceptRepository;

    @Autowired
    ZonesRepository zonesRepository;

    @Autowired
    ZoneShelvesRepository zoneShelvesRepository;

    @Autowired
    StorageRepository storageRepository;
    @Autowired
    StorageService storageService;
    @Autowired
    InWareService inWareService;

    @Autowired
    CaptureMetadataService captureMetadataService;
    @Autowired
    EntryIndexTempRepository entryIndexTempRepository;
    @Autowired
    CodesettingService codesettingService;
    @Autowired
    EntryIndexService entryIndexService;
    @Autowired
    SystemConfigRepository systemConfigRepository;


    @Value("${system.document.reportFullDir}")
    private String uploaddir;//电子文件上传路径
    @Value("${system.document.rootpath}")
    private String rootpath;// 系统文件根目录
    //    @Value("${imporCount}")
//    private String imporCount;// 系统文件根目录
    /*
     * @Value("${task.oa.filepath}") private String copyPath;// 文件复制目录
     */
    private String eledir;// 电子文件存储路径

    // 点击保存后的电子文件存放路径(electronics/storages/年/月/日/类型(capture-采集、management-数据管理)/条目ID)
    public String getStorageBaseDir() {
        Calendar cal = Calendar.getInstance();
        return "/electronics/storages/" + cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/"
                + cal.get(Calendar.DATE) + "/" + "management";
    }

    private String getStorageDir() {
        Calendar cal = Calendar.getInstance();
        eledir = rootpath + getStorageBaseDir();

        File eleDir = new File(eledir);
        if (!eleDir.exists()) {
            eleDir.mkdirs();
        }
        return eledir;
    }

    public String captureGetStorageBaseDir() {
        Calendar cal = Calendar.getInstance();
        return "/electronics/storages/" + cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/"
                + cal.get(Calendar.DATE) + "/" + "capture";
    }

    private String captureGetStorageDir() {
        Calendar cal = Calendar.getInstance();
        eledir = rootpath + captureGetStorageBaseDir();

        File eleDir = new File(eledir);
        if (!eleDir.exists()) {
            eleDir.mkdirs();
        }
        return eledir;
    }

    // 文件转存
    public String fileTransfer(HttpServletRequest request) {
        boolean isMutipart = ServletFileUpload.isMultipartContent(request);
        String fileTransferPath = "";
        if (isMutipart) {
            StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
            MultipartFile file = req.getFile("file");// 获取到上传文件---一个文件一次请求
            File importPath = new File(rootpath + "/OAFile" + "/upload");
            if (!importPath.isDirectory() && !importPath.exists()) {
                importPath.mkdir();
            }
            fileTransferPath = importPath + "/" + file.getOriginalFilename();
            try {
                file.transferTo(new File(importPath + "/" + file.getOriginalFilename()));// 文件转存
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return fileTransferPath;
    }

    public List<Tb_entry_index> ListTransformEntryIndexde(List<List<String>> lists, String[] fieldcods, String nodeid)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            SecurityException, NoSuchFieldException, ParseException {
        List<Tb_entry_index> indexList = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            Tb_entry_index index = ValueUtil.creatEntryIndex(fieldcods, lists.get(i));
            indexList.add(index);
        }
        return indexList;
    }

    // 转换数据-判断重复
    public List<Tb_entry_index> ListTransformEntryIndex(List<List<String>> lists, String[] fieldcods, String nodeid, String isRepeat)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            SecurityException, NoSuchFieldException, ParseException {
        List<Tb_entry_index> indexList = new ArrayList<>();
        if (!"OK".equals(isRepeat)) {
            for (int i = 0; i < lists.size(); i++) {
                Tb_entry_index index = ValueUtil.creatEntryIndex(fieldcods, lists.get(i));
                indexList.add(index);
            }
        } else {
            nodeid = String.format("%1$-36s", (String) nodeid);
            List<Tb_entry_index> tb_entry_indexList = new ArrayList<>();
            List<Tb_entry_detail> tb_entry_detailList = new ArrayList<>();
            for (int i = 0; i < lists.size(); i++) {
                Tb_entry_index index = ValueUtil.creatEntryIndex(fieldcods, lists.get(i));
                Tb_entry_detail detail = ValueUtil.creatEntryDetail(fieldcods, lists.get(i));
                List<Tb_codeset> codesets = codesetRepository.findByDatanodeidOrderByOrdernum(nodeid);
                String Archivecode = index.getArchivecode();
                if (codesets.size() > 1) {// 多个档号组成字段
                    // 使用档号字段和节点查重
                    long count = entryIndexRepository.findEntryidCount(index.getArchivecode(), nodeid);
                    if (count < 1) { // 不重复
                        indexList.add(index);
                    } else {
                        List<String> idstr = entryIndexRepository.findAllByArchivecode(index.getArchivecode(), nodeid);
                        index.setEntryid(idstr == null ? null : idstr.get(0));
                        detail.setEntryid(idstr == null ? null : idstr.get(0));
                        indexList.add(index);
                        tb_entry_indexList.add(index);
                        tb_entry_detailList.add(detail);
                        RepetitionIndex.add(index.getArchivecode());
                    }
                } else if (codesets.size() == 1) {// 一个档号组成字段
                    // 获取条目数据中对应字段的数据
                    String fieldData = GainField.getFieldValueByName(codesets.get(codesets.size() - 1).getFieldcode(),
                            index) + "";
                    String sql = "select count(nodeid) from tb_entry_index where "
                            + codesets.get(codesets.size() - 1).getFieldcode() + "=" + "'" + fieldData + "'"
                            + " and NODEID='" + nodeid + "'";
                    Query query = entityManager.createNativeQuery(sql);
                    int counts = query.getSingleResult() == null ? 0 : Integer.valueOf(query.getSingleResult().toString());
                    if (counts < 1) {// 不重复
                        indexList.add(index);
                    } else {// 重复
                        String sqls = "select entryid from tb_entry_index where "
                                + codesets.get(codesets.size() - 1).getFieldcode() + "=" + "'" + fieldData + "'"
                                + " and NODEID='" + nodeid + "'";
                        Query querys = entityManager.createNativeQuery(sqls);
                        String entryid = querys.getSingleResult() == null ? null : String.valueOf(querys.getSingleResult().toString());
                        index.setEntryid(entryid);
                        detail.setEntryid(entryid);
                        indexList.add(index);
                        tb_entry_indexList.add(index);
                        tb_entry_detailList.add(detail);
                        RepetitionEntryid.add(index.getEntryid());
                    }
                } else if (codesets.size() == 0) {// 没有设置档号字段
                    // 不需要查重
                    indexList.add(index);
                }
            }
            List<Entry> entry = createEntrtList(tb_entry_indexList, tb_entry_detailList);
            entryList.addAll(entry);
        }
        return indexList;// 返回没有重复档号的条目
    }

    // 转换数据-判断重复  库房导入
    public List<Tb_entry_index> StorageListTransformEntryIndex(List<List<String>> lists, String[] fieldcods, String nodeid, String isRepeat, List<String> errorArchivecodeList)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            SecurityException, NoSuchFieldException, ParseException {
        List<Tb_entry_index> indexList = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            List<Tb_entry_index> indexs = entryIndexRepository.findByArchivecode(lists.get(i).get(1));
            if(indexs.size()==1){
                boolean flag=true;//是否已入库
                Tb_entry_index index=indexs.get(0);
                String entrystorage=index.getEntrystorage();
                if(entrystorage!=null&&entrystorage.length()>10){//已存在库存信息
                    //根据archivecode查询条目是否已经在库
                    try{
                        Storage storage = storageRepository.findByChipcode(lists.get(i).get(1));
                        if(storage==null){
                            flag=false;
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    flag=false;
                }
                if(flag){
                    errorArchivecodeList.add(lists.get(i).get(1)+" 档号已入库");
                }else{
                    index.setEntrystorage(lists.get(i).get(0));
                    index=entryIndexRepository.save(index);
                    indexList.add(index);
                }

            }else if(indexs.size()>1){//档号有重复的
                errorArchivecodeList.add(lists.get(i).get(1)+" 档号重复");
            }else{//没有找到档好对应的条目
                errorArchivecodeList.add(lists.get(i).get(1)+" 没找到对应的条目");
            }
        }
        return indexList;
    }

    // 转换数据-判断重复  库房导入临时表
    public List<Tb_entry_index_temp> StorageListTransformEntryIndexTemp(List<List<String>> lists, String[] fieldcods, String nodeid, String isRepeat, List<String> errorArchivecodeList)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            SecurityException, NoSuchFieldException, ParseException {
        String uniquetag = BatchModifyService.getUniquetagByType("kfdr");//库房导入
        List<Tb_entry_index_temp> indexTempList = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            List<String> valueList=lists.get(i);//每行的数值集合
            Tb_entry_index_temp temp= ValueUtil.creatEntryIndexTemp(fieldcods, lists.get(i));
            temp.setUniquetag(uniquetag);
            temp.setEntryid(uniquetag+i);//临时表不自增，需设置主键
            indexTempList.add(temp);
        }
        entryIndexTempRepository.save(indexTempList);
        return indexTempList;
    }

    // 数据采集--转换数据-判断重复
    public List<Tb_entry_index_capture> captureListTransformEntryIndex(List<List<String>> lists, String[] fieldcods,
                                                                       String nodeid, String isRepeat) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchFieldException, SecurityException, NoSuchMethodException, ParseException {
        List<Tb_entry_index_capture> indexList = new ArrayList<>();
        if (!"OK".equals(isRepeat)) {
            for (int i = 0; i < lists.size(); i++) {
                Tb_entry_index_capture index = ValueUtil.captureCreatEntryIndex(fieldcods, lists.get(i));
                indexList.add(index);
            }
        } else {
            nodeid = String.format("%1$-36s", (String) nodeid);
            List<Tb_entry_index_capture> tb_entry_indexList = new ArrayList<>();
            List<Tb_entry_detail_capture> tb_entry_detailList = new ArrayList<>();
            for (int i = 0; i < lists.size(); i++) {
                Tb_entry_index_capture index = ValueUtil.captureCreatEntryIndex(fieldcods, lists.get(i));
                Tb_entry_detail_capture detail = ValueUtil.captureCreatEntryDetail(fieldcods, lists.get(i));
                List<Tb_codeset> codesets = codesetRepository.findByDatanodeidOrderByOrdernum(nodeid);
                if (codesets.size() > 1) {// 多个档号组成字段
                    // 使用档号字段和节点查重
                    long count = entryIndexCaptureRepository.findEntryidCount(index.getArchivecode(), nodeid);
                    if (count < 1) { // 不重复
                        indexList.add(index);
                    } else {
                        List<String> idstr = entryIndexCaptureRepository.findEntryidByarchivecodeAndNodid(index.getArchivecode(), nodeid);
                        index.setEntryid(idstr == null ? null : idstr.get(0));
                        detail.setEntryid(idstr == null ? null : idstr.get(0));
                        indexList.add(index);
                        tb_entry_indexList.add(index);
                        tb_entry_detailList.add(detail);
                        RepetitionIndex.add(index.getArchivecode());
                    }
                } else if (codesets.size() == 1) {// 一个档号组成字段
                    // 获取条目数据中对应字段的数据
                    String fieldData = GainField.getFieldValueByName(codesets.get(codesets.size() - 1).getFieldcode(),
                            index) + "";
                    String sql = "select count(nodeid) from tb_entry_index_capture where "
                            + codesets.get(codesets.size() - 1).getFieldcode() + "=" + "'" + fieldData + "'"
                            + " and NODEID='" + nodeid + "'";
                    Query query = entityManager.createNativeQuery(sql);
                    int counts = query.getSingleResult() == null ? 0 : Integer.valueOf(query.getSingleResult().toString());
                    if (counts < 1) {// 不重复
                        indexList.add(index);
                    } else {// 重复
                        String sqls = "select entryid from tb_entry_index_capture where "
                                + codesets.get(codesets.size() - 1).getFieldcode() + "=" + "'" + fieldData + "'"
                                + " and NODEID='" + nodeid + "'";
                        Query querys = entityManager.createNativeQuery(sqls);
                        String idstr = querys.getSingleResult() == null ? null : String.valueOf(querys.getSingleResult().toString());
                        index.setEntryid(idstr);
                        detail.setEntryid(idstr);
                        indexList.add(index);
                        tb_entry_indexList.add(index);
                        tb_entry_detailList.add(detail);
                        RepetitionEntryid.add(index.getEntryid());
                    }
                } else if (codesets.size() == 0) {// 没有设置档号字段
                    // 不需要查重
                    indexList.add(index);
                }
            }
            List<EntryCapture> entry = captureCreateEntrtList(tb_entry_indexList, tb_entry_detailList);
            captureEntryList.addAll(entry);
        }
        return indexList;// 返回没有重复档号的条目
    }

    // 目录接收--转换数据-判断重复
    public List<Tb_entry_index_accept> captureListTransformEntryIndexAccept(List<List<String>> lists, String[] fieldcods,
                                                                            String nodeid, String isRepeat) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchFieldException, SecurityException, NoSuchMethodException, ParseException {
        List<Tb_entry_index_accept> indexList = new ArrayList<>();
        if (!"OK".equals(isRepeat)) {
            for (int i = 0; i < lists.size(); i++) {
                Tb_entry_index_accept index = ValueUtil.captureCreatEntryIndexAccept(fieldcods, lists.get(i));
                indexList.add(index);
            }
        } else {
            nodeid = String.format("%1$-36s", (String) nodeid);
            List<Tb_entry_index_accept> tb_entry_indexList = new ArrayList<>();
            List<Tb_entry_detail_accept> tb_entry_detailList = new ArrayList<>();
            for (int i = 0; i < lists.size(); i++) {
                Tb_entry_index_accept index = ValueUtil.captureCreatEntryIndexAccept(fieldcods, lists.get(i));
                Tb_entry_detail_accept detail = ValueUtil.captureCreatEntryDetailAccept(fieldcods, lists.get(i));
                List<Tb_codeset> codesets = codesetRepository.findByDatanodeidOrderByOrdernum(nodeid);
                if (codesets.size() > 1) {// 多个档号组成字段
                    // 使用档号字段和节点查重
                    long count = entryIndexCaptureRepository.findEntryidCount(index.getArchivecode(), nodeid);
                    if (count < 1) { // 不重复
                        indexList.add(index);
                    } else {
                        List<String> idstr = entryIndexCaptureRepository.findEntryidByarchivecodeAndNodid(index.getArchivecode(), nodeid);
                        index.setEntryid(idstr == null ? null : idstr.get(0));
                        detail.setEntryid(idstr == null ? null : idstr.get(0));
                        indexList.add(index);
                        tb_entry_indexList.add(index);
                        tb_entry_detailList.add(detail);
                        RepetitionIndex.add(index.getArchivecode());
                    }
                } else if (codesets.size() == 1) {// 一个档号组成字段
                    // 获取条目数据中对应字段的数据
                    String fieldData = GainField.getFieldValueByName(codesets.get(codesets.size() - 1).getFieldcode(),
                            index) + "";
                    String sql = "select count(nodeid) from tb_entry_index_capture where "
                            + codesets.get(codesets.size() - 1).getFieldcode() + "=" + "'" + fieldData + "'"
                            + " and NODEID='" + nodeid + "'";
                    Query query = entityManager.createNativeQuery(sql);
                    int counts = query.getSingleResult() == null ? 0 : Integer.valueOf(query.getSingleResult().toString());
                    if (counts < 1) {// 不重复
                        indexList.add(index);
                    } else {// 重复
                        String sqls = "select entryid from tb_entry_index_accept where "
                                + codesets.get(codesets.size() - 1).getFieldcode() + "=" + "'" + fieldData + "'"
                                + " and NODEID='" + nodeid + "'";
                        Query querys = entityManager.createNativeQuery(sqls);
                        String idstr = querys.getSingleResult() == null ? null : String.valueOf(querys.getSingleResult().toString());
                        index.setEntryid(idstr);
                        detail.setEntryid(idstr);
                        indexList.add(index);
                        tb_entry_indexList.add(index);
                        tb_entry_detailList.add(detail);
                        RepetitionEntryid.add(index.getEntryid());
                    }
                } else if (codesets.size() == 0) {// 没有设置档号字段
                    // 不需要查重
                    indexList.add(index);
                }
            }
            List<AcceptEntryCapture> entry = captureCreateEntrtListAccept(tb_entry_indexList, tb_entry_detailList);
            acceptEntryList.addAll(entry);
        }
        return indexList;// 返回没有重复档号的条目
    }

    // 转换数据
    public List<Tb_entry_detail> ListTransformEntryDetail(List<List<String>> lists, String[] fieldcods)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            SecurityException, NoSuchFieldException, ParseException {
        List<Tb_entry_detail> details = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            Tb_entry_detail detail = ValueUtil.creatEntryDetail(fieldcods, lists.get(i));
            details.add(detail);
        }
        return details;
    }

    // 数据采集--转换数据
    public List<Tb_entry_detail_capture> captureListTransformEntryDetail(List<List<String>> lists, String[] fieldcods)
            throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, ParseException {
        List<Tb_entry_detail_capture> details = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            Tb_entry_detail_capture detail = ValueUtil.captureCreatEntryDetail(fieldcods, lists.get(i));
            details.add(detail);
        }
        return details;
    }

    // 目录接收--转换数据
    public List<Tb_entry_detail_accept> ListTransformEntryDetailAccept(List<List<String>> lists, String[] fieldcods)
            throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, ParseException {
        List<Tb_entry_detail_accept> details = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            Tb_entry_detail_accept detail = ValueUtil.captureCreatEntryDetailAccept(fieldcods, lists.get(i));
            details.add(detail);
        }
        return details;
    }

    // 将entry添加进数据库
    public void saveEntry(List<Tb_entry_index> indexList, List<Tb_entry_detail> details, String nodeid) {
        for (int i = 0; i < indexList.size(); i++) {
            indexList.get(i).setNodeid(nodeid);
            indexList.get(i).setEntryid(null);
            //判断库存份数---如果为空则设置库存份数为1
            if ("".equals(indexList.get(i).getKccount()) || indexList.get(i).getKccount() == null) {
                indexList.get(i).setKccount("1");
            }
            //判断份数---如果为空则设置份数为1
            if ("".equals(indexList.get(i).getFscount()) || indexList.get(i).getFscount() == null) {
                indexList.get(i).setFscount("1");
            }
            Tb_entry_index entryid = entryIndexRepository.save(indexList.get(i));
            details.get(i).setEntryid(entryid.getEntryid());
            Tb_entry_detail detail = entryDetailRepository.save(details.get(i));
            //进行业务元数据的采集
            captureMetadataService.saveServiceMetadata(entryid.getEntryid(), "数据管理", "导入");
        }
    }

    // 将entry添加进数据库
    public void saveEntryde(List<Tb_entry_index> indexList, List<Tb_entry_detail> details, String nodeid) {
        for (int i = 0; i < indexList.size(); i++) {
            indexList.get(i).setNodeid(nodeid);
            indexList.get(i).setEntryid(null);
        }
        List<Tb_entry_index> bb = entryIndexRepository.save(indexList);
        for (int i = 0; i < details.size(); i++) {
            details.get(i).setEntryid(bb.get(i).getEntryid());
        }
        entryDetailRepository.save(details);
    }

    // 数据采集--将entry添加进数据库
    public void saveCaptureEntry(List<Tb_entry_index_capture> indexList, List<Tb_entry_detail_capture> details,
                                 String nodeid) {
        for (int i = 0; i < indexList.size(); i++) {
            indexList.get(i).setNodeid(nodeid);
            indexList.get(i).setEntryid(null);
            Tb_entry_index_capture entryid = entryIndexCaptureRepository.save(indexList.get(i));
            details.get(i).setEntryid(entryid.getEntryid());
            Tb_entry_detail_capture detail = entryDetailCaptureRepository.save(details.get(i));
            //进行业务元数据的采集
            captureMetadataService.saveServiceMetadata(entryid.getEntryid(), "数据采集", "导入");
        }
    }

    //FTP 导入方法
    public void ftpSaveEntry(List<Tb_entry_index> indexList, List<Tb_entry_detail> details, String nodeid,
                             List<String> eleFolder) {
        for (int i = 0; i < indexList.size(); i++) {
            indexList.get(i).setNodeid(nodeid);
            String indexEntryId = indexList.get(i).getEntryid();
            indexList.get(i).setEntryid(null);
            Tb_entry_index entryid = entryIndexRepository.save(indexList.get(i));
            details.get(i).setEntryid(entryid.getEntryid());
            Tb_entry_detail detail = entryDetailRepository.save(details.get(i));
            String newpath = getStorageDir();
            for (int k = 0; k < eleFolder.size(); k++) {// 遍历原文目录
                String[] lib = eleFolder.get(k).replaceAll("\\\\", "/").split("/");
                String libName = lib[lib.length - 1];
                // 未插入数据库前的entryid
                // 判断是否存在entryid命名目录
                //String indexEntryId = indexList.get(i).getEntryid().trim();
                if (indexEntryId != null) {
                    if (indexEntryId.trim().equals(libName)) {
                        try {
                            FileUtil.copyDir(eleFolder.get(k), newpath + "/" + entryid.getEntryid());// 将原文文件copy到newpath
                            List<String> files = FileUtil.getFile(eleFolder.get(k));
                            for (int l = 0; l < files.size(); l++) {// 遍历单个条目的原文数
                                Tb_electronic electronic = new Tb_electronic();
                                String[] fnames = files.get(l).replaceAll("\\\\", "/").split("/");
                                String eleFileName = fnames[fnames.length - 1];// 带后缀的原文名
                                String suffix = eleFileName.substring(eleFileName.lastIndexOf(".") + 1);
                                electronic.setFilepath((newpath + "/" + entryid.getEntryid()).replace(rootpath, ""));
                                electronic.setFilename(eleFileName);
                                electronic.setFilesize(String.valueOf(new File(files.get(l)).length()));
                                electronic.setEntryid(entryid.getEntryid());
                                electronic.setFiletype(suffix);
                                electronicRepository.save(electronic);
                            }
                            // 修改对应条目的eleid
                            entryIndexRepository.updateEleidByEntryid(String.valueOf(files.size()), entryid.getEntryid());
                        } catch (IOException e) {
                            logger.error(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    // 将entry添加进数据库---zip
    public void ZipSaveEntry(List<Tb_entry_index> indexList, List<Tb_entry_detail> details, String nodeid,
                             List<String> eleFolder) {
        for (int i = 0; i < indexList.size(); i++) {
            indexList.get(i).setNodeid(nodeid);
            String indexEntryId = indexList.get(i).getEntryid();
            indexList.get(i).setEntryid(null);
            Tb_entry_index entryid = entryIndexRepository.save(indexList.get(i));
            details.get(i).setEntryid(entryid.getEntryid());
            Tb_entry_detail detail = entryDetailRepository.save(details.get(i));
            //进行业务元数据的采集
            captureMetadataService.saveServiceMetadata(entryid.getEntryid(), "数据管理", "导入");
            String newpath = getStorageDir();
            for (int k = 0; k < eleFolder.size(); k++) {// 遍历原文目录
                String[] lib = eleFolder.get(k).replaceAll("\\\\", "/").split("/");
                String libName = lib[lib.length - 1];
                String indexArchivecode = entryid.getArchivecode();
                String newArchivalList = null;
                if (indexArchivecode != null) {
                    newArchivalList = indexArchivecode.replaceAll("\\·", "-");//防止档号中有'.'点号
                }
                if (newArchivalList != null) {
                    if (libName.equals(newArchivalList)) {// 存在同档号名的原文目录
                        try {
                            FileUtil.copyDir(eleFolder.get(k), newpath + "/" + entryid.getEntryid());// 将原文文件copy到newpath
                            // 1-获取目录下的所有文件
                            List<String> files = FileUtil.getFile(eleFolder.get(k));
                            for (int l = 0; l < files.size(); l++) {// 遍历单个条目的原文数
                                Tb_electronic electronic = new Tb_electronic();
                                String[] fnames = files.get(l).replaceAll("\\\\", "/").split("/");
                                String eleFileName = fnames[fnames.length - 1];// 带后缀的原文名
                                String suffix = eleFileName.substring(eleFileName.lastIndexOf(".") + 1);
                                electronic.setFilepath((newpath + "/" + entryid.getEntryid()).replace(rootpath, ""));
                                electronic.setFilename(eleFileName);
                                electronic.setFilesize(String.valueOf(new File(files.get(l)).length()));
                                electronic.setEntryid(entryid.getEntryid());
                                electronic.setFiletype(suffix);
                                electronicRepository.save(electronic);
                            }
                            // 修改对应条目的eleid
                            entryIndexRepository.updateEleidByEntryid(String.valueOf(files.size()),
                                    entryid.getEntryid());
                        } catch (IOException e) {
                            logger.error(e.getMessage());
                        }
                    }
                }
                // 未插入数据库前的entryid
                // 判断是否存在entryid命名目录
                //String indexEntryId = indexList.get(i).getEntryid().trim();
                if (indexEntryId != null) {
                    if (indexEntryId.trim().equals(libName)) {
                        try {
                            FileUtil.copyDir(eleFolder.get(k), newpath + "/" + entryid.getEntryid());// 将原文文件copy到newpath
                            List<String> files = FileUtil.getFile(eleFolder.get(k));
                            for (int l = 0; l < files.size(); l++) {// 遍历单个条目的原文数
                                Tb_electronic electronic = new Tb_electronic();
                                String[] fnames = files.get(l).replaceAll("\\\\", "/").split("/");
                                String eleFileName = fnames[fnames.length - 1];// 带后缀的原文名
                                String suffix = eleFileName.substring(eleFileName.lastIndexOf(".") + 1);
                                electronic.setFilepath((newpath + "/" + entryid.getEntryid()).replace(rootpath, ""));
                                electronic.setFilename(eleFileName);
                                electronic.setFilesize(String.valueOf(new File(files.get(l)).length()));
                                electronic.setEntryid(entryid.getEntryid());
                                electronic.setFiletype(suffix);
                                electronicRepository.save(electronic);
                            }
                            // 修改对应条目的eleid
                            entryIndexRepository.updateEleidByEntryid(String.valueOf(files.size()), entryid.getEntryid());
                        } catch (IOException e) {
                            logger.error(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    // 数据采集---将entry添加进数据库---zip
    public void captureZipSaveEntry(List<Tb_entry_index_capture> indexList, List<Tb_entry_detail_capture> details,
                                    String nodeid, List<String> eleFolder) {
        for (int i = 0; i < indexList.size(); i++) {
            indexList.get(i).setNodeid(nodeid);
            String indexEntryId = indexList.get(i).getEntryid();
            indexList.get(i).setEntryid(null);
            Tb_entry_index_capture entryid = entryIndexCaptureRepository.save(indexList.get(i));
            details.get(i).setEntryid(entryid.getEntryid());
            Tb_entry_detail_capture detail = entryDetailCaptureRepository.save(details.get(i));
            String newpath = captureGetStorageDir();
            //进行业务元数据的采集
            captureMetadataService.saveServiceMetadata(entryid.getEntryid(), "数据采集", "导入");
            for (int k = 0; k < eleFolder.size(); k++) {// 遍历原文目录
                String[] lib = eleFolder.get(k).replaceAll("\\\\", "/").split("/");
                String libName = lib[lib.length - 1];
                String indexArchivecode = entryid.getArchivecode();
                String newArchivalList = null;
                if (indexArchivecode != null) {
                    newArchivalList = indexArchivecode.replaceAll("\\·", "-");//防止档号中有'.'点号
                }
                if (newArchivalList != null) {
                    if (libName.equals(newArchivalList)) {// 存在同档号名的原文目录
                        try {
                            // 根据档号查找entryid
                            /*String indexEntryId = entryIndexCaptureRepository.findAllByArchivecode(indexArchivecode,
                                    nodeid);*/
                            FileUtil.copyDir(eleFolder.get(k), newpath + "/" + entryid.getEntryid());// 将原文文件copy到newpath
                            // 1-获取目录下的所有文件
                            List<String> files = FileUtil.getFile(eleFolder.get(k));
                            for (int l = 0; l < files.size(); l++) {// 遍历单个条目的原文数
                                Tb_electronic_capture electronic = new Tb_electronic_capture();
                                String[] fnames = files.get(l).replaceAll("\\\\", "/").split("/");
                                String eleFileName = fnames[fnames.length - 1];// 带后缀的原文名
                                String suffix = eleFileName.substring(eleFileName.lastIndexOf(".") + 1);
                                electronic.setFilepath((newpath + "/" + entryid.getEntryid()).replace(rootpath, ""));
                                electronic.setFilename(eleFileName);
                                electronic.setFilesize(String.valueOf(new File(files.get(l)).length()));
                                electronic.setEntryid(entryid.getEntryid());
                                electronic.setFiletype(suffix);
                                electronicCaptureRepository.save(electronic);
                            }
                            // 修改对应条目的eleid
                            entryIndexCaptureRepository.updateEleidByEntryid(String.valueOf(files.size()),
                                    entryid.getEntryid());
                        } catch (IOException e) {
                            logger.error(e.getMessage());
                        }
                    }
                }
                // 未插入数据库前的entryid
                // 判断是否存在entryid命名目录
                //String indexEntryId = indexList.get(i).getEntryid().trim();
                if (indexEntryId != null) {
                    if (indexEntryId.trim().equals(libName)) {
                        try {
                            FileUtil.copyDir(eleFolder.get(k), newpath + "/" + entryid.getEntryid());// 将原文文件copy到newpath
                            List<String> files = FileUtil.getFile(eleFolder.get(k));
                            for (int l = 0; l < files.size(); l++) {// 遍历单个条目的原文数
                                Tb_electronic electronic = new Tb_electronic();
                                String[] fnames = files.get(l).replaceAll("\\\\", "/").split("/");
                                String eleFileName = fnames[fnames.length - 1];// 带后缀的原文名
                                String suffix = eleFileName.substring(eleFileName.lastIndexOf(".") + 1);
                                electronic.setFilepath((newpath + "/" + entryid.getEntryid()).replace(rootpath, ""));
                                electronic.setFilename(eleFileName);
                                electronic.setFilesize(String.valueOf(new File(files.get(l)).length()));
                                electronic.setEntryid(entryid.getEntryid());
                                electronic.setFiletype(suffix);
                                electronicRepository.save(electronic);
                            }
                            // 修改对应条目的eleid
                            entryIndexRepository.updateEleidByEntryid(String.valueOf(files.size()), entryid.getEntryid());
                        } catch (IOException e) {
                            logger.error(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    // 创建导入失败的excel
    public List<String> CreateFailureExcel(String fileName, String[] fieldname, String[] fieldcode)
            throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, IOException, NoSuchFieldException {
        List<String> list = new ArrayList<>();
        if (RepetitionIndex.size() > 0) {
            list = RepetitionIndex;
            CreateExcel.createErroExcel(fileName, entryList, fieldcode, fieldname);
        } else if (RepetitionEntryid.size() > 0) {
            list = RepetitionEntryid;
            CreateExcel.createErroExcel(fileName, entryList, fieldcode, fieldname);
        }
        return list;
    }

    // 数据采集--创建导入失败的excel
    public List<String> captureCreateFailureExcel(String fileName, String[] fieldname, String[] fieldcode, String importtype)
            throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        List<String> list = new ArrayList<>();
        if (RepetitionIndex.size() > 0) {
            list = RepetitionIndex;
            // CreateExcel.CreateErroExcel(fileName, RepetitionIndex, "档号");
            CreateExcel.captureCreateErroExcel(fileName, captureEntryList, fieldcode, fieldname, acceptEntryList, importtype);
        } else if (RepetitionEntryid.size() > 0) {
            list = RepetitionEntryid;
            // CreateExcel.CreateErroExcel(fileName, RepetitionEntryid,
            // "档号ID");
            CreateExcel.captureCreateErroExcel(fileName, captureEntryList, fieldcode, fieldname, acceptEntryList, importtype);
        }
        // list.remove(list.size() - 1);//去掉集合最后的null元素
        return list;
    }

    // 创建导入失败的xml
    public List<String> createFailureXml(String fileName, String[] fieldcode, String[] fieldname)
            throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        List<String> list = new ArrayList<>();
        if (RepetitionIndex.size() > 0) {
            list = RepetitionIndex;
            // xmlutil.createFailureXml(RepetitionIndex, fileName);
            XmlUtil.CreateFailureXml(fieldname, fieldcode, entryList, fileName);
        } else if (RepetitionEntryid.size() > 0) {
            list = RepetitionEntryid;
            // xmlutil.createFailureXml(RepetitionEntryid, fileName);
            XmlUtil.CreateFailureXml(fieldname, fieldcode, entryList, fileName);
        }
        return list;
    }

    // 数据采集--创建导入失败的xml
    public List<String> captureCreateFailureXml(String fileName, String[] fieldcode, String[] fieldname, String importtype)
            throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        List<String> list = new ArrayList<>();
        if (RepetitionIndex.size() > 0) {
            list = RepetitionIndex;
            // xmlutil.createFailureXml(RepetitionIndex, fileName);
            XmlUtil.captureCreateFailureXml(fieldname, fieldcode, captureEntryList, fileName, acceptEntryList, importtype);
        } else if (RepetitionEntryid.size() > 0) {
            list = RepetitionEntryid;
            // xmlutil.createFailureXml(RepetitionEntryid, fileName);
            XmlUtil.captureCreateFailureXml(fieldname, fieldcode, captureEntryList, fileName, acceptEntryList, importtype);
        }
        return list;
    }

    // 读取excel 插入数据库
    public int excelSave(String fileTransferPath, String[] fieldName, String[] fieldCodes, String NodeIds,
                         String fileName, String isRepeat)
            throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, IOException, NoSuchFieldException, ParseException {
        List<List<String>> lists = new ArrayList<>();
        Map<String, String> map = ReadExcel.getAllFieldCode(new File(fileTransferPath), fieldName, fieldCodes);
        List<String> fname = new ArrayList<>();
        List<String> fcode = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            fcode.add(entry.getKey());
            fname.add(entry.getValue());
        }
        String[] userFieldCode = fcode.toArray(new String[fcode.size()]);
        String[] userFieldName = fname.toArray(new String[fname.size()]);

        try {
            lists = ReadExcel.readAllVersionExcel(new File(fileTransferPath), fieldName);// 解析文件
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        List<Tb_entry_index> entry_indexs = ListTransformEntryIndex(lists, userFieldCode, NodeIds, isRepeat);
        if (entry_indexs.size() > 0) { // entry_indexs ---做了重复判断（里面是不重复档号元素集合）
            List<Tb_entry_detail> entry_details = ListTransformEntryDetail(lists, userFieldCode);
            saveEntry(entry_indexs, entry_details, NodeIds);
        }
        if (lists.size() > 0) {
            if (entry_indexs.size() < lists.size()) {
                CreateFailureExcel(fileName, userFieldName, userFieldCode);// 创建导入失败excel
                RepetitionIndex.removeAll(RepetitionIndex);
                RepetitionEntryid.removeAll(RepetitionEntryid);
                entryList.removeAll(entryList);
            }
            int count = lists.size() - entry_indexs.size();// 失败数
            return count;
        }
        return 0;
    }

    // 数据采集--读取excel 插入数据库
    public int captureExcelSave(String fileTransferPath, String[] fieldName, String[] fieldCodes, String NodeIds,
                                String fileName)
            throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, IOException, NoSuchFieldException, ParseException {
        List<List<String>> lists = new ArrayList<>();
        Map<String, String> map = ReadExcel.getAllFieldCode(new File(fileTransferPath), fieldName, fieldCodes);
        List<String> fname = new ArrayList<>();
        List<String> fcode = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            fcode.add(entry.getKey());
            fname.add(entry.getValue());
        }
        String[] userFieldCode = fcode.toArray(new String[fcode.size()]);
        String[] userFieldName = fname.toArray(new String[fname.size()]);
        try {
            lists = ReadExcel.readAllVersionExcel(new File(fileTransferPath), fieldName);// 解析文件
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        List<Tb_entry_index_capture> entry_indexs = captureListTransformEntryIndex(lists, fieldCodes, NodeIds, "OK");
        if (entry_indexs.size() > 0) { // entry_indexs ---做了重复判断（里面是不重复档号元素集合）
            List<Tb_entry_detail_capture> entry_details = captureListTransformEntryDetail(lists, fieldCodes);
            saveCaptureEntry(entry_indexs, entry_details, NodeIds);
        }
        if (lists.size() > 0) {
            if (entry_indexs.size() < lists.size()) {
                try {
                    captureCreateFailureExcel(fileName, fieldName, fieldCodes, null);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
                // 创建导入失败excel
                RepetitionIndex.removeAll(RepetitionIndex);
                RepetitionEntryid.removeAll(RepetitionEntryid);
                captureEntryList.removeAll(captureEntryList);
            }
            int count = lists.size() - entry_indexs.size();// 失败数
            return count;
        }
        return 0;
    }

    // 读取xml 插入数据库
    public int xmlSave(String fileTransferPath, String[] fieldCodes, String[] fieldName, String NodeIds,
                       String fileName, String isRepeat)
            throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, IOException, NoSuchFieldException, ParseException {
        LinkedHashMap<String, String> map = XmlUtil.getXmlFieldCodeAndFieldName(fileTransferPath);
        List<String> fname = new ArrayList<>();
        List<String> fcode = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            fcode.add(entry.getKey());
            fname.add(entry.getValue());
        }
        String[] userFieldCode = fcode.toArray(new String[fcode.size()]);
        String[] userFieldName = fname.toArray(new String[fname.size()]);
        List<List<String>> lists = XmlUtil.readXml(fileTransferPath);
        List<Tb_entry_index> entry_indexs = ListTransformEntryIndex(lists, userFieldCode, NodeIds, isRepeat);
        if (entry_indexs.size() > 0) {// 档号不重复集合
            List<Tb_entry_detail> entry_details = ListTransformEntryDetail(lists, userFieldCode);
            saveEntry(entry_indexs, entry_details, NodeIds);
        }
        if (lists.size() > 0) {
            if (entry_indexs.size() < lists.size()) {
                createFailureXml(fileName, userFieldCode, userFieldName);//// 创建导入失败excel,返回重复档号/id
                RepetitionIndex.removeAll(RepetitionIndex);
                RepetitionEntryid.removeAll(RepetitionEntryid);
                entryList.removeAll(entryList);
            }
            int count = lists.size() - entry_indexs.size();// 失败数
            return count;
        }
        return 0;
    }

    // 数据采集--读取xml 插入数据库
    public int capturexmlSave(String fileTransferPath, String[] fieldCodes, String[] fieldName, String NodeIds,
                              String fileName)
            throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, IOException, NoSuchFieldException, ParseException {
        LinkedHashMap<String, String> map = XmlUtil.getXmlFieldCodeAndFieldName(fileTransferPath);
        List<String> fname = new ArrayList<>();
        List<String> fcode = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            fcode.add(entry.getKey());
            fname.add(entry.getValue());
        }
        String[] userFieldCode = fcode.toArray(new String[fcode.size()]);
        String[] userFieldName = fname.toArray(new String[fname.size()]);
        List<List<String>> lists = XmlUtil.readXml(fileTransferPath);
        List<Tb_entry_index_capture> entry_indexs = captureListTransformEntryIndex(lists, userFieldCode, NodeIds, "OK");
        if (entry_indexs.size() > 0) {// 档号不重复集合
            List<Tb_entry_detail_capture> entry_details = captureListTransformEntryDetail(lists, userFieldCode);
            saveCaptureEntry(entry_indexs, entry_details, NodeIds);
        }
        if (lists.size() > 0) {
            if (entry_indexs.size() < lists.size()) {
                captureCreateFailureXml(fileName, userFieldCode, userFieldName, null);//// 创建导入失败excel,返回重复档号/id
                RepetitionIndex.removeAll(RepetitionIndex);
                RepetitionEntryid.removeAll(RepetitionEntryid);
                captureEntryList.removeAll(captureEntryList);
            }
            int count = lists.size() - entry_indexs.size();// 失败数
            return count;
        }
        return 0;
    }

    // 获取zip包文件，进行解析以及存储
    public int zipSave(String UnZipFile, String[] fieldName, String[] fieldCodes, String NodeIds, String UserfileName, String isRepeat)
            throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException,
            ParseException {
        // 1.获取解压后的条目文件
        File file = new File(UnZipFile);
        File[] tempList = file.listFiles();
        List<String> fileList = FileUtil.getFile(tempList[0].getPath());
        // 2.获取原文目录
        List<String> eleFolder = FileUtil.getFolder(tempList[0].getPath() + "/document");
        /*// 3.获取字段模板目录
        List<String> tempFolder = FileUtil.getFolder(tempList[0].getPath());*/
        int count = 0;
        for (int i = 0; i < fileList.size(); i++) {// 条目文件
            String[] str = fileList.get(i).replaceAll("\\\\", "/").split("/");
            String fileName = str[str.length - 1];// 带后缀的文件名
            // 判断文件后缀（xml/excle）
            if (fileName.indexOf(".xml") > 0) {
                LinkedHashMap<String, String> map = XmlUtil.getXmlFieldCodeAndFieldName(fileList.get(i));
                List<String> fname = new ArrayList<>();
                List<String> fcode = new ArrayList<>();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    fcode.add(entry.getKey());
                    fname.add(entry.getValue());
                }
                String[] userFieldCode = fcode.toArray(new String[fcode.size()]);
                String[] userFieldName = fname.toArray(new String[fname.size()]);
                List<List<String>> lists = XmlUtil.readXml(fileList.get(i));
                List<Tb_entry_index> entry_indexs = ListTransformEntryIndex(lists, userFieldCode, NodeIds, isRepeat);
                if (entry_indexs.size() > 0) {// 档号不重复集合
                    List<Tb_entry_detail> entry_details = ListTransformEntryDetail(lists, userFieldCode);
                    ZipSaveEntry(entry_indexs, entry_details, NodeIds, eleFolder);// 条目都成功---进行原文分析--存入目录
                }
                if (lists.size() > 0) {
                    if (entry_indexs.size() < lists.size()) {
                        List<String> archivalList = createFailureXml(UserfileName, userFieldCode, userFieldName);// 创建导入失败xml,返回重复档号/id
                        String zippath = rootpath + "/OAFile/" + "导入失败/" + UserfileName + ".zip";// 压缩路径
                        // 条目存在失败---获取失败的档号--选择对应的目录--复制到xml导入失败目录--进行打包
                        for (int j = 0; j < archivalList.size(); j++) {// 遍历重复档号
                            for (int k = 0; k < eleFolder.size(); k++) {// 遍历原文目录
                                String[] lib = eleFolder.get(k).replaceAll("\\\\", "/").split("/");
                                String libName = lib[lib.length - 1];
                                String newArchivalList = archivalList.get(j).replaceAll("\\·", "-");//防止档号中有'.'点号
                                if (newArchivalList.trim().equals(libName)) {// 存在同档号名的目录
                                    // 拷贝文件到路径
                                    FileUtil.copyDir(eleFolder.get(k), rootpath + "/OAFile/" + "导入失败/" + UserfileName
                                            + "/document/" + archivalList.get(j).trim());
                                }
                            }
                        }
                        /*// 复制模板文件到目录下
                        for (int L = 0; L < tempFolder.size(); L++) {
                            String str3 = tempFolder.get(L).replaceAll("\\\\", "/");
                            if (!str3.equals(UnZipFile + "/" + UserfileName + "/document")) {
                                FileUtil.copyDir(tempFolder.get(L),
                                        rootpath + "/OAFile" + "/导入失败/" + UserfileName + "/字段模板信息");
                            }
                        }*/
                        FileOutputStream fileOutputStream = null;
                        try {
                            fileOutputStream = new FileOutputStream(zippath);
                            // 打包目录
                            ZipUtils.toZip(rootpath + "/OAFile/" + "导入失败/" + UserfileName, fileOutputStream, true);
                            fileOutputStream.flush();
                        } catch (IOException e) {
                            logger.error(e.getMessage());
                        } finally {
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException e) {
                                    logger.error(e.getMessage());
                                }
                            }
                        }
                        count = lists.size() - entry_indexs.size();
                        RepetitionIndex.removeAll(RepetitionIndex);// 清除重复档号
                        RepetitionEntryid.removeAll(RepetitionEntryid);
                        entryList.removeAll(entryList);
                    }
                }
            }
            if (fileName.indexOf(".xls") > 0) {
                //for (int j = 0; j < fileList.size(); j++) {// 遍历条目文件
                Map<String, String> map = ReadExcel.getAllFieldCode(new File(fileList.get(i)), fieldName, fieldCodes);
                List<String> fname = new ArrayList<>();
                List<String> fcode = new ArrayList<>();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    fcode.add(entry.getKey());
                    fname.add(entry.getValue());
                }
                String[] userFieldCode = fcode.toArray(new String[fcode.size()]);
                String[] userFieldName = fname.toArray(new String[fname.size()]);
                List<List<String>> lists = ReadExcel.readAllVersionExcel(new File(fileList.get(i)), fieldName);// 解析文件
                List<Tb_entry_index> entry_indexs = ListTransformEntryIndex(lists, userFieldCode, NodeIds, isRepeat);
                if (entry_indexs.size() > 0) { // entry_indexs
                    // ---做了重复判断（里面是不重复档号元素集合）
                    List<Tb_entry_detail> entry_details = ListTransformEntryDetail(lists, userFieldCode);
                    ZipSaveEntry(entry_indexs, entry_details, NodeIds, eleFolder);
                }
                if (lists.size() > 0) {
                    if (entry_indexs.size() < lists.size()) {
                        List<String> archivalList = CreateFailureExcel(UserfileName, userFieldName, userFieldCode);// 创建导入失败excel,返回重复档号/id
                        String zippath = rootpath + "/OAFile" + "/导入失败/" + "/" + UserfileName + ".zip";
                        for (int k = 0; k < archivalList.size(); k++) {// 遍历重复档号
                            for (int l = 0; l < eleFolder.size(); l++) {// 遍历原文目录
                                String[] lib = eleFolder.get(l).replaceAll("\\\\", "/").split("/");
                                String libName = lib[lib.length - 1];
                                String newArchivalList = archivalList.get(k).replaceAll("\\·", "-");//防止档号中有'.'点号
                                if (newArchivalList.trim().equals(libName)) {// 存在同档号名的目录
                                    FileUtil.copyDir(eleFolder.get(l), rootpath + "/OAFile" + "/导入失败/"
                                            + UserfileName + "/document/" + archivalList.get(k).trim());
                                }
                            }
                        }
                        /*// 复制模板文件到目录下
                        for (int L = 0; L < tempFolder.size(); L++) {
                            String str3 = tempFolder.get(L).replaceAll("\\\\", "/");
                            if (!str3.equals(UnZipFile + "/" + UserfileName + "/document")) {
                                FileUtil.copyDir(tempFolder.get(L),
                                        rootpath + "/OAFile" + "/导入失败/" + UserfileName + "/字段模板信息");
                            }
                        }*/
                        FileOutputStream fileOutputStream = null;
                        try {
                            fileOutputStream = new FileOutputStream(zippath);
                            ZipUtils.toZip(rootpath + "/OAFile" + "/导入失败/" + UserfileName, fileOutputStream, true);
                            fileOutputStream.flush();
                        } catch (IOException e) {
                            logger.error(e.getMessage());
                        } finally {
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException e) {
                                    logger.error(e.getMessage());
                                }
                            }
                        }
                        count = lists.size() - entry_indexs.size();
                        RepetitionIndex.removeAll(RepetitionIndex);// 清除重复档号
                        RepetitionEntryid.removeAll(RepetitionEntryid);
                        entryList.removeAll(entryList);
                    }
                }
                //}
            }
        }
        return count;
    }

    // 数据采集--获取zip包文件，进行解析以及存储
    public int captureZipSave(String UnZipFile, String[] fieldName, String[] fieldCodes, String NodeIds,
                              String UserfileName) throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException,
            ParseException {
        // 1.获取解压后的条目文件
        // 1.获取解压后的条目文件
        File file = new File(UnZipFile);
        File[] tempList = file.listFiles();
        List<String> fileList = FileUtil.getFile(tempList[0].getPath());
        // 2.获取原文目录
        List<String> eleFolder = FileUtil.getFolder(tempList[0].getPath() + "/document");
        // 3.获取字段模板目录
//        List<String> tempFolder = FileUtil.getFolder(UnZipFile + "/" + UserfileName);
        int count = 0;
        for (int i = 0; i < fileList.size(); i++) {// 条目文件
            String[] str = fileList.get(i).replaceAll("\\\\", "/").split("/");
            String fileName = str[str.length - 1];// 带后缀的文件名
            // 判断文件后缀（xml/excle）
            if (fileName.indexOf(".xml") > 0) {
                LinkedHashMap<String, String> map = XmlUtil.getXmlFieldCodeAndFieldName(fileList.get(i));
                List<String> fname = new ArrayList<>();
                List<String> fcode = new ArrayList<>();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    fcode.add(entry.getKey());
                    fname.add(entry.getValue());
                }
                String[] userFieldCode = fcode.toArray(new String[fcode.size()]);
                String[] userFieldName = fname.toArray(new String[fname.size()]);
                List<List<String>> lists = XmlUtil.readXml(fileList.get(i));
                List<Tb_entry_index_capture> entry_indexs = captureListTransformEntryIndex(lists, userFieldCode, NodeIds, "OK");
                if (entry_indexs.size() > 0) {// 档号不重复集合
                    List<Tb_entry_detail_capture> entry_details = captureListTransformEntryDetail(lists, userFieldCode);
                    captureZipSaveEntry(entry_indexs, entry_details, NodeIds, eleFolder);// 条目都成功---进行原文分析--存入目录
                }
                if (lists.size() > 0) {
                    if (entry_indexs.size() < lists.size()) {
                        List<String> archivalList = captureCreateFailureXml(UserfileName, userFieldCode, userFieldName, null);// 创建导入失败xml,返回重复档号/id
                        String zippath = rootpath + "/OAFile/" + "导入失败/" + UserfileName + ".zip";// 压缩路径
                        // 条目存在失败---获取失败的档号--选择对应的目录--复制到xml导入失败目录--进行打包
                        for (int j = 0; j < archivalList.size(); j++) {// 遍历重复档号
                            for (int k = 0; k < eleFolder.size(); k++) {// 遍历原文目录
                                String[] lib = eleFolder.get(k).replaceAll("\\\\", "/").split("/");
                                String libName = lib[lib.length - 1];
                                String newArchivalList = archivalList.get(j).replaceAll("\\·", "-");//防止档号中有'.'点号
                                if (newArchivalList.trim().equals(libName)) {// 存在同档号名的目录
                                    // 拷贝文件到路径
                                    FileUtil.copyDir(eleFolder.get(k), rootpath + "/OAFile/" + "导入失败/" + UserfileName
                                            + "/document/" + archivalList.get(j).trim());
                                }
                            }
                        }
                        // 复制模板文件到目录下
//                        for (int L = 0; L < tempFolder.size(); L++) {
//                            String str3 = tempFolder.get(L).replaceAll("\\\\", "/");
//                            if (!str3.equals(UnZipFile + "/" + UserfileName + "/document")) {
//                                FileUtil.copyDir(tempFolder.get(L),
//                                        rootpath + "/OAFile" + "/导入失败/" + UserfileName + "/字段模板信息");
//                            }
//                        }
                        FileOutputStream fileOutputStream = null;
                        try {
                            fileOutputStream = new FileOutputStream(zippath);
                            // 打包目录
                            ZipUtils.toZip(rootpath + "/OAFile/" + "导入失败/" + UserfileName, fileOutputStream, true);
                            fileOutputStream.flush();
                        } catch (IOException e) {
                            logger.error(e.getMessage());
                        } finally {
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException e) {
                                    logger.error(e.getMessage());
                                }
                            }
                        }
                        count = archivalList.size();
                        RepetitionIndex.removeAll(RepetitionIndex);// 清除重复档号
                        RepetitionEntryid.removeAll(RepetitionEntryid);
                        captureEntryList.removeAll(captureEntryList);
                    }
                }
            }
            if (fileName.indexOf(".xls") > 0) {
                //for (int j = 0; j < fileList.size(); j++) {// 遍历条目文件
                Map<String, String> map = ReadExcel.getAllFieldCode(new File(fileList.get(i)), fieldName, fieldCodes);
                List<String> fname = new ArrayList<>();
                List<String> fcode = new ArrayList<>();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    fcode.add(entry.getKey());
                    fname.add(entry.getValue());
                }
                String[] userFieldCode = fcode.toArray(new String[fcode.size()]);
                String[] userFieldName = fname.toArray(new String[fname.size()]);
                List<List<String>> lists = ReadExcel.readAllVersionExcel(new File(fileList.get(i)), fieldName);// 解析文件
                List<Tb_entry_index_capture> entry_indexs = captureListTransformEntryIndex(lists, userFieldCode,
                        NodeIds, "OK");
                if (entry_indexs.size() > 0) { // entry_indexs
                    // ---做了重复判断（里面是不重复档号元素集合）
                    List<Tb_entry_detail_capture> entry_details = captureListTransformEntryDetail(lists,
                            userFieldCode);
                    captureZipSaveEntry(entry_indexs, entry_details, NodeIds, eleFolder);
                }
                if (lists.size() > 0) {
                    if (entry_indexs.size() < lists.size()) {
                        List<String> archivalList = captureCreateFailureExcel(UserfileName, userFieldName, userFieldCode, null);// 创建导入失败excel,返回重复档号/id
                        String zippath = rootpath + "/OAFile" + "/导入失败/" + "/" + UserfileName + ".zip";
                        for (int k = 0; k < archivalList.size(); k++) {// 遍历重复档号
                            for (int l = 0; l < eleFolder.size(); l++) {// 遍历原文目录
                                String[] lib = eleFolder.get(l).replaceAll("\\\\", "/").split("/");
                                String libName = lib[lib.length - 1];
                                String newArchivalList = archivalList.get(k).replaceAll("\\·", "-");//防止档号中有'.'点号
                                if (newArchivalList.trim().equals(libName)) {// 存在同档号名的目录
                                    FileUtil.copyDir(eleFolder.get(l), rootpath + "/OAFile" + "/导入失败/"
                                            + UserfileName + "/document/" + archivalList.get(k).trim());
                                }
                            }
                        }
                        // 复制模板文件到目录下
//                        for (int L = 0; L < tempFolder.size(); L++) {
//                            String str3 = tempFolder.get(L).replaceAll("\\\\", "/");
//                            if (!str3.equals(UnZipFile + "/" + UserfileName + "/document")) {
//                                FileUtil.copyDir(tempFolder.get(L),
//                                        rootpath + "/OAFile" + "/导入失败/" + UserfileName + "/字段模板信息");
//                            }
//                        }
                        FileOutputStream fileOutputStream = null;
                        try {
                            fileOutputStream = new FileOutputStream(zippath);
                            ZipUtils.toZip(rootpath + "/OAFile" + "/导入失败/" + UserfileName, fileOutputStream, true);
                            fileOutputStream.flush();
                        } catch (IOException e) {
                            logger.error(e.getMessage());
                        } finally {
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException e) {
                                    logger.error(e.getMessage());
                                }
                            }
                        }
                        count = archivalList.size();
                        RepetitionIndex.removeAll(RepetitionIndex);// 清除重复档号
                        RepetitionEntryid.removeAll(RepetitionEntryid);
                        captureEntryList.removeAll(captureEntryList);
                    }
                }
                //}
            }
        }
        return count;
    }

    // -输出导入失败文件
    public void downloadImportFailure(HttpServletResponse response, List<String> fileList) throws IOException {
        for (int i = 0; i < fileList.size(); i++) {
            File file = new File(fileList.get(i));
            String fileName = file.getName();
            response.setHeader("Content-Disposition",
                    "attachment;filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");
            response.setHeader("Connection", "close");
            response.setHeader("Content-Type", "application/octet-stream");
            OutputStream ops = null;
            FileInputStream fis = null;
            byte[] buffer = new byte[8192];
            int bytesRead = 0;
            try {
                ops = response.getOutputStream();
                fis = new FileInputStream(fileList.get(i));
                while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                    ops.write(buffer, 0, bytesRead);
                }
                ops.flush();
            } catch (IOException e) {
                logger.error(e.getMessage());
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                    if (ops != null) {
                        ops.close();
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    // 拼接index detail 返回entry对象集合
    public List<Entry> createEntrtList(List<Tb_entry_index> indexList, List<Tb_entry_detail> details) {
        List<Entry> entryList = new ArrayList<>();
        if (indexList.size() > 0 && details.size() > 0) {
            for (int i = 0; i < indexList.size(); i++) {
                Entry entry = new Entry();// 生成entry对象 调用里面的方法
                entry.setEntryIndex(indexList.get(i));
                if (details.get(i).getEntryid() == null) {
                    details.get(i).setEntryid(indexList.get(i).getEntryid());
                }
                entry.setEntryDetial(details.get(i));
                entryList.add(entry);
            }
        }
        return entryList;
    }

    // 拼接index_capture detail_capture 返回entry对象集合
    public List<EntryCapture> captureCreateEntrtList(List<Tb_entry_index_capture> indexList,
                                                     List<Tb_entry_detail_capture> details) {
        List<EntryCapture> entryList = new ArrayList<>();
        if (indexList.size() > 0 && details.size() > 0) {
            for (int i = 0; i < indexList.size(); i++) {
                EntryCapture entry = new EntryCapture();// 生成entry对象 调用里面的方法
                entry.setEntryIndex(indexList.get(i));
                entry.setEntryDetial(details.get(i));
                entryList.add(entry);
            }
        }
        return entryList;
    }

    // 拼接index_accept detail_capture 返回entry对象集合
    public List<AcceptEntryCapture> captureCreateEntrtListAccept(List<Tb_entry_index_accept> indexList,
                                                                 List<Tb_entry_detail_accept> details) {
        List<AcceptEntryCapture> entryList = new ArrayList<>();
        if (indexList.size() > 0 && details.size() > 0) {
            for (int i = 0; i < indexList.size(); i++) {
                AcceptEntryCapture entry = new AcceptEntryCapture();// 生成entry对象 调用里面的方法
                entry.setEntryIndex(indexList.get(i));
                entry.setEntryDetial(details.get(i));
                entryList.add(entry);
            }
        }
        return entryList;
    }

    private String getUploadDir() {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        uploaddir = rootpath + "/electronics/uploads/" + userDetails.getUsername();

        File upDir = new File(uploaddir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }
        return uploaddir;
    }

    //文件分片上传调用
    public String uploadchunk(Map<String, Object> param) throws Exception {
        String tempFileName = param.get("filename") + "_tmp";
        File confFile = new File(getUploadDir(), param.get("filename") + ".conf");
        File tmpFile = new File(getUploadDir(), tempFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");

        long offset = chunkSize * Integer.parseInt((String) param.get("chunk"));
        //定位到该分片的偏移量
        accessTmpFile.seek(offset);
        //写入该分片数据
        accessTmpFile.write((byte[]) param.get("content"));

        //把该分段标记为 true 表示完成
        accessConfFile.setLength(Integer.parseInt((String) param.get("chunks")));
        accessConfFile.seek(Integer.parseInt((String) param.get("chunk")));
        accessConfFile.write(Byte.MAX_VALUE);

        //completeList 检查是否全部完成,如果数组里是否全部都是(全部分片都成功上传)
        byte[] completeList = FileUtils.readFileToByteArray(confFile);
        byte isComplete = Byte.MAX_VALUE;
        for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
            //与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
            isComplete = (byte) (isComplete & completeList[i]);
        }

        accessTmpFile.close();
        accessConfFile.close();
        String filePath = rootpath + "/OAFile" + "/upload/" + param.get("filename");
        if (!(new File(rootpath + "/OAFile" + "/upload/")).exists()) {
            (new File(rootpath + "/OAFile" + "/upload/")).mkdirs();
        }
        //上传完成，删除临时文件，移动到存储路径
        if (isComplete == Byte.MAX_VALUE) {
            confFile.delete();
            tmpFile.renameTo(new File(filePath));
        }
        return filePath;
    }

    //文件单片上传调用
    public String uploadfile(Map<String, Object> param) throws Exception {
        String targetFileName = (String) param.get("filename");
        File tmpFile = new File(getUploadDir(), targetFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        //写入数据
        accessTmpFile.write((byte[]) param.get("content"));
        accessTmpFile.close();
        String filePath = rootpath + "/OAFile" + "/upload/" + param.get("filename");
        if (!(new File(rootpath + "/OAFile" + "/upload/")).exists()) {
            (new File(rootpath + "/OAFile" + "/upload/")).mkdirs();
        }

        File targetFile = new File(filePath);
        //原来存在文件的话，先删除，后转存
        if (targetFile.exists()) {
            targetFile.delete();
        }
        tmpFile.renameTo(targetFile);
        return filePath;
    }

    //------------------------导入界面加载资源------------------------------------//

    /**
     * 查找数据节点，用于导入目的节点选择
     *
     * @param pid 父节点ID
     * @return
     */
    public List findDataNodes(String pid) {
        SecurityUser userDetails=(SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Tb_right_organ> childNode = rightOrganRepository.findByParentid(userDetails.getReplaceOrganid());
        Tb_right_organ userOrgan = rightOrganRepository.findByOrganid(userDetails.getReplaceOrganid());
        List<NodesettingTree> parentNode = nodesettingService.getNodeByParentId(pid.trim(),false,null);
        String sqlVersion = DBCompatible.getDBVersion();
        String querysql = "";
        if ("mysql".equals(sqlVersion) || "oracle".equals(sqlVersion)) {
            querysql = "select nodeid,nodename,leaf,organid,classid from tb_data_node where parentnodeid = '"
                    + (pid == null ? "" : pid) + "'order by sortsequence";
        } else {
            querysql = "select cast(nodeid as char(36) ) as nodeid,cast(nodename as varchar(150) ) as nodename ,cast(leaf as char(1) ) as leaf,cast(organid as char(36) ) as organid,cast(classid as char(36) ) as classid from tb_data_node where parentnodeid = '"
                    + (pid == null ? "" : pid) + "'order by sortsequence";
        }
        Query query = entityManager.createNativeQuery(querysql);
        List<Object[]> nodeList = query.getResultList();
        List<Object[]> replaceNodeList = new ArrayList<>();
        String classid = "";
        for (int i = 0; i < nodeList.size(); i++){
            if((nodeList.get(i)[4])!=null) {               //判断是否有classid 有则储存
                classid = (String)nodeList.get(i)[4];
            }
            if(nodeList.get(i)[3]!=null){                 //判断是否是有效节点
                if(((String)nodeList.get(i)[3]).trim().equals(userDetails.getReplaceOrganid())){
                    replaceNodeList.add(nodeList.get(i));
                }
                if (childNode.size()==0){                       //判断是否是最底层节点
                    if ("mysql".equals(sqlVersion) || "oracle".equals(sqlVersion)) {
                        querysql = "select nodeid,nodename,leaf,organid from tb_data_node where organid = '"
                                + (userDetails.getReplaceOrganid() == null ? "" : userDetails.getReplaceOrganid()) + "'and classid = '"+(classid == null ? "" : classid) +"' order by sortsequence";
                    } else {
                        querysql = "select cast(nodeid as char(36) ) as nodeid,cast(nodename as varchar(150) ) as nodename ,cast(leaf as char(1) ) as leaf,cast(organid as char(36) ) as organid from tb_data_node where organid = '"
                                + (userDetails.getReplaceOrganid() == null ? "" : userDetails.getReplaceOrganid()) + "'and classid = '"+(classid == null ? "" : classid) +"' order by sortsequence";
                    }
                    query = entityManager.createNativeQuery(querysql);
                    List<Object[]> checkNodeList = query.getResultList();
                    System.err.println(checkNodeList);
                    nodeList = checkNodeList;
                    break;
                }
                if(parentNode.size()==1){                      //如果是底层的父类层加载底层
                    break;
                }
                if(childNode.size()!=0 && !userOrgan.getParentid().trim().equals("0")){     //判断是否是既有底层又有父层节点
                    if ("mysql".equals(sqlVersion) || "oracle".equals(sqlVersion)) {
                        querysql = "select nodeid,nodename,leaf,organid from tb_data_node where organid = '"
                                + (userDetails.getReplaceOrganid() == null ? "" : userDetails.getReplaceOrganid()) + "'and classid = '"+(classid == null ? "" : classid) +"' order by sortsequence";
                    } else {
                        querysql = "select cast(nodeid as char(36) ) as nodeid,cast(nodename as varchar(150) ) as nodename ,cast(leaf as char(1) ) as leaf,cast(organid as char(36) ) as organid from tb_data_node where organid = '"
                                + (userDetails.getReplaceOrganid() == null ? "" : userDetails.getReplaceOrganid()) + "'and classid = '"+(classid == null ? "" : classid) +"' order by sortsequence";
                    }
                    query = entityManager.createNativeQuery(querysql);
                    List<Object[]> checkNodeList = query.getResultList();
                    replaceNodeList = checkNodeList;
                    break;
                }
            }
        }
        if(replaceNodeList.size() > 0){
            return replaceNodeList;
        }
        return nodeList;
    }

    /**
     * 查找节点配置的模板设置
     *
     * @param nodeid 节点ID
     * @return
     */
    public List findTemplates(String nodeid) {
        /*String querysql = "select fieldcode,fieldname,fieldtable from tb_data_template where nodeid = '"
                + nodeid + "' order by fsequence";
        Query query = entityManager.createNativeQuery(querysql);*/
        return templateRepository.findByNodeidOrderByFsequence(nodeid);
    }

    public List<Map<String, String>> getTempField(String nodeid, boolean isEntryStorage) {
        List<Tb_data_template> templates = templateRepository.findByNodeidOrderByFsequence(nodeid);
        List<Map<String, String>> resList = new ArrayList<Map<String, String>>();
        if (isEntryStorage == true) {
            Map<String, String> map = new HashMap<>();
            map.put("存储位置", "entrystorage");
            resList.add(map);
        }
        for (Tb_data_template template : templates) {
            Map<String, String> map = new HashMap<>();
            map.put(template.getFieldname(), template.getFieldcode());
            resList.add(map);
        }
        return resList;
    }

    /**
     * 获取上传文件的数据格式,并进行存储或者解压处理
     * 返回预览数据
     *
     * @param source 上传的数据文件
     * @return resMap 预览数据
     */
    public Map<String, Object> getDataStruct(MultipartFile source) {
        String fileSub = "";
        int rowCount = 0;
        Map<String, Object> resMap = new HashMap<String, Object>();
        SimpleDateFormat smf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String[] subFileName = source.getOriginalFilename().split("\\\\");
        String fileName = source.getOriginalFilename();
        if (subFileName.length > 1) {
            fileName = subFileName[subFileName.length - 1];
        }
        String fileTransferPath = rootpath + "/OAFile" + "/upload/" + smf.format(date) + "/" + fileName;
        File tmpFile = new File(fileTransferPath);
        //1.文件解压路径
        String UnZipPath = rootpath + "/OAFile/" + "zip解压目录/" + smf.format(date) + "/" +
                source.getOriginalFilename().substring(0, source.getOriginalFilename().lastIndexOf("."));
        if (tmpFile.exists()) {//检查文件或目录是否存在  存在就删除
            tmpFile.delete();
        }
        if (!(new File(rootpath + "/OAFile" + "/upload/" + smf.format(date) + "/")).exists()) {
            (new File(rootpath + "/OAFile" + "/upload/" + smf.format(date) + "/")).mkdirs();
        }
        try {
            source.transferTo(tmpFile);
            //数据后缀名判断
            if (source.getOriginalFilename().endsWith(".xls") || source.getOriginalFilename().endsWith(".xlsx")) {//excel格式
                fileSub = "Excel";
                resMap = ReadExcel.readAllexcelDate(fileTransferPath);
                File file = new File(rootpath + File.separatorChar + source.getOriginalFilename());
                resMap.put("fileTransferPath", fileTransferPath);

            } else if (source.getOriginalFilename().endsWith(".zip")) {//zip格式 --返回预览数据
                //2.解压文件
                ZipUtils.deCompress(fileTransferPath, UnZipPath, "UTF-8");//解压文件
                // 1.获取解压后的条目文件
                File file = new File(UnZipPath);
                File[] tempList = file.listFiles();
                //判断是否乱码
                boolean b = FileUtil.isMessyCode(tempList);
                if (b) {
                    //删除原先的解压目录
                    FileUtil.delFolder(UnZipPath);
                    //更换编码进行解压
                    ZipUtils.deCompress(fileTransferPath, UnZipPath, "GBK");//解压文件
                    //重新获取文件
                    file = new File(UnZipPath);
                    tempList = file.listFiles();
                }
                List<String> fileList = new ArrayList<>();
                int zipCount = tempList.length;
                for (File f : tempList) {
                    if (f.toString().lastIndexOf(".zip") > 1) {
                        zipCount--;
                    }
                }
                if (zipCount == 0) {//社保中心的数据包
                    Map<String, Object> newMap = new HashMap<>();
                    //存放解析后的value
                    List lists = new ArrayList<>();
                    //存放解析后的element的那么 用作模板字段
                    List codeLists = new ArrayList<>();
                    //记录wg11 电子文件id
                    String wg11 = "";
                    //1.拿到所有包 进行解压
                    for (File f : tempList) {
                        File newFile = null;
                        File[] newTempList = null;
                        String fUnzipPath = f.toString().substring(0, f.toString().lastIndexOf("."));
                        ZipUtils.deCompress(f.toString(), fUnzipPath, "UTF-8");//解压文件
                        //判断解压后的文件是否乱码
                        boolean fMessyCode = FileUtil.isMessyCode(new File(fUnzipPath).listFiles());
                        if (fMessyCode) {
                            //删除原先的解压目录
                            FileUtil.delFolder(fUnzipPath);
                            //更换编码进行解压
                            ZipUtils.deCompress(f.toString(), fUnzipPath, "GBK");//解压文件
                        }
                        //重新获取文件
                        newFile = new File(fUnzipPath);
                        newTempList = newFile.listFiles();
                        //拿到最后一层包中的文件（包含1.条目xml 2.yjrz.xml 3.条目对应的原文）
                        for (File newfile : newTempList) {
                            if (newfile.getName().indexOf("yjrz") == -1 && newfile.isFile()) {//判断 文件不是2 并且是文件
                                if (rowCount < 10) {//只返回最多10条预览记录
                                    //对条目文件进行解析 保存预览数据
                                    newMap = XmlUtil.readsocialSecurityXml(newfile.toString());
                                    lists.addAll((List) newMap.get("data"));
                                    //element的name 相同 只用保存1组
                                    codeLists = ((List) newMap.get("header"));
                                    wg11 = String.valueOf(newMap.get("wg11Index"));
                                }
                                rowCount++;
                            }
                        }
                    }
                    fileSub = "Xml";
                    resMap.put("data", lists);
                    resMap.put("rowCount", rowCount);
                    resMap.put("wg11Index", wg11);
                    resMap.put("header", codeLists);
                    resMap.put("success", true);

                } else {
                    if (tempList[0].isFile()) {//判断包内是文件还是文件夹（bs xml的判断）
                        fileList.add(tempList[0].toString());
                    } else {
                        fileList = FileUtil.getFile(tempList[0].getPath());//条目文件集合
                    }
                    if (fileList.get(0).endsWith(".xlsx") || fileList.get(0).endsWith(".xls")) {//zip中的excle
                        fileSub = "Excel";
                        resMap = ReadExcel.readAllexcelDate(fileList.get(0));
                    } else if (fileList.get(0).endsWith(".xml")) {             //-------zip中的xml
                        fileSub = "Xml";
                        resMap = XmlUtil.readDateXlsx(fileList.get(0));
                        rowCount = (int) resMap.get("rowCount");
                        resMap.put("rowCount", rowCount);
                    }
                }
                resMap.put("UnZipPath", UnZipPath);
            } else if (source.getOriginalFilename().endsWith(".xml")) {//xml格式
                fileSub = "Xml";
                resMap = XmlUtil.readDateXlsx(fileTransferPath);
                resMap.put("fileTransferPath", fileTransferPath);
            }
            //FileUtil.delFolder(rootpath + "/OAFile" + "/upload/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        resMap.put("fileSub", fileSub);
        return resMap;
    }

    /**
     * 解析文件---插入数据库
     * <p>
     * 1.根据返回的keymapList 分析出需要导入值的字段（源字段） 与对应的excel中的value
     * 2.读取excel时根据keymapList中的顺序号来存入值
     * 3.根据keymapList中的源字段+excelReadValue 组成成index 和detali 对象 然后插入数据库
     *
     * @param filename        文件名,包含后缀名
     * @param target          目标节点id
     * @param keymapList      选择的字段集合--格式 "字段顺序,源字段,目标字段"--集合长度=已设置目标字段数
     * @param isEntityStorage
     */
    public Map<String, Object> importDate(String filename, String filePath, String target,
                                          List<String> keymapList, String isRepeat, boolean isEntityStorage, boolean autoCreateArchivecode) throws Exception {
        long num = 0;
        long erro = 0;
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String importState = "";//用作记录zip包中的条目文件类型
        Map<String, Object> map = new HashMap<String, Object>();
        List<Tb_entry_index> tb_entry_indexList = new ArrayList<>();
        List<Tb_entry_detail> entry_details = new ArrayList<>();
        //源字段模板
        List<String> fieldnames = new ArrayList<>();//用户设置的字段fieldname集合
        List<Integer> fieldSequence = new ArrayList<>();//顺序号
        List<String> names = new ArrayList<>();//源字段名
        //拆分keymapList
        for (int i = 0; i < keymapList.size(); i++) {
            fieldnames.add(keymapList.get(i).split(",")[2]);
            fieldSequence.add(Integer.parseInt(keymapList.get(i).split(",")[0]));
            names.add(keymapList.get(i).split(",")[1]);
        }

        String[] strings = fieldnames.toArray(new String[fieldnames.size()]);//设置的目标字段数组--需要设置为改字段
        String[] namesString = names.toArray(new String[names.size()]);//-源字段名
        //根据源字段名得到源字段code--根据code 插入值到数据库
        List<String> list = getFieldCodeByFieldName(target, strings);
        String[] fieldCodeStr = list.toArray(new String[list.size()]);
        String[] strcode;
        String[] strname;
        if (isEntityStorage) {//实体档案入库导入需要多一个存储位置字段
            strcode = new String[fieldCodeStr.length + 2];
            strname = new String[namesString.length + 2];
            strname[strname.length - 1] = "条目ID";
            strcode[strcode.length - 1] = "entryid";
            strname[strname.length - 2] = "存储位置";
            strcode[strcode.length - 2] = "entrystorage";
            //strcode[0] = "entrystorage";
            System.arraycopy(fieldCodeStr, 0, strcode, 0, fieldCodeStr.length);
            System.arraycopy(namesString, 0, strname, 0, namesString.length);
        } else {
            strcode = new String[fieldCodeStr.length + 1];
            strname = new String[namesString.length + 1];
            strname[strname.length - 1] = "条目ID";
            strcode[strcode.length - 1] = "entryid";
            System.arraycopy(fieldCodeStr, 0, strcode, 0, fieldCodeStr.length);
            System.arraycopy(namesString, 0, strname, 0, namesString.length);
        }
        map.put("sname", strname);
        map.put("scode", strcode);
        //根据条目文件类型进行解析
        Map<String, Integer> reMap = null;
        List<String> eleFolder = new ArrayList<>();
        String imporFailureState = "";
        if (filename.endsWith(".xml")) {
            imporFailureState = "Xml";
            reMap = readXml(filePath, fieldSequence, strcode, target, isRepeat, eleFolder, isEntityStorage, autoCreateArchivecode);
            num = reMap.get("num");
            erro = reMap.get("erro");
        } else if (filename.endsWith(".xls") || filename.endsWith(".xlsx")) {//excel
            int lastRows = 0;
            if (filename.endsWith(".xlsx")) {
                importState = "xlsx";
                imporFailureState = "Excel";
                Workbook workbook = StreamingReader.builder()
                        .rowCacheSize(100)  //缓存到内存中的行数，默认是10
                        .bufferSize(4096)  //读取资源时，缓存到内存的字节大小，默认是1024
                        .open(new FileInputStream(new File(filePath)));  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件

                for (Sheet sheet : workbook) {
                    for (Row row : sheet) {
                        lastRows++;
                    }
                }
            } else {
                importState = "xls";
                imporFailureState = "Excel";
                Workbook workbook = WorkbookFactory.create(new FileInputStream(new File(filePath)));
                for (Sheet sheet : workbook) {
                    for (Row row : sheet) {
                        lastRows++;
                    }
                }
            }
            reMap = readExcel(lastRows, filePath, fieldSequence, strcode, target, isRepeat, eleFolder, importState, isEntityStorage, autoCreateArchivecode);//解析excle 并插入数据库
            num = reMap.get("num");
            erro = reMap.get("erro");
        } else if (filename.endsWith(".zip")) { //-zip
            // 1.获取解压后的条目文件
            String UnZipPath = rootpath + "/OAFile/" + "zip解压目录/" +
                    filename.substring(0, filename.lastIndexOf(".")) + "/";
            File file = new File(filePath);
            File[] tempList = file.listFiles();
            List<String> fileList = FileUtil.getFile(tempList[0].getPath());
            // 2.获取原文目录
            eleFolder = FileUtil.getFolder(tempList[0].getPath() + "/document");
            if (fileList.get(0).endsWith(".xls") || fileList.get(0).endsWith(".xlsx")) { //- zip中的excel
                InputStream in = new FileInputStream(new File(fileList.get(0)));
                int lastRows = 0;
                if (fileList.get(0).endsWith(".xlsx")) {
                    importState = "xlsx";
                    imporFailureState = "ZipExcel";
                    Workbook workbook = StreamingReader.builder()
                            .rowCacheSize(1000)  //缓存到内存中的行数，默认是10
                            .bufferSize(40960)  //读取资源时，缓存到内存的字节大小，默认是1024
                            .open(in);  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件

                    for (Sheet sheet : workbook) {
                        for (Row row : sheet) {
                            lastRows++;
                        }
                    }
                } else {
                    importState = "xls";
                    imporFailureState = "ZipExcel";
                    Workbook workbook = WorkbookFactory.create(in);
                    for (Sheet sheet : workbook) {
                        for (Row row : sheet) {
                            lastRows++;
                        }
                    }
                }
                in.close();
                reMap = readExcel(lastRows, fileList.get(0), fieldSequence, strcode, target, isRepeat, eleFolder, importState, isEntityStorage, autoCreateArchivecode);//解析excle 并插入数据库
                num = reMap.get("num");
                erro = reMap.get("erro");
            } else if (fileList.get(0).endsWith(".xml")) {
                imporFailureState = "ZipXml";
                reMap = readXml(fileList.get(0), fieldSequence, strcode, target, isRepeat, eleFolder, isEntityStorage, autoCreateArchivecode);
                num = reMap.get("num");
                erro = reMap.get("erro");
            }
        }
        //生成导入失败文件
        if (erro > 0) {
            if (IMPORT_STYPE_EXCEL.equals(imporFailureState)) {
                if (isEntityStorage) {//实体档案入库导入
                    CreateExcel.createErroExcel("导入失败-" + filename.substring(0, filename.lastIndexOf(".")), entryList, new String[]{"archivecode"}, new String[]{"异常信息"});
                }else{
                    CreateExcel.createErroExcel("导入失败-" + filename.substring(0, filename.lastIndexOf(".")), entryList, strcode, namesString);
                }
            } else if (IMPORT_STYPE_XML.equals(imporFailureState)) {
                XmlUtil.CreateFailureXml(strname, strcode, entryList, "导入失败-" + filename.substring(0, filename.lastIndexOf(".")));
            } else if (IMPORT_STYPE_ZIP_EXCEL.equals(imporFailureState)) {
                createErroFile(strname, strcode, "导入失败-" + filename.substring(0, filename.lastIndexOf(".")), eleFolder, "Excel");
            } else if (IMPORT_STYPE_ZIP_XML.equals(imporFailureState)) {
                createErroFile(strname, strcode, "导入失败-" + filename.substring(0, filename.lastIndexOf(".")), eleFolder, "Xml");
            }
            RepetitionIndex.removeAll(RepetitionIndex);
            RepetitionEntryid.removeAll(RepetitionEntryid);
            entryList.removeAll(entryList);
        }
        //删除文件
        FileUtil.delFolder(filePath);
        map.put("num", num);
        map.put("error", erro);
        //此处进行保存导入记录
        Tb_imp_record record = new Tb_imp_record();
        record.setImpuser(userDetails.getUsername());
        record.setSuccesscount(String.valueOf(num));
        record.setDefeatedcount(String.valueOf(erro));
        record.setImptime(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        record.setImptype("管理");
        impRecord.save(record);
        return map;
    }


    /**
     * 解析文件---插入数据库的临时表  库房
     * <p>
     * 1.根据返回的keymapList 分析出需要导入值的字段（源字段） 与对应的excel中的value
     * 2.读取excel时根据keymapList中的顺序号来存入值
     * 3.根据keymapList中的源字段+excelReadValue tb_entry_index_temp 对象 然后插入数据库
     *
     * @param filename        文件名,包含后缀名
     * @param target          目标节点id
     * @param keymapList      选择的字段集合--格式 "字段顺序,源字段,目标字段"--集合长度=已设置目标字段数
     */
    public Map<String, Object> importKfDate(String filename, String filePath, String target,
                                          List<String> keymapList, String isRepeat) throws Exception {
        long num = 0;
        long erro = 0;
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String importState = "";//用作记录zip包中的条目文件类型
        Map<String, Object> map = new HashMap<String, Object>();
        List<Tb_entry_index> tb_entry_indexList = new ArrayList<>();
        List<Tb_entry_detail> entry_details = new ArrayList<>();
        //源字段模板
        List<String> fieldnames = new ArrayList<>();//用户设置的字段fieldname集合
        List<Integer> fieldSequence = new ArrayList<>();//顺序号
        List<String> names = new ArrayList<>();//源字段名
        //拆分keymapList
        for (int i = 0; i < keymapList.size(); i++) {
            //fieldnames.add(keymapList.get(i).split(",")[2]);
            fieldSequence.add(Integer.parseInt(keymapList.get(i).split(",")[0]));
            names.add(keymapList.get(i).split(",")[1]);
        }

        //String[] strings = fieldnames.toArray(new String[fieldnames.size()]);//设置的目标字段数组--需要设置为改字段
        String[] namesString = names.toArray(new String[names.size()]);//-源字段名
        //根据源字段名得到源字段code--根据code 插入值到数据库
        //List<String> list = getFieldCodeByFieldName(target, strings);
        List<String> list = getKfFieldCodeByFieldName(names);//获取源字段对应的fieldcode
        String[] fieldCodeStr = list.toArray(new String[list.size()]);
        String[] strcode;
        String[] strname;
        strcode = new String[fieldCodeStr.length];
        strname = new String[namesString.length];
        /*strname[strname.length - 1] = "条目ID";
        strcode[strcode.length - 1] = "entryid";
        strname[strname.length - 2] = "存储位置";
        strcode[strcode.length - 2] = "entrystorage";*/
        //strcode[0] = "entrystorage";
        System.arraycopy(fieldCodeStr, 0, strcode, 0, fieldCodeStr.length);
        System.arraycopy(namesString, 0, strname, 0, namesString.length);
        map.put("sname", strname);
        map.put("scode", strcode);
        //根据条目文件类型进行解析
        Map<String, Integer> reMap = null;
        List<String> eleFolder = new ArrayList<>();
        String imporFailureState = "";
        if (filename.endsWith(".xls") || filename.endsWith(".xlsx")) {//excel
            int lastRows = 0;
            if (filename.endsWith(".xlsx")) {
                importState = "xlsx";
                imporFailureState = "Excel";
                Workbook workbook = StreamingReader.builder()
                        .rowCacheSize(100)  //缓存到内存中的行数，默认是10
                        .bufferSize(4096)  //读取资源时，缓存到内存的字节大小，默认是1024
                        .open(new FileInputStream(new File(filePath)));  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件

                for (Sheet sheet : workbook) {
                    for (Row row : sheet) {
                        lastRows++;
                    }
                }
            } else {
                importState = "xls";
                imporFailureState = "Excel";
                Workbook workbook = WorkbookFactory.create(new FileInputStream(new File(filePath)));
                for (Sheet sheet : workbook) {
                    for (Row row : sheet) {
                        lastRows++;
                    }
                }
            }
            reMap = readKfExcel(lastRows, filePath, fieldSequence, strcode, target, isRepeat, eleFolder, importState);//解析excle 并插入数据库
            num = reMap.get("num");
            erro = reMap.get("erro");
        }
        //生成导入失败文件
        if (erro > 0) {
            if (IMPORT_STYPE_EXCEL.equals(imporFailureState)) {
                CreateExcel.createErroExcel("导入失败-" + filename.substring(0, filename.lastIndexOf(".")), entryList, new String[]{"archivecode"}, new String[]{"异常信息"});
            }
            RepetitionIndex.removeAll(RepetitionIndex);
            RepetitionEntryid.removeAll(RepetitionEntryid);
            entryList.removeAll(entryList);
        }
        //删除文件
        FileUtil.delFolder(filePath);
        map.put("num", num);
        map.put("error", erro);
        //此处进行保存导入记录
        Tb_imp_record record = new Tb_imp_record();
        record.setImpuser(userDetails.getUsername());
        record.setSuccesscount(String.valueOf(num));
        record.setDefeatedcount(String.valueOf(erro));
        record.setImptime(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        record.setImptype("管理");
        impRecord.save(record);
        return map;
    }

    /**
     * 入库匹配校验
     * 情形1 没找到相关条目
     * 情形2 匹配到多条条目
     * 情形3  存储位置不够详细
     * 情形4  放入密集架空间不足
     * 情形5  已入库
     * 情形6  存储位置没有匹配到
     * 情形7 存储位置信息为空
     * 情形8  可以进行入库
     * @param codeSetValues
     * @return
     */
    public ExtMsg kfCheck(String codeSetValues,String uniquetag){
        List<String> indexTempList = entryIndexTempRepository.findEntryidByUniquetag(uniquetag);//库房导入临时表数据
        if (indexTempList .size() == 0) {
            return new ExtMsg(true, "没有需要处理的数据", null);
        }

        String [] fieldcodes=codeSetValues.split(",");//匹配字段组合
        Tb_entry_index_temp temp;
        List<String> resultList;//匹配条件查询结果集
        String sql="select entryid from tb_entry_index where 1=1 ";
        String value;//字段值
        for(String entryid:indexTempList){//根据设置的匹配字段，判断每一条临时表数据的入库可行性
            temp=entryIndexTempRepository.findOne(entryid);
            String sqlSub="";//sql拼接语句
            //1.查询条目匹配
            for(int i=0;i<fieldcodes.length;i++){//暂时只考虑varchar类型
                value= GainField.getFieldValueByName(fieldcodes[i], temp)!= null
                        ? (String) GainField.getFieldValueByName(fieldcodes[i], temp) : null;
                if(value==null){
                    sqlSub+=" and "+fieldcodes[i]+" is null ";
                }else{
                    sqlSub+=" and "+fieldcodes[i]+" = '"+value+"' ";
                }
            }
            Query query = entityManager.createNativeQuery(sql+sqlSub);
            query.setFirstResult(0);
            query.setMaxResults(3);
            resultList = query.getResultList();
            if(resultList.size()==0){//情形1 没找到相关条目
                temp.setSparefield5("1");
            }if(resultList.size()>0){//情形2 匹配到多条条目
                temp.setSparefield5("2");
            }

            //2.查询库存位置匹配
            if(resultList.size()==1){//只有一条匹配记录时才进行库存位置查询
                checkTempStorageMsg(temp,resultList.get(0));
            }
            //entryIndexTempRepository.updateSparefield5(temp.getSparefield5(),temp.getEntryid());
        }
        return new ExtMsg(true, "匹配字段成功", null);
    }

    //查询库存位置匹配
    private void checkTempStorageMsg(Tb_entry_index_temp temp, String indexEntryid){
        String entryStorage=temp.getEntrystorage();
        if (StringUtils.isNotBlank(entryStorage)) {
            //String[] entryStorages = entryStorage.split("-");
            String[] entryStorages = entryStorage.split("_");//改用下划线
            if (entryStorages.length < ImportService.ENTRY_STORAGE_LENGTH) {//有设置 3-1库房时 ，会大于8
                temp.setSparefield5("3"); //情形3  存储位置不够详细
                return;
            }
            String city = entryStorages[0];//城市
            String unit = entryStorages[1];//单位
            String room = entryStorages[2];//库房
            String zone = entryStorages[3];//架区
            String column=entryStorages[4];//列
            String section=entryStorages[5];//节
            String layer=entryStorages[6];//层
            String side=entryStorages[7];//面

            //为了防止同时输入了对应存储位置名称，所以如果有输入了就把它去掉
            if (column.contains("列")) {
                column = column.substring(0, column.indexOf("列"));
            }
            if (section.contains("节")) {
                section = section.substring(0, section.indexOf("节"));
            }
            if (layer.contains("层")) {
                layer = layer.substring(0, layer.indexOf("层"));
            }
            if (!side.contains("面")) {
                side = side + "面";
            }
            column = String.format("%02d", Integer.valueOf(column));//列
            section = String.format("%02d", Integer.valueOf(section ));//节
            layer = String.format("%02d", Integer.valueOf(layer));//层
            String zoneId = zonesRepository.findZondId(city, unit, room, zone);
            ZoneShelves zoneShelves = null;
            if (zoneId != null) {
                zoneShelves = zoneShelvesRepository.findZoneSheleves(column, section, layer, side, zoneId);

            }
            if (zoneShelves != null) {
                if (zoneShelves.getCapacity() <= zoneShelves.getUsecapacity()) {
                    temp.setSparefield5("4");//情形4  放入密集架空间不足
                }else{
                    //根据archivecode查询条目是否已经在库
                    try{
                        Storage storage = storageRepository.findByEntry(indexEntryid);
                        if(storage==null){
                            temp.setSparefield5("8");//情形8  可以进行入库
                            temp.setSparefield4(indexEntryid);//存储对应的TB_ENTRY_INDEX条目id
                            temp.setSparefield3(zoneShelves.getShid());//存储对应的库房zoneshelves
                        }else{
                            temp.setSparefield5("5");//情形5  已入库
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        temp.setSparefield5("5");//情形5  已入库
                    }
                }
            } else {
                temp.setSparefield5("6");//情形6  存储位置没有匹配到
            }
        } else {//没有存储位置
            temp.setSparefield5("7");//情形7 存储位置信息为空
        }
        //entryIndexTempRepository.updateSparefield5(temp.getSparefield5(),temp.getEntryid());
    }

    //设置匹配结果返回信息
    public ExtMsg setReturnMsg(ExtMsg newMsg,List<Tb_entry_index_temp>indexTempList,String uniquetag){
        List<String> allTemp=entryIndexTempRepository.findEntryidByUniquetag(uniquetag);//所有的临时入库数据
        int faileNum=indexTempList.size();//没匹配上的条目数
        int successNum=allTemp.size()-faileNum;//可入库的条目数
        int num1=0;//没找到相关条目
        int num2=0;//匹配到多条条目
        int num3=0;//存储位置不够详细
        int num4=0;//放入密集架空间不足
        int num5=0;//已入库
        int num6=0;//存储位置没有匹配到
        int num7=0;//存储位置信息为空

        String state="";
        for(Tb_entry_index_temp temp:indexTempList){
            state=temp.getSparefield5();
            if("1".equals(state)){
                num1++;
            }else if("2".equals(state)){
                num2++;
            }else if("3".equals(state)){
                num3++;
            }else if("4".equals(state)){
                num4++;
            }else if("5".equals(state)){
                num5++;
            }else if("6".equals(state)){
                num6++;
            }else if("7".equals(state)){
                num7++;
            }
        }

        String  successNumStr="",faileNumStr="",num1Str="",num2Str="",num3Str="",num4Str="",num5Str="",num6Str="",num7Str="";
        if(successNum<0){
            successNum=0;//处理负值
        }
        successNumStr="可入库的条目数 "+successNum;
        if(faileNum>0){
            faileNumStr=", 不可入库的条目总数 "+faileNum;
        }
        if(num1>0){
            num1Str=", 没找到相关条目 "+num1;
        }
        if(num2>0){
            num2Str=", 匹配到多条条目 "+num2;
        }
        if(num3>0){
            num3Str=", 存储位置不够详细 "+num3;
        }
        if(num4>0){
            num4Str=", 放入密集架空间不足 "+num4;
        }
        if(num5>0){
            num5Str=", 已入库 "+num5;
        }
        if(num6>0){
            num6Str=", 存储位置没有匹配到 "+num6;
        }
        if(num7>0){
            num7Str=", 存储位置信息为空 "+num7;
        }
        newMsg.setMsg(successNumStr+faileNumStr+num1Str+num2Str+num3Str+num4Str+num5Str+num6Str+num7Str);
        return newMsg;
    }

    //执行入库
    public ExtMsg importCheck(){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uniquetag = BatchModifyService.getUniquetagByType("kfdr");//库房导入
        List<String> indexTempList = entryIndexTempRepository.findEntryidByUniquetagAndSparefield5(uniquetag,"8");//库房导入临时表数据 可入库
        if (indexTempList .size() == 0) {
            return new ExtMsg(true, "没有需要处理的数据", null);
        }

        InWare iw = new InWare();
        iw.setWaretype("新增入库");
        iw.setWareuser(userDetails.getRealname());
        Tb_entry_index_temp temp;
        for(String entryid:indexTempList){
            temp=entryIndexTempRepository.findOne(entryid);
            //对临时条目相关数据进行入库操作
            String ygEntryid=temp.getSparefield4();//已归管理表 条目id
            String shid=temp.getSparefield3();//对应的存储单元格 id
            ZoneShelves zs = zoneShelvesRepository.findByShid(shid);
            if (iw.getStorages() == null) {
                iw.setStorages(new HashSet<Storage>());
            }
            Storage st = new Storage();
            st.setEntry(ygEntryid);
            st.setZoneShelves(zs);
            st.setStorestatus(Storage.STATUS_IN);
            iw.getStorages().add(st);
            Tb_entry_index index=entryIndexRepository.findOne(ygEntryid);
            index.setEntrystorage(temp.getEntrystorage());
            index.setZoneShelves(shid);
        }
        if (iw.getStorages() != null && iw.getStorages().size() > 0) {
            inWareService.save(iw);
        }
        //入库后删除相关临时条目
        entryIndexTempRepository.deleteByEntryidIn(indexTempList.toArray(new String[indexTempList.size()]));
        return new ExtMsg(true, "执行入库成功", null);
    }

    /**
     * 数据采集或目录接收
     * 解析文件---插入数据库
     * <p>
     * 1.根据返回的keymapList 分析出需要导入值的字段（源字段） 与对应的excel中的value
     * 2.读取excel时根据keymapList中的顺序号来存入值
     * 3.根据keymapList中的源字段+excelReadValue 组成成index 和detali 对象 然后插入数据库
     *
     * @param filename   文件名,包含后缀名
     * @param target     目标节点id
     * @param keymapList 选择的字段数组--格式 "字段顺序,源字段,目标字段"--长度=已设置目标字段数
     */
    @Transactional
    public Map<String, Object> importCaptureData(String filename, String filePath, String target, List<String> keymapList, String isRepeat, String importtype) throws Exception {
        long num = 0;
        long erro = 0;
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String importState = "";//用作记录zip包中的条目文件类型
        Map<String, Object> map = new HashMap<String, Object>();
        //源字段模板
        List<String> fieldnames = new ArrayList<>();//用户设置的字段fieldname集合
        List<Integer> fieldSequence = new ArrayList<>();//顺序号
        List<String> names = new ArrayList<>();//源字段名
        //拆分keymapList
        for (int i = 0; i < keymapList.size(); i++) {
            fieldnames.add(keymapList.get(i).split(",")[2]);
            fieldSequence.add(Integer.parseInt(keymapList.get(i).split(",")[0]));
            names.add(keymapList.get(i).split(",")[1]);
        }

        String[] strings = fieldnames.toArray(new String[fieldnames.size()]);//设置的目标字段数组--需要设置为改字段
        String[] namesString = names.toArray(new String[names.size()]);//-源字段名
        //根据源字段名得到源字段code--根据code 插入值到数据库
        List<String> list = getFieldCodeByFieldName(target, strings);
        String[] fieldCodeStr = list.toArray(new String[list.size()]);
        String[] strcode = new String[fieldCodeStr.length + 1];
        String[] strname = new String[namesString.length + 1];
        strname[strname.length - 1] = "条目ID";
        strcode[strcode.length - 1] = "entryid";
        System.arraycopy(fieldCodeStr, 0, strcode, 0, fieldCodeStr.length);
        System.arraycopy(namesString, 0, strname, 0, namesString.length);
        //根据条目文件类型进行解析
        Map<String, Integer> reMap = null;
        List<String> eleFolder = new ArrayList<>();
        String imporFailureState = "";
        if (filename.endsWith(".xml")) {
            imporFailureState = "Xml";
            reMap = readXmlCapture(filePath, fieldSequence, strcode, target, isRepeat, eleFolder, importtype);
            num = reMap.get("num");
            erro = reMap.get("erro");
        } else if (filename.endsWith(".xls") || filename.endsWith(".xlsx")) {//excel
            int lastRows = 0;
            if (filename.endsWith(".xlsx")) {
                importState = "xlsx";
                imporFailureState = "Excel";
                Workbook workbook = StreamingReader.builder()
                        .rowCacheSize(100)  //缓存到内存中的行数，默认是10
                        .bufferSize(4096)  //读取资源时，缓存到内存的字节大小，默认是1024
                        .open(new FileInputStream(new File(filePath)));  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件

                for (Sheet sheet : workbook) {
                    for (Row row : sheet) {
                        lastRows++;
                    }
                }
            } else {
                importState = "xls";
                imporFailureState = "Excel";
                Workbook workbook = WorkbookFactory.create(new FileInputStream(new File(filePath)));
                for (Sheet sheet : workbook) {
                    for (Row row : sheet) {
                        lastRows++;
                    }
                }
            }
            //String impState = filename.substring(filename.lastIndexOf("."));
            reMap = readExcelCapture(lastRows, filePath, fieldSequence, strcode, target, isRepeat, eleFolder, importState, importtype);//解析excle 并插入数据库
            num = reMap.get("num");
            erro = reMap.get("erro");
        } else if (filename.endsWith(".zip")) { //-zip
            // 1.获取解压后的条目文件
            String UnZipPath = rootpath + "/OAFile/" + "zip解压目录/" +
                    filename.substring(0, filename.lastIndexOf(".")) + "/";
            File file = new File(filePath);
            File[] tempList = file.listFiles();
            List<String> fileList = FileUtil.getFile(tempList[0].getPath());
            // 2.获取原文目录
            eleFolder = FileUtil.getFolder(tempList[0].getPath() + "/document");
            if (fileList.get(0).endsWith(".xls") || fileList.get(0).endsWith(".xlsx")) { //- zip中的excel
                int lastRows = 0;
                if (fileList.get(0).endsWith(".xlsx")) {
                    importState = "xlsx";
                    imporFailureState = "ZipExcel";
                    Workbook workbook = StreamingReader.builder()
                            .rowCacheSize(1000)  //缓存到内存中的行数，默认是10
                            .bufferSize(40960)  //读取资源时，缓存到内存的字节大小，默认是1024
                            .open(new FileInputStream(new File(fileList.get(0))));  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件

                    for (Sheet sheet : workbook) {
                        for (Row row : sheet) {
                            lastRows++;
                        }
                    }
                } else {
                    importState = "xls";
                    imporFailureState = "ZipExcel";
                    Workbook workbook = WorkbookFactory.create(new FileInputStream(new File(fileList.get(0))));
                    for (Sheet sheet : workbook) {
                        for (Row row : sheet) {
                            lastRows++;
                        }
                    }
                }
                reMap = readExcelCapture(lastRows, fileList.get(0), fieldSequence, strcode, target, isRepeat, eleFolder, importState, importtype);//解析excle 并插入数据库
                num = reMap.get("num");
                erro = reMap.get("erro");
            } else if (fileList.get(0).endsWith(".xml")) {
                imporFailureState = "ZipXml";
                reMap = readXmlCapture(fileList.get(0), fieldSequence, strcode, target, isRepeat, eleFolder, importtype);
                num = reMap.get("num");
                erro = reMap.get("erro");
            }
        }
        //生成导入失败文件
        if (erro > 0) {
            if (IMPORT_STYPE_EXCEL.equals(imporFailureState)) {
                CreateExcel.captureCreateErroExcel("导入失败-" + filename.substring(0, filename.lastIndexOf(".")), captureEntryList, strcode, namesString, acceptEntryList, importtype);
            } else if (IMPORT_STYPE_XML.equals(imporFailureState)) {
                XmlUtil.captureCreateFailureXml(strname, strcode, captureEntryList, "导入失败-" + filename.substring(0, filename.lastIndexOf(".")), acceptEntryList, importtype);
            } else if (IMPORT_STYPE_ZIP_EXCEL.equals(imporFailureState)) {
                capturecreateErroFile(strname, strcode, "导入失败-" + filename.substring(0, filename.lastIndexOf(".")), eleFolder, "Excel", importtype);
            } else if (IMPORT_STYPE_ZIP_XML.equals(imporFailureState)) {
                capturecreateErroFile(strname, strcode, "导入失败-" + filename.substring(0, filename.lastIndexOf(".")), eleFolder, "Xml", importtype);
            }
            RepetitionIndex.removeAll(RepetitionIndex);
            RepetitionEntryid.removeAll(RepetitionEntryid);
            captureEntryList.removeAll(captureEntryList);
            acceptEntryList.removeAll(acceptEntryList);
        }
        //删除文件
        FileUtil.delFolder(filePath);
        map.put("num", num);
        map.put("error", erro);
        Tb_imp_record record = new Tb_imp_record();
        record.setImpuser(userDetails.getUsername());
        record.setSuccesscount(String.valueOf(num));
        record.setDefeatedcount(String.valueOf(erro));
        record.setImptime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        if (importtype != null && "accept".equals(importtype)) {
            record.setImptype("目录接收");
        } else {
            record.setImptype("采集");
        }
        impRecord.save(record);
        return map;
    }


    /**
     * 根据字段名获取它对应顺序的code值  返回集合
     *
     * @param nodeid     目标节点id
     * @param fieldNames 字段名数组
     * @return 与fieldnames 对应fieldcode
     */
    public List<String> getFieldCodeByFieldName(String nodeid, String[] fieldNames) {
        List<Tb_data_template> templates = templateRepository.findByNodeidOrderByFsequence(nodeid);
        List<String> list = Arrays.asList(fieldNames);
        List<String> returnList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {//循环用户设置的字段名--与完整的字段比对
            for (int j = 0; j < templates.size(); j++) {
                if (list.get(i).equals(templates.get(j).getFieldname())) {
                    returnList.add(templates.get(j).getFieldcode());
                }
            }
        }
        return returnList;
    }

    /**
     * 根据字段名获取它对应顺序的code值  返回集合  库房导入
     *
     * @return 与fieldnames 对应fieldcode
     */
    public List<String> getKfFieldCodeByFieldName(List<String> fieldNames) {
        List<Tb_data_template> templates = templateRepository.findByNodeidOrderByFsequence("publicNode");//库房导入模板
        List<String> returnList = new ArrayList<>();
        List<String> kfList=new ArrayList<>();//库房位置详细字段集合
        kfList.add("citydisplay");
        kfList.add("unitdisplay");
        kfList.add("roomdisplay");
        kfList.add("zonedisplay");
        kfList.add("sectiondisplay");
        kfList.add("coldisplay");
        kfList.add("layerdisplay");
        kfList.add("sidedisplay");
        returnList.add("entrystorage");//首个字段设置问存储字段
        for (int i = 0; i < fieldNames.size(); i++) {//循环用户设置的字段名--与完整的字段比对
            for (int j = 0; j < templates.size(); j++) {
                if (fieldNames.get(i).equals(templates.get(j).getFieldname())) {
                    if(!kfList.contains(templates.get(j).getFieldcode())){//只放存储字段，不加进库房位置详细字段
                        returnList.add(templates.get(j).getFieldcode());
                    }
                    break;
                }
            }
        }
        return returnList;
    }


    private Map<String, Integer> readExcel(int lastRows, String file, List<Integer> fieldSequence,
                                           String[] strcode, String target, String isRepeat, List<String> eleFolder,
                                           String impState, boolean isEntityStorage, boolean autoCreateArchivecode)
            throws Exception {
        int count = 0;
        Map<String, Integer> reMap = new HashMap<>();
        //---创建workbook对象
        Workbook workbook = null;
        InputStream in = new FileInputStream(new File(file));
        if ("xlsx".equals(impState)) {
            workbook = StreamingReader.builder()
                    .rowCacheSize(100)  //缓存到内存中的行数，默认是10
                    .bufferSize(4096)  //读取资源时，缓存到内存的字节大小，默认是1024
                    .open(in);  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件
        } else {
            //workbook= new HSSFWorkbook(new FileInputStream(new File(file)));
            workbook = WorkbookFactory.create(in);
        }
        in.close();
        //Long statime = System.currentTimeMillis();
        for (Sheet sheet : workbook) {
            if (sheet == null) {
                continue;
            }
            List<Tb_entry_index> indexList = new ArrayList<>();
            List<Tb_entry_detail> details = new ArrayList<>();
            List sheetRowsValue = new ArrayList();
            int rowCount = 0;//记录循环行数
            int bRow = 0;//用来判断--去列头
            int sheetLastRowNum = sheet.getLastRowNum();
            for (Row row : sheet) {
                rowCount++;
                //跳过列头行
                if (bRow == 0) {
                    bRow++;
                    continue;
                }
                List<String> rowCellsValue = new ArrayList<>();
                List<String> archivecodeList=new ArrayList<>();//档号集合，库房导入用
                int maxcol = row.getLastCellNum();
                String field = "";
                for (int col = 0; col < maxcol; col++) {//循环列单元格，获取值
                    Cell cell = row.getCell(col);
                    if(isEntityStorage){//实体库房导入
                        if (cell == null) {
                            rowCellsValue.add("");
                            continue;
                        }
                        if ("xlsx".equals(impState)) {
                            String colvallue="";
                            //取出数组前8位拼接成存储位置
                            if (col+1 < ImportService.ENTRY_STORAGE_LENGTH) {
                                colvallue=ReadExcel.getStreamingCellStringVal(cell);
                                if(col==4&&!colvallue.endsWith("列")){
                                    colvallue+="列";
                                }else if(col==5&&!colvallue.endsWith("节")){
                                    colvallue+="节";
                                }else if(col==6&&!colvallue.endsWith("层")){
                                    colvallue+="层";
                                }else if(col==7&&!colvallue.endsWith("面")){
                                    colvallue+="面";
                                }
                                field += colvallue + "-";
                                continue;
                            } else if (col + 1 == ImportService.ENTRY_STORAGE_LENGTH) {
                                field += ReadExcel.getStreamingCellStringVal(cell);
                                rowCellsValue.add(field);
                            } else if (col == ImportService.ENTRY_STORAGE_LENGTH){
                                field = ReadExcel.getStreamingCellStringVal(cell);
                                rowCellsValue.add(field);
                                archivecodeList.add(field);
                            }
                        }
                    }else{
                        //只保存对应顺序号的值---顺序号是从小到大顺序--（0,1,5,6）类型
                        for (int i = 0; i < fieldSequence.size(); i++) {
                            if (fieldSequence.get(i) == col) {
                                if (cell == null) {
                                    rowCellsValue.add("");
                                    continue;
                                }
                                if ("xlsx".equals(impState)) {
                                    //取出数组前8位拼接成存储位置
                                    if (isEntityStorage && i + 1 < ImportService.ENTRY_STORAGE_LENGTH) {//
                                        field += ReadExcel.getStreamingCellStringVal(cell) + "-";
                                        continue;
                                    } else if (isEntityStorage && i + 1 == ImportService.ENTRY_STORAGE_LENGTH) {
                                        field += ReadExcel.getStreamingCellStringVal(cell);
                                        rowCellsValue.add(field);
                                    } else {
                                        field = ReadExcel.getStreamingCellStringVal(cell);
                                        rowCellsValue.add(field.replace("\n",""));
                                    }
                                } else {
                                    rowCellsValue.add(ReadExcel.getStringVal(cell).replace("\n",""));
                                }
                            }
                        }
                    }
                }
                sheetRowsValue.add(rowCellsValue);
                List<String> faileArchivecodeList=new ArrayList<>();//入库失败的档号信息
                List<String> errorArchivecodeList=new ArrayList<>();//查对应条目失败的档号信息
                if (rowCount == lastRows) {//最大循环时--
                    Integer errorCount=0;
                    if(isEntityStorage){
                        //indexList = entryIndexRepository.findByArchivecodeIn(archivecodeList.toArray(new String[archivecodeList.size()]));
                        indexList = StorageListTransformEntryIndex(sheetRowsValue, strcode, target, isRepeat, errorArchivecodeList);
                        faileArchivecodeList = saveImportStorages(indexList, details, target, eleFolder, isRepeat);
                        errorCount = faileArchivecodeList.size();
                        count = indexList.size() - errorCount;
                    }else{
                        indexList = ListTransformEntryIndex(sheetRowsValue, strcode, target, isRepeat);
                        details = ListTransformEntryDetail(sheetRowsValue, strcode);
                        errorCount = saveEntryAndEleFolder(indexList, details, target, eleFolder, isRepeat, isEntityStorage, autoCreateArchivecode);
                        count = indexList.size() + count;
                    }

                    sheetRowsValue.removeAll(sheetRowsValue);
                } else if (rowCount % 1000 == 0 && rowCount >= 1000) {//400循环一次
                    //Long stime = System.currentTimeMillis();
                    Integer errorCount=0;
                    if(isEntityStorage){
                        //indexList = entryIndexRepository.findByArchivecodeIn(archivecodeList.toArray(new String[archivecodeList.size()]));
                        indexList = StorageListTransformEntryIndex(sheetRowsValue, strcode, target, isRepeat, errorArchivecodeList);
                        faileArchivecodeList = saveImportStorages(indexList, details, target, eleFolder, isRepeat);
                        errorCount = faileArchivecodeList.size();
                        count = indexList.size() - errorCount;
                    }else{
                        indexList = ListTransformEntryIndex(sheetRowsValue, strcode, target, isRepeat);
                        details = ListTransformEntryDetail(sheetRowsValue, strcode);
                        errorCount = saveEntryAndEleFolder(indexList, details, target, eleFolder, isRepeat, isEntityStorage, autoCreateArchivecode);
                        count = indexList.size() + count;
                    }
                    sheetRowsValue.removeAll(sheetRowsValue);
                    //System.out.println("组装对象用时：" + (System.currentTimeMillis() - stime));
                }
                //生成异常信息
                faileArchivecodeList.addAll(errorArchivecodeList);
                for(String errmsg:faileArchivecodeList){
                    Entry entry=new Entry();
                    entry.setArchivecode(errmsg);
                    entryList.add(entry);
                }

            }
            //-
        }
        //Long endTime = System.currentTimeMillis();
        //System.out.println("=======读取用时：" + (endTime - statime));
        int errocount = lastRows - 1 - count;
        reMap.put("num", lastRows - 1);
        reMap.put("erro", errocount);
        return reMap;
    }

    //库房excel读取
    private Map<String, Integer> readKfExcel(int lastRows, String file, List<Integer> fieldSequence,
                                           String[] strcode, String target, String isRepeat, List<String> eleFolder,
                                           String impState)
            throws Exception {
        int count = 0;
        Map<String, Integer> reMap = new HashMap<>();
        //---创建workbook对象
        Workbook workbook = null;
        InputStream in = new FileInputStream(new File(file));
        if ("xlsx".equals(impState)) {
            workbook = StreamingReader.builder()
                    .rowCacheSize(100)  //缓存到内存中的行数，默认是10
                    .bufferSize(4096)  //读取资源时，缓存到内存的字节大小，默认是1024
                    .open(in);  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件
        } else {
            workbook = WorkbookFactory.create(in);
        }
        in.close();
        for (Sheet sheet : workbook) {
            if (sheet == null) {
                continue;
            }
            List<Tb_entry_index_temp> indexList = new ArrayList<>();
            List sheetRowsValue = new ArrayList();
            int rowCount = 0;//记录循环行数
            int bRow = 0;//用来判断--去列头
            int sheetLastRowNum = sheet.getLastRowNum();
            for (Row row : sheet) {
                rowCount++;
                //跳过列头行
                if (bRow == 0) {
                    bRow++;
                    continue;
                }
                List<String> rowCellsValue = new ArrayList<>();
                List<String> archivecodeList=new ArrayList<>();//档号集合，库房导入用
                int maxcol = row.getLastCellNum();
                String field = "";
                for (int col = 0; col < fieldSequence.size(); col++) {//循环列单元格，获取值
                    Cell cell = row.getCell(col);
                    if (cell == null) {
                        rowCellsValue.add("");
                        continue;//空格也保留
                    }
                    if ("xlsx".equals(impState)) {
                        String colvallue="";
                        //取出数组前8位拼接成存储位置
                        if (col+1 < ImportService.ENTRY_STORAGE_LENGTH) {
                            colvallue=ReadExcel.getStreamingCellStringVal(cell).trim();
                            if(col==4&&!colvallue.endsWith("列")){
                                colvallue+="列";
                            }else if(col==5&&!colvallue.endsWith("节")){
                                colvallue+="节";
                            }else if(col==6&&!colvallue.endsWith("层")){
                                colvallue+="层";
                            }else if(col==7&&!colvallue.endsWith("面")){
                                colvallue+="面";
                            }
                            //field += colvallue + "-";
                            field += colvallue + "_";//该用下划线
                            continue;
                        } else if (col + 1 == ImportService.ENTRY_STORAGE_LENGTH) {
                            field += ReadExcel.getStreamingCellStringVal(cell);
                            rowCellsValue.add(field);
                        } else if (col >= ImportService.ENTRY_STORAGE_LENGTH){
                            field = ReadExcel.getStreamingCellStringVal(cell);
                            rowCellsValue.add(field);
                            archivecodeList.add(field);
                        }
                    }
                }
                sheetRowsValue.add(rowCellsValue);
                List<String> faileArchivecodeList=new ArrayList<>();//入库失败的档号信息
                List<String> errorArchivecodeList=new ArrayList<>();//查对应条目失败的档号信息
                if (rowCount == lastRows) {//最大循环时--
                    Integer errorCount=0;
                    indexList =  StorageListTransformEntryIndexTemp(sheetRowsValue, strcode, target, isRepeat, errorArchivecodeList);
                    errorCount = faileArchivecodeList.size();
                    count = indexList.size() - errorCount;
                    sheetRowsValue.removeAll(sheetRowsValue);
                } else if (rowCount % 1000 == 0 && rowCount >= 1000) {//400循环一次
                    Integer errorCount=0;
                    indexList = StorageListTransformEntryIndexTemp(sheetRowsValue, strcode, target, isRepeat, errorArchivecodeList);
                    errorCount = faileArchivecodeList.size();
                    count = indexList.size() - errorCount;
                    sheetRowsValue.removeAll(sheetRowsValue);
                }
                //生成异常信息
                faileArchivecodeList.addAll(errorArchivecodeList);
                for(String errmsg:faileArchivecodeList){
                    Entry entry=new Entry();
                    entry.setArchivecode(errmsg);
                    entryList.add(entry);
                }

            }
        }
        int errocount = lastRows - 1 - count;
        reMap.put("num", lastRows - 1);
        reMap.put("erro", errocount);
        return reMap;
    }

    private Map<String, Integer> readExcelCapture(int lastRows, String file, List<Integer> fieldSequence,
                                                  String[] strcode, String target, String isRepeat,
                                                  List<String> eleFolder, String impState, String importtype)
            throws Exception {
        int count = 0;
        Map<String, Integer> reMap = new HashMap<>();
        //---创建workbook对象
        Workbook workbook = null;
        if ("xlsx".equals(impState)) {
            workbook = StreamingReader.builder()
                    .rowCacheSize(100)  //缓存到内存中的行数，默认是10
                    .bufferSize(4096)  //读取资源时，缓存到内存的字节大小，默认是1024
                    .open(new FileInputStream(new File(file)));  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件
        } else {
            //workbook= new HSSFWorkbook(new FileInputStream(new File(file)));
            workbook = WorkbookFactory.create(new FileInputStream(new File(file)));
        }

        for (Sheet sheet : workbook) {
            if (sheet == null) {
                continue;
            }
            List<Tb_entry_index_capture> indexList = new ArrayList<>();
            List<Tb_entry_detail_capture> details = new ArrayList<>();
            List<Tb_entry_index_accept> acceptindexList = new ArrayList<>();
            List<Tb_entry_detail_accept> acceptdetails = new ArrayList<>();
            List sheetRowsValue = new ArrayList();
            int rowCount = 0;//记录循环行数
            int bRow = 0;//用来判断--去列头
            int sheetLastRowNum = sheet.getLastRowNum();
            for (Row row : sheet) {
                rowCount++;
                //跳过列头行
                if (bRow == 0) {
                    bRow++;
                    continue;
                }
                List<String> rowCellsValue = new ArrayList<>();
                int maxcol = row.getLastCellNum();
                for (int col = 0; col < maxcol; col++) {//循环列单元格，获取值
                    Cell cell = row.getCell(col);

                    //只保存对应顺序号的值---顺序号是从小到大顺序--（0,1,5,6）类型
                    for (int i = 0; i < fieldSequence.size(); i++) {
                        if (fieldSequence.get(i) == col) {
                            if (cell == null) {
                                rowCellsValue.add("");
                                continue;
                            }
                            if ("xlsx".equals(impState)) {
                                rowCellsValue.add(ReadExcel.getStreamingCellStringVal(cell).replace("\n",""));
                            } else {
                                rowCellsValue.add(ReadExcel.getStringVal(cell).replace("\n",""));
                            }
                        }
                    }
                }
                sheetRowsValue.add(rowCellsValue);
                if (rowCount == lastRows) {//最大循环时--
                    Integer errorCount=0;
                    if (importtype != null && "accept".equals(importtype)) {  //判断是否目录接收导入
                        acceptindexList = captureListTransformEntryIndexAccept(sheetRowsValue, strcode, target, isRepeat);
                        acceptdetails = ListTransformEntryDetailAccept(sheetRowsValue, strcode);
                        capturesaveEntryAndEleFolder(indexList, details, target, eleFolder, isRepeat, acceptindexList, acceptdetails, importtype);
                        count = acceptindexList.size() + count;
                    } else {
                        indexList = captureListTransformEntryIndex(sheetRowsValue, strcode, target, isRepeat);
                        details = captureListTransformEntryDetail(sheetRowsValue, strcode);
                        capturesaveEntryAndEleFolder(indexList, details, target, eleFolder, isRepeat, acceptindexList, acceptdetails, importtype);
                        count = indexList.size() + count;
                    }
                    sheetRowsValue.removeAll(sheetRowsValue);
                } else if (rowCount % 1000 == 0 && rowCount >= 1000) {//400循环一次
                    if (importtype != null && "accept".equals(importtype)) {  //判断是否目录接收导入
                        acceptindexList = captureListTransformEntryIndexAccept(sheetRowsValue, strcode, target, isRepeat);
                        acceptdetails = ListTransformEntryDetailAccept(sheetRowsValue, strcode);
                        capturesaveEntryAndEleFolder(indexList, details, target, eleFolder, isRepeat, acceptindexList, acceptdetails, importtype);
                        count = acceptindexList.size() + count;
                    } else {
                        indexList = captureListTransformEntryIndex(sheetRowsValue, strcode, target, isRepeat);
                        details = captureListTransformEntryDetail(sheetRowsValue, strcode);
                        capturesaveEntryAndEleFolder(indexList, details, target, eleFolder, isRepeat, acceptindexList, acceptdetails, importtype);
                        count = indexList.size() + count;
                    }
                    sheetRowsValue.removeAll(sheetRowsValue);
                }
            }
        }
        int errocount = lastRows - 1 - count;
        reMap.put("num", lastRows - 1);
        reMap.put("erro", errocount);
        return reMap;
    }

    private Map<String, Integer> readXml(String file, List<Integer> fieldSequence, String[] strcode,
                                         String target, String isRepeat, List<String> eleFolder, boolean isEntityStorage, boolean autoCreateArchivecode) {
        //1.创建sax对象
        SAXReader reader = new SAXReader();
        List<List<String>> lists = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        List<Tb_entry_index> indexList = null;
        List<Tb_entry_detail> details = null;
        try {
            Document document = reader.read(new File(file));
            Element element = document.getRootElement();
            List<Element> elements = element.elements();
            for (int i = 0; i < elements.size(); i++) {// record 条目数
                List<String> list = new ArrayList<>();
                List<Element> elementListlist = elements.get(i).elements();
//                for (int j = 0; j < elementListlist.size(); j++) {
//                    for (int k = 0; k < fieldSequence.size(); k++) {
//                        if (fieldSequence.get(k) == j) {
//                            list.add(elementListlist.get(j).getStringValue());
//                        }
//                    }
//                }
                for(int a=0;a<fieldSequence.size();a++){//循环顺序号
                    for (int k = 0; k < elementListlist.size(); k++){//循环record下面的子标签
                        if(fieldSequence.get(a)==k){//保存相同顺序号的标签值
                            list.add(elementListlist.get(k).getStringValue());
                        }
                    }
                }
                lists.add(list);
            }
            //拿到解析结果 进行组装
            indexList = ListTransformEntryIndex(lists, strcode, target, isRepeat);
            details = ListTransformEntryDetail(lists, strcode);
            saveEntryAndEleFolder(indexList, details, target, eleFolder, isRepeat, isEntityStorage, autoCreateArchivecode);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("num", lists.size());
        map.put("erro", lists.size() - indexList.size());
        return map;
    }

    private Map<String, Integer> readXmlCapture(String file, List<Integer> fieldSequence, String[] strcode, String target, String isRepeat, List<String> eleFolder, String importtype) {
        //1.创建sax对象
        SAXReader reader = new SAXReader();
        List<List<String>> lists = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        List<Tb_entry_index_capture> indexList = null;
        List<Tb_entry_index_accept> acceptindexList = null;
        List<Tb_entry_detail_accept> acceptdetailList = null;
        List<Tb_entry_detail_capture> details = null;
        try {
            Document document = reader.read(new File(file));
            Element element = document.getRootElement();
            List<Element> elements = element.elements();
            for (int i = 0; i < elements.size(); i++) {// record 条目数
                List<String> list = new ArrayList<>();
                List<Element> elementListlist = elements.get(i).elements();
                for (int j = 0; j < elementListlist.size(); j++) {
                    for (int k = 0; k < fieldSequence.size(); k++) {
                        if (fieldSequence.get(k) == j) {
                            list.add(elementListlist.get(j).getStringValue());
                        }
                    }
                }
                lists.add(list);
            }
            //拿到解析结果 进行组装
            if (importtype != null && "accept".equals(importtype)) {  //判断是否目录接收导入
                acceptindexList = captureListTransformEntryIndexAccept(lists, strcode, target, isRepeat);
                acceptdetailList = ListTransformEntryDetailAccept(lists, strcode);
                capturesaveEntryAndEleFolder(indexList, details, target, eleFolder, isRepeat, acceptindexList, acceptdetailList, importtype);
                map.put("num", lists.size());
                map.put("erro", lists.size() - acceptindexList.size());
            } else {
                indexList = captureListTransformEntryIndex(lists, strcode, target, isRepeat);
                details = captureListTransformEntryDetail(lists, strcode);
                capturesaveEntryAndEleFolder(indexList, details, target, eleFolder, isRepeat, acceptindexList, acceptdetailList, importtype);
                map.put("num", lists.size());
                map.put("erro", lists.size() - indexList.size());
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    //保存原文文件
    private void capturesaveEntryAndEleFolder(List<Tb_entry_index_capture> indexList, List<Tb_entry_detail_capture> details, String nodeid,
                                              List<String> eleFolder, String isRepeat, List<Tb_entry_index_accept> acceptindexList,
                                              List<Tb_entry_detail_accept> acceptdetails, String importtype) {
        if (importtype != null && "accept".equals(importtype) ) { //判断是否目录接收导入
            if (acceptindexList.size() > 0) {
                for (int i = 0; i < acceptindexList.size(); i++) {
                    acceptindexList.get(i).setNodeid(nodeid);
                    if ("NO".equals(isRepeat)) {
                        acceptindexList.get(i).setEntryid(null);
                    }
                    //判断份数 如果为空 就赋值为1
                    if ("".equals(acceptindexList.get(i).getFscount()) || null == acceptindexList.get(i).getFscount()) {
                        acceptindexList.get(i).setFscount("1");
                    }
                    //判断库存份数 如果为空 就赋值为1
                    if ("".equals(acceptindexList.get(i).getKccount()) || null == acceptindexList.get(i).getKccount()) {
                        acceptindexList.get(i).setKccount("1");
                    }
                }
                List<Tb_entry_index_accept> bb = null;
                if ("NO".equals(isRepeat)) {
                    bb = insettEntryIndexAccept(acceptindexList);
                    for (int i = 0; i < acceptdetails.size(); i++) {
                        acceptdetails.get(i).setEntryid(bb.get(i).getEntryid());
                        entityManager.persist(acceptdetails.get(i));
                    }
                    entityManager.flush();
                    entityManager.clear();
                } else {
                    bb = entryIndexAcceptRepository.save(acceptindexList);
                    for (int i = 0; i < acceptdetails.size(); i++) {
                        acceptdetails.get(i).setEntryid(bb.get(i).getEntryid());
                    }
                    entryDetailAcceptRepository.save(acceptdetails);
                }
            }
        } else {
            String datastr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            if (eleFolder.size() > 0 && indexList.size() > 0) {
                List<String> indexEntryId = new ArrayList<>();
                for (int i = 0; i < indexList.size(); i++) {
                    indexList.get(i).setNodeid(nodeid);
                    indexEntryId.add(indexList.get(i).getEntryid());
                    if ("NO".equals(isRepeat)) {
                        indexList.get(i).setEntryid(null);
                    }
                    //判断份数 如果为空 就赋值为1
                    if ("".equals(indexList.get(i).getFscount()) || null == indexList.get(i).getFscount()) {
                        indexList.get(i).setFscount("1");
                    }
                    //判断库存份数 如果为空 就赋值为1
                    if ("".equals(indexList.get(i).getKccount()) || null == indexList.get(i).getKccount()) {
                        indexList.get(i).setKccount("1");
                    }
                }
                List<Tb_entry_index_capture> newEntryId = new ArrayList<>();
                List<String> entryIdList = new ArrayList<>();
                //覆盖导入暂不处理
                for(int i = 0; i < details.size(); i++){
                    Tb_entry_index_capture capture = indexList.get(i);
                    capture.setDescriptiondate(datastr);
                    capture = entryIndexCaptureRepository.save(capture);
                    details.get(i).setEntryid(capture.getEntryid());
                    entryDetailCaptureRepository.save(details.get(i));
                    newEntryId.add(capture);
                    entryIdList.add(capture.getEntryid());
                }
                captureMetadataService.addCaptureMetadataTxt(entryIdList);//记录元数据id到txt
                String newpath = captureGetStorageDir();
                for (int i = 0; i < newEntryId.size(); i++) {//遍历插入后的条目id
                    //遍历原文目录
                    for (int k = 0; k < eleFolder.size(); k++) {// 遍历原文目录
                        String[] lib = eleFolder.get(k).replaceAll("\\\\", "/").split("/");
                        String libName = lib[lib.length - 1];
                        String indexArchivecode = newEntryId.get(i).getArchivecode();
                        String newArchivalList = null;
                        if (indexArchivecode != null) {
                            newArchivalList = indexArchivecode.replaceAll("\\·", "-");//防止档号中有'.'点号
                        }
                        if (newArchivalList != null) {
                            if (libName.equals(newArchivalList)) {// 存在同档号名的原文目录
                                try {
                                    FileUtil.copyDir(eleFolder.get(k), newpath + "/" + newEntryId.get(i).getEntryid());// 将原文文件copy到newpath
                                    // 1-获取目录下的所有文件
                                    List<String> files = FileUtil.getFile(eleFolder.get(k));
                                    for (int l = 0; l < files.size(); l++) {// 遍历单个条目的原文数
                                        Tb_electronic_capture electronic = new Tb_electronic_capture();
                                        String[] fnames = files.get(l).replaceAll("\\\\", "/").split("/");
                                        String eleFileName = fnames[fnames.length - 1];// 带后缀的原文名
                                        String suffix = eleFileName.substring(eleFileName.lastIndexOf(".") + 1);
                                        electronic.setFilepath((newpath + "/" + newEntryId.get(i).getEntryid()).replace(rootpath, ""));
                                        electronic.setFilename(eleFileName);
                                        electronic.setFilesize(String.valueOf(new File(files.get(l)).length()));
                                        electronic.setEntryid(newEntryId.get(i).getEntryid());
                                        electronic.setFiletype(suffix);
                                        electronicCaptureRepository.save(electronic);
                                    }
                                    // 修改对应条目的eleid
                                    entryIndexCaptureRepository.updateEleidByEntryid(String.valueOf(files.size()),
                                            newEntryId.get(i).getEntryid());
                                } catch (IOException e) {
                                    logger.error(e.getMessage());
                                }
                            }
                        }
                        // 未插入数据库前的entryid
                        // 判断是否存在entryid命名目录
                        if (indexEntryId.get(i) != null) {
                            if (indexEntryId.get(i).trim().equals(libName)) {
                                try {
                                    FileUtil.copyDir(eleFolder.get(k), newpath + "/" + newEntryId.get(i).getEntryid());// 将原文文件copy到newpath
                                    List<String> files = FileUtil.getFile(eleFolder.get(k));
                                    for (int l = 0; l < files.size(); l++) {// 遍历单个条目的原文数
                                        Tb_electronic_capture electronic = new Tb_electronic_capture();
                                        String[] fnames = files.get(l).replaceAll("\\\\", "/").split("/");
                                        String eleFileName = fnames[fnames.length - 1];// 带后缀的原文名
                                        String suffix = eleFileName.substring(eleFileName.lastIndexOf(".") + 1);
                                        electronic.setFilepath((newpath + "/" + newEntryId.get(i).getEntryid()).replace(rootpath, ""));
                                        electronic.setFilename(eleFileName);
                                        electronic.setFilesize(String.valueOf(new File(files.get(l)).length()));
                                        electronic.setEntryid(newEntryId.get(i).getEntryid());
                                        electronic.setFiletype(suffix);
                                        electronicCaptureRepository.save(electronic);
                                    }
                                    // 修改对应条目的eleid
                                    entryIndexCaptureRepository.updateEleidByEntryid(String.valueOf(files.size()), newEntryId.get(i).getEntryid());
                                } catch (IOException e) {
                                    logger.error(e.getMessage());
                                }
                            }
                        }
                    }
                }
            } else if (indexList.size() > 0) {
                for (int i = 0; i < indexList.size(); i++) {
                    indexList.get(i).setNodeid(nodeid);
                    if ("NO".equals(isRepeat)) {
                        indexList.get(i).setEntryid(null);
                    }
                    //判断份数 如果为空 就赋值为1
                    if ("".equals(indexList.get(i).getFscount()) || null == indexList.get(i).getFscount()) {
                        indexList.get(i).setFscount("1");
                    }
                    //判断库存份数 如果为空 就赋值为1
                    if ("".equals(indexList.get(i).getKccount()) || null == indexList.get(i).getKccount()) {
                        indexList.get(i).setKccount("1");
                    }
                }
                List<Tb_entry_index_capture> bb = null;
                List<String> entryIdList = new ArrayList<>();
                //覆盖导入暂不处理
                for(int i = 0; i < details.size(); i++){
                    Tb_entry_index_capture capture = indexList.get(i);
                    capture.setDescriptiondate(datastr);
                    capture = entryIndexCaptureRepository.save(capture);
                    details.get(i).setEntryid(capture.getEntryid());
                    entryDetailCaptureRepository.save(details.get(i));
                    entryIdList.add(capture.getEntryid());
                }
                captureMetadataService.addCaptureMetadataTxt(entryIdList);//记录元数据id到txt
            }
        }
    }


    //保存原文文件
    private Integer saveEntryAndEleFolder(List<Tb_entry_index> indexList, List<Tb_entry_detail> details, String nodeid,
                                          List<String> eleFolder, String isReplace, boolean isEntityStorage, boolean autoCreateArchivecode) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (indexList.size() > 0 && !isEntityStorage) {
            List<String> indexEntryId = new ArrayList<>();
            for (int i = 0; i < indexList.size(); i++) {
                Tb_entry_index index = indexList.get(i);
                index.setNodeid(nodeid);
                if (null == index.getDescriptiondate() || "".equals(index.getDescriptiondate())) {
                    index.setDescriptiondate(df.format(System.currentTimeMillis()));
                }
                indexList.get(i).setNodeid(nodeid);
                indexEntryId.add(indexList.get(i).getEntryid());
                if ("NO".equals(isReplace)) {
                    indexList.get(i).setEntryid(null);
                }
                //判断份数 如果为空 就赋值为1
                if ("".equals(indexList.get(i).getFscount()) || null == indexList.get(i).getFscount()) {
                    indexList.get(i).setFscount("1");
                }
                //判断库存份数 如果为空 就赋值为1
                if ("".equals(indexList.get(i).getKccount()) || null == indexList.get(i).getKccount()) {
                    indexList.get(i).setKccount("1");
                }
                //判断是否有档号
                if("".equals(index.getArchivecode())||null==index.getArchivecode()&&autoCreateArchivecode){
                    Tb_entry_index archiveIndex = null;
                    try {
                        //创建档号
                        archiveIndex = alignArchivecode(index, "数据管理");
                    }catch (NullPointerException e){
                        throw new RuntimeException("导入自动生成档号失败",e);
                    }
                    if(archiveIndex!=null) {
                        String archiveCode = archiveIndex.getArchivecode();
                        index.setArchivecode(archiveCode);
                    }
                }
                index.setEntryid(null);
            }
            //保存index 获取返回的对象
            //List<Tb_entry_index> newEntryId = entryIndexRepository.save(indexList);
            List<Tb_entry_index> newEntryId = null;
            if ("NO".equals(isReplace)) {
//                newEntryId = insertEntryIndex(indexList);
                newEntryId = entryIndexRepository.save(indexList);
                for (int i = 0; i < details.size(); i++) {
                    captureMetadataService.saveServiceMetadata(newEntryId.get(i).getEntryid(),"数据管理","导入");
                    details.get(i).setEntryid(newEntryId.get(i).getEntryid());
//                    entityManager.persist(details.get(i));
                }
                entryDetailRepository.save(details);
//                entityManager.flush();
//                entityManager.clear();
            } else {
                newEntryId = entryIndexRepository.save(indexList);
                for (int i = 0; i < details.size(); i++) {
                    captureMetadataService.saveServiceMetadata(newEntryId.get(i).getEntryid(),"数据管理","导入");
                    details.get(i).setEntryid(newEntryId.get(i).getEntryid());
                }
                entryDetailRepository.save(details);
            }
            String newpath = getStorageDir();
            for (int i = 0; i < newEntryId.size(); i++) {//遍历插入后的条目id
                //遍历原文目录
                for (int k = 0; k < eleFolder.size(); k++) {// 遍历原文目录
                    String[] lib = eleFolder.get(k).replaceAll("\\\\", "/").split("/");
                    String libName = lib[lib.length - 1];
                    String indexArchivecode = newEntryId.get(i).getArchivecode();
                    String newArchivalList = null;
                    if (indexArchivecode != null) {
                        newArchivalList = indexArchivecode.replaceAll("\\·", "-");//防止档号中有'.'点号
                    }
                    if (newArchivalList != null) {
                        if (libName.equals(newArchivalList)) {// 存在同档号名的原文目录
                            try {
                                FileUtil.copyDir(eleFolder.get(k), newpath + "/" + newEntryId.get(i).getEntryid());// 将原文文件copy到newpath
                                // 1-获取目录下的所有文件
                                List<String> files = FileUtil.getFile(eleFolder.get(k));
                                for (int l = 0; l < files.size(); l++) {// 遍历单个条目的原文数
                                    Tb_electronic electronic = new Tb_electronic();
                                    String[] fnames = files.get(l).replaceAll("\\\\", "/").split("/");
                                    String eleFileName = fnames[fnames.length - 1];// 带后缀的原文名
                                    String suffix = eleFileName.substring(eleFileName.lastIndexOf(".") + 1);
                                    electronic.setFilepath((newpath + "/" + newEntryId.get(i).getEntryid()).replace(rootpath, ""));
                                    electronic.setFilename(eleFileName);
                                    electronic.setFilesize(String.valueOf(new File(files.get(l)).length()));
                                    electronic.setEntryid(newEntryId.get(i).getEntryid());
                                    electronic.setFiletype(suffix);
                                    electronicRepository.save(electronic);
                                }
                                // 修改对应条目的eleid
                                entryIndexRepository.updateEleidByEntryid(String.valueOf(files.size()),
                                        newEntryId.get(i).getEntryid());
                            } catch (IOException e) {
                                logger.error(e.getMessage());
                            }
                        }
                    }
                    // 未插入数据库前的entryid
                    // 判断是否存在entryid命名目录
                    if (indexEntryId.get(i) != null) {
                        if (indexEntryId.get(i).trim().equals(libName)) {
                            try {
                                FileUtil.copyDir(eleFolder.get(k), newpath + "/" + newEntryId.get(i).getEntryid());// 将原文文件copy到newpath
                                List<String> files = FileUtil.getFile(eleFolder.get(k));
                                for (int l = 0; l < files.size(); l++) {// 遍历单个条目的原文数
                                    Tb_electronic electronic = new Tb_electronic();
                                    String[] fnames = files.get(l).replaceAll("\\\\", "/").split("/");
                                    String eleFileName = fnames[fnames.length - 1];// 带后缀的原文名
                                    String suffix = eleFileName.substring(eleFileName.lastIndexOf(".") + 1);
                                    electronic.setFilepath((newpath + "/" + newEntryId.get(i).getEntryid()).replace(rootpath, ""));
                                    electronic.setFilename(eleFileName);
                                    electronic.setFilesize(String.valueOf(new File(files.get(l)).length()));
                                    electronic.setEntryid(newEntryId.get(i).getEntryid());
                                    electronic.setFiletype(suffix);
                                    electronicRepository.save(electronic);
                                }
                                // 修改对应条目的eleid
                                entryIndexRepository.updateEleidByEntryid(String.valueOf(files.size()), newEntryId.get(i).getEntryid());
                            } catch (IOException e) {
                                logger.error(e.getMessage());
                            }
                        }
                    }
                }
            }
        } else if (indexList.size() > 0) {//库房导入
            for (int i = 0; i < indexList.size(); i++) {
                Tb_entry_index index = indexList.get(i);
                index.setNodeid(nodeid);
                if (null == index.getDescriptiondate() || "".equals(index.getDescriptiondate())) {
                    index.setDescriptiondate(df.format(System.currentTimeMillis()));
                }
                indexList.get(i).setNodeid(nodeid);
                if ("NO".equals(isReplace)) {
                    indexList.get(i).setEntryid(null);
                }
                //判断份数 如果为空 就赋值为1
                if ("".equals(indexList.get(i).getFscount()) || null == indexList.get(i).getFscount()) {
                    indexList.get(i).setFscount("1");
                }
                //判断库存份数 如果为空 就赋值为1
                if ("".equals(indexList.get(i).getKccount()) || null == indexList.get(i).getKccount()) {
                    indexList.get(i).setKccount("1");
                }
                //判断是否有档号
                if("".equals(index.getArchivecode())||null==index.getArchivecode()&&autoCreateArchivecode){
                    Tb_entry_index archiveIndex = null;
                    try {
                        //创建档号
                        archiveIndex = alignArchivecode(index, "数据管理");
                    }catch (NullPointerException e){
                        throw new RuntimeException("导入自动生成档号失败",e);
                    }
                    if(archiveIndex!=null) {
                        String archiveCode = archiveIndex.getArchivecode();
                        index.setArchivecode(archiveCode);
                    }
                }
                index.setEntryid(null);
            }
            //表示为实体档案入库的导入

            List<Tb_entry_index> successEntryInddexs = new LinkedList<>();
            List<Tb_entry_detail> successEntryDetails = new LinkedList<>();
            List<Tb_entry_detail> errorEntryDetails = new LinkedList<>();
            List<Tb_entry_index> errorEntryInddexs = new LinkedList<>();
            if (isEntityStorage) {
                for (int i = 0; i < indexList.size(); i++) {
                    Tb_entry_index entryIndex = indexList.get(i);
                    Tb_entry_detail entryDetail = details.get(i);
                    String entryStorage = entryIndex.getEntrystorage();
                    if (StringUtils.isNotBlank(entryStorage)) {
                        String[] entryStorages = entryStorage.split("-");
                        if (entryStorages.length != ImportService.ENTRY_STORAGE_LENGTH) {
                            errorEntryInddexs.add(entryIndex);
                            errorEntryDetails.add(entryDetail);
                            break;
                        }
                        String city = entryStorages[0];//城市
                        String unit = entryStorages[1];//单位
                        String room = entryStorages[2];//库房
                        String zone = entryStorages[3];//架区
                        String column = String.format("%02d", Integer.valueOf(entryStorages[4]));//列
                        String section = String.format("%02d", Integer.valueOf(entryStorages[5]));//节
                        String layer = String.format("%02d", Integer.valueOf(entryStorages[6]));//层
                        String side = entryStorages[7];//面
                        //为了防止同时输入了对应存储位置名称，所以如果有输入了就把它去掉
                        if (column.contains("列")) {
                            column = column.substring(0, column.indexOf("列"));
                        }
                        if (section.contains("节")) {
                            section = section.substring(0, section.indexOf("节"));
                        }
                        if (layer.contains("层")) {
                            layer = layer.substring(0, layer.indexOf("层"));
                        }
                        if (!side.contains("面")) {
                            side = side + "面";
                        }
                        String zoneId = zonesRepository.findZondId(city, unit, room, zone);
                        ZoneShelves zoneShelves = null;
                        if (zoneId != null) {
                            zoneShelves = zoneShelvesRepository.findZoneSheleves(column, section, layer, side, zoneId);

                        }
                        if (zoneShelves != null) {
                            if (zoneShelves.getCapacity() <= zoneShelves.getUsecapacity()) {
                                errorEntryInddexs.add(entryIndex);//放入密集架空间不足的条目
                            }else{
                                entryIndex.setZoneShelves(zoneShelves.getShid());
                                successEntryInddexs.add(entryIndex);
                                successEntryDetails.add(entryDetail);
                            }
                        } else {
                            errorEntryInddexs.add(entryIndex);
                            errorEntryDetails.add(entryDetail);
                        }
                    } else {//没有存储位置
                        errorEntryInddexs.add(entryIndex);
                        errorEntryDetails.add(entryDetail);
                    }
                }
                indexList = successEntryInddexs;//筛选出密集架存储位置存在的条目
                details = successEntryDetails;
            }
            List<Tb_entry_index> bb = null;
            //从session获取用户名
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpSession session = request.getSession();
            String user = (String) session.getAttribute("username");
            InWare iw = new InWare();
            iw.setWaretype("新增入库");
            iw.setWareuser(user);
            if ("NO".equals(isReplace)) {
                bb = insertEntryIndex(indexList);
                for (int i = 0; i < details.size(); i++) {
                    if (isEntityStorage) {
                        Storage storage = storageRepository.findStorage(bb.get(i).getEntryid(), "已入库");
                        if (storage == null) {
                            ZoneShelves zs = zoneShelvesRepository.findByShid(indexList.get(i).getZoneShelves());
                            if (iw.getStorages() == null) {
                                iw.setStorages(new HashSet<Storage>());
                            }
                            Storage st = new Storage();
                            st.setEntry(bb.get(i).getEntryid());
                            st.setZoneShelves(zs);
                            st.setStorestatus(Storage.STATUS_IN);
                            iw.getStorages().add(st);
                        }
                    }
                    details.get(i).setEntryid(bb.get(i).getEntryid());
                    entityManager.persist(details.get(i));
                }
                if (iw.getStorages() != null && iw.getStorages().size() > 0) {
                    inWareService.save(iw);
                }
                entityManager.flush();
                entityManager.clear();
            } else {
                bb = entryIndexRepository.save(indexList);
                for (int i = 0; i < details.size(); i++) {
                    if (isEntityStorage) {
                        Storage storage = storageRepository.findStorage(bb.get(i).getEntryid(), "已入库");
                        if (storage == null) {
                            ZoneShelves zs = zoneShelvesRepository.findByShid(indexList.get(i).getZoneShelves());
                            if (iw.getStorages() == null) {
                                iw.setStorages(new HashSet<Storage>());
                            }
                            Storage st = new Storage();
                            st.setEntry(bb.get(i).getEntryid());
                            st.setZoneShelves(zs);
                            st.setStorestatus(Storage.STATUS_IN);
                            iw.getStorages().add(st);
                        }
                    }
                    details.get(i).setEntryid(bb.get(i).getEntryid());
                }
                if (iw.getStorages() != null && iw.getStorages().size() > 0) {
                    inWareService.save(iw);
                }
                entryDetailRepository.save(details);
            }
            if (isEntityStorage) {
                entryList.removeAll(entryList);
                List<Entry> entries = createEntrtList(errorEntryInddexs, errorEntryDetails);
                entryList.addAll(entries);
            }
            return errorEntryInddexs.size();
        }
        return null;
    }

    public List<String> saveImportStorages(List<Tb_entry_index> indexList, List<Tb_entry_detail> details, String nodeid,
                                  List<String> eleFolder, String isReplace){

        for (int i = 0; i < indexList.size(); i++) {
            //判断份数 如果为空 就赋值为1
            if ("".equals(indexList.get(i).getFscount()) || null == indexList.get(i).getFscount()) {
                indexList.get(i).setFscount("1");
            }
            //判断库存份数 如果为空 就赋值为1
            if ("".equals(indexList.get(i).getKccount()) || null == indexList.get(i).getKccount()) {
                indexList.get(i).setKccount("1");
            }
        }
        //表示为实体档案入库的导入

        List<Tb_entry_index> successEntryInddexs = new LinkedList<>();
        List<Tb_entry_index> errorEntryInddexs = new LinkedList<>();
        List<String> errorArchivecodeList = new LinkedList<>();
        for (int i = 0; i < indexList.size(); i++) {
            Tb_entry_index entryIndex = indexList.get(i);
            String entryStorage = entryIndex.getEntrystorage();
            if (StringUtils.isNotBlank(entryStorage)) {
                String[] entryStorages = entryStorage.split("-");
                if (entryStorages.length < ImportService.ENTRY_STORAGE_LENGTH) {//有设置 3-1库房时 ，会大于8
                    entryIndex.setEntrystorage("");
                    errorEntryInddexs.add(entryIndex);
                    errorArchivecodeList.add(entryIndex.getArchivecode()+"  存储位置不够详细");
                    continue;
                }
                String city = entryStorages[0];//城市
                String unit = entryStorages[1];//单位
                String room = entryStorages[2];//库房
                String zone = entryStorages[3];//架区
                String column=entryStorages[4];//列
                String section=entryStorages[5];//节
                String layer=entryStorages[6];//层
                String side=entryStorages[7];//面

                //为了防止同时输入了对应存储位置名称，所以如果有输入了就把它去掉
                if (column.contains("列")) {
                    column = column.substring(0, column.indexOf("列"));
                }
                if (section.contains("节")) {
                    section = section.substring(0, section.indexOf("节"));
                }
                if (layer.contains("层")) {
                    layer = layer.substring(0, layer.indexOf("层"));
                }
                if (!side.contains("面")) {
                    side = side + "面";
                }
                column = String.format("%02d", Integer.valueOf(column));//列
                section = String.format("%02d", Integer.valueOf(section ));//节
                layer = String.format("%02d", Integer.valueOf(layer));//层
                String zoneId = zonesRepository.findZondId(city, unit, room, zone);
                ZoneShelves zoneShelves = null;
                if (zoneId != null) {
                    zoneShelves = zoneShelvesRepository.findZoneSheleves(column, section, layer, side, zoneId);

                }
                if (zoneShelves != null) {
                    if (zoneShelves.getCapacity() <= zoneShelves.getUsecapacity()) {
                        entryIndex.setEntrystorage("");
                        errorEntryInddexs.add(entryIndex);//放入密集架空间不足的条目
                        errorArchivecodeList.add(entryIndex.getArchivecode()+"  放入密集架空间不足");
                    }else{
                        //根据archivecode查询条目是否已经在库
                        try{
                            Storage storage = storageRepository.findByChipcode(entryIndex.getArchivecode());
                            if(storage==null){
                                entryIndex.setZoneShelves(zoneShelves.getShid());
                                successEntryInddexs.add(entryIndex);
                            }else{
                                entryIndex.setEntrystorage("");
                                errorEntryInddexs.add(entryIndex);
                                errorArchivecodeList.add(entryIndex.getArchivecode()+"  已入库");
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                            entryIndex.setEntrystorage("");
                            errorEntryInddexs.add(entryIndex);
                            errorArchivecodeList.add(entryIndex.getArchivecode()+"  已入库");
                        }
                    }
                } else {
                    errorEntryInddexs.add(entryIndex);
                    entryIndex.setEntrystorage("");
                    errorArchivecodeList.add(entryIndex.getArchivecode()+"  存储位置没有匹配到");
                }
            } else {//没有存储位置
                errorEntryInddexs.add(entryIndex);
                errorArchivecodeList.add(entryIndex.getArchivecode()+"  存储位置信息为空");
            }
        }
        indexList = successEntryInddexs;//筛选出密集架存储位置存在的条目
        //从session获取用户名
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        String user = (String) session.getAttribute("username");
        InWare iw = new InWare();
        iw.setWaretype("新增入库");
        iw.setWareuser(user);
        if ("NO".equals(isReplace)) {
            for (int i = 0; i < indexList.size(); i++) {
                Storage storage = storageRepository.findStorage(indexList.get(i).getEntryid(), "已入库");
                if (storage == null) {
                    ZoneShelves zs = zoneShelvesRepository.findByShid(indexList.get(i).getZoneShelves());
                    if (iw.getStorages() == null) {
                        iw.setStorages(new HashSet<Storage>());
                    }
                    Storage st = new Storage();
                    st.setEntry(indexList.get(i).getEntryid());
                    st.setZoneShelves(zs);
                    st.setStorestatus(Storage.STATUS_IN);
                    iw.getStorages().add(st);
                }
            }
            if (iw.getStorages() != null && iw.getStorages().size() > 0) {
                inWareService.save(iw);
            }
        } else {
            for (int i = 0; i < indexList.size(); i++) {
                Storage storage = storageRepository.findStorage(indexList.get(i).getEntryid(), "已入库");
                if (storage == null) {
                    ZoneShelves zs = zoneShelvesRepository.findByShid(indexList.get(i).getZoneShelves());
                    if (iw.getStorages() == null) {
                        iw.setStorages(new HashSet<Storage>());
                    }
                    Storage st = new Storage();
                    st.setEntry(indexList.get(i).getEntryid());
                    st.setZoneShelves(zs);
                    st.setStorestatus(Storage.STATUS_IN);
                    iw.getStorages().add(st);
                }
            }
            if (iw.getStorages() != null && iw.getStorages().size() > 0) {
                inWareService.save(iw);
            }
        }
        return errorArchivecodeList;
    }


    private void createErroFile(String[] userFieldName, String[] userFieldCode, String UserfileName, List<String> eleFolder, String CreateType) throws Exception {
        List<String> archivalList = new ArrayList<>();
        if (IMPORT_STYPE_XML.equals(CreateType)) {
            archivalList = createFailureXml(UserfileName, userFieldCode, userFieldName);// 创建导入失败excel,返回重复档号/id
        } else if (IMPORT_STYPE_EXCEL.equals(CreateType)) {
            archivalList = CreateFailureExcel(UserfileName, userFieldName, userFieldCode);// 创建导入失败excel,返回重复档号/id
        }
        String zippath = rootpath + "/OAFile" + "/导入失败/" + "/" + UserfileName + ".zip";
        for (int k = 0; k < archivalList.size(); k++) {// 遍历重复档号
            for (int l = 0; l < eleFolder.size(); l++) {// 遍历原文目录
                String[] lib = eleFolder.get(l).replaceAll("\\\\", "/").split("/");
                String libName = lib[lib.length - 1];
                String newArchivalList = archivalList.get(k).replaceAll("\\·", "-");//防止档号中有'.'点号
                if (newArchivalList.trim().equals(libName)) {// 存在同档号名的目录
                    FileUtil.copyDir(eleFolder.get(l), rootpath + "/OAFile" + "/导入失败/"
                            + UserfileName + "/document/" + archivalList.get(k).trim());
                }
            }
        }
                        /*// 复制模板文件到目录下
                        for (int L = 0; L < tempFolder.size(); L++) {
                            String str3 = tempFolder.get(L).replaceAll("\\\\", "/");
                            if (!str3.equals(UnZipFile + "/" + UserfileName + "/document")) {
                                FileUtil.copyDir(tempFolder.get(L),
                                        rootpath + "/OAFile" + "/导入失败/" + UserfileName + "/字段模板信息");
                            }
                        }*/
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(zippath);
            ZipUtils.toZip(rootpath + "/OAFile" + "/导入失败/" + UserfileName, fileOutputStream, true);
            fileOutputStream.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        RepetitionIndex.removeAll(RepetitionIndex);// 清除重复档号
        RepetitionEntryid.removeAll(RepetitionEntryid);
    }


    private void capturecreateErroFile(String[] userFieldName, String[] userFieldCode, String UserfileName, List<String> eleFolder,
                                       String CreateType, String importtype) throws Exception {
        List<String> archivalList = new ArrayList<>();
        if (IMPORT_STYPE_XML.equals(CreateType)) {
            archivalList = captureCreateFailureXml(UserfileName, userFieldCode, userFieldName, importtype);// 创建导入失败excel,返回重复档号/id
        } else if (IMPORT_STYPE_EXCEL.equals(CreateType)) {
            archivalList = captureCreateFailureExcel(UserfileName, userFieldName, userFieldCode, importtype);// 创建导入失败excel,返回重复档号/id
        }
        String zippath = rootpath + "/OAFile" + "/导入失败/" + "/" + UserfileName + ".zip";
        for (int k = 0; k < archivalList.size(); k++) {// 遍历重复档号
            for (int l = 0; l < eleFolder.size(); l++) {// 遍历原文目录
                String[] lib = eleFolder.get(l).replaceAll("\\\\", "/").split("/");
                String libName = lib[lib.length - 1];
                String newArchivalList = archivalList.get(k).replaceAll("\\·", "-");//防止档号中有'.'点号
                if (newArchivalList.trim().equals(libName)) {// 存在同档号名的目录
                    FileUtil.copyDir(eleFolder.get(l), rootpath + "/OAFile" + "/导入失败/"
                            + UserfileName + "/document/" + archivalList.get(k).trim());
                }
            }
        }
                        /*// 复制模板文件到目录下
                        for (int L = 0; L < tempFolder.size(); L++) {
                            String str3 = tempFolder.get(L).replaceAll("\\\\", "/");
                            if (!str3.equals(UnZipFile + "/" + UserfileName + "/document")) {
                                FileUtil.copyDir(tempFolder.get(L),
                                        rootpath + "/OAFile" + "/导入失败/" + UserfileName + "/字段模板信息");
                            }
                        }*/
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(zippath);
            ZipUtils.toZip(rootpath + "/OAFile" + "/导入失败/" + UserfileName, fileOutputStream, true);
            fileOutputStream.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        RepetitionIndex.removeAll(RepetitionIndex);// 清除重复档号
        RepetitionEntryid.removeAll(RepetitionEntryid);
    }


    //FTP导入
    public void importFtp(File[] files, String unzipname) {
        //保存解析xml的数据
        List<Map<String, String>> listMap = new ArrayList<>();
        //保存解析的xml文件名
        List<String> nameList = new ArrayList<>();
        //存放组装后的Entryid对象
        List<Tb_entry_index> swEntryIndex = new ArrayList<>();
        List<Tb_entry_detail> swEntryDetail = new ArrayList<>();
        List<Tb_entry_index> fwEntryIndex = new ArrayList<>();
        List<Tb_entry_detail> fwEntryDetail = new ArrayList<>();
        //获取原文目录集合
        List<String> eleFolder = new ArrayList<>();
        for (File file : files) {
            if (file.getName().endsWith(".xml")) { //xml文件
                //2.进行数据包解析
                Map map = XmlUtil.parseSoapMessage(file);
                //---来保存对应的原文文件夹名
                map.put("原文目录名", file.getName().substring(0, file.getName().lastIndexOf(".")));
                listMap.add(map);
                nameList.add(file.getName());
                eleFolder.add(unzipname + File.separator + file.getName().substring(0, file.getName().lastIndexOf(".")) + File.separator);
            }
        }
        //遍历原文目录
        for (int i = 0; i < eleFolder.size(); i++) {

        }
        String s1 = dataNodeRepository.findnodeidByParentnodename("未归管理", "文书文件");
        String s2 = dataNodeRepository.findnodeidByParentnodeid(s1, "收文");
        String s3 = dataNodeRepository.findnodeidByParentnodeid(s1, "发文");
        //收文下的子节点nodeid--默认第一个
        String swid = dataNodeRepository.findnodeidOrderBynodecode(s2).get(0);
        //发文下的子节点nodeid--默认第一个
        String fwid = dataNodeRepository.findnodeidOrderBynodecode(s3).get(0);


        //存放未归管理节点模板
        List<Tb_data_template> templates = templateRepository.findByNodeid(swid);
        //存放收文和发文value
        List<List<String>> swListvalue = new ArrayList<>();
        List<List<String>> fwListvalue = new ArrayList<>();
        //存放对应value的name
        List<String> fieldname = new ArrayList<>();
        List<String> fieldcode = new ArrayList<>();
        int forcount = 0;
        //组装对象
        for (int i = 0; i < listMap.size(); i++) {//循环每个文件的map
            forcount++;
            List<String> swList = new ArrayList<>();
            List<String> fwList = new ArrayList<>();
            for (int j = 0; j < templates.size(); j++) {//循环节点字段
                if (listMap.get(i).get(templates.get(j).getFieldname()) != null) {
                    if (forcount == 1) {
                        fieldname.add(templates.get(j).getFieldname());//保存name
                        fieldcode.add(templates.get(j).getFieldcode());//保存code
                    }
                    if ("收文".equals(listMap.get(i).get("报文类别"))) {
                        swList.add(listMap.get(i).get(templates.get(j).getFieldname()));
                    } else if ("发文".equals(listMap.get(i).get("报文类别"))) {
                        fwList.add(listMap.get(i).get(templates.get(j).getFieldname()));
                    }
                }
            }
            if (swList.size() > 0) {
                swList.add(listMap.get(i).get("原文目录名"));//-在List集合参数后面添加目录名
                swListvalue.add(swList);
            }
            if (fwList.size() > 0) {
                fwList.add(listMap.get(i).get("原文目录名"));//-在List集合参数后面添加目录名
                fwListvalue.add(fwList);
            }
        }
        fieldname.add("条目id");//占用条目id来保存原文目录名
        fieldcode.add("entryid");//占用条目id来保存原文目录名
        //集合转换成数组
        String[] fieldcodearr = new String[fieldcode.size()];
        fieldcode.toArray(fieldcodearr);
        //获取机构单位节点 --只取默认第一个单位节点
        //获取未归节点
        //根据父节点id和需要查出的子节点名获取节点id
//        String wswjNodeid = dataNodeRepository.findNodeids(wgNode.getNodeid(),"文书文件");
//        String swnodeids = dataNodeRepository.findNodeids(wswjNodeid,"收文");
//        String fwnodeids = dataNodeRepository.findNodeids(wswjNodeid,"发文");
//        String dwName = dataNodeRepository.
        //参数准备完毕 --进行对象组装
        try {
            for (int i = 0; i < swListvalue.size(); i++) {//收文
                Tb_entry_index swindex = ValueUtil.creatEntryIndex(fieldcodearr, swListvalue.get(i));
                Tb_entry_detail swdetail = ValueUtil.creatEntryDetail(fieldcodearr, swListvalue.get(i));
                swEntryIndex.add(swindex);
                swEntryDetail.add(swdetail);
            }
            for (int i = 0; i < fwListvalue.size(); i++) {//发文
                Tb_entry_index fwindex = ValueUtil.creatEntryIndex(fieldcodearr, fwListvalue.get(i));
                Tb_entry_detail fwdetail = ValueUtil.creatEntryDetail(fieldcodearr, fwListvalue.get(i));
                fwEntryIndex.add(fwindex);
                fwEntryDetail.add(fwdetail);
            }
            if (swEntryIndex.size() > 0 && swEntryDetail.size() > 0) {//插入收文
                ftpSaveEntry(swEntryIndex, swEntryDetail, swid, eleFolder);
            }
            if (fwEntryIndex.size() > 0 && fwEntryDetail.size() > 0) {//插入发文
                ftpSaveEntry(fwEntryIndex, fwEntryDetail, fwid, eleFolder);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //批量插入条目
    public List<Tb_entry_index> insertEntryIndex(List<Tb_entry_index> list) {
        List<Tb_entry_index> entryid = new ArrayList<>();
        for (Tb_entry_index index : list) {
            entityManager.persist(index);
        }
        entryid.addAll(list);
        entityManager.flush();
        entityManager.clear();
        return entryid;
    }

    public List<Tb_entry_index_capture> insettEntryIndexCaputre(List<Tb_entry_index_capture> list) {
        List<Tb_entry_index_capture> entryid = new ArrayList<>();
        for (Tb_entry_index_capture index : list) {
            entityManager.persist(index);
        }
        entryid.addAll(list);
        entityManager.flush();
        entityManager.clear();
        return entryid;
    }

    public List<Tb_entry_index_accept> insettEntryIndexAccept(List<Tb_entry_index_accept> list) {
        List<Tb_entry_index_accept> entryid = new ArrayList<>();
        for (Tb_entry_index_accept index : list) {
            entityManager.persist(index);
        }
        entryid.addAll(list);
        entityManager.flush();
        entityManager.clear();
        return entryid;
    }


    /**
     * @param e          异常对象
     * @param keymapList 目标字段-设置字段
     * @param target     节点id
     * @return 异常中文信息
     */
    public String getErroString(Exception e, List<String> keymapList, String target) {
        System.out.println(e.getCause());
        String errStr = e.getCause() == null ? "" : e.getCause().getCause().getMessage();
        String returnMessage = "";
        //源字段模板
        List<String> fieldnames = new ArrayList<>();//用户设置的字段fieldname集合
        List<Integer> fieldSequence = new ArrayList<>();//顺序号
        List<String> names = new ArrayList<>();//源字段名
        //拆分keymapList
        for (int i = 0; i < keymapList.size(); i++) {
            fieldnames.add(keymapList.get(i).split(",")[2]);
            fieldSequence.add(Integer.parseInt(keymapList.get(i).split(",")[0]));
            names.add(keymapList.get(i).split(",")[1]);
        }

        String[] strings = fieldnames.toArray(new String[fieldnames.size()]);//设置的目标字段数组--需要设置为改字段
        //根据源字段名得到源字段code--根据code 插入值到数据库
        List<String> list = getFieldCodeByFieldName(target, strings);
        String[] fieldCodeStr = list.toArray(new String[list.size()]);
        if (errStr != null && errStr.indexOf("Out Of Memory Error") != -1) {
            returnMessage = "数据量过大，出现内存溢出异常";
        }
        if (errStr != null) {
            returnMessage = errStr;
        }
        if (errStr != null && errStr.indexOf("Data too long for column") != -1) {
            String subFieldCode = errStr.substring(errStr.trim().indexOf("'") + 1, errStr.trim().lastIndexOf("'"));
            String name = "";
            for (int i = 0; i < fieldCodeStr.length; i++) {
                if (fieldCodeStr[i].equals(subFieldCode)) {
                    name = names.get(i);
                }
            }
            returnMessage = "字段值过长,字段名:[" + name + "-" + subFieldCode + "]";
        }
        if (errStr != null && errStr.indexOf("Column  not found") != -1) {
            String subFieldCode = errStr.substring(errStr.trim().indexOf("'") + 1, errStr.trim().lastIndexOf("'"));
            String name = "";
            for (int i = 0; i < fieldCodeStr.length; i++) {
                if (fieldCodeStr[i].equals(subFieldCode)) {
                    name = names.get(i);
                }
            }
            returnMessage = "选中的列不存在,列名:[" + name + "-" + subFieldCode + "]";
        }
        return returnMessage;
    }


    public static void main(String[] age) throws Exception {
        /*Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)  //缓存到内存中的行数，默认是10
                .bufferSize(4096)  //读取资源时，缓存到内存的字节大小，默认是1024
                .open(new FileInputStream("f:/111306t.xlsx"));  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件
        for(Sheet sheet:workbook){
            int a=0;
            for(Row row:sheet){
                a++;
            }
            System.out.println(a);
        }*/

    }


    public boolean getOgranid(String nodeid) {
        Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
        if (node.getOrganid() != null) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 档案接收
     * 解析文件---插入数据库
     * <p>
     * 1.根据返回的keymapList 分析出需要导入值的字段（源字段） 与对应的excel中的value
     * 2.读取excel时根据keymapList中的顺序号来存入值
     * 3.根据keymapList中的源字段+excelReadValue 组成成index 和detali 对象 然后插入数据库
     *
     * @param copies   进件份数
     * @param filename 文件名,包含后缀名
     * @param target   目标节点id
     */
    public Map<String, Object> importCalloutEntryData(Integer copies, String filename, String filePath, String target,
                                                      String batchid) throws Exception {
        long num = 0;
        long erro = 0;
        String importState = "";//用作记录zip包中的条目文件类型
        Map<String, Object> map = new HashMap<String, Object>();
        //源字段模板
        List<Integer> fieldSequence = new ArrayList<>();//顺序号

        String[] strcode = new String[3];
        String[] strname = new String[3];
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                strcode[i] = "archivecode";
                strname[i] = "档号";
                fieldSequence.add(0);
            } else if (i == 1) {
                strcode[i] = "tracktext";
                strname[i] = "字轨";
                fieldSequence.add(1);
            } else if (i == 2) {
                strcode[i] = "tracknumber";
                strname[i] = "案号";
                fieldSequence.add(2);
            }
        }
        //根据条目文件类型进行解析
        Map<String, Integer> reMap = null;
        List<String> eleFolder = new ArrayList<>();
        if (filename.endsWith(".xls") || filename.endsWith(".xlsx")) {//excel
            importState = "Excel";
            int lastRows = 0;
            if (filename.endsWith(".xlsx")) {
                Workbook workbook = StreamingReader.builder()
                        .rowCacheSize(100)  //缓存到内存中的行数，默认是10
                        .bufferSize(4096)  //读取资源时，缓存到内存的字节大小，默认是1024
                        .open(new FileInputStream(new File(filePath)));  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件

                for (Sheet sheet : workbook) {
                    lastRows += sheet.getPhysicalNumberOfRows();
                }
            } else {
                Workbook workbook = WorkbookFactory.create(new FileInputStream(new File(filePath)));
                for (Sheet sheet : workbook) {
                    lastRows += sheet.getPhysicalNumberOfRows();
                }
            }
            Integer exist = szhCalloutEntryRepository.countByBatchcode(batchid);
            if (copies.intValue() < lastRows - 1 + exist) {
                return null;
            }
            String impState = filename.substring(filename.lastIndexOf("."));
            reMap = readCalloutExcel(lastRows, filePath, fieldSequence, strcode, target, importState, batchid);//解析excle 并插入数据库
            num = reMap.get("num");
            erro = reMap.get("erro");
        }
        //生成导入失败文件
        if (erro > 0) {
            if (IMPORT_STYPE_EXCEL.equals(importState)) {
                CreateExcel.createCalloutErroExcel("导入失败-" + filename.substring(0, filename.lastIndexOf(".")), rebackImportList, strcode, strname);
            } else if (IMPORT_STYPE_XML.equals(importState)) {
                XmlUtil.CreateCalloutFailureXml(strname, strcode, rebackImportList, "导入失败-" + filename.substring(0, filename.lastIndexOf(".")));
            } else if (IMPORT_STYPE_ZIP_EXCEL.equals(importState)) {
                createCalloutErroFile(strname, strcode, "导入失败-" + filename.substring(0, filename.lastIndexOf(".")), eleFolder, "Excel");
            } else if (IMPORT_STYPE_ZIP_XML.equals(importState)) {
                createCalloutErroFile(strname, strcode, "导入失败-" + filename.substring(0, filename.lastIndexOf(".")), eleFolder, "Xml");
            }
            rebackImportList.removeAll(rebackImportList);
        }
        //删除文件
        FileUtil.delFolder(filePath);
        map.put("num", num);
        map.put("error", erro);
        return map;
    }

    private Map<String, Integer> readCalloutExcel(int lastRows, String file, List<Integer> fieldSequence, String[] strcode, String target, String impState, String batchid)
            throws Exception {
        int count = 0;
        Map<String, Integer> reMap = new HashMap<>();
        //---创建workbook对象
        Workbook workbook = null;
        if ("xlsx".equals(impState)) {
            workbook = StreamingReader.builder()
                    .rowCacheSize(100)  //缓存到内存中的行数，默认是10
                    .bufferSize(4096)  //读取资源时，缓存到内存的字节大小，默认是1024
                    .open(new FileInputStream(new File(file)));  //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件
        } else {
            //workbook= new HSSFWorkbook(new FileInputStream(new File(file)));
            workbook = WorkbookFactory.create(new FileInputStream(new File(file)));
        }
        int remainder = lastRows % 400;
        for (Sheet sheet : workbook) {
            if (sheet == null) {
                continue;
            }
            List<Szh_entry_index_capture> indexList = new ArrayList<>();
            List<Szh_entry_detail_capture> details = new ArrayList<>();
            List<Szh_callout_entry> callout_entries = new ArrayList<>();
            List sheetRowsValue = new ArrayList();
            int rowCount = 0;//记录循环行数
            int bRow = 0;//用来判断--去列头
            int sheetLastRowNum = sheet.getLastRowNum();
            for (Row row : sheet) {
                rowCount++;
                //跳过列头行
                if (bRow == 0) {
                    bRow++;
                    continue;
                }
                List<String> rowCellsValue = new ArrayList<>();
                int maxcol = row.getLastCellNum();
                for (int col = 0; col < maxcol; col++) {//循环列单元格，获取值
                    Cell cell = row.getCell(col);

                    //只保存对应顺序号的值---顺序号是从小到大顺序--（0,1,5,6）类型
                    for (int i = 0; i < fieldSequence.size(); i++) {
                        if (fieldSequence.get(i) == col) {
                            if (cell == null) {
                                rowCellsValue.add("");
                                continue;
                            }
                            String floatValue = null;
                            if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {//解决浮点类型精度丢失问题
                                String floatStr = cell.getNumericCellValue() + "";
                                floatValue = floatStr.indexOf(".") > -1 ? floatStr : null;
                            }
                            rowCellsValue.add(floatValue != null ? floatValue : ReadExcel.getStringValnew(cell));
                        }
                    }
                }
                sheetRowsValue.add(rowCellsValue);
                if (rowCount == lastRows) {//最大循环时--
                    indexList = ListTransformCalloutEntryIndex(sheetRowsValue, strcode, target);
                    callout_entries = ListTransformCalloutEntry(sheetRowsValue, strcode, target, batchid);
                    saveCalloutEntry(indexList, target, callout_entries);
                    count = indexList.size() + count;
                    sheetRowsValue.removeAll(sheetRowsValue);
                } else if (rowCount % 400 == 0 && rowCount >= 400) {//400循环一次
                    indexList = ListTransformCalloutEntryIndex(sheetRowsValue, strcode, target);
                    callout_entries = ListTransformCalloutEntry(sheetRowsValue, strcode, target, batchid);
                    saveCalloutEntry(indexList, target, callout_entries);
                    count = indexList.size() + count;
                    sheetRowsValue.removeAll(sheetRowsValue);
                }
            }
        }
        int errocount = lastRows - 1 - count;
        reMap.put("num", lastRows - 1);
        reMap.put("erro", errocount);
        return reMap;
    }

    private void createCalloutErroFile(String[] userFieldName, String[] userFieldCode, String UserfileName, List<String> eleFolder, String CreateType) throws Exception {
        List<String> archivalList = new ArrayList<>();
        if (IMPORT_STYPE_XML.equals(CreateType)) {
            archivalList = createCalloutFailureXml(UserfileName, userFieldCode, userFieldName);// 创建导入失败excel,返回重复档号/id
        } else if (IMPORT_STYPE_EXCEL.equals(CreateType)) {
            archivalList = CreateCalloutFailureExcel(UserfileName, userFieldName, userFieldCode);// 创建导入失败excel,返回重复档号/id
        }
        String zippath = rootpath + "/OAFile" + "/导入失败/" + "/" + UserfileName + ".zip";
        for (int k = 0; k < archivalList.size(); k++) {// 遍历重复档号
            for (int l = 0; l < eleFolder.size(); l++) {// 遍历原文目录
                String[] lib = eleFolder.get(l).replaceAll("\\\\", "/").split("/");
                String libName = lib[lib.length - 1];
                String newArchivalList = archivalList.get(k).replaceAll("\\·", "-");//防止档号中有'.'点号
                if (newArchivalList.trim().equals(libName)) {// 存在同档号名的目录
                    FileUtil.copyDir(eleFolder.get(l), rootpath + "/OAFile" + "/导入失败/"
                            + UserfileName + "/document/" + archivalList.get(k).trim());
                }
            }
        }
                        /*// 复制模板文件到目录下
                        for (int L = 0; L < tempFolder.size(); L++) {
                            String str3 = tempFolder.get(L).replaceAll("\\\\", "/");
                            if (!str3.equals(UnZipFile + "/" + UserfileName + "/document")) {
                                FileUtil.copyDir(tempFolder.get(L),
                                        rootpath + "/OAFile" + "/导入失败/" + UserfileName + "/字段模板信息");
                            }
                        }*/
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(zippath);
            ZipUtils.toZip(rootpath + "/OAFile" + "/导入失败/" + UserfileName, fileOutputStream, true);
            fileOutputStream.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        RepetitionIndex.removeAll(RepetitionIndex);// 清除重复档号
        RepetitionEntryid.removeAll(RepetitionEntryid);
    }

    // 转换数据-判断重复
    public List<Szh_entry_index_capture> ListTransformCalloutEntryIndex(List<List<String>> lists, String[] fieldcods, String nodeid)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            SecurityException, NoSuchFieldException, ParseException {
        List<Szh_entry_index_capture> indexList = new ArrayList<>();
        nodeid = String.format("%1$-36s", (String) nodeid);
        List<Szh_RebackImport> rebackImports = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            Szh_entry_index_capture index = ValueUtil.creatCalloutEntryIndex(fieldcods, lists.get(i));
            Szh_callout_entry callout_entry = ValueUtil.creatCalloutEntry(fieldcods, lists.get(i));
            Szh_RebackImport rebackImport = new Szh_RebackImport();
            rebackImport.setArchivecode(index.getArchivecode());
            //rebackImport.setTracktext(callout_entry.getTracktext());
            //rebackImport.setTracknumber(callout_entry.getTracknumber());
            List<Tb_codeset> codesets = codesetRepository.findByDatanodeidOrderByOrdernum(nodeid);
            String Archivecode = index.getArchivecode();
            if (codesets.size() > 1) {// 多个档号组成字段
                // 使用档号字段和节点查重
                long count = szhEntryIndexCaptureRepository.findEntryidCount(index.getArchivecode(), nodeid);
                if (count < 1) { // 不重复
                    indexList.add(index);
                } else {
                    rebackImports.add(rebackImport);
                    RepetitionIndex.add(index.getArchivecode());
                }
            } else if (codesets.size() == 1) {// 一个档号组成字段
                // 获取条目数据中对应字段的数据
                String fieldData = GainField.getFieldValueByName(codesets.get(codesets.size() - 1).getFieldcode(),
                        index) + "";
                String sql = "select count(nodeid) from szh_entry_index_capture where "
                        + codesets.get(codesets.size() - 1).getFieldcode() + "=" + "'" + fieldData + "'"
                        + " and NODEID='" + nodeid + "'";
                Query query = entityManager.createNativeQuery(sql);
                int counts = query.getSingleResult() == null ? 0 : Integer.valueOf(query.getSingleResult().toString());
                if (counts < 1) {// 不重复
                    indexList.add(index);
                } else {// 重复
                    rebackImports.add(rebackImport);
                    RepetitionEntryid.add(index.getEntryid());
                }
            } else if (codesets.size() == 0) {// 没有设置档号字段
                // 不需要查重
                indexList.add(index);
            }
        }
        rebackImportList.addAll(rebackImports);
        return indexList;// 返回没有重复档号的条目
    }

    // 转换数据-判断重复
    public List<Szh_callout_entry> ListTransformCalloutEntry(List<List<String>> lists, String[] fieldcods, String nodeid, String batchid)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            SecurityException, NoSuchFieldException, ParseException {
        List<Szh_callout_entry> indexList = new ArrayList<>();
        nodeid = String.format("%1$-36s", (String) nodeid);
        Szh_archives_callout archives_callout = szhArchivesCalloutRepository.findByBatchcode(batchid);
        Szh_assembly assembly = szhAssemblyRepository.findByCode(archives_callout.getAssemblycode());
        List<Szh_assembly_flows> flows = szhAssemblyFlowsRepository.getFlowsByassemblyid(assembly.getId());
        for (int i = 0; i < lists.size(); i++) {
            Szh_entry_index_capture index = ValueUtil.creatCalloutEntryIndex(fieldcods, lists.get(i));
            Szh_callout_entry entry = ValueUtil.creatCalloutEntry(fieldcods, lists.get(i));
            String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 系统绑定对象(全局)
            entry.setWorkstate("未处理");//设置工作状态
            entry.setLendstate("已借出");//设置借出状态
            entry.setEntrysigner(userDetails.getRealname());//设置实体签收人
            entry.setEntrysigncode(userDetails.getLoginname());//设置实体签收工号
            entry.setEntrysigntime(nowTime);//设置实体签收时间
            entry.setEntrysignorgan(userDetails.getOrganid());//设置签收单位
            entry.setEntrysigntype("已签收");//设置实体签收状态
            entry.setA0(0);//设置A0页数
            entry.setA1(0);//设置A1页数
            entry.setA2(0);//设置A2页数
            entry.setA3(0);//设置A3页数
            entry.setA4(0);//设置A4页数
            entry.setZa4(0);//设置折算A4页数
            entry.setBatchcode(batchid);
            //entry = archivesCalloutService.saveCalloutEntryState(flows,entry,assembly.getId());  //根据流水线配置以及环节前置条件设置环节状态
            List<Tb_codeset> codesets = codesetRepository.findByDatanodeidOrderByOrdernum(nodeid);
            if (codesets.size() > 1) {// 多个档号组成字段
                // 使用档号字段和节点查重
                long count = szhEntryIndexCaptureRepository.findEntryidCount(index.getArchivecode(), nodeid);
                if (count < 1) { // 不重复
                    indexList.add(entry);
                }
            } else if (codesets.size() == 1) {// 一个档号组成字段
                // 获取条目数据中对应字段的数据
                String fieldData = GainField.getFieldValueByName(codesets.get(codesets.size() - 1).getFieldcode(),
                        index) + "";
                String sql = "select count(*) from szh_entry_index_capture where "
                        + codesets.get(codesets.size() - 1).getFieldcode() + "=" + "'" + fieldData + "'"
                        + " and NODEID='" + nodeid + "'";
                Query query = entityManager.createNativeQuery(sql);
                int counts = query.getSingleResult() == null ? 0 : Integer.valueOf(query.getSingleResult().toString());
                if (counts < 1) {// 不重复
                    indexList.add(entry);
                }
            } else if (codesets.size() == 0) {// 没有设置档号字段
                // 不需要查重
                indexList.add(entry);
            }
        }
        return indexList;// 返回没有重复档号的条目
    }

    // 创建导入失败的xml
    public List<String> createCalloutFailureXml(String fileName, String[] fieldcode, String[] fieldname)
            throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
        List<String> list = new ArrayList<>();
        if (RepetitionIndex.size() > 0) {
            list = RepetitionIndex;
            // xmlutil.createFailureXml(RepetitionIndex, fileName);
            XmlUtil.CreateCalloutFailureXml(fieldname, fieldcode, rebackImportList, fileName);
        } else if (RepetitionEntryid.size() > 0) {
            list = RepetitionEntryid;
            // xmlutil.createFailureXml(RepetitionEntryid, fileName);
            XmlUtil.CreateCalloutFailureXml(fieldname, fieldcode, rebackImportList, fileName);
        }
        return list;
    }

    //保存数字化条目
    private void saveCalloutEntry(List<Szh_entry_index_capture> indexList, String nodeid, List<Szh_callout_entry> callout_entries) {
        if (indexList.size() > 0) {
            for (int i = 0; i < indexList.size(); i++) {
                indexList.get(i).setNodeid(nodeid);
            }
            for (int i = 0; i < callout_entries.size(); i++) {
                callout_entries.get(i).setNodeid(nodeid);
            }
            List<Szh_entry_index_capture> bb = szhEntryIndexCaptureRepository.save(indexList);
            List<Szh_callout_entry> callentrys = szhCalloutEntryRepository.save(callout_entries);
            List<Szh_callout_capture> callout_captures = new ArrayList<>();
            List<Szh_entry_track> entry_tracks = new ArrayList<>();
            List<Szh_entry_detail_capture> detail_captures = new ArrayList<>();
            for (int i = 0; i < callentrys.size(); i++) {
                Szh_entry_detail_capture detail = new Szh_entry_detail_capture();
                detail.setEntryid(bb.get(i).getEntryid());
                //detail.setF49(callentrys.get(i).getTracktext());
                //detail.setF50(callentrys.get(i).getTracknumber());
                detail_captures.add(detail);

                //保存调出条目与临时数据对应信息
                Szh_callout_capture callout_capture = new Szh_callout_capture();
                callout_capture.setEntryid(bb.get(i).getEntryid());
                callout_capture.setCalloutid(callentrys.get(i).getId());
                callout_captures.add(callout_capture);

                //修改元数据默认数据
                List<Szh_media_metadata> metadatas = szhMediaMetadataRepository.findByArchivecode(callentrys.get(i).getArchivecode());
//                if(metadatas.size()>0){
//                    for(Szh_media_metadata metadata: metadatas){
//                        metadata.setMetadata();//初始化默认数据
//                    }
//                }

                //增加实物流向追踪
                Szh_entry_track track = new Szh_entry_track();
                track.setEntryid(callentrys.get(i).getId());
                track.setBatchcode(callentrys.get(i).getBatchcode());
                track.setArchivecode(callentrys.get(i).getArchivecode());
                track.setEntrysigner(callentrys.get(i).getEntrysigner());
                track.setNodename("进件登记(批量导入)");
                track.setStatus("已签收");
                track.setEntrysigntime(callentrys.get(i).getEntrysigntime());
                entry_tracks.add(track);

            }
            szhEntryDetailCaptureRepository.save(detail_captures);
            szhEntryTrackRepository.save(entry_tracks);
            szhCalloutCaptureRepository.save(callout_captures);
        }
    }

    // 创建导入失败的excel
    public List<String> CreateCalloutFailureExcel(String fileName, String[] fieldname, String[] fieldcode)
            throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, IOException, NoSuchFieldException {
        List<String> list = new ArrayList<>();
        if (RepetitionIndex.size() > 0) {
            list = RepetitionIndex;
            CreateExcel.createCalloutErroExcel(fileName, rebackImportList, fieldcode, fieldname);
        } else if (RepetitionEntryid.size() > 0) {
            list = RepetitionEntryid;
            CreateExcel.createCalloutErroExcel(fileName, rebackImportList, fieldcode, fieldname);
        }
        return list;
    }


    //------------------------------------------bs xml-------------------------//
    public Map importBsXml(String filename, String filePath, String target,
                           List<String> keymapList, String isRepeat, String wg11Index) throws Exception {
        //1.bs的数据包格式  1个zip包中有1个xml文件 以及条目的电子原文也是zip包
        long num = 0;
        long erro = 0;
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String importState = "";//用作记录zip包中的条目文件类型
        Map<String, Object> map = new HashMap<String, Object>();
        List<Tb_entry_index> tb_entry_indexList = new ArrayList<>();
        List<Tb_entry_detail> entry_details = new ArrayList<>();
        //源字段模板
        List<String> fieldnames = new ArrayList<>();//用户设置的字段fieldname集合
        List<Integer> fieldSequence = new ArrayList<>();//顺序号
        List<String> names = new ArrayList<>();//源字段名
        //拆分keymapList
        for (int i = 0; i < keymapList.size(); i++) {
            fieldnames.add(keymapList.get(i).split(",")[2]);
            fieldSequence.add(Integer.parseInt(keymapList.get(i).split(",")[0]));
            names.add(keymapList.get(i).split(",")[1]);
        }
        String[] strings = fieldnames.toArray(new String[fieldnames.size()]);//设置的目标字段数组--需要设置为改字段
        String[] namesString = names.toArray(new String[names.size()]);//-源字段名
        //根据源字段名得到源字段code--根据code 插入值到数据库
        List<String> list = getFieldCodeByFieldName(target, strings);
        String[] fieldCodeStr = list.toArray(new String[list.size()]);
        String[] strcode;
        String[] strname;
        strcode = new String[fieldCodeStr.length + 1];
        strname = new String[namesString.length + 1];
        strname[strname.length - 1] = "wg11";
        strcode[strcode.length - 1] = "entryid";
        System.arraycopy(fieldCodeStr, 0, strcode, 0, fieldCodeStr.length);
        System.arraycopy(namesString, 0, strname, 0, namesString.length);
        //存放电子原文集合
        List eleFolder = new ArrayList();
        //获取oa的xml文件
        String oaxml = "a";
        File[] files = new File(filePath).listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith(".xml")) {
                oaxml = files[i].getPath();
            } else if (files[i].getName().endsWith(".zip")) {
                //原文zip包 现在进行解压
                String UnZipPath = files[i].getPath().substring(0, files[i].getPath().lastIndexOf("."));
                ZipUtils.deCompress(files[i].getPath(), UnZipPath,"UTF-8");//解压文件
                //判断是否乱码
                boolean b = FileUtil.isMessyCode(new File(UnZipPath).listFiles());
                if (b) {
                    //删除原先的解压目录
                    FileUtil.delFolder(UnZipPath);
                    //更换编码进行解压
                    ZipUtils.deCompress(files[i].getPath(), UnZipPath, "GBK");//解压文件
                    //重新获取文件
                }
                eleFolder.add(UnZipPath);
            }
        }

        fieldSequence.add(Integer.parseInt(wg11Index));
        //
        Map<String, Integer> reMap = null;
        if (oaxml.endsWith(".xml")) {
            reMap = readXml(oaxml, fieldSequence, strcode, target, isRepeat, eleFolder, false,false);
            num = reMap.get("num");
            erro = reMap.get("erro");
        }
        map.put("num", num);
        map.put("error", erro);
        //此处进行保存导入记录
        Tb_imp_record record = new Tb_imp_record();
        record.setImpuser(userDetails.getUsername());
        record.setSuccesscount(String.valueOf(num));
        record.setDefeatedcount(String.valueOf(erro));
        record.setImptime(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        record.setImptype("管理");
        impRecord.save(record);
        return map;
    }

    //------------------------------------------社保中心xml-------------------------//
    public Map importsocialSecurityXml(String filename, String filePath, String target,
                                       List<String> keymapList, String isRepeat, String wg11Index) {
        //1.
        int num = 0;
        int erro = 0;
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String importState = "";//用作记录zip包中的条目文件类型
        Map<String, Object> map = new HashMap<String, Object>();
        List<Tb_entry_index> tb_entry_indexList = new ArrayList<>();
        List<Tb_entry_detail> entry_details = new ArrayList<>();
        //源字段模板
        List<String> fieldnames = new ArrayList<>();//用户设置的字段fieldname集合
        List<Integer> fieldSequence = new ArrayList<>();//顺序号
        List<String> names = new ArrayList<>();//源字段名
        //拆分keymapList
        for (int i = 0; i < keymapList.size(); i++) {
            fieldnames.add(keymapList.get(i).split(",")[2]);
            fieldSequence.add(Integer.parseInt(keymapList.get(i).split(",")[0]));
            names.add(keymapList.get(i).split(",")[1]);
        }
        String[] strings = fieldnames.toArray(new String[fieldnames.size()]);//设置的目标字段数组--需要设置为改字段
        String[] namesString = names.toArray(new String[names.size()]);//-源字段名
        //根据源字段名得到源字段code--根据code 插入值到数据库
        List<String> list = getFieldCodeByFieldName(target, strings);
        String[] fieldCodeStr = list.toArray(new String[list.size()]);
        String[] strcode;
        String[] strname;
        strcode = new String[fieldCodeStr.length + 1];
        strname = new String[namesString.length + 1];
        strname[strname.length - 1] = "wg11";
        strcode[strcode.length - 1] = "entryid";
        System.arraycopy(fieldCodeStr, 0, strcode, 0, fieldCodeStr.length);
        System.arraycopy(namesString, 0, strname, 0, namesString.length);
        //存放电子原文集合
        List<String> eleFolder = new ArrayList();
        //存放需要解析的xml集合
        List<String> xmlFiles = new ArrayList();
        //添加wg11字段的关联 通过设为entrtid来对应电子文件
        fieldSequence.add(Integer.parseInt(wg11Index));
        //Collections.sort(fieldSequence);
        File[] outerFiles = new File(filePath).listFiles();
        for (File file : outerFiles) {
            if (file.exists()) {
                File[] withinFiles = file.listFiles();
                if (null == withinFiles) {
                    continue;
                }
                for (File withinFile : withinFiles) {
                    if (withinFile.isDirectory()) {
                        eleFolder.add(withinFile.toString());
                    }
                    if (withinFile.getName().indexOf("yjrz") == -1 && withinFile.isFile()) {//判断 文件不是2 并且不是文件夹
                        xmlFiles.add(withinFile.toString());
                    }
                }
            }
        }
        Map<String, Integer> reMap = null;
        for (int i = 0; i < xmlFiles.size(); i++) {
            reMap = readSocialSecurityXml(xmlFiles.get(i), fieldSequence, strcode, target, isRepeat, eleFolder, false);
            num = num + reMap.get("num");
            erro = erro + reMap.get("erro");
        }
        Map<String, Integer> newMap = new HashMap<>();
        newMap.put("num",num);
        newMap.put("error",erro);
        return newMap;
    }


    private Map<String, Integer> readSocialSecurityXml(String file, List<Integer> fieldSequence, String[] strcode,
                                                       String target, String isRepeat, List<String> eleFolder, boolean isEntityStorage) {
        //1.创建sax对象
        SAXReader reader = new SAXReader();
        List<List<String>> lists = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        List<Tb_entry_index> indexList = null;
        List<Tb_entry_detail> details = null;
        try {
            //通过reader对象加载xml文件，生成document对象
            Document document = reader.read(new File(file));
            //通过document对象获取跟节点
            Element element = document.getRootElement();
            //获取table下面的子节点
            List<Element> elements = element.elements();
            for (int i = 0; i < elements.size(); i++) {
                List<String> list = new ArrayList<>();
                List<Element> list1 = elements.get(i).elements();
                for (int j = 0; j < list1.size(); j++) {
                    if ("RECORD".equalsIgnoreCase(list1.get(j).getName())) {// record
                        List<Element> list2 = list1.get(j).elements();
                        for(int a=0;a<fieldSequence.size();a++){//循环顺序号
                            for (int k = 0; k < list2.size(); k++){//循环record下面的子标签
                                if(fieldSequence.get(a)==k){//保存相同顺序号的标签值
                                    list.add(list2.get(k).getStringValue());
                                }
                            }
                        }
                    }
                }
                lists.add(list);
            }

            //拿到解析结果 进行组装
            indexList = ListTransformEntryIndex(lists, strcode, target, isRepeat);
            details = ListTransformEntryDetail(lists, strcode);
            saveEntryAndEleFolder(indexList, details, target, eleFolder, isRepeat, isEntityStorage, false);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("num", lists.size());
        map.put("erro", lists.size() - indexList.size());
        return map;
    }

    private Tb_entry_index alignArchivecode(Tb_entry_index entry, String operate) {
        String nodeid = entry.getNodeid();
        // 处理需对齐字段
        List<String> alignFieldList = new ArrayList<>();
        List<String> codeSettingFields = new ArrayList<>();
        List<String> codeSettingSplits = new ArrayList<>();
        List<Tb_codeset> codesetList = codesettingService.findCodesetByDatanodeid(nodeid);
        codesetList.forEach(codeset -> {
            codeSettingFields.add(codeset.getFieldcode());
            codeSettingSplits.add(codeset.getSplitcode());
            alignFieldList.add(codeset.getFieldcode() + "∪" + codeset.getFieldlength());
        });
        if (codeSettingSplits.size() > 1) {
            codeSettingSplits.remove(codeSettingSplits.size() - 1);
        }
        // 执行对齐操作
        String archivecode = "";
        for (int i = 0; i < alignFieldList.size(); i++) {// 档号构成字段值补0
            String alignField = alignFieldList.get(i);
            String[] alignFieldStrs = alignField.split("∪");
            String alignFieldcode = alignFieldStrs[0];
            Integer alignFieldlength = Integer.parseInt(alignFieldStrs[1]);// 档号设置的单位长度
            String alignFieldValue = GainField.getFieldValueByName(alignFieldcode, entry) != null
                    ? (String) GainField.getFieldValueByName(alignFieldcode, entry) : "";
            String alignedFieldValue = "";
            if ("".equals(alignFieldValue) || alignFieldValue == null) {
                return null;
            }
            if (isNumeric(alignFieldValue)) {
                int currentFieldlength = alignFieldValue.length();// 字段值当前的长度
                if (alignFieldlength != currentFieldlength && alignFieldValue.length() > 0) {
                    alignedFieldValue = entryIndexService.alignValue(alignFieldlength,
                            Integer.valueOf(alignFieldValue));
                    GainField.setFieldValueByName(alignFieldcode, entry, alignedFieldValue);
                    // GainField.setFieldValueByName(alignFieldcode,
                    // entry.getEntryIndex(), alignedFieldValue);
                }
            }
        }
        for (int i = 0; i < codeSettingFields.size() - 1; i++) {// 重新生成档号
            String field = codeSettingFields.get(i);
            String codeSettingFieldValue = GainField.getFieldValueByName(codeSettingFields.get(i), entry) != null
                    ? (String) GainField.getFieldValueByName(codeSettingFields.get(i), entry) : "";
            if ("".equals(codeSettingFieldValue)) {
                return null;
            } else {
                // 如果是机构名称
                String type = templateRepository.findOrganFtypeByNodeid(nodeid);
                if (field.equals("organ") && type.equals("string") && type != null) {
                    Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
                    Tb_right_organ right_organ = rightOrganRepository.findByOrganid(node.getRefid());
                    if (right_organ.getCode() != null && !right_organ.getCode().equals("")) {
                        archivecode += right_organ.getCode() + codeSettingSplits.get(i);
                    } else {
                        archivecode += codeSettingFieldValue + codeSettingSplits.get(i);
                    }
                } else if (field.equals("entryretention")) {
                    List<String> list = systemConfigRepository.findConfigvalueByConfigcode(codeSettingFieldValue);
                    if (list.size() == 0) {
                        archivecode += codeSettingFieldValue + codeSettingSplits.get(i);
                    } else {
                        archivecode += list.get(0) + codeSettingSplits.get(i);
                    }
                } else {
                    archivecode += codeSettingFieldValue + codeSettingSplits.get(i);
                }
            }
        }
        String calFieldvalue = "";
        if (codeSettingFields.size() >= 1) {
            String calFieldcode = codeSettingFields.get(codeSettingFields.size() - 1);
            calFieldvalue = GainField.getFieldValueByName(calFieldcode, entry) != null
                    ? (String) GainField.getFieldValueByName(calFieldcode, entry) : "";
            if ("".equals(calFieldvalue) && !operate.equals("未归管理")) {
                return null;
            }
        }
        archivecode += calFieldvalue;
        entry.setArchivecode(archivecode);
        // entry.getEntryIndex().setArchivecode(archivecode);
        return entry;
    }

    private boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}
