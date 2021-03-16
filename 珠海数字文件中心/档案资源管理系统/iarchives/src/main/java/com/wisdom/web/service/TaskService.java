package com.wisdom.web.service;

import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Leo on 2020/5/26 0026.
 */
@Service
@Transactional
public class TaskService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    BillApprovalRepository billApprovalRepository;

    @Autowired
    TransdocRepository transdocRepository;

    @Autowired
    FlowsRepository flowsRepository;

    @Autowired
    BorrowMsgRepository borrowMsgRepository;

    @Autowired
    OpendocRepository opendocRepository;

    @Autowired
    BorrowDocRepository borrowDocRepository;

    @Autowired
    ReserveRepository reserveRepository;

    @Autowired
    ThematicRepository thematicRepository;

    @Autowired
    ProjectManageRepository projectManageRepository;

    @Autowired
    PlaceOrderRepository placeOrderRepository;

    @Autowired
    CarOrderRepository carOrderRepository;

    //手动催办-销毁审批
    public Tb_bill_approval manualUrgingDestructionBill(String taskid){
        Tb_bill_approval billApproval= billApprovalRepository.findByBillidContains(taskid.trim());
        if(billApproval!=null){
            taskRepository.updateStateByTaskidIn(billApproval.getTaskid());
        }
        return billApproval;
    }

    //手动催办
    public Tb_flows manualUrging(String ids){
        Tb_flows flows= flowsRepository.findByMsgidAndState(ids,"处理中");//查询处理
        if(flows!=null){
            taskRepository.updateStateByTaskidIn(flows.getTaskid());//修改催办状态
            return flows;
        }
        return null;
    }

    public ExtMsg deleteTask(String msgId) {
        try{
            return new ExtMsg(true,"",taskRepository.deleteByTaskid(msgId));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public ExtMsg deletetask(String tasktype,String taskid) {
        boolean flag = false;
        if("采集移交审核".equals(tasktype)){
            Tb_transdoc transdoc = transdocRepository.getByTaskid(taskid);
            if(transdoc==null){
                taskRepository.deleteByTaskid(taskid);
                flag = true;
            }
        }else if("查档".equals(tasktype) || "电子打印".equals(tasktype) || "实体查档".equals(tasktype)){
            Tb_borrowdoc borrowdoc = borrowDocRepository.getBorrowDocByTaskid(taskid);
            if(borrowdoc==null){
                taskRepository.deleteByTaskid(taskid);
                flag = true;
            }
        } else if("实体出库".equals(tasktype)){
            Tb_borrowdoc borrowdoc = borrowDocRepository.findByBorrowmig(taskid);
            if(borrowdoc==null){
                taskRepository.deleteByTaskid(taskid);
                flag = true;
            }
        }else if("查档到期提醒".equals(tasktype)){
            List<Tb_borrowdoc> borrowdoc = borrowDocRepository.findByTaskid(taskid);
            if(borrowdoc==null){
                taskRepository.deleteByTaskid(taskid);
                flag = true;
            }
        }else if("数据开放".equals(tasktype)){
            List<Tb_opendoc> tb_opendocs = opendocRepository.getOpendocList(taskid);
            if(tb_opendocs==null){
                taskRepository.deleteByTaskid(taskid);
                flag = true;
            }
        }else if("销毁".equals(tasktype)){
            Tb_bill_approval tb_bill_approval = billApprovalRepository.findByTaskid(taskid);
            if(tb_bill_approval==null){
                taskRepository.deleteByTaskid(taskid);
                flag = true;
            }
        }else if("预约提醒".equals(tasktype)){
            Tb_reserve tb_reserve = reserveRepository.findByBorrowmig(taskid);
            if(tb_reserve==null){
                taskRepository.deleteByTaskid(taskid);
                flag = true;
            }
        }else if("分管领导审核".equals(tasktype)){
            Tb_thematic tb_thematic = thematicRepository.findByTaskid(taskid);
            if(tb_thematic==null){
                taskRepository.deleteByTaskid(taskid);
                flag = true;
            }
        }else if("部门审核".equals(tasktype) || "副馆长审阅".equals(tasktype) || "馆长审阅".equals(tasktype)){
            Tb_project_manage tb_project_manage = projectManageRepository.findByTaskid(taskid);
            if(tb_project_manage==null){
                taskRepository.deleteByTaskid(taskid);
                flag = true;
            }
        }else if("场地预约".equals(tasktype)){
            Tb_place_order tb_place_order = placeOrderRepository.getPlaceOrderByTaskid(taskid);
            if(tb_place_order==null){
                taskRepository.deleteByTaskid(taskid);
                flag = true;
            }
        }else if("公车预约".equals(tasktype)){
            Tb_car_order tb_car_order = carOrderRepository.getCarOrderByTaskid(taskid);
            if(tb_car_order==null){
                taskRepository.deleteByTaskid(taskid);
                flag = true;
            }
        }
        return new ExtMsg(flag,"",null);
    }
}
