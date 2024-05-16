package com.example.academichub.ClassRoomFragment;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.academichub.DetailedPost;
import com.example.academichub.PostPage;
import com.example.academichub.R;
import com.example.academichub.RetrofitAPI;
import com.example.academichub.responsePackage.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClassRoomHome extends Fragment {

    View view;
    LinearLayout post_layout;
    FloatingActionButton fab;
    TextView code,name,dept;

    CardView card;
    public ClassRoomHome() {
    }

    public int dpToPx(int val){
        float density = getContext().getResources().getDisplayMetrics().density;
        return Math.round((float) val * density);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_class_room_home, container, false);
        post_layout = view.findViewById(R.id.class_post);
        card = view.findViewById(R.id.class_room_header);
        fab = view.findViewById(R.id.post_page);
        SharedPreferences sh = getContext().getSharedPreferences("userData", Context.MODE_PRIVATE);

        code = view.findViewById(R.id.classroom_page_code);
        name = view.findViewById(R.id.classroom_page_cname);
        dept = view.findViewById(R.id.classroom_page_dept);

        code.setText(getActivity().getIntent().getStringExtra("code"));
        name.setText(getActivity().getIntent().getStringExtra("name"));
        dept.setText(getActivity().getIntent().getStringExtra("dept"));
        int bg_id = getActivity().getIntent().getIntExtra("bg_id",0);
        if(bg_id == 1) {
            card.setBackgroundColor(Color.parseColor("#7fbcfb"));
        }
        else if(bg_id == 2){
            card.setBackgroundColor(Color.parseColor("#4cd1bc"));
        }
        else if(bg_id == 3){
            card.setBackgroundColor(Color.parseColor("#fa8c73"));
        }
        else{
            card.setBackgroundColor(Color.parseColor("#9489f4"));
        }
        if(!getActivity().getIntent().getStringExtra("dept").equals("ALL")){
            if(!getActivity().getIntent().getStringExtra("section").equals("N")){
                dept.setText(getActivity().getIntent().getStringExtra("dept")+" - "+getActivity().getIntent().getStringExtra("section"));
            }
        }


        if(sh.getString("type",null).equals("Student")){
            fab.setVisibility(View.GONE);
        }



        if(bg_id == 1) {
            fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7fbcfb")));
        }
        else if(bg_id == 2){
            fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4cd1bc")));
        }
        else if(bg_id == 3){
            fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fa8c73")));
        }
        else{
            fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9489f4")));
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), PostPage.class);
                i.putExtra("cid",getActivity().getIntent().getStringExtra("id"));
                i.putExtra("bg_id",bg_id);
                startActivity(i);
            }
        });


        
        
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://academichub-restapi.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI api = retrofit.create(RetrofitAPI.class);
        Call<List<Post>> call = api.getPost(getActivity().getIntent().getStringExtra("id"));
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                List<Post> post_data = response.body();
                Log.d("class Data", post_data.toString());
                int bg_id = getActivity().getIntent().getIntExtra("bg_id",1);
                if(post_data.isEmpty()){

                }
                else{
                    for (Post data:post_data) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
                        params.setMargins(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));
//                        Card
                        CardView card = new CardView(getContext());
                        card.setCardBackgroundColor(getResources().getColor(R.color.white));
                        card.setRadius(dpToPx(8));
                        card.setLayoutParams(params);

//                        Card Linear Layout
                        LinearLayout layout = new LinearLayout(getContext());
                        layout.setOrientation(LinearLayout.HORIZONTAL);

//                        Icon

                        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.MATCH_PARENT);
                        iconParams.bottomMargin = dpToPx(15);
                        iconParams.leftMargin = dpToPx(15);
                        iconParams.topMargin = dpToPx(15);
                        iconParams.rightMargin = dpToPx(15);

                        LinearLayout icon_layout = new LinearLayout(getContext());
                        icon_layout.setBackground(getResources().getDrawable(R.drawable.circle));
                        icon_layout.setPadding(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));
                        icon_layout.setLayoutParams(iconParams);
                        icon_layout.setGravity(Gravity.CENTER);

                        ImageView img = new ImageView(getContext());
                        if(bg_id == 1) {
                            icon_layout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7fbcfb")));
                        }
                        else if(bg_id == 2){
                            icon_layout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4cd1bc")));
                        }
                        else if(bg_id == 3){
                            icon_layout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fa8c73")));
                        }
                        else{
                            icon_layout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9489f4")));
                        }
                        icon_layout.addView(img);
                        if(data.isAssignment())
                            img.setImageResource(R.drawable.baseline_assignment_24);
                        else
                            img.setImageResource(R.drawable.baseline_forum_24);

//                        Title and Date

                        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        LinearLayout content_layout = new LinearLayout(getContext());
                        content_layout.setLayoutParams(contentParams);
                        content_layout.setOrientation(LinearLayout.VERTICAL);
                        content_layout.setGravity(Gravity.CENTER_VERTICAL);
                        content_layout.setPadding(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));

//                        Title
                        TextView txt = new TextView(getContext());
                        if(data.isAssignment())
                            txt.setText("Assignment: "+data.getTitle());
                        else
                            txt.setText("Announcement: "+data.getTitle());
                        txt.setTextColor(getResources().getColor(R.color.black));

//                        Date
                        TextView txt1 = new TextView(getContext());
                        txt1.setText(data.getPosted_date());
                        txt1.setTextColor(getResources().getColor(R.color.gray));

                        content_layout.addView(txt);
                        content_layout.addView(txt1);
                        layout.addView(icon_layout);
                        layout.addView(content_layout);
                        card.addView(layout);

                        card.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(getContext(), DetailedPost.class);
                                i.putExtra("post_data", (Serializable) data);
                                startActivity(i);
                            }
                        });

                        post_layout.addView(card);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.d("Class Post",t.getMessage());
            }
        });
        return view;
    }
}