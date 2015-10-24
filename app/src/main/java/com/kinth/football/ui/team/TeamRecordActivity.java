//package com.kinth.football.ui.team;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.json.JSONObject;
//
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.View.OnClickListener;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.Toast;
//
//import com.android.volley.Response.ErrorListener;
//import com.android.volley.Response.Listener;
//import com.android.volley.VolleyError;
//import com.android.volley.VolleyLog;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.kinth.football.R;
//import com.kinth.football.bean.MatchInfoResponse;
//import com.kinth.football.bean.match.MatchInfo;
//import com.kinth.football.config.MatchStateEnum;
//import com.kinth.football.dao.Team;
//import com.kinth.football.manager.UserManager;
//import com.kinth.football.network.NetWorkManager;
//import com.kinth.football.ui.BaseActivity;
//import com.kinth.football.util.DBUtil;
//import com.kinth.football.util.ErrorCodeUtil;
//import com.kinth.football.util.PhotoUtil;
//import com.kinth.football.util.TeamMatchUitl;
//import com.kinth.football.util.ViewHolder;
//import com.kinth.football.view.xlist.XListView;
//import com.kinth.football.view.xlist.XListView.IXListViewListener;
//import com.nostra13.universalimageloader.core.ImageLoader;
//
///**
// * 历史战绩 ----------即该球队过去参加完成的比赛列表
// * @author Botision.Huang
// * Date: 2015-4-2
// * Descp:
// */
//public class TeamRecordActivity extends BaseActivity {
//
//	private Team teamResponse;
//
//	public static int page = 1;  //第一页,记录已加载的当前页数
//	public static int pageSize = 10;
//	
//	private int totalPages;     //总页数
//	
//	private XListView listview;
//	private Adapter_match_all adapter;
//	private List<MatchInfo> matchInfoList = new ArrayList<MatchInfo>();
//	
//	private LayoutInflater inflater;
//	private ImageButton back;
//	private TextView title;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_match_all);
//		
//		teamResponse = getIntent().getExtras().getParcelable(TeamInfoActivity.INTENT_TEAM_INFO_BEAN);
//		
//		initView();
//		
//		//从本地数据库中获取该球队的历史战绩
//		new getTeamRecordGamesTask().execute(teamResponse.getUuid());
//		//从服务器获取
//		executeTeamRecordGames();
//	}
//	
//	private void initView(){
//		inflater = LayoutInflater.from(mContext);
//		
//		title = (TextView) findViewById(R.id.nav_title);
//		title.setText("历史战绩");
//		
//		back = (ImageButton) findViewById(R.id.nav_left);
//		back.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				finish();
//			}
//		});
//		
//		listview = (XListView) findViewById(R.id.list_match_all_message);
//		adapter = new Adapter_match_all();
//		listview.setAdapter(adapter);
//		listview.setPullLoadEnable(true);
//		listview.setXListViewListener(new IXListViewListener() {
//			@Override
//			public void onRefresh() {     // 刷新
//			
//			}
//			
//			@Override
//			public void onLoadMore() {  // 加载更多
//				
//			}
//		});
//		
//		listview.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1,
//					final int position, long arg3) {
//				MatchInfo matchInfo = adapter.getItem(position - 1);
//				Toast.makeText(mContext, "点击", Toast.LENGTH_SHORT).show();
//			}
//		});
//	}
//	
//	private void executeTeamRecordGames(){
//		listview.pullRefreshing();
//		NetWorkManager.getInstance(mContext).getTeamRecordGames(teamResponse.getUuid(), 
//				page, pageSize, MatchStateEnum.FINISHED.getValue(), null,
//				UserManager.getInstance(mContext).getCurrentUser().getToken(),
//				new Listener<JSONObject>(){
//					@Override
//					public void onResponse(JSONObject response) {
//						// TODO Auto-generated method stub
//						System.out.println("球队历史战绩列表====response" + response.toString());
//						MatchInfoResponse infoResponse = null;
//						Gson gson = new Gson();
//						infoResponse = gson.fromJson(response.toString(),
//								new TypeToken<MatchInfoResponse>(){}.getType());
//						if(infoResponse != null && infoResponse.getMatchs() != null 
//								&& infoResponse.getMatchs().size() != 0){
//							matchInfoList = infoResponse.getMatchs();
//							
//							adapter.notifyDataSetChanged();
//							DBUtil.saveMatchInfoListToDB(mContext,matchInfoList,null);
//						}
//					}
//				}, 
//				new ErrorListener(){
//					@Override
//					public void onErrorResponse(VolleyError error) {
//						// TODO Auto-generated method stub
//						listview.stopRefresh();
//						listview.stopLoadMore();
//						
//						VolleyLog.e("MatchAll中的Error: ", error.getMessage());
//
//						if (!NetWorkManager.getInstance(mContext)
//								.isNetConnected()) {
//							ShowToast("没有可用的网络");
//						} else if (error.networkResponse == null) {
//							ShowToast("TeamRecordActivity-executeTeamRecordGames-服务器连接错误");
//						} else if (error.networkResponse.statusCode == 401) {
//							ErrorCodeUtil.ErrorCode401(mContext);
//						}else if(error.networkResponse.statusCode == 404){
//							ShowToast("球员找不到");
//						}
//					}
//				});
//	}
//	
//	class Adapter_match_all extends BaseAdapter {
//
//		@Override
//		public int getCount() {
//			// TODO Auto-generated method stub
//			return matchInfoList == null ? 0 : matchInfoList.size();
//		}
//
//		@Override
//		public MatchInfo getItem(int position) {
//			// TODO Auto-generated method stub
//			return matchInfoList == null ? null : matchInfoList.get(position);
//		}
//
//		@Override
//		public long getItemId(int arg0) {
//			// TODO Auto-generated method stub
//			return arg0;
//		}
//
//		@Override
//		public View getView(final int position, View convertView, ViewGroup arg2) {
//			if (convertView == null) {
//				convertView = inflater.inflate(R.layout.item_match_list_no_score, null);
//			}
//			ImageView homeTeamIcon = ViewHolder.get(convertView,
//					R.id.iv_home_team_icon);
//			TextView homeTeamName = ViewHolder.get(convertView,
//					R.id.tv_home_team_name);
//			TextView date = ViewHolder.get(convertView, R.id.tv_match_date);
//			ImageView awayTeamIcon = ViewHolder.get(convertView,
//					R.id.iv_away_team_icon);
//			TextView awayTeamName = ViewHolder.get(convertView,
//					R.id.tv_away_team_name);
//
//			if (matchInfoList.get(position).getHomeTeam().getBadge() != null) {
//				ImageLoader.getInstance().displayImage(
//						PhotoUtil.getAllPhotoPath(matchInfoList.get(position)
//								.getHomeTeam().getBadge()), homeTeamIcon);
//			}
//			homeTeamName.setText(matchInfoList.get(position).getHomeTeam()
//					.getName());
//			// date.setText("VS");//TODO
//			if (matchInfoList.get(position).getState()
//					.endsWith(MatchStateEnum.INVITING.getValue())) {
//				date.setText("创建中比赛");
//			} else if (matchInfoList.get(position).getState()
//					.endsWith(MatchStateEnum.CREATED.getValue())) {
//				date.setText("报名中比赛");
//			} else if (matchInfoList.get(position).getState()
//					.endsWith(MatchStateEnum.PENDING.getValue())) {
//				date.setText("等待比赛");
//			} else if (matchInfoList.get(position).getState()
//					.endsWith(MatchStateEnum.PLAYING.getValue())) {
//				date.setText("进行的比赛");
//			} else if (matchInfoList.get(position).getState()
//					.endsWith(MatchStateEnum.OVER.getValue())) {
//				date.setText("待评分的比赛");
//			} else if (matchInfoList.get(position).getState()
//					.endsWith(MatchStateEnum.FINISHED.getValue())) {
//				date.setText("已结束的比赛");
//			} else if (matchInfoList.get(position).getState()
//					.endsWith(MatchStateEnum.CANCELED.getValue())) {
//				date.setText("已取消的比赛");
//			}
//			// date.setText("创建中比赛");
//			if (matchInfoList.get(position).getAwayTeam() != null) {
//				ImageLoader.getInstance().displayImage(
//						PhotoUtil.getAllPhotoPath(matchInfoList.get(position)
//								.getAwayTeam().getBadge()), awayTeamIcon);
//			}
//			awayTeamName.setText(matchInfoList.get(position).getAwayTeam()
//					.getName());
//
//			return convertView;
//		}
//	}
//
//	private class getTeamRecordGamesTask extends AsyncTask<String, Void, List<MatchInfo>>{
//
//		@Override
//		protected List<MatchInfo> doInBackground(String... params) {
//			// TODO Auto-generated method stub
//			String teamUid = params[0];
//			
//			return TeamMatchUitl.getTeamMatchList(mContext, teamUid, MatchStateEnum.FINISHED.getValue(),"");
//		}
//
//		@Override
//		protected void onPostExecute(List<MatchInfo> result) {
//			// TODO Auto-generated method stub
//			super.onPostExecute(result);
//			matchInfoList = result;
//			adapter.notifyDataSetChanged();
//		}
//		
//		
//		
//	}
//}
