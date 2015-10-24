package com.kinth.football.util.pinyin;

import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.kinth.football.R;

public class TestContactActivity extends Activity {
	/** Called when the activity is first created. */

	private ListView lvContact;
	private SideBar indexBar;
	private WindowManager mWindowManager;
	private TextView mDialogText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_pinyin);
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		findView();
	}

	private void findView() {
		lvContact = (ListView) this.findViewById(R.id.lvContact);
		lvContact.setAdapter(new ContactAdapter(this));
		indexBar = (SideBar) findViewById(R.id.sideBar);
		indexBar.setListView(lvContact);
		mDialogText = (TextView) LayoutInflater.from(this).inflate(
				R.layout.test_pinyin_list_position, null);
		mDialogText.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mWindowManager.addView(mDialogText, lp);
		indexBar.setTextView(mDialogText);
	}

	static class ContactAdapter extends BaseAdapter implements SectionIndexer {
		private Context mContext;
		private String[] mNicks;

		@SuppressWarnings("unchecked")
		public ContactAdapter(Context mContext) {
			this.mContext = mContext;
			this.mNicks = nicks;
			// 排序(实现了中英文混排)
			Arrays.sort(mNicks, new PinyinComparator());
		}

		@Override
		public int getCount() {
			return mNicks.length;
		}

		@Override
		public Object getItem(int position) {
			return mNicks[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final String nickName = mNicks[position];
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.test_pinyin_contact_item, null);
				viewHolder = new ViewHolder();
				viewHolder.tvCatalog = (TextView) convertView
						.findViewById(R.id.contactitem_catalog);
				viewHolder.ivAvatar = (ImageView) convertView
						.findViewById(R.id.contactitem_avatar_iv);
				viewHolder.tvNick = (TextView) convertView
						.findViewById(R.id.contactitem_nick);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			String catalog = PingYinUtil.converterToFirstSpell(nickName)
					.substring(0, 1);
			if (position == 0) {
				viewHolder.tvCatalog.setVisibility(View.VISIBLE);
				viewHolder.tvCatalog.setText(catalog);
			} else {
				String lastCatalog = PingYinUtil.converterToFirstSpell(
						mNicks[position - 1]).substring(0, 1);
				if (catalog.equals(lastCatalog)) {
					viewHolder.tvCatalog.setVisibility(View.GONE);
				} else {
					viewHolder.tvCatalog.setVisibility(View.VISIBLE);
					viewHolder.tvCatalog.setText(catalog);
				}
			}

			viewHolder.ivAvatar.setImageResource(R.drawable.default_avatar);
			viewHolder.tvNick.setText(nickName);
			return convertView;
		}

		static class ViewHolder {
			TextView tvCatalog;// 目录
			ImageView ivAvatar;// 头像
			TextView tvNick;// 昵称
		}

		@Override
		public int getPositionForSection(int section) {
			for (int i = 0; i < mNicks.length; i++) {
				String l = PingYinUtil.converterToFirstSpell(mNicks[i])
						.substring(0, 1);
				char firstChar = l.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public int getSectionForPosition(int position) {
			return 0;
		}

		@Override
		public Object[] getSections() {
			return null;
		}
	}
	/**
	 * 昵称
	 */
	private static String[] nicks = {  "北风", "张山", "李四", "欧阳锋", "郭靖",
			"黄蓉", "杨过", "凤姐", "芙蓉姐姐", "移联网", "樱木花道", "风清扬", "张三丰", "梅超风","阿雅" };

}