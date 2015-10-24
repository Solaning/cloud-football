package com.kinth.football.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

import com.kinth.football.dao.PushMessageDao;
import com.kinth.football.dao.PlayerDao;
import com.kinth.football.dao.TeamDao;
import com.kinth.football.dao.TeamPlayerDao;
import com.kinth.football.dao.MatchDao;
import com.kinth.football.dao.MatchTeamDao;
import com.kinth.football.dao.MatchTeamPlayerDao;
import com.kinth.football.dao.FormationDBDao;
import com.kinth.football.dao.ChatDao;
import com.kinth.football.dao.RecentDao;
import com.kinth.football.dao.SharingDBDao;
import com.kinth.football.dao.CommentDBDao;
import com.kinth.football.database.MyDataBaseOpenHelper;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * Master of DAO (schema version 2): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 2;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        PushMessageDao.createTable(db, ifNotExists);
        PlayerDao.createTable(db, ifNotExists);
        TeamDao.createTable(db, ifNotExists);
        TeamPlayerDao.createTable(db, ifNotExists);
        MatchDao.createTable(db, ifNotExists);
        MatchTeamDao.createTable(db, ifNotExists);
        MatchTeamPlayerDao.createTable(db, ifNotExists);
        FormationDBDao.createTable(db, ifNotExists);
        ChatDao.createTable(db, ifNotExists);
        RecentDao.createTable(db, ifNotExists);
        SharingDBDao.createTable(db, ifNotExists);
        CommentDBDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        PushMessageDao.dropTable(db, ifExists);
        PlayerDao.dropTable(db, ifExists);
        TeamDao.dropTable(db, ifExists);
        TeamPlayerDao.dropTable(db, ifExists);
        MatchDao.dropTable(db, ifExists);
        MatchTeamDao.dropTable(db, ifExists);
        MatchTeamPlayerDao.dropTable(db, ifExists);
        FormationDBDao.dropTable(db, ifExists);
        ChatDao.dropTable(db, ifExists);
        RecentDao.dropTable(db, ifExists);
        SharingDBDao.dropTable(db, ifExists);
        CommentDBDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }
    
    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends MyDataBaseOpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
//            dropAllTables(db, true);
//            onCreate(db);
            super.onUpgrade(db, oldVersion, newVersion);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(PushMessageDao.class);
        registerDaoClass(PlayerDao.class);
        registerDaoClass(TeamDao.class);
        registerDaoClass(TeamPlayerDao.class);
        registerDaoClass(MatchDao.class);
        registerDaoClass(MatchTeamDao.class);
        registerDaoClass(MatchTeamPlayerDao.class);
        registerDaoClass(FormationDBDao.class);
        registerDaoClass(ChatDao.class);
        registerDaoClass(RecentDao.class);
        registerDaoClass(SharingDBDao.class);
        registerDaoClass(CommentDBDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}