package com.wisdom.web.service;

import com.wisdom.util.SolidifyThread;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2019/5/23.
 */
@Service
@Transactional
public class ElectronPrintApproveService {


    @Autowired
    TaskRepository taskRepository;

    @Autowired
    FlowsRepository flowsRepository;

    @Autowired
    NodeRepository nodeRepository;

    @Autowired
    BorrowDocRepository borrowDocRepository;

    @Autowired
    BorrowMsgRepository borrowMsgRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ElectronApproveService electronApproveService;

    @Autowired
    ElectronicPrintRepository electronicPrintRepository;



    /**
     * 审批提交
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
    public void approvePrintSubmit(String textArea, String nextNode, String nextSpman, String taskid, String nodeId,
                              int borrowtyts) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        Tb_task task1 = taskRepository.findByTaskid(taskid);
        Tb_flows flows = flowsRepository.findByTaskidAndSpman(taskid, userDetails.getUserid());// 获取当前任务流程设置状态
        Tb_node node = nodeRepository.findByNodeid(nextNode);// 拿到当前节点

        Tb_borrowdoc borrowdoc = borrowDocRepository.getBorrowDocByTaskid(taskid);// 获取任务单据设置批示
        borrowdoc.setApprove(textArea);
        borrowdoc.setBorrowtyts(borrowtyts);
        if(node.getOrders()==3){ //第一审批环节更新受理时间
            borrowdoc.setAcceptdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }
        List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.getBorrowmsgs(taskid);
        String str = "已通过";
        int i = 0;
        for (Tb_borrowmsg borrowmsg : borrowmsgs) {
            if ("拒绝".equals(borrowmsg.getLyqx()) || borrowmsg.getLyqx() == null) {
                i += 1;
            }
            borrowmsg.setJybackdate(new SimpleDateFormat("yyyyMMdd").format(new Date()));// 审批完成时间
        }
        if (i == borrowmsgs.size()) {
            str = "不通过";// 当全部数据利用权限为拒绝的时候状态为不通过
        }
        borrowdoc.setState(str);
        if (!"".equals(nextSpman)) {// 存在下一审批环节
            String nextSpmanRealname = userRepository.findByUserid(nextSpman).getRealname();
            // 更新上一个环节的任务信息
            electronApproveService.updateElectroInfo(node.getText(), nextSpmanRealname, taskid);

            // 创建下一环节的任务信息
            Tb_task task = new Tb_task();
            task.setState(Tb_task.STATE_WAIT_HANDLE);// 处理中
            task.setTime(new Date());
            task.setLoginname(nextSpman);
            task.setText(task1.getText());
            task.setType(task1.getType());
            task.setApprovetext(node.getText());
            task.setApproveman(nextSpmanRealname);
            task.setLastid(taskid);
            Tb_task task2 = taskRepository.save(task);// 下一审批人任务
            // 更新上一环节的工作流信息
            flows.setState(Tb_flows.STATE_FINISHED);
            flows.setDate(dateInt);
            // 创建下一环节的工作流信息
            Tb_flows flows1 = new Tb_flows();
            flows1.setNodeid(node.getId());
            flows1.setText(node.getText());
            flows1.setSpman(nextSpman);
            flows1.setTaskid(task2.getId());
            flows1.setMsgid(flows.getMsgid());
            flows1.setState(Tb_flows.STATE_HANDLE);// 处理中
            flows1.setDate(dateInt);
            flowsRepository.save(flows1);// 下一流程
            // 更新电子查档信息
            borrowdoc.setState(Tb_borrowdoc.STATE_FINISH_AUDIT);
            borrowdoc.setApprovetext(node.getText());// 更新下一审批环节
            borrowdoc.setApproveman(nextSpmanRealname);// 更新当前审批人
        } else { // 单据审批完毕
            // 更新上一环节工作流信息
            flows.setState(Tb_flows.STATE_FINISHED);// 完成
            flows.setDate(dateInt);

            List<Tb_node> nodes = nodeRepository.findByWorkidOrderBySortsequence(node.getWorkid());
            Tb_node node1 = nodes.get(nodes.size() - 1);
            // 完成单据审批
            Tb_flows flows1 = new Tb_flows();
            flows1.setNodeid(node1.getId());
            flows1.setText(node1.getText());
            flows1.setDate(dateInt);
            flows1.setTaskid(taskid);
            flows1.setMsgid(flows.getMsgid());
            flows1.setState(Tb_flows.STATE_FINISHED);// 完成
            flowsRepository.save(flows1);

            borrowdoc.setState(str);
            borrowdoc.setApprovetext(node1.getText());
            borrowdoc.setApproveman(userDetails.getRealname());
            borrowdoc.setClearstate("1");
            borrowdoc.setFinishtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            electronApproveService.updateElectroInfo(node.getText(), userDetails.getRealname(), taskid);
        }
        if ("已通过".equals(str)) {// 审批通过时修改条目查档天数
            for (Tb_borrowmsg borrowmsg : borrowmsgs) {
                borrowmsg.setJyts(borrowtyts);
            }
        }
    }
    /**
    * 查无此档 拒绝
    *
    * @param taskid 任务id
    * @param nodeid 节点
    * @param textarea 批示内容
    * @return
    * @throws
    */
    public void filenotfound(String taskid,String nodeid,String textarea){
        long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        Tb_task task = taskRepository.findByTaskid(taskid);// 获取任务修改任务状态
        task.setState(Tb_task.STATE_FINISHED);// 完成

        Tb_borrowdoc borrowdoc = borrowDocRepository.getBorrowDocByTaskid(taskid);// 获取单据修改状态
        borrowdoc.setState("查无此档");
        borrowdoc.setApprove(textarea);
        borrowdoc.setClearstate("1");
        borrowdoc.setFinishtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.getBorrowmsgs(taskid);// 获取查档信息修改状态
        for (Tb_borrowmsg borrowmsg : borrowmsgs) {
            borrowmsg.setLyqx("拒绝");
            borrowmsg.setState("");
        }

        List<Tb_flows> flows = flowsRepository.findByTaskid(taskid);// 获取本流程修改状态
        for (Tb_flows flow : flows) {
            flow.setState(Tb_flows.STATE_FINISHED);// 完成
        }

        Tb_node node = nodeRepository.findByNodeid(nodeid);
        List<Tb_node> nodes = nodeRepository.findByWorkidOrderBySortsequence(node.getWorkid());
        Tb_node node1 = nodes.get(nodes.size() - 1);
        Tb_flows flows1 = new Tb_flows();
        flows1.setNodeid(node1.getId());
        flows1.setText(node1.getText());
        // flows1.setSpman(user.getLoginname());
        flows1.setDate(dateInt);
        flows1.setTaskid(taskid);
        flows1.setMsgid(flows.get(0).getMsgid());
        flows1.setState(Tb_flows.STATE_FINISHED);// 完成
        flowsRepository.save(flows1);// 结束整个流程
    }

