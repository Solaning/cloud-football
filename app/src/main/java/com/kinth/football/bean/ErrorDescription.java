package com.kinth.football.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 错误与描述
 * @author Sola
 *
 */
public class ErrorDescription implements Parcelable{
	private String error;
	private String description;

	public static final Parcelable.Creator<ErrorDescription> CREATOR = new Creator<ErrorDescription>(){

		@Override
		public ErrorDescription createFromParcel(Parcel parcel) {
			// TODO Auto-generated method stub
			ErrorDescription err = new ErrorDescription();
			err.error = parcel.readString();
			err.description = parcel.readString();
			return err;
		}
		
		@Override
		public ErrorDescription[] newArray(int size) {
			// TODO Auto-generated method stub
			return new ErrorDescription[size];
		}

	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.error);
		dest.writeString(this.description);
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
