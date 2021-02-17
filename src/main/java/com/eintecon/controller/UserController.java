package com.eintecon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.eintecon.SessionManager;
import com.eintecon.model.ClientMappingModel;
import com.eintecon.service.DocumentSignService;


@RestController
@CrossOrigin
public class UserController {
	@Autowired
	DocumentSignService dss;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    private String sessionId = "";
    private String clientName = "";
    
	// https://www.youtube.com/watch?v=-ao3pX-UhQc&ab_channel=JavaMaster
	//@GetMapping("/registration/{Username}")

    @MessageMapping("/registration/{Username}")
	public ClientMappingModel  register(@PathVariable String Username){
    	
    	clientName = Username;
		System.out.println("USERNAME:" + clientName);
		SessionManager.openSession(sessionId, clientName);
		SessionManager.broadCast("New Client Register :  " + Username);
		ClientMappingModel client= dss.getClientMapping(clientName);
		client.setConnected(true);
		dss.saveClientMapping(client);
		if (client != null && !client.getId().equals("0") ) {
			client.setMessage("Registiration succesfully completed");
		}
		simpMessagingTemplate.convertAndSend("/topic/greetings/" + Username, "Mesaj denemesi");
		return client;
	}
    
    @EventListener
	public void onConnectEvent(SessionConnectEvent event) {
		try {
			StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
			sessionId = headers.getSessionId();
		} catch (Exception e) {
			System.out.println("Couldn't connect due to: "+e);
		}
	}
    
    @EventListener
	public void onDisconnectEvent(SessionDisconnectEvent event) {
		try {
			StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
			sessionId = headers.getSessionId();
			clientName = SessionManager.sessionMap.get(sessionId);
			ClientMappingModel client= dss.getClientMapping(clientName);
			client.setConnected(false);
			dss.saveClientMapping(client);
			SessionManager.closeSession(sessionId);
		} catch (Exception e) {
			System.out.println("Couldn't disconnect due to: "+e);

		}
	}
}
