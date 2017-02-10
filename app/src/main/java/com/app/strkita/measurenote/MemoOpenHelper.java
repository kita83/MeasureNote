package com.app.strkita.measurenote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kitada on 2017/02/09.
 */

public class MemoOpenHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "measurenote.db";
    public static final int DB_VERSION = 1;
    public static final String CREATE_TABLE =
            "create table notes (" +
                    "_id integer primary key autoincrement, " +
                    "title text, " +
                    "body text, " +
                    "time integer, " +
                    "created datetime default current_timestamp, " +
                    "updated datetime default current_timestamp)";
    public static final String INIT_TABLE =
            "insert into notes (title, body, time) values " +
                    "('title1', 'body1body1body1body1', 1257), " +
                    "('title2title2', 'body2body2body2body2body2body2body2', 2963), " +
                    "('title3', 'body3body3body3body3body3', 3945)";
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
