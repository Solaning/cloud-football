package com.kinth.football.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 选中城市的实体
 * @author Sola
 *
 */
public class SelectCityBean implements Parcelable{
	private int cityId;
	private String cityName;
	
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

	public static final Parcelable.Creator<SelectCityBean> CREATOR = new Creator<SelectCityBean>() {
		@Override  
		public SelectCityBean createFromParcel(Parcel parcel) {
			SelectCityBean city = new SelectCityBean();  
			city.cityId = parcel.readInt();  
			city.cityName = parcel.readString();
			return city;  
		}  
		
		@Override  
		public SelectCityBean[] newArray(int size) {
			// TODO Auto-generated method stub  
			return new SelectCityBean[size];  
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.cityId);
		dest.writeString(this.cityName);
	}
	
	
}
