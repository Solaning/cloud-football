package com.kinth.football.config;

/**
 * 比赛结果的枚举
 * @author Sola
 *
 */
public enum MatchResultEnum {
	NULL("NULL"),
	WIN("WIN"),
	DRAW("DRAW"),//已取消
	LOSS("LOSS");

	private String value;

	MatchResultEnum(String value) {
		this.value = value;
	}
	
	public static MatchResultEnum getEnumFromString(String string) {
		if (string != null) {
			for (MatchResultEnum s : MatchResultEnum.values()){
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
