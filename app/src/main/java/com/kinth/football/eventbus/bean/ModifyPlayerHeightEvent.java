package com.kinth.football.eventbus.bean;

/**
 * 修改球员身高事件
 * @author Sola
 *
 */
public class ModifyPlayerHeightEvent {
	private int newHeight;

	public int getNewHeight() {
		return newHeight;
	}

	public void setNewHeight(int newHeight) {
		this.newHeight = newHeight;
	}

	public ModifyPlayerHeightEvent(int newHeight) {
		super();
		this.newHeight = newHeight;
	}
	
}
