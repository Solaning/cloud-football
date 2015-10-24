package com.kinth.football.chat.adapter;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.adapter.BaseListAdapter;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.chat.DownloadManager;
import com.kinth.football.chat.NewRecordPlayClickListener;
import com.kinth.football.chat.bean.ChatMsg;
import com.kinth.football.chat.db.ChatDBManager;
import com.kinth.football.chat.listener.DownloadListener;
import com.kinth.football.chat.listener.OnDialogItemOnClickListener;
import com.kinth.football.chat.listener.OnPlayChangeListener;
import com.kinth.football.chat.ui.BiggerTextSizeActivity;
import com.kinth.football.chat.ui.ChatActivity;
import com.kinth.football.chat.ui.RecentActivity;
import com.kinth.football.chat.util.CustomDialogUtil;
import com.kinth.football.chat.util.FaceTextUtils;
import com.kinth.football.chat.util.MD5Util;
import com.kinth.football.chat.util.VoiceRecorder;
import com.kinth.football.config.Config;
import com.kinth.football.dao.Player;
import com.kinth.football.dao.Team;
import com.kinth.football.dao.TeamDao;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.ui.ImageBrowserActivity;
import com.kinth.football.ui.mine.SetMyInfoActivity;
import com.kinth.football.ui.team.TeamInfoActivity;
import com.kinth.football.ui.team.TeamPlayerInfo;
import com.kinth.football.util.DBUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.ImageLoadOptions;
import com.kinth.football.util.TimeUtil;
import com.kinth.football.util.ViewHolder;
import com.kinth.football.view.RoundImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import de.greenrobot.dao.query.QueryBuilder;
import eu.inmite.android.lib.dialogs.IListDialogListener;
import eu.inmite.android.lib.dialogs.ListDialogFragment;

/**
 * 聊天适配器
 * 
 * @author Administrator
 * 
 */
public class MessageChatAdapter extends BaseListAdapter<ChatMsg> {

	// 8种Item的类型
	private final int TYPE_RECEIVER_TXT = 0; // 接收文本
	private final int TYPE_SEND_TXT = 1; // 发送文本
	private final int TYPE_SEND_IMAGE = 2; // 发送图片
	private final int TYPE_RECEIVER_IMAGE = 3; // 接收图片
	// private final int TYPE_SEND_LOCATION = 4; //发送位置
	// private final int TYPE_RECEIVER_LOCATION = 5; //接收位置
	private final int TYPE_SEND_VOICE = 6; // 发送语音
	private final int TYPE_RECEIVER_VOICE = 7; // 接收语音

	private static String currentUserAccountName = UserManager
			.getInstance(CustomApplcation.getInstance()).getCurrentUser()
			.getPlayer().getPhone();// 当前用户通讯用的account-name
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	private CustomDialogUtil cUtil = null;

	public MessageChatAdapter(Context context, List<ChatMsg> msgList) {
		super(context, msgList);
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher)
				.resetViewBeforeLoading(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		cUtil = CustomDialogUtil.getInstance(context);
	}

	/**
	 * 取得消息类型，判断是发送还是接收
	 */
	@Override
	public int getItemViewType(int position) {
		ChatMsg msg = list.get(position);

		if (msg.getMsgType().equals(ChatConstants.MSG_TYPE_IMAGE)) {
			return msg.getBelongId().equals(currentUserAccountName) ? TYPE_SEND_IMAGE
					: TYPE_RECEIVER_IMAGE;
		} else if (msg.getMsgType().equals(ChatConstants.MSG_TYPE_AUDIO)) {
			return msg.getBelongId().equals(currentUserAccountName) ? TYPE_SEND_VOICE
					: TYPE_RECEIVER_VOICE;
		} else {
			return msg.getBelongId().equals(currentUserAccountName) ? TYPE_SEND_TXT
					: TYPE_RECEIVER_TXT;
		}
	}

	/**
	 * 取得消息类型的种类数量
	 */
	@Override
	public int getViewTypeCount() {
		return 8;
	}

