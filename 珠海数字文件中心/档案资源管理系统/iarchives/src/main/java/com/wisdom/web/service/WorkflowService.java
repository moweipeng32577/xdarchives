package com.wisdom.web.service;

import com.wisdom.secondaryDataSource.entity.Tb_node_sx;
import com.wisdom.secondaryDataSource.entity.Tb_user_node_sx;
import com.wisdom.secondaryDataSource.entity.Tb_work_sx;
import com.wisdom.secondaryDataSource.repository.SxNodeRepository;
import com.wisdom.secondaryDataSource.repository.SxUserNodeRepository;
import com.wisdom.secondaryDataSource.repository.SxWorkRepository;
import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.repository.UserNodeTempRepository;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;

import com.wisdom.web.security.SlmRuntimeEasy;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yl on 2017/10/26.
 */
@Service
@Transactional
public class WorkflowService {
	
    @Autowired
    WorkRepository workRepository;

    @Autowired
    NodeRepository nodeRepository;

    @Autowired
    OrganService organService;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserNodeRepository userNodeRepository;
    
    @Autowired
    UserNodeTempRepository userNodeTempRepository;

    @Autowired
    SlmRuntimeEasy slmRuntimeEasy;

    @Autowired
    SxWorkRepository sxWorkRepository;
    @Autowired
    SxNodeRepository sxnodeRepository;
    @Autowired
    SxUserNodeRepository sxUserNodeRepository;

    @Autowired
    RightOrganRepository rightOrganRepository;

    public List<ExtNcTree> getFindAllWorkflow(){
        List<ExtNcTree> list = new ArrayList<>();
        List<Tb_work> works = workRepository.findByWorkidIsNotNullOrderBySortsequence();
        for(Tb_work work:works){
            if(!slmRuntimeEasy.hasPlatform() && (work.getText().equals("查档审批") || work.getText().equals("实体查档审批"))){
                continue;
            }
            ExtNcTree tree = new ExtNcTree();
            tree.setFnid(work.getId());
            tree.setLeaf(true);
            tree.setText(work.getText());
            list.add(tree);
        }
        return list;
    }

    public List<ExtNcTree> getFindAllSxWorkflow(){
        List<ExtNcTree> list = new ArrayList<>();
        List<Tb_work_sx> works = sxWorkRepository.findByWorkidIsNotNullOrderBySortsequence();
        for(Tb_work_sx work:works){
            if(!slmRuntimeEasy.hasPlatform() && (work.getText().equals("查档审批") || work.getText().equals("实体查档审批"))){
                continue;
            }
            ExtNcTree tree = new ExtNcTree();
            tree.setFnid(work.getId());
            tree.setLeaf(true);
            tree.setText(work.getText());
            list.add(tree);
        }
        return list;
    }

    public Page<Tb_node> getWorkNode(String work_id, int page, int limit){
        PageRequest pageRequest = new PageRequest(page-1,limit, new Sort("sortsequence"));
        Specifications specifications = Specifications.where(new SpecificationUtil("workid","equal",work_id));
        Page<Tb_node> nodes = nodeRepository.findAll(specifications,pageRequest);
//        List<Tb_node> nodes = nodeRepository.findByWorkidOrderByOrders(work_id);
//        List<Tb_node> nodes1 = new ArrayList<>();
//        for(int i=0;i<nodes.size();i++){
//            if(!"1".equals(nodes.get(i).getNextid())){
//                String[] nextids = nodes.get(i).getNextid().split(",");
//                String[] texts = GainField.getFieldValues(nodeRepository.findByNextidIn(nextids),"text");
//                nodes.get(i).setNextid(texts.toString());
//                Tb_node node = new Tb_node();
//                node.setId(nodes.get(i).getId());
//                node.setText(nodes.get(i).getText());
//                node.setNextid(texts.toString());
//                node.setDesc(nodes.get(i).getDesc());
//                node.setOrders(nodes.get(i).getOrders());
//                nodes1.add(node);
//            }else{
//                nodes1.add(nodes.get(i));
//            }
//        }
        return nodes;
    }

    public Page<Tb_node> getSxWorkNode(String work_id, int page, int limit){
        PageRequest pageRequest = new PageRequest(page-1,limit, new Sort("sortsequence"));
        Specifications specifications = Specifications.where(new SpecificationUtil("workid","equal",work_id));
        Page<Tb_node> nodes = sxnodeRepository.findAll(specifications,pageRequest);
        return nodes;
    }

