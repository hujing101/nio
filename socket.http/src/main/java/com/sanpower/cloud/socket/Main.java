package com.sanpower.cloud.socket;

import com.sanpower.cloud.socket.business.Business;

public class Main {
	public static void main(String[] args) {
		new Business().exec();
		/*String url = "http://172.10.2.190:8081/search/456.json";
		String collectionName = url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
		System.out.println(collectionName);*/
	}
}
