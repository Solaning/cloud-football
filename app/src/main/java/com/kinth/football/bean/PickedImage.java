package com.kinth.football.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class PickedImage implements Parcelable{
	private String path;//图片的路径
	
	public static final Parcelable.Creator<PickedImage> CREATOR = new Parcelable.Creator<PickedImage>() {

		@Override
		public PickedImage createFromParcel(Parcel parcel) {
			return new PickedImage(parcel);
		}

		@Override
		public PickedImage[] newArray(int size) {
			return new PickedImage[size];
		}
	};
	
	public PickedImage(String path) {
		super();
		this.path = path;
	}
	
	public PickedImage(Parcel parcel) {
		this.path = parcel.readString();
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeString(this.path);
	}
	
}
