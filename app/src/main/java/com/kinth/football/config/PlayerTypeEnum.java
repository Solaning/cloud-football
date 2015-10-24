package com.kinth.football.config;

/**
 * 邀请成员的类型
 * @author Sola
 *
 */
public enum PlayerTypeEnum {
//	"GENERAL"--普通成员  "FIRST_CAPTAIN"--第一队长   "SECOND_CAPTAIN"--第二队长  "THIRD_CAPTAIN"--第三队长

	NULL("null","NULL"),
	GENERAL("GENERAL","普通成员"),//GENERAL
	FIRST_CAPTAIN("FIRST_CAPTAIN","队长"),
	SECOND_CAPTAIN("SECOND_CAPTAIN","队副"),
	THIRD_CAPTAIN("THIRD_CAPTAIN","教练");

	private String value;
	private String name;

	PlayerTypeEnum(String value, String name) {
		this.value = value;
		this.name = name;
	}
	
	public static PlayerTypeEnum getEnumFromString(String string) {
		if (string != null) {
			for (PlayerTypeEnum s : PlayerTypeEnum.values()){
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

	public String getName(){
		return name;
	}
}
