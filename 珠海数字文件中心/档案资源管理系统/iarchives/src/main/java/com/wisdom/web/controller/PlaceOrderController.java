package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.FunctionUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.*;
import com.xdtech.smsclient.SMSService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2020/4/28.
 */
@Controller
@RequestMapping("placeOrder")
public class PlaceOrderController {


    @Autowired
    PlaceManageService placeManageService;

    @Autowired
    PlaceOrderService placeOrderService;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    PlaceOrderRepository placeOrderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FlowsRepository flowsRepository;

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

    @Value("${workflow.cdborrow.approve.workid}")
    private String cdBorrowWorkId;

    @RequestMapping("/main")
    public String index(Model model, String taskid,String isp){
        Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
        model.addAttribute("functionButton",functionButton);
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("realname",userDetails.getRealname());
        model.addAttribute("taskId",taskid);
        if(taskid!=null){
            model.addAttribute("iflag","1");
            model.addAttribute("orderAuditState","true");
        }else{
            List<Tb_user_node> nodeUsers = userNodeRepository.findNodeidUser("场地预约审批",userDetails.getUserid());
            if(nodeUsers.size()>0){  //是否显示预约审核选项卡
                model.addAttribute("orderAuditState","true");
            }else{
                model.addAttribute("orderAuditState","false");
            }
            model.addAttribute("iflag","0");
        }
        return "/inlet/placeOrder";
    }

    @RequestMapping("/getPlaceManages")
    @ResponseBody
    public Page<Tb_place_manage> getPlaceManages(int page, int limit, String sort, String condition, String operator, String content){
        PageRequest pageRequest = new PageRequest(page-1,limit);
        Page<Tb_place_manage> placeManages =  placeManageService.getPlaceManages(page,limit,sort,condition,operator,content);
        List<Tb_place_manage> placeManageList = placeManages.getContent();
        List<Tb_place_manage> returnList = new ArrayList<>();
        for(Tb_place_manage placeManage : placeManageList){
            Tb_place_manage place = new Tb_place_manage();
            BeanUtils.copyProperties(placeManage,place);
            if("空闲中".equals(place.getState())){
                place.setState("<span style='color:green'>"+place.getState()+"</span>");
            }else if("使用中".equals(place.getState())){
                place.setState("<span style='color:red'>"+place.getState()+"</span>");
            }
            returnList.add(place);
        }
        return new PageImpl<Tb_place_manage>(returnList,pageRequest,placeManages.getTotalElements());
    }

