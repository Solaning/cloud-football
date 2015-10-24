package com.kinth.football.bean.match;

import java.util.List;

/**
 * 球员近5场比赛统计数据返回实体
 * @author Botision.Huang
 * Date: 2015-4-4
 * Descp:
 */
public class FiveMatchStatisticsResponse {

	private FiveDifferentData leagueTotal;   //综合数据（当前总分数）
	
	private List<FiveDifferentData> leagues; //最近五场比赛的五项数据List（进攻,防守，体能，技术，侵略性）（第一个折线图所用）
	
	private FiveDifferentData leagueDelta;   //五项数据的变化值
	
	private SkillAndMormality matchTotal;  //球品口碑与球技互评（当前总分数）
	
	private List<SkillAndMormality> matches; //球品球技两项数据的List（第二个折线图所用）
	
	private SkillAndMormality matchDelta;  //球品球技的变化值

	public FiveDifferentData getLeagueTotal() {
		return leagueTotal;
	}

	public void setLeagueTotal(FiveDifferentData leagueTotal) {
		this.leagueTotal = leagueTotal;
	}

	public List<FiveDifferentData> getLeagues() {
		return leagues;
	}

	public void setLeagues(List<FiveDifferentData> leagues) {
		this.leagues = leagues;
	}


	public FiveDifferentData getLeagueDelta() {
		return leagueDelta;
	}

	public void setLeagueDelta(FiveDifferentData leagueDelta) {
		this.leagueDelta = leagueDelta;
	}

	public SkillAndMormality getMatchTotal() {
		return matchTotal;
	}

	public void setMatchTotal(SkillAndMormality matchTotal) {
		this.matchTotal = matchTotal;
	}

	public List<SkillAndMormality> getMatches() {
		return matches;
	}

	public void setMatches(List<SkillAndMormality> matches) {
		this.matches = matches;
	}

	public SkillAndMormality getMatchDelta() {
		return matchDelta;
	}

	public void setMatchDelta(SkillAndMormality matchDelta) {
		this.matchDelta = matchDelta;
	}

}
