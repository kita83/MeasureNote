package com.app.strkita.measurenote;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;

/**
 * SimpleCursorAdapterを継承したカスタムクラス
 * Created by kitada on 2017/02/25.
 */

public class CustomListAdapter extends SimpleCursorAdapter {
    private LayoutInflater layoutInflater;

    public CustomListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);

        // inflaterを初期化
        this.layoutInflater = (LayoutInflater)context.getSystemService(
        Context.LAYOUT_INFLATER_SERVICE
        );
    }

}
