package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 申请加入球队的推送消息--消息体
 * @author Sola
 * 
 */
public class RequestJoinTeamPM extends PushMessageAbstract<RequestJoinTeamMC>{
	
	public static final Parcelable.Creator<RequestJoinTeamPM> CREATOR = new Parcelable.Creator<RequestJoinTeamPM>() {

		@Override
		public RequestJoinTeamPM createFromParcel(Parcel parcel) {
			RequestJoinTeamPM requestJoinTeamPM = new RequestJoinTeamPM();
			requestJoinTeamPM.type = parcel.readString();
			requestJoinTeamPM.date = parcel.readLong();
			requestJoinTeamPM.content = parcel.readParcelable(InviteMemberMC.class.getClassLoader());
			return requestJoinTeamPM;
		}

		@Override
		public RequestJoinTeamPM[] newArray(int size) {
			return new RequestJoinTeamPM[size];
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
