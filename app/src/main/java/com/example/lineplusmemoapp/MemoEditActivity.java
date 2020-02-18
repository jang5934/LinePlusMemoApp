package com.example.lineplusmemoapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import static com.example.lineplusmemoapp.AttachedImgAdapter.PICK_FROM_ALBUM;
import static com.example.lineplusmemoapp.AttachedImgAdapter.PICK_FROM_CAMERA;

public class MemoEditActivity extends AppCompatActivity {

    private String type; // 새 메모 추가인지 기존메모 수정인지를 기록
    private int memo_id; // 기존 메모 수정의 경우 해당 메모의 id 저장
    private AttachedImgAdapter attached_imgs_adapter;
    private RecyclerView attached_imgs_view;
    private LinearLayoutManager attached_imgs_layout_manager;
    private ImgPathModificationRecorder imgPathModificationRecorder;
    Vector<String> beAddedPathList;
    Vector<String> beAddedAndCopiedPathList;
    Vector<String> beDeletedIidList;

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
            try {
                while (iCursor.moveToNext()) {
                    attached_imgs_adapter.add(new AttachedImg(1, iCursor.getString(iCursor.getColumnIndex("path")), Integer.parseInt(iCursor.getString(iCursor.getColumnIndex("iid")))));
                }
            } finally {
                iCursor.close();
            }
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
                // 만약 현재 기능이 '새 메모 추가'였을 경우
                if(type.equals("add_memo")) {
                    openHelper.insertMemo(tSubjcet, tContent);
                    Cursor mCursor = openHelper.selectMemo();
                    mCursor.moveToNext();
                    memo_id = mCursor.getInt(mCursor.getColumnIndex("mid"));
                    myToast = Toast.makeText(MemoEditActivity.this, "새 메모가 추가되었습니다.", Toast.LENGTH_SHORT);
                    myToast.show();
                    finish();
                }
                // 현재 기능이 '기존 메모 수정'이었을 경우
                else {
                    openHelper.updateMemo(memo_id, tSubjcet, tContent);
                    myToast = Toast.makeText(MemoEditActivity.this, "메모가 수정되었습니다.", Toast.LENGTH_SHORT);
                    myToast.show();
                    finish();
                }
        }

        imgPathModificationRecorder = attached_imgs_adapter.getImgPathModificationRecorder();
        beAddedPathList = imgPathModificationRecorder.getBeAddedPathList();
        Iterator i = beAddedPathList.iterator();
        while (i.hasNext()) {
            // 새롭게 추가될 사진들의 경로 벡터
            // 카메라로 찍거나 URL 경로만 입력된 사진들에 대한 Path
            // DB 기록만 필요함
            openHelper.insertImgPath(memo_id, (String)i.next());
        }

        beAddedAndCopiedPathList = imgPathModificationRecorder.getBeAddedAndCopiedPathList();
        i = beAddedAndCopiedPathList.iterator();
        while (i.hasNext()) {
            // 새롭게 추가될 사진들의 경로 벡터
            // 사진첩에서 선택된 사진들의 경로 벡터
            // 우선 사진을 지정위치로 복사한 뒤, 지정 위치의 경로를 DB에 등록해준다.

            String tempDestFIlePath = null;
            try {
                tempDestFIlePath = copyImageFromPath((String) i.next());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            openHelper.insertImgPath(memo_id, tempDestFIlePath);
        }

        beDeletedIidList = imgPathModificationRecorder.getBeDeletedIidList();
        i = beDeletedIidList.iterator();
        while (i.hasNext()) {
            // DB에서 삭제될 사진들의 iid 벡터
            String tempBeDeletedIid = (String)i.next();
            int transformedIid = Integer.parseInt(tempBeDeletedIid);

            // iid로 사진경로를 가지고 온 뒤
            Cursor tempCursor = openHelper.selectImgPathWhereIid(transformedIid);
            tempCursor.moveToFirst();
            // 사진 삭제 및 해당 iid 컬럼 삭제
            new File(tempCursor.getString(tempCursor.getColumnIndex("path"))).delete();
            openHelper.deleteImgIid(transformedIid);
        }
        openHelper.close();

        return super.onOptionsItemSelected(item);
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
                attached_imgs_adapter.add(new AttachedImg(2, picturePath));
                break;
            case PICK_FROM_CAMERA:
                // 바로 촬영하는 경우
                dataUri = attached_imgs_adapter.getCameraImageUri();
                picturePath = dataUri.toString();
                picturePath = "/storage/emulated/0/" + picturePath.substring(picturePath.indexOf("/external/") + 10);
                attached_imgs_adapter.add(new AttachedImg(3, picturePath));
                break;
        }
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

    public String copyImageFromPath (String path) throws FileNotFoundException {
        String sourceFilePathStr = path;
        File sourceFilePath = new File(sourceFilePathStr);

        // 기존 이미지 파일 이름과 확장자 구분
        String destFileFullName = sourceFilePath.getName();
        int lastDotIdx = destFileFullName.lastIndexOf(".");
        String destFileNameStr = destFileFullName.substring(0, lastDotIdx);
        String destFileExtStr = destFileFullName.substring(lastDotIdx);

        // 메모 어플의 이미지 저장소
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "memo_app_images");
        imagesFolder.mkdirs();

        // 중복 검사
        File tempDestPath = null;
        int rn;
        do {
            rn = (int)(Math.random() * 1000000);
            destFileFullName = destFileNameStr  + "_" + Integer.toString(rn) + destFileExtStr;
            tempDestPath = new File(imagesFolder.getPath() + "/" + destFileFullName);
        } while(!tempDestPath.exists());

        // 복사될 이미지 경로
        File destFilePath = new File(imagesFolder, destFileFullName);

        // 파일스트림을 통한 이미지 복사
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(sourceFilePath);
            fos = new FileOutputStream(destFilePath);
            byte[] b = new byte[4096];
            int cnt = 0;
            while((cnt=fis.read(b)) != -1){
                fos.write(b, 0, cnt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 방금 저장된 복사된 이미지의 절대경로 반환
        return imagesFolder.getPath() + "/" + destFileFullName;
    }
}
