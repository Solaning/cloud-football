package com.kinth.football.ui.team.formation;

import com.kinth.football.dao.Player;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 阵容
 * @author Sola
 *
 */
public class Formation implements Parcelable{
	private String uuid;//uuid
	private String description;//描述
	private String image;//图片路径
	private String name;//名字
	
	public Formation() {
		super();
	}

	public static final Parcelable.Creator<Formation> CREATOR = new Creator<Formation>() {
		@Override
		public Formation createFromParcel(Parcel parcel) {
			Formation formation = new Formation();
			formation.uuid = parcel.readString();
			formation.description = parcel.readString();
			formation.image = parcel.readString();
			formation.name = parcel.readString();
			return formation;
		}

		@Override
		public Formation[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Formation[size];
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.uuid);
		dest.writeString(this.description);
		dest.writeString(this.image);
		dest.writeString(this.name);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
