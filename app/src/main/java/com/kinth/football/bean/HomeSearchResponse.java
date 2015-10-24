package com.kinth.football.bean;

import java.util.List;

import com.kinth.football.dao.Player;
import com.kinth.football.dao.Team;

public class HomeSearchResponse {

	private List<Player> players;
	private List<Team> teams;
	
	public List<Player> getPlayers() {
		return players;
	}
	
	public void setPlayers(List<Player> players) {
		this.players = players;
	}
	
	public List<Team> getTeams() {
		return teams;
	}
	
	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}
	
	
	
}
