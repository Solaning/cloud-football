package com.kinth.football.bean.message;

import com.google.gson.annotations.Expose;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛报名完成--消息内容
 * @author Sola
 *
 */
public class MatchSignupMC extends MessageContent{
	
	@Expose
	protected String matchUuid;
	
	@Expose
	protected String teamUuid;
	
	@Expose
	protected String playerUuid;
	
	@Expose
	protected String opponentTeamUuid;

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

	public String getPlayerUuid() {
		return playerUuid;
	}

	public void setPlayerUuid(String playerUuid) {
		this.playerUuid = playerUuid;
	}

	public String getOpponentTeamUuid() {
		return opponentTeamUuid;
	}

	public void setOpponentTeamUuid(String opponentTeamUuid) {
		this.opponentTeamUuid = opponentTeamUuid;
	}

	public static final Parcelable.Creator<MatchSignupMC> CREATOR = new Parcelable.Creator<MatchSignupMC>() {

		@Override
		public MatchSignupMC createFromParcel(Parcel parcel) {
			MatchSignupMC matchSignupMC = new MatchSignupMC();
			matchSignupMC.matchUuid = parcel.readString();
			matchSignupMC.teamUuid = parcel.readString();
			matchSignupMC.playerUuid = parcel.readString();
			matchSignupMC.opponentTeamUuid = parcel.readString();
			matchSignupMC.description = parcel.readString();
			return matchSignupMC;
		}

		@Override
		public MatchSignupMC[] newArray(int size) {
			return new MatchSignupMC[size];
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
		dest.writeString(this.teamUuid);
		dest.writeString(this.playerUuid);
		dest.writeString(this.opponentTeamUuid);
		dest.writeString(this.description);
	}

}
