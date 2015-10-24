package com.kinth.football.ui.team;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.adapter.InviteMemberAdapter;
import com.kinth.football.adapter.InviteMemberAdapter.OnInviteMemberListener;
import com.kinth.football.bean.SearchPersonResponse;
import com.kinth.football.config.Config;
import com.kinth.football.config.PlayerTypeEnum;
import com.kinth.football.dao.Player;
import com.kinth.football.dao.Team;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
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
 * 邀请球队成员
 * @author Sola
 */
@ContentView(R.layout.activity_invite_team_member)
public class InviteMemberActivity extends BaseActivity{
	private Team teamResponse;
	private InviteMemberAdapter adapter;
	private ProgressDialog proDialog;
	private AlertDialog.Builder  builder;
	private Dialog dialog;
	
	private String type;
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;
	
	@ViewInject(R.id.et_msg_search)
	private ClearEditText search;
	
	@ViewInject(R.id.rtl_search_layout)
	private RelativeLayout searchLayout;
	
	@ViewInject(R.id.tv_search_content)
	private TextView searchContent;
	
	@ViewInject(R.id.list_team_member)
	private XListView listview;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		finish();
	}
	
	@OnClick(R.id.rtl_search_layout)
	public void fun_2(View v){//搜索球员
		String content = search.getText().toString();
		if(TextUtils.isEmpty(content)){
			return;
		}
		proDialog = ProgressDialog.show(mContext, "提示", "请稍后", false, false);
		NetWorkManager.getInstance(mContext).searchPlayer(0, Config.PAGE_SIZE, -1,-1, content, null,UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						DialogUtil.dialogDismiss(proDialog);
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
						if(searchPersonResponse == null || searchPersonResponse.getPlayers() == null || searchPersonResponse.getPlayers().size() == 0){
							ShowToast("没有找到"); 
							return;
						}
						searchLayout.setVisibility(View.GONE);//隐藏搜索，显示结果
						listview.setVisibility(View.VISIBLE);
						listview.setDivider(null);
						listview.setDividerHeight(30);
						adapter.setSearchMemberList(searchPersonResponse.getPlayers());
						listview.stopRefresh();
					}
				},new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						DialogUtil.dialogDismiss(proDialog);
						if(!NetWorkManager.getInstance(mContext).isNetConnected()){
							ShowToast("当前网络不可用");
						}else if(error.networkResponse == null){
//							ShowToast("InviteMemberActivity-fun_2-服务器连接错误");
//							ShowToast("没有找到");
						}else if(error.networkResponse.statusCode == 401){
							ErrorCodeUtil.ErrorCode401(mContext);
						}
					}
				});
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		teamResponse = getIntent().getParcelableExtra(TeamInfoActivity.INTENT_TEAM_INFO_BEAN);
		if(teamResponse == null){
			return;
		}
		
		adapter = new InviteMemberAdapter(mContext, null,
				new OnInviteMemberListener() {
					@Override
					public void onInviteMember(final Player bean) {  // 邀请成员
						String[] types = {PlayerTypeEnum.GENERAL.getName(),PlayerTypeEnum.FIRST_CAPTAIN.getName(),PlayerTypeEnum.SECOND_CAPTAIN.getName(),PlayerTypeEnum.THIRD_CAPTAIN.getName()};
						final String[] types2 = {PlayerTypeEnum.GENERAL.getValue(),PlayerTypeEnum.FIRST_CAPTAIN.getValue(),PlayerTypeEnum.SECOND_CAPTAIN.getValue(),PlayerTypeEnum.THIRD_CAPTAIN.getValue()};
						builder = new AlertDialog.Builder(mContext);
						ListView lv = (ListView) LayoutInflater.from(mContext).inflate(R.layout.lv_invite_member, null);
//						lv.setBackgroundColor(color.listview_background);
//                      ColorDrawable drawable = new ColorDrawable(color.listview_selector);
//						lv.setSelector(drawable);
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.select_dialog_singlechoice, types);
						lv.setAdapter(adapter);
						builder.setView(lv);
						dialog = builder.show();
					
						lv.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								dialog.dismiss();
								type = types2[arg2];
								proDialog = ProgressDialog.show(mContext, "提示", "请稍后", false, false);
								NetWorkManager.getInstance(mContext).inviteMember(bean.getUuid(),type,
										teamResponse.getUuid(),
										UserManager.getInstance(mContext)
												.getCurrentUser().getToken(),
										new Listener<Void>() {
											@Override
											public void onResponse(Void response) {
												DialogUtil.dialogDismiss(proDialog);
												ShowToast("发送邀请成功");
											}
										}, new ErrorListener() {
											@Override
											public void onErrorResponse(VolleyError error) {
												DialogUtil.dialogDismiss(proDialog);
												if(!NetWorkManager.getInstance(mContext).isNetConnected()){
													ShowToast("当前网络不可用");
													return;
												}
												if(error.networkResponse != null){
													if(error.networkResponse.statusCode == 401){
														ErrorCodeUtil.ErrorCode401(mContext);
													}else if(error.networkResponse.statusCode == 404){
//														ShowToast("球队或用户找不到");
													}else if(error.networkResponse.statusCode == 409){
														ShowToast("该球员已经是球队成员");
													}
												}
											}
										});
							}
						});
					}
				});
		
		listview.setAdapter(adapter);
		
		listview.setXListViewListener(new IXListViewListener(){

			@Override
			public void onRefresh() {
				fun_2(null);
			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				
			}});
		
		search.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String content = s.toString();
				if (TextUtils.isEmpty(content)) {// 没有其他输入，替换成原来的列表
					searchLayout.setVisibility(View.GONE);
					searchContent.setText("");
					int coun3t = adapter.getCount();
					if(coun3t > 0){
						listview.setVisibility(View.VISIBLE);
					}else {
						listview.setVisibility(View.GONE);
					}
				} else {// 有输入内容
					searchLayout.setVisibility(View.VISIBLE);
					searchContent.setText(content);
					listview.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	
	}

}
