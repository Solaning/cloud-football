package com.kinth.football.bean.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 评论动态--消息体
 * @author Sola
 *
 */
public class SharingCommentedPM extends PushMessageAbstract<SharingCommentedMC>{
	public static final Parcelable.Creator<SharingCommentedPM> CREATOR = new Creator<SharingCommentedPM>() {
		
		@Override
		public SharingCommentedPM[] newArray(int size) {
			return new SharingCommentedPM[size];
		}
		
		@Override
		public SharingCommentedPM createFromParcel(Parcel source) {
			SharingCommentedPM sharingCommentedPM = new SharingCommentedPM();
			sharingCommentedPM.type = source.readString();
			sharingCommentedPM.date = source.readLong();
			sharingCommentedPM.content = source.readParcelable(SharingCommentedMC.class.getClassLoader());
			return sharingCommentedPM;
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
