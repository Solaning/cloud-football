package com.kinth.football.ui.team;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.adapter.TeamMemberListAdapter;
import com.kinth.football.adapter.TeamMemberListAdapter.Callback_change_number;
import com.kinth.football.bean.TeamInfoComplete;
import com.kinth.football.bean.match.PlayerInTeam;
import com.kinth.football.dao.Player;
import com.kinth.football.dao.PlayerDao;
import com.kinth.football.dao.Team;
import com.kinth.football.dao.TeamPlayer;
import com.kinth.football.dao.TeamPlayerDao;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.view.xlist.XListView;
import com.kinth.football.view.xlist.XListView.IXListViewListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * 球队成员列表
 * @author Sola
 */
@ContentView(R.layout.activity_team_member_list_layout)
public class TeamMemberListActivity extends BaseActivity {
	public static final int TEAMMEMBER_NUMBER_CHANGE = 2001;
	public static final int TEAMMEMBER_NUMBER_CHANGE_ONLY_TEAM = 2002;
	public static final String TEAMINFOCOMPLETE_TEAMMEMBER_NUMBER_CHANGE = "TEAMINFOCOMPLETE_TEAMMEMBER_NUMBER_CHANGE";
	public static final String ONLY_TEAM_TEAMMEMBER_NUMBER_CHANGE = "TEAMINFOCOMPLETE_TEAMMEMBER_NUMBER_CHANGE_ONLY_TEAM";
	private TeamMemberListAdapter adapter;
	private Team onlyTeam;// 只包含球队的数据
	private List<PlayerInTeam> memberList_onlyTeam ;//--onlyTeam时的成员列表
	
	private TeamInfoComplete teamInfoComplete; // 完整数据
	private boolean isCompleteInfo = false;   // 是否完整数据
	private boolean isCaptain = false;        // 是否队长
	
	private boolean isNeedToRefresh = false;  //是否需要刷新
	
	@ViewInject(R.id.layout_all)
	private LinearLayout layout_all;
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;
	
	@ViewInject(R.id.nav_title)
	private TextView title;
	
	@ViewInject(R.id.nav_right_btn)
	private Button right;//邀请（队长）或者隐藏（非队长）
	
