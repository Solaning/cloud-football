package com.kinth.football.bean.moments;

import java.util.Arrays;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 朋友圈的集合
 * @author Sola
 *
 */
public class SharingWithComments implements Parcelable, Comparable<SharingWithComments>{
	private Sharing sharing;//该动态
	private List<Comment> comments;//动态下的评论和点赞
	
	public static final Parcelable.Creator<SharingWithComments> CREATOR = new Creator<SharingWithComments>() {
		
		@Override
		public SharingWithComments[] newArray(int size) {
			return new SharingWithComments[size];
		}
		
		@Override
		public SharingWithComments createFromParcel(Parcel source) {
			SharingWithComments sharings = new SharingWithComments();
			sharings.sharing = source.readParcelable(Sharing.class.getClassLoader());
			
			Parcelable[] pars = source.readParcelableArray(Comment.class.getClassLoader());
			sharings.comments = Arrays.asList(Arrays.asList(pars).toArray(new Comment[pars.length]));
			
			return sharings;
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.sharing, flags);
		dest.writeParcelableArray(this.comments.toArray(new Comment[this.comments.size()]), flags);
	}

	public Sharing getSharing() {
		return sharing;
	}

	public void setSharing(Sharing sharing) {
		this.sharing = sharing;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SharingWithComments) {
			SharingWithComments user = (SharingWithComments) obj;
			return sharing.equals(user.sharing);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return sharing.hashCode();
	}

	@Override
	public int compareTo(SharingWithComments another) {
		return sharing.compareTo(another.getSharing());
	}
}
