package com.kinth.football.ui.match;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.bean.InvitationResponse;
import com.kinth.football.bean.MyInvitationResponse;
import com.kinth.football.bean.match.FriendMatch;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.chat.ui.TeamActivity;
import com.kinth.football.config.Action;
import com.kinth.football.config.MatchStateEnum;
import com.kinth.football.config.MatchTypeEnum;
import com.kinth.football.dao.Match;
import com.kinth.football.dao.MatchDao;
import com.kinth.football.dao.Team;
import com.kinth.football.eventbus.bean.CreateFriendlyMatchEvent;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.ui.match.invite.InviteMatchConstants;
import com.kinth.football.ui.team.TeamInfoActivity;
import com.kinth.football.ui.team.TeamInfoForGuestActivity;
import com.kinth.football.ui.team.TeamPlayerInfo;
import com.kinth.football.util.DBUtil;
import com.kinth.football.util.DateUtil;
import com.kinth.football.util.DialogUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.view.RoundImageView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

/**
 * 创建中的比赛详情页面
 * 
 * @author Sola
 */
@ContentView(R.layout.activity_match_detail_on_invitation)
public class MatchDetailOnInvitationStateActivity extends BaseActivity {
	public static final String INTENT_MATCH_DETAIL_BEAN = "INTENT_MATCH_DETAIL_BEAN";// intent中传递的比赛数据信息

	public static final String MATCH_INVITATION_CONFIRM = "COMFIRM"; // 比赛邀请确认（标识）
	public static final String MATCH_INVITATION_REFUSE = "REFUSE"; // 比赛邀请拒绝（标识）

	private static final int REQUEST_CODE_RECEIPT_HOMETEAM_INFO = 9001; // 查看主队球队信息请求码
	private static final int REQUEST_CODE_RECEIPT_AWAYTEAM_INFO = 9002; // 查看客队球队信息请求码

	private ProgressDialog progressDia;

	private String invitation_id = null;

	private MatchInfo matchInfo;

	@ViewInject(R.id.entire_layout)
	private View entireLayout;

	@ViewInject(R.id.nav_left)
	private ImageButton back;

	@ViewInject(R.id.nav_title)
	private TextView title;

	@ViewInject(R.id.tv_home_team_name)
	private TextView homeTeamName;// 主队名称

	@ViewInject(R.id.iv_home_team_badge)
	private ImageView homeTeamBadge;// 主队队徽

	@ViewInject(R.id.tv_away_team_name)
	private TextView awayTeamName;// 客队名称

	@ViewInject(R.id.iv_away_team_badge)
	private ImageView awayTeamBadge;// 客队队徽

	@ViewInject(R.id.tv_match_field)
	private TextView matchField;// 比赛场地

	@ViewInject(R.id.tv_match_date)
	private TextView matchDate;// 比赛时间

	@ViewInject(R.id.tv_match_player_count)
	private TextView matchPlayerCount;

	@ViewInject(R.id.tv_referee_name)
	private TextView refereeName;// 裁判名字

	@ViewInject(R.id.refree_avatar)
	private RoundImageView refree_avatar;// 裁判头像

	@ViewInject(R.id.tv_match_cost)
	private TextView matchCost;// 费用

	@ViewInject(R.id.tv_match_description)
	private TextView matchDescription;// 比赛描述

	@ViewInject(R.id.llt_agress_or_refuse)
	private LinearLayout llt_agress_or_refuse; // 同意参加比赛

	@ViewInject(R.id.agress_to_play)
	private Button agress_to; // 同意参加比赛

	@ViewInject(R.id.refuse_to_play)
	private Button refuse_to; // 拒绝参加比赛

	@ViewInject(R.id.llt_sign)
	private LinearLayout llt_sign; // 应战

	@ViewInject(R.id.sign_to_play)
	private Button sign_to_play; // 应战

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

	// 同意参加比赛
	@OnClick(R.id.agress_to_play)
	public void fun_4(View v) {
		// 先通过getMyMatchInvitation()方法获取得到该邀请信息的id
		getMyMatchInvitation(MATCH_INVITATION_CONFIRM);
	}

	// 拒绝参加比赛
	@OnClick(R.id.refuse_to_play)
	public void fun_5(View v) {
		getMyMatchInvitation(MATCH_INVITATION_REFUSE);
	}

