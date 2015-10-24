package com.kinth.football.ui.team;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.FastDateFormat;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.bean.PickedImage;
import com.kinth.football.bean.TeamRequest;
import com.kinth.football.bean.UploadImage;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.config.JConstants;
import com.kinth.football.dao.Team;
import com.kinth.football.dao.TeamDao;
import com.kinth.football.eventbus.bean.ModifyTeamAlternetJerseyEvent;
import com.kinth.football.eventbus.bean.ModifyTeamHomeJerseyEvent;
import com.kinth.football.eventbus.bean.ModifyTeamRoadJerseyEvent;
import com.kinth.football.gallery.CropImageActivity;
import com.kinth.football.gallery.CustomGalleryBean;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.AsyncUploadImage;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.FileUtil;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;
import eu.inmite.android.lib.dialogs.IListDialogListener;
import eu.inmite.android.lib.dialogs.ListDialogFragment;

/**
 * 球队队服展示 & 更改
 * 
 * @author Botision.Huang Descp:调用该类的时候，需要传递一个Team参数
 */
@ContentView(R.layout.activity_teamjersey)
public class TeamJerseyActivity extends BaseActivity implements
		IListDialogListener {

	public static final int REQUEST_CODE_HOMEJERSEY_PHOTO = 9003; // 修改主场球服请求码
	public static final int REQUEST_CODE_ROADJERSEY_PHOTO = 9004; // 修改客场球服请求码
	public static final int REQUEST_CODE_ALTEENATEJERSEY_PHOTO = 9005; // 修改第三球服请求码

	public static final String TAG_HOMEJERSEY_PHOTO = "TAG_HOMEJERSEY_PHOTO"; // 主场球服标签
	public static final String TAG_ROADJERSEY_PHOTO = "TAG_ROADJERSEY_PHOTO"; // 客场球服标签
	public static final String TAG_ALTERNATEJERSEY_PHOTO = "TAH_ALTERNATEJERSEY_PHOTO"; // 第三队服标签

	public static final String MODIFY_TEAM_JERSEY = "MODIFY_TEAM_JERSEY";
	public static final String IS_WHICH_JERSEY = "IS_WHICH_JERSEY";
	public static final String IS_HOMEJERSEY = "IS_HOMEJERSEY";
	public static final String IS_ROADJERSEY = "IS_ROADJERSEY";
	public static final String IS_ALTERNATEJERSEY = "IS_ALTERNATEJERSEY";

	public static final String INTENT_EDIT_TEAM_JERSEY = "INTENT_EDIT_TEAM_JERSEY";
	
	private Team team = null;

	private int flag;
	private ProgressDialog proDialog;

	@ViewInject(R.id.llt_jersey1)
	private LinearLayout llt_homejersey;  // 主队球服背景

	@ViewInject(R.id.homejersey)
	private ImageView homejersey;  // 主队球服

	@ViewInject(R.id.llt_jersey2)
	private LinearLayout llt_roadjersey;  // 客队球服背景
	
	@ViewInject(R.id.roadjersey)
	private ImageView roadjersey;// 客队球服

	@ViewInject(R.id.llt_jersey3)
	private LinearLayout llt_alternatejersey; // 第三球服背景

	@ViewInject(R.id.alternatejersey)
	private ImageView alternatejersey;  	// 第三球服

	@ViewInject(R.id.nav_left)
	private ImageButton back;

	@ViewInject(R.id.nav_title)
	private TextView title;
	
	@ViewInject(R.id.jersey_lin)
	private LinearLayout jersey_lin;

	@OnClick(R.id.nav_left)
	public void fun_1(View v) {
		finish();
	}

	@OnClick(R.id.homejersey)
	public void fun_2(View v) {
		if (currentUserId.equals(team.getFirstCaptainUuid())) {
			flag = 1;
			ListDialogFragment.createBuilder(this, getSupportFragmentManager())
					.setTitle("选择主队队服").setItems(new String[] { "拍照", "选择相册" })
					.hideDefaultButton(true).show();
		} else {
			return;
		}
	}

	@OnClick(R.id.roadjersey)
	public void fun_3(View v) {
		if (currentUserId.equals(team.getFirstCaptainUuid())) {
			flag = 2;
			ListDialogFragment.createBuilder(this, getSupportFragmentManager())
					.setTitle("选择客队队服").setItems(new String[] { "拍照", "选择相册" })
					.hideDefaultButton(true).show();
		} else {
			return;
		}
	}

	@OnClick(R.id.alternatejersey)
	public void fun_4(View v) {
		if (currentUserId.equals(team.getFirstCaptainUuid())) {
			flag = 3;
			ListDialogFragment.createBuilder(this, getSupportFragmentManager())
					.setTitle("选择第三队服").setItems(new String[] { "拍照", "选择相册" })
					.hideDefaultButton(true).show();
		} else {
			return;
		}
	}

	private String currentUserId = UserManager.getInstance(mContext)
			.getCurrentUser().getPlayer().getUuid();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		setTheme(R.style.DefaultLightTheme);

		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
				.getBackgroundBitmap();
		ViewCompat.setBackground(jersey_lin, new BitmapDrawable(getResources(),
				background));
		
		team = getIntent().getExtras().getParcelable(
				TeamInfoActivity.INTENT_TEAM_INFO_BEAN);
		if (team == null) {
			return;
		}
	
		initView();
	}

	private void initView() {
		title.setText("设置队服");
		if (team.getHomeJersey() != null) {
			PictureUtil
					.getMd5PathByUrl(
							PhotoUtil.getThumbnailPath(team.getHomeJersey()),
							homejersey);
		} else {
			llt_homejersey.setBackgroundResource(R.drawable.jersey);
		}
		if (team.getRoadJersey() != null) {
			PictureUtil
					.getMd5PathByUrl(
							PhotoUtil.getThumbnailPath(team.getRoadJersey()),
							roadjersey);
		} else {
			llt_roadjersey.setBackgroundResource(R.drawable.jersey);
		}
		if (team.getAlternateJersey() != null) {
			PictureUtil.getMd5PathByUrl(
					PhotoUtil.getThumbnailPath(team.getAlternateJersey()),
					alternatejersey);
		} else {
			llt_alternatejersey.setBackgroundResource(R.drawable.jersey);
		}
	}
	
	String path;
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == ChatConstants.REQUESTCODE_UPLOADAVATAR_CAMERA) {        //照相
			if (resultCode == RESULT_OK) {
				CustomGalleryBean galleryBean = new CustomGalleryBean(0, 0, false, mPhotoPath, mPhotoPath);

				Intent intent2 = new Intent(mContext,
						CropImageActivity.class);
				intent2.putExtra(CropImageActivity.INTENT_GALLERY_IMAGE_TO_CROP, galleryBean);
				startActivityForResult(intent2, ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP);
			}
			return;
		}
		if (requestCode == ChatConstants.REQUESTCODE_UPLOADAVATAR_LOCATION) {  //本地照片
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
		if (requestCode == ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP) {       //剪切照片
			// TODO sent to crop
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			if (intent == null) {
				return;
			} else {
				saveCropAvator(intent);
			}
			proDialog = ProgressDialog.show(mContext, "提示", "球服上传中，请稍后", false,
					false);
			path =  intent.getStringExtra(CropImageActivity.INTENT_NEW_IMAGE_PATH_FOR_RESULT);
			if (flag == 1) {
				uploadFile(path, TAG_HOMEJERSEY_PHOTO);
			} else if (flag == 2) {
				uploadFile(path, TAG_ROADJERSEY_PHOTO);
			} else {
				uploadFile(path, TAG_ALTERNATEJERSEY_PHOTO);
			}
			return;
		}
		if (requestCode == REQUEST_CODE_HOMEJERSEY_PHOTO) { // 主场队服
			String newPath = intent
					.getStringExtra(CropImageActivity.INTENT_NEW_IMAGE_PATH_FOR_RESULT);
			ImageLoader.getInstance().displayImage("file:///" + newPath,
					homejersey);
			
			proDialog = ProgressDialog.show(mContext, "提示", "球服上传中，请稍后", false,
					false);// 上传新图片
			uploadFile(newPath, TAG_HOMEJERSEY_PHOTO);
			return;
		}
		if (requestCode == REQUEST_CODE_ROADJERSEY_PHOTO) { // 客场队服
			String newPath = intent
					.getStringExtra(CropImageActivity.INTENT_NEW_IMAGE_PATH_FOR_RESULT);
			ImageLoader.getInstance().displayImage("file:///" + newPath,
					roadjersey);
			
			proDialog = ProgressDialog.show(mContext, "提示", "球服上传中，请稍后", false,
					false);
			uploadFile(newPath, TAG_ROADJERSEY_PHOTO);
			return;
		}
		if (requestCode == REQUEST_CODE_ALTEENATEJERSEY_PHOTO) { // 第三队服
			String newPath = intent
					.getStringExtra(CropImageActivity.INTENT_NEW_IMAGE_PATH_FOR_RESULT);
			ImageLoader.getInstance().displayImage("file:///" + newPath,
					alternatejersey);
		
			proDialog = ProgressDialog.show(mContext, "提示", "队服上传中，请稍后", false,
					false);
			return;
		}
	}

	private void uploadFile(String jerseyPath, final String tag) {

		AsyncUploadImage asyncUploadImage = new AsyncUploadImage() {
			@Override
			public void onUploadSuccess(Map<String, UploadImage> uploadMap) {
				proDialog.dismiss();
				TeamRequest teamRequest = new TeamRequest();
				for (Map.Entry<String, UploadImage> entry : uploadMap
						.entrySet()) {
					if (TAG_HOMEJERSEY_PHOTO.equals(entry.getValue().getTag())) { // 主场队服
						teamRequest.setHomeJersey(entry.getValue().getUrl());
						ImageLoader.getInstance().displayImage(
								PhotoUtil.getAllPhotoPath(entry.getValue().getUrl()),
								homejersey);
						continue;
					}
					if (TAG_ROADJERSEY_PHOTO.equals(entry.getValue().getTag())) { // 客场队服
						teamRequest.setRoadJersey(entry.getValue().getUrl());
						ImageLoader.getInstance().displayImage(
								PhotoUtil.getAllPhotoPath(entry.getValue().getUrl()),
								roadjersey);
						continue;
					}
					if (TAG_ALTERNATEJERSEY_PHOTO.equals(entry.getValue().getTag())) { // 第三队服
						teamRequest.setAlternateJersey(entry.getValue().getUrl());
						ImageLoader.getInstance().displayImage(
								PhotoUtil.getAllPhotoPath(entry.getValue().getUrl()),
								alternatejersey);
						continue;
					}
				}
				executeUpdateTeamInfo(teamRequest, tag);
			}

			@Override
			public void onUploadFailed() {
				proDialog.dismiss();
				Toast.makeText(mContext, "发送失败", Toast.LENGTH_LONG).show();
			}
		};

		Map<String, UploadImage> uploadMap = new LinkedHashMap<String, UploadImage>();// 服务器上的url
		if (!TextUtils.isEmpty(jerseyPath)) {
			uploadMap.put(jerseyPath, new UploadImage(jerseyPath, tag));
			asyncUploadImage.upload2(uploadMap, new PickedImage(jerseyPath));
		}
	}

	// 执行更新球队信息的操作
	private void executeUpdateTeamInfo(final TeamRequest teamRequest,
			final String tag) {
		NetWorkManager.getInstance(getApplicationContext()).updateTeamInfo(
				team.getUuid(),
				footBallUserManager.getCurrentUser().getToken(), teamRequest,
				new Listener<Void>() {
					@Override
					public void onResponse(Void response) {
						showNoticeAndDO(teamRequest, tag);
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							ShowToast("当前网络不可用");
							return;
						}
						if (error.networkResponse == null) {
//							ShowToast("TeamJerseyActivity-executeUpdateTeamInfo-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else {
							ShowToast("设置失败");
						}
					}
				});
	}

	@Override
	public void onListItemSelected(String value, int number) {
		// TODO Auto-generated method stub
		changeIcon(value, number);
	}

	boolean isFromCamera = false;// 区分拍照旋转
	int degree = 0;
	RelativeLayout layout_choose;
	RelativeLayout layout_photo;
	PopupWindow avatorPop;

	public String filePath = "";

	/**
	 * 更换头像
	 */
	String mPhotoPath;
	private void changeIcon(String value, int number) {
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
				bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
				if (isFromCamera && degree != 0) {
					bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
				}
				if (flag == 1) {
					homejersey.setImageBitmap(bitmap);
				} else if(flag == 2){
					roadjersey.setImageBitmap(bitmap);
				}else {
					alternatejersey.setImageBitmap(bitmap);
				}
				// 保存图片
				String filename = new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date());
				filePath = ChatConstants.MyAvatarDir + filename;
				PhotoUtil.saveBitmap(ChatConstants.MyAvatarDir, filename, bitmap,
						true);
				// 上传头像
				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
	}

	//球队队服的设置	
	private void showNoticeAndDO(TeamRequest teamRequest, String tag){
		if (tag.equals(TAG_HOMEJERSEY_PHOTO)) {
			ShowToast("设置主场队服成功");
			llt_homejersey.setBackgroundResource(R.drawable.bg_jersey);
			TeamDao teamDao = CustomApplcation.getDaoSession(mContext).getTeamDao();
			team.setHomeJersey(teamRequest.getHomeJersey());
			teamDao.insertOrReplace(team);
			
			EventBus.getDefault().post(new ModifyTeamHomeJerseyEvent(team.getUuid(), teamRequest.getHomeJersey()));
		} else if (tag.equals(TAG_ROADJERSEY_PHOTO)) {
			ShowToast("设置客场队服成功");
			llt_roadjersey.setBackgroundResource(R.drawable.bg_jersey);
			TeamDao teamDao = CustomApplcation.getDaoSession(mContext).getTeamDao();
			team.setHomeJersey(teamRequest.getRoadJersey());
			teamDao.insertOrReplace(team);
			
			EventBus.getDefault().post(new ModifyTeamRoadJerseyEvent(team.getUuid(), teamRequest.getRoadJersey()));
		} else if (tag.equals(TAG_ALTERNATEJERSEY_PHOTO)) {
			ShowToast("设置第三队服成功");
			llt_alternatejersey.setBackgroundResource(R.drawable.bg_jersey);
			TeamDao teamDao = CustomApplcation.getDaoSession(mContext).getTeamDao();
			team.setHomeJersey(teamRequest.getAlternateJersey());
			teamDao.insertOrReplace(team);
			
			EventBus.getDefault().post(new ModifyTeamAlternetJerseyEvent(team.getUuid(), teamRequest.getAlternateJersey()));
		}
	}
}
