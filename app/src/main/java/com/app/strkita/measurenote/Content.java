package com.app.strkita.measurenote;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;q
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;
import java.util.Locale;

import static com.app.strkita.measurenote.R.id.bodyText;
import static com.app.strkita.measurenote.R.id.countText;

public class Content extends AppCompatActivity {

    private Long noteId;
    private EditText bodyText;
    private TextView countText;
//    static DateFormat yyyymmddhhmm = new SimpleDateFormat("yyyy/MM/dd HH:mm");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        bodyText = (EditText) findViewById(R.id.bodyText);
        countText = (TextView) findViewById(R.id.countText);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        noteId = intent.getLongExtra(MainActivity.EXTRA_ID, 0L);

        if (noteId == 0) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("New Note");
            }
            countText.setText("0");
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Note");
            }
            Uri uri = ContentUris.withAppendedId(
                    NoteContentProvider.CONTENT_URI,
                    noteId
            );

            String[] projection = {
                    MemoContract.Notes.COL_BODY
            };

            Cursor c = getContentResolver().query(
                    uri,
                    projection,
                    MemoContract.Notes._ID + " = ?",
                    new String[] { Long.toString(noteId) },
                    null
            );

            c.moveToFirst();
            bodyText.setText(c.getString(c.getColumnIndex(MemoContract.Notes.COL_BODY)));
            c.close();
            countText.setText("debug");
        }

        bodyText.setSelection(bodyText.length());
    }

    private void deleteNote() {
        new AlertDialog.Builder(this)
                .setTitle("このノートを削除します")
                .setMessage("本当に削除しますか？")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = ContentUris.withAppendedId(
                                NoteContentProvider.CONTENT_URI,
                                noteId
                        );
                        getContentResolver().delete(
                                uri,
                                MemoContract.Notes._ID + " = ?",
                                new String[] { Long.toString(noteId)}
                        );
                        finish();
                    }
                })
                .show();
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void saveNote() {
        String body = bodyText.getText().toString().trim();
        int eTime = 0000;
        int updated = 100;

        ContentValues values = new ContentValues();
        values.put(MemoContract.Notes.COL_BODY, body);
        values.put(MemoContract.Notes.COL_ELAPSED_TIME, eTime);
        values.put(MemoContract.Notes.COL_CURRENT_COUNT, body.length());
        values.put(MemoContract.Notes.COL_UPDATED, updated);

        if (noteId == 0L) {
            // new Note
            getContentResolver().insert(
                    NoteContentProvider.CONTENT_URI,
                    values
            );
        } else {
            // updated NOTE
            Uri uri = ContentUris.withAppendedId(
                    NoteContentProvider.CONTENT_URI,
                    noteId
            );

            getContentResolver().update(
                    uri,
                    values,
                    MemoContract.Notes._ID + " = ?",
                    new String[] { Long.toString(noteId) }
            );
        }
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        MenuItem deleteItem = menu.findItem(R.id.action_delete);
//        if (noteId == 0L) deleteItem.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteNote();
                break;
            case android.R.id.home:
                saveNote();
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    /**
//     * Long の数字を日付フォーマットに変換します。
//     * @param date Long の数字
//     * @return "yyyy/MM/dd HH:mm" フォーマットの文字列
//     */
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public static String convertLongToYyyymmddhhmm(Long date) {
//        return yyyymmddhhmm.format(new Date(date));
//    }
//    /**
//     * 現在日時をyyyy/MM/dd HH:mm:ss形式で取得する.<br>
//     */
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public static String getNowDate(){
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Date date = new Date(System.currentTimeMillis());
//        return sdf.format(date);
//    }
}

