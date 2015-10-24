package com.kinth.football.eventbus.bean;

/**
 * 修改球队主场队服事件
 * @author Sola
 *
 */
public class ModifyTeamHomeJerseyEvent {
	private String teamUuid;
	private String newHomeJersey;
	
	public ModifyTeamHomeJerseyEvent(String teamUuid, String newHomeJersey) {
		super();
		this.teamUuid = teamUuid;
		this.newHomeJersey = newHomeJersey;
	}

	public String getTeamUuid() {
		return teamUuid;
	}

	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}

	public String getNewHomeJersey() {
		return newHomeJersey;
	}

	public void setNewHomeJersey(String newHomeJersey) {
		this.newHomeJersey = newHomeJersey;
	}
	
}
