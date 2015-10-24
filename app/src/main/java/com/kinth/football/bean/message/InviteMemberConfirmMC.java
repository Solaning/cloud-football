package com.kinth.football.bean.message;

import com.google.gson.annotations.Expose;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 邀请成员确认消息--消息内容
 * @author Sola
 *
 */
public class InviteMemberConfirmMC extends MessageContent{
	
	@Expose
	protected String uuid;
	
	@Expose
	protected String teamUuid;//加入的球队的uuid
	
	@Expose
	protected String playerUuid;//加入球队的球员uuid
	
	@Expose
	protected String confirm;//确认: "ACCESS" 或 "REFUSE"

	public static final Parcelable.Creator<InviteMemberConfirmMC> CREATOR = new Parcelable.Creator<InviteMemberConfirmMC>() {

		@Override
		public InviteMemberConfirmMC createFromParcel(Parcel parcel) {
			InviteMemberConfirmMC inviteMemberConfirmMC = new InviteMemberConfirmMC();
			inviteMemberConfirmMC.uuid = parcel.readString();
			inviteMemberConfirmMC.teamUuid = parcel.readString();
			inviteMemberConfirmMC.playerUuid = parcel.readString();
			inviteMemberConfirmMC.confirm = parcel.readString();
			inviteMemberConfirmMC.description = parcel.readString();
			return inviteMemberConfirmMC;
		}

		@Override
		public InviteMemberConfirmMC[] newArray(int size) {
			return new InviteMemberConfirmMC[size];
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
		dest.writeString(this.confirm);
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

	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

}
