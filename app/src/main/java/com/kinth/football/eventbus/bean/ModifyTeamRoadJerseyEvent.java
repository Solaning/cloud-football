package com.kinth.football.eventbus.bean;

/**
 * 修改球队客场队服事件
 * @author Sola
 *
 */
public class ModifyTeamRoadJerseyEvent {
	private String teamUuid;
	private String newRoadJersey;
	
	public ModifyTeamRoadJerseyEvent(String teamUuid, String newRoadJersey) {
		super();
		this.teamUuid = teamUuid;
		this.newRoadJersey = newRoadJersey;
	}

	public String getTeamUuid() {
		return teamUuid;
	}

	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}

	public String getNewRoadJersey() {
		return newRoadJersey;
	}

	public void setNewRoadJersey(String newRoadJersey) {
		this.newRoadJersey = newRoadJersey;
	}
	
	
}
