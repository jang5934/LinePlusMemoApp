package com.example.lineplusmemoapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AttachedImgAdapter extends RecyclerView.Adapter<AttachedImgViewHolder> {
    private ArrayList<AttachedImg> attachedImgList = new ArrayList<AttachedImg>() ;
    @NonNull
    @Override
    public AttachedImgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.attached_image, parent, false
        );
        return new AttachedImgViewHolder(v);
    }

    List<AttachedImg> items = new ArrayList<>();
    public void add(AttachedImg item) {
        items.add(item);
        // 정보를 받았을 때 뷰에 바로 반영해줌
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull AttachedImgViewHolder holder, int position) {
        AttachedImg item = items.get(position);

        /*
        String fileName= "https://cdn5.vectorstock.com/i/thumbs/78/24/blue-glossy-button-blank-icon-square-empty-shape-vector-12937824.jpg";
        Uri fileUri = Uri.parse(fileName);
        */

        if(item.getImg_type() == 1) {
            holder.mImg.setImageURI(item.getImageUri());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
