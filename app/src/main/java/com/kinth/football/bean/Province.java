package com.kinth.football.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Province implements Parcelable{

	private int id;
	private String name;
	
	public static final Parcelable.Creator<Province> CREATOR = new Creator<Province>() {

		@Override
		public Province createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			Province pro = new Province();
			pro.id = source.readInt();
			pro.name = source.readString();
			return pro;
		}

		@Override
		public Province[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Province[size];
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
}
