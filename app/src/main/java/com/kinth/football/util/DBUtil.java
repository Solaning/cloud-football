package com.kinth.football.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.kinth.football.CustomApplcation;
import com.kinth.football.bean.TeamInfoComplete;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.bean.match.PlayerInTeam;
import com.kinth.football.bean.moments.Comment;
import com.kinth.football.bean.moments.Sharing;
import com.kinth.football.bean.moments.SharingWithComments;
import com.kinth.football.config.Config;
import com.kinth.football.config.MatchStateEnum;
import com.kinth.football.dao.CommentDB;
import com.kinth.football.dao.CommentDBDao;
import com.kinth.football.dao.FormationDB;
import com.kinth.football.dao.FormationDBDao;
import com.kinth.football.dao.Match;
import com.kinth.football.dao.MatchDao;
import com.kinth.football.dao.MatchTeam;
import com.kinth.football.dao.MatchTeamDao;
import com.kinth.football.dao.Player;
import com.kinth.football.dao.PlayerDao;
import com.kinth.football.dao.SharingDB;
import com.kinth.football.dao.SharingDBDao;
import com.kinth.football.dao.Team;
import com.kinth.football.dao.TeamDao;
import com.kinth.football.dao.TeamPlayer;
import com.kinth.football.dao.TeamPlayerDao;
import com.kinth.football.manager.UserManager;
import com.kinth.football.ui.team.formation.Formation;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * 数据库Dao
 * @author Sola
 * 
 */
public class DBUtil {
	
