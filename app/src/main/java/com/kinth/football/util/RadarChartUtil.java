package com.kinth.football.util;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.kinth.football.R;

/**
 * 雷达图
 * @author Botision.Huang
 * Date: 2015-3-29
 * Descp:
 */
public class RadarChartUtil {
	
    private static String[] mParties = new String[] {
            "技能", " 技术", "速度", "意识 ", "球品 "
    };
	
	public static final void SetAttributeForRadarChart(Context mContext, RadarChart radarChart, Typeface tf){
		radarChart.setDescription("");

		radarChart.setWebLineWidth(1.5f);
		radarChart.setWebLineWidthInner(0.75f);
		radarChart.setWebAlpha(100);
		
//		MyMarkerView mv = new MyMarkerView(mContext, R.layout.custom_marker_view);
//		radarChart.setMarkerView(mv);

		setData(mContext, radarChart, tf);
		
		XAxis xAxis = radarChart.getXAxis(); // X轴
		xAxis.setTypeface(tf);
		xAxis.setTextSize(12f);
		xAxis.setTextColor(mContext.getResources().getColor(R.color.white_color_disable));
		//System.out.println("xAxis的值为==============" + new String(xAxis.toString()));
		
		/*if(xAxis.equals(mParties[0])){
			xAxis.setTextColor(mContext.getResources().getColor(R.color.white_color_disable));
		}else if(xAxis.equals(mParties[1])){
			xAxis.setTextColor(mContext.getResources().getColor(R.color.abc_search_url_text_normal));
		}else{
			xAxis.setTextColor(mContext.getResources().getColor(R.color.base_tab_indicator_text_color));
		}*/
		

/*		YAxis yAxis = radarChart.getYAxis(); // Y轴
		yAxis.setTypeface(tf);
		yAxis.setLabelCount(5);
		yAxis.setTextSize(9f);
		yAxis.setStartAtZero(true);*/

		Legend l = radarChart.getLegend(); // 图例
		l.setPosition(LegendPosition.RIGHT_OF_CHART);
		l.setTypeface(tf);
		l.setXEntrySpace(0f);
		l.setYEntrySpace(0f);
		
	}

    private static void setData(Context mContext, RadarChart radarChart, Typeface tf) {

        float mult = 200;
        int cnt = 5;

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        for (int i = 0; i < cnt; i++) {
            yVals1.add(new Entry((float)mult, i));
        }

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < cnt; i++)
            xVals.add(mParties[i % mParties.length]);

        //雷达数据集合set1（绿色部分）
        RadarDataSet set1 = new RadarDataSet(yVals1, "  ");
        set1.setColor(mContext.getResources().getColor(R.color.radarchart_bg));
        set1.setDrawFilled(true); 
        set1.setLineWidth(1f);     //线条宽度

        ArrayList<RadarDataSet> sets = new ArrayList<RadarDataSet>();
        sets.add(set1);

        RadarData data = new RadarData(xVals, sets);
        data.setValueTypeface(tf);
        data.setValueTextSize(8f);
        data.setDrawValues(false);

        radarChart.setData(data);
        //去掉Y轴坐标数据显示
        radarChart.getYAxis().setEnabled(!radarChart.getYAxis().isEnabled());  
        //去掉图例显示
        radarChart.getLegend().setEnabled(!radarChart.getLegend().isEnabled());
        radarChart.invalidate();
    }
	
}
