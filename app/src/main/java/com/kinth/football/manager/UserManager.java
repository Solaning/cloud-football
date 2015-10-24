package com.kinth.football.manager;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.kinth.football.CustomApplcation;
import com.kinth.football.bean.User;

/**
 * 用户账户管理
 */
public class UserManager {
	private static volatile UserManager INSTANCE;
	private static Object INSTANCE_LOCK = new Object();
	
	private User user;
	private UserSharedPreferences userSharedPreferences;
	
	public static UserManager getInstance(Context paramContext) {
		if (INSTANCE == null)
			synchronized (INSTANCE_LOCK) {
				if (INSTANCE == null)
					INSTANCE = new UserManager();
				INSTANCE.init(paramContext);
			}
		return INSTANCE;
	}

	public void init(Context paramContext) {
		userSharedPreferences = new UserSharedPreferences(paramContext);
	}

	/**
	 * 保存当前用户
	 * @param user
	 */
	public void saveCurrentUser(User user) {
		Gson gson = new Gson();
		String userJson = gson.toJson(user);
		userSharedPreferences.saveString("user", userJson);  //将用户JSON数据转换为字符串，保存
	}
	
	public void clearCurrentUser() {
		userSharedPreferences.remove("user");
		user = null;
		CustomApplcation.getInstance().logout();
	}

	public  User getCurrentUser(){
		if(user != null && !TextUtils.isEmpty(user.getToken())){
			return user;
		}else{
			String str = userSharedPreferences.getValue("user",
					"");
			if (TextUtils.isEmpty(str)) {
				return null;
			} else {
				Gson gson = new Gson();
				user = gson.fromJson(str, User.class);
				return user;
			}
		}
	}
	
	public void setCurrentUser(User user){
		this.user = user;
	}
	
	public  String getCurrentUserPhone() {
		if(user != null){
			return user.getPlayer().getPhone();
		}else{
			String str = userSharedPreferences.getValue("user",
					"");
			if (!str.equals("")) {
				Gson gson = new Gson();
				User currentUser = gson.fromJson(str, User.class);
				if (currentUser==null) {
					return "";
				}else {
					user = currentUser;
					return currentUser.getPlayer().getPhone();
				}
			} else {
				return "";
			}
		}
	}
}
