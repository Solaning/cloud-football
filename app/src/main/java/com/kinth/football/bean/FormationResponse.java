package com.kinth.football.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 创建阵容--返回、响应
 * @author Sola
 *
 */
public class FormationResponse implements Parcelable{
	private String uuid;
	private String description;
	private String image;
	
	public static final Parcelable.Creator<FormationResponse> CREATOR = new Creator<FormationResponse>() {
		@Override  
		public FormationResponse createFromParcel(Parcel parcel) {
			FormationResponse team = new FormationResponse();  
			team.uuid = parcel.readString();
			team.description = parcel.readString();
			team.image = parcel.readString();  
			return team;  
		}  
		
		@Override  
		public FormationResponse[] newArray(int size) {
			// TODO Auto-generated method stub  
			return new FormationResponse[size];  
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.uuid);
		dest.writeString(this.description);
		dest.writeString(this.image);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

}
