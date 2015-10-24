package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛进行中--消息体
 * @author Sola
 *
 */

public class MatchKickOffPM extends PushMessageAbstract<MatchKickOffMC>{
	
	public static final Parcelable.Creator<MatchKickOffPM> CREATOR = new Parcelable.Creator<MatchKickOffPM>() {

		@Override
		public MatchKickOffPM createFromParcel(Parcel parcel) {
			MatchKickOffPM matchKickOffMC = new MatchKickOffPM();
			matchKickOffMC.type = parcel.readString();
			matchKickOffMC.date = parcel.readLong();
			matchKickOffMC.content = parcel.readParcelable(MatchKickOffMC.class.getClassLoader());
			return matchKickOffMC;
		}

		@Override
		public MatchKickOffPM[] newArray(int size) {
			return new MatchKickOffPM[size];
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
