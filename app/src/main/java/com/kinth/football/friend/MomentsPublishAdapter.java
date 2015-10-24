package com.kinth.football.friend;

import java.util.ArrayList;

import com.kinth.football.R;
import com.kinth.football.util.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * 发表动态的Adapter
 * @author Sola
 *
 */
public class MomentsPublishAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<PickedImage> pickedImages = new ArrayList<PickedImage>(); // 选中的图片
	public static final String THIS_IS_PLUS = "THIS_IS_PLUS";// 该图片是+号

	public MomentsPublishAdapter(Context mContext,
			ArrayList<PickedImage> pickedImages) {
		super();
		infalter = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		int targetSizePX = UtilFunc.dip2px(mContext, 70f);
//		targetSize = new ImageSize(targetSizePX, targetSizePX); 
		this.mContext = mContext;
		
		this.pickedImages = pickedImages;
		if (pickedImages.size() < 9 && !pickedImages.get(pickedImages.size() - 1).getPath().equals(THIS_IS_PLUS)) {
			pickedImages.add(new PickedImage(Integer.MAX_VALUE, THIS_IS_PLUS));
		}
	}

	/**
	 * 按添加新选入图片，如果还是不够9张就补"+"，超过就截断
	 */
	public void addPickedImage(ArrayList<PickedImage> newPicked){
		int size = pickedImages.size();
		if(size <= 9 && pickedImages.get(size - 1).getPath().equals(THIS_IS_PLUS)){//小于或者刚好为9个，并且最后一个是“+”
			pickedImages.addAll(size - 1, newPicked);
		}
		//再校验一次，大于9个的要舍弃掉
		if(pickedImages.size() > 9){
			pickedImages.subList(9,pickedImages.size()).clear();
		}
	}
	
	public void setPickedImage(ArrayList<PickedImage> newPicked){
		pickedImages = newPicked;
	}
	
	@Override
	public int getCount() {
		return pickedImages.size();
	}

	@Override
	public String getItem(int arg0) {
		return pickedImages.get(arg0).getPath();//返回该项的
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//去掉convertView == null的判断  不会出现图片显示重复不对问题
//		if (convertView == null) {
			convertView = infalter.inflate(
					R.layout.moments_publish_gallery_item, parent, false);
//		}
		final ImageView itemImg = ViewHolder.get(convertView,
				R.id.iv_publish_item);
		if (THIS_IS_PLUS.equals(pickedImages.get(position).getPath())) {// 加号
			itemImg.setImageResource(R.drawable.selector_avatar_dotline_add_bg);
		} else {
//			ImageLoader.getInstance().loadImage("file:///" + pickedImages.get(position).getPath(), targetSize, new SimpleImageLoadingListener() {
//			    @Override
//			    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//			    	itemImg.setImageBitmap(loadedImage);
//			    }
//			});
			ImageLoader.getInstance().displayImage(
					"file:///" + pickedImages.get(position).getPath(), itemImg,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							itemImg.setImageResource(R.drawable.image_download_loading_icon);
							super.onLoadingStarted(imageUri, view);
						}
					});
		}
		return convertView;
	}

}
