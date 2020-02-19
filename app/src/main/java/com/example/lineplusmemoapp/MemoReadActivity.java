package com.example.lineplusmemoapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

public class MemoReadActivity extends AppCompatActivity {

    private int memo_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_read);

        // 액션 바 이름 설정 - '읽기 페이지'
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.read);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 이전 액티비티에서 선택된 메모의 id 획득
        Intent intent = getIntent();
        memo_id = intent.getExtras().getInt("mid");

        // DB 헬퍼 및 커서 생성
        MemoDBOpenHelper openhelper = new MemoDBOpenHelper(this);
        openhelper.open();
        openhelper.create();
        Cursor mCursor = openhelper.selectMemoWhereMid(memo_id);
        mCursor.moveToNext();

        // 해당하는 메모의 제목과 내용 임시저장
        String curSubject = mCursor.getString(mCursor.getColumnIndex("subject"));
        String curContent = mCursor.getString(mCursor.getColumnIndex("content"));

        // 텍스트 뷰 따오기
        TextView tSubject = (TextView)findViewById(R.id.textView_subject);
        tSubject.setText(curSubject);

        // 읽기 페이지의 내용_레이아웃 따오기
        LinearLayout content_container = findViewById(R.id.content_container);
        content_container.removeAllViews();

        TextView tContent = new TextView(this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.gravity = Gravity.TOP | Gravity.START;

        tContent.setTextSize(20);
        tContent.setLayoutParams(textParams);
        tContent.setText(curContent);

        content_container.addView(tContent);

        LinearLayout.LayoutParams imageParams =
                new LinearLayout.LayoutParams(300, 300, 1f);

        Cursor iCursor = openhelper.selectImgPathWhereMid(memo_id);
        try {
            while (iCursor.moveToNext()) {
                final ImageView iv = new ImageView(this);  // 새로 추가할 imageView 생성
                final String imgPath = iCursor.getString(iCursor.getColumnIndex("path"));
                iv.setLayoutParams(imageParams);  // imageView layout 설정
                content_container.addView(iv); // 기존 linearLayout에 imageView 추가

                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ImageViewerActivity.class);
                        intent.putExtra("path", imgPath);
                        startActivity(intent);
                    }
                });

                Glide.with(this)
                        .load(imgPath)
                        .error(R.mipmap.error_image)
                        .into(iv);
            }
        } finally {
            iCursor.close();
        }
        mCursor.close();
    }

    // 메모 읽기 창 액션 바 등록
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_for_read, menu);
        return true;
    }

    // 메모 읽기 창 액션 바의 버튼이 눌린 경우에 대한 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast myToast;

        switch (item.getItemId()) {
            case R.id.action_edit :
                // 현재 가지고있는 메모id와 편집 태그를 가지고 메모 수정 액티비티 이동
                Intent intent = new Intent(getApplicationContext(), MemoEditActivity.class);
                intent.putExtra("type", "edit_memo");
                intent.putExtra("mid", memo_id);
                startActivity(intent);
                return true;
            case R.id.action_delete :

                AlertDialog.Builder confirm = new AlertDialog.Builder(this);
                confirm.setTitle("메모 삭제")
                        .setMessage("정말 삭제하시겠습니까?")
                        .setPositiveButton( "확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int button) {
                                // 현재 메모 삭제 수행
                                MemoDBOpenHelper openHelper;
                                Toast myToast;
                                openHelper = new MemoDBOpenHelper(MemoReadActivity.this);
                                openHelper.open();
                                openHelper.create();

                                // delete cascade로 구현했기 때문에 메모를 지우기 전 이미지 경로들을 참고하여 이미지부터 삭제한다.
                                // 사진이 local에 저장된 경우에만 사진 삭제 수행
                                Cursor iCursor = openHelper.selectImgPathWhereMid(memo_id);
                                while(iCursor.moveToNext()) {
                                    String filePath = iCursor.getString(iCursor.getColumnIndex("path"));
                                    if(filePath.startsWith("/storage/"))
                                        new File(filePath).delete();
                                }

                                // 그리고 나서 메모를 삭제한다.
                                openHelper.deleteMemo(memo_id);
                                myToast = Toast.makeText(MemoReadActivity.this, "메모가 삭제되었습니다.", Toast.LENGTH_SHORT);
                                myToast.show();
                                openHelper.close();
                                finish();
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }
}
