package com.wisdom.web.service;


import com.wisdom.util.*;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.DataNodeRepository;
import com.wisdom.web.repository.FieldGroupRepository;
import com.wisdom.web.repository.MetadataTempRepository;
import com.wisdom.web.repository.SystemConfigRepository;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.wisdom.web.service.ThematicService.delFolder;

@Service
@Transactional
public class MetadataTempService {

    @Autowired
    MetadataTempRepository metadataTempRepository;

    @Autowired
    SystemConfigRepository systemConfigRepository;

    @Autowired
    FieldGroupRepository fieldGroupRepository;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Value("${system.document.rootpath}")
    private String rooPath;

    public List getAllByField(String nodeid) {
        // 分成两块：一块常用字段，一块附加字段；
        String[] appendFieldNameArray = new String[100];
        for (int i = 1; i <= appendFieldNameArray.length; i++) {
            if (i < 10) {
                appendFieldNameArray[i - 1] = "m0" + i + "_字段描述";
            } else {
                appendFieldNameArray[i - 1] = "m" + i + "_字段描述";
            }
        }
        List<HashMap> list = new ArrayList<>();
        list = filterSelected(appendFieldNameArray, new ArrayList<>(), new ArrayList<>());
        return list;
    }

    public List<HashMap> filterSelected(String[] nameArray, List<Tb_data_template> fieldlist, List<HashMap> list) {
        String fieldcode = "";
        for (String fieldName : nameArray) {
            HashMap map = new HashMap();
            fieldcode = fieldName.substring(0, fieldName.indexOf("_"));
            map.put("fieldcode", fieldcode);
            boolean flag = false;
            for (Tb_data_template data_template : fieldlist) {// 遍历查找，判断是否为已选字段
                if (data_template.getFieldcode().equals(fieldcode)) {
                    map.put("fieldname", fieldcode + "_" + data_template.getFieldname());// 是则使用已选字段名
                    flag = true;// 找到
                    break;
                }
            }
            if (!flag) {// 没找到
                map.put("fieldname", fieldName);// 否则使用初始字段名
            }
            list.add(map);
        }
        return list;
    }

