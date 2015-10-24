package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 申请加入球队--消息内容
 * @author Sola
 * 
 */
public class RequestJoinTeamMC extends MessageContent{
	private String uuid;
	private String teamUuid;
	private String playerUuid;
	
	public static final Parcelable.Creator<RequestJoinTeamMC> CREATOR = new Parcelable.Creator<RequestJoinTeamMC>() {

		@Override
		public RequestJoinTeamMC createFromParcel(Parcel parcel) {
			RequestJoinTeamMC requestJoinTeamMC = new RequestJoinTeamMC();
			requestJoinTeamMC.uuid = parcel.readString();
			requestJoinTeamMC.teamUuid = parcel.readString();
			requestJoinTeamMC.playerUuid = parcel.readString();
			requestJoinTeamMC.description = parcel.readString();
			return requestJoinTeamMC;
		}

		@Override
		public RequestJoinTeamMC[] newArray(int size) {
			return new RequestJoinTeamMC[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.uuid);
		dest.writeString(this.teamUuid);
		dest.writeString(this.playerUuid);
		dest.writeString(this.description);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getTeamUuid() {
		return teamUuid;
	}

	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}

	public String getPlayerUuid() {
		return playerUuid;
	}

	public void setPlayerUuid(String playerUuid) {
		this.playerUuid = playerUuid;
	}
	
}
