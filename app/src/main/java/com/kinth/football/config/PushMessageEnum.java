package com.kinth.football.config;

/**
 * 后台推送消息的类型枚举
 * @author Sola
 * 
 */
public enum PushMessageEnum {
	
	NULL("NULL"),
	TEAM_PLAYER_APPLICATION("TEAM_PLAYER_APPLICATION"),//球队--申请加入球队
	TEAM_PLAYER_APPLICATION_CONFIRM("TEAM_PLAYER_APPLICATION_CONFIRM"),//球队--申请加入球队确认
	TEAM_PLAYER_INVITATION("TEAM_PLAYER_INVITATION"),//球队--邀请加入球队
	TEAM_PLAYER_INVITATION_CONFIRM("TEAM_PLAYER_INVITATION_CONFIRM"),//球队--回复加入球队
	TEAM_PLAYER_QUIT("TEAM_PLAYER_QUIT"), //退出球队
	
	MATCH_INVITATION("MATCH_INVITATION"), //比赛--邀请
	MATCH_INVITATION_CONFIRM("MATCH_INVITATION_CONFIRM"),//比赛确认消息--给比赛创建者
	MATCH_CREATED("MATCH_CREATED"), //比赛--创建成功，等待报名
	MATCH_SIGNUP("MATCH_SIGNUP"), //比赛--有某个人报名了
	MATCH_PENDING("MATCH_PENDING"),//待开赛
	MATCH_KICK_OFF("MATCH_KICK_OFF"), //比赛--比赛中
	MATCH_OVER("MATCH_OVER"),//待评价--比赛结束
	MATCH_FINISHED("MATCH_FINISHED"), //比赛--比赛关闭-评价完成后
	MATCH_CANCELED("MATCH_CANCELED"),//比赛--比赛取消
	MATCH_REFEREE_INVITATION("MATCH_REFEREE_INVITATION"),  //比赛------通知球员成为裁判
	MATCH_CHALLENGE("MATCH_CHALLENGE"),//TODO 比赛--应战
	MATCH_CHALLENGE_CONFIRM("MATCH_CHALLENGE_CONFIRM"),//TODO 比赛--拒绝应战
	MATCH_KICK_OFF_NOTIFICATION("MATCH_KICK_OFF_NOTIFICATION"),// 比赛--开赛前通知
	MATCH_OVER_TO_REFEREE("MATCH_OVER_TO_REFEREE"),
	SHARING_COMMENTED("SHARING_COMMENTED");//朋友圈评论
	
	
	private String value;

	PushMessageEnum(String value) {
		this.value = value;
	}
	
	public static PushMessageEnum getEnumFromString(String string) {
		if (string != null) {
			for (PushMessageEnum s : PushMessageEnum.values()){
				if (string.equals(s.value)) {
	                return s;
	            }
			}
		}
		return NULL;
	}

	public String getValue() {
		return value;
	}

}
