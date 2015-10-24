package com.kinth.football.chat;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.kinth.football.bean.PickedImage;
import com.kinth.football.bean.UploadImage;
import com.kinth.football.bean.User;
import com.kinth.football.chat.bean.ChatMsg;
import com.kinth.football.chat.bean.ChatRecent;
import com.kinth.football.chat.db.ChatDBManager;
import com.kinth.football.chat.listener.UploadListener;
import com.kinth.football.chat.util.AsyncUploadRecord;
import com.kinth.football.chat.util.FileUtil;
import com.kinth.football.chat.util.MD5Util;
import com.kinth.football.config.Config;
import com.kinth.football.dao.Team;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.AsyncUploadImage;

public class MyChatManager {

	public static final String TAG = "ChatManager";
	private static Object INSTANCE_LOCK = new Object();
	private static volatile MyChatManager chatManager;
	private static Context mContext;
	private static final String TAG_CHAT = "TAG_CHAT";// 阵容标签

	public static MyChatManager getInstance(Context context) {
		if (chatManager == null)
			synchronized (INSTANCE_LOCK) {
				if (chatManager == null)
					chatManager = new MyChatManager();
				init(context);
			}
		return chatManager;
	}

	private static void init(Context context) {
		mContext = context;
	}

	public static Context getmContext() {
		return mContext;
	}

	public static void setmContext(Context mContext) {
		MyChatManager.mContext = mContext;
	}

	public void saveReceiveMessage(boolean paramBoolean, ChatMsg chatMsg) {
		String str = UserManager.getInstance(mContext).getCurrentUser()
				.getPlayer().getUuid() + "@chat"; // 暂时是uuid（需求是需要id,暂时用phone）
		// 3.保存消息
		// chatMsg.setStatus(ChatConstants.STATE_UNREAD);
		ChatDBManager.create(mContext, str).saveMessage(chatMsg);
		// 4.创建最新聊天
		ChatRecent recent = new ChatRecent(chatMsg.getConversationId().split(
				"&")[0].replace("@team", ""), chatMsg.getBelongUsername(),
				chatMsg.getBelongNick(), chatMsg.getBelongAvatar(),
				chatMsg.getContent(), Long.parseLong(chatMsg.getMsgTime()),
				chatMsg.getMsgType(), chatMsg.getTag());

		// 5.保存最近聊天信息
		ChatDBManager.create(mContext, str).saveRecent(recent);

	}

	public void saveGroupReceiveMessage(Team teamResponse, ChatMsg chatMsg) {
		String str = UserManager.getInstance(mContext).getCurrentUser()
				.getPlayer().getUuid() + "@chat"; // 暂时是uuid（需求是需要id,暂时用phone）
		// chatMsg.setStatus(ChatConstants.STATE_UNREAD);//暂时忘记要在这里设未读状态何用
		ChatDBManager.create(mContext, str).saveMessage(chatMsg);

		ChatRecent recent = new ChatRecent(teamResponse.getUuid(),
				teamResponse.getName(), teamResponse.getName(),
				teamResponse.getBadge(), chatMsg.getContent(),
				Long.parseLong(chatMsg.getMsgTime()), chatMsg.getMsgType(),
				chatMsg.getTag());

		ChatDBManager.create(mContext, str).saveRecent(recent);
	}

	public int saveChatMessage(ChatMsg chatMsg) {
		String uuid = UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid() + "@chat";
		return ChatDBManager.create(mContext, uuid).saveMessage(chatMsg);
	}

	public void saveRecentMessage(ChatRecent recent) {
		String uuid = UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid() + "@chat";
		ChatDBManager.create(mContext, uuid).saveRecent(recent);
	}

	public void updateMsgStatus(int msgStatus, ChatMsg chatMsg) {

		// 本机号码
		String str = UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid() + "@chat";
		// 修改chat数据库的状态
		ChatDBManager.create(mContext, str).updateTargetMsgStatus(msgStatus,
				chatMsg.getConversationId(), chatMsg.getMsgTime());
		// 修改recent数据
		// 1.如果targetid的 msgTime 和msgContent 一样 ，那么就小改 msgTime 和msgContent 的值
		// ChatRecent recent = new ChatRecent(chatMsg.getBelongId(),
		// chatMsg.getBelongUsername(), chatMsg.getBelongNick(),
		// chatMsg.getBelongAvatar(), chatMsg.getContent(),
		// Long.parseLong(chatMsg.getMsgTime()), chatMsg.getMsgType());
		//
		// ChatDBManager.create(mContext, str).saveRecent(recent);

	}

