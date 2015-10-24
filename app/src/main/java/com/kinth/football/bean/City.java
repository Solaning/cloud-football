package com.kinth.football.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class City implements Parcelable{

	private int id;
	private String name;
	private int province_id;
	
	public static final Parcelable.Creator<City> CREATOR = new Creator<City>(){

		@Override
		public City createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			City city = new City();
			city.id = source.readInt();
			city.name = source.readString();
			city.province_id = source.readInt();
			return city;
		}
		
		@Override
		public City[] newArray(int size) {
			// TODO Auto-generated method stub
			return new City[size];
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
		dest.writeInt(this.province_id);
	}

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

	public int getProvince_id() {
		return province_id;
	}

	public void setProvince_id(int province_id) {
		this.province_id = province_id;
	}
}
