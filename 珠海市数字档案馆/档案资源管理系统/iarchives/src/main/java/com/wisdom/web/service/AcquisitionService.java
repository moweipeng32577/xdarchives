package com.wisdom.web.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfStream;
import com.itextpdf.text.pdf.PdfWriter;
import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.*;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;

import com.wisdom.web.security.SecurityUser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.misc.BASE64Encoder;
import sun.net.www.protocol.http.HttpURLConnection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Rong on 2017/10/31.
 */
@Service
@Transactional
public class AcquisitionService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AuditService auditService;

    @Autowired
    PublicUtilService publicUtilService;

    @Autowired
    AcquisitionService acquisitionService;

    @Autowired
    EntryIndexCaptureService entryIndexCaptureService;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    NodeRepository nodeRepository;

    @Autowired
    FlowsRepository flowsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    TransdocRepository transdocRepository;

    @Autowired
    OrganService organService;

    @Autowired
    EntryIndexCaptureRepository entryIndexCaptureRepository;

    @Autowired
    TransdocEntryRepository transdocEntryRepository;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    EntryDetailCaptureRepository entryDetailCaptureRepository;
    
    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    CodesettingService codesettingService;

    @Autowired
    CodesetRepository codesetRepository;

    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    UserNodeRepository userNodeRepository;

    @Autowired
    TransdocPreviewRepository transdocPreviewRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Value("${system.document.rootpath}")
    private String rootpath;// 系统文件根目录


    public int transfor(String[] entryidData,String transdocid,String state,List<String> jnEntryIds){
        //String[] fileIds=parententryid.split(",");
        for(String entryid:jnEntryIds){
            Tb_transdoc_entry transdocEntry = new Tb_transdoc_entry();
            transdocEntry.setEntryid(entryid);
            transdocEntry.setDocid(transdocid);
            transdocEntry.setStatus(state);//设置条目状态为“待审核”
//            for(int i=0;i<fileIds.length;i++){
//                if(entryid.trim().equals(fileIds[i].trim())&&!"".equals(fileIds[i])){//非卷内条目
//                    transdocEntry.setParententryid("");
//                    break;
//                }
//            }
            transdocEntryRepository.save(transdocEntry);
        }
        for(String entryid:entryidData){
            Tb_transdoc_entry transdocEntry = new Tb_transdoc_entry();
            transdocEntry.setEntryid(entryid);
            transdocEntry.setDocid(transdocid);
            transdocEntry.setStatus(state);//设置条目状态为“待审核”
//            for(int i=0;i<fileIds.length;i++){
//                if(entryid.trim().equals(fileIds[i].trim())&&!"".equals(fileIds[i])){//非卷内条目
                    transdocEntry.setParententryid(entryid);
//                    break;
//                }
//            }
            transdocEntryRepository.save(transdocEntry);
        }
        return entryidData.length;
    }

    /**
     * 页数矫正,需求：传入的文件级记录集执行页数矫正(例如 2条记录，顺序号分别为001，002，页号分别为5，10,则第1条执行矫正后页数赋值为5)
     * 处理的条件：档号和页号字段有值；顺序号/件号相连的记录的页号、页数有值；
     *
     * 根据档号设置组成字段，匹配去查找当前比较条目的下一条目，即查找卷内顺序号+1的条目，页数=匹配到的条目页号-当前条目的页号
     * 不予处理的条件，并提示信息“矫正失败，请检查档号、页号数据是否规范正确!”：
     * 档号设置不存在、查找失败，即找不到下一条、档号重复(查找的条目存在重复的档号)、没有页号、页号比原来的小(计算结果为负数)
     * @param entryids 选择的记录ID
     * @return
     */
    public String  pgNumCorrect(String entryids){
        StringBuffer msg =new StringBuffer();
        String[] entryidData = entryids.split("、");
        //根据entryid获取条目列表
        List<Tb_entry_index_capture> entryIndexCaptures=entryIndexCaptureRepository.findByEntryidIn(entryidData);
        String nodeid="";
        Integer number = 0;
        List<String> codesetFieldCodes = new ArrayList<>();
        List<String> codeSettingSplitCodes = new ArrayList<>();
        if(entryIndexCaptures.size()>0){
            nodeid= entryIndexCaptures.get(0).getNodeid();
            //获取计算项单位长度
            number= codesettingService.getLastCalFieldLength(nodeid);
            //获取档号组成字段
            codesetFieldCodes = codesetRepository.findFieldcodeByDatanodeid(nodeid);
            //获取档号设置分割符号
            codeSettingSplitCodes = codesettingService.getCodeSettingSplitCodes(nodeid);
        }
        if(codesetFieldCodes.size()==0||codeSettingSplitCodes.size()==0){
            msg.append("矫正失败，请检查档号、页号数据是否规范正确!<br/>");
            msg.append("失败记录："+entryids);
            return msg.toString();
        }
        //正规纯数字组成
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        //正规范围型(036/037)
        Pattern backslash = Pattern.compile("^(\\d+)[//](\\d+)$");
        //正规范围型(036-037)
        Pattern bars = Pattern.compile("^(\\d+)[-](\\d+)$");
        //正规036/037
        for(int i =0;i<entryIndexCaptures.size();i++){
            String pageno =entryIndexCaptures.get(i).getPageno();
            ////判断页号是否为空
            if ("".equals(pageno)||pageno==null){
                pgNumMsg(msg,entryIndexCaptures,i);
                continue;
            }
            //不符合036/037或036-037规则且不是数字
            if(!backslash.matcher(pageno).matches()&&!bars.matcher(pageno).matches()&&!pattern.matcher(pageno).matches()){
                pgNumMsg(msg,entryIndexCaptures,i);
                continue;
            }
            String[] pagenos = null;
            //如果符合036/037
            if(backslash.matcher(pageno).matches()){
                pagenos = pageno.split("/");
            }else if (bars.matcher(pageno).matches()){//如果符合036-037
                pagenos = pageno.split("-");
            }
            if (pagenos != null && pagenos.length > 1) {
                //判断是否属于错误范围型 ，如 037/036，就当异常信息处理
                if (Integer.parseInt(pagenos[0]) > Integer.parseInt(pagenos[1])) {
                    pgNumMsg(msg, entryIndexCaptures, i);
                    continue;
                }
                //若符合036/0037,则页数为（037-036）+1=2
                String pages = String.valueOf(Integer.parseInt(pagenos[1])-Integer.parseInt(pagenos[0])+1);
                entryIndexCaptures.get(i).setPages(pages);
                continue;
            }
            //获取每个构成字段的值
            List<String> codesetFieldValues = new ArrayList<>();
            //获取最后一项值
            String calValue="";
            Map<String, Map<String, String>> mapFiled = entryIndexService.getConfigMap();//获取参数设置的MAP
            List<Tb_data_template> enumList = templateRepository.getByNodeidFtype("enum", nodeid);//获取某节点的模板中属于enum的字段
            for(int j=0;j<codesetFieldCodes.size();j++){
                String codesetFieldValue = (String) GainField.getFieldValueByName(codesetFieldCodes.get(j),
                        entryIndexCaptures.get(i));
                codesetFieldValue = entryIndexService.getConfigByName(codesetFieldCodes.get(j), codesetFieldValue, enumList, mapFiled);
                codesetFieldValues.add(codesetFieldValue);
                if(j==codesetFieldCodes.size()-1){
                    calValue= codesetFieldValue;
                }
            }
            //判断最后一项是否是数字
            if(calValue==null||"".equals(calValue)||!pattern.matcher(calValue).matches()){
                pgNumMsg(msg,entryIndexCaptures,i);
                continue;
            }else{
                //最后一个计算项+1
                calValue = entryIndexService.alignValue(number, Integer.valueOf(calValue)+1);
                //生成新的档号
                String archivecode = entryIndexService.produceArchivecode(codesetFieldValues,
                        codeSettingSplitCodes,calValue,nodeid);
                List<Tb_entry_index_capture> entryIndexCaptureList =entryIndexCaptureRepository.findByArchivecode
                        (archivecode);
                //判断是否档号重复，即查找是否有多条相同档号的条目
                if(entryIndexCaptureList.size()==0||entryIndexCaptureList.size()>1){
                    pgNumMsg(msg,entryIndexCaptures,i);
                    continue;
                }else{
                    Tb_entry_index_capture find = entryIndexCaptureList.get(0);
                    //判断有没有页号
                    if("".equals(find.getPageno())||find.getPageno()==null){
                        pgNumMsg(msg,entryIndexCaptures,i);
                        continue;
                    }
                    String[] findPagenos = null;
                    //如果符合036/037
                    if(backslash.matcher(find.getPageno()).matches()){
                        findPagenos = find.getPageno().split("/");
                    }else if (bars.matcher(find.getPageno()).matches()){//如果符合036-037
                        findPagenos = find.getPageno().split("-");
                    }
                    if (findPagenos == null) {
                        //页号比原来条目还要小
                        if (!pattern.matcher(find.getPageno()).matches
                                () || Integer.parseInt(find.getPageno()) < Integer.parseInt(pageno)) {
                            pgNumMsg(msg, entryIndexCaptures, i);
                            continue;
                        }

                        String pages = String.valueOf(Integer.parseInt(find.getPageno()) - Integer.parseInt(pageno));
                        entryIndexCaptures.get(i).setPages(pages);
                    }else{
                        // 判断是否属于错误范围型 ，如 037/036，就当异常信息处理
                        //判断当前条目的页号是否小于（如036/037）036
                        if (Integer.parseInt(findPagenos[0]) > Integer.parseInt
                                (findPagenos[1])||Integer.parseInt(findPagenos[0])<Integer.parseInt(pageno)) {
                            pgNumMsg(msg, entryIndexCaptures, i);
                            continue;
                        }
                        //若符合036/0037,则页数为036-当前条目的页号
                        String pages = String.valueOf(Integer.parseInt(findPagenos[0])-Integer.parseInt(pageno));
                        entryIndexCaptures.get(i).setPages(pages);
                    }
                }
            }
        }
        return msg.toString();
    }

    private void pgNumMsg(StringBuffer msg,List<Tb_entry_index_capture> entryIndexCaptures,int i){
        if("".equals(msg.toString())){
            msg.append("矫正失败，请检查档号、页号数据是否规范正确!<br/>");
            msg.append("失败记录："+entryIndexCaptures.get(i).getArchivecode());
        }else{
            msg.append("、" + entryIndexCaptures.get(i).getArchivecode());
        }
    }

    /**
     * 返回报表文件的BASE64编码文件流
     * @param docids
     * @param reportName
     * @return
     */
    public ExtMsg writeCataloguePdf(String docids,String reportName){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //获取服务端口
        int portStr = request.getServerPort();
        BufferedOutputStream bout = null;
        HttpURLConnection connection = null;
        InputStream is=null;
        String res="";
        BufferedInputStream bin=null;
        ByteArrayOutputStream baos=null;

        try {
            //String url = "http://localhost:" + portStr + "/ureport/pdf?entryid=" + entryid + "&booknumber=" + booknumber + "&_u=file:JNWJML.ureport.xml&_t=1,4,5,6,7&_i=1";
            String url = "http://localhost:" + portStr + "/ureport/pdf?docid=" + docids + "&_u=file:"+reportName+".ureport.xml";//中文会返回400状态码，用字母返回200
            URL urlhttp = new URL(url);//需要设置请求免拦截  .antMatchers("/ureport/**").permitAll()
            connection = (HttpURLConnection) urlhttp.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000 * 1000);
            connection.setReadTimeout(3000000);
            connection.setRequestProperty("Content-Type","application/pdf;charset=UTF-8");
            int responseCode = connection.getResponseCode();
            connection.connect();
            if(responseCode == HttpURLConnection.HTTP_OK){
                is = connection.getInputStream();
                bin=new BufferedInputStream(is);
                baos=new ByteArrayOutputStream();
                bout=new BufferedOutputStream(baos);
                byte[] buffer=new byte[1024];
                int len=bin.read(buffer);
                while(len!=-1){
                    bout.write(buffer,0,len);
                    len=bin.read(buffer);
                }
                //读取完毕
                bout.flush();
                byte[] bytes=baos.toByteArray();
                BASE64Encoder encoder = new BASE64Encoder();
                res= encoder.encode(bytes);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ExtMsg(false, "获取失败", null);
        }finally {
            try{
                if(bin!=null){
                    bin.close();
                    is.close();
                }
                if(bout!=null){
                    bout.close();
                    baos.close();
                }
                if(connection!=null){
                    connection.disconnect();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return new ExtMsg(true, "获取成功", res);
        //return new ExtMsg(true, filepath + File.separator + reportName+".pdf", null);
    }

    /**
     * 直接生成pdf报表文件到服务器
     * @param filepath  pdf存放地址
     * @param reportUrl 报表请求地址
     */
    public void writeUreportPdf(String reportUrl,String filepath){
        BufferedInputStream bint = null;
        BufferedOutputStream bout = null;
        HttpURLConnection connection = null;
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            //获取服务端口
            int portStr = request.getServerPort();
            String url = "http://localhost:" + portStr + reportUrl;
            URL urlhttp = new URL(url);
            connection = (HttpURLConnection) urlhttp.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type","application/pdf;charset=UTF-8");
            int responseCode = connection.getResponseCode();
            connection.connect();
            if(responseCode == HttpURLConnection.HTTP_OK){
                InputStream is = connection.getInputStream();
                bint = new BufferedInputStream(is);
                //String filePath=rootpath+ File.separator +"temp"+ File.separator +"ureport"+ File.separator +reportName+".pdf";
                File thumDir = new File(filepath).getParentFile();
                if (!thumDir.exists()) {// 创建文件夹，防止下面生成文件不成功
                    thumDir.mkdirs();
                }
                File file = new File(filepath);
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                bout = new BufferedOutputStream(fos);
                int b = 0;
                byte[] byArr = new byte[4096];
                while((b = bint.read(byArr))!=-1){
                    bout.write(byArr, 0, b);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(bint!=null){
                    bint.close();
                }
                if(bout!=null){
                    bout.close();
                }
                if(connection!=null){
                    connection.disconnect();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取指定路径的pdf文件的base64编码文件流
     * @param filepath
     * @return
     */
    public ExtMsg getFileBase64(String filepath){
        FileInputStream fin=null;
        BufferedInputStream bin=null;
        ByteArrayOutputStream baos=null;
        BufferedOutputStream bout=null;
        String res="";
        try{
            //String filepath=rootpath+ File.separator +"transdoc"+ File.separator +docid+".pdf";
            File file=new File(filepath);
            if(!file.exists()){
                return new ExtMsg(false, "文件不存在", null);
            }
            fin=new FileInputStream(file);
            bin=new BufferedInputStream(fin);
            baos=new ByteArrayOutputStream();
            bout=new BufferedOutputStream(baos);
            byte[] buffer=new byte[1024];
            int len=bin.read(buffer);
            while(len!=-1){
                bout.write(buffer,0,len);
                len=bin.read(buffer);
            }
            //读取完毕
            bout.flush();
            byte[] bytes=baos.toByteArray();
            BASE64Encoder encoder = new BASE64Encoder();
            res= encoder.encode(bytes);
        }catch(Exception e){
            e.printStackTrace();
            return new ExtMsg(false, "获取失败", null);
        }finally {
            try{
                if(fin!=null){fin.close();}
                if(bin!=null){bin.close();}
                if(bout!=null){bout.close();}
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return new ExtMsg(true, "获取成功", res);

    }

    /**
     *统计项更新
     * @param entryidData
     * @return
     */
    public String  statisticUpdate(String[] entryidData){
        String info="操作完成!";
        List<Tb_entry_index_capture> list=entryIndexCaptureRepository.findByEntryidIn(entryidData);
        //数据节点ID和档号分隔符的值对,例:"4028e681636cd73101636d3e80e41b69","-"
        Map<String,String> nodeid_splitcode_map=new Hashtable<>();
        String splitCode="-";

        for(Tb_entry_index_capture entry:list) {
            String archivecode = entry.getArchivecode();
            String nodeid = entry.getNodeid();
            //未缓存档号分隔符时去获取
            if(!nodeid_splitcode_map.containsKey(nodeid)) {
                //获取当前记录的档号设置
                List<Tb_codeset> codesetList= codesettingService.findCodesetByDatanodeid(nodeid);
                if (codesetList.size() > 0) {
                    nodeid_splitcode_map.put(nodeid,((Tb_codeset)codesetList.get(0)).getSplitcode());
                }
            }
            splitCode=nodeid_splitcode_map.get(nodeid).toString();
            if("".equals(splitCode)){
                info+="档号设置未找到!"+entry.getArchivecode()+"\r";
                splitCode="-";
            }
            int innerfileNumber=entryIndexCaptureRepository.countInnerfileNumberByArchivecode(archivecode+splitCode);
//            Object object = entryIndexCaptureRepository.sumInnerfilePagesByArchivecode(archivecode+splitCode);
            Query query = entityManager.createNativeQuery("select sum("
                    + DBCompatible.getInstance().findExpressionOfToNumber("pages")
                    + ") from tb_entry_index_capture  where archivecode like '"+ (archivecode + splitCode) +"%'");
            Object object = query.getSingleResult();
            int innerfilePageNumber = object == null ? 0 : Double.valueOf(String.valueOf(object)).intValue();
            info+="档号："+entry.getArchivecode()+"\r"+"卷内文件数修改为："+innerfileNumber+" 条\t"+"卷内总页数修改为："+innerfilePageNumber;
            logger.info("==============="+info);
            //更新案卷记录
            entry.setPages(String.valueOf(innerfilePageNumber));
            Tb_entry_detail_capture entryDetail=entryDetailCaptureRepository.findByEntryid(entry.getEntryid());
            entryDetail.setF02(String.valueOf(innerfileNumber));
            entryIndexCaptureRepository.save(entry);
            entryDetailCaptureRepository.save(entryDetail);
        }
        return info;
    }

    private String convertSeqCode(String seqCode,String patchStr,int strLength){
        int seqCodeLength=seqCode.length();
        for(int i=seqCodeLength;i< strLength;i++)
        {
            seqCode=patchStr+seqCode;
        }
        return seqCode;
    }

    public ExtMsg reTransforDocs(String[] docidData){
        Map<String,List<String>> mapAlreadyTransforEntryidList =new HashMap<>();
        Map<String,List<String>> mapStatusforEntryidList =new HashMap<>();//entryID与stutas对应map
        mapAlreadyTransforEntryidList.put("archivecode",new ArrayList<>());
        mapAlreadyTransforEntryidList.put("title",new ArrayList<>());
        List<String> entryIdList = new ArrayList<>();//管理id集合
        List<String> captureIdList = new ArrayList<>();//采集id集合
        for(String docid:docidData){//遍历所选单据
            List<Tb_transdoc_entry> transdoc_entries = transdocEntryRepository.findByDocid(docid);
            for(Tb_transdoc_entry tb_transdoc:transdoc_entries){
                if(mapStatusforEntryidList.containsKey(tb_transdoc.getEntryid())){
                    mapStatusforEntryidList.get(tb_transdoc.getEntryid()).add(tb_transdoc.getStatus());
                }else{
                    mapStatusforEntryidList.put(tb_transdoc.getEntryid(), Arrays.asList(tb_transdoc.getStatus()));
                }
            }

            for(int i=0;i<transdoc_entries.size();i++){//遍历单据中的条目
                //已退回的单据所关联的条目的移交状态可以中间表中查询到,可能有多个单据关联一个条目（旧单据被退回，新单据添加旧单据中的条目并移交）
                List<String> statusList = mapStatusforEntryidList.get(transdoc_entries.get(i).getEntryid());
                //若条目的移交状态含“待审核”或“已入库”，则表明该条目在退回后被另行移交过了
                if(statusList.contains(Tb_transdoc_entry.STATUS_AUDIT)){//条目已移交，但未入库
                      captureIdList.add(transdoc_entries.get(i).getEntryid());
                }
                if(statusList.contains(Tb_transdoc_entry.STATUS_MOVE)){//条目已移交并入库
                    entryIdList.add(transdoc_entries.get(i).getEntryid());
                }
            }
        }

        generateReminderMsg(mapAlreadyTransforEntryidList,captureIdList,"capture");
        generateReminderMsg(mapAlreadyTransforEntryidList,entryIdList,"entry");

        if(mapAlreadyTransforEntryidList.get("archivecode").size()>0||mapAlreadyTransforEntryidList.get("title").size()>0){//单据中包含已移交过的条目
            return new ExtMsg(false,generateJointReminderMsg(mapAlreadyTransforEntryidList),mapAlreadyTransforEntryidList.size());
        }
        Integer value = 0;
        for(String docid : docidData){//执行移交操作（单据中未包含已移交过的条目）
            value += reTransfor(docid);
        }
        Integer num = docidData.length - value;
        if (num <= 0) {
        	return new ExtMsg(false, "当前单据存在条目缺失情况，无法重新移交！", null);
        }
        return new ExtMsg(true, num + "条数据重新移交成功！" ,null);
    }

    public int reTransfor(String transdocid){
    	List<String> entryid = transdocEntryRepository.findEntryidByDocid(transdocid);
    	if (entryid.size() > 0) {
    		List<String[]> subAry = new InformService().subArray(entryid.toArray(new String[entryid.size()]), 1000);// 处理ORACLE1000参数问题
    		Integer value = 0;
    		for (String[] ary : subAry) {
    			value += entryIndexCaptureRepository.findByEntryidIn(ary).size();
    		}
    		if (value == entryid.size()) {
                //重新提交，启动审批流程
                long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
                Tb_transdoc transdoc = transdocRepository.findOne(transdocid);
                String transfercode = transdoc.getTransfercode();
                List<Tb_flows> flowList = flowsRepository.findByMsgid(transdoc.getTransfercode());
                String taskid = flowList.get(0).getTaskid();
                Tb_task task1 = taskRepository.findByTaskid(taskid);

                Tb_task task = new Tb_task();
                task.setLoginname(task1.getLoginname());
                task.setState(Tb_task.STATE_WAIT_HANDLE);// 待处理
                task.setText(transdoc.getTransuser() + " " + new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss").format(new Date())
                        + " 重新移交 "+transdoc.getTranscount()+" 份档案！");
                task.setType("采集移交审核");
                task.setTime(new Date());
                task = taskRepository.save(task);// 添加任务

                Tb_node node = nodeRepository.getNode("采集移交审核");
                Tb_flows flows = new Tb_flows();
                flows.setText("启动");
                flows.setState(Tb_flows.STATE_FINISHED);// 完成
                flows.setTaskid(task.getId());
                flows.setMsgid(transfercode);
                flows.setDate(dateInt);
                flows.setNodeid(node.getId());
                flowsRepository.save(flows);// 添加启动流程实例

                Tb_flows flows1 = new Tb_flows();
                flows1.setText(node.getText());
                flows1.setState(Tb_flows.STATE_HANDLE);// 处理中
                flows1.setDate(dateInt);
                flows1.setTaskid(task.getId());
                flows1.setMsgid(transfercode);
                flows1.setSpman(task.getLoginname());
                flows1.setNodeid(node.getId());

                flowsRepository.save(flows1);// 添加下一流程实例
                transdoc.setState(Tb_transdoc.STATE_TRANSFOR);  //设置单据状态为“已移交”
                transdocRepository.save(transdoc);
                transdocEntryRepository.changeStatusByDocid(transdocid,Tb_transdoc_entry.STATUS_AUDIT);//设置条目状态为“待审核”
                return 0;
    		}
    	}
    	return 1;
    }

    public Tb_transdoc save(Tb_transdoc transdoc,String nodeid,String state){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        transdoc.setState(state);//设置单据状态为“已移交”
        transdoc.setNodeid(nodeid);
        transdoc.setApprovemanid(userDetails.getUserid());  //设置提交人id
        return transdocRepository.save(transdoc);
    }

    public Page<Tb_transdoc> findTransdocBySearch(int page, int limit, String condition, String operator, String content, String nodeid, Sort sort){
        Specification<Tb_transdoc> searchNodeidCondition = getSearchNodeidCondition(new String[]{nodeid});
        Specifications specifications = Specifications.where(searchNodeidCondition);
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort(Sort.Direction.DESC,"transdate"):sort);
        SecurityUser userDetails=(SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //移交签章用户可以看到所有采集移交数据
        int size=userNodeRepository.findCountByName("采集移交审核");
        List<String> userids=new ArrayList<>();
        if(size>1){
            userids=userNodeRepository.findAllUserids("采集移交审核",size-1);
        }
        if(userids.size()>0&&userids.contains(userDetails.getUserid())){
            return transdocRepository.findAll(specifications,pageRequest);
        }else{
            Specification<Tb_transdoc> transuserCondition = getSearchTransuserCondition(userDetails.getRealname());
            return transdocRepository.findAll(specifications.and(transuserCondition),pageRequest);
        }
    }

    public Page<Tb_entry_index> findDocEntryindexBySearch(int page, int limit, String condition, String operator, String content, String[] entryidData, Sort sort){
        Specification<Tb_entry_index> searchEntryidsCondition = EntryIndexService.getSearchEntryidsCondition(entryidData);
        Specifications sp = Specifications.where(searchEntryidsCondition);
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort("archivecode") : sort);
        return getFullName(entryIndexRepository.findAll(sp, pageRequest), pageRequest);
    }

    //获取数据节点全名
    private Page<Tb_entry_index> getFullName(Page<Tb_entry_index> result,PageRequest pageRequest) {
        List<Tb_entry_index> content = result.getContent();
        long totalElements = result.getTotalElements();
        List<Tb_entry_index> returnResult = new ArrayList<>();
        for(Tb_entry_index entryIndex:content){
            Tb_entry_index entry_index = new Tb_entry_index();
            BeanUtils.copyProperties(entryIndex,entry_index);
            if(entry_index.getTdn()!=null){
                String nodeid = entry_index.getTdn().getNodeid();
                String nodefullname = nodesettingService.getNodefullnameLoop(nodeid,"_","");
                entry_index.setNodefullname(nodefullname);
                returnResult.add(entry_index);
            }
        }
        return new PageImpl(returnResult,pageRequest,totalElements);
    }

    public Page<Tb_entry_index_capture> findDocEntryindexcaptureBySearch(int page, int limit, String condition, String operator, String content, String[] entryidData, Sort sort){
        Specification<Tb_entry_index_capture> searchEntryidsCondition = EntryCaptureService.getSearchEntryidsCondition(entryidData);
        Specifications sp = Specifications.where(searchEntryidsCondition);
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort("archivecode") : sort);
        return entryIndexCaptureRepository.findAll(sp, pageRequest);
    }

    public void setGenerateReminderMsg(Map<String,List<String>> mapAlreadyTransforEntryidList,List<String> entryIdList,String type){
        if("entry".equals(type)){
            List<Tb_entry_index_capture> index_captures = entryIndexCaptureRepository.findByEntryidIn(entryIdList.toArray(new String[entryIdList.size()]));
            for(Tb_entry_index_capture capture:index_captures){
                if(capture.getArchivecode()==null){
                    mapAlreadyTransforEntryidList.get("title").add(capture.getTitle());
                }else{
                    mapAlreadyTransforEntryidList.get("archivecode").add(capture.getArchivecode());
                }
            }
        }else{
            List<Tb_entry_index> indexs = entryIndexRepository.findByEntryidIn(entryIdList.toArray(new String[entryIdList.size()]));
            for(Tb_entry_index index:indexs){
                if(index.getArchivecode()==null){
                    mapAlreadyTransforEntryidList.get("title").add(index.getTitle());
                }else{
                    mapAlreadyTransforEntryidList.get("archivecode").add(index.getArchivecode());
                }
            }
        }
    }

    public  boolean generateReminderMsg(Map<String,List<String>> mapAlreadyTransforEntryidList,List<String> entryIdList,String
            type){
        try{
            if(entryIdList.size()==0){
                return false;
            }
            int pointLinit = 10;
            if(entryIdList.size()>pointLinit){
                int splitNum = entryIdList.size()/pointLinit;
                for(int i=0;i<splitNum;i++){
                    List<String> subList = entryIdList.subList(0,pointLinit);
                    String[] ids = subList.toArray(new String[subList.size()]);
                    setGenerateReminderMsg(mapAlreadyTransforEntryidList,subList,type);
                    entryIdList.subList(0, pointLinit).clear();
                }

                if(!entryIdList.isEmpty()){
                    setGenerateReminderMsg(mapAlreadyTransforEntryidList,entryIdList,type);
                }
            }else{
                setGenerateReminderMsg(mapAlreadyTransforEntryidList,entryIdList,type);
            }
        }catch (Exception e){
             e.printStackTrace();
        }
        return true;
    }

    public static void main(String[] args){
        List<String> list = new ArrayList<>();
        for(int i=0;i<5;i++){
            list.add(i+"");
        }

        //generateReminderMsg(null,list,null);
    }
    /**
    * 当数据审核打开时，创建任务和工作流,生成采集移交审核单
    *
    * @param  entryids
    * @param transdoc
    * @param nodeid
    * @param state
    * @param spman
    * @param type
    * @param isSynch
    * @return {@link Tb_transdoc}
    * @throws 
    */
    @Transactional
    public Tb_transdoc transdocFormSubmit(String[] entryids,Tb_transdoc transdoc,String nodeid,String state,String spman,String type,boolean isSynch,String approvenodeid,String volumeNodeId){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        Tb_task task = new Tb_task();
        task.setLoginname(spman);
        task.setState(Tb_task.STATE_WAIT_HANDLE);// 待处理
        task.setText(transdoc.getTransuser() + " " + new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss").format(new Date())
                + " 移交 "+transdoc.getTranscount()+" 份档案！");
        task.setType("采集移交审核");
        task.setTime(new Date());
        task = taskRepository.save(task);// 添加任务
        Tb_user spmanUser = userRepository.findByUserid(spman);//根据审核人ID查找审核人
        String transfercode = UUID.randomUUID().toString().replace("-", "");// 表单号用uuid生成
        transdoc.setTransfercode(transfercode);
        transdoc.setApproveman(spmanUser.getRealname());
        transdoc.setState("待审核");
        transdoc.setNodeid(nodeid);
        transdoc.setApprovemanid(userDetails.getUserid());
        transdoc.setVolumenodeid(volumeNodeId);
        Tb_node node = nodeRepository.findByNodeid(approvenodeid);
        Tb_flows flows = new Tb_flows();
        flows.setText("启动");
        flows.setState(Tb_flows.STATE_FINISHED);// 完成
        flows.setTaskid(task.getId());
        flows.setMsgid(transfercode);
        flows.setDate(dateInt);
        flows.setNodeid(node.getId());
        flowsRepository.save(flows);// 添加启动流程实例
        Tb_flows flows1 = new Tb_flows();
        flows1.setText(node.getText());
        flows1.setState(Tb_flows.STATE_HANDLE);// 处理中
        flows1.setDate(dateInt);
        flows1.setTaskid(task.getId());
        flows1.setMsgid(transfercode);
        flows1.setSpman(spman);
        flows1.setNodeid(node.getId());
        flowsRepository.save(flows1);// 添加下一流程实例
        Tb_transdoc tb_transdoc=transdocRepository.save(transdoc);// 添加移交单据
        webSocketService.noticeRefresh();
        return tb_transdoc;
    }

    /**
    * 生成案卷卷内的采集移交审核单
    *
    * @param transdoc
    * @param tb_transdoc
    * @param spman
    * @return
    * @throws
    */
    @Transactional
    public void transinnerdoc(String[] entryids,Tb_transdoc transdoc,Tb_transdoc tb_transdoc,String spman,String approvenodeid){
        List<Tb_entry_index_capture> list;
        String temptext = "(同步卷内)" + tb_transdoc.getTransdesc();
        String temptransdocid="";
        String tempnodeid="";
        int count=0;
        for (int i = 0; i < entryids.length; i++) {
            list = entryIndexCaptureService.findInnerByArchivecodeLike(entryids[i]);//根据档号得到所有的卷内
            if(list.size()>0){
                temptransdocid += list.stream().filter(tb -> !tb.getEntryid().isEmpty()).map(Tb_entry_index_capture::getEntryid).collect(Collectors.joining(","));
                temptransdocid+=",";
                tempnodeid= list.get(0).getNodeid();
                count+=list.size();
            }
        }
        if(temptransdocid!=""){
            transdoc.setNodeid(tempnodeid);
            transdoc.setDocid(temptransdocid);
            transdoc.setTranscount(Long.valueOf(count));
            transdoc.setTransdesc(temptext);
            String[] innerentryids=temptransdocid.split(",");
            Tb_transdoc temp = acquisitionService.transdocFormSubmit(innerentryids,transdoc, tempnodeid, Tb_transdoc.STATE_TRANSFOR, spman,"卷内档案",false,approvenodeid,"");
            modifyEntry(temptransdocid, null, temp.getDocid());
        }
    }

    /**
    * 生成审批单据与采集条目联系的记录
    *
    * @param entryids
    * @param innserids
    * @param transdocid
    * @return {@link ExtMsg}
    * @throws
    */
    @Transactional
    public ExtMsg modifyEntry(String entryids, String innserids, String transdocid) {
        String[] entryidData = entryids.split(",");// 1.移交所选数据
        int num = acquisitionService.transfor(entryidData, transdocid, Tb_transdoc_entry.STATUS_AUDIT,new ArrayList<>());
//        delTransWriteLog(entryidData, "数据采集", "数据移交");// 写日志
        if (num > 0) {
            return new ExtMsg(true, "移交成功", num);
        }
        return new ExtMsg(false, "移交失败", null);
    }

    /**
    * 把在Controller写的采集条目移交放到service中可以复用和回滚
    *
    * @param entryids
    * @param transdoc
    * @param nodeid
    * @param isSynch
    * @return {@link ExtMsg}
    * @throws
    */
    @Transactional
    public ExtMsg transforAllEntry(String[] entryids,Tb_transdoc transdoc,String nodeid,boolean isSynch,String taskid,List<String> jnEntryIds){
        List<Tb_entry_index_capture> captures=new ArrayList<>();
        if(entryids.length>1000) {//大于1000的数据需要分批查询
            int quotient = entryids.length / 1000;
            for (int i = 0; i <= quotient; i++) {
                int idsLength = (i + 1) * 1000 > entryids.length ? entryids.length -i * 1000 : 1000;//判断是否够1000
                String[] idsArr = new String[idsLength];
                System.arraycopy(entryids, i * 1000, idsArr, 0, idsLength);
                captures.addAll(entryIndexCaptureRepository.findByEntryidIn(idsArr));
            }
        }else {
            captures = entryIndexCaptureRepository.findByEntryidIn(entryids);
        }
        String repeact = "";
        String innerRepeact = "";
        List<String> innerEntryids = new ArrayList<>();
        for (int i = 0; i < captures.size(); i++) {
            if (!StringUtils.isEmpty(captures.get(i).getArchivecode())) {
                List<Tb_entry_index> entry_index = entryIndexRepository.findByArchivecode(captures.get(i).getArchivecode());
                if (entry_index.size() > 0) {
                    if (i < captures.size() - 1) {
                        repeact += captures.get(i).getArchivecode() + "、";
                    } else {
                        repeact += captures.get(i).getArchivecode();
                    }
                }
            }
        }
        List<Tb_entry_index_capture> tb_index_detail_captures = entryIndexCaptureRepository.findByEntryidIn(entryids);
        for(int j =0;j<tb_index_detail_captures.size();j++){
            List<Tb_entry_index> entry_index = entryIndexRepository.findByArchivecode(tb_index_detail_captures.get(j).getArchivecode());
            if (entry_index.size() > 0) {
                if (j < captures.size() - 1) {
                    innerRepeact += tb_index_detail_captures.get(j).getArchivecode() + "、";
                } else {
                    innerRepeact += tb_index_detail_captures.get(j).getArchivecode();
                }
            }else{
                innerEntryids.add(tb_index_detail_captures.get(j).getEntryid());
            }
        }
        Tb_data_node node = entryIndexService.getNodeLevel(nodeid);
        //如果重复档号值不为空且非未归管理,那么就判断档号重复
        if (!repeact.equals("") && node != null && !node.getNodename().equals("未归管理")) {
            return new ExtMsg(false, "档号记录重复", "档号记录:"+repeact+"重复");
        }
        //卷内文件档号是否重复
        if (!innerRepeact.equals("") && node != null && !node.getNodename().equals("未归管理")) {
            return new ExtMsg(false, "卷内文件档号记录重复", "卷内文件档号记录:"+innerRepeact+"重复");
        }
        Tb_transdoc doc = acquisitionService.save(transdoc, nodeid,Tb_transdoc.STATE_AUDIT);
        //acquisitionService.transfor(entryids, doc.getDocid(),Tb_transdoc_entry.STATUS_MOVE,jnEntryIds);
        String[] entryidData = auditService.getEntryidsByDocid(doc.getDocid());
        if(jnEntryIds.size()>0){   //存在移交案卷的卷内文件
            String[] innerEntryidsStr = new String[jnEntryIds.size()];
            jnEntryIds.toArray(innerEntryidsStr);
            String[] allEntryidData = ArrayUtils.addAll(entryidData,innerEntryidsStr);   //合并案卷和卷内文件条目id
            entryidData = allEntryidData;
        }
        int[] num = auditService.move(entryidData, doc.getDocid(),taskid);//审核数据没开启的提交
        webSocketService.noticeRefresh(); //刷新入库成功提醒申请人
        if (num[0] > 0 && num[1] > 0 && num[2] > 0) {
            //需要改变状态为已审核，方便前端判断是否要再次请求移交关联的Tb_transdoc_entry的条目
            return new ExtMsg(true, "移交成功", doc);
        }
        return new ExtMsg(false, "移交异常", null);
    }

    public String generateJointReminderMsg(Map<String,List<String>> mapAlreadyTransforEntryidList){
        StringBuilder sb = new StringBuilder();
        int size = mapAlreadyTransforEntryidList.get("archivecode").size()+mapAlreadyTransforEntryidList.get("title")
                .size();
        sb.append("重新移交失败！共有"+size+"条记录在退回后已被另行移交。</br>");
        if(mapAlreadyTransforEntryidList.get("archivecode").size()>0){
            sb.append("记录的档号为：");
            for(int i=0;i<mapAlreadyTransforEntryidList.get("archivecode").size();i++){
               if(i==mapAlreadyTransforEntryidList.get("archivecode").size()-1){
                   sb.append("\""+mapAlreadyTransforEntryidList.get("archivecode").get(i)+"\"</br>");
               }else{
                   sb.append("\""+mapAlreadyTransforEntryidList.get("archivecode").get(i)+"\"、");
               }
            }
        }
        if(mapAlreadyTransforEntryidList.get("title").size()>0){
            sb.append("记录的题名为：");
            for(int i=0;i<mapAlreadyTransforEntryidList.get("title").size();i++){
                if(i==mapAlreadyTransforEntryidList.get("title").size()-1){
                    sb.append("\""+mapAlreadyTransforEntryidList.get("title").get(i)+"\"");
                }else{
                    sb.append("\""+mapAlreadyTransforEntryidList.get("title").get(i)+"\"、");
                }
            }
        }
        return sb.toString();
    }

    public static Specification<Tb_transdoc> getSearchNodeidCondition(String[] nodeids){
        Specification<Tb_transdoc> searchNodeID = null;
        if(nodeids!=null){
            if(nodeids.length>0){
                searchNodeID = new Specification<Tb_transdoc>() {
                    @Override
                    public Predicate toPredicate(Root<Tb_transdoc> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                        Predicate[] predicates = new Predicate[nodeids.length];
                        for(int i=0;i<nodeids.length;i++){
                            predicates[i] = criteriaBuilder.equal(root.get("nodeid"),nodeids[i]);
                        }
                        return criteriaBuilder.or(predicates);
                    }
                };
            }
        }
        return searchNodeID;
    }

    public static Specification<Tb_transdoc> getSearchTransuserCondition(String transuser){
        Specification<Tb_transdoc> searchTransuserCondition = null;
        searchTransuserCondition = new Specification<Tb_transdoc>() {
            @Override
            public Predicate toPredicate(Root<Tb_transdoc> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get("transuser"),transuser);
                return criteriaBuilder.or(p);
            }
        };
        return searchTransuserCondition;
    }

    public static Specification<Tb_transdoc> getSearchDocidCondition(String docid){
        Specification<Tb_transdoc> searchDocidCondition = null;
        searchDocidCondition = new Specification<Tb_transdoc>() {
            @Override
            public Predicate toPredicate(Root<Tb_transdoc> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get("docid"),docid);
                return criteriaBuilder.or(p);
            }
        };
        return searchDocidCondition;
    }

    public Tb_transdoc transdocFormSubmit(Tb_transdoc transdoc,String nodeid,String state,String spman){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        String[] entryids = transdoc.getDocid().split(",");
        Tb_task task = new Tb_task();
        task.setLoginname(spman);
        task.setState(Tb_task.STATE_WAIT_HANDLE);// 待处理
        task.setText(transdoc.getTransuser() + " " + new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss").format(new Date())
                + " 移交 "+entryids.length+" 份档案！");
        task.setType("采集移交审核");
        task.setTime(new Date());
        task = taskRepository.save(task);// 添加任务

        String transfercode = UUID.randomUUID().toString().replace("-", "");// 表单号用uuid生成
        transdoc.setTransfercode(transfercode);
        transdoc.setState("待审核");
        transdoc.setNodeid(nodeid);
        transdoc.setApprovemanid(userDetails.getUserid());

        Tb_node node = nodeRepository.getNode("采集移交审核");
        Tb_flows flows = new Tb_flows();
        flows.setText("启动");
        flows.setState(Tb_flows.STATE_FINISHED);// 完成
        flows.setTaskid(task.getId());
        flows.setMsgid(transfercode);
        flows.setDate(dateInt);
        flows.setNodeid(node.getId());
        flowsRepository.save(flows);// 添加启动流程实例

        Tb_flows flows1 = new Tb_flows();
        flows1.setText(node.getText());
        flows1.setState(Tb_flows.STATE_HANDLE);// 处理中
        flows1.setDate(dateInt);
        flows1.setTaskid(task.getId());
        flows1.setMsgid(transfercode);
        flows1.setSpman(spman);
        flows1.setNodeid(node.getId());

        flowsRepository.save(flows1);// 添加下一流程实例
        return transdocRepository.save(transdoc);// 添加移交单据
    }
}