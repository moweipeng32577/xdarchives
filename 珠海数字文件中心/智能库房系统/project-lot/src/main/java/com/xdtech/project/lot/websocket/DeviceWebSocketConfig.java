package com.xdtech.project.lot.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * Created by Rong on 2018/6/26.
 */
@Configuration
@EnableWebSocketMessageBroker
public class DeviceWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/devicewebsocket").setAllowedOrigins("*").withSockJS();
    }

}
