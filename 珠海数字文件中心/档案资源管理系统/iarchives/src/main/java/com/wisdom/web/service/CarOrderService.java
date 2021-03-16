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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2020/4/21.
 */
@Service
@Transactional
public class CarOrderService {

    @Autowired
    CarOrderRepository carOrderRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    NodeRepository nodeRepository;

    @Autowired
    FlowsRepository flowsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    InFormRepository inFormRepository;

    @Autowired
    InFormUserRepository inFormUserRepository;

    @Autowired
    JyAdminsService jyAdminsService;

    @Autowired
    ElectronApproveService electronApproveService;

    @Autowired
    CarManageRepository carManageRepository;

    @Autowired
    PlaceOrderService placeOrderService;

    @Autowired
    LogService logService;

    public Page<Tb_car_order> getAllCarOrder(int page, int limit, String sort, String condition, String operator, String content){
        Sort sortobj = WebSort.getSortByJson(sort);
        PageRequest pageRequest = new PageRequest(page-1,limit,sortobj);
        Specifications sp = null;
        if(content!=null){
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        return carOrderRepository.findAll(sp,pageRequest);
    }

    //根据Tb_car_manage的id查找对应的Tb_car_order
    public Page<Tb_car_order> getCarOrderByCarid(String id,String selectTime, int page, int limit, String sort, String condition,
                                                 String operator, String content){
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
            return  carOrderRepository.getCarOrderByCarid(id,"提交预约",pageRequest);
        }
        startDateStr = sdf.format(startDate);
        endDateStr = sdf.format(endDate);
        return  carOrderRepository.getCarOrderByCaridAndTimelimit(id,startDateStr,endDateStr,"提交预约",pageRequest);
    }

