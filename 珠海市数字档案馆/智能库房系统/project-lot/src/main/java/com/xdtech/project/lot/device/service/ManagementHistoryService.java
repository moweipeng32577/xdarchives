package com.xdtech.project.lot.device.service;

import com.alibaba.fastjson.JSONObject;
import com.xdtech.project.lot.device.entity.DeviceHistory;
import com.xdtech.project.lot.device.entity.DeviceInformation;
import com.xdtech.project.lot.device.repository.DeviceHistoryRepository;
import com.xdtech.project.lot.util.expUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class ManagementHistoryService {

    @Autowired
    DeviceHistoryRepository deviceHistoryRepository;

    public void expHistory(String[] ids, HttpServletResponse response, HttpServletRequest request){
        //前置参数
        String[] fieldname = {"采集时间","数据描述","设备类型"};
        String[] fieldcod = {"captureTime","captureValue","type"};
        //1.创建excel文件---用在保存循环的数据--循环写入
        //2.创建工作簿  SXSSFWorkbook 支持最大行1048576
        SXSSFWorkbook workbook = null;
        OutputStream os = null;
        InputStream inputStream = null;
        OutputStream out = null;
        try {
            workbook = new SXSSFWorkbook(10);
            Sheet sheet = workbook.createSheet();
            if (null != ids) {
                //1.根据id 查出需要导出的设备信息
                List<String[]> list = expUtil.splitAry(ids, 900);
                for (String[] arr : list) {
                    List<DeviceHistory> users = deviceHistoryRepository.findByIdIn(arr);
                    //每900条写入1次
                    sheet = expUtil.SXSSFWorkbookCreateUseExcle(sheet, users, fieldcod, fieldname);
                }
                String dir = "C:";
                String path = dir + File.separator + "OAFile" + File.separator + "导出设备历史数据" + File.separator;//
                File f = new File(path);
                f.mkdirs();
                if (!f.exists()) {
                    throw new RuntimeException("expHistory()---创建文件夹失败");
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
                expUtil.delFolder(dir + File.separator + "OAFile" + File.separator + "导出设备历史数据");
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


    public List<Map<String,String>> getHistory( List<DeviceHistory> HTlist){
        List<Map<String,String>> returnlist = new ArrayList();
        for(DeviceHistory device : HTlist){
            JSONObject jsonObject = JSONObject.parseObject(device.getCaptureValue());
            String tem =  jsonObject.getString("tem");
            String hum =  jsonObject.getString("hum");
            String type = device.getType();
            String operateMan = jsonObject.getString("operateMan");
            String operateType = jsonObject.getString("operateType");
            String door = jsonObject.getString("door");
            Map map = new HashMap();
            map.put("time", device.getCaptureTime());
            if(null!=tem&&null!=hum) {
                map.put("capturevalue", "温度:"+tem+" 湿度:"+hum);
            }else {
                map.put("capturevalue",operateMan+operateType+door);
            }
            map.put("type",type);
            map.put("id",device.getId());
            returnlist.add(map);
        }
        return returnlist;
    }
}
