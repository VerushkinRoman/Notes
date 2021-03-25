package com.posse.android1.notes.note;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable {
    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

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

    protected Note(Parcel in) {
        mNoteIndex = in.readInt();
        mName = in.readString();
        mDescription = in.readString();
        mCreationDate = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mNoteIndex);
        dest.writeString(mName);
        dest.writeString(mDescription);
        dest.writeString(mCreationDate);
    }
}
