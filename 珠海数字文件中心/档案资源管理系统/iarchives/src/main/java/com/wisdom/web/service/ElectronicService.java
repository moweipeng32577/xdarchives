package com.wisdom.web.service;

//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.wisdom.secondaryDataSource.entity.Tb_electronic_browse_sx;
import com.wisdom.secondaryDataSource.entity.Tb_electronic_sx;
import com.wisdom.secondaryDataSource.repository.SxElectronicBrowseRepository;
import com.wisdom.secondaryDataSource.repository.SxElectronicRepository;
import com.wisdom.util.*;
import com.wisdom.web.controller.FullSearchController;
import com.wisdom.web.entity.*;
import com.wisdom.web.entity.Tb_supervision_electronic;
import com.wisdom.web.entity.sip.Sip;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.pdfbox.pdmodel.PDDocument;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDirectory;
import sun.misc.Unsafe;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import static com.wisdom.web.service.ThematicService.delFolder;
import static java.lang.Integer.parseInt;

/**
 * Created by Rong on 2017/11/17.
 */
@Service
@Transactional
public class ElectronicService {

    @Autowired
    ElectronicCaptureRepository electronicCaptureRepository;

    @Autowired
    ElectronicSolidRepository electronicSolidRepository;

    @Autowired
    ElectronicLongRepository electronicLongRepository;

    @Autowired
    ElectronicRepository electronicRepository;

    @Autowired
    EntryCaptureService entryCaptureService;

    @Autowired
    EntryService entryService;

    @Autowired
    ThematicDetailRepository thematicDetailRepository;

    @Autowired
    FocusRepository focusRepository;

    @Autowired
    PersonalizedRepository personalizedRepository;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    EntryIndexCaptureRepository entryIndexCaptureRepository;

    @Autowired
    EntryIndexCaptureService entryIndexCaptureService;

    @Autowired
    private MediaCompressionService mediaCompressionService;


    @Autowired
    ExchangeService exchangeService;

    @Autowired
    CodesetRepository codesetRepository;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    ElectronicRecyclebinRepository electronicRecyclebinRepository;

    @Autowired
    ThematicRepository thematicRepository;

    @Autowired
    ThematicMakeRepository thematicMakeRepository;

    @Autowired
    ElectronicVersionRepository electronicVersionRepository;

    @Autowired
    ElectronicVersionCaptureRepository electronicVersionCaptureRepository;

    @Autowired
    BorrowDocRepository borrowDocRepository;

    @Autowired
    SzhElectronicCaptureRepository szhElectronicCaptureRepository;

    @Autowired
    DataNodeExtRepository dataNodeExtRepository;

    @Autowired
    ElectronicBrowseRepository electronicBrowseRepository;

    @Autowired
    ThumbnailRepository thumbnailRepository;

    @Autowired
    EntryDetailCaptureRepository entryDetailCaptureRepository;

    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    WatermarkService watermarkService;

    @Autowired
    EntryDetailRepository entryDetailRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    WatermarkUtil watermarkUtil;

    @Autowired
    SxElectronicRepository sxElectronicRepository;

    @Autowired
    SxElectronicBrowseRepository sxElectronicBrowseRepository;

    @Autowired
    FullSearchController fullSearchController;

    @Autowired
    FullTextRepository fullTextRepository;

    @Autowired
    SupervisionElectronicRepository supervisionElectronicRepository;

    @Value("${system.nginx.browse.path}") // 浏览文件路径
    private String browsepath;

    @Value("${system.support.solidify.type}")
    private String solidifyType;//oa文件根目录


    @Value("${compression.tool.path}")//压缩工具路径
    private String compressionToolPath;

    private static long chunkSize = 5242880;//文件分片大小5M
    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录
    @Value("${system.report.server}")
    private String reportServer;//报表服务
    @Value("${system.document.reportFullDir}")
    private String reportFullDir;//系统报表文件存储目录（FReport）
    @Value("${system.document.UReportFullDir}")
    private String UReportFullDir;//系统报表文件存储目录(UReport)
    private String uploaddir;//电子文件上传路径
    private String eledir;//电子文件存储路径
    private static CopyOnWriteArrayList<Tb_electronic> tb_electList;
    private static Integer mainStatus = 0;
    private volatile static Integer finshNum = 0;
    private static Map<String, String> resultMap;
    private static Date lastCheckDate;

    private String getUploadDir() {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        uploaddir = rootpath + "/electronics/uploads/" + userDetails.getUsername();

        File upDir = new File(uploaddir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }
        return uploaddir;
    }

