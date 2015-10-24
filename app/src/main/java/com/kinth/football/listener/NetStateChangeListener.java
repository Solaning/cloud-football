package com.kinth.football.listener;

public abstract interface NetStateChangeListener {

	/**
	 * 网络变化的接口
	 * 
	 * @param msg
	 */
	public abstract void onNetStateChange();
}
