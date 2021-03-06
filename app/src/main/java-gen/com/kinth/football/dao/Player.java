package com.kinth.football.dao;

import java.util.List;
import com.kinth.football.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import android.os.Parcelable;
import android.os.Parcel;
// KEEP INCLUDES END
/**
 * Entity mapped to table PLAYER.
 */
public class Player implements Parcelable {

    /** Not-null value. */
    private String uuid;
    private String accountName;
    private String name;
    private String gender;
    private String phone;
    private String email;
    private Integer cityId;
    private String city;
    private String province;
    private String picture;
    private String position;
    private Long date;
    private Long birthday;
    private Integer height;
    private Integer weight;
    private Float strength;
    private Float skill;
    private Float attack;
    private Float defence;
    private Float awareness;
    private Float composite;
    private Integer score;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient PlayerDao myDao;

    private List<TeamPlayer> teamPlayerList;
    private List<MatchTeamPlayer> matchTeamPlayerList;
    private List<FormationDB> formationDBList;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Player() {
    }

    public Player(String uuid) {
        this.uuid = uuid;
    }

    public Player(String uuid, String accountName, String name, String gender, String phone, String email, Integer cityId, String city, String province, String picture, String position, Long date, Long birthday, Integer height, Integer weight, Float strength, Float skill, Float attack, Float defence, Float awareness, Float composite, Integer score) {
        this.uuid = uuid;
        this.accountName = accountName;
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.cityId = cityId;
        this.city = city;
        this.province = province;
        this.picture = picture;
        this.position = position;
        this.date = date;
        this.birthday = birthday;
        this.height = height;
        this.weight = weight;
        this.strength = strength;
        this.skill = skill;
        this.attack = attack;
        this.defence = defence;
        this.awareness = awareness;
        this.composite = composite;
        this.score = score;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPlayerDao() : null;
    }

    /** Not-null value. */
    public String getUuid() {
        return uuid;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Float getStrength() {
        return strength;
    }

    public void setStrength(Float strength) {
        this.strength = strength;
    }

    public Float getSkill() {
        return skill;
    }

    public void setSkill(Float skill) {
        this.skill = skill;
    }

    public Float getAttack() {
        return attack;
    }

    public void setAttack(Float attack) {
        this.attack = attack;
    }

    public Float getDefence() {
        return defence;
    }

    public void setDefence(Float defence) {
        this.defence = defence;
    }

    public Float getAwareness() {
        return awareness;
    }

    public void setAwareness(Float awareness) {
        this.awareness = awareness;
    }

    public Float getComposite() {
        return composite;
    }

    public void setComposite(Float composite) {
        this.composite = composite;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<TeamPlayer> getTeamPlayerList() {
        if (teamPlayerList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TeamPlayerDao targetDao = daoSession.getTeamPlayerDao();
            List<TeamPlayer> teamPlayerListNew = targetDao._queryPlayer_TeamPlayerList(uuid);
            synchronized (this) {
                if(teamPlayerList == null) {
                    teamPlayerList = teamPlayerListNew;
                }
            }
        }
        return teamPlayerList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetTeamPlayerList() {
        teamPlayerList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<MatchTeamPlayer> getMatchTeamPlayerList() {
        if (matchTeamPlayerList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MatchTeamPlayerDao targetDao = daoSession.getMatchTeamPlayerDao();
            List<MatchTeamPlayer> matchTeamPlayerListNew = targetDao._queryPlayer_MatchTeamPlayerList(uuid);
            synchronized (this) {
                if(matchTeamPlayerList == null) {
                    matchTeamPlayerList = matchTeamPlayerListNew;
                }
            }
        }
        return matchTeamPlayerList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetMatchTeamPlayerList() {
        matchTeamPlayerList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<FormationDB> getFormationDBList() {
        if (formationDBList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            FormationDBDao targetDao = daoSession.getFormationDBDao();
            List<FormationDB> formationDBListNew = targetDao._queryPlayer_FormationDBList(uuid);
            synchronized (this) {
                if(formationDBList == null) {
                    formationDBList = formationDBListNew;
                }
            }
        }
        return formationDBList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetFormationDBList() {
        formationDBList = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
	public static final Parcelable.Creator<Player> CREATOR = new Creator<Player>() {
		@Override
		public Player createFromParcel(Parcel parcel) {
			Player team = new Player();
			team.uuid = parcel.readString();
			team.accountName = parcel.readString();
			team.name = parcel.readString();
			team.gender= parcel.readString();
			team.phone= parcel.readString();
			team.email = parcel.readString();
			team.cityId = parcel.readInt();
			team.city = parcel.readString();
			team.province = parcel.readString();
			team.picture = parcel.readString();
			team.position = parcel.readString();
			team.date = parcel.readLong();
			team.birthday = parcel.readLong();
			team.height = parcel.readInt();
			team.weight = parcel.readInt();
			team.strength = parcel.readFloat();
			team.skill = parcel.readFloat();
			team.attack = parcel.readFloat();
			team.defence = parcel.readFloat();
			team.awareness = parcel.readFloat();
			team.composite = parcel.readFloat();
			return team;
		}

		@Override
		public Player[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Player[size];
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.uuid);
		dest.writeString(this.accountName);
		dest.writeString(this.name);
		dest.writeString(this.gender);
		dest.writeString(this.phone);
		dest.writeString(this.email);
	    dest.writeInt(this.cityId == null ? 0 : this.cityId);
	    dest.writeString(this.city);
	    dest.writeString(this.province);
	    dest.writeString(this.picture);
	    dest.writeString(this.position);
	    dest.writeLong(this.date == null ? 0l : this.date);
	    dest.writeLong(this.birthday == null ? 0 : this.birthday);
	    dest.writeInt(this.height == null ? 0 : this.height);
	    dest.writeInt(this.weight == null ? 0 : this.weight);
	    dest.writeFloat(this.strength == null ? 0f : this.strength);
	    dest.writeFloat(this.skill == null ? 0f : this.skill);
	    dest.writeFloat(this.attack == null ? 0f : this.attack);
	    dest.writeFloat(this.defence == null ? 0f : this.defence);
	    dest.writeFloat(this.awareness == null ? 0f :this.awareness);
	    dest.writeFloat(this.composite == null ? 0f : this.composite);
	    
	}
    // KEEP METHODS END

}
