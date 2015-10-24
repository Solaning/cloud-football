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
 * Entity mapped to table MATCH.
 */
public class Match implements Parcelable {

    /** Not-null value. */
    private String uuid;
    private String name;
    private String field;
    private Long kickOff;
    private String type;
    private String state;
    private Long date;
    private Integer playerCount;
    private Integer homeTeamScore;
    private Integer awayTeamScore;
    private Integer homeTeamLike;
    private Integer awayTeamLike;
    private String referee_id;
    private String creator_id;
    private Float cost;
    private String homeTeamJersey;
    private String awayTeamJersey;
    private String homeTeamFormation_id;
    private String awayTeamFormation_id;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient MatchDao myDao;

    private List<MatchTeam> matchTeamList;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Match() {
    }

    public Match(String uuid) {
        this.uuid = uuid;
    }

    public Match(String uuid, String name, String field, Long kickOff, String type, String state, Long date, Integer playerCount, Integer homeTeamScore, Integer awayTeamScore, Integer homeTeamLike, Integer awayTeamLike, String referee_id, String creator_id, Float cost, String homeTeamJersey, String awayTeamJersey, String homeTeamFormation_id, String awayTeamFormation_id) {
        this.uuid = uuid;
        this.name = name;
        this.field = field;
        this.kickOff = kickOff;
        this.type = type;
        this.state = state;
        this.date = date;
        this.playerCount = playerCount;
        this.homeTeamScore = homeTeamScore;
        this.awayTeamScore = awayTeamScore;
        this.homeTeamLike = homeTeamLike;
        this.awayTeamLike = awayTeamLike;
        this.referee_id = referee_id;
        this.creator_id = creator_id;
        this.cost = cost;
        this.homeTeamJersey = homeTeamJersey;
        this.awayTeamJersey = awayTeamJersey;
        this.homeTeamFormation_id = homeTeamFormation_id;
        this.awayTeamFormation_id = awayTeamFormation_id;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMatchDao() : null;
    }

    /** Not-null value. */
    public String getUuid() {
        return uuid;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Long getKickOff() {
        return kickOff;
    }

    public void setKickOff(Long kickOff) {
        this.kickOff = kickOff;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Integer getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(Integer playerCount) {
        this.playerCount = playerCount;
    }

    public Integer getHomeTeamScore() {
        return homeTeamScore;
    }

    public void setHomeTeamScore(Integer homeTeamScore) {
        this.homeTeamScore = homeTeamScore;
    }

    public Integer getAwayTeamScore() {
        return awayTeamScore;
    }

    public void setAwayTeamScore(Integer awayTeamScore) {
        this.awayTeamScore = awayTeamScore;
    }

    public Integer getHomeTeamLike() {
        return homeTeamLike;
    }

    public void setHomeTeamLike(Integer homeTeamLike) {
        this.homeTeamLike = homeTeamLike;
    }

    public Integer getAwayTeamLike() {
        return awayTeamLike;
    }

    public void setAwayTeamLike(Integer awayTeamLike) {
        this.awayTeamLike = awayTeamLike;
    }

    public String getReferee_id() {
        return referee_id;
    }

    public void setReferee_id(String referee_id) {
        this.referee_id = referee_id;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    public String getHomeTeamJersey() {
        return homeTeamJersey;
    }

    public void setHomeTeamJersey(String homeTeamJersey) {
        this.homeTeamJersey = homeTeamJersey;
    }

    public String getAwayTeamJersey() {
        return awayTeamJersey;
    }

    public void setAwayTeamJersey(String awayTeamJersey) {
        this.awayTeamJersey = awayTeamJersey;
    }

    public String getHomeTeamFormation_id() {
        return homeTeamFormation_id;
    }

    public void setHomeTeamFormation_id(String homeTeamFormation_id) {
        this.homeTeamFormation_id = homeTeamFormation_id;
    }

    public String getAwayTeamFormation_id() {
        return awayTeamFormation_id;
    }

    public void setAwayTeamFormation_id(String awayTeamFormation_id) {
        this.awayTeamFormation_id = awayTeamFormation_id;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<MatchTeam> getMatchTeamList() {
        if (matchTeamList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MatchTeamDao targetDao = daoSession.getMatchTeamDao();
            List<MatchTeam> matchTeamListNew = targetDao._queryMatch_MatchTeamList(uuid);
            synchronized (this) {
                if(matchTeamList == null) {
                    matchTeamList = matchTeamListNew;
                }
            }
        }
        return matchTeamList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetMatchTeamList() {
        matchTeamList = null;
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
	public static final Parcelable.Creator<Match> CREATOR = new Creator<Match>() {
		@Override
		public Match createFromParcel(Parcel parcel) {
			Match match = new Match();
			match.uuid = parcel.readString();
			match.name = parcel.readString();
			match.field = parcel.readString();
			match.kickOff = parcel.readLong();
			match.type = parcel.readString();
			match.state = parcel.readString();
			match.date = parcel.readLong();
			match.playerCount = parcel.readInt();
			match.homeTeamScore = parcel.readInt();
			match.awayTeamScore = parcel.readInt();
			match.homeTeamLike = parcel.readInt();
			match.awayTeamLike = parcel.readInt();
			match.referee_id = parcel.readString();
			match.creator_id = parcel.readString();
			match.cost = parcel.readFloat();
			match.homeTeamJersey = parcel.readString();
			match.awayTeamJersey = parcel.readString();
			match.homeTeamFormation_id = parcel.readString();
			match.awayTeamFormation_id = parcel.readString();
			return match;
		}

		@Override
		public Match[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Match[size];
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
		dest.writeString(this.name);
		dest.writeString(this.field);
		dest.writeLong(this.kickOff == null ? 0l : this.kickOff);
	    dest.writeString(this.type);
	    dest.writeString(this.state);
	    dest.writeLong(this.date == null ? 0l : this.date);
	    dest.writeInt(this.playerCount == null ? 0 : this.playerCount);
	    dest.writeInt(this.homeTeamScore == null ? 0 : this.homeTeamScore);
	    dest.writeInt(this.awayTeamScore == null ? 0 : this.awayTeamScore);
	    dest.writeInt(this.homeTeamLike == null ? 0 : this.homeTeamLike);
	    dest.writeInt(this.awayTeamLike == null ? 0 : this.awayTeamLike);
	    dest.writeString(this.referee_id);
	    dest.writeString(this.creator_id);
	    dest.writeFloat(this.cost == null ? 0f : this.cost);
	    dest.writeString(this.homeTeamJersey);
	    dest.writeString(this.awayTeamJersey);
	    dest.writeString(this.homeTeamFormation_id);
	    dest.writeString(this.awayTeamFormation_id);
	}
    // KEEP METHODS END

}
