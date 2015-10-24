package com.kinth.football.ui.match.invite;

public interface InviteMatchConstants {
	
	/**
	 * 跳转回调请求码：选择城市
	 */
	public static final int REQUEST_CODE_CITY = 9003;//

	/**
	 * 跳转回调请求码：选择区县
	 */
	public static final int REQUEST_CODE_REGION = 9004;

	/**
	 * 跳转回调请求码:是否需要刷新比赛数据
	 */
	public static final int REQUEST_CODE_NEED_TO_REFRESH = 9001;
	
	/**
	 * 跳转回调请求码:创建比赛
	 */
	public static final int REQUEST_CODE_CREATE_MATCH = 9008;
	
	
	/**
	 * 跳转回调请求码：选择球队
	 */
	public static final int REQUEST_CODE_TEAM = 9009;
	
	/**
	 * 跳转回调结果码：选择的城市
	 */
	public static final String RESULT_SELECT_CITY = "RESULT_SELECT_CITY";
	
	/**
	 * 跳转传递参数:区县选择
	 */
	public static final String REGION_SELECT = "REGION_SELECT";
	
	/**
	 * 跳转Action值:选择球队
	 */
	public static final String INTENT_ACTION_PICK_TEAM = "INTENT_ACTION_PICK_TEAM";
	
	/**
	 * 跳转传递参数:球队对象
	 */
	public static final String INTENT_TEAM_BEAN = "INTENT_TEAM_BEAN";
}
