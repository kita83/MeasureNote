package com.app.strkita.measurenote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * カスタマイズしたListView用のアダプタ
 * Created by kitada on 2017/02/13.
 */

public class NoteListAdapter extends ArrayAdapter<ListItems> {
    private LayoutInflater layoutInflater;

    public NoteListAdapter(Context context, int resource, ArrayList<ListItems> items) {
        super(context, resource, items);

        // inflaterを初期化
        this.layoutInflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.notelist, parent, false);
        }

        ListItems item = getItem(position);
//
//        StringBuilder gCount = new StringBuilder();
//
//        // bodyテキストが無い場合、0文字を表示する
//        if (!item.getBody().isEmpty()) {
//            gCount.append(item.getBody().length());
//        } else {
//            gCount.append("0");
//        }
//        gCount.append("/");
//        gCount.append(item.getGoalCount());

        ((TextView) convertView.findViewById(R.id.bodyText)).setText(item.getBody());
        ((TextView) convertView.findViewById(R.id.current_count)).setText(Integer.toString(item.getCurrentCount()) + "/");
        ((TextView) convertView.findViewById(R.id.goal_count)).setText(Integer.toString(item.getGoalCount()));
        ((TextView) convertView.findViewById(R.id.elapsed_time)).setText(Integer.toString(item.getElapsedTime()));

        return convertView;
    }
}
