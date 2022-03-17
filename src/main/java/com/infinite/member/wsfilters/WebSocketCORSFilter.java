package com.infinite.member.wsfilters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.infinite.member.zchat.request.ZchatRequest;


@Component
@Order(1)
public class WebSocketCORSFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods",
				"POST, GET, HEAD, OPTIONS");
		response.setHeader(
				"Access-Control-Allow-Headers",
				"X-User-Id, X-Application-Access-Key, Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers,X-Presence-Status,X-App-Token");		
		
		String strAccessKey = request.getHeader("X-Application-Access-Key");
		
		String url = request.getServletPath();
		
		
		if(url.equalsIgnoreCase("/zchat")){

			String temp1= request.getHeader("Sec-WebSocket-Protocol");
			if(temp1!=null && temp1.trim().length() > 0 ){
				
				request = new ZchatRequest(request);
				
				String[] temp = temp1.split(",");
				response.setHeader("Sec-WebSocket-Protocol", temp[0]);
			}
		}
		chain.doFilter(request, response);
	}

}
