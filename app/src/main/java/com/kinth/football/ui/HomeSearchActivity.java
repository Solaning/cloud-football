package com.kinth.football.ui;

import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.adapter.HomeSearchPlayerAdapter;
import com.kinth.football.adapter.HomeSearchTeamAdapter;
import com.kinth.football.bean.HomeSearchResponse;
import com.kinth.football.dao.Player;
import com.kinth.football.dao.Team;
import com.kinth.football.dao.TeamDao;
import com.kinth.football.dao.TeamPlayer;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.ui.team.TeamInfoActivity;
import com.kinth.football.ui.team.TeamInfoForGuestActivity;
import com.kinth.football.ui.team.TeamPlayerInfo;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.view.ClearEditText;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 首页搜索界面
 * @author Botision.Huang
 * Date: 2015-4-6
 * Descp:
 */
@ContentView(R.layout.activity_home_search_layout)
public class HomeSearchActivity extends BaseActivity {
	private static final int REQUEST_CODE_RECEIPT_TEAM_INFO = 9001;
	
	private HomeSearchTeamAdapter teamAdapter;     //加载球队适配器
	private HomeSearchPlayerAdapter memberAdapter; //加载球员适配器
	
	private ProgressDialog dialog = null;
	private HomeSearchResponse searchReponse = null;
	
	@ViewInject(R.id.nav_left)
	private ImageButton nav_left;
	
	@ViewInject(R.id.et_msg_search)  
	private ClearEditText edit_search;
	
	@ViewInject(R.id.team_text)  //球队
	private TextView team_text;
	
	@ViewInject(R.id.search_team_list)  
	private ListView search_team_list;

	@ViewInject(R.id.player_text)  //球员
	private TextView player_text;
	
	@ViewInject(R.id.search_player_list)  
	private ListView search_player_list;
	
	@ViewInject(R.id.search_result)
	private LinearLayout search_result;
	
	@ViewInject(R.id.tv_search_content)  //用户输入的内容
	private TextView tv_search_content;
	
	@ViewInject(R.id.rtl_search_layout)
	private RelativeLayout rtl_search_layout;
	
	@ViewInject(R.id.playerabout)
	private View playerabout;
	
	@ViewInject(R.id.teamabout)
	private View teamabout;
	
