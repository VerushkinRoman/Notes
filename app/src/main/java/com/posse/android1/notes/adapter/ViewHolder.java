package com.posse.android1.notes.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.posse.android1.notes.R;
import com.posse.android1.notes.note.Note;
import com.posse.android1.notes.ui.notes.NoteListFragment;

class ViewHolder extends RecyclerView.ViewHolder {

    private final AppCompatTextView mHeader;
    private final AppCompatTextView mDescription;
    private final MaterialTextView mTimestamp;
    private final AppCompatImageView mCheckbox;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        mHeader = itemView.findViewById(R.id.list_note_header);
        mDescription = itemView.findViewById(R.id.list_note_description);
        mTimestamp = itemView.findViewById(R.id.list_note_date_time);
        mCheckbox = itemView.findViewById(R.id.delete_checkbox);
    }

    public void fillCard(NoteListFragment fragment, Note note) {
        mHeader.setText(note.getName());
        mDescription.setText(note.getDescription());
        mTimestamp.setText(note.getCreationDate());
        mCheckbox.setVisibility(note.isDeleteVisible() ? View.VISIBLE : View.INVISIBLE);
        itemView.setOnLongClickListener((v) -> {
            fragment.onViewHolderLongClick(getLayoutPosition());
            return false;
        });
        fragment.registerForContextMenu(itemView);
    }

    public void clear(Fragment fragment) {
        itemView.setOnLongClickListener(null);
        fragment.unregisterForContextMenu(itemView);
    }
}