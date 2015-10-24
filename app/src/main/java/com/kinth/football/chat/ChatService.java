package com.kinth.football.chat;

import com.kinth.football.bean.User;
import com.kinth.football.manager.UserManager;
import com.kinth.football.util.LogUtil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class ChatService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO 自动生成的方法存根
		return null;
	}

	/**
	 * Service被创建时回调该方法
	 */
	@Override
	public void onCreate() {
		// TODO 自动生成的方法存根
		super.onCreate();
	}

	/**
	 * Service被启动时调用该方法
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO 自动生成的方法存根
		loginChatServ();
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * xmpp连接
	 */
	private void loginChatServ() {
		User user = UserManager.getInstance(this).getCurrentUser();
		if (user == null) {
			return;
		}
		//XmppManager.getInstance(this).disconnect();	//有时候会收不到信息，但打开应用有，可能是上一个连接没有断开
		XmppManager.getInstance(this)
				.loginAndAddChatManagerListener(this,
						user.getPlayer().getPhone(), user.getToken(), new ConnectionImp() {
							@Override
							public void onSucc() {
								LogUtil.i("登录成功(后台)");
							}

							@Override
							public void onFail() {
								LogUtil.i("登录失败");
							}
						});
	}

}
