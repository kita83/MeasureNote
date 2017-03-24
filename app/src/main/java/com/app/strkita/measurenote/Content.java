package com.app.strkita.measurenote;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
    private TextView goalCountText;
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
        goalCountText = (TextView) findViewById(R.id.goalCountText);
        timerView = (TextView) findViewById(R.id.timerView);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        noteId = intent.getLongExtra(MainActivity.EXTRA_ID, 0L);

        if (noteId == 0) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("New Note");
            }
            countText.setText("0");
            timerView.setText("00:00:00");
            bodyText.setHint("入力開始でタイマー始動");
            showDialog();
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
                    MemoContract.Notes.COL_ELAPSED_TIME,
                    MemoContract.Notes.COL_GOAL_COUNT
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
                goalCountText.setText("/" + c.getString(c.getColumnIndex(MemoContract.Notes.COL_GOAL_COUNT)) + "文字");
                elapsedTime = c.getInt(c.getColumnIndex(MemoContract.Notes.COL_ELAPSED_TIME));
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                timerView.setText(sdf.format(elapsedTime));
                c.close();
            }
        }

        bodyText.setSelection(bodyText.length());
        countText.setText(String.valueOf(bodyText.length()));

        bodyText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (initFlag.equals("0")) {
                    startTimer();
                    initFlag = "1";
                    bodyText.setHint("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                countText.setText(String.valueOf(bodyText.length()));
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
        String str = goalCountText.getText().toString().substring(1, goalCountText.length()-2);
        int gCount = Integer.valueOf(str);
        // タイマーが始動していなければ経過時間変更しない
        long saveTime = elapsedTime;
        if (initFlag.equals("1")) {
            saveTime = SystemClock.elapsedRealtime() - startTime + elapsedTime;
        }
        ContentValues values = new ContentValues();
        values.put(MemoContract.Notes.COL_BODY, body);
        values.put(MemoContract.Notes.COL_ELAPSED_TIME, saveTime);
        values.put(MemoContract.Notes.COL_CURRENT_COUNT, body.length());
        values.put(MemoContract.Notes.COL_GOAL_COUNT, gCount);
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
                // テキストが空の場合、保存しない
                if (bodyText.length() != 0) {
                    saveNote();
                }
                stopTimer();
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 現在日時をyyyy/MM/dd HH:mm:ss形式で取得
     */
    public static String getNowDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        Date date = new Date(System.currentTimeMillis());
        return sdf.format(date);
    }
    /**
     * Long の数字を日付フォーマットに変換
     * @param date Long の数字
     * @return "yyyy/MM/dd HH:mm" フォーマットの文字列
     */
    public static String convertLongToYyyymmddhhmm(Long date) {
        return yyyymmddhhmm.format(new Date(date));
    }

    /**
     * 文字数設定用のダイアログを表示
     */
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        // レイアウトファイルからビューを取得
        final View dialogView = inflater.inflate(R.layout.input_count_dialog, null);

        builder.setView(dialogView)
                .setTitle("目標文字数の設定")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) dialogView.findViewById(R.id.dialog_edit);
                        goalCountText.setText("/" + editText.getText() + "文字");
                    }
                })
                .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(dialogView.findFocus(), 0);
            }
        });
        // ダイアログ外タップで消えないように設定
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // テキストが空の場合、保存しない
        if (bodyText.length() != 0) {
            saveNote();
        }
        stopTimer();
    }
}

