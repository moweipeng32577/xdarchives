package com.wisdom.web.service;

import com.wisdom.util.FileUtil;
import com.wisdom.util.GainField;
import com.wisdom.util.GuavaCache;
import com.wisdom.util.ZipUtils;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.xdtech.project.foursexverify.entity.FileTree;
import com.xdtech.project.foursexverify.inf.impl.FourSexVerifyImpl;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yl on 2019/11/22.
 */
@Service
@Transactional
public class LongRetentionService {
    @Autowired
    FourSexVerifyImpl fourSexVerifyImpl;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    EntryIndexCaptureRepository entryIndexCaptureRepository;

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录

    @Autowired
    ElectronicRepository electronicRepository;

    @Autowired
    ElectronicCaptureRepository electronicCaptureRepository;

    @Autowired
    ElectronicSolidRepository electronicSolidRepository;

    @Autowired
    LongRetentionRepository longRetentionRepository;

    @Autowired
    LongRetentionSettingRepository longRetentionSettingRepository;

    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    PublicUtilService publicUtilService;

    @Autowired
    EntryIndexCaptureService entryIndexCaptureService;

    @Autowired
    EntryIndexService entryIndexService;

    /**
     * 获取四性验证结果
     *
     * @param tb_entry_index
     * @return
     */
    public Map<String, String> getFourSexVerify(Tb_entry_index tb_entry_index,String module,Tb_long_retention_setting setting,
                                                Tb_long_retention tb_long_retention,Boolean isWsNode) {
        //生成长期保管包
        String zippath = longRetention(tb_entry_index.getEntryid(), module,tb_entry_index,isWsNode);
        Map<String, String> map;
        if (setting != null) {
            List<String> settings = new ArrayList<>();
            if (!setting.getAuthenticity().isEmpty()) {
                settings.addAll(Arrays.asList(setting.getAuthenticity().split(",")));
            }
            if (!setting.getIntegrity().isEmpty()) {
                settings.addAll(Arrays.asList(setting.getIntegrity().split(",")));
            }
            if (!setting.getUsability().isEmpty()) {
                settings.addAll(Arrays.asList(setting.getUsability().split(",")));
            }
            if (!setting.getSafety().isEmpty()) {
                settings.addAll(Arrays.asList(setting.getSafety().split(",")));
            }
            map = fourSexVerifyImpl.getAllVerifyResult(zippath, settings,false);
        } else {
            map = fourSexVerifyImpl.getAllVerifyResult(zippath,false);
        }
        saveLongRetention(tb_entry_index.getEntryid(),map,"",tb_long_retention);
        //删除采集验证的zip,采集的验证包不用长期保存
        if ("capture".equals(module)) {//数据采集
            File file = new File(zippath);
            // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        }
        return map;
    }


    /**
     * 获取案卷的四性验证结果
     *
     * @param entryIndex
     * @return
     */
    public List<String> getVolumeFourSexVerify(Tb_entry_index entryIndex, String module, String nodeid,String volumeNodeId,Tb_long_retention_setting setting,
                                               Tb_long_retention tb_long_retention,List<String> ajCodeSettingFieldList,List<String> jnCodeSettingFieldList,
                                               Tb_long_retention_setting jnSetting,Boolean isWsNode) {
        //获取卷内条目
        List<Tb_index_detail_capture> captureList= entryIndexCaptureService.getLongInnerfiles(volumeNodeId,entryIndex,ajCodeSettingFieldList,jnCodeSettingFieldList);
        List<String> volumeEntryIds=new ArrayList<>();//卷内的entryid
        if(captureList.size()>0) {
            Map<String,Tb_long_retention> long_retentionMap=new HashMap<>();
            String[] strings = GainField.getFieldValues(captureList, "entryid");
            if(strings!=null) {
                List<Tb_long_retention> longRetentionList = longRetentionRepository.findByEntryidIn(strings);
                longRetentionList.stream().forEach(long_retention -> {
                    long_retentionMap.put(long_retention.getEntryid().trim(), long_retention);
                });
            }
            captureList.stream().forEach(capture->{
                volumeEntryIds.add(capture.getEntryid());
                Tb_entry_index entry_index = new Tb_entry_index();
                BeanUtils.copyProperties(capture, entry_index);
                //生成案卷的长期保管包
                generateLongTermPackage(entry_index,module,entryIndex.getEntryid(),jnSetting,long_retentionMap.get(capture.getEntryid().trim()),false,isWsNode);
            });
        }
        entryIndex.setResponsible("默认");//案卷不需要检测责任人
        //生成案卷的长期保管包
        generateLongTermPackage(entryIndex,module,"",setting,tb_long_retention,true,isWsNode);
        return volumeEntryIds;
    }

    //生成长期保管包
    public void generateLongTermPackage(Tb_entry_index tb_entry_index, String module,String parentEntryId,Tb_long_retention_setting setting,
                                        Tb_long_retention tb_long_retention,Boolean isVolume,Boolean isWsNode){
        String zippath = longRetention(tb_entry_index.getEntryid(), module,tb_entry_index,isWsNode);
        Map<String, String> map;
        if (setting != null) {
            List<String> settings = new ArrayList<>();
            if (!setting.getAuthenticity().isEmpty()) {
                settings.addAll(Arrays.asList(setting.getAuthenticity().split(",")));
            }
            if (!setting.getIntegrity().isEmpty()) {
                settings.addAll(Arrays.asList(setting.getIntegrity().split(",")));
            }
            if (!setting.getUsability().isEmpty()) {
                settings.addAll(Arrays.asList(setting.getUsability().split(",")));
            }
            if (!setting.getSafety().isEmpty()) {
                settings.addAll(Arrays.asList(setting.getSafety().split(",")));
            }
            map = fourSexVerifyImpl.getAllVerifyResult(zippath, settings,isVolume);
        } else {
            map = fourSexVerifyImpl.getAllVerifyResult(zippath,isVolume);
        }
        //删除采集验证的zip,采集的验证包不用长期保存
        if ("capture".equals(module)) {//数据采集
            File file = new File(zippath);
            // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        }
        saveLongRetention(tb_entry_index.getEntryid(),map,parentEntryId,tb_long_retention);
    }

    public Map<String, String> saveLongRetention (String entryid,Map<String, String> map,String parentEntryId,Tb_long_retention tb_long_retention){
        if (map.get("authenticity").indexOf("不通过") > 0 || map.get("integrity").indexOf("不通过") > 0 || map.get("usability").indexOf("不通过") > 0 || map.get("safety").indexOf("不通过") > 0) {
            map.put("checkstatus", "<span style=\"color:red\">不通过</span>");
        } else {
            map.put("checkstatus", "<span style=\"color:green\">通过</span>");
        }
        if (tb_long_retention != null) {
            tb_long_retention.setCheckstatus(map.get("checkstatus"));
            tb_long_retention.setAuthenticity(map.get("authenticity"));
            tb_long_retention.setIntegrity(map.get("integrity"));
            tb_long_retention.setUsability(map.get("usability"));
            tb_long_retention.setSafety(map.get("safety"));
        } else {
            tb_long_retention = new Tb_long_retention();
            tb_long_retention.setEntryid(entryid);
            tb_long_retention.setCheckstatus(map.get("checkstatus"));
            tb_long_retention.setAuthenticity(map.get("authenticity"));
            tb_long_retention.setIntegrity(map.get("integrity"));
            tb_long_retention.setUsability(map.get("usability"));
            tb_long_retention.setSafety(map.get("safety"));
            tb_long_retention.setParententryid(parentEntryId);
        }
        longRetentionRepository.save(tb_long_retention);
        return map;
    }

    /**
     * 获取封装包的树结构
     *
     * @param entryid
     * @return
     */
    public List<FileTree> getVerifyPackage(String entryid) {
        Tb_entry_index index = entryIndexRepository.findByEntryid(entryid);
        String file = index.getEntryid().trim() + File.separator + index.getTitle() + ".zip";
        List<FileTree> trees = new ArrayList<>();
        trees = fourSexVerifyImpl.getFileTreesCharsetGBK(index.getTitle(), rootpath +
                File.separator + "longRetention" + File.separator + file);
        return trees;
    }

    /**
     * 获取xml元数据
     *
     * @param entryid
     * @param xmlName
     * @return
     */
    public Map<String, String> getMetadata(String entryid, String xmlName) {
        Tb_entry_index index = entryIndexRepository.findByEntryid(entryid);
        String file = index.getEntryid().trim() + File.separator + index.getTitle() + ".zip";
        return fourSexVerifyImpl.getXmlData(rootpath +
                File.separator + "longRetention" + File.separator + file, xmlName);
    }

    public String longRetention(String entryid, String module,Tb_entry_index index,Boolean isWsNode) {
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);           // 压缩方式
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);    // 压缩级别
        String titleStr;
        List<Tb_electronic> electronics = new ArrayList<>();
        List<Tb_electronic_solid> electronicSolids = new ArrayList<>();
        if ("capture".equals(module)) {//数据采集
            titleStr = index.getEntryid();
        } else {//数据管理
            titleStr = index.getEntryid();
        }
        //创建document文件夹
        String temporaryFile = rootpath + File.separator + "longRetention" + File.separator + entryid.trim() + File.separator + titleStr;
        //创建临时文件夹
        File eleDir = new File(temporaryFile);
        if (!eleDir.exists()) {
            eleDir.mkdirs();
        }
        if(!isWsNode){//非已归文书档案只验证：题名、档号、文件时间、电子文件
            index.setFilingyear("2020");
            index.setEntryretention("10");
            index.setResponsible("默认");
            index.setFiledate("2020");
        }
        //生成数据包xml
        createLongRetentionXML(temporaryFile, index, electronicSolids, electronics);
        zip(temporaryFile.replaceAll("/", "\\\\"), temporaryFile.replaceAll("/", "\\\\") + ".zip");//压缩
        //删除临时文件夹
        FileUtil.delFolder(eleDir.getPath());
        return temporaryFile + ".zip";
    }

