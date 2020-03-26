package com.example.lineplusmemoapp.EditMemo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lineplusmemoapp.Database.ImgPathEntity;
import com.example.lineplusmemoapp.Database.MemoAndImgPathEntity;
import com.example.lineplusmemoapp.Database.MemoEntity;
import com.example.lineplusmemoapp.R;
import com.example.lineplusmemoapp.ReadMemo.ImageViewerActivity;

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
    private AttachedImgAdapter attached_imgs_adapter;
    private RecyclerView attached_imgs_view;
    private LinearLayoutManager attached_imgs_layout_manager;
    private ImgPathModificationRecorder imgPathModificationRecorder;
    private EditViewModel editViewModel;

    Vector<CustomImagePath> beAddedPathList;
    Vector<Integer> beDeletedIidList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);

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

        // 편집 페이지 내 사진 목록에 대한 리사이클러뷰 생성
        attached_imgs_adapter = new AttachedImgAdapter(this);
        attached_imgs_view = (RecyclerView)findViewById(R.id.attached_images_recycler_view);
        attached_imgs_layout_manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        attached_imgs_layout_manager.setReverseLayout(true);
        attached_imgs_layout_manager.setStackFromEnd(true);
        attached_imgs_view.setLayoutManager(attached_imgs_layout_manager);
        attached_imgs_view.setAdapter(attached_imgs_adapter);

        attached_imgs_adapter.add(new AttachedImg(0));

        // 기존 메모 수정 기능으로 진입한 경우,
        if(!type.equals("add_memo")) {

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    // 선택된 메모 데이터 수신
                    MemoAndImgPathEntity t_Memo = editViewModel.getCurrentMemoAndImgPath(memo_id);

                    // 에딧텍스트 뷰 획득 및 텍스트 적용
                    EditText eSubject = (EditText) findViewById(R.id.editText_subject);
                    EditText eContent = (EditText) findViewById(R.id.editText_content);
                    eSubject.setText(t_Memo.getMemoEntity().getSubject());
                    eContent.setText(t_Memo.getMemoEntity().getContent());

                    // 현재 메모에 포함되는 이미지들을 선택

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

    // 메모 편집 창 액션 바의 버튼이 눌린 경우에 대한 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast myToast;
        final String tSubjcet, tContent;
        EditText eSubject, eContent;

        // 에딧텍스트 뷰 획득
        eSubject = (EditText) findViewById(R.id.editText_subject);
        eContent = (EditText) findViewById(R.id.editText_content);

        // 뷰에 포함된 문자열 획득
        tSubjcet = eSubject.getText().toString();
        tContent = eContent.getText().toString();

        if(tSubjcet.length() == 0 || tContent.length() == 0) {
            myToast = Toast.makeText(MemoEditActivity.this, "제목과 내용을 채워주신 후 저장해주십시오.", Toast.LENGTH_SHORT);
            myToast.show();
            return true;
        }

        // 액션 바 위의 저장 버튼이 눌렸을 때,
        switch (item.getItemId()) {
            case R.id.action_save :
                // 만약 현재 기능이 '새 메모 추가'였을 경우
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
                // 현재 기능이 '기존 메모 수정'이었을 경우
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

        imgPathModificationRecorder = attached_imgs_adapter.getImgPathModificationRecorder();
        beAddedPathList = imgPathModificationRecorder.getBeAddedPathList();
        Iterator i = beAddedPathList.iterator();
        int path_type = 1;
        CustomImagePath temp_path = null;
        while (i.hasNext()) {
            // 새롭게 추가될 사진들의 경로 벡터
            // 사진첩에서 선택된 경우는 1
            // 카메라로 찍은 경우는 2
            // URL 경로로 입력된 경우는 3
            temp_path = (CustomImagePath)i.next();
            final CustomImagePath finalTemp_path = temp_path;
            final String[] tempDestFIlePath = {null};
            // 사진첩에서 선택된 경우 특정 위치로 복사시켜준다.
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    if(finalTemp_path.getPathType() == 1) {
                        try {
                            tempDestFIlePath[0] = copyImageFromPath(finalTemp_path.getImagePath());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        editViewModel.insertImgPath(new ImgPathEntity(memo_id, tempDestFIlePath[0], finalTemp_path.getPathType()));
                    }
                    else {
                        editViewModel.insertImgPath(new ImgPathEntity(memo_id, finalTemp_path.getImagePath(), finalTemp_path.getPathType()));
                    }
                }
            };

            Thread t = new Thread(r);
            t.start();
        }

        // DB에서 삭제될 사진들의 iid 벡터
        beDeletedIidList = imgPathModificationRecorder.getBeDeletedIidList();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                for(int iid : beDeletedIidList) {
                    // iid로 사진경로를 가지고 온 뒤
                    ImgPathEntity t_imgPath = editViewModel.selectImgPath(iid);

                    // 사진 삭제 및 해당 iid 컬럼 삭제
                    // 사진이 local에 저장된 경우에만 사진 삭제 수행
                    String filePath = t_imgPath.getPath();
                    if(filePath.startsWith("/storage/")) {
                        File delFile = new File(filePath);
                        delFile.delete();
                    }
                    editViewModel.deleteImgPath(iid);
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
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
