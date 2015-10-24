package com.kinth.football.chat.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.kinth.football.bean.User;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.dao.Team;
import com.kinth.football.manager.UserManager;
import com.kinth.football.util.JsonUtil;

/**
 * 聊天消息实体类
 */
public class ChatMsg implements Serializable {

	private static final long serialVersionUID = 1L;

	private String conversationId; // 对话Id，由【发送者】+【+】+【接受者】组成
	private String content; // 消息內容
	private String belongId; // 发送者Id
	private String belongAvatar;// 发送者头像
	private String belongNick; // 发送者昵称
	private String belongUsername; // 发送者姓名
	private String msgType; // 消息类型
	private String msgTime; // 消息时间，
	private int status; // 消息狀態
	private int tag;

	public ChatMsg() {
		super();
	}

	public ChatMsg(String conversationId, String content, String belongId,
			String belongNick, String belongAvatar, String belongUsername,
			String msgTime, String msgType, int status, int tag) {
		super();
		this.conversationId = conversationId;
		this.content = content;
		this.belongId = belongId;
		this.belongAvatar = belongAvatar;
		this.belongNick = belongNick;
		this.belongUsername = belongUsername;
		this.msgType = msgType;
		this.msgTime = msgTime;
		this.status = status;
		this.tag = tag;
	}

	public String getBelongUsername() {
		return belongUsername;
	}

