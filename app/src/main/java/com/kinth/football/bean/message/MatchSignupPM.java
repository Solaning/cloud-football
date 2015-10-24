package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛报名完成--消息体
 * @author Sola
 *
 */
public class MatchSignupPM extends PushMessageAbstract<MatchSignupMC>{
	
	public static final Parcelable.Creator<MatchSignupPM> CREATOR = new Parcelable.Creator<MatchSignupPM>() {

		@Override
		public MatchSignupPM createFromParcel(Parcel parcel) {
			MatchSignupPM matchSignupPM = new MatchSignupPM();
			matchSignupPM.type = parcel.readString();
			matchSignupPM.date = parcel.readLong();
			matchSignupPM.content = parcel.readParcelable(MatchSignupMC.class.getClassLoader());
			return matchSignupPM;
		}

		@Override
		public MatchSignupPM[] newArray(int size) {
			return new MatchSignupPM[size];
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
