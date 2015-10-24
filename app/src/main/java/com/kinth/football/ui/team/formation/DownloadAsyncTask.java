package com.kinth.football.ui.team.formation;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

public class DownloadAsyncTask extends AsyncTask<String, Integer, String>
{
	public DownloadAsyncTask(Context context) {
		super();
	}

	@Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        FormationDetailActivity.pbLoad.setVisibility(View.VISIBLE);
    }

    @Override
	protected String doInBackground(String... params) {
		// TODO 自动生成的方法存根
    	//params[0]，params[1]参数的值分别是文件的网络地址和文件的保存路径
    	//实例化DownloadUtil对象，调用download()将所选媒体文件删除
    	DownloadUtil downloadUtil = new DownloadUtil(params[0],params[1]);
		try
		{
			// 开始下载
			downloadUtil.download();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		} 
		return params[0];
	}

	@Override
    protected void onProgressUpdate(Integer... values)
    {
        super.onProgressUpdate(values);
    }
    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        FormationDetailActivity.pbLoad.setVisibility(View.GONE);
    }
    
}