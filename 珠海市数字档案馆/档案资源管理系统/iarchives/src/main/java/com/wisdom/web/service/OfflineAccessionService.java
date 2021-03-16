package com.wisdom.web.service;

import com.wisdom.util.FileUtil;
import com.wisdom.util.ZipUtils;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_offline_accession_batch;
import com.wisdom.web.entity.Tb_offline_accession_batchdoc;
import com.wisdom.web.repository.TbofflineAccessionDocRepository;
import com.wisdom.web.repository.TbofflineAccessionRepository;
import com.wisdom.web.security.SecurityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.xdtech.project.foursexverify.inf.impl.FourSexVerifyImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by yl on 2017/11/4.
 * 离线接收service
 */
@Service
@Transactional
public class OfflineAccessionService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TbofflineAccessionRepository tbofflineAccessionRepository;

    @Autowired
    TbofflineAccessionDocRepository tbofflineAccessionDocRepository;

    @Autowired
    FourSexVerifyImpl fourSexVerifyImpl;

    @Autowired
    ElectronicService electronicService;

    @Autowired
    Ftpservice ftpservice;

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录

    public Page<Tb_offline_accession_batch> getBatch(Specifications sp, PageRequest pageRequest){
        return  tbofflineAccessionRepository.findAll(sp,pageRequest);
    }

    public Page<Tb_offline_accession_batchdoc> getBatchdoc(PageRequest pageRequest,String batchid){
        return  tbofflineAccessionDocRepository.findByBatchid(pageRequest,batchid);
    }

    public ExtMsg addBatch(Tb_offline_accession_batch batch){
        ExtMsg msg = new ExtMsg();
        tbofflineAccessionRepository.save(batch);
        msg.setMsg("添加批次成功");
        return msg;
    }

    public void addBatchDoc(List<Tb_offline_accession_batchdoc> batchdocList){
        for(Tb_offline_accession_batchdoc doc:batchdocList){
            tbofflineAccessionDocRepository.save(doc);
        }
    }

    public boolean batchDel(String[] batchids) {
        try{
            Integer billCount = tbofflineAccessionRepository.deleteByBatchidIn(batchids);//删除批次
            tbofflineAccessionDocRepository.deleteByBatchidIn(batchids);//删除批次条目
            if(billCount>0){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取四性验证结果
     * @param fileNames
     * @return
     */
    public List<Map<String, String>> getFourSexVerify(String fileNames, String insertFileNames){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String[] files = fileNames.split(",");
        List<Map<String, String>> resList = new ArrayList<Map<String, String>>();
        if(!"".equals(fileNames)){
            for(String file : files){
                Map<String, String> map = fourSexVerifyImpl.getAllVerifyResult(rootpath +
                        File.separator+"electronics"+File.separator+"offlineAccession"+File.separator+ userDetails.getUsername()+File
                        .separator+ file,false);
                map.put("filename", file);
                if( map.get("authenticity").indexOf("不通过")>0 || map.get("integrity").indexOf("不通过")>0||map.get("usability").indexOf("不通过")>0||map.get("safety").indexOf("不通过")>0){
                    map.put("checkstatus", "<span style=\"color:red\">不通过</span>");
                }else{
                    map.put("checkstatus", "<span style=\"color:green\">通过</span>");
                }

                //是否接入
                if(insertFileNames != null){
                    String [] insertFiles = insertFileNames.split(",");
                    for(String insertfile:insertFiles){
                        if(insertfile.equals(file) ){
                            map.put("isaccess","已接入");
                        }
                    }
                }
                resList.add(map);
            }
        }
        return resList;
    }

    /**
     * 获取封装包的树结构
     * @param filename
     * @return
     */
    public List<com.xdtech.project.foursexverify.entity.FileTree> getVerifyPackage(String filename) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<com.xdtech.project.foursexverify.entity.FileTree> trees = new ArrayList<>();
        trees = fourSexVerifyImpl.getFileTrees(filename, rootpath +
                File.separator + "electronics" + File.separator + "offlineAccession" + File.separator + userDetails.getUsername() + File
                .separator + filename);
        return trees;
    }

    /**
     * 获取xml元数据
     * @param fileName
     * @param xmlName
     * @return
     */
    public Map<String,String> getMetadata(String fileName,String xmlName){
        return fourSexVerifyImpl.getXmlData(electronicService.getUploadOfflineDir()+ File.separator+fileName,xmlName);
    }


    /**
     * 接入系统
     * @param nodeid
     * @param fileNames
     */
    public void insertCapture (String nodeid,String fileNames){
        String[] names = fileNames.split(",");
        for(String name:names){
            //解压的目标文件夹
            String unZipPath = electronicService.getUploadOfflineDir()+ File.separator + name.substring(0, name.lastIndexOf("."));
            //需要解压的zip文件
            String zipPath = electronicService.getUploadOfflineDir() + File.separator + name;
            File unFile = new File(unZipPath);
            if (!unFile.exists()) {
                unFile.mkdirs();
            }
            try {
                //解压文件
                ZipUtils.unZip4jx(zipPath, unZipPath);
                File[] files = unFile.listFiles();
                //调用ftp导入oa包方法
                ftpservice.importFtp(files,unZipPath,nodeid);
                FileUtil.delFolder(unZipPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 修改信息
     * @param ids
     */
    public void updateDoc (String[] ids){
        for(String id:ids){
           tbofflineAccessionDocRepository.updateIsaccessByid(id);
        }
    }


    /**
     * 获取封装包的树结构
     * @param filename
     * @return
     */
    public List<com.xdtech.project.foursexverify.entity.FileTree> getOAVerifyPackage(String filename) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<com.xdtech.project.foursexverify.entity.FileTree> trees = new ArrayList<>();
        trees = fourSexVerifyImpl.getFileTreesCharsetGBK(filename, rootpath + File.separator + "OAFile" + File.separator + "OA接收" + File.separator + filename);
//        trees = fourSexVerifyImpl.getFileTrees(filename, rootpath +
//                File.separator + "electronics" + File.separator + "offlineAccession" + File.separator + userDetails.getUsername() + File
//                .separator + "离线接收_20090523164907_立档单位.zip");
        return trees;
    }

}
