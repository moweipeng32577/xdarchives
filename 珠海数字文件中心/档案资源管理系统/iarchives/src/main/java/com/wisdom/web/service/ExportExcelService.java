package com.wisdom.web.service;

import com.alibaba.fastjson.JSON;
import com.wisdom.secondaryDataSource.repository.SxDataNodeRepository;
import com.wisdom.util.*;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.util.InternalZipConstants;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.checkerframework.checker.units.qual.A;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by SunK on 2018/8/1 0001.
 */
@Service
@Transactional
public class ExportExcelService {


    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    EntryDetailRepository entryDetailRepository;

    @Autowired
    ElectronicRepository electronicRepository;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    SxDataNodeRepository sxDataNodeRepository;

    @Autowired
    EntryIndexCaptureRepository entryIndexCaptureRepository;

    @Autowired
    EntryDetailCaptureRepository entryDetailCaptureRepository;

    @Autowired
    ElectronicCaptureRepository electronicCaptureRepository;

    @Autowired
    TemplateService templateService;

    @Autowired
    SzhEntryIndexCaptureRepository shzEntryIndexCaptureRepository;

    @Autowired
    EntryIndexAcceptRepository entryIndexAcceptRepository;

    @Autowired
    EntryDetailAcceptRepository entryDetailAcceptRepository;

    @Autowired
    EntryIndexManageRepository entryIndexManageRepository;

    @Autowired
    EntryDetailManageRepository entryDetailManageRepository;

    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    RightOrganRepository rightOrganRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReserveRepository reserveRepository;

    @Autowired
    LongRetentionService longRetentionService;

    @Autowired
    EquipmentRepository equipmentRepository;

    @PersistenceContext
    EntityManager entityManager;


    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录

    public static final int ID_CAPTURE_COUNT = 2000;
    public static final long FILE_SIZE=10;

    public static List<String[]> splitAry(String[] arr, int limit) {
        int arrlen = arr.length;
        //拆分次数
        int count = arrlen % limit == 0 ? arrlen / limit : arrlen / limit + 1;

        List<List<String>> subAryList = new ArrayList<>();

        for (int i = 0; i < count; i++) {//分组
            int index = i * limit;
            List<String> list = new ArrayList<>();
            int j = 0;
            while (j < limit && index < arr.length) {
                list.add(arr[index++]);
                j++;
            }
            subAryList.add(list);
        }
        List<String[]> list1 = new ArrayList<>();
        for (int k = 0; k < subAryList.size(); k++) {
            String[] str = subAryList.get(k).toArray(new String[subAryList.get(k).size()]);
            list1.add(str);
        }
        return list1;

    }

