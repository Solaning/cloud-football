package com.kinth.football.chat.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kinth.football.MyMessageReceiver;
import com.kinth.football.R;
import com.kinth.football.chat.bean.FaceText;
import com.kinth.football.bean.User;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.chat.ConnectionImp;
import com.kinth.football.chat.MyChatManager;
import com.kinth.football.chat.OnRecordChangeListener;
import com.kinth.football.chat.XmppManager;
import com.kinth.football.chat.adapter.EmoViewPagerAdapter;
import com.kinth.football.chat.adapter.EmoteAdapter;
import com.kinth.football.chat.adapter.MessageChatAdapter;
import com.kinth.football.chat.bean.ChatMsg;
import com.kinth.football.chat.db.ChatDBManager;
import com.kinth.football.chat.listener.UploadListener;
import com.kinth.football.chat.util.CommonUtils;
import com.kinth.football.chat.util.CompressListener;
import com.kinth.football.chat.util.ImageUtil;
import com.kinth.football.chat.util.VoiceRecorder;
import com.kinth.football.config.Config;
import com.kinth.football.listener.EventListener;
import com.kinth.football.listener.SendMsgListener;
import com.kinth.football.manager.UserManager;
import com.kinth.football.manager.UserSharedPreferences;
import com.kinth.football.ui.ActivityBase;
import com.kinth.football.ui.MainActivity;
import com.kinth.football.ui.team.formation.FormationConstants;
import com.kinth.football.ui.team.formation.showtipsview.ShowTipsBuilder;
import com.kinth.football.ui.team.formation.showtipsview.ShowTipsView;
import com.kinth.football.ui.team.formation.showtipsview.ShowTipsViewInterface;
import com.kinth.football.chat.util.FaceTextUtils;
import com.kinth.football.util.LogUtil;
import com.kinth.football.view.EmoticonsEditText;
import com.kinth.football.view.xlist.XListView;
import com.kinth.football.view.xlist.XListView.IXListViewListener;

/**
 * 聊天界面
 * @author Administrator
 *
 */
