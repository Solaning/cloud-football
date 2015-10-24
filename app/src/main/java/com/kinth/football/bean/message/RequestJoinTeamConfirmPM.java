package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 申请加入球队确认的推送消息--消息体
 * @author Sola
 *
 */
public class RequestJoinTeamConfirmPM extends PushMessageAbstract<RequestJoinTeamConfirmMC>{
	
	public static final Parcelable.Creator<RequestJoinTeamConfirmPM> CREATOR = new Parcelable.Creator<RequestJoinTeamConfirmPM>() {

		@Override
		public RequestJoinTeamConfirmPM createFromParcel(Parcel parcel) {
			RequestJoinTeamConfirmPM requestJoinTeamConfirmPM = new RequestJoinTeamConfirmPM();
			requestJoinTeamConfirmPM.type = parcel.readString();
			requestJoinTeamConfirmPM.date = parcel.readLong();
			requestJoinTeamConfirmPM.content = parcel.readParcelable(InviteMemberMC.class.getClassLoader());
			return requestJoinTeamConfirmPM;
		}

		@Override
		public RequestJoinTeamConfirmPM[] newArray(int size) {
			return new RequestJoinTeamConfirmPM[size];
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
