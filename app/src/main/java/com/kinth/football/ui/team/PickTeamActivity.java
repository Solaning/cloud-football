package com.kinth.football.ui.team;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.kinth.football.R;
import com.kinth.football.adapter.MyTeamListAdapter;
import com.kinth.football.bean.TeamInfoComplete;
import com.kinth.football.config.JConstants;
import com.kinth.football.dao.Team;
import com.kinth.football.manager.UserManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.DBUtil;
import com.kinth.football.util.UtilFunc;
import com.kinth.football.view.xlist.XListView;
import com.kinth.football.view.xlist.XListView.IXListViewListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 挑选某一个球队，然后把该球队的信息传给下一个Activity
 * @author Sola
 */
@ContentView(R.layout.activity_pick_team)
public class PickTeamActivity extends BaseActivity{
	private Bundle bundle = null;
	private String targetClassName; //需要跳转的Activity名称
	private String sourceClassName; //跳转过来的Activity名称，用于返回时跳转
	
	private MyTeamListAdapter adapter;
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;
	
	@ViewInject(R.id.nav_title)
	private TextView title;
	
	@ViewInject(R.id.list_my_team)
	private XListView teamList;
	
	@ViewInject(R.id.pick_lin)
	private LinearLayout pick_lin;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		back();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(pick_lin, new BitmapDrawable(getResources(),
				background));
		
		bundle = getIntent().getExtras();
		targetClassName = bundle.getString(JConstants.INTENT_TARGET_CLASS_NAME);//目标activity
		sourceClassName = bundle.getString(JConstants.INTENT_SOURCE_CLASS_NAME);//来源activity
		
		title.setText("选择球队");
		
		adapter = new MyTeamListAdapter(mContext, null);
		teamList.setAdapter(adapter);
		teamList.setDividerHeight(30);
		teamList.setPullRefreshEnable(false);
		teamList.setPullLoadEnable(false);
		teamList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TeamInfoComplete teamInfoComplete = adapter.getItem(position - 1);
				if(teamInfoComplete == null){
					return;
				}
				Class<?> clazz = UtilFunc.loadClass(mContext, targetClassName);
				if(clazz != null){
					Intent intent = new Intent(mContext,clazz);
					bundle.putParcelable(TeamInfoActivity.INTENT_TEAM_INFO_BEAN, teamInfoComplete.getTeam());//加入所选球队的信息
					intent.putExtras(bundle);
					startActivity(intent);
				}
				finish();
			}
		});
		teamList.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {//刷新
				new LoadCacheDataTask().execute();
			}
			
			@Override
			public void onLoadMore() {//加载更多
				
			}
		});
		
		new LoadCacheDataTask().execute();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			back();
		}
		return false;
	}
	
	/**
	 * 返回
	 */
	private void back(){
		Class<?> clazz = UtilFunc.loadClass(mContext, sourceClassName);
		if(clazz != null){
			Intent intent = new Intent(mContext, clazz);
			intent.putExtras(bundle);
			startActivity(intent);
		}
		finish();
	}
	
	/**
	 * 数据库找我的球队--我必须是队长
	 */
	class LoadCacheDataTask extends AsyncTask<Void, Void, List<TeamInfoComplete>> {
		
		@Override
		protected List<TeamInfoComplete> doInBackground(Void... arg0) {
			return DBUtil.getPrivilegeTeamInfoListFromDbById(mContext,UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid());
		}
		
		@Override
		protected void onPostExecute(List<TeamInfoComplete> myTeamList) {
			super.onPostExecute(myTeamList);
			if(myTeamList != null && myTeamList.size() != 0){
				adapter.setTeamList(myTeamList);
			}
		}
	}
	
}