public class ChatActivity extends ActivityBase implements OnClickListener,
		IXListViewListener, EventListener, SendMsgListener{

	private XListView mListView; // 下拉刷新列表
	private EmoticonsEditText etUserComment; // 表情编辑框
	private ViewPager pagerEmo; // 表情视图滑动
	private Button btnChatEmo = null; // 表情按钮
	private Button btnChatSend = null; // 发送按钮
	private Button btnChatAdd = null; // 更多按钮
	private Button btnChatKeyboard = null; // 键盘输入按钮
	private Button btnSpeak = null; // 语音输入按钮
	private Button btnChatVoice = null; // 开始语音按钮
	private TextView tvPicture = null; // 发送图片按钮
	private TextView tvCamera = null; // 拍照发送按钮
	private TextView tvFormation = null; // 发送位置按钮
	private TextView tvVoiceTips = null; // 录音提示
	private ImageView ivRecord = null; // 录音中图片
	private ImageView ivRecordPermission = null;
	private TextView tvTeamMessage = null;//查看球队消息入口
	private TextView tvGroupMessag = null;//查看群聊消息入口
	private LinearLayout layoutMore = null; // 表情选项和更多选项外部的布局
	private LinearLayout layoutEmo = null; // 显示表情选项的布局
	private LinearLayout layoutAdd = null; // 显示更多选项的布局
	private RelativeLayout layoutRecord = null; // 中间显示录音中的布局
	private RelativeLayout layoutUserComment = null; // 编辑框及下划线的布局
	private Dialog dlgLoadingImg; // 发送图片时显示的进度圈

	private User targetUser; // 接受者User对象
	private String formationDetailPath; // 阵容详情截图的本地路径
	private ChatMsg relayMsg;//转发的消息对象

	private String targetId = ""; // 接受者ID
	private String localCameraPath = "";// 拍照后得到的图片地址
	private int tag = 0; //聊天类型标识：群聊/单聊
	private static int MsgPagerNum = 0; // 信息页数
	public static final int NEW_MESSAGE = 0x001;// 收到消息

	private List<ChatMsg> chatMsgs; // 聊天信息列表
	private List<FaceText> emos; // 表情列表

	private MessageChatAdapter mAdapter; // 聊天适配器

	private VoiceRecorder recordManager;
	private MyChatManager chatManager;

	private Drawable[] drawableAnims;// 话筒动画

	private Toast toast;

	private UserSharedPreferences userSharedPreferences;
	private RefreshBrocastReceiver receiver;

	public static SparseArray<String> picUrls;//聊天记录里所有图片的集合
	public int picPosition = 0;//选中图片的索引

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		initData();
		initView();
		showTip();
		if (formationDetailPath != null) { // 通过是否为空判断是否是从阵容详情界面跳转过来的
			sendImageMessage(formationDetailPath); // 发送选中的阵容详情图片
		}
		if(relayMsg != null){//转发消息
			sendRelayMsg(relayMsg);
		}
	}

	private void initData() {
		// TODO 自动生成的方法存根
		// 组装聊天对象
		targetUser = getIntent().getParcelableExtra(
				ChatConstants.INTENT_USER_BEAN);
		targetId = targetUser.getPlayer().getAccountName();

		formationDetailPath = getIntent().getStringExtra(
				FormationConstants.INTENT_FORMATION_DETAIL_PATH);
		tag = getIntent().getIntExtra(ChatConstants.TAG,0);

		chatMsgs = new ArrayList<ChatMsg>();
		chatManager = MyChatManager.getInstance(this);

		userSharedPreferences = new UserSharedPreferences(this,
				this.getSharedPreferences("chat", MODE_PRIVATE));
		
		relayMsg = (ChatMsg) getIntent().getSerializableExtra(ChatConstants.INTENT_CHATMSG_BEAN);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction("chat.msg.del");
		filter.addAction("chat.msg.resend");
		filter.addAction("chat.msg.playrecord");
		receiver = new RefreshBrocastReceiver();
		registerReceiver(receiver, filter);
		
	}

	private void initView() {
		initHeader();
		initBottomView();
		initXListView();
		initVoiceView();
	}

	/**
	 * 初始化标题栏，显示聊天对象的名字
	 */
	private void initHeader() {
		// TODO 自动生成的方法存根
		initTopBarForLeft(targetUser.getPlayer().getName());
		if(targetUser.getPlayer().getName().equals("云球团队"))
			findViewById(R.id.include_chat_bottom_bar).setVisibility(View.GONE);
		tvTeamMessage = (TextView) findViewById(R.id.tv_team_msg);
		tvGroupMessag = (TextView) findViewById(R.id.tv_group_msg);
		if(tag==ChatConstants.TAG_GROUP){
			tvGroupMessag.setTextSize(20f);
			tvGroupMessag.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
			tvGroupMessag.getPaint().setFakeBoldText(true);//加粗
			tvTeamMessage.setTextSize(16f);
			tvTeamMessage.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
			tvTeamMessage.getPaint().setFakeBoldText(false);//加粗
			findViewById(R.id.llt_message_type).setVisibility(View.VISIBLE);
		}else if(tag==ChatConstants.TAG_TEAM){
			tvTeamMessage.setTextSize(20f);
			tvTeamMessage.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
			tvTeamMessage.getPaint().setFakeBoldText(true);//加粗
			tvGroupMessag.setTextSize(16f);
			tvGroupMessag.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
			tvGroupMessag.getPaint().setFakeBoldText(false);//加粗
			findViewById(R.id.include_chat_bottom_bar).setVisibility(View.GONE);
		}else{
			findViewById(R.id.llt_message_type).setVisibility(View.GONE);
		}
		
		tvTeamMessage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				targetId = targetId + "@team";
				tag = ChatConstants.TAG_TEAM;
				chatMsgs = initMsgData();//一定要这一步更新chatMsgs
				mAdapter.setList(chatMsgs);
				tvTeamMessage.setTextSize(20f);
				tvTeamMessage.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
				tvTeamMessage.getPaint().setFakeBoldText(true);//加粗
				tvGroupMessag.setTextSize(16f);
				tvGroupMessag.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
				tvGroupMessag.getPaint().setFakeBoldText(false);//加粗
				findViewById(R.id.include_chat_bottom_bar).setVisibility(View.GONE);
			}
			
		});
		tvGroupMessag.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				targetId = targetId.replace("@team", "");
				tag = ChatConstants.TAG_GROUP;
				chatMsgs = initMsgData();//一定要这一步更新chatMsgs
				mAdapter.setList(chatMsgs);
				tvGroupMessag.setTextSize(20f);
				tvGroupMessag.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
				tvGroupMessag.getPaint().setFakeBoldText(true);//加粗
				tvTeamMessage.setTextSize(16f);
				tvTeamMessage.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
				tvTeamMessage.getPaint().setFakeBoldText(false);//加粗
				findViewById(R.id.include_chat_bottom_bar).setVisibility(View.VISIBLE);
			}
			
		});
	}

	/**
	 * 初始化底部的聊天工具栏
	 */
	private void initBottomView() {
		etUserComment = (EmoticonsEditText) findViewById(R.id.edit_user_comment);
		btnChatAdd = (Button) findViewById(R.id.btn_chat_add);
		btnChatEmo = (Button) findViewById(R.id.btn_chat_emo);
		btnChatKeyboard = (Button) findViewById(R.id.btn_chat_keyboard);
		btnChatVoice = (Button) findViewById(R.id.btn_chat_voice);
		btnChatSend = (Button) findViewById(R.id.btn_chat_send);
		btnSpeak = (Button) findViewById(R.id.btn_speak);
		ivRecordPermission = (ImageView) findViewById(R.id.iv_record_permission);
		layoutMore = (LinearLayout) findViewById(R.id.layout_more);
		layoutEmo = (LinearLayout) findViewById(R.id.layout_emo);
		layoutAdd = (LinearLayout) findViewById(R.id.layout_add);
		layoutUserComment = (RelativeLayout) findViewById(R.id.rl_user_comment);

		String unSendTxt = userSharedPreferences.getValue(
				"chat_unsend_txt@"+UserManager.getInstance(this).getCurrentUser().getPlayer().getAccountName()+"&"+targetId, "");
		etUserComment.setText(unSendTxt);
		
		btnChatAdd.setOnClickListener(this);
		btnChatEmo.setOnClickListener(this);
		btnChatVoice.setOnClickListener(this);
		btnChatKeyboard.setOnClickListener(this);
		btnChatSend.setOnClickListener(this);
		etUserComment.setOnClickListener(this);
		etUserComment.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!TextUtils.isEmpty(s)) {// 一旦输入文字，则显示发送按钮
					btnChatSend.setVisibility(View.VISIBLE);
					btnChatKeyboard.setVisibility(View.GONE);
					btnChatAdd.setVisibility(View.GONE);
				} else {
					if (btnChatAdd.getVisibility() != View.VISIBLE) {
						btnChatAdd.setVisibility(View.VISIBLE);
						btnChatSend.setVisibility(View.GONE);
						btnChatKeyboard.setVisibility(View.GONE);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		initAddView();
		initEmoView();
	}

	/**
	 * 初始化【更多】按钮点击后的布局
	 */
	private void initAddView() {
		tvPicture = (TextView) findViewById(R.id.tv_picture);
		tvCamera = (TextView) findViewById(R.id.tv_camera);
		tvFormation = (TextView) findViewById(R.id.tv_formation);
		tvPicture.setOnClickListener(this);
		tvFormation.setOnClickListener(this);
		tvCamera.setOnClickListener(this);
	}

	/**
	 * 初始化表情布局
	 */
	private void initEmoView() {
		pagerEmo = (ViewPager) findViewById(R.id.pager_emo);
		emos = FaceTextUtils.faceTexts;

		List<View> views = new ArrayList<View>();
		for (int i = 0; i < 2; ++i) {
			views.add(getGridView(i));
		}

		pagerEmo.setAdapter(new EmoViewPagerAdapter(views));
	}

	/**
	 * 获得装载表情的GridView
	 * 
	 * @param i
	 * @return
	 */
	private View getGridView(final int i) {
		View view = View.inflate(this, R.layout.include_emo_gridview, null);
		GridView gridview = (GridView) view.findViewById(R.id.gridview);
		List<FaceText> list = new ArrayList<FaceText>();
		if (i == 0) {
			list.addAll(emos.subList(0, 21));
		} else if (i == 1) {
			list.addAll(emos.subList(21, emos.size()));
		}
		final EmoteAdapter gridAdapter = new EmoteAdapter(ChatActivity.this,
				list);
		gridview.setAdapter(gridAdapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				FaceText name = (FaceText) gridAdapter.getItem(position);
				String key = name.text.toString();
				try {
					if (etUserComment != null && !TextUtils.isEmpty(key)) {
						int start = etUserComment.getSelectionStart();
						CharSequence content = etUserComment.getText().insert(
								start, key);
						etUserComment.setText(content);
						// 定位光标位置
						CharSequence info = etUserComment.getText();
						if (info instanceof Spannable) {
							Spannable spanText = (Spannable) info;
							Selection.setSelection(spanText,
									start + key.length());
						}
					}
				} catch (Exception e) {

				}

			}
		});
		return view;
	}

	/**
	 * 初始化消息列表
	 */
	private void initXListView() {
		mListView = (XListView) findViewById(R.id.mListView);
		mListView.setPullLoadEnable(false);// 首先不允许加载更多
		mListView.setPullRefreshEnable(true);// 允许下拉
		mListView.setXListViewListener(this);// 设置监听器
		mListView.pullRefreshing();// 下拉刷新
		mListView.setDividerHeight(0);// 每一项间隔高度为0
		
		ChatMsg latestMsg = ChatDBManager.create(this).getLatestMessage(targetId);
		if(latestMsg!=null){
			if(latestMsg.getConversationId().contains("@team")){
				targetId = targetId + "@team";
				tag = ChatConstants.TAG_TEAM;
				tvTeamMessage.setTextSize(20f);
				tvTeamMessage.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
				tvTeamMessage.getPaint().setFakeBoldText(true);//加粗
				tvGroupMessag.setTextSize(16f);
				tvGroupMessag.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//加粗
				tvGroupMessag.getPaint().setFakeBoldText(false);//加粗
				findViewById(R.id.include_chat_bottom_bar).setVisibility(View.GONE);
			}
		}
		initOrRefresh();// 加载数据
		
		mListView.setSelection(mAdapter.getCount() - 1);// 定位到最后一项
		mListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				hideSoftInputView(); // 隐藏软键盘
				layoutMore.setVisibility(View.GONE);
				layoutAdd.setVisibility(View.GONE);
				btnChatAdd.setVisibility(View.VISIBLE);
				btnChatSend.setVisibility(View.GONE);
				if (btnSpeak.getVisibility() == View.VISIBLE) {
					btnChatVoice.setVisibility(View.GONE);
					btnChatKeyboard.setVisibility(View.VISIBLE);
				} else {
					btnChatVoice.setVisibility(View.VISIBLE);
					btnChatKeyboard.setVisibility(View.GONE);
				}

				return false;
			}
		});
	}

	/**
	 * 界面刷新
	 */
	public void initOrRefresh() {
		if (mAdapter != null) {
			int news = ChatDBManager.create(this).getUnreadCount(targetId, tag);// 有可能锁屏期间，来了N条消息,因此需要倒叙显示在界面上
			if (news != 0) {// 用于更新当在聊天界面锁屏期间来了消息，这时再回到聊天页面的时候需要显示新来的消息
				int size = initMsgData().size();
				for (int i = (news - 1); i >= 0; i--) {
					mAdapter.add(initMsgData().get(size - (i + 1)));// 添加最后一条消息到界面显示
				}
				mListView.setSelection(mAdapter.getCount() - 1);
				ChatDBManager.create(this).resetUnread(targetId, tag);// 重置未读消息，显示为已读
			} else {
				mAdapter.notifyDataSetChanged();
			}
		} else {
			chatMsgs = initMsgData();
			mAdapter = new MessageChatAdapter(this, chatMsgs);
			mListView.setAdapter(mAdapter);
		}
	}

	/**
	 * 加载消息历史，从数据库中读出
	 */
	private List<ChatMsg> initMsgData() {
		List<ChatMsg> list = ChatDBManager.create(this).queryMessages(targetId,
				MsgPagerNum, tag);
		picUrls = new SparseArray<String>();
		for (int i = 0; i < list.size(); i++) {
			ChatMsg chatMsg = list.get(i);
			if (chatMsg.getMsgType().equals(ChatConstants.MSG_TYPE_IMAGE)) {
				picUrls.put(picPosition++, chatMsg.getContent());
			}
		}
		return list;
	}

	/**
	 * 初始化语音布局
	 */
	private void initVoiceView() {
		layoutRecord = (RelativeLayout) findViewById(R.id.layout_record);
		tvVoiceTips = (TextView) findViewById(R.id.tv_voice_tips);
		ivRecord = (ImageView) findViewById(R.id.iv_record);
		btnSpeak.setOnTouchListener(new VoiceTouchListen());

		initVoiceAnimRes();
		initRecordManager();
	}

	/**
	 * 长按说话
	 * 
	 * @ClassName: VoiceTouchListen
	 * @Description: TODO
	 * @author smile
	 * @date 2014-7-1 下午6:10:16
	 */
	class VoiceTouchListen implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonUtils.checkSdCard()) {
					ShowToast("发送语音需要sdcard支持！");
					return false;
				}
				try {
					v.setPressed(true);
					layoutRecord.setVisibility(View.VISIBLE);
					tvVoiceTips.setText(getString(R.string.voice_cancel_tips));
					// 开始录音
					recordManager.startRecording(ChatActivity.this);
				} catch (Exception e) {
					ShowToast("录音失败，请检查是否被第三方软件禁止权限");
				}
				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					tvVoiceTips.setText(getString(R.string.voice_cancel_tips));
					tvVoiceTips.setTextColor(Color.RED);
				} else {
					tvVoiceTips.setText(getString(R.string.voice_up_tips));
					tvVoiceTips.setTextColor(Color.WHITE);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				layoutRecord.setVisibility(View.INVISIBLE);
				try {
					if (event.getY() < 0) {// 放弃录音
						recordManager.cancelRecording();
					} else {
						int recordTime = recordManager.stopRecording();
						if (recordTime > 1) { // 录音时间超过一秒
							// 发送语音文件
							sendVoiceMessage(recordManager.getRecordFilePath(),
									recordTime);
						} else {// 录音时间过短，则提示录音过短的提示
							layoutRecord.setVisibility(View.GONE);
							showShortToast().show();
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				return true;
			default:
				return false;
			}
		}
	}

	/**
	 * 显示录音时间过短的Toast
	 * 
	 * @Title: showShortToast
	 * @return void
	 * @throws
	 */
	private Toast showShortToast() {
		if (toast == null) {
			toast = new Toast(this);
		}
		View view = LayoutInflater.from(this).inflate(
				R.layout.include_chat_voice_short, null);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(50);
		return toast;
	}

	/**
	 * 发送语音消息
	 * 
	 * @Title: sendVoiceMessage
	 * @Description: TODO
	 * @param @param localPath
	 * @return void
	 * @throws
	 */
	private void sendVoiceMessage(String local, int length) {
		chatManager.sendVoiceMessage(targetUser, local, length, tag,
				new UploadListener() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(ChatMsg msg) {
						// TODO Auto-generated method stub
						XmppManager.getInstance(getApplicationContext())
								.sendTextMessage(targetUser, msg,
										ChatActivity.this);
						refreshMessage(msg);
					}

					@Override
					public void onFailure(int error, String arg1) {
						// TODO Auto-generated method stub
						mAdapter.notifyDataSetChanged();
					}
				});
	}

	/**
	 * 初始化语音动画资源
	 * 
	 * @Title: initVoiceAnimRes
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initVoiceAnimRes() {
		drawableAnims = new Drawable[] {
				getResources().getDrawable(R.drawable.chat_icon_voice2),
				getResources().getDrawable(R.drawable.chat_icon_voice3),
				getResources().getDrawable(R.drawable.chat_icon_voice4),
				getResources().getDrawable(R.drawable.chat_icon_voice5),
				getResources().getDrawable(R.drawable.chat_icon_voice6) };
	}

	/**
	 * 初始化语音相关管理器
	 */
	private void initRecordManager() {
		recordManager = new VoiceRecorder(handler);
		// 设置音量大小监听--在这里开发者可以自己实现：当剩余10秒情况下的给用户的提示，类似微信的语音那样
		recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {

			@Override
			public void onVolumnChanged(int value) { // 检测到音量变化，则显示不同的音量图
				// TODO Auto-generated method stub
				ivRecord.setImageDrawable(drawableAnims[value]);
			}

			@Override
			public void onTimeChanged(int recordTime, String localPath) {
				// TODO Auto-generated method stub
				if (recordTime >= VoiceRecorder.MAX_RECORD_TIME) {// 1分钟结束，发送消息
					// 需要重置按钮
					btnSpeak.setPressed(false);
					btnSpeak.setClickable(false);
					// 取消录音框
					layoutRecord.setVisibility(View.INVISIBLE);
					// 发送语音消息
					sendVoiceMessage(localPath, recordTime);
					// 是为了防止过了录音时间后，会多发一条语音出去的情况。
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							btnSpeak.setClickable(true);
						}
					}, 1000);
				} else {

				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.edit_user_comment:// 点击文本输入框
			break;
		case R.id.btn_chat_emo:// 点击笑脸图标
			if (layoutMore.getVisibility() == View.GONE) {
				showEditState(true);
			} else {
				if (layoutAdd.getVisibility() == View.VISIBLE) {
					layoutAdd.setVisibility(View.GONE);
					layoutEmo.setVisibility(View.VISIBLE);
				} else {
					layoutMore.setVisibility(View.GONE);
				}
			}
			break;
		case R.id.btn_chat_add:// 添加按钮-显示图片、拍照、位置
			if (layoutMore.getVisibility() == View.GONE) {
				layoutMore.setVisibility(View.VISIBLE);
				layoutAdd.setVisibility(View.VISIBLE);
				layoutEmo.setVisibility(View.GONE);
				hideSoftInputView();
			} else {
				if (layoutEmo.getVisibility() == View.VISIBLE) {
					layoutEmo.setVisibility(View.GONE);
					layoutAdd.setVisibility(View.VISIBLE);
				} else {
					layoutMore.setVisibility(View.GONE);
				}
			}
			break;
		case R.id.btn_chat_voice:// 语音按钮
			etUserComment.setVisibility(View.GONE);
			layoutMore.setVisibility(View.GONE);
			btnChatVoice.setVisibility(View.GONE);
			btnChatKeyboard.setVisibility(View.VISIBLE);
			btnSpeak.setVisibility(View.VISIBLE);
			layoutUserComment.setVisibility(View.GONE);
			hideSoftInputView();

			/**
			 * 这段代码是为了解决360阻止权限后程序崩溃的Bug，在这里先激发360弹窗，
			 * 之后无论是允许还是拒绝，按下【长按说话】按钮就不会卡在那里然后导致崩溃 但是这里就会有一点卡顿
			 */
			try {
				recordManager.startRecording(ChatActivity.this);
			} catch (Exception e) {
				ShowToast("初始化录音组件失败，请检查是否被第三方软件禁止权限");
			}
			recordManager.cancelRecording();
			break;
		case R.id.btn_chat_keyboard:// 键盘按钮，点击就弹出键盘并隐藏掉声音按钮
			showEditState(false);
			break;
		case R.id.btn_chat_send:// 发送文本
			String msg = etUserComment.getText().toString();
			if (msg.equals("")) {
				ShowToast("请输入发送消息!");
				return;
			}
			sendTextMessage(msg);
			break;
		case R.id.tv_camera:// 拍照
			selectImageFromCamera();
			break;
		case R.id.tv_picture:// 图片
			selectImageFromLocal();
			break;
		case R.id.tv_formation:// 阵容
			toChooseTeam();
			break;
		default:
			break;
		}
	}

	/**
	 * 根据是否点击笑脸来显示文本输入框的状态
	 */
	private void showEditState(boolean isEmo) {
		etUserComment.setVisibility(View.VISIBLE);
		btnChatKeyboard.setVisibility(View.GONE);
		btnChatVoice.setVisibility(View.VISIBLE);
		layoutUserComment.setVisibility(View.VISIBLE);
		btnSpeak.setVisibility(View.GONE);
		etUserComment.requestFocus();
		if (isEmo) {
			layoutMore.setVisibility(View.VISIBLE);
			layoutMore.setVisibility(View.VISIBLE);
			layoutEmo.setVisibility(View.VISIBLE);
			layoutAdd.setVisibility(View.GONE);
			hideSoftInputView();
		} else {
			layoutMore.setVisibility(View.GONE);
			showSoftInputView();
		}
	}

	/**
	 * 发送文字消息
	 * 
	 * @param msg
	 *            文本内容
	 */
	private void sendTextMessage(String msg) {
		// TODO 自动生成的方法存根
		boolean isNetConnected = CommonUtils.isNetworkAvailable(this);// 判断网络，以免异常
		if (!isNetConnected)
			ShowToast(R.string.network_tips);

		ChatMsg message = ChatMsg.createTextSendMsg(this, targetId, msg, tag);// 组装聊天实体类
		XmppManager.getInstance(getApplicationContext()).sendTextMessage(
				targetUser, message, this);// 发送文字消息
		refreshMessage(message);// 刷新界面
	}

	/**
	 * 提示用户打开被第三方软件禁止的权限
	 */
	private void showTip() {
		// TODO 自动生成的方法存根
		String isNeedTip = userSharedPreferences.getValue(
				"record_permission_tip", "0");
		if (isNeedTip.equals("0")) {
			ivRecordPermission.setVisibility(View.VISIBLE);
			ShowTipsView showtips = new ShowTipsBuilder(this)
					.setTarget(ivRecordPermission).setTitle("录音权限")
					.setTitleColor(Color.WHITE)
					.setDescription("为保证录音正常进行，请将第三方软件限制的录音权限设置为“一直允许”")
					.setDescriptionColor(Color.LTGRAY).setDelay(500)
					.setCircleColor(Color.WHITE)
					.setCallback(new ShowTipsViewInterface() {

						@Override
						public void gotItClicked() {
							// TODO 自动生成的方法存根
							ivRecordPermission.setVisibility(View.GONE);
						}
					}).build();

			showtips.show(this);
			userSharedPreferences.saveString("record_permission_tip", "1");
		}
	}

	/**
	 * 刷新界面
	 */
	private void refreshMessage(ChatMsg msg) {
		// 更新界面
		mAdapter.add(msg);
		mListView.setSelection(mAdapter.getCount() - 1);
		etUserComment.setText("");
	}

	/**
	 * 启动相机拍照
	 */
	public void selectImageFromCamera() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		File dir = new File(Config.PIC_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, String.valueOf(System.currentTimeMillis())
				+ ".jpg"); // 以当前时间命名
		localCameraPath = file.getPath();

		Uri imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent,
				ChatConstants.REQUESTCODE_TAKE_CAMERA);
	}

	/**
	 * 从系统图库选择图片
	 */
	public void selectImageFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, ChatConstants.REQUESTCODE_TAKE_LOCAL);
	}

	/**
	 * 发送阵容之前需要选择一个球队
	 */
	private void toChooseTeam() {
		Intent intent = new Intent(this, TeamActivity.class);
		intent.putExtra(ChatConstants.INTENT_USER_BEAN, targetUser);
		startActivity(intent);
		finish();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ChatConstants.REQUESTCODE_TAKE_CAMERA:
				ImageUtil.getCompressedImage(localCameraPath,// 先进行图片压缩再发送
						new CompressListener() {

							@Override
							public void CompressSuccess(String imagePath) {
								// TODO 自动生成的方法存根
								sendImageMessage(imagePath);
							}

							@Override
							public void CompressFail(String failReason) {
								// TODO 自动生成的方法存根
								Toast.makeText(mContext, failReason,
										Toast.LENGTH_LONG).show();
							}
						}, 500);
				break;
			case ChatConstants.REQUESTCODE_TAKE_LOCAL:
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						Cursor cursor = getContentResolver().query(
								selectedImage, null, null, null, null);
						cursor.moveToFirst();
						int columnIndex = cursor.getColumnIndex("_data");
						String localSelectPath = cursor.getString(columnIndex);
						cursor.close();
						if (localSelectPath == null
								|| localSelectPath.equals("null")) {
							ShowToast("找不到您想要的图片");
							return;
						}
						/**
						 * 三星有些手机上传不了图片，报FileNotFound错误，应该是压缩未完成它就上传图片
						 * 所以在此处使用回调函数，保证图片压缩完才上传
						 */
						ImageUtil.getCompressedImage(localSelectPath,
								new CompressListener() {

									@Override
									public void CompressSuccess(String imagePath) {
										// TODO 自动生成的方法存根
										sendImageMessage(imagePath);
									}

									@Override
									public void CompressFail(String failReason) {
										// TODO 自动生成的方法存根
										Toast.makeText(mContext, failReason,
												Toast.LENGTH_LONG).show();
									}
								}, 500);
					}
				}
				break;

			}
		}
	}

	/**
	 * 发送图片消息
	 */
	private void sendImageMessage(final String local) {
		if (layoutMore.getVisibility() == View.VISIBLE) {
			layoutMore.setVisibility(View.GONE);
			layoutAdd.setVisibility(View.GONE);
			layoutEmo.setVisibility(View.GONE);
		}

		MyChatManager.getInstance(this).sendImageMessage(targetUser, local,
				tag, new UploadListener() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						/**
						 * 因为发送图片时需要将图片上传到服务器，过程比较耗时，
						 * 客户端会有一段时间的卡顿，所以在开始上传图片之前显示一个环形进度条， 等上传成功后再关闭，从而优化用户体验
						 */
						showLoadingDialog();
					}

					@Override
					public void onSuccess(ChatMsg msg) {
						// TODO Auto-generated method stub
						XmppManager.getInstance(getApplicationContext())
								.sendTextMessage(targetUser, msg,
										ChatActivity.this);
						refreshMessage(msg);
						dlgLoadingImg.dismiss();
						
						picUrls.put(picPosition++, msg.getContent());
					}

					@Override
					public void onFailure(int error, String arg1) {
						// TODO Auto-generated method stub
						mAdapter.notifyDataSetChanged();
						dlgLoadingImg.dismiss();
					}
				});

	}

	private void showLoadingDialog() {
		View lLayout = getLayoutInflater().inflate(R.layout.loading_img, null);

		dlgLoadingImg = new Dialog(mContext, R.style.dialog);
		dlgLoadingImg.setContentView(lLayout);
		dlgLoadingImg.setCanceledOnTouchOutside(false); // 不能通过点击黑色背景关闭对话框
		dlgLoadingImg.show();
		dlgLoadingImg.getWindow().setGravity(Gravity.CENTER);

	}

	/**
	 * 显示软键盘
	 */
	public void showSoftInputView() {
		if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.showSoftInput(etUserComment, 0);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initOrRefresh();// 新消息到达，重新刷新界面
		MyMessageReceiver.ehList.add(this);// 监听推送的消息

		loginChatServ();
	}

	/**
	 * 登录XMPP服务器并添加聊天监听器
	 */
	private void loginChatServ() {
		User user = UserManager.getInstance(this).getCurrentUser();
		if (user == null) {
			ShowToast("用户为空");
		}
		XmppManager.getInstance(getApplicationContext())
				.loginAndAddChatManagerListener(getApplicationContext(),
						user.getPlayer().getAccountName(), user.getToken(),
						new ConnectionImp() {
							@Override
							public void onSucc() {
								LogUtil.i("登录成功");
							}

							@Override
							public void onFail() {
								LogUtil.i("登录失败");
							}
						});
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyMessageReceiver.ehList.remove(this);// 不在当前页面，则移除监听
		ChatDBManager.create(ChatActivity.this).resetUnread(targetId, tag);// 重置未读消息,避免明明在当前聊天界面接受到信息了ChatFragment那里还是显示未读的情况
		MsgPagerNum = 0;//重新只显示20条聊天记录
		if(userSharedPreferences!=null)	//保存未发送消息
			userSharedPreferences.saveString("chat_unsend_txt@"+UserManager.getInstance(this).getCurrentUser().getPlayer().getAccountName()+"&"+targetId, etUserComment.getText().toString());
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == NEW_MESSAGE) {
				ChatMsg message = (ChatMsg) msg.obj;
				if (message == null)
					return;
				String conversationId = message.getConversationId();
				String uid = message.getBelongId();
				if (message.getTag()==ChatConstants.TAG_GROUP) {// 先判断是不是群聊
					if (!conversationId.split("&")[1].equals(targetId))// 来的消息中的conversationId不包含球队Uuid，表明不是群聊，聊天内容不显示在群聊上
						return;
				} else {// 非群聊
					if (!uid.equals(targetId))// 如果不是当前正在聊天对象的消息，不处理
						return;
					// 而如果是群消息的话,uid是发送者，但targetId是球队Uuid，本来就不匹配
				}
				mAdapter.add(message);
				mListView.setSelection(mAdapter.getCount() - 1);

				// 重置未读消息,避免明明在当前聊天界面接受到信息了ChatFragment那里还是显示未读的情况
				ChatDBManager.create(ChatActivity.this).resetUnread(targetId, tag);

				if (MainActivity.iv_recent_tips != null)
					MainActivity.iv_recent_tips.setVisibility(View.INVISIBLE);
			}
		}
	};

	@Override
	public void onMessage(ChatMsg message) {
		Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
		handlerMsg.obj = message;
		handler.sendMessage(handlerMsg);

		if (message.getMsgType().equals(ChatConstants.MSG_TYPE_IMAGE))
			picUrls.put(picPosition++, message.getContent());
	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		if (!isNetConnected) {
			ShowToast(R.string.network_tips);
		}
	}

	/**
	 * 刷新操作，暂时还只是延迟动画，没做其他什么处理
	 */
	public void onRefresh() {
		// TODO Auto-generated method stub
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				MsgPagerNum++;
				chatMsgs = initMsgData();//一定要这一步更新chatMsgs
				mAdapter.setList(chatMsgs);
				mListView.setSelection(mListView.getCount()-(20*MsgPagerNum));
				mListView.stopRefresh();
			}
		}, 1000);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (layoutMore.getVisibility() == View.VISIBLE) {
				layoutMore.setVisibility(View.GONE);
				return false;
			} else {
				return super.onKeyDown(keyCode, event);
			}
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		hideSoftInputView();
		picUrls.clear();
		unregisterReceiver(receiver);
	}

	/**
	 * 消息发送成功后回调的方法
	 */
	@Override
	public void onSuccess(final ChatMsg chatMsg) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = chatMsgs.size() - 1; i >= 0; i--) {
					ChatMsg tempMsg = chatMsgs.get(i);
					if (chatMsg.getConversationId().equals(
							tempMsg.getConversationId())
							&& chatMsg.getMsgTime()
									.equals(tempMsg.getMsgTime())) {
						chatMsgs.get(i).setStatus(
								ChatConstants.STATUS_SEND_SUCCESS);
						mAdapter.notifyDataSetChanged();
						// 更新数据库
						MyChatManager.getInstance(getApplicationContext())
								.updateMsgStatus(
										ChatConstants.STATUS_SEND_SUCCESS,
										chatMsg);
						break;
					}
				}
			}
		});

	}

	/**
	 * 消息发送失败后回调的方法
	 */
	@Override
	public void onFailure(final ChatMsg chatMsg) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				for (int i = chatMsgs.size() - 1; i >= 0; i--) {
					ChatMsg tempMsg = chatMsgs.get(i);
					if (chatMsg.getConversationId().equals(
							tempMsg.getConversationId())
							&& chatMsg.getMsgTime()
									.equals(tempMsg.getMsgTime())) {
						chatMsgs.get(i).setStatus(
								ChatConstants.STATUS_SEND_FAIL);
						mAdapter.notifyDataSetChanged();
						// 更新数据库
						MyChatManager
								.getInstance(ChatActivity.this)
								.updateMsgStatus(
										ChatConstants.STATUS_SEND_FAIL, chatMsg);
					}
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO 自动生成的方法存根
		// 重置未读消息,避免明明在当前聊天界面接受到信息了ChatFragment那里还是显示未读的情况
		ChatDBManager.create(ChatActivity.this).resetUnread(targetId, tag);
		super.onBackPressed();
	}
	
	/**
	 * 发送转发消息并刷新
	 * @param msg
	 */
	private void sendRelayMsg(ChatMsg msg) {
		// TODO 自动生成的方法存根
		XmppManager.getInstance(getApplicationContext())
		.sendTextMessage(targetUser, msg,
				ChatActivity.this);
		refreshMessage(msg);
	}
	
	public class RefreshBrocastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO 自动生成的方法存根
			if(intent.getAction().equals("chat.msg.del")){
				int position = intent.getIntExtra(ChatConstants.INTENT_DEL_POSITION, 0);
				chatMsgs = initMsgData();//一定要这一步更新chatMsgs
				mAdapter.setList(chatMsgs);
				mListView.setSelection(position+1);//定位到删除项原先的位置，方便用户看到删除效果
			}else if(intent.getAction().equals("chat.msg.resend")){
				ChatMsg msg = (ChatMsg) intent.getSerializableExtra(ChatConstants.INTENT_CHATMSG_BEAN);
				int position = intent.getIntExtra(ChatConstants.INTENT_DEL_POSITION, 0);
				XmppManager.getInstance(getApplicationContext())
				.sendTextMessage(targetUser, msg,
						ChatActivity.this);
				mListView.setSelection(position);
				mAdapter.notifyDataSetChanged();
			}else if(intent.getAction().equals("chat.msg.playrecord")){
				chatMsgs = initMsgData();//一定要这一步更新chatMsgs
				mAdapter.setList(chatMsgs);
			}
		}
		
	}

}
