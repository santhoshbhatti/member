package com.infinite.member.messagehandlers;


import org.redisson.api.listener.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.infinite.member.wssession.WebsocketSessionStore;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class WebsocketMessageListener implements  MessageListener<String>{
	@Autowired
	WebsocketSessionStore websocketSessionStore;
	@Override
	public void onMessage(java.lang.String channel, String msg) {
		//handle the messages in the redis pub-sub
		//if the message is for a single user...find the user in the websocket session store
		//if found send the message to that websocket connection
		
		//if the message is for a group
		//find all users in the group who have established the connection
		//for all existing users send the message
		log.info("Message received on "+channel+" :  "+msg);
		
	}

}
