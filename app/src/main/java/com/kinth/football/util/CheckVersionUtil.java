package com.kinth.football.util;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.bean.Update;
import com.kinth.football.bean.UpdateVersionResponse;
import com.kinth.football.manager.UpdateVersionManager;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;

public class CheckVersionUtil {

	static ProgressDialog dialog = null;
	
	// 检查新版本
	public static void updataVersion(final Context mContext, final boolean isShowDialog) {
		if(isShowDialog){
			dialog = ProgressDialog.show(mContext, "提示", "检查中...", false, false);
		}
		NetWorkManager.getInstance(mContext).updateVersion(
				NetWorkManager.UPDATE_ANDROID_VERSION,
				getCurrentVersion(mContext),
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if(isShowDialog){
							dialog.dismiss();
						}
						Gson gson = new Gson();
						UpdateVersionResponse updateRe = gson.fromJson(
								response.toString(),
								new TypeToken<UpdateVersionResponse>() {
								}.getType());
						if (updateRe != null) {
							if (updateRe.isLasted()) { // true,说明当前版本为最新版本
								if(isShowDialog){
									Toast.makeText(mContext, "暂无更新", Toast.LENGTH_SHORT).show();
								}
							} else {    // 当前版本不是最新版本，则下载更新
								Update update = updateRe.getUpdate();
								UpdateVersionManager updateManager = new UpdateVersionManager(
										mContext, update);
								updateManager.start();
							}
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						if(isShowDialog){
							dialog.dismiss();
						}
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							Toast.makeText(mContext, "当前网络不可用", Toast.LENGTH_SHORT).show();
						} else if (error.networkResponse == null) {
							//ShowToast("CheckVersionUtil-updataVersion-服务器连接错误");
						} else if (error.networkResponse.statusCode == 400) {
//							ShowToast("传递参数为空，无法更新");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else if (error.networkResponse.statusCode == 404) {
//							String response = new String(error.networkResponse.data);
							if(isShowDialog){
								Toast.makeText(mContext, "暂无更新", Toast.LENGTH_SHORT).show();	
							}
						}
					}
				});
	}

	public static String getCurrentVersion(Context context) {
		// 获取packagemanager的实例
		PackageManager pm = context.getPackageManager();
		// getPackageName()是当前类的包名，0代表是获取版本信息
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return pi.versionName;
	}
	
}
