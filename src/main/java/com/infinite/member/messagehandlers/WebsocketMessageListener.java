package com.infinite.member.messagehandlers;


import org.redisson.api.listener.MessageListener;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class WebsocketMessageListener implements  MessageListener<String>{
	@Override
	public void onMessage(java.lang.String channel, String msg) {
		log.info("Message received on "+channel+" :  "+msg);
		
	}

}
