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
        body.setText(intent.getStringExtra("body"));

    }
}

