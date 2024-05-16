package com.example.academichub.student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.academichub.R;
import com.example.academichub.RetrofitAPI;
import com.example.academichub.responsePackage.MarkSchema;
import com.example.academichub.responsePackage.StudentFacultyDB;
import com.example.academichub.responsePackage.StudentMark;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StudentMarkPage extends AppCompatActivity {

    // variable for our bar chart
    BarChart barChart;

    // variable for our bar data.
    BarData barData;

    // variable for our bar data set.
    BarDataSet barDataSet;

    // array list for storing entries.
    ArrayList barEntriesArrayList;
    List<MarkSchema> marks = new ArrayList<>();


    TextView txt1,txt2,txt3,txt4,txt5,txt6,txt7,btn1,btn2,btn3,loading,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_mark_page);
        barChart = findViewById(R.id.idBarChart);
        txt1 = findViewById(R.id.cat_1);
        txt2 = findViewById(R.id.assign_1);
        txt3 = findViewById(R.id.cat_2);
        txt4 = findViewById(R.id.assign_2);
        txt5 = findViewById(R.id.cat_3);
        txt6 = findViewById(R.id.assign_3);
        txt7 = findViewById(R.id.graph_title);
        btn1 = findViewById(R.id.graph_cat_1);
        btn2 = findViewById(R.id.graph_cat_2);
        btn3 = findViewById(R.id.graph_cat_3);
        loading = findViewById(R.id.loading);


        String rno = getSharedPreferences("userData", Context.MODE_PRIVATE).getString("id",null);
        String cid = getIntent().getStringExtra("cid");
        title = findViewById(R.id.subject_code);
        title.setText(getIntent().getStringExtra("code"));
        Log.d("marks","Initial");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://academichub-restapi.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI api = retrofit.create(RetrofitAPI.class);
        StudentMark obj = new StudentMark(cid.toLowerCase(),rno);
        Call<List<MarkSchema>> call = api.getStudentMarks(obj);
        Log.d("marks",obj.toString());
        call.enqueue(new Callback<List<MarkSchema>>() {
            @Override
            public void onResponse(Call<List<MarkSchema>> call, Response<List<MarkSchema>> response) {
                Log.d("marks","start");
                marks = response.body();

                txt1.setText(Integer.toString(marks.get(0).getCat1()));
                txt2.setText(Integer.toString(marks.get(0).getAssignment1()));
                txt3.setText(Integer.toString(marks.get(0).getCat2()));
                txt4.setText(Integer.toString(marks.get(0).getAssignment2()));
                txt5.setText(Integer.toString(marks.get(0).getCat3()));
                txt6.setText(Integer.toString(marks.get(0).getAssignment3()));

                barEntriesArrayList = new ArrayList<>();
                barEntriesArrayList.add(new BarEntry(1f, marks.get(0).getCat1()));
                barEntriesArrayList.add(new BarEntry(2f, marks.get(1).getCat1()));
                barEntriesArrayList.add(new BarEntry(3f, marks.get(2).getCat1()));

                // creating a new bar data set.
                barDataSet = new BarDataSet(barEntriesArrayList,null);

                // creating a new bar data and
                // passing our bar data set.
                barData = new BarData(barDataSet);
                barData.setBarWidth(0.5f);

                // below line is to set data
                // to our bar chart.
                barChart.setData(barData);

                // adding color to our bar data set.
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                // setting text color.
                barDataSet.setValueTextColor(Color.BLACK);

                // setting text size
                List<String> categories = new ArrayList<>();
                categories.add("");
                categories.add("Mark");
                categories.add("Average");
                categories.add("Top Mark");
                barDataSet.setValueTextSize(16f);
                barChart.getDescription().setEnabled(false);
                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(categories));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                barChart.getAxisLeft().setDrawGridLines(false);
                barChart.getAxisRight().setDrawGridLines(false);
                barChart.getAxisRight().setDrawAxisLine(false);
                barChart.getAxisRight().setDrawLabels(false);
                barChart.getAxisLeft().setAxisMinimum(0f);
                barChart.getAxisLeft().setAxisMaximum(52f);
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<MarkSchema>> call, Throwable t) {
                Log.d("ErrorAPI",t.getMessage());
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                txt7.setText("CAT 1 Performance");
                barEntriesArrayList.remove(0);
                barEntriesArrayList.remove(0);
                barEntriesArrayList.remove(0);
                barEntriesArrayList.add(new BarEntry(1f, marks.get(0).getCat1()));
                barEntriesArrayList.add(new BarEntry(2f, marks.get(1).getCat1()));
                barEntriesArrayList.add(new BarEntry(3f, marks.get(2).getCat1()));
                barDataSet = new BarDataSet(barEntriesArrayList,null);
                barChart.setData(barData);
                Log.d("bars",barEntriesArrayList.toString());
                barChart.notifyDataSetChanged();
                barChart.invalidate();
                loading.setVisibility(View.GONE);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                txt7.setText("CAT 2 Performance");
                barEntriesArrayList.remove(0);
                barEntriesArrayList.remove(0);
                barEntriesArrayList.remove(0);
                barEntriesArrayList.add(new BarEntry(1f, marks.get(0).getCat2()));
                barEntriesArrayList.add(new BarEntry(2f, marks.get(1).getCat2()));
                barEntriesArrayList.add(new BarEntry(3f, marks.get(2).getCat2()));
                barDataSet = new BarDataSet(barEntriesArrayList,null);
                barChart.setData(barData);
                Log.d("bars",barEntriesArrayList.toString());
                barChart.notifyDataSetChanged();
                barChart.invalidate();
                loading.setVisibility(View.GONE);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                txt7.setText("CAT 3 Performance");
                barEntriesArrayList.remove(0);
                barEntriesArrayList.remove(0);
                barEntriesArrayList.remove(0);
                barEntriesArrayList.add(new BarEntry(1f, marks.get(0).getCat3()));
                barEntriesArrayList.add(new BarEntry(2f, marks.get(1).getCat3()));
                barEntriesArrayList.add(new BarEntry(3f, marks.get(2).getCat3()));
                Log.d("bars",barEntriesArrayList.toString());
                barDataSet = new BarDataSet(barEntriesArrayList,null);
                barChart.setData(barData);
                barChart.notifyDataSetChanged();
                barChart.invalidate();
                loading.setVisibility(View.GONE);
            }
        });
    }
}