package com.kinth.football.bean.moments;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.kinth.football.dao.Player;

/**
 * 朋友圈的动态
 * @author Sola
 * 
 */
public class Sharing implements Parcelable,Comparable<Sharing>{
    private String uuid;
    private long date;
    private String type;//: "TEXT",
    private String comment;//
    private String url;
    private List<String> imageUrls = new ArrayList<String>();//: [0],
    private Player player;
    private boolean onTop;//是否指定
    
    public static final Parcelable.Creator<Sharing> CREATOR = new Creator<Sharing>() {
		
		@Override
		public Sharing[] newArray(int size) {
			return new Sharing[size];
		}
		
		@Override
		public Sharing createFromParcel(Parcel source) {
			Sharing sharing = new Sharing();
			sharing.uuid = source.readString();
			sharing.date = source.readLong();
			sharing.type = source.readString();
			sharing.comment = source.readString();
			sharing.url = source.readString();
			source.readStringList(sharing.imageUrls);
			sharing.player = source.readParcelable(Player.class.getClassLoader());
			sharing.onTop = source.readByte() != 0;
			return sharing;
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
		dest.writeLong(this.date);
		dest.writeString(this.type);
		dest.writeString(this.comment);
		dest.writeString(this.url);
		dest.writeStringList(this.imageUrls);
		dest.writeParcelable(this.player, flags);
		dest.writeByte((byte)(this.onTop ? 1 : 0));
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public boolean isOnTop() {
		return onTop;
	}

	public void setOnTop(boolean onTop) {
		this.onTop = onTop;
	}

	@Override
	public int compareTo(Sharing another) {
		if (date < another.date) //这里比较的是什么 sort方法实现的就是按照此比较的东西从小到大排列
			return 1 ;
		if (date > another.date)  
			return -1 ;  
		return 0 ;   
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Sharing) {
			Sharing user = (Sharing) obj;
			return uuid.equals(user.uuid);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}
    
}
