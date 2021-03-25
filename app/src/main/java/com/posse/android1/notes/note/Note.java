package com.posse.android1.notes.note;

public class Note {
    private final int mNoteIndex;
    private final String mName;
    private final String mDescription;
    private final String mCreationDate;

    public Note(int noteIndex, String name, String description, String creationDate) {
        mNoteIndex = noteIndex;
        mName = name;
        mDescription = description;
        mCreationDate = creationDate;
    }

    public int getNoteIndex() {
        return mNoteIndex;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getCreationDate() {
        return mCreationDate;
    }
}
