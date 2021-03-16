package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.secondaryDataSource.repository.SxLogMsgRepository;
import com.wisdom.util.FunctionUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.ElectronicCaptureRepository;
import com.wisdom.web.repository.ElectronicRepository;
import com.wisdom.web.repository.ElectronicSolidRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.LogService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 电子库房管理控制器
 * Created wangmh on 2020/7/23.
 */
@Controller
@RequestMapping(value = "/elecapacity")
public class ElecapacityController {

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录

    @Autowired
    ElectronicRepository electronicRepository;

    @Autowired
    ElectronicCaptureRepository electronicCaptureRepository;

    @Autowired
    ElectronicSolidRepository electronicSolidRepository;

    @RequestMapping("/main")
    public String index(Model model, String isp) {
		return "/inlet/elecapacity/elecapacity";
    }

    @RequestMapping("/totalCapacity")
    public String diskspace(Model model, String isp) {
        return "/inlet/elecapacity/totalcapacity";
    }

    @RequestMapping("/usetotalCapacity")
    public String usertotalCapacity(Model model, String isp) {
        return "/inlet/elecapacity/usetotalcapacity";
    }

    @RequestMapping("/eleusetotalCapacity")
    public String eleusetotalCapacity(Model model, String isp) {
        return "/inlet/elecapacity/eleusetotalcapacity";
    }

    @RequestMapping("/getcapacity")
    @ResponseBody
    public ExtMsg getcapacity() {
        Map capacityMap = new HashMap();
        String manageCapacity = electronicRepository.getCapacity();
        String captureCapacity = electronicCaptureRepository.getCapacity();
        String solidCapacity = electronicSolidRepository.getCapacity();

        String path = rootpath + File.separator+"longRetention";
        int folderCount = 0;
        File d = new File(path);
        File list[] = d.listFiles();
        for(int i = 0; i < list.length; i++){
            folderCount++;
        }
        long size = FileUtils.sizeOfDirectory(d);


        capacityMap.put("fzbCapacityNum",folderCount);
        capacityMap.put("fzbCapacitySize",getPrintSize(size));
        capacityMap.put("manangeCapacityNum",manageCapacity.split(",")[0]);
        capacityMap.put("manangeCapacitySize",getPrintSize(Integer.parseInt(manageCapacity.split(",")[1])));
        capacityMap.put("captureCapacityNum",captureCapacity.split(",")[0]);
        capacityMap.put("captureCapacitySize",getPrintSize(Integer.parseInt(captureCapacity.split(",")[1])));
        capacityMap.put("solidCapacityNum",solidCapacity.split(",")[0]);
        capacityMap.put("solidCapacitySize",getPrintSize(Integer.parseInt(solidCapacity.split(",")[1])));
        capacityMap.put("capacityNum",Integer.parseInt(manageCapacity.split(",")[0]) + Integer.parseInt(captureCapacity.split(",")[0]) + Integer.parseInt(solidCapacity.split(",")[0]));
        capacityMap.put("capacitySize",getPrintSize(Integer.parseInt(manageCapacity.split(",")[1]) + Integer.parseInt(captureCapacity.split(",")[1]) + Integer.parseInt(solidCapacity.split(",")[1])));

        return new ExtMsg(true,"",capacityMap);

    }


    @RequestMapping("/getcapacityZX")
    @ResponseBody
    public ExtMsg getcapacityZX() {
        Map capacityMap = new HashMap();
        String[] date= new String[5];
        String[] num= new String[5];
        String sizeType = new String();
        for(int i=0;i<date.length;i++){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -i);
            Date time = c.getTime();
            String preDay = sdf.format(time);
            String manageSum = electronicRepository.getTotalCapacity(preDay);
            String capacitySum = electronicCaptureRepository.getTotalCapacity(preDay);
            String solidSum = electronicSolidRepository.getTotalCapacity(preDay);

            date[i] = preDay;
            String sizeAndType =  getPrintSize(Integer.parseInt(manageSum) + Integer.parseInt(capacitySum) +Integer.parseInt(solidSum));
            int index = sizeAndType.indexOf("-");
            sizeType = sizeAndType.substring(index+1);
            String size = sizeAndType.substring(0,index);
            num[i] =size;
        }

        capacityMap.put("sizeType",sizeType);
        capacityMap.put("date",date);
        capacityMap.put("num",num);

        return new ExtMsg(true,"",capacityMap);
    }

    @RequestMapping("/getsumZX")
    @ResponseBody
    public ExtMsg getsumZX() {
        Map capacityMap = new HashMap();
        String[] date= new String[5];
        String[] num= new String[5];
        for(int i=0;i<date.length;i++){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -i);
            Date time = c.getTime();
            String preDay = sdf.format(time);
            String manageSum = electronicRepository.getSumCapacity(preDay);
            String capacitySum = electronicCaptureRepository.getSumCapacity(preDay);
            String solidSum = electronicSolidRepository.getSumCapacity(preDay);
            date[i] = preDay;
            num[i] = String.valueOf(Integer.parseInt(manageSum) + Integer.parseInt(capacitySum) +Integer.parseInt(solidSum));
        }

        capacityMap.put("date",date);
        capacityMap.put("num",num);

        return new ExtMsg(true,"",capacityMap);
    }

    @RequestMapping("/getlist")
    @ResponseBody
    public Page<Elecapacity> getlist(int page,int limit) {
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        List<Elecapacity> lists = electronicRepository.getList();
        return  new PageImpl(lists, pageRequest, lists.size());
    }



    public static String getPrintSize(long size) {
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return String.valueOf(size) + "-B";
        } else {
            size = size / 1024;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + "-KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "-MB";
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "-GB";
        }
    }

}