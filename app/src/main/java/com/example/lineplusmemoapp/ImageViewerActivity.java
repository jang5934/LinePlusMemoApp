package com.example.lineplusmemoapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.view);

        ImageView refered_image = findViewById(R.id.refered_image);

        Intent intent = getIntent();
        String img_path = intent.getExtras().getString("path");

        Glide.with(this)
                .load(img_path)
                .error(R.mipmap.error_image)
                .into(refered_image);

    }
}
