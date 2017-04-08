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
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.R.drawable.ic_media_pause;
import static android.R.drawable.ic_media_play;

public class Content extends AppCompatActivity {

    private long noteId;
    private EditText bodyText;
    private TextView countText;
    private TextView goalCountText;
    private Chronometer timerView;
    private long elapsedTime = 0L;
    private long pausedTime = 0L;
    private long awayTime = 0L;
    private long saveTime = 0L;
    private String initFlag = "1";
    private String goalFlag = "0";
    private String pauseFlag = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        bodyText = (EditText) findViewById(R.id.bodyText);
        countText = (TextView) findViewById(R.id.countText);
        goalCountText = (TextView) findViewById(R.id.goalCountText);
        timerView = (Chronometer) findViewById(R.id.timerView);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        noteId = intent.getLongExtra(MainActivity.EXTRA_ID, 0L);

        // 新規作成
        if (noteId == 0) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("New Note");
            }
            countText.setText("0");
            bodyText.setHint(R.string.hint_start_timer);
            // 文字数設定ダイアログ表示
            showCountSetDialog();
        }
        // 編集
        else {
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

            // 初期値のセット
            if (c != null) {
                c.moveToFirst();
                // 本文
                bodyText.setText(c.getString(c.getColumnIndex(MemoContract.Notes.COL_BODY)));
                // 目標文字数
                goalCountText.setText("/" + c.getString(c.getColumnIndex(MemoContract.Notes.COL_GOAL_COUNT)) + "文字");
                // 経過時間
                elapsedTime = c.getInt(c.getColumnIndex(MemoContract.Notes.COL_ELAPSED_TIME));
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("mm:ss", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                timerView.setText(sdf.format(elapsedTime));
                c.close();
            }
        }

        // 初期カーソル位置を文末に移動
        bodyText.setSelection(bodyText.length());
        // 現在の文字数をセット
        countText.setText(String.valueOf(bodyText.length()));

        bodyText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 入力開始で計測開始
                if ("1".equals(initFlag)) {
                    timerView.setBase(SystemClock.elapsedRealtime() - elapsedTime);
                    timerView.start();
                    initFlag = "0";
                    bodyText.setHint("");
                }

                // 計測再開
                if ("1".equals(pauseFlag)) {
                    awayTime = SystemClock.elapsedRealtime() - pausedTime;
                    timerView.setBase(timerView.getBase() + awayTime);
                    timerView.start();
                    pauseFlag = "0";
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 文字数反映
                countText.setText(String.valueOf(bodyText.length()));

                // 目標文字数に達した時点でダイアログ表示(初回のみ)
                if ("0".equals(goalFlag)) {
                    String gText = goalCountText.getText().toString().substring(1, goalCountText.getText().toString().length()-2);
                    if (bodyText.length() == Integer.parseInt(gText)) {
                        goalFlag = "1";
                        showGetGoalDialog();
                    }
                }
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

    /**
     * 編集データを保存
     */
    @TargetApi(Build.VERSION_CODES.N)
    private void saveNote() {
        String body = bodyText.getText().toString().trim();
        String str = goalCountText.getText().toString().substring(1, goalCountText.length()-2);
        int gCount = Integer.valueOf(str);
        saveTime = Long.parseLong(timerView.getText().toString());
//        if ("1".equals(initFlag)) {
//            saveTime = elapsedTime;
//        } else {
//            awayTime = SystemClock.elapsedRealtime() - pausedTime;
//            saveTime = SystemClock.elapsedRealtime() - timerView.getBase() - awayTime;
//        }
        ContentValues values = new ContentValues();
        values.put(MemoContract.Notes.COL_BODY, body);
        values.put(MemoContract.Notes.COL_ELAPSED_TIME, saveTime);
        values.put(MemoContract.Notes.COL_CURRENT_COUNT, body.length());
        values.put(MemoContract.Notes.COL_GOAL_COUNT, gCount);
        values.put(MemoContract.Notes.COL_UPDATED, getNowDate());

        if (noteId == 0L) {
            // 新規追加
            getContentResolver().insert(
                    NoteContentProvider.CONTENT_URI,
                    values
            );
        } else {
            // 更新
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
            // 一時停止ボタン
            case R.id.action_pause:
                if ("0".equals(pauseFlag)) {
                    timerView.stop();
                    pausedTime = SystemClock.elapsedRealtime();
                    item.setIcon(ic_media_play);
                    pauseFlag = "1";
                    Toast.makeText(this, "一時停止", Toast.LENGTH_SHORT).show();
                } else {
                    awayTime = SystemClock.elapsedRealtime() - pausedTime;
                    // 計測起点を再セット
                    timerView.setBase(timerView.getBase() + awayTime);
                    timerView.start();
                    item.setIcon(ic_media_pause);
                    pauseFlag = "0";
                    Toast.makeText(this, "再開", Toast.LENGTH_SHORT).show();
                }
                break;
            // 削除ボタン
            case R.id.action_delete:
                deleteNote();
                break;
            // 戻るボタン
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 現在日時を取得
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
        timerView.stop();
        initFlag = "1";
    }
}

