package com.kinth.football.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.kinth.football.R;
import com.kinth.football.bean.match.PlayerInTeam;
import com.kinth.football.dao.Team;
import com.kinth.football.manager.UserManager;
import com.kinth.football.network.NetWorkManager;
import com.kinth.football.ui.team.TeamPlayerInfo;
import com.kinth.football.util.DialogUtil;
import com.kinth.football.util.ErrorCodeUtil;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.ViewHolder;
import com.kinth.football.view.RoundImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 球队成员Adapter
 * @author Sola
 *
 */
public class TeamMemberListAdapter extends BaseAdapter{
	private Context mContext;
	private LayoutInflater mInflater;
	private List<PlayerInTeam> memberList; 
	private Team team;
	private boolean isCaptain;
	private ArrayAdapter<String> adapter;  
	private ProgressDialog progressDia;
	private  Dialog dialog;
	private ListView listView ;
	private Callback_change_number mcallback_change_number;
	private boolean isNeedToRefresh;   //是否需要更新
	private List<String> list_number;  //这里用String 不用Integer -- 因为 list.remove方法传值混淆
	//加入是否是123队长的判断 -控制设置号码
	public TeamMemberListAdapter(Context mContext, List<PlayerInTeam> memberList,Team team,boolean isCaptain) {
		super(); 
		this.mContext = mContext;
		this.memberList = memberList;
		this.team = team;
		this.isCaptain = isCaptain;
		this.mInflater = LayoutInflater.from(mContext);
		
		list_number = new ArrayList<String>();  //数据集合
		for (int i = 1; i < 100; i++) {
			list_number.add(i + "");
		}
		if (memberList != null) {
			for (int i = 0; i < memberList.size(); i++) { //去除已经选择了的数字
				if (list_number.contains(memberList.get(i).getJerseyNo() + "")) {
					list_number.remove(memberList.get(i).getJerseyNo() + "");
				}
			}
		}

		dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(true);
		View view = (View) mInflater.inflate(
				R.layout.dialog_player_number_select, null);
		listView = (ListView) view.findViewById(R.id.lv_dialog_number);
		dialog.setContentView(view);
//		adapter = new ArrayAdapter<String>(mContext,
//				android.R.layout.simple_list_item_1, list_number);
		adapter = new ArrayAdapter<String>(mContext,
				R.layout.item_number_list, list_number);
		
		listView.setAdapter(adapter);

	}
	public void change_number(Callback_change_number mcallback_change_number) {
		this.mcallback_change_number = mcallback_change_number;
	}
	
	public interface Callback_change_number{
		public void change_number(boolean isNeedToRefresh,List<PlayerInTeam> memberList);
	}
	
	public List<PlayerInTeam> getMemberList() {
		return memberList;
	}

	public void setMemberList(List<PlayerInTeam> memberList) {
		this.memberList = memberList;
		if (memberList!=null) {
			for (int i = 0; i < memberList.size(); i++) {
				if (list_number.contains(memberList.get(i).getJerseyNo()+"")) {
					list_number.remove(memberList.get(i).getJerseyNo()+"");
				}
			}
		}
		notifyDataSetChanged();
	}
	
	public void addMemberListBean(PlayerInTeam teamMemberResponse){
		if(this.memberList == null){
			this.memberList = new ArrayList<PlayerInTeam>();
		}
		this.memberList.add(teamMemberResponse);
		notifyDataSetChanged();
	}
    
	@Override
	public int getCount() {
		return memberList == null ? 0 : memberList.size();
	}

