package com.posse.android1.notes.note;

import androidx.annotation.NonNull;

public interface NoteSource {

    Note getItemAt(int idx);

    int getItemsCount();

    void add(@NonNull Note data);

    void remove(int position);
}
