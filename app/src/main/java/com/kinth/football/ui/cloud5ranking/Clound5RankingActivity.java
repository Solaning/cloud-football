package com.kinth.football.ui.cloud5ranking;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.R.color;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.ui.team.TeamPlayerInfo;
import com.kinth.football.view.xlist.XListView;
import com.kinth.football.view.xlist.XListView.IXListViewListener;

public class Clound5RankingActivity extends BaseActivity implements
		OnClickListener, IXListViewListener {

	private LinearLayout lltTabs;
	private Button btnComposite;
	private Button btnAttack;
	private Button btnDefence;
	private Button btnAwareness;
	private Button btnSkill;
	private Button btnStrength;

	private XListView mListView;
	private Clound5RankingAdapter crAdapter;

	private String currentType;
	private int page = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_clound5_ranking_list);
		initView();
		initData();
	}

	private void initView() {
		// TODO 自动生成的方法存根
		initHeader();

		lltTabs = (LinearLayout) findViewById(R.id.llt_tabs);
		btnComposite = (Button) findViewById(R.id.btn_composite);
		btnAttack = (Button) findViewById(R.id.btn_attack);
		btnDefence = (Button) findViewById(R.id.btn_defence);
		btnAwareness = (Button) findViewById(R.id.btn_awareness);
		btnSkill = (Button) findViewById(R.id.btn_skill);
		btnStrength = (Button) findViewById(R.id.btn_strength);

		btnComposite.setOnClickListener(this);
		btnAttack.setOnClickListener(this);
		btnDefence.setOnClickListener(this);
		btnAwareness.setOnClickListener(this);
		btnSkill.setOnClickListener(this);
		btnStrength.setOnClickListener(this);
		
		initXListView();
	}

	private void initHeader() {
		// TODO 自动生成的方法存根
		initTopBarForLeft("云五排行");
	}

	private void initXListView() {
		// TODO 自动生成的方法存根
		mListView = (XListView) findViewById(R.id.list_cloud5_ranking);
		mListView.setPullLoadEnable(true);// 首先不允许加载更多
		mListView.setPullRefreshEnable(false);// 允许下拉
		mListView.setXListViewListener(this);// 设置监听器
		mListView.pullRefreshing();// 下拉刷新
		mListView.setDivider(new ColorDrawable(Color.DKGRAY));
		mListView.setDividerHeight(1);// 每一项间隔高度为2
		crAdapter = new Clound5RankingAdapter(mContext);
		mListView.setAdapter(crAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO 自动生成的方法存根
				CloudFive cloudFive = (CloudFive) crAdapter.getItem(position-1);
				
				Intent intent = new Intent(mContext,
						TeamPlayerInfo.class);
				intent.putExtra(TeamPlayerInfo.PLAYER_UUID,
						cloudFive.getUuid());
				startActivity(intent);
			}
		});
	}
	
	private void initData() {
		// TODO 自动生成的方法存根
		currentType = Cloud5RankingTypeEnum.COMPOSITE.getValue();
		executeGetCloud5RankingList(Cloud5RankingTypeEnum.COMPOSITE
				.getValue());
		setTabSelected(btnComposite);
	}

	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		page = 1;
		switch (v.getId()) {
		case R.id.btn_composite:
			currentType = Cloud5RankingTypeEnum.COMPOSITE.getValue();
			executeGetCloud5RankingList(Cloud5RankingTypeEnum.COMPOSITE
					.getValue());
			setTabSelected(btnComposite);
			break;
		case R.id.btn_attack:
			currentType = Cloud5RankingTypeEnum.ATTACK.getValue();
			executeGetCloud5RankingList(Cloud5RankingTypeEnum.ATTACK.getValue());
			setTabSelected(btnAttack);
			break;
		case R.id.btn_defence:
			currentType = Cloud5RankingTypeEnum.DEFENCE.getValue();
			executeGetCloud5RankingList(Cloud5RankingTypeEnum.DEFENCE
					.getValue());
			setTabSelected(btnDefence);
			break;
		case R.id.btn_awareness:
			currentType = Cloud5RankingTypeEnum.AWARENESS.getValue();
			executeGetCloud5RankingList(Cloud5RankingTypeEnum.AWARENESS
					.getValue());
			setTabSelected(btnAwareness);
			break;
		case R.id.btn_skill:
			currentType = Cloud5RankingTypeEnum.SKILL.getValue();
			executeGetCloud5RankingList(Cloud5RankingTypeEnum.SKILL.getValue());
			setTabSelected(btnSkill);
			break;
		case R.id.btn_strength:
			currentType = Cloud5RankingTypeEnum.STRENGTH.getValue();
			executeGetCloud5RankingList(Cloud5RankingTypeEnum.STRENGTH
					.getValue());
			setTabSelected(btnStrength);
			break;
		}
	}

	private void executeGetCloud5RankingList(final String type) {
		// TODO 自动生成的方法存根
		NetWorkManager.getInstance(mContext).getCloud5Ranking(type,
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				0, page * 10, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.e("TAG", response.toString());
						Gson gson = new Gson();
						Cloud5RankingBean cloud5Ranking = null;
						try {
							cloud5Ranking = gson.fromJson(response.toString(),
									new TypeToken<Cloud5RankingBean>() {
									}.getType());
						} catch (JsonSyntaxException e) {
							cloud5Ranking = null;
							e.printStackTrace();
						}
						Log.e("TAG",cloud5Ranking.getPageable().toString());
						if (cloud5Ranking != null
								&& cloud5Ranking.getCloudFives() != null) {
							crAdapter.updateListView(
									cloud5Ranking.getCloudFives(), type);
							mListView.stopLoadMore();
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						ShowToast("获取云五排行失败");
						mListView.stopRefresh();
					}
				});
	}

	@Override
	public void onRefresh() {
		// TODO 自动生成的方法存根
	}

	@Override
	public void onLoadMore() {
		// TODO 自动生成的方法存根
		page++;
		executeGetCloud5RankingList(currentType);
	}

	private void setTabSelected(Button btnSelected) {
		Drawable selectedDrawable = getResources().getDrawable(R.drawable.shape_nav_indicator);
		selectedDrawable.setBounds(0, 0, 90, 15);
		btnSelected.setSelected(true);
		btnSelected.setCompoundDrawables(null, null, null, selectedDrawable);
		int size = lltTabs.getChildCount();
		for (int i = 0; i < size; i++) {
			if (btnSelected.getId() != lltTabs.getChildAt(i).getId()) {
				lltTabs.getChildAt(i).setSelected(false);
				if((lltTabs.getChildAt(i)) instanceof Button)//判断是否是Button组件
					((Button) lltTabs.getChildAt(i)).setCompoundDrawables(null, null, null, null);
			}
		}
	}
	
}
