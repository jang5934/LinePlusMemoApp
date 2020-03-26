package com.example.lineplusmemoapp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(version = 2, entities = {MemoEntity.class, ImgPathEntity.class})
public abstract class EntireDatabase extends RoomDatabase {
    abstract public EntireDao entireDao();
}
