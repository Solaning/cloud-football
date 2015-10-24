package com.kinth.football.eventbus.message;

/**
 * 退出球队推送消息事件
 * @author Sola
 *
 */
public class ExitTeamPushMessageEvent {
	private String json;

	public ExitTeamPushMessageEvent(String json) {
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
