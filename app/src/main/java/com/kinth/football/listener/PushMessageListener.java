package com.kinth.football.listener;

import com.kinth.football.bean.message.MessageContent;
import com.kinth.football.bean.message.PushMessageAbstract;

/**
 * 推送消息的监听
 * @author Sola
 *
 */
public abstract interface PushMessageListener {
	/**
	 * 接收到球队的推送消息
	 */
	public abstract void onTeamMessageListener(PushMessageAbstract<? extends MessageContent> message);
	
	/**
	 * 接收到比赛的推送消息
	 */
	public abstract void onMatchMessageListener(PushMessageAbstract<? extends MessageContent> message);
	
}
