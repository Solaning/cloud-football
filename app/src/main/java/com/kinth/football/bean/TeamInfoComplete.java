package com.kinth.football.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.kinth.football.bean.match.PlayerInTeam;
import com.kinth.football.dao.Team;
import com.kinth.football.ui.team.formation.Formation;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 完整的球队信息，包含team,player,formation
 */

public class TeamInfoComplete implements Parcelable{
	private Team team;
	private List<PlayerInTeam> players;
	private List<Formation> formations;
	private int like;//该球队like个数
	private Boolean liked;//是否已经like该球队
	
	public TeamInfoComplete() {
		super();
		team = new Team();
		players = new ArrayList<PlayerInTeam>();
		formations = new ArrayList<Formation>();
	}

	public static final Parcelable.Creator<TeamInfoComplete> CREATOR = new Creator<TeamInfoComplete>() {
		@Override
		public TeamInfoComplete createFromParcel(Parcel parcel) {
			TeamInfoComplete teamInfoComplete = new TeamInfoComplete();
			teamInfoComplete.team = parcel.readParcelable(Team.class.getClassLoader());
			
			Parcelable[] pars = parcel.readParcelableArray(PlayerInTeam.class.getClassLoader());
			teamInfoComplete.players = Arrays.asList(Arrays.asList(pars).toArray(new PlayerInTeam[pars.length]));
			
			Parcelable[] pars2 = parcel.readParcelableArray(Formation.class.getClassLoader());
			teamInfoComplete.formations = Arrays.asList(Arrays.asList(pars2).toArray(new Formation[pars2.length]));
			
			teamInfoComplete.like = parcel.readInt();
			teamInfoComplete.liked = parcel.readByte() != 0;
			return teamInfoComplete;
		}

		@Override
		public TeamInfoComplete[] newArray(int size) {
			// TODO Auto-generated method stub
			return new TeamInfoComplete[size];
		}
	};
	

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.team, flags);
		dest.writeParcelableArray(this.players.toArray(new PlayerInTeam[this.players.size()]), flags);
		dest.writeParcelableArray(this.formations.toArray(new Formation[this.formations.size()]), flags);
		dest.writeInt(this.like);
		dest.writeByte((byte) (liked == null ? 0 : liked ? 1 : 0));
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public List<PlayerInTeam> getPlayers() {
		return players;
	}

	public void setPlayers(List<PlayerInTeam> players) {
		this.players = players;
	}

	public List<Formation> getFormations() {
		return formations;
	}

	public void setFormations(List<Formation> formations) {
		this.formations = formations;
	}

	public int getLike() {
		return like;
	}

	public void setLike(int like) {
		this.like = like;
	}

	public Boolean isLiked() {
		return liked;
	}

	public void setLiked(Boolean liked) {
		this.liked = liked;
	}
	
}
