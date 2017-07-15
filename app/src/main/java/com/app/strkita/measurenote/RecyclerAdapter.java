package com.app.strkita.measurenote;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import static android.R.attr.id;
import static com.app.strkita.measurenote.MainActivity.EXTRA_ID;

/**
 * 一覧リスト用Adapter
 * Created by strkita on 2017/06/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private String[] dataset;
    private View.OnClickListener mListener;

    public RecyclerAdapter(String[] dataset) {
        this.dataset = dataset;
    }

    public void setOnItemClickListener(View.OnClickListener listener) {
        mListener = listener;
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
