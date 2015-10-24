package com.kinth.football.eventbus.message;

/**
 * 申请加入球队确认的推送消息事件
 * @author Sola
 *
 */
public class RequestJoinTeamConfirmPushMessageEvent {
	private String json;

	public RequestJoinTeamConfirmPushMessageEvent(String json) {
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
