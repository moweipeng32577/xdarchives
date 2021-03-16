package com.xdtech.project.lot.device.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.project.lot.device.entity.DeviceLink;
import com.xdtech.project.lot.device.repository.DeviceLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by Rong on 2019-06-13.
 */
@Controller
@RequestMapping("/")
public class DeviceLinkController {

    @Autowired
    DeviceLinkRepository deviceLinkRepository;

    @RequestMapping(value = "/devicelink",method = RequestMethod.GET)
    @ResponseBody
    public void deviceLinks(HttpServletResponse httpServletResponse){
        List<DeviceLink> list = deviceLinkRepository.findAll(new Sort("deviceName","event","sequence"));
//        return deviceLinkRepository.findAll(new Sort("deviceName","event","sequence"));
        //不使用框架自带的json转换，避免循环引用
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("utf-8");
        String json = JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect);
        try {
            httpServletResponse.getWriter().write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/devicelink", method = RequestMethod.POST)
    @ResponseBody
    public void save(DeviceLink link){
        deviceLinkRepository.save(link);
    }

    @RequestMapping(value = "/devicelink/{linkid}", method = RequestMethod.POST)
    @ResponseBody
    public void delete(@PathVariable String linkid){
        deviceLinkRepository.delete(linkid);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/linkdel",method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg linkdel(String[] ids){
        deviceLinkRepository.deleteByIds(ids);
        return new ExtMsg(true,"删除成功",null);
    }

}
