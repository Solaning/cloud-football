package com.kinth.football.eventbus.message;

import com.kinth.football.bean.message.SharingCommentedPM;

/**
 * 朋友圈评论动态的推送消息
 * @author Sola
 *
 */
public class SharingCommentedPushMessageEvent {
	private SharingCommentedPM sharingCommentedPM;

	public SharingCommentedPushMessageEvent(
			SharingCommentedPM sharingCommentedPM) {
		super();
		this.sharingCommentedPM = sharingCommentedPM;
	}

	public SharingCommentedPM getSharingCommentedPM() {
		return sharingCommentedPM;
	}

	public void setSharingCommentedPM(SharingCommentedPM sharingCommentedPM) {
		this.sharingCommentedPM = sharingCommentedPM;
	}

	
	
}
