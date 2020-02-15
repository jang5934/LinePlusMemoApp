package com.example.lineplusmemoapp;

import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

public class AttachedImgAdapter extends RecyclerView.Adapter<AttachedImgViewHolder> {
    private List<AttachedImg> items = new ArrayList<>();
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
        return new AttachedImgViewHolder(v);
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
}
