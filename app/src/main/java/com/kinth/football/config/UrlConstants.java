package com.kinth.football.config;

/**
 * ip地址、url常量
 */
public class UrlConstants {
	public static final String IP_ADDRESS = "https://api.cloudfootball.com.cn";
	public static final String IP_ADDRESS_FOR_LOGIN = "https://api.cloudfootball.com.cn";//归登陆使用
	public static final String HOST_NAME = "xmpp.cloudfootball.com.cn";
	public static final String UPLOAD_URL = "http://resource.cloudfootball.com.cn/upload";//图片上传地址
	public static final int XMPP_PORT = 5223;
	
	//测试地址
//	public static final String IP_ADDRESS = "http://api.test.cloudfootball.com.cn";
//	public static final String IP_ADDRESS_FOR_LOGIN = "http://api.test.cloudfootball.com.cn";//归登陆使用
//	public static final String HOST_NAME = "xmpp.test.cloudfootball.com.cn";
//	public static final String UPLOAD_URL = "http://resource.test.cloudfootball.com.cn/upload";//图片上传地址
//	public static final int XMPP_PORT = 5225;
	
	public static final String URL_CREATE_TEAM = IP_ADDRESS + "/v1.0/team.json";//创建球队url 
	public static final String URL_TEAM_RELATED = IP_ADDRESS + "/v1.0/team/";//球队相关
	public static final String URL_SEARCH = IP_ADDRESS + "/v1.0/player/search.json";//搜索
	public static final String URL_CREATE_MATCH = IP_ADDRESS + "/v1.0/match.json";//创建比赛url
	public static final String URL_CREATE_INVITE_MATCH = IP_ADDRESS + "/v1.0/match-hall/match.json";//创建约赛url
	public static final String URL_MATCH_RELATED = IP_ADDRESS + "/v1.0/match/";//比赛相关
	public static final String URL_MATCH_HALL_RELATED = IP_ADDRESS + "/v1.0/match-hall/";//TODO 约赛大厅相关
	public static final String URL_MATCH_TEAM_INVITATION = IP_ADDRESS +"/v1.0/match-team-invitation/";//比赛邀请相关
	public static final String URL_PLAYER = IP_ADDRESS + "/v1.0/player.json";
	public static final String URL_PLAYER_RELATED = IP_ADDRESS + "/v1.0/player/";//球员相关url
	public static final String URL_MOMENTS = IP_ADDRESS + "/v1.0/sharing.json";//朋友圈
	public static final String URL_MOMENTS_RELATED = IP_ADDRESS + "/v1.0/sharing/";//朋友圈相关
	public static final String URL_CLOUD5_RANKING = IP_ADDRESS + "/v1.0/player/cloud-five-rank.json";//云五排行
	
	public static final String URL_UPDATE_VERSION = IP_ADDRESS + "/v1.0/app/check-for-updates.json";
	
	public static final String URL_TEAM_PLAYER_INVITATION = IP_ADDRESS + "/v1.0/team-player-invitation.json";//球员邀请
	public static final String URL_TEAM_PLAYER_INVITATION_RELATED = IP_ADDRESS + "/v1.0/team-player-invitation/";//回复球队邀请
	
	// 获取我的比赛邀请
	public static final String URL_GETMYMATCH_INVITATION = IP_ADDRESS + "/v1.0/match-team-invitation/list.json";
	
	//	首页跳转链接 
	public static final String GOLEN_HOME_WEB_URL = "http://www.jrjsl.com/statistic";
	// 比赛跳转链接
	public static final String GOLEN_MATCH_WEB_URL = "http://www.jrjsl.com/statistic#schedule";
	//球员个人跳转链接http://www.jrjsl.com/chart/{player-uuid}
	public static final String GOLEN_PERSON_WEB_URL = "http://www.jrjsl.com/chart/";
	
	//【关于】跳转链接
	public static final String ABOUT_WEB_URL = "http://www.cloudfootball.com.cn/app-about";
	
	//原来的图片上传地址
//	public static final String UPLOAD_URL = "http://resource.cloudfootball.com.cn:8089/FDFSWeb/fdfs/upload.jsp";

}
