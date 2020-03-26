package com.example.lineplusmemoapp.Database;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "tb_img_path",
        indices = {@Index ("iid")},
        foreignKeys = @ForeignKey(onDelete = CASCADE, entity = MemoEntity.class,
        parentColumns = "mid",
        childColumns = "mid"))

public class ImgPathEntity {
    @PrimaryKey(autoGenerate = true)
    int iid;
    @ColumnInfo(name = "mid")
    int mid;
    @ColumnInfo(name = "path")
    String path;
    @ColumnInfo(name = "path_type")
    int path_type;

    public ImgPathEntity(int mid, String path, int path_type) {
        this.mid = mid;
        this.path = path;
        this.path_type = path_type;
    }

    public int getIid() {
        return iid;
    }

    public void setIid(int iid) {
        this.iid = iid;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPath_type() {
        return path_type;
    }

    public void setPath_type(int path_type) {
        this.path_type = path_type;
    }

    @Override
    public String toString() {
        return "ImgPathEntity{" +
                "iid=" + iid +
                ", mid=" + iid +
                ", path='" + path + '\'' +
                ", path_type=" + path_type +
                '}';
    }

    public static DiffUtil.ItemCallback<ImgPathEntity> DIFF_CALLBACK = new  DiffUtil.ItemCallback<ImgPathEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull ImgPathEntity oldItem, @NonNull ImgPathEntity newItem) {
            return oldItem.iid == newItem.iid;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ImgPathEntity oldItem, @NonNull ImgPathEntity newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        ImgPathEntity imgPathEntity = (ImgPathEntity) obj;
        return imgPathEntity.iid == this.iid;
    }
}
