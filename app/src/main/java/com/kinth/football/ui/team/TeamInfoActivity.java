package com.kinth.football.ui.team;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.adapter.TeamMemberHorizontalListAdapter;
import com.kinth.football.bean.City;
import com.kinth.football.bean.RegionBean;
import com.kinth.football.bean.TeamInfoComplete;
import com.kinth.football.bean.User;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.bean.match.MatchRecords;
import com.kinth.football.bean.match.PlayerInTeam;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.chat.bean.ChatRecent;
import com.kinth.football.chat.db.ChatDBManager;
import com.kinth.football.chat.ui.ChatActivity;
import com.kinth.football.config.MatchResultEnum;
import com.kinth.football.dao.CityDao;
import com.kinth.football.dao.Player;
import com.kinth.football.dao.RegionDao;
import com.kinth.football.dao.Team;
import com.kinth.football.dao.TeamDao;
import com.kinth.football.dao.TeamPlayer;
import com.kinth.football.dao.TeamPlayerDao;
import com.kinth.football.eventbus.bean.ExitTeamEvent;
import com.kinth.football.eventbus.bean.ModifyTeamAlternetJerseyEvent;
import com.kinth.football.eventbus.bean.ModifyTeamBadgeEvent;
import com.kinth.football.eventbus.bean.ModifyTeamCityEvent;
import com.kinth.football.eventbus.bean.ModifyTeamDescriptionEvent;
import com.kinth.football.eventbus.bean.ModifyTeamFamilyPhotoEvent;
import com.kinth.football.eventbus.bean.ModifyTeamFirstCaptainEvent;
import com.kinth.football.eventbus.bean.ModifyTeamHomeJerseyEvent;
import com.kinth.football.eventbus.bean.ModifyTeamNameEvent;
import com.kinth.football.eventbus.bean.ModifyTeamRegionEvent;
import com.kinth.football.eventbus.bean.ModifyTeamRoadJerseyEvent;
import com.kinth.football.eventbus.bean.ModifyTeamSecondCaptainEvent;
import com.kinth.football.eventbus.bean.ModifyTeamSloganEvent;
import com.kinth.football.eventbus.bean.ModifyTeamThirdCaptainEvent;
import com.kinth.football.eventbus.bean.ModifyTeamIsLiked;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.ui.fragment.TeamFragment;
import com.kinth.football.ui.match.MatchAll_Finished_ByTeam;
import com.kinth.football.ui.match.MatchDetailOnFinishedStateActivity;
import com.kinth.football.ui.match.MatchFriendly_Finished_ByTeam;
import com.kinth.football.ui.team.formation.CreateFormationActivity;
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

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

/**
 * 球队详细信息页面 传递过来的数据可能分两种，一种是Team，一种是TeamInfoComplete
 * 
 * @author Sola
 */