    //获取勾选需调序的点
    public Page<Tb_node> getWorkNodeBySequence(String workid, int[] sortSequence,int page, int limit){
        PageRequest pageRequest = new PageRequest(page-1,limit, new Sort("sortsequence"));
        return nodeRepository.findBySortsequence(workid,sortSequence,pageRequest);
    }

    //获取勾选需调序的点 声像
    public Page<Tb_node> getSxWorkNodeBySequence(String workid, int[] sortSequence,int page, int limit){
        PageRequest pageRequest = new PageRequest(page-1,limit, new Sort("sortsequence"));
        Page<Tb_node_sx> pageList = sxnodeRepository.findBySortsequence(workid,sortSequence,pageRequest);
        List<Tb_node_sx> nodeSxList=pageList.getContent();
        List<Tb_node> nodeList=new ArrayList<>();
        if(nodeSxList.size()>0){
            for(Tb_node_sx nodeSx: nodeSxList){
                Tb_node node=new Tb_node();
                BeanUtils.copyProperties(nodeSx,node);
                nodeList.add(node);
            }
        }
        return new  PageImpl(nodeList, pageRequest, nodeSxList.size());
    }

    //节点上下调序 currentcount被选中的节点
    public void findNodeBySortsequence(int[] sortSequences,String work_id,int currentcount,String operate) {
        //被选中的节点
        List<Tb_node> nodeList = nodeRepository.findBySortsequence(work_id,sortSequences);
        //workid下的所有节点
        List<Tb_node> allNodeList = nodeRepository.findNodeByWorkid(work_id);
        //上调
        if("up".equals(operate)){
            Tb_node upnode = nodeList.get(currentcount);
            Tb_node downnode = nodeList.get(currentcount-1);
            int sortSequenceUp = upnode.getOrders();
            int sortSequenceDown = downnode.getOrders();
            if(sortSequenceUp < sortSequenceDown){//判断保证sortSequenceUp > sortSequenceDown
                int temp = sortSequenceUp;
                sortSequenceUp = sortSequenceDown;
                sortSequenceDown = temp;
                Tb_node tnode = upnode;
                upnode = downnode;
                downnode = tnode;
            }
            //分两种情况 sortSequence相邻与不相邻 两种情况的处理不同
            //两节点的顺序不相邻
            if(Math.abs(upnode.getOrders()-downnode.getOrders()) > 1){//绝对值判断
                //上
                Tb_node u1 = allNodeList.get(sortSequenceDown-2);
                Tb_node d1 = allNodeList.get(sortSequenceDown);
                u1.setNextid(upnode.getId());
                u1.setNexttext(upnode.getText());
                upnode.setNextid(d1.getId());
                upnode.setNexttext(d1.getText());
                //下
                Tb_node u2 = allNodeList.get(sortSequenceUp-2);
                Tb_node d2 = allNodeList.get(sortSequenceUp);
                u2.setNextid(downnode.getId());
                u2.setNexttext(downnode.getText());
                downnode.setNextid(d2.getId());
                downnode.setNexttext(d2.getText());
                //upnode 与 downnode 的次序互换
                int sortSequence = upnode.getOrders();
                upnode.setOrders(downnode.getOrders());
                downnode.setOrders(sortSequence);
            }else{//两节点的顺序相邻
                Tb_node up = allNodeList.get(sortSequenceDown-2);
                Tb_node down = allNodeList.get(sortSequenceUp);
                up.setNextid(upnode.getId());
                up.setNexttext(upnode.getText());
                upnode.setNextid(downnode.getId());
                upnode.setNexttext(downnode.getText());
                downnode.setNextid(down.getId());
                downnode.setNexttext(down.getText());
                //upnode 与 downnode 的次序交换
                int sortSequence = upnode.getOrders();
                upnode.setOrders(downnode.getOrders());
                downnode.setOrders(sortSequence);
            }
        }else{  //下调
            Tb_node upnode = nodeList.get(currentcount+1);
            Tb_node downnode = nodeList.get(currentcount);
            int sortSequenceUp = upnode.getOrders();
            int sortSequenceDown =downnode.getOrders();
            if(sortSequenceUp < sortSequenceDown){//判断保证sortSequenceUp > sortSequenceDown
                int temp = sortSequenceUp;
                sortSequenceUp = sortSequenceDown;
                sortSequenceDown = temp;
                Tb_node tnode = upnode;
                upnode = downnode;
                downnode = tnode;
            }
            //分两种情况 sortSequence相邻与不相邻 两种情况的处理不同
            //两节点顺序不相邻
            if(Math.abs(downnode.getOrders()-upnode.getOrders()) > 1){//绝对值判断
                //上
                Tb_node u1 = allNodeList.get(sortSequenceDown-2);
                Tb_node d1 = allNodeList.get(sortSequenceDown);
                u1.setNextid(upnode.getId());
                u1.setNexttext(upnode.getText());
                upnode.setNextid(d1.getId());
                upnode.setNexttext(d1.getText());
                //下
                Tb_node u2 = allNodeList.get(sortSequenceUp-2);
                Tb_node d2 = allNodeList.get(sortSequenceUp);
                u2.setNextid(downnode.getId());
                u2.setNexttext(downnode.getText());
                downnode.setNextid(d2.getId());
                downnode.setNexttext(d2.getText());
                //upnode 与 down 的次序互换
                int sortSequnce = upnode.getOrders();
                upnode.setOrders(downnode.getOrders());
                downnode.setOrders(sortSequnce);
            }else{//两节点顺序相邻
                Tb_node up = allNodeList.get(sortSequenceDown-2);
                Tb_node down = allNodeList.get(sortSequenceUp);
                up.setNextid(upnode.getId());
                up.setNexttext(upnode.getText());
                upnode.setNextid(downnode.getId());
                upnode.setNexttext(downnode.getText());
                downnode.setNextid(down.getId());
                downnode.setNexttext(down.getText());
                //upnode 与 downnode 的次序交换
                int sortSequence = upnode.getOrders();
                upnode.setOrders(downnode.getOrders());
                downnode.setOrders(sortSequence);
            }
            nodeRepository.flush();
        }
    }

