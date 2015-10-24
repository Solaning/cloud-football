package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛创建--消息内容
 * @author Sola
 *
 */
public class MatchCreatedMC extends MessageContent{
	
	protected String matchUuid;
	protected String teamUuid;//本队的uuid
	protected String opponentTeamUuid;//对手的uuid

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

	public static final Parcelable.Creator<MatchCreatedMC> CREATOR = new Parcelable.Creator<MatchCreatedMC>() {

		@Override
		public MatchCreatedMC createFromParcel(Parcel parcel) {
			MatchCreatedMC matchCreatedMC = new MatchCreatedMC();
			matchCreatedMC.matchUuid = parcel.readString();
			matchCreatedMC.teamUuid = parcel.readString();
			matchCreatedMC.opponentTeamUuid = parcel.readString();
			matchCreatedMC.description = parcel.readString();
			return matchCreatedMC;
		}

		@Override
		public MatchCreatedMC[] newArray(int size) {
			return new MatchCreatedMC[size];
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
