package com.wisdom.web.service;

import com.wisdom.service.startup.Constants;
import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.*;
import com.wisdom.web.controller.BackupController;
import com.wisdom.web.entity.BackupFile;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_user;
import com.wisdom.web.repository.*;
import org.apache.commons.io.FileUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by tanly on 2018/1/24 0024.
 */
@Service
public class BackupService {

    @Value("${system.document.rootpath}")
    private String rootpath;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    private static long chunkSize = 5242880;//文件分片大小5M

    //增量备份
    public static String BACKUP_TYPE_INCREMENT = "增量";
    //全量备份
    public static String BACKUP_TYPE_FULL = "全量";

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private UserRepository userRepository;//access can be private
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RightOrganRepository rightOrganRepository;
    @Autowired
    private ClassificationRepository classificationRepository;
    @Autowired
    private DataNodeRepository dataNodeRepository;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private CodesetRepository codesetRepository;
    @Autowired
    private EntryIndexCaptureRepository entryIndexCaptureRepository;
    @Autowired
    private ElectronicCaptureRepository electronicCaptureRepository;
    @Autowired
    private EntryDetailCaptureRepository entryDetailCaptureRepository;
    @Autowired
    private TransdocRepository transdocRepository;
    @Autowired
    private EntryIndexRepository entryIndexRepository;
    @Autowired
    private EntryDetailRepository entryDetailRepository;
    @Autowired
    private ElectronicRepository electronicRepository;
    @Autowired
    private WebSocketService webSocketService;

