package com.kinth.football.ui.team;

import com.kinth.football.bean.TeamRequest;
import com.kinth.football.ui.BaseActivity;

/**
 * 修改球队信息的基类
 * @author Sola
 *
 */
public abstract class ModifyTeamInfoBaseActivity extends BaseActivity{
	public static final String RESULT_MODIFY_TEAM_INFO = "RESULT_MODIFY_TEAM_INFO";//修改的返回字段
	public abstract void saveTeamInfo(TeamRequest teamRequest);
	
}
