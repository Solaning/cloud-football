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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.adapter.SearchReffreeAdapter;
import com.kinth.football.adapter.SearchReffreeAdapter.OnClickRivingListener;
import com.kinth.football.bean.SearchPersonResponse;
import com.kinth.football.dao.Player;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.ui.team.TeamPlayerInfo;
import com.kinth.football.util.DialogUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.view.ClearEditText;
import com.kinth.football.view.xlist.XListView;
import com.kinth.football.view.xlist.XListView.IXListViewListener;
import com.lidroid.xutils.view.annotation.ViewInject;

/*
 * 选择裁判
 */
public class Select_Referee_Activity extends BaseActivity {
	private ClearEditText search;
	private XListView listview;
	private RelativeLayout searchLayout;
	private TextView searchContent;
	private ImageView iv_search_icon;
	private ProgressDialog proDialog;
	private static final int PAGE_SIZE = 20;

	private List<Player> player_list;
	// private List<String> name_list;
	private ImageButton back;
	private TextView title;
	private SearchReffreeAdapter adapter;
	
	@ViewInject(R.id.entire_layout)
	private View entireLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_referee_layout);
		entireLayout = findViewById(R.id.entire_layout);
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(entireLayout, new BitmapDrawable(getResources(),
				background));
		init_view();
		listview.setDivider(null);
		listview.setDividerHeight(30);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, TeamPlayerInfo.class);
				intent.putExtra(TeamPlayerInfo.PLAYER_UUID, player_list.get(arg2 - 1).getUuid());
				startActivity(intent);
			}
		});
		listview.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
			}
		});
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String content = s.toString();
				if (TextUtils.isEmpty(content)) {// 没有其他输入，替换成原来的列表
					searchLayout.setVisibility(View.GONE);
					searchContent.setText("");
					listview.setVisibility(View.VISIBLE);
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
		searchLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String content = search.getText().toString();
				if (TextUtils.isEmpty(content)) {
					return;
				}
				proDialog = ProgressDialog.show(mContext, "提示", "请稍后", false,
						false);
				NetWorkManager.getInstance(mContext).searchPlayer(
						0,
						PAGE_SIZE,
						-1,
						-1,
						content,
						null,
						UserManager.getInstance(mContext).getCurrentUser()
								.getToken(), new Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {
								DialogUtil.dialogDismiss(proDialog);
								Gson gson = new Gson();
								SearchPersonResponse searchPersonResponse = null;
								try {
									searchPersonResponse = gson.fromJson(
											response.toString(),
											new TypeToken<SearchPersonResponse>() {
											}.getType());
								} catch (JsonSyntaxException e) {
									searchPersonResponse = null;
									e.printStackTrace();
								}
								if (searchPersonResponse == null
										|| searchPersonResponse.getPlayers() == null
										|| searchPersonResponse.getPlayers()
												.size() == 0) {
									ShowToast("没有找到");
									return;
								}
								player_list = searchPersonResponse.getPlayers();
								searchLayout.setVisibility(View.GONE);// 隐藏搜索，显示结果
								listview.setVisibility(View.VISIBLE);
								adapter.setplayerList(player_list);
								listview.stopRefresh();
								adapter.notifyDataSetChanged();// 更新数据
							}
						}, new ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								// TODO Auto-generated method stub
								DialogUtil.dialogDismiss(proDialog);
								if (!NetWorkManager.getInstance(mContext)
										.isNetConnected()) {
									ShowToast("当前网络不可用");
								} else if (error.networkResponse == null) {
//									ShowToast("Select_Referee_Activity-onCreate-服务器连接错误");
//									ShowToast("没有找到");
								} else if (error.networkResponse.statusCode == 401) {
									ErrorCodeUtil.ErrorCode401(mContext);
								}
							}
						});
			}

		});
	}

	private void init_view() {
		// name_list = new ArrayList<String>();
		search = (ClearEditText) findViewById(R.id.referee_search);
		listview = (XListView) findViewById(R.id.list_team_member);
		searchLayout = (RelativeLayout) findViewById(R.id.rtl_search_layout);
		searchContent = (TextView) findViewById(R.id.tv_search_content);
		iv_search_icon = (ImageView) findViewById(R.id.iv_search_icon);
		// adapter = new ArrayAdapter<String>(mContext,
		// android.R.layout.simple_expandable_list_item_1, name_list);

		adapter = new SearchReffreeAdapter(mContext, null,
				new OnClickRivingListener() {

					@Override
					public void onClickRiving(Player player) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.putExtra("player", player);// 设置比赛对手的uuid
						setResult(RESULT_OK, intent);
						finish();
					}
				});
		listview.setAdapter(adapter);
		title = (TextView) findViewById(R.id.nav_title);
		title.setText("邀请裁判");
		back = (ImageButton) findViewById(R.id.nav_left);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}
}