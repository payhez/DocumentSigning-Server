package com.eintecon.model;

import org.springframework.data.annotation.Id;

public class ClientMappingModel {
	private String clientID;
	private String erpID;
	@Id
	private String id;
	private String message;
	private boolean connected;
	
	public boolean isConnected() {
		return connected;
	}
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	public String getClientID() {
		return clientID;
	}
	
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}
	public String getErpID() {
		return erpID;
	}
	public void setErpID(String erpID) {
		this.erpID = erpID;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
