package com.example.lineplusmemoapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

public class AttachedImgViewHolder extends RecyclerView.ViewHolder {

    public ImageView mImg;
    public ProgressBar mProg;

    public AttachedImgViewHolder(View itemView) {
        super(itemView);
        mImg = (ImageView) itemView.findViewById(R.id.attached_image);
        mProg = (ProgressBar) itemView.findViewById(R.id.progress);
    }
}
