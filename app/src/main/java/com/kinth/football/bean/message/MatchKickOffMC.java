package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛进行中--消息内容
 * @author Sola
 *
 */
public class MatchKickOffMC extends MessageContent{
	
	protected String matchUuid;
	
	protected String teamUuid;
	
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

	public String getOpponentTeamUuid() {
		return opponentTeamUuid;
	}

	public void setOpponentTeamUuid(String opponentTeamUuid) {
		this.opponentTeamUuid = opponentTeamUuid;
	}

	public static final Parcelable.Creator<MatchKickOffMC> CREATOR = new Parcelable.Creator<MatchKickOffMC>() {

		@Override
		public MatchKickOffMC createFromParcel(Parcel parcel) {
			MatchKickOffMC matchKickOffMC = new MatchKickOffMC();
			matchKickOffMC.matchUuid = parcel.readString();
			matchKickOffMC.teamUuid = parcel.readString();
			matchKickOffMC.opponentTeamUuid	= parcel.readString();
			matchKickOffMC.description = parcel.readString();
			return matchKickOffMC;
		}

		@Override
		public MatchKickOffMC[] newArray(int size) {
			return new MatchKickOffMC[size];
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
		dest.writeString(this.opponentTeamUuid);
		dest.writeString(this.description);
	}

}