	/**
	 * 异步保存比赛数据到数据库，球队阵容只保存阵容id
	 * @param mContext
	 * @param matchInfoList
	 */
	/*public static void saveMatchInfoListToDB(final Context mContext, final List<MatchInfo> matchInfoList,final Handler handler){
		new Thread(new Runnable(){
			@Override
			public void run() {
				PlayerDao playerDao = CustomApplcation.getDaoSession(mContext).getPlayerDao();
				TeamDao teamDao = CustomApplcation.getDaoSession(mContext).getTeamDao();
				MatchDao matchDao = CustomApplcation.getDaoSession(mContext).getMatchDao();
				MatchTeamDao matchTeamDao = CustomApplcation.getDaoSession(mContext).getMatchTeamDao();
				TeamPlayerDao teamPlayerDao = CustomApplcation.getDaoSession(mContext).getTeamPlayerDao();
				MatchTeamPlayerDao matchTeamPlayerDao = CustomApplcation.getDaoSession(mContext).getMatchTeamPlayerDao();
				FormationDBDao formationDBDao = CustomApplcation.getDaoSession(mContext).getFormationDBDao();
				User user = UserManager.getInstance(mContext).getCurrentUser();
				
				for(MatchInfo matchInfoItem : matchInfoList){
					teamDao.insertOrReplace(matchInfoItem.getHomeTeam());//插入主队信息
					if(matchInfoItem.getAwayTeam()!=null)
						teamDao.insertOrReplace(matchInfoItem.getAwayTeam());//插入客队信息
					playerDao.insertOrReplace(matchInfoItem.getReferee());//保存裁判的个人信息
					playerDao.insertOrReplace(matchInfoItem.getCreator());//保存创建者的个人信息
					
					//-----------------Start 保存MatchTeam关系-------------------
					//根据match的id查出所有的MatchTeam映射关系
					List<MatchTeam> _queryTeam = matchTeamDao._queryMatch_MatchTeamList(matchInfoItem.getUuid());
					Long matchTeamId_Home = 0l;
					Long matchTeamId_Away = 0l;
					for(MatchTeam matchTeamItem : _queryTeam){
						if(matchTeamItem.getTeam_id().equals(matchInfoItem.getHomeTeam().getUuid())){//找出是matchTeam关联表是否有主队id的记录
							matchTeamId_Home = matchTeamItem.getId();
						}
						if(matchTeamItem.getTeam_id().equals(matchInfoItem.getAwayTeam().getUuid())){//找出是matchTeam关联表是否有客队id的记录
							matchTeamId_Away = matchTeamItem.getId();
						}
					}
					MatchTeam daoMatchHomeTeam = new MatchTeam();//更新主队与比赛的映射关系
					{
						if(matchTeamId_Home != 0l){
							daoMatchHomeTeam.setId(matchTeamId_Home);
						}
						daoMatchHomeTeam.setMatch_id(matchInfoItem.getUuid());
						daoMatchHomeTeam.setTeam_id(matchInfoItem.getHomeTeam().getUuid());//主队的uuid
						daoMatchHomeTeam.setHome_match(1);//主场
						daoMatchHomeTeam.setScore(matchInfoItem.getHomeTeamScore());
					}
					matchTeamId_Home = matchTeamDao.insertOrReplace(daoMatchHomeTeam);//取到保存到数据库时的记录id
					
					MatchTeam daoMatchAwayTeam = new MatchTeam();//更新客队与比赛的映射关系
					{
						if(matchTeamId_Away != 0l){
							daoMatchAwayTeam.setId(matchTeamId_Away);
						}
						daoMatchAwayTeam.setMatch_id(matchInfoItem.getUuid());
						daoMatchAwayTeam.setTeam_id(matchInfoItem.getAwayTeam().getUuid());
						daoMatchAwayTeam.setHome_match(0);//客场
						daoMatchAwayTeam.setScore(matchInfoItem.getAwayTeamScore());
					}
					matchTeamId_Away = matchTeamDao.insertOrReplace(daoMatchAwayTeam);
					//-------------------End 保存MatchTeam关系-------------------
					
					//-------------------Start---------------------------
					//创建中的比赛没有球员的信息，只有用户本人，创建者、裁判的信息，只保存用户本人的关系  TODO
					if(MatchStateEnum.INVITING.getValue().equals(matchInfoItem.getState())){
						Player mySelft = user.getPlayer();
						
						//看本人是在主队还是客队
						
						//根据player的id查出所有的TeamPlayer映射关系
						List<TeamPlayer> _queryPlayer = teamPlayerDao._queryPlayer_TeamPlayerList(mySelft.getUuid());
						Long teamPlayerId = 0l;//所在对的id
						boolean inHomeTeam = false;

						for(TeamPlayer teamPlayerItem : _queryPlayer){
							if(teamPlayerItem.getTeam_id().equals(matchInfoItem.getHomeTeam().getUuid())){//找出是该球员是否有该球队id的记录
								teamPlayerId = teamPlayerItem.getId();
								inHomeTeam = true;
								break;
							}
							if(teamPlayerItem.getTeam_id().equals(matchInfoItem.getAwayTeam().getUuid())){//找出是该球员是否有该球队id的记录
								teamPlayerId = teamPlayerItem.getId();
								inHomeTeam = false;
								break;
							}
						}
						TeamPlayer daoTeamPlayer = new TeamPlayer();//更新球队与球员的映射关系
						{
							if(teamPlayerId != 0l){
								daoTeamPlayer.setId(teamPlayerId);
							}
							daoTeamPlayer.setPlayer_id(mySelft.getUuid());
							if(inHomeTeam){
								daoTeamPlayer.setTeam_id(matchInfoItem.getHomeTeam().getUuid());
								if(mySelft.getUuid().equals(matchInfoItem.getHomeTeam().getCreatorUuid())){
									daoTeamPlayer.setCreator(true);
								}else{
									daoTeamPlayer.setCreator(false);
								}
							}else{
								daoTeamPlayer.setTeam_id(matchInfoItem.getAwayTeam().getUuid());
								if(mySelft.getUuid().equals(matchInfoItem.getAwayTeam().getCreatorUuid())){
									daoTeamPlayer.setCreator(true);
								}else{
									daoTeamPlayer.setCreator(false);
								}
							}
							daoTeamPlayer.setType("");
						}
						teamPlayerId = teamPlayerDao.insertOrReplace(daoTeamPlayer);
						playerDao.insertOrReplace(mySelft);
						
						Long matchTeamPlayer_Id = 0l;
						List<MatchTeamPlayer> _queryMatchTeam =	matchTeamPlayerDao._queryMatchTeam_MatchTeamPlayerList(matchTeamId_Home);
						for(MatchTeamPlayer matchTeamPlayerItem : _queryMatchTeam){
							if(matchTeamPlayerItem.getPlayer_id().equals(mySelft.getUuid())){
								matchTeamPlayer_Id = matchTeamPlayerItem.getId();
								break;
							}
						}
						MatchTeamPlayer matchTeamPlayer = new MatchTeamPlayer();
						{
							if(matchTeamPlayer_Id != 0l){
								matchTeamPlayer.setId(matchTeamPlayer_Id);
							}
							matchTeamPlayer.setMatch_team_id(matchTeamId_Home);
							matchTeamPlayer.setPlayer_id(mySelft.getUuid());
						}
						matchTeamPlayerDao.insertOrReplace(matchTeamPlayer);
					}
					//-------------------End------------------------------
					
					//-------------------Start 保存主队的Player、TeamPlayer、MatchTeamPlayer关系------------------
					for(PlayerInTeam play : matchInfoItem.getHomeTeamPlayers()){//插入主队的球员信息
						//根据player的id查出所有的TeamPlayer映射关系
						List<TeamPlayer> _queryPlayer = teamPlayerDao._queryPlayer_TeamPlayerList(play.getPlayer().getUuid());
						Long teamPlayerId = 0l;
						for(TeamPlayer teamPlayerItem : _queryPlayer){
							if(teamPlayerItem.getTeam_id().equals(matchInfoItem.getHomeTeam().getUuid())){//找出是该球员是否有该球队id的记录
								teamPlayerId = teamPlayerItem.getId();
								break;
							}
						}
						TeamPlayer daoTeamPlayer = new TeamPlayer();//更新球队与球员的映射关系
						{
							if(teamPlayerId != 0l){
								daoTeamPlayer.setId(teamPlayerId);
							}
							daoTeamPlayer.setPlayer_id(play.getPlayer().getUuid());
							daoTeamPlayer.setTeam_id(matchInfoItem.getHomeTeam().getUuid());
							daoTeamPlayer.setType(play.getType());
							daoTeamPlayer.setCreator(play.isCreator());
						}
						teamPlayerDao.insertOrReplace(daoTeamPlayer);//主队的球员
						
						playerDao.insertOrReplace(play.getPlayer());
						
						Long matchTeamPlayer_Id = 0l;
						List<MatchTeamPlayer> _queryMatchTeam =	matchTeamPlayerDao._queryMatchTeam_MatchTeamPlayerList(matchTeamId_Home);
						for(MatchTeamPlayer matchTeamPlayerItem : _queryMatchTeam){
							if(matchTeamPlayerItem.getPlayer_id().equals(play.getPlayer().getUuid())){
								matchTeamPlayer_Id = matchTeamPlayerItem.getId();
								break;
							}
						}
						MatchTeamPlayer matchTeamPlayer = new MatchTeamPlayer();
						{
							if(matchTeamPlayer_Id != 0l){
								matchTeamPlayer.setId(matchTeamPlayer_Id);
							}
							matchTeamPlayer.setMatch_team_id(matchTeamId_Home);
							matchTeamPlayer.setPlayer_id(play.getPlayer().getUuid());
						}
						matchTeamPlayerDao.insertOrReplace(matchTeamPlayer);
					}
					//-------------------End 保存主队的Player、TeamPlayer、MatchTeamPlayer关系------------------
					
					//-------------------Start 保存客队的Player、TeamPlayer、MatchTeamPlayer关系------------------
					for(PlayerInTeam play : matchInfoItem.getAwayTeamPlayers()){//插入客队的球员信息
						//根据player的id查出所有的TeamPlayer映射关系
						List<TeamPlayer> _queryPlayer = teamPlayerDao._queryPlayer_TeamPlayerList(play.getPlayer().getUuid());
						Long teamPlayerId = 0l;
						for(TeamPlayer teamPlayerItem : _queryPlayer){
							if(teamPlayerItem.getTeam_id().equals(matchInfoItem.getAwayTeam().getUuid())){//找出是该球员是否有该球队id的记录
								teamPlayerId = teamPlayerItem.getId();
								break;
							}
						}
						
						TeamPlayer daoTeamPlayer = new TeamPlayer();//更新球队与球员的映射关系
						{
							if(teamPlayerId != 0l){
								daoTeamPlayer.setId(teamPlayerId);
							}
							daoTeamPlayer.setPlayer_id(play.getPlayer().getUuid());
							daoTeamPlayer.setTeam_id(matchInfoItem.getAwayTeam().getUuid());
							daoTeamPlayer.setType(play.getType());
							daoTeamPlayer.setCreator(play.isCreator());
						}
						teamPlayerDao.insertOrReplace(daoTeamPlayer);//客队的球员
						
						playerDao.insertOrReplace(play.getPlayer());
						
						Long matchTeamPlayer_Id = 0l;
						List<MatchTeamPlayer> _queryMatchTeam =	matchTeamPlayerDao._queryMatchTeam_MatchTeamPlayerList(matchTeamId_Away);
						for(MatchTeamPlayer matchTeamPlayerItem : _queryMatchTeam){
							if(matchTeamPlayerItem.getPlayer_id().equals(play.getPlayer().getUuid())){
								matchTeamPlayer_Id = matchTeamPlayerItem.getId();
								break;
							}
						}
						MatchTeamPlayer matchTeamPlayer = new MatchTeamPlayer();
						{
							if(matchTeamPlayer_Id != 0l){
								matchTeamPlayer.setId(matchTeamPlayer_Id);
							}
							matchTeamPlayer.setMatch_team_id(matchTeamId_Away);
							matchTeamPlayer.setPlayer_id(play.getPlayer().getUuid());
						}
						matchTeamPlayerDao.insertOrReplace(matchTeamPlayer);
					}
					//-------------------End 保存客队的Player、TeamPlayer、MatchTeamPlayer关系------------------
					
					//--------------------Start 保存Match的信息---------------------
					Match daoMatch = new Match();//保存比赛信息
					{
						daoMatch.setUuid(matchInfoItem.getUuid());
						daoMatch.setName(matchInfoItem.getName());
						daoMatch.setField(matchInfoItem.getField());
						daoMatch.setKickOff(matchInfoItem.getKickOff());
						daoMatch.setType(matchInfoItem.getType());
						daoMatch.setState(matchInfoItem.getState());
						daoMatch.setDate(matchInfoItem.getDate());
						daoMatch.setPlayerCount(matchInfoItem.getPlayerCount());
						daoMatch.setHomeTeamScore(matchInfoItem.getHomeTeamScore());
						daoMatch.setAwayTeamScore(matchInfoItem.getAwayTeamScore());
						daoMatch.setHomeTeamLike(matchInfoItem.getHomeTeamLike());//喜欢主队
						daoMatch.setAwayTeamLike(matchInfoItem.getAwayTeamLike());
						daoMatch.setReferee_id(matchInfoItem.getReferee().getUuid());
						daoMatch.setCreator_id(matchInfoItem.getCreator().getUuid());//创建者id
						daoMatch.setCost(matchInfoItem.getCost());//费用
						daoMatch.setHomeTeamJersey(matchInfoItem.getHomeTeamJersey());//主队球衣
						daoMatch.setAwayTeamJersey(matchInfoItem.getAwayTeamJersey());//客队球衣
//						if(matchInfoItem.getHomeTeamFormation() != null){
//							//保存主队的比赛阵容
//							Formation2FormationDB(matchInfoItem.getHomeTeamFormation(),matchInfoItem.getHomeTeam(),formationDBDao);
//							daoMatch.setHomeTeamFormation_id(matchInfoItem.getHomeTeamFormation().getUuid());//主队阵容
//						}
//						if(matchInfoItem.getAwayTeamFormation() != null){
//							//保存客队的比赛阵容
//							Formation2FormationDB(matchInfoItem.getAwayTeamFormation(),matchInfoItem.getAwayTeam(),formationDBDao);
//							daoMatch.setAwayTeamFormation_id(matchInfoItem.getAwayTeamFormation().getUuid());//客队阵容
//						}
					}
					matchDao.insertOrReplace(daoMatch);//插入一条比赛信息
					//--------------------End 保存Match的信息---------------------
				}
				
				if(handler != null){
					//TODO
				};
			}}).start();
	}*/
	
