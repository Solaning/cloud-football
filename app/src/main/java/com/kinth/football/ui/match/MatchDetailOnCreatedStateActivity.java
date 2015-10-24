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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.adapter.TeamMemberHorizontalListAdapter;
import com.kinth.football.bean.ErrorDescription;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.bean.match.PlayerInTeam;
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
import com.kinth.football.util.DBUtil;
import com.kinth.football.util.DateUtil;
import com.kinth.football.util.DialogUtil;
import com.kinth.football.util.ErrorCodeUtil;
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
 * 报名中的比赛详情页面
 * @author Sola
 * 
 */
@ContentView(R.layout.activity_match_detail_on_created)
public class MatchDetailOnCreatedStateActivity extends BaseActivity {
	public static final String INTENT_MATCH_DETAIL_BEAN = "INTENT_MATCH_DETAIL_BEAN";// intent中传递的比赛数据信息
	

	
	private static final int REQUEST_CODE_RECEIPT_HOMETEAM_INFO = 10001;   //查看主队球队信息请求码
	private static final int REQUEST_CODE_RECEIPT_AWAYTEAM_INFO = 10002;   //查看客队球队信息请求码
	
	public static final int INTENT_SELECT_MATCH_JERSEY_HOME_TEAM = 10003; // 选择队服请求码(当前用户在主队)
	public static final int INTENT_SELECT_MATCH_JERSEY_AWAY_TEAM = 10004; // 选择队服请求码(当前用户在客队)

	
	private ProgressDialog progressDia;
	private TeamMemberHorizontalListAdapter adapter;//球队成员的横向adapter
	private MatchInfo matchInfo;
//	private boolean isHomeTeam;//是否主队
	private String currentUserID;
	
	private int userType;// 1-在主队，2-在客队，3-裁判
	private boolean isCaptain;// 是否队长
	
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
	
	@ViewInject(R.id.tv_referee_name)
	private TextView refereeName;//裁判名称
	
	@ViewInject(R.id.refree_avatar)
	private RoundImageView refreeAvatar;//裁判头像
	
	@ViewInject(R.id.tv_match_cost)
	private TextView matchCost;//费用
	
	@ViewInject(R.id.tv_match_player_count)
	private TextView matchPlayerCount;
	
	@ViewInject(R.id.teamInfo_memberlist)
	private HorizontalListView teamMemberHorizontalListView;// 球队成员列表
	
	@ViewInject(R.id.tv_match_description)
	private TextView matchDescription;//比赛描述
	
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
	
	@ViewInject(R.id.llt_match_sign_up)
	private LinearLayout matchSignUpLayout;
	
	@ViewInject(R.id.btn_match_sign_up)
	private Button signUp;  //报名按钮

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

