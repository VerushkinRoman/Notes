package com.posse.android1.notes.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.posse.android1.notes.R;
import com.posse.android1.notes.note.Note;
import com.posse.android1.notes.note.NoteSource;

import org.jetbrains.annotations.NotNull;

public class ViewHolderAdapter extends RecyclerView.Adapter<ViewHolder> {
    private final LayoutInflater mInflater;
    private final NoteSource mDataSource;

    private OnClickListener mOnClickListener;

    public ViewHolderAdapter(LayoutInflater inflater, NoteSource dataSource) {
        mInflater = inflater;
        mDataSource = dataSource;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.list_compact_note, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = mDataSource.getItemAt(position);
        holder.fillCard(note);
        holder.itemView.setOnClickListener((v) -> {
            if (mOnClickListener != null) {
                mOnClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSource.getItemsCount();
    }
}
