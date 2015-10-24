package com.kinth.football.listener;

/**
 * 朋友圈保存数据的监听
 * @author Sola
 *
 */
public interface MomentsSaveDataListener {
	void onMomentsSaveDataStart();//开始保存数据到数据库
	void onMomentsSaveDataCancle();//取消--暂时没用，凑数
	void onMomentsSaveDataFinish();//保存数据到数据库完成
}
