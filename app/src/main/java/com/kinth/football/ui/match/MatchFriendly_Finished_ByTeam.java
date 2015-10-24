package com.kinth.football.ui.match;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.bean.MatchInfoResponse;
import com.kinth.football.bean.Pageable;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.config.Config;
import com.kinth.football.config.MatchStateEnum;
import com.kinth.football.config.MatchTypeEnum;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.TeamMatchUitl;
import com.kinth.football.util.ViewHolder;
import com.kinth.football.view.CircularProgressView;
import com.kinth.football.view.xlist.XListView;
import com.kinth.football.view.xlist.XListView.IXListViewListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 球队查看友谊赛战绩 -- 已结束的比赛
 * @author Sola
 *
 */
@ContentView(R.layout.activity_match_all_finshed)
public class MatchFriendly_Finished_ByTeam extends BaseActivity {
	public static final String INTENT_TEAM_UUID = "INTENT_TEAM_UUID";

	public int page = 0; // 第一页
	private int totalPages; // 总页数
	private int totalElements; //总条数

	private Adapter_match_all_finished adapter;
	private List<MatchInfo> matchInfoList = new ArrayList<MatchInfo>();

	private LayoutInflater inflater;
	
	@ViewInject(R.id.entire_layout)
	private View entireLayout;
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;
	
	@ViewInject(R.id.nav_title)
	private TextView title;
	
	@ViewInject(R.id.nav_right_progress)
	private CircularProgressView circularProgress;
	
	@ViewInject(R.id.list_match_all_message)
	private XListView xListView;
	
	@ViewInject(R.id.tv_has_no_data_current)
	private TextView noDataTips;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		finish();
	}

	private String teamUuid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(entireLayout, new BitmapDrawable(getResources(),
				background));
		
		title.setText("友谊赛");
		teamUuid = getIntent().getStringExtra(INTENT_TEAM_UUID);
		if (TextUtils.isEmpty(teamUuid)) {
			return;
		}
		circularProgress.setVisibility(View.VISIBLE);

		inflater = LayoutInflater.from(mContext);
		
		xListView.setDivider(null);
		xListView.setDividerHeight(20);
		adapter = new Adapter_match_all_finished();
		xListView.setAdapter(adapter);
		xListView.setPullRefreshEnable(true);
		xListView.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() { // 刷新
				page = 0;//重置page
				executeMatchList();
			}

			@Override
			public void onLoadMore() { // 加载更多
				if (page > totalPages) {
					ShowToast("已没有更多数据");
					xListView.stopLoadMore();
					xListView.setPullLoadEnable(false);
					return;
				} else {
					executeLoadMoreAllMatchList();
				}
			}
		});
		xListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				MatchInfo matchInfo = adapter.getItem(position - 1);

				Intent intent = null;
				switch(MatchStateEnum.getEnumFromString(matchInfo.getState())){
				case INVITING:
					intent = new Intent(mContext,
							MatchDetailOnInvitationStateActivity.class);
					intent.putExtra(
							MatchDetailOnInvitationStateActivity.INTENT_MATCH_DETAIL_BEAN,
							matchInfo);
					startActivity(intent);
					break;
				case CREATED:
					intent = new Intent(mContext,
							MatchDetailOnCreatedStateActivity.class);
					intent.putExtra(
							MatchDetailOnCreatedStateActivity.INTENT_MATCH_DETAIL_BEAN,
							matchInfo);
					startActivity(intent);
					break;
				case PENDING:
					intent = new Intent(mContext,
							MatchDetailOnPendingStateActivity.class);
					intent.putExtra(
							MatchDetailOnPendingStateActivity.INTENT_MATCH_DETAIL_BEAN,
							matchInfo);
					startActivity(intent);
					break;
				case PLAYING:
					intent = new Intent(mContext,
							MatchDetailOnKickOffStateActivity.class);
					intent.putExtra(
							MatchDetailOnKickOffStateActivity.INTENT_MATCH_DETAIL_BEAN,
							matchInfo);
					startActivity(intent);
					break;
				case OVER:
					intent = new Intent(mContext,
							MatchDetailOnOverStateActivity.class);
					intent.putExtra(
							MatchDetailOnOverStateActivity.INTENT_MATCH_DETAIL_BEAN,
							matchInfo);
					startActivity(intent);
					break;
				case FINISHED:
					intent = new Intent(mContext,
							MatchDetailOnFinishedStateActivity.class);
					intent.putExtra(
							MatchDetailOnFinishedStateActivity.INTENT_MATCH_DETAIL_BEAN,
							matchInfo);
					startActivity(intent);
					break;
				case CANCELED:
					intent = new Intent(mContext,
							MatchDetailOnCancleStateActivity.class);
					intent.putExtra(
							MatchDetailOnCancleStateActivity.INTENT_MATCH_DETAIL_BEAN,
							matchInfo);
					startActivity(intent);
					break;
				case NULL:
					break;
				}
			}
		});
