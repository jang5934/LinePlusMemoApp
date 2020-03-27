package com.example.lineplusmemoapp.EditMemo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
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

import com.example.lineplusmemoapp.Database.ImgPathEntity;
import com.example.lineplusmemoapp.Database.MemoAndImgPathEntity;
import com.example.lineplusmemoapp.Database.MemoEntity;
import com.example.lineplusmemoapp.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import static com.example.lineplusmemoapp.EditMemo.AttachedImgAdapter.PICK_FROM_ALBUM;
import static com.example.lineplusmemoapp.EditMemo.AttachedImgAdapter.PICK_FROM_CAMERA;

public class MemoEditActivity extends AppCompatActivity {

    private String type; // 새 메모 추가인지 기존메모 수정인지를 기록
    private int memo_id; // 기존 메모 수정의 경우 해당 메모의 id 저장

    // 첨부된 사진들의 목록을 보여주기 위한 리사이클러뷰와 어댑터
    private AttachedImgAdapter attached_imgs_adapter;
    private RecyclerView attached_imgs_view;

    // 리사이클러뷰가 올라갈 레이아웃
    private LinearLayoutManager attached_imgs_layout_manager;

    // 첨부된 이미지의 추가와 삭제를 관리하기 위한 클래스
    private ImgPathModificationRecorder imgPathModificationRecorder;

    // 메모 편집기능에서 사용될 뷰 모델
    private EditViewModel editViewModel;

    // 추가될 이미지의 경로와 이미지 추가 방식을 기록할 벡터
    Vector<ImgPathModificationRecorder.CustomImagePath> beAddedPathList;

