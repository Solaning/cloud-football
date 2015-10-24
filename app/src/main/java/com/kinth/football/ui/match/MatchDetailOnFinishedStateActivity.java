package com.kinth.football.ui.match;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.adapter.TeamMemberHorizontalListAdapter;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.bean.match.PlayerInTeam;
import com.kinth.football.dao.Team;
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
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.UtilFunc;
import com.kinth.football.view.HorizontalListView;
import com.kinth.football.view.RoundImageView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 赛后存档
 * @author Sola
 * 
 */
@ContentView(R.layout.activity_match_detail_on_finished)
public class MatchDetailOnFinishedStateActivity extends BaseActivity {
	public static final String INTENT_MATCH_DETAIL_BEAN = "INTENT_MATCH_DETAIL_BEAN";// intent中传递的比赛数据信息

	private static final int REQUEST_CODE_RECEIPT_HOMETEAM_INFO = 9001;   //查看主队球队信息请求码
	private static final int REQUEST_CODE_RECEIPT_AWAYTEAM_INFO = 9002;   //查看客队球队信息请求码
	
	private MatchInfo matchInfo;
	private TeamMemberHorizontalListAdapter homeAdapter;// 球队成员的横向adapter
	private TeamMemberHorizontalListAdapter awayAdapter;// 客队
	
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
	private TextView matchDescription;// 比赛名称

	@ViewInject(R.id.tv_match_field)
	private TextView matchField;// 比赛场地

	@ViewInject(R.id.tv_match_date)
	private TextView matchDate;// 比赛时间

	@ViewInject(R.id.tv_match_player_count)
	private TextView matchPlayerCount;
	
	@ViewInject(R.id.tv_match_cost)
	private TextView matchCost;//费用

	@ViewInject(R.id.teamInfo_memberlist_home)
	private HorizontalListView homeTeamMemberHorizontalListView;// 球队成员列表

	@ViewInject(R.id.teamInfo_memberlist_away)
	private HorizontalListView awayTeamMemberHorizontalListView;// 球队成员列表
	
	@ViewInject(R.id.llt_homejersey)
	private LinearLayout llt_homejersey;  //本方队服的背景框
	
	@ViewInject(R.id.llt_otherjersey)
	private LinearLayout llt_otherjersey;  //对方队服的背景框

	@ViewInject(R.id.home_jersey)
	private RoundImageView self_jersey; // 本方队服

	@ViewInject(R.id.away_jersey)
	private RoundImageView other_jersey;// 对方队服

	@ViewInject(R.id.tv_see_home_team_formation)
	private TextView seeHomeTeamFormation;// 查看主队阵容

	@ViewInject(R.id.tv_see_away_team_formation)
	private TextView seeAwayTeamFormation;// 查看客队阵容

	@ViewInject(R.id.tv_referee_name)
	private TextView refereeName;// 裁判名称

	@ViewInject(R.id.refree_avatar)
	private RoundImageView refreeAvatar;// 裁判头像
	
	@ViewInject(R.id.iv_home_team_formation_bg)
	private ImageView homeTeamFormationBg;//主队阵容图片
	
	@ViewInject(R.id.iv_away_team_formation_bg)
	private ImageView awayTeamFormationBg;//主队阵容图片
	
	@ViewInject(R.id.btn_match_vedio)
	private ImageView matchVedio;

	@OnClick(R.id.nav_left)
	public void fun_1(View v) {
		finish();
	}
	
	@OnClick(R.id.btn_match_vedio)
	public void fun_2(View v){
		if(TextUtils.isEmpty(matchInfo.getVideo())){
			Toast.makeText(mContext, "暂无比赛视频", Toast.LENGTH_LONG).show();
			return;
		}
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);   
        Uri content_url = Uri.parse(matchInfo.getVideo());  
        intent.setData(content_url); 
        startActivity(intent);
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(entireLayout, new BitmapDrawable(getResources(),
				background));
		
//		LayoutParams params = new RelativeLayout.LayoutParams(mScreenWidth,
//				Math.round(mScreenWidth / 2.75348f));//图片的宽高比例
//		homeTeamFormationBg.setLayoutParams(params);
//		ImageLoader.getInstance().displayImage("drawable://" + R.drawable.formation_bg, homeTeamFormationBg, 
//				ImageLoadOptions.getDisplayDrawableBuilder().build());
//		awayTeamFormationBg.setLayoutParams(params);
//		ImageLoader.getInstance().displayImage("drawable://" + R.drawable.formation_bg, awayTeamFormationBg, 
//				ImageLoadOptions.getDisplayDrawableBuilder().build());
		

