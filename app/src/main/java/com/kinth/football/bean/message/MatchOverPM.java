package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 待评价--消息体
 * @author Sola
 *
 */

public class MatchOverPM extends PushMessageAbstract<MatchOverMC>{
	
	public static final Parcelable.Creator<MatchOverPM> CREATOR = new Parcelable.Creator<MatchOverPM>() {

		@Override
		public MatchOverPM createFromParcel(Parcel parcel) {
			MatchOverPM matchFinishedPM = new MatchOverPM();
			matchFinishedPM.type = parcel.readString();
			matchFinishedPM.date = parcel.readLong();
			matchFinishedPM.content = parcel.readParcelable(MatchOverMC.class.getClassLoader());
			return matchFinishedPM;
		}

		@Override
		public MatchOverPM[] newArray(int size) {
			return new MatchOverPM[size];
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
