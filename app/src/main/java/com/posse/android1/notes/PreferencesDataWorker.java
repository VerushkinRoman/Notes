package com.posse.android1.notes;

import android.content.Context;
import android.content.SharedPreferences;

import com.posse.android1.notes.note.Note;
import com.posse.android1.notes.note.NoteSourceImpl;

public class PreferencesDataWorker {
    private static final String PREFERENCES_KEY = NoteSourceImpl.class.getCanonicalName() + "preferences";
    private static final String PREFERENCES_KEY_NAMES = NoteSourceImpl.class.getCanonicalName() + "names";
    private static final String PREFERENCES_KEY_NOTES = NoteSourceImpl.class.getCanonicalName() + "notes";
    private static final String PREFERENCES_KEY_DATES = NoteSourceImpl.class.getCanonicalName() + "dates";

    private final SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;

    public PreferencesDataWorker(Context context) {
        mPrefs = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public void writeNote(Note note) {
        mEditor = mPrefs.edit();
        mEditor.putString(PREFERENCES_KEY_NAMES + "_" + note.getNoteIndex(), note.getName());
        mEditor.putString(PREFERENCES_KEY_NOTES + "_" + note.getNoteIndex(), note.getDescription());
        mEditor.putString(PREFERENCES_KEY_DATES + "_" + note.getNoteIndex(), note.getCreationDate());
        mEditor.apply();
    }

    public Note readNote(int index) {
        return new Note(index,
                mPrefs.getString(PREFERENCES_KEY_NAMES + "_" + index, null),
                mPrefs.getString(PREFERENCES_KEY_NOTES + "_" + index, null),
                mPrefs.getString(PREFERENCES_KEY_DATES + "_" + index, null));
    }

    public void deleteNote(int index, int fullSize) {
        mEditor = mPrefs.edit();
        for (int i = index; i < fullSize; i++) {
            mEditor.putString(PREFERENCES_KEY_NAMES + "_" + i, mPrefs.getString(PREFERENCES_KEY_NAMES + "_" + (i + 1), null));
            mEditor.putString(PREFERENCES_KEY_NOTES + "_" + i, mPrefs.getString(PREFERENCES_KEY_NOTES + "_" + (i + 1), null));
            mEditor.putString(PREFERENCES_KEY_DATES + "_" + i, mPrefs.getString(PREFERENCES_KEY_DATES + "_" + (i + 1), null));
        }
        mEditor.remove(PREFERENCES_KEY_NAMES + "_" + fullSize);
        mEditor.remove(PREFERENCES_KEY_NOTES + "_" + fullSize);
        mEditor.remove(PREFERENCES_KEY_DATES + "_" + fullSize);
        mEditor.apply();
    }

    public int getNotesQuantity() {
        return mPrefs.getInt(PREFERENCES_KEY + "_size", 0);
    }

    public void writeNotesQuantity(int size) {
        mEditor = mPrefs.edit();
        mEditor.putInt(PREFERENCES_KEY + "_size", size);
        mEditor.apply();
    }
}
