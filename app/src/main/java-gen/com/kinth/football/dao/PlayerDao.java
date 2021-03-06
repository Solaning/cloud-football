package com.kinth.football.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.kinth.football.dao.Player;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table PLAYER.
*/
public class PlayerDao extends AbstractDao<Player, String> {

    public static final String TABLENAME = "PLAYER";

    /**
     * Properties of entity Player.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Uuid = new Property(0, String.class, "uuid", true, "UUID");
        public final static Property AccountName = new Property(1, String.class, "accountName", false, "ACCOUNT_NAME");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Gender = new Property(3, String.class, "gender", false, "GENDER");
        public final static Property Phone = new Property(4, String.class, "phone", false, "PHONE");
        public final static Property Email = new Property(5, String.class, "email", false, "EMAIL");
        public final static Property CityId = new Property(6, Integer.class, "cityId", false, "CITY_ID");
        public final static Property City = new Property(7, String.class, "city", false, "CITY");
        public final static Property Province = new Property(8, String.class, "province", false, "PROVINCE");
        public final static Property Picture = new Property(9, String.class, "picture", false, "PICTURE");
        public final static Property Position = new Property(10, String.class, "position", false, "POSITION");
        public final static Property Date = new Property(11, Long.class, "date", false, "DATE");
        public final static Property Birthday = new Property(12, Long.class, "birthday", false, "BIRTHDAY");
        public final static Property Height = new Property(13, Integer.class, "height", false, "HEIGHT");
        public final static Property Weight = new Property(14, Integer.class, "weight", false, "WEIGHT");
        public final static Property Strength = new Property(15, Float.class, "strength", false, "STRENGTH");
        public final static Property Skill = new Property(16, Float.class, "skill", false, "SKILL");
        public final static Property Attack = new Property(17, Float.class, "attack", false, "ATTACK");
        public final static Property Defence = new Property(18, Float.class, "defence", false, "DEFENCE");
        public final static Property Awareness = new Property(19, Float.class, "awareness", false, "AWARENESS");
        public final static Property Composite = new Property(20, Float.class, "composite", false, "COMPOSITE");
        public final static Property Score = new Property(21, Integer.class, "score", false, "SCORE");
    };

    private DaoSession daoSession;


    public PlayerDao(DaoConfig config) {
        super(config);
    }
    
    public PlayerDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'PLAYER' (" + //
                "'UUID' TEXT PRIMARY KEY NOT NULL ," + // 0: uuid
                "'ACCOUNT_NAME' TEXT," + // 1: accountName
                "'NAME' TEXT," + // 2: name
                "'GENDER' TEXT," + // 3: gender
                "'PHONE' TEXT," + // 4: phone
                "'EMAIL' TEXT," + // 5: email
                "'CITY_ID' INTEGER," + // 6: cityId
                "'CITY' TEXT," + // 7: city
                "'PROVINCE' TEXT," + // 8: province
                "'PICTURE' TEXT," + // 9: picture
                "'POSITION' TEXT," + // 10: position
                "'DATE' INTEGER," + // 11: date
                "'BIRTHDAY' INTEGER," + // 12: birthday
                "'HEIGHT' INTEGER," + // 13: height
                "'WEIGHT' INTEGER," + // 14: weight
                "'STRENGTH' REAL," + // 15: strength
                "'SKILL' REAL," + // 16: skill
                "'ATTACK' REAL," + // 17: attack
                "'DEFENCE' REAL," + // 18: defence
                "'AWARENESS' REAL," + // 19: awareness
                "'COMPOSITE' REAL," + // 20: composite
                "'SCORE' INTEGER);"); // 21: score
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'PLAYER'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Player entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getUuid());
 
        String accountName = entity.getAccountName();
        if (accountName != null) {
            stmt.bindString(2, accountName);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String gender = entity.getGender();
        if (gender != null) {
            stmt.bindString(4, gender);
        }
 
        String phone = entity.getPhone();
        if (phone != null) {
            stmt.bindString(5, phone);
        }
 
        String email = entity.getEmail();
        if (email != null) {
            stmt.bindString(6, email);
        }
 
        Integer cityId = entity.getCityId();
        if (cityId != null) {
            stmt.bindLong(7, cityId);
        }
 
        String city = entity.getCity();
        if (city != null) {
            stmt.bindString(8, city);
        }
 
        String province = entity.getProvince();
        if (province != null) {
            stmt.bindString(9, province);
        }
 
        String picture = entity.getPicture();
        if (picture != null) {
            stmt.bindString(10, picture);
        }
 
        String position = entity.getPosition();
        if (position != null) {
            stmt.bindString(11, position);
        }
 
        Long date = entity.getDate();
        if (date != null) {
            stmt.bindLong(12, date);
        }
 
        Long birthday = entity.getBirthday();
        if (birthday != null) {
            stmt.bindLong(13, birthday);
        }
 
        Integer height = entity.getHeight();
        if (height != null) {
            stmt.bindLong(14, height);
        }
 
        Integer weight = entity.getWeight();
        if (weight != null) {
            stmt.bindLong(15, weight);
        }
 
        Float strength = entity.getStrength();
        if (strength != null) {
            stmt.bindDouble(16, strength);
        }
 
        Float skill = entity.getSkill();
        if (skill != null) {
            stmt.bindDouble(17, skill);
        }
 
        Float attack = entity.getAttack();
        if (attack != null) {
            stmt.bindDouble(18, attack);
        }
 
        Float defence = entity.getDefence();
        if (defence != null) {
            stmt.bindDouble(19, defence);
        }
 
        Float awareness = entity.getAwareness();
        if (awareness != null) {
            stmt.bindDouble(20, awareness);
        }
 
        Float composite = entity.getComposite();
        if (composite != null) {
            stmt.bindDouble(21, composite);
        }
 
        Integer score = entity.getScore();
        if (score != null) {
            stmt.bindLong(22, score);
        }
    }

    @Override
    protected void attachEntity(Player entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Player readEntity(Cursor cursor, int offset) {
        Player entity = new Player( //
            cursor.getString(offset + 0), // uuid
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // accountName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // gender
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // phone
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // email
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // cityId
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // city
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // province
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // picture
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // position
            cursor.isNull(offset + 11) ? null : cursor.getLong(offset + 11), // date
            cursor.isNull(offset + 12) ? null : cursor.getLong(offset + 12), // birthday
            cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13), // height
            cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14), // weight
            cursor.isNull(offset + 15) ? null : cursor.getFloat(offset + 15), // strength
            cursor.isNull(offset + 16) ? null : cursor.getFloat(offset + 16), // skill
            cursor.isNull(offset + 17) ? null : cursor.getFloat(offset + 17), // attack
            cursor.isNull(offset + 18) ? null : cursor.getFloat(offset + 18), // defence
            cursor.isNull(offset + 19) ? null : cursor.getFloat(offset + 19), // awareness
            cursor.isNull(offset + 20) ? null : cursor.getFloat(offset + 20), // composite
            cursor.isNull(offset + 21) ? null : cursor.getInt(offset + 21) // score
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Player entity, int offset) {
        entity.setUuid(cursor.getString(offset + 0));
        entity.setAccountName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setGender(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setPhone(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setEmail(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setCityId(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setCity(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setProvince(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setPicture(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setPosition(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setDate(cursor.isNull(offset + 11) ? null : cursor.getLong(offset + 11));
        entity.setBirthday(cursor.isNull(offset + 12) ? null : cursor.getLong(offset + 12));
        entity.setHeight(cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13));
        entity.setWeight(cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14));
        entity.setStrength(cursor.isNull(offset + 15) ? null : cursor.getFloat(offset + 15));
        entity.setSkill(cursor.isNull(offset + 16) ? null : cursor.getFloat(offset + 16));
        entity.setAttack(cursor.isNull(offset + 17) ? null : cursor.getFloat(offset + 17));
        entity.setDefence(cursor.isNull(offset + 18) ? null : cursor.getFloat(offset + 18));
        entity.setAwareness(cursor.isNull(offset + 19) ? null : cursor.getFloat(offset + 19));
        entity.setComposite(cursor.isNull(offset + 20) ? null : cursor.getFloat(offset + 20));
        entity.setScore(cursor.isNull(offset + 21) ? null : cursor.getInt(offset + 21));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Player entity, long rowId) {
        return entity.getUuid();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Player entity) {
        if(entity != null) {
            return entity.getUuid();
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
