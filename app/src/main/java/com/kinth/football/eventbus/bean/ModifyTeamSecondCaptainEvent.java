package com.kinth.football.eventbus.bean;

/**
 * 修改球队队副的事件
 * @author Sola
 *
 */
public class ModifyTeamSecondCaptainEvent {
	private String teamUuid;
	private String newCaptainUuid;
	
	public ModifyTeamSecondCaptainEvent(String teamUuid, String newCaptainUuid) {
		super();
		this.teamUuid = teamUuid;
		this.newCaptainUuid = newCaptainUuid;
	}

	public String getTeamUuid() {
		return teamUuid;
	}

	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}

	public String getNewCaptainUuid() {
		return newCaptainUuid;
	}

	public void setNewCaptainUuid(String newCaptainUuid) {
		this.newCaptainUuid = newCaptainUuid;
	}
	
	
}
