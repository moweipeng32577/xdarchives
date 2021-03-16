package com.xdtech.project.lot.hj;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/environment")
public class EnvironmentControl {

    public static void main(String[] args) {

        int status = Environment.environment.Open_Port();
        System.out.println(status);
        int result = Environment.environment.WriteAircondition("192.168.0.145",502, 1, 14, 0xe0, 0, -1, 25, 55);
        System.out.println(result);
    }

    @RequestMapping(value = "/command/{ip}/{sensor}/{tem}/{humi}", method = RequestMethod.GET)
    @ResponseBody
    public Integer command(@PathVariable String ip, @PathVariable String sensor, @PathVariable int tem, @PathVariable int humi){
        int reulst = -1;
        //1.打开串口
        Environment.environment.Open_Port();
        //2.发送控制指令
        reulst = Environment.environment.WriteAircondition(ip,502, 1, Integer.parseInt(sensor), 0xe0, 0, -1, tem, humi);
        return reulst;
    }
}
