package com.kinth.football.bean.match;

import java.util.Arrays;
import java.util.List;

import com.kinth.football.dao.Player;
import com.kinth.football.dao.Team;
import com.kinth.football.ui.team.formation.Formation;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 比赛信息
 * @author Sola
 *
 */
public class MatchInfo extends AbstractMatch{
	private String uuid;//比赛uuid
	private long date;//创建时间
	private Team homeTeam;//主队
	private List<PlayerInTeam> homeTeamPlayers;//球队成员--报名
	private int homeTeamScore;//主队进球
	private Team awayTeam;//客队
	private List<PlayerInTeam> awayTeamPlayers;//客队成员
	private int awayTeamScore;//客队进球
	private int homeTeamLike;//主队like数量
	private int awayTeamLike;//客队like数量
	private Player referee;//裁判
	private Player creator;//创建者
	private Formation homeTeamFormation;//主队阵容
	private Formation awayTeamFormation;//客队阵容
	private String homeTeamJersey;//主队球服
	private String awayTeamJersey;//客队球服
	private String challengeUuid; //TODO 应战Uuid
	private String video;//比赛视频

	public static final Parcelable.Creator<MatchInfo> CREATOR = new Creator<MatchInfo>() {
		@Override
		public MatchInfo createFromParcel(Parcel parcel) {
			MatchInfo matchInfo = new MatchInfo();
			matchInfo.uuid = parcel.readString();
			matchInfo.name = parcel.readString();
			matchInfo.field = parcel.readString();
			matchInfo.kickOff = parcel.readLong();
			matchInfo.playerCount = parcel.readInt();
			matchInfo.type = parcel.readString();
			matchInfo.state = parcel.readString();
			matchInfo.date = parcel.readLong();
			matchInfo.homeTeam = parcel.readParcelable(Team.class.getClassLoader());
//			matchInfo.homeTeamPlayers = parcel.readArrayList(UserPlayer.class.getClassLoader()); 错误
			Parcelable[] pars = parcel.readParcelableArray(PlayerInTeam.class.getClassLoader());
			matchInfo.homeTeamPlayers = Arrays.asList(Arrays.asList(pars).toArray(new PlayerInTeam[pars.length]));
			
//			UserPlayer[] tempHomeTeamPlayers = (UserPlayer[]) parcel.readParcelableArray(UserPlayer.class.getClassLoader());//
//			for (int i = 0; i < tempHomeTeamPlayers.length; i++) {
//				matchInfo.homeTeamPlayers.add(tempHomeTeamPlayers[i]);
//			}
			matchInfo.homeTeamScore = parcel.readInt();
			matchInfo.awayTeam = parcel.readParcelable(Team.class.getClassLoader());
			
			Parcelable[] pars3 = parcel.readParcelableArray(PlayerInTeam.class.getClassLoader());
			matchInfo.awayTeamPlayers = Arrays.asList(Arrays.asList(pars3).toArray(new PlayerInTeam[pars3.length]));
//			UserPlayer[] tempAwayTeamPlayers = (UserPlayer[]) parcel.readParcelableArray(UserPlayer.class.getClassLoader());
//			for(int i= 0; i < tempAwayTeamPlayers.length; i++){
//				matchInfo.awayTeamPlayers.add(tempAwayTeamPlayers[i]);
//			}
			
			matchInfo.awayTeamScore = parcel.readInt();
			matchInfo.homeTeamLike = parcel.readInt();
			matchInfo.awayTeamLike = parcel.readInt();
			matchInfo.referee = parcel.readParcelable(Player.class.getClassLoader());
			matchInfo.creator = parcel.readParcelable(Player.class.getClassLoader());//创建者
			matchInfo.cost = parcel.readFloat();//费用
			
			//add @2015-04-04 by sola
			matchInfo.homeTeamFormation = parcel.readParcelable(Formation.class.getClassLoader());
			matchInfo.awayTeamFormation = parcel.readParcelable(Formation.class.getClassLoader());
			matchInfo.homeTeamJersey = parcel.readString();
			matchInfo.awayTeamJersey = parcel.readString();
			matchInfo.challengeUuid = parcel.readString();
			matchInfo.video = parcel.readString();
			
			return matchInfo;
		}

		@Override
		public MatchInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new MatchInfo[size];
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
		dest.writeLong(this.kickOff);
		dest.writeInt(this.playerCount);
		dest.writeString(this.type);
		dest.writeString(this.state);
		dest.writeLong(this.date);
		dest.writeParcelable(this.homeTeam, flags);
		
		dest.writeParcelableArray(this.homeTeamPlayers.toArray(new PlayerInTeam[this.homeTeamPlayers.size()]), flags);//原来会报错
		
		dest.writeInt(this.homeTeamScore);
		dest.writeParcelable(this.awayTeam, flags);
		dest.writeParcelableArray(this.awayTeamPlayers.toArray(new PlayerInTeam[this.awayTeamPlayers.size()]), flags);
		dest.writeInt(this.awayTeamScore);
		dest.writeInt(this.homeTeamLike);
		dest.writeInt(this.awayTeamLike);
		dest.writeParcelable(this.referee, flags);
		dest.writeParcelable(this.creator, flags);
		dest.writeFloat(this.cost);
		
		dest.writeParcelable(this.homeTeamFormation, flags);
		dest.writeParcelable(this.awayTeamFormation, flags);
		dest.writeString(this.homeTeamJersey);
		dest.writeString(this.awayTeamJersey);
		dest.writeString(this.challengeUuid);
		dest.writeString(this.video);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}
	
