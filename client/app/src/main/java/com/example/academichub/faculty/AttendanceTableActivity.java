package com.example.academichub.faculty;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.academichub.ClassRoomPage;
import com.example.academichub.R;
import com.example.academichub.RetrofitAPI;
import com.example.academichub.responsePackage.Attendance;
import com.example.academichub.responsePackage.ClassDate;
import com.example.academichub.responsePackage.ClassDateResponse;
import com.example.academichub.responsePackage.Status;
import com.example.academichub.responsePackage.UpdateAttendance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AttendanceTableActivity extends AppCompatActivity {

    List<String> regno;
    ArrayList<String> present = new ArrayList<>();
    ArrayList<String> absent = new ArrayList<>();
    ArrayList<Attendance> students_attendance = new ArrayList<Attendance>();
    LinearLayout table;
    Button Marksubmitbtn;
    TextView txt;
    List<String> already_present= new ArrayList<>();
    List<String> already_absent=new ArrayList<>();

    int bg_id=0;
    Retrofit retrofit;
    RetrofitAPI api;

    public int dpToPx(int val){
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float) val * density);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_activity_table);

        bg_id = getIntent().getIntExtra("bg_id",0);
        table = findViewById(R.id.table);

        txt = findViewById(R.id.attendance_date);
        txt.setText(getIntent().getStringExtra("date"));


        retrofit = new Retrofit.Builder()
                .baseUrl("https://academichub-restapi.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(RetrofitAPI.class);

//        Fetch Student Attendance from DB
        Call<List<ClassDateResponse>> call1 = api.getFacultyClassWiseAttendance(new ClassDate(getIntent().getStringExtra("date"),getIntent().getStringExtra("id")));
        call1.enqueue(new Callback<List<ClassDateResponse>>() {
            @Override
            public void onResponse(Call<List<ClassDateResponse>> call, Response<List<ClassDateResponse>> response) {
                for(ClassDateResponse res : response.body()){
                    if(res.getStatus())
                        already_present.add(res.getRno());
                    else
                        already_absent.add(res.getRno());
                }
                generateTable();

            }

            @Override
            public void onFailure(Call<List<ClassDateResponse>> call, Throwable t) {
                Log.d("AttendanceData",t.getMessage());
            }
        });


        Marksubmitbtn=findViewById(R.id.markSubmit);
//        Check and append the register number of students' attendance in present and absent arrays respectively
        Marksubmitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i;
                Attendance obj[] = new Attendance[regno.size()];
                for(i = 0; i < regno.size(); i++) {
                    int newid = Integer.parseInt(regno.get(i).substring(regno.get(i).length()-3, regno.get(i).length()));

                    CheckBox check = findViewById(newid);
                    boolean isChecked = check.isChecked();
                    String attendance = String.valueOf(isChecked);

                    obj[i] = new Attendance(regno.get(i), attendance);

                    if(isChecked){
                        present.add(regno.get(i));
                        Toast.makeText(getApplicationContext(),"Attendance: " + attendance,Toast.LENGTH_SHORT).show();
                    } else {
                        absent.add(regno.get(i));
                    }
                }

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://academichub-restapi.onrender.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                RetrofitAPI api = retrofit.create(RetrofitAPI.class);
                UpdateAttendance attend = new UpdateAttendance(getIntent().getStringExtra("id"),getIntent().getStringExtra("date"),String.join(",",present),String.join(",",absent));
                Call<Status> call = api.updateAttendance(attend);

                call.enqueue(new Callback<Status>() {
                    @Override
                    public void onResponse(Call<Status> call, Response<Status> response) {
                        if(response.body().msg.equals("success")){
                            Intent i = new Intent(getApplicationContext(), ClassRoomPage.class);
                            startActivity(i);
                            finish();
                        }
                        Log.d("Attendance",response.body().msg);
                    }

                    @Override
                    public void onFailure(Call<Status> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                        Log.d("ErrorAttend",t.getMessage());
                    }
                });
            }
        });

        if(bg_id == 1) {
            txt.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7fbcfb")));
            findViewById(R.id.title_bar).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7fbcfb")));
            Marksubmitbtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7fbcfb")));
        }
        else if(bg_id == 2){
            txt.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4cd1bc")));
            findViewById(R.id.title_bar).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4cd1bc")));
            Marksubmitbtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4cd1bc")));
        }
        else if(bg_id == 3){
            txt.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fa8c73")));
            findViewById(R.id.title_bar).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fa8c73")));
            Marksubmitbtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fa8c73")));
        }
        else{
            txt.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9489f4")));
            findViewById(R.id.title_bar).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9489f4")));
            Marksubmitbtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9489f4")));
        }


    }

    public void generateTable(){
        //        Get Student List

        Call<List<String>> call = api.getStudentList(getIntent().getStringExtra("id"));

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                regno = response.body();
                for (int i = 0; i < regno.size(); i++) {
                    CardView cview = new CardView(getApplicationContext());
                    cview.setCardBackgroundColor(getResources().getColor(R.color.white));
                    LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    cardParams.setMargins(dpToPx(5),0,dpToPx(5),0);
                    cview.setLayoutParams(cardParams);

                    LinearLayout linearLayout = new LinearLayout(getApplicationContext());
                    linearLayout.setWeightSum(1f);

                    TextView textView = new TextView(getApplicationContext());
                    CheckBox checkBox = new CheckBox(getApplicationContext());

//            Design Stuffs for Linear Layout
                    textView.setText(regno.get(i));
                    textView.setGravity(Gravity.CENTER);

                    LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                            0, // Set layout_width to 0 (will be weighted)
                            ViewGroup.LayoutParams.MATCH_PARENT // Set layout_height to match_parent
                    );
                    textParams.weight = 0.5f; // Distribute space evenly

                    textView.setLayoutParams(textParams);

                    LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                            0,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    );
                    checkParams.gravity = Gravity.CENTER_HORIZONTAL;
                    checkParams.weight = 0.5f;
                    checkBox.setLayoutParams(checkParams);

                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params1.setMargins(dpToPx(30),0,dpToPx(30),0);
//            Adding id to the attendance
                    int id1 = Integer.parseInt(regno.get(i).substring(regno.get(i).length()-3, regno.get(i).length()));
                    checkBox.setId(id1);
                    checkBox.setEms(8);
                    checkBox.setGravity(Gravity.CENTER_HORIZONTAL);
                    Log.d("AttendanceDate",already_present.toString());
                    Log.d("AttendanceDate", String.valueOf((already_present.contains(regno.get(i)))));
                    if(already_present.contains(regno.get(i))) {
                        Log.d("AttendanceDate", "hello"+regno.get(i));
                        checkBox.setChecked(true);
                    }
                    else {
                        Log.d("AttendanceDate","lusu"+regno.get(i));
                        checkBox.setChecked(false);
                    }

//            Design stuffs for linear & table layouts
                    LinearLayout.LayoutParams linearparams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                    );
                    linearLayout.setLayoutParams(linearparams);
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayout.setPadding(10,10,10,10);
                    linearLayout.addView(textView);
                    linearLayout.addView(checkBox);

                    cview.setPadding(2, 2, 0, 2);
                    cview.addView(linearLayout);


                    table.addView(cview);
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {

            }
        });

    }
}
