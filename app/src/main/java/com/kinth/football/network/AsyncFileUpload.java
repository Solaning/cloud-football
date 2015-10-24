package com.kinth.football.network;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.kinth.football.config.UrlConstants;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

public class AsyncFileUpload {
	
	public AsyncFileUpload() {
		
	}

	public static String uploadFile(String filepath) {
		String rsltURL = "";
		try {
			URL url = new URL(UrlConstants.UPLOAD_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; ");
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			File file = new File(filepath);
			StringBuilder sb = new StringBuilder();
			byte[] data = sb.toString().getBytes();
			out.write(data);
			Bitmap bm = BitmapFactory.decodeFile(filepath);  
			  ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			  bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		        int options = 100;
		        // 循环判断如果压缩后图片是否大于1M,大于继续压缩
		        while (baos.toByteArray().length / 1024 >1024) {
		            // 重置baos即清空baos
		            baos.reset();
		            // 每次都减少10
		            options -= 10;
		            // 这里压缩options%，把压缩后的数据存放到baos中
		            bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
		        }
		        ByteArrayInputStream is = new ByteArrayInputStream(baos.toByteArray());			
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = is.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			is.close();
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
//				Log.i("AsyncFileUpload", url);
				Message message = handler.obtainMessage(0, url);
				handler.sendMessage(message);
			}
		}.start();
	}

	public interface FileCallback {//TODO 添加错误的回调
		public void fileUploadCallback(String fileUrl);
	}
}
