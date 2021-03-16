package com.wisdom.web.service;

import com.alibaba.fastjson.JSON;
import com.wisdom.util.*;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yl on 2017/11/1.
 * 专题模块service
 */
@Service
@Transactional
public class ThematicMakeService {
    @Autowired
    ThematicMakeRepository thematicRepository;

    @Autowired
    ThematicDetailMakeRepository thematicDetailRepository;

    @Autowired
    ThematicDetailRepository newthematicDetailRepository;

    @Autowired
    ElectronicRepository electronicRepository;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    ElectronicService electronicService;

    @Autowired
    ExportExcelService exportExcelService;

    @Autowired
    ThematicRepository themRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    ClassifySearchService classifySearchService;

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录

    @Value("${system.thematic.pwd}")
    private String thematicPwd;//专题解压密码

    private static long chunkSize = 5242880;//文件分片大小5M

    /**
     * @param page  第几页
     * @param limit 一页获取多少行
     * @return
     */
    public Page<Tb_thematic_make> getThematic(int page, int limit) {
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        Page<Tb_thematic_make> thematics = thematicRepository.findAll(pageRequest);
        return thematics;
    }

    /**
     * @param page      第几页
     * @param limit     一页获取多少行
     * @param condition 字段
     * @param operator  操作符
     * @param content   查询条件内容
     * @return
     */
    public Page<Tb_thematic_make> findBySearch(int page, int limit, String condition, String operator, String content, Sort sort) {
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
//        Specification<Tb_thematic_make> searchid = new Specification<Tb_thematic_make>() {
//            @Override
//            public Predicate toPredicate(Root<Tb_thematic_make> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
//                Predicate p = criteriaBuilder.equal(root.get("publishstate"), "已发布");
//                return criteriaBuilder.or(p);
//            }
//        };
//        Specifications specifications = Specifications.where(searchid);
        Specifications specifications = null;
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        return thematicRepository.findAll(Specifications.where(specifications), pageRequest);
    }

    public Tb_thematic_make saveThematic(Tb_thematic_make Tb_thematic_make,String backgroundpath) {
        if("".equals(backgroundpath)||backgroundpath==null){
            Tb_thematic_make.setBackgroundpath("/static/img/icon/thematic_def.png");
        }else{
            Tb_thematic_make.setBackgroundpath(backgroundpath);
        }
        return thematicRepository.save(Tb_thematic_make);
    }

    /**
     * 删除专题
     *
     * @param thematicids 专题id数组
     * @return
     */
    public Integer deleteThematic(String[] thematicids) {
        List<Tb_thematic_detail_make> thematic_details = thematicDetailRepository.findByThematicidIn(thematicids);
        String[] thematicDetilids = GainField.getFieldValues(thematic_details, "thematicdetilid").length == 0 ? new
                String[]{""} : GainField.getFieldValues(thematic_details, "thematicdetilid");
        List<Tb_electronic> electronics = electronicRepository.findByEntryidInOrderBySortsequence(thematicDetilids);
        if (electronics.size() > 0) {
            for(Tb_electronic electronic :electronics){
                // 获取改专题详情下的电子文件存放路径
                String filePath = rootpath + electronic.getFilepath();
                // 删除文件夹连同电子文件一并删除
                FileUtil.delFolder(filePath);
            }
        }
        electronicRepository.deleteByEntryidIn(thematicDetilids);//删除电子文件条目
        thematicDetailRepository.deleteByThematicdetilidIn(thematicDetilids);//删除专题内容
        return thematicRepository.deleteAllByThematicidIn(thematicids);
    }

    public Integer updataThematic(String title, String content,String thematictypes,String backgroundpath, String thematicid) {
        return thematicRepository.updateThematicid(title, content,thematictypes,backgroundpath,thematicid);
    }

    public List<ExtNcTree> getThematic() {
        List<ExtNcTree> extNcTrees = new ArrayList<ExtNcTree>();
        List<Tb_thematic_make> thematics = thematicRepository.findAll();
        for (Tb_thematic_make Tb_thematic_make : thematics) {
            ExtNcTree extNcTree = new ExtNcTree();
            extNcTree.setFnid(Tb_thematic_make.getThematicid());
            extNcTree.setText(Tb_thematic_make.getTitle());
            extNcTree.setLeaf(true);
            extNcTrees.add(extNcTree);
        }
        return extNcTrees;
    }

