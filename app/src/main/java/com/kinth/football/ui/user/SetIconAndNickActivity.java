package com.kinth.football.ui.user;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.FastDateFormat;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.kinth.football.R;
import com.kinth.football.bean.User;
import com.kinth.football.chat.ChatConstants;
import com.kinth.football.config.JConstants;
import com.kinth.football.config.PlayerPositionEnum;
import com.kinth.football.gallery.CropImageActivity;
import com.kinth.football.gallery.CustomGalleryBean;
import com.kinth.football.network.AsyncFileUpload;
import com.kinth.football.network.AsyncFileUpload.FileCallback;
import com.kinth.football.network.UserNetworkManager;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.ui.MainActivity;
import com.kinth.football.ui.mine.ModifyPositionActivity;
import com.kinth.football.util.FileUtil;
import com.kinth.football.util.LogUtil;
import com.kinth.football.util.PhotoUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

import eu.inmite.android.lib.dialogs.IListDialogListener;
import eu.inmite.android.lib.dialogs.ListDialogFragment;

@ContentView(R.layout.activity_set_icon_nick)
public class SetIconAndNickActivity extends BaseActivity implements IListDialogListener {

	public static final int THEME_CUSTOM_LIGHT = 3;
	public static final int THEME_DEFAULT_LIGHT = 1;
	
	@ViewInject(R.id.edtPosition)
	private EditText edtPosition; 
	
	@ViewInject(R.id.edtHeight)
	private EditText edtHeight;
	
	@ViewInject(R.id.edtWeight)
	private EditText edtWeight;
	
	@ViewInject(R.id.edtNick)
	private EditText edtNickName;

	@ViewInject(R.id.AppWidget)
	private LinearLayout layout_all;
	
	@ViewInject(R.id.ibtn_avatar)
	private ImageView iv_set_avator;
	
	@ViewInject(R.id.llt_avatar)
	private LinearLayout llt_avator;
	
	ProgressDialog progress;
	private String position;
	
	String iconUrl = "";
	String fileLocalUrl = "";
	
	private String fileUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		initView();
		//设置背景
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		opt.inInputShareable = true;
		opt.inPurgeable = true;
		// 获取资源图片
		InputStream is = getResources().openRawResource(
				R.drawable.bg2);
		Bitmap background = BitmapFactory.decodeStream(is, null, opt);
		ViewCompat.setBackground(layout_all, new BitmapDrawable(getResources(),
				background));
		
