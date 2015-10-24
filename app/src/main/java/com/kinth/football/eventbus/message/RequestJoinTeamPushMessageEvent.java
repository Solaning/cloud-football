package com.kinth.football.eventbus.message;

/**
 * 申请加入球队推送消息事件
 * @author Sola
 * 
 */
public class RequestJoinTeamPushMessageEvent {
	private String json;

	public RequestJoinTeamPushMessageEvent(String json) {
		super();
		this.json = json;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

}
