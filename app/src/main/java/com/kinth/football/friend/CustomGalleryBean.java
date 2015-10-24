package com.kinth.football.friend;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 本地图片路径bean
 * @author Sola
 * 暂时废弃
 */
@Deprecated
public class CustomGalleryBean implements Parcelable{
	private long id;
	private boolean isCamera;// 是否用于放照相机
	private String path;// 路径
	
	public static final Parcelable.Creator<CustomGalleryBean> CREATOR = new Parcelable.Creator<CustomGalleryBean>() {

		@Override
		public CustomGalleryBean createFromParcel(Parcel parcel) {
			return new CustomGalleryBean(parcel);
		}

		@Override
		public CustomGalleryBean[] newArray(int size) {
			return new CustomGalleryBean[size];
		}
	};
	
	public CustomGalleryBean(Parcel parcel) {
		this.id = parcel.readLong();
		this.isCamera = parcel.readByte()!=0;
		this.path = parcel.readString();
	}

	public CustomGalleryBean() {
		// TODO Auto-generated constructor stub
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public boolean isCamera() {
		return isCamera;
	}

	public void setCamera(boolean isCamera) {
		this.isCamera = isCamera;
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
		parcel.writeLong(this.id);
		parcel.writeByte((byte)(isCamera ?1:0));
		parcel.writeString(this.path);
	}

}
