package com.wisdom.web.controller;

import com.wisdom.secondaryDataSource.entity.Tb_data_node_sx;
import com.wisdom.secondaryDataSource.entity.Tb_right_organ_sx;
import com.wisdom.util.GuavaCache;
import com.wisdom.util.GuavaUsedKeys;
import com.wisdom.util.TimeScheduled;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.DataDeleteRepository;
import com.wisdom.web.repository.DataNodeExtRepository;
import com.wisdom.web.repository.DataNodeRepository;
import com.wisdom.web.repository.FunctionRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 *  数据节点设置控制器
 * Created by tanly on 2017/10/24 0024.
 */
@Controller
@RequestMapping(value = "/nodesetting")
public class NodesettingController {
	
    @Autowired
    NodesettingService nodesettingService;
    
    @Autowired
    EntryIndexService entryIndexService;
    
    @Autowired
    DataNodeRepository dataNodeRepository;
    
    @Autowired
    OrganService organService;
    
    @Autowired
    FundsService fundsService;

    @Autowired
    FunctionRepository functionRepository;

    @Autowired
    DataNodeExtRepository dataNodeExtRepository;

    @Autowired
    DataDeleteRepository dataDeleteRepository;

    @Autowired
    TimeScheduled timeScheduled;

    @Autowired
    UserService userService;

    @Value("${system.power.opened}")
    private String powerOpen;// 权限档案过滤单位

    @Value("${find.sx.data}")
    private Boolean openSxData;//是否可检索声像系统的声像数据

    private static final String PUBLIC_REPORT_FNID = "publicreportfnid";

    @RequestMapping("/main")
    public String nodesetting(Model model) {
        model.addAttribute("openSxData",openSxData);
        return "/inlet/nodesetting";
    }

    @RequestMapping("/getNodeidByRefid")
    @ResponseBody
    public List<String> getNodeidByRefid(String refid){
        List<String> nodeids = nodesettingService.getNodeidByRefid(refid);
        return nodeids.stream().map(nodeid -> nodeid.trim()).collect(Collectors.toList());
    }

    @RequestMapping("/getClassificationByParentClassId")
    @ResponseBody
    public List<NodesettingTree> getClassificationByParentClassId(String pcid,String xtType) {
        List<NodesettingTree> nodeTrees = nodesettingService.getClassificationByParentClassId(pcid,xtType);
        return nodeTrees;
    }

    @RequestMapping("/getCheckedClassificationByParentClassId")
    @ResponseBody
    public List<ExtTree> getCheckedClassificationByParentClassId(String pcid) {
        List<ExtTree> nodeTrees = nodesettingService.getCheckedClassificationByParentClassId(pcid);
        return nodeTrees;
    }

    @RequestMapping("/getOrganByParentId")
    @ResponseBody
    public List<NodesettingTree> getOrganByParentId(String pcid,String type) {
        return nodesettingService.getOrganByParentId(pcid,type);
    }

    @RequestMapping("/getCheckedOrganByParentId")
    @ResponseBody
    public List<ExtTree> getCheckedOrganByParentId(String pcid) {
        return nodesettingService.getCheckedOrganByParentId(pcid);
    }

