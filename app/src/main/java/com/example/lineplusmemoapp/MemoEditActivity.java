package com.example.lineplusmemoapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import static com.example.lineplusmemoapp.AttachedImgAdapter.PICK_FROM_ALBUM;
import static com.example.lineplusmemoapp.AttachedImgAdapter.PICK_FROM_CAMERA;

public class MemoEditActivity extends AppCompatActivity {

    private String type; // 새 메모 추가인지 기존메모 수정인지를 기록
    private int memo_id; // 기존 메모 수정의 경우 해당 메모의 id 저장
    private AttachedImgAdapter attached_imgs_adapter;
    private RecyclerView attached_imgs_view;
    private LinearLayoutManager attached_imgs_layout_manager;
    private Uri cameraImgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);

        // 이전 액티비티에서 보내온 데이터 획득
        Intent intent = getIntent();
        type = intent.getExtras().getString("type");
        memo_id = intent.getExtras().getInt("mid");

        // 이전 액티비티에서 새 메모 추가 기능으로 들어온 경우
        if(type.equals("add_memo")) {
            ActionBar ab = getSupportActionBar();
            ab.setTitle(R.string.write);
        }
        // 이전 액티비티에서 기존 메모 수정 기능으로 들어온 경우
        else {
            ActionBar ab = getSupportActionBar();
            ab.setTitle(R.string.modify);
        }

        // 편집 페이지 내 사진 목록에 대한 리사이클러뷰 생성
        // TODO 사진추가 리사이클러뷰 제작해야함.
        attached_imgs_adapter = new AttachedImgAdapter(this);
        attached_imgs_view = (RecyclerView)findViewById(R.id.attached_images_recycler_view);
        attached_imgs_layout_manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        attached_imgs_layout_manager.setReverseLayout(true);
        attached_imgs_layout_manager.setStackFromEnd(true);
        attached_imgs_view.setLayoutManager(attached_imgs_layout_manager);
        attached_imgs_view.setAdapter(attached_imgs_adapter);

        attached_imgs_adapter.add(new AttachedImg(0));
        if(!type.equals("add_memo")) {
            // 기존 메모 수정 기능으로 진입한 경우,
            MemoDBOpenHelper openHelper = new MemoDBOpenHelper(this);
            openHelper.open();
            openHelper.create();

            // 찾고자 하는 mid에 대한 커서 획득
            Cursor mCursor = openHelper.selectMemoWhereMid(memo_id);
            mCursor.moveToNext();

            // 현재 커서가 가리키는 컬럼의 제목과 내용 정보 획득
            String tSubjcet = mCursor.getString(mCursor.getColumnIndex("subject"));
            String tContent = mCursor.getString(mCursor.getColumnIndex("content"));

            // 에딧텍스트 뷰 획득 및 텍스트 적용
            EditText eSubject = (EditText)findViewById(R.id.editText_subject);
            EditText eContent = (EditText)findViewById(R.id.editText_content);
            eSubject.setText(tSubjcet);
            eContent.setText(tContent);

            // 현재 mid에 포함되는 이미지들을 선택
            Cursor iCursor = openHelper.selectImgPathWhereMid(memo_id);


            // TODO 사진추가 리사이클러뷰 제작해야함.
            attached_imgs_adapter.add(new AttachedImg(1, "/sdcard/Images/20200216_200140.png"));
            attached_imgs_adapter.add(new AttachedImg(2, "https://raw.githubusercontent.com/bumptech/glide/master/static/glide_logo.png"));
            iCursor.close();
            mCursor.close();
        }
    }

    // 메모 편집 창 액션 바 등록
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_for_modify, menu);
        return true;
    }

    // 메모 편집 창 액션 바의 버튼이 눌린 경우에 대한 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast myToast;
        String tSubjcet, tContent;
        EditText eSubject, eContent;
        MemoDBOpenHelper openHelper;

        // 에딧텍스트 뷰 획득
        eSubject = (EditText) findViewById(R.id.editText_subject);
        eContent = (EditText) findViewById(R.id.editText_content);

        // 뷰에 포함된 문자열 획득
        tSubjcet = eSubject.getText().toString();
        tContent = eContent.getText().toString();

        // DB open helper 획득
        openHelper = new MemoDBOpenHelper(this);
        openHelper.open();
        openHelper.create();

        // 액션 바 위의 저장 버튼이 눌렸을 때,
        switch (item.getItemId()) {
            case R.id.action_save :
                // 만약 현재 기능이 '새 메모 추가'였을 경우 Insert문 수행
                if(type.equals("add_memo")) {
                    openHelper.insertMemo(tSubjcet, tContent);
                    myToast = Toast.makeText(MemoEditActivity.this, "새 메모가 추가되었습니다.", Toast.LENGTH_SHORT);
                    myToast.show();
                    finish();
                }
                // 현재 기능이 '기존 메모 수정'이었을 경우 Update문 수행
                else {
                    openHelper.updateMemo(memo_id, tSubjcet, tContent);
                    myToast = Toast.makeText(MemoEditActivity.this, "메모가 수정되었습니다.", Toast.LENGTH_SHORT);
                    myToast.show();
                    finish();
                }
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode != RESULT_OK)
            return;

        Uri dataUri;
        String picturePath = "";

        switch(requestCode) {
            case PICK_FROM_ALBUM:
                // 사진첩에서 가져오는 경우
                dataUri = data.getData();
                picturePath = getPath(getApplicationContext(), dataUri, requestCode);
                Toast.makeText(this, picturePath, Toast.LENGTH_SHORT).show();
                break;
            case PICK_FROM_CAMERA:
                // 바로 촬영하는 경우
                dataUri = attached_imgs_adapter.getCameraImageUri();
                picturePath = dataUri.toString();
                picturePath = "/storage/emulated/0/" + picturePath.substring(picturePath.indexOf("/external/") + 10);
                Toast.makeText(this, picturePath, Toast.LENGTH_SHORT).show();

                // attached_imgs_adapter.add(new AttachedImg(1));
                // attached_imgs_adapter.add(new AttachedImg(1));
                break;
        }

        /*
        File imgFile = new File(picturePath);
        Bitmap tempBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        ImageView tempiv = (ImageView)findViewById(R.id.imageview_temp);
        tempiv.setImageBitmap(tempBitmap);
         */
    }

    public static String getPath(Context context, Uri uri, int flag ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }
}
