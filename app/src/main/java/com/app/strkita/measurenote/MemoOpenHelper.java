package com.app.strkita.measurenote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * DBアクセス管理
 * Created by kitada on 2017/02/09.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class MemoOpenHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "measurenote.db";
    public static final int DB_VERSION = 15;
    public static final String CREATE_TABLE =
            "create table notes (" +
                    MemoContract.Notes._ID + " integer primary key autoincrement, " +
                    MemoContract.Notes.COL_BODY + " text, " +
                    MemoContract.Notes.COL_ELAPSED_TIME + " integer, " +
                    MemoContract.Notes.COL_CURRENT_COUNT + " text, " +
                    MemoContract.Notes.COL_GOAL_COUNT + " text, " +
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
                    "('メジャーノートへようこそ。\n" +
                    "シンプルなこのメモアプリには３つの特徴があります。\n" +
                    "\n" +
                    "1. 時間計測機能\n" +
                    "メモを作成してからの経過時間を計測し、記録します。\n" +
                    "編集を開始すると自動的にタイマーが動き始めます。\n" +
                    "上部にある一時停止ボタンを押すことで、計測を一時ストップさせることが可能です。\n" +
                    "\n" +
                    "2. 文字数カウント機能\n" +
                    "作成中の文字数をリアルタイムでカウントします。\n" +
                    "\n" +
                    "3. 目標文字数設定機能\n" +
                    "目標文字数を設定し、達成までの時間を測ることで自分の文章作成スピードを知り、ブログなどの記事作成などに役立てることができます。\n" +
                    "\n" +
                    "このアプリは少しづつ機能や使い勝手をアップデートしていく予定です。', " +
                    "180000, " +
                    "'292', " +
                    "'300',  " +
                    "'"+ getNowDate() + "'" +
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

    /**
     * 現在日時を取得
     */
    public static String getNowDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }
}
