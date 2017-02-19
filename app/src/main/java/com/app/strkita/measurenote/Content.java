package com.app.strkita.measurenote;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class Content extends AppCompatActivity {

    private Long noteId;
    private EditText bodyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        bodyText = (EditText) findViewById(R.id.bodyText);

        Intent intent = getIntent();
        noteId = intent.getLongExtra(MainActivity.EXTRA_ID, 0L);

        if (noteId == 0) {

        } else {
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
        }

    }
}

