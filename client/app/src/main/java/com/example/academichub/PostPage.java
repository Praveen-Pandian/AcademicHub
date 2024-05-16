package com.example.academichub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.academichub.responsePackage.Post;
import com.example.academichub.responsePackage.Status;
import com.example.academichub.responsePackage.StudentFacultyDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostPage extends AppCompatActivity {

    Button btn,post_btn;
    Switch switch_btn;
    String cid;

    List urls = new ArrayList<String>();

    TextInputEditText title,desc,due_date;
    TextInputLayout due_date_layout;
    LinearLayout filelayout;

    StorageReference ref;
    int bg_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_page);

        TextView txt = findViewById(R.id.create_post_title);

        cid = getIntent().getStringExtra("cid");
        filelayout = findViewById(R.id.post_files);
        btn = findViewById(R.id.post_upload);
        switch_btn = findViewById(R.id.post_assignment);
        title = findViewById(R.id.post_title);
        desc = findViewById(R.id.post_desc);
        due_date = findViewById(R.id.due_date_input);
        due_date_layout = findViewById(R.id.due_date_layout);
        due_date_layout.setVisibility(View.GONE);
        post_btn = findViewById(R.id.post);

        bg_id = getIntent().getIntExtra("bg_id",1);

        ref = FirebaseStorage.getInstance().getReference();

        switch_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    due_date_layout.setVisibility(View.VISIBLE);
                }
                else{
                    due_date_layout.setVisibility(View.GONE);
                }
            }
        });

        if(bg_id == 1) {
            btn.setTextColor(Color.parseColor("#7fbcfb"));
            btn.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#7fbcfb")));
            txt.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7fbcfb")));
            post_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7fbcfb")));
        }
        else if(bg_id == 2){
            btn.setTextColor(Color.parseColor("#4cd1bc"));
            btn.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#4cd1bc")));
            txt.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4cd1bc")));
            post_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4cd1bc")));
        }
        else if(bg_id == 3){
            txt.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fa8c73")));
            btn.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#fa8c73")));
            btn.setTextColor(Color.parseColor("#fa8c73"));
            post_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fa8c73")));
        }
        else{
            btn.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#9489f4")));
            btn.setTextColor(Color.parseColor("#9489f4"));
            txt.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9489f4")));
            post_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9489f4")));
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i,"Select your file"),200);
            }
        });

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tit = title.getText().toString();
                String des = desc.getText().toString();
                Boolean assign = switch_btn.isChecked();
                String due = due_date.getText().toString();

                if(tit.trim().length() == 0){
                    Toast.makeText(getApplicationContext(),"Enter the title",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(des.trim().length() == 0){
                    Toast.makeText(getApplicationContext(),"Enter the description",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(assign && due.trim().length() == 0){
                    Toast.makeText(getApplicationContext(),"Enter the Due date",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!assign){
                    due = "N/A";
                }
                generateFileUrls(tit,des,assign,due);
            }
        });
    }

    public void generateFileUrls(String tit,String des,Boolean assign,String due){

        List<String> url_string = new ArrayList<>();
        final int[] cnt = {0};
        for(int i=0;i<urls.size();i++){
            String fileName = getFileNameFromUri(Uri.parse(urls.get(i).toString()));
            StorageReference fileRef = ref.child(cid.toLowerCase()+"/" + fileName);
            fileRef.putFile(Uri.parse(urls.get(i).toString()))
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                // File uploaded successfully, now get the download URL
                                fileRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            // Generate download URL
                                            Uri downloadUri = task.getResult();
                                            String downloadUrl = downloadUri.toString();
                                            url_string.add(downloadUrl);
                                            Log.d("PostData path",downloadUrl);
                                            cnt[0]++;
                                            if(cnt[0] == urls.size()){
                                                Post post = new Post(tit,des,"",cid,due,"",assign);
                                                post.setFiles(String.join(",",url_string));
                                                Log.d("PostData",post.toString());
                                                Log.d("FileDate",post.toString());
                                                Retrofit retrofit = new Retrofit.Builder()
                                                        .baseUrl("https://academichub-restapi.onrender.com/")
                                                        .addConverterFactory(GsonConverterFactory.create())
                                                        .build();

                                                RetrofitAPI api = retrofit.create(RetrofitAPI.class);
                                                Call<Status> call = api.createPost(post);

                                                call.enqueue(new Callback<Status>() {
                                                    @Override
                                                    public void onResponse(Call<Status> call, Response<Status> response) {
                                                        Status st = response.body();
                                                        if(st.msg.equals("success"))
                                                            startActivity(new Intent(getApplicationContext(), ClassRoomPage.class));
                                                        else
                                                            Toast.makeText(getApplicationContext(),st.msg,Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Status> call, Throwable t) {
                                                        Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                            // Use download URL as needed
                                        } else {
                                            // Handle failure to get download URL
                                            Toast.makeText(getApplicationContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                // Handle failure to upload file
                                Toast.makeText(getApplicationContext(), "Failed to upload file", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    public String getFileNameFromUri(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if (uri.getScheme().equals("file")) {
            fileName = new File(uri.getPath()).getName();
        }
        return fileName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("FileData","Start");
        if(requestCode == 200 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            urls.add(uri.toString());
            String file_name="";
            if (uri.getScheme().equals("content")) {
                try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        file_name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                }
            } else if (uri.getScheme().equals("file")) {
                file_name = new File(uri.getPath()).getName();
            }

            ContentResolver contentResolver = getContentResolver();
            String fileType = contentResolver.getType(uri);
            Log.d("type",fileType);
            LinearLayout llayout = new LinearLayout(getApplicationContext());
            llayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    150,
                    1
            );
            llayout.setLayoutParams(params);

            Button btn = new Button(getApplicationContext());
            Drawable draw = null;
            if(file_name.toLowerCase().contains(".pdf"))
                draw = getApplicationContext().getResources().getDrawable(R.drawable.pdf);
            else if(file_name.toLowerCase().contains(".jpg") || file_name.toLowerCase().contains(".png") || file_name.toLowerCase().contains(".jpeg"))
                draw = getApplicationContext().getResources().getDrawable(R.drawable.baseline_image_24);
            else if(file_name.toLowerCase().contains(".mp3"))
                draw = getApplicationContext().getResources().getDrawable(R.drawable.baseline_headphones_24);
            else
                draw = getApplicationContext().getResources().getDrawable(R.drawable.baseline_insert_drive_file_24);
            btn.setCompoundDrawablesWithIntrinsicBounds(draw,null,null,null);
            btn.setText(file_name);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            params1.weight=0.8f;
            btn.setLayoutParams(params1);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("uriFile",view.toString());
                    Log.d("FileData","Click");
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setDataAndType(Uri.parse(uri.toString()),"*/*");
                    startActivity(i);
                }
            });
            llayout.addView(btn);

            ImageButton cancel = new ImageButton(getApplicationContext());
            cancel.setImageResource(R.drawable.close);
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            params2.weight=0.2f;
            cancel.setLayoutParams(params2);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(int i = 0;i<urls.size();i++){
                        if(urls.get(i).equals(uri.toString())){
                            urls.remove(i);
                            filelayout.removeView(llayout);
                            break;
                        }
                    }
                }
            });



            llayout.addView(cancel);

            filelayout.addView(llayout);
        }
    }
}