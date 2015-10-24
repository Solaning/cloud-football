package com.kinth.football.ui.match;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
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
import com.kinth.football.ui.fragment.MatchFragment;
import com.kinth.football.util.DateUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.PlayerMatchUtil;
import com.kinth.football.util.ViewHolder;
import com.kinth.football.view.CircularProgressView;
import com.kinth.football.view.xlist.XListView;
import com.kinth.football.view.xlist.XListView.IXListViewListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 锦标赛列表
 * 
 * @author zyq （调用该Activity的时候需要传递一个用户ID参数）
 */
@ContentView(R.layout.activity_match_all)
public class LeagueListActivity extends BaseActivity {

	private String playerUuid; // intent中传递的参数
	private static final int PAGE_SIZE = 5;

	@ViewInject(R.id.entire_layout)
	private View entireLayout;
	
	@ViewInject(R.id.nav_title)
	private TextView title;

	@ViewInject(R.id.nav_left)
	private ImageButton back;

	@ViewInject(R.id.list_match_all_message)
	private XListView xListview;

	@ViewInject(R.id.tv_has_no_data_current)
	private TextView noDataTips;
	
	@ViewInject(R.id.nav_right_progress)
	private CircularProgressView circularProgress;

	@OnClick(R.id.nav_left)
	public void fun_1(View v) {
		finish();
	}

	private LayoutInflater inflater;

	private FriendlyMatchAdapter adapter;
	private List<MatchInfo> matchInfoList = new ArrayList<MatchInfo>();

	private int page = 0; // 记录已加载到的页数
	private int totalPages;
	private int totalElements;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(entireLayout, new BitmapDrawable(getResources(),
				background));

		playerUuid = this.getIntent().getExtras()
				.getString(MatchFragment.INTENT_LEAGUE_MATCH_DATA);
		if (playerUuid == null) {
			return;
		}
		circularProgress.setVisibility(View.VISIBLE);

		initView();
		adapter = new FriendlyMatchAdapter();
		xListview.setDivider(null);
		xListview.setDividerHeight(30);
		xListview.setAdapter(adapter);
		xListview.setPullRefreshEnable(true);
		xListview.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() { // 刷新
				page = 0;// 重置page
				executeFriendlyMatchList();
			}

