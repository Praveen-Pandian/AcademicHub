package com.example.academichub.student;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.academichub.R;
import com.example.academichub.responsePackage.AttendanceList;
import com.example.academichub.responsePackage.AttendanceReport;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailedAttendance extends AppCompatActivity {

    LinearLayout layout;

    public int dpToPx(int val){
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float) val * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_attendance);
        layout = findViewById(R.id.attendance_details);

        String cid = getIntent().getStringExtra("cid");
        List<AttendanceList> lst = (List<AttendanceList>) getIntent().getSerializableExtra("data");
        Log.d("user Data",lst.toString());

        float total = 0, present = 0, absent = 0;
        int percent = 0;
        for (AttendanceList item1 : lst) {
            total = total + 1;
            if (item1.getAttendance())
                present = present + 1;
            else
                absent = absent + 1;
        }
        percent = (total == 0) ? 0 : Math.round((present /  total) * 100);

        CardView cardView = new CardView(getApplicationContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
                );
        cardParams.setMargins(dpToPx(10), dpToPx(10),dpToPx(10),dpToPx(10));
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(dpToPx(8));
        cardView.setCardElevation(dpToPx(20));
        cardView.setCardBackgroundColor(getColor(R.color.white));

        // Creating LinearLayout inside CardView
        LinearLayout innerLayout = new LinearLayout(getApplicationContext());
        innerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setPadding(dpToPx(5),dpToPx(5),dpToPx(5),dpToPx(5));
        innerLayout.setBackgroundColor(getColor(R.color.white));

        // Creating TextView
        TextView courseCodeTextView = new TextView(getApplicationContext());
        courseCodeTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        courseCodeTextView.setText(cid);
        courseCodeTextView.setTextSize(dpToPx(7));
        courseCodeTextView.setTextColor(getResources().getColor(R.color.primary));
        courseCodeTextView.setPadding(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));
        courseCodeTextView.setBackgroundResource(R.drawable.border_bottom);
        innerLayout.addView(courseCodeTextView);


        // Creating LinearLayout for Total, Present, Absent
        LinearLayout infoLayout = new LinearLayout(getApplicationContext());
        infoLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.8f));
        infoLayout.setOrientation(LinearLayout.HORIZONTAL);
        infoLayout.setWeightSum(1);

        // Creating Total, Present, Absent TextViews
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                0.4f);
        infoParams.gravity = Gravity.CENTER_VERTICAL;

        LinearLayout inner_infoLayout = new LinearLayout(getApplicationContext());
        inner_infoLayout.setLayoutParams(infoParams);
        inner_infoLayout.setPadding(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));
        inner_infoLayout.setOrientation(LinearLayout.VERTICAL);
        inner_infoLayout.setGravity(Gravity.CENTER_VERTICAL);

        TextView totalTextView = new TextView(getApplicationContext());
        totalTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        totalTextView.setText("Total : " + (int)total);
        totalTextView.setPadding(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));

        TextView presentTextView = new TextView(getApplicationContext());
        presentTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        presentTextView.setText("Present : " + (int) present);
        presentTextView.setPadding(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));

        TextView absentTextView = new TextView(getApplicationContext());
        absentTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        absentTextView.setText("Absent : " + (int)absent);
        absentTextView.setPadding(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));

        inner_infoLayout.addView(totalTextView);
        inner_infoLayout.addView(presentTextView);
        inner_infoLayout.addView(absentTextView);

        infoLayout.addView(inner_infoLayout);


        // Creating PieChart
        PieChart pieChart = new PieChart(getApplicationContext());
        pieChart.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        ArrayList<Integer> colors = new ArrayList<>();
        if(total > 0) {
            //initializing data
            Map<String, Integer> hashMap = new HashMap<>();
            hashMap.put("Present", (int) present);
            hashMap.put("Absent", (int) absent);

            //initializing colors for the entries

            colors.add(getColor(R.color.primary));
            colors.add(getColor(R.color.red));

            //input data and fit data into pie chart entry
            for (String type : hashMap.keySet()) {
                pieEntries.add(new PieEntry(hashMap.get(type).intValue(), type));
            }
        }
        else{
            pieEntries.add(new PieEntry(100f,""));
            colors.add(getColor(R.color.gray));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries,"");
        //setting text size of the value
        pieDataSet.setValueTextSize(12f);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        pieChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        pieChart.setCenterTextSize(dpToPx(8));
        pieChart.setCenterText(percent + "%");
        pieChart.setHoleRadius(70f);
        pieChart.setHoleColor(Color.parseColor("#ffffff"));
        pieChart.setTransparentCircleRadius(70f);
        pieChart.setDrawEntryLabels(false);
        pieChart.setData(pieData);
        pieChart.invalidate();

        LinearLayout.LayoutParams pie_infoParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                0.6f);

        LinearLayout inner_infoLayout_pie = new LinearLayout(getApplicationContext());
        inner_infoLayout_pie.setLayoutParams(pie_infoParams);
        inner_infoLayout_pie.setPadding(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));
        inner_infoLayout_pie.addView(pieChart);
        infoLayout.addView(inner_infoLayout_pie);

        innerLayout.addView(infoLayout);

        cardView.addView(innerLayout);

        layout.addView(cardView);

        int i=-1;
        AttendanceList item = null;
        for (i=0;i<=lst.size();i++) {
            if (i > 0)
                item = lst.get(i-1);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1);
            CardView card = new CardView(getApplicationContext());
            card.setCardBackgroundColor(i==0 ? getColor(R.color.primary) : Color.WHITE);
            card.setLayoutParams(params);
            LinearLayout child_layout = new LinearLayout(getApplicationContext());
