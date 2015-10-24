package com.kinth.football.ui.team;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.bean.match.MatchRecords;
import com.kinth.football.config.MatchResultEnum;
import com.kinth.football.dao.Team;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.ui.match.MatchFriendly_Finished_ByTeam;
import com.kinth.football.ui.match.MatchLeague_Finished_ByTeam;
import com.kinth.football.util.ErrorCodeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
/**
 * 球队比赛战绩--友谊赛 and 锦标赛
 * @author zyq
 *
 */
public class TeamMatchRecordActivity extends BaseActivity {
	public static final String TEAM = "TEAM";
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;

	@ViewInject(R.id.nav_title)
	private TextView title;
	
	// 友谊比赛战绩 6种数字
	@ViewInject(R.id.friendly_match_won)
	private TextView friendly_match_won;

	@ViewInject(R.id.friendly_match_lost)
	private TextView friendly_match_lost;

	@ViewInject(R.id.friendly_match_drawn)
	private TextView friendly_match_drawn;

	@ViewInject(R.id.friendly_match_scored)
	private TextView friendly_match_scored;

	@ViewInject(R.id.friendly_match_against)
	private TextView friendly_match_against;

	@ViewInject(R.id.friendly_match_scored_jing)
	private TextView friendly_match_scored_jing;

	@ViewInject(R.id.no_last_five_friendly_result_tv)
	private TextView last_five_friendly_result_tv; // 没有最后5场友谊赛比赛结果的文本

	private ImageView[] friendly_match_results = new ImageView[5];// 最近5场友谊赛
		
	@ViewInject(R.id.friendly_match_result1)
	private ImageView friendly_match_result1;

	@ViewInject(R.id.friendly_match_result2)
	private ImageView friendly_match_result2;

	@ViewInject(R.id.friendly_match_result3)
	private ImageView friendly_match_result3;

	@ViewInject(R.id.friendly_match_result4)
	private ImageView friendly_match_result4;

	@ViewInject(R.id.friendly_match_result5)
	private ImageView friendly_match_result5;
		
	// 锦标比赛战绩 6种数字
	@ViewInject(R.id.tournament_match_won)
	private TextView tournament_match_won;

	@ViewInject(R.id.tournament_match_lost)
	private TextView tournament_match_lost;

	@ViewInject(R.id.tournament_match_drawn)
	private TextView tournament_match_drawn;

	@ViewInject(R.id.tournament_match_scored)
	private TextView tournament_match_scored;

	@ViewInject(R.id.tournament_match_against)
	private TextView tournament_match_against;

	@ViewInject(R.id.tournament_match_scored_jing)
	private TextView tournament_match_scored_jing;

	@ViewInject(R.id.no_last_five_tournament_result_tv)
	private TextView last_five_tournament_result_tv; // 没有最后5场锦标赛比赛结果的文本

	private ImageView[] tournament_match_results = new ImageView[5];// 最近5场锦标赛

	@ViewInject(R.id.tournament_match_result1)
	private ImageView tournament_match_result1;

	@ViewInject(R.id.tournament_match_result2)
	private ImageView tournament_match_result2;

	@ViewInject(R.id.tournament_match_result3)
	private ImageView tournament_match_result3;

	@ViewInject(R.id.tournament_match_result4)
	private ImageView tournament_match_result4;

