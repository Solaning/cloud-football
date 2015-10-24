package com.kinth.football.bean.match;

import android.os.Parcel;
import android.os.Parcelable;

public class FiveDifferentData implements Parcelable{
	
	private float strength;  //体能
	private float skill;     //技术
	private float attack;    //进攻
	private float defence;   //防守
	private float awareness; //（原本为'意识'）侵略性
	private float composite; //综合数据
	private String result; //比赛结果
	private String kickOff;  //开赛时间
	
	public static final Parcelable.Creator<FiveDifferentData> CREATOR = new Creator<FiveDifferentData>() {
		@Override
		public FiveDifferentData createFromParcel(Parcel parcel) {
			FiveDifferentData fiveDif = new FiveDifferentData();
			fiveDif.strength = parcel.readInt();
			fiveDif.skill = parcel.readInt();
			fiveDif.attack = parcel.readInt();
			fiveDif.defence = parcel.readInt();
			fiveDif.awareness = parcel.readInt();
			fiveDif.composite = parcel.readInt();
			fiveDif.result = parcel.readString();
			fiveDif.kickOff = parcel.readString();
			return fiveDif;
		}

		@Override
		public FiveDifferentData[] newArray(int size) {
			// TODO Auto-generated method stub
			return new FiveDifferentData[size];
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
		dest.writeFloat(this.strength);
		dest.writeFloat(this.skill);
		dest.writeFloat(this.attack);
		dest.writeFloat(this.defence);
		dest.writeFloat(this.awareness);
		dest.writeFloat(this.composite);
		dest.writeString(this.result);
		dest.writeString(this.kickOff);
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

	public float getAttack() {
		return attack;
	}

	public void setAttack(float attack) {
		this.attack = attack;
	}

	public float getDefence() {
		return defence;
	}

	public void setDefence(float defence) {
		this.defence = defence;
	}

	public float getAwareness() {
		return awareness;
	}

	public void setAwareness(float awareness) {
		this.awareness = awareness;
	}

	public float getComposite() {
		return composite;
	}

	public void setComposite(float composite) {
		this.composite = composite;
	}

	public void setComposite(int composite) {
		this.composite = composite;
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
