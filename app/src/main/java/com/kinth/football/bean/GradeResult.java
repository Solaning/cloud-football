package com.kinth.football.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 对某个球员的评分
 * @author Sola
 *
 */
public class GradeResult implements Parcelable{

	private String name;
	private String position;//新增position，接口返回数据没有该字段
	private String gender;
	private String phone;
	private String email;
	private int cityId;
    private String picture;
    private int age;
    private int height;
    private int weight;
    private String uuid;
    private String accountName;
    private String city;
    private String province;
    private long date;
    private float strength;
    private float skill;//需要的--评分
    private float pace;
    private float awareness;
    private float morality;//需要的--评分
    private float assist;
    private float score;
	
	public static final Parcelable.Creator<GradeResult> CREATOR = new Creator<GradeResult>() {
		@Override
		public GradeResult createFromParcel(Parcel parcel) {
			GradeResult match = new GradeResult();
			match.name = parcel.readString();
			match.position = parcel.readString();
			match.gender = parcel.readString();
			match.phone = parcel.readString();
			match.email = parcel.readString();
			match.cityId = parcel.readInt();
			match.picture = parcel.readString();
			match.age = parcel.readInt();
			match.height = parcel.readInt();
			match.weight = parcel.readInt();
			match.uuid = parcel.readString();
			match.accountName = parcel.readString();
			match.city = parcel.readString();
			match.province = parcel.readString();
			match.date = parcel.readLong();
			match.strength = parcel.readFloat();
			match.skill = parcel.readFloat();
			match.pace = parcel.readFloat();
			match.awareness = parcel.readFloat();
			match.morality = parcel.readFloat();
			match.assist = parcel.readFloat();
			match.score = parcel.readFloat();
			return match;
		}

		@Override
		public GradeResult[] newArray(int size) {
			// TODO Auto-generated method stub
			return new GradeResult[size];
		}
	};
    
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeString(this.position);
		dest.writeString(this.gender);
		dest.writeString(this.phone);
		dest.writeString(this.email);
		dest.writeInt(this.cityId);
		dest.writeString(this.picture);
		dest.writeInt(this.age);
		dest.writeInt(this.height);
		dest.writeInt(this.weight);
		dest.writeString(this.uuid);
		dest.writeString(this.accountName);
		dest.writeString(this.city);
		dest.writeString(this.province);
		dest.writeFloat(this.date);
		dest.writeFloat(this.strength);
		dest.writeFloat(this.skill);
		dest.writeFloat(this.pace);
		dest.writeFloat(this.awareness);
		dest.writeFloat(this.morality);
		dest.writeFloat(this.assist);
		dest.writeFloat(this.score);
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public float getStrength() {
		return strength;
	}

	public void setStrength(float strength) {
		this.strength = strength;
	}

	public float getSkill() {
		return skill;
	}

	public void setSkill(float skill) {
		this.skill = skill;
	}

	public float getPace() {
		return pace;
	}

	public void setPace(float pace) {
		this.pace = pace;
	}

	public float getAwareness() {
		return awareness;
	}

	public void setAwareness(float awareness) {
		this.awareness = awareness;
	}

	public float getMorality() {
		return morality;
	}

	public void setMorality(float morality) {
		this.morality = morality;
	}

	public float getAssist() {
		return assist;
	}

	public void setAssist(float assist) {
		this.assist = assist;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	
}
