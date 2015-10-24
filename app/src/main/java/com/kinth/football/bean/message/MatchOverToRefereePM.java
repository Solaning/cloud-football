package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛裁判邀请----消息体
 * @author Botision.Huang
 * Date: 2015-3-31
 * Descp:
 */
public class MatchOverToRefereePM extends PushMessageAbstract<MatchOverToRefereeMC>{

	public static final Parcelable.Creator<MatchOverToRefereePM> CREATOR = new Parcelable.Creator<MatchOverToRefereePM>() {

		@Override
		public MatchOverToRefereePM createFromParcel(Parcel parcel) {
			MatchOverToRefereePM matchRefereeInvitationPM = new MatchOverToRefereePM();
			matchRefereeInvitationPM.type = parcel.readString();
			matchRefereeInvitationPM.date = parcel.readLong();
			matchRefereeInvitationPM.content = parcel.readParcelable(MatchCancledMC.class.getClassLoader());
			return matchRefereeInvitationPM;
		}

		@Override
		public MatchOverToRefereePM[] newArray(int size) {
			return new MatchOverToRefereePM[size];
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(this.type);
		dest.writeLong(this.date);
		dest.writeParcelable(this.content, flags);
	}

}
