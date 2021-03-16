package com.wisdom.web.controller;

import com.alibaba.excel.EasyExcel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wisdom.secondaryDataSource.entity.Tb_system_config_sx;
import com.wisdom.secondaryDataSource.repository.SxSystemConfigRepository;
import com.wisdom.util.KeyWordUtil;
import com.wisdom.util.LogAnnotation;
import com.wisdom.util.SxSystemConfigListener;
import com.wisdom.util.SystemConfigListener;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.SystemConfigRepository;
import com.wisdom.web.service.SystemConfigService;
import com.wisdom.web.service.TemplateService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.ws.commons.schema.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 系统配置控制器
 * Created by Rong on 2017/11/3.
 */
@Controller
@RequestMapping(value = "/systemconfig")
public class SystemConfigController {


    @Value("${system.document.rootpath}")
    private String rootpath;

    @Value("${find.sx.data}")
    private Boolean openSxData;//是否可检索声像系统的声像数据

    @Autowired
    SystemConfigRepository systemConfigRepository;

    @Autowired
    SystemConfigService systemConfigService;

    @Autowired
    TemplateService templateService;

    @Autowired
    SxSystemConfigRepository sxSystemConfigRepository;
    
    @RequestMapping("/main")
    public String main(Model model) {
        model.addAttribute("openSxData",openSxData);
        return "/inlet/systemConfig";
    }

    @RequestMapping("/exportXLS")
    @ResponseBody
    public ExtMsg exportXLS(String[] ids, HttpServletResponse httpServletResponse ) throws Exception{
        List<Tb_system_config> list=systemConfigRepository.findByConfigidIn(ids);
        String tempName = UUID.randomUUID().toString().replace("-", "");
        String suffer=".xls";
        File tempDir = new File(rootpath + "/importSystemConfig");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        File tempFile = File.createTempFile(tempName, suffer, tempDir);
        EasyExcel.write(tempFile.getAbsolutePath(),Tb_system_config.class).sheet("系统参数").doWrite(list);
        return new ExtMsg(true,"success",tempFile.getName());
    }

    @RequestMapping("/exportSXXLS")
    @ResponseBody
    public ExtMsg exportSXXLS(String[] ids) throws Exception{
        List<Tb_system_config_sx>  list =sxSystemConfigRepository.findByConfigidIn(ids);
        String tempName = UUID.randomUUID().toString().replace("-", "");
        String suffer=".xls";
        File tempDir = new File(rootpath + "/importSystemConfig");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        File tempFile = File.createTempFile(tempName, suffer, tempDir);
        EasyExcel.write(tempFile.getAbsolutePath(),Tb_system_config_sx.class).sheet("系统参数").doWrite(list);
        return new ExtMsg(true,"success",tempFile.getName());
    }

    @RequestMapping("/exportXLSX")
    @ResponseBody
    public ExtMsg exportXLSX(String[] ids) throws Exception{
        List<Tb_system_config> list=systemConfigRepository.findByConfigidIn(ids);
//        String prefix= rootpath.substring(fileName.lastIndexOf("."));
        String tempName = UUID.randomUUID().toString().replace("-", "");
        String suffer=".xlsx";
        File tempDir = new File(rootpath + "/importSystemConfig");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        File tempFile = File.createTempFile(tempName, suffer, tempDir);
        EasyExcel.write(tempFile.getAbsolutePath(),Tb_system_config.class).sheet("系统参数").doWrite(list);
        return new ExtMsg(true,"success",tempFile.getName());
//        try {
//            File html_file = new File(tempFile.getAbsolutePath());
//            response.setCharacterEncoding("UTF-8");
//            response.setHeader("Content-Disposition",
//                    "attachment; filename=\"" + tempFile.getName() + "\"");
//            response.setContentType("application/xlsx");
//            FileInputStream inputStream = new FileInputStream(html_file);
//            ServletOutputStream out = response.getOutputStream();
//            int b = 0;
//            byte[] buffer = new byte[1024];
//            while ((b = inputStream.read(buffer)) != -1) {
//                out.write(buffer, 0, b);
//            }
//            inputStream.close();
//            out.flush();
//            out.close();
//        } catch (Exception e) {
//        }
    }

