package com.kinth.football.ui.fragment;

import java.util.List;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.adapter.MyTeamListAdapter;
import com.kinth.football.bean.TeamInfoComplete;
import com.kinth.football.bean.message.ExitTeamPM;
import com.kinth.football.bean.message.InviteMemberConfirmPM;
import com.kinth.football.bean.message.InviteMemberPM;
import com.kinth.football.bean.message.MessageContent;
import com.kinth.football.bean.message.PushMessageAbstract;
import com.kinth.football.bean.message.RequestJoinTeamConfirmPM;
import com.kinth.football.bean.message.RequestJoinTeamPM;
import com.kinth.football.config.PushMessageEnum;
import com.kinth.football.dao.PushMessage;
import com.kinth.football.dao.PushMessageDao;
import com.kinth.football.dao.Team;
import com.kinth.football.eventbus.bean.ExitTeamEvent;
import com.kinth.football.eventbus.bean.MemberJoinTeamEvent;
import com.kinth.football.eventbus.bean.ModifyTeamAlternetJerseyEvent;
import com.kinth.football.eventbus.bean.ModifyTeamBadgeEvent;
import com.kinth.football.eventbus.bean.ModifyTeamCityEvent;
import com.kinth.football.eventbus.bean.ModifyTeamDescriptionEvent;
import com.kinth.football.eventbus.bean.ModifyTeamFamilyPhotoEvent;
import com.kinth.football.eventbus.bean.ModifyTeamFirstCaptainEvent;
import com.kinth.football.eventbus.bean.ModifyTeamHomeJerseyEvent;
import com.kinth.football.eventbus.bean.ModifyTeamIsLiked;
import com.kinth.football.eventbus.bean.ModifyTeamNameEvent;
import com.kinth.football.eventbus.bean.ModifyTeamRegionEvent;
import com.kinth.football.eventbus.bean.ModifyTeamRoadJerseyEvent;
import com.kinth.football.eventbus.bean.ModifyTeamSecondCaptainEvent;
import com.kinth.football.eventbus.bean.ModifyTeamSloganEvent;
import com.kinth.football.eventbus.bean.ModifyTeamThirdCaptainEvent;
import com.kinth.football.eventbus.bean.NotifyReCountTeamPushMessageEvent;
import com.kinth.football.eventbus.bean.TeamPushMessageEvent;
import com.kinth.football.eventbus.message.ExitTeamPushMessageEvent;
import com.kinth.football.eventbus.message.RequestJoinTeamConfirmPushMessageEvent;
import com.kinth.football.eventbus.message.RequestJoinTeamPushMessageEvent;
import com.kinth.football.eventbus.message.TeamPlayerInvitationConfirmPushMessageEvent;
import com.kinth.football.eventbus.message.TeamPlayerInvitationPushMessageEvent;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.ui.FragmentBase;
import com.kinth.football.ui.match.MessageCenterActivity;
import com.kinth.football.ui.team.CreateTeamActivity;
import com.kinth.football.ui.team.TeamInfoActivity;
import com.kinth.football.util.DBUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.PushMessageUtil;
import com.kinth.football.view.xlist.XListView;
import com.kinth.football.view.xlist.XListView.IXListViewListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import de.greenrobot.dao.query.CountQuery;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

/**
  * 我的球队
  * @author Sola
  */
@SuppressLint("ValidFragment")
public class TeamFragment extends FragmentBase {
	public static final int REQUEST_CODE_CREATE_TEAM = 9004;//创建球队的请求码
	public static final String RESULT_AFTER_TEAM_FOUNDED = "RESULT_AFTER_TEAM_FOUNDED";//球队创建后的返回结果
	public static final int REQUEST_CODE_VISIT_TEAM_INFO = 9005;//查看球队详情
	public static final String RESULT_NEED_TO_REFRESH = "RESULT_NEED_TO_REFRESH";
	
	private Context mContext;
	private MyTeamListAdapter adapter;
	
	public TeamFragment() {
		super();
	}
	
	@ViewInject(R.id.nav_right_image)
	private ImageView creatTeam;//创建球队
	
	@ViewInject(R.id.llt_push_message)
	private LinearLayout pushMessageLayout;//推送消息布局
	
	@ViewInject(R.id.nav_title)
	private TextView title;
	
	@ViewInject(R.id.list_my_team)
	private XListView xListView;
	
	@OnClick(R.id.nav_right_image)
	public void fun_1(View v){//创建球队
		Intent intent = new Intent(mContext, CreateTeamActivity.class);
		startActivityForResult(intent, REQUEST_CODE_CREATE_TEAM);
	}
	
