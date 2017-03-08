package com.app.strkita.measurenote;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Content extends AppCompatActivity {

    private long startTime;

    private Handler handler = new Handler();
    private Runnable updateTimer;

    private long noteId;
    private EditText bodyText;
    private TextView countText;
    private TextView timerView;
    private long elapsedTime = 0L;
    private String initFlag = "0";
    static DateFormat yyyymmddhhmm = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        bodyText = (EditText) findViewById(R.id.bodyText);
        countText = (TextView) findViewById(R.id.countText);
        timerView = (TextView) findViewById(R.id.timerView);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        CountDialogFragment newFragment = new CountDialogFragment();
        newFragment.show(getSupportFragmentManager(), "count");

        Intent intent = getIntent();
        noteId = intent.getLongExtra(MainActivity.EXTRA_ID, 0L);

        if (noteId == 0) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("New Note");
            }
            countText.setText("0");
            timerView.setText("00:00:00");
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Note");
            }
            Uri uri = ContentUris.withAppendedId(
                    NoteContentProvider.CONTENT_URI,
                    noteId
            );

            String[] projection = {
                    MemoContract.Notes.COL_BODY,
                    MemoContract.Notes.COL_ELAPSED_TIME
            };

            Cursor c = getContentResolver().query(
                    uri,
                    projection,
                    MemoContract.Notes._ID + " = ?",
                    new String[] { Long.toString(noteId) },
                    null
            );

            // 経過時間を取得
            if (c != null) {
                c.moveToFirst();
                bodyText.setText(c.getString(c.getColumnIndex(MemoContract.Notes.COL_BODY)));
                elapsedTime = c.getInt(c.getColumnIndex(MemoContract.Notes.COL_ELAPSED_TIME));
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                timerView.setText(sdf.format(elapsedTime));
                c.close();
            }
        }

        bodyText.setSelection(bodyText.length());
        countText.setText(String.valueOf(bodyText.length()) + "文字");

        bodyText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                bodyText.setHint();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (initFlag.equals("0")) {
                    startTimer();
                    initFlag = "1";
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                countText.setText(String.valueOf(bodyText.length()) + "文字");
            }
        });
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
        long saveTime = SystemClock.elapsedRealtime() - startTime + elapsedTime;
//        long updated = System.currentTimeMillis();

        ContentValues values = new ContentValues();
        values.put(MemoContract.Notes.COL_BODY, body);
        values.put(MemoContract.Notes.COL_ELAPSED_TIME, saveTime);
        values.put(MemoContract.Notes.COL_CURRENT_COUNT, body.length());
        values.put(MemoContract.Notes.COL_UPDATED, getNowDate());

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

    public void startTimer() {
        // 起動してからの経過時間（ミリ秒）
        startTime = SystemClock.elapsedRealtime();

        // 一定時間ごとに現在の経過時間を表示
        updateTimer = new Runnable() {
            @Override
            public void run() {
                long t = SystemClock.elapsedRealtime() - startTime + elapsedTime;
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                timerView.setText(sdf.format(t));
                handler.removeCallbacks(updateTimer);
                handler.postDelayed(updateTimer, 10);
            }
        };
        handler.postDelayed(updateTimer, 10);
    }

    public void stopTimer() {
        handler.removeCallbacks(updateTimer);
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
                stopTimer();
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 現在日時をyyyy/MM/dd HH:mm:ss形式で取得する.<br>
     */
    public static String getNowDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }
    /**
     * Long の数字を日付フォーマットに変換します。
     * @param date Long の数字
     * @return "yyyy/MM/dd HH:mm" フォーマットの文字列
     */
    public static String convertLongToYyyymmddhhmm(Long date) {
        return yyyymmddhhmm.format(new Date(date));
    }
}

