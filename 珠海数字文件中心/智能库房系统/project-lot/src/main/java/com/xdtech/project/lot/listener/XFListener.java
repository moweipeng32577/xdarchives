package com.xdtech.project.lot.listener;

import com.xdtech.project.lot.device.entity.DeviceWarning;
import com.xdtech.project.lot.device.repository.DeviceWarningRepository;
import com.xdtech.smsclient.SMSService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 消防数据接口
 * 通过接收TCP通讯数据，获取消防继电器状态
 */
//@WebListener
public class XFListener implements ServletContextListener {

    private static final int MAX_DATA_SIZE = 20;    //数据缓冲区大小
    private static DatagramPacket packet = null;
    private static DatagramSocket socket = null;
    private ServerSocket tcpsocket = null;
    private SMSService smsService;
    DeviceWarningRepository deviceWarningRepository;

    @Value("${system.xf.receive.ip}")
    private String receiveip;

    @Value("${system.xf.receive.port}")
    private int receiveport;

    @Value("${sms.server.address}")
    private String smsAddress;//短信猫信息接收手机号码

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try{
            //启动线程，监听UDP端口
            new Thread(new XFUdpListener(receiveport)).start();
        } catch (Exception e){
            e.printStackTrace();
        }

        smsService = WebApplicationContextUtils.getWebApplicationContext(servletContextEvent.getServletContext())
                .getBean(SMSService.class);
        deviceWarningRepository =  WebApplicationContextUtils.getWebApplicationContext(servletContextEvent.getServletContext())
                .getBean(DeviceWarningRepository.class);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    class XFUdpListener implements Runnable {

        public XFUdpListener(final int port) throws IOException, UnknownHostException {
            tcpsocket = new ServerSocket(receiveport, 1, InetAddress.getByName(receiveip));
        }

        @Override
        public void run() {
            while(true){
                byte[] buf = new byte[MAX_DATA_SIZE];
                try{
                    Socket s = tcpsocket.accept();
                    InputStream in = s.getInputStream();
                    int len = in.read(buf);
                    System.out.println("消防周期上报：" + new String(buf,0,len));
                    /*
                    消防继电器周期上报数据：+OCCH_ALL:0,0,0,0..
                    输入1口信号,buffer[10],ASCII码,48对应0,49对应1
                    正常状态为0,当有报警时状态为1
                     */
                    if(buf[10] == 48){

                    }else if(buf[10]  == 49){
                        //TODO 消防报警，a）新增一条报警信息 b）发送短信通知

                        DeviceWarning deviceWarning = new DeviceWarning();
                        deviceWarning.setWarningTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        deviceWarning.setDescription("发生火灾消息");
                        deviceWarning.setWarningType("火灾告警");
                        deviceWarning.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        deviceWarningRepository.save(deviceWarning);

                        try{
                            String datestr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
                            smsService.SendSMS(smsAddress,"【增城档案馆】"+"发生火灾消息！ " +datestr);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    s.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}