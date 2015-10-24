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
import de.greenrobot.dao.query.Query;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.adapter.SearchTeamInHomeAdapter;
import com.kinth.football.bean.Pageable;
import com.kinth.football.bean.SearchTeamResponse;
import com.kinth.football.dao.CityDao;
import com.kinth.football.dao.ProvinceDao;
import com.kinth.football.dao.Team;
import com.kinth.football.dao.TeamDao;
import com.kinth.football.dao.TeamPlayer;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.ui.team.TeamInfoActivity;
import com.kinth.football.ui.team.TeamInfoForGuestActivity;
import com.kinth.football.util.DialogUtil;
import com.kinth.football.view.ClearEditText;
import com.kinth.football.view.xlist.XListView;
import com.kinth.football.view.xlist.XListView.IXListViewListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
/**
 * 搜索球队
 * @author zyq
 *
 */
@ContentView(R.layout.activity_home_search_team)
public class HomeSearchTeamActivity extends BaseActivity {
	private static final int REQUEST_CODE_RECEIPT_TEAM_INFO = 9001;
	public static final String RESULT_OF_MATCH_SELECT_RIVING = "RESULT_OF_MATCH_SELECT_RIVING";//选择对手的返回结果
	private ProgressDialog dialog;
	private SearchTeamInHomeAdapter adapter;
	private static final int PAGE_SIZE = 20;
	private int page = 0;  //第一页
	private int totalPages;     //总页数
	private int totalElements;  //总条数
	private List<Team> searchTeamList = new ArrayList<Team>();
	
	private int provinceId;
	private int cityId;
	private boolean isFirst = true;
	private String contentStr =null;
	@ViewInject(R.id.entire_layout)
	private View entireLayout;
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;
	
	@ViewInject(R.id.nav_right_image)
	private ImageView right;
	
//	@ViewInject(R.id.tv_city_name)
//	private TextView tv_city_name;
	
	@ViewInject(R.id.province_spinner)
	private Spinner province_spinner;
	
	@ViewInject(R.id.city_spinner)
	private Spinner city_spinner;
	
	@ViewInject(R.id.et_msg_search)
	private ClearEditText search;//输入搜索框

	@ViewInject(R.id.listview_search_team)
	private XListView teamList;//球队列表
	
	@ViewInject(R.id.tv_has_no_data_current)//没有结果的提示文本
	private TextView noDataTips;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		finish();
	}

	@OnClick(R.id.nav_right_image)
	public void fun_3(View v){//右上角图标 --选择地区
		//TODO
		 excuteSearchTeam();
	}
	private SearchTeamResponse searchResponse;
	
	private ArrayAdapter<String> aAdapterPr;
	private ArrayAdapter<String> aAdapterCity;
	
	private ProvinceDao pdao;
	private CityDao cdao;
	
//	private List<Cursor> listCursor = new ArrayList<Cursor>();//管理Cursor的集合
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
	   //设置模式为不自动弹出对话框
		 getWindow().setSoftInputMode
		 ( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		// 設置背景
//		Bitmap background = SingletonBitmapClass.getInstance()
//				.getBackgroundBitmap();
//		ViewCompat.setBackground(entireLayout, new BitmapDrawable(getResources(),
//				background));
        right.setVisibility(View.VISIBLE);
        right.setImageResource(R.drawable.home_search);
        
        adapter = new SearchTeamInHomeAdapter(mContext, null);
        teamList.setAdapter(adapter);
        teamList.setXListViewListener(new IXListViewListener(){
			@Override
			public void onRefresh() {
//				fun_3(null);
			}
			
			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				if(page > totalPages){
					ShowToast("已没有更多数据");
					teamList.stopLoadMore();
					teamList.setPullLoadEnable(false);
					return;
				}else{
					excuteSearchTeamMore();
				}
			}
		});
        teamList.setPullLoadEnable(false);
        teamList.setPullRefreshEnable(false);
    	search.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
