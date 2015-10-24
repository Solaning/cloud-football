package com.kinth.football.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class InvitationResponse implements Parcelable{

	private String invitationUuid;
	private String matchUuid;
	private String teamUuid;
	private String state;
	private String date;
	
	public static final Parcelable.Creator<InvitationResponse> CREATOR = new Creator<InvitationResponse>(){

		@Override
		public InvitationResponse createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			InvitationResponse invitation = new InvitationResponse();
			invitation.invitationUuid = source.readString();
			invitation.matchUuid = source.readString();
			invitation.teamUuid = source.readString();
			invitation.state = source.readString();
			invitation.date = source.readString();
			return invitation;
		}
		
		@Override
		public InvitationResponse[] newArray(int size) {
			// TODO Auto-generated method stub
			return new InvitationResponse[size];
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
		dest.writeString(this.invitationUuid);
		dest.writeString(this.matchUuid);
		dest.writeString(this.teamUuid);
		dest.writeString(this.state);
		dest.writeString(this.date);
	}

	public String getInvitationUuid() {
		return invitationUuid;
	}

	public void setInvitationUuid(String invitationUuid) {
		this.invitationUuid = invitationUuid;
	}

	public String getMatchUuid() {
		return matchUuid;
	}

	public void setMatchUuid(String matchUuid) {
		this.matchUuid = matchUuid;
	}

	public String getTeamUuid() {
		return teamUuid;
	}

	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	

}
