package com.kinth.football.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.kinth.football.R;

/**
 * 雷达图 & 折线图
 * @author Botision.Huang Date: 2015-3-29 
 * Descp: 设置图表的属性、数值
 */

public class AndroidChartUtil {

	private static String[] mParties = new String[] { // 这里“防守”右边多加了几个空格之后，雷达图中的字便不会被遮挡
	"进攻", "防守   ", "体能", "技术", "  侵略性" };

	public static void SetAttributeForRadarChart(Context mContext,
			RadarChart radarChart, List<Integer> radarValues, Typeface tf1) {

		radarChart.setNoDataTextDescription("目前没有相关的数据");
		radarChart.setDescription("");
		radarChart.setWebLineWidth(1.5f);
		radarChart.setWebLineWidthInner(0.75f);
		radarChart.setWebAlpha(150); 
		radarChart.setTouchEnabled(false);
		radarChart.setDrawingCacheEnabled(false);
		radarChart.setHighlightEnabled(false);
		radarChart.setFilterTouchesWhenObscured(false);
		radarChart.setFocusable(false);
		radarChart.setActivated(false);
		radarChart.setAlwaysDrawnWithCacheEnabled(false);
		radarChart.setClickable(false);
		radarChart.setFocusable(false);

		setRadarChartData(mContext, radarChart, radarValues, tf1);

		XAxis xAxis = radarChart.getXAxis(); // X轴
		xAxis.setTypeface(tf1);
		xAxis.setTextSize(10f);
		xAxis.setTextColor(mContext.getResources().getColor(
				R.color.white_color_disable));
		xAxis.setDrawGridLines(false);
		xAxis.setDrawAxisLine(false);
		xAxis.setSpaceBetweenLabels(0);
		xAxis.setXOffset(0.1f);

		YAxis yAxis = radarChart.getYAxis(); // Y轴
		yAxis.setTypeface(tf1);
		yAxis.setLabelCount(5);
		yAxis.setTextSize(9f);
		yAxis.setAxisMaxValue(100f);
		yAxis.setAxisMinValue(100f);
		yAxis.setStartAtZero(true);
		yAxis.setValueFormatter(new ValueFormatter() {
			@Override
			public String getFormattedValue(float value) {
				// TODO Auto-generated method stub
				return ((Integer) ((int) value)).toString();
			}
		});

		// 去掉Y轴坐标数据显示
		radarChart.getYAxis().setEnabled(false);
		// 去掉图例显示
		radarChart.getLegend().setEnabled(false);
		
		radarChart.invalidate();
	}

	private static void setRadarChartData(Context mContext,
			RadarChart radarChart, List<Integer> radarValues, Typeface tf1) {
		int cnt = 5;

		ArrayList<Entry> yVals1 = new ArrayList<Entry>();
		// 如果所有数据都为0,说明该用户还未参加过任何"联赛"，或者还未有数据，此时添加虚拟的数据
		if (radarValues != null && radarValues.size() != 0) {
			radarChart.setVisibility(View.VISIBLE);
			for (int i = 0; i < cnt; i++) {
				yVals1.add(new Entry((float) radarValues.get(i), i));
			}
		} else {
			radarChart.setVisibility(View.VISIBLE);
			for (int i = 0; i < cnt; i++) {
				yVals1.add(new Entry((float) 70, i));
			}
		}

		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 0; i < cnt; i++)
			xVals.add(mParties[i % mParties.length]);

		// 雷达数据集合set1
		RadarDataSet set1 = new RadarDataSet(yVals1, "  ");
		set1.setColor(mContext.getResources().getColor(R.color.radarchart_bg));
		set1.setDrawFilled(true);
		set1.setLineWidth(3f); // 线条宽度
		set1.setValueFormatter(new ValueFormatter() {
			@Override
			public String getFormattedValue(float value) {
				// TODO Auto-generated method stub
				return ((Integer) ((int) value)).toString();
			}
		});

		ArrayList<RadarDataSet> sets = new ArrayList<RadarDataSet>();
		sets.add(set1);

		RadarData data = new RadarData(xVals, sets);
		data.setValueTypeface(tf1);
		data.setValueTextSize(8f);
		data.setDrawValues(false);
		data.setValueTextColor(mContext.getResources().getColor(
				R.color.radar_x_color));

		radarChart.setData(data);

