package com.kinth.football.bean;

/**
 * 上传图片的实体
 * @author Sola
 *
 */
public class UploadImage {
	private String rawPath;//本地路径
	private String url;
	private String tag;//标签
	private boolean isSucceed;
	private int failTime;//失败次数
	
	public UploadImage(String rawPath, String tag) {
		super();
		this.rawPath = rawPath;
		this.tag = tag;
	}

	public String getRawPath() {
		return rawPath;
	}

	public void setRawPath(String rawPath) {
		this.rawPath = rawPath;
	}
	
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isSucceed() {
		return isSucceed;
	}

	public void setSucceed(boolean isSucceed) {
		this.isSucceed = isSucceed;
	}

	public int getFailTime() {
		return failTime;
	}

	public void setFailTime(int failTime) {
		this.failTime = failTime;
	}
	
	/**
	 * 失败次数自动加
	 */
	public void autoAddFailTime(){
		this.failTime++;
	}

}