    //节点上下调序 currentcount被选中的节点  声像
    public void findSxNodeBySortsequence(int[] sortSequences,String work_id,int currentcount,String operate) {
        //被选中的节点
        List<Tb_node_sx> nodeList = sxnodeRepository.findBySortsequence(work_id,sortSequences);
        //workid下的所有节点
        List<Tb_node_sx> allNodeList = sxnodeRepository.findNodeByWorkid(work_id);
        //上调
        if("up".equals(operate)){
            Tb_node_sx upnode = nodeList.get(currentcount);
            Tb_node_sx downnode = nodeList.get(currentcount-1);
            int sortSequenceUp = upnode.getOrders();
            int sortSequenceDown = downnode.getOrders();
            if(sortSequenceUp < sortSequenceDown){//判断保证sortSequenceUp > sortSequenceDown
                int temp = sortSequenceUp;
                sortSequenceUp = sortSequenceDown;
                sortSequenceDown = temp;
                Tb_node_sx tnode = upnode;
                upnode = downnode;
                downnode = tnode;
            }
            //分两种情况 sortSequence相邻与不相邻 两种情况的处理不同
            //两节点的顺序不相邻
            if(Math.abs(upnode.getOrders()-downnode.getOrders()) > 1){//绝对值判断
                //上
                Tb_node_sx u1 = allNodeList.get(sortSequenceDown-2);
                Tb_node_sx d1 = allNodeList.get(sortSequenceDown);
                u1.setNextid(upnode.getId());
                u1.setNexttext(upnode.getText());
                upnode.setNextid(d1.getId());
                upnode.setNexttext(d1.getText());
                //下
                Tb_node_sx u2 = allNodeList.get(sortSequenceUp-2);
                Tb_node_sx d2 = allNodeList.get(sortSequenceUp);
                u2.setNextid(downnode.getId());
                u2.setNexttext(downnode.getText());
                downnode.setNextid(d2.getId());
                downnode.setNexttext(d2.getText());
                //upnode 与 downnode 的次序互换
                int sortSequence = upnode.getOrders();
                upnode.setOrders(downnode.getOrders());
                downnode.setOrders(sortSequence);
            }else{//两节点的顺序相邻
                Tb_node_sx up = allNodeList.get(sortSequenceDown-2);
                Tb_node_sx down = allNodeList.get(sortSequenceUp);
                up.setNextid(upnode.getId());
                up.setNexttext(upnode.getText());
                upnode.setNextid(downnode.getId());
                upnode.setNexttext(downnode.getText());
                downnode.setNextid(down.getId());
                downnode.setNexttext(down.getText());
                //upnode 与 downnode 的次序交换
                int sortSequence = upnode.getOrders();
                upnode.setOrders(downnode.getOrders());
                downnode.setOrders(sortSequence);
            }
        }else{  //下调
            Tb_node_sx upnode = nodeList.get(currentcount+1);
            Tb_node_sx downnode = nodeList.get(currentcount);
            int sortSequenceUp = upnode.getOrders();
            int sortSequenceDown =downnode.getOrders();
            if(sortSequenceUp < sortSequenceDown){//判断保证sortSequenceUp > sortSequenceDown
                int temp = sortSequenceUp;
                sortSequenceUp = sortSequenceDown;
                sortSequenceDown = temp;
                Tb_node_sx tnode = upnode;
                upnode = downnode;
                downnode = tnode;
            }
            //分两种情况 sortSequence相邻与不相邻 两种情况的处理不同
            //两节点顺序不相邻
            if(Math.abs(downnode.getOrders()-upnode.getOrders()) > 1){//绝对值判断
                //上
                Tb_node_sx u1 = allNodeList.get(sortSequenceDown-2);
                Tb_node_sx d1 = allNodeList.get(sortSequenceDown);
                u1.setNextid(upnode.getId());
                u1.setNexttext(upnode.getText());
                upnode.setNextid(d1.getId());
                upnode.setNexttext(d1.getText());
                //下
                Tb_node_sx u2 = allNodeList.get(sortSequenceUp-2);
                Tb_node_sx d2 = allNodeList.get(sortSequenceUp);
                u2.setNextid(downnode.getId());
                u2.setNexttext(downnode.getText());
                downnode.setNextid(d2.getId());
                downnode.setNexttext(d2.getText());
                //upnode 与 down 的次序互换
                int sortSequnce = upnode.getOrders();
                upnode.setOrders(downnode.getOrders());
                downnode.setOrders(sortSequnce);
            }else{//两节点顺序相邻
                Tb_node_sx up = allNodeList.get(sortSequenceDown-2);
                Tb_node_sx down = allNodeList.get(sortSequenceUp);
                up.setNextid(upnode.getId());
                up.setNexttext(upnode.getText());
                upnode.setNextid(downnode.getId());
                upnode.setNexttext(downnode.getText());
                downnode.setNextid(down.getId());
                downnode.setNexttext(down.getText());
                //upnode 与 downnode 的次序交换
                int sortSequence = upnode.getOrders();
                upnode.setOrders(downnode.getOrders());
                downnode.setOrders(sortSequence);
            }
            sxnodeRepository.flush();
        }
        sxnodeRepository.save(nodeList);
        sxnodeRepository.save(allNodeList);
    }

