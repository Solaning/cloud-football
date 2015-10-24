package com.kinth.football.ui.match;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.adapter.TeamMemberHorizontalListAdapter;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.bean.match.PlayerInTeam;
import com.kinth.football.dao.Team;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.ui.team.TeamInfoActivity;
import com.kinth.football.ui.team.TeamInfoForGuestActivity;
import com.kinth.football.ui.team.TeamPlayerInfo;
import com.kinth.football.ui.team.formation.Formation;
import com.kinth.football.ui.team.formation.FormationConstants;
import com.kinth.football.ui.team.formation.FormationDetailActivity;
import com.kinth.football.ui.team.formation.FormationListActivity;
import com.kinth.football.util.DBUtil;
import com.kinth.football.util.DateUtil;
import com.kinth.football.util.DialogUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.ImageLoadOptions;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.view.HorizontalListView;
import com.kinth.football.view.RoundImageView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 待开赛的比赛详情页面
 * 
 * @author Sola
 * 
 */
@ContentView(R.layout.activity_match_detail_on_pending)
public class MatchDetailOnPendingStateActivity extends BaseActivity {
	public static final String INTENT_MATCH_DETAIL_BEAN = "INTENT_MATCH_DETAIL_BEAN";// intent中传递的比赛数据信息



	public static final int INTENT_SELECT_MATCH_JERSEY_HOME_TEAM = 9001; // 选择队服请求码(当前用户在主队)
	public static final int INTENT_SELECT_MATCH_JERSEY_AWAY_TEAM = 9002; // 选择队服请求码(当前用户在客队)
	public static final int INTENT_SELECT_MATCH_FORMATION_HOME_TEAM = 9003; // 选择阵容请求码(当前用户在主队)
	public static final int INTENT_SELECT_MATCH_FORMATION_AWAY_TEAM = 9004; // 选择阵容请求码(当前用户在客队)
	private static final int REQUEST_CODE_RECEIPT_HOMETEAM_INFO = 9005;   //查看主队球队信息请求码
	private static final int REQUEST_CODE_RECEIPT_AWAYTEAM_INFO = 9006;   //查看客队球队信息请求码

	private MatchInfo matchInfo;
	private int userType;// 1-在主队，2-在客队，3-裁判
	private boolean isCaptain;// 是否队长
	private TeamMemberHorizontalListAdapter adapter;// 球队成员的横向adapter

	@ViewInject(R.id.entire_layout)
	private View entireLayout;
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;

	@ViewInject(R.id.nav_title)
	private TextView title;

	@ViewInject(R.id.tv_home_team_name)
	private TextView homeTeamName;// 主队名称

	@ViewInject(R.id.iv_home_team_badge)
	private RoundImageView homeTeamBadge;// 主队队徽

	@ViewInject(R.id.tv_home_team_score)
	private TextView homeTeamScore;// 主队比分

	@ViewInject(R.id.tv_away_team_name)
	private TextView awayTeamName;// 客队名称

	@ViewInject(R.id.iv_away_team_badge)
	private RoundImageView awayTeamBadge;// 客队队徽

	@ViewInject(R.id.tv_away_team_score)
	private TextView awayTeamScore;// 客队比分

	@ViewInject(R.id.tv_match_description)
	private TextView matchDescription;

	@ViewInject(R.id.tv_match_field)
	private TextView matchField;// 比赛场地

	@ViewInject(R.id.tv_match_date)
	private TextView matchDate;// 比赛时间

	@ViewInject(R.id.tv_match_player_count)
	private TextView matchPlayerCount;

	@ViewInject(R.id.tv_match_cost)
	private TextView matchCost;// 费用

	@ViewInject(R.id.teamInfo_memberlist)
	private HorizontalListView teamMemberHorizontalListView;// 球队成员列表

	@ViewInject(R.id.llt_homejersey)
	private LinearLayout llt_homejersey;  //本方队服的背景框
	
	@ViewInject(R.id.llt_otherjersey)
	private LinearLayout llt_otherjersey;  //对方队服的背景框
	
	@ViewInject(R.id.self_jersey)
	private RoundImageView self_jersey; // 本方队服

	@ViewInject(R.id.setting_jersey_one)
	private Button setting_jersey_one;// 设置队服

	@ViewInject(R.id.hadNotsetjersey_one)
	private TextView hadNotsetjersey_one;// 队长未设置队服

	@ViewInject(R.id.other_jersey)
	private RoundImageView other_jersey;// 对方队服

	@ViewInject(R.id.hadNotsetjersey_two)
	private TextView hadNotsetjersey_two;// 队长未设置队服
	
	@ViewInject(R.id.tv_home_team_formation_title)
	private TextView homeTeamFormationTitle;//主队阵容title--默认为主队阵容
	
	@ViewInject(R.id.rtl_home_team_formation_layout)
	private RelativeLayout homeTeamFormationLayout;//主队阵容layout
	
	@ViewInject(R.id.tv_home_team_set_formation)
	private TextView homeTeamSetFormation;// 设置阵容（查看阵容）
	
	@ViewInject(R.id.tv_home_team_formation_notice)
	private TextView homeTeamFormationNotice;//主队队长未设置阵容
	
	@ViewInject(R.id.tv_away_team_formation_title)
	private TextView awayTeamFormationTitle;//客队阵容title
	