    public void backup(String[] fnidarr, String backupContent) throws Exception {
        String fileType = ("data".equals(backupContent)) ? "业务数据备份" : "设置数据备份";
        SimpleDateFormat sdf = new SimpleDateFormat("_yyyyMMdd_HHmmss");
        String fileName = getUploadDirZips() + fileType + sdf.format(new Date()) + ".zip";
        OutputStream out = new FileOutputStream(new File(fileName));
        ZipOutputStream zipOut = new ZipOutputStream(out);
        try {
            for (String fnid : fnidarr) {
                backupToXml(zipOut, fnid);
            }
        } finally {
            try {
                zipOut.close();
//                zipOut.finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void backupToXml(ZipOutputStream zipOut, String fnid) throws Exception {
        String[] typeAry = new String[]{"STRING", "DATE", "INTEGER", "INT", "LONG", "BOOLEAN", "DOUBLE", "FLOAT", "TIMESTAMP", "BYTE[]"};//基础类型
        Set<String> typeSet = new HashSet<>(Arrays.asList(typeAry));//为了去掉（类中是实体的属性）
        String[] tables = getRefTable(fnid);
        for (int i = 1, len = tables.length; i < len; ++i) {
            ZipEntry entry = new ZipEntry(tables[0] + "/" + tables[i] + ".xml");// 创建压缩文件
            zipOut.putNextEntry(entry);

            // 创建xml文件
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            outputFormat.setEncoding("utf-8");
            XMLWriter writer = new XMLWriter(zipOut, outputFormat);
            writer.startDocument();
            Element element = DocumentHelper.createElement("table");
            element.addAttribute("name", tables[i]);
            writer.writeOpen(element);

            List list = getRepository(tables[i]).findAll();
            String key, value;
            for (int li = 0; li < list.size(); li++) {
                Field[] fieldArray = list.get(0).getClass().getDeclaredFields();
                Element row = DocumentHelper.createElement("row");
                writer.writeOpen(row);
                for (int j = 0; j < fieldArray.length; ++j) {
                    if (Modifier.isStatic(fieldArray[j].getModifiers())) {//静态属性
                        continue;
                    } else if (!typeSet.contains(fieldArray[j].getType().getSimpleName().toUpperCase())) {
                        continue;
                    }
                    key = fieldArray[j].getName();//设置key

                    String getMethod = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
                    Method method = list.get(li).getClass().getMethod(getMethod);
                    value = method.invoke(list.get(li)) != null ? method.invoke(list.get(li)).toString() : "";// 调用getter方法设置value

                    Element column = DocumentHelper.createElement(key);
                    value = value.replaceAll("\\u0007", "·");//替换这个特殊字符
                    column.setText(value != null ? value : "");
                    writer.write(column);
                }
                writer.writeClose(row);
            }

            writer.writeClose(element);
            writer.endDocument();
        }
    }

    private String[] getRefTable(String backupfnid) {
        String[] tables = null;
        switch (backupfnid) {
            case "userRole":
                tables = new String[]{"设置数据/用户及用户组设置", "Tb_user", "Tb_role", "Tb_user_role"};
                break;
            case "organ":
                tables = new String[]{"设置数据/机构设置", "Tb_right_organ"};
                break;
            case "class":
                tables = new String[]{"设置数据/分类设置", "Tb_classification"};
                break;
            case "dataNode":
                tables = new String[]{"设置数据/数据节点设置", "Tb_data_node"};
                break;
            case "template":
                tables = new String[]{"设置数据/模板设置", "Tb_data_template"};
                break;
            case "code":
                tables = new String[]{"设置数据/档号设置", "Tb_codeset"};
                break;
            case "acquisitionAudit":
                tables = new String[]{"业务数据/数据采集及审核",
                        "Tb_entry_index_capture", "Tb_electronic_capture", "Tb_entry_detail_capture", "Tb_transdoc"};
                break;
            case "management":
                tables = new String[]{"业务数据/数据管理", "Tb_entry_index", "Tb_electronic", "Tb_entry_detail"};
                break;
        }
        return tables;
    }


    public JpaRepository getRepository(String table) {
        JpaRepository jpa;
        switch (table) {
            case "Tb_user":
                jpa = userRepository;
                break;
            case "Tb_role":
                jpa = roleRepository;
                break;
            case "Tb_user_role":
                jpa = userRoleRepository;
                break;
            case "Tb_right_organ":
                jpa = rightOrganRepository;
                break;
            case "Tb_classification":
                jpa = classificationRepository;
                break;
            case "Tb_data_node":
                jpa = dataNodeRepository;
                break;
            case "Tb_data_template":
                jpa = templateRepository;
                break;
            case "Tb_codeset":
                jpa = codesetRepository;
                break;
            case "Tb_entry_index_capture":
                jpa = entryIndexCaptureRepository;
                break;
            case "Tb_electronic_capture":
                jpa = electronicCaptureRepository;
                break;
            case "Tb_entry_detail_capture":
                jpa = entryDetailCaptureRepository;
                break;
            case "Tb_transdoc":
                jpa = transdocRepository;
                break;
            case "Tb_entry_index":
                jpa = entryIndexRepository;
                break;
            case "Tb_entry_detail":
                jpa = entryDetailRepository;
                break;
            case "Tb_electronic":
                jpa = electronicRepository;
                break;
            default:
                jpa = roleRepository;
                break;
        }
        return jpa;
    }

    public List<BackupFile> getBackupList(String tab) {
        File fileDir = new File(getUploadDirZips());
        if("数据库备份".equals(tab)){
            fileDir = new File(getBackupDir());
        }
        File[] fileList = fileDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                boolean accept = false;
                boolean param = (tab == null || pathname.getName().startsWith(tab));
                if (param && pathname.getPath().endsWith(".zip")) {
                    File confFile = new File(pathname.getPath() + ".conf");
                    if (!confFile.exists()) {
                        accept = true;
                    }
                }
                return accept;
            }
        });
        List<BackupFile> list = new ArrayList<>();
        if (fileList != null) {
            Arrays.sort(fileList, new BackupService.ComparatorByLastModified());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (File file : fileList) {
                String fileSize = String.valueOf(Math.ceil(file.length() / 10485.76) / 100);
                String fileTime = sdf.format(new Date(file.lastModified()));
                list.add(new BackupFile(file.getName(), fileSize, fileTime));
            }
        }

