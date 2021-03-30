package com.posse.android1.notes.note;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public abstract class BaseNoteSource implements NoteSource{

    private final HashSet<NoteSourceListener> mListeners = new HashSet<>();
    protected final LinkedList<Note> mData = new LinkedList<>();

    @Override
    public void addNoteSourceListener(NoteSourceListener listener) {
        mListeners.add(listener);
    }

    @Override
    public void removeNoteSourceListener(NoteSourceListener listener) {
        mListeners.remove(listener);
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

    @Override
    public void add(@NonNull Note note) {
        int idx = mData.size() - 1;
        for (NoteSourceListener listener : mListeners) {
            listener.onItemAdded(idx);
        }
    }

    @Override
    public void update(@NonNull Note note) {
        String id = note.getId();
        if (id != null) {
            int idx = 0;
            for (Note noteData : mData) {
                if (id.equals(noteData.getId())) {
                    noteData.setName(note.getName());
                    noteData.setDescription(note.getDescription());
                    noteData.setCreationDate(note.getCreationDate());
                    notifyUpdated(idx);
                    return;
                }
                idx++;
            }
        }
        add(note);
    }

    protected final void notifyUpdated(int idx) {
        for (NoteSourceListener listener : mListeners) {
            listener.onItemUpdated(idx);
        }
    }

    protected final void notifyDataSetChanged() {
        for (NoteSourceListener listener : mListeners) {
            listener.onDataSetChanged();
        }
    }


    @Override
    public void remove(int position) {
        mData.remove(position);
        for (NoteSourceListener listener : mListeners) {
            listener.onItemRemoved(position);
        }
    }
}
