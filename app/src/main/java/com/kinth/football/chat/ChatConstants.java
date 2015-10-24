package com.kinth.football.chat;

import java.io.File;

import android.os.Environment;

public class ChatConstants {
	public static final String CHAT_VOICE_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator
			+ "Chat" + File.separator + "voice";

	public static final int STATE_UNREAD = 0;
	public static final int STATE_READED = 1;
	public static final int STATUS_SEND_START = 0;	//发送开始，到达对方的则是未读消息，因为都是0
	public static final int STATUS_SEND_SUCCESS = 1;
	public static final int STATUS_SEND_FAIL = 2;
	public static final int STATUS_SEND_RECEIVERED = 3;
	
	/**
	 * 语音消息状态：未播放
	 */
	public static final int STATUS_UNPLAY = 4;
	/**
	 * 语音消息状态：已播放
	 */
	public static final int STATUS_PLAYED = 5;

	/**
	 * �ҵ�ͷ�񱣴�Ŀ¼
	 */
	public static String MyAvatarDir = "/sdcard/Football/avatar/";
	/**
	 * 选图片相关的请求码
	 */
	public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;//拍照选择图片
	public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;//本地图库选择图片
	public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;//对图片进行裁剪

	public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;// ����
	public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;// ����ͼƬ
	public static final int REQUESTCODE_TAKE_LOCATION = 0x000003;// λ��
	public static final String EXTRA_STRING = "extra_string";

	public static final String ACTION_REGISTER_SUCCESS_FINISH = "register.success.finish";// ע��ɹ�֮���½ҳ���˳�

	public static final String MSG_TYPE_TEXT = "text";
	public static final String MSG_TYPE_IMAGE = "photo";
	public static final String MSG_TYPE_VIDEO = "video";
	public static final String MSG_TYPE_AUDIO = "audio";

	public static final int TYPE_TEXT = 1;
	public static final int TYPE_IMAGE = 2;
	public static final int TYPE_VIDEO = 3;
	public static final int TYPE_VOICE = 4;
	
	/**
	 * Intent参数键名:聊天对象
	 */
	public static final String INTENT_USER_BEAN = "intent_user_bean";
	
	/**
	 * Intent参数键名:消息对象
	 */
	public static final String INTENT_CHATMSG_BEAN = "intent_chatmsg_bean";
	
	/**
	 * Intent参数键名:文本消息
	 */
	public static final String INTENT_TEXT_MESSAGE = "intent_text_message";
	
	/**
	 * Intent参数键名:删除消息位置
	 */
	public static final String INTENT_DEL_POSITION = "intent_del_position";
	
	/**
	 * Intent参数键名:聊天类型标识
	 */
	public static final String TAG = "tag";
	
	/**
	 * 聊天类型标识:单聊
	 */
	public static final int TAG_PRIVATE = 0;
	
	/**
	 * 聊天类型标识:群聊
	 */
	public static final int TAG_GROUP = 1;
	
	/**
	 * 聊天类型标识:球队消息
	 */
	public static final int TAG_TEAM = 2;
	
}
