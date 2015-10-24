package com.kinth.football.eventbus.bean;

/**
 * 修改球队第三队服
 * @author Sola
 *
 */
public class ModifyTeamAlternetJerseyEvent {
	private String teamUuid;
	private String newAlternetJersey;
	
	public ModifyTeamAlternetJerseyEvent(String teamUuid,
			String newAlternetJersey) {
		super();
		this.teamUuid = teamUuid;
		this.newAlternetJersey = newAlternetJersey;
	}
	public String getTeamUuid() {
		return teamUuid;
	}
	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}
	public String getNewAlternetJersey() {
		return newAlternetJersey;
	}
	public void setNewAlternetJersey(String newAlternetJersey) {
		this.newAlternetJersey = newAlternetJersey;
	}
	
	
}
