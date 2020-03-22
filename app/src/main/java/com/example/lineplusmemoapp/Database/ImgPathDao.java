package com.example.lineplusmemoapp.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ImgPathDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(ImgPathEntity entity);

    @Query("DELETE FROM tb_img_path WHERE iid = :iid")
    public void delete(int iid);

    @Delete
    public void delete(ImgPathEntity entity);

    @Query("SELECT * FROM tb_img_path")
    public LiveData<List<ImgPathEntity>> select();
}
