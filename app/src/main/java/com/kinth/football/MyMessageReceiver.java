package com.kinth.football;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.RemoteViews;

import com.kinth.football.bean.User;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.chat.bean.ChatMsg;
import com.kinth.football.chat.ui.RecentActivity;
import com.kinth.football.dao.Team;
import com.kinth.football.listener.EventListener;
import com.kinth.football.manager.UserManager;
import com.kinth.football.ui.MainActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @ClassName: MyMessageReceiver
 */
public class MyMessageReceiver {

	// 事件监听
	public static ArrayList<EventListener> ehList = new ArrayList<EventListener>();

	public static final int NOTIFY_ID = 0x000;
	public static int mNewNum = 0;//
	UserManager userManager;
	User currentUser;

	public static NotificationManager notificationManager;
	/** NotificationCompat 构造器 */
	static NotificationCompat.Builder mBuilder;


	/**
	 * 显示通知
	 * 
	 * @Title: showNotify
	 * @return void
	 * @throws
	 */
	@SuppressLint({ "InlinedApi", "SimpleDateFormat" })
	public static void showNotification(Context context, Team team, ChatMsg msg) {
		// avoidNetworkOnMainThreadException();

		notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		// 更新通知栏
		String trueMsg = "";
		if (msg.getMsgType()
				.equals(String.valueOf(ChatConstants.MSG_TYPE_TEXT))
				&& msg.getContent().contains("[f")) {
			trueMsg = "[表情]";
		} else if (msg.getMsgType().equals(
				String.valueOf(ChatConstants.MSG_TYPE_IMAGE))) {
			trueMsg = "[图片]";
		} else if (msg.getMsgType().equals(
				String.valueOf(ChatConstants.MSG_TYPE_AUDIO))) {
			trueMsg = "[语音]";
		} else {
			trueMsg = msg.getContent();
		}
		CharSequence tickerText = msg.getBelongNick();
		if (team != null)
			tickerText = team.getName();
		String contentTitle = trueMsg;
		if (team != null)
			contentTitle = msg.getBelongNick() + "：" + trueMsg;
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");// 设置日期格式
		String currentTime = df.format(new Date());// new Date()为获取当前系统时间

		// 跳转到本地媒体
		// Intent intent = new Intent(context, RecentActivity.class);
		PendingIntent pi = PendingIntent.getActivities(context, 0,
				makeIntentStack(context), PendingIntent.FLAG_CANCEL_CURRENT);

		// 先设定RemoteViews
		RemoteViews view_custom = new RemoteViews(context.getPackageName(),
				R.layout.view_custom);

		// 设置对应IMAGEVIEW的ID的资源图片
		String avatar = msg.getBelongAvatar().trim();
		if (team != null)
			avatar = team.getBadge();
		if (avatar != null && (!avatar.equals(""))) {
			avoidNetworkOnMainThreadException();
			view_custom.setImageViewBitmap(R.id.custom_icon, ImageLoader
					.getInstance().loadImageSync(avatar,new DisplayImageOptions.Builder()
					.showImageOnLoading(
							R.drawable.team_bage_default)
					.showImageForEmptyUri(
							R.drawable.team_bage_default)
					.showImageOnFail(
							R.drawable.team_bage_default)
					.cacheInMemory(true)
					.cacheOnDisk(true).build()));
		} else {
			if (team != null){
				view_custom.setImageViewBitmap(R.id.custom_icon, BitmapFactory
						.decodeResource(context.getResources(),
								R.drawable.team_bage_default));
			}else
				view_custom.setImageViewBitmap(R.id.custom_icon,
						BitmapFactory.decodeResource(context.getResources(),
								R.drawable.head));
		}
		
		if(msg.getBelongNick().equals("云球团队"))
			view_custom.setImageViewBitmap(R.id.custom_icon, BitmapFactory
					.decodeResource(context.getResources(),
							R.drawable.ic_launcher));

		view_custom.setTextViewText(R.id.tv_custom_title, tickerText);
		view_custom.setTextViewText(R.id.tv_custom_content, contentTitle);
		view_custom.setTextViewText(R.id.tv_custom_time, currentTime);

		mBuilder = new Builder(context);
		mBuilder.setContent(view_custom).setContentIntent(pi)
				.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
				.setTicker("有新资讯").setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
				.setOngoing(false)// 不是正在进行的 true为正在进行 效果和.flag一样
				.setSmallIcon(R.drawable.ic_launcher);
		Notification notify = mBuilder.build();
		notify.contentView = view_custom;
		notificationManager.notify(0, notify);
	}

	// 创建activity栈的根activity
	private static Intent[] makeIntentStack(Context context) {
		Intent[] intents = new Intent[2];
		intents[0] = Intent.makeRestartActivityTask(new ComponentName(context,
				MainActivity.class));
		intents[1] = new Intent(context, RecentActivity.class);
		intents[1].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		return intents;
	}

	/**
	 * 造成这样的错误原因是代码不符合Android规范，如果把上面访问方式改为异步操作就不会出现在4.0上访问出现
	 * android.os.NetworkOnMainThreadException异常
	 */

	private static void avoidNetworkOnMainThreadException() {
		// TODO 自动生成的方法存根
		// 详见StrictMode文档
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());
	}

}
