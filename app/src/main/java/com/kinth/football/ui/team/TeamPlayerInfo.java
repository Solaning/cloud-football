package com.kinth.football.ui.team;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.adapter.MyLineChartViewAdapter;
import com.kinth.football.bean.PlayerAllMatchsResultResponse;
import com.kinth.football.bean.Record;
import com.kinth.football.bean.User;
import com.kinth.football.bean.match.FiveDifferentData;
import com.kinth.football.bean.match.FiveMatchStatisticsResponse;
import com.kinth.football.bean.match.SkillAndMormality;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.chat.bean.ChatRecent;
import com.kinth.football.chat.db.ChatDBManager;
import com.kinth.football.chat.ui.ChatActivity;
import com.kinth.football.config.MatchResultEnum;
import com.kinth.football.config.PlayerPositionEnum;
import com.kinth.football.dao.Player;
import com.kinth.football.dao.Team;
import com.kinth.football.dao.TeamDao;
import com.kinth.football.dao.TeamPlayer;
import com.kinth.football.friend.MomentPersonalZoneActivity;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.ActivityBase;
import com.kinth.football.ui.fragment.MatchFragment;
import com.kinth.football.ui.match.FriendlyListActivity;
import com.kinth.football.ui.match.TournamentActivity;
import com.kinth.football.util.AndroidChartUtil;
import com.kinth.football.util.DateUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.ImageLoadOptions;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.PixelUtil;
import com.kinth.football.util.QuitWay;
import com.kinth.football.view.RoundImageView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 球员信息
 * 
 * @author Botision.Huang Date: 2015-3-29 Descp:在使用该类的时候，需要通过Intent传递一个用户ID
 */
@ContentView(R.layout.activity_templayer_info)
public class TeamPlayerInfo extends ActivityBase {

	public static final String PLAYER_UUID = "PLAYER_UUID"; // intent中传递的内容(用户UUID)
	public String intentPlayerId; // 从inent中传递过来的用户ID

	@ViewInject(R.id.teamplayerinfo_lin)
	private LinearLayout teamplayerinfo_lin;

	@ViewInject(R.id.vp)
	private ViewPager vp; // 用于存放划动两个折线图

	@ViewInject(R.id.tv_title)
	// 每一个Pager的图示说明
	private TextView tv_title;

	private List<LinearLayout> lineChartLinearLayouts; // 每一个折线图的布局都是一个LinearLayout
	private int[] lineChartLayoutIDs; // 两个折线图LinearLayout的ID集合
	private String[] titles; // 每一项标题
	private List<View> dots; // 图片标题正文的那些点

	LineChart lineChart_01; // 折线图01
	LineChart lineChart_02; // 折线图02
	TextView notice_none;
	TextView notice_none_two;
	TextView nodata_one;
	TextView nodata_two;

	// @ViewInject(R.id.radarchart) //雷达图
	private RadarChart radarChart;
	private LinearLayout lin_radarchart; // 包裹雷达图的外面一层 --为了控制雷达图位置适配屏幕

	private Typeface tf; // 字体样式
	private Typeface tf2;

	@ViewInject(R.id.synthesize_3)
	private TextView synthesize; // 综合数据

	@ViewInject(R.id.morality_value_3)
	private TextView morality_value; // 球品口碑

	@ViewInject(R.id.skill_value_3)
	private TextView skill_value; // 球技互评

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

	@ViewInject(R.id.tv_set_nick)
	private TextView tv_set_nick; // 昵称

	@ViewInject(R.id.result_more)
	// 总比赛战绩=====更多
	private LinearLayout result_more;

	@ViewInject(R.id.present_count)
	// 总比赛战绩中出场次数
	private TextView present_count;

	@ViewInject(R.id.win_count)
	// 总比赛战绩中赢的次数
	private TextView win_count;

	@ViewInject(R.id.equal_count)
	// 总比赛战绩中平的次数
	private TextView equal_count;

	@ViewInject(R.id.failed_count)
	private TextView failed_count; // 总比赛战绩中负的次数

	@ViewInject(R.id.one)
	private ImageView one;

	@ViewInject(R.id.two)
	private ImageView two;

	@ViewInject(R.id.third)
	private ImageView third;

	@ViewInject(R.id.four)
	private ImageView four;

	@ViewInject(R.id.five)
	private ImageView five;

	private ImageView[] matchResult_Imgs = new ImageView[5];

	@ViewInject(R.id.friendly_result_more)
	// 友谊赛战绩=======更多
	private TextView friendly_result_more;

	@ViewInject(R.id.friend_present_count)
	private TextView friend_present_count; // 友谊赛战绩出场次数

	@ViewInject(R.id.friendly_win_count)
	private TextView friendly_win_count; // 友谊赛战绩赢次数

	@ViewInject(R.id.friendly_equal_count)
	private TextView friendly_equal_count; // 友谊赛战绩平次数

	@ViewInject(R.id.friendly_failed_count)
	private TextView friendly_failed_count; // 友谊赛战绩负次数

	@ViewInject(R.id.friend_one)
	private ImageView friend_one;

	@ViewInject(R.id.friend_two)
	private ImageView friend_two;

	@ViewInject(R.id.friend_third)
	private ImageView friend_third;

	@ViewInject(R.id.friend_four)
	private ImageView friend_four;

	@ViewInject(R.id.friend_five)
	private ImageView friend_five;

	private ImageView[] friendResult_Imgs = new ImageView[5];

	@ViewInject(R.id.friend_none)
	private TextView friend_none;

	@ViewInject(R.id.all_none)
	private TextView all_none;

	@ViewInject(R.id.horview)
	// 我的球队列表（可划动）
	private LinearLayout horview;

	@ViewInject(R.id.no_anyteam)
	private TextView no_anyteam;

