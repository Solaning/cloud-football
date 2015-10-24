package com.kinth.football.friend;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.kinth.football.R;
import com.kinth.football.bean.moments.Sharing;
import com.kinth.football.config.MomentsTypeEnum;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 朋友圈个人主页适配器
 * 
 * @author Sola
 * 
 */
public class MomentPersonalZoneAdapter extends BaseAdapter {
	private Context mContext;
	private List<Sharing> sharingList;
	private LayoutInflater inflater;
	private final int TEXT = 0;// 只有文字
	private final int IMAGE = 1;// 图文
	private static Calendar calendar = Calendar.getInstance();

	private Typeface tf1;

	private HashMap<Integer, String> month_hashMap = new HashMap<Integer, String>();
	private String[] months = new String[] { "一", "二", "三", "四", "五", "六", "七",
			"八", "九", "十", "十一", "十二" };

	private int month = 0, day = 0;
	private int i = -1;
	private HashMap<Integer, String> hashMap = new HashMap<Integer, String>();

	public MomentPersonalZoneAdapter(Context mContext, List<Sharing> sharingList) {
		super();
		this.mContext = mContext;
		this.sharingList = sharingList;
		this.inflater = LayoutInflater.from(mContext);

		tf1 = Typeface.createFromAsset(mContext.getAssets(),
				"fonts/AGENCYB_0.TTF");
		for (int i = 0; i < 12; i++) {
			month_hashMap.put(i, months[i]);
		}
	}

	public List<Sharing> getSharingList() {
		return sharingList;
	}

	public void setSharingList(List<Sharing> sharingList) {
		this.sharingList = sharingList;
		notifyDataSetChanged();
	}

	public void addSharingList(List<Sharing> sharingList) {
		if (this.sharingList == null) {
			this.sharingList = new ArrayList<Sharing>();
		}
		this.sharingList.addAll(sharingList);
		notifyDataSetChanged();
	}
	
	

	@Override
	public int getCount() {
		return sharingList == null ? 0 : sharingList.size();
	}

	@Override
	public Sharing getItem(int position) {
		return sharingList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	// 每个convert view都会调用此方法，获得当前所需要的view样式
	@Override
	public int getItemViewType(int position) {
		switch (MomentsTypeEnum.getEnumFromString(sharingList.get(position)
				.getType())) {
		case TEXT:
			return TEXT;
		case IMAGE:
			return IMAGE;
		case URL:
			break;
		case VIDEO:
			break;
		case NULL:
			break;
		}
		return TEXT;// 默认为text
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		if (convertView ==null) {
			convertView = inflater.inflate(R.layout.item_personal_zone_image,
					parent, false);
		}
		View view1 = ViewHolder.get(convertView, R.id.view1);
		ImageView firstImage = ViewHolder.get(convertView,
				R.id.iv_first_only_image);
		// 改成日和 月 分开
		TextView time_day = ViewHolder
				.get(convertView, R.id.tv_moment_time_day);

		time_day.setTypeface(tf1);
		TextView time_month = ViewHolder.get(convertView,
				R.id.tv_moment_time_month);
		TextView content = ViewHolder.get(convertView, R.id.tv_text_content);
		content.setText(sharingList.get(position).getComment());

		calendar.setTimeInMillis(sharingList.get(position).getDate());
		if (position>i) {//往下滑的时候
		if (position == 0) {//第一个不显示上面那条线，并且记录month和day
			this.month = calendar.get(Calendar.MONTH) + 1;
			this.day = calendar.get(Calendar.DAY_OF_MONTH);
			view1.setVisibility(View.GONE);
			hashMap.put(position,calendar.get(Calendar.MONTH) + 1+"&"+calendar.get(Calendar.DAY_OF_MONTH));
		} else {//每一个与前一个比较时间
			if (calendar.get(Calendar.MONTH) + 1 == month
					&& calendar.get(Calendar.DAY_OF_MONTH) == day) {
				time_month.setVisibility(View.INVISIBLE);
				time_day.setVisibility(View.INVISIBLE);
				view1.setVisibility(View.GONE);
			} else {
				time_month.setVisibility(View.VISIBLE);
				time_day.setVisibility(View.VISIBLE);
				view1.setVisibility(View.VISIBLE);
				this.month = calendar.get(Calendar.MONTH) + 1;
				this.day = calendar.get(Calendar.DAY_OF_MONTH);
				//时间不同记录起来
				hashMap.put(position,calendar.get(Calendar.MONTH) + 1+"&"+calendar.get(Calendar.DAY_OF_MONTH));
			}
		}
		i++;		
	}
		else {//往上滑的时候
			Iterator iter = hashMap.entrySet().iterator();  
			while (iter.hasNext()) { //遍历已经储存的 
		         Map.Entry<Integer, String> entry = (Map.Entry<Integer, String>) iter.next();           
		         Integer key = entry.getKey();  
		         String value = entry.getValue();               
		         if (position ==key) {//当前位置与保存的位置值相同
		        	 if (position==0) {
		        		 view1.setVisibility(View.GONE);
					}else {
						 view1.setVisibility(View.VISIBLE);
					}
		        	 time_month.setVisibility(View.VISIBLE);
						time_day.setVisibility(View.VISIBLE);
						break;
				}
		         //与其它任意一个时间值相同时 
		         if (value.equals(calendar.get(Calendar.MONTH) + 1+"&"+calendar.get(Calendar.DAY_OF_MONTH))) {
		        		time_month.setVisibility(View.INVISIBLE);
						time_day.setVisibility(View.INVISIBLE);
						view1.setVisibility(View.GONE);
						break;
				}
			}  
		}
		String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		int month = calendar.get(Calendar.MONTH) + 1;
		if (month > 10) {
			time_month.setTextSize(24);
		}
		time_day.setText(day);
		time_month.setText(month_hashMap.get(month - 1) + "月");

		switch (type) {
		case TEXT:
			firstImage.setVisibility(View.GONE);
			break;
		case IMAGE:
			firstImage.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(
					sharingList.get(position).getImageUrls() == null ? null
							: PhotoUtil.getAllPhotoPath(sharingList
									.get(position).getImageUrls().get(0)),
					firstImage);
			break;
		}
		return convertView;
	}
}
