package com.wisdom.service.webservice.serviceImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wisdom.service.webservice.service.ReceiveWebService;
import com.wisdom.web.entity.OaEntry;
import com.wisdom.web.entity.Tb_entry_detail;
import com.wisdom.web.entity.Tb_entry_detail_capture;
import com.wisdom.web.repository.EntryDetailCaptureRepository;
import com.wisdom.web.repository.EntryDetailRepository;
import com.wisdom.web.service.EntryIndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 接收数据服务实现类
 * Created by tanly on 2018/11/7 0019.
 */
public class ReceiveWebServiceImpl implements ReceiveWebService {
    private static Logger LOGGER = LoggerFactory.getLogger(ReceiveWebServiceImpl.class);

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录

    @Value("${webservice.filearchive.module}")
    private String module;//接收OA数据的功能模块

    @Value("${webservice.filearchive.opened}")
    private String opened;//接收OA数据的开关

    @Autowired
    EntryDetailRepository entryDetailRepository;

    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    EntryDetailCaptureRepository entryDetailCaptureRepository;

    /**
     * 接收数据（dataJson、fileJson），还原附件、存入条目与文件表，返回结果（JSON）
     *
     * @param dataJson
     * @param fileJson
     * @return
     */
    @Override
    public String receiveData(String dataJson, String fileJson) {
        if (!"true".equals(opened)) {
            return reJson("接收程序未开启！请联系我方管理员", "0");
        }
        String failTip = "--失败--：";

        OaEntry oaEntry;
        try {
            oaEntry = JSONObject.parseObject(dataJson, OaEntry.class);
        } catch (Exception e) {
            LogOAReceive(failTip + "转换条目JSON （异常信息：" + e.getMessage() + "）", true, true);
            return reJson("转化条目JSON失败！", "0");
        }
        JSONArray fileAry;
        try {
            fileAry = JSONArray.parseArray(fileJson);
        } catch (Exception e) {
            LogOAReceive(failTip + "转换文件JSON （异常信息：" + e.getMessage() + "）", true, true);
            return reJson("转化文件JSON失败！", "0");
        }
        try {
            if(module.equals("capture")){
                Tb_entry_detail_capture edc = entryDetailCaptureRepository.findByF01(oaEntry.getArchives_no());
                if (edc != null) {
                    LogOAReceive(failTip + "ARCHIVE_NO重复：" + oaEntry.getArchives_no(), true, true);
                    return reJson("数据接收重复！", "0");
                }
            }else{
                Tb_entry_detail ed = entryDetailRepository.findByF01(oaEntry.getArchives_no());
                if (ed != null) {
                    LogOAReceive(failTip + "ARCHIVE_NO重复：" + oaEntry.getArchives_no(), true, true);
                    return reJson("数据接收重复！", "0");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogOAReceive(failTip + "验证重复性 （ARCHIVE_NO：" + oaEntry.getArchives_no() + "）", true, true);
            return reJson("验证接收数据重复性失败", "0");
        }
        try {
            String entryId;
            if(module.equals("capture")){
                entryId = entryIndexService.receiveCaptureImpl(oaEntry, fileAry);//插入记录及复制文件
            }else{
                entryId = entryIndexService.receiveImpl(oaEntry, fileAry);//插入记录及复制文件
            }
            LogOAReceive("成功：接收完整数据 （ARCHIVES_NO：" + oaEntry.getArchives_no() + "，题名:" + oaEntry.getTitle() + " ，ENTRY_INDEX" + (module.equals("capture") ? "_CAPTURE" : "") + "表ID:" + entryId + "）", true, true);
            return reJson("保存数据成功！", "1");
        } catch (Exception e) {
            try {
                File file = new File(rootpath + EntryIndexService.dir);
                for (File f : file.listFiles()) {
                    f.delete();
                }
                file.delete();//删除生成的附件

                String sb = "NO ADDRESS";
                for (StackTraceElement ste : e.getStackTrace()) {
                    if (ste.getClassName().contains("com.wisdom.service.webservice.serviceImpl")) {
                        sb = "类名：" + ste.getClassName() + "\t行号:" + ste.getLineNumber();//第一个就好
                        break;
                    }
                }

                final Writer result = new StringWriter();
                e.fillInStackTrace().printStackTrace(new PrintWriter(result));
                LogOAReceive(failTip + "保存数据 （ARCHIVES_NO：" + oaEntry.getArchives_no() + "，题名:" + oaEntry.getTitle() + "）", true, false);
                LogOAReceive("异常位置：" + sb + "\n异常信息：" + e.getMessage() + "\n详细信息：\n" + result.toString(), false, true);
            } catch (Exception fe) {
                fe.printStackTrace();
            }
            return reJson("保存数据失败！", "0");
        }
    }

    public String reJson(String tip, String ret) {
        return "{\"message\":\"" + tip + "\",\"ret\":" + ret + "}";
    }

    /**
     * 打印日志信息
     *
     * @param info       打印信息
     * @param printTime  是否打印时间
     * @param printSplit 是否分割线
     */
    private void LogOAReceive(String info, boolean printTime, boolean printSplit) {
        File dir = new File(rootpath + "/ReceiveOAFile/ReceiveLog");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String curMonth = new SimpleDateFormat("yyyyMM").format(new Date());
        File file = new File(rootpath + "/ReceiveOAFile/ReceiveLog/OAReceive_" + curMonth + ".log");//每月一个日志文件
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(file, true), "GBK");
            String time = "";
            if (printTime) {
                time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "：";
            }
            writer.write(time + info + "\n");
            if (printSplit) {
                writer.write("--------------------------------------------------------------------------------------------------------------\n");
            }
            writer.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}