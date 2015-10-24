package com.kinth.football.bean.moments;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 评论或者like的网络请求实体
 * 
 * @author Sola
 *
 */
public class CommentRequest implements Parcelable {
	private String sharingUuid;
	private String type;// "{COMMENT | LIKE}",
	private String comment;

	public static final Parcelable.Creator<CommentRequest> CREATOR = new Creator<CommentRequest>() {

		@Override
		public CommentRequest[] newArray(int size) {
			return new CommentRequest[size];
		}

		@Override
		public CommentRequest createFromParcel(Parcel source) {
			CommentRequest commentRequest = new CommentRequest();
			commentRequest.sharingUuid = source.readString();
			commentRequest.type = source.readString();
			commentRequest.comment = source.readString();
			return commentRequest;
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.sharingUuid);
		dest.writeString(this.type);
		dest.writeString(this.comment);
	}

	public String getSharingUuid() {
		return sharingUuid;
	}

	public void setSharingUuid(String sharingUuid) {
		this.sharingUuid = sharingUuid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
