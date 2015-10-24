package com.kinth.football.ui.fragment;

import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.CustomApplcation;
import com.kinth.football.MyMessageReceiver;
import com.kinth.football.R;
import com.kinth.football.bean.MatchInfoResponse;
import com.kinth.football.bean.TeamInfoComplete;
import com.kinth.football.bean.User;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.bean.message.MatchCancledPM;
import com.kinth.football.bean.message.MatchChallengePM;
import com.kinth.football.bean.message.MatchCreatedPM;
import com.kinth.football.bean.message.MatchFinishedPM;
import com.kinth.football.bean.message.MatchInvitationConfirmPM;
import com.kinth.football.bean.message.MatchInvitationPM;
import com.kinth.football.bean.message.MatchKickOffNotificationPM;
import com.kinth.football.bean.message.MatchKickOffPM;
import com.kinth.football.bean.message.MatchOverPM;
import com.kinth.football.bean.message.MatchPendingPM;
import com.kinth.football.bean.message.MatchSignupPM;
import com.kinth.football.bean.message.MessageContent;
import com.kinth.football.bean.message.PushMessageAbstract;
import com.kinth.football.chat.MyChatManager;
import com.kinth.football.chat.bean.ChatMsg;
import com.kinth.football.config.Action;
import com.kinth.football.config.MatchStateEnum;
import com.kinth.football.config.PushMessageEnum;
import com.kinth.football.config.UrlConstants;
import com.kinth.football.dao.Match;
import com.kinth.football.dao.MatchDao;
import com.kinth.football.dao.Team;
import com.kinth.football.eventbus.bean.CreateFriendlyMatchEvent;
import com.kinth.football.eventbus.bean.ExitTeamEvent;
import com.kinth.football.eventbus.bean.ModifyTeamAlternetJerseyEvent;
import com.kinth.football.eventbus.bean.ModifyTeamHomeJerseyEvent;
import com.kinth.football.eventbus.bean.ModifyTeamRoadJerseyEvent;
import com.kinth.football.eventbus.match.MatchFinishedEvent;
import com.kinth.football.listener.EventListener;
import com.kinth.football.listener.PushMessageListener;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.ui.CommonWebViewActivity;
import com.kinth.football.ui.FragmentBase;
import com.kinth.football.ui.MainActivity;
import com.kinth.football.ui.match.MatchCreatedActivity;
import com.kinth.football.ui.match.MatchInvitationActivity;
import com.kinth.football.ui.match.MatchKickOffActivity;
import com.kinth.football.ui.match.MatchOverActivity;
import com.kinth.football.ui.match.MatchPendingActivity;
import com.kinth.football.ui.match.TournamentActivity;
import com.kinth.football.ui.match.invite.InviteMatchListActivity;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.PhotoUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

/**
 * 比赛管理--默认加载所有的比赛数据
 * 
 * @author Sola
 */
@SuppressLint("ValidFragment")
public class MatchFragment extends FragmentBase implements PushMessageListener {
	private Context mContext;
//	private static final int REQUEST_CODE_CREATE_MATCH = 100;// 选择对手请求码

	public static final String INTENT_ALL_MATCH_DATA = "INTENT_ALL_MATCH_DATA";// 跳转到“所有比赛”intent中传递的参数
	public static final String INTENT_FRIEND_MATCH_DATA = "INTENT_FRIEND_MATCH_DATA";// 跳转到“友谊赛”intent中传递的参数
	public static final String INTENT_LEAGUE_MATCH_DATA = "INTENT_LEAGUE_MATCH_DATA";// 跳转到“锦标赛”intent中传递的参数
	private long countMatchInviting = 0;
	private long countMatchCreated = 0;
	private long countMatchPending = 0;
	private long countMatchKickOff = 0;
	private long countMatchOver = 0; // 对应比赛type数据库的数据数目
	private ArrayList<MatchInfo> matchInfoOfInviting = new ArrayList<MatchInfo>();
	// private ArrayList<MatchInfo> matchInfoOfCreated;//报名中的列表提到全局变量
	// private ArrayList<MatchInfo> matchInfoOfPending = new ArrayList<MatchInfo>();//待开赛列表提到全局变量
	private ArrayList<MatchInfo> matchInfoOfPlaying = new ArrayList<MatchInfo>();
	private ArrayList<MatchInfo> matchInfoOfOver = new ArrayList<MatchInfo>();

	private MyBroadcastReceiver receiver = new MyBroadcastReceiver();