	@Override
	public void onAttach(Activity activity) {
		EventBus.getDefault().register(this);//事件总线
		mContext = activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my_team, container, false);
		ViewUtils.inject(this, view);
		// 設置背景
//		Bitmap background = SingletonBitmapClass.getInstance()
//				.getBackgroundBitmap();
//		ViewCompat.setBackground(view, new BitmapDrawable(getResources(),
//				background));
		return view;
	}
	
	public static final TeamFragment newInstance(String key){
		TeamFragment fragment = new TeamFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Tag", key);
        fragment.setArguments(bundle);
        return fragment;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		title.setText(getResources().getString(R.string.main_tab_team));
		adapter = new MyTeamListAdapter(mContext, null);
		
		xListView.setDivider(null);
		xListView.setDividerHeight(30);
		
		xListView.setAdapter(adapter);
		xListView.setOnItemClickListener(new OnItemClickListener() {//单击某一项，进入球队详情
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TeamInfoComplete teamInfoComplete = adapter.getItem(position - 1);
				if(teamInfoComplete == null){
					return;
				}
				Intent intent = new Intent(mContext, TeamInfoActivity.class);
				intent.putExtra(TeamInfoActivity.INTENT_TEAM_COMPLETE_INFO_BEAN, teamInfoComplete);//球队完整消息
				intent.putExtra(TeamInfoActivity.INTENT_NEET_TO_RETURN_BEAN, true);//需要回传bean
				startActivityForResult(intent, REQUEST_CODE_VISIT_TEAM_INFO);
			}
		});
		
		xListView.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {//刷新
				executeGetMyTeamList();
			}
			
			@Override
			public void onLoadMore() {//加载更多
				
			}
		});
		
		//（1）加载本地数据库----我的球队-----数据