	/**
	 * 从比赛详情中保存比赛实体到数据库
	 */
	public static void saveMatchToDB(final Context mContext, final MatchInfo matchInfoList, final MatchStateEnum state){
		new Thread(new Runnable(){

			@Override
			public void run() {
				MatchDao matchDao = CustomApplcation.getDaoSession(mContext).getMatchDao();
				Match daoMatch = new Match();//保存比赛信息
				{
					daoMatch.setUuid(matchInfoList.getUuid());
					daoMatch.setName(matchInfoList.getName());
					daoMatch.setField(matchInfoList.getField());
					daoMatch.setKickOff(matchInfoList.getKickOff());
					daoMatch.setType(matchInfoList.getType());
					daoMatch.setState(state.getValue());
					daoMatch.setPlayerCount(matchInfoList.getPlayerCount());
					daoMatch.setHomeTeamScore(matchInfoList.getHomeTeamScore());
					daoMatch.setAwayTeamScore(matchInfoList.getAwayTeamScore());
					daoMatch.setReferee_id(matchInfoList.getReferee().getUuid());
					daoMatch.setCreator_id(matchInfoList.getCreator().getUuid());//创建者id默认为空 TODO
				}
				matchDao.update(daoMatch);//更新一条比赛信息
			}}).start();
	}
	