	public class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Action.ACTION_BROADCAST_MATCH_COMFIRM_OR_REFUSE.equals(action)) {
				getMatchInfo(MatchStateEnum.CHALLENGE_INVITING.getValue());
				return;
			}
		}
	}
	
	public MatchFragment() {
		super();
		// TODO 自动生成的构造函数存根
	}

	@ViewInject(R.id.nav_title)
	private TextView title;
	
	@ViewInject(R.id.nav_right_text)
	private TextView ruleIntroduction;//规则说明

	@ViewInject(R.id.btn_match_invitation)
	private RelativeLayout matchInvitation;// 创建中

	@ViewInject(R.id.rel_inviting_count)
	private RelativeLayout rel_inviting_count; // 创建中圆圈

	@ViewInject(R.id.inviting_count)
	private TextView inviting_count;	// 创建中圆圈中数字

	@ViewInject(R.id.btn_match_signup)
	private RelativeLayout matchSignUp; // 报名中

	@ViewInject(R.id.rel_created_count)
	private RelativeLayout rel_created_count;	// 报名中圆圈

	@ViewInject(R.id.created_count)
	private TextView created_count; // 报名中圆圈中数字

	@ViewInject(R.id.btn_match_wait)
	private RelativeLayout matchWait;// 待开赛，报名完成

	@ViewInject(R.id.rel_pedding_count)
	private RelativeLayout rel_pedding_count;// 待开赛圆圈

	@ViewInject(R.id.pedding_count)
	private TextView pedding_count;	// 待开赛圆圈中数字

	@ViewInject(R.id.btn_match_kick_off)
	private RelativeLayout matchKickOff;// 比赛中 rel_playing_count

	@ViewInject(R.id.rel_playing_count)
	private RelativeLayout rel_playing_count;// 比赛中圆圈

	@ViewInject(R.id.playing_count)
	private TextView playing_count;// 比赛中圆圈数字

	@ViewInject(R.id.btn_match_over)
	private RelativeLayout matchFinish;// 比赛结束，待评价

	@ViewInject(R.id.rel_over_count)
	private RelativeLayout rel_over_count;// 比赛结束，待评价圆圈

	@ViewInject(R.id.over_count)
	private TextView over_count;// 比赛结束，待评价圆圈中数字

	@ViewInject(R.id.inviting)
	private TextView inviting;

	@ViewInject(R.id.created)
	private TextView created;

	@ViewInject(R.id.pedding)
	private TextView pedding;

	@ViewInject(R.id.playing)
	private TextView playing;

	@ViewInject(R.id.over)
	private TextView over;

	@ViewInject(R.id.all_the_games)
	// 所有比赛
	private ImageView all_the_games;

	@ViewInject(R.id.friendly_play)// 友谊赛
	private ImageView friendly_play;

	@OnClick(R.id.nav_right_text)
	public void fun_0(View v){//比赛规则
		Intent intent = new Intent(mContext, CommonWebViewActivity.class);
		intent.putExtra(CommonWebViewActivity.INTENT_TITLE, "比赛规则");
		intent.putExtra(CommonWebViewActivity.INTENT_URL, "http://www.cloudfootball.com.cn/match/rules");
		startActivity(intent);
	}
	
	@OnClick(R.id.btn_match_invitation)
	public void fun_1(View v) {// 创建中
		Intent intent = new Intent(mContext, MatchInvitationActivity.class);
		intent.putParcelableArrayListExtra(
				MatchInvitationActivity.INTENT_MATCH_INVITATION_INFO_LIST,
				matchInfoOfInviting);
		startActivity(intent);
	}

	@OnClick(R.id.btn_match_signup)
	public void fun_2(View v) {// 报名中
		Intent intent = new Intent(mContext, MatchCreatedActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.btn_match_wait)
	public void fun_3(View v) {// 待开赛
		Intent intent = new Intent(mContext, MatchPendingActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.btn_match_kick_off)
	public void fun_4(View v) {// 比赛中--
		Intent intent = new Intent(mContext, MatchKickOffActivity.class);
		intent.putParcelableArrayListExtra(
				MatchKickOffActivity.INTENT_MATCH_KICK_OFF_INFO_LIST,
				matchInfoOfPlaying);
		startActivity(intent);
	}

	@OnClick(R.id.btn_match_over)
	public void fun_5(View v) {// 待评价
		Intent intent = new Intent(mContext, MatchOverActivity.class);
		intent.putParcelableArrayListExtra(
				MatchOverActivity.INTENT_MATCH_OVER_INFO_LIST, matchInfoOfOver);
		startActivity(intent);
	}

	@OnClick(R.id.all_the_games)
	public void fun_7(View v) {  // 所有比赛---锦标赛
//		Intent intent = new Intent(mContext, AllMatchListActivity.class);
//		intent.putExtra(INTENT_ALL_MATCH_DATA, UserManager
//				.getInstance(mContext).getCurrentUser().getPlayer().getUuid());
//		startActivity(intent);
		Intent intent = new Intent(mContext, TournamentActivity.class);
		intent.putExtra(TournamentActivity.LINK_IN_WHERE, TournamentActivity.LINK_IN_MATCH);
		startActivity(intent);
	}

	@OnClick(R.id.friendly_play)
	public void fun_13(View v) {   // 友谊赛
		/*Intent intent = new Intent(mContext, FriendlyListActivity.class);
		intent.putExtra(INTENT_FRIEND_MATCH_DATA,
				UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid());
		startActivity(intent);*///TODO
		Intent intent = new Intent(mContext, InviteMatchListActivity.class);
		startActivity(intent);
	}

	@Override
	public void onAttach(Activity activity) {
		mContext = activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		View view = inflater.inflate(R.layout.fragment_match_layout, container,
				false);
		ViewUtils.inject(this, view);
		
		int screenWidth = CustomApplcation.getInstance().getParameters().getmScreenWidth();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth,
				Math.round(screenWidth / 2.25f));//图片的宽高比例
		params.gravity = Gravity.CENTER;
		
		all_the_games.setLayoutParams(params);
		all_the_games.setImageBitmap(PhotoUtil.getDecodeBitmap(mContext, R.drawable.tournament2));
		
		friendly_play.setLayoutParams(params);
		friendly_play.setImageBitmap(PhotoUtil.getDecodeBitmap(mContext, R.drawable.friendlyplay));
		
		title.setText("比赛");
		return view;
	}
	
	public static final MatchFragment newInstance(String key){
		MatchFragment fragment = new MatchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Tag", key);
        fragment.setArguments(bundle);
        return fragment;
	}

	/**
	 * 异步统计数据库数据
	 */
	// class LoadCacheDataTask extends AsyncTask<Void, Void, Boolean> {
	//
	// @Override
	// protected Boolean doInBackground(Void... arg0) {
	// //
	// QueryBuilder<Match> qb1 = CustomApplcation.getDaoSession(mContext)
	// .getMatchDao().queryBuilder();
	// qb1.where(MatchDao.Properties.State.eq(MatchStateEnum.INVITING
	// .getValue()));
	// CountQuery<Match> query = qb1.buildCount();
	// countMatchInviting = query.count();
	// //
	// query.setParameter(0, MatchStateEnum.CREATED.getValue());
	// countMatchCreated = query.count();
	// //
	// query.setParameter(0, MatchStateEnum.PENDING.getValue());
	// countMatchPending = query.count();
	// //
	// query.setParameter(0, MatchStateEnum.PLAYING.getValue());
	// countMatchKickOff = query.count();
	// //
	// query.setParameter(0, MatchStateEnum.OVER.getValue());
	// countMatchOver = query.count();
	// return true;
	// }
	//
	// @Override
	// protected void onPostExecute(Boolean result) {
	// super.onPostExecute(result);
	// if (countMatchInviting > 0) {
	// rel_inviting_count.setVisibility(View.VISIBLE);
	// inviting_count.setText("" + countMatchInviting);
	// }else{
	// rel_inviting_count.setVisibility(View.GONE);
	// }
	// if (countMatchCreated > 0) {
	// rel_created_count.setVisibility(View.VISIBLE);
	// created_count.setText("" + countMatchCreated);
	// }else{
	// rel_created_count.setVisibility(View.GONE);
	// }
	// if (countMatchPending > 0) {
	// rel_pedding_count.setVisibility(View.VISIBLE);
	// pedding_count.setText("" + countMatchPending);
	// }else{
	// rel_pedding_count.setVisibility(View.GONE);
	// }
	// if (countMatchKickOff > 0) {
	// rel_playing_count.setVisibility(View.VISIBLE);
	// playing_count.setText("" + countMatchKickOff);
	// }else{
	// rel_playing_count.setVisibility(View.GONE);
	// }
	// if (countMatchOver > 0) {
	// rel_over_count.setVisibility(View.VISIBLE);
	// over_count.setText("" + countMatchOver);
	// }else{
	// rel_over_count.setVisibility(View.GONE);
	// }
	// if(tipsListener != null){
	// long num = countMatchInviting + countMatchCreated + countMatchPending +
	// countMatchKickOff + countMatchOver;
	// tipsListener.setMatchTips(num);
	// }
	// }
	// }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 加载所有比赛数据
		executeGetFiveStateList();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Action.ACTION_BROADCAST_MATCH_COMFIRM_OR_REFUSE);// 建议把它写一个公共的变量，这里方便阅读就不写了。
		mContext.registerReceiver(receiver, intentFilter);
	}

	/**
	 * 刷5个状态的比赛数据，包含裁判的
	 */
	private void executeGetFiveStateList() {
		getMatchInfo(MatchStateEnum.CHALLENGE_INVITING.getValue());
		getMatchInfo(MatchStateEnum.CREATED.getValue());
		getMatchInfo(MatchStateEnum.PENDING.getValue());
		getMatchInfo(MatchStateEnum.PLAYING.getValue());
		getMatchInfo(MatchStateEnum.OVER.getValue());
	}

	public void getMatchInfo(final String state) {
		User user = UserManager.getInstance(mContext).getCurrentUser();
		if(user == null || TextUtils.isEmpty(user.getToken())){
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.URL_PLAYER_RELATED).append(user.getPlayer().getUuid()).append("/matchs.json?page=").append(0).append("&pageSize=").append(1000);
		if(!TextUtils.isEmpty(state)){
			sb.append("&state=").append(state);
		}
		sb.append("&referee=true");
		RequestParams params = new RequestParams();
		params.addHeader("Authorization", "Bearer " + user.getToken());
		params.addHeader("Content-Type", "application/json");
		HttpUtils http = new HttpUtils();
		http.configDefaultHttpCacheExpiry(0);//不缓存该请求结果
		http.send(HttpMethod.GET,
				sb.toString(), params,
		    new RequestCallBack<String>(){

		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
					Gson gson = new Gson();
					MatchInfoResponse matchInfoResponse = null;
					try {
						matchInfoResponse = gson.fromJson(
								responseInfo.result,
								new TypeToken<MatchInfoResponse>() {
								}.getType());
//						Log.e("state :" + state, responseInfo.result);
					} catch (JsonSyntaxException e) {
						matchInfoResponse = null;
						e.printStackTrace();
					}

					// 将从服务器上获取得到的“我的球队”数据加入本地数据库，缓存
					if (matchInfoResponse != null
							&& matchInfoResponse.getMatchs() != null) {
						/*DBUtil.saveMatchInfoListToDB(mContext,
								matchInfoResponse.getMatchs(), null);*/
						switch (MatchStateEnum.getEnumFromString(state)) {
						case CHALLENGE_INVITING:
							matchInfoOfInviting = matchInfoResponse
									.getMatchs();
							countMatchInviting = matchInfoOfInviting.size();
							if (countMatchInviting == 0) {
								rel_inviting_count.setVisibility(View.GONE);
							} else {
								rel_inviting_count
										.setVisibility(View.VISIBLE);
								inviting_count.setText(""
										+ countMatchInviting);
							}
							break;
						case CREATED:
							CustomApplcation
									.setMatchInfoOfCreated(matchInfoResponse
											.getMatchs());// 保存到全局
							countMatchCreated = CustomApplcation
									.getMatchInfoOfCreated().size();
							if (countMatchCreated == 0) {
								rel_created_count.setVisibility(View.GONE);
							} else {
								rel_created_count
										.setVisibility(View.VISIBLE);
								created_count.setText(""
										+ countMatchCreated);
							}
							break;
						case PENDING:
							CustomApplcation.setMatchInfoOfPending(matchInfoResponse.getMatchs());
							countMatchPending = CustomApplcation.getMatchInfoOfPending().size();
							if(countMatchPending == 0){
								rel_pedding_count.setVisibility(View.GONE);
							}else{
								rel_pedding_count.setVisibility(View.VISIBLE);
								pedding_count.setText("" + countMatchPending);
							}
							break;
						case PLAYING:
							matchInfoOfPlaying = matchInfoResponse
									.getMatchs();
							countMatchKickOff = matchInfoOfPlaying.size();
							if (countMatchKickOff == 0) {
								rel_playing_count.setVisibility(View.GONE);
							} else {
								rel_playing_count
										.setVisibility(View.VISIBLE);
								playing_count.setText(""
										+ countMatchKickOff);
							}
							break;
						case OVER:
							matchInfoOfOver = matchInfoResponse.getMatchs();
							countMatchOver = matchInfoOfOver.size();
							if (countMatchOver == 0) {
								rel_over_count.setVisibility(View.GONE);
							} else {
								rel_over_count.setVisibility(View.VISIBLE);
								over_count.setText("" + countMatchOver);
							}
							break;
						default:
							break;
						}
					}
		        }

		        @Override
		        public void onFailure(HttpException error, String msg) {
					if(!NetWorkManager.getInstance(mContext).isNetConnected()) {
//						ShowToast("没有可用的网络");
					}else if(error == null) {
//						ShowToast("MatchFragment-getMatchInfo-服务器连接错误");
					}else if(error.getExceptionCode() == 401){
						ErrorCodeUtil.ErrorCode401(mContext);
					}
		        }
		});
		
		
