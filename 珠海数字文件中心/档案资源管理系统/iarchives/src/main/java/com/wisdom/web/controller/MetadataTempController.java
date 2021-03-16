package com.wisdom.web.controller;


import com.wisdom.util.CreateExcel;
import com.wisdom.util.GainField;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.FieldGroupRepository;
import com.wisdom.web.repository.MetadataTempRepository;
import com.wisdom.web.service.ExportExcelService;
import com.wisdom.web.service.MetadataTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 元数据模板控制器
 */
@Controller
@RequestMapping(value = "/metadataTemplate")
public class MetadataTempController {

    @Autowired
    MetadataTempService metadataTempService;

    @Autowired
    ExportExcelService exportExcelService;

    @Autowired
    FieldGroupRepository fieldGroupRepository;

    @Autowired
    MetadataTempRepository metadataTempRepository;

    @Value("${system.document.rootpath}")
    private String rooPath;

    @RequestMapping("/main")
    public String metadataTemp(){
        return "/inlet/metadataTemplate";
    }

    @RequestMapping("/getAllField")
    @ResponseBody
    public List getAllField(String nodeid){
        return metadataTempService.getAllByField(nodeid);
    }

    @RequestMapping("/templates")
    @ResponseBody
    public Page<Tb_metadata_temp> findTemplateDetailBySearch(int page, int limit, String condition, String operator,
                                                             String content, String nodeid, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        return metadataTempService.findBySearch(page, limit, condition, operator, content, nodeid, sortobj);
    }

    @RequestMapping("/getClassifyById")
    @ResponseBody
    public List<NodesettingTree> getClassifyById(String pcid, String type, String dataType){
//        List<NodesettingTree> nodeTreeList = new ArrayList<>();
//
//        NodesettingTree nodesettingTree = new NodesettingTree();
//        nodesettingTree.setFnid("c4b24c1a875f11e9a98a0242ac110004");
//        nodesettingTree.setText("文书类");
//        nodesettingTree.setCls("file");
//        nodesettingTree.setLeaf(true);
//        nodesettingTree.setExpanded(false);
//        nodesettingTree.setRoottype("nuit");
//        nodesettingTree.setClasslevel(8);
//        nodesettingTree.setNodeType(1);
//        nodesettingTree.setSortsequence(1);
//        nodeTreeList.add(nodesettingTree);
        return metadataTempService.getMetadataClassify("元数据模板分类");
    }

    @RequestMapping("/submitfields")
    @ResponseBody
    public ExtMsg submitfields(String nodeid, String[] fieldnames) {
        metadataTempService.submitfields(nodeid, fieldnames);
        return new ExtMsg(true, "成功", null);
    }


    @RequestMapping("/getSelectedByNodeid")
    @ResponseBody
    public ExtMsg getSelectedByNodeid(String nodeid) {
        List<Tb_metadata_temp> list = metadataTempService.findByNodeidOrderByFsequence(nodeid);
        return new ExtMsg(true, "操作成功", GainField.getFieldValues(list, "fieldcode"));
    }

    @RequestMapping("/UpdateTemplate")
    @ResponseBody
    public ExtMsg UpdateTemplate(Tb_metadata_temp metadata_temp) {
        metadataTempService.UpdateTemplate(metadata_temp);
        return new ExtMsg(true, "更新成功", null);
    }

    @RequestMapping("/form")
    @ResponseBody
    public List formtemplate(String nodeid,String module) {
        List<Tb_metadata_temp> list = metadataTempService.findByNodeidOrderByFsequence(nodeid);
        if(module!=null){
            if(module.equals("capture")){
                for (Tb_metadata_temp template : list) {
                    if(template.getFieldcode().equals("archivecode")){
                        list.remove(template);
                        break;
                    }
                }
            }
        }
        return list;
    }

    @RequestMapping("/grid")
    @ResponseBody
    public List gridtemplate(String nodeid, String type, String info, String eventid, String entryid) {
        List<Tb_metadata_temp> list = new ArrayList<>();
        list = metadataTempService.gridTemplate(nodeid, type, info);
        return list;
    }

    @RequestMapping("/copyTemplate")
    @ResponseBody
    public ExtMsg copyTemplate(String sourceid, String targetid, String withCode) {
        return metadataTempService.copyTemplate(sourceid, targetid, withCode);
    }

