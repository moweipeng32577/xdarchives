package com.wisdom.web.service;

import com.wisdom.util.*;
import com.wisdom.web.entity.Tb_data_node;
import com.wisdom.web.entity.Tb_oa_record;
import com.wisdom.web.entity.Tb_right_organ;
import com.wisdom.web.repository.DataNodeRepository;
import com.wisdom.web.repository.NodeRepository;
import com.wisdom.web.repository.OaRecordRepository;
import com.wisdom.web.repository.RightOrganRepository;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.wisdom.web.service.ExportExcelService.splitAry;


@Service
@Transactional
public class OARecordService {

    @Autowired
    OaRecordRepository oaRecordRepository;

    @Autowired
    Ftpservice ftpservice;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ExportExcelService excelService;

    @Autowired
    RightOrganRepository rightOrganRepository;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Value("${ftpHost}")
    private String ftpHost; //
    @Value("${ftpUserName}")
    private String ftpUserName; //
    @Value("${ftpPassword}")
    private String ftpPassword; //
    @Value("${ftpPath}")
    private String ftpPath; //
    @Value("${ftpPort}")
    private String ftpPort; //
    @Value("${ftpFileManage}")
    private String ftpFileManage; //
    @Value("${ftpOpen}")
    private String ftpOpen; //
    @Value("${ftpFormat}")
    private String ftpFormat; //
    /*@Value("${ftpFiledel}")
    private String ftpFiledel;*/ //
    @Value("${task.oa.filepath}")
    private String filepath;//oa文件根目录
    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录
    /**
     * @param page      第几页
     * @param limit     一页获取多少行
     * @param condition 字段o
     * @param operator  操作符
     * @param content   查询条件内容
     * @return
     */
    public Page<Tb_oa_record> findBySearch(int page, int limit, String condition, String operator,
                                           String content, Sort sortobj,String nodeid) {
        Sort sort = new Sort(Sort.Direction.DESC, "date");
        PageRequest pageRequest = new PageRequest(page-1, limit, sortobj==null?sort:sortobj);
        //过滤节点
        Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
        if(null==node){
            return null;
        }
        Tb_right_organ organ = rightOrganRepository.findByOrganid(node.getOrganid());
        Specifications specifications = null;
        if (content != null) {
            String[] conditions = condition.split(",");
            String[] operators = operator.split(",");
            String[] contents = content.split(",");
            for (int i = 0; i < contents.length; i++) {
                if("_".equals(contents[i])||"%".equals(contents[i])){
                    contents[i] ="\\"+contents[i];
                }
                specifications = specifications == null ?
                        Specifications.where(new SpecificationUtil(conditions[i], operators[i], contents[i])) :
                        specifications.and(new SpecificationUtil(conditions[i], operators[i], contents[i]));
            }
            specifications.and(new Specification() {
                @Override
                public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                    Predicate predicate = cb.equal(root.get("code"),organ.getCode());
                    return predicate;
                }
            });
        }else {
            specifications = Specifications.where(new Specification() {
                @Override
                public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                    Predicate predicate = cb.equal(root.get("code"),organ.getCode());
                    return predicate;
                }
            });
        }
        return oaRecordRepository.findAll(specifications,pageRequest);
    }


    public Map oaimport(){
        String encodingStr = "";
        Map<String,String> map = new HashMap<>();
        if (ftpHost == null || "".equals(ftpHost)) {
            map.put("erro","FTP连接ip为空！");
            System.out.println("FTP连接ip为空！");
            return map;
        }
        if (ftpUserName == null || "".equals(ftpUserName)) {
            map.put("erro","FTP连接配置用户名参数为空！");
            System.out.println("FTP连接配置用户名参数为空！");
            return map;
        }
        if (ftpPassword == null || "".equals(ftpPassword)) {
            map.put("erro","FTP连接配置密码参数为空！");
            System.out.println("FTP连接配置密码参数为空！");
            return map;
        }
        if (filepath == null || "".equals(filepath)) {
            map.put("erro","FTP文件存放物理路径为空！");
            System.out.println("FTP文件存放物理路径为空！");
            return map;
        }
        if(ftpFormat==null||"".equals(ftpFormat)){
            encodingStr = "UTF-8";
        }else {
            encodingStr = ftpFormat;
        }
        FTPClient ftpClient = null;
        //1.创建FTP连接
        try {
            ftpClient = FtpUtil.getFTPClient(ftpHost, ftpUserName, ftpPassword, Integer.parseInt(ftpPort));
            //2.进行数据包下载,并删除FTP上面的OA包--记录包名 插入数据库
           /*FileList(ftpClient, ftpPath, rootpath + File.separator + "OAFile" + File.separator + "OA接收",
                    rootpath + File.separator + "OAFile" + File.separator + "oa解压目录",new ArrayList<>(),ftpFileManage,ftpFiledel);*/
            //3.进行解压数据包，并删除本地oa包  -- 记录错误包名 插入数据库
            List<String> fileList = ftpservice.unzipAndDelFile(rootpath + File.separator + "OAFile" + File.separator + "OA接收",
                    rootpath + File.separator + "OAFile" + File.separator + "oa解压目录",encodingStr);
            //4.解析数据包 并插入数据库
            for (String s : fileList) {
                File[] files = new File(s).listFiles();
                ftpservice.importFtp(files,s,new File(s).getName());
                FileUtil.delFolder(s);
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                //注销FTP连接
                if(ftpClient!=null){
                    ftpClient.logout();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    //获取FTP 目录 下载数据包到本地
    /**
     * @param ftpClient Ftp连接
     * @param ftpPath   需要下载文件所在目录
     * @param loadPath  下载到本地目录
     * @param unZipPath 解压目录
     */
    public  FTPFile[] FileList(FTPClient ftpClient, String ftpPath, String loadPath, String unZipPath
            , List<String> saveFileName, String ftpFileManage, String ftpFiledel) {
        //路径判断 是否存在，不存在就创建路径
        if (loadPath != null && !new File(loadPath).exists()) {
            new File(loadPath).mkdirs();
        }
        if (unZipPath != null && !new File(unZipPath).exists()) {
            new File(unZipPath).mkdirs();
        }
        ftpClient.setControlEncoding("utf-8");
        List list = new ArrayList();
        FTPFile[] files = null;
        OutputStream ios = null;
        try {
            ftpClient.changeWorkingDirectory(ftpPath);
            files = ftpClient.listFiles();
            for (FTPFile ftpFile : files) {
                //使用验证方式解决文件重复
                if (ftpFile.isFile() && ftpFile.getName().endsWith(".zip") && "false".equals(ftpFileManage)) {
                    //读文件
                    boolean b = false;
                    Long count = oaRecordRepository.findCountByName(ftpFile.getName().substring(0,ftpFile.getName().lastIndexOf(".")));
                    if (count>0) {//确认重复
                        //System.out.println("重复文件");
                        continue;
                    } else{//不重复文件
                        System.out.println("发现OA数据包文件,文件名：" + ftpFile.getName());
                        //1.进行文件下载
                        ios = new FileOutputStream(new File(loadPath + File.separator + ftpFile.getName()));
                        ftpClient.retrieveFile(ftpFile.getName(), ios);
                        //记录获取到的OA文件包 插入数据库 1.oa包名 2.接收时间 3.文件状态 4.文件路径
                        // TODO: 2019/10/10 0010
                        Tb_oa_record oa_record = new Tb_oa_record();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        oa_record.setFilename(ftpFile.getName().substring(0,ftpFile.getName().lastIndexOf(".")));
                        oa_record.setDate(sdf.format(new Date()));
                        oa_record.setFilestate("正常");
                        oa_record.setReceivestate("已下载");
                        oa_record.setFilesize(String.valueOf(ftpFile.getSize()));
                        entityManager.persist(oa_record);
                    }
                    //写文件
                    String a = "";
                    a += ftpFile.getName() + "\r\n";
                    FtpUtil.appdFileName(a);
                }

            }
            if("true".equals(ftpFiledel)) {
                //所有OA包下载完成后 删除ftp上面的OA包
                for (FTPFile ftpFile : files) {
                    ftpClient.deleteFile(ftpPath + File.separator + ftpFile.getName());
                }
            }
            entityManager.flush();
            entityManager.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(ios!=null){
                try {
                    ios.close();
                }catch (IOException io){
                    io.printStackTrace();
                }
            }
        }
        return files;
    }

    public Map createOA(String ids,boolean selectAll){
        String loadPath = rootpath + File.separator +"OAFile" + File.separator + "OA接收";
        Map<String,String> map = new HashMap<>();
        String[] idarr = {};
        if (ids != null) {
            idarr = ids.split(",");
        }
        if (selectAll) {
            List<String> id =oaRecordRepository.findIdAll();
            idarr = id.toArray(new String[id.size()]);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String newName = sdf.format(new Date());
        String newpath = loadPath + File.separator + "OA导出" + File.separator+ newName +
                File.separator;
        if(!new File(newpath).exists()){
            new File(newpath).mkdirs();
        }
        if (idarr.length > 0) {
            List<String[]> listStr = excelService.splitAry(idarr, 900);
            for(String[] s : listStr) {
                List<Tb_oa_record> records = oaRecordRepository.findByIdIn(s);
                for(Tb_oa_record oa : records){
                    if("错误".equals(oa.getFilestate())) {
                        FileOutputStream fileOutputStream = null;
                        try {
                            FileUtil.copyFile(oa.getFilepath(), newpath + oa.getFilename()+".zip");
                            String zippath = loadPath + File.separator + "OA导出"+File.separator+newName+".zip";
                            fileOutputStream = new FileOutputStream(zippath);
                            //打包目录
                            ZipUtils.toZip(newpath,fileOutputStream,true);
                            map.put("filepath",newName+".zip");
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            if(fileOutputStream!=null){
                                try {
                                    fileOutputStream.flush();
                                    fileOutputStream.close();
                                }catch (IOException io){
                                    io.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
        return map;
    }

    public String expImportOAMsg(String ids,boolean selectAll){
        String zippath = null;
        String[] idarr = {};
        if (ids != null) {
            idarr = ids.split(",");
        }
        if (selectAll) {
            List<String> id =oaRecordRepository.findIdAll();
            idarr = id.toArray(new String[id.size()]);
        }
        SXSSFWorkbook workbook = null;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            String usefileName = sdf.format(new Date());
            String[] code = {"filename","date","filesize","filestate","receivestate"};
            String[] name = {"OA包名","接收日期","文件大小","文件状态","接收状态"};
            workbook = SXSSFCreatExcel(idarr,code,name);
            CreateExcel.ExportExcel(workbook,usefileName );
            // zip 完整路径
            //创建字段模板
            String dir = ConfigValue.getPath("system.document.rootpath");
            String path = dir + File.separator + "OAFile" + File.separator + "Excel导出" + File.separator + "临时目录" + File.separator + usefileName;//
            zippath = dir + File.separator + "OAFile" + File.separator + "Excel导出" + File.separator + usefileName + ".zip";
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, "");
            //wirteFile(response,zippath,usefileName,zipReturn,succ);//读取压缩包，发送页面
            ZipUtils.del(path);
        }catch (Exception e){
            e.printStackTrace();
        }
        return zippath;
    }

    public SXSSFWorkbook SXSSFCreatExcel(String[] ids, String[] strcode, String[] strname) throws Exception {
        //1.创建excel文件---用在保存循环的数据--循环写入
        //2.创建工作簿  SXSSFWorkbook 支持最大行1048576
        SXSSFWorkbook workbook = new SXSSFWorkbook(10);
        Sheet sheet = workbook.createSheet();
        if (ids.length > 0) {
            List<String[]> listStr = splitAry(ids, 900);
            for (int i = 0; i < listStr.size(); i++) {//每个元素都是进行截取过的 900条id
                List records = oaRecordRepository.findByIdIn(listStr.get(i));
                //每900条写入1次
                sheet = CreateExcel.SXSSFCreateExcle(sheet, records, strcode, strname);
            }
        }
        return workbook;
    }

}
