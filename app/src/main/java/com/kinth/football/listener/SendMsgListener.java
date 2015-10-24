package com.kinth.football.listener;

import com.kinth.football.chat.bean.ChatMsg;

public interface SendMsgListener {
	public void onSuccess(ChatMsg chatMsg);
	public void onFailure(ChatMsg chatMsg);
}
