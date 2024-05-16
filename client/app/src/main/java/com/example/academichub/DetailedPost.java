package com.example.academichub;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.academichub.responsePackage.Post;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;

public class DetailedPost extends AppCompatActivity {

    Post data = null;
    TextView title,description;
    LinearLayout files;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_post);

        title = findViewById(R.id.detailed_title);
        description = findViewById(R.id.detailed_desc);
        files = findViewById(R.id.detailed_files);

        data = (Post) getIntent().getSerializableExtra("post_data");
        title.setText(data.getTitle());
        description.setText(data.getDesc());
        String upload_files[] = data.getFiles().split(",");
        for (String file: upload_files) {
            Uri uri = Uri.parse(file);
            Log.d("FileNameURI",uri.toString());
            String file_name=FirebaseStorage.getInstance().getReferenceFromUrl(file).getName();

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
                Log.d("FileName",file_name);
                if(file_name.toLowerCase().contains(".pdf"))
                    draw = getApplicationContext().getResources().getDrawable(R.drawable.pdf);
                else if(file_name.toLowerCase().contains(".jpg") || file_name.toLowerCase().contains(".png") || file_name.toLowerCase().contains(".jpeg"))
                    draw = getApplicationContext().getResources().getDrawable(R.drawable.baseline_image_24);
                else if(file_name.toLowerCase().contains(".mp3"))
                    draw = getApplicationContext().getResources().getDrawable(R.drawable.baseline_headphones_24);
                else
                    draw = getApplicationContext().getResources().getDrawable(R.drawable.baseline_insert_drive_file_24);
                btn.setCompoundDrawablesWithIntrinsicBounds(draw, null, null, null);
                btn.setText(file_name);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
                params1.weight = 0.8f;
                btn.setLayoutParams(params1);
                btn.setText(FirebaseStorage.getInstance().getReferenceFromUrl(file).getName());
                Bitmap bitmap = pdfToBitmap(new File(file));
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setImageBitmap(bitmap);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("uriFile", view.toString());
                        Log.d("FileData", "Click");
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setDataAndType(uri,"*/*");
                        startActivity(i);
                    }
                });
                llayout.addView(imageView);
                llayout.addView(btn);
            files.addView(llayout);
        }
    }
    private Bitmap pdfToBitmap(File pdfFile) {
        Bitmap bitmap = null;
        Log.d("FilePAth",pdfFile.toString());
        try {
            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));
            Log.d("FilePath",renderer.toString());
            final int pageCount = renderer.getPageCount();
            if(pageCount>0){
                PdfRenderer.Page page = renderer.openPage(0);
                int width = (int) (page.getWidth());
                int height = (int) (page.getHeight());
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();
                renderer.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bitmap;
    }
}