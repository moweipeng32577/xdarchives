package com.wisdom.web.service;


import com.wisdom.util.*;
import com.wisdom.web.entity.*;
import com.wisdom.web.entity.cn.gov.saac.standards.erm.ArchivalCode;
import com.wisdom.web.entity.cn.gov.saac.standards.erm.Content;
import com.wisdom.web.entity.cn.gov.saac.standards.erm.Sip;
import com.wisdom.web.repository.*;
import com.xdtech.project.foursexverify.inf.impl.FourSexVerifyImpl;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class Ftpservice {

    @Autowired
    EntryIndexRepository entryIndexRepository;
    @Autowired
    DataNodeRepository dataNodeRepository;
    @Autowired
    TemplateRepository templateRepository;
    @Autowired
    EntryDetailRepository entryDetailRepository;
    @Autowired
    ElectronicCaptureRepository electronicCaptureRepository;
    @Autowired
    ElectronicRepository electronicRepository;
    @Autowired
    EntryIndexCaptureRepository entryIndexCaptureRepository;
    @Autowired
    MetadataLogService metadataLogService;
    @Autowired
    EntryDetailCaptureRepository entryDetailCaptureRepository;
    @Autowired
    RightOrganRepository rightOrganRepository;
    @Autowired
    OaRecordRepository oaRecordRepository;
    @Autowired
    EntryIndexCaptureService entryIndexCaptureService;
    @Autowired
    CodesettingService codesettingService;
    @Autowired
    CodesetRepository codesetRepository;
    @Autowired
    LongRetentionService longRetentionService;
    @Autowired
    LongRetentionSettingRepository longRetentionSettingRepository;
    @Autowired
    FourSexVerifyImpl fourSexVerifyImpl;
    @Autowired
    LongRetentionRepository longRetentionRepository;

    @Autowired
    NodesettingService nodesettingService;

    @PersistenceContext
    EntityManager entityManager;
    @Value("${system.document.rootpath}")
    private String rootpath;// 系统文件根目录
    private String eledir;// 电子文件存储路径
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void readDatePackage(String filePath) {
        String message = "";
        String[] str = filePath.split("/");
        String filename = str[str.length - 1].substring(0, str[str.length - 1].lastIndexOf("."));
        String unZipPath = rootpath + "/" + filename;
        try {
            ZipUtils.deCompress(filePath, unZipPath);//解压文件

            // 1.获取解压后的条目文件
            File file = new File(unZipPath);
            File[] tempList = file.listFiles();
            List<String> fileList = FileUtil.getFile(tempList[0].getPath());//xml文件+文件夹
            for (int i = 0; i < fileList.size(); i++) {
                if (fileList.get(i).endsWith(".xml")) {//xml文件
                    //解析xml文件

                }
            }
            //调用解析OAxml文件方法

        } catch (Exception e) {
            e.getMessage();
        }
    }

    //FTP导入
    public void importFtp(File[] files, String unzipname,String oaName) {
        //保存解析xml的数据
        List<Map<String, String>> listMap = new ArrayList<>();
        //保存解析的xml文件名
        List<String> nameList = new ArrayList<>();
        //存放组装后的Entryid对象
        List<Tb_entry_index_capture> swEntryIndex = new ArrayList<>();
        List<Tb_entry_detail_capture> swEntryDetail = new ArrayList<>();
        List<Tb_entry_index_capture> fwEntryIndex = new ArrayList<>();
        List<Tb_entry_detail_capture> fwEntryDetail = new ArrayList<>();
        //获取原文目录集合
        List<String> eleFolder = new ArrayList<>();
        int xmlcount = 0;
        for (File file : files) {
            if (file.getName().endsWith(".xml")) { //xml文件
                xmlcount++;
                //2.进行数据包解析
                Sip sip = SipReceiveBS.zipManager(file);
                if (null == sip) {//解析失败
                    FtpUtil.addErrorLog("--sip解析失败：" + unzipname);
                    return;
                }
                //Content
                Content content = sip.getContent();
                //ArchivalCode
                ArchivalCode archivalCode = sip.getArchivalCode();
                //进行参数转换
                Map archivalCode_map = ValueUtil.transBean2Map(archivalCode);
                Map map = ValueUtil.transBean2Map(content);
                map.putAll(archivalCode_map);
                //---来保存对应的原文文件夹名
                map.put("原文目录名", file.getName().substring(0, file.getName().lastIndexOf(".")));
                map.put("unit_name", sip.getUnitName());
                map.put("er_code", sip.getErCode());
                map.put("is_description", sip.getIsDescription());
                map.put("is_description", sip.getIsDescription());
                map.put("单位编码", sip.getUnitId());
                //---来保存对应的原文文件夹名
                map.put("原文目录名", "document");
                listMap.add(map);
                nameList.add(file.getName());
                eleFolder.add(unzipname + File.separator + "document" + File.separator);
            }
        }
        //记录解压包中无XML文件
        if (xmlcount == 0) {
            //写文件
            String a = "";
            a += "OA包内没有条目XML文件，错误OA包:" + unzipname;
            FtpUtil.addErrorFileName(a);
            return;
        }
        String s1 = dataNodeRepository.findnodeidByParentnodename("未归管理", "文书文件");
//        String s1 = dataNodeRepository.findnodeidByParentnodename("未归管理", "OA接收");
        if("".equals(listMap.get(0).get("单位编码"))||null==listMap.get(0).get("单位编码")){
            FtpUtil.addErrorLog("---163-xml中的单位编码为空，包名:" + unzipname);
            if (null != unzipname) {
                FileUtil.delFolder(unzipname);
            }
            return;
        }
        //根据xml中的单位编码拿到节点的organid
        FtpUtil.addErrorLog("---167-单位编码:" + listMap.get(0).get("单位编码"));
        List<Tb_right_organ> organs = rightOrganRepository.findByRefid(listMap.get(0).get("单位编码"));
        if (null == organs||organs.size()==0 ) {
            FtpUtil.addErrorLog("---170-未找到对应机构:" + listMap.get(0).get("单位编码"));
            if (null != unzipname) {
                FileUtil.delFolder(unzipname);
            }
            return;
        }
        if(organs.size()!=1){
            FtpUtil.addErrorLog("---177-出现重复机构编码，机构编码:" + listMap.get(0).get("单位编码"));
            if (null != unzipname) {
                FileUtil.delFolder(unzipname);
            }
            return;
        }
        Tb_right_organ organ = organs.get(0);
        //然后根据父节点id以及organid 查出需要导入的节点
        String swid = dataNodeRepository.findByParentnodeidAndOrganid(s1, organ.getOrganid());
//        String swid = "8a80cb81723acd3601723b4c144f1187";
        if (null == swid || "".equals(swid)) {
            //可能节点是文书下面的某个节点下面的子节点-重新查找节点
            List<Tb_data_node> nodes = dataNodeRepository.findByParentnodeid(s1);
            for (int i = 0; i < nodes.size(); i++) {
                swid = dataNodeRepository.findByParentnodeidAndOrganid(nodes.get(i).getNodeid(), organ.getOrganid());
                if(null!=swid&&!"".equals(swid)){
                    break;
                }
            }
            //还未查找到就记录日志并反回
            if (swid == null || "".equals(swid)) {
                FtpUtil.addErrorLog("--135--未找到对应节点:" + unzipname);
                if (null != unzipname) {
                    FileUtil.delFolder(unzipname);
                }
                return;
            }
        }
        //存放未归管理节点模板
        List<Tb_data_template> swtemplates = templateRepository.findByNodeid(swid);
        //存放收文和发文value
        List<List<String>> swListvalue = new ArrayList<>();
        int forcount = 0;
        //存放对应value的name
        List<String> swfieldname = new ArrayList<>();
        List<String> swfieldcode = new ArrayList<>();
        //组装对象
        for (int i = 0; i < listMap.size(); i++) {//循环每个文件的map
            forcount++;
            List<String> swList = new ArrayList<>();
            //组装对象
            for (int j = 0; j < swtemplates.size(); j++) {//循环节点字段
                //获取配置文件中配置的key（我们模板的code=oa方的字段）
                String temp_file_key = ConfigValue.getSGOATemp(swtemplates.get(j).getFieldcode());
                if (null == temp_file_key || "".equals(temp_file_key)) {//配置文件中没有配置该字段的对应code
                    continue;
                }
                if (listMap.get(i).get(temp_file_key) != null) {
                    if (forcount == 1) {
                        swfieldname.add(swtemplates.get(j).getFieldname());//保存name
                        swfieldcode.add(swtemplates.get(j).getFieldcode());//保存code
                    }
                    swList.add(listMap.get(i).get(temp_file_key));
                }
            }
            if (swList.size() > 0) {
                //此处的作用是用来保存条目的对应目录名--用条目的id保存原文目录名-
                swList.add(listMap.get(i).get("原文目录名"));//-在List集合参数后面添加目录名
                swListvalue.add(swList);
            }
        }
        swfieldname.add("条目id");//占用条目id来保存原文目录名
        swfieldcode.add("entryid");//占用条目id来保存原文目录名---收文
        //集合转换成数组
        String[] swfieldcodearr = new String[swfieldcode.size()];//-收文
        swfieldcode.toArray(swfieldcodearr);
        //参数准备完毕 --进行对象组装
        try {
            for (int i = 0; i < swListvalue.size(); i++) {//收文
                Tb_entry_index_capture swindex = ValueUtil.captureCreatEntryIndex(swfieldcodearr, swListvalue.get(i));
                Tb_entry_detail_capture swdetail = ValueUtil.captureCreatEntryDetail(swfieldcodearr, swListvalue.get(i));
                swEntryIndex.add(swindex);
                swEntryDetail.add(swdetail);
            }
            if (swEntryIndex.size() > 0 && swEntryDetail.size() > 0) {//插入收文
                ftpSaveEntry(swEntryIndex, swEntryDetail, swid, eleFolder,oaName+".zip",organ.getCode());
            }

        } catch (Exception e) {
            FtpUtil.addErrorLog("--组装对象error:" + e);
//            e.printStackTrace();
        }
    }

    //FTP导入
    public void importFtps(File[] files, String unzipname, String nodeid) {
        //保存解析xml的数据
        List<Map<String, String>> listMap = new ArrayList<>();
        //保存解析的xml文件名
        List<String> nameList = new ArrayList<>();
        //存放组装后的Entryid对象
        List<Tb_entry_index_capture> entryIndex = new ArrayList<>();
        List<Tb_entry_detail_capture> entryDetail = new ArrayList<>();
        //获取原文目录集合
        List<String> eleFolder = new ArrayList<>();
        int xmlcount = 0;
        for (File file : files) {
            if (file.getName().endsWith(".xml")) { //xml文件
                xmlcount++;
                //2.进行数据包解析
                Map map = XmlUtil.parseSoapMessage(file);
                //---来保存对应的原文文件夹名
                map.put("原文目录名", file.getName().substring(0, file.getName().lastIndexOf(".")));
                listMap.add(map);
                nameList.add(file.getName());
                eleFolder.add(unzipname + File.separator + file.getName().substring(0, file.getName().lastIndexOf(".")) + File.separator);
            }
        }
        //记录解压包中无XML文件
        if (xmlcount == 0) {
            //写文件
            String a = "";
            a += "OA包内没有条目XML文件，错误OA包:" + unzipname + "\r\n";
            FtpUtil.addErrorFileName(a);
        }

        //存放未归管理节点模板
        List<Tb_data_template> templates = templateRepository.findByNodeid(nodeid);
        //存放收文和发文value
        List<List<String>> listvalue = new ArrayList<>();
        //存放对应value的name
        List<String> fieldname = new ArrayList<>();
        List<String> fieldcode = new ArrayList<>();
        int forcount = 0;
        //组装对象
        for (int i = 0; i < listMap.size(); i++) {//循环每个文件的map
            forcount++;
            List<String> list = new ArrayList<>();
            for (int j = 0; j < templates.size(); j++) {//循环节点字段
                if (listMap.get(i).get(templates.get(j).getFieldname()) != null) {
                    if (forcount == 1) {
                        fieldname.add(templates.get(j).getFieldname());//保存name
                        fieldcode.add(templates.get(j).getFieldcode());//保存code
                    }
                    list.add(listMap.get(i).get(templates.get(j).getFieldname()));
                }
            }
            if (list.size() > 0) {
                list.add(listMap.get(i).get("原文目录名"));//-在List集合参数后面添加目录名
                listvalue.add(list);
            }
        }
        fieldname.add("条目id");//占用条目id来保存原文目录名
        fieldcode.add("entryid");//占用条目id来保存原文目录名
        //集合转换成数组
        String[] fieldcodearr = new String[fieldcode.size()];
        fieldcode.toArray(fieldcodearr);
        //参数准备完毕 --进行对象组装
        try {
            for (int i = 0; i < listvalue.size(); i++) {
                Tb_entry_index_capture index = ValueUtil.captureCreatEntryIndex(fieldcodearr, listvalue.get(i));
                Tb_entry_detail_capture detail = ValueUtil.captureCreatEntryDetail(fieldcodearr, listvalue.get(i));
                entryIndex.add(index);
                entryDetail.add(detail);
            }
            if (entryIndex.size() > 0 && entryDetail.size() > 0) {
                ftpSaveEntrycapture(entryIndex, entryDetail, nodeid, eleFolder);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //FTP 导入方法
    public void ftpSaveEntrycapture(List<Tb_entry_index_capture> indexList, List<Tb_entry_detail_capture> details, String nodeid,
                                    List<String> eleFolder) {
        for (int i = 0; i < indexList.size(); i++) {
            indexList.get(i).setNodeid(nodeid);
            String indexEntryId = indexList.get(i).getEntryid();
            indexList.get(i).setEntryid(null);
            Tb_entry_index_capture entryid = entryIndexCaptureRepository.save(indexList.get(i));
            Tb_metadata_log metadataLog = new Tb_metadata_log("通过电子接收，将立档单位生成的数据包接入到数据采集中", "电子文件接收", entryid.getEntryid
                    ());
            metadataLogService.save(metadataLog);
            Tb_long_retention_setting setting = longRetentionSettingRepository.findByNodeid(nodeid);
            Tb_long_retention long_retention=longRetentionRepository.findByEntryid(entryid.getEntryid());
            Tb_entry_index tb_entry_index=new Tb_entry_index();
            BeanUtils.copyProperties(entryid, tb_entry_index);
            //判断是否为文书档案节点
            Boolean isWsNode=false;
            String nodeAllName=nodesettingService.getNodefullnameLoop(nodeid,"-","");
            if(nodeAllName.contains("文书")){
                isWsNode=true;
            }
            //进行四性验证
            Map map = longRetentionService.getFourSexVerify(tb_entry_index,"capture",setting,long_retention,isWsNode);

            details.get(i).setEntryid(entryid.getEntryid());
            Tb_entry_detail_capture detail = entryDetailCaptureRepository.save(details.get(i));
            String newpath = getStorageDir();
            for (int k = 0; k < eleFolder.size(); k++) {// 遍历原文目录
                String[] lib = eleFolder.get(k).replaceAll("\\\\", "/").split("/");
                String libName = lib[lib.length - 1];
                // 未插入数据库前的entryid
                // 判断是否存在entryid命名目录
                //String indexEntryId = indexList.get(i).getEntryid().trim();
                if (indexEntryId != null) {
                    if (indexEntryId.trim().equals(libName)) {
                        try {
                            FileUtil.copyDir(eleFolder.get(k), newpath + "/" + entryid.getEntryid());// 将原文文件copy到newpath
                            List<String> files = FileUtil.getFile(eleFolder.get(k));
                            for (int l = 0; l < files.size(); l++) {// 遍历单个条目的原文数
                                Tb_electronic_capture electronic = new Tb_electronic_capture();
                                String[] fnames = files.get(l).replaceAll("\\\\", "/").split("/");
                                String eleFileName = fnames[fnames.length - 1];// 带后缀的原文名
                                String suffix = eleFileName.substring(eleFileName.lastIndexOf(".") + 1);
                                electronic.setFilepath((newpath + "/" + entryid.getEntryid()).replace(rootpath, ""));
                                electronic.setFilename(eleFileName);
                                electronic.setFilesize(String.valueOf(new File(files.get(l)).length()));
                                electronic.setEntryid(entryid.getEntryid());
                                electronic.setFiletype(suffix);
                                electronicCaptureRepository.save(electronic);
                            }
                            // 修改对应条目的eleid
                            entryIndexCaptureRepository.updateEleidByEntryid(String.valueOf(files.size()), entryid.getEntryid());
                        } catch (IOException e) {
                            logger.error(e.getMessage());
                        }
                    }
                }
            }
        }
    }


    //FTP 导入方法
    public void ftpSaveEntry(List<Tb_entry_index_capture> indexList, List<Tb_entry_detail_capture> details, String nodeid,
                             List<String> eleFolder,String oaName,String organ_code)throws Exception {
        for (int i = 0; i < indexList.size(); i++) {
            indexList.get(i).setNodeid(nodeid);
            String indexEntryId = indexList.get(i).getEntryid();
            indexList.get(i).setEntryid(null);
            //默认字段给值
            if (null == indexList.get(i).getDescriptiondate() || "".equals(indexList.get(i).getDescriptiondate())) {
                indexList.get(i).setDescriptiondate(new
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format
                        (System.currentTimeMillis()));
            }
            if (null == indexList.get(i).getDescriptionuser() || "".equals(indexList.get(i).getDescriptionuser())) {
                indexList.get(i).setDescriptionuser("oa");
            }
            if(null!=indexList.get(i).getFiledate()&&!"".equals(indexList.get(i).getFiledate())){

                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(indexList.get(i).getFiledate());
                indexList.get(i).setFiledate(new SimpleDateFormat("yyyyMMdd").format(date));
            }
            //设置份数
            indexList.get(i).setFscount("1");
            //获取流水号
            indexList.get(i).setSerial("");
            String serial = getSerial(indexList.get(i), nodeid);
            indexList.get(i).setSerial(serial);
            //密级
            indexList.get(i).setEntrysecurity("无密");
//            Tb_entry_index_capture entryid = entryIndexCaptureRepository.save(indexList.get(i));
            Tb_entry_index_capture entryid = indexList.get(i);
            Tb_entry_detail_capture  detail = details.get(i);
            entityManager.persist(entryid);
            details.get(i).setEntryid(entryid.getEntryid());
            entityManager.persist(detail);
//            Tb_entry_detail_capture detail = entryDetailCaptureRepository.save(details.get(i));
            String newpath = getStorageDir();
            for (int k = 0; k < eleFolder.size(); k++) {// 遍历原文目录
                String[] lib = eleFolder.get(k).replaceAll("\\\\", "/").split("/");
                String libName = lib[lib.length - 1];
                // 未插入数据库前的entryid
                // 判断是否存在entryid命名目录
                //String indexEntryId = indexList.get(i).getEntryid().trim();
                if (indexEntryId != null) {
                    if (indexEntryId.trim().equals(libName)) {
                        try {
                            FileUtil.copyDir(eleFolder.get(k), newpath + "/" + entryid.getEntryid());// 将原文文件copy到newpath
                            List<String> files = FileUtil.getFile(eleFolder.get(k));
                            for (int l = 0; l < files.size(); l++) {// 遍历单个条目的原文数
                                Tb_electronic_capture electronic = new Tb_electronic_capture();
                                String[] fnames = files.get(l).replaceAll("\\\\", "/").split("/");
                                String eleFileName = fnames[fnames.length - 1];// 带后缀的原文名
                                String suffix = eleFileName.substring(eleFileName.lastIndexOf(".") + 1);
                                electronic.setFilepath((newpath + "/" + entryid.getEntryid()).replace(rootpath, ""));
                                electronic.setFilename(eleFileName);
                                electronic.setFilesize(String.valueOf(new File(files.get(l)).length()));
                                electronic.setEntryid(entryid.getEntryid());
                                electronic.setFiletype(suffix);
                                electronicCaptureRepository.save(electronic);
                                entityManager.persist(electronic);
                            }
                            // 修改对应条目的eleid
//                            entryIndexRepository.updateEleidByEntryid(String.valueOf(files.size()), entryid.getEntryid());
                            entryIndexCaptureRepository.updateEleidByEntryid(String.valueOf(files.size()), entryid.getEntryid());
                        } catch (IOException e) {
                            logger.error(e.getMessage());
                        }
                    }
                }
            }
            entityManager.flush();
            entityManager.close();
            //进行四性验证
//            Map map = longRetentionService.getOAFourSexVerify(entryid.getEntryid(),"capture",nodeid);
            Tb_entry_index tb_entry_index=new Tb_entry_index();
            BeanUtils.copyProperties(entryid, tb_entry_index);
            String zip_path = getOAFourSexVerify(tb_entry_index,"capture",nodeid);
            // TODO: 2019/10/10 0010  修改oa包的
            //修改oa包的
            oaRecordRepository.updateStateByName("已接收", entryid.getEntryid(),entryid.getTitle(), oaName,organ_code);
        }
    }


    public void saveOARecord(String filename, long filesize, String receivestate, String filestate) {
        Long count = oaRecordRepository.findCountByName(filename);
        if (count == 0) {
            //进行日志插入
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Tb_oa_record oa_record = new Tb_oa_record();
            oa_record.setFilename(filename);
//            oa_record.setFilepath(files.getPath());
            oa_record.setFilesize(String.valueOf(filesize));
            oa_record.setReceivestate(receivestate);
            oa_record.setFilestate(filestate);
            oa_record.setDate(sdf.format(new Date()));
            entityManager.persist(oa_record);
            entityManager.flush();
            entityManager.close();
        }
    }

    // 点击保存后的电子文件存放路径(electronics/storages/年/月/日/类型(capture-采集、management-数据管理)/条目ID)
    public String getStorageBaseDir() {
        Calendar cal = Calendar.getInstance();
        return "/electronics/storages/" + cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/"
                + cal.get(Calendar.DATE) + "/" + "management";
    }

    private String getStorageDir() {
        Calendar cal = Calendar.getInstance();
        eledir = rootpath + getStorageBaseDir();

        File eleDir = new File(eledir);
        if (!eleDir.exists()) {
            eleDir.mkdirs();
        }
        return eledir;
    }

    public List<String> unzipAndDelFile(String loadPath, String unZipPath, String encoding) {
        List<String> list = new ArrayList<>();
        File[] filess = new File(loadPath + File.separator).listFiles();

        for (File file : filess) {
            if (file.getName().endsWith(".zip")) {
                long size = 0L;
                try {
                    size = new File(loadPath + File.separator + file.getName()).length();
                    ZipUtils.unZip4jx(loadPath + File.separator + file.getName(),
                            unZipPath + File.separator + file.getName().substring(0, file.getName().lastIndexOf(".")), encoding);//解压文件
                    list.add(unZipPath + File.separator + file.getName().substring(0, file.getName().lastIndexOf(".")));
                    new File(loadPath + File.separator + file.getName()).delete();
                    //记录获取到的OA文件包 插入数据库 1.oa包名 2.接收时间 3.文件状态 4.文件路径
                    // TODO: 2019/10/10 0010
                    Tb_oa_record oa_record = new Tb_oa_record();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    oa_record.setFilename(file.getName().substring(0,file.getName().lastIndexOf(".")));
                    oa_record.setDate(sdf.format(new Date()));
                    oa_record.setFilestate("正常");
                    oa_record.setReceivestate("已下载");
//                    oa_record.setFilepath(loadPath + File.separator + file.getName());
                    oa_record.setFilesize(String.valueOf(size));
                    entityManager.persist(oa_record);
                } catch (Exception e) {
                    //解包失败 也需要记录包名 修改记录的文件状态为 错误  并保存该OA包
                    String errorOA = loadPath + File.separator + file.getName();
                    String newErroPath = loadPath + File.separator + "错误OA包" + File.separator + file.getName();
                    try {
                        FileUtil.copyFile(errorOA, newErroPath);
                    }catch (IOException io){
                        io.printStackTrace();
                    }
                    Tb_oa_record oa_record = new Tb_oa_record();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    oa_record.setFilename(file.getName().substring(0,file.getName().lastIndexOf(".")));
                    oa_record.setDate(sdf.format(new Date()));
                    oa_record.setFilestate("错误");
                    oa_record.setReceivestate("已下载");
                    oa_record.setFilepath(loadPath + File.separator + file.getName());
                    oa_record.setFilepath(newErroPath);
                    oa_record.setFilesize(String.valueOf(size));
                    entityManager.persist(oa_record);
                    e.printStackTrace();
                }
            }
        }
        entityManager.flush();
        entityManager.clear();
        return list;
    }

    public String getSerial(Tb_entry_index_capture index, String nodeid) {
        List<Tb_codeset> codeSettingFieldList = codesettingService.findAllByDatanodeid(index.getNodeid());// 获取档号设置字段集合
        //List<String> codeSettingFieldList = new ArrayList<>();
        //codeSettingFieldList.add("serial");
        String serial = index.getSerial();
        String newSerial = "";
        if (codeSettingFieldList.size() == 0) {//未设置档号组成字段时
            return "";
        } else {
            if ("".equals(serial) || "null".equals(serial) || serial == null) {//文件流水号为空 进行获取该节点的最大流水号递增
                int c = entryIndexCaptureService.getCalValue(index, nodeid, codeSettingFieldList, null);
                // 获取计算项单位长度
                int number = Integer.parseInt(codesetRepository.findFieldlengthByDatanodeid(nodeid).get(0).toString());
                newSerial = alignValue(number, c);
            } else {
                newSerial = index.getSerial();
            }
            return newSerial;
        }
    }

    public String alignValue(Object no, Object vo) {
        Integer n = Integer.parseInt(no.toString());
        return n == 0 ? vo.toString().trim() : String.format("%0" + n + "d", vo).trim();
    }


    //获取FTP 目录 下载数据包到本地

    /**
     * @param ftpClient Ftp连接
     * @param ftpPath   需要下载文件所在目录
     * @param loadPath  下载到本地目录
     * @param unZipPath 解压目录
     */
    public List<String> FileList(FTPClient ftpClient, String ftpPath, String loadPath, String unZipPath
            , List<String> saveFileName, String ftpFileManage, String encoding) {
        //路径判断 是否存在，不存在就创建路径
        if (loadPath != null && !new File(loadPath).exists()) {
            new File(loadPath).mkdirs();
        }
        if (unZipPath != null && !new File(unZipPath).exists()) {
            new File(unZipPath).mkdirs();
        }
        ftpClient.setControlEncoding("utf-8");
        List list = new ArrayList();
        try {
//            ftpClient.changeWorkingDirectory(new String(ftpPath.getBytes("GBK"), "ISO-8859-1"));
            ftpClient.changeWorkingDirectory(ftpPath);
            ftpClient.enterLocalPassiveMode();
            FTPFile[] files = ftpClient.listFiles();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            addLog(files.length+"");
            for (FTPFile ftpFile : files) {
                Tb_oa_record oa_record = new Tb_oa_record();
                //使用验证方式解决文件重复
                if (ftpFile.isFile() && ftpFile.getName().endsWith(".zip") && "false".equals(ftpFileManage)) {
                    //读文件
                    boolean b = FtpUtil.createsaveFilenameXml(ftpFile.getName());
                    if (b) {//确认重复
                        //System.out.println("重复文件");
                        continue;
                    }
//                    addLog(ftpFile.getName());
                    oa_record.setFilename(ftpFile.getName());
                    oa_record.setFilepath(loadPath + File.separator + ftpFile.getName());
                    oa_record.setFilesize(String.valueOf(ftpFile.getSize()));
                    oa_record.setReceivestate("已下载");
                    oa_record.setFilestate("未解压");
                    oa_record.setDate(sdf.format(new Date()));
                    if (!b) {//不重复文件
                        System.out.println("发现OA数据包文件,文件名：" + ftpFile.getName());
                        //1.进行文件下载
                        OutputStream ios = new FileOutputStream(new File(loadPath + File.separator + ftpFile.getName()));
                        ftpClient.retrieveFile(ftpFile.getName(), ios);
                        ios.close();
                        try {
                            ZipUtils.unZip4jx(loadPath + File.separator + ftpFile.getName(),
                                    unZipPath + File.separator + ftpFile.getName().substring(0, ftpFile.getName().lastIndexOf(".")), encoding);//解压文件
                            oa_record.setFilestate("已解压");
                        }catch (Exception e){//解压报错
                            //记录格式错误的oa包名
                            FtpUtil.addErrorFileName("——"+ftpFile.getName());
                            FtpUtil.addErrorLog("--ftputil-221--"+e);
                            oa_record.setFilestate("解压失败");
                        }
                        list.add(unZipPath + File.separator + ftpFile.getName().substring(0, ftpFile.getName().lastIndexOf(".")));
//                        new File(loadPath + File.separator + ftpFile.getName()).delete();
                        Long count = oaRecordRepository.findCountByName(oa_record.getFilename());
                        if(count==0) {
                            entityManager.persist(oa_record);
                            entityManager.flush();
                            entityManager.close();
                        }
                    }
                    //写文件
                    String a = "";
                    a += ftpFile.getName() + "\r\n";
                    FtpUtil.addFileName(a);
                }
                //直接删除FTP上文件来处理重复
                if (ftpFile.isFile() && ftpFile.getName().endsWith(".zip") && "true".equals(ftpFileManage)) {//判断是否已经下载过并且是zip数据包文件
                    System.out.println("发现OA数据包文件,文件名：" + ftpFile.getName());
                    //1.进行文件下载
                    OutputStream ios = new FileOutputStream(new File(loadPath + File.separator + ftpFile.getName()));
                    ftpClient.retrieveFile(ftpFile.getName(), ios);
                    ios.close();
                    //对FTP上下载过的数据包直接删除
                    ftpClient.deleteFile(ftpPath + File.separator + ftpFile.getName());
                    oa_record.setFilename(ftpFile.getName());
                    oa_record.setFilepath(loadPath + File.separator + ftpFile.getName());
                    oa_record.setFilesize(String.valueOf(ftpFile.getSize()));
                    oa_record.setReceivestate("已下载");
                    oa_record.setFilestate("未解压");
                    try {
                        ZipUtils.unZip4jx(loadPath + File.separator + ftpFile.getName(),
                                unZipPath + File.separator + ftpFile.getName().substring(0, ftpFile.getName().lastIndexOf(".")));//解压文件
                        oa_record.setFilestate("已解压");
                    }catch (Exception e){
                        //记录格式错误的oa包名
                        FtpUtil.addErrorFileName("——"+ftpFile.getName());
                        FtpUtil.addErrorLog("-ftputil-250-"+e);
                        oa_record.setFilestate("解压失败");
                    }
                    list.add(unZipPath + File.separator + ftpFile.getName().substring(0, ftpFile.getName().lastIndexOf(".")));
//                    new File(loadPath + File.separator + ftpFile.getName()).delete();
                    Long count = oaRecordRepository.findCountByName(oa_record.getFilename());
                    if(count==0) {
                        entityManager.persist(oa_record);
                        entityManager.flush();
                        entityManager.close();
                    }
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
            FtpUtil.addErrorLog(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"--ftputil-258--"+e);
        }
        return list;
    }

    //从化4性验证 -test
    /**
     * 获取四性验证结果
     *
     * @param entryid
     * @return 路径
     */
    public String getOAFourSexVerify(Tb_entry_index entryid, String module, String nodeid) {
        //Tb_entry_index index = entryIndexRepository.findByEntryid(entryid);
        //生成长期保管包
        String zippath = longRetentionService.longRetention(entryid.getEntryid(), module);
        Map<String, String> map;
        map = getAllVerifyResult(zippath);
        if (map.get("authenticity").indexOf("不通过") > 0 || map.get("integrity").indexOf("不通过") > 0 || map.get("usability").indexOf("不通过") > 0 || map.get("safety").indexOf("不通过") > 0) {
            map.put("checkstatus", "<span style=\"color:red\">不通过</span>");
        } else {
            map.put("checkstatus", "<span style=\"color:green\">通过</span>");
        }
        Tb_long_retention tb_long_retention = longRetentionRepository.findByEntryid(entryid.getEntryid());
        if (tb_long_retention != null) {
            tb_long_retention.setCheckstatus(map.get("checkstatus"));
            tb_long_retention.setAuthenticity(map.get("authenticity"));
            tb_long_retention.setIntegrity(map.get("integrity"));
            tb_long_retention.setUsability(map.get("usability"));
            tb_long_retention.setSafety(map.get("safety"));
        } else {
            tb_long_retention = new Tb_long_retention();
            tb_long_retention.setEntryid(entryid.getEntryid());
            tb_long_retention.setCheckstatus(map.get("checkstatus"));
            tb_long_retention.setAuthenticity(map.get("authenticity"));
            tb_long_retention.setIntegrity(map.get("integrity"));
            tb_long_retention.setUsability(map.get("usability"));
            tb_long_retention.setSafety(map.get("safety"));
        }
        longRetentionRepository.save(tb_long_retention);
        //删除采集验证的zip,采集的验证包不用长期保存
        if ("capture".equals(module)) {//数据采集
            File file = new File(zippath);
            // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        }
        return zippath;
    }

    public Map<String, String> getAllVerifyResult(String filePath) {
        try {
            ZipFile zFile = new ZipFile(filePath);
            zFile.setFileNameCharset("GBK");
            return getAllVerifyResult(zFile);
        } catch (ZipException var4) {
            var4.printStackTrace();
            Map<String, String> map = new HashMap();
            map.put("authenticity", fourSexVerifyImpl.convertsHtml("验证不通过,文件不存在", false));
            map.put("integrity", fourSexVerifyImpl.convertsHtml("验证不通过,文件不存在", false));
            map.put("usability", fourSexVerifyImpl.convertsHtml("验证不通过,文件不存在", false));
            map.put("safety", fourSexVerifyImpl.convertsHtml("验证不通过,文件不存在", false));
            return map;
        }
    }

    private Map<String, String> getAllVerifyResult(ZipFile zFile) {
        Map<String, String> map = new HashMap();
        List<Map<String, Map<String, String>>> entryMaps = fourSexVerifyImpl.getXmlData(zFile);
        map.put("authenticity", authenticityVerify(entryMaps));
        map.put("integrity", integrityVerify(entryMaps));
        map.put("usability", usabilityVerify(entryMaps));
        map.put("safety", safetyVerify(entryMaps, zFile));
        return map;
    }

    private String authenticityVerify(List<Map<String, Map<String, String>>> entryMaps) {
        StringBuffer mAccuracy = new StringBuffer();
        mAccuracy.append(fourSexVerifyImpl.convertsHtml("目录信息准确性检测通过", true));
        mAccuracy.append(fourSexVerifyImpl.convertsHtml("目录和电子档案内容关联准确性检测通过", true));
        mAccuracy.append(fourSexVerifyImpl.convertsHtml("电子档案内容准确性检测", true));
        mAccuracy.append(fourSexVerifyImpl.convertsHtml("电子档案封装包准确性检测通过", true));
        return fourSexVerifyImpl.appendStatus(mAccuracy);
    }

    private String integrityVerify(List<Map<String, Map<String, String>>> entryMaps) {
        StringBuffer mIntegrity = new StringBuffer();
        mIntegrity.append(fourSexVerifyImpl.convertsHtml("电子档案封装包完整性检测通过", true));
        return fourSexVerifyImpl.appendStatus(mIntegrity);
    }

    private String usabilityVerify(List<Map<String, Map<String, String>>> entryMaps) {
        StringBuffer mAvailability = new StringBuffer();
        mAvailability.append(fourSexVerifyImpl.convertsHtml("目录数据可用性检测通过", true));
        mAvailability.append(fourSexVerifyImpl.convertsHtml("电子档案内容可用性检测通过", true));
        mAvailability.append(fourSexVerifyImpl.convertsHtml("电子档案内容软硬件环境监测通过", true));
        return fourSexVerifyImpl.appendStatus(mAvailability);
    }

    private String safetyVerify(List<Map<String, Map<String, String>>> entryMaps, ZipFile zFile) {
        StringBuffer mSafety = new StringBuffer();
        mSafety.append(fourSexVerifyImpl.convertsHtml("电子档案病毒检测通过", true));
        mSafety.append(fourSexVerifyImpl.convertsHtml("软件系统安全性检测通过", true));
        return fourSexVerifyImpl.appendStatus(mSafety);
    }

    public static void addLog(String addfilename) {
        String dir = ConfigValue.getPath("system.document.rootpath");//E:/document
        String filenmae = dir + File.separator +"OAFile"+File.separator+ "OA接收" + File.separator + "OALog2.txt";
        File file = new File(filenmae);
        FileWriter fileWriter = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file, true);

            fileWriter.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"="+addfilename+ "\r\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
