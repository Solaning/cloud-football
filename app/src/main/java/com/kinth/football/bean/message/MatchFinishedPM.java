package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛结束--消息体
 * @author Sola
 *
 */

public class MatchFinishedPM extends PushMessageAbstract<MatchFinishedMC>{
	
	public static final Parcelable.Creator<MatchFinishedPM> CREATOR = new Parcelable.Creator<MatchFinishedPM>() {

		@Override
		public MatchFinishedPM createFromParcel(Parcel parcel) {
			MatchFinishedPM matchFinishedPM = new MatchFinishedPM();
			matchFinishedPM.type = parcel.readString();
			matchFinishedPM.date = parcel.readLong();
			matchFinishedPM.content = parcel.readParcelable(MatchFinishedMC.class.getClassLoader());
			return matchFinishedPM;
		}

		@Override
		public MatchFinishedPM[] newArray(int size) {
			return new MatchFinishedPM[size];
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
