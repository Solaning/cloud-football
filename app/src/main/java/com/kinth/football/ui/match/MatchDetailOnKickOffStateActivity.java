package com.kinth.football.ui.match;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.adapter.TeamMemberHorizontalListAdapter;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.bean.match.PlayerInTeam;
import com.kinth.football.config.MatchStateEnum;
import com.kinth.football.dao.Team;
import com.kinth.football.dao.TeamPlayer;
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
import com.kinth.football.util.DBUtil;
import com.kinth.football.util.DateUtil;
import com.kinth.football.util.DialogUtil;
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
 * 比赛中状态的比赛详情页面 -- 裁判可以提交比分
 * 
 * @author Sola
 * 
 */
@ContentView(R.layout.activity_match_detail_on_kick_off_state)
public class MatchDetailOnKickOffStateActivity extends BaseActivity{
	public static final String INTENT_MATCH_DETAIL_BEAN = "INTENT_MATCH_DETAIL_BEAN";// intent中传递的比赛数据信息

	private static final int REQUEST_CODE_RECEIPT_HOMETEAM_INFO = 9001;   //查看主队球队信息请求码
	private static final int REQUEST_CODE_RECEIPT_AWAYTEAM_INFO = 9002;   //查看客队球队信息请求码
	
	private MatchInfo matchInfo;
	private TeamMemberHorizontalListAdapter adapter;// 球队成员的横向adapter
	private int userType;// 1-在主队，2-在客队，3-裁判
	private boolean isHomeTeam;// 是否主队 -- 差裁判查看队员 TODO
	private String currentUserID;

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

	@ViewInject(R.id.tv_away_team_name)
	private TextView awayTeamName;// 客队名称

	@ViewInject(R.id.iv_away_team_badge)
	private RoundImageView awayTeamBadge;// 客队队徽

	@ViewInject(R.id.reffree_set_score)
	private ImageView commitScore;// 提交比分
	
	@ViewInject(R.id.iv_vs)
	private ImageView VS;

	@ViewInject(R.id.tv_match_description)
	private TextView matchDescripotion;// 比赛名称

	@ViewInject(R.id.tv_match_field)
	private TextView matchField;// 比赛场地

	@ViewInject(R.id.tv_match_date)
	private TextView matchDate;// 比赛时间

	@ViewInject(R.id.tv_match_player_count)
	private TextView matchPlayerCount;// 比赛人数

	@ViewInject(R.id.teamInfo_memberlist)
	private HorizontalListView teamMemberHorizontalListView;// 球队成员列表

	
	@ViewInject(R.id.llt_homejersey)
	private LinearLayout llt_homejersey;  //本方队服的背景框
	
	@ViewInject(R.id.llt_otherjersey)
	private LinearLayout llt_otherjersey;  //对方队服的背景框
	
	@ViewInject(R.id.self_jersey)
	private RoundImageView self_jersey; // 本方队服

	@ViewInject(R.id.other_jersey)
	private RoundImageView other_jersey;// 对方队服

	@ViewInject(R.id.tv_home_team_formation_title)
	private TextView homeTeamFormationTitle;//主队阵容title--默认为主队阵容
	
	@ViewInject(R.id.rtl_home_team_formation_layout)
	private RelativeLayout homeTeamFormationLayout;//主队阵容layout
	
	@ViewInject(R.id.tv_home_team_set_formation)
	private TextView homeTeamSetFormation;// 设置阵容（查看阵容）
	
	@ViewInject(R.id.tv_away_team_formation_title)
	private TextView awayTeamFormationTitle;//客队阵容title
	
	@ViewInject(R.id.rtl_away_team_formation_layout)
	private RelativeLayout awayTeamFormationLayout;//客队阵容layou
	
	@ViewInject(R.id.tv_away_team_set_formation)
	private TextView awayTeamSetFormation;//客队设置阵容

	@ViewInject(R.id.tv_referee_name)
	private TextView refereeName;// 裁判名称

	@ViewInject(R.id.refree_avatar)
	private RoundImageView refereeAvatar;// 裁判头像
	
	@ViewInject(R.id.tv_match_cost)
	private TextView matchCost;//费用
	
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
	
	@OnClick(R.id.iv_home_team_badge)  //点击主队队徽
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
	
