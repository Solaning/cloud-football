package com.kinth.football.config;

/**
 * 朋友圈消息类型
 * @author Sola
 *
 */
public enum MomentsTypeEnum {
	
	NULL("NULL"), TEXT("TEXT"), IMAGE("IMAGE"), VIDEO("VIDEO"), URL("URL");
	
	private String value;

	MomentsTypeEnum(String value) {
		this.value = value;
	}
	
	public static MomentsTypeEnum getEnumFromString(String string) {
		if (string != null) {
			for (MomentsTypeEnum s : MomentsTypeEnum.values()){
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
