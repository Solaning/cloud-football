package com.kinth.football.manager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kinth.football.R;
import com.kinth.football.bean.Update;
import com.kinth.football.config.JConstants;
import com.kinth.football.util.FileUtil;

/**
 * @author Botision.Huang
 * Date: 2015-3-25
 * Descp: 更新版本
 * 
 * Thread：java的线程是通过java.lang.Thread类来实现的。
 * VM启动时会有一个由主方法所定义的线程。
 * 可以通过创建Thread的实例来创建新的线程。
 * 每个线程都是通过某个特定Thread对象所对应的方法run（）来完成其操作的，方法run()称为线程体。
 * 通过调用Thread类的start()方法来启动一个线程。
 */
public class UpdateVersionManager extends Thread {
	
	public static final String TAG = "UpdateVersionManager";

	public static final int MSG_SHOW_ALERT_DIALOG = 2015032505;
	public static final int MSG_SET_PROGRESS_MAX = 2015032506;
	public static final int MSG_SET_PROGRESS = 2015032507;
	public static final int MSG_SHOW_TOAST = 2015032508;
	public static final int MSG_INSTALL_APK = 2015032509;
	
	private Context mContext;
	private Update update;
	
	private ProgressDialog pDialog;
	private Thread downloadAndInstallThread = null;  //下载以及安装新版本的线程
	private boolean run = false;   
	private boolean hasSDCard = true;  //判断是否有SD卡
	private File apkFile = null;        //从服务器上下载下来的安装包file
	
	//private Handler handler;// UI线程的handler，需要判断是否为null
	
	public UpdateVersionManager(Context mContext, Update update){
		this.mContext = mContext;
		this.update = update;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (update == null) {
			Toast.makeText(mContext, "版本信息有误，更新失败", Toast.LENGTH_SHORT).show();
			return;
		}
		 //告诉thisHandler说我的消息类型为MSG_SHOW_ALERT_DIALOG
		Message msg = thisHandler.obtainMessage(MSG_SHOW_ALERT_DIALOG);
		thisHandler.sendMessage(msg);
	}
	
