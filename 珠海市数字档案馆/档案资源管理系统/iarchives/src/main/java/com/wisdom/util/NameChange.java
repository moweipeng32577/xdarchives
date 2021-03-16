package com.wisdom.util;

import com.wisdom.web.entity.Tb_electronic;
import com.wisdom.web.entity.Tb_electronic_capture;
import com.wisdom.web.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

/**
 * Created by tanly on 2018/4/18 0018.
 */
@Component
public class NameChange {

    public static NameChange namechange;


    @Autowired
    ElectronicRepository electronicRepository;

    @Autowired
    ElectronicCaptureRepository electronicCaptureRepository;

    @Autowired
    ElectronicSolidRepository electronicSolidRepository;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    EntryIndexCaptureRepository entryIndexCaptureRepository;


    @Value("${system.document.rootpath}")
    private String rootpath;// 系统文件根目录

    private final static Logger logger = LoggerFactory.getLogger(NameChange.class);

    @PostConstruct
    public void init() {
        namechange = this;
        namechange.electronicRepository = this.electronicRepository;
        namechange.electronicCaptureRepository = this.electronicCaptureRepository;
        namechange.entryIndexRepository = this.entryIndexRepository;
        namechange.entryIndexCaptureRepository = this.entryIndexCaptureRepository;
        namechange.rootpath = this.rootpath;
    }

    /**
     * 重命名
     *
     * @param entries
     */
    public static void changeFileName(String[] entries,String type) {
        if ("management".equals(type)){//数据管理
            for (String entryId : entries) {
                List<Tb_electronic> listEleids = namechange.electronicRepository.findByEntryid(entryId);//获取电子文件集合
                if (listEleids.size()>0){
                    String archivecode = namechange.entryIndexRepository.findArchivecodeByEntryid(entryId);//获取档号
                    for (int i = 0; i < listEleids.size(); i++) {
                        String filePath = listEleids.get(i).getFilepath();//获取文件路径
                        String fileName =listEleids.get(i).getFilename();//获取文件名
                        String fileType = listEleids.get(i).getFiletype();//获取文件类型
                        File lastFile = new File(namechange.rootpath + filePath +  File.separator  + fileName);
                        File newFile;
                        if(listEleids.size()==1){
                            newFile = new File(namechange.rootpath + filePath +  File.separator  + archivecode  + "." + fileType);
                        }
                        else{
                            newFile = new File(namechange.rootpath + filePath +  File.separator  + archivecode + "-" + (i + 1) + "." + fileType);//多份电子文件时，档号后面加上电子文件序号（-1、-2、-3....）
                        }
                        lastFile.renameTo(newFile);
                        logger.info(fileName+"更名成功:"+newFile.getName());
                        namechange.electronicRepository.updateFilenameByEleid(newFile.getName(), listEleids.get(i).getEleid());//更新表中文件名称
                        logger.info("更新数据成功");
                    }
                }
            }
        }else{//数据采集
            for (String entryId : entries) {
                List<Tb_electronic_capture> listEleids = namechange.electronicCaptureRepository.findByEntryid(entryId);//获取电子文件集合
                if (listEleids.size()>0){
                    String archivecode = namechange.entryIndexCaptureRepository.findArchivecodeByEntryid(entryId);//获取档号
                    for (int i = 0; i < listEleids.size(); i++) {
                        String filePath = listEleids.get(i).getFilepath();//获取文件路径
                        String fileName =listEleids.get(i).getFilename();//获取文件名
                        String fileType = listEleids.get(i).getFiletype();//获取文件类型
                        File lastFile = new File(namechange.rootpath + filePath +  File.separator  + fileName);
                        File newFile;
                        if(listEleids.size()==1){
                            newFile = new File(namechange.rootpath + filePath +  File.separator  + archivecode  + "." + fileType);
                        }
                        else{
                            newFile = new File(namechange.rootpath + filePath +  File.separator  + archivecode + "-" + (i + 1) + "." + fileType);//多份电子文件时，档号后面加上电子文件序号（-1、-2、-3....）
                        }
                        lastFile.renameTo(newFile);
                        logger.info(fileName+"更名成功:"+newFile.getName());
                        namechange.electronicCaptureRepository.updateFilenameByEleid(newFile.getName(), listEleids.get(i).getEleid());//更新表中文件名称
                        logger.info("更新数据成功");
                    }
                }
            }
        }

    }

}