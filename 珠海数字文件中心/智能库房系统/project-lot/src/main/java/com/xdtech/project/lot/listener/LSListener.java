package com.xdtech.project.lot.listener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xdtech.project.lot.device.entity.Device;
import com.xdtech.project.lot.device.entity.DeviceHistory;
import com.xdtech.project.lot.device.entity.DeviceWarning;
import com.xdtech.project.lot.device.repository.DeviceHistoryRepository;
import com.xdtech.project.lot.device.repository.DeviceWarningRepository;
import com.xdtech.project.lot.util.TokenUtil;
import com.xdtech.smsclient.SMSService;
import org.springframework.core.env.Environment;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 安防数据接口
 * 通过定时读取斯必得数据库获取红外传感器状态
 * 并结合当前布防状态，生成告警数据
 *
 * Created by Rong on 2019-11-20.
 */
//@WebListener
public class LSListener implements ServletContextListener {


    DeviceHistoryRepository  deviceHistoryRepository;
    DeviceWarningRepository deviceWarningRepository;
    SMSService smsService;
    Environment environment;


    @Override
    public void contextInitialized(ServletContextEvent sce) {

        deviceHistoryRepository =  WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext())
              .getBean(DeviceHistoryRepository.class);
        deviceHistoryRepository =  WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext())
                .getBean(DeviceHistoryRepository.class);
        deviceWarningRepository =  WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext())
                .getBean(DeviceWarningRepository.class);
        smsService = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext())
                .getBean(SMSService.class);
        environment = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext())
                .getBean(Environment.class);

        //启动数据加载线程
        new Thread(new LSCheckListener()).start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    class LSCheckListener implements Runnable {
        @Override
        public void run() {
            while(true){
                try{

                    DeviceWarning deviceWarning = new DeviceWarning();
                    DeviceHistory deviceHistory = new DeviceHistory();
                    String loginUrl = "http://192.168.1.224:7777/login";    //http://192.168.0.170:7777/login
                    //配置登录参数，以及要获取的字段信息。得到token用于认证。
                    String token = TokenUtil.getToken("admin", "admin", loginUrl, "data");
                    String overview = "http://192.168.1.224:7777/overview?access_token=";
                    String allproblems = "http://192.168.1.224:7777/allproblems?access_token=";
                    String overviewUrl = overview + token;
                    String allproblemsUrl  = allproblems + token;
                    String phone = "13106800095";
                    //初始化restTemplate
                    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                    factory.setConnectTimeout(30000);
                    factory.setReadTimeout(30000);
                    RestTemplate restTemplate = new RestTemplate(factory);
                    //设置编码
                    restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
                    //启动数据加载线程
                    JSONObject message = restTemplate.getForObject(overviewUrl, JSONObject.class);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if (message.getString("code").equals("92000")) {
                        JSONObject j = message.getJSONObject("data").getJSONObject("动环监控系统");
                        deviceHistory.setType("SJ");
                        JSONObject captureValue = new JSONObject();
                        captureValue.put("normalNumberOfHosts",j.getString("主机正常数"));
                        captureValue.put("warningNumberOfHosts",j.getString("主机报警数"));
                        captureValue.put("totalOfAlarm",j.getString("报警总数"));
                        captureValue.put("serviceOfAlarm",j.getString("服务报警数"));
                        captureValue.put("normalNumberOfService",j.getString("服务正常数"));
                        deviceHistory.setCaptureValue(captureValue.toJSONString());
                        Date date = new Date();
                        String captureTime = sdf.format(date);
                        deviceHistory.setCaptureTime(captureTime);
                        Device device = new Device();
                        device.setId("1111111111111111111111111111111");
                        deviceHistory.setDevice(device);
                        deviceHistoryRepository.save(deviceHistory);
                        int number = Integer.parseInt(j.getString("主机报警数"));
                        if(number>=1){
                            JSONObject warningMessage = restTemplate.getForObject(allproblemsUrl, JSONObject.class);
                            JSONArray jsonArray = warningMessage.getJSONObject("data").getJSONArray("服务报警列表");
                            String palce = "";
                            for(int i = 0;i<jsonArray.size();i++){
                                JSONObject warningJson = jsonArray.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                                if(j.getString("状态").equals("警告")){
                                    if(!Boolean.parseBoolean(j.getString("已确认"))){
                                        String checkTime = j.getString("最近检查");
                                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        String t = j.getString("持续时间");
                                        int day = Integer.parseInt(t.substring(0,t.indexOf("天")));
                                        int hour = Integer.parseInt(t.substring(t.indexOf("天"),t.indexOf("时")));
                                        int minutes = Integer.parseInt(t.substring(t.indexOf("时"),t.indexOf("分")));
                                        int second  = Integer.parseInt(t.substring(t.indexOf("分"),t.indexOf("秒")));
                                        long s = day * 86400000+hour*3600000+minutes*60000+second*1000;
                                        //持续时间是小于8分钟就进行插入操作，大于8分钟什么都不做。
                                        if(s<=480000){
                                            deviceWarning.setWarningTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                                            deviceWarning.setDescription(j.getString("主机")+"水位超过安全高度");
                                            deviceWarning.setDevice(device);
                                            deviceWarning.setWarningType("水浸告警");
                                            deviceWarning.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                                            deviceWarningRepository.save(deviceWarning);
                                            palce=palce+j.getString("主机");
                                        }
                                    }
                                }
                            }
                            //有警报
                            try{
                                String datestr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
                                smsService.SendSMS(phone,"【增城档案馆】"+palce+"有水浸发生，请注意！ " +datestr);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                    //8分钟检测一次  480000
                    //5秒   5000
                    Thread.sleep(480000);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}