package com.wisdom.web.service;

import com.wisdom.util.*;
import com.wisdom.web.controller.ClassifySearchController;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.ArchivesMigrateRepository;
import com.wisdom.web.repository.ElectronicRepository;
import com.wisdom.web.repository.MigrateEntryRepository;
import com.wisdom.web.security.SecurityUser;
import org.apache.commons.io.FileUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Leo on 2020/8/12 0012.
 */
@Service
@Transactional
public class ArchivesMigrateService {

    @Autowired
    ArchivesMigrateRepository archivesMigrateRepository;

    @Autowired
    MigrateEntryRepository migrateEntryRepository;

    @Autowired
    ClassifySearchService classifySearchService;

    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    ElectronicRepository electronicRepository;

    @Autowired
    ClassifySearchController classifySearchController;

    //打包状态
    private static Integer migrateState=0;

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录

    public Page<Tb_archives_migrate> getArchivesMigrateBySearch(int page, int limit, String condition,
                                                                String operator, String content, String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        Specifications sp=null;
        if(content!=null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit, sortobj);
        return archivesMigrateRepository.findAll(sp, pageRequest);
    }

    public Tb_archives_migrate getArchivesMigrate(){

        return null;
    }

    public Tb_archives_migrate save(Tb_archives_migrate archives_migrate){
        archives_migrate.setMigid(null);
        archives_migrate.setMigratecount("0");
        archives_migrate.setMigratestate(archives_migrate.STATE_INSERT);
        return archivesMigrateRepository.save(archives_migrate);
    }

    public List<Tb_migrate_entry> saveArchivesMigrateEntry(String migId,String[] entryids){
        List<Tb_migrate_entry> list=new ArrayList<>();
        for (String entryid : entryids) {
            Tb_migrate_entry migrate_entry=new Tb_migrate_entry();
            migrate_entry.setEntryid(entryid);
            migrate_entry.setMigid(migId);
            list.add(migrate_entry);
        }
        archivesMigrateRepository.updateMigCount(migId,list.size());
        return migrateEntryRepository.save(list);
    }

    public int deletArchivesMigrateEntry(String migId,String[] entryids){
        archivesMigrateRepository.updateMigCount(migId,(entryids.length*-1));
        return migrateEntryRepository.deleteByMigidAndEntryidIn(migId,entryids);
    }

    /**
     * 创建一个线程在后台打包，一次只能打包一个
     * @param migId
     * @return 0 空闲中   1 打包中
     */
    public Integer migratePack(String migId){
        Thread migratePackThread = new Thread() {
            @Override
            public void run() {
                synchronized (this) {
                    if(migrateState!=1) {
                        migrateState = 1;
                        Tb_archives_migrate migrate = archivesMigrateRepository.findByMigid(migId);
                        migrate.setMigratestate(Tb_archives_migrate.STATE_TRANSFER_ING);
                        archivesMigrateRepository.save(migrate);
                        pack(migId,migrate);
                    }
                }
            }
        };
        if(migrateState!=1) {
            migratePackThread.start();
        }
        return migrateState;
    }

    //打包
    public void pack(String migId,Tb_archives_migrate migrate){
        try {
            String path = rootpath + File.separator + "datarelease" + File.separator +"migrate" +File.separator  + "临时目录"+ File.separator+migId.trim() ;// 创建临时路径文件夹
            String thematicPath = path ;
            FileUtils.forceMkdir(new File(thematicPath));
            //创建说明文件.txt
            File txtfile = new File(thematicPath + File.separator +"说明文件.txt");
            if (!txtfile.exists()) {
                txtfile.createNewFile();
            }
            BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(txtfile, true),
                    "UTF-8")); // 指定编码格式，以免读取时中文字符异常
            fw.append("内容描述:"+ migrate.getMigratedesc());
            fw.newLine();
            fw.append("迁移人:"+ migrate.getMigrateuser());
            fw.newLine();
            fw.append("迁移时间:"+ migrate.getMigratedate());
            fw.newLine();
            fw.append("迁移数量:"+ migrate.getMigratecount());
            fw.newLine();
            fw.append("备注:"+ migrate.getRemarks());
            fw.newLine();
            fw.flush(); // 全部写入缓存中的内容
            if (fw != null) {
                fw.close();
            }
            List<String> migrateEntryList=migrateEntryRepository.findByMigid(migId);
            try {
                //电子文件（文件夹，存储：有电子文件的条目id文件夹，条目id文件夹中则存储条目的电子文件）
                for(String entryId : migrateEntryList){
                    String thematicEle = thematicPath + File.separator + "电子文件" + File.separator + entryId.trim();
                    FileUtils.forceMkdir(new File(thematicEle));
                    List<Tb_electronic> electronics = electronicRepository.findByEntryid(entryId);
                    for(Tb_electronic tbElectronic : electronics){
                        String elePath = rootpath + tbElectronic.getFilepath() + File.separator + tbElectronic.getFilename();
                        if(FileUtil.isexists(elePath)){
                            FileUtils.copyFile(new File(elePath), new File(thematicEle + File.separator + tbElectronic.getFilename()));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // zip 完整路径
            String zippath =
                    rootpath + File.separator + "datarelease" + File.separator + "migrate" + File.separator + "已发布" + File.separator + migId.trim() + ".zip";
            ZipUtils.del(zippath);//删除之前的包
            String zpath = zippath.replaceAll("/", "\\\\");
            String srPath = path.replaceAll("/", "\\\\");
            ZipUtil.zip(srPath + "\\", zpath, true, "");
            ZipUtils.del(path);
            migrate.setMigratestate(Tb_archives_migrate.STATE_TRANSFER_COMPLETE);
            archivesMigrateRepository.save(migrate);
            migrateState=0;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer isMigrate(){
        return migrateState;
    }

    public Page<Tb_index_detail> findBySearchArchivesMigrateEntry(String migid,String type, int page, int limit, String condition,
                                                                  String operator, String content, Sort sort){
        String searchCondition = "";
        String migStr="";
        //选中或未选中的条目
        if(type==null||"".equals(type)){
            type="";
        }else {
            type="not";
        }
        //过滤条目
        if(migid!=null&&!"".equals(migid)){
            List<String> entryids=migrateEntryRepository.findByMigid(migid);
            migStr="and sid.entryid "+type+" in('" + String.join("','", entryids) + "')";
        }
        if (content != null) {// 输入框检索
            searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        String sortStr = "";//排序
        int sortInt = 0;//判断是否副表表排序
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            if ("eleid".equals(order.getProperty())) {
                sortStr = " order by " + DBCompatible.getInstance().getNullSort(order.getProperty()) + " " + order.getDirection();
            } else {
                sortStr = " order by " + order.getProperty() + " " + order.getDirection();
            }
            sortInt = entryIndexService.checkFilecode(order.getProperty());
        } else {
            sortStr = " order by archivecode desc, descriptiondate desc ";
        }
        String table = "v_index_detail";
        String countTable = "v_index_detail";
        if (condition == null || entryIndexService.checkFilecode(condition) == 0) {//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
            countTable = "tb_entry_index";
            if (sortInt == 0) {//非副表表字段排序
                table = "tb_entry_index";
            }
        }
        String sql = "select sid.entryid from " + table + " sid where 1=1 "+migStr + searchCondition  ;
        String countSql = "select count(nodeid) from " + countTable + " sid where 1=1 " +migStr+ searchCondition  ;
        Page<Tb_index_detail> result = entryIndexService.getPageListTwo(sql, sortStr, countSql, page, limit, pageRequest);
        return classifySearchController.convertNodefullnameAll(result, pageRequest);
    }

}
