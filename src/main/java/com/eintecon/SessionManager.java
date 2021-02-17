package com.eintecon;

import java.util.HashMap;
import java.util.Map;


public class SessionManager {
	public static Map<String, String> sessionMap = new HashMap<String, String>();

	public static void openSession(String session, String userId) {
		sessionMap.put(session, userId);
	}
	
	public static void closeSession(String session) {
		sessionMap.remove(session);
	}
	
	public static void broadCast(String message) {
		for (Map.Entry<String, String> entry : sessionMap.entrySet()) {
			System.out.println(entry.getKey());
		}
	}
}
