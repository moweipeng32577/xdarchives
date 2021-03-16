package com.wisdom.web.controller;

import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_archives_migrate;
import com.wisdom.web.entity.Tb_index_detail;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.service.ArchivesMigrateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * 档案迁移控制器
 * Created by Leo on 2020/8/12 0012.
 */
@Controller
@RequestMapping(value = "/archivesMigrate")
public class ArchivesMigrateController {

    @Autowired
    ArchivesMigrateService archivesMigrateService;

    @Autowired
    ThematicProdController thematicProdController;

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录

    @RequestMapping("/main")
    public String main(Model model,String isp) {
        return "/inlet/archivesMigrate";
    }

    @RequestMapping("/getArchivesMigrateBySearch")
    @ResponseBody
    public Page<Tb_archives_migrate> getArchivesMigrateBySearch( int page, int limit, String condition,
                                                                 String operator, String content, String sort){

        return archivesMigrateService.getArchivesMigrateBySearch(page,limit,condition,operator,content,sort);
    }

    @RequestMapping("/getArchivesMigrate")
    @ResponseBody
    public ExtMsg getArchivesMigrate(){
        return null;
    }

    /**
     * 保存迁移登记
     * @param archives_migrate
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public ExtMsg save(Tb_archives_migrate archives_migrate){
        if(archivesMigrateService.save(archives_migrate)!=null){
            return new ExtMsg(true,"迁移登记成功","");
        }
        return new ExtMsg(false,"迁移登记失败","");
    }

    /**
     * 查看迁移登记单据的条目
     * @param migid 单据id
     * @return
     */
    @RequestMapping("/findBySearchArchivesMigrateEntry")
    @ResponseBody
    public Page<Tb_index_detail> findBySearchArchivesMigrateEntry(String migid,String type, int page, int limit, String condition,
                                                                  String operator, String content, String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        return archivesMigrateService.findBySearchArchivesMigrateEntry(migid,type,page,limit,condition,operator,content,sortobj);
    }

    /**
     * 迁移打包
     * @param migid 单据id
     * @return
     */
    @RequestMapping("/migratePack")
    @ResponseBody
    public Integer migratePack(String migid,String response){
        return archivesMigrateService.migratePack(migid);
    }

    /**
     * 判断后台是否在打包
     * @return
     */
    @RequestMapping("/isMigrate")
    @ResponseBody
    public Integer isMigrate(){
        return archivesMigrateService.isMigrate();
    }

    @RequestMapping("/downLoadMigratePack")
    public void downLoadMigratePack(String migid, HttpServletResponse response){
        String zipPath=rootpath + File.separator + "datarelease" + File.separator + "migrate" + File.separator + "已发布" + File.separator + migid.trim() + ".zip";
        thematicProdController.downLoad(response,zipPath);
    }


    /**
     * 迁移登记单据添加条目
     * @param migid 单据id
     * @param entryids 条目id
     * @return
     */
    @RequestMapping("/saveArchivesMigrateEntry")
    @ResponseBody
    public ExtMsg saveArchivesMigrateEntry(String migid,String entryids){
        String[] ids=entryids.split(",");
        if(archivesMigrateService.saveArchivesMigrateEntry(migid,ids)!=null){
            return new ExtMsg(true,"迁移数据成功","");
        }
        return new ExtMsg(false,"迁移数据失败","");
    }

    /**
     * 迁移登记单据添加条目
     * @param migid 单据id
     * @param entryids 条目id
     * @return
     */
    @RequestMapping("/deletArchivesMigrateEntry")
    @ResponseBody
    public ExtMsg deletArchivesMigrateEntry(String migid,String entryids){
        String[] ids=entryids.split(",");
        if(archivesMigrateService.deletArchivesMigrateEntry(migid,ids)>0){
            return new ExtMsg(true,"删除条目成功","");
        }
        return new ExtMsg(false,"删除条目失败","");
    }
}
