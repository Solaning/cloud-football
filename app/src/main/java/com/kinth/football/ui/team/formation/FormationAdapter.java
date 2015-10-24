package com.kinth.football.ui.team.formation;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.chat.util.MD5Util;
import com.kinth.football.config.Config;
import com.kinth.football.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 负责决定下拉刷新列表视图每项显示内容的适配器<br>
 * 
 * @author Alfred Chan
 * 
 */
public class FormationAdapter extends BaseAdapter {

	private List<Formation> listFormation;
	private Context context;

	private View selectedItem;
	private int selectedItemPostion;

	public FormationAdapter(Context context, List<Formation> list) {
		this.listFormation = new ArrayList<Formation>();
		// this.listFormation.addAll(list);
		this.context = context;
	}

	public void updateListView(List<Formation> listFormation) {
		this.listFormation = listFormation;
		notifyDataSetChanged();
	}

	public View getSeletedItem() {
		return selectedItem;
	}
	
	public int getSeletedItemPosition() {
		return selectedItemPostion;
	}

	public void deleteSelectedItem() {
		cancelDeleting();	//这里发现如果删除前面的项，而不是从后面删除的话，后一个会继承前一个的动画效果，所以要在这里先还原
		listFormation.remove(selectedItemPostion);
		notifyDataSetChanged();
		selectedItem = null;
	}

	public void cancelDeleting() {
		// 取消操作，还原被移动项的位置和透明度
		ObjectAnimator.ofFloat(selectedItem, "x", 0f).start();
		ObjectAnimator.ofFloat(selectedItem, "alpha", 1.0f).start();
		selectedItem = null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listFormation.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listFormation.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) { // 如果没有可复用的视图对象，则重新创建一个
			LayoutInflater mInflater = LayoutInflater.from(context);
			convertView = mInflater.inflate(R.layout.item_formation, parent,
					false);
			holder = new ViewHolder();
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
			holder.tvDescription = (TextView) convertView
					.findViewById(R.id.tv_description);
			holder.ivFormation = (ImageView) convertView
					.findViewById(R.id.iv_formation);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 以下代码用于实现设置具体内容
		Formation formation = (Formation) getItem(position);
		if (formation != null) {
			holder.tvTitle.setText(formation.getName());
			// holder.tvAuthor.setText(formation.getAuthor());
			holder.tvDescription.setText(formation.getDescription());

			// 图像的设置
			String picUrl = formation.getImage().trim();
			String localPath = null;
			try {
				localPath = Config.FORMATION_DIR + MD5Util.getMD5(picUrl)+".jpg";
			} catch (NoSuchAlgorithmException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
	    	if(new File(localPath).exists()){
				ImageLoader.getInstance().displayImage("file:///" + localPath,
						holder.ivFormation, ImageLoadOptions.getOptions());
			}else{
				ImageLoader.getInstance().displayImage(picUrl+"Q03.jpg", holder.ivFormation,
						ImageLoadOptions.getOptions());
				//滑动太快会报java.lang.OutOfMemoryError，进行缩放处理
				//holder.ivFormation.setImageBitmap(getBitmap(picUrl));
			}

		}

		convertView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO 自动生成的方法存根
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					selectedItem = v;
					selectedItemPostion = position;
					break;
				}
				return false;
			}
		});

		return convertView;
	}

	/**
	 * View的findViewById()方法也是比较耗时的，因此需要考虑只调用一次，
	 * 之后就用View.getTag()方法来获得ViewHolder对象。
	 * 
	 * @author Alfred Chan
	 * 
	 */
	public class ViewHolder {
		TextView tvTitle; // 地点名称
		// TextView tvAuthor; //编号
		TextView tvDescription; // 地理位置
		ImageView ivFormation; // 距离
	}
	
	public static Bitmap getBitmap(String filePath){
		BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options(); 
		//下面这个设置是将图片边界不可调节变为可调节，如果我们把它设为true，那么BitmapFactory.decodeFile(String path, Options opt)并不会真的返回一个Bitmap给你，
		//它仅仅会把它的宽，高取回来给你，这样就不会占用太多的内存，也就不会那么频繁的发生OOM了。
		bitmapFactoryOptions.inJustDecodeBounds = true; 
		Bitmap bmp = BitmapFactory.decodeFile(filePath, bitmapFactoryOptions);
		//将图片进行缩放，节约内存，否则会出现内存溢出现象，报Fatal signal 11 (SIGSEGV)错误
		bitmapFactoryOptions.inSampleSize = 10;
		//重新设为不可调节 
		bitmapFactoryOptions.inJustDecodeBounds = false; 
		
		bmp = BitmapFactory.decodeFile(filePath, bitmapFactoryOptions);
		
		return bmp;
	}
}