    private String getWatermarkDir() {
        if (eledir == null || "".equals(eledir)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            eledir = rootpath + "/electronics/watermark/" + cal.get(Calendar.YEAR) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DATE);

            File eleDir = new File(eledir);
            if (!eleDir.exists()) {
                eleDir.mkdirs();
            }
        }
        return eledir;
    }

    private String getUploadDirFocus() {
        String dir = "";
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dir = rootpath + "/electronicsFocus/uploads/" + userDetails.getLoginname();
        File upDir = new File(dir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }

        return dir;
    }

    private String getUploadDirUserimg() {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String dir = rootpath + "/userimg/uploads/" + userDetails.getLoginname();
        File upDir = new File(dir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }
        return dir;
    }

    private String getUploadDirSips() {
        String dir = "";
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dir = rootpath + "/electronicsSips/uploads/" + userDetails.getUsername();
        File upDir = new File(dir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }
        return dir;
    }

    //专题电子文件存储存放路径
    public String getUploadDirThematic(String thematicdetilid) {
        String dir = "";
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dir = rootpath + "/thematic/prod/" + userDetails.getUsername()+"/"+thematicdetilid.trim();
        File upDir = new File(dir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }
        return dir;
    }

    //专题电子文件临时存放路径
    public String getTemporaryThematic() {
        String dir = "";
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dir = rootpath + "/thematic/temporary/" + userDetails.getUsername();
        File upDir = new File(dir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }
        return dir;
    }

    //公告富文本电子文件临时存放路径
    public String getTemporaryInform() {
        Calendar cal = Calendar.getInstance();
        String dir = "";
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dir = "/inform/temporary/" + userDetails.getUsername()+ File.separator +cal.get(Calendar.YEAR) + File.separator +
                (cal.get(Calendar.MONTH) + 1) + File.separator + cal.get(Calendar.DATE) ;
        return dir;
    }

    //展厅富文本电子文件临时存放路径
    public String getTemporaryShowroom() {
        Calendar cal = Calendar.getInstance();
        String dir = "";
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dir = "/showroom/temporary/" + userDetails.getUsername()+ File.separator +cal.get(Calendar.YEAR) + File.separator +
                (cal.get(Calendar.MONTH) + 1) + File.separator + cal.get(Calendar.DATE) ;
        return dir;
    }

    private String getUploadDirReport() {
        if(reportServer.equals("FReport")) {
            File upDir = new File(reportFullDir);
            if (!upDir.exists()) {
                upDir.mkdirs();
            }
            return reportFullDir;
        }
        else if(reportServer.equals("UReport")) {
            File upDir = new File(UReportFullDir);
            if (!upDir.exists()) {
                upDir.mkdirs();
            }
            return UReportFullDir;
        }
        return "";
    }

    public String getScanBaseDir(String batchname, String archivecode){
        return "/scan" + File.separator + batchname + File.separator + archivecode;
    }

    public String getScanStorageDir(String batchname, String archivecode){
        String dir = rootpath + File.separator + "scan" + File.separator + batchname + File.separator + archivecode;
        File eleDir = new File(dir);
        if(!eleDir.exists()){
            eleDir.mkdirs();
        }
        return dir;
    }

    private String getStorageBaseDir() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return "/electronics/storages/" + cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE);
    }

    //点击保存后的电子文件存放路径(electronics/storages/年/月/日/类型(capture-采集、management-数据管理)/条目ID)
    public String getStorageBaseDir(String entrytype,String entryid) {
        Calendar cal = Calendar.getInstance();
        return "/electronics/storages/"  + cal.get(Calendar.YEAR) + "/" +
                (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE) + "/" + entrytype+"/"+entryid.trim();
    }

    //数据采集、数据管理临时存储的文件夹(electronics/temporaryStorages/年/月/日/类型(capture-采集、management-数据管理)/用户ID)
    private String getTemporaryStoragesBaseDir(String entrytype) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Calendar cal = Calendar.getInstance();
        return "/electronics/temporaryStorages/" + cal.get(Calendar.YEAR) + "/" +
                (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE) + "/" + entrytype + "/" + userDetails.getUserid()
                .trim();
    }

    //回收站的文件路径（electronics/storages/年/月/日/类型(capture-采集、management-数据管理)/条目ID/时间戳）
    private String getRecyclebinBaseDir(String entrytype, String entryid) {
        Calendar cal = Calendar.getInstance();
        return "/electronics/recyclebinElectronic/" + cal.get(Calendar.YEAR) + "/" +
                (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE) + "/" +
                entrytype + "/" + entryid.trim() + "/" + cal.getTimeInMillis();
    }

    private String getStorageUserimgBaseDir() {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return "/userimg/storages/" + userDetails.getLoginname();
    }

    private String getStorageUserimgDir() {
        String storageUserimgDir = rootpath + getStorageUserimgBaseDir();
        File storageUserimgDirFile = new File(storageUserimgDir);
        if (!storageUserimgDirFile.exists()) {
            storageUserimgDirFile.mkdirs();
        }
        return storageUserimgDir;
    }

    public String getStorageDir(String entrytype,String entryid) {
        eledir = rootpath + getStorageBaseDir(entrytype,entryid);
        File eleDir = new File(eledir);
        if (!eleDir.exists()) {
            eleDir.mkdirs();
        }
        return eledir;
    }

    private String getStorageDir() {
        if (eledir == null || "".equals(eledir)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            eledir = rootpath + getStorageBaseDir();
            File eleDir = new File(eledir);
            if (!eleDir.exists()) {
                eleDir.mkdirs();
            }
        }
        return eledir;
    }

    //年 + 月 + 日 + 类型(capture-采集、management-数据管理) + + 用户ID 路径的临时文件夹
    private String getEntrytypeStorageDir(String entrytype) {
        String eledir = rootpath + getTemporaryStoragesBaseDir(entrytype);
        File eleDir = new File(eledir);
        if (!eleDir.exists()) {
            eleDir.mkdirs();
        }
        return eledir;
    }

    public boolean checkchunk(String filename, int chunks, int chunk) throws Exception {
        File confFile = new File(getUploadDir(), filename + ".conf");
        if (!confFile.exists()) {
            return false;
        }

        RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "r");
        accessConfFile.seek(chunk);
        byte b = accessConfFile.readByte();
        accessConfFile.close();

        if (b == Byte.MAX_VALUE) {
            return true;
        } else {
            return false;
        }
    }

    public void delchunk(String filename) {
        File confFile = new File(getUploadDir(), filename + ".conf");
        if (confFile.exists()) {
            confFile.delete();
        }
    }

    public void uploadfile(Map<String, Object> param,String entrytype,String entryid) throws Exception {
        String targetFileName = (String) param.get("filename");
        File tmpFile = new File(getUploadDir(), targetFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        //写入数据
        accessTmpFile.write((byte[]) param.get("content"));
        accessTmpFile.close();
        if("capture".equals(entrytype)&&entryid!=null){
            List<Tb_electronic_capture> electronic_captures = electronicCaptureRepository.findByEntryidAndFilename(entryid,(String) param
                    .get("filename"));
            if(electronic_captures.size()>0){
                Tb_electronic_capture electronic_capture = electronic_captures.get(0);
                saveTVersion(electronic_capture.getFilepath(),electronic_capture.getFilename(),entrytype,entryid); //把同名原始文件存放到临时路径下
            }
        }else if("management".equals(entrytype)&&entryid!=null){
            List<Tb_electronic> electronics = electronicRepository.findByEntryidAndFilename(entryid,(String) param
                    .get("filename"));
            if(electronics.size()>0){
                Tb_electronic electronic = electronics.get(0);
                saveTVersion(electronic.getFilepath(),electronic.getFilename(),entrytype,entryid); //把同名原始文件存放到临时路径下
            }
        }
        //存在entryid时，直接转存到entryid目录下，否则，转存到临时目录
        File targetFile = new File("".equals(entryid) ? getEntrytypeStorageDir(entrytype) : getStorageDir(
                entrytype,entryid),
                (String) param.get("filename"));
        //原来存在文件的话，先删除，后转存
        if(targetFile.exists()){
            targetFile.delete();
        }
        tmpFile.renameTo(targetFile);
    }

    public void uploadfileVersion(Map<String, Object> param) throws Exception {
        String targetFileName = (String) param.get("filename");
        File tmpFile = new File(getUploadDir(), targetFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        //写入数据
        accessTmpFile.write((byte[]) param.get("content"));
        accessTmpFile.close();
        //存在entryid时，直接转存到entryid目录下，否则，转存到临时目录
        File targetFile = new File(getUploadDirSaveEle(),(String) param.get("filename"));
        //原来存在文件的话，先删除，后转存
        if(targetFile.exists()){
            targetFile.delete();
        }
        tmpFile.renameTo(targetFile);
    }

    public void uploadUserimg(Map<String, Object> param) throws Exception {
        String targetFileName = (String) param.get("filename");
        File tmpFile = new File(getUploadDirUserimg(), targetFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        //写入数据
        accessTmpFile.write((byte[]) param.get("content"));
        accessTmpFile.close();
        //文件转存
        File targetFile = new File(getStorageUserimgDir(), targetFileName);
        tmpFile.renameTo(targetFile);
    }

    public void uploadSipFile(String entryid, Sip sip, ZipInputStream zipInputStream) {
        //记录上传文件的数目
        int fileCount = 0;
        List<String> fileNameList = new ArrayList<String>();
        for (com.wisdom.web.entity.sip.File file : sip.getFile()) {
            fileNameList.add(file.getFileName());
        }
        ZipEntry zipEntry;
        try {
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String zeName = zipEntry.getName().substring(zipEntry.getName().lastIndexOf("/") + 1);
                if (!"sip.xml".equals(zeName) && !"".equals(zeName) && fileNameList.contains(zeName)) {
                    File tmpFile = new File(getStorageBaseDir("capture",entryid), zeName);
                    RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
                    byte[] buf = new byte[1024];
                    int len = -1;
                    while ((len = zipInputStream.read(buf)) != -1) {  // 直到读到该条目的结尾
                        //写入数据
                        accessTmpFile.write(buf, 0, len);
                    }
                    Tb_electronic_capture elec = new Tb_electronic_capture();
                    elec.setEntryid(entryid);
                    elec.setFilename(zeName);
                    elec.setFilepath(getStorageBaseDir("capture",entryid));
                    elec.setFilesize(String.valueOf(accessTmpFile.length()));
                    elec.setFiletype(zeName.substring(zeName.lastIndexOf('.') + 1));
                    if (electronicCaptureRepository.save(elec) != null) {
                        fileCount++;
                    }
                    accessTmpFile.close();
                }
                zipInputStream.closeEntry();  //关闭该条目
            }
            zipInputStream.close();
            //若文件夹数目大于，更新采集条目的文件个数，以便在grid显示文件图标
            if (fileCount > 0) {
                entryIndexCaptureService.updateEleId(String.valueOf(fileCount), entryid);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 焦点图上传
     *
     * @param param 图片对象
     * @throws Exception
     */
    public void uploadfileFocus(Map<String, Object> param) throws Exception {
        String targetFileName = (String) param.get("filename");
        File tmpFile = new File(getUploadDirFocus(), targetFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        //写入数据
        accessTmpFile.write((byte[]) param.get("content"));
        accessTmpFile.close();

        Tb_focus focus = new Tb_focus();
        focus.setPath(tmpFile.getPath().substring(tmpFile.getPath().indexOf("electronicsFocus") - 1));
        focus.setTitle(tmpFile.getName().substring(0, tmpFile.getName().lastIndexOf(".")));
        focusRepository.save(focus);
    }

    /**
     * 专题制作上传
     *
     * @param param 图片对象
     * @throws Exception
     */
    public void uploadfileThematic(Map<String, Object> param) throws Exception {
        String targetFileName = (String) param.get("filename");
        File tmpFile = new File(getTemporaryThematic(), targetFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        //写入数据
        accessTmpFile.write((byte[]) param.get("content"));
        accessTmpFile.close();
    }

    /**
     * 个性头像上传
     *
     * @param param 图片对象
     * @throws Exception
     */
    public void uploadfileUserimg(Map<String, Object> param) throws Exception {
        String targetFileName = (String) param.get("filename");
        File tmpFile = new File(getUploadDirUserimg(), targetFileName);

        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        //写入数据
        accessTmpFile.write((byte[]) param.get("content"));
        accessTmpFile.close();

        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Tb_Personalized personalized = personalizedRepository.findByUserid(userDetails.getUserid());
        personalized.setPath(tmpFile.getPath().substring(tmpFile.getPath().indexOf("userimg") - 1));
        personalized.setTitle(tmpFile.getName().substring(0, tmpFile.getName().lastIndexOf(".")));
        personalizedRepository.save(personalized);
    }

    /**
     * 报表样式管理上传
     *
     * @param param
     * @throws Exception
     */
    public void uploadfileReport(Map<String, Object> param) throws Exception {
        String targetFileName = (String) param.get("filename");
        File tmpFile = new File(getUploadDirReport(), targetFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        //写入数据
        accessTmpFile.write((byte[]) param.get("content"));
        accessTmpFile.close();
    }

    public void uploadfileSips(Map<String, Object> param) throws Exception {
        String targetFileName = (String) param.get("filename");
        File tmpFile = new File(getUploadDirSips(), targetFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        //写入数据
        accessTmpFile.write((byte[]) param.get("content"));
        accessTmpFile.close();
    }

    public void uploadchunk(Map<String, Object> param,String entrytype,String entryid) throws Exception {
        String tempFileName = param.get("filename") + "_tmp";
        File confFile = new File(getUploadDir(), param.get("filename") + ".conf");
        File tmpFile = new File(getUploadDir(), tempFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");

        long offset = chunkSize * Integer.parseInt((String) param.get("chunk"));
        //定位到该分片的偏移量
        accessTmpFile.seek(offset);
        //写入该分片数据
        accessTmpFile.write((byte[]) param.get("content"));

        //把该分段标记为 true 表示完成
        accessConfFile.setLength(Integer.parseInt((String) param.get("chunks")));
        accessConfFile.seek(Integer.parseInt((String) param.get("chunk")));
        accessConfFile.write(Byte.MAX_VALUE);

        //completeList 检查是否全部完成,如果数组里是否全部都是(全部分片都成功上传)
        byte[] completeList = FileUtils.readFileToByteArray(confFile);
        byte isComplete = Byte.MAX_VALUE;
        for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
            //与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
            isComplete = (byte) (isComplete & completeList[i]);
        }

        accessTmpFile.close();
        accessConfFile.close();

        if("capture".equals(entrytype)&&entryid!=null){
            List<Tb_electronic_capture> electronic_captures = electronicCaptureRepository.findByEntryidAndFilename(entryid,(String) param
                    .get("filename"));
            if(electronic_captures.size()>0){
                Tb_electronic_capture electronic_capture = electronic_captures.get(0);
                saveTVersion(electronic_capture.getFilepath(),electronic_capture.getFilename(),entrytype,entryid); //把同名原始文件存放到临时路径下
            }
        }else if("management".equals(entrytype)&&entryid!=null){
            List<Tb_electronic> electronics = electronicRepository.findByEntryidAndFilename(entryid,(String) param
                    .get("filename"));
            if(electronics.size()>0){
                Tb_electronic electronic = electronics.get(0);
                saveTVersion(electronic.getFilepath(),electronic.getFilename(),entrytype,entryid); //把同名原始文件存放到临时路径下
            }
        }

        //上传完成，删除临时文件，移动到存储路径
        if (isComplete == Byte.MAX_VALUE) {
            confFile.delete();
            //存在entryid时，直接转存到entryid目录下，否则，转存到临时目录
            tmpFile.renameTo(new File("".equals(entryid) ? getEntrytypeStorageDir(entrytype) : getStorageDir(
                    entrytype,entryid), (String) param
                    .get("filename")));
        }
    }

    public void uploadchunkVersion(Map<String, Object> param) throws Exception {
        String tempFileName = param.get("filename") + "_tmp";
        File confFile = new File(getUploadDir(), param.get("filename") + ".conf");
        File tmpFile = new File(getUploadDir(), tempFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");

        long offset = chunkSize * Integer.parseInt((String) param.get("chunk"));
        //定位到该分片的偏移量
        accessTmpFile.seek(offset);
        //写入该分片数据
        accessTmpFile.write((byte[]) param.get("content"));

        //把该分段标记为 true 表示完成
        accessConfFile.setLength(Integer.parseInt((String) param.get("chunks")));
        accessConfFile.seek(Integer.parseInt((String) param.get("chunk")));
        accessConfFile.write(Byte.MAX_VALUE);

        //completeList 检查是否全部完成,如果数组里是否全部都是(全部分片都成功上传)
        byte[] completeList = FileUtils.readFileToByteArray(confFile);
        byte isComplete = Byte.MAX_VALUE;
        for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
            //与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
            isComplete = (byte) (isComplete & completeList[i]);
        }

        accessTmpFile.close();
        accessConfFile.close();

        //上传完成，删除临时文件，移动到存储路径
        if (isComplete == Byte.MAX_VALUE) {
            confFile.delete();
            //存在entryid时，直接转存到entryid目录下，否则，转存到临时目录
            tmpFile.renameTo(new File(getUploadDirSaveEle(), (String) param
                    .get("filename")));
        }
    }

    public void uploadchunkFocus(Map<String, Object> param) throws Exception {
        String tempFileName = param.get("filename") + "_tmp";
        File confFile = new File(getUploadDirFocus(), param.get("filename") + ".conf");
        File tmpFile = new File(getUploadDirFocus(), tempFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");

        long offset = chunkSize * Integer.parseInt((String) param.get("chunk"));
        //定位到该分片的偏移量
        accessTmpFile.seek(offset);
        //写入该分片数据
        accessTmpFile.write((byte[]) param.get("content"));

        //把该分段标记为 true 表示完成
        accessConfFile.setLength(Integer.parseInt((String) param.get("chunks")));
        accessConfFile.seek(Integer.parseInt((String) param.get("chunk")));
        accessConfFile.write(Byte.MAX_VALUE);

        //completeList 检查是否全部完成,如果数组里是否全部都是(全部分片都成功上传)
        byte[] completeList = FileUtils.readFileToByteArray(confFile);
        byte isComplete = Byte.MAX_VALUE;
        for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
            //与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
            isComplete = (byte) (isComplete & completeList[i]);
        }

        accessTmpFile.close();
        accessConfFile.close();

        //上传完成，删除临时文件，移动到存储路径
        if (isComplete == Byte.MAX_VALUE) {
            confFile.delete();
            tmpFile.renameTo(new File(getUploadDirFocus(), (String) param
                    .get("filename")));
            Tb_focus focus = new Tb_focus();
            focus.setPath(tmpFile.getPath().replace(rootpath, ""));
            focus.setTitle(tmpFile.getName().substring(0, tmpFile.getName().lastIndexOf(".")));
            focusRepository.save(focus);
        }
    }

    public void uploadchunkThematic(Map<String, Object> param) throws Exception {
        String tempFileName = param.get("filename") + "_tmp";
        File confFile = new File(getTemporaryThematic(), param.get("filename") + ".conf");
        File tmpFile = new File(getTemporaryThematic(), tempFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");

        long offset = chunkSize * Integer.parseInt((String) param.get("chunk"));
        //定位到该分片的偏移量
        accessTmpFile.seek(offset);
        //写入该分片数据
        accessTmpFile.write((byte[]) param.get("content"));

        //把该分段标记为 true 表示完成
        accessConfFile.setLength(Integer.parseInt((String) param.get("chunks")));
        accessConfFile.seek(Integer.parseInt((String) param.get("chunk")));
        accessConfFile.write(Byte.MAX_VALUE);

        //completeList 检查是否全部完成,如果数组里是否全部都是(全部分片都成功上传)
        byte[] completeList = FileUtils.readFileToByteArray(confFile);
        byte isComplete = Byte.MAX_VALUE;
        for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
            //与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
            isComplete = (byte) (isComplete & completeList[i]);
        }

        accessTmpFile.close();
        accessConfFile.close();
        // 上传完成，删除临时文件
        if (isComplete == Byte.MAX_VALUE) {
            confFile.delete();
            tmpFile.renameTo(new File(getTemporaryThematic(), (String) param
                    .get("filename")));
        }
    }

    public void uploadchunkUserimg(Map<String, Object> param) throws Exception {
        String tempFileName = param.get("filename") + "_tmp";
        File confFile = new File(getUploadDirUserimg(), param.get("filename") + ".conf");
        File tmpFile = new File(getUploadDirUserimg(), tempFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");

        long offset = chunkSize * Integer.parseInt((String) param.get("chunk"));
        //定位到该分片的偏移量
        accessTmpFile.seek(offset);
        //写入该分片数据
        accessTmpFile.write((byte[]) param.get("content"));

        //把该分段标记为 true 表示完成
        accessConfFile.setLength(Integer.parseInt((String) param.get("chunks")));
        accessConfFile.seek(Integer.parseInt((String) param.get("chunk")));
        accessConfFile.write(Byte.MAX_VALUE);

        //completeList 检查是否全部完成,如果数组里是否全部都是(全部分片都成功上传)
        byte[] completeList = FileUtils.readFileToByteArray(confFile);
        byte isComplete = Byte.MAX_VALUE;
        for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
            //与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
            isComplete = (byte) (isComplete & completeList[i]);
        }

        accessTmpFile.close();
        accessConfFile.close();

        //上传完成，删除临时文件，移动到存储路径
        if (isComplete == Byte.MAX_VALUE) {
            confFile.delete();
            tmpFile.renameTo(new File(getUploadDirUserimg(), (String) param
                    .get("filename")));
            Tb_Personalized personalized = new Tb_Personalized();
            personalized.setPath(tmpFile.getPath().replace(rootpath, ""));
            personalized.setTitle(tmpFile.getName().substring(0, tmpFile.getName().lastIndexOf(".")));
            personalizedRepository.save(personalized);
        }
    }

    public void uploadchunkReport(Map<String, Object> param) throws Exception {
        String tempFileName = param.get("filename") + "_tmp";
        File confFile = new File(getUploadDirReport(), param.get("filename") + ".conf");
        File tmpFile = new File(getUploadDirReport(), tempFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");

        long offset = chunkSize * Integer.parseInt((String) param.get("chunk"));
        //定位到该分片的偏移量
        accessTmpFile.seek(offset);
        //写入该分片数据
        accessTmpFile.write((byte[]) param.get("content"));

        //把该分段标记为 true 表示完成
        accessConfFile.setLength(Integer.parseInt((String) param.get("chunks")));
        accessConfFile.seek(Integer.parseInt((String) param.get("chunk")));
        accessConfFile.write(Byte.MAX_VALUE);

        //completeList 检查是否全部完成,如果数组里是否全部都是(全部分片都成功上传)
        byte[] completeList = FileUtils.readFileToByteArray(confFile);
        byte isComplete = Byte.MAX_VALUE;
        for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
            //与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
            isComplete = (byte) (isComplete & completeList[i]);
        }

        accessTmpFile.close();
        accessConfFile.close();
        // 上传完成，删除临时文件
        if (isComplete == Byte.MAX_VALUE) {
            confFile.delete();
            tmpFile.renameTo(new File(getUploadDirReport(), (String) param
                    .get("filename")));
        }
    }

    public void uploadchunkSips(Map<String, Object> param) throws Exception {
        String tempFileName = (String) param.get("filename");
        File confFile = new File(getUploadDirSips(), param.get("filename") + ".conf");
        File tmpFile = new File(getUploadDirSips(), tempFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");

        long offset = chunkSize * Integer.parseInt((String) param.get("chunk"));
        //定位到该分片的偏移量
        accessTmpFile.seek(offset);
        //写入该分片数据
        accessTmpFile.write((byte[]) param.get("content"));

        //把该分段标记为 true 表示完成
        accessConfFile.setLength(Integer.parseInt((String) param.get("chunks")));
        accessConfFile.seek(Integer.parseInt((String) param.get("chunk")));
        accessConfFile.write(Byte.MAX_VALUE);

        //completeList 检查是否全部完成,如果数组里是否全部都是(全部分片都成功上传)
        byte[] completeList = FileUtils.readFileToByteArray(confFile);
        byte isComplete = Byte.MAX_VALUE;
        for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
            //与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
            isComplete = (byte) (isComplete & completeList[i]);
        }

        accessTmpFile.close();
        accessConfFile.close();
    }

    public List<Map<String, Object>> findElectronics(String entrytype, String entryid, String[] eleids) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(new Sort.Order(Sort.Direction.ASC,"sortsequence"));//默认字段排序
        sorts.add(new Sort.Order(Sort.Direction.ASC,"filename"));//文件名排序
        switch (entrytype) {
            case "capture":
                List<Tb_electronic_capture> listc = new ArrayList<Tb_electronic_capture>();
                if ("undefined".equals(entryid) || entryid == null) {
                    listc = electronicCaptureRepository.findByEleidIn(eleids, new Sort(sorts));
                } else {
                    listc = electronicCaptureRepository.findByEntryid(entryid, new Sort(sorts));
                }
                for (Tb_electronic_capture ele : listc) {
                    result.add(ele.getMap());
                }
                break;
            case "solid":
                List<Tb_electronic_solid> listSolid;
                if ("undefined".equals(entryid) || entryid == null) {
                    listSolid = electronicSolidRepository.findByEleidIn(eleids, new Sort(sorts));
                } else {
                    listSolid = electronicSolidRepository.findByEntryid(entryid, new Sort(sorts));
                }
                for (Tb_electronic_solid ele : listSolid) {
                    result.add(ele.getMap());
                }
                break;
            case "long":
                List<Tb_electronic_long> listLong;
                if ("undefined".equals(entryid) || entryid == null) {
                    listLong = electronicLongRepository.findByEleidIn(eleids, new Sort(sorts));
                } else {
                    listLong = electronicLongRepository.findByEntryid(entryid, new Sort(sorts));
                }
                for (Tb_electronic_long ele : listLong) {
                    result.add(ele.getMap());
                }
                break;
            case "access":
                break;
            default:
                List<Tb_electronic> list = new ArrayList<Tb_electronic>();
                if ("undefined".equals(entryid) || entryid == null) {
                    list = electronicRepository.findByEleidIn(eleids, new Sort(sorts));
                } else {
                    list = electronicRepository.findByEntryid(entryid, new Sort(sorts));
                }
                for (Tb_electronic ele : list) {
                    result.add(ele.getMap());
                }
                break;
        }
        return result;
    }

    //专题制作-查看专题的所有文件
    public List<ExtTree> findElectronics(String thematicId) {
        List<ExtTree> extTrees=new ArrayList<>();
        List<Tb_electronic> electronicList = new ArrayList<Tb_electronic>();
        List<Tb_thematic_detail> thematic_details=thematicDetailRepository.findByThematicid(thematicId);
        electronicList = electronicRepository.findByEntryidInOrderBySortsequence(GainField.getFieldValues(thematic_details,"thematicdetilid"));
        for (Tb_electronic ele : electronicList) {
            ExtTree tree = new ExtTree();
            tree.setFnid(ele.getEleid());
            tree.setText(ele.getFilename());
            tree.setExpanded(false);
            tree.setLeaf(true);
            tree.setCls("file");
            extTrees.add(tree);
        }
        return extTrees;
    }

    //递归调用
    public List<ExtTree> findElectronics(String entryid,String fileClassId) {
        List<ExtTree> extTrees=new ArrayList<>();
        List<Tb_electronic> electronicList = new ArrayList<Tb_electronic>();
        if(fileClassId==null||"".equals(fileClassId)){
            electronicList = electronicRepository.findByEntryidAndFileclassidNull(entryid);
        }else {
            electronicList = electronicRepository.findByEntryidAndFileclassid(entryid,fileClassId);
        }
        for (Tb_electronic ele : electronicList) {
            ExtTree tree = new ExtTree();
            tree.setFnid(ele.getEleid());
            tree.setText(ele.getFilename());
            tree.setExpanded(false);
            if("folder".equals(ele.getFilefolder())){
                tree.setFileClassId(ele.getFileclassid());
                tree.setLeaf(false);
                tree.setCls("folder");
                List<ExtTree> childrenTreeList= findElectronics(entryid,ele.getEleid());
                if(childrenTreeList.size()>0) {
                    tree.setExpanded(true);
                    ExtTree[] children = new ExtTree[childrenTreeList.size()];
                    for (int i = 0; i < childrenTreeList.size(); i++) {
                        children[i] = childrenTreeList.get(i);
                    }
                    tree.setChildren(children);
                }
            }else {
                tree.setLeaf(true);
                tree.setCls("file");
            }
            extTrees.add(tree);
        }
        return extTrees;
    }

    /**
     * 获取电子文件信息
     * @param entrytype
     * @param entryid
     * @return
     */
    public List findEle(String entrytype,String entryid) {
        switch (entrytype) {
            case "capture":
                return electronicCaptureRepository.findByEntryid(entryid);
            case "solid":
                return electronicSolidRepository.findByEntryid(entryid);
            case "long":
                return electronicLongRepository.findByEntryid(entryid);
            case "thematicUtilize":
                List<Tb_thematic_detail> thematic_details=thematicDetailRepository.findByThematicid(entryid);
                return electronicRepository.findByEntryidInOrderBySortsequence(GainField.getFieldValues(thematic_details,"thematicdetilid"));
            default:
                return electronicRepository.findByEntryid(entryid);
        }
    }

    public int updateFolderName(String eleId,String name) {
        return electronicRepository.updateFolderName(eleId,name);
    }

    public List<Map<String, Object>> findSxElectronics(String entrytype, String entryid, String[] eleids) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(new Sort.Order(Sort.Direction.ASC,"sequence"));//默认字段排序
        sorts.add(new Sort.Order(Sort.Direction.ASC,"filename"));//文件名排序
        switch (entrytype) {
            case "capture":
                List<Tb_electronic_capture> listc = new ArrayList<Tb_electronic_capture>();
                if ("undefined".equals(entryid) || entryid == null) {
                    listc = electronicCaptureRepository.findByEleidIn(eleids, new Sort(sorts));
                } else {
                    listc = electronicCaptureRepository.findByEntryid(entryid, new Sort(sorts));
                }
                for (Tb_electronic_capture ele : listc) {
                    result.add(ele.getMap());
                }
                break;
            case "solid":
                List<Tb_electronic_browse_sx> listSolid;
                if ("undefined".equals(entryid) || entryid == null) {
                    listSolid = sxElectronicBrowseRepository.findByEleidIn(eleids, new Sort(sorts));
                } else {
                    listSolid = sxElectronicBrowseRepository.findByEntryid(entryid, new Sort(sorts));
                }
                for (Tb_electronic_browse_sx ele : listSolid) {
                    result.add(ele.getMap());
                }
                break;
            case "long":
                List<Tb_electronic_long> listLong;
                if ("undefined".equals(entryid) || entryid == null) {
                    listLong = electronicLongRepository.findByEleidIn(eleids, new Sort(sorts));
                } else {
                    listLong = electronicLongRepository.findByEntryid(entryid, new Sort(sorts));
                }
                for (Tb_electronic_long ele : listLong) {
                    result.add(ele.getMap());
                }
                break;
            case "access":
                break;
            default:
                List<Tb_electronic> list = new ArrayList<Tb_electronic>();
                if ("undefined".equals(entryid) || entryid == null) {
                    list = electronicRepository.findByEleidIn(eleids, new Sort(sorts));
                } else {
                    list = electronicRepository.findByEntryid(entryid, new Sort(sorts));
                    for (Tb_electronic ele : list) {
                        result.add(ele.getMap());
                    }
                    break;
                }
        }
        return result;
    }

    /**
     * 获取焦点图树节点
     *
     * @return
     */
    public List<ExtTree> findElectronicTreeFocus() {
        List<Tb_focus> focuss = focusRepository.findAll();
        String[] focusids=new String[focuss.size()];
        for(int i=0;i<focuss.size();i++){
            focusids[i]=focuss.get(i).getId();
        }
        List<Tb_focus> focuslist = focusRepository.findByFocusidInOrderBySortsequence(focusids);
        List<ExtTree> exttrees = new ArrayList<>();
        for (Tb_focus focus : focuslist) {
            ExtTree extTree = new ExtTree();
            extTree.setFnid(focus.getId());
            extTree.setText(focus.getTitle() + focus.getPath().substring(focus.getPath().lastIndexOf(".")));
            extTree.setExpanded(false);
            extTree.setLeaf(true);
            exttrees.add(extTree);
        }
        return exttrees;
    }

    public Map<String, Object> findElectronic(String entrytype, String eleid) {
        Map<String, Object> map = new HashMap<>();
        switch (entrytype) {
            case "capture":
                Tb_electronic_capture elec = electronicCaptureRepository.findByEleid(eleid);
                map = elec.getMap();
                break;
            case "solid":
                Tb_electronic_solid eleSolid = electronicSolidRepository.findByEleid(eleid);
                if(eleSolid==null){//查档申请管理-申请管理-查看原文显示的利用文件为原始文件数据
                    Tb_electronic ele = electronicRepository.findByEleid(eleid);
                    map = ele.getMap();
                }else {
                    map = eleSolid.getMap();
                }
                break;
            case "long":
                Tb_electronic_long eleLong = electronicLongRepository.findByEleid(eleid);
                map = eleLong.getMap();
                break;
            case "supervisionWork":
                Tb_supervision_electronic supervisionElectronic = supervisionElectronicRepository.findByEleid(eleid);
                map = supervisionElectronic.getMap();
                break;
            case "access":
                break;
            default:
                Tb_electronic ele = electronicRepository.findByEleid(eleid);
                map = ele.getMap();
                break;
        }
//        List<String> imgTypes = Arrays.asList("jpg","png","jpeg","gif");
//        String fileType = (String)map.get("filetype");
//        Long fileSize = map.get("filesize")!=null?Long.parseLong((String)map.get("filesize")):0;
//        if(imgTypes.contains((fileType!=null?fileType.toLowerCase():null))&&fileSize>91000){//当图片大于30M(31457280)时压缩
//            String separator = File.separator;
//            String afterPath = separator+"electronics"+separator+"cache"+separator+userDetails.getLoginname()+separator;
//            String newFilename = "compressImg."+(String)map.get("filetype");
//            File tempFile = new File(rootpath+afterPath);
//            if(!tempFile.exists()){
//                tempFile.mkdirs();
//            }
//            String destPath = rootpath+afterPath+newFilename;
//            String srcPath = rootpath+map.get("filepath")+separator+map.get("filename");
//            boolean status = reduceImg(srcPath,destPath,1,1,0.5f);//压缩图片
//            if(status){
//                map.put("filepath",afterPath);
//                map.put("filename",newFilename);
//            }
//        }
        return map;
    }

    public Map<String, Object> findSxElectronic(String entrytype, String eleid) {
        Map<String, Object> map = new HashMap<>();
        switch (entrytype) {
            case "capture":
                Tb_electronic_capture elec = electronicCaptureRepository.findByEleid(eleid);
                map = elec.getMap();
                break;
            case "solid":
                Tb_electronic_browse_sx sxele = sxElectronicBrowseRepository.findByEleid(eleid);
                map = sxele.getMap();
                break;
            case "long":
                Tb_electronic_long eleLong = electronicLongRepository.findByEleid(eleid);
                map = eleLong.getMap();
                break;
            case "access":
                break;
            default:
                Tb_electronic_browse_sx ele = sxElectronicBrowseRepository.findByEleid(eleid);
                map = ele.getMap();
                break;
        }
        return map;
    }

    //判断文件是否存在
    public Boolean downloadsExist (String entrytype, String idStr){
        Boolean flag = true;
        List<Map<String, Object>> selectionEntitys = getSelectionEntity(entrytype, idStr);
        for (Map<String, Object> selectionEntity : selectionEntitys) {
            String selectionFilename = (String) selectionEntity.get("filename");
            String selectionFilepath = rootpath + selectionEntity.get("filepath");
            String mediaPath = selectionFilepath + File.separator + selectionFilename;
            File srcFile = new File(mediaPath);
            if(!srcFile.exists()){
                return false;
            }
        }
        return flag;
    }

    /**
     * 下载多个文件到指定文件夹
     *
     * @return
     */
    public List<Map<String, Object>> getSelectionEntity(String entrytype, String idStr) {
        String[] ids = idStr.split(",");
        List<Map<String, Object>> selectList = new ArrayList<Map<String, Object>>();
        for (String id : ids) {
            Map<String, Object> selectionMap = findElectronic(entrytype, id);
            selectList.add(selectionMap);
        }
        return selectList;
    }

    /**
     * 压缩文件
     *
     * @return
     */
    public String transFiles(String entrytype, String idStr,String mType) throws IOException {
        //定义下载压缩包名称
        Calendar cal = Calendar.getInstance();
        String zipname = "E" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(cal.getTime());

        //文件复制
        List<Map<String, Object>> selectionEntitys = getSelectionEntity(entrytype, idStr);
        String desPath = null;
        for (Map<String, Object> selectionEntity : selectionEntitys) {
            String selectionFilename = (String) selectionEntity.get("filename");
            String selectionFilepath = rootpath + selectionEntity.get("filepath");
            String file_type = (String) selectionEntity.get("filetype");
            desPath = selectionFilepath.split("storages")[0] + "originalElectronic/" + new SimpleDateFormat("yyyy/M/d").format(cal.getTime());
//            desPath = selectionFilepath.replace("/electronics/storages/", "/electronics/originalElectronic/");
            desPath += File.separator + zipname;
            String mediaPath = selectionFilepath + File.separator + selectionFilename;
            //pdf文件添加水印
            SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if("pdf".equals(file_type)&&("lyjylook".equals(mType)||"gljylook".equals(mType))){   //是否pdf文件
                Tb_watermark watermark = watermarkService.getWatermarkByOrgan(userDetails.getReplaceOrganid());//根据机构id获取水印配置信息
                if (watermark == null) {
                    watermark = watermarkService.getWatermarkByOrgan("0");//若所属机构没有设置水印则使用全局水印
                }
                String waterFilePath = watermarkUtil.getWatermarkPdf(watermark,mediaPath,userDetails.getLoginname(),userDetails.getRealname(),2052,selectionFilename);
                mediaPath = waterFilePath;
            }
            File srcFile = new File(mediaPath);
            File desFile = new File(desPath + File.separator + selectionFilename);
            FileUtils.copyFile(srcFile, desFile);
        }

        //文件压缩
        String transFilepath = desPath;//.substring(0,desPath.lastIndexOf(File.separator));//创建中转文件夹
        ZipUtil.zip(transFilepath.replaceAll("/", "\\\\"), transFilepath.replaceAll("/", "\\\\") + ".zip", "");//压缩
        String zipPath = transFilepath.replace("/", "\\") + ".zip";
        delFolder(transFilepath);
        return zipPath;
    }

    /**
     * 创建中转文件
     *
     * @return
     */
    public String TransFile() {
        String transFilepath = rootpath + "/" + new Date().getTime();
        File transDir = new File(transFilepath);
        if (!transDir.exists()) {
            transDir.mkdir();
        }
        File transZip = new File(transFilepath + ".zip");
        if (transZip.exists()) {
            transZip.delete();
        }
        return transFilepath;
    }

    public Integer deleteElectronic(String entrytype, String entryid, String eleids) throws IOException {
        String[] eleidArray = eleids.split(",");
        Integer num = 0;
        switch (entrytype) {
            case "capture":
                List<Tb_electronic_capture> electronicCaptures = electronicCaptureRepository.findByEleidInOrderBySortsequence(eleidArray);//获取删除电子文件
                for (Tb_electronic_capture electronic_capture : electronicCaptures) {
                    saveCaptureRecyclebinElectronic(electronic_capture,entrytype);//记录回收文件信息，存储至电子文件回收表
//                    recycleFile(electronic_capture.getFilepath(), electronic_capture.getFilename());//文件回收（将文件复制至另一存储路径后删除源文件）

                    String yasuo_filepath =rootpath + electronic_capture.getFilepath() + "/" + electronic_capture.getFilename().replace(".", "_compression.");//如果存在該圖像文件的壓縮文件，則把壓縮文件也一同刪除
                    File file =  new File(yasuo_filepath);//压缩图片文件路径
                    if(file.exists()){
                        file.delete();
                    }
                }
                num = electronicCaptureRepository.deleteByEleidIn(eleidArray);
                electronicVersionCaptureRepository.deleteByEleidIn(eleidArray); //删除电子文件历史版本
                if (!"undefined".equals(entryid)) {
                    entryCaptureService.updateEleNum(entryid, "remove", num);
                }
                break;
            case "access":
                break;
            default:
                List<Tb_electronic> electronics = electronicRepository.findByEleidInOrderBySortsequence(eleidArray);//获取删除电子文件
                for (Tb_electronic electronic : electronics) {
                    saveManagementRecyclebinElectronic(electronic,entrytype);//记录回收文件信息，存储至电子文件回收表
//                    recycleFile(electronic.getFilepath(), electronic.getFilename());//文件回收（将文件复制至另一存储路径后删除源文件）

                    String yasuo_filepath =rootpath + electronic.getFilepath() + "/" + electronic.getFilename().replace(".", "_compression.");//如果存在該圖像文件的壓縮文件，則把壓縮文件也一同刪除
                    File file =  new File(yasuo_filepath);//压缩图片文件路径
                    if(file.exists()){
                        file.delete();
                    }
                }
                num = electronicRepository.deleteByEleidIn(eleidArray);
                electronicVersionRepository.deleteByEleidIn(eleidArray);  //删除电子文件历史版本
                //删除关联的全文表记录
                fullTextRepository.deleteByEleidIn(eleidArray);
                //删除关联的solr全文检索记录
                fullSearchController.delSolrRecord(eleidArray,"eleid");
                if (!"undefined".equals(entryid)) {
                    entryService.updateEleNum(entryid, "remove", num);
                }
                break;
        }
        return num;
    }

    /**
     * 删除专题电子文件
     *
     * @param entrytype 类型
     * @param entryid   专题内容id
     * @param eleids    电子文件id
     * @return
     */
    public Integer deleteZtElectronic(String entrytype, String entryid, String eleids) {
        String[] eleidArray = eleids.split(",");
        Integer num = 0;
        List<Tb_electronic> electronics = electronicRepository.findByEleidInOrderBySortsequence(eleidArray);//获取删除电子文件
        for (Tb_electronic electronic : electronics) {
            File file = new File(rootpath + electronic.getFilepath() + "/" + electronic.getFilename());
            file.delete();//删除电子文件
        }
        num = electronicRepository.deleteByEleidIn(eleidArray);//删除条目
        if (!"undefined".equals(entryid)) {
            List<Tb_electronic> electronics1 = electronicRepository.findByEntryidOrderBySortsequence(entryid);
            String mediatext = "";
            for (Tb_electronic electronic : electronics1) {
                mediatext += "," + electronic.getFilename();//获取电子文件文本字段
            }
            mediatext = "".equals(mediatext) ? "" : mediatext.substring(1);
            Tb_thematic_detail thematic_detail = thematicDetailRepository.findByThematicdetilid(entryid);
            if (thematic_detail != null) {
                thematic_detail.setMediatext(mediatext);//更新电子文件文本字段
            }
        }
        return num;
    }

    /**
     * 声像用的保存
     * @param entrytype
     * @param entryid
     * @param filename
     * @param isMedia
     * @param currentMD5
     * @return
     */
    public List<Object> saveElectronic(String entrytype, String entryid, String filename, boolean isMedia,
                                       String currentMD5) {
        File targetFile;
        if (entryid != null) {
            targetFile = new File(rootpath + getStorageBaseDir(entrytype, entryid), filename);
            if (isMedia) {
                entryService.delElectronicByEntryid(new String[]{entryid});// 修改：删除已有的相关电子文件及其信息
            }
        } else {
            targetFile = new File(rootpath + getTemporaryStoragesBaseDir(entrytype), filename);
        }
        File thFile = new File(getThStorageDir(entrytype),filename);
        if(thFile.exists()){  //删除临时缩列图原始文件
            thFile.delete();
        }
        Tb_electronic_browse eb = new Tb_electronic_browse();
        Map<String, Object> map = new HashMap<>();
        Tb_electronic rebackelec = new Tb_electronic();
        Tb_electronic elec = new Tb_electronic();
        elec.setFilesize(String.valueOf(targetFile.length()));
        // 有条目ID 的上传文件，比如修改条目
        if (entryid != null) {
            elec.setEntryid(entryid);
            // 有entryid时的上传文件，由于前端监听上传成功时，已经直接将文件转存到entryid目录下，只需要保存文件路径就可以(electronics/storages/年/月/日/类型
            // (capture-采集、management-数据管理)/条目ID)
            elec.setFilepath(getStorageBaseDir(entrytype, entryid));
            entryService.updateEleNum(entryid, "add", 1);
        } else {
            // 没有entryid时的上传文件，即未保存条目，先上传文件(electronics/temporaryStorages/年/月/日/类型(capture-采集、management-数据管理)/用户ID)
            elec.setFilepath(getTemporaryStoragesBaseDir(entrytype));
        }
        elec.setFilename(filename);
        elec.setFiletype(filename.substring(filename.lastIndexOf('.') + 1));
        elec.setMd5(currentMD5);
        elec = electronicRepository.save(elec);
        rebackelec = elec;
        map = elec.getMap();

        if (isMedia) {
            BeanUtils.copyProperties(elec, eb);
        }
        List<Object> list = new ArrayList<>();
        list.add(map);
        list.add(eb);
        list.add(rebackelec);
        return list;
        // return map;
    }

    // 年 + 月 + 日 + 类型(capture-采集、management-数据管理) + + 用户ID 路径的临时文件夹
    private String getThStorageDir(String entrytype) {
        String eledir = rootpath + getThTemporaryStoragesBaseDir(entrytype);
        File eleDir = new File(eledir);
        if (!eleDir.exists()) {
            eleDir.mkdirs();
        }
        return eledir;
    }

    // 数据采集、数据管理临时存储的临时缩列图压缩文件夹(electronics/temporaryStorages/年/月/日/类型(capture-采集、management-数据管理)/用户ID)
    private String getThTemporaryStoragesBaseDir(String entrytype) {
        try {
            SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Calendar cal = Calendar.getInstance();
            return "/electronics/temporaryStorages/thumbnail/" + cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/"
                    + cal.get(Calendar.DATE) + "/" + entrytype + "/" + userDetails.getUserid().trim();
        }catch(NullPointerException e){
//            e.printStackTrace();
            Calendar cal = Calendar.getInstance();
            return "/electronics/temporaryStorages/thumbnail/" + cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/"
                    + cal.get(Calendar.DATE) + "/" + entrytype + "/" + "AutoCaptureTask";
        }
    }

    /**
     * 删除附件
     *
     * @param eleids    电子文件id
     * @return
     */
    public Integer deleteApproveEle(String eleids) {
        String[] eleidArray = eleids.split(",");
        Integer num = 0;
        List<Tb_electronic> electronics = electronicRepository.findByEleidInOrderBySortsequence(eleidArray);//获取删除电子文件
        List<String> eleidstrs = new ArrayList<>();
        for (Tb_electronic electronic : electronics) {
            File file = new File(rootpath + electronic.getFilepath() + "/" + electronic.getFilename());
            file.delete();//删除电子文件
            eleidstrs.add(electronic.getFilename());
        }
        Tb_borrowdoc borrowdoc = borrowDocRepository.findByBorrowcode(electronics.get(0).getEntryid());
        num = electronicRepository.deleteByEleidIn(eleidArray);//删除条目
        List<Tb_electronic> electronicdocs =  electronicRepository.findByEntryid(borrowdoc.getBorrowcode());
        String evidencetext = "";
        for(Tb_electronic electronic : electronicdocs){
            if("".equals(evidencetext)){
                evidencetext = electronic.getFilename();
            }else{
                evidencetext = evidencetext+","+electronic.getFilename();
            }
        }
        borrowdoc.setEvidencetext(evidencetext); //更新附件
        return num;
    }

    public Map<String, Object> saveElectronic(String entrytype, String entryid, String filename) {
        File targetFile;
        if(entryid != null){
            targetFile= new File(rootpath + getStorageBaseDir(entrytype,entryid), filename);
        }else{
            targetFile= new File(rootpath + getTemporaryStoragesBaseDir(entrytype), filename);
        }
        //计算文件的MD5值
        FileInputStream fis = null;
        StringBuffer md5 = new StringBuffer();
        try {
            fis = new FileInputStream(targetFile);
            md5.append(DigestUtils.md5Hex(fis));
            fis.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        Map<String, Object> map = new HashMap<>();
        switch (entrytype) {
            case "capture":
                Tb_electronic_capture elec = new Tb_electronic_capture();
                List<Tb_electronic_capture> electronic_captures = electronicCaptureRepository.findByEntryidAndFilename(entryid, filename);
                if (electronic_captures.size() > 0&&entryid !=null) { //判断是否上传同名文件
                    elec = electronic_captures.get(0);
                    saveVersionCapture(elec,entrytype,entryid); //更新电子版本信息
                    elec.setFilepath(getStorageBaseDir(entrytype, entryid));
                    elec.setFilesize(String.valueOf(targetFile.length()));
                    elec.setMd5(md5.toString());
                } else {
                    elec.setFilesize(String.valueOf(targetFile.length()));
                    String filetype = filename.substring(filename.lastIndexOf('.') + 1);
                    //有条目ID 的上传文件，比如修改条目
                    if (entryid != null) {
                        elec.setEntryid(entryid);
                        //有entryid时的上传文件，由于前端监听上传成功时，已经直接将文件转存到entryid目录下，只需要保存文件路径就可以(electronics/storages/年/月/日/类型
                        // (capture-采集、management-数据管理)/条目ID)
                        elec.setFilepath(getStorageBaseDir(entrytype, entryid));
                        entryCaptureService.updateEleNum(entryid, "add", 1);
                    } else {
                        //没有entryid时的上传文件，即未保存条目，先上传文件(electronics/temporaryStorages/年/月/日/类型(capture-采集、management-数据管理)/用户ID)
                        elec.setFilepath(getTemporaryStoragesBaseDir(entrytype));
                    }
                    elec.setFilename(filename);
                    elec.setFiletype(filename.substring(filename.lastIndexOf('.') + 1));
                    elec.setMd5(md5.toString());
                }
                elec = electronicCaptureRepository.save(elec);
                map = elec.getMap();
                if(entryid == null || "".equals(entryid))
                {
                    break;
                }
                boolean isMedia1 = false;
                List<Tb_electronic_capture> eleCaptureList = electronicCaptureRepository.findByEntryid(entryid);
                Tb_entry_index_capture tbe1 = entryIndexCaptureRepository.findByEntryid(entryid);
                Tb_data_node_mdaflag mdaflag1 = dataNodeExtRepository.findNodeid(tbe1.getNodeid());
                String nowDate1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                if(mdaflag1 != null)
                    isMedia1 = true;
                else
                    isMedia1 = false;

                //在这里判断是否是声像节点
                if(isMedia1 != true){
                    break;
                }
                String types = FileUtil.identifyFileType(targetFile.getName());
                ExtMsg mediaImfo1 = null;
                Tb_electronic_browse eb1 = new Tb_electronic_browse();
                BeanUtils.copyProperties(eleCaptureList.get(0), eb1);
                if(null != types && types.startsWith("image")){//图片
                    mediaImfo1 = getMediaDataCapture(targetFile.getName(),eleCaptureList.get(0),nowDate1,3);
                    this.compression_Capture(eb1, 3);// 压缩
                }
                else if(null != types && types.startsWith("video")){//视频
                    mediaImfo1 = getMediaDataCapture(targetFile.getName(),eleCaptureList.get(0),nowDate1,1);
                    this.compression_Capture(eb1, 1);// 压缩
                }
                else if(null != types && types.startsWith("audio")){//音频
                    mediaImfo1 = getMediaDataCapture(targetFile.getName(),eleCaptureList.get(0),nowDate1,2);
                    this.compression_Capture(eb1, 2);// 压缩
                }
                if(mediaImfo1!=null){
                    Tb_entry_detail_capture detail1 = (Tb_entry_detail_capture)mediaImfo1.getData();
                    entryDetailCaptureRepository.save(detail1);
                }
                break;
            case "access":
                break;
            default:
                Tb_electronic ele = new Tb_electronic();
                List<Tb_electronic> electronics = electronicRepository.findByEntryidAndFilename(entryid, filename);
                if (electronics.size() > 0&&entryid !=null) { //判断是否上传同名文件
                    ele = electronics.get(0);
                    saveVersion(ele,entrytype,entryid); //更新电子版本信息
                    ele.setFilepath(getStorageBaseDir(entrytype, entryid));
                    ele.setFilesize(String.valueOf(targetFile.length()));
                    ele.setMd5(md5.toString());
                } else {
                    ele.setFilesize(String.valueOf(targetFile.length()));
                    String type = filename.substring(filename.lastIndexOf('.') + 1);
                    if (entryid != null) {
                        ele.setEntryid(entryid);
                        //有entryid时的上传文件，由于前端监听上传成功时，已经直接将文件转存到entryid目录下，只需要保存文件路径就可以(electronics/storages/年/月/日/类型
                        // (capture-采集、management-数据管理)/条目ID)
                        ele.setFilepath(getStorageBaseDir(entrytype, entryid));
                        entryService.updateEleNum(entryid, "add", 1);
                    } else {
                        //没有entryid时的上传文件，即未保存条目，先上传文件(electronics/temporaryStorages/年/月/日/类型(capture-采集、management-数据管理)/用户ID)
                        ele.setFilepath(getTemporaryStoragesBaseDir(entrytype));
                    }
                    ele.setFilename(filename);
                    ele.setFiletype(filename.substring(filename.lastIndexOf('.') + 1));
                    ele.setMd5(md5.toString());
                }
                ele = electronicRepository.save(ele);
                map = ele.getMap() ;
                if(entryid == null || "".equals(entryid))
                {
                    break;
                }
                boolean isMedia = false;
                Tb_entry_index tbe = entryIndexRepository.findByEntryid(entryid);
                Tb_data_node_mdaflag mdaflag = dataNodeExtRepository.findNodeid(tbe.getNodeid());
                if(mdaflag != null)
                    isMedia = true;
                else
                    isMedia = false;

                //在这里判断是否是声像节点
                if(isMedia != true){
                    break;
                }
                Tb_electronic electronicForEntry = null;
                List<Tb_electronic> electronicsForEntry = electronicRepository.findByEntryid(entryid);
                if(electronicsForEntry.size() <= 0)
                {
                    electronicForEntry = new Tb_electronic();
                    electronicForEntry.setEntryid(entryid);
                }
                else{
                    electronicForEntry = electronicsForEntry.get(0);
                }
                String types1 = FileUtil.identifyFileType(targetFile.getName());
                ExtMsg mediaImfo = null;
                String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                Tb_electronic_browse eb = new Tb_electronic_browse();
                BeanUtils.copyProperties(electronicForEntry, eb);
                if(null != types1 && types1.startsWith("image")){//图片
                    mediaImfo = getMediaData(targetFile.getName(),electronicForEntry,nowDate,3);
                    compression(eb, 3);// 压缩
                }
                else if(null != types1 && types1.startsWith("video")){//视频
                    mediaImfo = getMediaData(targetFile.getName(),electronicForEntry,nowDate,1);
                    compression(eb, 1);// 压缩
                }
                else if(null != types1 && types1.startsWith("audio")){//音频
                    mediaImfo = getMediaData(targetFile.getName(),electronicForEntry,nowDate,2);
                    compression(eb, 2);// 压缩
                }

                Tb_entry_detail detail = (Tb_entry_detail)mediaImfo.getData();
                entryDetailRepository.save(detail);
                break;
        }
        return map;
    }

    public void saveVersion(Tb_electronic electronic,String entrytype,String entryid){
        List<Tb_electronic_version> versions = electronicVersionRepository.findByEleid(electronic.getEleid());
        int versionNumber = versions.size()+1;
        String version = "v"+versionNumber+".0";
        Tb_electronic_version eleversion = new Tb_electronic_version();
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        File targetFile = new File(getUploadDirSaveTVersionEle(entrytype,entryid), electronic.getFilename());
        // 获取新的存储电子文件路径
        String filepath = getUploadDirSaveVersion(version,electronic.getEleid().trim(),"management")
                .replace(rootpath, "");
        // 把之前原来电子文件复制到存储路径
        File newFile = new File(rootpath + filepath, electronic.getFilename());
        try {
            FileUtils.copyFile(targetFile,newFile);
            targetFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        eleversion.setFilesize(electronic.getFilesize());
        eleversion.setFilepath(filepath);
        eleversion.setCreatename(userDetails.getLoginname());
        eleversion.setCreatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        eleversion.setEleid(electronic.getEleid());
        eleversion.setFilename(electronic.getFilename());
        eleversion.setFiletype(electronic.getFiletype());
        eleversion.setEntryid(electronic.getEntryid());
        eleversion.setVersion(version);
        electronicVersionRepository.save(eleversion);
    }

    public void saveVersionCapture(Tb_electronic_capture electronic,String entrytype,String entryid){
        List<Tb_electronic_version_capture> version_captures = electronicVersionCaptureRepository.findByEleid(electronic.getEleid());
        int versionNumber = version_captures.size()+1;
        String version = "v"+versionNumber+".0";
        Tb_electronic_version_capture eleversion = new Tb_electronic_version_capture();
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        File targetFile = new File(getUploadDirSaveTVersionEle(entrytype,entryid), electronic.getFilename());
        // 获取新的存储电子文件路径
        String filepath = getUploadDirSaveVersion(version,electronic.getEleid().trim(),"capture")
                .replace(rootpath, "");
        // 把之前原来电子文件复制到存储路径
        File newFile = new File(rootpath + filepath, electronic.getFilename());
        try {
            FileUtils.copyFile(targetFile,newFile);
            targetFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        eleversion.setFilesize(electronic.getFilesize());
        eleversion.setFilepath(filepath);
        eleversion.setCreatename(userDetails.getLoginname());
        eleversion.setCreatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        eleversion.setEleid(electronic.getEleid());
        eleversion.setFilename(electronic.getFilename());
        eleversion.setFiletype(electronic.getFiletype());
        eleversion.setEntryid(electronic.getEntryid());
        eleversion.setVersion(version);
        electronicVersionCaptureRepository.save(eleversion);
    }

    public int readPages (String filetype, File targetFile,String filepath){ //读取文件页数
        int pages = 1;   // 不支持读页数的文件   设置为‘1’
        if (filetype.equals("pdf")) {//读取PDF文件的页数
            PDDocument pdfReader = null;
            try {
                pdfReader = PDDocument.load(targetFile);
                pages = pdfReader.getPages().getCount();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    pdfReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (filetype.equals("tif") || filetype.equals("Tif") || filetype.equals("tiff")) {//读取TIF文件的页数
            SeekableStream s = null;
            try {
                s = new FileSeekableStream(targetFile);
                pages = TIFFDirectory.getNumDirectories(s);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    s.close();  //关闭流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (filetype.equals("doc")) {
            pages = getDocPageNum(filepath);
        }
        else if (filetype.equals("docx")) {
            XWPFDocument docx = null;
            try {
                docx = new XWPFDocument(POIXMLDocument.openPackage(filepath));
                pages = docx.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    docx.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (filetype.endsWith("xls")) {
            HSSFWorkbook workbook = null;
            try {
                workbook = new HSSFWorkbook(new FileInputStream(filepath));
                Integer sheetNums = workbook.getNumberOfSheets();
                if (sheetNums > 0) {
                    pages = workbook.getSheetAt(0).getRowBreaks().length + 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (filetype.endsWith("xlsx")) {
            XSSFWorkbook xwb = null;
            try {
                xwb = new XSSFWorkbook(filepath);
                Integer sheetNums = xwb.getNumberOfSheets();
                if (sheetNums > 0) {
                    pages = xwb.getSheetAt(0).getRowBreaks().length + 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    xwb.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (filetype.endsWith("ppt")) {
            HSLFSlideShow document = null;
            try {
                document = new HSLFSlideShow(new FileInputStream(filepath));
                pages = document.getSlides().size();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (filetype.endsWith("pptx")) {
            XMLSlideShow xslideShow = null;
            try {
                xslideShow = new XMLSlideShow(new FileInputStream(filepath));
                pages = xslideShow.getSlides().size() + 1; //当前用的是poi3.1.6，不支持读取length。 需要用到poi3.8。但是poi3.8不支持dispose()方法。。暂时没有找到合适的版本所以暂时不读取pptx的文件
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    xslideShow.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return pages;
    }

    private static int getDocPageNum(String filePath) {
        int pageNum = 0;
        String newFilePath = filePath.replace("/","\\");
        try{
            // 建立ActiveX部件
            ActiveXComponent wordCom = new ActiveXComponent("Word.Application");
            //word应用程序不可见
            wordCom.setProperty("Visible", false);
            // 返回wrdCom.Documents的Dispatch
            Dispatch wrdDocs = wordCom.getProperty("Documents").toDispatch();//Documents表示word的所有文档窗口（word是多文档应用程序）

            // 调用wrdCom.Documents.Open方法打开指定的word文档，返回wordDoc
            Dispatch wordDoc = Dispatch.call(wrdDocs, "Open", newFilePath).toDispatch();
            Dispatch selection = Dispatch.get(wordCom, "Selection").toDispatch();
            pageNum = Integer.parseInt(Dispatch.call(selection,"information",4).toString());//总页数 //显示修订内容的最终状态

            //关闭文档且不保存
            Dispatch.call(wordDoc, "Close", new Variant(false));
            //退出进程对象
            wordCom.invoke("Quit", new Variant[] {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageNum;
    }

    public int setPages(String eleid,String pages,String entrytype){

        if(entrytype.equals("management"))
        {
            return  electronicRepository.updatePagesByEleid(pages,eleid);
        }
        else if(entrytype.equals("capture"))
        {
            return electronicCaptureRepository.updatePagesByEleid(pages,eleid);
        }
        else
            return 0;
    }

    /**
     *
     * @param entrytype
     * @param entryid
     * @param filename
     * @param fileclassid 父级编号
     * @param filePath 文件路径
     * @return
     */
    public Map<String, Object> saveZtElectronic(String entrytype, String entryid, String filename,String fileclassid,String filePath) {
        File targetFile;
        Tb_electronic ele = new Tb_electronic();
        ele.setFileclassid(fileclassid);
        if("".equals(fileclassid)||fileclassid==null||"".equals(filePath)){
            targetFile = new File(getTemporaryThematic(), filename);
        }else {
            targetFile=new File(filePath);
        }
        //计算文件的MD5值
        FileInputStream fis = null;
        StringBuffer md5 = new StringBuffer();
        try {
            fis = new FileInputStream(targetFile);
            md5.append(DigestUtils.md5Hex(fis));
            fis.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        Map<String, Object> map = new HashMap<>();
        ele.setEntryid(entryid == null ? "" : entryid);
        ele.setFilename(filename);
        ele.setFilesize(String.valueOf(targetFile.length()));
        if(entryid != null && !"undefined".equals(entryid)){
            List<Tb_electronic> electronics = electronicRepository.findByEntryidAndFilepathIsNotNull(entryid);
            String filepath;
            if(electronics.size()>0){
                filepath = electronics.get(0).getFilepath();
            }else{
                filepath= getUploadDirThematic(entryid).replace(rootpath, "");
            }
            ele.setFilepath(filepath);
            //把之前临时文件转存到存储路径
            targetFile.renameTo(new File(rootpath + filepath, filename));
            //转存完成后删除原来的临时文件
            targetFile.delete();

            String mediatext = "";
            for (Tb_electronic electronic : electronics) {
                mediatext += "," + electronic.getFilename();
            }
            mediatext = "".equals(mediatext) ? "" : mediatext.substring(1);
            Tb_thematic_detail thematic_detail = thematicDetailRepository.findByThematicdetilid(entryid);
            thematic_detail.setMediatext(mediatext);
        }else{
            ele.setFilepath(getTemporaryThematic().replace(rootpath, ""));
        }
        ele.setFiletype(filename.substring(filename.lastIndexOf('.') + 1));
        ele.setMd5(md5.toString());
        ele = electronicRepository.save(ele);//保存电子文件
        map = ele.getMap();
        return map;
    }

    public Tb_focus getFocus(String id) {
        return focusRepository.findByFocusid(id);
    }

    /**
     * 删除焦点图
     *
     * @param ids 节点元素id
     * @return
     */
    public int electronicsFocusDel(String[] ids) {
        List<Tb_focus> focuss = focusRepository.findByFocusidIn(ids);
        for (Tb_focus focus : focuss) {
            File file = new File(rootpath + focus.getPath());
            if (file.exists()) {
                file.delete();//删除文件
                file.getParentFile().delete();//删除文件所在文件夹
            }
        }
        int i = focusRepository.deleteByFocusidIn(ids);//删除焦点图条目
        return i;
    }

    public List<Tb_focus> getFocus() {
        List<Tb_focus> focuss = focusRepository.findAll();
        String[] focusids=new String[focuss.size()];
        for(int i=0;i<focuss.size();i++){
            focusids[i]=focuss.get(i).getId();
        }
        List<Tb_focus> focuslist = focusRepository.findByFocusidInOrderBySortsequence(focusids);
        return focuslist;
    }

    public ExtMsg analysisSipfile(String fileName, String fileSize) {
        CheckSip checkSip = new CheckSip();
        boolean result;
        String msg = "'" + fileName + "'";
        int count = 0;
        String quality = "";
        String norm = "";
        ExtMsg extMsg = null;
        File confFile = new File(getUploadDirSips(), fileName);
        try {
            FileInputStream fileInputStream = new FileInputStream(confFile);
            Tb_exchange_reception exchangeReception = null;
            String name = fileName.substring(fileName.lastIndexOf(".") + 1);
            if ("sip".equals(name)) {
                Charset gbk = Charset.forName("GBK");
                ZipInputStream zipInputStream = new ZipInputStream(fileInputStream, gbk);
                ZipEntry zipEntry;
                boolean document = false, sip = false;
                Sip mSip =null;
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    //判断是否包含document
                    if (zipEntry.getName().indexOf("/") != -1) {
                        if ("document".equals(zipEntry.getName().substring(0, zipEntry.getName().indexOf("/")))) {
                            document = true;
                            String zeName = zipEntry.getName().substring(zipEntry.getName().lastIndexOf("/") + 1);
                            if (!"".equals(zeName)) {
                                String extension = zeName.substring(zeName.lastIndexOf(".") + 1);
                                byte[] data = getByte(zipInputStream); // 获取当前条目的字节数组
                                boolean isextension = DocValidationUtil.contrastFileType(extension, new ByteArrayInputStream
                                        (data));
                                count++;
                                if ("".equals(quality)) {
                                    quality = convertsFileHtml(zeName, isextension);
                                } else {
                                    quality = quality + convertsFileHtml(zeName, isextension);
                                }
                            }
                        }
                    } else if ("sip.xml".equals(zipEntry.getName())) {
                        byte[] data = getByte(zipInputStream); // 获取当前条目的字节数组
                        InputStream sipInputStream = new ByteArrayInputStream(data); // 把当前条目的字节数据转换成Inputstream流
                        String xsdPath = "../xsd/富滇电子公文档案接收文件格式.xsd";
                        AnalysisSipUntil analysisSipUntil = new AnalysisSipUntil(xsdPath, sipInputStream);
                        mSip = analysisSipUntil.getSipData();
                        if(mSip!=null)
                        {
                            norm = "sip.xml文件数据格式规范";
                            sip = true;
                        }else{
                            norm = "sip.xml文件数据格式不规范";
                        }
                    }
                }
                if (!document && !sip) {
                    result = false;
                    msg = msg + "上传失败,请检查压缩文件是否存在document文件夹和sip.xml文件";
                    checkSip.setCount(convertsHtml(false, "电子数量：0"));
                    checkSip.setQuality(convertsHtml(false, "缺少document文件夹"));
                    checkSip.setNorm(convertsHtml(false, "缺少sip.xml文件"));
                } else if (!document) {
                    result = false;
                    msg = msg + "上传失败，压缩文件没有document文件夹或者存放目录格式不对";
                    checkSip.setCount(convertsHtml(false, "电子数量：0"));
                    checkSip.setQuality(convertsHtml(false, "缺少document文件夹"));
                    checkSip.setNorm(convertsHtml(sip, "".equals(norm)? "缺少sip.xml文件":norm));
                } else if (!sip) {
                    result = false;
                    msg = msg + "上传失败，压缩文件没有sip.xml文件或者存放目录格式不对";
                    checkSip.setCount(convertsHtml(false, "sip.xml电子数量："+ mSip!=null?String.valueOf(mSip.getFile().size())
                            :0+",document电子数量：" + count));
                    checkSip.setQuality(quality);
                    checkSip.setNorm(convertsHtml(false, "".equals(norm)? "缺少sip.xml文件":norm));
                } else {
                    if(mSip.getFile().size()!=count){
                        result = false;
                        msg = msg + "上传失败，sip.xml中文件数量与文件夹document文件数量不符合";
                        checkSip.setCount(convertsHtml(false, "sip.xml电子数量："+ mSip.getFile().size()+",document电子数量：" +
                                count));
                        checkSip.setQuality(quality);
                        checkSip.setNorm(convertsHtml(true, "sip.xml文件数据格式规范"));
                    }else{
                        FileInputStream arrayStream = new FileInputStream(confFile);
                        byte[] filedata = IOUtils.toByteArray(arrayStream);
                        exchangeReception = exchangeService.saveExchange(new Tb_exchange_reception
                                (fileName, DigestUtils.md5Hex(new FileInputStream(confFile)),filedata , Long
                                        .valueOf(fileSize), DateUtil.getCurrentTime()));
                        if (exchangeReception != null) {
                            result = true;
                            msg = msg + "上传成功";
                            checkSip.setCount(convertsHtml(true, "电子数量：" + count));
                            checkSip.setQuality(quality);
                            checkSip.setNorm(convertsHtml(true, "sip.xml文件数据格式规范"));
                        } else {
                            result = false;
                            msg = msg + "上传失败";
                            checkSip.setCount(convertsHtml(true, "电子数量：" + count));
                            checkSip.setQuality(convertsHtml(true, quality));
                            checkSip.setNorm(convertsHtml(false, "sip.xml文件数据格式不规范"));
                        }
                        arrayStream.close();
                    }
                }
                extMsg = new ExtMsg(result, msg, checkSip);
                zipInputStream.close();
            } else if ("xls".equals(name) || "xlsx".equals(name)) {
                FileInputStream arrayStream = new FileInputStream(confFile);
                byte[] filedata = IOUtils.toByteArray(arrayStream);
                exchangeReception = exchangeService.saveExchange(new Tb_exchange_reception
                        (fileName, DigestUtils.md5Hex(new FileInputStream(confFile)), filedata, Long
                                .valueOf(fileSize), DateUtil.getCurrentTime()));
                if (exchangeReception != null) {
                    extMsg = new ExtMsg(true, msg + "上传成功", null);
                } else {
                    extMsg = new ExtMsg(false, msg + "上传失败", null);
                }
                arrayStream.close();
            }
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            extMsg = new ExtMsg(false, msg + "上传失败", checkSip);
        }
        if (confFile.exists()) {
            confFile.delete();
        }
        return extMsg;
    }

    /**
     * 转换成html标签
     *
     * @param validate 验证是否通过
     * @param content  嵌套内容，以“,”隔开
     * @return
     */
    private String convertsHtml(boolean validate, String content) {
        String html = "";
        if (validate) {
            html += "<span style=\"color:green\">验证通过：</br>";
        } else {
            html += "<span style=\"color:red\">验证不通过：</br>";
        }
        String[] contens = content.split(",");
        for (int i = 0; i < contens.length; i++) {
            if (i == contens.length - 1) {
                html += "</br>" + contens[i] + "</span>";
            } else {
                html += "</br>" + contens[i] + "</br>";
            }
        }
        return html;
    }

    /**
     * 独立拼接质量检查
     *
     * @param fileName    文件名称
     * @param isextension 是否与扩展名相符合
     * @return
     */
    private String convertsFileHtml(String fileName, boolean isextension) {
        String html;
        if (isextension) {
            html = "<span style=\"color:green\">" + fileName + "（验证通过，文件头与文件扩展名相符合）</br></br></span>";
        } else {
            html = "<span style=\"color:red\">" + fileName + "（验证不通过，文件头与文件扩展名不符合）</br></br></span>";
        }
        return html;
    }

    public void saveUserimgInfo(String filenname) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Tb_Personalized personalized = personalizedRepository.findByUserid(userDetails.getUserid()) == null ?
                new Tb_Personalized() : personalizedRepository.findByUserid(userDetails.getUserid());
        personalized.setTitle(filenname);
        personalized.setPath(getStorageUserimgBaseDir());
        personalized.setUserid(userDetails.getUserid());
        personalizedRepository.save(personalized);
    }

    public String getFileEntryidByInnerfileEntryid(String entryid, String entrytype) {
        String innerfileArchivecode = "";
        String innerfileNodeid = "";
        String fileArchivecode = "";
        String fileEntryid = "";
        switch (entrytype) {//获取案卷档号
            case "capture":
                Tb_entry_index_capture indexCapture = entryIndexCaptureRepository.findByEntryid(entryid);
                innerfileArchivecode = indexCapture.getArchivecode();
                innerfileNodeid = indexCapture.getNodeid();
                break;
            case "access":
                break;
            case "management":
                Tb_entry_index index = entryIndexRepository.findByEntryid(entryid);
                innerfileArchivecode = index.getArchivecode();
                innerfileNodeid = index.getNodeid();
                break;
        }
        String innerfileParentnodeid = dataNodeRepository.findParentnodeidByNodeid(innerfileNodeid);//卷内文件节点的父节点nodeid
        List<String> childrenNodeids = entryIndexService.getNodeidByWithAs(innerfileParentnodeid);//卷内文件节点并列的节点的nodeid集合
        String[] childrenNodeidArr = new String[childrenNodeids.size()];
        childrenNodeids.toArray(childrenNodeidArr);
        List<String> codeSettingSplitCodes = codesetRepository.findSplitcodeByDatanodeid(innerfileNodeid);
        if (codeSettingSplitCodes.size() > 1) {
            String codeSettingSplitCode = codeSettingSplitCodes.get(codeSettingSplitCodes.size() - 2);//档号设置中倒数第二个字段的分隔符
            fileArchivecode = innerfileArchivecode.substring(0, innerfileArchivecode.lastIndexOf(codeSettingSplitCode));
        }
        switch (entrytype) {//获取案卷entryid
            case "capture":
                fileEntryid = entryIndexCaptureRepository.findEntryidByArchivecodeAndNodeidIn(fileArchivecode, childrenNodeidArr).get(0);
                break;
            case "access":
                break;
            case "management":
                fileEntryid = entryIndexRepository.findEntryidByArchivecodeAndNodeidIn(fileArchivecode, childrenNodeidArr).get(0);
                break;
        }
        return fileEntryid;
    }

    public void recycleFile(String srcFilePath, String fileName) throws IOException {
        String recyclebinFilePath = srcFilePath.replace("/electronics/storages/", "/electronics/recyclebinElectronic/");
        String srcFileFullPath = rootpath + srcFilePath + "/" + fileName;
        String recyclebinFileFullPath = rootpath + recyclebinFilePath + "/" + fileName;
        File srcFile = new File(srcFileFullPath);
        File recyclebinFile = new File(recyclebinFileFullPath);
        FileUtils.copyFile(srcFile, recyclebinFile);
        srcFile.delete();//删除电子文件
    }

    public void saveCaptureRecyclebinElectronic(Tb_electronic_capture electronic_capture,String entryType) {
        try {
            //原条目的电子文件路径
            String srcFileFullPath = rootpath + electronic_capture.getFilepath() + "/" + electronic_capture.getFilename();
            File srcFile = new File(srcFileFullPath);
            //如果不存在entryid，直接删除电子文件
            if(electronic_capture.getEntryid()==null){
                srcFile.delete();//删除电子文件
            }else{
                //新增的回收站文件路径
                String recyclebinFilePath = getRecyclebinBaseDir(entryType,electronic_capture.getEntryid());
                //第一步，拷贝文件（存在多条同entryid、文件路径、文件名称-主要之前富滇使用旧的日期路径，会存在多条目公用一条文件记录，不删除原来的电子文件，只新增记录）
                String recyclebinFileFullPath = rootpath + recyclebinFilePath + "/" + electronic_capture.getFilename();
                File recyclebinFile = new File(recyclebinFileFullPath);
                //拷贝原条目的电子文件到新的回收站文件路径
                FileUtils.copyFile(srcFile, recyclebinFile);
                //判断原条目的电子文件是否存在多条目公用一条文件,若只有一条关联，则删除原条目的电子文件
                if(electronicCaptureRepository.findByFilepathAndFilename(electronic_capture.getFilepath(),electronic_capture
                        .getFilename()).size()==1){
                    srcFile.delete();//删除电子文件
                }

                //第二步，生成新的回收站记录
                SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                Tb_electronic_recyclebin electronicRecyclebin = new Tb_electronic_recyclebin();
                BeanUtils.copyProperties(electronic_capture, electronicRecyclebin);
//                electronicRecyclebin.setUserid(userDetails.getUserid());
                electronicRecyclebin.setFilepath(recyclebinFilePath);
                electronicRecyclebin.setOriginaltable("tb_electronic_capture");
                electronicRecyclebin.setDeletetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                electronicRecyclebinRepository.save(electronicRecyclebin);

            }
            //判断文件夹是否还有文件，没有则把文件夹删除
            File folder =new File(rootpath + electronic_capture.getFilepath());
            if(folder.listFiles()!=null && folder.listFiles().length==0){
                //删除文件夹
                folder.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveManagementRecyclebinElectronic(Tb_electronic electronic,String entryType) {
        try {
            //原条目的电子文件路径
            String srcFileFullPath = rootpath + electronic.getFilepath() + "/" + electronic.getFilename();
            File srcFile = new File(srcFileFullPath);
            //如果不存在entryid，直接删除电子文件
            if(electronic.getEntryid()==null){
                srcFile.delete();//删除电子文件
            }else{
                //新增的回收站文件路径
                String recyclebinFilePath = getRecyclebinBaseDir(entryType,electronic.getEntryid().replace
                        (" ", ""));

                //第一步，拷贝文件（存在多条同entryid、文件路径、文件名称-主要之前富滇使用旧的日期路径，会存在多条目公用一条文件记录，不删除原来的电子文件，只新增记录）
                String recyclebinFileFullPath = rootpath + recyclebinFilePath + "/" + electronic.getFilename();
                File recyclebinFile = new File(recyclebinFileFullPath);
                //拷贝原条目的电子文件到新的回收站文件路径
                FileUtils.copyFile(srcFile, recyclebinFile);
                //判断原条目的电子文件是否存在多条目公用一条文件,若只有一条关联，则删除原条目的电子文件
                if(electronicRepository.findByFilepathAndFilename(electronic.getFilepath(),electronic.getFilename()).size()==1){
                    srcFile.delete();//删除电子文件
                }

                //第二步，生成新的回收站记录
                SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                Tb_electronic_recyclebin electronicRecyclebin = new Tb_electronic_recyclebin();
                BeanUtils.copyProperties(electronic, electronicRecyclebin);
//                electronicRecyclebin.setUserid(userDetails.getUserid());
                electronicRecyclebin.setFilepath(recyclebinFilePath);
                electronicRecyclebin.setOriginaltable("tb_electronic");
                electronicRecyclebin.setDeletetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                electronicRecyclebinRepository.save(electronicRecyclebin);

            }
            //判断文件夹是否还有文件，没有则把文件夹删除
            File folder =new File(rootpath + electronic.getFilepath());
            if(folder.listFiles()!=null && folder.listFiles().length==0){
                //删除文件夹
                folder.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getByte(InflaterInputStream zis) {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] temp = new byte[1024];
            byte[] buf = null;
            int length = 0;

            while ((length = zis.read(temp, 0, 1024)) != -1) {
                bout.write(temp, 0, length);
            }

            buf = bout.toByteArray();
            bout.close();
            return buf;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 补上 没有固化的文件
     * @param entryid
     * @param extTreeList
     * @param list
     * @return
     */
    public List<ExtTree> findSolidAndOriginal(String entryid, List<ExtTree> extTreeList, List<Map<String, Object>> list,String[] eleids) {
        List<Map<String, Object>> originalList = findElectronics("", entryid, new String[]{"undefined"});
        List<Map<String, Object>> listmap = new ArrayList<>();
        for (Map<String, Object> map : originalList) {
            for(int j=0;j<eleids.length;j++){
                if (map.get("eleid").equals(eleids[j])) {
                    listmap.add(map);
                    break;
                }
            }
        }
        for (Map<String, Object> ol : listmap) {
            boolean found = false;
            String olName = ol.get("filename").toString();
            String ft = olName.substring(olName.lastIndexOf(".") + 1).toLowerCase();
            String[] ftArr = solidifyType.split(",");//{"docx", "doc", "rtf", "txt", "xls", "xlsx", "xlsm", "ppt", "pptx", "pps", "jpg", "jpeg", "png", "tiff", "tif", "html", "pdf"};
            Set<String> ftSet = new HashSet<>();
            ftSet.addAll(new HashSet<>(Arrays.asList(ftArr)));//支持转换的格式集合
            if (ftSet.contains(ft)) {
                for (Map<String, Object> li : list) {
                    String olSubName = olName.substring(0, olName.lastIndexOf(".")) + "_"
                            + olName.substring(olName.lastIndexOf(".") + 1, olName.length()) + ".pdf";
                    String notype = olName.substring(0, olName.lastIndexOf(".")) + ".pdf";
                    if (li.get("filename").equals(notype) || li.get("filename").equals(olSubName)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                ExtTree nodes = new ExtTree();
                nodes.setFnid(ol.get("eleid").toString());
                nodes.setText(ol.get("filename").toString());
                nodes.setLeaf(true);
                extTreeList.add(nodes);
            }
        }

        return extTreeList;
    }

    /**
     * 获取管理表文件中符合固化格式而还没固化的数据
     * @param start
     * @param limit
     * @return
     */
    public List<Tb_electronic> getNotSolidified(int start, int limit, List<String> failList) {
        String[] typeArr = solidifyType.split(",");
        String inType = "";
        for (int i = 0; i < typeArr.length; i++) {
            if (i == 0) {
                inType += "'" + typeArr[i] + "'";
            } else {
                inType += ",'" + typeArr[i] + "'";
            }
        }
        String appendFailStr = "";
        if (failList.size() != 0) {
            String notIn = "";
            String[] failAry = new String[failList.size()];
            failList.toArray(failAry);
            List<String[]> subAry = subArray(failAry, 2);//1000
            for (String[] ary : subAry) {
                for (int i = 0; i < ary.length; i++) {
                    if (i == 0) {
                        notIn += "'" + ary[i] + "'";
                    } else {
                        notIn += ",'" + ary[i] + "'";
                    }
                }
                appendFailStr += " and eleid not in (" + notIn + ")";
                notIn = "";
            }
        }

        String dataSql = "select * from tb_electronic where entryid is not null and eleid not in (" +
                "select e.eleid from tb_electronic e,tb_electronic_solid es where e.entryid=es.entryid and " + DBCompatible.getInstance().subAddPdf() +
                ") and lower(filetype) in (" + inType + ")" + appendFailStr;

        dataSql += " order by eleid asc";
        Query query = entityManager.createNativeQuery(dataSql, Tb_electronic.class);
        query.setFirstResult(start);
        query.setMaxResults(limit);
        return (List<Tb_electronic>) query.getResultList();
    }

    /**
     * 按照limit数量分组
     */
    public List<String[]> subArray(String[] arr, int limit) {
        int arrlen = arr.length;
        int count = arrlen % limit == 0 ? arrlen / limit : arrlen / limit + 1;

        List<List<String>> subAryList = new ArrayList<>();

        for (int i = 0; i < count; i++) {// 分组
            int index = i * limit;
            List<String> list = new ArrayList<>();
            int j = 0;
            while (j < limit && index < arr.length) {
                list.add(arr[index++]);
                j++;
            }
            subAryList.add(list);
        }

        List<String[]> returnList = new ArrayList<>();

        for (int i = 0; i < subAryList.size(); i++) {// 转数组
            List<String> subList = subAryList.get(i);
            String[] subAryItem = new String[subList.size()];
            for (int j = 0; j < subList.size(); j++) {
                subAryItem[j] = subList.get(j);
            }
            returnList.add(subAryItem);
        }

        return returnList;
    }

    /**
     * 获取采集文件中符合固化格式而还没固化的数据
     * @param start
     * @param limit
     * @return
     */
    public List<Tb_electronic_capture> getNotSolidifiedCapture(int start, int limit, List<String> failList) {
        String[] typeArr = solidifyType.split(",");
        String inType = "";
        for (int i = 0; i < typeArr.length; i++) {
            if (i == 0) {
                inType += "'" + typeArr[i] + "'";
            } else {
                inType += ",'" + typeArr[i] + "'";
            }
        }

        String appendFailStr="";
        if(failList.size()!=0){
            String notIn = "";
            for (int i = 0; i <failList.size() ; i++) {
                if (i == 0) {
                    notIn += "'" + failList.get(i) + "'";
                } else {
                    notIn += ",'" + failList.get(i) + "'";
                }
            }
            appendFailStr+=" and eleid not in (" + notIn + ")";
        }
        String dataSql = "select * from tb_electronic_capture where entryid is not null and eleid not in (" +
                "select e.eleid from tb_electronic_capture e,tb_electronic_solid es where e.entryid=es.entryid and " + DBCompatible.getInstance().subAddPdf() +
                ") and lower(filetype) in (" + inType + ")" + appendFailStr;

        dataSql += " order by eleid asc";
        Query query = entityManager.createNativeQuery(dataSql, Tb_electronic_capture.class);
        query.setFirstResult(start);
        query.setMaxResults(limit);
        return (List<Tb_electronic_capture>) query.getResultList();
    }

    /**
     * 原始文件排序
     * @param eleids 需要排序的电子文件
     * @param entryType 数据类型（采集、管理、利用）
     */
    public void mediaFileSort(String[] eleids,String entryType) throws Exception{
        Map<String,Integer> eleMap = new HashMap<>();
        for(int i=0;i<eleids.length;i++){//将id与顺序对应
            eleMap.put(eleids[i],i+1);
        }

        switch (entryType){
            case "capture"://数据采集原始文件
                List<Tb_electronic_capture> eleCaptures = electronicCaptureRepository.findByEleidInOrderBySortsequence(eleids);
                for(Tb_electronic_capture eleCapture:eleCaptures){
                    eleCapture.setSortsequence(eleMap.get(eleCapture.getEleid()));
                }
                break;
            case "solid"://采集、管理固化文件
                List<Tb_electronic_solid> eleSolids = electronicSolidRepository.findByEleidInOrderBySortsequence(eleids);
                for(Tb_electronic_solid eleSolid:eleSolids){
                    eleSolid.setSequence(eleMap.get(eleSolid.getEleid()));
                }
                break;
            default://数据管理原始文件
                List<Tb_electronic> electronics = electronicRepository.findByEleidInOrderBySortsequence(eleids);
                for(Tb_electronic electronic:electronics){
                    electronic.setSortsequence(eleMap.get(electronic.getEleid()));
                }
                break;
        }
    }

    /**
     * 数据采集
     * 把之前未保存的临时电子文件转存到新的存储路径的文件夹（即electronics/storages/年/月/日/capture/条目ID）
     * @param entryType 类型(capture-采集、management-数据管理)
     * @param entryid 条目ID
     * @param eleid 原电子文件ID
     */
    public Tb_entry_detail_capture renameToCapture(String entryType,String entryid,String eleid, String nodeId) {
        Tb_electronic_capture elec = electronicCaptureRepository.findByEleid(eleid);
        Tb_entry_detail_capture returnCapture = new Tb_entry_detail_capture();
        if(elec!=null){
            //获取电子原文文件地址
            File targetFile = new File( rootpath + elec.getFilepath(), elec.getFilename());
            //把电子文件转存到以entryid为最后一层文件夹名称新的文件夹
            targetFile.renameTo(new File(getStorageDir(entryType,entryid),  elec.getFilename()));
            //更新新的文件路径
            elec.setFilepath(getStorageBaseDir(entryType,entryid));
            //获取已经修改过的elect
            Tb_electronic_capture resultElect = electronicCaptureRepository.save(elec);
            //删除原来文件
            targetFile.delete();

            //在这里识别元数据
            Tb_data_node_mdaflag dataNode  = dataNodeExtRepository.findNodeid(nodeId);
            if(dataNode == null ) {
                return null;
            }
            String types = FileUtil.identifyFileType(elec.getFilename());

            String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            int mediaNum = 0;
            if(null != types && types.startsWith("image")){//图片
                returnCapture = (Tb_entry_detail_capture)getMediaDataCapture(elec.getFilename(), resultElect, nowDate, 3).getData();
                mediaNum = 3;
            }
            else if(null != types && types.startsWith("video")){//视频
                returnCapture = (Tb_entry_detail_capture)getMediaDataCapture(elec.getFilename(), resultElect, nowDate, 1).getData();
                mediaNum = 1;
            }
            else if(null != types && types.startsWith("audio")){//音频
                returnCapture = (Tb_entry_detail_capture)getMediaDataCapture(elec.getFilename(), resultElect, nowDate, 2).getData();
                mediaNum = 2;
            }

//            List<Object> list = electronicService.saveElectronic("management", entryid, elec.getFilename(), true, null);
            Tb_electronic_browse eb = new Tb_electronic_browse();
            BeanUtils.copyProperties(resultElect, eb);
            this.compression_Capture(eb, mediaNum);// 压缩
            return returnCapture;
        }
        return null;
    }

    /**
     * 数据管理
     * 把之前未保存的临时电子文件转存到新的存储路径的文件夹（即electronics/storages/年/月/日/management/条目ID）
     * @param entryType 类型(capture-采集、management-数据管理)
     * @param entryid 条目ID
     * @param eleid 原电子文件ID
     */
    public Tb_entry_detail renameToIndex(String entryType,String entryid,String eleid, String nodeId) {
        Tb_electronic elec = electronicRepository.findByEleid(eleid);
        Tb_entry_detail returnDetail = new Tb_entry_detail();
        if(elec!=null){
            //获取电子原文文件地址
            File targetFile = new File( rootpath + elec.getFilepath(), elec.getFilename());
            //把原来的电子文件转存到以entryid为最后一层文件夹名称新的文件夹
            targetFile.renameTo(new File(getStorageDir(entryType,entryid),elec.getFilename())); //false
            //更新新的文件路径
            elec.setFilepath(getStorageBaseDir(entryType,entryid));
            //获取已经修改过的elect
            Tb_electronic resultElect = electronicRepository.save(elec);
            //删除原来文件
            targetFile.delete();
            //在这里识别元数据
            Tb_data_node_mdaflag dataNode  = dataNodeExtRepository.findNodeid(nodeId);
            if(dataNode == null ) {
                return null;
            }
            String types = FileUtil.identifyFileType(elec.getFilename());

            String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            int mediaNum = 0;
            if(null != types && types.startsWith("image")){//图片
                returnDetail = (Tb_entry_detail)getMediaData(elec.getFilename(), resultElect, nowDate, 3).getData();
                mediaNum = 3;
            }
            else if(null != types && types.startsWith("video")){//视频
                returnDetail = (Tb_entry_detail)getMediaData(elec.getFilename(), resultElect, nowDate, 1).getData();
                mediaNum = 1;
            }
            else if(null != types && types.startsWith("audio")){//音频
                returnDetail = (Tb_entry_detail)getMediaData(elec.getFilename(), resultElect, nowDate, 2).getData();
                mediaNum = 2;
            }

//            List<Object> list = electronicService.saveElectronic("management", entryid, elec.getFilename(), true, null);
            Tb_electronic_browse eb = new Tb_electronic_browse();
            BeanUtils.copyProperties(resultElect, eb);
            this.compression(eb, mediaNum);// 压缩
            return returnDetail;
        }
        return null;
    }
    public ExtMsg saveMetadataByTb_electronic(Tb_electronic elec){
        String types = FileUtil.identifyFileType(elec.getFilename());

        String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        if(null != types && types.startsWith("image")){//图片
            return getMediaData(elec.getFilename(), elec, nowDate, 3);
        }
        else if(null != types && types.startsWith("video")){//视频
            return getMediaData(elec.getFilename(), elec, nowDate, 1);
        }
        else if(null != types && types.startsWith("audio")){//音频
            return getMediaData(elec.getFilename(), elec, nowDate, 2);
        }
        return null;
    }

    //字节转换
    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + "/" + units[digitGroups];
    }

    /**
     * 音频元数据存储
     * @param detail_capture
     * @param electronic_capture
     * @return
     */
    public Tb_entry_detail setTbEntryCaptureMusicdata(Tb_entry_detail detail_capture,
                                                      Tb_electronic electronic_capture) {
        MP3AudioHeader header = MetadataUtil.getHead(rootpath + electronic_capture.getFilepath() + File.separator + electronic_capture.getFilename());
        detail_capture.setF08(electronic_capture.getFiletype());//文件格式
        detail_capture.setF16(Integer.parseInt(electronic_capture.getFilesize()) / header.getTrackLength() + "");//平均混合码率=文件大小/时长
        detail_capture.setF13(header.getMpegLayer());//音频编码
        detail_capture.setF11(header.getTrackLengthAsString());//时长
        detail_capture.setF26(header.getChannels());//声道
        detail_capture.setF17(header.getSampleRate());//采样率
        return detail_capture;
    }
    public Tb_entry_detail_capture setTbEntryCaptureMusicdata_Capture(Tb_entry_detail_capture detail_capture,
                                                                      Tb_electronic_capture electronic_capture) {
        MP3AudioHeader header = MetadataUtil.getHead(rootpath + electronic_capture.getFilepath() + File.separator + electronic_capture.getFilename());
        detail_capture.setF08(electronic_capture.getFiletype());//文件格式
        detail_capture.setF16(Integer.parseInt(electronic_capture.getFilesize()) / header.getTrackLength() + "");//平均混合码率=文件大小/时长
        detail_capture.setF13(header.getMpegLayer());//音频编码
        detail_capture.setF11(header.getTrackLengthAsString());//时长
        detail_capture.setF26(header.getChannels());//声道
        detail_capture.setF17(header.getSampleRate());//采样率
        return detail_capture;
    }
    public Tb_entry_detail setTbEntryCaptureMetadata(Tb_entry_detail detail_capture,
                                                     Tb_electronic electronic_capture) {
        Map<String, String> metadataMap = MetadataUtil.getImgMetadata(new File(
                rootpath + electronic_capture.getFilepath() + File.separator + electronic_capture.getFilename()));
        //detail_capture.setF22(metadataMap.get("Exif Image Width") + "x" + metadataMap.get("Exif Image Height"));// 尺寸
        detail_capture.setF19(metadataMap.get("Image Width") == null ? null : metadataMap.get("Image Width").split(" ")[0] + " x " + metadataMap.get("Image Height").split(" ")[0]);// 尺寸
        detail_capture.setF17(metadataMap.get("ISO Speed Ratings"));// ISO
        detail_capture.setF26(metadataMap.get("Model"));// 相机型号
        detail_capture.setF16(metadataMap.get("White Balance Mode"));// 白平衡
        detail_capture.setF13(metadataMap.get("Flash"));// 闪光
        detail_capture.setF11(metadataMap.get("F-Number"));// 光圈
        detail_capture.setF25(metadataMap.get("Make"));// 照相机品牌
        detail_capture.setF18(metadataMap.get("X Resolution") == null ? null : metadataMap.get("X Resolution").split(" ")[0] + "/1" + "," + metadataMap.get("Y Resolution").split(" ")[0] + "/1");// 分辨率
        detail_capture.setF14(metadataMap.get("Metering Mode"));// 测光方式
        detail_capture.setF15(metadataMap.get("Exposure Time"));// 快门速度
        detail_capture.setF12(metadataMap.get("Focal Length"));// 焦距
        detail_capture.setF24(metadataMap.get("GPS Longitude"));// 经度
        detail_capture.setF23(metadataMap.get("GPS Latitude"));// 纬度
        detail_capture.setF10(metadataMap.get("Make"));//镜头
        detail_capture.setF27("ver" + metadataMap.get("Version"));// 拍摄程序
        detail_capture.setF50(metadataMap.get("Date/Time Original")); //拍摄时间
        return detail_capture;
    }

    public Tb_entry_detail_capture setTbEntryCaptureMetadata_Capture(Tb_entry_detail_capture detail_capture,
                                                                     Tb_electronic_capture electronic_capture) {
        Map<String, String> metadataMap = MetadataUtil.getImgMetadata(new File(
                rootpath + electronic_capture.getFilepath() + File.separator + electronic_capture.getFilename()));
        //detail_capture.setF22(metadataMap.get("Exif Image Width") + "x" + metadataMap.get("Exif Image Height"));// 尺寸
        detail_capture.setF19(metadataMap.get("Image Width") == null ? null : metadataMap.get("Image Width").split(" ")[0] + " x " + metadataMap.get("Image Height").split(" ")[0]);// 尺寸
        detail_capture.setF17(metadataMap.get("ISO Speed Ratings"));// ISO
        detail_capture.setF26(metadataMap.get("Model"));// 相机型号
        detail_capture.setF16(metadataMap.get("White Balance Mode"));// 白平衡
        detail_capture.setF13(metadataMap.get("Flash"));// 闪光
        detail_capture.setF11(metadataMap.get("F-Number"));// 光圈
        detail_capture.setF25(metadataMap.get("Make"));// 照相机品牌
        detail_capture.setF18(metadataMap.get("X Resolution") == null ? null : metadataMap.get("X Resolution").split(" ")[0] + "/1" + "," + metadataMap.get("Y Resolution").split(" ")[0] + "/1");// 分辨率
        detail_capture.setF14(metadataMap.get("Metering Mode"));// 测光方式
        detail_capture.setF15(metadataMap.get("Exposure Time"));// 快门速度
        detail_capture.setF12(metadataMap.get("Focal Length"));// 焦距
        detail_capture.setF24(metadataMap.get("GPS Longitude"));// 经度
        detail_capture.setF23(metadataMap.get("GPS Latitude"));// 纬度
        detail_capture.setF10(metadataMap.get("Make"));//镜头
        detail_capture.setF27("ver" + metadataMap.get("Version"));// 拍摄程序
        detail_capture.setF50(metadataMap.get("Date/Time Original")); //拍摄时间
        return detail_capture;
    }

    public ExtMsg getMediaDataCapture(String filename,Tb_electronic_capture electronic_capture,String lastModifiedDate, int mediaNum){
//        int mediaNum = getMediaType(filename.substring(filename.lastIndexOf('.') + 1));
        String entryId = UUID.randomUUID().toString().replace("-", "");
        boolean flag = false;
        Tb_entry_detail_capture detail_capture = new Tb_entry_detail_capture();
        String filesize = readableFileSize(Long.parseLong(electronic_capture.getFilesize()));//存储大小
        String filedate = "";
        if(mediaNum==3){//照片
            this.setTbEntryCaptureMetadata_Capture(detail_capture,electronic_capture);
            detail_capture.setF09(filesize);//存储大小
            if(StringUtils.isEmpty(electronic_capture.getEntryid()))
                detail_capture.setEntryid(entryId);
            else
                detail_capture.setEntryid(electronic_capture.getEntryid());
            detail_capture.setF08(filename.substring(filename.lastIndexOf('.') + 1));//文件格式
            filedate = detail_capture.getF50(); //获取拍摄时间
            if(filedate==null||"".equals(filedate)){
                filedate = lastModifiedDate.replace("T"," ");
            }else{
                filedate = filedate.split(" ")[0];
                filedate = filedate.replace(":","");
            }
            detail_capture.setF50(null);
            flag = true;
        }else if(mediaNum==2){//音频
            this.setTbEntryCaptureMusicdata_Capture(detail_capture,electronic_capture);
            detail_capture.setEntryid(entryId);
            detail_capture.setF09(filesize);//存储大小
            filedate = lastModifiedDate.replace("T"," ");
            flag = true;
        }else if(mediaNum==1){//视频
            try {
                Map<String,String> map = MetadataUtil.getVideoInfos(rootpath + electronic_capture.getFilepath() + File.separator + electronic_capture.getFilename(),compressionToolPath);
                detail_capture.setEntryid(entryId);
                detail_capture.setF08(electronic_capture.getFiletype());//文件格式
                detail_capture.setF15("");//质量比
                detail_capture.setF09(filesize);//存储大小
                detail_capture.setF12(map.get("Frame rate")+"fps");//帧率
                detail_capture.setF11(map.get("Frame size").split(" ")[0]);//画面大小
                detail_capture.setF25("");//帧比例
                detail_capture.setF13(map.get("CodecID").split(" ")[0]+"格式");//视频编码
                detail_capture.setF17(map.get("Duration")+"秒");//时长
                BigDecimal num1 = new BigDecimal(map.get("Frame rate"));
                BigDecimal num2 = new BigDecimal(map.get("Duration"));
                detail_capture.setF16(num1.multiply(num2)+"");//帧数=帧率*时长
                detail_capture.setF10(map.get("code rate")+"Kbps");//码率(也叫比特率)
                detail_capture.setF18(map.get("bitrate"));//平均混合码率
                detail_capture.setF27("");//像素比
                detail_capture.setF14("");//图像比
                filedate = lastModifiedDate.replace("T"," ");
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        return new ExtMsg(flag,filedate,entryDetailCaptureRepository.save(detail_capture));
        return new ExtMsg(flag,filedate,detail_capture);
    }

    public ExtMsg getMediaData(String filename,Tb_electronic electronic_capture,String lastModifiedDate, int mediaNum){
//        int mediaNum = getMediaType(filename.substring(filename.lastIndexOf('.') + 1));
        String entryId = UUID.randomUUID().toString().replace("-", "");
        boolean flag = false;
        Tb_entry_detail detail_capture = new Tb_entry_detail();
        String filesize = readableFileSize(Long.parseLong(electronic_capture.getFilesize()));//存储大小
        String filedate = "";
        if(mediaNum==3){//照片
            this.setTbEntryCaptureMetadata(detail_capture,electronic_capture);
            detail_capture.setF09(filesize);//存储大小
            if(StringUtils.isEmpty(electronic_capture.getEntryid()))
                detail_capture.setEntryid(entryId);
            else
                detail_capture.setEntryid(electronic_capture.getEntryid());
            detail_capture.setF08(filename.substring(filename.lastIndexOf('.') + 1));//文件格式
            filedate = detail_capture.getF50(); //获取拍摄时间
            if(filedate==null||"".equals(filedate)){
                filedate = lastModifiedDate.replace("T"," ");
            }else{
                filedate = filedate.split(" ")[0];
                filedate = filedate.replace(":","");
            }
            detail_capture.setF50(null);
            flag = true;
        }else if(mediaNum==2){//音频
            this.setTbEntryCaptureMusicdata(detail_capture,electronic_capture);
            detail_capture.setEntryid(entryId);
            detail_capture.setF09(filesize);//存储大小
            filedate = lastModifiedDate.replace("T"," ");
            flag = true;
        }else if(mediaNum==1){//视频
            try {
                Map<String,String> map = MetadataUtil.getVideoInfos(rootpath + electronic_capture.getFilepath() + File.separator + electronic_capture.getFilename(),compressionToolPath);
                detail_capture.setEntryid(entryId);
                detail_capture.setF08(electronic_capture.getFiletype());//文件格式
                detail_capture.setF15("");//质量比
                detail_capture.setF09(filesize);//存储大小
                detail_capture.setF12(map.get("Frame rate")+"fps");//帧率
                detail_capture.setF11(map.get("Frame size").split(" ")[0]);//画面大小
                detail_capture.setF25("");//帧比例
                detail_capture.setF13(map.get("CodecID").split(" ")[0]+"格式");//视频编码
                detail_capture.setF17(map.get("Duration")+"秒");//时长
                BigDecimal num1 = new BigDecimal(map.get("Frame rate"));
                BigDecimal num2 = new BigDecimal(map.get("Duration"));
                detail_capture.setF16(num1.multiply(num2)+"");//帧数=帧率*时长
                detail_capture.setF10(map.get("code rate")+"Kbps");//码率(也叫比特率)
                detail_capture.setF18(map.get("bitrate"));//平均混合码率
                detail_capture.setF27("");//像素比
                detail_capture.setF14("");//图像比
                filedate = lastModifiedDate.replace("T"," ");
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        return new ExtMsg(flag,filedate,entryDetailRepository.save(detail_capture));
        return new ExtMsg(flag,filedate,detail_capture);
    }


    /**
     * 移动焦点图
     *
     * @param eleids 节点元素id
     * @return
     */
    public void electronicsFocusChange(String[] eleids) {
        Map<String,Integer> eleMap = new HashMap<>();
        for(int i=0;i<eleids.length;i++){//将id与顺序对应
            eleMap.put(eleids[i],i+1);
        }
        List<Tb_focus> focuss = focusRepository.findByFocusidInOrderBySortsequence(eleids);
        for(Tb_focus focus:focuss){
            focus.setSortsequence(eleMap.get(focus.getId().trim()));
        }
    }

    public Tb_thematic getThematic(String id) {
        return thematicRepository.findByThematicid(id);
    }

    public Tb_thematic_make getThematicMake(String id) {
        return thematicMakeRepository.findByThematicid(id);
    }

    public void uploadchunk(Map<String, Object> param) throws Exception {
        String tempFileName = param.get("filename") + "_tmp";
        File confFile = new File(getUploadDir(), param.get("filename") + ".conf");
        File tmpFile = new File(getUploadDir(), tempFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");

        long offset = chunkSize * parseInt((String) param.get("chunk"));
        //定位到该分片的偏移量
        accessTmpFile.seek(offset);
        //写入该分片数据
        accessTmpFile.write((byte[]) param.get("content"));

        //把该分段标记为 true 表示完成
        accessConfFile.setLength(parseInt((String) param.get("chunks")));
        accessConfFile.seek(parseInt((String) param.get("chunk")));
        accessConfFile.write(Byte.MAX_VALUE);

        //completeList 检查是否全部完成,如果数组里是否全部都是(全部分片都成功上传)
        byte[] completeList = FileUtils.readFileToByteArray(confFile);
        byte isComplete = Byte.MAX_VALUE;
        for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
            //与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
            isComplete = (byte) (isComplete & completeList[i]);
        }

        accessTmpFile.close();
        accessConfFile.close();

        //上传完成，删除临时文件，移动到存储路径
        if (isComplete == Byte.MAX_VALUE) {
            confFile.delete();
            tmpFile.renameTo(new File(getStorageDir(), (String) param.get("filename")));
        }
    }

    public ExtMsg watermarkElectronics(Map<String, Object> param) throws Exception {
        String targetFileName = (String) param.get("filename");
        File tmpFile = new File(getUploadDir(), targetFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        //写入数据
        accessTmpFile.write((byte[]) param.get("content"));
        accessTmpFile.close();
        //文件转存
        File targetFile = new File(getWatermarkDir(), (String) param.get("filename"));
        tmpFile.renameTo(targetFile);
        return new ExtMsg(true, getWatermarkDir().replaceAll(rootpath,"")+"/"+param.get("filename"), null);
    }

    public void uploadchunkBorrow(Map<String, Object> param) throws Exception {
        String tempFileName = param.get("filename") + "_tmp";
        File confFile = new File(getElectronicBorrow(), param.get("filename") + ".conf");
        File tmpFile = new File(getElectronicBorrow(), tempFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");

        long offset = chunkSize * Integer.parseInt((String) param.get("chunk"));
        //定位到该分片的偏移量
        accessTmpFile.seek(offset);
        //写入该分片数据
        accessTmpFile.write((byte[]) param.get("content"));

        //把该分段标记为 true 表示完成
        accessConfFile.setLength(Integer.parseInt((String) param.get("chunks")));
        accessConfFile.seek(Integer.parseInt((String) param.get("chunk")));
        accessConfFile.write(Byte.MAX_VALUE);

        //completeList 检查是否全部完成,如果数组里是否全部都是(全部分片都成功上传)
        byte[] completeList = FileUtils.readFileToByteArray(confFile);
        byte isComplete = Byte.MAX_VALUE;
        for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
            //与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
            isComplete = (byte) (isComplete & completeList[i]);
        }

        accessTmpFile.close();
        accessConfFile.close();
        // 上传完成，删除临时文件
        if (isComplete == Byte.MAX_VALUE) {
            confFile.delete();
            tmpFile.renameTo(new File(getTemporaryThematic(), (String) param
                    .get("filename")));
        }
    }

    //查档电子文件临时存放路径
    private String getElectronicBorrow() {
        String dir = "";
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dir = rootpath + "/borrow/temporary/" + userDetails.getUsername();
        File upDir = new File(dir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }
        return dir;
    }

    /**
     * 查档申请文件上传
     *
     * @param param 图片对象
     * @throws Exception
     */
    public void uploadfileBorrow(Map<String, Object> param) throws Exception {
        String targetFileName = (String) param.get("filename");
        File tmpFile = new File(getElectronicBorrow(), targetFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        //写入数据
        accessTmpFile.write((byte[]) param.get("content"));
        accessTmpFile.close();
    }

    public Map<String, Object> saveZtElectronicBorrow(String entrytype, String entryid, String filename) {
        File targetFile = new File(getElectronicBorrow(), filename);
        //计算文件的MD5值
        FileInputStream fis = null;
        StringBuffer md5 = new StringBuffer();
        try {
            fis = new FileInputStream(targetFile);
            md5.append(DigestUtils.md5Hex(fis));
            fis.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        Map<String, Object> map = new HashMap<>();
        Tb_electronic ele = new Tb_electronic();
        ele.setFilename(filename);
        ele.setFilesize(String.valueOf(targetFile.length()));
        ele.setFilepath(getElectronicBorrow().replace(rootpath, ""));
        ele.setFiletype(filename.substring(filename.lastIndexOf('.') + 1));
        ele.setMd5(md5.toString());
        ele = electronicRepository.save(ele);//保存电子文件
        map = ele.getMap();
        return map;
    }

    //审批上传附件
    public Map<String, Object> saveZtElectronicBorrowApprove( String borrowcode, String filename) {
        File targetFile = new File(getElectronicBorrow(), filename);
        //计算文件的MD5值
        FileInputStream fis = null;
        StringBuffer md5 = new StringBuffer();
        try {
            fis = new FileInputStream(targetFile);
            md5.append(DigestUtils.md5Hex(fis));
            fis.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        // 获取新的存储电子文件路径
        String filepath = getUploadDirBorrow(borrowcode).replace(rootpath, "");
        // 把之前原来电子文件转存到存储路径
        targetFile.renameTo(new File(rootpath + filepath, filename));
        // 转存完成后删除原来的文件
        targetFile.delete();
        Map<String, Object> map = new HashMap<>();
        Tb_electronic ele = new Tb_electronic();
        ele.setFilename(filename);
        ele.setFilesize(String.valueOf(targetFile.length()));
        ele.setFilepath(filepath);
        ele.setFiletype(filename.substring(filename.lastIndexOf('.') + 1));
        ele.setMd5(md5.toString());
        ele.setEntryid(borrowcode);
        ele = electronicRepository.save(ele);//保存电子文件
        Tb_borrowdoc borrowdoc = borrowDocRepository.findByBorrowcode(borrowcode);
        borrowdoc.setEvidencetext(borrowdoc.getEvidencetext()+","+filename); //更新附件
        map = ele.getMap();
        return map;
    }

    //证明文件存储存放路径
    public String getUploadDirBorrow(String borrowcode) {
        String dir = "";
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dir = rootpath + "/Borrow/evidencetext/" + userDetails.getUsername()+"/"+borrowcode.trim();
        File upDir = new File(dir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }
        return dir;
    }

    //电子文件版本存储存放路径
    public String getUploadDirSaveVersion(String version,String eleid,String savetype) {
        String dir = "";
        if("capture".equals(savetype)){
            dir = rootpath + "/electronics/saveVersion/capture/" +eleid+"/"+ version;
        }else{
            dir = rootpath + "/electronics/saveVersion/management/" +eleid+"/"+ version;
        }
        File upDir = new File(dir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }
        return dir;
    }

    //电子文件临时存储存放路径
    public String getUploadDirSaveEle() {
        String dir = rootpath+File.separator+"electronics"+File.separator+"saveVersion"+File.separator+"temporary";
        File upDir = new File(dir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }
        return dir;
    }

    public Tb_electronic_version findElectronicVersion(String eleversionid) {
        return electronicVersionRepository.findById(eleversionid);
    }

    public Tb_electronic_version_capture findEleCaptureVersion(String eleversionid) {
        return electronicVersionCaptureRepository.findById(eleversionid);
    }
    /*
     * 获取电子文件记录
     */
    public Page<Tb_electronic> getElectronics(int page, int limit) {
        PageRequest pageRequest = new PageRequest(page - 1, limit, new Sort("sortsequence"));
//        Page<Tb_electronic> epage = electronicRepository.findAll(pageRequest);
//        List<Tb_electronic> elist = epage.getContent();
        String sql = "select * from tb_electronic";
        String countSql = "select count(*) from tb_electronic";
        Query query = entityManager.createNativeQuery(sql,Tb_electronic.class);
        query.setFirstResult((page - 1) * limit);
        query.setMaxResults(limit);
        List<Tb_electronic> resultList = query.getResultList();
        for(Tb_electronic e:resultList){
          Tb_electronic_solid electronic_solid = electronicSolidRepository.findByElectronicid(e.getEleid());
          if(electronic_solid != null){
              e.setSolid(electronic_solid.getEleid());
          }
        }
        Query countQuery = entityManager.createNativeQuery(countSql);
        int count = Integer.parseInt(countQuery.getResultList().get(0) + "");
        return new PageImpl(resultList, pageRequest, count);
    }

    private CopyOnWriteArrayList<Tb_electronic> safeList(){
        List<Tb_electronic> tb_eles = electronicRepository.findAll();
        CopyOnWriteArrayList<Tb_electronic> list = new CopyOnWriteArrayList<Tb_electronic>();
        for(Tb_electronic tb_e : tb_eles){
            list.add(tb_e);
        }
        return list;
    }

    /**
     * 巡查入口
     * @return   0为未巡查过，1位巡查中，2 为巡查过
     */

    public Integer checkFile() throws IOException{
//        tb_electList = (CopyOnWriteArrayList)electronicRepository.findAll();
        tb_electList = safeList();
        if(resultMap == null)
            resultMap = new ConcurrentHashMap<String, String>();
        //创建一个线程在后台跑任务
        Thread fileCheckThread = new Thread() {
            @Override
            public void run() {
                synchronized (mainStatus) {
                    if(mainStatus != 1 ) {
                        mainStatus = 1;
                        lastCheckDate = new Date();
                        for (int i = 0, len = tb_electList.size(); i < len; i++) {
                            Tb_electronic elect = tb_electList.get(i);
                            File file = new File(rootpath + elect.getFilepath() + "/" + elect.getFilename());
                            if (file == null)
                                resultMap.put(elect.getEleid(), "0");
                            else {
                                try {
                                    FileInputStream fis = new FileInputStream(file);
                                    String md5 = DigestUtils.md5Hex(fis);
                                    if (!md5.equals(elect.getMd5())) {
                                        resultMap.put(elect.getEleid(), "1");//文件被更新
                                    }
                                } catch (FileNotFoundException e1) {
                                    e1.printStackTrace();
                                    resultMap.put(elect.getEleid(), "0");//文件不存在
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                }
                            }
//                            try {
//                                sleep(100);
//                            }catch(InterruptedException e4){
//                                e4.printStackTrace();
//
//                            }
                            if(finshNum < tb_electList.size())
                                finshNum++;
                            if(finshNum >= tb_electList.size()) {
                                mainStatus = 2;
                            }
                        }

                    }
                }

            }
        };
        if(mainStatus != 1){
            finshNum = 0;
            fileCheckThread.start();
            System.out.println("fileCheckThread is getting start !");
        }

        return mainStatus;
    }
    //定时器获取的数据
    public Map<String, Object> getFileCheckResult(){
        Map<String, Object> searchResultMap = new HashMap<String, Object>();
        double schedule = 0;
        if(tb_electList != null && tb_electList.size() != 0)    //防止除数为0
            schedule = (double)finshNum/(double)tb_electList.size();
        searchResultMap.put("scheduleRate", schedule);//进度条
//        searchResultMap.put("resultMap", resultMap);  //已检索的结果集
        searchResultMap.put("lastCheckDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastCheckDate));//最后巡查日期
        return searchResultMap;
    }

    //返回当前页的结果巡查集
    public Map<String, Object> getDataMap(int currentPage,int pageSize) {
        if(resultMap == null || resultMap.size() == 0)
            return null;
        Page<Tb_electronic> pg = this.getElectronics(currentPage, pageSize);
        List<Tb_electronic> elePageList = pg.getContent();
        Map<String, Object> dataMap = new ConcurrentHashMap<>();
        for(int i = 0, len = elePageList.size(); i < len; i++){
            if(resultMap.containsKey(elePageList.get(i).getEleid()))
                dataMap.put(elePageList.get(i).getEleid(),resultMap.get(elePageList.get(i).getEleid()));
        }
        return dataMap;
    }

    //判断是否在查询中
    public Integer isChecking() {
        if(lastCheckDate != null){
            long nd = 1000 * 24 * 60 * 60;
            long diff = new Date().getTime() - lastCheckDate.getTime();
            long day = diff / nd;
            if(day > 3)
                mainStatus = 0;
        }
        return this.mainStatus;
    }
//
//    /**
//     * 采用指定宽度、高度或压缩比例 的方式对图片进行压缩
//     * @param imgsrc 源图片地址
//     * @param widthdist 压缩后图片宽度（当rate==null时，必传）
//     * @param heightdist 压缩后图片高度（当rate==null时，必传）
//     * @param rate 压缩比例
//     */
//    private  boolean reduceImg(String imgsrc,String destImg,int widthdist, int heightdist, Float rate) {
//        try {
//            ImageIO.setUseCache(false);
//            ImageIO.setCacheDirectory(new File("E:\\log"));
//            File srcfile = new File(imgsrc);
//            // 检查文件是否存在
//            if (!srcfile.exists()) {
//                return false;
//            }
//            // 如果rate不为空说明是按比例压缩
//            if (rate != null && rate > 0) {
//                // 获取文件高度和宽度
//                long[] results = getImgWidth(srcfile);
//                if (results == null || results[0] == 0 || results[1] == 0) {
//                    return false;
//                } else {
//                    widthdist = (int) (results[0] * rate);
//                    heightdist = (int) (results[1] * rate);
//                }
//            }
//            // 开始读取文件并进行压缩
//            Image src = javax.imageio.ImageIO.read(srcfile);
//            BufferedImage tag = new BufferedImage((int) widthdist,
//                    (int) heightdist, BufferedImage.TYPE_INT_RGB);
//
//            tag.getGraphics().drawImage(
//                    src.getScaledInstance(widthdist, heightdist,
//                            Image.SCALE_SMOOTH), 0, 0, null);
//
//            FileOutputStream out = new FileOutputStream(destImg);
//            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//            encoder.encode(tag);
//            out.close();
//            return true;
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * 获取图片宽度
//     *
//     * @param file
//     *            图片文件
//     * @return 宽度
//     */
//    public static long[] getImgWidth(File file) {
//        InputStream is = null;
//        BufferedImage src = null;
//        long result[] = { 0, 0 };
//        try {
//            is = new FileInputStream(file);
//            src = javax.imageio.ImageIO.read(is);
//            result[0] = src.getWidth(null); // 得到源图宽
//            result[1] = src.getHeight(null); // 得到源图高
//            is.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

    public List<Map<String, Object>> findSzhElectronics(String entryid) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Szh_electronic_capture> list = new ArrayList<>();
        list = szhElectronicCaptureRepository.findByEntryidOrderByFilename(entryid);
        for (Szh_electronic_capture ele : list) {
            result.add(ele.getMap());
        }
        return result;
    }

    public void uploadOfflineChunk(Map<String, Object> param) throws Exception {
        String tempFileName = (String)param.get("filename");
        File confFile = new File(getUploadOfflineDir(), param.get("filename") + ".conf");
        File tmpFile = new File(getUploadOfflineDir(), tempFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");

        long offset = chunkSize * Integer.parseInt((String) param.get("chunk"));
        //定位到该分片的偏移量
        accessTmpFile.seek(offset);
        //写入该分片数据
        accessTmpFile.write((byte[]) param.get("content"));

        //把该分段标记为 true 表示完成
        accessConfFile.setLength(Integer.parseInt((String) param.get("chunks")));
        accessConfFile.seek(Integer.parseInt((String) param.get("chunk")));
        accessConfFile.write(Byte.MAX_VALUE);

        //completeList 检查是否全部完成,如果数组里是否全部都是(全部分片都成功上传)
        byte[] completeList = FileUtils.readFileToByteArray(confFile);
        byte isComplete = Byte.MAX_VALUE;
        for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
            //与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
            isComplete = (byte) (isComplete & completeList[i]);
        }

        accessTmpFile.close();
        accessConfFile.close();

        //上传完成，删除临时文件，移动到存储路径
        if (isComplete == Byte.MAX_VALUE) {
            confFile.delete();
        }
    }

    public void uploadOfflineFile(Map<String, Object> param) throws Exception {
        String targetFileName = (String) param.get("filename");
        File tmpFile = new File(getUploadOfflineDir(), targetFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        //写入数据
        accessTmpFile.write((byte[]) param.get("content"));
        accessTmpFile.close();
    }

    public String getUploadOfflineDir() {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        uploaddir = rootpath + "/electronics/offlineAccession/" + userDetails.getUsername();
        File upDir = new File(uploaddir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }
        return uploaddir;
    }

    public void compression(Tb_electronic_browse elebrowse, int mediaNum) {
//        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String path = elebrowse.getFilepath() + File.separator + elebrowse.getFilename();
        String url;
        String filename = elebrowse.getFilename();
        if (mediaNum == 2) {// 考虑是否加个判断视频的
            url = "/static/img/defaultMedia/default_audio.png";
        } else {
            url = "/thumbnail" + path.substring(0, path.lastIndexOf(".")) + ".jpg";
            File thumDir = new File(browsepath + url).getParentFile();
            if (!thumDir.exists()) {// 创建文件夹，防止下面生成文件不成功
                thumDir.mkdirs();
            }
        }

        electronicBrowseRepository.deleteByEntryid(elebrowse.getEntryid());
        thumbnailRepository.deleteByEntryid(elebrowse.getEntryid());

        Thread thread = new Thread(() -> {
            if (mediaCompressionService.process(path, mediaNum,filename)) {
                if (elebrowse.getEntryid() != null) {// 修改
                    List<Tb_electronic> elecList = electronicRepository
                            .findByEntryid(elebrowse.getEntryid());
                    if (elecList != null && elecList.size() > 0 &&!elebrowse.getEleid().equals(elecList.get(0).getEleid())) {// 此进程不是最新的，无效进程
                        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        // 删除一些文件//todo
                        return;
                    }
                } else {// 新增
                    Tb_electronic ec = electronicRepository.findByEleid(elebrowse.getEleid());
                    if (ec.getEntryid() == null) {// 未保存条目
                        System.out.println(33);// todo:如何判断新增中的无效进程？
                    } else {// 保存了条目
                        System.out.println(44);
                    }
                }
                if (mediaNum == 1) {// 视频
                    elebrowse.setFiletype("flv");
                    elebrowse.setFilename(
                            elebrowse.getFilename().substring(0, elebrowse.getFilename().lastIndexOf(".")) + ".flv");

                    String combinedPath = browsepath + "/thumbnail" + path.substring(0, path.lastIndexOf("."))
                            + "_combined.jpg";
                    File tempDir = new File(
                            browsepath + "/thumbnail" + path.substring(0, path.lastIndexOf("/")) + "/temp/"+filename+File.separator);
                    File[] tempList = tempDir.listFiles();
                    File[] tList = tempList.length==1? tempList[0].listFiles():tempList;
                    //合并视频图片--拼成长图--替换缩略图
                    combineImg(tList, combinedPath);
                    if (tList != null) {
                        for (File file : tempList) {
                            file.delete();
                        }
                        if(tempDir.exists()){
                            try {
                                FileUtils.deleteDirectory(tempDir);  //删除临时文件夹
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                Tb_thumbnail thumbnail = new Tb_thumbnail(elebrowse.getEleid(),
                        elebrowse.getEntryid(), url.substring(url.lastIndexOf(File.separator) + 1, url.length()), url, "capture");
                // Tb_thumbnail thumbnail = new
                // Tb_thumbnail(elebrowse.getEleid(), elebrowse.getEntryid(),
                // elebrowse.getFilename().substring(0,
                // elebrowse.getFilename().lastIndexOf(".")) + ".jpg", url,
                // "capture");
                Tb_electronic ec = electronicRepository.findByEleid(elebrowse.getEleid());
                // System.out.println(ec.getEleid()+"----------"+ec.getEntryid());
                if (ec != null && elebrowse.getEntryid() == null) {// 新增且已保存条目：等到压缩完，若条目已经保存，则直接将临时移到正式文件夹；
                    String baseDir = getStorageBaseDir("capture", ec.getEntryid());
                    File eleDir = new File(browsepath + "/browse" + baseDir);
                    if (!eleDir.exists()) {
                        eleDir.mkdirs();
                    }
                    File targetFile = new File(browsepath + "/browse" + elebrowse.getFilepath(),
                            elebrowse.getFilename());
                    targetFile.renameTo(new File(browsepath + "/browse" + baseDir, elebrowse.getFilename()));
                    targetFile.delete();
                    targetFile.getParentFile().delete();
                    elebrowse.setFilepath("/browse" + baseDir);
                    elebrowse.setEntryid(ec.getEntryid());

                    File dir = new File(browsepath + "/thumbnail" + baseDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    // 移动合成图
                    targetFile = new File(browsepath
                            + thumbnail.getUrl().substring(0, thumbnail.getUrl().lastIndexOf(".")) + "_combined.jpg");
                    targetFile.renameTo(new File(browsepath + "/thumbnail" + baseDir + "/"
                            + thumbnail.getName().substring(0, thumbnail.getName().lastIndexOf("."))
                            + "_combined.jpg"));
                    targetFile.delete();
                    targetFile.getParentFile().delete();

                    // 移动缩略图
                    targetFile = new File(browsepath + thumbnail.getUrl());
                    targetFile.renameTo(new File(browsepath + "/thumbnail" + baseDir + "/" + thumbnail.getName()));
                    targetFile.delete();
                    targetFile.getParentFile().delete();
                    thumbnail.setUrl("/thumbnail" + baseDir + "/" + thumbnail.getName());
                    thumbnail.setEntryid(ec.getEntryid());
                } else {// 新增且未保存条目；修改
                    elebrowse.setFilepath("/browse" + elebrowse.getFilepath());
                }
                String waterFilePath = "";
                String sourceImg =
                        browsepath + File.separator + elebrowse.getFilepath() + File.separator + elebrowse.getFilename().substring(0,elebrowse.getFilename().lastIndexOf("."))+".jpg";
                //不加水印了，直接保存
                electronicBrowseRepository.save(elebrowse);
                thumbnailRepository.save(thumbnail);

//                if (entry != null && saveType != null && "saveEntry".equals(saveType)) {  //批量上传保存条目
//                    msg = saveEntry(entry, type, operate, isMedia, user.getRealname(),labarr);
//                }

//                if (ec.getEntryid() == null || elebrowse.getEntryid() != null) {
//                    // if (mediaNum == 2) {
//                    if (!msg.isSuccess() && "档号重复".equals(msg.getMsg())) { //档号重复
//                        webSocketService.refreshVideo(user.getUserid(), mediaServerPath + elebrowse.getFilepath() + ","
//                                + elebrowse.getFilename() + "," + currentMD5 + ",respeatCode");// 通知刷新
//                    } else if (!msg.isSuccess()) { //保存失败
//                        webSocketService.refreshVideo(user.getUserid(), mediaServerPath + elebrowse.getFilepath() + ","
//                                + elebrowse.getFilename() + "," + currentMD5 + ",no");// 通知刷新
//                    } else {
//                        webSocketService.refreshVideo(user.getUserid(), mediaServerPath + elebrowse.getFilepath() + ","
//                                + elebrowse.getFilename() + "," + currentMD5 + ",yes");// 通知刷新
//                    }
//                    // }else{
//                    // webSocketService.refreshVideo(user.getUserid(),elebrowse.getEleid()+","+currentMD5);//通知刷新
//                    // }
//                }
            }
        });
        thread.setName(System.currentTimeMillis() + "");
        thread.start();
    }

    public void compression_Capture(Tb_electronic_browse elebrowse, int mediaNum) {
//        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String path = elebrowse.getFilepath() + File.separator + elebrowse.getFilename();
        String url;
        String filename = elebrowse.getFilename();
        if (mediaNum == 2) {// 考虑是否加个判断视频的
            url = "/static/img/defaultMedia/default_audio.png";
        } else {
            url = "/thumbnail" + path.substring(0, path.lastIndexOf(".")) + ".jpg";
            File thumDir = new File(browsepath + url).getParentFile();
            if (!thumDir.exists()) {// 创建文件夹，防止下面生成文件不成功
                thumDir.mkdirs();
            }
        }
        electronicBrowseRepository.deleteByEntryid(elebrowse.getEntryid());
        thumbnailRepository.deleteByEntryid(elebrowse.getEntryid());
        Thread thread = new Thread(() -> {
            if (mediaCompressionService.process(path, mediaNum,filename)) {
                if (elebrowse.getEntryid() != null) {// 修改
                    List<Tb_electronic_capture> elecList = electronicCaptureRepository
                            .findByEntryid(elebrowse.getEntryid());
                    if (elecList != null && elecList.size() > 0 &&!elebrowse.getEleid().equals(elecList.get(0).getEleid())) {// 此进程不是最新的，无效进程
                        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        // 删除一些文件//todo
                        return;
                    }
                } else {// 新增
                    Tb_electronic_capture ec = electronicCaptureRepository.findByEleid(elebrowse.getEleid());
                    if (ec.getEntryid() == null) {// 未保存条目
                        System.out.println(33);// todo:如何判断新增中的无效进程？
                    } else {// 保存了条目
                        System.out.println(44);
                    }
                }
                if (mediaNum == 1) {// 视频
                    elebrowse.setFiletype("flv");
                    elebrowse.setFilename(
                            elebrowse.getFilename().substring(0, elebrowse.getFilename().lastIndexOf(".")) + ".flv");

                    String combinedPath = browsepath + "/thumbnail" + path.substring(0, path.lastIndexOf("."))
                            + "_combined.jpg";
                    File tempDir = new File(
                            browsepath + "/thumbnail" + path.substring(0, path.lastIndexOf("/")) + "/temp/"+filename+File.separator);
                    File[] tempList = tempDir.listFiles();
                    File[] tList = tempList.length==1? tempList[0].listFiles():tempList;
                    //合并视频图片--拼成长图--替换缩略图
                    combineImg(tList, combinedPath);
                    if (tList != null) {
                        for (File file : tempList) {
                            file.delete();
                        }
                        if(tempDir.exists()){
                            try {
                                FileUtils.deleteDirectory(tempDir);  //删除临时文件夹
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                Tb_thumbnail thumbnail = new Tb_thumbnail(elebrowse.getEleid(),
                        elebrowse.getEntryid(), url.substring(url.lastIndexOf(File.separator) + 1, url.length()), url, "capture");
                // Tb_thumbnail thumbnail = new
                // Tb_thumbnail(elebrowse.getEleid(), elebrowse.getEntryid(),
                // elebrowse.getFilename().substring(0,
                // elebrowse.getFilename().lastIndexOf(".")) + ".jpg", url,
                // "capture");
                Tb_electronic_capture ec = electronicCaptureRepository.findByEleid(elebrowse.getEleid());
                // System.out.println(ec.getEleid()+"----------"+ec.getEntryid());
                if (ec != null && elebrowse.getEntryid() == null) {// 新增且已保存条目：等到压缩完，若条目已经保存，则直接将临时移到正式文件夹；
                    String baseDir = getStorageBaseDir("capture", ec.getEntryid());
                    File eleDir = new File(browsepath + "/browse" + baseDir);
                    if (!eleDir.exists()) {
                        eleDir.mkdirs();
                    }
                    File targetFile = new File(browsepath + "/browse" + elebrowse.getFilepath(),
                            elebrowse.getFilename());
                    targetFile.renameTo(new File(browsepath + "/browse" + baseDir, elebrowse.getFilename()));
                    targetFile.delete();
                    targetFile.getParentFile().delete();
                    elebrowse.setFilepath("/browse" + baseDir);
                    elebrowse.setEntryid(ec.getEntryid());

                    File dir = new File(browsepath + "/thumbnail" + baseDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    // 移动合成图
                    targetFile = new File(browsepath
                            + thumbnail.getUrl().substring(0, thumbnail.getUrl().lastIndexOf(".")) + "_combined.jpg");
                    targetFile.renameTo(new File(browsepath + "/thumbnail" + baseDir + "/"
                            + thumbnail.getName().substring(0, thumbnail.getName().lastIndexOf("."))
                            + "_combined.jpg"));
                    targetFile.delete();
                    targetFile.getParentFile().delete();

                    // 移动缩略图
                    targetFile = new File(browsepath + thumbnail.getUrl());
                    targetFile.renameTo(new File(browsepath + "/thumbnail" + baseDir + "/" + thumbnail.getName()));
                    targetFile.delete();
                    targetFile.getParentFile().delete();
                    thumbnail.setUrl("/thumbnail" + baseDir + "/" + thumbnail.getName());
                    thumbnail.setEntryid(ec.getEntryid());
                } else {// 新增且未保存条目；修改
                    elebrowse.setFilepath("/browse" + elebrowse.getFilepath());
                }
                String waterFilePath = "";
                String sourceImg =
                        browsepath + File.separator + elebrowse.getFilepath() + File.separator + elebrowse.getFilename().substring(0,elebrowse.getFilename().lastIndexOf("."))+".jpg";
                //不加水印了，直接保存
                electronicBrowseRepository.save(elebrowse);
                thumbnailRepository.save(thumbnail);

//                if (entry != null && saveType != null && "saveEntry".equals(saveType)) {  //批量上传保存条目
//                    msg = saveEntry(entry, type, operate, isMedia, user.getRealname(),labarr);
//                }

//                if (ec.getEntryid() == null || elebrowse.getEntryid() != null) {
//                    // if (mediaNum == 2) {
//                    if (!msg.isSuccess() && "档号重复".equals(msg.getMsg())) { //档号重复
//                        webSocketService.refreshVideo(user.getUserid(), mediaServerPath + elebrowse.getFilepath() + ","
//                                + elebrowse.getFilename() + "," + currentMD5 + ",respeatCode");// 通知刷新
//                    } else if (!msg.isSuccess()) { //保存失败
//                        webSocketService.refreshVideo(user.getUserid(), mediaServerPath + elebrowse.getFilepath() + ","
//                                + elebrowse.getFilename() + "," + currentMD5 + ",no");// 通知刷新
//                    } else {
//                        webSocketService.refreshVideo(user.getUserid(), mediaServerPath + elebrowse.getFilepath() + ","
//                                + elebrowse.getFilename() + "," + currentMD5 + ",yes");// 通知刷新
//                    }
//                    // }else{
//                    // webSocketService.refreshVideo(user.getUserid(),elebrowse.getEleid()+","+currentMD5);//通知刷新
//                    // }
//                }
            }
        });
        thread.setName(System.currentTimeMillis() + "");
        thread.start();
    }

    public static void combineImg(File[] files, String path) {
        try {
            Integer allWidth = 0;
            Integer allHeight = 0;
            List<BufferedImage> imgs = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                imgs.add(ImageIO.read(files[i]));
                if (i == 0) {
                    allWidth = imgs.get(0).getWidth();
                }
                allHeight += imgs.get(i).getHeight();
            }
            BufferedImage bi = new BufferedImage(allWidth, allHeight, BufferedImage.TYPE_INT_RGB);
            Graphics gph = bi.getGraphics();
            Integer height = 0;
            for (int i = 0; i < imgs.size(); i++) {
                gph.drawImage(imgs.get(i), 0, height, null);
                height += imgs.get(i).getHeight();
            }
            ImageIO.write(bi, "jpg", new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //电子版本文件临时存储存放路径
    public String getUploadDirSaveTVersionEle(String entrytype,String entryid) {
        String dir = rootpath+File.separator+"electronics"+File.separator+"saveVersion"+File.separator+"temporary"+File.separator+entrytype+File.separator+entryid;
        File upDir = new File(dir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }
        return dir;
    }

    public void saveTVersion(String filepathold,String filename,String entrytype,String entryid){
        File targetFile = new File(rootpath + filepathold, filename);
        // 获取新的存储电子文件路径
        String filepath = getUploadDirSaveTVersionEle(entrytype,entryid);
        // 把之前原来电子文件复制到存储路径
        File newFile = new File(filepath, filename);
        try {
            FileUtils.copyFile(targetFile,newFile);
            targetFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadchunkSupervisionWork(Map<String, Object> param) throws Exception {
        String tempFileName = param.get("filename") + "_tmp";
        File confFile = new File(getElectronicSupervisionWorkDir(), param.get("filename") + ".conf");
        File tmpFile = new File(getElectronicSupervisionWorkDir(), tempFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");

        long offset = chunkSize * Integer.parseInt((String) param.get("chunk"));
        //定位到该分片的偏移量
        accessTmpFile.seek(offset);
        //写入该分片数据
        accessTmpFile.write((byte[]) param.get("content"));

        //把该分段标记为 true 表示完成
        accessConfFile.setLength(Integer.parseInt((String) param.get("chunks")));
        accessConfFile.seek(Integer.parseInt((String) param.get("chunk")));
        accessConfFile.write(Byte.MAX_VALUE);

        //completeList 检查是否全部完成,如果数组里是否全部都是(全部分片都成功上传)
        byte[] completeList = FileUtils.readFileToByteArray(confFile);
        byte isComplete = Byte.MAX_VALUE;
        for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
            //与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
            isComplete = (byte) (isComplete & completeList[i]);
        }

        accessTmpFile.close();
        accessConfFile.close();
        // 上传完成，删除临时文件
        if (isComplete == Byte.MAX_VALUE) {
            confFile.delete();
            tmpFile.renameTo(new File(getElectronicSupervisionWorkDir(), (String) param
                    .get("filename")));
        }
    }

    /**
     * 工作监督管理文件上传
     *
     * @param param 图片对象
     * @throws Exception
     */
    public void uploadfileSupervisionWork(Map<String, Object> param) throws Exception {
        String targetFileName = (String) param.get("filename");
        File tmpFile = new File(getElectronicSupervisionWorkDir(), targetFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        //写入数据
        accessTmpFile.write((byte[]) param.get("content"));
        accessTmpFile.close();
    }

    //工作监督管理电子文件临时存放路径
    private String getElectronicSupervisionWorkDir() {
        String dir = "";
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dir = rootpath + "/supervisionWork/temporary/" + userDetails.getUsername();
        File upDir = new File(dir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }
        return dir;
    }

    //工作监督管理电子文件存放路径
    private String getSupervisionWorkDir(String organid, String selectyear,String savetype) {
        String dir = "";
        dir = rootpath + "/supervisionWork/storages/" + selectyear+"/"+savetype+"/"+organid;
        File upDir = new File(dir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }
        return dir;
    }

    public Map<String, Object> saveElectronicSupervisionWork(String organid, String selectyear,String savetype, String filename) {
        File targetFile = new File(getElectronicSupervisionWorkDir(), filename);
        //计算文件的MD5值
        FileInputStream fis = null;
        StringBuffer md5 = new StringBuffer();
        try {
            fis = new FileInputStream(targetFile);
            md5.append(DigestUtils.md5Hex(fis));
            fis.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        File saveFile = new File(getSupervisionWorkDir(organid,selectyear,savetype),filename);
        targetFile.renameTo(saveFile);
        Map<String, Object> map = new HashMap<>();
        Tb_supervision_electronic ele = new Tb_supervision_electronic();
        ele.setFilename(filename);
        ele.setFilesize(String.valueOf(saveFile.length()));
        ele.setFilepath(getSupervisionWorkDir(organid,selectyear,savetype).replace(rootpath, ""));
        ele.setFiletype(filename.substring(filename.lastIndexOf('.') + 1));
        ele.setMd5(md5.toString());
        ele.setOrganid(organid);
        ele.setSelectyear(selectyear);
        ele.setSavetype(savetype);
        ele = supervisionElectronicRepository.save(ele);//保存电子文件
        map = ele.getMap();
        return map;
    }

    public List<Map<String, Object>> findElectronicSupervisionWorks(String organid, String selectyear, String savetype) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(new Sort.Order(Sort.Direction.ASC,"sortsequence"));//默认字段排序
        sorts.add(new Sort.Order(Sort.Direction.ASC,"filename"));//文件名排序
        List<Tb_supervision_electronic> listc = supervisionElectronicRepository.findByOrganidAndSelectyearAndSavetype(organid,selectyear,savetype,new Sort(sorts));
        for (Tb_supervision_electronic ele : listc) {
            result.add(ele.getMap());
        }
        return result;
    }

    /**
     * 删除工作监督管理电子文件
     *
     * @param eleids    电子文件id
     * @return
     */
    public Integer deleteElectronicSupervisionWork(String eleids) {
        String[] eleidArray = eleids.split(",");
        Integer num = 0;
        List<Tb_supervision_electronic> electronics = supervisionElectronicRepository.findByEleidInOrderBySortsequence(eleidArray);//获取删除电子文件
        for (Tb_supervision_electronic electronic : electronics) {
            File file = new File(rootpath + electronic.getFilepath() + "/" + electronic.getFilename());
            file.delete();//删除电子文件
        }
        num = supervisionElectronicRepository.deleteByEleidIn(eleidArray);//删除条目
        return num;
    }

    /**
     * 按文件名排序电子文件
     * @param entryid 条目id
     * @param eleids 电子文件id
     * @return
     */
    public void updateSortElectronics(String entryid,String entrytype, String[] eleids) {
        switch (entrytype) {
            case "capture":
                List<Tb_electronic_capture> listc;
                if ("undefined".equals(entryid) || entryid == null||"".equals(entryid)) {
                    listc = electronicCaptureRepository.findByEleidInOrderByFilename(eleids);
                } else {
                    listc = electronicCaptureRepository.findByEntryidOrderByFilename(entryid);
                }
                for (int i=0;i<listc.size();i++) {
                    Tb_electronic_capture electronicCapture = listc.get(i);
                    electronicCapture.setSortsequence(i+1);
                }
                electronicCaptureRepository.save(listc);
                break;
            case "solid":
                List<Tb_electronic_solid> listSolid;
                if ("undefined".equals(entryid) || entryid == null||"".equals(entryid)) {
                    listSolid = electronicSolidRepository.findByEleidInOrderByFilename(eleids);
                } else {
                    listSolid = electronicSolidRepository.findByEntryidOrderByFilename(entryid);
                }
                for (int i=0;i<listSolid.size();i++) {
                    Tb_electronic_solid electronicSolid = listSolid.get(i);
                    electronicSolid.setSequence(i+1);
                }
                electronicSolidRepository.save(listSolid);
                break;
            default:
                List<Tb_electronic> list;
                if ("undefined".equals(entryid) || entryid == null||"".equals(entryid)) {
                    list = electronicRepository.findByEleidInOrderByFilename(eleids);
                } else {
                    list = electronicRepository.findByEntryidOrderByFilename(entryid);
                }
                for (int i=0;i<list.size();i++) {
                    Tb_electronic electronic = list.get(i);
                    electronic.setSortsequence(i+1);
                }
                electronicRepository.save(list);
                break;
        }
    }

    /**
     * 递归遍历文件夹写入文件表
     * @param path
     * @throws Exception
     */
    public void traverse(String path,String parent_id,String entryid) throws Exception {
        File file = new File(path);
        Map<String, Object> map = new HashMap<String, Object>();
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
                return ;
            } else {
                String k_id = new String(parent_id+"");//重新生成k_id,改变引用地址
                //System.out.println("k_id------"+k_id);
                for(int i=0;i<files.length;i++){
                    if (files[i].isDirectory()) {//文件夹
                        Tb_electronic ele = new Tb_electronic();
                        ele.setEntryid(entryid == null ? "" : entryid);
                        ele.setFilename(files[i].getName());
                        ele.setFileclassid(parent_id);
                        ele.setFilefolder("folder");
                        ele.setSortsequence(i);
//                        System.out.println("parent_id------"+parent_id);
//                        System.out.println("path---->" + files[i].getAbsolutePath());
                        k_id =   electronicRepository.save(ele).getEleid();
                        traverse(files[i].getAbsolutePath(),k_id,entryid);
                    } else if (files[i].isFile()){//文件
//                        System.out.println("parent_id------"+parent_id);
//                        System.out.println("path---->" + files[i].getAbsolutePath());
                        saveZtElectronic("",entryid, files[i].getName(),parent_id,files[i].getAbsolutePath());
                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
    }
}