package com.infinite.member.wssession;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
public class WebsocketSessionStore {
	
	private final Map<String,List<WebSocketSession>> sessionStore;

	public WebsocketSessionStore() {
		sessionStore = Collections
				.synchronizedMap(new HashMap<>());
	}
	
	public Map<String, List<WebSocketSession>> getAllSessions(){
		return Collections.unmodifiableMap(sessionStore);
	}
	
	public void storeSession(String userId,String domain,String clientId, WebSocketSession session) {
		session.getAttributes().put("clientId", clientId);
		if(sessionStore.containsKey(userId+"@"+domain)) {
			var existingSession =sessionStore.get(userId+"@"+domain)
			.stream()
			.filter(s -> s.getAttributes().containsKey(clientId))
			.findAny();
			
			if(existingSession.isPresent()) {
				log.info("session already exists!!!!!!!");
			}else {
				sessionStore.get(userId+"@"+domain).add(session);
			}
		}else {
			var list = List.of(session);
			sessionStore.put(userId+"@"+domain, list);
		}
	}
	
	public Optional<List<WebSocketSession>> getAllUserSessions(String userId,String domain){
		if(sessionStore.containsKey(userId+"@"+domain)) {
			var existingSessions =Collections.unmodifiableList(sessionStore.get(userId+"@"+domain));
			return Optional.of(existingSessions);
			
		}
		return Optional.empty();	
	}
	
	public Optional<WebSocketSession> getUserSession(String userId,String domain,String clientId) {
		if(sessionStore.containsKey(userId+"@"+domain)) {
			var existingSession =sessionStore.get(userId+"@"+domain)
			.stream()
			.filter(s -> s.getAttributes().containsKey(clientId))
			.findAny();
			return existingSession;
		}
		return Optional.empty();
	}
	

}
