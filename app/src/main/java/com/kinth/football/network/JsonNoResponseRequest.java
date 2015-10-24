package com.kinth.football.network;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

public class JsonNoResponseRequest extends JsonRequest<Void>{

	public JsonNoResponseRequest(int method, String url, JSONObject jsonRequest,
			Listener<Void> listener, ErrorListener errorListener) {
		super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener, errorListener);
	}

	public JsonNoResponseRequest(int method, String url, JSONArray jsonRequest,
			Listener<Void> listener, ErrorListener errorListener) {
		super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener, errorListener);
	}
	
	@Override
	protected Response<Void> parseNetworkResponse(NetworkResponse response) {
		return Response.success(null,
                HttpHeaderParser.parseCacheHeaders(response));
	}
}
