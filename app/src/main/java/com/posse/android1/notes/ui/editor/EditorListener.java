package com.posse.android1.notes.ui.editor;

import android.os.Parcelable;

import com.posse.android1.notes.note.Note;

public interface EditorListener extends Parcelable {
    void noteSaved(Note note);
}
