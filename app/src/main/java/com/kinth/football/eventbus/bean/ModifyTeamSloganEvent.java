package com.kinth.football.eventbus.bean;

/**
 * 修改球队口号事件
 * @author Sola
 *
 */
public class ModifyTeamSloganEvent {
	private String teamUuid;
	private String newSlogan;
	
	public ModifyTeamSloganEvent(String teamUuid, String newSlogan) {
		super();
		this.teamUuid = teamUuid;
		this.newSlogan = newSlogan;
	}

	public String getTeamUuid() {
		return teamUuid;
	}

	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}

	public String getNewSlogan() {
		return newSlogan;
	}

	public void setNewSlogan(String newSlogan) {
		this.newSlogan = newSlogan;
	}
	
}