    @RequestMapping("/getNodeByParentId")
    @ResponseBody
    public List<NodesettingTree> getNodeByParentId(String pcid, String type,String xtType) {
        List<NodesettingTree> nodeTreeListAll = nodesettingService.getNodeByParentId(pcid, true,xtType);
        List<NodesettingTree> nodeTreeList = new ArrayList<>();
        if ("".equals(pcid)) {
            for (NodesettingTree nodetree : nodeTreeListAll) {
                if(nodetree.getNodeType()==2){//首层节点只显示分类节点
                    nodeTreeList.add(nodetree);
                }
            }
        }else{
            nodeTreeList=nodeTreeListAll;
        }
        List<NodesettingTree> nodesetting_return = new ArrayList<>();
        if (type != null && type.equals("数据开放")) {
            for (NodesettingTree nodetree : nodeTreeList) {
                if (!"未归管理".equals(nodetree.getText())) {
                    nodesetting_return.add(nodetree);
                }
            }
        } else if("template".equals(type)&&"".equals(pcid)&&!"声像系统".equals(xtType)){//增加一个库房检索列表模板
            for (NodesettingTree nodetree : nodeTreeList) {
                nodesetting_return.add(nodetree);
            }
            List<Tb_right_function> functions=functionRepository.findByFunctionname("库房系统");
            if(functions.size()>0&&"1".equals(functions.get(0).getStatus())){
                nodesetting_return.add( nodesettingService.addKfTree());
            }
        }else if(type != null && "compilation".equals(type)){
            for (NodesettingTree nodesettingTree : nodeTreeList) {
                if("编研采集".equals(nodesettingTree.getText())){
                    nodesetting_return.add(nodesettingTree);
                }
            }
        }else  if(type != null && "classifySearchDirectory".equals(type)){  //加上声像系统节点
            if(nodeTreeList!=null&&nodeTreeList.size()>0){
                for (NodesettingTree nodetree : nodeTreeList) {
                    nodetree.setTreetype("da"); //区别档案系统还是声像系统节点
                    nodesetting_return.add(nodetree);
                }
            }
            List<NodesettingTree> sxNodeTreeList = nodesettingService.getNodeByParentId(pcid, true,"声像系统");
            if(sxNodeTreeList!=null&&sxNodeTreeList.size()>0){
                for (NodesettingTree nodetree : sxNodeTreeList) {
                    nodetree.setTreetype("sx");
                    nodesetting_return.add(nodetree);
                }
            }
        } else if(type != null && "到期鉴定".equals(type)){
            timeScheduled.saveAppraisalNode();//缓存静态
            SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
            String[] nodeIds=dataDeleteRepository.findByUserId(userDetails.getUserid());
            Map<String,Integer> numberMap=(Map<String,Integer>)GuavaCache.getValueByKey(GuavaUsedKeys.APPRAISAL_NODE);
            nodeTreeList.parallelStream().forEach(nodeTree->{//根据节点获取到期鉴定数
                Integer number=numberMap.get(nodeTree.getFnid().trim());
                if(number!=null&&number>0){
                    String text=number>100?">100":number.toString();
                    nodeTree.setText("<span style='color:red;'>(" + text + ")</span>" + nodeTree.getText());
                }
                for (String nodeId : nodeIds) {//权限过滤
                    if(nodeTree.getFnid().equals(nodeId.trim())){
                        nodeTree.setText("<span style='color:red;'>"+nodeTree.getText()+"</span>");
                    }
                }
            });
            nodesetting_return.addAll(nodeTreeList);
        }else {
            for (NodesettingTree tree : nodeTreeList) {
                if(!tree.getText().equals("编研采集")){
                    nodesetting_return.add(tree);
                }
            }
        }
        return nodesetting_return;
    }

    @RequestMapping("/getNodeByParentIdReport")
    @ResponseBody
    public List<NodesettingTree> getNodeByParentIdReport(String pcid, String type) {
        List<NodesettingTree> nodeTreeList =  nodesettingService.getNodeByParentId(pcid, true,"");
        List<NodesettingTree> nodesetting_return = new ArrayList<>();
        if (type != null && type.equals("数据开放")) {
            nodesetting_return = new ArrayList<>();
            for (NodesettingTree nodetree : nodeTreeList) {
                if (!nodetree.getText().equals("未归管理")) {
                    nodesetting_return.add(nodetree);
                }
            }
        } else {
            nodesetting_return.addAll(nodeTreeList);
        }
        if("".equals(pcid)) {// 仅最外层级别的节点加载时，追加一个公共报表节点
            NodesettingTree publicReportNode = new NodesettingTree();
            publicReportNode.setText("公共报表");
            publicReportNode.setLeaf(true);
            publicReportNode.setFnid(PUBLIC_REPORT_FNID);
            publicReportNode.setCls("file");
            publicReportNode.setRoottype("classification");
            publicReportNode.setChildren(null);
            nodesetting_return.add(publicReportNode);
        }
        return nodesetting_return;
    }

