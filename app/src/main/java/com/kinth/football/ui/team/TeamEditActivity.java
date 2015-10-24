package com.kinth.football.ui.team;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.bean.AddressBean;
import com.kinth.football.bean.City;
import com.kinth.football.bean.PickedImage;
import com.kinth.football.bean.Province;
import com.kinth.football.bean.RegionBean;
import com.kinth.football.bean.TeamInfoComplete;
import com.kinth.football.bean.TeamRequest;
import com.kinth.football.bean.UploadImage;
import com.kinth.football.bean.match.PlayerInTeam;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.config.JConstants;
import com.kinth.football.config.PlayerTypeEnum;
import com.kinth.football.dao.CityDao;
import com.kinth.football.dao.Player;
import com.kinth.football.dao.PlayerDao;
import com.kinth.football.dao.ProvinceDao;
import com.kinth.football.dao.RegionDao;
import com.kinth.football.dao.TeamDao;
import com.kinth.football.eventbus.bean.ModifyTeamAlternetJerseyEvent;
import com.kinth.football.eventbus.bean.ModifyTeamBadgeEvent;
import com.kinth.football.eventbus.bean.ModifyTeamCityEvent;
import com.kinth.football.eventbus.bean.ModifyTeamDescriptionEvent;
import com.kinth.football.eventbus.bean.ModifyTeamFamilyPhotoEvent;
import com.kinth.football.eventbus.bean.ModifyTeamFirstCaptainEvent;
import com.kinth.football.eventbus.bean.ModifyTeamHomeJerseyEvent;
import com.kinth.football.eventbus.bean.ModifyTeamNameEvent;
import com.kinth.football.eventbus.bean.ModifyTeamRegionEvent;
import com.kinth.football.eventbus.bean.ModifyTeamRoadJerseyEvent;
import com.kinth.football.eventbus.bean.ModifyTeamSecondCaptainEvent;
import com.kinth.football.eventbus.bean.ModifyTeamSloganEvent;
import com.kinth.football.eventbus.bean.ModifyTeamThirdCaptainEvent;
import com.kinth.football.gallery.CropImageActivity;
import com.kinth.football.gallery.CustomGalleryBean;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.AsyncUploadImage;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.DateUtil;
import com.kinth.football.util.DialogUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.FileUtil;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.view.RoundImageView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;
import eu.inmite.android.lib.dialogs.IListDialogListener;
import eu.inmite.android.lib.dialogs.ListDialogFragment;

/**
 * 球队信息编辑页面--注意修改队长时的权限判断
 * 
 * @author Sola
 */
