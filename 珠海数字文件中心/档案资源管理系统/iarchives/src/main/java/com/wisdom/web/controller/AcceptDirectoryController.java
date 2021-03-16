package com.wisdom.web.controller;

import com.wisdom.util.GainField;
import com.wisdom.util.LogAnnotation;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2019/6/24.
 */
@Controller
@RequestMapping(value = "/acceptDirectory")
public class AcceptDirectoryController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AcceptDirectoryService acceptDirectoryService;

    @Autowired
    CodesettingService codesettingService;

    @Autowired
    TemplateService templateService;

    @Autowired
    CodesetRepository codesetRepository;

    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    RightOrganRepository rightOrganRepository;

    @Autowired
    SystemConfigRepository systemConfigRepository;

    @Autowired
    OrganService organService;

    @Autowired
    FundsService fundsService;

    @Autowired
    EntryIndexAcceptRepository entryIndexAcceptRepository;

    @Autowired
    EntryIndexManageRepository entryIndexManageRepository;

    @Autowired
    AcquisitionController acquisitionController;

    @Value("${system.report.server}")
    private String reportServer;//报表服务

    @RequestMapping("/main")
    public String acceptDirectory(Model model){
        model.addAttribute("reportServer", reportServer);

        return "/inlet/acceptDirectory";
    }

    @RequestMapping(value = "/entries", method = RequestMethod.GET)
    @ResponseBody
    public Page<Tb_index_detail_accept> getEntries(String nodeid, String basicCondition, String basicOperator,
                                               String basicContent, String condition, String docid, String operator, String content, String info, int page,
                                               int limit, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        //Page<Tb_entry_index_accept> list = null;
        if (info != null && "批量操作".equals(info)) {
            return acceptDirectoryService.getEntriesByacc( nodeid,basicCondition, basicOperator, basicContent, page, limit, sortobj);
        } else {
            return acceptDirectoryService.getEntriesByacc( nodeid, condition, operator, content, page, limit, sortobj);
        }
        /*List<Tb_entry_index_accept> teiList = list.getContent();
        List<AcceptEntryCapture> eList = acceptDirectoryService.getEntrys(teiList);
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        return new PageImpl<AcceptEntryCapture>(eList, pageRequest, list.getTotalElements());*/
    }

    @RequestMapping(value = "/entries/innerfile/{entryid}/", method = RequestMethod.GET)
    @ResponseBody
    public Page<Tb_entry_index_accept> getEntryInnerFile(@PathVariable String entryid, String nodeid, Integer page,
                                                            Integer start, Integer limit, String sort) {
        logger.info("nodeid:" + nodeid + ";page:" + page + ";start:" + start + ";limt:" + limit);
        Sort sortobj = WebSort.getSortByJson(sort);
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        List list = acceptDirectoryService.findAllByNodeidAndArchivecodeLike(start, limit, nodeid, entryid, sortobj);
        return new PageImpl((List<Tb_entry_index_accept>) list.get(1), pageRequest, (int) list.get(0));
    }

    /**
     * 获取 计算项字段名 或 字段名及数值
     *
     * @param entryIndexCapture
     * @return
     */
    @RequestMapping("/getCalValue")
    @ResponseBody
    public ExtMsg getCalValue(Tb_entry_index_accept entryIndexCapture, String nodeid, String nodename) {
        List<String> codeSettingFieldList = codesettingService.getCodeSettingFields(nodeid);// 获取档号设置字段集合
        if (codeSettingFieldList.size() == 0 && !nodename.equals("未归管理")) {// 档号字段未设置
            return new ExtMsg(false, "请检查档号设置信息是否正确", null);
        }
        String calFieldName = "";
        String archiveCode = "";
        Integer number = 0;
        String value = templateService.getFieldName(nodeid);
        if (codeSettingFieldList.size() >= 1) {
            Integer size = codeSettingFieldList.size() - 1;
            calFieldName = codeSettingFieldList.get(size);// 动态获取计算项字段名
            // 获取计算项单位长度
            number = Integer.parseInt(codesetRepository.findFieldlengthByDatanodeid(nodeid).get(size).toString());
            // if(number==null || number==0){
            // return new ExtMsg(false,"请检查计算项单位长度是否设置正确",null);
            // }
            String calValueStr = "";
            if (!GainField.objectIsNull(entryIndexCapture, 0) || codeSettingFieldList.size() == 1) {
                Integer calValue = null;
                try {
                    calValue = acceptDirectoryService.getCalValue(entryIndexCapture, nodeid, codeSettingFieldList);
                } catch (NumberFormatException e) {
                    return new ExtMsg(false, "获取档号失败，请检查档号构成字段（" + value + "）是否包含非数字字符", null);
                }
                if (calValue == null) {
                    return new ExtMsg(false, "获取档号失败，请检查档号构成字段（" + value + "）输入值是否为空。", null);
                }
                // 将计算项数值补0到指定位数，若calValue为null,且number数值大于4,则生成的字符串为：空格+"null",需去除空格
                calValueStr = entryIndexService.alignValue(number, calValue);
                GainField.setFieldValueByName(codeSettingFieldList.get(codeSettingFieldList.size() - 1),
                        entryIndexCapture, calValueStr);
                archiveCode = alignArchivecode(entryIndexCapture, "数据采集").getArchivecode();
                GainField.setFieldValueByName("archivecode", entryIndexCapture, archiveCode);
            } else {// 表单值未传入
                if (!"null".equals(calFieldName) && !"".equals(calFieldName)) {// 若表单值未传入，且获取到的字段名不为空，则返回字段名
                    return new ExtMsg(true, "获取计算项字段名成功", calFieldName);
                }
            }
            Map<String, String> result = new HashMap<String, String>();
            result.put("calFieldName", calFieldName);
            result.put("calValueStr", calValueStr);
            result.put("archive", archiveCode);
            if (!"null".equals(calValueStr) && !"".equals(calValueStr) && !"null".equals(calFieldName)
                    && !"".equals(calFieldName)) {
                return new ExtMsg(true, "获取计算项字段名及数值成功", result);
            }
        }
        if (!nodename.equals("未归管理")) {
            return new ExtMsg(false, "获取档号失败，请检查档号构成字段（" + value + "）输入值是否为空。", null);
        }
        return null;
    }

    private Tb_entry_index_accept alignArchivecode(Tb_entry_index_accept entry, String operate) {
        String nodeid = entry.getNodeid();
        // 处理需对齐字段
        List<String> alignFieldList = new ArrayList<>();
        List<String> codeSettingFields = new ArrayList<>();
        List<String> codeSettingSplits = new ArrayList<>();
        List<Tb_codeset> codesetList = codesettingService.findCodesetByDatanodeid(nodeid);
        codesetList.forEach(codeset -> {
            codeSettingFields.add(codeset.getFieldcode());
            codeSettingSplits.add(codeset.getSplitcode());
            alignFieldList.add(codeset.getFieldcode() + "∪" + codeset.getFieldlength());
        });
        if (codeSettingSplits.size() > 1) {
            codeSettingSplits.remove(codeSettingSplits.size() - 1);
        }
        // 执行对齐操作
        String archivecode = "";
        for (int i = 0; i < alignFieldList.size(); i++) {// 档号构成字段值补0
            String alignField = alignFieldList.get(i);
            String[] alignFieldStrs = alignField.split("∪");
            String alignFieldcode = alignFieldStrs[0];
            Integer alignFieldlength = Integer.parseInt(alignFieldStrs[1]);// 档号设置的单位长度
            String alignFieldValue = GainField.getFieldValueByName(alignFieldcode, entry) != null
                    ? (String) GainField.getFieldValueByName(alignFieldcode, entry) : "";
            String alignedFieldValue = "";
            if ("".equals(alignFieldValue) || alignFieldValue == null) {
                return null;
            }
            if (isNumeric(alignFieldValue)) {
                int currentFieldlength = alignFieldValue.length();// 字段值当前的长度
                if (alignFieldlength != currentFieldlength && alignFieldValue.length() > 0) {
                    alignedFieldValue = entryIndexService.alignValue(alignFieldlength,
                            Integer.valueOf(alignFieldValue));
                    GainField.setFieldValueByName(alignFieldcode, entry, alignedFieldValue);
                }
            }
        }
        for (int i = 0; i < codeSettingFields.size() - 1; i++) {// 重新生成档号
            String field = codeSettingFields.get(i);
            String codeSettingFieldValue = GainField.getFieldValueByName(codeSettingFields.get(i), entry) != null
                    ? (String) GainField.getFieldValueByName(codeSettingFields.get(i), entry) : "";
            if ("".equals(codeSettingFieldValue)) {
                throw new RuntimeException("档号构成字段值为空");
            } else {
                // 如果是机构名称
                String type = templateRepository.findOrganFtypeByNodeid(nodeid);
                if (field.equals("organ") && type.equals("string") && type != null) {
                    Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
                    Tb_right_organ right_organ = rightOrganRepository.findByOrganid(node.getRefid());
                    if (right_organ.getCode() != null && !"".equals(right_organ.getCode())) {
                        archivecode += right_organ.getCode() + codeSettingSplits.get(i);
                    } else {
                        archivecode += codeSettingFieldValue + codeSettingSplits.get(i);
                    }
                } else if (field.equals("entryretention")) {
                    List<String> list = systemConfigRepository.findConfigvalueByConfigcode(codeSettingFieldValue);
                    if (list.size() == 0) {
                        archivecode += codeSettingFieldValue + codeSettingSplits.get(i);
                    } else {
                        archivecode += list.get(0) + codeSettingSplits.get(i);
                    }
                } else {
                    archivecode += codeSettingFieldValue + codeSettingSplits.get(i);
                }
            }
        }
        String calFieldvalue = "";
        if (codeSettingFields.size() >= 1) {
            String calFieldcode = codeSettingFields.get(codeSettingFields.size() - 1);
            calFieldvalue = GainField.getFieldValueByName(calFieldcode, entry) != null
                    ? (String) GainField.getFieldValueByName(calFieldcode, entry) : "";
            if ("".equals(calFieldvalue) && !operate.equals("未归管理")) {
                throw new RuntimeException("计算项字段值为空");
            }
        }
        archivecode += calFieldvalue;
        entry.setArchivecode(archivecode);
        return entry;
    }

    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 获取案卷or卷内著录的初始数据
     *
     * @param nodeid
     * @param entryid
     * @param type
     * @return
     */
    @RequestMapping(value = "/getDefaultInfo", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg getDefaultInfo(String nodeid, String entryid, String type) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EntryBase capture = new EntryBase();
        List<Tb_data_template> templates = templateRepository.findByNodeid(nodeid);// 查找到当前节点的模板信息
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String organid = entryIndexService.getOrganidByNodeidLoop(nodeid);// 机构id
        for (int i = 0; i < templates.size(); i++) {
            Tb_data_template template = templates.get(i);
            if (type.equals("卷内著录")) {
                if (template.getFieldcode().equals("filecode")) {// 案卷号
                    capture.setFilecode(acceptDirectoryService.getEntry(entryid).getFilecode());
                }
                if (template.getFieldcode().equals("catalog")) {// 目录号
                    capture.setCatalog(acceptDirectoryService.getEntry(entryid).getCatalog());
                }
                if (template.getFieldcode().equals("responsible")) {// 责任者
                    capture.setResponsible(acceptDirectoryService.getEntry(entryid).getResponsible());
                }
            }
            if (template.getFieldcode().equals("filingyear")) {// 归档年度
                String year = templateRepository.findFdefaultByFieldcodeAndNodeid("filingyear", nodeid);
                if (year != null && !"".equals(year)) {
                    capture.setFilingyear(year);
                } else {
                    capture.setFilingyear(String.valueOf(cal.get(Calendar.YEAR)));
                }
            } else if (template.getFieldcode().equals("descriptiondate")) {// 著录时间
                capture.setDescriptiondate(df.format(System.currentTimeMillis()));
            } else if (template.getFieldcode().equals("descriptionuser")) {// 著录人
                capture.setDescriptionuser(userDetails.getRealname());
            } else if (template.getFieldcode().equals("organ")) {// 机构
                String organ = organService.findOrganByOrganid(organid);
                capture.setOrgan(organ == null ? "" : organ);
            } else if (template.getFieldcode().equals("funds")) {// 全宗号
                String defaultFunds = templateRepository.findFdefaultByFieldcodeAndNodeid("funds", nodeid);
                if (defaultFunds != null && !"".equals(defaultFunds)) {
                    capture.setFunds(defaultFunds);
                } else {
                    String funds = fundsService.getOrganFunds(organid);
                    if (funds != null) {// 如果是单位机构的话,直接填充获取到的全宗号
                        capture.setFunds(funds);
                    } else {// 如果是部门机构的话,需要获取到所属单位的全宗号
                        String unitOrganid = entryIndexService.getOrganInfo(organid);
                        String unitFunds = fundsService.getOrganFunds(unitOrganid);
                        capture.setFunds(unitFunds == null ? "" : unitFunds);
                    }
                }
            } else {
                // 如果模板当中的默认值不为空的话,卷内著录or案卷著录时填充模板默认值
                if (template.getFdefault() != null && !template.getFdefault().equals("")) {
                    GainField.setFieldValueByName(template.getFieldcode(), capture, template.getFdefault());
                }
            }
        }
        return new ExtMsg(true, "获取初始值成功", capture);
    }

    @RequestMapping(value = "/entries/{entryid}", method = RequestMethod.GET)
    @ResponseBody
    public AcceptEntryCapture getEntry(@PathVariable String entryid) {
        return acceptDirectoryService.getEntry(entryid);
    }

    @LogAnnotation(module = "目录中心-目录接收", sites = "1", fields = "title,archivecode", connect = "##题名；,##档号；", startDesc = "保存操作，条目详情：")
    @RequestMapping(value = "/entries", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg saveEntry(@ModelAttribute("form") AcceptEntryCapture entry, String type, String operate) {
        entry.setEntryIndex(entry.getRawEntryIndex());
        entry.setEntryDetial(entry.getRawEntryDetail());
        Tb_data_node node = entryIndexService.getNodeLevel(entry.getNodeid());
        String code = alignArchivecode(entry.getEntryIndex(), operate).getArchivecode();
        if (!code.isEmpty()) {// 如果档号不为空
            // 查询当前节点所有数据的档号,判断档号的唯一性
            List<String> archivecode = entryIndexAcceptRepository.findCodeByNodeid(entry.getNodeid());
            if (archivecode.size() > 0) {
                if (type.equals("add") && isExist(code, archivecode)) {
                    return new ExtMsg(false, "保存失败，档号重复！", null);
                }
                if (type.equals("modify")) {
                    Tb_entry_index_accept entryIndex = entryIndexAcceptRepository.findByEntryid(entry.getEntryid());
                    // 如果修改了档号
                    if (entryIndex.getArchivecode() != null && !code.equals(entryIndex.getArchivecode())
                            && isExist(code, archivecode)) {
                        return new ExtMsg(false, "保存失败，档号重复！", null);
                    }
                }
            }
        } else {
            // 如果档号为空,且非未归管理
            if (!node.getNodename().equals("未归管理") && node.getNodename().equals("文件管理") && node.getNodename().equals("资料管理")) {
                return new ExtMsg(false, "保存失败，档号为空", null);
            }
        }
        AcceptEntryCapture result = acceptDirectoryService.saveEntry(entry, type);
        return new ExtMsg(result != null ? true : false, result != null ? "保存成功" : "保存失败", result);
    }

    // 判断档号是否存在
    private boolean isExist(String entryCode, List<String> archivecode) {
        for (int i = 0; i < archivecode.size(); i++) {
            String code = archivecode.get(i);
            // 如果档号存在(传过来的档号在节点当中已经存在)
            if (code != null && entryCode.equals(code)) {
                return true;
            }
        }
        return false;
    }

    @LogAnnotation(module = "目录中心-目录接收", sites = "1",startDesc = "删除操作，条目编号：")
    @RequestMapping("/delete")
    @ResponseBody
    public ExtMsg delEntry(String[] entryids, String isSelectAll, String nodeid, String condition, String operator,
                           String content) {
        String[] entryInfo;
        if("true".equals(isSelectAll)){
            entryInfo = acceptDirectoryService.findEntryids(nodeid);
        }else{
            entryInfo = entryids;
        }
        Integer del = 0;
        List<String[]> subAry = new InformService().subArray(entryInfo, 1000);// 处理ORACLE1000参数问题
        for (String[] ary : subAry) {
            del += acceptDirectoryService.delEntry(ary);
        }
        if (del > 0) {
            return new ExtMsg(true, "删除成功", del);
        }
        return new ExtMsg(false, "删除失败", null);
    }

    //@LogAnnotation(module = "目录接收",startDesc = "接收操作，单据id为：",sites = "1")
    @RequestMapping(value = "/move")
    @ResponseBody
    public ExtMsg move(String[] entryids, String nodeid,String isSelectAll,String condition, String operator, String content,String movetype) throws InterruptedException, ExecutionException {
        String[] entryidData={};
        if("true".equals(isSelectAll)){
            List<String> entryList = entryIndexService.getIndexCaptureIds(nodeid, condition, operator, content,movetype);
            entryidData = new String[entryList.size()];
            entryList.toArray(entryidData);
        }else {
            entryidData = entryids;
        }
        List<String[]> subAry = new InformService().subArray(entryidData, 1000);//处理ORACLE1000参数问题
        Tb_data_node node = entryIndexService.getNodeLevel(nodeid);
        String repeact = "";
        // 创建一个线程池
        ExecutorService pool = Executors.newFixedThreadPool(subAry.size()*2);
        List<Callable<String>> tasks = new ArrayList<>();
        for (String[] ary : subAry) {
            List<Tb_entry_index_accept> captures=null;
            int finaNum=500;//一次性查询的最大条目数
            if(ary.length>finaNum){
                int quotient = ary.length / finaNum;
                for(int i = 0; i <= quotient; i ++ ){
                    int dataLength = (i+1)*finaNum>ary.length?ary.length-i*finaNum:finaNum;
                    String[] arry2=new String[dataLength];
                    System.arraycopy(ary,0,arry2,0,dataLength);
                    captures=entryIndexAcceptRepository.findByEntryidIn(arry2);
                    Callable<String> callable=new moveCallable(captures);//开启一个线程执行查询
                    tasks.add(callable);
                }
            }else {
                captures=entryIndexAcceptRepository.findByEntryidIn(ary);
                Callable<String> callable=new moveCallable(captures);
                tasks.add(callable);
            }
            for (int i = 0; i < captures.size(); i++){
                ary[i] = String.format("%1$-36s",(String) ary[i]);
            }
        }
        // 从Future对象上获取线程任务的返回值
        List<Future<String>> futures=pool.invokeAll(tasks);
        for (Future f : futures) {
            repeact+=f.get();
        }
        pool.shutdown();
        //如果重复档号值不为空且非未归管理,那么就判断档号重复
        if (!repeact.equals("") && node != null&&!"未归管理".equals(node.getNodename())) {
            repeact = repeact.substring(0, repeact.length() - 1);
            return new ExtMsg(false, "档号记录重复", "档号记录:" + repeact + "重复");
        }
        int[] num = acceptDirectoryService.move(subAry);
        if (num[0] > 0 && num[1] > 0) {
            acquisitionController.delTransWriteLog(entryidData,"目录接收","接收操作");
            return new ExtMsg(true, "入库成功", num);
        }
        return new ExtMsg(false, "入库异常", null);
    }

    class moveCallable implements Callable<String> {
        private List<Tb_entry_index_accept> captures;

        moveCallable(List<Tb_entry_index_accept> captures) {
            this.captures=captures;
        }

        @Override
        public String call() throws Exception {
            String repeact="";
            for (int i = 0; i < captures.size(); i++){
                if (captures.get(i).getArchivecode() != null) {
                    if (entryIndexManageRepository.findByArchivecode(captures.get(i).getArchivecode()).size() > 0) {
                        repeact += captures.get(i).getArchivecode() + "、";
                    }
                }
            }
            return repeact;
         }
    }

    /**
     * 获取接收明细记录
     *
     * @param imptype
     *            导入明细类型
     */
    @RequestMapping("/getimpRecord")
    @ResponseBody
    public Page<Tb_imp_record> getimpRecord(String imptype,int page,int limit,Sort sort) {
            return acceptDirectoryService.getimpRecord(imptype,new PageRequest(page-1,limit,
                    sort==null?new Sort(Sort.Direction.DESC,"imptime"):sort));
    }


    @RequestMapping(value = "/entriesPost")
    @ResponseBody
    public Page<AcceptEntryCapture> getEntriesPost(String nodeid, String basicCondition, String basicOperator,
                                                        String basicContent, String condition, String docid, String operator, String content, String info, int page,
                                                        int limit, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        Page<Tb_entry_index_accept> list = null;
        if (info != null && "批量操作".equals(info)) {
            list = acceptDirectoryService.getEntries(nodeid, basicCondition, basicOperator, basicContent, page, limit, sortobj);
        } else {
            list = acceptDirectoryService. getEntries(nodeid, condition, operator, content, page, limit, sortobj);
        }
        List<Tb_entry_index_accept> teiList = list.getContent();
        List<AcceptEntryCapture> eList = acceptDirectoryService.getEntrys(teiList);
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        return new PageImpl<AcceptEntryCapture>(eList, pageRequest, list.getTotalElements());
    }
}
