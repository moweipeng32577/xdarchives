package com.xdtech.project.lot.websocket;

import com.xdtech.project.lot.device.entity.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 信息推送服务
 */
@Service
public class DeviceWebSocketService {
    private static String DEVICES_STATUS = "/devicesStatus";  //推送密集架当前状态
    private static String AF_STATUS = "/afStatus";  //推送密集架当前状态

    @Autowired
    SimpMessagingTemplate template;

    /**
     * 推送密集架当前状态
     * @param message 推送消息
     */
    public void sendDevicesStatus(String message){
        template.convertAndSend(DEVICES_STATUS, message);
    }

    /**
     * 安防推送信息
     * @param device 设备信息
     */
    public void sendAFStatus(Device device){
        template.convertAndSend(AF_STATUS, device);
    }

}
