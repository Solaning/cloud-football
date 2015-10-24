package com.kinth.football.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Update implements Parcelable{
	
	private String version;
	private String description;
	private String url;
	
	public static final Parcelable.Creator<Update> CREATOR = new Creator<Update>() {
		@Override  
		public Update createFromParcel(Parcel parcel) {
			Update update = new Update();
			update.version = parcel.readString();
			update.description = parcel.readString();
			update.url = parcel.readString();
			return update;
		}  
		
		@Override  
		public Update[] newArray(int size) {
			// TODO Auto-generated method stub  
			return new Update[size];  
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
		dest.writeString(this.version);
		dest.writeString(this.description);
		dest.writeString(this.url);
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
