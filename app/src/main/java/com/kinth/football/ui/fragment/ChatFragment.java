package com.kinth.football.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.kinth.football.MyMessageReceiver;
import com.kinth.football.R;
import com.kinth.football.bean.User;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.chat.adapter.ChatRecentAdapter;
import com.kinth.football.chat.bean.ChatRecent;
import com.kinth.football.chat.db.ChatDBManager;
import com.kinth.football.chat.listener.RefreshEvent;
import com.kinth.football.chat.ui.ChatActivity;
import com.kinth.football.dao.Player;
import com.kinth.football.manager.UserManager;
import com.kinth.football.ui.FragmentBase;
import com.kinth.football.ui.MainActivity;
import com.kinth.football.view.ClearEditText;

import de.greenrobot.event.EventBus;

/**
 * 最近会话Fragment
 */
@SuppressLint("ValidFragment")
public class ChatFragment extends FragmentBase implements OnItemClickListener {

	private ClearEditText etMsgSearch;	//搜索框，目前是以关键字查询最近聊天对象
	private ListView listview;
	
	private Context context;
	ChatRecentAdapter adapter;
	List<ChatRecent> chatRecents;

	public ChatFragment() {
		chatRecents = new ArrayList<ChatRecent>();
	}

	public ChatFragment(Context context) {
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.fragment_chat, container, false);
		// 設置背景
//		Bitmap background = SingletonBitmapClass.getInstance()
//				.getBackgroundBitmap();
//		ViewCompat.setBackground(view, new BitmapDrawable(getResources(),
//				background));
		return view;
	}

	public static final ChatFragment newInstance(String key, Context mContext){
		ChatFragment fragment = new ChatFragment(mContext);
        Bundle bundle = new Bundle();
        bundle.putString("Tag", key);
        fragment.setArguments(bundle);
        return fragment;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		EventBus.getDefault().register(this);//事件总线
	}
	
	@Override
	public void onDetach() {
		EventBus.getDefault().unregister(this);
		super.onDetach();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	private void initView() {
		initTopBarForOnlyTitle("聊天");
		etMsgSearch = (ClearEditText) findViewById(R.id.et_msg_search);
		etMsgSearch.addTextChangedListener(new TextChangedListener());
		listview = (ListView) findViewById(R.id.lv);
		User user = UserManager.getInstance(context).getCurrentUser();
		if(user == null || TextUtils.isEmpty(user.getToken())){
			return;
		}
		adapter = new ChatRecentAdapter(getActivity(),
				R.layout.item_conversation, ChatDBManager.create(getActivity())
						.queryRecents(
								user.getPlayer().getUuid(), 0));
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		listview.setDivider(null);
		listview.setDividerHeight(5);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden) {
			refresh();
		}
	}

	private class TextChangedListener implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO 自动生成的方法存根

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO 自动生成的方法存根

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO 自动生成的方法存根
			if (s.length() != 0) {
				final String nickKey = etMsgSearch.getText().toString();
				try {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {

							adapter = new ChatRecentAdapter(getActivity(),
									R.layout.item_conversation, ChatDBManager
											.create(getActivity())
											.queryRecents(nickKey, 1));
							listview.setAdapter(adapter);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				refresh();
			}
		}

	}

	public void refresh() {
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					adapter = new ChatRecentAdapter(getActivity(),
							R.layout.item_conversation, ChatDBManager.create(
									getActivity()).queryRecents(
											UserManager.getInstance(context)
											.getCurrentUser().getPlayer().getUuid(), 0));
					listview.setAdapter(adapter);
				}
			});
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		ChatRecent recent = (ChatRecent) adapter.getItem(position);
		// 重置未读消息(目前是靠phone)
		ChatDBManager.create(getActivity()).resetUnread(recent.getTargetid(),recent.getTag());
		// 组装聊天对象
		User user = new User();
		Player player = new Player();
		player.setName(recent.getNick());
		player.setPicture(recent.getAvatar());
		player.setUuid(recent.getUserName());
		player.setAccountName(recent.getTargetid());
		user.setPlayer(player);

		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra(ChatConstants.INTENT_USER_BEAN, user);
		intent.putExtra(ChatConstants.TAG, recent.getTag());
		startAnimActivity(intent);

		MainActivity.iv_recent_tips.setVisibility(View.INVISIBLE);
		
		if (MyMessageReceiver.notificationManager != null) {//清除推送栏消息
			MyMessageReceiver.notificationManager.cancelAll();
		}
	}

	private boolean hidden;

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}
	
	public void onEventMainThread(RefreshEvent event){
		refresh();
	}
	
	
}
