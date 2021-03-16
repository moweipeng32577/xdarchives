package com.wisdom.web.controller;

import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.BorrowMsgRepository;
import com.wisdom.web.repository.ElectronicSolidRepository;
import com.wisdom.web.repository.UserRepository;
import com.wisdom.web.repository.WorkRepository;
import com.wisdom.web.service.ElectronApproveService;
import com.wisdom.web.service.ElectronPrintApproveService;
import com.xdtech.smsclient.SMSService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 电子打印审批控制器
 * Created by Administrator on 2019/5/23.
 */
@Controller
@RequestMapping(value = "/electronPrintApprove")
public class ElectronPrintApproveController {

    @Autowired
    ElectronApproveService electronApproveService;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    ElectronPrintApproveService electronPrintApproveService;

    @Autowired
    ElectronicSolidRepository electronicSolidRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SMSService smsService;

    @Autowired
    WorkRepository workRepository;


    @RequestMapping("/main")
    public String index(Model model, String taskid, String flag, String type) {
        List<Tb_flows> flows = electronApproveService.getFlowsInfo(taskid, type);
        if (flows.size() > 1) {
            model.addAttribute("info","电子打印");
            return "/inlet/missionAdmins";
        }
        model.addAttribute("flowsText",flows.get(0).getText());
        model.addAttribute("nodeId",flows.get(0).getNodeid());
        model.addAttribute("taskid",taskid);
        model.addAttribute("iflag",flag);
        model.addAttribute("type",type);
        Tb_work workPrint = workRepository.findByWorktext("电子打印审批");
        if(workPrint!=null){
            model.addAttribute("printSendmsg","1".equals(workPrint.getSendmsgstate())?true:false);  //电子打印是否短信通知
        }else{
            model.addAttribute("printSendmsg",false);
        }
        return "/inlet/electronPrintApprove";
    }

    /**
     * 电子打印审批提交
     *
     * @param textArea
     *            批示
     * @param nextNode
     *            下一节点
     * @param nextSpman
     *            下一审批人
     * @param taskid
     *            任务Id
     * @param nodeId
     *            节点id
     * @return
     */
    @RequestMapping("/approvePrintSubmit")
    @ResponseBody
    public ExtMsg approveSubmit(String textArea, String nextNode, String nextSpman, String taskid, String nodeId,
                                int borrowtyts,String sendMsg) {
        electronPrintApproveService.approvePrintSubmit(textArea, nextNode, nextSpman, taskid, nodeId, borrowtyts);
        webSocketService.noticeRefresh();
        Tb_user spuser = userRepository.findByUserid(nextSpman);
        String returnStr = "";
        if(sendMsg!=null&&"true".equals(sendMsg)&&spuser!=null){   //短信提醒
            try {
                returnStr = smsService.SendSMS(spuser.getPhone(),"您有一条档案系统的待办审批，请登录档案系统管理平台及时处理！");
            }catch (Exception e){
                e.printStackTrace();
                return new ExtMsg(true,"审批完成，短信发送失败",null);
            }
        }
        if("".equals(returnStr)){
            return new ExtMsg(true,"审批完成",null);
        }else{
            return new ExtMsg(true,"审批完成，短信发送结果为："+returnStr,null);
        }
    }
    /**
    * 查无此档 返回
    *
    * @param taskid
    * @param nodeId
    * @param textarea
    * @return {@link ExtMsg}
    * @throws
    */
    @RequestMapping("/filenotfound")
    @ResponseBody
    public ExtMsg filenotfound(String taskid,String nodeId,String textarea){
        electronPrintApproveService.filenotfound(taskid,nodeId,textarea);
        return new ExtMsg(true,"审批完成",null);
    }

    //电子打印审批退回
    @RequestMapping("/returnPrintApply")
    @ResponseBody
    public ExtMsg returnPrintApply(String textarea,String taskid,String nodeId){
        electronPrintApproveService.returnPrintApply(textarea,taskid,nodeId);
        webSocketService.noticeRefresh();
        return new ExtMsg(true,"",null);
    }

    @RequestMapping("/setPrintApproveState")
    @ResponseBody
    public ExtMsg setPrintApproveState(String taskid,String[] entryids,String type,String setType) {
        electronPrintApproveService.setPrintApproveState( taskid, entryids, type,setType);
        return new ExtMsg(true,"",null);
    }

    @RequestMapping("/getApproveSetPrint")
    @ResponseBody
    public List<Tb_electronic_print> getApproveSetPrint(String entryid,String borrowcode,String type) {
        List<Tb_electronic_print> electronic_prints = electronPrintApproveService.getApproveSetPrint(entryid,borrowcode);
        List<Tb_electronic_print> electronicPrintsIsP = new ArrayList<>(); //申请打印的电子文件
        List<Tb_electronic_print> electronicPrintsNoP = new ArrayList<>(); //未申请打印的电子文件
        if("all".equals(type)){
            for(Tb_electronic_print electronicPrint : electronic_prints){
                if(electronicPrint.getState()!=null&&!"".equals(electronicPrint.getState())){
                    electronicPrintsIsP.add(electronicPrint);
                }else{
                    electronicPrintsNoP.add(electronicPrint);
                }
            }
            electronicPrintsIsP.addAll(electronicPrintsNoP);
            return electronicPrintsIsP;
        }else if("allPass".equals(type)){
            for(Tb_electronic_print electronicPrint : electronic_prints){
                if(electronicPrint.getState()!=null&&!"".equals(electronicPrint.getState())&&!"拒绝".equals(electronicPrint.getState())){
                    electronicPrintsIsP.add(electronicPrint);
                }else{
                    electronicPrintsNoP.add(electronicPrint);
                }
            }
            electronicPrintsIsP.addAll(electronicPrintsNoP);
            return electronicPrintsIsP;
        }else{
            for(Tb_electronic_print electronicPrint : electronic_prints){
                if(electronicPrint.getState()!=null&&!"".equals(electronicPrint.getState())&&!"拒绝".equals(electronicPrint.getState())){
                    electronicPrintsIsP.add(electronicPrint);
                }
            }
            return electronicPrintsIsP;
        }
    }
}
