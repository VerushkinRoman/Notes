package com.posse.android1.notes.note;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

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
    @Nullable
    private String mId;
    private long mNoteIndex;
    private String mName;
    private String mDescription;
    private String mCreationDate;
    private boolean mIsDeleteVisible;
    private int mColor;
    private String mAuthor;

    public Note(long noteIndex, String name, String description, String creationDate, int color, String author) {
        mNoteIndex = noteIndex;
        mName = name;
        mDescription = description;
        mCreationDate = creationDate;
        mColor = color;
        mAuthor = author;
    }

    protected Note(Parcel in) {
        mId = in.readString();
        mNoteIndex = in.readLong();
        mName = in.readString();
        mDescription = in.readString();
        mCreationDate = in.readString();
        mIsDeleteVisible = in.readByte() != 0;
        mColor = in.readInt();
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getId() {
        return mId;
    }

    public void setId(@Nullable String id) {
        mId = id;
    }

    public long getNoteIndex() {
        return mNoteIndex;
    }

    public void setNoteIndex(int idx) {
        mNoteIndex = idx;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getCreationDate() {
        return mCreationDate;
    }

    public void setCreationDate(String creationDate) {
        this.mCreationDate = creationDate;
    }

    public void setIsDeleteVisible(boolean isVisible) {
        mIsDeleteVisible = isVisible;
    }

    public boolean isDeleteVisible() {
        return mIsDeleteVisible;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeLong(mNoteIndex);
        dest.writeString(mName);
        dest.writeString(mDescription);
        dest.writeString(mCreationDate);
        dest.writeByte((byte) (mIsDeleteVisible ? 1 : 0));
        dest.writeInt(mColor);
    }
}
