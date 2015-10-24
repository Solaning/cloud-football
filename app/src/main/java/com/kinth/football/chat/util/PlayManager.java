package com.kinth.football.chat.util;

import java.io.File;
import java.io.IOException;

import com.kinth.football.chat.listener.OnPlayChangeListener;
import com.kinth.football.manager.UserManager;
import com.kinth.football.util.LogUtil;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class PlayManager implements MediaPlayer.OnCompletionListener,
		PlayControlListener {
	private MediaPlayer mMediaPlayer;
	private Context context;
	private static volatile PlayManager playManager;
	private static Object INSTANCE_LOCK = new Object();
	private boolean isPlaying = false;
	private OnPlayChangeListener onPlayChangeListener;

	public static PlayManager getInstance(Context context) {
		if (playManager == null)
			synchronized (INSTANCE_LOCK) {
				if (playManager == null)
					playManager = new PlayManager();
				playManager.init(context);
			}
		return playManager;
	}

	public void setOnPlayChangeListener(
			OnPlayChangeListener paramOnPlayChangeListener) {
		this.onPlayChangeListener = paramOnPlayChangeListener;
	}

	public void init(Context context) {
		this.context = context;
		UserManager.getInstance(context);
	}

	public void playRecording(String path, boolean isOpen) {
		File file = new File(path);
		if (!file.exists())
			return;
		if (this.mMediaPlayer == null) {
			this.mMediaPlayer = new MediaPlayer();
			this.mMediaPlayer.setOnErrorListener(new PlayerErrorListener(
							this));
		} else {
			this.mMediaPlayer.stop();
			this.mMediaPlayer.reset();
		}
		AudioManager audioManager = (AudioManager) this.context
				.getSystemService("audio");
		if (isOpen) {
			audioManager.setMode(0);
			audioManager.setSpeakerphoneOn(true);
			this.mMediaPlayer.setAudioStreamType(2);
		} else {
			audioManager.setSpeakerphoneOn(false);
			audioManager.setMode(2);
			this.mMediaPlayer.setAudioStreamType(0);
		}
		try {
			this.mMediaPlayer.setDataSource(path);
			this.mMediaPlayer.prepare();
			this.mMediaPlayer.setOnCompletionListener(this);
			this.mMediaPlayer.seekTo(0);
			this.mMediaPlayer.start();
			this.isPlaying = true;
			if (this.onPlayChangeListener != null) {
				this.onPlayChangeListener.onPlayStart();
				return;
			}
		} catch (IOException localIOException) {
			this.mMediaPlayer.release();
			this.mMediaPlayer = null;
		}
	}

	public void stopPlayback() {
		if (this.mMediaPlayer != null)
			onStop();
	}

	public boolean isPlaying() {
		return this.isPlaying;
	}

	public int getPlaybackDuration() {
		int i = 0;
		if ((this.mMediaPlayer != null) && (this.mMediaPlayer.isPlaying()))
			i = this.mMediaPlayer.getDuration();
		return i;
	}

	public MediaPlayer getMediaPlayer() {
		return this.mMediaPlayer;
	}

	public void onCompletion(MediaPlayer paramMediaPlayer) {
		onStop();
	}

	private void onStop() {
		if (this.onPlayChangeListener != null)
			this.onPlayChangeListener.onPlayStop();
		this.mMediaPlayer.stop();
		this.mMediaPlayer.release();
		this.mMediaPlayer = null;
		this.isPlaying = false;
	}
	
	
	public class PlayerErrorListener
	  implements MediaPlayer.OnErrorListener
	{
	  public PlayerErrorListener(PlayManager playManager)
	  {
	  }

	  public boolean onError(MediaPlayer paramMediaPlayer, int what, int paramInt2)
	  {
		  String error = "";
	    switch (what)
	    {
	    case 1:
	    	error = "MEDIA_ERROR_UNKNOWN";
	      break;
	    case 100:
	    	error = "MEDIA_ERROR_SERVER_DIED";
	      break;
	    default:
	    	error = Integer.toString(what);
	    }
	    LogUtil.i("voice", String.format("MediaPlayer error occured: %s:%d", new Object[] { error, Integer.valueOf(paramInt2) }));
	    return false;
	  }
	}
}