	//见函数名
	public static FormationDB Formation2FormationDB(Formation formation, Team team, FormationDBDao formationDBDao){
		FormationDB formationDB = new FormationDB();
		formationDB.setDescription(formation.getDescription());
		formationDB.setImage(formation.getImage());
		formationDB.setName(formation.getName());
		formationDB.setUuid(formation.getUuid());
//		formationDB.setTeam(team);//关联更新？？？ TODO
		formationDB.setTeam_id(team.getUuid());
		
		formationDBDao.insertOrReplace(formationDB);
		return formationDB;
	}
	
	/**
	 * 保存球队数据到数据库
	 * @param mContext
	 * @param teamInfoList
	 */
	public static void saveTeamInfoToDB(final Context mContext,
			final TeamInfoComplete teamInfo) {
		// （1）将“我的球队”信息加入到表Team中
		TeamDao teamDao = CustomApplcation.getDaoSession(mContext).getTeamDao();
		TeamPlayerDao tpDao = CustomApplcation.getDaoSession(mContext)
				.getTeamPlayerDao();
		PlayerDao playerDao = CustomApplcation.getDaoSession(mContext)
				.getPlayerDao();

		teamDao.insertOrReplace(teamInfo.getTeam());// 保存球队
		// 保存球员与球队的关系
		for (PlayerInTeam player : teamInfo.getPlayers()) {
			playerDao.insertOrReplace(player.getPlayer());
			// 根据球队id查询teamPlayer关联表
			QueryBuilder<TeamPlayer> teamPlayerQB = CustomApplcation
					.getDaoSession(mContext)
					.getTeamPlayerDao()
					.queryBuilder()
					.where(TeamPlayerDao.Properties.Team_id.eq(teamInfo
							.getTeam().getUuid()));
			List<TeamPlayer> allTeamPlayer = teamPlayerQB.list();
			long teamPlayerId = 0l;
			for (TeamPlayer teamPlayer : allTeamPlayer) {
				if (teamPlayer.getPlayer_id().equals(
						player.getPlayer().getUuid())) {
					teamPlayerId = teamPlayer.getId();
					break;
				}
			}
			TeamPlayer tp = new TeamPlayer();
			if (teamPlayerId != 0l) {
				tp.setId(teamPlayerId);
			}
			tp.setPlayer_id(player.getPlayer().getUuid());
			tp.setType(player.getType());
			tp.setCreator(player.isCreator());
			tp.setTeam_id(teamInfo.getTeam().getUuid());
			tpDao.insertOrReplace(tp);
		}
		// 保存阵容与作者的关系、与球队的关系 一对多的关系，阵容为1，其他为多
/*		for (Formation formation : teamInfo.getFormations()) {
			FormationDB formationDB = new FormationDB();
			formationDB.setUuid(formation.getUuid());
			formationDB.setImage(formation.getImage());
			formationDB.setName(formation.getName());
			formationDB.setDescription(formation.getDescription());
			formationDB.setTeam_id(teamInfo.getTeam().getUuid());// 保存与球队的关系
			CustomApplcation.getDaoSession(mContext).getFormationDBDao()
					.insertOrReplace(formationDB);
		}*/
	}
	
	/**
	 * 保存球队数据到数据库
	 * @param mContext
	 * @param teamInfoList
	 */
	public static void saveTeamInfoListToDB(final Context mContext, final List<TeamInfoComplete> teamInfoList){
		//（1）将“我的球队”信息加入到表Team中
		TeamDao teamDao = CustomApplcation.getDaoSession(mContext).getTeamDao();
		TeamPlayerDao tpDao = CustomApplcation.getDaoSession(mContext).getTeamPlayerDao();
		PlayerDao playerDao = CustomApplcation.getDaoSession(mContext).getPlayerDao();
		
		for(TeamInfoComplete item : teamInfoList){
			teamDao.insertOrReplace(item.getTeam());//保存球队
			
			QueryBuilder<TeamPlayer> teamPlayerQB = CustomApplcation.getDaoSession(mContext).getTeamPlayerDao().queryBuilder().where(TeamPlayerDao.Properties.Team_id.eq(item.getTeam().getUuid()));
			List<TeamPlayer> allTeamPlayer = teamPlayerQB.list();
			//保存球员与球队的关系
			for(PlayerInTeam player :item.getPlayers()){
				playerDao.insertOrReplace(player.getPlayer());
				//根据球队id查询teamPlayer关联表
				long teamPlayerId = 0l;
				for(TeamPlayer teamPlayer : allTeamPlayer){
					if(teamPlayer.getPlayer_id().equals(player.getPlayer().getUuid())){
						teamPlayerId = teamPlayer.getId();
						break;
					}
				}
				TeamPlayer tp = new TeamPlayer();
				if(teamPlayerId != 0l){
					tp.setId(teamPlayerId);
				}
				tp.setPlayer_id(player.getPlayer().getUuid());
				tp.setType(player.getType());
				tp.setCreator(player.isCreator());
				tp.setTeam_id(item.getTeam().getUuid());
				tpDao.insertOrReplace(tp);
			}
			//保存阵容与作者的关系、与球队的关系  一对多的关系，阵容为1，其他为多
//			for(Formation formation : item.getFormations()){
//				FormationDB formationDB = new FormationDB();
//				formationDB.setUuid(formation.getUuid());
//				formationDB.setImage(formation.getImage());
//				formationDB.setName(formation.getName());
//				formationDB.setDescription(formation.getDescription());
//				formationDB.setTeam_id(item.getTeam().getUuid());//保存与球队的关系
//				CustomApplcation.getDaoSession(mContext).getFormationDBDao().insertOrReplace(formationDB);
//			}
		}
	}
	
