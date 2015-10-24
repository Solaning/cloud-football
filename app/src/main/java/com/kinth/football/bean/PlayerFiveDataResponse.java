package com.kinth.football.bean;


import android.os.Parcel;
import android.os.Parcelable;

public class PlayerFiveDataResponse implements Parcelable{

	private int[] skill;
	private int[] morality;
	
	public static final Parcelable.Creator<PlayerFiveDataResponse> CREATOR = new Creator<PlayerFiveDataResponse>() {
		@Override  
		public PlayerFiveDataResponse createFromParcel(Parcel parcel) {
			PlayerFiveDataResponse five = new PlayerFiveDataResponse();
			parcel.readIntArray(five.skill);   //这里有待商榷
			parcel.readIntArray(five.morality);
			
			return five;  
		}  
		
		@Override  
		public PlayerFiveDataResponse[] newArray(int size) {
			// TODO Auto-generated method stub  
			return new PlayerFiveDataResponse[size];  
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeIntArray(this.skill);	
		arg0.writeIntArray(this.morality);	
	}
	public int[] getSkill() {
		return skill;
	}
	public void setSkill(int[] skill) {
		this.skill = skill;
	}
	public int[] getMorality() {
		return morality;
	}
	public void setMorality(int[] morality) {
		this.morality = morality;
	}
	
}
