package com.wisdom.service.jms;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Destination;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class MessageProducerService {

    @Value("${system.document.rootpath}")
    private String rootpath;// 系统文件根目录

    @Autowired
    JmsMessagingTemplate jmsMessageTemplate;

    public void producerMessage(String token, Object[] obj, String type){
        try{
            Map<String, Object> props = new HashMap<>();
            props.put("archivecode", obj[0]);
            props.put("filename", obj[2]);
            props.put("batchname", obj[3]);
            String fullpath = rootpath + obj[1] + File.separator + obj[2];
            if("scan".equals(type)){
                fullpath = rootpath + File.separator + "scan" + File.separator
                        + obj[3] + File.separator + obj[0] + File.separator + obj[2];
            }

            FileInputStream fis = new FileInputStream(new File(fullpath));
            byte[] contents = new byte[fis.available()];
            fis.read(contents);
            fis.close();

            ActiveMQBytesMessage msg = new ActiveMQBytesMessage();
            msg.writeBytes(contents);
            msg.setProperties(props);
            Destination destination = new ActiveMQQueue(token + String.valueOf(obj[0]));
            jmsMessageTemplate.convertAndSend(destination, msg);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
