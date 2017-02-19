package com.app.strkita.measurenote;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapter;
    public final static String EXTRA_ID = "com.app.strkita.measurenote.ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] from = {
                MemoContract.Notes.COL_BODY,
                MemoContract.Notes.COL_GOAL_COUNT,
                MemoContract.Notes.COL_ELAPSED_TIME,
        };

        int[] to = {
                R.id.bodyText,
                R.id.goal_count,
                R.id.elapsed_time,
        };

        adapter = new SimpleCursorAdapter(
                this,
                R.layout.notelist,
                null,
                from,
                to,
                0
        );

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(
                    AdapterView<?> parent,
                    View view,
                    int position,
                    long id
            ) {
                Intent intent = new Intent(MainActivity.this, Content.class);
                intent.putExtra(EXTRA_ID, id);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(0, null, this);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(MainActivity.this, Content.class);
//                String text = (String) parent.getItemAtPosition(position);
//                intent.putExtra("body", text);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                MemoContract.Notes._ID,
                MemoContract.Notes.COL_BODY,
                MemoContract.Notes.COL_GOAL_COUNT,
                MemoContract.Notes.COL_ELAPSED_TIME,
                MemoContract.Notes.COL_UPDATED
        };

        return new CursorLoader(
                this,
                NoteContentProvider.CONTENT_URI,
                projection,
                null,
                null,
                MemoContract.Notes.COL_UPDATED + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
