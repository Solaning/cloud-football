package com.kinth.football.chat.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.kinth.football.CustomApplcation;
import com.kinth.football.bean.User;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.chat.bean.ChatMsg;
import com.kinth.football.chat.bean.ChatRecent;
import com.kinth.football.chat.listener.RefreshEvent;
import com.kinth.football.manager.UserManager;

import de.greenrobot.event.EventBus;

public class ChatDBManager {
	private static HashMap<String, ChatDBManager> ai = new HashMap();
	private SQLiteDatabase mSqLiteDatabase;

	/*
	 * private static User user =
	 * UserManager.getInstance(null).getCurrentUser(); private String userPhone
	 * = UserManager.getInstance(null).getCurrentUserPhone();
	 */

	/**
	 * 根据用户ID（现在是手机号，创建数据库）
	 * 
	 * @param context
	 * @return
	 */
	public static ChatDBManager create(Context context) {
		DBConfig dbConfig = new DBConfig();
		dbConfig.setContext(context);
		User user = UserManager.getInstance(context).getCurrentUser();
		if (user != null && !TextUtils.isEmpty(user.getPlayer().getPhone())) {
			dbConfig.setDbName(user.getPlayer().getUuid()+"@chat");
		}
		return getInstance(dbConfig);
	}

	/**
	 * 根据dbName获取实例
	 * 
	 * @param context
	 * @param dbName
	 * @return
	 */
	public static ChatDBManager create(Context context, String dbName) {
		Log.e("2", dbName);
		DBConfig dbConfig = new DBConfig();
		dbConfig.setContext(context);
		dbConfig.setDbName(dbName);
		return getInstance(dbConfig);
	}

	/**
	 * 根据DBConfig获取实例
	 * 
	 * @param paramDBConfig
	 * @return
	 */
	public static ChatDBManager getInstance(DBConfig paramDBConfig) {
		return Code(paramDBConfig);
	}

	private static synchronized ChatDBManager Code(DBConfig config) {
		ChatDBManager localBmobDB;
		if ((localBmobDB = (ChatDBManager) ai.get(config.getDbName())) == null) {
			localBmobDB = new ChatDBManager(config);
			ai.put(config.getDbName(), localBmobDB);
			Log.e("3", config.getDbName());
		}
		return localBmobDB;
	}

	/**
	 * 根据dbconfig创建数据库
	 * 
	 * @param dbConfig
	 */
	private ChatDBManager(DBConfig dbConfig) {
		Log.e("step", "5"+dbConfig.getDbName());
		if (dbConfig == null)
			throw new RuntimeException("dbConfig is null");
		if (dbConfig.getContext() == null)
			throw new RuntimeException("android context is null");
		this.mSqLiteDatabase = new ChatSQLiteOpenHelper(this, dbConfig
				.getContext().getApplicationContext(), dbConfig.getDbName(),
				dbConfig.getDbVersion(), dbConfig.getDbUpdateListener())
				.getWritableDatabase();
	}
	
	public ChatMsg getLatestMessage(String targetId){
		ChatMsg chatMsg = null;
		if (this.mSqLiteDatabase != null) {
			String sql = "SELECT * from chat WHERE conversationid LIKE  '%"
						+ targetId + "%' " + " ORDER BY " + "_id";
			Cursor cursor = this.mSqLiteDatabase.rawQuery(sql, null);
			if (cursor.moveToLast()) {
				String conversationId = cursor.getString(cursor
						.getColumnIndex("conversationid")); // 获取conversationid
				String content = cursor.getString(cursor
						.getColumnIndex("content")); // 获取conversationid
				String belongId = cursor.getString(cursor
						.getColumnIndex("belongid")); // 获取属于谁的id
				String belongAvatar = cursor.getString(cursor
						.getColumnIndex("belongavatar")); // 发送者的头像
				String belongNick = cursor.getString(cursor
						.getColumnIndex("belongnick")); // 发送者的昵称
				String belongUsername = cursor.getString(cursor
						.getColumnIndex("belongaccount"));

				int status = cursor.getInt(cursor.getColumnIndex("status")); // 消息状态
				String msgType = cursor.getString(cursor
						.getColumnIndex("msgtype")); // 消息类型
				String msgTime = cursor.getString(cursor
						.getColumnIndex("msgtime"));
				int tag = cursor.getInt(cursor.getColumnIndex("tag"));

				chatMsg = new ChatMsg(conversationId, content,
						belongId, belongNick, belongAvatar, belongUsername,
						msgTime, msgType, status, tag);
			}
			if ((cursor != null) && (!cursor.isClosed()))
				cursor.close();
		}
		return chatMsg;
	}

