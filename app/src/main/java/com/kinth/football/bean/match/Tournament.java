package com.kinth.football.bean.match;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 锦标赛实体
 * 
 * @author Sola
 * 
 */
public class Tournament implements Parcelable {
	private String uuid;
	private String name;
	private String shortName;
	private String picture;
	private long date;
	private String site;
	private String linkInHomePage;
	private String linkInPersonInfo;
	private String linkInMatch;
	

	public static final Parcelable.Creator<Tournament> CREATOR = new Parcelable.Creator<Tournament>() {

		@Override
		public Tournament createFromParcel(Parcel parcel) {
			// TODO Auto-generated method stub
			Tournament tournament = new Tournament();
			tournament.uuid = parcel.readString();
			tournament.name = parcel.readString();
			tournament.shortName = parcel.readString();
			tournament.picture = parcel.readString();
			tournament.date = parcel.readLong();
			tournament.site = parcel.readString();
			tournament.linkInHomePage = parcel.readString();
			tournament.linkInPersonInfo = parcel.readString();
			tournament.linkInMatch = parcel.readString();
			return tournament;
		}

		@Override
		public Tournament[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Tournament[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		// TODO Auto-generated method stub
		parcel.writeString(this.uuid);
		parcel.writeString(this.name);
		parcel.writeString(this.shortName);
		parcel.writeString(this.picture);
		parcel.writeLong(this.date);
		parcel.writeString(this.site);
		parcel.writeString(this.linkInHomePage);
		parcel.writeString(this.linkInPersonInfo);
		parcel.writeString(this.linkInMatch);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getLinkInHomePage() {
		return linkInHomePage;
	}

	public void setLinkInHomePage(String linkInHomePage) {
		this.linkInHomePage = linkInHomePage;
	}

	public String getLinkInPersonInfo() {
		return linkInPersonInfo;
	}

	public void setLinkInPersonInfo(String linkInPersonInfo) {
		this.linkInPersonInfo = linkInPersonInfo;
	}

	public String getLinkInMatch() {
		return linkInMatch;
	}

	public void setLinkInMatch(String linkInMatch) {
		this.linkInMatch = linkInMatch;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}
}
