package com.wisdom.web.controller;

import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_car_manage;
import com.wisdom.web.entity.Tb_car_order;
import com.wisdom.web.repository.CarOrderRepository;
import com.wisdom.web.service.MyOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2020/4/27.
 */
@Controller
@RequestMapping("myOrder")
public class MyOrderController {

    @Autowired
    MyOrderService myOrderService;

    @Autowired
    CarOrderRepository carOrderRepository;

    @RequestMapping("/main")
    public String index(){
        return "/inlet/myOrder";
    }

    @RequestMapping("/placeMain")
    public String placeIndex(){
        return "/inlet/myPlaceOrder";
    }

    @RequestMapping("/getUserOrder")
    @ResponseBody
    public Page<Tb_car_order> getUserOrder(int page, int limit, String sort, String condition, String operator,
                                            String content) throws ParseException {
        Page<Tb_car_order> car_orders = myOrderService.getUserOrder(page,limit,sort,condition,operator,content);
        List<Tb_car_order> car_orderList = car_orders.getContent();
        List<Tb_car_order> returnOrders = new ArrayList<>();
        for(Tb_car_order carOrder : car_orderList){
            Tb_car_order returnCarOrder = new Tb_car_order();
            BeanUtils.copyProperties(carOrder,returnCarOrder);
            Tb_car_order order = carOrderRepository.getCarOrderByIdAndReturnstateAndState(carOrder.getId(),
                    "未归还","预约成功");
            if(order != null){
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date endDate = df.parse(order.getEndtime());
                Date currenttime = df.parse(df.format(new Date()));//获取系统当前时间
                if(currenttime.compareTo(endDate)>0) {
                    returnCarOrder.setState("<span style='color:red'>" + myOrderService.removeNull(returnCarOrder.getState()) + "</span>");
                    returnCarOrder.setReturnstate("<span style='color:red'>" + myOrderService.removeNull(returnCarOrder.getReturnstate()) + "</span>");
                    returnCarOrder.setCaruser("<span style='color:red'>" + myOrderService.removeNull(returnCarOrder.getCaruser()) + "</span>");
                    returnCarOrder.setPhonenumber("<span style='color:red'>" + myOrderService.removeNull(returnCarOrder.getPhonenumber()) + "</span>");
                    returnCarOrder.setStarttime("<span style='color:red'>" + myOrderService.removeNull(returnCarOrder.getStarttime()) + "</span>");
                    returnCarOrder.setEndtime("<span style='color:red'>" + myOrderService.removeNull(returnCarOrder.getEndtime()) + "</span>");
                    returnCarOrder.setUseway("<span style='color:red'>" + myOrderService.removeNull(returnCarOrder.getUseway()) + "</span>");
                    returnCarOrder.setCancelreason("<span style='color:red'>" + myOrderService.removeNull(returnCarOrder.getCancelreason()) + "</span>");
                    returnCarOrder.setOrdertime("<span style='color:red'>" + myOrderService.removeNull(returnCarOrder.getOrdertime()) + "</span>");
                }
            }
            if ("预约成功".equals(returnCarOrder.getState())) {
                returnCarOrder.setState("<span style='color:green'>" + returnCarOrder.getState() + "</span>");
            } else if ("预约失败".equals(returnCarOrder.getState())) {
                returnCarOrder.setState("<span style='color:red'>" + returnCarOrder.getState() + "</span>");
            }
            returnOrders.add(returnCarOrder);
        }
        PageRequest pageRequest = new PageRequest(page-1,limit);
        return new PageImpl<Tb_car_order>(returnOrders,pageRequest,car_orders.getTotalElements());
    }

    @RequestMapping("/returnUserOrder")
    @ResponseBody
    public ExtMsg returnUserOrder(String[] orderids){
        myOrderService.returnUserOrder(orderids);
        return new ExtMsg(true,"",null);
    }
}
