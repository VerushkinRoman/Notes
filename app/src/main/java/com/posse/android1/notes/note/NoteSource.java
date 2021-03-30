package com.posse.android1.notes.note;

import androidx.annotation.NonNull;

import java.util.List;

public interface NoteSource {

    void addNoteSourceListener(NoteSourceListener listener);

    void removeNoteSourceListener(NoteSourceListener listener);

    List<Note> getNote();

    Note getItemAt(int idx);

    int getItemsCount();

    void add(@NonNull Note data);

    void update(@NonNull Note note);

    void remove(int position);

    interface NoteSourceListener {
        void onItemAdded(int idx);

        void onItemRemoved(int idx);

        void onItemUpdated(int idx);

        void onDataSetChanged();
    }
}