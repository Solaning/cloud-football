package com.kinth.football.ui.fragment;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.CustomApplcation;
import com.kinth.football.NetStateReceiver;
import com.kinth.football.R;
import com.kinth.football.bean.TeamInfoComplete;
import com.kinth.football.bean.User;
import com.kinth.football.bean.match.FiveDifferentData;
import com.kinth.football.bean.match.FiveMatchStatisticsResponse;
import com.kinth.football.bean.match.SkillAndMormality;
import com.kinth.football.config.PlayerPositionEnum;
import com.kinth.football.config.PushMessageEnum;
import com.kinth.football.dao.Player;
import com.kinth.football.dao.PushMessage;
import com.kinth.football.dao.PushMessageDao;
import com.kinth.football.eventbus.bean.GetPersonInfoEvent;
import com.kinth.football.eventbus.bean.ModifyPlayerBirthdayEvent;
import com.kinth.football.eventbus.bean.ModifyPlayerHeightEvent;
import com.kinth.football.eventbus.bean.ModifyPlayerNickNameEvent;
import com.kinth.football.eventbus.bean.ModifyPlayerPhotoEvent;
import com.kinth.football.eventbus.bean.ModifyPlayerPositionEvent;
import com.kinth.football.eventbus.bean.ModifyPlayerRegionEvent;
import com.kinth.football.eventbus.bean.ModifyPlayerWeightEvent;
import com.kinth.football.eventbus.bean.MomentsHasReadPushMessageEvent;
import com.kinth.football.eventbus.match.MatchFinishedEvent;
import com.kinth.football.eventbus.message.SharingCommentedPushMessageEvent;
import com.kinth.football.friend.MomentsActivity;
import com.kinth.football.listener.NetStateChangeListener;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.ui.FragmentBase;
import com.kinth.football.ui.HomeSearchActivity;
import com.kinth.football.ui.HomeSearchPlayerActivity;
import com.kinth.football.ui.HomeSearchTeamActivity;
import com.kinth.football.ui.MainActivity;
import com.kinth.football.ui.PKActivity;
import com.kinth.football.ui.cloud5ranking.Clound5RankingActivity;
import com.kinth.football.ui.match.invite.InviteMatchListActivity;
import com.kinth.football.ui.team.TeamPlayerInfo;
import com.kinth.football.util.AndroidChartUtil;
import com.kinth.football.util.DBUtil;
import com.kinth.football.util.DateUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.view.RoundImageView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.dao.query.CountQuery;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

/**
 * 云球--首页
 */
