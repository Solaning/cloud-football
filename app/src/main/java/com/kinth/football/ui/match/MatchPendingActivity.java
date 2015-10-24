package com.kinth.football.ui.match;

import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.adapter.MatchPendingAdapter;
import com.kinth.football.bean.MatchInfoResponse;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.config.MatchStateEnum;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.DBUtil;
import com.kinth.football.util.PlayerMatchUtil;
import com.kinth.football.view.xlist.XListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 待开赛的比赛列表 
 */
@ContentView(R.layout.activity_match_list_no_score)
public class MatchPendingActivity extends BaseActivity {
//	public static final String INTENT_MATCH_PENDOMG_INFO_LIST = "INTENT_MATCH_PENDOMG_INFO_LIST";
	
	private MatchPendingAdapter adapter;
	
	private List<MatchInfo> matchList;
	
	@ViewInject(R.id.entire_layout)
	private View entireLayout;
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;
	
	@ViewInject(R.id.nav_title)
	private TextView title;
	
	@ViewInject(R.id.listview_match_no_score)
	private XListView xListView;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		finish();
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
		
		title.setText("待开赛");
		matchList = CustomApplcation.getMatchInfoOfPending();
		
		adapter = new MatchPendingAdapter(mContext, matchList);
		xListView.setDivider(null);
		xListView.setDividerHeight(30);
		xListView.setAdapter(adapter);
		xListView.setPullLoadEnable(false);
		xListView.setPullRefreshEnable(false);
		xListView.setOnItemClickListener(new OnItemClickListener() {//点击某项
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MatchInfo matchInfo = adapter.getItem(position - 1);
				if(matchInfo == null){
					return;
				}
				Intent intent = new Intent(mContext, MatchDetailOnPendingStateActivity.class);
				intent.putExtra(MatchDetailOnPendingStateActivity.INTENT_MATCH_DETAIL_BEAN, matchInfo);
				startActivity(intent);// TODO
			}
		});
//		new LoadCacheDataTask().execute();
		executeGetScheduleList();
	}
	
	/**
	 * 获取日程列表
	 */
	private void executeGetScheduleList(){
		xListView.pullRefreshing();
		//查询日程--比赛列表
		NetWorkManager.getInstance(mContext).getStateMatchList(UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid(), UserManager.getInstance(mContext).getCurrentUser().getToken(),
				0, 20, MatchStateEnum.PENDING.getValue(),
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						xListView.stopRefresh();
						Gson gson = new Gson();
						MatchInfoResponse matchResponse = null;
						try{
							matchResponse = gson.fromJson(response.toString(), new TypeToken<MatchInfoResponse>(){}.getType());
						}catch(JsonSyntaxException e){
							e.printStackTrace();
						}
						matchList = matchResponse.getMatchs();
						if(matchList == null || matchList.size() == 0){
							//Toast.makeText(mContext, "没有待开赛的比赛", Toast.LENGTH_LONG).show();
							return;
						}else{
							//保存比赛信息到数据库
							/*DBUtil.saveMatchInfoListToDB(mContext, matchList,null);*/
							adapter.setMatchList(matchList);
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						xListView.stopRefresh();
						Log.e("response",""+error.getMessage());
						ShowToast("获取待开赛失败");
					}
				});
	}
	
	/**
	 * 异步加载数据库缓存数据
	 */
	class LoadCacheDataTask extends AsyncTask<Void, Void, List<MatchInfo>> {
		@Override
		protected List<MatchInfo> doInBackground(Void... arg0) {
			List<MatchInfo> _queryMatchInfoList = PlayerMatchUtil.getMatchByCurrentUserId(mContext, 
					UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid(), 
					MatchStateEnum.PENDING.getValue(),
					"");
			return _queryMatchInfoList;
		}

		@Override
		protected void onPostExecute(List<MatchInfo> result) {
			super.onPostExecute(result);
			if(result != null){
				matchList = result;
				adapter.setMatchList(matchList);
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

}
