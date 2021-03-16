package com.wisdom.web.service;

import com.wisdom.util.ConfigValue;
import com.wisdom.util.CreateExcel;
import com.wisdom.util.SpecificationUtil;
import com.wisdom.util.ZipUtils;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 档案调出服务层
 */
@Service
@Transactional
public class ArchivesCalloutService {

    @Autowired
    SzhAssemblyRepository szhAssemblyRepository;

    @Autowired
    SzhArchivesCalloutRepository szhArchivesCalloutRepository;

    @Autowired
    SzhCalloutEntryRepository szhCalloutEntryRepository;

    @Autowired
    SzhEntryIndexCaptureRepository szhEntryIndexCaptureRepository;

    @Autowired
    SzhCalloutCaptureRepository szhCalloutCaptureRepository;

    @Autowired
    SzhEntryDetailCaptureRepository szhEntryDetailCaptureRepository;

    @Autowired
    SzhMediaMetadataRepository szhMediaMetadataRepository;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    CodesetRepository codesetRepository;

    @Autowired
    SzhEntryTrackRepository szhEntryTrackRepository;

    public Page<Szh_archives_callout> getArchivesCalloutBySearch(int page, int limit, String sort,String condition, String operator, String content){
        Sort sortobj=WebSort.getSortByJson(sort);
        Specifications sp = null;
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        return szhArchivesCalloutRepository.findAll(sp, new PageRequest(page - 1, limit,sortobj));
    }

    public Page<Szh_callout_entry> getCalloutEntryBySearch(String batchcode, int page, int limit, String sort, String condition, String operator, String content){
        Sort sortobj=WebSort.getSortByJson(sort);
        Specifications sp = null;
        if (batchcode!=null) {
            sp = Specifications.where(new SpecificationUtil("batchcode","equal",batchcode));
        }
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        return szhCalloutEntryRepository.findAll(sp, new PageRequest(page - 1, limit,sortobj));
    }

    public Szh_archives_callout getArchivesCallout(String id){
        return szhArchivesCalloutRepository.findOne(id);
    }