    public Page<Tb_metadata_temp> findBySearch(int page, int limit, String condition, String operator, String content,
                                               String classify, Sort sort) {
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort("fsequence") : sort);
        Specification<Tb_metadata_temp> searchid = getSearchNodeidCondition(classify);
        Specifications specifications = Specifications.where(searchid);
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        return metadataTempRepository.findAll(specifications, pageRequest);
    }

    public static Specification<Tb_metadata_temp> getSearchNodeidCondition(String classifyid) {
        Specification<Tb_metadata_temp> searchNodeidCondition = new Specification<Tb_metadata_temp>() {
            @Override
            public Predicate toPredicate(Root<Tb_metadata_temp> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get("classify"), classifyid);
                return criteriaBuilder.or(p);
            }
        };
        return searchNodeidCondition;
    }

    //获取参数设置 中设置的参数
    public List<NodesettingTree> getMetadataClassify(String configcode) {
        List<NodesettingTree> trees = new ArrayList<>();
        if (null != configcode && !"".equals(configcode)) {
            List<Tb_system_config> configList = getSystemConfig(configcode);
            for (Tb_system_config config : configList) {
                NodesettingTree tree = new NodesettingTree();
                tree.setFnid(config.getValue());
                tree.setText(config.getCode());
                tree.setCls("file");
                tree.setLeaf(true);
                tree.setExpanded(false);
                tree.setRoottype("nuit");
                tree.setClasslevel(8);
                tree.setNodeType(1);
                tree.setSortsequence(1);
                trees.add(tree);
            }
        }
        return trees;
    }

    //获取元数据模板分类----占定 从参数设置中拿取
    public List<Tb_system_config> getSystemConfig(String configcode) {
        String configid = systemConfigRepository.findByConfigcode(configcode);
        List<Tb_system_config> configList = new ArrayList<>();
        if (null != configid && !"".equals(configid)) {
            configList = systemConfigRepository.findByParentconfigid(configid);
        }
        return configList;
    }

    public void submitfields(String classify, String[] fieldnames) {
        //1.查出分类的字段
        List<Tb_metadata_temp> metadataTemps = metadataTempRepository.findAllByClassify(classify);

//            for (String fieldname : fieldnames) {
//                for (int i = 0; i < metadataTemps.size(); i++) {
//                    String fieldcode = fieldname.substring(0, fieldname.indexOf("_"));
//                    if (!metadataTemps.get(i).getFieldcode().equals(fieldcode)) {
//                        //不存在相同 存入
//                        Tb_metadata_temp metadata_temp = new Tb_metadata_temp();
//                        metadata_temp.setFieldcode(fieldcode);
//                        metadata_temp.setClassify(classify);
//                        metadata_temp.setFieldname(fieldname.substring(fieldname.indexOf("_") + 1));
//                        metadata_temp.setFieldtable("tb_metadata");
//                        metadataTempRepository.save(metadata_temp);
//                    }
//                }
//            }
        if(metadataTemps.size()>0) {
            if(metadataTemps.size()>fieldnames.length){
                //减少字段
                List<String> f = new ArrayList<>();
                for (String fieldname : fieldnames) {
                    String str= fieldname.substring(0, fieldname.indexOf("_"));
                    f.add(str);
                    for(int i = 0; i < metadataTemps.size(); i++){
                        if(metadataTemps.get(i).getFieldcode().contains(str)){
                            metadataTemps.remove(i);
                            f.remove(str);
                        }
                    }
                }
                metadataTempRepository.delete(metadataTemps);
                for (int i = 0; i < f.size(); i++) {
                    //不存在相同 存入
                    Tb_metadata_temp metadata_temp = new Tb_metadata_temp();
                    metadata_temp.setFieldcode(f.get(i));
                    metadata_temp.setClassify(classify);
                    metadata_temp.setFieldname(f.get(i) + "_字段描述");
                    metadata_temp.setFieldtable("tb_metadata");
                    metadataTempRepository.save(metadata_temp);
                }
            }else {
                List<String> f = new ArrayList<>();
                for (String fieldname : fieldnames) {
                    f.add(fieldname.substring(0, fieldname.indexOf("_")));
                }
                for (int i = 0; i < metadataTemps.size(); i++) {
                    if (f.contains(metadataTemps.get(i).getFieldcode())) {
                        f.remove(metadataTemps.get(i).getFieldcode());
                    }
                }
                for (int i = 0; i < f.size(); i++) {
                    //不存在相同 存入
                    Tb_metadata_temp metadata_temp = new Tb_metadata_temp();
                    metadata_temp.setFieldcode(f.get(i));
                    metadata_temp.setClassify(classify);
                    metadata_temp.setFieldname(f.get(i) + "_字段描述");
                    metadata_temp.setFieldtable("tb_metadata");
                    metadataTempRepository.save(metadata_temp);
                }
            }
        }else if (metadataTemps.size() == 0) {
            for (String fieldname : fieldnames) {
                String fieldcode = fieldname.substring(0, fieldname.indexOf("_"));
                //不存在相同 存入
                Tb_metadata_temp metadata_temp = new Tb_metadata_temp();
                metadata_temp.setFieldcode(fieldcode);
                metadata_temp.setClassify(classify);
                metadata_temp.setFieldname(fieldname.substring(fieldname.indexOf("_") + 1));
                metadata_temp.setFieldtable("tb_metadata");
                metadataTempRepository.save(metadata_temp);
            }
        }
    }

    public List<Tb_metadata_temp> findByNodeidOrderByFsequence(String nodeid) {
        return metadataTempRepository.findByClassifyOrderByFsequence(nodeid);
    }

    public Tb_metadata_temp UpdateTemplate(Tb_metadata_temp metadata_temp) {
        /*Tb_metadata_temp data_template_return = metadataTempRepository.findByTemplateid(metadata_temp.getTemplateid());
        BeanUtils.copyProperties(metadata_temp, data_template_return);*/
//        String nodeid = data_template_return.getNodeid();
//        Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
//        if (node.getLuckstate() != null && "1".equals(node.getLuckstate())) {
//            synctemplate(nodeid, "allChild", "true");
//        }
        Tb_metadata_temp data_template_return = metadataTempRepository.save(metadata_temp);
        return data_template_return;
    }

    public List<Tb_metadata_temp> gridTemplate(String nodeid, String type, String info) {
        List<Tb_metadata_temp> templates = new ArrayList<Tb_metadata_temp>();
        List<Tb_metadata_temp> templateInfo = metadataTempRepository.findAllByClassify(nodeid);
        // 数据开放中,所有表单信息都要有'开放状态'属性
        if (type != null && type.equals("数据开放")) {
            // 如果原表单中有'开放状态'属性,那么直接返回
            for (int i = 0; i < templateInfo.size(); i++) {
                Tb_metadata_temp template = templateInfo.get(i);
                if (template.getFieldcode().equals("flagopen")) {
                    return templateInfo;
                }
            }
            // 没有'开放状态'则需要手动添加信息,且在表格中显示在序号后
            Tb_metadata_temp template = new Tb_metadata_temp();
            template.setFdefault("");
            template.setFfield(false);
            template.setFieldcode("flagopen");
            template.setFieldname("开放状态");
            template.setFieldtable("tb_entry_index");
            template.setFreadonly(false);
            template.setFrequired(false);
            template.setFsequence(new Long((long) 0));
            template.setFtip("");
            template.setFtype("String");
            template.setFvalidate("");
            template.setGfield(true);
            template.setGhidden(false);
            template.setGsequence(new Long((long) 0));
            template.setGwidth(new Long((long) 150));
            template.setInactiveformfield(false);
            template.setClassify(nodeid);
            template.setQfield(false);
            template.setQsequence(new Long((long) 0));
            template.setTemplateid("");
            templates.add(template);
            if (info != null && info.equals("详细内容")) {
                Tb_metadata_temp nodefullname = new Tb_metadata_temp();
                nodefullname.setFdefault("");
                nodefullname.setFfield(false);
                nodefullname.setFieldcode("nodefullname");
                nodefullname.setFieldname("数据节点全名");
                nodefullname.setFieldtable("tb_entry_index");
                nodefullname.setFreadonly(false);
                nodefullname.setFrequired(false);
                nodefullname.setFsequence(new Long((long) 0));
                nodefullname.setFtip("");
                nodefullname.setFtype("String");
                nodefullname.setFvalidate("");
                nodefullname.setGfield(true);
                nodefullname.setGhidden(false);
                nodefullname.setGsequence(new Long((long) 0));
                nodefullname.setGwidth(new Long((long) 150));
                nodefullname.setInactiveformfield(false);
                nodefullname.setClassify(nodeid);
                nodefullname.setQfield(false);
                nodefullname.setQsequence(new Long((long) 0));
                nodefullname.setTemplateid("");
                templates.add(nodefullname);
            }
        }
        templates.addAll(templateInfo);
        return templates;
    }

    public void deleteTemplateByNodeid(String nodeid) {
        metadataTempRepository.deleteByClassify(nodeid);
    }

    public ExtMsg copyTemplate(String sourceid, String targetid, String withCode) {
        List<Tb_metadata_temp> saveTemplateList = new ArrayList<>();
        deleteTemplateByNodeid(targetid);
        List<Tb_metadata_temp> data_templateList_return = metadataTempRepository.findAllByClassify(sourceid);
        for (Tb_metadata_temp data_template_return : data_templateList_return) {
            Tb_metadata_temp data_template = new Tb_metadata_temp();
            BeanUtils.copyProperties(data_template_return, data_template);
            data_template.setTemplateid(null);
            data_template.setClassify(targetid);
            saveTemplateList.add(data_template);
        }
        metadataTempRepository.save(saveTemplateList);
        // TODO: 2019/6/6 0006 元数据模板没有节点 档号设置修改？
//        if ("true".equals(withCode)) {
//            List<Tb_codeset> codesetList = new ArrayList<>();
//            codesettingService.deleteCodesettingByNodeid(targetid);
//            List<Tb_codeset> codeset_return_list = codesettingService.findCodesetByDatanodeid(sourceid);
//            for (Tb_codeset codeset_return : codeset_return_list) {
//                Tb_codeset codeset = new Tb_codeset();
//                BeanUtils.copyProperties(codeset_return, codeset);
//                codeset.setCodeid(null);
//                codeset.setDatanodeid(targetid);
//                codesetList.add(codeset);
//            }
//            codesettingService.SaveCodeset(codesetList);
//        }
        return new ExtMsg(true, "模板复制成功", null);
    }

    //获取对象的所有属性
    public String[] getFiledName(Object o){
        Field[] fields=o.getClass().getDeclaredFields();
        String[] fieldNames=new String[fields.length];
        for(int i=0;i<fields.length;i++){
            fieldNames[i]=fields[i].getName();
        }
        return fieldNames;
    }

    //导入字段模板
    public Map<Integer,Integer> importFieldModel(String filepath, String classify){
        Map map = new HashMap();
        int impCount = 0;
        int erro=0;
        List<Tb_metadata_temp> templates = new ArrayList<>();
        if(new File(filepath).isFile()&&classify!=null&&!"".equals(classify)){
            //解析文件
            try {
                //拿到列头
                List<String> headValue = ReadExcel.getHeadField(new File(filepath));
                String[] fieldcode = new String[headValue.size()];
                headValue.toArray(fieldcode);
                //拿到字段数据
                List<List<List<String>>> excelValue = ReadExcel.readFieldModel(filepath);
                //拼接对象
                for(int i=0;i<excelValue.get(0).size();i++){
                    Tb_metadata_temp template= ValueUtil.creatMetadataTemp(fieldcode,excelValue.get(0).get(i));
                    template.setTemplateid(null);
                    template.setClassify(classify);
                    templates.add(template);
                }
                List<Tb_metadata_temp> count=metadataTempRepository.save(templates);
                impCount =count.size();
                erro = excelValue.get(0).size()+excelValue.get(excelValue.size()-1).size()-impCount;
                map.put("impCount",impCount);
                map.put("erro",erro);
            }catch (IOException IO){
                IO.printStackTrace();
            }catch (Exception E){
                E.printStackTrace();
            }
        }
        return map;
    }



    public String export(String nodeid) {
        List<Tb_metadata_temp> templateList = metadataTempRepository.findAllByClassify(nodeid);
        if (templateList.size() == 0) {
            return "";
        }


        Field[] fields = templateList.get(0).getClass().getDeclaredFields();
        String[] keys = new String[fields.length];
        String[] names = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            f.setAccessible(true);
            keys[i] = f.getName();
            names[i] = f.getName();
        }

        List<Map<String, Object>> listmap = createExcelRecord(templateList, keys);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