	/**
	 * 获取两个人的聊天
	 * 
	 * @param targetId
	 * @param paramInt
	 * @return
	 */
	public List<ChatMsg> queryMessages(String targetId, int page, int paramTag) {
		List<ChatMsg> chatMsgs = new LinkedList<ChatMsg>();
		int limitCount = 20 * (page + 1);
		String userPhone = UserManager.getInstance(
				CustomApplcation.getInstance()).getCurrentUserPhone();
		if (this.mSqLiteDatabase != null) {
			String convasation1 = userPhone + "&" + targetId;
			String convasation2 = targetId + "&" + userPhone;

			String sql = "SELECT * from chat WHERE conversationid IN ( '"
					+ convasation1 + "' , '" + convasation2 + "' ) "
					+ " ORDER BY " + "_id" + " DESC LIMIT " + limitCount;

			if (paramTag == ChatConstants.TAG_GROUP)// 群聊消息
				sql = "SELECT * from chat WHERE conversationid LIKE  '%"
						+ targetId + "%' AND conversationid NOT LIKE '%@team%'"
						+ " ORDER BY " + "_id" + " DESC LIMIT " + limitCount;
			if (paramTag == ChatConstants.TAG_TEAM)// 球队消息
				sql = "SELECT * from chat WHERE conversationid LIKE  '%"
						+ targetId + "%' " + " ORDER BY " + "_id"
						+ " DESC LIMIT " + limitCount;

			Cursor cursor = this.mSqLiteDatabase.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				String conversationId = cursor.getString(cursor
						.getColumnIndex("conversationid")); // 获取conversationid
				String content = cursor.getString(cursor
						.getColumnIndex("content")); // 获取conversationid
				String belongId = cursor.getString(cursor
						.getColumnIndex("belongid")); // 获取属于谁的id
				String belongAvatar = cursor.getString(cursor
						.getColumnIndex("belongavatar")); // 发送者的头像
				String belongNick = cursor.getString(cursor
						.getColumnIndex("belongnick")); // 发送者的昵称
				String belongUsername = cursor.getString(cursor
						.getColumnIndex("belongaccount"));

				int status = cursor.getInt(cursor.getColumnIndex("status")); // 消息状态
				String msgType = cursor.getString(cursor
						.getColumnIndex("msgtype")); // 消息类型
				String msgTime = cursor.getString(cursor
						.getColumnIndex("msgtime"));
				int tag = cursor.getInt(cursor.getColumnIndex("tag"));

				ChatMsg chatMsg = new ChatMsg(conversationId, content,
						belongId, belongNick, belongAvatar, belongUsername,
						msgTime, msgType, status, tag);
				chatMsgs.add(chatMsg);

			}
			if ((cursor != null) && (!cursor.isClosed()))
				cursor.close();
			Collections.reverse(chatMsgs);
		}
		return chatMsgs;
	}

	/**
	 * 获取和某人聊天的总共聊天记录数量
	 * 
	 * @param paramString
	 * @return
	 */
	public int queryChatTotalCount(String paramString) {
		String userphone = UserManager.getInstance(null).getCurrentUserPhone();
		String convesationId1 = userphone + "&" + paramString;
		String convesationId2 = paramString + "&" + userphone;
		String sql = "SELECT * from chat WHERE conversationid IN ( '"
				+ convesationId1 + "' , '" + convesationId2 + "' )";
		Cursor cursor = this.mSqLiteDatabase.rawQuery(sql, null);
		int i = cursor.getCount();
		if ((cursor != null) && (!cursor.isClosed()))
			cursor.close();
		return i;
	}

	/**
	 * 删除和某人的对话
	 */
	public void deleteMessages(String targetID) {
		String userphone = UserManager.getInstance(null).getCurrentUserPhone();
		String[] arrays = new String[] { userphone + "&" + userphone,
				userphone + "&" + userphone };
		this.mSqLiteDatabase.delete("chat", "conversationid in(?,?)", arrays);
	}

	/**
	 * 删除目标消息（根据conversationid 以及时间）
	 * 
	 * @param chatMsg
	 */
	public void deleteTargetMsg(ChatMsg chatMsg) {
		if (this.mSqLiteDatabase.isOpen())
			this.mSqLiteDatabase
					.delete("chat",
							"conversationid = ? AND msgtime = ? ",
							new String[] { chatMsg.getConversationId(),
									chatMsg.getMsgTime() });
	}

	/**
	 * 保存消息
	 * 
	 * @param chatMsg
	 * @return
	 */
	public int saveMessage(ChatMsg chatMsg) {
		int i = -1;
		if (this.mSqLiteDatabase.isOpen()) {
			ContentValues values = new ContentValues();
			values.put("content", chatMsg.getContent());
			values.put("status", Integer.valueOf(chatMsg.getStatus()));
			// 如果消息不存在，则插入
			if (!isMsgExist(chatMsg.getConversationId(), chatMsg.getMsgTime())) {
				values.put("conversationid", chatMsg.getConversationId());
				values.put("msgtime", chatMsg.getMsgTime());
				values.put("msgtype", chatMsg.getMsgType());
				values.put("belongid", chatMsg.getBelongId());
				values.put("belongavatar", chatMsg.getBelongAvatar());
				values.put("belongnick", chatMsg.getBelongNick());
				values.put("belongaccount", chatMsg.getBelongUsername());
				values.put("tag", chatMsg.getTag());
				this.mSqLiteDatabase.insert("chat", null, values);
			}
			// 如果消息存在，则更新
			else {
				String[] conditions = new String[] {
						chatMsg.getConversationId(), chatMsg.getMsgTime() };
				this.mSqLiteDatabase.update("chat", values,
						"conversationid = ?  AND msgtime = ? ", conditions);
			}
			Cursor cursor = this.mSqLiteDatabase.rawQuery(
					"select last_insert_rowid() from chat", null);
			if (cursor.moveToFirst())
				i = cursor.getInt(0);
			if ((cursor != null) && (!cursor.isClosed()))
				cursor.close();
		}
		return i;
	}

	/**
	 * 获取未读消息总数
	 * 
	 * @param paramString
	 * @return
	 */
	public int getUnreadCount(String targetId, int tag) {
		String userPhone = UserManager.getInstance(
				CustomApplcation.getInstance()).getCurrentUserPhone();
		// 0.获取当前手机
		// String userPhone =
		// UserManager.getInstance(null).getCurrentUserPhone();
		// 1.组装conversionSion1
		String conversation1 = userPhone + "&" + targetId;
		// 2.组装conversionSion2
		String conversation2 = targetId + "&" + userPhone;
		// 3.组装sql
		// String sql = "SELECT * from chat";

		String sql = "SELECT * from chat WHERE conversationid IN ( '"
				+ conversation1 + "' , '" + conversation2 + "' ) AND "
				+ "status" + " = " + ChatConstants.STATE_UNREAD;

		if (tag == ChatConstants.TAG_GROUP)// 球队群聊
			sql = "SELECT * from chat WHERE conversationid LIKE  '%" + targetId
					+ "%' AND " + "status" + " = " + ChatConstants.STATE_UNREAD;

		Cursor cursor = this.mSqLiteDatabase.rawQuery(sql, null);
		// int i = cursor.getCount();
		int i = 0;
		while (cursor.moveToNext()) {
			i++;
		}
		if ((cursor != null) && (!cursor.isClosed()))
			cursor.close();
		return i;

	}

	public int getUnreadAndUnplayCount(String targetId, int tag) {
		String userPhone = UserManager.getInstance(
				CustomApplcation.getInstance()).getCurrentUserPhone();
		// 0.获取当前手机
		// String userPhone =
		// UserManager.getInstance(null).getCurrentUserPhone();
		// 1.组装conversionSion1
		String conversation1 = userPhone + "&" + targetId;
		// 2.组装conversionSion2
		String conversation2 = targetId + "&" + userPhone;
		// 3.组装sql
		// String sql = "SELECT * from chat";

		String sql = "SELECT * from chat WHERE conversationid IN ( '"
				+ conversation1 + "' , '" + conversation2 + "' ) AND "
				+ "status" + " IN ( " + ChatConstants.STATE_UNREAD + " , "
				+ ChatConstants.STATUS_UNPLAY + " )";

		if (tag == ChatConstants.TAG_GROUP)// 球队群聊
			sql = "SELECT * from chat WHERE conversationid LIKE  '%" + targetId
					+ "%' AND " + "status" + " IN ( "
					+ ChatConstants.STATE_UNREAD + " , "
					+ ChatConstants.STATUS_UNPLAY + " )";

		Cursor cursor = this.mSqLiteDatabase.rawQuery(sql, null);
		// int i = cursor.getCount();
		int i = 0;
		while (cursor.moveToNext()) {
			i++;
		}
		if ((cursor != null) && (!cursor.isClosed()))
			cursor.close();
		return i;

	}

	/**
	 * 查看是否有未读消息
	 * 
	 * @return
	 */
	public boolean hasUnReadMsg() {
		// String sql = "SELECT * from chat WHERE isreaded = 0";

		String sql = "SELECT * from chat WHERE isreaded = 0";

		Cursor cursor = this.mSqLiteDatabase.rawQuery(sql, null);
		int i = 0;
		try {
			i = cursor.getCount();
		} finally {
			if ((cursor != null) && (!cursor.isClosed()))
				cursor.close();
		}
		return i > 0;
	}

	/**
	 * 重置未读消息
	 * 
	 * @param paramString
	 */
	public void resetUnread(String targetId, int tag) {
		String userPhone = UserManager.getInstance(
				CustomApplcation.getInstance()).getCurrentUserPhone();
		// 0.获取当前手机，组装两个人的conversationid
		String conversationid1 = userPhone + "&" + targetId;
		String conversationid2 = targetId + "&" + userPhone;
		// 1.删除、更新条件
		String[] whereArgs = new String[] { conversationid1, conversationid2 };
		// 2.更新数据库
		ContentValues values = new ContentValues();
		// values.put("isreaded", Integer.valueOf(ChatConfig.STATE_READED));
		values.put("status", Integer.valueOf(ChatConstants.STATE_READED));

		if (tag == ChatConstants.TAG_GROUP) {// 球队群聊
			Log.e("tag", "resetUnread-group");
			this.mSqLiteDatabase.update("chat", values,
					"conversationid LIKE '%" + targetId + "%' AND status = 0",
					null);
		} else {
			Log.e("tag", "resetUnread-no");
			this.mSqLiteDatabase.update("chat", values,
					"conversationid in( ?, ? ) AND status = 0", whereArgs);
		}
		EventBus.getDefault().post(new RefreshEvent());
	}

	/**
	 * 更新目标消息状态
	 * 
	 * @param paramInt
	 * @param paramString1
	 * @param paramString2
	 */
	public void updateTargetMsgStatus(int status, String conversationid,
			String msgtime) {
		String[] conditions = new String[] { conversationid, msgtime };
		ContentValues values = new ContentValues();
		values.put("status", Integer.valueOf(status));
		this.mSqLiteDatabase.update("chat", values,
				"conversationid = ?  AND msgtime = ? ", conditions);
	}

	public void updateContentForTargetMsg(String paramString, ChatMsg chatMsg) {
		String[] arrayOfString = { chatMsg.getConversationId(),
				chatMsg.getMsgTime() };
		ContentValues values = new ContentValues();
		values.put("content", paramString + "&" + chatMsg.getContent());
		this.mSqLiteDatabase.update("chat", values,
				"conversationid = ?  AND msgtime = ? ", arrayOfString);
	}

	private boolean isMsgExist(String paramString1, String paramString2) {
		String[] arrays = new String[] { paramString1, paramString2 };
		Cursor cursor = this.mSqLiteDatabase.query("chat", null,
				"conversationid = ?  AND msgtime = ? ", arrays, null, null,
				null);
		boolean isExist = false;
		try {
			isExist = cursor.moveToFirst();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return isExist;
	}

	/**
	 * 获取最近聊天数组
	 * 
	 * @param currentUser
	 * 
	 * @return
	 */
	public List<ChatRecent> queryRecents(String key, int column) {
		Log.e("tag", "queryRecents");
		List<ChatRecent> recents = new ArrayList<ChatRecent>();
		Cursor cursor = this.mSqLiteDatabase.rawQuery(
				"SELECT * FROM recent WHERE tuid NOT LIKE '%" + key + "%'",
				null);
		if (column == 1) {
			cursor = this.mSqLiteDatabase.rawQuery(
					"SELECT * FROM recent WHERE tnick LIKE '%" + key + "%'",
					null);
		}
		while (cursor.moveToNext()) {
			String uid = cursor.getString(cursor.getColumnIndex("tuid"));
			String nick = cursor.getString(cursor.getColumnIndex("tnick"));
			String username = cursor.getString(cursor
					.getColumnIndex("tusername"));
			String avatar = cursor.getString(cursor.getColumnIndex("tavatar"));
			long msgTime = cursor.getLong(cursor.getColumnIndex("msgtime"));
			String msgContent = cursor.getString(cursor
					.getColumnIndex("lastmessage"));
			String msgType = cursor.getString(cursor.getColumnIndex("msgtype"));
			if (!isExistField3("recent", "tag")) {
				Log.e("db-recent-exist", "no-tag");
			}else{
				Log.e("db-recent-exist", "tag");
			}
			int tag = cursor.getInt(cursor.getColumnIndex("tag"));
			// 创建一个recent
			ChatRecent recent = new ChatRecent((String) uid, username, nick,
					avatar, msgContent, msgTime, msgType, tag);
			recents.add(recent);
		}
		if ((cursor != null) && (!cursor.isClosed()))
			cursor.close();
		Collections.sort(recents);
		return recents;
	}

	/**
	 * 保存最近消息
	 * 
	 * @param recent
	 */
	public void saveRecent(ChatRecent recent) {
		if (this.mSqLiteDatabase.isOpen()) {
			ContentValues values = new ContentValues();
			values.put("tavatar", recent.getAvatar());	//对方的头像和昵称可能会改变，所以都要更新
			values.put("tnick", recent.getNick());
			if (!isRecentFriendExsit(recent.getTargetid())) {	//与该对象第一次聊天,下面有些值是固定不变的
				values.put("tuid", recent.getTargetid());
				values.put("tusername", recent.getNick());
				values.put("lastmessage", recent.getMessage());
				values.put("msgtype", recent.getType());
				values.put("msgtime", Long.valueOf(recent.getTime()));
				values.put("tag", recent.getTag());
				this.mSqLiteDatabase.insert("recent", null, values);
				return;
			}
			values.put("lastmessage", recent.getMessage());
			values.put("msgtime", Long.valueOf(recent.getTime()));
			values.put("msgtype", recent.getType());
			this.mSqLiteDatabase.update("recent", values, "tuid = ?",
					new String[] { recent.getTargetid() });
		}
	}

	/**
	 * 保存测试的Recent
	 * 
	 * @param recent
	 */
	public void saveTestRecent(ChatRecent recent) {
		if (this.mSqLiteDatabase.isOpen()) {
			ContentValues values = new ContentValues();
			values.put("tavatar", recent.getAvatar());
			values.put("tnick", recent.getNick());
			if (!isRecentFriendExsit(recent.getTargetid())) {
				values.put("tuid", recent.getTargetid());
				values.put("tusername", recent.getUserName());
				values.put("msgtime", Long.valueOf(recent.getTime()));
				values.put("lastmessage", recent.getMessage());
				values.put("msgtype", recent.getType());
				this.mSqLiteDatabase.insert("recent", null, values);
				return;
			}

		}
	}

	/**
	 * 查看聊天列表是否有此人的聊天记录
	 * 
	 * @param paramString
	 * @return
	 */
	private boolean isRecentFriendExsit(String targetPhone) {
		String[] arrays = new String[] { targetPhone };

		Cursor cursor = this.mSqLiteDatabase.query("recent", null, "tuid=?",
				arrays, null, null, null);
		boolean isExist = false;
		try {
			isExist = cursor.moveToFirst();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return isExist;
	}

	private boolean isRecentMsgExsit(String paramString1, String paramString2) {
		String[] arrays = new String[] { paramString1, paramString2 };
		Cursor cursor = this.mSqLiteDatabase.query("recent", null,
				"tuid = ?  AND msgtime = ?", arrays, null, null, null);
		boolean isExist = false;
		try {
			isExist = cursor.moveToFirst();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return isExist;
	}

	/**
	 * 删除某最近聊天信息
	 * 
	 * @param paramString
	 */
	public void deleteRecent(String paramString) {
		this.mSqLiteDatabase.delete("recent", "tuid = ?",
				new String[] { paramString });
	}

	/**
	 * 删除所有聊天信息
	 */
	public void deleteAllRecent() {
		this.mSqLiteDatabase.delete("recent", null, null);
	}

	public void deleteInviteMsg(String paramString1, String paramString2) {
		if (this.mSqLiteDatabase.isOpen())
			this.mSqLiteDatabase.delete("tab_new_contacts",
					"fromid = ? AND fromtime = ? ", new String[] {
							paramString1, paramString2 });
	}

	public void updateAgreeMessage(String paramString) {
		if (this.mSqLiteDatabase.isOpen()) {
			ContentValues localContentValues;
			(localContentValues = new ContentValues()).put("status",
					Integer.valueOf(5));
			this.mSqLiteDatabase.update("tab_new_contacts", localContentValues,
					"fromname = ?", new String[] { paramString });
		}
	}

	/*
	 * 查看是否有邀请信息
	 */
	public boolean hasNewInvite() {
		if (this.mSqLiteDatabase.isOpen()) {
			Object localObject1 = "SELECT * from tab_new_contacts WHERE status = 4";
			Cursor cursor = this.mSqLiteDatabase.rawQuery(
					(String) localObject1, null);
			int i = 0;
			try {
				i = cursor.getCount();
			} finally {
				if (cursor != null)
					cursor.close();
			}
			return i > 0;
		}
		return false;
	}

	public void saveOrCheckContactList(List<User> paramList) {
		if (this.mSqLiteDatabase.isOpen()) {
			Iterator<User> iterator = paramList.iterator();
			while (iterator.hasNext()) {
				User user = (User) iterator.next();

				ContentValues values = new ContentValues();
				values.put("nick", user.getPlayer().getName());
				values.put("username", user.getPlayer().getUuid());
				values.put("avatar", user.getPlayer().getPicture());
				values.put("isblack", "n");

				if (I(user.getPlayer().getUuid())) {
					String[] arrStrings = new String[] { user.getPlayer()
							.getUuid() };
					this.mSqLiteDatabase.update("friends", values, "uid = ? ",
							arrStrings);
				} else {
					values.put("uid", user.getPlayer().getUuid());
					this.mSqLiteDatabase.insert("friends", null, values);
				}
			}
		}
	}

	public void batchAddBlack(List<User> list) {
		int i = list.size();
		for (int j = 0; j < i; j++) {
			User user = (User) list.get(j);
			if (!I(user.getPlayer().getUuid()))
				continue;
			addBlack(user.getPlayer().getUuid());
		}
	}

	public boolean isBlackUser(String paramString) {
		String[] arrays = { "isblack" };
		Cursor cursor = this.mSqLiteDatabase.query("friends", arrays,
				"uid = ? ", new String[] { paramString }, null, null, null);

		if ((cursor != null) && (cursor.moveToFirst())) {
			String isblack = "";
			try {
				isblack = cursor.getString(cursor.getColumnIndex("isblack"));
			} finally {
				if (cursor != null)
					cursor.close();
			}
			return !isblack.equals("n");
		}
		return false;
	}

	public void addBlack(String paramString) {
		ContentValues values = new ContentValues();
		values.put("isblack", "y");
		String[] arrays = new String[] { paramString };
		this.mSqLiteDatabase.update("friends", values, "username = ? ", arrays);
	}

	public void removeBlack(String paramString) {
		ContentValues values = new ContentValues();
		values.put("isblack", "n");
		String[] arrays = new String[] { paramString };
		this.mSqLiteDatabase.update("friends", values, "username = ? ", arrays);
	}

	public List<User> getContactsWithoutBlack(List<User> users1,
			List<User> users2) {
		List<User> userList = new ArrayList<User>();
		// userList = Utils.list2map(users2);
		// int i = users2.size();
		// for (int j = 0; j < i; j++)
		// {
		// User user = (User)users1.get(j);
		// if (users1.containsKey(user.getId()))
		// continue;
		// userList.add(user);
		// }
		return userList;
	}

	private boolean I(String paramString) {
		Cursor cursor = this.mSqLiteDatabase.query("friends", null, "uid=?",
				new String[] { paramString }, null, null, null);
		boolean isExsit = false;
		try {
			isExsit = cursor.moveToFirst();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return isExsit;
	}

	/*
	 * public void saveContact(ChatInvitation paramBmobInvitation) {
	 * ContentValues values; (values = new ContentValues()).put("uid",
	 * paramBmobInvitation.getFromid()); values.put("username",
	 * paramBmobInvitation.getFromname()); values.put("avatar",
	 * paramBmobInvitation.getAvatar()); values.put("nick",
	 * paramBmobInvitation.getNick()); values.put("isblack", "n"); if
	 * (this.mSqLiteDatabase.isOpen()) this.mSqLiteDatabase.insert("friends",
	 * null, values); }
	 */

	/**
	 * 获取联系人
	 * 
	 * @return
	 */
	public List<User> getContactList() {
		List<User> userList = new ArrayList<User>();
		if (this.mSqLiteDatabase.isOpen()) {
			Cursor cursor = this.mSqLiteDatabase.rawQuery(
					"select * from friends WHERE isblack = ?",
					new String[] { "n" });
			while (cursor.moveToNext()) {
				String str1 = cursor.getString(cursor.getColumnIndex("uid"));
				String str2 = cursor.getString(cursor
						.getColumnIndex("username"));
				String str3 = cursor.getString(cursor.getColumnIndex("avatar"));
				String str4 = cursor.getString(cursor.getColumnIndex("nick"));
				User user = new User();
				// user.setUsername(str2);
				// user.setNick(str4);
				// user.setObjectId(str1);
				// user.setAvatar(str3);
				userList.add(user);
			}
			if ((cursor != null) && (!cursor.isClosed()))
				cursor.close();
		}
		return userList;
	}

	/**
	 * 获取黑名单（暂时不需要）
	 * 
	 * @return
	 */
	public List<User> getBlackList() {
		List<User> blackList = new ArrayList<User>();
		if (this.mSqLiteDatabase.isOpen()) {
			Cursor cursor = this.mSqLiteDatabase.rawQuery(
					"select * from friends WHERE isblack = ?",
					new String[] { "y" });
			while (cursor.moveToNext()) {
				String str1 = cursor.getString(cursor.getColumnIndex("uid"));
				String str2 = cursor.getString(cursor
						.getColumnIndex("username"));
				String str3 = cursor.getString(cursor.getColumnIndex("avatar"));
				String str4 = cursor.getString(cursor.getColumnIndex("nick"));
				User blackUser = new User();
				// blackUser.setUsername(str2);
				// blackUser.setNick(str4);
				// blackUser.setObjectId(str1);
				// blackUser.setAvatar(str3);

				blackList.add(blackUser);
			}
			if ((cursor != null) && (!cursor.isClosed()))
				cursor.close();
		}
		return blackList;
	}

	public void deleteContact(String paramString) {
		if (this.mSqLiteDatabase.isOpen())
			this.mSqLiteDatabase.delete("friends", "uid = ?",
					new String[] { paramString });
	}

	/**
	 * 清除所有的缓存
	 */
	public void clearAllDbCache() {
		Cursor cursor = this.mSqLiteDatabase
				.rawQuery(
						"SELECT name FROM sqlite_master WHERE type ='table' AND name != 'sqlite_sequence'",
						null);
		if (cursor != null)
			while (cursor.moveToNext())
				this.mSqLiteDatabase.execSQL("DROP TABLE "
						+ cursor.getString(0));
		if (cursor != null)
			cursor.close();
	}

	/**
	 * @param db
	 * @param sqlitedatabase
	 */
	static void createChatDB(ChatDBManager bmobdb, SQLiteDatabase sqlitedatabase) {
		String sql = "CREATE TABLE IF NOT EXISTS chat (_id INTEGER PRIMARY KEY AUTOINCREMENT, conversationid INTEGER, belongaccount TEXT, belongnick TEXT, belongavatar TEXT, content TEXT NOT NULL, belongid TEXT NOT NULL,status INTEGER, msgtype INTEGER, msgtime TEXT); ";
		// (bmobdb = sqlitedatabase).execSQL(sql);
		sqlitedatabase.execSQL(sql);
	}

	static void createRecentDB(ChatDBManager bmobdb,
			SQLiteDatabase sqlitedatabase) {
		sqlitedatabase
				.execSQL("CREATE TABLE IF NOT EXISTS recent (_id INTEGER PRIMARY KEY AUTOINCREMENT, tuid TEXT, tusername TEXT, tnick TEXT, tavatar TEXT, lastmessage TEXT NOT NULL, msgtype INTEGER, msgtime TEXT); ");
		/**
		 * 群聊实现步骤5：数据库添加群聊标识字段 sqlitedatabase .execSQL(
		 * "CREATE TABLE IF NOT EXISTS recent (_id INTEGER PRIMARY KEY AUTOINCREMENT, tuid TEXT, tusername TEXT, tnick TEXT, tavatar TEXT, lastmessage TEXT NOT NULL, msgtype INTEGER, msgtime TEXT, tag INTEGER); "
		 * );
		 */
	}

	static void createNewContactsDB(ChatDBManager bmobdb,
			SQLiteDatabase sqlitedatabase) {
		sqlitedatabase
				.execSQL("CREATE TABLE tab_new_contacts (fromid TEXT, fromname TEXT, fromnick TEXT, avatar TEXT, fromtime TEXT, status INTEGER); ");
	}

	static void createFriendsDB(ChatDBManager bmobdb,
			SQLiteDatabase sqlitedatabase) {
		sqlitedatabase
				.execSQL("CREATE TABLE friends (username TEXT, nick TEXT, avatar TEXT, isblack TEXT, uid TEXT);");
	}

	/**
	 * 魅族会报duplicate column name错，即重复列，说明该判断没起效
	 * @param tableName
	 * @param fieldName
	 * @return
	 */
	public boolean isExistField(String tableName, String fieldName) {
		boolean isExist = false;
		Cursor cursor = this.mSqLiteDatabase.rawQuery("SELECT * FROM "
				+ tableName + " LIMIT 0", null);
		if (cursor.getColumnIndex(fieldName) != -1)
			isExist = true;
		if (cursor != null && !cursor.isClosed())
			cursor.close();
		return isExist;
	}

	/**
	 * 根据 cursor.getColumnIndex(String columnName) 的返回值判断，如果为-1表示表中无此字段
	 * @param tableName
	 * @param fieldName
	 * @return
	 */
	public boolean isExistField2(String tableName, String fieldName) {
	    boolean result = false ;
	    Cursor cursor = null ;
	    try{
	        //查询一行
	        cursor = this.mSqLiteDatabase.rawQuery( "SELECT * FROM " + tableName + " LIMIT 0"
	            , null );
	        result = cursor != null && cursor.getColumnIndex(fieldName) != -1 ;
	    }catch (Exception e){
	    }finally{
	        if(null != cursor && !cursor.isClosed()){
	            cursor.close() ;
	        }
	    }

	    return result ;
	}
	
	/**
	* 通过查询sqlite的系统表 sqlite_master 来查找相应表里是否存在该字段，稍微换下语句也可以查找表是否存在
	* @param db
	* @param tableName 表名
	* @param columnName 列名
	* @return
	*/
	public boolean isExistField3(String tableName
	       , String fieldName) {
	    boolean result = false ;
	    Cursor cursor = null ;

	    try{
	        cursor = this.mSqLiteDatabase.rawQuery( "select * from sqlite_master where name = ? and sql like ?"
	           , new String[]{tableName , "%" + fieldName + "%"} );
	        result = null != cursor && cursor.moveToFirst() ;
	    }catch (Exception e){
	    }finally{
	        if(null != cursor && !cursor.isClosed()){
	            cursor.close() ;
	        }
	    }

	    return result ;
	}

	public void addNewField(String tableName, String fieldName, String fieldType) {
		this.mSqLiteDatabase.execSQL("ALTER TABLE " + tableName + " ADD "
				+ fieldName + " " + fieldType + " NOT NULL DEFAULT 0");
	}

	public void updateRecentTag() {
		Cursor cursor = this.mSqLiteDatabase.rawQuery("SELECT * FROM recent",
				null);
		while (cursor.moveToNext()) {
			String uid = cursor.getString(cursor.getColumnIndex("tuid"));
			ContentValues values = new ContentValues();
			if (uid.length() > 20)
				values.put("tag", 1);
			else
				values.put("tag", 0);

			String[] conditions = new String[] { uid };
			this.mSqLiteDatabase.update("recent", values, "tuid = ? ",
					conditions);
		}
		if ((cursor != null) && (!cursor.isClosed()))
			cursor.close();
	}
}
