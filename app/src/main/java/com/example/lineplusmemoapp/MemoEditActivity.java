package com.example.lineplusmemoapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class MemoEditActivity extends AppCompatActivity {

    private String type;
    private String memo_subject;
    private String memo_content;
    private int memo_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);

        Intent intent = getIntent();

        type = intent.getExtras().getString("type");
        memo_id = intent.getExtras().getInt("mid");

        if(type.equals("add_memo")) {
            ActionBar ab = getSupportActionBar();
            ab.setTitle(R.string.write);
        }
        else {
            ActionBar ab = getSupportActionBar();
            ab.setTitle(R.string.modify);
        }

        AttachedImgAdapter attached_imgs_adapter = new AttachedImgAdapter();
        RecyclerView attached_imgs_view = (RecyclerView)findViewById(R.id.attached_images_recycler_view);
        LinearLayoutManager attached_imgs_layout_manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        attached_imgs_view.setLayoutManager(attached_imgs_layout_manager);
        attached_imgs_view.setAdapter(attached_imgs_adapter);

        if(!type.equals("add_memo")) {
            // 글 수정 기능으로 진입한 경우,

            MemoDBOpenHelper openHelper = new MemoDBOpenHelper(this);
            openHelper.open();
            openHelper.create();

            Cursor mCursor = openHelper.selectMemo();

            while(mCursor.moveToNext()) {
                if(memo_id == mCursor.getInt(mCursor.getColumnIndex("mid"))) {
                    String tSubjcet = mCursor.getString(mCursor.getColumnIndex("subject"));
                    String tContent = mCursor.getString(mCursor.getColumnIndex("content"));

                    EditText eSubject = (EditText)findViewById(R.id.editText_subject);
                    EditText eContent = (EditText)findViewById(R.id.editText_content);

                    eSubject.setText(tSubjcet);
                    eContent.setText(tContent);



                    attached_imgs_adapter.add(new AttachedImg(1));
                }
            }
        }
        attached_imgs_adapter.add(new AttachedImg(0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(type.equals("add_memo")) {
            getMenuInflater().inflate(R.menu.actionbar_for_modify, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.actionbar_for_read, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast myToast;
        switch (item.getItemId()) {
            case R.id.action_save :

                // 저장 기능 구현
                // Update랑 Intert 둘을 구분해야함.
                // 저장 기능 구현
                // Update랑 Intert 둘을 구분해야함.
                // 저장 기능 구현
                // Update랑 Intert 둘을 구분해야함.
                // 저장 기능 구현
                // Update랑 Intert 둘을 구분해야함.

                myToast = Toast.makeText(MemoEditActivity.this, "save", Toast.LENGTH_SHORT);
                myToast.show();
                return true;

            case R.id.action_edit :
                myToast = Toast.makeText(MemoEditActivity.this, "edit", Toast.LENGTH_SHORT);
                myToast.show();
                return true;

            case R.id.action_delete :
                myToast = Toast.makeText(MemoEditActivity.this, "delete", Toast.LENGTH_SHORT);
                myToast.show();
                return true;

            default :
                return super.onOptionsItemSelected(item);
        }
    }
}