    @RequestMapping("/exportSXXLSX")
    @ResponseBody
    public ExtMsg exportSXXLSX(String[] ids) throws Exception{
        List<Tb_system_config_sx>  list =sxSystemConfigRepository.findByConfigidIn(ids);
        String tempName = UUID.randomUUID().toString().replace("-", "");
        String suffer=".xlsx";
        File tempDir = new File(rootpath + "/importSystemConfig");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        File tempFile = File.createTempFile(tempName, suffer, tempDir);
        EasyExcel.write(tempFile.getAbsolutePath(),Tb_system_config_sx.class).sheet("系统参数").doWrite(list);
        return new ExtMsg(true,"success",tempFile.getName());
    }



    @RequestMapping("/importExcel")
    @ResponseBody
    public String importExcel(MultipartFile fileImport, String parentid,HttpServletResponse response) throws Exception{

        ObjectMapper json = new ObjectMapper();
        String fileName = fileImport.getOriginalFilename();
        if(StringUtils.isEmpty(fileName)){
            return json.writeValueAsString(new ExtMsg(false,"无文件上传","保存失败"));
        }
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        String tempName = UUID.randomUUID().toString().replace("-", "");
        File tempDir = new File(rootpath + "/importSystemConfig");
        if (!tempDir.exists()) {
           tempDir.mkdirs();
        }
        File tempFile = File.createTempFile(tempName, prefix, tempDir);
        fileImport.transferTo(tempFile);
        //读取excel 并保存数据
        EasyExcel.read(tempFile,Tb_system_config.class,new SystemConfigListener(systemConfigRepository,parentid)).sheet().doRead();
        response.setContentType("text/html");
        return json.writeValueAsString(new ExtMsg(true,"保存成功","保存成功"));
    }


    @RequestMapping("/importSXExcel")
    @ResponseBody
    public String importSXExcel(MultipartFile fileImport, String parentid,HttpServletResponse response) throws Exception{

        ObjectMapper json = new ObjectMapper();
        String fileName = fileImport.getOriginalFilename();
        if(StringUtils.isEmpty(fileName)){
            return json.writeValueAsString(new ExtMsg(false,"无文件上传","保存失败"));
        }
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        String tempName = UUID.randomUUID().toString().replace("-", "");
        File tempDir = new File(rootpath + "/importSystemConfig");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        File tempFile = File.createTempFile(tempName, prefix, tempDir);
        fileImport.transferTo(tempFile);
        //读取excel 并保存数据
        EasyExcel.read(tempFile,Tb_system_config_sx.class,new SxSystemConfigListener(sxSystemConfigRepository,parentid)).sheet().doRead();
        response.setContentType("text/html");

        return json.writeValueAsString(new ExtMsg(true,"保存成功","保存成功"));
    }

