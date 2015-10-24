package com.kinth.football.ui.team.formation;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.kinth.football.chat.listener.DownloadListener;
import com.kinth.football.util.FileUtil;

public class DownloadUtil {
	// 定义下载资源的路径
	private String filePath;
	// 指定所下载的文件的保存位置
	private String savePath;
	// 定义下载的线程对象
	private DownloadThread thread;
	// 定义下载的文件的总大小
	private long fileSize;
	
	public DownloadUtil(String filePath, String savePath) {
		this.filePath = filePath;
		this.savePath = savePath;
	}
 
	public void download() throws Exception {
		
		File file = new File(savePath);
		if(!file.exists()){
			FileUtil.createFile(file, true);// 创建文件
		}
		fileSize = file.length();
		
		//创建RandomAccessFile实例，指定读写权限
		RandomAccessFile rafile = new RandomAccessFile(savePath, "rw");
		//将文件记录指针定位到0位置
		rafile.seek(0);
		
		thread = new DownloadThread(rafile);
		// 启动下载线程
		thread.start();
	}

	// 获取下载的完成百分比
	public double getCompleteRate() {
		//统计该线程已经下载的总大小，即线程的大小
		int sumSize = thread.length;
		//通过计算出线程大小占文件总大小的比重计算出狭隘的进度
		double completeRate = sumSize * 1.0 / fileSize;
		
		return completeRate;
	}

	private class DownloadThread extends Thread {
		
		private RandomAccessFile raFile;
		public int length = 0;

		public DownloadThread(RandomAccessFile raFile) {
			this.raFile = raFile;
		}

		@Override
		public void run() {
			//创建HttpClient对象
            HttpClient httpClient = new DefaultHttpClient();
            //创建HttpGet对象，用于发送GET请求
            HttpGet httpGet = new HttpGet(filePath);
            try
            {
            	//发送GET请求
                HttpResponse httpResponse = httpClient.execute(httpGet);
                //获取HttpEntity对象，该对象包装了服务器的响应内容
                HttpEntity httpEntity = httpResponse.getEntity();
                
                if(httpEntity != null && httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                {
	                //获取实体内容，将内容写入输入流中
					InputStream in = httpEntity.getContent();
					//设置每次读取1024个字节
					byte[] data = new byte[1024];
					int hasRead = 0;
					// 读取网络数据，并写入本地文件
					while ((hasRead = in.read(data)) != -1) {
						raFile.write(data, 0, hasRead);
						// 累计该线程下载的总大小
						length += hasRead;
					}
					raFile.close();
					in.close(); 
                }
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
}
