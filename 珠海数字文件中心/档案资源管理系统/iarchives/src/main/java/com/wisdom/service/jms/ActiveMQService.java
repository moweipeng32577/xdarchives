package com.wisdom.service.jms;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2019/8/15.
 */
@Service
public class ActiveMQService {


    @Value("${spring.activemq.user}")
    private String user;// 系统文件根目录

    @Value("${spring.activemq.password}")
    private String pwd;// 系统文件根目录

    @Value("${spring.activemq.broker-url}")
    private String url;// 系统文件根目录

    public void destoryDestination(String name) throws Exception {
        ActiveMQDestination des = new ActiveMQQueue(name);
        ActiveMQConnection conn = ActiveMQConnection.makeConnection(user, pwd, url);
        conn.destroyDestination(des);
    }
}
