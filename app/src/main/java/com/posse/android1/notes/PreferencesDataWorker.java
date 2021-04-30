package com.posse.android1.notes;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesDataWorker {
    private static final String PREFERENCES_KEY = PreferencesDataWorker.class.getCanonicalName() + "_preferences";
    private static final String KEY_OPENED_EDITOR = PreferencesDataWorker.class.getCanonicalName() + "_openedEditor";
    private static final String KEY_LAST_INDEX = PreferencesDataWorker.class.getCanonicalName() + "_lastIndex";
    private static final String KEY_GRID_VIEW = PreferencesDataWorker.class.getCanonicalName() + "_gridView";
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