	/**
	 * 根据用户id从数据库查询球队数据  TODO 添加like和liked
	 * @param mContext
	 * @param teamInfoList
	 */
	public static List<TeamInfoComplete> getTeamInfoListFromDbById(final Context mContext, String uid){
		List<TeamInfoComplete> myTeamList = new ArrayList<TeamInfoComplete>();
		
		QueryBuilder<TeamPlayer> qb = CustomApplcation.getDaoSession(mContext).getTeamPlayerDao().queryBuilder();
		FormationDBDao formationDBDao = CustomApplcation.getDaoSession(mContext).getFormationDBDao();
		
		qb.where(TeamPlayerDao.Properties.Player_id.eq(uid));
		List<TeamPlayer> teamPlayerList = qb.list(); //根据账户uuid找出team_player映射组
		
		for(TeamPlayer item : teamPlayerList){
			TeamInfoComplete teamInfoComplete = new TeamInfoComplete();
			QueryBuilder<Team> teamQB = CustomApplcation.getDaoSession(mContext).getTeamDao().queryBuilder();
			teamQB.where(TeamDao.Properties.Uuid.eq(item.getTeam_id()));
			Team team = teamQB.unique();
			
			if(team != null){
				//找到该球队的人数
				teamInfoComplete.setTeam(team);
				teamInfoComplete.setLike(team.getLike() == null ? 0 : team.getLike());
				teamInfoComplete.setLiked(team.getLiked() == null ? false : team.getLiked());
				List<PlayerInTeam> players = new ArrayList<PlayerInTeam>();
				//根据球队id去找所有在该球队的人
				QueryBuilder<TeamPlayer> qqq = CustomApplcation.getDaoSession(mContext).getTeamPlayerDao().queryBuilder();
				qqq.where(TeamPlayerDao.Properties.Team_id.eq(team.getUuid()));
				List<TeamPlayer> teamMemberList = qqq.list();
				for(TeamPlayer teamMeber : teamMemberList){
					Player player = CustomApplcation.getDaoSession(mContext).getPlayerDao().queryBuilder().where(PlayerDao.Properties.Uuid.eq(teamMeber.getPlayer_id())).unique();
					if(player != null){
						PlayerInTeam playerInTeam = new PlayerInTeam(); 
						playerInTeam.setPlayer(player);
						playerInTeam.setType(teamMeber.getType());
						playerInTeam.setCreator(teamMeber.getCreator());
						players.add(playerInTeam);
					}
				}
				teamInfoComplete.setPlayers(players);
				List<FormationDB> formationDBs = formationDBDao._queryTeam_FormationDBList(team.getUuid());
				teamInfoComplete.setFormations(FormationDB2Formation(formationDBs));//找出阵容
				myTeamList.add(teamInfoComplete);
			}
		}
		return myTeamList;
	}
	
	/**
	 * 根据用户id从数据库查询球队数据，需是具有队长权限的
	 * @param mContext
	 * @param uid
	 */
	public static List<TeamInfoComplete> getPrivilegeTeamInfoListFromDbById(final Context mContext, String uid){
		List<TeamInfoComplete> myTeamList = new ArrayList<TeamInfoComplete>();//我是队长的球队---包含教练（第三队长）
		QueryBuilder<TeamPlayer> teamPlayerQB = CustomApplcation.getDaoSession(mContext).getTeamPlayerDao().queryBuilder();
		FormationDBDao formationDBDao = CustomApplcation.getDaoSession(mContext).getFormationDBDao();
		
		teamPlayerQB.where(TeamPlayerDao.Properties.Player_id.eq(uid));
		List<TeamPlayer> teamPlayerList = teamPlayerQB.list(); //根据账户uuid找出team_player映射组
		
		for(TeamPlayer item : teamPlayerList){
			TeamInfoComplete teamInfoComplete = new TeamInfoComplete();
			QueryBuilder<Team> teamQB = CustomApplcation.getDaoSession(mContext).getTeamDao().queryBuilder();
			teamQB.where(TeamDao.Properties.Uuid.eq(item.getTeam_id()));
			Team team = teamQB.unique();
			
			if(team != null && (uid.equals(team.getFirstCaptainUuid()) || uid.equals(team.getSecondCaptainUuid()) ||
					uid.equals(team.getThirdCaptainUuid()))){
				//找到该球队的人数
				teamInfoComplete.setTeam(team);
				List<PlayerInTeam> players = new ArrayList<PlayerInTeam>();
				//根据球队id去找所有在该球队的人
				QueryBuilder<TeamPlayer> qqq = CustomApplcation.getDaoSession(mContext).getTeamPlayerDao().queryBuilder();
				qqq.where(TeamPlayerDao.Properties.Team_id.eq(team.getUuid()));
				List<TeamPlayer> teamMemberList = qqq.list();
				for(TeamPlayer teamMember : teamMemberList){
					Player player = CustomApplcation.getDaoSession(mContext).getPlayerDao().queryBuilder().where(PlayerDao.Properties.Uuid.eq(teamMember.getPlayer_id())).unique();
					if(player != null){
						PlayerInTeam playerInTeam = new PlayerInTeam();
						playerInTeam.setPlayer(player);
						playerInTeam.setCreator(teamMember.getCreator());
						playerInTeam.setType(teamMember.getType());
						players.add(playerInTeam);
					}
				}
				teamInfoComplete.setPlayers(players);
				List<FormationDB> formationDBs = formationDBDao._queryTeam_FormationDBList(team.getUuid());
				teamInfoComplete.setFormations(FormationDB2Formation(formationDBs));//找出阵容
				myTeamList.add(teamInfoComplete);
			}
		}
		return myTeamList;
	}
	
	//见函数名
	public static List<Formation> FormationDB2Formation(List<FormationDB> in){
		if(in == null){
			return null;
		}
		List<Formation> out = new ArrayList<Formation>();
		for(FormationDB item : in){
			Formation formation = new Formation();
			formation.setUuid(item.getUuid());
			formation.setDescription(item.getDescription());
			formation.setImage(item.getImage());
			formation.setName(item.getName());
			out.add(formation);
		}
		return out;
	}
	
	//见函数名
	public static Formation FormationDB2Formation(FormationDB in){
		if(in == null){
			return null;
		}
		Formation out = new Formation();
		out.setUuid(in.getUuid());
		out.setDescription(in.getDescription());
		out.setImage(in.getImage());
		out.setName(in.getName());
		return out;
	}
	
