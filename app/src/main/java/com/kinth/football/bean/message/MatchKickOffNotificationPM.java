package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛创建--消息体 pushMessage   messageContent
 * @author Sola
 *
 */

public class MatchKickOffNotificationPM extends PushMessageAbstract<MatchCreatedMC>{
	
	public static final Parcelable.Creator<MatchKickOffNotificationPM> CREATOR = new Parcelable.Creator<MatchKickOffNotificationPM>() {

		@Override
		public MatchKickOffNotificationPM createFromParcel(Parcel parcel) {
			MatchKickOffNotificationPM matchCreatedPM = new MatchKickOffNotificationPM();
			matchCreatedPM.type = parcel.readString();
			matchCreatedPM.date = parcel.readLong();
			matchCreatedPM.content = parcel.readParcelable(MatchCreatedMC.class.getClassLoader());
			return matchCreatedPM;
		}

		@Override
		public MatchKickOffNotificationPM[] newArray(int size) {
			return new MatchKickOffNotificationPM[size];
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
