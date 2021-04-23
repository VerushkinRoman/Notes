package com.posse.android1.notes.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.posse.android1.notes.R;
import com.posse.android1.notes.note.Note;
import com.posse.android1.notes.note.NoteSource;
import com.posse.android1.notes.ui.notes.NoteListFragment;

import org.jetbrains.annotations.NotNull;

public class ViewHolderAdapter extends RecyclerView.Adapter<ViewHolder> {
    private final NoteListFragment mFragment;
    private final LayoutInflater mInflater;
    private final NoteSource mDataSource;
    private final float mHeaderTextSize;
    private final float mNoteTextSize;

    private OnClickListener mOnClickListener;

    public ViewHolderAdapter(NoteListFragment fragment, NoteSource dataSource, float headerTextSize, float noteTextSize) {
        mFragment = fragment;
        mInflater = fragment.getLayoutInflater();
        mDataSource = dataSource;
        mHeaderTextSize = headerTextSize;
        mNoteTextSize = noteTextSize;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.list_compact_note, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = mDataSource.getItemAt(position);
        holder.fillCard(mFragment, note, mHeaderTextSize, mNoteTextSize);
        holder.itemView.setOnClickListener((v) -> {
            if (mOnClickListener != null) {
                mOnClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.clear(mFragment);
    }

    @Override
    public int getItemCount() {
        return mDataSource.getItemsCount();
    }

    public interface OnClickListener {
        void onItemClick(View v, int position);
    }
}
