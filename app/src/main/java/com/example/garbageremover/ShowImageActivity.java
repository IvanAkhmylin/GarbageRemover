package com.example.garbageremover;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowImageActivity extends AppCompatActivity {
    ImageView image;
    TextView user_Name;
    ImageButton btn_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        image = findViewById(R.id.show_image);
        user_Name = findViewById(R.id.user_name);

        Intent intent = getIntent();
        byte[] bytes = intent.getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        image.setImageBitmap(bitmap);
        user_Name.setText(intent.getStringExtra("user"));
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
