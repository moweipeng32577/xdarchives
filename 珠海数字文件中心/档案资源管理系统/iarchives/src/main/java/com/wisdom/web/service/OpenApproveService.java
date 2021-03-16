package com.wisdom.web.service;

import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.DBCompatible;
import com.wisdom.util.GainField;
import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tanly on 2017/12/5 0005.
 */
@Service
@Transactional
public class OpenApproveService {
	
	@Autowired
	UserRepository userRepository;
	
    @Autowired
    OpendocRepository opendocRepository;

    @Autowired
    OpenmsgRepository openmsgRepository;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    FlowsRepository flowsRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    NodeRepository nodeRepository;
    
    @Autowired
    ElectronApproveService electronApproveService;

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    CaptureMetadataService captureMetadataService;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ClassifySearchService classifySearchService;

    @Autowired
    private ElectronicRepository electronicRepository;

    public List<Tb_opendoc> getOpendocList(String taskid) {
        taskid = String.format("%1$-36s",(String) taskid);
        return opendocRepository.getOpendocList(taskid);
    }

    public Page<Tb_entry_index_open> getEntryIndex(String taskid, int page, int limit,String condition, String operator,
                                                   String content,Sort sort) {
        taskid = String.format("%1$-36s",(String) taskid);
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        boolean sortflag = false;
        if (sort != null && sort.iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            if("archivecode".equals(order.getProperty())){
                sortflag = true;
            }
        }
        Specifications specifications = null;
        List<Tb_openmsg> openmsgList = new ArrayList<>();
        List<Tb_entry_index> entry_indexs = new ArrayList<>();
        Map<String, Tb_openmsg> map = new HashMap<String, Tb_openmsg>();
        long total = 0;
        if(content != null){
            if("result".equals(condition)||"appraisedata".equals(condition)||"firstappraiser".equals(condition)||"lastappraiser".equals(condition)){
                List<Tb_flows> flows = flowsRepository.findByTaskid(taskid);
                specifications = specifications.where(new SpecificationUtil("batchnum","equal",flows.get(0).getMsgid()));
                specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
                Page<Tb_openmsg> openmsgPage = openmsgRepository.findAll(specifications, pageRequest);
                openmsgList = openmsgPage.getContent();
                for (Tb_openmsg openmsg : openmsgList) {
                    map.put(openmsg.getEntryid().trim(), openmsg);
                }
                String[] entryids = GainField.getFieldValues(openmsgList, "entryid").length == 0 ? new String[]{""} : GainField.getFieldValues(openmsgList, "entryid");
                List<String[]> entryidAry = new InformService().subArray(entryids, 1000);// 处理ORACLE1000参数问题
                for (String[] ids : entryidAry) {
                    if(sortflag){
                        entry_indexs.addAll(entryIndexRepository.findByEntryidIn(ids,sort));
                    }else{
                        entry_indexs.addAll(entryIndexRepository.findByEntryidIn(ids));
                    }
                }
                total = openmsgPage.getTotalElements();
            }else{
                openmsgList = openmsgRepository.getOpenmsgs(taskid);
                for (int i=0;i<openmsgList.size();i++) {
                    map.put(openmsgList.get(i).getEntryid().trim(), openmsgList.get(i));
                }
                String searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "", operator);
                String sortStr ="";
                if (sort != null && sort.iterator().hasNext()) {
                    Sort.Order order = sort.iterator().next();
                    sortStr=" order by " + order.getProperty() + " " + order.getDirection();
                }
                String countSql="select count(t.entryid) from tb_entry_index t inner join tb_openmsg m on t.entryid =m.entryid where m.batchnum in (select msgid from Tb_flows where taskid='"+taskid+"') "+searchCondition;
                String sql="select t.* from tb_entry_index t inner join tb_openmsg m on t.entryid =m.entryid where m.batchnum in (select msgid from Tb_flows where taskid='"+taskid+"') "+searchCondition +sortStr;
                Query query = entityManager.createNativeQuery(sql, Tb_entry_index.class);
                query.setFirstResult((page - 1) * limit);
                query.setMaxResults(limit);
                List<Tb_entry_index> resultList = query.getResultList();
                Query couuntQuery = entityManager.createNativeQuery(countSql);
                total += Integer.parseInt(couuntQuery.getResultList().get(0) + "");
                entry_indexs.addAll(resultList);
            }
        }else{
            openmsgList = openmsgRepository.getOpenmsgs(taskid);
            for (Tb_openmsg openmsg : openmsgList) {
                map.put(openmsg.getEntryid().trim(), openmsg);
            }
            String searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "", operator);
            String sortStr = "";
            if (sort != null && sort.iterator().hasNext()) {
                Sort.Order order = sort.iterator().next();
                sortStr = " order by " + order.getProperty() + " " + order.getDirection();
            } else {
                sortStr = " order by entryretention,archivecode";
            }
            String countSql = "select count(t.entryid) from tb_entry_index t inner join tb_openmsg m on t.entryid =m.entryid where m.batchnum in (select msgid from Tb_flows where taskid='" + taskid + "') " + searchCondition;
            String sql = "select t.* from tb_entry_index t inner join tb_openmsg m on t.entryid =m.entryid where m.batchnum in (select msgid from Tb_flows where taskid='" + taskid + "') " + searchCondition + sortStr;
            Query query = entityManager.createNativeQuery(sql, Tb_entry_index.class);
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            List<Tb_entry_index> resultList = query.getResultList();
            Query couuntQuery = entityManager.createNativeQuery(countSql);
            total += Integer.parseInt(couuntQuery.getResultList().get(0) + "");
            if (resultList != null) {
                entry_indexs.addAll(resultList);
            }
        }
        List<Tb_entry_index_open> entry_index_openLists = new ArrayList<>();
        for (Tb_entry_index entry_index : entry_indexs) {
            Tb_entry_index_open entry_index_open = new Tb_entry_index_open();
            BeanUtils.copyProperties(entry_index, entry_index_open);
            Tb_openmsg openmsg = map.get(entry_index_open.getEntryid());
            entry_index_open.setResult(openmsg.getResult());
            entry_index_open.setMsgid(openmsg.getMsgid());
            entry_index_open.setAppraisedata(openmsg.getAppraisedata());
            entry_index_open.setAppraisetext(openmsg.getAppraisetext());
            entry_index_open.setFirstresult(openmsg.getFirstresult());
            entry_index_open.setFirstappraiser(openmsg.getFirstappraiser());
            entry_index_open.setLastresult(openmsg.getLastresult());
            entry_index_open.setLastappraiser(openmsg.getLastappraiser());
            entry_index_open.setLastappraisetext(openmsg.getLastappraisetext());
            entry_index_openLists.add(entry_index_open);
        }
        return new PageImpl<>(entry_index_openLists, pageRequest, total);
    }

    public List<Tb_entry_index_open> getApproveEntryId(String batchnum,String type) {
        List<Tb_openmsg> openmsgList=null;
        if("Fs".equals(type)){
            openmsgList= openmsgRepository.findByBatchnumAndFirstresult(batchnum);
        }else if("Ls".equals(type)){
            openmsgList=openmsgRepository.findByBatchnumAndLastresult(batchnum);
        }else {
            openmsgList=openmsgRepository.findByBatchnumAndFinalresult(batchnum);
        }

        String[] entryidArr=openmsgList.stream().map(item->item.getEntryid()).toArray(String[]::new);
        List<String> entryList=electronicRepository.findByEntryidIn(entryidArr).stream().map(item->item.getEntryid()).distinct().collect(Collectors.toList());
        if(entryList.size()==0){//没有可预览电子文件的条目
            return null;
        }
        openmsgList=openmsgList.stream().filter(item->entryList.contains(item.getEntryid())).collect(Collectors.toList());
        Map<String, Tb_openmsg> map = new HashMap<String, Tb_openmsg>();
        List<Tb_entry_index_open> entry_index_openLists = new ArrayList<>();

        if(openmsgList!=null) {
            for (Tb_openmsg openmsg : openmsgList) {
                map.put(openmsg.getEntryid().trim(), openmsg);
            }
            String[] entryids = GainField.getFieldValues(openmsgList, "entryid").length == 0 ? new String[]{""} : GainField.getFieldValues(openmsgList, "entryid");
            List<Tb_entry_index> entry_indexs = entryIndexRepository.findByEntryidIn(entryids, new Sort(Sort.Direction.DESC, "archivecode"));
            for (Tb_entry_index entry_index : entry_indexs) {
                Tb_entry_index_open entry_index_open = new Tb_entry_index_open();
                BeanUtils.copyProperties(entry_index, entry_index_open);
                Tb_openmsg openmsg = map.get(entry_index_open.getEntryid());
                entry_index_open.setResult(openmsg.getResult());
                entry_index_open.setMsgid(openmsg.getMsgid());
                entry_index_open.setAppraisedata(openmsg.getAppraisedata());
                entry_index_open.setAppraisetext(openmsg.getAppraisetext());
                entry_index_open.setFirstresult(openmsg.getFirstresult());
                entry_index_open.setFirstappraiser(openmsg.getFirstappraiser());
                entry_index_open.setLastresult(openmsg.getLastresult());
                entry_index_open.setLastappraiser(openmsg.getLastappraiser());
                entry_index_open.setLastappraisetext(openmsg.getLastappraisetext());
                entry_index_openLists.add(entry_index_open);
            }
        }
        return entry_index_openLists;
    }

    public void setQxAddSubmit(String msgid,String finalresult,String entryunit,String appraisedata,String appraisetext,
                               String updatetitle,String firstresult,String lastresult,String firstappraiser,String lastappraisetext,
                               String lastappraiser) {
        Tb_openmsg openmsg = openmsgRepository.findByMsgid(msgid);
        if(firstresult!=null&&!"".equals(firstresult)){
            openmsg.setResult(firstresult);
        }
        if(lastresult!=null&&!"".equals(lastresult)){
            openmsg.setResult(lastresult);
        }
        if(finalresult!=null&&!"".equals(finalresult)){
            openmsg.setResult(finalresult);
            openmsg.setFinalresult(finalresult);
        }
        openmsg.setEntryunit(entryunit);
        openmsg.setAppraisedata(appraisedata);
        openmsg.setAppraisetext(appraisetext);
        openmsg.setUpdatetitle(updatetitle);
        openmsg.setFirstresult(firstresult);
        openmsg.setLastresult(lastresult);
        openmsg.setFirstappraiser(firstappraiser);
        openmsg.setLastappraisetext(lastappraisetext);
        openmsg.setLastappraiser(lastappraiser);
        openmsgRepository.save(openmsg);
    }

    public void setZWWQxAddSubmit(String taskid, String[] dataids, String kfqx) {
        List<Tb_flows> flowss = flowsRepository.findByTaskid(taskid);
        String batchnum = flowss.get(0).getMsgid();
        List<Tb_openmsg> openmsgs = openmsgRepository.findByBatchnumInAndEntryidIn(new String[]{batchnum}, dataids);
        for (Tb_openmsg openmsg : openmsgs) {
            openmsg.setResult(kfqx);
        }
    }

    /**
     * 开放审批-完成
     * @param textArea
     * @param nextNode
     * @param nextSpman
     * @param taskid
     * @param nodeId
     */
    public void approveSubmit(String textArea, String nextNode, String nextSpman, String taskid, String nodeId) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        Tb_task task1 = taskRepository.findByTaskid(taskid);
        Tb_flows flows = flowsRepository.findByTaskidAndSpman(taskid, userDetails.getUserid());
        Tb_node node = nodeRepository.findByNodeid(nextNode);
        
        Tb_opendoc opendoc = opendocRepository.getOpendoc(taskid);
        opendoc.setApprove(textArea);
        if (!"".equals(nextSpman)) {//存在下一审批环节
            opendoc.setState(Tb_opendoc.STATE_WAIT_HANDLE);//待处理
            String nextSpmanRealname = userRepository.findByUserid(nextSpman).getRealname();
            
            // 更新审批流程信息
         	electronApproveService.updateTaskInfo(taskid, node.getText(), nextSpmanRealname, textArea, dateInt, userDetails.getUserid(), null);
            
            //创建下一环节的任务信息
            Tb_task task = new Tb_task();
            task.setState(Tb_task.STATE_WAIT_HANDLE);//处理中
            task.setTime(new Date());
            task.setLoginname(nextSpman);
            task.setText(task1.getText());
            task.setType(task1.getType());
            task.setApprovetext(node.getText());
            task.setApproveman(nextSpmanRealname);
            task.setLastid(taskid);
            Tb_task task2 = taskRepository.save(task);//下一审批人任务
            //更新上一环节的工作流信息
            flows.setState(Tb_flows.STATE_FINISHED);//完成
            flows.setDate(dateInt);//办理时间
            //创建下一环节的工作流信息
            Tb_flows flows1 = new Tb_flows();
            flows1.setNodeid(node.getId());
            flows1.setText(node.getText());
            flows1.setSpman(nextSpman);
            flows1.setTaskid(task2.getId());
            flows1.setMsgid(flows.getMsgid());
            flows1.setState(Tb_flows.STATE_HANDLE);//处理中
            flows1.setDate(dateInt);
            flowsRepository.save(flows1);//下一流程
        } else {//结束
        	//更新上一环节工作流信息
            flows.setState(Tb_flows.STATE_FINISHED);//完成
            flows.setDate(dateInt);
        	
            List<Tb_node> nodes = nodeRepository.findByWorkidOrderBySortsequence(node.getWorkid());
            Tb_node node1 = nodes.get(nodes.size() - 1);
            //完成单据审批
            Tb_flows flows1 = new Tb_flows();
            flows1.setNodeid(node1.getId());
            flows1.setText(node1.getText());
            flows1.setDate(dateInt);
            flows1.setTaskid(taskid);
            flows1.setMsgid(flows.getMsgid());
            flows1.setState(Tb_flows.STATE_FINISHED);//完成
            flowsRepository.save(flows1);

            String opendate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            List<Tb_openmsg> openmsgList = openmsgRepository.getOpenmsgs(taskid);
            for (Tb_openmsg openmsg : openmsgList) {
                if (openmsg.getResult() != null) {
                    Tb_entry_index entry_index = entryIndexRepository.findByEntryid(openmsg.getEntryid());
                    if (entry_index != null) {
                        if ("原文开放".equals(openmsg.getResult())||"条目开放".equals(openmsg.getResult())) {
                            entry_index.setFlagopen(openmsg.getResult());
                            entry_index.setOpendate(opendate);
                        }else{
                            entry_index.setFlagopen("不开放");
                            entry_index.setOpendate(null);
                        }
                        if(openmsg.getUpdatetitle()!=null&&!"".equals(openmsg.getUpdatetitle())){  //修改题名
                            entry_index.setTitle(openmsg.getUpdatetitle());
                        }
                        captureMetadataService.saveServiceMetadata(entry_index.getEntryid(),"数据开放","开放");
                    }
                }
            }
            opendoc.setState(Tb_opendoc.STATE_FINISH_AUDIT);
            opendoc.setOpendate(opendate);
            electronApproveService.updateElectroInfo(node1.getText(), userDetails.getRealname(), taskid);
            webSocketService.noticeRefresh(userDetails.getUserid());//通知所有用户更新通知
        }
    }

    /**
     * 开放审批-退回
     * @param taskid
     * @param nodeid
     * @param textArea
     */
    public void returnOpen(String taskid, String nodeid, String textArea) {
    	SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();//获取安全对象
        long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        Tb_node node = nodeRepository.findByNodeid(nodeid);
        
        electronApproveService.updateTaskInfo(taskid, node.getText(), userDetails.getRealname(), textArea, dateInt,
				userDetails.getUserid(), "开放");

        Tb_opendoc opendoc = opendocRepository.getOpendoc(taskid);
        opendoc.setState(Tb_opendoc.STATE_SEND_BACK);//已退回
        opendoc.setApprove(textArea);

//        List<Tb_flows> flows = flowsRepository.findByTaskid(taskid);
//        for (Tb_flows flow : flows) {
//            flow.setState(Tb_flows.STATE_END);//结束
//        }
        Tb_flows flows = flowsRepository.findByTaskidAndSpman(taskid, userDetails.getUserid());
        flows.setState(Tb_flows.STATE_FINISHED);//完成
        flows.setDate(dateInt);
        
        List<Tb_node> nodes = nodeRepository.findByWorkidOrderBySortsequence(node.getWorkid());
        Tb_node node1 = nodes.get(nodes.size() - 1);
        Tb_flows flows1 = new Tb_flows();
        flows1.setNodeid(node1.getId());
        flows1.setText(node1.getText());
        flows1.setDate(dateInt);
        flows1.setTaskid(taskid);
        flows1.setMsgid(flows.getMsgid());
        flows1.setState(Tb_flows.STATE_END);//结束
        flowsRepository.save(flows1);

        List<Tb_openmsg> openmsgList = openmsgRepository.getOpenmsgs(taskid);
        String[] entryids=GainField.getFieldValues(openmsgList,"entryid").length==0?new String[]{""}:GainField.getFieldValues(openmsgList,"entryid");
        entryIndexRepository.setOpenLock("",entryids);
    }

    /**
     * 退回上一环节
     *
     * @param taskid
     *            任务id
     * @param textarea
     *            批示
     */
    public void BackPreBorrowOpen(String textarea, String taskid) {
        electronApproveService.BackPreBorrowEle(textarea, taskid, "数据开放");
    }
}