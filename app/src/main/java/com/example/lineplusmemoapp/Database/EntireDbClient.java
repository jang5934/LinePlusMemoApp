package com.example.lineplusmemoapp.Database;

import android.content.Context;

import androidx.room.Room;

public class EntireDbClient {
    private static EntireDbClient mInstance;
    private EntireDatabase entireDatabase;

    public EntireDbClient(Context mCtx) {
        entireDatabase = Room.databaseBuilder(mCtx, EntireDatabase.class, "entire_Database.db").build();
    }

    public static synchronized EntireDbClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new EntireDbClient(mCtx);
        }
        return mInstance;
    }

    public EntireDatabase getAppDatabase() {
        return entireDatabase;
    }
}
