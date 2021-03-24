package com.posse.android1.notes.note;

import java.util.List;

public interface NoteSource {

    List<Note> getNote();
    Note getItemAt(int idx);
    int getItemsCount();
}
