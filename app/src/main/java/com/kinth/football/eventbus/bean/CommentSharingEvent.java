package com.kinth.football.eventbus.bean;

import com.kinth.football.bean.moments.Comment;

/**
 * 评论或者like动态的事件
 * @author Sola
 *
 */
public class CommentSharingEvent {
	private String sharingUuid;
	private Comment comment;

	public CommentSharingEvent(String sharingUuid, Comment comment) {
		super();
		this.sharingUuid = sharingUuid;
		this.comment = comment;
	}

	public String getSharingUuid() {
		return sharingUuid;
	}

	public void setSharingUuid(String sharingUuid) {
		this.sharingUuid = sharingUuid;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}
	
}
