package com.kinth.football.bean.message;

import com.google.gson.annotations.Expose;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛邀请确认--消息内容
 * @author Sola
 *
 */
public class MatchInvitationConfirmMC extends MessageContent{
	
	@Expose
	protected String matchUuid;
	
	@Expose
	protected String opponentTeamUuid;
	
	@Expose
	protected String teamUuid;
	
	@Expose
	protected boolean confirm;
	 
	public String getMatchUuid() {
		return matchUuid;
	}

	public void setMatchUuid(String matchUuid) {
		this.matchUuid = matchUuid;
	}

	public boolean isConfirm() {
		return confirm;
	}

	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}

	public String getOpponentTeamUuid() {
		return opponentTeamUuid;
	}

	public void setOpponentTeamUuid(String opponentTeamUuid) {
		this.opponentTeamUuid = opponentTeamUuid;
	}

	public String getTeamUuid() {
		return teamUuid;
	}

	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}

	public static final Parcelable.Creator<MatchInvitationConfirmMC> CREATOR = new Parcelable.Creator<MatchInvitationConfirmMC>() {

		@Override
		public MatchInvitationConfirmMC createFromParcel(Parcel parcel) {
			MatchInvitationConfirmMC matchInvitationMC = new MatchInvitationConfirmMC();
			matchInvitationMC.matchUuid = parcel.readString();
			matchInvitationMC.opponentTeamUuid = parcel.readString();
			matchInvitationMC.teamUuid = parcel.readString();
			matchInvitationMC.confirm = parcel.readByte() != 0;
			matchInvitationMC.description = parcel.readString();
			return matchInvitationMC;
		}

		@Override
		public MatchInvitationConfirmMC[] newArray(int size) {
			return new MatchInvitationConfirmMC[size];
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.matchUuid);
		dest.writeString(this.opponentTeamUuid);
		dest.writeString(this.teamUuid);
		dest.writeByte((byte)(confirm ? 1:0));
		dest.writeString(this.description);
	}

}
