package com.kinth.football.chat.ui;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.adapter.MyTeamListAdapter;
import com.kinth.football.bean.TeamInfoComplete;
import com.kinth.football.bean.User;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.manager.UserManager;
import com.kinth.football.ui.ActivityBase;
import com.kinth.football.ui.match.invite.InviteMatchConstants;
import com.kinth.football.ui.team.RegionListActivity;
import com.kinth.football.ui.team.SearchTeamActivity;
import com.kinth.football.ui.team.formation.FormationConstants;
import com.kinth.football.ui.team.formation.FormationListActivity;
import com.kinth.football.util.DBUtil;
import com.kinth.football.util.QuitWay;
import com.kinth.football.view.ClearEditText;
import com.kinth.football.view.xlist.XListView;
import com.kinth.football.view.xlist.XListView.IXListViewListener;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 我的球队
 * 
 * @author Sola
 */
public class TeamActivity extends ActivityBase {

	private MyTeamListAdapter adapter;

	@ViewInject(R.id.llt_push_message)
	private LinearLayout pushMessageLayout;// 推送消息布局

	@ViewInject(R.id.tv_push_message_date)
	private TextView pushMessageDate;// 推送消息时间

	@ViewInject(R.id.tv_push_message)
	private TextView pushMessageContent;// 推送消息的内容

	@ViewInject(R.id.nav_title)
	private TextView title;

	@ViewInject(R.id.list_my_team)
	private XListView listview;

	private User targetUser; // 接受者User对象

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_team);
		initData();
		listview = (XListView) findViewById(R.id.list_my_team);
		adapter = new MyTeamListAdapter(TeamActivity.this, null);
		listview.setAdapter(adapter);
		listview.setDivider(null);
		listview.setDividerHeight(10);
		listview.setOnItemClickListener(new OnItemClickListener() {// 单击某一项，进入球队详情
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TeamInfoComplete teamInfoComplete = adapter
						.getItem(position - 1);
				if (teamInfoComplete == null) {
					return;
				}
				if (getIntent().getAction() != null) {
					if (getIntent().getAction().equals(
							InviteMatchConstants.INTENT_ACTION_PICK_TEAM)) { // 选择球队应战
						Intent intent = new Intent();
						intent.putExtra(InviteMatchConstants.INTENT_TEAM_BEAN,
								teamInfoComplete.getTeam());
						setResult(RESULT_OK, intent);
						finish();
					}
				}else {
					Intent intent = new Intent(TeamActivity.this,
							FormationListActivity.class);
					intent.putExtra(FormationConstants.INTENT_TEAM_UUID,
							teamInfoComplete.getTeam().getUuid());
					intent.putExtra(FormationConstants.INTENT_IS_CAPTAIN,
							isCaptain(teamInfoComplete));
					intent.putExtra(ChatConstants.INTENT_USER_BEAN,
							targetUser);
					startActivity(intent);
					QuitWay.activityList.add(TeamActivity.this);
				}
			}
		});
		listview.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {// 刷新
				new LoadCacheDataTask().execute();
			}

			@Override
			public void onLoadMore() {// 加载更多

			}
		});
		// （1）加载本地数据库球队数据
		new LoadCacheDataTask().execute();
	}

	/**
	 * 数据库找我的球队--我必须是队长
	 */
	class LoadCacheDataTask extends
			AsyncTask<Void, Void, List<TeamInfoComplete>> {

		@Override
		protected List<TeamInfoComplete> doInBackground(Void... arg0) {
			return DBUtil.getPrivilegeTeamInfoListFromDbById(mContext,
					UserManager.getInstance(mContext).getCurrentUser()
							.getPlayer().getUuid());
		}

		@Override
		protected void onPostExecute(List<TeamInfoComplete> myTeamList) {
			super.onPostExecute(myTeamList);
			if (myTeamList != null && myTeamList.size() != 0) {
				adapter.setTeamList(myTeamList);
			}
		}
	}

	private void initData() {
		targetUser = (User) getIntent().getParcelableExtra(
				ChatConstants.INTENT_USER_BEAN);
		initTopBarForLeft("请选择");
	}

	/**
	 * 是否队长
	 * 
	 * @param teamInfoComplete
	 * 
	 * @return
	 */
	private boolean isCaptain(TeamInfoComplete teamInfoComplete) {
		String uid = footBallUserManager.getCurrentUser().getPlayer().getUuid();
		if (uid.equals(teamInfoComplete.getTeam().getFirstCaptainUuid())) {
			return true;
		}
		return false;
	}

}
