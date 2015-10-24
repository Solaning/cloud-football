package com.kinth.football.eventbus.bean;

/**
 * 修改用户昵称事件
 * @author Sola
 *
 */
public class ModifyPlayerNickNameEvent {
	private String newNick;

	public ModifyPlayerNickNameEvent(String newNick) {
		super();
		this.newNick = newNick;
	}

	public String getNewNick() {
		return newNick;
	}

	public void setNewNick(String newNick) {
		this.newNick = newNick;
	}
	
}