    // 기존 이미지에서 삭제될 이미지의 id값을 모아둘 벡터
    Vector<Integer> beDeletedIidList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);

        // 메모 편집 시 메모의 정보들을 가져오기 위해 뷰 모델 생성
        editViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(EditViewModel.class);

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

        // 리사이클러뷰 어댑터 생성
        attached_imgs_adapter = new AttachedImgAdapter(this);

        // 레이아웃 매니져 생성 및 옵션 설정
        attached_imgs_layout_manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        // 사진은 좌측에서 우측으로 추가되도록 설정
        attached_imgs_layout_manager.setReverseLayout(true);
        attached_imgs_layout_manager.setStackFromEnd(true);

        // 리사이클러 뷰의 레이아웃 매니저와 어댑터 설정
        attached_imgs_view = (RecyclerView)findViewById(R.id.attached_images_recycler_view);
        attached_imgs_view.setLayoutManager(attached_imgs_layout_manager);
        attached_imgs_view.setAdapter(attached_imgs_adapter);

        // 이미지 추가를 위한 더미 아이템 추가
        attached_imgs_adapter.add(new AttachedImg(0));

        // 기존 메모 수정 기능으로 진입한 경우,
        if(!type.equals("add_memo")) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    // 선택된 메모 데이터 수신
                    MemoAndImgPathEntity t_Memo = editViewModel.getCurrentMemoAndImgPath(memo_id);

                    // 텍스트편집 뷰 획득 및 기존 메모의 제목과 내용 삽입
                    EditText eSubject = (EditText) findViewById(R.id.editText_subject);
                    EditText eContent = (EditText) findViewById(R.id.editText_content);
                    eSubject.setText(t_Memo.getMemoEntity().getSubject());
                    eContent.setText(t_Memo.getMemoEntity().getContent());

                    // 현재 메모에 첨부된 이미지들을 리사이클러뷰에 추가
                    List<ImgPathEntity> t_Paths = t_Memo.getImgPaths();
                    for (ImgPathEntity t_Path : t_Paths) {
                        attached_imgs_adapter.add(new AttachedImg(1, t_Path.getPath(), t_Path.getIid()));
                    }
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
    }

    // 메모 편집 창 액션 바 등록
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_for_modify, menu);
        return true;
    }

    // 메모 편집 창 액션 바의 버튼이 눌린 경우에 대한 함수 (메모저장버튼)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final String tSubjcet, tContent;
        EditText eSubject, eContent;

        // 에딧텍스트 뷰 획득
        eSubject = (EditText) findViewById(R.id.editText_subject);
        eContent = (EditText) findViewById(R.id.editText_content);

        // 뷰에 포함된 문자열 획득
        tSubjcet = eSubject.getText().toString();
        tContent = eContent.getText().toString();

        // 메모의 제목과 내용에 Null 값이 들어가는 것 방지
        if(tSubjcet.length() == 0 || tContent.length() == 0) {
            Toast.makeText(MemoEditActivity.this, "제목과 내용을 채워주신 후 저장해주십시오.", Toast.LENGTH_SHORT).show();
            return true;
        }

        // '새 메모 추가' 인지 '기존 메모 편집' 인지에 따라 저장기능을 다르게 수행하기 위한 분기점
        switch (item.getItemId()) {
            case R.id.action_save :
                // '새 메모 추가' 기능
                if(type.equals("add_memo")) {
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            memo_id = (int)(editViewModel.insertCurrentMemo(new MemoEntity(tSubjcet, tContent)));
                            finish();
                        }
                    };
                    Thread t = new Thread(r);
                    t.start();
                    Toast.makeText(MemoEditActivity.this, "새 메모가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                }
                // '기존 메모 수정' 기능
                else {
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            editViewModel.updateCurrentMemo(memo_id, new MemoEntity(tSubjcet, tContent));
                            finish();
                        }
                    };
                    Thread t = new Thread(r);
                    t.start();
                    Toast.makeText(MemoEditActivity.this, "메모가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                }
        }

        // 첨부된 이미지가 기록된 이미지 리스트 획득
        imgPathModificationRecorder = attached_imgs_adapter.getImgPathModificationRecorder();

        /** 1. 새로 추가될 이미지에 대한 처리 시작 **/
        beAddedPathList = imgPathModificationRecorder.getBeAddedPathList();
        Iterator i = beAddedPathList.iterator();

        // 이미지 경로를 임시로 담아둘 변수
        ImgPathModificationRecorder.CustomImagePath temp_path;
        while (i.hasNext()) {
            // 새롭게 추가될 사진들의 경로 벡터
            // 사진첩에서 선택된 경우는 1
            // 카메라로 찍은 경우는 2
            // URL 경로로 입력된 경우는 3
            temp_path = (ImgPathModificationRecorder.CustomImagePath)i.next();
            final ImgPathModificationRecorder.CustomImagePath finalTemp_path = temp_path;


            // 첨부된 이미지를 DB에 등록만 하거나 복사또한 하는 경우를 나누기 위한 분기 시작
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    String tempDestFIlePath;
                    // 사진첩에서 선택된 경우 메모 어플리케이션에서 따로 관리하는 사진첩 디렉토리로 사진 복사 수행
                    if(finalTemp_path.getPathType() == 1) {
                        try {
                            tempDestFIlePath = copyImageFromPath(finalTemp_path.getImagePath());
                            editViewModel.insertImgPath(new ImgPathEntity(memo_id, tempDestFIlePath, finalTemp_path.getPathType()));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    // 그 외의 경우에는 이미지 복사를 수행하지 않고 DB에 사진경로 등록만 수행.
                    else {
                        editViewModel.insertImgPath(new ImgPathEntity(memo_id, finalTemp_path.getImagePath(), finalTemp_path.getPathType()));
                    }
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
        /** 새로 추가될 이미지에 대한 처리 끝 **/

        /** 2. 삭제될 이미지에 대한 처리 시작 **/
        // 삭제될 이미지들의 iid 벡터 획득
        beDeletedIidList = imgPathModificationRecorder.getBeDeletedIidList();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                for(int iid : beDeletedIidList) {
                    // iid에 해당하는 이미지경로 획득
                    ImgPathEntity t_imgPath = editViewModel.selectImgPath(iid);
                    // 사진이 메모앱의 이미지 디렉토리에 저장된 경우에만 사진 삭제 수행
                    if(t_imgPath.getPath().startsWith("/storage/")) {
                        File delFile = new File(t_imgPath.getPath());
                        delFile.delete();
                    }
                    // 사진 삭제 및 해당 iid 컬럼 삭제
                    editViewModel.deleteImgPath(iid);
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
        /** 삭제될 이미지에 대한 처리 끝 **/
        return super.onOptionsItemSelected(item);
    }

    /** 카메라 액티비티 혹은 앨범 액티비티에서 돌아온 경우를 처리하기 위한 메서드 **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode != RESULT_OK)
            return;

        Uri dataUri;
        String picturePath = "";

        switch(requestCode) {

            // 사진첩에서 가져오는 경우의 처리
            case PICK_FROM_ALBUM:
                dataUri = data.getData();
                picturePath = getPath(getApplicationContext(), dataUri);
                attached_imgs_adapter.add(new AttachedImg(2, picturePath));
                break;

            // 바로 촬영하는 경우의 처리
            case PICK_FROM_CAMERA:
                dataUri = attached_imgs_adapter.getCameraImageUri();
                picturePath = dataUri.toString();
                picturePath = "/storage/emulated/0/" + picturePath.substring(picturePath.indexOf("/external/") + 10);
                attached_imgs_adapter.add(new AttachedImg(3, picturePath));
                break;
        }
    }

    /** Uri를 String 형태의 경로로 변환해주는 메서드 **/
    public static String getPath(Context context, Uri uri) {
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

    /** 메모 어플리케이션의 이미지를 모아두는 디렉토리에 이미지를 복사하고 경로를 String으로 반환해주는 메서드 **/
    public String copyImageFromPath (String path) throws FileNotFoundException {
        String sourceFilePathStr = path;
        File sourceFilePath = new File(sourceFilePathStr);

        // 기존 이미지 파일 이름과 확장자 분리
        String destFileFullName = sourceFilePath.getName();
        int lastDotIdx = destFileFullName.lastIndexOf(".");
        String destFileNameStr = destFileFullName.substring(0, lastDotIdx);
        String destFileExtStr = destFileFullName.substring(lastDotIdx);

        // 메모 어플의 이미지 저장소
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "memo_app_images");
        imagesFolder.mkdirs();

        // 중복 검사
        File tempDestPath;
        do {
            destFileFullName = destFileNameStr  + "_" + (int)(Math.random() * 1000000) + destFileExtStr;
            tempDestPath = new File(imagesFolder.getPath() + "/" + destFileFullName);
        } while(tempDestPath.exists());

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
            while((cnt = fis.read(b)) != -1){
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

        // 복사된 이미지의 절대경로 반환
        return imagesFolder.getPath() + "/" + destFileFullName;
    }
}
