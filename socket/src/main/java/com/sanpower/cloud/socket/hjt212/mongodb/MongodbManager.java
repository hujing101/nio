package com.sanpower.cloud.socket.hjt212.mongodb;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Properties;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.sanpower.cloud.socket.hjt212.GlobalPropertyMgmt;
import com.sanpower.cloud.socket.hjt212.utils.Constants;

public class MongodbManager {
	private static MongoClient mongoClient = null;

	private MongodbManager() {
	}

	public static DB getDB(String dbName) {
		if (mongoClient == null) {
			init();
		}
		return mongoClient.getDB(dbName);
	}

	private static synchronized void init(){
		if (mongoClient != null) {
			return;
		}
		Properties properties = GlobalPropertyMgmt.getInstance()
				.getProperties();
		String ipList = properties.getProperty(Constants.MONGODB_IP_LIST);
		if (ipList == null || "".equals(ipList.trim())) {
			throw new RuntimeException("No mongodb ip list configuration!");
		}
		int connectionsPerHost = 100;
		String connectionsPerHostStr = properties.getProperty(Constants.CONNECTIONSPERHOST);
		try{
			connectionsPerHost = Integer.parseInt(connectionsPerHostStr);
		}catch(NumberFormatException e){
			
		}
		
		String[] ips = ipList.split("\\,");
		ServerAddress[] serverAddresses = new ServerAddress[ips.length];
		try{
			for (int i = 0; i < ips.length; i++) {
				serverAddresses[i] = new ServerAddress(ips[i]);
			}
		}catch(UnknownHostException e){
			throw new RuntimeException(e);
		}
		
		MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
		builder.connectionsPerHost(connectionsPerHost);
		mongoClient = new MongoClient(Arrays.asList(serverAddresses),builder.build());
	}
}