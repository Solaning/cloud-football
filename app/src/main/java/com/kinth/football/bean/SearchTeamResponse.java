package com.kinth.football.bean;

import java.util.List;

import com.kinth.football.dao.Team;

public class SearchTeamResponse {

	private List<Team> teams;
	private Pageable pageable;

	public List<Team> getTeams() {
		return teams;
	}

	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}

	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}
}
