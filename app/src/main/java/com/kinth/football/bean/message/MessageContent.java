package com.kinth.football.bean.message;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * 消息的内容--最基本的内容描述
 * @author Sola
 *
 */
public abstract class MessageContent implements Parcelable{
	
	@Expose
	protected String description;  
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

}
