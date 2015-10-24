package com.kinth.football.network;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kinth.football.CustomApplcation;
import com.kinth.football.bean.FeedbackBean;
import com.kinth.football.bean.TeamRequest;
import com.kinth.football.bean.match.FriendMatch;
import com.kinth.football.bean.moments.CommentRequest;
import com.kinth.football.bean.moments.SharingRequest;
import com.kinth.football.config.UrlConstants;
import com.kinth.football.ui.team.formation.Formation;
import com.kinth.football.util.JSONUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class NetWorkManager {
	
	private static volatile NetWorkManager mNetWorkManager;
	private static Object INSTANCE_LOCK = new Object();
	private Context mContext;

	//检查新版本的表示 ANDROID表示是android版本的检查
	public static final String UPDATE_ANDROID_VERSION = "ANDROID";
	
	public static NetWorkManager getInstance(Context context) {
		if (mNetWorkManager == null)
			synchronized (INSTANCE_LOCK) {
				if (mNetWorkManager == null)
					mNetWorkManager = new NetWorkManager();
				mNetWorkManager.init(context);
			}
		return mNetWorkManager;
	}
	
	public void init(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * 1.1 注册
	 * @param phone
	 * @param password
	 * @param listener
	 * @param errorListener
	 */
	public void registerAction(String phone, String password,
			Response.Listener<JSONObject> listener, ErrorListener errorListener) {
		/*
		 * curl -X POST -H "Content-Type: application/json" -d '{"phone":"15913142273","password":"111111"}' 
		 * "http://localhost:8080/api/v1.0/user.json"
		 */
		String url = UrlConstants.IP_ADDRESS +"/v1.0/user.json";
		RequestQueue requestQueue = Volley.newRequestQueue(mContext);
		Map<String, String> map = new HashMap<String, String>();
		map.put("phone", phone);
		map.put("password", password);
		JSONObject jsonObject = new JSONObject(map);
		
		JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(
				Method.POST, url, jsonObject, listener, errorListener) {
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = genHeaderParameters();
				headers.put("Content-Type", "application/json");
				
				return headers;
			}
		};
		requestQueue.add(jsonRequest);
	}

	/**
	 * 1.2 登录
	 * @param phone
	 * @param pwd
	 * @param listener
	 * @param errorListener
	 */
	public void loginAction(String phone, String pwd,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		String loginUrl = UrlConstants.IP_ADDRESS_FOR_LOGIN
				+ "/oauth/token?client_id=app-client&grant_type=password&scope=read&username="
				+ phone + "&password=" + pwd;
		
		requestString(Method.GET, loginUrl, null, listener, errorListener);
	}

	/**
	 * 1.3 注销
	 * @param accessToken
	 * @param listener
	 * @param errorListener
	 */
	public void logoutAction(final String accessToken,
			Response.Listener<String> listener, ErrorListener errorListener) {
		RequestQueue requestQueue = Volley.newRequestQueue(mContext);
		String url = UrlConstants.IP_ADDRESS + "/v1.0/user/logout.json";
		StringRequest mStringRequest = new StringRequest(Request.Method.POST,
				url, listener, errorListener) {
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = genHeaderParameters();
				headers.put("Authorization", "Bearer " + accessToken);
				return headers;
			}
		};
		requestQueue.add(mStringRequest);
	}
	
	/**
	 * 1.4 设置密码
	 * @param accessToken
	 * @param newPwd
	 * @param listener
	 * @param errorListener
	 */
	public void modifyPwdAction(final String accessToken,String oldPwd,
			String newPwd, Response.Listener<String> listener,
			ErrorListener errorListener) {
		/*
		 * curl -X POST -H
		 * "Authorization: Bearer fda45470-078d-4cc7-831c-979945d12bc8"
		 * "http://localhost:8080/api/v1.0/user/3eeb1063-2f6b-49fb-8a22-223e02fa6b5d/password.json?password=222222"
		 */
		RequestQueue requestQueue = Volley.newRequestQueue(mContext);
		String url = UrlConstants.IP_ADDRESS + "/v1.0/user/password.json?old-password=" + oldPwd+"&new-password="+newPwd;
		StringRequest mStringRequest = new StringRequest(Request.Method.PUT,
				url, listener, errorListener) {
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = genHeaderParameters();
				headers.put("Authorization", "Bearer " +accessToken);
				return headers;
			}
		};

		requestQueue.add(mStringRequest);
	}
	
	/**
	 * 1.5 获取找回密码验证码短信
	 */
	public void getVerifycode2ResetPassword(String phone, Listener<Void> listener, ErrorListener errorListener){
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.IP_ADDRESS).append("/v1.0/user/reset-password-verifycode.json?phone=").append(phone);
		JSONObject jsonObject = null;
		JsonRequest<Void> jsonRequest = new JsonNoResponseRequest(Method.GET, sb.toString(), jsonObject, listener, errorListener);
		CustomApplcation.getInstance().addToRequestQueue(jsonRequest);
	}
	
	/**
	 * 1.6 通过验证码设置密码
	 */
	public void setNewPassword(String phone, String verifycode, String password, Listener<Void> listener, ErrorListener errorListener){
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.IP_ADDRESS).append("/v1.0/user/password-by-verifycode.json?phone=").append(phone);
		sb.append("&verifycode=").append(verifycode).append("&password=").append(password);
		JSONObject jsonObject = null;
		JsonRequest<Void> jsonRequest = new JsonNoResponseRequest(Method.PUT, sb.toString(), jsonObject, listener, errorListener);
		CustomApplcation.getInstance().addToRequestQueue(jsonRequest);
	}
	
	/**
	 * 2.1 获取球员信息 -- 用户自己的
	 */
	public void getPlayerInfo(String accessToken, Listener<JSONObject> listener, ErrorListener errorListener){
		request(Method.GET, UrlConstants.URL_PLAYER, accessToken, null, listener, errorListener);
	}
	
	/**
	 * 2.2 通过UUID获取球员信息
	 */
	public void getPlayerInfoByUuid(String playerUuid, String accessToken, Listener<JSONObject> listener,  ErrorListener errorListener){
		String url = UrlConstants.URL_PLAYER_RELATED + playerUuid + "/detail.json";
		request(Method.GET, url, accessToken, null, listener, errorListener);
	}
	/**
	 * 2.2 通过UUID获取球员信息(详细的  +detail)
	 */
	public void getPlayerInfoDetailByUuid(String playerUuid, String accessToken, Listener<JSONObject> listener,  ErrorListener errorListener){
		String url = UrlConstants.URL_PLAYER_RELATED + playerUuid + "/detail.json";
		request(Method.GET, url, accessToken, null, listener, errorListener);
	}
	
	/**
	 * 2.3 更新球员信息
	 * 修改用户信息
	 * @param token
	 * @param listener
	 * @param errorListener
	 */
	public void monifyUserInfo(String token,JSONObject jsonObject, Listener<Void>listener,ErrorListener errorListener) {
		String url = UrlConstants.IP_ADDRESS +"/v1.0/player.json";
		requestVoid(Method.PUT, url, token, jsonObject, listener, errorListener);
	}
	
