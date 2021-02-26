package com.eintecon.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigService {
	
private static Properties prop = new Properties();
	
	private static Properties read() {
		
		try (FileInputStream ip = new FileInputStream("/src/main/resources/application.properties")) {
			prop.load(ip);
		} catch (IOException io) {
            io.printStackTrace();
        }
		
		return prop;
	}
	public static String getDateTimeStyle() {
		return read().getProperty("date_format");
	}
	public static String getUnsignedPath() {
		return read().getProperty("unsigned_documents_path");
	}
	public static String getSignedPath() {
		return read().getProperty("signed_documents_path");
	}
}
