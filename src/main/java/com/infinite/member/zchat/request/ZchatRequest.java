package com.infinite.member.zchat.request;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZchatRequest extends HttpServletRequestWrapper {

	public ZchatRequest(HttpServletRequest request) {
		super(request);
	}

	public static List<String> headers = new ArrayList<>();

	static {
		headers.add("X-User-Id");
		headers.add("X-Auth-Token");
		headers.add("schemaName");
	}
	
	@Override
	public String getHeader(String name) {
		
		String value = null;		
		
		if(headers.contains(name)){			
			String val = super.getHeader("Sec-WebSocket-Protocol");
			
			if(val==null){
				return "";
			}
			
			String[] temp = val.split(",");
			value = temp[headers.indexOf(name)].trim();
			try {
				value = URLDecoder.decode(value,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				
				log.error("error decoding ",e);
			}
		}
		else if(name.equalsIgnoreCase("Sec-WebSocket-Protocol")){
			value = "";
		}
		else{
			value = super.getHeader(name);
		}
		return value;
	}
	
	@Override
	public Enumeration<String> getHeaderNames() {
		
		Enumeration<String> enumeration = super.getHeaderNames();			
		
		Set<String> temp = new HashSet<String>();
		
		if(enumeration!=null){
			
			String val;
			while(enumeration.hasMoreElements()){
				
				val = enumeration.nextElement();
				
				if(!val.equalsIgnoreCase("Sec-WebSocket-Protocol")){
					temp.add(val);
				}				
				
			}
		}
		
		return java.util.Collections.enumeration(temp);
	}
	
	@Override
	public Enumeration<String> getHeaders(String name) {
		
		Enumeration<String> enumeration = Collections.enumeration(Collections.emptyList());;			
		
		if(!name.equalsIgnoreCase("Sec-WebSocket-Protocol")){
			enumeration = super.getHeaders(name);
		}		
		
		return enumeration;
	}

}
