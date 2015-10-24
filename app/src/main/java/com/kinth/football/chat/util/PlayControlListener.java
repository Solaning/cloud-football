package com.kinth.football.chat.util;

import android.media.MediaPlayer;

public abstract interface PlayControlListener {
	  public abstract void playRecording(String paramString, boolean paramBoolean);

	  public abstract void stopPlayback();

	  public abstract boolean isPlaying();

	  public abstract int getPlaybackDuration();

	  public abstract MediaPlayer getMediaPlayer();
}
