package com.kinth.football.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 创建球队字段
 * @author Sola
 *
 */
public class TeamRequest implements Parcelable{
	private String name;//球队名字
	private String slogan;//球队口号
	private String description;//球队介绍
	private String badge;//球队队徽
	private String familyPhoto;//全家福
	private String firstCaptainUuid;//第一队长
	private String secondCaptainUuid;//第二队长
	private String thirdCaptainUuid;//第三队长
	private Integer cityId;//所在城市代码
	private Integer regionId;//所在地区代码
	
	/**后来加的字段 By Botision**/
	private String homeField;    //主场
	private String homeJersey;   //主场队服
	private String roadJersey;   //客场队服
	private String alternateJersey;   //第三队服
	
	public TeamRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static final Parcelable.Creator<TeamRequest> CREATOR = new Creator<TeamRequest>() {
		@Override  
		public TeamRequest createFromParcel(Parcel parcel) {
			TeamRequest team = new TeamRequest();  
			team.name = parcel.readString();  
			team.slogan = parcel.readString();
			team.description = parcel.readString();
			team.badge = parcel.readString();
			team.familyPhoto = parcel.readString();
			team.firstCaptainUuid = parcel.readString();
			team.secondCaptainUuid = parcel.readString();
			team.thirdCaptainUuid = parcel.readString();
			team.cityId = parcel.readInt();
			team.regionId = parcel.readInt();
			team.homeField = parcel.readString();
			team.homeJersey = parcel.readString();
			team.roadJersey = parcel.readString();
			team.alternateJersey = parcel.readString();
			return team;  
		}  
		
		@Override  
		public TeamRequest[] newArray(int size) {
			// TODO Auto-generated method stub  
			return new TeamRequest[size];  
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
		dest.writeString(this.slogan);
		dest.writeString(this.description);
		dest.writeString(this.badge);
		dest.writeString(this.familyPhoto);
		dest.writeString(this.firstCaptainUuid);
		dest.writeString(this.secondCaptainUuid);
		dest.writeString(this.thirdCaptainUuid);
		dest.writeInt(this.cityId);
		dest.writeInt(this.regionId);
		dest.writeString(this.homeJersey);
		dest.writeString(this.homeJersey);
		dest.writeString(this.roadJersey);
		dest.writeString(this.alternateJersey);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSlogan() {
		return slogan;
	}

	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBadge() {
		return badge;
	}

	public void setBadge(String badge) {
		this.badge = badge;
	}

	public String getFamilyPhoto() {
		return familyPhoto;
	}

	public void setFamilyPhoto(String familyPhoto) {
		this.familyPhoto = familyPhoto;
	}

	public String getFirstCaptainUuid() {
		return firstCaptainUuid;
	}

	public void setFirstCaptainUuid(String firstCaptainUuid) {
		this.firstCaptainUuid = firstCaptainUuid;
	}

	public String getSecondCaptainUuid() {
		return secondCaptainUuid;
	}

	public void setSecondCaptainUuid(String secondCaptainUuid) {
		this.secondCaptainUuid = secondCaptainUuid;
	}

	public String getThirdCaptainUuid() {
		return thirdCaptainUuid;
	}

	public void setThirdCaptainUuid(String thirdCaptainUuid) {
		this.thirdCaptainUuid = thirdCaptainUuid;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getRegionId() {
		return regionId;
	}

	public void setRegionId(Integer regionId) {
		this.regionId = regionId;
	}

	public String getHomeField() {
		return homeField;
	}

	public void setHomeField(String homeField) {
		this.homeField = homeField;
	}

	public String getHomeJersey() {
		return homeJersey;
	}

	public void setHomeJersey(String homeJersey) {
		this.homeJersey = homeJersey;
	}

	public String getRoadJersey() {
		return roadJersey;
	}

	public void setRoadJersey(String roadJersey) {
		this.roadJersey = roadJersey;
	}

	public String getAlternateJersey() {
		return alternateJersey;
	}

	public void setAlternateJersey(String alternateJersey) {
		this.alternateJersey = alternateJersey;
	}

}