    @RequestMapping("/exportFieldModel")
    @ResponseBody
    public ExtMsg exportFieldModel(String nodeid) {
        //1.根据节点查询出当前节点的字段模板
        List<Tb_metadata_temp> templates = metadataTempService.findByNodeidOrderByFsequence(nodeid);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String nodename = sdf.format(new Date());
        //2查询出档号组成设置
        List<Tb_codeset> codesetlists = new ArrayList<>();
        String[] codeserFields = metadataTempService.getFiledName(new Tb_codeset());
        //3.调用生成excel方法
        String[] fields = metadataTempService.getFiledName(new Tb_metadata_temp());
        CreateExcel.createFieldModel(templates, nodename, fields);
        //4.执行文件导出
        Map<String, String> map = new HashMap();
        map.put("nodename", nodename);
        return new ExtMsg(true, "导出成功", map);
    }

    @RequestMapping("/importFieldModel")
    @ResponseBody
    public ExtMsg importFieldModel(HttpServletResponse response, HttpServletRequest request, String filename, String NodeIdf){
        //1.文件使用组件上传 存放目录 document/OAfile/upload
        String path = rooPath + File.separator + "OAFile" + File.separator + "upload" + File.separator + filename;
        Map<Integer, Integer> map = new HashMap();
        map = metadataTempService.importFieldModel(path, NodeIdf);
        return new ExtMsg(true, "导入完成", map);
    }

    @RequestMapping("/deleteTemplateByNodeid")
    @ResponseBody
    public ExtMsg deleteTemplateByNodeid(String nodeid) {
        metadataTempService.deleteTemplateByNodeid(nodeid);
        return new ExtMsg(true, "模板删除成功", null);
    }

    @RequestMapping("/export")
    @ResponseBody
    public void export(HttpServletResponse response, HttpServletRequest request, String nodeid) {
        String excelPath = metadataTempService.export(nodeid);
        try {
            File zipFile = new File(excelPath + ".zip");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + getOutName(request, zipFile.getName()) + "\"");
            response.setContentType("application/zip");
            FileInputStream inputStream = new FileInputStream(zipFile);
            ServletOutputStream out = response.getOutputStream();
            int b;
            byte[] buffer = new byte[1024];
            while ((b = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, b);
            }
            inputStream.close();
            out.flush();
            out.close();
            if (zipFile.exists()) {// 输出后删除zip
                zipFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getOutName(HttpServletRequest request, String name) throws Exception {
        String agent = request.getHeader("User-Agent");
        if (agent != null && (agent.contains("Firefox") || agent.contains("Safari") || agent.contains("Chrome"))) {
            name = new String((name).getBytes(), "ISO8859-1");
        } else {
            name = URLEncoder.encode(name, "UTF8"); // 其他浏览器
        }
        return name;
    }

    //字段分组
    @RequestMapping("/getGroupField")
    @ResponseBody
    public Page<Tb_field_group> getGroupField(int page, int limit, String condition, String operator,
                                              String content, String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        PageRequest pageRequest = new PageRequest(page - 1, limit, sortobj == null ? new Sort("groupname") : sortobj);
        return fieldGroupRepository.findAll(pageRequest);
    }

    @RequestMapping("/addGroupField")
    @ResponseBody
    public void addGroupField(@ModelAttribute("form") Tb_field_group field_group){
        fieldGroupRepository.save(field_group);
    }

    @RequestMapping("/delGroupField")
    @ResponseBody
    public void delGroupField(String configid){
        metadataTempService.delGroupField(configid);
    }

    @RequestMapping("/groupField")
    @ResponseBody
    public List<Tb_field_group> groupField(){
        return fieldGroupRepository.findAllGroup();
    }

    @RequestMapping("/findByClassify")
    @ResponseBody
    public List<Tb_metadata_temp> findByClassify(String metadataType){
        return metadataTempService.findByClassify(metadataType);
    }

    @RequestMapping(value = "/getByTemplateid")
    @ResponseBody
    public Tb_metadata_temp getByTemplateid(String templateId){
        Tb_metadata_temp temp = metadataTempRepository.findOne(templateId);
        return temp;
    }

}
