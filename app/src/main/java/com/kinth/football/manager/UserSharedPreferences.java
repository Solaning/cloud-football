package com.kinth.football.manager;

import com.kinth.football.CustomApplcation;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSharedPreferences {
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor sharedEditor;

	public UserSharedPreferences(Context paramContext) {
		if(paramContext == null)
			paramContext = CustomApplcation.getInstance();
		this.sharedPreferences = paramContext.getSharedPreferences("bmob_sp", 0);
		this.sharedEditor = sharedPreferences.edit();
	}
	
	public UserSharedPreferences(Context paramContext, SharedPreferences sharedPreferences) {
		this.sharedPreferences = sharedPreferences;
		this.sharedEditor = sharedPreferences.edit();
	}

	public void Code(String paramString, boolean paramBoolean) {
		this.sharedEditor.putBoolean(paramString, true);
		this.sharedEditor.commit();
	}

	public void saveString(String paramString1, String paramString2) {
		this.sharedEditor.putString(paramString1, paramString2);
		this.sharedEditor.commit();
	}

	public boolean saveBoolean(String paramString, boolean paramBoolean) {
		return this.sharedPreferences.getBoolean(paramString, false);
	}

	public String getValue(String key, String defValue) {
		return this.sharedPreferences.getString(key, defValue);
	}

	public void remove(String paramString) {
		this.sharedEditor.remove(paramString);
		this.sharedEditor.commit();
	}
}