@ContentView(R.layout.activity_team_info)
public class TeamEditActivity extends BaseActivity implements
		IListDialogListener {
	public static final String INTENT_TEAM_INFO_BEAN = "INTENT_TEAM_INFO_BEAN";// intent中传递的球队数据信息
	public static final String INTENT_NEET_TO_RETURN_BEAN = "INTENT_NEET_TO_RETURN_BEAN";//是否需要返回bean给对方,true需要回传，false不需要回传
	
	public static final int REQUEST_CODE_MODIFY_TEAM_CITY = 9007; // 修改球队地区的请求码
	public static final int REQUEST_CODE_MODIFY_TEAM_REGION = 9008; // 修改球队地区的请求码

	private static final String TAG_FAMILY_PHOTO = "TAG_FAMILY_PHOTO";// 全家福标签
	private static final String TAG_BADGE = "TAG_BADGE";// 队徽标签
	
	public static final int FirstCaptain = 1;
	public static final int SecondCaptain = 2;
	public static final int ThirdCaptain = 3;
	
	private int needToRefreshCount;// 是否需要联网刷新数据的记数
	private boolean isNeedToReturnBean = false;//是否需要回传
	private RadioGroup group;
	private int selectLocation = -1;
	private int currentWhichCaptain = 0;//当前是哪个队长 1为第一队长，2为第二队长，3为教练
	
	private TeamInfoComplete teamInfoComplete;
	private Dialog dialog;
	private ProgressDialog proDialog;
	
	private int flag;
	private ProvinceDao proDao;
	private CityDao cityDao;
	
	@ViewInject(R.id.scrollview)
	private ScrollView scrollview;
	
	@ViewInject(R.id.nav_left)
	private ImageButton back;

	@ViewInject(R.id.nav_title)
	private TextView title;

	@ViewInject(R.id.iv_family_photo)
	private ImageView familyPhoto;// 全家福

	@ViewInject(R.id.iv_badge)
	private ImageView badge;// 队徽

	@ViewInject(R.id.llt_team_name)
	private LinearLayout teamName;//球队名称

	@ViewInject(R.id.tv_team_name)
	private TextView teamNameText;// 球队名称文本

	@ViewInject(R.id.llt_team_match_manage)
	private LinearLayout matchManage;// 比赛管理

	@ViewInject(R.id.llt_first_captain)
	private LinearLayout firstCaptain;// 第一队长

	@ViewInject(R.id.first_caption_name)// 第一队长名称
	private TextView first_caption_name;

	@ViewInject(R.id.iv_first_captain)
	private RoundImageView firstCaptainIcon;// 第一队长头像

	@ViewInject(R.id.llt_second_captain)
	private LinearLayout secondCaptain;// 队副

	@ViewInject(R.id.second_caption_name)
	private TextView second_caption_name; // 队副名称

	@ViewInject(R.id.iv_second_captain)
	private RoundImageView secondCaptainIcon;// 队副头像

	@ViewInject(R.id.rel_third_caption)//教练
	private RelativeLayout rel_third_caption;
	
	@ViewInject(R.id.iv_third_caption)//教练头像
	private RoundImageView third_captionIcon;
	
	@ViewInject(R.id.coach_name)//教练名字（当没有的时候，隐藏）
	private TextView coach_name;
	
	@ViewInject(R.id.llt_team_city) // 城市
	private RelativeLayout llt_team_city;

	@ViewInject(R.id.tv_team_city) // 城市文本
	private TextView tv_team_city;

	@ViewInject(R.id.llt_team_region)
	private RelativeLayout teamRegion;// 区/县

	@ViewInject(R.id.tv_team_region)
	private TextView teamRegionText;// 区/县文本

	@ViewInject(R.id.llt_team_founded_time)
	private LinearLayout teamFoundedTime;// 成立时间

	@ViewInject(R.id.tv_team_founded_time)
	private TextView teamFoundedTimeText;// 成立时间文本

	@ViewInject(R.id.llt_team_description)
	private LinearLayout teamDescription;// 球队介绍

	@ViewInject(R.id.tv_team_description)
	private TextView teamDescriptionText;// 球队介绍文本

	@ViewInject(R.id.llt_team_slogan)
	private LinearLayout teamSlogan;// 球队口号

	@ViewInject(R.id.tv_team_slogan)
	private TextView teamSloganText;// 球队口号文本
	
	@ViewInject(R.id.btn_delete_team)
	private Button btnDeleteTeam;// 删除球队--第一队长可见
	
	@OnClick(R.id.btn_delete_team)
	public void fun_20(View v) {
		//TODO
	}
	
	@ViewInject(R.id.rel_select_jersey) 
	private RelativeLayout rel_select_jersey;

	@OnClick(R.id.nav_left)
	public void fun_1(View v) {
		back();
	}

	@OnClick(R.id.l_team_allfamily)
	public void fun_2(View v) {// 全家福改图片
		if (teamInfoComplete == null || teamInfoComplete.getTeam() == null) {
			return;
		}
		flag = 1;
		ListDialogFragment.createBuilder(this, getSupportFragmentManager())
					.setTitle("更换全家福").setItems(new String[] { "拍照", "选择相册" })
					.hideDefaultButton(true).show();
	}

	@OnClick(R.id.l_team_brage)
	public void fun_3(View v) {// 队徽改图片
		if (teamInfoComplete == null || teamInfoComplete.getTeam() == null) {
			return;
		}
		flag = 2;
		ListDialogFragment.createBuilder(this, getSupportFragmentManager())
					.setTitle("更换队徽").setItems(new String[] { "拍照", "选择相册" })
					.hideDefaultButton(true).show();
	}

	@OnClick(R.id.llt_first_captain)
	public void fun_8(View v) { // 指定第一队长
		if (teamInfoComplete == null || teamInfoComplete.getTeam() == null) {
			return;
		}
		switch(currentWhichCaptain){
		case 1:
			ShowTeamMemDialog(teamInfoComplete.getPlayers(),
						teamInfoComplete.getTeam().getFirstCaptainUuid(), FirstCaptain);
			break;
		case 2:
		case 3:
		case 0:
		default:
			ShowToast("您不是队长，没有权限进行任命");
			break;
		}
	}

	@OnClick(R.id.llt_second_captain)
	public void fun_9(View v) {// 指定第二队长
		if (teamInfoComplete == null || teamInfoComplete.getTeam() == null) {
			return;
		}
		switch(currentWhichCaptain){
		case 1:
			ShowTeamMemDialog(teamInfoComplete.getPlayers(),
					teamInfoComplete.getTeam().getSecondCaptainUuid(), SecondCaptain);
			break;
		case 2:
		case 3:
		case 0:
		default:
			ShowToast("您不是队长，没有权限进行任命");
			break;
		}
	}
	
	@OnClick(R.id.rel_third_caption)   //指定教练
	public void fun_18(View v){
		if(teamInfoComplete == null || teamInfoComplete.getTeam() == null){
			return;
		}
		switch(currentWhichCaptain){
		case 1:
			ShowTeamMemDialog(teamInfoComplete.getPlayers(),
					teamInfoComplete.getTeam().getThirdCaptainUuid(), ThirdCaptain);
			break;
		case 2:
		case 3:
		case 0:
		default:
			ShowToast("您不是队长，没有权限进行任命");
			break;
		}
	}
	
	@OnClick(R.id.rel_select_jersey) //设置队服
	public void fun_19(View v){
		if(teamInfoComplete == null || teamInfoComplete.getTeam() == null){
			return;
		}
		Intent intent = new Intent(mContext, TeamJerseyActivity.class);
		intent.putExtra(TeamInfoActivity.INTENT_TEAM_INFO_BEAN, teamInfoComplete.getTeam());
		startActivity(intent);
	}

	@OnClick(R.id.llt_team_city)
	public void fun_13(View v) {// 修改城市
		if (teamInfoComplete == null || teamInfoComplete.getTeam() == null) {
			return;
		}
		Intent intent = new Intent(mContext, SelectAddressActivity.class);
		startActivityForResult(intent, REQUEST_CODE_MODIFY_TEAM_CITY);
	}
	
	@OnClick(R.id.llt_team_region)  //修改区县
	public void fun_17(View v){
		if (teamInfoComplete == null || teamInfoComplete.getTeam() == null) {
			return;
		}
		Intent intent = new Intent(mContext, RegionListActivity.class);
		intent.putExtra(CreateTeamActivity.REGION_SELECT, teamInfoComplete.getTeam().getCityId());
		startActivityForResult(intent, REQUEST_CODE_MODIFY_TEAM_REGION);
	}

	@OnClick(R.id.llt_team_description)
	public void fun_14(View v) {// 修改介绍
		if (teamInfoComplete == null || teamInfoComplete.getTeam() == null) {
			return;
		}
		Intent intent = new Intent(mContext,
					ModifyTeamDescriptionActivity.class);
		intent.putExtra(INTENT_TEAM_INFO_BEAN, teamInfoComplete.getTeam());
		startActivity(intent);
	}

	@OnClick(R.id.llt_team_slogan)
	public void fun_15(View v) {// 修改口号
		if (teamInfoComplete == null || teamInfoComplete.getTeam() == null) {
			return;
		}
		Intent intent = new Intent(mContext, ModifyTeamSloganActivity.class);
		intent.putExtra(INTENT_TEAM_INFO_BEAN, teamInfoComplete.getTeam());
		startActivity(intent);
	}

	@OnClick(R.id.llt_team_name)
	public void fun_16(View v) {// 修改球队名称
		if (teamInfoComplete == null || teamInfoComplete.getTeam() == null) {
			return;
		}
		Intent intent = new Intent(mContext, ModifyTeamNameActivity.class);
		intent.putExtra(INTENT_TEAM_INFO_BEAN, teamInfoComplete.getTeam());
		startActivity(intent);
	}
	private RegionDao rDao ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);//事件总线
		ViewUtils.inject(this);
		
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(scrollview, new BitmapDrawable(getResources(),
				background));

		setTheme(R.style.DefaultLightTheme);
		
		title.setText("球队编辑");

		teamInfoComplete = getIntent().getParcelableExtra(INTENT_TEAM_INFO_BEAN);
		isNeedToReturnBean = getIntent().getBooleanExtra(INTENT_NEET_TO_RETURN_BEAN, false);

		if (teamInfoComplete == null || teamInfoComplete.getTeam() == null) {// 加载默认的空白页面
			familyPhoto.setImageResource(R.drawable.team_family_defualt);// 默认
			badge.setImageResource(R.drawable.team_bage_default);
		} else {
			PictureUtil.getMd5PathByUrl(
					PhotoUtil.getThumbnailPath(teamInfoComplete.getTeam()
							.getFamilyPhoto()),
					familyPhoto,
					new DisplayImageOptions.Builder()
							.showImageOnLoading(
									R.drawable.image_download_loading_icon)
							.showImageForEmptyUri(
									R.drawable.team_family_defualt)
							.showImageOnFail(R.drawable.team_family_defualt)
							.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
							.cacheInMemory(true).cacheOnDisk(true).build());
			PictureUtil.getMd5PathByUrl(PhotoUtil
					.getThumbnailPath(teamInfoComplete.getTeam().getBadge()),
					badge, new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.team_bage_default)
							.showImageForEmptyUri(R.drawable.team_bage_default)
							.showImageOnFail(R.drawable.team_bage_default) // 默认球队队徽
							.cacheInMemory(true).cacheOnDisk(true).build());
			currentWhichCaptain = judgeWhichCaptain();
			if(currentWhichCaptain == 1){
				btnDeleteTeam.setVisibility(View.GONE);
			}else{
				btnDeleteTeam.setVisibility(View.GONE);
			}
			initData();
		}
	}
	
	/**
	 * 判断是哪个队长
	 * @return
	 */
	private int judgeWhichCaptain() {
		String uid = footBallUserManager.getCurrentUser().getPlayer().getUuid();
		if (uid.equals(teamInfoComplete.getTeam().getFirstCaptainUuid())){
			return 1;
		}
		if(uid.equals(teamInfoComplete.getTeam().getSecondCaptainUuid())){
			return 2;
		}
		if(uid.equals(teamInfoComplete.getTeam().getThirdCaptainUuid())) {
			return 3;
		}
		return 0;
	}

	String mPhotoPath;

	private void initData() {
		teamNameText.setText(StringUtils.defaultString(teamInfoComplete.getTeam().getName(), ""));
		proDao = new ProvinceDao(mContext);
		cityDao = new CityDao(mContext);
		Province province = proDao.getProvinceByCityProId(
				cityDao.getCityByCityId(teamInfoComplete.getTeam().getCityId()).getProvince_id());
		String proName = province == null ? "" : province.getName();
		City city = cityDao.getCityByCityId(teamInfoComplete.getTeam().getCityId());
		String cityName = city == null ? "" : city.getName();

		rDao = new RegionDao(mContext);
		if(teamInfoComplete.getTeam().getRegionId() != -1 ){
			RegionBean region = rDao.getRegionById(teamInfoComplete.getTeam().getRegionId());
			String regionName = region == null ? "" : region.getName();
			teamRegionText.setText(regionName);
		}
		tv_team_city.setText(proName + " " + cityName);

		teamFoundedTimeText.setText(DateUtil.parseTimeInMillis_only_nyr(teamInfoComplete.getTeam().getDate()));
		teamDescriptionText.setText(StringUtils.defaultString(
				teamInfoComplete.getTeam().getDescription(), ""));
		teamSloganText.setText(StringUtils.defaultString(teamInfoComplete.getTeam().getSlogan(), ""));

		// 显示队长,队副以及教练的名称，头像
		ShowCaptionNameAndPhoto();
	}

	boolean isFromCamera = false;// 区分拍照旋转
	int degree = 0;
	RelativeLayout layout_choose;
	RelativeLayout layout_photo;
	PopupWindow avatorPop;

	public String filePath = "";

	/**
	 * @Title: startImageAction
	 * @return void
	 * @throws
	 */
	private void startImageAction(Uri uri, int outputX, int outputY,
			int requestCode, boolean isCrop) {
		Intent intent = null;
		if (isCrop) {
			intent = new Intent("com.android.camera.action.CROP");
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		}
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}

	/**
	 * 保存裁剪的头像
	 * 
	 * @param data
	 */
	private void saveCropAvator(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			if (bitmap != null) {
//				bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
				if (isFromCamera && degree != 0) {
					bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
				}
				if (flag == 1) {
					familyPhoto.setImageBitmap(bitmap);
				} else {
					badge.setImageBitmap(bitmap);
				}
				// 保存图片
				String filename = new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date());
				filePath = ChatConstants.MyAvatarDir + filename;
				PhotoUtil.saveBitmap(ChatConstants.MyAvatarDir, filename, bitmap,
						true);
				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
	}
	
	/**
	 * 修改球队名称的事件
	 */
	public void onEventMainThread(ModifyTeamNameEvent modifyTeamNameEvent){
		needToRefreshCount++;
		teamNameText.setText(StringUtils.defaultString(modifyTeamNameEvent.getNewTeamName(), ""));
		teamInfoComplete.getTeam().setName(modifyTeamNameEvent.getNewTeamName()); // 重置球队名称
	}
	
	/**
	 * 修改球队介绍的事件
	 * @param modifyTeamDescriptionEvent
	 */
	public void onEventMainThread(ModifyTeamDescriptionEvent modifyTeamDescriptionEvent){
		needToRefreshCount++;
		teamDescriptionText.setText(StringUtils.defaultString(modifyTeamDescriptionEvent.getNewDescription(),
				""));
		teamInfoComplete.getTeam().setDescription(modifyTeamDescriptionEvent.getNewDescription());
	}
	
	/**
	 * 修改球队口号事件
	 * @param modifyTeamSloganEvent
	 */
	public void onEventMainThread(ModifyTeamSloganEvent modifyTeamSloganEvent){
		needToRefreshCount++;
		teamInfoComplete.getTeam().setSlogan(modifyTeamSloganEvent.getNewSlogan());
		teamSloganText.setText(StringUtils.defaultString(modifyTeamSloganEvent.getNewSlogan(), ""));
	}
	
	/**
	 * 修改主队队服事件
	 * @param modifyTeamHomeJerseyEvent
	 */
	public void onEventMainThread(ModifyTeamHomeJerseyEvent modifyTeamHomeJerseyEvent){
		needToRefreshCount++;
		teamInfoComplete.getTeam().setHomeJersey(modifyTeamHomeJerseyEvent.getNewHomeJersey());
	}
	
	/**
	 * 修改客队队服
	 * @param modifyTeamRoadJerseyEvent
	 */
	public void onEventMainThread(ModifyTeamRoadJerseyEvent modifyTeamRoadJerseyEvent){
		needToRefreshCount++;
		teamInfoComplete.getTeam().setRoadJersey(modifyTeamRoadJerseyEvent.getNewRoadJersey());
	}
	
	/**
	 * 修改第三队服
	 * @param modifyTeamAlternetJerseyEvent
	 */
	public void onEventMainThread(ModifyTeamAlternetJerseyEvent modifyTeamAlternetJerseyEvent){
		needToRefreshCount++;
		teamInfoComplete.getTeam().setAlternateJersey(modifyTeamAlternetJerseyEvent.getNewAlternetJersey());
	}
	
	String path;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == ChatConstants.REQUESTCODE_UPLOADAVATAR_CAMERA) {// 拍照
//				if (!Environment.getExternalStorageState().equals(
//						Environment.MEDIA_MOUNTED)) {
//					ShowToast("SD不可用");
//					return;
//				}
//				isFromCamera = true;
//				File file = new File(filePath);
//				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
//				Log.i("life", "拍照后的角度：" + degree);
//				startImageAction(Uri.fromFile(file), 200, 200,
//						ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			CustomGalleryBean galleryBean = new CustomGalleryBean(0, 0, false, mPhotoPath, mPhotoPath);

			Intent intent2 = new Intent(mContext,
					CropImageActivity.class);
			intent2.putExtra(CropImageActivity.INTENT_GALLERY_IMAGE_TO_CROP, galleryBean);
			startActivityForResult(intent2, ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP);
			return;
		}
		if (requestCode == ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP) {  // 裁剪完成
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
//			if (intent == null) {
//				// Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
//				return;
//			} else {
//				saveCropAvator(intent);
//			}
			// 初始化文件路径
			filePath = "";
			// 上传头像
			proDialog = ProgressDialog.show(mContext, "提示", "请稍后...",false, false);
			path =  intent.getStringExtra(CropImageActivity.INTENT_NEW_IMAGE_PATH_FOR_RESULT);
			if (flag == 1) {
				uploadFile(path, TAG_FAMILY_PHOTO);
			} else {
				uploadFile(path, TAG_BADGE);
			}
			return;
		}
		if (requestCode == ChatConstants.REQUESTCODE_UPLOADAVATAR_LOCATION) {//本地照片
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			Uri uri = null;
			if (intent == null) {
				return;
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = false;
				uri = intent.getData();
				 ContentResolver resolver = this.getContentResolver();
				    String[] pojo = {MediaStore.Images.Media.DATA};
				  
				    CursorLoader cursorLoader = new CursorLoader(this, uri, pojo, null,null, null);
				    Cursor cursor = cursorLoader.loadInBackground();
				     cursor.moveToFirst();
				     String path = cursor.getString(cursor.getColumnIndex(pojo[0]));
				     if (path != null && path.length() > 0) {
				    	 mPhotoPath = path;
				      }
//				startImageAction(uri, 200, 200,
//						ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
				CustomGalleryBean galleryBean = new CustomGalleryBean(0, 0, false, mPhotoPath, mPhotoPath);

				Intent intent2 = new Intent(mContext,
						CropImageActivity.class);
				intent2.putExtra(CropImageActivity.INTENT_GALLERY_IMAGE_TO_CROP, galleryBean);
				startActivityForResult(intent2, ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP);
			} else {
				ShowToast("照片获取失败");
			}
			return;
		}