    public String longRetention(String entryid, String module) {
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);           // 压缩方式
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);    // 压缩级别
        String titleStr;
        List<Tb_electronic> electronics = new ArrayList<>();
        List<Tb_electronic_solid> electronicSolids = new ArrayList<>();
        Tb_entry_index index = new Tb_entry_index();
        if ("capture".equals(module)) {//数据采集
            Tb_entry_index_capture indexCapture = entryIndexCaptureRepository.findByEntryid(entryid);
            BeanUtils.copyProperties(indexCapture, index);
            List<Tb_electronic_capture> electronicsCapture = electronicCaptureRepository.findByEntryid(entryid);
            for (Tb_electronic_capture electronicCapture : electronicsCapture) {
                Tb_electronic electronic = new Tb_electronic();
                BeanUtils.copyProperties(electronicCapture, electronic);
                electronics.add(electronic);
            }
            titleStr = index.getTitle();
        } else {//数据管理
            index = entryIndexRepository.findByEntryid(entryid);
            electronics = electronicRepository.findByEntryid(entryid);
            titleStr = index.getTitle();
        }
        if(titleStr!=null&&!"".equals(titleStr)) {
            //替换文件名不能出现的特殊符号
            titleStr = titleStr.replaceAll("<", "(");
            titleStr = titleStr.replaceAll(">", ")");
            titleStr = titleStr.replaceAll(":", "：");
            titleStr = titleStr.replaceAll("\"", "\\“");
            titleStr = titleStr.replaceAll("\"", "\\”");
            titleStr = titleStr.replaceAll("、", ".");
            titleStr = titleStr.replaceAll("\\?", "？");
        }
        electronicSolids = electronicSolidRepository.findByEntryid(entryid);
        //创建document文件夹
        String temporaryFile = rootpath + File.separator + "longRetention" + File.separator + entryid.trim() + File.separator + titleStr;
        //创建临时文件夹
        File eleDir = new File(temporaryFile);
        if (!eleDir.exists()) {
            eleDir.mkdirs();
        }
        copyFile(temporaryFile, electronicSolids, electronics);
        //生成数据包xml
        createLongRetentionXML(temporaryFile, index, electronicSolids, electronics);
        zip(temporaryFile.replaceAll("/", "\\\\"), temporaryFile.replaceAll("/", "\\\\") + ".zip");//压缩
        //删除临时文件夹
        FileUtil.delFolder(eleDir.getPath());
        return temporaryFile + ".zip";
    }

    /**
     * 复制文件到document目录下
     */
    private void copyFile(String temporaryFile, List<Tb_electronic_solid> electronicSolids,
                          List<Tb_electronic> electronics) {
        String desPath = temporaryFile + File.separator + "document";
        File file = new File(desPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (electronicSolids.size() > 0) {
            electronicSolids.forEach(electronicSolid -> {
                File src =
                        new File(rootpath + electronicSolid.getFilepath() + File.separator + electronicSolid.getFilename());
                //目标文件
                String electronicPath = desPath + File.separator + src.getName();
                File des = new File(electronicPath);
                try {
                    FileUtils.copyFile(src, des);//拷贝电子文件
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            electronics.forEach(electronic -> {
                File src =
                        new File(rootpath + electronic.getFilepath() + File.separator + electronic.getFilename());
                //目标文件
                String electronicPath = desPath + File.separator + src.getName();
                File des = new File(electronicPath);
                try {
                    FileUtils.copyFile(src, des);//拷贝电子文件
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void zip(String src, String dest) {
        File srcFile = new File(src);
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);           // 压缩方式
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);    // 压缩级别
        try {
            //删除原来压缩包
            ZipUtils.del(dest);
            ZipFile zipFile = new ZipFile(dest);
            zipFile.setFileNameCharset("GBK");
            File[] subFiles = srcFile.listFiles();
            if (subFiles != null) {
                for (File file : subFiles) {
                    if (file.isDirectory()) {
                        zipFile.addFolder(file, parameters);
                    } else {
                        zipFile.addFile(file, parameters);
                    }
                }
            }
        } catch (ZipException | IOException e) {
            e.printStackTrace();
        }
    }

    public void createLongRetentionXML(String temporaryFile, Tb_entry_index entryIndex, List<Tb_electronic_solid> electronicSolids, List<Tb_electronic> electronics) {
        Element root = DocumentHelper.createElement("soap:Envelope");
        Document doc = DocumentHelper.createDocument(root);

        root.addAttribute("xmlns:soap", "http://schemas.xmlsoap.org/soap/envelope/");
        //header标签
        Element header = DocumentHelper.createElement("soap:Header");
        root.add(header);

        //exHeader标签
        Element exHeader = header.addElement("exHeader");
        //服务地址
        Element serviceAddress = exHeader.addElement("服务地址");
        //安全信息
        Element securityInformation = exHeader.addElement("安全信息");
        //单位信息
        Element unitInformation = securityInformation.addElement("单位信息");
        //单位编码
        Element unitCoding = unitInformation.addElement("单位编码");
        //单位名称
        Element unitName = unitInformation.addElement("单位名称");
        //单位简称
        Element unitAbbreviation = unitInformation.addElement("单位简称");
        //下级单位列表
        Element listOfUnits = unitInformation.addElement("下级单位列表");
        //下级单位信息
        Element informationOfUnits = listOfUnits.addElement("下级单位信息");
        //下级单位编码
        Element codeOfCompany = informationOfUnits.addElement("下级单位编码");
        //下级单位名称
        Element nameOfCompany = informationOfUnits.addElement("下级单位名称");
        //用户信息
        Element userInformation = securityInformation.addElement("用户信息");
        //报文类别
        Element messageClass = exHeader.addElement("报文类别");
        messageClass.setText("00");

        Element body = DocumentHelper.createElement("soap:body");
        root.add(body);

        Element exchangeMessage = body.addElement("ExchangeMessage");
        Element exBody = exchangeMessage.addElement("exBody");
        Element message = exBody.addElement("报文");

        //报文头
        Element messageHeader = message.addElement("报文头");
        messageHeader.addAttribute("报文时戳", "");
        messageHeader.addAttribute("报文标识", "");

        //报文创建方
        Element messageCreator = messageHeader.addElement("报文创建方");
        //身份标识
        Element creatorIdentity = messageCreator.addElement("身份标识");
        creatorIdentity.addAttribute("身份标识类型", "");

        //报文发送方
        Element messagesender = messageHeader.addElement("报文发送方");
        //身份标识
        Element senderIdentity = messagesender.addElement("身份标识");
        senderIdentity.addAttribute("身份标识类型", "");

        //报文目的地
        Element messageDestination = messageHeader.addElement("报文目的地");
        //身份标识
        Element destinationIdentity = messageDestination.addElement("身份标识");
        destinationIdentity.addAttribute("身份标识类型", "");

        //报文路由
        Element messageRouting = messageHeader.addElement("报文路由");
        //路由项
        Element routingItem = messageRouting.addElement("路由项");
        //路由时间
        Element routingTime = routingItem.addElement("路由时间");
        //身份标识
        Element routingIdentity = routingItem.addElement("身份标识");
        routingIdentity.addAttribute("身份标识类型", "");
        //路由项说明
        Element routingItemDescription = routingItem.addElement("路由项说明");
        //短信通知
        Element sms = messageHeader.addElement("短信通知");

        //报文体
        Element messageStyle = message.addElement("报文体");
        //报文类别
        Element messageStyleClass = messageStyle.addElement("报文类别");
        messageStyleClass.setText("收文");
        //报文类别名称
        Element messageCategoryName = messageStyle.addElement("报文类别名称");
        messageCategoryName.setText("正文");

        //电子文件数据报文体
        Element electronicDocumentData = messageStyle.addElement("电子文件数据报文体");
        //保护描述信息
        Element protectionDescriptionInfor = electronicDocumentData.addElement("保护描述信息");

        //参考信息
        Element referenceInformation = protectionDescriptionInfor.addElement("参考信息");
        //标识信息
        Element identificationInformation = referenceInformation.addElement("标识信息");
        //标识码
        Element identificationCode = identificationInformation.addElement("标识码");
        //分类号
        Element classificationNumber = identificationInformation.addElement("分类号");
        //档案馆代号
        Element codeOfArchives = identificationInformation.addElement("档案馆代号");
        //缩微号
        Element microfilm = identificationInformation.addElement("缩微号");

        //内容描述
        Element contentdescription = referenceInformation.addElement("内容描述");
        //题名
        Element title = contentdescription.addElement("题名");
        title.setText(entryIndex.getTitle() != null ? entryIndex.getTitle() : "");
        //并列题名
        Element parallelTitle = contentdescription.addElement("并列题名");
        //副题名及说明题名文字
        Element subtitle = contentdescription.addElement("副题名及说明题名文字");
        //文件流水号
        Element serial = contentdescription.addElement("文件流水号");
        serial.setText(entryIndex.getSerial() != null ? entryIndex.getSerial() : "");
        //文件编号
        Element filenumber = contentdescription.addElement("文件编号");
        filenumber.setText(entryIndex.getFilenumber() != null ? entryIndex.getFilenumber() : "");
        //文件形成单位名称
        Element documentFormingUnit = contentdescription.addElement("文件形成单位名称");
        //附件
        Element enclosure = contentdescription.addElement("附件");
        //稿本
        Element manuscript = contentdescription.addElement("稿本");
        //文种
        Element wenzhong = contentdescription.addElement("文种");
        //归档年度
        Element filingyear = contentdescription.addElement("归档年度");
        filingyear.setText(entryIndex.getFilingyear() != null ? entryIndex.getFilingyear() : "");
        //文件日期
        Element filedate = contentdescription.addElement("文件日期");
        filedate.setText(entryIndex.getFiledate() != null ? entryIndex.getFiledate() : "");
        //页数
        Element pages = contentdescription.addElement("页数");
        pages.setText(entryIndex.getPages() != null ? entryIndex.getPages() : "");
        //份数
        Element fscount = contentdescription.addElement("份数");
        fscount.setText(entryIndex.getFscount() != null ? entryIndex.getFscount() : "");
        //录入人
        Element descriptionuser = contentdescription.addElement("录入人");
        descriptionuser.setText(entryIndex.getDescriptionuser() != null ? entryIndex.getDescriptionuser() : "");
        //录入时间
        Element descriptiondate = contentdescription.addElement("录入时间");
        descriptiondate.setText(entryIndex.getDescriptiondate() != null ? entryIndex.getDescriptiondate() : "");
        //附注
        Element noteAppended = contentdescription.addElement("附注");
        //提要
        Element summary = contentdescription.addElement("提要");
        //主题或关键词
        Element keyword = contentdescription.addElement("主题或关键词");
        keyword.setText(entryIndex.getKeyword() != null ? entryIndex.getKeyword() : "");
        //语种
        Element languages = contentdescription.addElement("语种");
        //责任者
        Element responsible = contentdescription.addElement("责任者");
        responsible.setText(entryIndex.getResponsible() != null ? entryIndex.getResponsible() : "");
        //开放审核
        Element openAuditing = contentdescription.addElement("开放审核");
        //内容覆盖范围
        Element contentCoverage = contentdescription.addElement("内容覆盖范围");
        //文件格式
        Element fileFormat = contentdescription.addElement("文件格式");

        //参考扩展信息
        Element referenceExtensionInformation = referenceInformation.addElement("参考扩展信息");
        //紧急程度
        Element emergencyLevel = referenceExtensionInformation.addElement("紧急程度");
        //发文字号
        Element issuedNumber = referenceExtensionInformation.addElement("发文字号");
        //签发人
        Element issuer = referenceExtensionInformation.addElement("签发人");
        //会签人
        Element countersigner = referenceExtensionInformation.addElement("会签人");
        //主送机关
        Element mainSendingOrgan = referenceExtensionInformation.addElement("主送机关");
        //抄送机关
        Element copyOffice = referenceExtensionInformation.addElement("抄送机关");
        //印发机关
        Element issuingOrgan = referenceExtensionInformation.addElement("印发机关");
        //印发日期
        Element dateOfIssue = referenceExtensionInformation.addElement("印发日期");

        //背景信息
        Element backgroundInformation = protectionDescriptionInfor.addElement("背景信息");
        //创建依据
        Element createBasis = backgroundInformation.addElement("创建依据");
        //法律法规依据
        Element basisOfLawsAndRegulations = createBasis.addElement("法律法规依据");
        //行政行业依据
        Element basisOfAdministrativeIndustry = createBasis.addElement("行政行业依据");
        //创建原因
        Element createReasons = createBasis.addElement("创建原因");
        //全宗号
        Element funds = createBasis.addElement("全宗号");
        funds.setText(entryIndex.getFunds() != null ? entryIndex.getFunds() : "");
        //全宗名称
        Element fundsName = createBasis.addElement("全宗名称");
        //全宗类型
        Element fundsType = createBasis.addElement("全宗类型");
        //全宗范围及沿革
        Element fundsEvolution = createBasis.addElement("全宗范围及沿革");
        //组织沿革
        Element administrativeHistory = createBasis.addElement("组织沿革");

        //关联信息
        Element relatedInformation = backgroundInformation.addElement("关联信息");
        //关联描述
        Element relationalDescription = relatedInformation.addElement("关联描述");
        //关联类型
        Element associationType = relatedInformation.addElement("关联类型");
        //关联文件
        Element associatedFile = relatedInformation.addElement("关联文件");

        //原始技术环境
        Element originalTechnologyEnvironment = backgroundInformation.addElement("原始技术环境");
        //原始信息系统描述
        Element originalInformationSystem = originalTechnologyEnvironment.addElement("原始信息系统描述");
        //原始档案管理信息系统描述
        Element descriptionOfOriginalArchives = originalTechnologyEnvironment.addElement("原始档案管理信息系统描述");
        //文件捕获功能描述
        Element fileFunctionDescription = originalTechnologyEnvironment.addElement("文件捕获功能描述");
        //背景扩展信息
        Element backgroundExtensionInformation = backgroundInformation.addElement("背景扩展信息");

        //来源信息
        Element aourceInformation = protectionDescriptionInfor.addElement("来源信息");
        //形成过程
        Element formationProcess = aourceInformation.addElement("形成过程");
        //拟稿人
        Element draftAuthor = formationProcess.addElement("拟稿人");
        //拟稿时间
        Element draftTime = formationProcess.addElement("拟稿时间");
        //审核人
        Element auditor = formationProcess.addElement("审核人");
        //审核时间
        Element auditTime = formationProcess.addElement("审核时间");
        //签发时间
        Element timeFiled = formationProcess.addElement("签发时间");
        //原始文件格式
        Element originalFileFormat = formationProcess.addElement("原始文件格式");
        //原始文件大小
        Element originalFileSize = formationProcess.addElement("原始文件大小");

        //处理信息
        Element processInformation = aourceInformation.addElement("处理信息");
        //处理类型
        Element processingType = processInformation.addElement("处理类型");
        //处理人
        Element dealingWithPeople = processInformation.addElement("处理人");
        //接收处理时间
        Element receiveProcessingTime = processInformation.addElement("接收处理时间");
        //实际处理时间
        Element actualProcessingTime = processInformation.addElement("实际处理时间");
        //处理后发出时间
        Element issueTimeAfterProcessing = processInformation.addElement("处理后发出时间");
        //处理意见
        Element handlingOpinions = processInformation.addElement("处理意见");

        //数字资源加工
        Element digitalResourceProcessing = aourceInformation.addElement("数字资源加工");
        //数字资源制作者
        Element digitalResourcProducer = digitalResourceProcessing.addElement("数字资源制作者");
        //数字资源制日期
        Element dateOfDigitalResourceSystem = digitalResourceProcessing.addElement("数字资源制日期");
        //数字资源制作地
        Element digitalProduced = digitalResourceProcessing.addElement("数字资源制作地");

        //来源扩展信息
        Element sourceExtensionInformation = aourceInformation.addElement("来源扩展信息");

        //管理信息
        Element managementInformation = protectionDescriptionInfor.addElement("管理信息");
        //鉴定信息
        Element identificationInfor = managementInformation.addElement("鉴定信息");
        //鉴定类型
        Element identificationType = identificationInfor.addElement("鉴定类型");
        //鉴定时间
        Element identificationTime = identificationInfor.addElement("鉴定时间");
        //鉴定人
        Element appraiser = identificationInfor.addElement("鉴定人");
        //鉴定意见
        Element appraisalOpinion = identificationInfor.addElement("鉴定意见");

        //归档信息
        Element archivedInformation = managementInformation.addElement("归档信息");
        //归档时间
        Element filingTime = managementInformation.addElement("归档时间");
        //归档方式
        Element filingMode = managementInformation.addElement("归档方式");
        //归档类型
        Element archivedType = managementInformation.addElement("归档类型");
        //移交责任者
        Element transferResponsible = managementInformation.addElement("移交责任者");
        //接收责任者
        Element receivingResponsible = managementInformation.addElement("接收责任者");
        //归档文件格式
        Element archiveFileFormat = managementInformation.addElement("归档文件格式");
        //归档文件大小
        Element archivesize = managementInformation.addElement("归档文件大小");
        //档案归档标识
        Element archiveidentification = managementInformation.addElement("档案归档标识");
        //档号
        Element archivecode = managementInformation.addElement("档号");
        archivecode.setText(entryIndex.getArchivecode() != null ? entryIndex.getArchivecode() : "");

        //权限管理
        Element privilegeManagement = managementInformation.addElement("权限管理");
        //密级
        Element entrysecurity = privilegeManagement.addElement("密级");
        entrysecurity.setText(entryIndex.getEntrysecurity() != null ? entryIndex.getEntrysecurity() : "");
        //解密期限
        Element decryptionPeriod = privilegeManagement.addElement("解密期限");
        //保管期限
        Element entryretention = privilegeManagement.addElement("保管期限");
        entryretention.setText(entryIndex.getEntryretention() != null ? entryIndex.getEntryretention() : "");
        //版权说明
        Element copyrightNotice = privilegeManagement.addElement("版权说明");
        //授权对象
        Element authorizedObject = privilegeManagement.addElement("授权对象");
        //授权行为
        Element delegationBehavior = privilegeManagement.addElement("授权行为");

        //维护历史
        Element maintainHistory = managementInformation.addElement("维护历史");
        //处置类型
        Element dispositionType = maintainHistory.addElement("处置类型");
        //处置责任者
        Element personResponsible = maintainHistory.addElement("处置责任者");
        //处置时间
        Element disposalTime = maintainHistory.addElement("处置时间");
        //处置依据
        Element disposalBasis = maintainHistory.addElement("处置依据");
        //处置过程
        Element disposalProcess = maintainHistory.addElement("处置过程");
        //处置结果
        Element dispositionResult = maintainHistory.addElement("处置结果");

        //管理扩展信息
        Element manageExtendedInformation = managementInformation.addElement("管理扩展信息");

        //固化信息
        Element curingInformation = protectionDescriptionInfor.addElement("固化信息");
        //数字签名
        Element digitalSignature = curingInformation.addElement("数字签名");
        //真实性指示符
        Element authenticationIndicator = curingInformation.addElement("真实性指示符");

        //电子文件数据
        Element documentData = electronicDocumentData.addElement("电子文件数据");
        //正文文件数据
        Element bodyFileData = documentData.addElement("正文文件数据");
        bodyFileData.addAttribute("文件类型", "ceb");
        bodyFileData.addAttribute("印章个数", "0");
        if (electronicSolids.size() > 0) {
            electronicSolids.forEach(electronicSolid -> {
                Element file = bodyFileData.addElement("文件");
                file.addAttribute("文件大小单位", "byte");
                file.addAttribute("文件大小", electronicSolid.getFilesize());
                file.addAttribute("文件名称", electronicSolid.getFilename());
                file.addAttribute("MIME类型", "String");
                Element data = file.addElement("数据");
                data.setText("");
            });
        } else if(electronics.size()>0){
            electronics.forEach(electronic -> {
                Element file = bodyFileData.addElement("文件");
                file.addAttribute("文件大小单位", "byte");
                file.addAttribute("文件大小", electronic.getFilesize());
                file.addAttribute("文件名称", electronic.getFilename());
                file.addAttribute("MIME类型", "String");
                Element data = file.addElement("数据");
                data.setText("");
            });
        }else {
            if(!"".equals(entryIndex.getEleid())&&entryIndex.getEleid()!=null){
                Element file = bodyFileData.addElement("文件");
                file.addAttribute("文件大小单位", "byte");
                file.addAttribute("文件名称", entryIndex.getTitle());
                file.addAttribute("MIME类型", "String");
                Element data = file.addElement("数据");
                data.setText("");
            }
        }

        String xmlPath = temporaryFile + File.separator + "meta.xml";
        File xmlFile = new File(xmlPath);
        try {
            // 设置XML文档格式
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            outputFormat.setEncoding("UTF-8");
            outputFormat.setIndent(true); //设置是否缩进
            outputFormat.setIndent("  "); //以两个空格方式实现缩进
            outputFormat.setNewlines(true); //设置是否换行
            XMLWriter writer = new XMLWriter(new FileOutputStream(xmlFile), outputFormat);
            writer.write(doc);
            writer.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ExtMsg checkFile(String entryid) {
        Tb_entry_index index = entryIndexRepository.findByEntryid(entryid);
        File file =
                new File(rootpath + File.separator + "longRetention" + File.separator + entryid.trim() + File.separator + index.getTitle() + ".zip");
        if (!file.exists()) {
            return new ExtMsg(false, "未检测到长期保管包，请执行验证操作！", "");
        }
        return new ExtMsg(true, "查找成功", "");
    }

    public Tb_long_retention_setting saveSetting(Tb_long_retention_setting setting) {
        return longRetentionSettingRepository.save(setting);
    }

    public Tb_long_retention_setting getSetting(String nodeid) {
        return longRetentionSettingRepository.findByNodeid(nodeid);
    }



    /**
     * 获取四性验证结果
     *
     * @param entry_index
     * @return
     */
    public Map<String, String> getOAFourSexVerify(Tb_entry_index entry_index, String module, String nodeid) {
        //Tb_entry_index index = entryIndexRepository.findByEntryid(entryid);
        //生成长期保管包
        String zippath = longRetention(entry_index.getEntryid(), module,entry_index,false);
        Map<String, String> map;
        Tb_long_retention_setting setting = longRetentionSettingRepository.findByNodeid(nodeid);
        if (setting != null) {
            List<String> settings = new ArrayList<>();
            if (!setting.getAuthenticity().isEmpty()) {
                settings.addAll(Arrays.asList(setting.getAuthenticity().split(",")));
            }
            if (!setting.getIntegrity().isEmpty()) {
                settings.addAll(Arrays.asList(setting.getIntegrity().split(",")));
            }
            if (!setting.getUsability().isEmpty()) {
                settings.addAll(Arrays.asList(setting.getUsability().split(",")));
            }
            if (!setting.getSafety().isEmpty()) {
                settings.addAll(Arrays.asList(setting.getSafety().split(",")));
            }
            map = fourSexVerifyImpl.getAllVerifyResult(zippath, settings,false);
        } else {
            map = fourSexVerifyImpl.getAllVerifyResult(zippath,true);
        }
        if (map.get("authenticity").indexOf("不通过") > 0 || map.get("integrity").indexOf("不通过") > 0 || map.get("usability").indexOf("不通过") > 0 || map.get("safety").indexOf("不通过") > 0) {
            map.put("checkstatus", "<span style=\"color:red\">不通过</span>");
        } else {
            map.put("checkstatus", "<span style=\"color:green\">通过</span>");
        }
        Tb_long_retention tb_long_retention = longRetentionRepository.findByEntryid(entry_index.getEntryid());
        if (tb_long_retention != null) {
            tb_long_retention.setCheckstatus(map.get("checkstatus"));
            tb_long_retention.setAuthenticity(map.get("authenticity"));
            tb_long_retention.setIntegrity(map.get("integrity"));
            tb_long_retention.setUsability(map.get("usability"));
            tb_long_retention.setSafety(map.get("safety"));
        } else {
            tb_long_retention = new Tb_long_retention();
            tb_long_retention.setEntryid(entry_index.getEntryid());
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
        return map;
    }

    public static void main(String[] args) throws Exception {
        FileUtils.copyFileToDirectory(new File("d:\\1\\sip.xml"),new File("d:\\1\\4\\"));
    }
}
