package com.kinth.football.network;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.kinth.football.config.UrlConstants;
import com.kinth.football.manager.UserManager;

/**
 * 获取省份
 * 
 */
public class UserNetworkManager {
	UserManager userManager;
	private static volatile UserNetworkManager mNetWorkManager;
	private static Object INSTANCE_LOCK = new Object();
	private Context mContext;
	
	public static UserNetworkManager getInstance(Context context) {
		if (mNetWorkManager == null)
			synchronized (INSTANCE_LOCK) {
				if (mNetWorkManager == null)
					mNetWorkManager = new UserNetworkManager();
				mNetWorkManager.init(context);
			}
		return mNetWorkManager;
	}
	
	public void init(Context mContext) {
		this.mContext = mContext;
		userManager = UserManager.getInstance(mContext);
	}
	
	/**
	 * 修改用户地区
	 * @param token
	 */
	public void modifyUserErea(final int cityId,final String token,Listener<Void>listener,ErrorListener errorListener) {
		String url = UrlConstants.IP_ADDRESS +"/v1.0/player.json";
		RequestQueue requestQueue = Volley.newRequestQueue(mContext);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cityId", cityId);
		
		JSONObject jsonObject = new JSONObject(map);
		JsonRequest<Void> jsonRequest = new JsonNoResponseRequest(
				Method.PUT, url, jsonObject, listener, errorListener) {
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = NetWorkManager.genHeaderParameters();
				headers.put("Authorization", "Bearer "+token);
				headers.put("Content-Type", "application/json");
				return headers;
			}
		};
		requestQueue.add(jsonRequest);
	}
	/**
	 * 修改用户昵称
	 * @param token
	 */
	public void modifyUserSex(final String gender,Listener<Void>listener,ErrorListener errorListener) {
		//安全措施
		if (userManager.getCurrentUser()==null) {
			errorListener.onErrorResponse(new VolleyError());
			return;
		}
		final String token = userManager.getCurrentUser().getToken()+"";
		String url = UrlConstants.IP_ADDRESS +"/v1.0/player.json";
		RequestQueue requestQueue = Volley.newRequestQueue(mContext);
		Map<String, Object> map = new HashMap<String, Object>();
			map.put("gender", gender);
		
		JSONObject jsonObject = new JSONObject(map);
		JsonRequest<Void> jsonRequest = new JsonNoResponseRequest(
				Method.PUT, url, jsonObject, listener, errorListener) {
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = NetWorkManager.genHeaderParameters();
				headers.put("Authorization", "Bearer "+token);
				headers.put("Content-Type", "application/json");
				return headers;
			}
		};
		requestQueue.add(jsonRequest);
	}
	
	/**
	 * 修改用户昵称
	 * @param token
	 */
	public void modifyUserNameOrEmail(String flag,final String data,final String token,Listener<Void>listener,ErrorListener errorListener) {
		String url = UrlConstants.IP_ADDRESS +"/v1.0/player.json";
		RequestQueue requestQueue = Volley.newRequestQueue(mContext);
		Map<String, Object> map = new HashMap<String, Object>();
		if (flag.equals("nick")) {
			map.put("name", data);
		}else {
			map.put("email", data);
		}
		JSONObject jsonObject = new JSONObject(map);
		JsonRequest<Void> jsonRequest = new JsonNoResponseRequest(
				Method.PUT, url, jsonObject, listener, errorListener) {
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = NetWorkManager.genHeaderParameters();
				headers.put("Authorization", "Bearer "+token);
				headers.put("Content-Type", "application/json");
				return headers;
			}
		};
		requestQueue.add(jsonRequest);
	}
	/**
	 * 修改用户昵称
	 * @param token
	 */
	public void modifyUserIcon(final String iconUrl,Listener<Void>listener,ErrorListener errorListener) {
		if (userManager.getCurrentUser()==null) {
			errorListener.onErrorResponse(new VolleyError());
			return;
		}
		final String token = userManager.getCurrentUser().getToken()+"";
		String url = UrlConstants.IP_ADDRESS +"/v1.0/player.json";
		RequestQueue requestQueue = Volley.newRequestQueue(mContext);
		Map<String, Object> map = new HashMap<String, Object>();
			map.put("picture", iconUrl);
			
		JSONObject jsonObject = new JSONObject(map);
		JsonRequest<Void> jsonRequest = new JsonNoResponseRequest(
				Method.PUT, url, jsonObject, listener, errorListener) {
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = NetWorkManager.genHeaderParameters();
				headers.put("Authorization", "Bearer "+token);
				headers.put("Content-Type", "application/json");
				return headers;
			}
		};
		requestQueue.add(jsonRequest);
	}
	/**
	 * 修改用户昵称
	 * @param token
	 */
	public void modifyUserIconAndNick(final String iconUrl,final String nick,final String position,final int height,final int weight,Listener<Void>listener,ErrorListener errorListener) {
		if (userManager.getCurrentUser()==null) {
			errorListener.onErrorResponse(new VolleyError());
			return;
		}
		final String token = userManager.getCurrentUser().getToken()+"";
		String url = UrlConstants.IP_ADDRESS +"/v1.0/player.json";
		RequestQueue requestQueue = Volley.newRequestQueue(mContext);
		Map<String, Object> map = new HashMap<String, Object>();
			map.put("picture", iconUrl);
			map.put("name", nick);
			map.put("position", position);
			map.put("height", height);
			map.put("weight", weight);
		JSONObject jsonObject = new JSONObject(map);
		JsonRequest<Void> jsonRequest = new JsonNoResponseRequest(
				Method.PUT, url, jsonObject, listener, errorListener) {
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = NetWorkManager.genHeaderParameters();
				headers.put("Authorization", "Bearer "+token);
				headers.put("Content-Type", "application/json");
				return headers;
			}
		};
		requestQueue.add(jsonRequest);
	}
}