	@ViewInject(R.id.rtl_away_team_formation_layout)
	private RelativeLayout awayTeamFormationLayout;//客队阵容layou
	
	@ViewInject(R.id.tv_away_team_set_formation)
	private TextView awayTeamSetFormation;//客队设置阵容
	
	@ViewInject(R.id.tv_away_team_formation_notice)
	private TextView awayTeamFormationNotice;//客队队长未设置阵容

	@ViewInject(R.id.tv_referee_name)
	private TextView refereeName;// 裁判名称

	@ViewInject(R.id.refree_avatar)
	private RoundImageView refreeAvatar;// 裁判
	
	@ViewInject(R.id.iv_home_team_formation_bg)
	private ImageView homeTeamFormationBg;//主队阵容图片
	
	@ViewInject(R.id.iv_away_team_formation_bg)
	private ImageView awayTeamFormationBg;//主队阵容图片

	@OnClick(R.id.nav_left)
	public void fun_1(View v) {
		finish();
	}
	
	@OnClick(R.id.refree_avatar)
	public void fun_2(View v){
		if(matchInfo.getReferee() != null && !TextUtils.isEmpty(matchInfo.getReferee().getUuid())){
			Intent intent = new Intent(mContext, TeamPlayerInfo.class);
			intent.putExtra(TeamPlayerInfo.PLAYER_UUID, matchInfo.getReferee().getUuid());
			startActivity(intent);
		}
	}

	private String currentUserID = null;
	private ProgressDialog progressDia;

