package com.kinth.football.chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;

import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.chat.bean.ChatMsg;
import com.kinth.football.chat.db.ChatDBManager;
import com.kinth.football.chat.listener.OnPlayChangeListener;
import com.kinth.football.chat.util.MD5Util;
import com.kinth.football.config.Config;
import com.kinth.football.manager.UserManager;

/**
 * 
 * @ClassName: NewRecordPlayClickListener
 * @Description: TODO
 * @author smile
 * @date 2014-7-3 11:05:06
 */
public class NewRecordPlayClickListener implements View.OnClickListener {

	ChatMsg message;
	ImageView iv_voice;
	ImageView iv_voice_tips;
	private AnimationDrawable anim = null;
	Context context;
	String currentObjectId = "";
	MediaPlayer mediaPlayer = null;
	public static boolean isPlaying = false;
	public static NewRecordPlayClickListener currentPlayListener = null;
	static ChatMsg currentMsg = null;// �������������ͬ�����Ĳ���

	UserManager userManager;
	private OnPlayChangeListener listener;

	public NewRecordPlayClickListener(Context context, ChatMsg msg,
			ImageView voice, ImageView voiceTips, OnPlayChangeListener listener) {
		this.iv_voice = voice;
		this.iv_voice_tips = voiceTips;
		this.message = msg;
		this.context = context;
		this.listener = listener;
		currentMsg = msg;
		currentPlayListener = this;
		currentObjectId = UserManager.getInstance(context)
				.getCurrentUserPhone();
		userManager = UserManager.getInstance(context);
	}

	/**
	 * ��������
	 * 
	 * @Title: playVoice
	 * @Description: TODO
	 * @param @param filePath
	 * @param @param isUseSpeaker
	 * @return void
	 * @throws
	 */
	public void startPlayRecord(String filePath, boolean isUseSpeaker) {
		if (!(new File(filePath).exists())) {
			return;
		}
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		mediaPlayer = new MediaPlayer();
		if (isUseSpeaker) {
			audioManager.setMode(AudioManager.MODE_NORMAL);
			audioManager.setSpeakerphoneOn(true);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
		} else {
			audioManager.setSpeakerphoneOn(false);// �ر�������
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
		}
		
		while (true) {
			try {
				mediaPlayer.reset();
				FileInputStream fis = new FileInputStream(new File(filePath));
				mediaPlayer.setDataSource(fis.getFD());
				mediaPlayer.prepare();
				fis.close();
				break;
			} catch (IllegalArgumentException e) {
			} catch (IllegalStateException e) {
			} catch (IOException e) {
			}
		}
		
		isPlaying = true;
		currentMsg = message;
		mediaPlayer.start();
		startRecordAnimation();
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				stopPlayRecord();
				listener.onPlayStop();
			}

		});
        currentPlayListener = this;
	}

	/**
	 * ֹͣ����
	 * 
	 * @Title: stopPlayRecord
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void stopPlayRecord() {
		stopRecordAnimation();
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		isPlaying = false;
	}

	/**
	 * �������Ŷ���
	 * 
	 * @Title: startRecordAnimation
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void startRecordAnimation() {
		if (message.getBelongId().equals(currentObjectId)) {
			iv_voice.setImageResource(R.anim.anim_chat_voice_right);
		} else {
			iv_voice.setImageResource(R.anim.anim_chat_voice_left);
		}
		anim = (AnimationDrawable) iv_voice.getDrawable();
		anim.start();
	}

	/**
	 * ֹͣ���Ŷ���
	 * 
	 * @Title: stopRecordAnimation
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void stopRecordAnimation() {
		if (message.getBelongId().equals(currentObjectId)) {
			iv_voice.setImageResource(R.drawable.voice_left3);
		} else {
			iv_voice.setImageResource(R.drawable.voice_right3);
		}
		if (anim != null) {
			anim.stop();
		}
	}

	@Override
	public void onClick(View arg0) {
		if (isPlaying) { 
			currentPlayListener.stopPlayRecord();
			if (currentMsg != null
					&& currentMsg.hashCode() == message.hashCode()) {
				currentMsg = null;
				return;
			}
		}
		try {
			String localPath = Config.VOICE_DIR
					+ MD5Util.getMD5(message.getContent().trim()) + ".amr";
			startPlayRecord(localPath, true);
		} catch (NoSuchAlgorithmException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		ChatDBManager.create(context).updateTargetMsgStatus(ChatConstants.STATE_READED, currentMsg.getConversationId(), currentMsg.getMsgTime());
		if(iv_voice_tips!=null)
			iv_voice_tips.setVisibility(View.GONE);
	}

	public String getDownLoadFilePath(ChatMsg msg) {
		String accountDir = userManager.getCurrentUserPhone();
		File dir = new File(ChatConstants.CHAT_VOICE_DIR + File.separator
				+ accountDir + File.separator + msg.getBelongId());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// �ڵ�ǰ�û���Ŀ¼������¼���ļ�
		File audioFile = new File(dir.getAbsolutePath() + File.separator
				+ msg.getMsgTime() + ".amr");
		try {
			if (!audioFile.exists()) {
				audioFile.createNewFile();
			}
		} catch (IOException e) {
		}
		return audioFile.getAbsolutePath();
	}

}