		edtPosition.setFocusable(true);
	}

	private void initView() {
		edtHeight.setKeyListener(new NumberKeyListener() {
			@Override
			protected char[] getAcceptedChars() {
				return new char[] { '1', '2', '3', '4', '5', '6', '7', '8',
						'9', '0' };
			}

			@Override
			public int getInputType() {
				// TODO Auto-generated method stub
				return android.text.InputType.TYPE_CLASS_PHONE;
			}
		});

		edtWeight.setKeyListener(new NumberKeyListener() {
			@Override
			protected char[] getAcceptedChars() {
				return new char[] { '1', '2', '3', '4', '5', '6', '7', '8',
						'9', '0' };
			}

			@Override
			public int getInputType() {
				return android.text.InputType.TYPE_CLASS_PHONE;
			}
		});

		edtPosition.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_DOWN){
					Intent intent = new Intent(mContext,
							ModifyPositionActivity.class);
					intent.putExtra("isFromRegist", 1);
					startActivityForResult(intent, 555);
					
				}
				return true;
			}
		});
		initTopBarForOnlyTitle("填写资料");
		setTheme(R.style.DefaultLightTheme);
	}

	public void onRegisterClick(View v) {
		switch (v.getId()) {
		case R.id.llt_avatar://选择图片、拍照
			showAvatarPop();
			break;
		case R.id.btn_submit:
			if (edtNickName.getText().toString().equals("")) {
				ShowToast("请输入昵称");
			} else if (edtPosition.getText().toString().equals("")) {
				ShowToast("请输入位置");
			} else if (edtHeight.getText().toString().equals("")
					|| edtHeight.getText().toString().length() == 0) {
				ShowToast("请输入身高");
			} else if (edtWeight.getText().toString().equals("")
					|| edtWeight.getText().toString().length() == 0) {
				ShowToast("请输入体重");
			} else {
				int height = Integer.valueOf(edtHeight.getText().toString());
				int weight = Integer.valueOf(edtWeight.getText().toString());
				uploadAvatar(fileUrl, edtNickName.getText().toString(), position,
						height, weight);
			}
			break;
		default:
			break;
		}
	}

	private void modifyUserInfo(final String nick, final String url,
			final String position, final int height, final int weight) {
		UserNetworkManager.getInstance(getApplicationContext())
				.modifyUserIconAndNick(url, nick, position, height, weight,
						new Listener<Void>() {

							@Override
							public void onResponse(Void response) {
								// TODO Auto-generated method stub
								ShowToast("用户资料填写成功");
								User user = footBallUserManager
										.getCurrentUser();
								user.getPlayer().setPicture(url);
								user.getPlayer().setName(nick);
								user.getPlayer().setPosition(position);
								user.getPlayer().setHeight(height);
								user.getPlayer().setWeight(weight);
								footBallUserManager.saveCurrentUser(user);
								startAnimActivity(MainActivity.class);
								finish();
							}
						}, new ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								// TODO Auto-generated method stub
								ShowToast("用户资料填写失败");
							}
						});
	}

	RelativeLayout layout_choose;
	RelativeLayout layout_photo;
	PopupWindow avatorPop;

	public String filePath = "";

	/**
	 * 显示更换头像
	 */
	private void showAvatarPop() {
		ListDialogFragment.createBuilder(mContext, getSupportFragmentManager())
				.setTitle("选择头像").setItems(new String[] { "拍照", "选择相册" })
				.hideDefaultButton(true).show();
	}

	Bitmap newBitmap;
	boolean isFromCamera = false;// 区分拍照旋转
	int degree = 0;
	String mPhotoPath;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 555:
			if (resultCode == 555) {
				position = data.getStringExtra("position");
				String positon_name = PlayerPositionEnum.getEnumFromString(
						position).getName();
				edtPosition.setText(positon_name + "(" + position + ")");
			}
			break;
		case JConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
			// 返回成功
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				// 是否是从拍照
//				isFromCamera = true;
//				File file = new File(filePath);
//				// 获取拍照角度
//				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
//				Log.i("life", "拍照后的角度：" + degree);
//				startImageAction(Uri.fromFile(file), 200, 200,
//						ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
				if (resultCode == RESULT_OK) {
					CustomGalleryBean galleryBean = new CustomGalleryBean(0, 0, false, mPhotoPath, mPhotoPath);

					Intent intent2 = new Intent(mContext,
							CropImageActivity.class);
					intent2.putExtra(CropImageActivity.INTENT_GALLERY_IMAGE_TO_CROP, galleryBean);
					startActivityForResult(intent2, ChatConstants.REQUESTCODE_UPLOADAVATAR_CROP);
				}
			}
			
			break;
		case JConstants.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			Uri uri = null;
			if (data == null) {
				return;
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = false;
				uri = data.getData();
				 ContentResolver resolver = this.getContentResolver();
				    String[] pojo = {MediaStore.Images.Media.DATA};
				  
				    CursorLoader cursorLoader = new CursorLoader(this, uri, pojo, null,null, null);
				    Cursor cursor = cursorLoader.loadInBackground();
				     cursor.moveToFirst(); //bug by sola TODO
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
			break;
		case JConstants.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
			// TODO sent to crop
//			if (avatorPop != null) {
//				avatorPop.dismiss();
//			}
//			if (data == null) {
//				// Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
//				return;
//			} else {
//				// 保存裁剪后的头像
//				saveCropAvator(data);
//			}
//			// 初始化文件路径
//			filePath = "";
//			// 上传头像 TODO
//			// uploadAvatar(path);
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			if (data == null) {
				// Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
				return;
			} else {
				saveCropAvator(data);
			}
			// 初始化文件路径
			filePath = "";
			// 上传头像
			path =  data.getStringExtra(CropImageActivity.INTENT_NEW_IMAGE_PATH_FOR_RESULT);
//			uploadAvatar();
			if (TextUtils.isEmpty(path)){

			} else {
//				ShowToast("正在上传图片...");
			}
			fileLocalUrl = path; //

			AsyncFileUpload asyncFileUpload = new AsyncFileUpload();
			asyncFileUpload.asynFileUploadToServer(path, new FileCallback() {
				@Override
				public void fileUploadCallback(final String fileUrl2) {
					LogUtil.i("头像地址：" + path);
					runOnUiThread(new Runnable() {
						public void run() {
							if (TextUtils.isEmpty(fileUrl2)) {
								// ShowToast("头像暂无");
							} else {
//								ShowToast("上传成功");
								ImageLoader.getInstance().displayImage(
										PhotoUtil.getAllPhotoPath(fileUrl2),
										iv_set_avator);
								fileUrl = fileUrl2;
							}
						
						}
					});
				}
			});
		
			break;
		default:
			break;
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

	String path;

	/**
	 * 保存裁剪的头像
	 * 
	 * @param data
	 */
	private void saveCropAvator(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			Log.i("life", "avatar - bitmap = " + bitmap);
			if (bitmap != null) {
				bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
				if (isFromCamera && degree != 0) {
					bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
				}
				iv_set_avator.setImageBitmap(bitmap);
				// 保存图片
				String filename = new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date());
				path = ChatConstants.MyAvatarDir + filename;
				PhotoUtil.saveBitmap(ChatConstants.MyAvatarDir, filename, bitmap,
						true);
				// 上传头像
				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
	}

	/**
	 * 上传头像
	 */
	@SuppressWarnings("static-access")
	private void uploadAvatar(final String fileUrl, final String nick,
			final String position, final int height, final int weight) {
//		LogUtil.i("头像地址：" + path);
//		LogUtil.i("头像地址：" + selectPath);
//		if (TextUtils.isEmpty(path)){
//
//		} else {
//			ShowToast("正在上传图片...");
//		}
//		fileLocalUrl = path; //
//
//		AsyncFileUpload asyncFileUpload = new AsyncFileUpload();
//		asyncFileUpload.asynFileUploadToServer(path, new FileCallback() {
//			@Override
//			public void fileUploadCallback(final String fileUrl) {
//				LogUtil.i("头像地址：" + path);
//				runOnUiThread(new Runnable() {
//					public void run() {
//						if (TextUtils.isEmpty(fileUrl)) {
//							// ShowToast("头像暂无");
//						} else {
//							ShowToast("上传成功");
//						}

						modifyUserInfo(nick, fileUrl, position, height, weight);
//					}
//				});
//			}
//		});
	}

	@Override
	public void onListItemSelected(String value, int number) {
		if (number == 0) { // 拍照
			java.util.Date now = new java.util.Date();
			String fileName = FastDateFormat.getInstance(
					"'IMG'_yyyyMMdd_HHmmss").format(now)
					+ ".jpg";

			File mPhotoFile;

			// 文件路径
			mPhotoPath = JConstants.IMAGE_CACHE + fileName;
			try {
				mPhotoFile = new File(mPhotoPath);
				if (!mPhotoFile.exists()) {
					FileUtil.createFile(mPhotoFile, true);
				}
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra("mPhotoPath", mPhotoPath);
				intent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(mPhotoFile));
				intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
				startActivityForResult(intent,
						ChatConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else { // 选择照片
			List<Intent> targets = new ArrayList<Intent>();
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			List<ResolveInfo> candidates = getPackageManager()
					.queryIntentActivities(intent, 0);

			for (ResolveInfo candidate : candidates) {
				String packageName = candidate.activityInfo.packageName;
				if (!packageName.equals("com.google.android.apps.photos")
						&& !packageName.equals("com.google.android.apps.plus")
						&& !packageName.equals("com.android.documentsui")) {
					Intent iWantThis = new Intent();
					iWantThis.setType("image/*");
					iWantThis.setAction(Intent.ACTION_GET_CONTENT);
					iWantThis.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
					iWantThis.setDataAndType(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							"image/*");
					iWantThis.setPackage(packageName);
					targets.add(iWantThis);
				}
			}
			Intent chooser = Intent.createChooser(targets.remove(0),
					"Select Picture");
			chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS,
					targets.toArray(new Parcelable[targets.size()]));
			startActivityForResult(chooser,
					ChatConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
		}
	}

}
