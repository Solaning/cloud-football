package com.kinth.football.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 选中城市与地区的实体
 * @author Sola
 *
 */
public class AddressBean implements Parcelable{
	private String provinceName;  //省份名称
	private int cityId;//城市id
	private String cityName;//城市名称
	private int regionId;//地区id
	private String regionName;//地区名称
	
	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	public int getRegionId() {
		return regionId;
	}

	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public static final Parcelable.Creator<AddressBean> CREATOR = new Creator<AddressBean>() {
		@Override  
		public AddressBean createFromParcel(Parcel parcel) {
			AddressBean city = new AddressBean();
			city.provinceName = parcel.readString();
			city.cityId = parcel.readInt();  
			city.cityName = parcel.readString();
			city.regionId = parcel.readInt();
			city.regionName = parcel.readString();
			return city;  
		}  
		
		@Override  
		public AddressBean[] newArray(int size) {
			// TODO Auto-generated method stub  
			return new AddressBean[size];  
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.provinceName);
		dest.writeInt(this.cityId);
		dest.writeString(this.cityName);
		dest.writeInt(this.regionId);
		dest.writeString(this.regionName);
	}
	
	
}
