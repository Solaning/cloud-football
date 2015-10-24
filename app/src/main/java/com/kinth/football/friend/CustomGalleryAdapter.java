package com.kinth.football.friend;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.time.FastDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinth.football.R;
import com.kinth.football.config.JConstants;
import com.kinth.football.util.FileUtil;
import com.kinth.football.util.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 暂时废弃
 * @author Sola
 *
 */
@Deprecated
public class CustomGalleryAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater infalter;
	private Map<String, PickedImage> pickedImages = new HashMap<String, PickedImage>();// 被选中的图片列表
	private int leftCapacity;// 剩余可选图片容量
	private final int CAMERA = 0;
	private final int PHOTO = 1;

	private ArrayList<CustomGalleryBean> data = new ArrayList<CustomGalleryBean>();// 第1项是照相机
	private OnImagePickListener imagePickListener;

	public CustomGalleryAdapter(Context context, int leftCapacity) {
		super();
		infalter = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mContext = context;
		this.leftCapacity = leftCapacity;
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
		// 选中图片
		public void onImagePicked(int total);

		// 反选图片
		public void onImageUnPicked(int total);

		// 该次拍照的图片路径
		public void transmitImageShootPath(String path);
	}

	/**
	 * 获取被选中的图片---预先排序
	 */
	public ArrayList<PickedImage> getPickedImages() {
		Iterator<String> iterator = pickedImages.keySet().iterator();
		ArrayList<PickedImage> pickedImageList = new ArrayList<PickedImage>();
		while (iterator.hasNext()) {
			pickedImageList.add(pickedImages.get(iterator.next()));
		}
		Collections.sort(pickedImageList, new Comparator<PickedImage>() {
			@Override
			public int compare(PickedImage o1, PickedImage o2) {
				return Integer.valueOf(o1.getPosition()).compareTo(
						Integer.valueOf(o2.getPosition()));
			}
		});
		return pickedImageList;
	}

	/**
	 * 被选中图片原始数据集 Map
	 * 
	 * @param newPicked
	 */
	public void setRawPickedImagesMap(Map<String, PickedImage> newPicked) {
		pickedImages = newPicked;
	}

	/**
	 * 被选中图片原始数据集 Map
	 * 
	 * @return
	 */
	public Map<String, PickedImage> getRawPickedImagesMap() {
		return pickedImages;
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
						R.layout.moments_gallery_camera_item, parent, false);
				break;
			case PHOTO:
				convertView = infalter.inflate(
						R.layout.moments_gallery_photo_item, parent, false);
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
								if (imagePickListener != null) {
									imagePickListener
											.transmitImageShootPath(mPhotoPath);
								}
								ContentValues values = new ContentValues();
								values.put(MediaStore.Images.Media.TITLE,
										fileName);
								values.put(MediaStore.Images.Media.DESCRIPTION,
										"Image capture by camera");
								values.put(MediaStore.Images.Media.MIME_TYPE,
										"image/jpeg");
								// Uri imageUri =
								// mContext.getContentResolver().insert(
								// MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								// values);
								Intent intent = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								intent.putExtra(MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(mPhotoFile));
								intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,
										1);
								((FragmentActivity) mContext)
										.startActivityForResult(
												intent,
												CustomGalleryActivity2.IMAGE_CAPTURE);
							} catch (Exception e) {
							}
						}
					});
			break;
		case PHOTO:
			final ImageView imgQueue = ViewHolder.get(convertView,
					R.id.imgQueue);
			final View pickMask = ViewHolder.get(convertView,
					R.id.view_pick_mask);// 选中后的蒙版
			final CheckBox checkBox = ViewHolder
					.get(convertView, R.id.checkbox);

			if (pickedImages.containsKey(data.get(position).getPath())) {
				checkBox.setChecked(true);
				pickMask.setBackgroundColor(mContext.getResources().getColor(
						R.color.b_transparent));
			} else {
				checkBox.setChecked(false);
				pickMask.setBackgroundColor(mContext.getResources().getColor(
						android.R.color.transparent));
			}

			checkBox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (checkBox.isChecked()) {// 如果选中
						if (pickedImages.size() < leftCapacity) {
							pickedImages.put(data.get(position).getPath(),
									new PickedImage(position, data
											.get(position).getPath()));
							pickMask.setBackgroundColor(mContext.getResources()
									.getColor(R.color.b_transparent));// 改变背景
							if (imagePickListener != null) {// 回调
								imagePickListener.onImagePicked(pickedImages
										.size());
							}
						} else {// 超过了leftCapacity张图片
							Toast.makeText(mContext,
									"你最多只能选择" + leftCapacity + "张照片",
									Toast.LENGTH_SHORT).show();
							checkBox.setChecked(false);
						}
					} else {// 反选
						pickedImages.remove(data.get(position).getPath());
						pickMask.setBackgroundColor(mContext.getResources()
								.getColor(android.R.color.transparent));// 改变背景
						if (imagePickListener != null) {// 回调
							imagePickListener.onImageUnPicked(pickedImages
									.size());
						}
					}
				}
			});
			// 图片预览
			imgQueue.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext,
							MomentsAllPreviewActivity.class);
					intent.putParcelableArrayListExtra(
							MomentsAllPreviewActivity.INTENT_ALL_IMAGES_TO_PREVIEW,
							data);// 所有图片，没有剔除相机
					intent.putParcelableArrayListExtra(
							MomentsAllPreviewActivity.INTENT_PICKED_IMAGES_TO_PREVIEW,
							getPickedImages());// 当前已选图片
					intent.putExtra(
							MomentsAllPreviewActivity.INTENT_IMAGE_POSITION,
							position);// 有相机情况下的位置
					((FragmentActivity) mContext).startActivityForResult(
							intent,
							CustomGalleryActivity2.REQUEST_CODE_ALL_PREVIEW);
				}
			});
			try {
				ImageLoader.getInstance().displayImage(
						"file:///" + data.get(position).getPath(), imgQueue,
						new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
								imgQueue.setImageResource(R.drawable.image_download_loading_icon);
								super.onLoadingStarted(imageUri, view);
							}
						});
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
		pickedImages.clear();
		notifyDataSetChanged();
	}
}