	public Team getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(Team homeTeam) {
		this.homeTeam = homeTeam;
	}

	public List<PlayerInTeam> getHomeTeamPlayers() {
		return homeTeamPlayers;
	}

	public void setHomeTeamPlayers(List<PlayerInTeam> homeTeamPlayers) {
		this.homeTeamPlayers = homeTeamPlayers;
	}

	public int getHomeTeamScore() {
		return homeTeamScore;
	}

	public void setHomeTeamScore(int homeTeamScore) {
		this.homeTeamScore = homeTeamScore;
	}

	public Team getAwayTeam() {
		return awayTeam;
	}

	public void setAwayTeam(Team awayTeam) {
		this.awayTeam = awayTeam;
	}

	public List<PlayerInTeam> getAwayTeamPlayers() {
		return awayTeamPlayers;
	}

	public void setAwayTeamPlayers(List<PlayerInTeam> awayTeamPlayers) {
		this.awayTeamPlayers = awayTeamPlayers;
	}

	public int getAwayTeamScore() {
		return awayTeamScore;
	}

	public void setAwayTeamScore(int awayTeamScore) {
		this.awayTeamScore = awayTeamScore;
	}

	public int getHomeTeamLike() {
		return homeTeamLike;
	}

	public void setHomeTeamLike(int homeTeamLike) {
		this.homeTeamLike = homeTeamLike;
	}

	public int getAwayTeamLike() {
		return awayTeamLike;
	}

	public void setAwayTeamLike(int awayTeamLike) {
		this.awayTeamLike = awayTeamLike;
	}

	public Player getReferee() {
		return referee;
	}

	public void setReferee(Player referee) {
		this.referee = referee;
	}

	public Player getCreator() {
		return creator;
	}

	public void setCreator(Player creator) {
		this.creator = creator;
	}

	public Formation getHomeTeamFormation() {
		return homeTeamFormation;
	}

	public void setHomeTeamFormation(Formation homeTeamFormation) {
		this.homeTeamFormation = homeTeamFormation;
	}

	public Formation getAwayTeamFormation() {
		return awayTeamFormation;
	}

	public void setAwayTeamFormation(Formation awayTeamFormation) {
		this.awayTeamFormation = awayTeamFormation;
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

	public String getChallengeUuid() {
		return challengeUuid;
	}

	public void setChallengeUuid(String challengeUuid) {
		this.challengeUuid = challengeUuid;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

}
