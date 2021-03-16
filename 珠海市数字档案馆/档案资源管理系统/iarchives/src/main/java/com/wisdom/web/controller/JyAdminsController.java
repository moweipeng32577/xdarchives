package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.FunctionUtil;
import com.wisdom.util.LogAnnotation;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.BorrowDocRepository;
import com.wisdom.web.repository.ReserveRepository;
import com.wisdom.web.repository.ShowroomRepository;
import com.wisdom.web.repository.UserRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.security.SlmRuntimeEasy;
import com.wisdom.web.service.EntryCaptureService;
import com.wisdom.web.service.JyAdminsService;
import com.wisdom.web.service.TaskService;
import com.wisdom.web.service.WorkflowService;
import com.xdtech.smsclient.SMSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 申请管理控制器
 * Created by Rong on 2017/10/24.
 */
@Controller
@RequestMapping(value = "/jyAdmins")
public class JyAdminsController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    JyAdminsService jyAdminsService;

    @Autowired
    SlmRuntimeEasy slmRuntimeEasy;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    BorrowDocRepository borrowDocRepository;

    @Autowired
    EntryCaptureService entryCaptureService;

    @Autowired
    ShowroomRepository showroomRepository;

    @Autowired
    ReserveRepository reserveRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SMSService smsService;

    @Autowired
    TaskService taskService;

    @Autowired
    WorkflowService workflowService;

    @Value("${system.report.server}")
    private String reportServer;//报表服务

    @Value("${workflow.dzborrow.approve.workid}")
    private String dzBorrowWorkId;//电子查档审批节点编号

    @Value("${workflow.stborrow.approve.workid}")
    private String stBorrowWorkId;//电子查档审批节点编号

    @Value("${workflow.dzPrint.approve.workid}")
    private String dyPrintWorkId;//电子查档审批节点编号

    @RequestMapping("/main")
    public String acquisition(Model model,String flag) {
        model.addAttribute("iflag",flag);
        model.addAttribute("reportServer",reportServer);
        return "/inlet/jyAdmins";
    }

    // 解决利用平台与管理平台公用页面权限控制问题
    @RequestMapping("/mainly")
    public String acquisitionly(Model model, String flag) {
        model.addAttribute("iflag", flag);
        model.addAttribute("reportServer",reportServer);
        return "/inlet/jyAdmins";
    }

    @RequestMapping("/ghmain")
    public String restitutionAdmins(Model model,String taskid,String borrowmsgid) {
        model.addAttribute("taskid",taskid);
        model.addAttribute("borrowmsgid",borrowmsgid);
        model.addAttribute("reportServer",reportServer);
        return "/inlet/restitutionAdmins";
    }

    @RequestMapping("/ghmainborrow")
    public String BorrowAdmins(Model model,String taskid,String borrowmsgid) {
        model.addAttribute("taskid",taskid);
        model.addAttribute("borrowmsgid",borrowmsgid);
        model.addAttribute("borrowflag","1");
        model.addAttribute("reportServer",reportServer);
        return "/inlet/restitutionAdmins";
    }

    //预约管理
    @RequestMapping("/yymain")
    public String Yymain(Model model,String isp) {
        Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
        model.addAttribute("functionButton", functionButton);
        return "/inlet/reservation";
    }

    //获取节点的权限按钮
    @RequestMapping("/getFunction")
    @ResponseBody
    public ExtMsg getFunction(String isp) {
        List functionButton = FunctionUtil.getQxFunction(isp);
        return new ExtMsg(true,"",functionButton);
    }

    @RequestMapping("/yymainly")
    public String Yymainly(Model model, String flag,String taskid,String isp) {
        Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
        model.addAttribute("functionButton", functionButton);
        model.addAttribute("iflag", flag);
        model.addAttribute("taskid", taskid);
        if(taskid!=null){
            Tb_reserve tb_reserve =  reserveRepository.findByBorrowmig(taskid);
            model.addAttribute("yytype", tb_reserve.getLymode());
        }
        return "/inlet/reservation";
    }

    //审批管理
    @RequestMapping("/approvemain")
    public String approvemain(Model model,String flag) {
        model.addAttribute("iflag",flag);
        model.addAttribute("reportServer",reportServer);
        return "/inlet/jyAdmins";
    }

    //审批管理-利用
    @RequestMapping("/approvemainly")
    public String approvemainly(Model model,String flag) {
        model.addAttribute("iflag",flag);
        model.addAttribute("reportServer",reportServer);
        return "/inlet/jyAdmins";
    }

    @RequestMapping("/getJyState")
    @ResponseBody
    public List<ExtNcTree> getJyState(){
        List<ExtNcTree> trees = new ArrayList<>();
        ExtNcTree tree = new ExtNcTree();
        tree.setFnid("1");
        tree.setLeaf(true);
        tree.setText("已送审");
        trees.add(tree);

        tree = new ExtNcTree();
        tree.setFnid("2");
        tree.setLeaf(true);
        tree.setText("已通过");
        trees.add(tree);

        tree = new ExtNcTree();
        tree.setFnid("5");
        tree.setLeaf(true);
        tree.setText("查无此档");
        trees.add(tree);

        tree = new ExtNcTree();
        tree.setFnid("3");
        tree.setLeaf(true);
        tree.setText("不通过");
        trees.add(tree);

        tree = new ExtNcTree();
        tree.setFnid("4");
        tree.setLeaf(true);
        tree.setText("退回");
        trees.add(tree);

        return trees;
    }

    //根据id查询审批节点
    @RequestMapping("/findByWorkId")
    @ResponseBody
    public ExtMsg findByWorkId(String workType) {
        String workId;
        if("实体查档".equals(workType)){
            workId=stBorrowWorkId;
        }else if("电子查档".equals(workType)) {
            workId=dzBorrowWorkId;
        }else{
            workId=dyPrintWorkId;
        }
        Tb_work work= workflowService.findByWorkid(workId);
        return new ExtMsg(true,"",work);
    }

    //手动催办
    @RequestMapping("/manualUrging")
    @ResponseBody
    public ExtMsg manualUrging(String borrowcode,String sendMsg){
        if(borrowcode==null){
            return new ExtMsg(false, "催办失败", null);
        }
        Tb_flows billApproval= taskService.manualUrging(borrowcode);
        String returnStr = "";
        if(billApproval!=null) {
            Tb_user spuser = userRepository.findByUserid(billApproval.getSpman());
            if(sendMsg!=null&&"true".equals(sendMsg)&&spuser!=null) {
                try {
                    returnStr = smsService.SendSMS(spuser.getPhone(), "您有一条利用平台的审批，请登录档案系统管理平台及时处理！");
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ExtMsg(true, "已催办，短信发送失败", null);
                }
            }
            if ("".equals(returnStr)) {
                return new ExtMsg(true, "已催办", null);
            } else {
                return new ExtMsg(true, "已催办，短信发送结果为：" + returnStr, null);
            }
        }
        return new ExtMsg(true, "催办失败", null);
    }

    @RequestMapping("/getBorrowdoc")
    @ResponseBody
    public Page<Tb_borrowdoc> getBorrowdoc(String flag,String condition,String operator,String content,String state,String type,int page,int start,int limit,String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        logger.info("page:" + page + ",start:" + start + ",limt:" + limit);
        if("3".equals(flag)){//审批管理
            return jyAdminsService.getBorrowDocsBySelfApprove(flag,state,type,page,limit,sortobj);
        }else{
            return jyAdminsService.getBorrowDocs(flag,condition,operator,content,state,type,page,limit,sortobj);
        }
//        Page<Tb_borrowdoc> jypage = jyAdminsService.getBorrowDocs(flag,state,type,page,limit,sortobj);
//        return jypage;
    }

    @RequestMapping("/getBorrowdocs")
    @ResponseBody
    public Page<Tb_borrowdoc> getBorrowdocs(int page,int limit,String condition,String operator,String content){
        return jyAdminsService.getBorrowDocs(page,limit, condition, operator, content);
    }

    @RequestMapping("/htmledit")
    public String htmledit() {
        return "/inlet/htmledit";
    }

    /**
     * 获取批示
     * @param borrowdocid 查档单号id
     * @return
     */
    @RequestMapping("/getApprove")
    @ResponseBody
    public ExtMsg getApprove(String borrowdocid){
        return new ExtMsg(true,"",jyAdminsService.getBorrowDoc(borrowdocid));
    }

    @RequestMapping("/getEntryIndex")
    @ResponseBody
    public Page<Tb_entry_index> getEntryIndex(int page,int limit,String borrowdocid,String type){
        return jyAdminsService.getEntryIndex(page,limit,borrowdocid,type);
    }

    @RequestMapping("/getBorrowEntryIndex")
    @ResponseBody
    public Page<Tb_entry_index_borrow> getBorrowEntryIndex(String flag,int page,int limit,String condition,String operator,String content){
        return jyAdminsService.getBorrowIndexEntry(flag,page,limit,condition,operator,content);
    }

    /**
     * 归还
     * @param ids 查档数据id数组
     * @return
     */
    @LogAnnotation(module="查档申请管理-归还管理",sites = "1",startDesc = "归还，条目编号：")
    @RequestMapping("/restitution")
    @ResponseBody
    public ExtMsg restitution(String[] ids,String returnMan,String remarkValue){
        jyAdminsService.restitution(ids,returnMan,remarkValue);
        return new ExtMsg(true,"归还成功",null);
    }

    /**
     * 转出
     * @param ids 查档数据id数组
     * @return
     */
    @RequestMapping("/moveout")
    @ResponseBody
    public ExtMsg moveout(String[] ids){
        jyAdminsService.moveout(ids);
        return new ExtMsg(true,"转出成功",null);
    }

    /**
     *  催还
     * @param ids
     * @return
     */
    @RequestMapping("/askToReturn")
    @ResponseBody
    public ExtMsg askToReturn(String[] ids){
        jyAdminsService.askToReturn(ids);
        return new ExtMsg(true,"催还成功",null);
    }

    /**
     * 检查本机构是否存在过期未还信息
     * @return
     */
    @RequestMapping("/checkExpireGhEntry")
    @ResponseBody
    public ExtMsg checkExpireGhEntry(){
        try {
            String state = jyAdminsService.checkExpireGhEntry();
            return new ExtMsg(true,"",state);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ExtMsg(false,"操作失败",null);
    }

    /**
     * 查档登记表单提交
     * @param borrowdoc 表单对象
     * @return 状态数据返回对象
     */
    @LogAnnotation(module="查档申请管理-来馆登记",sites = "1",fields = "borrowman",connect = "##查档人",startDesc = "新增登记，条目详细：")
    @RequestMapping("/borrowFormSubmit")
    @ResponseBody
    public ExtMsg borrowFormSubmit(Tb_borrowdoc borrowdoc){
        try {
            String state = jyAdminsService.borrowFormSubmit(borrowdoc);
            if(state!=null){
                return new ExtMsg(true,"表单提交完成,请导入需要借出的条目",state);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ExtMsg(false,"操作失败",null);
    }

    /**
     * 查档---修改表单提交
     * @param borrowdoc 表单对象
     * @return 状态数据返回对象
     */
    @LogAnnotation(module="查档申请管理-来馆登记",sites = "1",fields = "borrowman",connect = "##查档人",startDesc = "修改登记，条目详细：")
    @RequestMapping("/borrowUpdateSubmit")
    @ResponseBody
    public ExtMsg borrowUpdateSubmit(Tb_borrowdoc borrowdoc){
        try {
            String state = jyAdminsService.borrowModify(borrowdoc);
            if(state!=null){
                return new ExtMsg(true,"修改操作成功！",state);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ExtMsg(false,"操作失败",null);
    }

    /**
     * 查档数据导入
     * @param borrowcode 查档单号
     * @param dataids 需要导入的数据
     * @return 状态数据返回对象
     */
    @RequestMapping("/entryImport")
    @ResponseBody
    public ExtMsg entryImport(String borrowcode,String[] dataids){
        try {
            int[] states = jyAdminsService.entryImport(borrowcode,dataids);
            String msg = "";
            if(states[0]>0){
                msg = "成功导入"+states[0]+"条数据";
            }

            if(states[1]>0){
                String text = "重复"+states[1]+"条数据";
                msg += "".equals(msg)?text:","+text;
            }

            if(states[2]>0){
                String text = "库存不足"+states[2]+"条";
                msg += "".equals(msg)?text:","+text;
            }
            return new ExtMsg(true,"".equals(msg)?"操作失败":msg,null);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ExtMsg(false,"操作失败",null);
    }

    /**
     * 查档数据删除
     * @param borrowCodes 查档单号
     * @return 状态数据返回对象
     */
    @LogAnnotation(module="查档申请管理-来馆登记",sites = "1",startDesc = "删除登记，条目编号：")
    @RequestMapping("/deteteImport")
    @ResponseBody
    public ExtMsg deteteImport(String[] borrowCodes){
        try {
            String msg = jyAdminsService.deteteImport(borrowCodes);
            return new ExtMsg(true,msg,null);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ExtMsg(false,"操作失败",null);
    }

    /**
     * 根据查档单号显示查档条目
     * @param page 页码
     * @param limit 分页条目数
     * @param borrowcode 查档单号
     * @return 状态数据返回对象
     */
    @RequestMapping("/getMyBorrowmsgs")
    @ResponseBody
    public Page<Tb_entry_index> getMyBorrowmsgs(int page,int limit,String borrowcode){
        return jyAdminsService.getMyBorrowmsgs(page,limit,borrowcode);
    }

    /**
     * 根据查档单号显示查档条目(未归还)
     * @param page 页码
     * @param limit 分页条目数
     * @param borrowcode 查档单号
     * @return 状态数据返回对象
     */
    @RequestMapping("/getMyWGBorrowmsgs")
    @ResponseBody
    public Page<Tb_entry_index> getMyWGBorrowmsgs(int page,int limit,String borrowcode){
        return jyAdminsService.getMyWGBorrowmsgs(page,limit,borrowcode);
    }

    /**
     * 移除查档数据
     * @param borrowcode 查档单号
     * @param dataids 需要导入的数据
     * @return 状态数据返回对象
     */
    @RequestMapping("/removeImport")
    @ResponseBody
    public ExtMsg removeImport(String borrowcode,String[] dataids){
        try {
            int state = jyAdminsService.removeImport(borrowcode,dataids);
            return new ExtMsg(true,"移除"+state+"条数据",null);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ExtMsg(false,"操作失败",null);
    }

    @RequestMapping("/getplatformopen")
    @ResponseBody
    public ExtMsg getPlatformopen() {
        String platform = String.valueOf(slmRuntimeEasy.hasPlatform());
        return new ExtMsg(true, "", platform);
    }

    /**
     * 根据单据获取查档办理详情
     *
     * @param borrowdocid
     *            查档单据id
     * @return
     */
    @RequestMapping("/getDealDetails")
    @ResponseBody
    public Page getDealDetails(String borrowdocid) {
        try {
            return jyAdminsService.getDealDetails(borrowdocid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/getOutwareBorrowdocs")
    @ResponseBody
    public Page<Tb_borrowdoc> getOutwareBorrowdocs(String outwarestate,String type,int page,int limit,String condition,String operator,String content){
        return jyAdminsService.getOutwareBorrowdocs(outwarestate,type,page,limit, condition, operator, content);
    }

    /**
     * 实体查档单据出库
     *
     * @param borrowcodes
     *
     * @return
     */
    @RequestMapping("/setOutwareState")
    @ResponseBody
    public ExtMsg setOutwareState(String[] borrowcodes,String type) {
        try {
            jyAdminsService.setOutwareState(borrowcodes,type);
            if("1".equals(type)){
                webSocketService.noticeRefresh();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ExtMsg(true,"",null);
    }

    /**
     * 获取单据详情id
     *
     * @param borrowcodes
     *
     * @return
     */
    @RequestMapping("/getBorrowMsgEntryid")
    @ResponseBody
    public Map<String,List<Tb_entry_index>> getBorrowMsgEntryid(String[] borrowcodes) {
            return jyAdminsService.getBorrowMsgEntryid(borrowcodes);
    }


    @RequestMapping("/getBorrowDocsBycode")
    @ResponseBody
    public Map<String,List<Tb_borrowdoc>> getBorrowDocsBycode(String[] borrowcodes) {
        Map<String,List<Tb_borrowdoc>> map = new HashMap<>();
        List<Tb_borrowdoc> borrowdocList = borrowDocRepository.findByBorrowcodeIn(borrowcodes);
        map.put("borrowdocList",borrowdocList);
        return  map;
    }

    /**
     * 设置评分
     *
     * @param borrowdocid
     *
     * @return
     */
    @RequestMapping("/setAppraise")
    @ResponseBody
    public ExtMsg setAppraise(String borrowdocid,String labeltext,String content) {
         jyAdminsService.setAppraise(borrowdocid,labeltext,content);
        return new ExtMsg(true,"",null);
    }

    /**
     * 获取设置评分记录
     *
     * @param borrowdocid
     *
     * @return
     */
    @RequestMapping("/getAppraise")
    @ResponseBody
    public ExtMsg getAppraise(String borrowdocid) {
        return jyAdminsService.getAppraise(borrowdocid);
    }

    /**
     * 获取预约信息表
     * @param page
     * @param limit
     * @param condition
     * @param operator
     * @param content
     * @return
     */
    @RequestMapping("/getReservationdocs")
    @ResponseBody
    public Page<Tb_reserve> getReservationdocs(String type,int page,int limit,String condition,String operator,String content,String lymode,String taskid,String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        return jyAdminsService.getReservationdocs(type,page,limit, condition, operator, content,lymode,taskid,sortobj);
    }

    @RequestMapping("/reservationAddForm")
    @ResponseBody
    public ExtMsg reservationAddForm(Tb_reserve tb_reserve, String showroomid,String[] eleids) {
        int yyPerson=0;//预约人数
        if(showroomid!=null){//展厅预约
            String lgAudiences=tb_reserve.getBorrowmantime();//来馆人数
            try{
                if(lgAudiences!=null && entryCaptureService.isNumeric(lgAudiences)){
                    yyPerson=Integer.valueOf(lgAudiences);//预约来馆人数
                }else{//来馆人数信息不规范已在前端处理
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            //对应的展厅的指定日期的预约人数+申请来馆人数，判断是否当天预约人数已满，已满的话返回提示预约另一天
            boolean flag=jyAdminsService.checkAudiences(tb_reserve, showroomid, yyPerson);
            if(!flag){//预约人数超出
                return new ExtMsg(true, "预约人数超出，请预约另一天或者减少预约人数", null);
            } //未满的话保存预约信息，提示预约成功

            //在Tb_reserve存储展厅id和名称
            tb_reserve.setBorrowdate(showroomid);
            Tb_showroom showroom = showroomRepository.findByShowroomid(showroomid);
            String content = tb_reserve.getBorrowcontent();//查档内容
            if(content!=null){
                content = showroom.getTitle()+": "+content;
            }else{
                content = showroom.getTitle();
            }
            tb_reserve.setBorrowcontent(content);//查档内容显示展厅名称
        }
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        tb_reserve.setSubmiterid(userDetails.getUserid());  //设置提交人id
        tb_reserve.setYystate("未回复");
        Tb_reserve tbreserves = jyAdminsService.reservationAddForm(tb_reserve,eleids);
        if(tbreserves!=null){
            jyAdminsService.setYyTask(tbreserves);
            webSocketService.noticeRefresh();
            if(showroomid!=null && yyPerson>1){
                return new ExtMsg(true, "已成功提交预约，等待管理员审批", null);
            }
            return new ExtMsg(true, "预约成功", null);
        }
        return new ExtMsg(false, "预约失败", null);
    }


    @RequestMapping("/getReplyMsgForm")
    @ResponseBody
    public ExtMsg getReplyMsgForm(String replycontent) {
        Tb_reserve tb_reserve = new Tb_reserve();
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        tb_reserve.setReplier(userDetails.getRealname());
        tb_reserve.setReplytime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        tb_reserve.setReplycontent(replycontent);
        tb_reserve.setYystate("已回复");
        return new ExtMsg(true, "成功", tb_reserve);
    }

    @RequestMapping("/reservationReplyAddForm")
    @ResponseBody
    public ExtMsg reservationReplyAddForm(Tb_reserve tb_reserve,String docid,String taskid) {
        String type = jyAdminsService.reservationReplyAddForm(tb_reserve,docid,taskid);
        if("2".equals(type)){
            webSocketService.noticeRefresh();
            return new ExtMsg(true, "该预约已经回复", null);
        }else if("1".equals(type)){
            webSocketService.noticeRefresh();
            return new ExtMsg(true, "预约回复成功", null);
        }
        return new ExtMsg(false, "预约回复失败", null);
    }

    @RequestMapping("/getCancelMsgForm")
    @ResponseBody
    public ExtMsg getCancelMsgForm() {
        Tb_reserve tb_reserve = new Tb_reserve();
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        tb_reserve.setCanceler(userDetails.getRealname());
        tb_reserve.setCanceltime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        tb_reserve.setYystate("已取消");
        return new ExtMsg(true, "成功", tb_reserve);
    }

    @RequestMapping("/reservationCancelAddForm")
    @ResponseBody
    public ExtMsg reservationCancelAddForm(String docid) {
        Tb_reserve tb_reserves = jyAdminsService.reservationCancelAddForm(docid);
        if(tb_reserves!=null){
            return new ExtMsg(true, "预约取消成功", null);
        }
        return new ExtMsg(false, "预约取消失败", null);
    }

    /**
     * 库房入库对应条目归还
     *
     * @param ids
     *
     * @return
     */
    @RequestMapping("/inwareReturn")
    @ResponseBody
    public ExtMsg inwareReturn(String ids) {
        jyAdminsService.inwareReturn(ids);
        return new ExtMsg(true,"",null);
    }

    @RequestMapping("/isBorrowdocOutware")
    @ResponseBody
    public ExtMsg isBorrowdocOutware(String borrowcode) {
        Tb_borrowdoc borrowdoc = jyAdminsService.isBorrowdocOutware(borrowcode);
        if("已借出".equals(borrowdoc.getOutwarestate())){
            webSocketService.noticeRefresh();
        }
        return new ExtMsg(true,"",borrowdoc);
    }

    /**
     * 通过docid获取借阅单的批示传给前端
     *
     * @param docid
     * @throws
     */
    @RequestMapping(value = "/getApproveByDocid",method = RequestMethod.GET)
    @ResponseBody
    public ExtMsg getApproveByDocid(String docid,String doccode) {
        Set<String> list=jyAdminsService.getApproveByDocid(docid,doccode);
        if(list==null){
            return new ExtMsg(false,"",null);
        }
        return new ExtMsg(true,"",list);
    }
}
