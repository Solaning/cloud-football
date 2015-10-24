package com.kinth.football.bean.message;

import com.google.gson.annotations.Expose;

import android.os.Parcelable;

/**
 * 推送消息的抽象父类
 * @author Sola
 * 
 */
public abstract class PushMessageAbstract<T extends MessageContent> implements Parcelable{
	
	@Expose
	protected String type;//消息类型
	
	@Expose
	protected long date;//时间
	
	@Expose
	protected T content;//消息内容

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public T getContent() {
		return content;
	}

	public void setContent(T content) {
		this.content = content;
	}

}
