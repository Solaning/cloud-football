package com.kinth.football.config;

/**
 * 比赛进行的状态枚举
 * @author Sola
 */
public enum MatchStateEnum {
	NULL("NULL"),
	INVITING("INVITING"),//邀请中
	CREATED("CREATED"),//报名中
	PENDING("PENDING"),//待开赛
	PLAYING("PLAYING"),//比赛中
	OVER("OVER"),//待评价
	FINISHED("FINISHED"),//已完成
	CANCELED("CANCELED"),//已取消
	CALL_FOR("CALL_FOR"),
	CHALLENGE("CHALLENGE"),
	CHALLENGE_INVITING("CHALLENGE_INVITING");//组合状态

	private String value;

	MatchStateEnum(String value) {
		this.value = value;
	}
	
	public static MatchStateEnum getEnumFromString(String string) {
		if (string != null) {
			for (MatchStateEnum s : MatchStateEnum.values()){
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
