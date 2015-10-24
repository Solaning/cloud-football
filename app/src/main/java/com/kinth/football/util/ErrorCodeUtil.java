package com.kinth.football.util;

import com.kinth.football.chat.XmppManager;
import com.kinth.football.eventbus.bean.FinishAllActivityEvent;
import com.kinth.football.manager.UserManager;
import com.kinth.football.ui.LoginActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import de.greenrobot.event.EventBus;

public class ErrorCodeUtil {

	//报错  TODO
	/**
	 * Token失效
	 * @param mContext
	 */
	public static void ErrorCode401(Context mContext){
		UserManager.getInstance(mContext).clearCurrentUser();
		XmppManager.getInstance(mContext).disconnect();
		ImageLoader.getInstance().clearMemoryCache();
		EventBus.getDefault().post(new FinishAllActivityEvent());
		Intent intent = new Intent(mContext, LoginActivity.class);
		mContext.startActivity(intent);//TODO ??
		
		ShowToast(mContext, "登录失效，请重新登录");
		QuitWay.finishAll();  //关闭前面所有打开过的Activity
	}
	
	private static Toast toast;
	private static void ShowToast(Context context, String text){
		if(toast == null) {
			toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
		}
		toast.setText(text);
		toast.show();
	}
	
}