	/**
	 * 根据球队id从数据库加载球队成员
	 * @param mContext
	 * @param teamUuid
	 * @return
	 */
	public static List<Player> LoadTeamMemberFromDB(Context mContext, String teamUuid){
		List<Player> playerList = new ArrayList<Player>();
		QueryBuilder<TeamPlayer> tpQB = CustomApplcation.getDaoSession(mContext).getTeamPlayerDao().queryBuilder();
		tpQB.where(TeamPlayerDao.Properties.Team_id.eq(teamUuid));
		List<TeamPlayer> teamId_playerId_List = tpQB.list();

		QueryBuilder<Player> playerQB = CustomApplcation.getDaoSession(mContext).getPlayerDao().queryBuilder();
		Query<Player> query = playerQB.where(PlayerDao.Properties.Uuid.eq("")).build();
		for (TeamPlayer teamplayer : teamId_playerId_List) {
			query.setParameter(0, teamplayer.getPlayer_id());
			Player player = query.unique();
			if(player != null){
				playerList.add(player);
			}
		}
		return playerList;
	}
	
	/**
	 * 保存球队的成员列表到数据库
	 * @param mContext
	 * @param teamMebList
	 * @param team
	 */
	public static void saveTeamMember2DB(Context mContext, List<PlayerInTeam> teamMebList, Team team){
		PlayerDao playerDao = CustomApplcation.getDaoSession(mContext)
				.getPlayerDao();
		TeamPlayerDao tpDao = CustomApplcation.getDaoSession(mContext)
				.getTeamPlayerDao();

		//根据球队id查询teamPlayer关联表
		QueryBuilder<TeamPlayer> teamPlayerQB = CustomApplcation.getDaoSession(mContext).getTeamPlayerDao().queryBuilder().where(TeamPlayerDao.Properties.Team_id.eq(team.getUuid()));
		List<TeamPlayer> allTeamPlayer = teamPlayerQB.list();
		
		for(PlayerInTeam player : teamMebList){
			playerDao.insertOrReplace(player.getPlayer());
			long teamPlayerId = 0l;
			for(TeamPlayer teamPlayer : allTeamPlayer){
				if(teamPlayer.getPlayer_id().equals(player.getPlayer().getUuid())){
					teamPlayerId = teamPlayer.getId();
					break;
				}
			}
			TeamPlayer tp = new TeamPlayer();
			if(teamPlayerId != 0l){
				tp.setId(teamPlayerId);
			}
			tp.setPlayer_id(player.getPlayer().getUuid());
			tp.setType(player.getType());
			tp.setCreator(player.isCreator());
			tp.setTeam_id(team.getUuid());
			tpDao.insertOrReplace(tp);
		}
	}
	
	/**
	 * 通过Match查询MatchInfo
	 * 
	 * @param mContext
	 * @param match
	 * @return
	 */
	public static MatchInfo queryMatchInfoByMatch(Context mContext, Match match) {
		MatchInfo matchInfo = new MatchInfo();
		matchInfo.setUuid(match.getUuid());
		matchInfo.setName(match.getName());
		matchInfo.setField(match.getField());
		matchInfo.setKickOff(match.getKickOff());
		matchInfo.setPlayerCount(match.getPlayerCount());
		matchInfo.setType(match.getType());
		matchInfo.setState(match.getState());
		matchInfo.setDate(match.getDate());
		matchInfo.setHomeTeamScore(match.getHomeTeamScore());
		matchInfo.setAwayTeamScore(match.getAwayTeamScore());
		matchInfo.setHomeTeamLike(match.getHomeTeamLike());
		matchInfo.setAwayTeamLike(match.getAwayTeamLike());
		matchInfo.setCost(match.getCost());
		matchInfo.setHomeTeamJersey(match.getHomeTeamJersey());
		matchInfo.setAwayTeamJersey(match.getAwayTeamJersey());
		
		Query<Player> queryPlayer = CustomApplcation.getDaoSession(mContext).getPlayerDao().queryBuilder().where(PlayerDao.Properties.Uuid.eq("")).build();
		// 查询裁判的信息
		queryPlayer.setParameter(0, StringUtils.defaultIfEmpty(match.getReferee_id(),""));
		Player referee = queryPlayer.unique();
		matchInfo.setReferee(referee);
		// 查询创建者的信息
		queryPlayer.setParameter(0, StringUtils.defaultIfEmpty(match.getCreator_id(),""));
		Player creator = queryPlayer.unique();
		matchInfo.setCreator(creator);
		
		//查询阵容消息
		//主队阵容
		Query<FormationDB> queryFormation = CustomApplcation.getDaoSession(mContext).getFormationDBDao().queryBuilder().where(FormationDBDao.Properties.Uuid.eq("")).build();
		queryFormation.setParameter(0, StringUtils.defaultIfEmpty(match.getHomeTeamFormation_id(), ""));
		FormationDB db = queryFormation.unique();
		matchInfo.setHomeTeamFormation(DBUtil.FormationDB2Formation(db));
		//客队阵容
		queryFormation.setParameter(0, StringUtils.defaultIfEmpty(match.getAwayTeamFormation_id(),""));
		matchInfo.setAwayTeamFormation(DBUtil.FormationDB2Formation(queryFormation.unique()));
		
		QueryBuilder<MatchTeam> matchteamQB = CustomApplcation
				.getDaoSession(mContext).getMatchTeamDao().queryBuilder();
		matchteamQB.where(MatchTeamDao.Properties.Match_id.eq(match.getUuid()));
		// 4、获取待开赛下的两只球队及球队成员
		List<MatchTeam> theTeamsOfMatch = matchteamQB.list();
		for (MatchTeam theItem : theTeamsOfMatch) {
			if (theItem.getHome_match() == 1) { // 说明该MatchTeam中的球队为主队
				QueryBuilder<Team> homeTeamQB = CustomApplcation
						.getDaoSession(mContext).getTeamDao().queryBuilder();
				homeTeamQB.where(TeamDao.Properties.Uuid.eq(theItem
						.getTeam_id()));
				Team homeTeam = homeTeamQB.unique();
				// 获取该球队下面的成员
				List<PlayerInTeam> homeTeamPlayers = getTeamPlayersByTeamID(
						mContext, homeTeam.getUuid());
				matchInfo.setHomeTeam(homeTeam);
				matchInfo.setHomeTeamPlayers(homeTeamPlayers);
				matchInfo.setHomeTeamScore(theItem.getScore());
			} else if (theItem.getHome_match() == 0) { // 客队
				// 先获取客队球队
				QueryBuilder<Team> teamQB = CustomApplcation
						.getDaoSession(mContext).getTeamDao().queryBuilder();
				teamQB.where(TeamDao.Properties.Uuid.eq(theItem.getTeam_id()));
				Team awayTeam = teamQB.unique();
				// 再获取该球队下面的成员
				List<PlayerInTeam> awayTeamPlayers = getTeamPlayersByTeamID(
						mContext, awayTeam.getUuid());
				matchInfo.setAwayTeam(awayTeam);
				matchInfo.setAwayTeamPlayers(awayTeamPlayers);
				matchInfo.setAwayTeamScore(theItem.getScore());
			}
		}
		return matchInfo;
	}
	
