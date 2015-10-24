package com.kinth.football.network;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;

public interface INetworkManager {
	public void logoutAction(final String accessToken,
			Response.Listener<String> listener, ErrorListener errorListener);
	
}