//		if (requestCode == ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP) {
//			// TODO sent to crop
//			if (avatorPop != null) {
//				avatorPop.dismiss();
//			}
//			if (intent == null) {
//				// Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
//				return;
//			} else {
//				saveCropAvator(intent);
//			}
//			// 初始化文件路径
//			// filePath = "";
//			// 上传头像
//			if (flag == 1) {
//				uploadFile(filePath, TAG_FAMILY_PHOTO);
//			} else {
//				uploadFile(filePath, TAG_BADGE);
//			}
//		}
		if (requestCode == REQUEST_CODE_MODIFY_TEAM_CITY) { // 修改球队城市
			AddressBean addressBean = intent
					.getParcelableExtra(CreateTeamActivity.RESULT_SELECT_CITY);
			if (addressBean == null) {
				return;
			}
			
			executeUpdateTeamCity(addressBean);
			return;
		}
		if(requestCode == REQUEST_CODE_MODIFY_TEAM_REGION){
			RegionBean regionBean = intent.getParcelableExtra(RegionListActivity.REGION_BEAN);
			if (regionBean == null) {
				ShowToast("修改地区失败，请重试");
				return;
			}
			executeUpdateTeamRegion(regionBean);
			return;
		}
	}

	//上传图片到图片服务器
	private void uploadFile(String path, String tag) {
		AsyncUploadImage asyncUploadImage = new AsyncUploadImage() {
			@Override
			public void onUploadSuccess(Map<String, UploadImage> uploadMap) {
				TeamRequest teamRequest = new TeamRequest();
				for (Map.Entry<String, UploadImage> entry : uploadMap
						.entrySet()) {//其实一次只有一个值在上传
					if (TAG_FAMILY_PHOTO.equals(entry.getValue().getTag())) {// 全家福
						teamRequest.setFamilyPhoto(entry.getValue().getUrl());
						continue;
					}
					if (TAG_BADGE.equals(entry.getValue().getTag())) {// 队徽
						teamRequest.setBadge(entry.getValue().getUrl());
						continue;
					}
				}
				executeUpdateTeamInfo(teamRequest);
			}

			@Override
			public void onUploadFailed() {
				DialogUtil.dialogDismiss(proDialog);
				Toast.makeText(mContext, "发送失败", Toast.LENGTH_LONG).show();
			}
		};
		Map<String, UploadImage> uploadMap = new LinkedHashMap<String, UploadImage>();// 服务器上的url
		if (!TextUtils.isEmpty(path)) {
			uploadMap.put(path, new UploadImage(path, tag));//只放了一个值进去
			asyncUploadImage.upload2(uploadMap, new PickedImage(path));
		}
	}

	// 执行更新球队信息的操作
	private void executeUpdateTeamInfo(final TeamRequest teamRequest) {
		NetWorkManager.getInstance(getApplicationContext()).updateTeamInfo(
				teamInfoComplete.getTeam().getUuid(),
				footBallUserManager.getCurrentUser().getToken(), teamRequest,
				new Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						DialogUtil.dialogDismiss(proDialog);
						needToRefreshCount++;
						if (flag ==1) {
							ShowToast("更新全家福成功");
							teamInfoComplete.getTeam().setFamilyPhoto(teamRequest.getFamilyPhoto());
							PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(teamRequest.getFamilyPhoto()),
									familyPhoto);
							// 刷新本地数据库
							TeamDao teamDao = CustomApplcation.getDaoSession(mContext).getTeamDao();
							teamDao.insertOrReplace(teamInfoComplete.getTeam());
							EventBus.getDefault().post(new ModifyTeamFamilyPhotoEvent(teamInfoComplete.getTeam().getUuid(), teamRequest.getFamilyPhoto()));
						}else{
							ShowToast("更新队徽成功");
							teamInfoComplete.getTeam().setBadge(teamRequest.getBadge());
							PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(teamRequest.getBadge()),
									badge);
							TeamDao teamDao = CustomApplcation.getDaoSession(mContext).getTeamDao();
							teamDao.insertOrReplace(teamInfoComplete.getTeam());
							EventBus.getDefault().post(new ModifyTeamBadgeEvent(teamInfoComplete.getTeam().getUuid(), teamRequest.getBadge()));
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						DialogUtil.dialogDismiss(proDialog);
						ShowToast(flag == 1 ? "更新全家福失败" : "更新队徽失败");
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			back();
			return true;
		}
		return false;
	}

	private void back() {
		if(isNeedToReturnBean){
			if (needToRefreshCount > 0) {
				Intent data = new Intent();
				data.putExtra(TeamInfoActivity.RESULT_NEED_TO_REFRESH, true);
				data.putExtra(TeamInfoActivity.RESULT_RETURN_TEAM_BEAN, teamInfoComplete);
				setResult(RESULT_OK, data);
			}else{
				Intent data = new Intent();
				data.putExtra(TeamInfoActivity.RESULT_NEED_TO_REFRESH, false);
				setResult(RESULT_OK, data);
			}
		}
		finish();
	}

	/**
	 * 修改一二三队长弹窗
	 * @param teamMebList
	 * @param currentCaptionUid 当前位置的队长id
	 */
	private void ShowTeamMemDialog(final List<PlayerInTeam> teamMebList,
			final String currentCaptionUid, final int whichCaptain) {
		if(teamMebList == null){
			ShowToast("获取球员列表失败");
			return;
		}
		dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(true);
		// 设置dialog的布局及宽高
		View dialogView = LayoutInflater.from(mContext).inflate(
				R.layout.showdialog_layout, null);
		TextView title_dialogView = (TextView) dialogView.findViewById(R.id.title_dialogView);
		
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();//屏幕宽的80%
		LayoutParams layoutParams = new LayoutParams(width * 80 / 100,
				LayoutParams.WRAP_CONTENT);
		dialog.setContentView(dialogView, layoutParams);

		// 往RadioGroup中添加RadioButton，即添加球队成员
		group = (RadioGroup) dialogView.findViewById(R.id.radio_group);
		
		switch(whichCaptain){
		case FirstCaptain:// 修改第一队长
			title_dialogView.setText("请选择队长");
			for (int i = 0; i < teamMebList.size(); i++) {
				PlayerInTeam teamMember = teamMebList.get(i);
				
				RadioButton rBtn = new RadioButton(mContext);
				rBtn.setTextColor(mContext.getResources().getColor(
						R.color.white_color_disable));
				rBtn.setPadding(120, 0, 0, 0); // 设置文字距离按钮四周的距离
				
				rBtn.setText(teamMember.getPlayer().getName());
				rBtn.setId(i); // 这里setId（i）i的值为group的setOnCheckedChangeListener事件中的checkId
				group.addView(rBtn, LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
			}
			break;
		case SecondCaptain:// 修改第二队长
			if(teamMebList.size() <= 1){
				ShowToast("暂无可指定队员，请先邀请队员");
				return;
			}
			title_dialogView.setText("请选择队副");
			String uid = footBallUserManager.getCurrentUser().getPlayer().getUuid();
			for (int i = 0; i < teamMebList.size(); i++) {
				PlayerInTeam teamMember = teamMebList.get(i);
				
				if(teamMember.getPlayer().getUuid().equals(uid)){//跳过当前用户
					continue;
				}
				RadioButton rBtn = new RadioButton(mContext);
				rBtn.setTextColor(mContext.getResources().getColor(
						R.color.white_color_disable));
				rBtn.setPadding(110, 0, 0, 0); // 设置文字距离按钮四周的距离
				
				rBtn.setText(teamMember.getPlayer().getName());
				rBtn.setId(i); // 这里setId（i）i的值为group的setOnCheckedChangeListener事件中的checkId
				group.addView(rBtn, LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
			}
			break;
		case ThirdCaptain:
			if(teamMebList.size() <= 1){
				ShowToast("暂无可指定队员，请先邀请队员");
				return;
			}
			title_dialogView.setText("请选择教练");
			String uuid = footBallUserManager.getCurrentUser().getPlayer().getUuid();
			for (int i = 0; i < teamMebList.size(); i++) {//过滤掉当前的用户 TODO
				PlayerInTeam teamMember = teamMebList.get(i);
				
				if(teamMember.getPlayer().getUuid().equals(uuid)){//跳过当前用户
					continue;
				}
				RadioButton rBtn = new RadioButton(mContext);
				rBtn.setTextColor(mContext.getResources().getColor(
						R.color.white_color_disable));
				rBtn.setPadding(110, 0, 0, 0); // 设置文字距离按钮四周的距离
				
				rBtn.setText(teamMember.getPlayer().getName());
				rBtn.setId(i); // 这里setId（i）i的值为group的setOnCheckedChangeListener事件中的checkId
				group.addView(rBtn, LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
			}
			break;
		}

		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				selectLocation = checkedId;
			}
		});

		Button sureBtn = (Button) dialogView.findViewById(R.id.sure);
		Button cancelBtn = (Button) dialogView.findViewById(R.id.cancel);

		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		//确认修改
		sureBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (selectLocation == -1) {
					// 无需操作
				}else if(teamMebList.get(selectLocation).getPlayer()
						.getUuid().equals(currentCaptionUid)){
					ShowToast("与当前任命相同");
				} else {
					updateCaption(teamMebList.get(selectLocation).getPlayer()
							.getUuid(), whichCaptain, currentCaptionUid);
				}
			}
		});
		dialog.show();
	}

	/**
	 * 更新3个队长的操作
	 * @param newStaffUuid //新任命人员的id
	 * @param whichCaptain //任命的是哪个职位
	 * @param currentCaptionUid //当前职位上的人的uuid  可能为null 没有任命
	 */
	private void updateCaption(final String newStaffUuid, final int whichCaptain, final String currentCaptionUid) {
		final TeamRequest teamRequest = new TeamRequest();
		switch(whichCaptain){
		case FirstCaptain://修改第一队长--弹窗提示
			teamRequest.setFirstCaptainUuid(newStaffUuid);
			AlertDialog.Builder builder = new Builder(mContext);
			builder.setTitle("提示");
			builder.setMessage("一旦修改将失去队长权限，确认修改？");
			builder.setPositiveButton("确认", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog2, int which) {
					dialog2.dismiss();
					modifyCaptainData(teamRequest, newStaffUuid, whichCaptain, currentCaptionUid);
				}
			});
			builder.setNegativeButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.show();
			break;
		case SecondCaptain://修改队副
			teamRequest.setSecondCaptainUuid(newStaffUuid);
			modifyCaptainData(teamRequest, newStaffUuid, whichCaptain, currentCaptionUid);
			break;
		case ThirdCaptain://修改教练
			teamRequest.setThirdCaptainUuid(newStaffUuid);
			modifyCaptainData(teamRequest, newStaffUuid, whichCaptain, currentCaptionUid);
			break;
		default :
			return;
		}
	}
	
	/**
	 * 上传队长修改信息
	 * @param teamRequest
	 * @param newStaffUuid 
	 * @param whichCaptain 
	 * @param currentCaptionUid 
	 */
	private void modifyCaptainData(TeamRequest teamRequest, final String newStaffUuid, final int whichCaptain, final String currentCaptionUid) {
		proDialog = ProgressDialog
				.show(mContext, "修改中", "请稍后...", false, false);
		NetWorkManager.getInstance(mContext).updateTeamInfo(
				teamInfoComplete.getTeam().getUuid(),
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				teamRequest, new Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						DialogUtil.dialogDismiss(proDialog);
						needToRefreshCount++;
						switch (whichCaptain) {
						case FirstCaptain:
							Toast.makeText(mContext, "修改队长成功",
									Toast.LENGTH_SHORT).show();
							refreshCaptain(newStaffUuid, whichCaptain, currentCaptionUid); // 刷新操作（队长）
							back();
							break;
						case SecondCaptain:
							Toast.makeText(mContext, "修改队副成功",
									Toast.LENGTH_SHORT).show();
							refreshCaptain(newStaffUuid, whichCaptain, currentCaptionUid); // 刷新操作（队副）
							break;
						case ThirdCaptain:
							Toast.makeText(mContext, "修改教练成功",
									Toast.LENGTH_SHORT).show();
							refreshCaptain(newStaffUuid, whichCaptain, currentCaptionUid); // 刷新操作（教练）
							break;
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						DialogUtil.dialogDismiss(proDialog);
						Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT)
								.show();
					}
				});
	}

	/**
	 * 先去缓存找队长的信息，同时联网查询
	 */
	private void ShowCaptionNameAndPhoto() {
		if (!TextUtils.isEmpty(teamInfoComplete.getTeam().getFirstCaptainUuid())) {
			// 首先去缓存中查找，看是否已经存在队长信息
			LoadCaptionDataAsyncTask async = new LoadCaptionDataAsyncTask();
			async.setWhichCaptain(FirstCaptain);
			async.execute(teamInfoComplete.getTeam().getFirstCaptainUuid());
			getPersonByUUID(teamInfoComplete.getTeam().getFirstCaptainUuid(), FirstCaptain);
		}
		if (!TextUtils.isEmpty(teamInfoComplete.getTeam().getSecondCaptainUuid())) {
			LoadCaptionDataAsyncTask async = new LoadCaptionDataAsyncTask();
			async.setWhichCaptain(SecondCaptain);
			async.execute(teamInfoComplete.getTeam().getSecondCaptainUuid());
			getPersonByUUID(teamInfoComplete.getTeam().getSecondCaptainUuid(), SecondCaptain); 
		}
		if(!TextUtils.isEmpty(teamInfoComplete.getTeam().getThirdCaptainUuid())){
			LoadCaptionDataAsyncTask async = new LoadCaptionDataAsyncTask();
			async.setWhichCaptain(ThirdCaptain);
			async.execute(teamInfoComplete.getTeam().getThirdCaptainUuid());
			getPersonByUUID(teamInfoComplete.getTeam().getThirdCaptainUuid(), ThirdCaptain);
		}
	}

	private void getPersonByUUID(String CaptionUuid,
			final int whichCaptain) {
		NetWorkManager.getInstance(mContext).getPlayerInfoByUuid(CaptionUuid,
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Gson gson = new Gson();
						Player tempPlayer = gson.fromJson(response.toString(),
								new TypeToken<Player>() {
								}.getType());
						if (tempPlayer == null) {
							return;
						}
						// 将其保存到本地数据库中
						CustomApplcation.getDaoSession(mContext)
								.getPlayerDao().insertOrReplace(tempPlayer);
						DisplayImageOptions options = new DisplayImageOptions.Builder()
						.showImageOnLoading(R.drawable.icon_default_head)
						.showImageForEmptyUri(R.drawable.icon_default_head)
						.showImageOnFail(R.drawable.icon_default_head).build();
						switch(whichCaptain){
						case FirstCaptain:
							first_caption_name.setText(tempPlayer.getName());
							PictureUtil.getMd5PathByUrl(
									PhotoUtil.getThumbnailPath(tempPlayer
											.getPicture()), firstCaptainIcon, options);
							break;
						case SecondCaptain:
							second_caption_name.setText(tempPlayer.getName());
							PictureUtil.getMd5PathByUrl(
									PhotoUtil.getThumbnailPath(tempPlayer
											.getPicture()), secondCaptainIcon, options);
							break;
						case ThirdCaptain:
							coach_name.setText(tempPlayer.getName());
							PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(tempPlayer
											.getPicture()), third_captionIcon, options);
							break;
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("当前网络不可用");
						} else if (error.networkResponse == null) {
//							ShowToast("TeamEditActivity-getPersonByUUID-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						}
					}
				});
	}

	// 执行更改球队城市操作
	private void executeUpdateTeamCity(final AddressBean addressBean) {
		final TeamRequest teamRequest = new TeamRequest();
		teamRequest.setCityId(addressBean.getCityId());
		//给一个默认的地区
		if(!teamInfoComplete.getTeam().getCityId().equals(addressBean.getCityId())){
			int regionId = rDao.getRegionIdByCityId(addressBean.getCityId());
			if(regionId > 0){
				teamRequest.setRegionId(regionId);
			}
		}
		proDialog = ProgressDialog.show(mContext, "提示", "请稍后", false, false);
		NetWorkManager.getInstance(mContext).updateTeamInfo(teamInfoComplete.getTeam().getUuid(),
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				teamRequest, new Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						DialogUtil.dialogDismiss(proDialog);
						ShowToast("编辑城市成功");
						
						needToRefreshCount++;
						Province province = proDao.getProvinceByCityProId(
								cityDao.getCityByCityId(addressBean.getCityId())
										.getProvince_id());
						String proName = province == null ? "" : province.getName();//根据cityId查找省份id，再找省份名字 TODO
						tv_team_city.setText(proName + "·" + addressBean.getCityName());
						
						if(!teamInfoComplete.getTeam().getCityId().equals(addressBean.getCityId())){
							RegionBean region = rDao.getRegionById(teamRequest.getRegionId());
							String regionName = region == null ? "" : region.getName();
							teamRegionText.setText(regionName);
							teamInfoComplete.getTeam().setRegionId(teamRequest.getRegionId());
						}
						
						TeamDao teamDao = CustomApplcation.getDaoSession(mContext).getTeamDao();
						teamInfoComplete.getTeam().setCityId(addressBean.getCityId());
						teamDao.insertOrReplace(teamInfoComplete.getTeam());
						
						EventBus.getDefault().post(new ModifyTeamCityEvent(teamInfoComplete.getTeam().getUuid(), addressBean.getCityId(), addressBean.getCityName()));
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						DialogUtil.dialogDismiss(proDialog);
						Toast.makeText(mContext, "编辑城市失败", Toast.LENGTH_LONG).show();
					}
				});
	}
	
	// 执行更改球队地区操作
	private void executeUpdateTeamRegion(final RegionBean regionBean) {
		TeamRequest teamRequest = new TeamRequest();
		teamRequest.setRegionId(regionBean.getId());
		proDialog = ProgressDialog.show(mContext, "提示", "请稍后", false, false);
		NetWorkManager.getInstance(mContext).updateTeamInfo(teamInfoComplete.getTeam().getUuid(),
				UserManager.getInstance(mContext).getCurrentUser().getToken(),
				teamRequest, new Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						DialogUtil.dialogDismiss(proDialog);
						needToRefreshCount++;
						teamRegionText.setText(regionBean.getName());
						ShowToast("编辑区县成功");
						
						TeamDao teamDao = CustomApplcation.getDaoSession(mContext).getTeamDao();
						teamInfoComplete.getTeam().setRegionId(regionBean.getId());
						teamDao.insertOrReplace(teamInfoComplete.getTeam());
						
						EventBus.getDefault().post(new ModifyTeamRegionEvent(teamInfoComplete.getTeam().getUuid(), regionBean.getId(), regionBean.getName()));
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						DialogUtil.dialogDismiss(proDialog);
						Toast.makeText(mContext, "编辑区县失败", Toast.LENGTH_LONG).show();
//						Toast.makeText(mContext, "" + error.getMessage(),
//								Toast.LENGTH_LONG).show();
					}
				});
	}

	class LoadCaptionDataAsyncTask extends AsyncTask<String, Void, Player> {
		private int whichCaptain;
		
		public void setWhichCaptain(int value){
			whichCaptain = value;
		}
		
		@Override
		protected Player doInBackground(String... params) {
			QueryBuilder<Player> pQB = CustomApplcation.getDaoSession(mContext)
					.getPlayerDao().queryBuilder();
			pQB.where(PlayerDao.Properties.Uuid
					.eq(params[0]));
			Player player = pQB.unique();

			return player;
		}

		@Override
		protected void onPostExecute(Player result) {
			super.onPostExecute(result);
			if (result != null) {
				switch(whichCaptain){
				case FirstCaptain:
					first_caption_name.setText(result.getName());
					PictureUtil.getMd5PathByUrl(
							PhotoUtil.getThumbnailPath(result.getPicture()),
							firstCaptainIcon,new DisplayImageOptions.Builder()
							.showImageOnLoading(
									R.drawable.icon_default_head)
							.showImageForEmptyUri(
									R.drawable.icon_default_head)
							.showImageOnFail(R.drawable.icon_default_head)
							.build());
					break;
				case SecondCaptain:
					second_caption_name.setText(result.getName());
					PictureUtil.getMd5PathByUrl(
							PhotoUtil.getThumbnailPath(result.getPicture()),
							secondCaptainIcon,new DisplayImageOptions.Builder()
							.showImageOnLoading(
									R.drawable.icon_default_head)
							.showImageForEmptyUri(
									R.drawable.icon_default_head)
							.showImageOnFail(R.drawable.icon_default_head)
							.build());
					break;
				case ThirdCaptain:
					coach_name.setText(result.getName());
					PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(result.getPicture()),
							third_captionIcon,new DisplayImageOptions.Builder()
							.showImageOnLoading(
									R.drawable.icon_default_head)
							.showImageForEmptyUri(
									R.drawable.icon_default_head)
							.showImageOnFail(R.drawable.icon_default_head)
							.build());
					break;
				}
			}
		}

	}

	/**
	 * 弹窗选择拍照还是相册
	 */
	@Override
	public void onListItemSelected(String value, int number) {
		if (number == 0) { // 拍照
			java.util.Date now = new java.util.Date();
			String fileName = FastDateFormat
					.getInstance("'IMG'_yyyyMMdd_HHmmss").format(now)
					+ ".jpg";
			File mPhotoFile;
			// 文件路径
			mPhotoPath = JConstants.IMAGE_CACHE
					+ fileName;
			try {
				mPhotoFile = new File(mPhotoPath);
				if (!mPhotoFile.exists()) {
					FileUtil.createFile(mPhotoFile, true);
				}
//				ContentValues values = new ContentValues();
//				values.put(MediaStore.Images.Media.TITLE,
//						fileName);
//				values.put(MediaStore.Images.Media.DESCRIPTION,
//						"Image capture by camera");
//				values.put(MediaStore.Images.Media.MIME_TYPE,
//						"image/jpeg");
				// Uri imageUri =
				// mContext.getContentResolver().insert(
				// MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				// values);
				Intent intent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra("mPhotoPath", mPhotoPath);
				intent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(mPhotoFile));
				intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,
						1);
				startActivityForResult(
								intent,
								ChatConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else { // 选择照片
			ShowLog("点击相册");
			List<Intent> targets = new ArrayList<Intent>();
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			List<ResolveInfo> candidates = getPackageManager().queryIntentActivities(intent, 0);

			for (ResolveInfo candidate : candidates) {
			  String packageName = candidate.activityInfo.packageName;
			  if (!packageName.equals("com.google.android.apps.photos") && !packageName.equals("com.google.android.apps.plus") && !packageName.equals("com.android.documentsui")) {
			      Intent iWantThis = new Intent();
			      iWantThis.setType("image/*");
			      iWantThis.setAction(Intent.ACTION_GET_CONTENT);
			      iWantThis.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
			      iWantThis.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							"image/*");
			      iWantThis.setPackage(packageName);
			      targets.add(iWantThis);
			    }
			}
			Intent chooser = Intent.createChooser(targets.remove(0), "Select Picture");
			chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targets.toArray(new Parcelable[targets.size()]));
			startActivityForResult(chooser, ChatConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
		}
	}

	private void refreshCaptain(String newStaffUuid, int whichCaptain, final String currentCaptionUid) {
		// 修改本地数据库中队长信息
		TeamDao teamDao = CustomApplcation.getDaoSession(mContext).getTeamDao();
		switch(whichCaptain){
		case FirstCaptain:
			teamInfoComplete.getTeam().setFirstCaptainUuid(newStaffUuid);
			EventBus.getDefault().post(new ModifyTeamFirstCaptainEvent(teamInfoComplete.getTeam().getUuid(), newStaffUuid));
			for(PlayerInTeam player :teamInfoComplete.getPlayers()){
				if(player.getPlayer().getUuid().equals(currentCaptionUid)){//球员身份转变
					player.setType(PlayerTypeEnum.GENERAL.getValue());
				}
				if(player.getPlayer().getUuid().equals(newStaffUuid)){
					player.setType(PlayerTypeEnum.FIRST_CAPTAIN.getValue());
					first_caption_name.setText(StringUtils.defaultIfEmpty(player.getPlayer().getName(),""));// 队长名称
					PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(player.getPlayer().getPicture()), firstCaptainIcon, new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.icon_default_head)
							.showImageForEmptyUri(R.drawable.icon_default_head)
							.showImageOnFail(R.drawable.icon_default_head).build());
				}
			}
			break;
		case SecondCaptain:
			teamInfoComplete.getTeam().setSecondCaptainUuid(newStaffUuid);
			EventBus.getDefault().post(new ModifyTeamSecondCaptainEvent(teamInfoComplete.getTeam().getUuid(), newStaffUuid));
			for(PlayerInTeam player :teamInfoComplete.getPlayers()){
				if(player.getPlayer().getUuid().equals(newStaffUuid)){
					second_caption_name.setText(StringUtils.defaultIfEmpty(player.getPlayer().getName(),""));// 队副名称
					PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(player.getPlayer().getPicture()),//队副的头像
							secondCaptainIcon,
							new DisplayImageOptions.Builder()
									.showImageOnLoading(
											R.drawable.icon_default_head)
									.showImageForEmptyUri(
											R.drawable.icon_default_head)
									.showImageOnFail(R.drawable.icon_default_head)
									.build());
					player.setType(PlayerTypeEnum.SECOND_CAPTAIN.getValue());
				}
				if(player.getPlayer().getUuid().equals(currentCaptionUid)){
					player.setType(PlayerTypeEnum.GENERAL.getValue());
				}
			}
			
			if(newStaffUuid.equals(teamInfoComplete.getTeam().getThirdCaptainUuid())){//如果新任命的队副与教练id相等，则教练位置空出来
				teamInfoComplete.getTeam().setThirdCaptainUuid(null);
				coach_name.setText("未设置");//刷新界面
				PictureUtil.getMd5PathByUrl(null, third_captionIcon, new DisplayImageOptions.Builder()
							.showImageOnLoading(R.drawable.icon_default_head)
							.showImageForEmptyUri(R.drawable.icon_default_head)
							.showImageOnFail(R.drawable.icon_default_head).build());
			}
			break;
		case ThirdCaptain:
			teamInfoComplete.getTeam().setThirdCaptainUuid(newStaffUuid);
			EventBus.getDefault().post(new ModifyTeamThirdCaptainEvent(teamInfoComplete.getTeam().getUuid(), newStaffUuid));
			for(PlayerInTeam player :teamInfoComplete.getPlayers()){
				if(player.getPlayer().getUuid().equals(newStaffUuid)){
					coach_name.setText(StringUtils.defaultIfEmpty(player.getPlayer().getName(), ""));//教练名称
					player.setType(PlayerTypeEnum.THIRD_CAPTAIN.getValue());
					//教练的头像
					PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(player.getPlayer().getPicture()), third_captionIcon, new DisplayImageOptions.Builder()
								.showImageOnLoading(R.drawable.icon_default_head)
								.showImageForEmptyUri(R.drawable.icon_default_head)
								.showImageOnFail(R.drawable.icon_default_head).build());
				}
				if(player.getPlayer().getUuid().equals(currentCaptionUid)){
					player.setType(PlayerTypeEnum.GENERAL.getValue());
				}
			}
			
			if(newStaffUuid.equals(teamInfoComplete.getTeam().getSecondCaptainUuid())){//如果新任命的教练与队副id相等，则队副位置空出来
				teamInfoComplete.getTeam().setSecondCaptainUuid(null);
				second_caption_name.setText("未设置");// 队副名称
				PictureUtil.getMd5PathByUrl(null,
						secondCaptainIcon,
						new DisplayImageOptions.Builder()
								.showImageOnLoading(
										R.drawable.icon_default_head)
								.showImageForEmptyUri(
										R.drawable.icon_default_head)
								.showImageOnFail(R.drawable.icon_default_head)
								.build());
			}
			break;
		}
		teamDao.insertOrReplace(teamInfoComplete.getTeam());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
