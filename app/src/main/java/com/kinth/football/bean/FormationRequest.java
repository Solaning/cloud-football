package com.kinth.football.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 球队阵容--请求
 * @author Sola
 *
 */
public class FormationRequest implements Parcelable{
	private String description;
	private String image;
	
	public static final Parcelable.Creator<FormationRequest> CREATOR = new Creator<FormationRequest>() {
		@Override  
		public FormationRequest createFromParcel(Parcel parcel) {
			FormationRequest formation = new FormationRequest();  
			formation.description = parcel.readString();  
			formation.image = parcel.readString();
			return formation;  
		}  
		
		@Override  
		public FormationRequest[] newArray(int size) {
			// TODO Auto-generated method stub  
			return new FormationRequest[size];  
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.description);
		dest.writeString(this.image);
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
