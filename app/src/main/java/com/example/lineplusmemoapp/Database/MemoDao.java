package com.example.lineplusmemoapp.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MemoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(MemoEntity entity);

    @Query("DELETE FROM tb_memo WHERE mid = :mid")
    public void delete(int mid);

    @Delete
    public void delete(MemoEntity entity);

    @Query("DELETE FROM tb_memo")
    public void deleteAll();

    @Query("SELECT * FROM tb_memo")
    public LiveData<List<MemoEntity>> select();
}