//	/**
//	 * 获取用户的个人信息---TODO 合并到接口2.1
//	 * @param accessToken
//	 * @param listener
//	 * @param errorListener
//	 */
//	@Deprecated
//	public void getUserInfoAction(final String accessToken, 
//			Response.Listener<String> listener, ErrorListener errorListener) {
//		String url = UrlConstants.IP_ADDRESS + "/v1.0/player.json";
//		StringRequest mStringRequest = new StringRequest(url, listener,
//				errorListener) {
//			@Override
//			public Map<String, String> getHeaders() {
//				HashMap<String, String> headers = new HashMap<String, String>();
//				headers.put("Authorization", "Bearer " + accessToken);
//				return headers;
//			}
//		};
//		requestQueue.add(mStringRequest);
//	}
	
	/**
	 * 2.5 搜索用户、球员
	 * @param page
	 * @param pageSize
	 * @param cityId
	 * @param name
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void searchPlayer(int page, int pageSize,int provinceId, int cityId, String name,String position, String access_token, Listener<JSONObject> listener, ErrorListener errorListener){
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.URL_SEARCH);
		sb.append("?page=").append(page);
		sb.append("&pageSize=").append(pageSize);
		if(provinceId > 0 &&cityId<0){  //选择了省，市-不限  只传provinceId
			sb.append("&provinceId=").append(provinceId);
		}
		if(provinceId > 0 &&cityId > 0){//选择了省，也选择了市 --只传cityId
			sb.append("&cityId=").append(cityId);
		}
		if(!TextUtils.isEmpty(name)){
			sb.append("&name=").append(Uri.encode(name));
		}
		if(!TextUtils.isEmpty(position)){
			sb.append("&position=").append(position);
		}
//		Log.e("NetWorkManager", "搜索用户，球员" + sb.toString());
		request(Method.GET, sb.toString(), access_token, null, listener, errorListener);
	}
	
	/**
	 * 2.6  通过AccountName获取球员信息
	 * @param access_token
	 * @param accountName
	 * @param listener
	 * @param errorListener
	 * /api/v1.0/player/get-by-account/{account-name}.json
	 */
	public void getPlayerByAccountName(String access_token, String accountName, Listener<JSONObject> listener, ErrorListener errorListener){
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.IP_ADDRESS + "/v1.0/player/get-by-account/");
		if(accountName != null){
			sb.append(accountName + ".json");
		}
