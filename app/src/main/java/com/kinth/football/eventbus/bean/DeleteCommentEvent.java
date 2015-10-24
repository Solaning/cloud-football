package com.kinth.football.eventbus.bean;

/**
 * 删除评论的事件
 * 
 * @author Sola
 *
 */
public class DeleteCommentEvent {
	private String sharingUuid;
	private String commentUuid;

	public DeleteCommentEvent(String sharingUuid, String commentUuid) {
		super();
		this.sharingUuid = sharingUuid;
		this.commentUuid = commentUuid;
	}

	public String getSharingUuid() {
		return sharingUuid;
	}

	public void setSharingUuid(String sharingUuid) {
		this.sharingUuid = sharingUuid;
	}

	public String getCommentUuid() {
		return commentUuid;
	}

	public void setCommentUuid(String commentUuid) {
		this.commentUuid = commentUuid;
	}

}
