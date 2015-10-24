package com.kinth.football;

import java.io.File;
import java.util.ArrayList;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.kinth.football.bean.Parameters;
import com.kinth.football.bean.match.MatchInfo;
import com.kinth.football.chat.MyChatManager;
import com.kinth.football.dao.DaoMaster;
import com.kinth.football.dao.DaoMaster.DevOpenHelper;
import com.kinth.football.dao.DaoSession;
import com.kinth.football.manager.UserManager;
import com.kinth.football.threadpool.TaskManager;
import com.kinth.football.util.SharePreferenceUtil;
import com.kinth.football.util.UtilFunc;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * 自定义全局Applcation类
 * @ClassName: CustomApplcation
 * @author Sola
 * @date 2015-02-05
 */
public class CustomApplcation extends Application {
	private static final String TAG = "CustomApplcation";
	public static CustomApplcation mInstance;
	public LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;
//	public static BmobGeoPoint lastPoint = null;// 上一次定位到的经纬度
	MyChatManager myChatManager;
	private static DaoMaster daoMaster;  //greenDao数据库
    private static DaoSession daoSession; 
	private static ArrayList<MatchInfo> matchInfoOfCreated = new ArrayList<MatchInfo>();//全局的报名比赛列表
	private static ArrayList<MatchInfo> matchInfoOfPending = new ArrayList<MatchInfo>();//全局的
	private Parameters parameters;//通信参数
	
	/** 
	 * Global request queue for Volley 
	 * @2014-11-10
     */  
    private RequestQueue mRequestQueue;  
    
    /** 
     * @return The Volley Request queue, the queue will be created if it is null 
     */  
    public RequestQueue getRequestQueue() {  
        // lazy initialize the request queue, the queue instance will be  
        // created when it is accessed for the first time  
        if (mRequestQueue == null) {  
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());  
        }  
        return mRequestQueue;  
    } 
    
    /** 
     * Adds the specified request to the global queue, if tag is specified 
     * then it is used else Default TAG is used. 
     *  
     * @param req 
     * @param tag 
     */  
    public <T> void addToRequestQueue(Request<T> req, String tag) {  
        // set the default tag if tag is empty  
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);  
  
        VolleyLog.d("Adding request to queue: %s", req.getUrl());  
  
        getRequestQueue().add(req);  
    }  
  
    /** 
     * Adds the specified request to the global queue using the Default TAG. 
     *  
     * @param req 
     * @param tag 
     */  
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty  
        req.setTag(TAG);  

        getRequestQueue().add(req);  
    }  
  
    /** 
     * Cancels all pending requests by the specified TAG, it is important 
     * to specify a TAG so that the pending/ongoing requests can be cancelled. 
     *  
     * @param tag 
     */  
    public void cancelPendingRequests(Object tag) { 
        if (mRequestQueue != null) {  
            mRequestQueue.cancelAll(tag);  
        }  
    } 
	
	@Override
	public void onCreate() {
		super.onCreate();
		// 是否开启debug模式--默认开启状态
//		BmobChat.DEBUG_MODE = true;
		if(mInstance == null){
			mInstance = this;
		}
		init();
	}
	
	private void init() {
		mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		initImageLoader(getApplicationContext());
		
		myChatManager = MyChatManager.getInstance(this);
//		initBaidu();
		initParas();
	}

    private Parameters initParas() {
		if(parameters == null){
			parameters = UtilFunc.getCommonParameters(this); 
		}
		return parameters;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		TaskManager.getInstance().setStop();
	}

	public static ArrayList<MatchInfo> getMatchInfoOfCreated() {
		return matchInfoOfCreated;
	}

	public static void setMatchInfoOfCreated(ArrayList<MatchInfo> matchInfoOfCreated) {
		CustomApplcation.matchInfoOfCreated = matchInfoOfCreated;
	}

	public static ArrayList<MatchInfo> getMatchInfoOfPending() {
		return matchInfoOfPending;
	}

	public static void setMatchInfoOfPending(ArrayList<MatchInfo> matchInfoOfPending) {
		CustomApplcation.matchInfoOfPending = matchInfoOfPending;
	}

	public Parameters getParameters() {
		if(parameters == null){
			parameters = UtilFunc.getCommonParameters(this); 
		}
		return parameters;
	}

	/** 
     * 取得DaoMaster 
     *  
     * @param context 
     * @return 
     */  
    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            DevOpenHelper helper = new DaoMaster.DevOpenHelper(mInstance, UserManager.getInstance(context).getCurrentUser().getPlayer().getUuid(), null); //使用用户id来做数据库
            daoMaster = new DaoMaster(helper.getWritableDatabase());  
        }  
        return daoMaster;  
    }  
      
    /** 
     * 取得DaoSession 
     *  
     * @param context 
     * @return 
     */  
    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(mInstance);  
            }  
            daoSession = daoMaster.newSession();  
        }  
        return daoSession;  
    }  
    
	/**
	 * 初始化百度相关sdk initBaidumap
	 * @Title: initBaidumap
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initBaidu() {
		// 初始化地图Sdk
		SDKInitializer.initialize(this);
		// 初始化定位sdk
		initBaiduLocClient();
	}

	/**
	 * 初始化百度定位sdk
	 * @Title: initBaiduLocClient
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initBaiduLocClient() {
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
	}
	
	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			double latitude = location.getLatitude();
			double longtitude = location.getLongitude();
//			if (lastPoint != null) {
//				if (lastPoint.getLatitude() == location.getLatitude()
//						&& lastPoint.getLongitude() == location.getLongitude()) {
//					LogUtil.i("两次获取坐标相同");// 若两次请求获取到的地理位置坐标是相同的，则不再定位
//					mLocationClient.stop();
//					return;
//				}
//			}
//			lastPoint = new BmobGeoPoint(longtitude, latitude);
		}
	}

	/** 初始化ImageLoader */
	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context, "Kinth/Football/cache/images");// 获取到缓存的目录地址 /Kinth/Football/cache/images
		DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.image_download_loading_icon)
			.showImageForEmptyUri(R.drawable.image_download_loading_icon)
			.showImageOnFail(R.drawable.image_download_loading_icon)
			.cacheInMemory(true).cacheOnDisk(true)
			.build();
		// 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				// 线程池内加载的数量
				.threadPoolSize(4).threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(new WeakMemoryCache())
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.diskCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
				// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
