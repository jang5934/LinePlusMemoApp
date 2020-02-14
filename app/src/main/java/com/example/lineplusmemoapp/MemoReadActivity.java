package com.example.lineplusmemoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MemoReadActivity extends AppCompatActivity {

    private int mId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_read);

        // 이전 액티비티에서 선택된 메모의 id 획득
        Intent intent = getIntent();
        mId = intent.getExtras().getInt("mid");

        // DB 헬퍼 및 커서 생성
        MemoDBOpenHelper openhelper = new MemoDBOpenHelper(this);
        openhelper.open();
        openhelper.create();

        Cursor mCursor = openhelper.selectMemo();
        while(mCursor.moveToNext()) {
            int tMid = mCursor.getInt(mCursor.getColumnIndex("mid"));
            // 현재 열고자 하는 메모의 번호와 맞는 컬럼을 찾은 경우,
            if(tMid == mId)
            {
                // 해당하는 메모의 제목과 내용 임시저장
                String curSubject = mCursor.getString(mCursor.getColumnIndex("subject"));
                String curContent = mCursor.getString(mCursor.getColumnIndex("content"));

                // 텍스트 뷰 따오기
                TextView tSubject = (TextView)findViewById(R.id.textView_subject);
                TextView tContent = (TextView)findViewById(R.id.textView_content);

                // 텍스트 뷰에 받아온 메모제목과 메모내용 적용
                tSubject.setText(curSubject);
                tContent.setText(curContent);

                /*
                for(int i = 0; i < 1; i++) {
                    // 읽기 페이지의 레이아웃 따오기
                    LinearLayout layout = (LinearLayout) findViewById(R.id.read_page_layout);
                    LinearLayout.LayoutParams layoutParams =
                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    1f);

                    ImageView iv = new ImageView(this);  // 새로 추가할 imageView 생성
                    iv.setImageResource(R.drawable.ic_launcher_background);  // imageView에 내용 추가
                    iv.setLayoutParams(layoutParams);  // imageView layout 설정
                    layout.addView(iv); // 기존 linearLayout에 imageView 추가
                }
                */
                break;
            }
        }



    }

    // 액션바 지정 및 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_for_read, menu);
        return true;
    }

    // 액션바 버튼 눌렸을 때 (새 메모 쓰기 기능)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit :

                // 메모 수정 액티비티 이동
                Intent intent = new Intent(getApplicationContext(), MemoEditActivity.class);
                intent.putExtra("type", "add_memo");
                startActivity(intent);
                return true;

            case R.id.action_delete :

                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }
}
