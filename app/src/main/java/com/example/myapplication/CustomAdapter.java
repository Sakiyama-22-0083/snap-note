package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<MemoData> {
    private final List<MemoData> cards;

    public CustomAdapter(Context context, int layoutResourceId, List<MemoData> memoData) {
        super(context, layoutResourceId, memoData);
        this.cards = memoData;
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Nullable
    @Override
    public MemoData getItem(int position) {
        return cards.get(position);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_view, null);
            viewHolder = new ViewHolder();

            viewHolder.titleTextView = convertView.findViewById(R.id.title_text_view);
            viewHolder.contentTextView = convertView.findViewById(R.id.content_text_view);
            convertView.setTag(viewHolder);

        }

        MemoData memoData = cards.get(position);
        viewHolder.titleTextView.setText(memoData.getTitle());
        viewHolder.contentTextView.setText(memoData.getContent());

        return convertView;
    }

    public MemoData getMemoKey(String key) {
        for (MemoData memoData : cards) {
            if (memoData.getFirebaseKey().equals(key)) return memoData;
        }

        return null;
    }

    static class ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
    }

}
