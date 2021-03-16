package com.wisdom.service.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Rong on 2018/6/26.
 */
@Controller
public class WebSocketController {

    @Autowired
    WebSocketService webSocketService;

    @SubscribeMapping("/message")
    @ResponseBody
    public String message() throws Exception {
        webSocketService.noticeRefresh();
        return null;
    }

}