//		NetWorkManager.getInstance(mContext).getStateMatchList(
//				user.getPlayer().getUuid(), user.getToken(),
//				0, 1000, state, new Listener<JSONObject>() {
//					@Override
//					public void onResponse(JSONObject response) {
//						Gson gson = new Gson();
//						MatchInfoResponse matchInfoResponse = null;
//						try {
//							matchInfoResponse = gson.fromJson(
//									response.toString(),
//									new TypeToken<MatchInfoResponse>() {
//									}.getType());
//							String st = "获取状态成功：" + state ;
//							if (matchInfoResponse != null
//									&& matchInfoResponse.getMatchs() != null){
//								st += "大小：" +matchInfoResponse.getMatchs().size();
//							}
//							ShowToast(st);
//							FileOperation.writeFileAppend(st);
//							FileOperation.writeFileAppend("\n");
//						} catch (JsonSyntaxException e) {
//							matchInfoResponse = null;
//							e.printStackTrace();
//						}
//
//						// 将从服务器上获取得到的“我的球队”数据加入本地数据库，缓存
//						if (matchInfoResponse != null
//								&& matchInfoResponse.getMatchs() != null) {
//							/*DBUtil.saveMatchInfoListToDB(mContext,
//									matchInfoResponse.getMatchs(), null);*/
//							switch (MatchStateEnum.getEnumFromString(state)) {
//							case CHALLENGE_INVITING:
//								matchInfoOfInviting = matchInfoResponse
//										.getMatchs();
//								countMatchInviting = matchInfoOfInviting.size();
//								if (countMatchInviting == 0) {
//									rel_inviting_count.setVisibility(View.GONE);
//								} else {
//									rel_inviting_count
//											.setVisibility(View.VISIBLE);
//									inviting_count.setText(""
//											+ countMatchInviting);
//								}
//								break;
//							case CREATED:
//								CustomApplcation
//										.setMatchInfoOfCreated(matchInfoResponse
//												.getMatchs());// 保存到全局
//								countMatchCreated = CustomApplcation
//										.getMatchInfoOfCreated().size();
//								if (countMatchCreated == 0) {
//									rel_created_count.setVisibility(View.GONE);
//								} else {
//									rel_created_count
//											.setVisibility(View.VISIBLE);
//									created_count.setText(""
//											+ countMatchCreated);
//								}
//								break;
//							case PENDING:
//								CustomApplcation.setMatchInfoOfPending(matchInfoResponse.getMatchs());
//								countMatchPending = CustomApplcation.getMatchInfoOfPending().size();
//								if(countMatchPending == 0){
//									rel_pedding_count.setVisibility(View.GONE);
//								}else{
//									rel_pedding_count.setVisibility(View.VISIBLE);
//									pedding_count.setText("" + countMatchPending);
//								}
//								break;
//							case PLAYING:
//								matchInfoOfPlaying = matchInfoResponse
//										.getMatchs();
//								countMatchKickOff = matchInfoOfPlaying.size();
//								if (countMatchKickOff == 0) {
//									rel_playing_count.setVisibility(View.GONE);
//								} else {
//									rel_playing_count
//											.setVisibility(View.VISIBLE);
//									playing_count.setText(""
//											+ countMatchKickOff);
//								}
//								break;
//							case OVER:
//								matchInfoOfOver = matchInfoResponse.getMatchs();
//								countMatchOver = matchInfoOfOver.size();
//								if (countMatchOver == 0) {
//									rel_over_count.setVisibility(View.GONE);
//								} else {
//									rel_over_count.setVisibility(View.VISIBLE);
//									over_count.setText("" + countMatchOver);
//								}
//								break;
//							default:
//								break;
//							}
//						}
//					}
//				},new ErrorListener() {
//
//					@Override
//					public void onErrorResponse(VolleyError error) {
////						VolleyLog.e("Error: ", error.getMessage());
//						//ShowToast("获取球队列表失败");
//						if(!NetWorkManager.getInstance(mContext).isNetConnected()) {
////							ShowToast("没有可用的网络");
//						}else if(error.networkResponse == null) {
////							ShowToast("MatchFragment-getMatchInfo-服务器连接错误");
//						}else if(error.networkResponse.statusCode == 401){
//							ErrorCodeUtil.ErrorCode401(mContext);
//						}
//					}
//				});
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!MainActivity.pushMessageList.contains(this)) {
			MainActivity.pushMessageList.add(this);// 监听推送的消息
		}
	}

	public void onDestroy() {
		super.onDestroy();
		if (MainActivity.pushMessageList.contains(this)) {
			MainActivity.pushMessageList.remove(this);// 取消监听推送的消息
		}
	}

	@Override
	public void onDetach() {
		EventBus.getDefault().unregister(this);
		super.onDetach();
	}

	/**
	 * 注销广播
	 * */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mContext.unregisterReceiver(receiver);
	}

	@Override
	public void onTeamMessageListener(
			PushMessageAbstract<? extends MessageContent> message) {
	
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
/*		if (requestCode == REQUEST_CODE_CREATE_MATCH) {// 创建比赛成功，刷新创建中的列表
			getMatchInfo(MatchStateEnum.INVITING.getValue());
			return;
		}*/	//TODO 迁移至友谊赛
	}

	@SuppressWarnings("deprecation")
	public void Notification(Context context, String title, String description) {
		Notification notify = new Notification();
		// 通知栏图标
		notify.icon = R.drawable.ic_launcher;
		// 通知栏文本
		notify.tickerText = description;
		// 通知栏发送时间
		notify.when = System.currentTimeMillis();
		// 通知栏默认声音，默认振动，默认闪光灯
		notify.defaults = Notification.DEFAULT_ALL;
		PendingIntent contentIntent = PendingIntent.getActivities(context, 0,
				makeIntentStack(context), PendingIntent.FLAG_CANCEL_CURRENT);
		notify.setLatestEventInfo(context, title, description, contentIntent);
		// notify.flags |= Notification.DEFAULT_ALL;
		notify.flags |= Notification.FLAG_AUTO_CANCEL;
		// 获取系统的Notification服务
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, notify);
	}

	// 创建activity栈的根activity
	private Intent[] makeIntentStack(Context context) {
	    Intent[] intents = new Intent[2];  
	    intents[0] = Intent.makeRestartActivityTask(new ComponentName(context, com.kinth.football.ui.MainActivity.class));  
	    intents[1] = new Intent(context, com.kinth.football.ui.match.MessageCenterActivity.class); 
	    intents[1].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED); 
	    return intents;  
	}

	// 各种比赛相关的信息
	@Override
	public void onMatchMessageListener(
			PushMessageAbstract<? extends MessageContent> message) {
		String title = null;
		String description = null;
		String teamUuid = null;

		switch (PushMessageEnum.getEnumFromString(message.getType())) {
		case MATCH_INVITATION:
			title = "邀请消息";
			description = message.getContent().getDescription();
			teamUuid = ((MatchInvitationPM) message).getContent().getTeamUuid();
			sendTeamMessage(teamUuid, description);
			// 保存状态到数据库
			setMatchState(MatchStateEnum.INVITING.getValue(),
					((MatchInvitationPM) message).getContent().getMatchUuid());
			getMatchInfo(MatchStateEnum.CHALLENGE_INVITING.getValue());
			break;

		case MATCH_INVITATION_CONFIRM:
			if (((MatchInvitationConfirmPM) message).getContent().isConfirm()) {// 比赛同意
				title = "邀请确认消息";
				// 保存状态到数据库
				setMatchState(MatchStateEnum.INVITING.getValue(),
						((MatchInvitationConfirmPM) message).getContent()
								.getMatchUuid());
			} else {// 比赛拒绝
				title = "邀请拒绝消息";
				// 保存状态到数据库
				setMatchState(MatchStateEnum.CANCELED.getValue(),
						((MatchInvitationConfirmPM) message).getContent()
								.getMatchUuid());
			}
			description = message.getContent().getDescription();
			teamUuid = ((MatchInvitationConfirmPM) message).getContent()
					.getTeamUuid();
			sendTeamMessage(teamUuid, description);

			getMatchInfo(MatchStateEnum.CHALLENGE_INVITING.getValue());
			getMatchInfo(MatchStateEnum.CREATED.getValue());
			break;
		case MATCH_CREATED:
			title = "比赛创建消息--开始报名";
			description = message.getContent().getDescription();
			teamUuid = ((MatchCreatedPM) message).getContent().getTeamUuid();
			sendTeamMessage(teamUuid, description);
			// 保存状态到数据库
			setMatchState(MatchStateEnum.CREATED.getValue(),
					((MatchCreatedPM) message).getContent().getMatchUuid());

			getMatchInfo(MatchStateEnum.CHALLENGE_INVITING.getValue());
			getMatchInfo(MatchStateEnum.CREATED.getValue());
			break;
		case MATCH_SIGNUP:
			// tips_match_other.setVisibility(View.VISIBLE);
			title = "比赛报名消息--谁谁报名了";
			description = message.getContent().getDescription();
			teamUuid = ((MatchSignupPM) message).getContent().getTeamUuid();
			sendTeamMessage(teamUuid, description);
			// 显示提示信息
			// 该信息无需处理
			getMatchInfo(MatchStateEnum.CREATED.getValue());
			break;
		case MATCH_PENDING:
			// tips_match_other.setVisibility(View.VISIBLE);
			title = "待开赛消息";
			description = message.getContent().getDescription();
			teamUuid = ((MatchPendingPM) message).getContent().getTeamUuid();
			sendTeamMessage(teamUuid, description);
			// 保存状态到数据库
			setMatchState(MatchStateEnum.PENDING.getValue(),
					((MatchPendingPM) message).getContent().getMatchUuid());

			getMatchInfo(MatchStateEnum.CREATED.getValue());
			getMatchInfo(MatchStateEnum.PENDING.getValue());

			break;
		case MATCH_KICK_OFF:
			// tips_match_other.setVisibility(View.VISIBLE);
			title = "比赛中消息";
			description = message.getContent().getDescription();
			teamUuid = ((MatchKickOffPM) message).getContent().getTeamUuid();
			sendTeamMessage(teamUuid, description);
			// 保存状态到数据库
			setMatchState(MatchStateEnum.PLAYING.getValue(),
					((MatchKickOffPM) message).getContent().getMatchUuid());
			//
			getMatchInfo(MatchStateEnum.PENDING.getValue());
			getMatchInfo(MatchStateEnum.PLAYING.getValue());
			break;
		case MATCH_OVER:
			// tips_match_other.setVisibility(View.VISIBLE);
			title = "待评价消息";
			description = message.getContent().getDescription();
			teamUuid = ((MatchOverPM) message).getContent().getTeamUuid();
			sendTeamMessage(teamUuid, description);
			// 保存状态到数据库
			setMatchState(MatchStateEnum.OVER.getValue(),
					((MatchOverPM) message).getContent().getMatchUuid());

			getMatchInfo(MatchStateEnum.PLAYING.getValue());
			getMatchInfo(MatchStateEnum.OVER.getValue());
			break;
		case MATCH_FINISHED:
			//TODO 转到eventBus去处理
			break;
		case MATCH_CANCELED:
			title = "比赛取消消息";
			description = message.getContent().getDescription();
			teamUuid = ((MatchCancledPM) message).getContent().getTeamUuid();
			sendTeamMessage(teamUuid, description);
			// 保存状态到数据库
			setMatchState(MatchStateEnum.CANCELED.getValue(),
					((MatchCancledPM) message).getContent().getMatchUuid());

			switch (getCurrentState(((MatchCancledPM) message).getContent()
					.getMatchUuid())) {
			case INVITING:
				getMatchInfo(MatchStateEnum.CHALLENGE_INVITING.getValue());
				break;
			case CREATED:
				getMatchInfo(MatchStateEnum.CREATED.getValue());
				break;
			case PENDING:
				getMatchInfo(MatchStateEnum.PENDING.getValue());
				break;
			case PLAYING:
				getMatchInfo(MatchStateEnum.PLAYING.getValue());
				break;
			case OVER:
				getMatchInfo(MatchStateEnum.OVER.getValue());
				break;
			default:
				break;
			}
			break;
		case MATCH_REFEREE_INVITATION:
			title = "比赛裁判邀请消息";
			description = message.getContent().getDescription();
			sendSystemMessage(description);
			break;
		case MATCH_CHALLENGE:
			title = "约赛应战消息";
			description = message.getContent().getDescription();
			teamUuid = ((MatchChallengePM) message).getContent().getTeamUuid();
			sendTeamMessage(teamUuid, description);
			
			Intent intent2 = new Intent(Action.ACTION_BROADCAST_MATCH_COMFIRM_OR_REFUSE);//需要刷新列表数据
			getActivity().sendBroadcast(intent2);
			break;
		case MATCH_CHALLENGE_CONFIRM:
			title = "拒绝应战消息";
			description = message.getContent().getDescription();
			teamUuid = ((MatchChallengePM) message).getContent().getTeamUuid();
			sendTeamMessage(teamUuid, description);
			break;
		case MATCH_KICK_OFF_NOTIFICATION:
			title = "比赛开赛前通知消息";
			description = message.getContent().getDescription();
			teamUuid = ((MatchKickOffNotificationPM) message).getContent().getTeamUuid();
			sendTeamMessage(teamUuid, description);
			break;
		case MATCH_OVER_TO_REFEREE:
			title = "推送给裁判的比赛结束消息";
			getMatchInfo(MatchStateEnum.PLAYING.getValue());
			getMatchInfo(MatchStateEnum.OVER.getValue());
			break;
		case NULL:
		case TEAM_PLAYER_INVITATION:
		case TEAM_PLAYER_INVITATION_CONFIRM:
			break;
		}
	}

	private MatchStateEnum getCurrentState(String matchUuid) {
		for (MatchInfo matchInfo : matchInfoOfInviting) {
			if (matchUuid.equals(matchInfo.getUuid())) {
				return MatchStateEnum.INVITING;
			}
		}
		for (MatchInfo matchInfo : CustomApplcation.getMatchInfoOfCreated()) {
			if (matchUuid.equals(matchInfo.getUuid())) {
				return MatchStateEnum.CREATED;
			}
		}
		for(MatchInfo matchInfo : CustomApplcation.getMatchInfoOfPending()){
			if(matchUuid.equals(matchInfo.getUuid())){
				return MatchStateEnum.PENDING;
			}
		}
		for (MatchInfo matchInfo : matchInfoOfPlaying) {
			if (matchUuid.equals(matchInfo.getUuid())) {
				return MatchStateEnum.PLAYING;
			}
		}
		for (MatchInfo matchInfo : matchInfoOfOver) {
			if (matchUuid.equals(matchInfo.getUuid())) {
				return MatchStateEnum.OVER;
			}
		}
		return MatchStateEnum.NULL;
	}

	private void sendTeamMessage(String teamUuid, final String description) {
		User user = UserManager.getInstance(mContext).getCurrentUser();
		if(user == null || TextUtils.isEmpty(user.getToken())){
			return;
		}
		NetWorkManager.getInstance(mContext).getTeam(teamUuid,
				user.getToken(),
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// {"firstCaptainUuid":"22bee0e9-15c9-4ddf-87b6-79bd4d21698b","slogan":null,"secondCaptainUuid":null,"cityId":289,"description":null,"name":"HK","familyPhoto":null,"thirdCaptainUuid":null,"creatorUuid":"22bee0e9-15c9-4ddf-87b6-79bd4d21698b","badge":null,"uuid":"2dqYfDRmij-05yW8yAQsr4","date":1425372009889,"regionId":1}
						Gson gson = new Gson();
						TeamInfoComplete teamInfoComplete = gson.fromJson(
								response.toString(),
								new TypeToken<TeamInfoComplete>() {
								}.getType());
						Team team = teamInfoComplete.getTeam();
						if (team == null)
							return;
//						Log.e("sendTeamMessage", team.getBadge());
						team.setLike(teamInfoComplete.getLike());
						team.setLiked(teamInfoComplete.isLiked());// by sola
						ChatMsg msg = ChatMsg.createTeamReceiveMsg(mContext,
								team, description);
						MyChatManager.getInstance(mContext).saveReceiveMessage(
								true, msg);
						CustomApplcation.getInstance().getMediaPlayer().start();
						MyMessageReceiver.showNotification(mContext,team, msg);
						if (MyMessageReceiver.ehList.size() > 0) {// 有监听的时候，传递下去
							for (int i = 0; i < MyMessageReceiver.ehList.size(); i++) {
								((EventListener) MyMessageReceiver.ehList
										.get(i)).onMessage(msg);
							}
						}
						
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
//						VolleyLog.e("Error: ", error.getMessage());
//						Log.e("response error", "" + error.getMessage());
					}
				});
	}

	private void sendSystemMessage(String description) {
		ChatMsg msg = ChatMsg.createSystemReceiveMsg(mContext, description);
		MyChatManager.getInstance(mContext).saveReceiveMessage(true, msg);
		// 播放声音
		CustomApplcation.getInstance().getMediaPlayer().start();
		MyMessageReceiver.showNotification(mContext, null,msg);
		if (MyMessageReceiver.ehList.size() > 0) {// 有监听的时候，传递下去
			for (int i = 0; i < MyMessageReceiver.ehList.size(); i++) {
				((EventListener) MyMessageReceiver.ehList.get(i))
						.onMessage(msg);
			}
		}
	}

	// 保存比赛状态到数据库
	private void setMatchState(String state, String matchUuid) {
		QueryBuilder<Match> qb = CustomApplcation.getDaoSession(mContext)
				.getMatchDao().queryBuilder();
		Match match = null;
		qb.where(MatchDao.Properties.Uuid.eq(matchUuid));
		try {
			match = qb.uniqueOrThrow();
			match.setState(state);
		} catch (DaoException e) {
			// 没有该场比赛，插入比赛状态
			match = new Match();
			match.setUuid(matchUuid);
			match.setState(state);
		}
		CustomApplcation.getDaoSession(mContext).getMatchDao()
				.insertOrReplace(match);
	}

	//比赛结束的消息
	public void onEventMainThread(MatchFinishedEvent matchFinishedEvent){
		MatchFinishedPM matchFinishedPM = matchFinishedEvent.getMatchFinishedPM();
		String description = matchFinishedPM.getContent().getDescription();
		String teamUuid = matchFinishedPM.getContent().getTeamUuid();
		
		sendTeamMessage(teamUuid, description);
		// 保存状态到数据库
		setMatchState(MatchStateEnum.FINISHED.getValue(),
				matchFinishedPM.getContent().getMatchUuid());
		getMatchInfo(MatchStateEnum.OVER.getValue());
	}
	
	/**
	 * 创建友谊赛事件
	 * @param createFriendlyMatchEvent
	 */
	public void onEventMainThread(CreateFriendlyMatchEvent createFriendlyMatchEvent){
		getMatchInfo(MatchStateEnum.CHALLENGE_INVITING.getValue());
	}
	
	
	/**
	 * 修改球队主场队服
	 * @param modifyTeamHomeJerseyEvent
	 */
	public void onEventMainThread(ModifyTeamHomeJerseyEvent modifyTeamHomeJerseyEvent){
		getMatchInfo(MatchStateEnum.CREATED.getValue());
		getMatchInfo(MatchStateEnum.PENDING.getValue());
	}
	
	/**
	 * 修改球队客场队服
	 * @param modifyTeamRoadJerseyEvent
	 */
	public void onEventMainThread(ModifyTeamRoadJerseyEvent modifyTeamRoadJerseyEvent){
		getMatchInfo(MatchStateEnum.CREATED.getValue());
		getMatchInfo(MatchStateEnum.PENDING.getValue());
	}
	
	/**
	 * 修改第三队服
	 * @param modifyTeamAlternetJerseyEvent
	 */
	public void onEventMainThread(ModifyTeamAlternetJerseyEvent modifyTeamAlternetJerseyEvent){
		getMatchInfo(MatchStateEnum.CREATED.getValue());
		getMatchInfo(MatchStateEnum.PENDING.getValue());
	}
	/**
	 * 退出球队事件
	 * @param exitTeamEvent
	 */
	public void onEventMainThread(ExitTeamEvent exitTeamEvent){
            executeGetFiveStateList();
	}
	
}
