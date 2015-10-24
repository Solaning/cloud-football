package com.kinth.football.config;

/**
 * 球员位置
 * @author Sola
 *
 */
public enum PlayerPositionEnum2 {
	NULL("NULL","不限"),
	GK("GK","门将"),//门将:GK, 
	FW("FW","前场"),//门将:GK, 
	MF("MF","中场"),//门将:GK, 
	BF("BF","后场");//门将:GK, 

	private String value;
	private String name;

	PlayerPositionEnum2(String value, String name) {
		this.value = value;
		this.name = name;
	}
	
	public static PlayerPositionEnum2 getEnumFromString(String string) {
		if (string != null) {
			for (PlayerPositionEnum2 s : PlayerPositionEnum2.values()){
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

	public String getName() {
		return name;
	}
	
}
