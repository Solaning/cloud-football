package com.kinth.football.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 地区 实体类
 * @author Botision.Huang
 *
 */
public class RegionBean implements Parcelable{

	private int id;
	private String name;
	private int cityId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public static final Parcelable.Creator<RegionBean> CREATOR = new Creator<RegionBean>() {
		@Override  
		public RegionBean createFromParcel(Parcel parcel) {
			RegionBean region = new RegionBean();
			region.id = parcel.readInt();
			region.name = parcel.readString();
			region.cityId = parcel.readInt();
			return region;  
		}  
		
		@Override  
		public RegionBean[] newArray(int size) {
			// TODO Auto-generated method stub  
			return new RegionBean[size];  
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(this.id);
		dest.writeString(this.name);
		dest.writeInt(this.cityId);
	}

}
