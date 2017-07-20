package com.app.strkita.measurenote;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 一覧リスト用Adapter
 * Created by strkita on 2017/06/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private String[] dataset;
    private View.OnClickListener mListener;
    private Cursor dataCursor;

    public RecyclerAdapter(String[] dataset) {
        this.dataset = dataset;
    }

    public void setOnItemClickListener(View.OnClickListener listener) {
        mListener = listener;
    }

    public Cursor swapCursor(Cursor c) {
        if (dataCursor == c) {
            return null;
        }
        Cursor oldCursor = dataCursor;
        this.dataCursor = c;
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notelist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        holder.bodyText.setText("hoge");
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(v);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataset.length;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView bodyText;
        TextView currentCount;
        TextView goalCount;
        TextView elapsedTime;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            this.bodyText = (TextView) itemView.findViewById(R.id.bodyText);
            this.currentCount = (TextView) itemView.findViewById(R.id.currentCount);
            this.goalCount = (TextView) itemView.findViewById(R.id.goalCount);
            this.elapsedTime = (TextView) itemView.findViewById(R.id.elapsedTime);
        }
    }
}
