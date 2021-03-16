package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.FunctionUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.CarManageService;
import com.wisdom.web.service.CarOrderService;
import com.wisdom.web.service.TaskService;
import com.wisdom.web.service.WorkflowService;
import com.xdtech.smsclient.SMSService;
import org.apache.velocity.runtime.directive.Parse;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2020/4/21.
 */
@Controller
@RequestMapping("carOrder")
public class CarOrderController {


    @Autowired
    CarOrderService carOrderService;

    @Autowired
    CarOrderRepository carOrderRepository;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    FlowsRepository flowsRepository;

    @Autowired
    CarManageService carManageService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CarManageRepository carManageRepository;

    @Autowired
    PlaceManageRepository placeManageRepository;

    @Autowired
    SMSService smsService;

    @Autowired
    TaskService taskService;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    UserNodeRepository userNodeRepository;

    @Value("${workflow.busReservation.approve.workid}")
    private String busReservationWorkId;


    @RequestMapping("/main")
    public String index(Model model,String taskid,String isp){
        Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
        model.addAttribute("functionButton",functionButton);
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("realname",userDetails.getRealname());
        model.addAttribute("taskId",taskid);
        if(taskid!=null){
            model.addAttribute("iflag","1");
            model.addAttribute("orderAuditState","true");
        }else{
            List<Tb_user_node> nodeUsers = userNodeRepository.findNodeidUser("公车预约审批",userDetails.getUserid());
            if(nodeUsers.size()>0){  //是否显示预约审核选项卡,如果是审批环节审批人，则有
                model.addAttribute("orderAuditState","true");
            }else{
                model.addAttribute("orderAuditState","false");
            }
            model.addAttribute("iflag","0");
        }
        return "/inlet/carOrder";
    }

    @RequestMapping("/auditOrderMain")
    public String auditOrderMain(Model model,String auditType){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("realname",userDetails.getRealname());
        model.addAttribute("auditType",auditType);
        return "/inlet/auditOrder";
    }

    @RequestMapping("/getAllCarOrder")
    @ResponseBody
    public Page<Tb_car_order> getAllCarOrder(int page,int limit,String sort, String condition, String operator, String content){
        return carOrderService.getAllCarOrder(page,limit,sort,condition,operator,content);
    }

    @RequestMapping("/getCarOrderByCarid")
    @ResponseBody
    public Page<Tb_car_order> getCarOrderBy(String id,String selectTime, int page, int limit, String sort, String condition, String operator, String content){
        if("@".equals(id)){
            return null;
        }
        if("".equals(sort)||sort==null){
            sort="[{'property':'ordertime','direction':'desc'}]";
        }
        Page<Tb_car_order> returnList= carOrderService.getCarOrderByCarid(id,selectTime,page,limit,sort,condition,operator,content);
        if(returnList.getContent().size()>0) {
            for (Tb_car_order carOrder : returnList.getContent()) {
                if("预约成功".equals(carOrder.getState())){
                    carOrder.setState("<span style='color:green'>"+carOrder.getState()+"</span>");
                }else if("预约失败".equals(carOrder.getState())){
                    carOrder.setState("<span style='color:red'>"+carOrder.getState()+"</span>");
                }
            }
        }
        return returnList;
    }

    @RequestMapping("/carOrderFormLoad")
    @ResponseBody
    public ExtMsg carOrderFormLoad(){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String datastr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Tb_user user = userRepository.findByUserid(userDetails.getUserid());
        Tb_car_order carOrder = new Tb_car_order();
        carOrder.setCaruser(userDetails.getRealname());
        carOrder.setUserorgan(userDetails.getOrganid());
        carOrder.setPhonenumber(user.getPhone());
        carOrder.setOrdertime(datastr);
        return new ExtMsg(true,"",carOrder);
    }

    @RequestMapping("/carOrderFormSubmit")
    @ResponseBody
    public ExtMsg carOrderFormSubmit(Tb_car_order carOrder,String spnodeid,String spmanid,String carid){
        List<Tb_car_order> carOrders = carOrderService.carOrderFormSubmit(carOrder,spnodeid,spmanid,carid);
        webSocketService.noticeRefresh();
        if(carOrders!=null){
            return new ExtMsg(true,"",carOrders);
        }else{
            return new ExtMsg(true,"",null);
        }
    }