	/**
	 * 获取该球队下面的成员
	 * @param mContext
	 * @param teamID
	 * @return
	 */
	public static List<PlayerInTeam> getTeamPlayersByTeamID(Context mContext,
			String teamID) {
		List<PlayerInTeam> TeamPlayerList = new ArrayList<PlayerInTeam>();
		QueryBuilder<TeamPlayer> tpQB = CustomApplcation
				.getDaoSession(mContext).getTeamPlayerDao().queryBuilder();
		tpQB.where(TeamPlayerDao.Properties.Team_id.eq(teamID));
		List<TeamPlayer> tpList = tpQB.list();
		for (TeamPlayer tp : tpList) {
			Player player = CustomApplcation.getDaoSession(mContext)
					.getPlayerDao().load(tp.getPlayer_id());

			PlayerInTeam palyerInTeam = new PlayerInTeam();
			palyerInTeam.setPlayer(player);
			palyerInTeam.setCreator(tp.getCreator());
			palyerInTeam.setType(tp.getType());
			TeamPlayerList.add(palyerInTeam);
		}
		return TeamPlayerList;
	}
	
	/**
	 * 判断是否来宾权限   true：是    false：否
	 * @param mContext
	 * @param t
	 * @return
	 * 用于比赛详情中点击“球队头像”跳转到球队信息
	 */
	public static boolean isGuest(Context mContext, Team t){
		boolean isGuest = true;   //是否来宾权限
		List<TeamPlayer> teamPlayerList = CustomApplcation.getDaoSession(mContext).getTeamPlayerDao()
				._queryPlayer_TeamPlayerList(UserManager.getInstance(mContext).getCurrentUser().getPlayer().getUuid());
		for(TeamPlayer teamPlayer : teamPlayerList){
			Team team = CustomApplcation.getDaoSession(mContext).getTeamDao().queryBuilder()
					.where(TeamDao.Properties.Uuid.eq(teamPlayer.getTeam_id())).build().unique();
			if(team.getUuid().equals(t.getUuid())){
				isGuest = false;
				break;
			}
		}
		return isGuest;
	}
	
	/**
	 * 根据teamUuid查数据库里playerUuid创建或加入的球队
	 * @param teamUuid
	 * @param playerUuid
	 * @return
	 */
	public static boolean isTeamExitByUuid(String teamUuid, String playerUuid){
		if(TextUtils.isEmpty(teamUuid) || TextUtils.isEmpty(playerUuid)){
			return false;
		}
		List<TeamPlayer> teamPlayer = CustomApplcation.getDaoSession(null).getTeamPlayerDao()._queryPlayer_TeamPlayerList(playerUuid);
		boolean isTeamExit = false;
		for (TeamPlayer item : teamPlayer) {
			if (item.getTeam_id().equals(teamUuid)) {
				isTeamExit = true;
				break;
			}
		}
		return isTeamExit;
	}
	
	/**
	 * 保存朋友圈SharingWithComments到数据库中
	 */
	public static void saveSharingWithComments2DB(Context mContext, List<SharingWithComments> sharingWithComments){
		PlayerDao playerDao = CustomApplcation.getDaoSession(mContext).getPlayerDao();
		SharingDBDao sharingDBDao = CustomApplcation.getDaoSession(mContext).getSharingDBDao();
		CommentDBDao commentDBDao = CustomApplcation.getDaoSession(mContext).getCommentDBDao();
		for(SharingWithComments item : sharingWithComments){
			if(item.getSharing().getPlayer() != null){
				playerDao.insertOrReplace(item.getSharing().getPlayer());
			}
			SharingDB sharingDB = transSharing2SharingDB(item.getSharing());
			if(sharingDB != null){
				sharingDBDao.insertOrReplace(sharingDB);
			}
			if(item.getComments() != null){
				for(Comment commentItem : item.getComments()){//保存comment
					if(commentItem.getPlayer() != null){
						playerDao.insertOrReplace(commentItem.getPlayer());
					}
					CommentDB commentDB = transComment2CommentDB(commentItem);
					if(commentDB != null){
						commentDBDao.insertOrReplace(commentDB);
					}
				}
			}
		}
	}
	
	/**
	 * 保存朋友圈SharingWithComments到数据库中
	 */
	public static void saveSharingWithComments2DB(Context mContext, SharingWithComments sharingWithComments){
		PlayerDao playerDao = CustomApplcation.getDaoSession(mContext).getPlayerDao();
		SharingDBDao sharingDBDao = CustomApplcation.getDaoSession(mContext).getSharingDBDao();
		CommentDBDao commentDBDao = CustomApplcation.getDaoSession(mContext).getCommentDBDao();
		if(sharingWithComments.getSharing().getPlayer() != null){
			playerDao.insertOrReplace(sharingWithComments.getSharing().getPlayer());
		}
		SharingDB sharingDB = transSharing2SharingDB(sharingWithComments.getSharing());
		if(sharingDB != null){
			sharingDBDao.insertOrReplace(sharingDB);
		}
		if(sharingWithComments.getComments() != null){
			for(Comment commentItem : sharingWithComments.getComments()){//保存comment
				if(commentItem.getPlayer() != null){
					playerDao.insertOrReplace(commentItem.getPlayer());
				}
				CommentDB commentDB = transComment2CommentDB(commentItem);
				if(commentDB != null){
					commentDBDao.insertOrReplace(commentDB);
				}
			}
		}
	}
	
