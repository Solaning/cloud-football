package com.kinth.football.chat.listener;

import com.kinth.football.chat.bean.ChatMsg;

public abstract interface UploadListener
{
  public abstract void onStart();

  public abstract void onSuccess(ChatMsg paramChatMsg);

  public abstract void onFailure(int paramInt, String paramString);
}