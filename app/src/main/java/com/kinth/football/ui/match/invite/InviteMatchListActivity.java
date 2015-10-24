package com.kinth.football.ui.match.invite;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.bean.MatchInfoResponse;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.config.JConstants;
import com.kinth.football.config.MatchStateEnum;
import com.kinth.football.dao.CityDao;
import com.kinth.football.dao.ProvinceDao;
import com.kinth.football.dao.RegionDao;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.ui.match.CreateFriendMatchActivity;
import com.kinth.football.ui.match.MatchDetailOnInvitationStateActivity;
import com.kinth.football.ui.team.PickTeamActivity;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.view.xlist.XListView;
import com.kinth.football.view.xlist.XListView.IXListViewListener;

/**
 * 约赛列表
 * 
 * @author Administrator
 * 
 */
public class InviteMatchListActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener, IXListViewListener,
		InviteMatchConstants {

	private boolean firstLoad = true;
	
	private int provinceId;
	private int cityId;
	private int regionId;

	private Spinner spinProvince;// 省份下拉列表
	private Spinner spinCity;// 城市下拉列表
	private Spinner spinRegion;// 区县下拉列表
	private XListView mListView; // 下拉刷新列表视图，该XListView类继承了ListView并实现了滚动监听接口

	private ProvinceDao pdao;
	private CityDao cdao;
	private RegionDao rdao;

	private ArrayAdapter<String> aAdapterPr;
	private ArrayAdapter<String> aAdapterCity;
	private ArrayAdapter<String> aAdapterRg;
	private InviteMatchAdapter imAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_invite_match_list);
		initData();
		initView();
	}

	private void initData() {
		// TODO 自动生成的方法存根
		pdao = new ProvinceDao(this);
		cdao = new CityDao(this);
		rdao = new RegionDao(this);
	}

	private void initView() {
		initHeader();
		initXListView();
		initProvinceSpinner();
		setBackgroundWithSaveMemory();
	}

	private void initHeader() {
		// TODO 自动生成的方法存根
		ImageButton ibLeft = (ImageButton) findViewById(R.id.nav_left);
		TextView tvTitle = (TextView) findViewById(R.id.nav_title);
		ImageView ivRight = (ImageView) findViewById(R.id.nav_right_image);

		tvTitle.setText("友谊赛");
		ibLeft.setOnClickListener(this);
		ivRight.setOnClickListener(this);
	}

	private void initProvinceSpinner() {
		// TODO 自动生成的方法存根
		spinProvince = (Spinner) findViewById(R.id.province_spinner);
		aAdapterPr = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, initProvinceData());
		aAdapterPr.setDropDownViewResource(R.layout.item_spinner2);
		spinProvince.setAdapter(aAdapterPr);
		spinProvince.setOnItemSelectedListener(new OnItemSelectedListener() {

			/**
			 * 选中省份之后初始化该省份的相应城市
			 */
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO 自动生成的方法存根
				if(position == -1)
					return;
				
				if (position != 0) {
					provinceId = pdao.getProvinceByName(aAdapterPr
							.getItem(position));
					initCitySpinner(position);
				} else {
					provinceId = 0;
					cityId = 0;
					regionId = 0;
					
					if(aAdapterCity!=null){
						aAdapterCity.clear();
						aAdapterCity.add("不限");
						aAdapterCity.notifyDataSetChanged();
					}
					
					if(aAdapterRg!=null){
						aAdapterRg.clear();
						aAdapterRg.add("不限");
						aAdapterRg.notifyDataSetChanged();
					}
					
					executeGetMatchList();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO 自动生成的方法存根

			}
		});

		// 将用户资料中的省份设置为默认省份
		String defaultPrName = UserManager.getInstance(this).getCurrentUser()
				.getPlayer().getProvince();
		if (defaultPrName != null)
			spinProvince.setSelection(aAdapterPr.getPosition(defaultPrName),
					true);
	}

	private List<String> initProvinceData() {
		List<String> listPr = new ArrayList<String>();
		listPr.add("不限");
		listPr.addAll(pdao.getProvinceList());
		return listPr;
	}

	private void initXListView() {
		mListView = (XListView) findViewById(R.id.list_invite_match);
		mListView.setOnItemClickListener(this);
		mListView.setPullLoadEnable(false); // 首先不允许加载更多
		mListView.setPullRefreshEnable(true); // 允许下拉
		mListView.setXListViewListener(this); // 设置监听器
		mListView.setDividerHeight(5);// 列表项间隔高度
		mListView.pullRefreshing(); // 下拉刷新
		imAdapter = new InviteMatchAdapter(this);
		mListView.setAdapter(imAdapter);
		mListView.setOnItemClickListener(this);
	}

	private void executeGetMatchList() {
		// TODO 自动生成的方法存根
		int areaTag = 0;
		int areaId = 0;

		if (regionId != 0) {
			areaTag = 3;
			areaId = regionId;
		} else if (cityId != 0) {
			areaTag = 2;
			areaId = cityId;
		} else if (provinceId != 0) {
			areaTag = 1;
			areaId = provinceId;
		}

		if(mListView==null)
			initXListView();
			
		mListView.pullRefreshing();
		// 查询创建中所有球队信息
		NetWorkManager.getInstance(mContext).getPublicStateMatchList(
				UserManager.getInstance(mContext).getCurrentUser().getPlayer()
						.getUuid(),
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				0, 100, MatchStateEnum.CALL_FOR.getValue(), areaTag, areaId,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						mListView.stopRefresh();
						Gson gson = new Gson();
						MatchInfoResponse matchInfoResponse = null;
						try {
							matchInfoResponse = gson.fromJson(
									response.toString(),
									new TypeToken<MatchInfoResponse>() {
									}.getType());
						} catch (JsonSyntaxException e) {
							matchInfoResponse = null;
							e.printStackTrace();
						}
						if (matchInfoResponse != null
								&& matchInfoResponse.getMatchs() != null) {
							imAdapter.updateListView(matchInfoResponse
									.getMatchs());
							Log.e("tag",""+matchInfoResponse.getMatchs().size());
							if(matchInfoResponse.getMatchs().size()==0){
								ShowToast("没有结果");
								return;
							}
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						mListView.stopRefresh();
						// ShowToast("获取球队列表失败");
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("没有可用的网络");
						} else if (error.networkResponse == null) {
							// ShowToast("MatchInvitation-executeMatchList-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						}
					}
				});
	}

	private void setBackgroundWithSaveMemory() {
		// TODO 自动生成的方法存根
		View entireLayout = findViewById(R.id.entire_layout);
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(entireLayout, new BitmapDrawable(
				getResources(), background));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_CODE_CREATE_MATCH) {
			executeGetMatchList();
		} else if (requestCode == REQUEST_CODE_NEED_TO_REFRESH) {
			executeGetMatchList();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		switch (v.getId()) {
		case R.id.nav_left:
			finish();
			break;
		case R.id.nav_right_image:
			createMatch();
			break;
		}
	}

	private void createMatch() {
		// TODO 自动生成的方法存根
		Intent intent = new Intent(mContext, PickTeamActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(JConstants.INTENT_TARGET_CLASS_NAME,
				CreateFriendMatchActivity.class.getName());
		intent.putExtras(bundle);
		startActivityForResult(intent,
				InviteMatchConstants.REQUEST_CODE_CREATE_MATCH);
	}

	@Override
	public void onRefresh() {
		// TODO 自动生成的方法存根
		executeGetMatchList();
	}

	@Override
	public void onLoadMore() {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO 自动生成的方法存根
		MatchInfo matchInfo = (MatchInfo) imAdapter.getItem(position - 1);

		Intent intent = new Intent(mContext,
				MatchDetailOnInvitationStateActivity.class);
		intent.putExtra(
				MatchDetailOnInvitationStateActivity.INTENT_MATCH_DETAIL_BEAN,
				matchInfo);
		startActivityForResult(intent, REQUEST_CODE_NEED_TO_REFRESH);
	}

	private void initCitySpinner(int position) {
		// TODO 自动生成的方法存根
		spinCity = (Spinner) findViewById(R.id.city_spinner);

		aAdapterCity = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, initCityData(position));
		aAdapterCity.setDropDownViewResource(R.layout.item_spinner2);
		spinCity.setAdapter(aAdapterCity);
		spinCity.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO 自动生成的方法存根
				if (position != 0) {
					cityId = cdao.getCityByName(aAdapterCity.getItem(position));
					initRegionSpinner(position);
				} else {
					cityId = 0;
					regionId = 0;
					
					if(aAdapterRg!=null){
						aAdapterRg.clear();
						aAdapterRg.add("不限");
						aAdapterRg.notifyDataSetChanged();
					}
				}
				executeGetMatchList();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO 自动生成的方法存根

			}
		});

		String defaultCityName = UserManager.getInstance(this).getCurrentUser()
				.getPlayer().getCity();
		if (defaultCityName != null)
			spinCity.setSelection(aAdapterCity.getPosition(defaultCityName),
					true);
	}

	private List<String> initCityData(int position) {
		// TODO 自动生成的方法存根
		List<String> listCity = new ArrayList<String>();
		listCity.add("不限");
		int provinceId = pdao
				.getProvinceByName(aAdapterPr.getItem(position));
		if(provinceId > -1)
			listCity.addAll(cdao.getCityListByProvinceId(String.valueOf(provinceId)));
		return listCity;
	}

	private void initRegionSpinner(int position) {
		// TODO 自动生成的方法存根
		spinRegion = (Spinner) findViewById(R.id.county_spinner);
		aAdapterRg = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, initRegionData(position));
		aAdapterRg.setDropDownViewResource(R.layout.item_spinner2);
		spinRegion.setAdapter(aAdapterRg);
		spinRegion.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position != 0) {
					regionId = rdao.getRegionIdByName(aAdapterRg
							.getItem(position));
				} else {
					regionId = 0;
				}
//				executeGetMatchList();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO 自动生成的方法存根

			}
		});
	}

	private List<String> initRegionData(int position) {
		List<String> listRegion = new ArrayList<String>();
		listRegion.add("不限");
		
		listRegion.addAll(rdao.getRegionNameListByCityId(cdao.getCityByName(aAdapterCity.getItem(position))));
		return listRegion;
	}

	@Override
	protected void onDestroy() {
		// TODO 自动生成的方法存根
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// TODO 自动生成的方法存根
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		// TODO 自动生成的方法存根
		super.onResume();
		if(!firstLoad){
			executeGetMatchList();
		}
		firstLoad = false;
	}
}
