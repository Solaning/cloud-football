package com.kinth.football.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kinth.football.CustomApplcation;
import com.kinth.football.bean.message.MessageContent;
import com.kinth.football.bean.message.PushMessageAbstract;
import com.kinth.football.config.JConstants;
import com.kinth.football.dao.PushMessage;
import com.kinth.football.dao.PushMessageDao;

public class PushMessageUtil {
	
	/**
	 * 预先处理推送消息
	 * 
	 * @param intent
	 * @return
	 */
	public static <T extends PushMessageAbstract<? extends MessageContent>> PushMessageAbstract<? extends MessageContent> preHandlePushMessage(
			Context mContext, Intent intent, Class<T> clazz) {
		String json = intent.getStringExtra(JConstants.INTENT_JSON_STRING);
		return preHandlePushMessage(mContext, json, clazz);
	}

	/**
	 * 预先处理推送消息
	 * 
	 * @param intent
	 * @return
	 */
	public static <T extends PushMessageAbstract<? extends MessageContent>> PushMessageAbstract<? extends MessageContent> preHandlePushMessage(
			Context mContext, String json, Class<T> clazz) {
		if (TextUtils.isEmpty(json)) {
			return null;
		}
		PushMessageAbstract<? extends MessageContent> tempMessage = null;
		try {
			Gson gson = new Gson();
			tempMessage = gson.fromJson(json, type(clazz, clazz));

			// 保存消息到数据库 //未读数据
			PushMessageDao pushMessageDao = CustomApplcation.getDaoSession(
					mContext.getApplicationContext()).getPushMessageDao();
			PushMessage dbPushMessage = new PushMessage();
			dbPushMessage.setDate(tempMessage.getDate());
			dbPushMessage.setType(tempMessage.getType());
			dbPushMessage.setHasRead(false); // 数据库中false显示为0
			dbPushMessage.setIsClick(0); // 默认为0，未点击（1：点击了“确认”“取消”按钮）

			dbPushMessage.setContent(JSONUtils.toJsonObject(
					tempMessage.getContent()).toString());
			pushMessageDao.insertOrReplace(dbPushMessage);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			return null;
		}
		return tempMessage;
	}

	static ParameterizedType type(final Class<?> raw, final Type... args) {
		return new ParameterizedType() {
			public Type getRawType() {
				return raw;
			}

			public Type[] getActualTypeArguments() {
				return args;
			}

			public Type getOwnerType() {
				return null;
			}
		};
	}
}
