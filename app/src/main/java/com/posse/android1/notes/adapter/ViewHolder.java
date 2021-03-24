package com.posse.android1.notes.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.posse.android1.notes.R;
import com.posse.android1.notes.note.Note;

class ViewHolder extends RecyclerView.ViewHolder {

    public final MaterialTextView header;
    public final MaterialTextView timestamp;
    public int id;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        header = itemView.findViewById(R.id.list_note_header);
        timestamp = itemView.findViewById(R.id.list_note_date_time);
    }

    public void fillCard(Note note) {
        id = note.mNoteIndex;
        header.setText(note.mName);
        timestamp.setText(note.mCreationDate);
    }
}