    public Tb_work findByWorkid(String work_id) {
        return workRepository.findByWorkid(work_id);
    }

    public Tb_work_sx findBySxWorkid(String work_id) {
        return sxWorkRepository.findByWorkid(work_id);
    }

    public Integer updateWorkflowUrging(String work_id,String state,String type) {
        if("urging".equals(type)){  //催办
            return workRepository.updateUrgingByid(work_id,state);
        }else{  //短信通知
            return workRepository.updateSendmsgByid(work_id,state);
        }
    }

    /**
     * 新增环节
     * @param workid 流程类型id
     * @param node 新增环节实例
     * @return
     */
    /*public Tb_node workflowAddSubmit(String workid,Tb_node node){
        PageRequest pageRequest = new PageRequest(0,2);
        Page<Tb_node> pages = nodeRepository.findByWorkidOrderBySortsequenceDesc(pageRequest,workid);//获取上一个及下一个环节
        Tb_node node1 = pages.getContent().get(0);
        Tb_node node2 = pages.getContent().get(1);
        node.setOrders(node1.getOrders());
        node.setNextid(node1.getId());
        node.setNexttext(node1.getText());
        node.setWorkid(workid);
        node = nodeRepository.save(node);//保存
        nodeRepository.updateNodeById(node1.getNextid(),node1.getNexttext(),node1.getOrders()+1,node1.getDesci(),node1.getId());//更新上一节点
        nodeRepository.updateNodeById(node.getId(),node.getText(),node2.getOrders(),node2.getDesci(),node2.getId());
        return node;
    }*/

