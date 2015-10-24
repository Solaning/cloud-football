package com.kinth.football.eventbus.bean;

/**
 * 修改球队介绍的事件
 * @author Sola
 *
 */
public class ModifyTeamDescriptionEvent {
	private String teamUuid;
	private String newDescription;
	
	public ModifyTeamDescriptionEvent(String teamUuid, String newDescription) {
		super();
		this.teamUuid = teamUuid;
		this.newDescription = newDescription;
	}

	public String getTeamUuid() {
		return teamUuid;
	}

	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}

	public String getNewDescription() {
		return newDescription;
	}

	public void setNewDescription(String newDescription) {
		this.newDescription = newDescription;
	}
	
}
