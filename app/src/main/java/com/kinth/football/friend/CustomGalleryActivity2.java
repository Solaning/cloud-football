package com.kinth.football.friend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.friend.CustomGalleryAdapter.OnImagePickListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 选择图片与拍照
 * 
 * @author Sola 暂时废弃
 */
@Deprecated
@ContentView(R.layout.moments_gallery)
public class CustomGalleryActivity2 extends FragmentActivity implements
		LoaderCallbacks<Cursor> {
	private Context mContext;
	private CustomGalleryAdapter adapter;
	public static final int IMAGE_CAPTURE = 60;//拍照请求码
	private int loaderID = 100;
	public static final String INTENT_IMG_PATH_ARRAY = "INTENT_IMG_PATH_ARRAY";// 返回选中图片地址的数组
	public static final String INTENT_PICKED_IMG_CAPACITY = "INTENT_PICKED_IMG_CAPACITY";//已选图片数量
	public static final int REQUEST_CODE_PICKED_PREVIEW = 9990;//预览已选图片请求码
	public static final int REQUEST_CODE_ALL_PREVIEW = 9991;//预览所有图片请求码
	private int pickedNum;//已选图片，计算剩余可选
	private String mPhotoPath;//拍摄的路径

	@ViewInject(R.id.nav_left)
	private View back;

	@ViewInject(R.id.nav_title)
	private TextView title;

	@ViewInject(R.id.nav_right_btn)
	private Button pickPic;// 选择图片ok

	@ViewInject(R.id.gridGallery)
	private GridView gridGallery;
	
	@ViewInject(R.id.btn_preview)
	private Button preview;//预览

	@OnClick(R.id.nav_left)
	public void fun_1(View v) {
		finish();
		// rightOutFinishAnimation();
	}
	
	@OnClick(R.id.nav_right_btn)
	public void fun_2(View v) {// 完成选择
		ArrayList<PickedImage> pickedImages = adapter.getPickedImages();
		Intent data = new Intent();
		data.putParcelableArrayListExtra(INTENT_IMG_PATH_ARRAY, pickedImages);
		setResult(RESULT_OK, data);
		finish();
	}

	@OnClick(R.id.btn_preview)
	public void fun_3(View v) {//预览图片--所有已选图片的预览
		ArrayList<PickedImage> pickedImages = adapter.getPickedImages();//当前已选的图片列表
		if(pickedImages == null || pickedImages.size() == 0){
			return;
		}
		Intent intent = new Intent(mContext,MomentsPreviewActivity.class);
		intent.putParcelableArrayListExtra(MomentsPreviewActivity.INTENT_IMAGES_TO_PREVIEW, pickedImages);
		startActivityForResult(intent, REQUEST_CODE_PICKED_PREVIEW);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(null);
		mContext = this;
		ViewUtils.inject(this);

		pickedNum = getIntent().getIntExtra(INTENT_PICKED_IMG_CAPACITY, 0);
		init();
		getSupportLoaderManager().initLoader(loaderID, null, this);
	}

	private void init() {
		title.setText("图片");
		pickPic.setText("完成");
		pickPic.setEnabled(false);
		preview.setEnabled(false);
		gridGallery.setFastScrollEnabled(true);
		adapter = new CustomGalleryAdapter(mContext,9-pickedNum);
		adapter.setOnImagePickListener(onImagePickListener);// 图片选中与否的监听
		gridGallery.setAdapter(adapter);
	}

	/**
	 * 图片选中与反选的监听
	 */
	OnImagePickListener onImagePickListener = new OnImagePickListener() {

		@Override
		public void onImagePicked(int total) {
			if (total > 0 && total <= 9) {
				pickPic.setEnabled(true);
				pickPic.setText("完成(" + total + "/"+(9-pickedNum)+")");
				preview.setEnabled(true);
				preview.setText("预览("+total+"/"+(9-pickedNum)+")");
			}
		}

		@Override
		public void onImageUnPicked(int total) {
			if (total == 0) {
				pickPic.setText("完成");
				pickPic.setEnabled(false);
				preview.setEnabled(false);
				preview.setText("预览");
				return;
			}
			pickPic.setText("完成(" + total + "/"+(9-pickedNum)+")");
			preview.setText("预览(" + total + "/"+(9-pickedNum)+")");
		}

		@Override
		public void transmitImageShootPath(String path) {//传递该次拍摄的图片地址
			mPhotoPath = path;
		}
	};

	// void f(){
	// if(DocumentsContract.isDocumentUri(context, contentUri)){
	// String wholeID = DocumentsContract.getDocumentId(contentUri);
	// String id = wholeID.split(:)[1];
	// String[] column = { MediaStore.Images.Media.DATA };
	// String sel = MediaStore.Images.Media._ID + =?;
	// Cursor cursor =
	// context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
	// column,
	// sel, new String[] { id }, null);
	// int columnIndex = cursor.getColumnIndex(column[0]);
	// if (cursor.moveToFirst()) {
	// filePath = cursor.getString(columnIndex);
	// }
	// cursor.close();
	// }else{
	// String[] projection = { MediaStore.Images.Media.DATA };
	// Cursor cursor = context.getContentResolver().query(contentUri,
	// projection, null, null, null);
	// int column_index =
	// cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	// cursor.moveToFirst();
	// filePath = cursor.getString(column_index);
	// }
	// }

	/**
	 * 获取所有要显示的图片
	 * 
	 * @return
	 */
	@Deprecated
	private ArrayList<CustomGalleryBean> getGalleryPhotos() {
		ArrayList<CustomGalleryBean> galleryList = new ArrayList<CustomGalleryBean>();

		try {
			final String[] columns = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID };
			final String orderBy = MediaStore.Images.Media._ID;

			Cursor imagecursor = getContentResolver().query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					null, null, orderBy);

			if (imagecursor != null && imagecursor.getCount() > 0) {

				while (imagecursor.moveToNext()) {
					CustomGalleryBean item = new CustomGalleryBean();

					int dataColumnIndex = imagecursor
							.getColumnIndex(MediaStore.Images.Media.DATA);

					item.setPath(imagecursor.getString(dataColumnIndex));

					galleryList.add(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// show newest photo at beginning of the list
		Collections.reverse(galleryList);
		// 加入第一项，照相机
		CustomGalleryBean firstItem = new CustomGalleryBean();
		firstItem.setCamera(true);
		galleryList.add(0, firstItem);
		return galleryList;
	}

	@Override
	protected void onDestroy() {
		adapter.clear();
		super.onDestroy();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		final String[] columns = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.DATA };
		final String orderBy = MediaStore.Images.Media.DATE_MODIFIED + " desc";

		final int maxSize = 5 * 1024 * 1024;

		CursorLoader loader = new CursorLoader(this,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
				MediaStore.Images.Media.MIME_TYPE + "=? and "
						+ MediaStore.Images.Media.SIZE + "<" + maxSize,
				new String[] { "image/jpeg" }, orderBy);

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
							.getColumnIndex(MediaStore.Images.Media._ID)));//ID
					data.setPath(cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA)));
					list.add(data);
				}
				// 加入第一项，照相机
				CustomGalleryBean firstItem = new CustomGalleryBean();
				firstItem.setCamera(true);
				list.add(0, firstItem);
				adapter.clear();
				adapter.addAll(list);
			}
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
		if(requestCode == REQUEST_CODE_PICKED_PREVIEW){//预览已选图片后返回
			ArrayList<PickedImage> imageResult = intent.getParcelableArrayListExtra(MomentsPreviewActivity.INTENT_PREVIEW_IMAGES_FOR_RESULT);
			if(imageResult == null){
				return;
			}
			int size = imageResult.size();
			if(size ==0 ){//选择的图片都删完
				pickPic.setEnabled(false);
				preview.setEnabled(false);
				pickPic.setText("完成");
				preview.setText("预览");
//				pickedImages.clear();
//				pickedImages.add(new PickedImage(Integer.MAX_VALUE, MomentsPublishAdapter.THIS_IS_PLUS));
				adapter.getRawPickedImagesMap().clear();
				adapter.notifyDataSetChanged();
				return;
			}else {
//				pickedImages.clear();
//				pickedImages = imageResult;
//				if (pickedImages.size() < 9 && !pickedImages.get(pickedImages.size() - 1).getPath().equals(MomentsPublishAdapter.THIS_IS_PLUS)) {// 小于9张图片，添加“+”
//					pickedImages.add(new PickedImage(Integer.MAX_VALUE, MomentsPublishAdapter.THIS_IS_PLUS));
//				}
				pickPic.setEnabled(true);
				pickPic.setText("完成(" + size + "/"+(9-pickedNum)+")");
				preview.setEnabled(true);
				preview.setText("预览("+size+"/"+(9-pickedNum)+")");
				Map<String, PickedImage> tempMap = new HashMap<String, PickedImage>();
				for(PickedImage item: imageResult){
					tempMap.put(item.getPath(), item);
				}
				adapter.setRawPickedImagesMap(tempMap);
				adapter.notifyDataSetChanged();
				return;
			}
		}
		if(requestCode == REQUEST_CODE_ALL_PREVIEW){//预览所有图片返回结果
			//预览图片后返回
			ArrayList<PickedImage> imageResult = intent.getParcelableArrayListExtra(MomentsAllPreviewActivity.INTENT_PREVIEW_IMAGES_FOR_RESULT);
			boolean isFinish = intent.getBooleanExtra(MomentsAllPreviewActivity.INTENT_IS_FINISH_FOR_RESULT, false);//是否按完成按钮返回的
			if(imageResult == null){
				return;
			}
			int size = imageResult.size();
			if(size ==0){//选择的图片都删完
				pickPic.setEnabled(false);
				preview.setEnabled(false);
				pickPic.setText("完成");
				preview.setText("预览");
				adapter.getRawPickedImagesMap().clear();
				adapter.notifyDataSetChanged();
				return;
			}else {
				if(isFinish){//按完成返回
					Intent data = new Intent();
					data.putParcelableArrayListExtra(INTENT_IMG_PATH_ARRAY, imageResult);
					setResult(RESULT_OK, data);
					finish();
					return;
				}else {
					pickPic.setEnabled(true);
					pickPic.setText("完成(" + size + "/"+(9-pickedNum)+")");
					preview.setEnabled(true);
					preview.setText("预览("+size+"/"+(9-pickedNum)+")");
					Map<String, PickedImage> tempMap = new HashMap<String, PickedImage>();
					for(PickedImage item: imageResult){
						tempMap.put(item.getPath(), item);
					}
					adapter.setRawPickedImagesMap(tempMap);
					adapter.notifyDataSetChanged();
				}
				return;
			}
		}
		if (requestCode == IMAGE_CAPTURE) {//拍照返回 
			ArrayList<PickedImage> imageResult = new ArrayList<PickedImage>();
			imageResult.add(new PickedImage(0, mPhotoPath));
			Intent data = new Intent();
			data.putParcelableArrayListExtra(INTENT_IMG_PATH_ARRAY, imageResult);
			setResult(RESULT_OK, data);
			finish();
			return;
//            Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath, null);    
//            mImageView.setImageBitmap(bitmap);  
       }  
	}
}
