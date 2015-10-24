package com.kinth.football.ui.match;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.bean.MatchInfoResponse;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.config.MatchStateEnum;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.DBUtil;
import com.kinth.football.util.DateUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.PlayerMatchUtil;
import com.kinth.football.util.ViewHolder;
import com.kinth.football.view.xlist.XListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 待评价的比赛列表 Sola
 */
@ContentView(R.layout.activity_match_list_no_score)
public class MatchOverActivity extends BaseActivity {
	public static final String INTENT_MATCH_OVER_INFO_LIST = "INTENT_MATCH_OVER_INFO_LIST";

	@ViewInject(R.id.entire_layout)
	private View entireLayout;
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;

	@ViewInject(R.id.nav_title)
	private TextView title;

	@ViewInject(R.id.listview_match_no_score)
	private XListView listview;

	@OnClick(R.id.nav_left)
	public void fun_1(View v) {
		finish();
	}

	private LayoutInflater inflater;
	private Adapter_match_invitation adapter;
	private List<MatchInfo> matchInfoList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(entireLayout, new BitmapDrawable(getResources(),
				background));

		title.setText("待评价");
		matchInfoList = getIntent().getParcelableArrayListExtra(
				INTENT_MATCH_OVER_INFO_LIST);

		inflater = LayoutInflater.from(mContext);
		adapter = new Adapter_match_invitation();
		listview.setDivider(null);
		listview.setDividerHeight(30);
		listview.setAdapter(adapter);
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(false);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				MatchInfo matchInfo = adapter.getItem(position - 1);

				Intent intent = new Intent(mContext,
						MatchDetailOnOverStateActivity.class);
				intent.putExtra(
						MatchDetailOnOverStateActivity.INTENT_MATCH_DETAIL_BEAN,
						matchInfo);
				startActivity(intent);
			}
		});
		// readMatchListFromDB();
		// executeMatchList();
	}

	/**
	 * 获取比赛列表
	 */
	private void executeMatchList() {
		listview.pullRefreshing();
		// 查询待评价的所有球队信息
		NetWorkManager.getInstance(mContext).getStateMatchList(
				UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid(),
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				0, 1000, MatchStateEnum.OVER.getValue(),
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {// interface
						listview.stopRefresh();
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
						matchInfoList = matchInfoResponse.getMatchs();
						if (matchInfoList.size() == 0 || matchInfoList == null) {
							// ShowToast("没有当前相关比赛");
							return;
						} else {
							adapter.notifyDataSetChanged();
							/*DBUtil.saveMatchInfoListToDB(mContext,
									matchInfoList, null);*/
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						listview.stopRefresh();
						VolleyLog.e("Error: ", error.getMessage());
						// ShowToast("获取球队列表失败");
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("没有可用的网络");
						} else if (error.networkResponse == null) {
//							ShowToast("MatchOverActivity-executeMatchList-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						}
					}
				});
	}

	/**
	 * 异步加载数据库缓存数据
	 */
	class LoadCacheDataTask extends AsyncTask<Void, Void, List<MatchInfo>> {

		@Override
		protected List<MatchInfo> doInBackground(Void... arg0) {
			return PlayerMatchUtil.getMatchByCurrentUserId(mContext,
					UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid(),
					MatchStateEnum.OVER.getValue(), "");
		}

		@Override
		protected void onPostExecute(List<MatchInfo> result) {
			super.onPostExecute(result);
			if (result == null || result.size() == 0) {
				return;
			}
			matchInfoList = result;
			adapter.notifyDataSetChanged();
		}
	}

	class Adapter_match_invitation extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return matchInfoList == null ? 0 : matchInfoList.size();
		}

		@Override
		public MatchInfo getItem(int position) {
			// TODO Auto-generated method stub
			return matchInfoList == null ? null : matchInfoList.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.item_match_list_with_score, arg2, false);
			}
			ImageView homeTeamIcon = ViewHolder.get(convertView,
					R.id.iv_home_team_icon);
			TextView homeTeamName = ViewHolder.get(convertView,
					R.id.tv_home_team_name);
			ImageView awayTeamIcon = ViewHolder.get(convertView,
					R.id.iv_away_team_icon);
			TextView awayTeamName = ViewHolder.get(convertView,
					R.id.tv_away_team_name);
			TextView matchdate = ViewHolder
					.get(convertView, R.id.tv_match_date);
			TextView matchField = ViewHolder.get(convertView,
					R.id.tv_match_field);
			TextView txt_score = ViewHolder.get(convertView, R.id.txt_score);

			PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(matchInfoList.get(position)
							.getHomeTeam().getBadge()),
					homeTeamIcon,
					new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.team_bage_default)
							.showImageForEmptyUri(R.drawable.team_bage_default)
							.showImageOnFail(R.drawable.team_bage_default) // 默认球队队徽
							.cacheInMemory(true).cacheOnDisk(true).build());
			PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(matchInfoList.get(position)
							.getAwayTeam().getBadge()),
					awayTeamIcon,
					new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.team_bage_default)
							.showImageForEmptyUri(R.drawable.team_bage_default)
							.showImageOnFail(R.drawable.team_bage_default) // 默认球队队徽
							.cacheInMemory(true).cacheOnDisk(true).build());
			homeTeamName.setText(matchInfoList.get(position).getHomeTeam()
					.getName());
			matchdate.setText(StringUtils.defaultIfEmpty(DateUtil
					.parseTimeInMillis_no_ss(matchInfoList.get(position)
							.getKickOff()), ""));
			matchField.setText(StringUtils.defaultIfEmpty(
					matchInfoList.get(position).getField(), ""));
			awayTeamName.setText(matchInfoList.get(position).getAwayTeam()
					.getName());
			txt_score.setText(matchInfoList.get(position).getHomeTeamScore()
					+ "   :   "
					+ matchInfoList.get(position).getAwayTeamScore());
			return convertView;
		}
	}

	// 从数据库读比赛信息
	private void readMatchListFromDB() {
		new LoadCacheDataTask().execute();
	}

}
