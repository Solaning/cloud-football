package com.kinth.football.eventbus.message;

/**
 * 球队成员邀请事件
 * @author Sola
 *
 */
public class TeamPlayerInvitationPushMessageEvent {
	private String json;

	public TeamPlayerInvitationPushMessageEvent(String json) {
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
