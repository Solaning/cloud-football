package com.kinth.football.bean.moments;

import com.kinth.football.dao.Player;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 评论，用于解析网络json数据和显示
 * @author Sola
 *
 */
public class Comment implements Parcelable {
	private String uuid;
	private String replyToPlayerUuid;
	private String sharingUuid;
	private String type;
	private String comment;
	private Player player;
	private long date;

	public static final Parcelable.Creator<Comment> CREATOR = new Creator<Comment>() {

		@Override
		public Comment[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Comment[size];
		}

		@Override
		public Comment createFromParcel(Parcel source) {
			Comment comment = new Comment();
			comment.uuid = source.readString();
			comment.replyToPlayerUuid = source.readString();
			comment.sharingUuid = source.readString();
			comment.type = source.readString();
			comment.comment = source.readString();
			comment.player = source.readParcelable(Player.class
					.getClassLoader());
			comment.date = source.readLong();
			return comment;
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.uuid);
		dest.writeString(this.replyToPlayerUuid);
		dest.writeString(this.sharingUuid);
		dest.writeString(this.type);
		dest.writeString(this.comment);
		dest.writeParcelable(this.player, flags);
		dest.writeLong(this.date);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getReplyToPlayerUuid() {
		return replyToPlayerUuid;
	}

	public void setReplyToPlayerUuid(String replyToPlayerUuid) {
		this.replyToPlayerUuid = replyToPlayerUuid;
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

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

}
