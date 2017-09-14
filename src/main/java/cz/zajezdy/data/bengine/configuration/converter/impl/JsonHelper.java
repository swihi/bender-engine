package cz.zajezdy.data.bengine.configuration.converter.impl;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class JsonHelper {

	public static String toJson(Object o) {
		Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
		return gson.toJson(o);
	}

	public static <T> T fromJson(String json, Type type) {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
		return gson.fromJson(json, type);
	}
}