    @RequestMapping("/getNodesettingKfID")
    @ResponseBody
    public List<NodesettingTree> getNodesettingKfID(String pcid) {
        if("true".equals(powerOpen)){
            return nodesettingService.getNodesettingKfID(pcid);
        }else {
            List<NodesettingTree> nodeTreeList = nodesettingService.getNodeByParentId(pcid, true,"");
            List<NodesettingTree> nodesetting_return = new ArrayList<>();
                for (NodesettingTree nodetree : nodeTreeList) {
                    if (!nodetree.getText().equals("未归管理")) {
                        nodesetting_return.add(nodetree);
                    }
                }
                return nodesetting_return;
        }
    }


    /**
     * 获取库房数据节点
     * @param pcid
     * @return
     */
    @RequestMapping("/getKfNodeByParentId")
    @ResponseBody
    public List<NodesettingTree> getKfNodeByParentId(String pcid) {
        return nodesettingService.getKfNodeByParentId(pcid);
    }


    /**
     * 数据转移获取节点信息
     * @param
     * @return
     */
	@RequestMapping("/getNodeInfo")
	@ResponseBody
	public List<NodesettingTree> getNodeInfo(String nodeinfo,String type) {
        if("0".equals(type)){//初次加载不返回数据，加快页面加载
            List<NodesettingTree> list=new ArrayList<>();
            return list;
        }
		return nodesettingService.getNodeByParentId(nodeinfo, false,"");
	}

    @RequestMapping("/getCkeckNodeByParentId")
    @ResponseBody
    public List<ExtTree> getCkeckNodeByParentId(String pcid,String userids,String xtType) {
        return nodesettingService.getCheckNodeByParentId(pcid,userids,null,xtType);
    }
    
    @RequestMapping("/getCkeckOrganByParentId")
    @ResponseBody
    public List<ExtTree> getCkeckOrganByParentId(String pcid,String userids) {
        return nodesettingService.getCkeckOrganByParentId(pcid,userids);
    }

    @RequestMapping("/changeTreeModel")
    @ResponseBody
    public List<NodesettingTree> changeTreeModel(String initModel) {
        List<NodesettingTree> nodeTrees = new ArrayList<NodesettingTree>();
        long startTime = System.currentTimeMillis();
        nodeTrees = nodesettingService.changeTreeModel(initModel);
        long endTime = System.currentTimeMillis();
        System.out.println("数据节点初始化用时：" + (float) (endTime - startTime) / 1000 + "s");
        return nodeTrees;
    }

    @RequestMapping("/changeSxTreeModel")
    @ResponseBody
    public ExtMsg changeSxTreeModel(String initModel) {
        long startTime = System.currentTimeMillis();
        //先同步档案机构表到声像系统
        List<Tb_right_organ_sx> sxList=organService.syncDaOrganToSx();
        //根据声像分类表和按档案系统更新后的的声像机构表来初始化声像数据节点
        ExtMsg newMsg = nodesettingService.changeSxTreeModel(initModel);
        //最后同步用户信息到声像系统的对应机构节点
        userService.syncUserToSx();
        long endTime = System.currentTimeMillis();
        System.out.println("声像数据节点初始化用时：" + (float) (endTime - startTime) / 1000 + "s");
        return newMsg;
    }

    @RequestMapping("/getTreeNode")
    @ResponseBody
    public ExtMsg getTreeNode(String nodeid) {
        return new ExtMsg(true, "success", nodesettingService.getTreeNode(nodeid));
    }

    @RequestMapping("/updateTreeNode")
    @ResponseBody
    public ExtMsg updateTreeNode(Tb_data_node data_node,String nodename_real){
        data_node.setNodename(nodename_real);
        Tb_data_node data_node_return = nodesettingService.updateTreeNode(data_node);
        return new ExtMsg(true, "修改成功", data_node_return);
    }

