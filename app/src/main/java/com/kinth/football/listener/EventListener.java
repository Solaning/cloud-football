package com.kinth.football.listener;

import com.kinth.football.chat.bean.ChatMsg;

public abstract interface EventListener {
	/**
	 * 收到消息的接口
	 * 
	 * @param msg
	 */
	public abstract void onMessage(ChatMsg msg);

	/**
	 * 网络变化的接口
	 * 
	 * @param msg
	 */
	public abstract void onNetChange(boolean paramBoolean);
	
}