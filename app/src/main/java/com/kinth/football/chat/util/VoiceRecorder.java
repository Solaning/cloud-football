package com.kinth.football.chat.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Time;

import com.kinth.football.chat.OnRecordChangeListener;
import com.kinth.football.config.Config;
import com.kinth.football.util.LogUtil;

public class VoiceRecorder {
	MediaRecorder recorder;
	static final String PREFIX = "voice";
	static final String EXTENSION = ".amr";
	private boolean isRecording = false;
	private long startTime;
	private String voiceFilePath = null;
	private String voiceFileName = null;
	private File file;
	private Handler handler;

	OnRecordChangeListener O;

	public static int MAX_RECORD_TIME = 60;

	public VoiceRecorder(Handler paramHandler) {
		this.handler = paramHandler;
	}

	public void setOnRecordChangeListener(
			OnRecordChangeListener paramOnRecordChangeListener) {
		this.O = paramOnRecordChangeListener;
	}

	public String startRecording(Context paramContext) {
		this.file = null;
		try {
			if (this.recorder != null) {
				this.recorder.release();
				this.recorder = null;
			}
			this.recorder = new MediaRecorder();
			this.recorder.setAudioSource(1);
			this.recorder.setOutputFormat(3);
			this.recorder.setAudioEncoder(1);
			this.recorder.setAudioChannels(1);
			this.recorder.setAudioSamplingRate(8000);
			this.recorder.setAudioEncodingBitRate(64);
			this.voiceFileName = getVoiceFileName();
			this.voiceFilePath = getRecordFilePath();
			this.file = new File(this.voiceFilePath);
			this.recorder.setOutputFile(this.file.getAbsolutePath());
			this.recorder.prepare();
			this.isRecording = true;
			this.recorder.start();
		} catch (IOException localIOException) {
			LogUtil.e("voice", "prepare() failed");
			localIOException.printStackTrace();
		}
		new Thread(new Runnable() {
			public void run() {
				try {
					while (VoiceRecorder.this.isRecording) {
						Message localMessage = new Message();
						localMessage.what = (VoiceRecorder.this.recorder
								.getMaxAmplitude() * 13 / 32767);
						VoiceRecorder.this.handler.sendMessage(localMessage);
						SystemClock.sleep(100L);
					}
				} catch (Exception localException) {
					localException.printStackTrace();
				}
			}
		}).start();
		this.startTime = new Date().getTime();
		LogUtil.d("voice",
				"start voice recording to file:" + this.file.getAbsolutePath());
		
		mHandler.post(mUpdateMicStatusTimer);
		
		return this.file == null ? null : this.file.getAbsolutePath();
	}

	public void cancelRecording() {
		if (this.recorder != null) {
			try {
				this.recorder.stop();
				this.recorder.release();
				this.recorder = null;
				if ((this.file != null) && (this.file.exists())
						&& (!this.file.isDirectory()))
					this.file.delete();
			} catch (IllegalStateException localIllegalStateException) {
			} catch (RuntimeException localRuntimeException) {
			}
			this.isRecording = false;
		}
	}

	public int stopRecording() {
		if (this.recorder != null) {
			this.isRecording = false;
			this.recorder.stop();
			this.recorder.release();
			this.recorder = null;
			if ((this.file != null) && (this.file.exists())
					&& (this.file.isFile()) && (this.file.length() == 0L)) {
				this.file.delete();
				return -1011;
			}
			int i = (int) (new Date().getTime() - this.startTime) / 1000;
			LogUtil.d("voice", "voice recording finished. seconds:" + i
					+ " file length:" + this.file.length());
			return i;
		}
		return 0;
	}

	protected void finalize() throws Throwable {
		super.finalize();
		if (this.recorder != null)
			this.recorder.release();
	}

	public String getVoiceFileName() {
		Time localTime = new Time();
		localTime.setToNow();
		return localTime.toString().substring(0, 15) + ".amr";
	}

	public boolean isRecording() {
		return this.isRecording;
	}

	public String getRecordFilePath() {

		// 判断文件目录是否存在，若不存在，则创建该目录
		File dir = new File(Config.VOICE_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// 判断文件是否存在，若不存在，则创建新的文件
		String filepath = Config.VOICE_DIR + this.voiceFileName;
		File file = new File(filepath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}

		return filepath;
	}
	
	private final Handler mHandler = new Handler();  
    private Runnable mUpdateMicStatusTimer = new Runnable() {  
        public void run() {  
            updateMicStatus();  
        }  
    };  
  
    /** 
     * 更新话筒状态 分贝是也就是相对响度 分贝的计算公式K=20lg(Vo/Vi) Vo当前振幅值 Vi基准值为600：我是怎么制定基准值的呢？ 当20 
     * * Math.log10(recorder.getMaxAmplitude() / Vi)==0的时候vi就是我所需要的基准值 
     * 当我不对着麦克风说任何话的时候，测试获得的recorder.getMaxAmplitude()值即为基准值。 
     * Log.i("mic_", "麦克风的基准值：" + recorder.getMaxAmplitude());前提时不对麦克风说任何话 
     */  
    private int BASE = 600;  
    private int SPACE = 100;// 间隔取样时间  
  
    private void updateMicStatus() {  
        if (recorder != null) {  
            int ratio = recorder.getMaxAmplitude() / BASE;  
            int db = 0;// 分贝  
            if (ratio > 1)  
                db = (int) (20 * Math.log10(ratio)); 
            if((db/8)<=4)
            	this.O.onVolumnChanged(db/8);
            else
            	this.O.onVolumnChanged(4);
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);  
        }  
    }  
	
	/**
     * 得到amr的时长
     *  
     * @param file
     * @return
     * @throws IOException
     */  
    public static long getAmrDuration(File file) throws IOException {  
        long duration = -1;  
        int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };  
        RandomAccessFile randomAccessFile = null;  
        try {  
            randomAccessFile = new RandomAccessFile(file, "rw");  
            long length = file.length();//文件的长度  
            int pos = 6;//设置初始位置  
            int frameCount = 0;//初始帧数  
            int packedPos = -1;  
            /////////////////////////////////////////////////////  
            byte[] datas = new byte[1];//初始数据值  
            while (pos <= length) {  
                randomAccessFile.seek(pos);  
                if (randomAccessFile.read(datas, 0, 1) != 1) {  
                    duration = length > 0 ? ((length - 6) / 650) : 0;  
                    break;  
                }  
                packedPos = (datas[0] >> 3) & 0x0F;  
                pos += packedSize[packedPos] + 1;  
                frameCount++;  
            }  
            duration += frameCount * 20;//帧数*20  
        }catch (Exception e){
        	e.printStackTrace();
        }finally {  
            if (randomAccessFile != null) {  
                randomAccessFile.close();  
            }  
        }  
        return duration/1000;  
    }   
}