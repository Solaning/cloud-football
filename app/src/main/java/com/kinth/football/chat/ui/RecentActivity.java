package com.kinth.football.chat.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.kinth.football.MyMessageReceiver;
import com.kinth.football.R;
import com.kinth.football.bean.User;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.chat.adapter.ChatRecentAdapter;
import com.kinth.football.chat.bean.ChatMsg;
import com.kinth.football.chat.bean.ChatRecent;
import com.kinth.football.chat.db.ChatDBManager;
import com.kinth.football.dao.Player;
import com.kinth.football.manager.UserManager;
import com.kinth.football.ui.ActivityBase;
import com.kinth.football.ui.MainActivity;

/**
 * 点击推送栏消息之后跳转到的最近会话Activity
 */
public class RecentActivity extends ActivityBase implements OnItemClickListener {

	private ListView listview;
	private ChatRecentAdapter adapter;
	private List<ChatRecent> chatRecents;
	private ChatMsg relayMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent);
		initData();
		initView();
	}

	private void initData() {
		// TODO 自动生成的方法存根
		chatRecents = new ArrayList<ChatRecent>();
		chatRecents = ChatDBManager.create(this).queryRecents(
				UserManager.getInstance(this).getCurrentUserPhone(), 0);
		adapter = new ChatRecentAdapter(this, R.layout.item_conversation,
				chatRecents);
		if (MyMessageReceiver.notificationManager != null) {//清除推送栏消息
			MyMessageReceiver.notificationManager.cancelAll();
		}
		relayMsg = (ChatMsg) getIntent().getSerializableExtra(ChatConstants.INTENT_CHATMSG_BEAN);
	}

	private void initView() {
		initTopBarForLeft("请选择");
		listview = (ListView) findViewById(R.id.lv);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		listview.setDivider(null);	//间隔无背景色
		listview.setDividerHeight(5);	//间隔5像素
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		ChatRecent recent = (ChatRecent) adapter.getItem(position);
		if(MainActivity.iv_recent_tips != null && MainActivity.iv_recent_tips.getVisibility() != View.VISIBLE && recent.getTargetid().equals("System")){
			ShowToast("不能发消息给此人");
			return;
		}
		ChatDBManager.create(this).resetUnread(recent.getTargetid(), recent.getTag());// 重置未读消息
		
		// 组装聊天对象
		User user = new User();
		Player player = new Player();
		player.setName(recent.getNick());
		player.setPicture(recent.getAvatar());
		player.setUuid(recent.getUserName());
		player.setAccountName(recent.getTargetid());
		user.setPlayer(player);

		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra(ChatConstants.INTENT_USER_BEAN, user);
		intent.putExtra(ChatConstants.TAG, recent.getTag());
		if(relayMsg!=null){
			relayMsg = ChatMsg.createSendMessage(mContext, recent.getTargetid(), relayMsg.getContent(), relayMsg.getMsgType(), ChatConstants.STATUS_SEND_START, recent.getTag());
			intent.putExtra(ChatConstants.INTENT_CHATMSG_BEAN, relayMsg);
		}
		startAnimActivity(intent);

		if (MainActivity.iv_recent_tips != null)//取消红点提示
			MainActivity.iv_recent_tips.setVisibility(View.INVISIBLE);

		finish();
	}

}
