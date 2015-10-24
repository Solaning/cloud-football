package com.kinth.football.chat;

import android.os.AsyncTask;

import com.kinth.football.chat.listener.DownloadListener;
import com.kinth.football.ui.team.formation.DownloadUtil;

public class DownloadManager extends AsyncTask<String, Integer, Boolean> {
	private DownloadListener downloadListener;

	public DownloadManager(DownloadListener paramDownloadListener) {
		this.downloadListener = paramDownloadListener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		downloadListener.onStart();
	}

	@Override
	protected Boolean doInBackground(String... params) {
		// params[0]，params[1]参数的值分别是文件的网络地址和文件的保存路径
		// 实例化DownloadUtil对象，调用download()将所选媒体文件删除
		DownloadUtil downloadUtil = new DownloadUtil(params[0], params[1]);
		try {
			// 开始下载
			downloadUtil.download();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if(result){
			downloadListener.onSuccess();
		}else{
			downloadListener.onError(" ");
		}
	}
}
