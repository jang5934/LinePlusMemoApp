package com.example.lineplusmemoapp.MemoList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

// https://bumptech.github.io/glide/
import com.bumptech.glide.Glide;
import com.example.lineplusmemoapp.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<>() ;

    // Adapter에 사용되는 데이터의 개수를 리턴.
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = convertView.findViewById(R.id.imageView) ;
        TextView subjectTextView = convertView.findViewById(R.id.subject) ;
        TextView previewTextView = convertView.findViewById(R.id.preview) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        if(listViewItem.getIcon() == null) {
            // https://bumptech.github.io/glide/
            Glide.with(iconImageView)
                    .load(listViewItem.getImageUri())
                    .error(R.mipmap.error_image)
                    .into(iconImageView);
        }
        else
            iconImageView.setImageDrawable(listViewItem.getIcon());

        iconImageView.getLayoutParams().width = 200;
        iconImageView.getLayoutParams().height = 200;

        subjectTextView.setText(listViewItem.getSubject());
        previewTextView.setText(listViewItem.getPreview());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    // 메모에 첨부된 사진이 없을 경우에 실행되는 메소드
    public void addItem(int id, Drawable icon, String subject, String preview) {
        ListViewItem item = new ListViewItem();

        item.setMid(id);
        item.setIcon(icon);
        item.setImageuri(null);
        item.setSubject(subject);
        item.setPreview(preview);

        listViewItemList.add(item);
    }

    // 메모에 첨부된 사진이 하나 이상인 경우에 실행되는 메소드
    public void addItem(int id, String imgpath, String subject, String preview, int path_type) {
        ListViewItem item = new ListViewItem();

        item.setMid(id);
        item.setIcon(null);
        item.setImageuri(imgpath);
        item.setSubject(subject);
        item.setPreview(preview);
        item.setPathType(path_type);

        listViewItemList.add(item);
    }
}