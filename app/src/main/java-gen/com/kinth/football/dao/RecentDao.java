package com.kinth.football.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.kinth.football.dao.Recent;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table RECENT.
*/
public class RecentDao extends AbstractDao<Recent, Long> {

    public static final String TABLENAME = "RECENT";

    /**
     * Properties of entity Recent.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Tuid = new Property(1, String.class, "tuid", false, "TUID");
        public final static Property Tusername = new Property(2, String.class, "tusername", false, "TUSERNAME");
        public final static Property Tnick = new Property(3, String.class, "tnick", false, "TNICK");
        public final static Property Tavatar = new Property(4, String.class, "tavatar", false, "TAVATAR");
        public final static Property Lastmessage = new Property(5, String.class, "lastmessage", false, "LASTMESSAGE");
        public final static Property Msgtype = new Property(6, Integer.class, "msgtype", false, "MSGTYPE");
        public final static Property Msgtime = new Property(7, String.class, "msgtime", false, "MSGTIME");
    };


    public RecentDao(DaoConfig config) {
        super(config);
    }
    
    public RecentDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'RECENT' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'TUID' TEXT," + // 1: tuid
                "'TUSERNAME' TEXT," + // 2: tusername
                "'TNICK' TEXT," + // 3: tnick
                "'TAVATAR' TEXT," + // 4: tavatar
                "'LASTMESSAGE' TEXT NOT NULL ," + // 5: lastmessage
                "'MSGTYPE' INTEGER," + // 6: msgtype
                "'MSGTIME' TEXT);"); // 7: msgtime
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'RECENT'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Recent entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String tuid = entity.getTuid();
        if (tuid != null) {
            stmt.bindString(2, tuid);
        }
 
        String tusername = entity.getTusername();
        if (tusername != null) {
            stmt.bindString(3, tusername);
        }
 
        String tnick = entity.getTnick();
        if (tnick != null) {
            stmt.bindString(4, tnick);
        }
 
        String tavatar = entity.getTavatar();
        if (tavatar != null) {
            stmt.bindString(5, tavatar);
        }
        stmt.bindString(6, entity.getLastmessage());
 
        Integer msgtype = entity.getMsgtype();
        if (msgtype != null) {
            stmt.bindLong(7, msgtype);
        }
 
        String msgtime = entity.getMsgtime();
        if (msgtime != null) {
            stmt.bindString(8, msgtime);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Recent readEntity(Cursor cursor, int offset) {
        Recent entity = new Recent( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // tuid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // tusername
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // tnick
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // tavatar
            cursor.getString(offset + 5), // lastmessage
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // msgtype
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // msgtime
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Recent entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTuid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTusername(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTnick(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTavatar(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setLastmessage(cursor.getString(offset + 5));
        entity.setMsgtype(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setMsgtime(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Recent entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Recent entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