	@OnClick(R.id.iv_away_team_badge)  //点击客队队徽
	public void fun_7(View v){
		Intent intent = null;
		if(DBUtil.isGuest(mContext, matchInfo.getAwayTeam())){//来宾
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
	
	private EditText homeTeamScore;
	private EditText awayTeamScore;
	private AlertDialog.Builder builder;
	private Dialog dialog;
	
	@OnClick(R.id.reffree_set_score)
	public void fun_4(View v) {// 提交比分
		builder = new Builder(this);
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.input_match_score_layout, null);
		homeTeamScore = (EditText) view
				.findViewById(R.id.et_input_home_team_score);
		awayTeamScore = (EditText) view
				.findViewById(R.id.et_input_away_team_score);
		Button btn_sure_commit = (Button)view.findViewById(R.id.btn_sure_commit);
		Button btn_close = (Button)view.findViewById(R.id.btn_close);
		builder.setView(view);
		btn_sure_commit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogUtil.dialogDismiss(dialog);
				String homeTeamScoreStr = homeTeamScore.getText().toString();
				if (TextUtils.isEmpty(homeTeamScoreStr)) {
					ShowToast("请输入比分");
					return;
				}
				String awayTeamScoreStr = awayTeamScore.getText().toString();
				if (TextUtils.isEmpty(awayTeamScoreStr)) {
					ShowToast("请输入比分");
					return;
				}
				final int homeTeamScoreInt = Integer.valueOf(homeTeamScoreStr);// 主队比分
				final int awayTeamScoreInt = Integer.valueOf(awayTeamScoreStr);// 客队比分

				AlertDialog.Builder builder2 = new Builder(mContext);
				builder2.setMessage("一旦提交将不能修改，确认提交比分吗？" + "\n主队 "
						+ homeTeamScoreInt + " VS " + awayTeamScoreInt + "客队");
				builder2.setPositiveButton("确认", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog2, int which) {
						dialog2.dismiss();
						dialog = ProgressDialog.show(mContext, "提示", "请稍后", false, false);
						NetWorkManager.getInstance(mContext).typeInMatchScore(
								homeTeamScoreInt,
								awayTeamScoreInt,
								UserManager.getInstance(mContext)
										.getCurrentUser().getToken(),
								matchInfo.getUuid(), new Listener<Void>() {

									@Override
									public void onResponse(Void response) {
										DialogUtil.dialogDismiss(dialog);
										ShowToast("录入比分成功");
										commitScore.setEnabled(false);
										DBUtil.saveMatchToDB(mContext,
												matchInfo, MatchStateEnum.OVER);
									}
								}, new ErrorListener() {

									@Override
									public void onErrorResponse(
											VolleyError error) {
										DialogUtil.dialogDismiss(dialog);
										ShowToast("录入比分失败");
									}
								});
					}
				});
				builder2.setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder2.show();
			}
		});
		btn_close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogUtil.dialogDismiss(dialog);
			}
		});
		dialog = builder.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		title.setText("比赛详情");
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(entireLayout, new BitmapDrawable(getResources(),
				background));

		currentUserID = UserManager.getInstance(mContext).getCurrentUser()
				.getPlayer().getUuid();
		matchInfo = getIntent().getParcelableExtra(INTENT_MATCH_DETAIL_BEAN);
		if (matchInfo == null) {
			return;
		}
		
		VS.setVisibility(View.GONE);
		commitScore.setVisibility(View.GONE);
		if (currentUserID.equals(matchInfo.getReferee().getUuid())) {
			commitScore.setVisibility(View.VISIBLE);
		} else {
			VS.setVisibility(View.VISIBLE);
		}

		userType = getUserType();
		switch (userType) {
		case 1:
		case 2:
			isHomeTeam = inHomeOrAwayTeam();
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

	/**
	 * 返回是主队还是客队的id
	 * 
	 * @return
	 */
	private boolean inHomeOrAwayTeam() {
		String currentUserID = UserManager.getInstance(mContext)
				.getCurrentUser().getPlayer().getUuid();

		List<TeamPlayer> teamPlayer = CustomApplcation.getDaoSession(mContext)
				.getTeamPlayerDao()._queryPlayer_TeamPlayerList(currentUserID);
		boolean inHome = false;
		for (TeamPlayer item : teamPlayer) {// 保证不在主队才去搜索客队
			if (item.getTeam_id().equals(matchInfo.getHomeTeam().getUuid())) {
				inHome = true;
				break;
			}
			if (item.getTeam_id().equals(matchInfo.getAwayTeam().getUuid())) {
				inHome = false;
			}
		}
		return inHome;
	}

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
						if(playerInTeam == null){
							return;
						}
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
			if (isHomeTeam) {
				adapter.setPlayerList(matchInfo.getHomeTeamPlayers());
			} else {
				adapter.setPlayerList(matchInfo.getAwayTeamPlayers());
			}
		}
	}

	private void initDate() {
		homeTeamName.setText(matchInfo.getHomeTeam().getName());
		awayTeamName.setText(matchInfo.getAwayTeam().getName());
		refereeName.setText(matchInfo.getReferee().getName());

		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(matchInfo.getHomeTeam().getBadge()),
				homeTeamBadge,
				new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.team_bage_default)
				.showImageForEmptyUri(R.drawable.team_bage_default)
				.showImageOnFail(R.drawable.team_bage_default).build());
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(matchInfo.getAwayTeam().getBadge()),
				awayTeamBadge,
				new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.team_bage_default)
				.showImageForEmptyUri(R.drawable.team_bage_default)
				.showImageOnFail(R.drawable.team_bage_default).build());
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(matchInfo.getReferee().getPicture()),
				refereeAvatar,
				new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_default_head)
				.showImageForEmptyUri(R.drawable.icon_default_head)
				.showImageOnFail(R.drawable.icon_default_head).build());

		matchDescripotion.setText(matchInfo.getName());
		matchField.setText(matchInfo.getField());
		matchDate.setText(StringUtils.defaultIfEmpty(
				DateUtil.parseTimeInMillis_hadweek(matchInfo.getKickOff()), ""));// 比赛时间
		matchPlayerCount.setText(matchInfo.getPlayerCount() + "人");
		if(matchInfo.getCost() == 0){
			matchCost.setText("— —");
		}else{
			matchCost.setText((int)matchInfo.getCost() + "元");
		}
	}

	private void showJersey() {
		switch(userType){
		case 1:
			if (!TextUtils.isEmpty(matchInfo.getHomeTeamJersey())) {
				self_jersey.setVisibility(View.VISIBLE);
				PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(matchInfo
						.getHomeTeamJersey()), self_jersey);
				self_jersey.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ArrayList<String> photos = new ArrayList<String>();
						photos.add(PhotoUtil.getAllPhotoPath(PhotoUtil.getAllPhotoPath(matchInfo
								.getHomeTeamJersey())));
						PictureUtil.viewLargerImage(mContext, photos);
					}
				});
			} else {
//				self_jersey.setImageResource(R.drawable.jersey_default);
				llt_homejersey.setBackgroundResource(R.drawable.aaa);
				self_jersey.setVisibility(View.GONE);
			}
			// 将客队队服显示在对方中(无需考虑其他情况)
			if (!TextUtils.isEmpty(matchInfo.getAwayTeamJersey())) {
				other_jersey.setVisibility(View.VISIBLE);
				PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(matchInfo
						.getAwayTeamJersey()), other_jersey);
				other_jersey.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ArrayList<String> photos = new ArrayList<String>();
						photos.add(PhotoUtil.getAllPhotoPath(PhotoUtil.getAllPhotoPath(matchInfo
								.getAwayTeamJersey())));
						PictureUtil.viewLargerImage(mContext, photos);
					}
				});
			} else {
//				other_jersey.setImageResource(R.drawable.jersey_default);
				llt_otherjersey.setBackgroundResource(R.drawable.aaa);
				other_jersey.setVisibility(View.GONE);
			}
			break;
		case 2:
			if (!TextUtils.isEmpty(matchInfo.getAwayTeamJersey())) {
				self_jersey.setVisibility(View.VISIBLE);
				PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(matchInfo
						.getAwayTeamJersey()), self_jersey);
				self_jersey.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ArrayList<String> photos = new ArrayList<String>();
						photos.add(PhotoUtil.getAllPhotoPath(PhotoUtil.getAllPhotoPath(matchInfo
								.getAwayTeamJersey())));
						PictureUtil.viewLargerImage(mContext, photos);
					}
				});
			} else {
//				self_jersey.setImageResource(R.drawable.jersey_default);
				llt_homejersey.setBackgroundResource(R.drawable.aaa);
				self_jersey.setVisibility(View.GONE);
			}
			// 将主队队服显示在对方(无需考虑其他情况)
			if (!TextUtils.isEmpty(matchInfo.getHomeTeamJersey())) {
				other_jersey.setVisibility(View.VISIBLE);
				PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(matchInfo
						.getHomeTeamJersey()), other_jersey);
				other_jersey.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ArrayList<String> photos = new ArrayList<String>();
						photos.add(PhotoUtil.getAllPhotoPath(PhotoUtil.getAllPhotoPath(matchInfo
								.getHomeTeamJersey())));
						PictureUtil.viewLargerImage(mContext, photos);
					}
				});
			} else {
//				other_jersey.setImageResource(R.drawable.jersey_default);
				llt_otherjersey.setBackgroundResource(R.drawable.aaa);
				other_jersey.setVisibility(View.GONE);
			}
			break;
		case 3://裁判视角
			if (!TextUtils.isEmpty(matchInfo.getHomeTeamJersey())) {//主队
				self_jersey.setVisibility(View.VISIBLE);
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
//				self_jersey.setImageResource(R.drawable.jersey_default);
				llt_homejersey.setBackgroundResource(R.drawable.aaa);
				self_jersey.setVisibility(View.GONE);
			}
			//客队情况
			if (!TextUtils.isEmpty(matchInfo.getAwayTeamJersey())) {
				other_jersey.setVisibility(View.VISIBLE);
				PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(matchInfo
						.getAwayTeamJersey()), other_jersey);
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
//				other_jersey.setImageResource(R.drawable.jersey_default);
				llt_otherjersey.setBackgroundResource(R.drawable.aaa);
				other_jersey.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 阵容
	 */
	private void showFormation() {
		switch(userType){
		case 1:// 主队mScreenWidth
			LayoutParams params = new RelativeLayout.LayoutParams(CustomApplcation.getInstance().getParameters().getmScreenWidth(),
					Math.round(CustomApplcation.getInstance().getParameters().getmScreenWidth() / 2.75348f));//图片的宽高比例
			homeTeamFormationBg.setLayoutParams(params);
			ImageLoader.getInstance().displayImage("drawable://" + R.drawable.formation_bg, homeTeamFormationBg, 
					ImageLoadOptions.getDisplayDrawableBuilder().build());
			
			homeTeamFormationTitle.setVisibility(View.VISIBLE);
			homeTeamFormationTitle.setText("阵容");
			homeTeamFormationLayout.setVisibility(View.VISIBLE);
			if (matchInfo.getHomeTeamFormation() != null) {// 有阵容
				homeTeamSetFormation.setText("查看阵容");
				homeTeamSetFormation.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Formation formation = matchInfo.getHomeTeamFormation();
						if (formation == null)
							return;
						Intent intent = new Intent(mContext,
						FormationConstants.class);
						intent.putExtra(FormationConstants.INTENT_FORMATION_BEAN, formation);
						startActivity(intent);
					}
					});
			} else {// 没有阵容
				homeTeamSetFormation.setText("队长未设置阵容");
			}
			break;
		case 2:
			LayoutParams params2 = new RelativeLayout.LayoutParams(CustomApplcation.getInstance().getParameters().getmScreenWidth(),
					Math.round(CustomApplcation.getInstance().getParameters().getmScreenWidth() / 2.75348f));//图片的宽高比例
			awayTeamFormationBg.setLayoutParams(params2);
			ImageLoader.getInstance().displayImage("drawable://" + R.drawable.formation_bg, awayTeamFormationBg, 
					ImageLoadOptions.getDisplayDrawableBuilder().build());
			
			awayTeamFormationTitle.setVisibility(View.VISIBLE);
			awayTeamFormationLayout.setVisibility(View.VISIBLE);
			awayTeamFormationTitle.setText("阵容");
			if (matchInfo.getAwayTeamFormation() != null) {// 有阵容
				awayTeamSetFormation.setText("查看阵容");
				awayTeamSetFormation.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Formation formation = matchInfo.getAwayTeamFormation();
						if (formation == null)
							return;
						Intent intent = new Intent(mContext,
								FormationDetailActivity.class);
						intent.putExtra(FormationConstants.INTENT_FORMATION_BEAN, formation);
						startActivity(intent);
					}
				});
			} else {// 没有阵容
				awayTeamSetFormation.setText("队长未设置阵容");
			}
			break;
		case 3://裁判看到的是两队的两套阵容
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
				homeTeamSetFormation.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Formation formation = matchInfo.getHomeTeamFormation();
						if (formation == null)
							return;
						Intent intent = new Intent(mContext,
						FormationConstants.class);
						intent.putExtra(FormationConstants.INTENT_FORMATION_BEAN, formation);
						startActivity(intent);
					}
					});
			} else {// 没有阵容
				homeTeamSetFormation.setText("队长未设置阵容");
			}
			if (matchInfo.getAwayTeamFormation() != null) {// 有阵容
				awayTeamSetFormation.setText("查看阵容");
				awayTeamSetFormation.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Formation formation = matchInfo.getAwayTeamFormation();
						if (formation == null)
							return;
						Intent intent = new Intent(mContext,
								FormationConstants.class);
						intent.putExtra(FormationConstants.INTENT_FORMATION_BEAN, formation);
						startActivity(intent);
					}
				});
			} else {// 没有阵容
				awayTeamSetFormation.setText("队长未设置阵容");
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode != RESULT_OK) {
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
	
}
