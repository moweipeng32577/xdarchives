package com.wisdom.web.service;
/**
 * Created by yl on 2020/3/17.
 */

import com.wisdom.util.*;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.xdtech.project.foursexverify.inf.impl.FourSexVerifyImpl;
import net.lingala.zip4j.core.ZipFile;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.portable.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wujy
 */
@Service
@Transactional
public class DataReceiveService {

    @Autowired
    ExportExcelService excelService;

    @Autowired
    ImportService importService;

    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    ThematicMakeRepository thematicMakeRepository;

    @Autowired
    ThematicDetailRepository thematicDetailRepository;

    @Autowired
    ElectronicRepository electronicRepository;

    @Autowired
    DatareceiveRepository datareceiveRepository;

    @Autowired
    LongRetentionRepository longRetentionRepository;

    @Autowired
    FourSexVerifyImpl fourSexVerifyImpl;

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录

    public Page<Tb_datareceive> getRelease(String state, String type, int page, int start, int limit, Sort sort) {
        if (Tb_datareceive.STATE_UNRECEIVE.equals(state)) {
            List<Tb_datareceive> datareceives = new ArrayList<>();
            String directory = rootpath + File.separator + "datarelease" + File.separator + type + File.separator + state;
            File file = new File(directory);        //获取其file对象
            try {
                File[] fs = file.listFiles();    //遍历path下的文件和目录，放在File数组中
                for (File f : fs) {                    //遍历File[]数组
                    if (!f.isDirectory()) {
                        //根据文件名称查找接收表，不存在才插入
                        if (datareceiveRepository.findByFilename(f.getName()).size() == 0) {
                            ZipFile zipFile = new ZipFile(f);
                            if (zipFile.getComment() != null) {
                                datareceives.add(getZipComment(zipFile.getComment(),type,f));
                            } else {
                                Tb_datareceive datareceive = new Tb_datareceive();
                                datareceive.setState(Tb_datareceive.STATE_UNRECEIVE);
                                String size=String.valueOf((double)f.length()/1024/1024);
                                datareceive.setTransfersize(size.substring(0,size.lastIndexOf(".")+4));
                                datareceive.setType(type);
                                datareceive.setFilename(f.getName());
                                datareceive.setFilepath(f.getPath());
                                datareceives.add(datareceive);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (datareceives.size() > 0) {
                datareceiveRepository.save(datareceives);
            }
        }
        Sort sortobj = new Sort(Sort.Direction.DESC, "transdate");
        PageRequest pageRequest = new PageRequest(page - 1, limit,sort!=null?sort:sortobj);
        return datareceiveRepository.findByTypeAndState(pageRequest, type, state);
    }

    /**
     * 获取压缩包的注释转成 Tb_datareceive
     * @param comment 注释
     * @param type 接收类型
     * @param f 压缩包
     * @return
     */
    public Tb_datareceive getZipComment(String comment,String type,File f){
        JSONObject jsonObject = JSONObject.fromObject(comment);
        Tb_datareceive datareceive = (Tb_datareceive) JSONObject.toBean(jsonObject, Tb_datareceive.class);
        datareceive.setState(Tb_datareceive.STATE_UNRECEIVE);
        datareceive.setType(type);
        String size=String.valueOf((double)f.length()/1024/1024);
        datareceive.setTransfersize(size.substring(0,size.lastIndexOf(".")+4));
        datareceive.setFilename(f.getName());
        datareceive.setFilepath(f.getPath());
        datareceive.setCurrentnode(StringUtils.isEmpty(datareceive.getCurrentnode()) ? "" :
                decodeUnicode(datareceive.getCurrentnode()));
        datareceive.setTransfertitle(StringUtils.isEmpty(datareceive.getTransfertitle()) ? "" :
                decodeUnicode(datareceive.getTransfertitle()));
        datareceive.setTransdesc(StringUtils.isEmpty(datareceive.getTransdesc()) ? "" :
                decodeUnicode(datareceive.getTransdesc()));
        datareceive.setTransuser(StringUtils.isEmpty(datareceive.getTransuser()) ? "" :
                decodeUnicode(datareceive.getTransuser()));
        datareceive.setTransorgan(StringUtils.isEmpty(datareceive.getTransorgan()) ? "" :
                decodeUnicode(datareceive.getTransorgan()));
        datareceive.setSequencecode(StringUtils.isEmpty(datareceive.getSequencecode()) ? "" :
                decodeUnicode(datareceive.getSequencecode()));
        return datareceive;
    }

    public String decodeUnicode(String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }

    public void deletOpenFile(String filename) {
        String directory = rootpath + File.separator + "datarelease" + File.separator + "dataopen" + File.separator + "待接收";
        String received = rootpath + File.separator + "datarelease" + File.separator + "dataopen" + File.separator + "已接收";
        File file = new File(directory);        //获取其file对象
        try {
            File[] fs = file.listFiles();    //遍历path下的文件和目录，放在File数组中
            for (File f : fs) {                    //遍历File[]数组
                if (!f.isDirectory()) {
                    if (f.getName().equals(filename)) {
                        // 目标电子文件
                        File targetFile = new File(received, filename);
                        FileUtils.copyFile(f, targetFile);
                        f.delete();
                        //更新接收表对应的数据包
                        datareceiveRepository.updateState(filename);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ExtMsg deletThematicFile(String filenames) {
        List<String> list = Arrays.asList(filenames.split(","));
        String received = rootpath + File.separator + "datarelease" + File.separator + "thematic" + File.separator + "已接收";
        File file = new File(received);        //获取其file对象
        try {
            File[] fs = file.listFiles();    //遍历path下的文件和目录，放在File数组中
            for (File f : fs) {                    //遍历File[]数组
                if (!f.isDirectory()) {
                    if (list.contains(f.getName())) {
                        f.delete();
                        //删除对应的数据包记录
                        datareceiveRepository.deleteByFilename(f.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ExtMsg(true, "删除成功", null);
    }

    public ExtMsg deletReleasedFile(String filenames) {
        List<String> list = Arrays.asList(filenames.split(","));
        String received = rootpath + File.separator + "datarelease" + File.separator + "dataopen" + File.separator + "已接收";
        File file = new File(received);        //获取其file对象
        try {
            File[] fs = file.listFiles();    //遍历path下的文件和目录，放在File数组中
            for (File f : fs) {                    //遍历File[]数组
                if (!f.isDirectory()) {
                    if (list.contains(f.getName())) {
                        f.delete();
                        //删除对应的数据包记录
                        datareceiveRepository.deleteByFilename(f.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ExtMsg(true, "删除成功", null);
    }

    public ExtMsg thematicReceive(String filenames) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> list = Arrays.asList(filenames.split(","));
        String directory = rootpath + File.separator + "datarelease" + File.separator + "thematic" + File.separator + "待接收";
        String received = rootpath + File.separator + "datarelease" + File.separator + "thematic" + File.separator + "已接收";
        String unzip = rootpath + File.separator + "datarelease" + File.separator + "thematic" + File.separator + "临时目录";
        File file = new File(directory);        //获取其file对象
        try {
            File[] fs = file.listFiles();    //遍历path下的文件和目录，放在File数组中
            for (File f : fs) {                    //遍历File[]数组
                if (!f.isDirectory()) {
                    if (list.contains(f.getName())) {
                        ZipUtils.deCompress(f.getPath(), unzip, "UTF-8");//解压文件
                        String unzipPath = unzip + File.separator + f.getName().substring(0, f.getName().lastIndexOf("."));
                        File unzipFile = new File(unzipPath);
                        //获取第一层文件夹名称
                        String[] firstName = unzipFile.list();
                        for (int i = 0; i < firstName.length; i++) {
                            String firstDirectory = unzipPath + File.separator + firstName[i];
                            //获取说明文件.txt内容
                            String explainFile = firstDirectory + File.separator + "说明文件.txt";
                            String encode = EncodingDetect.getJavaEncode(explainFile);
                            Map<String, String> map = EncodingDetect.readThematicFile(explainFile, encode);
                            //拷贝背景图片
                            String backgroundpath = firstDirectory + File.separator + "其它" + File.separator + "专题的背景图";
                            File[] files = new File(backgroundpath).listFiles();
                            String backgroundName = "";
                            if (files.length > 0 && files[0] != null) {
                                backgroundName = files[0].getName();
                            }
                            if (!"".equals(backgroundName)) {
                                if (FileUtil.isexists(backgroundpath + File.separator + backgroundName)) {
                                    FileUtils.copyFile(new File(backgroundpath + File.separator + backgroundName), new File(rootpath + File.separator +
                                            "thematic" + File.separator + "background" + File.separator + userDetails.getUsername() + File.separator + backgroundName));
                                }
                            }
                            //新增专题
                            Tb_thematic_make tbThematicMake = new Tb_thematic_make();
                            tbThematicMake.setTitle(map.get("专题名称") != null ? map.get("专题名称") : "");
                            tbThematicMake.setThematiccontent(map.get("专题描述") != null ? map.get("专题名称") : "");
                            tbThematicMake.setBackgroundpath(File.separator +
                                    "thematic" + File.separator + "background" + File.separator + userDetails.getUsername() + File.separator + backgroundName);
                            Tb_thematic_make saveThematicMake = thematicMakeRepository.save(tbThematicMake);
                            //解析专题目录.xml
                            String thematicList = firstDirectory + File.separator + "专题目录.xml";
                            LinkedHashMap<String, String> thematicmap =
                                    XmlUtil.getXmlFieldCodeAndFieldName(thematicList);
                            List<String> fname = new ArrayList<>();
                            List<String> fcode = new ArrayList<>();
                            for (Map.Entry<String, String> entry : thematicmap.entrySet()) {
                                fcode.add(entry.getKey());
                                fname.add(entry.getValue());
                            }
                            String[] fieldcods = fcode.toArray(new String[fcode.size()]);
                            List<List<String>> lists = XmlUtil.readXml(thematicList);
                            for (List<String> stringList : lists) {
                                Tb_thematic_detail detail = ValueUtil.creatthematicDetail(fieldcods, stringList);
                                detail.setThematicid(saveThematicMake.getThematicid());
                                String thematicdetilid = detail.getThematicdetilid().trim();
                                detail.setThematicdetilid(null);
                                //获取信息编研的电子文件
                                String detilidFilePath =
                                        firstDirectory + File.separator + "电子文件" + File.separator + thematicdetilid;
                                Tb_thematic_detail saveDetail = thematicDetailRepository.save(detail);
                                File[] detilidFiles = new File(detilidFilePath).listFiles();
                                if (detilidFiles != null) {
                                    for (File detilidFile : detilidFiles) {
                                        //转存信息编研电子文件
                                        if (detilidFile.exists()) {
                                            FileUtils.copyFile(detilidFile, new File(rootpath + File.separator +
                                                    "thematic" + File.separator + "prod" + File.separator + userDetails.getUsername() + File.separator + saveDetail.getThematicdetilid().trim() + File.separator + detilidFile.getName()));
                                        }
                                        Tb_electronic tbElectronic = new Tb_electronic();
                                        tbElectronic.setEntryid(saveDetail.getThematicdetilid());
                                        tbElectronic.setFilename(detilidFile.getName());
                                        tbElectronic.setFilepath("/thematic/prod/" + userDetails.getUsername() +
                                                "/" + saveDetail.getThematicdetilid().trim());
                                        tbElectronic.setFiletype(detilidFile.getName().substring(detilidFile.getName().lastIndexOf(".") + 1));
                                        //计算文件的MD5值
                                        FileInputStream fis = null;
                                        StringBuffer md5 = new StringBuffer();
                                        try {
                                            fis = new FileInputStream(detilidFile);
                                            md5.append(DigestUtils.md5Hex(fis));
                                            fis.close();
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        tbElectronic.setMd5(md5.toString());
                                        tbElectronic.setFilesize(String.valueOf(detilidFile.length()));
                                        electronicRepository.save(tbElectronic);
                                    }
                                }
                            }
                        }
                        //删除临时目录
                        FileUtils.deleteDirectory(new File(unzipPath));
                        // 转存到已接收文件夹
                        File targetFile = new File(received, f.getName());
                        FileUtils.copyFile(f, targetFile);
                        f.delete();
                        //更新接收表对应的数据包
                        datareceiveRepository.updateState(f.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ExtMsg(true, "接收成功", null);
    }

    //上传数据包并检查格式
    public ExtMsg uploadDataPackage(MultipartFile source,String dataType){
        Boolean isZip=false;
        ExtMsg msg=new ExtMsg(false,"数据包格式不正确","");
        String directory = rootpath + File.separator + "datarelease" + File.separator + dataType + File.separator + "待接收";
        String unzip = rootpath + File.separator + "datarelease" + File.separator + dataType + File.separator + "临时目录";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(new Date());
        //获取当前文件名称
        String[] subFileName = source.getOriginalFilename().split("\\\\");
        String fileName = source.getOriginalFilename();
        if (subFileName.length > 1) {
            fileName = subFileName[subFileName.length - 1];
        }
        String filePath=unzip+File.separator+fileName;
        File zipFile=new File(filePath);
        try{
            source.transferTo(zipFile);//创建临时文件
            isZip=ZipUtils.isArchiveFile(zipFile);//判断是否为压缩包
            if(!isZip){
                return  msg;
            }
            String unPath = unzip + File.separator + source.getOriginalFilename().substring(0,source.getOriginalFilename().lastIndexOf("."));//创建解压文件夹路径
            ZipFile uploadZipFile=new ZipFile(zipFile);
            ZipUtils.deCompress(filePath, unzip, "UTF-8");//解压文件
            File unFile = new File(unPath);
            if (uploadZipFile.getComment() != null) {
                //Tb_datareceive datareceive=getZipComment(uploadZipFile.getComment(),dataType,zipFile);
                //datareceiveRepository.save(datareceive);
                //专题数据包格式检查
                if("thematic".equals(dataType)) {
                    //获取第一层文件夹名称
                    String[] firstName = unFile.list();
                    for (int i = 0; i < firstName.length; i++) {
                        String firstDirectory = unPath + File.separator + firstName[i];
                        //获取说明文件.txt内容
                        String explainFile = firstDirectory + File.separator + "说明文件.txt";
                        if (!new File(explainFile).exists()) {
                            return msg;
                        }
                        //专题背景
                        String backgroundpath = firstDirectory + File.separator + "其它" + File.separator + "专题的背景图";
                        if (!new File(backgroundpath).exists()) {
                            return msg;
                        }
                        //专题目录.xml
                        String thematicList = firstDirectory + File.separator + "专题目录.xml";
                        if (!new File(thematicList).exists()) {
                            return msg;
                        }
                    }
                }else {//开放数据包检查

                }
            }else {
                return msg;
            }
            FileUtil.copyFile(filePath,directory+File.separator+fileName);
        }catch (Exception e){
            e.printStackTrace();
        }finally {//删除临时压缩包和解压的文件
            zipFile.delete();
            if(isZip) {
                try {
                    FileUtils.deleteDirectory(new File(filePath.substring(0, filePath.lastIndexOf("."))));
                } catch (IOException e) {

                }
            }
        }
        return new ExtMsg(true,"上传数据包成功","");
    }



    public String verification(String receiveids) {
        String[] ids = receiveids.split(",");
        //先删除之前该数据包验证的记录
        longRetentionRepository.deleteByReceiveidIn(ids);
        for (String id : ids) {
            Tb_datareceive datareceive = datareceiveRepository.findByReceiveid(id);
            String filePath=datareceive.getFilepath();
            File file=new File(filePath);
            if(!file.exists()){
                return "验证失败，验证zip文件不存在";
            }
            List<Map<String, Map<String, String>>> entryidMaps = fourSexVerifyImpl.getVerifyForRecords(filePath);
            for (Map<String, Map<String, String>> mapMap : entryidMaps) {
                for (String entryid : mapMap.keySet()) {
                    Map<String, String> xlsxMap = mapMap.get(entryid);
                    Tb_long_retention longRetention = new Tb_long_retention();
                    longRetention.setEntryid(entryid);
                    longRetention.setReceiveid(id);
                    longRetention.setAuthenticity(xlsxMap.get("authenticity"));
                    longRetention.setIntegrity(xlsxMap.get("integrity"));
                    longRetention.setUsability(xlsxMap.get("usability"));
                    longRetention.setSafety(xlsxMap.get("safety"));
                    if (xlsxMap.get("authenticity").indexOf("不通过") > 0 || xlsxMap.get("integrity").indexOf("不通过") > 0 || xlsxMap.get("usability").indexOf("不通过") > 0 || xlsxMap.get("safety").indexOf("不通过") > 0) {
                        xlsxMap.put("checkstatus", "<span style=\"color:red\">不通过</span>");
                    } else {
                        xlsxMap.put("checkstatus", "<span style=\"color:green\">通过</span>");
                    }
                    longRetention.setCheckstatus(xlsxMap.get("checkstatus"));
                    longRetentionRepository.save(longRetention);
                }
            }
        }
        return "执行验证成功";
    }

    public Page<Tb_long_retention> getImplementResult(String receiveid, int page, int start, int limit, Sort sort) {
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        return longRetentionRepository.findByReceiveid(pageRequest, receiveid);
    }
}
