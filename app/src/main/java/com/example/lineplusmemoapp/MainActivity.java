package com.example.lineplusmemoapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tedPermission();

        // 액션 바 이름 설정 - '메모 보기'
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.read);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        // 리스트 아이템 추가 파트
        Cursor mCursor = openHelper.selectMemo();

        while(mCursor.moveToNext()) {
            int tMid = mCursor.getInt(mCursor.getColumnIndex("mid"));
            String tSubjcet = mCursor.getString(mCursor.getColumnIndex("subject"));
            String tContent = mCursor.getString(mCursor.getColumnIndex("content"));

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
            iCursor.close();
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
        mCursor.close();
    }

    // 메모 리스트 창 액션 바
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_for_list, menu);
        return true;
    }

    // 메모 리스트 창 액션 바의 버튼이 눌린 경우에 대한 함수
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

    private void tedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                // 권한을 반드시 모두 확보한 상태에서만 어플리케이션 진입할 수 있도록 함.
                finish();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.INTERNET)
                .check();
    }
}