//		Log.e("2.6通过获取球员AccountName信息", sb.toString());
		request(Method.GET, sb.toString(), access_token, null, listener, errorListener);
	}
	
	/**
	 * 创建球队
	 * @param access_token
	 * @param teamRequest--入参
	 * @param listener
	 * @param errorListener
	 */
	public void createTeam(String access_token, TeamRequest teamRequest, Listener<JSONObject> listener, ErrorListener errorListener) {
		/*
		* curl -X PUT -H "Content-Type: application/json" 
		* -H "Authorization: Bearer a7d6a27b-5ff9-471e-bcb2-50f7140a6554" 
		* -d '{"name":"championTeam","cityId": 231,"regionId":1}' 
		* "http://localhost:8080/api/v1.0/user/my.json"
		 */
		request(Method.POST, UrlConstants.URL_CREATE_TEAM, access_token, JSONUtils.toJsonObject(teamRequest), listener, errorListener);
	}
	
	/**
	 * 获取我所有球队的信息
	 */
	public void getMyTeamList(String access_token, Listener<JSONArray> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_TEAM_RELATED + "myTeam.json";
		requestArray(Method.GET, url, access_token, null, listener, errorListener);
	}
	
	/**
	 * 2.4 获取球员的球队
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void getUserTeamList(String palyerID, String access_token, Listener<JSONArray> listener, ErrorListener errorListener){
		//  /api/v1.0/player/{player-uuid}/teams.json
		String url = UrlConstants.URL_PLAYER_RELATED + palyerID + "/teams.json";
		requestArray(Method.GET, url, access_token, null, listener, errorListener);
	}

	/**
	 * 3.3 获取球队信息
	 * @param teamUUID
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void getTeam(String teamUUID, String access_token, Listener<JSONObject> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_TEAM_RELATED + teamUUID + ".json";
		request(Method.GET, url, access_token, null, listener, errorListener);
	}
	
	/**
	 * 更新球队信息
	 * @param teamUUID
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void updateTeamInfo(String teamUUID, String access_token,  TeamRequest teamRequest, Listener<Void> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_TEAM_RELATED + teamUUID + ".json";
		requestVoid(Method.PUT, url, access_token, JSONUtils.toJsonObject(teamRequest), listener, errorListener);
	}
	
	/**
	 * 创建球队阵容
	 * @param teamUUID
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void createFormation(String teamUUID, String access_token, Formation formation, Listener<JSONObject> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_TEAM_RELATED + teamUUID + "/formation.json";
		Log.e("tag", JSONUtils.toJsonObject(formation).toString());
		request(Method.POST, url, access_token, JSONUtils.toJsonObject(formation), listener, errorListener);
	}
	
	/**
	 * 获取球队所有阵容
	 * @param teamUUID
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void getAllFormation(String teamUUID, String access_token, Listener<JSONArray> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_TEAM_RELATED + teamUUID + "/formation.json";
		requestArray(Method.GET, url, access_token, null, listener, errorListener);
	}
	
	/**
	 * 获取球队某一套阵容
	 */
	public void getSpecialFormation(String teamUUID, int formationUUID, String access_token, Listener<JSONObject> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_TEAM_RELATED + teamUUID + "/formation/" + formationUUID + ".json";
		request(Method.GET, url, access_token, null, listener, errorListener);
	}
	
	/**
	 * 3.6
	 * 获取球队成员列表
	 */
	public void getTeamMember(String teamUUID, String access_token, Listener<JSONArray> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_TEAM_RELATED + teamUUID + "/players.json";
		requestArray(Method.GET, url, access_token, null, listener, errorListener);
	}
	
	/**
	 * 邀请成员加入球队(修改队长，队副，第三队长)通过type字段分类
	 * @param playerUuid
	 * @param type
	 * @param teamUUID
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void inviteMember(String playerUuid, String type, String teamUUID, String access_token, Listener<Void> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_TEAM_PLAYER_INVITATION;
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("teamUuid", teamUUID);
			jsonObject.put("playerUuid", playerUuid);
			jsonObject.put("type", type);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		requestVoid(Method.POST, url, access_token, jsonObject, listener, errorListener);
	}
	
	/**
	 * 回复球队邀请
	 */
	public void confirmInvitePushMessage(String uuid, String reply, String access_token, Listener<Void> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_TEAM_PLAYER_INVITATION_RELATED + uuid + "/confirm.json?accept=" + reply;
		requestVoid(Method.POST, url, access_token, null, listener, errorListener);
	}
	
	/**
	 * 比赛确认
	 */
	public void confirmMatchInvitation(String invitationUuid, String type, String access_token, Listener<Void> listener, ErrorListener errorListener){
		StringBuilder url = new StringBuilder();
		url.append(UrlConstants.URL_MATCH_TEAM_INVITATION).append(invitationUuid);
		url.append("/confirm.json?accept=").append(type);
		requestVoid(Method.POST, url.toString(), access_token, null, listener, errorListener);
	}
	//3.9   POST /api/v1.0/team/team-like.json?uuid={team-uuid}

	public void liketeam(String teamUuid,String access_token,Listener<Void> listener, ErrorListener errorListener){
		String url = UrlConstants.IP_ADDRESS+"/v1.0/team/team-like.json?uuid="+teamUuid;
		requestVoid(Method.POST, url, access_token, null, listener, errorListener);
		
	}
	
	/**
	 * 3.10取消like球队
	 * @param teamUuid
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void cancelLiketeam(String teamUuid, String access_token, Listener<Void> listener, ErrorListener errorListener){
		String url = UrlConstants.IP_ADDRESS+"/v1.0/team/team-like/cancel.json?uuid="+teamUuid;
		requestVoid(Method.POST, url, access_token, null, listener, errorListener);
	}
	
	/**
	 * 3.11申请加入球队
	 * @param teamUuid
	 * @param accessToken
	 * @param listener
	 * @param errorListener
	 */
	public void requestJoinTeam(String teamUuid, String accessToken, Listener<Void> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_TEAM_RELATED + teamUuid + "/apply.json";
		requestVoid(Method.POST, url, accessToken, null, listener, errorListener);
	}
	
	/**
	 * 3.12申请加入球队回复
	 */
	public void confirmJoinTeamRequest(String messageId, String reply, String accessToken, Listener<Void> listener, ErrorListener errorListener){
		String url = UrlConstants.IP_ADDRESS + "/v1.0/team/apply/" + messageId + "/reply.json?accept=" + reply;
		requestVoid(Method.POST, url, accessToken, null, listener, errorListener);
	}
	
	/**
	 * 3.13 退出球队
	 * @param accessToken
	 * @param teamUuid
	 * @param listener
	 * @param errorListener
	 */
	public void exitTeam(String teamUuid, String accessToken, Listener<Void> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_TEAM_RELATED + teamUuid + "/quit.json";
		requestVoid(Method.POST, url, accessToken, null, listener, errorListener);
	}
	
	/**
	 * 3.14 设置球员球衣号码
	 * 
	 * @param access_token
	 * @param teamUuid
	 * @param playerUuid
	 * @param jerseyNo
	 * @param listener
	 * @param errorListener
	 */
	public void changeJerseyNo(String access_token,String teamUuid,String playerUuid,int jerseyNo,Listener<Void> listener, ErrorListener errorListener){
		String url =UrlConstants.IP_ADDRESS+"/v1.0/team-player/jerseyNo.json?teamUuid="+teamUuid+"&playerUuid="+playerUuid+"&jerseyNo="+jerseyNo;
		requestVoid(Method.PUT, url, access_token, null, listener, errorListener);
	}
	
