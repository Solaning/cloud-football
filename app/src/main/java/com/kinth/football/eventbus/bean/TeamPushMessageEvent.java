package com.kinth.football.eventbus.bean;

/**
 * 球队消息的提示事件
 * @author Sola
 *
 */
public class TeamPushMessageEvent {
	private long teamTipsNum;

	public TeamPushMessageEvent(long teamTipsNum) {
		super();
		this.teamTipsNum = teamTipsNum;
	}

	public long getTeamTipsNum() {
		return teamTipsNum;
	}

	public void setTeamTipsNum(long teamTipsNum) {
		this.teamTipsNum = teamTipsNum;
	}
	
}
