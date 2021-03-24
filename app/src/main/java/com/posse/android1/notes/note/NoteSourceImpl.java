package com.posse.android1.notes.note;

import android.content.res.Resources;

import com.posse.android1.notes.R;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class NoteSourceImpl implements NoteSource {

    private final LinkedList<Note> mData = new LinkedList<>();

    public NoteSourceImpl(Resources resources) {
        String[] names = resources.getStringArray(R.array.notes_list);
        String[] notes = resources.getStringArray(R.array.notes);
        String[] creationDates = resources.getStringArray(R.array.dates);

        for (int i = 0; i < names.length; i++) {
            mData.add(new Note(i, names[i], notes[i], creationDates[i]));
        }
    }

    @Override
    public List<Note> getNote() {
        return Collections.unmodifiableList(mData);
    }

    @Override
    public Note getItemAt(int idx) {
        return mData.get(idx);
    }

    @Override
    public int getItemsCount() {
        return mData.size();
    }
}