    /**
     * 退回
     *
     * @param taskid
     *            任务id
     * @param nodeid
     *            节点id
     */
    public void returnPrintApply(String textarea, String taskid, String nodeid) {
        long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        Tb_task task = taskRepository.findByTaskid(taskid);// 获取任务修改任务状态
        task.setState(Tb_task.STATE_FINISHED);// 完成

        Tb_borrowdoc borrowdoc = borrowDocRepository.getBorrowDocByTaskid(taskid);// 获取单据修改状态
        borrowdoc.setState("退回");
        borrowdoc.setApprove(textarea);
        borrowdoc.setClearstate("1");
        borrowdoc.setFinishtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.getBorrowmsgs(taskid);// 获取查档信息修改状态
        for (Tb_borrowmsg borrowmsg : borrowmsgs) {
            borrowmsg.setLyqx("拒绝");
            borrowmsg.setState("");
        }

        List<Tb_flows> flows = flowsRepository.findByTaskid(taskid);// 获取本流程修改状态
        for (Tb_flows flow : flows) {
            flow.setState(Tb_flows.STATE_FINISHED);// 完成
        }

        Tb_node node = nodeRepository.findByNodeid(nodeid);
        List<Tb_node> nodes = nodeRepository.findByWorkidOrderBySortsequence(node.getWorkid());
        Tb_node node1 = nodes.get(nodes.size() - 1);
        Tb_flows flows1 = new Tb_flows();
        flows1.setNodeid(node1.getId());
        flows1.setText(node1.getText());
        // flows1.setSpman(user.getLoginname());
        flows1.setDate(dateInt);
        flows1.setTaskid(taskid);
        flows1.setMsgid(flows.get(0).getMsgid());
        flows1.setState(Tb_flows.STATE_FINISHED);// 完成
        flowsRepository.save(flows1);// 结束整个流程
    }

    public void setPrintApproveState(String taskid, String[] entryids, String type,String setType) {
        List<Tb_flows> flowss = flowsRepository.findByTaskid(taskid);
        String borrowcode = flowss.get(0).getMsgid();
        if("setEntry".equals(setType)){
            List<Tb_borrowmsg> borrowmsgs = borrowMsgRepository.findByBorrowcodeInAndEntryidIn(new String[] { borrowcode },
                    entryids);
            for (Tb_borrowmsg borrowmsg : borrowmsgs) {
                borrowmsg.setLyqx(type);
            }
            List<Tb_electronic_print> electronic_prints = electronicPrintRepository.findByEntryidInAndBorrowcode(entryids,borrowcode);
            for (int i = 0; i < electronic_prints.size(); i++) {
                if (electronic_prints.get(i).getPrintstate()!=null&&!"".equals(electronic_prints.get(i).getPrintstate())) {
                    electronic_prints.get(i).setState(type);
                }
            }
        }else{
            List<Tb_electronic_print> electronic_prints = electronicPrintRepository.findByIdIn(entryids);
            for (int i = 0; i < electronic_prints.size(); i++) {
                    electronic_prints.get(i).setState(type);
            }
        }
    }

    public List<Tb_electronic_print> getApproveSetPrint(String entryid, String borrowcode) {
            return electronicPrintRepository.findByEntryidAndBorrowcode(entryid,borrowcode);
        }
}
