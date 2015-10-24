package com.kinth.football.chat.util;

import java.util.Map;

import android.text.TextUtils;

import com.kinth.football.bean.PickedImage;
import com.kinth.football.bean.UploadImage;
import com.kinth.football.chat.util.AsyncRecordFileUpload.FileCallback;

/**
 * 包装后的上传图片
 * @author Sola
 *
 */
public abstract class AsyncUploadRecord {
	private static final int UPLOAD_RETRY_LIMIT = 3;//失败重试次数
	private boolean uploading;
	/**
	 * 上传图片
	 * @param uploadMap
	 * @param item
	 */
	public void upload2(final Map<String, UploadImage> uploadMap, final PickedImage item){
		uploading = true;
		AsyncRecordFileUpload.asynFileUploadToServer(item.getPath(), new FileCallback() {

			@Override
			public void fileUploadCallback(String fileUrl) {
				
				if (TextUtils.isEmpty(fileUrl)) {   //上传失败
		        	if(!uploading){
		        		return;
		        	}
		        	uploadMap.get(item.getPath()).setSucceed(false);
		        	uploadMap.get(item.getPath()).autoAddFailTime();//失败次数自动加1
		        	if(uploadMap.get(item.getPath()).getFailTime() < UPLOAD_RETRY_LIMIT){//重试
		        		upload2(uploadMap,item);
		        	}else{
		        		uploading = false;//失败了
		        		onUploadFailed();
		        	}
				}else {
		        	if(!uploading){
		        		return;
		        	}
		        	uploadMap.get(item.getPath()).setUrl(fileUrl);
		        	uploadMap.get(item.getPath()).setSucceed(true);
		        	boolean hasDone = true;//判断图片是否都上传完成
		        	for (Map.Entry<String, UploadImage> entry : uploadMap.entrySet()) {
		        		if(entry.getValue().isSucceed()){
		        			continue;
		        		}else{//还有一个没有传完
		        			hasDone = false;
		        			break;
		        		}
		        	}
		        	if(hasDone){
		        		onUploadSuccess(uploadMap);
		        	}
				}
			}
			
		});
	}
	
	public abstract void onUploadSuccess(Map<String, UploadImage> uploadMap);
	public abstract void onUploadFailed();

}