/**
 * 3.15 获取球队最新战报
 * @param access_token
 * @param teamUuid
 * @param listener
 * @param errorListener
 */
	public void getLatestMatchInfo(String access_token, String teamUuid, Listener<JSONObject> listener, ErrorListener errorListener){
		String url = UrlConstants.IP_ADDRESS+"/v1.0/team/" + teamUuid+"/latest-match.json";
		request(Method.GET, url, access_token, null, listener, errorListener);
	}
	
	/**
	 * 7.5 比赛报名
	 * @param uuid
	 * @param teamUuid
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void matchSignUp(String matchUuid, String teamUuid, String access_token, Listener<Void> listener, ErrorListener errorListener){
		StringBuilder url = new StringBuilder();
		url.append(UrlConstants.URL_MATCH_RELATED).append(matchUuid);
		url.append("/signup.json?teamUuid=").append(teamUuid);
		
		requestVoid(Method.POST, url.toString(), access_token, null, listener, errorListener);
	}
	
	/**
	 * 搜索球队
	 * @param page
	 * @param pageSize
	 * @param cityId
	 * @param teamName
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void searchTeam(int page, int pageSize, int provinceId,int cityId, String teamName, String access_token, Listener<JSONObject> listener, ErrorListener errorListener){
		//http://localhost:8080/api/v1.0/team/search.json?name=celtics&cityId=289&page=0&pageSize=10
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.URL_TEAM_RELATED);
		sb.append("search.json?");
		int count = 0;
		if(!TextUtils.isEmpty(teamName)){
			count++;
			sb.append("name=").append(Uri.encode(teamName)).append("&");
		}
		if(provinceId > 0 &&cityId<0){  //选了省  市--不限  --只传provinceId
			count++;
			sb.append("provinceId=").append(provinceId).append("&");
		}
		if(provinceId > 0 &&cityId > 0){//选了省 也选了市--只传cityID
			count++;
			sb.append("cityId=").append(cityId).append("&");
		}
		if(count == 0){
			sb.append("&");
		}
		sb.append("page=").append(page);
		sb.append("&pageSize=").append(pageSize);
		request(Method.GET, sb.toString(), access_token, null, listener, errorListener);
	}
	
	/**
	 * 创建比赛
	 * @param access_token
	 * @param friendMatch
	 * @param listener
	 * @param errorListener
	 */
	public void createMatch(String access_token, FriendMatch friendMatch, Listener<JSONObject> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_CREATE_MATCH;
		request(Method.POST, url, access_token, JSONUtils.toJsonObject(friendMatch), listener, errorListener);
	}
	
	/**
	 * 创建约赛
	 * @param access_token
	 * @param friendMatch
	 * @param listener
	 * @param errorListener
	 */
	public void createInviteMatch(String access_token, FriendMatch friendMatch, Listener<JSONObject> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_CREATE_INVITE_MATCH;
//		Log.e("呵呵", JSONUtils.toJsonObject(friendMatch).toString());
		request(Method.POST, url, access_token, JSONUtils.toJsonObject(friendMatch), listener, errorListener);
	}
	
	/**
	 * 获取比赛信息
	 * @param access_token
	 * @param match_uuid
	 * @param listener
	 * @param errorListener
	 */
	public void getMatchInfo(String access_token, String match_uuid, Listener<JSONObject> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_MATCH_RELATED + match_uuid + ".json";
		request(Method.GET, url, access_token, null, listener, errorListener);
	}
	
	/**
	 * 7.7 录入比赛比分
	 */
	public void  typeInMatchScore(int homeTeamScore,int awayTeamScore, String accessToken, String matchUuid,Listener<Void> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_MATCH_RELATED  + matchUuid + "/team-score.json";
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("homeTeamScore", homeTeamScore);
			jsonObject.put("awayTeamScore", awayTeamScore);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		requestVoid(Method.POST, url, accessToken, jsonObject, listener, errorListener);
	}
	
	/**
	 * 7.8录入球员评分 
	 */
	@Deprecated
	public void entering_player_score(String  matchUuid,String access_token,String teamUuid,String playerUuid, String skill,String morality,Listener<Void> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_MATCH_RELATED+matchUuid+"/player-score.json";
		JSONObject jsonObject = new JSONObject();
		try {		
			jsonObject.put("teamUuid", teamUuid);
			jsonObject.put("playerUuid", playerUuid);
			jsonObject.put("skill", skill);
			jsonObject.put("morality", morality);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestVoid(Method.POST, url, access_token, jsonObject, listener, errorListener);
	}
	
	/**
	 * 7.15 批量提交互评结果
	 * @param matchUuid
	 * @param teamUuid
	 * @param jsonArray
	 * @param accessToken
	 * @param listener
	 * @param errorListener
	 */
	public void batchSubmitComment(String matchUuid, String teamUuid, JSONArray jsonArray, String accessToken, Listener<Void> listener, ErrorListener errorListener){
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.URL_MATCH_RELATED);
		sb.append(matchUuid);
		sb.append("/player-score/batch.json?teamUuid=");
		sb.append(teamUuid);
		requestVoid4Array(Method.POST, sb.toString(), accessToken, jsonArray, listener, errorListener);
	}
	
	/**
	 * 8.3.1  获取某人报名的比赛列表--根据状态来获取  referee=true默认包含裁判的比赛
	 * @param person_uuid
	 * @param access_token
	 * @param page
	 * @param pageSize
	 * @param matchState
	 * @param listener
	 * @param errorListener
	 */
	public void getStateMatchList(String person_uuid, String access_token, int page, int pageSize, String matchState, Listener<JSONObject> listener, ErrorListener errorListener){
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.URL_PLAYER_RELATED + person_uuid + "/matchs.json?page=" + page +"&pageSize=" + pageSize);
		if(!TextUtils.isEmpty(matchState)){
			sb.append("&state=" + matchState);
		}
		sb.append("&referee=true");
		request(Method.GET, sb.toString(), access_token, null, listener, errorListener);
	}
	
	/**
	 * 8.3.1  获取所有公共的比赛列表--根据状态来获取  referee=true默认包含裁判的比赛
	 * 加上地区筛选
	 * @param person_uuid
	 * @param access_token
	 * @param page
	 * @param pageSize
	 * @param matchState
	 * @param regionId 区县id
	 * @param listener
	 * @param errorListener
	 */
	public void getPublicStateMatchList(String person_uuid, String access_token, int page, int pageSize, String matchState, int areaTag, int areaId, Listener<JSONObject> listener, ErrorListener errorListener){
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.URL_CREATE_MATCH + "?page=" + page +"&pageSize=" + pageSize);
		if(!TextUtils.isEmpty(matchState)){
			sb.append("&state=" + matchState);
		}
		if(areaTag==1){
			sb.append("&provinceId="+areaId);
		}else if(areaTag==2){
			sb.append("&cityId="+areaId);
		}else if(areaTag==3){
			sb.append("&regionId="+areaId);
		}
		
		Log.e("request", sb.toString());
		request(Method.GET, sb.toString(), access_token, null, listener, errorListener);
	}
	
	/**
	 * 3.10喜欢球队
	 * @param matchUuid
	 * @param teamUuid
	 * @param islike
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void like_team(String matchUuid,String teamUuid,String islike,String access_token,Listener<Void> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_MATCH_RELATED + matchUuid + "/like.json?teamUuid="+teamUuid+"&like="+islike;
		requestVoid(Method.POST, url, access_token, null, listener, errorListener);
	}
	
	/**
	 * 6.1 检查更新新版本
	 * @param type
	 * @param currentVersion
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void updateVersion(String type,String currentVersion, String access_token, Listener<JSONObject> listener, ErrorListener errorListener){
		//http://football-server/api/v1.0/app/check-for-updates.json?platform=ANDROID&currentVersion=0.1
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.URL_UPDATE_VERSION).append("?");
		if(!TextUtils.isEmpty(type)){
			sb.append("platform=" + "ANDROID").append("&");
		}
		if(!TextUtils.isEmpty(currentVersion)){
			sb.append("currentVersion=" + currentVersion);
		}
		request(Method.GET, sb.toString(), access_token, null, listener, errorListener);
	}
	
	
	/**
	 * 7.11
	 * 获取我的比赛邀请
	 * @param page:页号
	 * @param pageSize:页大小
	 * @param matchUid:获取该比赛的邀请, 不传则获取所有的比赛邀请
	 * @param teamUid:获取该球队指定比赛的比赛邀请(必须同时传matchUuid),不传则获取所有不指定team的比赛邀请;
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void getMyMatchInvitation(int page, int pageSize, String matchUid, String teamUid, 
			String access_token, Listener<JSONObject> listener, ErrorListener errorListener){
		///v1.0/match-team-invitation/list.json?matchUuid={matchUuid}&teamUuid={matchUuid}&page={page}&pageSize={page-size}
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.URL_GETMYMATCH_INVITATION).append("?");
		int count = 0;
		if(!TextUtils.isEmpty(matchUid)){
			count++;
			sb.append("matchUuid=" + matchUid).append("&");
		}
		if(!TextUtils.isEmpty(teamUid)){
			count++;
			sb.append("teamUuid=" + teamUid).append("&");
		}
		if(count == 0){
			sb.append("&");
		}
		sb.append("page=" + page);
		sb.append("&pageSize=" + pageSize);
		request(Method.GET, sb.toString(), access_token, null, listener, errorListener);
	}
	
	/**
	 * 7.3.1 获取球员相关的比赛列表
	 * @param playerUuid
	 * @param matchState state参数不传时，则返回所有比赛  state={INVITING|CREATED|PENDING|PLAYING|FINISHED|CANCELED}
	 * @param matchType FRIENDLYGAME | LEAGUE | FIFASEASON 3种比赛类型
	 * @param referee referee=true时返回该球员作为裁判的比赛, referee=false或不传时返回该球员作为普通参赛者的比赛(除裁判).
	 * @param access_token
	 * @param page
	 * @param pageSize
	 * @param listener
	 * @param errorListener
	 */
	public void getPlayerMatch(String playerUuid, String matchState, String matchType, boolean referee, String access_token, int page, int pageSize, 
			Listener<JSONObject> listener, ErrorListener errorListener){
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.URL_PLAYER_RELATED).append(playerUuid + "/matchs.json?");
		sb.append("page=" + page);
		sb.append("&pageSize=" + pageSize);
		
		if(!TextUtils.isEmpty(matchState)){
			sb.append("&state=").append(matchState);
		}
		if(!TextUtils.isEmpty(matchType)){
			sb.append("&type=").append(matchType);
		}
		if(referee){
			sb.append("&referee=" + referee);
		}
