package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *  比赛裁判邀请消息----消息内容
 * @author Botision.Huang
 * Date: 2015-3-31
 * Descp:
 */
public class MatchRefereeInvitationMC extends MessageContent {

	private String matchUuid;
	private String homeTeamUuid;
	private String awayTeamUuid;
	
	public static final Parcelable.Creator<MatchRefereeInvitationMC> CREATOR = new Parcelable.Creator<MatchRefereeInvitationMC>() {

		@Override
		public MatchRefereeInvitationMC createFromParcel(Parcel parcel) {
			MatchRefereeInvitationMC matchRefereeInvitationMC = new MatchRefereeInvitationMC();
			matchRefereeInvitationMC.matchUuid = parcel.readString();
			matchRefereeInvitationMC.homeTeamUuid = parcel.readString();
			matchRefereeInvitationMC.awayTeamUuid = parcel.readString();
			matchRefereeInvitationMC.description = parcel.readString();
			return matchRefereeInvitationMC;
		}

		@Override
		public MatchRefereeInvitationMC[] newArray(int size) {
			return new MatchRefereeInvitationMC[size];
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(this.matchUuid);
		dest.writeString(this.homeTeamUuid);
		dest.writeString(this.awayTeamUuid);
		dest.writeString(this.description);
	}

	public String getMatchUuid() {
		return matchUuid;
	}

	public void setMatchUuid(String matchUuid) {
		this.matchUuid = matchUuid;
	}

	public String getHomeTeamUuid() {
		return homeTeamUuid;
	}

	public void setHomeTeamUuid(String homeTeamUuid) {
		this.homeTeamUuid = homeTeamUuid;
	}

	public String getAwayTeamUuid() {
		return awayTeamUuid;
	}

	public void setAwayTeamUuid(String awayTeamUuid) {
		this.awayTeamUuid = awayTeamUuid;
	}
	
}
