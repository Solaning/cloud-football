package com.kinth.football.bean.match;

import android.os.Parcelable;

/**
 * 比赛的抽象父类
 * 
 * @author Sola
 *
 */
public abstract class AbstractMatch implements Parcelable {
	protected String name;//比赛名字
	protected String field;//比赛场地
	protected long kickOff;//比赛时间
	protected int playerCount;//比赛人数
	protected String type;//比赛类型  "FRIENDLY_GAME"——友谊赛 "FIFA_SEASON"——FIFA Seasons "LEAGUE"—— 联赛
	protected String state;//比赛状态
	protected float cost;//费用
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public long getKickOff() {
		return kickOff;
	}
	
	public void setKickOff(long kickOff) {
		this.kickOff = kickOff;
	}
	
	public int getPlayerCount() {
		return playerCount;
	}

	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

}