//		Log.e("sb",""+sb.toString());
		request(Method.GET, sb.toString(), access_token, null, listener, errorListener);
	}
	
	/**
	 * 7.3.2 球队的比赛列表（包括取消的等等）
	 * @param team_UUid
	 * @param page
	 * @param pageSize
	 * @param match_State
	 *  @param type
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void getTeamRecordGames(String teamUuid, int page, int pageSize, String match_State,String type,
			String access_token, RequestCallBack<String> listener){//Listener<JSONObject> listener, ErrorListener errorListener){
		// /api/v1.0/team/{team-uuid}/matchs.json?page=0&pageSize=10&state={match-state}
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.URL_TEAM_RELATED);
		if(teamUuid != null){
			sb.append(teamUuid + "/matchs.json?");
		}
		sb.append("page=" + page);
		sb.append("&pageSize=" + pageSize);
		if (match_State!=null) {
			sb.append("&state=" + match_State);
		}
		
		if (type!=null) {
			sb.append("&type="+type);
		}
		//因为Volley不适合加载大数据量，所以只能改为xUtils的方式来加载
//		request(Method.GET, sb.toString(), access_token, null, listener, errorListener);
		RequestParams params = new RequestParams();
		params.addHeader("Authorization", "Bearer " + access_token);
		params.addHeader("Content-Type", "application/json");
		HttpUtils http = new HttpUtils();
		http.configDefaultHttpCacheExpiry(0);//不缓存该请求结果
		http.send(HttpMethod.GET,
				sb.toString(), params,listener);
	}
	
	/**
	 *球队最近五场比赛数据
	 * @param access_token
	 * @param teamUuid
	 * @param listener
	 * @param errorListener
	 */
	public void getLast5Mtach(String access_token,String teamUuid,Listener<JSONObject> listener, ErrorListener errorListener){
		String url = UrlConstants.IP_ADDRESS +"/v1.0/player-data/"+teamUuid+"/recent-five-match.json";
		request(Method.GET, url, access_token, null, listener, errorListener);
	}
	
	/**
	 * 9.1 球员总比赛战绩接口
	 * @param player_Uuid
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void playerAllMatchData(String player_Uuid, String access_token,Listener<JSONObject> listener, ErrorListener errorListener){
		//api/v1.0/player/{uuid}/match-records.json
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.URL_PLAYER_RELATED);
		if(player_Uuid != null){
			sb.append(player_Uuid + "/match-records.json");
		}
		request(Method.GET, sb.toString(), access_token, null, listener, errorListener);
	}
	
	/**
	 * 9.2 球员友谊赛战绩接口
	 * @param player_Uuid
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void playerFriendMatchData(String player_Uuid, String access_token,Listener<JSONObject> listener, ErrorListener errorListener){
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.URL_PLAYER_RELATED);
		if(player_Uuid != null){
			sb.append(player_Uuid + "/friendly-game-records.json");
		}
		request(Method.GET, sb.toString(), access_token, null, listener, errorListener);
	}
	
	/**
	 * 9.3 球员近5场比赛统计数据接口
	 * @param playerUUid
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void getPlayerFMSMData(String playerUUid, 
			String access_token, Listener<JSONObject> listener, ErrorListener errorListener){
		//  /api/1.0/player/{uuid}/match-statistics.json
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.IP_ADDRESS + "/v1.0/player/");
		if(playerUUid != null){
			sb.append(playerUUid + "/match-statistics.json");
		}
		request(Method.GET, sb.toString(), access_token, null, listener, errorListener);
	}
	
	/**
	 * 9.4 球队所有比赛战绩
	 * @param access_token
	 * @param teamUuid
	 * @param listener
	 * @param errorListener
	 */
	public void getAllMatchOfTeam(String access_token,String teamUuid,Listener<JSONObject> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_TEAM_RELATED +teamUuid+"/match-records.json";
		request(Method.GET, url, access_token, null, listener, errorListener);
	}
	
	/**
	 * 9.5 球队所有友谊赛战绩
	 * @param access_token
	 * @param teamUuid
	 * @param listener
	 * @param errorListener
	 */
	public void getFriendlyMatchOfTeam(String access_token,String teamUuid,Listener<JSONObject> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_TEAM_RELATED +teamUuid+"/friendly-game-records.json";
		request(Method.GET, url, access_token, null, listener, errorListener);
		
	}
	
	/**
	 * 9.6 球员联赛战绩接口
	 * @param player_Uuid
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void playerTournamentMatchData(String player_Uuid, String access_token,Listener<JSONObject> listener, ErrorListener errorListener){
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.URL_PLAYER_RELATED);
		if(player_Uuid != null){
			sb.append(player_Uuid + "/league-game-records.json");
		}
		request(Method.GET, sb.toString(), access_token, null, listener, errorListener);
	}
	
	/**
	 * 9.7 球队联赛战绩接口
	 * @param access_token
	 * @param teamUuid
	 * @param listener
	 * @param errorListener
	 */
	public void getTournamentMatchOfTeam(String access_token,String teamUuid,Listener<JSONObject> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_TEAM_RELATED +teamUuid+"/league-game-records.json";
		request(Method.GET, url, access_token, null, listener, errorListener);
		
	}
	
	/**
	 * 7.12 设置球队比赛阵容
	 * @param matchUuid
	 * @param teamUUid
	 * @param formationUid
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void setTeamFormaForMatch(String matchUuid, String teamUUid, String formationUid, 
			String access_token, Listener<Void> listener, ErrorListener errorListener){
		///api/v1.0/match/{match-uuid}/formation.json
		String about_URL = UrlConstants.URL_MATCH_RELATED + matchUuid + "/formation.json";
		
		JSONObject object = new JSONObject();
		try {
			object.put("teamUuid", teamUUid);
			object.put("formationUuid", formationUid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestVoid(Method.POST, about_URL, access_token, object, listener, errorListener);
	}
	
	/**
	 * 7.13 设置球队比赛球衣
	 * @param matchUUid
	 * @param teamUUId
	 * @param teamJersey
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void setTeamJerseyForMatch(String matchUUid, String teamUUId, String teamJersey, 
			String access_token, Listener<Void> listener, ErrorListener errorListener){
		///api/v1.0/match/{match-uuid}/jersey.json
		String about_URL = UrlConstants.URL_MATCH_RELATED + matchUUid + "/jersey.json";
		
		JSONObject object = new JSONObject();
		try {
			object.put("teamUuid", teamUUId);
			object.put("jersey", teamJersey);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestVoid(Method.POST, about_URL, access_token, object, listener, errorListener);
	}

	/**
	 * 7.16 获取某场比赛对其他球员的评价
	 */
	public void getGradeResultForMatch(String matchUuid, String teamUuid, String token, Listener<JSONArray> listener, ErrorListener errorListener){
		///api/v1.0/match/{match-uuid}/player-score/mypost.json?teamUuid={team-uuid}
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.URL_MATCH_RELATED);
		sb.append(matchUuid);
		sb.append("/player-score/mypost.json?teamUuid=");
		sb.append(teamUuid);
		requestArray(Method.GET, sb.toString(), token, null, listener, errorListener);
	}
	
	/**
	 * 10.1 首页搜索接口
	 * @param searchContent
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void aboutHomeSearch(String searchContent, String access_token, Listener<JSONObject> listener, ErrorListener errorListener){
		// /api/v1.0/team-player/search.json?name="xxxx"
		String searchUrl = UrlConstants.IP_ADDRESS + "/v1.0/team-player/search.json?name=" + Uri.encode(searchContent);
		request(Method.GET, searchUrl, access_token, null, listener, errorListener);
	}
	
	/**
	 * 删除阵容
	 * @param accessToken
	 * @param formationUuid 阵容Uuid
	 * @param listener
	 * @param errorListener
	 */
	public void deleteFormation(final String accessToken, String teamUuid,String formationUuid, Response.Listener<String> listener,
			Response.ErrorListener errorListener){
		//DELETE /api/v1.0/team/{team-uuid}/formation/{formation-uuid}.json
		RequestQueue requestQueue = Volley.newRequestQueue(mContext);
		String url = UrlConstants.IP_ADDRESS + "/v1.0/team/" + teamUuid + "/formation/"+ formationUuid+".json";
		StringRequest mStringRequest = new StringRequest(Request.Method.DELETE, url, listener, errorListener){
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = genHeaderParameters();
				headers.put("Authorization", "Bearer " +accessToken);
				return headers;
			}
		};
		requestQueue.add(mStringRequest);
	}
	
	/**
	 * 11.4获取联赛列表
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void getTournamentList(String playerUuid, String access_token, Listener<JSONArray> listener, ErrorListener errorListener){
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.IP_ADDRESS).append("/v1.0/league/list.json?playerUuid=").append(playerUuid);
		requestArray(Method.GET, sb.toString(), access_token, null, listener, errorListener);
	}
	
	/**
	 * 11.5 根据playerUuid获取联赛列表
	 * @param playerUuid
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void getPersonalTournamentList(String playerUuid, String access_token, Listener<JSONArray> listener, ErrorListener errorListener){
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.IP_ADDRESS).append("/v1.0/league/list.json?playerUuid=").append(playerUuid).append("&private=1");
		requestArray(Method.GET, sb.toString(), access_token, null, listener, errorListener);
	}
	
	/**
	 * 约赛应战
	 * @param matchUuid
	 * @param teamUuid
	 * @param islike
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void signUpForMatch(String matchUuid,String teamUuid,String access_token,Listener<Void> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_MATCH_HALL_RELATED + "challenge.json?matchUuid="+matchUuid+"&teamUuid="+teamUuid;
		Log.e("##", url);
		requestVoid(Method.POST, url, access_token, null, listener, errorListener);
	}
	
	/**
	 * 同意约赛应战
	 * @param challengeUuid
	 * @param confirmOrrefuse
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	public void isAgreeOnChallenge(String challengeUuid,String confirmOrrefuse,String access_token,Listener<Void> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_MATCH_HALL_RELATED + "challenge/"+challengeUuid+"/confirm.json?accept="+confirmOrrefuse;
		Log.e("isAgreeOnChallenge", url);
		requestVoid(Method.POST, url, access_token, null, listener, errorListener);
	}
	
	/**
	 * 13.1 发朋友圈消息
	 * @param request
	 * @param accessToken
	 * @param listener
	 * @param errorListener
	 */
	public void genMomentsSharing(SharingRequest request, String accessToken, Listener<JSONObject> listener, ErrorListener errorListener){
		request(Method.POST, UrlConstants.URL_MOMENTS, accessToken, JSONUtils.toJsonObject(request), listener, errorListener);
	}
	
	/**
	 * 13.2 评论 or Like
	 * @param request
	 * @param accessToken
	 * @param listener
	 * @param errorListener
	 */
	public void commentSharing(CommentRequest request, String accessToken, Listener<JSONObject> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_MOMENTS_RELATED + "comment.json";
		request(Method.POST, url, accessToken, JSONUtils.toJsonObject(request), listener, errorListener);
	}
	
	/**
	 * 13.3 获取评论
	 * @param sharingUuid
	 * @param accessToken
	 * @param listener
	 * @param errorListener
	 */
	public void getSharingComments(String sharingUuid, String accessToken, Listener<JSONArray> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_MOMENTS_RELATED + sharingUuid + "/comment.json";
		requestArray(Method.GET, url, accessToken, null, listener, errorListener);
	}
	
	/**
	 * 13.4 获取朋友圈信息
	 * @param page
	 * @param pageSize
	 * @param beginDate
	 * @param endDate
	 * @param accessToken
	 * @param listener
	 * @param errorListener
	 */
	public void getMomentsActives(int page, int pageSize, long beginDate, long endDate, String accessToken, Listener<JSONObject> listener, ErrorListener errorListener){
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.URL_MOMENTS).append("?page=").append(page).append("&pageSize=").append(pageSize);
		if(beginDate > 0l){
			sb.append("&beginDate=").append(beginDate);
		}
		if(endDate > 0l){
			sb.append("&endDate=").append(endDate);
		}
		request(Method.GET, sb.toString(), accessToken, null, listener, errorListener);
	}
	
	/**
	 * 13.5 获取一个人的所有动态
	 * @param page
	 * @param pageSize
	 * @param accessToken
	 * @param listener
	 * @param errorListener
	 */
	public void getMomentsPersonalSharing(String playerUuid, int page, int pageSize, String accessToken, Listener<JSONObject> listener, ErrorListener errorListener){
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.URL_MOMENTS_RELATED).append("player/").append(playerUuid);
		sb.append("/list.json?page=").append(page).append("&pageSize=").append(pageSize);
		request(Method.GET, sb.toString(), accessToken, null, listener, errorListener);
	}
	
	/**
	 * 13.6 删除动态
	 * @param sharingUuid
	 * @param accessToken
	 * @param listener
	 * @param errorListener
	 */
	public void deleteMomentsSharing(String sharingUuid, String accessToken, Listener<Void> listener, ErrorListener errorListener){
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.URL_MOMENTS_RELATED).append(sharingUuid).append(".json");
		requestVoid(Method.DELETE, sb.toString(), accessToken, null, listener, errorListener);
	}
	
	/**
	 * 13.7 删除评论
	 * @param commentUuid
	 * @param accessToken
	 * @param listener
	 * @param errorListener
	 */
	public void deleteMomentsComment(String commentUuid, String accessToken, Listener<Void> listener, ErrorListener errorListener){
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.URL_MOMENTS_RELATED).append("comment/").append(commentUuid).append(".json");
		requestVoid(Method.DELETE, sb.toString(), accessToken, null, listener, errorListener);
	}
	
	/**
	 * 根据uuid获取Sharing
	 * @param uuid
	 * @param accessToken
	 * @param listener
	 * @param errorListener
	 */
	public void getSharingByUuid(String uuid, String accessToken, Listener<JSONObject> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_MOMENTS_RELATED + uuid + ".json";
		request(Method.GET, url, accessToken, null, listener, errorListener);
	}
	
