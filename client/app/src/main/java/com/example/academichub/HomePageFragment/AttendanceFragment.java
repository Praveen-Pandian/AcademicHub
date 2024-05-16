package com.example.academichub.HomePageFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.academichub.student.DetailedAttendance;
import com.example.academichub.R;
import com.example.academichub.RetrofitAPI;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AttendanceFragment extends Fragment {
    
    View view;
    LinearLayout parent_layout;

    public int dpToPx(int val){
        float density = getContext().getResources().getDisplayMetrics().density;
        return Math.round((float) val * density);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_attendance, container, false);

        parent_layout = view.findViewById(R.id.attendance_cards);

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://academichub-restapi.onrender.com/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        RetrofitAPI api = retrofit.create(RetrofitAPI.class);
//        SharedPreferences sh = getActivity().getSharedPreferences("userData", Context.MODE_PRIVATE);
//        Log.d("userData",sh.getString("id",null));
//        Call<List<AttendanceReport>> call = api.getAllAttendace(sh.getString("id",null));
//
//        call.enqueue(new Callback<List<AttendanceReport>>() {
//            @Override
//            public void onResponse(Call<List<AttendanceReport>> call, Response<List<AttendanceReport>> response) {
//                Log.d("userData",response.body().toString());
//                float total = 0,present=0,absent=0;
//                float percent = 0;
//                for (AttendanceReport item: response.body()) {
//                    total = 0;
//                    present = 0;
//                    absent = 0;
//                    percent = 0;
//                    List<AttendanceList> lst = item.getLst();
//                    for (AttendanceList item1:lst) {
//                        total = total + 1;
//                        if(item1.getAttendance())
//                            present = present + 1;
//                        else
//                            absent = absent + 1;
//                    }
//                    percent = total <= 0 ? 0 : ((present / total) * 100);
//                    CardView card = new CardView(getContext());
//                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dpToPx(130));
//                    params.setMargins(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));
//                    card.setLayoutParams(params);
//                    card.setRadius(40.0f);
//                    card.setElevation(40.0f);
//                    card.setCardBackgroundColor(getActivity().getColor(R.color.white));
//
//                    LinearLayout child_layout = new LinearLayout(getContext());
//                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                    child_layout.setLayoutParams(params1);
//                    child_layout.setOrientation(LinearLayout.HORIZONTAL);
//                    child_layout.setWeightSum(1.0f);
//
//                    LinearLayout graph_layout = new LinearLayout(getContext());
//                    graph_layout.setOrientation(LinearLayout.HORIZONTAL);
//                    graph_layout.setGravity(Gravity.CENTER);
//                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,0.35f);
//                    params2.setMargins(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));
//                    graph_layout.setLayoutParams(params2);
//                    GradientDrawable drawable = new GradientDrawable();
//                    drawable.setShape(GradientDrawable.OVAL);
//                    if(Math.round(percent) < 75)
//                        drawable.setStroke(dpToPx(10), Color.RED);
//                    else
//                        drawable.setStroke(dpToPx(10),getResources().getColor(R.color.primary));
//                    drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
//                    graph_layout.setBackground(drawable);
//                    TextView txt = new TextView(getContext());
//                    txt.setText(Math.round(percent) + "%");
//                    txt.setTextColor(getActivity().getColor(R.color.primary));
//                    txt.setTextSize(25.0f);
//                    graph_layout.addView(txt);
//                    child_layout.addView(graph_layout);
//
//                    LinearLayout content_layout = new LinearLayout(getContext());
//                    LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,0.65f);
//                    params3.setMargins(dpToPx(10),0,0,0);
//                    content_layout.setLayoutParams(params3);
//                    content_layout.setOrientation(LinearLayout.VERTICAL);
//
//                    TextView txt1 = new TextView(getContext());
//                    txt1.setText(item.getCid().split("_")[0]);
//                    txt1.setPadding(dpToPx(5),dpToPx(5),dpToPx(5),dpToPx(5));
//                    txt1.setTextSize(25.0f);
//                    txt1.setBackgroundColor(getActivity().getColor(R.color.primary));
//                    txt1.setTextColor(getActivity().getColor(R.color.white));
//                    content_layout.addView(txt1);
//
//                    TextView txt2 = new TextView(getContext());
//                    txt2.setText("Present : "+Math.round(present));
//                    txt2.setTextSize(15.0f);
//                    txt2.setTextColor(getActivity().getColor(R.color.black));
//                    txt2.setPadding(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));
//                    content_layout.addView(txt2);
//
//                    TextView txt3 = new TextView(getContext());
//                    txt3.setText("Absent : "+Math.round(absent));
//                    txt3.setTextSize(15.0f);
//                    txt3.setTextColor(getActivity().getColor(R.color.black));
//                    txt3.setPadding(dpToPx(10),0,dpToPx(10),dpToPx(10));
//                    content_layout.addView(txt3);
//
//                    child_layout.addView(content_layout);
//                    card.addView(child_layout);
//
//                    card.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent i = new Intent(getContext(), DetailedAttendance.class);
//                            i.putExtra("data",(Serializable) item.getLst());
//                            i.putExtra("cid",item.getCid().split("_")[0]);
//                            startActivity(i);
//                        }
//                    });
//                    parent_layout.addView(card);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<AttendanceReport>> call, Throwable t) {
//                Log.d("userData",t.getMessage());
//            }
//        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://academichub-restapi.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI api = retrofit.create(RetrofitAPI.class);
        SharedPreferences sh = getActivity().getSharedPreferences("userData", Context.MODE_PRIVATE);
        Log.d("userData",sh.getString("id",null));
        Call<List<AttendanceReport>> call = api.getAllAttendace(sh.getString("id",null));

        call.enqueue(new Callback<List<AttendanceReport>>() {
            @Override
            public void onResponse(Call<List<AttendanceReport>> call, Response<List<AttendanceReport>> response) {

                float total = 0, present = 0, absent = 0;
                int percent = 0;
                for (AttendanceReport item : response.body()) {
                    total = 0;
                    present = 0;
                    absent = 0;
                    percent = 0;
                    List<AttendanceList> lst = item.getLst();
                    for (AttendanceList item1 : lst) {
                        total = total + 1;
                        if (item1.getAttendance())
                            present = present + 1;
                        else
                            absent = absent + 1;
                    }
                        percent = (total == 0) ? 0 : Math.round((present /  total) * 100);
                    generateChart(item.getCid(),total,present,absent,percent,lst);
                }
            }

            @Override
            public void onFailure(Call<List<AttendanceReport>> call, Throwable t) {

            }
        });

        return view;
     }
    private void generateChart(String cid,float total,float present,float absent,int percent,List<AttendanceList> lst){

        // Creating CardView
        CardView cardView = new CardView(getContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0);
        cardParams.weight = 0.25f;
        cardParams.setMargins(dpToPx(10), dpToPx(10),dpToPx(10),dpToPx(10));
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(dpToPx(8));
        cardView.setCardElevation(dpToPx(20));

        // Creating LinearLayout inside CardView
        LinearLayout innerLayout = new LinearLayout(getContext());
        innerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setPadding(dpToPx(5),dpToPx(5),dpToPx(5),dpToPx(5));

        // Creating TextView
        TextView courseCodeTextView = new TextView(getContext());
        courseCodeTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        courseCodeTextView.setText(cid.split("_")[0]);
        courseCodeTextView.setTextSize(dpToPx(7));
        courseCodeTextView.setTextColor(getResources().getColor(R.color.primary));
        courseCodeTextView.setPadding(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));
        courseCodeTextView.setBackgroundResource(R.drawable.border_bottom);
        innerLayout.addView(courseCodeTextView);

        // Creating LinearLayout for Total, Present, Absent
        LinearLayout infoLayout = new LinearLayout(getContext());
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

        LinearLayout inner_infoLayout = new LinearLayout(getContext());
        inner_infoLayout.setLayoutParams(infoParams);
        inner_infoLayout.setPadding(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));
        inner_infoLayout.setOrientation(LinearLayout.VERTICAL);
        inner_infoLayout.setGravity(Gravity.CENTER_VERTICAL);

        TextView totalTextView = new TextView(getContext());
        totalTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        totalTextView.setText("Total : " + (int)total);
        totalTextView.setPadding(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));

        TextView presentTextView = new TextView(getContext());
        presentTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        presentTextView.setText("Present : " + (int) present);
        presentTextView.setPadding(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));

        TextView absentTextView = new TextView(getContext());
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
        PieChart pieChart = new PieChart(getContext());
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

            colors.add(getActivity().getColor(R.color.primary));
            colors.add(getActivity().getColor(R.color.red));

            //input data and fit data into pie chart entry
            for (String type : hashMap.keySet()) {
                pieEntries.add(new PieEntry(hashMap.get(type).intValue(), type));
            }
        }
        else{
            pieEntries.add(new PieEntry(100f,""));
            colors.add(getActivity().getColor(R.color.gray));
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

        LinearLayout inner_infoLayout_pie = new LinearLayout(getContext());
        inner_infoLayout_pie.setLayoutParams(pie_infoParams);
        inner_infoLayout_pie.setPadding(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));
        inner_infoLayout_pie.addView(pieChart);
        infoLayout.addView(inner_infoLayout_pie);

        innerLayout.addView(infoLayout);

        // Creating Button
        ImageButton viewMoreButton = new ImageButton(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;
        viewMoreButton.setLayoutParams(params);
//        viewMoreButton.setText("View More");
//        viewMoreButton.setGravity(Gravity.CENTER);
//        viewMoreButton.setTextColor(Color.WHITE);
        viewMoreButton.setBackgroundResource(R.drawable.baseline_keyboard_double_arrow_right_24);
        viewMoreButton.setBackgroundTintList(getResources().getColorStateList(R.color.primary));
        innerLayout.addView(viewMoreButton);

        ImageButton btn = new ImageButton(getContext());

        viewMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getContext(), DetailedAttendance.class);
                    i.putExtra("data",(Serializable) lst);
                    i.putExtra("cid",cid.split("_")[0]);
                    startActivity(i);
                }
            });

        cardView.addView(innerLayout);

        parent_layout.addView(cardView);

    }
}