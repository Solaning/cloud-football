package com.kinth.football.ui.match;

import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.adapter.SearchRivingAdapter;
import com.kinth.football.adapter.SearchRivingAdapter.OnClickRivingListener;
import com.kinth.football.bean.SearchTeamResponse;
import com.kinth.football.dao.Team;
import com.kinth.football.dao.TeamDao;
import com.kinth.football.dao.TeamPlayer;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
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
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;

/**
 * 选择比赛对手
 * @author Sola
 *
 */
@ContentView(R.layout.activity_select_match_riving)
public class SelectMatchRivingActivity extends BaseActivity{
	private static final int REQUEST_CODE_RECEIPT_TEAM_INFO = 9001;
	public static final String RESULT_OF_MATCH_SELECT_RIVING = "RESULT_OF_MATCH_SELECT_RIVING";//选择对手的返回结果
	private static final int PAGE_SIZE = 20;
	private ProgressDialog dialog;
	private SearchRivingAdapter adapter;
	
	@ViewInject(R.id.entire_layout) 
	private View entireLayout;
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;
	
	@ViewInject(R.id.nav_title)
	private TextView title;
	
	@ViewInject(R.id.nav_right_btn)
	private Button right;
	
	@ViewInject(R.id.et_msg_search)
	private ClearEditText search;//输入搜索框
	
	@ViewInject(R.id.rtl_search_layout)
	private RelativeLayout searchLayout;//
	
	@ViewInject(R.id.tv_search_content)
	private TextView searchContent;
	
	@ViewInject(R.id.listview_select_riving)
	private XListView selectRivingList;//球队列表
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		finish();
	}
	
	@OnClick(R.id.rtl_search_layout)
	public void fun_2(View v) {// 搜索内容
		String contentStr = searchContent.getText().toString();
		if (TextUtils.isEmpty(contentStr)) {
			return;
		}
		// 进行搜索
		dialog = ProgressDialog.show(mContext, "提示", "请稍后", false, false);
		NetWorkManager.getInstance(mContext).searchTeam(0, PAGE_SIZE, -1,-1,
				contentStr,
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						DialogUtil.dialogDismiss(dialog);
						Gson gson = new Gson();
						List<Team> searchTeamList = null;
						SearchTeamResponse searchResponse = null;
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
							Log.i("JSON", response.toString());
							return;
						}
						searchLayout.setVisibility(View.GONE);// 隐藏搜索，显示结果
						selectRivingList.setVisibility(View.VISIBLE);
						adapter.setSearchTeamList(searchTeamList);
						selectRivingList.stopRefresh();
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						DialogUtil.dialogDismiss(dialog);
//						ShowToast("失败：" + error.getMessage());
						ShowToast("没有找到");
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
		ViewCompat.setBackground(entireLayout, new BitmapDrawable(getResources(),
				background));
        title.setText("选择对手");
        selectRivingList.setDivider(null);
        selectRivingList.setDividerHeight(30);
        selectRivingList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Team bean = (Team)selectRivingList.getItemAtPosition(arg2);
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
	
		adapter = new SearchRivingAdapter(mContext, null, new OnClickRivingListener() {
			
			@Override
			public void onClickRiving(Team team) {
				//返回选中的队伍信息
				Intent intent = new Intent();
				intent.putExtra(RESULT_OF_MATCH_SELECT_RIVING, team);//设置比赛对手的uuid
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		selectRivingList.setAdapter(adapter);
		selectRivingList.setXListViewListener(new IXListViewListener(){
			@Override
			public void onRefresh() {
				fun_2(null);
			}
			
			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
			}
		});
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
					selectRivingList.setVisibility(View.VISIBLE);
				} else {// 有输入内容
					searchLayout.setVisibility(View.VISIBLE);
					searchContent.setText(content);
					selectRivingList.setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	
}
