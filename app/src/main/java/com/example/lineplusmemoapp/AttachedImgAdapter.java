package com.example.lineplusmemoapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AttachedImgAdapter extends RecyclerView.Adapter<AttachedImgViewHolder> {
    @NonNull
    @Override
    public AttachedImgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.attached_image, parent, false
        );
        return new AttachedImgViewHolder(v);
    }

    List<AttachedImg> items = new ArrayList<>();
    public void add(AttachedImg data) {
        items.add(data);
        // 정보를 받았을 때 뷰에 바로 반영해줌
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull AttachedImgViewHolder holder, int position) {
        AttachedImg item = items.get(position);
        // 추후 구현
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
