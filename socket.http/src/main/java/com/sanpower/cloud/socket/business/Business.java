package com.sanpower.cloud.socket.business;

import java.util.Properties;

import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.sanpower.cloud.socket.common.Constants;
import com.sanpower.cloud.socket.common.GlobalPropertyMgmt;
import com.sanpower.cloud.socket.http.HttpRequest;
import com.sanpower.cloud.socket.mongodb.MongodbManager;

public class Business {

	public void exec() {
		Properties properties = GlobalPropertyMgmt.getInstance()
				.getProperties();
		/*
		 * Map<String,String> url_collection = new HashMap<String,String>();
		 * String url1 = properties.getProperty("url1"); String collectionName1
		 * = properties.getProperty("collectionName1"); url_collection.put(url1,
		 * collectionName1);
		 * 
		 * String url2 = properties.getProperty("url2"); String collectionName2
		 * = properties.getProperty("collectionName2"); url_collection.put(url2,
		 * collectionName2);
		 * 
		 * String url3 = properties.getProperty("url3"); String collectionName3
		 * = properties.getProperty("collectionName3"); url_collection.put(url3,
		 * collectionName3);
		 */

		for (Object obj : properties.keySet()) {
			try {
				String key = (String) obj;
				if (key.startsWith("url")) {
					String url = properties.getProperty(key);
					String collectionName = url.substring(
							url.lastIndexOf("/") + 1, url.lastIndexOf("."));
					String json = HttpRequest.sendGet(url);
					if (json != null && !"".equals(json)) {
						json = json.replaceAll("id", "_id");
						json = removeUTF8BOM(json);
						BasicDBList basicDBList = (BasicDBList) JSON.parse(json);
						 for (Object ruleObj : basicDBList) {
							 DBObject dBObject = (DBObject) ruleObj;
							 saveData(dBObject, collectionName);
						 }
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public static final String UTF8_BOM = "\uFEFF";
	private static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }

	private void saveData(DBObject dbObject, String collectionName) {
		DB db = MongodbManager.getDB(Constants.MONGODB_DATABASE_NAME);
		DBCollection allDataCollection = db.getCollection(collectionName);
		allDataCollection.save(dbObject);
	}
}
