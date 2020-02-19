package com.example.lineplusmemoapp;

import android.provider.BaseColumns;

public final class MemoDB {
    public static final class DBReference implements BaseColumns {
        public static final String MID = "mid";
        public static final String SUBJECT = "subject";
        public static final String CONTENT = "content";
        public static final String _TABLENAME0 = "tb_memo";
        public static final String _CREATE0 = "create table if not exists "+_TABLENAME0+"("
                +MID+" integer primary key autoincrement, "
                +SUBJECT+" text not null , "
                +CONTENT+" text not null );";

        public static final String IID = "iid";
        public static final String PATH = "path";
        public static final String PATHTYPE = "path_type";
        public static final String _TABLENAME1 = "tb_img_path";
        public static final String _CREATE1 = "create table if not exists "+_TABLENAME1+"("
                +IID+" integer primary key autoincrement, "
                +MID+" integer references tb_memo (mid) on delete cascade,"
                +PATH+" text not null,"
                +PATHTYPE + " integer not null );";
    }
}
