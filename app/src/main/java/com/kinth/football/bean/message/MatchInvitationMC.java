package com.kinth.football.bean.message;

import com.google.gson.annotations.Expose;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛邀请--消息内容
 * @author Sola
 *
 */
public class MatchInvitationMC extends MessageContent{
	@Expose
	protected String invitationUuid;//邀请消息的uuid
	
	@Expose
	protected String matchUuid;
	
	@Expose
	protected String teamUuid;
	
	@Expose
	protected String opponentTeamUuid;

	public String getOpponentTeamUuid() {
		return opponentTeamUuid;
	}

	public void setOpponentTeamUuid(String opponentTeamUuid) {
		this.opponentTeamUuid = opponentTeamUuid;
	}

	public String getMatchUuid() {
		return matchUuid;
	}

	public void setMatchUuid(String matchUuid) {
		this.matchUuid = matchUuid;
	}

	public String getTeamUuid() {
		return teamUuid;
	}

	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}

	public String getInvitationUuid() {
		return invitationUuid;
	}

	public void setInvitationUuid(String invitationUuid) {
		this.invitationUuid = invitationUuid;
	}

	public static final Parcelable.Creator<MatchInvitationMC> CREATOR = new Parcelable.Creator<MatchInvitationMC>() {

		@Override
		public MatchInvitationMC createFromParcel(Parcel parcel) {
			MatchInvitationMC matchInvitationMC = new MatchInvitationMC();
			matchInvitationMC.invitationUuid = parcel.readString();
			matchInvitationMC.matchUuid = parcel.readString();
			matchInvitationMC.teamUuid = parcel.readString();
			matchInvitationMC.opponentTeamUuid = parcel.readString();
			matchInvitationMC.description = parcel.readString();
			return matchInvitationMC;
		}

		@Override
		public MatchInvitationMC[] newArray(int size) {
			return new MatchInvitationMC[size];
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.invitationUuid);
		dest.writeString(this.matchUuid);
		dest.writeString(this.teamUuid);
		dest.writeString(this.opponentTeamUuid);
		dest.writeString(this.description);
	}

}
