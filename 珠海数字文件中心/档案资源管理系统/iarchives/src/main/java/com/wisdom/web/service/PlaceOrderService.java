package com.wisdom.web.service;

import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2020/4/28.
 */
@Service
@Transactional
public class PlaceOrderService {



    @Autowired
    PlaceOrderRepository placeOrderRepository;

    @Autowired
    PlaceManageRepository placeManageRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    NodeRepository nodeRepository;

    @Autowired
    FlowsRepository flowsRepository;

    @Autowired
    JyAdminsService jyAdminsService;

    @Autowired
    InFormRepository inFormRepository;

    @Autowired
    InFormUserRepository inFormUserRepository;

    @Autowired
    ElectronApproveService electronApproveService;

    @Autowired
    LogService logService;

    //过滤空值或null
    public String removeNull(String param){
        if(param == null || "".equals(param))
            return "";
        return param;
    }

    public Page<Tb_place_order> getUserPlaceOrder(String type,int page, int limit, String sort, String condition, String operator, String content){
        Sort sortobj = WebSort.getSortByJson(sort);
        PageRequest pageRequest = new PageRequest(page-1,limit,sortobj);
        Specifications sp = null;
        /*if("user".equals(type)){
            SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            sp = sp.where(new SpecificationUtil("submiterid","equal",userDetails.getUserid()));
        }*/
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        sp = sp.where(new SpecificationUtil("submiterid","equal",userDetails.getUserid()));
        if(content!=null){
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        return placeOrderRepository.findAll(sp,pageRequest);
    }


    //根据场地管理的placeId查找对应的场地订单的placeOrder
    public Page<Tb_place_order> getUserPlaceOrderByPlaceId(String selectTime,String type,String id,int page, int limit, String sort,
                                                           String condition, String operator, String content){
        Sort sortobj = WebSort.getSortByJson(sort);
        PageRequest pageRequest = new PageRequest(page-1,limit,sortobj);
        Calendar calendarFrist = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        Date startDate = null;
        Date endDate = null;
        String startDateStr = "";
        String endDateStr = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if("today".equals(selectTime)){  //今天00:00:00到23:59:59
            calendarFrist.set(calendarFrist.get(Calendar.YEAR), calendarFrist.get(Calendar.MONTH), calendarFrist.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            startDate = calendarFrist.getTime();
            calendarEnd.set(calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH),
                    23, 59, 59);
            endDate = calendarEnd.getTime();
        }else if("tomorrow".equals(selectTime)){  //明天00:00:00到23:59:59
            calendarFrist.add(Calendar.DAY_OF_MONTH, 1);
            calendarFrist.set(calendarFrist.get(Calendar.YEAR), calendarFrist.get(Calendar.MONTH), calendarFrist.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            startDate = calendarFrist.getTime();
            calendarEnd.add(Calendar.DAY_OF_MONTH, 1);
            calendarEnd.set(calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH),
                    23, 59, 59);
            endDate = calendarEnd.getTime();
        }else if("aftertomorrow".equals(selectTime)){  //后天00:00:00到23:59:59
            calendarFrist.add(Calendar.DAY_OF_MONTH, 2);
            calendarFrist.set(calendarFrist.get(Calendar.YEAR), calendarFrist.get(Calendar.MONTH), calendarFrist.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            startDate = calendarFrist.getTime();
            calendarEnd.add(Calendar.DAY_OF_MONTH, 2);
            calendarEnd.set(calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH),
                    23, 59, 59);
            endDate = calendarEnd.getTime();
        }else if("week".equals(selectTime)){  //今天00:00:00到7天后23:59:59
            calendarFrist.set(calendarFrist.get(Calendar.YEAR), calendarFrist.get(Calendar.MONTH), calendarFrist.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            startDate = calendarFrist.getTime();
            calendarEnd.add(Calendar.DAY_OF_MONTH, 6);
            calendarEnd.set(calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH),
                    23, 59, 59);
            endDate = calendarEnd.getTime();
        }else{
            return placeOrderRepository.findByPlaceIds(id,"提交预约",pageRequest);
        }
        startDateStr = sdf.format(startDate);
        endDateStr = sdf.format(endDate);
        return placeOrderRepository.findByPlaceIdsAndTimelimit(id,startDateStr,endDateStr,"提交预约",pageRequest);
    }

    public List<Tb_place_order> placeOrderFormSubmit(Tb_place_order placeOrder, String spnodeid, String spmanid, String placeid){
        List<Tb_place_order> placeOrders = placeOrderRepository.findByPlaceid(placeid);
        String starttime = placeOrder.getStarttime();
        String endtime = placeOrder.getEndtime();
        List<Tb_place_order> returnOrder = new ArrayList<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            Date startdate = sdf.parse(starttime);
            Date enddate = sdf.parse(endtime);
            for(Tb_place_order order : placeOrders){
                Date orderstartdate = sdf.parse(order.getStarttime());
                Date orderenddate = sdf.parse(order.getEndtime());
                //判断开始时间、结束时间段，是否已被别人预约
                if((startdate.getTime() >= orderstartdate.getTime() && startdate.getTime() < orderenddate.getTime() ||
                    enddate.getTime() <= orderenddate.getTime() && enddate.getTime() > orderstartdate.getTime()) &&
                            ("预约成功".equals(order.getState()) || "正在申请".equals(order.getState()))){
                    returnOrder.add(order);
                    logService.recordTextLog("场地预约","新增预约;预约人:"+order.getPlaceuser()+";条目id:"+order.getPlaceid());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (returnOrder.size() > 0) {
            return returnOrder;
        } else {
            SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String ordercode = UUID.randomUUID().toString().replace("-", "");// 表单号用uuid生成
            Tb_place_manage placeManage = placeManageRepository.findById(placeid);
            placeOrder.setOrdercode(ordercode);
            placeOrder.setSubmiterid(userDetails.getUserid());
            placeOrder.setFloor(placeManage.getFloor());
            long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
            Tb_user spman = userRepository.findByUserid(spmanid);
            Tb_task task = new Tb_task();
            task.setLoginname(spmanid);
            task.setState(Tb_task.STATE_WAIT_HANDLE);// 待处理
            task.setText(placeOrder.getPlaceuser() + " " + new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss").format(new Date())
                    + " 场地预约申请");
            task.setType("场地预约");
            task.setTime(new Date());
            task = taskRepository.save(task);

            // 启动完成
            Tb_node node = nodeRepository.findByNodeid(spnodeid);
            Tb_flows flows = new Tb_flows();
            flows.setText("启动");
            flows.setState(Tb_flows.STATE_FINISHED);// 完成
            flows.setDate(dateInt);
            flows.setTaskid(task.getId());
            flows.setMsgid(ordercode);
            flows.setNodeid(spnodeid);
            flowsRepository.save(flows);

            // 进入审批环节
            Tb_flows sendflows = new Tb_flows();
            sendflows.setText(node.getText());
            sendflows.setState(Tb_flows.STATE_HANDLE);// 处理中
            sendflows.setTaskid(task.getId());
            sendflows.setMsgid(ordercode);
            sendflows.setSpman(spmanid);
            sendflows.setDate(dateInt);
            sendflows.setNodeid(spnodeid);
            flowsRepository.save(sendflows);

            placeOrder.setAuditlink(node.getText());
            placeOrder.setAuditer(spman.getRealname());
            placeOrder.setState("提交预约");
            placeOrderRepository.save(placeOrder);
            return null;
        }
    }

    public Page getAuditDetails(String orderid) {
        Tb_place_order placeOrder = placeOrderRepository.findById(orderid);
        if (placeOrder != null) {
            return jyAdminsService.getCommonDealDetails(placeOrder.getOrdercode(), placeOrder.getApprove(),
                    placeOrder.getPlaceuser());
        }
        return new PageImpl(new ArrayList(), null, 0);
    }

    public int placeOrderDelete(String[] orderids){
        List<Tb_place_order> placeOrders = placeOrderRepository.findByIdIn(orderids);
        for(Tb_place_order placeOrder : placeOrders){
            List<Tb_flows> flows = flowsRepository.findByMsgid(placeOrder.getOrdercode());
            taskRepository.deleteByTaskid(flows.get(0).getTaskid());
            flowsRepository.deleteByTaskid(flows.get(0).getTaskid());
        }
        return placeOrderRepository.deleteByIdIn(orderids);
    }

    public Tb_place_order placeOrderCancelFormSubmit(String orderid,String canceluser,String canceltime,String cancelreason) throws Exception{
        Tb_place_order placeOrder = placeOrderRepository.findById(orderid);
        placeOrder.setCanceluser(canceluser);
        placeOrder.setCanceltime(canceltime);
        placeOrder.setCancelreason(cancelreason);
        placeOrder.setState("取消预约");
        Tb_inform inform = new Tb_inform();
        inform.setTitle("场地取消预约");
        inform.setText(canceluser+" 取消 "+placeOrder.getFloor()+" 在"+placeOrder.getStarttime()+"-"+placeOrder.getEndtime()+"的场地预约！");
        inform.setPosteduser("已发布");
        inform.setInformdate(new Date());// 生成时间);
        //公告截止时间为预约单的结束时间（日期的第二天0点）
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        Date endDate = ft.parse(placeOrder.getEndtime());
        Calendar c = Calendar.getInstance();
        c.setTime(endDate);
        c.add(Calendar.DAY_OF_MONTH, 1);
        endDate = c.getTime();
        inform.setLimitdate(endDate);
        inform.setPostedman(canceluser);
        inform.setPostedusergroup("未发布");
        inform = inFormRepository.save(inform);
        List<String> spmanList = flowsRepository.getFlows(placeOrder.getOrdercode());
        List<Tb_inform_user> inform_users = new ArrayList<>();
        for(String userid : spmanList){
            Tb_inform_user inform_user = new Tb_inform_user();
            inform_user.setInformid(inform.getId());
            inform_user.setUserroleid(userid);
            inform_users.add(inform_user);
        }
        inFormUserRepository.save(inform_users);
        return placeOrderRepository.save(placeOrder);
    }

    public Page<Tb_place_order> getPlaceAuditOrder(String taskid,int page, int limit, String sort, String condition, String operator, String content){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Sort sortobj = WebSort.getSortByJson(sort);
        PageRequest pageRequest = new PageRequest(page-1,limit,sortobj);
        Specifications sp = null;
        if(taskid!=null){  //通知待办事项进入审核
            Tb_flows flows = flowsRepository.getFlowsByTaskid(taskid,userDetails.getUserid(),"处理中");
            sp = sp.where(new SpecificationUtil("ordercode","equal",flows.getMsgid()));
            if(content!=null){
                sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
            }
            return placeOrderRepository.findAll(sp,pageRequest);
        }else{
            List<String> ordercodes = flowsRepository.getMsgids(userDetails.getUserid(),"待处理","场地预约");
            if(ordercodes.size()>0){
                Specification<Tb_car_order> userAuditCondition = new Specification<Tb_car_order>() {
                    @Override
                    public Predicate toPredicate(Root<Tb_car_order> root, CriteriaQuery<?> criteriaQuery,
                                                 CriteriaBuilder criteriaBuilder) {

                        CriteriaBuilder.In in = criteriaBuilder.in(root.get("ordercode"));
                        for(String str : ordercodes){
                            in.value(str);
                        }
                        return criteriaBuilder.or(in);
                    }
                };
                sp = sp.where(userAuditCondition);
                if(content!=null){
                    sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
                }
                return placeOrderRepository.findAll(sp,pageRequest);
            }
            return null;
        }
    }

    public List<Tb_node> getNodes(String orderid) {
        Tb_place_order placeOrder = placeOrderRepository.findById(orderid);
        Tb_flows flows = flowsRepository.findByMsgidAndState(placeOrder.getOrdercode(),"处理中");
        Tb_node node = nodeRepository.findByNodeid(flows.getNodeid());
        String[] nextids = node.getNextid().split(",");
        return nodeRepository.getNodes(nextids);
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
    public void auditOrderSubmit(String textArea, String nextNode, String nextSpman, String orderid,String selectApprove) throws Exception{
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        Tb_place_order placeOrder = placeOrderRepository.findById(orderid);
        Tb_flows flowNow = flowsRepository.findByMsgidAndState(placeOrder.getOrdercode(),"处理中");
        String taskid = flowNow.getTaskid();
        if("同意".equals(selectApprove)){  //审批通过
            Tb_task task1 = taskRepository.findByTaskid(taskid);
            Tb_flows flows = flowsRepository.findByTaskidAndSpman(taskid, userDetails.getUserid());// 获取当前任务流程设置状态
            Tb_node node = nodeRepository.findByNodeid(nextNode);// 拿到当前节点
            placeOrder.setApprove(textArea);
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
                // 更新预约信息
                placeOrder.setAuditlink(node.getText());// 更新下一审批环节
                placeOrder.setAuditer(nextSpmanRealname);// 更新当前审批人
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

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                Date startdate = sdf.parse(placeOrder.getStarttime());
                Date enddate = sdf.parse(placeOrder.getEndtime());
                Date nowtime = new Date();
                if(nowtime.getTime()>=startdate.getTime()&&nowtime.getTime()<=enddate.getTime()){ //当前时间是否在预约时间范围内
                    Tb_place_manage placeManage = placeManageRepository.findById(placeOrder.getPlaceid());
                    placeManage.setState("使用中");
                    placeManageRepository.save(placeManage);
                }
                placeOrder.setState("预约成功");
                placeOrder.setAuditlink(node1.getText());
                placeOrder.setReturnstate("未归还");
                placeOrder.setAuditer(userDetails.getRealname());

                electronApproveService.updateElectroInfo(node.getText(), userDetails.getRealname(), taskid);
            }
        }else{  //审批不通过
            String nodeid = flowNow.getNodeid();
            Tb_task task = taskRepository.findByTaskid(taskid);// 获取任务修改任务状态
            task.setState(Tb_task.STATE_FINISHED);// 完成

            placeOrder.setApprove(textArea);
            placeOrder.setState("预约失败");

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
            flows1.setDate(dateInt);
            flows1.setTaskid(taskid);
            flows1.setMsgid(flows.get(0).getMsgid());
            flows1.setState(Tb_flows.STATE_FINISHED);// 完成
            flowsRepository.save(flows1);// 结束整个流程
        }
        placeOrderRepository.save(placeOrder);
    }

    public String getPlaceOrderid(String taskid){
        Tb_place_order placeOrder = placeOrderRepository.getPlaceOrderByTaskid(taskid);
        return placeOrder.getId();
    }

    public List<Tb_place_order> getUserPlaceOrders(){
        //SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return placeOrderRepository.findByStateInOrderByOrdertimeDesc(new String[]{"取消预约","预约成功"});
    }
}
