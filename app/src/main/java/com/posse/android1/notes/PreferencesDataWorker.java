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

    public PreferencesDataWorker(Context context) {
        mPrefs = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public void writeNote(Note note) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(PREFERENCES_KEY_NAMES + "_" + note.getNoteIndex(), note.getName());
        editor.putString(PREFERENCES_KEY_NOTES + "_" + note.getNoteIndex(), note.getDescription());
        editor.putString(PREFERENCES_KEY_DATES + "_" + note.getNoteIndex(), note.getCreationDate());
        editor.apply();
    }

    public Note readNote(int index) {
        return new Note(index,
                mPrefs.getString(PREFERENCES_KEY_NAMES + "_" + index, null),
                mPrefs.getString(PREFERENCES_KEY_NOTES + "_" + index, null),
                mPrefs.getString(PREFERENCES_KEY_DATES + "_" + index, null));
    }

    public void deleteNote(int index){
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.remove(PREFERENCES_KEY_NAMES + "_" + index);
        editor.remove(PREFERENCES_KEY_NOTES + "_" + index);
        editor.remove(PREFERENCES_KEY_DATES + "_" + index);
        editor.apply();
    }

    public int getNotesQuantity() {
        return mPrefs.getInt(PREFERENCES_KEY + "_size", 0);
    }

    public void writeNotesQuantity(int size) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(PREFERENCES_KEY + "_size", size);
        editor.apply();
    }
}