    @RequestMapping("/getUserPlaceOrder")
    @ResponseBody
    public Page<Tb_place_order> getUserPlaceOrder(String type,String id,String selectTime,int page, int limit, String sort, String condition, String operator, String content){
        if("@".equals(id)){
            return null;
        }
        if("".equals(sort)||sort==null){
            sort="[{'property':'ordertime','direction':'desc'}]";
        }
        Page<Tb_place_order> place_orders=null;
        if(id != null){
            place_orders =placeOrderService.getUserPlaceOrderByPlaceId(selectTime,type,id,page,limit,sort,condition,operator,content);
            List<Tb_place_order>  list=place_orders.getContent();
            list.stream().forEach(item->{
                if(!StringUtils.isEmpty(item.getPlaceid())){
                    item.setPlaceName(placeManageRepository.findPlacedescById(item.getPlaceid()));
                }
            });
        }else {
            place_orders = placeOrderService.getUserPlaceOrder(type, page, limit, sort, condition, operator, content);
            List<Tb_place_order>  list=place_orders.getContent();
            list.stream().forEach(item->{
                if(!StringUtils.isEmpty(item.getPlaceid())){
                    item.setPlaceName(placeManageRepository.findPlacedescById(item.getPlaceid()));
                }
            });
        }
//        List<Tb_place_order> place_orderList = place_orders.getContent();
//        List<Tb_place_order> returnOrders = new ArrayList<>();
//        for(Tb_place_order placeOrder : place_orderList){
//            Tb_place_order returnPlaceOrder = new Tb_place_order();
//            BeanUtils.copyProperties(placeOrder,returnPlaceOrder);//深度克隆
//            Tb_place_order order = placeOrderRepository.getPlaceOrderByIdAndReturnstateAndState(placeOrder.getId(),
//                    "未归还","预约成功");
//            if(order != null){
//                returnPlaceOrder.setState("<span style='color:red'>"+placeOrderService.removeNull(returnPlaceOrder.getState())+
//                        "</span>");
//                returnPlaceOrder.setPlaceuser("<span style='color:red'>"+placeOrderService.removeNull(returnPlaceOrder.getPlaceuser())+"</span>");
//                returnPlaceOrder.setPhonenumber("<span style='color:red'>"+placeOrderService.removeNull(returnPlaceOrder.getPhonenumber())+"</span>");
//                returnPlaceOrder.setOrdertime("<span style='color:red'>"+placeOrderService.removeNull(returnPlaceOrder.getOrdertime())+"</span>");
//                returnPlaceOrder.setStarttime("<span style='color:red'>"+placeOrderService.removeNull(returnPlaceOrder.getStarttime())+"</span>");
//                returnPlaceOrder.setEndtime("<span style='color:red'>"+placeOrderService.removeNull(returnPlaceOrder.getEndtime())+"</span>");
//                returnPlaceOrder.setUseway("<span style='color:red'>"+placeOrderService.removeNull(returnPlaceOrder.getUseway())+"</span>");
//                returnPlaceOrder.setCancelreason("<span style='color:red'>"+placeOrderService.removeNull(returnPlaceOrder.getCancelreason())+
//                        "</span>");
//            }
//            returnOrders.add(returnPlaceOrder);
//        }
        if( place_orders.getContent().size()>0) {
            for (Tb_place_order tb_place_order : place_orders.getContent()) {
                if ("预约成功".equals(tb_place_order.getState())) {
                    tb_place_order.setState("<span style='color:green'>" + tb_place_order.getState() + "</span>");
                } else if ("预约失败".equals(tb_place_order.getState())) {
                    tb_place_order.setState("<span style='color:red'>" + tb_place_order.getState() + "</span>");
                }
            }
        }
        PageRequest pageRequest = new PageRequest(page-1,limit);
        return new PageImpl<Tb_place_order>(place_orders.getContent(),pageRequest,place_orders.getTotalElements());
    }

    @RequestMapping("/placeOrderFormLoad")
    @ResponseBody
    public ExtMsg placeOrderFormLoad(){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String datastr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Tb_user user = userRepository.findByUserid(userDetails.getUserid());
        Tb_place_order placeOrder = new Tb_place_order();
        placeOrder.setPlaceuser(userDetails.getRealname());
        placeOrder.setUserorgan(userDetails.getOrganid());
        placeOrder.setPhonenumber(user.getPhone());
        placeOrder.setOrdertime(datastr);
        return new ExtMsg(true,"",placeOrder);
    }

    @RequestMapping("/placeOrderFormSubmit")
    @ResponseBody
    public ExtMsg placeOrderFormSubmit(Tb_place_order placeOrder,String spnodeid,String spmanid,String placeid){
        List<Tb_place_order> placeOrders = placeOrderService.placeOrderFormSubmit(placeOrder,spnodeid,spmanid,placeid);
        webSocketService.noticeRefresh();
        if(placeOrders!=null){
            return new ExtMsg(true,"",placeOrders);
        }else{
            return new ExtMsg(true,"",null);
        }
    }

    @RequestMapping("/placeOrderCancelFormLoad")
    @ResponseBody
    public ExtMsg placeOrderCancelFormLoad(){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String datastr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Tb_place_order placeOrder = new Tb_place_order();
        placeOrder.setCanceluser(userDetails.getRealname());
        placeOrder.setCanceltime(datastr);
        return new ExtMsg(true,"",placeOrder);
    }

    @RequestMapping("/placeOrderCancelFormSubmit")
    @ResponseBody
    public ExtMsg carOrderCancelFormSubmit(String orderid,String canceluser,String canceltime,String cancelreason) throws Exception{
        Tb_place_order placeOrder = placeOrderService.placeOrderCancelFormSubmit(orderid,canceluser,canceltime,cancelreason);
        webSocketService.noticeRefresh();
        if(placeOrder!=null){
            return new ExtMsg(true,"",placeOrder);
        }else{
            return new ExtMsg(true,"",null);
        }
    }

