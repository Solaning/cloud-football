package com.kinth.football.bean.message;

import com.kinth.football.bean.moments.Comment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 评论动态--消息内容
 * @author Sola
 *
 */
public class SharingCommentedMC extends MessageContent{
	private Comment comment;
	
	public static final Parcelable.Creator<SharingCommentedMC> CREATOR = new Creator<SharingCommentedMC>() {
		
		@Override
		public SharingCommentedMC[] newArray(int size) {
			return new SharingCommentedMC[size];
		}
		
		@Override
		public SharingCommentedMC createFromParcel(Parcel source) {
			SharingCommentedMC sharingCommentedMC = new SharingCommentedMC();
			sharingCommentedMC.comment = source.readParcelable(Comment.class.getClassLoader());
			sharingCommentedMC.description = source.readString();
			return sharingCommentedMC;
		}
	};
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.comment, flags);
		dest.writeString(this.description);
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	
}
