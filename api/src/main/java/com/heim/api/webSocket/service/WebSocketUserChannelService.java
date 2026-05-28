package com.heim.api.webSocket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WebSocketUserChannelService {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketUserChannelService(SimpMessagingTemplate messagingTemplate){
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyUser(Object payload, Long userId){
        String destination = "/topic/user/" + userId;
        messagingTemplate.convertAndSend(destination, payload);
    }
}
