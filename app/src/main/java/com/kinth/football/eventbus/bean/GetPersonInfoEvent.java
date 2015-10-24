package com.kinth.football.eventbus.bean;

import com.kinth.football.dao.Player;

/**
 * 取到当前用户资料的事件
 * @author Sola
 *
 */
public class GetPersonInfoEvent {
	private Player player;

	public GetPersonInfoEvent(Player player) {
		super();
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
}
