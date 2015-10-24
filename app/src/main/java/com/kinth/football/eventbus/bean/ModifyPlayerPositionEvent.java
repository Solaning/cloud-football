package com.kinth.football.eventbus.bean;

/**
 * 修改用户位置事件
 * 
 * @author Sola
 *
 */
public class ModifyPlayerPositionEvent {
	private String newPosition;

	public ModifyPlayerPositionEvent(String newPosition) {
		super();
		this.newPosition = newPosition;
	}

	public String getNewPosition() {
		return newPosition;
	}

	public void setNewPosition(String newPosition) {
		this.newPosition = newPosition;
	}

}
