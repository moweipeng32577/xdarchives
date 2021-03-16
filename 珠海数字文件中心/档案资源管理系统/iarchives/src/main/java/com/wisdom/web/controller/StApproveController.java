package com.wisdom.web.controller;

import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.UserRepository;
import com.wisdom.web.service.ElectronApproveService;
import com.wisdom.web.service.StApproveService;
import com.xdtech.smsclient.SMSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 实体查档审批控制器
 * Created by Administrator on 2017/10/30 0030.
 */
@Controller
@RequestMapping(value = "/stApprove")
public class StApproveController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ElectronApproveService electronApproveService;

    @Autowired
    StApproveService stApproveService;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SMSService smsService;

    @RequestMapping("/main")
    public String index(Model model,String taskid,String flag,String type) {
        List<Tb_flows> flows = electronApproveService.getFlowsInfo(taskid, type);
        if (flows.size() > 1) {
    		model.addAttribute("info","电子查档");
    		return "/inlet/missionAdmins";
    	}
        model.addAttribute("flowsText",flows.get(0).getText());
        model.addAttribute("nodeId",flows.get(0).getNodeid());
        model.addAttribute("taskid",taskid);
        model.addAttribute("iflag",flag);
        model.addAttribute("type",type);
        return "/inlet/stApprove";
    }

    @RequestMapping("/getEntryIndex")
    @ResponseBody
    public Page<Tb_entry_index_borrow> getEntryIndex(int page, int start, int limit, String taskid) {
        logger.info("page:" + page + "start:" + start + "limt:" + limit);
        return electronApproveService.getEntryIndex(page,limit,taskid);
    }

    @RequestMapping("/getBorrowDocByTaskid")
    @ResponseBody
    public ExtMsg getBorrowDocByTaskid(String taskid) {
        return new ExtMsg(true,"成功",electronApproveService.getBorrowDocByTaskid(taskid));
    }

    @RequestMapping("/approveAdd")
    @ResponseBody
    public ExtMsg approveAdd(String taskid,String approve) {
        return new ExtMsg(true,"成功",electronApproveService.getBorrowDocByTaskid(taskid));
    }

    @RequestMapping("/getNextNode")
    @ResponseBody
    public List<Tb_node> getNextNode(String nodeId) {
        return electronApproveService.getStNodes(nodeId);
    }

    @RequestMapping("/getNextSpman")
    @ResponseBody
    public List<Tb_user> getNextSpman(String nodeId,String organid) {

        return electronApproveService.getNextSpman(nodeId,organid,"");
    }

    @RequestMapping("/setQxAddSubmit")
    @ResponseBody
    public ExtMsg setQxAddSubmit(String taskid,String[] dataids,String lyqx) {
        electronApproveService.setQxAddSubmit( taskid, dataids, lyqx);
        return new ExtMsg(true,"",null);
    }


    @RequestMapping("/approveSubmit")
    @ResponseBody
    public ExtMsg approveSubmit(String textArea,String nextNode,String nextSpman,String taskid,String nodeId,int borrowtyts,String sendMsg) {
        String hintText = stApproveService.approveSubmit(textArea,nextNode,nextSpman,taskid,nodeId,borrowtyts);
        if("审批完成".equals(hintText)){
            //stApproveService.setEntryIndexKc(taskid);
            webSocketService.noticeRefresh();
            Tb_user spuser = userRepository.findByUserid(nextSpman);
            String returnStr = "";
            if(sendMsg!=null&&"true".equals(sendMsg)&&spuser!=null){   //短信提醒
                try {
                    returnStr = smsService.SendSMS(spuser.getPhone(),"您有一条档案系统的待办审批，请登录档案系统管理平台及时处理！");
                }catch (Exception e){
                    e.printStackTrace();
                    return new ExtMsg(true,hintText+"，短信发送失败",null);
                }
            }
            if("".equals(returnStr)){
                return new ExtMsg(true,hintText,null);
            }else{
                return new ExtMsg(true,hintText+"，短信发送结果为："+returnStr,null);
            }
        }
        return new ExtMsg(false,"".equals(hintText)?"操作失败":hintText,null);
    }

    @RequestMapping("/setlyqx")
    @ResponseBody
    public ExtMsg setlyqx(String taskid,String[] dataids,String lyqx) {
		electronApproveService.setQxAddSubmit(taskid, dataids, lyqx);
        return new ExtMsg(true,"",null);
    }

    @RequestMapping("/returnBorrow")
    @ResponseBody
    public ExtMsg returnBorrow(String textarea,String taskid,String nodeId){
        electronApproveService.returnBorrow(textarea,taskid,nodeId);
        webSocketService.noticeRefresh();
        return new ExtMsg(true,"",null);
    }

    @RequestMapping("/nofindBorrow")
    @ResponseBody
    public ExtMsg nofindBorrow(String textarea,String taskid,String nodeId){
        electronApproveService.nofindBorrow(textarea,taskid,nodeId);
        webSocketService.noticeRefresh();
        return new ExtMsg(true,"",null);
    }

    @RequestMapping("/deleteTask")
    @ResponseBody
    public ExtMsg deleteTask(String taskid){
        int count = stApproveService.deleteTask(taskid);
        if(count>0){
            return new ExtMsg(true,"删除成功",null);
        }
        return new ExtMsg(false,"删除失败",null);

    }

    @RequestMapping("/getWorkText")
    @ResponseBody
    public ExtMsg getWorkText(String taskid){
        boolean flag = stApproveService.getWorkText(taskid);
        if(flag){
            return new ExtMsg(true,"",flag);
        }
        return new ExtMsg(false,"",flag);
    }

    @RequestMapping("/searchAdd")
    @ResponseBody
    public ExtMsg searchAdd(String[] dataids, String taskid,String type) {
        String flag = stApproveService.searchAdd(dataids, taskid,type);
        if (flag.startsWith("导入失败")) {
            return new ExtMsg(false, flag, null);
        } else{
            return new ExtMsg(true, flag + "", null);
        }
    }

    @RequestMapping("/deleteEntries")
    @ResponseBody
    public ExtMsg deleteEntries(String[] dataids, String taskid) {
        int deleteNum = stApproveService.deleteEntries(dataids, taskid);
        return new ExtMsg(deleteNum > 0, "", null);
    }
}