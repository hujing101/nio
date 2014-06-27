package com.sanpower.cloud.socket.hjt212.thread;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.sanpower.cloud.socket.hjt212.model.DataSegment;
import com.sanpower.cloud.socket.hjt212.mongodb.MongodbManager;
import com.sanpower.cloud.socket.hjt212.utils.Constants;
import com.sanpower.cloud.socket.hjt212.utils.JsonUtils;

public class HandlerRunable implements Runnable {
	private static final Logger logger = LoggerFactory
			.getLogger(HandlerRunable.class);
	private ChannelHandlerContext ctx;
	private DataSegment message;

	public HandlerRunable(ChannelHandlerContext ctx, DataSegment message) {
		this.ctx = ctx;
		this.message = message;
	}

	@Override
	public void run() {
		try {
			addDataToMongoDb((DataSegment) message);
			ctx.writeAndFlush("");// TODO 回写数据
		} catch (Throwable e) {
			logger.error("HandlerRunable exec fail!", e);
		}
	}

	private void addDataToMongoDb(DataSegment dataSegment) {
		if (dataSegment != null) {
			DB db = MongodbManager.getDB(Constants.MONGODB_DATABASE_NAME);
			DBCollection allDataCollection = db
					.getCollection(Constants.MONGODB_ALL_DATA_COLLECTION);
			DBObject dbObject = (DBObject) JSON.parse(JsonUtils
					.objectToJson(dataSegment));
			allDataCollection.insert(dbObject);
			
			DBCollection cpDetailCollection = db
					.getCollection(Constants.MONGODB_CP_DETAIL_COLLECTION);
			
			Map<String,Object> cpMap= dataSegment.getCP();
			
			Map<String,Object> saveCp = new HashMap<String,Object>();
			saveCp.put("qn", dataSegment.getQN());
			saveCp.put("pnum", dataSegment.getPNUM());
			saveCp.put("pno", dataSegment.getPNO());
			saveCp.put("st", dataSegment.getST());
			saveCp.put("cn", dataSegment.getCN());
			saveCp.put("pw", dataSegment.getPW());
			saveCp.put("mn", dataSegment.getMN());
			saveCp.put("flag", dataSegment.getFlag());
			Object dataTime = cpMap.get("DataTime");
			for(String key : cpMap.keySet()){
				if(key.indexOf('-')>0){
					String[] keySplit = key.split("\\-");
					/*cp.setBh_tag(key);
					cp.setBh(keySplit[0]);
					cp.setTag(keySplit[1]);
					cp.setValue(cpMap.get(key));*/
					if(dataTime!=null){
						saveCp.put("DataTime", dataTime);
					}
					saveCp.put("bh_tag", key);
					saveCp.put("bh", keySplit[0]);
					saveCp.put("tag", keySplit[1]);
					saveCp.put("value",cpMap.get(key));
				}
				DBObject cpDbObject = (DBObject) JSON.parse(JsonUtils
						.objectToJson(saveCp));
				cpDetailCollection.insert(cpDbObject);
			}
		}
	}
}
