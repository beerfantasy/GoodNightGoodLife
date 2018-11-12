package com.kmutt.cs.goodnightgoodlife;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class SleepTrackActivity extends AppCompatActivity {

    private BarChart chart;
    private long[] avg_deep_sleep_in_week;
    private long avg_deepsleep;
    private TextView avg;
    private TextView day1;
    private TextView day2;
    private TextView day3;
    private TextView day4;
    private TextView day5;
    private TextView day6;
    private TextView day7;
    private String format = "%-45s%s";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleeptrack);
        setTitle("Deep Sleep Time");

        avg_deep_sleep_in_week = new long[7];

        chart = (BarChart) findViewById(R.id.bar_chart);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setTextColor(Color.WHITE);
        chart.getXAxis().setTextColor(Color.WHITE);


        ArrayList<BarEntry> list = new ArrayList<>();
        for(int i = 0; i < HomeActivity.deep_sleep.length; i++) {
            if(!HomeActivity.deep_sleep[i].matches("")){
            list.add(new BarEntry(i,Float.parseFloat(HomeActivity.deep_sleep[i])));
            avg_deepsleep += Float.parseFloat(HomeActivity.deep_sleep[i]);
            avg_deep_sleep_in_week[i] = (Long.parseLong(HomeActivity.deep_sleep[i])*100)/HomeActivity.sleep_time[i];}
        }

        avg_deepsleep = avg_deepsleep/HomeActivity.deep_sleep.length;


        BarDataSet set = new BarDataSet(list, "Data Set");
        set.setColor(Color.argb(150, 232, 240, 255));
        set.setDrawValues(true);

        BarData data = new BarData(set);
        data.setValueTextSize(0);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(HomeActivity.date_of_sleep));
        xAxis.setLabelRotationAngle(-90);
        Paint mPaint = chart.getRenderer().getPaintRender();
        mPaint.setShader(new SweepGradient(350,120,Color.parseColor("#64c8ff"),Color.parseColor("#2897ff")));
        chart.setData(data);
        chart.invalidate();
        chart.animateXY(1000,1000);
    }

    public String parseDate(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMM dd, yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


}
