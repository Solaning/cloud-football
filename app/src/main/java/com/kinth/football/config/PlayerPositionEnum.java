package com.kinth.football.config;

/**
 * 球员位置
 * @author Sola
 *
 */
public enum PlayerPositionEnum {
	NULL("NULL",""),
	GK("GK","门将"),//门将:GK, 
	CB("CB","中后卫"),//中后卫:CB, 
	LCB("LCB","左后卫"),//左后卫:LCB,
	RCB("RCB","右后卫"),//:RCB, 
	LWB("LWB","左边后卫"),//:LWB, 
	RWB("RWB","右边后卫"),//:RWB, 
	CDM("CDM","后腰"),//:CDM, 
	LCM("LCM","左前卫"),//:LCM, 
	RCM("RCM","右前卫"),//:RCM, 
	CM("CM","前卫"),//:CM, 
	LWM("LWM","左边前卫"),//:LWM, 
	RWM("RWM","右边前卫"),//:RWM, 
	CAM("CAM","前腰"),//:CAM, 
	LF("LF","左前锋"),//:LF, 
	RF("RF","右前锋"),//:RF, 
	CF("CF","前锋"),//:CF, 
	ST("ST","中锋"),//:ST, 
	SS("SS","影子前锋"),//:SS,
	LW("LW","左边锋"),//:LW, 
	RW("RW","右边锋");//:RW

	private String value;
	private String name;

	PlayerPositionEnum(String value, String name) {
		this.value = value;
		this.name = name;
	}
	
	public static PlayerPositionEnum getEnumFromString(String string) {
		if (string != null) {
			for (PlayerPositionEnum s : PlayerPositionEnum.values()){
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
