package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.DelThread;
import com.wisdom.util.FunctionUtil;
import com.wisdom.util.LogAnnotation;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.*;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * 数据审核控制器
 * Created by Rong on 2017/10/24.
 */
@Controller
@RequestMapping(value = "/audit")
public class AuditController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    EntryCaptureService entryCaptureService;

    @Autowired
    EntryIndexCaptureService entryIndexCaptureService;

    @Autowired
    AuditService auditService;
    
    @Autowired
    EntryIndexCaptureRepository entryIndexCaptureRepository;

    @Autowired
    EntryIndexRepository entryIndexRepository;
    
    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    EntryService entryService;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    PublicUtilService publicUtilService;

    @Autowired
    TransdocRepository transdocRepository;

    @Autowired
    ElectronApproveService electronApproveService;

    @Autowired
    FlowsRepository flowsRepository;

    @Autowired
    UserNodeRepository userNodeRepository;

    @Autowired
    AcquisitionController acquisitionController;
    @Autowired
    private NodesettingService nodesettingService;

    @Value("${system.loginType}")
    private String systemLoginType;//登录系统设置  政务网1  局域网0

    @Value("${CA.netcat.use}")
    private String netcatUse;//是否使用网证通电子签章  1使用  0禁用

    @Value("${system.report.server}")
    private String reportServer;//报表服务

    @RequestMapping("/main")
    public String audit(Model model,String isp) {
        Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
        SecurityUser userDetails=(SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("functionButton",functionButton);
        model.addAttribute("reportServer",reportServer);
        model.addAttribute("userid", userDetails.getUserid());
        model.addAttribute("userRealname",userDetails.getRealname());
        model.addAttribute("caUserid", userDetails.getNickname());
        model.addAttribute("systemLoginType",systemLoginType);
        model.addAttribute("netcatUse",netcatUse);
        return "/inlet/audit";
    }

    //数据审核办理
    @RequestMapping("/mainDeal")
    public String auditDeal(Model model,String flag,String taskid,String type) {
        List<Tb_flows> flows = electronApproveService.getAuditFlowsInfo(taskid, type);
//        if (flows.size() > 1) {
//            model.addAttribute("info","电子借阅");
//            return "/inlet/missionAdmins";
//        }else{
//            flows=flowsRepository.findByTaskid(taskid);
//        }
        if("1".equals(netcatUse)){//有设置采集移交的数字签章
            int size=userNodeRepository.findCountByName("采集移交审核");
            model.addAttribute("captureAuditSize",size);
        }
        model.addAttribute("taskid", taskid);
        model.addAttribute("type", type);
        model.addAttribute("flag", flag);
        model.addAttribute("nodeId",flows.get(0).getNodeid());
        model.addAttribute("systemLoginType",systemLoginType);
        model.addAttribute("netcatUse",netcatUse);
        return "/inlet/transforAuditDeal";
    }

    /**
     *  获取与单据相关联的详细条目
     * @param nodeid
     * @param condition
     * @param docid
     * @param operator
     * @param content
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping(value = "/entries", method = RequestMethod.GET)
    @ResponseBody
    public Page getEntries(String nodeid, String condition, String docid,String state, String operator, String content, int page, int limit, String sort,String parententryid) {
        Sort sortobj = WebSort.getSortByJson(sort);
        if("待审核".equals(state)||"已退回".equals(state)){
            return entryCaptureService.getEntries(this.getClass().getSimpleName(), nodeid, state, docid, condition, operator, content, page, limit, sortobj,parententryid);
        }else{
            return auditService.getEntries( nodeid,docid, condition, operator, content, page, limit, sortobj,parententryid);
        }
    }

    @RequestMapping(value = "/entries/{entryid}/{type}", method = RequestMethod.GET)
    @ResponseBody
    public EntryCapture getEntry(@PathVariable String entryid,@PathVariable String type){
        if("完成".equals(type)){
            Entry entry= entryService.getEntry(entryid);
            EntryCapture entryCapture=new EntryCapture();
            BeanUtils.copyProperties(entry,entryCapture);
            return entryCapture;
        }
        return entryCaptureService.getEntry(entryid);
    }

    /**
     *  获取卷内文件（数据审核模块暂已取消该功能）
     * @param archivecode
     * @param nodeid
     */
    @RequestMapping(value = "/entries/innerfile/{archivecode}/", method = RequestMethod.GET)
    @ResponseBody
    public Page<Tb_entry_index_capture> getEntryInnerFile(@PathVariable String archivecode,String nodeid,Integer
            page, Integer start,Integer limit,String sort){
        logger.info("nodeid:"+nodeid+";page:" + page + ";start:" + start + ";limt:" + limit);
        Sort sortobj = WebSort.getSortByJson(sort);
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        List list = entryIndexCaptureService.findAllByNodeidAndArchivecodeLike(start,limit,nodeid,archivecode,sortobj);
        return new PageImpl((List<Tb_entry_index_capture>)list.get(1),pageRequest,(int)list.get(0));
    }

    /**
     *  获取移交单据
     */
    @RequestMapping("/getDoc")
    @ResponseBody
    public Page<Tb_transdoc> getDoc(String nodeid, String condition,String operator,String content,int page, int limit, String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        Page<Tb_transdoc> transdocPage=null;
        if(!"".equals(nodeid)){
            transdocPage = auditService.findBySearch(page,limit,condition,operator,content,nodeid,sortobj);
        }
        return transdocPage;
    }

    /**
    *  获得移交单据
    *
    * @param state
    * @param taskid
    * @param condition
    * @param operator
    * @param content
    * @param page
    * @param limit
    * @param sort
    * @return {@link Page< TransdocVO>}
    * @throws
    **/
    @RequestMapping("/getDocByState")
    @ResponseBody
    public Page<TransdocVO> getDocByState(String state,String taskid, String condition, String operator, String content, int page, int limit, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        Page<TransdocVO> transdocPage = null;
        transdocPage = auditService.findTransdocVoBySearch(state,taskid,page, limit, condition, operator, content,sortobj);
        return transdocPage;
    }

    @RequestMapping("/getDocByTaskId")
    @ResponseBody
    public ExtMsg getDoc(String state,String taskid) {
        Sort sortobj = WebSort.getSortByJson("");
        Page<TransdocVO> transdocPage = null;
        transdocPage = auditService.findTransdocVoBySearch(state,taskid,1, 1, null, null, null,sortobj);
        return new ExtMsg(true,"成功",transdocPage.getContent().get(0));
    }


    /**
     *
     * @param docid
     * @param nodeid
     * @param taskid
     * @param usrCertNO 用户数字证书编号
     * @return
     */
    @RequestMapping(value = "/move")
    @ResponseBody
    public ExtMsg move(String docid, String nodeid,String taskid, String nextNode, String nextSpman, String usrCertNO) {
        if("".equals(nextSpman)) {  //不存在下一环节
            String[] entryidData = auditService.getEntryidsByDocid(docid);
            List<Tb_entry_index_capture> captures = new ArrayList<>();
            if (entryidData.length > 500){
                int quotient = entryidData.length / 500;
                for(int i = 0; i <= quotient; i ++ ){
                    int dataLength = (i+1)*500>entryidData.length?entryidData.length-i*500:500;
                    String[] entryid = new String[dataLength];
                    List<Tb_entry_index_capture> capture = new ArrayList<>();
                    System.arraycopy(entryidData,i * 500,entryid,0,dataLength);
                    capture = entryIndexCaptureRepository.findByEntryidIn(entryid);
                    for (Tb_entry_index_capture j : capture){
                        captures.add(j);
                    }
                }
            }else {
                captures = entryIndexCaptureRepository.findByEntryidIn(entryidData);
            }
            String repeact = "";
            String innerRepeact = "";
            List<String> innerEntryids = new ArrayList<>();
            for (int i = 0; i < captures.size(); i++) {
                if (!"".equals(captures.get(i).getArchivecode()) && captures.get(i).getArchivecode() != null) {
                    List<Tb_entry_index> entry_index = entryIndexRepository.findByArchivecode(captures.get(i).getArchivecode());
                    if (entry_index.size() > 0) {
                        repeact += captures.get(i).getArchivecode() + "、";
                    }
                }
                //判断是否案卷移交
                if(captures.get(i).getFilecode()!=null&&!"".equals(captures.get(i).getFilecode())&&captures.get(i).getInnerfile()==null){
                    String innernodeid = publicUtilService.getNodeid(nodeid);  //卷内节点id
                    List<Tb_index_detail_capture> tb_index_detail_captures = entryIndexCaptureService.getInnerfiles(innernodeid,captures.get(i));
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
                }
            }
            if (repeact.length() > 0) {
                repeact = repeact.substring(0, repeact.length() - 1);
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
            if(innerEntryids.size()>0){   //存在移交案卷的卷内文件
                String[] innerEntryidsStr = new String[innerEntryids.size()];
                innerEntryids.toArray(innerEntryidsStr);
                String[] allEntryidData = ArrayUtils.addAll(entryidData,innerEntryidsStr);   //合并案卷和卷内文件条目id
                entryidData = allEntryidData;
            }
            int[] num = {};
            if (entryidData.length > 1000){
                boolean flag = false;
                int quotient = entryidData.length / 1000;
                for(int i = 0; i <= quotient; i ++ ){
                    int dataLength = (i+1)*1000>entryidData.length ? entryidData.length-i*1000 : 1000;
                    String[] entryid = new String[dataLength];
                    System.arraycopy(entryidData,i * 1000,entryid,0,dataLength);
                    num = auditService.move(entryid, docid,taskid);
                    webSocketService.noticeRefresh();
                    if (!(num[0] > 0 && num[1] > 0 && num[2] > 0)){
                        flag = false;
                        return new ExtMsg(false, "入库异常", null);
                    }else {
                        flag = true;
                    }
                }
                if (flag){
                    webSocketService.noticeRefresh(); //刷新入库成功提醒申请人
                    //删除条目成功后，再启用线程默默的删除关联的电子文件等
                    DelThread delThread = new DelThread(entryidData,"数据采集");// 开启线程
                    delThread.start();
                    return new ExtMsg(false, "入库成功", null);
                }
            }else {
                num = auditService.move(entryidData, docid,taskid);
            }
            webSocketService.noticeRefresh(); //刷新入库成功提醒申请人
            if (num[0] > 0 && num[1] > 0 && num[2] > 0) {
                //删除条目成功后，再启用线程默默的删除关联的电子文件等
                DelThread delThread = new DelThread(entryidData,"数据采集");// 开启线程
                delThread.start();
                acquisitionController.delTransWriteLog(entryidData,"数据审核","入库操作");//写个日志
                return new ExtMsg(true, "入库成功", num);
            }
            return new ExtMsg(false, "入库异常", null);
        }else{
            auditService.approveTransDoc(taskid,nodeid,nextNode,nextSpman,docid);
            if(usrCertNO!=null&&!"".equals(usrCertNO)){//记录移交签章的数字证书编号
                auditService.updateTransforCa("1", usrCertNO, docid);
            }
            webSocketService.noticeRefresh();
            return new ExtMsg(true, "审批完成，暂未入库", null);
        }

    }

    //更新移交单据
    @RequestMapping(value = "/updateTrandoc")
    @ResponseBody
    public void updateTrandoc(String docid ,String type,String pwdno){
        auditService.updateTrandoc(docid,type,pwdno);
    }

    @LogAnnotation(module = "数据审核",startDesc = "退回操作，单据id为：",sites = "1")
    @RequestMapping(value = "/sendback")
    @ResponseBody
    public ExtMsg sendback(String docid,String sendbackreason,String taskid) {
        int[] num = auditService.sendback(docid,sendbackreason,taskid);
        if (num[0] > 0 && num[1] > 0) {
            return new ExtMsg(true, "退回成功", num);
        }
        return new ExtMsg(false, "退回失败", null);
    }

    /**
    * 获得填写采集移交单时的审批人
    *
    * @param worktext
    * @param nodeid
    * @return {@link List< Tb_user>}
    * @throws
    */
    @RequestMapping("/getApproveMan")
    @ResponseBody
    public List<Tb_user> getApproveMan(String worktext,String nodeid) {
        return auditService.getApproveMan(worktext,nodeid);
    }

    //获取单据审批树
    @RequestMapping("/getAuditDocTree")
    @ResponseBody
    public List<ExtNcTree> getAuditDocTree(){
        List<ExtNcTree> trees = new ArrayList<>();
        ExtNcTree tree = new ExtNcTree();
        tree.setFnid("1");
        tree.setLeaf(true);
        tree.setText("待审核");
        trees.add(tree);

        tree = new ExtNcTree();
        tree.setFnid("2");
        tree.setLeaf(true);
        tree.setText("已审核（入库）");
        trees.add(tree);

        tree = new ExtNcTree();
        tree.setFnid("3");
        tree.setLeaf(true);
        tree.setText("已审核（退回）");
        trees.add(tree);

        return trees;
    }

    //获取审核单据
    @RequestMapping(value = "/getAuditDoc")
    @ResponseBody
    public ExtMsg getAuditDoc(String docid) {
        return new ExtMsg(true, "", transdocRepository.findOne(docid));
    }

    //获取任务id
    @RequestMapping(value = "/getTaskid")
    @ResponseBody
    public String getTaskid(String docid) {
        return auditService.getTaskid(docid);
    }

    @RequestMapping(value = "/getNodeFullName")
    @ResponseBody
    public String getNodeFullName(String nodeid){
        return nodesettingService.getNodefullnameLoop(nodeid.trim(),"_","");
    }
}
