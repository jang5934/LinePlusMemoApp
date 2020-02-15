package com.example.lineplusmemoapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class AttachedImgAdapter extends RecyclerView.Adapter<AttachedImgViewHolder> {
    private ArrayList<AttachedImg> attachedImgList = new ArrayList<AttachedImg>() ;
    @NonNull
    @Override
    public AttachedImgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        // TODO 각 뷰의 레이아웃의 중류가 나뉘어져야 하는 경우 수정할 것
        if (viewType == 1) { // 현재 뷰가 사진을 포함한 뷰인 경우
            v = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.attached_image, parent, false);
        }
        else {
            v = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.attached_image, parent, false);
        }

        return new AttachedImgViewHolder(v);
    }

    List<AttachedImg> items = new ArrayList<>();
    public void add(AttachedImg item) {
        items.add(item);
        // 정보를 받았을 때 뷰에 바로 반영해줌
        notifyDataSetChanged();
    }


    // TODO : 수정 필요함
    @Override
    public void onBindViewHolder(@NonNull AttachedImgViewHolder holder, int position) {
        AttachedImg item = items.get(position);

        if(item.getImg_type() == 1) {
            // String imgPath = item.getImageUri().getPath();
            String imgPath = "https://bit.ly/2V1ipNj";

            // Glide.with(holder.mImg).load(imgPath).placeholder(R.drawable.placeholder).error(R.drawable.error_image).into(holder.mImg);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        // 사진이 담겨야하는 객체인 경우,
        if(attachedImgList.size() != 0 && attachedImgList.get(position).getImg_type() == 1){
            return 1;
        }
        // 빈 사진인 경우(사진 추가 버튼 형태의 뷰)
        return 0;
    }
}
