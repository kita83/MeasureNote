package com.app.strkita.measurenote;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;

/**
 * SimpleCursorAdapterを継承したカスタムクラス
 * Created by kitada on 2017/02/25.
 */

public class CustomListAdapter extends SimpleCursorAdapter {
    private LayoutInflater layoutInflater;

    static class ViewHolder {
        public TextView bodyText;
        public TextView current_count;
        public TextView goal_count;
        public TextView elapsed_time;

        ViewHolder(View v) {
            bodyText = (TextView) v.findViewById(R.id.bodyText);
            current_count= (TextView) v.findViewById(R.id.current_count);
            goal_count = (TextView) v.findViewById(R.id.goal_count);
            elapsed_time = (TextView) v.findViewById(R.id.elapsed_time);
        }
    }

    public CustomListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        // inflaterを初期化
//        this.layoutInflater = (LayoutInflater)context.getSystemService(
//                Context.LAYOUT_INFLATER_SERVICE
//        );

        View v = layoutInflater.inflate(R.layout.notelist, null);
        ViewHolder holder = new ViewHolder(v);

        v.setTag(holder);

        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.bodyText.setText(cursor.getString(cursor.getColumnIndex(MemoContract.Notes.COL_BODY)));
        holder.current_count.setText(cursor.getString(cursor.getColumnIndex(MemoContract.Notes.COL_CURRENT_COUNT)) + "文字");
        holder.goal_count.setText(cursor.getString(cursor.getColumnIndex(MemoContract.Notes.COL_GOAL_COUNT)) + " / ");

        long t = cursor.getLong(cursor.getColumnIndex(MemoContract.Notes.COL_ELAPSED_TIME));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        holder.elapsed_time.setText(sdf.format(t));

    }
}
