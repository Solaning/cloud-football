package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛邀请--消息体
 * @author Sola
 *
 */

public class MatchInvitationPM extends PushMessageAbstract<MatchInvitationMC>{
	
	public static final Parcelable.Creator<MatchInvitationPM> CREATOR = new Parcelable.Creator<MatchInvitationPM>() {

		@Override
		public MatchInvitationPM createFromParcel(Parcel parcel) {
			MatchInvitationPM matchInvitationPM = new MatchInvitationPM();
			matchInvitationPM.type = parcel.readString();
			matchInvitationPM.date = parcel.readLong();
			matchInvitationPM.content = parcel.readParcelable(MatchInvitationMC.class.getClassLoader());
			return matchInvitationPM;
		}

		@Override
		public MatchInvitationPM[] newArray(int size) {
			return new MatchInvitationPM[size];
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
