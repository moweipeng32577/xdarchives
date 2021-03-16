package com.xdtech.project.lot.device.controller;

import com.alibaba.fastjson.JSONObject;
import com.xdtech.project.lot.device.entity.DeviceHistory;
import com.xdtech.project.lot.device.entity.DeviceInformation;
import com.xdtech.project.lot.device.repository.DeviceHistoryRepository;
import com.xdtech.project.lot.device.service.ManagementHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/managementHistory")
public class ManagementHistoryController {

    @Autowired
    DeviceHistoryRepository deviceHistoryRepository;

    @Autowired
    ManagementHistoryService managementHistoryService;

    @RequestMapping(value = "/grid")
    @ResponseBody
    public Page getGrid(int limit, int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(new Sort.Order(Sort.Direction.DESC,"captureTime"));//升序
        Page<DeviceHistory> page1 =  deviceHistoryRepository.findAll( new PageRequest(page - 1, limit,new Sort(sorts)));
        List<Map<String,String>> returnlist = managementHistoryService.getHistory(page1.getContent());
        return new PageImpl<>(returnlist, new PageRequest(page - 1, limit),page1.getTotalElements());
    }

    @RequestMapping(value = "/expHistory")
    @ResponseBody
    public void expHistory(HttpServletRequest request, HttpServletResponse response,String[] ids){
        if(null!=ids){
            managementHistoryService.expHistory(ids,response,request);
        }
    }

    @RequestMapping(value = "/findHistory")
    @ResponseBody
    public Page findHistory(int page,int limit,String content,String searchcombo){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(new Sort.Order(Sort.Direction.DESC,"captureTime"));//升序
        PageRequest pageRequest = new PageRequest(page - 1, limit,new Sort(sorts));
        if(content!=null && !"".equals(content)){
            Specification<DeviceHistory> searchid = getSearchcontent(searchcombo,content);
            Specifications sp = Specifications.where(searchid);
            Page<DeviceHistory> page1 = deviceHistoryRepository.findAll(sp,pageRequest);
            List<Map<String,String>> returnlist = managementHistoryService.getHistory(page1.getContent());
            return new PageImpl<>(returnlist, new PageRequest(page - 1, limit),page1.getTotalElements());
        }
        Page page1 = deviceHistoryRepository.findAll( new PageRequest(page - 1, limit,new Sort(sorts)));
        List<Map<String,String>> returnlist = managementHistoryService.getHistory(page1.getContent());
        return new PageImpl<>(returnlist, new PageRequest(page - 1, limit),page1.getTotalElements());
    }

    /**
     *
     * @param searchcombo 查询字段
     * @param content 查询内容
     * @return 设备信息
     */
    public static Specification<DeviceHistory> getSearchcontent(String searchcombo, String content){
        Specification<DeviceHistory> searchCondition = new Specification<DeviceHistory>() {
            @Override
            public Predicate toPredicate(Root<DeviceHistory> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get(searchcombo), content);
                return criteriaBuilder.or(p);
            }
        };
        return searchCondition;
    }

}