    public void batchAddFormSubmit(Szh_archives_callout callout)throws Exception{
        if(callout.getId()==null||"".equals(callout.getId())){//新增
            String code = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());//获取时间
            String random = (int)(Math.random()*(9999-1000+1))+1000+"";//获取4位随机数
            Szh_assembly assembly = szhAssemblyRepository.findByCode(callout.getAssemblycode());
            callout.setLendpages(0);//设置借出页数
            callout.setReturncopies(0);//设置归还份数
            callout.setReturnpages(0);//设置归还页数
            callout.setReturnstatus("未归还");//设置归还状态
            callout.setBatchstatus("未处理");//设置批次状态
            callout.setConnectstatus("未移交");//设置移交状态
            callout.setAssembly(assembly.getTitle());//设置流水线名
            callout.setBatchcode(code+random);//设置批次号
        }
        callout.setLendcopies(callout.getAjcopies());//设置借出份数(与案卷份数是同一字段?)
        szhArchivesCalloutRepository.save(callout);
    }

    public Szh_callout_entry getEntryAddForm(String id){
        return szhCalloutEntryRepository.findOne(id);
    }

    public String entryAddFormSubmit(Szh_callout_entry entry, String nodeid)throws Exception{
        List<Szh_callout_entry> callouts =  szhCalloutEntryRepository.findByArchivecode(entry.getArchivecode());
        if((callouts.size()==1&&(entry.getId()==null||"".equals(entry.getId())))||callouts.size()>1){
            return "档号重复";
        }

        if(entry.getId()==null||"".equals(entry.getId())){
           String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
           SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 系统绑定对象(全局)
           entry.setWorkstate("未处理");//设置工作状态
           entry.setLendstate("已借出");//设置借出状态
           entry.setCheckstate("未审核");//设置检查状态
           entry.setScanstate("未扫描");//设置扫描状态
           entry.setPicturestate("未处理");//设置图片状态
           entry.setTidy("未签收");//设置整理状态
           entry.setScan("未签收");//设置扫描状态
           entry.setPictureprocess("未签收");//设置图片处理状态
            entry.setDefinition("未签收");//设置属性定义状态
           entry.setAudit("未签收");//设置审核状态
           entry.setRecord("未签收");//设置著录状态
           entry.setBind("未签收");//设置装订状态
           entry.setEntrysigner(userDetails.getRealname());//设置实体签收人
           entry.setEntrysigncode(userDetails.getLoginname());//设置实体签收工号
           entry.setEntrysigntime(nowTime);//设置实体签收时间
           entry.setEntrysignorgan(userDetails.getOrganid());//设置签收单位
           entry.setA0(0);//设置A0页数
           entry.setA1(0);//设置A1页数
           entry.setA2(0);//设置A2页数
           entry.setA3(0);//设置A3页数
           entry.setA4(0);//设置A4页数
           entry.setZa4(0);//设置折算A4页数
             entry = szhCalloutEntryRepository.save(entry);
            //保存基础信息
            Szh_entry_index_capture entry_index_capture = new Szh_entry_index_capture();
            entry_index_capture.setTitle("");
            entry_index_capture.setArchivecode(entry.getArchivecode());
            entry_index_capture.setNodeid(nodeid);
            entry_index_capture.setUserid("*");//多点录入标识(非用户录入数据,用于挂接原文)
            entry_index_capture = szhEntryIndexCaptureRepository.save(entry_index_capture);

            //保存扩展信息
            Szh_entry_detail_capture entry_detail_capture = new Szh_entry_detail_capture();
            entry_detail_capture.setEntryid(entry_index_capture.getEntryid());
            szhEntryDetailCaptureRepository.save(entry_detail_capture);

            //保存调出条目与临时数据对应信息
            Szh_callout_capture callout_capture = new Szh_callout_capture();
            callout_capture.setEntryid(entry_index_capture.getEntryid());
            callout_capture.setCalloutid(entry.getId());
            szhCalloutCaptureRepository.save(callout_capture);

            //增加实物流向追踪
            Szh_entry_track track=new Szh_entry_track();
            track.setEntryid(entry.getId());
            track.setBatchcode(entry.getBatchcode());
            track.setArchivecode(entry.getArchivecode());
            track.setEntrysigner(entry.getEntrysigner());
            track.setNodename("进件登记");
            track.setEntrysigntime(entry.getEntrysigntime());
            track.setStatus("已签收");
            szhEntryTrackRepository.save(track);
        }else{//修改
            List<Szh_entry_index_capture> captures = szhEntryIndexCaptureRepository.findEntryBycallout(entry.getId());
            if(captures!=null&&captures.size()>0){
                for(Szh_entry_index_capture capture:captures){
                    capture.setArchivecode(entry.getArchivecode());
                    capture.setNodeid(nodeid);
                }
            }
            //修改实物流向信息
            List<Szh_entry_track> tracks = szhEntryTrackRepository.findByEntryid(entry.getId());
            if(tracks!=null&&tracks.size()>0){
                for(Szh_entry_track track:tracks){
                    track.setArchivecode(entry.getArchivecode());
                }
            }
            szhCalloutEntryRepository.save(entry);
        }
        return "操作成功";
    }

    public void batchDel(String[] batchcodes) throws Exception{
        String[] entryids = szhCalloutEntryRepository.findIdByBatchcodes(batchcodes);
        szhArchivesCalloutRepository.deleteByBatchcodeIn(batchcodes);
        if(entryids!=null&&entryids.length>0){
            entryDel(entryids);
        }
    }

    public void entryDel(String[] ids)throws Exception{
        szhCalloutEntryRepository.deleteByIdIn(ids);//删除批次条目
        List<String> idsList = Arrays.asList(ids);
        List<Szh_entry_index_capture> captures = szhEntryIndexCaptureRepository.findEntryByCalloutIds(idsList);
        if(captures!=null&&captures.size()>0){
            String[] archivecodes = new String[captures.size()];
            String[] entrids = new String[captures.size()];
            for(int i=0;i<captures.size();i++){
                archivecodes[i] = captures.get(i).getArchivecode();
                entrids[i] = captures.get(i).getEntryid();
            }
            szhEntryIndexCaptureRepository.deleteByEntryidIn(entrids);//删除临时基础条目
            szhEntryDetailCaptureRepository.deleteByEntryidIn(entrids);//删除扩展条目
            szhMediaMetadataRepository.deleteByArchivecodeIn(archivecodes);//删除临时条目元数据
            szhCalloutCaptureRepository.deleteByEntryidIn(entrids);//删除中间表数据
        }
    }

    public boolean entryReturn(String[] ids){
        boolean state = false;
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Szh_callout_entry> callout_entries = szhCalloutEntryRepository.findByIdIn(ids);
        for(Szh_callout_entry callout_entry:callout_entries){
            String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            callout_entry.setLendstate("已归还");
            callout_entry.setReturnman(userDetails.getRealname());
            callout_entry.setReturntime(nowTime);
            callout_entry.setReturnloginname(userDetails.getLoginname());
        }
        callout_entries = szhCalloutEntryRepository.save(callout_entries);
        Szh_archives_callout callout = szhArchivesCalloutRepository.findByBatchcode(callout_entries.get(0).getBatchcode());
        //设置归还份数
        int returncount = callout.getReturncopies()+callout_entries.size();
        callout.setReturncopies(returncount);
        szhArchivesCalloutRepository.save(callout);
        if(callout_entries.size()>0){
            state = true;
        }
        return state;
    }

    public boolean isHasCalloutEntry(String batchcode){
        List<Szh_callout_entry> entries = szhCalloutEntryRepository.findByBatchcode(batchcode);
        if(entries.size()>0){
            return true;
        }else{
            return false;
        }
    }
    public void getDownLoadEntryTemp(HttpServletRequest request, HttpServletResponse response, String nodeid, String type) {
        String[] fieldName = getFieldNames(nodeid,type);
        String nodeName = null;
        if(type!=null&&"yes".equals(type)){
            nodeName = "导入不含条目信息";
        }else{
            nodeName = getParentNodeName(nodeid, fieldName.length);
        }
        String tempFileName = "字段模板";
        //创建字段模板
        String dir = ConfigValue.getPath("system.document.rootpath");
        String path = dir + "/OAFile" + "/导出模板/digtaiprocess/";//
        try {
            File f = new File(path);
            f.mkdirs();
            if (!f.exists()) {
                throw new RuntimeException("createXml()---创建文件夹失败");
            }
            //Workbook workbook = CreateExcel.createTemp(fieldCode, fieldName);//---创建excel字段模板
            Workbook workbook = CreateExcel.createFieldNameTemp(fieldName);
            OutputStream os = new FileOutputStream(new File(path + "/" + nodeName + ".xls"));
            workbook.write(os);
            os.flush();
            os.close();
            workbook.close();
            //创建xml字段模板

            //将2个文件进行压缩
            InputStream inputStream = new FileInputStream(new File(path + "/" + nodeName + ".xls"));
            OutputStream out = response.getOutputStream();
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename=\"" + new String((getOutName(request,nodeName) + ".xls").getBytes(), "iso-8859-1") + "\"");
            byte[] b = new byte[1024 * 1024 * 10];
            int leng = 0;
            while ((leng = inputStream.read(b)) != -1) {
                out.write(b, 0, leng);
            }
            out.flush();
            inputStream.close();
            out.close();
            ZipUtils.delFolder(path);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public String[] getFieldNames(String nodeId,String type) {
        String[] str = {};
        if (nodeId != null) {
            if(type!=null&&"yes".equals(type)){
                String[] strtype = new String[3];
                strtype[0] = "档号";
//                strtype[1] = "字轨";
//                strtype[2] = "案号";
                return strtype;
            }else{
                List<Tb_data_template> templates = templateRepository.findByNodeid(nodeId);
                //对模板字段进行排序，优先字轨、案号、档号及档号组成的字段，后面的就根据模板中表单的先后顺序显示
                Map<String,String> getFieldname = new HashMap<>();
                for(Tb_data_template template : templates){
                    getFieldname.put(template.getFieldcode(),template.getFieldname());
                }
                String[] filecodes = templateRepository.findFieldCodesByNodeidOrderfs(nodeId);
                String[] strnew = new String[ filecodes.length];
                //档号组成字段
                List<String> arcodes = codesetRepository.findFieldcodeByDatanodeid(nodeId);
                boolean flagF49 = false;
                boolean flagF50 = false;
                for(int i=0;i<filecodes.length;i++){
                    if("archivecode".equals(filecodes[i])){
                        filecodes = delete(i,filecodes);
                    }
                    if("f49".equals(filecodes[i])){
                        filecodes = delete(i,filecodes);
                        flagF49 = true;
                    }
                    if("f50".equals(filecodes[i])){
                        filecodes = delete(i,filecodes);
                        flagF50 = true;
                    }
                }
                if(flagF49&&flagF50){  //判断当前模板是否有字轨和案号
                    String[] arcodestr = new String[arcodes.size()+3];
                    for(int i=0;i<arcodes.size();i++){
                        for(int j=0;j<filecodes.length;j++){
                            if(arcodes.get(i).equals(filecodes[j])){
                                arcodestr[i+3] = filecodes[j];
                                filecodes = delete(j,filecodes);
                                break;
                            }
                        }
                    }
                    arcodestr[0] = "f49";
                    arcodestr[1] = "f50";
                    arcodestr[2] = "archivecode";
                    System.arraycopy(arcodestr, 0, strnew, 0, arcodestr.length);
                    System.arraycopy(filecodes, 0, strnew, arcodestr.length, filecodes.length);
                }else {
                    String[] arcodestr = new String[arcodes.size()+1];
                    for(int i=0;i<arcodes.size();i++){
                        for(int j=0;j<filecodes.length;j++){
                            if(arcodes.get(i).equals(filecodes[j])){
                                arcodestr[i+1] = filecodes[j];
                                filecodes = delete(j,filecodes);
                                break;
                            }
                        }
                    }
                    arcodestr[0] = "archivecode";
                    System.arraycopy(arcodestr, 0, strnew, 0, arcodestr.length);
                    System.arraycopy(filecodes, 0, strnew, arcodestr.length, filecodes.length);
                }

                String[] fieldnameetr = new String[strnew.length];
                for(int i=0;i<strnew.length;i++){
                    fieldnameetr[i] = getFieldname.get(strnew[i]);
                }
                return fieldnameetr;
            }
        }else{
            return str;
        }
    }

    //获取当前节点的所有父节点
    public String getParentNodeName(String nodeid, int count) {
        String nodeName = dataNodeRepository.findNodenameByNodeid(nodeid);
        int nodeLevel = Integer.parseInt(dataNodeRepository.findNodeLevelByNodeid(nodeid));
        String parentNodeId = dataNodeRepository.findParentNodeidByNodeid(nodeid);
        String str = new String();
        if (parentNodeId != null) {
            String parentName = dataNodeRepository.findNodenameByNodeid(parentNodeId);
            for (int i = 2; i < nodeLevel; i++) {
                parentNodeId = dataNodeRepository.findParentNodeidByNodeid(parentNodeId);
                str = dataNodeRepository.findNodenameByNodeid(parentNodeId) + "_" + str;
            }
            nodeName = str + parentName + "_" + nodeName;
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        return nodeName + "_" + sdf.format(date) + "_字段数_" + count;
    }

    /**
     * 处理文件下载时的中文名
     *
     * @param request
     *            HttpServletRequest
     * @param name
     *            文件名
     * @return 转码过的文件名
     * @throws Exception
     */
    public static String getOutName(HttpServletRequest request, String name) throws IOException {
        String outName = MimeUtility.encodeText(name, "UTF8", "B");
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        Browser browser = userAgent.getBrowser();
        String browseName = browser.getName() != null ? browser.getName().toLowerCase() : "";
        outName = URLEncoder.encode(name, "UTF8");
        return outName;
    }

    public String[] delete(int index, String array[]) {
        //数组的删除其实就是覆盖前一位
        String[] arrNew = new String[array.length - 1];
        for (int i = index; i < array.length - 1; i++) {
            array[i] = array[i + 1];
        }
        System.arraycopy(array, 0, arrNew, 0, arrNew.length);
        return arrNew;
    }
}
