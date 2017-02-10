package com.app.strkita.measurenote;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * メモ管理用
 * Created by kitada on 2017/02/09.
 */

public class MemoContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.app.strkita.measurenote.MemoContentProvider";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + MemoContract.Notes.TABLE_NAME);

    // UriMatcher
    // ノートリストのリクエスト
    private static final int NOTES = 1;
    // 単一ノートのリクエスト
    private static final int NOTE_ITEM = 2;
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MemoContract.Notes.TABLE_NAME, NOTES);
        uriMatcher.addURI(AUTHORITY, MemoContract.Notes.TABLE_NAME+"/#", NOTE_ITEM);
    }

    private MemoOpenHelper memoOpenHelper;

    @Override
    public boolean onCreate() {
        memoOpenHelper = new MemoOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    // 検索用メソッド
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder
    ) {
        switch (uriMatcher.match(uri)) {
            case NOTES:
            case NOTE_ITEM:
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }
        SQLiteDatabase db = memoOpenHelper.getReadableDatabase();
        Cursor c = db.query(
                MemoContract.Notes.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != NOTE_ITEM) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }
        SQLiteDatabase db = memoOpenHelper.getWritableDatabase();
        db.insert(MemoContract.Notes.TABLE_NAME, null, values);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) != NOTE_ITEM) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }
        SQLiteDatabase db = memoOpenHelper.getWritableDatabase();
        int updatedCount = db.update(
                MemoContract.Notes.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        getContext().getContentResolver().notifyChange(uri, null);
        return updatedCount;
    }
}
