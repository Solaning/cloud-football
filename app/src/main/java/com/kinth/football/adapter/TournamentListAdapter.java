package com.kinth.football.adapter;

import java.util.List;

import com.kinth.football.CustomApplcation;
import com.kinth.football.R;
import com.kinth.football.bean.match.Tournament;
import com.kinth.football.ui.CommonWebViewActivity;
import com.kinth.football.ui.LeagueWebViewActivity;
import com.kinth.football.ui.match.TournamentActivity;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.ViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
/**
 * @author zyq
 * 
 * 联赛列表适配器
 */
public class TournamentListAdapter extends BaseAdapter{
	private Context mContext;
	private LayoutInflater mInflater;
	private List<Tournament> tournamentList;
	private LinearLayout.LayoutParams params;
	private String link_in_where;
	private String playerUuid;
	
	public TournamentListAdapter(Context context,List<Tournament> tournamentBeanList,String link_in_where,
			String playerUuid){
		this.mContext = context;
		this.tournamentList = tournamentBeanList;
		this.link_in_where = link_in_where;
		this.playerUuid = playerUuid;
		this.mInflater = LayoutInflater.from(mContext);
		int screenWidth = CustomApplcation.getInstance().getParameters().getmScreenWidth();
		
		params = new LinearLayout.LayoutParams(screenWidth,
				Math.round(screenWidth / 2.25f));//图片的宽高比例
		params.gravity = Gravity.CENTER;
	}
	
	public synchronized void setTournamentList(List<Tournament> tournamentList) {
		this.tournamentList = tournamentList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tournamentList == null ? 0 : tournamentList.size();
	}
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.item_tournament_list, parent, false);
		}

		ImageView img_tournament = ViewHolder.get(convertView, R.id.img_tournament);
		img_tournament.setLayoutParams(params);
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getAllPhotoPath(tournamentList.get(position).getPicture()),
						img_tournament,
				new DisplayImageOptions.Builder()
						.showImageOnLoading(
								R.drawable.tournament)
						.showImageForEmptyUri(R.drawable.tournament)
						.showImageOnFail(R.drawable.tournament) // 默认球队队徽
						.cacheInMemory(true).cacheOnDisk(true).build());
		img_tournament.setOnClickListener(new OnClickListener() {
			// 跳转到CommonWebViewActivity
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mContext,
						LeagueWebViewActivity.class);
				intent.putExtra(LeagueWebViewActivity.INTENT_TITLE,
						tournamentList.get(position).getName());// 传联赛名字
				intent.putExtra(LeagueWebViewActivity.INTENT_SHORT_NAME,
						tournamentList.get(position).getShortName());// 传联赛名字
				String url = null;
				// 3种url 根据从哪里点进来
				if (link_in_where.equals(TournamentActivity.LINK_IN_HOMEPAGE)) {
					url = tournamentList.get(position).getLinkInHomePage();
				}else if (link_in_where.equals(TournamentActivity.LINK_IN_PERSONINFO)) {
					if(!TextUtils.isEmpty(playerUuid)){
						url = tournamentList.get(position).getLinkInPersonInfo() + "/" + playerUuid;
					} else{
						url = tournamentList.get(position).getLinkInPersonInfo();
					}
				}else if (link_in_where.equals(TournamentActivity.LINK_IN_MATCH)) {
					url = tournamentList.get(position).getLinkInMatch();
				}
				intent.putExtra(CommonWebViewActivity.INTENT_URL, url);
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}

}