	private Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// 刷新数据
			{
				progressDia = ProgressDialog.show(mContext, "提示", "请稍后...",
						false, false);
				NetWorkManager.getInstance(mContext).getMatchInfo(
						UserManager.getInstance(mContext).getCurrentUser()
								.getToken(), matchInfo.getUuid(),
						new Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								DialogUtil.dialogDismiss(progressDia);
								Gson gson = new Gson();
								MatchInfo match = null;
								match = gson.fromJson(response.toString(),
										new TypeToken<MatchInfo>() {
										}.getType());
								if (match != null) {
									for (int i = 0; i < CustomApplcation
											.getMatchInfoOfPending().size(); i++) {
										if (CustomApplcation
												.getMatchInfoOfPending().get(i)
												.getUuid()
												.equals(match.getUuid())) {
											CustomApplcation
													.getMatchInfoOfPending()
													.set(i, match);
											matchInfo = match;
										}
									}
								}
							}
						}, new ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								DialogUtil.dialogDismiss(progressDia);
								ShowToast("网络错误");
							}
						});
			}
			return false;
		}
	});

	//点击主队队徽
	@OnClick(R.id.iv_home_team_badge)  
	public void fun_6(View v){  
		Intent intent = null;
		if(DBUtil.isGuest(mContext, matchInfo.getHomeTeam())){  //来宾
			intent = new Intent(mContext, TeamInfoForGuestActivity.class);
			intent.putExtra(TeamInfoForGuestActivity.INTENT_TEAM_INFO_BEAN, matchInfo.getHomeTeam());
			intent.putExtra(TeamInfoForGuestActivity.INTENT_NEET_TO_RETURN_BEAN, true);
			intent.putExtra(TeamInfoForGuestActivity.INTENT_NEED_TO_GET_CYBER_NEW, true);
			startActivityForResult(intent,REQUEST_CODE_RECEIPT_HOMETEAM_INFO);
		}else{//成员
			intent = new Intent(mContext, TeamInfoActivity.class);
			intent.putExtra(TeamInfoActivity.INTENT_TEAM_INFO_BEAN, matchInfo.getHomeTeam());
			intent.putExtra(TeamInfoActivity.INTENT_NEET_TO_RETURN_BEAN, true);
			intent.putExtra(TeamInfoActivity.INTENT_NEED_TO_GET_CYBER_NEW, true);
			startActivityForResult(intent,REQUEST_CODE_RECEIPT_HOMETEAM_INFO);
		}
	}
	
	//点击客队队徽
	@OnClick(R.id.iv_away_team_badge)  
	public void fun_7(View v){
		Intent intent = null;
		if(DBUtil.isGuest(mContext, matchInfo.getAwayTeam())){  //来宾
			intent = new Intent(mContext, TeamInfoForGuestActivity.class);
			intent.putExtra(TeamInfoForGuestActivity.INTENT_TEAM_INFO_BEAN, matchInfo.getAwayTeam());
			intent.putExtra(TeamInfoForGuestActivity.INTENT_NEET_TO_RETURN_BEAN, true);
			intent.putExtra(TeamInfoForGuestActivity.INTENT_NEED_TO_GET_CYBER_NEW, true);
			startActivityForResult(intent,REQUEST_CODE_RECEIPT_AWAYTEAM_INFO);
		}else{//成员
			intent = new Intent(mContext, TeamInfoActivity.class);
			intent.putExtra(TeamInfoActivity.INTENT_TEAM_INFO_BEAN, matchInfo.getAwayTeam());
			intent.putExtra(TeamInfoActivity.INTENT_NEET_TO_RETURN_BEAN, true);
			intent.putExtra(TeamInfoActivity.INTENT_NEED_TO_GET_CYBER_NEW, true);
			startActivityForResult(intent,REQUEST_CODE_RECEIPT_AWAYTEAM_INFO);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(entireLayout, new BitmapDrawable(getResources(),
				background));

		title.setText("比赛详情");
		matchInfo = getIntent().getParcelableExtra(INTENT_MATCH_DETAIL_BEAN);
		if (matchInfo == null) {
			return;
		}
		currentUserID = UserManager.getInstance(mContext).getCurrentUser()
				.getPlayer().getUuid();
		userType = getUserType();
		switch (userType) {
		case 1:
			isCaptain = isHomeCaptain();
			init_scrollview(false);
			break;
		case 2:
			isCaptain = isAwayCaptain();
			init_scrollview(false);
			break;
		case 3:
			init_scrollview(true);
			break;
		}
		initDate();
		// 队服的显示
		showJersey();
		// 阵容的显示
		showFormation();
	}

	private boolean isHomeCaptain() {
		if (currentUserID.equals(matchInfo.getHomeTeam().getFirstCaptainUuid())
				|| currentUserID.equals(matchInfo.getHomeTeam()
						.getSecondCaptainUuid())
				|| currentUserID.equals(matchInfo.getHomeTeam()
						.getThirdCaptainUuid())) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isAwayCaptain() {
		if (currentUserID.equals(matchInfo.getAwayTeam().getFirstCaptainUuid())
				|| currentUserID.equals(matchInfo.getAwayTeam()
						.getSecondCaptainUuid())
				|| currentUserID.equals(matchInfo.getAwayTeam()
						.getThirdCaptainUuid())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 报名的队员
	 */
	private void init_scrollview(boolean isReferee) {
		adapter = new TeamMemberHorizontalListAdapter(mContext, null);
		teamMemberHorizontalListView.setAdapter(adapter);
		teamMemberHorizontalListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						PlayerInTeam playerInTeam = adapter.getItem(position);// ??为啥？？
																				// TODO
																				// 不是应该-1吗
						// 个人信息
						Intent intent = new Intent(mContext,
								TeamPlayerInfo.class);
						intent.putExtra(TeamPlayerInfo.PLAYER_UUID,
								playerInTeam.getPlayer().getUuid());
						startActivity(intent);
					}
				});
		if (isReferee) {
			List<PlayerInTeam> teamPlayerList = new ArrayList<PlayerInTeam>();
			teamPlayerList.addAll(matchInfo.getHomeTeamPlayers());
			teamPlayerList.addAll(matchInfo.getAwayTeamPlayers());
			adapter.setPlayerList(teamPlayerList);
		} else {
			if (userType == 1) {
				adapter.setPlayerList(matchInfo.getHomeTeamPlayers());
			} else {
				adapter.setPlayerList(matchInfo.getAwayTeamPlayers());
			}
		}
	}

	/**
	 * 获取用户类型
	 * 
	 * @return
	 */
	private int getUserType() {
		for (PlayerInTeam homeItem : matchInfo.getHomeTeamPlayers()) {
			if (currentUserID.equals(homeItem.getPlayer().getUuid())) { // 说明我在主队，主队是本方
				return 1;
			}
		}
		for (PlayerInTeam awayItem : matchInfo.getAwayTeamPlayers()) {
			if (currentUserID.equals(awayItem.getPlayer().getUuid())) { // 说明我在主队，主队是本方
				return 2;
			}
		}
		return 3;
	}

	private void initDate() {
		homeTeamName.setText(matchInfo.getHomeTeam().getName());
		awayTeamName.setText(matchInfo.getAwayTeam().getName());
		refereeName.setText(matchInfo.getReferee().getName());

		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(matchInfo.getHomeTeam().getBadge()),
				homeTeamBadge, new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.team_bage_default)
						.showImageForEmptyUri(R.drawable.team_bage_default)
						.showImageOnFail(R.drawable.team_bage_default).build());
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(matchInfo.getAwayTeam().getBadge()),
				awayTeamBadge, new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.team_bage_default)
						.showImageForEmptyUri(R.drawable.team_bage_default)
						.showImageOnFail(R.drawable.team_bage_default).build());
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(matchInfo.getReferee().getPicture()),
				refreeAvatar, new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.icon_default_head)
						.showImageForEmptyUri(R.drawable.icon_default_head)
						.showImageOnFail(R.drawable.icon_default_head).build());

		matchDescription.setText(matchInfo.getName());
		matchField.setText(matchInfo.getField());
		matchDate
				.setText(StringUtils.defaultIfEmpty(DateUtil
						.parseTimeInMillis_hadweek(matchInfo.getKickOff()), ""));// 比赛时间
		matchPlayerCount.setText(matchInfo.getPlayerCount() + "人");
		if(matchInfo.getCost() == 0){
			matchCost.setText("— —");
		}else{
			matchCost.setText((int) matchInfo.getCost() + "元");
		}
	}
