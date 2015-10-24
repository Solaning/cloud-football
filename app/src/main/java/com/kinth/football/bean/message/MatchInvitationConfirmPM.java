package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛邀请确认--消息体
 * @author Sola
 *
 */
public class MatchInvitationConfirmPM extends PushMessageAbstract<MatchInvitationConfirmMC>{
	
	public static final Parcelable.Creator<MatchInvitationConfirmPM> CREATOR = new Parcelable.Creator<MatchInvitationConfirmPM>() {

		@Override
		public MatchInvitationConfirmPM createFromParcel(Parcel parcel) {
			MatchInvitationConfirmPM matchInvitationPM = new MatchInvitationConfirmPM();
			matchInvitationPM.type = parcel.readString();
			matchInvitationPM.date = parcel.readLong();
			matchInvitationPM.content = parcel.readParcelable(MatchInvitationConfirmMC.class.getClassLoader());
			return matchInvitationPM;
		}

		@Override
		public MatchInvitationConfirmPM[] newArray(int size) {
			return new MatchInvitationConfirmPM[size];
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