			@Override
			public void onLoadMore() { // 加载更多
				if (page == totalPages) {
					xListview.stopLoadMore();
					ShowToast("没有更多");
					xListview.setPullLoadEnable(false);
					return;
				}
				executeLoadMoreFriendlyMatchList();
			}
		});
		xListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				MatchInfo matchInfo = adapter.getItem(position - 1);

				switch (MatchStateEnum.getEnumFromString(matchInfo.getState())) {
				case INVITING:
					Intent intent = new Intent(mContext,
							MatchDetailOnInvitationStateActivity.class);
					intent.putExtra(
							MatchDetailOnInvitationStateActivity.INTENT_MATCH_DETAIL_BEAN,
							matchInfo);
					startActivity(intent);
					break;
				case CREATED:
					Intent intent2 = new Intent(mContext,
							MatchDetailOnCreatedStateActivity.class);
					intent2.putExtra(
							MatchDetailOnCreatedStateActivity.INTENT_MATCH_DETAIL_BEAN,
							matchInfo);
					startActivity(intent2);
					break;
				case PENDING:
					Intent intent3 = new Intent(mContext,
							MatchDetailOnPendingStateActivity.class);
					intent3.putExtra(
							MatchDetailOnPendingStateActivity.INTENT_MATCH_DETAIL_BEAN,
							matchInfo);
					startActivity(intent3);
					break;
				case PLAYING:
					Intent intent4 = new Intent(mContext,
							MatchDetailOnKickOffStateActivity.class);
					intent4.putExtra(
							MatchDetailOnKickOffStateActivity.INTENT_MATCH_DETAIL_BEAN,
							matchInfo);
					startActivity(intent4);
					break;
				case OVER:
					Intent intent5 = new Intent(mContext,
							MatchDetailOnOverStateActivity.class);
					intent5.putExtra(
							MatchDetailOnOverStateActivity.INTENT_MATCH_DETAIL_BEAN,
							matchInfo);
					startActivity(intent5);
					break;
				case FINISHED:
					Intent intent6 = new Intent(mContext,
							MatchDetailOnFinishedStateActivity.class);
					intent6.putExtra(
							MatchDetailOnFinishedStateActivity.INTENT_MATCH_DETAIL_BEAN,
							matchInfo);
					startActivity(intent6);
					break;
				case CANCELED:
//					Intent intent7 = new Intent(mContext,
//							MatchDetailOnCancleStateActivity.class);
//					intent7.putExtra(
//							MatchDetailOnCancleStateActivity.INTENT_MATCH_DETAIL_BEAN,
//							matchInfo);
//					startActivity(intent7);
					break;
				case NULL:
				default:
					break;
				}
			}
		});
		// new LoadCacheDataTask().execute();
		executeFriendlyMatchList();
	}

	private void initView() {
		inflater = LayoutInflater.from(mContext);
		title.setText("锦标赛");
	}

	class LoadCacheDataTask extends AsyncTask<Void, Void, List<MatchInfo>> {

		@Override
		protected List<MatchInfo> doInBackground(Void... arg0) {
			return PlayerMatchUtil.getMatchByCurrentUserId(mContext,
					playerUuid, "", MatchTypeEnum.LEAGUE.getValue());
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

	/**
	 * 加载更多
	 */
	private void executeLoadMoreFriendlyMatchList() {
		NetWorkManager.getInstance(mContext).getPlayerMatch(playerUuid, MatchStateEnum.FINISHED.getValue(),
				MatchTypeEnum.LEAGUE.getValue(), false,
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				page, PAGE_SIZE,

				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						xListview.stopLoadMore();
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
						// 将从服务器上获取得到的“我的球队”数据加入本地数据库，缓存
						if (matchInfoResponse != null
								&& matchInfoResponse.getMatchs() != null
								&& matchInfoResponse.getMatchs().size() != 0) {
							matchInfoList.addAll(matchInfoResponse.getMatchs());
							adapter.notifyDataSetChanged();
//							DBUtil.saveMatchInfoListToDB(mContext,
//									matchInfoResponse.getMatchs(), null);

							Pageable pageable = matchInfoResponse.getPageable();
							totalPages = pageable.getTotalPages(); // 总页数
							totalElements = pageable.getTotalElements(); // 总条数

							page++;
							if(page == totalPages){
								ShowToast("没有更多");
								xListview.stopLoadMore();
								xListview.setPullLoadEnable(false);
							}
						}else{
							ShowToast("没有更多");
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						xListview.stopLoadMore();
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
//							ShowToast("没有可用的网络");
						} else if (error.networkResponse == null) {
//							ShowToast("FriendlyListActivity-executeLoadMoreFriendlyMatchList-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						}
					}
				});
	}

	/**
	 * 刷新
	 */
	private void executeFriendlyMatchList() {
		xListview.pullRefreshing();
		NetWorkManager.getInstance(mContext).getPlayerMatch(playerUuid,
				MatchStateEnum.FINISHED.getValue(),
				MatchTypeEnum.LEAGUE.getValue(), false,
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				page, PAGE_SIZE,

				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						xListview.stopRefresh();
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
						circularProgress.setVisibility(View.GONE);
						// 将从服务器上获取得到的“我的球队”数据加入本地数据库，缓存
						if (matchInfoResponse != null
								&& matchInfoResponse.getMatchs() != null
								&& matchInfoResponse.getMatchs().size() != 0) {
							matchInfoList = matchInfoResponse.getMatchs();
							adapter.notifyDataSetChanged();
							xListview.setPullLoadEnable(true);
							
//							DBUtil.saveMatchInfoListToDB(mContext,
//									matchInfoResponse.getMatchs(), null);

							Pageable pageable = matchInfoResponse.getPageable();
							totalPages = pageable.getTotalPages(); // 总页数
							totalElements = pageable.getTotalElements(); // 总条数
							page++;
						} else if (matchInfoList.size() == 0) {
							noDataTips.setVisibility(View.VISIBLE);
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						xListview.stopRefresh();
						xListview.stopLoadMore();
						noDataTips.setVisibility(View.VISIBLE);
						circularProgress.setVisibility(View.GONE);
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("没有可用的网络");
						} else if (error.networkResponse == null) {
//							ShowToast("FriendlyListActivity-executeFriendlyMatchList-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						}
					}
				});
	}

	class FriendlyMatchAdapter extends BaseAdapter {

		@Override
		// 取消子项的点击事件
		public boolean isEnabled(int position) {
			// TODO Auto-generated method stub
			return false;
		}

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
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_all_match_list,
						arg2, false);
			}
			ImageView homeTeamIcon = ViewHolder.get(convertView,
					R.id.iv_home_team_icon);
			TextView homeTeamName = ViewHolder.get(convertView,
					R.id.tv_home_team_name);
			TextView matchType = ViewHolder
					.get(convertView, R.id.tv_match_type);
			TextView matchDate = ViewHolder
					.get(convertView, R.id.tv_match_date);
			TextView matchField = ViewHolder.get(convertView,
					R.id.tv_match_field);
			// TextView matchDescription = ViewHolder.get(convertView,
			// R.id.tv_match_description);
			ImageView awayTeamIcon = ViewHolder.get(convertView,
					R.id.iv_away_team_icon);
			TextView awayTeamName = ViewHolder.get(convertView,
					R.id.tv_away_team_name);

			PictureUtil.getMd5PathByUrl(
					PhotoUtil.getThumbnailPath(matchInfoList.get(position)
							.getHomeTeam().getBadge()),
					homeTeamIcon,
					new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.team_bage_default)
							.showImageForEmptyUri(R.drawable.team_bage_default)
							.showImageOnFail(R.drawable.team_bage_default)
							.build());

			homeTeamName.setText(matchInfoList.get(position).getHomeTeam()
					.getName());
			matchDate.setText(StringUtils.defaultIfEmpty(DateUtil
					.parseTimeInMillis_no_ss(matchInfoList.get(position)
							.getKickOff()), ""));
			matchField.setText(StringUtils.defaultIfEmpty(
					matchInfoList.get(position).getField(), ""));
			// matchDescription.setText(StringUtils.defaultIfEmpty(matchInfoList.get(position).getName(),""));
			LinearLayout llt_main = ViewHolder.get(convertView, R.id.llt_main);// 点击事件，代替点击listView子项
			llt_main.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					MatchInfo matchInfo = matchInfoList.get(position);

					switch (MatchStateEnum.getEnumFromString(matchInfo
							.getState())) {
					case INVITING:
						Intent intent = new Intent(mContext,
								MatchDetailOnInvitationStateActivity.class);
						intent.putExtra(
								MatchDetailOnInvitationStateActivity.INTENT_MATCH_DETAIL_BEAN,
								matchInfo);
						startActivity(intent);
						break;
					case CREATED:
						Intent intent2 = new Intent(mContext,
								MatchDetailOnCreatedStateActivity.class);
						intent2.putExtra(
								MatchDetailOnCreatedStateActivity.INTENT_MATCH_DETAIL_BEAN,
								matchInfo);
						startActivity(intent2);
						break;
					case PENDING:
						Intent intent3 = new Intent(mContext,
								MatchDetailOnPendingStateActivity.class);
						intent3.putExtra(
								MatchDetailOnPendingStateActivity.INTENT_MATCH_DETAIL_BEAN,
								matchInfo);
						startActivity(intent3);
						break;
					case PLAYING:
						Intent intent4 = new Intent(mContext,
								MatchDetailOnKickOffStateActivity.class);
						intent4.putExtra(
								MatchDetailOnKickOffStateActivity.INTENT_MATCH_DETAIL_BEAN,
								matchInfo);
						startActivity(intent4);
						break;
					case OVER:
						Intent intent5 = new Intent(mContext,
								MatchDetailOnOverStateActivity.class);
						intent5.putExtra(
								MatchDetailOnOverStateActivity.INTENT_MATCH_DETAIL_BEAN,
								matchInfo);
						startActivity(intent5);
						break;
					case FINISHED:
						Intent intent6 = new Intent(mContext,
								MatchDetailOnFinishedStateActivity.class);
						intent6.putExtra(
								MatchDetailOnFinishedStateActivity.INTENT_MATCH_DETAIL_BEAN,
								matchInfo);
						startActivity(intent6);
						break;
					case CANCELED:
						Intent intent7 = new Intent(mContext,
								MatchDetailOnCancleStateActivity.class);
						intent7.putExtra(
								MatchDetailOnCancleStateActivity.INTENT_MATCH_DETAIL_BEAN,
								matchInfo);
						startActivity(intent7);
						break;
					case NULL:
					default:
						break;
					}

				}
			});
			switch (MatchStateEnum.getEnumFromString(matchInfoList
					.get(position).getState())) {
			case INVITING:
				matchType.setText("创建中比赛");
				break;
			case CREATED:
				matchType.setText("报名中比赛");
				break;
			case PENDING:
				matchType.setText("等待比赛");
				break;
			case PLAYING:
				matchType.setText("进行的比赛");
				break;
			case OVER:
				matchType.setText("待评分的比赛");
				break;
			case FINISHED:
				matchType.setText("已结束的比赛");
				break;
			case CANCELED:
				matchType.setText("已取消的比赛");
				break;
			case NULL:
			default:
				break;
			}

			PictureUtil.getMd5PathByUrl(
					PhotoUtil.getThumbnailPath(matchInfoList.get(position)
							.getAwayTeam().getBadge()),
					awayTeamIcon,
					new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.team_bage_default)
							.showImageForEmptyUri(R.drawable.team_bage_default)
							.showImageOnFail(R.drawable.team_bage_default)
							.build());
			awayTeamName.setText(matchInfoList.get(position).getAwayTeam()
					.getName());

			return convertView;
		}
	}
	
}
