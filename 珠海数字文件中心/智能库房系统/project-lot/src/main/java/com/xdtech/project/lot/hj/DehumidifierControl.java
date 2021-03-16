package com.xdtech.project.lot.hj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 抽湿机操作控制器
 */
@Controller
@RequestMapping("/dehumidifier")
public class DehumidifierControl  {

    private static final Logger logger = LoggerFactory.getLogger(DehumidifierControl.class);

    /**
     * 抽湿机控制
     * @param com   com口，值为3
     * @param id    id，3号库房为3，1号库房为4
     * @param prop  prop，设备序号
     * @param opt   opt操作，1为开启，0为关闭
     * @return
     */
    @RequestMapping(value = "/command/{com}/{id}/{prop}/{opt}", method = RequestMethod.GET)
    @ResponseBody
    public Integer command(@PathVariable int com, @PathVariable int id, @PathVariable int prop, @PathVariable int opt){
        int reulst = -1;
        //1.打开串口
        Dehumidifier.dehumidifier.OpenPortEx(com, "9600,8,N,1");
        //2.发送控制指令
        reulst = Dehumidifier.dehumidifier.WriteSensor(com, id, 6, prop, opt);
        return reulst;
    }

}