@ContentView(R.layout.activity_team_info2)
public class TeamInfoActivity extends BaseActivity implements
		OnPageChangeListener {
	private static Object object = new Object();// 同步锁
	public static final String INTENT_TEAM_COMPLETE_INFO_BEAN = "INTENT_TEAM_COMPLETE_INFO_BEAN";// intent中传递的球队数据信息，完整信息
	public static final String INTENT_TEAM_INFO_BEAN = "INTENT_TEAM_INFO_BEAN";// 只包含基本的球队信息Team
	public static final String INTENT_NEET_TO_RETURN_BEAN = "INTENT_NEET_TO_RETURN_BEAN";// 是否需要返回bean给对方
	public static final String INTENT_NEED_TO_GET_CYBER_NEW = "INTENT_NEED_TO_GET_CYBER_NEW";// 是否需要联网获取数据
																								// 目前仅限onlyTeam

	public static final String INTENT_EDIT_TEAM_INFO = "INTENT_EDIT_TEAM_INFO";

	public static final int REQUEST_CODE_EDIT_TEAM_INFO = 1004;// 完整球队信息
	public static final int REQUEST_CODE_EDIT_ONLY_TEAM_INFO = 1005;// 构造的球队信息
	public static final int REQUEST_CODE_CREATE_DEFAULT_FORMATION = 1006;// 构造的球队信息
	public static final String RESULT_NEED_TO_REFRESH = "RESULT_NEED_TO_REFRESH";// 需要刷新
	public static final String RESULT_RETURN_TEAM_BEAN = "RESULT_RETURN_TEAM_BEAN";

	private Team onlyTeam;// 只包含球队的数据
	private TeamInfoComplete teamConstruct;// 通过onlyTeam构造的不完善数据
	private TeamInfoComplete teamInfoComplete; // 完整数据
	private boolean isCompleteInfo = false; // 是否完整数据
	private int userType = 0; // 用户类型
	private boolean isNeedToReturnBean = false;// 是否需要回传
	private boolean isNeedToRefresh = false;
	private ProgressDialog progressDia;
	private TeamMemberHorizontalListAdapter adapter;// 球队成员的横向adapter
	// 队服的view数组
	private List<ViewWithPath> jersey_list;
	private Dialog dialog;

	private Formation defaultFormation;

	@ViewInject(R.id.nav_left)
	private ImageButton back;

	@ViewInject(R.id.nav_title)
	private TextView nav_title;

	@ViewInject(R.id.iv_fimily_photo_bg)
	private ImageView familyPhotoBg;// 全家福背景

	@ViewInject(R.id.nav_right_image)
	private ImageView right;// 编辑信息

	@ViewInject(R.id.iv_family_photo)
	private ImageView familyPhoto;// 全家福

	@ViewInject(R.id.iv_badge)
	private RoundImageView badge;// 队徽

	@ViewInject(R.id.like_team)
	private ImageView liketeam;// 喜欢球队

	@ViewInject(R.id.like_team_number)
	private TextView like_team_number;// 喜欢球队的人数

	@ViewInject(R.id.team_name)
	private TextView teamName;

	@ViewInject(R.id.team_place)
	// 球队地点 TODO
	private TextView team_place;

	@ViewInject(R.id.start_chat)
	private LinearLayout startGroupChat;

	@ViewInject(R.id.member_more)
	private LinearLayout member_more;// 查看更多成员

	@ViewInject(R.id.teamInfo_memberlist)
	private HorizontalListView teamMemberHorizontalListView;// 球队成员

	// 总比赛战绩 6种数字
	@ViewInject(R.id.allmatch_won)
	private TextView allmatch_won;

	@ViewInject(R.id.allmatch_lost)
	private TextView allmatch_lost;

	@ViewInject(R.id.allmatch_drawn)
	private TextView allmatch_drawn;

	@ViewInject(R.id.allmatch_scored)
	private TextView allmatch_scored;

	@ViewInject(R.id.allmatch_against)
	private TextView allmatch_against;

	@ViewInject(R.id.allmatch_scored_jing)
	private TextView allmatch_scored_jing;

	// 友谊比赛战绩 6种数字

	@ViewInject(R.id.all_match_more)
	private LinearLayout all_match_more;

	@ViewInject(R.id.tv_team_founded_time)
	private TextView teamFoundedTimeText;// 成立时间文本

	@ViewInject(R.id.tv_team_slogan)
	private TextView teamSloganText;// 球队口号文本

	@ViewInject(R.id.tv_team_description)
	private TextView teamDescriptionText;// 球队介绍文本

	/*
	 * @ViewInject(R.id.iv_formation_bg) private ImageView formationBg;//
	 * 阵容的bg图片
	 */
	@ViewInject(R.id.tv_formation_title)
	private TextView tvFormationTitle;

	@ViewInject(R.id.tv_formation_description)
	private TextView tvFormationDesc;

	@ViewInject(R.id.iv_formation)
	private ImageView ivFormation;

	@ViewInject(R.id.btn_exit_team)
	private Button btnExitTeam;

	@OnClick(R.id.start_chat)
	public void startGroupChat(View v) {// 发起群聊
		// 首先，将其添加到“聊天”Fragment中
		ChatRecent chatRecent = null;
		if (isCompleteInfo) {
			chatRecent = new ChatRecent(teamInfoComplete.getTeam().getUuid(),
					teamInfoComplete.getTeam().getUuid(), teamInfoComplete
							.getTeam().getName(),
					PhotoUtil.getAllPhotoPath(PhotoUtil
							.getAllPhotoPath(teamInfoComplete.getTeam()
									.getBadge())), "你可以和他进行聊天",
					System.currentTimeMillis(), ChatConstants.MSG_TYPE_TEXT, 1);
		} else {
			chatRecent = new ChatRecent(onlyTeam.getUuid(), onlyTeam.getUuid(),
					onlyTeam.getName(), PhotoUtil.getAllPhotoPath(PhotoUtil
							.getAllPhotoPath(onlyTeam.getBadge())),
					"你可以和他进行聊天", System.currentTimeMillis(),
					ChatConstants.MSG_TYPE_TEXT, 1);
		}
		ChatDBManager.create(mContext).saveRecent(chatRecent);

		// 其次，跳转到聊天Activity
		// 重置未读消息(目前是靠phone)
		ChatDBManager.create(mContext).resetUnread(chatRecent.getTargetid(),
				chatRecent.getTag());
		// 组装聊天对象
		User user = new User();
		Player player = new Player();
		player.setName(chatRecent.getNick());
		player.setPicture(chatRecent.getAvatar());
		player.setUuid(chatRecent.getUserName());
		player.setAccountName(chatRecent.getTargetid());
		user.setPlayer(player);

		Intent intent = new Intent(mContext, ChatActivity.class);
		intent.putExtra(ChatConstants.INTENT_USER_BEAN, user);
		intent.putExtra(ChatConstants.TAG, chatRecent.getTag());
		startAnimActivity(intent);
	}

	@OnClick(R.id.like_team)
	public void fun_11(View v) {// 喜欢球队
		if (isCompleteInfo) {
			if (teamInfoComplete.isLiked()) {// 已经like过
				// liketeam.setImageResource(R.drawable.like1);
				// ShowToast("已经喜欢过该球队");// TODO 取消like
				executeCancelLikeTeam(teamInfoComplete.getTeam());
			} else {
				executeLikeTeam(teamInfoComplete.getTeam());
			}
		} else {
			if (onlyTeam.getLiked()) {
				// liketeam.setImageResource(R.drawable.like1);
				// ShowToast("已经喜欢过该球队");// TODO 取消like
				executeCancelLikeTeam(onlyTeam);
			} else {
				executeLikeTeam(onlyTeam);
			}
		}
	};

	@OnClick(R.id.iv_badge)
	public void fun_10(View v) {// 点击查看大图
		if (isCompleteInfo) {
			String imgUrl = teamInfoComplete.getTeam().getBadge();
			if (imgUrl != null) {
				ArrayList<String> photos = new ArrayList<String>();
				photos.add(PhotoUtil.getAllPhotoPath(imgUrl));
				PictureUtil.viewLargerImage(mContext, photos);
			}
		} else {
			String imgUrl = onlyTeam.getBadge();
			if (imgUrl != null) {
				ArrayList<String> photos = new ArrayList<String>();
				photos.add(PhotoUtil.getAllPhotoPath(imgUrl));
				PictureUtil.viewLargerImage(mContext, photos);
			}
		}
	}

	@OnClick(R.id.all_match_more)
	public void fun_8(View v) {// 查看更多比赛
		// if (isCompleteInfo) {
		// Intent intent = new Intent(this, MatchAll_Finished_ByTeam.class);
		// intent.putExtra(MatchAll_Finished_ByTeam.INTENT_TEAM_UUID,
		// teamInfoComplete.getTeam().getUuid());
		// startActivity(intent);
		// } else {
		// Intent intent = new Intent(this, MatchAll_Finished_ByTeam.class);
		// intent.putExtra(MatchAll_Finished_ByTeam.INTENT_TEAM_UUID,
		// onlyTeam.getUuid());
		// startActivity(intent);
		// }
		if (isCompleteInfo) {
			Intent intent = new Intent(this, TeamMatchRecordActivity.class);
			intent.putExtra(TeamMatchRecordActivity.TEAM,
					teamInfoComplete.getTeam());
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, TeamMatchRecordActivity.class);
			intent.putExtra(TeamMatchRecordActivity.TEAM, onlyTeam);
			startActivity(intent);
		}
	}

	@OnClick(R.id.friendly_match_more)
	public void fun_9(View v) {// 查看更多友谊赛
		if (isCompleteInfo) {
			Intent intent = new Intent(this,
					MatchFriendly_Finished_ByTeam.class);
			intent.putExtra(MatchFriendly_Finished_ByTeam.INTENT_TEAM_UUID,
					teamInfoComplete.getTeam().getUuid());
			startActivity(intent);
		} else {
			Intent intent = new Intent(this,
					MatchFriendly_Finished_ByTeam.class);
			intent.putExtra(MatchFriendly_Finished_ByTeam.INTENT_TEAM_UUID,
					onlyTeam.getUuid());
			startActivity(intent);
		}
	}

	@OnClick(R.id.member_more)
	public void fun_6(View v) {// 查看更多成员列表
		if (isCompleteInfo) {
			Intent intent = new Intent(mContext, TeamMemberListActivity.class);
			intent.putExtra(TeamInfoActivity.INTENT_TEAM_COMPLETE_INFO_BEAN,
					teamInfoComplete);
			// startActivity(intent);
			startActivityForResult(intent,
					TeamMemberListActivity.TEAMMEMBER_NUMBER_CHANGE);
		} else {
			Intent intent = new Intent(mContext, TeamMemberListActivity.class);
			intent.putExtra(TeamInfoActivity.INTENT_TEAM_INFO_BEAN, onlyTeam);
			startActivityForResult(intent,
					TeamMemberListActivity.TEAMMEMBER_NUMBER_CHANGE_ONLY_TEAM);
		}

		// if (isCompleteInfo) {
		// Intent intent = new Intent(mContext, InviteMemberActivity.class);
		// intent.putExtra(TeamInfoActivity.INTENT_TEAM_INFO_BEAN,
		// teamInfoComplete.getTeam());
		// startActivity(intent);
		// } else {
		// Intent intent = new Intent(mContext, InviteMemberActivity.class);
		// intent.putExtra(TeamInfoActivity.INTENT_TEAM_INFO_BEAN, onlyTeam);
		// startActivity(intent);
		// }
	}

	@OnClick(R.id.nav_right_image)
	public void fun_4(View v) {// 编辑球队
		if (isCompleteInfo && (userType == 1 || userType == 2 || userType == 3)) {
			Intent intent = new Intent(this, TeamEditActivity.class);
			intent.putExtra(TeamEditActivity.INTENT_TEAM_INFO_BEAN,
					teamInfoComplete);
			intent.putExtra(TeamEditActivity.INTENT_NEET_TO_RETURN_BEAN, true);// 需要回传更改后的信息
			startActivityForResult(intent, REQUEST_CODE_EDIT_TEAM_INFO);
		} else if (!isCompleteInfo
				&& (userType == 1 || userType == 2 || userType == 3)) {// 非完整数据，是队长
			Intent intent = new Intent(this, TeamEditActivity.class);
			intent.putExtra(TeamEditActivity.INTENT_TEAM_INFO_BEAN,
					teamConstruct);
			intent.putExtra(TeamEditActivity.INTENT_NEET_TO_RETURN_BEAN, true);// 需要回传更改后的信息
			startActivityForResult(intent, REQUEST_CODE_EDIT_ONLY_TEAM_INFO);
		}
	}

	@OnClick(R.id.nav_left)
	public void fun_1(View v) {
		back();
	}

	@OnClick(R.id.ll_desc)
	public void defaultFormationDetail(View v) {
		if (defaultFormation == null) {
			if (userType == 1 || userType == 2 || userType == 3) { // 队长权限
				Intent intent = new Intent(TeamInfoActivity.this,
						CreateFormationActivity.class);
				if (teamInfoComplete != null) {
					intent.putExtra(FormationConstants.INTENT_TEAM_UUID,
							teamInfoComplete.getTeam().getUuid());
				} else if (onlyTeam != null) {
					intent.putExtra(FormationConstants.INTENT_TEAM_UUID,
							onlyTeam.getUuid());
				}
				intent.setAction("ACTION_CREATE_DEFAULT_FORMATION");
				startActivityForResult(intent, REQUEST_CODE_CREATE_DEFAULT_FORMATION);
			}
			return;
		}
		Intent intent = new Intent(TeamInfoActivity.this,
				FormationDetailActivity.class);
		intent.putExtra(FormationConstants.INTENT_FORMATION_BEAN,
				defaultFormation);
		if (teamInfoComplete != null) {
			intent.putExtra(FormationConstants.INTENT_TEAM_UUID,
					teamInfoComplete.getTeam().getUuid());
		} else if (onlyTeam != null) {
			intent.putExtra(FormationConstants.INTENT_TEAM_UUID,
					onlyTeam.getUuid());
		}
		startActivity(intent);
	}

	@OnClick(R.id.llt_more_formation)
	public void moreFormation(View v) {
		toFormationList();
	}

	private void toFormationList(){
		if (isCompleteInfo) {// 完整信息
			Intent intent = new Intent(mContext, FormationListActivity.class);
			intent.putExtra(FormationConstants.INTENT_TEAM_UUID,
					teamInfoComplete.getTeam().getUuid());
			if (userType == 1 || userType == 2 || userType == 3) {
				intent.putExtra(FormationListActivity.INTENT_IS_CAPTAIN, true);
			} else {
				intent.putExtra(FormationListActivity.INTENT_IS_CAPTAIN, false);
			}
			startActivity(intent);
		} else {// team信息
			if (onlyTeam == null) {
				return;
			}
			Intent intent = new Intent(mContext, FormationListActivity.class);
			intent.putExtra(FormationConstants.INTENT_TEAM_UUID,
					onlyTeam.getUuid());
			if (userType == 1 || userType == 2 || userType == 3) {
				intent.putExtra(FormationListActivity.INTENT_IS_CAPTAIN, true);
			} else {
				intent.putExtra(FormationListActivity.INTENT_IS_CAPTAIN, false);
			}
			startActivity(intent);
		}
	}
	
	private void back() {
		if (isNeedToReturnBean) {
			Intent intent = new Intent();
			if (isNeedToRefresh) {
				intent.putExtra(TeamFragment.RESULT_NEED_TO_REFRESH, true);
				if (isCompleteInfo) {
					intent.putExtra(INTENT_TEAM_INFO_BEAN, teamInfoComplete);
				} else {
					intent.putExtra(INTENT_TEAM_INFO_BEAN, onlyTeam);
				}
			} else {
				intent.putExtra(TeamFragment.RESULT_NEED_TO_REFRESH, false);
			}
			setResult(RESULT_OK, intent);
		}
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@OnClick(R.id.llt_team_formation)
	public void fun_5(View v) { // 默认球队阵容

	}

	@OnClick(R.id.btn_exit_team)
	public void fun_2(View v) {// 退出球队
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setMessage("确定退出球队？");
		builder.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog2, int which) {
						dialog2.dismiss();
						dialog = ProgressDialog.show(mContext, "提示", "请稍后",
								false, false);
						final String teamUuid = isCompleteInfo ? teamInfoComplete
								.getTeam().getUuid() : onlyTeam.getUuid();
						NetWorkManager.getInstance(mContext).exitTeam(
								teamUuid,
								UserManager.getInstance(mContext)
										.getCurrentUser().getToken(),
								new Listener<Void>() {

									@Override
									public void onResponse(Void response) {
										DialogUtil.dialogDismiss(dialog);
										ShowToast("退出球队成功");// 本地数据库表的更新
										// 删除TeamPlayer关联表的数据
										TeamPlayerDao teamPlayerDao = CustomApplcation
												.getDaoSession(mContext)
												.getTeamPlayerDao();
										QueryBuilder<TeamPlayer> teamPlayerQB = teamPlayerDao
												.queryBuilder()
												.where(TeamPlayerDao.Properties.Team_id
														.eq(teamUuid));
										List<TeamPlayer> allTeamPlayer = teamPlayerQB
												.list();
										for (TeamPlayer teamPlayer : allTeamPlayer) {
											if (teamPlayer.getPlayer_id()
													.equals(UserManager
															.getInstance(
																	mContext)
															.getCurrentUser()
															.getPlayer()
															.getUuid())) {
												teamPlayerDao
														.delete(teamPlayer);
												break;
											}
										}
										EventBus.getDefault().post(
												new ExitTeamEvent(teamUuid));
										finish();
									}
								}, new ErrorListener() {

									@Override
									public void onErrorResponse(
											VolleyError error) {
										DialogUtil.dialogDismiss(dialog);
										if (!NetWorkManager.getInstance(
												mContext).isNetConnected()) {
											ShowToast("当前网络不可用，请稍后重试");
										} else if (error.networkResponse == null) {
											ShowToast("退出球队失败，请稍后重试");
										} else if (error.networkResponse.statusCode == 401) {
											ErrorCodeUtil
													.ErrorCode401(mContext);
										} else if (error.networkResponse.statusCode == 403) {
											ShowToast("队长不能直接退出球队");
										} else if (error.networkResponse.statusCode == 404) {
											ShowToast("球队找不到");
										} else if (error.networkResponse.statusCode == 409) {
											ShowToast("您不是该球队成员");
										}
									}
								});
					}
				});
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.show();
	}

	@ViewInject(R.id.viewpager_jersey)
	private ViewPager viewPager;

	@ViewInject(R.id.no_have_last_five_result_tv)
	private TextView last_five_result_tv; // 没有最后5场比赛结果的文本

	@ViewInject(R.id.match_result1)
	private ImageView allmatch_result1;// 最近5场所有比赛结果的图标--1

	@ViewInject(R.id.match_result2)
	private ImageView allmatch_result2; // 最近5场所有比赛结果的图标--2

	@ViewInject(R.id.match_result3)
	private ImageView allmatch_result3;// 最近5场所有比赛结果的图标--3

	@ViewInject(R.id.match_result4)
	private ImageView allmatch_result4;// 最近5场所有比赛结果的图标--4

	@ViewInject(R.id.match_result5)
	private ImageView allmatch_result5;// 最近5场所有比赛结果的图标--5

	private ImageView[] match_results = new ImageView[5];// 最近5场比赛

	// 最新战报
	private MatchInfo latest_matchInfo;

	@ViewInject(R.id.llt_latest_battle)
	private LinearLayout llt_latest_battle; // 最新的比赛

	@ViewInject(R.id.tv_latest_battle_more)
	private LinearLayout tv_latest_battle_more; // 更多（3个小点点）

	@ViewInject(R.id.iv_home_team_icon)
	private ImageView iv_home_team_icon;

	@ViewInject(R.id.tv_home_team_name)
	private TextView tv_home_team_name;

	@ViewInject(R.id.iv_away_team_icon)
	private ImageView iv_away_team_icon;

	@ViewInject(R.id.tv_away_team_name)
	private TextView tv_away_team_name;

	@ViewInject(R.id.tv_match_field)
	private TextView tv_match_field;

	@ViewInject(R.id.tv_score)
	private TextView tv_score;

	@ViewInject(R.id.tv_match_date)
	private TextView tv_match_date;

	@ViewInject(R.id.tv_tip_no_match)
	private TextView tv_tip_no_match; // 没有最新战报时的文本显示

	@OnClick(R.id.llt_latest_battle)
	// 点击最新战报的比赛
	public void fun_30(View v) {

		Intent intent = new Intent(mContext,
				MatchDetailOnFinishedStateActivity.class);
		intent.putExtra(
				MatchDetailOnFinishedStateActivity.INTENT_MATCH_DETAIL_BEAN,
				latest_matchInfo);
		startActivity(intent);
	}

	@OnClick(R.id.tv_latest_battle_more)
	// 点右上方小点点
	public void fun_31(View v) {
		Intent intent = new Intent(mContext, MatchAll_Finished_ByTeam.class);
		if (isCompleteInfo) {
			intent.putExtra(MatchAll_Finished_ByTeam.INTENT_TEAM_UUID,
					teamInfoComplete.getTeam().getUuid());
		} else {
			intent.putExtra(MatchAll_Finished_ByTeam.INTENT_TEAM_UUID,
					onlyTeam.getUuid());
		}
		startActivity(intent);
	}

	@ViewInject(R.id.viewGroup)
	private LinearLayout viewGroup;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		ViewUtils.inject(this);

		
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();	
		ViewCompat.setBackground(viewGroup, new BitmapDrawable(getResources(),
				background));
		/*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				CustomApplcation.getInstance().getParameters()
						.getmScreenWidth(),
				Math.round(CustomApplcation.getInstance().getParameters()
						.getmScreenWidth() / 2.75348f));// 图片的宽高比例
		formationBg.setLayoutParams(params);*/

		/*
		 * ImageLoader.getInstance().displayImage( "drawable://" +
		 * R.drawable.formation_bg, formationBg,
		 * ImageLoadOptions.getDisplayDrawableBuilder().build());
		 */
		ImageLoader.getInstance().displayImage(
				"drawable://" + R.drawable.team_bage_default, badge,
				ImageLoadOptions.getDisplayDrawableBuilder().build());
		ImageLoader.getInstance().displayImage(
				"drawable://" + R.drawable.bg_family_photo, familyPhotoBg,
				ImageLoadOptions.getDisplayDrawableBuilder().build());

		initView();
		teamInfoComplete = getIntent().getParcelableExtra(
				INTENT_TEAM_COMPLETE_INFO_BEAN);
		isNeedToReturnBean = getIntent().getBooleanExtra(
				INTENT_NEET_TO_RETURN_BEAN, false);
		boolean isNeedToGetCyberNew = getIntent().getBooleanExtra(
				INTENT_NEED_TO_GET_CYBER_NEW, false);

		if (teamInfoComplete == null) {
			isCompleteInfo = false;
			onlyTeam = getIntent().getParcelableExtra(INTENT_TEAM_INFO_BEAN);
			initMemberList();
			if (onlyTeam == null) {// 加载默认的空白页面
				familyPhoto.setImageResource(R.drawable.team_family_defualt);// 默认

				badge.setImageResource(R.drawable.team_bage_default);
			} else {
				teamConstruct = new TeamInfoComplete();
				teamConstruct.setTeam(onlyTeam);// 尝试构造完整数据
				userType = judgeUserType(onlyTeam);
				path4Team();
				if (isNeedToGetCyberNew) {
					getCyberNew(onlyTeam);
				}
			}
		} else {
			isCompleteInfo = true;
			userType = judgeUserType(teamInfoComplete.getTeam());

			initMemberList();
			path4CompleteTeam();

			if (isNeedToGetCyberNew) {
				getCyberNew(teamInfoComplete.getTeam());
			}
		}

		boolean isGuest = true;// 是否来宾权限
		List<TeamPlayer> teamPlayerList = CustomApplcation
				.getDaoSession(mContext)
				.getTeamPlayerDao()
				._queryPlayer_TeamPlayerList(
						UserManager.getInstance(mContext).getCurrentUser()
								.getPlayer().getUuid());
		for (TeamPlayer teamPlayer : teamPlayerList) {
			Team team = CustomApplcation.getDaoSession(mContext).getTeamDao()
					.queryBuilder()
					.where(TeamDao.Properties.Uuid.eq(teamPlayer.getTeam_id()))
					.build().unique();
			if (onlyTeam != null) {
				if (team.getUuid().equals(onlyTeam.getUuid())) {
					isGuest = false;
					break;
				}
			}
			if (teamInfoComplete != null) {
				if (team.getUuid().equals(teamInfoComplete.getTeam().getUuid())) {
					isGuest = false;
					break;
				}
			}

		}
		if (isGuest)
			startGroupChat.setVisibility(View.GONE);
		else
			startGroupChat.setVisibility(View.VISIBLE);

		showDefaultFormation();
	}

	private void showDefaultFormation() {
		// TODO 自动生成的方法存根
		String teamUUID = "";
		if (teamInfoComplete != null)
			teamUUID = teamInfoComplete.getTeam().getUuid();
		else if (onlyTeam != null)
			teamUUID = onlyTeam.getUuid();
		NetWorkManager.getInstance(mContext).getAllFormation(teamUUID,
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<JSONArray>() {

					@Override
					public void onResponse(JSONArray response) {
						Gson gson = new Gson();
						List<Formation> formationList = null;
						try {
							formationList = gson.fromJson(response.toString(),
									new TypeToken<ArrayList<Formation>>() {
									}.getType());
						} catch (JsonSyntaxException e) {
							formationList = null;
							e.printStackTrace();
						}
						if (formationList != null && formationList.size() > 0) {
							defaultFormation = formationList.get(0);
							tvFormationTitle.setText(defaultFormation.getName());
							tvFormationDesc.setText(defaultFormation
									.getDescription());
							ImageLoader.getInstance().displayImage(
									defaultFormation.getImage() + "Q03.jpg",
									ivFormation, ImageLoadOptions.getOptions());
						} else {
							if (userType == 1 || userType == 2 || userType == 3) { // 队长权限
								tvFormationDesc.setText("还没创建阵容，马上去创建一个吧");
							} else {
								tvFormationDesc.setText("队长尚未创建阵容");
							}
							tvFormationDesc.setGravity(Gravity.CENTER);
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						ShowToast("获取阵容列表失败");
						tvFormationDesc.setText("获取阵容失败");
						tvFormationDesc.setGravity(Gravity.CENTER);
					}
				});
	}

	/**
	 * add by sola 联网获取球队新数据
	 */
	private void getCyberNew(Team oldTeam) {
		NetWorkManager.getInstance(mContext).getTeam(oldTeam.getUuid(),
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Gson gson = new Gson();
						TeamInfoComplete tic = gson.fromJson(
								response.toString(),
								new TypeToken<TeamInfoComplete>() {
								}.getType());
						if (tic == null || tic.getTeam() == null)
							return;
						if (isCompleteInfo) {
							teamInfoComplete = tic;
							path4CompleteTeam2();
						} else {
							onlyTeam = tic.getTeam();
							onlyTeam.setLike(tic.getLike());
							onlyTeam.setLiked(tic.isLiked());
							path4Team2();
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						ShowToast("获取球队信息失败");
					}
				});
	}

	private void init_data(Team team) {// 时间，介绍，口号 ,地点
		String teamDate = null;
		try {
			teamDate = DateUtil.parseTimeInMillis_only_nyr(team.getDate());
		} catch (Exception e) {
			teamDate = "无";
		}
		teamFoundedTimeText.setText(teamDate);// 成立时间

		team_place
				.setText(StringUtils.defaultIfEmpty(getRegionName(team), " "));

		teamSloganText.setText(StringUtils.defaultString(team.getSlogan(), ""));
		teamDescriptionText.setText(StringUtils.defaultString(
				team.getDescription(), ""));
	}

	/**
	 * 通过城市id获取名称 TODO 重构
	 * 
	 * @param team
	 * @return
	 */
	private String getRegionName(Team team) {
		CityDao	cityDao = new CityDao(mContext);
		City city = cityDao.getCityByCityId(team.getCityId() == null ? -1 : team.getCityId());

		String cityName = "";
		if(city != null){
			cityName = city.getName();
		}
		RegionDao regDao = new RegionDao(mContext);
		String regionName = "";
		if(team.getRegionId() != null && team.getRegionId() != -1){
			RegionBean region= regDao.getRegionById(team.getRegionId());
			if(region != null){
				regionName = region.getName();
			}
		}else {
			regionName = "";
		}
		return cityName + "   " + regionName;
	}

	/**
	 * 初始化球队成员列表
	 */
	private void initMemberList() {
		adapter = new TeamMemberHorizontalListAdapter(mContext, null);
		teamMemberHorizontalListView.setAdapter(adapter);
		teamMemberHorizontalListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						PlayerInTeam playerInTeam = adapter.getItem(position);// 因为有了headerView
						// 个人信息
						Intent intent = new Intent(mContext,
								TeamPlayerInfo.class);
						intent.putExtra(TeamPlayerInfo.PLAYER_UUID,
								playerInTeam.getPlayer().getUuid());
						startActivity(intent);
					}
				});
	}

	/**
	 * 是否队长--包含一二三队长
	 * 
	 * @return
	 */
	private int judgeUserType(Team team) {
		String uid = footBallUserManager.getCurrentUser().getPlayer().getUuid();
		if (uid.equals(team.getFirstCaptainUuid())) {
			return 1;
		}
		if (uid.equals(team.getSecondCaptainUuid())) {
			return 2;
		}
		if (uid.equals(team.getThirdCaptainUuid())) {
			return 3;
		}
		return 4;
	}

	/**
	 * 执行like球队的操作
	 */
	private void executeLikeTeam(Team team) {
		progressDia = ProgressDialog.show(mContext, "提示", "请稍后...", false,
				false);
		NetWorkManager.getInstance(mContext).liketeam(team.getUuid(),
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<Void>() {

					@Override
					public void onResponse(Void response) {
						DialogUtil.dialogDismiss(progressDia);
						ShowToast("成功喜欢该队");
						liketeam.setImageResource(R.drawable.like1);
						isNeedToRefresh = true;

						if (isCompleteInfo) {
							teamInfoComplete.setLike(teamInfoComplete.getLike() + 1);
							teamInfoComplete.setLiked(true);
							like_team_number.setText(String
									.valueOf(teamInfoComplete.getLike()));
							DBUtil.saveTeamInfoToDB(mContext, teamInfoComplete);
						} else {
							EventBus.getDefault().post(
									new ModifyTeamIsLiked(onlyTeam.getUuid(),
											true, onlyTeam.getLike() + 1));// 发送通知
							onlyTeam.setLiked(true);
							onlyTeam.setLike(onlyTeam.getLike() + 1);
							like_team_number.setText(String.valueOf(onlyTeam
									.getLike()));
							CustomApplcation.getDaoSession(mContext)
									.getTeamDao().insertOrReplace(onlyTeam);
						}
					}

				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						DialogUtil.dialogDismiss(progressDia);
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("当前网络不可用");
						} else if (error.networkResponse == null) {
							// ShowToast("TeamInfoActivity-executeLikeTeam-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else if (error.networkResponse.statusCode == 404) {
							ShowToast("球队找不到");
						} else if (error.networkResponse.statusCode == 409) {
							ShowToast("你已经like过此球队");
						}
					}
				});
	}

	/**
	 * 执行取消like球队的操作
	 */
	private void executeCancelLikeTeam(Team team) {
		progressDia = ProgressDialog.show(mContext, "提示", "请稍后...", false,
				false);
		NetWorkManager.getInstance(mContext).cancelLiketeam(team.getUuid(),
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<Void>() {

					@Override
					public void onResponse(Void response) {
						DialogUtil.dialogDismiss(progressDia);
						ShowToast("成功取消喜欢该队");
						liketeam.setImageResource(R.drawable.like2);
						isNeedToRefresh = true;
						if (isCompleteInfo) {
							teamInfoComplete.setLike(teamInfoComplete.getLike() - 1);
							teamInfoComplete.setLiked(false);
							like_team_number.setText(String
									.valueOf(teamInfoComplete.getLike()));
							DBUtil.saveTeamInfoToDB(mContext, teamInfoComplete);
						} else {
							EventBus.getDefault().post(
									new ModifyTeamIsLiked(onlyTeam.getUuid(),
											false, onlyTeam.getLike() - 1));// 发送通知
							onlyTeam.setLiked(false);
							onlyTeam.setLike(onlyTeam.getLike() - 1);
							like_team_number.setText(String.valueOf(onlyTeam
									.getLike()));
							CustomApplcation.getDaoSession(mContext)
									.getTeamDao().insertOrReplace(onlyTeam);
						}
					}

				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						DialogUtil.dialogDismiss(progressDia);
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("当前网络不可用");
						} else if (error.networkResponse == null) {
							// ShowToast("TeamInfoActivity-executeLikeTeam-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else if (error.networkResponse.statusCode == 404) {
							ShowToast("球队找不到");
						} else if (error.networkResponse.statusCode == 409) {
							ShowToast("你已经取消like过此球队");
						}
					}
				});
	}

	/**
	 * 在有完整球队数据的情况下执行的操作，无需请求网络数据
	 */
	private void path4CompleteTeam() {
		initLike2(teamInfoComplete);
		init_data(teamInfoComplete.getTeam());
		initJerseys(teamInfoComplete.getTeam()); // 队服的滑动显示
		getAllMatchResultOfTeam(teamInfoComplete.getTeam());
		getLatestMatchInfo(teamInfoComplete.getTeam());
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(teamInfoComplete.getTeam()
						.getFamilyPhoto()),
				familyPhoto,
				new DisplayImageOptions.Builder()
						.showImageOnLoading(
								R.drawable.image_download_loading_icon)
						.showImageForEmptyUri(R.drawable.bg_family_dafult)
						// 默认球队全家福
						.showImageOnFail(R.drawable.bg_family_dafult)
						.decodingOptions(ImageLoadOptions.decodingOptions())
						.cacheInMemory(true).cacheOnDisk(true).build());
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(teamInfoComplete.getTeam()
						.getBadge()),
				badge,
				new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.team_bage_default)
						.showImageForEmptyUri(R.drawable.team_bage_default)
						.showImageOnFail(R.drawable.team_bage_default)
						.cacheInMemory(true).cacheOnDisk(true).build());
		teamName.setText(teamInfoComplete.getTeam().getName());// 球队名称
		if (userType == 1 || userType == 2 || userType == 3) {
			right.setImageResource(R.drawable.edit);// 球队编辑，队长才显示
		} else {
			right.setVisibility(View.GONE);
		}
		if (userType != 1) {
			btnExitTeam.setVisibility(View.VISIBLE);// 可见退出球队
		} else {
			btnExitTeam.setVisibility(View.GONE);
		}
		adapter.setPlayerList(teamInfoComplete.getPlayers());
	}

	/**
	 * 在有完整球队数据的情况下执行的操作，无需请求网络数据
	 */
	private void path4CompleteTeam2() {
		initLike2(teamInfoComplete);
		init_data(teamInfoComplete.getTeam());
		initJerseys(teamInfoComplete.getTeam()); // 队服的滑动显示

		getLatestMatchInfo(teamInfoComplete.getTeam());
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(teamInfoComplete.getTeam()
						.getFamilyPhoto()),
				familyPhoto,
				new DisplayImageOptions.Builder()
						.showImageOnLoading(
								R.drawable.image_download_loading_icon)
						.showImageForEmptyUri(R.drawable.bg_family_dafult)
						// 默认球队全家福
						.showImageOnFail(R.drawable.bg_family_dafult)
						.decodingOptions(ImageLoadOptions.decodingOptions())
						.cacheInMemory(true).cacheOnDisk(true).build());
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(teamInfoComplete.getTeam()
						.getBadge()),
				badge,
				new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.team_bage_default)
						.showImageForEmptyUri(R.drawable.team_bage_default)
						.showImageOnFail(R.drawable.team_bage_default)
						.cacheInMemory(true).cacheOnDisk(true).build());
		teamName.setText(teamInfoComplete.getTeam().getName());// 球队名称
		if (userType == 1 || userType == 2 || userType == 3) {
			right.setImageResource(R.drawable.edit);// 球队编辑，队长才显示
		} else {
			right.setVisibility(View.GONE);
		}
		adapter.setPlayerList(teamInfoComplete.getPlayers());
	}

	/**
	 * 在只有球队简单数据的情况下执行的路径，需要请求网络数据
	 */
	private void path4Team() {
		initLike(onlyTeam);
		init_data(onlyTeam);
		initJerseys(onlyTeam); // 队服的滑动显示
		getAllMatchResultOfTeam(onlyTeam); // 获取总比赛战绩
		getLatestMatchInfo(onlyTeam);
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(onlyTeam.getFamilyPhoto()),
				familyPhoto,
				new DisplayImageOptions.Builder()
						.showImageOnLoading(
								R.drawable.image_download_loading_icon)
						.showImageForEmptyUri(R.drawable.bg_family_dafult)
						.decodingOptions(ImageLoadOptions.decodingOptions())
						// 默认球队全家福
						.showImageOnFail(R.drawable.bg_family_dafult)
						.cacheInMemory(true).cacheOnDisk(true).build());
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(onlyTeam.getBadge()),
				badge,
				new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.team_bage_default)
						.showImageForEmptyUri(R.drawable.team_bage_default)
						.showImageOnFail(R.drawable.team_bage_default)
						.cacheInMemory(true).cacheOnDisk(true).build());// 队徽
		teamName.setText(onlyTeam.getName());// 球队名称
		if (userType == 1 || userType == 2 || userType == 3) {
			right.setImageResource(R.drawable.edit);// 球队编辑 队长才显示
		} else {
			right.setVisibility(View.GONE);
		}
		if (userType != 1) {
			btnExitTeam.setVisibility(View.VISIBLE);
		} else {
			btnExitTeam.setVisibility(View.GONE);
		}
		new LoadPlayerInTeamFromSqlAsyncTask().execute(onlyTeam.getUuid());
		executeGetTeamMemberList();
	}

	/**
	 * 在只有球队简单数据的情况下执行的路径，需要请求网络数据
	 */
	private void path4Team2() {
		initLike(onlyTeam);
		init_data(onlyTeam);
		initJerseys(onlyTeam); // 队服的滑动显示

		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(onlyTeam.getFamilyPhoto()),
				familyPhoto,
				new DisplayImageOptions.Builder()
						.showImageOnLoading(
								R.drawable.image_download_loading_icon)
						.showImageForEmptyUri(R.drawable.bg_family_dafult)
						.decodingOptions(ImageLoadOptions.decodingOptions())
						// 默认球队全家福
						.showImageOnFail(R.drawable.bg_family_dafult)
						.cacheInMemory(true).cacheOnDisk(true).build());
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(onlyTeam.getBadge()),
				badge,
				new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.team_bage_default)
						.showImageForEmptyUri(R.drawable.team_bage_default)
						.showImageOnFail(R.drawable.team_bage_default)
						.cacheInMemory(true).cacheOnDisk(true).build());// 队徽
		teamName.setText(onlyTeam.getName());// 球队名称
		if (userType == 1 || userType == 2 || userType == 3) {
			right.setImageResource(R.drawable.edit);// 球队编辑 队长才显示
		} else {
			right.setVisibility(View.GONE);
		}
		if (userType != 1) {
			btnExitTeam.setVisibility(View.VISIBLE);
		} else {
			btnExitTeam.setVisibility(View.GONE);
		}
	}

	/**
	 * 对球队的喜欢情况 -- 对于只有球队--team
	 * 
	 * @param onlyTeam
	 */
	private void initLike(Team team) {
		like_team_number.setText(String.valueOf(team.getLike()));
		if (team.getLiked() == null ? false : team.getLiked()) {
			liketeam.setImageResource(R.drawable.like1);
		} else {
			liketeam.setImageResource(R.drawable.like2);
		}
	}

	/**
	 * 对球队的喜欢情况 -- 对于完整数据teamInfoComplete
	 * 
	 * @param teamInfoComplete
	 */
	private void initLike2(TeamInfoComplete teamInfoComplete) {
		like_team_number.setText(String.valueOf(teamInfoComplete.getLike()));
		if (teamInfoComplete.isLiked() == null ? false : teamInfoComplete
				.isLiked()) {
			liketeam.setImageResource(R.drawable.like1);
		} else {
			liketeam.setImageResource(R.drawable.like2);
		}
	}

	/**
	 * 初始化队服数据
	 */
	private void initJerseys(final Team team) {
		boolean hasHomeJersey = !TextUtils.isEmpty(team.getHomeJersey());
		boolean hasRoadJersey = !TextUtils.isEmpty(team.getRoadJersey());
		boolean hasAlternateJersey = !TextUtils.isEmpty(team
				.getAlternateJersey());
		jersey_list = new ArrayList<ViewWithPath>();
		if (hasHomeJersey) {// 有主场队服
			View view = LayoutInflater.from(this).inflate(
					R.layout.viewpager_jersey, null);

			RoundImageView img_jersey = (RoundImageView) view
					.findViewById(R.id.team_jersey);
			ImageView jersey_tip1 = (ImageView) view
					.findViewById(R.id.jersey_tip1);
			ImageView jersey_tip2 = (ImageView) view
					.findViewById(R.id.jersey_tip2);
			ImageView jersey_tip3 = (ImageView) view
					.findViewById(R.id.jersey_tip3);
			TextView jersey_text = (TextView) view
					.findViewById(R.id.jersey_txt);
			PictureUtil.getMd5PathByUrl(
					PhotoUtil.getThumbnailPath(team.getHomeJersey()),
					img_jersey);

			img_jersey.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					ArrayList<String> photos = new ArrayList<String>();
					photos.add(PhotoUtil.getAllPhotoPath(team.getHomeJersey()));
					PictureUtil.viewLargerImage(mContext, photos);
				}
			});

			jersey_tip1.setImageResource(R.drawable.xiaodiandian1);
			jersey_text.setText("主场队服");
			if (hasRoadJersey) {
				jersey_tip2.setVisibility(View.VISIBLE);
			} else {
				jersey_tip2.setVisibility(View.GONE);
			}
			if (hasAlternateJersey) {
				jersey_tip3.setVisibility(View.VISIBLE);
			} else {
				jersey_tip3.setVisibility(View.GONE);
			}
			ViewWithPath viewWithPath = new ViewWithPath(view,
					team.getHomeJersey());
			jersey_list.add(viewWithPath);
		}

		if (hasRoadJersey) {// 有客场队服
			View view = LayoutInflater.from(this).inflate(
					R.layout.viewpager_jersey, null);

			RoundImageView img_jersey = (RoundImageView) view
					.findViewById(R.id.team_jersey);
			ImageView jersey_tip1 = (ImageView) view
					.findViewById(R.id.jersey_tip1);
			ImageView jersey_tip2 = (ImageView) view
					.findViewById(R.id.jersey_tip2);
			ImageView jersey_tip3 = (ImageView) view
					.findViewById(R.id.jersey_tip3);
			TextView jersey_text = (TextView) view
					.findViewById(R.id.jersey_txt);
			PictureUtil.getMd5PathByUrl(
					PhotoUtil.getThumbnailPath(team.getRoadJersey()),
					img_jersey);
			img_jersey.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					ArrayList<String> photos = new ArrayList<String>();
					photos.add(PhotoUtil.getAllPhotoPath(team.getRoadJersey()));
					PictureUtil.viewLargerImage(mContext, photos);
				}
			});

			jersey_tip2.setImageResource(R.drawable.xiaodiandian1);
			jersey_text.setText("客场队服");
			if (hasHomeJersey) {
				jersey_tip1.setVisibility(View.VISIBLE);
			} else {
				jersey_tip1.setVisibility(View.GONE);
			}
			if (hasAlternateJersey) {
				jersey_tip3.setVisibility(View.VISIBLE);
			} else {
				jersey_tip3.setVisibility(View.GONE);
			}
			ViewWithPath viewWithPath = new ViewWithPath(view,
					team.getRoadJersey());
			jersey_list.add(viewWithPath);
		}

		if (hasAlternateJersey) {// 有第三队服
			View view = LayoutInflater.from(this).inflate(
					R.layout.viewpager_jersey, null);
			RoundImageView img_jersey = (RoundImageView) view
					.findViewById(R.id.team_jersey);
			ImageView jersey_tip1 = (ImageView) view
					.findViewById(R.id.jersey_tip1);
			ImageView jersey_tip2 = (ImageView) view
					.findViewById(R.id.jersey_tip2);
			ImageView jersey_tip3 = (ImageView) view
					.findViewById(R.id.jersey_tip3);
			TextView jersey_text = (TextView) view
					.findViewById(R.id.jersey_txt);
			PictureUtil.getMd5PathByUrl(
					PhotoUtil.getThumbnailPath(team.getAlternateJersey()),
					img_jersey);
			img_jersey.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					ArrayList<String> photos = new ArrayList<String>();
					photos.add(PhotoUtil.getAllPhotoPath(team
							.getAlternateJersey()));
					PictureUtil.viewLargerImage(mContext, photos);
				}
			});
			jersey_tip3.setImageResource(R.drawable.xiaodiandian1);
			jersey_text.setText("第三队服");
			if (hasHomeJersey) {
				jersey_tip1.setVisibility(View.VISIBLE);
			} else {
				jersey_tip1.setVisibility(View.GONE);
			}
			if (hasRoadJersey) {
				jersey_tip2.setVisibility(View.VISIBLE);
			} else {
				jersey_tip2.setVisibility(View.GONE);
			}
			ViewWithPath viewWithPath = new ViewWithPath(view,
					team.getAlternateJersey());
			jersey_list.add(viewWithPath);
		}

		if (jersey_list.size() == 0) {
			View view = LayoutInflater.from(this).inflate(
					R.layout.viewpager_jersey, null);
			LinearLayout llt_team_jersey = (LinearLayout) view
					.findViewById(R.id.llt_team_jersey);
			RoundImageView img_jersey = (RoundImageView) view
					.findViewById(R.id.team_jersey);
			ImageView jersey_tip1 = (ImageView) view
					.findViewById(R.id.jersey_tip1);
			ImageView jersey_tip2 = (ImageView) view
					.findViewById(R.id.jersey_tip2);
			ImageView jersey_tip3 = (ImageView) view
					.findViewById(R.id.jersey_tip3);
			TextView jersey_text = (TextView) view
					.findViewById(R.id.jersey_txt);
			img_jersey.setVisibility(View.GONE);
			llt_team_jersey.setBackgroundResource(R.drawable.aaa);
			jersey_tip2.setVisibility(View.GONE);
			jersey_tip3.setVisibility(View.GONE);
			jersey_text.setText("没有设置队服");
			ViewWithPath viewWithPath = new ViewWithPath(view,
					team.getAlternateJersey());
			jersey_list.add(viewWithPath);
		}

		viewPager.setAdapter(new MyAdapter());
		viewPager.setOnPageChangeListener(this);
		viewPager.setCurrentItem(0);
	}

	private void initView() {
		nav_title.setText("球队信息");
		// 最近5场所有比赛结果的图标
		match_results[0] = allmatch_result1;
		match_results[1] = allmatch_result2;
		match_results[2] = allmatch_result3;
		match_results[3] = allmatch_result4;
		match_results[4] = allmatch_result5;
	}

	// 获取最新战报
	private void getLatestMatchInfo(Team team) {
		NetWorkManager.getInstance(mContext).getLatestMatchInfo(
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				team.getUuid(), new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Gson gson = new Gson();
						latest_matchInfo = null;
						latest_matchInfo = gson.fromJson(response.toString(),
								new TypeToken<MatchInfo>() {
								}.getType());
						if (latest_matchInfo == null) {
							// TODO
							tv_tip_no_match.setVisibility(View.VISIBLE);
							llt_latest_battle.setVisibility(View.GONE);
						} else {
							// TODO
							tv_tip_no_match.setVisibility(View.GONE);
							llt_latest_battle.setVisibility(View.VISIBLE);
							PictureUtil.getMd5PathByUrl(
									PhotoUtil.getThumbnailPath(latest_matchInfo
											.getHomeTeam().getBadge()),
									iv_home_team_icon,
									new DisplayImageOptions.Builder()
											.showImageOnLoading(
													R.drawable.team_bage_default)
											.showImageForEmptyUri(
													R.drawable.team_bage_default)
											.showImageOnFail(
													R.drawable.team_bage_default)
											.cacheInMemory(true)
											.cacheOnDisk(true).build());// 队徽
							PictureUtil.getMd5PathByUrl(
									PhotoUtil.getThumbnailPath(latest_matchInfo
											.getAwayTeam().getBadge()),
									iv_away_team_icon,
									new DisplayImageOptions.Builder()
											.showImageOnLoading(
													R.drawable.team_bage_default)
											.showImageForEmptyUri(
													R.drawable.team_bage_default)
											.showImageOnFail(
													R.drawable.team_bage_default)
											.cacheInMemory(true)
											.cacheOnDisk(true).build());// 队徽
							tv_home_team_name.setText(latest_matchInfo
									.getHomeTeam().getName());
							tv_away_team_name.setText(latest_matchInfo
									.getAwayTeam().getName());
							tv_match_date.setText(StringUtils.defaultIfEmpty(
									DateUtil.parseTimeInMillis_no_ss(latest_matchInfo
											.getKickOff()), ""));// 比赛时间
							tv_match_field.setText(latest_matchInfo.getField());
							tv_score.setText(latest_matchInfo
									.getHomeTeamScore()
									+ "  "
									+ ":"
									+ "  "
									+ latest_matchInfo.getAwayTeamScore());
						}

					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("当前网络不可用");
						} else if (error.networkResponse == null) { // --没有数据显示暂无比赛
							// ShowToast("TeamInfoActivity-getLatestMatchInfo-服务器连接错误");
							tv_tip_no_match.setVisibility(View.VISIBLE);
							llt_latest_battle.setVisibility(View.GONE);
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else if (error.networkResponse.statusCode == 404) {
							ShowToast("球队找不到");
						}
					}
				});
	}

	// 获取队伍的所有比赛结果
	private void getAllMatchResultOfTeam(Team team2) {
		NetWorkManager.getInstance(mContext).getAllMatchOfTeam(
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				team2.getUuid(), new Listener<JSONObject>() {

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
						allmatch_won.setText(String.valueOf(matchRecords
								.getRecord().getWon()));// 胜
						allmatch_lost.setText(String.valueOf(matchRecords
								.getRecord().getLost()));// 负
						allmatch_drawn.setText(String.valueOf(matchRecords
								.getRecord().getDrawn()));// 平
						Log.e("matchRecords.getDrawn()", ""
								+ matchRecords.getRecord().getDrawn());
						allmatch_scored.setText(String.valueOf(matchRecords
								.getRecord().getScored()));// 进球
						allmatch_against.setText(String.valueOf(matchRecords
								.getRecord().getAgainst()));// 失球
						if (matchRecords.getRecord().getScored()
								- matchRecords.getRecord().getAgainst() > 0) {// 净胜球为正数
							allmatch_scored_jing
									.setText("净胜球   +"
											+ (matchRecords.getRecord()
													.getScored() - matchRecords
													.getRecord().getAgainst()));// 净胜球
						} else {
							allmatch_scored_jing
									.setText("净胜球   "
											+ (matchRecords.getRecord()
													.getScored() - matchRecords
													.getRecord().getAgainst()));// 净胜球
						}

						// 获取最近最多5场总比赛的结果
						if (matchRecords.getResults().size() == 0) {

						} else {
							last_five_result_tv.setVisibility(View.GONE);
							for (int i = 0; i < matchRecords.getResults()
									.size(); i++) {
								switch (MatchResultEnum
										.getEnumFromString(matchRecords
												.getResults().get(i))) {
								case WIN:
									match_results[i]
											.setImageResource(R.drawable.win);
									break;
								case DRAW:
									match_results[i]
											.setImageResource(R.drawable.equal);
									break;
								case LOSS:
									match_results[i]
											.setImageResource(R.drawable.falie);
									break;
								case NULL:
									match_results[i].setImageResource(0);// 显示空
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
							// ShowToast("TeamInfoActivity-getAllMatchResultOfTeam-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else if (error.networkResponse.statusCode == 404) {
							// ShowToast("球队找不到");
						}
					}
				});
	}

	/**
	 * 联网获取某个球队的球员
	 */
	private void executeGetTeamMemberList() {
		NetWorkManager.getInstance(mContext).getTeamMember(onlyTeam.getUuid(),
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<JSONArray>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onResponse(JSONArray response) {
						Gson gson = new Gson();
						List<PlayerInTeam> myTeamList = null;
						try {
							myTeamList = gson.fromJson(response.toString(),
									new TypeToken<ArrayList<PlayerInTeam>>() {
									}.getType());
						} catch (JsonSyntaxException e) {
							myTeamList = null;
							e.printStackTrace();
						}
						// 将获取得到的球队成员列表数据放入到本地数据库中，缓存
						if (myTeamList != null && myTeamList.size() != 0) {
							synchronized (object) {
								adapter.setPlayerList(myTeamList);
								if (teamConstruct != null) {
									teamConstruct.setPlayers(myTeamList);
								}
							}
							new WriteTeamMebToSqlAsyncTask()
									.execute(myTeamList);
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							// ShowToast("当前网络不可用");
						} else if (error.networkResponse == null) {
							// ShowToast("TeamInfoActivity-executeGetTeamMemberList-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else if (error.networkResponse.statusCode == 404) {
							// ShowToast("球队找不到");
						}
					}
				});
	}

	/**
	 * 保存联网获取的球队成员数据到数据库
	 * 
	 * @author Sola
	 * 
	 */
	class WriteTeamMebToSqlAsyncTask extends
			AsyncTask<List<PlayerInTeam>, Void, Void> {

		@Override
		protected Void doInBackground(List<PlayerInTeam>... params) {
			DBUtil.saveTeamMember2DB(mContext, params[0], onlyTeam);
			return null;
		}
	}

	/**
	 * 从数据库加载球队的球员信息
	 * 
	 * @author Sola
	 * 
	 */
	@Deprecated
	class LoadTeamMebFromSqlAsyncTask extends
			AsyncTask<String, Void, List<Player>> {

		@Override
		protected List<Player> doInBackground(String... params) {
			String teamUuid = params[0];
			return DBUtil.LoadTeamMemberFromDB(mContext, teamUuid);
		}

		@Override
		protected void onPostExecute(List<Player> result) {// TODO
			super.onPostExecute(result);
			if (result.size() != 0 && result != null) {
				List<PlayerInTeam> teamMebList = new ArrayList<PlayerInTeam>();

				for (Player item : result) {
					PlayerInTeam response = new PlayerInTeam();
					response.setPlayer(item);
					teamMebList.add(response);
				}
				synchronized (object) {
					if (teamConstruct != null) {
						teamConstruct.setPlayers(teamMebList);
					}
					adapter.setPlayerList(teamMebList);
				}
			}
		}
	}

	/**
	 * 从数据库加载球队的球员信息 --PlayerInTeam
	 * 
	 * @author zyq
	 * 
	 */
	@Deprecated
	class LoadPlayerInTeamFromSqlAsyncTask extends
			AsyncTask<String, Void, List<PlayerInTeam>> {

		@Override
		protected List<PlayerInTeam> doInBackground(String... params) {
			String teamUuid = params[0];
			return DBUtil.getTeamPlayersByTeamID(mContext, teamUuid);
		}

		@Override
		protected void onPostExecute(List<PlayerInTeam> result) {// TODO
			super.onPostExecute(result);
			if (result.size() != 0 && result != null) {

				adapter.setPlayerList(result);
			}
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (REQUEST_CODE_EDIT_TEAM_INFO == requestCode) {// 编辑球队信息返回---完整信息
			boolean needToRefresh = intent.getBooleanExtra(
					RESULT_NEED_TO_REFRESH, false);
			if (needToRefresh) {
				isNeedToRefresh = true;
				TeamInfoComplete tmpTeam = intent
						.getParcelableExtra(RESULT_RETURN_TEAM_BEAN);
				if (tmpTeam != null) {// 刷新页面
					teamInfoComplete = tmpTeam;
					userType = judgeUserType(teamInfoComplete.getTeam());
					initLike(teamInfoComplete.getTeam());
					init_data(teamInfoComplete.getTeam());
					initJerseys(teamInfoComplete.getTeam()); // 队服的滑动显示
					PictureUtil
							.getMd5PathByUrl(
									PhotoUtil.getThumbnailPath(teamInfoComplete
											.getTeam().getFamilyPhoto()),
									familyPhoto,
									new DisplayImageOptions.Builder()
											.showImageOnLoading(
													R.drawable.image_download_loading_icon)
											.showImageForEmptyUri(
													R.drawable.bg_family_dafult)
											// 默认球队全家福
											.showImageOnFail(
													R.drawable.bg_family_dafult)
											.cacheInMemory(true)
											.cacheOnDisk(true).build());
					PictureUtil.getMd5PathByUrl(
							PhotoUtil.getThumbnailPath(teamInfoComplete
									.getTeam().getBadge()),
							badge,
							new DisplayImageOptions.Builder()
									.showImageOnLoading(
											R.drawable.team_bage_default)
									.showImageForEmptyUri(
											R.drawable.team_bage_default)
									.showImageOnFail(
											R.drawable.team_bage_default)
									.cacheInMemory(true).cacheOnDisk(true)
									.build());
					teamName.setText(teamInfoComplete.getTeam().getName());// 球队名称
					if (userType == 1 || userType == 2 || userType == 3) {
						right.setVisibility(View.VISIBLE);
						right.setImageResource(R.drawable.edit);// 球队编辑 TODO//
																// 是否队长才显示//
																// 或者判断跳转
					} else {
						right.setVisibility(View.GONE);
					}
					adapter.setPlayerList(teamInfoComplete.getPlayers());

				}
			}
			return;
		}

		if (REQUEST_CODE_EDIT_ONLY_TEAM_INFO == requestCode) {// onlyTeam
			boolean needToRefresh = intent.getBooleanExtra(
					RESULT_NEED_TO_REFRESH, false);
			if (needToRefresh) {
				isNeedToRefresh = true;
				TeamInfoComplete tmpTeam = intent
						.getParcelableExtra(RESULT_RETURN_TEAM_BEAN);
				if (tmpTeam != null) {// 刷新页面
					onlyTeam = tmpTeam.getTeam();
					teamConstruct = tmpTeam;
					userType = judgeUserType(onlyTeam);
					initLike(onlyTeam);
					init_data(onlyTeam);
					initJerseys(onlyTeam); // 队服的滑动显示
					PictureUtil
							.getMd5PathByUrl(
									PhotoUtil.getThumbnailPath(onlyTeam
											.getFamilyPhoto()),
									familyPhoto,
									new DisplayImageOptions.Builder()
											.showImageOnLoading(
													R.drawable.image_download_loading_icon)
											.showImageForEmptyUri(
													R.drawable.bg_family_dafult)
											// 默认球队全家福
											.showImageOnFail(
													R.drawable.bg_family_dafult)
											.cacheInMemory(true)
											.cacheOnDisk(true).build());
					PictureUtil.getMd5PathByUrl(
							PhotoUtil.getThumbnailPath(onlyTeam.getBadge()),
							badge,
							new DisplayImageOptions.Builder()
									.showImageOnLoading(
											R.drawable.team_bage_default)
									.showImageForEmptyUri(
											R.drawable.team_bage_default)
									.showImageOnFail(
											R.drawable.team_bage_default)
									.cacheInMemory(true).cacheOnDisk(true)
									.build());
					teamName.setText(onlyTeam.getName());// 球队名称
					if (userType == 1 || userType == 2 || userType == 3) {
						right.setVisibility(View.VISIBLE);
						right.setImageResource(R.drawable.edit);// 球队编辑 TODO//
																// 是否队长才显示//
																// 或者判断跳转
					} else {
						right.setVisibility(View.GONE);
					}
					adapter.setPlayerList(teamConstruct.getPlayers());
				}
			}
			return;
		}
		// 更改号码后 刷新球员列表 （更新号码） --对于 teamInfoComplete
		if (requestCode == TeamMemberListActivity.TEAMMEMBER_NUMBER_CHANGE) {
			TeamInfoComplete tmpTeam = intent
					.getParcelableExtra(TeamMemberListActivity.TEAMINFOCOMPLETE_TEAMMEMBER_NUMBER_CHANGE);
			if (tmpTeam != null) {
				isNeedToRefresh = true;
				teamInfoComplete = tmpTeam;
				adapter.setPlayerList(tmpTeam.getPlayers());
			}
			return;
		}

		// 更改号码后 刷新球员列表 （更新号码） --对于 only_team
		if (requestCode == TeamMemberListActivity.TEAMMEMBER_NUMBER_CHANGE_ONLY_TEAM) {
			List<PlayerInTeam> memberList_onlyTeam = intent
					.getParcelableArrayListExtra(TeamMemberListActivity.ONLY_TEAM_TEAMMEMBER_NUMBER_CHANGE);
			if (memberList_onlyTeam != null) {
				isNeedToRefresh = true;
				adapter.setPlayerList(memberList_onlyTeam);
			}
			return;
		}
		
		if (REQUEST_CODE_CREATE_DEFAULT_FORMATION == requestCode) {
			Log.e("TAG", "2");
			toFormationList();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	/**
	 * viewpager的元素
	 * 
	 * @author Sola
	 * 
	 */
	class ViewWithPath {
		private View view;
		private String path;// 队服的图片url

		public ViewWithPath(View view, String path) {
			super();
			this.view = view;
			this.path = path;
		}

		public View getView() {
			return view;
		}

		public void setView(View view) {
			this.view = view;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

	}

	/**
	 * 队服的pager适配器
	 * 
	 * @author Sola
	 * 
	 */
	public class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return jersey_list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			// TODO Auto-generated method stub
			((ViewPager) container)
					.addView(jersey_list.get(position).getView());
			return jersey_list.get(position).getView();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			((ViewPager) container).removeView(jersey_list.get(position)
					.getView());
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageSelected(int selectItems) {
		// TODO Auto-generated method stub
	}

	/**
	 * 修改球队名称的事件
	 */
	public void onEventMainThread(ModifyTeamNameEvent modifyTeamNameEvent) {
		if (isCompleteInfo) {
			teamInfoComplete.getTeam().setName(
					modifyTeamNameEvent.getNewTeamName());
		} else {
			onlyTeam.setName(modifyTeamNameEvent.getNewTeamName());
		}
		teamName.setText(StringUtils.defaultIfEmpty(
				modifyTeamNameEvent.getNewTeamName(), ""));
	}

	/**
	 * 修改球队城市的事件
	 * 
	 * @param modifyTeamCityEvent
	 */
	public void onEventMainThread(ModifyTeamCityEvent modifyTeamCityEvent) {
		if (isCompleteInfo) {
			teamInfoComplete.getTeam().setCityId(
					modifyTeamCityEvent.getNewTeamCityId());
			teamInfoComplete.getTeam().setRegionId(-1);
		} else {
			onlyTeam.setCityId(modifyTeamCityEvent.getNewTeamCityId());
			onlyTeam.setRegionId(-1);
		}
		teamName.setText(StringUtils.defaultIfEmpty(
				modifyTeamCityEvent.getNewTeamCityName(), ""));
	}

	/**
	 * 修改球队地区的事件
	 * 
	 * @param modifyTeamRegionEvent
	 */
	public void onEventMainThread(ModifyTeamRegionEvent modifyTeamRegionEvent) {
		if (isCompleteInfo) {
			teamInfoComplete.getTeam().setRegionId(
					modifyTeamRegionEvent.getNewRegionId());
		} else {
			onlyTeam.setRegionId(modifyTeamRegionEvent.getNewRegionId());
		}
		team_place.setText(StringUtils.defaultIfEmpty(
				modifyTeamRegionEvent.getNewRegionName(), " "));
	}

	/**
	 * 修改球队介绍事件
	 * 
	 * @param modifyTeamDescriptionEvent
	 */
	public void onEventMainThread(
			ModifyTeamDescriptionEvent modifyTeamDescriptionEvent) {
		if (isCompleteInfo) {
			teamInfoComplete.getTeam().setDescription(
					modifyTeamDescriptionEvent.getNewDescription());
		} else {
			onlyTeam.setDescription(modifyTeamDescriptionEvent
					.getNewDescription());
		}
		teamDescriptionText.setText(StringUtils.defaultString(
				modifyTeamDescriptionEvent.getNewDescription(), ""));
	}

	/**
	 * 编辑球队口号
	 * 
	 * @param modifyTeamSloganEvent
	 */
	public void onEventMainThread(ModifyTeamSloganEvent modifyTeamSloganEvent) {
		if (isCompleteInfo) {
			teamInfoComplete.getTeam().setSlogan(
					modifyTeamSloganEvent.getNewSlogan());
		} else {
			onlyTeam.setSlogan(modifyTeamSloganEvent.getNewSlogan());
		}
		teamSloganText.setText(StringUtils.defaultIfEmpty(
				modifyTeamSloganEvent.getNewSlogan(), ""));
	}

	/**
	 * 修改主场队服 TODO
	 * 
	 * @param modifyTeamHomeJerseyEvent
	 */
	public void onEventMainThread(
			ModifyTeamHomeJerseyEvent modifyTeamHomeJerseyEvent) {
		// jersey_list.clear();// = new ArrayList<ViewWithPath>(); //
		// 重新new一个新的对象，以免数据重复加载
		jersey_list = new ArrayList<ViewWithPath>();
		if (isCompleteInfo) {
			teamInfoComplete.getTeam().setHomeJersey(
					modifyTeamHomeJerseyEvent.getNewHomeJersey());
			initJerseys(teamInfoComplete.getTeam()); // TODO--只更新主场队服即可
		} else {
			onlyTeam.setHomeJersey(modifyTeamHomeJerseyEvent.getNewHomeJersey());
			initJerseys(onlyTeam); // TODO -- 只更新主场队服即可
		}
	}

	/**
	 * 修改客场队服
	 * 
	 * @param modifyTeamRoadJerseyEvent
	 */
	public void onEventMainThread(
			ModifyTeamRoadJerseyEvent modifyTeamRoadJerseyEvent) {
		jersey_list = new ArrayList<ViewWithPath>(); // 重新new一个新的对象，以免数据重复加载
		if (isCompleteInfo) {
			teamInfoComplete.getTeam().setRoadJersey(
					modifyTeamRoadJerseyEvent.getNewRoadJersey());
			initJerseys(teamInfoComplete.getTeam());
		} else {
			onlyTeam.setRoadJersey(modifyTeamRoadJerseyEvent.getNewRoadJersey());
			initJerseys(onlyTeam);
		}
	}

	/**
	 * 修改球队第三队服
	 * 
	 * @param modifyTeamAlternetJerseyEvent
	 */
	public void onEventMainThread(
			ModifyTeamAlternetJerseyEvent modifyTeamAlternetJerseyEvent) {
		jersey_list = new ArrayList<ViewWithPath>();
		if (isCompleteInfo) {
			teamInfoComplete.getTeam().setAlternateJersey(
					modifyTeamAlternetJerseyEvent.getNewAlternetJersey());
			initJerseys(teamInfoComplete.getTeam());
		} else {
			onlyTeam.setAlternateJersey(modifyTeamAlternetJerseyEvent
					.getNewAlternetJersey());
			initJerseys(onlyTeam);
		}
	}

	/**
	 * 修改全家福事件
	 * 
	 * @param modifyTeamFamilyPhotoEvent
	 */
	public void onEventMainThread(
			ModifyTeamFamilyPhotoEvent modifyTeamFamilyPhotoEvent) {
		if (isCompleteInfo) {
			teamInfoComplete.getTeam().setFamilyPhoto(
					modifyTeamFamilyPhotoEvent.getNewFamilyPhoto());
		} else {
			onlyTeam.setFamilyPhoto(modifyTeamFamilyPhotoEvent
					.getNewFamilyPhoto());
		}
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(modifyTeamFamilyPhotoEvent
						.getNewFamilyPhoto()),
				familyPhoto,
				new DisplayImageOptions.Builder()
						.showImageOnLoading(
								R.drawable.image_download_loading_icon)
						.showImageForEmptyUri(R.drawable.default_family_photo)
						// 默认球队全家福
						.showImageOnFail(R.drawable.default_family_photo)
						.cacheInMemory(true).cacheOnDisk(true).build());
	}

	/**
	 * 编辑球队队徽
	 * 
	 * @param modifyTeamBadgeEvent
	 */
	public void onEventMainThread(ModifyTeamBadgeEvent modifyTeamBadgeEvent) {
		if (isCompleteInfo) {
			teamInfoComplete.getTeam().setBadge(
					modifyTeamBadgeEvent.getNewBadge());
		} else {
			onlyTeam.setBadge(modifyTeamBadgeEvent.getNewBadge());
		}
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(modifyTeamBadgeEvent.getNewBadge()),
				badge,
				new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.team_bage_default)
						.showImageForEmptyUri(R.drawable.team_bage_default)
						.showImageOnFail(R.drawable.team_bage_default)
						.cacheInMemory(true).cacheOnDisk(true).build());// 队徽
	}

	/**
	 * 修改第一队长
	 * 
	 * @param modifyTeamFirstCaptainEvent
	 */
	public void onEventMainThread(
			ModifyTeamFirstCaptainEvent modifyTeamFirstCaptainEvent) {
		if (isCompleteInfo) {
			teamInfoComplete.getTeam().setFirstCaptainUuid(
					modifyTeamFirstCaptainEvent.getNewCaptainUuid());
			showBtnOrNot(teamInfoComplete.getTeam());
		} else {
			onlyTeam.setFirstCaptainUuid(modifyTeamFirstCaptainEvent
					.getNewCaptainUuid());
			showBtnOrNot(onlyTeam);
		}
	}

	/**
	 * 修改队副事件
	 * 
	 * @param modifyTeamSecondCaptainEvent
	 */
	public void onEventMainThread(
			ModifyTeamSecondCaptainEvent modifyTeamSecondCaptainEvent) {
		if (isCompleteInfo) { // TODO
			teamInfoComplete.getTeam().setSecondCaptainUuid(
					modifyTeamSecondCaptainEvent.getNewCaptainUuid());
			showBtnOrNot(teamInfoComplete.getTeam());
		} else {
			onlyTeam.setSecondCaptainUuid(modifyTeamSecondCaptainEvent
					.getNewCaptainUuid());
			showBtnOrNot(onlyTeam);
		}
	}

	/**
	 * 修改教练事件
	 * 
	 * @param modifyTeamThirdCaptainEvent
	 */
	public void onEventMainThread(
			ModifyTeamThirdCaptainEvent modifyTeamThirdCaptainEvent) {
		if (isCompleteInfo) {
			teamInfoComplete.getTeam().setThirdCaptainUuid(
					modifyTeamThirdCaptainEvent.getNewCaptainUuid());
			showBtnOrNot(teamInfoComplete.getTeam());
		} else {
			onlyTeam.setThirdCaptainUuid(modifyTeamThirdCaptainEvent
					.getNewCaptainUuid());
			showBtnOrNot(onlyTeam);
		}
	}

	private void showBtnOrNot(Team team) {
		int userType = judgeUserType(team);
		if (userType == 1 || userType == 2 || userType == 3) {
			right.setImageResource(R.drawable.edit);
		} else {
			right.setVisibility(View.GONE);
		}
	}
}
