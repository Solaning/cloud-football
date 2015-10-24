package com.kinth.football.chat.listener;

/**
 * 下载操作监听器
 * @author Administrator
 *
 */
public abstract interface DownloadListener
{
  public abstract void onStart();//开始下载

  public abstract void onSuccess();//下载成功

  public abstract void onError(String paramString);//下载失败，传入参数:失败原因
}