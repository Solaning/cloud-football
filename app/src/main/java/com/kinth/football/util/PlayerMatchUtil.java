package com.kinth.football.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.kinth.football.CustomApplcation;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.dao.Match;
import com.kinth.football.dao.MatchDao;
import com.kinth.football.dao.MatchTeam;
import com.kinth.football.dao.MatchTeamDao;
import com.kinth.football.dao.MatchTeamPlayer;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * 根据用户id查询数据库比赛信息
 * 
 * @author Botision.Huang Date: 2015-3-27 Descp:根据当前用户ID从数据库中获取该用户相关status的比赛
 */
public class PlayerMatchUtil {

	/**
	 * 根据用户id和比赛状态(比赛类型)查询比赛，默认按时间逆序
	 * 
	 * @param mContext
	 * @param currentUserID
	 * @param status
	 *            比赛的状态，不传默认搜索所有比赛状态
	 * @param match_type
	 *            比赛类型，不传默认搜索所有比赛类型
	 * @return
	 */
	public static List<MatchInfo> getMatchByCurrentUserId(Context mContext,
			String currentUserID, String status, String match_type) {
		List<MatchInfo> _queryMatchInfoList = new ArrayList<MatchInfo>(); // 最终需要的结果
		/*
		 * 分为4中情况，（0x11）代表status,match_type都不为空 （0x10）代表match_type不为空，status为空
		 * （0x01）代表match_type为空，status不为空 （0x00）代表match_type为空，status为空，查询所有
		 */
		byte state = 0x00;
		if (TextUtils.isEmpty(status)) {
			state = (byte) (state | 0x00);
		} else {
			state = (byte) (state | 0x01);
		}
		if (TextUtils.isEmpty(match_type)) {
			state = (byte) (state | 0x00);
		} else {
			state = (byte) (state | 0x10);
		}

		List<MatchTeam> _querymatchTeamList = new ArrayList<MatchTeam>();
		MatchDao matchDao = CustomApplcation.getDaoSession(mContext)
				.getMatchDao();
		MatchTeamDao matchTeamDao = CustomApplcation.getDaoSession(mContext)
				.getMatchTeamDao();

		// 1、根据当前用户Uid从而获取得到MatchTeamPlayer映射表List

		List<MatchTeamPlayer> _queryMTP = CustomApplcation
				.getDaoSession(mContext).getMatchTeamPlayerDao()
				._queryPlayer_MatchTeamPlayerList(currentUserID);
		// 2、通过MatchTeamPlayer映射表List得到MatchTeam映射表List
		for (MatchTeamPlayer mtpItem : _queryMTP) {
			long matchTeam_id = mtpItem.getMatch_team_id();

			QueryBuilder<MatchTeam> matchteamQB = matchTeamDao.queryBuilder();
			matchteamQB.where(MatchTeamDao.Properties.Id.eq(matchTeam_id)); // 这里应该要注意matchTeam_id的数据类型是否为long
			MatchTeam matchTeam = matchteamQB.unique();

			_querymatchTeamList.add(matchTeam);
		}

		// 3、遍历MatchTeam映射表_querymatchTeamList，通过比赛status得到“待比赛”列表_queryMatchInfoList
		for (MatchTeam mtItem : _querymatchTeamList) {
			String matchByPlayerID = mtItem.getMatch_id();

			QueryBuilder<Match> matchQB = matchDao.queryBuilder();
			matchQB.where(MatchDao.Properties.Uuid.eq(matchByPlayerID));
			Match match = matchQB.unique();

			switch (state) {
			case 0x00:// 查所有状态和类型
				MatchInfo matchInfo = DBUtil.queryMatchInfoByMatch(mContext, match);
				_queryMatchInfoList.add(matchInfo);
				break;
			case 0x01:// 查特定状态
				if (status.equals(match.getState())) {
					MatchInfo matchInfo2 = DBUtil.queryMatchInfoByMatch(mContext,
							match);
					_queryMatchInfoList.add(matchInfo2);
				}
				break;
			case 0x10:// 查特定类型
				if (match_type.equals(match.getType())) {
					MatchInfo matchInfo3 = DBUtil.queryMatchInfoByMatch(mContext,
							match);
					_queryMatchInfoList.add(matchInfo3);
				}
				break;
			case 0x11:// 查特定状态和类型的比赛
				if (status.equals(match.getState())
						&& match_type.equals(match.getType())) {
					MatchInfo matchInfo4 = DBUtil.queryMatchInfoByMatch(mContext,
							match);
					_queryMatchInfoList.add(matchInfo4);
				}
				break;
			}
		}
		Collections.sort(_queryMatchInfoList, new Comparator<MatchInfo>() {// 排序
					@Override
					public int compare(MatchInfo o1, MatchInfo o2) {
						return Long.valueOf(o2.getDate()).compareTo(
								Long.valueOf(o1.getDate()));
					}
				});
		// 5、已经获取得到比赛列表，接下来相应操作
		return _queryMatchInfoList;
	}
}
