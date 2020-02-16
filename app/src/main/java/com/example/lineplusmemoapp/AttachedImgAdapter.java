package com.example.lineplusmemoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AttachedImgAdapter extends RecyclerView.Adapter<AttachedImgViewHolder> implements OnAttachedImgClickListener{
    private List<AttachedImg> items = new ArrayList<>();
    private Context mContext;
    static int remember_position;

    public static final int PICK_FROM_CAMERA = 0;
    public static final int PICK_FROM_ALBUM = 1;

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
        // 정보를 받았을 때 뷰에 바로 반영해줌
        notifyDataSetChanged();
    }


    // TODO : 수정 필요함
    @Override
    public void onBindViewHolder(@NonNull AttachedImgViewHolder holder, int position) {
        AttachedImg item = items.get(position);

        final AttachedImgViewHolder tHolder = holder;

        if(item.getImg_type() == 1) {

            // String imgPath = item.getImageUri().getPath();
            String imgPath = "https://raw.githubusercontent.com/bumptech/glide/master/static/glide_logo11.png";


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
        if(items.size() != 0 && items.get(position).getImg_type() == 1){
            return 1;
        }
        // 빈 사진인 경우(사진 추가 버튼 형태의 뷰)
        return 0;
    }

    @Override
    public void onAttachedImgClick(int position) {
        // 선택된 사진의 타입이 '추가'인 경우
        remember_position = position;
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
                                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPath.makeUri());
                                            ((Activity)mContext).startActivityForResult(intent, PICK_FROM_CAMERA);
                                            break;
                                        //사진 URL 입력
                                        case 3:
                                            break;
                                    }
                                }
                            })
                    .setCancelable(true)
                    .show();
        }
        // 선택된 사진의 타입이 '기존 사진'인 경우
        else {
            // 사진을 삭제하겠냐는 메시지를 띄워준다.
        }
    }
}
