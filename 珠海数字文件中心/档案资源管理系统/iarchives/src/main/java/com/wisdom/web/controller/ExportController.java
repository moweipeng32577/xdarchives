package com.wisdom.web.controller;

import com.alibaba.fastjson.JSONArray;
import com.wisdom.util.ExportUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.service.*;
import com.xdtech.component.storeroom.entity.Storage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 数据导出控制器 Created by xd on 2017/10/17.
 */
@Controller
@RequestMapping(value = "/export")
public class ExportController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LogService logService;

    @Autowired
    ExportExcelService excelService;

    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    EntryIndexCaptureService entryIndexCaptureService;

    @Autowired
    ThematicService thematicService;

    @Autowired
    ThematicMakeService thematicMakeService;

    @Autowired
    CaptureMetadataService captureMetadataService;

    @Autowired
    ClassifySearchService classifySearchService;

    private static final String EXPORT_XML = "Xml";
    private static final String EXPORT_EXCEL = "Excel";
    private static final String EXPORT_XMLANDFIELD = "XmlAndFile";
    private static final String EXPORT_EXCELANDFIELD = "ExcelAndFile";
    private static final int ENTRY_SIZE = 500000;
    private static final int ENTRY_FILE_SIZE = 100000;

    @RequestMapping("/main")
    public String main() {
        return "/inlet/export";
    }

    @RequestMapping("/extportData")
    @ResponseBody
    public void extportData(String fileName, String[] ids, HttpServletResponse response) {
        logger.info("fileName:" + fileName + "ids:" + ids);
        List<Tb_log_msg> logDetails = new ArrayList<Tb_log_msg>();
        logDetails = logService.getLogDetailByIDIn(ids);
        String names[] = {"操作人", "用户名", "机构", "IP地址", "模块", "操作时间", "操作描述"};// 列名
        String keys[] = {"getOperate_user", "realname", "organ", "ip", "module", "start_time", "desci"};// map中的key
        List<Map<String, Object>> list = createExcelRecord(logDetails);
        ExportUtil exportUtil = new ExportUtil(fileName, response, list, keys, names);
        exportUtil.exportExcel();
    }

    @RequestMapping("/extportDataTemplate")
    @ResponseBody
    public void extportDataTemplate(String fileName, HttpServletResponse response) {
        logger.info("fileName:" + fileName);

        String names[] = {"操作人", "IP地址", "模块", "操作时间", "操作描述"};// 列名
        ExportUtil exportUtil = new ExportUtil(fileName, response, names);
        exportUtil.exportExcelTemplate();
    }

    private List<Map<String, Object>> createExcelRecord(List<Tb_log_msg> logDetails) {
        List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
        Tb_log_msg logDetail;
        for (int j = 0; j < logDetails.size(); j++) {
            logDetail = logDetails.get(j);
            Map<String, Object> mapValue = new HashMap<>();
            mapValue.put("getOperate_user", logDetail.getOperate_user());
            mapValue.put("realname", logDetail.getRealname());
            mapValue.put("organ", logDetail.getOrgan());
            mapValue.put("ip", logDetail.getIp());
            mapValue.put("module", logDetail.getModule());
            mapValue.put("start_time", logDetail.getStartTime());
            mapValue.put("desci", logDetail.getDesci());
            listmap.add(mapValue);
        }
        return listmap;
    }


    //--数据管理选择字段窗口里面的字段数据
    @RequestMapping("/managementGetFields")
    @ResponseBody
    public List<Tb_data_template> managementGetFields(HttpServletRequest request, HttpServletResponse response, String fieldNodeid) {
        List<Tb_data_template> list = new ArrayList<>();
        if (fieldNodeid != null) {
            list = excelService.getDatatemplate(fieldNodeid);
            System.out.println(list);
        }
        return list;
    }

    //数据采集或目录接收-导出
    @RequestMapping("/capturechooseFieldExport")
    @ResponseBody
    public synchronized ExtMsg capturechooseFieldExport(HttpServletRequest request, HttpServletResponse response,
             String fileName, String zipPassword, String exportState, String userFieldCode, String entryids,
               String isSelectAll, String nodeid, String condition, String operator, String content,
                 Tb_entry_index_capture formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata,
                   String logic, boolean ifSearchLeafNode, boolean ifContainSelfNode,String exporttype) {
        String[] idarr={};
        if(entryids!=null) {
            idarr = entryids.split(",");
        }
        String[] userfilecode = {};
        String filePath = "";
        String FileSizeMsg = "OK";
        String entrySizeMsg = "OK";
        if (userFieldCode != null && !"".equals(userFieldCode)) {
            userfilecode = userFieldCode.split(",");
        }
        if ("true".equals(isSelectAll)) {
            List<String> entryList = entryIndexService.getIndexCaptureIds(nodeid, condition, operator, content,exporttype);
            idarr = entryList.toArray(new String[entryList.size()]);
        }
        //采集业务元数据
        try{
            captureMetadataService.captureServiceMetadataByZL(idarr, "数据采集", "导出");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        if (EXPORT_XML.equals(exportState)) {
            if (idarr.length > ENTRY_SIZE) {
                entrySizeMsg = "NO";
            }
            if ("OK".equals(entrySizeMsg)) {
                filePath = excelService.captureCreatexml(idarr, nodeid, fileName.trim(), response, userfilecode, zipPassword,exporttype);
            }
        } else if (EXPORT_EXCEL.equals(exportState)) {
            if (idarr.length > ENTRY_SIZE) {
                entrySizeMsg = "NO";
            }
            if ("OK".equals(entrySizeMsg)) {
                filePath = excelService.captureCreateExcel(idarr, nodeid, fileName.trim(), response, userfilecode, zipPassword,exporttype);
            }
        } else if (EXPORT_XMLANDFIELD.equals(exportState)) {
            if (idarr.length > ENTRY_FILE_SIZE) {
                entrySizeMsg = "NO";
            }
            FileSizeMsg = excelService.getFileSize(idarr);
            if ("OK".equals(entrySizeMsg) && "OK".equals(FileSizeMsg)) {
                filePath = excelService.captureCreateXmlAndElectronic(idarr, nodeid, fileName.trim(), response, userfilecode, zipPassword);
            }
        } else if (EXPORT_EXCELANDFIELD.equals(exportState)) {
            if (idarr.length > ENTRY_FILE_SIZE) {
                entrySizeMsg = "NO";
            }
            FileSizeMsg = excelService.getFileSize(idarr);
            if ("OK".equals(entrySizeMsg) && "OK".equals(FileSizeMsg)) {
                filePath = excelService.captureCreateExcleAndElectronic(idarr, nodeid, fileName.trim(), response, userfilecode, zipPassword);
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put("filePath", filePath);
        map.put("msg", FileSizeMsg);
        map.put("entrySizeMsg", entrySizeMsg);
        ExtMsg extMsg = new ExtMsg(true, "message", map);
        return extMsg;
    }

    //预约管理的导出
    @RequestMapping("/reservationChooseFieldExport")
    @ResponseBody
    public synchronized ExtMsg reservationChooseFieldExport(HttpServletResponse response,String userFieldName,
                                                 String fileName, String zipPassword, String exportState, String userFieldCode, String entryids,
                                                 String isSelectAll, String nodeid, String condition, String operator, String content) {
        String[] idarr={};
        if(entryids!=null) {
            idarr = entryids.split(",");
        }
        String[] userfilecode = {},userFielName = {};
        String filePath = "";
        String FileSizeMsg = "OK";
        String entrySizeMsg = "OK";
        if (userFieldCode != null && !"".equals(userFieldCode)) {
            userfilecode = userFieldCode.split(",");
        }
        if (userFieldName != null && !"".equals(userFieldName)) {
            userFielName = userFieldName.split(",");
        }
        if ("true".equals(isSelectAll)) {
            List<String> entryList = entryIndexService.getIndexIds(nodeid, condition, operator, content, null,null,null);
            idarr = entryList.toArray(new String[entryList.size()]);
        }
        if (idarr.length > ENTRY_SIZE) {
            entrySizeMsg = "NO";
        }
        if ("OK".equals(entrySizeMsg)) {
            filePath = excelService.reservationCreateExcel(idarr, nodeid, fileName.trim(), response, userfilecode,userFielName, zipPassword);
        }
        Map<String, String> map = new HashMap<>();
        map.put("filePath", filePath);
        map.put("fileSizeMsg", FileSizeMsg);
        map.put("entrySizeMsg", entrySizeMsg);
        ExtMsg extMsg = new ExtMsg(true, "message", map);
        return extMsg;
    }

    //设备管理导出
    @RequestMapping("/equipmentChooseFieldExport")
    @ResponseBody
    public synchronized ExtMsg equipmentChooseFieldExport(HttpServletResponse response,String userFieldName,
                                                            String fileName, String zipPassword, String exportState, String userFieldCode, String equipmentID,
                                                            String nodeid, String condition, String operator, String content) {
        String[] idarr={};
        if(equipmentID!=null) {
            idarr = equipmentID.split(",");
        }
        String[] userfilecode = {},userFielName = {};
        String filePath = "";
        String FileSizeMsg = "OK";
        String entrySizeMsg = "OK";
        if (userFieldCode != null && !"".equals(userFieldCode)) {
            userfilecode = userFieldCode.split(",");
        }
        if (userFieldName != null && !"".equals(userFieldName)) {
            userFielName = userFieldName.split(",");
        }
        if (idarr.length > ENTRY_SIZE) {
            entrySizeMsg = "NO";
        }
        if ("OK".equals(entrySizeMsg)) {
            filePath = excelService.equipmentCreateExcel(idarr, nodeid, fileName.trim(), response, userfilecode,userFielName, zipPassword);
        }
        Map<String, String> map = new HashMap<>();
        map.put("filePath", filePath);
        map.put("fileSizeMsg", FileSizeMsg);
        map.put("entrySizeMsg", entrySizeMsg);
        ExtMsg extMsg = new ExtMsg(true, "message", map);
        return extMsg;
    }

    //1.获取用户选择的字段
    //2.根据选择的节点获取到fieldcode fieldname
    //3.循环if 比对用户选择的fieldname 获取到fieldcode
    //4.根据entryids 查找出完整的档案对象
    //5.调用反射方法 获取到用户选择的字段-->fieldcode-->get/set方法--对象属性值
    //导出
    @RequestMapping("/chooseFieldExport")
    @ResponseBody
    public synchronized ExtMsg chooseFieldExport(HttpServletRequest request, HttpServletResponse response,
                                                 String fileName, String zipPassword, String exportState, String userFieldCode, String entryids,
                                                 String isSelectAll, String nodeid, String condition, String operator, String content,
                                                 Tb_index_detail formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata,
                                                 String logic, boolean ifSearchLeafNode, boolean ifContainSelfNode) {
        String[] idarr={};
        if(!StringUtils.isEmpty(entryids)) {
            idarr = entryids.split(",");
        }
        String[] userfilecode = {};
        String filePath = "";
        String FileSizeMsg = "OK";
        String entrySizeMsg = "OK";
        if (!StringUtils.isEmpty(userFieldCode)) {
            userfilecode = userFieldCode.split(",");
        }
        if ("true".equals(isSelectAll)) {
            List<String> entryList = entryIndexService.getIndexIds(nodeid, condition, operator, content, logic, formConditions, formOperators);
            idarr = entryList.toArray(new String[entryList.size()]);
        }
        //采集业务元数据
        try{
            captureMetadataService.captureServiceMetadataByZL(idarr, "数据管理", "导出");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        if (EXPORT_XML.equals(exportState)) {
            if (idarr.length > ENTRY_SIZE) {
                entrySizeMsg = "NO";
            }
            if ("OK".equals(entrySizeMsg)) {
                filePath = excelService.createxml(idarr, nodeid, fileName.trim(), response, userfilecode, zipPassword);
            }
        } else if (EXPORT_EXCEL.equals(exportState)) {
            if (idarr.length > ENTRY_SIZE) {
                entrySizeMsg = "NO";
            }
            if ("OK".equals(entrySizeMsg)) {
                filePath = excelService.createExcel(idarr, nodeid, fileName.trim(), response, userfilecode, zipPassword);
            }
        } else if (EXPORT_XMLANDFIELD.equals(exportState)) {
            if (idarr.length > ENTRY_FILE_SIZE) {
                entrySizeMsg = "NO";
            }
            FileSizeMsg = excelService.getFileSize(idarr);
            if ("OK".equals(entrySizeMsg) && "OK".equals(FileSizeMsg)) {
                filePath = excelService.createXmlAndElectronic(idarr, nodeid, fileName.trim(), response, userfilecode, zipPassword);
            }
        } else if (EXPORT_EXCELANDFIELD.equals(exportState)) {
            if (idarr.length > ENTRY_FILE_SIZE) {
                entrySizeMsg = "NO";
            }
            FileSizeMsg = excelService.getFileSize(idarr);
            if ("OK".equals(entrySizeMsg) && "OK".equals(FileSizeMsg)) {
                filePath = excelService.createExcleAndElectronic(idarr, nodeid, fileName.trim(), response, userfilecode, zipPassword);
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put("filePath", filePath);
        map.put("fileSizeMsg", FileSizeMsg);
        map.put("entrySizeMsg", entrySizeMsg);
        ExtMsg extMsg = new ExtMsg(true, "message", map);
        return extMsg;
    }

    @RequestMapping("/downloadZipFile")
    @ResponseBody
    public void downloadZipFile(HttpServletResponse response, HttpServletRequest request, String fpath) throws Exception {
        File file = new File(fpath);
        String str = file.getName();
        String fileName = str.substring(0, str.indexOf("."));
        excelService.wirteFile(response, fpath, fileName, "ok", true);
        //ZipUtils.del(fpath);
        new File(fpath).delete();
    }

    @RequestMapping("/downloadFieldTemp")
    @ResponseBody
    public void downloadFieldTemp(String nodeid, boolean isEntryStorage,HttpServletRequest request, HttpServletResponse response) {
        excelService.exportFieldTemp(response,request, nodeid,isEntryStorage);
    }

    //导出漏页信息
    @RequestMapping("/missPageFieldExport")
    @ResponseBody
    public synchronized ExtMsg missPageFieldExport(HttpServletRequest request, HttpServletResponse response,
                                                   String fileName, String zipPassword,String[] ids) {
        String filePath = "";
        String entrySizeMsg = "OK";
        if (ids.length > ENTRY_SIZE) {
            entrySizeMsg = "NO";
        }
        if ("OK".equals(entrySizeMsg)) {
            filePath = excelService.createMissPageExcel(ids,fileName.trim(), response,zipPassword);
        }
        Map<String, String> map = new HashMap<>();
        map.put("filePath", filePath);
        map.put("entrySizeMsg", entrySizeMsg);
        ExtMsg extMsg = new ExtMsg(true, "message", map);
        return extMsg;
    }

    //导出漏页信息数据管理
    @RequestMapping("/missPageFieldExportManagement")
    @ResponseBody
    public synchronized ExtMsg missPageFieldExportManagement(HttpServletRequest request, HttpServletResponse response,
                                                   String fileName, String zipPassword,String[] ids) {
        String filePath = "";
        String entrySizeMsg = "OK";
        if (ids.length > ENTRY_SIZE) {
            entrySizeMsg = "NO";
        }
        if ("OK".equals(entrySizeMsg)) {
            filePath = excelService.createMissPageExcelManagement(ids,fileName.trim(), response,zipPassword);
        }
        Map<String, String> map = new HashMap<>();
        map.put("filePath", filePath);
        map.put("entrySizeMsg", entrySizeMsg);
        ExtMsg extMsg = new ExtMsg(true, "message", map);
        return extMsg;
    }

    /**
     * 编研管理系统的发布数字资源
     * @param thematicIds
     * @return
     */
    @RequestMapping("/exportReleaseResurce")
    public ExtMsg exportReleaseResurce(String[] thematicIds,HttpServletResponse response){
        List<String[]> subAry = new InformService().subArray(thematicIds, 1000);
        List<Tb_thematic> thematics = new ArrayList<>();
        for (String[] arr : subAry) {
           thematics.addAll(thematicService.getThematic(arr));
        }
        String fileName = "专题制作";
        String names[] = { "专题名称", "专题描述", "专题类型", "发布状态"};// 列名
        String keys[] = { "title", "thematiccontent", "thematictypes", "publishstate" };// map中的key
        List<Map<String, Object>> list = this.objectToMap(thematics,keys);
        ExportUtil exportUtil = new ExportUtil(fileName, response, list, keys, names);
        exportUtil.exportExcel();
        return null;
    }

    /**
     * 编研管理的发布数字资源
     * @param thematicIds
     * @return
     */
    @RequestMapping("/exportReleaseThematicResurce")
    public ExtMsg exportReleaseThematicResurce(String[] thematicIds,HttpServletResponse response){
        List<String[]> subAry = new InformService().subArray(thematicIds, 1000);
        List<Tb_thematic_make> thematics = new ArrayList<>();
        for (String[] arr : subAry) {
            thematics.addAll(thematicMakeService.getThematic(arr));
        }
        String fileName = "专题制作";
        String names[] = { "专题名称", "专题描述", "发布状态"};// 列名
        String keys[] = { "title", "thematiccontent", "publishstate" };// map中的key
        List<Map<String, Object>> list = this.objectToMap(thematics,keys);
        ExportUtil exportUtil = new ExportUtil(fileName, response, list, keys, names);
        exportUtil.exportExcel();
        return null;
    }

    /**
     * 把对象转换成map
     * keys:要转换的属性
     * T要转换的对象
     *
     */
    private <T> List<Map<String, Object>> objectToMap(List<T> list,String[] keys)  {
        List<Map<String, Object>> result = new ArrayList<>();
        for (T t : list) {
            Map<String,Object> map = new HashMap<>();
            Class<?> clazz = t.getClass();
            for (String key : keys) {
                try {
                    String getFun = "get"+new StringBuilder().append(Character.toUpperCase(key.charAt(0))).append(key.substring(1)).toString();
                    Method method = clazz.getMethod(getFun);
                    map.put(key,method.invoke(t));
                }catch (Exception e){//当找不到属性就跳过当前
                    continue;
                }
            }
            result.add(map);
        }
        return result;
    }

    //库房管理入库出库记录导出
    @RequestMapping("/exportInware")
    @ResponseBody
    public ExtMsg exportInware(HttpServletRequest request, HttpServletResponse response,
                                                             String fileName, String zipPassword,String[] ids,String nodeid) {
        String filePath = "";
        String entrySizeMsg = "OK";
        if (ids.length > ENTRY_SIZE) {
            entrySizeMsg = "NO";
        }
        if ("OK".equals(entrySizeMsg)) {
            filePath = excelService.exportInware(ids,fileName.trim(), response,zipPassword);
        }
        Map<String, String> map = new HashMap<>();
        map.put("filePath", filePath);
        map.put("entrySizeMsg", entrySizeMsg);
        ExtMsg extMsg = new ExtMsg(true, "message", map);
        return extMsg;
    }

    //导出模板
    @RequestMapping("/exportColumnNames")
    @ResponseBody
    public void exportColumnNames(HttpServletRequest request, HttpServletResponse response, String[] columnNames) {
        excelService.exportColumnNames(request, response, columnNames);
    }

    //导出评价
    @RequestMapping("/exportAppraise")
    @ResponseBody
    public synchronized ExtMsg exportAppraise(String fileName, String zipPassword,String[] feedbackids) {
        String filePath = excelService.exportAppraise(feedbackids,fileName, zipPassword);
        ExtMsg extMsg = new ExtMsg(true, "message", filePath);
        return extMsg;
    }

    //导出用户
    @RequestMapping("/expUse")
    @ResponseBody
    public void expUse(String[] userid,HttpServletResponse response,HttpServletRequest request){
        excelService.expUse(userid,request,response);
    }

    //导出长期保管包
    @RequestMapping("/createLongRetention")
    @ResponseBody
    public void createLongRetention(String entryid, HttpServletResponse response) {
        excelService.createLongRetention(entryid, response);
    }

    //导出全文检索
    @RequestMapping("/originalExport")
    @ResponseBody
    public synchronized ExtMsg originalExport(HttpServletResponse response,String userFieldName,
                                                          String fileName, String zipPassword, String userFieldCode, String ids,
                                                          String nodeid) {
        String[] idarr={};
        if(ids!=null) {
            idarr = ids.split(",");
        }
        String[] userfilecode = {},userFielName = {};
        String filePath = "";
        String FileSizeMsg = "OK";
        String entrySizeMsg = "OK";
        if (userFieldCode != null && !"".equals(userFieldCode)) {
            userfilecode = userFieldCode.split(",");
        }
        if (userFieldName != null && !"".equals(userFieldName)) {
            userFielName = userFieldName.split(",");
        }
        if (idarr.length > ENTRY_SIZE) {
            entrySizeMsg = "NO";
        }
        if ("OK".equals(entrySizeMsg)) {
            filePath = excelService.originalCreateExcel(idarr, nodeid, fileName.trim(), response, userfilecode,userFielName, zipPassword);
        }
        Map<String, String> map = new HashMap<>();
        map.put("filePath", filePath);
        map.put("fileSizeMsg", FileSizeMsg);
        map.put("entrySizeMsg", entrySizeMsg);
        ExtMsg extMsg = new ExtMsg(true, "message", map);
        return extMsg;
    }
}
