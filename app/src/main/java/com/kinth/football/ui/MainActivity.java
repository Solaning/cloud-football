package com.kinth.football.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.MyMessageReceiver;
import com.kinth.football.R;
import com.kinth.football.bean.User;
import com.kinth.football.bean.message.MatchCancledPM;
import com.kinth.football.bean.message.MatchChallengePM;
import com.kinth.football.bean.message.MatchCreatedPM;
import com.kinth.football.bean.message.MatchInvitationConfirmPM;
import com.kinth.football.bean.message.MatchInvitationPM;
import com.kinth.football.bean.message.MatchKickOffNotificationPM;
import com.kinth.football.bean.message.MatchKickOffPM;
import com.kinth.football.bean.message.MatchOverPM;
import com.kinth.football.bean.message.MatchOverToRefereePM;
import com.kinth.football.bean.message.MatchPendingPM;
import com.kinth.football.bean.message.MatchRefereeInvitationPM;
import com.kinth.football.bean.message.MatchSignupPM;
import com.kinth.football.bean.message.MessageContent;
import com.kinth.football.bean.message.PushMessageAbstract;
import com.kinth.football.chat.ChatService;
import com.kinth.football.chat.ConnectionImp;
import com.kinth.football.chat.XmppManager;
import com.kinth.football.chat.bean.ChatMsg;
import com.kinth.football.chat.db.ChatDBManager;
import com.kinth.football.config.Action;
import com.kinth.football.config.JConstants;
import com.kinth.football.dao.Player;
import com.kinth.football.eventbus.bean.FinishAllActivityEvent;
import com.kinth.football.eventbus.bean.GetPersonInfoEvent;
import com.kinth.football.eventbus.bean.TeamPushMessageEvent;
import com.kinth.football.listener.EventListener;
import com.kinth.football.listener.PushMessageListener;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.fragment.ChatFragment;
import com.kinth.football.ui.fragment.HomeFragment;
import com.kinth.football.ui.fragment.MatchFragment;
import com.kinth.football.ui.fragment.MineFragment;
import com.kinth.football.ui.fragment.TeamFragment;
import com.kinth.football.util.CheckVersionUtil;
import com.kinth.football.util.DatabaseUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.PushMessageUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import de.greenrobot.event.EventBus;

