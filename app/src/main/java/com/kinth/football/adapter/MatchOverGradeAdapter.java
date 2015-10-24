package com.kinth.football.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.kinth.football.R;
import com.kinth.football.bean.GradeNumber;
import com.kinth.football.config.PlayerPositionEnum;
import com.kinth.football.ui.match.MatchOverGradeActivity.HasGrade;
import com.kinth.football.util.PhotoUtil;
import com.kinth.football.util.PictureUtil;
import com.kinth.football.util.ViewHolder;
import com.kinth.football.view.RoundImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.RatingBar.OnRatingBarChangeListener;

/**
 * 赛后评分的adapter
 * 
 * @author Sola
 *
 */
public class MatchOverGradeAdapter extends BaseAdapter {
	private List<HasGrade> dataList;
	private LayoutInflater mInflater;
	private Map<String, GradeNumber> gradeValueMap = new HashMap<String, GradeNumber>();//对球员的评分

	public MatchOverGradeAdapter(LayoutInflater mInflater,
			List<HasGrade> dataList) {
		super();
		this.dataList = dataList;
		this.mInflater = mInflater;
		initGradeValue();
	}

	private void initGradeValue() {
		for(HasGrade hasGrade : dataList){//初始化默认值
			GradeNumber gradeNumber = new GradeNumber();
			gradeNumber.setPlayerUuid(hasGrade.getGrade().getUuid());
			gradeNumber.setSkill(3);
			gradeNumber.setMorality(3);
			gradeValueMap.put(hasGrade.getGrade().getUuid(), gradeNumber);
		}
	}

	public void setDataList(List<HasGrade> dataList) {
		this.dataList = dataList;
		notifyDataSetChanged();
	}
	
	/**
	 * 获取评分数值
	 * @return 
	 */
	public Map<String, GradeNumber> getGradeValue(){
		return gradeValueMap;
	}

	@Override
	public int getCount() {
		return dataList == null ? 0 : dataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
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
					R.layout.activity_matchover_grade_lv, parent, false);
		}
		TextView grade_player_name = ViewHolder.get(convertView,
				R.id.grade_player_name);
		TextView grade_player_position = ViewHolder.get(convertView,
				R.id.grade_player_position);
		RoundImageView grade_player_avatar = ViewHolder.get(convertView,
				R.id.grade_player_avatar);
		final RatingBar grade_skill = ViewHolder.get(convertView, R.id.grade_skill);// 球技
		final RatingBar grade_character = ViewHolder.get(convertView,
				R.id.grade_character);// 球品

		grade_player_name.setText(StringUtils.defaultIfEmpty(
				dataList.get(position).getGrade().getName(), ""));
		switch(PlayerPositionEnum.getEnumFromString(dataList.get(position).getGrade().getPosition())){
		case NULL:
		default:
			grade_player_position.setText("无");
			break;
		case GK://门将:GK: 
			grade_player_position.setText("门将");
			break;
		case CB://中后卫:CB: 
			grade_player_position.setText("中后卫");
			break;
		case LCB://左后卫:LCB:
			grade_player_position.setText("左后卫");
			break;
		case RCB://:RCB: 
			grade_player_position.setText("右后卫");
			break;
		case LWB://:LWB: 
			grade_player_position.setText("左边后卫");
			break;
		case RWB://:RWB:
			grade_player_position.setText("右边后卫");
			break;
		case CDM://:CDM: 
			grade_player_position.setText("后腰");
			break;
		case LCM://:LCM: 
			grade_player_position.setText("左前卫");
			break;
		case RCM://:RCM: 
			grade_player_position.setText("右前卫");
			break;
		case CM://:CM: 
			grade_player_position.setText("前卫");
			break;
		case LWM://:LWM: 
			grade_player_position.setText("左边前卫");
			break;
		case RWM://:RWM: 
			grade_player_position.setText("右边前卫");
			break;
		case CAM://:CAM: 
			grade_player_position.setText("前腰");
			break;
		case LF://:LF: 
			grade_player_position.setText("左前锋");
			break;
		case RF://:RF: 
			grade_player_position.setText("右前锋");
			break;
		case CF://:CF: 
			grade_player_position.setText("前锋");
			break;
		case ST://:ST: 
			grade_player_position.setText("中锋");
			break;
		case SS://:SS:
			grade_player_position.setText("影子前锋");
			break;
		case LW://:LW: 
			grade_player_position.setText("左边锋");
			break;
		case RW:
			grade_player_position.setText("右边锋");
			break;
		}
		PictureUtil.getMd5PathByUrl(
				PhotoUtil.getThumbnailPath(dataList.get(position).getGrade()
						.getPicture()), grade_player_avatar,
						new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_default_head)
				.showImageForEmptyUri(R.drawable.icon_default_head)
				.showImageOnFail(R.drawable.icon_default_head).build());
		if(dataList.get(position).isHasGrade()){//已经评价
			grade_skill.setIsIndicator(true);
			grade_character.setIsIndicator(true);
			grade_skill.setRating(dataList.get(position).getGrade().getSkill());
			grade_character.setRating(dataList.get(position).getGrade().getMorality());
		}else{//没有评价
			grade_skill.setIsIndicator(false);
			grade_character.setIsIndicator(false);
			GradeNumber gradeNumber = gradeValueMap.get(dataList.get(position).getGrade().getUuid());
			if(gradeNumber != null){//有评价过该球员
				grade_skill.setRating(gradeNumber.getSkill());
				grade_character.setRating(gradeNumber.getMorality());
			}else{
				grade_skill.setRating(3f);
				grade_character.setRating(3f);
			}
			grade_skill.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
	
						@Override
						public void onRatingChanged(RatingBar ratingBar,
								float rating, boolean fromUser) {
							String playerUuid = dataList.get(position).getGrade().getUuid();
							if(gradeValueMap.containsKey(playerUuid)){
								gradeValueMap.get(playerUuid).setSkill((int) rating);//强转
								gradeValueMap.get(playerUuid).setMorality((int) grade_character.getRating());
							}else{
								GradeNumber gradeNumber = new GradeNumber();
								gradeNumber.setPlayerUuid(playerUuid);
								gradeNumber.setSkill((int) rating);
								gradeNumber.setMorality((int) grade_character.getRating());
								gradeValueMap.put(playerUuid, gradeNumber);
							}
						}
					});
			grade_character.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
				
				@Override
				public void onRatingChanged(RatingBar ratingBar, float rating,
						boolean fromUser) {
					String playerUuid = dataList.get(position).getGrade().getUuid();
					if(gradeValueMap.containsKey(playerUuid)){
						gradeValueMap.get(playerUuid).setMorality((int) rating);//强转
						gradeValueMap.get(playerUuid).setSkill((int) grade_skill.getRating());
					}else{
						GradeNumber gradeNumber = new GradeNumber();
						gradeNumber.setPlayerUuid(playerUuid);
						gradeNumber.setSkill((int) grade_skill.getRating());
						gradeNumber.setMorality((int) rating);
						gradeValueMap.put(playerUuid, gradeNumber);
					}
				}
			});
		}
		return convertView;
	}

}
