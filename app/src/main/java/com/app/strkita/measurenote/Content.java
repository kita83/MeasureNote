package com.app.strkita.measurenote;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.R.drawable.ic_media_pause;
import static android.R.drawable.ic_media_play;

public class Content extends AppCompatActivity {

    private long startTime;

    private Handler handler = new Handler();
    private Runnable updateTimer;

    private long noteId;
    private EditText bodyText;
    private TextView countText;
    private TextView goalCountText;
    private TextView timerView;
    private MenuItem menuItem;
    private long elapsedTime = 0L;
    private String initFlag = "0";
    private String goalFlag = "0";
    private String pauseFlag = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        bodyText = (EditText) findViewById(R.id.bodyText);
        countText = (TextView) findViewById(R.id.countText);
        goalCountText = (TextView) findViewById(R.id.goalCountText);
        timerView = (TextView) findViewById(R.id.timerView);
        menuItem = (MenuItem) findViewById(R.id.action_pause);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
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
            timerView.setText(R.string.default_time);
            bodyText.setHint(R.string.hint_start_timer);
            showCountSetDialog();
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
                    // 起動してからの経過時間（ミリ秒）
                    startTime = SystemClock.elapsedRealtime();
                    startTimer();
                    initFlag = "1";
                    pauseFlag = "0";
                    bodyText.setHint("");
                }
                if (goalFlag.equals("0")) {
                    String gText = goalCountText.getText().toString().substring(1, goalCountText.getText().toString().length()-2);
                    if (bodyText.length() == Integer.parseInt(gText)) {
                        goalFlag = "1";
                        showGetGoalDialog();
                    }
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
            case R.id.action_pause:
                if ("0".equals(pauseFlag)) {
                    stopTimer();
                    elapsedTime = elapsedTime + (SystemClock.elapsedRealtime() - startTime);
                    item.setIcon(ic_media_play);
                    initFlag = "0";
                    pauseFlag = "1";
                    Toast.makeText(this, "一時停止", Toast.LENGTH_SHORT).show();
                } else {
                    startTimer();
                    item.setIcon(ic_media_pause);
                    pauseFlag = "0";
                    Toast.makeText(this, "再開", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_delete:
                deleteNote();
                break;
            case android.R.id.home:
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
     * 文字数達成時表示用のダイアログを表示
    */
    public void showGetGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ゴール！")
                .setMessage("目標文字数に到達しました。")
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

     /**
     * 文字数設定用のダイアログを表示
     */
    public void showCountSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        // レイアウトファイルからビューを取得
        final View dialogView = inflater.inflate(R.layout.input_count_dialog, null);

        builder.setView(dialogView)
                .setTitle("目標文字数の設定")
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) dialogView.findViewById(R.id.dialog_edit);
                        goalCountText.setText("/" + editText.getText() + "文字");
                    }
                })
                .setNegativeButton(R.string.Later, new DialogInterface.OnClickListener() {
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
        initFlag = "0";
    }
}