	@ViewInject(R.id.tournament_match_result5)
	private ImageView tournament_match_result5;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v) {
		finish();
	}
	
	@OnClick(R.id.friendly_match_more)
	public void fun_9(View v) {// 查看更多友谊赛
		Intent intent = new Intent(this, MatchFriendly_Finished_ByTeam.class);
		intent.putExtra(MatchFriendly_Finished_ByTeam.INTENT_TEAM_UUID,
				team.getUuid());
		startActivity(intent);
	}

	@OnClick(R.id.tournament_match_more)
	public void fun_10(View v) {// 查看更多锦标赛
		Intent intent = new Intent(this, MatchLeague_Finished_ByTeam.class);
		intent.putExtra(MatchFriendly_Finished_ByTeam.INTENT_TEAM_UUID,
				team.getUuid());
		startActivity(intent);
	}

	private Team team;

	@ViewInject(R.id.viewGroup)
	private LinearLayout viewGroup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_team_match_record);
		ViewUtils.inject(this);
		
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();	
		ViewCompat.setBackground(viewGroup, new BitmapDrawable(getResources(),
				background));
		
		
		title.setText("比赛战绩");
		init_data();
	}

	private void init_data() {
		Intent intent = getIntent();
		team = intent.getParcelableExtra(TEAM);
		if (team == null) {
			return;
		}
		// 最近5场友谊比赛结果的图标
		friendly_match_results[0] = friendly_match_result1;
		friendly_match_results[1] = friendly_match_result2;
		friendly_match_results[2] = friendly_match_result3;
		friendly_match_results[3] = friendly_match_result4;
		friendly_match_results[4] = friendly_match_result5;
		
		tournament_match_results[0] = tournament_match_result1;
		tournament_match_results[1] = tournament_match_result2;
		tournament_match_results[2] = tournament_match_result3;
		tournament_match_results[3] = tournament_match_result4;
		tournament_match_results[4] = tournament_match_result5;
		
		getFriendlyMatchResultOfTeam(team);
		getTournamentMatchResultOfTeam(team);

	}

	/**
	 * 获取友谊赛最近5场的比赛战绩
	 */
	private void getFriendlyMatchResultOfTeam(Team team) {
		NetWorkManager.getInstance(mContext).getFriendlyMatchOfTeam(
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				team.getUuid(), new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						MatchRecords matchRecords = null;
						Gson gson = new Gson();
						matchRecords = gson.fromJson(response.toString(),
								new TypeToken<MatchRecords>() {
								}.getType());
						if (matchRecords == null) {
							return;
						}
						friendly_match_won.setText(String.valueOf(matchRecords
								.getRecord().getWon()));// 胜
						friendly_match_lost.setText(String.valueOf(matchRecords
								.getRecord().getLost()));// 负
						friendly_match_drawn.setText(String
								.valueOf(matchRecords.getRecord().getDrawn()));// 平
						friendly_match_scored.setText(String
								.valueOf(matchRecords.getRecord().getScored()));// 进球
						friendly_match_against.setText(String
								.valueOf(matchRecords.getRecord().getAgainst()));// 失球
						if (matchRecords.getRecord().getScored()
								- matchRecords.getRecord().getAgainst() > 0) {// 净胜球为正数
							// uncheck
							// TODO
							friendly_match_scored_jing
									.setText("净胜球   +"
											+ (matchRecords.getRecord()
													.getScored() - matchRecords
													.getRecord().getAgainst()));// 净胜球
						} else {
							friendly_match_scored_jing
									.setText("净胜球   "
											+ (matchRecords.getRecord()
													.getScored() - matchRecords
													.getRecord().getAgainst()));// 净胜球
						}
						if (matchRecords.getResults().size() == 0) {

						}
						// 获取最近最多5场总比赛的结果
						else {
							last_five_friendly_result_tv
									.setVisibility(View.GONE);
							for (int i = 0; i < matchRecords.getResults()
									.size(); i++) {
								switch (MatchResultEnum
										.getEnumFromString(matchRecords
												.getResults().get(i))) {
								case WIN:
									friendly_match_results[i]
											.setImageResource(R.drawable.win);
									break;
								case DRAW:
									friendly_match_results[i]
											.setImageResource(R.drawable.equal);
									break;
								case LOSS:
									friendly_match_results[i]
											.setImageResource(R.drawable.falie);
									break;
								case NULL:
									friendly_match_results[i]
											.setImageResource(0);// 显示空
									break;
								}
							}
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							// ShowToast("当前网络不可用");
						} else if (error.networkResponse == null) {
							// ShowToast("TeamInfoActivity-getFriendlyMatchResultOfTeam-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else if (error.networkResponse.statusCode == 404) {
							// ShowToast("球队找不到");
						}
					}
				});
	}

	/**
	 * 获取锦标赛赛最近5场的比赛战绩
	 */
	private void getTournamentMatchResultOfTeam(Team team) {
		NetWorkManager.getInstance(mContext).getTournamentMatchOfTeam(
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				team.getUuid(), new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						MatchRecords matchRecords = null;
						Gson gson = new Gson();
						matchRecords = gson.fromJson(response.toString(),
								new TypeToken<MatchRecords>() {
								}.getType());
						if (matchRecords == null) {
							return;
						}
						tournament_match_won.setText(String
								.valueOf(matchRecords.getRecord().getWon()));// 胜
						tournament_match_lost.setText(String
								.valueOf(matchRecords.getRecord().getLost()));// 负
						tournament_match_drawn.setText(String
								.valueOf(matchRecords.getRecord().getDrawn()));// 平
						tournament_match_scored.setText(String
								.valueOf(matchRecords.getRecord().getScored()));// 进球
						tournament_match_against.setText(String
								.valueOf(matchRecords.getRecord().getAgainst()));// 失球
						if (matchRecords.getRecord().getScored()
								- matchRecords.getRecord().getAgainst() > 0) {// 净胜球为正数
							// uncheck
							// TODO
							tournament_match_scored_jing
									.setText("净胜球   +"
											+ (matchRecords.getRecord()
													.getScored() - matchRecords
													.getRecord().getAgainst()));// 净胜球
						} else {
							tournament_match_scored_jing
									.setText("净胜球   "
											+ (matchRecords.getRecord()
													.getScored() - matchRecords
													.getRecord().getAgainst()));// 净胜球
						}
						if (matchRecords.getResults().size() == 0) {

						}
						// 获取最近最多5场总比赛的结果
						else {
							last_five_tournament_result_tv
									.setVisibility(View.GONE);
							for (int i = 0; i < matchRecords.getResults()
									.size(); i++) {
								switch (MatchResultEnum
										.getEnumFromString(matchRecords
												.getResults().get(i))) {
								case WIN:
									tournament_match_results[i]
											.setImageResource(R.drawable.win);
									break;
								case DRAW:
									tournament_match_results[i]
											.setImageResource(R.drawable.equal);
									break;
								case LOSS:
									tournament_match_results[i]
											.setImageResource(R.drawable.falie);
									break;
								case NULL:
									tournament_match_results[i]
											.setImageResource(0);// 显示空
									break;
								}
							}
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							// ShowToast("当前网络不可用");
						} else if (error.networkResponse == null) {
							// ShowToast("TeamInfoActivity-getFriendlyMatchResultOfTeam-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else if (error.networkResponse.statusCode == 404) {
							// ShowToast("球队找不到");
						}
					}
				});
	}

}
