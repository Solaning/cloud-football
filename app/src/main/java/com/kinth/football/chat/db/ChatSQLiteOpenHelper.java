package com.kinth.football.chat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

final class ChatSQLiteOpenHelper extends SQLiteOpenHelper {
	private DBConfig.DbUpdateListener dbUpdateListener;
	private Context context;
	private ChatDBManager mBmobDB;

	public ChatSQLiteOpenHelper(ChatDBManager db, Context context,
			String dbName, int paramInt,
			DBConfig.DbUpdateListener dbUpdateListener) {
		super(context, dbName, null, paramInt);
		this.dbUpdateListener = dbUpdateListener;
		this.context = context;
		this.mBmobDB = db;
	}

	/**
	 * 创建数据库
	 */
	public final void onCreate(SQLiteDatabase sqLiteDatabase) {
		ChatDBManager.createChatDB(this.mBmobDB, sqLiteDatabase); // 创建聊天的数据库
		ChatDBManager.createRecentDB(this.mBmobDB, sqLiteDatabase); // 创建最近聊天列表数据库
		ChatDBManager.createNewContactsDB(this.mBmobDB, sqLiteDatabase); // 创建联系人数据库
		ChatDBManager.createFriendsDB(this.mBmobDB, sqLiteDatabase); // 创建朋友聊天
	}

	public final void onUpgrade(SQLiteDatabase paramSQLiteDatabase,
			int paramInt1, int paramInt2) {
		if (this.dbUpdateListener != null) {
			this.dbUpdateListener.onUpgrade(paramSQLiteDatabase, paramInt1,
					paramInt2);
			return;
		} else {
			ChatDBManager.create(context).clearAllDbCache();
			return;
		}
	}

	//Can't downgrade database from version 2 to 1
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 自动生成的方法存根
//		super.onDowngrade(db, oldVersion, newVersion);
	}

}