public class HomeFragment extends FragmentBase implements
		NetStateChangeListener {

	private static final int WHAT_CODE_0 = 0;
	private static final int WHAT_CODE_INIT_DATA = 102;

    private View view;
    
	@ViewInject(R.id.nav_right_image)
	private ImageView home_search; // 搜索按钮

	@ViewInject(R.id.entry_search_player)
	private LinearLayout entry_search_player;	// 搜索球员入口
	
	@ViewInject(R.id.iv_search_player)
	private ImageView searchPlayerImage;
	
	@OnClick(R.id.entry_search_player)
	public void fun_21(View v){
		Intent intent = new Intent(mContext, HomeSearchPlayerActivity.class);
		startActivity(intent);
	}

	@ViewInject(R.id.entry_search_team)
	private LinearLayout entry_search_team; // 搜索球队入口

	@ViewInject(R.id.iv_search_team)
	private ImageView searchTeamImage;
	
	@OnClick(R.id.entry_search_team)
	public void fun_22(View v){
		Intent intent = new Intent(mContext, HomeSearchTeamActivity.class);
		startActivity(intent);
	}

	@ViewInject(R.id.entry_invite_match)
	private LinearLayout entry_invite_match; // 快速越赛入口

	@ViewInject(R.id.iv_invite_match)
	private ImageView inviteMatchImage;
	
	@OnClick(R.id.entry_invite_match)
	public void fun_23(View v){
		Intent intent = new Intent(mContext, InviteMatchListActivity.class);
		startActivity(intent);
	}

	@ViewInject(R.id.entry_football_square)
	private RelativeLayout entry_football_square; // 云球广场入口
	
	@ViewInject(R.id.iv_football_square)
	private ImageView footballSquareImage;
	
	@OnClick(R.id.entry_football_square)
	public void fun_24(View v){
        Intent intent = new Intent(getActivity(), MomentsActivity.class);
        startActivity(intent);
	}
	
	@ViewInject(R.id.entry_yunwu_list)
	private LinearLayout entry_yunwu_list; // 云五排行入口

	@ViewInject(R.id.iv_yunwu_list)
	private ImageView iv_yunwu_list;
	
	@OnClick(R.id.entry_yunwu_list)
	public void fun_25(View v){
		//TODO
		Intent intent =new Intent(getActivity(), Clound5RankingActivity.class);
		startActivity(intent);
	}
	
	@ViewInject(R.id.entry_yunwu_PK)
	private LinearLayout entry_yunwu_PK; // 云五PK入口

	@ViewInject(R.id.iv_yunwu_PK)
	private ImageView iv_yunwu_PK;
	
	@OnClick(R.id.entry_yunwu_PK)
	public void fun_26(View v){
		Intent intent = new Intent(mContext, PKActivity.class);
		startActivity(intent);
	}
	
	@ViewInject(R.id.rel_football_square_count)
	private RelativeLayout footballSquareCountContainer;//云球广场新消息
	
	@ViewInject(R.id.tv_football_square_count)
	private TextView footballSquareCountNum;//云球广场新消息条数

	
//	@ViewInject(R.id.all_datacharrt)
//	private ImageView all_datacharrt;
//
//	@ViewInject(R.id.morality_img)
//	private ImageView morality_img; // 球品口碑
//
//	@ViewInject(R.id.tv_title_mor)
//	private TextView tv_title_mor;
//
//	@ViewInject(R.id.skill_img)
//	private ImageView skill_img; // 球技互评
//
//	@ViewInject(R.id.tv_title_skill)
//	private TextView tv_title_skill;
//
//	private TextView attack; // （雷达图）进攻
//
//	private TextView defence; // 防守
//
//	private TextView stren; // 体能
//
//	private TextView skill; // 技术
//
//	private TextView awareness; // 侵略性
//
//	private ImageView attack_updowneq; // 进攻（上升下降）图标
//
//	private ImageView defence_updowneq; // 防守（上升下降）图标
//
//	private ImageView stren_updowneq; // 体能（上升下降）图标
//
//	private ImageView skill_updowneq; // 技术（上升下降）图标
//
//	private ImageView awareness_updowneq; // 侵略性（上升下降）图标
//
//	private TextView attack_value2; // 进攻（变化量）
//
//	private TextView defence_value2; // 防守（变化量）
//
//	private TextView stren_value2; // 体能（变化量）
//
//	private TextView skill_value2; // 技术（变化量）
//
//	private TextView awareness_value2; // 侵略性（变化量）
//
//	LineChart lineChart01; // 折线图
//	LineChart lineChart02;
//
//	private TextView nodata_one;
//	private TextView nodata_two;
//
//	private List<View> dots; // 图片标题正文的那些点
//
//	private RadarChart radarChart; // 雷达图
//	private LinearLayout lin_radarchart;// 包裹雷达图的外面一层 --为了控制雷达图位置适配屏幕
//
//	@ViewInject(R.id.synthesize_3)
//	private TextView synthesize; // 综合数据
//
//	@ViewInject(R.id.morality_value_3)
//	private TextView morality_value; // 球品口碑
//
//	@ViewInject(R.id.skill_value_3)
//	private TextView skill_value; // 球技互评

	@ViewInject(R.id.player_img)
	private RoundImageView player_img;

	@ViewInject(R.id.player_name)
	private TextView player_name;

	@ViewInject(R.id.region)
	private TextView region;

	@ViewInject(R.id.location01)
	private TextView location01; // 该用户在球队中的位置

	@ViewInject(R.id.synthesize_3)
	private TextView synthesize;	// 综合数据

	@ViewInject(R.id.morality_value_3)
	private TextView morality_value;	// 球品口碑

	@ViewInject(R.id.skill_value_3)
	private TextView skill_value;	// 球技互评
	
	@ViewInject(R.id.synthesize_change)
	private TextView synthesize_change; // 综合数据变化值
	
	@ViewInject(R.id.change_img1)
	private ImageView change_img1; // 综合数据变化（上升下降等于图标）

	@ViewInject(R.id.morality_change)
	private TextView morality_change; // 球品口碑变化值

	@ViewInject(R.id.change_img2)
	private ImageView change_img2; // 球品口碑变化（上升下降等于图标）

	@ViewInject(R.id.skill_change)
	private TextView skill_change; // 球技互评变化值

	@ViewInject(R.id.change_img3)
	private ImageView change_img3; // 球技互评变化（上升下降等于图标）

	
	@ViewInject(R.id.detailed_information)
	private RelativeLayout detailed_information;

	@ViewInject(R.id.years)  //年龄
	private TextView years;
	
	@ViewInject(R.id.heights)  //身高
	private TextView heights;
	
	@ViewInject(R.id.weights)  //体重
	private TextView weights;
	
	@ViewInject(R.id.bmi_value)  //BMI
	private TextView bmi_value;
//	@ViewInject(R.id.all_none)
//	private TextView all_none;

	// 当没有可用的网络时，点击雷达图会奔溃。所以隐藏雷达图，显示该文本
//	private TextView nonet_nodata;

//	@ViewInject(R.id.loaddata)
//	private ProgressBar loaddataBar;

	private boolean isLoadOK_ONE = false;
	private boolean isLoadOK_TWO = false;

	public HomeFragment() {
		super();
		// TODO 自动生成的构造函数存根
	}

	@OnClick(R.id.player_img)
	public void fun_3(View v) {// 跳转球员信息
		Intent intent = new Intent(mContext, TeamPlayerInfo.class);
		intent.putExtra(TeamPlayerInfo.PLAYER_UUID,
				UserManager.getInstance(mContext).getCurrentUser()
						.getPlayer().getUuid());
		startActivity(intent);
		// if (UserManager.getInstance(mApplication).getCurrentUser() != null
		// &&
		// !TextUtils.isEmpty(UserManager.getInstance(mApplication).getCurrentUser().getPlayer().getPicture()))
		// {
		// ArrayList<String> photos = new ArrayList<String>();
		// photos.add(PhotoUtil.getAllPhotoPath(UserManager.getInstance(mApplication).getCurrentUser().getPlayer().getPicture()));
		// // PictureUtil.viewLargerImage(mApplication, photos); //--这里用这个方法会错误
		// Intent intent = new Intent(mApplication, ImageBrowserActivity.class);
		// intent.putStringArrayListExtra("photos", photos);
		// intent.putExtra("position", 0);
		// intent.putExtra("flag",1);
		// startActivity(intent);
		// }
	}

	@OnClick(R.id.nav_right_image)
	public void fun_1(View v) { // 搜索
		Intent intent = new Intent(mContext, HomeSearchActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.detailed_information)
	public void fun_11(View v) {// 点击详细信息跳转球员信息
		Intent intent = new Intent(mContext, TeamPlayerInfo.class);
		intent.putExtra(TeamPlayerInfo.PLAYER_UUID,
				UserManager.getInstance(mContext).getCurrentUser()
						.getPlayer().getUuid());
		startActivity(intent);
	}

//	private LinearLayout llt_radarchart;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		EventBus.getDefault().register(this);//事件总线
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	@Override
	public void onDetach() {
		EventBus.getDefault().unregister(this);
		super.onDetach();
	}
	private Typeface tf1;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view == null) {
			view = inflater.inflate(R.layout.fragment_recent3, container, false);
			ViewUtils.inject(this, view);
			initView();
        }
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		handler.sendEmptyMessageDelayed(WHAT_CODE_INIT_DATA, 150);
	}

	public static final HomeFragment newInstance(String key) {
		HomeFragment fragment = new HomeFragment();
		Bundle bundle = new Bundle();
		bundle.putString("Tag", key);
		fragment.setArguments(bundle);
		return fragment;
	}

	private void initView() {
		player_name.setTypeface(Typeface.SERIF);
		tf1 = Typeface.createFromAsset(mContext.getAssets(),
				"fonts/agencyb.ttf");
		years.setTypeface(tf1);
		heights.setTypeface(tf1);
		weights.setTypeface(tf1);
		bmi_value.setTypeface(tf1);
		
		synthesize.setTypeface(tf1);
		morality_value.setTypeface(tf1);
		skill_value.setTypeface(tf1);
		synthesize_change.setTypeface(tf1);
		morality_change.setTypeface(tf1);
		skill_change.setTypeface(tf1);
		
	}

	private void showCurrentUserData() {
		User currentUser = UserManager.getInstance(mContext)
				.getCurrentUser();
		if (currentUser != null && currentUser.getPlayer() != null) {
			PictureUtil.getMd5PathByUrl(
					PhotoUtil.getThumbnailPath(currentUser.getPlayer()
							.getPicture()),
					player_img,
					new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.icon_default_head)
							.showImageForEmptyUri(R.drawable.icon_default_head)
							.showImageOnFail(R.drawable.icon_default_head)
							.cacheInMemory(true).cacheOnDisk(true).build());
			player_name.setText(StringUtils.defaultIfEmpty(currentUser.getPlayer().getName(),""));
			if (TextUtils.isEmpty(currentUser.getPlayer().getProvince()) || TextUtils.isEmpty(currentUser.getPlayer().getCity())) {
				region.setText("未知");
			} else {
				region.setText(currentUser.getPlayer().getProvince() + " "
						+ currentUser.getPlayer().getCity());
			}
			if (TextUtils.isEmpty(currentUser.getPlayer().getPosition())) {
				location01.setText("未知");
			} else {
				location01.setText(StringUtils.defaultIfBlank(
						PlayerPositionEnum.getEnumFromString(
								 currentUser.getPlayer().getPosition()).getName(),
						"未知"));	
			}
			if(currentUser.getPlayer().getBirthday() != null){
				int age = DateUtil.calcAgeByBirthday(currentUser.getPlayer().getBirthday());
				years.setText(String.valueOf(age));
			}else{
				years.setText("未知");
			}
			if (currentUser.getPlayer().getHeight()!=null) {
				heights.setText("" + currentUser.getPlayer().getHeight() + "cm");
			}else {
				heights.setText("未知");
			}
			if (currentUser.getPlayer().getWeight()!=null) {
				weights.setText("" + currentUser.getPlayer().getWeight() + "kg");
			}else{
				weights.setText("未知");
			}
			if (currentUser.getPlayer().getHeight() != null && currentUser.getPlayer().getWeight() != null) {
				float he = currentUser.getPlayer().getHeight() / 100f;
				float BMI = currentUser.getPlayer().getWeight() / (he * he);
				BigDecimal b = new BigDecimal(BMI);  
				float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();  
				bmi_value.setText("" + f1);
			}else {
				bmi_value.setText("未知");
			}
		}
	}
	
	//
	private void executePlayerFMSMData() {
		User user = UserManager.getInstance(mContext).getCurrentUser();
		if(user == null || TextUtils.isEmpty(user.getToken()) || isRemoving() || isDetached()){
			return;
		}
		NetWorkManager.getInstance(mContext).getPlayerFMSMData(
				user.getPlayer().getUuid(),
				user.getToken(), new Listener<JSONObject>() {
					
					@SuppressWarnings("deprecation")
					@Override
					public void onResponse(JSONObject response) {
						Gson gson = new Gson();
						FiveMatchStatisticsResponse fiveRe = gson.fromJson(
								response.toString(),
								new TypeToken<FiveMatchStatisticsResponse>() {
								}.getType());
						if (fiveRe != null && isAdded()) {
//							// 显示雷达图右边的五项数据值，变化量(以及雷达图坐标值)
							FiveDifferentData fiveDiff = fiveRe
									.getLeagueTotal();
							FiveDifferentData fiveChange = fiveRe
									.getLeagueDelta();

							/** -----------综合数据，技术互评，球品口碑-------------- **/
							synthesize.setText(""
									+ AndroidChartUtil.roundNumber(fiveDiff
											.getComposite()));
							morality_value.setText(AndroidChartUtil
									.roundNumber(fiveRe.getMatchTotal()
											.getMorality() * 20)
									+ "");
							skill_value.setText(AndroidChartUtil
									.roundNumber(fiveRe.getMatchTotal()
											.getSkill() * 20)
									+ "");

							if (fiveChange != null) {
								// 变化值取整
								int composite_detla = AndroidChartUtil
										.roundNumber(fiveChange.getComposite());
								if (composite_detla < 0) {
									String composite = String.valueOf(
											composite_detla).substring(
											1,
											String.valueOf(composite_detla)
													.length());
									synthesize_change.setText("" + composite);

									change_img1
											.setBackgroundDrawable(mContext
													.getResources() // 红色箭头
													.getDrawable(
															R.drawable.radarchart_down3));
									synthesize_change
											.setTextColor(getResources()
													.getColor(
															R.color.red_down_text)); // 红色字体
								} else if (composite_detla > 0) {
									synthesize_change.setText(""
											+ composite_detla);
									change_img1.setBackgroundDrawable(mContext
											.getResources().getDrawable(
													R.drawable.radarchart_up2)); // 绿色箭头
									synthesize_change
											.setTextColor(getResources()
													.getColor(
															R.color.green_up_text)); // 绿色字体
								} else {
									synthesize_change.setVisibility(View.GONE);
									change_img1.setBackgroundDrawable(mContext
											.getResources().getDrawable(
													R.drawable.radarchart_eq5));
								}
							}

							SkillAndMormality sam = fiveRe.getMatchDelta();
							if (sam != null) {
								int morality_detla = AndroidChartUtil
										.roundNumber(sam.getMorality() * 20);
								if (morality_detla < 0) {
									String morality = String.valueOf(
											morality_detla).substring(
											1,
											String.valueOf(morality_detla)
													.length()); // 去掉减号
									morality_change.setText("" + morality); // 取整

									change_img2
											.setBackgroundDrawable(mContext
													.getResources()
													.getDrawable(
															R.drawable.radarchart_down3));
									morality_change.setTextColor(getResources()
											.getColor(R.color.red_down_text));
								} else if (morality_detla > 0) {
									morality_change
											.setText("" + morality_detla);
									change_img2.setBackgroundDrawable(mContext
											.getResources().getDrawable(
													R.drawable.radarchart_up2));
									morality_change.setTextColor(getResources()
											.getColor(R.color.green_up_text));
								} else {
									change_img2.setBackgroundDrawable(mContext
											.getResources().getDrawable(
													R.drawable.radarchart_eq5));
									morality_change.setVisibility(View.GONE);
								}

								int skill_detla = AndroidChartUtil
										.roundNumber(sam.getSkill() * 20);
								if (skill_detla < 0) {
									String skill = String.valueOf(skill_detla)
											.substring(
													1,
													String.valueOf(skill_detla)
															.length());
									skill_change.setText("" + skill);
									change_img3
											.setBackgroundDrawable(mContext
													.getResources()
													.getDrawable(
															R.drawable.radarchart_down3));
									skill_change.setTextColor(getResources()
											.getColor(R.color.red_down_text));
								} else if (skill_detla > 0) {
									skill_change.setText("" + skill_detla);
									change_img3.setBackgroundDrawable(mContext
											.getResources().getDrawable(
													R.drawable.radarchart_up2));
									skill_change.setTextColor(getResources()
											.getColor(R.color.green_up_text));
								} else {
									change_img3.setBackgroundDrawable(mContext
											.getResources().getDrawable(
													R.drawable.radarchart_eq5));
									skill_change.setVisibility(View.GONE);
								}
							}
							isLoadOK_TWO = true; // 加载完成
							handler.sendEmptyMessage(WHAT_CODE_0);
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							// ShowToast("当前网络不可用");
						}
						if (error.networkResponse == null) {
							// ShowToast("HomeFragment-executePlayerFMSMData-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							// ErrorCodeUtil.ErrorCode401(mApplication);
						} else if (error.networkResponse.statusCode == 404) {
							// ShowToast("球员找不到,折线图数据未能加载");
						}
//						loaddataBar.setVisibility(View.GONE);
					}
				});
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!NetStateReceiver.netList.contains(this)) {
			NetStateReceiver.netList.add(this);
		}
	}

	public void onDestroy() {
		if (NetStateReceiver.netList.contains(this)) {
			NetStateReceiver.netList.remove(this);// 取消监听推送的消息
		}
		if (MainActivity.pushMessageList.contains(this)) {
			MainActivity.pushMessageList.remove(this);// 取消监听推送的消息
		}
		super.onDestroy();
	}

	@Override
	public void onNetStateChange() {
		// TODO Auto-generated method stub
		if (NetWorkManager.getInstance(mContext).isNetConnected()) {
			// //当有网络时，重新加载图表，不过图表显示会出问题
//			loaddataBar.setVisibility(View.VISIBLE);
			showCurrentUserData();
			executePlayerFMSMData();
		} else {
			// ShowToast("没有可用的网络");
		}
	}

	Handler handler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what){
			case WHAT_CODE_0:
				if (isLoadOK_ONE == true && isLoadOK_TWO == true) {
					isLoadOK_ONE = false;
					isLoadOK_TWO = false;

//					loaddataBar.setVisibility(View.GONE);
				}
				break;
			case WHAT_CODE_INIT_DATA:
				if(mContext == null || isRemoving() || isDetached()){
					return false;
				}
				searchPlayerImage.setImageResource(R.drawable.search_player1);
				searchTeamImage.setImageResource(R.drawable.search_team1);
				inviteMatchImage.setImageResource(R.drawable.invite_match1);
				footballSquareImage.setImageResource(R.drawable.football_square1);

				iv_yunwu_list.setImageResource(R.drawable.yunwu_list1);
				iv_yunwu_PK.setImageResource(R.drawable.yunwu_pk1);
				// 获取球员最近五场比赛数据
				executePlayerFMSMData();
				// 获取当前用户基本信息
				showCurrentUserData();
				executeGetMyTeamList();
				break;
			}
			return false;
		}
	});

	// 收到比赛结束消息，刷新当前图表数据
	public void onEventMainThread(MatchFinishedEvent matchFinishedEvent){
//		loaddataBar.setVisibility(View.VISIBLE);

		showCurrentUserData();
		executePlayerFMSMData();
	}
	
	/**
	 * 朋友圈评论推送消息
	 * @param sharingCommentedPushMessageEvent
	 */
	public void onEventMainThread(SharingCommentedPushMessageEvent sharingCommentedPushMessageEvent){
		countUnreadMessage();
	}

	public void onEventMainThread(MomentsHasReadPushMessageEvent momentsHasReadPushMessageEvent){
		countUnreadMessage();
	}
	
	//修改用户头像
	public void onEventMainThread(ModifyPlayerPhotoEvent modifyPlayerPhotoEvent){
		PictureUtil.getMd5PathByUrl(PhotoUtil.getAllPhotoPath(modifyPlayerPhotoEvent.getNewPhoto()),
				player_img);
	}
	
	//修改用户昵称
	public void onEventMainThread(ModifyPlayerNickNameEvent modifyPlayerNickNameEvent){
		player_name.setText(modifyPlayerNickNameEvent.getNewNick());
	}
	
	//修改用户地区
	public void onEventMainThread(ModifyPlayerRegionEvent modifyPlayerRegionEvent){
		region.setText(modifyPlayerRegionEvent.getNewProvinceName() + " " + modifyPlayerRegionEvent.getNewCityName());
	}
	
	//修改用户位置
	public void onEventMainThread(ModifyPlayerPositionEvent modifyPlayerPositionEvent){
		location01.setText(PlayerPositionEnum
				.getEnumFromString(modifyPlayerPositionEvent.getNewPosition()).getName());
	}
	
	//修改用户身高
	public void onEventMainThread(ModifyPlayerHeightEvent modifyPlayerHeightEvent){
		heights.setText(modifyPlayerHeightEvent.getNewHeight() + "cm");
		//重新计算BMI
		User user = UserManager.getInstance(mContext).getCurrentUser();
		if (user.getPlayer().getHeight() != null && user.getPlayer().getWeight() != null) {
			float he = user.getPlayer().getHeight() / 100f;
			float BMI = user.getPlayer().getWeight() / (he * he);
			BigDecimal b = new BigDecimal(BMI);  
			float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();  
			bmi_value.setText("" + f1);
		}
	}
	
	//修改用户体重
	public void onEventMainThread(ModifyPlayerWeightEvent modifyPlayerWeightEvent){
		weights.setText(modifyPlayerWeightEvent.getNewWeight() + "kg");
		//重新计算BMI
		User user = UserManager.getInstance(mContext).getCurrentUser();
		if (user.getPlayer().getHeight() != null && user.getPlayer().getWeight() != null) {
			float he = user.getPlayer().getHeight() / 100f;
			float BMI = user.getPlayer().getWeight() / (he * he);
			BigDecimal b = new BigDecimal(BMI);  
			float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();  
			bmi_value.setText("" + f1);
		}
	}
	
	public void onEventMainThread(ModifyPlayerBirthdayEvent modifyPlayerBirthdayEvent){
		int age = DateUtil.calcAgeByBirthday(modifyPlayerBirthdayEvent.getNewBirthday());
		years.setText(String.valueOf(age));
	}
	
	//获取到当前用户的信息的事件
	public void onEventMainThread(GetPersonInfoEvent getPersonInfoEvent){
		Player player = getPersonInfoEvent.getPlayer();
		if(player == null){
			return;
		}
		ImageLoader.getInstance().displayImage(
						PhotoUtil.getAllPhotoPath(player.getPicture()),
						player_img,
						new DisplayImageOptions.Builder()
								.showImageOnLoading(
										R.drawable.icon_default_head)
								.showImageForEmptyUri(
										R.drawable.icon_default_head)
								.showImageOnFail(
										R.drawable.icon_default_head)
								.cacheInMemory(true)
								.cacheOnDisk(true).build());
		player_name.setText(org.apache.commons.lang3.StringUtils
						.defaultIfEmpty(player.getName(),
								"未知"));
		if (TextUtils.isEmpty(player.getProvince())
				|| TextUtils.isEmpty(player.getCity())) {
			region.setText("未知");
		} else {
			region.setText(player.getProvince() + " "
					+ player.getCity());
		}
		if (TextUtils.isEmpty(player.getPosition())) {
			location01.setText("未知");
		} else {
			location01.setText(StringUtils.defaultIfBlank(
					PlayerPositionEnum.getEnumFromString(
							player.getPosition()).getName(),
					"未知"));	
		}

		isLoadOK_ONE = true; // 加载完成  TODO
		handler.sendEmptyMessage(WHAT_CODE_0);
	}
	
	private void countUnreadMessage(){
		QueryBuilder<PushMessage> qb1 = CustomApplcation.getDaoSession(null)
				.getPushMessageDao().queryBuilder();
		qb1.where(PushMessageDao.Properties.Type.eq(PushMessageEnum.SHARING_COMMENTED.getValue()),PushMessageDao.Properties.HasRead.eq(false));
		CountQuery<PushMessage> query = qb1.buildCount();
		long unReadPushMessage = query.count();
		//朋友圈显示未读评论数量
		if(unReadPushMessage <= 0l){
			footballSquareCountContainer.setVisibility(View.GONE);
		}else{
			footballSquareCountContainer.setVisibility(View.VISIBLE);
			footballSquareCountNum.setText(String.valueOf(unReadPushMessage));
		}
	}
	
	//查询我所有球队信息
	private void executeGetMyTeamList(){
		User user = UserManager.getInstance(mContext).getCurrentUser();
		if(user == null || TextUtils.isEmpty(user.getToken()) || isRemoving() || isDetached()){
			return;
		}
		NetWorkManager.getInstance(mContext).getMyTeamList(UserManager.getInstance(mContext).getCurrentUser().getToken(), new Listener<JSONArray>() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void onResponse(JSONArray response) {
				Gson gson = new Gson();
				List<TeamInfoComplete> teamInfoCompleteList = null;
				try {
					teamInfoCompleteList = gson.fromJson(response.toString(),
							new TypeToken<List<TeamInfoComplete>>() {
							}.getType());
				} catch (JsonSyntaxException e) {
					teamInfoCompleteList = null;
					e.printStackTrace();
				}
				
				//将从服务器上获取得到的“我的球队”数据加入本地数据库，缓存
				if(teamInfoCompleteList != null && teamInfoCompleteList.size() != 0){
					for(TeamInfoComplete item : teamInfoCompleteList){//把点赞信息设置到球队
						if(item.getTeam() != null){
							item.getTeam().setLike(item.getLike());
							item.getTeam().setLiked(item.isLiked());
						}
					}
					new InsertMyTeamDataAsyTask().execute(teamInfoCompleteList);
				}
			}
		}, new ErrorListener() { 
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				if(!NetWorkManager.getInstance(mContext).isNetConnected()) {
				}else if(error.networkResponse == null) {
				}else if(error.networkResponse.statusCode == 401){
					ErrorCodeUtil.ErrorCode401(mContext);
				}
			}
		} );
	}
	
	/**
	 * 异步将从服务器上获取得到的“我的球队”数据加入到本地数据库中
	 * @author Botision.Huang
	 * Date: 2015-3-26
	 * Descp:
	 */
	public class InsertMyTeamDataAsyTask extends AsyncTask<List<TeamInfoComplete>, Void, Void>{

		@Override
		protected Void doInBackground(List<TeamInfoComplete>... params) {
			DBUtil.saveTeamInfoListToDB(mContext, params[0]);
			return null;
		}
	}
}
