package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 退出球队--消息体
 * @author Sola
 *
 */
public class ExitTeamPM extends PushMessageAbstract<ExitTeamMC>{
	
	public static final Parcelable.Creator<ExitTeamPM> CREATOR = new Parcelable.Creator<ExitTeamPM>() {

		@Override
		public ExitTeamPM createFromParcel(Parcel parcel) {
			ExitTeamPM exitTeamPM = new ExitTeamPM();
			exitTeamPM.type = parcel.readString();
			exitTeamPM.date = parcel.readLong();
			exitTeamPM.content = parcel.readParcelable(ExitTeamMC.class.getClassLoader());
			return exitTeamPM;
		}

		@Override
		public ExitTeamPM[] newArray(int size) {
			return new ExitTeamPM[size];
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
