package com.kinth.football.config;

/**
 * 比赛类型
 * @author Sola
 *
 */
public enum MatchTypeEnum {

	NULL("NULL"),
	FRIENDLY_GAME("FRIENDLY_GAME"),//——友谊赛
	FIFA_SEASON("FIFA_SEASON"),//——FIFA Seasons
	LEAGUE("LEAGUE");//联赛

	private String value;

	MatchTypeEnum(String value) {
		this.value = value;
	}
	
	public static MatchTypeEnum getEnumFromString(String string) {
		if (string != null) {
			for (MatchTypeEnum s : MatchTypeEnum.values()){
				if (string.equals(s.value)) {
	                return s;
	            }
			}
		}
		return NULL;
	}

	public String getValue() {
		return value;
	}
}
