package com.kinth.football.gallery;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 本地图片路径bean
 * @author Sola
 *
 */
public class CustomGalleryBean implements Parcelable{
	private long id;
	private long image_id;
	private boolean isCamera;// 是否用于放照相机
	private String thumbnailPath;// 缩略图路径
	private String realPath;//真实路径
	
	public CustomGalleryBean(long id, long image_id, boolean isCamera,
			String thumbnailPath, String realPath) {
		super();
		this.id = id;
		this.image_id = image_id;
		this.isCamera = isCamera;
		this.thumbnailPath = thumbnailPath;
		this.realPath = realPath;
	}

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
		this.image_id = parcel.readLong();
		this.isCamera = parcel.readByte()!=0;
		this.thumbnailPath = parcel.readString();
		this.realPath = parcel.readString();
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
	
	public long getImage_id() {
		return image_id;
	}

	public void setImage_id(long image_id) {
		this.image_id = image_id;
	}

	public boolean isCamera() {
		return isCamera;
	}

	public void setCamera(boolean isCamera) {
		this.isCamera = isCamera;
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	public String getRealPath() {
		return realPath;
	}

	public void setRealPath(String realPath) {
		this.realPath = realPath;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeLong(this.id);
		parcel.writeLong(this.image_id);
		parcel.writeByte((byte)(isCamera ? 1:0));
		parcel.writeString(this.thumbnailPath);
		parcel.writeString(this.realPath);
	}

}
