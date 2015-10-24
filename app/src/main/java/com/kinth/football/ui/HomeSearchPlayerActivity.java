package com.kinth.football.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.adapter.SearchPlayerInHomeAdapter;
import com.kinth.football.bean.Pageable;
import com.kinth.football.bean.SearchPersonResponse;
import com.kinth.football.config.PlayerPositionEnum;
import com.kinth.football.config.PlayerPositionEnum2;
import com.kinth.football.dao.CityDao;
import com.kinth.football.dao.Player;
import com.kinth.football.dao.ProvinceDao;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.ui.team.TeamPlayerInfo;
import com.kinth.football.util.DialogUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.view.ClearEditText;
import com.kinth.football.view.xlist.XListView;
import com.kinth.football.view.xlist.XListView.IXListViewListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 搜索球员
 * 
 * @author zyq
 * 
 */
@ContentView(R.layout.activity_home_search_player)
public class HomeSearchPlayerActivity extends BaseActivity {
	public static final String RESULT_OF_MATCH_SELECT_RIVING = "RESULT_OF_MATCH_SELECT_RIVING";// 选择对手的返回结果
	private ProgressDialog dialog;
	private SearchPlayerInHomeAdapter adapter;
	private static final int PAGE_SIZE = 20;
	private int page = 0; // 第一页
	private int totalPages; // 总页数
	private int totalElements; // 总条数
	private List<Player> searchMemberList = new ArrayList<Player>();

	private int provinceId;// 省ID
	private int cityId; // 城市ID
	private boolean isFirst = true;
	private String contentStr = null;// 搜索框的输入内容
	@ViewInject(R.id.entire_layout)
	private View entireLayout;

	@ViewInject(R.id.nav_left)
	private ImageButton back;

	@ViewInject(R.id.nav_right_image)
	private ImageView right;

	@ViewInject(R.id.tv_position)
	private TextView tv_position;

	@ViewInject(R.id.et_msg_search)
	private ClearEditText search;// 输入搜索框

	@ViewInject(R.id.listview_search_player)
	private XListView playerList;// 球员列表

	@OnClick(R.id.nav_left)
	public void fun_1(View v) {
		finish();
	}

	@OnClick(R.id.nav_right_image)
	public void fun_2(View v) {// 搜索内容
		excuteSerachPlayer();
	}

	private String position = null;

	private ArrayAdapter<String> aAdapterPr;
	private ArrayAdapter<String> aAdapterCity;
	private ArrayAdapter<String> aAdapterPositon;

	private ProvinceDao pdao;
	private CityDao cdao;

//	private List<Cursor> listCursor = new ArrayList<Cursor>();// 管理Cursor的集合

	@ViewInject(R.id.province_spinner)
	private Spinner province_spinner;

	@ViewInject(R.id.city_spinner)
	private Spinner city_spinner;

	@ViewInject(R.id.position_spinner)
	private Spinner position_spinner;

	@ViewInject(R.id.tv_has_no_data_current) // 没有结果的提示文本
	private TextView noDataTips;

	private final String[] position_value = { PlayerPositionEnum2.NULL.getValue(), PlayerPositionEnum2.FW.getValue(),
			PlayerPositionEnum2.MF.getValue(), PlayerPositionEnum2.BF.getValue(), PlayerPositionEnum2.GK.getValue(), };

	private List<String> list_FW, list_MF, list_BF;

	private String fromPK = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		// 设置模式为不自动弹出对话框
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		fromPK = getIntent().getStringExtra(PKActivity.FROM_PK);

		// 初始 前场 中场 后场 的位置集合 用于判断当前用户属于哪种 位置大类
		init_list_FW();
		init_list_MF();
		init_list_BF();
		// 設置背景
		// Bitmap background = SingletonBitmapClass.getInstance()
		// .getBackgroundBitmap();
		// ViewCompat.setBackground(entireLayout, new
		// BitmapDrawable(getResources(),
		// background));
		right.setVisibility(View.VISIBLE);
		right.setImageResource(R.drawable.home_search);
		if (UserManager.getInstance(mContext).getCurrentUser().getPlayer().getCityId() != null) {
			cityId = UserManager.getInstance(mContext).getCurrentUser().getPlayer().getCityId();
		} else {
			cityId = -1;
		}
		initProvinceSpinner();
		initPositionSpinner();

		noDataTips.setText("没有结果");

