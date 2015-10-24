package com.kinth.football.ui;

import java.util.ArrayList;
import java.util.List;

import com.kinth.football.R;
import com.kinth.football.config.PlayerPositionEnum2;
import com.kinth.football.singleton.SingletonBitmapClass;
import com.kinth.football.singleton.ViewCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HomeSearchPlayerSelectPositionActivity extends BaseActivity {
	public static final String RESULT_SELECT_POSITON = "RESULT_SELECT_POSITON";
  @Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_select_position_in_search);
	LinearLayout entireLayout = (LinearLayout)this.findViewById(R.id.entireLayout);
	// 設置背景
	Bitmap background = SingletonBitmapClass.getInstance()
			.getBackgroundBitmap();
	ViewCompat.setBackground(entireLayout, new BitmapDrawable(getResources(),
			background));
	TextView tvTitle = (TextView) findViewById(R.id.nav_title);
	tvTitle.setText("选择位置");
	
	ImageButton ibLeft = (ImageButton) findViewById(R.id.nav_left);
	ibLeft.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO 自动生成的方法存根
			finish();
		}
		
	});
	ListView lv_positon = (ListView)this.findViewById(R.id.lv_positon);

	List<String> list = new ArrayList<String>();
	final String[] position_value = {
			PlayerPositionEnum2.FW.getValue(),PlayerPositionEnum2.MF.getValue(),
			PlayerPositionEnum2.BF.getValue(),PlayerPositionEnum2.GK.getValue(),
			PlayerPositionEnum2.NULL.getValue()};
	String[] position = {PlayerPositionEnum2.FW.getValue()+PlayerPositionEnum2.FW.getName(),
			PlayerPositionEnum2.MF.getValue()+PlayerPositionEnum2.MF.getName(),
			PlayerPositionEnum2.BF.getValue()+PlayerPositionEnum2.BF.getName(),
			PlayerPositionEnum2.GK.getValue()+PlayerPositionEnum2.GK.getName(),
			"全部位置"
			};
	for (int i = 0; i < position.length; i++) {
		list.add(position[i]);
	}

	ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, 
			R.layout.item_select_positon,R.id.text1, list);
	lv_positon.setAdapter(adapter);
	lv_positon.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.putExtra(RESULT_SELECT_POSITON, position_value[arg2]);
			setResult(RESULT_OK, intent);
			finish();
		}
	});
}
}
