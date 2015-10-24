package com.kinth.football.listener;

import android.media.MediaRecorder;

public abstract interface RecordControlListener {
	
	public abstract void startRecording(String path);

	public abstract void cancelRecording();
	
	public abstract int stopRecording();

	public abstract boolean isRecording();

	public abstract MediaRecorder getMediaRecorder();

	public abstract String getRecordFilePath(String path);
	
	
}