	public void sendImageMessage(final User user, final String local,
			final int tag, final UploadListener uploadsListener) {
		uploadsListener.onStart();
		AsyncUploadImage asyncUploadImage = new AsyncUploadImage() {

			@Override
			public void onUploadSuccess(Map<String, UploadImage> uploadMap) {
				for (Map.Entry<String, UploadImage> entry : uploadMap
						.entrySet()) {
					if (TAG_CHAT.equals(entry.getValue().getTag())) {// 聊天图片
						String imgChatUrl = entry.getValue().getUrl();
						try {
							String renamedFile = Config.PIC_DIR
									+ MD5Util.getMD5(imgChatUrl.trim())
									+ ".jpg";
							FileUtil.renameFile(local, renamedFile);
						} catch (NoSuchAlgorithmException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}

						ChatMsg chatMsg = ChatMsg.createImageSendMsg(
								MyChatManager.mContext, user.getPlayer()
										.getAccountName(), imgChatUrl.trim(),
								tag);
						uploadsListener.onSuccess(chatMsg);

						continue;
					}
				}
			}

			@Override
			public void onUploadFailed() {
				Toast.makeText(mContext, "上传失败", Toast.LENGTH_LONG).show();
				uploadsListener.onFailure(0, null);
			}
		};
		Map<String, UploadImage> uploadMap = new LinkedHashMap<String, UploadImage>();// 服务器上的url
		if (!TextUtils.isEmpty(local)) {
			uploadMap.put(local, new UploadImage(local, TAG_CHAT));
			asyncUploadImage.upload2(uploadMap, new PickedImage(local));
		}
	}

	/**
	 * 发送语音消息，这里进行的操作是上传语音文件到服务器
	 * 
	 * @param user
	 *            聊天对象
	 * @param local
	 *            本地文件
	 * @param length
	 *            语音长度
	 * @param paramUploadListener
	 *            上传结果回调函数
	 */
	public void sendVoiceMessage(final User user, final String local,
			final int length, final int tag,
			final UploadListener paramUploadListener) {
		AsyncUploadRecord asyncUploadImage = new AsyncUploadRecord() {

			@Override
			public void onUploadSuccess(Map<String, UploadImage> uploadMap) {
				for (Map.Entry<String, UploadImage> entry : uploadMap
						.entrySet()) {
					if (TAG_CHAT.equals(entry.getValue().getTag())) {// 语音文件
						String recordUrl = entry.getValue().getUrl(); // 取得上传成功后返回的网络路径
						try {
							String renamedFile = Config.VOICE_DIR
									+ MD5Util.getMD5(recordUrl.trim()) + ".amr"; // 以返回的网络路径进行MD5算法加密，以作为查找本地文件的依据
							FileUtil.renameFile(local, renamedFile);// 重命名本地文件
						} catch (NoSuchAlgorithmException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
						ChatMsg msg = ChatMsg.createVoiceSendMsg(mContext, user
								.getPlayer().getAccountName(),
								recordUrl.trim(), tag); // 组装语音信息
						paramUploadListener.onSuccess(msg); // 回调上传成功的函数，传入组装好的聊天实体类
						continue;
					}
				}
			}

			@Override
			public void onUploadFailed() {
				Toast.makeText(mContext, "语音文件上传失败", Toast.LENGTH_LONG).show();
			}
		};
		Map<String, UploadImage> uploadMap = new LinkedHashMap<String, UploadImage>();// 服务器上的url
		if (!TextUtils.isEmpty(local)) {
			uploadMap.put(local, new UploadImage(local, TAG_CHAT));
			asyncUploadImage.upload2(uploadMap, new PickedImage(local));
		}

	}

}
