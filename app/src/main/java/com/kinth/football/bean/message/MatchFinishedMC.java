package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛结束--消息内容
 * @author Sola
 *
 */
public class MatchFinishedMC extends MessageContent{
	
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

	public static final Parcelable.Creator<MatchFinishedMC> CREATOR = new Parcelable.Creator<MatchFinishedMC>() {

		@Override
		public MatchFinishedMC createFromParcel(Parcel parcel) {
			MatchFinishedMC matchFinishedMC = new MatchFinishedMC();
			matchFinishedMC.matchUuid = parcel.readString();
			matchFinishedMC.teamUuid = parcel.readString();
			matchFinishedMC.opponentTeamUuid = parcel.readString();
			matchFinishedMC.description = parcel.readString();
			return matchFinishedMC;
		}

		@Override
		public MatchFinishedMC[] newArray(int size) {
			return new MatchFinishedMC[size];
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
