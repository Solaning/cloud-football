package com.kinth.football.database;

import com.kinth.football.dao.CommentDBDao;
import com.kinth.football.dao.DaoMaster;
import com.kinth.football.dao.SharingDBDao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * greenDao数据库升级--每次生成新的Dao后需要升级数据库，需要把DaoMaster中的OpenHelper替换成继承自MyDataBaseOpenHelper，并进行如下处理
 *     public static class DevOpenHelper extends MyDataBaseOpenHelper {
 *       public DevOpenHelper(Context context, String name, CursorFactory factory) {
 *           super(context, name, factory);
 *       }
 *
 *       @Override
 *       public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 *           Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
 *            //注销  dropAllTables(db, true);
 *            //注销  onCreate(db);
 *            super.onUpgrade(db, oldVersion, newVersion);  //新增加
 *       }
 *   }
 * @author Sola
 *
 */
public class MyDataBaseOpenHelper extends SQLiteOpenHelper{
	
    public MyDataBaseOpenHelper(Context context, String name, CursorFactory factory) {
        super(context, name, factory, DaoMaster.SCHEMA_VERSION);
    }
	
	public MyDataBaseOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		DaoMaster.createAllTables(db, false);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion < newVersion){//需要升级数据库版本
            boolean ifNotExists = true;
            //Leave old tables alone and only create ones that didn't exist
            //in the previous schema
//            NewTable1Dao.createTable(db, ifNotExists);
            switch(oldVersion){
            case 1:
            	from1to2(db,  ifNotExists);
            	break;
//            case 2:  新版本的数据库在此添加
            default:
            	onCreate(db);
            	break;
            }
        } else {
        	DaoMaster.dropAllTables(db, true);
            onCreate(db);
        }
	}
	
	void from1to2(SQLiteDatabase db, boolean ifNotExists){
        SharingDBDao.createTable(db, ifNotExists);
        CommentDBDao.createTable(db, ifNotExists);
	}

}
