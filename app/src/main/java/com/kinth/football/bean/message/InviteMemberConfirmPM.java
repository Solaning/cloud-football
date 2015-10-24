package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 邀请球员确认消息--消息体
 * @author Sola
 *
 */
public class InviteMemberConfirmPM extends PushMessageAbstract<InviteMemberConfirmMC> {
	
	public static final Parcelable.Creator<InviteMemberConfirmPM> CREATOR = new Parcelable.Creator<InviteMemberConfirmPM>() {

		@Override
		public InviteMemberConfirmPM createFromParcel(Parcel parcel) {
			InviteMemberConfirmPM inviteMemberConfirmPM = new InviteMemberConfirmPM();
			inviteMemberConfirmPM.type = parcel.readString();
			inviteMemberConfirmPM.date = parcel.readLong();
			inviteMemberConfirmPM.content = parcel.readParcelable(InviteMemberConfirmMC.class.getClassLoader());
			return inviteMemberConfirmPM;
		}

		@Override
		public InviteMemberConfirmPM[] newArray(int size) {
			return new InviteMemberConfirmPM[size];
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
