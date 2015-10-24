package com.kinth.football.eventbus.bean;

/**
 * 修改队徽事件
 * 
 * @author Sola
 * 
 */
public class ModifyTeamBadgeEvent {
	private String teamUuid;
	private String newBadge;

	public ModifyTeamBadgeEvent(String teamUuid, String newBadge) {
		super();
		this.teamUuid = teamUuid;
		this.newBadge = newBadge;
	}

	public String getTeamUuid() {
		return teamUuid;
	}

	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}

	public String getNewBadge() {
		return newBadge;
	}

	public void setNewBadge(String newBadge) {
		this.newBadge = newBadge;
	}

}