    @RequestMapping("/carOrderCancelFormLoad")
    @ResponseBody
    public ExtMsg carOrderCancelFormLoad(){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String datastr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Tb_car_order carOrder = new Tb_car_order();
        carOrder.setCanceluser(userDetails.getRealname());
        carOrder.setCanceltime(datastr);
        return new ExtMsg(true,"",carOrder);
    }

    @RequestMapping("/carOrderCancelFormSubmit")
    @ResponseBody
    public ExtMsg carOrderCancelFormSubmit(String orderid,String canceluser,String canceltime,String cancelreason) throws Exception{
        Tb_car_order carOrder = carOrderService.carOrderCancelFormSubmit(orderid,canceluser,canceltime,cancelreason);
        webSocketService.noticeRefresh();
        if(carOrder!=null){
            return new ExtMsg(true,"",carOrder);
        }else{
            return new ExtMsg(true,"",null);
        }
    }

    @RequestMapping("/carOrderDelete")
    @ResponseBody
    public ExtMsg carOrderDelete(String[] orderids){
        int count = 0;
        count = carOrderService.carOrderDelete(orderids);
        if(count>0){
            return new ExtMsg(true,"",null);
        }else{
            return new ExtMsg(false,"",null);
        }
    }

    @RequestMapping("/carOrderLookFormLoad")
    @ResponseBody
    public ExtMsg carOrderLookFormLoad(String orderid){
        Tb_car_order carOrder = carOrderRepository.findById(orderid);
        return new ExtMsg(true,"",carOrder);
    }

    @RequestMapping("/getAuditDetails")
    @ResponseBody
    public Page getAuditDetails(String orderid){
        return carOrderService.getAuditDetails(orderid);
    }

    @RequestMapping("/getUserAuditOrder")
    @ResponseBody
    public Page<Tb_car_order> getUserAuditOrder(String taskid,int page,int limit,String sort, String condition, String operator, String content){
        return carOrderService.getUserAuditOrder(taskid,page,limit,sort,condition,operator,content);
    }

    @RequestMapping("/getNextNode")
    @ResponseBody
    public List<Tb_node> getNextNode(String orderid) {
        return carOrderService.getNodes(orderid);
    }

    /**
     * 审核提交
     *
     * @param textArea
     *            批示
     * @param nextNode
     *            下一节点
     * @param nextSpman
     *            下一审批人
     * @return
     */
    @RequestMapping("/auditOrderSubmit")
    @ResponseBody
    public ExtMsg auditOrderSubmit(String textArea, String nextNode, String nextSpman, String orderid,String selectApprove) {
        carOrderService.auditOrderSubmit(textArea, nextNode, nextSpman,orderid,selectApprove);
        webSocketService.noticeRefresh();
        return new ExtMsg(true,"审核完成",null);
    }

    @RequestMapping("/getAllCarManages")
    @ResponseBody
    public Page<Tb_car_manage> getAllCarManages(int page, int limit, String sort, String condition, String operator, String content){
        Page<Tb_car_manage> carManages = carManageService.getCarManages(page,limit,sort,condition,operator,content);
        List<Tb_car_manage> car_manageList = carManages.getContent();
        List<Tb_car_manage> returnManages = new ArrayList<>();
        for(Tb_car_manage carManage : car_manageList){
            if("使用中".equals(carManage.getState())){
                Tb_car_manage returnCarManage = new Tb_car_manage();
                BeanUtils.copyProperties(carManage,returnCarManage);
                 List<Tb_car_order> orders = carOrderRepository.findByCaridAndStateAndReturnstate(carManage.getId(),"预约成功","未归还");
                 if(orders.size()>0){
                     returnCarManage.setState("<span style='color:red'>已预约</span>");
                 }else{
                     returnCarManage.setState("<span style='color:green'>正常</span>");
                 }
                returnManages.add(returnCarManage);
            }else{
                returnManages.add(carManage);
            }
        }
        PageRequest pageRequest = new PageRequest(page-1,limit);
        return new PageImpl<Tb_car_manage>(returnManages,pageRequest,carManages.getTotalElements());
    }

