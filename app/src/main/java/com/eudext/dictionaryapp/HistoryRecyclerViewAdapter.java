package com.eudext.dictionaryapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.HistoryViewHolder> {

    private ArrayList<History> histories;
    private Context context;

    public HistoryRecyclerViewAdapter(Context context, ArrayList<History> histories) {
        this.context = context;
        this.histories = histories;
    }


    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView enWord;

        public HistoryViewHolder(View view) {
            super(view);
            enWord = (TextView)view.findViewById(R.id.word);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    String text = histories.get(position).getEnWord();

                    Intent intent = new Intent(context, WordMeaning.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("word", text);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

        }

    }


    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_item, viewGroup, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryRecyclerViewAdapter.HistoryViewHolder historyViewHolder, int i) {
        historyViewHolder.enWord.setText(histories.get(i).getEnWord());
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }
}
