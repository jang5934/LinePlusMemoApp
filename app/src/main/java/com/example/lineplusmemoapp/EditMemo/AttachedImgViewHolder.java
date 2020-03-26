package com.example.lineplusmemoapp.EditMemo;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lineplusmemoapp.R;

public class AttachedImgViewHolder extends RecyclerView.ViewHolder {

    public ImageView mImg;
    public ProgressBar mProg;
    public OnAttachedImgClickListener mListener;
    public ImageView mIconDel;

    public AttachedImgViewHolder(View itemView) {
        super(itemView);

        mImg = (ImageView) itemView.findViewById(R.id.attached_image);
        mIconDel = (ImageView) itemView.findViewById(R.id.delete_icon);
        mProg = (ProgressBar) itemView.findViewById(R.id.progress);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAttachedImgClick(getAdapterPosition());
            }
        });
    }

    public void setOnAttachedImgClickListener(OnAttachedImgClickListener onAttachedImgClickListener) {
        mListener = onAttachedImgClickListener;
    }
}