    @RequestMapping("/getMyCarOrder")
    @ResponseBody
    public IndexMsg getMyCarOrder(int page, int limit, String sort){
        Page<Tb_car_order> orderPage = carOrderService.getMyCarOrder(page,limit,sort);
        List<Tb_car_order> orderList = orderPage.getContent();
        List<Tb_car_order> returnOrders = new ArrayList<>();
        for(Tb_car_order carOrder : orderList){
            Tb_car_order orderReturn = new Tb_car_order();
            BeanUtils.copyProperties(carOrder,orderReturn);
            Tb_car_manage manage = carManageRepository.findById(carOrder.getCarid());
            orderReturn.setRemark(manage.getCartype());
            returnOrders.add(orderReturn);
        }
        return new IndexMsg(true,"0","成功",returnOrders);
    }

    //根据id查询审批节点
    @RequestMapping("/findByWorkId")
    @ResponseBody
    public ExtMsg findByWorkId() {
        Tb_work work= workflowService.findByWorkid(busReservationWorkId);
        return new ExtMsg(true,"",work);
    }

    //手动催办
    @RequestMapping("/manualUrging")
    @ResponseBody
    public ExtMsg manualUrging(String ordercode,String sendMsg){
        if(ordercode==null){
            return new ExtMsg(false, "催办失败", null);
        }
        Tb_flows billApproval= taskService.manualUrging(ordercode);
        String returnStr = "";
        if(billApproval!=null) {
            Tb_user spuser = userRepository.findByUserid(billApproval.getSpman());
            if(sendMsg!=null&&"true".equals(sendMsg)&&spuser!=null) {
                try {
                    returnStr = smsService.SendSMS(spuser.getPhone(), "您有一条档案系统的销毁审批，请登录档案系统管理平台及时处理！");
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

    //根据任务id找单据id
    @RequestMapping("/getCarOrderid")
    @ResponseBody
    public String getCarOrderid(String taskid) {
        return carOrderService.getCarOrderid(taskid);
    }

    //借出
    @RequestMapping("/updateReturnstate")
    @ResponseBody
    public ExtMsg updateReturnstate(String ordercode,String state) {
        Tb_car_order carOrder=carOrderRepository.findById(ordercode);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date startTime=df.parse(carOrder.getStarttime());
            Date endTime=df.parse(carOrder.getEndtime());
            if(endTime.compareTo(new Date())<0){
                return new ExtMsg(false, "超过预约结束时间，请重新预约", null);
            }
            if(startTime.compareTo(new Date())>0){
                return new ExtMsg(false, "未到预约开始时间，不可借出", null);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(carOrderService.updateReturnstate(ordercode,state)>0){
            return new ExtMsg(true, "借出成功", null);
        }
        return new ExtMsg(false, "借出失败", null);
    }

    @RequestMapping("/getNextNodeByType")
    @ResponseBody
    public List<Tb_node> getNextNodeByType(String orderid,String type) {
        return carOrderService.getNextNodeByType(orderid,type);
    }

    @RequestMapping("/getAuditOrderByTask")
    @ResponseBody
    public Page<Tb_task> getAuditOrderByTask(int page,int limit,String sort){
        return carOrderService.getAuditOrderByTask(page,limit,sort);
    }

    @RequestMapping("/getInforms")
    @ResponseBody
    public Page<Tb_inform> getInforms(int page,int limit,String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        return carOrderService.getInforms(page,limit,sortobj);
    }

    @RequestMapping("/htmledit")
    public String htmledit() {
        return "/inlet/htmledit";
    }

    //根据任务id找单据id
    @RequestMapping("/getCarOrderidByType")
    @ResponseBody
    public String getCarOrderidByType(String taskid,String type) {
        return carOrderService.getCarOrderidByType(taskid,type);
    }

    @RequestMapping("/getUserCarOrders")
    @ResponseBody
    public List getUserCarOrders(){
        return carOrderService.getUserCarOrders();
    }
}
