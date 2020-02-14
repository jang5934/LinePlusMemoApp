package com.example.lineplusmemoapp;

import android.content.Context;
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
        // 추후 구현
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView) ;
        TextView subjectTextView = (TextView) convertView.findViewById(R.id.subject) ;
        TextView previewTextView = (TextView) convertView.findViewById(R.id.preview) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        AttachedImg attachedImgItem = attachedImgList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        if(attachedImgItem.getImg_type() == 1)
            iconImageView.setImageURI(attachedImgItem.getImageUri());
        else
            iconImageView.setImageDrawable(attachedImgItem.getIcon());

        return convertView;
    }
}
