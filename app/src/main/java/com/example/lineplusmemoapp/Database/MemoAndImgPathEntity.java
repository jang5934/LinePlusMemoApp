package com.example.lineplusmemoapp.Database;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class MemoAndImgPathEntity {
    @Embedded
    MemoEntity memoEntity;
    @Relation(parentColumn = "mid",
            entityColumn = "mid")
    List<ImgPathEntity> imgPaths = new ArrayList();

    public MemoEntity getMemoEntity() {
        return memoEntity;
    }

    public void setMemoEntity(MemoEntity memoEntity) {
        this.memoEntity = memoEntity;
    }

    public List<ImgPathEntity> getImgPaths() {
        return imgPaths;
    }

    public void setImgPaths(List<ImgPathEntity> imgPaths) {
        this.imgPaths = imgPaths;
    }
}
