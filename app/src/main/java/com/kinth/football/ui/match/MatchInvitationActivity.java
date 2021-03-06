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
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.R;
import com.kinth.football.bean.MatchInfoResponse;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.config.MatchStateEnum;
import com.kinth.football.eventbus.bean.CreateFriendlyMatchEvent;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
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

import de.greenrobot.event.EventBus;

/**
 * 创建中的比赛列表
 * Sola
 */
@ContentView(R.layout.activity_match_list_no_score)
public class MatchInvitationActivity extends BaseActivity {
	public static final String INTENT_MATCH_INVITATION_INFO_LIST = "INTENT_MATCH_INVITATION_INFO_LIST";
	public static final int REQUEST_CODE_NEED_TO_REFRESH = 9001;//是否需要刷新比赛数据
	public static final int MATCH_COMFIRM = 100;
	public static final int MATCH_REFUSE = 101;//拒绝
	
	public static final String BROADCAST_CONFIRM_OF_REFUSE = "BROADCAST_CONFIRM_OF_REFUSE";//同意还是拒绝
	public static final String BROADCAST_MATCH_INFO_UUID = "BROADCAST_MATCH_INFO_UUID";//同意或拒绝的比赛id
	
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
	private MatchInvitationAdapter adapter;
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

		title.setText("创建中");

		matchInfoList = getIntent().getParcelableArrayListExtra(INTENT_MATCH_INVITATION_INFO_LIST);
		
		inflater = LayoutInflater.from(mContext);
		adapter = new MatchInvitationAdapter();
		listview.setDivider(null);
		listview.setDividerHeight(30);
		listview.setAdapter(adapter);
		listview.setPullLoadEnable(false);//关闭加载更多
		listview.setPullRefreshEnable(false);//关闭下拉刷新
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int position,
					long arg3) {
				MatchInfo matchInfo = adapter.getItem(position - 1);
				
				Intent intent = new Intent(mContext, MatchDetailOnInvitationStateActivity.class);
				intent.putExtra(MatchDetailOnInvitationStateActivity.INTENT_MATCH_DETAIL_BEAN, matchInfo);
				startActivityForResult(intent, REQUEST_CODE_NEED_TO_REFRESH);
			}
		});
