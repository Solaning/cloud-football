package com.kinth.football.chat;

import java.io.IOException;
import java.lang.reflect.Field;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.sasl.provided.SASLPlainMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.TLSUtils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.kinth.football.bean.User;
import com.kinth.football.chat.bean.ChatMsg;
import com.kinth.football.chat.bean.ChatRecent;
import com.kinth.football.chat.listener.ChatManagerListener2;
import com.kinth.football.chat.listener.RefreshEvent;
import com.kinth.football.chat.listener.TaxiConnectionListener;
import com.kinth.football.chat.util.ShortUUID;
import com.kinth.football.config.UrlConstants;
import com.kinth.football.listener.SendMsgListener;
import com.kinth.football.manager.UserManager;

import de.greenrobot.event.EventBus;

/**
 * XMPP
 */
public class XmppManager {
	
	public static final String TAG = "XmppManager";
	private static volatile XmppManager xmppManager = null;
	private static XMPPTCPConnection connection;
	private static Context mContext;
	private static ChatManagerListener2 chatManagerListener;
	private static User user;
	private static TaxiConnectionListener connectionListener;

	private XmppManager(){};
	
	public static XmppManager getInstance(Context context) {
		if (xmppManager == null)
			synchronized (XmppManager.class) {
				if (xmppManager == null)
					xmppManager = new XmppManager();
				init(context);
			}
		return xmppManager;
	}

	public static void init(Context context) {
		mContext = context;
		user = UserManager.getInstance(mContext).getCurrentUser();
		try {
			SASLAuthentication.registerSASLMechanism(new SASLPlainMechanism());
			XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration
					.builder().setServiceName(UrlConstants.HOST_NAME)
					.setHost(UrlConstants.HOST_NAME).setPort(UrlConstants.XMPP_PORT)
					.setDebuggerEnabled(false);
			TLSUtils.acceptAllCertificates(configBuilder);

			XMPPTCPConnectionConfiguration config = configBuilder.build();
			Field field = ConnectionConfiguration.class
					.getDeclaredField("socketFactory");
			field.setAccessible(true);
			field.set(config, config.getCustomSSLContext().getSocketFactory());

			connection = new XMPPTCPConnection(config);
			connection.setPacketReplyTimeout(10000);
			connectionListener = new TaxiConnectionListener(context);
			connection.addConnectionListener(connectionListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loginAndAddChatManagerListener(final Context context,
			final String name, final String pwd, final ConnectionImp listner) {

		login(name, pwd, new ConnectionImp() {

			@Override
			public void onSucc() {
				if (chatManagerListener == null)
					chatManagerListener = new ChatManagerListener2(context);

				ChatManager.getInstanceFor(connection).addChatListener(
						chatManagerListener);
				listner.onSucc();
			}

			@Override
			public void onFail() {
				listner.onFail();
			}
		});
	}

	public void login(final String name, final String pwd,
			final ConnectionImp listner) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (!connection.isConnected()) {
					try {
						connection.connect();
					} catch (SmackException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						listner.onFail();
						return;
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						listner.onFail();
						return;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						listner.onFail();
						return;
					}
				}

				try {
					if (!connection.isAuthenticated()) {
						connection.login(name, pwd);
					}
					listner.onSucc();
				} catch (Exception e) {
					e.printStackTrace();
					listner.onFail();
				}
			}
		}).start();
	}

	public void sendTextMessage(final User targetUser, final ChatMsg chatMsg,
			final SendMsgListener listener) {
		login(user.getPlayer().getAccountName(), user.getToken(),
				new ConnectionImp() {

					@Override
					public void onSucc() {
						// 保存数据聊天信息
						MyChatManager.getInstance(mContext).saveChatMessage(
								chatMsg);
						ChatRecent recent = new ChatRecent(targetUser
								.getPlayer().getAccountName(), targetUser
								.getPlayer().getUuid(), targetUser.getPlayer()
								.getName(),
								targetUser.getPlayer().getPicture(), chatMsg
										.getContent(), Long.parseLong(chatMsg
										.getMsgTime()), chatMsg.getMsgType(), chatMsg.getTag());

						// 保存最近聊天信息
						MyChatManager.getInstance(mContext).saveRecentMessage(
								recent);

						// 1.创建一个会话
						String userJID = null;
						if(chatMsg.getTag()==ChatConstants.TAG_GROUP){//球队群聊
							Log.e("before", targetUser.getPlayer().getAccountName());
							String teamUuid = ShortUUID.toUUID(targetUser.getPlayer().getAccountName());//服务器那边会把teamUuid全部转为小写报错，所以要转为另外一种形式
							Log.e("after", teamUuid);
							userJID = teamUuid
									+ "@broadcast."
									+ connection.getServiceName();
						}else{
							userJID = targetUser.getPlayer().getAccountName()
									+ "@" + connection.getServiceName();
						}
							
						Chat chat = ChatManager.getInstanceFor(connection)
								.createChat(userJID, null);

						// 2.将chatMsg解析为一个Json字符串
						Gson gson = new Gson();
						String json = gson.toJson(chatMsg);

						try {
							chat.sendMessage(json); // 发送成功
							listener.onSuccess(chatMsg);
							
							EventBus.getDefault().post(new RefreshEvent());//刷新ChatFragment消息
						} catch (NotConnectedException e) {// 发送失败
							// TODO Auto-generated catch block
							e.printStackTrace();
							listener.onFailure(chatMsg);
							MyChatManager.getInstance(mContext).saveChatMessage(
									chatMsg);
						}
					}

					@Override
					public void onFail() {
						listener.onFailure(chatMsg);
						MyChatManager.getInstance(mContext).saveChatMessage(
								chatMsg);
					}
				});
	}

	public void disconnect() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Presence presence = new Presence(Presence.Type.unavailable, "",
						1, Presence.Mode.away);
				try {
					if (connection != null) {
						if (connectionListener != null) {
							connection
									.removeConnectionListener(connectionListener);
						}
						if (connection.isConnected())
							connection.disconnect(presence);
					}
				} catch (NotConnectedException e) {
				} finally {
					synchronized (XmppManager.class) {
						xmppManager = null;
					}
				}
			}
		}).start();
	}
}