	@ViewInject(R.id.homes_lin)
	private LinearLayout homes_lin;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		this.finish();
	}
	
	@OnClick(R.id.rtl_search_layout)
	public void fun_2(View v){
		String contentStr = tv_search_content.getText().toString();
		if(TextUtils.isEmpty(contentStr)){
			return;
		}
		//开始搜索
		executeHomeSearch(contentStr);
	}
	
	List<Team> teamLists = null;
	List<Player> playerLists = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		ViewUtils.inject(this);
		
		search_result.setVisibility(View.GONE);
		
		//用户输入操作
		EditTextDO();  
	}
	
	private void EditTextDO(){
		edit_search.addTextChangedListener(new TextWatcher(){
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				String content = arg0.toString();
				if(content.isEmpty()){  //用户没有输入
					rtl_search_layout.setVisibility(View.GONE);
				}else{  //用户输入了内容
					rtl_search_layout.setVisibility(View.VISIBLE);
					tv_search_content.setText(content);
					
					search_result.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void executeHomeSearch(String searchContent){
		rtl_search_layout.setVisibility(View.GONE);
		
		dialog = ProgressDialog.show(mContext, "提示", "搜索中...", false, false);
		NetWorkManager.getInstance(mContext).aboutHomeSearch(tv_search_content.getText().toString(), 
				UserManager.getInstance(mContext).getCurrentUser().getToken(), 
				new Listener<JSONObject>(){
					@Override
					public void onResponse(JSONObject response) {
						dialog.dismiss();
						Gson gson = new Gson();
						searchReponse = gson.fromJson(response.toString(), 
								new TypeToken<HomeSearchResponse>(){}.getType());
						
						List<Team> searchTeamList = searchReponse.getTeams();
						List<Player> searchPlayerList = searchReponse.getPlayers();
						if(searchTeamList.size() == 0 && searchPlayerList.size() == 0){
							ShowToast("没有找到相关的内容");
							search_result.setVisibility(View.GONE);
							
							rtl_search_layout.setVisibility(View.VISIBLE);
							tv_search_content.setText(edit_search.getText().toString());
							return;
						}
						
						search_result.setVisibility(View.VISIBLE);
						if(searchTeamList.size() != 0 && searchTeamList != null){
							team_text.setVisibility(View.VISIBLE);
							search_team_list.setVisibility(View.VISIBLE);
							
							teamAdapter = new HomeSearchTeamAdapter(mContext, searchTeamList, null);
							search_team_list.setAdapter(teamAdapter);
							setHeightListView(search_team_list);
							
							search_team_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									Team bean = (Team)search_team_list.getItemAtPosition(arg2);
									boolean isGuest = true;//是否来宾权限
									List<TeamPlayer> teamPlayerList = CustomApplcation.getDaoSession(mContext).getTeamPlayerDao()._queryPlayer_TeamPlayerList(UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid());
									for(TeamPlayer teamPlayer : teamPlayerList){
										Team team = CustomApplcation.getDaoSession(mContext).getTeamDao().queryBuilder().where(TeamDao.Properties.Uuid.eq(teamPlayer.getTeam_id())).build().unique();
										if(team.getUuid().equals(bean.getUuid())){
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
										intent.putExtra(TeamInfoActivity.INTENT_NEED_TO_GET_CYBER_NEW, true);
										startActivityForResult(intent,REQUEST_CODE_RECEIPT_TEAM_INFO);
									}
								}
							});
						}else{
							team_text.setVisibility(View.GONE);
							search_team_list.setVisibility(View.GONE);
							teamabout.setVisibility(View.GONE);
						}
						
						if(searchPlayerList.size() != 0 && searchPlayerList != null){
							player_text.setVisibility(View.VISIBLE);
							search_player_list.setVisibility(View.VISIBLE);
							
							memberAdapter = new HomeSearchPlayerAdapter(mContext, searchPlayerList, null);
							search_player_list.setAdapter(memberAdapter);
							setHeightListView(search_player_list);
							
							search_player_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(mContext, TeamPlayerInfo.class);
									intent.putExtra(TeamPlayerInfo.PLAYER_UUID, memberAdapter.getItem(arg2).getUuid());
									startActivity(intent);
								}
							});
						}else{
							player_text.setVisibility(View.GONE);
							search_player_list.setVisibility(View.GONE);
							playerabout.setVisibility(View.GONE);
						}
					}}
				, new ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						if(!NetWorkManager.getInstance(mContext).isNetConnected()){
							ShowToast("当前网络不可用");
							return;
						}
						if(error.networkResponse == null){
//							ShowToast("HomeSearchActivity-executeHomeSearch-服务器连接错误");
						}else if(error.networkResponse.statusCode == 401){
							ErrorCodeUtil.ErrorCode401(mContext);
						}else if(error.networkResponse.statusCode == 400){
							ShowToast("请输入搜索关键词");
						}
					}
				});
	}
	
	protected void setHeightListView(ListView lv) {
		ListAdapter la = lv.getAdapter();
		if (la == null) {
			return;
		}
		int i = 0;
		int h = 0;
		for (i = 0; i < la.getCount(); i++) {
			View item = la.getView(i, null, lv);
			item.measure(0, 0);
			h += item.getMeasuredHeight();
		}
		ViewGroup.LayoutParams p = lv.getLayoutParams();
		p.height = h + lv.getDividerHeight() * (lv.getCount() - 1);
		lv.setLayoutParams(p);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode != RESULT_OK) {
			return;
		}
		if(requestCode == REQUEST_CODE_RECEIPT_TEAM_INFO){
			Team receiptTeam = intent.getParcelableExtra(TeamInfoForGuestActivity.INTENT_TEAM_INFO_BEAN);
			if(receiptTeam == null || searchReponse == null || searchReponse.getTeams() == null){
				return;
			}
			for(int i = 0; i < searchReponse.getTeams().size(); i++){
				if(receiptTeam.getUuid().equals(searchReponse.getTeams().get(i).getUuid())){
					searchReponse.getTeams().remove(i);
					searchReponse.getTeams().add(i, receiptTeam);
					teamAdapter.notifyDataSetChanged();
					return;
				}
			}
			return;
		}
	}
}
