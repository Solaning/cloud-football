package com.kinth.football.ui.cloud5ranking;

public enum Cloud5RankingTypeEnum {
	
	NULL("NULL"),
	COMPOSITE("COMPOSITE"),
	STRENGTH("STRENGTH"),
	SKILL("SKILL"),
	ATTACK("ATTACK"),
	DEFENCE("DEFENCE"),
	AWARENESS("AWARENESS");
	
	private String value;
	
	Cloud5RankingTypeEnum(String value) {
		this.value = value;
	}
	
	public static Cloud5RankingTypeEnum getEnumFromString(String string) {
		if (string != null) {
			for (Cloud5RankingTypeEnum s : Cloud5RankingTypeEnum.values()){
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
