//package com.kinth.football.ui.team;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.android.volley.VolleyError;
//import com.android.volley.Response.ErrorListener;
//import com.android.volley.Response.Listener;
//import com.google.gson.Gson;
//import com.google.gson.JsonSyntaxException;
//import com.google.gson.reflect.TypeToken;
//import com.kinth.football.R;
//import com.kinth.football.bean.TeamMemberResponse;
//import com.kinth.football.dao.CityDao;
//import com.kinth.football.dao.Player;
//import com.kinth.football.dao.ProvinceDao;
//import com.kinth.football.dao.RegionDao;
//import com.kinth.football.dao.Team;
//import com.kinth.football.manager.UserManager;
//import com.kinth.football.network.NetWorkManager;
//import com.kinth.football.ui.BaseActivity;
//import com.kinth.football.util.ErrorCodeUtil;
//import com.kinth.football.util.PhotoUtil;
//import com.lidroid.xutils.ViewUtils;
//import com.lidroid.xutils.view.annotation.ContentView;
//import com.lidroid.xutils.view.annotation.ViewInject;
//import com.lidroid.xutils.view.annotation.event.OnClick;
//import com.nostra13.universalimageloader.core.ImageLoader;
//
///**
// * 搜索得到的球队信息——Activity
// * @author Botision.Huang
// *
// */
//@ContentView(R.layout.activity_search_team_info)
//public class SearchTeamInfoActivity extends BaseActivity{
//
//	private Team team;
//	
//	private List<TeamMemberResponse> myTeamList = new ArrayList<TeamMemberResponse>();
//	
//	@ViewInject(R.id.nav_title)
//	private TextView title;      //标题
//	
//	@ViewInject(R.id.nav_left)
//	private ImageButton back;    //返回按钮
//	
//	@ViewInject(R.id.iv_family_photo)  //全家福
//	private ImageView family_photo;
//	
//	@ViewInject(R.id.iv_badge)      //队徽
//	private ImageView badge;
//	
//	@ViewInject(R.id.tv_team_name)
//	private TextView team_name;  //球队名称
//	
//	@ViewInject(R.id.iv_first_captain)
//	private ImageView first_captain;  //队长
//	
//	@ViewInject(R.id.text_first_caption_name)
//	private TextView text_first_caption_name;
//	
//	@ViewInject(R.id.iv_second_captain)
//	private ImageView second_captain;  //队副
//	
//	@ViewInject(R.id.text_second_caption_name)
//	private TextView text_second_caption_name;
//	
//	@ViewInject(R.id.tv_member_number)
//	private TextView member_number;  //球队人数
//	
//	@ViewInject(R.id.tv_team_region)
//	private TextView team_region;  //地区
//	
//	@ViewInject(R.id.tv_team_founded_time)
//	private TextView team_founded_time;  //成立时间
//	
//	@ViewInject(R.id.tv_team_description)
//	private TextView team_description;  //介绍
//	
//	@ViewInject(R.id.tv_team_slogan)
//	private TextView team_slogan;  //口号
//	
//	
//	@OnClick(R.id.nav_left)
//	public void fun_1(View v){
//		finish();
//	}
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		ViewUtils.inject(this);
//		
//		getIntentData();
//		
//		showData();
//	}
//	
//	private void getIntentData(){
//		Intent intent = this.getIntent();
//		team = intent.getExtras().getParcelable("searchTeam");
//	}
//	
//	private void showData(){
//		title.setText("球队资料");
//		
//		team_name.setText(team.getName());
//		
//		ProvinceDao proDao = new ProvinceDao(mContext);
//		CityDao cityDao = new CityDao(mContext);
//		String proName = proDao.getProvinceByCityProId(cityDao.getCityByCityId(team.getCityId()).getProvince_id()).getName();
//		String cityName = cityDao.getCityByCityId(team.getCityId()).getName();
//		RegionDao regDao = new RegionDao(mContext);
//		String regionName = regDao.getRegionById(team.getRegionId()).getName();
//		team_region.setText(proName + "·" + cityName + "·" + regionName);
//		
//		team_founded_time.setText(team.getDate() + "");
//		team_description.setText(team.getDescription());
//		team_slogan.setText(team.getSlogan());
//		
//		if(team.getFamilyPhoto() == null || team.getFamilyPhoto().length() == 0){
//			family_photo.setImageResource(R.drawable.default_family_photo);
//		}else{
//			ImageLoader.getInstance().displayImage(PhotoUtil.getAllPhotoPath(team.getFamilyPhoto()), family_photo);
//		}
//		if(team.getBadge() == null || team.getBadge().length() == 0){
//			badge.setImageResource(R.drawable.default_head);
//		}else{
//			ImageLoader.getInstance().displayImage(PhotoUtil.getAllPhotoPath(team.getBadge()), badge);
//		}
//		
//		//点击队徽展示图片
//		badge.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(SearchTeamInfoActivity.this, ShowImageActivity.class);
//				Bundle bundle = new Bundle();
//				if(team.getBadge() == null || team.getBadge().length() == 0)
//					bundle.putString("img_URL", "null");
//				else 
//					bundle.putString("img_URL", team.getBadge());
//				bundle.putString("badgeOrFamily", "isBadge");
//				intent.putExtras(bundle);
//				mContext.startActivity(intent);
//			}
//		});
//		
//		//点击全家福展示图片
//		family_photo.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(SearchTeamInfoActivity.this, ShowImageActivity.class);
//				Bundle bundle = new Bundle();
//				if(team.getFamilyPhoto() == null || team.getFamilyPhoto().length() == 0)
//					bundle.putString("img_URL", "null");
//				else 
//					bundle.putString("img_URL", team.getFamilyPhoto());
//				bundle.putString("badgeOrFamily", "isFamily");
//				intent.putExtras(bundle);
//				mContext.startActivity(intent);
//			}
//		});
//		
//		//显示球队人数
//		executeGetTeamMemberCount();
//		
//		//显示队长，队副名称
//		if(team.getFirstCaptainUuid() != null){
//			getPersonByUUID(team.getFirstCaptainUuid(), "FirstCaptain");
//		}else if(team.getSecondCaptainUuid() != null){
//			getPersonByUUID(team.getSecondCaptainUuid(), "SecondCaptain");
//		}
//		
//	}
//	
//	//获取球队成员方法
//	private void executeGetTeamMemberCount(){
//		NetWorkManager.getInstance(mContext).getTeamMember(team.getUuid(), 
//				UserManager.getInstance(mContext).getCurrentUser().getToken(), 
//				new Listener<JSONArray>() {
//					@Override
//					public void onResponse(JSONArray response) {
//						Gson gson = new Gson();
//						try {
//							myTeamList = gson.fromJson(response.toString(),
//									new TypeToken<ArrayList<TeamMemberResponse>>() {
//									}.getType());
//							
//						} catch (JsonSyntaxException e) {
//							myTeamList = null;
//							e.printStackTrace();
//						}
//						if(myTeamList.size() != 0 ){
//							member_number.setText(myTeamList.size() + "人");
//						}
//					}
//				}, new ErrorListener() {
//					@Override
//					public void onErrorResponse(VolleyError error) {
//						ShowToast("获取人数失败");
//					}
//				});
//	}
//
//	Player player;
//	private void getPersonByUUID(String CaptionUuid, final String CaptionCategory){
//		NetWorkManager.getInstance(mContext).getPlayerInfoByUuid(CaptionUuid,
//				UserManager.getInstance(mContext).getCurrentUser().getToken(), 
//				new Listener<JSONObject>(){
//					@Override
//					public void onResponse(JSONObject response) {
//						// TODO Auto-generated method stub
//						Gson gson = new Gson();
//						try {
//							player = gson.fromJson(response.getString("player"), new TypeToken<Player>(){}.getType());
//						} catch (JSONException e) {
//							// TODO Auto-generated catch block
//							player = null;
//							e.printStackTrace();
//						}
//						if(CaptionCategory.equals("FirstCaptain")){
//							text_first_caption_name.setText(player.getName());
//							ImageLoader.getInstance().displayImage(PhotoUtil.getAllPhotoPath(player.getPicture()), first_captain);
//						}else if(CaptionCategory.equals("SecondCaptain")){
//							text_second_caption_name.setText(player.getName());
//							ImageLoader.getInstance().displayImage(PhotoUtil.getAllPhotoPath(player.getPicture()), second_captain);
//						}
//					}
//				},
//				new ErrorListener(){
//					@Override
//					public void onErrorResponse(VolleyError error) {
//						// TODO Auto-generated method stub
//						if(!NetWorkManager.getInstance(mContext).isNetConnected()){
//							ShowToast("当前网络不可用");
//						}else if(error.networkResponse == null){
//							ShowToast("服务器连接错误");
//						}else if(error.networkResponse.statusCode == 401){
//							ErrorCodeUtil.ErrorCode401(mContext);
//						}
//					}
//				}
//		);
//	}
//	
//}
