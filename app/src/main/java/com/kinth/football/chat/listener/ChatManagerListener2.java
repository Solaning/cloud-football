package com.kinth.football.chat.listener;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.CustomApplcation;
import com.kinth.football.MyMessageReceiver;
import com.kinth.football.bean.TeamInfoComplete;
import com.kinth.football.bean.User;
import com.kinth.football.bean.message.MatchFinishedPM;
import com.kinth.football.bean.message.SharingCommentedPM;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.chat.MyChatManager;
import com.kinth.football.chat.bean.ChatMsg;
import com.kinth.football.config.Action;
import com.kinth.football.config.JConstants;
import com.kinth.football.config.PushMessageEnum;
import com.kinth.football.dao.Team;
import com.kinth.football.eventbus.match.MatchFinishedEvent;
import com.kinth.football.eventbus.message.ExitTeamPushMessageEvent;
import com.kinth.football.eventbus.message.RequestJoinTeamConfirmPushMessageEvent;
import com.kinth.football.eventbus.message.RequestJoinTeamPushMessageEvent;
import com.kinth.football.eventbus.message.SharingCommentedPushMessageEvent;
import com.kinth.football.eventbus.message.TeamPlayerInvitationConfirmPushMessageEvent;
import com.kinth.football.eventbus.message.TeamPlayerInvitationPushMessageEvent;
import com.kinth.football.listener.EventListener;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.util.FileOperation;
import com.kinth.football.util.LogUtil;
import com.kinth.football.util.PushMessageUtil;

import de.greenrobot.event.EventBus;

public class ChatManagerListener2 implements ChatManagerListener {
	private Context mContext;

	public ChatManagerListener2(Context context) {
		super();
		this.mContext = context;
	}

