package com.kinth.football;

import java.util.ArrayList;

import com.kinth.football.chat.ChatService;
import com.kinth.football.listener.NetStateChangeListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetStateReceiver extends BroadcastReceiver {

	// 网络变化事件监听
	public static ArrayList<NetStateChangeListener> netList = new ArrayList<NetStateChangeListener>();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
			if(netList.size() > 0) {// 有监听的时候，传递下去
				for (int i = 0; i < netList.size(); i++) {
					netList.get(i).onNetStateChange();
				}
			}
			return;
		}
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			Intent service = new Intent(context, ChatService.class);// 5.0中需要显式调用
			context.startService(service);// 启动指定Service
			return;
		}
	}

}