//				String content = s.toString();
//				if (TextUtils.isEmpty(content)) {// 没有其他输入，替换成原来的列表
//					searchLayout.setVisibility(View.GONE);
//					searchContent.setText("");
//					playerList.setVisibility(View.VISIBLE);
//				} else {// 有输入内容
//					searchLayout.setVisibility(View.VISIBLE);
//					searchContent.setText(content);
//					playerList.setVisibility(View.GONE);
//				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
        
		if (UserManager.getInstance(mContext).getCurrentUser().getPlayer().getCityId() != null) {
			cityId = UserManager.getInstance(mContext).getCurrentUser().getPlayer().getCityId();
		} else {
			cityId = -1;
		}
		initProvinceSpinner();
      
		noDataTips.setText("没有结果");
        teamList.setDivider(null);
        teamList.setDividerHeight(1);
        teamList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Team bean = (Team)teamList.getItemAtPosition(arg2);
				if (bean == null) {
					return;
				}
				boolean isGuest = true;//是否来宾权限
				List<TeamPlayer> teamPlayerList = CustomApplcation.getDaoSession(mContext).getTeamPlayerDao()._queryPlayer_TeamPlayerList(UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid());
				Query<Team> query = CustomApplcation.getDaoSession(mContext).getTeamDao().queryBuilder().where(TeamDao.Properties.Uuid.eq("")).build();
				for(TeamPlayer teamPlayer : teamPlayerList){
					query.setParameter(0, teamPlayer.getTeam_id());
					Team team = query.unique();
//					Team team = CustomApplcation.getDaoSession(mContext).getTeamDao().queryBuilder().where(TeamDao.Properties.Uuid.eq(teamPlayer.getTeam_id())).build().unique();
					if(team != null && team.getUuid().equals(bean.getUuid())){
						isGuest = false;
						break;
					}
				}
				Intent intent = null;
				if(isGuest){//来宾
					intent = new Intent(mContext, TeamInfoForGuestActivity.class);
					intent.putExtra(TeamInfoForGuestActivity.INTENT_TEAM_INFO_BEAN, bean);
					intent.putExtra(TeamInfoForGuestActivity.INTENT_NEET_TO_RETURN_BEAN, true);	
					intent.putExtra(TeamInfoForGuestActivity.INTENT_NEED_TO_GET_CYBER_NEW, true);
					startActivityForResult(intent,REQUEST_CODE_RECEIPT_TEAM_INFO);
				}else{//成员
					intent = new Intent(mContext, TeamInfoActivity.class);
					intent.putExtra(TeamInfoActivity.INTENT_TEAM_INFO_BEAN, bean);
					intent.putExtra(TeamInfoActivity.INTENT_NEET_TO_RETURN_BEAN, true);
					intent.putExtra(TeamInfoForGuestActivity.INTENT_NEED_TO_GET_CYBER_NEW, true);
					startActivityForResult(intent,REQUEST_CODE_RECEIPT_TEAM_INFO);
				}
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
		if(requestCode == REQUEST_CODE_RECEIPT_TEAM_INFO){//查看球队后更新球队
			Team receiptTeam = intent.getParcelableExtra(TeamInfoForGuestActivity.INTENT_TEAM_INFO_BEAN);
			if(receiptTeam == null || searchResponse == null || searchResponse.getTeams() == null){
				return;
			}
			for(int i = 0; i < searchResponse.getTeams().size(); i++){
				if(receiptTeam.getUuid().equals(searchResponse.getTeams().get(i).getUuid())){
					searchResponse.getTeams().remove(i);
					searchResponse.getTeams().add(i, receiptTeam);
					adapter.notifyDataSetChanged();
					return;
				}
			}
			return;
		}
	}
	//搜索球队
	private void excuteSearchTeam(){
		 contentStr = search.getText().toString();
		 //选择完后进行搜索
//		 if (isFirst) { //第一次进来  搜索框没有内容  可以搜索
//			 isFirst = false;
			    page=0;
				dialog = ProgressDialog.show(mContext, "提示", "请稍后", false, false);
				adapter.setSearchTeamList(null);
				NetWorkManager.getInstance(mContext).searchTeam(page, PAGE_SIZE, provinceId,cityId,
						contentStr,
						UserManager.getInstance(mContext).getCurrentUser().getToken(),
						new Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {
								DialogUtil.dialogDismiss(dialog);
								Gson gson = new Gson();
								try {
									searchResponse = gson.fromJson(response.toString(),
											new TypeToken<SearchTeamResponse>() {
											}.getType());
									searchTeamList = searchResponse.getTeams();
								} catch (JsonSyntaxException e) {
									searchTeamList = null;
									e.printStackTrace();
								}
								if (searchTeamList == null
										|| searchTeamList.size() == 0) {
										ShowToast("没有找到");			
										teamList.setVisibility(View.GONE);
										noDataTips.setVisibility(View.VISIBLE);
									Log.i("JSON", response.toString());
									return;
								}else {
								Pageable pageable = searchResponse.getPageable();
								totalPages = pageable.getTotalPages();    //总页数
								totalElements = pageable.getTotalElements();  //总条数
								page++;
								if (page ==totalPages ) {
									teamList.setPullLoadEnable(false);
								}else {
								teamList.setPullLoadEnable(true);}
								teamList.setVisibility(View.VISIBLE);
								noDataTips.setVisibility(View.GONE);
								adapter.setSearchTeamList(searchTeamList);
								teamList.setSelection(0);
								}
							}
						}, new ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								// TODO Auto-generated method stub
								teamList.stopLoadMore();
								DialogUtil.dialogDismiss(dialog);
								teamList.setVisibility(View.GONE);
								noDataTips.setVisibility(View.VISIBLE);
//								ShowToast("失败：" + error.getMessage());
								ShowToast("没有找到");
							}
						});
	}
	//搜索球队
	private void excuteSearchTeamMore(){
		 contentStr = search.getText().toString();
		 //选择完后进行搜索
//		 if (isFirst) { //第一次进来  搜索框没有内容  可以搜索
//			 isFirst = false;
//				dialog = ProgressDialog.show(mContext, "提示", "请稍后", false, false);
//				adapter.setSearchTeamList(null);
				NetWorkManager.getInstance(mContext).searchTeam(page, PAGE_SIZE, provinceId,cityId,
						contentStr,
						UserManager.getInstance(mContext).getCurrentUser().getToken(),
						new Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {
								// TODO Auto-generated method stub
							
								Gson gson = new Gson();
								try {
									searchResponse = gson.fromJson(response.toString(),
											new TypeToken<SearchTeamResponse>() {
											}.getType());					
								} catch (JsonSyntaxException e) {
									searchResponse = null;
									e.printStackTrace();
								}
								if (searchResponse != null&&searchResponse.getTeams()!=null
										&&searchResponse.getTeams().size() != 0) {	
										teamList.stopLoadMore();
										searchTeamList.addAll(searchResponse.getTeams());
										adapter.notifyDataSetChanged();
										Pageable pageable = searchResponse.getPageable();
										totalPages = pageable.getTotalPages();    //总页数
										totalElements = pageable.getTotalElements();  //总条数
										page++;
										if(page == totalPages){
											ShowToast("没有更多了");
											teamList.setPullLoadEnable(false);
										}
										teamList.setVisibility(View.VISIBLE);
										noDataTips.setVisibility(View.GONE);
								}else {
									teamList.stopLoadMore();
									teamList.setPullLoadEnable(false);
									teamList.setVisibility(View.GONE);
									noDataTips.setVisibility(View.VISIBLE);
									ShowToast("没有更多！");
								}
							}
						}, new ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								// TODO Auto-generated method stub
								teamList.stopLoadMore();
								teamList.setVisibility(View.GONE);
								noDataTips.setVisibility(View.VISIBLE);
//								DialogUtil.dialogDismiss(dialog);
//								ShowToast("失败：" + error.getMessage());
								ShowToast("没有找到");
							}
						});
	}
	private void initProvinceSpinner() {
		// TODO 自动生成的方法存根
		province_spinner = (Spinner) findViewById(R.id.province_spinner);
		aAdapterPr = new ArrayAdapter<String>(this, R.layout.item_spinner,R.id.text1, initProvinceData());
		province_spinner.setAdapter(aAdapterPr);
		
		province_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			/**
			 * 选中省份之后初始化该省份的相应城市
			 */
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				//获得provinceId 选择不限 provinceId = -1;
				provinceId = pdao.getProvinceByName(aAdapterPr.getItem(position));
				initCitySpinner(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO 自动生成的方法存根
				
			}
		});	
		//将用户资料中的省份设置为默认省份   --在OnItemSelectedListener之后
		String defaultPrName = UserManager.getInstance(this).getCurrentUser().getPlayer().getProvince();
		if(defaultPrName!=null){
			province_spinner.setSelection(aAdapterPr.getPosition(defaultPrName), false);
			provinceId = pdao.getProvinceByName(defaultPrName);
		}else {
			provinceId = -1;
		}
	}
	private void initCitySpinner(int position) {
		// TODO 自动生成的方法存根
		city_spinner = (Spinner) findViewById(R.id.city_spinner);
		aAdapterCity =  new ArrayAdapter<String>(this, R.layout.item_spinner,R.id.text1,initCityData(position));
		city_spinner.setAdapter(aAdapterCity);

		city_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				//获得 cityId  ，选择不限 cityId = -1;
				cityId = cdao.getCityByName(aAdapterCity.getItem(position));	
				teamList.setVisibility(View.GONE);
				excuteSearchTeam();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO 自动生成的方法存根
				
			}
		});
		if (isFirst) {
			// -- 默认选择城市放在OnItemSelectedListener之前
			String defaultCityName = UserManager.getInstance(this).getCurrentUser().getPlayer().getCity();
			if(defaultCityName!=null)
				city_spinner.setSelection(aAdapterCity.getPosition(defaultCityName), true);
			isFirst = false;
		}
		
	}
	
	private List<String> initProvinceData() {
		List<String> listPr = new ArrayList<String>();
		listPr.add("不限");
		pdao = new ProvinceDao(this);
		listPr.addAll(pdao.getProvinceList());
		return listPr;
	}
	
	private List<String> initCityData(int position) {
		List<String> listCity = new ArrayList<String>();
		listCity.add("不限");
		cdao = new CityDao(this);
		int provinceId = pdao.getProvinceByName(aAdapterPr.getItem(position));
		if(provinceId > -1)
			listCity.addAll(cdao.getCityListByProvinceId(String.valueOf(provinceId)));
		return listCity;
	}
}
