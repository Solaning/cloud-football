package com.kinth.football.bean;

import java.util.List;

public class MyInvitationResponse {

	private Pageable pageable;
	private List<InvitationResponse> invitations;
	
	public Pageable getPageable() {
		return pageable;
	}
	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}
	public List<InvitationResponse> getInvitations() {
		return invitations;
	}
	public void setInvitations(List<InvitationResponse> invitations) {
		this.invitations = invitations;
	}
	
	

}