	// 选择球队应战
	@OnClick(R.id.sign_to_play)
	public void fun_8(View v) {
		Intent intent = new Intent(mContext, TeamActivity.class);
		intent.setAction(InviteMatchConstants.INTENT_ACTION_PICK_TEAM);
		startActivityForResult(intent, InviteMatchConstants.REQUEST_CODE_TEAM);
	}

	// 点击主队队徽
	@OnClick(R.id.iv_home_team_badge)
	public void fun_6(View v) {
		Intent intent = null;
		if(matchInfo==null)
			return;
					
		if (DBUtil.isGuest(mContext, matchInfo.getHomeTeam())) { // 来宾
			intent = new Intent(mContext, TeamInfoForGuestActivity.class);
			intent.putExtra(TeamInfoForGuestActivity.INTENT_TEAM_INFO_BEAN,
					matchInfo.getHomeTeam());
			intent.putExtra(
					TeamInfoForGuestActivity.INTENT_NEET_TO_RETURN_BEAN, true);
			intent.putExtra(
					TeamInfoForGuestActivity.INTENT_NEED_TO_GET_CYBER_NEW, true);
			startActivityForResult(intent, REQUEST_CODE_RECEIPT_HOMETEAM_INFO);
		} else { // 成员
			intent = new Intent(mContext, TeamInfoActivity.class);
			intent.putExtra(TeamInfoActivity.INTENT_TEAM_INFO_BEAN,
					matchInfo.getHomeTeam());
			intent.putExtra(TeamInfoActivity.INTENT_NEET_TO_RETURN_BEAN, true);
			intent.putExtra(TeamInfoActivity.INTENT_NEED_TO_GET_CYBER_NEW, true);
			startActivityForResult(intent, REQUEST_CODE_RECEIPT_HOMETEAM_INFO);
		}
	}