	@ViewInject(R.id.iv_set_avator)
	private RoundImageView iv_set_avator;

	@ViewInject(R.id.team_role1)
	private TextView team_role1;

	@ViewInject(R.id.team_role2)
	// 队长字段（目前未做任何关联操作）默认隐藏
	private TextView team_role2;

	@ViewInject(R.id.tv_set_erea)
	private TextView tv_set_erea;

	@ViewInject(R.id.years)
	// 年龄
	private TextView years;

	@ViewInject(R.id.heights)
	// 身高
	private TextView heights;

	@ViewInject(R.id.weights)
	// 体重
	private TextView weights;

	@ViewInject(R.id.bmi_value)
	// BMI
	private TextView bmi_value;

	@ViewInject(R.id.nav_right_image)
	private ImageView home_search;

	@ViewInject(R.id.all_datacharrt)
	private ImageView all_datacharrt;

	@ViewInject(R.id.morality_img)
	// 球品口碑
	private ImageView morality_img;

	@ViewInject(R.id.tv_title_mor)
	private TextView tv_title_mor;

	@ViewInject(R.id.skill_img)
	// 球技互评
	private ImageView skill_img;

	@ViewInject(R.id.tv_title_skill)
	private TextView tv_title_skill;

	@ViewInject(R.id.view_chat_and_zone)
	// 发起聊天与个人空间上面的一天线
	private View view_chat_and_zone;

	@ViewInject(R.id.llt_chat_and_zone)
	// 发起聊天与个人空间整一块
	private LinearLayout llt_chat_and_zone;

	@ViewInject(R.id.start_chat)
	// 发起聊天
	private LinearLayout start_chat;

	@ViewInject(R.id.person_zone)
	// 个人空间
	private LinearLayout person_zone;

	private TextView attack; // （雷达图）进攻

	private TextView defence; // 防守

	private TextView stren; // 体能

	private TextView skill; // 技术

	private TextView awareness; // 侵略性

	private ImageView attack_updowneq; // 进攻（上升下降）图标

	private ImageView defence_updowneq; // 防守（上升下降）图标

	private ImageView stren_updowneq; // 体能（上升下降）图标

	private ImageView skill_updowneq; // 技术（上升下降）图标

	private ImageView awareness_updowneq; // 侵略性（上升下降）图标

	private TextView attack_value2; // 进攻（变化量）

	private TextView defence_value2; // 防守（变化量）

	private TextView stren_value2; // 体能（变化量）

	private TextView skill_value2; // 技术（变化量）

	private TextView awareness_value2; // 侵略性（变化量）

	private LinearLayout llt_radarchart;

	@ViewInject(R.id.tournament)
	// 个人锦标赛
	private ImageView tournament;

	@ViewInject(R.id.nonet_nodata)
	private TextView nonet_nodata;

	@OnClick(R.id.start_chat)
	public void fun_3(View v) {
		startChat();
	}

	@OnClick(R.id.person_zone)
	public void fun_13(View v) {
		Intent intent = new Intent(mContext, MomentPersonalZoneActivity.class);
		intent.putExtra(MomentPersonalZoneActivity.INTENT_PLAYER_UUID,
				intentPlayerId);
		mContext.startActivity(intent);
	}

	@OnClick(R.id.iv_set_avator)
	public void fun_6(View v) {
		if (thisPlayer != null && !TextUtils.isEmpty(thisPlayer.getPicture())) {
			ArrayList<String> photos = new ArrayList<String>();
			photos.add(PhotoUtil.getAllPhotoPath(thisPlayer.getPicture()));
			PictureUtil.viewLargerImage(mContext, photos);
		}
	}

	@OnClick(R.id.result_more)
	public void fun_1(View v) {
		// Intent intent = new Intent(mContext, AllMatchListActivity.class);
		// intent.putExtra(MatchFragment.INTENT_ALL_MATCH_DATA, intentPlayerId);
		// startActivity(intent);
		Intent intent = new Intent(mContext,
				TeamPlayerMatchRecordActivity.class);
		intent.putExtra(PLAYER_UUID, intentPlayerId);
		startActivity(intent);
	}

	@OnClick(R.id.friendly_result_more)
	public void fun_2(View v) {
		Intent intent = new Intent(mContext, FriendlyListActivity.class);
		intent.putExtra(MatchFragment.INTENT_FRIEND_MATCH_DATA, intentPlayerId);
		startActivity(intent);
	}

	@OnClick(R.id.tournament)
	public void fun_4(View v) {// 球员里的金超联赛
	// Intent intent = new Intent(mContext, CommonWebViewActivity.class);
	// intent.putExtra(CommonWebViewActivity.INTENT_TITLE, "金超联赛");
	// String url = UrlConstants.GOLEN_PERSON_WEB_URL + intentPlayerId;
	// intent.putExtra(CommonWebViewActivity.INTENT_URL, url);
	// startActivity(intent);
		Intent intent = new Intent(mContext, TournamentActivity.class);
		intent.putExtra(TournamentActivity.LINK_IN_WHERE,
				TournamentActivity.LINK_IN_PERSONINFO);
		intent.putExtra(TournamentActivity.INTENT_PLAYER_UUID, intentPlayerId);// 需要球员的id
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(teamplayerinfo_lin, new BitmapDrawable(
				getResources(), background));

		int realWidth = CustomApplcation.getInstance().getParameters()
				.getmScreenWidth()
				- PixelUtil.dp2px(20);// margin == 10 去掉左右共20dp
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				realWidth, Math.round(realWidth / 4.17f));// 图片的宽高比例
		// Math.round(realWidth / 2.25f));
		params.gravity = Gravity.CENTER;
		tournament.setLayoutParams(params);
		// ImageLoader.getInstance().displayImage("drawable://" +
		// R.drawable.tournament_player, jinchao,
		// ImageLoadOptions.getDisplayDrawableBuilder().build());
		tournament.setImageResource(R.drawable.tournament_player);

