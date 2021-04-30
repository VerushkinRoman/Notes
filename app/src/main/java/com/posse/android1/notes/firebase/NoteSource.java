package com.posse.android1.notes.firebase;

import androidx.annotation.NonNull;

import com.posse.android1.notes.note.Note;

import java.util.List;

public interface NoteSource {

    void addNoteSourceListener(NoteSourceListener listener);

    void removeNoteSourceListener(NoteSourceListener listener);

    Note getItemAt(int idx);

    int getItemsCount();

    String getAuthor();

    void add(@NonNull Note data);

    void update(@NonNull Note note);

    void remove(@NonNull Note note);

    void setColor(@NonNull Note note);

    interface NoteSourceListener {
        void onFetchComplete();
    }
}
