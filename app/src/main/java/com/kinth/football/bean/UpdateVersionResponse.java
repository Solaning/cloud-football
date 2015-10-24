package com.kinth.football.bean;

public class UpdateVersionResponse {
	
	private boolean lasted;
	private Update update;
	
	public boolean isLasted() {
		return lasted;
	}
	public void setLasted(boolean lasted) {
		this.lasted = lasted;
	}
	public Update getUpdate() {
		return update;
	}
	public void setUpdate(Update update) {
		this.update = update;
	}
	
}