		ImageLoader.getInstance().displayImage(
				"drawable://" + R.drawable.icon_default_head, iv_set_avator,
				ImageLoadOptions.getDisplayDrawableBuilder().build());

		intentPlayerId = this.getIntent().getStringExtra(PLAYER_UUID);
		if (intentPlayerId == null) {
			return;
		}
		initView();

		// "发起聊天"按钮的判断显示
		IsShowStartChat();

		// 将MainActivity加入到QuitWay中的activity列表中
		QuitWay.activityList.add(TeamPlayerInfo.this);
	}

	private void initView() {
		initTopBarForLeft("球员信息");

		// 总比赛战绩结果图表集合
		matchResult_Imgs[0] = one;
		matchResult_Imgs[1] = two;
		matchResult_Imgs[2] = third;
		matchResult_Imgs[3] = four;
		matchResult_Imgs[4] = five;

		// 友谊赛战绩结果图表集合
		friendResult_Imgs[0] = friend_one;
		friendResult_Imgs[1] = friend_two;
		friendResult_Imgs[2] = friend_third;
		friendResult_Imgs[3] = friend_four;
		friendResult_Imgs[4] = friend_five;

		// init 云5数据的布局
		init_llt_radarchart();

		tf = Typeface.createFromAsset(getAssets(),
				"fonts/OpenSans-LightItalic.ttf");

		tv_set_nick.setTypeface(Typeface.SERIF);

		team_role2.setVisibility(View.GONE);

		tf2 = Typeface.createFromAsset(mContext.getAssets(),
				"fonts/agencyb.ttf");
		years.setTypeface(tf2);
		heights.setTypeface(tf2);
		weights.setTypeface(tf2);
		bmi_value.setTypeface(tf2);

		synthesize.setTypeface(tf2);
		morality_value.setTypeface(tf2);
		skill_value.setTypeface(tf2);
		synthesize_change.setTypeface(tf2);
		morality_change.setTypeface(tf2);
		skill_change.setTypeface(tf2);
		// 获取当前用户的用户信息
		if (intentPlayerId.equals(UserManager.getInstance(mContext)
				.getCurrentUser().getPlayer().getUuid())) {
			showUserManagerData();
		}
		executePlayerInfo();

		// 获取用户的球队
		executeUserTeamList();

		// 为ViewPager设置基本参数
		setBasicForViewPager();

		// 球员近5场比赛统计数据(各种图表数据)
		executePlayerFMSMData();

		// 球员总比赛战绩（出场次数，胜平负等）
		executeAllMatchsResult();

		// 球员友谊赛战绩（出场次数，胜平负等）
		executeFriendMatchsResult();
	}

	private void init_llt_radarchart() {
		llt_radarchart = (LinearLayout) LinearLayout.inflate(mContext,
				R.layout.radarchart_layout, null);
		nonet_nodata = (TextView) llt_radarchart
				.findViewById(R.id.nonet_nodata);
		radarChart = (RadarChart) llt_radarchart.findViewById(R.id.radarchart);
		lin_radarchart = (LinearLayout) llt_radarchart
				.findViewById(R.id.lin_radarchart);
		attack = (TextView) llt_radarchart.findViewById(R.id.attack_value); // （雷达图）进攻

		defence = (TextView) llt_radarchart.findViewById(R.id.defence_value); // 防守

		stren = (TextView) llt_radarchart.findViewById(R.id.stren_value); // 体能

		skill = (TextView) llt_radarchart.findViewById(R.id.skill_value); // 技术

		awareness = (TextView) llt_radarchart
				.findViewById(R.id.awareness_value); // 侵略性

		attack_updowneq = (ImageView) llt_radarchart
				.findViewById(R.id.attack_updowneq); // 进攻（上升下降）图标

		defence_updowneq = (ImageView) llt_radarchart
				.findViewById(R.id.defence_updowneq); // 防守（上升下降）图标

		stren_updowneq = (ImageView) llt_radarchart
				.findViewById(R.id.stren_updowneq); // 体能（上升下降）图标

		skill_updowneq = (ImageView) llt_radarchart
				.findViewById(R.id.skill_updowneq); // 技术（上升下降）图标

		awareness_updowneq = (ImageView) llt_radarchart
				.findViewById(R.id.awareness_updowneq); // 侵略性（上升下降）图标

		attack_value2 = (TextView) llt_radarchart
				.findViewById(R.id.attack_value2); // 进攻（变化量）

		defence_value2 = (TextView) llt_radarchart
				.findViewById(R.id.defence_value2); // 防守（变化量）

		stren_value2 = (TextView) llt_radarchart
				.findViewById(R.id.stren_value2); // 体能（变化量）

		skill_value2 = (TextView) llt_radarchart
				.findViewById(R.id.skill_value2); // 技术（变化量）

		awareness_value2 = (TextView) llt_radarchart
				.findViewById(R.id.awareness_value2); // 侵略性（变化量）
	}

	private void showUserManagerData() {
		User user = UserManager.getInstance(mContext).getCurrentUser();
		if (user == null) {
			return;
		}
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(user.getPlayer().getPicture()),
				iv_set_avator,
				new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.icon_head)
						.showImageForEmptyUri(R.drawable.icon_head)
						.showImageOnFail(R.drawable.icon_head)
						.cacheInMemory(true).cacheOnDisk(true).build());

		// tv_set_nick.setText(user.getPlayer().getName());
		tv_set_nick.setText(StringUtils.defaultIfEmpty(user.getPlayer()
				.getName(), ""));
		if (user.getPlayer().getPosition() != null
				&& !user.getPlayer().getPosition().isEmpty()) {
			team_role1.setText(StringUtils.defaultIfEmpty(PlayerPositionEnum
					.getEnumFromString(user.getPlayer().getPosition())
					.getName(), "未知"));
		}
		// if(user.getPlayer().getCityId() == null){
		// tv_set_erea.setText("未知");
		// }else{
		// tv_set_erea.setText(new
		// CityDao(mContext).getCityByCityId(user.getPlayer().getCityId()).getName());
		// }
		if (user.getPlayer().getProvince() != null
				&& !user.getPlayer().getProvince().isEmpty()
				&& user.getPlayer().getCity() != null
				&& !user.getPlayer().getCity().isEmpty()) {
			tv_set_erea.setText(user.getPlayer().getProvince() + " "
					+ user.getPlayer().getCity());
		} else {
			tv_set_erea.setText("未知");
		}
		if (user.getPlayer().getBirthday() != null) {
			int age = DateUtil
					.calcAgeByBirthday(user.getPlayer().getBirthday());
			years.setText(String.valueOf(age));
		} else {
			years.setText("未知");
		}
		if (user.getPlayer().getHeight() != null) {
			heights.setText("" + user.getPlayer().getHeight() + "cm");
		} else {
			heights.setText("未知");
		}
		if (user.getPlayer().getWeight() != null) {
			weights.setText("" + user.getPlayer().getWeight() + "kg");
		} else {
			weights.setText("未知");
		}
		if (user.getPlayer().getHeight() != null
				&& user.getPlayer().getWeight() != null) {
			float he = user.getPlayer().getHeight() / 100f;
			float BMI = user.getPlayer().getWeight() / (he * he);
			BigDecimal b = new BigDecimal(BMI);
			float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
			bmi_value.setText("" + f1);
		} else {
			bmi_value.setText("未知");
		}

	}

	private void IsShowStartChat() {
		if (UserManager.getInstance(mContext).getCurrentUser().getPlayer()
				.getUuid().equals(intentPlayerId)) {
			start_chat.setVisibility(View.GONE);
			// person_zone.setVisibility(View.GONE);
			// llt_chat_and_zone.setVisibility(View.GONE);
			// view_chat_and_zone.setVisibility(View.GONE);
		} else {
			start_chat.setVisibility(View.VISIBLE);
			// person_zone.setVisibility(View.VISIBLE);
			// llt_chat_and_zone.setVisibility(View.VISIBLE);
			// view_chat_and_zone.setVisibility(View.VISIBLE);
		}
	}

	Player thisPlayer = null;

	private void executePlayerInfo() {
		NetWorkManager.getInstance(mContext).getPlayerInfoByUuid(
				intentPlayerId,
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Gson gson = new Gson();
						thisPlayer = gson.fromJson(response.toString(),
								new TypeToken<Player>() {
								}.getType());
						if (thisPlayer != null) {
							PictureUtil.getMd5PathByUrl(
									PhotoUtil.getThumbnailPath(thisPlayer
											.getPicture()),
									iv_set_avator,
									new DisplayImageOptions.Builder()
											.showImageOnLoading(
													R.drawable.icon_head)
											.showImageForEmptyUri(
													R.drawable.icon_head)
											.showImageOnFail(
													R.drawable.icon_head)
											.cacheInMemory(true)
											.cacheOnDisk(true).build());

							tv_set_nick.setText(thisPlayer.getName());
							team_role1.setText(StringUtils.defaultIfEmpty(
									PlayerPositionEnum.getEnumFromString(
											thisPlayer.getPosition()).getName(),
									"未知"));
							// if(thisPlayer.getCityId() == null){
							// tv_set_erea.setText("未知");
							// }else{
							// tv_set_erea.setText(new
							// CityDao(mContext).getCityByCityId(thisPlayer.getCityId()).getName());
							// }
							if (thisPlayer.getProvince() != null
									&& thisPlayer.getCity() != null) {
								tv_set_erea.setText(thisPlayer.getProvince()
										+ " " + thisPlayer.getCity());
							} else {
								tv_set_erea.setText("未知");
							}
							if (thisPlayer.getBirthday() != null) {
								int age = DateUtil.calcAgeByBirthday(thisPlayer
										.getBirthday());
								years.setText(String.valueOf(age));
							} else {
								years.setText("未知");
							}
							if (thisPlayer.getHeight() != null) {
								heights.setText("" + thisPlayer.getHeight()
										+ "cm");
							} else {
								heights.setText("未知");
							}
							if (thisPlayer.getWeight() != null) {
								weights.setText("" + thisPlayer.getWeight()
										+ "kg");
							} else {
								weights.setText("未知");
							}
							if (thisPlayer.getWeight() != null
									&& thisPlayer.getHeight() != null) {
								float he = thisPlayer.getHeight() / 100f;
								float BMI = thisPlayer.getWeight() / (he * he);

								BigDecimal b = new BigDecimal(BMI);
								float f1 = b.setScale(1,
										BigDecimal.ROUND_HALF_UP).floatValue();

								bmi_value.setText("" + f1);
							} else {
								bmi_value.setText("未知");
							}
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							// ShowToast("当前网络不可用");
							return;
						}
						if (error.networkResponse == null) {
							// ShowToast("服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else if (error.networkResponse.statusCode == 404) {
							// ShowToast("球员找不到");
						}
					}
				});
	}

	private void executeUserTeamList() {
		NetWorkManager.getInstance(mContext).getUserTeamList(intentPlayerId,
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						// TODO Auto-generated method stub
						// System.out.println("球员信息中我的球队=======" + response);
						Gson gson = new Gson();
						List<Team> teamList = null;
						teamList = gson.fromJson(response.toString(),
								new TypeToken<ArrayList<Team>>() {
								}.getType());
						if (teamList != null) {
							addUserTeamToHorscrollView(teamList); // 添加显示我的球队
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							// ShowToast("没有可用的网络");
						} else if (error.networkResponse == null) {
							// ShowToast("服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						}
					}
				});
	}

	private void addUserTeamToHorscrollView(List<Team> teamList) {
		if (teamList.size() != 0) { // 有球队
			for (final Team item : teamList) {
				RelativeLayout rel = (RelativeLayout) LayoutInflater.from(this)
						.inflate(R.layout.horscrollview_item_layout, null);

				RoundImageView roundImg = (RoundImageView) rel
						.findViewById(R.id.team_one);
				TextView team_one_name = (TextView) rel
						.findViewById(R.id.team_one_name);

				PictureUtil.getMd5PathByUrl(
						PhotoUtil.getThumbnailPath(item.getBadge()),
						roundImg,
						new DisplayImageOptions.Builder()
								.showImageOnLoading(
										R.drawable.team_bage_default)
								.showImageForEmptyUri(
										R.drawable.team_bage_default)
								.showImageOnFail(R.drawable.team_bage_default) // 默认球队队徽
								.cacheInMemory(true).cacheOnDisk(true).build());

				if (item.getName() != null) {
					team_one_name.setText(item.getName());
				} else {
					team_one_name.setText("未知");
				}

				rel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// Intent intent = new Intent(mContext,
						// TeamInfoActivity.class);
						// intent.putExtra(TeamInfoActivity.INTENT_TEAM_INFO_BEAN,
						// item);
						// startActivity(intent);
						boolean isGuest = true;// 是否来宾权限
						List<TeamPlayer> teamPlayerList = CustomApplcation
								.getDaoSession(mContext)
								.getTeamPlayerDao()
								._queryPlayer_TeamPlayerList(
										UserManager.getInstance(mContext)
												.getCurrentUser().getPlayer()
												.getUuid());
						for (TeamPlayer teamPlayer : teamPlayerList) {
							Team team = CustomApplcation
									.getDaoSession(mContext)
									.getTeamDao()
									.queryBuilder()
									.where(TeamDao.Properties.Uuid
											.eq(teamPlayer.getTeam_id()))
									.build().unique();
							if (team.getUuid().equals(item.getUuid())) {
								isGuest = false;
								break;
							}
						}
						Intent intent = null;
						if (isGuest) {// 来宾
							intent = new Intent(mContext,
									TeamInfoForGuestActivity.class);
							intent.putExtra(
									TeamInfoForGuestActivity.INTENT_TEAM_INFO_BEAN,
									item);
							intent.putExtra(
									TeamInfoForGuestActivity.INTENT_NEET_TO_RETURN_BEAN,
									true);
							intent.putExtra(
									TeamInfoForGuestActivity.INTENT_NEED_TO_GET_CYBER_NEW,
									true);
							startActivity(intent);
							// startActivityForResult(intent,REQUEST_CODE_RECEIPT_TEAM_INFO);
						} else {// 成员
							intent = new Intent(mContext,
									TeamInfoActivity.class);
							intent.putExtra(
									TeamInfoActivity.INTENT_TEAM_INFO_BEAN,
									item);
							intent.putExtra(
									TeamInfoActivity.INTENT_NEET_TO_RETURN_BEAN,
									true);
							intent.putExtra(
									TeamInfoForGuestActivity.INTENT_NEED_TO_GET_CYBER_NEW,
									true);
							startActivity(intent);
							// startActivityForResult(intent,REQUEST_CODE_RECEIPT_TEAM_INFO);
						}
					}
				});
				horview.addView(rel);
			}
		} else { // 目前没有球队
			horview.setVisibility(View.GONE);
			no_anyteam.setVisibility(View.VISIBLE);
			if (intentPlayerId.equals(UserManager.getInstance(mContext)
					.getCurrentUser().getPlayer().getUuid())) {
				no_anyteam.setText("您未有任何球队");
			}
		}
	}

	private void setBasicForViewPager() {
		lineChartLayoutIDs = new int[] { R.id.llt_radarchart,
				R.id.five_result_data_one, R.id.five_result_data_two };
		titles = new String[lineChartLayoutIDs.length];
		titles[0] = "";
		titles[1] = "球技互评、球品口碑";
		titles[2] = "综合数据";

		lineChartLinearLayouts = new ArrayList<LinearLayout>();
		// 初始化LinearLayout资源

		lineChartLinearLayouts.add(llt_radarchart);

		LinearLayout linear_02 = (LinearLayout) LinearLayout.inflate(mContext,
				R.layout.linechart_layout_two, null);
		lineChart_02 = (LineChart) linear_02.findViewById(R.id.line_chart_two);
		nodata_two = (TextView) linear_02.findViewById(R.id.nodata_two);
		lineChartLinearLayouts.add(linear_02);

		LinearLayout linear_01 = (LinearLayout) LinearLayout.inflate(mContext,
				R.layout.linechart_layout, null);
		lineChart_01 = (LineChart) linear_01.findViewById(R.id.line_chart_one);
		nodata_one = (TextView) linear_01.findViewById(R.id.nodata_one);
		lineChartLinearLayouts.add(linear_01);

		dots = new ArrayList<View>();
		dots.add(findViewById(R.id.v_dot0));
		dots.add(findViewById(R.id.v_dot1));
		dots.add(findViewById(R.id.v_dot2));
		all_datacharrt.setVisibility(View.INVISIBLE);// 为了不影响下面的小圆点的位置,不设为gone
		tv_title.setVisibility(View.INVISIBLE);

		vp.setAdapter(new MyLineChartViewAdapter(mContext, lineChartLayoutIDs,
				lineChartLinearLayouts));// 设置填充ViewPager页面的适配器
		// 设置一个监听器，当ViewPager中的页面改变时调用
		vp.setOnPageChangeListener(new MyPageChangeListener());
	}

	private void executePlayerFMSMData() {
		NetWorkManager.getInstance(mContext).getPlayerFMSMData(intentPlayerId,
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// System.out.println("球员近5场球赛数据-----" +
						// response.toString());
						// TODO Auto-generated method stub
						Gson gson = new Gson();
						FiveMatchStatisticsResponse fiveRe = gson.fromJson(
								response.toString(),
								new TypeToken<FiveMatchStatisticsResponse>() {
								}.getType());
						if (fiveRe != null) {

							/**
							 * ------------------------雷达图----------------------
							 * ---
							 **/
							FiveDifferentData fiveDiff = fiveRe
									.getLeagueTotal();
							FiveDifferentData fiveChange = fiveRe
									.getLeagueDelta();
							showRadarChartAllData(fiveDiff, fiveChange);
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
							// skill_value.setText("" + 100);

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
											.setBackgroundDrawable(getResources() // 红色箭头
													.getDrawable(
															R.drawable.radarchart_down3));
									synthesize_change
											.setTextColor(getResources()
													.getColor(
															R.color.red_down_text)); // 红色字体
								} else if (composite_detla > 0) {
									synthesize_change.setText(""
											+ composite_detla);
									change_img1
											.setBackgroundDrawable(getResources()
													.getDrawable(
															R.drawable.radarchart_up2)); // 绿色箭头
									synthesize_change
											.setTextColor(getResources()
													.getColor(
															R.color.green_up_text)); // 绿色字体
								} else {
									synthesize_change.setVisibility(View.GONE);
									change_img1
											.setBackgroundDrawable(getResources()
													.getDrawable(
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
											.setBackgroundDrawable(getResources()
													.getDrawable(
															R.drawable.radarchart_down3));
									morality_change.setTextColor(getResources()
											.getColor(R.color.red_down_text));
								} else if (morality_detla > 0) {
									morality_change
											.setText("" + morality_detla);
									change_img2
											.setBackgroundDrawable(getResources()
													.getDrawable(
															R.drawable.radarchart_up2));
									morality_change.setTextColor(getResources()
											.getColor(R.color.green_up_text));
								} else {
									change_img2
											.setBackgroundDrawable(getResources()
													.getDrawable(
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
											.setBackgroundDrawable(getResources()
													.getDrawable(
															R.drawable.radarchart_down3));
									skill_change.setTextColor(getResources()
											.getColor(R.color.red_down_text));
								} else if (skill_detla > 0) {
									skill_change.setText("" + skill_detla);
									change_img3
											.setBackgroundDrawable(getResources()
													.getDrawable(
															R.drawable.radarchart_up2));
									skill_change.setTextColor(getResources()
											.getColor(R.color.green_up_text));
								} else {
									change_img3
											.setBackgroundDrawable(getResources()
													.getDrawable(
															R.drawable.radarchart_eq5));
									skill_change.setVisibility(View.GONE);
								}
							}
							// 显示“综合数据”折线图中点坐标值(包括最近五场赛事的输赢)
							List<FiveDifferentData> fiveDiffList = fiveRe
									.getLeagues();
							List<Integer> staticDataList = new ArrayList<Integer>(); // 最近五场比赛的综合数据集合（用于显示第一个折线图）
							List<String> kickOffList = new ArrayList<String>();
							if (fiveDiffList != null
									&& fiveDiffList.size() != 0) {
								Collections.sort(fiveDiffList,
										new Comparator<FiveDifferentData>() {
											@Override
											public int compare(
													FiveDifferentData lhs,
													FiveDifferentData rhs) {
												// TODO Auto-generated method
												// stub
												int flag = lhs
														.getKickOff()
														.compareTo(
																rhs.getKickOff());
												return flag;
											}
										});

								for (FiveDifferentData item : fiveDiffList) {
									int staticData = AndroidChartUtil
											.roundNumber(item.getComposite()); // 每一场比赛的综合数据值
									staticDataList.add(staticData);
									kickOffList.add(item.getKickOff());
								}
							}
							AndroidChartUtil.SetAttributeForLineChartOne(
									mContext, lineChart_01, tf, staticDataList,
									nodata_one);

							// 显示“球技互评，球品口碑”折线图中坐标值
							List<SkillAndMormality> samList = fiveRe
									.getMatches();
							List<Integer> skills = new ArrayList<Integer>(); // 技术
							List<Integer> moralitys = new ArrayList<Integer>(); // 球品
							if (samList != null && samList.size() != 0) {
								Collections.sort(samList,
										new Comparator<SkillAndMormality>() {
											@Override
											public int compare(
													SkillAndMormality lhs,
													SkillAndMormality rhs) {
												// TODO Auto-generated method
												// stub
												int flag = lhs
														.getKickOff()
														.compareTo(
																rhs.getKickOff());
												return flag;
											}
										});
								for (SkillAndMormality item : samList) {
									skills.add(AndroidChartUtil
											.roundNumber(item.getSkill() * 20));
									moralitys.add(AndroidChartUtil
											.roundNumber(item.getMorality() * 20));
								}
							}
							AndroidChartUtil.SetAttributeForLineChartTwo(
									mContext, lineChart_02, tf, skills,
									moralitys, nodata_two);
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							// ShowToast("当前网络不可用");
							return;
						}
						if (error.networkResponse == null) {
							// ShowToast("服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else if (error.networkResponse.statusCode == 404) {
							// ShowToast("球员找不到,折线图数据未能加载");
						}
					}
				});
	}

	private void executeAllMatchsResult() {
		NetWorkManager.getInstance(mContext).playerAllMatchData(intentPlayerId,
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						// System.out.println("球员总比赛战绩（出场次数，胜平负等）" +
						// response.toString());
						Gson gson = new Gson();
						PlayerAllMatchsResultResponse allResult = null;
						allResult = gson.fromJson(response.toString(),
								new TypeToken<PlayerAllMatchsResultResponse>() {
								}.getType());
						if (allResult != null) {
							Record record = allResult.getRecord();

							present_count.setText("" + record.getPlayed());
							win_count.setText("" + record.getWon());
							equal_count.setText("" + record.getDrawn());
							failed_count.setText("" + record.getLost());

							// 判断显示“胜”，“平”，“负”img
							showKindOfImg(allResult.getResults());
						}

					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							// ShowToast("当前网络不可用");
							return;
						}
						if (error.networkResponse == null) {
							// ShowToast("服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else if (error.networkResponse.statusCode == 404) {
							// ShowToast("找不到该球员,总比赛战绩数据加载失败");
						}
					}
				});
	}

	private void executeFriendMatchsResult() {
		NetWorkManager.getInstance(mContext).playerFriendMatchData(
				intentPlayerId,
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						// System.out.println("球员友谊赛战绩（出场次数，胜平负等）" +
						// response.toString());
						Gson gson = new Gson();
						PlayerAllMatchsResultResponse allResult = null;
						allResult = gson.fromJson(response.toString(),
								new TypeToken<PlayerAllMatchsResultResponse>() {
								}.getType());
						if (allResult != null) {
							Record record = allResult.getRecord();

							friend_present_count.setText(""
									+ record.getPlayed());
							friendly_win_count.setText("" + record.getWon());
							friendly_equal_count.setText("" + record.getDrawn());
							friendly_failed_count.setText("" + record.getLost());

							// 判断显示“胜”，“平”，“负”img
							showKindOfFriendImg(allResult.getResults());
						}

					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							// ShowToast("当前网络不可用");
							return;
						}
						if (error.networkResponse == null) {
							// ShowToast("服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else if (error.networkResponse.statusCode == 404) {
							// ShowToast("找不到该球员,友谊赛战绩数据加载失败");
						}
					}
				});
	}

	private void showKindOfImg(String[] results) {
		if (results != null && results.length != 0) {
			all_none.setVisibility(View.GONE);
			for (int i = 0; i < results.length; i++) {
				switch (MatchResultEnum.getEnumFromString(results[i])) {
				case WIN:
					matchResult_Imgs[i].setImageResource(R.drawable.win);
					break;
				case DRAW:
					matchResult_Imgs[i].setImageResource(R.drawable.equal);
					break;
				case LOSS:
					matchResult_Imgs[i].setImageResource(R.drawable.falie);
					break;
				case NULL:
					matchResult_Imgs[i].setImageResource(0); // 显示空
					break;
				}
			}
		} else {
			all_none.setVisibility(View.VISIBLE);
		}
	}

	private void showKindOfFriendImg(String[] results) {
		if (results != null && results.length != 0) {
			friend_none.setVisibility(View.GONE);
			for (int i = 0; i < results.length; i++) {
				switch (MatchResultEnum.getEnumFromString(results[i])) {
				case WIN:
					friendResult_Imgs[i].setImageResource(R.drawable.win);
					break;
				case DRAW:
					friendResult_Imgs[i].setImageResource(R.drawable.equal);
					break;
				case LOSS:
					friendResult_Imgs[i].setImageResource(R.drawable.falie);
					break;
				case NULL:
					friendResult_Imgs[i].setImageResource(0); // 显示空
					break;
				}
			}
		} else {
			friend_none.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		public void onPageSelected(int position) {
			if (position == 2) { // 说明第三 个
				all_datacharrt.setVisibility(View.VISIBLE);
				tv_title.setVisibility(View.VISIBLE);

				morality_img.setVisibility(View.GONE);
				tv_title_mor.setVisibility(View.GONE);
				skill_img.setVisibility(View.GONE);
				tv_title_skill.setVisibility(View.GONE);

			}
			if (position == 1) {
				all_datacharrt.setVisibility(View.GONE);
				tv_title.setVisibility(View.GONE);

				morality_img.setVisibility(View.VISIBLE);
				tv_title_mor.setVisibility(View.VISIBLE);
				skill_img.setVisibility(View.VISIBLE);
				tv_title_skill.setVisibility(View.VISIBLE);
			}
			if (position == 0) {
				all_datacharrt.setVisibility(View.INVISIBLE);
				tv_title.setVisibility(View.INVISIBLE);

				morality_img.setVisibility(View.GONE);
				tv_title_mor.setVisibility(View.GONE);
				skill_img.setVisibility(View.GONE);
				tv_title_skill.setVisibility(View.GONE);
			}
			tv_title.setText(titles[position]);
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	private void startChat() {
		String playerUuid = UserManager.getInstance(mContext).getCurrentUser()
				.getPlayer().getUuid();

		if (thisPlayer == null) {
			ShowToast("该球员信息未完善，无法与其聊天");
			return;
		}
		// 首先，将其添加到“聊天”Fragment中
		ChatRecent chatRecent = new ChatRecent(thisPlayer.getAccountName(),
				thisPlayer.getUuid(), thisPlayer.getName(),
				PhotoUtil.getAllPhotoPath(PhotoUtil.getAllPhotoPath(thisPlayer
						.getPicture())), "你可以和他进行聊天",
				System.currentTimeMillis(), ChatConstants.MSG_TYPE_TEXT, 0);
		ChatDBManager.create(mContext, playerUuid + "@chat").saveRecent(
				chatRecent);

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

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void showRadarChartAllData(FiveDifferentData fiveDiff,
			FiveDifferentData fiveChange) {
		if (fiveDiff != null) {
			List<Integer> radarList = new ArrayList<Integer>(); // 雷达图坐标值List
			radarList.add(AndroidChartUtil.roundNumber(fiveDiff.getAttack())); // 取整数
			radarList.add(AndroidChartUtil.roundNumber(fiveDiff.getDefence()));
			radarList.add(AndroidChartUtil.roundNumber(fiveDiff.getStrength()));
			radarList.add(AndroidChartUtil.roundNumber(fiveDiff.getSkill()));
			radarList
					.add(AndroidChartUtil.roundNumber(fiveDiff.getAwareness()));

			nonet_nodata.setVisibility(View.GONE);
			radarChart.setVisibility(View.VISIBLE);
			lin_radarchart.setVisibility(View.VISIBLE);
			AndroidChartUtil.SetAttributeForRadarChart(mContext, radarChart,
					radarList, tf);

			attack.setText(""
					+ AndroidChartUtil.roundNumber(fiveDiff.getAttack())); // 当前总分数
			defence.setText(""
					+ AndroidChartUtil.roundNumber(fiveDiff.getDefence()));
			stren.setText(""
					+ AndroidChartUtil.roundNumber(fiveDiff.getStrength()));
			skill.setText(""
					+ AndroidChartUtil.roundNumber(fiveDiff.getSkill()));
			awareness.setText(""
					+ AndroidChartUtil.roundNumber(fiveDiff.getAwareness()));
		}

		if (fiveChange != null) {
			// 显示五项数据变化值
			int attack_delta = AndroidChartUtil.roundNumber(fiveChange
					.getAttack()); // 先取整
			if (attack_delta < 0) { // 进攻
				String attack = String.valueOf(attack_delta).substring(1,
						String.valueOf(attack_delta).length()); // 去掉减号
				attack_value2.setText("" + attack); // 变化值(取整)

				attack_updowneq.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.radarchart_down3));
				attack_value2.setTextColor(getResources().getColor(
						R.color.red_down_text));
			} else if (attack_delta > 0) {
				attack_value2.setText(attack_delta + "");
				attack_updowneq.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.radarchart_up2));
				attack_value2.setTextColor(getResources().getColor(
						R.color.green_up_text));
			} else {
				attack_updowneq.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.radarchart_eq5));
				attack_value2.setVisibility(View.GONE);
			}

			int defence_delta = AndroidChartUtil.roundNumber(fiveChange
					.getDefence());
			if (defence_delta < 0) { // 防守
				String defence = String.valueOf(defence_delta).substring(1,
						String.valueOf(defence_delta).length());
				defence_value2.setText("" + defence);

				defence_updowneq.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.radarchart_down3));
				defence_value2.setTextColor(getResources().getColor(
						R.color.red_down_text));
			} else if (defence_delta > 0) {
				defence_value2.setText(defence_delta + "");
				defence_updowneq.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.radarchart_up2));
				defence_value2.setTextColor(getResources().getColor(
						R.color.green_up_text));
			} else {
				defence_updowneq.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.radarchart_eq5));
				defence_value2.setVisibility(View.GONE);
			}

			int streng_delta = AndroidChartUtil.roundNumber(fiveChange
					.getStrength());
			if (streng_delta < 0) { // 体能
				String streng = String.valueOf(streng_delta).substring(1,
						String.valueOf(streng_delta).length());
				stren_value2.setText("" + streng);
				stren_updowneq.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.radarchart_down3));
				stren_value2.setTextColor(getResources().getColor(
						R.color.red_down_text));
			} else if (streng_delta > 0) {
				stren_value2.setText(streng_delta + "");
				stren_updowneq.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.radarchart_up2));
				stren_value2.setTextColor(getResources().getColor(
						R.color.green_up_text));
			} else {
				stren_updowneq.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.radarchart_eq5));
				stren_value2.setVisibility(View.GONE);
			}

			int skill_delta = AndroidChartUtil.roundNumber(fiveChange
					.getSkill());
			if (skill_delta < 0) { // 技术
				String skill = String.valueOf(skill_delta).substring(1,
						String.valueOf(skill_delta).length());
				skill_value2.setText("" + skill);
				skill_updowneq.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.radarchart_down3));
				skill_value2.setTextColor(getResources().getColor(
						R.color.red_down_text));
			} else if (skill_delta > 0) {
				skill_value2.setText(skill_delta + "");
				skill_updowneq.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.radarchart_up2));
				skill_value2.setTextColor(getResources().getColor(
						R.color.green_up_text));
			} else {
				skill_updowneq.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.radarchart_eq5));
				skill_value2.setVisibility(View.GONE);
			}

			int awareness_delta = AndroidChartUtil.roundNumber(fiveChange
					.getAwareness());
			if (awareness_delta < 0) { // 侵略性
				String awareness = String.valueOf(awareness_delta).substring(1,
						String.valueOf(awareness_delta).length());
				awareness_value2.setText("" + awareness);
				awareness_updowneq.setBackgroundDrawable(mContext
						.getResources()
						.getDrawable(R.drawable.radarchart_down3));
				awareness_value2.setTextColor(getResources().getColor(
						R.color.red_down_text));
			} else if (awareness_delta > 0) {
				awareness_value2.setText(awareness_delta + "");
				awareness_updowneq.setBackgroundDrawable(mContext
						.getResources().getDrawable(R.drawable.radarchart_up2));
				awareness_value2.setTextColor(getResources().getColor(
						R.color.green_up_text));
			} else {
				awareness_updowneq.setBackgroundDrawable(mContext
						.getResources().getDrawable(R.drawable.radarchart_eq5));
				awareness_value2.setVisibility(View.GONE);
			}
		}
	}

}
