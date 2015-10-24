package com.kinth.football.ui.match;


import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.dao.Team;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.DateUtil;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.ViewHolder;
import com.kinth.football.view.xlist.XListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 报名中的比赛列表
 * Sola
 */
@ContentView(R.layout.activity_match_list_no_score)
public class MatchCreatedActivity extends BaseActivity {
//	public static final String INTENT_MATCH_CREATED_INFO_LIST = "INTENT_MATCH_CREATED_INFO_LIST";

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
//	private List<MatchInfo> matchInfoList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(entireLayout, new BitmapDrawable(getResources(),
				background));
		
		title.setText("报名中");
//		matchInfoList = getIntent().getParcelableArrayListExtra(INTENT_MATCH_CREATED_INFO_LIST);

		inflater = LayoutInflater.from(mContext);
		adapter = new MatchInvitationAdapter();
		listview.setDivider(null);
		listview.setDividerHeight(30);
		listview.setAdapter(adapter);
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(false);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int position,
					long arg3) {
				MatchInfo matchInfo = adapter.getItem(position - 1);
				Intent intent = new Intent(mContext, MatchDetailOnCreatedStateActivity.class);
				intent.putExtra(MatchDetailOnCreatedStateActivity.INTENT_MATCH_DETAIL_BEAN, matchInfo);
				startActivity(intent);
			}
		});
//		readMatchListFromDB();
//		executeMatchList();
	}

	
//	/**
//	 * 获取比赛列表
//	 */
//	private void executeMatchList(){
//		listview.pullRefreshing();
//		//查询报名中的球队信息
//		NetWorkManager.getInstance(mContext).getStateMatchList(UserManager.getInstance(mContext).getCurrentUser().getId(), UserManager.getInstance(mContext).getCurrentUser().getToken(), 
//				0, 100, MatchStateEnum.CREATED.getValue(), new Listener<JSONObject>() {//TODO
//			
//			@Override
//			public void onResponse(JSONObject response) {
//				listview.stopRefresh();
//				Gson gson = new Gson();
//				MatchInfoResponse matchInfoResponse = null;
//				try {
//					matchInfoResponse = gson.fromJson(response.toString(),
//							new TypeToken<MatchInfoResponse>() {
//							}.getType());
//				} catch (JsonSyntaxException e) {
//					matchInfoResponse = null;
//					e.printStackTrace();
//				}
//				matchInfoList = matchInfoResponse.getMatchs();
//				
//				if(matchInfoList != null || matchInfoList.size() == 0){
//					//ShowToast("没有报名中的比赛");
//					return;
//				}else{
//					adapter.notifyDataSetChanged();
//					DBUtil.saveMatchInfoListToDB(mContext, matchInfoList,null);
//				}
//			}
//		}, new ErrorListener() { 
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				// TODO Auto-generated method stub
//				listview.stopRefresh();
//				VolleyLog.e("Error: ", error.getMessage());
//				//ShowToast("获取球队列表失败");
//				if(!NetWorkManager.getInstance(mContext).isNetConnected()) {
//					ShowToast("没有可用的网络");
//				}else if(error.networkResponse == null) {
//					ShowToast("服务器连接错误");
//				}else if(error.networkResponse.statusCode == 401){
//					ErrorCodeUtil.ErrorCode401(mContext);
//				}
//			}
//		} );
//	}
	
	@Override
	protected void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
	}


	/**
	 * 异步加载数据库缓存数据
	 */
//	class LoadCacheDataTask extends AsyncTask<Void, Void, List<MatchInfo>> {
//
//		@Override
//		protected List<MatchInfo> doInBackground(Void... arg0) {
//			return PlayerMatchUtil.getMatchByCurrentUserId(mContext,
//					UserManager.getInstance(mContext).getCurrentUser().getId(), 
//					MatchStateEnum.CREATED.getValue(), "");
//		}
//
//		@Override
//		protected void onPostExecute(List<MatchInfo> result) {
//			super.onPostExecute(result);
//			matchInfoList = result;
//			adapter.notifyDataSetChanged();
//		}
//	}

	class MatchInvitationAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return CustomApplcation.getMatchInfoOfCreated() == null ? 0 : CustomApplcation.getMatchInfoOfCreated().size();
		}

		@Override
		public MatchInfo getItem(int position) {
			// TODO Auto-generated method stub
			return CustomApplcation.getMatchInfoOfCreated() == null ? null : CustomApplcation.getMatchInfoOfCreated().get(position);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
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
			
			Team homeTeam = CustomApplcation.getMatchInfoOfCreated().get(position).getHomeTeam();
			if(homeTeam!=null){
				homeTeamName.setText(homeTeam.getName());
				PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(homeTeam.getBadge()),
						homeTeamIcon,
						new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.team_bage_default)
							.showImageForEmptyUri(R.drawable.team_bage_default)
							.showImageOnFail(R.drawable.team_bage_default).build());
			}else{
				homeTeamName.setText("");
				ImageLoader.getInstance().displayImage("", homeTeamIcon, new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.team_bage_default)
				.showImageForEmptyUri(R.drawable.team_bage_default)
				.showImageOnFail(R.drawable.team_bage_default).build());
			}
			
			Team awayTeam = CustomApplcation.getMatchInfoOfCreated().get(position).getAwayTeam();
			if(awayTeam!=null){	
				awayTeamName.setText(awayTeam.getName());
				PictureUtil.getMd5PathByUrl(awayTeam.getBadge(), 
						awayTeamIcon,
						new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.team_bage_default)
							.showImageForEmptyUri(R.drawable.team_bage_default)
							.showImageOnFail(R.drawable.team_bage_default).build());
			}else{
				awayTeamName.setText("");
				ImageLoader.getInstance().displayImage("", awayTeamIcon, new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.team_bage_default)
				.showImageForEmptyUri(R.drawable.team_bage_default)
				.showImageOnFail(R.drawable.team_bage_default).build());
			}
			
			matchDate.setText(StringUtils.defaultIfEmpty(DateUtil.parseTimeInMillis_no_ss(CustomApplcation.getMatchInfoOfCreated().get(position).getKickOff()),""));
			matchField.setText(StringUtils.defaultIfEmpty(CustomApplcation.getMatchInfoOfCreated().get(position).getField(),""));
			
			return convertView;
		}
	}
	
//	//从数据库读比赛信息
//	private void readMatchListFromDB() {
//		new LoadCacheDataTask().execute();
//	}
	
}

