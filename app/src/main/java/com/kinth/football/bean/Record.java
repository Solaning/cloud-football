package com.kinth.football.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @author Botision.Huang
 * Date: 2015-4-4
 * Descp:
 */
public class Record implements Parcelable{

	private int matches;  //总比赛场次
	private int won;      //胜
	private int drawn;    //平
	private int lost;     //负
	private int scored;   //进球
	private int against;  //失球
	private int played;   //出场次数
	
	public static final Parcelable.Creator<Record> CREATOR = new Creator<Record>() {

		@Override
		public Record createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			Record record = new Record();
			record.matches = source.readInt();
			record.won = source.readInt();
			record.drawn = source.readInt();
			record.lost = source.readInt();
			record.scored = source.readInt();
			record.against = source.readInt();
			record.played = source.readInt();
			return record;
		}

		@Override
		public Record[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Record[size];
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		// TODO Auto-generated method stub
		dest.writeInt(this.matches);
		dest.writeInt(this.won);
		dest.writeInt(this.drawn);
		dest.writeInt(this.lost);
		dest.writeInt(this.scored);
		dest.writeInt(this.against);
		dest.writeInt(this.played);
	}

	public int getMatches() {
		return matches;
	}

	public void setMatches(int matches) {
		this.matches = matches;
	}

	public int getWon() {
		return won;
	}

	public void setWon(int won) {
		this.won = won;
	}

	public int getDrawn() {
		return drawn;
	}

	public void setDrawn(int drawn) {
		this.drawn = drawn;
	}

	public int getLost() {
		return lost;
	}

	public void setLost(int lost) {
		this.lost = lost;
	}

	public int getScored() {
		return scored;
	}

	public void setScored(int scored) {
		this.scored = scored;
	}

	public int getAgainst() {
		return against;
	}

	public void setAgainst(int against) {
		this.against = against;
	}

	public int getPlayed() {
		return played;
	}

	public void setPlayed(int played) {
		this.played = played;
	}

	
	
}
