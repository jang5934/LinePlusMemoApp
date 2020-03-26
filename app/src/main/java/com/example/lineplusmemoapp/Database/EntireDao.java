package com.example.lineplusmemoapp.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public abstract class EntireDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertMemo(MemoEntity entity);

    @Query("SELECT * FROM tb_memo")
    public abstract LiveData<List<MemoAndImgPathEntity>> selectMemoAndImgPathEntity();

    @Query("UPDATE tb_memo SET subject = :subject, content =:content WHERE mid = :mid")
    public abstract void updateMemo(int mid, String subject, String content);

    @Query("DELETE FROM tb_memo WHERE mid = :mid")
    public abstract void deleteMemo(int mid);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertImgPath(ImgPathEntity entity);

    @Query("SELECT * FROM tb_img_path WHERE iid = :iid")
    public abstract ImgPathEntity selectImgPathWhereIid(int iid);

    @Query("DELETE FROM tb_img_path WHERE iid = :iid")
    public abstract void deleteImgPath(int iid);

    @Query("SELECT * FROM tb_memo WHERE mid = :mid")
    public abstract LiveData<MemoAndImgPathEntity> selectImgPathWhereMid(int mid);

    @Query("SELECT * FROM tb_memo WHERE mid = :mid")
    public abstract MemoAndImgPathEntity selectCurrentMemoAndImgPathWhereMid(int mid);
}
