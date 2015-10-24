package com.kinth.football.bean.match;

import android.os.Parcel;
import android.os.Parcelable;

public class SkillAndMormality implements Parcelable{

	private float skill;     //球技
	private float morality;  //球品
	private String result;   //结果
	private String kickOff;  //开赛时间
	
	public static final Parcelable.Creator<SkillAndMormality> CREATOR = new Creator<SkillAndMormality>() {
		@Override
		public SkillAndMormality createFromParcel(Parcel parcel) {
			SkillAndMormality skm = new SkillAndMormality();
			skm.skill = parcel.readInt();
			skm.morality = parcel.readInt();
			skm.result = parcel.readString();
			return skm;
		}

		@Override
		public SkillAndMormality[] newArray(int size) {
			// TODO Auto-generated method stub
			return new SkillAndMormality[size];
		}
	};
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeFloat(this.skill);
		dest.writeFloat(this.morality);
		dest.writeString(this.kickOff);
		dest.writeString(this.result);
	}

	public float getSkill() {
		return skill;
	}

	public void setSkill(float skill) {
		this.skill = skill;
	}

	public float getMorality() {
		return morality;
	}

	public void setMorality(float morality) {
		this.morality = morality;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getKickOff() {
		return kickOff;
	}

	public void setKickOff(String kickOff) {
		this.kickOff = kickOff;
	}

}
