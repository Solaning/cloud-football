package com.kinth.football.ui.team;

import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.adapter.SearchTeamAdapter;
import com.kinth.football.adapter.SearchTeamAdapter.OnSearchTeamListener;
import com.kinth.football.bean.SearchTeamResponse;
import com.kinth.football.config.Config;
import com.kinth.football.dao.Team;
import com.kinth.football.dao.TeamDao;
import com.kinth.football.dao.TeamPlayer;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.DialogUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.view.ClearEditText;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;

/**
 * 搜索球队
 * @author Sola
 */

@ContentView(R.layout.activity_search_team)
public class SearchTeamActivity extends BaseActivity{
	public static final String INTENT_SEARCH_KEY = "INTENT_SEARCH_KEY";//搜索关键字
	private static final int REQUEST_CODE_RECEIPT_TEAM_INFO = 9001;
	
	private ProgressDialog dialog;
	private SearchTeamAdapter adapter;
	private List<Team> searchTeamList;

	@ViewInject(R.id.nav_left)
	private ImageButton back;
	
	@ViewInject(R.id.et_msg_search)
	private ClearEditText search;
	
	@ViewInject(R.id.rtl_search_layout)
	private RelativeLayout searchLayout;
	
	@ViewInject(R.id.tv_search_content)
	private TextView searchContent;
	
	@ViewInject(R.id.list_team)
	private ListView searTeam_listview;
	
	@ViewInject(R.id.rtl_search_layout)
	private RelativeLayout rtl_search_layout;
	
	@ViewInject(R.id.searchteam_lin)
	private LinearLayout searchteam_lin;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		finish();
	}
	
	@OnClick(R.id.rtl_search_layout)
	public void fun_2(View v){//搜索球队
		String contentStr = searchContent.getText().toString();
		if(TextUtils.isEmpty(contentStr)){
			return;
		}
		//进行搜索
		dialog = ProgressDialog.show(mContext, "提示", "请稍后", false, false);
		NetWorkManager.getInstance(mContext).searchTeam(0, Config.PAGE_SIZE,-1, -1, contentStr,
				UserManager.getInstance(mContext).getCurrentUser().getToken(), 
				new Listener<JSONObject>(){     
					@SuppressWarnings("unchecked")
					@Override
					public void onResponse(JSONObject response) {
						DialogUtil.dialogDismiss(dialog);
						Gson gson = new Gson();
						SearchTeamResponse searchResponse = null;
						try {
							searchResponse = gson.fromJson(response.toString(),
									new TypeToken<SearchTeamResponse>(){}
									.getType());
							searchTeamList = searchResponse.getTeams();
						} catch (JsonSyntaxException e) {
							searchTeamList = null;
							e.printStackTrace();
						}
						if(searchTeamList == null || searchTeamList.size() == 0){
							ShowToast("没有找到");
							Log.i("JSON", response.toString());
							return;
						}
						searchLayout.setVisibility(View.GONE);//隐藏搜索，显示结果
						searTeam_listview.setVisibility(View.VISIBLE);
						adapter.setSearchTeamList(searchTeamList);
					
						
						//将搜索得到球队信息放入本地数据库
						new InsertSearchTeamAsyncTask().execute(searchTeamList);
					}
				},
				new ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						DialogUtil.dialogDismiss(dialog);
						if(!NetWorkManager.getInstance(mContext).isNetConnected()){
							ShowToast("没有可用的网络");
						}else if(error.networkResponse == null){
//							ShowToast("SearchTeamActivity-fun_2-服务器连接错误");
						}else if(error.networkResponse.statusCode == 401){
							ErrorCodeUtil.ErrorCode401(mContext);
						}else{
//							ShowToast("没有符合的球队");
							ShowToast("没有找到");
						}
						
					}
				});
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(searchteam_lin, new BitmapDrawable(getResources(),
				background));
		
		adapter = new SearchTeamAdapter(mContext, null,
				new OnSearchTeamListener(){
					@Override
					public void onSearchTeam(Team bean) {
						//判断用户的身份，来决定查看权限  TODO
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
							startActivityForResult(intent,REQUEST_CODE_RECEIPT_TEAM_INFO);
						}else{//成员
							intent = new Intent(mContext, TeamInfoActivity.class);
							intent.putExtra(TeamInfoActivity.INTENT_TEAM_INFO_BEAN, bean);
							intent.putExtra(TeamInfoActivity.INTENT_NEET_TO_RETURN_BEAN, true);
							startActivityForResult(intent,REQUEST_CODE_RECEIPT_TEAM_INFO);
						}
						
					}
		});
		searTeam_listview.setAdapter(adapter);
		searTeam_listview.setDividerHeight(1);
		
		search.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String content = s.toString();
				if (TextUtils.isEmpty(content)) {// 没有其他输入，替换成原来的列表
					searchLayout.setVisibility(View.GONE);
					searchContent.setText("");
					searTeam_listview.setVisibility(View.VISIBLE);
				} else {// 有输入内容
					searchLayout.setVisibility(View.VISIBLE);
					searchContent.setText(content);
					searTeam_listview.setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}});
	}
	
	class InsertSearchTeamAsyncTask extends AsyncTask<List<Team>, Void, Void>{

		@Override
		protected Void doInBackground(List<Team>... params) {
			List<Team> searchTeamList = params[0];
			
			TeamDao teamDao = CustomApplcation.getDaoSession(mContext).getTeamDao();
			for(Team item : searchTeamList){
				teamDao.insertOrReplace(item);
			}
			return null;
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode != RESULT_OK) {
			return;
		}
		if(requestCode == REQUEST_CODE_RECEIPT_TEAM_INFO){
			Team receiptTeam = intent.getParcelableExtra(TeamInfoForGuestActivity.INTENT_TEAM_INFO_BEAN);
			if(receiptTeam == null){
				return;
			}
			for(int i = 0; i < searchTeamList.size(); i++){
				if(receiptTeam.getUuid().equals(searchTeamList.get(i).getUuid())){
					searchTeamList.add(i, receiptTeam);
					adapter.notifyDataSetChanged();
					return;
				}
			}
			return;
		}
	}
	
	
	
}