/**
 * 主界面
 * 	
 * @ClassName: MainActivity
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends ActivityBase implements EventListener {

	public static final String CURRENTTIEM = "CURRENTTIEM";
	private static final String CURRENT_TAB_INDEX = "CURRENT_TAB_INDEX";
	private static final int WHAT_START_LOAD_PAGE = 100;
	private static final int WHAT_ON_MESSAGE = 101;

	private Button[] mTabs;

	private PushMessageAbstract<? extends MessageContent> pushMessage = null;

	// (XMPP)事件监听
	public static ArrayList<PushMessageListener> pushMessageList = new ArrayList<PushMessageListener>();

	private int currentTabIndex;
	private MyBroadcastReceiver receiver = new MyBroadcastReceiver();

	@ViewInject(R.id.iv_chat_tips)
	public static ImageView iv_recent_tips;// 聊天提示

	@ViewInject(R.id.rtl_match_tips)
	private RelativeLayout matchTips;// 比赛提示

	@ViewInject(R.id.tv_match_tips)
	private TextView matchTipsNum;// 比赛信息提示的个数

	@ViewInject(R.id.iv_team_tips)
	private ImageView teamTips;// 球队提示
	
	@ViewInject(R.id.viewGroup)
	private RelativeLayout viewGroup;

	// @ViewInject(R.id.rtl_team_tips)
	// private RelativeLayout teamTips;//球队的提示

	// @ViewInject(R.id.tv_team_tips)
	// private TextView teamTipsNum;//球队提示

	private Timer tStartService = new Timer();
	
	@Override
	protected void onRestart() {
		super.onRestart();
	}
	
	@Override
	protected void onStart() {
		super.onRestart();
	}
	
	@Override
	protected void onPause() {
		super.onRestart();
	}

	@Override
	protected void onStop() {
		super.onRestart();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		ViewUtils.inject(this);

		// 将MainActivity加入到QuitWay中的activity列表中
		// QuitWay.activityList.add(MainActivity.this);
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();	
		ViewCompat.setBackground(viewGroup, new BitmapDrawable(getResources(),
				background));
		
		initView();
		handler.sendEmptyMessageDelayed(WHAT_START_LOAD_PAGE, 170);
	}

	public MainActivity() {
		super();
	}

	private void executeGetPersonInfo() {
		NetWorkManager.getInstance(mContext).getPlayerInfo(
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Gson gson = new Gson();
						Player player = gson.fromJson(response.toString(),
								new TypeToken<Player>() {
								}.getType());
						if (player == null) {
							return;
						}
						UserManager.getInstance(mContext).getCurrentUser()
								.setPlayer(player);
						EventBus.getDefault().post(new GetPersonInfoEvent(player));
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							// ShowToast("当前网络不可用");
						} else if (error.networkResponse == null) {
							// ShowToast("MainActivity-executeGetPersonInfo-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						}
					}
				});
	}

	/**
	 * xmpp连接
	 */
	private void loginXmppServ() {
		User user = footBallUserManager.getCurrentUser();
		if (user == null) {
			ErrorCodeUtil.ErrorCode401(mContext);
			return;
		}
		XmppManager.getInstance(getApplicationContext())
				.loginAndAddChatManagerListener(getApplicationContext(),
						user.getPlayer().getPhone(), user.getToken(),
						new ConnectionImp() {
							@Override
							public void onSucc() {
								// LogUtil.i("登录成功");
								// TODO 没有其他处理
							}

							@Override
							public void onFail() {
								// LogUtil.i("登录失败");
								// TODO 没有其他处理
							}
						});
	}

	private void initView() {
		mTabs = new Button[5];
		mTabs[0] = (Button) findViewById(R.id.btn_home);
		mTabs[1] = (Button) findViewById(R.id.btn_match);
		mTabs[2] = (Button) findViewById(R.id.btn_game_manage);
		mTabs[3] = (Button) findViewById(R.id.btn_chat);
		mTabs[4] = (Button) findViewById(R.id.btn_me);
		// 把currentTabIndex设为选中状态
		mTabs[currentTabIndex].setSelected(true);

		IntentFilter filter = new IntentFilter();
		filter.addAction(JConstants.ACTION_FINISH_MAIN);// 退出广播
		filter.addAction(Action.ACTION_BROADCAST_MATCH_INVITATION);
		filter.addAction(Action.ACTION_BROADCAST_MATCH_INVITATION_CONFIRM);
		filter.addAction(Action.ACTION_BROADCAST_MATCH_CREATED);
		filter.addAction(Action.ACTION_BROADCAST_MATCH_SIGNUP);
		filter.addAction(Action.ACTION_BROADCAST_MATCH_PENDING);
		filter.addAction(Action.ACTION_BROADCAST_MATCH_KICK_OFF);
		filter.addAction(Action.ACTION_BROADCAST_MATCH_OVER);
		filter.addAction(Action.ACTION_BROADCAST_MATCH_CANCELED);
		filter.addAction(Action.ACTION_BROADCAST_MATCH_REFEREE_INVITATION);
		filter.addAction(Action.ACTION_BROADCAST_MATCH_CHALLENGE);
		filter.addAction(Action.ACTION_BROADCAST_MATCH_CHALLENGE_CONFIRM);
		filter.addAction(Action.ACTION_BROADCAST_MATCH_KICK_OFF_NOTIFICATION);
		filter.addAction(Action.ACTION_BROADCAST_MATCH_OVER_TO_REFEREE_NOTIFICATION);
		registerReceiver(receiver, filter);
	}

	private void initTab(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			List<Fragment> fragments = fragmentManager.getFragments();
			if(fragments != null){
				for(Fragment fragment : fragments){
					if(fragment != null){
						fragmentTransaction.remove(fragment);
					}
				}
			}
			currentTabIndex = 0;
			HomeFragment homeFragment = HomeFragment.newInstance("HomeFragment");
			MatchFragment matchFragment = MatchFragment.newInstance("MatchFragment");
			// 添加显示第一个fragment
			fragmentTransaction
					.add(R.id.fragment_container, homeFragment, HomeFragment.class.getCanonicalName())
					.add(R.id.fragment_container, matchFragment, MatchFragment.class.getCanonicalName())
					.show(homeFragment).hide(matchFragment).commit();
		}
	}

	/**
	 * button点击事件
	 * 
	 * @param view
	 */
	public void onTabSelect(View view) {
		int index = 0;
		switch (view.getId()) {
		case R.id.btn_home:
			index = 0;
			if (currentTabIndex != index) {
				FragmentManager fragmentManager = getSupportFragmentManager();
				FragmentTransaction trx = fragmentManager.beginTransaction();
				Fragment currentFragment = getCurrentFragment(currentTabIndex, fragmentManager);
				Fragment targetFragment = fragmentManager.
	                    findFragmentByTag(HomeFragment.class.getCanonicalName());
				if(currentFragment != null){
					trx.hide(currentFragment);
				}
				if(targetFragment == null){
					targetFragment = HomeFragment.newInstance("HomeFragment");
				}
				if (!targetFragment.isAdded()) {
					trx.add(R.id.fragment_container, targetFragment, HomeFragment.class.getCanonicalName());
				}
				trx.show(targetFragment).commit();
			}
			break;
		case R.id.btn_match:
			index = 1;
			if (currentTabIndex != index) {
				FragmentManager fragmentManager = getSupportFragmentManager();
				FragmentTransaction trx = fragmentManager.beginTransaction();
				Fragment currentFragment = getCurrentFragment(currentTabIndex, fragmentManager);
				Fragment targetFragment = fragmentManager.
	                    findFragmentByTag(MatchFragment.class.getCanonicalName());
				if(currentFragment != null){
					trx.hide(currentFragment);
				}
				if(targetFragment == null){
					targetFragment = MatchFragment.newInstance("MatchFragment");
				}
				if (!targetFragment.isAdded()) {
					trx.add(R.id.fragment_container, targetFragment, MatchFragment.class.getCanonicalName());
				}
				trx.show(targetFragment).commit();
			}
			break;
		case R.id.btn_game_manage:
			index = 2;
			if (currentTabIndex != index) {
				FragmentManager fragmentManager = getSupportFragmentManager();
				FragmentTransaction trx = fragmentManager.beginTransaction();
				Fragment currentFragment = getCurrentFragment(currentTabIndex, fragmentManager);
				Fragment targetFragment = fragmentManager.
	                    findFragmentByTag(TeamFragment.class.getCanonicalName());
				if(currentFragment != null){
					trx.hide(currentFragment);
				}
				if(targetFragment == null){
					targetFragment = TeamFragment.newInstance("TeamFragment");
				}
				if (!targetFragment.isAdded()) {
					trx.add(R.id.fragment_container, targetFragment, TeamFragment.class.getCanonicalName());
				}
				trx.show(targetFragment).commit();
			}
			break;
		case R.id.btn_chat:
			index = 3;
			if (currentTabIndex != index) {
				FragmentManager fragmentManager = getSupportFragmentManager();
				FragmentTransaction trx = fragmentManager.beginTransaction();
				Fragment currentFragment = getCurrentFragment(currentTabIndex, fragmentManager);
				Fragment targetFragment = fragmentManager.
	                    findFragmentByTag(ChatFragment.class.getCanonicalName());
				if(currentFragment != null){
					trx.hide(currentFragment);
				}
				if(targetFragment == null){
					targetFragment = ChatFragment.newInstance("ChatFragment",
							getApplicationContext());
				}
				if (!targetFragment.isAdded()) {
					trx.add(R.id.fragment_container, targetFragment, ChatFragment.class.getCanonicalName());
				}
				trx.show(targetFragment).commit();
			}
			break;
		case R.id.btn_me:
			index = 4;
			if (currentTabIndex != index) {
				FragmentManager fragmentManager = getSupportFragmentManager();
				FragmentTransaction trx = fragmentManager.beginTransaction();
				Fragment currentFragment = getCurrentFragment(currentTabIndex, fragmentManager);
				Fragment targetFragment = fragmentManager.
	                    findFragmentByTag(MineFragment.class.getCanonicalName());
				if(currentFragment != null){
					trx.hide(currentFragment);
				}
				if(targetFragment == null){
					targetFragment = MineFragment.newInstance("MineFragment");
				}
				if (!targetFragment.isAdded()) {
					trx.add(R.id.fragment_container, targetFragment, MineFragment.class.getCanonicalName());
				}
				trx.show(targetFragment).commit();
			}
			break;
		}
		mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}
	
	private Fragment getCurrentFragment(int currentIndex, FragmentManager fragmentManager){
		Fragment fragment = null;
		switch(currentIndex){
		case 0:
			fragment = fragmentManager.findFragmentByTag(HomeFragment.class.getCanonicalName());
			break;
		case 1:
			fragment = fragmentManager.findFragmentByTag(MatchFragment.class.getCanonicalName());
			break;
		case 2:
			fragment = fragmentManager.findFragmentByTag(TeamFragment.class.getCanonicalName());
			break;
		case 3:
			fragment = fragmentManager.findFragmentByTag(ChatFragment.class.getCanonicalName());
			break;
		case 4:
			fragment = fragmentManager.findFragmentByTag(MineFragment.class.getCanonicalName());
			break;
		}
		return fragment;
	}
	
	@Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(CURRENT_TAB_INDEX)) {
        	currentTabIndex = savedInstanceState.getInt(CURRENT_TAB_INDEX);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	outState.putInt(CURRENT_TAB_INDEX, currentTabIndex);
        super.onSaveInstanceState(outState);
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
	}

	private static long firstTime;// 返回键时间记录

	/**
	 * 连续按两次返回键就退出
	 */
	@Override
	public void onBackPressed() {
		if (firstTime + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();

			MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息

			// 可能还有消息栏的消息没有点击，如果程序退出了还点击该消息会报错
			if (MyMessageReceiver.notificationManager != null)
				MyMessageReceiver.notificationManager.cancelAll();

			finish();

			// 程序完全退出，保证聊天收发消息不受影响
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			// android.os.Process.killProcess(android.os.Process.myPid());//启动服务前杀死进程服务无法启动,而启动服务后再杀死进程时，好像服务会重启，但有时候可能重启比较慢，甚至没重启，所以退出到后台时由可能延迟甚至收不到消息
			XmppManager.getInstance(this).disconnect();

			/*
			 * Intent service = new Intent();// 创建启动Service的Intent
			 * service.setAction("chat.service.CHAT_SERVICE");//
			 * 为Intent设置Action属性
			 */
			Intent service = new Intent(MainActivity.this, ChatService.class);// 5.0中需要显式调用
			startService(service);// 启动指定Service
			tStartService.schedule(new timetask(), 2000);

		} else {
			ShowToast("再按一次退出程序");
		}
		firstTime = System.currentTimeMillis();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		unregisterReceiver(receiver);
	}

	/**
	 * 推送消息的处理
	 */
	public class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (intent != null && JConstants.ACTION_FINISH_MAIN.equals(action)) {
				finish();
				return;
			}
			if (Action.ACTION_BROADCAST_MATCH_INVITATION.equals(action)) {// 比赛邀请中
				pushMessage = PushMessageUtil.preHandlePushMessage(mContext,
						intent, MatchInvitationPM.class);
				if (pushMessage == null) {
					return;
				}
				if (pushMessageList.size() > 0) {// 有监听的时候，传递下去
					for (int i = 0; i < pushMessageList.size(); i++) {
						pushMessageList.get(i).onMatchMessageListener(
								pushMessage);
					}
				}
				return;
			}
			if (Action.ACTION_BROADCAST_MATCH_INVITATION_CONFIRM.equals(action)) {// 比赛邀请确认
				pushMessage = PushMessageUtil.preHandlePushMessage(mContext,
						intent, MatchInvitationConfirmPM.class);
				if (pushMessage == null) {
					return;
				}
				if (pushMessageList.size() > 0) {// 有监听的时候，传递下去
					for (int i = 0; i < pushMessageList.size(); i++) {
						pushMessageList.get(i).onMatchMessageListener(
								pushMessage);
					}
				}
				return;
			}
			if (Action.ACTION_BROADCAST_MATCH_CREATED.equals(action)) { // 创建成功，等待报名
				pushMessage = PushMessageUtil.preHandlePushMessage(mContext,
						intent, MatchCreatedPM.class);
				if (pushMessage == null) {
					return;
				}
				if (pushMessageList.size() > 0) {// 有监听的时候，传递下去
					for (int i = 0; i < pushMessageList.size(); i++) {
						pushMessageList.get(i).onMatchMessageListener(
								pushMessage);
					}
				}
				return;
			}
			if (Action.ACTION_BROADCAST_MATCH_PENDING.equals(action)) {// 待开赛
				pushMessage = PushMessageUtil.preHandlePushMessage(mContext,
						intent, MatchPendingPM.class);
				if (pushMessage == null) {
					return;
				}
				if (pushMessageList.size() > 0) {// 有监听的时候，传递下去
					for (int i = 0; i < pushMessageList.size(); i++) {
						pushMessageList.get(i).onMatchMessageListener(
								pushMessage);
					}
				}
				return;
			}
			if (Action.ACTION_BROADCAST_MATCH_SIGNUP.equals(action)) {// 报名完成
				pushMessage = PushMessageUtil.preHandlePushMessage(mContext,
						intent, MatchSignupPM.class);
				if (pushMessage == null) {
					return;
				}
				if (pushMessageList.size() > 0) {// 有监听的时候，传递下去
					for (int i = 0; i < pushMessageList.size(); i++) {
						pushMessageList.get(i).onMatchMessageListener(
								pushMessage);
					}
				}
				return;
			}
			if (Action.ACTION_BROADCAST_MATCH_KICK_OFF.equals(action)) {// 比赛中
				pushMessage = PushMessageUtil.preHandlePushMessage(mContext,
						intent, MatchKickOffPM.class);
				if (pushMessage == null) {
					return;
				}
				if (pushMessageList.size() > 0) {// 有监听的时候，传递下去
					for (int i = 0; i < pushMessageList.size(); i++) {
						pushMessageList.get(i).onMatchMessageListener(
								pushMessage);
					}
				}
				return;
			}
			if (Action.ACTION_BROADCAST_MATCH_OVER.equals(action)) {// 待评价
				pushMessage = PushMessageUtil.preHandlePushMessage(mContext,
						intent, MatchOverPM.class);
				if (pushMessage == null) {
					return;
				}
				if (pushMessageList.size() > 0) {// 有监听的时候，传递下去
					for (int i = 0; i < pushMessageList.size(); i++) {
						pushMessageList.get(i).onMatchMessageListener(
								pushMessage);
					}
				}
				return;
			}
			if (Action.ACTION_BROADCAST_MATCH_CANCELED.equals(action)) {// 比赛取消
				pushMessage = PushMessageUtil.preHandlePushMessage(mContext,
						intent, MatchCancledPM.class);
				if (pushMessage == null) {
					return;
				}
				if (pushMessageList.size() > 0) {// 有监听的时候，传递下去
					for (int i = 0; i < pushMessageList.size(); i++) {
						pushMessageList.get(i).onMatchMessageListener(
								pushMessage);
					}
				}
				return;
			}
			if (Action.ACTION_BROADCAST_MATCH_REFEREE_INVITATION.equals(action)) { // 裁判邀请消息
				pushMessage = PushMessageUtil.preHandlePushMessage(mContext,
						intent, MatchRefereeInvitationPM.class);
				if (pushMessage == null) {
					return;
				}
				if (pushMessageList.size() > 0) {// 有监听的时候，传递下去
					for (int i = 0; i < pushMessageList.size(); i++) {
						pushMessageList.get(i).onMatchMessageListener(
								pushMessage);
					}
				}
				return;
			}
			if (Action.ACTION_BROADCAST_MATCH_CHALLENGE.equals(action)) { // TODO
																			// 约赛应战消息
				pushMessage = PushMessageUtil.preHandlePushMessage(mContext,
						intent, MatchChallengePM.class);
				if (pushMessage == null) {
					return;
				}
				if (pushMessageList.size() > 0) {// 有监听的时候，传递下去
					for (int i = 0; i < pushMessageList.size(); i++) {
						pushMessageList.get(i).onMatchMessageListener(
								pushMessage);
					}
				}
				return;
			}
			if (Action.ACTION_BROADCAST_MATCH_CHALLENGE_CONFIRM.equals(action)) { // TODO
				// 约赛应战消息
				pushMessage = PushMessageUtil.preHandlePushMessage(mContext,
						intent, MatchChallengePM.class);
				if (pushMessage == null) {
					return;
				}
				if (pushMessageList.size() > 0) {// 有监听的时候，传递下去
					for (int i = 0; i < pushMessageList.size(); i++) {
						pushMessageList.get(i).onMatchMessageListener(
								pushMessage);
					}
				}
				return;
			}
			if (Action.ACTION_BROADCAST_MATCH_KICK_OFF_NOTIFICATION
					.equals(action)) { // TODO
				// 比赛开赛前通知消息
				pushMessage = PushMessageUtil.preHandlePushMessage(mContext,
						intent, MatchKickOffNotificationPM.class);
				if (pushMessage == null) {
					return;
				}
				if (pushMessageList.size() > 0) {// 有监听的时候，传递下去
					for (int i = 0; i < pushMessageList.size(); i++) {
						pushMessageList.get(i).onMatchMessageListener(
								pushMessage);
					}
				}
				return;
			}
			if (Action.ACTION_BROADCAST_MATCH_OVER_TO_REFEREE_NOTIFICATION
					.equals(action)) { // TODO
				// 比赛开赛前通知消息
				Log.e("2", "ACTION_BROADCAST_MATCH_OVER_TO_REFEREE_NOTIFICATION");
				pushMessage = PushMessageUtil.preHandlePushMessage(mContext,
						intent, MatchOverToRefereePM.class);
				if (pushMessage == null) {
					return;
				}
				if (pushMessageList.size() > 0) {// 有监听的时候，传递下去
					for (int i = 0; i < pushMessageList.size(); i++) {
						pushMessageList.get(i).onMatchMessageListener(
								pushMessage);
					}
				}
				return;
			}
		}
	}

	private Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_START_LOAD_PAGE://开始加载页面
				initTab(getIntent().getExtras());
				executeGetPersonInfo();

				DatabaseUtil.packDataBase(mContext);// 数据库文件拷贝
				loginXmppServ();

				// 定时检查更新版本
				checkUpdate();

				if (!ChatDBManager.create(mContext).isExistField3("recent", "tag")) {
					Log.e("recent-exist", "no-tag");
					ChatDBManager.create(mContext).addNewField("recent", "tag", "INTEGER");
					ChatDBManager.create(mContext).updateRecentTag(); // 针对以前没tag字段的数据表数据为空的情况，等全面使用改表格后就弃用此语句
				} else {
					Log.e("recent-exist", "tag");
					ChatDBManager.create(mContext).updateRecentTag();
				}

				if (!ChatDBManager.create(mContext).isExistField3("chat", "tag")) {
					Log.e("chat-exist", "no-tag");
					ChatDBManager.create(mContext).addNewField("chat", "tag", "INTEGER");
				}
				break;
			case WHAT_ON_MESSAGE:
				iv_recent_tips.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
			return false;
		}
	});

	/**
	 * 收到消息的监听
	 */
	@Override
	public void onMessage(final ChatMsg msg) {
		handler.sendEmptyMessage(WHAT_ON_MESSAGE);
		Fragment chatFragment = getCurrentFragment(3, getSupportFragmentManager());//获取聊天页面
		if (chatFragment != null) {
			((ChatFragment)chatFragment).refresh();
		}
	}

	@Override
	public void onNetChange(boolean paramBoolean) {

	}

	/**
	 * 检测更新，自带时间控制，一天只检测两次
	 */
	private void checkUpdate() {
		SharedPreferences currentTimeShared = getSharedPreferences(
				"current_time", Activity.MODE_PRIVATE);
//		SharedPreferences.Editor editor = currentTimeShared.edit();

		// 上次检测时间
		Long lastCheckTime = currentTimeShared.getLong(CURRENTTIEM, 0);
		// 当前时间
		Long currentTime = System.currentTimeMillis();
		// 相差的小时数
		Long diff = (currentTime - lastCheckTime) / 1000 / 3600;
		// 如果上次检测时间距离现在超过12小时将再次检测
		// if (diff >= 12L) {
		// // 设置检测时间
		// editor.putLong(CURRENTTIEM, currentTime);
		// editor.commit();
		// //更新版本信息
		// CheckVersionUtil.updataVersion(mContext);
		// }
		CheckVersionUtil.updataVersion(mContext, false);// 每次都检测更新
	}

	/**
	 * 用来判断服务是否运行.
	 * 
	 * @param context
	 * @param className
	 *            判断的服务名字：包名+类名
	 * @return true 在运行, false 不在运行
	 */

	public boolean isServiceRunning(Context context, String className) {
		boolean isRunning = false;

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(ACTIVITY_SERVICE);

		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(Integer.MAX_VALUE);

		if (!(serviceList.size() > 0)) {
			return false;
		}

		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	/**
	 * 计时器，判断后台服务是否启动
	 * 
	 * @author Administrator
	 * 
	 */
	class timetask extends TimerTask {

		@Override
		public void run() {
			if (!isServiceRunning(MainActivity.this,
					"com.kinth.football.chat.ChatService")) {
				Intent service = new Intent(MainActivity.this,
						ChatService.class);// 5.0中需要显式调用
				startService(service);// 启动指定Service
				tStartService.schedule(new timetask(), 2000);
			}
		}
	}
	
	/**
	 * 球队消息提示
	 * @param teamPushMessageEvent
	 */
	public void onEventMainThread(TeamPushMessageEvent teamPushMessageEvent){
		if (teamPushMessageEvent.getTeamTipsNum() <= 0) {
			teamTips.setVisibility(View.GONE);
		} else {
			teamTips.setVisibility(View.VISIBLE);
		}
	}

	public void onEventMainThread(FinishAllActivityEvent finishAllActivityEvent){
		finish();
	}
}
