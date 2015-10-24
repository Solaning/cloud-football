package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛取消--消息内容
 * @author Sola
 *
 */
public class MatchCancledMC extends MessageContent{
	
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

	public static final Parcelable.Creator<MatchCancledMC> CREATOR = new Parcelable.Creator<MatchCancledMC>() {

		@Override
		public MatchCancledMC createFromParcel(Parcel parcel) {
			MatchCancledMC matchCancledMC = new MatchCancledMC();
			matchCancledMC.matchUuid = parcel.readString();
			matchCancledMC.teamUuid = parcel.readString();
			matchCancledMC.opponentTeamUuid = parcel.readString();
			matchCancledMC.description = parcel.readString();
			return matchCancledMC;
		}

		@Override
		public MatchCancledMC[] newArray(int size) {
			return new MatchCancledMC[size];
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