    @RequestMapping("/addTreeNode")
    @ResponseBody
    public ExtMsg addTreeNode(String nodename_real,String selectedid,String refid,String nodecode,String nodetype_real,String containchild){
        Tb_data_node data_node_return;
        if("true".equals(containchild)){
            data_node_return = nodesettingService.addTreeNode(nodename_real,selectedid,refid,nodecode,nodetype_real);
        }else{
            data_node_return = nodesettingService.addTreeNodeSingle(nodename_real,selectedid,refid,nodecode,nodetype_real);
        }
        return new ExtMsg(true, "增加节点成功", data_node_return);
    }

    @RequestMapping("/addChildTreeNode")
    @ResponseBody
    public ExtMsg addChildTreeNode(String nodename_real,String selectedid,String refid,String nodecode,String nodetype_real,String containchild){
        Tb_data_node data_node_return;
        if("true".equals(containchild)){
            data_node_return = nodesettingService.addChildTreeNode(nodename_real,selectedid,refid,nodecode,nodetype_real);
        }else{
            data_node_return = nodesettingService.addChildTreeNodeSingle(nodename_real,selectedid,refid,nodecode,nodetype_real);
        }
        return new ExtMsg(true, "增加子节点成功", data_node_return);
    }

    @RequestMapping("/deleteNode")
    @ResponseBody
    public ExtMsg deleteNode(String[] nodeid){
        nodesettingService.deleteNodeAll(nodeid);
        return new ExtMsg(true, "delete success", null);
    }

    @RequestMapping("/nodes")
    @ResponseBody
    public Page<Tb_data_node> findNodeDetailBySearch(int page, int limit, String condition, String operator, String content, String parentnodeid,String xtType, String sort) {
        Sort sortobj = WebSort.getSortByJson(sort);
        return nodesettingService.findBySearch(page, limit, condition, operator, content, parentnodeid,xtType, sortobj);
    }

    @RequestMapping("/node/{nodeid}/{targetorder}")
    @ResponseBody
    public ExtMsg modifyorder(@PathVariable String nodeid, @PathVariable String targetorder){
        Tb_data_node node = nodesettingService.getTreeNode(nodeid);
        nodesettingService.modifyNodeOrder(node, Integer.parseInt(targetorder));
        return null;
    }
    
    @RequestMapping("/findByNodeid/{nodeid}")
    @ResponseBody
    public ExtMsg findByNodeid(@PathVariable String nodeid){
    	Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
    	if (node != null) {
    		return new ExtMsg(true, "success", node);
    	}
    	return new ExtMsg(false, "failed", null);
    }
    
    @RequestMapping("/getFirstLevelNode/{nodeid}")
    @ResponseBody
    public String getFirstLevelNode(@PathVariable String nodeid,String xtType){
	    if("声像系统".equals(xtType)){
            Tb_data_node_sx node=entryIndexService.getSxNodeLevel(nodeid);
            if(node!=null){
                return node.getNodename();
            }
        }else {
            Tb_data_node node= entryIndexService.getNodeLevel(nodeid);
            if (node != null) {
                return node.getNodename();
            }
        }
    	return null;
    }
    
    /**
     * 通过数据节点获取机构名称
     * @param nodeid
     * @return
     */
    @RequestMapping(value = "/getRefid",method = RequestMethod.GET)
    @ResponseBody
    public ExtMsg getOrganidByNodeidLoop(String nodeid){
    	String organid = entryIndexService.getOrganidByNodeidLoop(nodeid);// 机构id
    	String organ = organService.findOrganByOrganid(organid);// 机构名称
    	String funds = fundsService.getOrganFunds(organid);// 全宗号
    	if (organ != null && funds != null) {
    		return new ExtMsg(true, "success", organ+","+funds);
    	} else if (organ != null && funds == null) {
    		return new ExtMsg(true, "success-organ", organ);
    	} else if (organ == null && funds != null) {
    		return new ExtMsg(true, "success-funds", funds);
    	}
    	return new ExtMsg(false, "failed", null);
    }

