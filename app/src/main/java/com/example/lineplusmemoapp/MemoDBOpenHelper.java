package com.example.lineplusmemoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

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
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(MemoDB.DBReference._CREATE0);
            db.execSQL(MemoDB.DBReference._CREATE1);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + MemoDB.DBReference._TABLENAME0);
            db.execSQL("DROP TABLE IF EXISTS " + MemoDB.DBReference._TABLENAME1);
            onCreate(db);
        }

        @Override
        public void onConfigure(SQLiteDatabase db) {
            super.onConfigure(db);
            if(!db.isReadOnly()) {
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    String query = String.format("PRAGMA foreign_keys = %s", "ON");
                    db.execSQL(query);
                } else {
                    db.setForeignKeyConstraintsEnabled(true);
                }
            }
        }
    }

    public MemoDBOpenHelper(Context context) {
        this.mCtx = context;
    }

    public MemoDBOpenHelper open() throws SQLException {
        mDBHelper = new MemoDBHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create() {
        mDBHelper.onCreate(mDB);
    }

    public void close() {
        mDB.close();
    }

    public long insertMemo(String subject, String content) {
        ContentValues values = new ContentValues();
        values.put(MemoDB.DBReference.SUBJECT, subject);
        values.put(MemoDB.DBReference.CONTENT, content);
        return mDB.insert(MemoDB.DBReference._TABLENAME0, null, values);
    }

    public long insertImgPath(int mid, String path) {
        ContentValues values = new ContentValues();
        values.put(MemoDB.DBReference.MID, mid);
        values.put(MemoDB.DBReference.PATH, path);
        return mDB.insert(MemoDB.DBReference._TABLENAME1, null, values);
    }

    public Cursor selectMemo() {
        return mDB.query(MemoDB.DBReference._TABLENAME0, null, null, null, null, null, MemoDB.DBReference.MID + " DESC");
    }

    public Cursor selectMemoWhereMid(int mid) {
        return mDB.query(MemoDB.DBReference._TABLENAME0, null, MemoDB.DBReference.MID + "=?", new String[]{Integer.toString(mid)}, null, null, null);
    }

    public Cursor selectImgPathWhereMid(int mid) {
        return mDB.query(MemoDB.DBReference._TABLENAME1, null, MemoDB.DBReference.MID + "=?", new String[]{Integer.toString(mid)}, null, null, null);
    }

    public Cursor selectImgPathWhereIid(int iid) {
        return mDB.query(MemoDB.DBReference._TABLENAME1, null, MemoDB.DBReference.IID + "=?", new String[]{Integer.toString(iid)}, null, null, null);
    }

    public void deleteMemo(int mid) {
        mDB.delete(MemoDB.DBReference._TABLENAME0, MemoDB.DBReference.MID + "=?", new String[]{Integer.toString(mid)});
    }

    public void deleteImgIid(int iid) {
        mDB.delete(MemoDB.DBReference._TABLENAME1, MemoDB.DBReference.IID + "=?", new String[]{Integer.toString(iid)});
    }

    public int updateMemo(int mid, String subject, String content) {
        ContentValues values = new ContentValues();
        values.put(MemoDB.DBReference.SUBJECT, subject);
        values.put(MemoDB.DBReference.CONTENT, content);

        return mDB.update(MemoDB.DBReference._TABLENAME0, values, MemoDB.DBReference.MID + "=?",
                new String[]{Integer.toString(mid)});
    }
}