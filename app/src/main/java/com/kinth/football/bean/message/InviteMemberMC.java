package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * 邀请成员--消息内容
 * @author Sola
 *
 */
public class InviteMemberMC extends MessageContent{
	
	protected String uuid;
	
	@Expose
	protected String teamUuid;//邀请加入的球队的uuid
	
	protected String invitorUuid;//邀请者的uuid
	
	protected String playerUuid;
	
	@Expose
	protected String playerType;//邀请担当的角色: 'GENERAL','FIRST_CAPTAIN','SECOND_CAPTAIN','THIRD_CAPTAIN'

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

	public String getInvitorUuid() {
		return invitorUuid;
	}

	public void setInvitorUuid(String invitorUuid) {
		this.invitorUuid = invitorUuid;
	}

	public String getPlayerType() {
		return playerType;
	}

	public void setPlayerType(String playerType) {
		this.playerType = playerType;
	}
	
	public String getPlayerUuid() {
		return playerUuid;
	}

	public void setPlayerUuid(String playerUuid) {
		this.playerUuid = playerUuid;
	}

	public static final Parcelable.Creator<InviteMemberMC> CREATOR = new Parcelable.Creator<InviteMemberMC>() {

		@Override
		public InviteMemberMC createFromParcel(Parcel parcel) {
			InviteMemberMC inviteMemberMC = new InviteMemberMC();
			inviteMemberMC.uuid = parcel.readString();
			inviteMemberMC.teamUuid = parcel.readString();
			inviteMemberMC.invitorUuid = parcel.readString();
			inviteMemberMC.playerUuid = parcel.readString();
			inviteMemberMC.playerType = parcel.readString();
			inviteMemberMC.description = parcel.readString();
			return inviteMemberMC;
		}

		@Override
		public InviteMemberMC[] newArray(int size) {
			return new InviteMemberMC[size];
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
		dest.writeString(this.invitorUuid);
		dest.writeString(this.playerUuid);
		dest.writeString(this.playerType);
		dest.writeString(this.description);
	}

}
