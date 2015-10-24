package com.kinth.football.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.kinth.football.CustomApplcation;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.dao.Match;
import com.kinth.football.dao.MatchDao;
import com.kinth.football.dao.MatchTeam;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * 根据球队id查询数据库比赛信息
 * 
 * @author Botision.Huang Date: 2015-4-2 Descp: 根据球队相关信息，从本地数据库中获取得到该球队相关的比赛数据
 */
public class TeamMatchUitl {

	/**
	 * 根据球队id和比赛状态(比赛类型)查询比赛，默认按时间逆序
	 * @param mContext
	 * @param teamUuid
	 * @param matchState
	 * @param matchType
	 * @return
	 */
	public static List<MatchInfo> getTeamMatchList(Context mContext, String teamUuid, String matchState,String matchType) {
		List<MatchInfo> _queryMatchInfoList = new ArrayList<MatchInfo>(); // 最终需要的结果

		// 1、首先根据球队ID获取得到MatchTeam映射表list
		List<MatchTeam> _queryMT = CustomApplcation.getDaoSession(mContext)
				.getMatchTeamDao()._queryTeam_MatchTeamList(teamUuid);
		
		/*
		 * 分为4中情况，（0x11）代表status,match_type都不为空 
		 * （0x10）代表match_type不为空，status为空
		 * （0x01）代表match_type为空，status不为空
		 * （0x00）代表match_type为空，status为空，查询所有
		 */
		byte state = 0x00;
		if(TextUtils.isEmpty(matchState)){
			state = (byte) (state | 0x00);
		}else{
			state = (byte) (state | 0x01);
		}
		if(TextUtils.isEmpty(matchType)){
			state = (byte) (state | 0x00);
		}else{
			state = (byte) (state | 0x10);
		}
		
		for(MatchTeam matchTeam : _queryMT){
			String matchByTeamID = matchTeam.getMatch_id();
			
			QueryBuilder<Match> matchQB = CustomApplcation
					.getDaoSession(mContext).getMatchDao().queryBuilder();
			matchQB.where(MatchDao.Properties.Uuid.eq(matchByTeamID));
			Match match = matchQB.unique(); //再逐一得到每一个比赛实体
			
			switch (state) {
			case 0x00:// 查所有状态和类型
				MatchInfo matchInfo = DBUtil.queryMatchInfoByMatch(mContext, match);
				_queryMatchInfoList.add(matchInfo);
				break;
			case 0x01:// 查特定状态
				if (matchState.equals(match.getState())) {
					MatchInfo matchInfo2 = DBUtil.queryMatchInfoByMatch(mContext,
							match);
					_queryMatchInfoList.add(matchInfo2);
				}
				break;
			case 0x10:// 查特定类型
				if (matchType.equals(match.getType())) {
					MatchInfo matchInfo3 = DBUtil.queryMatchInfoByMatch(mContext,
							match);
					_queryMatchInfoList.add(matchInfo3);
				}
				break;
			case 0x11:// 查特定状态和类型的比赛
				if (matchState.equals(match.getState())
						&& matchType.equals(match.getType())) {
					MatchInfo matchInfo4 = DBUtil.queryMatchInfoByMatch(mContext,
							match);
					_queryMatchInfoList.add(matchInfo4);
				}
				break;
			}
		}
		return _queryMatchInfoList;
	}

