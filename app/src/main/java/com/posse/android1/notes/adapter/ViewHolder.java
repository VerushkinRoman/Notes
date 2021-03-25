package com.posse.android1.notes.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.posse.android1.notes.R;
import com.posse.android1.notes.note.Note;

class ViewHolder extends RecyclerView.ViewHolder {

    private final MaterialTextView header;
    private final MaterialTextView timestamp;
    private int id;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        header = itemView.findViewById(R.id.list_note_header);
        timestamp = itemView.findViewById(R.id.list_note_date_time);
    }

    public void fillCard(Note note) {
        id = note.getNoteIndex();
        header.setText(note.getName());
        timestamp.setText(note.getCreationDate());
    }
}