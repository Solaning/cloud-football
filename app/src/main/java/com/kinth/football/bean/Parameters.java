package com.kinth.football.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 通讯接口的参数
 * @author Sola
 *
 */
public class Parameters implements Parcelable{
	private String CFB_Udid;//设备唯一标识
	private String CFB_Device;// 设备
	private String CFB_Resolution;// 分辨率
	private String CFB_Platform_Version;// 设备系统版本
	private String CFB_App_Version;// APP版本
	private String CFB_App_Source;// APP来源
	private String CFB_Operator;// 运营商
	private int mScreenWidth;//屏幕宽
	private int mScreenHeight;//屏幕高


	public static final Parcelable.Creator<Parameters> CREATOR = new Creator<Parameters>() {
		@Override  
		public Parameters createFromParcel(Parcel parcel) {
			Parameters response = new Parameters();
			response.CFB_Udid = parcel.readString();
			response.CFB_Device = parcel.readString();
			response.CFB_Resolution = parcel.readString();
			response.CFB_Platform_Version = parcel.readString();
			response.CFB_App_Version = parcel.readString();
			response.CFB_App_Source = parcel.readString();
			response.CFB_Operator = parcel.readString();
			response.mScreenWidth = parcel.readInt();
			response.mScreenHeight = parcel.readInt();
			return response;  
		}  
		
		@Override  
		public Parameters[] newArray(int size) {
			// TODO Auto-generated method stub  
			return new Parameters[size];  
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.CFB_Udid);
		dest.writeString(this.CFB_Device);
		dest.writeString(this.CFB_Resolution);
		dest.writeString(this.CFB_Platform_Version);
		dest.writeString(this.CFB_App_Version);
		dest.writeString(this.CFB_App_Source);
		dest.writeString(this.CFB_Operator);
		dest.writeInt(this.mScreenWidth);
		dest.writeInt(this.mScreenHeight);
	}

	public String getCFB_Udid() {
		return CFB_Udid;
	}

	public void setCFB_Udid(String cFB_Udid) {
		CFB_Udid = cFB_Udid;
	}

	public String getCFB_Device() {
		return CFB_Device;
	}

	public void setCFB_Device(String cFB_Device) {
		CFB_Device = cFB_Device;
	}

	public String getCFB_Resolution() {
		return CFB_Resolution;
	}

	public void setCFB_Resolution(String cFB_Resolution) {
		CFB_Resolution = cFB_Resolution;
	}

	public String getCFB_Platform_Version() {
		return CFB_Platform_Version;
	}

	public void setCFB_Platform_Version(String cFB_Platform_Version) {
		CFB_Platform_Version = cFB_Platform_Version;
	}

	public String getCFB_App_Version() {
		return CFB_App_Version;
	}

	public void setCFB_App_Version(String cFB_App_Version) {
		CFB_App_Version = cFB_App_Version;
	}

	public String getCFB_App_Source() {
		return CFB_App_Source;
	}

	public void setCFB_App_Source(String cFB_App_Source) {
		CFB_App_Source = cFB_App_Source;
	}

	public String getCFB_Operator() {
		return CFB_Operator;
	}

	public void setCFB_Operator(String cFB_Operator) {
		CFB_Operator = cFB_Operator;
	}

	public int getmScreenWidth() {
		return mScreenWidth;
	}

	public void setmScreenWidth(int mScreenWidth) {
		this.mScreenWidth = mScreenWidth;
	}

	public int getmScreenHeight() {
		return mScreenHeight;
	}

	public void setmScreenHeight(int mScreenHeight) {
		this.mScreenHeight = mScreenHeight;
	}

}
