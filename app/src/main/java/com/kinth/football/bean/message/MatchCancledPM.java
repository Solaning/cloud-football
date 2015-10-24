package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛取消--消息体
 * @author Sola
 *
 */

public class MatchCancledPM extends PushMessageAbstract<MatchCancledMC>{
	
	public static final Parcelable.Creator<MatchCancledPM> CREATOR = new Parcelable.Creator<MatchCancledPM>() {

		@Override
		public MatchCancledPM createFromParcel(Parcel parcel) {
			MatchCancledPM matchCancledPM = new MatchCancledPM();
			matchCancledPM.type = parcel.readString();
			matchCancledPM.date = parcel.readLong();
			matchCancledPM.content = parcel.readParcelable(MatchCancledMC.class.getClassLoader());
			return matchCancledPM;
		}

		@Override
		public MatchCancledPM[] newArray(int size) {
			return new MatchCancledPM[size];
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
