package com.kinth.football.gallery;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.lang3.time.FastDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.config.JConstants;
import com.kinth.football.friend.CustomGalleryActivity2;
import com.kinth.football.util.FileUtil;
import com.kinth.football.util.ImageLoadOptions;
import com.kinth.football.util.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class CustomGalleryAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater infalter;
	private final int CAMERA = 0;
	private final int PHOTO = 1;

	private ArrayList<CustomGalleryBean> data = new ArrayList<CustomGalleryBean>();// 第1项是照相机
	private OnImagePickListener imagePickListener;

	public CustomGalleryAdapter(Context context) {
		super();
		infalter = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mContext = context;
	}

	public void setOnImagePickListener(OnImagePickListener listener) {
		this.imagePickListener = listener;
	}

	// 每个convert view都会调用此方法，获得当前所需要的view样式
	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return CAMERA;
		}
		return PHOTO;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getCount() {
		return data.size();// 默认多一项用于放照相按钮
	}

	@Override
	public CustomGalleryBean getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 单选框选中与反选接口
	 * 
	 * @author Sola
	 *
	 */
	public interface OnImagePickListener {
		
		// 该次拍照的图片路径
		public void transmitImageShootPath(String path);
	}

	public void addAll(ArrayList<CustomGalleryBean> files) {
		try {
			this.data.clear();
			this.data.addAll(files);
		} catch (Exception e) {
			e.printStackTrace();
		}
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		if (convertView == null) {
			switch (type) {
			case CAMERA:
				convertView = infalter.inflate(
						R.layout.gallery_camera_item, parent, false);
				break;
			case PHOTO:
				convertView = infalter.inflate(
						R.layout.gallery_photo_item, parent, false);
				break;
			}
		}
		switch (type) {
		case CAMERA:
			ImageView cameraIcon = ViewHolder
					.get(convertView, R.id.camera_icon);
			TextView takePhoto = ViewHolder
					.get(convertView, R.id.tv_take_photo);
			cameraIcon
					.setImageResource(R.drawable.selector_sentpic_camera_icon);
			cameraIcon.setOnClickListener(new OnClickListener() {// 照相

						@Override
						public void onClick(View arg0) {
							java.util.Date now = new java.util.Date();
							String fileName = FastDateFormat
									.getInstance("'IMG'_yyyyMMdd_HHmmss").format(now)
									+ ".jpg";

							File mPhotoFile;
							String mPhotoPath;
							// 文件路径
							mPhotoPath = JConstants.IMAGE_CACHE
									+ fileName;
							try {
								mPhotoFile = new File(mPhotoPath);
								if (!mPhotoFile.exists()) {
									FileUtil.createFile(mPhotoFile, true);
								}
								if (imagePickListener != null) {//接口，把图片地址传递出去
									imagePickListener
											.transmitImageShootPath(mPhotoPath);
								}
//								ContentValues values = new ContentValues();
//								values.put(MediaStore.Images.Media.TITLE,
//										fileName);
//								values.put(MediaStore.Images.Media.DESCRIPTION,
//										"Image capture by camera");
//								values.put(MediaStore.Images.Media.MIME_TYPE,
//										"image/jpeg");
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
								((FragmentActivity) mContext)
										.startActivityForResult(
												intent,
												CustomGalleryActivity.IMAGE_CAPTURE);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
			break;
		case PHOTO:
			final ImageView imgQueue = ViewHolder.get(convertView,
					R.id.imgQueue);

			//选中该图片进行裁剪
			imgQueue.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(mContext,
							CropImageActivity.class);
					intent.putExtra(CropImageActivity.INTENT_GALLERY_IMAGE_TO_CROP, data.get(position));
//					System.out.println(data.get(position).getThumbnailPath()+"$$$"+"xxxxxxxxxx");
//					System.out.println(data.get(position).getRealPath()+"$$$"+"xxxxxxxxxx");
//					System.out.println(data.get(position).getImage_id()+"$$$"+"xxxxxxxxxx");
//					System.out.println(data.get(position).getId()+"$$$"+"xxxxxxxxxx");
					((FragmentActivity) mContext).startActivityForResult(
							intent,
							CustomGalleryActivity.REQUEST_CODE_CROP_IMAGE);
				}
			}
			);
			try {//显示缩略图片
				ImageLoader.getInstance().displayImage(
						"file:///" + data.get(position).getThumbnailPath(), imgQueue,
						new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
								imgQueue.setImageResource(R.drawable.image_download_loading_icon);
								System.out.println(11111);
								super.onLoadingStarted(imageUri, view);
							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								// TODO Auto-generated method stub
								imgQueue.setImageBitmap(loadedImage);
								System.out.println(22222);
								super.onLoadingComplete(imageUri, view, loadedImage);
							}
							
						});
//				ImageLoader.getInstance().displayImage("file:///" + data.get(position).getThumbnailPath(), imgQueue);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		return convertView;
	}

	public void clearCache() {
		ImageLoader.getInstance().clearDiskCache();
		ImageLoader.getInstance().clearMemoryCache();
	}

	public void clear() {
		data.clear();
		notifyDataSetChanged();
	}
}