	public static SharingDB transSharing2SharingDB(Sharing sharing){
		if(sharing == null)
			return null;
		SharingDB sharingDB = new SharingDB();
		sharingDB.setUuid(sharing.getUuid());
		sharingDB.setDate(sharing.getDate());
		sharingDB.setType(sharing.getType());
		sharingDB.setComment(sharing.getComment());
		sharingDB.setUrl(sharing.getUrl());
		sharingDB.setImageUrls(JSONUtils.toJson(sharing.getImageUrls(), new TypeToken<List<String>>(){}.getType()));
		sharingDB.setPlayerUuid(sharing.getPlayer() == null ? null : sharing.getPlayer().getUuid());
		return sharingDB;
	}
	
	public static CommentDB transComment2CommentDB(Comment comment){
		if(comment == null)
			return null;
		CommentDB commentDB = new CommentDB();
		commentDB.setUuid(comment.getUuid());
		commentDB.setReplyToPlayerUuid(comment.getReplyToPlayerUuid());
		commentDB.setSharingUuid(comment.getSharingUuid());
		commentDB.setType(comment.getType());
		commentDB.setComment(comment.getComment());
		commentDB.setPlayerUuid(comment.getPlayer() == null ? null : comment.getPlayer().getUuid());
		commentDB.setDate(comment.getDate());
		return commentDB;
	}
	
	/**
	 * 取默认pagesize条数的最新动态，按时间倒序
	 * @param mContext
	 * @param endDate 如果有传截止时间，则取截止时间之前的动态
	 * @return
	 */
	public static List<SharingWithComments> getSharingWithCommentsFromDB(Context mContext, long endDate){
		List<SharingWithComments> sharingsWithComments = new ArrayList<SharingWithComments>();
		PlayerDao playerDao = CustomApplcation.getDaoSession(mContext).getPlayerDao();
		SharingDBDao sharingDBDao = CustomApplcation.getDaoSession(mContext).getSharingDBDao();
		CommentDBDao commentDBDao = CustomApplcation.getDaoSession(mContext).getCommentDBDao();
		List<SharingDB> sharingDBs = null;
		if(endDate > 0l){
			sharingDBs = sharingDBDao.queryBuilder().limit(Config.DEFAULT_NUM_OF_ACTIVE).where(SharingDBDao.Properties.Date.lt(endDate)).orderDesc(SharingDBDao.Properties.Date).list();
		}else{
			sharingDBs = sharingDBDao.queryBuilder().limit(Config.DEFAULT_NUM_OF_ACTIVE).orderDesc(SharingDBDao.Properties.Date).list();
		}
		for(SharingDB item : sharingDBs){
			SharingWithComments sharingWithComments = new SharingWithComments();
			Sharing sharing = transSharingDB2Sharing(item, playerDao);
			if(sharing == null){
				continue;
			}
			sharingWithComments.setSharing(sharing);
			
			List<CommentDB> commentDBs = commentDBDao.queryBuilder().where(CommentDBDao.Properties.SharingUuid.eq(item.getUuid())).orderDesc(CommentDBDao.Properties.Date).list();
			List<Comment> comments = new ArrayList<Comment>();
			for(CommentDB commentItem : commentDBs){
				Comment comment = transCommentDB2Comment(commentItem, playerDao);
				if(comment == null){
					continue;
				}
				comments.add(comment);
			}
			sharingWithComments.setComments(comments);
			sharingsWithComments.add(sharingWithComments);
		}
		return sharingsWithComments;
	}
	
	private static Comment transCommentDB2Comment(CommentDB commentDB, PlayerDao playerDao){
		if(commentDB == null)
			return null;
		Comment comment = new Comment();
		comment.setUuid(commentDB.getUuid());
		comment.setReplyToPlayerUuid(commentDB.getReplyToPlayerUuid());
		comment.setSharingUuid(commentDB.getSharingUuid());
		comment.setType(commentDB.getType());
		comment.setComment(commentDB.getComment());
		if(!TextUtils.isEmpty(commentDB.getPlayerUuid())){
			Player player = playerDao.queryBuilder().where(PlayerDao.Properties.Uuid.eq(commentDB.getPlayerUuid())).unique();
			if(player != null){
				comment.setPlayer(player);
			}
		}
		comment.setDate(commentDB.getDate() == null ? 0l : commentDB.getDate());
		return comment;
	}
	
	public static Sharing transSharingDB2Sharing(SharingDB sharingDB, PlayerDao playerDao){
		if(sharingDB == null){
			return null;
		}
		Sharing sharing = new Sharing();
		sharing.setUuid(sharingDB.getUuid());
		sharing.setDate(sharingDB.getDate() == null ? 0l : sharingDB.getDate());
		sharing.setType(sharingDB.getType());
		sharing.setComment(sharingDB.getComment());
		sharing.setUrl(sharingDB.getUrl());
		List<String> imageUrls = JSONUtils.fromJson(sharingDB.getImageUrls(), new TypeToken<List<String>>(){});
		if(imageUrls != null){
			sharing.setImageUrls(imageUrls);
		}
		if(!TextUtils.isEmpty(sharingDB.getPlayerUuid())){
			Player player = playerDao.queryBuilder().where(PlayerDao.Properties.Uuid.eq(sharingDB.getPlayerUuid())).unique();
			if(player != null){
				sharing.setPlayer(player);
			}
		}
		return sharing;
	}
}