	// 点击客队队徽
	@OnClick(R.id.iv_away_team_badge)
	public void fun_7(View v) {
		Intent intent = null;
		if(matchInfo==null)
			return;
		if (matchInfo.getAwayTeam() == null)
			return;
		if (DBUtil.isGuest(mContext, matchInfo.getAwayTeam())) { // 来宾
			intent = new Intent(mContext, TeamInfoForGuestActivity.class);
			intent.putExtra(TeamInfoForGuestActivity.INTENT_TEAM_INFO_BEAN,
					matchInfo.getAwayTeam());
			intent.putExtra(
					TeamInfoForGuestActivity.INTENT_NEET_TO_RETURN_BEAN, true);
			intent.putExtra(
					TeamInfoForGuestActivity.INTENT_NEED_TO_GET_CYBER_NEW, true);
			startActivityForResult(intent, REQUEST_CODE_RECEIPT_AWAYTEAM_INFO);
		} else {// 成员
			intent = new Intent(mContext, TeamInfoActivity.class);
			intent.putExtra(TeamInfoActivity.INTENT_TEAM_INFO_BEAN,
					matchInfo.getAwayTeam());
			intent.putExtra(TeamInfoActivity.INTENT_NEET_TO_RETURN_BEAN, true);
			intent.putExtra(TeamInfoActivity.INTENT_NEED_TO_GET_CYBER_NEW, true);
			startActivityForResult(intent, REQUEST_CODE_RECEIPT_AWAYTEAM_INFO);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(entireLayout, new BitmapDrawable(
				getResources(), background));

		title.setText("比赛详情");
		matchInfo = getIntent().getParcelableExtra(INTENT_MATCH_DETAIL_BEAN);
		if (matchInfo == null) {
			llt_agress_or_refuse.setVisibility(View.GONE);
			return;
		}
		initData();
		// 判断显示“参加”和“拒绝”按钮
		showOrhideBtn();
	}

	private void initData() {
		homeTeamName.setText(matchInfo.getHomeTeam().getName());
		if (matchInfo.getAwayTeam() != null)
			awayTeamName.setText(matchInfo.getAwayTeam().getName());

		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(matchInfo.getHomeTeam().getBadge()),
				homeTeamBadge, new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.team_bage_default)
						.showImageForEmptyUri(R.drawable.team_bage_default)
						.showImageOnFail(R.drawable.team_bage_default).build());// 主队队徽
		if (matchInfo.getAwayTeam() == null) { // TODO
			awayTeamName.setText("等待应战");
			awayTeamBadge
					.setImageResource(R.drawable.team_bage_default_no_team);
		} else {
			PictureUtil.getMd5PathByUrl(
					PhotoUtil.getThumbnailPath(matchInfo.getAwayTeam()
							.getBadge()),
					awayTeamBadge,
					new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.team_bage_default)
							.showImageForEmptyUri(R.drawable.team_bage_default)
							.showImageOnFail(R.drawable.team_bage_default)
							.build());// 客队队徽
		}
		PictureUtil
				.getMd5PathByUrl(
						PhotoUtil.getThumbnailPath(matchInfo.getReferee()
								.getPicture()),
						refree_avatar,
						new DisplayImageOptions.Builder()
								.showImageOnLoading(
										R.drawable.icon_default_head)
								.showImageForEmptyUri(
										R.drawable.icon_default_head)
								.showImageOnFail(R.drawable.icon_default_head)
								.build());

		matchDescription.setText(matchInfo.getName());
		matchField
				.setText(StringUtils.defaultIfBlank(matchInfo.getField(), ""));
		matchDate
				.setText(StringUtils.defaultIfEmpty(DateUtil
						.parseTimeInMillis_hadweek(matchInfo.getKickOff()), ""));// 比赛时间
		matchPlayerCount.setText(matchInfo.getPlayerCount() + "人");
		refereeName.setText(matchInfo.getReferee().getName());
		if (matchInfo.getCost() == 0) {
			matchCost.setText("— —");
		} else {
			matchCost.setText((int) matchInfo.getCost() + "元");
		}
	}

	private void showOrhideBtn() {
		String currentUserUid = UserManager.getInstance(mContext)
				.getCurrentUser().getPlayer().getUuid();
		if (currentUserUid.equals(matchInfo.getCreator().getUuid())) {// 是创建者
			if (matchInfo.getState().equals("INVITING")) {// 非约赛则隐藏
				llt_agress_or_refuse.setVisibility(View.GONE);
				agress_to.setVisibility(View.GONE); // 隐藏“参加”按钮
				refuse_to.setVisibility(View.GONE); // 隐藏“拒绝”按钮
			} else if (matchInfo.getState().equals("CHALLENGE")){//有人应战状态
				int userType = judgeUserType(matchInfo.getHomeTeam());
				if(userType == 1 || userType == 2 || userType == 3){//队长权限
					llt_agress_or_refuse.setVisibility(View.VISIBLE);
					agress_to.setVisibility(View.VISIBLE);
					refuse_to.setVisibility(View.VISIBLE);
				}
			} else if (matchInfo.getState().equals("CALL_FOR")){//等待应战状态
				llt_agress_or_refuse.setVisibility(View.GONE);
				agress_to.setVisibility(View.GONE); // 隐藏“参加”按钮
				refuse_to.setVisibility(View.GONE); // 隐藏“拒绝”按钮
			}
		} else {
			if (matchInfo.getState().equals("INVITING")){//等待应战状态
				llt_agress_or_refuse.setVisibility(View.VISIBLE);
				agress_to.setVisibility(View.VISIBLE);
				refuse_to.setVisibility(View.VISIBLE);
			}else{
				llt_agress_or_refuse.setVisibility(View.GONE);
				agress_to.setVisibility(View.GONE); // 隐藏“参加”按钮
				refuse_to.setVisibility(View.GONE); // 隐藏“拒绝”按钮
			}
		}
		if (matchInfo.getAwayTeam() == null){// TODO 无对手，则显示应战按钮
			if (currentUserUid.equals(matchInfo.getCreator().getUuid())) {// 是创建者{
				llt_sign.setVisibility(View.GONE);
			}else{
				llt_sign.setVisibility(View.VISIBLE);
			}
		}
	}

	// 回复是否同意比赛
	private void getMyMatchInvitation(final String confirmOrrefuse) {
		progressDia = ProgressDialog.show(mContext, "提示", "请稍后...", false,
				false);
		if (matchInfo.getState().equals(MatchStateEnum.INVITING.getValue())) {// 非约赛，客队选择是否应战
			NetWorkManager.getInstance(mContext).getMyMatchInvitation(
					0,
					100,
					matchInfo.getUuid(),
					matchInfo.getAwayTeam().getUuid(),
					UserManager.getInstance(mContext).getCurrentUser()
							.getToken(), new Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							Gson gson = new Gson();
							MyInvitationResponse inviRe = null;
							InvitationResponse invitationRe = null;
							inviRe = gson.fromJson(response.toString(),
									new TypeToken<MyInvitationResponse>() {
									}.getType());
							if (inviRe != null) {
								invitationRe = inviRe.getInvitations().get(0);

								invitation_id = invitationRe
										.getInvitationUuid();

								if (confirmOrrefuse
										.equals(MATCH_INVITATION_CONFIRM)) {
									confirm(invitation_id);
								} else if (confirmOrrefuse
										.equals(MATCH_INVITATION_REFUSE)) {
									refuse(invitation_id);
								}
							}
						}
					}, new ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							if (!NetWorkManager.getInstance(mContext)
									.isNetConnected()) {
								ShowToast("当前网络不可用");
								return;
							}
							if (error.networkResponse == null) {
								// ShowToast("MatchDetailOnInvitationStateActivity-getMyMatchInvitation-服务器连接错误");
								return;
							} else if (error.networkResponse.statusCode == 401) {
								ErrorCodeUtil.ErrorCode401(mContext);
								return;
							} else if (error.networkResponse.statusCode == 404) {
								// ShowToast("比赛找不到或者球队找不到");
								return;
							}
							// ShowToast("错误:请稍后重试");
						}
					});
		} else {
			String isConfirm = "";
			if (confirmOrrefuse.equals(MATCH_INVITATION_CONFIRM))
				isConfirm = "true";
			else if (confirmOrrefuse.equals(MATCH_INVITATION_REFUSE))
				isConfirm = "false";
			NetWorkManager.getInstance(this).isAgreeOnChallenge(matchInfo.getChallengeUuid(), isConfirm, UserManager.getInstance(mContext).getCurrentUser()
							.getToken(), new Listener<Void>() {
						@Override
						public void onResponse(Void response) {
							DialogUtil.dialogDismiss(progressDia);
							ShowToast("已处理该应战请求");
							
							Intent intent = new Intent();
							if (confirmOrrefuse.equals(MATCH_INVITATION_CONFIRM))
								intent.putExtra(
										MatchInvitationActivity.BROADCAST_CONFIRM_OF_REFUSE,
										MatchInvitationActivity.MATCH_COMFIRM);// 同意
							else if (confirmOrrefuse.equals(MATCH_INVITATION_REFUSE))
								intent.putExtra(
										MatchInvitationActivity.BROADCAST_CONFIRM_OF_REFUSE,
										MatchInvitationActivity.MATCH_REFUSE);// 同意
							intent.putExtra(
									MatchInvitationActivity.BROADCAST_MATCH_INFO_UUID,
									matchInfo.getUuid());// 拒绝的比赛id
							setResult(RESULT_OK, intent);
							finish();
							
							Intent intent2 = new Intent(Action.ACTION_BROADCAST_MATCH_COMFIRM_OR_REFUSE);//需要刷新列表数据
							sendBroadcast(intent2);
						}
					}, new ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							DialogUtil.dialogDismiss(progressDia);
							if (!NetWorkManager.getInstance(mContext)
									.isNetConnected()) {
								ShowToast("当前网络不可用");
								return;
							}
							if (error.networkResponse == null) {
								ShowToast("服务器连接错误");
								return;
							} else if (error.networkResponse.statusCode == 401) {
								ErrorCodeUtil.ErrorCode401(mContext);
								return;
							} else if (error.networkResponse.statusCode == 403) {
								ShowToast("不是队长，没权限");
								return;
							} else if (error.networkResponse.statusCode == 404) {
								ShowToast("应战请求找不到");
								return;
							} else if (error.networkResponse.statusCode == 409) {
								ShowToast("应战请求已被回复");
								return;
							}
						}
					});
		}
	}

	/**
	 * 同意比赛
	 * 
	 * @param invitation_Uid
	 */
	private void confirm(String invitation_Uid) {
		NetWorkManager.getInstance(mContext).confirmMatchInvitation(
				invitation_Uid, "true",
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						DialogUtil.dialogDismiss(progressDia);
						ShowToast("成功接受邀请");
						Intent intent = new Intent();
						intent.putExtra(
								MatchInvitationActivity.BROADCAST_CONFIRM_OF_REFUSE,
								MatchInvitationActivity.MATCH_COMFIRM);// 同意
						intent.putExtra(
								MatchInvitationActivity.BROADCAST_MATCH_INFO_UUID,
								matchInfo.getUuid());// 拒绝的比赛id
						setResult(RESULT_OK, intent);
						finish();
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						DialogUtil.dialogDismiss(progressDia);
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("当前网络不可用");
						}
						if (error.networkResponse != null) {
							if (error.networkResponse.statusCode == 401) {
								ErrorCodeUtil.ErrorCode401(mContext);
							} else if (error.networkResponse.statusCode == 404) {
								// ShowToast("比赛邀请没找到");
							}
						}
					}
				});
	}

	/**
	 * 拒绝比赛邀请
	 * 
	 * @param invitation_Uid
	 */
	private void refuse(String invitation_Uid) {
		NetWorkManager.getInstance(mContext).confirmMatchInvitation(
				invitation_Uid, "false",
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						DialogUtil.dialogDismiss(progressDia);
						ShowToast("拒绝比赛邀请");
						updateMatchState(MatchStateEnum.CANCELED);
						Intent intent = new Intent();
						intent.putExtra(
								MatchInvitationActivity.BROADCAST_CONFIRM_OF_REFUSE,
								MatchInvitationActivity.MATCH_REFUSE);// 拒绝
						intent.putExtra(
								MatchInvitationActivity.BROADCAST_MATCH_INFO_UUID,
								matchInfo.getUuid());// 拒绝的比赛id
						setResult(RESULT_OK, intent);
						finish();
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						DialogUtil.dialogDismiss(progressDia);
						// ShowToast("失败："+error.getMessage());
					}
				});
	}

	private void updateMatchState(MatchStateEnum state) {
		QueryBuilder<Match> qb = CustomApplcation.getDaoSession(mContext)
				.getMatchDao().queryBuilder();
		Match match = null;
		qb.where(MatchDao.Properties.Uuid.eq(matchInfo.getUuid()));
		try {
			match = qb.uniqueOrThrow();
			match.setState(state.getValue());
		} catch (DaoException ex) {
			// 没有该场比赛，插入比赛状态
			match = new Match();
			match.setUuid(matchInfo.getUuid());
			match.setState(state.getValue());
		}
		CustomApplcation.getDaoSession(mContext).getMatchDao()
				.insertOrReplace(match);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_CODE_RECEIPT_HOMETEAM_INFO) {
			Team receiptTeam = intent
					.getParcelableExtra(TeamInfoForGuestActivity.INTENT_TEAM_INFO_BEAN);
			if (receiptTeam == null) {
				return;
			}
			matchInfo.setHomeTeam(receiptTeam); // 替换之前的HomeTeam
			return;
		}
		if (requestCode == REQUEST_CODE_RECEIPT_AWAYTEAM_INFO) {
			Team receiptTeam = intent
					.getParcelableExtra(TeamInfoForGuestActivity.INTENT_TEAM_INFO_BEAN);
			if (receiptTeam == null) {
				return;
			}
			matchInfo.setAwayTeam(receiptTeam); // 替换之前的AwayTeam
			return;
		}
		if (requestCode == InviteMatchConstants.REQUEST_CODE_TEAM) { // TODO
																		// 选择一个球队应战
			Team signTeam = intent
					.getParcelableExtra(InviteMatchConstants.INTENT_TEAM_BEAN);

			// 设置客队队名和队徽
			awayTeamName.setText(signTeam.getName());
			PictureUtil.getMd5PathByUrl(
					PhotoUtil.getThumbnailPath(signTeam.getBadge()),
					awayTeamBadge, new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.team_bage_default)
							.showImageForEmptyUri(R.drawable.team_bage_default)
							.showImageOnFail(R.drawable.team_bage_default)
							.build());

			NetWorkManager.getInstance(this).signUpForMatch(
					matchInfo.getUuid(), signTeam.getUuid(),
					UserManager.getInstance(this).getCurrentUser().getToken(),
					new Listener<Void>() {

						@Override
						public void onResponse(Void response) {
							// TODO 自动生成的方法存根
							ShowToast("应战成功");
							finish();
							
							Intent intent2 = new Intent(Action.ACTION_BROADCAST_MATCH_COMFIRM_OR_REFUSE);//需要刷新列表数据
							sendBroadcast(intent2);
						}
					}, new ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							ShowToast("应战失败");
							if(error.networkResponse.statusCode == 409){
								finish();
							}
						}
					});
		}
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
	
}
