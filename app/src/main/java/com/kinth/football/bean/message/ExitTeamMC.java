package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 退出球队--消息内容
 * @author Sola
 *
 */
public class ExitTeamMC extends MessageContent{

	private String uuid;//消息id
	private String teamUuid;
	private String playerUuid;
	
	public static final Parcelable.Creator<ExitTeamMC> CREATOR = new Parcelable.Creator<ExitTeamMC>() {

		@Override
		public ExitTeamMC createFromParcel(Parcel parcel) {
			ExitTeamMC requestJoinTeamConfirmMC = new ExitTeamMC();
			requestJoinTeamConfirmMC.uuid = parcel.readString();
			requestJoinTeamConfirmMC.teamUuid = parcel.readString();
			requestJoinTeamConfirmMC.playerUuid = parcel.readString();
			requestJoinTeamConfirmMC.description = parcel.readString();
			return requestJoinTeamConfirmMC;
		}

		@Override
		public ExitTeamMC[] newArray(int size) {
			return new ExitTeamMC[size];
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
