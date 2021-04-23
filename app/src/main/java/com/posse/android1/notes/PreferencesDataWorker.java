package com.posse.android1.notes;

import android.content.Context;
import android.content.SharedPreferences;

import com.posse.android1.notes.note.Note;

public class PreferencesDataWorker {
    private static final String PREFERENCES_KEY = PreferencesDataWorker.class.getCanonicalName() + "_preferences";
    private static final String KEY_NAMES = PreferencesDataWorker.class.getCanonicalName() + "_names";
    private static final String KEY_NOTES = PreferencesDataWorker.class.getCanonicalName() + "_notes";
    private static final String KEY_DATES = PreferencesDataWorker.class.getCanonicalName() + "_dates";
    private static final String KEY_OPENED_EDITOR = PreferencesDataWorker.class.getCanonicalName() + "_openedEditor";
    private static final String KEY_LAST_INDEX = PreferencesDataWorker.class.getCanonicalName() + "_lastIndex";
    private static final String KEY_GRID_VIEW = PreferencesDataWorker.class.getCanonicalName() + "_gridView";
    private static final String KEY_COLOR = PreferencesDataWorker.class.getCanonicalName() + "_noteColor";
    private static final String KEY_HEADER_TEXT_SIZE = PreferencesDataWorker.class.getCanonicalName() + "_headerTextSize";
    private static final String KEY_NOTE_TEXT_SIZE = PreferencesDataWorker.class.getCanonicalName() + "_noteTextSize";
    private static final String KEY_DEFAULT_COLOR = PreferencesDataWorker.class.getCanonicalName() + "_cardDefaultColor";

    private final SharedPreferences mPrefs;
    private final Context mContext;
    private SharedPreferences.Editor mEditor;

    public PreferencesDataWorker(Context context) {
        mContext = context;
        mPrefs = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public void writeNote(Note note) {
        mEditor = mPrefs.edit();
        mEditor.putString(KEY_NAMES + "_" + note.getNoteIndex(), note.getName());
        mEditor.putString(KEY_NOTES + "_" + note.getNoteIndex(), note.getDescription());
        mEditor.putString(KEY_DATES + "_" + note.getNoteIndex(), note.getCreationDate());
        mEditor.putInt(KEY_COLOR + "_" + note.getNoteIndex(), note.getColor());
        mEditor.apply();
    }

    public Note readNote(int index) {
        return new Note(index,
                mPrefs.getString(KEY_NAMES + "_" + index, null),
                mPrefs.getString(KEY_NOTES + "_" + index, null),
                mPrefs.getString(KEY_DATES + "_" + index, null),
                mPrefs.getInt(KEY_COLOR + "_" + index, -1));
    }

    public void deleteNote(int index, int fullSize) {
        mEditor = mPrefs.edit();
        for (int i = index; i < fullSize; i++) {
            mEditor.putString(KEY_NAMES + "_" + i, mPrefs.getString(KEY_NAMES + "_" + (i + 1), null));
            mEditor.putString(KEY_NOTES + "_" + i, mPrefs.getString(KEY_NOTES + "_" + (i + 1), null));
            mEditor.putString(KEY_DATES + "_" + i, mPrefs.getString(KEY_DATES + "_" + (i + 1), null));
            mEditor.putInt(KEY_COLOR + "_" + i, mPrefs.getInt(KEY_COLOR + "_" + (i + 1), -1));
        }
        mEditor.remove(KEY_NAMES + "_" + fullSize);
        mEditor.remove(KEY_NOTES + "_" + fullSize);
        mEditor.remove(KEY_DATES + "_" + fullSize);
        mEditor.remove(KEY_COLOR + "_" + fullSize);
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
        return mPrefs.getBoolean(KEY_OPENED_EDITOR, false);
    }

    public int getLastIndex() {
        return mPrefs.getInt(KEY_LAST_INDEX, -1);
    }

    public void setEditorOpened(boolean isOpened, int lastIndex) {
        mEditor = mPrefs.edit();
        mEditor.putBoolean(KEY_OPENED_EDITOR, isOpened);
        mEditor.putInt(KEY_LAST_INDEX, lastIndex);
        mEditor.apply();
    }

    public boolean isGridView() {
        return mPrefs.getBoolean(KEY_GRID_VIEW, false);
    }

    public void setGridView(boolean isGridView) {
        mEditor = mPrefs.edit();
        mEditor.putBoolean(KEY_GRID_VIEW, isGridView);
        mEditor.apply();
    }

    public void setNoteColor(Note note) {
        mEditor = mPrefs.edit();
        mEditor.putInt(KEY_COLOR + "_" + note.getNoteIndex(), note.getColor());
        mEditor.apply();
    }

    public float getHeaderTextSize() {
        return mPrefs.getFloat(KEY_HEADER_TEXT_SIZE,
                mContext.getResources().getDimension(R.dimen.header_text_size)
                        / mContext.getResources().getDisplayMetrics().scaledDensity);
    }

    public void setHeaderTextSize(float textSize) {
        mEditor = mPrefs.edit();
        mEditor.putFloat(KEY_HEADER_TEXT_SIZE, textSize);
        mEditor.apply();
    }

    public float getNoteTextSize() {
        return mPrefs.getFloat(KEY_NOTE_TEXT_SIZE,
                mContext.getResources().getDimension(R.dimen.note_text_size)
                        / mContext.getResources().getDisplayMetrics().scaledDensity);
    }

    public void setNoteTextSize(float textSize) {
        mEditor = mPrefs.edit();
        mEditor.putFloat(KEY_NOTE_TEXT_SIZE, textSize);
        mEditor.apply();
    }

    public int getDefaultNoteColor() {
        return mPrefs.getInt(KEY_DEFAULT_COLOR, -1);
    }

    public void setDefaultNoteColor(int color) {
        mEditor = mPrefs.edit();
        mEditor.putInt(KEY_DEFAULT_COLOR, color);
        mEditor.apply();
    }
}
