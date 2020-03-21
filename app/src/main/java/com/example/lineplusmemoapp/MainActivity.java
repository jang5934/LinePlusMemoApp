package com.example.lineplusmemoapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

// https://github.com/ParkSangGwon/TedPermission
import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel mainActivityViewModel;
    private Context currentContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentContext = getBaseContext();

        // https://github.com/ParkSangGwon/TedPermission
        tedPermission();

        // 리스트 뷰 및 어댑터 생성
        final ListView listview;
        final ListViewAdapter adapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listview = findViewById(R.id.memo_listview);
        listview.setAdapter(adapter);

        mainActivityViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(MainActivityViewModel.class);
        mainActivityViewModel.setRequestingContextt(currentContext);

        final Observer<ArrayList<MemoData>> dataObserver = new Observer<ArrayList<MemoData>>() {
            @Override
            public void onChanged(ArrayList<MemoData> data) {
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

                for(final MemoData tListItem : data) {
                    if(tListItem.getImages() == null) {
                        adapter.addItem(tListItem.getMid(), ContextCompat.getDrawable(currentContext, R.mipmap.ic_img_empty), tListItem.getSubject(), tListItem.getMemoContent());
                    }
                    else {
                        adapter.addItem(tListItem.getMid(), tListItem.getImages().get(0).getImagePath(), tListItem.getSubject(), tListItem.getMemoContent(), tListItem.getImages().get(0).getPathType());
                    }
                }
            }
        };

        // LiveData를 관찰하고 관찰한 데이터를 이 액티비티에 넘기도록 설정.
        mainActivityViewModel.getCurrentData().observe(this, dataObserver);

        // 액션 바 이름 설정 - '메모 보기'
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.app_name);
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

    // https://github.com/ParkSangGwon/TedPermission
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
