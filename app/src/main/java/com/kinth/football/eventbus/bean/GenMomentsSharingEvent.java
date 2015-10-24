package com.kinth.football.eventbus.bean;

import com.kinth.football.bean.moments.Sharing;

/**
 * 发送朋友圈动态的事件
 * @author Sola
 *
 */
public class GenMomentsSharingEvent {
	private Sharing sharing;

	public GenMomentsSharingEvent(Sharing sharing) {
		super();
		this.sharing = sharing;
	}

	public Sharing getSharing() {
		return sharing;
	}

	public void setSharing(Sharing sharing) {
		this.sharing = sharing;
	}
	
}
