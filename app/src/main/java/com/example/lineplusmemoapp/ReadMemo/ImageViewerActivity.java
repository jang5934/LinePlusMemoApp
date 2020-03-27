package com.example.lineplusmemoapp.ReadMemo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

// https://bumptech.github.io/glide/
import com.bumptech.glide.Glide;
import com.example.lineplusmemoapp.R;

// 메모 읽기 화면에서 첨부된 사진을 터치했을 때 실행되는 사진 보기 액티비티
public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        // 액션바 이름 수정
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.view);

        // 메모 읽기 화면에서 가져 온 사진의 경로를 받음
        ImageView refered_image = findViewById(R.id.refered_image);
        Intent intent = getIntent();
        String img_path = intent.getExtras().getString("path");

        // Glide 라이브러리를 활용해 방금 받은 사진경로에 위치한 사진 출력
        // https://bumptech.github.io/glide/
        Glide.with(this)
                .load(img_path)
                .error(R.mipmap.error_image)
                .into(refered_image);

    }
}
