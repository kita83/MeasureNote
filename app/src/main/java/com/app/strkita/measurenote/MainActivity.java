package com.app.strkita.measurenote;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.noteList);

        // DB
        MemoOpenHelper memoOpenHelper = new MemoOpenHelper(this);
        SQLiteDatabase db = memoOpenHelper.getWritableDatabase();

        ArrayList<ListItems> items = new ArrayList<ListItems>();

        // select
        Cursor c = db.query(
                MemoContract.Notes.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        boolean isEof = c.moveToLast();
        while (isEof) {
            ListItems item = new ListItems();
            item.setBody(c.getString(c.getColumnIndex(MemoContract.Notes.COL_BODY)));
            item.setGoalCount(c.getInt(c.getColumnIndex(MemoContract.Notes.COL_GOAL_COUNT)));
            item.setGoalCount(c.getInt(c.getColumnIndex(MemoContract.Notes.COL_GOAL_COUNT)));
            item.setElapsedTime(c.getInt(c.getColumnIndex(MemoContract.Notes.COL_ELAPSED_TIME)));

            items.add(item);
            isEof = c.moveToPrevious();
        }

        c.close();
        db.close();

//        // adapterの準備
//        String[] from = {"body", "current_count", "goal_count", "elapsed_time"};
//        int[] to = {R.id.body, R.id.current_count, R.id.goal_count, R.id.elapsed_time};

        // adapter生成
        NoteListAdapter adapter = new NoteListAdapter(
                this,
                R.layout.notelist,
                items
        );

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, Content.class);
                intent.putExtra("id", parent.getItemIdAtPosition(position));
                startActivity(intent);
            }
        });
    }

}
