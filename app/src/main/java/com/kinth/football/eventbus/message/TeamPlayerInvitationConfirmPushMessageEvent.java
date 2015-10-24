package com.kinth.football.eventbus.message;

/**
 * 球队成员邀请确认事件
 * @author Sola
 *
 */
public class TeamPlayerInvitationConfirmPushMessageEvent {
	private String json;

	public TeamPlayerInvitationConfirmPushMessageEvent(String json) {
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