    @RequestMapping("/placeOrderDelete")
    @ResponseBody
    public ExtMsg placeOrderDelete(String[] orderids){
        int count = 0;
        count = placeOrderService.placeOrderDelete(orderids);
        if(count>0){
            return new ExtMsg(true,"",null);
        }else{
            return new ExtMsg(false,"",null);
        }
    }

    @RequestMapping("/placeOrderLookFormLoad")
    @ResponseBody
    public ExtMsg placeOrderLookFormLoad(String orderid){
        Tb_place_order placeOrder = placeOrderRepository.findById(orderid);
        return new ExtMsg(true,"",placeOrder);
    }

    @RequestMapping("/getAuditDetails")
    @ResponseBody
    public Page getAuditDetails(String orderid){
        return placeOrderService.getAuditDetails(orderid);
    }

    @RequestMapping("/getPlaceAuditOrder")
    @ResponseBody
    public Page<Tb_place_order> getPlaceAuditOrder(String taskid,int page,int limit,String sort, String condition, String operator, String content){
        return placeOrderService.getPlaceAuditOrder(taskid,page,limit,sort,condition,operator,content);
    }

    @RequestMapping("/getNextNode")
    @ResponseBody
    public List<Tb_node> getNextNode(String orderid) {
        return placeOrderService.getNodes(orderid);
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
    public ExtMsg auditOrderSubmit(String textArea, String nextNode, String nextSpman, String orderid,String selectApprove) throws Exception{
        placeOrderService.auditOrderSubmit(textArea, nextNode, nextSpman,orderid,selectApprove);
        webSocketService.noticeRefresh();
        return new ExtMsg(true,"审核完成",null);
    }

    @RequestMapping("/getMyPlaceOrder")
    @ResponseBody
    public IndexMsg getMyPlaceOrder(int page, int limit, String sort, String condition, String operator, String content){
        Page<Tb_place_order> orderPage = placeOrderService.getUserPlaceOrder("user",page,limit,sort,condition,operator,content);
        List<Tb_place_order> orderList = orderPage.getContent();
        List<Tb_place_order> returnOrders = new ArrayList<>();
        for(Tb_place_order placeOrder : orderList){
            Tb_place_order orderReturn = new Tb_place_order();
            BeanUtils.copyProperties(placeOrder,orderReturn);
            Tb_place_manage manage = placeManageRepository.findById(placeOrder.getPlaceid());
            if(manage != null){
                orderReturn.setRemark(manage.getPlacedesc());
            }
            //orderReturn.setRemark(manage.getPlacedesc());
            returnOrders.add(orderReturn);
        }
        return new IndexMsg(true,"0","成功",returnOrders);
    }

    //根据id查询审批节点
    @RequestMapping("/findByWorkId")
    @ResponseBody
    public ExtMsg findByWorkId() {
        Tb_work work= workflowService.findByWorkid(cdBorrowWorkId);
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
    @RequestMapping("/getPlaceOrderid")
    @ResponseBody
    public String getPlaceOrderid(String taskid) {
        return placeOrderService.getPlaceOrderid(taskid);
    }

    @RequestMapping("/getUserPlaceOrders")
    @ResponseBody
    public List getUserPlaceOrders(){
        List<Tb_place_order> placeOrders = placeOrderService.getUserPlaceOrders();
        List<Tb_place_order> returnOrders = new ArrayList<>();
        for(Tb_place_order placeOrder : placeOrders){
            Tb_place_order returnOrder = new Tb_place_order();
            BeanUtils.copyProperties(placeOrder,returnOrder);
            Tb_place_manage placeManage = placeManageRepository.findById(placeOrder.getPlaceid());
            if(placeManage != null){
                returnOrder.setApprove(placeManage.getPlacedesc());
            }
            returnOrders.add(returnOrder);
        }
        return returnOrders;
    }
}
