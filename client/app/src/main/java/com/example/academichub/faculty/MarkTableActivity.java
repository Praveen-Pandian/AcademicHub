package com.example.academichub.faculty;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.example.academichub.responsePackage.ClassDateResponse;
import com.example.academichub.responsePackage.ClassMark;
import com.example.academichub.responsePackage.ClassMarkResponse;
import com.example.academichub.responsePackage.Marks;
import com.example.academichub.responsePackage.Status;
import com.example.academichub.responsePackage.UpdateMark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MarkTableActivity extends AppCompatActivity {
    List<String> regno = new ArrayList<String>();
    List<Marks> students_marks= new ArrayList<Marks>();
    LinearLayout table;
    Button Marksubmitbtn;
    TextView txt;
    int mark;
    RetrofitAPI api;
    Retrofit retrofit;
    int bg_id=0;

    HashMap<String,Integer> already_marks = new HashMap<>();

    public int dpToPx(int val){
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float) val * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_mark);

        bg_id = getIntent().getIntExtra("bg_id",0);

        table = findViewById(R.id.table);
        txt = findViewById(R.id.mark_title);
        Marksubmitbtn=findViewById(R.id.markSubmit);
        txt.setText(getIntent().getStringExtra("name"));
        txt.setTextColor(getResources().getColor(R.color.white));
        txt.setTextSize(25.0f);

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

        retrofit = new Retrofit.Builder()
                .baseUrl("https://academichub-restapi.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(RetrofitAPI.class);

        ClassMark input = new ClassMark(getIntent().getStringExtra("id").toLowerCase(),getIntent().getStringExtra("name").toLowerCase().replace(' ','_'));
        Log.d("studentMarks1",input.toString());
        Call<List<ClassMarkResponse>> call1 = api.getFacultyClassWiseMarkForExam(input);

        call1.enqueue(new Callback<List<ClassMarkResponse>>() {
            @Override
            public void onResponse(Call<List<ClassMarkResponse>> call, Response<List<ClassMarkResponse>> response) {
                Log.d("studentMarks",response.body().toString());
                for(ClassMarkResponse studentMark:response.body()){
                    already_marks.put(studentMark.getRno(),studentMark.getMark());
                }
                generateTable();
            }

            @Override
            public void onFailure(Call<List<ClassMarkResponse>> call, Throwable t) {

            }
        });

        Marksubmitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i;
                Log.d("regno",regno.toString());
                Marks obj[] = new Marks[regno.size()];
                for(i = 0; i < regno.size(); i++) {
                    EditText edit = findViewById(i);
                    String marks = String.valueOf(edit.getText());
                    Log.d("marks",marks);
                    mark = (marks.equalsIgnoreCase("")) ? 0 : Integer.parseInt(marks);
                    Log.d("marks",Integer.toString(marks.length()));
                    if(!marks.isEmpty())
                    {
                        if ((Integer.parseInt(marks) >= 0 && Integer.parseInt(marks) <= 100)) {
                            obj[i] = new Marks(regno.get(i), Integer.parseInt(marks));
                            Log.d("marks",obj[i].toString());
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Enter valid marks for "+regno.get(i),Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Enter valid marks for "+regno.get(i),Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if(i == regno.size()) {
                    for (i=0;i<regno.size();i++){
                        students_marks.add(obj[i]);
                    }
                    Log.d("student_mark",students_marks.toString());
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://academichub-restapi.onrender.com/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitAPI api = retrofit.create(RetrofitAPI.class);
                    UpdateMark umark = new UpdateMark(getIntent().getStringExtra("id"),getIntent().getStringExtra("name"),students_marks);
                    Log.d("umark",umark.toString());
                    Call<Status> call = api.updateMarks(umark);
                    call.enqueue(new Callback<Status>() {
                        @Override
                        public void onResponse(Call<Status> call, Response<Status> response) {
                            Log.d("updatemarks",response.body().toString());
                            Intent i = new Intent(getApplicationContext(), ClassRoomPage.class);
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<Status> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                            Log.d("updatemarks",t.getMessage());
                        }
                    });
                }
            }
        });
    }

    public void generateTable(){
        Call<List<String>> call = api.getStudentList(getIntent().getStringExtra("id"));

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                regno = response.body();
                Log.d("markData",response.body().toString());
                for (int i = 0; i < regno.size(); i++) {
                    CardView cview = new CardView(getApplicationContext());
                    cview.setCardBackgroundColor(getResources().getColor(R.color.white));
                    LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    cardParams.setMargins(dpToPx(5),0,dpToPx(5),0);
                    cview.setLayoutParams(cardParams);
                    LinearLayout linearLayout = new LinearLayout(getApplicationContext());
                    TextView textView = new TextView(getApplicationContext());
                    EditText editText = new EditText(getApplicationContext());

//            Design Stuffs for Linear Layout
                    textView.setText(regno.get(i));
                    textView.setGravity(Gravity.CENTER);

                    LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                            0, // Set layout_width to 0 (will be weighted)
                            ViewGroup.LayoutParams.MATCH_PARENT // Set layout_height to match_parent
                    );
                    textParams.weight = 1; // Distribute space evenly

                    textView.setLayoutParams(textParams);

                    LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(
                            0,// Set layout_width to 0 (will be weighted)
                            ViewGroup.LayoutParams.MATCH_PARENT // Set layout_height to match_parent
                    );
                    editParams.weight = 1; // Distribute space evenly
                    editText.setLayoutParams(editParams);

//            Adding id to the marks
                    editText.setId(i);
                    editText.setEms(8);
                    editText.setHint("0");
                    if(already_marks.get(regno.get(i)) != null)
                        editText.setText(already_marks.get(regno.get(i)).toString());
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);

//            Design stuffs for linear & table layouts
                    LinearLayout.LayoutParams linearparams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                    );
                    linearLayout.setLayoutParams(linearparams);
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayout.setPadding(10,10,10,10);
                    linearLayout.addView(textView);
                    linearLayout.addView(editText);

                    // Set the layout_width and layout_height of the CardView to match_parent
                    cview.addView(linearLayout);

                    table.addView(cview);
                }

            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.d("markData",t.getMessage());
            }
        });
    }
}
