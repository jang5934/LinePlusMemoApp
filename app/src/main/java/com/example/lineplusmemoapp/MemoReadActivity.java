package com.example.lineplusmemoapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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

        // 커서 닫기
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
