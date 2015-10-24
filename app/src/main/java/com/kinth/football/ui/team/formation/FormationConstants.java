package com.kinth.football.ui.team.formation;

public interface FormationConstants {
	
	/**
	 * Intent参数键名:比赛选择阵容
	 */
	public static final String RESULT_MATCH_FORMATION = "result_match_formation";

	/**
	 * Intent参数键名:是否具有队长权限
	 */
	public static final String INTENT_IS_CAPTAIN = "intent_is_captain";
	
	/**
	 * Intent参数键名:是否用来选择阵容，还是仅仅浏览
	 */
	public static final String INTENT_IS_FOR_SELECT = "intent_is_for_select";
	
	/**
	 * Intent参数键名:阵容归属的球队的uuid
	 */
	public static final String INTENT_TEAM_UUID = "intent_team_uuid";
	
	/**
	 * Intent参数键名:阵容实体类
	 */
	public static final String INTENT_FORMATION_BEAN = "intent_formation_bean";
	
	/**
	 * Intent参数键名:阵容详情截图的本地路径
	 */
	public static final String INTENT_FORMATION_DETAIL_PATH = "intent_formation_detail_path";
	
	/**
	 * 选择阵容请求码(当前用户在客队)
	 */
	public static final int INTENT_CREATE_MATCH_FORMATION = 9005;

}
