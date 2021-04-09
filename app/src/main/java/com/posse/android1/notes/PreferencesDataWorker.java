package com.posse.android1.notes;

import android.content.Context;
import android.content.SharedPreferences;

import com.posse.android1.notes.note.Note;

public class PreferencesDataWorker {
    private static final String PREFERENCES_KEY = PreferencesDataWorker.class.getCanonicalName() + "_preferences";
    private static final String PREFERENCES_KEY_NAMES = PreferencesDataWorker.class.getCanonicalName() + "_names";
    private static final String PREFERENCES_KEY_NOTES = PreferencesDataWorker.class.getCanonicalName() + "_notes";
    private static final String PREFERENCES_KEY_DATES = PreferencesDataWorker.class.getCanonicalName() + "_dates";
    private static final String PREFERENCES_KEY_OPENED_EDITOR = PreferencesDataWorker.class.getCanonicalName() + "_openedEditor";
    private static final String PREFERENCES_KEY_LAST_INDEX = PreferencesDataWorker.class.getCanonicalName() + "_lastIndex";
    private static final String PREFERENCES_KEY_GRID_VIEW = PreferencesDataWorker.class.getCanonicalName() + "_gridView";


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

    public boolean isEditorOpened() {
        return mPrefs.getBoolean(PREFERENCES_KEY_OPENED_EDITOR, false);
    }

    public int getLastIndex() {
        return mPrefs.getInt(PREFERENCES_KEY_LAST_INDEX, -1);
    }

    public void setEditorOpened(boolean isOpened, int lastIndex) {
        mEditor = mPrefs.edit();
        mEditor.putBoolean(PREFERENCES_KEY_OPENED_EDITOR, isOpened);
        mEditor.putInt(PREFERENCES_KEY_LAST_INDEX, lastIndex);
        mEditor.apply();
    }

    public boolean isGridView() {
        return mPrefs.getBoolean(PREFERENCES_KEY_GRID_VIEW, false);
    }

    public void setGridView(boolean isGridView) {
        mEditor = mPrefs.edit();
        mEditor.putBoolean(PREFERENCES_KEY_GRID_VIEW, isGridView);
        mEditor.apply();
    }
}
