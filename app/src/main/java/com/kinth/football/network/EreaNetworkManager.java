package com.kinth.football.network;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kinth.football.config.UrlConstants;

/**
 * 获取省份
 * @author BJ
 *
 */
public class EreaNetworkManager {
	
	private static volatile EreaNetworkManager mNetWorkManager;
	private static Object INSTANCE_LOCK = new Object();
	private Context mContext;
	public static EreaNetworkManager getInstance(Context context) {
		
		if (mNetWorkManager == null)
			synchronized (INSTANCE_LOCK) {
				if (mNetWorkManager == null)
					mNetWorkManager = new EreaNetworkManager();
				mNetWorkManager.init(context);
			}
		return mNetWorkManager;
	}
	public void init(Context mContext) {
		this.mContext = mContext;
	}
	
	/**
	 *获取省份
	 * @param token
	 * @param listener
	 * @param errorListener
	 */
	public  void getRrovince(final String token,Listener<String>listener,ErrorListener errorListener) {
		RequestQueue requestQueue = Volley.newRequestQueue(mContext);
		String url = UrlConstants.IP_ADDRESS + "/v1.0/province.json";
		StringRequest mStringRequest = new StringRequest(url, listener,
				errorListener) {
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Authorization", "Bearer " + token);
				return headers;
			}
		};
		requestQueue.add(mStringRequest);
	}
	
	/**
	 * 获取城市信息
	 * @param token
	 * @param provinceId
	 * @param listener
	 * @param errorListener
	 */
	public  void getCity(final String token,String provinceId,Listener<String>listener,ErrorListener errorListener) {
		RequestQueue requestQueue = Volley.newRequestQueue(mContext);
		
		String url = UrlConstants.IP_ADDRESS + "/v1.0/province/"+provinceId+".json";

		StringRequest mStringRequest = new StringRequest(url, listener,
				errorListener) {
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Authorization", "Bearer " + token);
				return headers;
			}
		};
		requestQueue.add(mStringRequest);
	}
}
