package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 待开赛--消息体
 * @author Sola
 *
 */

public class MatchPendingPM extends PushMessageAbstract<MatchPendingMC>{
	
	public static final Parcelable.Creator<MatchPendingPM> CREATOR = new Parcelable.Creator<MatchPendingPM>() {

		@Override
		public MatchPendingPM createFromParcel(Parcel parcel) {
			MatchPendingPM matchFinishedPM = new MatchPendingPM();
			matchFinishedPM.type = parcel.readString();
			matchFinishedPM.date = parcel.readLong();
			matchFinishedPM.content = parcel.readParcelable(MatchPendingMC.class.getClassLoader());
			return matchFinishedPM;
		}

		@Override
		public MatchPendingPM[] newArray(int size) {
			return new MatchPendingPM[size];
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