	private View createViewByType(ChatMsg message, int position) {

		String type = message.getMsgType();

		// 先判断是哪种类型的文件，再根据是接收还是发送的返回不同的布局文件
		if (type.equals(ChatConstants.MSG_TYPE_IMAGE)) {// 图片类型
			return getItemViewType(position) == TYPE_RECEIVER_IMAGE ? mInflater
					.inflate(R.layout.item_chat_received_image, null)
					: mInflater.inflate(R.layout.item_chat_sent_image, null);
		} else if (type.equals(ChatConstants.MSG_TYPE_AUDIO)) {// 语音类型
			return getItemViewType(position) == TYPE_RECEIVER_VOICE ? mInflater
					.inflate(R.layout.item_chat_received_voice, null)
					: mInflater.inflate(R.layout.item_chat_sent_voice, null);
		} else {// 剩下默认的都是文本
			return getItemViewType(position) == TYPE_RECEIVER_TXT ? mInflater
					.inflate(R.layout.item_chat_received_message, null)
					: mInflater.inflate(R.layout.item_chat_sent_message, null);
		}
	}

	@Override
	public View bindView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ChatMsg item = list.get(position);
		if (convertView == null) {
			convertView = createViewByType(item, position);
		}
		// 文本类型
		RoundImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);// 圆形头像
		final ImageView iv_fail_resend = ViewHolder.get(convertView,
				R.id.iv_fail_resend);// 失败重发
		final TextView tv_send_status = ViewHolder.get(convertView,
				R.id.tv_send_status);// 发送状态
		TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);// 发送时间
		TextView tv_message = ViewHolder.get(convertView, R.id.tv_message);// 文本内容
		// 图片
		ImageView iv_picture = ViewHolder.get(convertView, R.id.iv_picture);
		final ProgressBar progress_load = ViewHolder.get(convertView,
				R.id.progress_load);// 进度条
		// 语音
		final ImageView iv_voice = ViewHolder.get(convertView, R.id.iv_voice);
		final TextView tv_voice_length = ViewHolder.get(convertView,
				R.id.tv_voice_length);// 语音长度

		final ImageView iv_voice_tips = ViewHolder.get(convertView,
				R.id.iv_voice_tips);

		final boolean isTeamExist = DBUtil.isTeamExitByUuid(item
				.getBelongId().replace("@team", ""), UserManager
				.getInstance(mContext).getCurrentUser().getPlayer()
				.getUuid());
		String avatar = item.getBelongAvatar();
		if (avatar != null && !avatar.equals("")) {// 加载头像-为了不每次都加载头像
			if (item.getBelongId().equals("System")) { // 云球团队
				ImageLoader.getInstance().displayImage(avatar.trim(), iv_avatar,
						new DisplayImageOptions.Builder()
				.showImageOnLoading(
						R.drawable.ic_launcher)
				.showImageForEmptyUri(
						R.drawable.ic_launcher)
				.showImageOnFail(
						R.drawable.ic_launcher)
				.cacheInMemory(true)
				.cacheOnDisk(true).build(), animateFirstListener);
			} else if (isTeamExist) {
				ImageLoader.getInstance().displayImage(avatar.trim(), iv_avatar,
						new DisplayImageOptions.Builder()
				.showImageOnLoading(
						R.drawable.team_bage_default)
				.showImageForEmptyUri(
						R.drawable.team_bage_default)
				.showImageOnFail(
						R.drawable.team_bage_default)
				.cacheInMemory(true)
				.cacheOnDisk(true).build(), animateFirstListener);
			} else{
				ImageLoader.getInstance().displayImage(avatar.trim(), iv_avatar,
						new DisplayImageOptions.Builder()
				.showImageOnLoading(
						R.drawable.icon_default_head)
				.showImageForEmptyUri(
						R.drawable.icon_default_head)
				.showImageOnFail(
						R.drawable.icon_default_head)
				.cacheInMemory(true)
				.cacheOnDisk(true).build(), animateFirstListener);
			}
		} else {
			iv_avatar.setImageResource(R.drawable.head);
		}

		// 点击头像进入个人资料（"球员信息"）
		iv_avatar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				if (getItemViewType(position) == TYPE_RECEIVER_TXT
						|| getItemViewType(position) == TYPE_RECEIVER_IMAGE
						|| getItemViewType(position) == TYPE_RECEIVER_VOICE) {
					if (item.getBelongId().equals("System")) { // 云球团队
						// 不处理
					} else if (isTeamExist) {// 球队,不能用tag=group判断，因为球员的普通群聊消息的tag也是group
						intent.setClass(mContext, TeamInfoActivity.class);
						QueryBuilder<Team> teamQB = CustomApplcation
								.getDaoSession(mContext).getTeamDao()
								.queryBuilder();
						teamQB.where(TeamDao.Properties.Uuid.eq(item
								.getBelongId().replace("@team", "")));
						Team team = teamQB.unique();
						if (team == null || TextUtils.isEmpty(team.getUuid())) {
							return;
						}
						intent.putExtra(TeamInfoActivity.INTENT_TEAM_INFO_BEAN,
								team);
						mContext.startActivity(intent);
					} else { // 球员
						String accountName = item.getConversationId()
								.split("&")[0];
						// 先根据accountName获取得到用户的UUID
						getPlayerUidByAccountName(accountName);
					}
				} else {
					intent.setClass(mContext, SetMyInfoActivity.class);
					mContext.startActivity(intent);
				}
			}
		});

		// 以时间差显示
		if (position > 0) {// 从第二条消息开始显示时间差
			long preMsgTime = Long.parseLong(list.get(position - 1)
					.getMsgTime());// 前一条消息时间
			long currentMsgTime = Long.parseLong(list.get(position)
					.getMsgTime());// 当前消息时间
			if ((currentMsgTime - preMsgTime) / 1000 < 120) {// 计算两条消息时间间隔，小于两分钟则不显示时间
				tv_time.setVisibility(View.GONE);
			} else {
				tv_time.setVisibility(View.VISIBLE);
				tv_time.setText(TimeUtil.getChatTime(currentMsgTime));
			}
		} else {// 第一条消息直接显示时间
			tv_time.setText(TimeUtil.getChatTime(Long.parseLong(item
					.getMsgTime())));
		}

		int itemViewType = getItemViewType(position);
		if (itemViewType == TYPE_SEND_TXT
		// ||getItemViewType(position)==TYPE_SEND_IMAGE//图片单独处理
				|| getItemViewType(position) == TYPE_SEND_VOICE) {// 只有自己发送的消息才有重发机制
			// 状态描述
			if (item.getStatus() == ChatConstants.STATUS_SEND_SUCCESS) {// 发送成功
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				if (item.getMsgType().equals(ChatConstants.MSG_TYPE_AUDIO)) {// 如果是语音消息的话，显示语音长度
					tv_send_status.setVisibility(View.GONE);
					tv_voice_length.setVisibility(View.VISIBLE);
				} else {// 如果是文字消息的话则显示发送状态
					tv_send_status.setVisibility(View.VISIBLE);
					tv_send_status.setText("已发送");
				}
			} else if (item.getStatus() == ChatConstants.STATUS_SEND_FAIL) {// 服务器无响应或者查询失败等原因造成的发送失败，均需要重发
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.VISIBLE);// 显示重发按钮
				iv_fail_resend.setOnClickListener(new OnResendClickListener(
						item, position));
				tv_send_status.setVisibility(View.INVISIBLE);
				if (item.getMsgType().equals(ChatConstants.MSG_TYPE_AUDIO)) {
					tv_voice_length.setVisibility(View.GONE);
				}
			} else if (item.getStatus() == ChatConstants.STATUS_SEND_START) {// 开始上传
				progress_load.setVisibility(View.VISIBLE);// 显示发送进度
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
				if (item.getMsgType().equals(ChatConstants.MSG_TYPE_AUDIO)) {
					tv_voice_length.setVisibility(View.GONE);
				}
			}
		}

		// 根据类型显示内容
		final String text = item.getContent();

		int msgTypeInt = getMsgTypeIntValue(item.getMsgType());

		switch (msgTypeInt) {
		case ChatConstants.TYPE_TEXT:
			try {
				SpannableString spannableString = FaceTextUtils
						.toSpannableString(mContext, text);// 转为可显示表情的字符串
				tv_message.setText(spannableString);
				tv_message.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO 自动生成的方法存根
						Intent intent = new Intent(mContext,
								BiggerTextSizeActivity.class);
						intent.putExtra(ChatConstants.INTENT_TEXT_MESSAGE, text);
						mContext.startActivity(intent);
					}

				});
				tv_message
						.setOnLongClickListener(new OnChatItemLongClickListener(
								item, position));
			} catch (Exception e) {
			}
			break;

		case ChatConstants.TYPE_IMAGE:// 图片类
			try {
				if (text != null && !text.equals("")) {// 发送成功之后存储的图片类型的content和接收到的是不一样的
					dealWithImage(position, progress_load, iv_fail_resend,
							tv_send_status, iv_picture, item);
				}
				iv_picture.setOnClickListener(new OnClickListener() {// 浏览大图

							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent(mContext,
										ImageBrowserActivity.class);
								ArrayList<String> list = new ArrayList<String>();
								for (int i = 0; i < ChatActivity.picUrls.size(); i++) {
									list.add(ChatActivity.picUrls.valueAt(i));
								}
								intent.putStringArrayListExtra("photos", list);
								intent.putExtra("position",
										ChatActivity.picUrls.indexOfValue(item
												.getContent()));
								mContext.startActivity(intent);
							}
						});
				iv_picture
						.setOnLongClickListener(new OnChatItemLongClickListener(
								item, position));

			} catch (Exception e) {
			}
			break;

		case ChatConstants.TYPE_VOICE:// 语音消息
			try {
				if (text != null && !text.equals("")) {
					tv_voice_length.setVisibility(View.VISIBLE);
					final String localFilePath = Config.VOICE_DIR
							+ MD5Util.getMD5(text.trim()) + ".amr";
					if (item.getBelongId().equals(currentUserAccountName)) {// 发送的消息
						if (item.getStatus() == ChatConstants.STATUS_SEND_RECEIVERED
								|| item.getStatus() == ChatConstants.STATUS_SEND_SUCCESS) {// 当发送成功或者发送已阅读的时候，则显示语音长度
							tv_voice_length.setVisibility(View.VISIBLE);
							long duration = VoiceRecorder
									.getAmrDuration(new File(localFilePath));
							tv_voice_length.setText(duration + "\''");

						} else {
							tv_voice_length.setVisibility(View.INVISIBLE);
						}

					} else {// 收到的消息，则先下载下来
						iv_voice_tips.setVisibility(View.GONE);	//有出现一个Bug，就是有未读消息时，上下滑动消息记录，上方已读的语音消息又会出现红点
						if (!(new File(localFilePath).exists())) {
							final String netUrl = text.trim();
							DownloadManager downloadTask = new DownloadManager(
									new DownloadListener() {

										@Override
										public void onStart() {
											// TODO Auto-generated method stub
											progress_load
													.setVisibility(View.VISIBLE);
											tv_voice_length
													.setVisibility(View.GONE);
											iv_voice.setVisibility(View.INVISIBLE);// 只有下载完成才显示播放的按钮
										}

										@Override
										public void onSuccess() {
											// TODO Auto-generated method stub
											try {
												long duration = 0;
												while (true) { // 取到真正语音长度为止，不能为0
													duration = VoiceRecorder
															.getAmrDuration(new File(
																	localFilePath));
													if (duration != 0)
														break;
												}
												progress_load
														.setVisibility(View.GONE);
												tv_voice_length
														.setVisibility(View.VISIBLE);
												tv_voice_length
														.setText(duration
																+ "\''");
												iv_voice.setVisibility(View.VISIBLE);
												iv_voice_tips
														.setVisibility(View.VISIBLE);
											} catch (IOException e) {
												// TODO 自动生成的 catch 块
												e.printStackTrace();
											}

										}

										@Override
										public void onError(String error) {
											// TODO Auto-generated method stub
											progress_load
													.setVisibility(View.GONE);
											tv_voice_length
													.setVisibility(View.GONE);
											iv_voice.setVisibility(View.INVISIBLE);
										}
									});
							downloadTask.execute(netUrl, localFilePath);
						} else {
							long duration = VoiceRecorder
									.getAmrDuration(new File(localFilePath));
							tv_voice_length.setText(duration + "\''");
							if (item.getStatus() == ChatConstants.STATUS_UNPLAY) {
								iv_voice_tips.setVisibility(View.VISIBLE);
							}
						}
					}
				}
				// 播放语音文件
				convertView.setOnClickListener(new NewRecordPlayClickListener(
						mContext, item, iv_voice, iv_voice_tips,
						new OnPlayChangeListener() {

							@Override
							public void onPlayStop() {
								// TODO 自动生成的方法存根
								Intent intent = new Intent();
								intent.setAction("chat.msg.playrecord");
								mContext.sendBroadcast(intent);
							}

							@Override
							public void onPlayStart() {
								// TODO 自动生成的方法存根

							}
						}));
				convertView
						.setOnLongClickListener(new OnChatItemLongClickListener(
								item, position));
			} catch (Exception e) {

			}
			break;
		default:
			break;
		}
		return convertView;
	}

	/**
	 * 判断是否是手机号码的正则表达式
	 * 
	 * @param number
	 * @return
	 */
	public boolean isNumber(String number) {
		if (number == null)
			return false;
		return number.matches("[+-]?[1-9]+[0-9]*(\\.[0-9]+)?");
	}

	/**
	 * 自己添加的方法，因为默认的是使用Int值的，要转换为我们使用的String值
	 * 
	 * @param msgType
	 * @return
	 */
	private int getMsgTypeIntValue(String msgType) {
		if (msgType.equals(ChatConstants.MSG_TYPE_TEXT)) {
			return ChatConstants.TYPE_TEXT;
		} else if (msgType.equals(ChatConstants.MSG_TYPE_IMAGE)) {
			return ChatConstants.TYPE_IMAGE;
		} else if (msgType.equals(ChatConstants.MSG_TYPE_AUDIO)) {
			return ChatConstants.TYPE_VOICE;
		} else if (msgType.equals(ChatConstants.MSG_TYPE_VIDEO)) {
			return ChatConstants.TYPE_VIDEO;
		} else {
			return ChatConstants.TYPE_TEXT;
		}

	}

	/**
	 * 获取图片的地址--
	 * 
	 * @Description: TODO
	 * @param @param item
	 * @param @return
	 * @return String
	 * @throws
	 */
	private String getImageUrl(ChatMsg item) {
		String showUrl = "";
		String text = item.getContent();
		if (item.getBelongId().equals(currentUserAccountName)) {//
			try {
				showUrl = Config.PIC_DIR + MD5Util.getMD5(text.trim()) + ".jpg";
				if (!(new File(showUrl).exists())) {
					showUrl = text.trim();
					// 为了方便每次都是取本地图片显示
				} else {
					showUrl = "file:///" + showUrl;
				}
			} catch (NoSuchAlgorithmException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		} else {// 如果是收到的消息，则需要从网络下载
			showUrl = text.trim();
		}
		return showUrl;
	}

	/**
	 * 处理图片消息
	 * 
	 * @param position
	 *            位置
	 * @param progress_load
	 *            滚动进度
	 * @param iv_fail_resend
	 *            重发图标
	 * @param tv_send_status
	 *            发送状态
	 * @param iv_picture
	 *            图片
	 * @param item
	 *            聊天实体
	 * @throws NoSuchAlgorithmException
	 */
	private void dealWithImage(int position, final ProgressBar progress_load,
			ImageView iv_fail_resend, TextView tv_send_status,
			final ImageView iv_picture, ChatMsg item)
			throws NoSuchAlgorithmException {
		String text = item.getContent();
		String localFilePath = Config.PIC_DIR + MD5Util.getMD5(text.trim())
				+ ".jpg";
		if (getItemViewType(position) == TYPE_SEND_IMAGE) {// 发送的消息
			if (item.getStatus() == ChatConstants.STATUS_SEND_START) {
				progress_load.setVisibility(View.VISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
			} else if (item.getStatus() == ChatConstants.STATUS_SEND_SUCCESS) {
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.VISIBLE);
				tv_send_status.setText("已发送");
			} else if (item.getStatus() == ChatConstants.STATUS_SEND_FAIL) {
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.VISIBLE);
				iv_fail_resend.setOnClickListener(new OnResendClickListener(
						item, position));
				tv_send_status.setVisibility(View.INVISIBLE);
			}

			if (!(new File(localFilePath).exists())) {
				// 为了方便每次都是取本地图片显示
				ImageLoader.getInstance().displayImage(text.trim(), iv_picture);
			} else {
				ImageLoader.getInstance().displayImage(
						"file:///" + localFilePath, iv_picture);
			}
		} else {
			progress_load.setVisibility(View.INVISIBLE);
			// 接受的消息则判断有无本地文件，没有则直接显示网络图片
			if (!new File(localFilePath).exists()) {
				ImageLoader.getInstance().displayImage(text.trim(), iv_picture,
						options, new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
								// TODO Auto-generated method stub
								progress_load.setVisibility(View.VISIBLE);
							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
								// TODO Auto-generated method stub
								progress_load.setVisibility(View.INVISIBLE);
							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								// TODO Auto-generated method stub
								progress_load.setVisibility(View.INVISIBLE);
							}

							@Override
							public void onLoadingCancelled(String imageUri,
									View view) {
								// TODO Auto-generated method stub
								progress_load.setVisibility(View.INVISIBLE);
							}
						});
			} else {
				ImageLoader.getInstance().displayImage(
						"file:///" + localFilePath, iv_picture);
			}
		}
	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 200);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	private String playerUuid = null;
	ProgressDialog dialog = null;

	private void getPlayerUidByAccountName(String accountName) {
		dialog = new ProgressDialog(mContext);
		dialog.show();
		NetWorkManager.getInstance(mContext).getPlayerByAccountName(
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				accountName, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						Player player = null;
						Gson gson = new Gson();
						player = gson.fromJson(response.toString(),
								new TypeToken<Player>() {
								}.getType());
						if (player != null) {
							playerUuid = player.getUuid();

							Intent intent = new Intent(mContext,
									TeamPlayerInfo.class);
							intent.putExtra(TeamPlayerInfo.PLAYER_UUID,
									playerUuid);
							mContext.startActivity(intent);
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("当前网络不可用");
						} else if (error.networkResponse == null) {
							// ShowToast("MessageChatAdapter-getPlayerUidByAccountName-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else if (error.networkResponse.statusCode == 404) {
							// ShowToast("球员找不到");
						}
					}
				});
	}

	class OnChatItemLongClickListener implements OnLongClickListener {

		public ChatMsg chatMsg = null;
		public int position = 0;

		public OnChatItemLongClickListener(ChatMsg chatMsg, int position) {
			super();
			this.chatMsg = chatMsg;
			this.position = position;
		}

		@Override
		public boolean onLongClick(View v) {
			// TODO 自动生成的方法存根
			cUtil.setTitleText("请选择");
			cUtil.setItems(mContext, new String[] { "删除", "转发" },
					new OnDialogItemOnClickListener() {

						@Override
						public void onDialogItemOnClick(int key, String value) {
							// TODO 自动生成的方法存根
							switch (key) {
							case 0:// 删除
								ChatDBManager.create(mContext).deleteTargetMsg(
										chatMsg);
								Intent intent = new Intent();
								intent.setAction("chat.msg.del");
								intent.putExtra(
										ChatConstants.INTENT_DEL_POSITION,
										position);
								mContext.sendBroadcast(intent);
								break;
							case 1:
								Intent intentRecent = new Intent(mContext,
										RecentActivity.class);
								intentRecent.putExtra(
										ChatConstants.INTENT_CHATMSG_BEAN,
										chatMsg);
								mContext.startActivity(intentRecent);
								break;
							}
						}
					});
			cUtil.showCustomDailog(mContext);
			return false;
		}

	}

	class OnResendClickListener implements OnClickListener {

		public ChatMsg chatMsg = null;
		public int position = 0;

		public OnResendClickListener(ChatMsg chatMsg, int position) {
			super();
			this.chatMsg = chatMsg;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			// TODO 自动生成的方法存根
			Intent intent = new Intent();
			intent.setAction("chat.msg.resend");
			intent.putExtra(ChatConstants.INTENT_CHATMSG_BEAN, chatMsg);
			intent.putExtra(ChatConstants.INTENT_DEL_POSITION, position);
			mContext.sendBroadcast(intent);
		}

	}

}
