package com.posse.android1.notes;

import android.content.Context;

public class Note {
    private int mNoteIndex;
    private String mName;
    private String mDescription;
    private String mCreationDate;

    public Note(int noteIndex, Context context) {
        mNoteIndex = noteIndex;
        mName = context.getResources().getStringArray(R.array.notes_list)[noteIndex];
        mDescription = context.getResources().getStringArray(R.array.notes)[noteIndex];
        mCreationDate = context.getResources().getStringArray(R.array.dates)[noteIndex];
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

    public int getNoteIndex() {
        return mNoteIndex;
    }
}