		playerList.setDivider(null);
		playerList.setDividerHeight(1);
		playerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				// String uid = adapter.getItem(arg2-1).getUuid(); //选中的用户ID
				// String ownerID =
				// UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid();
				// if(ownerID.equals(uid)){
				// Intent intent =new Intent(mContext,SetMyInfoActivity.class);
				// mContext.startActivity(intent);
				// }else{
				// Intent intent = new Intent(mContext,
				// AllUserInfoActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putParcelable("searchPerson",
				// searchMemberList.get(position));
				// intent.putExtras(bundle);
				// mContext.startActivity(intent);
				if (fromPK != null) {
					Intent intent = getIntent();
					intent.putExtra(PKActivity.SELECT_PLAYER, adapter.getItem(arg2 - 1));
					setResult(RESULT_OK, intent);
					finish();
				} else {
					if (adapter.getItem(arg2 - 1) == null) {
						return;
					}
					Intent intent = new Intent(mContext, TeamPlayerInfo.class);
					Bundle bundle = new Bundle();
					bundle.putString(TeamPlayerInfo.PLAYER_UUID, adapter.getItem(arg2 - 1).getUuid());
					intent.putExtras(bundle);
					mContext.startActivity(intent);
					// }
				}
			}
		});
		adapter = new SearchPlayerInHomeAdapter(mContext, null);
		playerList.setAdapter(adapter);
		playerList.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				// fun_2(null);
			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				if (page > totalPages) {
					ShowToast("已没有更多数据");
					playerList.stopLoadMore();
					playerList.setPullLoadEnable(false);
					return;
				} else {
					excuteSerachPlayerMore();
				}
			}
		});
		playerList.setPullLoadEnable(false);
		playerList.setPullRefreshEnable(false);
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// String content = s.toString();
				// if (TextUtils.isEmpty(content)) {// 没有其他输入，替换成原来的列表
				// searchLayout.setVisibility(View.GONE);
				// searchContent.setText("");
				// playerList.setVisibility(View.VISIBLE);
				// } else {// 有输入内容
				// searchLayout.setVisibility(View.VISIBLE);
				// searchContent.setText(content);
				// playerList.setVisibility(View.GONE);
				// }
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode != RESULT_OK) {
			return;
		}
	}

	private void init_list_FW() {
		list_FW = new ArrayList<String>();
		list_FW.add(PlayerPositionEnum.LF.getValue());
		list_FW.add(PlayerPositionEnum.RF.getValue());
		list_FW.add(PlayerPositionEnum.CF.getValue());
		list_FW.add(PlayerPositionEnum.ST.getValue());
		list_FW.add(PlayerPositionEnum.SS.getValue());
		list_FW.add(PlayerPositionEnum.LW.getValue());
		list_FW.add(PlayerPositionEnum.RW.getValue());
	}

	private void init_list_MF() {
		list_MF = new ArrayList<String>();
		list_MF.add(PlayerPositionEnum.CDM.getValue());
		list_MF.add(PlayerPositionEnum.LCM.getValue());
		list_MF.add(PlayerPositionEnum.RCM.getValue());
		list_MF.add(PlayerPositionEnum.CM.getValue());
		list_MF.add(PlayerPositionEnum.LWM.getValue());
		list_MF.add(PlayerPositionEnum.RWM.getValue());
		list_MF.add(PlayerPositionEnum.CAM.getValue());
	}

	private void init_list_BF() {
		list_BF = new ArrayList<String>();
		list_BF.add(PlayerPositionEnum.CB.getValue());
		list_BF.add(PlayerPositionEnum.LCB.getValue());
		list_BF.add(PlayerPositionEnum.RCB.getValue());
		list_BF.add(PlayerPositionEnum.LWB.getValue());
		list_BF.add(PlayerPositionEnum.RWB.getValue());
	}

	// 搜索球员
	private void excuteSerachPlayer() {
		contentStr = search.getText().toString();
		// 选择完后进行搜索
		// if (isFirst) {//第一次进来 搜索框没有内容 可以搜索
		// isFirst = false;
		page = 0;
		if(dialog == null)
			dialog = ProgressDialog.show(mContext, "提示", "请稍后", false, false);
		if(!dialog.isShowing()){
			dialog.show();
		}
		adapter.setSearchMemberList(null);
		NetWorkManager.getInstance(mContext).searchPlayer(page, PAGE_SIZE, provinceId, cityId, contentStr, position,
				UserManager.getInstance(mContext).getCurrentUser().getToken(), new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						DialogUtil.dialogDismiss(dialog);
						Gson gson = new Gson();
						SearchPersonResponse searchPersonResponse = null;
						try {
							searchPersonResponse = gson.fromJson(response.toString(),
									new TypeToken<SearchPersonResponse>() {
							}.getType());
						} catch (JsonSyntaxException e) {
							searchPersonResponse = null;
							e.printStackTrace();
						}
						if (searchPersonResponse == null || searchPersonResponse.getPlayers() == null
								|| searchPersonResponse.getPlayers().size() == 0) {
							ShowToast("没有找到");
							playerList.setVisibility(View.GONE);
							noDataTips.setVisibility(View.VISIBLE);
							return;
						} else {
							Pageable pageable = searchPersonResponse.getPageable();
							totalPages = pageable.getTotalPages(); // 总页数
							totalElements = pageable.getTotalElements(); // 总条数
							page++;
							if (page == totalPages) {
								playerList.setPullLoadEnable(false);
							} else {
								playerList.setPullLoadEnable(true);
							}
							playerList.setVisibility(View.VISIBLE);
							noDataTips.setVisibility(View.GONE);
							searchMemberList = searchPersonResponse.getPlayers();

							adapter.setSearchMemberList(searchMemberList);
							playerList.setSelection(0);
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						DialogUtil.dialogDismiss(dialog);
						playerList.setVisibility(View.GONE);
						noDataTips.setVisibility(View.VISIBLE);
						if (!NetWorkManager.getInstance(mContext).isNetConnected()) {
							ShowToast("当前网络不可用");
						} else if (error.networkResponse == null) {
							// ShowToast("InviteMemberActivity-fun_2-服务器连接错误");
							// ShowToast("没有找到");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						}
					}
				});
	}

	// 搜索球员
	private void excuteSerachPlayerMore() {
		contentStr = search.getText().toString();
		// 选择完后进行搜索
		// if (isFirst) {//第一次进来 搜索框没有内容 可以搜索
		// isFirst = false;
		NetWorkManager.getInstance(mContext).searchPlayer(page, PAGE_SIZE, provinceId, cityId, contentStr, position,
				UserManager.getInstance(mContext).getCurrentUser().getToken(), new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Gson gson = new Gson();
						SearchPersonResponse searchPersonResponse = null;
						try {
							searchPersonResponse = gson.fromJson(response.toString(),
									new TypeToken<SearchPersonResponse>() {
							}.getType());
						} catch (JsonSyntaxException e) {
							searchPersonResponse = null;
							e.printStackTrace();
						}
						if (searchPersonResponse != null && searchPersonResponse.getPlayers() != null
								&& searchPersonResponse.getPlayers().size() != 0) {
							playerList.stopLoadMore();
							searchMemberList.addAll(searchPersonResponse.getPlayers());
							// adapter.notifyDataSetChanged();
							adapter.setSearchMemberList(searchMemberList);
							Pageable pageable = searchPersonResponse.getPageable();
							totalPages = pageable.getTotalPages(); // 总页数
							totalElements = pageable.getTotalElements(); // 总条数
							page++;
							if (page == totalPages) {
								ShowToast("没有更多了");
								playerList.setPullLoadEnable(false);
							}
							playerList.setVisibility(View.VISIBLE);
							noDataTips.setVisibility(View.GONE);
						} else {
							playerList.setVisibility(View.GONE);
							noDataTips.setVisibility(View.VISIBLE);
							playerList.stopLoadMore();
							playerList.setPullLoadEnable(false);
							ShowToast("没有更多！");
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						playerList.stopLoadMore();
						playerList.setVisibility(View.GONE);
						noDataTips.setVisibility(View.VISIBLE);
						if (!NetWorkManager.getInstance(mContext).isNetConnected()) {
							ShowToast("当前网络不可用");
						} else if (error.networkResponse == null) {
							// ShowToast("InviteMemberActivity-fun_2-服务器连接错误");
							// ShowToast("没有找到");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						}
					}
				});
	}

	private void initProvinceSpinner() {
		aAdapterPr = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.text1, initProvinceData());
		// aAdapterPr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		province_spinner.setAdapter(aAdapterPr);

		province_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			/**
			 * 选中省份之后初始化该省份的相应城市
			 */
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// 获得provinceId 选择不限 provinceId = -1;
				provinceId = pdao.getProvinceByName(aAdapterPr.getItem(position));
				initCitySpinner(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		// 将用户资料中的省份设置为默认省份 --在OnItemSelectedListener之后
		String defaultPrName = UserManager.getInstance(this).getCurrentUser().getPlayer().getProvince();
		if (defaultPrName != null) {
			province_spinner.setSelection(aAdapterPr.getPosition(defaultPrName), false);
		}
	}

	private void initCitySpinner(int position) {
		aAdapterCity = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.text1, initCityData(position));
		city_spinner.setAdapter(aAdapterCity);
		// -- 默认选择城市放在OnItemSelectedListener之前 ,这样第一次不会实现点击事件的内容
		String defaultCityName = UserManager.getInstance(this).getCurrentUser().getPlayer().getCity();
		if (defaultCityName != null)
			city_spinner.setSelection(aAdapterCity.getPosition(defaultCityName), true);
		city_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// 获得 cityId ，选择不限 cityId = -1;
				cityId = cdao.getCityByName(aAdapterCity.getItem(position));
				playerList.setVisibility(View.GONE);
				excuteSerachPlayer();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO 自动生成的方法存根

			}
		});

	}

	private void initPositionSpinner() {
		// TODO 自动生成的方法存根
		aAdapterPositon = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.text1, initPositionData());
		// aAdapterPr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		position_spinner.setAdapter(aAdapterPositon);
		position_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO 自动生成的方法存根
				position = position_value[arg2];
				if (position.equals("NULL")) {
					position = null;
				}

				playerList.setVisibility(View.GONE);
				excuteSerachPlayer();
				Log.e("setOnItemSelectedListener", "setOnItemSelectedListener"+9999);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO 自动生成的方法存根

			}
		});
		// 默认选择与自己相同的位置
		String defaultPosition = null; // 默认位置
		String defaultPosition_catagory = null; // 默认位置的大类
		if (UserManager.getInstance(mContext).getCurrentUser().getPlayer().getPosition() != null) {
			defaultPosition = UserManager.getInstance(mContext).getCurrentUser().getPlayer().getPosition();
			// 判断 当前用户属于哪个 位置大类--
			if (list_FW.contains(defaultPosition)) {
				defaultPosition_catagory = "FW";
			} else if (list_MF.contains(defaultPosition)) {
				defaultPosition_catagory = "MF";
			} else if (list_BF.contains(defaultPosition)) {
				defaultPosition_catagory = "BF";
			} else if (defaultPosition.equals("GK")) {
				defaultPosition_catagory = "GK";
			} else {
				defaultPosition_catagory = "NULL";
			}
		} else {
			defaultPosition = "NULL";
			defaultPosition_catagory = "NULL";
		}
		// 设定默认位置大类
		position_spinner.setSelection(aAdapterPositon.getPosition(defaultPosition_catagory), false);
	}

	private List<String> initProvinceData() {
		List<String> listPr = new ArrayList<String>();
		listPr.add("不限");
		pdao = new ProvinceDao(this);
		listPr.addAll(pdao.getProvinceList());
		return listPr;
	}

	private List<String> initCityData(int position) {
		// TODO 自动生成的方法存根
		List<String> listCity = new ArrayList<String>();
		listCity.add("不限");
		cdao = new CityDao(this);
		int provinceId = pdao.getProvinceByName(aAdapterPr.getItem(position));
		if(provinceId > -1)
			listCity.addAll(cdao.getCityListByProvinceId(String.valueOf(provinceId)));
		return listCity;
	}

	private List<String> initPositionData() {
		List<String> listPosition = new ArrayList<String>();

		// String[] position =
		// {PlayerPositionEnum2.NULL.getName(),PlayerPositionEnum2.FW.getValue()+"
		// "+PlayerPositionEnum2.FW.getName(),
		// PlayerPositionEnum2.MF.getValue()+"
		// "+PlayerPositionEnum2.MF.getName(),
		// PlayerPositionEnum2.BF.getValue()+"
		// "+PlayerPositionEnum2.BF.getName(),
		// PlayerPositionEnum2.GK.getValue()+"
		// "+PlayerPositionEnum2.GK.getName()
		// };
		String[] position = { PlayerPositionEnum2.NULL.getName(), PlayerPositionEnum2.FW.getName(),
				PlayerPositionEnum2.MF.getName(), PlayerPositionEnum2.BF.getName(), PlayerPositionEnum2.GK.getName() };
		for (int i = 0; i < position.length; i++) {
			listPosition.add(position[i]);
		}
		return listPosition;

	}

}
