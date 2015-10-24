package com.kinth.football.bean.moments;

import java.util.Arrays;
import java.util.List;

import com.kinth.football.bean.Pageable;
import com.kinth.football.dao.Player;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 朋友圈个人空间里的服务器回复数据格式
 * 因为是都是一个人的动态，所以个人信息再player字段里，sharing里的player字段置为null
 * @author Sola
 * 
 */
public class MomentsPersonalZoneResponse implements Parcelable{
	private Pageable pageable;
	private Player player;
	private List<Sharing> sharings;
	
	public static final Parcelable.Creator<MomentsPersonalZoneResponse> CREATOR = new Creator<MomentsPersonalZoneResponse>() {
		
		@Override
		public MomentsPersonalZoneResponse[] newArray(int size) {
			return new MomentsPersonalZoneResponse[size];
		}
		
		@Override
		public MomentsPersonalZoneResponse createFromParcel(Parcel source) {
			MomentsPersonalZoneResponse momentsPersonalZoneResponse = new MomentsPersonalZoneResponse();
			momentsPersonalZoneResponse.pageable = source.readParcelable(Pageable.class.getClassLoader());
			momentsPersonalZoneResponse.player = source.readParcelable(Player.class.getClassLoader());
			
			Parcelable[] pars = source
					.readParcelableArray(Sharing.class
							.getClassLoader());
			momentsPersonalZoneResponse.sharings = Arrays.asList(Arrays.asList(pars)
					.toArray(new Sharing[pars.length]));
			return momentsPersonalZoneResponse;
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.pageable, flags);
		dest.writeParcelable(this.player, flags);
		dest.writeParcelableArray(this.sharings
				.toArray(new Sharing[this.sharings.size()]), flags);
	}

	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public List<Sharing> getSharings() {
		return sharings;
	}

	public void setSharings(List<Sharing> sharings) {
		this.sharings = sharings;
	}

}
