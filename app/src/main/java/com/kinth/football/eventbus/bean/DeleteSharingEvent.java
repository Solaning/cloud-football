package com.kinth.football.eventbus.bean;

/**
 * 删除朋友圈动态事件
 * @author Sola
 *
 */
public class DeleteSharingEvent {
	private String sharingUuid;

	public DeleteSharingEvent(String sharingUuid) {
		super();
		this.sharingUuid = sharingUuid;
	}

	public String getSharingUuid() {
		return sharingUuid;
	}

	public void setSharingUuid(String sharingUuid) {
		this.sharingUuid = sharingUuid;
	}
	
	
}