    /**
     * 环节节点修改
     * @param node
     * @return
     */
    public Tb_node workflowAddSubmit(String workid,Tb_node node){   //workId为树节点
        Tb_node node1=nodeRepository.findByWorkidAndSortsequence(workid,node.getOrders()-1);//上一节点
        Tb_node node2=nodeRepository.findByWorkidAndSortsequence(workid,node.getOrders());//下一节点
        //修改后面节点的顺序 desci+1 后移一位
        nodeRepository.updateNodeByOrders(workid,node.getOrders());

        //增加新节点
        node.setWorkid(workid);
        node.setNextid(node2.getId());
        node.setNexttext(node2.getText());
        node = nodeRepository.save(node);
        //修改上一节点并更新
        node1.setNextid(node.getId());
        node1.setNexttext(node.getText());
        nodeRepository.save(node1);
        //修改后面顺序节点的顺序 desci+1
        //nodeRepository.updateNodeByOrders(workid,node.getOrders());
        return node;
    }

    /**
     * 环节节点修改 声像
     * @param node
     * @return
     */
    public Tb_node_sx workflowSxAddSubmit(String workid,Tb_node_sx node){   //workId为树节点
        Tb_node_sx node1=  sxnodeRepository.findByWorkidAndSortsequence(workid,node.getOrders()-1);//上一节点
        Tb_node_sx node2=sxnodeRepository.findByWorkidAndSortsequence(workid,node.getOrders());//下一节点
        //修改后面节点的顺序 desci+1 后移一位
        sxnodeRepository.updateNodeByOrders(workid,node.getOrders());

        //增加新节点
        node.setWorkid(workid);
        node.setNextid(node2.getId());
        node.setNexttext(node2.getText());
        node = sxnodeRepository.save(node);
        //修改上一节点并更新
        node1.setNextid(node.getId());
        node1.setNexttext(node.getText());
        sxnodeRepository.save(node1);
        //修改后面顺序节点的顺序 desci+1
        //nodeRepository.updateNodeByOrders(workid,node.getOrders());
        return node;
    }

    public Tb_node nodeEdit(String id){
        return nodeRepository.findByNodeid(id);
    }

    public Tb_node_sx nodeSxEdit(String id){
        return sxnodeRepository.findByNodeid(id);
    }

