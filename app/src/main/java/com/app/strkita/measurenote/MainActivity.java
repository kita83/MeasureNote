package com.app.strkita.measurenote;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MemoOpenHelper memoOpenHelper = new MemoOpenHelper(this);
        SQLiteDatabase db = memoOpenHelper.getWritableDatabase();

//        ContentValues newUser = new ContentValues();
//        newUser.put(MemoContract.Notes.COL_BODY, "tanaka");
//        newUser.put(MemoContract.Notes.COL_SCORE, 44);
//        long newId = db.insert(
//                MemoContract.Notes.TABLE_NAME,
//                null,
//                newUser
//        );
//
//        ContentValues newScore = new ContentValues();
//        newScore.put(MemoContract.Notes.COL_SCORE, 100);
//        int updateCount = db.update(
//                MemoContract.Notes.TABLE_NAME,
//                newScore,
//                MemoContract.Notes.COL_NAME + " = ?",
//                new String[] { "fkoji" }
//        );
//
//        int deletedCount = db.delete(
//                MemoContract.Notes.TABLE_NAME,
//                MemoContract.Notes.COL_NAME + " = ?",
//                new String[] { "dotinstall" }
//        );

        Cursor c = null;
        c = db.query(
                MemoContract.Notes.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Log.v("DB_TEST", "Count: " + c.getCount());

        while(c.moveToNext()) {
            int id = c.getInt(c.getColumnIndex((MemoContract.Notes._ID)));
            String body = c.getString(c.getColumnIndex((MemoContract.Notes.COL_BODY)));
            int elapsedTime = c.getInt(c.getColumnIndex((MemoContract.Notes.COL_ELAPSED_TIME)));
            int goalCount = c.getInt(c.getColumnIndex((MemoContract.Notes.COL_GOAL_COUNT)));
            int created = c.getInt(c.getColumnIndex((MemoContract.Notes.COL_CREATED)));
            int updated = c.getInt(c.getColumnIndex((MemoContract.Notes.COL_UPDATED)));
            Log.v("DB_DEBUG", " id: " + id + " body: " + body + " elapsedTime: " + elapsedTime +
                    " goalCount: " + goalCount + " created: " + created + " updated: " + updated);
        }
        c.close();
        db.close();
    }
}
