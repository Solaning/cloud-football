package com.kinth.football.chat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBConfig {
	private Context mContext = null;
	private String dbName = "bmobchat.db";
	private int dbVersion = 1;
	private DBConfig.DbUpdateListener dbUpdateListener;

	public Context getContext() {
		return this.mContext;
	}

	public void setContext(Context paramContext) {
		this.mContext = paramContext;
	}

	public String getDbName() {
		return this.dbName;
	}

	public void setDbName(String paramString) {
		this.dbName = paramString;
	}

	public int getDbVersion() {
		return this.dbVersion;
	}

	public void setDbVersion(int paramInt) {
		this.dbVersion = paramInt;
	}

	public DBConfig.DbUpdateListener getDbUpdateListener() {
		return this.dbUpdateListener;
	}

	public void setDbUpdateListener(
			DBConfig.DbUpdateListener paramDbUpdateListener) {
		this.dbUpdateListener = paramDbUpdateListener;
	}

	public abstract interface DbUpdateListener {
		public abstract void onUpgrade(SQLiteDatabase paramSQLiteDatabase,
				int paramInt1, int paramInt2);
	}
}
