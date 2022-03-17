package com.infinite.member.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.infinite.member.chat.messagehandler.ChatMessageRouter;
import com.infinite.member.wsfilters.ZChatHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{
	@Autowired
	ChatMessageRouter chatMessageRouter;
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		
		registry.addHandler(chatMessageRouter, "/zchat")
		.addInterceptors(new ZChatHandshakeInterceptor());
		
	}
	
}
