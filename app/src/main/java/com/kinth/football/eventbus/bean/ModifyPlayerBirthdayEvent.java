package com.kinth.football.eventbus.bean;

/**
 * 修改球员生日事件
 * @author Sola
 *
 */
public class ModifyPlayerBirthdayEvent {
	private long newBirthday;

	public long getNewBirthday() {
		return newBirthday;
	}

	public void setNewBirthday(long newBirthday) {
		this.newBirthday = newBirthday;
	}

	public ModifyPlayerBirthdayEvent(long newBirthday) {
		super();
		this.newBirthday = newBirthday;
	}

}
