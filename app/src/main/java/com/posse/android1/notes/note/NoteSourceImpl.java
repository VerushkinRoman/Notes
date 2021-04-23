package com.posse.android1.notes.note;

import android.content.Context;

import androidx.annotation.NonNull;

import com.posse.android1.notes.PreferencesDataWorker;

import java.util.LinkedList;

public class NoteSourceImpl implements NoteSource {

    private volatile static NoteSourceImpl sInstance;
    private final LinkedList<Note> mData = new LinkedList<>();

    public NoteSourceImpl(Context resources) {
        PreferencesDataWorker prefsData = new PreferencesDataWorker(resources);
        int notes = prefsData.getNotesQuantity();
        for (int i = 0; i < notes; i++) {
            mData.add(prefsData.readNote(i));
        }
    }

    public static NoteSourceImpl getInstance(Context resources) {
        NoteSourceImpl instance = sInstance;
        if (instance == null) {
            synchronized (NoteSourceImpl.class) {
                if (sInstance == null) {
                    instance = new NoteSourceImpl(resources);
                    sInstance = instance;
                }
            }
        }
        return instance;
    }

    @Override
    public Note getItemAt(int idx) {
        return mData.get(idx);
    }

    @Override
    public int getItemsCount() {
        return mData.size();
    }

    @Override
    public void add(@NonNull Note data) {
        mData.add(data);
    }

    @Override
    public void remove(int position) {
        mData.remove(position);
    }
}
