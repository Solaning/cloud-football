package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 待评价--消息内容 TODO
 * @author Sola
 *
 */
public class MatchOverMC extends MessageContent{
	
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

	public static final Parcelable.Creator<MatchOverMC> CREATOR = new Parcelable.Creator<MatchOverMC>() {

		@Override
		public MatchOverMC createFromParcel(Parcel parcel) {
			MatchOverMC matchFinishedMC = new MatchOverMC();
			matchFinishedMC.matchUuid = parcel.readString();
			matchFinishedMC.teamUuid = parcel.readString();
			matchFinishedMC.opponentTeamUuid = parcel.readString();
			matchFinishedMC.description = parcel.readString();
			return matchFinishedMC;
		}

		@Override
		public MatchOverMC[] newArray(int size) {
			return new MatchOverMC[size];
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
