package com.kinth.football.gallery;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images.Thumbnails;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.gallery.CustomGalleryAdapter.OnImagePickListener;
import com.kinth.football.util.PictureUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 选择图片与拍照
 * 
 * @author Sola
 */
@ContentView(R.layout.gallery_layout)
public class CustomGalleryActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor> {
	private Context mContext;
	private CustomGalleryAdapter adapter;
	public static final int IMAGE_CAPTURE = 60;//拍照请求码
	private int loaderID = 100;
	public static final String INTENT_IMG_PATH_ARRAY = "INTENT_IMG_PATH_ARRAY";// 返回选中图片地址的数组
	public static final int REQUEST_CODE_CROP_IMAGE = 9991;//裁剪图片的请求码
	private String mPhotoPath;//拍摄的路径

	@ViewInject(R.id.nav_left)
	private View back;

	@ViewInject(R.id.nav_title)
	private TextView title;

	@ViewInject(R.id.gridGallery)
	private GridView gridGallery;
	
	@OnClick(R.id.nav_left)
	public void fun_1(View v) {
		finish();
		//rightOutFinishAnimation();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(null);
		mContext = this;
		ViewUtils.inject(this);
		
		init();
//		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(JConstants.SDCARD_DIRECTORY)));//扫描sd卡的图片
//		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(JConstants.SDCARD_DIRECTORY)));//扫描sd卡的图片
		 Intent scanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_DIR");
         scanIntent.setData(Uri.fromFile(new File(Environment.getExternalStorageDirectory()+"/DCIM")));
         sendBroadcast(scanIntent);
		getSupportLoaderManager().initLoader(loaderID, null, this);
	}

	private void init() {
		title.setText("图片");
		gridGallery.setFastScrollEnabled(true);
		adapter = new CustomGalleryAdapter(mContext);
		adapter.setOnImagePickListener(new OnImagePickListener() {
			
			@Override
			public void transmitImageShootPath(String path) {
				mPhotoPath = path;
			}
		});
		gridGallery.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		adapter.clear();
		super.onDestroy();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
//		final String[] columns = { MediaStore.Images.Media._ID,
//				MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.DATA };
//		final String orderBy = MediaStore.Images.Media.DATE_MODIFIED + " desc";
//
//		final int maxSize = 5 * 1024 * 1024;
//
//		CursorLoader loader = new CursorLoader(this,
//				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
//				MediaStore.Images.Media.MIME_TYPE + "=? and "
//						+ MediaStore.Images.Media.SIZE + "<" + maxSize,
//				new String[] { "image/jpeg" }, orderBy);
		
		final String[] columns = { Thumbnails._ID,Thumbnails.IMAGE_ID,
				Thumbnails.DATA};
		final String orderBy = Thumbnails.IMAGE_ID + " DESC";

		CursorLoader loader = new CursorLoader(this,
				Thumbnails.EXTERNAL_CONTENT_URI, columns, null,  
	           null,  orderBy);
	
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try {
			ArrayList<CustomGalleryBean> list = new ArrayList<CustomGalleryBean>();
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					CustomGalleryBean data = new CustomGalleryBean();
					data.setId(cursor.getLong(cursor
							.getColumnIndex(Thumbnails._ID)));//ID
					data.setImage_id(cursor.getLong(cursor.getColumnIndex(Thumbnails.IMAGE_ID)));
					data.setThumbnailPath(cursor.getString(cursor
							.getColumnIndex(Thumbnails.DATA)));//缩略图路径
					
					data.setRealPath("");//暂时不获取真实图片路径--在预览的时候再一一获取
//					new Thread(new Runnable() {
//						
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							
//						}
//					});
				
					String realPath = PictureUtil.getRealPath(mContext.getContentResolver(), cursor.getLong(cursor.getColumnIndex(Thumbnails.IMAGE_ID)));
					if(TextUtils.isEmpty(realPath)){
						
					}else{
						list.add(data); 
					}
					
				}
			}
			// 加入第一项，照相机
			CustomGalleryBean firstItem = new CustomGalleryBean();
			firstItem.setCamera(true);
			list.add(0, firstItem);
			adapter.clear();
			adapter.addAll(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if(resultCode != RESULT_OK){
			return;
		}
		if(requestCode == REQUEST_CODE_CROP_IMAGE){//裁剪图片后返回--携带新图片的路径
			setResult(RESULT_OK, intent);
			finish();
			return;
		}
		if (requestCode == IMAGE_CAPTURE) {//拍照后的图片进行裁剪
			CustomGalleryBean galleryBean = new CustomGalleryBean(0, 0, false, mPhotoPath, mPhotoPath);

			Intent intent2 = new Intent(mContext,
					CropImageActivity.class);
			intent2.putExtra(CropImageActivity.INTENT_GALLERY_IMAGE_TO_CROP, galleryBean);
			startActivityForResult(
					intent2, REQUEST_CODE_CROP_IMAGE);
			return;
       }  
	}
}