//补充了部分显示队服大图 --5.12 zyq    队长可以修改队服  5.15
	private void showJersey() {
		switch (userType) {
		case 1:
			if (!TextUtils.isEmpty(matchInfo.getHomeTeamJersey())) {
				llt_homejersey.setVisibility(View.VISIBLE);
				self_jersey.setVisibility(View.VISIBLE);
				setting_jersey_one.setVisibility(View.GONE);
				hadNotsetjersey_one.setVisibility(View.GONE);
				PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(matchInfo
						.getHomeTeamJersey()), self_jersey);
				if (isCaptain) {//已经有队服，队长可修改队服
					self_jersey.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext,
									JerseyofTeamActivity.class);
							intent.putExtra(JerseyofTeamActivity.INTENT_MATCH_TEAM,
									matchInfo.getHomeTeam());
							startActivityForResult(intent,
									INTENT_SELECT_MATCH_JERSEY_HOME_TEAM);// 当前用户在主队
						}
					});
				}else {
				self_jersey.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {// 已经有队服，其它人查看队服大图
						ArrayList<String> photos = new ArrayList<String>();
						photos.add(PhotoUtil.getAllPhotoPath(matchInfo
								.getHomeTeamJersey()));
						PictureUtil.viewLargerImage(mContext, photos);
					}
				});
				}
			} else {// 没有队服
				if (isCaptain) {
					llt_homejersey.setVisibility(View.GONE);
					self_jersey.setVisibility(View.GONE);
					
					setting_jersey_one.setVisibility(View.VISIBLE);
					hadNotsetjersey_one.setVisibility(View.GONE);
					setting_jersey_one
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent intent = new Intent(mContext,
											JerseyofTeamActivity.class);
									intent.putExtra(JerseyofTeamActivity.INTENT_MATCH_TEAM,
											matchInfo.getHomeTeam());
									startActivityForResult(intent,
											INTENT_SELECT_MATCH_JERSEY_HOME_TEAM);// 当前用户在主队
								}
							});
				} else {
					llt_homejersey.setVisibility(View.GONE);
					self_jersey.setVisibility(View.GONE);
					setting_jersey_one.setVisibility(View.GONE);
					hadNotsetjersey_one.setVisibility(View.VISIBLE);
				}
			}
			// 将客队队服显示在对方中(无需考虑其他情况)
			if (!TextUtils.isEmpty(matchInfo.getAwayTeamJersey())) {
				llt_otherjersey.setVisibility(View.VISIBLE);
				other_jersey.setVisibility(View.VISIBLE);
				hadNotsetjersey_two.setVisibility(View.GONE);
				PictureUtil.getMd5PathByUrl(PhotoUtil
						.getThumbnailPath(matchInfo.getAwayTeamJersey()),
						other_jersey);
				
				other_jersey.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {// 已经有队服，都是查看队服
						ArrayList<String> photos = new ArrayList<String>();
						photos.add(PhotoUtil.getAllPhotoPath(matchInfo
								.getAwayTeamJersey()));
						PictureUtil.viewLargerImage(mContext, photos);
					}
				});
			} else {
				llt_otherjersey.setVisibility(View.GONE);
				other_jersey.setVisibility(View.GONE);
				hadNotsetjersey_two.setVisibility(View.VISIBLE);
			}
			break;
		case 2:// userType == 2 在客队
			if (!TextUtils.isEmpty(matchInfo.getAwayTeamJersey())) {
				PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(matchInfo
						.getAwayTeamJersey()), self_jersey);
				llt_homejersey.setVisibility(View.VISIBLE);
				self_jersey.setVisibility(View.VISIBLE);
				setting_jersey_one.setVisibility(View.GONE);
				hadNotsetjersey_one.setVisibility(View.GONE);
				if (isCaptain) { //已经有队服，队长可设置队服
					self_jersey.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext,
									JerseyofTeamActivity.class);
							intent.putExtra(JerseyofTeamActivity.INTENT_MATCH_TEAM,
									matchInfo.getAwayTeam());
							startActivityForResult(intent,
									INTENT_SELECT_MATCH_JERSEY_AWAY_TEAM);// 当前用户在客队
						}
					});
				}
				else{//已经有队服，其它人可以查看队服
				self_jersey.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ArrayList<String> photos = new ArrayList<String>();
						photos.add(PhotoUtil.getAllPhotoPath(matchInfo
								.getAwayTeamJersey()));
						PictureUtil.viewLargerImage(mContext, photos);
					}
				});
				}
			} else {// 在客队没有队服
				if (isCaptain) {
					llt_homejersey.setVisibility(View.GONE);
					self_jersey.setVisibility(View.GONE);
					setting_jersey_one.setVisibility(View.VISIBLE);
					hadNotsetjersey_one.setVisibility(View.GONE);
					setting_jersey_one
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent intent = new Intent(mContext,
											JerseyofTeamActivity.class);
									intent.putExtra(JerseyofTeamActivity.INTENT_MATCH_TEAM,
											matchInfo.getAwayTeam());
									startActivityForResult(intent,
											INTENT_SELECT_MATCH_JERSEY_AWAY_TEAM);// 当前用户在客队
								}
							});
				} else {
					self_jersey.setVisibility(View.GONE);
					llt_homejersey.setVisibility(View.GONE);
					setting_jersey_one.setVisibility(View.GONE);
					hadNotsetjersey_one.setVisibility(View.VISIBLE);
				}
			}
			// 将主队队服显示在对方(无需考虑其他情况)
			if (!TextUtils.isEmpty(matchInfo.getHomeTeamJersey())) {
				llt_otherjersey.setVisibility(View.VISIBLE);
				other_jersey.setVisibility(View.VISIBLE);
				hadNotsetjersey_two.setVisibility(View.GONE);
				PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(matchInfo
						.getHomeTeamJersey()), other_jersey);
				
				other_jersey.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {// 已经有队服，都是查看队服
						ArrayList<String> photos = new ArrayList<String>();
						photos.add(PhotoUtil.getAllPhotoPath(matchInfo
								.getHomeTeamJersey()));
						PictureUtil.viewLargerImage(mContext, photos);
					}
				});
				
			} else {
				llt_otherjersey.setVisibility(View.GONE);
				other_jersey.setVisibility(View.GONE);
				hadNotsetjersey_two.setVisibility(View.VISIBLE);
			}
			break;
		case 3://裁判视角
			if (!TextUtils.isEmpty(matchInfo.getHomeTeamJersey())) {//主队
				llt_homejersey.setVisibility(View.VISIBLE);
				self_jersey.setVisibility(View.VISIBLE);
				setting_jersey_one.setVisibility(View.GONE);
				hadNotsetjersey_one.setVisibility(View.GONE);
				PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(matchInfo
						.getHomeTeamJersey()), self_jersey);
				self_jersey.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {// 已经有队服，都是查看队服
						ArrayList<String> photos = new ArrayList<String>();
						photos.add(PhotoUtil.getAllPhotoPath(matchInfo
								.getHomeTeamJersey()));
						PictureUtil.viewLargerImage(mContext, photos);
					}
				});
			} else {// 没有队服
				llt_homejersey.setVisibility(View.GONE);
				self_jersey.setVisibility(View.GONE);
				setting_jersey_one.setVisibility(View.GONE);
				hadNotsetjersey_one.setVisibility(View.VISIBLE);
			}
			//客队情况
			if (!TextUtils.isEmpty(matchInfo.getAwayTeamJersey())) {
				PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(matchInfo
						.getAwayTeamJersey()), other_jersey);
				llt_otherjersey.setVisibility(View.VISIBLE);
				other_jersey.setVisibility(View.VISIBLE);
				hadNotsetjersey_two.setVisibility(View.GONE);
				other_jersey.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ArrayList<String> photos = new ArrayList<String>();
						photos.add(PhotoUtil.getAllPhotoPath(matchInfo
								.getAwayTeamJersey()));
						PictureUtil.viewLargerImage(mContext, photos);
					}
				});
			} else {// 在客队没有队服
				llt_otherjersey.setVisibility(View.GONE);
				other_jersey.setVisibility(View.GONE);
				hadNotsetjersey_two.setVisibility(View.VISIBLE);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == INTENT_SELECT_MATCH_JERSEY_HOME_TEAM) { // 选择球服(当前用户在本方)
			String teamJersey = intent.getExtras().getString(
					JerseyofTeamActivity.MATCH_JERSEY);

			if (TextUtils.isEmpty(teamJersey)) {
				if (!TextUtils.isEmpty(matchInfo.getHomeTeamJersey())) {
					ShowToast("修改队服失败，请重新修改");
				}else {
					ShowToast("设置队服失败，请重新设置");
				}
				return;
			}
			// 访问后台服务器，修改队服
			executeSetTeamJersey(teamJersey, matchInfo.getHomeTeam().getUuid());
			return;
		}
		if (requestCode == INTENT_SELECT_MATCH_JERSEY_AWAY_TEAM) {// 设置比赛球服(当前用户在客队)
			String teamJersey = intent.getExtras().getString(
					JerseyofTeamActivity.MATCH_JERSEY);

			if (TextUtils.isEmpty(teamJersey)) {
				if (!TextUtils.isEmpty(matchInfo.getAwayTeamJersey())) {
					ShowToast("修改队服失败，请重新修改");
				}else {
					ShowToast("设置队服失败，请重新设置");
				}
				return;
			}
			// 访问后台服务器，修改队服
			executeSetTeamJersey(teamJersey, matchInfo.getAwayTeam().getUuid());
			return;
		}
		if (requestCode == INTENT_SELECT_MATCH_FORMATION_HOME_TEAM) { // 选择阵容（当前用户在主队）
			Formation formation = intent
					.getParcelableExtra(FormationListActivity.RESULT_MATCH_FORMATION);
			if (formation == null) {
				if(matchInfo.getHomeTeamFormation()!= null){
					ShowToast("修改阵容失败");
				}else{
					ShowToast("设置阵容失败");
				}
//				ShowToast("设置阵容失败");
				return;
			}
			// 访问后台服务器，修改阵容
			executeSetTeamFormation(matchInfo.getHomeTeam().getUuid(),
					formation);
			return;
		}
		if (requestCode == INTENT_SELECT_MATCH_FORMATION_AWAY_TEAM) { // 选择阵容（当前用户在客队）
			Formation formation = intent
					.getParcelableExtra(FormationListActivity.RESULT_MATCH_FORMATION);
			if (formation == null) {
					if(matchInfo.getAwayTeamFormation() != null){
				ShowToast("修改阵容失败");
			}else{
				ShowToast("设置阵容失败");
			}
//			ShowToast("设置阵容失败");
			return;
			}
			executeSetTeamFormation(matchInfo.getAwayTeam().getUuid(),
					formation);
			return;
		}
		if(requestCode == REQUEST_CODE_RECEIPT_HOMETEAM_INFO){
			Team receiptTeam = intent.getParcelableExtra(TeamInfoForGuestActivity.INTENT_TEAM_INFO_BEAN);
			if(receiptTeam == null){
				return;
			}
			matchInfo.setHomeTeam(receiptTeam);  //替换之前的HomeTeam
			return;
		}
		if(requestCode == REQUEST_CODE_RECEIPT_AWAYTEAM_INFO){
			Team receiptTeam = intent.getParcelableExtra(TeamInfoForGuestActivity.INTENT_TEAM_INFO_BEAN);
			if(receiptTeam == null){
				return;
			}
			matchInfo.setAwayTeam(receiptTeam);  //替换之前的AwayTeam
			return;    
		}
	}

	ProgressDialog dialog = null;

	private void executeSetTeamJersey(final String teamJersey, String teamUUid) {
		dialog = ProgressDialog.show(mContext, "提示", "请稍后", false, false);
		NetWorkManager.getInstance(mContext).setTeamJerseyForMatch(
				matchInfo.getUuid(), teamUUid, teamJersey,
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						DialogUtil.dialogDismiss(dialog);
//						ShowToast("队服设置成功");// 以后只能查看队服 TODO 更新数据
						switch (userType) {
						case 1:
							if (!TextUtils.isEmpty(matchInfo.getHomeTeamJersey())) {
								ShowToast("队服修改成功");
							}else {
								ShowToast("队服设置成功");
							}
							matchInfo.setHomeTeamJersey(teamJersey);
							break;
						case 2:
							if (!TextUtils.isEmpty(matchInfo.getAwayTeamJersey())) {
								ShowToast("队服修改成功");
							}else {
								ShowToast("队服设置成功");
							}
							matchInfo.setAwayTeamJersey(teamJersey);
							break;
						default:
							break;
						}
						llt_homejersey.setVisibility(View.VISIBLE);
						self_jersey.setVisibility(View.VISIBLE);
						setting_jersey_one.setVisibility(View.GONE);
						hadNotsetjersey_one.setVisibility(View.GONE);
						PictureUtil.getMd5PathByUrl(
								PhotoUtil.getThumbnailPath(teamJersey),
								self_jersey);
//						self_jersey
//								.setOnClickListener(new View.OnClickListener() {
//									@Override
//									public void onClick(View v) {
//										ArrayList<String> photos = new ArrayList<String>();
//										switch (userType) {
//										case 1:
//											if (TextUtils.isEmpty(matchInfo
//													.getHomeTeamJersey())) {
//												return;
//											}
//											photos.add(PhotoUtil.getAllPhotoPath(matchInfo
//													.getHomeTeamJersey()));
//											break;
//										case 2:
//											if (TextUtils.isEmpty(matchInfo
//													.getAwayTeamJersey())) {
//												return;
//											}
//											photos.add(PhotoUtil.getAllPhotoPath(matchInfo
//													.getAwayTeamJersey()));
//											break;
//										default:
//											return;
//										}
//										PictureUtil.viewLargerImage(mContext,
//												photos);
//									}
//								});
						// 获取当前比赛的数据并替换当前的数据
						handler.sendEmptyMessage(1);
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						DialogUtil.dialogDismiss(dialog);
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("当前网络不可用");
							return;
						}
						if (error.networkResponse == null) {
//							 ShowToast("MatchDetailOnPendingStateActivity-executeSetTeamJersey-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else if (error.networkResponse.statusCode == 403) {
//							ShowToast("您不是球队队长");
						} else if (error.networkResponse.statusCode == 404) {
//							ShowToast("比赛找不到|球队找不到");
						} else if (error.networkResponse.statusCode == 409) {
//							ShowToast("球队没有参加这场比赛");
						}
					}
				});
	}

	/**
	 * 提交比赛阵容到服务器
	 * 
	 * @param teamUUid
	 * @param teamFormationUUid
	 */
	private void executeSetTeamFormation(String teamUUid,
			final Formation formation) {
		dialog = ProgressDialog.show(mContext, "提示", "请稍后", false, false);
		NetWorkManager.getInstance(mContext).setTeamFormaForMatch(
				matchInfo.getUuid(), teamUUid, formation.getUuid(),
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						DialogUtil.dialogDismiss(dialog);
//						ShowToast("阵容设置成功");
						switch (userType) {
						case 1:
							if(matchInfo.getHomeTeamFormation()!= null){
								ShowToast("修改阵容成功");
							}else{
								ShowToast("设置阵容成功");
							}
							matchInfo.setHomeTeamFormation(formation);
							// 都变成了查看阵容
							homeTeamSetFormation.setVisibility(View.VISIBLE);
							homeTeamFormationNotice.setVisibility(View.GONE);
							homeTeamSetFormation.setText("设置阵容");
//							homeTeamSetFormation
//									.setOnClickListener(new View.OnClickListener() {
//										@Override
//										public void onClick(View v) {
//											Formation formation = matchInfo
//														.getHomeTeamFormation();
//											if (formation == null)
//												return;
//											Intent intent = new Intent(mContext,
//													FormationDetailActivity.class);
//											intent.putExtra(
//													FormationConstants.INTENT_FORMATION_BEAN,
//													formation);
//											startActivity(intent);
//										}
//									});
							break;
						case 2:
							if(matchInfo.getAwayTeamFormation()!= null){
								ShowToast("修改阵容成功");
							}else{
								ShowToast("设置阵容成功");
							}
							matchInfo.setAwayTeamFormation(formation);
							// 都变成了查看阵容
							awayTeamSetFormation.setVisibility(View.VISIBLE);
							awayTeamFormationNotice.setVisibility(View.GONE);
							awayTeamSetFormation.setText("已设置阵容");
//							awayTeamSetFormation
//									.setOnClickListener(new View.OnClickListener() {
//										@Override
//										public void onClick(View v) {
//											Formation formation  = matchInfo
//														.getAwayTeamFormation();
//											if (formation == null)
//												return;
//											Intent intent = new Intent(mContext,
//													FormationDetailActivity.class);
//											intent.putExtra(
//													FormationConstants.INTENT_FORMATION_BEAN,
//													formation);
//											startActivity(intent);
//										}
//									});
							break;
						default:
							break;
						}
						// 获取当前比赛的数据并替换当前的数据
						handler.sendEmptyMessage(1);
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						DialogUtil.dialogDismiss(dialog);
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("当前网络不可用");
							return;
						}
						if (error.networkResponse == null) {
//							 ShowToast("MatchDetailOnPendingStateActivity-executeSetTeamFormation-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else if (error.networkResponse.statusCode == 403) {
//							ShowToast("您不是球队队长");
						} else if (error.networkResponse.statusCode == 404) {
//							ShowToast("比赛找不到|球队找不到");
						} else if (error.networkResponse.statusCode == 409) {
//							ShowToast("球队没有参加这场比赛");
						}
					}
				});
	}

	/**有阵容队长 查看阵容--》修改阵容  --5.15 zyq
	 * 阵容
	 */
	private void showFormation() {
		switch (userType) {
		case 1:// 主队
			LayoutParams params = new RelativeLayout.LayoutParams(CustomApplcation.getInstance().getParameters().getmScreenWidth(),
					Math.round(CustomApplcation.getInstance().getParameters().getmScreenWidth() / 2.75348f));//图片的宽高比例
			homeTeamFormationBg.setLayoutParams(params);
			ImageLoader.getInstance().displayImage("drawable://" + R.drawable.formation_bg, homeTeamFormationBg, 
					ImageLoadOptions.getDisplayDrawableBuilder().build());
			
			homeTeamFormationTitle.setVisibility(View.VISIBLE);
			homeTeamFormationTitle.setText("阵容");
			homeTeamFormationLayout.setVisibility(View.VISIBLE);
			if (matchInfo.getHomeTeamFormation() != null) {// 有阵容
				if (isCaptain) {// 是队长
					homeTeamSetFormation.setText("设置阵容");
					homeTeamFormationNotice.setVisibility(View.GONE);
//					homeTeamSetFormation
//							.setOnClickListener(new View.OnClickListener() {// 只能查看阵容
//								@Override
//								public void onClick(View v) {
//									Intent intent = new Intent(mContext,
//											FormationDetailActivity.class);
//									intent.putExtra(
//											FormationConstants.INTENT_FORMATION_BEAN,
//											matchInfo.getHomeTeamFormation());
//									startActivity(intent);
//								}
//							});
					homeTeamSetFormation
					.setOnClickListener(new View.OnClickListener() {//有阵容 修改
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext,
									FormationListActivity.class);
							intent.putExtra(
									FormationListActivity.INTENT_IS_CAPTAIN,
									true);
							intent.putExtra(
									FormationListActivity.INTENT_TEAM_UUID,
									matchInfo.getHomeTeam().getUuid());
							intent.putExtra(
									FormationListActivity.INTENT_IS_FOR_SELECT,
									true);
							startActivityForResult(intent,
									INTENT_SELECT_MATCH_FORMATION_HOME_TEAM);
						}
					});
				} else {// 不是队长
					// 普通队员
					homeTeamSetFormation.setText("查看阵容");
					homeTeamFormationNotice.setVisibility(View.GONE);
					homeTeamSetFormation
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent intent = new Intent(mContext,
											FormationDetailActivity.class);
									intent.putExtra(
											FormationConstants.INTENT_FORMATION_BEAN,
											matchInfo.getHomeTeamFormation());
									startActivity(intent);
								}
							});
				}
			} else {// 没有阵容
				if (isCaptain) {// 是队长
					// 当前用户是队长
					homeTeamSetFormation.setText("设置阵容");
					homeTeamFormationNotice.setVisibility(View.GONE);
					homeTeamSetFormation
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent intent = new Intent(mContext,
											FormationListActivity.class);
									intent.putExtra(
											FormationListActivity.INTENT_IS_CAPTAIN,
											true);
									intent.putExtra(
											FormationListActivity.INTENT_TEAM_UUID,
											matchInfo.getHomeTeam().getUuid());
									intent.putExtra(
											FormationListActivity.INTENT_IS_FOR_SELECT,
											true);
									startActivityForResult(intent,
											INTENT_SELECT_MATCH_FORMATION_HOME_TEAM);
								}
							});
				} else {// 不是队长
					homeTeamSetFormation.setVisibility(View.GONE);
					homeTeamFormationNotice.setVisibility(View.VISIBLE);
				}
			}
			break;
		case 2:// 客队
			LayoutParams params2 = new RelativeLayout.LayoutParams(CustomApplcation.getInstance().getParameters().getmScreenWidth(),
					Math.round(CustomApplcation.getInstance().getParameters().getmScreenWidth() / 2.75348f));//图片的宽高比例
			awayTeamFormationBg.setLayoutParams(params2);
			ImageLoader.getInstance().displayImage("drawable://" + R.drawable.formation_bg, awayTeamFormationBg, 
					ImageLoadOptions.getDisplayDrawableBuilder().build());
			
			awayTeamFormationTitle.setVisibility(View.VISIBLE);
			awayTeamFormationTitle.setText("阵容");
			awayTeamFormationLayout.setVisibility(View.VISIBLE);
			if (matchInfo.getAwayTeamFormation() != null) {// 有阵容
				if (isCaptain) {// 是队长
					awayTeamSetFormation.setText("设置阵容");
					awayTeamFormationNotice.setVisibility(View.GONE);
//					awayTeamSetFormation
//							.setOnClickListener(new View.OnClickListener() {
//								@Override
//								public void onClick(View v) {
//									Intent intent = new Intent(mContext,
//											FormationDetailActivity.class);
//									intent.putExtra(
//											FormationConstants.INTENT_FORMATION_BEAN,
//											matchInfo.getAwayTeamFormation());
//									startActivity(intent);
//								}
//							});
					awayTeamSetFormation.setOnClickListener(new OnClickListener() {//有阵容 修改

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext,
									FormationListActivity.class);
							intent.putExtra(
									FormationListActivity.INTENT_IS_CAPTAIN,
									true);
							intent.putExtra(
									FormationListActivity.INTENT_IS_FOR_SELECT,
									true);
							intent.putExtra(
									FormationListActivity.INTENT_TEAM_UUID,
									matchInfo.getAwayTeam().getUuid());
							startActivityForResult(intent,
									INTENT_SELECT_MATCH_FORMATION_AWAY_TEAM);
						}
					});
				} else {// 不是队长
					awayTeamSetFormation.setText("查看阵容");
					awayTeamFormationNotice.setVisibility(View.GONE);
					awayTeamSetFormation
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									Formation formation = matchInfo
											.getAwayTeamFormation();
									if (formation == null)
										return;
									Intent intent = new Intent(mContext,
											FormationDetailActivity.class);
									intent.putExtra(
											FormationConstants.INTENT_FORMATION_BEAN,
											formation);
									startActivity(intent);
								}
							});
				}
			} else {// 没有阵容
				if (isCaptain) {// 是队长
					awayTeamSetFormation.setText("设置阵容");
					awayTeamFormationNotice.setVisibility(View.GONE);
					awayTeamSetFormation.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext,
									FormationListActivity.class);
							intent.putExtra(
									FormationListActivity.INTENT_IS_CAPTAIN,
									true);
							intent.putExtra(
									FormationListActivity.INTENT_IS_FOR_SELECT,
									true);
							intent.putExtra(
									FormationListActivity.INTENT_TEAM_UUID,
									matchInfo.getAwayTeam().getUuid());
							startActivityForResult(intent,
									INTENT_SELECT_MATCH_FORMATION_AWAY_TEAM);
						}
					});
				} else {// 不是队长
					awayTeamSetFormation.setVisibility(View.GONE);
					awayTeamFormationNotice.setVisibility(View.VISIBLE);
				}
			}
			break;
		case 3:
			LayoutParams params3 = new RelativeLayout.LayoutParams(CustomApplcation.getInstance().getParameters().getmScreenWidth(),
					Math.round(CustomApplcation.getInstance().getParameters().getmScreenWidth() / 2.75348f));//图片的宽高比例
			homeTeamFormationBg.setLayoutParams(params3);
			ImageLoader.getInstance().displayImage("drawable://" + R.drawable.formation_bg, homeTeamFormationBg, 
					ImageLoadOptions.getDisplayDrawableBuilder().build());
			awayTeamFormationBg.setLayoutParams(params3);
			ImageLoader.getInstance().displayImage("drawable://" + R.drawable.formation_bg, awayTeamFormationBg, 
					ImageLoadOptions.getDisplayDrawableBuilder().build());
			
			homeTeamFormationTitle.setVisibility(View.VISIBLE);
			homeTeamFormationLayout.setVisibility(View.VISIBLE);
			awayTeamFormationTitle.setVisibility(View.VISIBLE);
			awayTeamFormationLayout.setVisibility(View.VISIBLE);
			if (matchInfo.getHomeTeamFormation() != null) {// 有阵容
				homeTeamSetFormation.setText("查看阵容");
				homeTeamFormationNotice.setVisibility(View.GONE);
				homeTeamSetFormation
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent intent = new Intent(mContext,
											FormationDetailActivity.class);
									intent.putExtra(
											FormationConstants.INTENT_FORMATION_BEAN,
											matchInfo.getHomeTeamFormation());
									startActivity(intent);
								}
							});
			} else {// 没有阵容
				homeTeamSetFormation.setVisibility(View.GONE);
				homeTeamFormationNotice.setVisibility(View.VISIBLE);
			}
			if (matchInfo.getAwayTeamFormation() != null) {//客队有阵容
				awayTeamSetFormation.setText("查看阵容");
				awayTeamFormationNotice.setVisibility(View.GONE);
				awayTeamSetFormation
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent intent = new Intent(mContext,
											FormationDetailActivity.class);
									intent.putExtra(
											FormationConstants.INTENT_FORMATION_BEAN,
											matchInfo.getHomeTeamFormation());
									startActivity(intent);
								}
							});
			} else {// 没有阵容
				awayTeamSetFormation.setVisibility(View.GONE);
				awayTeamFormationNotice.setVisibility(View.VISIBLE);
			}
			break;
		default:
			break;
		}
	}
}
