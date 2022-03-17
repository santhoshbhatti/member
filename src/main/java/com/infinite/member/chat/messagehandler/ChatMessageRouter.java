package com.infinite.member.chat.messagehandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.infinite.member.wssession.WebsocketSessionStore;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ChatMessageRouter extends TextWebSocketHandler {

	@Autowired
	WebsocketSessionStore websocketSessionStore;

	List<WebSocketSession> webSocketSessions = Collections.synchronizedList(new ArrayList<>());

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);
		webSocketSessions.add(session);
		//{domain= prime, userid=shiva, auth-token= 324324234}
		var userId = session.getAttributes().get("userid").toString();
		var domain = session.getAttributes().get("domain").toString();
		var authToken = session.getAttributes().get("auth-token").toString();
		websocketSessionStore.storeSession(userId, domain, session.getId(), session);
		HttpHeaders headers = session.getHandshakeHeaders();
		System.out.println(headers);
		//("X-User-Id");headers.add("X-Auth-Token");headers.add("schemaName")
		/*
		 * List<String> groupIds = headers.get("X-User-Id"); List<String> groupKeys =
		 * headers.get("X-Auth-Token"); List<String> groupDomain =
		 * headers.get("schemaName"); log.info(groupIds.toString());
		 * log.info(groupKeys.toString()); log.info(groupDomain.toString());
		 */
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		super.afterConnectionClosed(session, status);
		webSocketSessions.remove(session);
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		super.handleMessage(session, message);
		
		//publish the message to redis topic
		for (WebSocketSession webSocketSession : webSocketSessions) {
			webSocketSession.sendMessage(message);
		}
	}

}