	@Override
	public void chatCreated(Chat arg0, boolean arg1) {

		arg0.addMessageListener(new ChatMessageListener() {

			@Override
			public void processMessage(Chat arg0, Message arg1) {
				// 消息内容
				String body = arg1.getBody();
				Log.e("processMessage", body);
				String type = null;
				User currentUser = UserManager.getInstance(mContext).getCurrentUser();
				if (currentUser != null) {
					JSONObject jo;
					try {
						jo = new JSONObject(body);
						try {
							type = jo.getString("type");
						} catch (JSONException e) {
							type = null;
						}
						
						String fromId = null;
						try {
							fromId = jo.getString("conversationId").split("&")[0];
						} catch (JSONException e) {
							fromId = null; // 不能和tag放在一起，不然的话这里出错了tag也为null了
						}

						String toId = null;
						try {
							toId = jo.getString("conversationId").split("&")[1];
						} catch (JSONException e) {
							toId = null; // 不能和tag放在一起，不然的话这里出错了tag也为null了
						}

						if (fromId!=null && fromId.equals(currentUser.getPlayer().getAccountName())) {
							// 自己发送的消息，不处理
							return;
						}
						
						int tag = 0;
						try {
							tag = jo.getInt("tag");
						} catch (JSONException e) {
							tag = 0;
						}

						/*if (toId != null && toId.length() > 20) {// 接受者是球队Uuid，长度大于20
							sendGroupMessage(context, toId, body);
							return;
						}*/
						if (toId != null){
							if(tag==ChatConstants.TAG_GROUP){
								sendGroupMessage(mContext, toId, body);
								return;
							}
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
						LogUtil.i("parseMessage错误：" + e.getMessage());
					}

					if (TextUtils.isEmpty(type)) {// 不携带tag标签(普通聊天)
						// 1.组装ChatMsg对象
						ChatMsg msg = ChatMsg.createReceiveMsg(mContext, body);
						MyChatManager.getInstance(mContext)
								.saveReceiveMessage(true, msg);
						// 播放声音
						CustomApplcation.getInstance().getMediaPlayer().start();
						MyMessageReceiver.showNotification(mContext,null, msg);
						if (MyMessageReceiver.ehList.size() > 0) {// 有监听的时候，传递下去
							for (int i = 0; i < MyMessageReceiver.ehList.size(); i++) {
								((EventListener) MyMessageReceiver.ehList.get(i)).onMessage(msg);
							}
						}
						return;
					}
					Intent intent = new Intent();
					intent.putExtra(JConstants.INTENT_JSON_STRING, body);
					switch (PushMessageEnum.getEnumFromString(type)) {
					case TEAM_PLAYER_INVITATION:// 球队成员邀请
						EventBus.getDefault().post(new TeamPlayerInvitationPushMessageEvent(body));
						break;
					case TEAM_PLAYER_INVITATION_CONFIRM:// 球队成员邀请确认
						EventBus.getDefault().post(new TeamPlayerInvitationConfirmPushMessageEvent(body));
						break;
					case MATCH_INVITATION:// 比赛邀请
						intent.setAction(Action.ACTION_BROADCAST_MATCH_INVITATION);
						mContext.sendBroadcast(intent);
						break;
					case MATCH_INVITATION_CONFIRM:// 比赛确认
						intent.setAction(Action.ACTION_BROADCAST_MATCH_INVITATION_CONFIRM);
						mContext.sendBroadcast(intent);
						break;
					case MATCH_CREATED:// 创建成功，等待报名
						intent.setAction(Action.ACTION_BROADCAST_MATCH_CREATED);
						mContext.sendBroadcast(intent);
						break;
					case MATCH_SIGNUP:// 有人报名
						intent.setAction(Action.ACTION_BROADCAST_MATCH_SIGNUP);
						mContext.sendBroadcast(intent);
						break;
					case MATCH_PENDING:// 待开赛
						intent.setAction(Action.ACTION_BROADCAST_MATCH_PENDING);
						mContext.sendBroadcast(intent);
						break;
					case MATCH_KICK_OFF:// 比赛中
						intent.setAction(Action.ACTION_BROADCAST_MATCH_KICK_OFF);
						mContext.sendBroadcast(intent);
						break;
					case MATCH_OVER:// 待评价
						intent.setAction(Action.ACTION_BROADCAST_MATCH_OVER);
						mContext.sendBroadcast(intent);
						break;
					case MATCH_FINISHED:// 比赛关闭
						MatchFinishedPM matchFinishedPM = (MatchFinishedPM) PushMessageUtil.preHandlePushMessage(mContext, body, MatchFinishedPM.class);
						if(matchFinishedPM != null)
							EventBus.getDefault().post(new MatchFinishedEvent(matchFinishedPM));
						break;
					case MATCH_CANCELED:// 比赛取消
						intent.setAction(Action.ACTION_BROADCAST_MATCH_CANCELED);
						mContext.sendBroadcast(intent);
						break;
					case MATCH_REFEREE_INVITATION:// 通知球员是裁判
						intent.setAction(Action.ACTION_BROADCAST_MATCH_REFEREE_INVITATION);
						mContext.sendBroadcast(intent);
						break;
					case MATCH_CHALLENGE://比赛应战
						intent.setAction(Action.ACTION_BROADCAST_MATCH_CHALLENGE);
						mContext.sendBroadcast(intent);
						break;
					case MATCH_CHALLENGE_CONFIRM:
						intent.setAction(Action.ACTION_BROADCAST_MATCH_CHALLENGE_CONFIRM);
						mContext.sendBroadcast(intent);
						break;
					case MATCH_KICK_OFF_NOTIFICATION://比赛开赛前通知
						intent.setAction(Action.ACTION_BROADCAST_MATCH_KICK_OFF_NOTIFICATION);
						mContext.sendBroadcast(intent);
						break;
					case MATCH_OVER_TO_REFEREE://比赛开赛前通知-裁判
						Log.e("1", "MATCH_OVER_TO_REFEREE");
						intent.setAction(Action.ACTION_BROADCAST_MATCH_KICK_OFF_NOTIFICATION);
						mContext.sendBroadcast(intent);
						break;
					case TEAM_PLAYER_APPLICATION://申请加入球队
						EventBus.getDefault().post(new RequestJoinTeamPushMessageEvent(body));
						break;
					case TEAM_PLAYER_APPLICATION_CONFIRM://申请加入球队确认
						EventBus.getDefault().post(new RequestJoinTeamConfirmPushMessageEvent(body));
						break;
					case TEAM_PLAYER_QUIT://退出球队
						EventBus.getDefault().post(new ExitTeamPushMessageEvent(body));
						break;
					case SHARING_COMMENTED://朋友圈评论
						SharingCommentedPM sharingCommentedPM = (SharingCommentedPM) PushMessageUtil.preHandlePushMessage(mContext, body, SharingCommentedPM.class);
						EventBus.getDefault().post(new SharingCommentedPushMessageEvent(sharingCommentedPM));
						break;
					case NULL:
						break;
					}
				}
//				FileOperation.writeFileAppend("sender:"
//						+ arg1.getFrom().split("@")[0] + "\n");
//				FileOperation.writeFileAppend("body:" + body);
			}
		});

	}
	
	private void sendGroupMessage(final Context mContext, String teamUuid,
			final String json) {
		NetWorkManager.getInstance(mContext).getTeam(teamUuid,
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// {"firstCaptainUuid":"22bee0e9-15c9-4ddf-87b6-79bd4d21698b","slogan":null,"secondCaptainUuid":null,"cityId":289,"description":null,"name":"HK","familyPhoto":null,"thirdCaptainUuid":null,"creatorUuid":"22bee0e9-15c9-4ddf-87b6-79bd4d21698b","badge":null,"uuid":"2dqYfDRmij-05yW8yAQsr4","date":1425372009889,"regionId":1}
						Gson gson = new Gson();
						TeamInfoComplete teamInfoComplete = gson.fromJson(
								response.toString(),
								new TypeToken<TeamInfoComplete>() {
								}.getType());
						Team team = teamInfoComplete.getTeam();
						if (team == null)
							return;
						team.setLike(teamInfoComplete.getLike());
						team.setLiked(teamInfoComplete.isLiked());// by sola
						ChatMsg msg = ChatMsg.createGroupReceiveMsg(mContext,
								json);
						MyChatManager.getInstance(mContext)
								.saveGroupReceiveMessage(team, msg);
						CustomApplcation.getInstance().getMediaPlayer().start();
						MyMessageReceiver.showNotification(mContext, team, msg);
						if (MyMessageReceiver.ehList.size() > 0) {// 有监听的时候，传递下去
							for (int i = 0; i < MyMessageReceiver.ehList.size(); i++) {
								((EventListener) MyMessageReceiver.ehList
										.get(i)).onMessage(msg);
							}
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// VolleyLog.e("Error: ", error.getMessage());
						// Log.e("response error", "" + error.getMessage());
					}
				});
	}
}
