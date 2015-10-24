package com.kinth.football.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.kinth.football.dao.Player;

public class User implements Parcelable{
	private Player player;//球员信息
	private String token;//token
	private String password;//密码

	public static final Parcelable.Creator<User> CREATOR = new Creator<User>() {
		@Override
		public User createFromParcel(Parcel parcel) {
			User user = new User();
			user.player = parcel.readParcelable(Player.class.getClassLoader());
			user.token = parcel.readString();
			user.password = parcel.readString();
			return user;
		}

		@Override
		public User[] newArray(int size) {
			// TODO Auto-generated method stub
			return new User[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.player, flags);
		dest.writeString(this.token);
		dest.writeString(this.password);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
