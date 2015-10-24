package com.kinth.football.eventbus.bean;

public class ModifyTeamIsLiked {
	private String teamUuid;
	private boolean isliked;
	private int like;
  
	public ModifyTeamIsLiked(String teamUuid, boolean isliked,int like) {
		this.isliked = isliked;
		this.teamUuid = teamUuid;
		this.like = like;
	}

	public void setIsliked(boolean isliked) {
		this.isliked = isliked;
	}

	public boolean getIsliked() {
		return isliked;
	}
   public void setLike(int like){
	   this.like = like;
   }
   public int getLike(){
	   return like;
   }
	public String getTeamUuid() {
		return teamUuid;
	}

	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}
}
