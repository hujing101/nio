package com.sanpower.cloud.socket.common;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

	@SuppressWarnings("rawtypes")
	public static JsonNode mapToJsonNode(Map map) {
		if (map == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.valueToTree(map);
	}

	public static String objectToJson(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		String str = null;
		try {
			str = mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return str;
	}

	public static String toJSon(Object object) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
