package com.kinth.football.friend;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kinth.football.R;
import com.kinth.football.listener.MomentImageLoadingListener;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.UtilFunc;
import com.kinth.football.util.ViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 每条动态中显示的图片
 * 
 * @author Sola
 */
public class ActiveGalleryAdapter extends BaseAdapter {
    private final DisplayImageOptions options;
    private final Context mContext;
    private final List<String> path;
    private int type;
    private final int PHOTOS = 0;// 不止一张图片
    private final int ONE_PHOTO = 1;// 只有一张图片

    public ActiveGalleryAdapter(Context mContext, List<String> path) {
        super();
        this.mContext = mContext;
        this.path = path;
        if (path.size() == 1) {
            type = ONE_PHOTO;
        } else {
            type = PHOTOS;
        }
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.image_download_loading_icon)
                .showImageForEmptyUri(R.drawable.image_download_loading_icon)
                .showImageOnFail(R.drawable.image_download_fail_icon).cacheInMemory(true)
                .cacheOnDisk(true).build();
    }

    @Override
    public int getCount() {
        return path == null ? 0 : path.size();
    }

    @Override
    public List<String> getItem(int arg0) {
        return path;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            switch (type) {
                case ONE_PHOTO:
                    convertView = LayoutInflater.from(mContext).inflate(
                            R.layout.moments_active_gallery_one_image_item, parent, false);
                    break;
                case PHOTOS:
                    convertView = LayoutInflater.from(mContext).inflate(
                            R.layout.moments_active_gallery_image_item, parent, false);
                    break;
            }
        }

        switch (type) {
            case PHOTOS:// 图片
                final ImageView activeImg = ViewHolder.get(convertView,
                        R.id.iv_active_gallery_image);
				PictureUtil.UILShowImage(mContext, activeImg, PhotoUtil.getThumbnailPath(path.get(position)), options);
                break;
            case ONE_PHOTO:// 单张图片
                final ImageView activeOneImg = ViewHolder.get(convertView,
                        R.id.iv_active_gallery_one_image);
                PictureUtil.UILShowImage(mContext, activeOneImg, PhotoUtil.getThumbnailPath(path.get(position)), options, new MomentImageLoadingListener() {
					
					@Override
					public void onMomentImageExit(String md5Path) {
						loadAloneImage(md5Path, null, activeOneImg);
					}
					
					@Override
					public void onMomentImageDownloadFailed() {
						// TODO Auto-generated method stub
					}
					
					@Override
					public void onMomentImageDownloadComplete(String md5Path, Bitmap loadedImage) {
						loadAloneImage(md5Path, loadedImage, activeOneImg);
					}
				});
                break;
        }
        return convertView;
    }

    private void loadAloneImage(String md5Path, Bitmap loadedImage, ImageView activeOneImg) {
		int realWidth = 0;// 图片原始的宽
		int realHeight = 0;// 图片原始的高
		if (loadedImage == null) {
			// 加载图片，根据图片的尺寸来显示
			BitmapFactory.Options Options = new BitmapFactory.Options();
			// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
			Options.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeFile(md5Path, Options);// 此时返回bm为空
			Options.inJustDecodeBounds = false;
			realWidth = Options.outWidth;// 图片原始的宽
			realHeight = Options.outHeight;// 图片原始的高
		} else {
			realWidth = loadedImage.getWidth();
			realHeight = loadedImage.getHeight();
		}

        int maxWidthPx = UtilFunc.dip2px(mContext, 300);
        int maxHeightPx = UtilFunc.dip2px(mContext, 150);// 180dp的高，只限制最大高度，宽度根据比例来缩放

        if (realHeight <= maxHeightPx) {// 图片的像素高度没有超过最大高度限制，比较高度和宽度
            if (realWidth <= maxWidthPx) {// 宽度没有超过，按真实宽度来显示
                activeOneImg.setLayoutParams(new LayoutParams(realWidth, realHeight));// 动态设置宽高
            } else {// 如果宽度超过最大限制，整体缩小，宽按
                int h = maxWidthPx * realHeight / realWidth;
                activeOneImg.setLayoutParams(new LayoutParams(maxWidthPx, h));
            }
        } else {// 像素高度超过最大高度限制，
            if (realWidth <= maxWidthPx) {// 只有高度超了，宽度没有超
                int w = maxHeightPx * realWidth / realHeight;
                activeOneImg.setLayoutParams(new LayoutParams(w, maxHeightPx));
            } else {// 宽度和高度都超了
                float widthRate = realWidth * 1.0f / maxWidthPx;
                float heightRate = realHeight * 1.0f / maxHeightPx;
                if (widthRate > heightRate) {// 宽度超出多，以宽为基准缩放，求高
                    int h = maxWidthPx * realHeight / realWidth;
                    activeOneImg.setLayoutParams(new LayoutParams(maxWidthPx, h));
                } else {
                    int w = maxHeightPx * realWidth / realHeight;
                    activeOneImg.setLayoutParams(new LayoutParams(w, maxHeightPx));
                }
            }
        }
        if(loadedImage == null){
        	ImageLoader.getInstance().displayImage("file:///" + md5Path, activeOneImg, options, null);
        }else{
        	activeOneImg.setImageBitmap(loadedImage);
        }
    }
}