		radarChart.invalidate();
	}

	public static final void SetAttributeForLineChartTwo(Context mContext,
			LineChart lineChart, Typeface tf, List<Integer> skills,
			List<Integer> moralitys, TextView nodata_two) {
		
		if (!lineChart.isEmpty()) {
			lineChart.clearValues();
		}

		lineChart.setDescription("");
		lineChart.setNoDataTextDescription("目前没有相关数据");
		lineChart.setTouchEnabled(false);
		lineChart.setDragEnabled(false);
		lineChart.setScaleEnabled(false);
		lineChart.setDrawGridBackground(false);
		lineChart.setPinchZoom(false);
		lineChart.setBackgroundColor(mContext.getResources().getColor(
				R.color.home_uplevel_bg_color));
		
		lineChart.setAlwaysDrawnWithCacheEnabled(false);
		lineChart.setDrawingCacheEnabled(false);
		lineChart.fitScreen();
        // 设置在Y轴上是否是从0开始显示  
//		mChart.animateX(2500);  //动画（顺着X轴方向）如果设置了动画，就无需再调用invalidate()去刷新

		setLineChartDataTwo(mContext, lineChart, skills, moralitys, nodata_two);

		XAxis xAxis = lineChart.getXAxis();
		xAxis.setTypeface(tf);
		xAxis.setTextSize(12f);
		xAxis.setTextColor(Color.WHITE);
		xAxis.setDrawGridLines(false);
		xAxis.setDrawAxisLine(false);
		xAxis.setSpaceBetweenLabels(1);
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

		YAxis leftYAxis = lineChart.getAxisLeft(); // 右边Y轴
		leftYAxis.setTypeface(tf);
		leftYAxis.setTextColor(mContext.getResources().getColor(
				R.color.base_color_text_white));
		leftYAxis.setAxisMaxValue(100f);
		leftYAxis.setValueFormatter(new ValueFormatter() {
			@Override
			public String getFormattedValue(float value) {
				// TODO Auto-generated method stub
				return ((Integer) ((int) value)).toString();
			}
		});
        // 设置在Y轴上是否是从0开始显示  
		leftYAxis.setStartAtZero(true);  

		Legend l = lineChart.getLegend(); // 图例
		l.setForm(LegendForm.LINE);
		l.setTypeface(tf);
		l.setTextSize(11f);
		l.setTextColor(Color.WHITE);
		l.setPosition(LegendPosition.BELOW_CHART_LEFT);

		// 隐藏图例
		lineChart.getLegend().setEnabled(false);
		// 隐藏右边Y轴
		lineChart.getAxisRight().setEnabled(false);
		// 隐藏X轴
		lineChart.getXAxis().setEnabled(false);
		
	}

	public static void setLineChartDataTwo(Context mContext,
			LineChart lineChart, List<Integer> skills, List<Integer> moralitys, TextView nodata_two) {

		ArrayList<String> xVals = new ArrayList<String>();
		for(int i = 0; i < 7; i++){   //测试
			xVals.add((i) + "");
		}
		
		ArrayList<Entry> yVals1 = new ArrayList<Entry>();
		ArrayList<Entry> yVals2 = new ArrayList<Entry>();
		if (null != skills && skills.size() != 0) {
			for (int i = 0; i < skills.size(); i++) {
				yVals1.add(new Entry((float) skills.get(i), i + 1));  //i+1,坐标点从x=1开始
				yVals2.add(new Entry((float) moralitys.get(i), i + 1));
			}
		} else {
			lineChart.setVisibility(View.GONE);
			nodata_two.setVisibility(View.VISIBLE);
		
/*			yVals1.add(new Entry((float)20, 1));  //测试
			yVals1.add(new Entry((float)85, 2));
			yVals1.add(new Entry((float)76, 3));
			yVals1.add(new Entry((float)50, 4));
			yVals1.add(new Entry((float)16, 5));
			
			yVals2.add(new Entry((float)56, 1));
			yVals2.add(new Entry((float)10, 2));
			yVals2.add(new Entry((float)30, 3));
			yVals2.add(new Entry((float)45, 4));
			yVals2.add(new Entry((float)78, 5));*/
		}

		LineDataSet set1 = new LineDataSet(yVals1, "DataSet 1");
		set1.setAxisDependency(AxisDependency.LEFT);   //此处应该多加注意，设置Y轴坐标点是依赖于左边Y轴，还是右边Y轴
		set1.setColor(mContext.getResources().getColor(R.color.skill_linecolor));
		set1.setCircleColor(Color.WHITE);
		set1.setLineWidth(2f);
		set1.setCircleSize(3f);
		set1.setHighLightColor(Color.rgb(244, 117, 117));
		set1.setDrawCircleHole(false);
		set1.setValueFormatter(new ValueFormatter() {
			@Override
			public String getFormattedValue(float value) {
				// TODO Auto-generated method stub
				return ((Integer) ((int) value)).toString();
			}
		});

		LineDataSet set2 = new LineDataSet(yVals2, "DataSet 2");
		set2.setAxisDependency(AxisDependency.LEFT);
		set2.setColor(mContext.getResources().getColor(R.color.moral_linecolor));
		set2.setCircleColor(Color.WHITE);
		set2.setLineWidth(2f);
		set2.setCircleSize(3f);
		set2.setDrawCircleHole(false);
		set2.setHighLightColor(Color.rgb(244, 117, 117));
		set2.setValueFormatter(new ValueFormatter() {
			@Override
			public String getFormattedValue(float value) {
				// TODO Auto-generated method stub
				return ((Integer) ((int) value)).toString();
			}
		});

		ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
		dataSets.add(set1);
		dataSets.add(set2);

		LineData data = new LineData(xVals, dataSets);
		data.setValueTextColor(Color.WHITE);
		data.setValueTextSize(9f);

		lineChart.setData(data);
		
		lineChart.invalidate();
	}

	public static final void SetAttributeForLineChartOne(Context mContext,
			LineChart mChart, Typeface tf, List<Integer> fiveData, TextView nodata_one) {

		if (!mChart.isEmpty()) {
			mChart.clearValues();
		}

		mChart.setDescription("");
		mChart.setNoDataTextDescription("目前没有相关数据");
		mChart.setTouchEnabled(false);
		mChart.setDragEnabled(false);
		mChart.setScaleEnabled(false);
		mChart.setDrawGridBackground(false);
		mChart.setPinchZoom(false);
		mChart.setBackgroundColor(mContext.getResources().getColor(
				R.color.home_uplevel_bg_color));
		
		mChart.setAlwaysDrawnWithCacheEnabled(false);
		mChart.setDrawingCacheEnabled(false);
		mChart.fitScreen();
        // 设置在Y轴上是否是从0开始显示  
//		mChart.animateX(2500);  //动画（顺着X轴方向）如果设置了动画，就无需再调用invalidate()去刷新

		setLineChartOneData(mContext, mChart, tf, fiveData,nodata_one);

		XAxis xAxis = mChart.getXAxis();
		xAxis.setTypeface(tf);
		xAxis.setTextSize(12f);
		xAxis.setTextColor(Color.WHITE);
		xAxis.setDrawGridLines(false);
		xAxis.setDrawAxisLine(false);
		xAxis.setSpaceBetweenLabels(1);
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		

		YAxis leftYAxis = mChart.getAxisLeft(); // 右边Y轴
		leftYAxis.setTypeface(tf);
		leftYAxis.setTextColor(mContext.getResources().getColor(
				R.color.base_color_text_white));
		leftYAxis.setAxisMaxValue(100f);
		leftYAxis.setValueFormatter(new ValueFormatter() {
			@Override
			public String getFormattedValue(float value) {
				// TODO Auto-generated method stub
				return ((Integer) ((int) value)).toString();
			}
		});
        // 设置在Y轴上是否是从0开始显示  
		leftYAxis.setStartAtZero(true);  

		Legend l = mChart.getLegend(); // 图例
		l.setForm(LegendForm.LINE);
		l.setTypeface(tf);
		l.setTextSize(11f);
		l.setTextColor(Color.WHITE);
		l.setPosition(LegendPosition.BELOW_CHART_LEFT);

		// 隐藏图例
		mChart.getLegend().setEnabled(false);
		// 隐藏右边Y轴
		mChart.getAxisRight().setEnabled(false);
		// 隐藏X轴
		mChart.getXAxis().setEnabled(false);
		
	}
	
	private static void setLineChartOneData(Context mContext, LineChart mChart,
			Typeface tf, List<Integer> fiveData, TextView nodata_one) {

		ArrayList<String> xVals = new ArrayList<String>(); // X轴
		for(int i = 0; i < 7; i++){  //固定总共7个坐标点，其中间五个用于显示
			xVals.add("11.1");
		}
		
		
		ArrayList<Entry> yVals1 = new ArrayList<Entry>(); // Y轴
		if (null != fiveData && fiveData.size() != 0) {
			for (int i = 0; i < fiveData.size(); i++) {
				yVals1.add(new Entry((float) fiveData.get(i), i + 1));
			}
		} else if (fiveData.size() == 0) { // 说明综合数据没有数据
			mChart.setVisibility(View.GONE);
			nodata_one.setVisibility(View.VISIBLE);  
/*			yVals1.add(new Entry((float)65, 1));   //测试
			yVals1.add(new Entry((float)78, 2));
			yVals1.add(new Entry((float)70, 3));
			yVals1.add(new Entry((float)86, 4));
			yVals1.add(new Entry((float)78, 5));*/
		}

		LineDataSet set1 = new LineDataSet(yVals1, " ");
		set1.setAxisDependency(AxisDependency.LEFT);
		set1.setColor(mContext.getResources().getColor(
				R.color.linechart_linecolor));
		set1.setCircleColor(Color.WHITE);
		set1.setLineWidth(2f);
		set1.setCircleSize(3f);
		set1.setDrawCircleHole(false);
		set1.setHighLightColor(Color.rgb(244, 117, 117));
		set1.setValueFormatter(new ValueFormatter() {
			@Override
			public String getFormattedValue(float value) {
				// TODO Auto-generated method stub
				return ((Integer) ((int) value)).toString();
			}
		});

		ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
		dataSets.add(set1);

		LineData data = new LineData(xVals, dataSets);
		data.setValueTextColor(Color.WHITE);
		data.setValueTextSize(9f);

		mChart.setData(data);
		
		mChart.invalidate();
	}

	// 四舍五入
	public static int roundNumber(float number) {
		return Math.round(number);
	}

}
