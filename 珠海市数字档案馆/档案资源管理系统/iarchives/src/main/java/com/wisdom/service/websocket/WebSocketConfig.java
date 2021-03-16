package com.wisdom.service.websocket;

import com.wisdom.web.security.SlmRuntimeEasy;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * Created by Rong on 2018/6/26.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.setInterceptors(new ChannelInterceptorAdapter(){
            @Override
            public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
                StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);
                if(sha.getCommand() == null) {
                    return;
                }
                //判断客户端的连接状态
                switch(sha.getCommand()) {
                    case CONNECT:
                        SlmRuntimeEasy.ONLINE++;
                        System.out.println("---------------连接用户，当前在线用户数：" + SlmRuntimeEasy.ONLINE);
                        break;
                    case CONNECTED:
                        break;
                    case DISCONNECT:
                        if(SlmRuntimeEasy.ONLINE > 0){
                            SlmRuntimeEasy.ONLINE--;
                        }
                        System.out.println("---------------断开用户，当前在线用户数" + SlmRuntimeEasy.ONLINE);
                        break;
                    default:
                        break;
                }
            }
        });
        super.configureClientInboundChannel(registration);
    }
}
