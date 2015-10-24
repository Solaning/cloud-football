package com.kinth.football.bean.match;

import com.kinth.football.dao.Player;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 该球员在球队的角色
 * @author Sola
 *
 */
public class PlayerInTeam implements Parcelable{
	private String type;//在球队中的角色
	private Player player;
	private boolean creator;//是否创建者
    private Integer jerseyNo; //队服号码 
    
	public static final Parcelable.Creator<PlayerInTeam> CREATOR = new Creator<PlayerInTeam>() {
		@Override
		public PlayerInTeam createFromParcel(Parcel parcel) {
			PlayerInTeam team = new PlayerInTeam();
			team.type = parcel.readString();
			team.player = parcel.readParcelable(Player.class.getClassLoader());
			team.creator = parcel.readByte() != 0;
			team.jerseyNo = parcel.readInt();
			return team;
		}

		@Override
		public PlayerInTeam[] newArray(int size) {
			// TODO Auto-generated method stub
			return new PlayerInTeam[size];
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.type);
		dest.writeParcelable(this.player, flags);
		dest.writeByte((byte)(this.creator ? 1:0));
		dest.writeInt(this.jerseyNo == null ? -1 : this.jerseyNo);
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public boolean isCreator() {
		return creator;
	}

	public void setCreator(boolean creator) {
		this.creator = creator;
	}
	
	public Integer getJerseyNo() {
		return jerseyNo;
	}

	public void setJerseyNo(Integer jerseyNo) {
		this.jerseyNo = jerseyNo;
	}

}