//		readMatchListFromDB();
		executeMatchList();
	}

	/**
	 * 异步加载数据库缓存数据
	 */
	class LoadCacheDataTask extends AsyncTask<Void, Void, List<MatchInfo>> {

		@Override
		protected List<MatchInfo> doInBackground(Void... arg0) {
			return TeamMatchUitl.getTeamMatchList(mContext, teamUuid,
					MatchStateEnum.FINISHED.getValue(), MatchTypeEnum.FRIENDLY_GAME.getValue());
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

	private void executeMatchList() {
		xListView.pullRefreshing();
		// 获取球员相关的比赛列表(此处为所有列表)
		NetWorkManager.getInstance(mContext).getTeamRecordGames(teamUuid,
				page, 10, MatchStateEnum.FINISHED.getValue(), MatchTypeEnum.FRIENDLY_GAME.getValue(),
				UserManager.getInstance(mContext).getCurrentUser().getToken(),new RequestCallBack<String>() {
					
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						xListView.stopRefresh();
						Gson gson = new Gson();
						MatchInfoResponse matchInfoResponse = null;
						try {
							matchInfoResponse = gson.fromJson(
									responseInfo.result,
									new TypeToken<MatchInfoResponse>() {
									}.getType());
						} catch (JsonSyntaxException e) {
							matchInfoResponse = null;
							e.printStackTrace();
						}
						circularProgress.setVisibility(View.GONE);
						// 将从服务器上获取得到的“我的球队”数据加入本地数据库，缓存
						if (matchInfoResponse != null
								&& matchInfoResponse.getMatchs() != null
								&& matchInfoResponse.getMatchs().size() != 0) {
							List<MatchInfo> tempMatchInfoList = matchInfoResponse
									.getMatchs();
							matchInfoList = tempMatchInfoList;
							adapter.notifyDataSetChanged();
//							DBUtil.saveMatchInfoListToDB(mContext,
//									matchInfoList,null);

							Pageable pageable = matchInfoResponse.getPageable();
							totalPages = pageable.getTotalPages(); // 总页数
							totalElements = pageable.getTotalElements();// //总条数
							page++;
							xListView.setPullLoadEnable(true);
						}else if(matchInfoList.size() == 0){
							noDataTips.setVisibility(View.VISIBLE);
						}
					}
					
					@Override
					public void onFailure(HttpException error, String arg1) {
						xListView.stopRefresh();
						xListView.stopLoadMore();
						noDataTips.setVisibility(View.VISIBLE);
						circularProgress.setVisibility(View.GONE);
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("没有可用的网络");
						} else if (error == null) {
//							ShowToast("MatchFriendly_finished_ByTeam-executeMatchList-服务器连接错误");
						} else if (error.getExceptionCode() == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else if (error.getExceptionCode() == 404) {
//							ShowToast("球队找不到");
						}
					}
				});
	}

	/**
	 * 联网加载更多比赛
	 */
	private void executeLoadMoreAllMatchList() {
		//获取球员相关的比赛列表(此处为所有列表)
		NetWorkManager.getInstance(mContext).getTeamRecordGames(teamUuid,
				page, Config.PAGE_SIZE, MatchStateEnum.FINISHED.getValue(), MatchTypeEnum.FRIENDLY_GAME.getValue(),
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new RequestCallBack<String>() {
					
					@Override
					public void onSuccess(ResponseInfo<String> response) {
						xListView.stopLoadMore();
						Gson gson = new Gson();
						MatchInfoResponse matchInfoResponse = null;
						try {
							matchInfoResponse = gson.fromJson(
									response.result,
									new TypeToken<MatchInfoResponse>() {
									}.getType());
						} catch (JsonSyntaxException e) {
							matchInfoResponse = null;
							e.printStackTrace();
						}
						// 将从服务器上获取得到的“我的球队”数据加入本地数据库，缓存
						if (matchInfoResponse != null && matchInfoResponse.getMatchs() != null && matchInfoResponse.getMatchs().size() != 0) {
							matchInfoList.addAll(matchInfoResponse.getMatchs());
							adapter.notifyDataSetChanged();
//							DBUtil.saveMatchInfoListToDB(mContext, matchInfoList,null);
							
							Pageable pageable = matchInfoResponse.getPageable();
							totalPages = pageable.getTotalPages();    //总页数
							totalElements = pageable.getTotalElements();  //总条数
							page++;
							if(page == totalPages){
								ShowToast("没有更多");
								xListView.stopLoadMore();
								xListView.setPullLoadEnable(false);
							}
						}else{
							ShowToast("没有更多");
						}
					}
					
					@Override
					public void onFailure(HttpException error, String arg1) {
						xListView.stopLoadMore();
						
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
//							ShowToast("没有可用的网络");
						} else if (error == null) {
//							ShowToast("MatchFriendly_finished_ByTeam-executeLoadMoreAllMatchList-服务器连接错误");
						} else if (error.getExceptionCode() == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						}else if(error.getExceptionCode() == 404){
//							ShowToast("球队找不到");
						}
					}
				});
	}
	
	class Adapter_match_all_finished extends BaseAdapter {

		@Override
		public int getCount() {
			return matchInfoList == null ? 0 : matchInfoList.size();
		}

		@Override
		public MatchInfo getItem(int position) {
			return matchInfoList == null ? null : matchInfoList.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_match_list_no_score, null);
			}
			ImageView homeTeamIcon = ViewHolder.get(convertView,
					R.id.iv_home_team_icon);
			TextView homeTeamName = ViewHolder.get(convertView,
					R.id.tv_home_team_name);
			TextView date = ViewHolder.get(convertView, R.id.tv_match_date);
			ImageView awayTeamIcon = ViewHolder.get(convertView,
					R.id.iv_away_team_icon);
			TextView awayTeamName = ViewHolder.get(convertView,
					R.id.tv_away_team_name);

			PictureUtil.getMd5PathByUrl(
					PhotoUtil.getThumbnailPath(matchInfoList.get(position)
							.getHomeTeam().getBadge()), homeTeamIcon,
							new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.team_bage_default)
					.showImageForEmptyUri(R.drawable.team_bage_default)
					.showImageOnFail(R.drawable.team_bage_default).build());
			homeTeamName.setText(matchInfoList.get(position).getHomeTeam()
					.getName());
			
			switch (MatchStateEnum.getEnumFromString(matchInfoList
					.get(position).getState())) {
			case INVITING:
				date.setText("创建中比赛");
				break;
			case CREATED:
				date.setText("报名中比赛");
				break;
			case PENDING:
				date.setText("等待比赛");
				break;
			case PLAYING:
				date.setText("进行的比赛");
				break;
			case OVER:
				date.setText("待评分的比赛");
				break;
			case FINISHED:
				date.setText("已结束的比赛");
				break;
			case CANCELED:
				date.setText("已取消的比赛");
				break;
			case NULL:
				break;
			}
			PictureUtil.getMd5PathByUrl(
					PhotoUtil.getThumbnailPath(matchInfoList.get(position)
							.getAwayTeam().getBadge()), awayTeamIcon,
							new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.team_bage_default)
					.showImageForEmptyUri(R.drawable.team_bage_default)
					.showImageOnFail(R.drawable.team_bage_default).build());
			awayTeamName.setText(matchInfoList.get(position).getAwayTeam()
					.getName());

			return convertView;
		}
	}

//	private void readMatchListFromDB() {
//		new LoadCacheDataTask().execute();
//	}
}
