package com.example.lineplusmemoapp;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

// https://bumptech.github.io/glide/
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class MemoReadActivity extends AppCompatActivity {

    private ReadViewModel readViewModel;
    private int memo_id;
    private Context currentContext;
    private ArrayList<MemoImageData> imgPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_read);
        currentContext = getBaseContext();

        // 이전 액티비티에서 선택된 메모의 id 획득
        Intent intent = getIntent();
        memo_id = intent.getExtras().getInt("mid");

        // 전체 틀 선택
        RelativeLayout parentReadLayout = findViewById(R.id.read_page_layout);
        parentReadLayout.removeAllViews();

        // 제목 단 생성
        LinearLayout subjectLayout = new LinearLayout(this);
        subjectLayout.setOrientation(LinearLayout.HORIZONTAL);

        RelativeLayout.LayoutParams subjectLayoutParams = new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        subjectLayout.setLayoutParams(subjectLayoutParams);

        // 각 텍스트뷰에 내용 삽입
        TextView tSubject_left = new TextView(this);
        tSubject_left.setText("제목 : ");
        tSubject_left.setTextColor(Color.parseColor("#000000"));
        tSubject_left.setTextSize(25);

        final TextView tSubject_right = new TextView(this);
        tSubject_right.setTextSize(20);
        tSubject_right.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

        LinearLayout.LayoutParams linearLayoutParams_left = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayoutParams_left.weight = 4;
        tSubject_left.setLayoutParams(linearLayoutParams_left);

        LinearLayout.LayoutParams linearLayoutParams_right = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayoutParams_right.weight = 1;
        tSubject_right.setLayoutParams(linearLayoutParams_right);

        // 제목 레이어에 텍스트뷰 삽입
        subjectLayout.addView(tSubject_left);
        subjectLayout.addView(tSubject_right);
        // 전체 레이어에 제목 레이어 삽입
        parentReadLayout.addView(subjectLayout);


        // 내용 레이아웃 생성
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.HORIZONTAL);

        RelativeLayout.LayoutParams contentLayoutParams = new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        subjectLayout.setId(View.generateViewId());
        contentLayoutParams.addRule(RelativeLayout.BELOW, subjectLayout.getId());
        contentLayout.setLayoutParams(contentLayoutParams);

        // 내용 텍스트 뷰 생성
        TextView tContent_left = new TextView(this);
        tContent_left.setText("내용 : ");
        tContent_left.setTextSize(25);
        tContent_left.setTextColor(Color.parseColor("#000000"));
        tContent_left.setLayoutParams(linearLayoutParams_left);

        // 읽기 페이지의 내용_레이아웃 생성
        final LinearLayout content_container = new LinearLayout(this);
        content_container.setOrientation(LinearLayout.VERTICAL);
        content_container.setLayoutParams(linearLayoutParams_right);

        // 내용 뷰 생성
        final TextView tContent_right = new TextView(this);
        tContent_right.setTextSize(20);

        content_container.addView(tContent_right);

        contentLayout.addView(tContent_left);
        contentLayout.addView(content_container);

        parentReadLayout.addView(contentLayout);

        // 읽기 뷰 모델 호출
        readViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(ReadViewModel.class);

        readViewModel.setMemo_id(memo_id);
        readViewModel.setRequestingContextt(currentContext);

        final LinearLayout.LayoutParams imageParams =
                new LinearLayout.LayoutParams(300, 300, 1f);
        imageParams.topMargin = 20;

        final Observer<MemoData> dataObserver = new Observer<MemoData>() {
            @Override
            public void onChanged(MemoData data) {
                tSubject_right.setText(data.getSubject());
                tContent_right.setText(data.getMemoContent());

                if(imgPaths != null)
                    imgPaths.clear();

                imgPaths = data.getImages();

                for (final MemoImageData imgPath : imgPaths) {
                    ImageView iv = new ImageView(currentContext);  // 새로 추가할 imageView 생성
                    iv.setLayoutParams(imageParams);  // imageView layout 설정
                    content_container.addView(iv); // 기존 linearLayout에 imageView 추가

                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), ImageViewerActivity.class);
                            intent.putExtra("path", imgPath.getImagePath());
                            startActivity(intent);
                        }
                    });

                    // https://bumptech.github.io/glide/
                    Glide.with(currentContext)
                            .load(imgPath.getImagePath())
                            .error(R.mipmap.error_image)
                            .into(iv);
                }
            }
        };

        // LiveData를 관찰하고 관찰한 데이터를 이 액티비티에 넘기도록 설정.
        readViewModel.getCurrentData().observe(this, dataObserver);

        // 액션 바 이름 설정 - '읽기 페이지'
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.read);
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

                                // delete cascade로 구현했기 때문에 메모를 지우기 전 이미지 경로들을 참고하여 이미지부터 삭제한다.
                                // 사진이 local에 저장된 경우에만 사진 삭제 수행
                                Cursor iCursor = openHelper.selectImgPathWhereMid(memo_id);
                                while(iCursor.moveToNext()) {
                                    String filePath = iCursor.getString(iCursor.getColumnIndex("path"));
                                    int path_type = iCursor.getInt(iCursor.getColumnIndex("path_type"));
                                    if(path_type != 3)
                                        new File(filePath).delete();
                                }

                                // 그리고 나서 메모를 삭제한다.
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