	private Handler handler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			//刷新数据
			{
				progressDia = ProgressDialog.show(mContext, "提示", "请稍后...",false, false);
				NetWorkManager.getInstance(mContext).getMatchInfo(UserManager.getInstance(mContext).getCurrentUser().getToken(),matchInfo.getUuid(),
						new Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								DialogUtil.dialogDismiss(progressDia);
								Gson gson = new Gson();
								MatchInfo match = null;
								match = gson.fromJson(response.toString(), new TypeToken<MatchInfo>(){}.getType());
								if(match != null){
									for(int i=0; i < CustomApplcation.getMatchInfoOfCreated().size(); i++){
										if(CustomApplcation.getMatchInfoOfCreated().get(i).getUuid().equals(match.getUuid())){
											CustomApplcation.getMatchInfoOfCreated().set(i, match);
											matchInfo = match;
											adapter.notifyDataSetInvalidated();
//											if(isHomeTeam){
//												adapter.setPlayerList(match.getHomeTeamPlayers());
//											}else{
//												adapter.setPlayerList(match.getAwayTeamPlayers());
//											}
											if (userType ==1) {
												adapter.setPlayerList(match.getHomeTeamPlayers());
											}else if (userType==2){
												adapter.setPlayerList(match.getAwayTeamPlayers());
											}
										}
									}
								}
							}
						},new ErrorListener(){

							@Override
							public void onErrorResponse(VolleyError error) {
								DialogUtil.dialogDismiss(progressDia);
								ShowToast("网络错误");
							}});
			}
			return false;
		}
	});
	
	//比赛报名操作
	@OnClick(R.id.btn_match_sign_up)
	public void fun_2(View v){
		//判断当前用户是在比赛中的主队还是客队中，则该用户报名成为哪队球员
		progressDia = ProgressDialog.show(mContext, "提示", "请稍后...",false, false);
		if (judgeHomeOrAwayTeam()!=null) {
			NetWorkManager.getInstance(mContext).matchSignUp(matchInfo.getUuid(), 
					judgeHomeOrAwayTeam(),
					UserManager.getInstance(mContext).getCurrentUser().getToken(),new Listener<Void>() {
						@Override
						public void onResponse(Void response) {
							DialogUtil.dialogDismiss(progressDia);
							ShowToast("报名成功");
							//把自己加入到报名列表中
							//获取当前比赛的数据并替换当前的数据
							handler.sendEmptyMessage(1);
						}
					}, new ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							DialogUtil.dialogDismiss(progressDia);
							if(!NetWorkManager.getInstance(mContext).isNetConnected()){
								ShowToast("没有可用的网络");
								return;
							}
							if(error.networkResponse != null){
								if(error.networkResponse.statusCode == 401){
									ErrorCodeUtil.ErrorCode401(mContext);
									return;
								}
								if(error.networkResponse.statusCode == 403){
									ShowToast("比赛不在报名阶段");
									return;
								}
								if(error.networkResponse.statusCode == 404){
									if(error.networkResponse.data != null){
										String errStr = new String(error.networkResponse.data);
										ErrorDescription err = null;
										Gson gson = new Gson();
										err = gson.fromJson(errStr, new TypeToken<ErrorDescription>(){}.getType());
										if(err.getError().equals("team")){
											ShowToast("球队找不到");
										}else if(err.getError().equals("match")){
											ShowToast("比赛找不到");
										}else if(err.getError().equals("NOT_FOUND")){
											ShowToast("不是本队球员");
										}
									}
									return;
								}
								if(error.networkResponse.statusCode == 409){
//									ShowToast("在本队报过名或者在另外的一个球队里报过名");
									ShowToast("您已经报过名");
									return;
								}
							}else{
								ShowToast("报名失败");
							}
						}
					});
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
		}else{    //成员
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
		currentUserID = UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid();
		matchInfo = getIntent().getParcelableExtra(INTENT_MATCH_DETAIL_BEAN);
		if (matchInfo == null) {
			return;
		}
//		userType = getUserType();  不能用这个方法--
		userType = inHomeOrAwayTeamOrNot();
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
		
		
		matchSignUpLayout.setVisibility(View.GONE);
		signUp.setVisibility(View.GONE);
//		isHomeTeam = inHomeOrAwayTeam();
//		init_scrollview();
		initData();  
		showJersey();
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
//		if(isHomeTeam){
//			adapter.setPlayerList(matchInfo.getHomeTeamPlayers());
//		}else{
//			adapter.setPlayerList(matchInfo.getAwayTeamPlayers());
//		}
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

	private void initData() {
		if(matchInfo.getHomeTeam()!=null){
			homeTeamName.setText(matchInfo.getHomeTeam().getName());
			PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(
					matchInfo.getHomeTeam().getBadge()), homeTeamBadge,
					new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.team_bage_default)
						.showImageForEmptyUri(R.drawable.team_bage_default)
						.showImageOnFail(R.drawable.team_bage_default).build());
		}
		
		if(matchInfo.getAwayTeam()!=null){
			awayTeamName.setText(matchInfo.getAwayTeam().getName());
			PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(
					matchInfo.getAwayTeam().getBadge()), awayTeamBadge,
					new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.team_bage_default)
						.showImageForEmptyUri(R.drawable.team_bage_default)
						.showImageOnFail(R.drawable.team_bage_default).build());
		}
			
		refereeName.setText(StringUtils.defaultIfEmpty(matchInfo.getReferee().getName(),""));
		PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(
				matchInfo.getReferee().getPicture()), refreeAvatar,
				new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.icon_default_head)
					.showImageForEmptyUri(R.drawable.icon_default_head)
					.showImageOnFail(R.drawable.icon_default_head).build());

		matchField.setText(matchInfo.getField());
		matchDate.setText(StringUtils.defaultIfEmpty(DateUtil.parseTimeInMillis_hadweek(matchInfo.getKickOff()),""));//比赛时间
		matchPlayerCount.setText(matchInfo.getPlayerCount() + "人");
		if(matchInfo.getCost() == 0){
			matchCost.setText("— —");
		}else{
			matchCost.setText((int)matchInfo.getCost() + "元");
		}
		matchDescription.setText(StringUtils.defaultIfEmpty(matchInfo.getName(),""));
		//报名按钮
		if(isHomeOrAwayTeamMember()){
			matchSignUpLayout.setVisibility(View.VISIBLE);
			signUp.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 是否显示报名按钮
	 * @return
	 */
	private boolean isHomeOrAwayTeamMember(){
		List<TeamPlayer> teamPlayer = CustomApplcation.getDaoSession(mContext).getTeamPlayerDao()._queryPlayer_TeamPlayerList(currentUserID);
		for(TeamPlayer item : teamPlayer){//该用户有主队或者客队球队id
			if(item.getTeam_id().equals(matchInfo.getHomeTeam().getUuid()) || item.getTeam_id().equals(matchInfo.getAwayTeam().getUuid())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 返回是主队还是客队
	 * @return
	 */
	private int inHomeOrAwayTeamOrNot(){
		List<TeamPlayer> teamPlayer = CustomApplcation.getDaoSession(mContext).getTeamPlayerDao()._queryPlayer_TeamPlayerList(currentUserID);
//		boolean inHome = false;
		userType =3;
		for(TeamPlayer item : teamPlayer){//保证不在主队才去搜索客队
			if(item.getTeam_id().equals(matchInfo.getHomeTeam().getUuid())){
//				inHome = true;
				userType =1;
				break;
			}
			else if(item.getTeam_id().equals(matchInfo.getAwayTeam().getUuid())){
//				inHome = false;
				userType =2;
			}
		}
//		return inHome;
		return userType;
	}
	
	/**
	 * 返回是主队还是客队的id
	 * @return
	 */
	private String judgeHomeOrAwayTeam(){
		if(userType ==1){
			return matchInfo.getHomeTeam().getUuid();
		}else if (userType ==2){
			return matchInfo.getAwayTeam().getUuid();
		}
		else {
			return null;
		}
//		List<PlayerInTeam> homePlayers = matchInfo.getHomeTeamPlayers();
//		for(PlayerInTeam homeItem : homePlayers){
//			if(currentUserID.equals(homeItem.getPlayer().getUuid())){ //说明该当前球员在主队
//				whichTeamID = matchInfo.getHomeTeam().getUuid();
//				break;
//			}
//		}
//		
//		List<PlayerInTeam> awayPlayers = matchInfo.getAwayTeamPlayers();
//		for(PlayerInTeam awayItem : awayPlayers){
//			if(currentUserID.equals(awayItem.getPlayer().getUuid())){ //说明该当前球员在主队
//				whichTeamID = matchInfo.getAwayTeam().getUuid();
//				break;
//			}
//		}
//		return whichTeamID;
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
//	/**
//	 * 获取用户类型
//	 * 
//	 * @return
//	 */
	//这里不适用 -- 一定是3 
//	private int getUserType() {
//		for (PlayerInTeam homeItem : matchInfo.getHomeTeamPlayers()) {
//			if (currentUserID.equals(homeItem.getPlayer().getUuid())) { // 说明我在主队，主队是本方
//				return 1;
//			}
//		}
//		for (PlayerInTeam awayItem : matchInfo.getAwayTeamPlayers()) {
//			if (currentUserID.equals(awayItem.getPlayer().getUuid())) { // 说明我在主队，主队是本方
//				return 2;
//			}
//		}
//		return 3;
//	}
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
				self_jersey.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ArrayList<String> photos = new ArrayList<String>();
						photos.add(PhotoUtil.getAllPhotoPath(matchInfo
								.getAwayTeamJersey()));
						PictureUtil.viewLargerImage(mContext, photos);
					}
				});
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
						ShowToast("队服设置成功");// 以后只能查看队服 TODO 更新数据
						switch (userType) {
						case 1:
							matchInfo.setHomeTeamJersey(teamJersey);
							break;
						case 2:
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
						self_jersey
								.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										ArrayList<String> photos = new ArrayList<String>();
										switch (userType) {
										case 1:
											if (TextUtils.isEmpty(matchInfo
													.getHomeTeamJersey())) {
												return;
											}
											photos.add(PhotoUtil.getAllPhotoPath(matchInfo
													.getHomeTeamJersey()));
											break;
										case 2:
											if (TextUtils.isEmpty(matchInfo
													.getAwayTeamJersey())) {
												return;
											}
											photos.add(PhotoUtil.getAllPhotoPath(matchInfo
													.getAwayTeamJersey()));
											break;
										default:
											return;
										}
										PictureUtil.viewLargerImage(mContext,
												photos);
									}
								});
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
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
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
		if (requestCode == INTENT_SELECT_MATCH_JERSEY_HOME_TEAM) { // 选择球服(当前用户在本方)
			String teamJersey = intent.getExtras().getString(
					JerseyofTeamActivity.MATCH_JERSEY);

			if (TextUtils.isEmpty(teamJersey)) {
				ShowToast("设置队服失败，请重新设置");
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
				ShowToast("设置队服失败，请重新设置");
				return;
			}
			// 访问后台服务器，修改队服
			executeSetTeamJersey(teamJersey, matchInfo.getAwayTeam().getUuid());
			return;
		}
	}
	
}
