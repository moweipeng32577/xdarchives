package com.wisdom.web.service;

import com.wisdom.util.*;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.xml.crypto.Data;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yl on 2017/11/1.
 * 专题模块service
 */
@Service
@Transactional
public class ThematicService {
    @Autowired
    ThematicRepository thematicRepository;

    @Autowired
    ThematicMakeRepository thematicMakeRepository;

    @Autowired
    ThematicDetailRepository thematicDetailRepository;

    @Autowired
    ElectronicRepository electronicRepository;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    ElectronicService electronicService;

    @Autowired
    ExportExcelService exportExcelService;

    @Autowired
    UserFunctionRepository userFunctionRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    TaskRepository taskRepository;

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
    public Page<Tb_thematic> getThematic(int page, int limit) {
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        Page<Tb_thematic> thematics = thematicRepository.findAll(pageRequest);
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
    public Page<Tb_thematic> findBySearch(int page, int limit, String condition, String operator, String content, Sort sort) {
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        Specifications specifications = null;
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        return thematicRepository.findAll(Specifications.where(specifications), pageRequest);
    }

    public Tb_thematic saveThematic(Tb_thematic tb_thematic,String backgroundpath) {
        if("".equals(backgroundpath)||backgroundpath==null){
            tb_thematic.setBackgroundpath("/static/img/icon/thematic_def.png");
        }else{
            tb_thematic.setBackgroundpath(backgroundpath);
        }
        tb_thematic.setCreatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return thematicRepository.save(tb_thematic);
    }

    /**
     * 删除专题
     *
     * @param thematicids 专题id数组
     * @return
     */
    public Integer deleteThematic(String[] thematicids) {
        List<Tb_thematic_detail> thematic_details = thematicDetailRepository.findByThematicidIn(thematicids);
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
        List<Tb_thematic> thematics = thematicRepository.findAll();
        for (Tb_thematic tb_thematic : thematics) {
            ExtNcTree extNcTree = new ExtNcTree();
            extNcTree.setFnid(tb_thematic.getThematicid());
            extNcTree.setText(tb_thematic.getTitle());
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
    public Page<Tb_thematic_detail> findTDetailByThematicid(int page, int limit, String condition, String operator, String content, String thematicid, Sort sort) {
//        Sort sortobj = null;
//        if(sort==null) {//默认按章节排序
//            List<Sort.Order> sorts = new ArrayList<>();
//            sorts.add(new Sort.Order(Sort.Direction.ASC, "chapter"));
//            sorts.add(new Sort.Order(Sort.Direction.ASC, "section"));
//            sortobj=new Sort(sorts);
//        }else {
//            sortobj=sort;
//        }
        PageRequest pageRequest = new PageRequest(page - 1, limit,sort);
        Specification<Tb_thematic_detail> searchid = getSearchThematicidCondition(thematicid);
        Specifications specifications = Specifications.where(searchid);
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        return thematicDetailRepository.findAll(specifications, pageRequest);
    }

    public Tb_thematic_detail saveThematicDetail(Tb_thematic_detail tb_thematic_detail, String[] mediaids) {
        tb_thematic_detail.setThematicdetilid(UUID.randomUUID().toString().replace("-", ""));
        Tb_thematic_detail thematic_detail = thematicDetailRepository.save(tb_thematic_detail);
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

    public Tb_thematic_detail updateThematicDetail(Tb_thematic_detail tb_thematic_detail) {
        return thematicDetailRepository.save(tb_thematic_detail);
    }

    /**
    * 更改专题状态
    *
    * @param thematicIds
    * @return {@link boolean}
    * @throws
    **/
    @Transactional
    public boolean releaseThmatic(String thematicIds,String publishstate){
        String[] ids=thematicIds.split(",");
        int count=0;
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(String id: ids){
            String time =sdf.format(new Date());
            count+=thematicRepository.updateThematicForPublishstate(publishstate,time,id);
        }
        return count>0?true:false;
    }

    //发布数字资源zip包
    public String releasenetwork(String thematicIds) {
        String[] ids = thematicIds.split(",");
        List<Tb_thematic> tbThematicMakes = thematicRepository.findThematicByThematicidIn(ids);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(new Date());
        String usefileName = "专题发布_" + time;
        try {
            String path = rootpath + File.separator + "datarelease" + File.separator +"thematic" +File.separator  + "临时目录"+ File.separator + usefileName;// 创建临时路径文件夹
            FileUtils.forceMkdir(new File(path));
            for (Tb_thematic tb_thematic : tbThematicMakes) {
                String thematicPath = path + File.separator + tb_thematic.getTitle();
                FileUtils.forceMkdir(new File(thematicPath));
                //创建说明文件.txt
                File txtfile = new File(thematicPath + File.separator +"说明文件.txt");
                if (!txtfile.exists()) {
                    txtfile.createNewFile();
                }
                BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(txtfile, true),
                        "UTF-8")); // 指定编码格式，以免读取时中文字符异常
                fw.append("专题名称:"+ tb_thematic.getTitle());
                fw.newLine();
                fw.append("专题描述:"+ tb_thematic.getThematiccontent());
                fw.newLine();
                fw.append("专题发布时间:"+ tb_thematic.getPublishtime());
                fw.newLine();
                fw.append("专题大小:"+ tb_thematic.getFilesize());
                fw.newLine();
                List<Tb_thematic_detail> details =
                        thematicDetailRepository.findByThematicid(tb_thematic.getThematicid());
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
                    //电子文件（文件夹，存储：有电子文件的条目id文件夹，条目id文件夹中则存储条目的电子文件）
                    for(Tb_thematic_detail tbThematicDetail : details){
                        String thematicEle =
                                thematicPath + File.separator + "电子文件" + File.separator + tbThematicDetail.getThematicdetilid().trim();
                        FileUtils.forceMkdir(new File(thematicEle));
                        List<Tb_electronic> electronics =
                                electronicRepository.findByEntryid(tbThematicDetail.getThematicdetilid());
                        for(Tb_electronic tbElectronic : electronics){
                            String elePath = rootpath + tbElectronic.getFilepath() + File.separator + tbElectronic.getFilename();
                            if(FileUtil.isexists(elePath)){
                                FileUtils.copyFile(new File(elePath), new File(thematicEle + File.separator + tbElectronic.getFilename()));
                            }
                        }
                    }
                    //转存专题发布数据包-即专题利用下载的数据包
                    String thematicUtilize = thematicPath + File.separator + "其它" + File.separator + "专题发布数据包";
                    FileUtils.forceMkdir(new File(thematicUtilize));
                    String utilizeName ="";
                    if(tb_thematic.getFilepath()!=null&&"".equals(tb_thematic.getFilepath())) {
                        utilizeName = tb_thematic.getFilepath().substring(tb_thematic.getFilepath().lastIndexOf("\\") + 1);
                        String utilizePath = rootpath + tb_thematic.getFilepath();
                        if (FileUtil.isexists(utilizePath) && FileUtil.isexists(thematicUtilize + File.separator + utilizeName)) {
                            FileUtils.copyFile(new File(utilizePath), new File(thematicUtilize + File.separator + utilizeName));
                        }
                    }

                    //其他（文件夹，存储：①专题的背景图；②如果是已发布，还有专题发布数据包-即专题利用下载的数据包；③等等）
                    String thematicOther = thematicPath + File.separator + "其它" + File.separator + "专题的背景图";
                    FileUtils.forceMkdir(new File(thematicOther));
                    //转存背景图
                    String backgroundName = "";
                    if(tb_thematic.getBackgroundpath()!=null&&!"".equals(tb_thematic.getBackgroundpath())){
                        backgroundName=tb_thematic.getBackgroundpath().substring(tb_thematic.getBackgroundpath().lastIndexOf("/") + 1);
                    }
                    if (tb_thematic.getBackgroundpath().indexOf("thematic_def.png") != -1) {
                        FileUtils.copyFile(ResourceUtils.getFile("classpath:static/img/icon/thematic_def.png"), new File(thematicOther + File.separator + backgroundName));
                    } else {
                        String backgroundpath = rootpath + tb_thematic.getBackgroundpath();
                        if(FileUtil.isexists(backgroundpath)){
                            FileUtils.copyFile(new File(backgroundpath), new File(thematicOther + File.separator + backgroundName));
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
            ZipUtils.del(path);
            return zpath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //编研管理-发布数字资源zip包
    public String releasenetworkMake(String thematicIds) {
        String[] ids = thematicIds.split(",");
        List<Tb_thematic_make> tbThematicMakes = thematicMakeRepository.findThematicByThematicidIn(ids);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(new Date());
        String usefileName = "专题发布_" + time;
        try {
            String path = rootpath + File.separator + "datarelease" + File.separator +"thematic" +File.separator  + "临时目录"+ File.separator + usefileName;// 创建临时路径文件夹
            FileUtils.forceMkdir(new File(path));
            for (Tb_thematic_make tb_thematic : tbThematicMakes) {
                String thematicPath = path + File.separator + tb_thematic.getTitle();
                FileUtils.forceMkdir(new File(thematicPath));
                //创建说明文件.txt
                File txtfile = new File(thematicPath + File.separator +"说明文件.txt");
                if (!txtfile.exists()) {
                    txtfile.createNewFile();
                }
                BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(txtfile, true),
                        "UTF-8")); // 指定编码格式，以免读取时中文字符异常
                fw.append("专题名称:"+ tb_thematic.getTitle());
                fw.newLine();
                fw.append("专题描述:"+ tb_thematic.getThematiccontent());
                fw.newLine();
                fw.append("专题发布时间:"+ tb_thematic.getPublishtime());
                fw.newLine();
                fw.append("专题大小:"+ tb_thematic.getFilesize());
                fw.newLine();
                List<Tb_thematic_detail> details =
                        thematicDetailRepository.findByThematicid(tb_thematic.getThematicid());
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
                    String backgroundName = "";
                    if(tb_thematic.getBackgroundpath()!=null&&!"".equals(tb_thematic.getBackgroundpath())){
                        backgroundName=tb_thematic.getBackgroundpath().substring(tb_thematic.getBackgroundpath().lastIndexOf("/") + 1);
                    }
                    if (tb_thematic.getBackgroundpath().indexOf("thematic_def.png") != -1) {
                        FileUtils.copyFile(ResourceUtils.getFile("classpath:static/img/icon/thematic_def.png"), new File(thematicOther + File.separator + backgroundName));
                    } else {
                        String backgroundpath = rootpath + tb_thematic.getBackgroundpath();
                        if(FileUtil.isexists(backgroundpath)){
                            FileUtils.copyFile(new File(backgroundpath), new File(thematicOther + File.separator + backgroundName));
                        }
                    }

                    //电子文件（文件夹，存储：有电子文件的条目id文件夹，条目id文件夹中则存储条目的电子文件）
                    for(Tb_thematic_detail tbThematicDetail : details){
                        String thematicEle =
                                thematicPath + File.separator + "电子文件" + File.separator + tbThematicDetail.getThematicdetilid().trim();
                        FileUtils.forceMkdir(new File(thematicEle));
                        List<Tb_electronic> electronics =
                                electronicRepository.findByEntryid(tbThematicDetail.getThematicdetilid());
                        for(Tb_electronic tbElectronic : electronics){
                            String elePath = rootpath + tbElectronic.getFilepath() + File.separator + tbElectronic.getFilename();
                            if(FileUtil.isexists(elePath)){
                                FileUtils.copyFile(new File(elePath), new File(thematicEle + File.separator + tbElectronic.getFilename()));
                            }
                        }
                    }

                    //转存专题发布数据包-即专题利用下载的数据包
                    String thematicUtilize = thematicPath + File.separator + "其它" + File.separator + "专题发布数据包";
                    FileUtils.forceMkdir(new File(thematicUtilize));
                    String utilizeName ="";
                    if(tb_thematic.getFilepath()!=null&&!"".equals(tb_thematic.getFilepath())){
                        utilizeName= tb_thematic.getFilepath().substring(tb_thematic.getFilepath().lastIndexOf("\\") + 1);
                    }
                    String utilizePath = rootpath + tb_thematic.getFilepath();
                    if(FileUtil.isexists(utilizePath)&&FileUtil.isexists(thematicUtilize + File.separator + utilizeName)){
                        FileUtils.copyFile(new File(utilizePath), new File(thematicUtilize + File.separator + utilizeName));
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
            ZipUtils.del(path);
            return zpath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除专题内容
     *
     * @param thematicdetilids 专题内容id数组
     * @return
     */
    public Integer deleteThematicDetail(String[] thematicdetilids) {
        int count = thematicDetailRepository.deleteByThematicdetilidIn(thematicdetilids);
        return count;
    }

    /**
     * 另开线程删除专题关联电子文件条目和电子文件
     *
     * @param thematicdetilids 专题内容id数组
     *
     */
    public void delElectronic(String[] thematicdetilids) {
        Thread thread = new Thread(() -> {
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
        });
        thread.start();
    }

    public Integer updateThematicForPublishstate(String type, String thematicids) {
        //SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();//获取安全对象
        int count=0;
        String[] ids = thematicids.split(",");
        for (String thematicid : ids) {
            String publishtime = "已发布".equals(type)?new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()):"";
            if(thematicRepository.updateThematicForPublishstate(type,publishtime,thematicid)>0){
                /*if("已发布".equals(type)){
                    Tb_thematic tb_thematic=thematicRepository.findByThematicid(thematicid);
                    List<Tb_thematic_detail> thematic_details = thematicDetailRepository.findByThematicid(thematicid);//获取内容

                    List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
                    //Tb_thematic_detail thematic_detail1;
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
                    String excelPath = rootpath + "/thematic/utilize/" + userDetails.getLoginname() + "/" + tb_thematic
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
                        String srcPath = rootpath + "/" + electronic.getFilepath() + "/" + electronic.getFilename();
                        String desPath = rootpath + "/" +
                                electronic.getFilepath().replace("/thematic/prod/", "/thematic/utilize/" +
                                        userDetails.getLoginname() + "/" + tb_thematic.getTitle() + "/") + "/" + electronic
                                .getFilename();
                        try {
                            File src = new File(srcPath);
                            File des = new File(desPath);
                            FileUtils.copyFile(src, des);//拷贝电子文件
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        String outName=excelPath + "/" + tb_thematic.getTitle() + ".xls";
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
                }*/
                count++;
            }
        }
        return count;
    }

    public Integer updateThematicForPublish(String type, String thematicids) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();//获取安全对象
        int count=0;
        String[] ids = thematicids.split(",");
        for(int i=0;i<ids.length;i++){
            String publishtime = "已发布".equals(type)?new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()):"";
            if(thematicRepository.updateThematicForPublishstate(type,publishtime,ids[i])>0){
                if("已发布".equals(type)){
                    Tb_thematic tb_thematic=thematicRepository.findByThematicid(ids[i]);
                    List<Tb_thematic_detail> thematic_details = thematicDetailRepository.findByThematicid(ids[i]);//获取内容
                    String title = tb_thematic.getTitle();
                    String excelPath = rootpath + "/thematic/utilize/" + userDetails.getLoginname() + "/" +title;//拷贝后根路径
                    File eleDir = new File(excelPath);
                    if (!eleDir.exists()) {
                        eleDir.mkdirs();
                    }
                    //判断有没有存在之前压缩的zip，有的就删除
                    File zipFile=new File(excelPath+".zip");
                    if (zipFile.exists()) {
                        zipFile.delete();
                    }
                    List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
                    for (Tb_thematic_detail thematic_detail : thematic_details) {//生成excel数据并保存在map中
                        Map<String, Object> mapValue = new HashMap<String, Object>();
                        mapValue.put("title", thematic_detail.getTitle());
                        mapValue.put("date", thematic_detail.getFiledate());
                        mapValue.put("responsibleperson", thematic_detail.getResponsibleperson());
                        mapValue.put("filecode", thematic_detail.getFilecode());
                        mapValue.put("subheadings", thematic_detail.getSubheadings());
                        mapValue.put("mediatext", thematic_detail.getMediatext());
                        //copyThematicElectronics(thematic_detail.getThematicdetilid(),"",userDetails.getLoginname(),title,thematic_detail.getTitle(),"");
                        listmap.add(mapValue);
                    }
                    String names[] = {"题名", "时间", "责任者", "文件编号", "主题词","电子文件"};//列名
                    String keys[] = {"title", "date", "responsibleperson", "filecode", "subheadings","mediatext"};//map中的key
                    String[] thematicdetilid = GainField.getFieldValues(thematic_details, "thematicdetilid").length == 0 ? new String[]{""} : GainField.getFieldValues(thematic_details, "thematicdetilid");
                    List<Tb_electronic> electronics = electronicRepository.findByEntryidInOrderBySortsequence(thematicdetilid);//获取专题信息电子文件
                    for (Tb_electronic electronic : electronics) {
                        if(!"folder".equals(electronic.getFilefolder())){
                            String srcPath = rootpath + "/" + electronic.getFilepath() + "/" + electronic.getFilename();
                            String desPath = rootpath + "/" +
                                    electronic.getFilepath().replace("/thematic/prod/", "/thematic/utilize/" +
                                            userDetails.getLoginname() + "/" + tb_thematic.getTitle() + "/") + "/" + electronic
                                    .getFilename();
                            try {
                                File src = new File(srcPath);
                                File des = new File(desPath);
                                if(src.exists()) {
                                    FileUtils.copyFile(src, des);//拷贝电子文件
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        String outName=excelPath + "/" + title + ".xls";
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
                    thematicRepository.updateThematicidFilePath(filePath,fileSize,ids[i]);
                    delFolder(excelPath);//打包完，删除文件夹及文件夹里面的数据
                }
                count++;
            }
        }
        return count;
    }

    //递归调用
    public void copyThematicElectronics(String entryid,String fileClassId,String userName,String title,String detailTitle,String parentFolderName) {
        List<Tb_electronic> electronicList = new ArrayList<Tb_electronic>();
        if(fileClassId==null||"".equals(fileClassId)){
            electronicList = electronicRepository.findByEntryidAndFileclassidNull(entryid);
        }else {
            electronicList = electronicRepository.findByEntryidAndFileclassid(entryid,fileClassId);
        }
        String  folderName= new String(parentFolderName+"");
        for (Tb_electronic ele : electronicList) {
            if("folder".equals(ele.getFilefolder())){
                String folderPath = rootpath + "/thematic/utilize/" + userName+"/" + title+"/"+detailTitle+parentFolderName;//拷贝后根路径
                File eleDir = new File(folderPath);
                if (!eleDir.exists()) {
                    eleDir.mkdirs();
                }
                folderName+="/"+ele.getFilename();
                System.out.println("---------------folderPath------"+folderPath);
                System.out.println("---------------folderName------"+folderName);
                copyThematicElectronics(entryid,ele.getEleid(),userName,title,detailTitle,folderName);
            }else {
                String srcPath = rootpath + ele.getFilepath() + "/" + ele.getFilename();
                String desPath = rootpath + ele.getFilepath().replace("/thematic/prod/", "/thematic/utilize/" +
                                    userName + "/" + title+"/")+ "/"+detailTitle+parentFolderName + "/" + ele.getFilename();
                System.out.println("---------------desPath------"+desPath);
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
    }

    /**
     * 另开线程打包专题发布内容
     *
     * @param thematicids 专题内容id字符串
     *
     */
    public void thematicForPublishFolder(String thematicids){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();//获取安全对象
        String[] ids = thematicids.split(",");
        Thread thread = new Thread(() -> {
            for (String thematicid : ids) {
                Tb_thematic tb_thematic=thematicRepository.findByThematicid(thematicid);
                List<Tb_thematic_detail> thematic_details = thematicDetailRepository.findByThematicid(thematicid);//获取内容

                List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
                //Tb_thematic_detail thematic_detail1;
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
                String excelPath = rootpath + "/thematic/utilize/" + userDetails.getLoginname() + "/" + tb_thematic
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
                    String srcPath = rootpath + "/" + electronic.getFilepath() + "/" + electronic.getFilename();
                    String desPath = rootpath + "/" +
                            electronic.getFilepath().replace("/thematic/prod/", "/thematic/utilize/" +
                                    userDetails.getLoginname() + "/" + tb_thematic.getTitle() + "/") + "/" + electronic
                            .getFilename();
                    try {
                        File src = new File(srcPath);
                        File des = new File(desPath);
                        FileUtils.copyFile(src, des);//拷贝电子文件
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    String outName=excelPath + "/" + tb_thematic.getTitle() + ".xls";
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
        });
        thread.start();
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
        Specification<Tb_thematic> searchid = new Specification<Tb_thematic>() {
            @Override
            public Predicate toPredicate(Root<Tb_thematic> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get("publishstate"), "已发布");
                return criteriaBuilder.or(p);
            }
        };
        Specifications specifications = Specifications.where(searchid);
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        Page<Tb_thematic> pages = thematicRepository.findAll(specifications, pageRequest);
        ;//获取已发布专题
        List<Tb_thematic> thematics = pages.getContent();
        String[] thematicids = GainField.getFieldValues(thematics, "thematicid").length == 0 ? new String[]{""} : GainField.getFieldValues(thematics, "thematicid");
        List<Tb_thematic_detail> thematic_details = thematicDetailRepository.findByThematicidIn(thematicids);//获取内容
        List<Tb_electronic> electronics = electronicRepository.getElectronics(thematicids);//获取内容电子文件
        Map<String, Integer> mediasizemap = new HashMap<>();
        for (Tb_thematic_detail thematic_detail : thematic_details) {//遍历统计电子文件大小
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
        String msg="重复导入";
        PageRequest pageRequest = new PageRequest(0, 200);
        Page<Tb_entry_index> page = entryIndexRepository.findByEntryidIn(dataids, pageRequest);
        List<Tb_entry_index> entry_indices = page.getContent();
        Boolean isRepeat=true;
        for(Tb_entry_index entry_index : entry_indices){
//            if(thematicDetailRepository.findByThematicidAndTitle(treeid,entry_index.getTitle()).size()>0){
//                msg=msg +"'"+entry_index.getTitle()+"'导入失败，题名不能重复<br />";
//            }else{

            List<Tb_thematic_detail> thematic_detailFind = thematicDetailRepository.findByEntryidAndThematicid(entry_index.getEntryid(),treeid);
            if(thematic_detailFind.size()>0){ //重复导入
                //msg=msg +"'"+entry_index.getTitle()+"'重复导入<br />";
                continue;
            }else{
                if(isRepeat){
                    isRepeat=false;
                    msg="";
                }
                msg=msg +"'"+entry_index.getTitle()+"'导入成功<br />";
            }
            List<Tb_electronic> electronics = electronicRepository.findByEntryidOrderBySortsequence(entry_index.getEntryid());
            String mediatext = "";
            for (Tb_electronic electronic : electronics) {
                mediatext += "," + electronic.getFilename();
            }
            mediatext = "".equals(mediatext) ? "" : mediatext.substring(1);
            Tb_thematic_detail thematic_detail = thematicDetailRepository.save(new
                    Tb_thematic_detail(treeid, entry_index.getTitle(), entry_index.getDescriptiondate(), entry_index
                    .getResponsible(), entry_index.getFilenumber(), entry_index.getKeyword(), mediatext,entry_index.getEntryid()));
            for (Tb_electronic e : electronics) {
                Tb_electronic tb_electronic = new Tb_electronic();
                BeanUtils.copyProperties(e,tb_electronic);
                tb_electronic.setEleid("");
                tb_electronic.setEntryid(thematic_detail.getThematicdetilid());
                tb_electronic.setFilepath(renameToThematic(thematic_detail.getThematicdetilid(),rootpath + e
                        .getFilepath(), e.getFilename()));
                electronicRepository.save(tb_electronic);
            }
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
        List<Tb_thematic_detail> thematic_details = thematicDetailRepository.findByThematicid(id);
        List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
        for (Tb_thematic_detail thematic_detail : thematic_details) {//生成excel数据并保存在map中
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

    public Page<Tb_thematic> findTbThematicPage(int page, int limit, String condition, String operator, String
            content,Sort sort,String thematictypes) {
        PageRequest pageRequest = new PageRequest(page - 1, limit,sort);
        Specification<Tb_thematic> searchid = new Specification<Tb_thematic>() {
            @Override
            public Predicate toPredicate(Root<Tb_thematic> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get("publishstate"), "已发布");
                return criteriaBuilder.or(p);
            }
        };
        Specifications specifications = Specifications.where(searchid);
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        return thematicRepository.findAll(Specifications.where(specifications), pageRequest);
    }

    public Page<Tb_thematic> findTbThematicBythematictypes(int page, int limit,Sort sort,String thematictypes,String condition, String operator, String
            content){
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        Specifications sp = Specifications.where(getSearchpublishstateConditions("已发布"));//已发布的专题
        if(!"方志馆".equals(thematictypes) && thematictypes!=null ){
            sp = sp.and(getSearchthematictypesCondition(thematictypes));//根据类型查询专题
        }
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        return thematicRepository.findAll(sp, pageRequest);
    }

    //根据类型查询专题
    public static Specification<Tb_thematic> getSearchthematictypesCondition(String thematictypes){
        Specification<Tb_thematic> getSearchthematictypesCondition = null;
        getSearchthematictypesCondition = new Specification<Tb_thematic>() {
            @Override
            public Predicate toPredicate(Root<Tb_thematic> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get("thematictypes"),thematictypes);
                return criteriaBuilder.or(p);
            }
        };
        return getSearchthematictypesCondition;
    }

    public static Specification<Tb_thematic> getSearchpublishstateConditions(String publishstate){
        Specification<Tb_thematic> getSearchpublishstateConditions = null;
        getSearchpublishstateConditions = new Specification<Tb_thematic>() {
            @Override
            public Predicate toPredicate(Root<Tb_thematic> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get("publishstate"), publishstate);
                return criteriaBuilder.or(p);
            }
        };
        return getSearchpublishstateConditions;
    }

    public String findFilePathByThematicid(String thematicid) {
        Tb_thematic tbThematic = thematicRepository.findByThematicid(thematicid);
        return rootpath + tbThematic.getFilepath();
    }

    public Tb_thematic findByThematicid(String thematicid) {
        return thematicRepository.findByThematicid(thematicid);
    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
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
        Tb_thematic tb_thematic=thematicRepository.findByThematicid(thematicid);
        tb_thematic.setBackgroundpath("/thematic/background/" + userDetails.getUsername()+"/"+filename);
        return "/thematic/background/" + userDetails.getUsername()+"/"+filename;
    }

    public static Specification<Tb_thematic_detail> getSearchThematicidCondition(String thematicid){
        Specification<Tb_thematic_detail> searchThematicidCondition = new Specification<Tb_thematic_detail>() {
            @Override
            public Predicate toPredicate(Root<Tb_thematic_detail> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get("thematicid"), thematicid);
                return criteriaBuilder.or(p);
            }
        };
        return searchThematicidCondition;
    }

    public Tb_thematic findByTitle(String title){
        return thematicRepository.findByTitle(title);
    }

    public List<Tb_thematic> getThematicbyState(){
        Specifications sp = Specifications.where(new SpecificationUtil("publishstate","equal","已发布"));
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(new Sort.Order(Sort.Direction.DESC,"publishtime"));
        PageRequest pageRequest = new PageRequest(0,5,new Sort(sorts));
        return thematicRepository.findAll(sp,pageRequest).getContent();
    }

    public List<Tb_thematic_detail> getThematicdetail(String thematicid){
        return thematicDetailRepository.findByThematicid(thematicid);
    }

    public List<Tb_thematic> getThematic(String[] thematicIds) {
        return thematicRepository.findThematicByThematicidIn(thematicIds);
    }

    /**
     * 成果提交
     *
     * @param thematicIds
     * @return {@link boolean}
     * @throws
     **/
    @Transactional
    public boolean updateThmaticStatus(String thematicIds){
        String[] ids=thematicIds.split(",");
        int count=0;
        String submitedtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        for(String id: ids){
            count+=thematicRepository.updateThematicPublishstateAndSubmitedtime("已提交",id,submitedtime);
        }
        return count>0?true:false;
    }

    //成果提交审核提醒通知
    public void task(String[] ids,String type){
        List<Tb_thematic> list = thematicRepository.findThematicByThematicidIn(ids);
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> userids = userFunctionRepository.findUseridsByFunctionname(type);
        List<String> useridList = userRoleRepository.findUseridsByFunctionname(type);
        userids.addAll(useridList);  //所有拥有功能权限的用户
        List<Tb_task> tasks = new ArrayList<>();
        for(String userid : userids){
            for(Tb_thematic tb_thematic : list){
                Tb_task task = new Tb_task();
                task.setState(Tb_task.STATE_WAIT_HANDLE);// 处理中
                task.setTime(new Date());
                task.setLoginname(userid);//审核人、提交人
                task.setText(userDetails.getRealname() + " 提交一条新编研成果审批！");
                task.setType(type);
                task.setBorrowmsgid(tb_thematic.getThematicid());
                tasks.add(task);
            }
        }
        taskRepository.save(tasks);
    }

    public Page<Tb_thematic> findBySearchByState(int page, int limit, String condition, String operator, String content, Sort sort,String state) {
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        Specifications specifications = Specifications.where(getSearchpublishstateConditions(state));
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        return thematicRepository.findAll(Specifications.where(specifications), pageRequest);
    }
}
