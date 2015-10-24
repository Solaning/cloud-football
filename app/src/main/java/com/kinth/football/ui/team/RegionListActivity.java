package com.kinth.football.ui.team;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kinth.football.R;
import com.kinth.football.bean.RegionBean;
import com.kinth.football.dao.RegionDao;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;
import com.kinth.football.ui.BaseActivity;
import com.kinth.football.util.ViewHolder;

/**
 * 指定城市下面的区县
 * @author Botision.Huang
 * Date: 2015-4-6
 * Descp:
 */
public class RegionListActivity extends BaseActivity {

	public static final String REGION_BEAN = "REGION_BEAN";
	
	private ListView select_regionlist;
	private int cityId = -1;
	
	private LayoutInflater inflater;
	private LinearLayout regionlist_line;
	
	private List<RegionBean> regionList = null;
	
	private ImageButton nav_left = null;
	private TextView nav_title;
	RegionAdatper adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.region_select_layout);
		
		nav_left = (ImageButton)this.findViewById(R.id.nav_left);
		nav_title = (TextView)this.findViewById(R.id.nav_title);
		regionlist_line = (LinearLayout)this.findViewById(R.id.regionlist_line);
		
		// 設置背景
		Bitmap background = SingletonBitmapClass.getInstance()
						.getBackgroundBitmap();
		ViewCompat.setBackground(regionlist_line, new BitmapDrawable(getResources(),
						background));

		nav_title.setText("请选择区县");
		
		cityId = this.getIntent().getExtras().getInt(CreateTeamActivity.REGION_SELECT);
		if(cityId == -1){
			return;
		}
		
		inflater = LayoutInflater.from(mContext);
		
		//根据城市ID获取得到其下的所有区县
		getRegionList();
		
		select_regionlist = (ListView)this.findViewById(R.id.select_regionlist);
		adapter = new RegionAdatper();
		select_regionlist.setAdapter(adapter);
		
		select_regionlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				//ShowToast("dfdf");
				RegionBean selectBean = (RegionBean)adapter.getItem(arg2);
				Intent intent = new Intent();
				intent.putExtra(REGION_BEAN, selectBean);
				RegionListActivity.this.setResult(RESULT_OK, intent);
				RegionListActivity.this.finish();
			}
		});
		
		nav_left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RegionListActivity.this.finish();
			}
		});
		
	}
	
	private void getRegionList(){
		RegionDao rDao = new RegionDao(mContext);
		regionList = rDao.getRegionListByProvinceID(cityId);
	}
	
	class RegionAdatper extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return regionList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return regionList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView == null){
				convertView = inflater.inflate(R.layout.regionlist_item, null);
			}
			TextView regionName = ViewHolder.get(convertView, R.id.name_region);
			regionName.setText(regionList.get(position).getName());
			return convertView;
		}
		
	}
	
	
}
