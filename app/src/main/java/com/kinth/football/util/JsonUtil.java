package com.kinth.football.util;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {
	
	public static String getString(JSONObject jsonObject, String key)
	  {
		try {
			return jsonObject.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	  }

	public static int getInt(JSONObject jsonObject, String key) {
		try {
			return jsonObject.getInt(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
}
