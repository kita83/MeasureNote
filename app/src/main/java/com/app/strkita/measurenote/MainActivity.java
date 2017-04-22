package com.app.strkita.measurenote;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.text.SimpleDateFormat;
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

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Content.class);
                startActivity(intent);
            }
        });

        String[] from = {
                MemoContract.Notes.COL_BODY,
                MemoContract.Notes.COL_ELAPSED_TIME,
                MemoContract.Notes.COL_CURRENT_COUNT,
                MemoContract.Notes.COL_GOAL_COUNT,
        };

        int[] to = {
                R.id.bodyText,
                R.id.elapsed_time,
                R.id.current_count,
                R.id.goal_count,
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
        final ListView listView = (ListView) findViewById(R.id.listView);
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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(
                    AdapterView<?> parent,
                    View view,
                    int position,
                    long id) {
                showDeleteDialog(id);
                return true;
            }
        });
        getSupportLoaderManager().initLoader(0, null, this);

    }

    /**
     * 削除用ダイアログ
     */
    public void showDeleteDialog(long id) {
        final long _id = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("このノートを削除しますか？")
                .setNegativeButton("Cancel", null)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = ContentUris.withAppendedId(
                                NoteContentProvider.CONTENT_URI,
                                _id
                        );
                        getContentResolver().delete(
                                uri,
                                MemoContract.Notes._ID + " = ?",
                                new String[] { Long.toString(_id) }
                        );
                    }
                })
                .show();
    }

    public boolean setViewValue(View view, Cursor c, int columnIndex) {
        switch (columnIndex) {
            case BODY:
                TextView bd = (TextView) view;
                bd.setText(c.getString(columnIndex));
                return true;
            case ELAPSED_TIME:
                TextView et = (TextView) view;
                long t = c.getLong(columnIndex);
                SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                et.setText(sdf.format(t));
                return true;
            case CURRENT_COUNT:
                TextView cc = (TextView) view;
                cc.setText(c.getInt(columnIndex) + " / ");
                return true;
            case GOAL_COUNT:
                TextView gc = (TextView) view;
                gc.setText(c.getInt(columnIndex) + "文字");
                return true;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
