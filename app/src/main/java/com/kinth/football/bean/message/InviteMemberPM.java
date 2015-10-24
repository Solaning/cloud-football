package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 邀请球员的推送消息--消息体
 * @author Sola
 * 
 */
public class InviteMemberPM extends PushMessageAbstract<InviteMemberMC>{
	
	public static final Parcelable.Creator<InviteMemberPM> CREATOR = new Parcelable.Creator<InviteMemberPM>() {

		@Override
		public InviteMemberPM createFromParcel(Parcel parcel) {
			InviteMemberPM inviteMemberPM = new InviteMemberPM();
			inviteMemberPM.type = parcel.readString();
			inviteMemberPM.date = parcel.readLong();
			inviteMemberPM.content = parcel.readParcelable(InviteMemberMC.class.getClassLoader());
			return inviteMemberPM;
		}

		@Override
		public InviteMemberPM[] newArray(int size) {
			return new InviteMemberPM[size];
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
