package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛创建--消息体 pushMessage   messageContent
 * @author Sola
 *
 */

public class MatchChallengePM extends PushMessageAbstract<MatchCreatedMC>{
	
	public static final Parcelable.Creator<MatchChallengePM> CREATOR = new Parcelable.Creator<MatchChallengePM>() {

		@Override
		public MatchChallengePM createFromParcel(Parcel parcel) {
			MatchChallengePM matchCreatedPM = new MatchChallengePM();
			matchCreatedPM.type = parcel.readString();
			matchCreatedPM.date = parcel.readLong();
			matchCreatedPM.content = parcel.readParcelable(MatchCreatedMC.class.getClassLoader());
			return matchCreatedPM;
		}

		@Override
		public MatchChallengePM[] newArray(int size) {
			return new MatchChallengePM[size];
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.type);
		dest.writeLong(this.date);
		dest.writeParcelable(this.content, flags);
	}
	
}