//		readMatchListFromDB();
		//executeMatchList();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if(resultCode != RESULT_OK){
			return;
		}
		if (requestCode == REQUEST_CODE_NEED_TO_REFRESH) {//同意或者拒绝
			int confirmOrRefuse = intent.getIntExtra(BROADCAST_CONFIRM_OF_REFUSE, 0);
			String matchId = intent.getStringExtra(BROADCAST_MATCH_INFO_UUID);
			switch(confirmOrRefuse){
			case 0:
				break;
			case MATCH_COMFIRM://同意比赛--不用刷新页面，等报名的消息来
				MatchInfo matchGoal2 = null;
				for(MatchInfo match : matchInfoList){
					if(match.getUuid().equals(matchId)){
						matchGoal2 = match;
						break;
					}
				}
				if(matchGoal2 != null){
					matchInfoList.remove(matchGoal2);//从比赛列表中去掉该比赛
					adapter.notifyDataSetChanged();
				}
				break;
			case MATCH_REFUSE://拒绝比赛--，刷新上层页面
				MatchInfo matchGoal = null;
				for(MatchInfo match : matchInfoList){
					if(match.getUuid().equals(matchId)){
						matchGoal = match;
						break;
					}
				}
				if(matchGoal != null){
					matchInfoList.remove(matchGoal);//从比赛列表中去掉该比赛
					adapter.notifyDataSetChanged();
				}
				break;
			}
			EventBus.getDefault().post(new CreateFriendlyMatchEvent());
		}
	}


	/**
	 * 获取比赛列表
	 */
	private void executeMatchList(){
		listview.pullRefreshing();
		//查询创建中所有球队信息
		NetWorkManager.getInstance(mContext).getStateMatchList(UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid(), UserManager.getInstance(mContext).getCurrentUser().getToken(), 
				0, 100, MatchStateEnum.CHALLENGE_INVITING.getValue(), new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				listview.stopRefresh();
				Gson gson = new Gson();
				MatchInfoResponse matchInfoResponse = null;
				try {
					matchInfoResponse = gson.fromJson(response.toString(),
							new TypeToken<MatchInfoResponse>() {
							}.getType());
				} catch (JsonSyntaxException e) {
					matchInfoResponse = null;
					e.printStackTrace();
				}
				if(matchInfoResponse != null && matchInfoResponse.getMatchs()!= null && matchInfoResponse.getMatchs().size() != 0){
					matchInfoList = matchInfoResponse.getMatchs();
					adapter.notifyDataSetChanged();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				listview.stopRefresh();
				//ShowToast("获取球队列表失败");
				if(!NetWorkManager.getInstance(mContext).isNetConnected()) {
					ShowToast("没有可用的网络");
				}else if(error.networkResponse == null) {
//					ShowToast("MatchInvitation-executeMatchList-服务器连接错误");
				}else if(error.networkResponse.statusCode == 401){
					ErrorCodeUtil.ErrorCode401(mContext);
				}
			}
		} );
	}
	
	/**
	 * 异步加载数据库缓存数据
	 */
	class LoadCacheDataTask extends AsyncTask<Void, Void, List<MatchInfo>> {

		@Override
		protected List<MatchInfo> doInBackground(Void... arg0) {
			return PlayerMatchUtil.getMatchByCurrentUserId(mContext, 
					UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid(), 
					MatchStateEnum.INVITING.getValue(),
					"");
		}

		@Override
		protected void onPostExecute(List<MatchInfo> result) {
			super.onPostExecute(result);
			if(result == null || result.size() == 0){
				return;
			}
			matchInfoList = result;
			adapter.notifyDataSetChanged();
		}
	}

	class MatchInvitationAdapter extends BaseAdapter {
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
				convertView = inflater.inflate(R.layout.item_match_list_no_score, arg2, false);
			} 
			ImageView homeTeamIcon = ViewHolder.get(convertView, R.id.iv_home_team_icon);
			TextView homeTeamName = ViewHolder.get(convertView, R.id.tv_home_team_name);
			TextView matchDate = ViewHolder.get(convertView, R.id.tv_match_date);
			TextView matchField = ViewHolder.get(convertView, R.id.tv_match_field);
//			TextView matchDescription = ViewHolder.get(convertView, R.id.tv_match_description);
			ImageView awayTeamIcon = ViewHolder.get(convertView, R.id.iv_away_team_icon);
			TextView awayTeamName = ViewHolder.get(convertView, R.id.tv_away_team_name);
			
			homeTeamName.setText(matchInfoList.get(position).getHomeTeam().getName());
			awayTeamName.setText(matchInfoList.get(position).getAwayTeam().getName());
			matchDate.setText(StringUtils.defaultIfEmpty(DateUtil.parseTimeInMillis_no_ss(matchInfoList.get(position).getKickOff()),""));
			matchField.setText(StringUtils.defaultIfEmpty(matchInfoList.get(position).getField(),""));
//			matchDescription.setText(StringUtils.defaultIfEmpty(matchInfoList.get(position).getName(),""));
			
			PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(matchInfoList.get(position).getHomeTeam().getBadge()), 
					homeTeamIcon,
					new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.team_bage_default)
			.showImageForEmptyUri(R.drawable.team_bage_default)
			.showImageOnFail(R.drawable.team_bage_default).build());
			PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(matchInfoList.get(position).getAwayTeam().getBadge()), 
					awayTeamIcon,
					new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.team_bage_default)
			.showImageForEmptyUri(R.drawable.team_bage_default)
			.showImageOnFail(R.drawable.team_bage_default).build());
			
			return convertView;
		}
	}
	
	//从数据库读比赛信息
	private void readMatchListFromDB() {
		new LoadCacheDataTask().execute();
	}
	
}
