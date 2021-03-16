package com.wisdom.web.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wisdom.util.*;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.EntryIndexTempRepository;
import com.wisdom.web.repository.RightOrganRepository;
import com.wisdom.web.repository.SzhArchivesCalloutRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 数据导入控制器
 * Created by xd on 2017/10/17.
 */
@Controller
@RequestMapping(value = "/import")
public class ImportController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LogService logService;
    @Autowired
    ImportService importService;
    @Autowired
    ExportExcelService excelService;
    @Autowired
    NodesettingService nodesettingService;
    @Autowired
    RightOrganRepository rightOrganRepository;
    @Autowired
    SzhArchivesCalloutRepository szhArchivesCalloutRepository;
    @Autowired
    EntryIndexTempRepository entryIndexTempRepository;
    @Autowired
    TemplateController templateController;
    @Value("${system.document.rootpath}")
    private String rooPath;


    private int importXmlFailureCount = 0;//导入excel成功数
    private int importExcelFailureCount = 0; //导入excel失败数
    private int importZipFailureCount = 0;//导入zip失败
    private static final String IMPORT_ZIP = ".zip";


    @RequestMapping("/main")
    public String main() {
        return "/inlet/import";
    }

    @RequestMapping("/importData")
    @ResponseBody
    public boolean importData(@RequestParam("import") MultipartFile file) {
        // 获取文件名
        logger.info("fileName:" + file.getOriginalFilename());
//        String fileName = file.getOriginalFilename();
        String columnNames[] = {"操作人", "用户名", "机构", "IP地址", "模块", "操作时间", "操作描述"};
        ImportUtil importUtil = new ImportUtil(columnNames);
        List<Tb_log_msg> logDetails = importUtil.importFile(file);
        logService.saveLogDetail(logDetails);
        return true;
    }

    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    @ResponseBody
    public synchronized void importexcel(HttpServletResponse response, HttpServletRequest request, String NodeIds, String filename, String isRepeat) throws Exception {
        //1.获取上传文件---判断后缀名
        //String fileTransferPath = importService.fileTransfer(request);//上传文件转存后的路径
        //String fileTransferPath = importService.fileTransfer(request);//上传文件转存后的路径
        String fileTransferPath = rooPath + File.separator + "OAFile" + File.separator + "upload" + File.separator + filename;
        String suffixName = filename.substring(filename.indexOf(".") + 1);//文件后缀
        String fileName = filename.substring(0, filename.lastIndexOf("."));//不带后缀的文件名
        String[] fieldName = excelService.getFieldNames(NodeIds);
        String[] fieldCodes = excelService.getFieldCodes(NodeIds);
        String[] strcode = new String[fieldCodes.length + 1];
        String[] strname = new String[fieldName.length + 1];
        strcode[strcode.length - 1] = "entryid";
        strname[strname.length - 1] = "条目ID";
        System.arraycopy(fieldCodes, 0, strcode, 0, fieldCodes.length);
        System.arraycopy(fieldName, 0, strname, 0, fieldName.length);
        SimpleDateFormat smf = new SimpleDateFormat("YYYYMMddHHmmss");
        Date date = new Date();
        String UnZipPath = rooPath + File.separator + "OAFile" + File.separator + "zip解压目录" + File.separator + smf.format(date);

        if ("xls".equals(suffixName.toLowerCase()) || "xlsx".equals(suffixName.toLowerCase())) {
            importExcelFailureCount = importService.excelSave(fileTransferPath, strname, strcode, NodeIds, fileName, isRepeat);

        }
        if ("xml".equals(suffixName)) {
            importXmlFailureCount = importService.xmlSave(fileTransferPath, strcode, strname, NodeIds, fileName, isRepeat);
        }
        if ("zip".equals(suffixName)) {
            ZipUtils.deCompress(fileTransferPath, UnZipPath);//解压文件
            //String UnZipFile = UnZipPath + "/" + fileName;//解压后的目录路径
            //读取解压目录下的xml或excel文件
            importZipFailureCount = importService.zipSave(UnZipPath, strname, strcode, NodeIds, fileName, isRepeat);
        }
        ZipUtils.del(UnZipPath);
        //ZipUtils.del(rooPath +"/OAFile"+ "/upload");
        ZipUtils.del(fileTransferPath);
    }

    //数据采集--导入
    @RequestMapping(value = "/captureImportexcel", method = RequestMethod.POST)
    @ResponseBody
    public synchronized void captureImportexcel(HttpServletResponse response, HttpServletRequest request, String NodeIds, String filename) throws Exception {
        //1.获取上传文件---判断后缀名
       /* String fileTransferPath = importService.fileTransfer(request);//上传文件转存后的路径
        String suffixName = fileTransferPath.substring(fileTransferPath.indexOf(".") + 1);
        String[] str = fileTransferPath.split("/");
        String fileName = fileTransferPath.split("/")[1].substring(0, str[1].indexOf("."));*/
        String fileTransferPath = rooPath + File.separator + "OAFile" + File.separator + "upload" + File.separator + filename;
        String suffixName = filename.substring(filename.indexOf(".") + 1);//文件后缀
        String fileName = filename.substring(0, filename.lastIndexOf("."));//不带后缀的文件名
        String[] fieldName = excelService.getFieldNames(NodeIds);
        String[] fieldCodes = excelService.getFieldCodes(NodeIds);
        String[] strcode = new String[fieldCodes.length + 1];
        String[] strname = new String[fieldName.length + 1];
        strcode[strcode.length - 1] = "entryid";
        strname[strname.length - 1] = "条目ID";
        System.arraycopy(fieldCodes, 0, strcode, 0, fieldCodes.length);
        System.arraycopy(fieldName, 0, strname, 0, fieldName.length);

        SimpleDateFormat smf = new SimpleDateFormat("YYYYMMddHHmmss");
        Date date = new Date();
        String UnZipPath = rooPath + File.separator + "OAFile" + File.separator + "zip解压目录" + File.separator + smf.format(date);

        if ("xls".equals(suffixName.toLowerCase()) || "xlsx".equals(suffixName.toLowerCase())) {
            importExcelFailureCount = importService.captureExcelSave(fileTransferPath, strname, strcode, NodeIds, fileName);
        }
        if ("xml".equals(suffixName)) {
            importXmlFailureCount = importService.capturexmlSave(fileTransferPath, strcode, strname, NodeIds, fileName);
        }
        if ("zip".equals(suffixName)) {
            ZipUtils.deCompress(fileTransferPath, UnZipPath);//解压文件
            //String UnZipFile = UnZipPath + "/" + fileName;//解压后的目录路径
            //读取解压目录下的xml或excel文件
            importZipFailureCount = importService.captureZipSave(UnZipPath, strname, strcode, NodeIds, fileName);
        }
        ZipUtils.del(UnZipPath);
        ZipUtils.del(fileTransferPath);
    }


    //下载导入失败文件
    @RequestMapping("downloadImportFailure")
    @ResponseBody
    public synchronized void downloadImportFailure(HttpServletResponse response) {
        List<String> fileList = FileUtil.getFile(rooPath + File.separator + "OAFile" + File.separator + "导入失败" + File.separator);
        List<String> eleFolder = FileUtil.getFolder(rooPath + File.separator + "OAFile" + File.separator + "导入失败");
        try {
            if (fileList.size() > 0) {
                importService.downloadImportFailure(response, fileList);
            }
            if (fileList.size() == 0) {
                for (int i = 0; i < eleFolder.size(); i++) {
                    List<String> filelist = FileUtil.getFile(eleFolder.get(i));
                    importService.downloadImportFailure(response, filelist);
                }
            }
            ZipUtils.del(rooPath + File.separator + "OAFile" + File.separator + "导入失败");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //失败后删除生成的失败压缩包
    @RequestMapping("/deleteFailureFile")
    @ResponseBody
    public void deleteFailureFile(String confirm) {
        if ("confirm".equals(confirm)) {
            try {
                ZipUtils.del(rooPath + File.separator + "OAFile" + File.separator + "导入失败");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //向页面传输失败数，页面判断失败数，只要数大于0就弹出提示 进行下载
    @RequestMapping("isimport")
    @ResponseBody
    public synchronized ExtMsg isImport() {
        int xmlFailureCount = importXmlFailureCount;
        int excelFailureCount = importExcelFailureCount;
        int ZipFailureCount = importZipFailureCount;
        Map<String, Integer> count = new HashMap<>();
        count.put("importXmlFailureCount", xmlFailureCount);
        count.put("importExcelFailureCount", excelFailureCount);
        count.put("importZipFailureCount", ZipFailureCount);
        if (xmlFailureCount > 0 || excelFailureCount > 0 || ZipFailureCount > 0) {//进行清零 --下次重新计算
            importXmlFailureCount = 0;
            importExcelFailureCount = 0;
            importZipFailureCount = 0;
        }
        ExtMsg extMsg = new ExtMsg(true, "count", count);
        return extMsg;
    }


    //文件分片保存
    @RequestMapping(value = "/importFileTransfer/{entrytype}", method = RequestMethod.POST)
    public synchronized void importFileTransfer(HttpServletResponse response, HttpServletRequest request) throws Exception {
        Map<String, Object> params = parse(request);
        if ((boolean) params.get("mutipart")) {
            if (params.get("chunk") != null) { // 文件分片上传
                importService.uploadchunk(params);
            } else { // 文件单片上传
                importService.uploadfile(params);
            }
        }
    }

    public Map<String, Object> parse(HttpServletRequest request) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        boolean isMutipart = ServletFileUpload.isMultipartContent(request);
        result.put("mutipart", isMutipart);
        if (isMutipart) {
            StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
            result.put("id", req.getParameter("id"));
            result.put("filename", req.getParameter("name"));
            result.put("chunk", req.getParameter("chunk"));
            result.put("chunks", req.getParameter("chunks"));

            Iterator iterator = req.getFileNames();
            while (iterator.hasNext()) {
                MultipartFile file = req.getFile((String) iterator.next());
                result.put("size", file.getSize());
                result.put("content", file.getBytes());
            }
        }
        return result;
    }

    //-----------------------------------------------------选字段导入---------------------------------------------//
    //导入界面加载资源节点请求
    @RequestMapping("/datanodes")
    @ResponseBody
    public List<Map<String, Object>> findDataNodes(String pid) {
        List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
        List<Object[]> nodeList = importService.findDataNodes(pid);
        Map<String, Object> map;
        for (Object[] node : nodeList) {
            String nodeName = (String) node[1];
            if (nodeName.contains("卷内管理")) {
                continue;
            }
            map = new HashMap<String, Object>();
            map.put("fnid", node[0]);
            map.put("text", node[1]);
            map.put("leaf", node[2]);
            resList.add(map);
        }
        return resList;
    }

    @RequestMapping("/template")
    @ResponseBody
    public List<Map<String, Object>> findTemplates(String nodeid, boolean isEntryStorage) {
        List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
        List<Tb_data_template> templateList = importService.findTemplates(nodeid);
        Map<String, Object> map;
        if (isEntryStorage) {//导入实体档案时专用的存储位置
            map = new HashMap<String, Object>();
            map.put("fieldcode", "entrystorage");
            map.put("fieldname", "存储位置");
            resList.add(map);
        }
        for (Tb_data_template template : templateList) {
            map = new HashMap<String, Object>();
            map.put("fieldcode", template.getFieldcode());
            map.put("fieldname", template.getFieldname());
            resList.add(map);
        }
        Map<String, Object> entryid_map = new HashMap<>();
        entryid_map.put("fieldcode", "entryid");
        entryid_map.put("fieldname", "条目ID");
        resList.add(entryid_map);
        return resList;
    }

    //读取文件---返回10行预览数据
    @RequestMapping("/upload")
    @ResponseBody
    public String uploadFile(@RequestParam("source") MultipartFile source, HttpServletRequest request,
                             String systype, HttpServletResponse response) throws Exception {
        ObjectMapper json = new ObjectMapper();
        String jsonString = json.writeValueAsString(importService.getDataStruct(source));
        return jsonString;
    }

    //读取文件---返回10行预览数据
    @RequestMapping("/uploadOpenData")
    @ResponseBody
    public ExtMsg uploadOpenData(String source) throws Exception {
        File pdfFile = new File(source);
        FileInputStream fileInputStream = new FileInputStream(pdfFile);
        MultipartFile multipartFile = new MockMultipartFile(pdfFile.getName(), pdfFile.getName(),
                ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
        ObjectMapper json = new ObjectMapper();
        String jsonString = json.writeValueAsString(importService.getDataStruct(multipartFile));
        return new ExtMsg(true, "", jsonString);
    }

    //根据节点名 返回对应节点的字段模板
    @RequestMapping("/template/init")
    @ResponseBody
    public List<Map<String, String>> findInitTemplate(String nodeid, boolean isEntryStorage) {
        List<Map<String, String>> resList = importService.getTempField(nodeid, isEntryStorage);
        return resList;
    }

    /**
     * @param fields          字段
     * @param filePath        文件上传预览后转存全路径
     * @param filename        文件全路径
     * @param target          目标节点id
     * @param isEntityStorage 是否为实体档案入库的导入，因为实体档案会要把存储位置去判断是否存在， 存在才会把条目和实体档案插入到数据中。
     * @return
     */
    @RequestMapping("/import")
    @ResponseBody
    public Map<String, Object> importData(String fields, String filePath,
                                          String filename, String target,
                                          String isRepeat, boolean isEntityStorage,
                                          boolean taitanXml, boolean socialSecurityXml, String wg11Index, boolean autoCreateArchivecode) throws Exception {
        JSONArray jsonArray = JSONArray.parseArray(fields);
        Map<String, Object> map = new HashMap<>();
        //读取字段配置
        List<String> keymapList = new ArrayList<String>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = JSONObject.parseObject(jsonArray.get(i).toString());
            if (obj.get("target") != null && !"".equals(obj.get("target"))) {
                int count = i;
                if (isEntityStorage) {//如果是实体档案导入就取keymapList的长度作为字段顺序号，这个顺序才不会乱。
                    count = keymapList.size();
                }
                keymapList.add(count + "," + obj.get("source") + "," + obj.get("target"));
            }
        }
        String erroMessage = "";
        if (taitanXml) {//bs 的xml
            map = importService.importBsXml(filename, filePath, target, keymapList, isRepeat, wg11Index);
        } else if (socialSecurityXml) { //社保中心的xml
            map = importService.importsocialSecurityXml(filename, filePath, target, keymapList, isRepeat, wg11Index);
        } else {
            map = importService.importDate(filename, filePath, target, keymapList, isRepeat, isEntityStorage, autoCreateArchivecode);
        }
        map.put("erroMessage", erroMessage);
        return map;
    }

    /**库房导入
     * @param fields          字段
     * @param filePath        文件上传预览后转存全路径
     * @param filename        文件全路径
     * @param target          目标节点id
     * @return
     */
    @RequestMapping("/importKf")
    @ResponseBody
    public Map<String, Object> importStorageData(String fields, String filePath,
                                          String filename, String target,
                                          String isRepeat,
                                          boolean taitanXml, boolean socialSecurityXml, String wg11Index) throws Exception {
        JSONArray jsonArray = JSONArray.parseArray(fields);
        Map<String, Object> map = new HashMap<>();
        //读取字段配置
        List<String> keymapList = new ArrayList<String>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = JSONObject.parseObject(jsonArray.get(i).toString());
            String targetName="";
            if (obj.get("target") != null && !"".equals(obj.get("target"))) {
                targetName="," + obj.get("target");
            }
            int count = i;
            count = keymapList.size();
            keymapList.add(count + "," + obj.get("source") +targetName);
        }
        String erroMessage = "";
        if (taitanXml) {//bs 的xml
            erroMessage="请选择excel数据表";
        } else if (socialSecurityXml) { //社保中心的xml
            erroMessage="请选择excel数据表";
        } else {
            map = importService.importKfDate(filename, filePath, target, keymapList, isRepeat);
        }
        map.put("erroMessage", erroMessage);
        return map;
    }

    /**
     * 入库匹配校验
     * 情形1  没找到相关条目
     * 情形2  匹配到多条条目
     * 情形3  存储位置不够详细
     * 情形4  放入密集架空间不足
     * 情形5  已入库
     * 情形6  存储位置没有匹配到
     * 情形7  存储位置信息为空
     * 情形8  可以进行入库
     * @param codeSetValues
     * @return
     */
    @RequestMapping("/kfCheck")
    @ResponseBody
    public ExtMsg kfCheck(String codeSetValues) {
        String uniquetag = BatchModifyService.getUniquetagByType("kfdr");//库房导入
        ExtMsg newMsg= importService.kfCheck(codeSetValues,uniquetag);
        //生成异常信息，以供稍后下载
        List<Tb_entry_index_temp> indexTempList = entryIndexTempRepository.getUniquetagAndSparefield5Less(uniquetag);//库房导入临时表数据 可入库
        List<Tb_entry_index_temp> copyList=new ArrayList<>();
        copyList.addAll(indexTempList);
        try{
            //获取字段信息
            List<Tb_data_template> templateList=templateController.changeGrid("publicNode",null,null);
            String[] fieldCodeArr=new String[templateList.size()];
            String[] fieldNameArr=new String[templateList.size()];
            for(int i=0;i<templateList.size();i++){
                fieldCodeArr[i]=templateList.get(i).getFieldcode();
                fieldNameArr[i]=templateList.get(i).getFieldname();
            }
            ZipUtils.del(rooPath + File.separator + "OAFile" + File.separator + "导入失败");//先删除旧信息
            CreateExcel.createTempErroExcel("库房导入失败-" +DateUtil.getCurrentTimeStr(), copyList, fieldCodeArr, fieldNameArr);
        }catch(Exception e){
            e.printStackTrace();
        }
        //设置匹配结果返回
        return importService.setReturnMsg(newMsg,indexTempList,uniquetag);
    }

    /**
     * 执行入库
     * @return
     */
    @RequestMapping("/importCheck")
    @ResponseBody
    public ExtMsg importCheck() {
        return importService.importCheck();
    }

    /**
     * @param fields   字段
     * @param filePath 文件上传预览后转存全路径
     * @param filename 文件全路径
     * @param target   目标节点id
     * @return
     */
    @RequestMapping("/CaptureImport")
    @ResponseBody
    public Map<String, Object> importCaptureData(String fields, String filePath, String filename, String target, String isRepeat, String importtype) throws Exception {
        JSONArray jsonArray = JSONArray.parseArray(fields);
        Map<String, Object> map = new HashMap<>();
        //读取字段配置
        List<String> keymapList = new ArrayList<String>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = JSONObject.parseObject(jsonArray.get(i).toString());
            if (obj.get("target") != null && !"".equals(obj.get("target"))) {
                keymapList.add(i + "," + obj.get("source") + "," + obj.get("target"));
            }
        }
        String erroMessage = "";
        try {
            map = importService.importCaptureData(filename, filePath, target, keymapList, isRepeat, importtype);
        } catch (Exception e) {
            erroMessage = importService.getErroString(e, keymapList, target);
        }
        map.put("erroMessage", erroMessage);
        return map;
    }


    @RequestMapping("/deletUploadFile")
    @ResponseBody
    public void deletUploadFile(String filePath) {
        String[] s = filePath.split("/");
        //删除上传预览转存的文件
        String str = rooPath + File.separator + "OAFile" + File.separator + "upload";
        String zipStr = rooPath + File.separator + "OAFile" + File.separator + "zip解压目录";
        File[] unZipFiles = new File(zipStr).listFiles();
        File[] files = new File(str).listFiles();
        if (unZipFiles != null) {
            for (File file : unZipFiles) {
                //判断是否是目录，存在则删除
                if (filePath != "" && file.getName().equals(s[s.length - 2])) {
                    if (file.isDirectory()) {
                        FileUtil.delFolder(zipStr + File.separator + file.getName());
                    }
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
        if (files != null) {
            for (File file : files) {
                //判断是否是目录，存在则删除
                if (filePath != "" && file.getName().equals(s[s.length - 2])) {
                    if (file.isDirectory()) {
                        FileUtil.delFolder(str + File.separator + file.getName());
                    }
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
    }


    public static void main(String[] a) {
        String str = "E:\\2a\\dsa.txt";
        String f = new File(str).getName();
        String lastNmae = f.substring(0, f.lastIndexOf("."));
        System.out.println(lastNmae);
    }


    @RequestMapping("/getOgranid")
    @ResponseBody
    public ExtMsg getOgranid(String nodeid) {
        boolean flag = importService.getOgranid(nodeid);
        if (flag) {
            return new ExtMsg(true, "", flag);
        } else {
            return new ExtMsg(false, "", flag);
        }
    }

    /**
     * 档案接收-导入
     *
     * @param filePath 文件上传预览后转存全路径
     * @param filename 文件全路径
     * @param target   目标节点id
     * @param batchid  批次id
     * @return
     */
    @RequestMapping("/importCalloutEntry")
    @ResponseBody
    public Map<String, Object> importCalloutEntryData(String filePath, String filename, String target, String batchid) throws Exception {
        Integer copies = szhArchivesCalloutRepository.findByBatchcode(batchid).getAjcopies();
        Map<String, Object> map = new HashMap<>();
        String exceptiomMsg = "";
        try {
            map = importService.importCalloutEntryData(copies, filename, filePath, target, batchid);
        } catch (Exception e) {
            exceptiomMsg = getErroStringCalloutEntry(e, target);
            map.put("erroMessage", exceptiomMsg);
        }
        return map;
    }

    public String getErroStringCalloutEntry(Exception e, String target) {
        String errStr = e.getCause() == null ? null : e.getCause().getCause().getMessage();
        String returnMessage = "";
        String[] strcode = new String[3];
        String[] strname = new String[3];
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                strcode[i] = "archivecode";
                strname[i] = "档号";
            } else if (i == 1) {
                strcode[i] = "tracktext";
                strname[i] = "字轨";
            } else if (i == 2) {
                strcode[i] = "tracknumber";
                strname[i] = "案号";
            }
        }
        if (errStr != null && errStr.indexOf("Out Of Memory Error") != -1) {
            returnMessage = "数据量过大，出现内存溢出异常";
        }
        if (errStr != null) {
            returnMessage = errStr;
        }
        if (errStr != null && errStr.indexOf("Data too long for column") != -1) {
            String subFieldCode = errStr.substring(errStr.trim().indexOf("'") + 1, errStr.trim().lastIndexOf("'"));
            String name = "";
            for (int i = 0; i < strcode.length; i++) {
                if (strcode[i].equals(subFieldCode)) {
                    name = strname[i];
                }
            }
            returnMessage = "字段值过长,字段名:[" + name + "-" + subFieldCode + "]";
        }
        if (errStr != null && errStr.indexOf("Column  not found") != -1) {
            String subFieldCode = errStr.substring(errStr.trim().indexOf("'") + 1, errStr.trim().lastIndexOf("'"));
            String name = "";
            for (int i = 0; i < strcode.length; i++) {
                if (strcode[i].equals(subFieldCode)) {
                    name = strname[i];
                }
            }
            returnMessage = "选中的列不存在,列名:[" + name + "-" + subFieldCode + "]";
        }
        return returnMessage;
    }
}