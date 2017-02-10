package com.app.strkita.measurenote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DBアクセス管理
 * Created by kitada on 2017/02/09.
 */

public class MemoOpenHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "measurenote.db";
    public static final int DB_VERSION = 1;
    public static final String CREATE_TABLE =
            "create table notes (" +
                    "_id integer primary key autoincrement, " +
                    "body text, " +
                    "elapsed_time integer, " +
                    "goal_count integer, " +
                    "created integer, " +
                    "updated integer default current_timestamp)";
    public static final String INIT_TABLE =
            "insert into notes (body, elapsed_time, goal_count, created) values " +
                    "('今日は雪が降った。寒かった', 1257, 3000, datetime('now', 'localtime')), " +
                    "('body2body2body2body2body2body2body2', 2963, 5000, datetime('now', 'localtime') ), " +
                    "('This is my first note.', 3945, 800, datetime('now', 'localtime') )";
    public static final String DROP_TABLE =
            "drop table if exists " + MemoContract.Notes.TABLE_NAME;

    public MemoOpenHelper(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(INIT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
