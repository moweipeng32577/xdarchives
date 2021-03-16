package com.xdtech.project.lot.device.controller;


import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.project.lot.device.entity.DeviceInformation;
import com.xdtech.project.lot.device.repository.DeviceInformationRepository;
import com.xdtech.project.lot.device.service.DeviceInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/deviceInformation")
public class DeviceInformationController {

    @Autowired
    DeviceInformationRepository deviceInformationRepository;

    @Autowired
    DeviceInformationService deviceInformationService;

    /**
     * 获取列表
     */
    @RequestMapping(value = "/grid", method = RequestMethod.GET)
    @ResponseBody
    public Page<DeviceInformation> grid(int page, int limit) {
        return deviceInformationRepository.findAll( new PageRequest(page - 1, limit,new Sort("installdate")));
    }

    /**
     * 查询
     */
    @RequestMapping("/findDevicesBySearch")
    @ResponseBody
    public Page<DeviceInformation> findDevicesBySearch(int page,int limit,String searchcombo,String content) {
        PageRequest pageRequest = new PageRequest(page - 1, limit,new Sort("installdate"));
        if(content!=null && !"".equals(content)){
            Specification<DeviceInformation> searchid = getSearchcontent(searchcombo,content);
            Specifications sp = Specifications.where(searchid);
            return deviceInformationRepository.findAll(sp,pageRequest);
        }
        return deviceInformationRepository.findAll(pageRequest);
    }

    /**
     * 导出设备信息
     */
    @RequestMapping("/expDeviceInformation")
    public void expDeviceInformation(String[] inforids, HttpServletRequest request, HttpServletResponse response) {
        if (null != inforids) {
            deviceInformationService.expDeviceInformation(inforids, request, response);
        }
    }

    /**
     * 录入设备信息
     */
    @RequestMapping(value = "/saveInformation")
    @ResponseBody
    public ExtMsg saveInformation(DeviceInformation deviceInformation) {
        boolean b = deviceInformationService.saveInformation(deviceInformation);
        if (b) {
            return new ExtMsg(true, "保存成功！", null);
        }
        return new ExtMsg(false, "保存失败！", null);
    }

    /**
     *删除设备信息
     */
    @RequestMapping(value = "/delInformation")
    @ResponseBody
    public ExtMsg delInformation(String[] inforIds) {
        int delCount = 0;
        delCount = delCount +deviceInformationService.delInformation(inforIds);
        if (delCount>0) {
            return new ExtMsg(true, "删除成功！", null);
        }
        return new ExtMsg(false, "删除失败！", null);
    }

    /**
     *
     * @param searchcombo 查询字段
     * @param content 查询内容
     * @return 设备信息
     */
    public static Specification<DeviceInformation> getSearchcontent(String searchcombo,String content){
        Specification<DeviceInformation> searchCondition = new Specification<DeviceInformation>() {
            @Override
            public Predicate toPredicate(Root<DeviceInformation> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get(searchcombo), content);
                return criteriaBuilder.or(p);
            }
        };
        return searchCondition;
    }
}
