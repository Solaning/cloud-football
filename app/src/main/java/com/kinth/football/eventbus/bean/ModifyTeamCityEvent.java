package com.kinth.football.eventbus.bean;
/**
 * EventBus修改球队城市的事件
 * @author Sola
 * 
 */
public class ModifyTeamCityEvent {
	private String teamUuid;
	private int newTeamCityId;//新的城市id
	private String newTeamCityName;//新的城市名

	public ModifyTeamCityEvent(String teamUuid, int newTeamCityId,
			String newTeamCityName) {
		super();
		this.teamUuid = teamUuid;
		this.newTeamCityId = newTeamCityId;
		this.newTeamCityName = newTeamCityName;
	}

	public int getNewTeamCityId() {
		return newTeamCityId;
	}

	public void setNewTeamCityId(int newTeamCityId) {
		this.newTeamCityId = newTeamCityId;
	}

	public String getTeamUuid() {
		return teamUuid;
	}

	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}

	public String getNewTeamCityName() {
		return newTeamCityName;
	}

	public void setNewTeamCityName(String newTeamCityName) {
		this.newTeamCityName = newTeamCityName;
	}
	
}
