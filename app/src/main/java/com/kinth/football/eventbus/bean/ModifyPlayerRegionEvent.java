package com.kinth.football.eventbus.bean;

/**
 * 修改用户地区事件
 * @author Sola
 *
 */
public class ModifyPlayerRegionEvent {
	private String newProvinceName;
	private String newCityName;

	public ModifyPlayerRegionEvent(String newProvinceName, String newCityName) {
		super();
		this.newProvinceName = newProvinceName;
		this.newCityName = newCityName;
	}

	public String getNewProvinceName() {
		return newProvinceName;
	}

	public void setNewProvinceName(String newProvinceName) {
		this.newProvinceName = newProvinceName;
	}

	public String getNewCityName() {
		return newCityName;
	}

	public void setNewCityName(String newCityName) {
		this.newCityName = newCityName;
	}

}
