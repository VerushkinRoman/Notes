package com.posse.android1.notes.note;

public class Note {
    public final int mNoteIndex;
    public final String mName;
    public final String mDescription;
    public final String mCreationDate;

    public Note(int noteIndex, String name, String description, String creationDate) {
        mNoteIndex = noteIndex;
        mName = name;
        mDescription = description;
        mCreationDate = creationDate;
    }
}
