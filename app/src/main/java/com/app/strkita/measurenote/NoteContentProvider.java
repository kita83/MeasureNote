package com.app.strkita.measurenote;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

public class NoteContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.app.strkita.measurenote.NoteContentProvider";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + MemoContract.Notes.TABLE_NAME);

    // UriMatcher
    private static final int NOTES = 1;
    private static final int NOTE_ITEM = 2;
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MemoContract.Notes.TABLE_NAME, NOTES);
        uriMatcher.addURI(AUTHORITY, MemoContract.Notes.TABLE_NAME+"/#", NOTE_ITEM);
    }

    MemoOpenHelper memoOpenhelper;

    public NoteContentProvider() {}

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onCreate() {
        memoOpenhelper = new MemoOpenHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
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
        SQLiteDatabase db = memoOpenhelper.getReadableDatabase();
        Cursor c = db.query(
                MemoContract.Notes.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        // 指定したURIへの通知イベントを受信する設定
        Context context = getContext();
        if (context != null) {
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != NOTES) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        SQLiteDatabase db = memoOpenhelper.getWritableDatabase();
        long newId = db.insert(
                MemoContract.Notes.TABLE_NAME,
                null,
                values
        );

        Uri newUri = ContentUris.withAppendedId(
                NoteContentProvider.CONTENT_URI,
                newId
        );

        Context context = getContext();
        if (context != null) {
            getContext().getContentResolver().notifyChange(newUri, null);
        }
        return newUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        if (uriMatcher.match(uri) != NOTE_ITEM) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        SQLiteDatabase db = memoOpenhelper.getWritableDatabase();
        int updatedCount = db.update(
                MemoContract.Notes.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        Context context = getContext();
        if (context != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updatedCount;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) != NOTE_ITEM) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        SQLiteDatabase db = memoOpenhelper.getWritableDatabase();
        int deletedCount = db.delete(
                MemoContract.Notes.TABLE_NAME,
                selection,
                selectionArgs
        );

        Context context = getContext();
        if (context != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedCount;
    }
}
