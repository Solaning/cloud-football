package com.kinth.football.chat.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.kinth.football.config.UrlConstants;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AsyncRecordFileUpload {

	public AsyncRecordFileUpload() {

	}

	public static String uploadFile(String filepath) {
		String rsltURL = "";
		try {
			URL url = new URL(UrlConstants.UPLOAD_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();//利用HttpURLConnection对象从网络中获取网页数据
			conn.setDoOutput(true);// 设置容许输出，即允许上传
			conn.setDoInput(true);// 设置容许输入，即允许下载
			conn.setUseCaches(false);// 设置不使用缓存
			conn.setRequestMethod("POST");// 设置使用POST的方式发送
			/* 设置通用的请求属性 */
			conn.setRequestProperty("connection", "Keep-Alive");// 设置维持长连接
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("Charsert", "UTF-8");// 设置文件字符集
			conn.setRequestProperty("Content-Type", "multipart/form-data; ");// 设置文件类型
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			File file = new File(filepath);
			StringBuilder sb = new StringBuilder();
			byte[] data = sb.toString().getBytes();
			out.write(data);
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			in.close();
			out.flush();
			out.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String photourl = "";
			String line = "";
			while ((line = reader.readLine()) != null) {
				photourl += line;
			}
			if (photourl.equals("")) {
			} else {
				rsltURL = photourl;
				Log.i("AsyncFileUpload", photourl);
			}
		} catch (Exception e) {
			e.printStackTrace();
			rsltURL = "";
		}
		return rsltURL;
	}

	public static void asynFileUploadToServer(final String fileName,
			final FileCallback fileCallback) {
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				fileCallback.fileUploadCallback((String) message.obj);
			}
		};
		new Thread() {
			@Override
			public void run() {
				String url = uploadFile(fileName);
				// Log.i("AsyncFileUpload", url);
				Message message = handler.obtainMessage(0, url);
				handler.sendMessage(message);
			}
		}.start();
	}

	public interface FileCallback {// TODO 添加错误的回调
		public void fileUploadCallback(String fileUrl);
	}
}