	@ViewInject(R.id.list_team_member)
	private XListView listview;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v){
		back();
	}
	
	@OnClick(R.id.nav_right_btn)
	public void fun_2(View v){//邀请球队成员
		if(!isCaptain){
			return;
		}
		if (isCompleteInfo) {
			Intent intent = new Intent(mContext, InviteMemberActivity.class);
			intent.putExtra(TeamInfoActivity.INTENT_TEAM_INFO_BEAN,
					teamInfoComplete.getTeam());
			startActivity(intent);
		} else {
			Intent intent = new Intent(mContext, InviteMemberActivity.class);
			intent.putExtra(TeamInfoActivity.INTENT_TEAM_INFO_BEAN, onlyTeam);
			startActivity(intent);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(layout_all, new BitmapDrawable(getResources(),
				background));
		
		teamInfoComplete = getIntent().getParcelableExtra(TeamInfoActivity.
				INTENT_TEAM_COMPLETE_INFO_BEAN);
		if (teamInfoComplete == null) {
			isCompleteInfo = false;
			onlyTeam = getIntent().getParcelableExtra(TeamInfoActivity.INTENT_TEAM_INFO_BEAN);
			if (onlyTeam == null) {// 加载默认的空白页面
				right.setEnabled(false);
				title.setText("球队成员");
				return;
			} else {
				title.setText("球队成员");
				isCaptain = isCaptain(onlyTeam);
				path4Team();
			}
		} else {
			isCompleteInfo = true;
			title.setText("球队成员");
			isCaptain = isCaptain(teamInfoComplete.getTeam());
			path4CompleteTeam();
		}
		if(isCaptain){
			right.setText("邀请");
		}else{
			right.setVisibility(View.GONE);
		}
	}

	private void path4CompleteTeam() {
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(false);
		adapter = new TeamMemberListAdapter(mContext, teamInfoComplete.getPlayers(),teamInfoComplete.getTeam(),isCaptain);
		//得到适配器的值
		adapter.change_number(new Callback_change_number() {

			@Override
			public void change_number(boolean isNeedToRefresh2,
					List<PlayerInTeam> memberList) {
				// TODO Auto-generated method stub
				isNeedToRefresh = isNeedToRefresh2;
				teamInfoComplete.setPlayers(memberList);
			}		
		});
		listview.setAdapter(adapter);
		
	}

	//需要联网拿数据
	private void path4Team() {
		adapter = new TeamMemberListAdapter(mContext, null,onlyTeam,isCaptain);
		//得到适配器的值
		adapter.change_number(new Callback_change_number() {

			@Override
			public void change_number(boolean isNeedToRefresh2,
					List<PlayerInTeam> memberList) {
				// TODO Auto-generated method stub
				isNeedToRefresh = isNeedToRefresh2;
				memberList_onlyTeam = memberList;
			}		
		});
		listview.setAdapter(adapter);
		listview.setPullLoadEnable(false);
		listview.setXListViewListener(new IXListViewListener(){
			@Override
			public void onRefresh() {
				executeGetTeamMemberList();
			}
			
			@Override
			public void onLoadMore() {
			}
		});
		new LoadTeamMebFromSqlAsyncTask().execute();//加载本地数据
		executeGetTeamMemberList();
	}

	/**
	 * 获取球队成员列表--只针对onlyTeam
	 */
	private void executeGetTeamMemberList(){
		NetWorkManager.getInstance(mContext).getTeamMember(onlyTeam.getUuid(), UserManager.getInstance(mContext).getCurrentUser().getToken(), 
				new Listener<JSONArray>() {
					@SuppressWarnings("unchecked")
					@Override
					public void onResponse(JSONArray response) {
						Gson gson = new Gson();
						List<PlayerInTeam> myTeamList = null;
						try {
							myTeamList = gson.fromJson(response.toString(),
									new TypeToken<ArrayList<PlayerInTeam>>() {
									}.getType());
						} catch (JsonSyntaxException e) {
							myTeamList = null;
							e.printStackTrace();
						}
						adapter.setMemberList(myTeamList);
						listview.stopRefresh();
						
						//将获取得到的球队成员列表数据放入到本地数据库中，缓存
						if(myTeamList != null && myTeamList.size() != 0){
							new WriteTeamMebToSqlAsyncTask().execute(myTeamList);
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						//ShowToast("获取球队成员失败："+error.getMessage());
						listview.stopRefresh();
						if(!NetWorkManager.getInstance(mContext).isNetConnected()){
							ShowToast("当前网络不可用");
						}else if(error.networkResponse == null){
							ShowToast("获取球队成员失败");
						}else if(error.networkResponse.statusCode == 401){
							ErrorCodeUtil.ErrorCode401(mContext);
						}else if(error.networkResponse.statusCode == 404){
							ShowToast("球队找不到");
						}
					}
				});
	}
	
	class WriteTeamMebToSqlAsyncTask extends AsyncTask<List<PlayerInTeam>, Void, Void>{
		
		@Override
		protected Void doInBackground(List<PlayerInTeam>... params) {
			// TODO Auto-generated method stub
			PlayerDao playerDao = CustomApplcation.getDaoSession(mContext).getPlayerDao();
			TeamPlayerDao tpDao = CustomApplcation.getDaoSession(mContext).getTeamPlayerDao();
			
			Query<TeamPlayer> tpQuery = CustomApplcation.getDaoSession(mContext).getTeamPlayerDao().queryBuilder()
					.where(TeamPlayerDao.Properties.Player_id.eq(""), TeamPlayerDao.Properties.Team_id.eq("")).build();
			
			//（1）、将球队成员数据放入到Player表中
			List<PlayerInTeam> teamMebList = params[0];
			for(PlayerInTeam teamRe : teamMebList){
				
				//去掉了数据库表Player的自增长ID之后，才能使用insertOrReplace方法
				playerDao.insertOrReplace(teamRe.getPlayer());
				
				//（2）、同时，在成员Player与球队Team关联表TeamPlayer表中，也要添加相对应的数据
				TeamPlayer team_player = new TeamPlayer();
				team_player.setPlayer_id(teamRe.getPlayer().getUuid());
				team_player.setTeam_id(onlyTeam.getUuid());
				team_player.setCreator(teamRe.isCreator());   //1创建者
				team_player.setType(teamRe.getType());
				
				tpQuery.setParameter(0, teamRe.getPlayer().getUuid());
				tpQuery.setParameter(1, onlyTeam.getUuid());
				TeamPlayer teamPlayerSearch = tpQuery.unique();
				if(teamPlayerSearch != null){
					team_player.setId(teamPlayerSearch.getId());//找到已有的teamplayer映射的id
					tpDao.insertOrReplace(team_player);
				}else{
					tpDao.insertOrReplace(team_player);
				}
			}
			return null;
		}
	}
	
	@Deprecated
	class LoadTeamMebFromSqlAsyncTask extends AsyncTask<Void, Void, List<Player>>{

		List<Player> playerList = new ArrayList<Player>();
		
		@Override
		protected  List<Player> doInBackground(Void... params) {
			QueryBuilder<TeamPlayer> tpQB = CustomApplcation.getDaoSession(mContext).getTeamPlayerDao().queryBuilder();
			tpQB.where(com.kinth.football.dao.TeamPlayerDao.Properties.Team_id.eq(onlyTeam.getUuid()));
			List<TeamPlayer> teamId_playerId_List = tpQB.list();
			
			if(teamId_playerId_List.size() != 0){
				for(TeamPlayer teamplayer : teamId_playerId_List){
					getPlayerData(teamplayer.getPlayer_id());
				}
			}
			return playerList;
		}
			
		private void getPlayerData(String playerID){
			QueryBuilder<Player> playerQB = CustomApplcation.getDaoSession(mContext).getPlayerDao().queryBuilder();
			playerQB.where(com.kinth.football.dao.PlayerDao.Properties.Uuid.eq(playerID));
			List<Player> list = playerQB.list();
			for(int i = 0; i < list.size(); i++){
				playerList.add(list.get(i));
			}
		}
		
		@Override
		protected void onPostExecute( List<Player> result) {
			super.onPostExecute(result);
			if(result.size() != 0 && result != null){
				List<PlayerInTeam> teamMebList = new ArrayList<PlayerInTeam>();
				
				for(Player item : result){
					PlayerInTeam response = new PlayerInTeam();
					response.setPlayer(item);
					
					teamMebList.add(response);
				}
				adapter.setMemberList(teamMebList);
			}
		}
	}
	
	/**
	 * 是否队长--包含一二三队长
	 * 
	 * @return
	 */
	private boolean isCaptain(Team team) {
		String uid = footBallUserManager.getCurrentUser().getPlayer().getUuid();
		if (uid.equals(team.getFirstCaptainUuid())
				|| uid.equals(team.getSecondCaptainUuid())
				|| uid.equals(team.getThirdCaptainUuid())) {
			return true;
		}
		return false;
	}

private void back(){
		if (isNeedToRefresh) {
			Intent data = new Intent();
			if (isCompleteInfo) {//将teamInfoComplete穿回去
				data.putExtra(TEAMINFOCOMPLETE_TEAMMEMBER_NUMBER_CHANGE,
						teamInfoComplete);
				setResult(RESULT_OK, data);
			} else {//把成员列表传回去
				data.putParcelableArrayListExtra(
						ONLY_TEAM_TEAMMEMBER_NUMBER_CHANGE,
						(ArrayList<? extends Parcelable>) memberList_onlyTeam);
                  
				setResult(RESULT_OK, data);
			}
		}
		finish();
}

@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
		back();
		return true;
	}
	return false;
}
	
}