	public void setBelongUsername(String belongUsername) {
		this.belongUsername = belongUsername;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getBelongId() {
		return belongId;
	}

	public void setBelongId(String belongId) {
		this.belongId = belongId;
	}

	public String getBelongAvatar() {
		return belongAvatar;
	}

	public void setBelongAvatar(String belongAvatar) {
		this.belongAvatar = belongAvatar;
	}

	public String getBelongNick() {
		return belongNick;
	}

	public void setBelongNick(String belongNick) {
		this.belongNick = belongNick;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getMsgTime() {
		return msgTime;
	}

	public void setMsgTime(String msgTime) {
		this.msgTime = msgTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 组装一个文字聊天消息实体类
	 * 
	 * @param mContext
	 *            上下文
	 * @param targetId
	 *            聊天对象AccountName
	 * @param textContent
	 *            文本内容
	 * @return
	 */
	public static ChatMsg createTextSendMsg(Context mContext, String targetId,
			String textContent, int tag) {
		return createSendMessage(mContext, targetId, textContent,
				ChatConstants.MSG_TYPE_TEXT, ChatConstants.STATUS_SEND_START, tag);
	}

	/**
	 * 组装一个图片聊天消息实体类
	 * 
	 * @param mContext
	 *            上下文
	 * @param targetId
	 *            聊天对象AccountName
	 * @param textContent
	 *            文本内容
	 * @return
	 */
	public static ChatMsg createImageSendMsg(Context mContext, String targetId,
			String textContent, int tag) {
		return createSendMessage(mContext, targetId, textContent,
				ChatConstants.MSG_TYPE_IMAGE, ChatConstants.STATUS_SEND_START, tag);
	}

	/**
	 * 组装一个语音聊天消息实体类
	 * 
	 * @param mContext
	 *            上下文
	 * @param targetId
	 *            聊天对象AccountName
	 * @param textContent
	 *            文本内容
	 * @return
	 */
	public static ChatMsg createVoiceSendMsg(Context mContext, String targetId,
			String textContent, int tag) {
		return createSendMessage(mContext, targetId, textContent,
				ChatConstants.MSG_TYPE_AUDIO, ChatConstants.STATUS_SEND_START, tag);
	}

	/**
	 * 
	 * 组装发送的消息
	 * 
	 * @param mContext
	 *            上下文
	 * @param targetId
	 *            聊天对象AccountName
	 * @param textContent
	 *            文本内容
	 * @param status
	 *            发送状态
	 * @return
	 */
	public static ChatMsg createSendMessage(Context context, String targetId,
			String textContent, String type, int status, int tag) {
		// 1.获取当前用户
		User currentUser = UserManager.getInstance(context).getCurrentUser();
		// 2.获取当前用户的电话
		String userId = currentUser.getPlayer().getPhone();
		String userName = currentUser.getPlayer().getUuid();// 这里暂时将用户名设为用户的id

		return new ChatMsg(userId + "&" + targetId, textContent, userId,
				currentUser.getPlayer().getName(), currentUser.getPlayer()
						.getPicture() == null ? "" : currentUser.getPlayer()
						.getPicture().trim(), userName, String.valueOf(System
						.currentTimeMillis() / 1L), type, status, tag);
	}

	/**
	 * 组装收到的消息
	 * 
	 * @param context
	 * @param json
	 * @return
	 */
	public static ChatMsg createReceiveMsg(Context context, String json) {
		ChatMsg msg = null;
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		String conversationId = JsonUtil
				.getString(jsonObject, "conversationId");
		String toId = conversationId.split("&")[1];// 接收人
		String fromId = conversationId.split("&")[0];//发送人
		String avatar = JsonUtil.getString(jsonObject, "belongAvatar");// 头像
		String nick = JsonUtil.getString(jsonObject, "belongNick");// 昵称
		String userName = JsonUtil.getString(jsonObject, "belongUsername");//暂时用户名字和昵称一样
		String content = JsonUtil.getString(jsonObject, "content");// 消息内容
		String msgType = JsonUtil.getString(jsonObject, "msgType");// 消息类型
		String sendTime = JsonUtil.getString(jsonObject, "msgTime");// 发送时间
		int status = ChatConstants.STATE_UNREAD;
		if(msgType.equals(ChatConstants.MSG_TYPE_AUDIO)){
			status = ChatConstants.STATUS_UNPLAY;
		}
		
		// 获取当前用户
		User currentUser = UserManager.getInstance(context).getCurrentUser();
		if (currentUser != null
				&& currentUser.getPlayer().getPhone().equals(toId)) {
			String currentUserId = currentUser.getPlayer().getPhone();

			msg = new ChatMsg((new StringBuilder(fromId)).append("&")
					.append(currentUserId).toString(), content, fromId,
					nick != null ? nick : "", avatar != null ? avatar : "",
					userName != null ? userName : "", sendTime, msgType,
							status, 0);
		} else {
			msg = new ChatMsg((new StringBuilder(String.valueOf(fromId)))
					.append("&").append(toId).toString(), content, fromId,
					nick != null ? nick : "", avatar != null ? avatar : "",
					userName != null ? userName : "", sendTime, msgType,
							status, 0);
		}
		return msg;
	}

	/**
	 * 组装收到的系统消息
	 * 
	 * @param context
	 *            上下文
	 * @param description
	 *            文本内容
	 * @return
	 */
	public static ChatMsg createSystemReceiveMsg(Context context,
			String description) {
		ChatMsg msg = null;
		//发送人
		String fromId = "System";
		// 头像
		String avatar = "http://120.196.123.14/M00/00/14/wKgeDlUvE-CAYgFvAAAx8w26TEM0911959";
		// 昵称
		String nick = "云球团队";
		// TODO 暂时用户名字和昵称一样
		String userName = nick;
		// 消息内容
		String content = description;
		// 消息类型
		String msgType = "text";
		// 发送时间
		String sendTime = Long.toString(System.currentTimeMillis());

		// 获取当前用户
		User currentUser = UserManager.getInstance(context).getCurrentUser();
		//
		if (currentUser != null) {
			String currentUserId = currentUser.getPlayer().getPhone();
			msg = new ChatMsg((new StringBuilder(fromId)).append("&")
					.append(currentUserId).toString(), content, fromId,
					nick != null ? nick : "", avatar != null ? avatar : "",
					userName != null ? userName : "", sendTime, msgType,
					ChatConstants.STATE_UNREAD, 2);
		}

		return msg;
	}

	/**
	 * 组装收到的球队消息
	 * 
	 * @param context
	 *            上下文
	 * @param teamResponse
	 *            球队实体类
	 * @param description
	 *            文本内容
	 * @return
	 */
	public static ChatMsg createTeamReceiveMsg(Context context,
			Team teamResponse, String description) {
		ChatMsg msg = null;

		// 发送人
		String fromId = teamResponse.getUuid()+"@team";
		// 头像
		String avatar = teamResponse.getBadge();
		if (avatar == null || avatar.equals(""))
			avatar = "http://120.196.123.14/M00/00/14/wKgeDlUvWmCAe4SsAAAUDzEuSH81621204";
		// 昵称
		String nick = teamResponse.getName();
		// TODO 暂时用户名字和昵称一样
		String userName = nick;
		// 消息内容
		String content = description;
		// 消息类型
		String msgType = "text";
		// 发送时间
		String sendTime = Long.toString(System.currentTimeMillis());

		// 获取当前用户
		User currentUser = UserManager.getInstance(context).getCurrentUser();
		//
		if (currentUser != null) {
			String currentUserId = currentUser.getPlayer().getPhone();
			msg = new ChatMsg((new StringBuilder(fromId)).append("&")
					.append(currentUserId).toString(), content, fromId,
					nick != null ? nick : "", avatar != null ? avatar : "",
					userName != null ? userName : "", sendTime, msgType,
					ChatConstants.STATE_UNREAD, 1);
		}

		return msg;
	}
	
	/**
	 * 组装收到的球队消息
	 * 
	 * @param context
	 *            上下文
	 * @param teamResponse
	 *            球队实体类
	 * @param description
	 *            文本内容
	 * @return
	 */
	public static ChatMsg createGroupReceiveMsg(Context context,
			 String json) {
		ChatMsg msg = null;
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		String conversationId = JsonUtil
				.getString(jsonObject, "conversationId");
		String fromId = conversationId.split("&")[0];//发送者
		String toId = conversationId.split("&")[1];// 球队Uuid
		String avatar = JsonUtil.getString(jsonObject, "belongAvatar");// 头像
		String nick = JsonUtil.getString(jsonObject, "belongNick");// 昵称
		String userName = JsonUtil.getString(jsonObject, "belongUsername");//暂时用户名字和昵称一样
		String content = JsonUtil.getString(jsonObject, "content");// 消息内容
		String msgType = JsonUtil.getString(jsonObject, "msgType");// 消息类型
		String sendTime = JsonUtil.getString(jsonObject, "msgTime");// 发送时间
		int status = ChatConstants.STATE_UNREAD;
		if(msgType.equals(ChatConstants.MSG_TYPE_AUDIO)){
			status = ChatConstants.STATUS_UNPLAY;
		}
		
		// 获取当前用户
		User currentUser = UserManager.getInstance(context).getCurrentUser();
		//
		if (currentUser != null) {
			msg = new ChatMsg((new StringBuilder(fromId)).append("&")
					.append(toId).toString(), content, fromId,
					nick != null ? nick : "", avatar != null ? avatar : "",
					userName != null ? userName : "", sendTime, msgType,
							status, 1);
		}

		return msg;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

}
