package com.wisdom.web.controller;

import com.wisdom.secondaryDataSource.entity.Tb_node_sx;
import com.wisdom.secondaryDataSource.entity.Tb_work_sx;
import com.wisdom.util.GainField;
import com.wisdom.util.LogAnnotation;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.MissionUserRepository;
import com.wisdom.web.repository.UserNodeTempRepository;
import com.wisdom.web.repository.UserRepository;
import com.wisdom.web.service.BatchModifyService;
import com.wisdom.web.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 工作流管理控制器
 * Created by Administrator on 2017/10/30 0030.
 */
@Controller
@RequestMapping(value = "/workflow")
public class WorkflowController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${find.sx.data}")
    private Boolean openSxData;//是否可检索声像系统的声像数据
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    UserNodeTempRepository userNodeTempRepository;
    
    @Autowired
    MissionUserRepository missionUserRepository;

    @Autowired
    WorkflowService workflowService;

    @RequestMapping("/main")
    public String index(Model model) {
        model.addAttribute("openSxData",openSxData);
        return "/inlet/workflowAdmin";
    }

    @RequestMapping("/getWorkflow")
    @ResponseBody
    public List<ExtNcTree> getWorflow(String fnid, String userId, String xtType) {
        if("声像系统".equals(xtType)){
            return workflowService.getFindAllSxWorkflow();
        }
        return workflowService.getFindAllWorkflow();
    }

    @RequestMapping("/getWorkNode")
    @ResponseBody
    public Page<Tb_node> getWorkNode(String work_id, String xtType, int page, int start, int limit) {
        logger.info("page:" + page + ";start:" + start + ";limt:" + limit);
        if("声像系统".equals(xtType)){
            return workflowService.getSxWorkNode(work_id,page,limit);
        }
        return workflowService.getWorkNode(work_id,page,limit);
    }

    @RequestMapping("/getWorkNodeBySequence")
    @ResponseBody
    public Page<Tb_node> getWorkNodeBySequence(String work_id, int[] sequence,int page, int start, int limit, String xtType) {
        logger.info("page:" + page + ";start:" + start + ";limt:" + limit);
        if("声像系统".equals(xtType)){
            return workflowService.getSxWorkNodeBySequence(work_id,sequence,page,limit);
        }else{
            return workflowService.getWorkNodeBySequence(work_id,sequence,page,limit);
        }
    }

    //节点调序
    @RequestMapping("/findNodeBySortsequence")
    @ResponseBody
    public void findNodeBySortsequence(int[] sortSequences,String work_id,int currentcount,String operate, String xtType) {
        if("声像系统".equals(xtType)){
            workflowService.findSxNodeBySortsequence(sortSequences,work_id,currentcount,operate);
        }else{
            workflowService.findNodeBySortsequence(sortSequences,work_id,currentcount,operate);
        }
    }

    //设置审批节点是否可催办
    @RequestMapping("/updateWorkflowUrging")
    @ResponseBody
    public ExtMsg updateWorkflowUrging(String workid,String state,String type) {
        if(workflowService.updateWorkflowUrging(workid,state,type)>0){
            return new ExtMsg(true,"设置成功",null);
        }
        return new ExtMsg(false,"设置失败",null);
    }

    //根据id查询审批节点
    @RequestMapping("/findByWorkId")
    @ResponseBody
    public ExtMsg findByWorkId(String workid, String xtType) {
        if("声像系统".equals(xtType)){
            Tb_work_sx work= workflowService.findBySxWorkid(workid);
            return new ExtMsg(true,"",work);
        }
        Tb_work work= workflowService.findByWorkid(workid);
        return new ExtMsg(true,"",work);
    }

    /*/**
     * 新增环节
     * @param treeid 流程类型id
     * @param node 新增环节实例
     * @return
     */
    /*@RequestMapping("/workflowAdd/workflowAddSubmit")
    @ResponseBody
    public ExtMsg workflowAddSubmit(String treeid, Tb_node node) {
        Tb_node node1 = workflowService.workflowAddSubmit(treeid,node);
        if(node1!=null){
            return new ExtMsg(true,"成功",null);
        }
        return new ExtMsg(false,"失败",null);
    }*/

    @LogAnnotation(module="系统设置-工作流维护",sites = "1",fields = "text",connect = "##名称",startDesc = "操作节点，条目详细：")
    @RequestMapping("/workflowAdd/workflowAddSubmit")
    @ResponseBody
    public ExtMsg workflowAddSubmit(String treeid, String text,String desci,int sortsequence,String xtType) {
        if("声像系统".equals(xtType)){
            Tb_node_sx node=new Tb_node_sx();
            node.setText(text);
            node.setDesci(desci);
            node.setOrders(sortsequence);
            Tb_node_sx node1 = workflowService.workflowSxAddSubmit(treeid,node);
            if(node1!=null){
                return new ExtMsg(true,"成功",null);
            }
        }else{
            Tb_node node=new Tb_node();
            node.setText(text);
            node.setDesci(desci);
            node.setOrders(sortsequence);
            Tb_node node1 = workflowService.workflowAddSubmit(treeid,node);
            if(node1!=null){
                return new ExtMsg(true,"成功",null);
            }
        }

        return new ExtMsg(false,"失败",null);
    }

    @RequestMapping("/nodeEdit")
    @ResponseBody
    public ExtMsg nodeEdit(String nodeid,String xtType) {
        if("声像系统".equals(xtType)){
            ExtMsg msg = new ExtMsg(true,"成功",workflowService.nodeSxEdit(nodeid));
            return msg;
        }else{
            ExtMsg msg = new ExtMsg(true,"成功",workflowService.nodeEdit(nodeid));
            return msg;
        }
    }

    /**
     * 修改环节
     * @param node 环节实例
     * @return
     */
    @RequestMapping("/workflowEdit/workflowEditSubmit")
    @ResponseBody
    public ExtMsg workflowEditSubmit(Tb_node node,String xtType) {
        if("声像系统".equals(xtType)){
            int i = workflowService.workflowSxEditSubmit(node);
            if(i==1){
                return new ExtMsg(true,"成功",null);
            }
        }else{
            int i = workflowService.workflowEditSubmit(node);
            if(i==1){
                return new ExtMsg(true,"成功",null);
            }
        }

        return new ExtMsg(true,"失败",null);
    }

    /**
     * 删除环节节点
     * @param nodeid 环节id
     * @return
     */
    @LogAnnotation(module="系统设置-工作流维护",sites = "1",startDesc = "删除节点，条目编号：")
    @RequestMapping("/nodeDel")
    @ResponseBody
    public ExtMsg nodeDel(String nodeid,String xtType) {
        if("声像系统".equals(xtType)){
            int i = workflowService.nodeSxDel(nodeid);
            if(i==1){
                return new ExtMsg(true,"成功",null);
            }
        }else{
            int i = workflowService.nodeDel(nodeid);
            if(i==1){
                return new ExtMsg(true,"成功",null);
            }
        }
        return new ExtMsg(true,"失败",null);
    }


    @RequestMapping("/updateSelectedUser")
    @ResponseBody
    public List<String> updateSelectedUser(String organid, String userid, String nodeid) {
    	List<Tb_user> users = workflowService.getWorkUser(organid,null);
    	List<String> userids = new ArrayList<>();
    	if (userid != null && !userid.equals("")) {
    		String[] ids = userid.split(",");
            for (int i = 0; i < users.size(); i++) {
            	userids.add(users.get(i).getUserid());
            }
            List<String> idsInfo = new ArrayList<>(Arrays.asList(ids));
            idsInfo.retainAll(userids);
            // 查找出临时表中的已选择用户
            String uniquetag = BatchModifyService.getUniquetag();
            List<String> seletedUser = userNodeTempRepository.findByNodeidAndUniquetag(nodeid, uniquetag);
            seletedUser.contains(idsInfo);
            List<Tb_user> returnUserInfo = userRepository.findByUseridIn(seletedUser);
            return workflowService.getUseridCollatorList(returnUserInfo);
    	}
        return null;
    }

    @RequestMapping("/getWorkUser")
    @ResponseBody
    public List<Tb_user> getWorkUser(String organid,String username) {
        List<Tb_user> users = workflowService.getWorkUser(organid,username);
        return users;
    }

    /**
     * 获取环节用户
     * @param id 环节id
     * @return
     */
    @RequestMapping("/getNodeUser")
    @ResponseBody
    public ExtMsg getNodeUser(String id,String xtType) {
        if("声像系统".equals(xtType)){
            List<Tb_user> users = workflowService.getSxNodeUser(id);
            return new ExtMsg(true, "成功", GainField.getFieldValues(users,"userid"));
        }else{
            List<Tb_user> users = workflowService.getNodeUser(id);
            return new ExtMsg(true, "成功", GainField.getFieldValues(users,"userid"));
        }
    }
    
    /**
     * 设置环节用户
     * @param nodeid 环节id
     * @return
     */
    @RequestMapping("/setUserNode")
    @ResponseBody
    public ExtMsg setUserNode(String nodeid,String xtType) {
        if("声像系统".equals(xtType)){
            List list = workflowService.setSxUserNode(nodeid);
            if(list!=null){
                return new ExtMsg(true,"设置环节用户成功！",null);
            }
        }else{
            List list = workflowService.setUserNode(nodeid);
            if(list!=null){
                return new ExtMsg(true,"设置环节用户成功！",null);
            }
        }
        return new ExtMsg(false,"设置环节用户失败！",null);
    }

    /**
     * 设置环节权限
     * @param nodeid 环节id
     * @return
     */
    @RequestMapping("/getNodeQx")
    @ResponseBody
    public List<Tb_node> getNodeQx(String nodeid) {
        List<Tb_node> nodes = workflowService.getNodeQx(nodeid);
        return nodes;
    }

    @RequestMapping("/getNodeUserQx")
    @ResponseBody
    public ExtMsg getNodeUserQx(String id,String xtType) {
        if("声像系统".equals(xtType)){
            List<Tb_node_sx> nodes = workflowService.getSxNodeUserQx(id);
            ExtMsg msg = new ExtMsg(true,"成功",GainField.getFieldValues(nodes,"id"));
            return msg;
        }else{
            List<Tb_node> nodes = workflowService.getNodeUserQx(id);
            ExtMsg msg = new ExtMsg(true,"成功",GainField.getFieldValues(nodes,"id"));
            return msg;
        }
    }

	/**
	 * 设置环节权限
	 * 
	 * @param nodeid
	 *            环节id
	 * @param qxs
	 *            权限id数组
	 * @return
	 */
	@RequestMapping("/setUserNodeQx")
	@ResponseBody
	public ExtMsg setUserNodeQx(String nodeid, String[] qxs,String xtType) {
        if("声像系统".equals(xtType)){
            workflowService.setSxUserNodeQx(nodeid, qxs);
            return new ExtMsg(true, "环节权限设置成功！", null);
        }else{
            workflowService.setUserNodeQx(nodeid, qxs);
            return new ExtMsg(true, "环节权限设置成功！", null);
        }
	}

    //获取审批节点审批范围
    @RequestMapping("/getNodeApproveScope")
    @ResponseBody
    public ExtMsg getNodeApproveScope(String id) {
        return workflowService.getNodeApproveScope(id);
    }

    //设置审批节点审批范围
    @RequestMapping("/setNodeApproveScope")
    @ResponseBody
    public ExtMsg setNodeApproveScope(String id,String approveScope) {
        return workflowService.setNodeApproveScope(id,approveScope);
    }
}