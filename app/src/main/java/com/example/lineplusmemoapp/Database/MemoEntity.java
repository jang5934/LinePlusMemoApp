package com.example.lineplusmemoapp.Database;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tb_memo")
public class MemoEntity {

    @PrimaryKey(autoGenerate = true)
    int mid;
    String subject;
    String content;

    public MemoEntity(int mid, String subject, String content) {
        this.mid = mid;
        this.subject = subject;
        this.content = content;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MemoEntity{" +
                "mid=" + mid +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public static DiffUtil.ItemCallback<MemoEntity> DIFF_CALLBACK = new  DiffUtil.ItemCallback<MemoEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull MemoEntity oldItem, @NonNull MemoEntity newItem) {
            return oldItem.mid == newItem.mid;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MemoEntity oldItem, @NonNull MemoEntity newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        MemoEntity memoEntity = (MemoEntity) obj;
        return memoEntity.mid == this.mid;
    }
}
