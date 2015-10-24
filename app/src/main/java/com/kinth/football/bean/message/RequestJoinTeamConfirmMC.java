package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 申请加入球队确认--消息内容
 * @author Sola
 * 
 */
public class RequestJoinTeamConfirmMC extends MessageContent{

	private String uuid;//消息id
	private String teamUuid;//要加入的球队id
	private String playerUuid;//申请的用户id
	
	public static final Parcelable.Creator<RequestJoinTeamConfirmMC> CREATOR = new Parcelable.Creator<RequestJoinTeamConfirmMC>() {

		@Override
		public RequestJoinTeamConfirmMC createFromParcel(Parcel parcel) {
			RequestJoinTeamConfirmMC requestJoinTeamConfirmMC = new RequestJoinTeamConfirmMC();
			requestJoinTeamConfirmMC.uuid = parcel.readString();
			requestJoinTeamConfirmMC.teamUuid = parcel.readString();
			requestJoinTeamConfirmMC.playerUuid = parcel.readString();
			requestJoinTeamConfirmMC.description = parcel.readString();
			return requestJoinTeamConfirmMC;
		}

		@Override
		public RequestJoinTeamConfirmMC[] newArray(int size) {
			return new RequestJoinTeamConfirmMC[size];
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
