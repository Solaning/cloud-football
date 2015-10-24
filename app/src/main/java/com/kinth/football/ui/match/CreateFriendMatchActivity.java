package com.kinth.football.ui.match;

import java.text.ParseException;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.bean.AddressBean;
import com.kinth.football.bean.Province;
import com.kinth.football.bean.RegionBean;
import com.kinth.football.bean.match.FriendMatch;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.dao.CityDao;
import com.kinth.football.dao.Player;
import com.kinth.football.dao.ProvinceDao;
import com.kinth.football.dao.Team;
import com.kinth.football.eventbus.bean.CreateFriendlyMatchEvent;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.ui.team.CreateTeamActivity;
import com.kinth.football.ui.team.RegionListActivity;
import com.kinth.football.ui.team.SelectAddressActivity;
import com.kinth.football.ui.team.TeamInfoActivity;
import com.kinth.football.util.DateUtil;
import com.kinth.football.util.DialogUtil;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.sleepbot.datetimepicker.time.TimePickerDialog.OnTimeSetListener;

import de.greenrobot.event.EventBus;

/**
 * 创建友谊赛
 * 
 * @author Sola
 */

@ContentView(R.layout.activity_create_friend_match)
public class CreateFriendMatchActivity extends BaseActivity implements
		CreateMatchInterface {
	public static final int REQUEST_CODE_SELECT_MATCH_RIVING = 9390;// 选择对手请求码
	public static final int REQUEST_CODE_SELECT_REFEREE = 9391;// 选择裁判
	public static final int REQUEST_CODE_CITY = 9003;// 城市请求码//TODO
	public static final int REQUEST_CODE_REGION = 9004;// 区县请求码//TODO
	public static final String RESULT_SELECT_CITY = "RESULT_SELECT_CITY";// 选择城市的结果//TODO

	public static final String DATEPICKER_TAG = "DATE_PICKER";// 选择日期
	public static final String TIMEPICKER_TAG = "TIME_PICKER";// 选择时间
	public static final String REGION_SELECT = "REGION_SELECT";// TODO

	private String memberNumberStr;
	private int cityId;// 城市id//TODO
	private int regionId;// 地区id//TODO
	private FriendMatch friendMatch;
	private Team team;// 主队信息

	private ProgressDialog dialog;

	private String dateStart;
	private String referee_uuid;// 裁判的uuid
	private String awayTeamUuid;// 对手球队的uuid

	@ViewInject(R.id.entire_layout)
	private View entireLayout;

	@ViewInject(R.id.nav_left)
	private ImageButton back;

	@ViewInject(R.id.nav_title)
	private TextView title;

	@ViewInject(R.id.nav_right_btn)
	private Button right;

	@ViewInject(R.id.et_input_friend_match_date)
	private EditText matchDate;// 日期

	@ViewInject(R.id.et_input_friend_match_yard)
	private EditText matchYard;// 场地

	@ViewInject(R.id.spinner_friend_match_member_number)
	private Spinner matchMemberNumber;// 人数

	@ViewInject(R.id.et_input_friend_match_name)
	private EditText matchName;// 描述

	@ViewInject(R.id.et_input_friend_match_fee)
	private EditText matchFee;// 费用

	@ViewInject(R.id.btn_input_city)
	private RelativeLayout city;// 所在城市

	@ViewInject(R.id.city_name)
	private TextView city_name;

	@ViewInject(R.id.select_region)
	private RelativeLayout region; // 区县

	@ViewInject(R.id.region_name)
	private TextView region_name;

	@ViewInject(R.id.txt_opponent)
	private TextView txt_opponent;// 对手名字

	@ViewInject(R.id.img_opponent)
	private ImageView img_opponent;// 对手的队徽

	@ViewInject(R.id.txt_referee)
	private TextView txt_referee;// 裁判名字

	@ViewInject(R.id.img_referee)
	private ImageView img_referee;// 裁判头像

	@ViewInject(R.id.layout_referee)
	private LinearLayout layout_referee;//

	@ViewInject(R.id.layout_opponent)
	private LinearLayout layout_opponent;//

	@OnClick(R.id.layout_opponent)
	public void fun_4(View v) { // 选择对手
		Intent intent = new Intent(this, SelectMatchRivingActivity.class);
		startActivityForResult(intent, REQUEST_CODE_SELECT_MATCH_RIVING);
	}

	@OnClick(R.id.layout_referee)
	public void fun_3(View v) { // 选择裁判
		Intent intent = new Intent(this, Select_Referee_Activity.class);
		startActivityForResult(intent, REQUEST_CODE_SELECT_REFEREE);
	}

	@OnClick(R.id.nav_left)
	public void fun_1(View v) {
		finish();
	}

	@OnClick(R.id.nav_right_btn)
	public void fun_2(View v) {// 创建比赛
		String dateStr = matchDate.getText().toString();
		if (TextUtils.isEmpty(dateStr)) {
			ShowToast("选择比赛时间");
			return;
		}
		String fieldStr = matchYard.getText().toString();
		if (TextUtils.isEmpty(fieldStr)) {
			ShowToast("输入比赛场地");
			return;
		}
		String nameStr = matchName.getText().toString();
		if (TextUtils.isEmpty(nameStr)) {
			ShowToast("输入比赛描述");
			return;
		}
		String feeStr = matchFee.getText().toString();
		if (TextUtils.isEmpty(feeStr)) {
			ShowToast("输入比赛费用");
			return;
		}

		if (TextUtils.isEmpty(awayTeamUuid)) { // 如果没有选择对手，则比赛信息一定要设置城市和区县）//
												// TODO
			if (TextUtils.isEmpty(region_name.getText())) {
				ShowToast("请选择区县");
				return;
			}
		}

		if (TextUtils.isEmpty(referee_uuid)) {
			ShowToast("请选择裁判");
			return;
		}
		friendMatch = new FriendMatch();
		friendMatch.setHomeTeamUuid(team.getUuid());
		friendMatch.setName(nameStr);
		friendMatch.setKickOff(DateUtil.parseTimeToMillis(dateStr));// 比赛时间
		friendMatch.setField(fieldStr);// 比赛场地
		friendMatch.setPlayerCount(Integer.valueOf(memberNumberStr));// 比赛人数
																		// 比赛测试1人
		friendMatch.setType("FRIENDLY_GAME");// 比赛类型--友谊赛
		if (!TextUtils.isEmpty(awayTeamUuid)) {
			friendMatch.setAwayTeamUuid(awayTeamUuid);// 对手
		}
		friendMatch.setRefereeUuid(referee_uuid);// 裁判
		friendMatch.setCost(Float.valueOf(feeStr));
		friendMatch.setRegionId(regionId);// TODO
		 // Intent intent = new Intent(mContext,
			// SelectMatchRivingActivity.class);//只是获取一个球队的id
			// startActivityForResult(intent, REQUEST_CODE_SELECT_MATCH_RIVING);
		createMatch();

		// 按钮没加响应，会让用户以为没按到多按几下，而造成重复消息，所以按下一次后要设为不可用
		right.setClickable(false);
		right.setEnabled(false);
		right.setFocusable(false);
	}

	@OnClick(R.id.btn_input_city)
	public void fun_5(View v) {// 选择城市
		Intent intent = new Intent(CreateFriendMatchActivity.this,
				SelectAddressActivity.class);
		startActivityForResult(intent, REQUEST_CODE_CITY);
	}

	@OnClick(R.id.select_region)
	public void fun_6(View v) {
		if (!TextUtils.isEmpty(city_name.getText().toString())) { // 说明已经选择了城市
			Intent intent = new Intent(mContext, RegionListActivity.class);
			intent.putExtra(REGION_SELECT, cityId);
			startActivityForResult(intent, REQUEST_CODE_REGION);
		} else {
			ShowToast("请先选择城市");
			return;
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

		team = getIntent().getParcelableExtra(
				TeamInfoActivity.INTENT_TEAM_INFO_BEAN);
		initView();
		matchDate.setOnTouchListener(touchListener);// 比赛时间的监听
	}

	private void initView() {
		title.setText("创建友谊赛");
		right.setText("完成");

		// 构建“人数”的下拉框
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
		// android.R.layout.simple_spinner_item, playCount);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
				R.layout.matchmembernumber_adapter, R.id.tv_matchmember_number,
				getResources().getStringArray(R.array.CompetitionSystem));

		// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		matchMemberNumber.setAdapter(adapter);

		// 添加对人数Spinner的监听
		matchMemberNumber
				.setOnItemSelectedListener(new SpinnerSelectedListener());

		String defaultProvince =  UserManager.getInstance(this).getCurrentUser()
				.getPlayer().getProvince();
		String defaultCity = UserManager.getInstance(this).getCurrentUser().getPlayer()
				.getCity(); 
		if (defaultProvince!=null && defaultCity != null)
			city_name.setText(defaultProvince+ "    "
					+ defaultCity);
		if(UserManager.getInstance(this).getCurrentUser().getPlayer()
				.getCityId()!=null)
			cityId = UserManager.getInstance(this).getCurrentUser().getPlayer()
				.getCityId();
	}

	private OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				switch (v.getId()) {
				case R.id.et_input_friend_match_date:// 选比赛时间
					final Calendar calendar = Calendar.getInstance();
					final DatePickerDialog datePickerDialog = DatePickerDialog
							.newInstance(new OnDateSetListener() {

								@Override
								public void onDateSet(
										DatePickerDialog datePickerDialog,
										int year, int month, int day) {
									// Calendar月份是从0开始，所以month要加1
									dateStart = year + "-" + (month + 1) + "-"
											+ day;
									int i = 0;
									try {
										i = DateUtil
												.compareDateWithToday(dateStart);
									} catch (ParseException e1) {
										e1.printStackTrace();
										// Toast.makeText(mContext, "日期解析出错",
										// Toast.LENGTH_LONG).show();
										return;
									}
									if (i < 0) {// 所选日期在今天之前
										Toast.makeText(mContext, "生效日不能比当前日期小",
												Toast.LENGTH_LONG).show();
										return;
									} else {// 所选日期在今天或之后
										matchDate.setText(dateStart);
										// 选择完日期后弹出选择时间
										TimePickerDialog timePickerDialog = TimePickerDialog
												.newInstance(
														new OnTimeSetListener() {
															boolean isPick = false;

															@Override
															public void onTimeSet(
																	RadialPickerLayout view,
																	int hourOfDay,
																	int minute) {
																if (isPick) {
																	return;
																} else {
																	isPick = true;
																	dateStart = dateStart
																			+ " "
																			+ hourOfDay
																			+ ":"
																			+ minute;
																	matchDate
																			.setText(dateStart);
																}

															}
														},
														calendar.get(Calendar.HOUR_OF_DAY),
														calendar.get(Calendar.MINUTE),
														false, false);
										timePickerDialog
												.setCloseOnSingleTapMinute(false);
										timePickerDialog.setCancelable(false);
										timePickerDialog.show(
												getSupportFragmentManager(),
												TIMEPICKER_TAG);
									}
								}
							}, calendar.get(Calendar.YEAR), calendar
									.get(Calendar.MONTH), calendar
									.get(Calendar.DAY_OF_MONTH), true);
					datePickerDialog.setYearRange(2014, 2037);
					datePickerDialog.setCloseOnSingleTapDay(true);
					datePickerDialog.show(getSupportFragmentManager(),
							DATEPICKER_TAG);
					break;
				}
			}
			return true;
		}
	};

	class SpinnerSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			memberNumberStr = getResources().getStringArray(
					R.array.CompetitionSystem)[arg2];
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_CODE_SELECT_MATCH_RIVING) {// 选择对手成功
			Team opponentTeam = intent
					.getParcelableExtra(SelectMatchRivingActivity.RESULT_OF_MATCH_SELECT_RIVING);
			if (team.getUuid().equals(opponentTeam.getUuid())) {
				ShowToast("不能选本球队作为对手");
				return;
			}
			awayTeamUuid = opponentTeam.getUuid();
			txt_opponent.setText(StringUtils.defaultIfEmpty(
					opponentTeam.getName(), ""));

			PictureUtil.getMd5PathByUrl(
					PhotoUtil.getThumbnailPath(opponentTeam.getBadge()),
					img_opponent, new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.team_bage_default)
							.showImageForEmptyUri(R.drawable.team_bage_default)
							.showImageOnFail(R.drawable.team_bage_default)
							.build());
			return;
		}
		if (REQUEST_CODE_SELECT_REFEREE == requestCode) { // 选择裁判成功
			Player player = intent.getParcelableExtra("player");
			referee_uuid = player.getUuid();
			txt_referee
					.setText(StringUtils.defaultIfEmpty(player.getName(), ""));
			PictureUtil.getMd5PathByUrl(
					PhotoUtil.getThumbnailPath(player.getPicture()),
					img_referee, new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.icon_head)
							.showImageForEmptyUri(R.drawable.icon_head)
							.showImageOnFail(R.drawable.icon_head) // 默认头像
							.cacheInMemory(true).cacheOnDisk(true).build());
			return;
		}
		if (requestCode == REQUEST_CODE_CITY) { // TODO 选择城市成功
			AddressBean addressBean = intent
					.getParcelableExtra(RESULT_SELECT_CITY);
			if (addressBean == null) {
				return;
			}
			// 根据传递过来的城市ID获取得到其省份
			int cityProId = new CityDao(mContext).getCityByCityId(
					addressBean.getCityId()).getProvince_id();
			Province province = new ProvinceDao(mContext).getProvinceByCityProId(
					cityProId);
			String proName = province == null ? "" : province.getName();
			city_name.setText(proName + " " + addressBean.getCityName());

			cityId = addressBean.getCityId();

			region_name.setText("");
			regionId = 0;
			return;
		}
		if (requestCode == REQUEST_CODE_REGION) { // TODO 选择区县成功
			RegionBean regionBean = intent
					.getParcelableExtra(RegionListActivity.REGION_BEAN);

			if (regionBean == null) {
				return;
			}
			region_name.setText(regionBean.getName());
			regionId = regionBean.getId();
			return;
		}
	}

	/**
	 * 创建比赛
	 */
	private void createMatch() {
		dialog = ProgressDialog.show(mContext, "提示", "请稍后", false, false);
		if (TextUtils.isEmpty(awayTeamUuid)) {	//如果没有选择对手则比赛会进入约赛大厅列表
			NetWorkManager.getInstance(mContext).createInviteMatch(
					UserManager.getInstance(mContext).getCurrentUser()
							.getToken(), friendMatch,
					new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							DialogUtil.dialogDismiss(dialog);
							Gson gson = new Gson();
							MatchInfo matchInfo = null;
							try {
								matchInfo = gson.fromJson(response.toString(),
										new TypeToken<MatchInfo>() {
										}.getType());
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
							// 保存数据和更新日程
							ShowToast("创建比赛成功");
							EventBus.getDefault().post(new CreateFriendlyMatchEvent());
							finish();
						}
					}, new ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							DialogUtil.dialogDismiss(dialog);
							error.printStackTrace();
							ShowToast("创建比赛失败");
						}
					});
		} else {
			NetWorkManager.getInstance(mContext).createMatch(
					UserManager.getInstance(mContext).getCurrentUser()
							.getToken(), friendMatch,
					new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							DialogUtil.dialogDismiss(dialog);
							Gson gson = new Gson();
							MatchInfo matchInfo = null;
							try {
								matchInfo = gson.fromJson(response.toString(),
										new TypeToken<MatchInfo>() {
										}.getType());
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
							}
							// 保存数据和更新日程
							ShowToast("创建比赛成功");
							EventBus.getDefault().post(new CreateFriendlyMatchEvent());
							finish();
						}
					}, new ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							DialogUtil.dialogDismiss(dialog);
							ShowToast("创建比赛失败");
						}
					});
		}
	}

}