		// 2、遍历MatchTeam映射list,得到该球队每一比赛ID
//			for (MatchTeam matchItem : _queryMT) {
//				String matchByTeamID = matchItem.getMatch_id();
//
//				QueryBuilder<Match> matchQB = CustomApplcation
//						.getDaoSession(mContext).getMatchDao().queryBuilder();
//				matchQB.where(MatchDao.Properties.Uuid.eq(matchByTeamID));
//				Match match = matchQB.unique(); //再逐一得到每一个比赛实体
//
//				if (!matchState.isEmpty() && matchType.isEmpty()) { // 查询对应状态的所有比赛类型列表
//					if (match.getState().equals(matchState) ) {
//						MatchInfo matchInfo = new MatchInfo();
//						matchInfo.setUuid(match.getUuid());
//						matchInfo.setName(match.getName());
//						matchInfo.setField(match.getField());
//						matchInfo.setKickOff(match.getKickOff());
//						matchInfo.setType(match.getType());
//						matchInfo.setState(match.getState());
//						matchInfo.setDate(match.getDate());
//						matchInfo.setPlayerCount(match.getPlayerCount());
//						matchInfo.setHomeTeamScore(match.getHomeTeamScore());
//						matchInfo.setAwayTeamScore(match.getAwayTeamScore());
//						matchInfo.setHomeTeamLike(match.getHomeTeamLike());
//						matchInfo.setAwayTeamLike(match.getAwayTeamLike());
//						//跳过referee_id，creator_id
//						matchInfo.setCost(match.getCost());
//						matchInfo.setHomeTeamJersey(match.getHomeTeamJersey());
//						matchInfo.setAwayTeamJersey(match.getAwayTeamJersey());
//						
//						//主队阵容
//						FormationDB homeTeamFormationDB = CustomApplcation.getDaoSession(mContext).getFormationDBDao().load(match.getHomeTeamFormation_id());
//						Formation homeTeamFormation = new Formation();
//						homeTeamFormation.setAuthor(homeTeamFormationDB.getPlayer());//TODO 
//						homeTeamFormation.setDescription(homeTeamFormationDB.getDescription());
//						homeTeamFormation.setImage(homeTeamFormationDB.getImage());
//						homeTeamFormation.setName(homeTeamFormationDB.getName());
//						homeTeamFormation.setUuid(homeTeamFormationDB.getUuid());
//						matchInfo.setHomeTeamFormation(homeTeamFormation);
//						
//						//客队阵容
//						FormationDB awayTeamFormationDB = CustomApplcation.getDaoSession(mContext).getFormationDBDao().load(match.getAwayTeamFormation_id());
//						Formation awayTeamFormation = new Formation();
//						awayTeamFormation.setUuid(match.getAwayTeamFormation_id());
//						awayTeamFormation.setAuthor(awayTeamFormationDB.getPlayer());
//						awayTeamFormation.setDescription(awayTeamFormationDB.getDescription());
//						awayTeamFormation.setImage(awayTeamFormationDB.getImage());
//						awayTeamFormation.setName(awayTeamFormationDB.getName());
//						matchInfo.setAwayTeamFormation(awayTeamFormation);
//						
//						QueryBuilder<Player> qb = CustomApplcation.getDaoSession(mContext).getPlayerDao().queryBuilder();
//						qb.where(PlayerDao.Properties.Uuid.eq(match.getReferee_id()));
//						Query<Player> query = qb.build();
//						
//						// 查询裁判的信息
//						Player referee = query.unique();
//						matchInfo.setReferee(referee);
//						//设置创建者
//						query.setParameter(0, match.getCreator_id());
//						Player creator = query.unique();
//						matchInfo.setCreator(creator);
//
//						String matchID = match.getUuid();
//						QueryBuilder<MatchTeam> matchteamQB = CustomApplcation
//								.getDaoSession(mContext).getMatchTeamDao()
//								.queryBuilder();
//						matchteamQB.where(MatchTeamDao.Properties.Match_id
//								.eq(matchID));
//						// 4、获取待开赛下的两只球队及球队成员
//						List<MatchTeam> theTeamsOfMatch = matchteamQB.list();
//						for (MatchTeam theItem : theTeamsOfMatch) {
//							if (theItem.getHome_match() == 1) { // 说明该MatchTeam中的球队为主队
//								QueryBuilder<Team> homeTeamQB = CustomApplcation
//										.getDaoSession(mContext).getTeamDao()
//										.queryBuilder();
//								homeTeamQB.where(TeamDao.Properties.Uuid
//										.eq(theItem.getTeam_id()));
//								Team homeTeam = homeTeamQB.unique();
//								// 获取该球队下面的成员
//								List<PlayerInTeam> homeTeamPlayers = getTeamPlayersByTeamID(
//										mContext, homeTeam.getUuid());
//								matchInfo.setHomeTeam(homeTeam);
//								matchInfo.setHomeTeamPlayers(homeTeamPlayers);
//								matchInfo.setHomeTeamScore(theItem.getScore());
//							} else if (theItem.getHome_match() == 0) { // 客队
//								// 先获取客队球队
//								QueryBuilder<Team> teamQB = CustomApplcation
//										.getDaoSession(mContext).getTeamDao()
//										.queryBuilder();
//								teamQB.where(TeamDao.Properties.Uuid.eq(theItem
//										.getTeam_id()));
//								Team awayTeam = teamQB.unique();
//								// 再获取该球队下面的成员
//								List<PlayerInTeam> awayTeamPlayers = getTeamPlayersByTeamID(
//										mContext, awayTeam.getUuid());
//								matchInfo.setAwayTeam(awayTeam);
//								matchInfo.setAwayTeamPlayers(awayTeamPlayers);
//								matchInfo.setAwayTeamScore(theItem.getScore());
//							}
//						}
//						_queryMatchInfoList.add(matchInfo);
//					}
//				} 
//				else if (!matchState.isEmpty() && !matchType.isEmpty()) {// 查询对应状态的对应比赛类型列表    --zyq 4.4
//					if (match.getState().equals(matchState) && match.getType().equals(matchType)) {
//						MatchInfo matchInfo = new MatchInfo();
//						matchInfo.setUuid(match.getUuid());
//						matchInfo.setName(match.getName());
//						matchInfo.setField(match.getField());
//						matchInfo.setKickOff(match.getKickOff());
//						matchInfo.setType(match.getType());
//						matchInfo.setState(match.getState());
//						matchInfo.setDate(match.getDate());
//						matchInfo.setPlayerCount(match.getPlayerCount());
//						matchInfo.setHomeTeamScore(match.getHomeTeamScore());
//						matchInfo.setAwayTeamScore(match.getAwayTeamScore());
//						matchInfo.setHomeTeamLike(match.getHomeTeamLike());
//						matchInfo.setAwayTeamLike(match.getAwayTeamLike());
//						//跳过referee_id，creator_id
//						matchInfo.setCost(match.getCost());
//						matchInfo.setHomeTeamJersey(match.getHomeTeamJersey());
//						matchInfo.setAwayTeamJersey(match.getAwayTeamJersey());
//						
//						//主队阵容
//						FormationDB homeTeamFormationDB = CustomApplcation.getDaoSession(mContext).getFormationDBDao().load(match.getHomeTeamFormation_id());
//						Formation homeTeamFormation = new Formation();
//						homeTeamFormation.setAuthor(homeTeamFormationDB.getPlayer());
//						homeTeamFormation.setDescription(homeTeamFormationDB.getDescription());
//						homeTeamFormation.setImage(homeTeamFormationDB.getImage());
//						homeTeamFormation.setName(homeTeamFormationDB.getName());
//						homeTeamFormation.setUuid(homeTeamFormationDB.getUuid());
//						matchInfo.setHomeTeamFormation(homeTeamFormation);
//						
//						//客队阵容
//						FormationDB awayTeamFormationDB = CustomApplcation.getDaoSession(mContext).getFormationDBDao().load(match.getAwayTeamFormation_id());
//						Formation awayTeamFormation = new Formation();
//						awayTeamFormation.setUuid(match.getAwayTeamFormation_id());
//						awayTeamFormation.setAuthor(awayTeamFormationDB.getPlayer());
//						awayTeamFormation.setDescription(awayTeamFormationDB.getDescription());
//						awayTeamFormation.setImage(awayTeamFormationDB.getImage());
//						awayTeamFormation.setName(awayTeamFormationDB.getName());
//						matchInfo.setAwayTeamFormation(awayTeamFormation);
//						
//						QueryBuilder<Player> qb = CustomApplcation.getDaoSession(mContext).getPlayerDao().queryBuilder();
//						qb.where(PlayerDao.Properties.Uuid.eq(match.getReferee_id()));
//						Query<Player> query = qb.build();
//						
//						// 查询裁判的信息
//						Player referee = query.unique();
//						matchInfo.setReferee(referee);
//						//设置创建者
//						query.setParameter(0, match.getCreator_id());
//						Player creator = query.unique();
//						matchInfo.setCreator(creator);
//
//						String matchID = match.getUuid();
//						QueryBuilder<MatchTeam> matchteamQB = CustomApplcation
//								.getDaoSession(mContext).getMatchTeamDao()
//								.queryBuilder();
//						matchteamQB.where(MatchTeamDao.Properties.Match_id
//								.eq(matchID));
//						// 4、获取待开赛下的两只球队及球队成员
//						List<MatchTeam> theTeamsOfMatch = matchteamQB.list();
//						for (MatchTeam theItem : theTeamsOfMatch) {
//							if (theItem.getHome_match() == 1) { // 说明该MatchTeam中的球队为主队
//								QueryBuilder<Team> homeTeamQB = CustomApplcation
//										.getDaoSession(mContext).getTeamDao()
//										.queryBuilder();
//								homeTeamQB.where(TeamDao.Properties.Uuid
//										.eq(theItem.getTeam_id()));
//								Team homeTeam = homeTeamQB.unique();
//								// 获取该球队下面的成员
//								List<PlayerInTeam> homeTeamPlayers = getTeamPlayersByTeamID(
//										mContext, homeTeam.getUuid());
//								matchInfo.setHomeTeam(homeTeam);
//								matchInfo.setHomeTeamPlayers(homeTeamPlayers);
//								matchInfo.setHomeTeamScore(theItem.getScore());
//							} else if (theItem.getHome_match() == 0) { // 客队
//								// 先获取客队球队
//								QueryBuilder<Team> teamQB = CustomApplcation
//										.getDaoSession(mContext).getTeamDao()
//										.queryBuilder();
//								teamQB.where(TeamDao.Properties.Uuid.eq(theItem
//										.getTeam_id()));
//								Team awayTeam = teamQB.unique();
//								// 再获取该球队下面的成员
//								List<PlayerInTeam> awayTeamPlayers = getTeamPlayersByTeamID(
//										mContext, awayTeam.getUuid());
//								matchInfo.setAwayTeam(awayTeam);
//								matchInfo.setAwayTeamPlayers(awayTeamPlayers);
//								matchInfo.setAwayTeamScore(theItem.getScore());
//							}
//						}
//						_queryMatchInfoList.add(matchInfo);
//					}
//				}else { // 查询该球队的所有比赛列表
//					MatchInfo matchInfo = new MatchInfo();
//					matchInfo.setUuid(match.getUuid());
//					matchInfo.setName(match.getName());
//					matchInfo.setField(match.getField());
//					matchInfo.setKickOff(match.getKickOff());
//					matchInfo.setType(match.getType());
//					matchInfo.setState(match.getState());
//					matchInfo.setDate(match.getDate());
//					matchInfo.setPlayerCount(match.getPlayerCount());
//					matchInfo.setHomeTeamScore(match.getHomeTeamScore());
//					matchInfo.setAwayTeamScore(match.getAwayTeamScore());
//					matchInfo.setHomeTeamLike(match.getHomeTeamLike());
//					matchInfo.setAwayTeamLike(match.getAwayTeamLike());
//					//跳过referee_id，creator_id
//					matchInfo.setCost(match.getCost());
//					matchInfo.setHomeTeamJersey(match.getHomeTeamJersey());
//					matchInfo.setAwayTeamJersey(match.getAwayTeamJersey());
//
//					//主队阵容
//					FormationDB homeTeamFormationDB = CustomApplcation.getDaoSession(mContext).getFormationDBDao().load(match.getHomeTeamFormation_id());
//					Formation homeTeamFormation = new Formation();
//					homeTeamFormation.setAuthor(homeTeamFormationDB.getPlayer());
//					homeTeamFormation.setDescription(homeTeamFormationDB.getDescription());
//					homeTeamFormation.setImage(homeTeamFormationDB.getImage());
//					homeTeamFormation.setName(homeTeamFormationDB.getName());
//					homeTeamFormation.setUuid(homeTeamFormationDB.getUuid());
//					matchInfo.setHomeTeamFormation(homeTeamFormation);
//					
//					//客队阵容
//					FormationDB awayTeamFormationDB = CustomApplcation.getDaoSession(mContext).getFormationDBDao().load(match.getAwayTeamFormation_id());
//					Formation awayTeamFormation = new Formation();
//					awayTeamFormation.setUuid(match.getAwayTeamFormation_id());
//					awayTeamFormation.setAuthor(awayTeamFormationDB.getPlayer());
//					awayTeamFormation.setDescription(awayTeamFormationDB.getDescription());
//					awayTeamFormation.setImage(awayTeamFormationDB.getImage());
//					awayTeamFormation.setName(awayTeamFormationDB.getName());
//					matchInfo.setAwayTeamFormation(awayTeamFormation);
//					
//
//					QueryBuilder<Player> qb = CustomApplcation.getDaoSession(mContext).getPlayerDao().queryBuilder();
//					qb.where(PlayerDao.Properties.Uuid.eq(match.getReferee_id()));
//					Query<Player> query = qb.build();
//					
//					// 查询裁判的信息
//					Player referee = query.unique();
//					matchInfo.setReferee(referee);
//					//设置创建者
//					query.setParameter(0, match.getCreator_id());
//					Player creator = query.unique();
//					matchInfo.setCreator(creator);
//
//					String matchID = match.getUuid();
//					QueryBuilder<MatchTeam> matchteamQB = CustomApplcation
//							.getDaoSession(mContext).getMatchTeamDao()
//							.queryBuilder();
//					matchteamQB.where(MatchTeamDao.Properties.Match_id
//							.eq(matchID));
//					// 4、获取待开赛下的两只球队及球队成员
//					List<MatchTeam> theTeamsOfMatch = matchteamQB.list();
//					for (MatchTeam theItem : theTeamsOfMatch) {
//						if (theItem.getHome_match() == 1) { // 说明该MatchTeam中的球队为主队
//							QueryBuilder<Team> homeTeamQB = CustomApplcation
//									.getDaoSession(mContext).getTeamDao()
//									.queryBuilder();
//							homeTeamQB.where(TeamDao.Properties.Uuid.eq(theItem
//									.getTeam_id()));
//							Team homeTeam = homeTeamQB.unique();
//							// 获取该球队下面的成员
//							List<PlayerInTeam> homeTeamPlayers = getTeamPlayersByTeamID(
//									mContext, homeTeam.getUuid());
//							matchInfo.setHomeTeam(homeTeam);
//							matchInfo.setHomeTeamPlayers(homeTeamPlayers);
//							matchInfo.setHomeTeamScore(theItem.getScore());
//						} else if (theItem.getHome_match() == 0) { // 客队
//							// 先获取客队球队
//							QueryBuilder<Team> teamQB = CustomApplcation
//									.getDaoSession(mContext).getTeamDao()
//									.queryBuilder();
//							teamQB.where(TeamDao.Properties.Uuid.eq(theItem
//									.getTeam_id()));
//							Team awayTeam = teamQB.unique();
//							// 再获取该球队下面的成员
//							List<PlayerInTeam> awayTeamPlayers = getTeamPlayersByTeamID(
//									mContext, awayTeam.getUuid());
//							matchInfo.setAwayTeam(awayTeam);
//							matchInfo.setAwayTeamPlayers(awayTeamPlayers);
//							matchInfo.setAwayTeamScore(theItem.getScore());
//						}
//					}
//					_queryMatchInfoList.add(matchInfo);
//				}
//			}


}
