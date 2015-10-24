package com.kinth.football.bean.match;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 友谊赛的请求实体
 * @author Sola
 *
 */
public class FriendMatch extends AbstractMatch{
	private String refereeUuid;//裁判uuid
	private String homeTeamUuid;//主队uuid
	private String awayTeamUuid;//客队uuid
	private int regionId;//TODO 区县id

	public static final Parcelable.Creator<FriendMatch> CREATOR = new Parcelable.Creator<FriendMatch>() {

		@Override
		public FriendMatch createFromParcel(Parcel parcel) {
			FriendMatch friendMatch = new FriendMatch();
			friendMatch.name = parcel.readString();
			friendMatch.field = parcel.readString();
			friendMatch.kickOff = parcel.readLong();
			friendMatch.playerCount = parcel.readInt();
			friendMatch.type = parcel.readString();
			friendMatch.homeTeamUuid = parcel.readString();
			friendMatch.awayTeamUuid = parcel.readString();
			friendMatch.state = parcel.readString();
			friendMatch.refereeUuid = parcel.readString();
			friendMatch.cost = parcel.readFloat();
			friendMatch.regionId = parcel.readInt();//TODO
			return friendMatch;
		}

		@Override
		public FriendMatch[] newArray(int size) {
			return new FriendMatch[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeString(this.field);
		dest.writeLong(this.kickOff);
		dest.writeInt(this.playerCount);
		dest.writeString(this.type);
		dest.writeString(this.homeTeamUuid);
		dest.writeString(this.awayTeamUuid);
		dest.writeString(this.state);
		dest.writeString(this.refereeUuid);
		dest.writeFloat(this.cost);
		dest.writeInt(this.regionId);//TODO
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}

	public String getHomeTeamUuid() {
		return homeTeamUuid;
	}

	public void setHomeTeamUuid(String homeTeamUuid) {
		this.homeTeamUuid = homeTeamUuid;
	}

	public String getAwayTeamUuid() {
		return awayTeamUuid;
	}

	public void setAwayTeamUuid(String awayTeamUuid) {
		this.awayTeamUuid = awayTeamUuid;
	}

	public String getRefereeUuid() {
		return refereeUuid;
	}

	public void setRefereeUuid(String refereeUuid) {
		this.refereeUuid = refereeUuid;
	}

	public int getRegionId() {//TODO
		return regionId;
	}

	public void setRegionId(int regionId) {//TODO
		this.regionId = regionId;
	}
	
}