	@Override
	public PlayerInTeam getItem(int position) {
		return memberList == null ? null : memberList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.item_member_list, parent, false);
		}
		final TextView txt_number = ViewHolder.get(convertView, R.id.txt_number);
		if (memberList.get(position).getJerseyNo()!=null&&memberList.get(position).getJerseyNo()!=-1) {
			txt_number.setText(memberList.get(position).getJerseyNo()+"号");
		}
		if (isCaptain) { //是队长 可以点击设置或修改
			if (memberList.get(position).getJerseyNo()==null||memberList.get(position).getJerseyNo()==-1) {
				txt_number.setText("设置号码");
			}
		txt_number.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
					dialog.show();
					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							if (Integer.valueOf(list_number.get(arg2)) != memberList
									.get(position).getJerseyNo()) { // 判断是否跟原来的一样
								//设置或修改号码
								changeJerseyNo(memberList.get(position)
										.getPlayer().getUuid(),
										Integer.valueOf(list_number.get(arg2)),
										txt_number, position);
							}
						}
					});
				}
			});
		}
		
		RelativeLayout item = ViewHolder.get(convertView, R.id.rtl_invite_member_item);
		RoundImageView picture = ViewHolder.get(convertView,R.id.iv_invite_member_picture);
		TextView name = ViewHolder.get(convertView,R.id.tv_invite_member_name);
		
		PictureUtil.getMd5PathByUrl(PhotoUtil.getThumbnailPath(memberList.get(position).getPlayer().getPicture()), 
				picture,
				new DisplayImageOptions.Builder()
					.showImageOnLoading(
							R.drawable.icon_default_head)
					.showImageForEmptyUri(R.drawable.icon_default_head)
					.showImageOnFail(R.drawable.icon_default_head) // 默认队员头像
					.cacheInMemory(true).cacheOnDisk(true).build());
		name.setText(StringUtils.defaultIfBlank(memberList.get(position).getPlayer().getName(), ""));
		item.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, TeamPlayerInfo.class);
				intent.putExtra(TeamPlayerInfo.PLAYER_UUID, memberList.get(position).getPlayer().getUuid());
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}
	/**
	 * 设置或修改号码
	 * @param playerUuid
	 * @param jerseyNo
	 * @param txt_number
	 * @param position
	 */
	 private void changeJerseyNo(String playerUuid,final int jerseyNo,final TextView txt_number,final int position){
		   NetWorkManager.getInstance(mContext).changeJerseyNo(UserManager.getInstance(mContext).getCurrentUser().getToken(), team.getUuid(), playerUuid, jerseyNo, new Listener<Void>() {
			   ProgressDialog progressDia = ProgressDialog.show(mContext, "提示", "请稍后...", false,
						false);
				@Override
				public void onResponse(Void response) {
					DialogUtil.dialogDismiss(progressDia);
					if (memberList==null&&memberList.get(position)==null) {
						Toast.makeText(mContext, "修改号码异常", 1).show();
						return;
					}
					if (memberList.get(position).getJerseyNo()!=-1) {
						list_number.add(memberList.get(position).getJerseyNo()+"");	//增加号码 -- 选择其它号码前添加原号码
					}			
					//比较器  --按数字大小排序
					Comparator<String> comparator = new Comparator<String>() {
							@Override
							public int compare(String arg0, String arg1) {
								// TODO Auto-generated method stub
								//顺序比较的逻辑处理
								int i = Integer.valueOf(arg0)
										.compareTo(Integer.valueOf(arg1));
								return i;
							}
						};
						Collections.sort(list_number, comparator);//对数列集合排序
						memberList.get(position).setJerseyNo(jerseyNo);
						txt_number.setText(jerseyNo + "号");
						list_number.remove(jerseyNo + "");  //去掉被选择的号码

						isNeedToRefresh = true; //需要更新
						if (mcallback_change_number != null) {//回调  将isNeedToRefresh和memberList传给activity
							mcallback_change_number.change_number(
									isNeedToRefresh, memberList);
//							dialog.dismiss();
							DialogUtil.dialogDismiss(dialog);
						}
				}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						DialogUtil.dialogDismiss(progressDia);

						if (!NetWorkManager.getInstance(mContext)
								.isNetConnected()) {
							Toast.makeText(mContext, "当前网络不可用", 1).show();
						} else if (error.networkResponse == null) {
							// ShowToast("TeamInfoActivity-executeLikeTeam-服务器连接错误");
						} else if (error.networkResponse.statusCode == 401) {
							ErrorCodeUtil.ErrorCode401(mContext);
						} else if (error.networkResponse.statusCode == 404) {
	
						} else if (error.networkResponse.statusCode == 409) {

						}
					}
				});
	 }
}
