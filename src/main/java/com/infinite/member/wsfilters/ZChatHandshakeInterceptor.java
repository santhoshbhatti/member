package com.infinite.member.wsfilters;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.infinite.member.zchat.request.ZchatRequest;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ZChatHandshakeInterceptor implements HandshakeInterceptor {

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		log.info(attributes.toString());
		
		HttpHeaders headers = request.getHeaders();
		if(headers != null) {
			log.info(headers.toString());
		}
		ServletServerHttpRequest serRequest = (ServletServerHttpRequest)request;
		var httpRequest = serRequest.getServletRequest();
		var attrs = httpRequest.getAttributeNames();
		while(attrs.hasMoreElements()) {
			String nextElement = attrs.nextElement();
			log.info(nextElement);
			log.info(httpRequest.getAttribute(nextElement).toString());
		}
		attributes.put("userid", httpRequest.getAttribute("userid"));
		attributes.put("domain", httpRequest.getAttribute("domain"));
		attributes.put("auth-token", httpRequest.getAttribute("auth-token"));
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		// TODO Auto-generated method stub

	}

}
