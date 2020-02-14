package com.example.lineplusmemoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MemoDBOpenHelper {
    private static final String DATABASE_NAME = "content.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private MemoDBHelper mDBHelper;
    private Context mCtx;

    private class MemoDBHelper extends SQLiteOpenHelper {

        public MemoDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(MemoDB.CreateDB._CREATE0);
            db.execSQL(MemoDB.CreateDB._CREATE1);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+MemoDB.CreateDB._TABLENAME0);
            db.execSQL("DROP TABLE IF EXISTS "+MemoDB.CreateDB._TABLENAME1);
            onCreate(db);
        }
    }

    public MemoDBOpenHelper(Context context){
        this.mCtx = context;
    }

    public MemoDBOpenHelper open() throws SQLException{
        mDBHelper = new MemoDBHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create(){
        mDBHelper.onCreate(mDB);
    }

    public void close(){
        mDB.close();
    }

    public long insertMemo(String subject, String content){
        ContentValues values = new ContentValues();
        values.put(MemoDB.CreateDB.SUBJECT, subject);
        values.put(MemoDB.CreateDB.CONTENT, content);
        return mDB.insert(MemoDB.CreateDB._TABLENAME0, null, values);
    }

    public long insertImgPath(int mid, String path){
        ContentValues values = new ContentValues();
        values.put(MemoDB.CreateDB.MID, mid);
        values.put(MemoDB.CreateDB.PATH, path);
        return mDB.insert(MemoDB.CreateDB._TABLENAME1, null, values);
    }

    public Cursor selectMemo(){
        return mDB.query(MemoDB.CreateDB._TABLENAME0, null, null, null, null, null, null);
    }

    public Cursor selectImgPath(){
        return mDB.query(MemoDB.CreateDB._TABLENAME1, null, null, null, null, null, null);
    }
}
