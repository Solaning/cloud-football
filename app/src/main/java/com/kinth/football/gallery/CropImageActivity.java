package com.kinth.football.gallery;

import java.io.IOException;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.util.DateUtil;
import com.kinth.football.util.ImageLoadOptions;
import com.kinth.football.util.Md5Util;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.view.ClipView;

/**
 * 裁剪图片的Activity
 * 
 * @author Sola
 */

@ContentView(R.layout.activity_crop_image_layout)
public class CropImageActivity extends Activity implements OnTouchListener {
	public static final String INTENT_GALLERY_IMAGE_TO_CROP = "INTENT_GALLERY_IMAGE_TO_CROP";// 裁剪的图片数据源
	public static final String INTENT_NEW_IMAGE_PATH_FOR_RESULT = "INTENT_NEW_IMAGE_PATH_FOR_RESULT";// 裁剪后返回的图片路径

	private CustomGalleryBean galleryBean;

	// These matrices will be used to move and zoom image //切取图片的方框
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	private static final String TAG = "CropImageActivity";
	int mode = NONE;

	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 0.1f;

	int statusBarHeight = 0;
	int titleBarHeight = 0;

	@ViewInject(R.id.nav_left)
	private ImageButton back;

	@ViewInject(R.id.nav_title)
	private TextView title;

	@ViewInject(R.id.nav_right_btn)
	private Button right;

	@ViewInject(R.id.CropImageView)
	private ImageView cropImageView;

	@ViewInject(R.id.clipview)
	private ClipView clipview;

	@OnClick(R.id.nav_left)
	public void fun_1(View v) {
		finish();
	}

	@OnClick(R.id.nav_right_btn)
	public void fun_2(View v) {// 裁剪后保存到sd卡
		// Bitmap croppedImage = cropImageView.getCroppedImage();//裁剪 by Sola
		Bitmap croppedImage = getBitmap(); // 裁剪
		String timeStamp = DateUtil.getStringDate();// 当前时间戳
		String newPath = null; // 新的图片保存路径
		try {
			newPath = PictureUtil.saveImg(croppedImage, Md5Util.md5s(timeStamp + Math.random()*1000));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Intent data = new Intent();
		data.putExtra(INTENT_NEW_IMAGE_PATH_FOR_RESULT, newPath);
		setResult(RESULT_OK, data);
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		cropImageView.setOnTouchListener(this); // 对待裁剪的图片监听触摸事件

		title.setText("图片");
		right.setText(getResources().getString(R.string.use));

		galleryBean = getIntent().getParcelableExtra(INTENT_GALLERY_IMAGE_TO_CROP);// 需要裁剪的原始

		if (galleryBean == null) {
			galleryBean = new CustomGalleryBean();
		}
		final String realPath = galleryBean.getThumbnailPath();
		clipview.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {// 布局完成时的回调

			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {

				// Load image, decode it to Bitmap and return Bitmap to callback
				ImageLoader.getInstance().loadImage("file:///" + realPath, ImageLoadOptions.getOptions(),
						new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						super.onLoadingComplete(imageUri, view, loadedImage);
						cropImageView.setImageBitmap(loadedImage);

						int width = clipview.getWidth();
						int height = clipview.getHeight();
						int left = (width - height / 2) / 2;
						int top = height / 4;
						int right = (width + height / 2) / 2;
						int buttom = height * 3 / 4;

						// 2. 缩放
						float sx = 0f;
						float sy = 0f;

						int realWidth = loadedImage.getWidth();// 图片原始的宽
						int realHeight = loadedImage.getHeight();// 图片原始的高

						int maxWidthPx = right - left;
						int maxHeightPx = buttom - top;

						if (realHeight <= maxHeightPx) {// 图片的像素高度没有超过最大高度限制，比较宽度
							if (realWidth <= maxWidthPx) {// 宽度没有超过
								// 用比值小的那个作为两个共同的缩放值
								sx = maxWidthPx * 1.0f / realWidth;
								sy = maxHeightPx * 1.0f / realHeight;
								if (sx < sy) {
									sy = sx;
								} else {
									sx = sy;
								}
							} else {// 如果宽度超过最大限制，整体缩小，宽按
								sx = maxWidthPx * 1.0f / realWidth;
								sy = sx;
							}
						} else {// 像素高度超过最大高度限制，
							if (realWidth <= maxWidthPx) {// 只有高度超了，宽度没有超
								sy = maxHeightPx * 1.0f / realHeight;
								sx = sy;
							} else {// 宽度和高度都超了
								sx = maxWidthPx * 1.0f / realWidth;
								sy = maxHeightPx * 1.0f / realHeight;
								if (sx > sy) {
									sy = sx;
								} else {
									sx = sy;
								}
							}
						}
						matrix.postScale(sx, sy);
						// 1. 平移
						matrix.postTranslate(left, top);

						cropImageView.setImageMatrix(matrix);
					}

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// TODO Auto-generated method stub
						super.onLoadingStarted(imageUri, view);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						super.onLoadingFailed(imageUri, view, failReason);
						cropImageView.setImageBitmap(null);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						super.onLoadingCancelled(imageUri, view);
						cropImageView.setImageBitmap(null);
					}
				});
				clipview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		ImageView view = (ImageView) v;
		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: // 单点触摸动作

			savedMatrix.set(matrix);
			// 設置初始點位置
			start.set(event.getX(), event.getY());
			// Log.d(TAG, "mode=DRAG_____________DRAG");
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN: // 多点触摸动作
			oldDist = spacing(event);
			Log.d(TAG, "oldDist=" + oldDist);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
				// Log.d(TAG, "mode=ZOOM____________ZOOM");
			}
			break;
		case MotionEvent.ACTION_UP: // 单点触摸离开动作
		case MotionEvent.ACTION_POINTER_UP: // 多点离开动作
			v.performClick();
			mode = NONE;
			// Log.d(TAG, "mode=NONE____________NONE");
			break;
		case MotionEvent.ACTION_MOVE: // 触摸点移动动作
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				// Log.d(TAG, "newDist=" + newDist);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}
		view.setImageMatrix(matrix);
		return true; // indicate event was handled 表明事件已经操作完成
	}

	/** Determine the space between the first two fingers */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	/* 获取矩形区域内的截图 */
	private Bitmap getBitmap() {
		getBarHeight();
		Bitmap screenShoot = takeScreenShot();

		clipview = (ClipView) this.findViewById(R.id.clipview);
		int width = clipview.getWidth();
		int height = clipview.getHeight();
		Bitmap finalBitmap = Bitmap.createBitmap(screenShoot, (width - height / 2) / 2,
				height / 4 + titleBarHeight + statusBarHeight, height / 2, height / 2);
		return finalBitmap;
	}

	private void getBarHeight() {
		// 获取状态栏高度
		Rect frame = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		statusBarHeight = frame.top;

		int contenttop = this.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
		// statusBarHeight是上面所求的状态栏的高度
		titleBarHeight = contenttop - statusBarHeight;

		Log.v(TAG, "statusBarHeight = " + statusBarHeight + ", titleBarHeight = " + titleBarHeight);
	}

	// 获取Activity的截屏
	private Bitmap takeScreenShot() {
		View view = this.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		return view.getDrawingCache();
	}

}