    /**
     * @param page       第几页
     * @param limit      一页获取多少行
     * @param thematicid 专题ID
     * @return
     */
    public Page<Tb_thematic_detail_make> findTDetailByThematicid(int page, int limit, String condition, String operator, String content, String thematicid, Sort sort) {
        PageRequest pageRequest = new PageRequest(page - 1, limit,sort);
        Specification<Tb_thematic_detail_make> searchid = getSearchThematicidCondition(thematicid);
        Specifications specifications = Specifications.where(searchid);
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        return thematicDetailRepository.findAll(specifications, pageRequest);
    }

    public Tb_thematic_detail_make saveThematicDetail(Tb_thematic_detail_make Tb_thematic_detail_make, String[] mediaids) {
        Tb_thematic_detail_make.setThematicdetilid(UUID.randomUUID().toString().replace("-", ""));
        Tb_thematic_detail_make thematic_detail = thematicDetailRepository.save(Tb_thematic_detail_make);
        if (mediaids != null) {
            List<Tb_electronic> electronics = electronicRepository.findByEleidInOrderBySortsequence(mediaids);
            for (Tb_electronic electronic : electronics) {
                // 获取原来电子文件
                File targetFile = new File(rootpath + electronic.getFilepath(), electronic.getFilename());
                // 获取新的存储电子文件路径
                String filepath = electronicService.getUploadDirThematic(thematic_detail.getThematicdetilid())
                        .replace(rootpath, "");
                // 把之前原来电子文件转存到存储路径
                targetFile.renameTo(new File(rootpath + filepath, electronic.getFilename()));
                // 转存完成后删除原来的文件
                targetFile.delete();
                electronic.setEntryid(thematic_detail.getThematicdetilid());
                electronic.setFilepath(filepath);
            }
        }
        return thematic_detail;
    }

    public Tb_thematic_detail_make updateThematicDetail(Tb_thematic_detail_make Tb_thematic_detail_make) {
        return thematicDetailRepository.save(Tb_thematic_detail_make);
    }

    /**
     * 删除专题内容
     *
     * @param thematicdetilids 专题内容id数组
     * @return
     */
    public Integer deleteThematicDetail(String[] thematicdetilids) {
        List<Tb_electronic> electronics = electronicRepository.findByEntryidInOrderBySortsequence(thematicdetilids);
        if (electronics.size() > 0) {
            for(Tb_electronic electronic :electronics){
                // 获取改专题详情下的电子文件存放路径
                String filePath = rootpath + electronic.getFilepath();
                // 删除文件夹连同电子文件一并删除
                FileUtil.delFolder(filePath);
            }
        }
        electronicRepository.deleteByEntryidIn(thematicdetilids);//删除电子文件条目
        int count = thematicDetailRepository.deleteByThematicdetilidIn(thematicdetilids);
        return count;
    }

