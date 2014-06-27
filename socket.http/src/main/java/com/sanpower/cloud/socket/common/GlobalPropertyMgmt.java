package com.sanpower.cloud.socket.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalPropertyMgmt {
	private static final Logger logger = LoggerFactory
			.getLogger(GlobalPropertyMgmt.class);
	private static GlobalPropertyMgmt instance = new GlobalPropertyMgmt();
	private Properties properties;

	private GlobalPropertyMgmt() {
	}

	public static GlobalPropertyMgmt getInstance() {
		return instance;
	}

	public Properties getProperties() {
		if (properties == null) {
			loadProperties();
		}
		return properties;
	}

	private void loadProperties() {
		String fileName = "/socket-http.properties";
		String envFileName = System.getProperty("configFile");
		//String fileName = "/usr/local/xiaoqiaoliushui/socket/socket-http.properties";
		InputStream input = null;
		if(envFileName!=null&&!"".equals(envFileName.trim())){
			try {
				input = new FileInputStream(envFileName);
			} catch (FileNotFoundException e) {
				logger.error("load "+envFileName +" error",e);
				throw new RuntimeException(e);
			}
		}else{
			input = this.getClass().getResourceAsStream(fileName);
		}
		
		if(input != null){
			Properties properties = new Properties();
			try {
				properties.load(input);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			this.properties = properties;
		}else{
			throw new RuntimeException(fileName + "--> File does not exist!");
		}
	}
}
