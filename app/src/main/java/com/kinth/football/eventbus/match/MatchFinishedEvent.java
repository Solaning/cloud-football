package com.kinth.football.eventbus.match;

import com.kinth.football.bean.message.MatchFinishedPM;

/**
 * 比赛结束的事件
 * @author Sola
 *
 */
public class MatchFinishedEvent {
	private MatchFinishedPM matchFinishedPM;

	public MatchFinishedEvent(MatchFinishedPM matchFinishedPM) {
		super();
		this.matchFinishedPM = matchFinishedPM;
	}

	public MatchFinishedPM getMatchFinishedPM() {
		return matchFinishedPM;
	}

	public void setMatchFinishedPM(MatchFinishedPM matchFinishedPM) {
		this.matchFinishedPM = matchFinishedPM;
	}
	
	
}