    public List<Tb_car_order> carOrderFormSubmit(Tb_car_order carOrder, String spnodeid, String spmanid, String carid){
        List<Tb_car_order> carOrders = carOrderRepository.findByCarid(carid);
        String starttime = carOrder.getStarttime();//开始时间
        String endtime = carOrder.getEndtime();//结束时间
        List<Tb_car_order> returnOrder = new ArrayList<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            Date startdate = sdf.parse(starttime);//开始时间
            Date enddate = sdf.parse(endtime);//结束时间
            //判断是否该公车在申请时间段内已存在预约
            /*for (Tb_car_order order : carOrders) {
                Date orderstartdate = sdf.parse(order.getStarttime());
                Date orderenddate = sdf.parse(order.getEndtime());
                //判断开始时间、结束时间段，是否已被别人预约
                if (startdate.getTime() >= orderstartdate.getTime()) {
                    //开始时间处于预约时间段内
                    if (startdate.getTime() < orderenddate.getTime()) {
                        returnOrder.add(order);
                    }
                } else if (enddate.getTime() > orderstartdate.getTime()) {  //结束时间大于预约的开始时间
                    returnOrder.add(order);
                }
            }*/
            for(Tb_car_order order : carOrders){
                Date orderStartDate = sdf.parse(order.getStarttime());
                Date orderEndDate = sdf.parse(order.getEndtime());
                if((startdate.getTime() >= orderStartDate.getTime() && startdate.getTime() < orderEndDate.getTime() ||//判断开始时间范围
                        enddate.getTime() <= orderEndDate.getTime() && enddate.getTime() > orderStartDate.getTime()) &&  //判断结束时间范围
                        ("预约成功".equals(order.getState()) || "正在申请".equals(order.getState()))){
                    returnOrder.add(order);
                    logService.recordTextLog("公车预约","新增预约;预约人:"+order.getCaruser()+";条目id:"+order.getId());
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
            Tb_car_manage carManage = carManageRepository.findById(carid);
            carOrder.setOrdercode(ordercode);
            carOrder.setSubmiterid(userDetails.getUserid());
            carOrder.setCarnumber(carManage.getCarnumber());
            long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
            Tb_user spman = userRepository.findByUserid(spmanid);
            Tb_task task = new Tb_task();
            task.setLoginname(spmanid);
            task.setState(Tb_task.STATE_WAIT_HANDLE);// 待处理
            task.setText(carOrder.getCaruser() + " " + new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss").format(new Date())
                    + " 公车预约申请");
            task.setType("公车预约");
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

            carOrder.setAuditlink(node.getText());
            carOrder.setAuditer(spman.getRealname());
            carOrder.setState("提交预约");
            carOrderRepository.save(carOrder);
            return null;
        }
    }

    public Tb_car_order carOrderCancelFormSubmit(String orderid,String canceluser,String canceltime,String cancelreason) throws Exception{
        Tb_car_order carOrder = carOrderRepository.findById(orderid);
        carOrder.setCanceluser(canceluser);
        carOrder.setCanceltime(canceltime);
        carOrder.setCancelreason(cancelreason);
        carOrder.setState("取消预约");
        Tb_inform inform = new Tb_inform();
        inform.setTitle("公车取消预约");
        inform.setText(canceluser+" 取消 "+carOrder.getCarnumber()+" 在"+carOrder.getStarttime()+"-"+carOrder.getEndtime()+"的用车预约！");
        inform.setPosteduser("已发布");
        inform.setInformdate(new Date());// 生成时间);
        //公告截止时间为预约单的结束时间（日期的第二天0点）
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        Date endDate = ft.parse(carOrder.getEndtime());
        Calendar c = Calendar.getInstance();
        c.setTime(endDate);
        c.add(Calendar.DAY_OF_MONTH, 1);
        endDate = c.getTime();
        inform.setLimitdate(endDate);
        inform.setPostedman(canceluser);
        inform.setPostedusergroup("未发布");
        inform = inFormRepository.save(inform);
        List<String> spmanList = flowsRepository.getFlows(carOrder.getOrdercode());
        List<Tb_inform_user> inform_users = new ArrayList<>();
        for(String userid : spmanList){
            Tb_inform_user inform_user = new Tb_inform_user();
            inform_user.setInformid(inform.getId());
            inform_user.setUserroleid(userid);
            inform_users.add(inform_user);
        }
        inFormUserRepository.save(inform_users);
        return carOrderRepository.save(carOrder);
    }

    public int carOrderDelete(String[] orderids){
        List<Tb_car_order> carOrders = carOrderRepository.findByIdIn(orderids);
        for(Tb_car_order carOrder : carOrders){
            List<Tb_flows> flows = flowsRepository.findByMsgid(carOrder.getOrdercode());
            taskRepository.deleteByTaskid(flows.get(0).getTaskid());
            flowsRepository.deleteByTaskid(flows.get(0).getTaskid());
        }
        return carOrderRepository.deleteByIdIn(orderids);
    }

    public Page getAuditDetails(String orderid) {
        Tb_car_order carOrder = carOrderRepository.findById(orderid);
        if (carOrder != null) {
            return jyAdminsService.getCommonDealDetails(carOrder.getOrdercode(), carOrder.getApprove(),
                    carOrder.getCaruser());
        }
        return new PageImpl(new ArrayList(), null, 0);
    }

    public Page<Tb_car_order> getUserAuditOrder(String taskid,int page, int limit, String sort, String condition, String operator, String content){
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
            return carOrderRepository.findAll(sp,pageRequest);
        }else{
            List<String> ordercodes = flowsRepository.getMsgids(userDetails.getUserid(),"待处理","公车预约");
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
                return carOrderRepository.findAll(sp,pageRequest);
            }
            return null;
        }
    }

    public List<Tb_node> getNodes(String orderid) {
        Tb_car_order carOrder = carOrderRepository.findById(orderid);
        Tb_flows flows = flowsRepository.findByMsgidAndState(carOrder.getOrdercode(),"处理中");
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
    public void auditOrderSubmit(String textArea, String nextNode, String nextSpman, String orderid,String selectApprove) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long dateInt = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        Tb_car_order carOrder = carOrderRepository.findById(orderid);
        Tb_flows flowNow = flowsRepository.findByMsgidAndState(carOrder.getOrdercode(),"处理中");
        String taskid = flowNow.getTaskid();
        if("同意".equals(selectApprove)){  //审批通过
            Tb_task task1 = taskRepository.findByTaskid(taskid);
            Tb_flows flows = flowsRepository.findByTaskidAndSpman(taskid, userDetails.getUserid());// 获取当前任务流程设置状态
            Tb_node node = nodeRepository.findByNodeid(nextNode);
            Tb_node nowNode = nodeRepository.findByNodeid(flowNow.getNodeid());// 拿到当前节点
            if(nowNode.getOrders()==2){ //审批第一环节
                carOrder.setState("审批中");
            }
            carOrder.setApprove(textArea);
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
                carOrder.setAuditlink(node.getText());// 更新下一审批环节
                carOrder.setAuditer(nextSpmanRealname);// 更新当前审批人
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

                carOrder.setState("预约成功");
                carOrder.setAuditlink(node1.getText());
                carOrder.setReturnstate("");
                carOrder.setAuditer(userDetails.getRealname());

                electronApproveService.updateElectroInfo(node.getText(), userDetails.getRealname(), taskid);
            }
        }else{  //审批不通过
            String nodeid = flowNow.getNodeid();
            Tb_task task = taskRepository.findByTaskid(taskid);// 获取任务修改任务状态
            task.setState(Tb_task.STATE_FINISHED);// 完成

            carOrder.setApprove(textArea);
            carOrder.setState("预约失败");

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
        carOrderRepository.save(carOrder);
    }

    public Page<Tb_car_order> getMyCarOrder(int page, int limit, String sort){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Sort sortobj = WebSort.getSortByJson(sort);
        PageRequest pageRequest = new PageRequest(page-1,limit,sortobj);
        Specifications sp = null;
        sp = sp.where(new SpecificationUtil("submiterid","equal",userDetails.getUserid()));
        return carOrderRepository.findAll(sp,pageRequest);
    }


    public String getCarOrderid(String taskid){
        Tb_car_order carOrder = carOrderRepository.getCarOrderByTaskid(taskid);
        return carOrder.getId();
    }

    public int updateReturnstate(String id,String state){
        Tb_car_order carOrder = carOrderRepository.findById(id);
        Tb_car_manage carManage = carManageRepository.findById(carOrder.getCarid());
        carManage.setState("使用中");
        carManageRepository.save(carManage);
        return carOrderRepository.updateReturnstate(id,state);
    }

    public List<Tb_node> getNextNodeByType(String orderid,String type) {
        if("carOrder".equals(type)){  //公车预约
            Tb_car_order carOrder = carOrderRepository.findById(orderid);
            Tb_flows flows = flowsRepository.findByMsgidAndState(carOrder.getOrdercode(),"处理中");
            Tb_node node = nodeRepository.findByNodeid(flows.getNodeid());
            String[] nextids = node.getNextid().split(",");
            return nodeRepository.getNodes(nextids);
        }else{  //场地预约
            return placeOrderService.getNodes(orderid);
        }
    }

    public Page<Tb_task> getAuditOrderByTask(int page, int limit, String sort){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Sort sortobj = WebSort.getSortByJson(sort);
        Sort sorts = new Sort(new Sort.Order(Sort.Direction.DESC, "tasktime"),
                new Sort.Order(Sort.Direction.DESC, "taskid"));
        PageRequest pageRequest = new PageRequest(page-1,limit,sortobj==null?sorts:sortobj);
        String[] tasktypeStr = new String[5];
        tasktypeStr[0] = "公车预约";
        tasktypeStr[1] = "场地预约";
        tasktypeStr[2] = "部门审核";
        tasktypeStr[3] = "副馆长审阅";
        tasktypeStr[4] = "馆长审阅";
        return taskRepository.findByLoginnameAndStateAndTasktypeIn(pageRequest,userDetails.getUserid(),Tb_task.STATE_WAIT_HANDLE,tasktypeStr);
    }

    public Page<Tb_inform> getInforms(int page,int limit, Sort sort) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(new Sort.Order(Sort.Direction.ASC,"stick"));//置顶
        sorts.add(new Sort.Order(Sort.Direction.DESC,"informdate"));//发布时间降序
        PageRequest pageRequest = new PageRequest(page-1,limit,sort==null?new Sort(sorts):sort);
        List<Tb_inform> informs = inFormRepository.getOrderInForms(userDetails.getUserid());
        return new PageImpl<Tb_inform>(informs,pageRequest,informs.size());
    }

    public String getCarOrderidByType(String taskid,String type){
        if("carOrder".equals(type)) {  //公车预约
            Tb_car_order carOrder = carOrderRepository.getCarOrderByTaskid(taskid);
            return carOrder.getId();
        }else{
            return placeOrderService.getPlaceOrderid(taskid);
        }
    }

    public List<Tb_car_order> getUserCarOrders(){
        //SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return carOrderRepository.findByStateInOrderByOrdertimeDesc(new String[]{"取消预约","预约成功"});
    }
}
