package com.xdtech.project.lot.device.service;

import com.xdtech.project.lot.device.entity.DeviceInformation;
import com.xdtech.project.lot.device.repository.DeviceInformationRepository;
import com.xdtech.project.lot.util.expUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class DeviceInformationService {

    @Autowired
    DeviceInformationRepository deviceInformationRepository;

    public void expDeviceInformation(String[] inforid, HttpServletRequest request, HttpServletResponse response){
        //前置参数
        String[] fieldname = {"设备名","设备编号","制造商","安装日期","厂家联系方式","管理人员","保养周期"};
        String[] fieldcod = {"devicename","devicecode","manufacturers","installdate","pthone","adminuser","maintenance"};
        //1.创建excel文件---用在保存循环的数据--循环写入
        //2.创建工作簿  SXSSFWorkbook 支持最大行1048576
        SXSSFWorkbook workbook = null;
        OutputStream os = null;
        InputStream inputStream = null;
        OutputStream out = null;
        try {
            workbook = new SXSSFWorkbook(10);
            Sheet sheet = workbook.createSheet();
            if (null != inforid) {
                //1.根据id 查出需要导出的设备信息
                List<String[]> list = expUtil.splitAry(inforid, 900);
                for (String[] arr : list) {
                    List<DeviceInformation> users = deviceInformationRepository.findByInfroIdIn(arr);
                    //每900条写入1次
                    sheet = expUtil.SXSSFWorkbookCreateUseExcle(sheet, users, fieldcod, fieldname);
                }
                String dir = "C:";
                String path = dir + File.separator + "OAFile" + File.separator + "导出模板" + File.separator;//
                File f = new File(path);
                f.mkdirs();
                if (!f.exists()) {
                    throw new RuntimeException("expDeviceInformation()---创建文件夹失败");
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
                expUtil.delFolder(dir + File.separator + "OAFile" + File.separator + "导出模板");
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


    public boolean saveInformation(DeviceInformation deviceInformation){
        DeviceInformation information = null;
        if (null != deviceInformation) {
            information =  deviceInformationRepository.save(deviceInformation);
        }
        return information==null?false:true;
    }

    public int delInformation(String[] ids){
        int delCount = 0;
        if(null!=ids){
            List<String[]> list = expUtil.splitAry(ids, 900);
            for (String[] arr : list) {
                delCount = delCount + deviceInformationRepository.deleteByInforidIn(arr);
            }
        }
        return delCount;
    }
}
