package com.kinth.football.bean;

import java.util.List;

import com.kinth.football.dao.Player;

public class SearchPersonResponse {
	private List<Player> players;
	private Pageable pageable;
	
	public List<Player> getPlayers() {
		return players;
	}
	public void setPlayers(List<Player> players) {
		this.players = players;
	}
	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}

}