    /**
     * 环节节点修改
     * @param node
     * @return
     */
    public int workflowEditSubmit(Tb_node node){
        try{
            Tb_node node2 = nodeRepository.findByNodeid(node.getId());
            List<Tb_node> nodes = nodeRepository.findByWorkidOrderBySortsequence(node.getWorkid());
            for(Tb_node node1:nodes){
                if(node1.getId().equals(node2.getId())){
                    node1.setText(node.getText());
                    node1.setDesci(node.getDesci());
                }
                String nodeText = node1.getNexttext()==null?"":node1.getNexttext();
                node1.setNexttext(nodeText.replace(node2.getText(),node.getText()));
            }
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 环节节点修改  声像
     * @param node
     * @return
     */
    public int workflowSxEditSubmit(Tb_node node){
        try{
            Tb_node_sx node2 = sxnodeRepository.findByNodeid(node.getId());
            List<Tb_node_sx> nodes = sxnodeRepository.findByWorkidOrderBySortsequence(node.getWorkid());
            for(Tb_node_sx node1:nodes){
                if(node1.getId().equals(node2.getId())){
                    node1.setText(node.getText());
                    node1.setDesci(node.getDesci());
                }
                String nodeText = node1.getNexttext()==null?"":node1.getNexttext();
                node1.setNexttext(nodeText.replace(node2.getText(),node.getText()));
            }
            sxnodeRepository.save(nodes);
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 删除环节节点
     * @param nodeid 环节id
     * @return
     */
    public int nodeDel(String nodeid){
        Tb_node node = nodeRepository.findByNodeid(nodeid);
        List<Tb_node> nodes = nodeRepository.findByWorkidOrderBySortsequence(node.getWorkid());
        int j = nodeRepository.deleteByNodeid(nodeid);//删除环节
        userNodeRepository.deleteByNodeid(nodeid);//删除环节用户对应数据
        int orders = 1;
        for(int i=0;i<nodes.size();i++){//更新相邻环节
            Tb_node node1  = nodes.get(i);
            if(node.getId().equals(node1.getId())){
                continue;
            }
            if(!"结束".equals(node1.getText())){
                String[] nextIds = node1.getNextid().split(",");
                if(node1.getNextid().indexOf(node.getId())>-1){
                    String nextId = (node1.getNextid()==null?"":node1.getNextid()).replace(","+node.getId(),"");
                    nextId = nextId.replace(node.getId()+",","");
                    nextId = nextId.replace(node.getId(),"");
                    node1.setNextid(nextId);
                }
                List<Tb_node> nodes2 = nodeRepository.findByNodeidIn(nextIds);
                String nodeText = "";
                for(Tb_node node2 : nodes2){
                    nodeText += ","+node2.getText();
                }
                if(!"".equals(nodeText)){
                    nodeText = nodeText.substring(1);
                }else{
                    if(nodes.size()>3){
                        nodeText = nodes.get(i+2).getText();
                        node1.setNextid(nodes.get(i+2).getId());
                    }
                }
                node1.setNexttext(nodeText);
            }
            node1.setOrders(orders);
            orders++;
        }
        return j;
    }

    /**
     * 删除环节节点 声像
     * @param nodeid 环节id
     * @return
     */
    public int nodeSxDel(String nodeid){
        Tb_node_sx node = sxnodeRepository.findByNodeid(nodeid);
        List<Tb_node_sx> nodes = sxnodeRepository.findByWorkidOrderBySortsequence(node.getWorkid());
        int j = sxnodeRepository.deleteByNodeid(nodeid);//删除环节
        sxUserNodeRepository.deleteByNodeid(nodeid);//删除环节用户对应数据
        int orders = 1;
        for(int i=0;i<nodes.size();i++){//更新相邻环节
            Tb_node_sx node1  = nodes.get(i);
            if(node.getId().equals(node1.getId())){
                continue;
            }
            if(!"结束".equals(node1.getText())){
                String[] nextIds = node1.getNextid().split(",");
                if(node1.getNextid().indexOf(node.getId())>-1){
                    String nextId = (node1.getNextid()==null?"":node1.getNextid()).replace(","+node.getId(),"");
                    nextId = nextId.replace(node.getId()+",","");
                    nextId = nextId.replace(node.getId(),"");
                    node1.setNextid(nextId);
                }
                List<Tb_node_sx> nodes2 = sxnodeRepository.findByNodeidIn(nextIds);
                String nodeText = "";
                for(Tb_node_sx node2 : nodes2){
                    nodeText += ","+node2.getText();
                }
                if(!"".equals(nodeText)){
                    nodeText = nodeText.substring(1);
                }else{
                    if(nodes.size()>3){
                        nodeText = nodes.get(i+2).getText();
                        node1.setNextid(nodes.get(i+2).getId());
                    }
                }
                node1.setNexttext(nodeText);
            }
            node1.setOrders(orders);
            orders++;
            sxnodeRepository.save(node1);
        }
        return j;
    }
    
    public List<Tb_user> getWorkUser(String organid,String username) {
        List<Tb_user> userList = new ArrayList<>();
        Specification<Tb_user> specification = null;
        Specification<Tb_user> specificationUserName = null;
        if(username!=null&&!"".equals(username)){
            specificationUserName = Specifications.where(new SpecificationUtil("realname","like",username));
        }
        if(organid == null || "0".equals(organid)){
            specification = Specifications.where(new SpecificationUtil("outuserstate","isNull","")).and(specificationUserName);
            userList = userRepository.findAll(specification,new Sort(Sort.Direction.ASC,"sortsequence")); //排序
        }else{
            Tb_right_organ organ = rightOrganRepository.findByOrganid(organid);
            List<String> organidList = organService.getOrganidLoop(organid, true, new ArrayList<String>());
            organidList.add(organid);
            String[] organs = new String[organidList.size()];
            specification = userService.getUsers(organidList.toArray(organs));
            if(!"外来人员部门".equals(organ.getOrganname())){
                userList = userRepository.findAll(Specifications.where(specification).and(new SpecificationUtil("outuserstate","isNull","")).and(specificationUserName),new Sort(Sort.Direction.ASC,"sortsequence")); //排序
            }else{
                userList = userRepository.findAll(Specifications.where(specification).and(specificationUserName),new Sort(Sort.Direction.ASC,"sortsequence")); //排序
            }
        }
        List<Tb_user> returnList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            Tb_user userinfo = userList.get(i);
            if (!"安全保密管理员".equals(userinfo.getRealname()) && !"系统管理员".equals(userinfo.getRealname()) && !"安全审计员".equals(userinfo.getRealname())) {
                returnList.add(userinfo);
            }
        }
        return getCollatorList(userList);
    }

    public List<Tb_user> getOrganUserList(String organid) {
    	List<Tb_user> users = userRepository.findByOrganid(organid);
    	return getCollatorList(users);
    }
    
    public List<Tb_user> getCollatorList(List<Tb_user> userList) {
    	List<String> users = new ArrayList<String>();
        if (userList.size() > 0) {
        	for (int i = 0; i < userList.size(); i++) {
        		Tb_user user = userList.get(i);
        		if(user!=null){
                    users.add(user.getRealname()+"（"+user.getOrgan().getOrganname()+"）"+ "∪" + user.getUserid());  //显示机构名
                }
			}
        }
    	String[] strings = new String[users.size()];
        String[] arrStrings = users.toArray(strings);
        List<Tb_user> returnList = new ArrayList<>();
        for (int i = 0; i < arrStrings.length; i++) {
            String[] info = arrStrings[i].split("∪");
            Tb_user userinfo = new Tb_user();
            userinfo.setUserid(info[1]);
            userinfo.setRealname(info[0]);
            returnList.add(userinfo);
        }
        return returnList;
    }
    
    public List<String> getUseridCollatorList(List<Tb_user> userList) {
    	List<String> users = new ArrayList<String>();
        if (userList.size() > 0) {
        	for (int i = 0; i < userList.size(); i++) {
        		Tb_user user = userList.get(i);
				users.add(user.getRealname() + "∪" + user.getUserid());
			}
        }
    	String[] strings = new String[users.size()];
        String[] arrStrings = users.toArray(strings);
        // Collator 类是用来执行区分语言环境的 String 比较的，这里选择使用CHINA
// 		Comparator comparator = Collator.getInstance(java.util.Locale.CHINA);
 		// 使根据指定比较器产生的顺序对指定对象数组进行排序。
// 		Arrays.sort(arrStrings, comparator);
        List<String> userid = new ArrayList<>();
        for (int i = 0; i < arrStrings.length; i++) {
            String[] info = arrStrings[i].split("∪");
            userid.add(info[1]);
        }
        return userid;
    }
    
    /**
     * 获取环节用户
     * @param nodeid 环节id
     * @return
     */
    public List<Tb_user> getNodeUser(String nodeid){
    	// 先删除临时表中的已选用户数据
    	userNodeTempRepository.deleteByUniquetag(BatchModifyService.getUniquetag());
    	
    	List<String> userid = userNodeRepository.findUserids(nodeid);
    	List<Tb_user_node_temp> user_node_temps = new ArrayList<>();
        List<Tb_user> userList = new ArrayList<>();
    	for (int i = 0; i < userid.size(); i++) {
    		Tb_user_node_temp user_node_temp = new Tb_user_node_temp();
    		user_node_temp.setNodeid(nodeid);
    		user_node_temp.setUserid(userid.get(i));
    		user_node_temp.setUniquetag(BatchModifyService.getUniquetag());
            user_node_temp.setSortsquence(i+1);
    		user_node_temps.add(user_node_temp);
            Tb_user user = userRepository.findByUserid(userid.get(i)); //排序
            userList.add(user);
    	}
    	// 将已选用户保存至临时表中
    	userNodeTempRepository.save(user_node_temps);

        return getCollatorList(userList);
    }

    /**
     * 获取环节用户 声像
     * @param nodeid 环节id
     * @return
     */
    public List<Tb_user> getSxNodeUser(String nodeid){
        // 先删除临时表中的已选用户数据
        userNodeTempRepository.deleteByUniquetag(BatchModifyService.getUniquetag());

        List<String> userid = sxUserNodeRepository.findUserids(nodeid);
        List<Tb_user_node_temp> user_node_temps = new ArrayList<>();
        List<Tb_user> userList = new ArrayList<>();
        for (int i = 0; i < userid.size(); i++) {
            Tb_user_node_temp user_node_temp = new Tb_user_node_temp();
            user_node_temp.setNodeid(nodeid);
            user_node_temp.setUserid(userid.get(i));
            user_node_temp.setUniquetag(BatchModifyService.getUniquetag());
            user_node_temp.setSortsquence(i+1);
            user_node_temps.add(user_node_temp);
            Tb_user user = userRepository.findByUserid(userid.get(i)); //排序
            userList.add(user);
        }
        // 将已选用户保存至临时表中
        userNodeTempRepository.save(user_node_temps);

        return getCollatorList(userList);
    }

    /**
     * 设置环节用户
     * @param id 环节id
     * @return
     */
    public List setUserNode(String id){
        List<Tb_user_node> list = new ArrayList<>();
        List<String> users = userNodeTempRepository.findByNodeidAndUniquetag(id, BatchModifyService.getUniquetag());
        for(int i=0;i<users.size();i++){
            Tb_user_node ur = new Tb_user_node();
            ur.setUserid(users.get(i));
            ur.setNodeid(id);
            ur.setSortsequence(i+1); //设置序号
            list.add(ur);
        }
        userNodeRepository.deleteByNodeid(id);
        return userNodeRepository.save(list);
    }

    /**
     * 设置环节用户  声像
     * @param id 环节id
     * @return
     */
    public List setSxUserNode(String id){
        List<Tb_user_node_sx> list = new ArrayList<>();
        List<String> users = userNodeTempRepository.findByNodeidAndUniquetag(id, BatchModifyService.getUniquetag());
        for(int i=0;i<users.size();i++){
            Tb_user_node_sx ur = new Tb_user_node_sx();
            ur.setUserid(users.get(i));
            ur.setNodeid(id);
            ur.setSortsequence(i+1); //设置序号
            list.add(ur);
        }
        sxUserNodeRepository.deleteByNodeid(id);
        return sxUserNodeRepository.save(list);
    }

    /**
     * 设置环节权限
     * @param nodeid 环节id
     * @return
     */
    public List<Tb_node> getNodeQx(String nodeid){
        Tb_node node = nodeRepository.findByNodeid(nodeid);
        List<Tb_node> list = nodeRepository.findBySortsequenceGreaterThanAndWorkidOrderBySortsequence(node.getOrders(),node.getWorkid());
        for(int i=0;i<list.size();i++){
            if("启动".equals(node.getText())&&"结束".equals(list.get(i).getText())){
                list.remove(i);
            }
        }
        return list;
    }

    public List<Tb_node> getNodeUserQx(String nodeid){
        Tb_node node = nodeRepository.findByNodeid(nodeid);
        String[] nextId = node.getNextid().split(",");
        return nodeRepository.findByNodeidIn(nextId);
    }

    public List<Tb_node_sx> getSxNodeUserQx(String nodeid){
        Tb_node_sx node = sxnodeRepository.findByNodeid(nodeid);
        String[] nextId = node.getNextid().split(",");
        return sxnodeRepository.findByNodeidIn(nextId);
    }

    /**
     * 设置环节权限
     * @param id 环节id
     * @param qxs 权限数组
     */
    public void setUserNodeQx(String id,String[] qxs){
        Tb_node node = nodeRepository.findByNodeid(id);
        List<Tb_node> nodes = nodeRepository.findByNodeidIn(qxs);
        String nextText = "";
        for(Tb_node node1:nodes){
            nextText += ","+node1.getText();
        }
        String sb = "";
        for(int j = 0; j < qxs.length; j++){
            sb +=","+qxs[j];
        }

        if(!"".equals(nextText)){
            nextText = nextText.substring(1);
        }

        if(!"".equals(sb)){
            sb = sb.substring(1);
        }
        node.setNextid(sb);
        node.setNexttext(nextText);
    }

    /**
     * 设置环节权限  声像
     * @param id 环节id
     * @param qxs 权限数组
     */
    public void setSxUserNodeQx(String id,String[] qxs){
        Tb_node_sx node = sxnodeRepository.findByNodeid(id);
        List<Tb_node_sx> nodes = sxnodeRepository.findByNodeidIn(qxs);
        String nextText = "";
        for(Tb_node_sx node1:nodes){
            nextText += ","+node1.getText();
        }
        String sb = "";
        for(int j = 0; j < qxs.length; j++){
            sb +=","+qxs[j];
        }

        if(!"".equals(nextText)){
            nextText = nextText.substring(1);
        }

        if(!"".equals(sb)){
            sb = sb.substring(1);
        }
        node.setNextid(sb);
        node.setNexttext(nextText);
        sxnodeRepository.save(node);
    }

    //获取审批节点审批范围
    public ExtMsg getNodeApproveScope(String nodeid){
        Tb_node node = nodeRepository.findByNodeid(nodeid);
        if(node.getApprovescope()==null||"".equals(node.getApprovescope())){ //默认为仅本单位
            return new ExtMsg(true,"","仅本单位");
        }else{
            return new ExtMsg(true,"",node.getApprovescope());
        }
    }

    //设置审批节点审批范围
    public ExtMsg setNodeApproveScope(String nodeid,String approveScope){
        Tb_node node = nodeRepository.findByNodeid(nodeid);
        if(node!=null){
            node.setApprovescope(approveScope);
            return new ExtMsg(true,"",null);
        }
        return new ExtMsg(false,"",null);
    }
}