/*	public void getCloud5Ranking(String access_token, int page, int pageSize, String type, Listener<JSONObject> listener, ErrorListener errorListener){
		StringBuilder sb = new StringBuilder();
		sb.append(UrlConstants.URL_CLOUD5_RANKING + "?page=" + page +"&pageSize=" + pageSize + "&type=" + type);
		Log.e("request", sb.toString());
		request(Method.GET, sb.toString(), access_token, null, listener, errorListener);
	}*/
	
	public void getCloud5Ranking(String type ,String access_token, int page, int pageSize, Listener<JSONObject> listener, ErrorListener errorListener){
		String url = UrlConstants.URL_CLOUD5_RANKING + "?page=" + page +"&pageSize=" + pageSize + "&type=" + type;
		request(Method.GET, url, access_token, null, listener, errorListener);
	}
	
	/**
	 * 14.1反馈
	 * @param access_token
	 * @param feedbackBean
	 * @param listener
	 * @param errorListener
	 */
	public void createFeedback(String access_token, FeedbackBean feedbackBean, Listener<Void> listener, ErrorListener errorListener){
		String url = UrlConstants.IP_ADDRESS+"/v1.0/feedback.json";
		requestVoid(Method.POST, url, access_token, JSONUtils.toJsonObject(feedbackBean), listener, errorListener);
	}
	/*--------------------------------------------------------------------------------------------------*/
	/**
	 * 公共请求函数--提取出来
	 * @param method
	 * @param map
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	private void request(int method, String url, final String access_token, JSONObject jsonObject, Listener<JSONObject> listener, ErrorListener errorListener){
		
		JsonObjectRequest jsonRequest = new JsonObjectRequest(method, url, jsonObject, listener, errorListener){
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = genHeaderParameters();
				headers.put("Authorization", "Bearer " + access_token);
				headers.put("Content-Type", "application/json");
				return headers;
			}
		};
		CustomApplcation.getInstance().addToRequestQueue(jsonRequest);
	}
	
	/**
	 * 公共请求函数--提取出来
	 * @param method
	 * @param map
	 * @param access_token
	 * @param listener
	 * @param errorListener
	 */
	private void requestString(int method, String url, JSONObject jsonObject, Listener<JSONObject> listener, ErrorListener errorListener){
		
		JsonObjectRequest jsonRequest = new JsonObjectRequest(method, url, jsonObject, listener, errorListener){
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = genHeaderParameters();
				headers.put("Content-Type", "application/json");
				return headers;
			}
		};
		CustomApplcation.getInstance().addToRequestQueue(jsonRequest);
	}
	
	/**
	 * JSONArray的请求  
	 * 公共请求函数--提取出来
	 * @param get
	 * @param url
	 * @param access_token
	 * @param object
	 * @param listener
	 * @param errorListener
	 */
	private void requestArray(int method, String url, final String access_token,
			JSONObject jsonObject, 
			Listener<JSONArray> listener,
			ErrorListener errorListener) {
		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,listener,errorListener){
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = genHeaderParameters();
				headers.put("Authorization", "Bearer " + access_token);
				headers.put("Content-Type", "application/json");
				return headers;
			}
		};
		CustomApplcation.getInstance().addToRequestQueue(jsonArrayRequest);
	}
	
	/**
	 * 公共请求函数--提取出来 
	 * @param method
	 * @param url
	 * @param access_token
	 * @param jsonObject
	 * @param listener
	 * @param errorListener
	 */
	private void requestVoid(int method, String url, final String access_token,
			JSONObject jsonObject, Listener<Void> listener,
			ErrorListener errorListener) {
		JsonRequest<Void> jsonRequest = new JsonNoResponseRequest(method, url, jsonObject, listener, errorListener){
			
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = genHeaderParameters();
				headers.put("Authorization", "Bearer " + access_token);
				headers.put("Content-Type", "application/json");
				return headers;
			}
		};
		CustomApplcation.getInstance().addToRequestQueue(jsonRequest);
	}
	
	/**
	 * 公共请求函数--提取出来
	 * @param method
	 * @param url
	 * @param access_token
	 * @param jsonObject
	 * @param listener
	 * @param errorListener
	 */
	private void requestVoid4Array(int method, String url, final String access_token,
			JSONArray jsonArray, Listener<Void> listener,
			ErrorListener errorListener) {
		JsonRequest<Void> jsonRequest = new JsonNoResponseRequest(method, url, jsonArray, listener, errorListener){
			
			@Override
			public Map<String, String> getHeaders() {
				HashMap<String, String> headers = genHeaderParameters();
				headers.put("Authorization", "Bearer " + access_token);
				headers.put("Content-Type", "application/json");
				return headers;
			}
		};
		CustomApplcation.getInstance().addToRequestQueue(jsonRequest);
	}
	
	public static HashMap<String, String> genHeaderParameters(){
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("CFB-Udid", CustomApplcation.getInstance().getParameters().getCFB_Udid());//设备唯一标识
		headers.put("CFB-Device", CustomApplcation.getInstance().getParameters().getCFB_Device());//设备
		headers.put("CFB-Resolution", CustomApplcation.getInstance().getParameters().getCFB_Resolution());//分辨率
		headers.put("CFB-Platform-Version", CustomApplcation.getInstance().getParameters().getCFB_Platform_Version());//设备系统版本
		headers.put("CFB-App-Version", CustomApplcation.getInstance().getParameters().getCFB_App_Version());//APP版本
		headers.put("CFB-App-Source", CustomApplcation.getInstance().getParameters().getCFB_App_Source());//APP来源
		headers.put("CFB-Operator", CustomApplcation.getInstance().getParameters().getCFB_Operator());//运营商
//		Log.e("CustomApplcation.getInstance().getParameters().getCFB_Udid()",""+CustomApplcation.getInstance().getParameters().getCFB_Udid());
//		Log.e("CustomApplcation.getInstance().getParameters().getCFB_Device()",""+CustomApplcation.getInstance().getParameters().getCFB_Device());
//		Log.e("CustomApplcation.getInstance().getParameters().getCFB_Resolution()",""+CustomApplcation.getInstance().getParameters().getCFB_Resolution());
//		Log.e("CustomApplcation.getInstance().getParameters().getCFB_Platform_Version()",""+CustomApplcation.getInstance().getParameters().getCFB_Platform_Version());
//		Log.e("CustomApplcation.getInstance().getParameters().getCFB_App_Version()",""+CustomApplcation.getInstance().getParameters().getCFB_App_Version());
//		Log.e("CustomApplcation.getInstance().getParameters().getCFB_App_Source()",""+CustomApplcation.getInstance().getParameters().getCFB_App_Source());
//		Log.e("CustomApplcation.getInstance().getParameters().getCFB_Operator()",""+CustomApplcation.getInstance().getParameters().getCFB_Operator());
		return headers;
	}
	
	/**判断是否有网络连接**/
	public boolean isNetConnected() {
		ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm != null) {
			NetworkInfo[] infos = cm.getAllNetworkInfo();
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			if (infos != null) {
				for (NetworkInfo ni : infos) {
					if (ni.isConnected()) {
						return true;
					} else if (networkInfo != null
							&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
						return true;
					} else {
						//Toast.makeText(EventapproveActivity.this, "没有可用网络！！！", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
		return false;
	}
}