//				.writeDebugLogs() // Remove for release app
				.defaultDisplayImageOptions(options)
				.build();
		com.nostra13.universalimageloader.utils.L.writeDebugLogs(false);
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);// 全局初始化此配置
	}
	
	public static CustomApplcation getInstance() {
		return mInstance;
	}

	// 单例模式，才能及时返回数据
	SharePreferenceUtil mSpUtil;
	public static final String PREFERENCE_NAME = "_sharedinfo";

	public synchronized SharePreferenceUtil getSpUtil() {
		if (mSpUtil == null) {
//			String currentId = UserManager.getInstance(
//					getApplicationContext()).getCurrentUserObjectId();
//			String sharedName = currentId + PREFERENCE_NAME;
//			mSpUtil = new SharePreferenceUtil(this, sharedName);
		}
		return mSpUtil;
	}

	NotificationManager mNotificationManager;

	public NotificationManager getNotificationManager() {
		if (mNotificationManager == null)
			mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		return mNotificationManager;
	}

	MediaPlayer mMediaPlayer;

	public synchronized MediaPlayer getMediaPlayer() {
		if (mMediaPlayer == null)
			mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
		return mMediaPlayer;
	}
	
	public final String PREF_LONGTITUDE = "longtitude";// 经度
	private String longtitude = "";

	/**
	 * 获取经度
	 * 
	 * @return
	 */
	public String getLongtitude() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		longtitude = preferences.getString(PREF_LONGTITUDE, "");
		return longtitude;
	}

	/**
	 * 设置经度
	 * @param pwd
	 */
	public void setLongtitude(String lon) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(PREF_LONGTITUDE, lon).commit()) {
			longtitude = lon;
		}
	}

	public final String PREF_LATITUDE = "latitude";// 经度
	private String latitude = "";

	/**
	 * 获取纬度
	 * 
	 * @return
	 */
	public String getLatitude() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		latitude = preferences.getString(PREF_LATITUDE, "");
		return latitude;
	}

	/**
	 * 设置维度
	 * @param pwd
	 */
	public void setLatitude(String lat) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(PREF_LATITUDE, lat).commit()) {
			latitude = lat;
		}
	}
	
	/**
	 * 退出登录,清空缓存数据
	 */
	public void logout() {
		daoMaster = null; //greenDao数据库使用
	    daoSession = null; 
	    matchInfoOfCreated.clear();
	    matchInfoOfPending.clear();
		setLatitude(null);
		setLongtitude(null);
		cancelPendingRequests(TAG);//取消所有的网络请求
	}

}
