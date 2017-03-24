package com.app.strkita.measurenote;

import android.content.Intent;
import android.database.Cursor;
import java.text.SimpleDateFormat;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SimpleCursorAdapter.ViewBinder {

    private SimpleCursorAdapter adapter;
    public final static String EXTRA_ID = "com.app.strkita.measurenote.ID";
    private static final int BODY = 1;
    private static final int ELAPSED_TIME = 2;
    private static final int CURRENT_COUNT = 3;
    private static final int GOAL_COUNT = 4;
    private static final int UPDATED = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(this, Content.class);
//                startActivity(intent);
            }
        });

        String[] from = {
                MemoContract.Notes.COL_BODY,
                MemoContract.Notes.COL_ELAPSED_TIME,
                MemoContract.Notes.COL_CURRENT_COUNT,
                MemoContract.Notes.COL_GOAL_COUNT,
//                MemoContract.Notes.COL_CREATED,
        };

        int[] to = {
                R.id.bodyText,
                R.id.elapsed_time,
                R.id.current_count,
                R.id.goal_count,
//                R.id.updated,
        };

        adapter = new SimpleCursorAdapter(
                this,
                R.layout.notelist,
                null,
                from,
                to,
                0
        );

        adapter.setViewBinder(this);
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
    }

    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        switch (columnIndex) {
            case BODY:
                TextView bd = (TextView) view;
                bd.setText(cursor.getString(columnIndex));
                return true;
            case ELAPSED_TIME:
                TextView et = (TextView) view;
                // -9時間したミリ秒を取得
                long t = cursor.getLong(columnIndex);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                et.setText(sdf.format(t));
                return true;
            case CURRENT_COUNT:
                TextView cc = (TextView) view;
                cc.setText(cursor.getInt(columnIndex) + " / ");
                return true;
            case GOAL_COUNT:
                TextView gc = (TextView) view;
                gc.setText(cursor.getInt(columnIndex) + "文字");
                return true;
//            case UPDATED:
//                TextView ud = (TextView) view;
//                ud.setText(cursor.getString(columnIndex));
//                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent intent = new Intent(this, Content.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                MemoContract.Notes._ID,
                MemoContract.Notes.COL_BODY,
                MemoContract.Notes.COL_ELAPSED_TIME,
                MemoContract.Notes.COL_CURRENT_COUNT,
                MemoContract.Notes.COL_GOAL_COUNT,
                MemoContract.Notes.COL_CREATED,
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