		title.setText("比赛详情");
		matchInfo = getIntent().getParcelableExtra(INTENT_MATCH_DETAIL_BEAN);
		if (matchInfo == null) {
			return;
		}
		initDate();
		init_scrollview();
		showJersey();
		showFormation();
	}

	private void initDate() {
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
				refreeAvatar,
				new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_default_head)
				.showImageForEmptyUri(R.drawable.icon_default_head)
				.showImageOnFail(R.drawable.icon_default_head).build());

		homeTeamName.setText(matchInfo.getHomeTeam().getName());
		awayTeamName.setText(matchInfo.getAwayTeam().getName());
		refereeName.setText(matchInfo.getReferee().getName());

		matchDescription.setText(matchInfo.getName());
		matchField.setText(matchInfo.getField());
		matchDate.setText(StringUtils.defaultIfEmpty(
				DateUtil.parseTimeInMillis_hadweek(matchInfo.getKickOff()), ""));// 比赛时间
		matchPlayerCount.setText(matchInfo.getPlayerCount() + "人");
		if(matchInfo.getCost() == 0){
			matchCost.setText("— —");
		}else{
			matchCost.setText((int)matchInfo.getCost() + "元");
		}
		homeTeamScore.setText(String.valueOf(matchInfo.getHomeTeamScore()));
		awayTeamScore.setText(String.valueOf(matchInfo.getAwayTeamScore()));
		if(TextUtils.isEmpty(matchInfo.getVideo())){
			matchVedio.setVisibility(View.GONE);
		}else{
			matchVedio.setVisibility(View.VISIBLE);
			int mScreenWidth = CustomApplcation.getInstance().getParameters().getmScreenWidth() - UtilFunc.dip2px(mContext, 30);
			android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mScreenWidth, Math.round(mScreenWidth / 4.477f));// 图片的宽高比例
			matchVedio.setLayoutParams(params);
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
			opt.inInputShareable = true;
			opt.inPurgeable = true;
			// 获取资源图片
			InputStream is = mContext.getResources().openRawResource(
					R.drawable.match_vedio);
			Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
			matchVedio.setImageBitmap(bitmap);
		}
	}

	/**
	 * 成员列表
	 */
	private void init_scrollview() {
		homeAdapter = new TeamMemberHorizontalListAdapter(mContext,
				matchInfo.getHomeTeamPlayers());
		awayAdapter = new TeamMemberHorizontalListAdapter(mContext,
				matchInfo.getAwayTeamPlayers());
		homeTeamMemberHorizontalListView.setAdapter(homeAdapter);
		homeTeamMemberHorizontalListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						PlayerInTeam playerInTeam = homeAdapter
								.getItem(position);
						// 个人信息
						Intent intent = new Intent(mContext,
								TeamPlayerInfo.class);
						intent.putExtra(TeamPlayerInfo.PLAYER_UUID,
								playerInTeam.getPlayer().getUuid());
						startActivity(intent);
					}
				});
		awayTeamMemberHorizontalListView.setAdapter(awayAdapter);
		awayTeamMemberHorizontalListView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						PlayerInTeam playerInTeam = awayAdapter
								.getItem(position);
						// 个人信息
						Intent intent = new Intent(mContext,
								TeamPlayerInfo.class);
						intent.putExtra(TeamPlayerInfo.PLAYER_UUID,
								playerInTeam.getPlayer().getUuid());
						startActivity(intent);
					}
				});
	}

	private void showJersey() {
		if (!TextUtils.isEmpty(matchInfo.getHomeTeamJersey())) {// 主队队服
			self_jersey.setVisibility(View.VISIBLE);
			PictureUtil.getMd5PathByUrl(
					PhotoUtil.getThumbnailPath(matchInfo.getHomeTeamJersey()),
					self_jersey);
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
//			self_jersey.setImageResource(R.drawable.jersey_default);
			llt_homejersey.setBackgroundResource(R.drawable.aaa);
			self_jersey.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(matchInfo.getAwayTeamJersey())) {// 客队队服
			other_jersey.setVisibility(View.VISIBLE);
			PictureUtil.getMd5PathByUrl(
					PhotoUtil.getThumbnailPath(matchInfo.getAwayTeamJersey()),
					other_jersey);
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
//			other_jersey.setImageResource(R.drawable.jersey_default);
			llt_otherjersey.setBackgroundResource(R.drawable.aaa);
			other_jersey.setVisibility(View.GONE);
		}
	}

	/**
	 * 阵容
	 */
	private void showFormation() {
		if (matchInfo.getHomeTeamFormation() != null) {// 有阵容
			seeHomeTeamFormation.setText("查看主队阵容");
			seeHomeTeamFormation.setOnClickListener(new View.OnClickListener() {
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
			seeHomeTeamFormation.setText("主队队长未设置阵容");
		}
		if (matchInfo.getAwayTeamFormation() != null) {// 有阵容
			seeAwayTeamFormation.setText("查看客队阵容");
			seeAwayTeamFormation.setOnClickListener(new View.OnClickListener() {
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
			seeAwayTeamFormation.setText("客队队长未设置阵容");
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