//		new LoadCacheDataTask().execute();
		
		//（2）再从服务器获取得到球队数据
		executeGetMyTeamList();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//(3)加载本地数据库相关球员邀请，确认信息等
		new CountTeamPushMessageTask().execute();
	}
	
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onDetach() {
		EventBus.getDefault().unregister(this);
		super.onDetach();
	}

	/**
	 * 成员加入球队的事件，需要刷新球队列表
	 */
	public void onEventMainThread(MemberJoinTeamEvent memberJoinTeamEvent){
		executeGetMyTeamListNoPullHead();
	}
	
	/**
	 * 修改球队名称的事件
	 */
	public void onEventMainThread(ModifyTeamNameEvent modifyTeamNameEvent){
		//TODO 是要重新加载数据还是仅仅只是更新页面数据？？
		new LoadCacheDataTask().execute();
	}
	
	/**
	 * 修改球队城市的事件
	 * @param modifyTeamCityEvent
	 */
	public void onEventMainThread(ModifyTeamCityEvent modifyTeamCityEvent){
		new LoadCacheDataTask().execute();//TODO
	}
	
	/**
	 * 修改球队地区的事件
	 * @param modifyTeamRegionEvent
	 */
	public void onEventMainThread(ModifyTeamRegionEvent modifyTeamRegionEvent){
		new LoadCacheDataTask().execute();//TODO
	}
	
	/**
	 * 修改球队介绍事件
	 * @param modifyTeamDescriptionEvent
	 */
	public void onEventMainThread(ModifyTeamDescriptionEvent modifyTeamDescriptionEvent){
		new LoadCacheDataTask().execute();//TODO
	}
	
	/**
	 * 修改球队口号事件
	 * @param modifyTeamSloganEvent
	 */
	public void onEventMainThread(ModifyTeamSloganEvent modifyTeamSloganEvent){
		new LoadCacheDataTask().execute();//TODO
	}
	
	/**
	 * 修改球队主场队服
	 * @param modifyTeamHomeJerseyEvent
	 */
	public void onEventMainThread(ModifyTeamHomeJerseyEvent modifyTeamHomeJerseyEvent){
		new LoadCacheDataTask().execute();//TODO
	}
	
	/**
	 * 修改球队客场队服
	 * @param modifyTeamRoadJerseyEvent
	 */
	public void onEventMainThread(ModifyTeamRoadJerseyEvent modifyTeamRoadJerseyEvent){
		new LoadCacheDataTask().execute();// TODO
	}
	
	/**
	 * 修改第三队服
	 * @param modifyTeamAlternetJerseyEvent
	 */
	public void onEventMainThread(ModifyTeamAlternetJerseyEvent modifyTeamAlternetJerseyEvent){
		new LoadCacheDataTask().execute();// TODO
	}
	
	/**
	 * 修改全家福
	 * @param modifyTeamFamilyPhotoEvent
	 */
	public void onEventMainThread(ModifyTeamFamilyPhotoEvent modifyTeamFamilyPhotoEvent){
		new LoadCacheDataTask().execute();// TODO
	}
	
	/**
	 * 修改队徽
	 */
	public void onEventMainThread(ModifyTeamBadgeEvent modifyTeamBadgeEvent){
		new LoadCacheDataTask().execute();// TODO
	}
	
	/**
	 * 修改第一队长
	 * @param modifyTeamFirstCaptainEvent
	 */
	public void onEventMainThread(ModifyTeamFirstCaptainEvent modifyTeamFirstCaptainEvent){
		new LoadCacheDataTask().execute();// TODO
	}
	
	/**
	 * 修改队副事件
	 * @param modifyTeamSecondCaptainEvent
	 */
	public void onEventMainThread(ModifyTeamSecondCaptainEvent modifyTeamSecondCaptainEvent){
		new LoadCacheDataTask().execute();// TODO
	}
	
	/**
	 * 修改教练事件
	 * @param modifyTeamThirdCaptainEvent
	 */
	public void onEventMainThread(ModifyTeamThirdCaptainEvent modifyTeamThirdCaptainEvent){
		new LoadCacheDataTask().execute();// TODO
	}
	
	/**
	 * 退出球队事件
	 * @param exitTeamEvent
	 */
	public void onEventMainThread(ExitTeamEvent exitTeamEvent){
		new LoadCacheDataTask().execute();//TODO
		executeGetMyTeamList();
	}
	
	/**
	 * 修改是否喜欢球队
	 * @param modifyTeamIsLiked
	 */
	public void onEventMainThread(ModifyTeamIsLiked modifyTeamIsLiked){
		List<TeamInfoComplete> newTeamList = adapter.getTeamList();
		for (int i = 0; i < newTeamList.size(); i++) {//遍历我的球队 ，Uuid相同的修改TeamInfoComplete
			if (modifyTeamIsLiked.getTeamUuid().equals(newTeamList.get(i).getTeam().getUuid())) {
				newTeamList.get(i).setLiked(modifyTeamIsLiked.getIsliked());
				newTeamList.get(i).setLike(modifyTeamIsLiked.getLike());
			}
		}
		adapter.setTeamList(newTeamList);
	}
	
	/**
	 * 异步加载数据库缓存数据
	 */
	class LoadCacheDataTask extends AsyncTask<Void, Void, List<TeamInfoComplete>> {

		@Override
		protected List<TeamInfoComplete> doInBackground(Void... arg0) {
			return DBUtil.getTeamInfoListFromDbById(mContext,UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid());
		}
		
		@Override
		protected void onPostExecute(List<TeamInfoComplete> myTeamList) {
			super.onPostExecute(myTeamList);
			if(myTeamList != null && myTeamList.size() != 0){
				adapter.setTeamList(myTeamList);
			}
		}
	}
	
	/**
	 * 联网获取我的球队列表
	 */
	private void executeGetMyTeamList(){
		xListView.pullRefreshing();
		//查询我所有球队信息
		NetWorkManager.getInstance(mContext).getMyTeamList(UserManager.getInstance(mContext).getCurrentUser().getToken(), new Listener<JSONArray>() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void onResponse(JSONArray response) {
				xListView.stopRefresh();
				xListView.stopLoadMore();
				Gson gson = new Gson();
				List<TeamInfoComplete> teamInfoCompleteList = null;
				try {
					teamInfoCompleteList = gson.fromJson(response.toString(),
							new TypeToken<List<TeamInfoComplete>>() {
							}.getType());
				} catch (JsonSyntaxException e) {
					teamInfoCompleteList = null;
					e.printStackTrace();
				}
				
				//将从服务器上获取得到的“我的球队”数据加入本地数据库，缓存
				if(teamInfoCompleteList != null && teamInfoCompleteList.size() != 0){
					for(TeamInfoComplete item : teamInfoCompleteList){//把点赞信息设置到球队
						if(item.getTeam() != null){
							item.getTeam().setLike(item.getLike());
							item.getTeam().setLiked(item.isLiked());
						}
					}
					new InsertMyTeamDataAsyTask().execute(teamInfoCompleteList);
					adapter.setTeamList(teamInfoCompleteList);
				}
			}
		}, new ErrorListener() { 
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				xListView.stopRefresh();
				xListView.stopLoadMore();
				VolleyLog.e("Error: ", error.getMessage());
				//ShowToast("获取球队列表失败");
				if(!NetWorkManager.getInstance(mContext).isNetConnected()) {
//					ShowToast("没有可用的网络");
				}else if(error.networkResponse == null) {
//					ShowToast("TeamFragment-executeGetMyTeamList-服务器连接错误");
				}else if(error.networkResponse.statusCode == 401){
					ErrorCodeUtil.ErrorCode401(mContext);
				}
			}
		} );
	}
	/**
	 * 联网获取我的球队列表--不会弹出刷新头
	 */
	private void executeGetMyTeamListNoPullHead(){
//		xListView.pullRefreshing();
		//查询我所有球队信息
		NetWorkManager.getInstance(mContext).getMyTeamList(UserManager.getInstance(mContext).getCurrentUser().getToken(), new Listener<JSONArray>() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void onResponse(JSONArray response) {
//				xListView.stopRefresh();
				Gson gson = new Gson();
				List<TeamInfoComplete> teamInfoCompleteList = null;
				try {
					teamInfoCompleteList = gson.fromJson(response.toString(),
							new TypeToken<List<TeamInfoComplete>>() {
							}.getType());
				} catch (JsonSyntaxException e) {
					teamInfoCompleteList = null;
					e.printStackTrace();
				}
				
				//将从服务器上获取得到的“我的球队”数据加入本地数据库，缓存
				if(teamInfoCompleteList != null && teamInfoCompleteList.size() != 0){
					for(TeamInfoComplete item : teamInfoCompleteList){//把点赞信息设置到球队
						if(item.getTeam() != null){
							item.getTeam().setLike(item.getLike());
							item.getTeam().setLiked(item.isLiked());
						}
					}
					new InsertMyTeamDataAsyTask().execute(teamInfoCompleteList);
					adapter.setTeamList(teamInfoCompleteList);
				}
			}
		}, new ErrorListener() { 
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
//				xListView.stopRefresh();
				VolleyLog.e("Error: ", error.getMessage());
				//ShowToast("获取球队列表失败");
				if(!NetWorkManager.getInstance(mContext).isNetConnected()) {
//					ShowToast("没有可用的网络");
				}else if(error.networkResponse == null) {
//					ShowToast("TeamFragment-executeGetMyTeamList-服务器连接错误");
				}else if(error.networkResponse.statusCode == 401){
					ErrorCodeUtil.ErrorCode401(mContext);
				}
			}
		} );
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if(resultCode != Activity.RESULT_OK){
			return;
		}
		if(requestCode == REQUEST_CODE_CREATE_TEAM){//创建球队后
			Team team = intent.getParcelableExtra(RESULT_AFTER_TEAM_FOUNDED);
			if(team != null){
				executeGetMyTeamList();
			}
			return;
		}
		if(requestCode == REQUEST_CODE_VISIT_TEAM_INFO){//查看详情后
			boolean needToRefresh = intent.getBooleanExtra(RESULT_NEED_TO_REFRESH, true);
			if(needToRefresh){
				TeamInfoComplete team = null;
				try{
					team = intent.getParcelableExtra(TeamInfoActivity.INTENT_TEAM_INFO_BEAN);
				}catch(Exception e){
					
				}
				if(team != null)
					adapter.updateTeamInfoComplete(team);
				executeGetMyTeamList();
			}
			return;
		}
	}
	
	/**
	 * 球队成员邀请消息事件
	 * @param TeamPlayerInvitationPushMessageEvent
	 */
	public void onEventMainThread(TeamPlayerInvitationPushMessageEvent TeamPlayerInvitationEvent){
		PushMessageAbstract<? extends MessageContent> pushMessage = PushMessageUtil.preHandlePushMessage(mContext, TeamPlayerInvitationEvent.getJson(), InviteMemberPM.class);
		if (pushMessage == null) {
			return;
		}
		new CountTeamPushMessageTask().execute();
	}
	
	/**
	 * 球队成员邀请确认消息事件
	 * @param teamPlayerInvitationConfirmEvent
	 */
	public void onEventMainThread(TeamPlayerInvitationConfirmPushMessageEvent teamPlayerInvitationConfirmEvent){
		PushMessageAbstract<? extends MessageContent> pushMessage = PushMessageUtil.preHandlePushMessage(mContext, teamPlayerInvitationConfirmEvent.getJson(), InviteMemberConfirmPM.class);
		if (pushMessage == null) {
			return;
		}
		executeGetMyTeamList();
		new CountTeamPushMessageTask().execute();
	}
	
	/**
	 * 申请加入球队消息事件
	 * @param requestJoinTeamEvent
	 */
	public void onEventMainThread(RequestJoinTeamPushMessageEvent requestJoinTeamEvent){
		PushMessageAbstract<? extends MessageContent> pushMessage = PushMessageUtil.preHandlePushMessage(mContext, requestJoinTeamEvent.getJson(), RequestJoinTeamPM.class);
		if (pushMessage == null) {
			return;
		}
		new CountTeamPushMessageTask().execute();
	}
	
	/**
	 * 申请加入球队确认消息事件
	 * @param requestJoinTeamConfirmEvent
	 */
	public void onEventMainThread(RequestJoinTeamConfirmPushMessageEvent requestJoinTeamConfirmEvent){
		PushMessageAbstract<? extends MessageContent> pushMessage = PushMessageUtil.preHandlePushMessage(mContext, requestJoinTeamConfirmEvent.getJson(), RequestJoinTeamConfirmPM.class);
		if (pushMessage == null) {
			return;
		}
		executeGetMyTeamListNoPullHead(); 
		new CountTeamPushMessageTask().execute();
	}
	
	/**
	 * 退出球队消息
	 * @param exitTeamPushMessageEvent
	 */
	public void onEventMainThread(ExitTeamPushMessageEvent exitTeamPushMessageEvent){
		PushMessageAbstract<? extends MessageContent> pushMessage = PushMessageUtil.preHandlePushMessage(mContext, exitTeamPushMessageEvent.getJson(), ExitTeamPM.class);
		if (pushMessage == null) {
			return;
		}
		executeGetMyTeamList();
		new CountTeamPushMessageTask().execute();
	}
	
	public void onEventMainThread(NotifyReCountTeamPushMessageEvent notifyReCountTeamPushMessageEvent){
		new CountTeamPushMessageTask().execute();
	}

	/**
	 * 异步将从服务器上获取得到的“我的球队”数据加入到本地数据库中
	 * @author Botision.Huang
	 * Date: 2015-3-26
	 * Descp:
	 */
	public class InsertMyTeamDataAsyTask extends AsyncTask<List<TeamInfoComplete>, Void, Void>{

		@Override
		protected Void doInBackground(List<TeamInfoComplete>... params) {
			DBUtil.saveTeamInfoListToDB(mContext, params[0]);
			return null;
		}
	}
	
	/**
	 * 异步统计数据库数据
	 */
	class CountTeamPushMessageTask extends AsyncTask<Void, Void, Long> {

		@Override
		protected Long doInBackground(Void... arg0) {
			QueryBuilder<PushMessage> qb1 = CustomApplcation.getDaoSession(mContext)
					.getPushMessageDao().queryBuilder();
			qb1.where(PushMessageDao.Properties.IsClick.eq(0),
						qb1.or(PushMessageDao.Properties.Type.eq(PushMessageEnum.TEAM_PLAYER_INVITATION.getValue()),
								PushMessageDao.Properties.Type.eq(PushMessageEnum.TEAM_PLAYER_INVITATION_CONFIRM.getValue()),
								PushMessageDao.Properties.Type.eq(PushMessageEnum.TEAM_PLAYER_APPLICATION.getValue()),
								PushMessageDao.Properties.Type.eq(PushMessageEnum.TEAM_PLAYER_APPLICATION_CONFIRM.getValue()),
								PushMessageDao.Properties.Type.eq(PushMessageEnum.TEAM_PLAYER_QUIT.getValue())));
			CountQuery<PushMessage> query = qb1.buildCount();
			long countTeamMessage = query.count();
			return countTeamMessage;//球队的消息统计
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			if(result != null){
				EventBus.getDefault().post(new TeamPushMessageEvent(result));
				if(result > 0){
					pushMessageLayout.setVisibility(View.VISIBLE);
					pushMessageLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							pushMessageLayout.setVisibility(View.GONE);
							Intent intent = new Intent(mContext,MessageCenterActivity.class);
							startActivity(intent);
						}
					});
				}else {
					pushMessageLayout.setVisibility(View.GONE);
				}
			}else{
				EventBus.getDefault().post(new TeamPushMessageEvent(0));
			}
		}
	}
}
