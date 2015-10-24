package com.kinth.football.friend;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 已经选中的图片，含选中顺序和图片原始路径
 * @author Sola
 *
 */
public class PickedImage implements Parcelable{
	private int position;//选中时的顺序
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
	
	public PickedImage(int position, String path) {
		super();
		this.position = position;
		this.path = path;
	}
	
	public PickedImage(Parcel parcel) {
		this.position = parcel.readInt();
		this.path = parcel.readString();
	}
	
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
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
		parcel.writeInt(this.position);
		parcel.writeString(this.path);
	}
	
}
