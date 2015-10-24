package com.kinth.football.chat.bean;

import java.io.Serializable;

public class ChatRecent implements Comparable<ChatRecent>, Serializable {

	private static final long serialVersionUID = 1L;
	private String targetid; // 目标id
	private String userName; // 用户名字
	private String nick; // 昵称
	private String avatar; // 头像
	private String message; // 消息
	private long time; // 消息事件
	private String type; // 消息类型
	private int tag;	//群聊标识

	public ChatRecent() {

	}

	public ChatRecent(String targetid, String userName, String nick,
			String avatar, String message, long time, String type, int tag) {
		this.targetid = targetid;
		this.avatar = avatar;
		this.nick = nick;
		this.userName = userName;
		this.message = message;
		this.time = time;
		this.type = type;
		this.tag = tag;
	}

	public String getNick() {
		return this.nick;
	}

	public void setNick(String paramString) {
		this.nick = paramString;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTargetid() {
		return this.targetid;
	}

	public void setTargetid(String paramString) {
		this.targetid = paramString;
	}

	public String getAvatar() {
		return this.avatar;
	}

	public void setAvatar(String paramString) {
		this.avatar = paramString;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String paramString) {
		this.userName = paramString;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String paramString) {
		this.message = paramString;
	}

	public long getTime() {
		return this.time;
	}

	public void setTime(long paramLong) {
		this.time = paramLong;
	}
	
	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public final int hashCode() {
		return 10 * getUserName().hashCode();
	}

	public final boolean equals(Object paramObject) {
		if ((paramObject == null) || (!(paramObject instanceof ChatRecent)))
			return false;
		return getUserName().equals(((ChatRecent) paramObject).getUserName());
	}

	public final String toString() {
		if (this.nick == null)
			return this.userName;
		return this.nick;
	}

	public int compareTo(ChatRecent paramBmobRecent) {
		return (int) (paramBmobRecent.time - this.time);
	}

}
