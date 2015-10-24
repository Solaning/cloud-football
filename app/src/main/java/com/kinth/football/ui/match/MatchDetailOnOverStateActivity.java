package com.kinth.football.ui.match;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;

import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.adapter.TeamMemberHorizontalListAdapter;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.bean.match.PlayerInTeam;
import com.kinth.football.dao.Team;
import com.kinth.football.manager.UserManager;
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
 * 待评价的比赛详情页面
 * @author Sola
 * 
 */
@ContentView(R.layout.activity_match_detail_on_over)
public class MatchDetailOnOverStateActivity extends BaseActivity {
	public static final String INTENT_MATCH_DETAIL_BEAN = "INTENT_MATCH_DETAIL_BEAN";// intent中传递的比赛数据信息
	
	private static final int REQUEST_CODE_RECEIPT_HOMETEAM_INFO = 9001;   //查看主队球队信息请求码
	private static final int REQUEST_CODE_RECEIPT_AWAYTEAM_INFO = 9002;   //查看客队球队信息请求码
	
	private MatchInfo matchInfo;
	private TeamMemberHorizontalListAdapter adapter;//球队成员的横向adapter
	private int userType;//1-在主队，2-在客队，3-裁判  ,4-其他
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

	@ViewInject(R.id.tv_home_team_score)
	private TextView homeTeamScore;// 主队比分

	@ViewInject(R.id.tv_away_team_name)
	private TextView awayTeamName;// 客队名称

	@ViewInject(R.id.iv_away_team_badge)
	private RoundImageView awayTeamBadge;// 客队队徽

	@ViewInject(R.id.tv_away_team_score)
	private TextView awayTeamScore;// 客队比分

	@ViewInject(R.id.tv_match_description)
	private TextView matchDescription;// 比赛描述

	@ViewInject(R.id.tv_match_field)
	private TextView matchField;// 比赛场地

	@ViewInject(R.id.tv_match_date)
	private TextView matchDate;// 比赛时间
	
	@ViewInject(R.id.tv_match_player_count)
	private TextView matchPlayerCount;
	
	@ViewInject(R.id.tv_referee_name)
	private TextView refereeName;//裁判名字
	
	@ViewInject(R.id.refree_avatar)
	private RoundImageView refereeAvatar;//裁判头像
	
	@ViewInject(R.id.tv_match_cost)
	private TextView matchCost;//费用

	@ViewInject(R.id.layout_grade)
	private LinearLayout gradeLayout;//评分布局
	
	@ViewInject(R.id.btn_grade)
	private Button layout_grade;//评分按钮
	
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
	
	@ViewInject(R.id.iv_home_team_formation_bg)
	private ImageView homeTeamFormationBg;//主队阵容图片
	
	@ViewInject(R.id.iv_away_team_formation_bg)
	private ImageView awayTeamFormationBg;//主队阵容图片

	@OnClick(R.id.nav_left)
	public void fun_1(View v) {
		finish();
	}
	
	@OnClick(R.id.refree_avatar)
	public void fun_3(View v){
		if(matchInfo.getReferee() != null && !TextUtils.isEmpty(matchInfo.getReferee().getUuid())){
			Intent intent = new Intent(mContext, TeamPlayerInfo.class);
			intent.putExtra(TeamPlayerInfo.PLAYER_UUID, matchInfo.getReferee().getUuid());
			startActivity(intent);
		}
	}

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
	
	@OnClick(R.id.btn_grade)
	public void fun_2(View v) {//评价
		Intent intent = new Intent(this, MatchOverGradeActivity.class);
		intent.putExtra(MatchOverGradeActivity.INTENT_MATCH_INFO, matchInfo);
		startActivity(intent);
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
		
		gradeLayout.setVisibility(View.GONE);
		currentUserID = UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid();
		matchInfo = getIntent().getParcelableExtra(INTENT_MATCH_DETAIL_BEAN);
		if (matchInfo == null) {
			return;
		}
		
		userType = getUserType();
		switch(userType){
		case 1:
		case 2:
			gradeLayout.setVisibility(View.VISIBLE);
			init_scrollview(false);
			break;
		case 3:
			gradeLayout.setVisibility(View.GONE);
			init_scrollview(true);
			break;
		case 4://几乎不可能出现的人
			break;
		}
		initData();
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
		if(currentUserID.equals(matchInfo.getReferee().getUuid())){
			return 3;
		}
		return 4;
	}

	private void init_scrollview(boolean isReferee) {
		adapter = new TeamMemberHorizontalListAdapter(mContext, null);
		teamMemberHorizontalListView.setAdapter(adapter);
		teamMemberHorizontalListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				PlayerInTeam playerInTeam = adapter.getItem(position);//??为啥？？ TODO 不是应该-1吗
				//个人信息
				Intent intent = new Intent(mContext, TeamPlayerInfo.class);
				intent.putExtra(TeamPlayerInfo.PLAYER_UUID, playerInTeam.getPlayer().getUuid());
				startActivity(intent);
			}
		});
		if(isReferee){
			List<PlayerInTeam> teamPlayerList = new ArrayList<PlayerInTeam>();
			teamPlayerList.addAll(matchInfo.getHomeTeamPlayers());
			teamPlayerList.addAll(matchInfo.getAwayTeamPlayers());
			adapter.setPlayerList(teamPlayerList);
		}else{
			if(userType == 1){
				adapter.setPlayerList(matchInfo.getHomeTeamPlayers());
			}else{
				adapter.setPlayerList(matchInfo.getAwayTeamPlayers());
			}
		}
	}

	private void initData() {
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
				refereeAvatar, new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.icon_default_head)
						.showImageForEmptyUri(R.drawable.icon_default_head)
						.showImageOnFail(R.drawable.icon_default_head).build());
		
		homeTeamName.setText(matchInfo.getHomeTeam().getName());
		awayTeamName.setText(matchInfo.getAwayTeam().getName());
		matchDescription.setText(matchInfo.getName());
		matchField.setText(matchInfo.getField());
		matchDate.setText(StringUtils.defaultIfEmpty(DateUtil.parseTimeInMillis_hadweek(matchInfo.getKickOff()),""));//比赛时间
		matchPlayerCount.setText(matchInfo.getPlayerCount() + "人");
		refereeName.setText(StringUtils.defaultIfEmpty(matchInfo.getReferee().getName(),""));
		if(matchInfo.getCost() == 0){
			matchCost.setText("— —");
		}else{
			matchCost.setText((int)matchInfo.getCost() + "元");
		}
		homeTeamScore.setText(String.valueOf(matchInfo.getHomeTeamScore()));
		awayTeamScore.setText(String.valueOf(matchInfo.getAwayTeamScore()));
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
				homeTeamSetFormation.setText("查看阵容");
				homeTeamSetFormation.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Formation formation = matchInfo.getHomeTeamFormation();
						if (formation == null)
							return;
						Intent intent = new Intent(mContext,
						FormationDetailActivity.class);
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
				homeTeamSetFormation.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Formation formation = matchInfo.getHomeTeamFormation();
						if (formation == null)
							return;
						Intent intent = new Intent(mContext,
						FormationDetailActivity.class);
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
								FormationDetailActivity.class);
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
