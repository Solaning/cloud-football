package com.kinth.football.eventbus.bean;

/**
 * 修改球员头像事件
 * @author Sola
 *
 */
public class ModifyPlayerPhotoEvent {
	private String newPhoto;

	public ModifyPlayerPhotoEvent(String newPhoto) {
		super();
		this.newPhoto = newPhoto;
	}

	public String getNewPhoto() {
		return newPhoto;
	}

	public void setNewPhoto(String newPhoto) {
		this.newPhoto = newPhoto;
	}
	
}
