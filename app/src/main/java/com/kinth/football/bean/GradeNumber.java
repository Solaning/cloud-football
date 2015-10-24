package com.kinth.football.bean;

/**
 * 评分的数值
 * 
 * @author Sola
 *
 */
public class GradeNumber {
	private String playerUuid;
	private int skill;
	private int morality;

	public String getPlayerUuid() {
		return playerUuid;
	}

	public void setPlayerUuid(String playerUuid) {
		this.playerUuid = playerUuid;
	}

	public int getSkill() {
		return skill;
	}

	public void setSkill(int skill) {
		this.skill = skill;
	}

	public int getMorality() {
		return morality;
	}

	public void setMorality(int morality) {
		this.morality = morality;
	}
}