//            if(i>0)
//                child_layout.setBackground(getDrawable(R.drawable.attendance_content));
            child_layout.setLayoutParams(params);
            child_layout.setPadding(0,20,0,20);

            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1);
            params1.weight = 0.3f;

            TextView txt = new TextView(getApplicationContext());
            txt.setText(i==0 ? "SNO" : Integer.toString(i));
            txt.setLayoutParams(params1);
            txt.setTextSize(15);
            txt.setGravity(Gravity.CENTER);
            txt.setTextColor(getColor(i==0 ? R.color.white : R.color.black));
            txt.setBackgroundColor(getColor(i==0 ? R.color.primary : R.color.white));
//            txt.setBackground(getDrawable(R.drawable.attendance_content));
            child_layout.addView(txt);

            TextView txt1 = new TextView(getApplicationContext());
            txt1.setText(i==0 ? "DATE" : item.getDate());
            txt1.setTextSize(15);
            txt1.setLayoutParams(params1);
            txt1.setGravity(Gravity.CENTER);
            txt1.setTextColor(getColor(i==0 ? R.color.white : R.color.black));
            txt1.setBackgroundColor(getColor(i==0 ? R.color.primary : R.color.white));
//            txt1.setBackground(getDrawable(R.drawable.attendance_content));
            child_layout.addView(txt1);

            TextView txt2 = new TextView(getApplicationContext());
            txt2.setText(i ==0 ? "STATUS" : item.getAttendance() ? "Present" : "Absent");
            txt2.setTextSize(15);
            if(i==0)
                txt2.setTextColor(getColor(R.color.white));
            else
                txt2.setTextColor((item.getAttendance() ? Color.GREEN : Color.RED));
            txt2.setLayoutParams(params1);
            txt2.setGravity(Gravity.CENTER);
            txt2.setBackgroundColor(getColor(i==0 ? R.color.primary : R.color.white));

//            txt2.setBackground(getDrawable(R.drawable.attendance_content));

            child_layout.addView(txt2);
            if(i != 0)
            child_layout.setBackgroundResource(R.drawable.border_bottom);

            card.addView(child_layout);
            layout.addView(card);
        }
    }
}