        return list;
    }

    static class ComparatorByLastModified implements Comparator<File> {//按照最后修改日期倒序排列

        public int compare(File f1, File f2) {
            long diff = f1.lastModified() - f2.lastModified();
            if (diff > 0)
                return -1;
            else if (diff == 0)
                return 0;
            else
                return 1;
        }
    }

    public void deletebackup(String[] filenames) {
        for (String filename : filenames) {
            File file = new File(getUploadDirZips() + filename);
            if (file.exists()) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
    }

    public Set<String> analyzeByZip(String filename) {
        File file = new File(getUploadDirZips() + filename);

        Set<String> data = new HashSet<String>();
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(file, Charset.forName("GBK"));//GBK处理MALFORMED异常
            Enumeration entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                boolean isRight = Pattern.matches(".*/.*/.*\\.xml", entry.getName());//是否符合目录结构
                if (isRight) {
                    String substr = entry.getName().substring(0, entry.getName().lastIndexOf("/"));
                    String backUpClass = substr.substring(substr.lastIndexOf("/") + 1);
                    if (!data.contains(backUpClass)) {
                        data.add(backUpClass);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (zipFile != null)
                    zipFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public void uploadFileZips(Map<String, Object> param) throws Exception {
        String targetFileName = (String) param.get("filename");
        File tmpFile = new File(getUploadDirZips(), targetFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        //写入数据
        accessTmpFile.write((byte[]) param.get("content"));
        accessTmpFile.close();

        File confFile = new File(getUploadDirZips(), param.get("filename") + ".conf");
        if (confFile.exists()) {
            confFile.delete();//上传断点续传剩余的文件时，删除.conf文件
        }
    }

    private String getUploadDirZips() {
        String dir = rootpath + "/backupRestore/documents/";
        File upDir = new File(dir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }
        return dir;
    }

    private String getBackupDir(){
        File dir = new File(GuavaCache.getValueByKey(Constants.BACKUP_FILE_PATH).toString());
        if(!dir.exists()){
            dir.mkdirs();
        }
        return dir.getPath();
    }

    public void uploadchunkZips(Map<String, Object> param) throws Exception {
        String tempFileName = (String) param.get("filename");
        File confFile = new File(getUploadDirZips(), param.get("filename") + ".conf");
        File tmpFile = new File(getUploadDirZips(), tempFileName);
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

        //上传完成，删除临时文件
        if (isComplete == Byte.MAX_VALUE) {
            confFile.delete();
            tmpFile.renameTo(new File(getUploadDirZips(), (String) param
                    .get("filename")));
        }
    }

    public ExtMsg validateZip(String[] fileName) {
        ExtMsg extMsg = new ExtMsg(true, "", null);
        Set<String> repeatName = new HashSet<>();
        File[] listFiles = new File(getUploadDirZips()).listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getPath().endsWith(".zip");
            }
        });//测试没文件时是否异常//todo

        if (listFiles != null) {
            for (String filename : fileName) {
                for (File file : listFiles) {
                    if (file.getName().equals(filename)) {
                        File confFile = new File(file.getPath() + ".conf");
                        if (!confFile.exists()) {//文件名重复条件：重名+无.conf文件
                            repeatName.add(filename);
                            break;
                        }
                    }
                }
            }
            if (repeatName.size() != 0) {
                String msg = repeatName.size() + "个文件添加失败：<br>";
                String repeat = "";
                Iterator<String> it = repeatName.iterator();
                while (it.hasNext()) {
                    String filename = it.next();
                    msg += filename + "<br>";
                    repeat += filename + ",";
                }
                repeat = repeat.substring(0, repeat.lastIndexOf(","));
                msg += "原因：系统已有同名文件";
                extMsg = new ExtMsg(false, msg, repeat);
            }
        }

        return extMsg;
    }

    public void restore(String[] fnidarr, String filename) throws Exception {
        File file = new File(getUploadDirZips() + filename);

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(file, Charset.forName("GBK"));//GBK处理MALFORMED异常
            Enumeration entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                boolean isRight = Pattern.matches(".*/.*/.*\\.xml", entry.getName());//是否符合目录结构
                if (isRight) {
                    //备份勾选的数据分类
                    String subStr = entry.getName().substring(0, entry.getName().lastIndexOf("/"));
                    String entryClass = subStr.substring(subStr.lastIndexOf("/") + 1);
                    boolean backup = false;//此分类是否要备份
                    for (String fnid : fnidarr) {
                        String secondPath = getRefTable(fnid)[0];
                        String backupClass = secondPath.substring(secondPath.lastIndexOf("/") + 1);
                        if (entryClass.equals(backupClass)) {
                            backup = true;
                            break;
                        }
                    }
                    if (backup) {
                        InputStream inputStream = zipFile.getInputStream(entry);
                        SAXParser sax = SAXParserFactory.newInstance().newSAXParser();
                        BigXmlParse handler = new BigXmlParse();
                        sax.parse(inputStream, handler);
                        inputStream.close();
                        List<Map<String, Object>> dataList = handler.getDataList();
                        String table = handler.getTable();
                        // 删除数据
                        entityManager.createQuery("delete from " + table).executeUpdate();
                        // 获取行数据
                        for (Map<String, Object> map : dataList) {
                            setValue(table, map);//保存数据
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (zipFile != null)
                    zipFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 插入一行数据
     * @param table
     * @param map
     * @throws Exception
     */
    public void setValue(String table, Map<String, Object> map) throws Exception {
        Class entityClass = Class.forName("com.wisdom.web.entity." + table);//根据表名取类
        Map<String, Object> fieldValueMap = new HashMap<>();
        for (String field : map.keySet()) {
            setFieldValueMap(entityClass, field, map.get(field).toString(), fieldValueMap);//设置字段和值的Map
        }

        //拼接sql——开始
        String sql = "insert into " + table.substring(0, 1).toLowerCase() + table.substring(1) + "(";
        String tmp = "";
        for (String field : fieldValueMap.keySet()) {
            sql += field + ",";
            tmp += ":" + field + ",";
        }
        sql = sql.substring(0, sql.length() - 1) + ") values(" + tmp.substring(0, tmp.length() - 1) + ")";
        //拼接sql——结束

        Query query = entityManager.createNativeQuery(sql);
        for (String field : fieldValueMap.keySet()) {
            query.setParameter(field, fieldValueMap.get(field));
        }
        try {
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置字段/值的Map
     * @param entityClass
     * @param fieldStr
     * @param valueStr
     * @param fieldValueMap
     */
    public void setFieldValueMap(Class entityClass, String fieldStr, String valueStr, Map<String, Object> fieldValueMap) {
        try {
            Field field = entityClass.getDeclaredField(fieldStr);
            String fieldType = field.getType().getSimpleName();//获取属性类型
            if ("String".equals(fieldType)) {
                fieldValueMap.put(fieldStr, valueStr);
            } else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
                Integer value = null;
                if (!"".equals(valueStr)) {
                    value = Integer.parseInt(valueStr);
                }
                fieldValueMap.put(fieldStr, value);
            } else if ("Long".equalsIgnoreCase(fieldType)) {
                Long value = null;
                if (!"".equals(valueStr)) {
                    value = Long.parseLong(valueStr);
                }
                fieldValueMap.put(fieldStr, value);
            } else if ("Double".equalsIgnoreCase(fieldType) || "Float".equalsIgnoreCase(fieldType)) {
                Double value = null;
                if (!"".equals(valueStr)) {
                    value = Double.parseDouble(valueStr);
                }
                fieldValueMap.put(fieldStr, value);
            } else if ("Boolean".equalsIgnoreCase(fieldType)) {
                Boolean value = null;
                if (!"".equals(valueStr)) {
                    value = Boolean.parseBoolean(valueStr);
                }
                fieldValueMap.put(fieldStr, value);
            } else if ("Timestamp".equalsIgnoreCase(fieldType)) {
                Timestamp value = null;
                if (!"".equals(valueStr)) {
                    value = Timestamp.valueOf(valueStr);
                }
                fieldValueMap.put(fieldStr, value);
            } else if ("Date".equalsIgnoreCase(fieldType)) {
                Date value = null;
                if (!"".equals(valueStr)) {
                    value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(valueStr);
                }
                fieldValueMap.put(fieldStr, value);
            } else if ("byte[]".equalsIgnoreCase(fieldType)) {
                byte[] value = null;
                if (!"".equals(valueStr)) {
                    value = valueStr.getBytes();//TODO
                }
                fieldValueMap.put(fieldStr, value);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void backupAll(String backupContent) throws Exception {
        String[] fnidarr = new String[]{};
        if("setting".equals(backupContent)){
            fnidarr = BackupController.BACKUP_SETTING;
        }
        if("data".equals(backupContent)){
            fnidarr = BackupController.BACKUP_DATA;
        }
        List<String> fnidList = new ArrayList<>();
        for(String fnid:fnidarr){
            fnidList.add(fnid.split(",")[0]);
        }
        //此处必须新建一个字符串数组接收集合中的值，
        // 若直接用fnidarr接收，会导致常量的值发生改变，导致获取备份列表时产生bug
        String[] newfnidarr = new String[fnidarr.length];
        fnidList.toArray(newfnidarr);
        backup(newfnidarr,backupContent);
    }

    /**
     * 数据库备份
     * @param type
     * @param userid
     * @throws Exception
     */
    public void backupDataBase(String type, String userid) throws Exception {
        if(type.equals(BackupService.BACKUP_TYPE_INCREMENT)){
            return;
        }
        String dbName="";
        String dbVersion = DBCompatible.getInstance().getDBVersion();//获取数据库的类型
        String path ="";
        //   password = stringEncryptor.decrypt(password); //对密码进行解密
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fileName = "数据库备份" + '_' + type + '_' + sdf.format(new Date()); //文件的名字
        switch (dbVersion) {
            case "mysql":
//                String dbip = DBCompatible.getDBIP();
//                dbip = dbip.replace(":", " -P"); //获取数据库的IP地址
//                dbName = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?")); //获取当前mysql数据库的名字
//                backupAll(dbip,path, username, password, getUploadDirZips(), fileName, dbName,userid);
                break;
            case "oracle":
//                int i = url.lastIndexOf(":");
//                dbName= url.substring(i + 1, url.length()); //获取oracle数据库的名字
//                backUpDataBaseOracle(username,password,dbName,getUploadDirZips()+fileName,userid);
                break;
            case "sqlserver":
                dbName = url.substring(url.lastIndexOf("=") + 1, url.length());
                backupFullForSqlServer(url,getBackupDir()+File.separator+fileName,dbName,
                        username,password,driverClassName,userid);
                break;
        }
    }

    private void backupFullForSqlServer(String url,String fileName,String dbname,String username,
                                               String password,String driver,String userid) throws Exception {

        String path = fileName+".bak";// name文件名
        if(!FileUtil.isexists(path)){
            new File(path);
        }
        String bakSQL = "backup database "+dbname+" to disk=? with init";// SQL语句
        Connection conn = DataBaseUtil.getConnection(url,username,password,driver);
        PreparedStatement bak = conn.prepareStatement(bakSQL);
        bak.setString(1, path);// path必须是绝对路径

        new Thread() {
            @Override
            public void run() {
                try {
                    bak.execute();// 备份数据库
                    ZipUtil.zip(path);
                    FileUtils.forceDelete(new File(path));
                    webSocketService.sendMessageBackupDatabase(userid,"备份所有数据已完成");// 通知用户备份已经完成
                } catch (Exception e) {
                    e.printStackTrace();
                    webSocketService.sendMessageBackupDatabase(userid,"备份所有数据失败");
                } finally {
                    DataBaseUtil.closeConn(conn);
                    try {
                        bak.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void recoverDatabase(String filepath,String userid) throws Exception {
        String dbVersion = DBCompatible.getInstance().getDBVersion();//获取数据库的类型
        String path ="";
        filepath = getBackupDir() + File.separator + filepath;
        switch (dbVersion) {
            case "mysql":
//                String dbip = DBCompatible.getDBIP();
//                dbip = dbip.replace(":", " -P"); //获取数据库的IP地址
//                String database = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?")); //获取当前mysql数据库的名字
//                recoverMysql(filepath,dbip,database,username,password,path, userid);
                break;
            case "oracle":
//                int i = url.lastIndexOf(":");
//                String dbName= url.substring(i + 1, url.length()); //获取数据库的名字
//                resumeDataBaseOracle(username,password,dbName,getUploadDirZips()+filepath, userid);
                break;
            case "sqlserver":
                String dbName = url.substring(url.lastIndexOf("=") + 1, url.length());
                recoverySqlServer(url,filepath,dbName,driverClassName,userid);
                break;
        }
    }

    public void recoverySqlServer(String url,String back_path,String dbname,String driver,String userid) throws Exception {
        //恢复所有连接 sql
        String recoverySql = "alter database "+dbname+" set online with rollback immediate";
        //备份文件解压缩
        File zipFile = new File(back_path);
        ZipUtils.unZip4j(back_path, zipFile.getParent());
        File bakFile = new File(back_path.replace(".zip", ".bak"));
        String offlineSql = "alter database "+dbname+" set offline with rollback immediate";
        //拼接恢復數據庫的sql語句
        StringBuffer restoreSql = new StringBuffer();
        restoreSql.append("RESTORE DATABASE ").append(dbname);
        restoreSql.append(" FROM DISK=N'").append(bakFile.getPath()).append("'");
        restoreSql.append(" WITH REPLACE,FILE = 1,RECOVERY,STATS = 5");
        String select="select distinct b.name" +
                " from dbo.syscomments a, dbo.sysobjects b" +
                " where a.id=b.id  and b.xtype='p' and a.text like '%killrestore%'";
        //创建存储过程
        String killrestore = "CREATE proc killrestore (@dbname varchar(20),@dbpath varchar(40)) " +
                "as begin declare @sql nvarchar(500) declare @spid int " +
                "set @sql='declare getspid cursor for select spid from sys.sysprocesses where dbid=db_id('''+@dbname+''')' " +
                "exec (@sql) open getspid fetch next from getspid into @spid "+
                "while @@fetch_status <> -1 begin exec('kill '+@spid) " +
                "fetch next from getspid into @spid end close getspid deallocate getspid end";
        Connection conn = DataBaseUtil.getConnection(GuavaCache.getValueByKey(Constants.BACKUP_DATASOURCE_URL).toString(),
                GuavaCache.getValueByKey(Constants.BACKUP_DATASOURCE_USERNAME).toString(),
                GuavaCache.getValueByKey(Constants.BACKUP_DATASOURCE_PASSWORD).toString(),
                driver);
        PreparedStatement offlinePs = conn.prepareStatement(offlineSql);
        PreparedStatement et = conn.prepareStatement(select);
        PreparedStatement s = conn.prepareStatement(killrestore);
        PreparedStatement ps = conn.prepareStatement(recoverySql);
        PreparedStatement rs = conn.prepareStatement(restoreSql.toString());
        CallableStatement cs = conn.prepareCall("{call killrestore(?,?)}");

        new Thread() {
            @Override
            public void run() {
                try {
                    offlinePs.execute();
                    ResultSet rsexist = et.executeQuery();
                    if (!rsexist.next()) {
                        s.execute();//創建存儲過程，用於關閉連接進程
                    }
                    cs.setString(1, dbname); // 数据库名
                    cs.setString(2, "'"+back_path+"'"); // 已备份数据库所在路径
                    cs.execute(); // 关闭数据库链接进程

                    rs.execute();//还原备份
                    ps.execute(); // 恢复数据库连接
                    bakFile.delete();//删除解压的备份文件

                    try {
                        //此时连接已断开，调用一次查询甩掉异常，后面可正常查询
                        entityManager.createNativeQuery("select 1 from dual").getSingleResult();
                    } catch (Exception e){

                    }
                    Thread.sleep(10000);
                    webSocketService.sendMessagerestoreAll(userid,"还原所有数据已完成");// 通知用户备份已经完成
                } catch (Exception e) {
                    e.printStackTrace();
                    webSocketService.sendMessagerestoreAll(userid,"还原所有数据失败");
                } finally {
                    try {
                        offlinePs.close();
                        et.close();
                        s.close();
                        ps.close();
                        rs.close();
                        cs.close();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    DataBaseUtil.closeConn(conn);
                }
            }
        }.start();
    }

}
