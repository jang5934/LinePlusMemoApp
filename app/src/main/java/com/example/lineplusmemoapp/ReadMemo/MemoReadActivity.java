package com.example.lineplusmemoapp.ReadMemo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.lineplusmemoapp.Database.ImgPathEntity;
import com.example.lineplusmemoapp.Database.MemoAndImgPathEntity;
import com.example.lineplusmemoapp.EditMemo.MemoEditActivity;
import com.example.lineplusmemoapp.R;

import java.io.File;
import java.util.List;

public class MemoReadActivity extends AppCompatActivity {

    // 현재 액티비티에서 상수처럼 들고 있을 메모의 id번호
    private int memo_id;
    // 메모의 데이터들을 받기 위한 뷰-모델객체와 첨부사진경로 객체
    private ReadViewModel readViewModel;
    private List<ImgPathEntity> imgPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_read);

        // 이전 액티비티에서 선택된 메모의 mid 획득
        Intent intent = getIntent();
        memo_id = intent.getExtras().getInt("mid");

        // 전체 틀 선택
        RelativeLayout parentReadLayout = findViewById(R.id.read_page_layout);
        // parentReadLayout.removeAllViews();

        /** 제목 단 생성 시작 **/
        // 제목레이어 생성
        LinearLayout subjectLayout = new LinearLayout(this);
        // 제목레이어는 가로로 쌓아올림
        subjectLayout.setOrientation(LinearLayout.HORIZONTAL);
        // 제목레이어 사이즈 설정
        RelativeLayout.LayoutParams subjectLayoutParams = new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subjectLayout.setLayoutParams(subjectLayoutParams);

        // '제목 : '을 담는 뷰 생성
        TextView tSubject_left = TvSetter("제목 : ");
        // 메모의 제목을 담는 뷰 생성
        final TextView tSubject_right = TvSetter();

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
        /** 제목 단 생성 끝 **/

        /** 내용 단 생성 시작 **/
        // 내용 레이아웃 생성
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.HORIZONTAL);

        RelativeLayout.LayoutParams contentLayoutParams = new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        subjectLayout.setId(View.generateViewId());
        contentLayoutParams.addRule(RelativeLayout.BELOW, subjectLayout.getId());
        contentLayout.setLayoutParams(contentLayoutParams);

        // '내용 : '을 담는 뷰 생성
        TextView tContent_left = TvSetter("내용 : ");
        tContent_left.setLayoutParams(linearLayoutParams_left);

        // 메모 내용이 담겨질 내용 컨테이너 레이아웃 생성
        final LinearLayout content_container = new LinearLayout(this);
        content_container.setOrientation(LinearLayout.VERTICAL);
        content_container.setLayoutParams(linearLayoutParams_right);

        // 내용 단에 들어갈 각 뷰들 삽입
        contentLayout.addView(tContent_left);
        contentLayout.addView(content_container);

        // 현 액티비티의 최상단 뷰에 내용 레이어 삽입
        parentReadLayout.addView(contentLayout);

        // DB 데이터를 가져오기 위한 읽기 뷰 모델 호출
        readViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(ReadViewModel.class);
        final LinearLayout.LayoutParams imageParams =
                new LinearLayout.LayoutParams(300, 300, 1f);
        imageParams.topMargin = 20;

        // LiveData를 관찰하고 관찰한 데이터를 이 액티비티에 넘기도록 설정.
        // 아래의 옵저버에서는 메모의 제목, 내용 그리고 첨부된 사진들이 담길 뷰에 대해서만 변경.
        readViewModel.getCurrentMemoAndImgPath(memo_id).observe(this, new Observer<MemoAndImgPathEntity>() {
            @Override
            public void onChanged(MemoAndImgPathEntity data) {
                if(data == null)
                    return;

                // 사진이 뷰 형태로 추가되어 있어 현 액티비티로 '돌아오는' 경우에 메모의 사진을 DB에서 지웠어도
                // 이미지 뷰 형태로는 남기 때문에 직접 제거해준다.
                content_container.removeAllViews();

                // 메모 제목 삽입
                tSubject_right.setText(data.getMemoEntity().getSubject());

                // 메모 내용 삽입
                // 메모 내용은 메모 내용 컨테이너 레이아웃에 직접 삽입되어
                // 위의 removeAllViews() 호출 당시에 텍스트뷰가 지워지므로 여기서 텍스트뷰를 새로 할당해준다.
                TextView tContent_right = new TextView(getApplicationContext());
                tContent_right.setTextSize(20);
                tContent_right.setText(data.getMemoEntity().getContent());
                content_container.addView(tContent_right);

                // 현 메모의 첨부 사진 정보들을 받아오고
                // 그대로 content_container에 추가해준다.
                imgPaths = data.getImgPaths();
                for (final ImgPathEntity imgPath : imgPaths) {
                    ImageView iv = new ImageView(getBaseContext());  // 새로 추가할 imageView 생성
                    iv.setLayoutParams(imageParams);  // imageView layout 설정
                    content_container.addView(iv); // 기존 linearLayout에 imageView 추가

                    // 사진 크게 보기 액티비티로 이동하는 클릭 이벤트 리스너 생성
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), ImageViewerActivity.class);
                            intent.putExtra("path", imgPath.getPath());
                            startActivity(intent);
                        }
                    });
                    // https://bumptech.github.io/glide/
                    Glide.with(getBaseContext())
                            .load(imgPath.getPath())
                            .error(R.mipmap.error_image)
                            .into(iv);
                }
            }
        });
        /** 내용 단 생성 끝 **/

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
        switch (item.getItemId()) {
            case R.id.action_edit :
                // 메모 수정버튼의 경우
                // 현재 가지고있는 메모id와 편집 태그를 가지고 메모 수정 액티비티 이동
                Intent intent = new Intent(getApplicationContext(), MemoEditActivity.class);
                intent.putExtra("type", "edit_memo");
                intent.putExtra("mid", memo_id);
                startActivity(intent);
                return true;
            case R.id.action_delete :
                // 메모 삭제버튼의 경우
                AlertDialog.Builder confirm = new AlertDialog.Builder(this);
                confirm.setTitle("메모 삭제")
                        .setMessage("정말 삭제하시겠습니까?")
                        .setPositiveButton( "확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int button) {
                                Runnable r = new Runnable() {
                                    @Override
                                    public void run() {
                                        // 현재 메모를 삭제하는 쓰레드
                                        // 메모를 지우기 전 이미지 경로들을 참고하여 이미지부터 삭제한다.
                                        // 사진이 로컬에 저장된 경우에만 사진 삭제 수행
                                        for(ImgPathEntity t_Path : imgPaths) {
                                            if(t_Path.getPath_type() != 3)
                                                new File(t_Path.getPath()).delete();
                                        }
                                        // 그리고 나서 메모를 삭제한다.
                                        readViewModel.deleteCurrentMemoAndImgPath(memo_id);
                                    }
                                };
                                Thread t = new Thread(r);
                                t.start();
                                Toast.makeText(MemoReadActivity.this, "메모가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
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

    // 좌측에 삽입될 TV의 경우에 호출될 메서드
    public TextView TvSetter (String innerString) {
        TextView tv = new TextView(this);
        tv.setText(innerString);
        tv.setTextColor(Color.parseColor("#000000"));
        tv.setTextSize(25);
        return tv;
    }

    // 우측에 삽입될 TV의 경우에 호출될 메서드
    public TextView TvSetter () {
        TextView tv = new TextView(this);
        tv.setTextSize(20);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        return tv;
    }
}
