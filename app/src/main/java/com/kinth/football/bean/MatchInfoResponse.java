package com.kinth.football.bean;

import java.util.ArrayList;

import com.kinth.football.bean.match.MatchInfo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛状态列表返回结果
 *
 */
public class MatchInfoResponse implements Parcelable{

	private Pageable pageable;
	private ArrayList<MatchInfo> matchs;
	
	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}
	
	public ArrayList<MatchInfo> getMatchs() {
		return matchs;
	}

	public void setMatchs(ArrayList<MatchInfo> matchs) {
		this.matchs = matchs;
	}


	public static final Parcelable.Creator<MatchInfoResponse> CREATOR = new Creator<MatchInfoResponse>() {
		@Override  
		public MatchInfoResponse createFromParcel(Parcel parcel) {
			MatchInfoResponse response = new MatchInfoResponse();
			response.pageable = parcel.readParcelable(Pageable.class.getClassLoader());
			
			MatchInfo[] matchInfos = (MatchInfo[]) parcel.readParcelableArray(MatchInfo.class.getClassLoader());
			for (int i = 0; i < matchInfos.length; i++) {
				response.matchs.add(matchInfos[i]);
			}
			return response;  
		}  
		
		@Override  
		public MatchInfoResponse[] newArray(int size) {
			// TODO Auto-generated method stub  
			return new MatchInfoResponse[size];  
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
		dest.writeParcelableArray((Parcelable[])this.matchs.toArray(), flags);
	}

	
}
