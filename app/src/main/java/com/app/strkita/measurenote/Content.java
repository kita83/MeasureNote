package com.app.strkita.measurenote;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class Content extends AppCompatActivity {
    private Long noteId;
    private EditText body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        body = (EditText) findViewById(R.id.body);

        // DB
        MemoOpenHelper memoOpenHelper = new MemoOpenHelper(this);
        SQLiteDatabase db = memoOpenHelper.getWritableDatabase();

        Intent intent = getIntent();
        noteId = intent.getLongExtra("id", 0L);

        String[] projection = { MemoContract.Notes.COL_BODY };
        String selection = "_id = ?";
        String[] selectionArgs = { noteId.toString() };

        if (noteId != null) {

            Cursor c = db.query(
                    MemoContract.Notes.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            c.moveToFirst();

            String debug = c.getString(c.getColumnIndex(MemoContract.Notes.COL_BODY));

            body.setText(c.getString(c.getColumnIndex(MemoContract.Notes.COL_BODY)));

            c.close();
        }

        db.close();
    }
}
