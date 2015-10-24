package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 待开赛--消息内容 TODO
 * @author Sola
 *
 */
public class MatchPendingMC extends MessageContent{
	
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

	public static final Parcelable.Creator<MatchPendingMC> CREATOR = new Parcelable.Creator<MatchPendingMC>() {

		@Override
		public MatchPendingMC createFromParcel(Parcel parcel) {
			MatchPendingMC matchFinishedMC = new MatchPendingMC();
			matchFinishedMC.matchUuid = parcel.readString();
			matchFinishedMC.teamUuid = parcel.readString();
			matchFinishedMC.opponentTeamUuid = parcel.readString();
			matchFinishedMC.description = parcel.readString();
			return matchFinishedMC;
		}

		@Override
		public MatchPendingMC[] newArray(int size) {
			return new MatchPendingMC[size];
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
