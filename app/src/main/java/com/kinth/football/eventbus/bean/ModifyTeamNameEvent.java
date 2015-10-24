package com.kinth.football.eventbus.bean;

/**
 * 修改球队名称的事件
 * @author Sola
 * 
 */
public class ModifyTeamNameEvent {
	private String newTeamName;//新的队名

	public ModifyTeamNameEvent(String newTeamName) {
		super();
		this.newTeamName = newTeamName;
	}

	public String getNewTeamName() {
		return newTeamName;
	}

	public void setNewTeamName(String newTeamName) {
		this.newTeamName = newTeamName;
	}

}