    public List<Tb_entry_detail> getEntryDetailList(String[] ids) {
        List<Tb_entry_detail> entry_details = new ArrayList<>();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {
                List<Tb_entry_detail> list = entryDetailRepository.findByEntryidIn(listStr.get(i));
                for (int j = 0; j < list.size(); j++) {
                    entry_details.add(list.get(j));
                }
            }
        }
        return entry_details;
    }

    public List<Tb_entry_index> getEntryIndexList(String[] ids) {
        List<Tb_entry_index> entry_indices = new ArrayList<>();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {
                List<Tb_entry_index> list = entryIndexRepository.findByEntryidIn(listStr.get(i));
                for (int j = 0; j < list.size(); j++) {
                    entry_indices.add(list.get(j));
                }
            }
        }
        return entry_indices;
    }

    /**
     * 生成excel
     *
     * @param ids     条目id集合
     * @param
     * @param strcode code集合
     * @param strname 字段名集合
     * @return 返回workbook对象
     * @throws Exception
     */
    public SXSSFWorkbook SXSSFCreatExcel(String[] ids, String[] strcode, String[] strname ,String nodeid) throws Exception {
        //1.创建excel文件---用在保存循环的数据--循环写入
        //2.创建工作簿  SXSSFWorkbook 支持最大行1048576
        SXSSFWorkbook workbook = new SXSSFWorkbook(10);
        Sheet sheet = workbook.createSheet();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 1500);
            for (int i = 0; i < listStr.size(); i++) {//每个元素都是进行截取过的 900条id
                int start=i*1500;
                int end=(start+1500)>ids.length?ids.length:(start+1500);
                List<Tb_entry_index> list;
                list = entryIndexRepository.findByEntryidIn(listStr.get(i));
                List<Entry> entryList = createEntrtList(list,listStr.get(i));
                //每900条写入1次
                sheet = CreateExcel.SXSSFWorkbookCreateExcle(sheet, entryList, strcode, strname);
            }
        }
        return workbook;
    }

    /**
     * 生成预约管理excel
     * @param ids
     * @param strcode
     * @param strname
     * @return
     * @throws Exception
     */
    public SXSSFWorkbook rservationSXSSFCreatExcel(String[] ids, String[] strcode, String[] strname) throws Exception {
        //1.创建excel文件---用在保存循环的数据--循环写入
        //2.创建工作簿  SXSSFWorkbook 支持最大行1048576
        SXSSFWorkbook workbook = new SXSSFWorkbook(10);
        Sheet sheet = workbook.createSheet();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {//每个元素都是进行截取过的 900条id
                List<Tb_reserve> list = reserveRepository.findByDocidIn(listStr.get(i));
                //每900条写入1次
                sheet = CreateExcel.reservationSXSSFWorkbookCreateExcle(sheet, list, strcode, strname);
            }
        }
        return workbook;
    }

    /**
     * 生成设备管理excel
     * @param ids
     * @param strcode
     * @param strname
     * @return
     * @throws Exception
     */
    public SXSSFWorkbook equipmentSXSSFCreatExcel(String[] ids, String[] strcode, String[] strname) throws Exception {
        //1.创建excel文件---用在保存循环的数据--循环写入
        //2.创建工作簿  SXSSFWorkbook 支持最大行1048576
        SXSSFWorkbook workbook = new SXSSFWorkbook(10);
        Sheet sheet = workbook.createSheet();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {//每个元素都是进行截取过的 900条id
                List<Tb_equipment> list = equipmentRepository.findByEquipmentIDIn(listStr.get(i));
                //每900条写入1次
                sheet = CreateExcel.equipmentSXSSFWorkbookCreateExcle(sheet, list, strcode, strname);
            }
        }
        return workbook;
    }

    /**
     * 全文检索生成excel
     * @param ids
     * @param strcode
     * @param strname
     * @return
     * @throws Exception
     */
    public SXSSFWorkbook originalSXSSFCreatExcel(String[] ids, String[] strcode, String[] strname) throws Exception {
        //1.创建excel文件---用在保存循环的数据--循环写入
        //2.创建工作簿  SXSSFWorkbook 支持最大行1048576
        SXSSFWorkbook workbook = new SXSSFWorkbook(10);
        Sheet sheet = workbook.createSheet();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {//每个元素都是进行截取过的 900条id
                String sql = "select ei.archivecode as archivecode,el.* from tb_electronic el inner join tb_entry_index ei on el.entryid=ei.entryid where el.eleid in('" + String.join("','", listStr.get(i)) + "') ";
                Query query = entityManager.createNativeQuery(sql,OriginalExcel.class);
                List<OriginalExcel> originalExcels=query.getResultList();
                //每900条写入1次
                sheet = CreateExcel.originalSXSSFWorkbookCreateExcle(sheet,originalExcels, strcode, strname);
            }
        }
        return workbook;
    }

    /**
     * 生成excel 并复制原文
     *
     * @param ids      条目id集合
     * @param fileName 文件名
     * @param strcode  code集合
     * @param strname  字段名集合
     * @return 返回workbook对象
     * @throws Exception
     */
    public SXSSFWorkbook SXSSFCreatExcelAndCopyFile(String[] ids, String fileName, String[] strcode, String[] strname) throws Exception {
        //1.创建excel文件---用在保存循环的数据--循环写入
        //2.创建工作簿  SXSSFWorkbook 支持最大行1048576
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        boolean b = Arrays.asList(strcode).contains("archivecode");
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {//每个元素都是进行截取过的 900条id
                List<Tb_entry_index> list = entryIndexRepository.findByEntryidIn(listStr.get(i));
                List<Entry> entryList = createEntrtList(list,listStr.get(i));
                //每900条写入1次
                sheet = CreateExcel.SXSSFWorkbookCreateExcle(sheet, entryList, strcode, strname);
                //拷贝电子文件
                List<Tb_electronic> electronics = getElectronic(listStr.get(i));
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
                        if(new File(file).exists()) {
                            FileUtil.CopyFile(file, newUserFileName, filename);
                        }else {
                            System.out.println("---------------电子文件缺失："+file);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //清空档号字符串
                    archivecode = "";
                }
            }
        }
        return workbook;
    }

    //采集
    public List<Tb_entry_detail_capture> getCaptureEntryDetailList(String[] ids) {
        List<Tb_entry_detail_capture> entry_details = new ArrayList<>();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {
                List<Tb_entry_detail_capture> list = entryDetailCaptureRepository.findByEntryidIn(listStr.get(i));
                for (int j = 0; j < list.size(); j++) {
                    entry_details.add(list.get(j));
                }
            }
        }
        return entry_details;
    }

    //目录接收
    public List<Tb_entry_detail_accept> getAcceptEntryDetailList(String[] ids) {
        List<Tb_entry_detail_accept> entry_details = new ArrayList<>();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {
                List<Tb_entry_detail_accept> list = entryDetailAcceptRepository.findByEntryidIn(listStr.get(i));
                for (int j = 0; j < list.size(); j++) {
                    entry_details.add(list.get(j));
                }
            }
        }
        return entry_details;
    }

    //目录管理
    public List<Tb_entry_detail_manage> getManageEntryDetailList(String[] ids) {
        List<Tb_entry_detail_manage> entry_details = new ArrayList<>();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {
                List<Tb_entry_detail_manage> list = entryDetailManageRepository.findByEntryidIn(listStr.get(i));
                for (int j = 0; j < list.size(); j++) {
                    entry_details.add(list.get(j));
                }
            }
        }
        return entry_details;
    }

    public Workbook CaptureSXSSFCreatExcel(String[] ids, String[] strcode, String[] strname,String exporttype) throws Exception {
        //1.创建excel文件---用在保存循环的数据--循环写入
        //2.创建工作簿  SXSSFWorkbook 支持最大行1048576
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        List<Tb_entry_index_capture> entry_indices = new ArrayList<>();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {
                List<Tb_entry_index_capture> list = new ArrayList<>();
                List<Tb_entry_detail_capture> list1 = new ArrayList<>();
                List<EntryCapture> entryCaptures = new ArrayList<>();

                List<Tb_entry_index_accept> acceptlist = new ArrayList<>();
                List<Tb_entry_detail_accept> acceptlist1 = new ArrayList<>();
                List<AcceptEntryCapture> entryAccepts = new ArrayList<>();

                List<Tb_entry_index_manage> manageindexList = new ArrayList<>();
                List<Tb_entry_detail_manage> managedetails = new ArrayList<>();
                List<ManageEntry> manageEntries = new ArrayList<>();

                if(exporttype!=null&&"accept".equals(exporttype)){ //判断是否目录接收导出
                    acceptlist = entryIndexAcceptRepository.findByEntryidIn(listStr.get(i));
                    acceptlist1 = entryDetailAcceptRepository.findByEntryidIn(listStr.get(i));
                    entryAccepts = acceptCreateEntrtList(acceptlist,acceptlist1);
                }else if(exporttype!=null&&"manage".equals(exporttype)){ //判断是否目录管理导出
                    manageindexList = getManageEntryIndexList(ids);
                    managedetails = getManageEntryDetailList(ids);
                    manageEntries = manageCreateEntrtList(manageindexList,managedetails);
                }else{
                    list = entryIndexCaptureRepository.findByEntryidIn(listStr.get(i));
                    entryCaptures = captureCreateEntrtList(list);
                }
                //每900条写入1次
                sheet = CreateExcel.captureSXSSFWorkbookCreateExcle(sheet, entryCaptures, strcode, strname,entryAccepts,exporttype,manageEntries);
            }
        }
        return workbook;
    }

    public SXSSFWorkbook CaptureSXSSFCreatExcelAndCopyFile(String[] ids, String[] strcode, String[] strname, String usefileName) throws Exception {
        //1.创建excel文件---用在保存循环的数据--循环写入
        //2.创建工作簿  SXSSFWorkbook 支持最大行1048576
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        List<Tb_entry_index_capture> entry_indices = new ArrayList<>();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {
                List<Tb_entry_index_capture> list = entryIndexCaptureRepository.findByEntryidIn(listStr.get(i));
                List<EntryCapture> entryCaptures = captureCreateEntrtList(list);
                //每900条写入1次
                sheet = CreateExcel.captureSXSSFWorkbookCreateExcle(sheet, entryCaptures, strcode, strname,null,null,null);
                //拷贝电子文件
                //String[] entryId =ConfigValue.getEntryid(entryList);//--组装entrtid
                List<Tb_electronic_capture> electronics = getElectronicCapture(ids);
                //存放档号
                String archivecode = "";
                String dir = ConfigValue.getPath("system.document.rootpath");
                for (int j = 0; j < electronics.size(); j++) {
                    for (int k = 0; k < entryCaptures.size(); k++) {
                        String eleEntryId = electronics.get(j).getEntryid().trim();
                        String entryid = entryCaptures.get(k).getEntryid().trim();
                        if (entryid.equals(eleEntryId)) {
                            archivecode = entryCaptures.get(k).getArchivecode();
                        }
                    }
                    String filepath = electronics.get(j).getFilepath();//路径
                    String filename = electronics.get(j).getFilename();//文件名
                    String file = dir + filepath + "/" + filename;//完整路径
                    String newUserFileName = "";
                    boolean b = Arrays.asList(strcode).contains("archivecode");
                    if (!b || (archivecode == null) || "".equals(archivecode)) {//-当自选字段中没有档号时/档号为空/未归管理-使用条目id为原文目录名
                        newUserFileName = usefileName + "/" + "document/" + electronics.get(j).getEntryid().trim();//拷贝路径
                    } else {
                        String str = archivecode.replaceAll("\\·", "-");
                        newUserFileName = usefileName + "/" + "document/" + str;//拷贝路径
                    }
                    try {
                        if(new File(file).exists()) {
                            FileUtil.CopyFile(file, newUserFileName, filename);
                        }else {
                            System.out.println("---------------电子文件缺失："+file);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {

                    }
                    //清空档号字符串
                    archivecode = "";
                }
            }
        }
        return workbook;
    }

    //采集
    public List<Tb_entry_index_capture> getCaptureEntryIndexList(String[] ids) {
        List<Tb_entry_index_capture> entry_indices = new ArrayList<>();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {
                List<Tb_entry_index_capture> list = entryIndexCaptureRepository.findByEntryidIn(listStr.get(i));
                for (int j = 0; j < list.size(); j++) {
                    entry_indices.add(list.get(j));
                }
            }
        }
        return entry_indices;
    }

    //目录接收
    public List<Tb_entry_index_accept> getAcceptEntryIndexList(String[] ids) {
        List<Tb_entry_index_accept> entry_indices = new ArrayList<>();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {
                List<Tb_entry_index_accept> list = entryIndexAcceptRepository.findByEntryidIn(listStr.get(i));
                for (int j = 0; j < list.size(); j++) {
                    entry_indices.add(list.get(j));
                }
            }
        }
        return entry_indices;
    }

    //目录管理
    public List<Tb_entry_index_manage> getManageEntryIndexList(String[] ids) {
        List<Tb_entry_index_manage> entry_indices = new ArrayList<>();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {
                List<Tb_entry_index_manage> list = entryIndexManageRepository.findByEntryidIn(listStr.get(i));
                for (int j = 0; j < list.size(); j++) {
                    entry_indices.add(list.get(j));
                }
            }
        }
        return entry_indices;
    }

    public List<Tb_electronic_capture> getElectronicCapture(String[] ids) {
        List<Tb_electronic_capture> list = new ArrayList<>();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {
                List<Tb_electronic_capture> electronics = electronicCaptureRepository.findByEntryidIn(listStr.get(i));
                for (int j = 0; j < electronics.size(); j++) {
                    list.add(electronics.get(j));
                }
            }
        }
        return list;
    }

    public List<Tb_electronic> getElectronic(String[] ids) {
        List<Tb_electronic> list = new ArrayList<>();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {
                List<Tb_electronic> electronics = electronicRepository.findByEntryidIn(listStr.get(i));
                for (int j = 0; j < electronics.size(); j++) {
                    list.add(electronics.get(j));
                }
            }
        }

        return list;
    }

    public List<String> getEleArchivecode(List<Tb_electronic> electronics) {
        List<String> list = new ArrayList<>();
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < electronics.size(); i++) {
            stringList.add(electronics.get(i).getEntryid());
        }
        String[] str = stringList.toArray(new String[stringList.size()]);
        List<String[]> subarr = new ArrayList<>();
        if (str != null) {
            subarr = splitAry(str, 900);
        }
        for (int j = 0; j < subarr.size(); j++) {
            String[] archivecodeList = entryIndexRepository.findArchivecodeByEntryid(subarr.get(j));
            List<String> arrlist = Arrays.asList(archivecodeList);
            list.addAll(arrlist);
        }
        return list;
    }

    public String[] getFieldNames(String nodeId) {
        String[] str = {};
        if (nodeId != null) {
            List<Tb_data_template> templates = templateService.fromTemplateOrderbyFs(nodeId);
            List<String> fieldname = new ArrayList<>();
            for(Tb_data_template template:templates){
                fieldname.add(template.getFieldname());
            }
            str = fieldname.toArray(new String[fieldname.size()]);
        }
        return str;
    }

    public String[] getFieldCodes(String nodeId) {
        String[] str = {};
        if (nodeId != null) {
            List<Tb_data_template> templates = templateService.fromTemplateOrderbyFs(nodeId);
            List<String> fieldname = new ArrayList<>();
            for(Tb_data_template template:templates){
                fieldname.add(template.getFieldcode());
            }
            str = fieldname.toArray(new String[fieldname.size()]);
        }
        return str;
    }

    public List<Tb_data_template> getDatatemplate(String nodeId) {
        List<Tb_data_template> list = new ArrayList<>();
        if (nodeId != null) {
            list = templateRepository.findByNodeid(nodeId);
        }
        return list;
    }

    public String[] findEntryIdByNodeId(String nodeid) {
        String[] entryId = {};
        if (!"".equals(nodeid)) {
            entryId = entryIndexRepository.findEntryidByNodeid(nodeid);
        }
        return entryId;
    }

    //---数据采集 根据节点id查询所有id
    public String[] captureFindEntryIdByNodeId(String nodeid) {
        String[] entryId = {};
        if (!"".equals(nodeid)) {
            entryId = entryIndexCaptureRepository.FindEntryidByNodeid(nodeid);
        }
        return entryId;
    }

    /**
     * index  detail  数据组装成Entry 对象
     *
     * @param indexList
     * @return entryList
     */
    public List<Entry> createEntrtList(List<Tb_entry_index> indexList, String[] entryidArr) {
        List<Entry> entryList = new ArrayList<>();
        List<Tb_entry_detail> details=entryDetailRepository.findByEntryidIn(entryidArr);
        if (indexList.size() > 0) {
            for (int i = 0; i < indexList.size(); i++) {
                Entry entry = new Entry();//生成entry对象 调用里面的方法
                entry.setEntryIndex(indexList.get(i));
                if (details.size() > 0) {
                    int flag=-1;
                    for (int  j= 0; j < details.size(); j++) {
                        if(indexList.get(i).getEntryid().equals(details.get(j).getEntryid())){
                            flag=j;//找到同entryid的副表条目
                            break;
                        }
                    }
                    if(flag==-1){//没找到副表对应条目
                        Tb_entry_detail new_detail=new Tb_entry_detail();
                        new_detail.setEntryid(indexList.get(i).getEntryid());
                        entry.setEntryDetial(new_detail);
                    }else{
                        entry.setEntryDetial(details.get(flag));
                    }
                }

                /*Tb_entry_detail detail = entryDetailRepository.findByEntryid(indexList.get(i).getEntryid());
                if(detail != null) {//防止数据库无副表对应条目,导致导出空白文件
                    entry.setEntryDetial(detail);
                }else {
                    detail.setEntryid(indexList.get(i).getEntryid());
                    entry.setEntryDetial(detail);
                }*/

                entryList.add(entry);
            }
        }
        return entryList;
    }

    /**
     * 数据采集
     * index  detail  数据组装成Entry 对象
     *
     * @param indexList
     * @return entryList
     */
    public List<EntryCapture> captureCreateEntrtList(List<Tb_entry_index_capture> indexList) {
        List<EntryCapture> entryList = new ArrayList<>();
        if (indexList.size() > 0 ) {
            for (int i = 0; i < indexList.size(); i++) {
                EntryCapture entry = new EntryCapture();//生成entry对象 调用里面的方法
                entry.setEntryIndex(indexList.get(i));
                Tb_entry_detail_capture detail_capture = entryDetailCaptureRepository.findByEntryid(indexList.get(i).getEntryid());
                if (detail_capture != null) {//防止数据库无副表对应条目,导致导出空白文件
                    entry.setEntryDetial(detail_capture);
                } else {
                    detail_capture.setEntryid(indexList.get(i).getEntryid());
                    entry.setEntryDetial(detail_capture);
                }
                entryList.add(entry);
            }
        }
        return entryList;
    }

    /**
     * 目录接收
     * index  detail  数据组装成Entry 对象
     *
     * @param indexList
     * @param details
     * @return entryList
     */
    public List<AcceptEntryCapture> acceptCreateEntrtList(List<Tb_entry_index_accept> indexList, List<Tb_entry_detail_accept> details) {
        List<AcceptEntryCapture> entryList = new ArrayList<>();
        if (indexList.size() > 0 && details.size() > 0) {
            for (int i = 0; i < indexList.size(); i++) {
                AcceptEntryCapture entry = new AcceptEntryCapture();//生成entry对象 调用里面的方法
                entry.setEntryIndex(indexList.get(i));
                if (details.size() > 0 && details.size() == indexList.size()) {//防止数据库无副表对应条目,导致导出空白文件
                    entry.setEntryDetial(details.get(i));
                } else {
                    Tb_entry_detail_accept detail_capture = new Tb_entry_detail_accept();
                    detail_capture.setEntryid(indexList.get(i).getEntryid());
                    entry.setEntryDetial(detail_capture);
                }
                entryList.add(entry);
            }
        }
        return entryList;
    }

    /**
     * 目录管理
     * index  detail  数据组装成Entry 对象
     *
     * @param indexList
     * @param details
     * @return entryList
     */
    public List<ManageEntry> manageCreateEntrtList(List<Tb_entry_index_manage> indexList, List<Tb_entry_detail_manage> details) {
        List<ManageEntry> entryList = new ArrayList<>();
        if (indexList.size() > 0 && details.size() > 0) {
            for (int i = 0; i < indexList.size(); i++) {
                ManageEntry entry = new ManageEntry();//生成entry对象 调用里面的方法
                entry.setEntryIndex(indexList.get(i));
                if (details.size() > 0 && details.size() == indexList.size()) {//防止数据库无副表对应条目,导致导出空白文件
                    entry.setEntryDetial(details.get(i));
                } else {
                    Tb_entry_detail_manage detail_capture = new Tb_entry_detail_manage();
                    detail_capture.setEntryid(indexList.get(i).getEntryid());
                    entry.setEntryDetial(detail_capture);
                }
                entryList.add(entry);
            }
        }
        return entryList;
    }

    //数据采集调用生成xml方法
    public String captureCreatexml(String[] ids, String nodeId, String filename, HttpServletResponse response, String[] fieldcod, String zipPassword,String exporttype) {

        List<Tb_entry_index_capture> indexList = new ArrayList<>();
        List<Tb_entry_detail_capture> details = new ArrayList<>();
        List<EntryCapture> entryList = new ArrayList<>();

        List<Tb_entry_index_accept> acceptindexList = new ArrayList<>();
        List<Tb_entry_detail_accept> acceptdetails = new ArrayList<>();
        List<AcceptEntryCapture> acceptList = new ArrayList<>();

        List<Tb_entry_index_manage> manageindexList = new ArrayList<>();
        List<Tb_entry_detail_manage> managedetails = new ArrayList<>();
        List<ManageEntry> manageEntries = new ArrayList<>();

        if(exporttype!=null&&"accept".equals(exporttype)){ //判断是否目录接收导出
            acceptindexList = getAcceptEntryIndexList(ids);
            acceptdetails = getAcceptEntryDetailList(ids);
            acceptList = acceptCreateEntrtList(acceptindexList,acceptdetails);
        }else if(exporttype!=null&&"manage".equals(exporttype)){ //判断是否目录管理导出
            manageindexList = getManageEntryIndexList(ids);
            managedetails = getManageEntryDetailList(ids);
            manageEntries = manageCreateEntrtList(manageindexList,managedetails);
        } else {
            indexList = getCaptureEntryIndexList(ids);
            entryList = captureCreateEntrtList(indexList);
        }
        /*String[] fieldcod = getFieldCodes(nodeId);
        String[] fieldname = getFieldNames(nodeId);*/
        List<String> userFieldNameList = getUserFieldName(fieldcod, nodeId);
        String[] fieldname = new String[userFieldNameList.size()];
        userFieldNameList.toArray(fieldname);
        String[] strcode = new String[fieldcod.length + 1];
        String[] strname = new String[fieldname.length + 1];
        strcode[strcode.length - 1] = "entryid";
        strname[strname.length - 1] = "条目ID";
        System.arraycopy(fieldcod, 0, strcode, 0, fieldcod.length);
        System.arraycopy(fieldname, 0, strname, 0, fieldname.length);
        String zippath = "";
        try {
            //创建字段模板
            String dir = ConfigValue.getPath("system.document.rootpath");
            String path = dir + "/OAFile" + "/xml导出/临时目录/" + filename;//
//            createTemp(nodeId, strcode, strname, path + "/", ids.length);
            XmlUtil.captureExportXml(strname, strcode, entryList, filename, response,acceptList,exporttype,manageEntries);
            // zip 完整路径
            zippath = dir + "/OAFile" + "/xml导出/" + filename + ".zip";
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, zipPassword);
            //wirteFile(response,zippath,filename,zipReturn,succ);//读取压缩包，发送页面
            ZipUtils.del(path);
            //new File(zippath).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zippath;
    }

    //调用生成xml方法
    public String createxml(String[] ids, String nodeId, String filename, HttpServletResponse response, String[] fieldcod, String zipPassword) {

       /* List<Tb_entry_index> indexList = getEntryIndexList(ids);
        List<Entry> entryList = createEntrtList(indexList);*/
        List<Entry> entryList=new ArrayList<>();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {
                List<Tb_entry_index> list = entryIndexRepository.findByEntryidIn(listStr.get(i));
                entryList.addAll(createEntrtList(list,listStr.get(i)));
            }
        }
        /*String[] fieldcod = getFieldCodes(nodeId);
        String[] fieldname = getFieldNames(nodeId);*/
        List<String> userFieldNameList = getUserFieldName(fieldcod, nodeId);
        String[] fieldname = new String[userFieldNameList.size()];
        userFieldNameList.toArray(fieldname);
        String[] strcode = new String[fieldcod.length + 1];
        String[] strname = new String[fieldname.length + 1];
        strcode[strcode.length - 1] = "entryid";
        strname[strname.length - 1] = "条目ID";
        System.arraycopy(fieldcod, 0, strcode, 0, fieldcod.length);
        System.arraycopy(fieldname, 0, strname, 0, fieldname.length);
        String zippath = "";
        try {
            //创建字段模板
            String dir = ConfigValue.getPath("system.document.rootpath");
            String path = dir + "/OAFile" + "/xml导出/临时目录/" + filename;//
//            createTemp(nodeId, strcode, strname, path + "/", ids.length);
            XmlUtil.exportXml(strname, strcode, entryList, filename);//生成文件
            // zip 完整路径
            zippath = dir + "/OAFile" + "/xml导出/" + filename + ".zip";
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, zipPassword);
            //wirteFile(response,zippath,filename,zipReturn,succ);//读取压缩包，发送页面
            ZipUtils.del(path);
            //new File(zippath).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zippath;
    }

    //数据采集调用生成excel方法
    public String captureCreateExcel(String[] ids, String nodeId, String fileName, HttpServletResponse response,
                                     String[] fieldcod, String zipPassword,String exporttype) {
        //List<Tb_entry_index_capture> indexList = getCaptureEntryIndexList(ids);
        //List<Tb_entry_detail_capture> details = getCaptureEntryDetailList(ids);
        //List<EntryCapture> entryList = captureCreateEntrtList(indexList, details);
        /*String[] fieldcod = getFieldCodes(nodeId);
        String[] fieldname = getFieldNames(nodeId);*/
        List<String> userFieldNameList = getUserFieldName(fieldcod, nodeId);
        String[] fieldname = new String[userFieldNameList.size()];
        userFieldNameList.toArray(fieldname);
        String[] strcode = new String[fieldcod.length + 1];
        String[] strname = new String[fieldname.length + 1];
        strcode[strcode.length - 1] = "entryid";
        strname[strname.length - 1] = "条目ID";
        System.arraycopy(fieldcod, 0, strcode, 0, fieldcod.length);
        System.arraycopy(fieldname, 0, strname, 0, fieldname.length);
        String zippath = "";
        try {
            //创建字段模板
            String dir = ConfigValue.getPath("system.document.rootpath");
            String path = dir + "/OAFile" + "/Excel导出/临时目录/" + fileName;//
//            createTemp(nodeId, strcode, strname, path + "/", ids.length);
            Workbook workbook = CaptureSXSSFCreatExcel(ids, strcode, strname,exporttype);
            CreateExcel.ExportExcel(workbook, fileName);
            // zip 完整路径
            zippath = dir + "/OAFile" + "/xml导出/" + fileName + ".zip";
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, zipPassword);
            //wirteFile(response,zippath,fileName,zipReturn,succ);//读取压缩包，发送页面
            ZipUtils.del(path);
            //new File(zippath).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zippath;
    }

    //预约管理调用生成excel方法
    public String reservationCreateExcel(String[] ids, String nodeId, String fileName, HttpServletResponse response, String[] fieldcod,String[] fieldName, String zipPassword) {
        String[] strcode = new String[fieldcod.length + 1];
        String[] strname = new String[fieldName.length + 1];
        strcode[strcode.length - 1] = "docid";
        strname[strname.length - 1] = "条目ID";
        System.arraycopy(fieldcod, 0, strcode, 0, fieldcod.length);
        System.arraycopy(fieldName, 0, strname, 0, fieldName.length);
        String zippath = "";
        SXSSFWorkbook workbook =null;
        try {
            workbook = rservationSXSSFCreatExcel(ids, strcode, strname);
            //创建字段模板
            String dir = ConfigValue.getPath("system.document.rootpath");
            String path = dir + "/OAFile" + "/Excel导出/临时目录/" + fileName;//
//            createTemp(nodeId, strcode, strname, path + "/", ids.length);
            //Workbook workbook = CreateExcel.CreateExcle(fileName, entryList, strcode, strname);
            //entryList = null;
            CreateExcel.ExportExcel(workbook, fileName);
            // zip 完整路径
            zippath = dir + "/OAFile" + "/Excel导出/" + fileName + ".zip";
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, zipPassword);
            //wirteFile(response,zippath,fileName,zipRturn,succ);//读取压缩包，发送页面
            ZipUtils.del(path);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workbook.dispose();
        }
        return zippath;
    }

    //设备管理调用生成excel方法
    public String equipmentCreateExcel(String[] ids, String nodeId, String fileName, HttpServletResponse response, String[] fieldcod,String[] fieldName, String zipPassword) {
        String[] strcode = new String[fieldcod.length + 1];
        String[] strname = new String[fieldName.length + 1];
        strcode[strcode.length - 1] = "equipmentID";
        strname[strname.length - 1] = "设备ID";
        System.arraycopy(fieldcod, 0, strcode, 0, fieldcod.length);
        System.arraycopy(fieldName, 0, strname, 0, fieldName.length);
        String zippath = "";
        SXSSFWorkbook workbook =null;
        try {
            workbook = equipmentSXSSFCreatExcel(ids, strcode, strname);
            //创建字段模板
            String dir = ConfigValue.getPath("system.document.rootpath");
            String path = dir + "/OAFile" + "/Excel导出/临时目录/" + fileName;//
//            createTemp(nodeId, strcode, strname, path + "/", ids.length);
            //Workbook workbook = CreateExcel.CreateExcle(fileName, entryList, strcode, strname);
            //entryList = null;
            CreateExcel.ExportExcel(workbook, fileName);
            // zip 完整路径
            zippath = dir + "/OAFile" + "/Excel导出/" + fileName + ".zip";
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, zipPassword);
            //wirteFile(response,zippath,fileName,zipRturn,succ);//读取压缩包，发送页面
            ZipUtils.del(path);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workbook.dispose();
        }
        return zippath;
    }

    //全文检索调用生成excel方法
    public String originalCreateExcel(String[] ids, String nodeId, String fileName, HttpServletResponse response, String[] fieldcod,String[] fieldName, String zipPassword) {
        String[] strcode = new String[fieldcod.length + 1];
        String[] strname = new String[fieldName.length + 1];
        strcode[strcode.length - 1] = "eleid";
        strname[strname.length - 1] = "文件ID";
        System.arraycopy(fieldcod, 0, strcode, 0, fieldcod.length);
        System.arraycopy(fieldName, 0, strname, 0, fieldName.length);
        String zippath = "";
        SXSSFWorkbook workbook =null;
        try {
            workbook = originalSXSSFCreatExcel(ids, strcode, strname);
            //创建字段模板
            String dir = ConfigValue.getPath("system.document.rootpath");
            String path = dir + "/OAFile" + "/Excel导出/临时目录/" + fileName;//
            CreateExcel.ExportExcel(workbook, fileName);
            // zip 完整路径
            zippath = dir + "/OAFile" + "/Excel导出/" + fileName + ".zip";
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, zipPassword);
            ZipUtils.del(path);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workbook.dispose();
        }
        return zippath;
    }

    //调用生成excel方法
    public String createExcel(String[] ids, String nodeId, String fileName, HttpServletResponse response, String[] fieldcod, String zipPassword) {
        List<String> userFieldNameList = getUserFieldName(fieldcod, nodeId);
        String[] fieldname = new String[userFieldNameList.size()];
        userFieldNameList.toArray(fieldname);
        String[] strcode = new String[fieldcod.length + 1];
        String[] strname = new String[fieldname.length + 1];
        strcode[strcode.length - 1] = "entryid";
        strname[strname.length - 1] = "条目ID";
        System.arraycopy(fieldcod, 0, strcode, 0, fieldcod.length);
        System.arraycopy(fieldname, 0, strname, 0, fieldname.length);
        String zippath = "";
        SXSSFWorkbook workbook =null;
        try {
            workbook = SXSSFCreatExcel(ids, strcode, strname, nodeId);
            //创建字段模板
            String dir = ConfigValue.getPath("system.document.rootpath");
            String path = dir + "/OAFile" + "/Excel导出/临时目录/" + fileName;//
//            createTemp(nodeId, strcode, strname, path + "/", ids.length);
            //Workbook workbook = CreateExcel.CreateExcle(fileName, entryList, strcode, strname);
            //entryList = null;
            CreateExcel.ExportExcel(workbook, fileName);
            // zip 完整路径
            zippath = dir + "/OAFile" + "/Excel导出/" + fileName + ".zip";
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, zipPassword);
            //wirteFile(response,zippath,fileName,zipRturn,succ);//读取压缩包，发送页面
            ZipUtils.del(path);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workbook.dispose();
        }
        return zippath;
    }

    //数据采集--导出excel和电子原文

    /**
     * @param ids
     * @param nodeId
     * @param usefileName 页面用户输入的文件名
     * @param response
     */
    public String captureCreateExcleAndElectronic(String[] ids, String nodeId, String usefileName, HttpServletResponse response,
                                                  String[] fieldcod, String zipPassword) {
        List<String> userFieldNameList = getUserFieldName(fieldcod, nodeId);
        String[] fieldname = new String[userFieldNameList.size()];
        userFieldNameList.toArray(fieldname);
        String[] strcode = new String[fieldcod.length + 1];
        String[] strname = new String[fieldname.length + 1];
        strcode[strcode.length - 1] = "entryid";
        strname[strname.length - 1] = "条目ID";
        System.arraycopy(fieldcod, 0, strcode, 0, fieldcod.length);
        System.arraycopy(fieldname, 0, strname, 0, fieldname.length);
        String dir = ConfigValue.getPath("system.document.rootpath");
        String zippath = "";
        SXSSFWorkbook workbook =null;
        try {
            workbook = CaptureSXSSFCreatExcelAndCopyFile(ids, strcode, strname, usefileName);
            //创建字段模板
            String path = dir + "/OAFile" + "/Excel导出/临时目录/" + usefileName;
            CreateExcel.ExportExcel(workbook, usefileName);
            // zip 完整路径
            zippath = dir + "/OAFile" + "/Excel导出/" + usefileName + ".zip";
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, zipPassword);
            //读取压缩包，发送页面
            ZipUtils.del(path);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            workbook.dispose();
        }
        return zippath;
    }


    //导出excel和电子原文

    /**
     * @param ids
     * @param nodeId
     * @param usefileName 页面用户输入的文件名
     * @param response
     */
    public String createExcleAndElectronic(String[] ids, String nodeId, String usefileName, HttpServletResponse response,
                                           String[] fieldcod, String zipPassword) {
        List<String> userFieldNameList = getUserFieldName(fieldcod, nodeId);
        String[] fieldname = new String[userFieldNameList.size()];
        userFieldNameList.toArray(fieldname);
        String[] strcode = new String[fieldcod.length + 1];
        String[] strname = new String[fieldname.length + 1];
        strcode[strcode.length - 1] = "entryid";
        strname[strname.length - 1] = "条目ID";
        System.arraycopy(fieldcod, 0, strcode, 0, fieldcod.length);
        System.arraycopy(fieldname, 0, strname, 0, fieldname.length);
        String zippath = "";
        SXSSFWorkbook workbook =null;
        try {
            workbook = SXSSFCreatExcelAndCopyFile(ids, usefileName, strcode, strname);
            //创建字段模板
            String dir = ConfigValue.getPath("system.document.rootpath");
            String path = dir + "/OAFile" + "/Excel导出/临时目录/" + usefileName;
            CreateExcel.ExportExcel(workbook, usefileName);
            // zip 完整路径
            zippath = dir + "/OAFile" + "/Excel导出/" + usefileName + ".zip";
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, zipPassword);
            ZipUtils.del(path);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            workbook.dispose();
        }
        return zippath;
    }

    //数据采集--导出xml和电子原文

    /**
     * @param ids
     * @param nodeId
     * @param usefileName 页面用户输入的文件名
     * @param response
     */
    public String captureCreateXmlAndElectronic(String[] ids, String nodeId, String usefileName, HttpServletResponse response, String[] fieldcod, String zipPassword) {

        List<Tb_entry_index_capture> indexList = getCaptureEntryIndexList(ids);
        List<EntryCapture> entryList = captureCreateEntrtList(indexList);
        List<String> userFieldNameList = getUserFieldName(fieldcod, nodeId);
        String[] fieldname = new String[userFieldNameList.size()];
        userFieldNameList.toArray(fieldname);
        /*String[] fieldcod = getFieldCodes(nodeId);
        String[] fieldname = getFieldNames(nodeId);*/
        String[] strcode = new String[fieldcod.length + 1];
        String[] strname = new String[fieldname.length + 1];
        strcode[strcode.length - 1] = "entryid";
        strname[strname.length - 1] = "条目ID";
        System.arraycopy(fieldcod, 0, strcode, 0, fieldcod.length);
        System.arraycopy(fieldname, 0, strname, 0, fieldname.length);
        //拷贝电子文件
        //String[] entryId =ConfigValue.getEntryid(entryList);//--组装entrtid
        List<Tb_electronic_capture> electronics = getElectronicCapture(ids);
        String dir = ConfigValue.getPath("system.document.rootpath");

        //存放档号
        String archivecode = "";
        String zippath = "";
        for (int i = 0; i < electronics.size(); i++) {
            for (int j = 0; j < entryList.size(); j++) {
                String eleEntryId = electronics.get(i).getEntryid().trim();
                String entryid = entryList.get(j).getEntryid().trim();
                if (entryid.equals(eleEntryId)) {
                    archivecode = entryList.get(j).getArchivecode();
                }
            }
            String filepath = electronics.get(i).getFilepath();//路径
            String filename = electronics.get(i).getFilename();//文件名
            String file = dir + filepath + "/" + filename;//完整路径(目标文件)
            String newUserFileName = "";
            boolean b = Arrays.asList(strcode).contains("archivecode");
            if (!b || (archivecode == null) || "".equals(archivecode)) {//-当自选字段中没有档号时/档号为空/未归管理-使用条目id为原文目录名
                newUserFileName = usefileName + "/" + "document/" + electronics.get(i).getEntryid().trim();//拷贝路径
            }
            /*if (archivecode == null || "".equals(archivecode)) {//未归档--没有档号
                newUserFileName = usefileName + "/" + "document/" + electronics.get(i).getEntryid().trim();//拷贝路径
            }*/
            else {
                String str = archivecode.replaceAll("\\·", "-");
                newUserFileName = usefileName + "/" + "document/" + str;//拷贝路径
            }
            try {
                if(new File(file).exists()) {
                    FileUtil.CopyFileXml(new File(file), newUserFileName, filename);
                }else {
                    System.out.println("---------------电子文件缺失："+file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //清空档号字符串
            archivecode = "";
        }
        try {
            //创建字段模板
            String path = dir + "/OAFile" + "/xml导出/临时目录/" + usefileName;//
//            createTemp(nodeId, strcode, strname, path + "/", ids.length);
            XmlUtil.captureExportXml(strname, strcode, entryList, usefileName, response,null,null,null);
            // zip 完整路径
            zippath = dir + "/OAFile" + "/xml导出/" + usefileName + ".zip";
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, zipPassword);
            //wirteFile(response,zippath,usefileName,zipReturn,succ);//读取压缩包，发送页面
            ZipUtils.del(path);
            //new File(zippath).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zippath;
    }

    //导出xml和电子原文

    /**
     * @param ids
     * @param nodeId
     * @param usefileName 页面用户输入的文件名
     * @param response
     */
    public String createXmlAndElectronic(String[] ids, String nodeId, String usefileName, HttpServletResponse response, String[] fieldcod, String zipPassword) {
        /*List<Tb_entry_index> indexList = getEntryIndexList(ids);
        List<Entry> entryList = createEntrtList(indexList);*/
        List<Entry> entryList=new ArrayList<>();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {
                List<Tb_entry_index> list = entryIndexRepository.findByEntryidIn(listStr.get(i));
                entryList.addAll(createEntrtList(list,listStr.get(i)));
            }
        }
		/*String[] fieldcod = getFieldCodes(nodeId);
		String[] fieldname = getFieldNames(nodeId);*/
        List<String> userFieldNameList = getUserFieldName(fieldcod, nodeId);
        String[] fieldname = new String[userFieldNameList.size()];
        userFieldNameList.toArray(fieldname);
        String[] strcode = new String[fieldcod.length + 1];
        String[] strname = new String[fieldname.length + 1];
        strcode[strcode.length - 1] = "entryid";
        strname[strname.length - 1] = "条目ID";
        System.arraycopy(fieldcod, 0, strcode, 0, fieldcod.length);
        System.arraycopy(fieldname, 0, strname, 0, fieldname.length);
        //拷贝电子原文
        //String[] entryId =ConfigValue.getEntryid(entryList);
        List<Tb_electronic> electronics = getElectronic(ids);
        String dir = ConfigValue.getPath("system.document.rootpath");

        //存放档号
        String archivecode = "";
        String zippath = "";
        for (int i = 0; i < electronics.size(); i++) {
            for (int j = 0; j < entryList.size(); j++) {
                String eleEntryId = electronics.get(i).getEntryid().trim();
                String entryid = entryList.get(j).getEntryid().trim();
                if (entryid.equals(eleEntryId)) {
                    archivecode = entryList.get(j).getArchivecode();
                }
            }
            String filepath = electronics.get(i).getFilepath();//路径
            String filename = electronics.get(i).getFilename();//文件名
            String file = dir + filepath + "/" + filename;//完整路径(目标文件)
            String newUserFileName = "";
            boolean b = Arrays.asList(strcode).contains("archivecode");
            if (!b || (archivecode == null) || "".equals(archivecode)) {//-当自选字段中没有档号时/档号为空/未归管理-使用条目id为原文目录名
                newUserFileName = usefileName + "/" + "document/" + electronics.get(i).getEntryid().trim();//拷贝路径
            } else {
                String str = archivecode.replaceAll("\\·", "-");
                newUserFileName = usefileName + "/" + "document/" + str;//拷贝路径
            }
            try {
                File file1 = new File(file);
                if(file1.exists()) {
                    FileUtil.CopyFileXml(file1, newUserFileName, filename);
                }else {
                    System.out.println("---------------电子文件缺失："+file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //清空档号字符串
            archivecode = "";
        }
        try {
            //创建字段模板
            String path = dir + "/OAFile" + "/xml导出/临时目录/" + usefileName;//
//            createTemp(nodeId, strcode, strname, path + "/", ids.length);
            XmlUtil.exportXml(strname, strcode, entryList, usefileName);
            // zip 完整路径
            zippath = dir + "/OAFile" + "/xml导出/" + usefileName + ".zip";
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, zipPassword);
            //wirteFile(response,zippath,usefileName,zipReturn,succ);//读取压缩包，发送页面
            ZipUtils.del(path);
            //new File(zippath).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zippath;
    }

    //创建节点字段模板
    public void createTemp(String nodeid, String[] strcode, String[] strname, String path, int count) throws IOException {

        //path = path + "/Excel导出/临时目录/"+fileName+"/";//

        //生成excel
        String nodeName = getParentNodeName(nodeid, count);
        File f = new File(path + "/字段模板信息/");
        f.mkdirs();
        if (!f.exists()) {
            throw new RuntimeException("createXml()---创建文件夹失败");
        }
        Workbook workbook = CreateExcel.createTemp(strcode, strname);
        OutputStream os = new FileOutputStream(new File(path + "/字段模板信息/" + nodeName + ".xls"));
        workbook.write(os);
        os.flush();
        os.close();
        workbook.close();

    }

    //获取当前节点的所有父节点
    public String getParentNodeName(String nodeid, int count) {
        String nodeName="";
        if("publicNode".equals(nodeid) || "12345678910".equals(nodeid)){
            nodeName= "库房模板";
        }else {
            nodeName = dataNodeRepository.findNodenameByNodeid(nodeid);
            int nodeLevel = Integer.parseInt(dataNodeRepository.findNodeLevelByNodeid(nodeid));
            String parentNodeId = dataNodeRepository.findParentNodeidByNodeid(nodeid);
            String str = new String();
            if (parentNodeId != null) {
                String parentName = dataNodeRepository.findNodenameByNodeid(parentNodeId);
                for (int i = 2; i < nodeLevel; i++) {
                    parentNodeId = dataNodeRepository.findParentNodeidByNodeid(parentNodeId);
                    str = dataNodeRepository.findNodenameByNodeid(parentNodeId) + "_" + str;
                }
                nodeName = str + parentName + "_" + nodeName;
            }
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        return nodeName + "_" + sdf.format(date) + "_条数_" + count;
    }

    //获取当前节点的所有父节点-声像
    public String getSxParentNodeName(String nodeid, int count) {
        String nodeName="";
        if("publicNode".equals(nodeid) || "12345678910".equals(nodeid)){
            nodeName= "库房模板";
        }else {
            nodeName = sxDataNodeRepository.findNodenameByNodeid(nodeid);
            int nodeLevel = Integer.parseInt(sxDataNodeRepository.findNodeLevelByNodeid(nodeid));
            String parentNodeId = sxDataNodeRepository.findParentNodeidByNodeid(nodeid);
            String str = new String();
            if (parentNodeId != null) {
                String parentName = sxDataNodeRepository.findNodenameByNodeid(parentNodeId);
                for (int i = 2; i < nodeLevel; i++) {
                    parentNodeId = sxDataNodeRepository.findParentNodeidByNodeid(parentNodeId);
                    str = sxDataNodeRepository.findNodenameByNodeid(parentNodeId) + "_" + str;
                }
                nodeName = str + parentName + "_" + nodeName;
            }
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        return nodeName + "_" + sdf.format(date) + "_条数_" + count;
    }

    //根据用户选择的fidecode 找出对应的fieldname
    public List<String> getUserFieldName(String[] userfieldcode, String nodeId) {
        List<String> fieldname = new ArrayList<>();
        String[] fieldName = getFieldNames(nodeId);
        String[] fieldcode = getFieldCodes(nodeId);
        if (fieldName != null && fieldcode != null && userfieldcode != null) {
            for (int i = 0; i < userfieldcode.length; i++) {
                for (int j = 0; j < fieldcode.length; j++) {
                    if (userfieldcode[i].equals(fieldcode[j])) {//存在相同fielcode -->记录对应的name
                        fieldname.add(fieldName[j]);
                    }
                }
            }
        }
        return fieldname;
    }

    public void exportFieldTemp(HttpServletResponse response, HttpServletRequest request, String nodeid, boolean isEntryStorage) {
        //String[] fieldCode = getFieldCodes(nodeid);
        String[] fieldName = getFieldNames(nodeid);
        String nodeName = "条目列表数据模板";
        if(!isEntryStorage){
            nodeName = getParentNodeName(nodeid, 0);
        }
        String tempFileName = "字段模板";
        //创建字段模板
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + "/OAFile" + "/导出模板/";//
        try {
            File f = new File(path);
            f.mkdirs();
            if (!f.exists()) {
                throw new RuntimeException("createXml()---创建文件夹失败");
            }
            //Workbook workbook = CreateExcel.createTemp(fieldCode, fieldName);//---创建excel字段模板
            Workbook workbook = CreateExcel.createFieldNameTemp(fieldName);
            OutputStream os = new FileOutputStream(new File(path + "/" + nodeName + ".xls"));
            workbook.write(os);
            os.flush();
            os.close();
            workbook.close();
            //创建xml字段模板
            //将2个文件进行压缩
            InputStream inputStream = new FileInputStream(new File(path + "/" + nodeName + ".xls"));
            OutputStream out = response.getOutputStream();
            String userAgent = request.getHeader("user-agent").toLowerCase();
            if (userAgent.contains("msie") || userAgent.contains("like gecko") ) {
                // win10 ie edge 浏览器 和其他系统的ie
                nodeName = URLEncoder.encode(nodeName, "UTF-8").replaceAll("\\+", "%20")
                        .replaceAll("%28", "\\(").replaceAll("%29", "\\)")
                        .replaceAll("%3B", ";").replaceAll("%40", "@")
                        .replaceAll("%23", "\\#").replaceAll("%26", "\\&")
                        .replaceAll("%2C", "\\,");
            } else {
                // fe
                nodeName = new String(nodeName.getBytes("utf-8"), "iso-8859-1").replaceAll("\\+", "%20")
                        .replaceAll("%28", "\\(").replaceAll("%29", "\\)")
                        .replaceAll("%3B", ";").replaceAll("%40", "@")
                        .replaceAll("%23", "\\#").replaceAll("%26", "\\&")
                        .replaceAll("%2C", "\\,");
            }
            response.setContentType("application/octet-stream;charset=UTF-8");
//            response.setHeader("Content-Disposition",
//                    "attachment;filename=\"" + new String((nodeName + ".xlsx").getBytes(), "iso-8859-1") + "\"");
            response.setHeader("Content-Disposition",
                    "attachment;fileName=" + nodeName+".xlsx");

            byte[] b = new byte[1024 * 1024 * 10];
            int leng = 0;
            while ((leng = inputStream.read(b)) != -1) {
                out.write(b, 0, leng);
            }
            out.flush();
            inputStream.close();
            out.close();
            ZipUtils.delFolder(path);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void wirteFile(HttpServletResponse response, String filepath, String filename, String zipReturn, boolean succ) throws Exception {
        if (zipReturn != null || succ) {
            InputStream inputStream = new FileInputStream(new File(filepath));
            OutputStream out = response.getOutputStream();
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename=\"" + new String((filename + ".zip").getBytes(), "iso-8859-1") + "\"");
            byte[] b = new byte[1024 * 1024 * 10];
            int leng = 0;
            while ((leng = inputStream.read(b)) != -1) {
                out.write(b, 0, leng);
            }
            out.flush();
            inputStream.close();
            out.close();
        }
    }

    /**
     *
     */
    public String getFileSize(String[] ids){
        String msg="OK";
        long size=0;
        List<Tb_electronic> electronics = getElectronic(ids);
        for(int i=0;i<electronics.size();i++){
           size+=Integer.parseInt(electronics.get(i).getFilesize());
        }
        //b-->转换为G
        size=size/1024/1024/1024;
        if(size>FILE_SIZE){
            msg="NO";
        }
        return msg;
    }



    //-------------------------------------------处理大数据量生成entry对象 导致的内存溢出问题-------------------------//

    /**
     * @param ids 用户选择的条目id
     */
    public List<Entry> createBigDate(String[] ids) {//
        List<Tb_entry_index> indexList = getEntryIndexList(ids);
        List<Entry> entryList = new ArrayList<>();
        //1.进行长度判断截取多少次
        for (int i = 0; i < 5; i++) {
            List<Tb_entry_index> subIndex = indexList.subList(i * ID_CAPTURE_COUNT, i * ID_CAPTURE_COUNT + ID_CAPTURE_COUNT - 1);
            List<Entry> entrys = createEntrtList(subIndex,null);
            entryList.addAll(entrys);
        }
        return entryList;
    }


    public static void main(String[] age) {
        System.out.println("=====================通过java来获取相关系统状态====================");
        long i = Runtime.getRuntime().totalMemory()/1024/1024;//Java 虚拟机中的内存总量，以字节为单位
        System.out.println("总的内存量为:" + i + "Mb");
        long j = Runtime.getRuntime().freeMemory()/1024/1024;//Java 虚拟机中的空闲内存量
        System.out.println("空闲内存量:" + j + "Mb");
        long k = Runtime.getRuntime().maxMemory()/1024/1024;
        System.out.println("最大可用内存量:" + k + "Mb");


    }

    //调用生成漏页信息excel方法
    public String createMissPageExcel(String[] ids,String fileName, HttpServletResponse response,String zipPassword) {
        String[] strcode = new String[5];
        String[] strname = new String[5];
        strcode[0] = "id";
        strname[0] = "条目ID";
        strcode[1] = "archivecode";
        strname[1] = "档号";
        strcode[2] = "page";
        strname[2] = "页数";
        strcode[3] = "elenumber";
        strname[3] = "原文数量";
        strcode[4] = "result";
        strname[4] = "结果";
        String zippath = "";
        SXSSFWorkbook workbook =null;
        try {
            workbook = SXSSFMissPageCreatExcel(ids, strcode, strname);
            //创建字段模板
            String dir = ConfigValue.getPath("system.document.rootpath");
            String path = dir + "/OAFile" + "/Excel导出/临时目录/" + fileName;//
//            createTemp(nodeId, strcode, strname, path + "/", ids.length);
            //Workbook workbook = CreateExcel.CreateExcle(fileName, entryList, strcode, strname);
            //entryList = null;
            CreateExcel.ExportExcel(workbook, fileName);
            // zip 完整路径
            zippath = dir + "/OAFile" + "/Excel导出/" + fileName + ".zip";
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, zipPassword);
            //wirteFile(response,zippath,fileName,zipRturn,succ);//读取压缩包，发送页面
            ZipUtils.del(path);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workbook.dispose();
        }
        return zippath;
    }

    /**
     * 生成漏页信息excel
     *
     * @param ids     条目id集合
     * @param
     * @param strcode code集合
     * @param strname 字段名集合
     * @return 返回workbook对象
     * @throws Exception
     */
    public SXSSFWorkbook SXSSFMissPageCreatExcel(String[] ids, String[] strcode, String[] strname) throws Exception {
        //1.创建excel文件---用在保存循环的数据--循环写入
        //2.创建工作簿  SXSSFWorkbook 支持最大行1048576
        SXSSFWorkbook workbook = new SXSSFWorkbook(10);
        Sheet sheet = workbook.createSheet();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {//每个元素都是进行截取过的 900条id
                List<Tb_entry_index_capture> index_captures = entryIndexCaptureRepository.findByEntryidIn(listStr.get(i));
                List<RebackMissPageCheck> rebackMissPageChecks = new ArrayList<>();
                for(Tb_entry_index_capture index_capture : index_captures){
                    RebackMissPageCheck missPageCheck = new RebackMissPageCheck();
                    if(index_capture.getEleid()!=null&&index_capture.getPages()!=null&&!"".equals(index_capture.getPages())&&
                            !"".equals(index_capture.getEleid())) {
                        if (index_capture.getPages().equals(index_capture.getEleid())) {
                            missPageCheck.setResult("通过");
                        } else if (Integer.parseInt(index_capture.getPages()) < Integer.parseInt(index_capture.getEleid())) {
                            int count = Integer.parseInt(index_capture.getEleid()) - Integer.parseInt(index_capture.getPages());
                            missPageCheck.setResult("多" + count + "页");
                        } else {
                            int count = Integer.parseInt(index_capture.getPages()) - Integer.parseInt(index_capture.getEleid());
                            missPageCheck.setResult("漏" + count + "页");
                        }
                    }
                    missPageCheck.setArchivecode(index_capture.getArchivecode());
                    missPageCheck.setPage(index_capture.getPages());
                    missPageCheck.setElenumber(index_capture.getEleid());
                    missPageCheck.setId(index_capture.getEntryid());
                    rebackMissPageChecks.add(missPageCheck);
                }
                //每900条写入1次
                sheet = CreateExcel.SXSSFMissPageWorkbookCreateExcle(sheet, rebackMissPageChecks, strcode, strname);
            }
        }
        return workbook;
    }


    //调用生成漏页信息excel方法 数据管理
    public String createMissPageExcelManagement(String[] ids,String fileName, HttpServletResponse response,String zipPassword) {
        String[] strcode = new String[5];
        String[] strname = new String[5];
        strcode[0] = "id";
        strname[0] = "条目ID";
        strcode[1] = "archivecode";
        strname[1] = "档号";
        strcode[2] = "page";
        strname[2] = "页数";
        strcode[3] = "elenumber";
        strname[3] = "原文数量";
        strcode[4] = "result";
        strname[4] = "结果";
        String zippath = "";
        SXSSFWorkbook workbook =null;
        try {
            workbook = SXSSFMissPageCreatExcelManagement(ids, strcode, strname);
            //创建字段模板
            String dir = ConfigValue.getPath("system.document.rootpath");
            String path = dir + "/OAFile" + "/Excel导出/临时目录/" + fileName;//
//            createTemp(nodeId, strcode, strname, path + "/", ids.length);
            //Workbook workbook = CreateExcel.CreateExcle(fileName, entryList, strcode, strname);
            //entryList = null;
            CreateExcel.ExportExcel(workbook, fileName);
            // zip 完整路径
            zippath = dir + "/OAFile" + "/Excel导出/" + fileName + ".zip";
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, zipPassword);
            //wirteFile(response,zippath,fileName,zipRturn,succ);//读取压缩包，发送页面
            ZipUtils.del(path);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workbook.dispose();
        }
        return zippath;
    }

    /**
     * 生成漏页信息excel  数据管理
     *
     * @param ids     条目id集合
     * @param
     * @param strcode code集合
     * @param strname 字段名集合
     * @return 返回workbook对象
     * @throws Exception
     */
    public SXSSFWorkbook SXSSFMissPageCreatExcelManagement(String[] ids, String[] strcode, String[] strname) throws Exception {
        //1.创建excel文件---用在保存循环的数据--循环写入
        //2.创建工作簿  SXSSFWorkbook 支持最大行1048576
        SXSSFWorkbook workbook = new SXSSFWorkbook(10);
        Sheet sheet = workbook.createSheet();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {//每个元素都是进行截取过的 900条id
                List<Tb_entry_index> index_captures = entryIndexRepository.findByEntryidIn(listStr.get(i));
                List<RebackMissPageCheck> rebackMissPageChecks = new ArrayList<>();
                for(Tb_entry_index index_capture : index_captures){
                    RebackMissPageCheck missPageCheck = new RebackMissPageCheck();
                    if(index_capture.getEleid()!=null&&index_capture.getPages()!=null&&!"".equals(index_capture.getPages())&&
                            !"".equals(index_capture.getEleid())) {
                        if (index_capture.getPages().equals(index_capture.getEleid())) {
                            missPageCheck.setResult("通过");
                        } else if (Integer.parseInt(index_capture.getPages()) < Integer.parseInt(index_capture.getEleid())) {
                            int count = Integer.parseInt(index_capture.getEleid()) - Integer.parseInt(index_capture.getPages());
                            missPageCheck.setResult("多" + count + "页");
                        } else {
                            int count = Integer.parseInt(index_capture.getPages()) - Integer.parseInt(index_capture.getEleid());
                            missPageCheck.setResult("漏" + count + "页");
                        }
                    }
                    missPageCheck.setArchivecode(index_capture.getArchivecode());
                    missPageCheck.setPage(index_capture.getPages());
                    missPageCheck.setElenumber(index_capture.getEleid());
                    missPageCheck.setId(index_capture.getEntryid());
                    rebackMissPageChecks.add(missPageCheck);
                }
                //每900条写入1次
                sheet = CreateExcel.SXSSFMissPageWorkbookCreateExcle(sheet, rebackMissPageChecks, strcode, strname);
            }
        }
        return workbook;
    }

    //库房管理入库出库记录导出
    public String exportInware(String[] ids,String fileName, HttpServletResponse response,String zipPassword) {
        String[] strcode = new String[6];
        String[] strname = new String[6];
        strcode[0] = "entryid";
        strname[0] = "条目ID";
        strcode[1] = "archivecode";
        strname[1] = "档号";
        strcode[2] = "title";
        strname[2] = "题名";
        strcode[3] = "entrystorage";
        strname[3] = "存储位置";
        strcode[4] = "filecode";
        strname[4] = "案卷号";
        strcode[5] = "filenumber";
        strname[5] = "文件编号";
        String zippath = "";
        SXSSFWorkbook workbook =null;
        try {
            workbook = SXSSFMissPageCreatExcelInware(ids, strcode, strname);
            //创建字段模板
            String dir = ConfigValue.getPath("system.document.rootpath");
            String path = dir + "/OAFile" + "/Excel导出/临时目录/" + fileName;//
//            createTemp(nodeId, strcode, strname, path + "/", ids.length);
            //Workbook workbook = CreateExcel.CreateExcle(fileName, entryList, strcode, strname);
            //entryList = null;
            CreateExcel.ExportExcel(workbook, fileName);
            // zip 完整路径
            zippath = dir + "/OAFile" + "/Excel导出/" + fileName + ".zip";
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, zipPassword);
            //wirteFile(response,zippath,fileName,zipRturn,succ);//读取压缩包，发送页面
            ZipUtils.del(path);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workbook.dispose();
        }
        return zippath;
    }

    /**
     * 库房管理入库出库记录导出
     *
     * @param ids     条目id集合
     * @param
     * @param strcode code集合
     * @param strname 字段名集合
     * @return 返回workbook对象
     * @throws Exception
     */
    public SXSSFWorkbook SXSSFMissPageCreatExcelInware(String[] ids, String[] strcode, String[] strname) throws Exception {
        //1.创建excel文件---用在保存循环的数据--循环写入
        //2.创建工作簿  SXSSFWorkbook 支持最大行1048576
        SXSSFWorkbook workbook = new SXSSFWorkbook(10);
        Sheet sheet = workbook.createSheet();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {//每个元素都是进行截取过的 900条id
                List<Tb_entry_index> indexs = entryIndexRepository.findByEntryidIn(listStr.get(i));
                List<Entry> entryList = new ArrayList<>();
                for(Tb_entry_index index : indexs){
                    Entry entry = new Entry();
                    Tb_entry_detail detail = entryDetailRepository.findByEntryid(index.getEntryid());
                    entry.setEntryDetial(detail);
                    entry.setEntryIndex(index);
                    entryList.add(entry);
                }
                //每900条写入1次
                sheet = CreateExcel.SXSSFWorkbookCreateExcle(sheet, entryList, strcode, strname);
            }
        }
        return workbook;
    }
    //导出columnNames字段模板
    public void exportColumnNames(HttpServletRequest request, HttpServletResponse response, String[] columnNames) {
        String[] fieldName = columnNames;
        String tempFileName;
        if(fieldName[0].equals("账号")){
            tempFileName = "用户模板";
        }else if(fieldName[0].equals("机构名称")){
            tempFileName = "机构模板";
        }else {
            tempFileName = "设备模板";
        }
        //创建字段模板
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + File.separator + "OAFile" + File.separator + "导出模板" + File.separator;//
        try {
            File f = new File(path);
            f.mkdirs();
            if (!f.exists()) {
                throw new RuntimeException("createXml()---创建文件夹失败");
            }
            //Workbook workbook = CreateExcel.createTemp(fieldCode, fieldName);//---创建excel字段模板
            Workbook workbook = CreateExcel.createFieldNameTemp(fieldName);
            OutputStream os = new FileOutputStream(new File(path + File.separator + tempFileName + ".xls"));
            workbook.write(os);
            os.flush();
            os.close();
            workbook.close();
            //创建xml字段模板
            //将2个文件进行压缩
            InputStream inputStream = new FileInputStream(new File(path + File.separator + tempFileName + ".xls"));
            OutputStream out = response.getOutputStream();
            String userAgent = request.getHeader("user-agent").toLowerCase();
            if (userAgent.contains("msie") || userAgent.contains("like gecko")) {
                // win10 ie edge 浏览器 和其他系统的ie
                tempFileName = URLEncoder.encode(tempFileName, "UTF-8");
            } else {
                // fe
                tempFileName = new String(tempFileName.getBytes("utf-8"), "iso-8859-1");
            }
            response.setContentType("application/octet-stream;charset=UTF-8");
//            response.setHeader("Content-Disposition",
//                    "attachment;filename=\"" + new String((nodeName + ".xlsx").getBytes(), "iso-8859-1") + "\"");
            response.setHeader("Content-Disposition",
                    "attachment;fileName=" + tempFileName + ".xlsx");

            byte[] b = new byte[1024 * 1024 * 10];
            int leng = 0;
            while ((leng = inputStream.read(b)) != -1) {
                out.write(b, 0, leng);
            }
            out.flush();
            inputStream.close();
            out.close();
            ZipUtils.delFolder(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //评价记录导出
    public String exportAppraise(String[] feedbackids,String fileName,String zipPassword) {
        String[] namearr = {"评价人", "评分", "评分星数", "评价内容", "评价类型"};
        String[] codearr = {"askman", "appraise","appraisestar","appraisetext","appraisetype"};
        String zippath = "";
        SXSSFWorkbook workbook =null;
        try {
            workbook = SXSSFMissPageCreatExcelAppraise(feedbackids, codearr, namearr);
            //创建字段模板
            String dir = ConfigValue.getPath("system.document.rootpath");
            String path = dir + "/OAFile" + "/Excel导出/临时目录/" + fileName;//
//            createTemp(nodeId, strcode, strname, path + "/", ids.length);
            //Workbook workbook = CreateExcel.CreateExcle(fileName, entryList, strcode, strname);
            //entryList = null;
            CreateExcel.ExportExcel(workbook, fileName);
            // zip 完整路径
            zippath = dir + "/OAFile" + "/Excel导出/" + fileName + ".zip";
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, zipPassword);
            ZipUtils.del(path);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workbook.dispose();
        }
        return zippath;
    }

    /**
     * 评价导出
     *
     * @param feedbackids     条目id集合
     * @param
     * @param strcode code集合
     * @param strname 字段名集合
     * @return 返回workbook对象
     * @throws Exception
     */
    public SXSSFWorkbook SXSSFMissPageCreatExcelAppraise(String[] feedbackids, String[] strcode, String[] strname) throws Exception {
        //1.创建excel文件---用在保存循环的数据--循环写入
        //2.创建工作簿  SXSSFWorkbook 支持最大行1048576
        SXSSFWorkbook workbook = new SXSSFWorkbook(10);
        Sheet sheet = workbook.createSheet();
        if (feedbackids.length > 0) {
            List<String[]> listStr = splitAry(feedbackids, 900);
            for (int i = 0; i < listStr.size(); i++) {//每个元素都是进行截取过的 900条id
                List<Tb_feedback> feedbacks = feedbackRepository.findByFeedbackidIn(listStr.get(i));
                List<Appraise> appraises = new ArrayList<>();
                for(Tb_feedback feedback : feedbacks){
                    Appraise appraise = new Appraise();
                    appraise.setAskman(feedback.getAskman());
                    appraise.setAppraise(feedback.getAppraise());
                    appraise.setAppraisetext(feedback.getAppraisetext());
                    if(feedback.getBorrowdocid()!=null&&!"".equals(feedback.getBorrowdocid())){
                        appraise.setAppraisetype("借阅评价");
                    }else{
                        appraise.setAppraisetype("使用评价");
                    }
                    String appraisestr = feedback.getAppraise();
                    if ("无可挑剔".equals(appraisestr)) {
                        appraise.setAppraisestar("5星");
                    } else if ("非常满意".equals(appraisestr)){
                        appraise.setAppraisestar("4星");
                    } else if ("满意".equals(appraisestr)){
                        appraise.setAppraisestar("3星");
                    } else if ("一般".equals(appraisestr)){
                        appraise.setAppraisestar("2星");
                    }else{
                        appraise.setAppraisestar("1星");
                    }
                    appraises.add(appraise);
                }
                //每900条写入1次
//                sheet = CreateExcel.SXSSFWorkbookCreateExcleAppraise(sheet, appraises, strcode, strname);
            }
        }
        return workbook;
    }

    /**
     * 页面用户输入的文件名
     * @param nodeid
     * @param entryids
     * @param currentnode
     * @param datareceive
     * @return
     */
    public String exportDataopenRelease(String nodeid, String entryids, String currentnode,Tb_datareceive datareceive) {
        String[] ids = entryids.split(",");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(new Date());
        String usefileName = currentnode + "_" + time + "_条数_" + ids.length;
        String[] fieldcod = getFieldCodes(nodeid);//--文件模板
        List<Tb_entry_index> indexList = getEntryIndexList(ids);
        //List<Tb_entry_detail> details = getEntryDetailList(ids);
        List<Entry> entryList = createEntrtList(indexList,ids);
        List<String> userFieldNameList = getUserFieldName(fieldcod, nodeid);
        String[] fieldname = new String[userFieldNameList.size()];
        userFieldNameList.toArray(fieldname);
        String[] strcode = new String[fieldcod.length + 1];
        String[] strname = new String[fieldname.length + 1];
        strcode[strcode.length - 1] = "entryid";
        strname[strname.length - 1] = "条目ID";
        System.arraycopy(fieldcod, 0, strcode, 0, fieldcod.length);
        System.arraycopy(fieldname, 0, strname, 0, fieldname.length);
        List<Tb_electronic> electronics = getElectronic(ids);

        //存放档号
        String archivecode = "";
        for (int i = 0; i < electronics.size(); i++) {
            for (int j = 0; j < entryList.size(); j++) {
                String eleEntryId = electronics.get(i).getEntryid().trim();
                String entryid = entryList.get(j).getEntryid().trim();
                if (entryid.equals(eleEntryId)) {
                    archivecode = entryList.get(j).getArchivecode();
                }
            }
            String filepath = electronics.get(i).getFilepath();//路径
            String filename = electronics.get(i).getFilename();//文件名
            String file = rootpath + filepath + File.separator + filename;//完整路径(目标文件)
            String newUserFileName = "";
            boolean b = Arrays.asList(strcode).contains("archivecode");
            if (!b || (archivecode == null) || "".equals(archivecode)) {//-当自选字段中没有档号时/档号为空/未归管理-使用条目id为原文目录名
                newUserFileName = usefileName + File.separator + "document"+ File.separator + electronics.get(i).getEntryid().trim();//拷贝路径
            } else {
                String str = archivecode.replaceAll("\\·", "-");
                newUserFileName = usefileName + File.separator + "document"+ File.separator + str;//拷贝路径
            }
            try {
                FileUtil.CopyDataopenFile(new File(file), newUserFileName, filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //清空档号字符串
            archivecode = "";
        }
        String zippath = "";
        SXSSFWorkbook workbook = null;
        try {
            workbook = SXSSFCreatExcelAndCopyFile(ids, usefileName, strcode, strname);
            String path =
                    rootpath + File.separator + "datarelease" + File.separator + "dataopen" +File.separator  + "临时目录" + File.separator + usefileName;
            // 创建临时路径文件夹
            File f = new File(path);
            f.mkdirs();
            if (!f.exists()) {
                throw new RuntimeException("createXml()---创建文件夹失败");
            }
            try {
                String newpath = path + File.separator + time + ".xlsx";
                FileOutputStream fout = new FileOutputStream(newpath);
                workbook.write(fout);
                workbook.close();
                fout.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            // zip 完整路径
            zippath =
                    rootpath + File.separator + "datarelease" + File.separator + "dataopen"+ File.separator + "待接收" + File.separator + usefileName + ".zip";
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, "");
            ZipFile zipFile =new ZipFile(new File(zippath));
            datareceive.setNodeid(nodeid);
            //由于压缩包的注释中文会乱码，所以需要把它转换成unicode,拿出来的再转回来
            datareceive.setCurrentnode(gbEncoding(currentnode));
            datareceive.setTransfertitle(gbEncoding(datareceive.getTransfertitle()));
            datareceive.setTransdesc(gbEncoding(datareceive.getTransdesc()));
            datareceive.setTransuser(gbEncoding(datareceive.getTransuser()));
            datareceive.setTransorgan(gbEncoding(datareceive.getTransorgan()));
            datareceive.setSequencecode(gbEncoding(datareceive.getSequencecode()));
            zipFile.setComment(JSON.toJSONString(datareceive));
//            zipFile.setComment("测试测试");
            ZipUtils.del(path);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workbook.dispose();
        }
        return zippath;
    }
    public String gbEncoding(String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }
    //导出机构
    public void exporOrgan(List<String> fieldcode, List<String> fieldname, String[] ids, HttpServletResponse response) {
        //文件名
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = sdf.format(new Date());
        //excel生成路径
        String excelPath = rootpath + File.separator + "OAFile" + File.separator + "Excel导出" + File.separator + dateStr + File.separator;
        if (!new File(excelPath).exists()) {
            new File(excelPath).mkdirs();
        }

        //获取对象
        List<Tb_right_organ> organs = rightOrganRepository.findByOrganid(ids);
        //创建excel并写入对象
        try {
            Workbook workbook = CreateExcel.createOrganExcel(organs, fieldname, fieldcode);
            workbook.write(new FileOutputStream(excelPath + dateStr + "-导出机构.xlsx"));
            InputStream inputStream = new FileInputStream(new File(excelPath + dateStr + "-导出机构.xlsx"));
            OutputStream out = response.getOutputStream();
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename=\"" + new String((dateStr + "-导出机构.xlsx").getBytes(), "iso-8859-1") + "\"");
            byte[] b = new byte[1024 * 1024 * 10];
            int leng = 0;
            while ((leng = inputStream.read(b)) != -1) {
                out.write(b, 0, leng);
            }
            out.flush();
            inputStream.close();
            out.close();
            new File(excelPath).delete();
        } catch (
                Exception e) {
            throw new RuntimeException("生成excel失败", e);
        }
    }

    //导出用户
    public void expUse(String[] userId,HttpServletRequest request,HttpServletResponse response){
        //前置参数
        String[] fieldcod = new String[]{"loginname","realname","phone","address","sex","organusertype","duty","status"};
        String[] fieldname = new String[]{"账号","用户姓名","电话","地址","性别","机构人员类型","人员职位","用户状态"};
        //1.创建excel文件---用在保存循环的数据--循环写入
        //2.创建工作簿  SXSSFWorkbook 支持最大行1048576
        SXSSFWorkbook workbook = null;
        OutputStream os = null;
        InputStream inputStream = null;
        OutputStream out = null;
        try {
            workbook = new SXSSFWorkbook(10);
            Sheet sheet = workbook.createSheet();
            if (null != userId) {
                //1.根据用户id 查出需要导出的用户
                List<String[]> list = splitAry(userId, 900);
                for (String[] arr : list) {
                    List<Tb_user> users = userRepository.findByUseridIn(arr);
                    //每900条写入1次
                    sheet = CreateExcel.SXSSFWorkbookCreateUseExcle(sheet, users, fieldcod, fieldname);
                }
                String dir = ConfigValue.getPath("system.document.rootpath");
                String path = dir + File.separator + "OAFile" + File.separator + "导出模板" + File.separator;//
                File f = new File(path);
                f.mkdirs();
                if (!f.exists()) {
                    throw new RuntimeException("expUse()---创建文件夹失败");
                }
                SimpleDateFormat smf = new SimpleDateFormat("yyyyMMddHHmmss");
                String filename = smf.format(new Date());
                os = new FileOutputStream(new File(path + File.separator + filename + ".xlsx"));
                workbook.write(os);
                inputStream = new FileInputStream(new File(path + File.separator + filename + ".xlsx"));
                out = response.getOutputStream();
                response.setContentType("application/octet-stream;charset=UTF-8");
                response.setHeader("Content-Disposition",
                        "attachment;fileName=" + filename + ".xlsx");
                byte[] b = new byte[1024 * 1024 * 10];
                int leng = 0;
                while ((leng = inputStream.read(b)) != -1) {
                    out.write(b, 0, leng);
                }
                os.flush();
                out.flush();
                ZipUtils.delFolder(path);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null!=workbook){
                workbook.dispose();
                try {
                    workbook.close();
                }catch (IOException io){
                    io.printStackTrace();
                }
            }
            if(null!=os){
                try{
                    os.close();
                }catch (IOException io){
                    io.printStackTrace();
                }
            }
            if(inputStream!=null){
                try{
                    inputStream.close();
                }catch (IOException io){
                    io.printStackTrace();
                }
            }
            if(out!=null){
                try{
                    out.close();
                }catch (IOException io){
                    io.printStackTrace();
                }
            }
        }
    }

    //调用生成xml方法
    public void createThematicxml(String thematicPath,List<Tb_thematic_detail> detailList) throws Exception {
        String[] fieldcode =new String[]{"title","filedate","responsibleperson","filecode","subheadings","mediatext","thematicdetilid"};
        String[] fieldname =new String[]{"题名","时间","责任者","文件编号","主题词","电子文件","专题编研ID"};
        String[] fieldProperty = new String[]{"varchar(1000)","varchar(20)","varchar(30)","varchar(20)","varchar(100)","varchar(500)","char(36)"};
        String filename = "专题目录.xml";
        // 创建xml
        Document document = DocumentHelper.createDocument();// 创建Doucument对象
        Element root = document.addElement("table"); // 根节点
        root.addAttribute("tablename", filename);
        for (int i = 0; i < detailList.size(); i++) {// 遍历数据集
            Tb_thematic_detail tbThematicDetail =detailList.get(i);
            Element record = root.addElement("record");// 创建record节点
            for (int j = 0; j < fieldcode.length; j++) {
                String value = String.valueOf(ValueUtil.getPoFieldValue(fieldcode[j], tbThematicDetail));
                Element field = record.addElement(fieldcode[j]);
                field.addAttribute("property",fieldProperty[j]);
                field.addAttribute("fieldname", fieldname[j]);
                field.addText(value);// 给节点添加文本内容
            }
        }
        String newPath = thematicPath + File.separator + filename;
        // 生成xml文件
        File xmlFile = new File(newPath);
        FileOutputStream fileOutputStream =new FileOutputStream(xmlFile);
        // 能输出流写入新xml文件
        XMLWriter writer = new XMLWriter(new FileOutputStream(xmlFile));
        writer.write(document);
        writer.close();
        fileOutputStream.close();
    }

    public void createLongRetention(String entryid, HttpServletResponse response) {
        Tb_entry_index entryIndex = entryIndexRepository.findByEntryid(entryid);
        String zippath = longRetentionService.longRetention(entryIndex.getEntryid(),"");
        try {
            wirteFile(response, zippath, entryIndex.getTitle(), "ok", true);
        } catch (Exception e) {
            throw new RuntimeException("长期保管包生成失败", e);
        }
    }


}
