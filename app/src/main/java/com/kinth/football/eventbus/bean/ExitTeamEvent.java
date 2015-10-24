package com.kinth.football.eventbus.bean;
/**
 * 退出球队的事件
 * @author Sola
 *
 */
public class ExitTeamEvent {
	private String teamUuid;

	public ExitTeamEvent(String teamUuid) {
		super();
		this.teamUuid = teamUuid;
	}

	public String getTeamUuid() {
		return teamUuid;
	}

	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}
	
}