	@RequestMapping("/getExpandNodeById")
    @ResponseBody
    public List<NodesettingTree> getExpandNodeById(String pcid, String xtType) {
        return nodesettingService.getExpandNodeById(pcid, xtType);
    }

    @RequestMapping("/getWCLNodeByParentId")
    @ResponseBody
    public List<NodesettingTree> getWCLNodeByParentId(String pcid) {
        return nodesettingService.getWCLNodeByParentId(pcid);
    }

    @RequestMapping("/getYGDNodeByParentId")
    @ResponseBody
    public List<NodesettingTree> getYGDNodeByParentId(String pcid) {
        return nodesettingService.getYGDNodeByParentId(pcid);
    }

    @RequestMapping("/getSzhWCLNodeByParentId")
    @ResponseBody
    public List<NodesettingTree> getSzhWCLNodeByParentId(String pcid) {
        return nodesettingService.getSzhWCLNodeByParentId(pcid);
    }

    @RequestMapping("/getAssemblyOrganByParentId")
    @ResponseBody
    public List<NodesettingTree> getAssemblyOrganByParentId(String pcid) {
        return nodesettingService.getAssemblyOrganByParentId(pcid);
    }

    @RequestMapping(value = "/getChildNodeId",method = RequestMethod.GET)
    @ResponseBody
    public ExtMsg  getChildNodeId(String[] classifyId){
        List<String> childNodeId = nodesettingService.getChildNodeId(classifyId);
        return new ExtMsg(true,"ok",childNodeId);
    }

    @RequestMapping(value = "/getNodefullnameLoop",method = RequestMethod.POST)
    @ResponseBody
    public String  getNodefullnameLoop(String nodeid){
       String nodefullname= nodesettingService.getNodefullnameLoop(nodeid,"_","");
       return nodefullname;
    }


    @RequestMapping(value = "/checkMediaNodeId")
    @ResponseBody
    public int checkMediaNodeId(String nodeid) {
        Tb_data_node_mdaflag flag = dataNodeExtRepository.findNodeid(nodeid);
        if(flag != null && flag.getIs_media() == 1)
            return 1;
        else
            return 0;
    }

    @RequestMapping(value = "/checkMediaNodeIdByClassId")
    @ResponseBody
    public int checkMediaNodeIdByClassId(String classid) {
        Tb_data_node_mdaflag flag = dataNodeExtRepository.findNodeidByClassid(classid);
        if(flag != null && flag.getIs_media() == 1)
            return 1;
        else
            return 0;
    }

//    @RequestMapping(value = "/setNodeId")
//    @ResponseBody
//    public ExtMsg setNodeId(String nodeid,int isMedia) {
//        Tb_data_node_mdaflag flag = new Tb_data_node_mdaflag();
//        flag.setNodeid(nodeid);
//        flag.setIs_media(isMedia);
//        Tb_data_node_mdaflag f = dataNodeExtRepository.save(flag);
//        if(f != null)
//            return new ExtMsg(true,"设置成功",null);
//        else
//            return new ExtMsg(false,"设置失败",null);
//    }

    @RequestMapping("/updateClass")
    @ResponseBody
    @Transactional
    public ExtMsg updateClass(Tb_classification classification, int is_media) {
        dataNodeExtRepository.deleteTb_data_node_mdaflagByClassid(classification.getClassid());
        if(is_media == 1){
            dataNodeExtRepository.insertTb_data_node_mdaflagByClassid(classification.getClassid(), is_media);
            return new ExtMsg(true, "已设置为声像节点", null);
        }
        return new ExtMsg(true, "已设置为非声像节点", null);
    }

    @RequestMapping("/getCheckExpandNodeByIds")
    @ResponseBody
    public List<ExtTree> getCheckExpandNodeByIds(String pcid,String xtType) {
        return nodesettingService.getCheckExpandNodeByIds(pcid,xtType);
    }
}