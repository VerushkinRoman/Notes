package com.posse.android1.notes.ui.notes;

import com.posse.android1.notes.note.NoteSource;

public interface NoteListFragmentListener {
    void onAddButtonPressed();

    void onListItemClicked(int idx);

    NoteSource onSourceRequest();

    void onLastSelectedPositionChanged(int lastSelectedPosition);

    void onContextEditMenuSelected();

    int onContextDeleteMenuSelected();
}
