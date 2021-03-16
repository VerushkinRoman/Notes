package com.posse.android1.notes;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable {
    private int mNoteIndex;
    private String mName;
    private String mDescription;
    private String mCreationDate;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getNoteIndex());
        dest.writeString(getName());
        dest.writeString(getDescription());
        dest.writeString(getCreationDate());
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
