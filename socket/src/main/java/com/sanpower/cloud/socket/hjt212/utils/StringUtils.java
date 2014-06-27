package com.sanpower.cloud.socket.hjt212.utils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class StringUtils {
	public static boolean isEmpty(String str) {
		if (str == null || str.length() == 0 || "".equals(str.trim())) {
			return true;
		}
		return false;
	}

	public static String[] getFiledName(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		String[] fieldNames = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			fieldNames[i] = fields[i].getName();
		}
		return fieldNames;
	}

	public static Object getValue(String str){
		Object obj = str;
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		 
		try {
			obj = formatter.parse(str);
			return str;
		} catch (ParseException e) {
			
		}
		
		try{
			obj = Integer.parseInt(str);
			return obj;
		}catch(NumberFormatException e){
			
		}
		
		try{
			obj = Float.parseFloat(str);
			return obj;
		}catch(NumberFormatException e){
			
		}
		
		try{
			obj = Double.parseDouble(str);
			return obj;
		}catch(NumberFormatException e){
			
		}
		
		return obj;
	}
	
	public static void main(String[] args) {
		//getFiledName(DataSegment.class);
		System.out.println(0x0a);
	}
}