//        String fullName = nodesettingService.getNodefullnameLoop(node.getNodeid(), "_", "");
        Tb_system_config config = systemConfigRepository.findAllByConfigvalue(nodeid);
        String fileName = "删除【" + config.getCode() + "】节点模板数据" + "前备份" + sdf.format(new Date());

        String excelPath = rooPath +File.separator+ "backupRestore/excelTemp"+File.separator + fileName;// 拷贝后根路径
        File eleDir = new File(excelPath);
        if (!eleDir.exists()) {
            eleDir.mkdirs();
        }
        // 判断有没有存在之前压缩的zip，有的就删除
        File zipFile = new File(excelPath + ".zip");
        if (zipFile.exists()) {
            zipFile.delete();
        }

        try {
            String outName = excelPath + "/" + fileName + ".xls";
            ExportUtil eu = new ExportUtil();
            Workbook wb = new HSSFWorkbook();
            wb = eu.createWorkBook(wb, "模板数据.xls", listmap, keys, names);// 获取工作簿
            OutputStream outXlsx = new FileOutputStream(outName);
            wb.write(outXlsx);
            outXlsx.flush();
            outXlsx.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ZipUtil.zip(excelPath.replaceAll("/", "\\\\"), excelPath.replaceAll("/", "\\\\") + ".zip", "");// 压缩
        delFolder(excelPath);// 打包完，删除文件夹及文件夹里面的数据
        return excelPath;
    }

    private List<Map<String, Object>> createExcelRecord(List<Tb_metadata_temp> templateList, String[] keys) {
        List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
        Tb_metadata_temp template;
        for (int j = 0; j < templateList.size(); j++) {
            template = templateList.get(j);
            Map<String, Object> mapValue = new HashMap<>();
            for (String key : keys) {
                mapValue.put(key, GainField.getFieldValueByName(key, template));
            }
            listmap.add(mapValue);
        }
        return listmap;
    }

    public void delGroupField(String configid){
        if(!"".equals(configid)){
            String[] groupids = configid.split(",");
            for(String str :groupids){
                fieldGroupRepository.deleteByGroupid(str);
            }
        }
    }

    public List<Tb_metadata_temp> findByClassify(String metadataType){
        List<Tb_metadata_temp> list  = new ArrayList();
        if(null!=metadataType&&!"".equalsIgnoreCase(metadataType)){
            List<Tb_metadata_temp> r = metadataTempRepository.findAllByClassify(metadataType);
            return  r;
        }
        return list;
    }

    public String getClassifyByNodeid(String nodeid){
        if(null!=nodeid&&!"".equals(nodeid)){
            Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
            if(node.getClasslevel()== 5 ||node.getClasslevel()==8){//照片
                return "m2";
            }
            else if(node.getClasslevel() ==6||node.getClasslevel()==9){//视频
                return "m4";
            }
            else if(node.getClasslevel() == 7||node.getClasslevel()==10){//音频
                return "m3";
            }
        }
        return "";
    }
}
