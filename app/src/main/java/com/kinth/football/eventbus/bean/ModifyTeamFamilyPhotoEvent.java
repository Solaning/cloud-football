package com.kinth.football.eventbus.bean;

/**
 * 修改球队全家福
 * @author Sola
 *
 */
public class ModifyTeamFamilyPhotoEvent {
	private String teamUuid;
	private String newFamilyPhoto;
	
	public ModifyTeamFamilyPhotoEvent(String teamUuid, String newFamilyPhoto) {
		super();
		this.teamUuid = teamUuid;
		this.newFamilyPhoto = newFamilyPhoto;
	}

	public String getTeamUuid() {
		return teamUuid;
	}

	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}

	public String getNewFamilyPhoto() {
		return newFamilyPhoto;
	}

	public void setNewFamilyPhoto(String newFamilyPhoto) {
		this.newFamilyPhoto = newFamilyPhoto;
	}
	
}
