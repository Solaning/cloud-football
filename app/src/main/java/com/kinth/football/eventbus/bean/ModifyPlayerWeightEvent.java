package com.kinth.football.eventbus.bean;

/**
 * 修改球员体重事件
 * @author Sola
 *
 */
public class ModifyPlayerWeightEvent {
	private int newWeight;

	public int getNewWeight() {
		return newWeight;
	}

	public void setNewWeight(int newWeight) {
		this.newWeight = newWeight;
	}

	public ModifyPlayerWeightEvent(int newWeight) {
		super();
		this.newWeight = newWeight;
	}

}
