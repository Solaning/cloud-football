package com.kinth.football.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 登陆的返回结果
 * @author Sola
 *
 */
public class LoginResponse implements Parcelable {
	private String access_token;//
	private String token_type;//
	private String refresh_token;//
	private long expires_in;// 599590
	private String scope;// "read"

	public static final Parcelable.Creator<LoginResponse> CREATOR = new Creator<LoginResponse>() {
		@Override
		public LoginResponse createFromParcel(Parcel parcel) {
			LoginResponse login = new LoginResponse();
			login.access_token = parcel.readString();
			login.token_type = parcel.readString();
			login.refresh_token = parcel.readString();
			login.expires_in = parcel.readLong();
			login.scope = parcel.readString();
			return login;
		}

		@Override
		public LoginResponse[] newArray(int size) {
			// TODO Auto-generated method stub
			return new LoginResponse[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.access_token);
		dest.writeString(this.token_type);
		dest.writeString(this.refresh_token);
		dest.writeLong(this.expires_in);
		dest.writeString(this.scope);
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getToken_type() {
		return token_type;
	}

	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public long getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(long expires_in) {
		this.expires_in = expires_in;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

}
