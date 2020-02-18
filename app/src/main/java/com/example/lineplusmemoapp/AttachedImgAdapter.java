package com.example.lineplusmemoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class AttachedImgAdapter extends RecyclerView.Adapter<AttachedImgViewHolder> implements OnAttachedImgClickListener{

    private List<AttachedImg> items = new ArrayList<>();

    // 저장 버튼이 눌렸을 때 반환할 추가/삭제할 이미지 정보 벡터
    private Vector<String> beAddedPathList = new Vector<>();
    private Vector<String> beDeletedIidList = new Vector<>();

    private Context mContext;
    private Uri cameraImageUri;

    public static final int PICK_FROM_ALBUM = 1;
    public static final int PICK_FROM_CAMERA = 2;

    public AttachedImgAdapter (@NonNull Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public AttachedImgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        if (viewType == 1) { // 현재 뷰가 사진을 포함한 뷰인 경우
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.attached_image, parent, false);
        }
        else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_image, parent, false);
        }

        AttachedImgViewHolder holder = new AttachedImgViewHolder(v);
        holder.setOnAttachedImgClickListener(this);

        return holder;
    }

    public void add(AttachedImg item) {
        items.add(item);
        if(item.getImg_type() > 1)
            beAddedPathList.add(item.getImgPath());
        // 뷰에 바로 반영해줌
        notifyDataSetChanged();
    }


    // TODO : 수정 필요함
    @Override
    public void onBindViewHolder(@NonNull AttachedImgViewHolder holder, int position) {
        AttachedImg item = items.get(position);

        final AttachedImgViewHolder tHolder = holder;

        if(item.getImg_type() != 0) {
            String imgPath = item.getImgPath();
            Glide.with(holder.mImg)
                 .load(imgPath)
                 .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        tHolder.mProg.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        tHolder.mProg.setVisibility(View.GONE);
                        return false;
                    }
                })
                    .error(R.mipmap.error_image)
                    .into(holder.mImg);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        // 사진이 담겨야하는 객체인 경우,
        if(items.size() != 0 && items.get(position).getImg_type() != 0){
            return 1;
        }
        // 빈 사진인 경우(사진 추가 버튼 형태의 뷰)
        return 0;
    }

    @Override
    public void onAttachedImgClick(int position) {
        if(items.get(position).getImg_type() == 0) {
            // 사진 추가 메뉴를 띄워준다.
            new AlertDialog.Builder(mContext)
                    .setTitle("사진 추가")
                    .setItems(R.array.img_path_menu,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent;

                                    switch(which)
                                    {
                                        //사진첩에서 첨부
                                        case 1:
                                            intent = new Intent(Intent.ACTION_PICK);
                                            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                            ((Activity)mContext).startActivityForResult(intent, PICK_FROM_ALBUM);
                                            break;
                                        //카메라 촬영
                                        case 2:
                                            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            ImgPathMaker uriPath = new ImgPathMaker(mContext);
                                            cameraImageUri = uriPath.makeUri();
                                            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                                            ((Activity)mContext).startActivityForResult(intent, PICK_FROM_CAMERA);

                                            break;
                                        //사진 URL 입력
                                        case 3:
                                            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mContext);
                                            View mView = layoutInflaterAndroid.inflate(R.layout.url_input_dialog, null);
                                            AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(mContext);
                                            alertDialogBuilderUserInput.setView(mView);

                                            final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.urlInputDialog);
                                            alertDialogBuilderUserInput
                                                    .setCancelable(false)
                                                    .setPositiveButton("추가", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialogBox, int id) {
                                                            if(userInputDialogEditText.getText().toString().length() != 0)
                                                                add(new AttachedImg(4, userInputDialogEditText.getText().toString()));
                                                        }
                                                    })
                                                    .setNegativeButton("취소",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialogBox, int id) {
                                                                    dialogBox.cancel();
                                                                }
                                                            });

                                            AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                                            alertDialogAndroid.show();
                                            break;
                                    }
                                }
                            })
                    .setCancelable(true)
                    .show();
        }
        // 선택된 사진의 타입이 편집 페이지에 올라와있는 사진인 경우
        else {
            // 사진을 삭제하겠냐는 메시지를 띄워준다.
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            final int tPos = position;
            builder.setTitle("첨부 사진 제거")
            .setMessage("사진을 첨부 리스트에서 제거하시겠습니까?")
            .setPositiveButton("제거", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(items.get(tPos).getImg_type() == 1)
                        beDeletedIidList.add(Integer.toString(items.get(tPos).getIid()));
                    items.remove(items.get(tPos));
                    // 뷰에 바로 반영해줌
                    notifyDataSetChanged();
                }
            }).setNegativeButton("취소", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public Uri getCameraImageUri() {
        return cameraImageUri;
    }

    public Vector<String> getBeAddedPathList() {
        return beAddedPathList;
    }

    public Vector<String> getBeDeletedIidList() {
        return beDeletedIidList;
    }
}
