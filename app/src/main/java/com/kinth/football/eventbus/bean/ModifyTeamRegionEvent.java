package com.kinth.football.eventbus.bean;

/**
 * 修改球队地区的事件
 * @author Sola
 *
 */
public class ModifyTeamRegionEvent {
	private String teamUuid;
	private int newRegionId;
	private String newRegionName;
	
	public ModifyTeamRegionEvent(String teamUuid, int newRegionId,
			String newRegionName) {
		super();
		this.teamUuid = teamUuid;
		this.newRegionId = newRegionId;
		this.newRegionName = newRegionName;
	}

	public String getTeamUuid() {
		return teamUuid;
	}

	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}

	public int getNewRegionId() {
		return newRegionId;
	}

	public void setNewRegionId(int newRegionId) {
		this.newRegionId = newRegionId;
	}

	public String getNewRegionName() {
		return newRegionName;
	}

	public void setNewRegionName(String newRegionName) {
		this.newRegionName = newRegionName;
	}
	
	
}