    public Integer updateThematicForPublishstate(String type, String thematicids) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();//获取安全对象
        int count=0;
        String[] ids = thematicids.split(",");
        for (String thematicid : ids) {
            String publishtime = "已发布".equals(type)?new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()):"";
            if(thematicRepository.updateThematicForPublishstate(type,publishtime,thematicid)>0){
                if("已发布".equals(type)){
                    Tb_thematic_make Tb_thematic_make=thematicRepository.findByThematicid(thematicid);
                    //List<Tb_thematic_detail_make> thematic_details = thematicDetailRepository.findByThematicid(thematicid);//获取内容
                    List<Tb_thematic_detail> thematic_details = newthematicDetailRepository.findByThematicid(thematicid);
                    List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
                    //Tb_thematic_detail_make thematic_detail1;
                    for (Tb_thematic_detail thematic_detail : thematic_details) {//生成excel数据并保存在map中

                        Map<String, Object> mapValue = new HashMap<String, Object>();
                        mapValue.put("title", thematic_detail.getTitle());
                        mapValue.put("date", thematic_detail.getFiledate());
                        mapValue.put("responsibleperson", thematic_detail.getResponsibleperson());
                        mapValue.put("filecode", thematic_detail.getFilecode());
                        mapValue.put("subheadings", thematic_detail.getSubheadings());
                        mapValue.put("mediatext", thematic_detail.getMediatext());
                        listmap.add(mapValue);
                    }

                    String names[] = {"题名", "时间", "责任者", "文件编号", "主题词","电子文件"};//列名
                    String keys[] = {"title", "date", "responsibleperson", "filecode", "subheadings","mediatext"};//map中的key


                    String[] thematicdetilid = GainField.getFieldValues(thematic_details, "thematicdetilid").length == 0 ? new String[]{""} : GainField.getFieldValues(thematic_details, "thematicdetilid");
                    List<Tb_electronic> electronics = electronicRepository.findByEntryidInOrderBySortsequence(thematicdetilid);//获取专题信息电子文件
                    String excelPath = rootpath + "/thematic/utilize/" + userDetails.getLoginname() + "/" + Tb_thematic_make
                            .getTitle();//拷贝后根路径
                    File eleDir = new File(excelPath);
                    if (!eleDir.exists()) {
                        eleDir.mkdirs();
                    }
                    //判断有没有存在之前压缩的zip，有的就删除
                    File zipFile=new File(excelPath+".zip");
                    if (zipFile.exists()) {
                        zipFile.delete();
                    }
                    for (Tb_electronic electronic : electronics) {
                        if(!"folder".equals(electronic.getFilefolder())) {
                            String srcPath = rootpath + "/" + electronic.getFilepath() + "/" + electronic.getFilename();
                            String desPath = rootpath + "/" +
                                    electronic.getFilepath().replace("/thematic/prod/", "/thematic/utilize/" +
                                            userDetails.getLoginname() + "/" + Tb_thematic_make.getTitle() + "/") + "/" + electronic
                                    .getFilename();
                            try {
                                File src = new File(srcPath);
                                File des = new File(desPath);
                                if (src.exists()) {
                                    FileUtils.copyFile(src, des);//拷贝电子文件
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        String outName=excelPath + "/" + Tb_thematic_make.getTitle() + ".xls";
                        Workbook wb = new HSSFWorkbook();
                        Workbook workbook = new ExportUtil().createWorkBook(wb,"text.xls", listmap, keys, names);//获取工作簿
                        OutputStream outXlsx = null;
                        outXlsx = new FileOutputStream(outName);
                        workbook.write(outXlsx);
                        outXlsx.flush();
                        outXlsx.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ZipUtil.zip(excelPath.replaceAll("/", "\\\\"), excelPath.replaceAll("/", "\\\\") + ".zip", thematicPwd);//压缩
                    String zipPath = excelPath.replaceAll("/", "\\\\") + ".zip";
                    String filePath=zipPath.substring(rootpath.length());
                    File file=new File(zipPath);
                    long fileSize=file.length();
                    thematicRepository.updateThematicidFilePath(filePath,fileSize,thematicid);
                    delFolder(excelPath);//打包完，删除文件夹及文件夹里面的数据
                }
                count++;
            }
        }
        return count;
    }

    /**
     * 获取已发布专题
     *
     * @param page  第几页
     * @param limit 一页获取多少行
     * @return
     */
    public List getThematicDetailFb(int page, int limit, String condition, String operator, String content) {
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        Specification<Tb_thematic_make> searchid = new Specification<Tb_thematic_make>() {
            @Override
            public Predicate toPredicate(Root<Tb_thematic_make> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get("publishstate"), "已发布");
                return criteriaBuilder.or(p);
            }
        };
        Specifications specifications = Specifications.where(searchid);
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        Page<Tb_thematic_make> pages = thematicRepository.findAll(specifications, pageRequest);
        ;//获取已发布专题
        List<Tb_thematic_make> thematics = pages.getContent();
        String[] thematicids = GainField.getFieldValues(thematics, "thematicid").length == 0 ? new String[]{""} : GainField.getFieldValues(thematics, "thematicid");
        List<Tb_thematic_detail_make> thematic_details = thematicDetailRepository.findByThematicidIn(thematicids);//获取内容
        List<Tb_electronic> electronics = electronicRepository.getElectronics(thematicids);//获取内容电子文件
        Map<String, Integer> mediasizemap = new HashMap<>();
        for (Tb_thematic_detail_make thematic_detail : thematic_details) {//遍历统计电子文件大小
            for (Tb_electronic electronic : electronics) {
                if (mediasizemap.containsKey(thematic_detail.getThematicid())) {
                    mediasizemap.put(thematic_detail.getThematicid(), mediasizemap.get(thematic_detail.getThematicid()) + Integer.parseInt(electronic.getFilesize()));
                } else {
                    mediasizemap.put(thematic_detail.getThematicid(), Integer.parseInt(electronic.getFilesize()));
                }
            }
        }

        List list = new ArrayList();
        list.add(thematics);
        list.add(mediasizemap);
        return list;
    }

    /**
     * 专题内容导入
     *
     * @param dataids 专题内容id数组
     * @param treeid  专题id
     * @return
     */
    public ExtMsg searchleadin(String[] dataids, String treeid) {
        String msg="";
        PageRequest pageRequest = new PageRequest(0, 200);
        Page<Tb_entry_index> page = entryIndexRepository.findByEntryidIn(dataids, pageRequest);
        List<Tb_entry_index> entry_indices = page.getContent();
        for(Tb_entry_index entry_index : entry_indices){
//            if(thematicDetailRepository.findByThematicidAndTitle(treeid,entry_index.getTitle()).size()>0){
//                msg=msg +"'"+entry_index.getTitle()+"'导入失败，题名不能重复<br />";
//            }else{
                msg=msg +"'"+entry_index.getTitle()+"'导入成功<br />";
                List<Tb_electronic> electronics = electronicRepository.findByEntryidOrderBySortsequence(entry_index.getEntryid());
                String mediatext = "";
                for (Tb_electronic electronic : electronics) {
                    mediatext += "," + electronic.getFilename();
                }
                mediatext = "".equals(mediatext) ? "" : mediatext.substring(1);
                Tb_thematic_detail_make thematic_detail = thematicDetailRepository.save(new
                        Tb_thematic_detail_make(treeid, entry_index.getTitle(), entry_index.getDescriptiondate(), entry_index
                        .getResponsible(), entry_index.getFilenumber(), entry_index.getKeyword(), mediatext));
                for (Tb_electronic e : electronics) {
                    Tb_electronic tb_electronic = new Tb_electronic();
                    BeanUtils.copyProperties(e,tb_electronic);
                    tb_electronic.setEleid("");
                    tb_electronic.setEntryid(thematic_detail.getThematicdetilid());
                    tb_electronic.setFilepath(renameToThematic(thematic_detail.getThematicdetilid(),rootpath + e
                            .getFilepath(), e.getFilename()));
                    electronicRepository.save(tb_electronic);
                }
//            }
        }
        return new ExtMsg(true,msg,null);
    }

    //转存文件到专题目录下
    public String renameToThematic(String thematicdetilid,String filePath, String fileName) {
        String path = electronicService.getUploadDirThematic(thematicdetilid);
        if (!"".equals(filePath) && filePath != null) {
            File tmpFile = new File(filePath, fileName);
            File targetFile = new File(path, fileName);
            try {
                FileUtils.copyFile(tmpFile, targetFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path.replace(rootpath, "");
    }

    /**
     * 专题下载
     *
     * @param id   专题id
     * @param text 专题名称
     * @return
     */
    public String downloadZt(String id, String text) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();//获取安全对象
        List<Tb_thematic_detail_make> thematic_details = thematicDetailRepository.findByThematicid(id);
        List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
        for (Tb_thematic_detail_make thematic_detail : thematic_details) {//生成excel数据并保存在map中

            Map<String, Object> mapValue = new HashMap<String, Object>();
            mapValue.put("title", thematic_detail.getTitle());
            mapValue.put("date", thematic_detail.getFiledate());
            mapValue.put("responsibleperson", thematic_detail.getResponsibleperson());
            mapValue.put("filecode", thematic_detail.getFilecode());
            mapValue.put("subheadings", thematic_detail.getSubheadings());
            listmap.add(mapValue);
        }

        String names[] = {"题名", "时间", "责任者", "文件标号", "主题词"};//列名
        String keys[] = {"title", "date", "responsibleperson", "filecode", "subeadings"};//map中的key


        String[] thematicdetilid = GainField.getFieldValues(thematic_details, "thematicdetilid").length == 0 ? new String[]{""} : GainField.getFieldValues(thematic_details, "thematicdetilid");
        List<Tb_electronic> electronics = electronicRepository.findByEntryidInOrderBySortsequence(thematicdetilid);//获取专题信息电子文件
        String excelPath = rootpath + "/electronics/thematic/" + userDetails.getLoginname() + "/" + text;//拷贝后根路径
        File eleDir = new File(excelPath);
        if (!eleDir.exists()) {
            eleDir.mkdirs();
        }
        //判断有没有存在之前压缩的zip，有的就删除
        File zipFile=new File(excelPath+".zip");
        if (zipFile.exists()) {
            zipFile.delete();
        }
        try {
            for (Tb_electronic electronic : electronics) {
                String srcPath = rootpath + "/" + electronic.getFilepath() + "/" + electronic.getFilename();
                String desPath = rootpath + "/" +
                        electronic.getFilepath().replace("/electronics/storages/", "/electronics/thematic/" + userDetails.getLoginname() + "/" + text + "/") + "/" + electronic.getFilename();

                File src = new File(srcPath);
                File des = new File(desPath);
                FileUtils.copyFile(src, des);//拷贝电子文件
            }
            Workbook wb = new HSSFWorkbook();
            Workbook workbook = new ExportUtil().createWorkBook(wb, "text.xls", listmap, keys, names);//获取工作簿
            OutputStream outXlsx = new FileOutputStream(excelPath + "/" + text + ".xls");
            workbook.write(outXlsx);
            outXlsx.flush();
            outXlsx.close();
        } catch (IOException e) {
        }

        ZipUtil.zip(excelPath.replaceAll("/", "\\\\"), excelPath.replaceAll("/", "\\\\") + ".zip", "555");//压缩
        String zipPath = excelPath.replaceAll("/", "\\\\") + ".zip";
        delFolder(excelPath);//打包完，删除文件夹及文件夹里面的数据
        return zipPath;
    }

    public Page findTbThematicPage(int page, int limit, String condition, String operator, String
            content,Sort sort,String thematictypes) {
        String sortStr = "";
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            sortStr = " order by " + order.getProperty() + " " + order.getDirection();
        } else {
            sortStr = " ORDER BY publishtime desc ";
        }
        String thematictypesStr = "";
        if(thematictypes!=null){
            thematictypesStr = " and thematictypes ='"+thematictypes+"'";
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        String sqlTh = "select * from tb_thematic sid where publishstate='已发布' ";
        String sqlThMake = "select * from tb_thematic_make sid where publishstate='已发布' ";
        String searchCondition = "";
        if (content != null && !"".equals(content)) {//输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        Query queryTh = entityManager.createNativeQuery(sqlTh+searchCondition+thematictypesStr+sortStr, Tb_thematic_make.class);
        Query queryThMake = entityManager.createNativeQuery(sqlThMake+searchCondition+thematictypesStr+sortStr, Tb_thematic_make.class);
        List returnList = new ArrayList();
        returnList.addAll(queryThMake.getResultList());
        returnList.addAll(queryTh.getResultList());
        return new PageImpl(returnList,pageRequest,returnList.size());
    }

    public Page<Tb_thematic_make> findTbThematicBythematictypes(int page, int limit,Sort sort,String thematictypes){
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        return thematicRepository.findThematicbyThematictypes(pageRequest,thematictypes);
    }

    public String findFilePathByThematicid(String thematicid) {
        Tb_thematic_make tbThematic = thematicRepository.findByThematicid(thematicid);
        if(null==tbThematic){
            return null;
        }
        return rootpath + tbThematic.getFilepath();
    }

    public Tb_thematic_make findByThematicid(String thematicid) {
        return thematicRepository.findByThematicid(thematicid);
    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
        //删除指定文件夹下所有文件
    }

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }
    public void uploadchunk(Map<String, Object> param) throws Exception {
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
        // 上传完成，删除临时文件
        if (isComplete == Byte.MAX_VALUE) {
            confFile.delete();
            tmpFile.renameTo(new File(getUploadDir(), (String) param
                    .get("filename")));
        }
    }
    public void uploadfileInform(Map<String, Object> param) throws Exception {
        String targetFileName = (String) param.get("filename");
        File tmpFile = new File(getUploadDir(), targetFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        //写入数据
        accessTmpFile.write((byte[]) param.get("content"));
        accessTmpFile.close();
    }
    private String getUploadDir() {
        String dir = "";
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dir = rootpath + "/thematic/background/" + userDetails.getUsername();
        File upDir = new File(dir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }

        return dir;
    }

    public String getBackgroundPath(String fileName){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return "/thematic/background/" + userDetails.getUsername()+"/"+fileName;
    }

    public String  getBackgroundpath(String thematicid, String filename){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Tb_thematic_make Tb_thematic_make=thematicRepository.findByThematicid(thematicid);
        Tb_thematic_make.setBackgroundpath("/thematic/background/" + userDetails.getUsername()+"/"+filename);
        return "/thematic/background/" + userDetails.getUsername()+"/"+filename;
    }

    public static Specification<Tb_thematic_detail_make> getSearchThematicidCondition(String thematicid){
        Specification<Tb_thematic_detail_make> searchThematicidCondition = new Specification<Tb_thematic_detail_make>() {
            @Override
            public Predicate toPredicate(Root<Tb_thematic_detail_make> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get("thematicid"), thematicid);
                return criteriaBuilder.or(p);
            }
        };
        return searchThematicidCondition;
    }

    public Tb_thematic_make findByTitle(String title){
        return thematicRepository.findByTitle(title);
    }

    public List<Tb_thematic_make> getThematicbyState(){
        Specifications sp = Specifications.where(new SpecificationUtil("publishstate","equal","已发布"));
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(new Sort.Order(Sort.Direction.DESC,"publishtime"));
        PageRequest pageRequest = new PageRequest(0,3,new Sort(sorts));
        String sql="select * from tb_thematic where publishstate='已发布' ORDER BY publishtime desc ";
        Query query = entityManager.createNativeQuery(DBCompatible.getInstance().sqlPages(sql,0,3), Tb_thematic_make.class);

        List<Tb_thematic_make> thematic_makes=new ArrayList<>();
        Page<Tb_thematic_make> thematic_makePage=thematicRepository.findAll(sp,pageRequest);
        if(thematic_makePage.getTotalElements()>0){
            thematic_makes=thematic_makePage.getContent();
        }
        List<Tb_thematic_make> returnList=new ArrayList<>();
        returnList.addAll(thematic_makes);
        returnList.addAll(query.getResultList());
        return returnList;
    }

    public List<Tb_thematic_detail_make> getThematicdetail(String thematicid){
        return thematicDetailRepository.findByThematicid(thematicid);
    }

    public List<Tb_thematic_make> getThematic(String[] thematicIds) {
        return thematicRepository.findThematicByThematicidIn(thematicIds);
    }

    public String releasenetwork(String thematicIds,Tb_datareceive datareceive) {
        String[] ids = thematicIds.split(",");
        List<Tb_thematic_make> tbThematicMakes = thematicRepository.findThematicByThematicidIn(ids);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(new Date());
        String usefileName = "专题发布_" + time;
        try {
            String path = rootpath + File.separator + "datarelease" + File.separator +"thematic" +File.separator  + "临时目录"+ File.separator + usefileName;// 创建临时路径文件夹
            FileUtils.forceMkdir(new File(path));
            for (Tb_thematic_make tb_thematic_make : tbThematicMakes) {
                String thematicPath = path + File.separator + tb_thematic_make.getTitle();
                FileUtils.forceMkdir(new File(thematicPath));
                //创建说明文件.txt
                File txtfile = new File(thematicPath + File.separator +"说明文件.txt");
                if (!txtfile.exists()) {
                    txtfile.createNewFile();
                }
                BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(txtfile, true),
                        "UTF-8")); // 指定编码格式，以免读取时中文字符异常
                fw.append("专题名称:"+ tb_thematic_make.getTitle());
                fw.newLine();
                fw.append("专题描述:"+ tb_thematic_make.getThematiccontent());
                fw.newLine();
                fw.append("专题发布时间:"+ tb_thematic_make.getPublishtime());
                fw.newLine();
                fw.append("专题大小:"+ tb_thematic_make.getFilesize());
                fw.newLine();
                List<Tb_thematic_detail> details =
                        newthematicDetailRepository.findByThematicid(tb_thematic_make.getThematicid());
                fw.append("目录数量:"+ details.size());
                fw.newLine();
                fw.flush(); // 全部写入缓存中的内容
                if (fw != null) {
                    fw.close();
                }
                //当信息编研条目数大于0才创建专题目录.xml
                if(details.size()>0){
                    exportExcelService.createThematicxml(thematicPath,details);
                }
                try {
                    //其他（文件夹，存储：①专题的背景图；②如果是已发布，还有专题发布数据包-即专题利用下载的数据包；③等等）
                    String thematicOther = thematicPath + File.separator + "其它" + File.separator + "专题的背景图";
                    FileUtils.forceMkdir(new File(thematicOther));
                    //转存背景图
                    String backgroundName = tb_thematic_make.getBackgroundpath().substring(tb_thematic_make.getBackgroundpath().lastIndexOf("/") + 1);
                    if (tb_thematic_make.getBackgroundpath().indexOf("thematic_def.png") != -1) {
                        FileUtils.copyFile(ResourceUtils.getFile("classpath:static/img/icon/thematic_def.png"), new File(thematicOther + File.separator + backgroundName));
                    } else {
                        String backgroundpath = rootpath + tb_thematic_make.getBackgroundpath();
                        if(FileUtil.isexists(backgroundpath)){
                            FileUtils.copyFile(new File(backgroundpath), new File(thematicOther + File.separator + backgroundName));
                        }
                    }
                    //转存专题发布数据包-即专题利用下载的数据包
                    String thematicUtilize = thematicPath + File.separator + "其它" + File.separator + "专题发布数据包";
                    FileUtils.forceMkdir(new File(thematicUtilize));
                    String utilizeName =
                            tb_thematic_make.getFilepath().substring(tb_thematic_make.getFilepath().lastIndexOf("\\") + 1);
                    String utilizePath = rootpath + tb_thematic_make.getFilepath();
                    if(FileUtil.isexists(utilizePath)){
                        FileUtils.copyFile(new File(utilizePath), new File(thematicUtilize + File.separator + utilizeName));
                    }

                    //电子文件（文件夹，存储：有电子文件的条目id文件夹，条目id文件夹中则存储条目的电子文件）
                    for(Tb_thematic_detail tbThematicDetail : details){
                        String thematicEle =
                                thematicPath + File.separator + "电子文件" + File.separator + tbThematicDetail.getThematicdetilid().trim();
                        FileUtils.forceMkdir(new File(thematicEle));
                        List<Tb_electronic> electronics =
                                electronicRepository.findByEntryid(tbThematicDetail.getThematicdetilid());
                        for(Tb_electronic tbElectronic : electronics){
                            if(!"folder".equals(tbElectronic.getFilefolder())) {
                                String elePath = rootpath +tbElectronic.getFilepath() + File.separator + tbElectronic.getFilename();
                                if (FileUtil.isexists(elePath)) {
                                    FileUtils.copyFile(new File(elePath), new File(thematicEle + File.separator + tbElectronic.getFilename()));
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // zip 完整路径
            String zippath =
                    rootpath + File.separator + "datarelease" + File.separator + "thematic" + File.separator + "待接收" + File.separator + usefileName + ".zip";
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, "");
            ZipFile zipFile =new ZipFile(new File(zippath));
            //由于压缩包的注释中文会乱码，所以需要把它转换成unicode,拿出来的再转回来
            datareceive.setTransfertitle(gbEncoding(datareceive.getTransfertitle()));
            datareceive.setTransdesc(gbEncoding(datareceive.getTransdesc()));
            datareceive.setTransuser(gbEncoding(datareceive.getTransuser()));
            datareceive.setTransorgan(gbEncoding(datareceive.getTransorgan()));
            datareceive.setSequencecode(gbEncoding(datareceive.getSequencecode()));
            zipFile.setComment(JSON.toJSONString(datareceive));
            ZipUtils.del(path);
            return zippath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String gbEncoding(String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }

    public Tb_datareceive getThematicDoc(String thematicIds){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Tb_datareceive datareceive = new Tb_datareceive();
        datareceive.setTransuser(userDetails.getRealname());
        datareceive.setTransorgan(userDetails.getOrganid());
        String[] ids = thematicIds.split(",");
        //计算条目数量
        datareceive.setTranscount(String.valueOf(ids.length));
        datareceive.setTransferstcount("0");
        return datareceive;
    }
}
