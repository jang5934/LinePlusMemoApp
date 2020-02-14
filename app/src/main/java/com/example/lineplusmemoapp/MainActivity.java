package com.example.lineplusmemoapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 액션 바 이름 바꿔줌
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.read);

        // DBhelper 및 커서 생성
        MemoDBOpenHelper openHelper = new MemoDBOpenHelper(this);
        openHelper.open();
        openHelper.create();

        // 리스트 뷰 및 어댑터 생성
        ListView listview;
        ListViewAdapter adapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.memo_listview);
        listview.setAdapter(adapter);

        // 임시 삽입문
        // 추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제
        openHelper.insertMemo("제목", "내용내용11");
        // 추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제추후 삭제


        // 리스트 아이템 추가 파트
        Cursor mCursor = openHelper.selectMemo();

        while(mCursor.moveToNext()) {
            int tMid = mCursor.getInt(mCursor.getColumnIndex("mid"));
            String tSubjcet = mCursor.getString(mCursor.getColumnIndex("subject"));
            String tContent = mCursor.getString(mCursor.getColumnIndex("content"));
            adapter.addItem(tMid, ContextCompat.getDrawable(this, R.mipmap.ic_img_empty), tSubjcet, tContent);


            Cursor iCursor = openHelper.selectImgPath();

            if(iCursor.getCount() == 0) {
                adapter.addItem(tMid, ContextCompat.getDrawable(this, R.mipmap.ic_img_empty), tSubjcet, tContent);
            }
            else {
                iCursor.moveToFirst();
                String img_path = iCursor.getString(iCursor.getColumnIndex("path"));
                Uri img_uri = Uri.parse(img_path);
                adapter.addItem(tMid, img_uri, tSubjcet, tContent);
            }
        }

        // 리스트 뷰 아이템의 이벤트 리스너
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // 선택된 아이템 획득
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);

                // 선택된 아이템의 실제 메모id를 가지고 읽기 액티비티로 넘어감
                Intent intent = new Intent(getApplicationContext(), MemoReadActivity.class);
                intent.putExtra("mid", item.getMid());
                startActivity(intent);
            }
        }) ;
    }

    // 액션바 지정 및 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_for_list, menu);
        return true;
    }

    // 액션바 버튼 눌렸을 때 (새 메모 쓰기 기능)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add :

                // 새로 쓰기 액티비티 이동
                Intent intent = new Intent(getApplicationContext(), MemoEditActivity.class);
                intent.putExtra("type", "add_memo");
                startActivity(intent);
                return true;

            default :
                return super.onOptionsItemSelected(item);
        }
    }
}
