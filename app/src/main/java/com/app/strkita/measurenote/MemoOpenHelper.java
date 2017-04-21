package com.app.strkita.measurenote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Calendar;
import java.util.Date;

/**
 * DBアクセス管理
 * Created by kitada on 2017/02/09.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class MemoOpenHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "measurenote.db";
    public static final int DB_VERSION = 10;
    public static final String CREATE_TABLE =
            "create table notes (" +
                    MemoContract.Notes._ID + " integer primary key autoincrement, " +
                    MemoContract.Notes.COL_BODY + " text, " +
                    MemoContract.Notes.COL_ELAPSED_TIME + " integer, " +
                    MemoContract.Notes.COL_CURRENT_COUNT + " integer, " +
                    MemoContract.Notes.COL_GOAL_COUNT + " integer, " +
                    MemoContract.Notes.COL_CREATED + " text, " +
                    MemoContract.Notes.COL_UPDATED + " text)";
    public static final String INIT_TABLE =
            "insert into notes (" +
                    MemoContract.Notes.COL_BODY + ", " +
                    MemoContract.Notes.COL_ELAPSED_TIME + ", " +
                    MemoContract.Notes.COL_CURRENT_COUNT + ", " +
                    MemoContract.Notes.COL_GOAL_COUNT + ", " +
                    MemoContract.Notes.COL_CREATED +
                    ") values " +
                    "('メジャーノートへようこそ', " +
                    "180000, " +
                    "12, " +
                    "500,  " +
                    "'2017/01/01'" +
                    ")";
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
