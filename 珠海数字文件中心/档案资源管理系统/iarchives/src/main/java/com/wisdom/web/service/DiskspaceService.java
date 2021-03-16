package com.wisdom.web.service;

import com.wisdom.web.entity.Diskspace;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by RonJiang on 2018/5/9 0009.
 */
@Service
@Transactional
public class DiskspaceService {

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录

    @Value("${system.diskspace.threshold}")
    private String threshold;//系统文件根目录

    public boolean diskspaceOverThresholdvalue(){
        String drivernumber = rootpath.substring(0,1);
        File[] roots = File.listRoots();// 获取磁盘分区列表
        for (File file : roots) {
            if (file.toString().startsWith(drivernumber)) {
                Double thresholdValue = Double.parseDouble(threshold);
                Double usedPercent = getDiskspaceDetail(file,drivernumber).getUsedpercent();
                return usedPercent>thresholdValue?true:false;
            }
        }
        return false;
    }

    public Diskspace getDiskspaceDetail(File file,String drivernumber){
        double totalSpace = file.getTotalSpace() / 1024 / 1024/ 1024;
        double freeSpace = file.getFreeSpace() / 1024 / 1024/ 1024;
        Long totalSpaceLong = (long)totalSpace;
        Double freePercent = Double.valueOf(new DecimalFormat("#.00").format(freeSpace/totalSpace*100));
        return new Diskspace(drivernumber,totalSpaceLong,freePercent,100-freePercent);
    }
}