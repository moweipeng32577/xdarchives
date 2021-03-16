package com.wisdom.web.service;

import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.Tb_car_manage;
import com.wisdom.web.entity.Tb_car_order;
import com.wisdom.web.entity.Tb_flows;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.repository.CarManageRepository;
import com.wisdom.web.repository.CarOrderRepository;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2020/4/27.
 */
@Service
@Transactional
public class MyOrderService {


    @Autowired
    CarOrderRepository carOrderRepository;

    @Autowired
    CarManageRepository carManageRepository;

    //过滤“”或null
    public String removeNull(String param){
        if(param == null || "".equals(param))
            return "";
        return param;
    }

    public Page<Tb_car_order> getUserOrder(int page, int limit, String sort, String condition, String operator, String content) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if("".equals(sort)||sort==null){
            sort="[{'property':'ordertime','direction':'desc'}]";
        }
        Sort sortobj = WebSort.getSortByJson(sort);
        PageRequest pageRequest = new PageRequest(page - 1, limit, sortobj);
        Specifications sp = null;
        sp = sp.where(new SpecificationUtil("submiterid", "equal", userDetails.getUserid()));
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        return carOrderRepository.findAll(sp, pageRequest);
    }

    public void returnUserOrder(String[] orderids){
        List<Tb_car_order> orders = carOrderRepository.findByIdIn(orderids);
        String datastr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        List<Tb_car_manage> car_manages = new ArrayList<>();
        for(Tb_car_order order : orders){
            order.setReturnstate("已归还");
            order.setReturntime(datastr);
            Tb_car_manage carManage = carManageRepository.findById(order.getCarid());
            carManage.setState("空闲中");
            car_manages.add(carManage);
        }
        carManageRepository.save(car_manages);
        carOrderRepository.save(orders);
    }
}