    @RequestMapping("/getExport")
    public void getExport(String fileName,HttpServletResponse response){
        File tempFile=new File(rootpath+"/importSystemConfig/"+fileName);
        if(tempFile.exists()){
            File html_file = new File(tempFile.getAbsolutePath());
            try {
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-Disposition",
                        "attachment; filename=\"" + tempFile.getName() + "\"");
                response.setContentType("application/xlsx");
                FileInputStream inputStream = new FileInputStream(html_file);
                ServletOutputStream out = response.getOutputStream();
                int b = 0;
                byte[] buffer = new byte[1024];
                while ((b = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, b);
                }
                inputStream.close();
                out.flush();
                out.close();
            } catch (Exception e) {
            }finally {
                html_file.delete();
            }
        }
    }



    @RequestMapping("/getByParentconfigid")
    @ResponseBody
    public List<ExtNcTree> getByParentconfigid(String parentconfigid, String xtType) {
        if("声像系统".equals(xtType)){
            return systemConfigService.findSxByParentconfigid(parentconfigid);
        }
        return systemConfigService.findByParentconfigid(parentconfigid);
    }

    @RequestMapping("/getByConfigcode")
    @ResponseBody
    public List<ExtNcTree> getByConfigcode(String configcode,String type){
        return systemConfigService.findByConfigcodes(configcode,type);
    }
    
    @RequestMapping("/getConfigValue")
    @ResponseBody
    public List<Tb_system_config> getConfigValue(String configCode) {
    	if (configCode != null && !"".equals(configCode)) {
    		String configid = systemConfigRepository.findByConfigcode(configCode.split("_")[1]);
            return systemConfigRepository.findConfigcodeByParentconfigid(configid);
    	}
    	return null;
    }

    @RequestMapping("/systemconfigs")
    @ResponseBody
    public Page<Tb_system_config> findSystemconfigDetailBySearch(String xtType, int page, int limit, String condition, String
            operator, String content, String configid, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        if("声像系统".equals(xtType)){
            return systemConfigService.findSxBySearch(page, limit, condition, operator, content, configid, sortobj);
        }
        return systemConfigService.findBySearch(page, limit, condition, operator, content, configid, sortobj);
    }

    @RequestMapping(value = "/testAdd/{configid}", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg testAdd(@PathVariable String configid,String xtType) {
        ExtMsg extMsg;
        String parentconfigid;
        if("声像档案".equals(xtType)){
            parentconfigid = systemConfigService.findSxByConfigid(configid) == null ? null : systemConfigService.findSxByConfigid(configid).getParentconfigid();

        }else{
            parentconfigid = systemConfigService.findByConfigid(configid) == null ? null : systemConfigService.findByConfigid(configid).getParentconfigid();
        }
        //如果不存在父ID，允许新增
        if (parentconfigid == null) {
            extMsg = new ExtMsg(true, "允许新增", null);
        } else {
            extMsg = new ExtMsg(false, "只允许三层结构，不能新增", null);
        }
        return extMsg;
    }

    @LogAnnotation(module="系统设置-参数设置",sites = "1",fields = "configcode",connect = "##名称",startDesc = "操作参数，条目详细：")
    @RequestMapping("/addSystemConfig")
    @ResponseBody
    public ExtMsg addSystemConfig(@ModelAttribute("form") Tb_system_config systemConfig,String xtType) {
        ExtMsg extMsg;
        String configid = systemConfig.getConfigid();
        if("声像系统".equals(xtType)){
            return addSxSystemConfig(systemConfig);
        }else{
            if (!"".equals(configid)) {//修改
                String parentconfigid = systemConfig.getParentconfigid();
                //判断是否有父id
                if (!"".equals(parentconfigid)) {//需要保证在父节点下参数名称、参数值都是唯一
                    if(systemConfig.getCode().equals(systemConfigService.findByConfigid(configid).getCode())){//判断参数名称是否被修改
                        //判断参数值是否被修改
                        if(systemConfig.getValue().equals(systemConfigService.findByConfigid(configid).getValue())){
                            extMsg = new ExtMsg(true, "修改成功", systemConfig);
                        }else{
                            if(systemConfigService.findByParentconfigidAndConfigvalue(systemConfig.getParentconfigid(),
                                    systemConfig.getValue())!=null) {//判断父节点下修改后的参数值是否存在
                                extMsg = new ExtMsg(false, "修改失败,该参数类型下已存在'" + systemConfig.getValue() + "'相同的参数值",
                                        null);
                            }else{
                                Tb_system_config tb_system_config = systemConfigService.saveSystemConfig(systemConfig);
                                extMsg = new ExtMsg(true, "修改成功", tb_system_config);
                            }
                        }
                    }else{
                        if(systemConfigService.findByParentconfigidAndConfigcode(systemConfig.getParentconfigid(),
                                systemConfig.getCode())!=null) {//判断父节点下修改后的参数名称是否存在
                            extMsg = new ExtMsg(false, "修改失败,该参数类型下已存在'" + systemConfig.getCode() + "'相同的参数名称",
                                    null);
                        }else{
                            //判断参数值是否被修改
                            if(systemConfig.getValue().equals(systemConfigService.findByConfigid(systemConfig.getConfigid
                                    ()).getValue())){
                                Tb_system_config tb_system_config = systemConfigService.saveSystemConfig(systemConfig);
                                extMsg = new ExtMsg(true, "修改成功", tb_system_config);
                            }else{
                                if(systemConfigService.findByParentconfigidAndConfigvalue(systemConfig.getParentconfigid(),
                                        systemConfig.getValue())!=null) {//判断父节点下修改后的参数值是否存在
                                    extMsg = new ExtMsg(false, "修改失败,该参数类型下已存在'" + systemConfig.getValue() + "'相同的参数值",
                                            null);
                                }else{
                                    Tb_system_config tb_system_config = systemConfigService.saveSystemConfig(systemConfig);
                                    extMsg = new ExtMsg(true, "修改成功", tb_system_config);
                                }
                            }
                        }
                    }
                } else {//需要保证参数名称、参数值都是唯一
                    if (systemConfig.getCode().equals(systemConfigService.findByConfigid(configid).getCode())) {//判断参数名称是否被修改
                        //判断参数值是否被修改
                        if (systemConfig.getValue().equals(systemConfigService.findByConfigid(configid).getValue())) {
                            extMsg = new ExtMsg(true, "修改成功", systemConfig);
                        } else {
                            if (systemConfigService.findByConfigvalue(systemConfig.getValue()) != null) {//判断修改后的参数值是否存在
                                extMsg = new ExtMsg(false, "修改失败,该参数类型下已存在'" + systemConfig.getValue() + "'相同的参数值",
                                        null);
                            } else {
                                systemConfig.setParentconfigid(null);
                                Tb_system_config tb_system_config = systemConfigService.saveSystemConfig(systemConfig);
                                extMsg = new ExtMsg(true, "修改成功", tb_system_config);
                            }
                        }
                    } else {
                        if(systemConfigService.findByConfigcode(systemConfig.getCode())!=null){//判断修改后的参数名称是否存在
                            extMsg = new ExtMsg(false, "修改失败,该参数类型下已存在'" + systemConfig.getCode() + "'相同的参数名称",
                                    null);
                        }else{
                            systemConfig.setParentconfigid(null);
                            //判断参数值是否被修改
                            if (systemConfig.getValue().equals(systemConfigService.findByConfigid(configid).getValue())) {
                                Tb_system_config tb_system_config = systemConfigService.saveSystemConfig(systemConfig);
                                extMsg = new ExtMsg(true, "修改成功", tb_system_config);
                            } else {
                                if (systemConfigService.findByConfigvalue(systemConfig.getValue()) != null) {//判断修改后的参数值是否存在
                                    extMsg = new ExtMsg(false, "修改失败,该参数类型下已存在'" + systemConfig.getValue() + "'相同的参数值",
                                            null);
                                } else {
                                    Tb_system_config tb_system_config = systemConfigService.saveSystemConfig(systemConfig);
                                    extMsg = new ExtMsg(true, "修改成功", tb_system_config);
                                }
                            }
                        }
                    }
                }
            } else {//新增
                Integer maxSequence;
                if (systemConfig.getParentconfigid() == null || "".equals(systemConfig.getParentconfigid().trim())) {
                    maxSequence = systemConfigRepository.findMaxSequenceByParentidOrNull(systemConfig.getParentconfigid());
                } else {
                    maxSequence = systemConfigRepository.findMaxSequenceByParentid(systemConfig.getParentconfigid());
                }
                systemConfig.setSequence(maxSequence == null ? 0 : maxSequence + 1);//设置顺序
                //需要保证参数名称、参数值都是唯一
                if("".equals(systemConfig.getParentconfigid())){
                    if(systemConfigService.findByConfigcode(systemConfig.getCode()) != null){//判断参数名称是否存在
                        extMsg = new ExtMsg(false, "新增失败,该参数类型下已存在'" + systemConfig.getCode() + "'相同的参数名称", null);
                    }else if (systemConfigService.findByConfigvalue(systemConfig.getValue()) != null) {//判断参数值是否存在
                        extMsg = new ExtMsg(false, "新增失败,该参数类型下已存在'" + systemConfig.getValue() + "'相同的参数值", null);
                    } else {
                        systemConfig.setParentconfigid(null);
                        Tb_system_config tb_system_config = systemConfigService.saveSystemConfig(systemConfig);
                        extMsg = new ExtMsg(true, "新增成功", tb_system_config);
                    }
                }else{
                    //需要保证在父节点下参数名称、参数值都是唯一
                    if(systemConfigService.findByParentconfigidAndConfigcode(systemConfig.getParentconfigid(),
                            systemConfig.getCode())!=null){//判断父节点下参数名称是否存在
                        extMsg = new ExtMsg(false, "新增失败,该参数类型下已存在'" + systemConfig.getCode() + "'相同的参数名称", null);
                    }else if (systemConfigService.findByParentconfigidAndConfigvalue(systemConfig.getParentconfigid(),
                            systemConfig.getValue())!=null){//判断父节点下参数值是否存在
                        extMsg = new ExtMsg(false, "新增失败,该参数类型下已存在'" + systemConfig.getValue() + "'相同的参数值", null);
                    }else {
                        Tb_system_config tb_system_config = systemConfigService.saveSystemConfig(systemConfig);
                        extMsg = new ExtMsg(true, "新增成功", tb_system_config);
                    }
                }
            }
            return extMsg;
        }
    }

    public ExtMsg addSxSystemConfig(Tb_system_config systemConfig){
        ExtMsg extMsg;
        String configid = systemConfig.getConfigid();
        if (!"".equals(configid)) {//修改
            String parentconfigid = systemConfig.getParentconfigid();
            //判断是否有父id
            if (!"".equals(parentconfigid)) {//需要保证在父节点下参数名称、参数值都是唯一
                if(systemConfig.getCode().equals(systemConfigService.findSxByConfigid(configid).getCode())){//判断参数名称是否被修改
                    //判断参数值是否被修改
                    if(systemConfig.getValue().equals(systemConfigService.findSxByConfigid(configid).getValue())){
                        extMsg = new ExtMsg(true, "修改成功", systemConfig);
                    }else{
                        if(systemConfigService.findSxByParentconfigidAndConfigvalue(systemConfig.getParentconfigid(),
                                systemConfig.getValue())!=null) {//判断父节点下修改后的参数值是否存在
                            extMsg = new ExtMsg(false, "修改失败,该参数类型下已存在'" + systemConfig.getValue() + "'相同的参数值",
                                    null);
                        }else{
                            Tb_system_config_sx tb_system_config = systemConfigService.saveSxSystemConfig(systemConfig);
                            extMsg = new ExtMsg(true, "修改成功", tb_system_config);
                        }
                    }
                }else{
                    if(systemConfigService.findSxByParentconfigidAndConfigcode(systemConfig.getParentconfigid(),
                            systemConfig.getCode())!=null) {//判断父节点下修改后的参数名称是否存在
                        extMsg = new ExtMsg(false, "修改失败,该参数类型下已存在'" + systemConfig.getCode() + "'相同的参数名称",
                                null);
                    }else{
                        //判断参数值是否被修改
                        if(systemConfig.getValue().equals(systemConfigService.findSxByConfigid(systemConfig.getConfigid
                                ()).getValue())){
                            Tb_system_config_sx tb_system_config = systemConfigService.saveSxSystemConfig(systemConfig);
                            extMsg = new ExtMsg(true, "修改成功", tb_system_config);
                        }else{
                            if(systemConfigService.findSxByParentconfigidAndConfigvalue(systemConfig.getParentconfigid(),
                                    systemConfig.getValue())!=null) {//判断父节点下修改后的参数值是否存在
                                extMsg = new ExtMsg(false, "修改失败,该参数类型下已存在'" + systemConfig.getValue() + "'相同的参数值",
                                        null);
                            }else{
                                Tb_system_config_sx tb_system_config = systemConfigService.saveSxSystemConfig(systemConfig);
                                extMsg = new ExtMsg(true, "修改成功", tb_system_config);
                            }
                        }
                    }
                }
            } else {//需要保证参数名称、参数值都是唯一
                if (systemConfig.getCode().equals(systemConfigService.findSxByConfigid(configid).getCode())) {//判断参数名称是否被修改
                    //判断参数值是否被修改
                    if (systemConfig.getValue().equals(systemConfigService.findSxByConfigid(configid).getValue())) {
                        extMsg = new ExtMsg(true, "修改成功", systemConfig);
                    } else {
                        if (systemConfigService.findSxByConfigvalue(systemConfig.getValue()) != null) {//判断修改后的参数值是否存在
                            extMsg = new ExtMsg(false, "修改失败,该参数类型下已存在'" + systemConfig.getValue() + "'相同的参数值",
                                    null);
                        } else {
                            systemConfig.setParentconfigid(null);
                            Tb_system_config_sx tb_system_config = systemConfigService.saveSxSystemConfig(systemConfig);
                            extMsg = new ExtMsg(true, "修改成功", tb_system_config);
                        }
                    }
                } else {
                    if(systemConfigService.findSxByConfigcode(systemConfig.getCode())!=null){//判断修改后的参数名称是否存在
                        extMsg = new ExtMsg(false, "修改失败,该参数类型下已存在'" + systemConfig.getCode() + "'相同的参数名称",
                                null);
                    }else{
                        systemConfig.setParentconfigid(null);
                        //判断参数值是否被修改
                        if (systemConfig.getValue().equals(systemConfigService.findSxByConfigid(configid).getValue())) {
                            Tb_system_config_sx tb_system_config = systemConfigService.saveSxSystemConfig(systemConfig);
                            extMsg = new ExtMsg(true, "修改成功", tb_system_config);
                        } else {
                            if (systemConfigService.findSxByConfigvalue(systemConfig.getValue()) != null) {//判断修改后的参数值是否存在
                                extMsg = new ExtMsg(false, "修改失败,该参数类型下已存在'" + systemConfig.getValue() + "'相同的参数值",
                                        null);
                            } else {
                                Tb_system_config_sx tb_system_config = systemConfigService.saveSxSystemConfig(systemConfig);
                                extMsg = new ExtMsg(true, "修改成功", tb_system_config);
                            }
                        }
                    }
                }
            }
        } else {//新增
            Integer maxSequence;
            if (systemConfig.getParentconfigid() == null || "".equals(systemConfig.getParentconfigid().trim())) {
                maxSequence = sxSystemConfigRepository.findMaxSequenceByParentidOrNull(systemConfig.getParentconfigid());
            } else {
                maxSequence = sxSystemConfigRepository.findMaxSequenceByParentid(systemConfig.getParentconfigid());
            }
            systemConfig.setSequence(maxSequence == null ? 0 : maxSequence + 1);//设置顺序
            //需要保证参数名称、参数值都是唯一
            if("".equals(systemConfig.getParentconfigid())){
                if(systemConfigService.findSxByConfigcode(systemConfig.getCode()) != null){//判断参数名称是否存在
                    extMsg = new ExtMsg(false, "新增失败,该参数类型下已存在'" + systemConfig.getCode() + "'相同的参数名称", null);
                }else if (systemConfigService.findSxByConfigvalue(systemConfig.getValue()) != null) {//判断参数值是否存在
                    extMsg = new ExtMsg(false, "新增失败,该参数类型下已存在'" + systemConfig.getValue() + "'相同的参数值", null);
                } else {
                    systemConfig.setParentconfigid(null);
                    Tb_system_config_sx tb_system_config = systemConfigService.saveSxSystemConfig(systemConfig);
                    extMsg = new ExtMsg(true, "新增成功", tb_system_config);
                }
            }else{
                //需要保证在父节点下参数名称、参数值都是唯一
                if(systemConfigService.findSxByParentconfigidAndConfigcode(systemConfig.getParentconfigid(),
                        systemConfig.getCode())!=null){//判断父节点下参数名称是否存在
                    extMsg = new ExtMsg(false, "新增失败,该参数类型下已存在'" + systemConfig.getCode() + "'相同的参数名称", null);
                }else if (systemConfigService.findSxByParentconfigidAndConfigvalue(systemConfig.getParentconfigid(),
                        systemConfig.getValue())!=null){//判断父节点下参数值是否存在
                    extMsg = new ExtMsg(false, "新增失败,该参数类型下已存在'" + systemConfig.getValue() + "'相同的参数值", null);
                }else {
                    Tb_system_config_sx tb_system_config = systemConfigService.saveSxSystemConfig(systemConfig);
                    extMsg = new ExtMsg(true, "新增成功", tb_system_config);
                }
            }
        }
        return extMsg;
    }

    @LogAnnotation(module="系统设置-参数设置",sites = "1",startDesc = "删除参数，条目编号：")
    @RequestMapping(value = "/systemconfigs/{configids}/{xtType}", method = RequestMethod.DELETE)
    @ResponseBody
    public ExtMsg deleteConfig(@PathVariable String configids,@PathVariable String xtType) {
        if ("声像系统".equals(xtType)) {
            return deleteSxConfig(configids);
        } else {
            ExtMsg extMsg;
            String successid = "";
            String[] configid = configids.split(",");
            String parentconfigid = systemConfigService.findByConfigid(configid[0]) == null ? null : systemConfigService
                    .findByConfigid(configid[0]).getParentconfigid();
            //判断是否存在父ID,若存在，判断父级是否被Tb_data_template使用
            if (parentconfigid != null) {
//            if (templateService.findByFenums(systemConfigService.findByConfigid(parentconfigid).getValue()).size() > 0) {
//                extMsg = new ExtMsg(false, "删除失败,该参数值的父级已在‘模版维护’中使用", null);
//            } else {
                if (systemConfigService.deleteByConfigidIn(configid) > 0) {
                    successid += configids;
                    extMsg = new ExtMsg(true, "删除成功", successid);
                } else {
                    extMsg = new ExtMsg(false, "删除失败", null);
                }
//            }
            } else {
                String[] msgs = new String[configid.length];
                for (int i = 0; i < configid.length; i++) {
                    String code = systemConfigService.findByConfigid(configid[i]).getCode();
//                if (templateService.findByFenums(systemConfigService.findByConfigid(configid[i]).getValue().getValue()).size() >
//                        0) {
//                    msgs[i] = "'"+code+"'" + "删除失败,该参数值已在‘模版维护’中使用";
//                } else {
                    if (systemConfigService.deleteByConfigid(configid[i]) > 0) {
                        msgs[i] = "'" + code + "'删除成功";
                        successid += (configid[i] + ",");
                    } else {
                        msgs[i] = "'" + code + "'删除失败";
                    }
//                }
                }
                String msg = "";
                for (String m : msgs) {
                    if (m != null) {
                        msg += m + ";<br />";
                    }
                }
                extMsg = new ExtMsg(true, msg, successid);
            }
            return extMsg;
        }
    }

    public ExtMsg deleteSxConfig(String configids) {
        ExtMsg extMsg;
        String successid="";
        String[] configid = configids.split(",");
        String parentconfigid = systemConfigService.findSxByConfigid(configid[0]) == null ?null:systemConfigService
                .findSxByConfigid(configid[0]).getParentconfigid();
        //判断是否存在父ID,若存在，判断父级是否被Tb_data_template使用
        if (parentconfigid != null) {
//            if (templateService.findByFenums(systemConfigService.findByConfigid(parentconfigid).getValue()).size() > 0) {
//                extMsg = new ExtMsg(false, "删除失败,该参数值的父级已在‘模版维护’中使用", null);
//            } else {
            if (systemConfigService.deleteSxByConfigidIn(configid) > 0) {
                successid += configids;
                extMsg = new ExtMsg(true, "删除成功", successid);
            } else {
                extMsg = new ExtMsg(false, "删除失败", null);
            }
//            }
        } else {
            String[] msgs = new String[configid.length];
            for(int i =0;i<configid.length;i++){
                String code=systemConfigService.findSxByConfigid(configid[i]).getCode();
//                if (templateService.findByFenums(systemConfigService.findByConfigid(configid[i]).getValue()).size() >
//                        0) {
//                    msgs[i] = "'"+code+"'" + "删除失败,该参数值已在‘模版维护’中使用";
//                } else {
                if (systemConfigService.deleteSxByConfigid(configid[i]) > 0) {
                    msgs[i] ="'" +code+"'删除成功";
                    successid +=(configid[i]+",");
                } else {
                    msgs[i] ="'" +code+"'删除失败";
                }
//                }
            }
            String msg = "";
            for (String m : msgs) {
                if (m != null) {
                    msg += m +";<br />";
                }
            }
            extMsg = new ExtMsg(true, msg, successid);
        }
        return extMsg;
    }

	@RequestMapping("/enums")
	@ResponseBody
	public List findbyparentvalue(String value,String xtType) {
        if("声像系统".equals(xtType)){
            return systemConfigService.findByParentSxValue(value);
        }
		return systemConfigService.findbyparentvalue(value);
	}

    @RequestMapping("/enumsName/findByNodeid")
    @ResponseBody
    public List<ExtSearchData> findcodebynodeid(String nodeid) {
        String value = templateService.findFEnumsByNodeid(nodeid);
        return systemConfigService.findnamebyparentvalue(value);
    }

    @RequestMapping("/enumsName/findByParentvalue")
    @ResponseBody
    public List<ExtSearchData> findcodebyparentvalue(String value) {
        return systemConfigService.findnamebyparentvalue(value);
    }

    @RequestMapping("/configs")
    @ResponseBody
    public List<Tb_system_config> findsystemconfigs() {
        return systemConfigRepository.findByParentconfigidIsNullOrderBySortsequence();
    }
    
    @RequestMapping("/findConfigValue")
    @ResponseBody
    public ExtMsg findByValue(String value) {
    	String parentconfigid = systemConfigRepository.findByConfigcode("主题词");
    	List<String> config = systemConfigService.findConfigcode(parentconfigid);// 获取主题词的所有关键字
        KeyWordUtil keyWordUtil = new KeyWordUtil(config);
        Set<String> words = keyWordUtil.getWords(value);
    	if (words != null) {
    		return new ExtMsg(true, "success", words);
    	}
        return new ExtMsg(true, "failed", "非主题词");
    }

    @RequestMapping("/findConfigByConfigcode")
    @ResponseBody
    public List<Tb_system_config> findConfigByConfigcode(String configcode) {
        return systemConfigService.findConfigByConfigcode(configcode);
    }

    //通过拖拉方式进行排序
    @RequestMapping("/orderConfig")
    @ResponseBody
    public void orderConfig(String configid,int order,String xtType) {
        if("声像系统".equals(xtType)){
            Tb_system_config_sx config = sxSystemConfigRepository.findByConfigid(configid);
            systemConfigService.orderSxConfig(config.getParentconfigid(),order);
            config.setSequence(order);
            systemConfigService.saveSxSystemConfigTwo(config);
        }else{
            Tb_system_config config = systemConfigRepository.findByConfigid(configid);
            config.setSequence(order);
            systemConfigService.orderConfig(config.getParentconfigid(),order);
        }
    }
}