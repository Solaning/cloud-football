package com.kinth.football.bean.moments;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 发动态时的网络请求实体
 * @author Sola
 *
 */
public class SharingRequest implements Parcelable{
	
	private String type;//"{'TEXT' | 'IMAGE' | 'VIDEO' | 'URL'}",
	private String comment;
	private String url;
	private List<String> imageUrls;//["xxx", "xxxx"]

	public static final Parcelable.Creator<SharingRequest> CREATOR = new Creator<SharingRequest>() {
		
		@Override
		public SharingRequest[] newArray(int size) {
			return new SharingRequest[size];
		}
		
		@Override
		public SharingRequest createFromParcel(Parcel source) {
			SharingRequest sharingRequest = new SharingRequest();
			sharingRequest.type = source.readString();
			sharingRequest.comment = source.readString();
			sharingRequest.url = source.readString();
			source.readStringList(sharingRequest.imageUrls);
			return sharingRequest;
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
		dest.writeString(this.comment);
		dest.writeString(this.url);
		dest.writeStringList(this.imageUrls);
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}

}