	private Handler thisHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case MSG_SHOW_ALERT_DIALOG:  //弹出窗口
				UpdateDialog updateDialog = new UpdateDialog(mContext,
						R.style.MyDialogStyle);
				updateDialog.show();
				break;
			case MSG_SET_PROGRESS_MAX:   //获取不到安装包的长度
				Integer max = (Integer) msg.obj;
				if (max != null && max != 0){
					pDialog.setMax(max);
				}
				break;
			case MSG_SET_PROGRESS:
				Integer value = (Integer) msg.obj;
				if (value != null && value != 0) {
					pDialog.setProgress(value);
				}
				break;
			case MSG_SHOW_TOAST:
				String str = (String) msg.obj;
				if (str == null || str.equals("")) {
					Toast.makeText(mContext, "thisHandler参数错误", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
				};
			case MSG_INSTALL_APK:
				if (apkFile != null) {
					installApk(apkFile);
				}
				break;
			default:
				break;
			}
		}
	};
	
	private void downloadAndInstallApk() {
		pDialog = new ProgressDialog(mContext);
		pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pDialog.setCanceledOnTouchOutside(false);
		pDialog.setTitle("正在下载最新安装包");
		pDialog.setCancelable(true);
		//如果下载到一半，用户点击了取消按钮
		pDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (downloadAndInstallThread != null
						&& downloadAndInstallThread.isAlive()) {
					// 中断下载线程
					run = false;
				}
			}
		});
		pDialog.show();   
		
		downloadAndInstallThread = new Thread(new Runnable() {
			@Override
			public void run() {
				run = true;
				File file = downloadApkFromServer();
				if (hasSDCard == false && file == null) {
					// 没有sdcard，下载失败
					String str = mContext.getResources().getString(
							R.string.error_download_for_no_sdcard);
					Message msg = thisHandler
							.obtainMessage(MSG_SHOW_TOAST, str);
					/*Message msg = thisHandler.obtainMessage();
					msg.what = MSG_SHOW_TOAST;
					msg.obj = str;
					其中用thisHandler.obtainMessage()，而不是直接new Message()，是为了避免创建新的对象，减少内存的开销*/
					thisHandler.sendMessage(msg);
				}else if (file == null && run == false) {
					// 取消下载
					String str = mContext.getResources().getString(
							R.string.hint_cancel_downlaod);
					Message msg = thisHandler
							.obtainMessage(MSG_SHOW_TOAST, str);
					thisHandler.sendMessage(msg);
				} else if (file != null && hasSDCard == true) {
					// 下载成功
					apkFile = file;
					Message msg = thisHandler.obtainMessage(MSG_INSTALL_APK);
					thisHandler.sendMessage(msg);
				} else {
					String str = mContext.getResources().getString(
							R.string.error_download_for_unkown_reason);
					Message msg = thisHandler
							.obtainMessage(MSG_SHOW_TOAST, str);
					thisHandler.sendMessage(msg);
				}
				pDialog.dismiss();
			}
		});
		downloadAndInstallThread.start();
	}
	
	private File downloadApkFromServer() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			hasSDCard = true;
			URL url = null;
			HttpURLConnection conn = null;
			InputStream is = null;
			FileOutputStream fos = null;
			BufferedInputStream bis = null;
			File file = null;
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			boolean ok = true;
			try {
				url = new URL(update.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				// 设置进度条的最大值
				int length = conn.getContentLength();
				if (length == -1) {// 获取长度失败
					Message msg = thisHandler.obtainMessage(MSG_SET_PROGRESS_MAX, 10000001);
					thisHandler.sendMessage(msg);
				} else {          
					Message msg = thisHandler.obtainMessage(
							MSG_SET_PROGRESS_MAX, conn.getContentLength());
					thisHandler.sendMessage(msg);
				}
				is = conn.getInputStream();
				bis = new BufferedInputStream(is);
				
				file = new File(JConstants.PROGRAM_DIRECTORY + "/update.apk");
				if(!file.exists()){
					FileUtil.createFile(file, true);// 创建文件
				}
				fos = new FileOutputStream(file, false);
				while ((len = bis.read(buffer)) != -1 && run == true) {
					fos.write(buffer, 0, len);
					total += len;
					// 显示下载进度
					Message msg2 = thisHandler.obtainMessage(MSG_SET_PROGRESS,
							total);
					thisHandler.sendMessage(msg2);
				}
				// 中途退出下载线程
				if (run == false) {
					ok = false;
				}
			} catch (MalformedURLException e) {
				ok = false;
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ok = false;
				e.printStackTrace();
			} finally {
				try {
					if (is != null) {
						is.close();
						is = null;
					}
					if (bis != null) {
						bis.close();
						bis = null;
					}
					if (fos != null) {
						fos.close();
						fos = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				// 若异常，并且文件存在，删除它
				if (ok == false && file != null && file.exists()) {
					file.delete();
					file = null;
				}
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
			}
			return file;
		} else {
			Log.e(TAG, "无法识别存储卡sdcard");
			hasSDCard = false;
			return null;
		}
	}
	
	private void installApk(File file) {
		if (file == null) {
			return;
		}
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}
	
	public class UpdateDialog extends Dialog implements OnClickListener{
		private Context context;
		private Button yesButton; // “确认下载”按钮
		private Button noButton; // “以后再说”按钮
		private TextView dialog_title;// 标题
		private TextView version_description;
		
		public UpdateDialog(Context context) {
			super(context);
			this.context = context;
		}

		public UpdateDialog(Context context, int theme) {
			super(context, theme);
			this.context = context;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.sola_update_dialog_layout);
			yesButton = (Button) findViewById(R.id.update_to_new_version);
			noButton = (Button) findViewById(R.id.cancel_to_update);
			dialog_title = (TextView) findViewById(R.id.update_dialog_title);
			version_description = (TextView) findViewById(R.id.new_version_description);

			yesButton.setOnClickListener(this);
			noButton.setOnClickListener(this);

			if (update != null) {
				dialog_title.setText("有新版本 " + update.getVersion());
				version_description.setText(update.getDescription());
			}
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.update_to_new_version:// 下载
				dismiss();
				downloadAndInstallApk();// 开始下载
				break;
			case R.id.cancel_to_update:
				dismiss();
				break;
			default:
				break;
			}
		}
	}
}
